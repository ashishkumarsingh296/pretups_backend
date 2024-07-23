
package restassuredapi.pojo.pinpasswordhistorysearchresponsepojo;

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
    "modifiedOn",
    "moidifiedBy",
    "msisdnOrLoginID",
    "userName"
})
@Generated("jsonschema2pojo")
public class PinPassHistSearchVO {

    @JsonProperty("modifiedOn")
    private String modifiedOn;
    @JsonProperty("moidifiedBy")
    private String moidifiedBy;
    @JsonProperty("msisdnOrLoginID")
    private String msisdnOrLoginID;
    @JsonProperty("userName")
    private String userName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("modifiedOn")
    public String getModifiedOn() {
        return modifiedOn;
    }

    @JsonProperty("modifiedOn")
    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @JsonProperty("moidifiedBy")
    public String getMoidifiedBy() {
        return moidifiedBy;
    }

    @JsonProperty("moidifiedBy")
    public void setMoidifiedBy(String moidifiedBy) {
        this.moidifiedBy = moidifiedBy;
    }

    @JsonProperty("msisdnOrLoginID")
    public String getMsisdnOrLoginID() {
        return msisdnOrLoginID;
    }

    @JsonProperty("msisdnOrLoginID")
    public void setMsisdnOrLoginID(String msisdnOrLoginID) {
        this.msisdnOrLoginID = msisdnOrLoginID;
    }

    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
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
