
package restassuredapi.pojo.userassociateprofileresponsepojo;

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
    "status",
    "messageCode",
    "message",
    "gradeList",
    "transferProfileList",
    "transferRuleTypeList",
    "lmsList"
})
public class UserAssociateProfileResponsePojo {

    @JsonProperty("status")
    private Integer status;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("gradeList")
    private List<GradeList> gradeList = null;
    @JsonProperty("transferProfileList")
    private List<TransferProfileList> transferProfileList = null;
    @JsonProperty("transferRuleTypeList")
    private List<TransferRuleTypeList> transferRuleTypeList = null;
    @JsonProperty("lmsList")
    private List<LmsList> lmsList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
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

    @JsonProperty("gradeList")
    public List<GradeList> getGradeList() {
        return gradeList;
    }

    @JsonProperty("gradeList")
    public void setGradeList(List<GradeList> gradeList) {
        this.gradeList = gradeList;
    }

    @JsonProperty("transferProfileList")
    public List<TransferProfileList> getTransferProfileList() {
        return transferProfileList;
    }

    @JsonProperty("transferProfileList")
    public void setTransferProfileList(List<TransferProfileList> transferProfileList) {
        this.transferProfileList = transferProfileList;
    }

    @JsonProperty("transferRuleTypeList")
    public List<TransferRuleTypeList> getTransferRuleTypeList() {
        return transferRuleTypeList;
    }

    @JsonProperty("transferRuleTypeList")
    public void setTransferRuleTypeList(List<TransferRuleTypeList> transferRuleTypeList) {
        this.transferRuleTypeList = transferRuleTypeList;
    }

    @JsonProperty("lmsList")
    public List<LmsList> getLmsList() {
        return lmsList;
    }

    @JsonProperty("lmsList")
    public void setLmsList(List<LmsList> lmsList) {
        this.lmsList = lmsList;
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
