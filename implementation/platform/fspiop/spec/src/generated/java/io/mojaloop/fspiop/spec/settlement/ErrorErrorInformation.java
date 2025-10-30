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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;

import java.util.Objects;

@JsonTypeName("Error_errorInformation")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              date = "2025-10-22T16:46:48.253622+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class ErrorErrorInformation {

    private Integer errorCode;

    private String errorDescription;

    private ErrorErrorInformationExtensionList extensionList;

    public ErrorErrorInformation() {

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorErrorInformation errorErrorInformation = (ErrorErrorInformation) o;
        return Objects.equals(this.errorCode, errorErrorInformation.errorCode) && Objects.equals(this.errorDescription, errorErrorInformation.errorDescription) &&
                   Objects.equals(this.extensionList, errorErrorInformation.extensionList);
    }

    /**
     **/
    public ErrorErrorInformation errorCode(Integer errorCode) {

        this.errorCode = errorCode;
        return this;
    }

    /**
     **/
    public ErrorErrorInformation errorDescription(String errorDescription) {

        this.errorDescription = errorDescription;
        return this;
    }

    /**
     **/
    public ErrorErrorInformation extensionList(ErrorErrorInformationExtensionList extensionList) {

        this.extensionList = extensionList;
        return this;
    }

    @JsonProperty("errorCode")
    public Integer getErrorCode() {

        return errorCode;
    }

    @JsonProperty("errorCode")
    public void setErrorCode(Integer errorCode) {

        this.errorCode = errorCode;
    }

    @JsonProperty("errorDescription")
    public String getErrorDescription() {

        return errorDescription;
    }

    @JsonProperty("errorDescription")
    public void setErrorDescription(String errorDescription) {

        this.errorDescription = errorDescription;
    }

    @JsonProperty("extensionList")
    @Valid
    public ErrorErrorInformationExtensionList getExtensionList() {

        return extensionList;
    }

    @JsonProperty("extensionList")
    public void setExtensionList(ErrorErrorInformationExtensionList extensionList) {

        this.extensionList = extensionList;
    }

    @Override
    public int hashCode() {

        return Objects.hash(errorCode, errorDescription, extensionList);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ErrorErrorInformation {\n");

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

