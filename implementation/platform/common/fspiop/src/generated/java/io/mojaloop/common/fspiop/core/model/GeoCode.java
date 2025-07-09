package io.mojaloop.common.fspiop.core.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Data model for the complex type GeoCode. Indicates the geographic location from where the transaction was initiated.
 **/

@JsonTypeName("GeoCode")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-07-09T06:35:16.332603+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class GeoCode   {
  private String latitude;
  private String longitude;

  public GeoCode() {
  }

  @JsonCreator
  public GeoCode(
    @JsonProperty(required = true, value = "latitude") String latitude,
    @JsonProperty(required = true, value = "longitude") String longitude
  ) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * The API data type Latitude is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons.
   **/
  public GeoCode latitude(String latitude) {
    this.latitude = latitude;
    return this;
  }

  
  @JsonProperty(required = true, value = "latitude")
  @NotNull  @Pattern(regexp="^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$")public String getLatitude() {
    return latitude;
  }

  @JsonProperty(required = true, value = "latitude")
  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  /**
   * The API data type Longitude is a JSON String in a lexical format that is restricted by a regular expression for interoperability reasons.
   **/
  public GeoCode longitude(String longitude) {
    this.longitude = longitude;
    return this;
  }

  
  @JsonProperty(required = true, value = "longitude")
  @NotNull  @Pattern(regexp="^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$")public String getLongitude() {
    return longitude;
  }

  @JsonProperty(required = true, value = "longitude")
  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeoCode geoCode = (GeoCode) o;
    return Objects.equals(this.latitude, geoCode.latitude) &&
        Objects.equals(this.longitude, geoCode.longitude);
  }

  @Override
  public int hashCode() {
    return Objects.hash(latitude, longitude);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeoCode {\n");
    
    sb.append("    latitude: ").append(toIndentedString(latitude)).append("\n");
    sb.append("    longitude: ").append(toIndentedString(longitude)).append("\n");
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

