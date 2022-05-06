package com.github.warmuuh.jedge.db.protocol;

import com.github.warmuuh.jedge.db.protocol.reader.StringReader;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import lombok.experimental.Delegate;

public class ErrorResponseImpl extends ErrorResponse{

  @Delegate
  private final StringReader stringReader = new StringReader();

  public String getMessage() {
    return ((JBBPFieldString)this.message).getAsString();
  }
}
