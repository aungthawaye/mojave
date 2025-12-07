package io.mojaloop.core.accounting.contract.command.account;

import io.mojaloop.core.common.datatype.enums.accounting.ChartEntryCategory;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.List;

public interface CreateAccountInBulkCommand {

    Output execute(Input input);

    record Input(AccountOwnerId ownerId,
                 FspCode fspCode,
                 Currency currency,
                 ChartEntryCategory category) { }

    record Output(List<AccountId> accountIds) { }

}
