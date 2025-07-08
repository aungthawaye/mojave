package io.mojaloop.common.fspiop.core.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * The object sent in the PUT /participants/{Type}/{ID}/{SubId} and /participants/{Type}/{ID} callbacks.
 **/

@JsonTypeName("ParticipantsTypeIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-08T15:50:15.331321+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class ParticipantsTypeIDPutResponse   {
  private String fspId;

  public ParticipantsTypeIDPutResponse() {
  }

  /**
   * FSP identifier.
   **/
  public ParticipantsTypeIDPutResponse fspId(String fspId) {
    this.fspId = fspId;
    return this;
  }

  
  @JsonProperty("fspId")
   @Size(min=1,max=32)public String getFspId() {
    return fspId;
  }

  @JsonProperty("fspId")
  public void setFspId(String fspId) {
    this.fspId = fspId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParticipantsTypeIDPutResponse participantsTypeIDPutResponse = (ParticipantsTypeIDPutResponse) o;
    return Objects.equals(this.fspId, participantsTypeIDPutResponse.fspId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fspId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ParticipantsTypeIDPutResponse {\n");
    
    sb.append("    fspId: ").append(toIndentedString(fspId)).append("\n");
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

