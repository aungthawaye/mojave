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

package io.mojaloop.core.transaction.contract.data.definition;

import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.TerminationStatus;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.definition.fundtransfer.PostingAmountType;
import io.mojaloop.core.common.datatype.enums.trasaction.definition.fundtransfer.PostingOwnerType;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.transaction.DefinitionId;
import io.mojaloop.core.common.datatype.identifier.transaction.PostingId;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.List;
import java.util.Objects;

public record FundTransferDefinitionData(DefinitionId definitionId,
                                         Currency currency,
                                         String name,
                                         String description,
                                         ActivationStatus activationStatus,
                                         TerminationStatus terminationStatus,
                                         List<Posting> postings) {

    public static record Posting(PostingId postingId,
                                 PostingOwnerType forOwner,
                                 PostingAmountType forAmount,
                                 Side side,
                                 ChartEntryId chartEntryId,
                                 String description) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof Posting that)) {
                return false;
            }
            return Objects.equals(postingId, that.postingId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(postingId);
        }
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof FundTransferDefinitionData that)) {
            return false;
        }
        return Objects.equals(definitionId, that.definitionId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(definitionId);
    }
}
