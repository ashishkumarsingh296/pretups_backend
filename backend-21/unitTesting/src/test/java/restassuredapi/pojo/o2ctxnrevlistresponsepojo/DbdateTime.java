
package restassuredapi.pojo.o2ctxnrevlistresponsepojo;

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
@Generated("jsonschema2pojo")
public class DbdateTime {

    @JsonProperty("date")
    private Integer date;
    @JsonProperty("day")
    private Integer day;
    @JsonProperty("hours")
    private Integer hours;
    @JsonProperty("minutes")
    private Integer minutes;
    @JsonProperty("month")
    private Integer month;
    @JsonProperty("nanos")
    private Integer nanos;
    @JsonProperty("seconds")
    private Integer seconds;
    @JsonProperty("time")
    private Integer time;
    @JsonProperty("timezoneOffset")
    private Integer timezoneOffset;
    @JsonProperty("year")
    private Integer year;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("date")
    public Integer getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(Integer date) {
        this.date = date;
    }

    @JsonProperty("day")
    public Integer getDay() {
        return day;
    }

    @JsonProperty("day")
    public void setDay(Integer day) {
        this.day = day;
    }

    @JsonProperty("hours")
    public Integer getHours() {
        return hours;
    }

    @JsonProperty("hours")
    public void setHours(Integer hours) {
        this.hours = hours;
    }

    @JsonProperty("minutes")
    public Integer getMinutes() {
        return minutes;
    }

    @JsonProperty("minutes")
    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    @JsonProperty("month")
    public Integer getMonth() {
        return month;
    }

    @JsonProperty("month")
    public void setMonth(Integer month) {
        this.month = month;
    }

    @JsonProperty("nanos")
    public Integer getNanos() {
        return nanos;
    }

    @JsonProperty("nanos")
    public void setNanos(Integer nanos) {
        this.nanos = nanos;
    }

    @JsonProperty("seconds")
    public Integer getSeconds() {
        return seconds;
    }

    @JsonProperty("seconds")
    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    @JsonProperty("time")
    public Integer getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(Integer time) {
        this.time = time;
    }

    @JsonProperty("timezoneOffset")
    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    @JsonProperty("timezoneOffset")
    public void setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    @JsonProperty("year")
    public Integer getYear() {
        return year;
    }

    @JsonProperty("year")
    public void setYear(Integer year) {
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
