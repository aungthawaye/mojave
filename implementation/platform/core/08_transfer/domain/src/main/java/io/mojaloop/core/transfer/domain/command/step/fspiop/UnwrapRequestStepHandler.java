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

package io.mojaloop.core.transfer.domain.command.step.fspiop;

import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.transfer.contract.command.step.fspiop.UnwrapRequestStep;
import io.mojaloop.core.transfer.contract.component.interledger.AgreementUnwrapper;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.component.handy.FspiopCurrencies;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.handy.FspiopMoney;
import io.mojaloop.fspiop.component.interledger.Interledger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class UnwrapRequestStepHandler implements UnwrapRequestStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnwrapRequestStepHandler.class);

    private final AgreementUnwrapper agreementUnwrapper;

    public UnwrapRequestStepHandler(AgreementUnwrapper agreementUnwrapper) {

        assert agreementUnwrapper != null;

        this.agreementUnwrapper = agreementUnwrapper;
    }

    @Override
    public UnwrapRequestStep.Output execute(UnwrapRequestStep.Input input) throws FspiopException {

        var startAt = System.nanoTime();

        LOGGER.info("UnwrapRequestStep : input : ({})", ObjectLogger.log(input));

        var payerFspCode = input.payerFsp().fspCode();
        var payeeFspCode = input.payeeFsp().fspCode();

        var request = input.request();

        if (!request.getPayerFsp().equals(payerFspCode.value()) ||
                !request.getPayeeFsp().equals(payeeFspCode.value())) {

            LOGGER.info(
                "FSPs information in the request body and request header must be the same.");
            throw new FspiopException(
                FspiopErrors.GENERIC_VALIDATION_ERROR,
                "FSPs information in the request body and request header must be the same.");
        }

        var currency = request.getAmount().getCurrency();
        var transferAmount = new BigDecimal(request.getAmount().getAmount());

        FspiopMoney.validate(request.getAmount());

        var ilpPacketString = request.getIlpPacket();
        var ilpCondition = request.getCondition();

        var ilpPacket = Interledger.unwrap(ilpPacketString);
        var ilpTransferAmount = Interledger.Amount.deserialize(
            ilpPacket.getAmount(), FspiopCurrencies.get(currency).scale());

        var agreement = this.agreementUnwrapper.unwrap(ilpPacket.getData());

        if (agreement == null) {

            LOGGER.warn("Agreement cannot be unwrapped from ILP packet data.");

            throw new FspiopException(
                FspiopErrors.GENERIC_VALIDATION_ERROR,
                "Agreement cannot be unwrapped from ILP packet data.");
        }

        if (!agreement.quoteId().equals(request.getTransferId())) {

            LOGGER.warn("QuoteId from Agreement does not match the transferId of Transfer request.");

            throw new FspiopException(
                FspiopErrors.GENERIC_VALIDATION_ERROR,
                "QuoteId from Agreement does not match the transferId of Transfer request.");
        }

        if (ilpTransferAmount.subtract(transferAmount).signum() != 0) {

            LOGGER.info("The amount from ILP packet must be equal to the transfer amount.");

            throw new FspiopException(
                FspiopErrors.GENERIC_VALIDATION_ERROR,
                "The amount from ILP packet must be equal to the transfer amount.");
        }

        if ((new BigDecimal(agreement.transferAmount().getAmount()))
                .subtract(transferAmount)
                .signum() != 0) {

            LOGGER.info("The amount from Agreement must be equal to the transfer amount.");

            throw new FspiopException(
                FspiopErrors.GENERIC_VALIDATION_ERROR,
                "The amount from Agreement must be equal to the transfer amount.");
        }

        var expiration = request.getExpiration();

        Instant requestExpiration = null;

        if (expiration != null) {

            requestExpiration = FspiopDates.fromRequestBody(expiration);

            if (requestExpiration.isBefore(Instant.now())) {

                LOGGER.info(
                    "The transfer request from Payer FSP has expired. The expiration is : ({})",
                    expiration);
                throw new FspiopException(
                    FspiopErrors.GENERIC_VALIDATION_ERROR,
                    "The transfer request from Payer FSP has expired. The expiration is : " +
                        expiration);
            }

        }

        var output = new UnwrapRequestStep.Output(
            ilpCondition, ilpPacketString, agreement, requestExpiration);

        var endAt = System.nanoTime();
        LOGGER.info(
            "UnwrapRequestStep : output : ({}), took {} ms", ObjectLogger.log(output),
            (endAt - startAt) / 1_000_000);

        return output;
    }

}
