package io.mojaloop.common.component.crypto;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Hs256 {

    public static String signature(String secret, byte[] message) {

        byte[] hmacSha256;
        byte[] keyBytes = DigestUtils.sha256(secret);

        try {

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
            mac.init(secretKeySpec);

            hmacSha256 = mac.doFinal(message);

        } catch (Exception e) {

            throw new RuntimeException("Failed to calculate hmac-sha256", e);

        }

        return DigestUtils.sha256Hex(hmacSha256);

    }

    public static boolean verify(String secret, String signature, byte[] payload) {

        return Hs256.signature(secret, payload).equals(signature);
    }

}
