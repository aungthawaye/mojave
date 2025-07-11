package io.mojaloop.common.fspiop.model.settlement;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("SettlementWindowClosurePayload")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-11T07:50:33.786087+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class SettlementWindowClosurePayload   {
  public enum StateEnum {

    CLOSED(String.valueOf("CLOSED"));


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

  public SettlementWindowClosurePayload() {
  }

  @JsonCreator
  public SettlementWindowClosurePayload(
    @JsonProperty(required = true, value = "state") StateEnum state,
    @JsonProperty(required = true, value = "reason") String reason
  ) {
    this.state = state;
    this.reason = reason;
  }

  /**
   **/
  public SettlementWindowClosurePayload state(StateEnum state) {
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
  public SettlementWindowClosurePayload reason(String reason) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SettlementWindowClosurePayload settlementWindowClosurePayload = (SettlementWindowClosurePayload) o;
    return Objects.equals(this.state, settlementWindowClosurePayload.state) &&
        Objects.equals(this.reason, settlementWindowClosurePayload.reason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(state, reason);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SettlementWindowClosurePayload {\n");
    
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
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

