/*-
 * ================================================================================
 * Mojave
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

package io.mojaloop.core.transaction.producer.command;

import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.contract.command.OpenTransactionCommand;
import io.mojaloop.core.transaction.producer.TestConfiguration;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transaction.producer.publisher.CloseTransactionPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest(classes = TestConfiguration.class)
public class AddStepPublisherIT {

    @Autowired
    private AddStepPublisher addStepPublisher;

    @Autowired
    private CloseTransactionPublisher closeTransactionPublisher;

    @Autowired
    private OpenTransactionCommand openTransactionCommand;

    @Test
    public void test() {

        for (int i = 0; i < 1000; i++) {

            var output = this.openTransactionCommand.execute(
                new OpenTransactionCommand.Input(TransactionType.FUND_TRANSFER));
            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    output.transactionId(), "step" + i, "c", Map.of("k1", "v1", "k2", "v2"),
                    StepPhase.BEFORE));
            this.closeTransactionPublisher.publish(
                new CloseTransactionCommand.Input(output.transactionId(), null));
        }

    }

}
