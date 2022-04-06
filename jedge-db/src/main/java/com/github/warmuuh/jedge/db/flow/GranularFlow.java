package com.github.warmuuh.jedge.db.flow;

import com.github.warmuuh.jedge.db.flow.MessageFlow.FlowStep;
import com.github.warmuuh.jedge.db.protocol.CommandCompleteImpl;
import com.github.warmuuh.jedge.db.protocol.Data;
import com.github.warmuuh.jedge.db.protocol.DataImpl;
import com.github.warmuuh.jedge.db.protocol.ExecuteImpl;
import com.github.warmuuh.jedge.db.protocol.ExecuteScriptImpl;
import com.github.warmuuh.jedge.db.protocol.PrepareComplete;
import com.github.warmuuh.jedge.db.protocol.PrepareCompleteImpl;
import com.github.warmuuh.jedge.db.protocol.PrepareImpl;
import com.github.warmuuh.jedge.db.protocol.PrepareImpl.Cardinality;
import com.github.warmuuh.jedge.db.protocol.PrepareImpl.IOFormat;
import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import com.github.warmuuh.jedge.db.protocol.SyncMessage;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.Value;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GranularFlow {

  @Delegate
  private final MessageFlow flow;

  List<byte[]> dataChunks = new LinkedList<>();
  boolean finished = false;

  public GranularFlow(String script, IOFormat ioFormat, Cardinality cardinality) {
    String commandName = ""; //has to be empty, not yet supported by edgedb otherwise
    flow = new MessageFlow(
        () -> List.of(PrepareImpl.of(script, ioFormat, cardinality, commandName), SyncMessage.INSTANCE),
        FlowStep.one(PrepareCompleteImpl.class, resp -> {
          log.info("Received prepare complete. cardinality {}", resp.getCardinality());
          return List.of(ExecuteImpl.of(commandName, ""), SyncMessage.INSTANCE);
        }),
        FlowStep.oneOrMore(DataImpl.class, resp -> {
          log.info("Received Data: {}", resp.getDataAsString());
          dataChunks.add(resp.getData());
          return List.of();
        }),
        FlowStep.one(CommandCompleteImpl.class, resp -> {
          log.info("Command complete");
          finished = true;
          return List.of();
        }));
  }


  public GranularFlowResult getResult() {
    return new GranularFlowResult(dataChunks, finished);
  }

  @Value
  public static class GranularFlowResult {
    List<byte[]> dataChunks;
    boolean finished;
  }

}
