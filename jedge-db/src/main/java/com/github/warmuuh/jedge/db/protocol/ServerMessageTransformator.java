package com.github.warmuuh.jedge.db.protocol;

public abstract class ServerMessageTransformator<T> {

  abstract T visit(ServerHandshakeImpl serverHandshake);
  abstract T visit(ErrorResponseImpl errorResponse);


  public T visit(ProtocolMessage message) {

    if (message instanceof ServerHandshakeImpl) {
      return visit((ServerHandshakeImpl) message);
    }

    if (message instanceof ErrorResponseImpl) {
      return visit((ErrorResponseImpl) message);
    }

    throw new IllegalArgumentException("Unknown server response type: " + message.getClass());
  }

}
