package org.mojave.core.accounting.intercom.requestor.command.chart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.core.accounting.contract.command.chart.ChangeCoaEntryPropertiesCommand;
import org.mojave.core.accounting.contract.command.chart.ChangeCoaNameCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.core.accounting.intercom.requestor.AccountingIntercomRequestorTestConfiguration;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingIntercomRequestorTestConfiguration.class})
@DisplayName("Chart Commands Requestor Integration Test")
public class ChartCommandsRequestorIT {

    @Autowired
    private ChangeCoaEntryPropertiesCommand changeCoaEntryPropertiesCommand;

    @Autowired
    private ChangeCoaNameCommand changeCoaNameCommand;

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Autowired
    private CreateCoaEntryCommand createCoaEntryCommand;

    @Test
    @DisplayName("Execute chart commands through requestor")
    public void successful() {

        final var suffix = Long.toString(System.currentTimeMillis());

        final var createCoaOutput = this.createCoaCommand.execute(
            new CreateCoaCommand.Input("requestor-coa-" + suffix));

        final var createCoaEntryOutput = this.createCoaEntryCommand.execute(
            new CreateCoaEntryCommand.Input(
                createCoaOutput.coaId(),
                "FSP",
                new CoaEntryCode("REQ_COA_" + suffix),
                "Requestor CoA Entry " + suffix,
                "Requestor CoA Entry Description " + suffix,
                AccountType.ASSET));

        final var changeCoaNameOutput = this.changeCoaNameCommand.execute(
            new ChangeCoaNameCommand.Input(
                createCoaOutput.coaId(),
                "requestor-coa-updated-" + suffix));

        final var changeCoaEntryOutput = this.changeCoaEntryPropertiesCommand.execute(
            new ChangeCoaEntryPropertiesCommand.Input(
                createCoaEntryOutput.coaEntryId(),
                "Requestor CoA Entry Updated " + suffix,
                "Requestor CoA Entry Description Updated " + suffix));

        assertNotNull(createCoaOutput.coaId());
        assertNotNull(createCoaEntryOutput.coaEntryId());

        assertEquals(createCoaOutput.coaId(), changeCoaNameOutput.coaId());
        assertEquals(createCoaEntryOutput.coaEntryId(), changeCoaEntryOutput.coaEntryId());
    }

}
