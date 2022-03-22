package com.github.warmuuh.jedge;

import com.github.warmuuh.jedge.db.protocol.MessageEnvelope;
import com.github.warmuuh.jedge.db.util.ByteBufferBackedInputStream;
import com.github.warmuuh.jedge.db.util.ByteBufferOutputStream;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPBitOutputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.io.IOUtils;

public class Connection implements Closeable {

  private final  SSLSocket socket;
  private final ByteBuffer buffer = ByteBuffer.allocate(1024 * 10);
  private final InputStream in;
  private final OutputStream out;


  public static Connection connect(InetSocketAddress address) throws IOException, GeneralSecurityException {
    SSLContext sslContext = trustAllSSLContext();
    SSLSocketFactory factory = sslContext.getSocketFactory();
    SSLSocket socket = (SSLSocket) factory.createSocket(address.getHostName(), address.getPort());

    SSLParameters sslp = socket.getSSLParameters();
    sslp.setApplicationProtocols(new String[]{"edgedb-binary"});
    socket.setSSLParameters(sslp);
    socket.startHandshake();
    return new Connection(socket);
  }

  private Connection(SSLSocket socket) throws IOException {
    this.socket = socket;
    this.in = socket.getInputStream();
    this.out = socket.getOutputStream();
  }



  public void writeMessage(MessageEnvelope messageEnvelope) throws IOException {
    buffer.clear();
    ByteBufferOutputStream out1 = new ByteBufferOutputStream(buffer, true);
    JBBPBitOutputStream envOut = new JBBPBitOutputStream(out1);
    messageEnvelope.write(envOut);
    byte[] toSend = Arrays.copyOfRange(buffer.array(), 0, messageEnvelope.message_length + 1);
    out.write(toSend);
  }

  public List<MessageEnvelope> readMessages() throws IOException {

    buffer.flip();
    buffer.clear();
    BufferedInputStream inputStream = new BufferedInputStream(in);
    JBBPBitInputStream reader = new JBBPBitInputStream(inputStream);
    while (reader.hasAvailableData()) {
      int b = reader.readByte();
      if (b == -1) {
        break;
      }
      buffer.put((byte)b);
      int messageLength = reader.readInt(JBBPByteOrder.BIG_ENDIAN);
      buffer.putInt(messageLength);
      buffer.put(reader.readByteArray(messageLength-4));
      break;
    }

    buffer.flip();

    JBBPBitInputStream in = new JBBPBitInputStream(new ByteBufferBackedInputStream(buffer));

    List<MessageEnvelope> messages = new LinkedList<>();
    while(in.hasAvailableData()) {
      MessageEnvelope serverMessage = new MessageEnvelope();
      serverMessage.read(in);
      messages.add(serverMessage);
    }


    return messages;
  }

  @Override
  public void close() throws IOException {
    socket.close();
  }

  private static SSLContext trustAllSSLContext() throws IOException, GeneralSecurityException {
    SSLContext sslContext = SSLContext.getInstance("TLS");
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      public void checkClientTrusted(X509Certificate[] certs, String authType) {
      }

      public void checkServerTrusted(X509Certificate[] certs, String authType) {
      }
    }};
    sslContext.init(null, trustAllCerts, null);
    System.out.println(
        "SSLContext :: Protocol :: " + sslContext.getProtocol() + " Provider :: " + sslContext.getProvider());
    return sslContext;
  }

}
