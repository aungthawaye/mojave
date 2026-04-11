package org.mojave.accounting.intercom.requestor.command.chart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.accounting.intercom.requestor.AccountingIntercomRequestorTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingIntercomRequestorTestConfiguration.class})
@DisplayName("Create Coa Requestor Integration Test")
public class CreateCoaRequestorIT {

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Test
    public void successful(){

        this.createCoaCommand.execute(new CreateCoaCommand.Input("Hub2"));
    }
}
