package org.mojave.rail.fspiop.component.participant.loader;

import org.mojave.rail.fspiop.component.participant.ParticipantContext;
import org.mojave.rail.fspiop.spec.Currency;

import java.util.ArrayList;
import java.util.HashMap;

public class EnvBasedParticipantContextLoader implements ParticipantContext.Loader {

    @Override
    public ParticipantContext load() {

        var hubCode = System.getenv("FSPIOP_HUB_CODE");
        var fspCode = System.getenv("FSPIOP_FSP_CODE");
        var fspName = System.getenv("FSPIOP_FSP_NAME");

        var currencyNames = System.getenv("FSPIOP_CURRENCIES").split(",", -1);
        var currencies = new ArrayList<Currency>();

        for (var currencyName : currencyNames) {
            currencies.add(Currency.valueOf(currencyName));
        }

        var ilpSecret = System.getenv("FSPIOP_ILP_SECRET");
        var signJws = Boolean.parseBoolean(System.getenv("FSPIOP_SIGN_JWS"));
        var verifyJws = Boolean.parseBoolean(System.getenv("FSPIOP_VERIFY_JWS"));
        var privateKeyPem = System.getenv("FSPIOP_PRIVATE_KEY_PEM");

        var fsps = System.getenv("FSPIOP_FSPS").split(",", -1);
        var fspPublicKeyPem = new HashMap<String, String>();

        for (var fsp : fsps) {

            var env = "FSPIOP_PUBLIC_KEY_PEM_OF_" + fsp.toUpperCase();
            var publicKeyPem = System.getenv(env);

            if (publicKeyPem != null) {
                fspPublicKeyPem.put(fsp, publicKeyPem);
            }
        }

        return ParticipantContext.with(
            hubCode, fspCode, fspName, currencies, ilpSecret, signJws, verifyJws, privateKeyPem,
            fspPublicKeyPem);
    }

}
