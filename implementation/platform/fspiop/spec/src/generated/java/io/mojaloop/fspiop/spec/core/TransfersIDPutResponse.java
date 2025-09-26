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

import java.util.Objects;

/**
 * The object sent in the PUT /transfers/{ID} callback.
 **/

@JsonTypeName("TransfersIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.13.0")
public class TransfersIDPutResponse {

    private String fulfilment;

    private String completedTimestamp;

    private TransferState transferState;

    private ExtensionList extensionList;

    public TransfersIDPutResponse() {

    }

    @JsonCreator
    public TransfersIDPutResponse(
        @JsonProperty(required = true, value = "transferState") TransferState transferState
                                 ) {

        this.transferState = transferState;
    }

    /**
     * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
     **/
    public TransfersIDPutResponse completedTimestamp(String completedTimestamp) {

        this.completedTimestamp = completedTimestamp;
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
        TransfersIDPutResponse transfersIDPutResponse = (TransfersIDPutResponse) o;
        return Objects.equals(this.fulfilment, transfersIDPutResponse.fulfilment) &&
                   Objects.equals(this.completedTimestamp, transfersIDPutResponse.completedTimestamp) &&
                   Objects.equals(this.transferState, transfersIDPutResponse.transferState) &&
                   Objects.equals(this.extensionList, transfersIDPutResponse.extensionList);
    }

    /**
     **/
    public TransfersIDPutResponse extensionList(ExtensionList extensionList) {

        this.extensionList = extensionList;
        return this;
    }

    /**
     * Fulfilment that must be attached to the transfer by the Payee.
     **/
    public TransfersIDPutResponse fulfilment(String fulfilment) {

        this.fulfilment = fulfilment;
        return this;
    }

    @JsonProperty("completedTimestamp")
    @Pattern(
        regexp = "^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:(\\.\\d{3}))(?:Z|[+-][01]\\d:[0-5]\\d)$")
    public String getCompletedTimestamp() {

        return completedTimestamp;
    }

    @JsonProperty("completedTimestamp")
    public void setCompletedTimestamp(String completedTimestamp) {

        this.completedTimestamp = completedTimestamp;
    }

    @JsonProperty("extensionList")
    @Valid
    public ExtensionList getExtensionList() {

        return extensionList;
    }

    @JsonProperty("extensionList")
    public void setExtensionList(ExtensionList extensionList) {

        this.extensionList = extensionList;
    }

    @JsonProperty("fulfilment")
    @Pattern(regexp = "^[A-Za-z0-9-_]{43}$")
    @Size(max = 48)
    public String getFulfilment() {

        return fulfilment;
    }

    @JsonProperty("fulfilment")
    public void setFulfilment(String fulfilment) {

        this.fulfilment = fulfilment;
    }

    @JsonProperty(required = true, value = "transferState")
    @NotNull
    public TransferState getTransferState() {

        return transferState;
    }

    @JsonProperty(required = true, value = "transferState")
    public void setTransferState(TransferState transferState) {

        this.transferState = transferState;
    }

    @Override
    public int hashCode() {

        return Objects.hash(fulfilment, completedTimestamp, transferState, extensionList);
    }

    @Override
    public String toString() {

        String sb = "class TransfersIDPutResponse {\n" +
                        "    fulfilment: " + toIndentedString(fulfilment) + "\n" +
                        "    completedTimestamp: " + toIndentedString(completedTimestamp) + "\n" +
                        "    transferState: " + toIndentedString(transferState) + "\n" +
                        "    extensionList: " + toIndentedString(extensionList) + "\n" +
                        "}";
        return sb;
    }

    /**
     **/
    public TransfersIDPutResponse transferState(TransferState transferState) {

        this.transferState = transferState;
        return this;
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

