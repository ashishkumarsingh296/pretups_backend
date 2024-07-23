
package restassuredapi.pojo.channelUserListResponsepojo;

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
    "channelUserVO",
    "channelUsersList",
    "errorMap",
    "message",
    "messageCode",
    "ownerUsersList",
    "prodList",
    "referenceId",
    "service",
    "status",
    "successList"
})
public class ChannelUserListResponsePojo {

    @JsonProperty("channelUserVO")
    private ChannelUserVO channelUserVO;
    @JsonProperty("channelUsersList")
    private List<ChannelUsersList> channelUsersList = null;
    @JsonProperty("errorMap")
    private ErrorMap errorMap;
    @JsonProperty("message")
    private String message;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("ownerUsersList")
    private List<OwnerUsersList> ownerUsersList = null;
    @JsonProperty("prodList")
    private List<ProdList> prodList = null;
    @JsonProperty("referenceId")
    private int referenceId;
    @JsonProperty("service")
    private String service;
    @JsonProperty("status")
    private String status;
    @JsonProperty("successList")
    private List<SuccessList> successList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("channelUserVO")
    public ChannelUserVO getChannelUserVO() {
        return channelUserVO;
    }

    @JsonProperty("channelUserVO")
    public void setChannelUserVO(ChannelUserVO channelUserVO) {
        this.channelUserVO = channelUserVO;
    }

    @JsonProperty("channelUsersList")
    public List<ChannelUsersList> getChannelUsersList() {
        return channelUsersList;
    }

    @JsonProperty("channelUsersList")
    public void setChannelUsersList(List<ChannelUsersList> channelUsersList) {
        this.channelUsersList = channelUsersList;
    }

    @JsonProperty("errorMap")
    public ErrorMap getErrorMap() {
        return errorMap;
    }

    @JsonProperty("errorMap")
    public void setErrorMap(ErrorMap errorMap) {
        this.errorMap = errorMap;
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

    @JsonProperty("ownerUsersList")
    public List<OwnerUsersList> getOwnerUsersList() {
        return ownerUsersList;
    }

    @JsonProperty("ownerUsersList")
    public void setOwnerUsersList(List<OwnerUsersList> ownerUsersList) {
        this.ownerUsersList = ownerUsersList;
    }

    @JsonProperty("prodList")
    public List<ProdList> getProdList() {
        return prodList;
    }

    @JsonProperty("prodList")
    public void setProdList(List<ProdList> prodList) {
        this.prodList = prodList;
    }

    @JsonProperty("referenceId")
    public int getReferenceId() {
        return referenceId;
    }

    @JsonProperty("referenceId")
    public void setReferenceId(int referenceId) {
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
    public List<SuccessList> getSuccessList() {
        return successList;
    }

    @JsonProperty("successList")
    public void setSuccessList(List<SuccessList> successList) {
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
