package io.mojaloop.common.fspiop.settlement.model;

import io.mojaloop.common.fspiop.settlement.model.NetSettlementAmount;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("Account")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-09T06:35:17.230850+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class Account   {
  private String id;
  private String reason;
  private String state;
  private NetSettlementAmount netSettlementAmount;

  public Account() {
  }

  /**
   * Account Id
   **/
  public Account id(String id) {
    this.id = id;
    return this;
  }

  
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  /**
   * TBD
   **/
  public Account reason(String reason) {
    this.reason = reason;
    return this;
  }

  
  @JsonProperty("reason")
  public String getReason() {
    return reason;
  }

  @JsonProperty("reason")
  public void setReason(String reason) {
    this.reason = reason;
  }

  /**
   **/
  public Account state(String state) {
    this.state = state;
    return this;
  }

  
  @JsonProperty("state")
  public String getState() {
    return state;
  }

  @JsonProperty("state")
  public void setState(String state) {
    this.state = state;
  }

  /**
   **/
  public Account netSettlementAmount(NetSettlementAmount netSettlementAmount) {
    this.netSettlementAmount = netSettlementAmount;
    return this;
  }

  
  @JsonProperty("netSettlementAmount")
  @Valid public NetSettlementAmount getNetSettlementAmount() {
    return netSettlementAmount;
  }

  @JsonProperty("netSettlementAmount")
  public void setNetSettlementAmount(NetSettlementAmount netSettlementAmount) {
    this.netSettlementAmount = netSettlementAmount;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Account account = (Account) o;
    return Objects.equals(this.id, account.id) &&
        Objects.equals(this.reason, account.reason) &&
        Objects.equals(this.state, account.state) &&
        Objects.equals(this.netSettlementAmount, account.netSettlementAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, reason, state, netSettlementAmount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Account {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    netSettlementAmount: ").append(toIndentedString(netSettlementAmount)).append("\n");
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

