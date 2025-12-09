package io.mojaloop.fspiop.spec.settlement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@JsonTypeName("SettlementWindowId")
@jakarta.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
    date = "2025-12-09T08:19:11.267774+06:30[Asia/Rangoon]",
    comments = "Generator version: 7.13.0")
public class SettlementWindowId {

    private Integer id;

    public SettlementWindowId() {

    }

    @JsonCreator
    public SettlementWindowId(@JsonProperty(
        required = true,
        value = "id") Integer id) {

        this.id = id;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SettlementWindowId settlementWindowId = (SettlementWindowId) o;
        return Objects.equals(this.id, settlementWindowId.id);
    }

    @JsonProperty(
        required = true,
        value = "id")
    @NotNull
    public Integer getId() {

        return id;
    }

    @JsonProperty(
        required = true,
        value = "id")
    public void setId(Integer id) {

        this.id = id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    /**
     **/
    public SettlementWindowId id(Integer id) {

        this.id = id;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class SettlementWindowId {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

