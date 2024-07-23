
package restassuredapi.pojo.commissioncalculatoresponsepojo;

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
    "currentMonthIncome",
    "previousMonthIncome",
    "previousMonthFromDate",
    "previousMonthToDate"
})
public class Comincomedetails {

    @JsonProperty("currentMonthIncome")
    private String currentMonthIncome;
    @JsonProperty("previousMonthIncome")
    private String previousMonthIncome;
    @JsonProperty("previousMonthFromDate")
    private String previousMonthFromDate;
    @JsonProperty("previousMonthToDate")
    private String previousMonthToDate;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("currentMonthIncome")
    public String getCurrentMonthIncome() {
        return currentMonthIncome;
    }

    @JsonProperty("currentMonthIncome")
    public void setCurrentMonthIncome(String currentMonthIncome) {
        this.currentMonthIncome = currentMonthIncome;
    }

    @JsonProperty("previousMonthIncome")
    public String getPreviousMonthIncome() {
        return previousMonthIncome;
    }

    @JsonProperty("previousMonthIncome")
    public void setPreviousMonthIncome(String previousMonthIncome) {
        this.previousMonthIncome = previousMonthIncome;
    }

    @JsonProperty("previousMonthFromDate")
    public String getPreviousMonthFromDate() {
        return previousMonthFromDate;
    }

    @JsonProperty("previousMonthFromDate")
    public void setPreviousMonthFromDate(String previousMonthFromDate) {
        this.previousMonthFromDate = previousMonthFromDate;
    }

    @JsonProperty("previousMonthToDate")
    public String getPreviousMonthToDate() {
        return previousMonthToDate;
    }

    @JsonProperty("previousMonthToDate")
    public void setPreviousMonthToDate(String previousMonthToDate) {
        this.previousMonthToDate = previousMonthToDate;
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
