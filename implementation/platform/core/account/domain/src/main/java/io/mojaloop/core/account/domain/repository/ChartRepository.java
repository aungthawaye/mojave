package io.mojaloop.core.account.domain.repository;

import io.mojaloop.core.account.domain.model.Chart;
import io.mojaloop.core.common.datatype.identifier.account.ChartId;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartRepository extends JpaRepository<Chart, ChartId>, JpaSpecificationExecutor<Chart> {

    class Filters {

        public static Specification<Chart> withId(ChartId id) {
            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public static Specification<Chart> withNameContains(String name) {
            return (root, query, cb) -> cb.like(root.get("name"), "%" + name + "%");
        }
    }
}
