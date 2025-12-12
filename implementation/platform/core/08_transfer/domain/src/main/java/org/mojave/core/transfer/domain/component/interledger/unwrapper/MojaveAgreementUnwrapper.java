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
package org.mojave.core.transfer.domain.component.interledger.unwrapper;

import org.mojave.core.transfer.contract.component.interledger.AgreementUnwrapper;
import org.mojave.fspiop.component.data.Agreement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

public class MojaveAgreementUnwrapper implements AgreementUnwrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MojaveAgreementUnwrapper.class);

    private final ObjectMapper objectMapper;

    public MojaveAgreementUnwrapper(ObjectMapper objectMapper) {

        assert objectMapper != null;

        this.objectMapper = objectMapper;
    }

    @Override
    public Agreement unwrap(byte[] data) {

        var json = new String(data, StandardCharsets.UTF_8);
        LOGGER.debug("Unwrapping: {}", json);

        try {

            var agreement = this.objectMapper.readValue(
                json, org.mojave.fspiop.component.data.Agreement.class);
            LOGGER.debug("Unwrapped Agreement: {}", agreement);

            return agreement;

        } catch (Exception e) {

            LOGGER.warn("Failed to unwrap ILP packet data: {}", e.getMessage());
            return null;
        }
    }

}
