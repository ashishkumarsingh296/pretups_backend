package restassuredapi.pojo.bulko2capprovallistresponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"additionalProp1",
"additionalProp2",
"additionalProp3"
})
public class BulkApprovalList {

@JsonProperty("additionalProp1")
private List<AdditionalProp1> additionalProp1 = null;
@JsonProperty("additionalProp2")
private List<AdditionalProp2> additionalProp2 = null;
@JsonProperty("additionalProp3")
private List<AdditionalProp3> additionalProp3 = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("additionalProp1")
public List<AdditionalProp1> getAdditionalProp1() {
return additionalProp1;
}

@JsonProperty("additionalProp1")
public void setAdditionalProp1(List<AdditionalProp1> additionalProp1) {
this.additionalProp1 = additionalProp1;
}

@JsonProperty("additionalProp2")
public List<AdditionalProp2> getAdditionalProp2() {
return additionalProp2;
}

@JsonProperty("additionalProp2")
public void setAdditionalProp2(List<AdditionalProp2> additionalProp2) {
this.additionalProp2 = additionalProp2;
}

@JsonProperty("additionalProp3")
public List<AdditionalProp3> getAdditionalProp3() {
return additionalProp3;
}

@JsonProperty("additionalProp3")
public void setAdditionalProp3(List<AdditionalProp3> additionalProp3) {
this.additionalProp3 = additionalProp3;
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

