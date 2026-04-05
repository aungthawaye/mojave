package org.mojave.accounting.domain.command.chart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.accounting.contract.exception.chart.CoaNameAlreadyExistsException;
import org.mojave.accounting.contract.query.CoaQuery;
import org.mojave.accounting.domain.AccountingDomainTestConfiguration;
import org.mojave.accounting.domain.BaseIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingDomainTestConfiguration.class})
@DisplayName("Create CoA Command Integration Test")
public class CreateCoaCommandIT extends BaseIT {

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Autowired
    private CoaQuery coaQuery;

    @Test
    @DisplayName("Throw when CoA name already exists")
    public void coaNameAlreadyExists() {

        this.createCoaCommand.execute(new CreateCoaCommand.Input("Treasury CoA"));

        assertThrows(
            CoaNameAlreadyExistsException.class,
            () -> this.createCoaCommand.execute(new CreateCoaCommand.Input("Treasury CoA")));
    }

    @Test
    @DisplayName("Create CoA successfully")
    public void successful() {

        final var output = this.createCoaCommand.execute(new CreateCoaCommand.Input("Primary CoA"));
        final var coa = this.coaQuery.get(output.coaId());

        assertNotNull(output.coaId());
        assertEquals("Primary CoA", coa.name());
        assertEquals(0, coa.entries().length);
    }

}
