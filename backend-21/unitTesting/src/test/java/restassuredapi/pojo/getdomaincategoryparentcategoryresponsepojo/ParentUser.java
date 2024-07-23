
package restassuredapi.pojo.getdomaincategoryparentcategoryresponsepojo;

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
    "parentUserName",
    "parentMsisdn",
    "parentUserId"
})
public class ParentUser {

    @JsonProperty("parentUserName")
    private String parentUserName;
    @JsonProperty("parentMsisdn")
    private String parentMsisdn;
    @JsonProperty("parentUserId")
    private String parentUserId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("parentUserName")
    public String getParentUserName() {
        return parentUserName;
    }

    @JsonProperty("parentUserName")
    public void setParentUserName(String parentUserName) {
        this.parentUserName = parentUserName;
    }

    @JsonProperty("parentMsisdn")
    public String getParentMsisdn() {
        return parentMsisdn;
    }

    @JsonProperty("parentMsisdn")
    public void setParentMsisdn(String parentMsisdn) {
        this.parentMsisdn = parentMsisdn;
    }

    @JsonProperty("parentUserId")
    public String getParentUserId() {
        return parentUserId;
    }

    @JsonProperty("parentUserId")
    public void setParentUserId(String parentUserId) {
        this.parentUserId = parentUserId;
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
