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
import io.mojaloop.common.fspiop.model.core.ExtensionList;
import io.mojaloop.common.fspiop.model.core.FxCharge;
import io.mojaloop.common.fspiop.model.core.FxMoney;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * A DFSP will be able to request a currency conversion, and an FX provider will be able to describe its involvement in a proposed transfer, using a FxConversion object.
 **/

@JsonTypeName("FxConversion")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class FxConversion   {
  private String conversionId;
  private String determiningTransferId;
  private String initiatingFsp;
  private String counterPartyFsp;
  private AmountType amountType;
  private FxMoney sourceAmount;
  private FxMoney targetAmount;
  private String expiration;
  private @Valid List<@Valid FxCharge> charges = new ArrayList<>();
  private ExtensionList extensionList;

  public FxConversion() {
  }

  @JsonCreator
  public FxConversion(
    @JsonProperty(required = true, value = "conversionId") String conversionId,
    @JsonProperty(required = true, value = "initiatingFsp") String initiatingFsp,
    @JsonProperty(required = true, value = "counterPartyFsp") String counterPartyFsp,
    @JsonProperty(required = true, value = "amountType") AmountType amountType,
    @JsonProperty(required = true, value = "sourceAmount") FxMoney sourceAmount,
    @JsonProperty(required = true, value = "targetAmount") FxMoney targetAmount,
    @JsonProperty(required = true, value = "expiration") String expiration
  ) {
    this.conversionId = conversionId;
    this.initiatingFsp = initiatingFsp;
    this.counterPartyFsp = counterPartyFsp;
    this.amountType = amountType;
    this.sourceAmount = sourceAmount;
    this.targetAmount = targetAmount;
    this.expiration = expiration;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public FxConversion conversionId(String conversionId) {
    this.conversionId = conversionId;
    return this;
  }

  
  @JsonProperty(required = true, value = "conversionId")
  @NotNull  @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getConversionId() {
    return conversionId;
  }

  @JsonProperty(required = true, value = "conversionId")
  public void setConversionId(String conversionId) {
    this.conversionId = conversionId;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public FxConversion determiningTransferId(String determiningTransferId) {
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
  public FxConversion initiatingFsp(String initiatingFsp) {
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
  public FxConversion counterPartyFsp(String counterPartyFsp) {
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
  public FxConversion amountType(AmountType amountType) {
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
  public FxConversion sourceAmount(FxMoney sourceAmount) {
    this.sourceAmount = sourceAmount;
    return this;
  }

  
  @JsonProperty(required = true, value = "sourceAmount")
  @NotNull @Valid public FxMoney getSourceAmount() {
    return sourceAmount;
  }

  @JsonProperty(required = true, value = "sourceAmount")
  public void setSourceAmount(FxMoney sourceAmount) {
    this.sourceAmount = sourceAmount;
  }

  /**
   **/
  public FxConversion targetAmount(FxMoney targetAmount) {
    this.targetAmount = targetAmount;
    return this;
  }

  
  @JsonProperty(required = true, value = "targetAmount")
  @NotNull @Valid public FxMoney getTargetAmount() {
    return targetAmount;
  }

  @JsonProperty(required = true, value = "targetAmount")
  public void setTargetAmount(FxMoney targetAmount) {
    this.targetAmount = targetAmount;
  }

  /**
   * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
   **/
  public FxConversion expiration(String expiration) {
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
   * One or more charges which the FXP intends to levy as part of the currency conversion, or which the payee DFSP intends to add to the amount transferred.
   **/
  public FxConversion charges(List<@Valid FxCharge> charges) {
    this.charges = charges;
    return this;
  }

  
  @JsonProperty("charges")
  @Valid  @Size(min=0,max=16)public List<@Valid FxCharge> getCharges() {
    return charges;
  }

  @JsonProperty("charges")
  public void setCharges(List<@Valid FxCharge> charges) {
    this.charges = charges;
  }

  public FxConversion addChargesItem(FxCharge chargesItem) {
    if (this.charges == null) {
      this.charges = new ArrayList<>();
    }

    this.charges.add(chargesItem);
    return this;
  }

  public FxConversion removeChargesItem(FxCharge chargesItem) {
    if (chargesItem != null && this.charges != null) {
      this.charges.remove(chargesItem);
    }

    return this;
  }
  /**
   **/
  public FxConversion extensionList(ExtensionList extensionList) {
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
    FxConversion fxConversion = (FxConversion) o;
    return Objects.equals(this.conversionId, fxConversion.conversionId) &&
        Objects.equals(this.determiningTransferId, fxConversion.determiningTransferId) &&
        Objects.equals(this.initiatingFsp, fxConversion.initiatingFsp) &&
        Objects.equals(this.counterPartyFsp, fxConversion.counterPartyFsp) &&
        Objects.equals(this.amountType, fxConversion.amountType) &&
        Objects.equals(this.sourceAmount, fxConversion.sourceAmount) &&
        Objects.equals(this.targetAmount, fxConversion.targetAmount) &&
        Objects.equals(this.expiration, fxConversion.expiration) &&
        Objects.equals(this.charges, fxConversion.charges) &&
        Objects.equals(this.extensionList, fxConversion.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(conversionId, determiningTransferId, initiatingFsp, counterPartyFsp, amountType, sourceAmount, targetAmount, expiration, charges, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FxConversion {\n");
    
    sb.append("    conversionId: ").append(toIndentedString(conversionId)).append("\n");
    sb.append("    determiningTransferId: ").append(toIndentedString(determiningTransferId)).append("\n");
    sb.append("    initiatingFsp: ").append(toIndentedString(initiatingFsp)).append("\n");
    sb.append("    counterPartyFsp: ").append(toIndentedString(counterPartyFsp)).append("\n");
    sb.append("    amountType: ").append(toIndentedString(amountType)).append("\n");
    sb.append("    sourceAmount: ").append(toIndentedString(sourceAmount)).append("\n");
    sb.append("    targetAmount: ").append(toIndentedString(targetAmount)).append("\n");
    sb.append("    expiration: ").append(toIndentedString(expiration)).append("\n");
    sb.append("    charges: ").append(toIndentedString(charges)).append("\n");
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

