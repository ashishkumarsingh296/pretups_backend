
package restassuredapi.pojo.channelUserListResponsepojo;

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
    "counters",
    "day",
    "groupType",
    "module",
    "month",
    "msisdn",
    "type",
    "userID",
    "year"
})
public class UserChargeGrouptypeCounters {

    @JsonProperty("counters")
    private int counters;
    @JsonProperty("day")
    private int day;
    @JsonProperty("groupType")
    private String groupType;
    @JsonProperty("module")
    private String module;
    @JsonProperty("month")
    private int month;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("type")
    private String type;
    @JsonProperty("userID")
    private String userID;
    @JsonProperty("year")
    private int year;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("counters")
    public int getCounters() {
        return counters;
    }

    @JsonProperty("counters")
    public void setCounters(int counters) {
        this.counters = counters;
    }

    @JsonProperty("day")
    public int getDay() {
        return day;
    }

    @JsonProperty("day")
    public void setDay(int day) {
        this.day = day;
    }

    @JsonProperty("groupType")
    public String getGroupType() {
        return groupType;
    }

    @JsonProperty("groupType")
    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    @JsonProperty("module")
    public String getModule() {
        return module;
    }

    @JsonProperty("module")
    public void setModule(String module) {
        this.module = module;
    }

    @JsonProperty("month")
    public int getMonth() {
        return month;
    }

    @JsonProperty("month")
    public void setMonth(int month) {
        this.month = month;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("userID")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @JsonProperty("year")
    public int getYear() {
        return year;
    }

    @JsonProperty("year")
    public void setYear(int year) {
        this.year = year;
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
