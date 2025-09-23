/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.fspiop.common.error;

import java.util.HashMap;
import java.util.Map;

public class FspiopErrors {

    //@@formatter:off
    public static final ErrorDefinition COMMUNICATION_ERROR = new ErrorDefinition(FspiopErrorType.COMMUNICATION_ERROR, "Generic communication error.");
    public static final ErrorDefinition DESTINATION_COMMUNICATION_ERROR = new ErrorDefinition(FspiopErrorType.DESTINATION_COMMUNICATION_ERROR, "Destination of the request failed to be reached. This usually indicates that a Peer FSP failed to respond from an intermediate entity.");

    public static final ErrorDefinition GENERIC_SERVER_ERROR = new ErrorDefinition(FspiopErrorType.GENERIC_SERVER_ERROR, "Used to avoid disclosing private details.");
    public static final ErrorDefinition INTERNAL_SERVER_ERROR = new ErrorDefinition(FspiopErrorType.INTERNAL_SERVER_ERROR, "Unexpected exception or bug.");
    public static final ErrorDefinition NOT_IMPLEMENTED = new ErrorDefinition(FspiopErrorType.NOT_IMPLEMENTED, "Service not supported by server.");
    public static final ErrorDefinition SERVICE_CURRENTLY_UNAVAILABLE = new ErrorDefinition(FspiopErrorType.SERVICE_CURRENTLY_UNAVAILABLE, "Service temporarily unavailable (e.g. maintenance).");
    public static final ErrorDefinition SERVER_TIMED_OUT = new ErrorDefinition(FspiopErrorType.SERVER_TIMED_OUT, "Callback not received within timeout period.");
    public static final ErrorDefinition SERVER_BUSY = new ErrorDefinition(FspiopErrorType.SERVER_BUSY, "Server rejecting requests due to overload.");

    public static final ErrorDefinition GENERIC_CLIENT_ERROR = new ErrorDefinition(FspiopErrorType.GENERIC_CLIENT_ERROR, "Used to avoid disclosing private client errors.");
    public static final ErrorDefinition UNACCEPTABLE_VERSION_REQUESTED = new ErrorDefinition(FspiopErrorType.UNACCEPTABLE_VERSION_REQUESTED, "Client requested unsupported API version.");
    public static final ErrorDefinition UNKNOWN_URI = new ErrorDefinition(FspiopErrorType.UNKNOWN_URI, "Provided URI not recognized by server.");
    public static final ErrorDefinition ADD_PARTY_INFORMATION_ERROR = new ErrorDefinition(FspiopErrorType.ADD_PARTY_INFORMATION_ERROR, "Error occurred while adding/updating party info.");

    public static final ErrorDefinition GENERIC_VALIDATION_ERROR = new ErrorDefinition(FspiopErrorType.GENERIC_VALIDATION_ERROR, "Generic format error.");
    public static final ErrorDefinition MALFORMED_SYNTAX = new ErrorDefinition(FspiopErrorType.MALFORMED_SYNTAX, "Invalid parameter format (e.g. amount '5.ABC').");
    public static final ErrorDefinition MISSING_MANDATORY_ELEMENT = new ErrorDefinition(FspiopErrorType.MISSING_MANDATORY_ELEMENT, "Required element in data model is missing.");
    public static final ErrorDefinition TOO_MANY_ELEMENTS = new ErrorDefinition(FspiopErrorType.TOO_MANY_ELEMENTS, "Array exceeds allowed maximum.");
    public static final ErrorDefinition TOO_LARGE_PAYLOAD = new ErrorDefinition(FspiopErrorType.TOO_LARGE_PAYLOAD, "Request payload exceeds allowed size.");
    public static final ErrorDefinition INVALID_SIGNATURE = new ErrorDefinition(FspiopErrorType.INVALID_SIGNATURE, "Payload signature invalid or modified.");
    public static final ErrorDefinition MODIFIED_REQUEST = new ErrorDefinition(FspiopErrorType.MODIFIED_REQUEST, "Parameters differ from a previously processed request.");
    public static final ErrorDefinition MISSING_EXTENSION_PARAMETER = new ErrorDefinition(FspiopErrorType.MISSING_EXTENSION_PARAMETER, "Required extension parameter not provided.");

