package com.github.warmuuh.jedge.db.protocol;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingMessageVisitor extends ServerMessageVisitor {

  @Override
  void visit(ServerHandshakeImpl message) {
      log.info("response: {}", message.major_ver);
  }

  @Override
  void visit(ErrorResponseImpl message) {
    log.error("error: {} (code: {})", message.getMessage(), Integer.toHexString(message.error_code));
  }

  @Override
  void visit(AuthenticationImpl authMsg) {
    log.info("Auth response: {}, (code: {})", authMsg.getStatus(), authMsg.auth_status);
    authMsg.getPayload().ifPresent(payload -> {
      log.info("Auth response payload: " + payload.getMethods());
    });
  }

  @Override
  void visitUnknown(ProtocolMessage message) {
    log.info("Unknown message type: " + message.getClass());
    log.info(message.toString());
  }
}
