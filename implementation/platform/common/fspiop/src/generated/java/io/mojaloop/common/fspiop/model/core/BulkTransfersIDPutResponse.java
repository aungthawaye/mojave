package io.mojaloop.common.fspiop.model.core;

import io.mojaloop.common.fspiop.model.core.BulkTransferState;
import io.mojaloop.common.fspiop.model.core.ExtensionList;
import io.mojaloop.common.fspiop.model.core.IndividualTransferResult;
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
 * The object sent in the PUT /bulkTransfers/{ID} callback.
 **/

@JsonTypeName("BulkTransfersIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:32.899106+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class BulkTransfersIDPutResponse   {
  private String completedTimestamp;
  private @Valid List<@Valid IndividualTransferResult> individualTransferResults = new ArrayList<>();
  private BulkTransferState bulkTransferState;
  private ExtensionList extensionList;

  public BulkTransfersIDPutResponse() {
  }

  @JsonCreator
  public BulkTransfersIDPutResponse(
    @JsonProperty(required = true, value = "bulkTransferState") BulkTransferState bulkTransferState
  ) {
    this.bulkTransferState = bulkTransferState;
  }

  /**
   * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
   **/
  public BulkTransfersIDPutResponse completedTimestamp(String completedTimestamp) {
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
   * List of IndividualTransferResult elements.
   **/
  public BulkTransfersIDPutResponse individualTransferResults(List<@Valid IndividualTransferResult> individualTransferResults) {
    this.individualTransferResults = individualTransferResults;
    return this;
  }

  
  @JsonProperty("individualTransferResults")
  @Valid  @Size(max=1000)public List<@Valid IndividualTransferResult> getIndividualTransferResults() {
    return individualTransferResults;
  }

  @JsonProperty("individualTransferResults")
  public void setIndividualTransferResults(List<@Valid IndividualTransferResult> individualTransferResults) {
    this.individualTransferResults = individualTransferResults;
  }

  public BulkTransfersIDPutResponse addIndividualTransferResultsItem(IndividualTransferResult individualTransferResultsItem) {
    if (this.individualTransferResults == null) {
      this.individualTransferResults = new ArrayList<>();
    }

    this.individualTransferResults.add(individualTransferResultsItem);
    return this;
  }

  public BulkTransfersIDPutResponse removeIndividualTransferResultsItem(IndividualTransferResult individualTransferResultsItem) {
    if (individualTransferResultsItem != null && this.individualTransferResults != null) {
      this.individualTransferResults.remove(individualTransferResultsItem);
    }

    return this;
  }
  /**
   **/
  public BulkTransfersIDPutResponse bulkTransferState(BulkTransferState bulkTransferState) {
    this.bulkTransferState = bulkTransferState;
    return this;
  }

  
  @JsonProperty(required = true, value = "bulkTransferState")
  @NotNull public BulkTransferState getBulkTransferState() {
    return bulkTransferState;
  }

  @JsonProperty(required = true, value = "bulkTransferState")
  public void setBulkTransferState(BulkTransferState bulkTransferState) {
    this.bulkTransferState = bulkTransferState;
  }

  /**
   **/
  public BulkTransfersIDPutResponse extensionList(ExtensionList extensionList) {
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
    BulkTransfersIDPutResponse bulkTransfersIDPutResponse = (BulkTransfersIDPutResponse) o;
    return Objects.equals(this.completedTimestamp, bulkTransfersIDPutResponse.completedTimestamp) &&
        Objects.equals(this.individualTransferResults, bulkTransfersIDPutResponse.individualTransferResults) &&
        Objects.equals(this.bulkTransferState, bulkTransfersIDPutResponse.bulkTransferState) &&
        Objects.equals(this.extensionList, bulkTransfersIDPutResponse.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(completedTimestamp, individualTransferResults, bulkTransferState, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BulkTransfersIDPutResponse {\n");
    
    sb.append("    completedTimestamp: ").append(toIndentedString(completedTimestamp)).append("\n");
    sb.append("    individualTransferResults: ").append(toIndentedString(individualTransferResults)).append("\n");
    sb.append("    bulkTransferState: ").append(toIndentedString(bulkTransferState)).append("\n");
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

