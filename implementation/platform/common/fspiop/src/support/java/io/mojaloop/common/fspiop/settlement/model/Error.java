package io.mojaloop.common.fspiop.settlement.model;

import io.mojaloop.common.fspiop.settlement.model.ErrorErrorInformation;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("Error")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-08T15:50:16.236200+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class Error   {
  private ErrorErrorInformation errorInformation;

  public Error() {
  }

  @JsonCreator
  public Error(
    @JsonProperty(required = true, value = "errorInformation") ErrorErrorInformation errorInformation
  ) {
    this.errorInformation = errorInformation;
  }

  /**
   **/
  public Error errorInformation(ErrorErrorInformation errorInformation) {
    this.errorInformation = errorInformation;
    return this;
  }

  
  @JsonProperty(required = true, value = "errorInformation")
  @NotNull @Valid public ErrorErrorInformation getErrorInformation() {
    return errorInformation;
  }

  @JsonProperty(required = true, value = "errorInformation")
  public void setErrorInformation(ErrorErrorInformation errorInformation) {
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
    Error error = (Error) o;
    return Objects.equals(this.errorInformation, error.errorInformation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorInformation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Error {\n");
    
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

