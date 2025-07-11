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
import io.mojaloop.common.fspiop.model.core.TransactionState;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * The object sent in the PUT /transactions/{ID} callback.
 **/

@JsonTypeName("TransactionsIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class TransactionsIDPutResponse   {
  private String completedTimestamp;
  private TransactionState transactionState;
  private String code;
  private ExtensionList extensionList;

  public TransactionsIDPutResponse() {
  }

  @JsonCreator
  public TransactionsIDPutResponse(
    @JsonProperty(required = true, value = "transactionState") TransactionState transactionState
  ) {
    this.transactionState = transactionState;
  }

  /**
   * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
   **/
  public TransactionsIDPutResponse completedTimestamp(String completedTimestamp) {
    this.completedTimestamp = completedTimestamp;
    return this;
  }

  
  @JsonProperty("completedTimestamp")
   @Pattern(regexp="^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:(\\.\\d{3}))(?:Z|[+-][01]\\d:[0-5]\\d)$")public String getCompletedTimestamp() {
    return completedTimestamp;
  }

  @JsonProperty("completedTimestamp")
  public void setCompletedTimestamp(String completedTimestamp) {
    this.completedTimestamp = completedTimestamp;
  }

  /**
   **/
  public TransactionsIDPutResponse transactionState(TransactionState transactionState) {
    this.transactionState = transactionState;
    return this;
  }

  
  @JsonProperty(required = true, value = "transactionState")
  @NotNull public TransactionState getTransactionState() {
    return transactionState;
  }

  @JsonProperty(required = true, value = "transactionState")
  public void setTransactionState(TransactionState transactionState) {
    this.transactionState = transactionState;
  }

  /**
   * Any code/token returned by the Payee FSP (TokenCode Type).
   **/
  public TransactionsIDPutResponse code(String code) {
    this.code = code;
    return this;
  }

  
  @JsonProperty("code")
   @Pattern(regexp="^[0-9a-zA-Z]{4,32}$")public String getCode() {
    return code;
  }

  @JsonProperty("code")
  public void setCode(String code) {
    this.code = code;
  }

  /**
   **/
  public TransactionsIDPutResponse extensionList(ExtensionList extensionList) {
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
    TransactionsIDPutResponse transactionsIDPutResponse = (TransactionsIDPutResponse) o;
    return Objects.equals(this.completedTimestamp, transactionsIDPutResponse.completedTimestamp) &&
        Objects.equals(this.transactionState, transactionsIDPutResponse.transactionState) &&
        Objects.equals(this.code, transactionsIDPutResponse.code) &&
        Objects.equals(this.extensionList, transactionsIDPutResponse.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(completedTimestamp, transactionState, code, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransactionsIDPutResponse {\n");
    
    sb.append("    completedTimestamp: ").append(toIndentedString(completedTimestamp)).append("\n");
    sb.append("    transactionState: ").append(toIndentedString(transactionState)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
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

