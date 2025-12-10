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

package org.mojave.core.common.datatype.identifier.accounting;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.mojave.component.misc.ddd.EntityId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.InvalidFormatException;

@JsonDeserialize(using = LedgerMovementId.Deserializer.class)
public class LedgerMovementId extends EntityId<Long> {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public LedgerMovementId(Long id) {

        super(id);
    }

    public static class Deserializer extends ValueDeserializer<LedgerMovementId> {

        @Override
        public LedgerMovementId deserialize(JsonParser p, DeserializationContext ctx)
            throws JacksonException {

            var field = p.currentName();
            var text = p.getValueAsString();

            if (text == null || text.isBlank()) {
                return null;
            }

            try {
                return new LedgerMovementId(Long.parseLong(text));
            } catch (NumberFormatException e) {
                throw InvalidFormatException.from(
                    p, "'" + field + "' has invalid format. Must be number.", e);
            }
        }

    }

    @Component
    public static class ParamConverter implements Converter<String, LedgerMovementId> {

        @Override
        public LedgerMovementId convert(String source) {

            return new LedgerMovementId(Long.parseLong(source));
        }

    }

}

