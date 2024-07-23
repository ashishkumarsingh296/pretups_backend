
package restassuredapi.pojo.focapprovalrequestpojo;

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
    "currentStatus",
    "extTxnDate",
    "extTxnNumber",
    "language1",
    "language2",
    "refNumber",
    "remarks",
    "status",
    "toMsisdn",
    "txnId"
})
public class FocApprovalRequest {

    @JsonProperty("currentStatus")
    private String currentStatus;
    @JsonProperty("extTxnDate")
    private String extTxnDate;
    @JsonProperty("extTxnNumber")
    private String extTxnNumber;
    @JsonProperty("language1")
    private String language1;
    @JsonProperty("language2")
    private String language2;
    @JsonProperty("refNumber")
    private String refNumber;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("status")
    private String status;
    @JsonProperty("toMsisdn")
    private String toMsisdn;
    @JsonProperty("txnId")
    private String txnId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("currentStatus")
    public String getCurrentStatus() {
        return currentStatus;
    }

    @JsonProperty("currentStatus")
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    @JsonProperty("extTxnDate")
    public String getExtTxnDate() {
        return extTxnDate;
    }

    @JsonProperty("extTxnDate")
    public void setExtTxnDate(String extTxnDate) {
        this.extTxnDate = extTxnDate;
    }

    @JsonProperty("extTxnNumber")
    public String getExtTxnNumber() {
        return extTxnNumber;
    }

    @JsonProperty("extTxnNumber")
    public void setExtTxnNumber(String extTxnNumber) {
        this.extTxnNumber = extTxnNumber;
    }

    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
    }

    @JsonProperty("language2")
    public String getLanguage2() {
        return language2;
    }

    @JsonProperty("language2")
    public void setLanguage2(String language2) {
        this.language2 = language2;
    }

    @JsonProperty("refNumber")
    public String getRefNumber() {
        return refNumber;
    }

    @JsonProperty("refNumber")
    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("toMsisdn")
    public String getToMsisdn() {
        return toMsisdn;
    }

    @JsonProperty("toMsisdn")
    public void setToMsisdn(String toMsisdn) {
        this.toMsisdn = toMsisdn;
    }

    @JsonProperty("txnId")
    public String getTxnId() {
        return txnId;
    }

    @JsonProperty("txnId")
    public void setTxnId(String txnId) {
        this.txnId = txnId;
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
