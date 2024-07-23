
package restassuredapi.pojo.selfvoucherenquirysubscriberrequestpojo;

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
    "subscriberMsisdn"
})
public class Data {

    @JsonProperty("subscriberMsisdn")
    private String subscriberMsisdn;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("subscriberMsisdn")
    public String getSubscriberMsisdn() {
        return subscriberMsisdn;
    }

    @JsonProperty("subscriberMsisdn")
    public void setSubscriberMsisdn(String subscriberMsisdn) {
        this.subscriberMsisdn = subscriberMsisdn;
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
