package restassuredapi.pojo.vouchervalidateinforequestpojo;

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
"count",
"fromSerialNumber",
"toSerialNumber"
})
public class VoucherValidateInfoRequestPojo {

@JsonProperty("count")
private String count;
@JsonProperty("fromSerialNumber")
private String fromSerialNumber;
@JsonProperty("toSerialNumber")
private String toSerialNumber;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("count")
public String getCount() {
return count;
}

@JsonProperty("count")
public void setCount(String count) {
this.count = count;
}

@JsonProperty("fromSerialNumber")
public String getFromSerialNumber() {
return fromSerialNumber;
}

@JsonProperty("fromSerialNumber")
public void setFromSerialNumber(String fromSerialNumber) {
this.fromSerialNumber = fromSerialNumber;
}

@JsonProperty("toSerialNumber")
public String getToSerialNumber() {
return toSerialNumber;
}

@JsonProperty("toSerialNumber")
public void setToSerialNumber(String toSerialNumber) {
this.toSerialNumber = toSerialNumber;
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