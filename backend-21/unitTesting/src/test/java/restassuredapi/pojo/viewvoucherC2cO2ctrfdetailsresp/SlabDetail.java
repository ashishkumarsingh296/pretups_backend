
package restassuredapi.pojo.viewvoucherC2cO2ctrfdetailsresp;

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
    "salbAmount",
    "slabQty"
})
public class SlabDetail {

    @JsonProperty("salbAmount")
    private String salbAmount;
    @JsonProperty("slabQty")
    private String slabQty;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public SlabDetail() {
    }

    /**
     * 
     * @param salbAmount
     * @param slabQty
     */
    public SlabDetail(String salbAmount, String slabQty) {
        super();
        this.salbAmount = salbAmount;
        this.slabQty = slabQty;
    }

    @JsonProperty("salbAmount")
    public String getSalbAmount() {
        return salbAmount;
    }

    @JsonProperty("salbAmount")
    public void setSalbAmount(String salbAmount) {
        this.salbAmount = salbAmount;
    }

    @JsonProperty("slabQty")
    public String getSlabQty() {
        return slabQty;
    }

    @JsonProperty("slabQty")
    public void setSlabQty(String slabQty) {
        this.slabQty = slabQty;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

  /*  @Override
    public String toString() {
        return new ToStringBuilder(this).append("salbAmount", salbAmount).append("slabQty", slabQty).append("additionalProperties", additionalProperties).toString();
    }
*/
}
