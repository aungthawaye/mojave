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
package org.mojave.component.misc.crypto;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.EdECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public final class KeyPairs {

    private KeyPairs() {

    }

    public static int checkKeySize(PublicKey publicKey) {

        return switch (publicKey) {
            case RSAPublicKey rsaPublicKey -> rsaPublicKey.getModulus().bitLength();

            case ECPublicKey ecPublicKey ->
                ecPublicKey.getParams().getCurve().getField().getFieldSize();

            case EdECPublicKey edECPublicKey -> {

                String alg = edECPublicKey.getAlgorithm(); // "Ed25519", "Ed448", etc.

                yield switch (alg) {
                    case "Ed25519" -> 255;
                    case "Ed448" -> 448;
                    default -> throw new IllegalArgumentException("Unsupported key type: " + alg);
                };
            }
            default -> throw new IllegalArgumentException("Unsupported key type");
        };

    }

    public static PrivateKey privateKeyOf(String pem, String algorithm)
        throws NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] der = Pem.from(pem);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance(algorithm);

        return kf.generatePrivate(spec);
    }

    public static PublicKey publicKeyOf(String pem, String algorithm)
        throws NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] der = Pem.from(pem);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance(algorithm);

        return kf.generatePublic(spec);
    }

    public static class Rsa {

        public static KeyPair generate(Size size) throws NoSuchAlgorithmException {

            var keyGen = KeyPairGenerator.getInstance("RSA");

            int keySize = switch (size) {
                case SIZE_2048 -> 2048;
                case SIZE_3072 -> 3072;
                case SIZE_4096 -> 4096;
            };

            keyGen.initialize(keySize, new SecureRandom());

            return keyGen.generateKeyPair();
        }

        public static PrivateKey privateKeyOf(String pem)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

            return KeyPairs.privateKeyOf(pem, "RSA");
        }

        public static PublicKey publicKeyOf(String pem)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

            return KeyPairs.publicKeyOf(pem, "RSA");
        }

        public enum Size {
            SIZE_2048,
            SIZE_3072,
            SIZE_4096
        }

    }

    public static class EllipticCurve {

        public static KeyPair generate(final Curve curve) throws Exception {

            var keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(new ECGenParameterSpec(curve.jcaName()), new SecureRandom());
            return keyGen.generateKeyPair();
        }

        public static PrivateKey privateKeyOf(String pem)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

            return KeyPairs.privateKeyOf(pem, "EC");
        }

        public static PublicKey publicKeyOf(String pem)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

            return KeyPairs.publicKeyOf(pem, "EC");
        }

        public enum Curve {
            P256("secp256r1"),
            P384("secp384r1"),
            P521("secp521r1");

            private final String jcaName;

            Curve(String jcaName) {

                this.jcaName = jcaName;
            }

            public String jcaName() {

                return jcaName;
            }
        }

    }

    public static class EdDsa {

        public static KeyPair generate(Curve curve) throws NoSuchAlgorithmException {

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(curve.jcaName());
            return keyGen.generateKeyPair();
        }

        public static PrivateKey privateKeyOf(String pem, Curve curve)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

            return KeyPairs.privateKeyOf(pem, curve.jcaName());
        }

        public static PublicKey publicKeyOf(String pem, Curve curve)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

            return KeyPairs.publicKeyOf(pem, curve.jcaName());
        }

        public enum Curve {
            ED25519("Ed25519"),
            ED448("Ed448");

            private final String jcaName;

            Curve(String jcaName) {

                this.jcaName = jcaName;
            }

            public String jcaName() {

                return jcaName;
            }
        }

    }

}
