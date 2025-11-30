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
package io.mojaloop.component.misc.crypto;

import io.mojaloop.component.misc.handy.ContentLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;

public final class KeyStores {

    private KeyStores() { }

    public static class Base64Pkcs12 {

        public static KeyStore base64(String content, String password)
            throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {

            try (var in = new ByteArrayInputStream(Base64.getMimeDecoder().decode(content))) {

                KeyStore ks = KeyStore.getInstance("PKCS12");

                ks.load(in, password.toCharArray());

                return ks;
            }
        }

        public static KeyStore file(String location, String password)
            throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {

            try (var in = Base64.getMimeDecoder().wrap(ContentLoader.from(location))) {

                KeyStore ks = KeyStore.getInstance("PKCS12");

                ks.load(in, password.toCharArray());

                return ks;
            }
        }

    }

}
