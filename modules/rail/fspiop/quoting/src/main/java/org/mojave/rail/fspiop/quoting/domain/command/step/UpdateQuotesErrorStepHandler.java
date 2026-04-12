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

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.scheme.rule.enums.Direction;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.quoting.contract.command.step.UpdateQuotesErrorStep;
import org.mojave.rail.fspiop.quoting.domain.repository.QuoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UpdateQuotesErrorStepHandler implements UpdateQuotesErrorStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        UpdateQuotesErrorStepHandler.class);

    private final QuoteRepository quoteRepository;

    public UpdateQuotesErrorStepHandler(QuoteRepository quoteRepository) {

        Objects.requireNonNull(quoteRepository);

        this.quoteRepository = quoteRepository;
    }

    @Transactional
    @Write
    @Override
    public void execute(Input input) throws FspiopException {

        MDC.put("REQ_ID", input.udfQuoteId().getId());

        var startAt = System.nanoTime();

        LOGGER.info("UpdateQuotesErrorStep : udfQuoteId : ({})", input.udfQuoteId().getId());

        try {

            var optQuote = this.quoteRepository.findOne(
                QuoteRepository.Filters.withUdfQuoteId(input.udfQuoteId()));

            if (optQuote.isEmpty()) {

                LOGGER.warn(
                    "UpdateQuotesErrorStep : quote not found. udfQuoteId : ({})",
                    input.udfQuoteId().getId());

                return;
            }

            var quote = optQuote.get();

            quote.error(input.errorDescription());

            var extensionList = input.extensionList();

            if (extensionList != null && extensionList.getExtension() != null) {

                extensionList.getExtension().forEach(ext ->
                    quote.addExtension(Direction.TO_PAYEE, ext.getKey(), ext.getValue()));
            }

            this.quoteRepository.save(quote);

            var endAt = System.nanoTime();
            LOGGER.info(
                "UpdateQuotesErrorStep : done, took {} ms", (endAt - startAt) / 1_000_000);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());

        } finally {

            MDC.remove("REQ_ID");
        }
    }

}
