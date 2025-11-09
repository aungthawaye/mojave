/*-
 * ================================================================================
 * Mojave
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
package io.mojaloop.fspiop.spec.settlement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonTypeName("SettlementUpdateBySettlementIdPayload")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              date = "2025-11-09T11:40:06.177887+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class SettlementUpdateBySettlementIdPayload {

    private StateEnum state;

    private String reason;

    private String externalReference;

    private @Valid List<@Valid SettlementUpdateBySettlementIDOuterPayload> participants = new ArrayList<>();

    public SettlementUpdateBySettlementIdPayload() {

    }

    public SettlementUpdateBySettlementIdPayload addParticipantsItem(SettlementUpdateBySettlementIDOuterPayload participantsItem) {

        if (this.participants == null) {
            this.participants = new ArrayList<>();
        }

        this.participants.add(participantsItem);
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
        return Objects.equals(this.state, settlementUpdateBySettlementIdPayload.state) && Objects.equals(this.reason, settlementUpdateBySettlementIdPayload.reason) &&
                   Objects.equals(this.externalReference, settlementUpdateBySettlementIdPayload.externalReference) &&
                   Objects.equals(this.participants, settlementUpdateBySettlementIdPayload.participants);
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

    @JsonProperty("participants")
    @Valid
    public List<@Valid SettlementUpdateBySettlementIDOuterPayload> getParticipants() {

        return participants;
    }

    @JsonProperty("participants")
    public void setParticipants(List<@Valid SettlementUpdateBySettlementIDOuterPayload> participants) {

        this.participants = participants;
    }

    @JsonProperty("reason")
    public String getReason() {

        return reason;
    }

    @JsonProperty("reason")
    public void setReason(String reason) {

        this.reason = reason;
    }

    @JsonProperty("state")
    public StateEnum getState() {

        return state;
    }

    @JsonProperty("state")
    public void setState(StateEnum state) {

        this.state = state;
    }

    @Override
    public int hashCode() {

        return Objects.hash(state, reason, externalReference, participants);
    }

    /**
     **/
    public SettlementUpdateBySettlementIdPayload participants(List<@Valid SettlementUpdateBySettlementIDOuterPayload> participants) {

        this.participants = participants;
        return this;
    }

    /**
     **/
    public SettlementUpdateBySettlementIdPayload reason(String reason) {

        this.reason = reason;
        return this;
    }

    public SettlementUpdateBySettlementIdPayload removeParticipantsItem(SettlementUpdateBySettlementIDOuterPayload participantsItem) {

        if (participantsItem != null && this.participants != null) {
            this.participants.remove(participantsItem);
        }

        return this;
    }

    /**
     **/
    public SettlementUpdateBySettlementIdPayload state(StateEnum state) {

        this.state = state;
        return this;
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

    public enum StateEnum {

        ABORTED(String.valueOf("ABORTED")),
        INVALID(String.valueOf("INVALID"));

        private String value;

        StateEnum(String v) {

            value = v;
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

        @Override
        @JsonValue
        public String toString() {

            return String.valueOf(value);
        }

        public String value() {

            return value;
        }
    }

}

