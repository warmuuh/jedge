package com.github.warmuuh.jedge;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;

@Builder(access = AccessLevel.PRIVATE)
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Dsn {

  public static final String EDGEDB_PROTOCOL = "edgedb";
  @Default
  String host = "localhost";

  @Default
  int port = 5656;

  @Default
  String username = EDGEDB_PROTOCOL;

  @Default
  String password = null;


  @Default
  String tlsCertFile = null;
  @Default
  String tlsSecurity = null;


  @Default
  String database = EDGEDB_PROTOCOL;

  public static Dsn fromString(String dsnSpec) {
    URI uri = URI.create(dsnSpec);
    if (!EDGEDB_PROTOCOL.equals(uri.getScheme())) {
      throw new IllegalArgumentException("Unknown schema: " + uri.getScheme());
    }

    DsnBuilder builder = Dsn.builder();

    Optional.ofNullable(uri.getUserInfo())
        .map(up -> up.split(":"))
        .ifPresent(up -> builder.username(up[0]).password(up[1]));

    Optional.ofNullable(uri.getHost()).ifPresent(builder::host);
    Optional.ofNullable(uri.getPort()).ifPresent(builder::port);
    Optional.ofNullable(uri.getPath())
        .map(path -> path.replaceAll("/", ""))
        .ifPresent(builder::database);

    Map<String, String> paramMap = splitQuery(uri);
    Optional.ofNullable(paramMap.get("tls_cert_file")).ifPresent(builder::tlsCertFile);
    Optional.ofNullable(paramMap.get("tls_security")).ifPresent(builder::tlsCertFile);

    return builder.build();
  }

  @SneakyThrows
  public static Map<String, String> splitQuery(URI url) {
    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
    String query = url.getQuery();
    if (query != null && !query.isEmpty()) {
      String[] pairs = query.split("&");
      for (String pair : pairs) {
        int idx = pair.indexOf("=");
        query_pairs.put(
            URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
      }
    }
    return query_pairs;
  }
}
