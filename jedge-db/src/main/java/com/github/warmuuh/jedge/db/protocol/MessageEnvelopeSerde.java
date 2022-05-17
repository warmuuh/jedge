package com.github.warmuuh.jedge.db.protocol;

import com.github.warmuuh.jedge.DatabaseProtocolException;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MessageEnvelopeSerde {

  static final Map<Character, Class<? extends ProtocolMessage>> responseRegistry;
  static final Map<Class<? extends ProtocolMessage>, Character> requestRegistry;
  static {
    requestRegistry = new HashMap<>();
    requestRegistry.put(ClientHandshakeImpl.class, 'V');
    requestRegistry.put(AuthenticationSASLInitialResponseImpl.class, 'p');
    requestRegistry.put(AuthenticationSASLResponseImpl.class, 'r');
    requestRegistry.put(ExecuteScriptImpl.class, 'Q');
    requestRegistry.put(PrepareImpl.class, 'P');
    requestRegistry.put(ExecuteImpl.class, 'E');
    requestRegistry.put(SyncMessage.class, 'S');
    requestRegistry.put(DescribeStatementImpl.class, 'D');


    responseRegistry = new HashMap<>();
    responseRegistry.put('v', ServerHandshakeImpl.class);
    responseRegistry.put('E', ErrorResponseImpl.class);
    responseRegistry.put('R', AuthenticationImpl.class);
    responseRegistry.put('C', CommandCompleteImpl.class);
    responseRegistry.put('1', PrepareCompleteImpl.class);
    responseRegistry.put('D', DataImpl.class);
    responseRegistry.put('Z', ReadyForCommandImpl.class);
    responseRegistry.put('K', ServerKeyData.class);
    responseRegistry.put('S', ParameterStatusImpl.class);
    responseRegistry.put('T', CommandDataDescriptionImpl.class);

  }


  public ProtocolMessage deserialize(MessageEnvelope envelope) throws IOException, DatabaseProtocolException {
    Class<? extends ProtocolMessage> msgType = responseRegistry.get(envelope.mtype);
    if (msgType == null) {
      throw new IllegalArgumentException("Unknown message type: " + envelope.mtype);
    }
    try {
      ProtocolMessage message = msgType.getDeclaredConstructor().newInstance();
      message.read(new JBBPBitInputStream(new ByteArrayInputStream(envelope.message)));
      return message;
    } catch (Exception e) {
      throw new DatabaseProtocolException("failed to instantiade protocol message", e);
    }
  }


  public MessageEnvelope serialize(ProtocolMessage message) throws IOException {

    char msgType = Optional.ofNullable(requestRegistry.get(message.getClass()))
        .orElseThrow(() -> new IllegalArgumentException("Unregistered message class: " + message.getClass()));

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    JBBPBitOutputStream out = new JBBPBitOutputStream(bout);
    message.write(out);

    MessageEnvelope envelope = new MessageEnvelope();
    envelope.mtype = msgType;
    envelope.message = bout.toByteArray();
    envelope.message_length = envelope.message.length + 4;

    return envelope;
  }


}
