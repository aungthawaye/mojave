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
import org.mojave.scheme.rule.type.TransactionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class InvalidParticipantForTransactionTypeException extends UncheckedDomainException {

    public static final String CODE = "INVALID_PARTICIPANT_FOR_TRANSACTION_TYPE";

    private static final String VALUES_DELIMITER = ",";

    private static final String TEMPLATE = "Participant is invalid for Transaction Type. It must be one of {0}.";

    private final TransactionType transactionType;

    private final Set<String> participants;

    public InvalidParticipantForTransactionTypeException(final TransactionType transactionType,
                                                         final Set<String> participants) {

        super(new ErrorTemplate(
            CODE, TEMPLATE,
            new String[]{participants.toString()}));

        this.transactionType = transactionType;
        this.participants = participants;
    }

    public static InvalidParticipantForTransactionTypeException from(final Map<String, String> extras) {

        final var type = TransactionType.valueOf(extras.get(Keys.TRANSACTION_TYPE));
        final var participants = deserializeValues(extras.get(Keys.PARTICIPANTS));

        return new InvalidParticipantForTransactionTypeException(type, participants);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.TRANSACTION_TYPE, this.transactionType.name());
        extras.put(Keys.PARTICIPANTS, serializeValues(this.participants));

        return extras;
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

    private static String serializeValues(final Set<String> values) {

        return String.join(VALUES_DELIMITER, values);
    }

    public static class Keys {

        public static final String TRANSACTION_TYPE = "transactionType";

        public static final String PARTICIPANTS = "participants";

    }

}
