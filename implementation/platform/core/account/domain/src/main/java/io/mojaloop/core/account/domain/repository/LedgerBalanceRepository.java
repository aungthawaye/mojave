package io.mojaloop.core.account.domain.repository;

import io.mojaloop.core.account.domain.model.LedgerBalance;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.LedgerBalanceId;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerBalanceRepository extends JpaRepository<LedgerBalance, LedgerBalanceId>, JpaSpecificationExecutor<LedgerBalance> {

    class Filters {

        public static Specification<LedgerBalance> withAccountId(AccountId accountId) {

            return (root, query, cb) -> cb.equal(root.get("account").get("id"), accountId);
        }

        public static Specification<LedgerBalance> withId(LedgerBalanceId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

    }

}