    public static final ErrorDefinition GENERIC_ID_NOT_FOUND = new ErrorDefinition(FspiopErrorType.GENERIC_ID_NOT_FOUND, "Generic identifier error.");
    public static final ErrorDefinition DESTINATION_FSP_ERROR = new ErrorDefinition(FspiopErrorType.DESTINATION_FSP_ERROR, "Destination FSP cannot be found or doesn't exist.");
    public static final ErrorDefinition PAYER_FSP_ID_NOT_FOUND = new ErrorDefinition(FspiopErrorType.PAYER_FSP_ID_NOT_FOUND, "Payer FSP ID not recognized.");
    public static final ErrorDefinition PAYEE_FSP_ID_NOT_FOUND = new ErrorDefinition(FspiopErrorType.PAYEE_FSP_ID_NOT_FOUND, "Payee FSP ID not recognized.");
    public static final ErrorDefinition PARTY_NOT_FOUND = new ErrorDefinition(FspiopErrorType.PARTY_NOT_FOUND, "Provided party identifier not found.");
    public static final ErrorDefinition QUOTE_ID_NOT_FOUND = new ErrorDefinition(FspiopErrorType.QUOTE_ID_NOT_FOUND, "Quote ID not found on server.");
    public static final ErrorDefinition TRANSACTION_REQUEST_ID_NOT_FOUND = new ErrorDefinition(FspiopErrorType.TRANSACTION_REQUEST_ID_NOT_FOUND, "ID not found for transaction request.");
    public static final ErrorDefinition TRANSACTION_ID_NOT_FOUND = new ErrorDefinition(FspiopErrorType.TRANSACTION_ID_NOT_FOUND, "Transaction ID not found.");
    public static final ErrorDefinition TRANSFER_ID_NOT_FOUND = new ErrorDefinition(FspiopErrorType.TRANSFER_ID_NOT_FOUND, "Transfer ID not found.");
    public static final ErrorDefinition BULK_QUOTE_ID_NOT_FOUND = new ErrorDefinition(FspiopErrorType.BULK_QUOTE_ID_NOT_FOUND, "Bulk quote ID not found.");
    public static final ErrorDefinition BULK_TRANSFER_ID_NOT_FOUND = new ErrorDefinition(FspiopErrorType.BULK_TRANSFER_ID_NOT_FOUND, "Bulk transfer ID not found.");

    public static final ErrorDefinition GENERIC_EXPIRED_ERROR = new ErrorDefinition(FspiopErrorType.GENERIC_EXPIRED_ERROR, "Generic expired object error (non‑specific).");
    public static final ErrorDefinition TRANSACTION_REQUEST_EXPIRED = new ErrorDefinition(FspiopErrorType.TRANSACTION_REQUEST_EXPIRED, "Transaction request has already expired.");
    public static final ErrorDefinition QUOTE_EXPIRED = new ErrorDefinition(FspiopErrorType.QUOTE_EXPIRED, "The quote is no longer valid.");
    public static final ErrorDefinition TRANSFER_EXPIRED = new ErrorDefinition(FspiopErrorType.TRANSFER_EXPIRED, "The transfer has already expired.");

