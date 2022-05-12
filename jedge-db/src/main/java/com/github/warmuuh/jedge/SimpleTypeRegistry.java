package com.github.warmuuh.jedge;

import com.github.warmuuh.jedge.db.protocol.types.ScalarType;
import com.github.warmuuh.jedge.db.protocol.types.TypeDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SimpleTypeRegistry implements TypeRegistry {
  Map<String, List<TypeDescriptor>> typeDescriptors = new HashMap<>();

  @Override
  public boolean isTypeKnown(String typeId) {
    Optional<ScalarType> t = ScalarType.fromTypeId(typeId);
    return typeDescriptors.containsKey(typeId) || ScalarType.fromTypeId(typeId).isPresent();
  }

  @Override
  public void registerType(String inputTypeId, List<TypeDescriptor> inputTypeDescriptors) {
    typeDescriptors.put(inputTypeId, inputTypeDescriptors);
  }
}
