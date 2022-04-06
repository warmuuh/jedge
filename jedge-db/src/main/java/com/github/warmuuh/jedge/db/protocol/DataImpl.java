package com.github.warmuuh.jedge.db.protocol;

public class DataImpl extends Data {

  public String getDataAsString() {
    //currently, data.length is always 1
    return new String(data[0].data);
  }

  public byte[] getData() {
    //currently, data.length is always 1
    return data[0].data;
  }
}
