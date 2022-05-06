package com.github.warmuuh.jedge.db.protocol;

import com.github.warmuuh.jedge.db.protocol.reader.StringReader;
import lombok.experimental.Delegate;

public class ServerHandshakeImpl extends ServerHandshake {

  @Delegate
  private final StringReader stringReader = new StringReader();
}
