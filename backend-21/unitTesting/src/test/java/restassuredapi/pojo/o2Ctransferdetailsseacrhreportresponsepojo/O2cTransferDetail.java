
package restassuredapi.pojo.o2Ctransferdetailsseacrhreportresponsepojo;

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
"transdateTime",
"transactionID",
"transactionStatus",
"distributionType",
"senderName",
"senderMsisdn",
"receiverName",
"receiverMsisdn",
"receiverQuantity",
"transferCategory",
"transferSubType",
"modifiedOn",
"externalTransferNumber",
"externalTransferDate",
"transactionMode",
"requestedQuantity",
"approvedQuantity",
"commission",
"cumulativeBaseCommission",
"tax1",
"tax2",
"tax3",
"payableAmount",
"netPayableAmount",
"initiatorRemarks",
"firstLevelApprovedQuantity",
"secondLevelApprovedQuantity",
"thirdLevelApprovedQuantity",
"approver1Remarks",
"approver2Remarks",
"approver3Remarks",
"paymentInstType",
"paymentInstNumber",
"paymentInstDate",
"productName",
"voucherBatchNumber",
"vomsProductName",
"batchType",
"totalNoofVouchers",
"fromSerialNumber",
"toSerialNumber",
"voucherSegment",
"voucherType",
"voucherDenomination",
"requestGateWay",
"senderDebitQuantity",
"receiverPreviousBalance",
"receiverPostBalance"
})
@Generated("jsonschema2pojo")
public class O2cTransferDetail {

@JsonProperty("transdateTime")
private String transdateTime;
@JsonProperty("transactionID")
private String transactionID;
@JsonProperty("transactionStatus")
private String transactionStatus;
@JsonProperty("distributionType")
private String distributionType;
@JsonProperty("senderName")
private String senderName;
@JsonProperty("senderMsisdn")
private Object senderMsisdn;
@JsonProperty("receiverName")
private String receiverName;
@JsonProperty("receiverMsisdn")
private String receiverMsisdn;
@JsonProperty("receiverQuantity")
private String receiverQuantity;
@JsonProperty("transferCategory")
private String transferCategory;
@JsonProperty("transferSubType")
private String transferSubType;
@JsonProperty("modifiedOn")
private String modifiedOn;
@JsonProperty("externalTransferNumber")
private String externalTransferNumber;
@JsonProperty("externalTransferDate")
private String externalTransferDate;
@JsonProperty("transactionMode")
private String transactionMode;
@JsonProperty("requestedQuantity")
private String requestedQuantity;
@JsonProperty("approvedQuantity")
private String approvedQuantity;
@JsonProperty("commission")
private String commission;
@JsonProperty("cumulativeBaseCommission")
private String cumulativeBaseCommission;
@JsonProperty("tax1")
private String tax1;
@JsonProperty("tax2")
private String tax2;
@JsonProperty("tax3")
private String tax3;
@JsonProperty("payableAmount")
private String payableAmount;
@JsonProperty("netPayableAmount")
private String netPayableAmount;
@JsonProperty("initiatorRemarks")
private String initiatorRemarks;
@JsonProperty("firstLevelApprovedQuantity")
private String firstLevelApprovedQuantity;
@JsonProperty("secondLevelApprovedQuantity")
private String secondLevelApprovedQuantity;
@JsonProperty("thirdLevelApprovedQuantity")
private String thirdLevelApprovedQuantity;
@JsonProperty("approver1Remarks")
private Object approver1Remarks;
@JsonProperty("approver2Remarks")
private String approver2Remarks;
@JsonProperty("approver3Remarks")
private String approver3Remarks;
@JsonProperty("paymentInstType")
private String paymentInstType;
@JsonProperty("paymentInstNumber")
private Object paymentInstNumber;
@JsonProperty("paymentInstDate")
private String paymentInstDate;
@JsonProperty("productName")
private String productName;
@JsonProperty("voucherBatchNumber")
private Object voucherBatchNumber;
@JsonProperty("vomsProductName")
private Object vomsProductName;
@JsonProperty("batchType")
private Object batchType;
@JsonProperty("totalNoofVouchers")
private Object totalNoofVouchers;
@JsonProperty("fromSerialNumber")
private Object fromSerialNumber;
@JsonProperty("toSerialNumber")
private Object toSerialNumber;
@JsonProperty("voucherSegment")
private Object voucherSegment;
@JsonProperty("voucherType")
private Object voucherType;
@JsonProperty("voucherDenomination")
private Object voucherDenomination;
@JsonProperty("requestGateWay")
private Object requestGateWay;
@JsonProperty("senderDebitQuantity")
private String senderDebitQuantity;
@JsonProperty("receiverPreviousBalance")
private String receiverPreviousBalance;
@JsonProperty("receiverPostBalance")
private String receiverPostBalance;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("transdateTime")
public String getTransdateTime() {
return transdateTime;
}

@JsonProperty("transdateTime")
public void setTransdateTime(String transdateTime) {
this.transdateTime = transdateTime;
}

@JsonProperty("transactionID")
public String getTransactionID() {
return transactionID;
}

@JsonProperty("transactionID")
public void setTransactionID(String transactionID) {
this.transactionID = transactionID;
}

@JsonProperty("transactionStatus")
public String getTransactionStatus() {
return transactionStatus;
}

@JsonProperty("transactionStatus")
public void setTransactionStatus(String transactionStatus) {
this.transactionStatus = transactionStatus;
}

@JsonProperty("distributionType")
public String getDistributionType() {
return distributionType;
}

@JsonProperty("distributionType")
public void setDistributionType(String distributionType) {
this.distributionType = distributionType;
}

@JsonProperty("senderName")
public String getSenderName() {
return senderName;
}

@JsonProperty("senderName")
public void setSenderName(String senderName) {
this.senderName = senderName;
}

@JsonProperty("senderMsisdn")
public Object getSenderMsisdn() {
return senderMsisdn;
}

@JsonProperty("senderMsisdn")
public void setSenderMsisdn(Object senderMsisdn) {
this.senderMsisdn = senderMsisdn;
}

@JsonProperty("receiverName")
public String getReceiverName() {
return receiverName;
}

@JsonProperty("receiverName")
public void setReceiverName(String receiverName) {
this.receiverName = receiverName;
}

@JsonProperty("receiverMsisdn")
public String getReceiverMsisdn() {
return receiverMsisdn;
}

@JsonProperty("receiverMsisdn")
public void setReceiverMsisdn(String receiverMsisdn) {
this.receiverMsisdn = receiverMsisdn;
}

@JsonProperty("receiverQuantity")
public String getReceiverQuantity() {
return receiverQuantity;
}

@JsonProperty("receiverQuantity")
public void setReceiverQuantity(String receiverQuantity) {
this.receiverQuantity = receiverQuantity;
}

@JsonProperty("transferCategory")
public String getTransferCategory() {
return transferCategory;
}

@JsonProperty("transferCategory")
public void setTransferCategory(String transferCategory) {
this.transferCategory = transferCategory;
}

@JsonProperty("transferSubType")
public String getTransferSubType() {
return transferSubType;
}

@JsonProperty("transferSubType")
public void setTransferSubType(String transferSubType) {
this.transferSubType = transferSubType;
}

@JsonProperty("modifiedOn")
public String getModifiedOn() {
return modifiedOn;
}

@JsonProperty("modifiedOn")
public void setModifiedOn(String modifiedOn) {
this.modifiedOn = modifiedOn;
}

@JsonProperty("externalTransferNumber")
public String getExternalTransferNumber() {
return externalTransferNumber;
}

@JsonProperty("externalTransferNumber")
public void setExternalTransferNumber(String externalTransferNumber) {
this.externalTransferNumber = externalTransferNumber;
}

@JsonProperty("externalTransferDate")
public String getExternalTransferDate() {
return externalTransferDate;
}

@JsonProperty("externalTransferDate")
public void setExternalTransferDate(String externalTransferDate) {
this.externalTransferDate = externalTransferDate;
}

@JsonProperty("transactionMode")
public String getTransactionMode() {
return transactionMode;
}

@JsonProperty("transactionMode")
public void setTransactionMode(String transactionMode) {
this.transactionMode = transactionMode;
}

@JsonProperty("requestedQuantity")
public String getRequestedQuantity() {
return requestedQuantity;
}

@JsonProperty("requestedQuantity")
public void setRequestedQuantity(String requestedQuantity) {
this.requestedQuantity = requestedQuantity;
}

@JsonProperty("approvedQuantity")
public String getApprovedQuantity() {
return approvedQuantity;
}

@JsonProperty("approvedQuantity")
public void setApprovedQuantity(String approvedQuantity) {
this.approvedQuantity = approvedQuantity;
}

@JsonProperty("commission")
public String getCommission() {
return commission;
}

@JsonProperty("commission")
public void setCommission(String commission) {
this.commission = commission;
}

@JsonProperty("cumulativeBaseCommission")
public String getCumulativeBaseCommission() {
return cumulativeBaseCommission;
}

@JsonProperty("cumulativeBaseCommission")
public void setCumulativeBaseCommission(String cumulativeBaseCommission) {
this.cumulativeBaseCommission = cumulativeBaseCommission;
}

@JsonProperty("tax1")
public String getTax1() {
return tax1;
}

@JsonProperty("tax1")
public void setTax1(String tax1) {
this.tax1 = tax1;
}

@JsonProperty("tax2")
public String getTax2() {
return tax2;
}

@JsonProperty("tax2")
public void setTax2(String tax2) {
this.tax2 = tax2;
}

@JsonProperty("tax3")
public String getTax3() {
return tax3;
}

@JsonProperty("tax3")
public void setTax3(String tax3) {
this.tax3 = tax3;
}

@JsonProperty("payableAmount")
public String getPayableAmount() {
return payableAmount;
}

@JsonProperty("payableAmount")
public void setPayableAmount(String payableAmount) {
this.payableAmount = payableAmount;
}

@JsonProperty("netPayableAmount")
public String getNetPayableAmount() {
return netPayableAmount;
}

@JsonProperty("netPayableAmount")
public void setNetPayableAmount(String netPayableAmount) {
this.netPayableAmount = netPayableAmount;
}

@JsonProperty("initiatorRemarks")
public String getInitiatorRemarks() {
return initiatorRemarks;
}

@JsonProperty("initiatorRemarks")
public void setInitiatorRemarks(String initiatorRemarks) {
this.initiatorRemarks = initiatorRemarks;
}

@JsonProperty("firstLevelApprovedQuantity")
public String getFirstLevelApprovedQuantity() {
return firstLevelApprovedQuantity;
}

@JsonProperty("firstLevelApprovedQuantity")
public void setFirstLevelApprovedQuantity(String firstLevelApprovedQuantity) {
this.firstLevelApprovedQuantity = firstLevelApprovedQuantity;
}

@JsonProperty("secondLevelApprovedQuantity")
public String getSecondLevelApprovedQuantity() {
return secondLevelApprovedQuantity;
}

@JsonProperty("secondLevelApprovedQuantity")
public void setSecondLevelApprovedQuantity(String secondLevelApprovedQuantity) {
this.secondLevelApprovedQuantity = secondLevelApprovedQuantity;
}

@JsonProperty("thirdLevelApprovedQuantity")
public String getThirdLevelApprovedQuantity() {
return thirdLevelApprovedQuantity;
}

@JsonProperty("thirdLevelApprovedQuantity")
public void setThirdLevelApprovedQuantity(String thirdLevelApprovedQuantity) {
this.thirdLevelApprovedQuantity = thirdLevelApprovedQuantity;
}

@JsonProperty("approver1Remarks")
public Object getApprover1Remarks() {
return approver1Remarks;
}

@JsonProperty("approver1Remarks")
public void setApprover1Remarks(Object approver1Remarks) {
this.approver1Remarks = approver1Remarks;
}

@JsonProperty("approver2Remarks")
public String getApprover2Remarks() {
return approver2Remarks;
}

@JsonProperty("approver2Remarks")
public void setApprover2Remarks(String approver2Remarks) {
this.approver2Remarks = approver2Remarks;
}

@JsonProperty("approver3Remarks")
public String getApprover3Remarks() {
return approver3Remarks;
}

@JsonProperty("approver3Remarks")
public void setApprover3Remarks(String approver3Remarks) {
this.approver3Remarks = approver3Remarks;
}

@JsonProperty("paymentInstType")
public String getPaymentInstType() {
return paymentInstType;
}

@JsonProperty("paymentInstType")
public void setPaymentInstType(String paymentInstType) {
this.paymentInstType = paymentInstType;
}

@JsonProperty("paymentInstNumber")
public Object getPaymentInstNumber() {
return paymentInstNumber;
}

@JsonProperty("paymentInstNumber")
public void setPaymentInstNumber(Object paymentInstNumber) {
this.paymentInstNumber = paymentInstNumber;
}

@JsonProperty("paymentInstDate")
public String getPaymentInstDate() {
return paymentInstDate;
}

@JsonProperty("paymentInstDate")
public void setPaymentInstDate(String paymentInstDate) {
this.paymentInstDate = paymentInstDate;
}

@JsonProperty("productName")
public String getProductName() {
return productName;
}

@JsonProperty("productName")
public void setProductName(String productName) {
this.productName = productName;
}

@JsonProperty("voucherBatchNumber")
public Object getVoucherBatchNumber() {
return voucherBatchNumber;
}

@JsonProperty("voucherBatchNumber")
public void setVoucherBatchNumber(Object voucherBatchNumber) {
this.voucherBatchNumber = voucherBatchNumber;
}

@JsonProperty("vomsProductName")
public Object getVomsProductName() {
return vomsProductName;
}

@JsonProperty("vomsProductName")
public void setVomsProductName(Object vomsProductName) {
this.vomsProductName = vomsProductName;
}

@JsonProperty("batchType")
public Object getBatchType() {
return batchType;
}

@JsonProperty("batchType")
public void setBatchType(Object batchType) {
this.batchType = batchType;
}

@JsonProperty("totalNoofVouchers")
public Object getTotalNoofVouchers() {
return totalNoofVouchers;
}

@JsonProperty("totalNoofVouchers")
public void setTotalNoofVouchers(Object totalNoofVouchers) {
this.totalNoofVouchers = totalNoofVouchers;
}

@JsonProperty("fromSerialNumber")
public Object getFromSerialNumber() {
return fromSerialNumber;
}

@JsonProperty("fromSerialNumber")
public void setFromSerialNumber(Object fromSerialNumber) {
this.fromSerialNumber = fromSerialNumber;
}

@JsonProperty("toSerialNumber")
public Object getToSerialNumber() {
return toSerialNumber;
}

@JsonProperty("toSerialNumber")
public void setToSerialNumber(Object toSerialNumber) {
this.toSerialNumber = toSerialNumber;
}

@JsonProperty("voucherSegment")
public Object getVoucherSegment() {
return voucherSegment;
}

@JsonProperty("voucherSegment")
public void setVoucherSegment(Object voucherSegment) {
this.voucherSegment = voucherSegment;
}

@JsonProperty("voucherType")
public Object getVoucherType() {
return voucherType;
}

@JsonProperty("voucherType")
public void setVoucherType(Object voucherType) {
this.voucherType = voucherType;
}

@JsonProperty("voucherDenomination")
public Object getVoucherDenomination() {
return voucherDenomination;
}

@JsonProperty("voucherDenomination")
public void setVoucherDenomination(Object voucherDenomination) {
this.voucherDenomination = voucherDenomination;
}

@JsonProperty("requestGateWay")
public Object getRequestGateWay() {
return requestGateWay;
}

@JsonProperty("requestGateWay")
public void setRequestGateWay(Object requestGateWay) {
this.requestGateWay = requestGateWay;
}

@JsonProperty("senderDebitQuantity")
public String getSenderDebitQuantity() {
return senderDebitQuantity;
}

@JsonProperty("senderDebitQuantity")
public void setSenderDebitQuantity(String senderDebitQuantity) {
this.senderDebitQuantity = senderDebitQuantity;
}

@JsonProperty("receiverPreviousBalance")
public String getReceiverPreviousBalance() {
return receiverPreviousBalance;
}

@JsonProperty("receiverPreviousBalance")
public void setReceiverPreviousBalance(String receiverPreviousBalance) {
this.receiverPreviousBalance = receiverPreviousBalance;
}

@JsonProperty("receiverPostBalance")
public String getReceiverPostBalance() {
return receiverPostBalance;
}

@JsonProperty("receiverPostBalance")
public void setReceiverPostBalance(String receiverPostBalance) {
this.receiverPostBalance = receiverPostBalance;
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