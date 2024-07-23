
package restassuredapi.pojo.selfprofilethresholdresponsepojo;

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
    "userTransferCountsVO",
    "profileProductList",
    "subscriberOutCountFlag",
    "userVO",
    "unctrlTransferFlag"
})
public class DataObject {

    @JsonProperty("userTransferCountsVO")
    private UserTransferCountsVO userTransferCountsVO;
    @JsonProperty("profileProductList")
    private List<ProfileProductList> profileProductList = null;
    @JsonProperty("subscriberOutCountFlag")
    private Boolean subscriberOutCountFlag;
    @JsonProperty("userVO")
    private UserVO userVO;
    @JsonProperty("unctrlTransferFlag")
    private Boolean unctrlTransferFlag;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("userTransferCountsVO")
    public UserTransferCountsVO getUserTransferCountsVO() {
        return userTransferCountsVO;
    }

    @JsonProperty("userTransferCountsVO")
    public void setUserTransferCountsVO(UserTransferCountsVO userTransferCountsVO) {
        this.userTransferCountsVO = userTransferCountsVO;
    }

    @JsonProperty("profileProductList")
    public List<ProfileProductList> getProfileProductList() {
        return profileProductList;
    }

    @JsonProperty("profileProductList")
    public void setProfileProductList(List<ProfileProductList> profileProductList) {
        this.profileProductList = profileProductList;
    }

    @JsonProperty("subscriberOutCountFlag")
    public Boolean getSubscriberOutCountFlag() {
        return subscriberOutCountFlag;
    }

    @JsonProperty("subscriberOutCountFlag")
    public void setSubscriberOutCountFlag(Boolean subscriberOutCountFlag) {
        this.subscriberOutCountFlag = subscriberOutCountFlag;
    }

    @JsonProperty("userVO")
    public UserVO getUserVO() {
        return userVO;
    }

    @JsonProperty("userVO")
    public void setUserVO(UserVO userVO) {
        this.userVO = userVO;
    }

    @JsonProperty("unctrlTransferFlag")
    public Boolean getUnctrlTransferFlag() {
        return unctrlTransferFlag;
    }

    @JsonProperty("unctrlTransferFlag")
    public void setUnctrlTransferFlag(Boolean unctrlTransferFlag) {
        this.unctrlTransferFlag = unctrlTransferFlag;
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
