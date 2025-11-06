package io.mojaloop.core.transaction.producer.command;

import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.transaction.contract.command.CommitTransactionCommand;
import io.mojaloop.core.transaction.producer.TestConfiguration;
import io.mojaloop.core.transaction.producer.publisher.CommitTransactionPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestConfiguration.class)
public class CommitTransactionPublisherIT {

    @Autowired
    private CommitTransactionPublisher producer;

    @Test
    public void test(){

        this.producer.publish(new CommitTransactionCommand.Input(new TransactionId(Snowflake.get().nextId()), null));
        this.producer.publish(new CommitTransactionCommand.Input(new TransactionId(Snowflake.get().nextId()), "Something went wrong."));
    }
}
