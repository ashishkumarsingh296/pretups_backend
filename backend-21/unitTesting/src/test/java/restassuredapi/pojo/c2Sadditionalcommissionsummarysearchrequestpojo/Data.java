
package restassuredapi.pojo.c2Sadditionalcommissionsummarysearchrequestpojo;

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
    "dailyOrmonthlyOption",
    "domain",
    "fromDate",
    "fromMonthYear",
    "geography",
    "service",
    "toDate",
    "toMonthYear"
})
@Generated("jsonschema2pojo")
public class Data {

    @JsonProperty("categoryCode")
    private String categoryCode;
    @JsonProperty("dailyOrmonthlyOption")
    private String dailyOrmonthlyOption;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("fromDate")
    private String fromDate;
    @JsonProperty("fromMonthYear")
    private String fromMonthYear;
    @JsonProperty("geography")
    private String geography;
    @JsonProperty("service")
    private String service;
    @JsonProperty("toDate")
    private String toDate;
    @JsonProperty("toMonthYear")
    private String toMonthYear;
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

    @JsonProperty("dailyOrmonthlyOption")
    public String getDailyOrmonthlyOption() {
        return dailyOrmonthlyOption;
    }

    @JsonProperty("dailyOrmonthlyOption")
    public void setDailyOrmonthlyOption(String dailyOrmonthlyOption) {
        this.dailyOrmonthlyOption = dailyOrmonthlyOption;
    }

    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    @JsonProperty("domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @JsonProperty("fromDate")
    public String getFromDate() {
        return fromDate;
    }

    @JsonProperty("fromDate")
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    @JsonProperty("fromMonthYear")
    public String getFromMonthYear() {
        return fromMonthYear;
    }

    @JsonProperty("fromMonthYear")
    public void setFromMonthYear(String fromMonthYear) {
        this.fromMonthYear = fromMonthYear;
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

    @JsonProperty("toMonthYear")
    public String getToMonthYear() {
        return toMonthYear;
    }

    @JsonProperty("toMonthYear")
    public void setToMonthYear(String toMonthYear) {
        this.toMonthYear = toMonthYear;
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
