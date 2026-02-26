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

package org.mojave.core.settlement.intercom.controller.api.query;

import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.settlement.contract.data.SettlementRecordData;
import org.mojave.core.settlement.contract.query.SettlementRecordQuery;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class SettlementRecordQueryController {

    private final SettlementRecordQuery settlementRecordQuery;

    public SettlementRecordQueryController(final SettlementRecordQuery settlementRecordQuery) {

        Objects.requireNonNull(settlementRecordQuery);

        this.settlementRecordQuery = settlementRecordQuery;
    }

    @GetMapping("/settlement/settlement-records/get-by-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SettlementRecordData get(
        @RequestParam final SettlementRecordId settlementRecordId) {

        return this.settlementRecordQuery.get(settlementRecordId);
    }

    @GetMapping("/settlement/settlement-records/get-by-transaction-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SettlementRecordData> get(
        @RequestParam final TransactionId transactionId) {

        return this.settlementRecordQuery.get(transactionId);
    }

    @GetMapping("/settlement/settlement-records/get-all")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SettlementRecordData> getAll() {

        return this.settlementRecordQuery.getAll();
    }

}
