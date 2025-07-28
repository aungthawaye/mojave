package io.mojaloop.common.fspiop.error;

public class FspiopErrors {

    //@@formatter:off
    public static class CommunicationErrors {

        public static final ErrorMessage E1000 = new ErrorMessage("1000", "Communication error", "Generic communication error.");
        public static final ErrorMessage E1001 = new ErrorMessage("1001", "Destination communication error", "Destination of the request failed to be reached. This usually indicates that a Peer FSP failed to respond from an intermediate entity.");

    }

    public static class ServerErrors {

        public static final ErrorMessage E2000 = new ErrorMessage("2000", "Generic server error", "Used to avoid disclosing private details.");
        public static final ErrorMessage E2001 = new ErrorMessage("2001", "Internal server error", "Unexpected exception or bug.");
        public static final ErrorMessage E2002 = new ErrorMessage("2002", "Not implemented", "Service not supported by server.");
        public static final ErrorMessage E2003 = new ErrorMessage("2003", "Service currently unavailable", "Service temporarily unavailable (e.g. maintenance).");
        public static final ErrorMessage E2004 = new ErrorMessage("2004", "Server timed out", "Callback not received within timeout period.");
        public static final ErrorMessage E2005 = new ErrorMessage("2005", "Server busy", "Server rejecting requests due to overload.");

    }

    public static class ClientErrors {

        public static final ErrorMessage E3000 = new ErrorMessage("3000", "Generic client error", "Used to avoid disclosing private client errors.");
        public static final ErrorMessage E3001 = new ErrorMessage("3001", "Unacceptable version requested", "Client requested unsupported API version.");
        public static final ErrorMessage E3002 = new ErrorMessage("3002", "Unknown URI", "Provided URI not recognized by server.");
        public static final ErrorMessage E3003 = new ErrorMessage("3003", "Add Party information error", "Error occurred while adding/updating party info.");

        public static final ErrorMessage E3100 = new ErrorMessage("3100", "Generic validation error", "Generic format error.");
        public static final ErrorMessage E3101 = new ErrorMessage("3101", "Malformed syntax", "Invalid parameter format (e.g. amount '5.ABC').");
        public static final ErrorMessage E3102 = new ErrorMessage("3102", "Missing mandatory element", "Required element in data model is missing.");
        public static final ErrorMessage E3103 = new ErrorMessage("3103", "Too many elements", "Array exceeds allowed maximum.");
        public static final ErrorMessage E3104 = new ErrorMessage("3104", "Too large payload", "Request payload exceeds allowed size.");
        public static final ErrorMessage E3105 = new ErrorMessage("3105", "Invalid signature", "Payload signature invalid or modified.");
        public static final ErrorMessage E3106 = new ErrorMessage("3106", "Modified request", "Parameters differ from a previously processed request.");
        public static final ErrorMessage E3107 = new ErrorMessage("3107", "Missing extension parameter", "Required extension parameter not provided.");

        public static final ErrorMessage E3200 = new ErrorMessage("3200", "Generic ID not found", "Generic identifier error.");
        public static final ErrorMessage E3201 = new ErrorMessage("3201", "Destination FSP Error", "Destination FSP cannot be found or doesn't exist.");
        public static final ErrorMessage E3202 = new ErrorMessage("3202", "Payer FSP ID not found", "Payer FSP ID not recognized.");
        public static final ErrorMessage E3203 = new ErrorMessage("3203", "Payee FSP ID not found", "Payee FSP ID not recognized.");
        public static final ErrorMessage E3204 = new ErrorMessage("3204", "Party not found", "Provided party identifier not found.");
        public static final ErrorMessage E3205 = new ErrorMessage("3205", "Quote ID not found", "Quote ID not found on server.");
        public static final ErrorMessage E3206 = new ErrorMessage("3206", "Transaction request ID not found", "ID not found for transaction request.");
        public static final ErrorMessage E3207 = new ErrorMessage("3207", "Transaction ID not found", "Transaction ID not found.");
        public static final ErrorMessage E3208 = new ErrorMessage("3208", "Transfer ID not found", "Transfer ID not found.");
        public static final ErrorMessage E3209 = new ErrorMessage("3209", "Bulk quote ID not found", "Bulk quote ID not found.");
        public static final ErrorMessage E3210 = new ErrorMessage("3210", "Bulk transfer ID not found", "Bulk transfer ID not found.");

