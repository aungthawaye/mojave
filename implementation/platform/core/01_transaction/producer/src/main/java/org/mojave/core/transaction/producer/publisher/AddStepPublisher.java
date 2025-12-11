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
package org.mojave.core.transaction.producer.publisher;

import org.mojave.core.transaction.contract.command.AddStepCommand;
import org.mojave.core.transaction.contract.constant.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AddStepPublisher {

    public static final String QUALIFIER = "addStep";

    public static boolean ENABLED = true;

    private final KafkaTemplate<String, AddStepCommand.Input> kafkaTemplate;

    public AddStepPublisher(
        @Qualifier(QUALIFIER) KafkaTemplate<String, AddStepCommand.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AddStepCommand.Input input) {

        if (!ENABLED) {
            return;
        }

        this.kafkaTemplate.send(
            TopicNames.ADD_STEP, input.transactionId().getId().toString(), input);

    }

}
