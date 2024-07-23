
package restassuredapi.pojo.commissionslabdetailsresponsepojo;

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
    "commission",
    "fromRange",
    "tax1",
    "tax2",
    "tax3",
    "toRange"
})
@Generated("jsonschema2pojo")
public class ListCommissionSlabDet {

    @JsonProperty("commission")
    private String commission;
    @JsonProperty("fromRange")
    private String fromRange;
    @JsonProperty("tax1")
    private String tax1;
    @JsonProperty("tax2")
    private String tax2;
    @JsonProperty("tax3")
    private String tax3;
    @JsonProperty("toRange")
    private String toRange;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("commission")
    public String getCommission() {
        return commission;
    }

    @JsonProperty("commission")
    public void setCommission(String commission) {
        this.commission = commission;
    }

    @JsonProperty("fromRange")
    public String getFromRange() {
        return fromRange;
    }

    @JsonProperty("fromRange")
    public void setFromRange(String fromRange) {
        this.fromRange = fromRange;
    }

    @JsonProperty("tax1")
    public String getTax1() {
        return tax1;
    }

    @JsonProperty("tax1")
    public void setTax1(String tax1) {
        this.tax1 = tax1;
    }

    @JsonProperty("tax2")
    public String getTax2() {
        return tax2;
    }

    @JsonProperty("tax2")
    public void setTax2(String tax2) {
        this.tax2 = tax2;
    }

    @JsonProperty("tax3")
    public String getTax3() {
        return tax3;
    }

    @JsonProperty("tax3")
    public void setTax3(String tax3) {
        this.tax3 = tax3;
    }

    @JsonProperty("toRange")
    public String getToRange() {
        return toRange;
    }

    @JsonProperty("toRange")
    public void setToRange(String toRange) {
        this.toRange = toRange;
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
