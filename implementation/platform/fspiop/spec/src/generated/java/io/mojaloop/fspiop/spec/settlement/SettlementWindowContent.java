package io.mojaloop.fspiop.spec.settlement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@JsonTypeName("SettlementWindowContent")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              date = "2025-08-04T08:01:35.765568+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
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
                                   @JsonProperty(required = true, value = "ledgerAccountType") String ledgerAccountType,
                                   @JsonProperty(required = true, value = "currencyId") String currencyId,
                                   @JsonProperty(required = true, value = "createdDate") String createdDate) {

        this.id = id;
        this.state = state;
        this.ledgerAccountType = ledgerAccountType;
        this.currencyId = currencyId;
        this.createdDate = createdDate;
    }

    /**
     **/
    public SettlementWindowContent changedDate(String changedDate) {

        this.changedDate = changedDate;
        return this;
    }

    /**
     **/
    public SettlementWindowContent createdDate(String createdDate) {

        this.createdDate = createdDate;
        return this;
    }

    /**
     **/
    public SettlementWindowContent currencyId(String currencyId) {

        this.currencyId = currencyId;
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
        SettlementWindowContent settlementWindowContent = (SettlementWindowContent) o;
        return Objects.equals(this.id, settlementWindowContent.id) && Objects.equals(this.state, settlementWindowContent.state) &&
                   Objects.equals(this.ledgerAccountType, settlementWindowContent.ledgerAccountType) &&
                   Objects.equals(this.currencyId, settlementWindowContent.currencyId) &&
                   Objects.equals(this.createdDate, settlementWindowContent.createdDate) &&
                   Objects.equals(this.changedDate, settlementWindowContent.changedDate) &&
                   Objects.equals(this.settlementId, settlementWindowContent.settlementId);
    }

    @JsonProperty("changedDate")
    public String getChangedDate() {

        return changedDate;
    }

    @JsonProperty("changedDate")
    public void setChangedDate(String changedDate) {

        this.changedDate = changedDate;
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

    @JsonProperty(required = true, value = "currencyId")
    @NotNull
    public String getCurrencyId() {

        return currencyId;
    }

    @JsonProperty(required = true, value = "currencyId")
    public void setCurrencyId(String currencyId) {

        this.currencyId = currencyId;
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

    @JsonProperty(required = true, value = "ledgerAccountType")
    @NotNull
    public String getLedgerAccountType() {

        return ledgerAccountType;
    }

    @JsonProperty(required = true, value = "ledgerAccountType")
    public void setLedgerAccountType(String ledgerAccountType) {

        this.ledgerAccountType = ledgerAccountType;
    }

    @JsonProperty("settlementId")
    public Integer getSettlementId() {

        return settlementId;
    }

    @JsonProperty("settlementId")
    public void setSettlementId(Integer settlementId) {

        this.settlementId = settlementId;
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

        return Objects.hash(id, state, ledgerAccountType, currencyId, createdDate, changedDate, settlementId);
    }

    /**
     **/
    public SettlementWindowContent id(Integer id) {

        this.id = id;
        return this;
    }

    /**
     **/
    public SettlementWindowContent ledgerAccountType(String ledgerAccountType) {

        this.ledgerAccountType = ledgerAccountType;
        return this;
    }

    /**
     **/
    public SettlementWindowContent settlementId(Integer settlementId) {

        this.settlementId = settlementId;
        return this;
    }

    /**
     **/
    public SettlementWindowContent state(String state) {

        this.state = state;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class SettlementWindowContent {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    ledgerAccountType: ").append(toIndentedString(ledgerAccountType)).append("\n");
        sb.append("    currencyId: ").append(toIndentedString(currencyId)).append("\n");
        sb.append("    createdDate: ").append(toIndentedString(createdDate)).append("\n");
        sb.append("    changedDate: ").append(toIndentedString(changedDate)).append("\n");
        sb.append("    settlementId: ").append(toIndentedString(settlementId)).append("\n");
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

