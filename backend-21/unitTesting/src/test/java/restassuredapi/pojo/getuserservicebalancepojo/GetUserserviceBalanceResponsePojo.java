
package restassuredapi.pojo.getuserservicebalancepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "message",
    "messageCode",
    "serviceList",
    "status"
})
public class GetUserserviceBalanceResponsePojo {

    @JsonProperty("message")
    private String message;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("serviceList")
    private List<ServiceList> serviceList = null;
    @JsonProperty("status")
    private String status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public GetUserserviceBalanceResponsePojo() {
    }

    /**
     * 
     * @param serviceList
     * @param messageCode
     * @param message
     * @param status
     */
    public GetUserserviceBalanceResponsePojo(String message, String messageCode, List<ServiceList> serviceList, String status) {
        super();
        this.message = message;
        this.messageCode = messageCode;
        this.serviceList = serviceList;
        this.status = status;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("messageCode")
    public String getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("serviceList")
    public List<ServiceList> getServiceList() {
        return serviceList;
    }

    @JsonProperty("serviceList")
    public void setServiceList(List<ServiceList> serviceList) {
        this.serviceList = serviceList;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
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
