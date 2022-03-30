package com.github.warmuuh.jedge.db.protocol;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOutputStream;
import java.io.IOException;

public abstract class EmptyMessage implements ProtocolMessage {
  @Override
  public Object write(JBBPBitOutputStream Out) throws IOException {
    return this;
  }

  @Override
  public Object read(JBBPBitInputStream In) throws IOException {
    return this;
  }
}
