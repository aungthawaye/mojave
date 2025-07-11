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
import io.mojaloop.common.fspiop.model.core.Money;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type IndividualTransfer.
 **/

@JsonTypeName("IndividualTransfer")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T21:01:08.672712+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class IndividualTransfer   {
  private String transferId;
  private Money transferAmount;
  private String ilpPacket;
  private String condition;
  private ExtensionList extensionList;

  public IndividualTransfer() {
  }

  @JsonCreator
  public IndividualTransfer(
    @JsonProperty(required = true, value = "transferId") String transferId,
    @JsonProperty(required = true, value = "transferAmount") Money transferAmount,
    @JsonProperty(required = true, value = "ilpPacket") String ilpPacket,
    @JsonProperty(required = true, value = "condition") String condition
  ) {
    this.transferId = transferId;
    this.transferAmount = transferAmount;
    this.ilpPacket = ilpPacket;
    this.condition = condition;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public IndividualTransfer transferId(String transferId) {
    this.transferId = transferId;
    return this;
  }

  
  @JsonProperty(required = true, value = "transferId")
  @NotNull  @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getTransferId() {
    return transferId;
  }

  @JsonProperty(required = true, value = "transferId")
  public void setTransferId(String transferId) {
    this.transferId = transferId;
  }

  /**
   **/
  public IndividualTransfer transferAmount(Money transferAmount) {
    this.transferAmount = transferAmount;
    return this;
  }

  
  @JsonProperty(required = true, value = "transferAmount")
  @NotNull @Valid public Money getTransferAmount() {
    return transferAmount;
  }

  @JsonProperty(required = true, value = "transferAmount")
  public void setTransferAmount(Money transferAmount) {
    this.transferAmount = transferAmount;
  }

  /**
   * Information for recipient (transport layer information).
   **/
  public IndividualTransfer ilpPacket(String ilpPacket) {
    this.ilpPacket = ilpPacket;
    return this;
  }

  
  @JsonProperty(required = true, value = "ilpPacket")
  @NotNull  @Pattern(regexp="^[A-Za-z0-9-_]+[=]{0,2}$") @Size(min=1,max=32768)public String getIlpPacket() {
    return ilpPacket;
  }

  @JsonProperty(required = true, value = "ilpPacket")
  public void setIlpPacket(String ilpPacket) {
    this.ilpPacket = ilpPacket;
  }

  /**
   * Condition that must be attached to the transfer by the Payer.
   **/
  public IndividualTransfer condition(String condition) {
    this.condition = condition;
    return this;
  }

  
  @JsonProperty(required = true, value = "condition")
  @NotNull  @Pattern(regexp="^[A-Za-z0-9-_]{43}$") @Size(max=48)public String getCondition() {
    return condition;
  }

  @JsonProperty(required = true, value = "condition")
  public void setCondition(String condition) {
    this.condition = condition;
  }

  /**
   **/
  public IndividualTransfer extensionList(ExtensionList extensionList) {
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
    IndividualTransfer individualTransfer = (IndividualTransfer) o;
    return Objects.equals(this.transferId, individualTransfer.transferId) &&
        Objects.equals(this.transferAmount, individualTransfer.transferAmount) &&
        Objects.equals(this.ilpPacket, individualTransfer.ilpPacket) &&
        Objects.equals(this.condition, individualTransfer.condition) &&
        Objects.equals(this.extensionList, individualTransfer.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transferId, transferAmount, ilpPacket, condition, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndividualTransfer {\n");
    
    sb.append("    transferId: ").append(toIndentedString(transferId)).append("\n");
    sb.append("    transferAmount: ").append(toIndentedString(transferAmount)).append("\n");
    sb.append("    ilpPacket: ").append(toIndentedString(ilpPacket)).append("\n");
    sb.append("    condition: ").append(toIndentedString(condition)).append("\n");
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

