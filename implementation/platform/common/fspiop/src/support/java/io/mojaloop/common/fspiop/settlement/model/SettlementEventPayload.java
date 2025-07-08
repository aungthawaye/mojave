package io.mojaloop.common.fspiop.settlement.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.mojaloop.common.fspiop.settlement.model.SettlementWindowId;
import java.util.LinkedHashSet;
import java.util.Set;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("SettlementEventPayload")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-08T15:50:16.236200+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class SettlementEventPayload   {
  private String settlementModel;
  private String reason;
  private @Valid Set<@Valid SettlementWindowId> settlementWindows = new LinkedHashSet<>();

  public SettlementEventPayload() {
  }

  @JsonCreator
  public SettlementEventPayload(
    @JsonProperty(required = true, value = "settlementModel") String settlementModel,
    @JsonProperty(required = true, value = "reason") String reason,
    @JsonProperty(required = true, value = "settlementWindows") Set<@Valid SettlementWindowId> settlementWindows
  ) {
    this.settlementModel = settlementModel;
    this.reason = reason;
    this.settlementWindows = settlementWindows;
  }

  /**
   **/
  public SettlementEventPayload settlementModel(String settlementModel) {
    this.settlementModel = settlementModel;
    return this;
  }

  
  @JsonProperty(required = true, value = "settlementModel")
  @NotNull public String getSettlementModel() {
    return settlementModel;
  }

  @JsonProperty(required = true, value = "settlementModel")
  public void setSettlementModel(String settlementModel) {
    this.settlementModel = settlementModel;
  }

  /**
   **/
  public SettlementEventPayload reason(String reason) {
    this.reason = reason;
    return this;
  }

  
  @JsonProperty(required = true, value = "reason")
  @NotNull public String getReason() {
    return reason;
  }

  @JsonProperty(required = true, value = "reason")
  public void setReason(String reason) {
    this.reason = reason;
  }

  /**
   **/
  public SettlementEventPayload settlementWindows(Set<@Valid SettlementWindowId> settlementWindows) {
    this.settlementWindows = settlementWindows;
    return this;
  }

  
  @JsonProperty(required = true, value = "settlementWindows")
  @NotNull @Valid  @Size(min=1)public Set<@Valid SettlementWindowId> getSettlementWindows() {
    return settlementWindows;
  }

  @JsonProperty(required = true, value = "settlementWindows")
  @JsonDeserialize(as = LinkedHashSet.class)
  public void setSettlementWindows(Set<@Valid SettlementWindowId> settlementWindows) {
    this.settlementWindows = settlementWindows;
  }

  public SettlementEventPayload addSettlementWindowsItem(SettlementWindowId settlementWindowsItem) {
    if (this.settlementWindows == null) {
      this.settlementWindows = new LinkedHashSet<>();
    }

    this.settlementWindows.add(settlementWindowsItem);
    return this;
  }

  public SettlementEventPayload removeSettlementWindowsItem(SettlementWindowId settlementWindowsItem) {
    if (settlementWindowsItem != null && this.settlementWindows != null) {
      this.settlementWindows.remove(settlementWindowsItem);
    }

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
    SettlementEventPayload settlementEventPayload = (SettlementEventPayload) o;
    return Objects.equals(this.settlementModel, settlementEventPayload.settlementModel) &&
        Objects.equals(this.reason, settlementEventPayload.reason) &&
        Objects.equals(this.settlementWindows, settlementEventPayload.settlementWindows);
  }

  @Override
  public int hashCode() {
    return Objects.hash(settlementModel, reason, settlementWindows);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SettlementEventPayload {\n");
    
    sb.append("    settlementModel: ").append(toIndentedString(settlementModel)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    settlementWindows: ").append(toIndentedString(settlementWindows)).append("\n");
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

