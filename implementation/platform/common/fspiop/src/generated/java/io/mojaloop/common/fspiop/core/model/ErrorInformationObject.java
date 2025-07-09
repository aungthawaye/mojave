package io.mojaloop.common.fspiop.core.model;

import io.mojaloop.common.fspiop.core.model.ErrorInformation;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type object that contains ErrorInformation.
 **/

@JsonTypeName("ErrorInformationObject")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-09T06:35:16.332603+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class ErrorInformationObject   {
  private ErrorInformation errorInformation;

  public ErrorInformationObject() {
  }

  @JsonCreator
  public ErrorInformationObject(
    @JsonProperty(required = true, value = "errorInformation") ErrorInformation errorInformation
  ) {
    this.errorInformation = errorInformation;
  }

  /**
   **/
  public ErrorInformationObject errorInformation(ErrorInformation errorInformation) {
    this.errorInformation = errorInformation;
    return this;
  }

  
  @JsonProperty(required = true, value = "errorInformation")
  @NotNull @Valid public ErrorInformation getErrorInformation() {
    return errorInformation;
  }

  @JsonProperty(required = true, value = "errorInformation")
  public void setErrorInformation(ErrorInformation errorInformation) {
    this.errorInformation = errorInformation;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorInformationObject errorInformationObject = (ErrorInformationObject) o;
    return Objects.equals(this.errorInformation, errorInformationObject.errorInformation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorInformation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorInformationObject {\n");
    
    sb.append("    errorInformation: ").append(toIndentedString(errorInformation)).append("\n");
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

