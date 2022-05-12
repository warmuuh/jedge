package com.github.warmuuh.jedge.db.protocol.types;

import com.github.warmuuh.jedge.db.protocol.PrepareImpl.Cardinality;
import com.github.warmuuh.jedge.db.protocol.reader.CompositeReader;
import com.igormaznitsa.jbbp.model.JBBPFieldString;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.Value;
import lombok.experimental.Delegate;

public class ObjectShapeDescriptorImpl extends ObjectShapeDescriptor {

  @Delegate
  CompositeReader compositeReader = CompositeReader.INSTANCE;

  @Override
  public String getId() {
    return ((JBBPFieldString)id).getAsString();
  }

  public Stream<Element> getElements() {
    return Arrays.stream(elements).map(e -> new Element(Cardinality.getCardinality(e.cardinality),
        ((JBBPFieldString)e.name).getAsString(),
        e.flags,
        e.type_pos));
  }

  @Value
  public static class Element {
    Cardinality cardinality;
    String name;
    int flags;
    int typePos;
  }
}
