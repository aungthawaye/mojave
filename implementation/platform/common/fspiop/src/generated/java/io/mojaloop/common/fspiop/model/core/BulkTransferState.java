/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package io.mojaloop.common.fspiop.model.core;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Below are the allowed values for the enumeration. - RECEIVED - Payee FSP has received the bulk transfer from the Payer FSP. - PENDING - Payee FSP has validated the bulk transfer. - ACCEPTED - Payee FSP has accepted to process the bulk transfer. - PROCESSING - Payee FSP has started to transfer fund to the Payees. - COMPLETED - Payee FSP has completed transfer of funds to the Payees. - REJECTED - Payee FSP has rejected to process the bulk transfer.
 */
public enum BulkTransferState {
  
  RECEIVED("RECEIVED"),
  
  PENDING("PENDING"),
  
  ACCEPTED("ACCEPTED"),
  
  PROCESSING("PROCESSING"),
  
  COMPLETED("COMPLETED"),
  
  REJECTED("REJECTED");

  private String value;

  BulkTransferState(String value) {
    this.value = value;
  }

    /**
     * Convert a String into String, as specified in the
     * <a href="https://download.oracle.com/otndocs/jcp/jaxrs-2_0-fr-eval-spec/index.html">See JAX RS 2.0 Specification, section 3.2, p. 12</a>
     */
    public static BulkTransferState fromString(String s) {
      for (BulkTransferState b : BulkTransferState.values()) {
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
  public static BulkTransferState fromValue(String value) {
    for (BulkTransferState b : BulkTransferState.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}


