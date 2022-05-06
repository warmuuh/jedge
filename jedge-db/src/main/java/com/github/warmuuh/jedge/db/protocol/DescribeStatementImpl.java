package com.github.warmuuh.jedge.db.protocol;

import com.github.warmuuh.jedge.db.protocol.reader.StringReader;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

public class DescribeStatementImpl extends DescribeStatement {

  @Delegate
  private final StringReader stringReader = new StringReader();


  @AllArgsConstructor
  public enum DescribeAspect {
    DATA_DESCRIPTION(0x54);
    int id;
  }

  public static DescribeStatementImpl of(String commandName) {
    DescribeStatementImpl command = new DescribeStatementImpl();
    command.aspect = (char)DescribeAspect.DATA_DESCRIPTION.id;
    command.statement_name = new JBBPFieldString(null, commandName);
    return command;
  }
}
