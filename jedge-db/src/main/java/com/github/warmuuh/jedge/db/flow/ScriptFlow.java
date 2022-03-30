package com.github.warmuuh.jedge.db.flow;

import com.github.warmuuh.jedge.db.flow.MessageFlow.FlowStep;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLInitialResponse;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLInitialResponseImpl;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLResponse;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLResponseImpl;
import com.github.warmuuh.jedge.db.protocol.CommandCompleteImpl;
import com.github.warmuuh.jedge.db.protocol.ExecuteScriptImpl;
import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import com.ongres.scram.client.ScramSession.ClientFinalProcessor;
import com.ongres.scram.client.ScramSession.ServerFirstProcessor;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScriptFlow {

  @Delegate
  private final MessageFlow flow;


  public ScriptFlow(String script) {
    flow = new MessageFlow(
        () -> List.of(ExecuteScriptImpl.of(script)),
        FlowStep.step(CommandCompleteImpl.class, resp -> {
          log.info("Received result: {}", resp.getStatus());
          return List.<ProtocolMessage>of();
        }));
  }

}
