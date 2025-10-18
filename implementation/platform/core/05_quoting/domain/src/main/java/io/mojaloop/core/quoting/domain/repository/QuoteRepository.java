package io.mojaloop.core.quoting.domain.repository;

import io.mojaloop.core.common.datatype.identifier.quoting.QuoteId;
import io.mojaloop.core.common.datatype.identifier.quoting.UdfQuoteId;
import io.mojaloop.core.quoting.domain.model.Quote;
import io.mojaloop.fspiop.spec.core.AmountType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, QuoteId>, JpaSpecificationExecutor<Quote> {

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
