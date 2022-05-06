package com.github.warmuuh.jedge.db.protocol;

import com.github.warmuuh.jedge.db.protocol.reader.StringReader;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import lombok.experimental.Delegate;

public class ExecuteScriptImpl extends ExecuteScript {

  @Delegate
  private final StringReader stringReader = new StringReader();

  public static ExecuteScriptImpl of(String script) {
    ExecuteScriptImpl command = new ExecuteScriptImpl();
    command.script = new JBBPFieldString(null, script);
    return command;
  }
}
