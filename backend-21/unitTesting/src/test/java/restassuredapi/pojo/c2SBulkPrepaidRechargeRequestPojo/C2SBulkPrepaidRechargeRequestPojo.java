
package restassuredapi.pojo.c2SBulkPrepaidRechargeRequestPojo;

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
public class C2SBulkPrepaidRechargeRequestPojo {

    @JsonProperty("data")
    private restassuredapi.pojo.c2SBulkInternetRechargerRequestPojo.Data data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("data")
    public restassuredapi.pojo.c2SBulkInternetRechargerRequestPojo.Data getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(restassuredapi.pojo.c2SBulkInternetRechargerRequestPojo.Data data2) {
        this.data = data2;
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
