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

import io.mojaloop.common.fspiop.model.core.FxConversion;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * The object sent in the POST /fxQuotes request.
 **/

@JsonTypeName("FxQuotesPostRequest")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class FxQuotesPostRequest   {
  private String conversionRequestId;
  private FxConversion conversionTerms;

  public FxQuotesPostRequest() {
  }

  @JsonCreator
  public FxQuotesPostRequest(
    @JsonProperty(required = true, value = "conversionRequestId") String conversionRequestId,
    @JsonProperty(required = true, value = "conversionTerms") FxConversion conversionTerms
  ) {
    this.conversionRequestId = conversionRequestId;
    this.conversionTerms = conversionTerms;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public FxQuotesPostRequest conversionRequestId(String conversionRequestId) {
    this.conversionRequestId = conversionRequestId;
    return this;
  }

  
  @JsonProperty(required = true, value = "conversionRequestId")
  @NotNull  @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getConversionRequestId() {
    return conversionRequestId;
  }

  @JsonProperty(required = true, value = "conversionRequestId")
  public void setConversionRequestId(String conversionRequestId) {
    this.conversionRequestId = conversionRequestId;
  }

  /**
   **/
  public FxQuotesPostRequest conversionTerms(FxConversion conversionTerms) {
    this.conversionTerms = conversionTerms;
    return this;
  }

  
  @JsonProperty(required = true, value = "conversionTerms")
  @NotNull @Valid public FxConversion getConversionTerms() {
    return conversionTerms;
  }

  @JsonProperty(required = true, value = "conversionTerms")
  public void setConversionTerms(FxConversion conversionTerms) {
    this.conversionTerms = conversionTerms;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FxQuotesPostRequest fxQuotesPostRequest = (FxQuotesPostRequest) o;
    return Objects.equals(this.conversionRequestId, fxQuotesPostRequest.conversionRequestId) &&
        Objects.equals(this.conversionTerms, fxQuotesPostRequest.conversionTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(conversionRequestId, conversionTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FxQuotesPostRequest {\n");
    
    sb.append("    conversionRequestId: ").append(toIndentedString(conversionRequestId)).append("\n");
    sb.append("    conversionTerms: ").append(toIndentedString(conversionTerms)).append("\n");
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

