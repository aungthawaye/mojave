package io.mojaloop.core.account.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.core.common.datatype.converter.identifier.account.AccountIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.account.LedgerBalanceIdConverter;
import io.mojaloop.core.common.datatype.converter.identifier.account.LedgerMovementIdJavaType;
import io.mojaloop.core.common.datatype.enums.account.NormalSide;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.LedgerBalanceId;
import io.mojaloop.core.common.datatype.identifier.account.LedgerMovementId;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;

import static java.sql.Types.BIGINT;

@Getter
@Table(name = "acc_ledger_movement")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerMovement extends JpaEntity<LedgerMovementId> {

    @Id
    @JavaType(LedgerMovementIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    protected LedgerMovementId id;

    @Column(name = "account_id", nullable = false, updatable = false)
    @Convert(converter = AccountIdJavaType.class)
    protected AccountId accountId;

    @Column(name = "ledger_balance_id", nullable = false, updatable = false)
    @Convert(converter = LedgerBalanceIdConverter.class)
    protected LedgerBalanceId ledgerBalanceId;

    @Column(name = "side", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    protected NormalSide side;

    @Column(name = "amount", nullable = false, updatable = false)
    protected BigDecimal amount;

    @Column(name = "old_drcr", nullable = false, updatable = false)
    protected BigDecimal oldDrCr;

    @Column(name = "new_drcr", nullable = false, updatable = false)
    protected BigDecimal newDrCr;

    @Override
    public LedgerMovementId getId() {

        return this.id;
    }

}
