package com.github.warmuuh.jedge.db.protocol;

import com.igormaznitsa.jbbp.model.JBBPFieldString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;

public class PrepareImpl extends Prepare {

  @Delegate
  private final StringReader stringReader = new StringReader();

  @AllArgsConstructor
  public enum IOFormat {
    BINARY(0x62),
    JSON(0x6a),
    JSON_ELEMENTS( 0x4a);
    int id;
  }

  @AllArgsConstructor
  public enum Cardinality {
    NO_RESULT(0x6e),
    AT_MOST_ONE(0x6f),
    ONE(0x41),
    MANY(0x6d),
    AT_LEAST_ONE(0x4d);
    int id;
  }

  public static PrepareImpl of(String script, String commandName) {
    PrepareImpl command = new PrepareImpl();
    command.io_format = (char)IOFormat.BINARY.id;
    command.expected_cardinality = (char)Cardinality.MANY.id;
    command.command = new JBBPFieldString(null, script);
    command.statement_name = new JBBPFieldString(null, commandName);
    return command;
  }

}
