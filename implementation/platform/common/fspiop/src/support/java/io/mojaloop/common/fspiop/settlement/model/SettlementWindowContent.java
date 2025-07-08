package io.mojaloop.common.fspiop.settlement.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("SettlementWindowContent")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-08T15:50:16.236200+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class SettlementWindowContent   {
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
  public SettlementWindowContent(
    @JsonProperty(required = true, value = "id") Integer id,
    @JsonProperty(required = true, value = "state") String state,
    @JsonProperty(required = true, value = "ledgerAccountType") String ledgerAccountType,
    @JsonProperty(required = true, value = "currencyId") String currencyId,
    @JsonProperty(required = true, value = "createdDate") String createdDate
  ) {
    this.id = id;
    this.state = state;
    this.ledgerAccountType = ledgerAccountType;
    this.currencyId = currencyId;
    this.createdDate = createdDate;
  }

  /**
   **/
  public SettlementWindowContent id(Integer id) {
    this.id = id;
    return this;
  }

  
  @JsonProperty(required = true, value = "id")
  @NotNull public Integer getId() {
    return id;
  }

  @JsonProperty(required = true, value = "id")
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   **/
  public SettlementWindowContent state(String state) {
    this.state = state;
    return this;
  }

  
  @JsonProperty(required = true, value = "state")
  @NotNull public String getState() {
    return state;
  }

  @JsonProperty(required = true, value = "state")
  public void setState(String state) {
    this.state = state;
  }

  /**
   **/
  public SettlementWindowContent ledgerAccountType(String ledgerAccountType) {
    this.ledgerAccountType = ledgerAccountType;
    return this;
  }

  
  @JsonProperty(required = true, value = "ledgerAccountType")
  @NotNull public String getLedgerAccountType() {
    return ledgerAccountType;
  }

  @JsonProperty(required = true, value = "ledgerAccountType")
  public void setLedgerAccountType(String ledgerAccountType) {
    this.ledgerAccountType = ledgerAccountType;
  }

  /**
   **/
  public SettlementWindowContent currencyId(String currencyId) {
    this.currencyId = currencyId;
    return this;
  }

  
  @JsonProperty(required = true, value = "currencyId")
  @NotNull public String getCurrencyId() {
    return currencyId;
  }

  @JsonProperty(required = true, value = "currencyId")
  public void setCurrencyId(String currencyId) {
    this.currencyId = currencyId;
  }

  /**
   **/
  public SettlementWindowContent createdDate(String createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  
  @JsonProperty(required = true, value = "createdDate")
  @NotNull public String getCreatedDate() {
    return createdDate;
  }

  @JsonProperty(required = true, value = "createdDate")
  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   **/
  public SettlementWindowContent changedDate(String changedDate) {
    this.changedDate = changedDate;
    return this;
  }

  
  @JsonProperty("changedDate")
  public String getChangedDate() {
    return changedDate;
  }

  @JsonProperty("changedDate")
  public void setChangedDate(String changedDate) {
    this.changedDate = changedDate;
  }

  /**
   **/
  public SettlementWindowContent settlementId(Integer settlementId) {
    this.settlementId = settlementId;
    return this;
  }

  
  @JsonProperty("settlementId")
  public Integer getSettlementId() {
    return settlementId;
  }

  @JsonProperty("settlementId")
  public void setSettlementId(Integer settlementId) {
    this.settlementId = settlementId;
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
    return Objects.equals(this.id, settlementWindowContent.id) &&
        Objects.equals(this.state, settlementWindowContent.state) &&
        Objects.equals(this.ledgerAccountType, settlementWindowContent.ledgerAccountType) &&
        Objects.equals(this.currencyId, settlementWindowContent.currencyId) &&
        Objects.equals(this.createdDate, settlementWindowContent.createdDate) &&
        Objects.equals(this.changedDate, settlementWindowContent.changedDate) &&
        Objects.equals(this.settlementId, settlementWindowContent.settlementId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, state, ledgerAccountType, currencyId, createdDate, changedDate, settlementId);
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

