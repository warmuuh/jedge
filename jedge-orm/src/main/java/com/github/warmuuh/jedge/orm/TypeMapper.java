package com.github.warmuuh.jedge.orm;

import com.github.warmuuh.jedge.DeserializationException;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public interface TypeMapper {

  Object mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException;


  class UuidTypeMapper implements TypeMapper {

    @Override
    public UUID mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {

        int length = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (length <= 0) {
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
        if (length < 0) {
          return null;
        }

        return Long.valueOf(reader.readLong(JBBPByteOrder.BIG_ENDIAN));
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }

  class CharTypeMapper implements TypeMapper {

    @Override
    public Character mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {
        int len = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (len < 0) {
          return null;
        }

        return (char) reader.readUnsignedShort(JBBPByteOrder.BIG_ENDIAN);
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }

  class IntTypeMapper implements TypeMapper {

    @Override
    public Integer mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {
        int len = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (len < 0) {
          return null;
        }

        return reader.readInt(JBBPByteOrder.BIG_ENDIAN);
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }

  class Float32TypeMapper implements TypeMapper {

    @Override
    public Float mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {
        int len = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (len < 0) {
          return null;
        }

        return reader.readFloat(JBBPByteOrder.BIG_ENDIAN);
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }

  class BooleanTypeMapper implements TypeMapper {

    public static final int TRUE_VALUE = 0x01;

    @Override
    public Boolean mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {
        int len = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (len < 0) {
          return null;
        }

        return reader.readByte() == TRUE_VALUE;
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }

  class InstantTypeMapper implements TypeMapper {

    // edgedb timestamp is microseconds since January 1st 2000, 00:00 UTC
    // this offset is the microseconds between 1970-01-01T00:00:00Z and this date
    public static final long UNIX_TO_EDGEDB_TIME_OFFSET = 946684800L;

    @Override
    public Instant mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {
        int len = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (len < 0) {
          return null;
        }

        long epochMilli = (reader.readLong(JBBPByteOrder.BIG_ENDIAN) - UNIX_TO_EDGEDB_TIME_OFFSET) / 1000L;
        return Instant.ofEpochMilli(epochMilli);
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }


  class LocalDateTimeTypeMapper implements TypeMapper {

    @Override
    public LocalDateTime mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {
        int len = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (len < 0) {
          return null;
        }

        long microSinceEdgedbEpoch = (reader.readLong(JBBPByteOrder.BIG_ENDIAN));
        return LocalDateTime.of(2000, 1, 1, 0, 0).plus(microSinceEdgedbEpoch, ChronoUnit.MICROS);
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }

  class LocalDateTypeMapper implements TypeMapper {

    @Override
    public LocalDate mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {
        int len = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (len < 0) {
          return null;
        }

        long days = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        return LocalDate.of(2000, 1, 1).plusDays(days);
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }


  class LocalTimeTypeMapper implements TypeMapper {

    @Override
    public LocalTime mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {
        int len = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (len < 0) {
          return null;
        }

        long microSecondsSinceMidnight = reader.readLong(JBBPByteOrder.BIG_ENDIAN);
        return LocalTime.of(0, 0).plus(microSecondsSinceMidnight, ChronoUnit.MICROS);
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }

  class DurationTypeMapper implements TypeMapper {

    @Override
    public Duration mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {
        int len = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (len < 0) {
          return null;
        }

        long microSeconds = reader.readLong(JBBPByteOrder.BIG_ENDIAN);
        //days & months are deprecated for duration but not for relative-duration
        long days = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        long months = reader.readInt(JBBPByteOrder.BIG_ENDIAN);

        return Duration.of(microSeconds, ChronoUnit.MICROS)
            .plus(days, ChronoUnit.DAYS)
            .plus(months, ChronoUnit.MONTHS);
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }

  class JsonStringTypeMapper implements TypeMapper {

    @Override
    public String mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {
        int len = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (len < 0) {
          return null;
        }

        //format is always 1
        long format = reader.readByte();

        return new String(reader.readByteArray(len-1));
      } catch (IOException e) {
        throw new DeserializationException(e);
      }
    }
  }

  class ByteTypeMapper implements TypeMapper {

    @Override
    public byte[] mapType(Class<?> type, JBBPBitInputStream reader) throws DeserializationException {
      try {
        int len = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
        if (len < 0) {
          return null;
        }

        byte[] bytes = reader.readByteArray(len);
        return bytes;
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
        if (strLen < 0) {
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
