package io.mojaloop.common.fspiop.settlement.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("netSettlementAmount")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-08T15:50:16.236200+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class NetSettlementAmount   {
  private BigDecimal amount;
  private String currency;

  public NetSettlementAmount() {
  }

  /**
   **/
  public NetSettlementAmount amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  
  @JsonProperty("amount")
  @Valid public BigDecimal getAmount() {
    return amount;
  }

  @JsonProperty("amount")
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  /**
   **/
  public NetSettlementAmount currency(String currency) {
    this.currency = currency;
    return this;
  }

  
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  @JsonProperty("currency")
  public void setCurrency(String currency) {
    this.currency = currency;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NetSettlementAmount netSettlementAmount = (NetSettlementAmount) o;
    return Objects.equals(this.amount, netSettlementAmount.amount) &&
        Objects.equals(this.currency, netSettlementAmount.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, currency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NetSettlementAmount {\n");
    
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
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

