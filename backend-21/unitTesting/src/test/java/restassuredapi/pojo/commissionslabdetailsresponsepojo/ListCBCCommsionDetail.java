
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
    "otfRate",
    "otfType",
    "otfValue"
})
@Generated("jsonschema2pojo")
public class ListCBCCommsionDetail {

    @JsonProperty("otfRate")
    private String otfRate;
    @JsonProperty("otfType")
    private String otfType;
    @JsonProperty("otfValue")
    private String otfValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("otfRate")
    public String getOtfRate() {
        return otfRate;
    }

    @JsonProperty("otfRate")
    public void setOtfRate(String otfRate) {
        this.otfRate = otfRate;
    }

    @JsonProperty("otfType")
    public String getOtfType() {
        return otfType;
    }

    @JsonProperty("otfType")
    public void setOtfType(String otfType) {
        this.otfType = otfType;
    }

    @JsonProperty("otfValue")
    public String getOtfValue() {
        return otfValue;
    }

    @JsonProperty("otfValue")
    public void setOtfValue(String otfValue) {
        this.otfValue = otfValue;
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
