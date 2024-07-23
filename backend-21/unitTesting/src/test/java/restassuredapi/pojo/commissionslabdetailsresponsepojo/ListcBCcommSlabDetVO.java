
package restassuredapi.pojo.commissionslabdetailsresponsepojo;

import java.util.HashMap;
import java.util.List;
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
    "cbcApplicableFromNTo",
    "cbcTimeSlab",
    "listCBCCommsionDetails",
    "product"
})
@Generated("jsonschema2pojo")
public class ListcBCcommSlabDetVO {

    @JsonProperty("cbcApplicableFromNTo")
    private String cbcApplicableFromNTo;
    @JsonProperty("cbcTimeSlab")
    private String cbcTimeSlab;
    @JsonProperty("listCBCCommsionDetails")
    private List<ListCBCCommsionDetail> listCBCCommsionDetails = null;
    @JsonProperty("product")
    private String product;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cbcApplicableFromNTo")
    public String getCbcApplicableFromNTo() {
        return cbcApplicableFromNTo;
    }

    @JsonProperty("cbcApplicableFromNTo")
    public void setCbcApplicableFromNTo(String cbcApplicableFromNTo) {
        this.cbcApplicableFromNTo = cbcApplicableFromNTo;
    }

    @JsonProperty("cbcTimeSlab")
    public String getCbcTimeSlab() {
        return cbcTimeSlab;
    }

    @JsonProperty("cbcTimeSlab")
    public void setCbcTimeSlab(String cbcTimeSlab) {
        this.cbcTimeSlab = cbcTimeSlab;
    }

    @JsonProperty("listCBCCommsionDetails")
    public List<ListCBCCommsionDetail> getListCBCCommsionDetails() {
        return listCBCCommsionDetails;
    }

    @JsonProperty("listCBCCommsionDetails")
    public void setListCBCCommsionDetails(List<ListCBCCommsionDetail> listCBCCommsionDetails) {
        this.listCBCCommsionDetails = listCBCCommsionDetails;
    }

    @JsonProperty("product")
    public String getProduct() {
        return product;
    }

    @JsonProperty("product")
    public void setProduct(String product) {
        this.product = product;
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
