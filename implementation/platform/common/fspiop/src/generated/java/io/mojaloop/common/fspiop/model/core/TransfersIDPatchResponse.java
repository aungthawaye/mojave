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
import io.mojaloop.common.fspiop.model.core.TransferState;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * PATCH /transfers/{ID} object
 **/

@JsonTypeName("TransfersIDPatchResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class TransfersIDPatchResponse   {
  private String completedTimestamp;
  private TransferState transferState;
  private ExtensionList extensionList;

  public TransfersIDPatchResponse() {
  }

  @JsonCreator
  public TransfersIDPatchResponse(
    @JsonProperty(required = true, value = "completedTimestamp") String completedTimestamp,
    @JsonProperty(required = true, value = "transferState") TransferState transferState
  ) {
    this.completedTimestamp = completedTimestamp;
    this.transferState = transferState;
  }

  /**
   * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
   **/
  public TransfersIDPatchResponse completedTimestamp(String completedTimestamp) {
    this.completedTimestamp = completedTimestamp;
    return this;
  }

  
  @JsonProperty(required = true, value = "completedTimestamp")
  @NotNull  @Pattern(regexp="^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:(\\.\\d{3}))(?:Z|[+-][01]\\d:[0-5]\\d)$")public String getCompletedTimestamp() {
    return completedTimestamp;
  }

  @JsonProperty(required = true, value = "completedTimestamp")
  public void setCompletedTimestamp(String completedTimestamp) {
    this.completedTimestamp = completedTimestamp;
  }

  /**
   **/
  public TransfersIDPatchResponse transferState(TransferState transferState) {
    this.transferState = transferState;
    return this;
  }

  
  @JsonProperty(required = true, value = "transferState")
  @NotNull public TransferState getTransferState() {
    return transferState;
  }

  @JsonProperty(required = true, value = "transferState")
  public void setTransferState(TransferState transferState) {
    this.transferState = transferState;
  }

  /**
   **/
  public TransfersIDPatchResponse extensionList(ExtensionList extensionList) {
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
    TransfersIDPatchResponse transfersIDPatchResponse = (TransfersIDPatchResponse) o;
    return Objects.equals(this.completedTimestamp, transfersIDPatchResponse.completedTimestamp) &&
        Objects.equals(this.transferState, transfersIDPatchResponse.transferState) &&
        Objects.equals(this.extensionList, transfersIDPatchResponse.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(completedTimestamp, transferState, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransfersIDPatchResponse {\n");
    
    sb.append("    completedTimestamp: ").append(toIndentedString(completedTimestamp)).append("\n");
    sb.append("    transferState: ").append(toIndentedString(transferState)).append("\n");
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

