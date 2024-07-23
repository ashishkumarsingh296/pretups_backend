
package restassuredapi.pojo.c2sgetmvddenominationresponsepojo;

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
    "voucherDenomList"
})
public class C2SGetMvdDenominationResponsePojo {

    @JsonProperty("service")
    private String service;
    @JsonProperty("referenceId")
    private Long referenceId;
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
    @JsonProperty("voucherDenomList")
    private List<VoucherDenomList> voucherDenomList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("service")
    public String getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(String service) {
        this.service = service;
    }

    @JsonProperty("referenceId")
    public Long getReferenceId() {
        return referenceId;
    }

    @JsonProperty("referenceId")
    public void setReferenceId(Long referenceId) {
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

    @JsonProperty("voucherDenomList")
    public List<VoucherDenomList> getVoucherDenomList() {
        return voucherDenomList;
    }

    @JsonProperty("voucherDenomList")
    public void setVoucherDenomList(List<VoucherDenomList> voucherDenomList) {
        this.voucherDenomList = voucherDenomList;
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
