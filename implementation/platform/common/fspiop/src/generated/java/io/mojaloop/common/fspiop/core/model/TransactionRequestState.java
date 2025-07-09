package io.mojaloop.common.fspiop.core.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Below are the allowed values for the enumeration. - RECEIVED - Payer FSP has received the transaction from the Payee FSP. - PENDING - Payer FSP has sent the transaction request to the Payer. - ACCEPTED - Payer has approved the transaction. - REJECTED - Payer has rejected the transaction.
 */
public enum TransactionRequestState {
  
  RECEIVED("RECEIVED"),
  
  PENDING("PENDING"),
  
  ACCEPTED("ACCEPTED"),
  
  REJECTED("REJECTED");

  private String value;

  TransactionRequestState(String value) {
    this.value = value;
  }

    /**
     * Convert a String into String, as specified in the
     * <a href="https://download.oracle.com/otndocs/jcp/jaxrs-2_0-fr-eval-spec/index.html">See JAX RS 2.0 Specification, section 3.2, p. 12</a>
     */
    public static TransactionRequestState fromString(String s) {
      for (TransactionRequestState b : TransactionRequestState.values()) {
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
  public static TransactionRequestState fromValue(String value) {
    for (TransactionRequestState b : TransactionRequestState.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}