    public static final ErrorDefinition GENERIC_PAYER_ERROR = new ErrorDefinition(FspiopErrorType.GENERIC_PAYER_ERROR, "Used for private payer-related errors.");
    public static final ErrorDefinition PAYER_FSP_INSUFFICIENT_LIQUIDITY = new ErrorDefinition(FspiopErrorType.PAYER_FSP_INSUFFICIENT_LIQUIDITY, "Payer’s FSP lacks funds.");
    public static final ErrorDefinition GENERIC_PAYER_REJECTION = new ErrorDefinition(FspiopErrorType.GENERIC_PAYER_REJECTION, "Payer or payer FSP rejected the request.");
    public static final ErrorDefinition PAYER_REJECTED_TRANSACTION_REQUEST = new ErrorDefinition(FspiopErrorType.PAYER_REJECTED_TRANSACTION_REQUEST, "Payer rejected the transaction request.");
    public static final ErrorDefinition PAYER_FSP_UNSUPPORTED_TRANSACTION_TYPE = new ErrorDefinition(FspiopErrorType.PAYER_FSP_UNSUPPORTED_TRANSACTION_TYPE, "Transaction type not supported by Payer FSP.");
    public static final ErrorDefinition PAYER_UNSUPPORTED_CURRENCY = new ErrorDefinition(FspiopErrorType.PAYER_UNSUPPORTED_CURRENCY, "Payer does not support requested currency.");
    public static final ErrorDefinition PAYER_LIMIT_ERROR = new ErrorDefinition(FspiopErrorType.PAYER_LIMIT_ERROR, "Payment amount/frequency exceeds limits.");
    public static final ErrorDefinition PAYER_PERMISSION_ERROR = new ErrorDefinition(FspiopErrorType.PAYER_PERMISSION_ERROR, "Payer lacks permission to perform operation.");
    public static final ErrorDefinition GENERIC_PAYER_BLOCKED_ERROR = new ErrorDefinition(FspiopErrorType.GENERIC_PAYER_BLOCKED_ERROR, "Payer is blocked or failed regulatory screening.");

    public static final ErrorDefinition GENERIC_PAYEE_ERROR = new ErrorDefinition(FspiopErrorType.GENERIC_PAYEE_ERROR, "Generic error related to payee or payee FSP.");
    public static final ErrorDefinition PAYEE_FSP_INSUFFICIENT_LIQUIDITY = new ErrorDefinition(FspiopErrorType.PAYEE_FSP_INSUFFICIENT_LIQUIDITY, "Payee FSP lacks sufficient liquidity.");
    public static final ErrorDefinition GENERIC_PAYEE_REJECTION = new ErrorDefinition(FspiopErrorType.GENERIC_PAYEE_REJECTION, "Payee or payee FSP rejected request.");
    public static final ErrorDefinition PAYEE_REJECTED_QUOTE = new ErrorDefinition(FspiopErrorType.PAYEE_REJECTED_QUOTE, "Payee unwilling to proceed after quote.");
    public static final ErrorDefinition PAYEE_FSP_UNSUPPORTED_TRANSACTION_TYPE = new ErrorDefinition(FspiopErrorType.PAYEE_FSP_UNSUPPORTED_TRANSACTION_TYPE, "Payee FSP rejects unsupported transaction type.");
    public static final ErrorDefinition PAYEE_FSP_REJECTED_QUOTE = new ErrorDefinition(FspiopErrorType.PAYEE_FSP_REJECTED_QUOTE, "Payee FSP unwilling to continue after quote.");
    public static final ErrorDefinition PAYEE_REJECTED_TRANSACTION = new ErrorDefinition(FspiopErrorType.PAYEE_REJECTED_TRANSACTION, "Payee rejected the transaction.");
    public static final ErrorDefinition PAYEE_FSP_REJECTED_TRANSACTION = new ErrorDefinition(FspiopErrorType.PAYEE_FSP_REJECTED_TRANSACTION, "Payee FSP rejected the transaction.");
    public static final ErrorDefinition PAYEE_UNSUPPORTED_CURRENCY = new ErrorDefinition(FspiopErrorType.PAYEE_UNSUPPORTED_CURRENCY, "Payee does not support requested currency.");
    public static final ErrorDefinition PAYEE_LIMIT_ERROR = new ErrorDefinition(FspiopErrorType.PAYEE_LIMIT_ERROR, "Receiving amount/frequency exceeds allowed limits.");
    public static final ErrorDefinition PAYEE_PERMISSION_ERROR = new ErrorDefinition(FspiopErrorType.PAYEE_PERMISSION_ERROR, "Payee lacks permission to perform operation.");
    public static final ErrorDefinition GENERIC_PAYEE_BLOCKED_ERROR = new ErrorDefinition(FspiopErrorType.GENERIC_PAYEE_BLOCKED_ERROR, "Payee is blocked or failed regulatory screening.");
    //@@formatter:on

    private static final Map<String, ErrorDefinition> ERROR_MAP = new HashMap<>();

