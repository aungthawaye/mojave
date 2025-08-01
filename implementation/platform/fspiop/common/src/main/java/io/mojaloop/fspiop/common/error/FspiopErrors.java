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
    public static final ErrorDefinition COMMUNICATION_ERROR = new ErrorDefinition("1000", "Communication error", "Generic communication error.");
    public static final ErrorDefinition DESTINATION_COMMUNICATION_ERROR = new ErrorDefinition("1001", "Destination communication error", "Destination of the request failed to be reached. This usually indicates that a Peer FSP failed to respond from an intermediate entity.");

    public static final ErrorDefinition GENERIC_SERVER_ERROR = new ErrorDefinition("2000", "Generic server error", "Used to avoid disclosing private details.");
    public static final ErrorDefinition INTERNAL_SERVER_ERROR = new ErrorDefinition("2001", "Internal server error", "Unexpected exception or bug.");
    public static final ErrorDefinition NOT_IMPLEMENTED = new ErrorDefinition("2002", "Not implemented", "Service not supported by server.");
    public static final ErrorDefinition SERVICE_CURRENTLY_UNAVAILABLE = new ErrorDefinition("2003", "Service currently unavailable", "Service temporarily unavailable (e.g. maintenance).");
    public static final ErrorDefinition SERVER_TIMED_OUT = new ErrorDefinition("2004", "Server timed out", "Callback not received within timeout period.");
    public static final ErrorDefinition SERVER_BUSY = new ErrorDefinition("2005", "Server busy", "Server rejecting requests due to overload.");

    public static final ErrorDefinition GENERIC_CLIENT_ERROR = new ErrorDefinition("3000", "Generic client error", "Used to avoid disclosing private client errors.");
    public static final ErrorDefinition UNACCEPTABLE_VERSION_REQUESTED = new ErrorDefinition("3001", "Unacceptable version requested", "Client requested unsupported API version.");
    public static final ErrorDefinition UNKNOWN_URI = new ErrorDefinition("3002", "Unknown URI", "Provided URI not recognized by server.");
    public static final ErrorDefinition ADD_PARTY_INFORMATION_ERROR = new ErrorDefinition("3003", "Add Party information error", "Error occurred while adding/updating party info.");

    public static final ErrorDefinition GENERIC_VALIDATION_ERROR = new ErrorDefinition("3100", "Generic validation error", "Generic format error.");
    public static final ErrorDefinition MALFORMED_SYNTAX = new ErrorDefinition("3101", "Malformed syntax", "Invalid parameter format (e.g. amount '5.ABC').");
    public static final ErrorDefinition MISSING_MANDATORY_ELEMENT = new ErrorDefinition("3102", "Missing mandatory element", "Required element in data model is missing.");
    public static final ErrorDefinition TOO_MANY_ELEMENTS = new ErrorDefinition("3103", "Too many elements", "Array exceeds allowed maximum.");
    public static final ErrorDefinition TOO_LARGE_PAYLOAD = new ErrorDefinition("3104", "Too large payload", "Request payload exceeds allowed size.");
    public static final ErrorDefinition INVALID_SIGNATURE = new ErrorDefinition("3105", "Invalid signature", "Payload signature invalid or modified.");
    public static final ErrorDefinition MODIFIED_REQUEST = new ErrorDefinition("3106", "Modified request", "Parameters differ from a previously processed request.");
    public static final ErrorDefinition MISSING_EXTENSION_PARAMETER = new ErrorDefinition("3107", "Missing extension parameter", "Required extension parameter not provided.");

    public static final ErrorDefinition GENERIC_ID_NOT_FOUND = new ErrorDefinition("3200", "Generic ID not found", "Generic identifier error.");
    public static final ErrorDefinition DESTINATION_FSP_ERROR = new ErrorDefinition("3201", "Destination FSP Error", "Destination FSP cannot be found or doesn't exist.");
    public static final ErrorDefinition PAYER_FSP_ID_NOT_FOUND = new ErrorDefinition("3202", "Payer FSP ID not found", "Payer FSP ID not recognized.");
    public static final ErrorDefinition PAYEE_FSP_ID_NOT_FOUND = new ErrorDefinition("3203", "Payee FSP ID not found", "Payee FSP ID not recognized.");
    public static final ErrorDefinition PARTY_NOT_FOUND = new ErrorDefinition("3204", "Party not found", "Provided party identifier not found.");
    public static final ErrorDefinition QUOTE_ID_NOT_FOUND = new ErrorDefinition("3205", "Quote ID not found", "Quote ID not found on server.");
    public static final ErrorDefinition TRANSACTION_REQUEST_ID_NOT_FOUND = new ErrorDefinition("3206", "Transaction request ID not found", "ID not found for transaction request.");
    public static final ErrorDefinition TRANSACTION_ID_NOT_FOUND = new ErrorDefinition("3207", "Transaction ID not found", "Transaction ID not found.");
    public static final ErrorDefinition TRANSFER_ID_NOT_FOUND = new ErrorDefinition("3208", "Transfer ID not found", "Transfer ID not found.");
    public static final ErrorDefinition BULK_QUOTE_ID_NOT_FOUND = new ErrorDefinition("3209", "Bulk quote ID not found", "Bulk quote ID not found.");
    public static final ErrorDefinition BULK_TRANSFER_ID_NOT_FOUND = new ErrorDefinition("3210", "Bulk transfer ID not found", "Bulk transfer ID not found.");

    public static final ErrorDefinition GENERIC_EXPIRED_ERROR = new ErrorDefinition("3300", "Generic expired error", "Generic expired object error (non‑specific).");
    public static final ErrorDefinition TRANSACTION_REQUEST_EXPIRED = new ErrorDefinition("3301", "Transaction request expired", "Transaction request has already expired.");
    public static final ErrorDefinition QUOTE_EXPIRED = new ErrorDefinition("3302", "Quote expired", "The quote is no longer valid.");
    public static final ErrorDefinition TRANSFER_EXPIRED = new ErrorDefinition("3303", "Transfer expired", "The transfer has already expired.");

    public static final ErrorDefinition GENERIC_PAYER_ERROR = new ErrorDefinition("4000", "Generic Payer error", "Used for private payer-related errors.");
    public static final ErrorDefinition PAYER_FSP_INSUFFICIENT_LIQUIDITY = new ErrorDefinition("4001", "Payer FSP insufficient liquidity", "Payer’s FSP lacks funds.");
    public static final ErrorDefinition GENERIC_PAYER_REJECTION = new ErrorDefinition("4100", "Generic Payer rejection", "Payer or payer FSP rejected the request.");
    public static final ErrorDefinition PAYER_REJECTED_TRANSACTION_REQUEST = new ErrorDefinition("4101", "Payer rejected transaction request", "Payer rejected the transaction request.");
    public static final ErrorDefinition PAYER_FSP_UNSUPPORTED_TRANSACTION_TYPE = new ErrorDefinition("4102", "Payer FSP unsupported transaction type", "Transaction type not supported by Payer FSP.");
    public static final ErrorDefinition PAYER_UNSUPPORTED_CURRENCY = new ErrorDefinition("4103", "Payer unsupported currency", "Payer does not support requested currency.");
    public static final ErrorDefinition PAYER_LIMIT_ERROR = new ErrorDefinition("4200", "Payer limit error", "Payment amount/frequency exceeds limits.");
    public static final ErrorDefinition PAYER_PERMISSION_ERROR = new ErrorDefinition("4300", "Payer permission error", "Payer lacks permission to perform operation.");
    public static final ErrorDefinition GENERIC_PAYER_BLOCKED_ERROR = new ErrorDefinition("4400", "Generic Payer blocked error", "Payer is blocked or failed regulatory screening.");

    public static final ErrorDefinition GENERIC_PAYEE_ERROR = new ErrorDefinition("5000", "Generic Payee error", "Generic error related to payee or payee FSP.");
    public static final ErrorDefinition PAYEE_FSP_INSUFFICIENT_LIQUIDITY = new ErrorDefinition("5001", "Payee FSP insufficient liquidity", "Payee FSP lacks sufficient liquidity.");
    public static final ErrorDefinition GENERIC_PAYEE_REJECTION = new ErrorDefinition("5100", "Generic Payee rejection", "Payee or payee FSP rejected request.");
    public static final ErrorDefinition PAYEE_REJECTED_QUOTE = new ErrorDefinition("5101", "Payee rejected quote", "Payee unwilling to proceed after quote.");
    public static final ErrorDefinition PAYEE_FSP_UNSUPPORTED_TRANSACTION_TYPE = new ErrorDefinition("5102", "Payee FSP unsupported transaction type", "Payee FSP rejects unsupported transaction type.");
    public static final ErrorDefinition PAYEE_FSP_REJECTED_QUOTE = new ErrorDefinition("5103", "Payee FSP rejected quote", "Payee FSP unwilling to continue after quote.");
    public static final ErrorDefinition PAYEE_REJECTED_TRANSACTION = new ErrorDefinition("5104", "Payee rejected transaction", "Payee rejected the transaction.");
    public static final ErrorDefinition PAYEE_FSP_REJECTED_TRANSACTION = new ErrorDefinition("5105", "Payee FSP rejected transaction", "Payee FSP rejected the transaction.");
    public static final ErrorDefinition PAYEE_UNSUPPORTED_CURRENCY = new ErrorDefinition("5106", "Payee unsupported currency", "Payee does not support requested currency.");
    public static final ErrorDefinition PAYEE_LIMIT_ERROR = new ErrorDefinition("5200", "Payee limit error", "Receiving amount/frequency exceeds allowed limits.");
    public static final ErrorDefinition PAYEE_PERMISSION_ERROR = new ErrorDefinition("5300", "Payee permission error", "Payee lacks permission to perform operation.");
    public static final ErrorDefinition GENERIC_PAYEE_BLOCKED_ERROR = new ErrorDefinition("5400", "Generic Payee blocked error", "Payee is blocked or failed regulatory screening.");
    //@@formatter:on

    private static final Map<String, ErrorDefinition> ERROR_MAP = new HashMap<>();

    static {

        ERROR_MAP.put(COMMUNICATION_ERROR.code(), COMMUNICATION_ERROR);
        ERROR_MAP.put(DESTINATION_COMMUNICATION_ERROR.code(), DESTINATION_COMMUNICATION_ERROR);

        ERROR_MAP.put(GENERIC_SERVER_ERROR.code(), GENERIC_SERVER_ERROR);
        ERROR_MAP.put(INTERNAL_SERVER_ERROR.code(), INTERNAL_SERVER_ERROR);
        ERROR_MAP.put(NOT_IMPLEMENTED.code(), NOT_IMPLEMENTED);
        ERROR_MAP.put(SERVICE_CURRENTLY_UNAVAILABLE.code(), SERVICE_CURRENTLY_UNAVAILABLE);
        ERROR_MAP.put(SERVER_TIMED_OUT.code(), SERVER_TIMED_OUT);
        ERROR_MAP.put(SERVER_BUSY.code(), SERVER_BUSY);

        ERROR_MAP.put(GENERIC_CLIENT_ERROR.code(), GENERIC_CLIENT_ERROR);
        ERROR_MAP.put(UNACCEPTABLE_VERSION_REQUESTED.code(), UNACCEPTABLE_VERSION_REQUESTED);
        ERROR_MAP.put(UNKNOWN_URI.code(), UNKNOWN_URI);
        ERROR_MAP.put(ADD_PARTY_INFORMATION_ERROR.code(), ADD_PARTY_INFORMATION_ERROR);

        ERROR_MAP.put(GENERIC_VALIDATION_ERROR.code(), GENERIC_VALIDATION_ERROR);
        ERROR_MAP.put(MALFORMED_SYNTAX.code(), MALFORMED_SYNTAX);
        ERROR_MAP.put(MISSING_MANDATORY_ELEMENT.code(), MISSING_MANDATORY_ELEMENT);
        ERROR_MAP.put(TOO_MANY_ELEMENTS.code(), TOO_MANY_ELEMENTS);
        ERROR_MAP.put(TOO_LARGE_PAYLOAD.code(), TOO_LARGE_PAYLOAD);
        ERROR_MAP.put(INVALID_SIGNATURE.code(), INVALID_SIGNATURE);
        ERROR_MAP.put(MODIFIED_REQUEST.code(), MODIFIED_REQUEST);
        ERROR_MAP.put(MISSING_EXTENSION_PARAMETER.code(), MISSING_EXTENSION_PARAMETER);

        ERROR_MAP.put(GENERIC_ID_NOT_FOUND.code(), GENERIC_ID_NOT_FOUND);
        ERROR_MAP.put(DESTINATION_FSP_ERROR.code(), DESTINATION_FSP_ERROR);
        ERROR_MAP.put(PAYER_FSP_ID_NOT_FOUND.code(), PAYER_FSP_ID_NOT_FOUND);
        ERROR_MAP.put(PAYEE_FSP_ID_NOT_FOUND.code(), PAYEE_FSP_ID_NOT_FOUND);
        ERROR_MAP.put(PARTY_NOT_FOUND.code(), PARTY_NOT_FOUND);
        ERROR_MAP.put(QUOTE_ID_NOT_FOUND.code(), QUOTE_ID_NOT_FOUND);
        ERROR_MAP.put(TRANSACTION_REQUEST_ID_NOT_FOUND.code(), TRANSACTION_REQUEST_ID_NOT_FOUND);
        ERROR_MAP.put(TRANSACTION_ID_NOT_FOUND.code(), TRANSACTION_ID_NOT_FOUND);
        ERROR_MAP.put(TRANSFER_ID_NOT_FOUND.code(), TRANSFER_ID_NOT_FOUND);
        ERROR_MAP.put(BULK_QUOTE_ID_NOT_FOUND.code(), BULK_QUOTE_ID_NOT_FOUND);
        ERROR_MAP.put(BULK_TRANSFER_ID_NOT_FOUND.code(), BULK_TRANSFER_ID_NOT_FOUND);

        ERROR_MAP.put(GENERIC_EXPIRED_ERROR.code(), GENERIC_EXPIRED_ERROR);
        ERROR_MAP.put(TRANSACTION_REQUEST_EXPIRED.code(), TRANSACTION_REQUEST_EXPIRED);
        ERROR_MAP.put(QUOTE_EXPIRED.code(), QUOTE_EXPIRED);
        ERROR_MAP.put(TRANSFER_EXPIRED.code(), TRANSFER_EXPIRED);

        ERROR_MAP.put(GENERIC_PAYER_ERROR.code(), GENERIC_PAYER_ERROR);
        ERROR_MAP.put(PAYER_FSP_INSUFFICIENT_LIQUIDITY.code(), PAYER_FSP_INSUFFICIENT_LIQUIDITY);
        ERROR_MAP.put(GENERIC_PAYER_REJECTION.code(), GENERIC_PAYER_REJECTION);
        ERROR_MAP.put(PAYER_REJECTED_TRANSACTION_REQUEST.code(), PAYER_REJECTED_TRANSACTION_REQUEST);
        ERROR_MAP.put(PAYER_FSP_UNSUPPORTED_TRANSACTION_TYPE.code(), PAYER_FSP_UNSUPPORTED_TRANSACTION_TYPE);
        ERROR_MAP.put(PAYER_UNSUPPORTED_CURRENCY.code(), PAYER_UNSUPPORTED_CURRENCY);
        ERROR_MAP.put(PAYER_LIMIT_ERROR.code(), PAYER_LIMIT_ERROR);
        ERROR_MAP.put(PAYER_PERMISSION_ERROR.code(), PAYER_PERMISSION_ERROR);
        ERROR_MAP.put(GENERIC_PAYER_BLOCKED_ERROR.code(), GENERIC_PAYER_BLOCKED_ERROR);

        ERROR_MAP.put(GENERIC_PAYEE_ERROR.code(), GENERIC_PAYEE_ERROR);
        ERROR_MAP.put(PAYEE_FSP_INSUFFICIENT_LIQUIDITY.code(), PAYEE_FSP_INSUFFICIENT_LIQUIDITY);
        ERROR_MAP.put(GENERIC_PAYEE_REJECTION.code(), GENERIC_PAYEE_REJECTION);
        ERROR_MAP.put(PAYEE_REJECTED_QUOTE.code(), PAYEE_REJECTED_QUOTE);
        ERROR_MAP.put(PAYEE_FSP_UNSUPPORTED_TRANSACTION_TYPE.code(), PAYEE_FSP_UNSUPPORTED_TRANSACTION_TYPE);
        ERROR_MAP.put(PAYEE_FSP_REJECTED_QUOTE.code(), PAYEE_FSP_REJECTED_QUOTE);
        ERROR_MAP.put(PAYEE_REJECTED_TRANSACTION.code(), PAYEE_REJECTED_TRANSACTION);
        ERROR_MAP.put(PAYEE_FSP_REJECTED_TRANSACTION.code(), PAYEE_FSP_REJECTED_TRANSACTION);
        ERROR_MAP.put(PAYEE_UNSUPPORTED_CURRENCY.code(), PAYEE_UNSUPPORTED_CURRENCY);
        ERROR_MAP.put(PAYEE_LIMIT_ERROR.code(), PAYEE_LIMIT_ERROR);
        ERROR_MAP.put(PAYEE_PERMISSION_ERROR.code(), PAYEE_PERMISSION_ERROR);
        ERROR_MAP.put(GENERIC_PAYEE_BLOCKED_ERROR.code(), GENERIC_PAYEE_BLOCKED_ERROR);
    }

    public static ErrorDefinition find(String code) {

        return ERROR_MAP.get(code);
    }

}
