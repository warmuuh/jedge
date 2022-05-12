package com.github.warmuuh.jedge.orm;

import com.github.warmuuh.jedge.DeserializationException;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;
import java.io.IOException;
import java.util.UUID;

public interface TypeMapper {
  Object mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException;


  class UuidTypeMapper implements TypeMapper {

    @Override
    public UUID mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {

        int length = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (length <= 0 ) {
          return null;
        }
        long highByte = reader.readLong(JBBPByteOrder.BIG_ENDIAN);
        long lowByte = reader.readLong(JBBPByteOrder.BIG_ENDIAN);
        return new UUID(highByte, lowByte);
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }

  class LongTypeMapper implements TypeMapper {

    @Override
    public Long mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {

        int length = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (length <= 0 ) {
          return null;
        }

        return Long.valueOf(reader.readLong(JBBPByteOrder.BIG_ENDIAN));
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }

  class StringTypeMapper implements TypeMapper {

    @Override
    public String mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {
        int strLen = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (strLen < 0 ) {
          return null;
        }

        byte[] bytes = reader.readByteArray(strLen);
        return new String(bytes);
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }
}
