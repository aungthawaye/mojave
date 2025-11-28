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
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@JsonTypeName("Extension")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              date = "2025-11-22T11:20:03.781165+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class Extension {

    private String key;

    private String value;

    public Extension() {

    }

    @JsonCreator
    public Extension(@JsonProperty(required = true, value = "key") String key,
                     @JsonProperty(required = true, value = "value") String value) {

        this.key = key;
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

    @JsonProperty(required = true, value = "key")
    @NotNull
    public String getKey() {

        return key;
    }

    @JsonProperty(required = true, value = "key")
    public void setKey(String key) {

        this.key = key;
    }

    @JsonProperty(required = true, value = "value")
    @NotNull
    public String getValue() {

        return value;
    }

    @JsonProperty(required = true, value = "value")
    public void setValue(String value) {

        this.value = value;
    }

    @Override
    public int hashCode() {

        return Objects.hash(key, value);
    }
    private String toIndentedString(Object o) {

        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

