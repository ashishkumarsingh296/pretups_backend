
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
    "denomination",
    "quantity",
    "voucherType",
    "vouchersegment"
})
public class VoucherDetail {

    @JsonProperty("denomination")
    private String denomination;
    @JsonProperty("quantity")
    private String quantity;
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

    @JsonProperty("quantity")
    public String getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(String quantity) {
        this.quantity = quantity;
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
