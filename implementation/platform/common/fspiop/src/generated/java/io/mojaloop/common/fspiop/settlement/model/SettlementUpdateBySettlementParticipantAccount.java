package io.mojaloop.common.fspiop.settlement.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("SettlementUpdateBySettlementParticipantAccount")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-09T06:35:17.230850+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class SettlementUpdateBySettlementParticipantAccount   {
  public enum StateEnum {

    PENDING_SETTLEMENT(String.valueOf("PENDING_SETTLEMENT")), PS_TRANSFERS_RECORDED(String.valueOf("PS_TRANSFERS_RECORDED")), PS_TRANSFERS_RESERVED(String.valueOf("PS_TRANSFERS_RESERVED")), PS_TRANSFERS_COMMITTED(String.valueOf("PS_TRANSFERS_COMMITTED")), SETTLED(String.valueOf("SETTLED"));


    private String value;

    StateEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Convert a String into String, as specified in the
     * <a href="https://download.oracle.com/otndocs/jcp/jaxrs-2_0-fr-eval-spec/index.html">See JAX RS 2.0 Specification, section 3.2, p. 12</a>
     */
    public static StateEnum fromString(String s) {
        for (StateEnum b : StateEnum.values()) {
            // using Objects.toString() to be safe if value type non-object type
            // because types like 'int' etc. will be auto-boxed
            if (java.util.Objects.toString(b.value).equals(s)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected string value '" + s + "'");
    }

    @JsonCreator
    public static StateEnum fromValue(String value) {
        for (StateEnum b : StateEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

  private StateEnum state;
  private String reason;
  private String externalReference;

  public SettlementUpdateBySettlementParticipantAccount() {
  }

  @JsonCreator
  public SettlementUpdateBySettlementParticipantAccount(
    @JsonProperty(required = true, value = "state") StateEnum state,
    @JsonProperty(required = true, value = "reason") String reason
  ) {
    this.state = state;
    this.reason = reason;
  }

  /**
   **/
  public SettlementUpdateBySettlementParticipantAccount state(StateEnum state) {
    this.state = state;
    return this;
  }

  
  @JsonProperty(required = true, value = "state")
  @NotNull public StateEnum getState() {
    return state;
  }

  @JsonProperty(required = true, value = "state")
  public void setState(StateEnum state) {
    this.state = state;
  }

  /**
   **/
  public SettlementUpdateBySettlementParticipantAccount reason(String reason) {
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
  public SettlementUpdateBySettlementParticipantAccount externalReference(String externalReference) {
    this.externalReference = externalReference;
    return this;
  }

  
  @JsonProperty("externalReference")
  public String getExternalReference() {
    return externalReference;
  }

  @JsonProperty("externalReference")
  public void setExternalReference(String externalReference) {
    this.externalReference = externalReference;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SettlementUpdateBySettlementParticipantAccount settlementUpdateBySettlementParticipantAccount = (SettlementUpdateBySettlementParticipantAccount) o;
    return Objects.equals(this.state, settlementUpdateBySettlementParticipantAccount.state) &&
        Objects.equals(this.reason, settlementUpdateBySettlementParticipantAccount.reason) &&
        Objects.equals(this.externalReference, settlementUpdateBySettlementParticipantAccount.externalReference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(state, reason, externalReference);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SettlementUpdateBySettlementParticipantAccount {\n");
    
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    externalReference: ").append(toIndentedString(externalReference)).append("\n");
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

