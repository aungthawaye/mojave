package io.mojaloop.core.account.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.input.BlankOrEmptyInputException;
import io.mojaloop.component.misc.exception.input.TextTooLargeException;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.account.ChartEntryIdJavaType;
import io.mojaloop.core.common.datatype.converter.type.account.ChartEntryCodeConverter;
import io.mojaloop.core.common.datatype.enumeration.account.AccountType;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static java.sql.Types.BIGINT;

@Getter
@Table(name = "acc_chart_entry", uniqueConstraints = @UniqueConstraint(name = "uk_chart_entry_code", columnNames = {"chart_entry_code"}))
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChartEntry extends JpaEntity<ChartEntryId> {

    @Id
    @JavaType(ChartEntryIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "chart_entry_id", nullable = false, updatable = false)
    protected ChartEntryId id;

    @Column(name = "chart_entry_code", nullable = false, length = StringSizeConstraints.MAX_CODE_LENGTH)
    @Convert(converter = ChartEntryCodeConverter.class)
    protected ChartEntryCode code;

    @Column(name = "name", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(name = "description", nullable = false, length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @Column(name = "account_type", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH, updatable = false)
    protected AccountType accountType;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "chart_id", nullable = false)
    protected Chart chart;

    @OneToMany(mappedBy = "chartEntry", orphanRemoval = true, fetch = FetchType.EAGER)
    protected Set<Account> accounts = new HashSet<>();

    public ChartEntry(Chart chart, ChartEntryCode code, String name, String description, AccountType accountType) {

        assert chart != null;
        assert code != null;
        assert name != null;
        assert description != null;
        assert accountType != null;

        this.id = new ChartEntryId(Snowflake.get().nextId());
        this.chart = chart;
        this.code(code).name(name).description(description);
        this.accountType = accountType;
        this.createdAt = Instant.now();
    }


    public ChartEntry code(ChartEntryCode code) {

        assert code != null;

        this.code = code;

        return this;
    }

    public ChartEntry description(String description) {

        assert description != null;

        var value = description.trim();

        if (value.isEmpty()) {
            throw new BlankOrEmptyInputException("Chart Entry Description");
        }

        if (value.length() > StringSizeConstraints.MAX_DESCRIPTION_LENGTH) {
            throw new TextTooLargeException("Chart Entry Description", StringSizeConstraints.MAX_DESCRIPTION_LENGTH);
        }

        this.description = description;

        return this;
    }

    @Override
    public ChartEntryId getId() {

        return this.id;
    }

    public ChartEntry name(String name) {

        assert name != null;

        var value = name.trim();

        if (value.isEmpty()) {
            throw new BlankOrEmptyInputException("Chart Entry Name");
        }

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new TextTooLargeException("Chart Entry Name", StringSizeConstraints.MAX_NAME_TITLE_LENGTH);
        }

        this.name = name;

        return this;
    }

}
