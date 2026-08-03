package org.mojave.core.settlement.domain.query;

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.settlement.contract.data.SettlementRecordData;
import org.mojave.core.settlement.contract.exception.record.SettlementRecordNotFoundException;
import org.mojave.core.settlement.contract.exception.window.SettlementWindowNotFoundException;
import org.mojave.core.settlement.contract.query.SettlementRecordQuery;
import org.mojave.core.settlement.domain.model.record.SettlementRecord;
import org.mojave.core.settlement.domain.repository.SettlementRecordRepository;
import org.mojave.core.settlement.domain.repository.SettlementWindowRepository;
import org.mojave.scheme.rule.identifier.settlement.SettlementRecordId;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SettlementRecordQueryHandler implements SettlementRecordQuery {

    private final SettlementRecordRepository settlementRecordRepository;

    private final SettlementWindowRepository settlementWindowRepository;

    public SettlementRecordQueryHandler(final SettlementRecordRepository settlementRecordRepository,
                                        final SettlementWindowRepository settlementWindowRepository) {

        Objects.requireNonNull(settlementRecordRepository);
        Objects.requireNonNull(settlementWindowRepository);

        this.settlementRecordRepository = settlementRecordRepository;
        this.settlementWindowRepository = settlementWindowRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public SettlementRecordData get(final SettlementRecordId settlementRecordId)
        throws SettlementRecordNotFoundException {

        return this.settlementRecordRepository
                   .findById(settlementRecordId)
                   .orElseThrow(() -> new SettlementRecordNotFoundException(settlementRecordId))
                   .convert();
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public SettlementRecordData get(final TransactionId transactionId)
        throws SettlementRecordNotFoundException {

        return this.settlementRecordRepository
                   .findOne(SettlementRecordRepository.Filters.withTransactionId(transactionId))
                   .orElseThrow(
                       () -> new SettlementRecordNotFoundException(new SettlementRecordId(0L)))
                   .convert();
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public List<SettlementRecordData> get(final SettlementWindowId settlementWindowId) {

        final var settlementWindow = this.settlementWindowRepository
                                         .findById(settlementWindowId)
                                         .orElseThrow(() -> new SettlementWindowNotFoundException(
                                             settlementWindowId));

        return this.settlementRecordRepository
                   .findAll(
                       SettlementRecordRepository.Filters.withSettlementWindow(settlementWindow))
                   .stream()
                   .map(SettlementRecord::convert)
                   .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public List<SettlementRecordData> getAll() {

        return this.settlementRecordRepository
                   .findAll()
                   .stream()
                   .map(SettlementRecord::convert)
                   .toList();
    }

}
