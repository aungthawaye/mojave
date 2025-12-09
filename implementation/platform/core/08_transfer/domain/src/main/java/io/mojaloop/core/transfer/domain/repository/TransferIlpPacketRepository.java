package io.mojaloop.core.transfer.domain.repository;

import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.transfer.domain.model.TransferIlpPacket;
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
