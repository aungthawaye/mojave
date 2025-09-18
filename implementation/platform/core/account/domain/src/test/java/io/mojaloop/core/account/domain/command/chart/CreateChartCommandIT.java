/*-
 * ================================================================================
 * Mojaloop OSS
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
package io.mojaloop.core.account.domain.command.chart;

import io.mojaloop.core.account.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.account.contract.exception.chart.ChartNameRequiredException;
import io.mojaloop.core.account.domain.TestConfiguration;
import io.mojaloop.core.account.domain.repository.ChartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class CreateChartCommandIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateChartCommandIT.class);

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private ChartRepository chartRepository;

    @Test
    public void blankName_throwsChartNameRequiredException() {

        assertThrows(ChartNameRequiredException.class, () -> this.createChartCommand.execute(new CreateChartCommand.Input(" ")));
    }

    @Test
    public void createChart_success_persistsAndReturnsId() {

        // Validate bean wiring
        LOGGER.info("Bean class: {}", this.createChartCommand.getClass());
        assertNotNull(this.createChartCommand);

        // Execute command
        var out = this.createChartCommand.execute(new CreateChartCommand.Input("Default Chart"));
        assertNotNull(out);
        assertNotNull(out.chartId());

        // Retrieve and verify persistence
        var saved = this.chartRepository.findById(out.chartId());
        assertTrue(saved.isPresent());
        assertEquals("Default Chart", saved.get().getName());
        assertNotNull(saved.get().getCreatedAt());
    }

    @BeforeEach
    void cleanDatabase() {
        // Ensure tests are independent and deterministic
        this.chartRepository.deleteAll();
    }

}
