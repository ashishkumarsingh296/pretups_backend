
package restassuredapi.pojo.getuserservicebalancepojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "balanceAssociated",
    "serviceCode",
    "serviceName"
})
public class ServiceList {

    @JsonProperty("balanceAssociated")
    private String balanceAssociated;
    @JsonProperty("serviceCode")
    private String serviceCode;
    @JsonProperty("serviceName")
    private String serviceName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public ServiceList() {
    }

    /**
     * 
     * @param serviceCode
     * @param balanceAssociated
     * @param serviceName
     */
    public ServiceList(String balanceAssociated, String serviceCode, String serviceName) {
        super();
        this.balanceAssociated = balanceAssociated;
        this.serviceCode = serviceCode;
        this.serviceName = serviceName;
    }

    @JsonProperty("balanceAssociated")
    public String getBalanceAssociated() {
        return balanceAssociated;
    }

    @JsonProperty("balanceAssociated")
    public void setBalanceAssociated(String balanceAssociated) {
        this.balanceAssociated = balanceAssociated;
    }

    @JsonProperty("serviceCode")
    public String getServiceCode() {
        return serviceCode;
    }

    @JsonProperty("serviceCode")
    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @JsonProperty("serviceName")
    public String getServiceName() {
        return serviceName;
    }

    @JsonProperty("serviceName")
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
