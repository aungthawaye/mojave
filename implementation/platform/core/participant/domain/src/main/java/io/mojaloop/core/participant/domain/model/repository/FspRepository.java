package io.mojaloop.core.participant.domain.model.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import io.mojaloop.common.datatype.identifier.participant.FspId;
import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.domain.model.Fsp;
import io.mojaloop.core.participant.domain.model.QFsp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FspRepository extends JpaRepository<Fsp, FspId>, QuerydslPredicateExecutor<Fsp> {

    class Filters {

        public static BooleanExpression nameContains(String name) {

            return QFsp.fsp.name.containsIgnoreCase(name);
        }

        public static BooleanExpression notWithId(FspId fspId) {

            return QFsp.fsp.id.ne(fspId);
        }

        public static BooleanExpression withFspCode(FspCode fspCode) {

            return QFsp.fsp.fspCode.eq(fspCode);
        }

    }

}
