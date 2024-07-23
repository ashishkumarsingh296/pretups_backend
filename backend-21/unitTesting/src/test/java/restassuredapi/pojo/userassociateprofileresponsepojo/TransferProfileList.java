
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
    "transferprofilecode",
    "transferprofilename"
})
public class TransferProfileList {

    @JsonProperty("transferprofilecode")
    private String transferprofilecode;
    @JsonProperty("transferprofilename")
    private String transferprofilename;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("transferprofilecode")
    public String getTransferprofilecode() {
        return transferprofilecode;
    }

    @JsonProperty("transferprofilecode")
    public void setTransferprofilecode(String transferprofilecode) {
        this.transferprofilecode = transferprofilecode;
    }

    @JsonProperty("transferprofilename")
    public String getTransferprofilename() {
        return transferprofilename;
    }

    @JsonProperty("transferprofilename")
    public void setTransferprofilename(String transferprofilename) {
        this.transferprofilename = transferprofilename;
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
