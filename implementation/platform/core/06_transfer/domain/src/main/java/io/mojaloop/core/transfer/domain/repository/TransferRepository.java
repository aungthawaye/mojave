package io.mojaloop.core.transfer.domain.repository;

import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.transfer.domain.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, TransferId> { }
