package restassuredapi.pojo.c2cenquiryrequestpojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	    "category",
	    "distributionType",
	    "domain",
	    "fromDate",
	    "geography",
	    "orderStatus",
	    "productCode",
	    "receiverMsisdn",
	    "senderMsisdn",
	    "toDate",
	    "transactionID",
	    "transferSubType",
	    "userID"
	
})
public class C2cAndO2cEnquiryRequestVO {
	 @JsonProperty("transactionID")
	private String transactionID;
	
	 @JsonProperty("transferSubType")
	private String transferSubType;
	
	 @JsonProperty("fromDate")
	private String fromDate;
	
	@JsonProperty("toDate")
	private String toDate;
	
	@JsonProperty("senderMsisdn")
	private String senderMsisdn;
	
	@JsonProperty("receiverMsisdn")
	private String receiverMsisdn;
	
	@JsonProperty("userID")
	private String userID;
	
	@JsonProperty("distributionType")
	private String distributionType;
	
	@JsonProperty("orderStatus")
	private String orderStatus;
	
	@JsonProperty("productCode")
	private String productCode;
	
	
	
	@JsonProperty("domain")
	private String domain;
	
    @JsonProperty("category")
	private String category;
	
    @JsonProperty("geography")
	private String geography;
   
    @JsonProperty("domain")
	public String getDomain() {
		return domain;
	}
	@JsonProperty("domain")
	public void setDomain(String domain) {
		this.domain = domain;
	}
	 @JsonProperty("category")
	public String getCategory() {
		return category;
	}
	 @JsonProperty("category")
	public void setCategory(String category) {
		this.category = category;
	}
	  @JsonProperty("geography")
	public String getGeography() {
		return geography;
	}
	  @JsonProperty("geography")
	public void setGeography(String geography) {
		this.geography = geography;
	}

	@JsonProperty("transactionID")
	public String getTransactionID() {
		return transactionID;
	}
	@JsonProperty("transactionID")
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	@JsonProperty("transferSubType")
	public String getTransferSubType() {
		return transferSubType;
	}
	@JsonProperty("transferSubType")
	public void setTransferSubType(String transferSubType) {
		this.transferSubType = transferSubType;
	}
	 @JsonProperty("fromDate")
	public String getFromDate() {
		return fromDate;
	}
	 @JsonProperty("fromDate")
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	 @JsonProperty("toDate")
	public String getToDate() {
		return toDate;
	}
	 @JsonProperty("toDate")
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	 @JsonProperty("senderMsisdn")
	public String getSenderMsisdn() {
		return senderMsisdn;
	}
	 @JsonProperty("senderMsisdn")
	public void setSenderMsisdn(String senderMsisdn) {
		this.senderMsisdn = senderMsisdn;
	}
	 @JsonProperty("receiverMsisdn")
	public String getReceiverMsisdn() {
		return receiverMsisdn;
	}
	 @JsonProperty("receiverMsisdn")
	public void setReceiverMsisdn(String receiverMsisdn) {
		this.receiverMsisdn = receiverMsisdn;
	}
	 @JsonProperty("userID")
	public String getUserID() {
		return userID;
	}
	 @JsonProperty("userID")
	public void setUserID(String userID) {
		this.userID = userID;
	}
	 @JsonProperty("distributionType") 
	public String getDistributionType() {
		return distributionType;
	}
	 @JsonProperty("distributionType")
	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}
	 @JsonProperty("orderStatus")
	public String getOrderStatus() {
		return orderStatus;
	}
	 @JsonProperty("orderStatus")
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	 
	 
	 
	 @JsonProperty("productCode")
	public String getProductCode() {
		return productCode;
	}
	@JsonProperty("productCode")
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	@Override
	public String toString() {
		return "ChannelEnquiryRequestVO [transactionID=" + transactionID + ", transferSubType=" + transferSubType
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + ", senderMsisdn=" + senderMsisdn
				+ ", receiverMsisdn=" + receiverMsisdn + ", userID=" + userID + ", distributionType=" + distributionType + ", orderStatus=" + orderStatus + ", domain=" + domain
				+ ", category=" + category + ", geography=" + geography + "]";
	}

}