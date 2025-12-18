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
package org.mojave.core.wallet.producer.publisher;

import org.mojave.core.wallet.contract.command.position.RollbackReservationCommand;
import org.mojave.core.wallet.contract.constant.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RollbackReservationPublisher {

    public static final String QUALIFIER = "rollbackReservation";

    private final KafkaTemplate<String, RollbackReservationCommand.Input> kafkaTemplate;

    public RollbackReservationPublisher(
        @Qualifier(QUALIFIER) KafkaTemplate<String, RollbackReservationCommand.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(RollbackReservationCommand.Input input) {

        this.kafkaTemplate.send(
            TopicNames.ROLLBACK_RESERVATION, input.reservationId().getId().toString(), input);

    }

}
