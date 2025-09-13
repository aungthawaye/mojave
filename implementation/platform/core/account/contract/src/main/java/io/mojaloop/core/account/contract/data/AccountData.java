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

package io.mojaloop.core.account.contract.data;

import io.mojaloop.core.common.datatype.enums.TerminationStatus;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.account.ChartId;
import io.mojaloop.core.common.datatype.identifier.account.LedgerBalanceId;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record AccountData(AccountId accountId,
                          OwnerId ownerId,
                          Currency currency,
                          AccountCode code,
                          String name,
                          String description,
                          OverdraftMode overdraftMode,
                          Instant createdAt,
                          TerminationStatus terminationStatus,
                          ChartEntryData chartEntry,
                          LedgerBalanceData ledgerBalance) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof AccountData accountData)) {
            return false;
        }
        return Objects.equals(accountId, accountData.accountId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(accountId);
    }

    public record ChartEntryData(ChartEntryId chartEntryId,
                                 ChartEntryCode code,
                                 String name,
                                 String description,
                                 AccountType accountType,
                                 Instant createdAt,
                                 ChartData chart) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof ChartEntryData that)) {
                return false;
            }
            return Objects.equals(chartEntryId, that.chartEntryId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(chartEntryId);
        }

        public record ChartData(ChartId chartId, String name, Instant createdAt) {

            @Override
            public boolean equals(Object o) {

                if (!(o instanceof ChartData that)) {
                    return false;
                }
                return Objects.equals(chartId, that.chartId);
            }

            @Override
            public int hashCode() {

                return Objects.hashCode(chartId);
            }

        }

    }

    public record LedgerBalanceData(LedgerBalanceId ledgerBalanceId, BigDecimal postedDebits, BigDecimal postedCredits) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof LedgerBalanceData that)) {
                return false;
            }
            return Objects.equals(ledgerBalanceId, that.ledgerBalanceId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(ledgerBalanceId);
        }

    }

}
