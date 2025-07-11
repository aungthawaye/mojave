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
package io.mojaloop.common.component.crypto;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RsaUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static KeyPair generateKeyPair(int keySize) throws Exception {

        var kpg = KeyPairGenerator.getInstance("RSA", "BC");

        kpg.initialize(keySize);

        return kpg.generateKeyPair();
    }

    public static PrivateKey readPrivateKey(Reader reader) throws Exception {

        try (PEMParser parser = new PEMParser(reader)) {

            Object obj = parser.readObject();

            if (obj instanceof PrivateKeyInfo info) {

                var spec = new PKCS8EncodedKeySpec(info.getEncoded());
                var kf = KeyFactory.getInstance(info.getPrivateKeyAlgorithm().getAlgorithm().getId(), "BC");

                return kf.generatePrivate(spec);
            } else if (obj instanceof PEMKeyPair keyPair) {
                // Handle PKCS#1 format private keys
                var info = PrivateKeyInfo.getInstance(keyPair.getPrivateKeyInfo());
                var spec = new PKCS8EncodedKeySpec(info.getEncoded());
                var kf = KeyFactory.getInstance(info.getPrivateKeyAlgorithm().getAlgorithm().getId(), "BC");

                return kf.generatePrivate(spec);
            }

            throw new IllegalArgumentException("Invalid PEM data: no PrivateKeyInfo found");
        }
    }

    public static PrivateKey readPrivateKey(String pem) throws Exception {

        return readPrivateKey(new java.io.StringReader(pem));
    }

    public static PublicKey readPublicKey(Reader reader) throws Exception {

        try (PEMParser parser = new PEMParser(reader)) {
            Object obj = parser.readObject();
            SubjectPublicKeyInfo spki;
            if (obj instanceof SubjectPublicKeyInfo) {
                spki = (SubjectPublicKeyInfo) obj;
            } else if (obj instanceof RSAPublicKey) {
                // PKCS#1 "RSA PUBLIC KEY"
                RSAPublicKey raw = (RSAPublicKey) obj;
                AlgorithmIdentifier algId = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
                spki = new SubjectPublicKeyInfo(algId, raw);
            } else if (obj instanceof PEMKeyPair) {
                // Extract public part from a PEM key pair
                spki = ((PEMKeyPair) obj).getPublicKeyInfo();
            } else if (obj instanceof ASN1Sequence) {
                // Generic ASN1 sequence: try to parse as SPKI
                spki = SubjectPublicKeyInfo.getInstance(obj);
            } else {
                throw new PEMException("Unsupported PEM object: " + obj.getClass().getName());
            }
            X509EncodedKeySpec spec = new X509EncodedKeySpec(spki.getEncoded());
            return KeyFactory.getInstance(spki.getAlgorithm().getAlgorithm().getId(), "BC").generatePublic(spec);
        }
    }

    public static PublicKey readPublicKey(String pem) throws Exception {

        return readPublicKey(new java.io.StringReader(pem));
    }

    public static void writePrivateKey(PrivateKey key, Writer writer) throws IOException {
        // Use JcaPEMWriter directly to properly handle the key format
        try (var pemWriter = new JcaPEMWriter(writer)) {
            pemWriter.writeObject(key);
        }
    }

    public static String writePrivateKeyToString(PrivateKey key) throws IOException {

        var sw = new StringWriter();

        writePrivateKey(key, sw);

        return sw.toString();
    }

    public static void writePublicKey(PublicKey key, Writer writer) throws IOException {
        // Use JcaPEMWriter directly to properly handle the key format
        try (JcaPEMWriter pem = new JcaPEMWriter(writer)) {
            pem.writeObject(key);
        }
    }

    public static String writePublicKeyToString(PublicKey key) throws IOException {

        var sw = new StringWriter();

        writePublicKey(key, sw);

        return sw.toString();
    }

}
