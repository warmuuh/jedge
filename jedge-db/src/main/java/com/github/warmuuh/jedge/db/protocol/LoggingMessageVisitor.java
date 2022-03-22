package com.github.warmuuh.jedge.db.protocol;

public class LoggingMessageVisitor extends ServerMessageVisitor {

  @Override
  void visit(ServerHandshakeImpl message) {
      System.out.println("response:");
      System.out.println(message.major_ver);
  }

  @Override
  void visit(ErrorResponseImpl message) {
    System.out.println("error: " + message.getMessage());
    System.out.println(Integer.toHexString(message.error_code));

  }

  @Override
  void visit(AuthenticationImpl errorResponse) {
    System.out.println("Auth response: " + errorResponse.auth_status);
    errorResponse.getPayload().ifPresent(payload -> {
      System.out.println("payload: " + payload.getMethods());
    });
  }

  @Override
  void visitUnknown(ProtocolMessage message) {
    System.out.println("Unknown message type: " + message.getClass());
    System.out.println(message);
  }
}
