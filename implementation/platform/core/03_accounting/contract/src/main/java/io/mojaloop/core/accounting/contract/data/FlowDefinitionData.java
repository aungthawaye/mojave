package io.mojaloop.core.accounting.contract.data;

import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.TerminationStatus;
import io.mojaloop.core.common.datatype.enums.accounting.AccountResolving;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import io.mojaloop.core.common.datatype.identifier.accounting.PostingDefinitionId;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.List;
import java.util.Objects;

public record FlowDefinitionData(FlowDefinitionId flowDefinitionId,
                                 TransactionType transactionType,
                                 Currency currency,
                                 String name,
                                 String description,
                                 ActivationStatus activationStatus,
                                 TerminationStatus terminationStatus,
                                 List<Posting> postings) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof FlowDefinitionData that)) {
            return false;
        }
        return Objects.equals(flowDefinitionId, that.flowDefinitionId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(flowDefinitionId);
    }

    public record Posting(PostingDefinitionId postingDefinitionId,
                          String participantType,
                          String amountName,
                          Side side,
                          AccountResolving selection,
                          Long accountOrChartEntryId,
                          String description) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof Posting that)) {
                return false;
            }
            return Objects.equals(postingDefinitionId, that.postingDefinitionId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(postingDefinitionId);
        }

    }

}

