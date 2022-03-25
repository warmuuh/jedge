package com.github.warmuuh.jedge.db.protocol;

public class DataImpl extends Data {

  public String getDataAsString() {
    return new String(data[0].data);
  }

}
