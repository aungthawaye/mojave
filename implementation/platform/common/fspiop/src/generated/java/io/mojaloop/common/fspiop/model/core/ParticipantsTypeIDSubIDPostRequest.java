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

import io.mojaloop.common.fspiop.model.core.Currency;
import io.mojaloop.common.fspiop.model.core.ExtensionList;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * The object sent in the POST /participants/{Type}/{ID}/{SubId} and /participants/{Type}/{ID} requests. An additional optional ExtensionList element has been added as part of v1.1 changes.
 **/

@JsonTypeName("ParticipantsTypeIDSubIDPostRequest")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T21:01:08.672712+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class ParticipantsTypeIDSubIDPostRequest   {
  private String fspId;
  private Currency currency;
  private ExtensionList extensionList;

  public ParticipantsTypeIDSubIDPostRequest() {
  }

  @JsonCreator
  public ParticipantsTypeIDSubIDPostRequest(
    @JsonProperty(required = true, value = "fspId") String fspId
  ) {
    this.fspId = fspId;
  }

  /**
   * FSP identifier.
   **/
  public ParticipantsTypeIDSubIDPostRequest fspId(String fspId) {
    this.fspId = fspId;
    return this;
  }

  
  @JsonProperty(required = true, value = "fspId")
  @NotNull  @Size(min=1,max=32)public String getFspId() {
    return fspId;
  }

  @JsonProperty(required = true, value = "fspId")
  public void setFspId(String fspId) {
    this.fspId = fspId;
  }

  /**
   **/
  public ParticipantsTypeIDSubIDPostRequest currency(Currency currency) {
    this.currency = currency;
    return this;
  }

  
  @JsonProperty("currency")
  public Currency getCurrency() {
    return currency;
  }

  @JsonProperty("currency")
  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  /**
   **/
  public ParticipantsTypeIDSubIDPostRequest extensionList(ExtensionList extensionList) {
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
    ParticipantsTypeIDSubIDPostRequest participantsTypeIDSubIDPostRequest = (ParticipantsTypeIDSubIDPostRequest) o;
    return Objects.equals(this.fspId, participantsTypeIDSubIDPostRequest.fspId) &&
        Objects.equals(this.currency, participantsTypeIDSubIDPostRequest.currency) &&
        Objects.equals(this.extensionList, participantsTypeIDSubIDPostRequest.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fspId, currency, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ParticipantsTypeIDSubIDPostRequest {\n");
    
    sb.append("    fspId: ").append(toIndentedString(fspId)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
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

