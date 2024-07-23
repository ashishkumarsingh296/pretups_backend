
package restassuredapi.pojo.redispreferencecacheresponsepojo;

import java.util.HashMap;
import javax.annotation.Generated;
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
@Generated("jsonschema2pojo")
public class Map {

    @JsonProperty("additionalProp1")
    private AdditionalProp1 additionalProp1;
    @JsonProperty("additionalProp2")
    private AdditionalProp2 additionalProp2;
    @JsonProperty("additionalProp3")
    private AdditionalProp3 additionalProp3;
    @JsonIgnore
    private java.util.Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("additionalProp1")
    public AdditionalProp1 getAdditionalProp1() {
        return additionalProp1;
    }

    @JsonProperty("additionalProp1")
    public void setAdditionalProp1(AdditionalProp1 additionalProp1) {
        this.additionalProp1 = additionalProp1;
    }

    @JsonProperty("additionalProp2")
    public AdditionalProp2 getAdditionalProp2() {
        return additionalProp2;
    }

    @JsonProperty("additionalProp2")
    public void setAdditionalProp2(AdditionalProp2 additionalProp2) {
        this.additionalProp2 = additionalProp2;
    }

    @JsonProperty("additionalProp3")
    public AdditionalProp3 getAdditionalProp3() {
        return additionalProp3;
    }

    @JsonProperty("additionalProp3")
    public void setAdditionalProp3(AdditionalProp3 additionalProp3) {
        this.additionalProp3 = additionalProp3;
    }

    @JsonAnyGetter
    public java.util.Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
