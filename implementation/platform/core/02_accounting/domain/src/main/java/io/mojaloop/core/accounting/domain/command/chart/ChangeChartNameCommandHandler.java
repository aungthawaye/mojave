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

package io.mojaloop.core.accounting.domain.command.chart;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.accounting.contract.command.chart.ChangeChartNameCommand;
import io.mojaloop.core.accounting.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.accounting.domain.repository.ChartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeChartNameCommandHandler implements ChangeChartNameCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ChangeChartNameCommandHandler.class);

    private final ChartRepository chartRepository;

    public ChangeChartNameCommandHandler(ChartRepository chartRepository) {

        assert chartRepository != null;
        this.chartRepository = chartRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("Executing ChangeChartNameCommand with input: {}", input);

        var chart = this.chartRepository
                        .findById(input.chartId())
                        .orElseThrow(() -> new ChartIdNotFoundException(input.chartId()));
        LOGGER.info("Found Chart with id: {}", input.chartId());

        chart.name(input.name());
        LOGGER.info("Updated name for Chart id: {}", chart.getId());

        this.chartRepository.save(chart);
        LOGGER.info("Saved Chart with id: {}", chart.getId());

        LOGGER.info("Completed ChangeChartNameCommand with input: {}", input);

        return new Output(chart.getId());
    }

}
