
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
    "transferrulecode",
    "transferrulename"
})
public class TransferRuleTypeList {

    @JsonProperty("transferrulecode")
    private String transferrulecode;
    @JsonProperty("transferrulename")
    private String transferrulename;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("transferrulecode")
    public String getTransferrulecode() {
        return transferrulecode;
    }

    @JsonProperty("transferrulecode")
    public void setTransferrulecode(String transferrulecode) {
        this.transferrulecode = transferrulecode;
    }

    @JsonProperty("transferrulename")
    public String getTransferrulename() {
        return transferrulename;
    }

    @JsonProperty("transferrulename")
    public void setTransferrulename(String transferrulename) {
        this.transferrulename = transferrulename;
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
