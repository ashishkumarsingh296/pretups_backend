package restassuredapi.pojo.c2sreversalresponsepojo;
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
"additionalProp1",
"additionalProp2",
"additionalProp3"
})
public class FieldError {

@JsonProperty("additionalProp1")
private String additionalProp1;
@JsonProperty("additionalProp2")
private String additionalProp2;
@JsonProperty("additionalProp3")
private String additionalProp3;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("additionalProp1")
public String getAdditionalProp1() {
return additionalProp1;
}

@JsonProperty("additionalProp1")
public void setAdditionalProp1(String additionalProp1) {
this.additionalProp1 = additionalProp1;
}

@JsonProperty("additionalProp2")
public String getAdditionalProp2() {
return additionalProp2;
}

@JsonProperty("additionalProp2")
public void setAdditionalProp2(String additionalProp2) {
this.additionalProp2 = additionalProp2;
}

@JsonProperty("additionalProp3")
public String getAdditionalProp3() {
return additionalProp3;
}

@JsonProperty("additionalProp3")
public void setAdditionalProp3(String additionalProp3) {
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