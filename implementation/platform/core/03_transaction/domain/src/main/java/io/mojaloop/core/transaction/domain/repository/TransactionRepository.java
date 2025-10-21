package io.mojaloop.core.transaction.domain.repository;

import io.mojaloop.core.common.datatype.enums.trasaction.TransactionPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.transaction.domain.model.Transaction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, TransactionId>, JpaSpecificationExecutor<Transaction> {

    class Filters {

        public Specification<Transaction> completedDuring(Instant from, Instant to) {

            return (root, query, cb) -> cb.between(root.get("completedAt"), from, to);
        }

        public Specification<Transaction> initiatedDuring(Instant from, Instant to) {

            return (root, query, cb) -> cb.between(root.get("initiatedAt"), from, to);
        }

        public Specification<Transaction> reservedDuring(Instant from, Instant to) {

            return (root, query, cb) -> cb.between(root.get("reservedAt"), from, to);
        }

        public Specification<Transaction> withId(TransactionId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public Specification<Transaction> withStage(TransactionPhase stage) {

            return (root, query, cb) -> cb.equal(root.get("stage"), stage);
        }

    }

}
