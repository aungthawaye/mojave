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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonTypeName("Settlement")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              date = "2025-11-09T11:40:06.177887+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class Settlement {

    private Integer id;

    private String state;

    private @Valid List<List<@Valid SettlementWindow>> settlementWindows = new ArrayList<>();

    private @Valid List<@Valid Participant> participants = new ArrayList<>();

    public Settlement() {

    }

    public Settlement addParticipantsItem(Participant participantsItem) {

        if (this.participants == null) {
            this.participants = new ArrayList<>();
        }

        this.participants.add(participantsItem);
        return this;
    }

    public Settlement addSettlementWindowsItem(List<@Valid SettlementWindow> settlementWindowsItem) {

        if (this.settlementWindows == null) {
            this.settlementWindows = new ArrayList<>();
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
        Settlement settlement = (Settlement) o;
        return Objects.equals(this.id, settlement.id) && Objects.equals(this.state, settlement.state) && Objects.equals(this.settlementWindows, settlement.settlementWindows) &&
                   Objects.equals(this.participants, settlement.participants);
    }

    @JsonProperty("id")
    public Integer getId() {

        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {

        this.id = id;
    }

    @JsonProperty("participants")
    @Valid
    public List<@Valid Participant> getParticipants() {

        return participants;
    }

    @JsonProperty("participants")
    public void setParticipants(List<@Valid Participant> participants) {

        this.participants = participants;
    }

    @JsonProperty("settlementWindows")
    @Valid
    public List<@Valid List<@Valid SettlementWindow>> getSettlementWindows() {

        return settlementWindows;
    }

    @JsonProperty("settlementWindows")
    public void setSettlementWindows(List<List<@Valid SettlementWindow>> settlementWindows) {

        this.settlementWindows = settlementWindows;
    }

    @JsonProperty("state")
    public String getState() {

        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {

        this.state = state;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, state, settlementWindows, participants);
    }

    /**
     **/
    public Settlement id(Integer id) {

        this.id = id;
        return this;
    }

    /**
     **/
    public Settlement participants(List<@Valid Participant> participants) {

        this.participants = participants;
        return this;
    }

    public Settlement removeParticipantsItem(Participant participantsItem) {

        if (participantsItem != null && this.participants != null) {
            this.participants.remove(participantsItem);
        }

        return this;
    }

    public Settlement removeSettlementWindowsItem(List<@Valid SettlementWindow> settlementWindowsItem) {

        if (settlementWindowsItem != null && this.settlementWindows != null) {
            this.settlementWindows.remove(settlementWindowsItem);
        }

        return this;
    }

    /**
     **/
    public Settlement settlementWindows(List<List<@Valid SettlementWindow>> settlementWindows) {

        this.settlementWindows = settlementWindows;
        return this;
    }

    /**
     **/
    public Settlement state(String state) {

        this.state = state;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class Settlement {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    settlementWindows: ").append(toIndentedString(settlementWindows)).append("\n");
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

