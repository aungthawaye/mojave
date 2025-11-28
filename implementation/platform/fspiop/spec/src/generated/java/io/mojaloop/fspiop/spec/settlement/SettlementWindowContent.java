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

@JsonTypeName("SettlementWindowContent")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              date = "2025-11-22T11:20:03.781165+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class SettlementWindowContent {

    private Integer id;

    private String state;

    private String ledgerAccountType;

    private String currencyId;

    private String createdDate;

    private String changedDate;

    private Integer settlementId;

    public SettlementWindowContent() {

    }

    @JsonCreator
    public SettlementWindowContent(@JsonProperty(required = true, value = "id") Integer id,
                                   @JsonProperty(required = true, value = "state") String state,
                                   @JsonProperty(required = true,
                                                 value = "ledgerAccountType") String ledgerAccountType,
                                   @JsonProperty(required = true,
                                                 value = "currencyId") String currencyId,
                                   @JsonProperty(required = true,
                                                 value = "createdDate") String createdDate) {

        this.id = id;
        this.state = state;
        this.ledgerAccountType = ledgerAccountType;
        this.currencyId = currencyId;
        this.createdDate = createdDate;
    }
    private String toIndentedString(Object o) {

        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

