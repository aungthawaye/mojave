/*-
 * ================================================================================
 * Mojave
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

public enum FspiopErrorType {

    COMMUNICATION_ERROR("1000", "Communication error"),
    DESTINATION_COMMUNICATION_ERROR("1001", "Destination communication error"),

    GENERIC_SERVER_ERROR("2000", "Generic server error"),
    INTERNAL_SERVER_ERROR("2001", "Internal server error"),
    NOT_IMPLEMENTED("2002", "Not implemented"),
    SERVICE_CURRENTLY_UNAVAILABLE("2003", "Service currently unavailable"),
    SERVER_TIMED_OUT("2004", "Server timed out"),
    SERVER_BUSY("2005", "Server busy"),

    GENERIC_CLIENT_ERROR("3000", "Generic client error"),
    UNACCEPTABLE_VERSION_REQUESTED("3001", "Unacceptable version requested"),
    UNKNOWN_URI("3002", "Unknown URI"),
    ADD_PARTY_INFORMATION_ERROR("3003", "Add Party information error"),

    GENERIC_VALIDATION_ERROR("3100", "Generic validation error"),
    MALFORMED_SYNTAX("3101", "Malformed syntax"),
    MISSING_MANDATORY_ELEMENT("3102", "Missing mandatory element"),
    TOO_MANY_ELEMENTS("3103", "Too many elements"),
    TOO_LARGE_PAYLOAD("3104", "Too large payload"),
    INVALID_SIGNATURE("3105", "Invalid signature"),
    MODIFIED_REQUEST("3106", "Modified request"),
    MISSING_EXTENSION_PARAMETER("3107", "Missing extension parameter"),

    GENERIC_ID_NOT_FOUND("3200", "Generic ID not found"),
    DESTINATION_FSP_ERROR("3201", "Destination FSP Error"),
    PAYER_FSP_ID_NOT_FOUND("3202", "Payer FSP ID not found"),
    PAYEE_FSP_ID_NOT_FOUND("3203", "Payee FSP ID not found"),
    PARTY_NOT_FOUND("3204", "Party not found"),
    QUOTE_ID_NOT_FOUND("3205", "Quote ID not found"),
    TRANSACTION_REQUEST_ID_NOT_FOUND("3206", "Transaction request ID not found"),
    TRANSACTION_ID_NOT_FOUND("3207", "Transaction ID not found"),
    TRANSFER_ID_NOT_FOUND("3208", "Transfer ID not found"),
    BULK_QUOTE_ID_NOT_FOUND("3209", "Bulk quote ID not found"),
    BULK_TRANSFER_ID_NOT_FOUND("3210", "Bulk transfer ID not found"),

    GENERIC_EXPIRED_ERROR("3300", "Generic expired error"),
    TRANSACTION_REQUEST_EXPIRED("3301", "Transaction request expired"),
    QUOTE_EXPIRED("3302", "Quote expired"),
    TRANSFER_EXPIRED("3303", "Transfer expired"),

    GENERIC_PAYER_ERROR("4000", "Generic Payer error"),
    PAYER_FSP_INSUFFICIENT_LIQUIDITY("4001", "Payer FSP insufficient liquidity"),
    GENERIC_PAYER_REJECTION("4100", "Generic Payer rejection"),
    PAYER_REJECTED_TRANSACTION_REQUEST("4101", "Payer rejected transaction request"),
    PAYER_FSP_UNSUPPORTED_TRANSACTION_TYPE("4102", "Payer FSP unsupported transaction type"),
    PAYER_UNSUPPORTED_CURRENCY("4103", "Payer unsupported currency"),
    PAYER_LIMIT_ERROR("4200", "Payer limit error"),
    PAYER_PERMISSION_ERROR("4300", "Payer permission error"),
    GENERIC_PAYER_BLOCKED_ERROR("4400", "Generic Payer blocked error"),

    GENERIC_PAYEE_ERROR("5000", "Generic Payee error"),
    PAYEE_FSP_INSUFFICIENT_LIQUIDITY("5001", "Payee FSP insufficient liquidity"),
    GENERIC_PAYEE_REJECTION("5100", "Generic Payee rejection"),
    PAYEE_REJECTED_QUOTE("5101", "Payee rejected quote"),
    PAYEE_FSP_UNSUPPORTED_TRANSACTION_TYPE("5102", "Payee FSP unsupported transaction type"),
    PAYEE_FSP_REJECTED_QUOTE("5103", "Payee FSP rejected quote"),
    PAYEE_REJECTED_TRANSACTION("5104", "Payee rejected transaction"),
    PAYEE_FSP_REJECTED_TRANSACTION("5105", "Payee FSP rejected transaction"),
    PAYEE_UNSUPPORTED_CURRENCY("5106", "Payee unsupported currency"),
    PAYEE_LIMIT_ERROR("5200", "Payee limit error"),
    PAYEE_PERMISSION_ERROR("5300", "Payee permission error"),
    GENERIC_PAYEE_BLOCKED_ERROR("5400", "Generic Payee blocked error");

    private final String code;

    private final String name;

    FspiopErrorType(String code, String name) {

        this.code = code;
        this.name = name;
    }

    public static FspiopErrorType fromCode(String code) {

        for (FspiopErrorType e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown FSPIOP error code: " + code);
    }

    public String getCode() {

        return this.code;
    }

    public String getName() {

        return this.name;
    }
}
