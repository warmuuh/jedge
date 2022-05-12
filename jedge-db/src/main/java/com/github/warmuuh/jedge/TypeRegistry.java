package com.github.warmuuh.jedge;

import com.github.warmuuh.jedge.db.protocol.types.TypeDescriptor;
import java.util.List;
import java.util.Optional;

public interface TypeRegistry {
  boolean isTypeKnown(String typeId);

  void registerType(String inputTypeId, List<TypeDescriptor> inputTypeDescriptors);

  Optional<List<TypeDescriptor>> getTypeDescriptors(String typeId);
}
