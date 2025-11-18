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

package io.mojaloop.fspiop.spec.settlement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@JsonTypeName("Error")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              date = "2025-11-09T11:40:06.177887+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class Error {

    private ErrorErrorInformation errorInformation;

    public Error() {

    }

    @JsonCreator
    public Error(@JsonProperty(required = true, value = "errorInformation") ErrorErrorInformation errorInformation) {

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

    /**
     **/
    public Error errorInformation(ErrorErrorInformation errorInformation) {

        this.errorInformation = errorInformation;
        return this;
    }

    @JsonProperty(required = true, value = "errorInformation")
    @NotNull
    @Valid
    public ErrorErrorInformation getErrorInformation() {

        return errorInformation;
    }

    @JsonProperty(required = true, value = "errorInformation")
    public void setErrorInformation(ErrorErrorInformation errorInformation) {

        this.errorInformation = errorInformation;
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

