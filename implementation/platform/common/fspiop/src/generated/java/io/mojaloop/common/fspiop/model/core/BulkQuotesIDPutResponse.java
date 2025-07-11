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
import io.mojaloop.common.fspiop.model.core.IndividualQuoteResult;
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
 * The object sent in the PUT /bulkQuotes/{ID} callback.
 **/

@JsonTypeName("BulkQuotesIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class BulkQuotesIDPutResponse   {
  private @Valid List<@Valid IndividualQuoteResult> individualQuoteResults = new ArrayList<>();
  private String expiration;
  private ExtensionList extensionList;

  public BulkQuotesIDPutResponse() {
  }

  @JsonCreator
  public BulkQuotesIDPutResponse(
    @JsonProperty(required = true, value = "expiration") String expiration
  ) {
    this.expiration = expiration;
  }

  /**
   * Fees for each individual transaction, if any of them are charged per transaction.
   **/
  public BulkQuotesIDPutResponse individualQuoteResults(List<@Valid IndividualQuoteResult> individualQuoteResults) {
    this.individualQuoteResults = individualQuoteResults;
    return this;
  }

  
  @JsonProperty("individualQuoteResults")
  @Valid  @Size(max=1000)public List<@Valid IndividualQuoteResult> getIndividualQuoteResults() {
    return individualQuoteResults;
  }

  @JsonProperty("individualQuoteResults")
  public void setIndividualQuoteResults(List<@Valid IndividualQuoteResult> individualQuoteResults) {
    this.individualQuoteResults = individualQuoteResults;
  }

  public BulkQuotesIDPutResponse addIndividualQuoteResultsItem(IndividualQuoteResult individualQuoteResultsItem) {
    if (this.individualQuoteResults == null) {
      this.individualQuoteResults = new ArrayList<>();
    }

    this.individualQuoteResults.add(individualQuoteResultsItem);
    return this;
  }

  public BulkQuotesIDPutResponse removeIndividualQuoteResultsItem(IndividualQuoteResult individualQuoteResultsItem) {
    if (individualQuoteResultsItem != null && this.individualQuoteResults != null) {
      this.individualQuoteResults.remove(individualQuoteResultsItem);
    }

    return this;
  }
  /**
   * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
   **/
  public BulkQuotesIDPutResponse expiration(String expiration) {
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
  public BulkQuotesIDPutResponse extensionList(ExtensionList extensionList) {
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
    BulkQuotesIDPutResponse bulkQuotesIDPutResponse = (BulkQuotesIDPutResponse) o;
    return Objects.equals(this.individualQuoteResults, bulkQuotesIDPutResponse.individualQuoteResults) &&
        Objects.equals(this.expiration, bulkQuotesIDPutResponse.expiration) &&
        Objects.equals(this.extensionList, bulkQuotesIDPutResponse.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(individualQuoteResults, expiration, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BulkQuotesIDPutResponse {\n");
    
    sb.append("    individualQuoteResults: ").append(toIndentedString(individualQuoteResults)).append("\n");
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

