package io.mojaloop.core.wallet.intercom.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.core.wallet.intercom.client.exception.WalletIntercomClientException;
import io.mojaloop.core.wallet.intercom.client.service.WalletIntercomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetWallets {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetWallets.class);

    private final WalletIntercomService walletIntercomService;

    private final ObjectMapper objectMapper;

    public GetWallets(final WalletIntercomService walletIntercomService, final ObjectMapper objectMapper) {

        assert walletIntercomService != null;
        assert objectMapper != null;

        this.walletIntercomService = walletIntercomService;
        this.objectMapper = objectMapper;
    }

    public List<WalletData> execute() throws WalletIntercomClientException {

        try {

            return RetrofitService.invoke(this.walletIntercomService.getWallets(), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                                  .body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking getWallets : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new WalletIntercomClientException(code, message);
            }

            throw new WalletIntercomClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

}
