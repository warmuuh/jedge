package com.github.warmuuh.jedge.db.protocol;

import com.github.warmuuh.jedge.db.protocol.PrepareImpl.Cardinality;
import lombok.experimental.Delegate;

public class PrepareCompleteImpl extends PrepareComplete {
  @Delegate
  private final StringReader stringReader = new StringReader();

  public Cardinality getCardinality() {
    for (Cardinality c : Cardinality.values()) {
      if (c.id == cardinality) {
        return c;
      }
    }
    throw new IllegalArgumentException("Unknown Cardinality: " + cardinality);
  }
}
