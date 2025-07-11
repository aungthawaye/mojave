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

import io.mojaloop.common.fspiop.model.core.Refund;
import io.mojaloop.common.fspiop.model.core.TransactionInitiator;
import io.mojaloop.common.fspiop.model.core.TransactionInitiatorType;
import io.mojaloop.common.fspiop.model.core.TransactionScenario;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type TransactionType.
 **/

@JsonTypeName("TransactionType")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T21:01:08.672712+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class TransactionType   {
  private TransactionScenario scenario;
  private String subScenario;
  private TransactionInitiator initiator;
  private TransactionInitiatorType initiatorType;
  private Refund refundInfo;
  private String balanceOfPayments;

  public TransactionType() {
  }

  @JsonCreator
  public TransactionType(
    @JsonProperty(required = true, value = "scenario") TransactionScenario scenario,
    @JsonProperty(required = true, value = "initiator") TransactionInitiator initiator,
    @JsonProperty(required = true, value = "initiatorType") TransactionInitiatorType initiatorType
  ) {
    this.scenario = scenario;
    this.initiator = initiator;
    this.initiatorType = initiatorType;
  }

  /**
   **/
  public TransactionType scenario(TransactionScenario scenario) {
    this.scenario = scenario;
    return this;
  }

  
  @JsonProperty(required = true, value = "scenario")
  @NotNull public TransactionScenario getScenario() {
    return scenario;
  }

  @JsonProperty(required = true, value = "scenario")
  public void setScenario(TransactionScenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Possible sub-scenario, defined locally within the scheme (UndefinedEnum Type).
   **/
  public TransactionType subScenario(String subScenario) {
    this.subScenario = subScenario;
    return this;
  }

  
  @JsonProperty("subScenario")
   @Pattern(regexp="^[A-Z_]{1,32}$")public String getSubScenario() {
    return subScenario;
  }

  @JsonProperty("subScenario")
  public void setSubScenario(String subScenario) {
    this.subScenario = subScenario;
  }

  /**
   **/
  public TransactionType initiator(TransactionInitiator initiator) {
    this.initiator = initiator;
    return this;
  }

  
  @JsonProperty(required = true, value = "initiator")
  @NotNull public TransactionInitiator getInitiator() {
    return initiator;
  }

  @JsonProperty(required = true, value = "initiator")
  public void setInitiator(TransactionInitiator initiator) {
    this.initiator = initiator;
  }

  /**
   **/
  public TransactionType initiatorType(TransactionInitiatorType initiatorType) {
    this.initiatorType = initiatorType;
    return this;
  }

  
  @JsonProperty(required = true, value = "initiatorType")
  @NotNull public TransactionInitiatorType getInitiatorType() {
    return initiatorType;
  }

  @JsonProperty(required = true, value = "initiatorType")
  public void setInitiatorType(TransactionInitiatorType initiatorType) {
    this.initiatorType = initiatorType;
  }

  /**
   **/
  public TransactionType refundInfo(Refund refundInfo) {
    this.refundInfo = refundInfo;
    return this;
  }

  
  @JsonProperty("refundInfo")
  @Valid public Refund getRefundInfo() {
    return refundInfo;
  }

  @JsonProperty("refundInfo")
  public void setRefundInfo(Refund refundInfo) {
    this.refundInfo = refundInfo;
  }

  /**
   * (BopCode) The API data type [BopCode](https://www.imf.org/external/np/sta/bopcode/) is a JSON String of 3 characters, consisting of digits only. Negative numbers are not allowed. A leading zero is not allowed.
   **/
  public TransactionType balanceOfPayments(String balanceOfPayments) {
    this.balanceOfPayments = balanceOfPayments;
    return this;
  }

  
  @JsonProperty("balanceOfPayments")
   @Pattern(regexp="^[1-9]\\d{2}$")public String getBalanceOfPayments() {
    return balanceOfPayments;
  }

  @JsonProperty("balanceOfPayments")
  public void setBalanceOfPayments(String balanceOfPayments) {
    this.balanceOfPayments = balanceOfPayments;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransactionType transactionType = (TransactionType) o;
    return Objects.equals(this.scenario, transactionType.scenario) &&
        Objects.equals(this.subScenario, transactionType.subScenario) &&
        Objects.equals(this.initiator, transactionType.initiator) &&
        Objects.equals(this.initiatorType, transactionType.initiatorType) &&
        Objects.equals(this.refundInfo, transactionType.refundInfo) &&
        Objects.equals(this.balanceOfPayments, transactionType.balanceOfPayments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scenario, subScenario, initiator, initiatorType, refundInfo, balanceOfPayments);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransactionType {\n");
    
    sb.append("    scenario: ").append(toIndentedString(scenario)).append("\n");
    sb.append("    subScenario: ").append(toIndentedString(subScenario)).append("\n");
    sb.append("    initiator: ").append(toIndentedString(initiator)).append("\n");
    sb.append("    initiatorType: ").append(toIndentedString(initiatorType)).append("\n");
    sb.append("    refundInfo: ").append(toIndentedString(refundInfo)).append("\n");
    sb.append("    balanceOfPayments: ").append(toIndentedString(balanceOfPayments)).append("\n");
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

