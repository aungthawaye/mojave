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

package org.mojave.accounting.contract.exception.definition;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.rule.accounting.scenario.AccountingScenario;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class InvalidParticipantForAccountingScenarioException extends UncheckedDomainException {

    public static final String CODE = "INVALID_PARTICIPANT_FOR_ACCOUNTING_SCENARIO";

    private static final String VALUES_DELIMITER = ",";

    private static final String TEMPLATE = "Participant is invalid for Accounting Scenario. It must be one of {0}.";

    private final AccountingScenario scenario;

    private final Set<String> participants;

    public InvalidParticipantForAccountingScenarioException(final AccountingScenario scenario,
                                                            final Set<String> participants) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{participants.toString()}));

        this.scenario = scenario;
        this.participants = participants;
    }

    private static Set<String> deserializeValues(final String values) {

        if (values == null || values.isBlank()) {
            return Set.of();
        }

        return Arrays
                   .stream(values.split(VALUES_DELIMITER))
                   .map(String::trim)
                   .filter(value -> !value.isBlank())
                   .collect(Collectors.toUnmodifiableSet());
    }

    public static InvalidParticipantForAccountingScenarioException from(final Map<String, String> extras) {

        final var scenario = AccountingScenario.valueOf(extras.get(Keys.SCENARIO));
        final var participants = deserializeValues(extras.get(Keys.PARTICIPANTS));

        return new InvalidParticipantForAccountingScenarioException(scenario, participants);
    }

    private static String serializeValues(final Set<String> values) {

        return String.join(VALUES_DELIMITER, values);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.SCENARIO, this.scenario.name());
        extras.put(Keys.PARTICIPANTS, serializeValues(this.participants));

        return extras;
    }

    public static class Keys {

        public static final String SCENARIO = "scenario";

        public static final String PARTICIPANTS = "participants";

    }

}
