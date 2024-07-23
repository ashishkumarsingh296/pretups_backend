
package restassuredapi.pojo.totalincomedetailedviewresponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "date",
    "txnstatus",
    "errorcode",
    "detailedinfo",
    "totalIncome",
    "previousTotalIncome",
    "totalBaseCom",
    "previousTotalBaseComm",
    "totalAdditionalBaseCom",
    "previousTotalAdditionalBaseCom",
    "totalCac",
    "previousTotalCac",
    "totalCbc",
    "previousTotalCbc",
    "fromDate",
    "toDate",
    "previousFromDate",
    "previousToDate"
})
public class DataObject {

    @JsonProperty("type")
    private String type;
    @JsonProperty("date")
    private String date;
    @JsonProperty("txnstatus")
    private String txnstatus;
    @JsonProperty("errorcode")
    private String errorcode;
    @JsonProperty("detailedinfo")
    private List<Object> detailedinfo = null;
    @JsonProperty("totalIncome")
    private String totalIncome;
    @JsonProperty("previousTotalIncome")
    private String previousTotalIncome;
    @JsonProperty("totalBaseCom")
    private String totalBaseCom;
    @JsonProperty("previousTotalBaseComm")
    private String previousTotalBaseComm;
    @JsonProperty("totalAdditionalBaseCom")
    private String totalAdditionalBaseCom;
    @JsonProperty("previousTotalAdditionalBaseCom")
    private String previousTotalAdditionalBaseCom;
    @JsonProperty("totalCac")
    private String totalCac;
    @JsonProperty("previousTotalCac")
    private String previousTotalCac;
    @JsonProperty("totalCbc")
    private String totalCbc;
    @JsonProperty("previousTotalCbc")
    private String previousTotalCbc;
    @JsonProperty("fromDate")
    private String fromDate;
    @JsonProperty("toDate")
    private String toDate;
    @JsonProperty("previousFromDate")
    private String previousFromDate;
    @JsonProperty("previousToDate")
    private String previousToDate;
    @JsonProperty("message")
    private String message;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("txnstatus")
    public String getTxnstatus() {
        return txnstatus;
    }

    @JsonProperty("txnstatus")
    public void setTxnstatus(String txnstatus) {
        this.txnstatus = txnstatus;
    }

    @JsonProperty("errorcode")
    public String getErrorcode() {
        return errorcode;
    }

    @JsonProperty("errorcode")
    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    @JsonProperty("detailedinfo")
    public List<Object> getDetailedinfo() {
        return detailedinfo;
    }

    @JsonProperty("detailedinfo")
    public void setDetailedinfo(List<Object> detailedinfo) {
        this.detailedinfo = detailedinfo;
    }

    @JsonProperty("totalIncome")
    public String getTotalIncome() {
        return totalIncome;
    }

    @JsonProperty("totalIncome")
    public void setTotalIncome(String totalIncome) {
        this.totalIncome = totalIncome;
    }

    @JsonProperty("previousTotalIncome")
    public String getPreviousTotalIncome() {
        return previousTotalIncome;
    }

    @JsonProperty("previousTotalIncome")
    public void setPreviousTotalIncome(String previousTotalIncome) {
        this.previousTotalIncome = previousTotalIncome;
    }

    @JsonProperty("totalBaseCom")
    public String getTotalBaseCom() {
        return totalBaseCom;
    }

    @JsonProperty("totalBaseCom")
    public void setTotalBaseCom(String totalBaseCom) {
        this.totalBaseCom = totalBaseCom;
    }

    @JsonProperty("previousTotalBaseComm")
    public String getPreviousTotalBaseComm() {
        return previousTotalBaseComm;
    }

    @JsonProperty("previousTotalBaseComm")
    public void setPreviousTotalBaseComm(String previousTotalBaseComm) {
        this.previousTotalBaseComm = previousTotalBaseComm;
    }

    @JsonProperty("totalAdditionalBaseCom")
    public String getTotalAdditionalBaseCom() {
        return totalAdditionalBaseCom;
    }

    @JsonProperty("totalAdditionalBaseCom")
    public void setTotalAdditionalBaseCom(String totalAdditionalBaseCom) {
        this.totalAdditionalBaseCom = totalAdditionalBaseCom;
    }

    @JsonProperty("previousTotalAdditionalBaseCom")
    public String getPreviousTotalAdditionalBaseCom() {
        return previousTotalAdditionalBaseCom;
    }

    @JsonProperty("previousTotalAdditionalBaseCom")
    public void setPreviousTotalAdditionalBaseCom(String previousTotalAdditionalBaseCom) {
        this.previousTotalAdditionalBaseCom = previousTotalAdditionalBaseCom;
    }

    @JsonProperty("totalCac")
    public String getTotalCac() {
        return totalCac;
    }

    @JsonProperty("totalCac")
    public void setTotalCac(String totalCac) {
        this.totalCac = totalCac;
    }

    @JsonProperty("previousTotalCac")
    public String getPreviousTotalCac() {
        return previousTotalCac;
    }

    @JsonProperty("previousTotalCac")
    public void setPreviousTotalCac(String previousTotalCac) {
        this.previousTotalCac = previousTotalCac;
    }

    @JsonProperty("totalCbc")
    public String getTotalCbc() {
        return totalCbc;
    }

    @JsonProperty("totalCbc")
    public void setTotalCbc(String totalCbc) {
        this.totalCbc = totalCbc;
    }

    @JsonProperty("previousTotalCbc")
    public String getPreviousTotalCbc() {
        return previousTotalCbc;
    }

    @JsonProperty("previousTotalCbc")
    public void setPreviousTotalCbc(String previousTotalCbc) {
        this.previousTotalCbc = previousTotalCbc;
    }

    @JsonProperty("fromDate")
    public String getFromDate() {
        return fromDate;
    }

    @JsonProperty("fromDate")
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    @JsonProperty("toDate")
    public String getToDate() {
        return toDate;
    }

    @JsonProperty("toDate")
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    @JsonProperty("previousFromDate")
    public String getPreviousFromDate() {
        return previousFromDate;
    }

    @JsonProperty("previousFromDate")
    public void setPreviousFromDate(String previousFromDate) {
        this.previousFromDate = previousFromDate;
    }

    @JsonProperty("previousToDate")
    public String getPreviousToDate() {
        return previousToDate;
    }

    @JsonProperty("previousToDate")
    public void setPreviousToDate(String previousToDate) {
        this.previousToDate = previousToDate;
    }

    @JsonProperty("message")
    public String getMessage() {
		return message;
	}

    @JsonProperty("message")
	public void setMessage(String message) {
		this.message = message;
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
