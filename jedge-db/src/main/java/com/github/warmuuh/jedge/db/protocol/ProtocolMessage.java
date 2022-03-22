package com.github.warmuuh.jedge.db.protocol;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOutputStream;
import java.io.IOException;

public interface ProtocolMessage {

  Object write(JBBPBitOutputStream Out) throws IOException;
  Object read(JBBPBitInputStream In) throws IOException;

}
