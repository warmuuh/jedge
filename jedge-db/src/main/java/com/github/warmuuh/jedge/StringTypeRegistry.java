package com.github.warmuuh.jedge;

import com.github.warmuuh.jedge.db.protocol.types.ScalarType;
import java.util.Optional;

public class StringTypeRegistry implements TypeRegistry {
  @Override
  public boolean isTypeKnown(String typeId) {
    Optional<ScalarType> t = ScalarType.fromTypeId(typeId);
    return t.stream().allMatch(type -> type == ScalarType.NONE || type == ScalarType.STR);
  }

  @Override
  public String convertResult(byte[] result) {
    return new String(result);
  }
}
