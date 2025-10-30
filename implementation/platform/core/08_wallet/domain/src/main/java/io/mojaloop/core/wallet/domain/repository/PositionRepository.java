package io.mojaloop.core.wallet.domain.repository;

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.domain.model.Position;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, PositionId>, JpaSpecificationExecutor<Position> {

    @Query("SELECT p FROM Position p WHERE p.id = :positionId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Position> selectForUpdate(PositionId positionId);

    class Filters {

        public static Specification<Position> withCurrency(final Currency currency) {

            return (root, query, cb) -> cb.equal(root.get("currency"), currency);
        }

        public static Specification<Position> withId(final PositionId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public static Specification<Position> withOwnerId(final WalletOwnerId ownerId) {

            return (root, query, cb) -> cb.equal(root.get("walletOwnerId"), ownerId);
        }

    }

}
