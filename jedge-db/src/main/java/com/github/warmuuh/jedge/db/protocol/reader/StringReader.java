package com.github.warmuuh.jedge.db.protocol.reader;

import com.igormaznitsa.jbbp.compiler.JBBPNamedFieldInfo;
import com.igormaznitsa.jbbp.compiler.tokenizer.JBBPFieldTypeParameterContainer;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOutputStream;
import com.igormaznitsa.jbbp.model.JBBPAbstractField;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import java.io.IOException;

public class StringReader  implements CustomTypeReaderWriter {

  public JBBPAbstractField readCustomFieldType(Object sourceStruct, JBBPBitInputStream inStream,
      JBBPFieldTypeParameterContainer typeParameterContainer, JBBPNamedFieldInfo nullableNamedFieldInfo, int extraValue,
      boolean readWholeStream, int arraySize) throws IOException {
    int strLen = inStream.readInt(typeParameterContainer.getByteOrder());
    byte[] bytes = inStream.readByteArray(strLen);
    return new JBBPFieldString(nullableNamedFieldInfo, new String(bytes));
  }

  public void writeCustomFieldType(Object sourceStruct, JBBPBitOutputStream outStream, JBBPAbstractField fieldValue,
      JBBPFieldTypeParameterContainer typeParameterContainer, JBBPNamedFieldInfo nullableNamedFieldInfo, int extraValue,
      boolean wholeArray, int arraySize) throws IOException {
    var strVal = (JBBPFieldString) fieldValue;
    outStream.writeInt(strVal.getAsString().length(), typeParameterContainer.getByteOrder());
    byte[] bytes = strVal.getAsString().getBytes();
    outStream.writeBytes(bytes, bytes.length, typeParameterContainer.getByteOrder());
  }
}
