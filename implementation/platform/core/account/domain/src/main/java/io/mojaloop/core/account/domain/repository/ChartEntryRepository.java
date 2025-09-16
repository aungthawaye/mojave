package io.mojaloop.core.account.domain.repository;

import io.mojaloop.core.account.domain.model.ChartEntry;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.account.ChartId;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartEntryRepository extends JpaRepository<ChartEntry, ChartEntryId>, JpaSpecificationExecutor<ChartEntry> {

    class Filters {

        public static Specification<ChartEntry> withAccountType(AccountType type) {

            return (root, query, cb) -> cb.equal(root.get("accountType"), type);
        }

        public static Specification<ChartEntry> withChartId(ChartId chartId) {

            return (root, query, cb) -> cb.equal(root.get("chart").get("id"), chartId);
        }

        public static Specification<ChartEntry> withCode(ChartEntryCode code) {

            return (root, query, cb) -> cb.equal(root.get("code"), code);
        }

        public static Specification<ChartEntry> withId(ChartEntryId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public static Specification<ChartEntry> withNameContains(String name) {

            return (root, query, cb) -> cb.like(root.get("name"), "%" + name + "%");
        }

    }

}
