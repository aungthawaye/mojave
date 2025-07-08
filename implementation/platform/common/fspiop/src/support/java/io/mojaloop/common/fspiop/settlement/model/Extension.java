package io.mojaloop.common.fspiop.settlement.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("Extension")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-08T15:50:16.236200+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class Extension   {
  private String key;
  private String value;

  public Extension() {
  }

  @JsonCreator
  public Extension(
    @JsonProperty(required = true, value = "key") String key,
    @JsonProperty(required = true, value = "value") String value
  ) {
    this.key = key;
    this.value = value;
  }

  /**
   **/
  public Extension key(String key) {
    this.key = key;
    return this;
  }

  
  @JsonProperty(required = true, value = "key")
  @NotNull public String getKey() {
    return key;
  }

  @JsonProperty(required = true, value = "key")
  public void setKey(String key) {
    this.key = key;
  }

  /**
   **/
  public Extension value(String value) {
    this.value = value;
    return this;
  }

  
  @JsonProperty(required = true, value = "value")
  @NotNull public String getValue() {
    return value;
  }

  @JsonProperty(required = true, value = "value")
  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Extension extension = (Extension) o;
    return Objects.equals(this.key, extension.key) &&
        Objects.equals(this.value, extension.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Extension {\n");
    
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

