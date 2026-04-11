package org.mojave.accounting.domain.command.chart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.accounting.contract.command.chart.ChangeCoaEntryPropertiesCommand;
import org.mojave.accounting.contract.command.chart.ChangeCoaNameCommand;
import org.mojave.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.accounting.contract.exception.chart.CoaEntryIdNotFoundException;
import org.mojave.accounting.contract.exception.chart.CoaIdNotFoundException;
import org.mojave.accounting.contract.query.CoaEntryQuery;
import org.mojave.accounting.contract.query.CoaQuery;
import org.mojave.accounting.domain.AccountingDomainTestConfiguration;
import org.mojave.accounting.domain.BaseIT;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingDomainTestConfiguration.class})
@DisplayName("Chart Commands Integration Test")
public class ChartCommandIT extends BaseIT {

    @Autowired
    private ChangeCoaEntryPropertiesCommand changeCoaEntryPropertiesCommand;

    @Autowired
    private ChangeCoaNameCommand changeCoaNameCommand;

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Autowired
    private CreateCoaEntryCommand createCoaEntryCommand;

    @Autowired
    private CoaEntryQuery coaEntryQuery;

    @Autowired
    private CoaQuery coaQuery;

    @Test
    @DisplayName("Throw when changing missing CoA entry")
    public void changeCoaEntryPropertiesNotFound() {

        assertThrows(
            CoaEntryIdNotFoundException.class, () -> this.changeCoaEntryPropertiesCommand.execute(
                new ChangeCoaEntryPropertiesCommand.Input(
                    new CoaEntryId(Long.MAX_VALUE), "Missing",
                    "Missing")));
    }

    @Test
    @DisplayName("Change CoA entry properties successfully")
    public void changeCoaEntryPropertiesSuccessful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Change Entry CoA");

        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "CHANGE_ENTRY_01", "Change Entry 01",
            AccountType.ASSET);

        final var output = this.changeCoaEntryPropertiesCommand.execute(
            new ChangeCoaEntryPropertiesCommand.Input(
                coaEntryId, "Updated Entry Name",
                "Updated Entry Description"));

        final var coaEntry = this.coaEntryQuery.get(coaEntryId);

        assertEquals(coaEntryId, output.coaEntryId());
        assertEquals("Updated Entry Name", coaEntry.name());
        assertEquals("Updated Entry Description", coaEntry.description());
    }

    @Test
    @DisplayName("Keep CoA entry properties when optional fields are null")
    public void changeCoaEntryPropertiesWithNullValues() {

        final var coaId = this.createCoa(this.createCoaCommand, "Change Entry Null CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "CHANGE_ENTRY_02", "Change Entry 02",
            AccountType.ASSET);

        this.changeCoaEntryPropertiesCommand.execute(
            new ChangeCoaEntryPropertiesCommand.Input(coaEntryId, null, null));

        final var coaEntry = this.coaEntryQuery.get(coaEntryId);

        assertEquals("Change Entry 02", coaEntry.name());
        assertEquals("Change Entry 02 description", coaEntry.description());
    }

    @Test
    @DisplayName("Throw when changing missing CoA")
    public void changeCoaNameNotFound() {

        assertThrows(
            CoaIdNotFoundException.class, () -> this.changeCoaNameCommand.execute(
                new ChangeCoaNameCommand.Input(new CoaId(Long.MAX_VALUE), "Missing")));
    }

    @Test
    @DisplayName("Change CoA name successfully")
    public void changeCoaNameSuccessful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Original CoA Name");

        final var output = this.changeCoaNameCommand.execute(
            new ChangeCoaNameCommand.Input(coaId, "Updated CoA Name"));
        final var coa = this.coaQuery.get(coaId);

        assertEquals(coaId, output.coaId());
        assertEquals("Updated CoA Name", coa.name());
    }

}
