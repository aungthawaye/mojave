package org.mojave.core.settlement.domain.repository;

import org.mojave.core.settlement.domain.model.SettlementModel;
import org.mojave.core.settlement.domain.model.SettlementModel_;
import org.mojave.scheme.rule.enums.ActivationStatus;
import org.mojave.scheme.rule.enums.TerminationStatus;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementModelRepository
    extends JpaRepository<SettlementModel, SettlementModelId>,
            JpaSpecificationExecutor<SettlementModel> {

    class Filters {

        public static Specification<SettlementModel> withActivationStatus(
            final ActivationStatus status) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementModel_.activationStatus), status);
        }

        public static Specification<SettlementModel> withId(final SettlementModelId id) {

            return (root, query, cb) -> cb.equal(root.get(SettlementModel_.id), id);
        }

        public static Specification<SettlementModel> withNameContains(final String name) {

            return (root, query, cb) -> cb.like(root.get(SettlementModel_.name), "%" + name + "%");
        }

        public static Specification<SettlementModel> withNameEquals(final String name) {

            return (root, query, cb) -> cb.equal(root.get(SettlementModel_.name), name);
        }

        public static Specification<SettlementModel> withTerminationStatus(
            final TerminationStatus status) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementModel_.terminationStatus), status);
        }

    }

}
