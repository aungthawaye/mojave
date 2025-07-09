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
 * The FxRate object contains information about a currency conversion in the transfer. It can be used by parties to the transfer to exchange information with each other about the exchange rate for the transfer, to ensure that the best rate can be agreed on.
 **/

@JsonTypeName("FxRate")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-09T06:35:16.332603+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class FxRate   {
  private Money sourceAmount;
  private Money targetAmount;

  public FxRate() {
  }

  @JsonCreator
  public FxRate(
    @JsonProperty(required = true, value = "sourceAmount") Money sourceAmount,
    @JsonProperty(required = true, value = "targetAmount") Money targetAmount
  ) {
    this.sourceAmount = sourceAmount;
    this.targetAmount = targetAmount;
  }

  /**
   **/
  public FxRate sourceAmount(Money sourceAmount) {
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
  public FxRate targetAmount(Money targetAmount) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FxRate fxRate = (FxRate) o;
    return Objects.equals(this.sourceAmount, fxRate.sourceAmount) &&
        Objects.equals(this.targetAmount, fxRate.targetAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceAmount, targetAmount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FxRate {\n");
    
    sb.append("    sourceAmount: ").append(toIndentedString(sourceAmount)).append("\n");
    sb.append("    targetAmount: ").append(toIndentedString(targetAmount)).append("\n");
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

