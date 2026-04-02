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

package org.mojave.core.accounting.domain.command.chart;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.accounting.contract.command.chart.ChangeCoaNameCommand;
import org.mojave.core.accounting.contract.exception.chart.CoaIdNotFoundException;
import org.mojave.core.accounting.domain.repository.CoaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class ChangeCoaNameCommandHandler implements ChangeCoaNameCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ChangeCoaNameCommandHandler.class);

    private final CoaRepository coaRepository;

    public ChangeCoaNameCommandHandler(CoaRepository coaRepository) {

        Objects.requireNonNull(coaRepository);
        this.coaRepository = coaRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("ChangeCoaNameCommand : input: ({})", ObjectLogger.log(input));

        var coa = this.coaRepository
                      .findById(input.coaId())
                      .orElseThrow(() -> new CoaIdNotFoundException(input.coaId()));

        coa.name(input.name());

        this.coaRepository.save(coa);
        var output = new Output(coa.getId());

        LOGGER.info("ChangeCoaNameCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
