package io.mojaloop.core.account.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.account.contract.exception.chart.ChartNameRequiredException;
import io.mojaloop.core.account.contract.exception.chart.ChartNameTooLongException;
import io.mojaloop.core.account.contract.exception.chart.SameChartEntryCodeExistsException;
import io.mojaloop.core.account.contract.exception.chart.SameChartEntryNameExistsException;
import io.mojaloop.core.common.datatype.converter.identifier.account.ChartIdJavaType;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.account.ChartId;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
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
    protected ChartId id;

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

    public Chart(String name) {

        this.id = new ChartId(Snowflake.get().nextId());
        this.name(name);
        this.createdAt = Instant.now();
    }

    public ChartEntry addEntry(ChartEntryCode code, String name, String description, AccountType accountType) {

        ChartEntry entry = new ChartEntry(this, code, name, description, accountType);

        this.entries.stream().findFirst().ifPresent(existingEntry -> {
            if (existingEntry.getCode().equals(code)) {
                throw new SameChartEntryCodeExistsException();
            }
        });

        this.entries.stream().findFirst().ifPresent(existingEntry -> {
            if (existingEntry.getName().equalsIgnoreCase(name)) {
                throw new SameChartEntryNameExistsException();
            }
        });

        this.entries.add(entry);

        return entry;
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

        return this.id;
    }

    public Chart name(String name) {

        if (name == null || name.isBlank()) {
            throw new ChartNameRequiredException();
        }

        var value = name.trim();

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new ChartNameTooLongException();
        }

        this.name = value;

        return this;
    }

    /**
     * Removes an entry from this chart by its identifier.
     *
     * <p>Searches for an entry with the specified ID and removes it from the chart if found.</p>
     *
     * @param chartEntryId the unique identifier of the chart entry to remove
     * @return {@code true} if an entry was found and removed, {@code false} otherwise
     * @see ChartEntryId
     */
    public boolean removeEntry(ChartEntryId chartEntryId) {

        return this.entries.removeIf(entry -> entry.getId().equals(chartEntryId));
    }

}
