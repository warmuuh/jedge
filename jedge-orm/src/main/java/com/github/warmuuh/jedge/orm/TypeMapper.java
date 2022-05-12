package com.github.warmuuh.jedge.orm;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;

public interface TypeMapper {
  void mapType(Object instance, JBBPBitInputStream reader) throws MappingException;
}
