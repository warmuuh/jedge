package com.github.warmuuh.jedge.db.protocol;

import lombok.experimental.Delegate;

public class CommandDataDescriptionImpl extends CommandDataDescription {
  @Delegate
  private final StringReader stringReader = new StringReader();


}
