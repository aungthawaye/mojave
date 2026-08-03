package org.mojave.core.settlement.contract.data;

import org.mojave.scheme.rule.enums.ActivationStatus;
import org.mojave.scheme.rule.enums.TerminationStatus;
import org.mojave.scheme.rule.enums.settlement.SettlementMethod;
import org.mojave.scheme.rule.identifier.participant.SspId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;

public record SettlementModelData(SettlementModelId settlementModelId,
                                  String name,
                                  SettlementMethod settlementMethod,
                                  SspId sspId,
                                  ActivationStatus activationStatus,
                                  TerminationStatus terminationStatus) { }
