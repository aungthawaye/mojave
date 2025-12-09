package io.mojaloop.core.wallet.domain.repository;

import io.mojaloop.core.common.datatype.identifier.wallet.NetDebitCapUpdateId;
import io.mojaloop.core.wallet.domain.model.NdcUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NdcUpdateRepository
    extends JpaRepository<NdcUpdate, NetDebitCapUpdateId>, JpaSpecificationExecutor<NdcUpdate> { }
