/*-
 * ==============================================================================
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
 * ==============================================================================
 */

package io.mojaloop.core.participant.domain.model.hub;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.exception.input.BlankOrEmptyInputException;
import io.mojaloop.component.misc.exception.input.TextTooLargeException;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.participant.HubIdJavaType;
import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.participant.contract.data.HubData;
import io.mojaloop.core.participant.contract.exception.CurrencyAlreadySupportedException;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

@Getter
@Entity
@Table(name = "pcp_hub")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hub extends JpaEntity<HubId> implements DataConversion<io.mojaloop.core.participant.contract.data.HubData> {

    @Id
    @JavaType(HubIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "hub_id", nullable = false, updatable = false)
    protected HubId id;

    @Column(name = "name", length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Getter(AccessLevel.NONE)
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "hub", targetEntity = HubCurrency.class, fetch = FetchType.EAGER)
    protected Set<HubCurrency> currencies = new HashSet<>();

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    public Hub(String name) {

        assert name != null;

        this.id = new HubId(Snowflake.get().nextId());
        this.name(name);
        this.createdAt = Instant.now();
    }

    public boolean activateCurrency(Currency currency) {

        assert currency != null;

        var optSupportedCurrency = this.currencies.stream().filter(sc -> sc.getCurrency() == currency).findFirst();

        if (optSupportedCurrency.isEmpty()) {
            return false;
        }

        optSupportedCurrency.get().activate();

        return true;
    }

    public HubCurrency addCurrency(Currency currency) throws CurrencyAlreadySupportedException {

        assert currency != null;

        if (this.isCurrencySupported(currency)) {

            throw new CurrencyAlreadySupportedException(currency);
        }

        var supportedCurrency = new HubCurrency(this, currency);

        this.currencies.add(supportedCurrency);

        return supportedCurrency;
    }

    @Override
    public HubData convert() {

        return new HubData(this.getId(), this.getName(), this.getCurrencies().stream().map(HubCurrency::convert)
                                                             .toArray(io.mojaloop.core.participant.contract.data.HubData.HubCurrencyData[]::new));
    }

    public boolean deactivateCurrency(Currency currency) {

        assert currency != null;

        var optSupportedCurrency = this.currencies.stream().filter(sc -> sc.getCurrency() == currency).findFirst();

        if (optSupportedCurrency.isEmpty()) {
            return false;
        }

        optSupportedCurrency.get().deactivate();

        return true;
    }

    public Set<HubCurrency> getCurrencies() {

        return Collections.unmodifiableSet(this.currencies);
    }

    public boolean isCurrencySupported(Currency currency) {

        assert currency != null;

        return this.currencies.stream().anyMatch(sc -> sc.getCurrency() == currency);
    }

    public Hub name(String name) {

        assert name != null;

        var value = name.trim();

        if (value.isEmpty()) {
            throw new BlankOrEmptyInputException("Hub Name");
        }

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new TextTooLargeException("Hub Name", StringSizeConstraints.MAX_NAME_TITLE_LENGTH);
        }

        this.name = value;

        return this;
    }

}
