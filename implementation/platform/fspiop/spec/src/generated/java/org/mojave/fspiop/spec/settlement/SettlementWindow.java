/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.fspiop.spec.settlement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.mojave.fspiop.spec.settlement.SettlementWindowContent;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("SettlementWindow")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-12-16T10:02:26.115253+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class SettlementWindow   {
  private Integer id;
  private String reason;
  private String state;
  private String createdDate;
  private String changedDate;
  private @Valid List<@Valid SettlementWindowContent> content = new ArrayList<>();

  public SettlementWindow() {
  }

  @JsonCreator
  public SettlementWindow(
    @JsonProperty(required = true, value = "id") Integer id,
    @JsonProperty(required = true, value = "state") String state,
    @JsonProperty(required = true, value = "createdDate") String createdDate
  ) {
    this.id = id;
    this.state = state;
    this.createdDate = createdDate;
  }
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


}

