
package restassuredapi.pojo.o2cvoucherinitiaterequestpojo;

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
    "paymentdate",
    "paymentinstnumber",
    "paymenttype",
    "paymentgatewaytype"
})
public class PaymentDetail {

    @JsonProperty("paymentdate")
    private String paymentdate;
    @JsonProperty("paymentinstnumber")
    private String paymentinstnumber;
    @JsonProperty("paymenttype")
    private String paymenttype;
    @JsonProperty("paymentgatewaytype")
    private String paymentgatewaytype;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("paymentdate")
    public String getPaymentdate() {
        return paymentdate;
    }

    @JsonProperty("paymentdate")
    public void setPaymentdate(String paymentdate) {
        this.paymentdate = paymentdate;
    }

    @JsonProperty("paymentinstnumber")
    public String getPaymentinstnumber() {
        return paymentinstnumber;
    }

    @JsonProperty("paymentinstnumber")
    public void setPaymentinstnumber(String paymentinstnumber) {
        this.paymentinstnumber = paymentinstnumber;
    }

    @JsonProperty("paymenttype")
    public String getPaymenttype() {
        return paymenttype;
    }

    @JsonProperty("paymenttype")
    public void setPaymenttype(String paymenttype) {
        this.paymenttype = paymenttype;
    }

    @JsonProperty("paymentgatewaytype")
    public String getPaymentgatewaytype() {
		return paymentgatewaytype;
	}

    @JsonProperty("paymentgatewaytype")
	public void setPaymentgatewaytype(String paymentgatewaytype) {
		this.paymentgatewaytype = paymentgatewaytype;
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
