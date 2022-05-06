package com.github.warmuuh.jedge.db.protocol;

import com.github.warmuuh.jedge.db.protocol.reader.StringReader;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import lombok.experimental.Delegate;

public class AuthenticationSASLInitialResponseImpl extends AuthenticationSASLInitialResponse {
  @Delegate
  private final StringReader stringReader = new StringReader();

  public static AuthenticationSASLInitialResponse of(String method, String data) {
    AuthenticationSASLInitialResponseImpl response = new AuthenticationSASLInitialResponseImpl();
    response.method = new JBBPFieldString(null, method);
    response.data = new JBBPFieldString(null, data); //data.getBytes();
    return response;
  }
}
