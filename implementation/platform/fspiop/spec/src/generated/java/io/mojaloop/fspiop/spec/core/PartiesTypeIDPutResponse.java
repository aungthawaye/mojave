package io.mojaloop.fspiop.spec.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * The object sent in the PUT /parties/{Type}/{ID} callback.
 **/

@JsonTypeName("PartiesTypeIDPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              comments = "Generator version: 7.13.0")
public class PartiesTypeIDPutResponse {

    private Party party;

    public PartiesTypeIDPutResponse() {

    }

    @JsonCreator
    public PartiesTypeIDPutResponse(@JsonProperty(required = true, value = "party") Party party) {

        this.party = party;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PartiesTypeIDPutResponse partiesTypeIDPutResponse = (PartiesTypeIDPutResponse) o;
        return Objects.equals(this.party, partiesTypeIDPutResponse.party);
    }

    @JsonProperty(required = true, value = "party")
    @NotNull
    @Valid
    public Party getParty() {

        return party;
    }

    @JsonProperty(required = true, value = "party")
    public void setParty(Party party) {

        this.party = party;
    }

    @Override
    public int hashCode() {

        return Objects.hash(party);
    }

    /**
     **/
    public PartiesTypeIDPutResponse party(Party party) {

        this.party = party;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PartiesTypeIDPutResponse {\n");

        sb.append("    party: ").append(toIndentedString(party)).append("\n");
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

