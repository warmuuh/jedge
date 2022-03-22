package com.github.warmuuh.jedge.db.flow;

import com.github.warmuuh.jedge.Connection;
import com.github.warmuuh.jedge.db.protocol.AuthenticationImpl;
import com.github.warmuuh.jedge.db.protocol.LoggingMessageVisitor;
import com.github.warmuuh.jedge.db.protocol.MessageEnvelope;
import com.github.warmuuh.jedge.db.protocol.MessageEnvelopeSerde;
import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageFlow {

  public MessageFlow(
      Supplier<? extends ProtocolMessage> initialStep,
      FlowStep... steps) {
    this.initialStep = initialStep;
    this.steps = Arrays.asList(steps);
  }

  @Value
  @RequiredArgsConstructor(staticName = "step")
  public static class FlowStep<T extends ProtocolMessage, R extends ProtocolMessage> {
    Class<T> serverMessage;
    Function<T, Optional<R>> step;
  }

  private final Supplier<? extends ProtocolMessage> initialStep;
  private final List<FlowStep> steps;
  private final MessageEnvelopeSerde serde = new MessageEnvelopeSerde();

  public void run(Connection connection) throws Exception {
    log.info("Sending initial");
    sendInitialMessage(connection);
    log.info("polling");
    pollMessagesAndSendNextSteps(connection);


  }

  private void pollMessagesAndSendNextSteps(Connection connection) throws Exception {
    int currentStep = 0;
    outer: while(currentStep < steps.size()){
      for (MessageEnvelope svrMsg : connection.readMessages()) {
        ProtocolMessage srvResponse = serde.deserialize(svrMsg);
        log.info("<< {}", srvResponse.getClass());
        new LoggingMessageVisitor().visit(srvResponse);
        FlowStep step = steps.get(currentStep);
        if (!step.getServerMessage().isInstance(srvResponse)){
          continue;
        }
        Optional<ProtocolMessage> response = (Optional<ProtocolMessage>)step.getStep().apply(srvResponse);
        if (response.isEmpty()){
          log.info("|| End of flow");
          continue;
//          break outer;
        }
        log.info(">> {}", response.get().getClass());
        MessageEnvelope msg = serde.serialize(response.get());
        connection.writeMessage(msg);
//        continue outer;
      }
      currentStep++;
    }
  }

  private void sendInitialMessage(Connection connection) throws Exception {
    ProtocolMessage firstMessage = initialStep.get();
    MessageEnvelope envelope = serde.serialize(firstMessage);
    connection.writeMessage(envelope);
  }
}
