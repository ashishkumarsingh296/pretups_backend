package restassuredapi.pojo.c2sgettransactiondetailresponsepojo;

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
"transferCount",
"transferValue",
"transferdate"
})
public class TransferList {

@JsonProperty("transferCount")
private String transferCount;
@JsonProperty("transferValue")
private String transferValue;
@JsonProperty("transferdate")
private String transferdate;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("transferCount")
public String getTransferCount() {
return transferCount;
}

@JsonProperty("transferCount")
public void setTransferCount(String transferCount) {
this.transferCount = transferCount;
}

@JsonProperty("transferValue")
public String getTransferValue() {
return transferValue;
}

@JsonProperty("transferValue")
public void setTransferValue(String transferValue) {
this.transferValue = transferValue;
}

@JsonProperty("transferdate")
public String getTransferdate() {
return transferdate;
}

@JsonProperty("transferdate")
public void setTransferdate(String transferdate) {
this.transferdate = transferdate;
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