package org.mojave.core.settlement.domain.repository;

import org.mojave.core.settlement.domain.model.SettlementModel;
import org.mojave.core.settlement.domain.model.SettlementWindow;
import org.mojave.core.settlement.domain.model.SettlementWindow_;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.SettlementWindowStatus;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementWindowRepository
    extends JpaRepository<SettlementWindow, SettlementWindowId>,
            JpaSpecificationExecutor<SettlementWindow> {

    class Filters {

        public static Specification<SettlementWindow> withCurrency(final Currency currency) {

            return (root, query, cb) -> cb.equal(root.get(SettlementWindow_.currency), currency);
        }

        public static Specification<SettlementWindow> withFspGroupId(final FspGroupId fspGroupId) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementWindow_.fspGroupId), fspGroupId);
        }

        public static Specification<SettlementWindow> withSettlementModel(
            final SettlementModel settlementModel) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementWindow_.settlementModel), settlementModel);
        }

        public static Specification<SettlementWindow> withStatus(
            final SettlementWindowStatus status) {

            return (root, query, cb) -> cb.equal(root.get(SettlementWindow_.status), status);
        }

    }

}
