package com.github.warmuuh.jedge.db.protocol.types;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import java.io.IOException;

public interface TypeDescriptor {
  Object read(JBBPBitInputStream In) throws IOException;

  default String getId() {
    return "-";
  };
}
