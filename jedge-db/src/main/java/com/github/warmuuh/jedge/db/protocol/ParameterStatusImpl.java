package com.github.warmuuh.jedge.db.protocol;

import com.github.warmuuh.jedge.db.protocol.reader.StringReader;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import lombok.experimental.Delegate;

public class ParameterStatusImpl extends ParameterStatus {

  @Delegate
  private final StringReader stringReader = new StringReader();

  public String getName() {
    return ((JBBPFieldString)this.name).getAsString();
  }

  public String getValue() {
    return ((JBBPFieldString)this.value).getAsString();
  }

}
