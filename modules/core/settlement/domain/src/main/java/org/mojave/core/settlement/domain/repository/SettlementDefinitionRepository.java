package org.mojave.core.settlement.domain.repository;

import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.settlement.FilterGroupId;
import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;
import org.mojave.core.settlement.domain.model.SettlementDefinition;
import org.mojave.core.settlement.domain.model.SettlementDefinition_;
import org.mojave.core.settlement.domain.model.FilterGroup_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SettlementDefinitionRepository
    extends JpaRepository<SettlementDefinition, SettlementDefinitionId>,
            JpaSpecificationExecutor<SettlementDefinition> {

    class Filters {

        public static Specification<SettlementDefinition> withActivationStatus(final ActivationStatus status) {

            return (root, query, cb) -> cb.equal(root.get(SettlementDefinition_.activationStatus), status);
        }

        public static Specification<SettlementDefinition> withCurrency(final Currency currency) {

            return (root, query, cb) -> cb.equal(root.get(SettlementDefinition_.currency), currency);
        }

        public static Specification<SettlementDefinition> withNameEquals(final String name) {

            return (root, query, cb) -> cb.equal(root.get(SettlementDefinition_.name), name);
        }

        public static Specification<SettlementDefinition> withPayeeFilterGroupId(final FilterGroupId filterGroupId) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementDefinition_.payeeFilterGroup).get(FilterGroup_.id), filterGroupId);
        }

        public static Specification<SettlementDefinition> withPayerFilterGroupId(final FilterGroupId filterGroupId) {

            return (root, query, cb) -> cb.equal(
                root.get(SettlementDefinition_.payerFilterGroup).get(FilterGroup_.id), filterGroupId);
        }

    }

}
