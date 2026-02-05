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

import org.mojave.core.settlement.contract.command.record.RequestSettlementInitiationCommand;
import org.mojave.core.settlement.contract.constant.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RequestSettlementInitiationPublisher {

    public static final String QUALIFIER = "requestSettlementInitiation";

    private final KafkaTemplate<String, RequestSettlementInitiationCommand.Input> kafkaTemplate;

    public RequestSettlementInitiationPublisher(@Qualifier(QUALIFIER)
                                                KafkaTemplate<String, RequestSettlementInitiationCommand.Input> kafkaTemplate) {

        Objects.requireNonNull(kafkaTemplate);
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(RequestSettlementInitiationCommand.Input input) {

        this.kafkaTemplate.send(
            TopicNames.REQUEST_SETTLEMENT_INITIATION, input.settlementRecordId().getId().toString(),
            input);
    }

}
