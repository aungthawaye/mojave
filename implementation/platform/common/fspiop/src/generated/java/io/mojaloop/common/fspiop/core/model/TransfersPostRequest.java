package io.mojaloop.common.fspiop.core.model;

import io.mojaloop.common.fspiop.core.model.ExtensionList;
import io.mojaloop.common.fspiop.core.model.Money;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * The object sent in the POST /transfers request.
 **/

@JsonTypeName("TransfersPostRequest")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-09T06:35:16.332603+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class TransfersPostRequest   {
  private String transferId;
  private String payeeFsp;
  private String payerFsp;
  private Money amount;
  private String ilpPacket;
  private String condition;
  private String expiration;
  private ExtensionList extensionList;

  public TransfersPostRequest() {
  }

  @JsonCreator
  public TransfersPostRequest(
    @JsonProperty(required = true, value = "transferId") String transferId,
    @JsonProperty(required = true, value = "payeeFsp") String payeeFsp,
    @JsonProperty(required = true, value = "payerFsp") String payerFsp,
    @JsonProperty(required = true, value = "amount") Money amount,
    @JsonProperty(required = true, value = "ilpPacket") String ilpPacket,
    @JsonProperty(required = true, value = "condition") String condition,
    @JsonProperty(required = true, value = "expiration") String expiration
  ) {
    this.transferId = transferId;
    this.payeeFsp = payeeFsp;
    this.payerFsp = payerFsp;
    this.amount = amount;
    this.ilpPacket = ilpPacket;
    this.condition = condition;
    this.expiration = expiration;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public TransfersPostRequest transferId(String transferId) {
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
   * FSP identifier.
   **/
  public TransfersPostRequest payeeFsp(String payeeFsp) {
    this.payeeFsp = payeeFsp;
    return this;
  }

  
  @JsonProperty(required = true, value = "payeeFsp")
  @NotNull  @Size(min=1,max=32)public String getPayeeFsp() {
    return payeeFsp;
  }

  @JsonProperty(required = true, value = "payeeFsp")
  public void setPayeeFsp(String payeeFsp) {
    this.payeeFsp = payeeFsp;
  }

  /**
   * FSP identifier.
   **/
  public TransfersPostRequest payerFsp(String payerFsp) {
    this.payerFsp = payerFsp;
    return this;
  }

  
  @JsonProperty(required = true, value = "payerFsp")
  @NotNull  @Size(min=1,max=32)public String getPayerFsp() {
    return payerFsp;
  }

  @JsonProperty(required = true, value = "payerFsp")
  public void setPayerFsp(String payerFsp) {
    this.payerFsp = payerFsp;
  }

  /**
   **/
  public TransfersPostRequest amount(Money amount) {
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
   * Information for recipient (transport layer information).
   **/
  public TransfersPostRequest ilpPacket(String ilpPacket) {
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
  public TransfersPostRequest condition(String condition) {
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
   * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
   **/
  public TransfersPostRequest expiration(String expiration) {
    this.expiration = expiration;
    return this;
  }

  
  @JsonProperty(required = true, value = "expiration")
  @NotNull  @Pattern(regexp="^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:(\\.\\d{3}))(?:Z|[+-][01]\\d:[0-5]\\d)$")public String getExpiration() {
    return expiration;
  }

  @JsonProperty(required = true, value = "expiration")
  public void setExpiration(String expiration) {
    this.expiration = expiration;
  }

  /**
   **/
  public TransfersPostRequest extensionList(ExtensionList extensionList) {
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
    TransfersPostRequest transfersPostRequest = (TransfersPostRequest) o;
    return Objects.equals(this.transferId, transfersPostRequest.transferId) &&
        Objects.equals(this.payeeFsp, transfersPostRequest.payeeFsp) &&
        Objects.equals(this.payerFsp, transfersPostRequest.payerFsp) &&
        Objects.equals(this.amount, transfersPostRequest.amount) &&
        Objects.equals(this.ilpPacket, transfersPostRequest.ilpPacket) &&
        Objects.equals(this.condition, transfersPostRequest.condition) &&
        Objects.equals(this.expiration, transfersPostRequest.expiration) &&
        Objects.equals(this.extensionList, transfersPostRequest.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transferId, payeeFsp, payerFsp, amount, ilpPacket, condition, expiration, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransfersPostRequest {\n");
    
    sb.append("    transferId: ").append(toIndentedString(transferId)).append("\n");
    sb.append("    payeeFsp: ").append(toIndentedString(payeeFsp)).append("\n");
    sb.append("    payerFsp: ").append(toIndentedString(payerFsp)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    ilpPacket: ").append(toIndentedString(ilpPacket)).append("\n");
    sb.append("    condition: ").append(toIndentedString(condition)).append("\n");
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

