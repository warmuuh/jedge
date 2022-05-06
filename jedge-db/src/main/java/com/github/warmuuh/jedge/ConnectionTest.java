package com.github.warmuuh.jedge;


import static com.ongres.scram.common.stringprep.StringPreparations.NO_PREPARATION;

import com.github.warmuuh.jedge.db.flow.AuthFlow;
import com.github.warmuuh.jedge.db.flow.AuthFlow.AuthFlowResult;
import com.github.warmuuh.jedge.db.flow.BlockingFlowExecutor;
import com.github.warmuuh.jedge.db.flow.GranularFlow;
import com.github.warmuuh.jedge.db.flow.GranularFlow.GranularFlowResult;
import com.github.warmuuh.jedge.db.flow.ScriptFlow;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLInitialResponse;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLInitialResponseImpl;
import com.github.warmuuh.jedge.db.protocol.ClientHandshakeImpl;
import com.github.warmuuh.jedge.db.protocol.ExecuteImpl;
import com.github.warmuuh.jedge.db.protocol.LoggingMessageVisitor;
import com.github.warmuuh.jedge.db.protocol.MessageEnvelope;
import com.github.warmuuh.jedge.db.protocol.MessageEnvelopeSerde;
import com.github.warmuuh.jedge.db.protocol.PrepareImpl;
import com.github.warmuuh.jedge.db.protocol.PrepareImpl.Cardinality;
import com.github.warmuuh.jedge.db.protocol.PrepareImpl.IOFormat;
import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import com.ongres.scram.client.ScramClient;
import com.ongres.scram.client.ScramClient.ChannelBinding;
import com.ongres.scram.client.ScramSession;
import java.net.InetSocketAddress;
import java.util.List;

public class ConnectionTest {

  private static final MessageEnvelopeSerde serde = new MessageEnvelopeSerde();

  public static void main(String[] args) throws Exception {

    BlockingFlowExecutor executor = new BlockingFlowExecutor();

    try (Connection connection = Connection.connect(new InetSocketAddress("localhost", 10701))) {
      AuthFlow authFlow = new AuthFlow("edgedb", "edgedb", "password");
      AuthFlowResult result = executor.run(authFlow, connection);
      System.out.println("Authflow result: " + result);

      GranularFlow granularFlow = new GranularFlow("select Movie { title, year };", IOFormat.JSON, Cardinality.MANY);
      GranularFlowResult granularFlowResult = executor.run(granularFlow, connection);
      granularFlowResult.getDataChunks().forEach(chunk -> System.out.println(new String(chunk)));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void sendMessage(ProtocolMessage message, Connection connection) throws Exception {
    MessageEnvelope serializedMessage = serde.serialize(message);
    connection.writeMessage(serializedMessage);
  }

  private static void readAllMessages(Connection connection) throws Exception {
    List<MessageEnvelope> serverMessages = connection.readMessages();
    for (MessageEnvelope serverMessage : serverMessages) {
      ProtocolMessage message = serde.deserialize(serverMessage);
      new LoggingMessageVisitor().visit(message);
    }
  }

}
