package restassuredapi.pojo.transactionaldatarequestpojo;

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
"fromDate",
"toDate"
})
public class TransactionalDataRequestPojo {

@JsonProperty("fromDate")
private String fromDate;
@JsonProperty("toDate")
private String toDate;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("fromDate")
public String getFromDate() {
return fromDate;
}

@JsonProperty("fromDate")
public void setFromDate(String fromDate) {
this.fromDate = fromDate;
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