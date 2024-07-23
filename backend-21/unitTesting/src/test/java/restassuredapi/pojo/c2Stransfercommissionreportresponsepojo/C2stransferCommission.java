
package restassuredapi.pojo.c2Stransfercommissionreportresponsepojo;

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
    "bonus",
    "creditedAmount",
    "differentialApplicable",
    "differentialCommission",
    "differentialGiven",
    "grandParentCategory",
    "grandParentGeography",
    "grandParentName",
    "grantParentMobileNumber",
    "loginID",
    "ownerCategory",
    "ownerGeography",
    "ownerMobileNumber",
    "ownerName",
    "parentCategory",
    "parentGeography",
    "parentMobileNumber",
    "parentName",
    "pinSentTo",
    "productName",
    "rate",
    "receiverMobileNumber",
    "receiverServiceClass",
    "requestedAmount",
    "requestedSource",
    "senderCategory",
    "senderGeography",
    "senderMobileNumber",
    "senderName",
    "senderNetworkCode",
    "serialNumber",
    "service",
    "services",
    "status",
    "subService",
    "transactionCount",
    "transactionID",
    "transdateTime",
    "transferAmount",
    "transferValue"
})
@Generated("jsonschema2pojo")
public class C2stransferCommission {

    @JsonProperty("bonus")
    private String bonus;
    @JsonProperty("creditedAmount")
    private String creditedAmount;
    @JsonProperty("differentialApplicable")
    private String differentialApplicable;
    @JsonProperty("differentialCommission")
    private String differentialCommission;
    @JsonProperty("differentialGiven")
    private String differentialGiven;
    @JsonProperty("grandParentCategory")
    private String grandParentCategory;
    @JsonProperty("grandParentGeography")
    private String grandParentGeography;
    @JsonProperty("grandParentName")
    private String grandParentName;
    @JsonProperty("grantParentMobileNumber")
    private String grantParentMobileNumber;
    @JsonProperty("loginID")
    private String loginID;
    @JsonProperty("ownerCategory")
    private String ownerCategory;
    @JsonProperty("ownerGeography")
    private String ownerGeography;
    @JsonProperty("ownerMobileNumber")
    private String ownerMobileNumber;
    @JsonProperty("ownerName")
    private String ownerName;
    @JsonProperty("parentCategory")
    private String parentCategory;
    @JsonProperty("parentGeography")
    private String parentGeography;
    @JsonProperty("parentMobileNumber")
    private String parentMobileNumber;
    @JsonProperty("parentName")
    private String parentName;
    @JsonProperty("pinSentTo")
    private String pinSentTo;
    @JsonProperty("productName")
    private String productName;
    @JsonProperty("rate")
    private String rate;
    @JsonProperty("receiverMobileNumber")
    private String receiverMobileNumber;
    @JsonProperty("receiverServiceClass")
    private String receiverServiceClass;
    @JsonProperty("requestedAmount")
    private String requestedAmount;
    @JsonProperty("requestedSource")
    private String requestedSource;
    @JsonProperty("senderCategory")
    private String senderCategory;
    @JsonProperty("senderGeography")
    private String senderGeography;
    @JsonProperty("senderMobileNumber")
    private String senderMobileNumber;
    @JsonProperty("senderName")
    private String senderName;
    @JsonProperty("senderNetworkCode")
    private String senderNetworkCode;
    @JsonProperty("serialNumber")
    private String serialNumber;
    @JsonProperty("service")
    private String service;
    @JsonProperty("services")
    private String services;
    @JsonProperty("status")
    private String status;
    @JsonProperty("subService")
    private String subService;
    @JsonProperty("transactionCount")
    private String transactionCount;
    @JsonProperty("transactionID")
    private String transactionID;
    @JsonProperty("transdateTime")
    private String transdateTime;
    @JsonProperty("transferAmount")
    private String transferAmount;
    @JsonProperty("transferValue")
    private String transferValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("bonus")
    public String getBonus() {
        return bonus;
    }

    @JsonProperty("bonus")
    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    @JsonProperty("creditedAmount")
    public String getCreditedAmount() {
        return creditedAmount;
    }

