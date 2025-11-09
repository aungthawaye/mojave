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
