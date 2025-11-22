package io.mojaloop.fspiop.spec.settlement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.Objects;

@JsonTypeName("netSettlementAmount")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              date = "2025-11-22T11:20:03.781165+06:30[Asia/Rangoon]",
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
        return Objects.equals(this.amount, netSettlementAmount.amount) && Objects.equals(this.currency, netSettlementAmount.currency);
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

        StringBuilder sb = new StringBuilder();
        sb.append("class NetSettlementAmount {\n");

        sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
        sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
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

