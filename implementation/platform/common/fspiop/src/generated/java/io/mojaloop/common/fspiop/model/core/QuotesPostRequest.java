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
import io.mojaloop.common.fspiop.model.core.CurrencyConverter;
import io.mojaloop.common.fspiop.model.core.ExtensionList;
import io.mojaloop.common.fspiop.model.core.FxRate;
import io.mojaloop.common.fspiop.model.core.GeoCode;
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
 * The object sent in the POST /quotes request.
 **/

@JsonTypeName("QuotesPostRequest")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class QuotesPostRequest   {
  private String quoteId;
  private String transactionId;
  private String transactionRequestId;
  private Party payee;
  private Party payer;
  private AmountType amountType;
  private Money amount;
  private Money fees;
  private TransactionType transactionType;
  private CurrencyConverter converter;
  private FxRate currencyConversion;
  private GeoCode geoCode;
  private String note;
  private String expiration;
  private ExtensionList extensionList;

  public QuotesPostRequest() {
  }

  @JsonCreator
  public QuotesPostRequest(
    @JsonProperty(required = true, value = "quoteId") String quoteId,
    @JsonProperty(required = true, value = "transactionId") String transactionId,
    @JsonProperty(required = true, value = "payee") Party payee,
    @JsonProperty(required = true, value = "payer") Party payer,
    @JsonProperty(required = true, value = "amountType") AmountType amountType,
    @JsonProperty(required = true, value = "amount") Money amount,
    @JsonProperty(required = true, value = "transactionType") TransactionType transactionType
  ) {
    this.quoteId = quoteId;
    this.transactionId = transactionId;
    this.payee = payee;
    this.payer = payer;
    this.amountType = amountType;
    this.amount = amount;
    this.transactionType = transactionType;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public QuotesPostRequest quoteId(String quoteId) {
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
  public QuotesPostRequest transactionId(String transactionId) {
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
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public QuotesPostRequest transactionRequestId(String transactionRequestId) {
    this.transactionRequestId = transactionRequestId;
    return this;
  }

  
  @JsonProperty("transactionRequestId")
   @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getTransactionRequestId() {
    return transactionRequestId;
  }

  @JsonProperty("transactionRequestId")
  public void setTransactionRequestId(String transactionRequestId) {
    this.transactionRequestId = transactionRequestId;
  }

  /**
   **/
  public QuotesPostRequest payee(Party payee) {
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
  public QuotesPostRequest payer(Party payer) {
    this.payer = payer;
    return this;
  }

  
  @JsonProperty(required = true, value = "payer")
  @NotNull @Valid public Party getPayer() {
    return payer;
  }

  @JsonProperty(required = true, value = "payer")
  public void setPayer(Party payer) {
    this.payer = payer;
  }

  /**
   **/
  public QuotesPostRequest amountType(AmountType amountType) {
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
  public QuotesPostRequest amount(Money amount) {
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
  public QuotesPostRequest fees(Money fees) {
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
  public QuotesPostRequest transactionType(TransactionType transactionType) {
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
   **/
  public QuotesPostRequest converter(CurrencyConverter converter) {
    this.converter = converter;
    return this;
  }

  
  @JsonProperty("converter")
  public CurrencyConverter getConverter() {
    return converter;
  }

  @JsonProperty("converter")
  public void setConverter(CurrencyConverter converter) {
    this.converter = converter;
  }

  /**
   **/
  public QuotesPostRequest currencyConversion(FxRate currencyConversion) {
    this.currencyConversion = currencyConversion;
    return this;
  }

  
  @JsonProperty("currencyConversion")
  @Valid public FxRate getCurrencyConversion() {
    return currencyConversion;
  }

  @JsonProperty("currencyConversion")
  public void setCurrencyConversion(FxRate currencyConversion) {
    this.currencyConversion = currencyConversion;
  }

  /**
   **/
  public QuotesPostRequest geoCode(GeoCode geoCode) {
    this.geoCode = geoCode;
    return this;
  }

  
  @JsonProperty("geoCode")
  @Valid public GeoCode getGeoCode() {
    return geoCode;
  }

  @JsonProperty("geoCode")
  public void setGeoCode(GeoCode geoCode) {
    this.geoCode = geoCode;
  }

  /**
   * Memo assigned to transaction.
   **/
  public QuotesPostRequest note(String note) {
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
   * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
   **/
  public QuotesPostRequest expiration(String expiration) {
    this.expiration = expiration;
    return this;
  }

  
  @JsonProperty("expiration")
   @Pattern(regexp="^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:(\\.\\d{3}))(?:Z|[+-][01]\\d:[0-5]\\d)$")public String getExpiration() {
    return expiration;
  }

  @JsonProperty("expiration")
  public void setExpiration(String expiration) {
    this.expiration = expiration;
  }

  /**
   **/
  public QuotesPostRequest extensionList(ExtensionList extensionList) {
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
    QuotesPostRequest quotesPostRequest = (QuotesPostRequest) o;
    return Objects.equals(this.quoteId, quotesPostRequest.quoteId) &&
        Objects.equals(this.transactionId, quotesPostRequest.transactionId) &&
        Objects.equals(this.transactionRequestId, quotesPostRequest.transactionRequestId) &&
        Objects.equals(this.payee, quotesPostRequest.payee) &&
        Objects.equals(this.payer, quotesPostRequest.payer) &&
        Objects.equals(this.amountType, quotesPostRequest.amountType) &&
        Objects.equals(this.amount, quotesPostRequest.amount) &&
        Objects.equals(this.fees, quotesPostRequest.fees) &&
        Objects.equals(this.transactionType, quotesPostRequest.transactionType) &&
        Objects.equals(this.converter, quotesPostRequest.converter) &&
        Objects.equals(this.currencyConversion, quotesPostRequest.currencyConversion) &&
        Objects.equals(this.geoCode, quotesPostRequest.geoCode) &&
        Objects.equals(this.note, quotesPostRequest.note) &&
        Objects.equals(this.expiration, quotesPostRequest.expiration) &&
        Objects.equals(this.extensionList, quotesPostRequest.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(quoteId, transactionId, transactionRequestId, payee, payer, amountType, amount, fees, transactionType, converter, currencyConversion, geoCode, note, expiration, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QuotesPostRequest {\n");
    
    sb.append("    quoteId: ").append(toIndentedString(quoteId)).append("\n");
    sb.append("    transactionId: ").append(toIndentedString(transactionId)).append("\n");
    sb.append("    transactionRequestId: ").append(toIndentedString(transactionRequestId)).append("\n");
    sb.append("    payee: ").append(toIndentedString(payee)).append("\n");
    sb.append("    payer: ").append(toIndentedString(payer)).append("\n");
    sb.append("    amountType: ").append(toIndentedString(amountType)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    fees: ").append(toIndentedString(fees)).append("\n");
    sb.append("    transactionType: ").append(toIndentedString(transactionType)).append("\n");
    sb.append("    converter: ").append(toIndentedString(converter)).append("\n");
    sb.append("    currencyConversion: ").append(toIndentedString(currencyConversion)).append("\n");
    sb.append("    geoCode: ").append(toIndentedString(geoCode)).append("\n");
    sb.append("    note: ").append(toIndentedString(note)).append("\n");
    sb.append("    expiration: ").append(toIndentedString(expiration)).append("\n");
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

