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
 * The object sent in the POST /transactionRequests request.
 **/

@JsonTypeName("TransactionRequestsPostRequest")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.13.0")
public class TransactionRequestsPostRequest {

    private String transactionRequestId;

    private Party payee;

    private PartyIdInfo payer;

    private Money amount;

    private TransactionType transactionType;

    private String note;

    private GeoCode geoCode;

    private AuthenticationType authenticationType;

    private String expiration;

    private ExtensionList extensionList;

    public TransactionRequestsPostRequest() {

    }

    @JsonCreator
    public TransactionRequestsPostRequest(
        @JsonProperty(required = true, value = "transactionRequestId") String transactionRequestId,
        @JsonProperty(required = true, value = "payee") Party payee,
        @JsonProperty(required = true, value = "payer") PartyIdInfo payer,
        @JsonProperty(required = true, value = "amount") Money amount,
        @JsonProperty(required = true, value = "transactionType") TransactionType transactionType
                                         ) {

        this.transactionRequestId = transactionRequestId;
        this.payee = payee;
        this.payer = payer;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    /**
     **/
    public TransactionRequestsPostRequest amount(Money amount) {

        this.amount = amount;
        return this;
    }

    /**
     **/
    public TransactionRequestsPostRequest authenticationType(AuthenticationType authenticationType) {

        this.authenticationType = authenticationType;
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
        TransactionRequestsPostRequest transactionRequestsPostRequest = (TransactionRequestsPostRequest) o;
        return Objects.equals(this.transactionRequestId, transactionRequestsPostRequest.transactionRequestId) &&
                   Objects.equals(this.payee, transactionRequestsPostRequest.payee) &&
                   Objects.equals(this.payer, transactionRequestsPostRequest.payer) &&
                   Objects.equals(this.amount, transactionRequestsPostRequest.amount) &&
                   Objects.equals(this.transactionType, transactionRequestsPostRequest.transactionType) &&
                   Objects.equals(this.note, transactionRequestsPostRequest.note) &&
                   Objects.equals(this.geoCode, transactionRequestsPostRequest.geoCode) &&
                   Objects.equals(this.authenticationType, transactionRequestsPostRequest.authenticationType) &&
                   Objects.equals(this.expiration, transactionRequestsPostRequest.expiration) &&
                   Objects.equals(this.extensionList, transactionRequestsPostRequest.extensionList);
    }

    /**
     * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
     **/
    public TransactionRequestsPostRequest expiration(String expiration) {

        this.expiration = expiration;
        return this;
    }

    /**
     **/
    public TransactionRequestsPostRequest extensionList(ExtensionList extensionList) {

        this.extensionList = extensionList;
        return this;
    }

    /**
     **/
    public TransactionRequestsPostRequest geoCode(GeoCode geoCode) {

        this.geoCode = geoCode;
        return this;
    }

    @JsonProperty(required = true, value = "amount")
    @NotNull
    @Valid
    public Money getAmount() {

        return amount;
    }

    @JsonProperty(required = true, value = "amount")
    public void setAmount(Money amount) {

        this.amount = amount;
    }

    @JsonProperty("authenticationType")
    public AuthenticationType getAuthenticationType() {

        return authenticationType;
    }

    @JsonProperty("authenticationType")
    public void setAuthenticationType(AuthenticationType authenticationType) {

        this.authenticationType = authenticationType;
    }

    @JsonProperty("expiration")
    @Pattern(
        regexp = "^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:(\\.\\d{3}))(?:Z|[+-][01]\\d:[0-5]\\d)$")
    public String getExpiration() {

        return expiration;
    }

    @JsonProperty("expiration")
    public void setExpiration(String expiration) {

        this.expiration = expiration;
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

    @JsonProperty("geoCode")
    @Valid
    public GeoCode getGeoCode() {

        return geoCode;
    }

    @JsonProperty("geoCode")
    public void setGeoCode(GeoCode geoCode) {

        this.geoCode = geoCode;
    }

    @JsonProperty("note")
    @Size(min = 1, max = 128)
    public String getNote() {

        return note;
    }

    @JsonProperty("note")
    public void setNote(String note) {

        this.note = note;
    }

    @JsonProperty(required = true, value = "payee")
    @NotNull
    @Valid
    public Party getPayee() {

        return payee;
    }

    @JsonProperty(required = true, value = "payee")
    public void setPayee(Party payee) {

        this.payee = payee;
    }

    @JsonProperty(required = true, value = "payer")
    @NotNull
    @Valid
    public PartyIdInfo getPayer() {

        return payer;
    }

    @JsonProperty(required = true, value = "payer")
    public void setPayer(PartyIdInfo payer) {

        this.payer = payer;
    }

    @JsonProperty(required = true, value = "transactionRequestId")
    @NotNull
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    public String getTransactionRequestId() {

        return transactionRequestId;
    }

    @JsonProperty(required = true, value = "transactionRequestId")
    public void setTransactionRequestId(String transactionRequestId) {

        this.transactionRequestId = transactionRequestId;
    }

    @JsonProperty(required = true, value = "transactionType")
    @NotNull
    @Valid
    public TransactionType getTransactionType() {

        return transactionType;
    }

    @JsonProperty(required = true, value = "transactionType")
    public void setTransactionType(TransactionType transactionType) {

        this.transactionType = transactionType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(transactionRequestId, payee, payer, amount, transactionType, note, geoCode, authenticationType, expiration, extensionList);
    }

    /**
     * Memo assigned to transaction.
     **/
    public TransactionRequestsPostRequest note(String note) {

        this.note = note;
        return this;
    }

    /**
     **/
    public TransactionRequestsPostRequest payee(Party payee) {

        this.payee = payee;
        return this;
    }

    /**
     **/
    public TransactionRequestsPostRequest payer(PartyIdInfo payer) {

        this.payer = payer;
        return this;
    }

    @Override
    public String toString() {

        String sb = "class TransactionRequestsPostRequest {\n" +
                        "    transactionRequestId: " + toIndentedString(transactionRequestId) + "\n" +
                        "    payee: " + toIndentedString(payee) + "\n" +
                        "    payer: " + toIndentedString(payer) + "\n" +
                        "    amount: " + toIndentedString(amount) + "\n" +
                        "    transactionType: " + toIndentedString(transactionType) + "\n" +
                        "    note: " + toIndentedString(note) + "\n" +
                        "    geoCode: " + toIndentedString(geoCode) + "\n" +
                        "    authenticationType: " + toIndentedString(authenticationType) + "\n" +
                        "    expiration: " + toIndentedString(expiration) + "\n" +
                        "    extensionList: " + toIndentedString(extensionList) + "\n" +
                        "}";
        return sb;
    }

    /**
     * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
     **/
    public TransactionRequestsPostRequest transactionRequestId(String transactionRequestId) {

        this.transactionRequestId = transactionRequestId;
        return this;
    }

    /**
     **/
    public TransactionRequestsPostRequest transactionType(TransactionType transactionType) {

        this.transactionType = transactionType;
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

