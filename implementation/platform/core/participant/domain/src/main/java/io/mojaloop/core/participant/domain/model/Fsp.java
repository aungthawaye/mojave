package io.mojaloop.core.participant.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pcp_fsp")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fsp { }
