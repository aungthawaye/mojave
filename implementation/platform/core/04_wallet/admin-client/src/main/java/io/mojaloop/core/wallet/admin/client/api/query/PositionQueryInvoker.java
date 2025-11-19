package io.mojaloop.core.wallet.admin.client.api.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.admin.client.service.WalletAdminService;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.core.wallet.contract.query.PositionQuery;
import io.mojaloop.fspiop.spec.core.Currency;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PositionQueryInvoker implements PositionQuery {

    private final WalletAdminService.PositionQuery positionQuery;

    private final ObjectMapper objectMapper;

    public PositionQueryInvoker(final WalletAdminService.PositionQuery positionQuery, final ObjectMapper objectMapper) {

        assert positionQuery != null;
        assert objectMapper != null;

        this.positionQuery = positionQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public PositionData get(final PositionId positionId) {

        try {

            return RetrofitService.invoke(this.positionQuery.getByPositionId(positionId),
                    (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PositionData> get(final WalletOwnerId ownerId, final Currency currency) {

        try {

            return RetrofitService.invoke(this.positionQuery.getByOwnerIdAndCurrency(ownerId, currency),
                    (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PositionData> getAll() {

        try {

            return RetrofitService.invoke(this.positionQuery.getAll(),
                    (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
