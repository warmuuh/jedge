package com.github.warmuuh.jedge;


import static com.ongres.scram.common.stringprep.StringPreparations.NO_PREPARATION;

import com.github.warmuuh.jedge.db.flow.AuthFlow;
import com.github.warmuuh.jedge.db.flow.GranularFlow;
import com.github.warmuuh.jedge.db.flow.ScriptFlow;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLInitialResponse;
import com.github.warmuuh.jedge.db.protocol.AuthenticationSASLInitialResponseImpl;
import com.github.warmuuh.jedge.db.protocol.ClientHandshakeImpl;
import com.github.warmuuh.jedge.db.protocol.LoggingMessageVisitor;
import com.github.warmuuh.jedge.db.protocol.MessageEnvelope;
import com.github.warmuuh.jedge.db.protocol.MessageEnvelopeSerde;
import com.github.warmuuh.jedge.db.protocol.ProtocolMessage;
import com.ongres.scram.client.ScramClient;
import com.ongres.scram.client.ScramClient.ChannelBinding;
import com.ongres.scram.client.ScramSession;
import java.net.InetSocketAddress;
import java.util.List;

public class ConnectionTest {

  public static void main(String[] args) throws Exception {


    try (Connection connection = Connection.connect(new InetSocketAddress("localhost", 10701))) {
      new AuthFlow("edgedb", "edgedb", "password").run(connection);
      new GranularFlow("select Movie;").run(connection);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void readAllMessages(MessageEnvelopeSerde serde, Connection connection) throws Exception {
    List<MessageEnvelope> serverMessages = connection.readMessages();
    for (MessageEnvelope serverMessage : serverMessages) {
      ProtocolMessage message = serde.deserialize(serverMessage);
      new LoggingMessageVisitor().visit(message);
    }
  }

}
