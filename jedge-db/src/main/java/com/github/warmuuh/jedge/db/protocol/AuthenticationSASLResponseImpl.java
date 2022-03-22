package com.github.warmuuh.jedge.db.protocol;

import com.igormaznitsa.jbbp.model.JBBPFieldString;
import lombok.experimental.Delegate;

public class AuthenticationSASLResponseImpl extends AuthenticationSASLResponse {
  @Delegate
  private final StringReader stringReader = new StringReader();

  public static AuthenticationSASLResponse of(String data) {
    AuthenticationSASLResponseImpl response = new AuthenticationSASLResponseImpl();
    response.data = new JBBPFieldString(null, data); //data.getBytes();
    return response;
  }
}
