package org.mojave.core.settlement.domain.repository;

import org.mojave.core.settlement.domain.model.definition.SettlementModelDefinition;
import org.mojave.core.settlement.domain.model.definition.SettlementModelDefinition_;
import org.mojave.scheme.rule.enums.ActivationStatus;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.TerminationStatus;
import org.mojave.scheme.rule.enums.settlement.TransactionPeriod;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.identifier.settlement.SettlementDefinitionId;
import org.mojave.scheme.rule.scenario.ScenarioType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementModelDefinitionRepository
    extends JpaRepository<SettlementModelDefinition, SettlementDefinitionId>,
            JpaSpecificationExecutor<SettlementModelDefinition> {

    class Filters {

        public static Specification<SettlementModelDefinition> withActivationStatus(
            final ActivationStatus status) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementModelDefinition_.activationStatus), status);
        }

        public static Specification<SettlementModelDefinition> withCurrency(
            final Currency currency) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementModelDefinition_.currency), currency);
        }

        public static Specification<SettlementModelDefinition> withFspGroupId(
            final FspGroupId fspGroupId) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementModelDefinition_.fspGroupId), fspGroupId);
        }

        public static Specification<SettlementModelDefinition> withNameContains(final String name) {

            return (root, query, cb) -> cb.like(
                root.get(SettlementModelDefinition_.name), "%" + name + "%");
        }

        public static Specification<SettlementModelDefinition> withNameEquals(final String name) {

            return (root, query, cb) -> cb.equal(root.get(SettlementModelDefinition_.name), name);
        }

        public static Specification<SettlementModelDefinition> withScenario(
            final ScenarioType scenario) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementModelDefinition_.scenario), scenario);
        }

        public static Specification<SettlementModelDefinition> withTerminationStatus(
            final TerminationStatus status) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementModelDefinition_.terminationStatus), status);
        }

        public static Specification<SettlementModelDefinition> withTransactionPeriod(
            final TransactionPeriod period) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementModelDefinition_.transactionPeriod), period);
        }

    }

}
