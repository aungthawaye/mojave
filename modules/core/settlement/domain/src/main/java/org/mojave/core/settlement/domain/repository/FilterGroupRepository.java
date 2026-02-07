package org.mojave.core.settlement.domain.repository;

import org.mojave.common.datatype.identifier.settlement.FilterGroupId;
import org.mojave.core.settlement.domain.model.FilterGroup;
import org.mojave.core.settlement.domain.model.FilterGroup_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FilterGroupRepository
    extends JpaRepository<FilterGroup, FilterGroupId>, JpaSpecificationExecutor<FilterGroup> {

    class Filters {

        public static Specification<FilterGroup> withNameEquals(final String name) {

            return (root, query, cb) -> cb.equal(root.get(FilterGroup_.name), name);
        }

    }

}
