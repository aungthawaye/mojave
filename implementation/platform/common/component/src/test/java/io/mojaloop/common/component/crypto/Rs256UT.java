package io.mojaloop.common.component.crypto;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rs256UT {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rs256UT.class);

    @Test
    public void test() throws Exception {

        var keyPair = RsaUtil.generateKeyPair(2048);
        var publicKeyString = RsaUtil.writePublicKeyToString(keyPair.getPublic());
        var privateKeyString = RsaUtil.writePrivateKeyToString(keyPair.getPrivate());
        var publicKey = RsaUtil.readPublicKey(publicKeyString);
        var privateKey = RsaUtil.readPrivateKey(privateKeyString);

        var message = "This is a test message";

        var encrypted = Rs256.encrypt(message.getBytes(), publicKey);
        var decrypted = Rs256.decrypt(encrypted, privateKey);

        LOGGER.debug("decrypted : {}", new String(decrypted));
    }
}
