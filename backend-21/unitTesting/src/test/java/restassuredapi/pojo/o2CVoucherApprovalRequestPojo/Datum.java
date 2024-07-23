package restassuredapi.pojo.o2CVoucherApprovalRequestPojo;

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
"approvalLevel",
"externalTxnDate",
"externalTxnNum",
"paymentDetails",
"pin",
"refrenceNumber",
"remarks",
"status",
"toUserId",
"transactionId",
"transferDate",
"voucherDetails"
})
public class Datum {

@JsonProperty("approvalLevel")
private String approvalLevel;
@JsonProperty("externalTxnDate")
private String externalTxnDate;
@JsonProperty("externalTxnNum")
private String externalTxnNum;
@JsonProperty("paymentDetails")
private List<PaymentDetails> paymentDetails = null;
@JsonProperty("refrenceNumber")
private String refrenceNumber;
@JsonProperty("remarks")
private String remarks;
@JsonProperty("status")
private String status;
@JsonProperty("toUserId")
private String toUserId;
@JsonProperty("transactionId")
private String transactionId;
@JsonProperty("transferDate")
private String transferDate;
@JsonProperty("voucherDetails")
private List<VoucherDetails> voucherDetails = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("approvalLevel")
public String getApprovalLevel() {
return approvalLevel;
}

@JsonProperty("approvalLevel")
public void setApprovalLevel(String approvalLevel) {
this.approvalLevel = approvalLevel;
}

@JsonProperty("externalTxnDate")
public String getExternalTxnDate() {
return externalTxnDate;
}

@JsonProperty("externalTxnDate")
public void setExternalTxnDate(String externalTxnDate) {
this.externalTxnDate = externalTxnDate;
}

@JsonProperty("externalTxnNum")
public String getExternalTxnNum() {
return externalTxnNum;
}

@JsonProperty("externalTxnNum")
public void setExternalTxnNum(String externalTxnNum) {
this.externalTxnNum = externalTxnNum;
}

@JsonProperty("paymentDetails")
public List<PaymentDetails> getPaymentDetails() {
return paymentDetails;
}

@JsonProperty("paymentDetails")
public void setPaymentDetails(List<PaymentDetails> paymentDetails) {
this.paymentDetails = paymentDetails;
}


@JsonProperty("refrenceNumber")
public String getRefrenceNumber() {
return refrenceNumber;
}

@JsonProperty("refrenceNumber")
public void setRefrenceNumber(String refrenceNumber) {
this.refrenceNumber = refrenceNumber;
}

@JsonProperty("remarks")
public String getRemarks() {
return remarks;
}

@JsonProperty("remarks")
public void setRemarks(String remarks) {
this.remarks = remarks;
}

@JsonProperty("status")
public String getStatus() {
return status;
}

@JsonProperty("status")
public void setStatus(String status) {
this.status = status;
}

@JsonProperty("toUserId")
public String getToUserId() {
return toUserId;
}

@JsonProperty("toUserId")
public void setToUserId(String toUserId) {
this.toUserId = toUserId;
}

@JsonProperty("transactionId")
public String getTransactionId() {
return transactionId;
}

@JsonProperty("transactionId")
public void setTransactionId(String transactionId) {
this.transactionId = transactionId;
}

@JsonProperty("transferDate")
public String getTransferDate() {
return transferDate;
}

@JsonProperty("transferDate")
public void setTransferDate(String transferDate) {
this.transferDate = transferDate;
}

@JsonProperty("voucherDetails")
public List<VoucherDetails> getVoucherDetails() {
return voucherDetails;
}

@JsonProperty("voucherDetails")
public void setVoucherDetails(List<VoucherDetails> voucherDetails) {
this.voucherDetails = voucherDetails;
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

