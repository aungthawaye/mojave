package io.mojaloop.core.transaction.domain.model.definition;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.accounting.ChartEntryIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.DefinitionIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.PostingIdJavaType;
import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.TerminationStatus;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.transaction.DefinitionId;
import io.mojaloop.core.common.datatype.identifier.transaction.PostingId;
import io.mojaloop.core.transaction.contract.exception.DefinitionDescriptionTooLongException;
import io.mojaloop.core.transaction.contract.exception.DefinitionNameTooLongException;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "txn_fund_in_definition", uniqueConstraints = {@UniqueConstraint(name = "trn_fund_in_definition_currency_UK", columnNames = {"currency"}),
                                                             @UniqueConstraint(name = "trn_fund_in_definition_name_UK", columnNames = {"name"})})

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FundInDefinition extends JpaEntity<DefinitionId> {

    @Id
    @JavaType(DefinitionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "definition_id", nullable = false, updatable = false)
    protected DefinitionId id;

    @Column(name = "currency", nullable = false, length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "name", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(name = "description", length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @Column(name = "activation_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "termination_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

    @OneToMany(mappedBy = "definition", orphanRemoval = true, cascade = {jakarta.persistence.CascadeType.ALL}, fetch = FetchType.EAGER)
    protected List<Posting> postings = new ArrayList<>();

    public FundInDefinition(Currency currency, String name, String description) {

        assert currency != null;
        assert name != null;

        this.id = new DefinitionId(Snowflake.get().nextId());
        this.name(name).currency(currency).description(description);
    }

    public FundInDefinition.Posting addPosting(PostingAmountType forAmountType, Side side, ChartEntryId chartEntryId, String description)
        throws PostingAlreadyExistsException {

        var posting = new FundInDefinition.Posting(this, forAmountType, side, chartEntryId, description);

        this.postings.add(posting);

        return posting;
    }

    public FundInDefinition currency(Currency currency) {

        assert currency != null;

        this.currency = currency;

        return this;
    }

    public FundInDefinition description(String description) {

        if (description == null) {
            return this;
        }

        var value = description.trim();

        if (value.length() > StringSizeConstraints.MAX_DESCRIPTION_LENGTH) {
            throw new DefinitionDescriptionTooLongException();
        }

        this.description = description;

        return this;
    }

    public FundInDefinition name(String name) {

        if (name == null) {
            return this;
        }

        var value = name.trim();

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new DefinitionNameTooLongException();
        }

        this.name = name;

        return this;
    }

    public enum PostingAmountType {

        LIQUIDITY_AMOUNT
    }

    @Getter
    @Entity
    @Table(name = "txn_fund_in_posting")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Posting extends JpaEntity<PostingId> {

        @Id
        @JavaType(PostingIdJavaType.class)
        @JdbcTypeCode(BIGINT)
        protected PostingId id;

        @Column(name = "for_amount_type", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
        @Enumerated(EnumType.STRING)
        protected PostingAmountType forAmountType;

        @Column(name = "side", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
        @Enumerated(EnumType.STRING)
        protected Side side;

        @Basic
        @JavaType(ChartEntryIdJavaType.class)
        @JdbcTypeCode(BIGINT)
        @Column(name = "chart_entry_id", nullable = false, updatable = false)
        protected ChartEntryId chartEntryId;

        @Column(name = "description", length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
        protected String description;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "definition_id", nullable = false)
        protected FundInDefinition definition;

        public Posting(FundInDefinition definition, PostingAmountType forAmountType, Side side, ChartEntryId chartEntryId, String description)
            throws PostingAlreadyExistsException {

            assert definition != null;
            assert forAmountType != null;
            assert side != null;
            assert chartEntryId != null;

            this.id = new PostingId(Snowflake.get().nextId());
            this.definition = definition;
            this.forAmountType(forAmountType).side(side).chartEntryId(chartEntryId).description(description);
        }

        public Posting chartEntryId(ChartEntryId chartEntryId) throws PostingAlreadyExistsException {

            assert chartEntryId != null;

            try {

                this.definition.postings.stream().filter(p -> p.getChartEntryId().equals(chartEntryId)).findFirst().ifPresent(p -> {
                    try {
                        throw new PostingAlreadyExistsException();
                    } catch (PostingAlreadyExistsException e) {
                        throw new RuntimeException(e);
                    }
                });

            } catch (RuntimeException e) {

                if (e.getCause() instanceof PostingAlreadyExistsException e1) {
                    throw e1;
                }
            }

            this.chartEntryId = chartEntryId;

            return this;
        }

        public Posting description(String description) {

            if (description == null) {
                return this;
            }

            var value = description.trim();

            if (value.length() > StringSizeConstraints.MAX_DESCRIPTION_LENGTH) {
                throw new DefinitionDescriptionTooLongException();
            }

            this.description = description;

            return this;
        }

        public Posting forAmountType(PostingAmountType forAmountType) {

            assert forAmountType != null;

            this.forAmountType = forAmountType;

            return this;
        }

        public Posting side(Side side) {

            assert side != null;

            this.side = side;

            return this;
        }

    }

}
