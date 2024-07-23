
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
    "date",
    "day",
    "hours",
    "minutes",
    "month",
    "nanos",
    "seconds",
    "time",
    "timezoneOffset",
    "year"
})
public class ModifiedOnTimestamp {

    @JsonProperty("date")
    private int date;
    @JsonProperty("day")
    private int day;
    @JsonProperty("hours")
    private int hours;
    @JsonProperty("minutes")
    private int minutes;
    @JsonProperty("month")
    private int month;
    @JsonProperty("nanos")
    private int nanos;
    @JsonProperty("seconds")
    private int seconds;
    @JsonProperty("time")
    private int time;
    @JsonProperty("timezoneOffset")
    private int timezoneOffset;
    @JsonProperty("year")
    private int year;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("date")
    public int getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(int date) {
        this.date = date;
    }

    @JsonProperty("day")
    public int getDay() {
        return day;
    }

    @JsonProperty("day")
    public void setDay(int day) {
        this.day = day;
    }

    @JsonProperty("hours")
    public int getHours() {
        return hours;
    }

    @JsonProperty("hours")
    public void setHours(int hours) {
        this.hours = hours;
    }

    @JsonProperty("minutes")
    public int getMinutes() {
        return minutes;
    }

    @JsonProperty("minutes")
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @JsonProperty("month")
    public int getMonth() {
        return month;
    }

    @JsonProperty("month")
    public void setMonth(int month) {
        this.month = month;
    }

    @JsonProperty("nanos")
    public int getNanos() {
        return nanos;
    }

    @JsonProperty("nanos")
    public void setNanos(int nanos) {
        this.nanos = nanos;
    }

    @JsonProperty("seconds")
    public int getSeconds() {
        return seconds;
    }

    @JsonProperty("seconds")
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    @JsonProperty("time")
    public int getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(int time) {
        this.time = time;
    }

    @JsonProperty("timezoneOffset")
    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    @JsonProperty("timezoneOffset")
    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
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
