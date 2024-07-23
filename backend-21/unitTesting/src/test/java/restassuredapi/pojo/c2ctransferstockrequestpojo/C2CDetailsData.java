package restassuredapi.pojo.c2ctransferstockrequestpojo;

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
    "refnumber",
    "msisdn2",
    "loginid2",
    "extcode2",
    "paymentdetails",
    "products",
    "remarks",
    "language1",
    "extrefnum"
})
public class C2CDetailsData {

    @JsonProperty("refnumber")
    private String refnumber;
    @JsonProperty("msisdn2")
    private String msisdn2;
    @JsonProperty("loginid2")
    private String loginid2;
    @JsonProperty("extcode2")
    private String extcode2;
    @JsonProperty("paymentdetails")
    private List<Paymentdetail> paymentdetails = null;
    @JsonProperty("products")
    private List<Product> products = null;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("language1")
    private String language1;
    @JsonProperty("extrefnum")
    private String extrefnum;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("refnumber")
    public String getRefnumber() {
        return refnumber;
    }

    @JsonProperty("refnumber")
    public void setRefnumber(String refnumber) {
        this.refnumber = refnumber;
    }

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

    @JsonProperty("paymentdetails")
    public List<Paymentdetail> getPaymentdetails() {
        return paymentdetails;
    }

    @JsonProperty("paymentdetails")
    public void setPaymentdetails(List<Paymentdetail> paymentdetails) {
        this.paymentdetails = paymentdetails;
    }

    @JsonProperty("products")
    public List<Product> getProducts() {
        return products;
    }

    @JsonProperty("products")
    public void setProducts(List<Product> products) {
        this.products = products;
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

    @JsonProperty("extrefnum")
    public String getExtrefnum() {
        return extrefnum;
    }

    @JsonProperty("extrefnum")
    public void setExtrefnum(String extrefnum) {
        this.extrefnum = extrefnum;
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
