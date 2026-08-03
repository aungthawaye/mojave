package org.mojave.core.settlement.contract.data;

import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.SettlementWindowStatus;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;

import java.time.Instant;

public record SettlementWindowData(SettlementWindowId settlementWindowId,
                                   SettlementModelId settlementModelId,
                                   Currency currency,
                                   FspGroupId fspGroupId,
                                   Instant periodStartAt,
                                   Instant periodEndAt,
                                   SettlementWindowStatus status) { }
