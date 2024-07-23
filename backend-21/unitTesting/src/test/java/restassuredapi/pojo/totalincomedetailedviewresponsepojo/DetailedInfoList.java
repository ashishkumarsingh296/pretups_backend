
package restassuredapi.pojo.totalincomedetailedviewresponsepojo;

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
    "totalIncome",
    "baseCommission",
    "additionalCommission",
    "cac",
    "cbc",
    "date"
})
public class DetailedInfoList {

    @JsonProperty("totalIncome")
    private String totalIncome;
    @JsonProperty("baseCommission")
    private String baseCommission;
    @JsonProperty("additionalCommission")
    private String additionalCommission;
    @JsonProperty("cac")
    private String cac;
    @JsonProperty("cbc")
    private String cbc;
    @JsonProperty("date")
    private String date;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("totalIncome")
    public String getTotalIncome() {
        return totalIncome;
    }

    @JsonProperty("totalIncome")
    public void setTotalIncome(String totalIncome) {
        this.totalIncome = totalIncome;
    }

    @JsonProperty("baseCommission")
    public String getBaseCommission() {
        return baseCommission;
    }

    @JsonProperty("baseCommission")
    public void setBaseCommission(String baseCommission) {
        this.baseCommission = baseCommission;
    }

    @JsonProperty("additionalCommission")
    public String getAdditionalCommission() {
        return additionalCommission;
    }

    @JsonProperty("additionalCommission")
    public void setAdditionalCommission(String additionalCommission) {
        this.additionalCommission = additionalCommission;
    }

    @JsonProperty("cac")
    public String getCac() {
        return cac;
    }

    @JsonProperty("cac")
    public void setCac(String cac) {
        this.cac = cac;
    }

    @JsonProperty("cbc")
    public String getCbc() {
        return cbc;
    }

    @JsonProperty("cbc")
    public void setCbc(String cbc) {
        this.cbc = cbc;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
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
