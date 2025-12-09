package io.mojaloop.fspiop.spec.settlement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonTypeName("Error_errorInformation_extensionList")
@jakarta.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
    date = "2025-12-09T08:19:11.267774+06:30[Asia/Rangoon]",
    comments = "Generator version: 7.13.0")
public class ErrorErrorInformationExtensionList {

    private @Valid List<@Valid Extension> extension = new ArrayList<>();

    public ErrorErrorInformationExtensionList() {

    }

    public ErrorErrorInformationExtensionList addExtensionItem(Extension extensionItem) {

        if (this.extension == null) {
            this.extension = new ArrayList<>();
        }

        this.extension.add(extensionItem);
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
        ErrorErrorInformationExtensionList errorErrorInformationExtensionList = (ErrorErrorInformationExtensionList) o;
        return Objects.equals(this.extension, errorErrorInformationExtensionList.extension);
    }

    /**
     **/
    public ErrorErrorInformationExtensionList extension(List<@Valid Extension> extension) {

        this.extension = extension;
        return this;
    }

    @JsonProperty("extension")
    @Valid
    public List<@Valid Extension> getExtension() {

        return extension;
    }

    @JsonProperty("extension")
    public void setExtension(List<@Valid Extension> extension) {

        this.extension = extension;
    }

    @Override
    public int hashCode() {

        return Objects.hash(extension);
    }

    public ErrorErrorInformationExtensionList removeExtensionItem(Extension extensionItem) {

        if (extensionItem != null && this.extension != null) {
            this.extension.remove(extensionItem);
        }

        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ErrorErrorInformationExtensionList {\n");

        sb.append("    extension: ").append(toIndentedString(extension)).append("\n");
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

