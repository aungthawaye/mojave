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

package org.mojave.common.datatype.identifier.settlement;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.mojave.component.misc.ddd.EntityId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = SettlementId.Deserializer.class)
public class SettlementId extends EntityId<String> {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public SettlementId(String id) {

        super(id);
    }

    public static class Deserializer extends ValueDeserializer<SettlementId> {

        @Override
        public SettlementId deserialize(JsonParser p, DeserializationContext ctx)
            throws JacksonException {

            var field = p.currentName();
            var text = p.getValueAsString();

            if (text == null || text.isBlank()) {
                return null;
            }

            return new SettlementId(text);
        }

    }

    @Component
    public static class ParamConverter implements Converter<String, SettlementId> {

        @Override
        public SettlementId convert(String source) {

            return new SettlementId(source);
        }

    }

}
