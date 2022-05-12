package com.github.warmuuh.jedge.db.protocol.types;

import com.github.warmuuh.jedge.db.protocol.reader.CompositeReader;
import lombok.experimental.Delegate;

public class SetDescriptorImpl extends SetDescriptor {

  @Delegate
  CompositeReader compositeReader = CompositeReader.INSTANCE;

}
