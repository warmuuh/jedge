package com.github.warmuuh.jedge.db.protocol;

import com.igormaznitsa.jbbp.model.JBBPFieldString;
import lombok.experimental.Delegate;

public class ExecuteImpl extends Execute {
  @Delegate
  private final StringReader stringReader = new StringReader();

  public static ExecuteImpl of(String statementName, String arguments) {
    ExecuteImpl command = new ExecuteImpl();
    command.statement_name = new JBBPFieldString(null, statementName);
    command.arguments = new JBBPFieldString(null, arguments);
    return command;
  }

}
