
package restassuredapi.pojo.c2cstockapprovalrequestpojo;

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
    "date",
    "extnwcode",
    "msisdn",
    "pin",
    "loginid",
    "password",
    "extcode",
    "txnid",
    "products",
    "refnumber",
    "paymentdetails",
    "currentstatus",
    "status",
    "remarks",
    "language1"
})
public class DataApproval {

    @JsonProperty("date")
    private String date;
    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("loginid")
    private String loginid;
    @JsonProperty("password")
    private String password;
    @JsonProperty("extcode")
    private String extcode;
    @JsonProperty("txnid")
    private String txnid;
    @JsonProperty("products")
    private List<ProductApproval> products = null;
    @JsonProperty("refnumber")
    private String refnumber;
    @JsonProperty("paymentdetails")
    private List<PaymentdetailApproval> paymentdetails = null;
    @JsonProperty("currentstatus")
    private String currentstatus;
    @JsonProperty("status")
    private String status;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("language1")
    private String language1;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
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

    @JsonProperty("loginid")
    public String getLoginid() {
        return loginid;
    }

    @JsonProperty("loginid")
    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("extcode")
    public String getExtcode() {
        return extcode;
    }

    @JsonProperty("extcode")
    public void setExtcode(String extcode) {
        this.extcode = extcode;
    }

    @JsonProperty("txnid")
    public String getTxnid() {
        return txnid;
    }

    @JsonProperty("txnid")
    public void setTxnid(String txnid) {
        this.txnid = txnid;
    }

    @JsonProperty("products")
    public List<ProductApproval> getProducts() {
        return products;
    }

    @JsonProperty("products")
    public void setProducts(List<ProductApproval> products) {
        this.products = products;
    }

    @JsonProperty("refnumber")
    public String getRefnumber() {
        return refnumber;
    }

    @JsonProperty("refnumber")
    public void setRefnumber(String refnumber) {
        this.refnumber = refnumber;
    }

    @JsonProperty("paymentdetails")
    public List<PaymentdetailApproval> getPaymentdetails() {
        return paymentdetails;
    }

    @JsonProperty("paymentdetails")
    public void setPaymentdetails(List<PaymentdetailApproval> paymentdetails) {
        this.paymentdetails = paymentdetails;
    }

    @JsonProperty("currentstatus")
    public String getCurrentstatus() {
        return currentstatus;
    }

    @JsonProperty("currentstatus")
    public void setCurrentstatus(String currentstatus) {
        this.currentstatus = currentstatus;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
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
