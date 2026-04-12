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

package org.mojave.rail.fspiop.quoting.contract.command.step;

import org.mojave.scheme.rule.identifier.quoting.UdfQuoteId;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.spec.ExtensionList;

import java.math.BigDecimal;
import java.time.Instant;

public interface UpdateQuotesResponseStep {

    void execute(Input input) throws FspiopException;

    record Input(
        UdfQuoteId udfQuoteId,
        Instant responseExpiration,
        BigDecimal transferAmount,
        BigDecimal payeeFspFee,
        BigDecimal payeeFspCommission,
        BigDecimal payeeReceiveAmount,
        String ilpPacket,
        String condition,
        ExtensionList extensionList) { }

}
