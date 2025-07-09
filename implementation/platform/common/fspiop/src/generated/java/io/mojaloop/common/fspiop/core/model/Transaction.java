package io.mojaloop.common.fspiop.core.model;

import io.mojaloop.common.fspiop.core.model.CurrencyConverter;
import io.mojaloop.common.fspiop.core.model.ExtensionList;
import io.mojaloop.common.fspiop.core.model.FxRate;
import io.mojaloop.common.fspiop.core.model.Money;
import io.mojaloop.common.fspiop.core.model.Party;
import io.mojaloop.common.fspiop.core.model.TransactionType;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type Transaction. The Transaction type is used to carry end-to-end data between the Payer FSP and the Payee FSP in the ILP Packet. Both the transactionId and the quoteId in the data model are decided by the Payer FSP in the POST /quotes request.
 **/

@JsonTypeName("Transaction")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-09T06:35:16.332603+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class Transaction   {
  private String transactionId;
  private String quoteId;
  private Party payee;
  private Party payer;
  private Money amount;
  private Money payeeReceiveAmount;
  private CurrencyConverter converter;
  private FxRate currencyConversion;
  private TransactionType transactionType;
  private String note;
  private ExtensionList extensionList;

  public Transaction() {
  }

  @JsonCreator
  public Transaction(
    @JsonProperty(required = true, value = "transactionId") String transactionId,
    @JsonProperty(required = true, value = "quoteId") String quoteId,
    @JsonProperty(required = true, value = "payee") Party payee,
    @JsonProperty(required = true, value = "payer") Party payer,
    @JsonProperty(required = true, value = "amount") Money amount,
    @JsonProperty(required = true, value = "transactionType") TransactionType transactionType
  ) {
    this.transactionId = transactionId;
    this.quoteId = quoteId;
    this.payee = payee;
    this.payer = payer;
    this.amount = amount;
    this.transactionType = transactionType;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public Transaction transactionId(String transactionId) {
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
  public Transaction quoteId(String quoteId) {
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
   **/
  public Transaction payee(Party payee) {
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
  public Transaction payer(Party payer) {
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
  public Transaction amount(Money amount) {
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
  public Transaction payeeReceiveAmount(Money payeeReceiveAmount) {
    this.payeeReceiveAmount = payeeReceiveAmount;
    return this;
  }

  
  @JsonProperty("payeeReceiveAmount")
  @Valid public Money getPayeeReceiveAmount() {
    return payeeReceiveAmount;
  }

  @JsonProperty("payeeReceiveAmount")
  public void setPayeeReceiveAmount(Money payeeReceiveAmount) {
    this.payeeReceiveAmount = payeeReceiveAmount;
  }

  /**
   **/
  public Transaction converter(CurrencyConverter converter) {
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
  public Transaction currencyConversion(FxRate currencyConversion) {
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
  public Transaction transactionType(TransactionType transactionType) {
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
  public Transaction note(String note) {
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
  public Transaction extensionList(ExtensionList extensionList) {
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
    Transaction transaction = (Transaction) o;
    return Objects.equals(this.transactionId, transaction.transactionId) &&
        Objects.equals(this.quoteId, transaction.quoteId) &&
        Objects.equals(this.payee, transaction.payee) &&
        Objects.equals(this.payer, transaction.payer) &&
        Objects.equals(this.amount, transaction.amount) &&
        Objects.equals(this.payeeReceiveAmount, transaction.payeeReceiveAmount) &&
        Objects.equals(this.converter, transaction.converter) &&
        Objects.equals(this.currencyConversion, transaction.currencyConversion) &&
        Objects.equals(this.transactionType, transaction.transactionType) &&
        Objects.equals(this.note, transaction.note) &&
        Objects.equals(this.extensionList, transaction.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactionId, quoteId, payee, payer, amount, payeeReceiveAmount, converter, currencyConversion, transactionType, note, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Transaction {\n");
    
    sb.append("    transactionId: ").append(toIndentedString(transactionId)).append("\n");
    sb.append("    quoteId: ").append(toIndentedString(quoteId)).append("\n");
    sb.append("    payee: ").append(toIndentedString(payee)).append("\n");
    sb.append("    payer: ").append(toIndentedString(payer)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    payeeReceiveAmount: ").append(toIndentedString(payeeReceiveAmount)).append("\n");
    sb.append("    converter: ").append(toIndentedString(converter)).append("\n");
    sb.append("    currencyConversion: ").append(toIndentedString(currencyConversion)).append("\n");
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

