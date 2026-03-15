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

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.rail.fspiop.quoting.contract.command.step.FindQuotesStep;
import org.mojave.rail.fspiop.quoting.domain.repository.QuoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class FindQuotesStepHandler implements FindQuotesStep {

    private final QuoteRepository quoteRepository;

    public FindQuotesStepHandler(QuoteRepository quoteRepository) {

        Objects.requireNonNull(quoteRepository);

        this.quoteRepository = quoteRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public Output execute(Input input) {

        return new Output(
            this.quoteRepository.findOne(
                QuoteRepository.Filters.withUdfQuoteId(input.udfQuoteId())));
    }

}
