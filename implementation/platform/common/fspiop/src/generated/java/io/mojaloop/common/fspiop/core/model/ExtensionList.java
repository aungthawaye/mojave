package io.mojaloop.common.fspiop.core.model;

import io.mojaloop.common.fspiop.core.model.Extension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type ExtensionList. An optional list of extensions, specific to deployment.
 **/

@JsonTypeName("ExtensionList")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-09T06:35:16.332603+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class ExtensionList   {
  private @Valid List<@Valid Extension> extension = new ArrayList<>();

  public ExtensionList() {
  }

  @JsonCreator
  public ExtensionList(
    @JsonProperty(required = true, value = "extension") List<@Valid Extension> extension
  ) {
    this.extension = extension;
  }

  /**
   * Number of Extension elements.
   **/
  public ExtensionList extension(List<@Valid Extension> extension) {
    this.extension = extension;
    return this;
  }

  
  @JsonProperty(required = true, value = "extension")
  @NotNull @Valid  @Size(min=1,max=16)public List<@Valid Extension> getExtension() {
    return extension;
  }

  @JsonProperty(required = true, value = "extension")
  public void setExtension(List<@Valid Extension> extension) {
    this.extension = extension;
  }

  public ExtensionList addExtensionItem(Extension extensionItem) {
    if (this.extension == null) {
      this.extension = new ArrayList<>();
    }

    this.extension.add(extensionItem);
    return this;
  }

  public ExtensionList removeExtensionItem(Extension extensionItem) {
    if (extensionItem != null && this.extension != null) {
      this.extension.remove(extensionItem);
    }

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
    ExtensionList extensionList = (ExtensionList) o;
    return Objects.equals(this.extension, extensionList.extension);
  }

  @Override
  public int hashCode() {
    return Objects.hash(extension);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExtensionList {\n");
    
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

