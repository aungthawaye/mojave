package org.mojave.core.accounting.domain.command.chart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryCodeAlreadyExistsException;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryNameAlreadyExistsException;
import org.mojave.core.accounting.contract.exception.chart.CoaIdNotFoundException;
import org.mojave.core.accounting.contract.query.CoaEntryQuery;
import org.mojave.core.accounting.domain.AccountingDomainTestConfiguration;
import org.mojave.core.accounting.domain.BaseIT;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingDomainTestConfiguration.class})
@DisplayName("Create CoA Entry Command Integration Test")
public class CreateCoaEntryCommandIT extends BaseIT {

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Autowired
    private CreateCoaEntryCommand createCoaEntryCommand;

    @Autowired
    private CoaEntryQuery coaEntryQuery;

    @Test
    @DisplayName("Throw when CoA entry code already exists")
    public void coaEntryCodeAlreadyExists() {

        final var coaId = this.createCoa(this.createCoaCommand, "Duplicate Entry Code CoA");

        this.createCoaEntryCommand.execute(new CreateCoaEntryCommand.Input(
            coaId, "FSP", new CoaEntryCode("DUPLICATE_ENTRY_CODE"), "Duplicate Entry Code 01",
            "Duplicate Entry Code 01 description", AccountType.ASSET));

        assertThrows(
            CoaEntryCodeAlreadyExistsException.class,
            () -> this.createCoaEntryCommand.execute(new CreateCoaEntryCommand.Input(
                coaId, "FSP", new CoaEntryCode("DUPLICATE_ENTRY_CODE"), "Duplicate Entry Code 02",
                "Duplicate Entry Code 02 description", AccountType.LIABILITY)));
    }

    @Test
    @DisplayName("Throw when CoA entry name already exists")
    public void coaEntryNameAlreadyExists() {

        final var coaId = this.createCoa(this.createCoaCommand, "Duplicate Entry Name CoA");

        this.createCoaEntryCommand.execute(new CreateCoaEntryCommand.Input(
            coaId, "FSP", new CoaEntryCode("DUPLICATE_ENTRY_NAME_01"), "Duplicate Entry Name",
            "Duplicate Entry Name 01 description", AccountType.ASSET));

        assertThrows(
            CoaEntryNameAlreadyExistsException.class,
            () -> this.createCoaEntryCommand.execute(new CreateCoaEntryCommand.Input(
                coaId, "FSP", new CoaEntryCode("DUPLICATE_ENTRY_NAME_02"), "Duplicate Entry Name",
                "Duplicate Entry Name 02 description", AccountType.LIABILITY)));
    }

    @Test
    @DisplayName("Throw when CoA id cannot be found")
    public void coaIdNotFound() {

        assertThrows(
            CoaIdNotFoundException.class,
            () -> this.createCoaEntryCommand.execute(new CreateCoaEntryCommand.Input(
                new CoaId(Long.MAX_VALUE), "HUB", new CoaEntryCode("HUB_FLOAT"), "Hub Float",
                "Hub Float description", AccountType.ASSET)));
    }

    @Test
    @DisplayName("Create CoA entry successfully")
    public void successful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Operations CoA");
        final var output = this.createCoaEntryCommand.execute(new CreateCoaEntryCommand.Input(
            coaId, "FSP", new CoaEntryCode("FSP_SETTLEMENT"), "FSP Settlement",
            "FSP Settlement description", AccountType.ASSET));
        final var coaEntry = this.coaEntryQuery.get(output.coaEntryId());

        assertNotNull(output.coaEntryId());
        assertEquals("FSP", coaEntry.category());
        assertEquals("FSP_SETTLEMENT", coaEntry.code().value());
        assertEquals(AccountType.ASSET, coaEntry.accountType());
    }

}
