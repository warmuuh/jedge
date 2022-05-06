package com.github.warmuuh.jedge;

public class JedgeTest {

  public static void main(String[] args) throws Exception {

    try(var jedge = new Jedge<>(WireFormat.JsonFormat)) {
      jedge.connect(Dsn.fromString("edgedb://edgedb:password@localhost:10701/edgedb"));
      for (String result : jedge.queryList("select Movie { title, year };")) {
        System.out.println("Received result: " + result);
      }
    }
  }
}
