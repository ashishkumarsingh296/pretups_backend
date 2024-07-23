
package restassuredapi.pojo.c2CVoucherTransferRequestPojo;

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
    "denomination",
    "fromSerialNo",
    "toSerialNo",
    "voucherType",
    "vouchersegment"
})
public class VoucherDetail {

    @JsonProperty("denomination")
    private String denomination;
    @JsonProperty("fromSerialNo")
    private String fromSerialNo;
    @JsonProperty("toSerialNo")
    private String toSerialNo;
    @JsonProperty("voucherType")
    private String voucherType;
    @JsonProperty("vouchersegment")
    private String vouchersegment;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("denomination")
    public String getDenomination() {
        return denomination;
    }

    @JsonProperty("denomination")
    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    @JsonProperty("fromSerialNo")
    public String getFromSerialNo() {
        return fromSerialNo;
    }

    @JsonProperty("fromSerialNo")
    public void setFromSerialNo(String fromSerialNo) {
        this.fromSerialNo = fromSerialNo;
    }

    @JsonProperty("toSerialNo")
    public String getToSerialNo() {
        return toSerialNo;
    }

    @JsonProperty("toSerialNo")
    public void setToSerialNo(String toSerialNo) {
        this.toSerialNo = toSerialNo;
    }

    @JsonProperty("voucherType")
    public String getVoucherType() {
        return voucherType;
    }

    @JsonProperty("voucherType")
    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    @JsonProperty("vouchersegment")
    public String getVouchersegment() {
        return vouchersegment;
    }

    @JsonProperty("vouchersegment")
    public void setVouchersegment(String vouchersegment) {
        this.vouchersegment = vouchersegment;
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
