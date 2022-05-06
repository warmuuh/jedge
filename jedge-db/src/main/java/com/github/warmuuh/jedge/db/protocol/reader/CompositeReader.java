package com.github.warmuuh.jedge.db.protocol.reader;

import com.igormaznitsa.jbbp.compiler.JBBPNamedFieldInfo;
import com.igormaznitsa.jbbp.compiler.tokenizer.JBBPFieldTypeParameterContainer;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOutputStream;
import com.igormaznitsa.jbbp.model.JBBPAbstractField;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeReader implements CustomTypeReaderWriter{

  private final Map<String, CustomTypeReaderWriter> typeReaders;


  @Override
  public JBBPAbstractField readCustomFieldType(Object sourceStruct, JBBPBitInputStream inStream,
      JBBPFieldTypeParameterContainer typeParameterContainer, JBBPNamedFieldInfo nullableNamedFieldInfo, int extraValue,
      boolean readWholeStream, int arraySize) throws IOException {
    CustomTypeReaderWriter readerWriter = getReaderWriter(typeParameterContainer);

    return readerWriter.readCustomFieldType(sourceStruct, inStream, typeParameterContainer, nullableNamedFieldInfo, extraValue, readWholeStream, arraySize);
  }

  @Override
  public void writeCustomFieldType(Object sourceStruct, JBBPBitOutputStream outStream, JBBPAbstractField fieldValue,
      JBBPFieldTypeParameterContainer typeParameterContainer, JBBPNamedFieldInfo nullableNamedFieldInfo, int extraValue,
      boolean wholeArray, int arraySize) throws IOException {
    CustomTypeReaderWriter readerWriter = getReaderWriter(typeParameterContainer);
    readerWriter.writeCustomFieldType(sourceStruct, outStream, fieldValue, typeParameterContainer, nullableNamedFieldInfo, extraValue, wholeArray, arraySize);
  }

  private CustomTypeReaderWriter getReaderWriter(JBBPFieldTypeParameterContainer typeParameterContainer) {
    if (!typeReaders.containsKey(typeParameterContainer.getTypeName())) {
      throw new IllegalArgumentException("No typewrite/reader found for type " + typeParameterContainer.getTypeName());
    }
    CustomTypeReaderWriter readerWriter = typeReaders.get(typeParameterContainer.getTypeName());
    return readerWriter;
  }

}
