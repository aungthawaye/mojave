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

package io.mojaloop.component.tomcat;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class TomcatFactoryConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatFactoryConfigurer.class);

    public static TomcatServletWebServerFactory configure(ApplicationContext parentContext, HostSettings... hostSettings) {

        return new TomcatServletWebServerFactory() {

            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {

                try {

                    var service = tomcat.getService();
                    // remove any auto-added connectors
                    for (Connector c : service.findConnectors()) {
                        service.removeConnector(c);
                    }

                    File tempDir = new File(System.getProperty("java.io.tmpdir"), "tomcat-contexts");

                    if (!tempDir.exists()) {
                        var created = tempDir.mkdirs();
                        LOGGER.info("Created temp dir {} : {}", tempDir.getAbsolutePath(), created);
                    }

                    for (var hostSetting : hostSettings) {

                        var connectorDecorator = hostSetting.connectorDecorator();

                        var standardHost = TomcatFactoryConfigurer.createHost(hostSetting.host(), tempDir);
                        tomcat.getEngine().addChild(standardHost);
                        LOGGER.info("Created host: name [{}] appBase : [{}]", standardHost.getName(), standardHost.getAppBase());

                        var connector = TomcatFactoryConfigurer.createConnector(connectorDecorator.getPort(), hostSetting.host());
                        tomcat.getService().addConnector(connector);
                        LOGGER.info("Created connector: port [{}], host : [{}]", connectorDecorator.getPort(), hostSetting.host());
                        connectorDecorator.decorate(connector);

                        TomcatFactoryConfigurer.configureContext(parentContext,
                                                                 tomcat,
                                                                 standardHost,
                                                                 tempDir,
                                                                 hostSetting.contextPath(),
                                                                 hostSetting.configurations());
                    }

                    return super.getTomcatWebServer(tomcat);

                } catch (Exception e) {
                    throw new RuntimeException("Failed to configure multi-host web server", e);
                }
            }
        };
    }

    private static void configureContext(ApplicationContext parentContext,
                                         Tomcat tomcat,
                                         StandardHost host,
                                         File baseDir,
                                         String contextPath,
                                         Class<?>... configurations) {

        try {

            var hostName = host.getName();

            // Create and configure Tomcat context
            File contextDir = new File(baseDir, hostName);

            if (!contextDir.exists()) {
                var created = contextDir.mkdirs();
                LOGGER.info("Created context dir {} : {}", contextDir.getAbsolutePath(), created);
            }

            var context = new StandardContext();
            context.setPath(contextPath);
            context.setDocBase(contextDir.getAbsolutePath());
            context.addLifecycleListener(new Tomcat.FixContextListener());
            context.setReloadable(false);

            // Add context to host BEFORE creating the application context
            host.addChild(context);

            // Create application context with the parent context
            AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
            appContext.setParent(parentContext);
            appContext.register(configurations);

            // Set the ServletContext in the application context before refreshing
            appContext.setServletContext(context.getServletContext());

            // CRITICAL: Refresh the application context to process @ComponentScan and create beans
            appContext.refresh();

            // Create dispatcher servlet
            var dispatcherServlet = new DispatcherServlet(appContext);

            // Add servlet to context
            var servletName = hostName + "-dispatcher";
            var wrapper = context.createWrapper();

            wrapper.setName(servletName);
            wrapper.setServlet(dispatcherServlet);
            wrapper.setLoadOnStartup(1);

            context.addChild(wrapper);
            context.addServletMappingDecoded("/*", servletName);

            LOGGER.info("Configured context for host [{}] with {} configurations", hostName, configurations.length);

        } catch (Exception e) {
            LOGGER.error("Failed to configure context for host [{}]", host.getName(), e);
            throw new RuntimeException("Failed to configure context for host: " + host.getName(), e);

        }
    }

    private static Connector createConnector(int port, String defaultHost) {

        Connector connector = new Connector(Http11NioProtocol.class.getName());
        connector.setPort(port);
        connector.setProperty("defaultHost", defaultHost);

        return connector;
    }

    private static StandardHost createHost(String hostName, File baseDir) {

        StandardHost host = new StandardHost();

        host.setName(hostName);
        host.setAppBase(new File(baseDir, hostName).getAbsolutePath());
        host.setAutoDeploy(false);
        host.setDeployOnStartup(false);

        return host;
    }

    public record HostSettings(String host, String contextPath, ConnectorDecorator connectorDecorator, Class<?>... configurations) {

    }

}
