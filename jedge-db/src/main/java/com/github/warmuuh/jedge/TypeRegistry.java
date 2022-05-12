package com.github.warmuuh.jedge;

import com.github.warmuuh.jedge.db.protocol.types.TypeDescriptor;
import java.util.List;

public interface TypeRegistry {
  boolean isTypeKnown(String typeId);

  void registerType(String inputTypeId, List<TypeDescriptor> inputTypeDescriptors);
}
