
package restassuredapi.pojo.vouchercardgroupchangestatusrequestpojo;

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
    "moduleCode",
    "networkCode",
    "cardGroupSetList"
})
public class Data {

    @JsonProperty("moduleCode")
    private String moduleCode;
    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("cardGroupSetList")
    private List<CardGroupSetList> cardGroupSetList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("moduleCode")
    public String getModuleCode() {
        return moduleCode;
    }

    @JsonProperty("moduleCode")
    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("cardGroupSetList")
    public List<CardGroupSetList> getCardGroupSetList() {
        return cardGroupSetList;
    }

    @JsonProperty("cardGroupSetList")
    public void setCardGroupSetList(List<CardGroupSetList> cardGroupSetList) {
        this.cardGroupSetList = cardGroupSetList;
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
