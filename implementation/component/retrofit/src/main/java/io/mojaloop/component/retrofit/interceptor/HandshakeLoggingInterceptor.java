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

package io.mojaloop.component.retrofit.interceptor;

import okhttp3.Connection;
import okhttp3.Handshake;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

public final class HandshakeLoggingInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandshakeLoggingInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request req = chain.request();
        String host = req.url().host();

        Connection connBefore = chain.connection();

        if (connBefore != null) {

            this.logConn(host, connBefore);

        } else {

            LOGGER.info("TLS-HS pre  host(SNI)={} conn=<none yet>", host);
        }

        Response resp = chain.proceed(req);

        // Prefer response.handshake(); fall back to connection.handshake()
        Handshake hs = resp.handshake();

        if (hs == null && resp.networkResponse() != null) {

            Connection c = chain.connection();

            if (c != null) {
                hs = c.handshake();
            }
        }

        if (hs != null) {

            LOGGER.info(
                "TLS-HS end  host(SNI)={} version={} cipher={}", host, hs.tlsVersion(),
                hs.cipherSuite());
            logPeerCerts(hs.peerCertificates());

        } else {

            LOGGER.warn("TLS-HS end  host(SNI)={} (no handshake info)", host);
        }

        Connection connAfter = chain.connection();

        if (connAfter != null) {

            LOGGER.info("TLS-CONN route={} protocol={}", connAfter.route(), connAfter.protocol());
        }

        return resp;
    }

    private void logConn(String host, Connection conn) {

        Handshake hs = conn.handshake();

        if (hs != null) {

            LOGGER.info(
                "TLS-HS {pre}  host(SNI)={} version={} cipher={}", host, hs.tlsVersion(),
                hs.cipherSuite());

        } else {

            LOGGER.info("TLS-HS {pre}  host(SNI)={} (no handshake yet)", host);
        }
    }

    private void logPeerCerts(List<Certificate> certs) {

        int i = 0;
        for (Certificate c : certs) {

            if (c instanceof X509Certificate x) {

                String subj = x.getSubjectX500Principal().getName();
                String issuer = x.getIssuerX500Principal().getName();

                LOGGER.info(
                    "TLS-PEER[{}] subject='{}' issuer='{}' notBefore={} notAfter={}", i, subj,
                    issuer, x.getNotBefore(), x.getNotAfter());

                try {

                    Collection<List<?>> sans = x.getSubjectAlternativeNames();

                    if (sans != null) {

                        StringBuilder sb = new StringBuilder();

                        for (List<?> san : sans) {
                            if (san.size() >= 2) {
                                sb.append(san.get(1)).append(", ");
                            }
                        }

                        if (sb.length() > 2) {
                            sb.setLength(sb.length() - 2);
                        }

                        LOGGER.info("TLS-PEER[{}] SANs={}", i, sb);
                    }
                } catch (Exception ignore) { }

            } else {

                LOGGER.info("TLS-PEER[{}] type={}", i, c.getType());
            }

            i++;
        }
    }

}
