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
 * The object sent in the PUT /services/FXP callback.
 **/

@JsonTypeName("ServicesFXPPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class ServicesFXPPutResponse   {
  private @Valid List<@Size(min = 1, max = 32)String> providers = new ArrayList<>();

  public ServicesFXPPutResponse() {
  }

  @JsonCreator
  public ServicesFXPPutResponse(
    @JsonProperty(required = true, value = "providers") List< @Size(min=1,max=32)String> providers
  ) {
    this.providers = providers;
  }

  /**
   * The FSP Id(s) of the participant(s) who offer currency conversion services.
   **/
  public ServicesFXPPutResponse providers(List<@Size(min = 1, max = 32)String> providers) {
    this.providers = providers;
    return this;
  }

  
  @JsonProperty(required = true, value = "providers")
  @NotNull  @Size(min=0,max=16)public List< @Size(min=1,max=32)String> getProviders() {
    return providers;
  }

  @JsonProperty(required = true, value = "providers")
  public void setProviders(List<@Size(min = 1, max = 32)String> providers) {
    this.providers = providers;
  }

  public ServicesFXPPutResponse addProvidersItem(String providersItem) {
    if (this.providers == null) {
      this.providers = new ArrayList<>();
    }

    this.providers.add(providersItem);
    return this;
  }

  public ServicesFXPPutResponse removeProvidersItem(String providersItem) {
    if (providersItem != null && this.providers != null) {
      this.providers.remove(providersItem);
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
    ServicesFXPPutResponse servicesFXPPutResponse = (ServicesFXPPutResponse) o;
    return Objects.equals(this.providers, servicesFXPPutResponse.providers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(providers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServicesFXPPutResponse {\n");
    
    sb.append("    providers: ").append(toIndentedString(providers)).append("\n");
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

