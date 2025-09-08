package io.mojaloop.core.account.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.core.common.datatype.converter.identifier.account.AccountIdJavaType;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "acc_account")
public class Account extends JpaEntity<AccountId> {

    @Id
    @JavaType(AccountIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "account_id", nullable = false, updatable = false)
    protected AccountId accountId;

    @Override
    public AccountId getId() {

        return this.accountId;
    }

}
