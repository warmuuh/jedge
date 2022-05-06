package com.github.warmuuh.jedge.db.protocol.reader;

import com.igormaznitsa.jbbp.compiler.JBBPNamedFieldInfo;
import com.igormaznitsa.jbbp.compiler.tokenizer.JBBPFieldTypeParameterContainer;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOutputStream;
import com.igormaznitsa.jbbp.model.JBBPAbstractField;
import com.igormaznitsa.jbbp.model.JBBPFieldArrayString;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringArrayReader implements CustomTypeReaderWriter {

  StringReader stringReader = new StringReader();

  @Override
  public JBBPAbstractField readCustomFieldType(Object sourceStruct, JBBPBitInputStream inStream,
      JBBPFieldTypeParameterContainer typeParameterContainer, JBBPNamedFieldInfo nullableNamedFieldInfo, int extraValue,
      boolean readWholeStream, int arraySize) throws IOException {
    List<String> arrayContent = IntStream.of(arraySize)
        .mapToObj(idx -> {
          try {
            return (JBBPFieldString) stringReader.readCustomFieldType(sourceStruct, inStream, typeParameterContainer,
                nullableNamedFieldInfo, extraValue, readWholeStream, arraySize);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .map(JBBPFieldString::getAsString)
        .collect(Collectors.toList());
    return new JBBPFieldArrayString(nullableNamedFieldInfo, arrayContent.toArray(new String[]{}));
  }

  @Override
  public void writeCustomFieldType(Object sourceStruct, JBBPBitOutputStream outStream, JBBPAbstractField fieldValue,
      JBBPFieldTypeParameterContainer typeParameterContainer, JBBPNamedFieldInfo nullableNamedFieldInfo, int extraValue,
      boolean wholeArray, int arraySize) throws IOException {
    throw new UnsupportedOperationException();
  }
}
