package com.github.warmuuh.jedge.db.protocol.types;

import com.github.warmuuh.jedge.db.protocol.reader.CompositeReader;
import lombok.experimental.Delegate;

public class TypeAnnotationNameDescriptorImpl extends TypeAnnotationNameDescriptor {

  @Delegate
  CompositeReader compositeReader = CompositeReader.INSTANCE;

}
