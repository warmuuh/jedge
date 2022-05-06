package com.github.warmuuh.jedge;

import com.github.warmuuh.jedge.db.protocol.PrepareImpl.IOFormat;
import com.github.warmuuh.jedge.db.protocol.types.ScalarType;
import java.util.Optional;

public interface WireFormat<T> {


  IOFormat getIoFormat();

  boolean isTypeKnown(String typeId);

  T convertResult(byte[] result);

  WireFormat<String> JsonFormat = new WireFormat<String>() {

    @Override
    public IOFormat getIoFormat() {
      return IOFormat.JSON;
    }

    @Override
    public boolean isTypeKnown(String typeId) {
      Optional<ScalarType> t = ScalarType.fromTypeId(typeId);
      return t.stream().allMatch(type -> type == ScalarType.NONE || type == ScalarType.STR);
    }

    @Override
    public String convertResult(byte[] result) {
      return new String(result);
    }
  };


  WireFormat<byte[]> BinaryFormat = new WireFormat<byte[]>() {

    @Override
    public IOFormat getIoFormat() {
      return IOFormat.BINARY;
    }

    @Override
    public boolean isTypeKnown(String typeId) {
      return false;
    }

    @Override
    public byte[] convertResult(byte[] result) {
      return result;
    }
  };

}
