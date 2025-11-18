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

package io.mojaloop.core.transfer.domain.command;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.intercom.client.api.OpenTransaction;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transaction.producer.publisher.CloseTransactionPublisher;
import io.mojaloop.core.transfer.TransferDomainConfiguration;
import io.mojaloop.core.transfer.contract.command.PostTransfersCommand;
import io.mojaloop.core.transfer.contract.component.interledger.PartyUnwrapper;
import io.mojaloop.core.transfer.domain.command.internal.ReceiveTransfer;
import io.mojaloop.core.transfer.domain.command.internal.ReserveTransfer;
import io.mojaloop.core.transfer.domain.command.internal.UnwrapRequest;
import io.mojaloop.core.transfer.domain.repository.TransferRepository;
import io.mojaloop.core.wallet.intercom.client.api.ReservePosition;
import io.mojaloop.core.wallet.intercom.client.api.RollbackPosition;
import io.mojaloop.core.wallet.store.PositionStore;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopErrorResponder;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.transfers.RespondTransfers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.util.Map;

@Service
public class PostTransfersCommandHandler implements PostTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostTransfersCommandHandler.class);

    private final ParticipantStore participantStore;

    private final PositionStore positionStore;

    private final OpenTransaction openTransaction;

    private final ReservePosition reservePosition;

    private final RollbackPosition rollbackPosition;

    private final ReserveTransfer reserveTransfer;

    private final RespondTransfers respondTransfers;

    private final ForwardRequest forwardRequest;

    private final AddStepPublisher addStepPublisher;

    private final CloseTransactionPublisher closeTransactionPublisher;

    private final PartyUnwrapper partyUnwrapper;

    private final TransferDomainConfiguration.TransferSettings transferSettings;

    private final TransferRepository transferRepository;

    private final PlatformTransactionManager transactionManager;

    private final UnwrapRequest unwrapRequest;

    private final ReceiveTransfer receiveTransfer;

    public PostTransfersCommandHandler(ParticipantStore participantStore,
                                       PositionStore positionStore,
                                       OpenTransaction openTransaction,
                                       ReservePosition reservePosition,
                                       RollbackPosition rollbackPosition,
                                       ReserveTransfer reserveTransfer,
                                       RespondTransfers respondTransfers,
                                       ForwardRequest forwardRequest,
                                       AddStepPublisher addStepPublisher,
                                       CloseTransactionPublisher closeTransactionPublisher,
                                       PartyUnwrapper partyUnwrapper,
                                       TransferDomainConfiguration.TransferSettings transferSettings,
                                       TransferRepository transferRepository,
                                       PlatformTransactionManager transactionManager,
                                       UnwrapRequest unwrapRequest,
                                       ReceiveTransfer receiveTransfer) {

        assert participantStore != null;
        assert positionStore != null;
        assert openTransaction != null;
        assert reservePosition != null;
        assert rollbackPosition != null;
        assert reserveTransfer != null;
        assert respondTransfers != null;
        assert forwardRequest != null;
        assert addStepPublisher != null;
        assert closeTransactionPublisher != null;
        assert partyUnwrapper != null;
        assert transferSettings != null;
        assert transferRepository != null;
        assert transactionManager != null;
        assert unwrapRequest != null;
        assert receiveTransfer != null;

        this.participantStore = participantStore;
        this.positionStore = positionStore;
        this.openTransaction = openTransaction;
        this.reservePosition = reservePosition;
        this.rollbackPosition = rollbackPosition;
        this.reserveTransfer = reserveTransfer;
        this.respondTransfers = respondTransfers;
        this.forwardRequest = forwardRequest;
        this.addStepPublisher = addStepPublisher;
        this.closeTransactionPublisher = closeTransactionPublisher;
        this.partyUnwrapper = partyUnwrapper;
        this.transferSettings = transferSettings;
        this.transferRepository = transferRepository;
        this.transactionManager = transactionManager;
        this.unwrapRequest = unwrapRequest;
        this.receiveTransfer = receiveTransfer;
    }

    @Override
    @Write
    public Output execute(Input input) {

        var udfTransferId = new UdfTransferId(input.transfersPostRequest().getTransferId());

        MDC.put("requestId", udfTransferId.getId());

        LOGGER.info("Executing PostTransfersCommandHandler with input: {}", input);

        TransactionId transactionId = null;
        String transactionIdString = null;

        Instant transactionAt = null;
        String transactionAtString = null;

        PositionUpdateId positionReservationId = null;
        boolean reservedPosition = false;

        Throwable errorOccurred = null;

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);
            LOGGER.debug("Found payer FSP: [{}]", payerFsp);

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);
            LOGGER.debug("Found payee FSP: [{}]", payeeFsp);

            var request = input.transfersPostRequest();

            // 1. (Start) Unwrap the request.
            var unwrapRequestOutput = this.unwrapRequest.execute(new UnwrapRequest.Input(udfTransferId, payerFsp, payeeFsp, request));

            // 2. (Start) Open a transaction. And receive the Transfer.
            var receiveTransferOutput = this.receiveTransfer.execute(
                new ReceiveTransfer.Input(udfTransferId, payerFsp, payeeFsp, unwrapRequestOutput.payerPartyIdInfo(), unwrapRequestOutput.payeePartyIdInfo(),
                    unwrapRequestOutput.currency(), unwrapRequestOutput.transferAmount(), unwrapRequestOutput.requestExpiration()));

            // 3. (Start) Reserve a position of Payer.

            // 3. (End) Reserve a position of Payer.
            // ------------------------------------------------------------------------------------------------------------------------

            // 4. (Start) Update the Transfer as RESERVED

            before.put("positionUpdateId", reservePositionOutput.positionUpdateId().getId().toString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|update-transfer-as-reserved", before, StepPhase.BEFORE));

            before.clear();

            TransactionContext.startNew(this.transactionManager, transfer.getId().toString());
            transfer = this.transferRepository.getReferenceById(transfer.getId());
            transfer.reserved(reservePositionOutput.positionUpdateId());
            this.transferRepository.save(transfer);
            TransactionContext.commit();

            after.put("transferId", transfer.getId().toString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|update-transfer-as-reserved", after, StepPhase.AFTER));

            after.clear();

            LOGGER.info("Transfer updated successfully: udfTransferId : [{}], transferId : [{}]", udfTransferId.getId(), transfer.getId().toString());

            // 4. (End) Update the Transfer as RESERVED
            // ------------------------------------------------------------------------------------------------------------------------

            // 5. (Start) Forward the request to Payee.
            var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("Forwarding request to payee FSP (Url): [{}]", payeeBaseUrl);

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|forward-to-payee", Map.of("url", payeeBaseUrl), StepPhase.BEFORE));

            this.forwardRequest.forward(payeeBaseUrl, input.request());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|forward-to-payee", Map.of("url", payeeBaseUrl), StepPhase.AFTER));

            LOGGER.info("Done forwarding request to payee FSP (Url): [{}]", payeeBaseUrl);

            // 5. (End) Forward the request to Payee.
            // ------------------------------------------------------------------------------------------------------------------------

        } catch (Exception e) {

            LOGGER.error("Exception occurred while executing PostTransfersCommandHandler: [{}]", e.getMessage());

            errorOccurred = e;

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                final var url = FspiopUrls.Transfers.putTransfersError(baseUrl, udfTransferId.getId());

                try {

                    if (transactionId != null) {

                        this.addStepPublisher.publish(
                            new AddStepCommand.Input(transactionId, "post-transfers|send-error-to-payer", Map.of("error", e.getMessage()), StepPhase.BEFORE));

                        FspiopErrorResponder.toPayer(new Payer(payeeFsp.fspCode().value()), e, (payer, error) -> this.respondTransfers.putTransfersError(sendBackTo, url, error));

                        this.addStepPublisher.publish(
                            new AddStepCommand.Input(transactionId, "post-transfers|send-error-to-payer", Map.of("error", e.getMessage()), StepPhase.AFTER));

                    } else {

                        LOGGER.warn("Transaction was not opened. Skipping adding steps to transaction. udfTransactionId : [{}]", udfTransferId.getId());

                        FspiopErrorResponder.toPayer(new Payer(payerFspCode.value()), e, (payer, error) -> this.respondTransfers.putTransfersError(sendBackTo, url, error));
                    }

                } catch (Throwable throwable) {

                    errorOccurred = throwable;

                    LOGGER.error("Something went wrong while sending error response to payer FSP:", throwable);
                }

            }

        } finally {

            if (transactionId != null && errorOccurred != null) {

                LOGGER.error("Closing transaction failed: transactionId : [{}], transactionAt : [{}], error : [{}]", transactionId, transactionAt, errorOccurred.getMessage());

                this.closeTransactionPublisher.publish(new CloseTransactionCommand.Input(transactionId, errorOccurred.getMessage()));

            }
        }

        LOGGER.info("Returning from PostTransfersCommandHandler successfully.");

        MDC.remove("requestId");

        return new Output();
    }

}
