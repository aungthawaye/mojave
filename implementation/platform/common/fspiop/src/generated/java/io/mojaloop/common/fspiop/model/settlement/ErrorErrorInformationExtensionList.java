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
package io.mojaloop.common.fspiop.model.settlement;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.mojaloop.common.fspiop.model.settlement.Extension;
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



@JsonTypeName("Error_errorInformation_extensionList")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T21:01:09.599695+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class ErrorErrorInformationExtensionList   {
  private @Valid List<@Valid Extension> extension = new ArrayList<>();

  public ErrorErrorInformationExtensionList() {
  }

  /**
   **/
  public ErrorErrorInformationExtensionList extension(List<@Valid Extension> extension) {
    this.extension = extension;
    return this;
  }

  
  @JsonProperty("extension")
  @Valid public List<@Valid Extension> getExtension() {
    return extension;
  }

  @JsonProperty("extension")
  public void setExtension(List<@Valid Extension> extension) {
    this.extension = extension;
  }

  public ErrorErrorInformationExtensionList addExtensionItem(Extension extensionItem) {
    if (this.extension == null) {
      this.extension = new ArrayList<>();
    }

    this.extension.add(extensionItem);
    return this;
  }

  public ErrorErrorInformationExtensionList removeExtensionItem(Extension extensionItem) {
    if (extensionItem != null && this.extension != null) {
      this.extension.remove(extensionItem);
    }

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
    ErrorErrorInformationExtensionList errorErrorInformationExtensionList = (ErrorErrorInformationExtensionList) o;
    return Objects.equals(this.extension, errorErrorInformationExtensionList.extension);
  }

  @Override
  public int hashCode() {
    return Objects.hash(extension);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorErrorInformationExtensionList {\n");
    
    sb.append("    extension: ").append(toIndentedString(extension)).append("\n");
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

