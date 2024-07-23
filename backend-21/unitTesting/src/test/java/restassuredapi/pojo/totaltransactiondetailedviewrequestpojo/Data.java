
package restassuredapi.pojo.totaltransactiondetailedviewrequestpojo;
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
"extnwcode",
"fromDate",
"language1",
"language2",
"toDate"
})
public class Data {

@JsonProperty("extnwcode")
private String extnwcode;
@JsonProperty("fromDate")
private String fromDate;
@JsonProperty("language1")
private String language1;
@JsonProperty("language2")
private String language2;
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

@JsonProperty("language1")
public String getLanguage1() {
return language1;
}

@JsonProperty("language1")
public void setLanguage1(String language1) {
this.language1 = language1;
}

@JsonProperty("language2")
public String getLanguage2() {
return language2;
}

@JsonProperty("language2")
public void setLanguage2(String language2) {
this.language2 = language2;
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
