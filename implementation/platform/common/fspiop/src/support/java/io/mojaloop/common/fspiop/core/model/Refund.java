package io.mojaloop.common.fspiop.core.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type Refund.
 **/

@JsonTypeName("Refund")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-08T15:50:15.331321+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class Refund   {
  private String originalTransactionId;
  private String refundReason;

  public Refund() {
  }

  @JsonCreator
  public Refund(
    @JsonProperty(required = true, value = "originalTransactionId") String originalTransactionId
  ) {
    this.originalTransactionId = originalTransactionId;
  }

  /**
   * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
   **/
  public Refund originalTransactionId(String originalTransactionId) {
    this.originalTransactionId = originalTransactionId;
    return this;
  }

  
  @JsonProperty(required = true, value = "originalTransactionId")
  @NotNull  @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")public String getOriginalTransactionId() {
    return originalTransactionId;
  }

  @JsonProperty(required = true, value = "originalTransactionId")
  public void setOriginalTransactionId(String originalTransactionId) {
    this.originalTransactionId = originalTransactionId;
  }

  /**
   * Reason for the refund.
   **/
  public Refund refundReason(String refundReason) {
    this.refundReason = refundReason;
    return this;
  }

  
  @JsonProperty("refundReason")
   @Size(min=1,max=128)public String getRefundReason() {
    return refundReason;
  }

  @JsonProperty("refundReason")
  public void setRefundReason(String refundReason) {
    this.refundReason = refundReason;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Refund refund = (Refund) o;
    return Objects.equals(this.originalTransactionId, refund.originalTransactionId) &&
        Objects.equals(this.refundReason, refund.refundReason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(originalTransactionId, refundReason);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Refund {\n");
    
    sb.append("    originalTransactionId: ").append(toIndentedString(originalTransactionId)).append("\n");
    sb.append("    refundReason: ").append(toIndentedString(refundReason)).append("\n");
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

