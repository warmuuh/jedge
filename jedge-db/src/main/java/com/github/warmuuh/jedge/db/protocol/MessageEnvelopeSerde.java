package com.github.warmuuh.jedge.db.protocol;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.io.IOUtils;

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

  }


  public ProtocolMessage deserialize(MessageEnvelope envelope) throws Exception {
    Class<? extends ProtocolMessage> msgType = responseRegistry.get(envelope.mtype);
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
