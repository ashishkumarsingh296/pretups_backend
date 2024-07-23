
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
    "service",
    "referenceId",
    "status",
    "messageCode",
    "message",
    "errorMap",
    "successList",
    "detailedInfoList",
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
    "previousToDate",
    "totalIncomePercentage",
    "totalBaseComPercentage",
    "totalAdditionalBaseComPercentage",
    "totalCacPercentage",
    "totalCbcPercentage"
})
public class TotalIncomeDetailedViewResponsePojo {

    @JsonProperty("service")
    private Object service;
    @JsonProperty("referenceId")
    private Object referenceId;
    @JsonProperty("status")
    private String status;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("errorMap")
    private Object errorMap;
    @JsonProperty("successList")
    private List<Object> successList = null;
    @JsonProperty("detailedInfoList")
    private List<DetailedInfoList> detailedInfoList = null;
    @JsonProperty("totalIncome")
    private float totalIncome;
    @JsonProperty("previousTotalIncome")
    private float previousTotalIncome;
    @JsonProperty("totalBaseCom")
    private int totalBaseCom;
    @JsonProperty("previousTotalBaseComm")
    private int previousTotalBaseComm;
    @JsonProperty("totalAdditionalBaseCom")
    private float totalAdditionalBaseCom;
    @JsonProperty("previousTotalAdditionalBaseCom")
    private float previousTotalAdditionalBaseCom;
    @JsonProperty("totalCac")
    private int totalCac;
    @JsonProperty("previousTotalCac")
    private int previousTotalCac;
    @JsonProperty("totalCbc")
    private int totalCbc;
    @JsonProperty("previousTotalCbc")
    private int previousTotalCbc;
    @JsonProperty("fromDate")
    private String fromDate;
    @JsonProperty("toDate")
    private String toDate;
    @JsonProperty("previousFromDate")
    private String previousFromDate;
    @JsonProperty("previousToDate")
    private String previousToDate;
    @JsonProperty("totalIncomePercentage")
    private String totalIncomePercentage;
    @JsonProperty("totalBaseComPercentage")
    private String totalBaseComPercentage;
    @JsonProperty("totalAdditionalBaseComPercentage")
    private String totalAdditionalBaseComPercentage;
    @JsonProperty("totalCacPercentage")
    private String totalCacPercentage;
    @JsonProperty("totalCbcPercentage")
    private String totalCbcPercentage;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("service")
    public Object getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(Object service) {
        this.service = service;
    }

    @JsonProperty("referenceId")
    public Object getReferenceId() {
        return referenceId;
    }

