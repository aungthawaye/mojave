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
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.data.FspCurrencyData;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.FspEndpointData;
import io.mojaloop.core.participant.contract.exception.fsp.CannotActivateFspCurrencyException;
import io.mojaloop.core.participant.contract.exception.fsp.CannotActivateFspEndpointException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeRequiredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspNameRequiredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspNameTooLongException;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * @author Mojaloop OSS
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

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "fsp", targetEntity = FspCurrency.class, fetch = FetchType.EAGER)
    protected Set<FspCurrency> currencies = new HashSet<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "fsp", targetEntity = FspEndpoint.class, fetch = FetchType.EAGER)
    protected Set<FspEndpoint> endpoints = new HashSet<>();

    @Column(name = "activation_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "termination_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

    @Column(name = "created_at", nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    /**
     * Creates a new FSP with the specified code and name.
     *
     * @param fspCode the unique FSP code identifier
     * @param name the human-readable name of the FSP
     * @throws AssertionError if fspCode or name is null
     */
    public Fsp(FspCode fspCode, String name) {

        assert fspCode != null;
        assert name != null;

        this.id = new FspId(Snowflake.get().nextId());
        this.fspCode(fspCode).name(name);
        this.activationStatus = ActivationStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    /**
     * Activates this FSP and attempts to activate all its currencies and endpoints.
     *
     * <p>This method sets the FSP's activation status to ACTIVE and tries to activate
     * all associated currencies and endpoints. If individual currencies or endpoints
     * cannot be activated, those exceptions are silently ignored.</p>
     */
    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;

        this.currencies.forEach(sc -> {
            try {
                sc.activate();
            } catch (CannotActivateFspCurrencyException ignored) { }
        });

        this.endpoints.forEach(e -> {
            try {
                e.activate();
            } catch (CannotActivateFspEndpointException ignored) { }
        });
    }

    /**
     * Activates support for a specific currency.
     *
     * @param currency the currency to activate
     * @throws CannotActivateFspCurrencyException if the currency cannot be activated
     * @throws AssertionError if currency is null
     */
    public void activateCurrency(Currency currency) throws CannotActivateFspCurrencyException {

        assert currency != null;

        var optSupportedCurrency = this.currencies.stream().filter(sc -> sc.getCurrency() == currency).findFirst();

        if (optSupportedCurrency.isEmpty()) {
            return;
        }

        optSupportedCurrency.get().activate();
    }

    /**
     * Activates an endpoint of the specified type.
     *
     * @param type the type of endpoint to activate
     * @throws CannotActivateFspEndpointException if the endpoint cannot be activated
     * @throws AssertionError if type is null
     */
    public void activateEndpoint(EndpointType type) throws CannotActivateFspEndpointException {

        assert type != null;

        var optEndpoint = this.endpoints.stream().filter(fspEndpoint -> fspEndpoint.getType() == type).findFirst();

        if (optEndpoint.isEmpty()) {
            return;
        }

        optEndpoint.get().activate();
    }

    /**
     * Adds support for a new currency to this FSP.
     *
     * @param currency the currency to add support for
     * @return the newly created FspCurrency instance
     * @throws FspCurrencyAlreadySupportedException if the currency is already supported
     * @throws AssertionError if currency is null
     */
    public FspCurrency addCurrency(Currency currency) throws FspCurrencyAlreadySupportedException {

        assert currency != null;

        if (this.isCurrencySupported(currency)) {
            throw new FspCurrencyAlreadySupportedException(currency);
        }

        var supportedCurrency = new FspCurrency(this, currency);

        this.currencies.add(supportedCurrency);

        return supportedCurrency;
    }

    /**
     * Adds a new endpoint configuration to this FSP.
     *
     * @param type the type of endpoint to add
     * @param host the host URL for the endpoint
     * @return the newly created FspEndpoint instance
     * @throws FspEndpointAlreadyConfiguredException if an endpoint of this type already exists
     * @throws AssertionError if type or host is null
     */
    public FspEndpoint addEndpoint(EndpointType type, String host) throws FspEndpointAlreadyConfiguredException {

        assert type != null;
        assert host != null;

        if (this.hasEndpoint(type)) {

            throw new FspEndpointAlreadyConfiguredException(type);
        }

        var endpoint = new FspEndpoint(this, type, host);

        this.endpoints.add(endpoint);

        return endpoint;
    }

    /**
     * Changes the base URL of an existing endpoint.
     *
     * @param type the type of endpoint to modify
     * @param baseUrl the new base URL for the endpoint
     * @return true if the endpoint was found and updated, false otherwise
     * @throws AssertionError if type or baseUrl is null
     */
    public boolean changeEndpoint(EndpointType type, String baseUrl) {

        assert type != null;
        assert baseUrl != null;

        var optEndpoint = this.endpoints.stream().filter(fspEndpoint -> fspEndpoint.getType() == type).findFirst();

        if (optEndpoint.isEmpty()) {
            return false;
        }

        var endpoint = optEndpoint.get();

        endpoint.baseUrl(baseUrl);

        return true;
    }

    @Override
    public FspData convert() {

        return new FspData(this.getId(), this.getFspCode(), this.getName(),
                           this.getCurrencies().stream().map(FspCurrency::convert).toArray(FspCurrencyData[]::new),
                           this.getEndpoints().stream().map(FspEndpoint::convert)
                               .collect(Collectors.toMap(FspEndpointData::type, Function.identity(), (existing, replacement) -> replacement)),
                           this.activationStatus, this.terminationStatus);
    }

    /**
     * Deactivates this FSP and all its currencies and endpoints.
     *
     * <p>This method sets the FSP's activation status to INACTIVE and deactivates
     * all associated currencies and endpoints.</p>
     */
    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
        this.currencies.forEach(FspCurrency::deactivate);
        this.endpoints.forEach(FspEndpoint::deactivate);
    }

    /**
     * Deactivates support for a specific currency.
     *
     * @param currency the currency to deactivate
     * @throws AssertionError if currency is null
     */
    public void deactivateCurrency(Currency currency) {

        assert currency != null;

        var optSupportedCurrency = this.currencies.stream().filter(sc -> sc.getCurrency() == currency).findFirst();

        if (optSupportedCurrency.isEmpty()) {
            return;
        }

        optSupportedCurrency.get().deactivate();
    }

    /**
     * Deactivates an endpoint of the specified type.
     *
     * @param type the type of endpoint to deactivate
     * @throws AssertionError if type is null
     */
    public void deactivateEndpoint(EndpointType type) {

        assert type != null;

        var optEndpoint = this.endpoints.stream().filter(fspEndpoint -> fspEndpoint.getType() == type).findFirst();

        if (optEndpoint.isEmpty()) {
            return;
        }

        optEndpoint.get().deactivate();
    }

    /**
     * Sets the FSP code for this FSP.
     *
     * @param fspCode the FSP code to set
     * @return this FSP instance for method chaining
     * @throws AssertionError if fspCode is null
     */
    public Fsp fspCode(FspCode fspCode) {

        if (fspCode == null) {

            throw new FspCodeRequiredException();
        }

        this.fspCode = fspCode;

        return this;
    }

    /**
     * Returns an unmodifiable set of currencies supported by this FSP.
     *
     * @return an unmodifiable set of FspCurrency instances
     */
    public Set<FspCurrency> getCurrencies() {

        return Collections.unmodifiableSet(this.currencies);
    }

    /**
     * Returns an unmodifiable set of endpoints configured for this FSP.
     *
     * @return an unmodifiable set of FspEndpoint instances
     */
    public Set<FspEndpoint> getEndpoints() {

        return Collections.unmodifiableSet(this.endpoints);
    }

    @Override
    public FspId getId() {

        return this.id;
    }

    /**
     * Checks if this FSP has an endpoint of the specified type.
     *
     * @param type the endpoint type to check for
     * @return true if an endpoint of the specified type exists, false otherwise
     * @throws AssertionError if type is null
     */
    public boolean hasEndpoint(EndpointType type) {

        assert type != null;

        return this.endpoints.stream().anyMatch(fspEndpoint -> fspEndpoint.getType() == type);
    }

    /**
     * Checks if this FSP is currently active.
     *
     * @return true if the FSP's activation status is ACTIVE, false otherwise
     */
    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

    /**
     * Checks if this FSP supports the specified currency.
     *
     * @param currency the currency to check for support
     * @return true if the currency is supported, false otherwise
     * @throws AssertionError if currency is null
     */
    public boolean isCurrencySupported(Currency currency) {

        assert currency != null;

        return this.currencies.stream().anyMatch(sc -> sc.getCurrency() == currency);
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

    /**
     * Terminates this FSP by setting its activation status to INACTIVE.
     *
     * <p>This method deactivates the FSP but does not affect the individual
     * currencies and endpoints, unlike the {@link #deactivate()} method.</p>
     *
     * @see #deactivate()
     */
    public void terminate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

}
