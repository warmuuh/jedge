package com.github.warmuuh.jedge.db.protocol;

import lombok.experimental.Delegate;

public class ParameterStatusImpl extends ParameterStatus {

  @Delegate
  private final StringReader stringReader = new StringReader();

}
