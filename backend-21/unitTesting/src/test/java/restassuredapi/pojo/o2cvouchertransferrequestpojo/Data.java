
package restassuredapi.pojo.o2cvouchertransferrequestpojo;

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
	"msisdn2",
	"loginid2",
	"extcode2",
    "language",
    "paymentDetails",
    "pin",
    "refnumber",
    "remarks",
    "voucherDetails"
})
public class Data {
	
	@JsonProperty("msisdn2")
	private String msisdn2;
	@JsonProperty("loginid2")
    private String loginid2;
	@JsonProperty("extcode2")
    private String extcode2;
    @JsonProperty("language")
    private String language;
    @JsonProperty("paymentDetails")
    private List<PaymentDetail> paymentDetails = null;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("refnumber")
    private String refnumber;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("voucherDetails")
    private List<VoucherDetail> voucherDetails = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("msisdn2")
    public String getMsisdn2() {
		return msisdn2;
	}

    @JsonProperty("msisdn2")
	public void setMsisdn2(String msisdn2) {
		this.msisdn2 = msisdn2;
	}

    @JsonProperty("loginid2")
	public String getLoginid2() {
		return loginid2;
	}

    @JsonProperty("loginid2")
	public void setLoginid2(String loginid2) {
		this.loginid2 = loginid2;
	}

    @JsonProperty("extcode2")
	public String getExtcode2() {
		return extcode2;
	}

    @JsonProperty("extcode2")
	public void setExtcode2(String extcode2) {
		this.extcode2 = extcode2;
	}
    
	@JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("paymentDetails")
    public List<PaymentDetail> getPaymentDetails() {
        return paymentDetails;
    }

    @JsonProperty("paymentDetails")
    public void setPaymentDetails(List<PaymentDetail> paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }

    @JsonProperty("refnumber")
    public String getRefnumber() {
        return refnumber;
    }

    @JsonProperty("refnumber")
    public void setRefnumber(String refnumber) {
        this.refnumber = refnumber;
    }

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("voucherDetails")
    public List<VoucherDetail> getVoucherDetails() {
        return voucherDetails;
    }

    @JsonProperty("voucherDetails")
    public void setVoucherDetails(List<VoucherDetail> voucherDetails) {
        this.voucherDetails = voucherDetails;
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
