
package restassuredapi.pojo.dvdapirequestpojo;

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
    "voucherProfile",
    "voucherSegment",
    "voucherType"
})
public class VoucherDetail {

    @JsonProperty("denomination")
    private String denomination;
    @JsonProperty("quantity")
    private String quantity;
    @JsonProperty("voucherProfile")
    private String voucherProfile;
    @JsonProperty("voucherSegment")
    private String voucherSegment;
    @JsonProperty("voucherType")
    private String voucherType;
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

    @JsonProperty("voucherProfile")
    public String getVoucherProfile() {
        return voucherProfile;
    }

    @JsonProperty("voucherProfile")
    public void setVoucherProfile(String voucherProfile) {
        this.voucherProfile = voucherProfile;
    }

    @JsonProperty("voucherSegment")
    public String getVoucherSegment() {
        return voucherSegment;
    }

    @JsonProperty("voucherSegment")
    public void setVoucherSegment(String voucherSegment) {
        this.voucherSegment = voucherSegment;
    }

    @JsonProperty("voucherType")
    public String getVoucherType() {
        return voucherType;
    }

    @JsonProperty("voucherType")
    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
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
