package io.mojaloop.common.fspiop.core.model;

import io.mojaloop.common.fspiop.core.model.ExtensionList;
import io.mojaloop.common.fspiop.core.model.TransactionRequestState;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * The object sent in the PUT /transactionRequests/{ID} callback.
 **/

@JsonTypeName("TransactionRequestsIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-09T06:35:16.332603+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class TransactionRequestsIDPutResponse   {
  private String transactionId;
  private TransactionRequestState transactionRequestState;
  private ExtensionList extensionList;

  public TransactionRequestsIDPutResponse() {
  }

  @JsonCreator
  public TransactionRequestsIDPutResponse(
    @JsonProperty(required = true, value = "transactionRequestState") TransactionRequestState transactionRequestState
  ) {
    this.transactionRequestState = transactionRequestState;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public TransactionRequestsIDPutResponse transactionId(String transactionId) {
    this.transactionId = transactionId;
    return this;
  }

  
  @JsonProperty("transactionId")
   @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getTransactionId() {
    return transactionId;
  }

  @JsonProperty("transactionId")
  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  /**
   **/
  public TransactionRequestsIDPutResponse transactionRequestState(TransactionRequestState transactionRequestState) {
    this.transactionRequestState = transactionRequestState;
    return this;
  }

  
  @JsonProperty(required = true, value = "transactionRequestState")
  @NotNull public TransactionRequestState getTransactionRequestState() {
    return transactionRequestState;
  }

  @JsonProperty(required = true, value = "transactionRequestState")
  public void setTransactionRequestState(TransactionRequestState transactionRequestState) {
    this.transactionRequestState = transactionRequestState;
  }

  /**
   **/
  public TransactionRequestsIDPutResponse extensionList(ExtensionList extensionList) {
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
    TransactionRequestsIDPutResponse transactionRequestsIDPutResponse = (TransactionRequestsIDPutResponse) o;
    return Objects.equals(this.transactionId, transactionRequestsIDPutResponse.transactionId) &&
        Objects.equals(this.transactionRequestState, transactionRequestsIDPutResponse.transactionRequestState) &&
        Objects.equals(this.extensionList, transactionRequestsIDPutResponse.extensionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactionId, transactionRequestState, extensionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransactionRequestsIDPutResponse {\n");
    
    sb.append("    transactionId: ").append(toIndentedString(transactionId)).append("\n");
    sb.append("    transactionRequestState: ").append(toIndentedString(transactionRequestState)).append("\n");
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

