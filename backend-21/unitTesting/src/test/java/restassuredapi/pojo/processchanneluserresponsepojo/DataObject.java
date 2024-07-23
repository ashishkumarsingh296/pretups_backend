
package restassuredapi.pojo.processchanneluserresponsepojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "date",
    "txnstatus",
    "message",
    "txnid",
    "errorcode"
})
public class DataObject {

    @JsonProperty("type")
    private String type;
    @JsonProperty("date")
    private String date;
    @JsonProperty("txnstatus")
    private String txnstatus;
    @JsonProperty("txnid")
    private String txnid;
    @JsonProperty("errorcode")
    private String errorcode;
    @JsonProperty("message")
    private String message;

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

    @JsonProperty("txnid")
    public String getTxnid() {
        return txnid;
    }

    @JsonProperty("txnid")
    public void setTxnid(String txnid) {
        this.txnid = txnid;
    }

    @JsonProperty("errorcode")
    public String getErrorcode() {
        return errorcode;
    }
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("errorcode")
    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

}
