
package restassuredapi.pojo.getdomaincategoryparentcategoryresponsepojo;

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
    "parentCategory",
    "parentUser"
})
public class ParentList {

    @JsonProperty("parentCategory")
    private String parentCategory;
    @JsonProperty("parentUser")
    private List<ParentUser> parentUser = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("parentCategory")
    public String getParentCategory() {
        return parentCategory;
    }

    @JsonProperty("parentCategory")
    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    @JsonProperty("parentUser")
    public List<ParentUser> getParentUser() {
        return parentUser;
    }

    @JsonProperty("parentUser")
    public void setParentUser(List<ParentUser> parentUser) {
        this.parentUser = parentUser;
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