    static {

        ERROR_MAP.put(COMMUNICATION_ERROR.errorType().getCode(), COMMUNICATION_ERROR);
        ERROR_MAP.put(DESTINATION_COMMUNICATION_ERROR.errorType().getCode(), DESTINATION_COMMUNICATION_ERROR);

        ERROR_MAP.put(GENERIC_SERVER_ERROR.errorType().getCode(), GENERIC_SERVER_ERROR);
        ERROR_MAP.put(INTERNAL_SERVER_ERROR.errorType().getCode(), INTERNAL_SERVER_ERROR);
        ERROR_MAP.put(NOT_IMPLEMENTED.errorType().getCode(), NOT_IMPLEMENTED);
        ERROR_MAP.put(SERVICE_CURRENTLY_UNAVAILABLE.errorType().getCode(), SERVICE_CURRENTLY_UNAVAILABLE);
        ERROR_MAP.put(SERVER_TIMED_OUT.errorType().getCode(), SERVER_TIMED_OUT);
        ERROR_MAP.put(SERVER_BUSY.errorType().getCode(), SERVER_BUSY);

        ERROR_MAP.put(GENERIC_CLIENT_ERROR.errorType().getCode(), GENERIC_CLIENT_ERROR);
        ERROR_MAP.put(UNACCEPTABLE_VERSION_REQUESTED.errorType().getCode(), UNACCEPTABLE_VERSION_REQUESTED);
        ERROR_MAP.put(UNKNOWN_URI.errorType().getCode(), UNKNOWN_URI);
        ERROR_MAP.put(ADD_PARTY_INFORMATION_ERROR.errorType().getCode(), ADD_PARTY_INFORMATION_ERROR);

        ERROR_MAP.put(GENERIC_VALIDATION_ERROR.errorType().getCode(), GENERIC_VALIDATION_ERROR);
        ERROR_MAP.put(MALFORMED_SYNTAX.errorType().getCode(), MALFORMED_SYNTAX);
        ERROR_MAP.put(MISSING_MANDATORY_ELEMENT.errorType().getCode(), MISSING_MANDATORY_ELEMENT);
        ERROR_MAP.put(TOO_MANY_ELEMENTS.errorType().getCode(), TOO_MANY_ELEMENTS);
        ERROR_MAP.put(TOO_LARGE_PAYLOAD.errorType().getCode(), TOO_LARGE_PAYLOAD);
        ERROR_MAP.put(INVALID_SIGNATURE.errorType().getCode(), INVALID_SIGNATURE);
        ERROR_MAP.put(MODIFIED_REQUEST.errorType().getCode(), MODIFIED_REQUEST);
        ERROR_MAP.put(MISSING_EXTENSION_PARAMETER.errorType().getCode(), MISSING_EXTENSION_PARAMETER);

        ERROR_MAP.put(GENERIC_ID_NOT_FOUND.errorType().getCode(), GENERIC_ID_NOT_FOUND);
        ERROR_MAP.put(DESTINATION_FSP_ERROR.errorType().getCode(), DESTINATION_FSP_ERROR);
        ERROR_MAP.put(PAYER_FSP_ID_NOT_FOUND.errorType().getCode(), PAYER_FSP_ID_NOT_FOUND);
        ERROR_MAP.put(PAYEE_FSP_ID_NOT_FOUND.errorType().getCode(), PAYEE_FSP_ID_NOT_FOUND);
        ERROR_MAP.put(PARTY_NOT_FOUND.errorType().getCode(), PARTY_NOT_FOUND);
        ERROR_MAP.put(QUOTE_ID_NOT_FOUND.errorType().getCode(), QUOTE_ID_NOT_FOUND);
        ERROR_MAP.put(TRANSACTION_REQUEST_ID_NOT_FOUND.errorType().getCode(), TRANSACTION_REQUEST_ID_NOT_FOUND);
        ERROR_MAP.put(TRANSACTION_ID_NOT_FOUND.errorType().getCode(), TRANSACTION_ID_NOT_FOUND);
        ERROR_MAP.put(TRANSFER_ID_NOT_FOUND.errorType().getCode(), TRANSFER_ID_NOT_FOUND);
        ERROR_MAP.put(BULK_QUOTE_ID_NOT_FOUND.errorType().getCode(), BULK_QUOTE_ID_NOT_FOUND);
        ERROR_MAP.put(BULK_TRANSFER_ID_NOT_FOUND.errorType().getCode(), BULK_TRANSFER_ID_NOT_FOUND);

        ERROR_MAP.put(GENERIC_EXPIRED_ERROR.errorType().getCode(), GENERIC_EXPIRED_ERROR);
        ERROR_MAP.put(TRANSACTION_REQUEST_EXPIRED.errorType().getCode(), TRANSACTION_REQUEST_EXPIRED);
        ERROR_MAP.put(QUOTE_EXPIRED.errorType().getCode(), QUOTE_EXPIRED);
        ERROR_MAP.put(TRANSFER_EXPIRED.errorType().getCode(), TRANSFER_EXPIRED);

        ERROR_MAP.put(GENERIC_PAYER_ERROR.errorType().getCode(), GENERIC_PAYER_ERROR);
        ERROR_MAP.put(PAYER_FSP_INSUFFICIENT_LIQUIDITY.errorType().getCode(), PAYER_FSP_INSUFFICIENT_LIQUIDITY);
        ERROR_MAP.put(GENERIC_PAYER_REJECTION.errorType().getCode(), GENERIC_PAYER_REJECTION);
        ERROR_MAP.put(PAYER_REJECTED_TRANSACTION_REQUEST.errorType().getCode(), PAYER_REJECTED_TRANSACTION_REQUEST);
        ERROR_MAP.put(PAYER_FSP_UNSUPPORTED_TRANSACTION_TYPE.errorType().getCode(), PAYER_FSP_UNSUPPORTED_TRANSACTION_TYPE);
        ERROR_MAP.put(PAYER_UNSUPPORTED_CURRENCY.errorType().getCode(), PAYER_UNSUPPORTED_CURRENCY);
        ERROR_MAP.put(PAYER_LIMIT_ERROR.errorType().getCode(), PAYER_LIMIT_ERROR);
        ERROR_MAP.put(PAYER_PERMISSION_ERROR.errorType().getCode(), PAYER_PERMISSION_ERROR);
        ERROR_MAP.put(GENERIC_PAYER_BLOCKED_ERROR.errorType().getCode(), GENERIC_PAYER_BLOCKED_ERROR);

        ERROR_MAP.put(GENERIC_PAYEE_ERROR.errorType().getCode(), GENERIC_PAYEE_ERROR);
        ERROR_MAP.put(PAYEE_FSP_INSUFFICIENT_LIQUIDITY.errorType().getCode(), PAYEE_FSP_INSUFFICIENT_LIQUIDITY);
        ERROR_MAP.put(GENERIC_PAYEE_REJECTION.errorType().getCode(), GENERIC_PAYEE_REJECTION);
        ERROR_MAP.put(PAYEE_REJECTED_QUOTE.errorType().getCode(), PAYEE_REJECTED_QUOTE);
        ERROR_MAP.put(PAYEE_FSP_UNSUPPORTED_TRANSACTION_TYPE.errorType().getCode(), PAYEE_FSP_UNSUPPORTED_TRANSACTION_TYPE);
        ERROR_MAP.put(PAYEE_FSP_REJECTED_QUOTE.errorType().getCode(), PAYEE_FSP_REJECTED_QUOTE);
        ERROR_MAP.put(PAYEE_REJECTED_TRANSACTION.errorType().getCode(), PAYEE_REJECTED_TRANSACTION);
        ERROR_MAP.put(PAYEE_FSP_REJECTED_TRANSACTION.errorType().getCode(), PAYEE_FSP_REJECTED_TRANSACTION);
        ERROR_MAP.put(PAYEE_UNSUPPORTED_CURRENCY.errorType().getCode(), PAYEE_UNSUPPORTED_CURRENCY);
        ERROR_MAP.put(PAYEE_LIMIT_ERROR.errorType().getCode(), PAYEE_LIMIT_ERROR);
        ERROR_MAP.put(PAYEE_PERMISSION_ERROR.errorType().getCode(), PAYEE_PERMISSION_ERROR);
        ERROR_MAP.put(GENERIC_PAYEE_BLOCKED_ERROR.errorType().getCode(), GENERIC_PAYEE_BLOCKED_ERROR);
    }

    public static ErrorDefinition find(String code) {

        return ERROR_MAP.get(code);
    }

}
