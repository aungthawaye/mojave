package io.mojaloop.common.fspiop.core.model;

import io.mojaloop.common.fspiop.core.model.Party;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * The object sent in the PUT /parties/{Type}/{ID} callback.
 **/

@JsonTypeName("PartiesTypeIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-09T06:35:16.332603+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class PartiesTypeIDPutResponse   {
  private Party party;

  public PartiesTypeIDPutResponse() {
  }

  @JsonCreator
  public PartiesTypeIDPutResponse(
    @JsonProperty(required = true, value = "party") Party party
  ) {
    this.party = party;
  }

  /**
   **/
  public PartiesTypeIDPutResponse party(Party party) {
    this.party = party;
    return this;
  }

  
  @JsonProperty(required = true, value = "party")
  @NotNull @Valid public Party getParty() {
    return party;
  }

  @JsonProperty(required = true, value = "party")
  public void setParty(Party party) {
    this.party = party;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PartiesTypeIDPutResponse partiesTypeIDPutResponse = (PartiesTypeIDPutResponse) o;
    return Objects.equals(this.party, partiesTypeIDPutResponse.party);
  }

  @Override
  public int hashCode() {
    return Objects.hash(party);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PartiesTypeIDPutResponse {\n");
    
    sb.append("    party: ").append(toIndentedString(party)).append("\n");
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