    @JsonProperty("creditedAmount")
    public void setCreditedAmount(String creditedAmount) {
        this.creditedAmount = creditedAmount;
    }

    @JsonProperty("differentialApplicable")
    public String getDifferentialApplicable() {
        return differentialApplicable;
    }

    @JsonProperty("differentialApplicable")
    public void setDifferentialApplicable(String differentialApplicable) {
        this.differentialApplicable = differentialApplicable;
    }

    @JsonProperty("differentialCommission")
    public String getDifferentialCommission() {
        return differentialCommission;
    }

    @JsonProperty("differentialCommission")
    public void setDifferentialCommission(String differentialCommission) {
        this.differentialCommission = differentialCommission;
    }

    @JsonProperty("differentialGiven")
    public String getDifferentialGiven() {
        return differentialGiven;
    }

    @JsonProperty("differentialGiven")
    public void setDifferentialGiven(String differentialGiven) {
        this.differentialGiven = differentialGiven;
    }

    @JsonProperty("grandParentCategory")
    public String getGrandParentCategory() {
        return grandParentCategory;
    }

    @JsonProperty("grandParentCategory")
    public void setGrandParentCategory(String grandParentCategory) {
        this.grandParentCategory = grandParentCategory;
    }

    @JsonProperty("grandParentGeography")
    public String getGrandParentGeography() {
        return grandParentGeography;
    }

    @JsonProperty("grandParentGeography")
    public void setGrandParentGeography(String grandParentGeography) {
        this.grandParentGeography = grandParentGeography;
    }

    @JsonProperty("grandParentName")
    public String getGrandParentName() {
        return grandParentName;
    }

    @JsonProperty("grandParentName")
    public void setGrandParentName(String grandParentName) {
        this.grandParentName = grandParentName;
    }

    @JsonProperty("grantParentMobileNumber")
    public String getGrantParentMobileNumber() {
        return grantParentMobileNumber;
    }

    @JsonProperty("grantParentMobileNumber")
    public void setGrantParentMobileNumber(String grantParentMobileNumber) {
        this.grantParentMobileNumber = grantParentMobileNumber;
    }

    @JsonProperty("loginID")
    public String getLoginID() {
        return loginID;
    }

    @JsonProperty("loginID")
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    @JsonProperty("ownerCategory")
    public String getOwnerCategory() {
        return ownerCategory;
    }

    @JsonProperty("ownerCategory")
    public void setOwnerCategory(String ownerCategory) {
        this.ownerCategory = ownerCategory;
    }

    @JsonProperty("ownerGeography")
    public String getOwnerGeography() {
        return ownerGeography;
    }

    @JsonProperty("ownerGeography")
    public void setOwnerGeography(String ownerGeography) {
        this.ownerGeography = ownerGeography;
    }

    @JsonProperty("ownerMobileNumber")
    public String getOwnerMobileNumber() {
        return ownerMobileNumber;
    }

    @JsonProperty("ownerMobileNumber")
    public void setOwnerMobileNumber(String ownerMobileNumber) {
        this.ownerMobileNumber = ownerMobileNumber;
    }

    @JsonProperty("ownerName")
    public String getOwnerName() {
        return ownerName;
    }

    @JsonProperty("ownerName")
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @JsonProperty("parentCategory")
    public String getParentCategory() {
        return parentCategory;
    }

    @JsonProperty("parentCategory")
    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    @JsonProperty("parentGeography")
    public String getParentGeography() {
        return parentGeography;
    }

    @JsonProperty("parentGeography")
    public void setParentGeography(String parentGeography) {
        this.parentGeography = parentGeography;
    }

    @JsonProperty("parentMobileNumber")
    public String getParentMobileNumber() {
        return parentMobileNumber;
    }

    @JsonProperty("parentMobileNumber")
    public void setParentMobileNumber(String parentMobileNumber) {
        this.parentMobileNumber = parentMobileNumber;
    }

    @JsonProperty("parentName")
    public String getParentName() {
        return parentName;
    }

    @JsonProperty("parentName")
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @JsonProperty("pinSentTo")
    public String getPinSentTo() {
        return pinSentTo;
    }

    @JsonProperty("pinSentTo")
    public void setPinSentTo(String pinSentTo) {
        this.pinSentTo = pinSentTo;
    }

