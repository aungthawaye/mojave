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
 * The object sent in the PUT /quotes/{ID} callback.
 **/

@JsonTypeName("QuotesIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.13.0")
public class QuotesIDPutResponse {

    private Money transferAmount;

    private Money payeeReceiveAmount;

    private Money payeeFspFee;

    private Money payeeFspCommission;

    private String expiration;

    private GeoCode geoCode;

    private String ilpPacket;

    private String condition;

    private ExtensionList extensionList;

    public QuotesIDPutResponse() {

    }

    @JsonCreator
    public QuotesIDPutResponse(
        @JsonProperty(required = true, value = "transferAmount") Money transferAmount,
        @JsonProperty(required = true, value = "expiration") String expiration,
        @JsonProperty(required = true, value = "ilpPacket") String ilpPacket,
        @JsonProperty(required = true, value = "condition") String condition
                              ) {

        this.transferAmount = transferAmount;
        this.expiration = expiration;
        this.ilpPacket = ilpPacket;
        this.condition = condition;
    }

    /**
     * Condition that must be attached to the transfer by the Payer.
     **/
    public QuotesIDPutResponse condition(String condition) {

        this.condition = condition;
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
        QuotesIDPutResponse quotesIDPutResponse = (QuotesIDPutResponse) o;
        return Objects.equals(this.transferAmount, quotesIDPutResponse.transferAmount) &&
                   Objects.equals(this.payeeReceiveAmount, quotesIDPutResponse.payeeReceiveAmount) &&
                   Objects.equals(this.payeeFspFee, quotesIDPutResponse.payeeFspFee) &&
                   Objects.equals(this.payeeFspCommission, quotesIDPutResponse.payeeFspCommission) &&
                   Objects.equals(this.expiration, quotesIDPutResponse.expiration) &&
                   Objects.equals(this.geoCode, quotesIDPutResponse.geoCode) &&
                   Objects.equals(this.ilpPacket, quotesIDPutResponse.ilpPacket) &&
                   Objects.equals(this.condition, quotesIDPutResponse.condition) &&
                   Objects.equals(this.extensionList, quotesIDPutResponse.extensionList);
    }

    /**
     * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
     **/
    public QuotesIDPutResponse expiration(String expiration) {

        this.expiration = expiration;
        return this;
    }

    /**
     **/
    public QuotesIDPutResponse extensionList(ExtensionList extensionList) {

        this.extensionList = extensionList;
        return this;
    }

    /**
     **/
    public QuotesIDPutResponse geoCode(GeoCode geoCode) {

        this.geoCode = geoCode;
        return this;
    }

    @JsonProperty(required = true, value = "condition")
    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9-_]{43}$")
    @Size(max = 48)
    public String getCondition() {

        return condition;
    }

    @JsonProperty(required = true, value = "condition")
    public void setCondition(String condition) {

        this.condition = condition;
    }

    @JsonProperty(required = true, value = "expiration")
    @NotNull
    @Pattern(
        regexp = "^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:(\\.\\d{3}))(?:Z|[+-][01]\\d:[0-5]\\d)$")
    public String getExpiration() {

        return expiration;
    }

    @JsonProperty(required = true, value = "expiration")
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

    @JsonProperty(required = true, value = "ilpPacket")
    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9-_]+[=]{0,2}$")
    @Size(min = 1, max = 32768)
    public String getIlpPacket() {

        return ilpPacket;
    }

    @JsonProperty(required = true, value = "ilpPacket")
    public void setIlpPacket(String ilpPacket) {

        this.ilpPacket = ilpPacket;
    }

    @JsonProperty("payeeFspCommission")
    @Valid
    public Money getPayeeFspCommission() {

        return payeeFspCommission;
    }

    @JsonProperty("payeeFspCommission")
    public void setPayeeFspCommission(Money payeeFspCommission) {

        this.payeeFspCommission = payeeFspCommission;
    }

    @JsonProperty("payeeFspFee")
    @Valid
    public Money getPayeeFspFee() {

        return payeeFspFee;
    }

    @JsonProperty("payeeFspFee")
    public void setPayeeFspFee(Money payeeFspFee) {

        this.payeeFspFee = payeeFspFee;
    }

    @JsonProperty("payeeReceiveAmount")
    @Valid
    public Money getPayeeReceiveAmount() {

        return payeeReceiveAmount;
    }

    @JsonProperty("payeeReceiveAmount")
    public void setPayeeReceiveAmount(Money payeeReceiveAmount) {

        this.payeeReceiveAmount = payeeReceiveAmount;
    }

    @JsonProperty(required = true, value = "transferAmount")
    @NotNull
    @Valid
    public Money getTransferAmount() {

        return transferAmount;
    }

    @JsonProperty(required = true, value = "transferAmount")
    public void setTransferAmount(Money transferAmount) {

        this.transferAmount = transferAmount;
    }

    @Override
    public int hashCode() {

        return Objects.hash(transferAmount, payeeReceiveAmount, payeeFspFee, payeeFspCommission, expiration, geoCode, ilpPacket, condition, extensionList);
    }

    /**
     * Information for recipient (transport layer information).
     **/
    public QuotesIDPutResponse ilpPacket(String ilpPacket) {

        this.ilpPacket = ilpPacket;
        return this;
    }

    /**
     **/
    public QuotesIDPutResponse payeeFspCommission(Money payeeFspCommission) {

        this.payeeFspCommission = payeeFspCommission;
        return this;
    }

    /**
     **/
    public QuotesIDPutResponse payeeFspFee(Money payeeFspFee) {

        this.payeeFspFee = payeeFspFee;
        return this;
    }

    /**
     **/
    public QuotesIDPutResponse payeeReceiveAmount(Money payeeReceiveAmount) {

        this.payeeReceiveAmount = payeeReceiveAmount;
        return this;
    }

    @Override
    public String toString() {

        String sb = "class QuotesIDPutResponse {\n" +
                        "    transferAmount: " + toIndentedString(transferAmount) + "\n" +
                        "    payeeReceiveAmount: " + toIndentedString(payeeReceiveAmount) + "\n" +
                        "    payeeFspFee: " + toIndentedString(payeeFspFee) + "\n" +
                        "    payeeFspCommission: " + toIndentedString(payeeFspCommission) + "\n" +
                        "    expiration: " + toIndentedString(expiration) + "\n" +
                        "    geoCode: " + toIndentedString(geoCode) + "\n" +
                        "    ilpPacket: " + toIndentedString(ilpPacket) + "\n" +
                        "    condition: " + toIndentedString(condition) + "\n" +
                        "    extensionList: " + toIndentedString(extensionList) + "\n" +
                        "}";
        return sb;
    }

    /**
     **/
    public QuotesIDPutResponse transferAmount(Money transferAmount) {

        this.transferAmount = transferAmount;
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

