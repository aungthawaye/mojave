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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.Objects;

@JsonTypeName("netSettlementAmount")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-09-08T15:21:55.746682+06:30[Asia/Rangoon]",
                              comments = "Generator version: 7.13.0")
public class NetSettlementAmount {

    private BigDecimal amount;

    private String currency;

    public NetSettlementAmount() {

    }

    /**
     **/
    public NetSettlementAmount amount(BigDecimal amount) {

        this.amount = amount;
        return this;
    }

    /**
     **/
    public NetSettlementAmount currency(String currency) {

        this.currency = currency;
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
        NetSettlementAmount netSettlementAmount = (NetSettlementAmount) o;
        return Objects.equals(this.amount, netSettlementAmount.amount) &&
                   Objects.equals(this.currency, netSettlementAmount.currency);
    }

    @JsonProperty("amount")
    @Valid
    public BigDecimal getAmount() {

        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(BigDecimal amount) {

        this.amount = amount;
    }

    @JsonProperty("currency")
    public String getCurrency() {

        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {

        this.currency = currency;
    }

    @Override
    public int hashCode() {

        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {

        String sb = "class NetSettlementAmount {\n" +
                        "    amount: " + toIndentedString(amount) + "\n" +
                        "    currency: " + toIndentedString(currency) + "\n" +
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

