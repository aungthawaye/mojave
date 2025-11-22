package io.mojaloop.fspiop.spec.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The object sent in the PUT /services/FXP callback.
 **/

@JsonTypeName("ServicesFXPPutResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
                              comments = "Generator version: 7.13.0")
public class ServicesFXPPutResponse {

    private @Valid List<@Size(min = 1, max = 32) String> providers = new ArrayList<>();

    public ServicesFXPPutResponse() {

    }

    @JsonCreator
    public ServicesFXPPutResponse(@JsonProperty(required = true,
                                                value = "providers") List<@Size(min = 1,
                                                                                max = 32) String> providers) {

        this.providers = providers;
    }

    public ServicesFXPPutResponse addProvidersItem(String providersItem) {

        if (this.providers == null) {
            this.providers = new ArrayList<>();
        }

        this.providers.add(providersItem);
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
        ServicesFXPPutResponse servicesFXPPutResponse = (ServicesFXPPutResponse) o;
        return Objects.equals(this.providers, servicesFXPPutResponse.providers);
    }

    @JsonProperty(required = true, value = "providers")
    @NotNull
    @Size(min = 0, max = 16)
    public List<@Size(min = 1, max = 32) String> getProviders() {

        return providers;
    }

    @JsonProperty(required = true, value = "providers")
    public void setProviders(List<@Size(min = 1, max = 32) String> providers) {

        this.providers = providers;
    }

    @Override
    public int hashCode() {

        return Objects.hash(providers);
    }

    /**
     * The FSP Id(s) of the participant(s) who offer currency conversion services.
     **/
    public ServicesFXPPutResponse providers(List<@Size(min = 1, max = 32) String> providers) {

        this.providers = providers;
        return this;
    }

    public ServicesFXPPutResponse removeProvidersItem(String providersItem) {

        if (providersItem != null && this.providers != null) {
            this.providers.remove(providersItem);
        }

        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ServicesFXPPutResponse {\n");

        sb.append("    providers: ").append(toIndentedString(providers)).append("\n");
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

