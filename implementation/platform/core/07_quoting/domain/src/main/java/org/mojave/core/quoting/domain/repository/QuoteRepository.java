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
package org.mojave.core.quoting.domain.repository;

import org.mojave.core.common.datatype.identifier.quoting.QuoteId;
import org.mojave.core.common.datatype.identifier.quoting.UdfQuoteId;
import org.mojave.core.quoting.domain.model.Quote;
import org.mojave.specification.fspiop.core.AmountType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface QuoteRepository
    extends JpaRepository<Quote, QuoteId>, JpaSpecificationExecutor<Quote> {

    class Filters {

        public static Specification<Quote> withAmountType(AmountType amountType) {

            return (root, query, cb) -> cb.equal(root.get("amountType"), amountType);
        }

        public static Specification<Quote> withRequestedAtRange(Instant from, Instant to) {

            return (root, query, cb) -> cb.between(root.get("requestedAt"), from, to);
        }

        public static Specification<Quote> withUdfQuoteId(UdfQuoteId udfQuoteId) {

            return (root, query, cb) -> cb.equal(root.get("udfQuoteId"), udfQuoteId);
        }

    }

}
