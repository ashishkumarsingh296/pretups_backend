
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
    "applicableFrom",
    "applicableTo",
    "gateWaySelected",
    "listAdditionalCommSlabDetails",
    "maxTransferValue",
    "minTransferValue",
    "service",
    "status",
    "subService",
    "timeSlab"
})
@Generated("jsonschema2pojo")
public class ListAdditionalCommSlabVO {

    @JsonProperty("applicableFrom")
    private String applicableFrom;
    @JsonProperty("applicableTo")
    private String applicableTo;
    @JsonProperty("gateWaySelected")
    private String gateWaySelected;
    @JsonProperty("listAdditionalCommSlabDetails")
    private List<ListAdditionalCommSlabDetail> listAdditionalCommSlabDetails = null;
    @JsonProperty("maxTransferValue")
    private String maxTransferValue;
    @JsonProperty("minTransferValue")
    private String minTransferValue;
    @JsonProperty("service")
    private String service;
    @JsonProperty("status")
    private String status;
    @JsonProperty("subService")
    private String subService;
    @JsonProperty("timeSlab")
    private String timeSlab;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("applicableFrom")
    public String getApplicableFrom() {
        return applicableFrom;
    }

    @JsonProperty("applicableFrom")
    public void setApplicableFrom(String applicableFrom) {
        this.applicableFrom = applicableFrom;
    }

    @JsonProperty("applicableTo")
    public String getApplicableTo() {
        return applicableTo;
    }

    @JsonProperty("applicableTo")
    public void setApplicableTo(String applicableTo) {
        this.applicableTo = applicableTo;
    }

    @JsonProperty("gateWaySelected")
    public String getGateWaySelected() {
        return gateWaySelected;
    }

    @JsonProperty("gateWaySelected")
    public void setGateWaySelected(String gateWaySelected) {
        this.gateWaySelected = gateWaySelected;
    }

    @JsonProperty("listAdditionalCommSlabDetails")
    public List<ListAdditionalCommSlabDetail> getListAdditionalCommSlabDetails() {
        return listAdditionalCommSlabDetails;
    }

    @JsonProperty("listAdditionalCommSlabDetails")
    public void setListAdditionalCommSlabDetails(List<ListAdditionalCommSlabDetail> listAdditionalCommSlabDetails) {
        this.listAdditionalCommSlabDetails = listAdditionalCommSlabDetails;
    }

    @JsonProperty("maxTransferValue")
    public String getMaxTransferValue() {
        return maxTransferValue;
    }

    @JsonProperty("maxTransferValue")
    public void setMaxTransferValue(String maxTransferValue) {
        this.maxTransferValue = maxTransferValue;
    }

    @JsonProperty("minTransferValue")
    public String getMinTransferValue() {
        return minTransferValue;
    }

    @JsonProperty("minTransferValue")
    public void setMinTransferValue(String minTransferValue) {
        this.minTransferValue = minTransferValue;
    }

    @JsonProperty("service")
    public String getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(String service) {
        this.service = service;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("subService")
    public String getSubService() {
        return subService;
    }

    @JsonProperty("subService")
    public void setSubService(String subService) {
        this.subService = subService;
    }

    @JsonProperty("timeSlab")
    public String getTimeSlab() {
        return timeSlab;
    }

    @JsonProperty("timeSlab")
    public void setTimeSlab(String timeSlab) {
        this.timeSlab = timeSlab;
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
