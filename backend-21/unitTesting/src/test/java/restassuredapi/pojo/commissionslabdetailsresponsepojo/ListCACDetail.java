
package restassuredapi.pojo.commissionslabdetailsresponsepojo;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cacDetailRate",
    "cacDetailType",
    "cacDetailValue"
})
@Generated("jsonschema2pojo")
public class ListCACDetail {

    @JsonProperty("cacDetailRate")
    private String cacDetailRate;
    @JsonProperty("cacDetailType")
    private String cacDetailType;
    @JsonProperty("cacDetailValue")
    private String cacDetailValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cacDetailRate")
    public String getCacDetailRate() {
        return cacDetailRate;
    }

    @JsonProperty("cacDetailRate")
    public void setCacDetailRate(String cacDetailRate) {
        this.cacDetailRate = cacDetailRate;
    }

    @JsonProperty("cacDetailType")
    public String getCacDetailType() {
        return cacDetailType;
    }

    @JsonProperty("cacDetailType")
    public void setCacDetailType(String cacDetailType) {
        this.cacDetailType = cacDetailType;
    }

    @JsonProperty("cacDetailValue")
    public String getCacDetailValue() {
        return cacDetailValue;
    }

    @JsonProperty("cacDetailValue")
    public void setCacDetailValue(String cacDetailValue) {
        this.cacDetailValue = cacDetailValue;
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
