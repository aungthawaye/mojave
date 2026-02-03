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
import org.mojave.core.accounting.contract.command.chart.CreateChartEntryCommand;
import org.mojave.core.accounting.contract.exception.chart.ChartIdNotFoundException;
import org.mojave.core.accounting.domain.repository.ChartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
public class CreateChartEntryCommandHandler implements CreateChartEntryCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateChartEntryCommandHandler.class);

    private final ChartRepository chartRepository;

    public CreateChartEntryCommandHandler(ChartRepository chartRepository) {

        Objects.requireNonNull(chartRepository);

        this.chartRepository = chartRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("CreateChartEntryCommand : input: ({})", ObjectLogger.log(input));

        var chart = this.chartRepository
                        .findById(input.chartId())
                        .orElseThrow(() -> new ChartIdNotFoundException(input.chartId()));

        var entry = chart.addEntry(
            input.category(), input.code(), input.name(),
            input.description(), input.accountType());

        this.chartRepository.save(chart);
        var output = new Output(entry.getId());

        LOGGER.info("CreateChartEntryCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
