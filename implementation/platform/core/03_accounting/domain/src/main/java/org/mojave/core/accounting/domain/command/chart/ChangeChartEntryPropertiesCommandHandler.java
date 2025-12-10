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
/*-
 * =============================================================================
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
 * =============================================================================
 */

package org.mojave.core.accounting.domain.command.chart;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.accounting.contract.command.chart.ChangeChartEntryPropertiesCommand;
import org.mojave.core.accounting.contract.exception.chart.ChartEntryIdNotFoundException;
import org.mojave.core.accounting.domain.repository.ChartEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeChartEntryPropertiesCommandHandler implements ChangeChartEntryPropertiesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ChangeChartEntryPropertiesCommandHandler.class);

    private final ChartEntryRepository chartEntryRepository;

    public ChangeChartEntryPropertiesCommandHandler(ChartEntryRepository chartEntryRepository) {

        assert chartEntryRepository != null;
        this.chartEntryRepository = chartEntryRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("ChangeChartEntryPropertiesCommand : input: ({})", ObjectLogger.log(input));

        var entry = this.chartEntryRepository
                        .findById(input.chartEntryId())
                        .orElseThrow(() -> new ChartEntryIdNotFoundException(input.chartEntryId()));

        if (input.name() != null) {
            entry.name(input.name());
        }

        if (input.description() != null) {
            entry.description(input.description());
        }

        this.chartEntryRepository.save(entry);
        var output = new Output(entry.getId());

        LOGGER.info("ChangeChartEntryPropertiesCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
