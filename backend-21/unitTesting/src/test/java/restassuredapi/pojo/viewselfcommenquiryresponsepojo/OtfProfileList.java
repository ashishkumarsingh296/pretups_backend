
package restassuredapi.pojo.viewselfcommenquiryresponsepojo;

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
    "otfProfileVO",
    "slabsList"
})
public class OtfProfileList {

    @JsonProperty("otfProfileVO")
    private OtfProfileVO otfProfileVO;
    @JsonProperty("slabsList")
    private List<SlabsList_> slabsList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("otfProfileVO")
    public OtfProfileVO getOtfProfileVO() {
        return otfProfileVO;
    }

    @JsonProperty("otfProfileVO")
    public void setOtfProfileVO(OtfProfileVO otfProfileVO) {
        this.otfProfileVO = otfProfileVO;
    }

    @JsonProperty("slabsList")
    public List<SlabsList_> getSlabsList() {
        return slabsList;
    }

    @JsonProperty("slabsList")
    public void setSlabsList(List<SlabsList_> slabsList) {
        this.slabsList = slabsList;
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
