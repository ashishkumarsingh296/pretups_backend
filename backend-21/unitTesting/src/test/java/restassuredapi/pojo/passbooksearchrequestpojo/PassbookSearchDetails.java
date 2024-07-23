package restassuredapi.pojo.passbooksearchrequestpojo;

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
"extnwcode",
"fromDate",
"productCode",
"toDate"
})
@Generated("jsonschema2pojo")
public class PassbookSearchDetails {

@JsonProperty("extnwcode")
private String extnwcode;
@JsonProperty("fromDate")
private String fromDate;
@JsonProperty("productCode")
private String productCode;
@JsonProperty("toDate")
private String toDate;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("extnwcode")
public String getExtnwcode() {
return extnwcode;
}

@JsonProperty("extnwcode")
public void setExtnwcode(String extnwcode) {
this.extnwcode = extnwcode;
}

@JsonProperty("fromDate")
public String getFromDate() {
return fromDate;
}

@JsonProperty("fromDate")
public void setFromDate(String fromDate) {
this.fromDate = fromDate;
}

@JsonProperty("productCode")
public String getProductCode() {
return productCode;
}

@JsonProperty("productCode")
public void setProductCode(String productCode) {
this.productCode = productCode;
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


@Override
public String toString() {
	return "PassbookSearchDetails [fromDate=" + fromDate + ", toDate=" + toDate + ", extnwcode=" + extnwcode + ",productCode=" + productCode +", additionalProperties=" + additionalProperties + "]";
}


}
