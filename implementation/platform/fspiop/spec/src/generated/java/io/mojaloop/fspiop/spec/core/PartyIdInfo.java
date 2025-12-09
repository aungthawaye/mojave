package io.mojaloop.fspiop.spec.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Data model for the complex type PartyIdInfo. An ExtensionList element has been added to this reqeust in version v1.1
 **/

@JsonTypeName("PartyIdInfo")
@jakarta.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
    comments = "Generator version: 7.13.0")
public class PartyIdInfo {

    private PartyIdType partyIdType;

    private String partyIdentifier;

    private String partySubIdOrType;

    private String fspId;

    private ExtensionList extensionList;

    public PartyIdInfo() {

    }

    @JsonCreator
    public PartyIdInfo(@JsonProperty(
                           required = true,
                           value = "partyIdType") PartyIdType partyIdType,
                       @JsonProperty(
                           required = true,
                           value = "partyIdentifier") String partyIdentifier) {

        this.partyIdType = partyIdType;
        this.partyIdentifier = partyIdentifier;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PartyIdInfo partyIdInfo = (PartyIdInfo) o;
        return Objects.equals(this.partyIdType, partyIdInfo.partyIdType) &&
                   Objects.equals(this.partyIdentifier, partyIdInfo.partyIdentifier) &&
                   Objects.equals(this.partySubIdOrType, partyIdInfo.partySubIdOrType) &&
                   Objects.equals(this.fspId, partyIdInfo.fspId) &&
                   Objects.equals(this.extensionList, partyIdInfo.extensionList);
    }

    /**
     **/
    public PartyIdInfo extensionList(ExtensionList extensionList) {

        this.extensionList = extensionList;
        return this;
    }

    /**
     * FSP identifier.
     **/
    public PartyIdInfo fspId(String fspId) {

        this.fspId = fspId;
        return this;
    }

    @JsonProperty("extensionList")
    @Valid
    public ExtensionList getExtensionList() {

        return extensionList;
    }

    @JsonProperty("extensionList")
    public void setExtensionList(ExtensionList extensionList) {

        this.extensionList = extensionList;
    }

    @JsonProperty("fspId")
    @Size(
        min = 1,
        max = 32)
    public String getFspId() {

        return fspId;
    }

    @JsonProperty("fspId")
    public void setFspId(String fspId) {

        this.fspId = fspId;
    }

    @JsonProperty(
        required = true,
        value = "partyIdType")
    @NotNull
    public PartyIdType getPartyIdType() {

        return partyIdType;
    }

    @JsonProperty(
        required = true,
        value = "partyIdType")
    public void setPartyIdType(PartyIdType partyIdType) {

        this.partyIdType = partyIdType;
    }

    @JsonProperty(
        required = true,
        value = "partyIdentifier")
    @NotNull
    @Size(
        min = 1,
        max = 128)
    public String getPartyIdentifier() {

        return partyIdentifier;
    }

    @JsonProperty(
        required = true,
        value = "partyIdentifier")
    public void setPartyIdentifier(String partyIdentifier) {

        this.partyIdentifier = partyIdentifier;
    }

    @JsonProperty("partySubIdOrType")
    @Size(
        min = 1,
        max = 128)
    public String getPartySubIdOrType() {

        return partySubIdOrType;
    }

    @JsonProperty("partySubIdOrType")
    public void setPartySubIdOrType(String partySubIdOrType) {

        this.partySubIdOrType = partySubIdOrType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(partyIdType, partyIdentifier, partySubIdOrType, fspId, extensionList);
    }

    /**
     **/
    public PartyIdInfo partyIdType(PartyIdType partyIdType) {

        this.partyIdType = partyIdType;
        return this;
    }

    /**
     * Identifier of the Party.
     **/
    public PartyIdInfo partyIdentifier(String partyIdentifier) {

        this.partyIdentifier = partyIdentifier;
        return this;
    }

    /**
     * Either a sub-identifier of a PartyIdentifier, or a sub-type of the PartyIdType, normally a PersonalIdentifierType.
     **/
    public PartyIdInfo partySubIdOrType(String partySubIdOrType) {

        this.partySubIdOrType = partySubIdOrType;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PartyIdInfo {\n");

        sb.append("    partyIdType: ").append(toIndentedString(partyIdType)).append("\n");
        sb.append("    partyIdentifier: ").append(toIndentedString(partyIdentifier)).append("\n");
        sb.append("    partySubIdOrType: ").append(toIndentedString(partySubIdOrType)).append("\n");
        sb.append("    fspId: ").append(toIndentedString(fspId)).append("\n");
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

