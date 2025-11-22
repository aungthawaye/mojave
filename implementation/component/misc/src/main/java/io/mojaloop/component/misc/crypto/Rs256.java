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

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Rs256 {

    private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";

    private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

    private static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";

    private static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";

    private static final int PEM_LINE_LENGTH = 64;

    // Decrypt data with a private key
    public static byte[] decrypt(byte[] message, PrivateKey privateKey) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(message);
    }

    // Encrypt data with a public key
    public static byte[] encrypt(byte[] message, PublicKey publicKey) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(message);
    }

    // Extract the Base64 data from a PEM string
    private static String extractBase64FromPem(String pem) {
        // Remove begin and end markers, newlines, and any whitespace

        return pem.replace(BEGIN_PRIVATE_KEY, "").replace(END_PRIVATE_KEY, "").replace(BEGIN_PUBLIC_KEY, "").replace(END_PUBLIC_KEY, "").replaceAll("\\s", "");
    }

    // Helper method to format a Base64 string as PEM
    private static String formatPem(String base64Data, String beginMarker, String endMarker) {

        StringBuilder sb = new StringBuilder();
        sb.append(beginMarker).append("\n");

        // Split the Base64 string into lines of PEM_LINE_LENGTH characters
        for (int i = 0; i < base64Data.length(); i += PEM_LINE_LENGTH) {
            int endIndex = Math.min(i + PEM_LINE_LENGTH, base64Data.length());
            sb.append(base64Data, i, endIndex).append("\n");
        }

        sb.append(endMarker);
        return sb.toString();
    }

    // Generate RSA key pairs
    public static KeyPair generateKeyPair(int size) throws NoSuchAlgorithmException {

        switch (size) {
            case 1024:
            case 2048:
            case 3072:
            case 4096:
                break;
            default:
                throw new NoSuchAlgorithmException("RSA key size must be 1024, 2048, 3072, or 4096");
        }

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(size);

        return keyGen.generateKeyPair();
    }

    public static PrivateKey privateKeyFromBase64(String base64PrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] decodedKey = Base64.getDecoder().decode(base64PrivateKey);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }

    // Convert a PEM string to a PrivateKey
    public static PrivateKey privateKeyFromPem(String pem) throws NoSuchAlgorithmException, InvalidKeySpecException {

        String base64Key = extractBase64FromPem(pem);
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }

    // Helper method to convert a PrivateKey to Base64 encoded string
    public static String privateKeyToBase64(PrivateKey privateKey) {

        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    // Convert a PrivateKey to PEM format
    public static String privateKeyToPem(PrivateKey privateKey) {

        String base64Key = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        return formatPem(base64Key, BEGIN_PRIVATE_KEY, END_PRIVATE_KEY);
    }

    public static PublicKey publicKeyFromBase64(String base64PublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] decodedKey = Base64.getDecoder().decode(base64PublicKey);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
    }

    // Convert a PEM string to a PublicKey
    public static PublicKey publicKeyFromPem(String pem) throws NoSuchAlgorithmException, InvalidKeySpecException {

        String base64Key = extractBase64FromPem(pem);
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
    }

    // Helper method to convert a PublicKey to Base64 encoded string
    public static String publicKeyToBase64(PublicKey publicKey) {

        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    // Convert a PublicKey to PEM format
    public static String publicKeyToPem(PublicKey publicKey) {

        String base64Key = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return formatPem(base64Key, BEGIN_PUBLIC_KEY, END_PUBLIC_KEY);
    }

    // Sign data with a private key
    public static String sign(PrivateKey privateKey, String message) throws Exception {

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));

        byte[] signedBytes = signature.sign();

        return Base64.getEncoder().encodeToString(signedBytes);
    }

    // Verify the signature with a public key
    public static boolean verify(PublicKey publicKey, String message, String sha256Signature) throws Exception {

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));

        byte[] signedBytes = Base64.getDecoder().decode(sha256Signature);

        return signature.verify(signedBytes);
    }

}
