package io.mojaloop.common.fspiop.core.model;

import io.mojaloop.common.fspiop.core.model.Currency;
import io.mojaloop.common.fspiop.core.model.PartyIdInfo;
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

/**
 * The object sent in the POST /participants request.
 **/

@JsonTypeName("ParticipantsPostRequest")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-08T15:50:15.331321+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class ParticipantsPostRequest   {
  private String requestId;
  private @Valid List<@Valid PartyIdInfo> partyList = new ArrayList<>();
  private Currency currency;

  public ParticipantsPostRequest() {
  }

  @JsonCreator
  public ParticipantsPostRequest(
    @JsonProperty(required = true, value = "requestId") String requestId,
    @JsonProperty(required = true, value = "partyList") List<@Valid PartyIdInfo> partyList
  ) {
    this.requestId = requestId;
    this.partyList = partyList;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public ParticipantsPostRequest requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  
  @JsonProperty(required = true, value = "requestId")
  @NotNull  @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getRequestId() {
    return requestId;
  }

  @JsonProperty(required = true, value = "requestId")
  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  /**
   * List of PartyIdInfo elements that the client would like to update or create FSP information about.
   **/
  public ParticipantsPostRequest partyList(List<@Valid PartyIdInfo> partyList) {
    this.partyList = partyList;
    return this;
  }

  
  @JsonProperty(required = true, value = "partyList")
  @NotNull @Valid  @Size(min=1,max=10000)public List<@Valid PartyIdInfo> getPartyList() {
    return partyList;
  }

  @JsonProperty(required = true, value = "partyList")
  public void setPartyList(List<@Valid PartyIdInfo> partyList) {
    this.partyList = partyList;
  }

  public ParticipantsPostRequest addPartyListItem(PartyIdInfo partyListItem) {
    if (this.partyList == null) {
      this.partyList = new ArrayList<>();
    }

    this.partyList.add(partyListItem);
    return this;
  }

  public ParticipantsPostRequest removePartyListItem(PartyIdInfo partyListItem) {
    if (partyListItem != null && this.partyList != null) {
      this.partyList.remove(partyListItem);
    }

    return this;
  }
  /**
   **/
  public ParticipantsPostRequest currency(Currency currency) {
    this.currency = currency;
    return this;
  }

  
  @JsonProperty("currency")
  public Currency getCurrency() {
    return currency;
  }

  @JsonProperty("currency")
  public void setCurrency(Currency currency) {
    this.currency = currency;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParticipantsPostRequest participantsPostRequest = (ParticipantsPostRequest) o;
    return Objects.equals(this.requestId, participantsPostRequest.requestId) &&
        Objects.equals(this.partyList, participantsPostRequest.partyList) &&
        Objects.equals(this.currency, participantsPostRequest.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, partyList, currency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ParticipantsPostRequest {\n");
    
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    partyList: ").append(toIndentedString(partyList)).append("\n");
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

