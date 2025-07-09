package io.mojaloop.common.fspiop.core.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * U2F challenge-response, where payer FSP verifies if the response provided by end-user device matches the previously registered key. 
 **/

@JsonTypeName("U2FPinValue")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-09T06:35:16.332603+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class U2FPinValue   {
  private String pinValue;
  private String counter;

  public U2FPinValue() {
  }

  @JsonCreator
  public U2FPinValue(
    @JsonProperty(required = true, value = "pinValue") String pinValue,
    @JsonProperty(required = true, value = "counter") String counter
  ) {
    this.pinValue = pinValue;
    this.counter = counter;
  }

  /**
   * U2F challenge-response.
   **/
  public U2FPinValue pinValue(String pinValue) {
    this.pinValue = pinValue;
    return this;
  }

  
  @JsonProperty(required = true, value = "pinValue")
  @NotNull  @Pattern(regexp="^\\S{1,64}$") @Size(min=1,max=64)public String getPinValue() {
    return pinValue;
  }

  @JsonProperty(required = true, value = "pinValue")
  public void setPinValue(String pinValue) {
    this.pinValue = pinValue;
  }

  /**
   * Sequential counter used for cloning detection. Present only for U2F authentication.
   **/
  public U2FPinValue counter(String counter) {
    this.counter = counter;
    return this;
  }

  
  @JsonProperty(required = true, value = "counter")
  @NotNull  @Pattern(regexp="^[1-9]\\d*$")public String getCounter() {
    return counter;
  }

  @JsonProperty(required = true, value = "counter")
  public void setCounter(String counter) {
    this.counter = counter;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    U2FPinValue u2FPinValue = (U2FPinValue) o;
    return Objects.equals(this.pinValue, u2FPinValue.pinValue) &&
        Objects.equals(this.counter, u2FPinValue.counter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pinValue, counter);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class U2FPinValue {\n");
    
    sb.append("    pinValue: ").append(toIndentedString(pinValue)).append("\n");
    sb.append("    counter: ").append(toIndentedString(counter)).append("\n");
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

