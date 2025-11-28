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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonTypeName("Error_errorInformation_extensionList")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              date = "2025-11-22T11:20:03.781165+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class ErrorErrorInformationExtensionList {

    private @Valid List<@Valid Extension> extension = new ArrayList<>();

    public ErrorErrorInformationExtensionList() {

    }

    public ErrorErrorInformationExtensionList addExtensionItem(Extension extensionItem) {

        if (this.extension == null) {
            this.extension = new ArrayList<>();
        }

        this.extension.add(extensionItem);
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
        ErrorErrorInformationExtensionList errorErrorInformationExtensionList = (ErrorErrorInformationExtensionList) o;
        return Objects.equals(this.extension, errorErrorInformationExtensionList.extension);
    }
    private String toIndentedString(Object o) {

        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

