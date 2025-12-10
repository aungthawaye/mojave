package org.mojave.core.transfer.domain.repository;

import org.mojave.core.common.datatype.identifier.transfer.TransferId;
import org.mojave.core.transfer.domain.model.TransferIlpPacket;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferIlpPacketRepository extends JpaRepository<TransferIlpPacket, TransferId>,
                                                     JpaSpecificationExecutor<TransferIlpPacket> {

    class Filters {

        public static Specification<TransferIlpPacket> withIlpCondition(String ilpCondition) {

            return (root, query, cb) -> cb.equal(root.get("condition"), ilpCondition);
        }

    }

}
