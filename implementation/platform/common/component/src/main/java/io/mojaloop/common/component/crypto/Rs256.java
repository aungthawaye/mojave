package io.mojaloop.common.component.crypto;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Rs256 {

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

    public static PrivateKey privateKeyFromBase64(String base64PrivateKey) throws Exception {

        byte[] decodedKey = Base64.getDecoder().decode(base64PrivateKey);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }

    // Helper method to convert a PrivateKey to Base64 encoded string
    public static String privateKeyToBase64(PrivateKey privateKey) {

        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static PublicKey publicKeyFromBase64(String base64PublicKey) throws Exception {

        byte[] decodedKey = Base64.getDecoder().decode(base64PublicKey);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
    }

    // Helper method to convert a PublicKey to Base64 encoded string
    public static String publicKeyToBase64(PublicKey publicKey) {

        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    // Sign data with a private key
    public static String sign(String message, PrivateKey privateKey) throws Exception {

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));

        byte[] signedBytes = signature.sign();

        return Base64.getEncoder().encodeToString(signedBytes);
    }

    // Verify the signature with a public key
    public static boolean verify(String message, String sha256Signature, PublicKey publicKey) throws Exception {

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));

        byte[] signedBytes = Base64.getDecoder().decode(sha256Signature);

        return signature.verify(signedBytes);
    }

}
