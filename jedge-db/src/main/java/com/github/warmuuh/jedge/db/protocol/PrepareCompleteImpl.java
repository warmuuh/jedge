package com.github.warmuuh.jedge.db.protocol;

import com.github.warmuuh.jedge.db.protocol.PrepareImpl.Cardinality;
import com.github.warmuuh.jedge.db.protocol.reader.CompositeReader;
import com.github.warmuuh.jedge.db.protocol.reader.StringReader;
import com.github.warmuuh.jedge.db.protocol.reader.UuidReader;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import java.util.Map;
import lombok.experimental.Delegate;

public class PrepareCompleteImpl extends PrepareComplete {
  @Delegate
  private final CompositeReader reader = new CompositeReader(Map.of(
      "string", new StringReader(),
      "uuid", new UuidReader()
  ));

  public Cardinality getCardinality() {
    for (Cardinality c : Cardinality.values()) {
      if (c.id == cardinality) {
        return c;
      }
    }
    throw new IllegalArgumentException("Unknown Cardinality: " + cardinality);
  }

  public String getInputTypeId() {
    return ((JBBPFieldString)input_typedesc_id).getAsString();
  }

  public String getOutputTypeId() {
    return ((JBBPFieldString)output_typedesc_id).getAsString();
  }
}
