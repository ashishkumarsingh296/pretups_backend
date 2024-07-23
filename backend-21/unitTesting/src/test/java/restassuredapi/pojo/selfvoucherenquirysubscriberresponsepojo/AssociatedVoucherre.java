
package restassuredapi.pojo.selfvoucherenquirysubscriberresponsepojo;

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
    "productname",
    "serialno",
    "vouchertype",
    "vouchersegment",
    "voucherdenominaton",
    "voucherpin",
    "userid"
})
public class AssociatedVoucherre {

    @JsonProperty("productname")
    private String productname;
    @JsonProperty("serialno")
    private String serialno;
    @JsonProperty("vouchertype")
    private String vouchertype;
    @JsonProperty("vouchersegment")
    private String vouchersegment;
    @JsonProperty("voucherdenominaton")
    private Integer voucherdenominaton;
    @JsonProperty("voucherpin")
    private String voucherpin;
    @JsonProperty("userid")
    private Object userid;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("productname")
    public String getProductname() {
        return productname;
    }

    @JsonProperty("productname")
    public void setProductname(String productname) {
        this.productname = productname;
    }

    @JsonProperty("serialno")
    public String getSerialno() {
        return serialno;
    }

    @JsonProperty("serialno")
    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    @JsonProperty("vouchertype")
    public String getVouchertype() {
        return vouchertype;
    }

    @JsonProperty("vouchertype")
    public void setVouchertype(String vouchertype) {
        this.vouchertype = vouchertype;
    }

    @JsonProperty("vouchersegment")
    public String getVouchersegment() {
        return vouchersegment;
    }

    @JsonProperty("vouchersegment")
    public void setVouchersegment(String vouchersegment) {
        this.vouchersegment = vouchersegment;
    }

    @JsonProperty("voucherdenominaton")
    public Integer getVoucherdenominaton() {
        return voucherdenominaton;
    }

    @JsonProperty("voucherdenominaton")
    public void setVoucherdenominaton(Integer voucherdenominaton) {
        this.voucherdenominaton = voucherdenominaton;
    }

    @JsonProperty("voucherpin")
    public String getVoucherpin() {
        return voucherpin;
    }

    @JsonProperty("voucherpin")
    public void setVoucherpin(String voucherpin) {
        this.voucherpin = voucherpin;
    }

    @JsonProperty("userid")
    public Object getUserid() {
        return userid;
    }

    @JsonProperty("userid")
    public void setUserid(Object userid) {
        this.userid = userid;
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