    @JsonProperty("productName")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("productName")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @JsonProperty("rate")
    public String getRate() {
        return rate;
    }

    @JsonProperty("rate")
    public void setRate(String rate) {
        this.rate = rate;
    }

    @JsonProperty("receiverMobileNumber")
    public String getReceiverMobileNumber() {
        return receiverMobileNumber;
    }

    @JsonProperty("receiverMobileNumber")
    public void setReceiverMobileNumber(String receiverMobileNumber) {
        this.receiverMobileNumber = receiverMobileNumber;
    }

    @JsonProperty("receiverServiceClass")
    public String getReceiverServiceClass() {
        return receiverServiceClass;
    }

    @JsonProperty("receiverServiceClass")
    public void setReceiverServiceClass(String receiverServiceClass) {
        this.receiverServiceClass = receiverServiceClass;
    }

    @JsonProperty("requestedAmount")
    public String getRequestedAmount() {
        return requestedAmount;
    }

    @JsonProperty("requestedAmount")
    public void setRequestedAmount(String requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    @JsonProperty("requestedSource")
    public String getRequestedSource() {
        return requestedSource;
    }

    @JsonProperty("requestedSource")
    public void setRequestedSource(String requestedSource) {
        this.requestedSource = requestedSource;
    }

    @JsonProperty("senderCategory")
    public String getSenderCategory() {
        return senderCategory;
    }

    @JsonProperty("senderCategory")
    public void setSenderCategory(String senderCategory) {
        this.senderCategory = senderCategory;
    }

    @JsonProperty("senderGeography")
    public String getSenderGeography() {
        return senderGeography;
    }

    @JsonProperty("senderGeography")
    public void setSenderGeography(String senderGeography) {
        this.senderGeography = senderGeography;
    }

    @JsonProperty("senderMobileNumber")
    public String getSenderMobileNumber() {
        return senderMobileNumber;
    }

    @JsonProperty("senderMobileNumber")
    public void setSenderMobileNumber(String senderMobileNumber) {
        this.senderMobileNumber = senderMobileNumber;
    }

    @JsonProperty("senderName")
    public String getSenderName() {
        return senderName;
    }

    @JsonProperty("senderName")
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @JsonProperty("senderNetworkCode")
    public String getSenderNetworkCode() {
        return senderNetworkCode;
    }

    @JsonProperty("senderNetworkCode")
    public void setSenderNetworkCode(String senderNetworkCode) {
        this.senderNetworkCode = senderNetworkCode;
    }

    @JsonProperty("serialNumber")
    public String getSerialNumber() {
        return serialNumber;
    }

    @JsonProperty("serialNumber")
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @JsonProperty("service")
    public String getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(String service) {
        this.service = service;
    }

    @JsonProperty("services")
    public String getServices() {
        return services;
    }

    @JsonProperty("services")
    public void setServices(String services) {
        this.services = services;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("subService")
    public String getSubService() {
        return subService;
    }

    @JsonProperty("subService")
    public void setSubService(String subService) {
        this.subService = subService;
    }

    @JsonProperty("transactionCount")
    public String getTransactionCount() {
        return transactionCount;
    }

    @JsonProperty("transactionCount")
    public void setTransactionCount(String transactionCount) {
        this.transactionCount = transactionCount;
    }

    @JsonProperty("transactionID")
    public String getTransactionID() {
        return transactionID;
    }

    @JsonProperty("transactionID")
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    @JsonProperty("transdateTime")
    public String getTransdateTime() {
        return transdateTime;
    }

    @JsonProperty("transdateTime")
    public void setTransdateTime(String transdateTime) {
        this.transdateTime = transdateTime;
    }

    @JsonProperty("transferAmount")
    public String getTransferAmount() {
        return transferAmount;
    }

    @JsonProperty("transferAmount")
    public void setTransferAmount(String transferAmount) {
        this.transferAmount = transferAmount;
    }

    @JsonProperty("transferValue")
    public String getTransferValue() {
        return transferValue;
    }

    @JsonProperty("transferValue")
    public void setTransferValue(String transferValue) {
        this.transferValue = transferValue;
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
