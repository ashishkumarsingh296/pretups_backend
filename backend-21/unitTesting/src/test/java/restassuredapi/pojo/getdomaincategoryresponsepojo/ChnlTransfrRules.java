
package restassuredapi.pojo.getdomaincategoryresponsepojo;

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
    "domainCode",
    "domainCodeName",
    "categoryList"
})
public class ChnlTransfrRules {

    @JsonProperty("domainCode")
    private String domainCode;
    @JsonProperty("domainCodeName")
    private String domainCodeName;
    @JsonProperty("categoryList")
    private List<CategoryList> categoryList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("domainCode")
    public String getDomainCode() {
        return domainCode;
    }

    @JsonProperty("domainCode")
    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    @JsonProperty("domainCodeName")
    public String getDomainCodeName() {
        return domainCodeName;
    }

    @JsonProperty("domainCodeName")
    public void setDomainCodeName(String domainCodeName) {
        this.domainCodeName = domainCodeName;
    }

    @JsonProperty("categoryList")
    public List<CategoryList> getCategoryList() {
        return categoryList;
    }

    @JsonProperty("categoryList")
    public void setCategoryList(List<CategoryList> categoryList) {
        this.categoryList = categoryList;
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
