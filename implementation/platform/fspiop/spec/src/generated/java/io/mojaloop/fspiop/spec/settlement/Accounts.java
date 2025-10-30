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

@JsonTypeName("Accounts")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              date = "2025-10-22T16:46:48.253622+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class Accounts {

    private Integer id;

    private String reason;

    private String state;

    private NetSettlementAmount netSettlementAmount;

    public Accounts() {

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Accounts accounts = (Accounts) o;
        return Objects.equals(this.id, accounts.id) && Objects.equals(this.reason, accounts.reason) && Objects.equals(this.state, accounts.state) &&
                   Objects.equals(this.netSettlementAmount, accounts.netSettlementAmount);
    }

    @JsonProperty("id")
    public Integer getId() {

        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {

        this.id = id;
    }

    @JsonProperty("netSettlementAmount")
    @Valid
    public NetSettlementAmount getNetSettlementAmount() {

        return netSettlementAmount;
    }

    @JsonProperty("netSettlementAmount")
    public void setNetSettlementAmount(NetSettlementAmount netSettlementAmount) {

        this.netSettlementAmount = netSettlementAmount;
    }

    @JsonProperty("reason")
    public String getReason() {

        return reason;
    }

    @JsonProperty("reason")
    public void setReason(String reason) {

        this.reason = reason;
    }

    @JsonProperty("state")
    public String getState() {

        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {

        this.state = state;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, reason, state, netSettlementAmount);
    }

    /**
     * Participant Currency Id
     **/
    public Accounts id(Integer id) {

        this.id = id;
        return this;
    }

    /**
     **/
    public Accounts netSettlementAmount(NetSettlementAmount netSettlementAmount) {

        this.netSettlementAmount = netSettlementAmount;
        return this;
    }

    /**
     * TBD
     **/
    public Accounts reason(String reason) {

        this.reason = reason;
        return this;
    }

    /**
     **/
    public Accounts state(String state) {

        this.state = state;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class Accounts {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    netSettlementAmount: ").append(toIndentedString(netSettlementAmount)).append("\n");
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

