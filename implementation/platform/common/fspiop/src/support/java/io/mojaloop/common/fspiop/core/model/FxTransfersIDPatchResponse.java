package io.mojaloop.common.fspiop.core.model;

import io.mojaloop.common.fspiop.core.model.ExtensionList;
import io.mojaloop.common.fspiop.core.model.TransferState;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * PATCH /fxTransfers/{ID} object
 **/

@JsonTypeName("FxTransfersIDPatchResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-08T15:50:15.331321+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class FxTransfersIDPatchResponse   {
  private String completedTimestamp;
  private TransferState conversionState;
  private ExtensionList extensionList;

  public FxTransfersIDPatchResponse() {
  }

  @JsonCreator
  public FxTransfersIDPatchResponse(
    @JsonProperty(required = true, value = "conversionState") TransferState conversionState
  ) {
    this.conversionState = conversionState;
  }

  /**
   * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
   **/
  public FxTransfersIDPatchResponse completedTimestamp(String completedTimestamp) {
    this.completedTimestamp = completedTimestamp;
    return this;
  }

  
  @JsonProperty("completedTimestamp")
   @Pattern(regexp="^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:(\\.\\d{3}))(?:Z|[+-][01]\\d:[0-5]\\d)$")public String getCompletedTimestamp() {
    return completedTimestamp;
  }

  @JsonProperty("completedTimestamp")
  public void setCompletedTimestamp(String completedTimestamp) {
    this.completedTimestamp = completedTimestamp;
  }

  /**
   **/
  public FxTransfersIDPatchResponse conversionState(TransferState conversionState) {
    this.conversionState = conversionState;
    return this;
  }

  
  @JsonProperty(required = true, value = "conversionState")
  @NotNull public TransferState getConversionState() {
    return conversionState;
  }

  @JsonProperty(required = true, value = "conversionState")
  public void setConversionState(TransferState conversionState) {
    this.conversionState = conversionState;
  }

  /**
   **/
  public FxTransfersIDPatchResponse extensionList(ExtensionList extensionList) {
    this.extensionList = extensionList;
    return this;
  }

  
  @JsonProperty("extensionList")
  @Valid public ExtensionList getExtensionList() {
    return extensionList;
  }

  @JsonProperty("extensionList")
  public void setExtensionList(ExtensionList extensionList) {
    this.extensionList = extensionList;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FxTransfersIDPatchResponse fxTransfersIDPatchResponse = (FxTransfersIDPatchResponse) o;
    return Objects.equals(this.completedTimestamp, fxTransfersIDPatchResponse.completedTimestamp) &&
        Objects.equals(this.conversionState, fxTransfersIDPatchResponse.conversionState) &&
        Objects.equals(this.extensionList, fxTransfersIDPatchResponse.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(completedTimestamp, conversionState, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FxTransfersIDPatchResponse {\n");
    
    sb.append("    completedTimestamp: ").append(toIndentedString(completedTimestamp)).append("\n");
    sb.append("    conversionState: ").append(toIndentedString(conversionState)).append("\n");
    sb.append("    extensionList: ").append(toIndentedString(extensionList)).append("\n");
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

