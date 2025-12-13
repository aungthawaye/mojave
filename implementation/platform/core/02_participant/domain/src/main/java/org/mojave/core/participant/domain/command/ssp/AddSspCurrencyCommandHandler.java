/*-
 * ==============================================================================
 * Mojave
 * -----------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * -----------------------------------------------------------------------------
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
 * ==============================================================================
 */

package org.mojave.core.participant.domain.command.ssp;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.participant.contract.command.ssp.AddSspCurrencyCommand;
import org.mojave.core.participant.contract.exception.ssp.SspIdNotFoundException;
import org.mojave.core.participant.domain.repository.SspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class AddSspCurrencyCommandHandler implements AddSspCurrencyCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddSspCurrencyCommandHandler.class);

    private final SspRepository sspRepository;

    public AddSspCurrencyCommandHandler(final SspRepository sspRepository) {

        assert sspRepository != null;
        this.sspRepository = sspRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("AddSspCurrencyCommand : input: ({})", ObjectLogger.log(input));

        var ssp = this.sspRepository
                       .findById(input.sspId())
                       .orElseThrow(() -> new SspIdNotFoundException(input.sspId()));

        var supportedCurrency = ssp.addCurrency(input.currency());

        this.sspRepository.save(ssp);

        var output = new Output(supportedCurrency.getId());

        LOGGER.info("AddSspCurrencyCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }
}
