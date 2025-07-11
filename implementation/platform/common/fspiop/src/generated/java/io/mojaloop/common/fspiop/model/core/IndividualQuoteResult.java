package io.mojaloop.common.fspiop.model.core;

import io.mojaloop.common.fspiop.model.core.ErrorInformation;
import io.mojaloop.common.fspiop.model.core.ExtensionList;
import io.mojaloop.common.fspiop.model.core.Money;
import io.mojaloop.common.fspiop.model.core.Party;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type IndividualQuoteResult.
 **/

@JsonTypeName("IndividualQuoteResult")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class IndividualQuoteResult   {
  private String quoteId;
  private Party payee;
  private Money transferAmount;
  private Money payeeReceiveAmount;
  private Money payeeFspFee;
  private Money payeeFspCommission;
  private String ilpPacket;
  private String condition;
  private ErrorInformation errorInformation;
  private ExtensionList extensionList;

  public IndividualQuoteResult() {
  }

  @JsonCreator
  public IndividualQuoteResult(
    @JsonProperty(required = true, value = "quoteId") String quoteId
  ) {
    this.quoteId = quoteId;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public IndividualQuoteResult quoteId(String quoteId) {
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
  public IndividualQuoteResult payee(Party payee) {
    this.payee = payee;
    return this;
  }

  
  @JsonProperty("payee")
  @Valid public Party getPayee() {
    return payee;
  }

  @JsonProperty("payee")
  public void setPayee(Party payee) {
    this.payee = payee;
  }

  /**
   **/
  public IndividualQuoteResult transferAmount(Money transferAmount) {
    this.transferAmount = transferAmount;
    return this;
  }

  
  @JsonProperty("transferAmount")
  @Valid public Money getTransferAmount() {
    return transferAmount;
  }

  @JsonProperty("transferAmount")
  public void setTransferAmount(Money transferAmount) {
    this.transferAmount = transferAmount;
  }

  /**
   **/
  public IndividualQuoteResult payeeReceiveAmount(Money payeeReceiveAmount) {
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
  public IndividualQuoteResult payeeFspFee(Money payeeFspFee) {
    this.payeeFspFee = payeeFspFee;
    return this;
  }

  
  @JsonProperty("payeeFspFee")
  @Valid public Money getPayeeFspFee() {
    return payeeFspFee;
  }

  @JsonProperty("payeeFspFee")
  public void setPayeeFspFee(Money payeeFspFee) {
    this.payeeFspFee = payeeFspFee;
  }

  /**
   **/
  public IndividualQuoteResult payeeFspCommission(Money payeeFspCommission) {
    this.payeeFspCommission = payeeFspCommission;
    return this;
  }

  
  @JsonProperty("payeeFspCommission")
  @Valid public Money getPayeeFspCommission() {
    return payeeFspCommission;
  }

  @JsonProperty("payeeFspCommission")
  public void setPayeeFspCommission(Money payeeFspCommission) {
    this.payeeFspCommission = payeeFspCommission;
  }

  /**
   * Information for recipient (transport layer information).
   **/
  public IndividualQuoteResult ilpPacket(String ilpPacket) {
    this.ilpPacket = ilpPacket;
    return this;
  }

  
  @JsonProperty("ilpPacket")
   @Pattern(regexp="^[A-Za-z0-9-_]+[=]{0,2}$") @Size(min=1,max=32768)public String getIlpPacket() {
    return ilpPacket;
  }

  @JsonProperty("ilpPacket")
  public void setIlpPacket(String ilpPacket) {
    this.ilpPacket = ilpPacket;
  }

  /**
   * Condition that must be attached to the transfer by the Payer.
   **/
  public IndividualQuoteResult condition(String condition) {
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
  public IndividualQuoteResult errorInformation(ErrorInformation errorInformation) {
    this.errorInformation = errorInformation;
    return this;
  }

  
  @JsonProperty("errorInformation")
  @Valid public ErrorInformation getErrorInformation() {
    return errorInformation;
  }

  @JsonProperty("errorInformation")
  public void setErrorInformation(ErrorInformation errorInformation) {
    this.errorInformation = errorInformation;
  }

  /**
   **/
  public IndividualQuoteResult extensionList(ExtensionList extensionList) {
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
    IndividualQuoteResult individualQuoteResult = (IndividualQuoteResult) o;
    return Objects.equals(this.quoteId, individualQuoteResult.quoteId) &&
        Objects.equals(this.payee, individualQuoteResult.payee) &&
        Objects.equals(this.transferAmount, individualQuoteResult.transferAmount) &&
        Objects.equals(this.payeeReceiveAmount, individualQuoteResult.payeeReceiveAmount) &&
        Objects.equals(this.payeeFspFee, individualQuoteResult.payeeFspFee) &&
        Objects.equals(this.payeeFspCommission, individualQuoteResult.payeeFspCommission) &&
        Objects.equals(this.ilpPacket, individualQuoteResult.ilpPacket) &&
        Objects.equals(this.condition, individualQuoteResult.condition) &&
        Objects.equals(this.errorInformation, individualQuoteResult.errorInformation) &&
        Objects.equals(this.extensionList, individualQuoteResult.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(quoteId, payee, transferAmount, payeeReceiveAmount, payeeFspFee, payeeFspCommission, ilpPacket, condition, errorInformation, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndividualQuoteResult {\n");
    
    sb.append("    quoteId: ").append(toIndentedString(quoteId)).append("\n");
    sb.append("    payee: ").append(toIndentedString(payee)).append("\n");
    sb.append("    transferAmount: ").append(toIndentedString(transferAmount)).append("\n");
    sb.append("    payeeReceiveAmount: ").append(toIndentedString(payeeReceiveAmount)).append("\n");
    sb.append("    payeeFspFee: ").append(toIndentedString(payeeFspFee)).append("\n");
    sb.append("    payeeFspCommission: ").append(toIndentedString(payeeFspCommission)).append("\n");
    sb.append("    ilpPacket: ").append(toIndentedString(ilpPacket)).append("\n");
    sb.append("    condition: ").append(toIndentedString(condition)).append("\n");
    sb.append("    errorInformation: ").append(toIndentedString(errorInformation)).append("\n");
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

