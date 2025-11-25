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

package io.mojaloop.core.transaction.producer.publisher;

import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.constant.TopicNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AddStepPublisher {

    public static final String QUALIFIER = "addStep";

    private static final Logger LOGGER = LoggerFactory.getLogger(AddStepPublisher.class);

    private final KafkaTemplate<String, AddStepCommand.Input> kafkaTemplate;

    public AddStepPublisher(@Qualifier(QUALIFIER) KafkaTemplate<String, AddStepCommand.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AddStepCommand.Input input) {

        var startAt = System.nanoTime();
        LOGGER.info("AddStepPublisher : input: ({})", ObjectLogger.log(input));

        this.kafkaTemplate.send(
            TopicNames.ADD_STEP, input.transactionId().getId().toString(), input);

        var endAt = System.nanoTime();
        LOGGER.info("AddStepPublisher : done , took {} ms", (endAt - startAt) / 1_000_000);

    }

}
