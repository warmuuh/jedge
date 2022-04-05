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
import lombok.Data;
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

  @Data
  public static class FlowStep<T extends ProtocolMessage> {
    private final Class<T> serverMessage;
    private final Function<T, List<? extends ProtocolMessage>> step;
    private final boolean optional;
    private final boolean multiple;
    private int executionCount = 0;

    public List<? extends ProtocolMessage> computeResponse(T serverMessage) {

      if (!multiple && executionCount > 0) {
        log.warn("unexpected repetition of messagetype {}", serverMessage.getClass());
      }

      List<? extends ProtocolMessage> result = step.apply(serverMessage);
      executionCount++;
      return result;
    }

    public static <T extends ProtocolMessage> FlowStep<T> optional(Class<T> serverMessage, Function<T, List<? extends ProtocolMessage>> step) {
      return new FlowStep<>(serverMessage, step, true, false);
    }

    public static <T extends ProtocolMessage> FlowStep<T> one(Class<T> serverMessage, Function<T, List<? extends ProtocolMessage>> step) {
      return new FlowStep<>(serverMessage, step, false, false);
    }

    public static <T extends ProtocolMessage> FlowStep<T> zeroOrMore(Class<T> serverMessage, Function<T, List<? extends ProtocolMessage>> step) {
      return new FlowStep<>(serverMessage, step, true, true);
    }

    public static <T extends ProtocolMessage> FlowStep<T> oneOrMore(Class<T> serverMessage, Function<T, List<? extends ProtocolMessage>> step) {
      return new FlowStep<>(serverMessage, step, false, true);
    }

    public boolean canBeSkipped() {
      return optional || executionCount > 0;
    }

    public boolean canHandle(ProtocolMessage protocolMessage) {
      return serverMessage.isInstance(protocolMessage);
    }

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

        // find the next step
        FlowStep step = null;
        for(int nextIdx = currentStep; nextIdx < steps.size(); ) {
          if (steps.get(nextIdx).canHandle(srvResponse)){
           step = steps.get(nextIdx);
           currentStep = nextIdx;
           break;
          } else {
            if (steps.get(nextIdx).canBeSkipped()) {
              ++nextIdx;
            } else {
              break;
            }
          }
        }

        if (step != null) {
          List<ProtocolMessage> response = step.computeResponse(srvResponse);
          if (response.isEmpty()){
            //nothing to do, lets continue in the flow
            continue;
          }
          sendMessages(connection, response);
        } else {
          log.warn("No defined step for msg: {}", srvResponse.getClass());
        }
      }
      if (!steps.get(currentStep).isMultiple()) {
        currentStep++;
      }
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
