
package restassuredapi.pojo.c2sgettransactiondetailresponsepojo;

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
    "message",
    "fromDate",
    "toDate",
    "totalCount",
    "totalValue",
    "lastMonthCount",
    "previousFromDate",
    "previousToDate",
    "transferList",
    "errorcode"
})
public class DataObject {

    @JsonProperty("type")
    private String type;
    @JsonProperty("date")
    private String date;
    @JsonProperty("txnstatus")
    private String txnstatus;
    @JsonProperty("message")
    private String message;
    @JsonProperty("fromDate")
    private String fromDate;
    @JsonProperty("toDate")
    private String toDate;
    @JsonProperty("totalCount")
    private Long totalCount;
    @JsonProperty("totalValue")
    private Long totalValue;
    @JsonProperty("lastMonthCount")
    private String lastMonthCount;
    @JsonProperty("previousFromDate")
    private String previousFromDate;
    @JsonProperty("previousToDate")
    private String previousToDate;
  /*  @JsonProperty("transferList")
    private List<TransferList> transferList = null;*/
    @JsonProperty("errorcode")
    private String errorcode;
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

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
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

    @JsonProperty("totalCount")
    public Long getTotalCount() {
        return totalCount;
    }

    @JsonProperty("totalCount")
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    @JsonProperty("totalValue")
    public Long getTotalValue() {
        return totalValue;
    }

    @JsonProperty("totalValue")
    public void setTotalValue(Long totalValue) {
        this.totalValue = totalValue;
    }

    @JsonProperty("lastMonthCount")
    public String getLastMonthCount() {
        return lastMonthCount;
    }

    @JsonProperty("lastMonthCount")
    public void setLastMonthCount(String lastMonthCount) {
        this.lastMonthCount = lastMonthCount;
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

 /*   @JsonProperty("transferList")
    public List<TransferList> getTransferList() {
        return transferList;
    }

    @JsonProperty("transferList")
    public void setTransferList(List<TransferList> transferList) {
        this.transferList = transferList;
    }*/

    @JsonProperty("errorcode")
    public String getErrorcode() {
        return errorcode;
    }

    @JsonProperty("errorcode")
    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
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
