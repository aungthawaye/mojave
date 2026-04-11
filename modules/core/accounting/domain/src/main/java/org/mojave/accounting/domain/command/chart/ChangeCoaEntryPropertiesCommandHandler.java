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

package org.mojave.accounting.domain.command.chart;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.accounting.contract.command.chart.ChangeCoaEntryPropertiesCommand;
import org.mojave.accounting.contract.exception.chart.CoaEntryIdNotFoundException;
import org.mojave.accounting.domain.repository.CoaEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class ChangeCoaEntryPropertiesCommandHandler implements ChangeCoaEntryPropertiesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ChangeCoaEntryPropertiesCommandHandler.class);

    private final CoaEntryRepository coaEntryRepository;

    public ChangeCoaEntryPropertiesCommandHandler(CoaEntryRepository coaEntryRepository) {

        Objects.requireNonNull(coaEntryRepository);
        this.coaEntryRepository = coaEntryRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("ChangeCoaEntryPropertiesCommand : input: ({})", ObjectLogger.log(input));

        var entry = this.coaEntryRepository
                        .findById(input.coaEntryId())
                        .orElseThrow(() -> new CoaEntryIdNotFoundException(input.coaEntryId()));

        if (input.name() != null) {
            entry.name(input.name());
        }

        if (input.description() != null) {
            entry.description(input.description());
        }

        this.coaEntryRepository.save(entry);
        var output = new Output(entry.getId());

        LOGGER.info("ChangeCoaEntryPropertiesCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
