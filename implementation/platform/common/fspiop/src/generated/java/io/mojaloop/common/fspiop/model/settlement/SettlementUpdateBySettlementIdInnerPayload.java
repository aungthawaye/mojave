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
package io.mojaloop.common.fspiop.model.settlement;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("SettlementUpdateBySettlementIdInnerPayload")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:33.786087+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class SettlementUpdateBySettlementIdInnerPayload   {
  private Integer id;
  private String reason;
  public enum StateEnum {

    PENDING_SETTLEMENT(String.valueOf("PENDING_SETTLEMENT")), PS_TRANSFERS_RECORDED(String.valueOf("PS_TRANSFERS_RECORDED")), PS_TRANSFERS_RESERVED(String.valueOf("PS_TRANSFERS_RESERVED")), PS_TRANSFERS_COMMITTED(String.valueOf("PS_TRANSFERS_COMMITTED")), SETTLED(String.valueOf("SETTLED"));


    private String value;

    StateEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Convert a String into String, as specified in the
     * <a href="https://download.oracle.com/otndocs/jcp/jaxrs-2_0-fr-eval-spec/index.html">See JAX RS 2.0 Specification, section 3.2, p. 12</a>
     */
    public static StateEnum fromString(String s) {
        for (StateEnum b : StateEnum.values()) {
            // using Objects.toString() to be safe if value type non-object type
            // because types like 'int' etc. will be auto-boxed
            if (java.util.Objects.toString(b.value).equals(s)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected string value '" + s + "'");
    }

    @JsonCreator
    public static StateEnum fromValue(String value) {
        for (StateEnum b : StateEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

  private StateEnum state;
  private String externalReference;

  public SettlementUpdateBySettlementIdInnerPayload() {
  }

  /**
   * Participant Currency Id
   **/
  public SettlementUpdateBySettlementIdInnerPayload id(Integer id) {
    this.id = id;
    return this;
  }

  
  @JsonProperty("id")
  public Integer getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   **/
  public SettlementUpdateBySettlementIdInnerPayload reason(String reason) {
    this.reason = reason;
    return this;
  }

  
  @JsonProperty("reason")
  public String getReason() {
    return reason;
  }

  @JsonProperty("reason")
  public void setReason(String reason) {
    this.reason = reason;
  }

  /**
   **/
  public SettlementUpdateBySettlementIdInnerPayload state(StateEnum state) {
    this.state = state;
    return this;
  }

  
  @JsonProperty("state")
  public StateEnum getState() {
    return state;
  }

  @JsonProperty("state")
  public void setState(StateEnum state) {
    this.state = state;
  }

  /**
   **/
  public SettlementUpdateBySettlementIdInnerPayload externalReference(String externalReference) {
    this.externalReference = externalReference;
    return this;
  }

  
  @JsonProperty("externalReference")
  public String getExternalReference() {
    return externalReference;
  }

  @JsonProperty("externalReference")
  public void setExternalReference(String externalReference) {
    this.externalReference = externalReference;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SettlementUpdateBySettlementIdInnerPayload settlementUpdateBySettlementIdInnerPayload = (SettlementUpdateBySettlementIdInnerPayload) o;
    return Objects.equals(this.id, settlementUpdateBySettlementIdInnerPayload.id) &&
        Objects.equals(this.reason, settlementUpdateBySettlementIdInnerPayload.reason) &&
        Objects.equals(this.state, settlementUpdateBySettlementIdInnerPayload.state) &&
        Objects.equals(this.externalReference, settlementUpdateBySettlementIdInnerPayload.externalReference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, reason, state, externalReference);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SettlementUpdateBySettlementIdInnerPayload {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    externalReference: ").append(toIndentedString(externalReference)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


}

