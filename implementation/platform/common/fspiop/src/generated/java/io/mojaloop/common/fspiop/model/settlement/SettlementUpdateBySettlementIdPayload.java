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

import io.mojaloop.common.fspiop.model.settlement.SettlementUpdateBySettlementIDOuterPayload;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("SettlementUpdateBySettlementIdPayload")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T21:01:09.599695+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class SettlementUpdateBySettlementIdPayload   {
  public enum StateEnum {

    ABORTED(String.valueOf("ABORTED")), INVALID(String.valueOf("INVALID"));


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
  private String reason;
  private String externalReference;
  private @Valid List<@Valid SettlementUpdateBySettlementIDOuterPayload> participants = new ArrayList<>();

  public SettlementUpdateBySettlementIdPayload() {
  }

  /**
   **/
  public SettlementUpdateBySettlementIdPayload state(StateEnum state) {
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
  public SettlementUpdateBySettlementIdPayload reason(String reason) {
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
  public SettlementUpdateBySettlementIdPayload externalReference(String externalReference) {
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

  /**
   **/
  public SettlementUpdateBySettlementIdPayload participants(List<@Valid SettlementUpdateBySettlementIDOuterPayload> participants) {
    this.participants = participants;
    return this;
  }

  
  @JsonProperty("participants")
  @Valid public List<@Valid SettlementUpdateBySettlementIDOuterPayload> getParticipants() {
    return participants;
  }

  @JsonProperty("participants")
  public void setParticipants(List<@Valid SettlementUpdateBySettlementIDOuterPayload> participants) {
    this.participants = participants;
  }

  public SettlementUpdateBySettlementIdPayload addParticipantsItem(SettlementUpdateBySettlementIDOuterPayload participantsItem) {
    if (this.participants == null) {
      this.participants = new ArrayList<>();
    }

    this.participants.add(participantsItem);
    return this;
  }

  public SettlementUpdateBySettlementIdPayload removeParticipantsItem(SettlementUpdateBySettlementIDOuterPayload participantsItem) {
    if (participantsItem != null && this.participants != null) {
      this.participants.remove(participantsItem);
    }

    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SettlementUpdateBySettlementIdPayload settlementUpdateBySettlementIdPayload = (SettlementUpdateBySettlementIdPayload) o;
    return Objects.equals(this.state, settlementUpdateBySettlementIdPayload.state) &&
        Objects.equals(this.reason, settlementUpdateBySettlementIdPayload.reason) &&
        Objects.equals(this.externalReference, settlementUpdateBySettlementIdPayload.externalReference) &&
        Objects.equals(this.participants, settlementUpdateBySettlementIdPayload.participants);
  }

  @Override
  public int hashCode() {
    return Objects.hash(state, reason, externalReference, participants);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SettlementUpdateBySettlementIdPayload {\n");
    
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    externalReference: ").append(toIndentedString(externalReference)).append("\n");
    sb.append("    participants: ").append(toIndentedString(participants)).append("\n");
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

