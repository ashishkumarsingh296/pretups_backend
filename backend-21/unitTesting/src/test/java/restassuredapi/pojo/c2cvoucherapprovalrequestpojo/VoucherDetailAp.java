
package restassuredapi.pojo.c2cvoucherapprovalrequestpojo;

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
    "fromSerialNum",
    "toSerialNum"
})
public class VoucherDetailAp {

    @JsonProperty("fromSerialNum")
    private String fromSerialNum;
    @JsonProperty("toSerialNum")
    private String toSerialNum;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("fromSerialNum")
    public String getFromSerialNum() {
        return fromSerialNum;
    }

    @JsonProperty("fromSerialNum")
    public void setFromSerialNum(String fromSerialNum) {
        this.fromSerialNum = fromSerialNum;
    }

    @JsonProperty("toSerialNum")
    public String getToSerialNum() {
        return toSerialNum;
    }

    @JsonProperty("toSerialNum")
    public void setToSerialNum(String toSerialNum) {
        this.toSerialNum = toSerialNum;
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
