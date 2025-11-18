package io.mojaloop.core.participant.admin.client.api.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.admin.client.service.ParticipantAdminService;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.contract.exception.ParticipantExceptionResolver;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import io.mojaloop.core.participant.contract.exception.oracle.OracleTypeNotFoundException;
import io.mojaloop.core.participant.contract.query.OracleQuery;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OracleQueryInvoker implements OracleQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryInvoker.class);

    private final ParticipantAdminService.OracleQuery oracleQuery;

    private final ObjectMapper objectMapper;

    public OracleQueryInvoker(final ParticipantAdminService.OracleQuery oracleQuery, final ObjectMapper objectMapper) {

        assert oracleQuery != null;
        assert objectMapper != null;

        this.oracleQuery = oracleQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<OracleData> find(final PartyIdType type) {

        try {

            var data = RetrofitService.invoke(this.oracleQuery.getByPartyIdType(type.name()), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                                      .body();
            return Optional.ofNullable(data);

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                var throwable = ParticipantExceptionResolver.resolve(errorResponse);

                if (throwable instanceof OracleTypeNotFoundException) {
                    return Optional.empty();
                }

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public OracleData get(final PartyIdType type) {

        try {

            return RetrofitService.invoke(this.oracleQuery.getByPartyIdType(type.name()), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                                  .body();

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
    public OracleData get(final OracleId oracleId) {

        try {

            return RetrofitService.invoke(this.oracleQuery.getByOracleId(oracleId.getId().toString()), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                                  .body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                var throwable = ParticipantExceptionResolver.resolve(errorResponse);

                if (throwable instanceof OracleIdNotFoundException) {
                    // fall through to general path which will rethrow
                }

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OracleData> getAll() {

        try {

            return RetrofitService.invoke(this.oracleQuery.getAllOracles(), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                                  .body();

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
