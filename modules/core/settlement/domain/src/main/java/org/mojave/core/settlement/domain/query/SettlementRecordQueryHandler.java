package org.mojave.core.settlement.domain.query;

import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.settlement.contract.data.SettlementRecordData;
import org.mojave.core.settlement.contract.exception.SettlementRecordNotFoundException;
import org.mojave.core.settlement.contract.query.SettlementRecordQuery;
import org.mojave.core.settlement.domain.model.SettlementRecord;
import org.mojave.core.settlement.domain.repository.SettlementRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SettlementRecordQueryHandler implements SettlementRecordQuery {

    private final SettlementRecordRepository settlementRecordRepository;

    public SettlementRecordQueryHandler(final SettlementRecordRepository settlementRecordRepository) {

        Objects.requireNonNull(settlementRecordRepository);

        this.settlementRecordRepository = settlementRecordRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public SettlementRecordData get(final SettlementRecordId settlementRecordId) {

        return this.settlementRecordRepository
                   .findById(settlementRecordId)
                   .orElseThrow(() -> new SettlementRecordNotFoundException(
                       settlementRecordId))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<SettlementRecordData> get(final TransactionId transactionId) {

        return this.settlementRecordRepository
                   .findAll(SettlementRecordRepository.Filters.withTransactionId(transactionId))
                   .stream()
                   .map(SettlementRecord::convert)
                   .toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<SettlementRecordData> getAll() {

        return this.settlementRecordRepository
                   .findAll()
                   .stream()
                   .map(SettlementRecord::convert)
                   .toList();
    }

}
