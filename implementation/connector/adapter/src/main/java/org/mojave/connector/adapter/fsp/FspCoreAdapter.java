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

package org.mojave.connector.adapter.fsp;

import org.mojave.connector.adapter.fsp.client.FspClient;
import org.mojave.connector.adapter.fsp.payload.Parties;
import org.mojave.connector.adapter.fsp.payload.Quotes;
import org.mojave.connector.adapter.fsp.payload.Transfers;
import org.mojave.fspiop.component.data.Agreement;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.handy.FspiopCurrencies;
import org.mojave.fspiop.component.handy.FspiopDates;
import org.mojave.fspiop.component.interledger.Interledger;
import org.mojave.fspiop.component.participant.ParticipantContext;
import org.mojave.fspiop.component.type.Payer;
import org.mojave.specification.fspiop.core.Extension;
import org.mojave.specification.fspiop.core.ExtensionList;
import org.mojave.specification.fspiop.core.Money;
import org.mojave.specification.fspiop.core.PartiesTypeIDPutResponse;
import org.mojave.specification.fspiop.core.Party;
import org.mojave.specification.fspiop.core.PartyIdInfo;
import org.mojave.specification.fspiop.core.PartyIdType;
import org.mojave.specification.fspiop.core.QuotesIDPutResponse;
import org.mojave.specification.fspiop.core.QuotesPostRequest;
import org.mojave.specification.fspiop.core.TransfersIDPatchResponse;
import org.mojave.specification.fspiop.core.TransfersIDPutResponse;
import org.mojave.specification.fspiop.core.TransfersPostRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class FspCoreAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspCoreAdapter.class);

    private final ParticipantContext participantContext;

    private final FspClient fspClient;

    private final ObjectMapper objectMapper;

    public FspCoreAdapter(ParticipantContext participantContext,
                          FspClient fspClient,
                          ObjectMapper objectMapper) {

        assert null != participantContext;
        assert null != fspClient;
        assert null != objectMapper;

        this.participantContext = participantContext;
        this.fspClient = fspClient;
        this.objectMapper = objectMapper;
    }

    public PartiesTypeIDPutResponse getParties(Payer payer,
                                               PartyIdType partyIdType,
                                               String partyId,
                                               String subId) throws FspiopException {

        try {

            var result = fspClient.getParties(
                payer, new Parties.Get.Request(partyIdType, partyId, subId));

            return new PartiesTypeIDPutResponse(new Party()
                                                    .name(result.name())
                                                    .personalInfo(result.personalInfo())
                                                    .partyIdInfo(new PartyIdInfo()
                                                                     .partyIdType(partyIdType)
                                                                     .partyIdentifier(partyId)
                                                                     .partySubIdOrType(subId))
                                                    .supportedCurrencies(
                                                        this.participantContext.currencies()));

        } catch (FspiopException e) {

            LOGGER.error("Error:", e);
            throw e;

        } catch (Exception e) {

            LOGGER.error("Error:", e);
            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e.getMessage());
        }
    }

    public void patchTransfers(Payer payer, String transferId, TransfersIDPatchResponse response)
        throws FspiopException {

        var ok = false;
        try {

            ok = this.fspClient.patchTransfers(
                payer, new Transfers.Patch.Request(
                    transferId, response.getTransferState(), response.getCompletedTimestamp(),
                    response.getExtensionList()));

            if (!ok) {
                throw new FspiopException(
                    FspiopErrors.GENERIC_PAYEE_ERROR,
                    "Something went wrong while notifying the Payee FSP for the final Transfer state.");
            }

        } catch (FspiopException e) {

            LOGGER.error("Error:", e);
            throw e;

        } catch (Exception e) {

            LOGGER.error("Error:", e);
            throw new FspiopException(
                FspiopErrors.GENERIC_PAYEE_ERROR,
                "Something went wrong while notifying the Payee FSP for the final Transfer state.");
        }

    }

    public QuotesIDPutResponse postQuotes(Payer payer, QuotesPostRequest request)
        throws FspiopException {

        try {

            var quoteId = request.getQuoteId();

            var expiration = request.getExpiration();
            Instant expireAt = null;

            if (expiration != null) {

                expireAt = FspiopDates.fromRequestBody(expiration);

                if (expireAt.isBefore(Instant.now())) {

                    LOGGER.error("Quote request has expired.");
                    throw new FspiopException(
                        FspiopErrors.QUOTE_EXPIRED, "Quote request (" + quoteId + ") has expired.");
                }
            }

            var currency = request.getAmount().getCurrency();

            var result = fspClient.postQuotes(
                payer, new Quotes.Post.Request(
                    quoteId, request.getPayer(), request.getPayee(), request.getAmountType(),
                    request.getAmount(), expireAt));

            var originalAmount = new BigDecimal(result.originalAmount().getAmount());
            var payeeFspFee = new BigDecimal(result.payeeFspFee().getAmount());
            var payeeFspCommission = new BigDecimal(result.payeeFspCommission().getAmount());
            var payeeReceiveAmount = new BigDecimal(result.payeeReceiveAmount().getAmount());
            var transferAmount = new BigDecimal(result.transferAmount().getAmount());
            expiration = FspiopDates.forRequestBody(result.quoteExpireAt());

            var agreement = new Agreement(
                quoteId, request.getPayer().getPartyIdInfo(), request.getPayee().getPartyIdInfo(),
                request.getAmountType(),
                new Money(currency, originalAmount.stripTrailingZeros().toPlainString()),
                new Money(currency, payeeFspFee.stripTrailingZeros().toPlainString()),
                new Money(currency, payeeFspCommission.stripTrailingZeros().toPlainString()),
                new Money(currency, payeeReceiveAmount.stripTrailingZeros().toPlainString()),
                new Money(currency, transferAmount.stripTrailingZeros().toPlainString()),
                result.quoteExpireAt().getEpochSecond());

            var payload = this.objectMapper.writeValueAsString(agreement);
            var preparePacket = Interledger.prepare(
                this.participantContext.ilpSecret(), Interledger.address(payer.fspCode()),
                Interledger.Amount.serialize(
                    transferAmount, FspiopCurrencies.get(currency).scale(),
                    RoundingMode.UNNECESSARY), payload, 900);

            var extensionList = new ExtensionList();
            // Payer related
            extensionList.addExtensionItem(new Extension("payerFspId", payer.fspCode()));
            extensionList.addExtensionItem(
                new Extension(
                    "payerPartyIdType",
                    request.getPayer().getPartyIdInfo().getPartyIdType().toString()));
            extensionList.addExtensionItem(new Extension(
                "payerPartyId",
                request.getPayer().getPartyIdInfo().getPartyIdentifier()));

            // Payee related
            extensionList.addExtensionItem(
                new Extension("payeeFspId", this.participantContext.fspCode()));
            extensionList.addExtensionItem(
                new Extension(
                    "payeePartyIdType",
                    request.getPayee().getPartyIdInfo().getPartyIdType().toString()));
            extensionList.addExtensionItem(new Extension(
                "payeePartyId",
                request.getPayee().getPartyIdInfo().getPartyIdentifier()));

            return new QuotesIDPutResponse()
                       .condition(preparePacket.base64Condition())
                       .ilpPacket(preparePacket.base64PreparePacket())
                       .expiration(expiration)
                       .payeeFspCommission(new Money(
                           currency,
                           payeeFspCommission.stripTrailingZeros().toPlainString()))
                       .payeeFspFee(
                           new Money(currency, payeeFspFee.stripTrailingZeros().toPlainString()))
                       .payeeReceiveAmount(new Money(
                           currency,
                           payeeReceiveAmount.stripTrailingZeros().toPlainString()))
                       .transferAmount(
                           new Money(currency, transferAmount.stripTrailingZeros().toPlainString()))
                       .extensionList(extensionList);

        } catch (FspiopException e) {

            LOGGER.error("Error:", e);
            throw e;

        } catch (Exception e) {

            LOGGER.error("Error:", e);
            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e.getMessage());
        }
    }

    public TransfersIDPutResponse postTransfers(Payer payer, TransfersPostRequest request)
        throws FspiopException {

        try {

            var transferId = request.getTransferId();
            var expiration = request.getExpiration();

            if (expiration != null) {

                var expiredAt = FspiopDates.fromRequestBody(expiration);

                if (expiredAt.isBefore(Instant.now())) {

                    LOGGER.error("Transfer request has expired.");
                    throw new FspiopException(
                        FspiopErrors.TRANSFER_EXPIRED,
                        "Transfer request (" + transferId + ") has expired.");
                }
            }

            var ilpConditionFromRequest = request.getCondition();
            var ilpPacketFromRequest = request.getIlpPacket();
            var unwrappedIlpPacket = Interledger.unwrap(ilpPacketFromRequest);
            var unwrappedIlpPacketData = new String(
                unwrappedIlpPacket.getData(), StandardCharsets.UTF_8);
            var currency = request.getAmount().getCurrency();
            var transferAmount = new BigDecimal(request.getAmount().getAmount());

            var optFulfilment = Interledger.fulfil(
                this.participantContext.ilpSecret(), Interledger.address(payer.fspCode()),
                Interledger.Amount.serialize(
                    transferAmount, FspiopCurrencies.get(currency).scale(),
                    RoundingMode.UNNECESSARY), unwrappedIlpPacketData, ilpConditionFromRequest,
                900);

            if (optFulfilment.isEmpty()) {

                LOGGER.error("Fulfillment is not valid");
                throw new FspiopException(
                    FspiopErrors.GENERIC_VALIDATION_ERROR, "ILP Fulfillment is not valid.");
            }

            var fulfilment = optFulfilment.get();
            LOGGER.debug("Fulfilment : ({})", fulfilment);

            var agreement = this.objectMapper.readValue(unwrappedIlpPacketData, Agreement.class);

            if (agreement.expireAt() != null) {

                var quoteExpiredAt = Instant.ofEpochSecond(agreement.expireAt());

                if (quoteExpiredAt.isBefore(Instant.now())) {

                    LOGGER.error("Quote has expired.");
                    throw new FspiopException(
                        FspiopErrors.QUOTE_EXPIRED,
                        "Quote has already expired. Transfer (" + transferId +
                            ") cannot be performed.");
                }
            }

            var result = this.fspClient.postTransfers(
                payer, new Transfers.Post.Request(
                    request.getTransferId(), agreement,
                    request.getExtensionList()));

            var response = new TransfersIDPutResponse();
            var extensionList = new ExtensionList();

            result.extensionList().getExtension().forEach(extension -> {
                extensionList.addExtensionItem(
                    new Extension(extension.getKey(), extension.getValue()));
            });

            response
                .fulfilment(fulfilment)
                .transferState(result.transferState())
                .completedTimestamp(FspiopDates.forRequestBody())
                .extensionList(extensionList);

            return response;

        } catch (FspiopException e) {

            LOGGER.error("Error:", e);
            throw e;

        } catch (Exception e) {

            LOGGER.error("Error:", e);
            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e.getMessage());
        }

    }

}
