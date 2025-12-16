/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.fspiop.spec.core;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
public enum PersonalIdentifierType {
  
  PASSPORT("PASSPORT"),
  
  NATIONAL_REGISTRATION("NATIONAL_REGISTRATION"),
  
  DRIVING_LICENSE("DRIVING_LICENSE"),
  
  ALIEN_REGISTRATION("ALIEN_REGISTRATION"),
  
  NATIONAL_ID_CARD("NATIONAL_ID_CARD"),
  
  EMPLOYER_ID("EMPLOYER_ID"),
  
  TAX_ID_NUMBER("TAX_ID_NUMBER"),
  
  SENIOR_CITIZENS_CARD("SENIOR_CITIZENS_CARD"),
  
  MARRIAGE_CERTIFICATE("MARRIAGE_CERTIFICATE"),
  
  HEALTH_CARD("HEALTH_CARD"),
  
  VOTERS_ID("VOTERS_ID"),
  
  UNITED_NATIONS("UNITED_NATIONS"),
  
  OTHER_ID("OTHER_ID");

  private String value;

  PersonalIdentifierType(String value) {
    this.value = value;
  }

    /**
     * Convert a String into String, as specified in the
     * <a href="https://download.oracle.com/otndocs/jcp/jaxrs-2_0-fr-eval-spec/index.html">See JAX RS 2.0 Specification, section 3.2, p. 12</a>
     */
    public static PersonalIdentifierType fromString(String s) {
      for (PersonalIdentifierType b : PersonalIdentifierType.values()) {
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
  public static PersonalIdentifierType fromValue(String value) {
    for (PersonalIdentifierType b : PersonalIdentifierType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}


