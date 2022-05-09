package com.github.warmuuh.jedge;

import com.github.warmuuh.jedge.db.protocol.PrepareImpl.IOFormat;
import com.github.warmuuh.jedge.db.protocol.types.ScalarType;
import java.util.Optional;

public interface WireFormat {


  IOFormat getIoFormat();

  WireFormat JsonFormat = new WireFormat() {

    @Override
    public IOFormat getIoFormat() {
      return IOFormat.JSON;
    }

  };


  WireFormat BinaryFormat = new WireFormat() {

    @Override
    public IOFormat getIoFormat() {
      return IOFormat.BINARY;
    }
  };

}
