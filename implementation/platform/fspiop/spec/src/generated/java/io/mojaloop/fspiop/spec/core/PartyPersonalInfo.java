package io.mojaloop.fspiop.spec.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Data model for the complex type PartyPersonalInfo.
 **/

@JsonTypeName("PartyPersonalInfo")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              comments = "Generator version: 7.13.0")
public class PartyPersonalInfo {

    private PartyComplexName complexName;

    private String dateOfBirth;

    private String kycInformation;

    public PartyPersonalInfo() {

    }

    /**
     **/
    public PartyPersonalInfo complexName(PartyComplexName complexName) {

        this.complexName = complexName;
        return this;
    }

    /**
     * Date of Birth of the Party.
     **/
    public PartyPersonalInfo dateOfBirth(String dateOfBirth) {

        this.dateOfBirth = dateOfBirth;
        return this;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PartyPersonalInfo partyPersonalInfo = (PartyPersonalInfo) o;
        return Objects.equals(this.complexName, partyPersonalInfo.complexName) &&
                   Objects.equals(this.dateOfBirth, partyPersonalInfo.dateOfBirth) &&
                   Objects.equals(this.kycInformation, partyPersonalInfo.kycInformation);
    }

    @JsonProperty("complexName")
    @Valid
    public PartyComplexName getComplexName() {

        return complexName;
    }

    @JsonProperty("complexName")
    public void setComplexName(PartyComplexName complexName) {

        this.complexName = complexName;
    }

    @JsonProperty("dateOfBirth")
    @Pattern(
        regexp = "^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)$")
    public String getDateOfBirth() {

        return dateOfBirth;
    }

    @JsonProperty("dateOfBirth")
    public void setDateOfBirth(String dateOfBirth) {

        this.dateOfBirth = dateOfBirth;
    }

    @JsonProperty("kycInformation")
    @Size(min = 1, max = 2048)
    public String getKycInformation() {

        return kycInformation;
    }

    @JsonProperty("kycInformation")
    public void setKycInformation(String kycInformation) {

        this.kycInformation = kycInformation;
    }

    @Override
    public int hashCode() {

        return Objects.hash(complexName, dateOfBirth, kycInformation);
    }

    /**
     * KYC information for the party in a form mandated by an individual scheme.
     **/
    public PartyPersonalInfo kycInformation(String kycInformation) {

        this.kycInformation = kycInformation;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PartyPersonalInfo {\n");

        sb.append("    complexName: ").append(toIndentedString(complexName)).append("\n");
        sb.append("    dateOfBirth: ").append(toIndentedString(dateOfBirth)).append("\n");
        sb.append("    kycInformation: ").append(toIndentedString(kycInformation)).append("\n");
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

