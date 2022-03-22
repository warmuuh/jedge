package com.github.warmuuh.jedge.db.protocol;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.io.IOUtils;

public class MessageEnvelopeSerde {

  Map<Character, Class<? extends ProtocolMessage>> typeRegistry = Map.of(
      'V', ClientHandshakeImpl.class,
      'v', ServerHandshakeImpl.class,
      'E', ErrorResponseImpl.class,
      'R', AuthenticationImpl.class,
      'p', AuthenticationSASLInitialResponseImpl.class,
      'r', AuthenticationSASLResponseImpl.class
  );


  public ProtocolMessage deserialize(MessageEnvelope envelope) throws Exception {
    Class<? extends ProtocolMessage> msgType = typeRegistry.get(envelope.mtype);
    if (msgType == null) {
      System.out.println("Unknown message type: " + envelope.mtype);
      System.out.println(IOUtils.toString(new ByteArrayInputStream(envelope.message)));
      throw new IllegalArgumentException("Unknown message type: " + envelope.mtype);
    }
    ProtocolMessage message = msgType.getDeclaredConstructor().newInstance();
    message.read(new JBBPBitInputStream(new ByteArrayInputStream(envelope.message)));

    return message;
  }


  public MessageEnvelope serialize(ProtocolMessage message) throws Exception {

    char msgType = typeRegistry.entrySet().stream()
        .filter(e -> e.getValue().equals(message.getClass()))
        .findAny()
        .map(Entry::getKey)
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
