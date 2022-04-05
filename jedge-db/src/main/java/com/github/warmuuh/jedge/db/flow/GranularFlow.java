package com.github.warmuuh.jedge.db.flow;

import com.github.warmuuh.jedge.db.flow.MessageFlow.FlowStep;
import com.github.warmuuh.jedge.db.protocol.CommandCompleteImpl;
import com.github.warmuuh.jedge.db.protocol.DataImpl;
import com.github.warmuuh.jedge.db.protocol.ExecuteImpl;
import com.github.warmuuh.jedge.db.protocol.ExecuteScriptImpl;
import com.github.warmuuh.jedge.db.protocol.PrepareComplete;
import com.github.warmuuh.jedge.db.protocol.PrepareCompleteImpl;
import com.github.warmuuh.jedge.db.protocol.PrepareImpl;
import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import com.github.warmuuh.jedge.db.protocol.SyncMessage;
import java.util.List;
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
        () -> List.of(PrepareImpl.of(script, commandId), SyncMessage.INSTANCE),
        FlowStep.one(PrepareCompleteImpl.class, resp -> {
          log.info("Received prepare complete. cardinality {}", resp.getCardinality());
          return List.of(ExecuteImpl.of(commandId, ""), SyncMessage.INSTANCE);
        }),
        FlowStep.oneOrMore(DataImpl.class, resp -> {
          log.info("Received Data: {}", resp.getDataAsString());
          return List.of();
        }),
        FlowStep.one(CommandCompleteImpl.class, resp -> {
          log.info("Command complete");
          return List.of();
        }));
  }

}
