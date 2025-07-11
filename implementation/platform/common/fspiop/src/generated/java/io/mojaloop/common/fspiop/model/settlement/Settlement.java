package io.mojaloop.common.fspiop.model.settlement;

import io.mojaloop.common.fspiop.model.settlement.Participant;
import io.mojaloop.common.fspiop.model.settlement.SettlementWindow;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("Settlement")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:33.786087+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class Settlement   {
  private Integer id;
  private String state;
  private @Valid List<List<@Valid SettlementWindow>> settlementWindows = new ArrayList<>();
  private @Valid List<@Valid Participant> participants = new ArrayList<>();

  public Settlement() {
  }

  /**
   **/
  public Settlement id(Integer id) {
    this.id = id;
    return this;
  }

  
  @JsonProperty("id")
  public Integer getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   **/
  public Settlement state(String state) {
    this.state = state;
    return this;
  }

  
  @JsonProperty("state")
  public String getState() {
    return state;
  }

  @JsonProperty("state")
  public void setState(String state) {
    this.state = state;
  }

  /**
   **/
  public Settlement settlementWindows(List<List<@Valid SettlementWindow>> settlementWindows) {
    this.settlementWindows = settlementWindows;
    return this;
  }

  
  @JsonProperty("settlementWindows")
  @Valid public List<@Valid List<@Valid SettlementWindow>> getSettlementWindows() {
    return settlementWindows;
  }

  @JsonProperty("settlementWindows")
  public void setSettlementWindows(List<List<@Valid SettlementWindow>> settlementWindows) {
    this.settlementWindows = settlementWindows;
  }

  public Settlement addSettlementWindowsItem(List<@Valid SettlementWindow> settlementWindowsItem) {
    if (this.settlementWindows == null) {
      this.settlementWindows = new ArrayList<>();
    }

    this.settlementWindows.add(settlementWindowsItem);
    return this;
  }

  public Settlement removeSettlementWindowsItem(List<@Valid SettlementWindow> settlementWindowsItem) {
    if (settlementWindowsItem != null && this.settlementWindows != null) {
      this.settlementWindows.remove(settlementWindowsItem);
    }

    return this;
  }
  /**
   **/
  public Settlement participants(List<@Valid Participant> participants) {
    this.participants = participants;
    return this;
  }

  
  @JsonProperty("participants")
  @Valid public List<@Valid Participant> getParticipants() {
    return participants;
  }

  @JsonProperty("participants")
  public void setParticipants(List<@Valid Participant> participants) {
    this.participants = participants;
  }

  public Settlement addParticipantsItem(Participant participantsItem) {
    if (this.participants == null) {
      this.participants = new ArrayList<>();
    }

    this.participants.add(participantsItem);
    return this;
  }

  public Settlement removeParticipantsItem(Participant participantsItem) {
    if (participantsItem != null && this.participants != null) {
      this.participants.remove(participantsItem);
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
    Settlement settlement = (Settlement) o;
    return Objects.equals(this.id, settlement.id) &&
        Objects.equals(this.state, settlement.state) &&
        Objects.equals(this.settlementWindows, settlement.settlementWindows) &&
        Objects.equals(this.participants, settlement.participants);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, state, settlementWindows, participants);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Settlement {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    settlementWindows: ").append(toIndentedString(settlementWindows)).append("\n");
    sb.append("    participants: ").append(toIndentedString(participants)).append("\n");
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

