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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonTypeName("SettlementWindow")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              date = "2025-11-09T11:40:06.177887+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class SettlementWindow {

    private Integer id;

    private String reason;

    private String state;

    private String createdDate;

    private String changedDate;

    private @Valid List<@Valid SettlementWindowContent> content = new ArrayList<>();

    public SettlementWindow() {

    }

    @JsonCreator
    public SettlementWindow(@JsonProperty(required = true, value = "id") Integer id,
                            @JsonProperty(required = true, value = "state") String state,
                            @JsonProperty(required = true, value = "createdDate") String createdDate) {

        this.id = id;
        this.state = state;
        this.createdDate = createdDate;
    }

    public SettlementWindow addContentItem(SettlementWindowContent contentItem) {

        if (this.content == null) {
            this.content = new ArrayList<>();
        }

        this.content.add(contentItem);
        return this;
    }

    /**
     **/
    public SettlementWindow changedDate(String changedDate) {

        this.changedDate = changedDate;
        return this;
    }

    /**
     **/
    public SettlementWindow content(List<@Valid SettlementWindowContent> content) {

        this.content = content;
        return this;
    }

    /**
     **/
    public SettlementWindow createdDate(String createdDate) {

        this.createdDate = createdDate;
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
        SettlementWindow settlementWindow = (SettlementWindow) o;
        return Objects.equals(this.id, settlementWindow.id) && Objects.equals(this.reason, settlementWindow.reason) && Objects.equals(this.state, settlementWindow.state) &&
                   Objects.equals(this.createdDate, settlementWindow.createdDate) && Objects.equals(this.changedDate, settlementWindow.changedDate) &&
                   Objects.equals(this.content, settlementWindow.content);
    }

    @JsonProperty("changedDate")
    public String getChangedDate() {

        return changedDate;
    }

    @JsonProperty("changedDate")
    public void setChangedDate(String changedDate) {

        this.changedDate = changedDate;
    }

    @JsonProperty("content")
    @Valid
    public List<@Valid SettlementWindowContent> getContent() {

        return content;
    }

    @JsonProperty("content")
    public void setContent(List<@Valid SettlementWindowContent> content) {

        this.content = content;
    }

    @JsonProperty(required = true, value = "createdDate")
    @NotNull
    public String getCreatedDate() {

        return createdDate;
    }

    @JsonProperty(required = true, value = "createdDate")
    public void setCreatedDate(String createdDate) {

        this.createdDate = createdDate;
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

    @JsonProperty("reason")
    public String getReason() {

        return reason;
    }

    @JsonProperty("reason")
    public void setReason(String reason) {

        this.reason = reason;
    }

    @JsonProperty(required = true, value = "state")
    @NotNull
    public String getState() {

        return state;
    }

    @JsonProperty(required = true, value = "state")
    public void setState(String state) {

        this.state = state;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, reason, state, createdDate, changedDate, content);
    }

    /**
     **/
    public SettlementWindow id(Integer id) {

        this.id = id;
        return this;
    }

    /**
     **/
    public SettlementWindow reason(String reason) {

        this.reason = reason;
        return this;
    }

    public SettlementWindow removeContentItem(SettlementWindowContent contentItem) {

        if (contentItem != null && this.content != null) {
            this.content.remove(contentItem);
        }

        return this;
    }

    /**
     **/
    public SettlementWindow state(String state) {

        this.state = state;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class SettlementWindow {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    createdDate: ").append(toIndentedString(createdDate)).append("\n");
        sb.append("    changedDate: ").append(toIndentedString(changedDate)).append("\n");
        sb.append("    content: ").append(toIndentedString(content)).append("\n");
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

