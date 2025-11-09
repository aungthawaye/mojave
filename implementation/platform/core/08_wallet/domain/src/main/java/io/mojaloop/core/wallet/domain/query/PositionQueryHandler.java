package io.mojaloop.core.wallet.domain.query;

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.core.wallet.contract.query.PositionQuery;
import io.mojaloop.core.wallet.domain.model.Position;
import io.mojaloop.core.wallet.domain.repository.PositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PositionQueryHandler implements PositionQuery {

    private final PositionRepository positionRepository;

    public PositionQueryHandler(final PositionRepository positionRepository) {

        assert positionRepository != null;

        this.positionRepository = positionRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public PositionData get(final PositionId positionId) {

        // NOTE: PositionIdNotFoundException currently expects WalletId; using IllegalArgumentException until fixed.
        return this.positionRepository.findById(positionId).orElseThrow(() -> new IllegalArgumentException("Position not found: " + positionId)).convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<PositionData> getAll() {

        return this.positionRepository.findAll().stream().map(Position::convert).toList();
    }

}
