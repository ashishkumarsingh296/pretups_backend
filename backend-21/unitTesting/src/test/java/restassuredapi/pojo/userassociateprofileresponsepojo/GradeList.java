
package restassuredapi.pojo.userassociateprofileresponsepojo;

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
    "gradecode",
    "gradename",
    "commisionProfileList"
})
public class GradeList {

    @JsonProperty("gradecode")
    private String gradecode;
    @JsonProperty("gradename")
    private String gradename;
    @JsonProperty("commisionProfileList")
    private List<CommisionProfileList> commisionProfileList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("gradecode")
    public String getGradecode() {
        return gradecode;
    }

    @JsonProperty("gradecode")
    public void setGradecode(String gradecode) {
        this.gradecode = gradecode;
    }

    @JsonProperty("gradename")
    public String getGradename() {
        return gradename;
    }

    @JsonProperty("gradename")
    public void setGradename(String gradename) {
        this.gradename = gradename;
    }

    @JsonProperty("commisionProfileList")
    public List<CommisionProfileList> getCommisionProfileList() {
        return commisionProfileList;
    }

    @JsonProperty("commisionProfileList")
    public void setCommisionProfileList(List<CommisionProfileList> commisionProfileList) {
        this.commisionProfileList = commisionProfileList;
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
