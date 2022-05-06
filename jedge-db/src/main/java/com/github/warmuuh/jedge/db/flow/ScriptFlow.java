package com.github.warmuuh.jedge.db.flow;

import com.github.warmuuh.jedge.db.protocol.CommandCompleteImpl;
import com.github.warmuuh.jedge.db.protocol.ExecuteScriptImpl;
import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScriptFlow implements Flow<String> {

  @Getter
  private final List<? extends ProtocolMessage> initialStep;

  @Getter
  private final List<FlowStep> steps;
  private String status;

  public ScriptFlow(String script) {
    initialStep = List.of(ExecuteScriptImpl.of(script));
    steps = List.of(FlowStep.one(CommandCompleteImpl.class, resp -> {
      status = resp.getStatus();
      log.info("Received result: {}", status);
      return List.<ProtocolMessage>of();
    }));
  }

  @Override
  public String getResult() {
    return status;
  }
}
