package io.mojaloop.core.account.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.core.common.datatype.converter.identifier.account.LedgerBalanceIdJavaType;
import io.mojaloop.core.common.datatype.identifier.account.LedgerBalanceId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;

import static java.sql.Types.BIGINT;

@Getter
@Table(name = "acc_ledger_balance")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerBalance extends JpaEntity<LedgerBalanceId> {

    @Id
    @JavaType(LedgerBalanceIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    private LedgerBalanceId id;

    @Column(name = "posted_debits")
    private BigDecimal postedDebits;

    @Column(name = "posted_credits")
    private BigDecimal postedCredits;

    @OneToOne(optional = false)
    private Account account;

    @Override
    public LedgerBalanceId getId() {

        return this.id;
    }

}
