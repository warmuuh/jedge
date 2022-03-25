package com.github.warmuuh.jedge.db.protocol;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

public class ReadyForCommandImpl extends ReadyForCommand {
  @Delegate
  private final StringReader stringReader = new StringReader();

  @AllArgsConstructor
  public enum TransactionState {
    NOT_IN_TRANSACTION(0x49),
    IN_TRANSACTION (0x54),
    IN_FAILED_TRANSACTION(0x45);
    int id;
  }

  public TransactionState getTransactionState() {
    for (TransactionState state : TransactionState.values()) {
      if (state.id == transaction_state) {
        return state;
      }
    }
    throw new IllegalArgumentException("Transactionstate unknown: " + transaction_state);
  }

}
