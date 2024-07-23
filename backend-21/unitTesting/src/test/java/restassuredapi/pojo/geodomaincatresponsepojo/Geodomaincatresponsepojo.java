
package restassuredapi.pojo.geodomaincatresponsepojo;

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
    "geoList",
    "channelDomainList",
    "categoryList"
})
public class Geodomaincatresponsepojo {

    @JsonProperty("service")
    private String service;
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
    @JsonProperty("geoList")
    private List<GeoList> geoList = null;
    @JsonProperty("channelDomainList")
    private List<ChannelDomainList> channelDomainList = null;
    @JsonProperty("categoryList")
    private List<CategoryList> categoryList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Geodomaincatresponsepojo() {
    }

    /**
     * 
     * @param errorMap
     * @param successList
     * @param geoList
     * @param service
     * @param categoryList
     * @param messageCode
     * @param message
     * @param referenceId
     * @param channelDomainList
     * @param status
     */
    public Geodomaincatresponsepojo(String service, Object referenceId, String status, String messageCode, String message, Object errorMap, List<Object> successList, List<GeoList> geoList, List<ChannelDomainList> channelDomainList, List<CategoryList> categoryList) {
        super();
        this.service = service;
        this.referenceId = referenceId;
        this.status = status;
        this.messageCode = messageCode;
        this.message = message;
        this.errorMap = errorMap;
        this.successList = successList;
        this.geoList = geoList;
        this.channelDomainList = channelDomainList;
        this.categoryList = categoryList;
    }

    @JsonProperty("service")
    public String getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(String service) {
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

    @JsonProperty("geoList")
    public List<GeoList> getGeoList() {
        return geoList;
    }

    @JsonProperty("geoList")
    public void setGeoList(List<GeoList> geoList) {
        this.geoList = geoList;
    }

    @JsonProperty("channelDomainList")
    public List<ChannelDomainList> getChannelDomainList() {
        return channelDomainList;
    }

    @JsonProperty("channelDomainList")
    public void setChannelDomainList(List<ChannelDomainList> channelDomainList) {
        this.channelDomainList = channelDomainList;
    }

    @JsonProperty("categoryList")
    public List<CategoryList> getCategoryList() {
        return categoryList;
    }

    @JsonProperty("categoryList")
    public void setCategoryList(List<CategoryList> categoryList) {
        this.categoryList = categoryList;
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
