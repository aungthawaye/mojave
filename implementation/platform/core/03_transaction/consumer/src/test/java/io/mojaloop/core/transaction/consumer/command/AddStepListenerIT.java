package io.mojaloop.core.transaction.consumer.command;

import io.mojaloop.core.transaction.consumer.TestConfiguration;
import io.mojaloop.core.transaction.consumer.listener.AddStepListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestConfiguration.class)
public class AddStepListenerIT {

    @Autowired
    private AddStepListener listener;

    @Test
    public void test() throws InterruptedException{

        Thread.sleep(600_000);
    }
}
