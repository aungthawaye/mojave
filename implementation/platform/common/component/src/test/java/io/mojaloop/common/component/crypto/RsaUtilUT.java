package io.mojaloop.common.component.crypto;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RsaUtilUT {

    private static final Logger LOGGER = LoggerFactory.getLogger(RsaUtilUT.class);

    @Test
    public void test() throws Exception {

        var keyPair = RsaUtil.generateKeyPair(2048);

        var publicKeyString = RsaUtil.writePublicKeyToString(keyPair.getPublic());
        var privateKeyString = RsaUtil.writePrivateKeyToString(keyPair.getPrivate());

        LOGGER.debug("Public Key: {}", publicKeyString);
        LOGGER.debug("Private Key: {}", privateKeyString);

        var publicKey = RsaUtil.readPublicKey(publicKeyString);
        var privateKey = RsaUtil.readPrivateKey(privateKeyString);

    }

}
