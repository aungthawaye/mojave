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

package io.mojaloop.fspiop.spec.settlement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@JsonTypeName("SettlementUpdateBySettlementParticipantAccount")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-09-08T15:21:55.746682+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class SettlementUpdateBySettlementParticipantAccount {

    private StateEnum state;

    private String reason;

    private String externalReference;

    public SettlementUpdateBySettlementParticipantAccount() {

    }

    @JsonCreator
    public SettlementUpdateBySettlementParticipantAccount(
        @JsonProperty(required = true, value = "state") StateEnum state,
        @JsonProperty(required = true, value = "reason") String reason
                                                         ) {

        this.state = state;
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SettlementUpdateBySettlementParticipantAccount settlementUpdateBySettlementParticipantAccount = (SettlementUpdateBySettlementParticipantAccount) o;
        return Objects.equals(this.state, settlementUpdateBySettlementParticipantAccount.state) &&
                   Objects.equals(this.reason, settlementUpdateBySettlementParticipantAccount.reason) &&
                   Objects.equals(this.externalReference, settlementUpdateBySettlementParticipantAccount.externalReference);
    }

    /**
     **/
    public SettlementUpdateBySettlementParticipantAccount externalReference(String externalReference) {

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

    @JsonProperty(required = true, value = "reason")
    @NotNull
    public String getReason() {

        return reason;
    }

    @JsonProperty(required = true, value = "reason")
    public void setReason(String reason) {

        this.reason = reason;
    }

    @JsonProperty(required = true, value = "state")
    @NotNull
    public StateEnum getState() {

        return state;
    }

    @JsonProperty(required = true, value = "state")
    public void setState(StateEnum state) {

        this.state = state;
    }

    @Override
    public int hashCode() {

        return Objects.hash(state, reason, externalReference);
    }

    /**
     **/
    public SettlementUpdateBySettlementParticipantAccount reason(String reason) {

        this.reason = reason;
        return this;
    }

    /**
     **/
    public SettlementUpdateBySettlementParticipantAccount state(StateEnum state) {

        this.state = state;
        return this;
    }

    @Override
    public String toString() {

        String sb = "class SettlementUpdateBySettlementParticipantAccount {\n" +
                        "    state: " + toIndentedString(state) + "\n" +
                        "    reason: " + toIndentedString(reason) + "\n" +
                        "    externalReference: " + toIndentedString(externalReference) + "\n" +
                        "}";
        return sb;
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

        PENDING_SETTLEMENT("PENDING_SETTLEMENT"),
        PS_TRANSFERS_RECORDED("PS_TRANSFERS_RECORDED"),
        PS_TRANSFERS_RESERVED("PS_TRANSFERS_RESERVED"),
        PS_TRANSFERS_COMMITTED("PS_TRANSFERS_COMMITTED"),
        SETTLED("SETTLED");

        private final String value;

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

