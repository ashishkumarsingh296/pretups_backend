
package restassuredapi.pojo.c2cvoucherapprovalrequestpojo;

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
    "extcode",
    "loginid",
    "language2",
    "language1",
    "extnwcode",
    "type",
    "transferId",
    "paymentinstcode",
    "paymentinstdate",
    "paymentinstnum",
    "voucherDetails",
    "password",
    "pin",
    "msisdn",
    "remarks",
    "status"
})
public class DataAp {

    @JsonProperty("extcode")
    private String extcode;
    @JsonProperty("loginid")
    private String loginid;
    @JsonProperty("language2")
    private String language2;
    @JsonProperty("language1")
    private String language1;
    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("type")
    private String type;
    @JsonProperty("transferId")
    private String transferId;
    @JsonProperty("paymentinstcode")
    private String paymentinstcode;
    @JsonProperty("paymentinstdate")
    private String paymentinstdate;
    @JsonProperty("paymentinstnum")
    private String paymentinstnum;
    @JsonProperty("voucherDetails")
    private List<VoucherDetailAp> voucherDetails = null;
    @JsonProperty("password")
    private String password;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("status")
    private String status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("extcode")
    public String getExtcode() {
        return extcode;
    }

    @JsonProperty("extcode")
    public void setExtcode(String extcode) {
        this.extcode = extcode;
    }

    @JsonProperty("loginid")
    public String getLoginid() {
        return loginid;
    }

    @JsonProperty("loginid")
    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    @JsonProperty("language2")
    public String getLanguage2() {
        return language2;
    }

    @JsonProperty("language2")
    public void setLanguage2(String language2) {
        this.language2 = language2;
    }

    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
    }

    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("transferId")
    public String getTransferId() {
        return transferId;
    }

    @JsonProperty("transferId")
    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    @JsonProperty("paymentinstcode")
    public String getPaymentinstcode() {
        return paymentinstcode;
    }

    @JsonProperty("paymentinstcode")
    public void setPaymentinstcode(String paymentinstcode) {
        this.paymentinstcode = paymentinstcode;
    }

    @JsonProperty("paymentinstdate")
    public String getPaymentinstdate() {
        return paymentinstdate;
    }

    @JsonProperty("paymentinstdate")
    public void setPaymentinstdate(String paymentinstdate) {
        this.paymentinstdate = paymentinstdate;
    }

    @JsonProperty("paymentinstnum")
    public String getPaymentinstnum() {
        return paymentinstnum;
    }

    @JsonProperty("paymentinstnum")
    public void setPaymentinstnum(String paymentinstnum) {
        this.paymentinstnum = paymentinstnum;
    }

    @JsonProperty("voucherDetails")
    public List<VoucherDetailAp> getVoucherDetails() {
        return voucherDetails;
    }

    @JsonProperty("voucherDetails")
    public void setVoucherDetails(List<VoucherDetailAp> voucherDetails) {
        this.voucherDetails = voucherDetails;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
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
