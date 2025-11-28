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

package io.mojaloop.core.participant.domain.command.fsp;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.participant.contract.command.fsp.ActivateFspCurrencyCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import io.mojaloop.core.participant.domain.model.fsp.FspCurrency;
import io.mojaloop.core.participant.domain.repository.FspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class ActivateFspCurrencyCommandHandler implements ActivateFspCurrencyCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ActivateFspCurrencyCommandHandler.class);

    private final FspRepository fspRepository;

    public ActivateFspCurrencyCommandHandler(FspRepository fspRepository) {

        assert fspRepository != null;

        this.fspRepository = fspRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("Executing ActivateSupportedCurrencyCommand with input: {}", input);

        var fsp = this.fspRepository
                      .findById(input.fspId())
                      .orElseThrow(() -> new FspIdNotFoundException(input.fspId()));

        var optFspCurrency = fsp.activate(input.currency());

        this.fspRepository.save(fsp);

        LOGGER.info("Completed ActivateSupportedCurrencyCommand with input: {}", input);

        if (optFspCurrency.isPresent()) {
            return new Output(
                optFspCurrency.map(FspCurrency::getId).orElse(null),
                optFspCurrency.get().isActive());
        }

        return new Output(null, false);
    }

}
