package com.github.warmuuh.jedge;

public interface TypeRegistry<T> {
  boolean isTypeKnown(String typeId);

  T convertResult(byte[] result);

}
