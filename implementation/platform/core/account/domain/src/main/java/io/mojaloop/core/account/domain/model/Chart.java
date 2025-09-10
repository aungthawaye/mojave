package io.mojaloop.core.account.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.input.BlankOrEmptyInputException;
import io.mojaloop.component.misc.exception.input.TextTooLargeException;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.account.ChartIdJavaType;
import io.mojaloop.core.common.datatype.identifier.account.ChartId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.sql.Types.BIGINT;

/**
 * Domain model for a Chart of Accounts.
 * A chart of accounts is a structured list of an organization's financial accounts used to categorize
 * financial transactions and track financial activities.
 *
 * <p>Each chart is identified by a unique {@link ChartId} and has a human‑readable {@code name}.</p>
 *
 * <p>This class implements {@link JpaEntity} for JPA persistence and maintains a collection of
 * {@link ChartEntry} objects that define the structure of accounts within this chart.</p>
 *
 * <p>Instances are mutable and use fluent, builder‑style mutator methods that return {@code this} for chaining.</p>
 *
 * @see ChartId
 * @see ChartEntry
 */
@Getter
@Entity
@Table(name = "acc_chart", uniqueConstraints = {@UniqueConstraint(name = "uk_chart_name", columnNames = {"name"}),
                                                @UniqueConstraint(name = "uk_chart_owner_id", columnNames = {"owner_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chart extends JpaEntity<ChartId> {

    /**
     * The unique identifier for this chart.
     * This is the primary key in the database and is automatically generated.
     */
    @Id
    @JavaType(ChartIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "chart_id", nullable = false, updatable = false)
    protected ChartId chartId;

    /**
     * The name of the chart. 
     * Must be non-empty, non-null, and not exceed the maximum length defined by 
     * {@link StringSizeConstraints#MAX_NAME_TITLE_LENGTH}.
     */
    @Column(name = "name", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;


    /**
     * The timestamp when this chart was created.
     * This is automatically set to the current time when the chart is first created.
     */
    @Column(name = "created_at", nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    /**
     * The collection of entry definitions that make up this chart.
     * This is a one-to-many relationship where each entry definition belongs to exactly one chart.
     * The collection is eagerly fetched and modifications are cascaded to the database.
     *
     * @see ChartEntry
     */

    @OneToMany(mappedBy = "chart", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    protected Set<ChartEntry> entries = new HashSet<>();

    /**
     * Creates a new Chart.
     *
     * <p>A new {@link ChartId} is generated and {@code createdAt} is initialized to the current time.
     * Name validation rules are applied via {@link #name(String)} if you choose to set the name after construction.</p>
     *
     * @param name the chart name; must be non-blank and within the allowed size constraints
     * @throws BlankOrEmptyInputException if {@code name} is blank
     * @throws TextTooLargeException if {@code name} exceeds {@link StringSizeConstraints#MAX_NAME_TITLE_LENGTH}
     * @throws AssertionError if assertions are enabled and {@code name} is null
     */
    public Chart(String name) {

        assert name != null;

        this.chartId = new ChartId(Snowflake.get().nextId());
        this.createdAt = Instant.now();
    }

    /**
     * Gets an unmodifiable view of the entry definitions in this chart.
     *
     * @return an unmodifiable set of entry definitions
     */
    public Set<ChartEntry> getEntries() {

        return Collections.unmodifiableSet(entries);
    }

    /**
     * Gets the unique identifier of this chart.
     *
     * @return the chart's unique identifier
     */
    @Override
    public ChartId getId() {

        return chartId;
    }

    /**
     * Sets the name of this chart.
     *
     * @param name the new name for the chart (must not be null, empty, or exceed maximum length)
     * @return this chart instance for method chaining
     * @throws BlankOrEmptyInputException if the name is empty or contains only whitespace
     * @throws TextTooLargeException if the name exceeds the maximum allowed length
     * @implNote Null is guarded by an assertion; enable JVM assertions to enforce at runtime.
     */
    public Chart name(String name) {

        assert name != null : "Name cannot be null";

        var value = name.trim();

        if (value.isEmpty()) {
            throw new BlankOrEmptyInputException("Chart Name");
        }

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new TextTooLargeException("Chart Name", StringSizeConstraints.MAX_NAME_TITLE_LENGTH);
        }

        this.name = value;

        return this;
    }


}
