
package restassuredapi.pojo.fetchusernameautosearchresponsepojo;

import java.util.HashMap;
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
    "msisdn",
    "userID",
    "userName",
    "userNameMsisdn"
})
@Generated("jsonschema2pojo")
public class User {

    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("userID")
    private String userID;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("userNameMsisdn")
    private String userNameMsisdn;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("userID")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("userNameMsisdn")
    public String getUserNameMsisdn() {
        return userNameMsisdn;
    }

    @JsonProperty("userNameMsisdn")
    public void setUserNameMsisdn(String userNameMsisdn) {
        this.userNameMsisdn = userNameMsisdn;
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
