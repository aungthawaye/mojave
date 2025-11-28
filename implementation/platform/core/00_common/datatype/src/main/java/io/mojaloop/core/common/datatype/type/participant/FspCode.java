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

package io.mojaloop.core.common.datatype.type.participant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.exception.participant.FspCodeValueRequiredException;
import io.mojaloop.core.common.datatype.exception.participant.FspCodeValueTooLargeException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@JsonDeserialize(using = FspCode.Deserializer.class)
public record FspCode(@JsonValue @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_CODE_LENGTH) String value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public FspCode(String value) {

        if (value == null || value.isBlank()) {
            throw new FspCodeValueRequiredException();
        }

        if (value.length() > StringSizeConstraints.MAX_CODE_LENGTH) {
            throw new FspCodeValueTooLargeException();
        }

        this.value = value;

    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof FspCode(String code))) {
            return false;
        }

        return Objects.equals(this.value, code);
    }

    public static class Deserializer extends JsonDeserializer<FspCode> {

        @Override
        public FspCode deserialize(JsonParser p, DeserializationContext ctx) throws IOException {

            var text = p.getValueAsString();

            if (text == null || text.isBlank()) {
                return null;
            }

            return new FspCode(text);
        }

    }

    @Component
    public static class ParamConverter implements Converter<String, FspCode> {

        @Override
        public FspCode convert(String source) {

            return new FspCode(source);
        }

    }

}
