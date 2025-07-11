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
 * The object sent in the PUT /fxQuotes/{ID} callback.
 **/

@JsonTypeName("FxQuotesIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class FxQuotesIDPutResponse   {
  private String condition;
  private FxConversion conversionTerms;

  public FxQuotesIDPutResponse() {
  }

  @JsonCreator
  public FxQuotesIDPutResponse(
    @JsonProperty(required = true, value = "conversionTerms") FxConversion conversionTerms
  ) {
    this.conversionTerms = conversionTerms;
  }

  /**
   * Condition that must be attached to the transfer by the Payer.
   **/
  public FxQuotesIDPutResponse condition(String condition) {
    this.condition = condition;
    return this;
  }

  
  @JsonProperty("condition")
   @Pattern(regexp="^[A-Za-z0-9-_]{43}$") @Size(max=48)public String getCondition() {
    return condition;
  }

  @JsonProperty("condition")
  public void setCondition(String condition) {
    this.condition = condition;
  }

  /**
   **/
  public FxQuotesIDPutResponse conversionTerms(FxConversion conversionTerms) {
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
    FxQuotesIDPutResponse fxQuotesIDPutResponse = (FxQuotesIDPutResponse) o;
    return Objects.equals(this.condition, fxQuotesIDPutResponse.condition) &&
        Objects.equals(this.conversionTerms, fxQuotesIDPutResponse.conversionTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(condition, conversionTerms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FxQuotesIDPutResponse {\n");
    
    sb.append("    condition: ").append(toIndentedString(condition)).append("\n");
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

