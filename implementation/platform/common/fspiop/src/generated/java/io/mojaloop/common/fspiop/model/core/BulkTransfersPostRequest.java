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
package io.mojaloop.common.fspiop.model.core;

import io.mojaloop.common.fspiop.model.core.ExtensionList;
import io.mojaloop.common.fspiop.model.core.IndividualTransfer;
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

/**
 * The object sent in the POST /bulkTransfers request.
 **/

@JsonTypeName("BulkTransfersPostRequest")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class BulkTransfersPostRequest   {
  private String bulkTransferId;
  private String bulkQuoteId;
  private String payerFsp;
  private String payeeFsp;
  private @Valid List<@Valid IndividualTransfer> individualTransfers = new ArrayList<>();
  private String expiration;
  private ExtensionList extensionList;

  public BulkTransfersPostRequest() {
  }

  @JsonCreator
  public BulkTransfersPostRequest(
    @JsonProperty(required = true, value = "bulkTransferId") String bulkTransferId,
    @JsonProperty(required = true, value = "bulkQuoteId") String bulkQuoteId,
    @JsonProperty(required = true, value = "payerFsp") String payerFsp,
    @JsonProperty(required = true, value = "payeeFsp") String payeeFsp,
    @JsonProperty(required = true, value = "individualTransfers") List<@Valid IndividualTransfer> individualTransfers,
    @JsonProperty(required = true, value = "expiration") String expiration
  ) {
    this.bulkTransferId = bulkTransferId;
    this.bulkQuoteId = bulkQuoteId;
    this.payerFsp = payerFsp;
    this.payeeFsp = payeeFsp;
    this.individualTransfers = individualTransfers;
    this.expiration = expiration;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public BulkTransfersPostRequest bulkTransferId(String bulkTransferId) {
    this.bulkTransferId = bulkTransferId;
    return this;
  }

  
  @JsonProperty(required = true, value = "bulkTransferId")
  @NotNull  @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getBulkTransferId() {
    return bulkTransferId;
  }

  @JsonProperty(required = true, value = "bulkTransferId")
  public void setBulkTransferId(String bulkTransferId) {
    this.bulkTransferId = bulkTransferId;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public BulkTransfersPostRequest bulkQuoteId(String bulkQuoteId) {
    this.bulkQuoteId = bulkQuoteId;
    return this;
  }

  
  @JsonProperty(required = true, value = "bulkQuoteId")
  @NotNull  @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getBulkQuoteId() {
    return bulkQuoteId;
  }

  @JsonProperty(required = true, value = "bulkQuoteId")
  public void setBulkQuoteId(String bulkQuoteId) {
    this.bulkQuoteId = bulkQuoteId;
  }

  /**
   * FSP identifier.
   **/
  public BulkTransfersPostRequest payerFsp(String payerFsp) {
    this.payerFsp = payerFsp;
    return this;
  }

  
  @JsonProperty(required = true, value = "payerFsp")
  @NotNull  @Size(min=1,max=32)public String getPayerFsp() {
    return payerFsp;
  }

  @JsonProperty(required = true, value = "payerFsp")
  public void setPayerFsp(String payerFsp) {
    this.payerFsp = payerFsp;
  }

  /**
   * FSP identifier.
   **/
  public BulkTransfersPostRequest payeeFsp(String payeeFsp) {
    this.payeeFsp = payeeFsp;
    return this;
  }

  
  @JsonProperty(required = true, value = "payeeFsp")
  @NotNull  @Size(min=1,max=32)public String getPayeeFsp() {
    return payeeFsp;
  }

  @JsonProperty(required = true, value = "payeeFsp")
  public void setPayeeFsp(String payeeFsp) {
    this.payeeFsp = payeeFsp;
  }

  /**
   * List of IndividualTransfer elements.
   **/
  public BulkTransfersPostRequest individualTransfers(List<@Valid IndividualTransfer> individualTransfers) {
    this.individualTransfers = individualTransfers;
    return this;
  }

  
  @JsonProperty(required = true, value = "individualTransfers")
  @NotNull @Valid  @Size(min=1,max=1000)public List<@Valid IndividualTransfer> getIndividualTransfers() {
    return individualTransfers;
  }

  @JsonProperty(required = true, value = "individualTransfers")
  public void setIndividualTransfers(List<@Valid IndividualTransfer> individualTransfers) {
    this.individualTransfers = individualTransfers;
  }

  public BulkTransfersPostRequest addIndividualTransfersItem(IndividualTransfer individualTransfersItem) {
    if (this.individualTransfers == null) {
      this.individualTransfers = new ArrayList<>();
    }

    this.individualTransfers.add(individualTransfersItem);
    return this;
  }

  public BulkTransfersPostRequest removeIndividualTransfersItem(IndividualTransfer individualTransfersItem) {
    if (individualTransfersItem != null && this.individualTransfers != null) {
      this.individualTransfers.remove(individualTransfersItem);
    }

    return this;
  }
  /**
   * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
   **/
  public BulkTransfersPostRequest expiration(String expiration) {
    this.expiration = expiration;
    return this;
  }

  
  @JsonProperty(required = true, value = "expiration")
  @NotNull  @Pattern(regexp="^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:(\\.\\d{3}))(?:Z|[+-][01]\\d:[0-5]\\d)$")public String getExpiration() {
    return expiration;
  }

  @JsonProperty(required = true, value = "expiration")
  public void setExpiration(String expiration) {
    this.expiration = expiration;
  }

  /**
   **/
  public BulkTransfersPostRequest extensionList(ExtensionList extensionList) {
    this.extensionList = extensionList;
    return this;
  }

  
  @JsonProperty("extensionList")
  @Valid public ExtensionList getExtensionList() {
    return extensionList;
  }

  @JsonProperty("extensionList")
  public void setExtensionList(ExtensionList extensionList) {
    this.extensionList = extensionList;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BulkTransfersPostRequest bulkTransfersPostRequest = (BulkTransfersPostRequest) o;
    return Objects.equals(this.bulkTransferId, bulkTransfersPostRequest.bulkTransferId) &&
        Objects.equals(this.bulkQuoteId, bulkTransfersPostRequest.bulkQuoteId) &&
        Objects.equals(this.payerFsp, bulkTransfersPostRequest.payerFsp) &&
        Objects.equals(this.payeeFsp, bulkTransfersPostRequest.payeeFsp) &&
        Objects.equals(this.individualTransfers, bulkTransfersPostRequest.individualTransfers) &&
        Objects.equals(this.expiration, bulkTransfersPostRequest.expiration) &&
        Objects.equals(this.extensionList, bulkTransfersPostRequest.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bulkTransferId, bulkQuoteId, payerFsp, payeeFsp, individualTransfers, expiration, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BulkTransfersPostRequest {\n");
    
    sb.append("    bulkTransferId: ").append(toIndentedString(bulkTransferId)).append("\n");
    sb.append("    bulkQuoteId: ").append(toIndentedString(bulkQuoteId)).append("\n");
    sb.append("    payerFsp: ").append(toIndentedString(payerFsp)).append("\n");
    sb.append("    payeeFsp: ").append(toIndentedString(payeeFsp)).append("\n");
    sb.append("    individualTransfers: ").append(toIndentedString(individualTransfers)).append("\n");
    sb.append("    expiration: ").append(toIndentedString(expiration)).append("\n");
    sb.append("    extensionList: ").append(toIndentedString(extensionList)).append("\n");
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

