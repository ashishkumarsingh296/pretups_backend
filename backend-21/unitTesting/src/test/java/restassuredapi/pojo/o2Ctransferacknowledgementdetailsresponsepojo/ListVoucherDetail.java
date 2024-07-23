
package restassuredapi.pojo.o2Ctransferacknowledgementdetailsresponsepojo;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "batchType",
    "fromSerialNumber",
    "toSerialNumber",
    "totalNoofVouchers",
    "vomsProductName",
    "voucherBatchNumber"
})
@Generated("jsonschema2pojo")
public class ListVoucherDetail {

    @JsonProperty("batchType")
    private String batchType;
    @JsonProperty("fromSerialNumber")
    private String fromSerialNumber;
    @JsonProperty("toSerialNumber")
    private String toSerialNumber;
    @JsonProperty("totalNoofVouchers")
    private String totalNoofVouchers;
    @JsonProperty("vomsProductName")
    private String vomsProductName;
    @JsonProperty("voucherBatchNumber")
    private String voucherBatchNumber;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("batchType")
    public String getBatchType() {
        return batchType;
    }

    @JsonProperty("batchType")
    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }

    @JsonProperty("fromSerialNumber")
    public String getFromSerialNumber() {
        return fromSerialNumber;
    }

    @JsonProperty("fromSerialNumber")
    public void setFromSerialNumber(String fromSerialNumber) {
        this.fromSerialNumber = fromSerialNumber;
    }

    @JsonProperty("toSerialNumber")
    public String getToSerialNumber() {
        return toSerialNumber;
    }

    @JsonProperty("toSerialNumber")
    public void setToSerialNumber(String toSerialNumber) {
        this.toSerialNumber = toSerialNumber;
    }

    @JsonProperty("totalNoofVouchers")
    public String getTotalNoofVouchers() {
        return totalNoofVouchers;
    }

    @JsonProperty("totalNoofVouchers")
    public void setTotalNoofVouchers(String totalNoofVouchers) {
        this.totalNoofVouchers = totalNoofVouchers;
    }

    @JsonProperty("vomsProductName")
    public String getVomsProductName() {
        return vomsProductName;
    }

    @JsonProperty("vomsProductName")
    public void setVomsProductName(String vomsProductName) {
        this.vomsProductName = vomsProductName;
    }

    @JsonProperty("voucherBatchNumber")
    public String getVoucherBatchNumber() {
        return voucherBatchNumber;
    }

    @JsonProperty("voucherBatchNumber")
    public void setVoucherBatchNumber(String voucherBatchNumber) {
        this.voucherBatchNumber = voucherBatchNumber;
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
