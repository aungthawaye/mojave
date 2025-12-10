package org.mojave.core.accounting.contract.command.account;

import org.mojave.core.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.core.common.datatype.identifier.accounting.AccountId;
import org.mojave.core.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.fspiop.spec.core.Currency;

import java.util.List;

public interface CreateAccountInBulkCommand {

    Output execute(Input input);

    record Input(AccountOwnerId ownerId,
                 FspCode fspCode,
                 Currency currency,
                 ChartEntryCategory category) { }

    record Output(List<AccountId> accountIds) { }

}
