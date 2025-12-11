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
package org.mojave.fspiop.spec.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;

import java.util.Objects;

/**
 * Data model for the complex type object that contains an optional element ErrorInformation used along with 4xx and 5xx responses.
 **/

@JsonTypeName("ErrorInformationResponse")
@jakarta.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
    comments = "Generator version: 7.13.0")
public class ErrorInformationResponse {

    private ErrorInformation errorInformation;

    public ErrorInformationResponse() {

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorInformationResponse errorInformationResponse = (ErrorInformationResponse) o;
        return Objects.equals(this.errorInformation, errorInformationResponse.errorInformation);
    }

    /**
     **/
    public ErrorInformationResponse errorInformation(ErrorInformation errorInformation) {

        this.errorInformation = errorInformation;
        return this;
    }

    @JsonProperty("errorInformation")
    @Valid
    public ErrorInformation getErrorInformation() {

        return errorInformation;
    }

    @JsonProperty("errorInformation")
    public void setErrorInformation(ErrorInformation errorInformation) {

        this.errorInformation = errorInformation;
    }

    @Override
    public int hashCode() {

        return Objects.hash(errorInformation);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ErrorInformationResponse {\n");

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

