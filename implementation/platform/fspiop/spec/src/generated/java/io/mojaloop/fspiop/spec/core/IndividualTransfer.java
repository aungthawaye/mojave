package io.mojaloop.fspiop.spec.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Data model for the complex type IndividualTransfer.
 **/

@JsonTypeName("IndividualTransfer")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.13.0")
public class IndividualTransfer {

    private String transferId;

    private Money transferAmount;

    private String ilpPacket;

    private String condition;

    private ExtensionList extensionList;

    public IndividualTransfer() {

    }

    @JsonCreator
    public IndividualTransfer(@JsonProperty(required = true, value = "transferId") String transferId,
                              @JsonProperty(required = true, value = "transferAmount") Money transferAmount,
                              @JsonProperty(required = true, value = "ilpPacket") String ilpPacket,
                              @JsonProperty(required = true, value = "condition") String condition) {

        this.transferId = transferId;
        this.transferAmount = transferAmount;
        this.ilpPacket = ilpPacket;
        this.condition = condition;
    }

    /**
     * Condition that must be attached to the transfer by the Payer.
     **/
    public IndividualTransfer condition(String condition) {

        this.condition = condition;
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
        IndividualTransfer individualTransfer = (IndividualTransfer) o;
        return Objects.equals(this.transferId, individualTransfer.transferId) && Objects.equals(this.transferAmount, individualTransfer.transferAmount) &&
                   Objects.equals(this.ilpPacket, individualTransfer.ilpPacket) && Objects.equals(this.condition, individualTransfer.condition) &&
                   Objects.equals(this.extensionList, individualTransfer.extensionList);
    }

    /**
     **/
    public IndividualTransfer extensionList(ExtensionList extensionList) {

        this.extensionList = extensionList;
        return this;
    }

    @JsonProperty(required = true, value = "condition")
    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9-_]{43}$")
    @Size(max = 48)
    public String getCondition() {

        return condition;
    }

    @JsonProperty(required = true, value = "condition")
    public void setCondition(String condition) {

        this.condition = condition;
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

    @JsonProperty(required = true, value = "ilpPacket")
    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9-_]+[=]{0,2}$")
    @Size(min = 1, max = 32768)
    public String getIlpPacket() {

        return ilpPacket;
    }

    @JsonProperty(required = true, value = "ilpPacket")
    public void setIlpPacket(String ilpPacket) {

        this.ilpPacket = ilpPacket;
    }

    @JsonProperty(required = true, value = "transferAmount")
    @NotNull
    @Valid
    public Money getTransferAmount() {

        return transferAmount;
    }

    @JsonProperty(required = true, value = "transferAmount")
    public void setTransferAmount(Money transferAmount) {

        this.transferAmount = transferAmount;
    }

    @JsonProperty(required = true, value = "transferId")
    @NotNull
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    public String getTransferId() {

        return transferId;
    }

    @JsonProperty(required = true, value = "transferId")
    public void setTransferId(String transferId) {

        this.transferId = transferId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(transferId, transferAmount, ilpPacket, condition, extensionList);
    }

    /**
     * Information for recipient (transport layer information).
     **/
    public IndividualTransfer ilpPacket(String ilpPacket) {

        this.ilpPacket = ilpPacket;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class IndividualTransfer {\n");

        sb.append("    transferId: ").append(toIndentedString(transferId)).append("\n");
        sb.append("    transferAmount: ").append(toIndentedString(transferAmount)).append("\n");
        sb.append("    ilpPacket: ").append(toIndentedString(ilpPacket)).append("\n");
        sb.append("    condition: ").append(toIndentedString(condition)).append("\n");
        sb.append("    extensionList: ").append(toIndentedString(extensionList)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     **/
    public IndividualTransfer transferAmount(Money transferAmount) {

        this.transferAmount = transferAmount;
        return this;
    }

    /**
     * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
     **/
    public IndividualTransfer transferId(String transferId) {

        this.transferId = transferId;
        return this;
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

