package com.github.warmuuh.jedge.db.flow;

import com.github.warmuuh.jedge.Connection;
import com.github.warmuuh.jedge.db.protocol.AuthenticationImpl;
import com.github.warmuuh.jedge.db.protocol.LoggingMessageVisitor;
import com.github.warmuuh.jedge.db.protocol.MessageEnvelope;
import com.github.warmuuh.jedge.db.protocol.MessageEnvelopeSerde;
import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageFlow {

  public MessageFlow(
      Supplier<List<? extends ProtocolMessage>> initialStep,
      FlowStep... steps) {
    this.initialStep = initialStep;
    this.steps = Arrays.asList(steps);
  }

  @Value
  @RequiredArgsConstructor(staticName = "step")
  public static class FlowStep<T extends ProtocolMessage, R extends ProtocolMessage> {
    Class<T> serverMessage;
    Function<T, List<? extends ProtocolMessage>> step;
  }

  private final Supplier<List<? extends ProtocolMessage>> initialStep;
  private final List<FlowStep> steps;
  private final MessageEnvelopeSerde serde = new MessageEnvelopeSerde();

  public void run(Connection connection) throws Exception {
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
        List<ProtocolMessage> response = (List<ProtocolMessage>)step.getStep().apply(srvResponse);
        if (response.isEmpty()){
          //nothing to do, lets continue in the flow
          continue;
//          break outer;
        }
        sendMessages(connection, response);
//        continue outer;
      }
      currentStep++;
    }
  }

  private void sendInitialMessage(Connection connection) throws Exception {
    List<? extends ProtocolMessage> messages = initialStep.get();
    sendMessages(connection, messages);
  }

  private void sendMessages(Connection connection, List<? extends ProtocolMessage> messages) throws Exception {
    log.info(">> {}", messages);
    ArrayList<MessageEnvelope> envelopes = new ArrayList<>(messages.size());
    for (ProtocolMessage message : messages) {
      envelopes.add(serde.serialize(message));
    }
    connection.writeMessage(envelopes.toArray(new MessageEnvelope[0]));
  }
}
