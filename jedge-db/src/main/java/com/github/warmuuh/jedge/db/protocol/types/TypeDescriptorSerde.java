package com.github.warmuuh.jedge.db.protocol.types;

import com.github.warmuuh.jedge.DatabaseProtocolException;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TypeDescriptorSerde {

  static final Map<Character, Class<? extends TypeDescriptor>> typeDescriptorIds;

  static {
    typeDescriptorIds = new HashMap<>();
    typeDescriptorIds.put((char) 0, SetDescriptorImpl.class);
    typeDescriptorIds.put((char) 1, ObjectShapeDescriptorImpl.class);
    typeDescriptorIds.put((char) 2, BaseScalarTypeDescriptorImpl.class);
    typeDescriptorIds.put((char) 3, ScalarTypeDescriptorImpl.class);
    typeDescriptorIds.put((char) 4, TupleTypeDescriptorImpl.class);
    typeDescriptorIds.put((char) 5, NamedTupleTypeDescriptorImpl.class);
    typeDescriptorIds.put((char) 6, ArrayTypeDescriptorImpl.class);
    typeDescriptorIds.put((char) 7, EnumerationTypeDescriptorImpl.class);
    typeDescriptorIds.put((char) 0xff, TypeAnnotationNameDescriptorImpl.class);
    // 0.80 - 0xff are annotation descriptors, for now, we just ignore them
  }

  public List<TypeDescriptor> readDescriptors(byte[] typedesc) throws IOException, DatabaseProtocolException {
    JBBPBitInputStream reader = new JBBPBitInputStream(new ByteArrayInputStream(typedesc));

    List<TypeDescriptor> descriptors = new LinkedList<>();
    while (reader.available() > 0) {
      TypeDescriptorPreface envelope = new TypeDescriptorPreface();
      envelope.read(reader);
      Optional<TypeDescriptor> parsedDescriptor = deserialize(envelope, reader);
      parsedDescriptor.ifPresent(descriptors::add);
    }
    return descriptors;
  }


  private Optional<TypeDescriptor> deserialize(TypeDescriptorPreface typeDescriptorPreface, JBBPBitInputStream reader)
      throws DatabaseProtocolException {
    // 0.80 - 0xff are annotation descriptors, for now, we just ignore them
    return Optional.ofNullable(typeDescriptorIds.get(typeDescriptorPreface.type))
        .map(descType -> {
          try {
            TypeDescriptor message = descType.getDeclaredConstructor().newInstance();
            message.read(reader);
            return message;
          } catch (Exception e) {
            throw new DatabaseProtocolException("failed to instantiade descriptor type", e);
          }
        });
  }
}
