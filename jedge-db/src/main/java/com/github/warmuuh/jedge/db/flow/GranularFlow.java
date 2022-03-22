package com.github.warmuuh.jedge.db.flow;

import com.github.warmuuh.jedge.db.flow.MessageFlow.FlowStep;
import com.github.warmuuh.jedge.db.protocol.CommandCompleteImpl;
import com.github.warmuuh.jedge.db.protocol.DataImpl;
import com.github.warmuuh.jedge.db.protocol.ExecuteImpl;
import com.github.warmuuh.jedge.db.protocol.ExecuteScriptImpl;
import com.github.warmuuh.jedge.db.protocol.PrepareComplete;
import com.github.warmuuh.jedge.db.protocol.PrepareCompleteImpl;
import com.github.warmuuh.jedge.db.protocol.PrepareImpl;
import java.util.Optional;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GranularFlow {

  @Delegate
  private final MessageFlow flow;


  public GranularFlow(String script) {
    String commandId = ""; //has to be empty, not yet supported by edgedb otherwise
    flow = new MessageFlow(
        () -> PrepareImpl.of(script, commandId),
        FlowStep.step(PrepareCompleteImpl.class, resp -> {
          log.info("Received prepare complete. cardinality {}", resp.getCardinality());
          return Optional.of(ExecuteImpl.of(commandId, ""));
        }),
        FlowStep.step(DataImpl.class, resp -> {
          log.info("Received Data: {}", new String(resp.data[0].data));
          return Optional.empty();
        }),
        FlowStep.step(CommandCompleteImpl.class, resp -> {
          log.info("Command complete");
          return Optional.empty();
        }));
  }

}
