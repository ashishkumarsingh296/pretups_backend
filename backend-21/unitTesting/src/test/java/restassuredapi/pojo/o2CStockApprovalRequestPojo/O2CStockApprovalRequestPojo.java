
package restassuredapi.pojo.o2CStockApprovalRequestPojo;

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
    "o2cStockAppRequests"
})
public class O2CStockApprovalRequestPojo {

    @JsonProperty("o2cStockAppRequests")
    private List<O2cStockAppRequest> o2cStockAppRequests = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("o2cStockAppRequests")
    public List<O2cStockAppRequest> getO2cStockAppRequests() {
        return o2cStockAppRequests;
    }

    @JsonProperty("o2cStockAppRequests")
    public void setO2cStockAppRequests(List<O2cStockAppRequest> o2cStockAppRequests) {
        this.o2cStockAppRequests = o2cStockAppRequests;
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
