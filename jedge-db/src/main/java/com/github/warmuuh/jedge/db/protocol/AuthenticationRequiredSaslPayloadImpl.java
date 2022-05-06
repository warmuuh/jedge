package com.github.warmuuh.jedge.db.protocol;

import com.github.warmuuh.jedge.db.protocol.reader.StringArrayReader;
import com.igormaznitsa.jbbp.model.JBBPFieldArrayString;
import java.util.Arrays;
import java.util.List;
import lombok.experimental.Delegate;

public class AuthenticationRequiredSaslPayloadImpl extends AuthenticationRequiredSaslPayload {
  @Delegate
  private final StringArrayReader stringArrayReader = new StringArrayReader();

  public List<String> getMethods(){
    return Arrays.asList(((JBBPFieldArrayString) methods).getArray());
  }

}
