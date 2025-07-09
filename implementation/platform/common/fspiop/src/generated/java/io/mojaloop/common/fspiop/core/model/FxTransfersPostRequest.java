package io.mojaloop.common.fspiop.core.model;

import io.mojaloop.common.fspiop.core.model.Money;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * The object sent in the POST /fxTransfers request.
 **/

@JsonTypeName("FxTransfersPostRequest")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-09T06:35:16.332603+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class FxTransfersPostRequest   {
  private String commitRequestId;
  private String determiningTransferId;
  private String initiatingFsp;
  private String counterPartyFsp;
  private Money sourceAmount;
  private Money targetAmount;
  private String condition;
  private String expiration;

  public FxTransfersPostRequest() {
  }

  @JsonCreator
  public FxTransfersPostRequest(
    @JsonProperty(required = true, value = "commitRequestId") String commitRequestId,
    @JsonProperty(required = true, value = "initiatingFsp") String initiatingFsp,
    @JsonProperty(required = true, value = "counterPartyFsp") String counterPartyFsp,
    @JsonProperty(required = true, value = "sourceAmount") Money sourceAmount,
    @JsonProperty(required = true, value = "targetAmount") Money targetAmount,
    @JsonProperty(required = true, value = "condition") String condition
  ) {
    this.commitRequestId = commitRequestId;
    this.initiatingFsp = initiatingFsp;
    this.counterPartyFsp = counterPartyFsp;
    this.sourceAmount = sourceAmount;
    this.targetAmount = targetAmount;
    this.condition = condition;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public FxTransfersPostRequest commitRequestId(String commitRequestId) {
    this.commitRequestId = commitRequestId;
    return this;
  }

  
  @JsonProperty(required = true, value = "commitRequestId")
  @NotNull  @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getCommitRequestId() {
    return commitRequestId;
  }

  @JsonProperty(required = true, value = "commitRequestId")
  public void setCommitRequestId(String commitRequestId) {
    this.commitRequestId = commitRequestId;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public FxTransfersPostRequest determiningTransferId(String determiningTransferId) {
    this.determiningTransferId = determiningTransferId;
    return this;
  }

  
  @JsonProperty("determiningTransferId")
   @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getDeterminingTransferId() {
    return determiningTransferId;
  }

  @JsonProperty("determiningTransferId")
  public void setDeterminingTransferId(String determiningTransferId) {
    this.determiningTransferId = determiningTransferId;
  }

  /**
   * FSP identifier.
   **/
  public FxTransfersPostRequest initiatingFsp(String initiatingFsp) {
    this.initiatingFsp = initiatingFsp;
    return this;
  }

  
  @JsonProperty(required = true, value = "initiatingFsp")
  @NotNull  @Size(min=1,max=32)public String getInitiatingFsp() {
    return initiatingFsp;
  }

  @JsonProperty(required = true, value = "initiatingFsp")
  public void setInitiatingFsp(String initiatingFsp) {
    this.initiatingFsp = initiatingFsp;
  }

  /**
   * FSP identifier.
   **/
  public FxTransfersPostRequest counterPartyFsp(String counterPartyFsp) {
    this.counterPartyFsp = counterPartyFsp;
    return this;
  }

  
  @JsonProperty(required = true, value = "counterPartyFsp")
  @NotNull  @Size(min=1,max=32)public String getCounterPartyFsp() {
    return counterPartyFsp;
  }

  @JsonProperty(required = true, value = "counterPartyFsp")
  public void setCounterPartyFsp(String counterPartyFsp) {
    this.counterPartyFsp = counterPartyFsp;
  }

  /**
   **/
  public FxTransfersPostRequest sourceAmount(Money sourceAmount) {
    this.sourceAmount = sourceAmount;
    return this;
  }

  
  @JsonProperty(required = true, value = "sourceAmount")
  @NotNull @Valid public Money getSourceAmount() {
    return sourceAmount;
  }

  @JsonProperty(required = true, value = "sourceAmount")
  public void setSourceAmount(Money sourceAmount) {
    this.sourceAmount = sourceAmount;
  }

  /**
   **/
  public FxTransfersPostRequest targetAmount(Money targetAmount) {
    this.targetAmount = targetAmount;
    return this;
  }

  
  @JsonProperty(required = true, value = "targetAmount")
  @NotNull @Valid public Money getTargetAmount() {
    return targetAmount;
  }

  @JsonProperty(required = true, value = "targetAmount")
  public void setTargetAmount(Money targetAmount) {
    this.targetAmount = targetAmount;
  }

  /**
   * Condition that must be attached to the transfer by the Payer.
   **/
  public FxTransfersPostRequest condition(String condition) {
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
  public FxTransfersPostRequest expiration(String expiration) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FxTransfersPostRequest fxTransfersPostRequest = (FxTransfersPostRequest) o;
    return Objects.equals(this.commitRequestId, fxTransfersPostRequest.commitRequestId) &&
        Objects.equals(this.determiningTransferId, fxTransfersPostRequest.determiningTransferId) &&
        Objects.equals(this.initiatingFsp, fxTransfersPostRequest.initiatingFsp) &&
        Objects.equals(this.counterPartyFsp, fxTransfersPostRequest.counterPartyFsp) &&
        Objects.equals(this.sourceAmount, fxTransfersPostRequest.sourceAmount) &&
        Objects.equals(this.targetAmount, fxTransfersPostRequest.targetAmount) &&
        Objects.equals(this.condition, fxTransfersPostRequest.condition) &&
        Objects.equals(this.expiration, fxTransfersPostRequest.expiration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commitRequestId, determiningTransferId, initiatingFsp, counterPartyFsp, sourceAmount, targetAmount, condition, expiration);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FxTransfersPostRequest {\n");
    
    sb.append("    commitRequestId: ").append(toIndentedString(commitRequestId)).append("\n");
    sb.append("    determiningTransferId: ").append(toIndentedString(determiningTransferId)).append("\n");
    sb.append("    initiatingFsp: ").append(toIndentedString(initiatingFsp)).append("\n");
    sb.append("    counterPartyFsp: ").append(toIndentedString(counterPartyFsp)).append("\n");
    sb.append("    sourceAmount: ").append(toIndentedString(sourceAmount)).append("\n");
    sb.append("    targetAmount: ").append(toIndentedString(targetAmount)).append("\n");
    sb.append("    condition: ").append(toIndentedString(condition)).append("\n");
    sb.append("    expiration: ").append(toIndentedString(expiration)).append("\n");
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

