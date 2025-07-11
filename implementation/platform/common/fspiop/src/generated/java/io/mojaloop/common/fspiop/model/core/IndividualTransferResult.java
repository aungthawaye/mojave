package io.mojaloop.common.fspiop.model.core;

import io.mojaloop.common.fspiop.model.core.ErrorInformation;
import io.mojaloop.common.fspiop.model.core.ExtensionList;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type IndividualTransferResult.
 **/

@JsonTypeName("IndividualTransferResult")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class IndividualTransferResult   {
  private String transferId;
  private String fulfilment;
  private ErrorInformation errorInformation;
  private ExtensionList extensionList;

  public IndividualTransferResult() {
  }

  @JsonCreator
  public IndividualTransferResult(
    @JsonProperty(required = true, value = "transferId") String transferId
  ) {
    this.transferId = transferId;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public IndividualTransferResult transferId(String transferId) {
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
   * Fulfilment that must be attached to the transfer by the Payee.
   **/
  public IndividualTransferResult fulfilment(String fulfilment) {
    this.fulfilment = fulfilment;
    return this;
  }

  
  @JsonProperty("fulfilment")
   @Pattern(regexp="^[A-Za-z0-9-_]{43}$") @Size(max=48)public String getFulfilment() {
    return fulfilment;
  }

  @JsonProperty("fulfilment")
  public void setFulfilment(String fulfilment) {
    this.fulfilment = fulfilment;
  }

  /**
   **/
  public IndividualTransferResult errorInformation(ErrorInformation errorInformation) {
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
  public IndividualTransferResult extensionList(ExtensionList extensionList) {
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
    IndividualTransferResult individualTransferResult = (IndividualTransferResult) o;
    return Objects.equals(this.transferId, individualTransferResult.transferId) &&
        Objects.equals(this.fulfilment, individualTransferResult.fulfilment) &&
        Objects.equals(this.errorInformation, individualTransferResult.errorInformation) &&
        Objects.equals(this.extensionList, individualTransferResult.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transferId, fulfilment, errorInformation, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndividualTransferResult {\n");
    
    sb.append("    transferId: ").append(toIndentedString(transferId)).append("\n");
    sb.append("    fulfilment: ").append(toIndentedString(fulfilment)).append("\n");
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

