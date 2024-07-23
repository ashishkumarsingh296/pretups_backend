
package restassuredapi.pojo.topfiveproductrechargedetailresponsepojo;

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
    "lastMonthValue",
    "previousFromDate",
    "previousToDate",
    "currentData",
    "previousData",
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
    private Object fromDate;
    @JsonProperty("toDate")
    private Object toDate;
    @JsonProperty("totalCount")
    private Object totalCount;
    @JsonProperty("totalValue")
    private Object totalValue;
    @JsonProperty("lastMonthCount")
    private Object lastMonthCount;
    @JsonProperty("lastMonthValue")
    private Object lastMonthValue;
    @JsonProperty("previousFromDate")
    private Object previousFromDate;
    @JsonProperty("previousToDate")
    private Object previousToDate;
    @JsonIgnore
    private CurrentData currentData;
    @JsonIgnore
    private PreviousData previousData;
    @JsonIgnore()
    private List<Object> transferList = null;
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
    public Object getFromDate() {
        return fromDate;
    }

    @JsonProperty("fromDate")
    public void setFromDate(Object fromDate) {
        this.fromDate = fromDate;
    }

    @JsonProperty("toDate")
    public Object getToDate() {
        return toDate;
    }

    @JsonProperty("toDate")
    public void setToDate(Object toDate) {
        this.toDate = toDate;
    }

    @JsonProperty("totalCount")
    public Object getTotalCount() {
        return totalCount;
    }

    @JsonProperty("totalCount")
    public void setTotalCount(Object totalCount) {
        this.totalCount = totalCount;
    }

    @JsonProperty("totalValue")
    public Object getTotalValue() {
        return totalValue;
    }

    @JsonProperty("totalValue")
    public void setTotalValue(Object totalValue) {
        this.totalValue = totalValue;
    }

    @JsonProperty("lastMonthCount")
    public Object getLastMonthCount() {
        return lastMonthCount;
    }

    @JsonProperty("lastMonthCount")
    public void setLastMonthCount(Object lastMonthCount) {
        this.lastMonthCount = lastMonthCount;
    }

    @JsonProperty("lastMonthValue")
    public Object getLastMonthValue() {
        return lastMonthValue;
    }

    @JsonProperty("lastMonthValue")
    public void setLastMonthValue(Object lastMonthValue) {
        this.lastMonthValue = lastMonthValue;
    }

    @JsonProperty("previousFromDate")
    public Object getPreviousFromDate() {
        return previousFromDate;
    }

    @JsonProperty("previousFromDate")
    public void setPreviousFromDate(Object previousFromDate) {
        this.previousFromDate = previousFromDate;
    }

    @JsonProperty("previousToDate")
    public Object getPreviousToDate() {
        return previousToDate;
    }

    @JsonProperty("previousToDate")
    public void setPreviousToDate(Object previousToDate) {
        this.previousToDate = previousToDate;
    }

    @JsonIgnore
    public CurrentData getCurrentData() {
        return currentData;
    }

    @JsonIgnore
    public void setCurrentData(CurrentData currentData) {
        this.currentData = currentData;
    }

    @JsonIgnore
    public PreviousData getPreviousData() {
        return previousData;
    }

    @JsonIgnore
    public void setPreviousData(PreviousData previousData) {
        this.previousData = previousData;
    }

    @JsonIgnore
    public List<Object> getTransferList() {
        return transferList;
    }

    @JsonIgnore
    public void setTransferList(List<Object> transferList) {
        this.transferList = transferList;
    }

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
