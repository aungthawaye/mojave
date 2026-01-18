/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.core.common.datatype.type.accounting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.core.common.datatype.exception.accounting.AccountCodeValueRequiredException;
import org.mojave.core.common.datatype.exception.accounting.AccountCodeValueTooLargeException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

@JsonDeserialize(using = AccountCode.Deserializer.class)
public record AccountCode(@JsonValue @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_CODE_LENGTH) String value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public AccountCode(String value) {

        if (value == null || value.isBlank()) {
            throw new AccountCodeValueRequiredException();
        }

        if (value.length() > StringSizeConstraints.MAX_CODE_LENGTH) {
            throw new AccountCodeValueTooLargeException();
        }

        this.value = value;

    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof AccountCode(String code))) {
            return false;
        }

        return Objects.equals(this.value, code);
    }

    public static class Deserializer extends ValueDeserializer<AccountCode> {

        @Override
        public AccountCode deserialize(JsonParser p, DeserializationContext ctx) {

            var text = p.getValueAsString();

            if (text == null || text.isBlank()) {
                return null;
            }

            return new AccountCode(text);
        }

    }

    @Component
    public static class ParamConverter implements Converter<String, AccountCode> {

        @Override
        public AccountCode convert(String source) {

            return new AccountCode(source);
        }

    }

}
