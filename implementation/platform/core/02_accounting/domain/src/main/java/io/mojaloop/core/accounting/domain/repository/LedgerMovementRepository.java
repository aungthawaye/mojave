package io.mojaloop.core.accounting.domain.repository;

import io.mojaloop.core.accounting.domain.model.LedgerMovement;
import io.mojaloop.core.common.datatype.identifier.accounting.LedgerMovementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerMovementRepository extends JpaRepository<LedgerMovement, LedgerMovementId>, JpaSpecificationExecutor<LedgerMovement> { }
