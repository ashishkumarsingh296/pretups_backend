
package restassuredapi.pojo.BatchO2CApprovalDetailRequestpojo;

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
    "approvalLevel",
    "approvalSubType",
    "approvalType",
    "batchId"
})
public class Data {

    @JsonProperty("approvalLevel")
    private String approvalLevel;
    @JsonProperty("approvalSubType")
    private String approvalSubType;
    @JsonProperty("approvalType")
    private String approvalType;
    @JsonProperty("batchId")
    private String batchId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("approvalLevel")
    public String getApprovalLevel() {
        return approvalLevel;
    }

    @JsonProperty("approvalLevel")
    public void setApprovalLevel(String approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    @JsonProperty("approvalSubType")
    public String getApprovalSubType() {
        return approvalSubType;
    }

    @JsonProperty("approvalSubType")
    public void setApprovalSubType(String approvalSubType) {
        this.approvalSubType = approvalSubType;
    }

    @JsonProperty("approvalType")
    public String getApprovalType() {
        return approvalType;
    }

    @JsonProperty("approvalType")
    public void setApprovalType(String approvalType) {
        this.approvalType = approvalType;
    }

    @JsonProperty("batchId")
    public String getBatchId() {
        return batchId;
    }

    @JsonProperty("batchId")
    public void setBatchId(String batchId) {
        this.batchId = batchId;
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
