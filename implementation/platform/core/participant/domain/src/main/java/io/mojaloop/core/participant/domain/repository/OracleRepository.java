package io.mojaloop.core.participant.domain.repository;

import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.domain.model.oracle.Oracle;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OracleRepository extends JpaRepository<Oracle, OracleId>, JpaSpecificationExecutor<Oracle> {

    class Filters {

        public static Specification<Oracle> withType(PartyIdType type) {

            return (root, query, cb) -> cb.equal(root.get("type"), type);
        }

        public static Specification<Oracle> withId(OracleId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

    }

}
