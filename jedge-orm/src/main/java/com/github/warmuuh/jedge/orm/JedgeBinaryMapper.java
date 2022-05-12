package com.github.warmuuh.jedge.orm;

import com.github.warmuuh.jedge.DeserializationException;
import com.github.warmuuh.jedge.TypeDeserializer;
import com.github.warmuuh.jedge.TypeRegistry;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class JedgeBinaryMapper {

  private final TypeRegistry typeRegistry;

  private final Map<TypeIdClassPair, TypeMapper> mapperCache;

  public <T> TypeDeserializer<T> deserializerFor(Class<T> type) {
      return (typeId, binaryData) -> {
        try {
          TypeMapper mapper = getOrCreateDeserializer(typeId, type, binaryData);
          T t = type.getDeclaredConstructor().newInstance();
          mapper.mapType(t, new JBBPBitInputStream(new ByteArrayInputStream(binaryData)));
          return t;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
         throw new DeserializationException(e);
        }
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

  private TypeMapper buildMapper(String typeId, Class<?> type) {
    return null;
  }


  @Value(staticConstructor = "of")
  static class TypeIdClassPair {
    String typeId;
    Class<?> type;
  }
}
