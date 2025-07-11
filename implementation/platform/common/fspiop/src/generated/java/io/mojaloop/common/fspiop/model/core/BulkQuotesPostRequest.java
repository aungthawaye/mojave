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
import io.mojaloop.common.fspiop.model.core.GeoCode;
import io.mojaloop.common.fspiop.model.core.IndividualQuote;
import io.mojaloop.common.fspiop.model.core.Party;
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
 * The object sent in the POST /bulkQuotes request.
 **/

@JsonTypeName("BulkQuotesPostRequest")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class BulkQuotesPostRequest   {
  private String bulkQuoteId;
  private Party payer;
  private GeoCode geoCode;
  private String expiration;
  private @Valid List<@Valid IndividualQuote> individualQuotes = new ArrayList<>();
  private ExtensionList extensionList;

  public BulkQuotesPostRequest() {
  }

  @JsonCreator
  public BulkQuotesPostRequest(
    @JsonProperty(required = true, value = "bulkQuoteId") String bulkQuoteId,
    @JsonProperty(required = true, value = "payer") Party payer,
    @JsonProperty(required = true, value = "individualQuotes") List<@Valid IndividualQuote> individualQuotes
  ) {
    this.bulkQuoteId = bulkQuoteId;
    this.payer = payer;
    this.individualQuotes = individualQuotes;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public BulkQuotesPostRequest bulkQuoteId(String bulkQuoteId) {
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
   **/
  public BulkQuotesPostRequest payer(Party payer) {
    this.payer = payer;
    return this;
  }

  
  @JsonProperty(required = true, value = "payer")
  @NotNull @Valid public Party getPayer() {
    return payer;
  }

  @JsonProperty(required = true, value = "payer")
  public void setPayer(Party payer) {
    this.payer = payer;
  }

  /**
   **/
  public BulkQuotesPostRequest geoCode(GeoCode geoCode) {
    this.geoCode = geoCode;
    return this;
  }

  
  @JsonProperty("geoCode")
  @Valid public GeoCode getGeoCode() {
    return geoCode;
  }

  @JsonProperty("geoCode")
  public void setGeoCode(GeoCode geoCode) {
    this.geoCode = geoCode;
  }

  /**
   * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
   **/
  public BulkQuotesPostRequest expiration(String expiration) {
    this.expiration = expiration;
    return this;
  }

  
  @JsonProperty("expiration")
   @Pattern(regexp="^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:(\\.\\d{3}))(?:Z|[+-][01]\\d:[0-5]\\d)$")public String getExpiration() {
    return expiration;
  }

  @JsonProperty("expiration")
  public void setExpiration(String expiration) {
    this.expiration = expiration;
  }

  /**
   * List of quotes elements.
   **/
  public BulkQuotesPostRequest individualQuotes(List<@Valid IndividualQuote> individualQuotes) {
    this.individualQuotes = individualQuotes;
    return this;
  }

  
  @JsonProperty(required = true, value = "individualQuotes")
  @NotNull @Valid  @Size(min=1,max=1000)public List<@Valid IndividualQuote> getIndividualQuotes() {
    return individualQuotes;
  }

  @JsonProperty(required = true, value = "individualQuotes")
  public void setIndividualQuotes(List<@Valid IndividualQuote> individualQuotes) {
    this.individualQuotes = individualQuotes;
  }

  public BulkQuotesPostRequest addIndividualQuotesItem(IndividualQuote individualQuotesItem) {
    if (this.individualQuotes == null) {
      this.individualQuotes = new ArrayList<>();
    }

    this.individualQuotes.add(individualQuotesItem);
    return this;
  }

  public BulkQuotesPostRequest removeIndividualQuotesItem(IndividualQuote individualQuotesItem) {
    if (individualQuotesItem != null && this.individualQuotes != null) {
      this.individualQuotes.remove(individualQuotesItem);
    }

    return this;
  }
  /**
   **/
  public BulkQuotesPostRequest extensionList(ExtensionList extensionList) {
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
    BulkQuotesPostRequest bulkQuotesPostRequest = (BulkQuotesPostRequest) o;
    return Objects.equals(this.bulkQuoteId, bulkQuotesPostRequest.bulkQuoteId) &&
        Objects.equals(this.payer, bulkQuotesPostRequest.payer) &&
        Objects.equals(this.geoCode, bulkQuotesPostRequest.geoCode) &&
        Objects.equals(this.expiration, bulkQuotesPostRequest.expiration) &&
        Objects.equals(this.individualQuotes, bulkQuotesPostRequest.individualQuotes) &&
        Objects.equals(this.extensionList, bulkQuotesPostRequest.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bulkQuoteId, payer, geoCode, expiration, individualQuotes, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BulkQuotesPostRequest {\n");
    
    sb.append("    bulkQuoteId: ").append(toIndentedString(bulkQuoteId)).append("\n");
    sb.append("    payer: ").append(toIndentedString(payer)).append("\n");
    sb.append("    geoCode: ").append(toIndentedString(geoCode)).append("\n");
    sb.append("    expiration: ").append(toIndentedString(expiration)).append("\n");
    sb.append("    individualQuotes: ").append(toIndentedString(individualQuotes)).append("\n");
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

