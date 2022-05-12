package com.github.warmuuh.jedge.orm;

import com.github.warmuuh.jedge.DeserializationException;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class PropertyTypeMapper implements TypeMapper {

  private final List<PropertyMapping> subTypeMappers;

  @Override
  public Object mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
    try {
      Object t = type.getDeclaredConstructor().newInstance();
      int elementCount = reader.readInt(JBBPByteOrder.BIG_ENDIAN);


      for (PropertyMapping typeMapper : subTypeMappers) {

        int reserved = reader.readInt(JBBPByteOrder.BIG_ENDIAN);

        Object mappedValue = typeMapper.getPropertyTypeMapper().mapType(typeMapper.getPropertyType(), reader);
        typeMapper.getMethodHandle().invoke(t, mappedValue);
      }
      return t;
    } catch (Throwable e) {
      throw new DeserializationException(e);
    }
  }

  @Value
  public static class PropertyMapping {
    Class<?> propertyType;
    MethodHandle methodHandle;
    TypeMapper propertyTypeMapper;
  }
}
