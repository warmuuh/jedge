package com.github.warmuuh.jedge.db.flow;

import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import java.util.List;

public interface Flow<T> {

  List<? extends ProtocolMessage> getInitialStep();

  List<FlowStep> getSteps();

  T getResult();

}
