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

package io.mojaloop.core.accounting.domain.command.chart;

import io.mojaloop.core.accounting.contract.command.chart.ChangeChartEntryPropertiesCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryIdNotFoundException;
import io.mojaloop.core.accounting.domain.command.BaseDomainIT;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChangeChartEntryPropertiesCommandIT extends BaseDomainIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private ChangeChartEntryPropertiesCommand changeChartEntryPropertiesCommand;

    @Test
    void should_change_entry_name_and_description_successfully() throws Exception {
        // Arrange
        final var chartOutput = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var chartId = chartOutput.chartId();

        final var entryOutput = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(chartId,
                                                                                                       new ChartEntryCode("ASSETS"),
                                                                                                       "Assets",
                                                                                                       "Assets Description",
                                                                                                       AccountType.ASSET));
        final var entryId = entryOutput.chartEntryId();

        final var input = new ChangeChartEntryPropertiesCommand.Input(entryId, "Assets New", "Updated Description");

        // Act
        final var output = this.changeChartEntryPropertiesCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertNotNull(output.chartEntryId());
    }

    @Test
    void should_fail_when_chart_entry_id_not_found() {
        // Arrange
        final var input = new ChangeChartEntryPropertiesCommand.Input(new ChartEntryId(8888888L), "Name", "Desc");

        // Act & Assert
        assertThrows(ChartEntryIdNotFoundException.class, () -> this.changeChartEntryPropertiesCommand.execute(input));
    }

}
