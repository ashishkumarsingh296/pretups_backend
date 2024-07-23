package restassuredapi.pojo.o2cinitiateoptreqpojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data"
})
public class O2CInitiateByOptRequest {
    @JsonProperty("data")
    private List<O2CInitiateByOptReqData> data = null;

    @JsonProperty("data")
    public List<O2CInitiateByOptReqData> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<O2CInitiateByOptReqData> data) {
        this.data = data;
    }
}