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

package io.mojaloop.connector.gateway;

import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.connector.gateway.inbound.ConnectorInboundConfiguration;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.Import;

@Import(value = {MiscConfiguration.class, FspiopCommonConfiguration.class})
public class ConnectorGatewayConfiguration {

    public interface RequiredBeans extends ConnectorInboundConfiguration.RequiredBeans, ConnectorOutboundConfiguration.RequiredBeans { }

    public interface RequiredSettings extends ConnectorInboundConfiguration.RequiredSettings, ConnectorOutboundConfiguration.RequiredSettings { }

}
