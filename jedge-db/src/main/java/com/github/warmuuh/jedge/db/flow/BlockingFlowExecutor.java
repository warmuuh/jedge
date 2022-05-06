package com.github.warmuuh.jedge.db.flow;

import com.github.warmuuh.jedge.Connection;
import com.github.warmuuh.jedge.DatabaseProtocolException;
import com.github.warmuuh.jedge.db.protocol.LoggingMessageVisitor;
import com.github.warmuuh.jedge.db.protocol.MessageEnvelope;
import com.github.warmuuh.jedge.db.protocol.MessageEnvelopeSerde;
import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockingFlowExecutor {

  private final MessageEnvelopeSerde serde = new MessageEnvelopeSerde();

  public <T> T run(Flow<T> flow, Connection connection) throws IOException, DatabaseProtocolException {
    sendInitialMessage(flow, connection);
    log.info("polling");
    pollMessagesAndSendNextSteps(flow, connection);
    return flow.getResult();
  }

  private void pollMessagesAndSendNextSteps(Flow flow, Connection connection) throws DatabaseProtocolException, IOException {
    List<FlowStep> steps = flow.getSteps();
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

  private void sendInitialMessage(Flow flow, Connection connection) throws IOException {
    List<? extends ProtocolMessage> messages = flow.getInitialStep();
    sendMessages(connection, messages);
  }

  private void sendMessages(Connection connection, List<? extends ProtocolMessage> messages) throws IOException {
    log.info(">> {}", messages);
    ArrayList<MessageEnvelope> envelopes = new ArrayList<>(messages.size());
    for (ProtocolMessage message : messages) {
      envelopes.add(serde.serialize(message));
    }
    connection.writeMessage(envelopes.toArray(new MessageEnvelope[0]));
  }
}
