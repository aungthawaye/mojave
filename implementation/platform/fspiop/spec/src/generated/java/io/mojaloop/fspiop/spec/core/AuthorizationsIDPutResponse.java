/*-
 * ================================================================================
 * Mojave
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
package io.mojaloop.fspiop.spec.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * The object sent in the PUT /authorizations/{ID} callback.
 **/

@JsonTypeName("AuthorizationsIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.13.0")
public class AuthorizationsIDPutResponse {

    private AuthenticationInfo authenticationInfo;

    private AuthorizationResponse responseType;

    public AuthorizationsIDPutResponse() {

    }

    @JsonCreator
    public AuthorizationsIDPutResponse(@JsonProperty(required = true, value = "responseType") AuthorizationResponse responseType) {

        this.responseType = responseType;
    }

    /**
     **/
    public AuthorizationsIDPutResponse authenticationInfo(AuthenticationInfo authenticationInfo) {

        this.authenticationInfo = authenticationInfo;
        return this;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthorizationsIDPutResponse authorizationsIDPutResponse = (AuthorizationsIDPutResponse) o;
        return Objects.equals(this.authenticationInfo, authorizationsIDPutResponse.authenticationInfo) &&
                   Objects.equals(this.responseType, authorizationsIDPutResponse.responseType);
    }

    @JsonProperty("authenticationInfo")
    @Valid
    public AuthenticationInfo getAuthenticationInfo() {

        return authenticationInfo;
    }

    @JsonProperty("authenticationInfo")
    public void setAuthenticationInfo(AuthenticationInfo authenticationInfo) {

        this.authenticationInfo = authenticationInfo;
    }

    @JsonProperty(required = true, value = "responseType")
    @NotNull
    public AuthorizationResponse getResponseType() {

        return responseType;
    }

    @JsonProperty(required = true, value = "responseType")
    public void setResponseType(AuthorizationResponse responseType) {

        this.responseType = responseType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(authenticationInfo, responseType);
    }

    /**
     **/
    public AuthorizationsIDPutResponse responseType(AuthorizationResponse responseType) {

        this.responseType = responseType;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class AuthorizationsIDPutResponse {\n");

        sb.append("    authenticationInfo: ").append(toIndentedString(authenticationInfo)).append("\n");
        sb.append("    responseType: ").append(toIndentedString(responseType)).append("\n");
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

