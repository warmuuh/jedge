package com.github.warmuuh.jedge.db.flow;

import com.github.warmuuh.jedge.DatabaseProtocolException;
import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class FlowStep<T extends ProtocolMessage> {

  private final Class<T> serverMessage;
  private final FlowStepLogic<T> step;
  private final boolean optional;
  private final boolean multiple;
  private int executionCount = 0;

  public List<? extends ProtocolMessage> computeResponse(T serverMessage) throws DatabaseProtocolException, IOException {

    if (!multiple && executionCount > 0) {
      log.warn("unexpected repetition of messagetype {}", serverMessage.getClass());
    }

    List<? extends ProtocolMessage> result = step.onMessage(serverMessage);
    executionCount++;
    return result;
  }

  public static <T extends ProtocolMessage> FlowStep<T> optional(Class<T> serverMessage,
      FlowStepLogic<T> step) {
    return new FlowStep<>(serverMessage, step, true, false);
  }

  public static <T extends ProtocolMessage> FlowStep<T> one(Class<T> serverMessage,
      FlowStepLogic<T> step) {
    return new FlowStep<>(serverMessage, step, false, false);
  }

  public static <T extends ProtocolMessage> FlowStep<T> zeroOrMore(Class<T> serverMessage,
      FlowStepLogic<T> step) {
    return new FlowStep<>(serverMessage, step, true, true);
  }

  public static <T extends ProtocolMessage> FlowStep<T> oneOrMore(Class<T> serverMessage,
      FlowStepLogic<T> step) {
    return new FlowStep<>(serverMessage, step, false, true);
  }

  public boolean canBeSkipped() {
    return optional || executionCount > 0;
  }

  public boolean canHandle(ProtocolMessage protocolMessage) {
    return serverMessage.isInstance(protocolMessage);
  }

  @FunctionalInterface
  public interface FlowStepLogic<T> {
    List<? extends ProtocolMessage> onMessage(T message) throws DatabaseProtocolException, IOException;
  }
  
  
}
