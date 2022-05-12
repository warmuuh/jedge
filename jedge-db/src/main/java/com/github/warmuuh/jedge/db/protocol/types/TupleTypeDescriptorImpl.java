package com.github.warmuuh.jedge.db.protocol.types;

import com.github.warmuuh.jedge.db.protocol.reader.CompositeReader;
import lombok.experimental.Delegate;

public class TupleTypeDescriptorImpl extends TupleTypeDescriptor {

  @Delegate
  CompositeReader compositeReader = CompositeReader.INSTANCE;

}
