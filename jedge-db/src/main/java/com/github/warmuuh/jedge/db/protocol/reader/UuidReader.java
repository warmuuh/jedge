package com.github.warmuuh.jedge.db.protocol.reader;

import com.igormaznitsa.jbbp.compiler.JBBPNamedFieldInfo;
import com.igormaznitsa.jbbp.compiler.tokenizer.JBBPFieldTypeParameterContainer;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOutputStream;
import com.igormaznitsa.jbbp.model.JBBPAbstractField;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import java.io.IOException;
import java.util.UUID;

public class UuidReader implements CustomTypeReaderWriter {

  public JBBPAbstractField readCustomFieldType(Object sourceStruct, JBBPBitInputStream inStream,
      JBBPFieldTypeParameterContainer typeParameterContainer, JBBPNamedFieldInfo nullableNamedFieldInfo, int extraValue,
      boolean readWholeStream, int arraySize) throws IOException {
    long highByte = inStream.readLong(typeParameterContainer.getByteOrder());
    long lowByte = inStream.readLong(typeParameterContainer.getByteOrder());
    return new JBBPFieldString(nullableNamedFieldInfo, new UUID(highByte, lowByte).toString());
  }

  public void writeCustomFieldType(Object sourceStruct, JBBPBitOutputStream outStream, JBBPAbstractField fieldValue,
      JBBPFieldTypeParameterContainer typeParameterContainer, JBBPNamedFieldInfo nullableNamedFieldInfo, int extraValue,
      boolean wholeArray, int arraySize) throws IOException {
    var strVal = (JBBPFieldString) fieldValue;
    UUID uuid = UUID.fromString(strVal.getAsString());
    outStream.writeLong(uuid.getMostSignificantBits(), typeParameterContainer.getByteOrder());
    outStream.writeLong(uuid.getLeastSignificantBits(), typeParameterContainer.getByteOrder());
  }
}
