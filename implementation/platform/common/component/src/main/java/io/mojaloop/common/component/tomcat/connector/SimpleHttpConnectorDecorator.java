/*-
 * ================================================================================
 * Mojaloop OSS
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
package io.mojaloop.common.component.tomcat.connector;

import io.mojaloop.common.component.tomcat.ConnectorDecorator;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;

public class SimpleHttpConnectorDecorator implements ConnectorDecorator {

    private final Settings settings;

    public SimpleHttpConnectorDecorator(Settings settings) {

        assert settings != null;

        this.settings = settings;
    }

    @Override
    public void decorate(Connector connector) {

        if (connector.getProtocolHandler() instanceof Http11NioProtocol protocol) {

            connector.setPort(this.settings.port());
            connector.setSecure(false);
            connector.setScheme("http");

            protocol.setMaxThreads(this.getMaxThreads());
            protocol.setConnectionTimeout(this.getConnectionTimeout());
        }
    }

    @Override
    public int getConnectionTimeout() {

        return this.settings.connectionTimeout();
    }

    @Override
    public int getMaxThreads() {

        return this.settings.maxThreads();
    }

    @Override
    public int getPort() {

        return this.settings.port();
    }

    public record Settings(int port, int maxThreads, int connectionTimeout) { }

}
