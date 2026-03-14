/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.rail.fspiop.quoting.domain.kafka.publisher;

import org.mojave.rail.fspiop.quoting.contract.command.step.UpdateQuotesErrorStep;
import org.mojave.rail.fspiop.quoting.domain.kafka.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateQuotesErrorStepPublisher {

    public static final String QUALIFIER = "updateQuotesErrorStep";

    private final KafkaTemplate<String, UpdateQuotesErrorStep.Input> kafkaTemplate;

    public UpdateQuotesErrorStepPublisher(
        @Qualifier(QUALIFIER) KafkaTemplate<String, UpdateQuotesErrorStep.Input> kafkaTemplate) {

        Objects.requireNonNull(kafkaTemplate);

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(UpdateQuotesErrorStep.Input input) {

        this.kafkaTemplate.send(
            TopicNames.UPDATE_QUOTES_ERROR_STEP, input.udfQuoteId().getId(), input);
    }

}
