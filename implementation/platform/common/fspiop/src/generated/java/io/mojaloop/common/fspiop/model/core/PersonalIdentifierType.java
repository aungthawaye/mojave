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
 * Below are the allowed values for the enumeration. - PASSPORT - A passport number is used as reference to a Party. - NATIONAL_REGISTRATION - A national registration number is used as reference to a Party. - DRIVING_LICENSE - A driving license is used as reference to a Party. - ALIEN_REGISTRATION - An alien registration number is used as reference to a Party. - NATIONAL_ID_CARD - A national ID card number is used as reference to a Party. - EMPLOYER_ID - A tax identification number is used as reference to a Party. - TAX_ID_NUMBER - A tax identification number is used as reference to a Party. - SENIOR_CITIZENS_CARD - A senior citizens card number is used as reference to a Party. - MARRIAGE_CERTIFICATE - A marriage certificate number is used as reference to a Party. - HEALTH_CARD - A health card number is used as reference to a Party. - VOTERS_ID - A voter’s identification number is used as reference to a Party. - UNITED_NATIONS - An UN (United Nations) number is used as reference to a Party. - OTHER_ID - Any other type of identification type number is used as reference to a Party.
 */
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


