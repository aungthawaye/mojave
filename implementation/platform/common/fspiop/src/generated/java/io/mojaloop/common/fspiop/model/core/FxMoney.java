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

import io.mojaloop.common.fspiop.model.core.Currency;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type FxMoney; This is based on the type Money but allows the amount to be optional to support FX quotations.
 **/

@JsonTypeName("FxMoney")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class FxMoney   {
  private Currency currency;
  private String amount;

  public FxMoney() {
  }

  @JsonCreator
  public FxMoney(
    @JsonProperty(required = true, value = "currency") Currency currency
  ) {
    this.currency = currency;
  }

  /**
   **/
  public FxMoney currency(Currency currency) {
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
  public FxMoney amount(String amount) {
    this.amount = amount;
    return this;
  }

  
  @JsonProperty("amount")
   @Pattern(regexp="^([0]|([1-9][0-9]{0,17}))([.][0-9]{0,3}[1-9])?$")public String getAmount() {
    return amount;
  }

  @JsonProperty("amount")
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
    FxMoney fxMoney = (FxMoney) o;
    return Objects.equals(this.currency, fxMoney.currency) &&
        Objects.equals(this.amount, fxMoney.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currency, amount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FxMoney {\n");
    
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

