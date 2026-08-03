package org.mojave.core.settlement.domain.repository;

import org.mojave.core.settlement.domain.model.SettlementWindow;
import org.mojave.core.settlement.domain.model.record.SettlementRecord;
import org.mojave.core.settlement.domain.model.record.SettlementRecord_;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.SettlementRecordStatus;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;
import org.mojave.scheme.rule.identifier.settlement.SettlementRecordId;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.scenario.ScenarioType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRecordRepository
    extends JpaRepository<SettlementRecord, SettlementRecordId>,
            JpaSpecificationExecutor<SettlementRecord> {

    class Filters {

        public static Specification<SettlementRecord> withCurrency(final Currency currency) {

            return (root, query, cb) -> cb.equal(root.get(SettlementRecord_.currency), currency);
        }

        public static Specification<SettlementRecord> withScenario(final ScenarioType scenario) {

            return (root, query, cb) -> cb.equal(root.get(SettlementRecord_.scenario), scenario);
        }

        public static Specification<SettlementRecord> withSettlementModelId(
            final SettlementModelId settlementModelId) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementRecord_.settlementModel).get("id"), settlementModelId);
        }

        public static Specification<SettlementRecord> withSettlementWindow(
            final SettlementWindow settlementWindow) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementRecord_.settlementWindow), settlementWindow);
        }

        public static Specification<SettlementRecord> withStatus(
            final SettlementRecordStatus status) {

            return (root, query, cb) -> cb.equal(root.get(SettlementRecord_.status), status);
        }

        public static Specification<SettlementRecord> withTransactionId(
            final TransactionId transactionId) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementRecord_.transactionId), transactionId);
        }

    }

}
