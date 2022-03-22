package com.github.warmuuh.jedge.db.protocol;

import lombok.experimental.Delegate;

public class ServerHandshakeImpl extends ServerHandshake {

  @Delegate
  private final StringReader stringReader = new StringReader();
}
