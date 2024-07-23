
package restassuredapi.pojo.lowthresholdreportrequestpojo;

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
    "category",
    "domain",
    "extnwcode",
    "fromDate",
    "geography",
    "threshhold",
    "toDate"
})
@Generated("jsonschema2pojo")
public class Data {

    @JsonProperty("category")
    private String category;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("fromDate")
    private String fromDate;
    @JsonProperty("geography")
    private String geography;
    @JsonProperty("threshhold")
    private String threshhold;
    @JsonProperty("toDate")
    private String toDate;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    @JsonProperty("domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }

    @JsonProperty("fromDate")
    public String getFromDate() {
        return fromDate;
    }

    @JsonProperty("fromDate")
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    @JsonProperty("geography")
    public String getGeography() {
        return geography;
    }

    @JsonProperty("geography")
    public void setGeography(String geography) {
        this.geography = geography;
    }

    @JsonProperty("threshhold")
    public String getThreshhold() {
        return threshhold;
    }

    @JsonProperty("threshhold")
    public void setThreshhold(String threshhold) {
        this.threshhold = threshhold;
    }

    @JsonProperty("toDate")
    public String getToDate() {
        return toDate;
    }

    @JsonProperty("toDate")
    public void setToDate(String toDate) {
        this.toDate = toDate;
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
