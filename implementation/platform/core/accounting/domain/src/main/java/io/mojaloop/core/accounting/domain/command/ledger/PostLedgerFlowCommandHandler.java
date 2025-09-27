package io.mojaloop.core.accounting.domain.command.ledger;

import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.PostingAccountFoundException;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.component.ledger.Ledger;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.LedgerMovementId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class PostLedgerFlowCommandHandler implements PostLedgerFlowCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostLedgerFlowCommandHandler.class);

    private final AccountCache accountCache;

    private final Ledger ledger;

    public PostLedgerFlowCommandHandler(AccountCache accountCache, Ledger ledger) {

        assert accountCache != null;
        assert ledger != null;

        this.accountCache = accountCache;
        this.ledger = ledger;
    }

    @Override
    public Output execute(Input input) throws PostingAccountFoundException, InsufficientBalanceInAccountException {

        var requests = new ArrayList<Ledger.Request>();
        var accounts = new HashMap<AccountId, AccountData>();

        try {

            if (input.postings() == null || input.postings().isEmpty()) {
                return new Output(new ArrayList<>());
            }

            input.postings().forEach(posting -> {

                LOGGER.info("Processing posting: {}", posting);

                var accountData = this.accountCache.get(posting.chartEntryId(), posting.ownerId(), posting.currency());

                if (accountData == null) {

                    try {

                        throw new PostingAccountFoundException(posting.ownerId(), posting.chartEntryId(), posting.currency());

                    } catch (PostingAccountFoundException e) {

                        throw new RuntimeException(e);
                    }
                }

                accounts.put(accountData.accountId(), accountData);

                var request = new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()), accountData.accountId(), posting.side(), posting.currency(), posting.amount());

                requests.add(request);

            });

            LOGGER.info("Requests: {}", requests);

            var movements = this.ledger.post(requests, input.transactionId(), input.transactionAt(), input.transactionType());

            var flows = new ArrayList<Output.Flow>();

            movements.forEach(movement -> {

                var accountData = accounts.get(movement.accountId());

                flows.add(new Output.Flow(movement.accountId(),
                                          accountData.ownerId(),
                                          accountData.chartEntryId(),
                                          movement.ledgerMovementId(),
                                          movement.side(),
                                          movement.amount(),
                                          new Output.DrCr(movement.oldDrCr().debits(), movement.oldDrCr().credits()),
                                          new Output.DrCr(movement.oldDrCr().debits(), movement.oldDrCr().credits()),
                                          movement.movementStage(),
                                          movement.movementResult()));
            });

            return new Output(flows);

        } catch (RuntimeException e) {

            if (e.getCause() instanceof PostingAccountFoundException e1) {

                throw e1;
            }

            throw e;

        } catch (Ledger.InsufficientBalanceException e) {

            var accountData = this.accountCache.get(e.getAccountId());

            throw new InsufficientBalanceInAccountException(accountData.code(), e.getSide(), e.getAmount(), e.getDrCr().debits(), e.getDrCr().credits());

        } catch (Ledger.OverdraftExceededException e) {
            throw new RuntimeException(e);
        } catch (Ledger.RestoreFailedException e) {
            throw new RuntimeException(e);
        }

    }

}
