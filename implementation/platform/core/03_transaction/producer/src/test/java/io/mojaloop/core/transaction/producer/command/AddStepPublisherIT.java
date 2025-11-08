package io.mojaloop.core.transaction.producer.command;

import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.TestConfiguration;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest(classes = TestConfiguration.class)
public class AddStepPublisherIT {

    @Autowired
    private AddStepPublisher producer;

    @Test
    public void test() {

        for (int i = 0; i < 1000; i++) {
            this.producer.publish(new AddStepCommand.Input(new TransactionId(Snowflake.get().nextId()), "step" + i, Map.of("k1", "v1", "k2", "v2")));
        }

    }

}
