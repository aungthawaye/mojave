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
package io.mojaloop.core.transfer.domain.component.interledger.unwrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.core.transfer.contract.component.interledger.PartyUnwrapper;
import io.mojaloop.fspiop.common.data.Agreement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class MojavePartyUnwrapper implements PartyUnwrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MojavePartyUnwrapper.class);

    private final ObjectMapper objectMapper;

    public MojavePartyUnwrapper(ObjectMapper objectMapper) {

        assert objectMapper != null;

        this.objectMapper = objectMapper;
    }

    @Override
    public Parties unwrap(byte[] data) {

        var json = new String(data, StandardCharsets.UTF_8);
        LOGGER.debug("Unwrapping: {}", json);

        try {

            var agreement = this.objectMapper.readValue(json, Agreement.class);
            LOGGER.debug("Unwrapped Agreement: {}", agreement);

            return new Parties(Optional.ofNullable(agreement.payer()), Optional.ofNullable(agreement.payee()));

        } catch (JsonProcessingException e) {

            LOGGER.warn("Failed to unwrap ILP packet data: {}", e.getMessage());
            return Parties.empty();
        }
    }

}
