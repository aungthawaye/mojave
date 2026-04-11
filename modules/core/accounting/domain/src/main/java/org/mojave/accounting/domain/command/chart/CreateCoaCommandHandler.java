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
import org.mojave.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.accounting.contract.exception.chart.CoaNameAlreadyExistsException;
import org.mojave.accounting.domain.model.Coa;
import org.mojave.accounting.domain.repository.CoaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CreateCoaCommandHandler implements CreateCoaCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCoaCommandHandler.class);

    private final CoaRepository coaRepository;

    public CreateCoaCommandHandler(CoaRepository coaRepository) {

        Objects.requireNonNull(coaRepository);
        this.coaRepository = coaRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("CreateCoaCommand : input: ({})", ObjectLogger.log(input));

        if (this.coaRepository
                .findOne(CoaRepository.Filters.withNameEquals(input.name()))
                .isPresent()) {

            throw new CoaNameAlreadyExistsException(input.name());
        }

        var coa = new Coa(input.name());

        coa = this.coaRepository.save(coa);
        var output = new Output(coa.getId());

        LOGGER.info("CreateCoaCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
