package com.github.warmuuh.jedge.db.protocol;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import lombok.SneakyThrows;

public class AuthenticationImpl extends Authentication {

  public enum AuthenticationStatus {
    OK,
    SASL_REQUIRED,
    SASL_CONTINUE,
    SASL_FINAL;
  }

  public AuthenticationStatus getStatus() {
    switch (auth_status){
      case 0xa: return AuthenticationStatus.SASL_REQUIRED;
      case 0xb: return AuthenticationStatus.SASL_CONTINUE;
      case 0xc: return AuthenticationStatus.SASL_FINAL;
      default: return AuthenticationStatus.OK;
    }
  }

  @SneakyThrows
  public Optional<AuthenticationRequiredSaslPayloadImpl> getPayload() {
    if (getStatus() == AuthenticationStatus.SASL_REQUIRED) {
      AuthenticationRequiredSaslPayloadImpl payload = new AuthenticationRequiredSaslPayloadImpl();
      payload.read(new JBBPBitInputStream(new ByteArrayInputStream(data)));
      return Optional.of(payload);
    }
    return Optional.empty();
  }

  @SneakyThrows
  public Optional<AuthenticationContinueSaslPayloadImpl> getContinuePayload() {
    if (getStatus() == AuthenticationStatus.SASL_CONTINUE) {
      AuthenticationContinueSaslPayloadImpl payload = new AuthenticationContinueSaslPayloadImpl();
      payload.read(new JBBPBitInputStream(new ByteArrayInputStream(data)));
      return Optional.of(payload);
    }
    return Optional.empty();
  }
}
