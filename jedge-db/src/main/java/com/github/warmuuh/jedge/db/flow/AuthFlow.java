package com.github.warmuuh.jedge.db.flow;

import static com.ongres.scram.common.stringprep.StringPreparations.NO_PREPARATION;

import com.github.warmuuh.jedge.db.flow.AuthFlow.AuthFlowResult;
import com.github.warmuuh.jedge.db.protocol.AuthenticationImpl;
import com.github.warmuuh.jedge.db.protocol.AuthenticationImpl.AuthenticationStatus;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLInitialResponse;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLInitialResponseImpl;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLResponse;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLResponseImpl;
import com.github.warmuuh.jedge.db.protocol.ClientHandshakeImpl;
import com.github.warmuuh.jedge.db.protocol.ParameterStatusImpl;
import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import com.github.warmuuh.jedge.db.protocol.ReadyForCommandImpl;
import com.github.warmuuh.jedge.db.protocol.ServerKeyData;
import com.ongres.scram.client.ScramClient;
import com.ongres.scram.client.ScramClient.ChannelBinding;
import com.ongres.scram.client.ScramSession;
import com.ongres.scram.client.ScramSession.ClientFinalProcessor;
import com.ongres.scram.client.ScramSession.ServerFirstProcessor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthFlow implements Flow<AuthFlowResult> {

  @Getter
  private final List<? extends ProtocolMessage> initialStep;
  @Getter
  private final List<FlowStep> steps;

  private final ScramClient scramClient = ScramClient
      .channelBinding(ChannelBinding.NO)
      .stringPreparation(NO_PREPARATION)
      .selectMechanismBasedOnServerAdvertised("SCRAM-SHA-256")
      .setup();
  private ScramSession scramSession;

  private Map<String, String> parameters = new HashMap<>();
  private boolean connected = false;


  public AuthFlow(String database, String username, String password) {
    initialStep = List.of(ClientHandshakeImpl.of(username, database));
    steps = List.of(
        FlowStep.one(AuthenticationImpl.class, resp -> {
          if (resp.getStatus() != AuthenticationStatus.SASL_REQUIRED) {
            throw new IllegalStateException("expected SASL_REQUIRED");
          }
          return List.of(saslFirstMessage(username));
        }),
        FlowStep.one(AuthenticationImpl.class, resp -> {
          if (resp.getStatus() != AuthenticationStatus.SASL_CONTINUE) {
            throw new IllegalStateException("expected SASL_CONTINUE");
          }
          return resp.getContinuePayload().map(payload -> saslFinalMessage(payload.getData(), password))
              .stream().collect(Collectors.toList());
        }),
        FlowStep.one(AuthenticationImpl.class, resp -> {
          if (resp.getStatus() != AuthenticationStatus.SASL_FINAL) {
            throw new IllegalStateException("expected SASL_FINAL");
          }
          return List.<ProtocolMessage>of();
        }),
        FlowStep.one(AuthenticationImpl.class, resp -> {
          if (resp.getStatus() != AuthenticationStatus.OK) {
            throw new IllegalStateException("expected OK");
          }
          return List.<ProtocolMessage>of();
        }),
        FlowStep.one(ServerKeyData.class, resp -> {
          System.out.println("skd: " + new String(resp.data));
          return List.<ProtocolMessage>of();
        }),
        FlowStep.zeroOrMore(ParameterStatusImpl.class, param -> {
          parameters.put(param.getName(), param.getValue());
          return List.<ProtocolMessage>of();
        }),
        FlowStep.one(ReadyForCommandImpl.class, ready -> {
          connected = true;
          return List.<ProtocolMessage>of();
        })
    );
  }

  @Override
  public AuthFlowResult getResult() {
    return new AuthFlowResult(parameters, connected);
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

  @Value
  public static class AuthFlowResult {
    Map<String, String> parameters;
    boolean connected;
  }
}
