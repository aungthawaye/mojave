package org.mojave.core.wallet.domain.repository;

import jakarta.persistence.LockModeType;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.domain.model.Wallet;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository
    extends JpaRepository<Wallet, WalletId>, JpaSpecificationExecutor<Wallet> {

    @Query("SELECT w FROM Wallet w WHERE w.id = :walletId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> selectForUpdate(WalletId walletId);

    class Filters {

        public static Specification<Wallet> withCurrency(final Currency currency) {

            return (root, query, cb) -> cb.equal(root.get("currency"), currency);
        }

        public static Specification<Wallet> withId(final WalletId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public static Specification<Wallet> withOwnerId(final WalletOwnerId ownerId) {

            return (root, query, cb) -> cb.equal(root.get("walletOwnerId"), ownerId);
        }

        public static Specification<Wallet> withTag(final String tag) {

            return (root, query, cb) -> cb.equal(root.get("tag"), tag);
        }

    }

}
