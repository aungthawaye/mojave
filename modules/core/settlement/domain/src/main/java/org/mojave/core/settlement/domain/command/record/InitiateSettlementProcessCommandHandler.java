package org.mojave.core.settlement.domain.command.record;

import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.core.settlement.contract.command.definition.FindSettlementProviderCommand;
import org.mojave.core.settlement.contract.command.record.InitiateSettlementProcessCommand;
import org.mojave.core.settlement.contract.exception.InitiateSettlementProcessInvalidInputException;
import org.mojave.core.settlement.contract.exception.SettlementProviderDispatchFailedException;
import org.mojave.core.settlement.contract.exception.SettlementProviderEndpointNotFoundException;
import org.mojave.core.settlement.contract.exception.SettlementProviderIdNotFoundException;
import org.mojave.core.settlement.domain.command.definition.FindSettlementProviderCommandHandler;
import org.mojave.core.settlement.domain.component.provider.SettlementProvider;
import org.mojave.core.settlement.domain.model.SettlementRecord;
import org.mojave.core.settlement.domain.repository.SettlementRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class InitiateSettlementProcessCommandHandler implements InitiateSettlementProcessCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        InitiateSettlementProcessCommandHandler.class);

    private final SettlementRecordRepository settlementRecordRepository;

    private final FindSettlementProviderCommandHandler findSettlementProviderCommandHandler;

    private final SettlementProvider settlementProvider;

    private final ObjectProvider<ParticipantStore> participantStoreProvider;

    public InitiateSettlementProcessCommandHandler(final SettlementRecordRepository settlementRecordRepository,
                                                   final FindSettlementProviderCommandHandler findSettlementProviderCommandHandler,
                                                   final SettlementProvider settlementProvider,
                                                   final ObjectProvider<ParticipantStore> participantStoreProvider) {

        Objects.requireNonNull(settlementRecordRepository);
        Objects.requireNonNull(findSettlementProviderCommandHandler);
        Objects.requireNonNull(settlementProvider);
        Objects.requireNonNull(participantStoreProvider);

        this.settlementRecordRepository = settlementRecordRepository;
        this.findSettlementProviderCommandHandler = findSettlementProviderCommandHandler;
        this.settlementProvider = settlementProvider;
        this.participantStoreProvider = participantStoreProvider;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("InitiateSettlementProcessCommand : input: ({})", ObjectLogger.log(input));

        if (input.transactionId() == null) {
            throw new InitiateSettlementProcessInvalidInputException(
                "transactionId",
                "must not be null");
        }

        if (input.lines() == null || input.lines().isEmpty()) {
            throw new InitiateSettlementProcessInvalidInputException(
                "lines",
                "must not be null or empty");
        }

        final var providerOutput = this.findSettlementProviderCommandHandler.execute(
            new FindSettlementProviderCommand.Input(
                input.currency(), input.transactionAt(), input.payerFspId(), input.payeeFspId(),
                input.payerFspGroupId(), input.payeeFspGroupId()));

        final var settlementProviderId = providerOutput.settlementProviderId();

        if (settlementProviderId == null) {
            throw new SettlementProviderIdNotFoundException(null);
        }

        final var settlementRecordIds = new ArrayList<SettlementRecordId>();
        final var settlementRecords = new ArrayList<SettlementRecord>();

        for (final var line : input.lines()) {

            if (line == null) {
                throw new InitiateSettlementProcessInvalidInputException(
                    "line",
                    "must not be null");
            }

            if (line.lineNo() == null) {
                throw new InitiateSettlementProcessInvalidInputException(
                    "lineNo",
                    "must not be null");
            }

            final var idempotencySpec = SettlementRecordRepository.Filters
                                            .withTransactionId(input.transactionId())
                                            .and(SettlementRecordRepository.Filters.withLineNo(
                                                line.lineNo()));

            final var existing = this.settlementRecordRepository.findOne(idempotencySpec);

            if (existing.isPresent()) {
                final var settlementRecord = existing.get();
                settlementRecordIds.add(settlementRecord.getId());
                settlementRecords.add(settlementRecord);
                continue;
            }

            final var settlementRecord = new SettlementRecord(
                input.settlementType(), input.payerFspId(), input.payeeFspId(), line.partyFspId(),
                line.liquidityDirection(), line.amountType(), line.lineNo(), input.currency(),
                line.amount(), input.transferId(), input.transactionId(), input.transactionAt(),
                settlementProviderId);

            settlementRecordIds.add(settlementRecord.getId());
            settlementRecords.add(settlementRecord);
        }

        final var recordsToDispatch = settlementRecords
                                          .stream()
                                          .filter(settlementRecord ->
                                                      settlementRecord.getInitiatedAt() == null)
                                          .toList();

        if (!recordsToDispatch.isEmpty()) {

            final var participantStore = this.participantStoreProvider.getIfAvailable();

            if (participantStore == null) {

                this.markDispatchFailure(recordsToDispatch, "ParticipantStore is not available.");
                this.settlementRecordRepository.saveAll(settlementRecords);

                throw new SettlementProviderDispatchFailedException(
                    settlementProviderId, null,
                    "ParticipantStore is not available.");
            }

            final var sspData = participantStore.getSspData(settlementProviderId);

            if (sspData == null || sspData.baseUrl() == null || sspData.baseUrl().isBlank()) {

                this.markDispatchFailure(
                    recordsToDispatch,
                    "Settlement provider endpoint not found.");
                this.settlementRecordRepository.saveAll(settlementRecords);

                throw new SettlementProviderEndpointNotFoundException(settlementProviderId);
            }

            final var endpoint = sspData.baseUrl();

            try {

                this.settlementProvider.initiateSettlementProcess(endpoint, input);

                for (final var settlementRecord : recordsToDispatch) {
                    settlementRecord.markInitiated(null);
                }

            } catch (Exception e) {

                final var reason =
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();

                this.markDispatchFailure(recordsToDispatch, reason);
                this.settlementRecordRepository.saveAll(settlementRecords);

                throw new SettlementProviderDispatchFailedException(
                    settlementProviderId, endpoint,
                    reason);
            }
        }

        this.settlementRecordRepository.saveAll(settlementRecords);

        final var output = new Output(settlementRecordIds, settlementProviderId);

        LOGGER.info("InitiateSettlementProcessCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

    private void markDispatchFailure(final List<SettlementRecord> settlementRecords,
                                     final String error) {

        for (final var settlementRecord : settlementRecords) {
            settlementRecord.markError(error);
        }
    }

}
