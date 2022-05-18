![GitHub release (latest by date)](https://img.shields.io/github/v/release/warmuuh/jedge?label=latest%20release)

# Jedge
unofficial [Edgedb](https://www.edgedb.com/) database connector for Java 

(!) CAUTION: this library is work in progress and nowhere near production-ready (even simple parsers for basic types are missing right now)

## details
This package contains a database connector for [edgedb](https://www.edgedb.com/) in java. 

## Features

* connection to edgedb via TLS/SASL Authentification
* wireformats: json, binary
* parser support for binary protocol
* [Milkman](https://github.com/warmuuh/milkman) integration (see [readme](/jedge-milkman))

## Usage
if you want to use binary protocol and included parsers, you can do so like this:

```java
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

  @lombok.Data
  public static class ResultType {
    private UUID id;
    private String title;
    private Long year;
  }
}
```


## Installation

in maven, include it via jitpack:
```xml
<!-- dependency for plain db connection: -->
	<dependency>
      <groupId>com.github.warmuuh.jedge</groupId>
      <artifactId>jedge</artifactId>
      <version>Tag</version>
    </dependency>

<!-- or if you want to use binary protocol and mapping-support: -->
    <dependency>
        <groupId>com.github.warmuuh.jedge</groupId>
        <artifactId>jedge-orm</artifactId>
        <version>Tag</version>
    </dependency>


<!-- repository for jitpack -->
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

