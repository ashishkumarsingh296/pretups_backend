
package restassuredapi.pojo.userdetiresponsepojo;

import java.util.HashMap;
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
    "firstname",
    "lastname",
    "categoryname",
    "categorycode",
    "usernameprefix"
})
public class DataObject {

    @JsonProperty("type")
    private String type;
    @JsonProperty("date")
    private String date;
    @JsonProperty("txnstatus")
    private String txnstatus;
    @JsonProperty("firstname")
    private String firstname;
    @JsonProperty("lastname")
    private String lastname;
    @JsonProperty("categoryname")
    private String categoryname;
    @JsonProperty("categorycode")
    private String categorycode;
    @JsonProperty("usernameprefix")
    private String usernameprefix;
    @JsonProperty("message")
    private String message;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }
    
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

    @JsonProperty("firstname")
    public String getFirstname() {
        return firstname;
    }

    @JsonProperty("firstname")
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @JsonProperty("lastname")
    public String getLastname() {
        return lastname;
    }

    @JsonProperty("lastname")
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @JsonProperty("categoryname")
    public String getCategoryname() {
        return categoryname;
    }

    @JsonProperty("categoryname")
    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    @JsonProperty("categorycode")
    public String getCategorycode() {
        return categorycode;
    }

    @JsonProperty("categorycode")
    public void setCategorycode(String categorycode) {
        this.categorycode = categorycode;
    }

    @JsonProperty("usernameprefix")
    public String getUsernameprefix() {
        return usernameprefix;
    }

    @JsonProperty("usernameprefix")
    public void setUsernameprefix(String usernameprefix) {
        this.usernameprefix = usernameprefix;
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
