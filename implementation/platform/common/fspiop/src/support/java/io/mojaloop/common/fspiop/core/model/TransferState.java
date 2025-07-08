package io.mojaloop.common.fspiop.core.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Below are the allowed values for the enumeration. - RECEIVED - Next ledger has received the transfer. - RESERVED - Next ledger has reserved the transfer. - COMMITTED - Next ledger has successfully performed the transfer. - ABORTED - Next ledger has aborted the transfer due to a rejection or failure to perform the transfer.
 */
public enum TransferState {
  
  RECEIVED("RECEIVED"),
  
  RESERVED("RESERVED"),
  
  COMMITTED("COMMITTED"),
  
  ABORTED("ABORTED");

  private String value;

  TransferState(String value) {
    this.value = value;
  }

    /**
     * Convert a String into String, as specified in the
     * <a href="https://download.oracle.com/otndocs/jcp/jaxrs-2_0-fr-eval-spec/index.html">See JAX RS 2.0 Specification, section 3.2, p. 12</a>
     */
    public static TransferState fromString(String s) {
      for (TransferState b : TransferState.values()) {
        // using Objects.toString() to be safe if value type non-object type
        // because types like 'int' etc. will be auto-boxed
        if (java.util.Objects.toString(b.value).equals(s)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected string value '" + s + "'");
    }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static TransferState fromValue(String value) {
    for (TransferState b : TransferState.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}


