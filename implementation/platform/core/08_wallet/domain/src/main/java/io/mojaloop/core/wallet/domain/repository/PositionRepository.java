package io.mojaloop.core.wallet.domain.repository;

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.wallet.domain.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, PositionId>, JpaSpecificationExecutor<Position> { }
