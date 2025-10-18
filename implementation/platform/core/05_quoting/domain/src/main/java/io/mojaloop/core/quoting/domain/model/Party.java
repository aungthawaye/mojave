package io.mojaloop.core.quoting.domain.model;

import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record Party(@Column(name = "party_id_type", nullable = false) @Enumerated(EnumType.STRING) PartyIdType partyIdType,
                    @Column(name = "party_id", nullable = false) String partyId,
                    @Column(name = "sub_id") String subId) { }
