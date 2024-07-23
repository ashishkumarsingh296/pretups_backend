
package restassuredapi.pojo.processchanneluserrequestpojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "extnwcode",
    "vouchertype",
    "msisdn",
    "pin",
    "msisdn2",
    "vouchersegment",
    "amount",
    "voucherprofile",
    "quantity",
    "language1",
    "language2",
    "selector",
    "loginid",
    "password",
    "extcode"
})
public class Data {

    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("vouchertype")
    private String vouchertype;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("msisdn2")
    private String msisdn2;
    @JsonProperty("vouchersegment")
    private String vouchersegment;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("voucherprofile")
    private String voucherprofile;
    @JsonProperty("quantity")
    private String quantity;
    @JsonProperty("language1")
    private String language1;
    @JsonProperty("language2")
    private String language2;
    @JsonProperty("selector")
    private String selector;
    @JsonProperty("loginid")
    private String loginid;
    @JsonProperty("password")
    private String password;
    @JsonProperty("extcode")
    private String extcode;

    public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getExtcode() {
		return extcode;
	}

	public void setExtcode(String extcode) {
		this.extcode = extcode;
	}

	@JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }

    @JsonProperty("vouchertype")
    public String getVouchertype() {
        return vouchertype;
    }

    @JsonProperty("vouchertype")
    public void setVouchertype(String vouchertype) {
        this.vouchertype = vouchertype;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }

    @JsonProperty("msisdn2")
    public String getMsisdn2() {
        return msisdn2;
    }

    @JsonProperty("msisdn2")
    public void setMsisdn2(String msisdn2) {
        this.msisdn2 = msisdn2;
    }

    @JsonProperty("vouchersegment")
    public String getVouchersegment() {
        return vouchersegment;
    }

    @JsonProperty("vouchersegment")
    public void setVouchersegment(String vouchersegment) {
        this.vouchersegment = vouchersegment;
    }

    @JsonProperty("amount")
    public String getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    @JsonProperty("voucherprofile")
    public String getVoucherprofile() {
        return voucherprofile;
    }

    @JsonProperty("voucherprofile")
    public void setVoucherprofile(String voucherprofile) {
        this.voucherprofile = voucherprofile;
    }

    @JsonProperty("quantity")
    public String getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
    }

    @JsonProperty("language2")
    public String getLanguage2() {
        return language2;
    }

    @JsonProperty("language2")
    public void setLanguage2(String language2) {
        this.language2 = language2;
    }

    @JsonProperty("selector")
    public String getSelector() {
        return selector;
    }

    @JsonProperty("selector")
    public void setSelector(String selector) {
        this.selector = selector;
    }

}
