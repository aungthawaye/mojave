/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.core.participant.domain.model.fsp;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
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
import org.mojave.common.datatype.converter.identifier.participant.FspIdJavaType;
import org.mojave.common.datatype.converter.type.fspiop.FspCodeConverter;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.enums.participant.EndpointType;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.participant.contract.data.FspCurrencyData;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.contract.data.FspEndpointData;
import org.mojave.core.participant.contract.exception.fsp.FspCodeRequiredException;
import org.mojave.core.participant.contract.exception.fsp.FspNameRequiredException;
import org.mojave.core.participant.contract.exception.fsp.FspNameTooLongException;
import org.mojave.core.participant.domain.model.hub.Hub;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "pcp_fsp",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "pcp_fsp_01_UK",
            columnNames = {"code"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fsp extends JpaEntity<FspId> implements DataConversion<FspData> {

    @Id
    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "fsp_id",
        nullable = false,
        updatable = false)
    protected FspId id;

    @Basic
    @Column(
        name = "code",
        nullable = false,
        length = StringSizeConstraints.MAX_CODE_LENGTH)
    @Convert(converter = FspCodeConverter.class)
    protected FspCode code;

    @Column(
        name = "name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(
        name = "activation_status",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(
        name = "termination_status",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

    @Column(
        name = "created_at",
        nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @Getter(AccessLevel.NONE)
    @OneToMany(
        mappedBy = "fsp",
        cascade = {CascadeType.ALL},
        orphanRemoval = true,
        targetEntity = FspCurrency.class,
        fetch = FetchType.EAGER)
    protected Set<FspCurrency> currencies = new HashSet<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(
        mappedBy = "fsp",
        cascade = {CascadeType.ALL},
        orphanRemoval = true,
        targetEntity = FspEndpoint.class,
        fetch = FetchType.EAGER)
    protected Set<FspEndpoint> endpoints = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "hub_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "pcp_hub_pcp_fsp_FK"))
    protected Hub hub;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "fsp_group_id",
        foreignKey = @ForeignKey(name = "pcp_fsp_group_pcp_fsp_FK"))
    protected FspGroup fspGroup;

    public Fsp(final Hub hub, final FspCode code, final String name) {

        Objects.requireNonNull(hub);
        Objects.requireNonNull(code);
        Objects.requireNonNull(name);

        this.id = new FspId(Snowflake.get().nextId());
        this.hub = hub;
        this.fspCode(code).name(name);
        this.activationStatus = ActivationStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public Optional<FspCurrency> activate(final Currency currency) {

        final var fspCurrency = this.currencies
                                    .stream()
                                    .filter(f -> f.getCurrency().equals(currency))
                                    .findFirst();

        if (fspCurrency.isPresent()) {

            fspCurrency.get().activate();

            return fspCurrency;
        }

        return Optional.empty();
    }

    public Optional<FspEndpoint> activate(final EndpointType type) {

        final var fspEndpoint = this.endpoints
                                    .stream()
                                    .filter(f -> f.getType().equals(type))
                                    .findFirst();

        if (fspEndpoint.isPresent()) {

            fspEndpoint.get().activate();

            return fspEndpoint;
        }

        return Optional.empty();
    }

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;

        this.currencies.forEach(FspCurrency::activate);
        this.endpoints.forEach(FspEndpoint::activate);
    }

    public FspCurrency addCurrency(final Currency currency) {

        Objects.requireNonNull(currency);

        final var supportedCurrency = new FspCurrency(this, currency);

        this.currencies.add(supportedCurrency);

        return supportedCurrency;
    }

    public FspEndpoint addEndpoint(final EndpointType type, final String host) {

        Objects.requireNonNull(type);
        Objects.requireNonNull(host);

        final var endpoint = new FspEndpoint(this, type, host);

        this.endpoints.add(endpoint);

        return endpoint;
    }

    public Optional<FspEndpoint> changeEndpoint(final EndpointType type, final String baseUrl) {

        Objects.requireNonNull(type);
        Objects.requireNonNull(baseUrl);

        final var optEndpoint = this.endpoints
                                    .stream()
                                    .filter(fspEndpoint -> fspEndpoint.getType() == type)
                                    .findFirst();

        if (optEndpoint.isEmpty()) {

            return optEndpoint;
        }

        final var endpoint = optEndpoint.get();

        endpoint.baseUrl(baseUrl);

        return Optional.of(endpoint);
    }

    @Override
    public FspData convert() {

        return new FspData(
            this.getId(), this.getCode(), this.getName(),
            this.fspGroup == null ? null : this.fspGroup.getId(),
            this.getCurrencies().stream().map(FspCurrency::convert).toArray(FspCurrencyData[]::new),
            this.getEndpoints().stream().map(FspEndpoint::convert).collect(Collectors.toMap(
                FspEndpointData::type, Function.identity(),
                (existing, replacement) -> replacement)), this.activationStatus,
            this.terminationStatus);
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;

        this.currencies.forEach(FspCurrency::deactivate);
        this.endpoints.forEach(FspEndpoint::deactivate);
    }

    public Optional<FspCurrency> deactivate(final Currency currency) {

        Objects.requireNonNull(currency);

        final var optSupportedCurrency = this.currencies
                                             .stream()
                                             .filter(sc -> sc.getCurrency() == currency)
                                             .findFirst();

        if (optSupportedCurrency.isEmpty()) {

            return optSupportedCurrency;
        }

        optSupportedCurrency.get().deactivate();

        return optSupportedCurrency;
    }

    public Optional<FspEndpoint> deactivate(final EndpointType type) {

        Objects.requireNonNull(type);

        final var optEndpoint = this.endpoints
                                    .stream()
                                    .filter(fspEndpoint -> fspEndpoint.getType() == type)
                                    .findFirst();

        if (optEndpoint.isEmpty()) {

            return optEndpoint;
        }

        optEndpoint.get().deactivate();

        return optEndpoint;
    }

    public Fsp fspCode(final FspCode fspCode) {

        if (fspCode == null) {

            throw new FspCodeRequiredException();
        }

        this.code = fspCode;

        return this;
    }

    public Set<FspCurrency> getCurrencies() {

        return Collections.unmodifiableSet(this.currencies);
    }

    public Optional<FspCurrency> getCurrency(final Currency currency) {

        return this.currencies.stream().filter(f -> f.getCurrency().equals(currency)).findFirst();
    }

    public Optional<FspEndpoint> getEndpoint(final EndpointType type) {

        return this.endpoints.stream().filter(f -> f.getType().equals(type)).findFirst();
    }

    public Set<FspEndpoint> getEndpoints() {

        return Collections.unmodifiableSet(this.endpoints);
    }

    @Override
    public FspId getId() {

        return this.id;
    }

    public boolean hasEndpoint(final EndpointType type) {

        Objects.requireNonNull(type);

        return this.endpoints.stream().anyMatch(fspEndpoint -> fspEndpoint.getType() == type);
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

    public boolean isCurrencySupported(final Currency currency) {

        Objects.requireNonNull(currency);

        return this.currencies
                   .stream()
                   .anyMatch(sc -> sc.getCurrency() == currency && sc.isActive());
    }

    public void join(FspGroup fspGroup) {

        Objects.requireNonNull(fspGroup);

        if (this.fspGroup != null && this.fspGroup.getId().equals(fspGroup.getId())) {
            return;
        }

        this.fspGroup = fspGroup;
    }

    public void leave() {

        this.fspGroup = null;
    }

    public Fsp name(final String name) {

        if (name == null || name.isBlank()) {

            throw new FspNameRequiredException();
        }

        final var value = name.trim();

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {

            throw new FspNameTooLongException();
        }

        this.name = value;

        return this;
    }

    public void terminate() {

        this.activationStatus = ActivationStatus.INACTIVE;
        this.terminationStatus = TerminationStatus.TERMINATED;
    }

}
