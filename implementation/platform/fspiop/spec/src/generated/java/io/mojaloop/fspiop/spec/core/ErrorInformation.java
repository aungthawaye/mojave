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
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Data model for the complex type ErrorInformation.
 **/

@JsonTypeName("ErrorInformation")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.13.0")
public class ErrorInformation {

    private String errorCode;

    private String errorDescription;

    private ExtensionList extensionList;

    public ErrorInformation() {

    }

    @JsonCreator
    public ErrorInformation(@JsonProperty(required = true, value = "errorCode") String errorCode,
                            @JsonProperty(required = true, value = "errorDescription") String errorDescription) {

        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorInformation errorInformation = (ErrorInformation) o;
        return Objects.equals(this.errorCode, errorInformation.errorCode) && Objects.equals(this.errorDescription, errorInformation.errorDescription) &&
                   Objects.equals(this.extensionList, errorInformation.extensionList);
    }

    /**
     * The API data type ErrorCode is a JSON String of four characters, consisting of digits only. Negative numbers are not allowed. A leading zero is not allowed. Each error code in the API is a four-digit number, for example, 1234, where the first number (1 in the example) represents the high-level error category, the second number (2 in the example) represents the low-level error category, and the last two numbers (34 in the example) represent the specific error.
     **/
    public ErrorInformation errorCode(String errorCode) {

        this.errorCode = errorCode;
        return this;
    }

    /**
     * Error description string.
     **/
    public ErrorInformation errorDescription(String errorDescription) {

        this.errorDescription = errorDescription;
        return this;
    }

    /**
     **/
    public ErrorInformation extensionList(ExtensionList extensionList) {

        this.extensionList = extensionList;
        return this;
    }

    @JsonProperty(required = true, value = "errorCode")
    @NotNull
    @Pattern(regexp = "^[1-9]\\d{3}$")
    public String getErrorCode() {

        return errorCode;
    }

    @JsonProperty(required = true, value = "errorCode")
    public void setErrorCode(String errorCode) {

        this.errorCode = errorCode;
    }

    @JsonProperty(required = true, value = "errorDescription")
    @NotNull
    @Size(min = 1, max = 128)
    public String getErrorDescription() {

        return errorDescription;
    }

    @JsonProperty(required = true, value = "errorDescription")
    public void setErrorDescription(String errorDescription) {

        this.errorDescription = errorDescription;
    }

    @JsonProperty("extensionList")
    @Valid
    public ExtensionList getExtensionList() {

        return extensionList;
    }

    @JsonProperty("extensionList")
    public void setExtensionList(ExtensionList extensionList) {

        this.extensionList = extensionList;
    }

    @Override
    public int hashCode() {

        return Objects.hash(errorCode, errorDescription, extensionList);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ErrorInformation {\n");

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

