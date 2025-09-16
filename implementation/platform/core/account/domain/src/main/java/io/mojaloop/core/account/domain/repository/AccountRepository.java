package io.mojaloop.core.account.domain.repository;

import io.mojaloop.core.account.domain.model.Account;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, AccountId>, JpaSpecificationExecutor<Account> {

    class Filters {

        public static Specification<Account> withChartEntryId(ChartEntryId chartEntryId) {

            return (root, query, cb) -> cb.equal(root.get("chartEntryId"), chartEntryId);
        }

        public static Specification<Account> withCode(AccountCode code) {

            return (root, query, cb) -> cb.equal(root.get("code"), code);
        }

        public static Specification<Account> withCurrency(Currency currency) {

            return (root, query, cb) -> cb.equal(root.get("currency"), currency);
        }

        public static Specification<Account> withId(AccountId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public static Specification<Account> withOwnerId(OwnerId ownerId) {

            return (root, query, cb) -> cb.equal(root.get("ownerId"), ownerId);
        }

    }

}
