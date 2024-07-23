
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
    "commissionProfileProductVO",
    "slabsList"
})
public class CommissionList {

    @JsonProperty("commissionProfileProductVO")
    private CommissionProfileProductVO commissionProfileProductVO;
    @JsonProperty("slabsList")
    private List<SlabsList> slabsList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("commissionProfileProductVO")
    public CommissionProfileProductVO getCommissionProfileProductVO() {
        return commissionProfileProductVO;
    }

    @JsonProperty("commissionProfileProductVO")
    public void setCommissionProfileProductVO(CommissionProfileProductVO commissionProfileProductVO) {
        this.commissionProfileProductVO = commissionProfileProductVO;
    }

    @JsonProperty("slabsList")
    public List<SlabsList> getSlabsList() {
        return slabsList;
    }

    @JsonProperty("slabsList")
    public void setSlabsList(List<SlabsList> slabsList) {
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
