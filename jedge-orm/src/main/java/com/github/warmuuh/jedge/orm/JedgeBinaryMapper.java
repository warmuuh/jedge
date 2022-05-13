package com.github.warmuuh.jedge.orm;

import com.github.warmuuh.jedge.DeserializationException;
import com.github.warmuuh.jedge.TypeDeserializer;
import com.github.warmuuh.jedge.TypeRegistry;
import com.github.warmuuh.jedge.db.protocol.types.ObjectShapeDescriptorImpl;
import com.github.warmuuh.jedge.db.protocol.types.ObjectShapeDescriptorImpl.Element;
import com.github.warmuuh.jedge.db.protocol.types.ScalarType;
import com.github.warmuuh.jedge.db.protocol.types.TypeDescriptor;
import com.github.warmuuh.jedge.orm.PropertyTypeMapper.PropertyMapping;
import com.github.warmuuh.jedge.orm.TypeMapper.BooleanTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.ByteTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.CharTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.DurationTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.Float32TypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.InstantTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.IntTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.JsonStringTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.LocalDateTimeTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.LocalDateTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.LocalTimeTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.LongTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.StringTypeMapper;
import com.github.warmuuh.jedge.orm.TypeMapper.UuidTypeMapper;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class JedgeBinaryMapper {

  private final TypeRegistry typeRegistry;

  private final Map<TypeIdClassPair, TypeMapper> mapperCache = new HashMap<>();
  {
    //register standard typeMappers
    mapperCache.put(TypeIdClassPair.of(ScalarType.BYTES.getTypeId(), byte[].class), new ByteTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.BOOL.getTypeId(), Boolean.class), new BooleanTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.FLOAT32.getTypeId(), Float.class), new Float32TypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.INT16.getTypeId(), Character.class), new CharTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.INT32.getTypeId(), Integer.class), new IntTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.INT64.getTypeId(), Long.class), new LongTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.UUID.getTypeId(), UUID.class), new UuidTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.STR.getTypeId(), String.class), new StringTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.JSON.getTypeId(), String.class), new JsonStringTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.DATETIME.getTypeId(), Instant.class), new InstantTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.LOCAL_DATE.getTypeId(), LocalDate.class), new LocalDateTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.LOCAL_DATETIME.getTypeId(), LocalDateTime.class), new LocalDateTimeTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.LOCAL_TIME.getTypeId(), LocalTime.class), new LocalTimeTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.DURATION.getTypeId(), Duration.class), new DurationTypeMapper());
    mapperCache.put(TypeIdClassPair.of(ScalarType.RELATIVE_DURATION.getTypeId(), Duration.class), new DurationTypeMapper());
    //TODO: create mappers for these
//    mapperCache.put(TypeIdClassPair.of(ScalarType.BIGINT.getTypeId(), Long.class), );
//    mapperCache.put(TypeIdClassPair.of(ScalarType.FLOAT64.getTypeId(), Long.class), );
//    mapperCache.put(TypeIdClassPair.of(ScalarType.DECIMAL.getTypeId(), Long.class), );

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
      TypeIdClassPair mapperForTypeId = findMapperKeyForTypeId(typeDescriptor.getId());
      if (mapperForTypeId != null) {
        throw new DeserializationException(
            "Type with id " + typeDescriptor.getId() + " cannot be mapped to type " + type + ". (Should be mapped to type " + mapperForTypeId.getType() + ")");
      }
      throw new DeserializationException(
          "Unsupported type descriptor: " + typeDescriptor.getClass().getName()
          + " id: " + typeDescriptor.getId()
          + " mapped to type " + type);
    }
  }

  private TypeIdClassPair findMapperKeyForTypeId(String typeId) {
    return mapperCache.keySet().stream().filter(k -> k.getTypeId().equals(typeId))
        .findAny().orElse(null);
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
      try {
        TypeMapper propertyMapper = buildMapperRecursive(descriptors.get(e.getTypePos()), propertyType, descriptors);
        return new PropertyMapping(propertyType, setter, propertyMapper);
      } catch (DeserializationException ex) {
        throw new DeserializationException("Cannot map property '" + e.getName() + "' of type " + type, ex);
      }
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
          .orElseThrow(() -> new DeserializationException("no setter found for property '" + e.getName() + "'"));
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
