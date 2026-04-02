package org.mojave.core.accounting.domain.command.account;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.core.accounting.domain.AccountingDomainConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AccountingDomainConfiguration.class})
public class ActivateAccountCommandIT { }
