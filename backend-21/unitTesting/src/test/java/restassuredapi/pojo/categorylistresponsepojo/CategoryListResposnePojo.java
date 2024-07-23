package restassuredapi.pojo.categorylistresponsepojo;
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
"categoryList",
"message",
"messageCode",
"status",
"transactionId"
})
public class CategoryListResposnePojo {

@JsonProperty("categoryList")
private List<CategoryList> categoryList = null;
@JsonProperty("message")
private String message;
@JsonProperty("messageCode")
private String messageCode;
@JsonProperty("status")
private int status;
@JsonProperty("transactionId")
private String transactionId;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("categoryList")
public List<CategoryList> getCategoryList() {
return categoryList;
}

@JsonProperty("categoryList")
public void setCategoryList(List<CategoryList> categoryList) {
this.categoryList = categoryList;
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

@JsonProperty("status")
public int getStatus() {
return status;
}

@JsonProperty("status")
public void setStatus(int status) {
this.status = status;
}

@JsonProperty("transactionId")
public String getTransactionId() {
return transactionId;
}

@JsonProperty("transactionId")
public void setTransactionId(String transactionId) {
this.transactionId = transactionId;
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

