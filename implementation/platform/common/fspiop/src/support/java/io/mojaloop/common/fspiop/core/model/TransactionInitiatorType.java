package io.mojaloop.common.fspiop.core.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Below are the allowed values for the enumeration. - CONSUMER - Consumer is the initiator of the transaction. - AGENT - Agent is the initiator of the transaction. - BUSINESS - Business is the initiator of the transaction. - DEVICE - Device is the initiator of the transaction.
 */
public enum TransactionInitiatorType {
  
  CONSUMER("CONSUMER"),
  
  AGENT("AGENT"),
  
  BUSINESS("BUSINESS"),
  
  DEVICE("DEVICE");

  private String value;

  TransactionInitiatorType(String value) {
    this.value = value;
  }

    /**
     * Convert a String into String, as specified in the
     * <a href="https://download.oracle.com/otndocs/jcp/jaxrs-2_0-fr-eval-spec/index.html">See JAX RS 2.0 Specification, section 3.2, p. 12</a>
     */
    public static TransactionInitiatorType fromString(String s) {
      for (TransactionInitiatorType b : TransactionInitiatorType.values()) {
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
  public static TransactionInitiatorType fromValue(String value) {
    for (TransactionInitiatorType b : TransactionInitiatorType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}


