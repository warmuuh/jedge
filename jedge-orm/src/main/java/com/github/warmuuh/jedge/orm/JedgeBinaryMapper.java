package com.github.warmuuh.jedge.orm;

import com.github.warmuuh.jedge.DeserializationException;
import com.github.warmuuh.jedge.TypeDeserializer;
import com.github.warmuuh.jedge.TypeRegistry;
import com.github.warmuuh.jedge.db.protocol.types.ObjectShapeDescriptorImpl;
import com.github.warmuuh.jedge.db.protocol.types.ObjectShapeDescriptorImpl.Element;
import com.github.warmuuh.jedge.db.protocol.types.ScalarType;
import com.github.warmuuh.jedge.db.protocol.types.TypeDescriptor;
import com.github.warmuuh.jedge.orm.PropertyTypeMapper.PropertyMapping;
import com.github.warmuuh.jedge.orm.TypeMapper.LongTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.StringTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.UuidTypeMapper;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class JedgeBinaryMapper {

  private final TypeRegistry typeRegistry;

  private final Map<TypeIdClassPair, TypeMapper> mapperCache = new HashMap<>();
  {
    //register standard typeMappers
    mapperCache.put(TypeIdClassPair.of(ScalarType.UUID.getTypeId(), UUID.class), new UuidTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.STR.getTypeId(), String.class), new StringTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.INT64.getTypeId(), Long.class), new LongTypeMapper());
  }

  public <T> TypeDeserializer<T> deserializerFor(Class<T> type) {
    return (typeId, binaryData) -> {
      TypeMapper mapper = getOrCreateDeserializer(typeId, type, binaryData);
      return (T) mapper.mapType(type, new JBBPBitInputStream(new ByteArrayInputStream(binaryData)));
    };
  }

  private TypeMapper getOrCreateDeserializer(String typeId, Class<?> type, byte[] binaryData) {
    TypeIdClassPair cacheKey = TypeIdClassPair.of(typeId, type);
    TypeMapper cachedMapper = mapperCache.get(cacheKey);
    //TODO: concurrent handling
    if (cachedMapper != null) {
      return cachedMapper;
    }

    TypeMapper newMapper = buildMapper(typeId, type);
    mapperCache.put(cacheKey, newMapper);
    return newMapper;
  }

  private TypeMapper buildMapper(String typeId, Class<?> type) throws DeserializationException {
    List<TypeDescriptor> descriptors = typeRegistry.getTypeDescriptors(typeId)
        .orElseThrow(() -> new DeserializationException("Type for id " + typeId + " not found"));

    TypeDescriptor rootTypeDescriptor = descriptors.stream().filter(t -> t.getId().equals(typeId)).findAny()
        .orElseThrow(() -> new DeserializationException("Type for id " + typeId + " not found in descriptor"));

    return buildMapperRecursive(rootTypeDescriptor, type, descriptors);
  }

  private TypeMapper buildMapperRecursive(TypeDescriptor typeDescriptor, Class<?> type,
      List<TypeDescriptor> descriptors) throws DeserializationException {

    TypeMapper cachedMapper = mapperCache.get(TypeIdClassPair.of(typeDescriptor.getId(), type));
    if (cachedMapper != null) {
      return cachedMapper;
    }

    if (typeDescriptor instanceof ObjectShapeDescriptorImpl) {
      return buildMapperForObject((ObjectShapeDescriptorImpl) typeDescriptor, type, descriptors);
    } else {
      throw new DeserializationException(
          "Unsupported type descriptor: " + typeDescriptor.getClass().getName()
          + " id: " + typeDescriptor.getId()
          + " mapped to type " + type);
    }
  }

  private TypeMapper buildMapperForObject(ObjectShapeDescriptorImpl typeDescriptor,
      Class<?> type,
      List<TypeDescriptor> descriptors) throws DeserializationException {

    List<PropertyMapping> mappings = new LinkedList<>();
    PropertyTypeMapper mapper = new PropertyTypeMapper(mappings);
    //we have to register the mapper already to avoid recursion
    mapperCache.put(TypeIdClassPair.of(typeDescriptor.getId(), type), mapper);

    typeDescriptor.getElements().map(e -> {
      MethodHandle setter = findSetter(type, e);
      Class<?> propertyType = setter.type().parameterType(1);
      TypeMapper propertyMapper = buildMapperRecursive(descriptors.get(e.getTypePos()), propertyType, descriptors);
      return new PropertyMapping(propertyType, setter, propertyMapper);
    }).forEach(mappings::add);

    return mapper;
  }

  private MethodHandle findSetter(Class<?> type, Element e) {
    try {
      String setterName = "set" + e.getName().substring(0, 1).toUpperCase() + e.getName().substring(1);
      MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
      Method setter = Arrays.stream(type.getDeclaredMethods())
          .filter(m -> m.getName().equals(setterName))
          .findAny()
          .orElseThrow(() -> new DeserializationException("no setter found for property " + e.getName()));
      return publicLookup.findVirtual(type, setterName, MethodType.methodType(void.class, setter.getParameterTypes()[0]));
    } catch (IllegalAccessException | NoSuchMethodException ex) {
     throw new DeserializationException(ex);
    }
  }


  @Value(staticConstructor = "of")
  static class TypeIdClassPair {

    String typeId;
    Class<?> type;
  }
}
