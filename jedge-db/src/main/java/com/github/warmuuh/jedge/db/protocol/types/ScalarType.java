package com.github.warmuuh.jedge.db.protocol.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ScalarType {

  NONE("00000000-0000-0000-0000-000000000000"),
  UUID("00000000-0000-0000-0000-000000000100"),
  STR("00000000-0000-0000-0000-000000000101"),
  BYTES("00000000-0000-0000-0000-000000000102"),
  INT16("00000000-0000-0000-0000-000000000103"),
  INT32("00000000-0000-0000-0000-000000000104"),
  INT64("00000000-0000-0000-0000-000000000105"),
  FLOAT32("00000000-0000-0000-0000-000000000106"),
  FLOAT64("00000000-0000-0000-0000-000000000107"),
  DECIMAL("00000000-0000-0000-0000-000000000108"),
  BOOL("00000000-0000-0000-0000-000000000109"),
  DATETIME("00000000-0000-0000-0000-00000000010A"),
  DURATION("00000000-0000-0000-0000-00000000010E"),
  JSON("00000000-0000-0000-0000-00000000010F"),
  LOCAL_DATETIME("00000000-0000-0000-0000-00000000010B"),
  LOCAL_DATE("00000000-0000-0000-0000-00000000010C"),
  LOCAL_TIME("00000000-0000-0000-0000-00000000010D"),
  BIGINT("00000000-0000-0000-0000-000000000110"),
  RELATIVE_DURATION("00000000-0000-0000-0000-000000000111");

  @Getter
  private final String typeId;

  private static Map<String, ScalarType> idToScalarType;
  static {
    idToScalarType = new HashMap<>();
    for (ScalarType value : values()) {
      idToScalarType.put(value.typeId, value);
    }
  }

  public static Optional<ScalarType> fromTypeId(String typeId) {
    return Optional.ofNullable(idToScalarType.get(typeId));
  }

}
