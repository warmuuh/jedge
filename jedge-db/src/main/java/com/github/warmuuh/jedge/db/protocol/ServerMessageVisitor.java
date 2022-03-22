package com.github.warmuuh.jedge.db.protocol;

public abstract class ServerMessageVisitor {

  abstract void visit(ServerHandshakeImpl serverHandshake);
  abstract void visit(ErrorResponseImpl errorResponse);
  abstract void visit(AuthenticationImpl errorResponse);

  abstract void visitUnknown(ProtocolMessage message);

  public void visit(ProtocolMessage message) {

    if (message instanceof ServerHandshakeImpl) {
      visit((ServerHandshakeImpl) message);
    } else if (message instanceof ErrorResponseImpl) {
      visit((ErrorResponseImpl) message);
    } else if (message instanceof  Authentication) {
      visit((AuthenticationImpl) message);
    }

    else {
      visitUnknown(message);
    }
  }

}
