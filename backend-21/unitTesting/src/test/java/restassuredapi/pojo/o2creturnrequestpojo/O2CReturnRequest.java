
package restassuredapi.pojo.o2creturnrequestpojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data"
})
public class O2CReturnRequest {
    @JsonProperty("data")
    private List<O2CReturnReqData> data = null;

    @JsonProperty("data")
    public List<O2CReturnReqData> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<O2CReturnReqData> data) {
        this.data = data;
    }
}