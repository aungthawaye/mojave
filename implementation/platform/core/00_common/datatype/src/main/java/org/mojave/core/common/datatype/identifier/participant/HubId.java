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

package org.mojave.core.common.datatype.identifier.participant;

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

@JsonDeserialize(using = HubId.Deserializer.class)
public class HubId extends EntityId<Long> {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public HubId() {

        super(1L);
    }

    public static class Deserializer extends ValueDeserializer<HubId> {

        @Override
        public HubId deserialize(JsonParser p, DeserializationContext ctx) throws JacksonException {

            var field = p.currentName();
            var text = p.getValueAsString();

            if (text == null || text.isBlank()) {
                return null;
            }

            try {
                var value = Long.parseLong(text);
                if (value != 1L) {
                    throw InvalidFormatException.from(
                        p, "'" + field + "' has invalid value. Only '1' is accepted.", null);
                }
                return new HubId();
            } catch (NumberFormatException e) {
                throw InvalidFormatException.from(
                    p, "'" + field + "' has invalid format. Must be number '1'.", e);
            }
        }

    }

    @Component
    public static class ParamConverter implements Converter<String, HubId> {

        @Override
        public HubId convert(final String source) {

            var value = Long.parseLong(source);
            if (value != 1L) {
                throw new IllegalArgumentException(
                    "'hubId' has invalid value. Only '1' is accepted.");
            }

            return new HubId();
        }

    }

}

