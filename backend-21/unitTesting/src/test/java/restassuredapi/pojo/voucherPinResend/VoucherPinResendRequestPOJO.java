package restassuredapi.pojo.voucherPinResend;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import restassuredapi.pojo.voucherPinResend.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data"
})

public class VoucherPinResendRequestPOJO {

	  @JsonProperty("data")
	    private DataVoucher data;
	    @JsonIgnore
	    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	    @JsonProperty("data")
	    public DataVoucher getData() {
	        return data;
	    }

	    @JsonProperty("data")
	    public void setData(DataVoucher data) {
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