        public static final ErrorMessage E3300 = new ErrorMessage("3300", "Generic expired error", "Generic expired object error (non‑specific).");
        public static final ErrorMessage E3301 = new ErrorMessage("3301", "Transaction request expired", "Transaction request has already expired.");
        public static final ErrorMessage E3302 = new ErrorMessage("3302", "Quote expired", "The quote is no longer valid.");
        public static final ErrorMessage E3303 = new ErrorMessage("3303", "Transfer expired", "The transfer has already expired.");

    }

    public static class PayerErrors {

        public static final ErrorMessage E4000 = new ErrorMessage("4000", "Generic Payer error", "Used for private payer-related errors.");
        public static final ErrorMessage E4001 = new ErrorMessage("4001", "Payer FSP insufficient liquidity", "Payer’s FSP lacks funds.");
        public static final ErrorMessage E4100 = new ErrorMessage("4100", "Generic Payer rejection", "Payer or payer FSP rejected the request.");
        public static final ErrorMessage E4101 = new ErrorMessage("4101", "Payer rejected transaction request", "Payer rejected the transaction request.");
        public static final ErrorMessage E4102 = new ErrorMessage("4102", "Payer FSP unsupported transaction type", "Transaction type not supported by Payer FSP.");
        public static final ErrorMessage E4103 = new ErrorMessage("4103", "Payer unsupported currency", "Payer does not support requested currency.");
        public static final ErrorMessage E4200 = new ErrorMessage("4200", "Payer limit error", "Payment amount/frequency exceeds limits.");
        public static final ErrorMessage E4300 = new ErrorMessage("4300", "Payer permission error", "Payer lacks permission to perform operation.");
        public static final ErrorMessage E4400 = new ErrorMessage("4400", "Generic Payer blocked error", "Payer is blocked or failed regulatory screening.");

    }

    public static class PayeeErrors {

        public static final ErrorMessage E5000 = new ErrorMessage("5000", "Generic Payee error", "Generic error related to payee or payee FSP.");
        public static final ErrorMessage E5001 = new ErrorMessage("5001", "Payee FSP insufficient liquidity", "Payee FSP lacks sufficient liquidity.");
        public static final ErrorMessage E5100 = new ErrorMessage("5100", "Generic Payee rejection", "Payee or payee FSP rejected request.");
        public static final ErrorMessage E5101 = new ErrorMessage("5101", "Payee rejected quote", "Payee unwilling to proceed after quote.");
        public static final ErrorMessage E5102 = new ErrorMessage("5102", "Payee FSP unsupported transaction type", "Payee FSP rejects unsupported transaction type.");
        public static final ErrorMessage E5103 = new ErrorMessage("5103", "Payee FSP rejected quote", "Payee FSP unwilling to continue after quote.");
        public static final ErrorMessage E5104 = new ErrorMessage("5104", "Payee rejected transaction", "Payee rejected the transaction.");
        public static final ErrorMessage E5105 = new ErrorMessage("5105", "Payee FSP rejected transaction", "Payee FSP rejected the transaction.");
        public static final ErrorMessage E5106 = new ErrorMessage("5106", "Payee unsupported currency", "Payee does not support requested currency.");
        public static final ErrorMessage E5200 = new ErrorMessage("5200", "Payee limit error", "Receiving amount/frequency exceeds allowed limits.");
        public static final ErrorMessage E5300 = new ErrorMessage("5300", "Payee permission error", "Payee lacks permission to perform operation.");
        public static final ErrorMessage E5400 = new ErrorMessage("5400", "Generic Payee blocked error", "Payee is blocked or failed regulatory screening.");

    }

    //@@formatter:on
}
