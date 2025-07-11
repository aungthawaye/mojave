package io.mojaloop.common.fspiop.model.core;

import io.mojaloop.common.fspiop.model.core.AuthenticationType;
import io.mojaloop.common.fspiop.model.core.AuthenticationValue;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type AuthenticationInfo.
 **/

@JsonTypeName("AuthenticationInfo")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class AuthenticationInfo   {
  private AuthenticationType authentication;
  private AuthenticationValue authenticationValue;

  public AuthenticationInfo() {
  }

  @JsonCreator
  public AuthenticationInfo(
    @JsonProperty(required = true, value = "authentication") AuthenticationType authentication,
    @JsonProperty(required = true, value = "authenticationValue") AuthenticationValue authenticationValue
  ) {
    this.authentication = authentication;
    this.authenticationValue = authenticationValue;
  }

  /**
   **/
  public AuthenticationInfo authentication(AuthenticationType authentication) {
    this.authentication = authentication;
    return this;
  }

  
  @JsonProperty(required = true, value = "authentication")
  @NotNull public AuthenticationType getAuthentication() {
    return authentication;
  }

  @JsonProperty(required = true, value = "authentication")
  public void setAuthentication(AuthenticationType authentication) {
    this.authentication = authentication;
  }

  /**
   **/
  public AuthenticationInfo authenticationValue(AuthenticationValue authenticationValue) {
    this.authenticationValue = authenticationValue;
    return this;
  }

  
  @JsonProperty(required = true, value = "authenticationValue")
  @NotNull @Valid public AuthenticationValue getAuthenticationValue() {
    return authenticationValue;
  }

  @JsonProperty(required = true, value = "authenticationValue")
  public void setAuthenticationValue(AuthenticationValue authenticationValue) {
    this.authenticationValue = authenticationValue;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuthenticationInfo authenticationInfo = (AuthenticationInfo) o;
    return Objects.equals(this.authentication, authenticationInfo.authentication) &&
        Objects.equals(this.authenticationValue, authenticationInfo.authenticationValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authentication, authenticationValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationInfo {\n");
    
    sb.append("    authentication: ").append(toIndentedString(authentication)).append("\n");
    sb.append("    authenticationValue: ").append(toIndentedString(authenticationValue)).append("\n");
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

