package com.github.warmuuh.jedge.db.protocol;

import com.igormaznitsa.jbbp.model.JBBPFieldString;
import lombok.experimental.Delegate;

public class AuthenticationContinueSaslPayloadImpl  extends AuthenticationContinueSaslPayload {
  @Delegate
  private final StringReader stringArrayReader = new StringReader();

  public String getData(){
    return ((JBBPFieldString)data).getAsString();
  }

}
