
package restassuredapi.pojo.channelUserListResponsepojo;

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
public class RoleHitTimeMap {

    @JsonProperty("additionalProp1")
    private AdditionalProp1__ additionalProp1;
    @JsonProperty("additionalProp2")
    private AdditionalProp2__ additionalProp2;
    @JsonProperty("additionalProp3")
    private AdditionalProp3__ additionalProp3;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("additionalProp1")
    public AdditionalProp1__ getAdditionalProp1() {
        return additionalProp1;
    }

    @JsonProperty("additionalProp1")
    public void setAdditionalProp1(AdditionalProp1__ additionalProp1) {
        this.additionalProp1 = additionalProp1;
    }

    @JsonProperty("additionalProp2")
    public AdditionalProp2__ getAdditionalProp2() {
        return additionalProp2;
    }

    @JsonProperty("additionalProp2")
    public void setAdditionalProp2(AdditionalProp2__ additionalProp2) {
        this.additionalProp2 = additionalProp2;
    }

    @JsonProperty("additionalProp3")
    public AdditionalProp3__ getAdditionalProp3() {
        return additionalProp3;
    }

    @JsonProperty("additionalProp3")
    public void setAdditionalProp3(AdditionalProp3__ additionalProp3) {
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
