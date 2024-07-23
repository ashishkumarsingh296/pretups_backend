
package restassuredapi.pojo.viewvoucherC2cO2ctrfdetails;

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
    "fromSerialNo",
    "qty",
    "segmentType",
    "toSerialNo",
    "voucherMrp",
    "voucherType"
})
public class Slablist {

    @JsonProperty("fromSerialNo")
    private int fromSerialNo;
    @JsonProperty("qty")
    private int qty;
    @JsonProperty("segmentType")
    private String segmentType;
    @JsonProperty("toSerialNo")
    private int toSerialNo;
    @JsonProperty("voucherMrp")
    private int voucherMrp;
    @JsonProperty("voucherType")
    private String voucherType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Slablist() {
    }

    /**
     * 
     * @param fromSerialNo
     * @param voucherType
     * @param qty
     * @param toSerialNo
     * @param voucherMrp
     * @param segmentType
     */
    public Slablist(int fromSerialNo, int qty, String segmentType, int toSerialNo, int voucherMrp, String voucherType) {
        super();
        this.fromSerialNo = fromSerialNo;
        this.qty = qty;
        this.segmentType = segmentType;
        this.toSerialNo = toSerialNo;
        this.voucherMrp = voucherMrp;
        this.voucherType = voucherType;
    }

    @JsonProperty("fromSerialNo")
    public int getFromSerialNo() {
        return fromSerialNo;
    }

    @JsonProperty("fromSerialNo")
    public void setFromSerialNo(int fromSerialNo) {
        this.fromSerialNo = fromSerialNo;
    }

    @JsonProperty("qty")
    public int getQty() {
        return qty;
    }

    @JsonProperty("qty")
    public void setQty(int qty) {
        this.qty = qty;
    }

    @JsonProperty("segmentType")
    public String getSegmentType() {
        return segmentType;
    }

    @JsonProperty("segmentType")
    public void setSegmentType(String segmentType) {
        this.segmentType = segmentType;
    }

    @JsonProperty("toSerialNo")
    public int getToSerialNo() {
        return toSerialNo;
    }

    @JsonProperty("toSerialNo")
    public void setToSerialNo(int toSerialNo) {
        this.toSerialNo = toSerialNo;
    }

    @JsonProperty("voucherMrp")
    public int getVoucherMrp() {
        return voucherMrp;
    }

    @JsonProperty("voucherMrp")
    public void setVoucherMrp(int voucherMrp) {
        this.voucherMrp = voucherMrp;
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

   /* @Override
    public String toString() {
        return new ToStringBuilder(this).append("fromSerialNo", fromSerialNo).append("qty", qty).append("segmentType", segmentType).append("toSerialNo", toSerialNo).append("voucherMrp", voucherMrp).append("voucherType", voucherType).append("additionalProperties", additionalProperties).toString();
    }
*/
}
