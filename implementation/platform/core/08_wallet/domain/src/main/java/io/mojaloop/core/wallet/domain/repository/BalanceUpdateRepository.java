package io.mojaloop.core.wallet.domain.repository;

import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.wallet.domain.model.BalanceUpdate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceUpdateRepository extends JpaRepository<BalanceUpdate, BalanceUpdateId>, JpaSpecificationExecutor<BalanceUpdate> {

    class Filters {

        public static Specification<BalanceUpdate> withReversalId(final BalanceUpdateId walletId) {

            return (root, query, cb) -> cb.equal(root.get("reversalId"), walletId);
        }

        public static Specification<BalanceUpdate> withWalletId(final BalanceUpdateId walletId) {

            return (root, query, cb) -> cb.equal(root.get("walletId"), walletId);
        }

    }

}
