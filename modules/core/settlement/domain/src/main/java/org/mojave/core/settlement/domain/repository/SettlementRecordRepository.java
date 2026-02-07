package org.mojave.core.settlement.domain.repository;

import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.transfer.TransferId;
import org.mojave.core.settlement.domain.model.SettlementRecord;
import org.mojave.core.settlement.domain.model.SettlementRecord_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SettlementRecordRepository
    extends JpaRepository<SettlementRecord, SettlementRecordId>,
            JpaSpecificationExecutor<SettlementRecord> {

    class Filters {

        public static Specification<SettlementRecord> withTransactionId(final TransactionId transactionId) {

            return (root, query, cb) -> cb.equal(root.get(SettlementRecord_.transactionId), transactionId);
        }

        public static Specification<SettlementRecord> withTransferId(final TransferId transferId) {

            return (root, query, cb) -> cb.equal(root.get(SettlementRecord_.transferId), transferId);
        }

    }

}
