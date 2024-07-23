
package restassuredapi.pojo.o2CSearchDetailsRequestpojo;

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
    "channelDomain",
    "channelOwnerCategory",
    "channelOwnerName",
    "channelOwnerUserID",
    "channelUserID",
    "geoDomainCode",
    "userCategory"
})
public class Data {

    @JsonProperty("channelDomain")
    private String channelDomain;
    @JsonProperty("channelOwnerCategory")
    private String channelOwnerCategory;
    @JsonProperty("channelOwnerName")
    private String channelOwnerName;
    @JsonProperty("channelOwnerUserID")
    private String channelOwnerUserID;
    @JsonProperty("channelUserID")
    private String channelUserID;
    @JsonProperty("geoDomainCode")
    private String geoDomainCode;
    @JsonProperty("userCategory")
    private String userCategory;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("channelDomain")
    public String getChannelDomain() {
        return channelDomain;
    }

    @JsonProperty("channelDomain")
    public void setChannelDomain(String channelDomain) {
        this.channelDomain = channelDomain;
    }

    @JsonProperty("channelOwnerCategory")
    public String getChannelOwnerCategory() {
        return channelOwnerCategory;
    }

    @JsonProperty("channelOwnerCategory")
    public void setChannelOwnerCategory(String channelOwnerCategory) {
        this.channelOwnerCategory = channelOwnerCategory;
    }

    @JsonProperty("channelOwnerName")
    public String getChannelOwnerName() {
        return channelOwnerName;
    }

    @JsonProperty("channelOwnerName")
    public void setChannelOwnerName(String channelOwnerName) {
        this.channelOwnerName = channelOwnerName;
    }

    @JsonProperty("channelOwnerUserID")
    public String getChannelOwnerUserID() {
        return channelOwnerUserID;
    }

    @JsonProperty("channelOwnerUserID")
    public void setChannelOwnerUserID(String channelOwnerUserID) {
        this.channelOwnerUserID = channelOwnerUserID;
    }

    @JsonProperty("channelUserID")
    public String getChannelUserID() {
        return channelUserID;
    }

    @JsonProperty("channelUserID")
    public void setChannelUserID(String channelUserID) {
        this.channelUserID = channelUserID;
    }

    @JsonProperty("geoDomainCode")
    public String getGeoDomainCode() {
        return geoDomainCode;
    }

    @JsonProperty("geoDomainCode")
    public void setGeoDomainCode(String geoDomainCode) {
        this.geoDomainCode = geoDomainCode;
    }

    @JsonProperty("userCategory")
    public String getUserCategory() {
        return userCategory;
    }

    @JsonProperty("userCategory")
    public void setUserCategory(String userCategory) {
        this.userCategory = userCategory;
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
