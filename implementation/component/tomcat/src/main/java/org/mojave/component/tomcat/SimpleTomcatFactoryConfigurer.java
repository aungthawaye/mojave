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
package org.mojave.component.tomcat;

import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

public class SimpleTomcatFactoryConfigurer {

    public static WebServerFactoryCustomizer<TomcatServletWebServerFactory> configure(ServerSettings settings) {

        return factory -> {

            // Basic things
            factory.setPort(settings.portNo());

            // ---------- Connection & thread limits ----------
            factory.addConnectorCustomizers(connector -> {
                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();

                // Thread pool (workers serving requests)
                protocol.setMaxThreads(settings.maxThreads());          // e.g. 200
                protocol.setMinSpareThreads(settings.minSpareThreads()); // e.g. 20

                // Connection limits
                protocol.setMaxConnections(settings.maxConnections());   // e.g. 10_000
                protocol.setConnectionTimeout(settings.connectionTimeoutMillis()); // e.g. 30_000

                Optionally:
                protocol.setAcceptCount(
                    settings.acceptCount());  // queue size when all threads busy
                protocol.setKeepAliveTimeout(20_000);
            });

        };
    }

    public record ServerSettings(int portNo,
                                 int maxThreads,
                                 int minSpareThreads,
                                 int maxConnections,
                                 int connectionTimeoutMillis,
                                 int acceptCount) { }

}
