package org.mojave.core.participant.intercom.client.api.query;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.common.datatype.identifier.participant.SspId;
import org.mojave.core.common.datatype.type.participant.SspCode;
import org.mojave.core.participant.contract.data.SspData;
import org.mojave.core.participant.contract.exception.ParticipantExceptionResolver;
import org.mojave.core.participant.contract.query.SspQuery;
import org.mojave.core.participant.intercom.client.service.ParticipantIntercomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class SspQueryInvoker implements SspQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(SspQueryInvoker.class);

    private final ParticipantIntercomService.SspQuery sspQuery;

    private final ObjectMapper objectMapper;

    public SspQueryInvoker(final ParticipantIntercomService.SspQuery sspQuery,
                           final ObjectMapper objectMapper) {

        assert sspQuery != null;
        assert objectMapper != null;

        this.sspQuery = sspQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public SspData get(final SspId sspId) {

        try {

            return RetrofitService.invoke(
                this.sspQuery.getBySspId(sspId.getId().toString()),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                var throwable = ParticipantExceptionResolver.resolve(errorResponse);

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public SspData get(final SspCode sspCode) {

        try {

            return RetrofitService.invoke(
                this.sspQuery.getBySspCode(sspCode.value()),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                var throwable = ParticipantExceptionResolver.resolve(errorResponse);

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SspData> getAll() {

        try {

            return RetrofitService.invoke(
                this.sspQuery.getAllSsps(),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                var throwable = ParticipantExceptionResolver.resolve(errorResponse);

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

}
