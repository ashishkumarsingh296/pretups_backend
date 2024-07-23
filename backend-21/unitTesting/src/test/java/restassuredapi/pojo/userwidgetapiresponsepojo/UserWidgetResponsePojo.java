
package restassuredapi.pojo.userwidgetapiresponsepojo;

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
    "widgetList",
    "status",
    "messageCode",
    "message"
})
public class UserWidgetResponsePojo {

    @JsonProperty("widgetList")
    private List<String> widgetList = null;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("messageCode")
    private Object messageCode;
    @JsonProperty("message")
    private Object message;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("widgetList")
    public List<String> getWidgetList() {
        return widgetList;
    }

    @JsonProperty("widgetList")
    public void setWidgetList(List<String> widgetList) {
        this.widgetList = widgetList;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("messageCode")
    public Object getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(Object messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("message")
    public Object getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(Object message) {
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