    @JsonProperty("referenceId")
    public void setReferenceId(Object referenceId) {
        this.referenceId = referenceId;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("messageCode")
    public String getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("errorMap")
    public Object getErrorMap() {
        return errorMap;
    }

    @JsonProperty("errorMap")
    public void setErrorMap(Object errorMap) {
        this.errorMap = errorMap;
    }

    @JsonProperty("successList")
    public List<Object> getSuccessList() {
        return successList;
    }

    @JsonProperty("successList")
    public void setSuccessList(List<Object> successList) {
        this.successList = successList;
    }

    @JsonProperty("detailedInfoList")
    public List<DetailedInfoList> getDetailedInfoList() {
        return detailedInfoList;
    }

    @JsonProperty("detailedInfoList")
    public void setDetailedInfoList(List<DetailedInfoList> detailedInfoList) {
        this.detailedInfoList = detailedInfoList;
    }

    @JsonProperty("totalIncome")
    public float getTotalIncome() {
        return totalIncome;
    }

    @JsonProperty("totalIncome")
    public void setTotalIncome(float totalIncome) {
        this.totalIncome = totalIncome;
    }

    @JsonProperty("previousTotalIncome")
    public float getPreviousTotalIncome() {
        return previousTotalIncome;
    }

    @JsonProperty("previousTotalIncome")
    public void setPreviousTotalIncome(float previousTotalIncome) {
        this.previousTotalIncome = previousTotalIncome;
    }

    @JsonProperty("totalBaseCom")
    public int getTotalBaseCom() {
        return totalBaseCom;
    }

    @JsonProperty("totalBaseCom")
    public void setTotalBaseCom(int totalBaseCom) {
        this.totalBaseCom = totalBaseCom;
    }

    @JsonProperty("previousTotalBaseComm")
    public int getPreviousTotalBaseComm() {
        return previousTotalBaseComm;
    }

    @JsonProperty("previousTotalBaseComm")
    public void setPreviousTotalBaseComm(int previousTotalBaseComm) {
        this.previousTotalBaseComm = previousTotalBaseComm;
    }

    @JsonProperty("totalAdditionalBaseCom")
    public float getTotalAdditionalBaseCom() {
        return totalAdditionalBaseCom;
    }

    @JsonProperty("totalAdditionalBaseCom")
    public void setTotalAdditionalBaseCom(float totalAdditionalBaseCom) {
        this.totalAdditionalBaseCom = totalAdditionalBaseCom;
    }

    @JsonProperty("previousTotalAdditionalBaseCom")
    public float getPreviousTotalAdditionalBaseCom() {
        return previousTotalAdditionalBaseCom;
    }

    @JsonProperty("previousTotalAdditionalBaseCom")
    public void setPreviousTotalAdditionalBaseCom(float previousTotalAdditionalBaseCom) {
        this.previousTotalAdditionalBaseCom = previousTotalAdditionalBaseCom;
    }

    @JsonProperty("totalCac")
    public int getTotalCac() {
        return totalCac;
    }

    @JsonProperty("totalCac")
    public void setTotalCac(int totalCac) {
        this.totalCac = totalCac;
    }

    @JsonProperty("previousTotalCac")
    public int getPreviousTotalCac() {
        return previousTotalCac;
    }

    @JsonProperty("previousTotalCac")
    public void setPreviousTotalCac(int previousTotalCac) {
        this.previousTotalCac = previousTotalCac;
    }

    @JsonProperty("totalCbc")
    public int getTotalCbc() {
        return totalCbc;
    }

    @JsonProperty("totalCbc")
    public void setTotalCbc(int totalCbc) {
        this.totalCbc = totalCbc;
    }

    @JsonProperty("previousTotalCbc")
    public int getPreviousTotalCbc() {
        return previousTotalCbc;
    }

    @JsonProperty("previousTotalCbc")
    public void setPreviousTotalCbc(int previousTotalCbc) {
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

    @JsonProperty("totalIncomePercentage")
    public String getTotalIncomePercentage() {
        return totalIncomePercentage;
    }

    @JsonProperty("totalIncomePercentage")
    public void setTotalIncomePercentage(String totalIncomePercentage) {
        this.totalIncomePercentage = totalIncomePercentage;
    }

    @JsonProperty("totalBaseComPercentage")
    public String getTotalBaseComPercentage() {
        return totalBaseComPercentage;
    }

    @JsonProperty("totalBaseComPercentage")
    public void setTotalBaseComPercentage(String totalBaseComPercentage) {
        this.totalBaseComPercentage = totalBaseComPercentage;
    }

    @JsonProperty("totalAdditionalBaseComPercentage")
    public String getTotalAdditionalBaseComPercentage() {
        return totalAdditionalBaseComPercentage;
    }

    @JsonProperty("totalAdditionalBaseComPercentage")
    public void setTotalAdditionalBaseComPercentage(String totalAdditionalBaseComPercentage) {
        this.totalAdditionalBaseComPercentage = totalAdditionalBaseComPercentage;
    }

    @JsonProperty("totalCacPercentage")
    public String getTotalCacPercentage() {
        return totalCacPercentage;
    }

    @JsonProperty("totalCacPercentage")
    public void setTotalCacPercentage(String totalCacPercentage) {
        this.totalCacPercentage = totalCacPercentage;
    }

    @JsonProperty("totalCbcPercentage")
    public String getTotalCbcPercentage() {
        return totalCbcPercentage;
    }

    @JsonProperty("totalCbcPercentage")
    public void setTotalCbcPercentage(String totalCbcPercentage) {
        this.totalCbcPercentage = totalCbcPercentage;
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
