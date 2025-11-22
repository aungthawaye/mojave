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

package io.mojaloop.core.transaction.producer;

import io.mojaloop.component.kafka.KafkaProducerConfigurer;
import io.mojaloop.core.transaction.intercom.client.TransactionIntercomClientConfiguration;
import io.mojaloop.core.transaction.intercom.client.service.TransactionIntercomService;
import org.springframework.context.annotation.Bean;

public class TestSettings implements TransactionProducerConfiguration.RequiredSettings,
                                     TransactionIntercomClientConfiguration.RequiredSettings {

    @Bean
    @Override
    public KafkaProducerConfigurer.ProducerSettings producerSettings() {

        return new KafkaProducerConfigurer.ProducerSettings("localhost:9092", "all");
    }

    @Bean
    @Override
    public TransactionIntercomService.Settings transactionIntercomServiceSettings() {

        return new TransactionIntercomService.Settings("http://localhost:4302");
    }

}
