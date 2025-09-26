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
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The object sent in the POST /participants request.
 **/

@JsonTypeName("ParticipantsPostRequest")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.13.0")
public class ParticipantsPostRequest {

    private String requestId;

    private @Valid List<@Valid PartyIdInfo> partyList = new ArrayList<>();

    private Currency currency;

    public ParticipantsPostRequest() {

    }

    @JsonCreator
    public ParticipantsPostRequest(
        @JsonProperty(required = true, value = "requestId") String requestId,
        @JsonProperty(required = true, value = "partyList") List<@Valid PartyIdInfo> partyList
                                  ) {

        this.requestId = requestId;
        this.partyList = partyList;
    }

    public ParticipantsPostRequest addPartyListItem(PartyIdInfo partyListItem) {

        if (this.partyList == null) {
            this.partyList = new ArrayList<>();
        }

        this.partyList.add(partyListItem);
        return this;
    }

    /**
     **/
    public ParticipantsPostRequest currency(Currency currency) {

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
        ParticipantsPostRequest participantsPostRequest = (ParticipantsPostRequest) o;
        return Objects.equals(this.requestId, participantsPostRequest.requestId) &&
                   Objects.equals(this.partyList, participantsPostRequest.partyList) &&
                   Objects.equals(this.currency, participantsPostRequest.currency);
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
    public List<@Valid PartyIdInfo> getPartyList() {

        return partyList;
    }

    @JsonProperty(required = true, value = "partyList")
    public void setPartyList(List<@Valid PartyIdInfo> partyList) {

        this.partyList = partyList;
    }

    @JsonProperty(required = true, value = "requestId")
    @NotNull
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    public String getRequestId() {

        return requestId;
    }

    @JsonProperty(required = true, value = "requestId")
    public void setRequestId(String requestId) {

        this.requestId = requestId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(requestId, partyList, currency);
    }

    /**
     * List of PartyIdInfo elements that the client would like to update or create FSP information about.
     **/
    public ParticipantsPostRequest partyList(List<@Valid PartyIdInfo> partyList) {

        this.partyList = partyList;
        return this;
    }

    public ParticipantsPostRequest removePartyListItem(PartyIdInfo partyListItem) {

        if (partyListItem != null && this.partyList != null) {
            this.partyList.remove(partyListItem);
        }

        return this;
    }

    /**
     * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
     **/
    public ParticipantsPostRequest requestId(String requestId) {

        this.requestId = requestId;
        return this;
    }

    @Override
    public String toString() {

        String sb = "class ParticipantsPostRequest {\n" +
                        "    requestId: " + toIndentedString(requestId) + "\n" +
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

