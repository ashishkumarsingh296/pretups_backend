
package restassuredapi.pojo.addvouchercardgrouprequestpojo;

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
    "bonusName",
    "type",
    "bonusValue",
    "bonusValidity",
    "multFactor"
})
public class BonusAccList {

    @JsonProperty("bonusName")
    private String bonusName;
    @JsonProperty("type")
    private String type;
    @JsonProperty("bonusValue")
    private String bonusValue;
    @JsonProperty("bonusValidity")
    private String bonusValidity;
    @JsonProperty("multFactor")
    private String multFactor;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("bonusName")
    public String getBonusName() {
        return bonusName;
    }

    @JsonProperty("bonusName")
    public void setBonusName(String bonusName) {
        this.bonusName = bonusName;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("bonusValue")
    public String getBonusValue() {
        return bonusValue;
    }

    @JsonProperty("bonusValue")
    public void setBonusValue(String bonusValue) {
        this.bonusValue = bonusValue;
    }

    @JsonProperty("bonusValidity")
    public String getBonusValidity() {
        return bonusValidity;
    }

    @JsonProperty("bonusValidity")
    public void setBonusValidity(String bonusValidity) {
        this.bonusValidity = bonusValidity;
    }

    @JsonProperty("multFactor")
    public String getMultFactor() {
        return multFactor;
    }

    @JsonProperty("multFactor")
    public void setMultFactor(String multFactor) {
        this.multFactor = multFactor;
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
