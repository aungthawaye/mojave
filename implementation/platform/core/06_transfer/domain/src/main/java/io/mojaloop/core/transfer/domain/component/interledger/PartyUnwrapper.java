package io.mojaloop.core.transfer.domain.component.interledger;

import io.mojaloop.fspiop.spec.core.PartyIdInfo;

public interface PartyUnwrapper<T> {

    Parties unwrap(byte[] data);

    record Parties(PartyIdInfo payer, PartyIdInfo payee) { }

}
