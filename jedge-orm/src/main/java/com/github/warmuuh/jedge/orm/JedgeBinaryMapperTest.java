package com.github.warmuuh.jedge.orm;

import com.github.warmuuh.jedge.Dsn;
import com.github.warmuuh.jedge.Jedge;
import com.github.warmuuh.jedge.SimpleTypeRegistry;
import com.github.warmuuh.jedge.StringTypeDeserializer;
import com.github.warmuuh.jedge.WireFormat;
import com.github.warmuuh.jedge.orm.TypeMapper.UuidTypeMapper;
import java.util.UUID;
import lombok.Data;

public class JedgeBinaryMapperTest {

  public static void main(String[] args) throws Exception {

    SimpleTypeRegistry typeRegistry = new SimpleTypeRegistry();
    JedgeBinaryMapper mapper = new JedgeBinaryMapper(typeRegistry);
    try(var jedge = new Jedge(WireFormat.BinaryFormat, typeRegistry)) {
      jedge.connect(Dsn.fromString("edgedb://edgedb:password@localhost:10701/edgedb"));
      for (ResultType result : jedge.queryList("select Movie { title, year };", mapper.deserializerFor(ResultType.class))) {
        System.out.println("Received result: " + result);
      }
    }
  }

  @Data
  public static class ResultType {
    private UUID id;
    private String title;
    private Long year;
  }
}
