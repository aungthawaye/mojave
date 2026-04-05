package org.mojave.accounting.intercom.producer.command.chart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.accounting.intercom.producer.AccountingIntercomProducerTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingIntercomProducerTestConfiguration.class})
@DisplayName("Create Coa Producer Integration Test")
public class CreateCoaProducerIT {

    @Autowired
    private CreateCoaProducer createCoaProducer;

    @Test
    public void successful() {

        this.createCoaProducer.publish(new CreateCoaCommand.Input("Hub"));
    }

}
