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

import io.mojaloop.component.jpa.routing.RoutingDataSource;
import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transaction.producer.publisher.CloseTransactionPublisher;
import io.mojaloop.core.transfer.contract.command.PutTransfersCommand;
import io.mojaloop.core.transfer.domain.command.internal.CommitTransfer;
import io.mojaloop.core.transfer.domain.model.Transfer;
import io.mojaloop.core.transfer.domain.repository.TransferRepository;
import io.mojaloop.core.wallet.contract.command.position.CommitPositionCommand;
import io.mojaloop.core.wallet.contract.command.position.DecreasePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.RollbackPositionCommand;
import io.mojaloop.core.wallet.intercom.client.exception.WalletIntercomClientException;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.handy.FspiopErrorResponder;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.spec.core.TransferState;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.util.Map;

@Service
public class PutTransfersCommandHandler implements PutTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostTransfersCommandHandler.class);

    private final ParticipantStore participantStore;

    private final CommitTransfer commitTransfer;

    private final AddStepPublisher addStepPublisher;

    private final CloseTransactionPublisher closeTransactionPublisher;

    private final TransferRepository transferRepository;

    private final PlatformTransactionManager transactionManager;

    public PutTransfersCommandHandler(ParticipantStore participantStore,
                                      CommitTransfer commitTransfer,
                                      AddStepPublisher addStepPublisher,
                                      CloseTransactionPublisher closeTransactionPublisher,
                                      TransferRepository transferRepository,
                                      PlatformTransactionManager transactionManager) {

        assert participantStore != null;
        assert commitTransfer != null;
        assert addStepPublisher != null;
        assert closeTransactionPublisher != null;
        assert transferRepository != null;
        assert transactionManager != null;

        this.participantStore = participantStore;
        this.commitTransfer = commitTransfer;
        this.addStepPublisher = addStepPublisher;
        this.closeTransactionPublisher = closeTransactionPublisher;
        this.transferRepository = transferRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    @Write
    public Output execute(Input input) {

        var udfTransferId = input.udfTransferId();

        MDC.put("requestId", udfTransferId.getId());

        LOGGER.info("({}) Executing PutTransfersCommandHandler with input: [{}]", udfTransferId.getId(), input);

        Transfer transfer = null;
        TransactionId transactionId = null;
        Throwable errorOccurred = null;

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        boolean decreasedPosition = false;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);
            LOGGER.debug("({}) Found payer FSP: [{}]", udfTransferId.getId(), payerFsp);

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);
            LOGGER.debug("({}) Found payee FSP: [{}]", udfTransferId.getId(), payeeFsp);

            var putTransfersResponse = input.transfersIDPutResponse();

            TransactionContext.startNew(this.transactionManager, udfTransferId.getId(), RoutingDataSource.Keys.WRITE);
            var optTransfer = this.transferRepository.findOne(TransferRepository.Filters.withUdfTransferId(udfTransferId));
            TransactionContext.commit();

            if (optTransfer.isEmpty()) {

                LOGGER.warn("({}) Receiving non-existence Transfer. Just ignore it.", udfTransferId.getId());
                LOGGER.info("({}) Returning from PutTransfersCommandHandler (non-existence Transfer).", udfTransferId.getId());

                return new Output();
            }

            var transferState = putTransfersResponse.getTransferState();
            var completedTimestamp = putTransfersResponse.getCompletedTimestamp();

            if (transferState != TransferState.RESERVED && transferState != TransferState.ABORTED) {

                throw new FspiopException(FspiopErrors.GENERIC_PAYEE_ERROR,
                    "The transfer result from Payee FSP has no valid transfer state. State must be either RESERVED or ABORTED.");
            }

            transfer = optTransfer.get();

            // Validation is OK.
            if (transferState == TransferState.RESERVED) {
                // Commit the position of Payer. And then, decrease the position of Payee.
                this.commitTransfer.commit(transfer, putTransfersResponse);

            } else {
                // Abort the transaction.
                this.abortTransaction(udfTransferId, transfer);
            }

        } catch (Exception e) {

            LOGGER.error("({}) Exception occurred while executing PutTransfersCommandHandler: [{}]", udfTransferId.getId(), e.getMessage());

            errorOccurred = e;

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                final var url = FspiopUrls.newUrl(baseUrl, "transfers/" + udfTransferId.getId() + "/error");

                try {

                    if (transaction != null) {

                        // Roll back the position if the position has been reserved.
                        if (reservedPosition) {

                            this.rollbackPosition(udfTransferId, transaction.transactionId(), positionReservationId, new PositionUpdateId(Snowflake.get().nextId()));

                        }

                        // Then, send an error response to Payer.
                        this.addStepPublisher.publish(
                            new AddStepCommand.Input(transaction.transactionId(), "post-transfers|send-error-to-payer", Map.of("error", e.getMessage()), StepPhase.BEFORE));

                        FspiopErrorResponder.toPayer(new Payer(payerFspCode.value()), e, (payer, error) -> this.respondTransfers.putTransfersError(sendBackTo, url, error));

                        this.addStepPublisher.publish(
                            new AddStepCommand.Input(transaction.transactionId(), "post-transfers|send-error-to-payer", Map.of("error", e.getMessage()), StepPhase.AFTER));

                    } else {

                        LOGGER.warn("({}) Transaction was not opened. Skipping adding steps to transaction.", udfTransferId.getId());
                        FspiopErrorResponder.toPayer(new Payer(payerFspCode.value()), e, (payer, error) -> this.respondTransfers.putTransfersError(sendBackTo, url, error));
                    }

                } catch (Throwable throwable) {

                    errorOccurred = throwable;

                    LOGGER.error("({}) Something went wrong while sending error response to payer FSP: {}", udfTransferId.getId(), throwable);
                }

            }
        } finally {

        }

        LOGGER.info("Returning from PutTransfersCommandHandler successfully.");

        MDC.remove("requestId");

        return new Output();
    }

    private void abortTransaction(UdfTransferId udfTransferId, Transfer transfer) {

    }

    private CommitPositionCommand.Output commitPayerPosition(Transfer transfer) throws FspiopException {

        try {

            LOGGER.info("Committing reserved position : positionId : [{}]", transfer.getReservationId().getId().toString());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(transfer.getTransactionId(), "put-transfer|commit-position", Map.of("reservationId", transfer.getReservationId().getId().toString()),
                    StepPhase.BEFORE));

            var output = this.commitPosition.execute(new CommitPositionCommand.Input(transfer.getReservationId(), new PositionUpdateId(Snowflake.get().nextId()), null));

            this.addStepPublisher.publish(
                new AddStepCommand.Input(transfer.getTransactionId(), "put-transfer|commit-position", Map.of("payerCommitId", output.positionUpdateId().getId().toString()),
                    StepPhase.AFTER));

            LOGGER.info("({}) Committed reserved position successfully : positionId : [{}]", transfer.getReservationId().getId().toString(),
                output.positionUpdateId().getId().toString());

            return output;

        } catch (Exception e) {

            LOGGER.error("Failed to commit payer position : positionId : [{}]", transfer.getReservationId().getId().toString());

            throw new FspiopException(FspiopErrors.INTERNAL_SERVER_ERROR);
        }

    }

    private void commitTransaction(UdfTransferId udfTransferId, FspData payeeFsp, Transfer transfer, TransfersIDPutResponse putTransfersResponse) throws FspiopException {

        var committedPosition = false;
        var decreasedPosition = false;

        try {

            Instant transferCompletedAt = Instant.now();

            if (putTransfersResponse.getCompletedTimestamp() != null) {
                try {
                    transferCompletedAt = FspiopDates.fromRequestBody(putTransfersResponse.getCompletedTimestamp());
                } catch (Exception ignored) { }
            }

            var commitOutput = this.commitPayerPosition(transfer);
            var payerCommitId = commitOutput.positionUpdateId();
            committedPosition = true;

            var decreaseOutput = this.decreasePayeePosition(payeeFsp, transfer);
            var payeeCommitId = decreaseOutput.positionUpdateId();
            decreasedPosition = true;

            // Update the Transfer data in the database.
            this.addStepPublisher.publish(new AddStepCommand.Input(transfer.getTransactionId(), "put-transfer|commit-transaction",
                Map.of("payerCommitId", payerCommitId.getId().toString(), "payeeCommitId", payeeCommitId.getId().toString()), StepPhase.BEFORE));

            TransactionContext.startNew(this.transactionManager, udfTransferId.getId(), RoutingDataSource.Keys.WRITE);

            transfer = this.transferRepository.getReferenceById(transfer.getId());
            transfer.committed(putTransfersResponse.getFulfilment(), payerCommitId, payeeCommitId, transferCompletedAt);

            this.transferRepository.save(transfer);
            TransactionContext.commit();

            this.addStepPublisher.publish(new AddStepCommand.Input(transfer.getTransactionId(), "put-transfer|commit-transaction", Map.of("-", "-"), StepPhase.AFTER));

        } catch (Exception e) {

        }
    }

    private DecreasePositionCommand.Output decreasePayeePosition(FspData payeeFsp, Transfer transfer) throws FspiopException {

        try {
            LOGGER.info("Decreasing payee position : positionId : [{}]", transfer.getReservationId().getId().toString());

            var payeePosition = this.positionStore.get(new WalletOwnerId(payeeFsp.fspId().getId()), transfer.getCurrency());
            var payeePositionId = payeePosition.positionId();
            var positionUpdateId = new PositionUpdateId(Snowflake.get().nextId());

            this.addStepPublisher.publish(new AddStepCommand.Input(transfer.getTransactionId(), "put-transfer|decrease-position",
                Map.of("payeePositionId", payeePositionId.getId().toString(), "positionUpdateId", positionUpdateId.getId().toString()), StepPhase.BEFORE));

            var transferAmount = transfer.getTransferAmount();

            var output = this.decreasePosition.execute(new DecreasePositionCommand.Input(payeePositionId, transferAmount, transfer.getTransactionId(), transfer.getTransactionAt(),
                "Receiving : " + transferAmount.stripTrailingZeros().toPlainString() + " " + transfer.getCurrency().name()));

            var payeeCommitId = output.positionUpdateId();

            this.addStepPublisher.publish(
                new AddStepCommand.Input(transfer.getTransactionId(), "put-transfer|decrease-position", Map.of("payeeCommitId", payeeCommitId.getId().toString()),
                    StepPhase.AFTER));

            LOGGER.info("Committed decrease payee position successfully : positionId : [{}]", payeeCommitId.getId().toString());

            return output;

        } catch (Exception e) {

            LOGGER.error("Failed to decrease payee position : positionId : [{}]", transfer.getReservationId().getId().toString());

            throw new FspiopException(FspiopErrors.INTERNAL_SERVER_ERROR);

        }
    }

    private void rollbackPayerPosition(Transfer transfer) {

        LOGGER.info("Rolling back reserved position : positionId : [{}]", transfer.getReservationId().getId().toString());

        try {

            this.rollbackPosition.execute(new RollbackPositionCommand.Input(transfer.getReservationId(), new PositionUpdateId(Snowflake.get().nextId()), null));

        } catch (WalletIntercomClientException e) {

        } catch (Exception e) { }
    }

}
