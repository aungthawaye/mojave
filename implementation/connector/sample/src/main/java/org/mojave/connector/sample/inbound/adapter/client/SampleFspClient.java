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

package org.mojave.connector.sample.inbound.adapter.client;

import com.github.f4b6a3.ulid.UlidCreator;
import org.mojave.connector.adapter.fsp.client.FspClient;
import org.mojave.connector.adapter.fsp.payload.Parties;
import org.mojave.connector.adapter.fsp.payload.Quotes;
import org.mojave.connector.adapter.fsp.payload.Transfers;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.type.Payer;
import org.mojave.fspiop.spec.core.AmountType;
import org.mojave.fspiop.spec.core.Money;
import org.mojave.fspiop.spec.core.PartyComplexName;
import org.mojave.fspiop.spec.core.PartyPersonalInfo;
import org.mojave.fspiop.spec.core.TransferState;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SampleFspClient implements FspClient {

    public SampleFspClient() {

    }

    @Override
    public Parties.Get.Response getParties(Payer payer, Parties.Get.Request request)
        throws FspiopException {

        var partyId = request.partyId();

        if (Long.parseLong(partyId) % 2 == 0) {
            throw new FspiopException(
                FspiopErrors.PARTY_NOT_FOUND,
                request.partyIdType().name() + " with Party ID (" + partyId + ") not found.");
        }

        return new Parties.Get.Response(
            "Nezuko Kamado", new PartyPersonalInfo().complexName(
            new PartyComplexName().firstName("Nezuko").lastName("Kamado")));
    }

    @Override
    public void patchTransfers(Payer payer, Transfers.Patch.Request request)
        throws FspiopException {

        var extensionList = request.extensionList();
        var simulateDispute = false;

        for (var extensionItem : extensionList.getExtension()) {
            if (extensionItem.getKey().equals("simulateDispute")) {
                simulateDispute = true;
                break;
            }
        }

        if (simulateDispute) {

            throw new FspiopException(
                FspiopErrors.PAYEE_FSP_REJECTED_TRANSACTION,
                "Payee's wallet has reached the daily limit.");
        }

    }

    @Override
    public Quotes.Post.Response postQuotes(Payer payer, Quotes.Post.Request request)
        throws FspiopException {

        var currency = request.originalAmount().getCurrency();
        var originalAmount = new BigDecimal(request.originalAmount().getAmount());
        var payeeFspFee = new BigDecimal("0");
        var payeeFspCommission = new BigDecimal("0");
        var payeeReceiveAmount = BigDecimal.ZERO;
        var transferAmount = new BigDecimal("0");

        if (request.amountType() == AmountType.SEND) {

            // Dummy fee 5USD.
            transferAmount = originalAmount;
            payeeReceiveAmount = transferAmount.subtract(payeeFspFee);

        } else {

            // Dummy fee 5USD.
            payeeReceiveAmount = originalAmount;
            transferAmount = originalAmount.add(payeeFspFee);

        }

        var _15minsLater = Instant.now().plus(15, ChronoUnit.MINUTES);

        return new Quotes.Post.Response(
            new Money(currency, originalAmount.stripTrailingZeros().toPlainString()),
            new Money(currency, payeeFspFee.stripTrailingZeros().toPlainString()),
            new Money(currency, payeeFspCommission.stripTrailingZeros().toPlainString()),
            new Money(currency, payeeReceiveAmount.stripTrailingZeros().toPlainString()),
            new Money(currency, transferAmount.stripTrailingZeros().toPlainString()), _15minsLater);
    }

    @Override
    public Transfers.Post.Response postTransfers(Payer payer, Transfers.Post.Request request)
        throws FspiopException {

        var partyId = request.agreement().payee().getPartyIdentifier();
        var transferState = TransferState.RESERVED;

        if (partyId.startsWith("5")) {
            transferState = TransferState.RECEIVED;
        } else if (partyId.startsWith("6")) {
            transferState = TransferState.ABORTED;
        } else if (partyId.startsWith("7")) {
            transferState = TransferState.COMMITTED;
        }

        return new Transfers.Post.Response(
            transferState, UlidCreator.getUlid().toString(), request.extensionList());
    }

}
