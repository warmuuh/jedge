package com.github.warmuuh.jedge;

import com.github.warmuuh.jedge.db.flow.AuthFlow;
import com.github.warmuuh.jedge.db.flow.AuthFlow.AuthFlowResult;
import com.github.warmuuh.jedge.db.flow.BlockingFlowExecutor;
import com.github.warmuuh.jedge.db.flow.GranularFlow;
import com.github.warmuuh.jedge.db.flow.GranularFlow.GranularFlowResult;
import com.github.warmuuh.jedge.db.protocol.PrepareImpl.Cardinality;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Jedge implements Closeable {

  private final WireFormat wireFormat;
  private final TypeRegistry typeRegistry;
  private Connection connection;
  private BlockingFlowExecutor flowExecutor = new BlockingFlowExecutor();

  public void connect(Dsn dsn) throws IOException, DatabaseProtocolException {
    close();
    connection = Connection.connect(new InetSocketAddress(dsn.getHost(), dsn.getPort()));
    AuthFlow flow = new AuthFlow(dsn.getDatabase(), dsn.getUsername(), dsn.getPassword());
    AuthFlowResult result = flowExecutor.run(flow, connection);
    if (!result.isConnected()) {
      throw new DatabaseProtocolException("Failed to connect to database");
    }
  }


  public <T> Optional<T> querySingle(String query, TypeDeserializer<T> deserializer) {
    GranularFlow flow = new GranularFlow(query, wireFormat, typeRegistry, Cardinality.AT_MOST_ONE);
    GranularFlowResult result = flow.getResult();
    return result.getDataChunks().stream()
        .findAny()
        .map(data -> deserializer.deserialize(result.getOutputType(), data));
  }

  public <T> List<T> queryList(String query, TypeDeserializer<T> deserializer) throws DatabaseProtocolException, IOException {
    GranularFlow flow = new GranularFlow(query, wireFormat, typeRegistry, Cardinality.MANY);
    GranularFlowResult result = flowExecutor.run(flow, connection);
    return result.getDataChunks().stream()
        .map(data -> deserializer.deserialize(result.getOutputType(), data))
        .collect(Collectors.toList());
  }


  @Override
  public void close() throws IOException {
    if (connection != null) {
      connection.close();
      connection = null;
    }
  }
}
