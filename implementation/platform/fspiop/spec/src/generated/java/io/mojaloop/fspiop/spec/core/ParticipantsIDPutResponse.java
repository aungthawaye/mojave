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

package io.mojaloop.fspiop.spec.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The object sent in the PUT /participants/{ID} callback.
 **/

@JsonTypeName("ParticipantsIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.13.0")
public class ParticipantsIDPutResponse {

    private @Valid List<@Valid PartyResult> partyList = new ArrayList<>();

    private Currency currency;

    public ParticipantsIDPutResponse() {

    }

    @JsonCreator
    public ParticipantsIDPutResponse(
        @JsonProperty(required = true, value = "partyList") List<@Valid PartyResult> partyList
                                    ) {

        this.partyList = partyList;
    }

    public ParticipantsIDPutResponse addPartyListItem(PartyResult partyListItem) {

        if (this.partyList == null) {
            this.partyList = new ArrayList<>();
        }

        this.partyList.add(partyListItem);
        return this;
    }

    /**
     **/
    public ParticipantsIDPutResponse currency(Currency currency) {

        this.currency = currency;
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
        ParticipantsIDPutResponse participantsIDPutResponse = (ParticipantsIDPutResponse) o;
        return Objects.equals(this.partyList, participantsIDPutResponse.partyList) &&
                   Objects.equals(this.currency, participantsIDPutResponse.currency);
    }

    @JsonProperty("currency")
    public Currency getCurrency() {

        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(Currency currency) {

        this.currency = currency;
    }

    @JsonProperty(required = true, value = "partyList")
    @NotNull
    @Valid
    @Size(min = 1, max = 10000)
    public List<@Valid PartyResult> getPartyList() {

        return partyList;
    }

    @JsonProperty(required = true, value = "partyList")
    public void setPartyList(List<@Valid PartyResult> partyList) {

        this.partyList = partyList;
    }

    @Override
    public int hashCode() {

        return Objects.hash(partyList, currency);
    }

    /**
     * List of PartyResult elements that were either created or failed to be created.
     **/
    public ParticipantsIDPutResponse partyList(List<@Valid PartyResult> partyList) {

        this.partyList = partyList;
        return this;
    }

    public ParticipantsIDPutResponse removePartyListItem(PartyResult partyListItem) {

        if (partyListItem != null && this.partyList != null) {
            this.partyList.remove(partyListItem);
        }

        return this;
    }

    @Override
    public String toString() {

        String sb = "class ParticipantsIDPutResponse {\n" +
                        "    partyList: " + toIndentedString(partyList) + "\n" +
                        "    currency: " + toIndentedString(currency) + "\n" +
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

