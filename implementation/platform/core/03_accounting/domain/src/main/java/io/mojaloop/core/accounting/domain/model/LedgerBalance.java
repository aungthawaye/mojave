/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.core.accounting.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.accounting.contract.data.LedgerBalanceData;
import io.mojaloop.core.accounting.domain.component.ledger.Ledger;
import io.mojaloop.core.common.datatype.converter.identifier.accounting.AccountIdJavaType;
import io.mojaloop.core.common.datatype.enums.accounting.OverdraftMode;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.LedgerMovementId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.fspiop.component.handy.FspiopCurrencies;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Table(name = "acc_ledger_balance")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerBalance extends JpaEntity<AccountId> implements DataConversion<LedgerBalanceData> {

    @Id
    @JavaType(AccountIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "ledger_balance_id", nullable = false, updatable = false)
    protected AccountId id;

    @Column(name = "currency", nullable = false, length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "scale", nullable = false, updatable = false)
    protected int scale;

    @Column(name = "nature", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Side nature;

    @Column(name = "posted_debits", precision = 34, scale = 4)
    protected BigDecimal postedDebits;

    @Column(name = "posted_credits", precision = 34, scale = 4)
    protected BigDecimal postedCredits;

    @Column(name = "overdraft_mode", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected OverdraftMode overdraftMode;

    @Column(name = "overdraft_limit", precision = 34, scale = 4)
    protected BigDecimal overdraftLimit;

    @Column(name = "created_at", nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @MapsId
    @OneToOne
    @JoinColumn(name = "ledger_balance_id", nullable = false,
                foreignKey = @ForeignKey(name = "ledger_balance_account_FK"))
    protected Account account;

    public LedgerBalance(Account account, Side nature, OverdraftMode overdraftMode, BigDecimal overdraftLimit) {

        assert account != null;
        assert nature != null;
        assert overdraftMode != null;
        assert overdraftLimit != null;

        this.id = account.getId();
        this.currency = account.currency;
        this.scale = FspiopCurrencies.get(account.currency).scale();
        this.nature = nature;
        this.postedDebits = BigDecimal.ZERO.setScale(this.scale, RoundingMode.UNNECESSARY);
        this.postedCredits = BigDecimal.ZERO.setScale(this.scale, RoundingMode.UNNECESSARY);
        this.overdraftMode = overdraftMode;
        this.overdraftLimit = overdraftLimit;
        this.createdAt = Instant.now();
        this.account = account;
    }

    public LedgerMovement apply(Side side,
                                BigDecimal amount,
                                TransactionId transactionId,
                                Instant transactionAt,
                                TransactionType transactionType)
        throws Ledger.InsufficientBalanceException, Ledger.NegativeAmountException {

        var amt = this.norm(amount);

        var oldDrCr = new DrCr(this.postedDebits, this.postedCredits);

        var nd = this.postedDebits.setScale(this.scale, RoundingMode.UNNECESSARY);
        var nc = this.postedCredits.setScale(this.scale, RoundingMode.UNNECESSARY);

        if (side == Side.DEBIT) {
            nd = nd.add(amt);
        } else {
            nc = nc.add(amt);
        }

        var nb = (this.nature == Side.DEBIT ? nd.subtract(nc) : nc.subtract(nd));

        if (nb.signum() < 0) {

            throw new Ledger.InsufficientBalanceException(
                this.getId(), side, amount, new Ledger.DrCr(this.postedDebits, this.postedCredits));
        }

        this.postedDebits = nd;
        this.postedCredits = nc;

        return new LedgerMovement(
            new LedgerMovementId(Snowflake.get().nextId()), this.getId(), side, amt, oldDrCr,
            new DrCr(this.postedDebits, this.postedCredits), transactionId, transactionAt, transactionType);

    }

    public BigDecimal available() {

        return switch (this.nature) {
            case CREDIT -> this.postedCredits.subtract(this.postedDebits).add(this.overdraftLimit);
            case DEBIT -> this.postedDebits.subtract(this.postedCredits).add(this.overdraftLimit);
        };
    }

    @Override
    public LedgerBalanceData convert() {

        return new LedgerBalanceData(
            this.getId(), this.currency, this.scale, this.nature, this.postedDebits, this.postedCredits,
            this.overdraftMode,
            this.overdraftLimit, this.createdAt);
    }

    public DrCr getDrCr() {

        return new DrCr(this.postedDebits, this.postedCredits);
    }

    @Override
    public AccountId getId() {

        return this.id;
    }

    private BigDecimal norm(BigDecimal amount) throws Ledger.NegativeAmountException {

        assert amount != null;

        var n = amount.setScale(this.scale, RoundingMode.UNNECESSARY);

        if (n.signum() < 0) {
            throw new Ledger.NegativeAmountException();
        }

        return n;
    }

}
