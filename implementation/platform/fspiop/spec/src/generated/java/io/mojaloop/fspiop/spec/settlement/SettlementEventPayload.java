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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@JsonTypeName("SettlementEventPayload")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-09-08T15:21:55.746682+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class SettlementEventPayload {

    private String settlementModel;

    private String reason;

    private @Valid Set<@Valid SettlementWindowId> settlementWindows = new LinkedHashSet<>();

    public SettlementEventPayload() {

    }

    @JsonCreator
    public SettlementEventPayload(
        @JsonProperty(required = true, value = "settlementModel") String settlementModel,
        @JsonProperty(required = true, value = "reason") String reason,
        @JsonProperty(required = true, value = "settlementWindows") Set<@Valid SettlementWindowId> settlementWindows
                                 ) {

        this.settlementModel = settlementModel;
        this.reason = reason;
        this.settlementWindows = settlementWindows;
    }

    public SettlementEventPayload addSettlementWindowsItem(SettlementWindowId settlementWindowsItem) {

        if (this.settlementWindows == null) {
            this.settlementWindows = new LinkedHashSet<>();
        }

        this.settlementWindows.add(settlementWindowsItem);
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
        SettlementEventPayload settlementEventPayload = (SettlementEventPayload) o;
        return Objects.equals(this.settlementModel, settlementEventPayload.settlementModel) &&
                   Objects.equals(this.reason, settlementEventPayload.reason) &&
                   Objects.equals(this.settlementWindows, settlementEventPayload.settlementWindows);
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

    @JsonProperty(required = true, value = "settlementModel")
    @NotNull
    public String getSettlementModel() {

        return settlementModel;
    }

    @JsonProperty(required = true, value = "settlementModel")
    public void setSettlementModel(String settlementModel) {

        this.settlementModel = settlementModel;
    }

    @JsonProperty(required = true, value = "settlementWindows")
    @NotNull
    @Valid
    @Size(min = 1)
    public Set<@Valid SettlementWindowId> getSettlementWindows() {

        return settlementWindows;
    }

    @JsonProperty(required = true, value = "settlementWindows")
    @JsonDeserialize(as = LinkedHashSet.class)
    public void setSettlementWindows(Set<@Valid SettlementWindowId> settlementWindows) {

        this.settlementWindows = settlementWindows;
    }

    @Override
    public int hashCode() {

        return Objects.hash(settlementModel, reason, settlementWindows);
    }

    /**
     **/
    public SettlementEventPayload reason(String reason) {

        this.reason = reason;
        return this;
    }

    public SettlementEventPayload removeSettlementWindowsItem(SettlementWindowId settlementWindowsItem) {

        if (settlementWindowsItem != null && this.settlementWindows != null) {
            this.settlementWindows.remove(settlementWindowsItem);
        }

        return this;
    }

    /**
     **/
    public SettlementEventPayload settlementModel(String settlementModel) {

        this.settlementModel = settlementModel;
        return this;
    }

    /**
     **/
    public SettlementEventPayload settlementWindows(Set<@Valid SettlementWindowId> settlementWindows) {

        this.settlementWindows = settlementWindows;
        return this;
    }

    @Override
    public String toString() {

        String sb = "class SettlementEventPayload {\n" +
                        "    settlementModel: " + toIndentedString(settlementModel) + "\n" +
                        "    reason: " + toIndentedString(reason) + "\n" +
                        "    settlementWindows: " + toIndentedString(settlementWindows) + "\n" +
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

}

