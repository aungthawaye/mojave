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
package org.mojave.connector.gateway;

import org.mojave.connector.gateway.inbound.ConnectorInboundConfiguration;
import org.mojave.connector.gateway.outbound.ConnectorOutboundConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class ConnectorGatewayApplication {

    public static ConfigurableApplicationContext run(String[] args,
                                                     Class<?>[] sharedExtraConfigurations,
                                                     Class<?>[] inboundExtraConfigurations,
                                                     Class<?>[] outboundExtraConfigurations) {

        var coreConfigurations = new Class[]{ConnectorGatewayConfiguration.class};
        var fullSharedConfigurations = new Class[sharedExtraConfigurations.length +
                                                     coreConfigurations.length];

        if (sharedExtraConfigurations.length > 0) {
            System.arraycopy(
                coreConfigurations, 0, fullSharedConfigurations, 0, coreConfigurations.length);
            System.arraycopy(
                sharedExtraConfigurations, 0, fullSharedConfigurations, coreConfigurations.length,
                sharedExtraConfigurations.length);
        }

        var inboundConfigurations = new Class[]{ConnectorInboundConfiguration.class};
        var fullInboundConfigurations = new Class[inboundConfigurations.length +
                                                      inboundExtraConfigurations.length];

        if (inboundExtraConfigurations.length > 0) {
            System.arraycopy(
                inboundConfigurations, 0, fullInboundConfigurations, 0,
                inboundConfigurations.length);
            System.arraycopy(
                inboundExtraConfigurations, 0, fullInboundConfigurations,
                inboundConfigurations.length, inboundExtraConfigurations.length);
        }

        var outboundConfigurations = new Class[]{ConnectorOutboundConfiguration.class};
        var fullOutboundConfigurations = new Class[outboundConfigurations.length +
                                                       outboundExtraConfigurations.length];

        if (outboundExtraConfigurations.length > 0) {
            System.arraycopy(
                outboundConfigurations, 0, fullOutboundConfigurations, 0,
                outboundConfigurations.length);
            System.arraycopy(
                outboundExtraConfigurations, 0, fullOutboundConfigurations,
                outboundConfigurations.length, outboundExtraConfigurations.length);
        }

        return new SpringApplicationBuilder(fullSharedConfigurations)
                   .web(WebApplicationType.NONE)
                   .properties("spring.profiles.active=prod")
                   .child(fullInboundConfigurations)
                   .properties(
                       "spring.application.name=connector-inbound",
                       "management.endpoints.web.base-path=/actuator",
                       "management.endpoints.web.exposure.include=health,info,metrics,prometheus")
                   .web(WebApplicationType.SERVLET)
                   .sibling(fullOutboundConfigurations)
                   .properties(
                       "spring.application.name=connector-outbound",
                       "management.endpoints.web.base-path=/actuator",
                       "management.endpoints.web.exposure.include=health,info,metrics,prometheus")
                   .web(WebApplicationType.SERVLET)
                   .run(args);
    }

}
