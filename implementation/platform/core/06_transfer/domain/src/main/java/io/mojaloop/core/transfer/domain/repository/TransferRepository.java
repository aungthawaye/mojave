package io.mojaloop.core.transfer.domain.repository;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.transfer.domain.model.Transfer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, TransferId>, JpaSpecificationExecutor<Transfer> {

    class Filters {

        public static Specification<Transfer> withTransactionId(TransactionId transactionId) {

            return (root, query, cb) -> cb.equal(root.get("transactionId"), transactionId);
        }

    }

}
