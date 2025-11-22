package io.mojaloop.fspiop.spec.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Data model for the complex type PartyResult.
 **/

@JsonTypeName("PartyResult")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              comments = "Generator version: 7.13.0")
public class PartyResult {

    private PartyIdInfo partyId;

    private ErrorInformation errorInformation;

    public PartyResult() {

    }

    @JsonCreator
    public PartyResult(@JsonProperty(required = true, value = "partyId") PartyIdInfo partyId) {

        this.partyId = partyId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PartyResult partyResult = (PartyResult) o;
        return Objects.equals(this.partyId, partyResult.partyId) &&
                   Objects.equals(this.errorInformation, partyResult.errorInformation);
    }

    /**
     **/
    public PartyResult errorInformation(ErrorInformation errorInformation) {

        this.errorInformation = errorInformation;
        return this;
    }

    @JsonProperty("errorInformation")
    @Valid
    public ErrorInformation getErrorInformation() {

        return errorInformation;
    }

    @JsonProperty("errorInformation")
    public void setErrorInformation(ErrorInformation errorInformation) {

        this.errorInformation = errorInformation;
    }

    @JsonProperty(required = true, value = "partyId")
    @NotNull
    @Valid
    public PartyIdInfo getPartyId() {

        return partyId;
    }

    @JsonProperty(required = true, value = "partyId")
    public void setPartyId(PartyIdInfo partyId) {

        this.partyId = partyId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(partyId, errorInformation);
    }

    /**
     **/
    public PartyResult partyId(PartyIdInfo partyId) {

        this.partyId = partyId;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PartyResult {\n");

        sb.append("    partyId: ").append(toIndentedString(partyId)).append("\n");
        sb.append("    errorInformation: ").append(toIndentedString(errorInformation)).append("\n");
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

