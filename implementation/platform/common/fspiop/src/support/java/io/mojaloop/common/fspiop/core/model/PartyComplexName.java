package io.mojaloop.common.fspiop.core.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type PartyComplexName.
 **/

@JsonTypeName("PartyComplexName")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-08T15:50:15.331321+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class PartyComplexName   {
  private String firstName;
  private String middleName;
  private String lastName;

  public PartyComplexName() {
  }

  /**
   * First name of the Party (Name Type).
   **/
  public PartyComplexName firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  
  @JsonProperty("firstName")
   @Pattern(regexp="^(?!\\s*$)[\\p{L}\\p{gc=Mark}\\p{digit}\\p{gc=Connector_Punctuation}\\p{Join_Control} .,''-]{1,128}$") @Size(min=1,max=128)public String getFirstName() {
    return firstName;
  }

  @JsonProperty("firstName")
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Middle name of the Party (Name Type).
   **/
  public PartyComplexName middleName(String middleName) {
    this.middleName = middleName;
    return this;
  }

  
  @JsonProperty("middleName")
   @Pattern(regexp="^(?!\\s*$)[\\p{L}\\p{gc=Mark}\\p{digit}\\p{gc=Connector_Punctuation}\\p{Join_Control} .,''-]{1,128}$") @Size(min=1,max=128)public String getMiddleName() {
    return middleName;
  }

  @JsonProperty("middleName")
  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  /**
   * Last name of the Party (Name Type).
   **/
  public PartyComplexName lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  
  @JsonProperty("lastName")
   @Pattern(regexp="^(?!\\s*$)[\\p{L}\\p{gc=Mark}\\p{digit}\\p{gc=Connector_Punctuation}\\p{Join_Control} .,''-]{1,128}$") @Size(min=1,max=128)public String getLastName() {
    return lastName;
  }

  @JsonProperty("lastName")
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PartyComplexName partyComplexName = (PartyComplexName) o;
    return Objects.equals(this.firstName, partyComplexName.firstName) &&
        Objects.equals(this.middleName, partyComplexName.middleName) &&
        Objects.equals(this.lastName, partyComplexName.lastName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstName, middleName, lastName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PartyComplexName {\n");
    
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    middleName: ").append(toIndentedString(middleName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
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

