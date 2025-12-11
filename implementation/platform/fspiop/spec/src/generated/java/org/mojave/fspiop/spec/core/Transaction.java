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
package org.mojave.fspiop.spec.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Data model for the complex type Transaction. The Transaction type is used to carry end-to-end data between the Payer FSP and the Payee FSP in the ILP Packet. Both the transactionId and the quoteId in the data model are decided by the Payer FSP in the POST /quotes request.
 **/

@JsonTypeName("Transaction")
@jakarta.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
    comments = "Generator version: 7.13.0")
public class Transaction {

    private String transactionId;

    private String quoteId;

    private Party payee;

    private Party payer;

    private Money amount;

    private Money payeeReceiveAmount;

    private CurrencyConverter converter;

    private FxRate currencyConversion;

    private TransactionType transactionType;

    private String note;

    private ExtensionList extensionList;

    public Transaction() {

    }

    @JsonCreator
    public Transaction(@JsonProperty(
        required = true,
        value = "transactionId") String transactionId, @JsonProperty(
        required = true,
        value = "quoteId") String quoteId, @JsonProperty(
        required = true,
        value = "payee") Party payee, @JsonProperty(
        required = true,
        value = "payer") Party payer, @JsonProperty(
        required = true,
        value = "amount") Money amount, @JsonProperty(
        required = true,
        value = "transactionType") TransactionType transactionType) {

        this.transactionId = transactionId;
        this.quoteId = quoteId;
        this.payee = payee;
        this.payer = payer;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    /**
     **/
    public Transaction amount(Money amount) {

        this.amount = amount;
        return this;
    }

    /**
     **/
    public Transaction converter(CurrencyConverter converter) {

        this.converter = converter;
        return this;
    }

    /**
     **/
    public Transaction currencyConversion(FxRate currencyConversion) {

        this.currencyConversion = currencyConversion;
        return this;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction transaction = (Transaction) o;
        return Objects.equals(this.transactionId, transaction.transactionId) &&
                   Objects.equals(this.quoteId, transaction.quoteId) &&
                   Objects.equals(this.payee, transaction.payee) &&
                   Objects.equals(this.payer, transaction.payer) &&
                   Objects.equals(this.amount, transaction.amount) &&
                   Objects.equals(this.payeeReceiveAmount, transaction.payeeReceiveAmount) &&
                   Objects.equals(this.converter, transaction.converter) &&
                   Objects.equals(this.currencyConversion, transaction.currencyConversion) &&
                   Objects.equals(this.transactionType, transaction.transactionType) &&
                   Objects.equals(this.note, transaction.note) &&
                   Objects.equals(this.extensionList, transaction.extensionList);
    }

    /**
     **/
    public Transaction extensionList(ExtensionList extensionList) {

        this.extensionList = extensionList;
        return this;
    }

    @JsonProperty(
        required = true,
        value = "amount")
    @NotNull
    @Valid
    public Money getAmount() {

        return amount;
    }

    @JsonProperty(
        required = true,
        value = "amount")
    public void setAmount(Money amount) {

        this.amount = amount;
    }

    @JsonProperty("converter")
    public CurrencyConverter getConverter() {

        return converter;
    }

    @JsonProperty("converter")
    public void setConverter(CurrencyConverter converter) {

        this.converter = converter;
    }

    @JsonProperty("currencyConversion")
    @Valid
    public FxRate getCurrencyConversion() {

        return currencyConversion;
    }

    @JsonProperty("currencyConversion")
    public void setCurrencyConversion(FxRate currencyConversion) {

        this.currencyConversion = currencyConversion;
    }

    @JsonProperty("extensionList")
    @Valid
    public ExtensionList getExtensionList() {

        return extensionList;
    }

    @JsonProperty("extensionList")
    public void setExtensionList(ExtensionList extensionList) {

        this.extensionList = extensionList;
    }

    @JsonProperty("note")
    @Size(
        min = 1,
        max = 128)
    public String getNote() {

        return note;
    }

    @JsonProperty("note")
    public void setNote(String note) {

        this.note = note;
    }

    @JsonProperty(
        required = true,
        value = "payee")
    @NotNull
    @Valid
    public Party getPayee() {

        return payee;
    }

    @JsonProperty(
        required = true,
        value = "payee")
    public void setPayee(Party payee) {

        this.payee = payee;
    }

    @JsonProperty("payeeReceiveAmount")
    @Valid
    public Money getPayeeReceiveAmount() {

        return payeeReceiveAmount;
    }

    @JsonProperty("payeeReceiveAmount")
    public void setPayeeReceiveAmount(Money payeeReceiveAmount) {

        this.payeeReceiveAmount = payeeReceiveAmount;
    }

    @JsonProperty(
        required = true,
        value = "payer")
    @NotNull
    @Valid
    public Party getPayer() {

        return payer;
    }

    @JsonProperty(
        required = true,
        value = "payer")
    public void setPayer(Party payer) {

        this.payer = payer;
    }

    @JsonProperty(
        required = true,
        value = "quoteId")
    @NotNull
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    public String getQuoteId() {

        return quoteId;
    }

    @JsonProperty(
        required = true,
        value = "quoteId")
    public void setQuoteId(String quoteId) {

        this.quoteId = quoteId;
    }

    @JsonProperty(
        required = true,
        value = "transactionId")
    @NotNull
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    public String getTransactionId() {

        return transactionId;
    }

    @JsonProperty(
        required = true,
        value = "transactionId")
    public void setTransactionId(String transactionId) {

        this.transactionId = transactionId;
    }

    @JsonProperty(
        required = true,
        value = "transactionType")
    @NotNull
    @Valid
    public TransactionType getTransactionType() {

        return transactionType;
    }

    @JsonProperty(
        required = true,
        value = "transactionType")
    public void setTransactionType(TransactionType transactionType) {

        this.transactionType = transactionType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(
            transactionId, quoteId, payee, payer, amount, payeeReceiveAmount, converter,
            currencyConversion, transactionType, note, extensionList);
    }

    /**
     * Memo assigned to transaction.
     **/
    public Transaction note(String note) {

        this.note = note;
        return this;
    }

    /**
     **/
    public Transaction payee(Party payee) {

        this.payee = payee;
        return this;
    }

    /**
     **/
    public Transaction payeeReceiveAmount(Money payeeReceiveAmount) {

        this.payeeReceiveAmount = payeeReceiveAmount;
        return this;
    }

    /**
     **/
    public Transaction payer(Party payer) {

        this.payer = payer;
        return this;
    }

    /**
     * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
     **/
    public Transaction quoteId(String quoteId) {

        this.quoteId = quoteId;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class Transaction {\n");

        sb.append("    transactionId: ").append(toIndentedString(transactionId)).append("\n");
        sb.append("    quoteId: ").append(toIndentedString(quoteId)).append("\n");
        sb.append("    payee: ").append(toIndentedString(payee)).append("\n");
        sb.append("    payer: ").append(toIndentedString(payer)).append("\n");
        sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
        sb
            .append("    payeeReceiveAmount: ")
            .append(toIndentedString(payeeReceiveAmount))
            .append("\n");
        sb.append("    converter: ").append(toIndentedString(converter)).append("\n");
        sb
            .append("    currencyConversion: ")
            .append(toIndentedString(currencyConversion))
            .append("\n");
        sb.append("    transactionType: ").append(toIndentedString(transactionType)).append("\n");
        sb.append("    note: ").append(toIndentedString(note)).append("\n");
        sb.append("    extensionList: ").append(toIndentedString(extensionList)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Identifier that correlates all messages of the same sequence. The API data type UUID (Universally Unique Identifier) is a JSON String in canonical format, conforming to [RFC 4122](https://tools.ietf.org/html/rfc4122), that is restricted by a regular expression for interoperability reasons. A UUID is always 36 characters long, 32 hexadecimal symbols and 4 dashes (‘-‘).
     **/
    public Transaction transactionId(String transactionId) {

        this.transactionId = transactionId;
        return this;
    }

    /**
     **/
    public Transaction transactionType(TransactionType transactionType) {

        this.transactionType = transactionType;
        return this;
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {

        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

