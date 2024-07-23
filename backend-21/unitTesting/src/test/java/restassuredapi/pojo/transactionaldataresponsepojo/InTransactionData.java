package restassuredapi.pojo.transactionaldataresponsepojo;

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
"countPercentage",
"fromDate",
"lastMonthCount",
"lastMonthValue",
"previousFromDate",
"previousToDate",
"toDate",
"totalCount",
"totalPercentage",
"totalValue"
})
public class InTransactionData {

@JsonProperty("countPercentage")
private String countPercentage;
@JsonProperty("fromDate")
private String fromDate;
@JsonProperty("lastMonthCount")
private String lastMonthCount;
@JsonProperty("lastMonthValue")
private String lastMonthValue;
@JsonProperty("previousFromDate")
private String previousFromDate;
@JsonProperty("previousToDate")
private String previousToDate;
@JsonProperty("toDate")
private String toDate;
@JsonProperty("totalCount")
private String totalCount;
@JsonProperty("totalPercentage")
private String totalPercentage;
@JsonProperty("totalValue")
private String totalValue;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("countPercentage")
public String getCountPercentage() {
return countPercentage;
}

@JsonProperty("countPercentage")
public void setCountPercentage(String countPercentage) {
this.countPercentage = countPercentage;
}

@JsonProperty("fromDate")
public String getFromDate() {
return fromDate;
}

@JsonProperty("fromDate")
public void setFromDate(String fromDate) {
this.fromDate = fromDate;
}

@JsonProperty("lastMonthCount")
public String getLastMonthCount() {
return lastMonthCount;
}

@JsonProperty("lastMonthCount")
public void setLastMonthCount(String lastMonthCount) {
this.lastMonthCount = lastMonthCount;
}

@JsonProperty("lastMonthValue")
public String getLastMonthValue() {
return lastMonthValue;
}

@JsonProperty("lastMonthValue")
public void setLastMonthValue(String lastMonthValue) {
this.lastMonthValue = lastMonthValue;
}

@JsonProperty("previousFromDate")
public String getPreviousFromDate() {
return previousFromDate;
}

@JsonProperty("previousFromDate")
public void setPreviousFromDate(String previousFromDate) {
this.previousFromDate = previousFromDate;
}

@JsonProperty("previousToDate")
public String getPreviousToDate() {
return previousToDate;
}

@JsonProperty("previousToDate")
public void setPreviousToDate(String previousToDate) {
this.previousToDate = previousToDate;
}

@JsonProperty("toDate")
public String getToDate() {
return toDate;
}

@JsonProperty("toDate")
public void setToDate(String toDate) {
this.toDate = toDate;
}

@JsonProperty("totalCount")
public String getTotalCount() {
return totalCount;
}

@JsonProperty("totalCount")
public void setTotalCount(String totalCount) {
this.totalCount = totalCount;
}

@JsonProperty("totalPercentage")
public String getTotalPercentage() {
return totalPercentage;
}

@JsonProperty("totalPercentage")
public void setTotalPercentage(String totalPercentage) {
this.totalPercentage = totalPercentage;
}

@JsonProperty("totalValue")
public String getTotalValue() {
return totalValue;
}

@JsonProperty("totalValue")
public void setTotalValue(String totalValue) {
this.totalValue = totalValue;
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

