package com.github.warmuuh.jedge;

import com.github.warmuuh.jedge.db.protocol.PrepareImpl.IOFormat;

public interface WireFormat<T> {

  IOFormat getFormat();

  T convertResult(byte[] result);

  WireFormat<String> JsonFormat = new WireFormat<String>() {

    @Override
    public IOFormat getFormat() {
      return IOFormat.JSON;
    }

    @Override
    public String convertResult(byte[] result) {
      return new String(result);
    }
  };


  WireFormat<byte[]> BinaryFormat = new WireFormat<byte[]>() {

    @Override
    public IOFormat getFormat() {
      return IOFormat.BINARY;
    }

    @Override
    public byte[] convertResult(byte[] result) {
      return result;
    }
  };

}
