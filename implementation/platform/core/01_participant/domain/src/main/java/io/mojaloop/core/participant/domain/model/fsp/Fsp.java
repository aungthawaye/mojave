/*-
 * ================================================================================
 * Mojave
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

package io.mojaloop.core.participant.domain.model.fsp;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.participant.FspIdJavaType;
import io.mojaloop.core.common.datatype.converter.type.fspiop.FspCodeConverter;
import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.TerminationStatus;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspCurrencyData;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.FspEndpointData;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeRequiredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspNameRequiredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspNameTooLongException;
import io.mojaloop.core.participant.domain.model.hub.Hub;
import io.mojaloop.fspiop.spec.core.Currency;
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

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.sql.Types.BIGINT;

/**
 * Financial Service Provider (FSP) entity representing a participant in the Mojaloop network.
 *
 * <p>An FSP can support multiple currencies and endpoints for different types of operations.
 * The FSP has activation and termination statuses that control its operational state within
 * the Mojaloop ecosystem.</p>
 *
 * @author Mojave
 * @since 1.0
 */
@Getter
@Entity
@Table(name = "pcp_fsp", uniqueConstraints = {@UniqueConstraint(name = "pcp_fsp_fsp_code_UK", columnNames = {"fsp_code"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fsp extends JpaEntity<FspId> implements DataConversion<FspData> {

    @Id
    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "fsp_id", nullable = false, updatable = false)
    protected FspId id;

    @Basic
    @Column(name = "fsp_code", nullable = false, length = StringSizeConstraints.MAX_CODE_LENGTH)
    @Convert(converter = FspCodeConverter.class)
    protected FspCode fspCode;

    @Column(name = "name", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(name = "activation_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "termination_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

    @Column(name = "created_at", nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hub_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fsp_hub_FK"))
    protected Hub hub;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "fsp", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = FspCurrency.class, fetch = FetchType.EAGER)
    protected Set<FspCurrency> currencies = new HashSet<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "fsp", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = FspEndpoint.class, fetch = FetchType.EAGER)
    protected Set<FspEndpoint> endpoints = new HashSet<>();

    public Fsp(Hub hub, FspCode fspCode, String name) {

        assert hub != null;
        assert fspCode != null;
        assert name != null;

        this.id = new FspId(Snowflake.get().nextId());
        this.hub = hub;
        this.fspCode(fspCode).name(name);
        this.activationStatus = ActivationStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public Optional<FspCurrency> activate(Currency currency) {

        var fspCurrency = this.currencies.stream().filter(f -> f.getCurrency().equals(currency)).findFirst();

        if (fspCurrency.isPresent()) {

            fspCurrency.get().activate();

            return fspCurrency;
        }

        return Optional.empty();
    }

    public Optional<FspEndpoint> activate(EndpointType type) {

        var fspEndpoint = this.endpoints.stream().filter(f -> f.getType().equals(type)).findFirst();

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

    public FspCurrency addCurrency(Currency currency) {

        assert currency != null;

        var supportedCurrency = new FspCurrency(this, currency);

        this.currencies.add(supportedCurrency);

        return supportedCurrency;
    }

    public FspEndpoint addEndpoint(EndpointType type, String host) {

        assert type != null;
        assert host != null;

        var endpoint = new FspEndpoint(this, type, host);

        this.endpoints.add(endpoint);

        return endpoint;
    }

    public Optional<FspEndpoint> changeEndpoint(EndpointType type, String baseUrl) {

        assert type != null;
        assert baseUrl != null;

        var optEndpoint = this.endpoints.stream().filter(fspEndpoint -> fspEndpoint.getType() == type).findFirst();

        if (optEndpoint.isEmpty()) {
            return optEndpoint;
        }

        var endpoint = optEndpoint.get();

        endpoint.baseUrl(baseUrl);

        return Optional.of(endpoint);
    }

    @Override
    public FspData convert() {

        return new FspData(
            this.getId(), this.getFspCode(), this.getName(), this.getCurrencies().stream().map(FspCurrency::convert).toArray(FspCurrencyData[]::new),
            this.getEndpoints().stream().map(FspEndpoint::convert).collect(Collectors.toMap(FspEndpointData::type, Function.identity(), (existing, replacement) -> replacement)),
            this.activationStatus, this.terminationStatus);
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
        this.currencies.forEach(FspCurrency::deactivate);
        this.endpoints.forEach(FspEndpoint::deactivate);
    }

    public Optional<FspCurrency> deactivate(Currency currency) {

        assert currency != null;

        var optSupportedCurrency = this.currencies.stream().filter(sc -> sc.getCurrency() == currency).findFirst();

        if (optSupportedCurrency.isEmpty()) {
            return optSupportedCurrency;
        }

        optSupportedCurrency.get().deactivate();

        return optSupportedCurrency;
    }

    public Optional<FspEndpoint> deactivate(EndpointType type) {

        assert type != null;

        var optEndpoint = this.endpoints.stream().filter(fspEndpoint -> fspEndpoint.getType() == type).findFirst();

        if (optEndpoint.isEmpty()) {
            return optEndpoint;
        }

        optEndpoint.get().deactivate();

        return optEndpoint;
    }

    public Fsp fspCode(FspCode fspCode) {

        if (fspCode == null) {

            throw new FspCodeRequiredException();
        }

        this.fspCode = fspCode;

        return this;
    }

    public Set<FspCurrency> getCurrencies() {

        return Collections.unmodifiableSet(this.currencies);
    }

    public Optional<FspCurrency> getCurrency(Currency currency) {

        return this.currencies.stream().filter(f -> f.getCurrency().equals(currency)).findFirst();
    }

    public Optional<FspEndpoint> getEndpoint(EndpointType type) {

        return this.endpoints.stream().filter(f -> f.getType().equals(type)).findFirst();
    }

    public Set<FspEndpoint> getEndpoints() {

        return Collections.unmodifiableSet(this.endpoints);
    }

    @Override
    public FspId getId() {

        return this.id;
    }

    public boolean hasEndpoint(EndpointType type) {

        assert type != null;

        return this.endpoints.stream().anyMatch(fspEndpoint -> fspEndpoint.getType() == type);
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

    public boolean isCurrencySupported(Currency currency) {

        assert currency != null;

        return this.currencies.stream().anyMatch(sc -> sc.getCurrency() == currency && sc.isActive());
    }

    public Fsp name(String name) {

        if (name == null || name.isBlank()) {
            throw new FspNameRequiredException();
        }

        var value = name.trim();

        if (name.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
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
