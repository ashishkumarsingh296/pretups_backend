
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
    "commissionType",
    "errorMap",
    "listAdditionalCommSlabVO",
    "listcBCcommSlabDetVO",
    "listcommissionSlabDetVO",
    "message",
    "messageCode",
    "referenceId",
    "service",
    "status",
    "successList"
})
@Generated("jsonschema2pojo")
public class CommissionSlabDetailsResponsePojo {

    @JsonProperty("applicableFrom")
    private String applicableFrom;
    @JsonProperty("commissionType")
    private String commissionType;
    @JsonProperty("errorMap")
    private ErrorMap errorMap;
    @JsonProperty("listAdditionalCommSlabVO")
    private List<ListAdditionalCommSlabVO> listAdditionalCommSlabVO = null;
    @JsonProperty("listcBCcommSlabDetVO")
    private List<ListcBCcommSlabDetVO> listcBCcommSlabDetVO = null;
    @JsonProperty("listcommissionSlabDetVO")
    private List<ListcommissionSlabDetVO> listcommissionSlabDetVO = null;
    @JsonProperty("message")
    private String message;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("referenceId")
    private Integer referenceId;
    @JsonProperty("service")
    private String service;
    @JsonProperty("status")
    private String status;
    @JsonProperty("successList")
    private List<Success> successList = null;
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

    @JsonProperty("commissionType")
    public String getCommissionType() {
        return commissionType;
    }

    @JsonProperty("commissionType")
    public void setCommissionType(String commissionType) {
        this.commissionType = commissionType;
    }

    @JsonProperty("errorMap")
    public ErrorMap getErrorMap() {
        return errorMap;
    }

    @JsonProperty("errorMap")
    public void setErrorMap(ErrorMap errorMap) {
        this.errorMap = errorMap;
    }

    @JsonProperty("listAdditionalCommSlabVO")
    public List<ListAdditionalCommSlabVO> getListAdditionalCommSlabVO() {
        return listAdditionalCommSlabVO;
    }

    @JsonProperty("listAdditionalCommSlabVO")
    public void setListAdditionalCommSlabVO(List<ListAdditionalCommSlabVO> listAdditionalCommSlabVO) {
        this.listAdditionalCommSlabVO = listAdditionalCommSlabVO;
    }

    @JsonProperty("listcBCcommSlabDetVO")
    public List<ListcBCcommSlabDetVO> getListcBCcommSlabDetVO() {
        return listcBCcommSlabDetVO;
    }

    @JsonProperty("listcBCcommSlabDetVO")
    public void setListcBCcommSlabDetVO(List<ListcBCcommSlabDetVO> listcBCcommSlabDetVO) {
        this.listcBCcommSlabDetVO = listcBCcommSlabDetVO;
    }

    @JsonProperty("listcommissionSlabDetVO")
    public List<ListcommissionSlabDetVO> getListcommissionSlabDetVO() {
        return listcommissionSlabDetVO;
    }

    @JsonProperty("listcommissionSlabDetVO")
    public void setListcommissionSlabDetVO(List<ListcommissionSlabDetVO> listcommissionSlabDetVO) {
        this.listcommissionSlabDetVO = listcommissionSlabDetVO;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("messageCode")
    public String getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("referenceId")
    public Integer getReferenceId() {
        return referenceId;
    }

    @JsonProperty("referenceId")
    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
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

    @JsonProperty("successList")
    public List<Success> getSuccessList() {
        return successList;
    }

    @JsonProperty("successList")
    public void setSuccessList(List<Success> successList) {
        this.successList = successList;
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
