/*-
 * ================================================================================
 * Mojave
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

package io.mojaloop.connector.adapter.fsp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.connector.adapter.fsp.client.FspClient;
import io.mojaloop.connector.adapter.fsp.payload.Parties;
import io.mojaloop.connector.adapter.fsp.payload.Quotes;
import io.mojaloop.connector.adapter.fsp.payload.Transfers;
import io.mojaloop.fspiop.common.data.Agreement;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopCurrencies;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.interledger.Interledger;
import io.mojaloop.fspiop.spec.core.Extension;
import io.mojaloop.fspiop.spec.core.ExtensionList;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.Party;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
import io.mojaloop.fspiop.spec.core.TransferState;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Component
public class FspCoreAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspCoreAdapter.class);

    private final ParticipantContext participantContext;

    private final FspClient fspClient;

    private final ObjectMapper objectMapper;

    public FspCoreAdapter(ParticipantContext participantContext, FspClient fspClient, ObjectMapper objectMapper) {

        assert null != participantContext;
        assert null != fspClient;
        assert null != objectMapper;

        this.participantContext = participantContext;
        this.fspClient = fspClient;
        this.objectMapper = objectMapper;
    }

    public PartiesTypeIDPutResponse getParties(Payer payer, PartyIdType partyIdType, String partyId, String subId) throws FspiopException {

        try {

            LOGGER.debug("Getting parties: {}", partyIdType);

            LOGGER.info("Getting parties from FSP Core: {}", partyIdType);
            var result = fspClient.getParties(payer, new Parties.Get.Request(partyIdType, partyId, subId));
            LOGGER.info("Got parties from FSP Core: {}", result);

            var party = new Party().name(result.name()).personalInfo(result.personalInfo())
                                   .partyIdInfo(new PartyIdInfo().partyIdType(partyIdType).partyIdentifier(partyId).partySubIdOrType(subId))
                                   .supportedCurrencies(result.supportedCurrencies());

            var response = new PartiesTypeIDPutResponse(party);

            LOGGER.debug("Returning parties: {}", response);
            return response;

        } catch (FspiopException e) {

            LOGGER.error("Error while getting parties", e);
            throw e;

        } catch (Exception e) {

            LOGGER.error("Error while getting parties", e);
            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e.getMessage());
        }
    }

    public void patchTransfers(Payer payer, String transferId, TransfersIDPatchResponse response) throws FspiopException {

        try {

            LOGGER.info("Confirming transfer: {}", response);
            this.fspClient.patchTransfers(
                payer, new Transfers.Patch.Request(transferId, response.getTransferState(), response.getCompletedTimestamp(), response.getExtensionList()));
            LOGGER.info("Confirmed transfer: {}", response);

        } catch (FspiopException e) {

            LOGGER.error("Error while posting transfers", e);
            throw e;

        } catch (Exception e) {

            LOGGER.error("Error while posting transfers", e);
            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e.getMessage());
        }

    }

    public QuotesIDPutResponse postQuotes(Payer payer, QuotesPostRequest request) throws FspiopException {

        try {

            LOGGER.debug("Posting quotes: {}", request);

            var quoteId = request.getQuoteId();

            var expiration = request.getExpiration();

            if (expiration != null) {

                var expiredAt = FspiopDates.fromRequestBody(expiration);

                if (expiredAt.isBefore(Instant.now())) {

                    LOGGER.error("Quote request has expired.");
                    throw new FspiopException(FspiopErrors.QUOTE_EXPIRED, "Quote request (" + quoteId + ") has expired.");
                }
            }

            var currency = request.getAmount().getCurrency();

            LOGGER.info("Getting quotes from FSP Core: {}", quoteId);
            var result = fspClient.postQuotes(
                payer,
                new Quotes.Post.Request(quoteId, request.getPayer(), request.getPayee(), request.getAmountType(), request.getAmount(), request.getExpiration()));
            LOGGER.info("Got quotes from FSP Core: {}", result);

            var originalAmount = new BigDecimal(result.originalAmount().getAmount());
            var payeeFspFee = new BigDecimal(result.payeeFspFee().getAmount());
            var payeeFspCommission = new BigDecimal(result.payeeFspCommission().getAmount());
            var payeeReceiveAmount = new BigDecimal(result.payeeReceiveAmount().getAmount());
            var transferAmount = new BigDecimal(result.transferAmount().getAmount());

            var agreement = new Agreement(
                quoteId, request.getPayer().getPartyIdInfo(), request.getPayee().getPartyIdInfo(), request.getAmountType(), new Money(currency, originalAmount.toPlainString()),
                new Money(currency, payeeFspFee.toPlainString()), new Money(currency, payeeFspCommission.toPlainString()), new Money(currency, payeeReceiveAmount.toPlainString()),
                new Money(currency, transferAmount.toPlainString()), result.expiration());

            var payload = this.objectMapper.writeValueAsString(agreement);
            var preparePacket = Interledger.prepare(
                this.participantContext.ilpSecret(), Interledger.address(payer.fspCode()),
                Interledger.Amount.serialize(transferAmount, FspiopCurrencies.get(currency).scale(), RoundingMode.UNNECESSARY), payload, 900);

            var extensionList = new ExtensionList();
            // Payer related
            extensionList.addExtensionItem(new Extension("payerFspId", payer.fspCode()));
            extensionList.addExtensionItem(new Extension("payerPartyIdType", request.getPayer().getPartyIdInfo().getPartyIdType().toString()));
            extensionList.addExtensionItem(new Extension("payerPartyId", request.getPayer().getPartyIdInfo().getPartyIdentifier()));

            // Payee related
            extensionList.addExtensionItem(new Extension("payeeFspId", this.participantContext.fspCode()));
            extensionList.addExtensionItem(new Extension("payeePartyIdType", request.getPayee().getPartyIdInfo().getPartyIdType().toString()));
            extensionList.addExtensionItem(new Extension("payeePartyId", request.getPayee().getPartyIdInfo().getPartyIdentifier()));

            var response = new QuotesIDPutResponse().condition(preparePacket.base64Condition()).ilpPacket(preparePacket.base64PreparePacket()).expiration(result.expiration())
                                                    .payeeFspCommission(new Money(currency, payeeFspCommission.toPlainString()))
                                                    .payeeFspFee(new Money(currency, payeeFspFee.toPlainString()))
                                                    .payeeReceiveAmount(new Money(currency, payeeReceiveAmount.toPlainString()))
                                                    .transferAmount(new Money(currency, transferAmount.toPlainString())).extensionList(extensionList);

            LOGGER.debug("Returning quotes: {}", response);

            return response;

        } catch (FspiopException e) {

            LOGGER.error("Error while getting quotes", e);
            throw e;

        } catch (Exception e) {

            LOGGER.error("Error while getting quotes", e);
            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e.getMessage());
        }
    }

    public TransfersIDPutResponse postTransfers(Payer payer, TransfersPostRequest request) throws FspiopException {

        try {

            LOGGER.debug("Posting transfers: {}", request);

            var transferId = request.getTransferId();

            var expiration = request.getExpiration();

            if (expiration != null) {

                var expiredAt = FspiopDates.fromRequestBody(expiration);

                if (expiredAt.isBefore(Instant.now())) {

                    LOGGER.error("Transfer request has expired.");
                    throw new FspiopException(FspiopErrors.TRANSFER_EXPIRED, "Transfer request (" + transferId + ") has expired.");
                }
            }

            var ilpConditionFromRequest = request.getCondition();
            var ilpPacketFromRequest = request.getIlpPacket();
            var unwrappedIlpPacket = Interledger.unwrap(ilpPacketFromRequest);
            var unwrappedIlpPacketData = new String(unwrappedIlpPacket.getData(), StandardCharsets.UTF_8);

            LOGGER.debug("Transfer Id : [{}]", transferId);
            LOGGER.debug("Request ILP Packet : [{}]", ilpPacketFromRequest);
            LOGGER.debug("Prepare ILP Packet : [{}]", unwrappedIlpPacket);
            LOGGER.debug("ILP Packet Data : [{}]", unwrappedIlpPacketData);

            var currency = request.getAmount().getCurrency();
            var transferAmount = new BigDecimal(request.getAmount().getAmount());

            var optFulfilment = Interledger.fulfil(
                this.participantContext.ilpSecret(), Interledger.address(payer.fspCode()),
                Interledger.Amount.serialize(transferAmount, FspiopCurrencies.get(currency).scale(), RoundingMode.UNNECESSARY), unwrappedIlpPacketData, ilpConditionFromRequest,
                900);

            if (optFulfilment.isEmpty()) {

                LOGGER.error("Fulfillment is not valid");
                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "ILP Fulfillment is not valid.");
            }

            var fulfilment = optFulfilment.get();
            LOGGER.debug("Fulfilment : [{}]", fulfilment);

            var agreement = this.objectMapper.readValue(unwrappedIlpPacketData, Agreement.class);

            if (agreement.expiration() != null) {

                var quoteExpiredAt = FspiopDates.fromRequestBody(agreement.expiration());

                if (quoteExpiredAt.isBefore(Instant.now())) {

                    LOGGER.error("Quote has expired.");
                    throw new FspiopException(FspiopErrors.QUOTE_EXPIRED, "Quote has already expired. Transfer (" + transferId + ") cannot be performed.");
                }
            }

            LOGGER.info("Posting transfers to FSP Core: {}", agreement);
            var result = this.fspClient.postTransfers(payer, new Transfers.Post.Request(request.getTransferId(), agreement, request.getExtensionList()));
            LOGGER.info("Got transfers from FSP Core: {}", result);

            var response = new TransfersIDPutResponse();

            response.fulfilment(fulfilment).transferState(TransferState.RESERVED).completedTimestamp(FspiopDates.forRequestBody()).extensionList(
                new ExtensionList().addExtensionItem(new Extension("homeTransactionId", UUID.randomUUID().toString())).addExtensionItem(new Extension("transferId", transferId)));

            LOGGER.debug("Returning transfers: {}", response);

            return response;

        } catch (FspiopException e) {

            LOGGER.error("Error while posting transfers", e);
            throw e;

        } catch (Exception e) {

            LOGGER.error("Error while posting transfers", e);
            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e.getMessage());
        }

    }

}
