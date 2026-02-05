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
package org.mojave.component.retrofit.debug;

import okhttp3.Dns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public final class DnsDebug implements Dns {

    private static final Logger LOGGER = LoggerFactory.getLogger(DnsDebug.class);

    private final Dns delegate;

    public DnsDebug(Dns delegate) {

        this.delegate = delegate != null ? delegate : Dns.SYSTEM;
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {

        LOGGER.info("TLS-DNS start domain={}", hostname);

        try {

            List<InetAddress> out = this.delegate.lookup(hostname);

            LOGGER.info("TLS-DNS end   domain={} resolved={}", hostname, out);

            return out;

        } catch (UnknownHostException e) {

            LOGGER.error("TLS-DNS fail  domain={} err={}", hostname, e.toString());

            throw e;
        }
    }

}
