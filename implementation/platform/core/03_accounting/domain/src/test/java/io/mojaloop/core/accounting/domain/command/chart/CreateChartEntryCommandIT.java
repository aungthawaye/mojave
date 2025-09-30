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

package io.mojaloop.core.accounting.domain.command.chart;

import io.mojaloop.core.accounting.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryCodeAlreadyExistsException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.accounting.domain.command.BaseDomainIT;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateChartEntryCommandIT extends BaseDomainIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Test
    void should_create_chart_entry_successfully() throws Exception {
        // Arrange
        final var chartOutput = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var chartId = chartOutput.chartId();

        final var input = new CreateChartEntryCommand.Input(chartId, new ChartEntryCode("ASSETS"), "Assets", "Assets Description", AccountType.ASSET);

        // Act
        final var output = this.createChartEntryCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertNotNull(output.chartEntryId());
    }

    @Test
    void should_fail_when_chart_entry_code_already_exists_globally() throws Exception {
        // Arrange
        final var chartA = this.createChartCommand.execute(new CreateChartCommand.Input("Chart A")).chartId();
        final var chartB = this.createChartCommand.execute(new CreateChartCommand.Input("Chart B")).chartId();

        final var code = new ChartEntryCode("CASH");

        // First creation should succeed
        this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(chartA, code, "Cash", "Cash on hand", AccountType.ASSET));

        // Second creation with the same code (even in another chart) should fail due to global uniqueness
        final var duplicateInput = new CreateChartEntryCommand.Input(chartB, code, "Cash Duplicate", "Another description", AccountType.ASSET);

        // Act & Assert
        assertThrows(ChartEntryCodeAlreadyExistsException.class, () -> this.createChartEntryCommand.execute(duplicateInput));
    }

    @Test
    void should_fail_when_chart_not_found() {
        // Arrange
        final var input = new CreateChartEntryCommand.Input(new ChartId(999999L), new ChartEntryCode("LIAB"), "Liabilities", "Liabilities Description", AccountType.LIABILITY);

        // Act & Assert
        assertThrows(ChartIdNotFoundException.class, () -> this.createChartEntryCommand.execute(input));
    }

}
