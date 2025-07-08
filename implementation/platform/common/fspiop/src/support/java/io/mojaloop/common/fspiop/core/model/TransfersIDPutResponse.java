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
 * The object sent in the PUT /transfers/{ID} callback.
 **/

@JsonTypeName("TransfersIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-08T15:50:15.331321+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class TransfersIDPutResponse   {
  private String fulfilment;
  private String completedTimestamp;
  private TransferState transferState;
  private ExtensionList extensionList;

  public TransfersIDPutResponse() {
  }

  @JsonCreator
  public TransfersIDPutResponse(
    @JsonProperty(required = true, value = "transferState") TransferState transferState
  ) {
    this.transferState = transferState;
  }

  /**
   * Fulfilment that must be attached to the transfer by the Payee.
   **/
  public TransfersIDPutResponse fulfilment(String fulfilment) {
    this.fulfilment = fulfilment;
    return this;
  }

  
  @JsonProperty("fulfilment")
   @Pattern(regexp="^[A-Za-z0-9-_]{43}$") @Size(max=48)public String getFulfilment() {
    return fulfilment;
  }

  @JsonProperty("fulfilment")
  public void setFulfilment(String fulfilment) {
    this.fulfilment = fulfilment;
  }

  /**
   * The API data type DateTime is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons. The format is according to [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html), expressed in a combined date, time and time zone format. A more readable version of the format is yyyy-MM-ddTHH:mm:ss.SSS[-HH:MM]. Examples are \&quot;2016-05-24T08:38:08.699-04:00\&quot;, \&quot;2016-05-24T08:38:08.699Z\&quot; (where Z indicates Zulu time zone, same as UTC).
   **/
  public TransfersIDPutResponse completedTimestamp(String completedTimestamp) {
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
  public TransfersIDPutResponse transferState(TransferState transferState) {
    this.transferState = transferState;
    return this;
  }

  
  @JsonProperty(required = true, value = "transferState")
  @NotNull public TransferState getTransferState() {
    return transferState;
  }

  @JsonProperty(required = true, value = "transferState")
  public void setTransferState(TransferState transferState) {
    this.transferState = transferState;
  }

  /**
   **/
  public TransfersIDPutResponse extensionList(ExtensionList extensionList) {
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
    TransfersIDPutResponse transfersIDPutResponse = (TransfersIDPutResponse) o;
    return Objects.equals(this.fulfilment, transfersIDPutResponse.fulfilment) &&
        Objects.equals(this.completedTimestamp, transfersIDPutResponse.completedTimestamp) &&
        Objects.equals(this.transferState, transfersIDPutResponse.transferState) &&
        Objects.equals(this.extensionList, transfersIDPutResponse.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fulfilment, completedTimestamp, transferState, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransfersIDPutResponse {\n");
    
    sb.append("    fulfilment: ").append(toIndentedString(fulfilment)).append("\n");
    sb.append("    completedTimestamp: ").append(toIndentedString(completedTimestamp)).append("\n");
    sb.append("    transferState: ").append(toIndentedString(transferState)).append("\n");
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

