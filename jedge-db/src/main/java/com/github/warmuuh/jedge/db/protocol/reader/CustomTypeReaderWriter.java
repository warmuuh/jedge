package com.github.warmuuh.jedge.db.protocol.reader;

import com.igormaznitsa.jbbp.compiler.JBBPNamedFieldInfo;
import com.igormaznitsa.jbbp.compiler.tokenizer.JBBPFieldTypeParameterContainer;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOutputStream;
import com.igormaznitsa.jbbp.model.JBBPAbstractField;
import java.io.IOException;

public interface CustomTypeReaderWriter {

  JBBPAbstractField readCustomFieldType(Object sourceStruct, JBBPBitInputStream inStream,
      JBBPFieldTypeParameterContainer typeParameterContainer, JBBPNamedFieldInfo nullableNamedFieldInfo, int extraValue,
      boolean readWholeStream, int arraySize) throws IOException;

  void writeCustomFieldType(Object sourceStruct, JBBPBitOutputStream outStream, JBBPAbstractField fieldValue,
      JBBPFieldTypeParameterContainer typeParameterContainer, JBBPNamedFieldInfo nullableNamedFieldInfo, int extraValue,
      boolean wholeArray, int arraySize) throws IOException;
}
