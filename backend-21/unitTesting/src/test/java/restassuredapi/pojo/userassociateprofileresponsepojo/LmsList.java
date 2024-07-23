
package restassuredapi.pojo.userassociateprofileresponsepojo;

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
    "lmsprofilecode",
    "lmsprofilename"
})
public class LmsList {

    @JsonProperty("lmsprofilecode")
    private String lmsprofilecode;
    @JsonProperty("lmsprofilename")
    private String lmsprofilename;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("lmsprofilecode")
    public String getLmsprofilecode() {
        return lmsprofilecode;
    }

    @JsonProperty("lmsprofilecode")
    public void setLmsprofilecode(String lmsprofilecode) {
        this.lmsprofilecode = lmsprofilecode;
    }

    @JsonProperty("lmsprofilename")
    public String getLmsprofilename() {
        return lmsprofilename;
    }

    @JsonProperty("lmsprofilename")
    public void setLmsprofilename(String lmsprofilename) {
        this.lmsprofilename = lmsprofilename;
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
