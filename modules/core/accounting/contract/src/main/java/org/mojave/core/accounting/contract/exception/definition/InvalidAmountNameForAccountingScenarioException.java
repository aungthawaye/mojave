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

package org.mojave.core.accounting.contract.exception.definition;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.rule.scenario.ScenarioType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class InvalidAmountNameForAccountingScenarioException extends UncheckedDomainException {

    public static final String CODE = "INVALID_AMOUNT_NAME_FOR_ACCOUNTING_SCENARIO";

    private static final String VALUES_DELIMITER = ",";

    private static final String TEMPLATE = "Amount Name ({0}) is invalid for Accounting Scenario ({1}). It must be one of {2}.";

    private final String amountName;

    private final ScenarioType scenario;

    private final Set<String> amountNames;

    public InvalidAmountNameForAccountingScenarioException(final String amountName,
                                                           final ScenarioType scenario,
                                                           final Set<String> amountNames) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            amountName,
            scenario.name(),
            amountNames.toString()}));

        this.amountName = amountName;
        this.scenario = scenario;
        this.amountNames = amountNames;
    }

    private static Set<String> deserializeValues(final String values) {

        if (values == null || values.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(values.split(VALUES_DELIMITER))
                     .map(String::trim)
                     .filter(value -> !value.isBlank())
                     .collect(Collectors.toUnmodifiableSet());
    }

    public static InvalidAmountNameForAccountingScenarioException from(
        final Map<String, String> extras) {

        final var amountName = extras.get(Keys.AMOUNT_NAME);
        final var scenario = ScenarioType.valueOf(extras.get(Keys.SCENARIO));
        final var amounts = deserializeValues(extras.get(Keys.AMOUNTS));

        return new InvalidAmountNameForAccountingScenarioException(amountName, scenario, amounts);
    }

    private static String serializeValues(final Set<String> values) {

        return String.join(VALUES_DELIMITER, values);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.AMOUNT_NAME, this.amountName);
        extras.put(Keys.SCENARIO, this.scenario.name());
        extras.put(Keys.AMOUNTS, serializeValues(this.amountNames));

        return extras;
    }

    public static class Keys {

        public static final String AMOUNT_NAME = "amountName";

        public static final String SCENARIO = "scenario";

        public static final String AMOUNTS = "amounts";

    }

}
