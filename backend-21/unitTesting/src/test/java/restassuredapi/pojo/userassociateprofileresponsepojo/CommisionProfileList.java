
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
    "commprofilecode",
    "commprofilename"
})
public class CommisionProfileList {

    @JsonProperty("commprofilecode")
    private String commprofilecode;
    @JsonProperty("commprofilename")
    private String commprofilename;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("commprofilecode")
    public String getCommprofilecode() {
        return commprofilecode;
    }

    @JsonProperty("commprofilecode")
    public void setCommprofilecode(String commprofilecode) {
        this.commprofilecode = commprofilecode;
    }

    @JsonProperty("commprofilename")
    public String getCommprofilename() {
        return commprofilename;
    }

    @JsonProperty("commprofilename")
    public void setCommprofilename(String commprofilename) {
        this.commprofilename = commprofilename;
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
