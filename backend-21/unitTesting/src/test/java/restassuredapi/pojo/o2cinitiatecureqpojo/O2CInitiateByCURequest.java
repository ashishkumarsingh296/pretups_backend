package restassuredapi.pojo.o2cinitiatecureqpojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data"
})
public class O2CInitiateByCURequest {
    @JsonProperty("data")
    private List<O2CInitiateByCUReqData> data = null;

    @JsonProperty("data")
    public List<O2CInitiateByCUReqData> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<O2CInitiateByCUReqData> data) {
        this.data = data;
    }
}