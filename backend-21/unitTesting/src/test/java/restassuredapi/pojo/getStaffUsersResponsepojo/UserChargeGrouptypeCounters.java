
package restassuredapi.pojo.getStaffUsersResponsepojo;

import javax.annotation.Generated;
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
@Generated("jsonschema2pojo")
public class UserChargeGrouptypeCounters {

    @JsonProperty("counters")
    private Integer counters;
    @JsonProperty("day")
    private Integer day;
    @JsonProperty("groupType")
    private String groupType;
    @JsonProperty("module")
    private String module;
    @JsonProperty("month")
    private Integer month;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("type")
    private String type;
    @JsonProperty("userID")
    private String userID;
    @JsonProperty("year")
    private Integer year;

    @JsonProperty("counters")
    public Integer getCounters() {
        return counters;
    }

    @JsonProperty("counters")
    public void setCounters(Integer counters) {
        this.counters = counters;
    }

    @JsonProperty("day")
    public Integer getDay() {
        return day;
    }

    @JsonProperty("day")
    public void setDay(Integer day) {
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
    public Integer getMonth() {
        return month;
    }

    @JsonProperty("month")
    public void setMonth(Integer month) {
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
    public Integer getYear() {
        return year;
    }

    @JsonProperty("year")
    public void setYear(Integer year) {
        this.year = year;
    }

}
