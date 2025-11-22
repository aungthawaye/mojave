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

package io.mojaloop.core.common.datatype.identifier.accounting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.mojaloop.component.misc.ddd.EntityId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@JsonDeserialize(using = AccountOwnerId.Deserializer.class)
public class AccountOwnerId extends EntityId<Long> {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public AccountOwnerId(Long id) {

        super(id);
    }

    public static class Deserializer extends JsonDeserializer<AccountOwnerId> {

        @Override
        public AccountOwnerId deserialize(JsonParser p, DeserializationContext ctx) throws IOException {

            var field = p.currentName();
            var text = p.getValueAsString();

            if (text == null || text.isBlank()) {
                return null;
            }

            try {
                return new AccountOwnerId(Long.parseLong(text));
            } catch (NumberFormatException e) {
                throw InvalidFormatException.from(p, "'" + field + "' has invalid format. Must be number.", e);
            }
        }

    }

    @Component
    public static class ParamConverter implements Converter<String, AccountOwnerId> {

        @Override
        public AccountOwnerId convert(String source) {

            return new AccountOwnerId(Long.parseLong(source));
        }
    }

}
