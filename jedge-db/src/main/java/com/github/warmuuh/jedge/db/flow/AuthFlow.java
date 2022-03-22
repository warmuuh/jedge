package com.github.warmuuh.jedge.db.flow;

import static com.ongres.scram.common.stringprep.StringPreparations.NO_PREPARATION;

import com.github.warmuuh.jedge.db.flow.MessageFlow.FlowStep;
import com.github.warmuuh.jedge.db.protocol.AuthenticationImpl;
import com.github.warmuuh.jedge.db.protocol.AuthenticationImpl.AuthenticationStatus;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLInitialResponse;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLInitialResponseImpl;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLResponse;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLResponseImpl;
import com.github.warmuuh.jedge.db.protocol.ClientHandshakeImpl;
import com.ongres.scram.client.ScramClient;
import com.ongres.scram.client.ScramClient.ChannelBinding;
import com.ongres.scram.client.ScramSession;
import com.ongres.scram.client.ScramSession.ClientFinalProcessor;
import com.ongres.scram.client.ScramSession.ServerFirstProcessor;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

public class AuthFlow {

  @Delegate
  private final MessageFlow flow;
  private final ScramClient scramClient = ScramClient
      .channelBinding(ChannelBinding.NO)
      .stringPreparation(NO_PREPARATION)
      .selectMechanismBasedOnServerAdvertised("SCRAM-SHA-256")
      .setup();
  private ScramSession scramSession;

  public AuthFlow(String database, String username, String password) {
    flow = new MessageFlow(
        () -> ClientHandshakeImpl.of(username, database),
        FlowStep.step(AuthenticationImpl.class, resp -> {
          if (resp.getStatus() != AuthenticationStatus.SASL_REQUIRED) {
            throw new IllegalStateException("expected SASL_REQUIRED");
          }
          return Optional.of(saslFirstMessage(username));
        }),
        FlowStep.step(AuthenticationImpl.class, resp -> {
          if (resp.getStatus() != AuthenticationStatus.SASL_CONTINUE) {
            throw new IllegalStateException("expected SASL_CONTINUE");
          }
          return resp.getContinuePayload().map(payload -> saslFinalMessage(payload.getData(), password));
        }),
        FlowStep.step(AuthenticationImpl.class, resp -> {
          if (resp.getStatus() != AuthenticationStatus.SASL_FINAL) {
            throw new IllegalStateException("expected SASL_FINAL");
          }
          return Optional.empty();
        }),
        FlowStep.step(AuthenticationImpl.class, resp -> {
          if (resp.getStatus() != AuthenticationStatus.OK) {
            throw new IllegalStateException("expected OK");
          }
          return Optional.empty();
        }));
  }


  private AuthenticationSASLInitialResponse saslFirstMessage(String username) {
    scramSession = scramClient.scramSession(username);
    return AuthenticationSASLInitialResponseImpl.of("SCRAM-SHA-256", scramSession.clientFirstMessage());
  }

  @SneakyThrows
  private AuthenticationSASLResponse saslFinalMessage(String serverResponse, String password) {
    ServerFirstProcessor serverFirst = scramSession.receiveServerFirstMessage(serverResponse);
    ClientFinalProcessor finalProcessor = serverFirst.clientFinalProcessor(password);
    return AuthenticationSASLResponseImpl.of(finalProcessor.clientFinalMessage());
  }
}
