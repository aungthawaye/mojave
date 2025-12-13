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
package org.mojave.component.jpa.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.InfrastructureProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource implements InfrastructureProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingDataSource.class);

    @Override
    public Object getWrappedObject() {

        return this.determineTargetDataSource();
    }

    @Override
    protected Object determineCurrentLookupKey() {

        Object key = Context.get();

        LOGGER.debug("determineCurrentLookupKey - key : ({})", key);

        return key;
    }

    public static class Context {

        private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

        public static void clear() {

            CONTEXT_HOLDER.remove();
        }

        public static String get() {

            return CONTEXT_HOLDER.get();
        }

        public static void set(String key) {

            CONTEXT_HOLDER.set(key);
        }

    }

    public static class Keys {

        public static final String READ = "read";

        public static final String WRITE = "write";

    }

}
