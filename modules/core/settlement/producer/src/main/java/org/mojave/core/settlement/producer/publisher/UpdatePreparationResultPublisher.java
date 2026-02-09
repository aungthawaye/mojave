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

package org.mojave.core.settlement.producer.publisher;

import org.mojave.core.settlement.contract.command.record.HandleSettlementPreparationCommand;
import org.mojave.core.settlement.contract.constant.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdatePreparationResultPublisher {

    public static final String QUALIFIER = "updatePreparationResult";

    private final KafkaTemplate<String, HandleSettlementPreparationCommand.Input> kafkaTemplate;

    public UpdatePreparationResultPublisher(@Qualifier(QUALIFIER)
                                            KafkaTemplate<String, HandleSettlementPreparationCommand.Input> kafkaTemplate) {

        Objects.requireNonNull(kafkaTemplate);
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(HandleSettlementPreparationCommand.Input input) {

        this.kafkaTemplate.send(
            TopicNames.UPDATE_PREPARATION_RESULT, input.settlementRecordId().getId().toString(),
            input);
    }

}
