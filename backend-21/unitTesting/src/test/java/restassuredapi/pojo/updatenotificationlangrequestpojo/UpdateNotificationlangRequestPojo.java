
package restassuredapi.pojo.updatenotificationlangrequestpojo;

import java.util.HashMap;
import java.util.List;
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
    "changedPhoneLanguageList",
    "userLoginID"
})
@Generated("jsonschema2pojo")
public class UpdateNotificationlangRequestPojo {

    @JsonProperty("changedPhoneLanguageList")
    private List<ChangedPhoneLanguage> changedPhoneLanguageList = null;
    @JsonProperty("userLoginID")
    private String userLoginID;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("changedPhoneLanguageList")
    public List<ChangedPhoneLanguage> getChangedPhoneLanguageList() {
        return changedPhoneLanguageList;
    }

    @JsonProperty("changedPhoneLanguageList")
    public void setChangedPhoneLanguageList(List<ChangedPhoneLanguage> changedPhoneLanguageList) {
        this.changedPhoneLanguageList = changedPhoneLanguageList;
    }

    @JsonProperty("userLoginID")
    public String getUserLoginID() {
        return userLoginID;
    }

    @JsonProperty("userLoginID")
    public void setUserLoginID(String userLoginID) {
        this.userLoginID = userLoginID;
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
