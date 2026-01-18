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
package org.mojave.core.accounting.contract.exception.ledger;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public class RequiredParticipantNotFoundInTransactionException extends UncheckedDomainException {

    public static final String CODE = "REQUIRED_PARTICIPANT_NOT_FOUND_IN_TRANSACTION";

    private static final String TEMPLATE = "Required Participant ({0}) cannot be found in participants ({1}) of Transaction Id ({2}).";

    private final String participant;

    private final Set<String> participants;

    private final TransactionId transactionId;

    public RequiredParticipantNotFoundInTransactionException(final String participant,
                                                             final Set<String> participants,
                                                             final TransactionId transactionId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            participant,
            participants.toString(),
            transactionId.getId().toString()}));

        this.participant = participant;
        this.participants = participants;
        this.transactionId = transactionId;
    }

    public static RequiredParticipantNotFoundInTransactionException from(final Map<String, String> extras) {

        final var participant = extras.get(Keys.PARTICIPANT);
        final var participants = Set.of(extras.get(Keys.PARTICIPANTS));
        final var transactionId = new TransactionId(Long.valueOf(extras.get(Keys.TRANSACTION_ID)));

        return new RequiredParticipantNotFoundInTransactionException(
            participant, participants, transactionId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.PARTICIPANT, this.participant);
        extras.put(Keys.PARTICIPANTS, this.participants.toString());
        extras.put(Keys.TRANSACTION_ID, this.transactionId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String PARTICIPANT = "participant";

        public static final String PARTICIPANTS = "participants";

        public static final String TRANSACTION_ID = "transactionId";

    }

}
