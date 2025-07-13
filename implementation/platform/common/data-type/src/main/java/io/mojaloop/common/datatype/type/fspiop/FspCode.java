package io.mojaloop.common.datatype.type.fspiop;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record FspCode(@Column(name = "fsp_code") String fspCode) { }
