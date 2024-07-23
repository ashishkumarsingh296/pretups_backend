
package restassuredapi.pojo.c2cstockapprovalrequestpojo;

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
   
    "data"
})
public class C2CStockApprovalRequestPojo {

   
    @JsonProperty("data")
    private DataApproval data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

   
    @JsonProperty("data")
    public DataApproval getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(DataApproval data) {
        this.data = data;
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
