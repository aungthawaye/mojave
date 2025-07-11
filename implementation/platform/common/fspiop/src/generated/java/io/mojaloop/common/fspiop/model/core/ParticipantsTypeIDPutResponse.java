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

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * The object sent in the PUT /participants/{Type}/{ID}/{SubId} and /participants/{Type}/{ID} callbacks.
 **/

@JsonTypeName("ParticipantsTypeIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class ParticipantsTypeIDPutResponse   {
  private String fspId;

  public ParticipantsTypeIDPutResponse() {
  }

  /**
   * FSP identifier.
   **/
  public ParticipantsTypeIDPutResponse fspId(String fspId) {
    this.fspId = fspId;
    return this;
  }

  
  @JsonProperty("fspId")
   @Size(min=1,max=32)public String getFspId() {
    return fspId;
  }

  @JsonProperty("fspId")
  public void setFspId(String fspId) {
    this.fspId = fspId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParticipantsTypeIDPutResponse participantsTypeIDPutResponse = (ParticipantsTypeIDPutResponse) o;
    return Objects.equals(this.fspId, participantsTypeIDPutResponse.fspId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fspId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ParticipantsTypeIDPutResponse {\n");
    
    sb.append("    fspId: ").append(toIndentedString(fspId)).append("\n");
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

