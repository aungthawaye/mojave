package org.mojave.core.settlement.domain.model;

import org.mojave.core.common.datatype.enums.ActivationStatus;
import org.mojave.core.common.datatype.identifier.participant.FspId;
import org.mojave.core.common.datatype.identifier.participant.SspId;
import org.mojave.core.common.datatype.identifier.settlement.SettlementDefinitionId;
import org.mojave.fspiop.spec.core.Currency;

public class SettlementDefinition {

    protected SettlementDefinitionId id;

    protected String name;

    protected FspId payerFspId;

    protected FspId payeeFspId;

    protected Currency currency;

    protected Integer startFrom;

    protected Integer endBefore;

    protected SspId settlementProviderId;

    protected ActivationStatus activationStatus;



}
