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
package org.mojave.connector.sample;

import org.mojave.component.misc.pubsub.PubSubClient;
import org.mojave.component.misc.pubsub.local.LocalPubSub;
import org.mojave.component.misc.pubsub.local.LocalPubSubClient;
import org.mojave.connector.adapter.fsp.client.FspClient;
import org.mojave.connector.gateway.ConnectorGatewayConfiguration;
import org.mojave.connector.sample.inbound.adapter.client.SampleFspClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"org.mojave.connector.sample"})
public class SampleConnectorSharedConfiguration
    implements ConnectorGatewayConfiguration.RequiredBeans {

    public SampleConnectorSharedConfiguration() {

    }

    @Bean
    @Override
    public FspClient fspClient() {

        return new SampleFspClient();
    }

    @Bean
    @Override
    public PubSubClient pubSubClient() {

        return new LocalPubSubClient(new LocalPubSub());
    }

}
