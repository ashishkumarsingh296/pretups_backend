
package restassuredapi.pojo.addvouchercardgrouprequestpojo;

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
    "cardGroupDetails",
    "cardGroupList"
})
public class Data {

    @JsonProperty("cardGroupDetails")
    private CardGroupDetails cardGroupDetails;
    @JsonProperty("cardGroupList")
    private List<CardGroupList> cardGroupList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cardGroupDetails")
    public CardGroupDetails getCardGroupDetails() {
        return cardGroupDetails;
    }

    @JsonProperty("cardGroupDetails")
    public void setCardGroupDetails(CardGroupDetails cardGroupDetails) {
        this.cardGroupDetails = cardGroupDetails;
    }

    @JsonProperty("cardGroupList")
    public List<CardGroupList> getCardGroupList() {
        return cardGroupList;
    }

    @JsonProperty("cardGroupList")
    public void setCardGroupList(List<CardGroupList> cardGroupList) {
        this.cardGroupList = cardGroupList;
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
