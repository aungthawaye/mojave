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

package org.mojave.core.participant.domain.command.fsp;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.participant.contract.command.fsp.DeactivateFspCurrencyCommand;
import org.mojave.core.participant.contract.exception.fsp.FspIdNotFoundException;
import org.mojave.core.participant.domain.repository.FspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class DeactivateFspCurrencyCommandHandler implements DeactivateFspCurrencyCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        DeactivateFspCurrencyCommandHandler.class);

    private final FspRepository fspRepository;

    public DeactivateFspCurrencyCommandHandler(FspRepository fspRepository) {

        assert fspRepository != null;

        this.fspRepository = fspRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("DeactivateFspCurrencyCommand : input: ({})", ObjectLogger.log(input));

        var fsp = this.fspRepository
                      .findById(input.fspId())
                      .orElseThrow(() -> new FspIdNotFoundException(input.fspId()));

        var optFspCurrency = fsp.deactivate(input.currency());

        this.fspRepository.save(fsp);

        var output = optFspCurrency
                         .map(currency -> new Output(currency.getId(), currency.isActive()))
                         .orElse(new Output(null, false));

        LOGGER.info("DeactivateFspCurrencyCommand : output: ({})", ObjectLogger.log(output));

        return output;
    }

}
