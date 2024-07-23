
package restassuredapi.pojo.lowthresholdreportresponsepojo;

import java.util.HashMap;
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
    "categoryName",
    "currentBalance",
    "dateTime",
    "mobileNumber",
    "previousBalance",
    "productName",
    "threshHold",
    "transactionID",
    "transferType",
    "userName",
    "userStatus"
})
@Generated("jsonschema2pojo")
public class LowThreshHoldData {

    @JsonProperty("categoryName")
    private String categoryName;
    @JsonProperty("currentBalance")
    private String currentBalance;
    @JsonProperty("dateTime")
    private String dateTime;
    @JsonProperty("mobileNumber")
    private String mobileNumber;
    @JsonProperty("previousBalance")
    private String previousBalance;
    @JsonProperty("productName")
    private String productName;
    @JsonProperty("threshHold")
    private String threshHold;
    @JsonProperty("transactionID")
    private String transactionID;
    @JsonProperty("transferType")
    private String transferType;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("userStatus")
    private String userStatus;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("categoryName")
    public String getCategoryName() {
        return categoryName;
    }

    @JsonProperty("categoryName")
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("currentBalance")
    public String getCurrentBalance() {
        return currentBalance;
    }

    @JsonProperty("currentBalance")
    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }

    @JsonProperty("dateTime")
    public String getDateTime() {
        return dateTime;
    }

    @JsonProperty("dateTime")
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @JsonProperty("mobileNumber")
    public String getMobileNumber() {
        return mobileNumber;
    }

    @JsonProperty("mobileNumber")
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @JsonProperty("previousBalance")
    public String getPreviousBalance() {
        return previousBalance;
    }

    @JsonProperty("previousBalance")
    public void setPreviousBalance(String previousBalance) {
        this.previousBalance = previousBalance;
    }

    @JsonProperty("productName")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("productName")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @JsonProperty("threshHold")
    public String getThreshHold() {
        return threshHold;
    }

    @JsonProperty("threshHold")
    public void setThreshHold(String threshHold) {
        this.threshHold = threshHold;
    }

    @JsonProperty("transactionID")
    public String getTransactionID() {
        return transactionID;
    }

    @JsonProperty("transactionID")
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    @JsonProperty("transferType")
    public String getTransferType() {
        return transferType;
    }

    @JsonProperty("transferType")
    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("userStatus")
    public String getUserStatus() {
        return userStatus;
    }

    @JsonProperty("userStatus")
    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
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
