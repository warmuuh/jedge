package com.github.warmuuh.jedge.db.protocol;


import com.github.warmuuh.jedge.db.protocol.reader.StringReader;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import lombok.experimental.Delegate;

public class CommandCompleteImpl extends CommandComplete {
  @Delegate
  private final StringReader stringReader = new StringReader();

  public String getStatus() {
    return ((JBBPFieldString)status).getAsString();
  }
}
