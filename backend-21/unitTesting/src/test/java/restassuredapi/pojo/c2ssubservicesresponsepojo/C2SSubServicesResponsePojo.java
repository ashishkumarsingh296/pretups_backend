package restassuredapi.pojo.c2ssubservicesresponsepojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status",
    "messageCode",
    "message",
    "Sub Services List"
})
public class C2SSubServicesResponsePojo {
    @JsonProperty("status")
    private Long status;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("status")
    public Long getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Long status) {
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

    @JsonProperty("Sub Services List")
    private HashMap<String, List<SubServiceVO> > subServicesList = null;

    @JsonProperty("Sub Services List")
    public HashMap<String, List<SubServiceVO> > getSubServicesList() {
        return subServicesList;
    }

    @JsonProperty("Sub Services List")
    public void setSubServicesList(HashMap<String, List<SubServiceVO> > subServicesList) {
        this.subServicesList = subServicesList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("subServicesList", subServicesList).toString();
    }
}


class SubServiceVO {
    @JsonProperty("subServiceName")
    private String subServiceName;
    @JsonProperty("subServiceCode")
    private String subServiceCode;

    @JsonProperty("subServiceName")
    public String getSubServiceName() {
        return subServiceName;
    }

    @JsonProperty("subServiceName")
    public void setSubServiceName(String subServiceName) {
        this.subServiceName = subServiceName;
    }

    @JsonProperty("subServiceCode")
    public String getSubServiceCode() {
        return subServiceCode;
    }

    @JsonProperty("subServiceCode")
    public void setSubServiceCode(String subServiceCode) {
        this.subServiceCode = subServiceCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("subServiceName", subServiceName).append("subServiceCode", subServiceCode).toString();
    }
}