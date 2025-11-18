package io.mojaloop.core.transfer.domain.command.internal;

import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.service.api.transfers.RespondTransfers;
import io.mojaloop.fspiop.spec.core.Extension;
import io.mojaloop.fspiop.spec.core.ExtensionList;
import io.mojaloop.fspiop.spec.core.TransferState;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Service
public class PatchPayee {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatchPayee.class);

    private final RespondTransfers respondTransfers;

    public PatchPayee(RespondTransfers respondTransfers) {

        assert respondTransfers != null;

        this.respondTransfers = respondTransfers;
    }

    public Output execute(Input input) throws FspiopException {

        try {

            LOGGER.info("Executing PatchPayee command for transfer : udfTransferId : [{}]", input.udfTransferId.getId());

            var response = new TransfersIDPatchResponse(FspiopDates.forRequestBody(new Date()), input.state);

            var extensions = new ArrayList<Extension>();
            input.extensions.forEach((k, v) -> extensions.add(new Extension(k, v)));

            var extensionList = new ExtensionList(extensions);
            response.setExtensionList(extensionList);

            var payeeBaseUrl = input.payeeFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
            var finalUrl = FspiopUrls.Transfers.patchTransfers(payeeBaseUrl, input.udfTransferId.getId());

            this.respondTransfers.patchTransfers(new Payee(input.payeeFsp.fspCode().value()), finalUrl, response);

            LOGGER.info("PatchPayee command for transfer : udfTransferId : [{}] executed successfully", input.udfTransferId.getId());

        } catch (Exception e) {

            LOGGER.error("Error while executing PatchPayee command for transfer : udfTransferId : [{}]", input.udfTransferId.getId(), e);

            throw e;
        }

        return new Output();
    }

    public record Input(UdfTransferId udfTransferId, FspData payerFsp, FspData payeeFsp, TransferState state, Map<String, String> extensions) { }

    public record Output() { }

}
