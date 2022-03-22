package com.github.warmuuh.jedge.db.protocol;

import lombok.experimental.Delegate;

public class DataImpl extends Data {

  @Delegate
  private final StringReader stringReader = new StringReader();

}
