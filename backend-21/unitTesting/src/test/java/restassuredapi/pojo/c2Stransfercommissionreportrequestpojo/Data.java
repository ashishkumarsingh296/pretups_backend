
package restassuredapi.pojo.c2Stransfercommissionreportrequestpojo;

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
    "categoryCode",
    "domain",
    "extnwcode",
    "fromDate",
    "geography",
    "service",
    "toDate",
    "transStatus"
})
@Generated("jsonschema2pojo")
public class Data {

    @JsonProperty("categoryCode")
    private String categoryCode;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("fromDate")
    private String fromDate;
    @JsonProperty("geography")
    private String geography;
    @JsonProperty("service")
    private String service;
    @JsonProperty("toDate")
    private String toDate;
    @JsonProperty("transStatus")
    private String transStatus;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("categoryCode")
    public String getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
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

    @JsonProperty("service")
    public String getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(String service) {
        this.service = service;
    }

    @JsonProperty("toDate")
    public String getToDate() {
        return toDate;
    }

    @JsonProperty("toDate")
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    @JsonProperty("transStatus")
    public String getTransStatus() {
        return transStatus;
    }

    @JsonProperty("transStatus")
    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
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
