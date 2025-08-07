package io.mojaloop.fspiop.spec.settlement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonTypeName("Participant")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              date = "2025-08-04T08:01:35.765568+06:30[Asia/Rangoon]", comments = "Generator version: 7.13.0")
public class Participant {

    private Integer id;

    private @Valid List<@Valid Accounts> accounts = new ArrayList<>();

    public Participant() {

    }

    /**
     **/
    public Participant accounts(List<@Valid Accounts> accounts) {

        this.accounts = accounts;
        return this;
    }

    public Participant addAccountsItem(Accounts accountsItem) {

        if (this.accounts == null) {
            this.accounts = new ArrayList<>();
        }

        this.accounts.add(accountsItem);
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
        Participant participant = (Participant) o;
        return Objects.equals(this.id, participant.id) && Objects.equals(this.accounts, participant.accounts);
    }

    @JsonProperty("accounts")
    @Valid
    public List<@Valid Accounts> getAccounts() {

        return accounts;
    }

    @JsonProperty("accounts")
    public void setAccounts(List<@Valid Accounts> accounts) {

        this.accounts = accounts;
    }

    @JsonProperty("id")
    public Integer getId() {

        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {

        this.id = id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, accounts);
    }

    /**
     **/
    public Participant id(Integer id) {

        this.id = id;
        return this;
    }

    public Participant removeAccountsItem(Accounts accountsItem) {

        if (accountsItem != null && this.accounts != null) {
            this.accounts.remove(accountsItem);
        }

        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class Participant {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    accounts: ").append(toIndentedString(accounts)).append("\n");
        sb.append("}");
        return sb.toString();
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

