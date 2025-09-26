/*-
 * ================================================================================
 * Mojaloop OSS
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

@JsonTypeName("SettlementWindowId")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-09-08T15:21:55.746682+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class SettlementWindowId {

    private Integer id;

    public SettlementWindowId() {

    }

    @JsonCreator
    public SettlementWindowId(
        @JsonProperty(required = true, value = "id") Integer id
                             ) {

        this.id = id;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SettlementWindowId settlementWindowId = (SettlementWindowId) o;
        return Objects.equals(this.id, settlementWindowId.id);
    }

    @JsonProperty(required = true, value = "id")
    @NotNull
    public Integer getId() {

        return id;
    }

    @JsonProperty(required = true, value = "id")
    public void setId(Integer id) {

        this.id = id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    /**
     **/
    public SettlementWindowId id(Integer id) {

        this.id = id;
        return this;
    }

    @Override
    public String toString() {

        String sb = "class SettlementWindowId {\n" +
                        "    id: " + toIndentedString(id) + "\n" +
                        "}";
        return sb;
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

