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

import io.mojaloop.common.fspiop.model.core.PartyComplexName;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type PartyPersonalInfo.
 **/

@JsonTypeName("PartyPersonalInfo")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T21:01:08.672712+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class PartyPersonalInfo   {
  private PartyComplexName complexName;
  private String dateOfBirth;
  private String kycInformation;

  public PartyPersonalInfo() {
  }

  /**
   **/
  public PartyPersonalInfo complexName(PartyComplexName complexName) {
    this.complexName = complexName;
    return this;
  }

  
  @JsonProperty("complexName")
  @Valid public PartyComplexName getComplexName() {
    return complexName;
  }

  @JsonProperty("complexName")
  public void setComplexName(PartyComplexName complexName) {
    this.complexName = complexName;
  }

  /**
   * Date of Birth of the Party.
   **/
  public PartyPersonalInfo dateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  
  @JsonProperty("dateOfBirth")
   @Pattern(regexp="^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)$")public String getDateOfBirth() {
    return dateOfBirth;
  }

  @JsonProperty("dateOfBirth")
  public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  /**
   * KYC information for the party in a form mandated by an individual scheme.
   **/
  public PartyPersonalInfo kycInformation(String kycInformation) {
    this.kycInformation = kycInformation;
    return this;
  }

  
  @JsonProperty("kycInformation")
   @Size(min=1,max=2048)public String getKycInformation() {
    return kycInformation;
  }

  @JsonProperty("kycInformation")
  public void setKycInformation(String kycInformation) {
    this.kycInformation = kycInformation;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PartyPersonalInfo partyPersonalInfo = (PartyPersonalInfo) o;
    return Objects.equals(this.complexName, partyPersonalInfo.complexName) &&
        Objects.equals(this.dateOfBirth, partyPersonalInfo.dateOfBirth) &&
        Objects.equals(this.kycInformation, partyPersonalInfo.kycInformation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(complexName, dateOfBirth, kycInformation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PartyPersonalInfo {\n");
    
    sb.append("    complexName: ").append(toIndentedString(complexName)).append("\n");
    sb.append("    dateOfBirth: ").append(toIndentedString(dateOfBirth)).append("\n");
    sb.append("    kycInformation: ").append(toIndentedString(kycInformation)).append("\n");
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

