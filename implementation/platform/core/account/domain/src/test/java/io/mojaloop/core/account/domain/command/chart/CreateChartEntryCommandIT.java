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
import io.mojaloop.core.account.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.account.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.account.domain.TestConfiguration;
import io.mojaloop.core.account.domain.repository.ChartEntryRepository;
import io.mojaloop.core.account.domain.repository.ChartRepository;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class CreateChartEntryCommandIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private ChartEntryRepository chartEntryRepository;

    @Test
    public void createChartEntry_success_persistsAndReturnsId() throws ChartIdNotFoundException {

        var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        var chartId = chartOut.chartId();

        assertTrue(this.chartRepository.findById(chartId).isPresent());

        var entryOut = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartId, new ChartEntryCode("1000"), "Cash", "Cash and cash equivalents", AccountType.ASSET));

        assertNotNull(entryOut);
        assertNotNull(entryOut.chartEntryId());

        var saved = this.chartEntryRepository.findById(entryOut.chartEntryId());

        assertTrue(saved.isPresent());
        assertEquals("Cash", saved.get().getName());
        assertEquals("1000", saved.get().getCode().value());
        assertEquals(AccountType.ASSET, saved.get().getAccountType());
        assertNotNull(saved.get().getCreatedAt());
        assertEquals(chartId, saved.get().getChart().getId());
    }

    @BeforeEach
    void cleanDatabase() {

        this.chartRepository.deleteAll();
        this.chartEntryRepository.deleteAll();
    }

}
