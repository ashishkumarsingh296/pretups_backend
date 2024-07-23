
package restassuredapi.pojo.c2sgetamountservicewiselistresponsepojo;

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
    "serviceType",
    "serviceName",
    "currentFrom",
    "currentTo",
    "currentValue",
    "previousFrom",
    "previousTo",
    "previousValue"
})
public class C2sservicedetail {

    @JsonProperty("serviceType")
    private String serviceType;
    @JsonProperty("serviceName")
    private String serviceName;
    @JsonProperty("currentFrom")
    private String currentFrom;
    @JsonProperty("currentTo")
    private String currentTo;
    @JsonProperty("currentValue")
    private String currentValue;
    @JsonProperty("previousFrom")
    private String previousFrom;
    @JsonProperty("previousTo")
    private String previousTo;
    @JsonProperty("previousValue")
    private String previousValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("serviceType")
    public String getServiceType() {
        return serviceType;
    }

    @JsonProperty("serviceType")
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    @JsonProperty("serviceName")
    public String getServiceName() {
        return serviceName;
    }

    @JsonProperty("serviceName")
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @JsonProperty("currentFrom")
    public String getCurrentFrom() {
        return currentFrom;
    }

    @JsonProperty("currentFrom")
    public void setCurrentFrom(String currentFrom) {
        this.currentFrom = currentFrom;
    }

    @JsonProperty("currentTo")
    public String getCurrentTo() {
        return currentTo;
    }

    @JsonProperty("currentTo")
    public void setCurrentTo(String currentTo) {
        this.currentTo = currentTo;
    }

    @JsonProperty("currentValue")
    public String getCurrentValue() {
        return currentValue;
    }

    @JsonProperty("currentValue")
    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    @JsonProperty("previousFrom")
    public String getPreviousFrom() {
        return previousFrom;
    }

    @JsonProperty("previousFrom")
    public void setPreviousFrom(String previousFrom) {
        this.previousFrom = previousFrom;
    }

    @JsonProperty("previousTo")
    public String getPreviousTo() {
        return previousTo;
    }

    @JsonProperty("previousTo")
    public void setPreviousTo(String previousTo) {
        this.previousTo = previousTo;
    }

    @JsonProperty("previousValue")
    public String getPreviousValue() {
        return previousValue;
    }

    @JsonProperty("previousValue")
    public void setPreviousValue(String previousValue) {
        this.previousValue = previousValue;
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
