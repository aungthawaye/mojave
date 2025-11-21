package io.mojaloop.core.transfer.contract.exception;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;

import java.util.HashMap;
import java.util.Map;

public class TransferIdNotFoundException extends UncheckedDomainException {

    public static final String CODE = "POSITION_ID_NOT_FOUND";

    private static final String TEMPLATE = "Position Id ({0}) cannot be found.";

    private final TransferId transferId;

    public TransferIdNotFoundException(final TransferId transferId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{transferId.getId().toString()}));

        this.transferId = transferId;
    }

    public static TransferIdNotFoundException from(final Map<String, String> extras) {

        final var id = new TransferId(Long.valueOf(extras.get(Keys.TRANSFER_ID)));

        return new TransferIdNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.TRANSFER_ID, this.transferId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String TRANSFER_ID = "transferId";

    }

}
