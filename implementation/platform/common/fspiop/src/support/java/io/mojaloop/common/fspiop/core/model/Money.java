package io.mojaloop.common.fspiop.core.model;

import io.mojaloop.common.fspiop.core.model.Currency;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type Money.
 **/

@JsonTypeName("Money")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-08T15:50:15.331321+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class Money   {
  private Currency currency;
  private String amount;

  public Money() {
  }

  @JsonCreator
  public Money(
    @JsonProperty(required = true, value = "currency") Currency currency,
    @JsonProperty(required = true, value = "amount") String amount
  ) {
    this.currency = currency;
    this.amount = amount;
  }

  /**
   **/
  public Money currency(Currency currency) {
    this.currency = currency;
    return this;
  }

  
  @JsonProperty(required = true, value = "currency")
  @NotNull public Currency getCurrency() {
    return currency;
  }

  @JsonProperty(required = true, value = "currency")
  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  /**
   * The API data type Amount is a JSON String in a canonical format that is restricted by a regular expression for interoperability reasons. This pattern does not allow any trailing zeroes at all, but allows an amount without a minor currency unit. It also only allows four digits in the minor currency unit; a negative value is not allowed. Using more than 18 digits in the major currency unit is not allowed.
   **/
  public Money amount(String amount) {
    this.amount = amount;
    return this;
  }

  
  @JsonProperty(required = true, value = "amount")
  @NotNull  @Pattern(regexp="^([0]|([1-9][0-9]{0,17}))([.][0-9]{0,3}[1-9])?$")public String getAmount() {
    return amount;
  }

  @JsonProperty(required = true, value = "amount")
  public void setAmount(String amount) {
    this.amount = amount;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Money money = (Money) o;
    return Objects.equals(this.currency, money.currency) &&
        Objects.equals(this.amount, money.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currency, amount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Money {\n");
    
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
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

