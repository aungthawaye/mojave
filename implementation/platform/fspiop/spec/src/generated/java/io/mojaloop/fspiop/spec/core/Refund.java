/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.fspiop.spec.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Data model for the complex type Refund.
 **/

@JsonTypeName("Refund")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.13.0")
public class Refund {

    private String originalTransactionId;

    private String refundReason;

    public Refund() {

    }

    @JsonCreator
    public Refund(@JsonProperty(required = true, value = "originalTransactionId") String originalTransactionId) {

        this.originalTransactionId = originalTransactionId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Refund refund = (Refund) o;
        return Objects.equals(this.originalTransactionId, refund.originalTransactionId) && Objects.equals(this.refundReason, refund.refundReason);
    }

    @JsonProperty(required = true, value = "originalTransactionId")
    @NotNull
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    public String getOriginalTransactionId() {

        return originalTransactionId;
    }

    @JsonProperty(required = true, value = "originalTransactionId")
    public void setOriginalTransactionId(String originalTransactionId) {

        this.originalTransactionId = originalTransactionId;
    }

    @JsonProperty("refundReason")
    @Size(min = 1, max = 128)
    public String getRefundReason() {

        return refundReason;
    }

    @JsonProperty("refundReason")
    public void setRefundReason(String refundReason) {

        this.refundReason = refundReason;
    }

    @Override
    public int hashCode() {

        return Objects.hash(originalTransactionId, refundReason);
    }

    /**
     * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
     **/
    public Refund originalTransactionId(String originalTransactionId) {

        this.originalTransactionId = originalTransactionId;
        return this;
    }

    /**
     * Reason for the refund.
     **/
    public Refund refundReason(String refundReason) {

        this.refundReason = refundReason;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class Refund {\n");

        sb.append("    originalTransactionId: ").append(toIndentedString(originalTransactionId)).append("\n");
        sb.append("    refundReason: ").append(toIndentedString(refundReason)).append("\n");
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

