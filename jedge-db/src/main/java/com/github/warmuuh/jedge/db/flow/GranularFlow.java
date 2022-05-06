package com.github.warmuuh.jedge.db.flow;

import com.github.warmuuh.jedge.db.flow.GranularFlow.GranularFlowResult;
import com.github.warmuuh.jedge.db.protocol.CommandCompleteImpl;
import com.github.warmuuh.jedge.db.protocol.CommandDataDescriptionImpl;
import com.github.warmuuh.jedge.db.protocol.DataImpl;
import com.github.warmuuh.jedge.db.protocol.DescribeStatementImpl;
import com.github.warmuuh.jedge.db.protocol.ExecuteImpl;
import com.github.warmuuh.jedge.db.protocol.PrepareCompleteImpl;
import com.github.warmuuh.jedge.db.protocol.PrepareImpl;
import com.github.warmuuh.jedge.db.protocol.PrepareImpl.Cardinality;
import com.github.warmuuh.jedge.db.protocol.PrepareImpl.IOFormat;
import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import com.github.warmuuh.jedge.db.protocol.SyncMessage;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GranularFlow implements Flow<GranularFlowResult> {

  @Getter
  private final List<? extends ProtocolMessage> initialStep;
  @Getter
  private final List<FlowStep> steps;

  List<byte[]> dataChunks = new LinkedList<>();
  boolean finished = false;

  public GranularFlow(String script, IOFormat ioFormat, Cardinality cardinality) {
    String commandName = ""; //has to be empty, not yet supported by edgedb otherwise
    initialStep = List.of(PrepareImpl.of(script, ioFormat, cardinality, commandName), SyncMessage.INSTANCE);
    steps = List.of(
        FlowStep.one(PrepareCompleteImpl.class, resp -> {
          log.info("Received prepare complete. cardinality {}", resp.getCardinality());
          return List.of(DescribeStatementImpl.of(commandName), SyncMessage.INSTANCE);
        }),
        FlowStep.one(CommandDataDescriptionImpl.class, resp -> {
          log.info("Received command data description. output type id: " + UUID.nameUUIDFromBytes(resp.output_typedesc));
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


  @Override
  public GranularFlowResult getResult() {
    return new GranularFlowResult(dataChunks, finished);
  }

  @Value
  public static class GranularFlowResult {
    List<byte[]> dataChunks;
    boolean finished;
  }

}
