package io.mojaloop.common.fspiop.model.settlement;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.mojaloop.common.fspiop.model.settlement.ErrorErrorInformationExtensionList;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("Error_errorInformation")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:33.786087+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class ErrorErrorInformation   {
  private Integer errorCode;
  private String errorDescription;
  private ErrorErrorInformationExtensionList extensionList;

  public ErrorErrorInformation() {
  }

  /**
   **/
  public ErrorErrorInformation errorCode(Integer errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  
  @JsonProperty("errorCode")
  public Integer getErrorCode() {
    return errorCode;
  }

  @JsonProperty("errorCode")
  public void setErrorCode(Integer errorCode) {
    this.errorCode = errorCode;
  }

  /**
   **/
  public ErrorErrorInformation errorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
    return this;
  }

  
  @JsonProperty("errorDescription")
  public String getErrorDescription() {
    return errorDescription;
  }

  @JsonProperty("errorDescription")
  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }

  /**
   **/
  public ErrorErrorInformation extensionList(ErrorErrorInformationExtensionList extensionList) {
    this.extensionList = extensionList;
    return this;
  }

  
  @JsonProperty("extensionList")
  @Valid public ErrorErrorInformationExtensionList getExtensionList() {
    return extensionList;
  }

  @JsonProperty("extensionList")
  public void setExtensionList(ErrorErrorInformationExtensionList extensionList) {
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
    ErrorErrorInformation errorErrorInformation = (ErrorErrorInformation) o;
    return Objects.equals(this.errorCode, errorErrorInformation.errorCode) &&
        Objects.equals(this.errorDescription, errorErrorInformation.errorDescription) &&
        Objects.equals(this.extensionList, errorErrorInformation.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorCode, errorDescription, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorErrorInformation {\n");
    
    sb.append("    errorCode: ").append(toIndentedString(errorCode)).append("\n");
    sb.append("    errorDescription: ").append(toIndentedString(errorDescription)).append("\n");
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

