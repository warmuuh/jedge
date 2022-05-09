package com.github.warmuuh.jedge.db.protocol.types;

import com.github.warmuuh.jedge.DatabaseProtocolException;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TypeDescriptorSerde {

  static final Map<Character, Class<? extends TypeDescriptor>> typeDescriptorIds;
  static {
    typeDescriptorIds = new HashMap<>();
    typeDescriptorIds.put((char)0, SetDescriptor.class);
    typeDescriptorIds.put((char)1, ObjectShapeDescriptor.class);
    typeDescriptorIds.put((char)2, BaseScalarTypeDescriptor.class);
    typeDescriptorIds.put((char)3, ScalarTypeDescriptor.class);
    typeDescriptorIds.put((char)4, TupleTypeDescriptor.class);
    typeDescriptorIds.put((char)5, NamedTupleTypeDescriptor.class);
    typeDescriptorIds.put((char)6, ArrayTypeDescriptor.class);
    typeDescriptorIds.put((char)7, EnumerationTypeDescriptor.class);
    typeDescriptorIds.put((char)0xff, TypeAnnotationNameDescriptor.class);
    // 0.80 - 0xff are annotation descriptors, for now, we just ignore them
  }


  public TypeDescriptor deserialize(DescriptorEnvelope envelope) throws IOException, DatabaseProtocolException {
    Class<? extends TypeDescriptor> descType = typeDescriptorIds.get(envelope.type);
    if (descType == null) {
      // 0.80 - 0xff are annotation descriptors, for now, we just ignore them
      System.out.println("Unknown descriptor type: " + envelope.type);
    }
    try {
      TypeDescriptor message = descType.getDeclaredConstructor().newInstance();
      message.read(new JBBPBitInputStream(new ByteArrayInputStream(envelope.descriptor)));
      return message;
    } catch (Exception e) {
      throw new DatabaseProtocolException("failed to instantiade descriptor type", e);
    }
  }
}
