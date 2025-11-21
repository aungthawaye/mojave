package io.mojaloop.core.transfer.domain.command.step.fspiop;

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.spec.core.TransferState;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UnwrapResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnwrapResponse.class);

    public UnwrapResponse() {

    }

    @Transactional
    @Read
    public Output execute(Input input) throws FspiopException {

        LOGGER.info("Unwrapping transfer response from Payee.");

        var response = input.response();
        var state = response.getTransferState();
        var completedTimestamp = response.getCompletedTimestamp();

        if (state != TransferState.RESERVED && state != TransferState.ABORTED) {

            LOGGER.info("Payee responded with invalid Transfer state : [{}]", state);
            throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "Payee responded with invalid Transfer state.");
        }

        var completedAt = Instant.now();

        try {
            completedAt = FspiopDates.fromRequestBody(completedTimestamp);
        } catch (Exception ignored) { }

        LOGGER.info("Unwrapped transfer response from Payee. State : [{}], completedAt : [{}]", state, completedAt);

        return new Output(state, response.getFulfilment(), completedAt);
    }

    public record Input(TransfersIDPutResponse response) { }

    public record Output(TransferState state, String ilpFulfilment, Instant completedAt) { }

}
