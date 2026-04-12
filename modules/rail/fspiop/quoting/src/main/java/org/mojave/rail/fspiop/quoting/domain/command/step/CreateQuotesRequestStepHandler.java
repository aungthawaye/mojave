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

package org.mojave.rail.fspiop.quoting.domain.command.step;

import org.mojave.scheme.rule.enums.Direction;
import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.quoting.contract.command.step.CreateQuotesRequestStep;
import org.mojave.rail.fspiop.quoting.contract.exception.ExpirationNotInFutureException;
import org.mojave.rail.fspiop.quoting.domain.model.Party;
import org.mojave.rail.fspiop.quoting.domain.model.Quote;
import org.mojave.rail.fspiop.quoting.domain.repository.QuoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CreateQuotesRequestStepHandler implements CreateQuotesRequestStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateQuotesRequestStepHandler.class);

    private final QuoteRepository quoteRepository;

    public CreateQuotesRequestStepHandler(QuoteRepository quoteRepository) {

        Objects.requireNonNull(quoteRepository);

        this.quoteRepository = quoteRepository;
    }

    @Transactional
    @Write
    @Override
    public void execute(Input input) throws FspiopException {

        MDC.put("REQ_ID", input.udfQuoteId().getId());

        var startAt = System.nanoTime();

        LOGGER.info("CreateQuotesRequestStep : input : ({})", ObjectLogger.log(input));

        try {

            var quote = new Quote(
                input.payerFspId(), input.payeeFspId(), input.udfQuoteId(), input.currency(),
                input.amount(), input.fees(), input.amountType(), input.scenario(),
                input.subScenario(), input.initiator(), input.initiatorType(),
                input.requestExpiration(),
                new Party(input.payerPartyIdType(), input.payerPartyId(), input.payerSubId()),
                new Party(input.payeePartyIdType(), input.payeePartyId(), input.payeeSubId()));

            var extensionList = input.extensionList();

            if (extensionList != null && extensionList.getExtension() != null) {

                extensionList
                    .getExtension()
                    .forEach(ext -> quote.addExtension(
                        Direction.FROM_PAYEE, ext.getKey(),
                        ext.getValue()));
            }

            this.quoteRepository.save(quote);

            var endAt = System.nanoTime();
            LOGGER.info(
                "CreateQuotesRequestStep : done, took {} ms", (endAt - startAt) / 1_000_000);

        } catch (ExpirationNotInFutureException ignored) {

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());

        } finally {

            MDC.remove("REQ_ID");
        }
    }

}
