package com.github.warmuuh.jedge.db.protocol.types;

import com.github.warmuuh.jedge.db.protocol.reader.CompositeReader;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import lombok.experimental.Delegate;

public class ArrayTypeDescriptorImpl extends ArrayTypeDescriptor {

  @Delegate
  CompositeReader compositeReader = CompositeReader.INSTANCE;

  @Override
  public String getId() {
    return ((JBBPFieldString)id).getAsString();
  }
}
