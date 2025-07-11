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

import io.mojaloop.common.fspiop.model.core.AmountType;
import io.mojaloop.common.fspiop.model.core.ExtensionList;
import io.mojaloop.common.fspiop.model.core.Money;
import io.mojaloop.common.fspiop.model.core.Party;
import io.mojaloop.common.fspiop.model.core.TransactionType;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type IndividualQuote.
 **/

@JsonTypeName("IndividualQuote")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T21:01:08.672712+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class IndividualQuote   {
  private String quoteId;
  private String transactionId;
  private Party payee;
  private AmountType amountType;
  private Money amount;
  private Money fees;
  private TransactionType transactionType;
  private String note;
  private ExtensionList extensionList;

  public IndividualQuote() {
  }

  @JsonCreator
  public IndividualQuote(
    @JsonProperty(required = true, value = "quoteId") String quoteId,
    @JsonProperty(required = true, value = "transactionId") String transactionId,
    @JsonProperty(required = true, value = "payee") Party payee,
    @JsonProperty(required = true, value = "amountType") AmountType amountType,
    @JsonProperty(required = true, value = "amount") Money amount,
    @JsonProperty(required = true, value = "transactionType") TransactionType transactionType
  ) {
    this.quoteId = quoteId;
    this.transactionId = transactionId;
    this.payee = payee;
    this.amountType = amountType;
    this.amount = amount;
    this.transactionType = transactionType;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public IndividualQuote quoteId(String quoteId) {
    this.quoteId = quoteId;
    return this;
  }

  
  @JsonProperty(required = true, value = "quoteId")
  @NotNull  @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getQuoteId() {
    return quoteId;
  }

  @JsonProperty(required = true, value = "quoteId")
  public void setQuoteId(String quoteId) {
    this.quoteId = quoteId;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public IndividualQuote transactionId(String transactionId) {
    this.transactionId = transactionId;
    return this;
  }

  
  @JsonProperty(required = true, value = "transactionId")
  @NotNull  @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getTransactionId() {
    return transactionId;
  }

  @JsonProperty(required = true, value = "transactionId")
  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  /**
   **/
  public IndividualQuote payee(Party payee) {
    this.payee = payee;
    return this;
  }

  
  @JsonProperty(required = true, value = "payee")
  @NotNull @Valid public Party getPayee() {
    return payee;
  }

  @JsonProperty(required = true, value = "payee")
  public void setPayee(Party payee) {
    this.payee = payee;
  }

  /**
   **/
  public IndividualQuote amountType(AmountType amountType) {
    this.amountType = amountType;
    return this;
  }

  
  @JsonProperty(required = true, value = "amountType")
  @NotNull public AmountType getAmountType() {
    return amountType;
  }

  @JsonProperty(required = true, value = "amountType")
  public void setAmountType(AmountType amountType) {
    this.amountType = amountType;
  }

  /**
   **/
  public IndividualQuote amount(Money amount) {
    this.amount = amount;
    return this;
  }

  
  @JsonProperty(required = true, value = "amount")
  @NotNull @Valid public Money getAmount() {
    return amount;
  }

  @JsonProperty(required = true, value = "amount")
  public void setAmount(Money amount) {
    this.amount = amount;
  }

  /**
   **/
  public IndividualQuote fees(Money fees) {
    this.fees = fees;
    return this;
  }

  
  @JsonProperty("fees")
  @Valid public Money getFees() {
    return fees;
  }

  @JsonProperty("fees")
  public void setFees(Money fees) {
    this.fees = fees;
  }

  /**
   **/
  public IndividualQuote transactionType(TransactionType transactionType) {
    this.transactionType = transactionType;
    return this;
  }

  
  @JsonProperty(required = true, value = "transactionType")
  @NotNull @Valid public TransactionType getTransactionType() {
    return transactionType;
  }

  @JsonProperty(required = true, value = "transactionType")
  public void setTransactionType(TransactionType transactionType) {
    this.transactionType = transactionType;
  }

  /**
   * Memo assigned to transaction.
   **/
  public IndividualQuote note(String note) {
    this.note = note;
    return this;
  }

  
  @JsonProperty("note")
   @Size(min=1,max=128)public String getNote() {
    return note;
  }

  @JsonProperty("note")
  public void setNote(String note) {
    this.note = note;
  }

  /**
   **/
  public IndividualQuote extensionList(ExtensionList extensionList) {
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
    IndividualQuote individualQuote = (IndividualQuote) o;
    return Objects.equals(this.quoteId, individualQuote.quoteId) &&
        Objects.equals(this.transactionId, individualQuote.transactionId) &&
        Objects.equals(this.payee, individualQuote.payee) &&
        Objects.equals(this.amountType, individualQuote.amountType) &&
        Objects.equals(this.amount, individualQuote.amount) &&
        Objects.equals(this.fees, individualQuote.fees) &&
        Objects.equals(this.transactionType, individualQuote.transactionType) &&
        Objects.equals(this.note, individualQuote.note) &&
        Objects.equals(this.extensionList, individualQuote.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(quoteId, transactionId, payee, amountType, amount, fees, transactionType, note, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndividualQuote {\n");
    
    sb.append("    quoteId: ").append(toIndentedString(quoteId)).append("\n");
    sb.append("    transactionId: ").append(toIndentedString(transactionId)).append("\n");
    sb.append("    payee: ").append(toIndentedString(payee)).append("\n");
    sb.append("    amountType: ").append(toIndentedString(amountType)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    fees: ").append(toIndentedString(fees)).append("\n");
    sb.append("    transactionType: ").append(toIndentedString(transactionType)).append("\n");
    sb.append("    note: ").append(toIndentedString(note)).append("\n");
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

