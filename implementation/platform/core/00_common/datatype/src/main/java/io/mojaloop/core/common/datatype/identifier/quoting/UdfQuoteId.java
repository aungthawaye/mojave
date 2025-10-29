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

package io.mojaloop.core.common.datatype.identifier.quoting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.mojaloop.component.misc.ddd.EntityId;

import java.io.IOException;

@JsonDeserialize(using = UdfQuoteId.Deserializer.class)
public class UdfQuoteId extends EntityId<String> {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public UdfQuoteId(String id) {

        super(id);
    }

    public static class Deserializer extends JsonDeserializer<UdfQuoteId> {

        @Override
        public UdfQuoteId deserialize(JsonParser p, DeserializationContext ctx) throws IOException {

            var text = p.getValueAsString();

            if (text == null || text.isBlank()) {
                return null;
            }

            return new UdfQuoteId(text);
        }

    }

}

