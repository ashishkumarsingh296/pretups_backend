
package restassuredapi.pojo.BatchO2CApprovalDetailResponsepojo;

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
    "batchDetailId",
    "batchId",
    "batchO2CItems",
    "cancelledBy",
    "cancelledOn",
    "categoryCode",
    "categoryName",
    "commCalReqd",
    "commissionProfileDetailId",
    "commissionProfileSetId",
    "commissionProfileVer",
    "commissionRate",
    "commissionType",
    "commissionValue",
    "dualCommissionType",
    "extTxnDate",
    "extTxnDateStr",
    "extTxnNo",
    "externalCode",
    "firstApprovedBy",
    "firstApprovedOn",
    "firstApprovedQuantity",
    "firstApproverName",
    "firstApproverRemarks",
    "gradeCode",
    "gradeName",
    "initiatedBy",
    "initiatedOn",
    "initiaterName",
    "initiatorRemarks",
    "isSlabDefine",
    "loginID",
    "modifiedBy",
    "modifiedOn",
    "msisdn",
    "netPayableAmount",
    "payableAmount",
    "paymentType",
    "rcrdStatus",
    "recordNumber",
    "referenceNo",
    "requestedQuantity",
    "secondApprQty",
    "secondApprovedBy",
    "secondApprovedOn",
    "secondApproverName",
    "secondApproverRemarks",
    "status",
    "tax1Rate",
    "tax1Type",
    "tax1Value",
    "tax2Rate",
    "tax2Type",
    "tax2Value",
    "tax3Rate",
    "tax3Type",
    "tax3Value",
    "thirdApprovedBy",
    "thirdApprovedOn",
    "thirdApproverRemarks",
    "transferDate",
    "transferDateStr",
    "transferMrp",
    "txnProfile",
    "userCategory",
    "userGradeCode",
    "userId",
    "userName"
})
public class BatchO2CItemsVO {

    @JsonProperty("batchDetailId")
    private String batchDetailId;
    @JsonProperty("batchId")
    private String batchId;
    @JsonProperty("batchO2CItems")
    private BatchO2CItems batchO2CItems;
    @JsonProperty("cancelledBy")
    private String cancelledBy;
    @JsonProperty("cancelledOn")
    private String cancelledOn;
    @JsonProperty("categoryCode")
    private String categoryCode;
    @JsonProperty("categoryName")
    private String categoryName;
    @JsonProperty("commCalReqd")
    private Boolean commCalReqd;
    @JsonProperty("commissionProfileDetailId")
    private String commissionProfileDetailId;
    @JsonProperty("commissionProfileSetId")
    private String commissionProfileSetId;
    @JsonProperty("commissionProfileVer")
    private String commissionProfileVer;
    @JsonProperty("commissionRate")
    private Integer commissionRate;
    @JsonProperty("commissionType")
    private String commissionType;
    @JsonProperty("commissionValue")
    private Integer commissionValue;
    @JsonProperty("dualCommissionType")
    private String dualCommissionType;
    @JsonProperty("extTxnDate")
    private String extTxnDate;
    @JsonProperty("extTxnDateStr")
    private String extTxnDateStr;
    @JsonProperty("extTxnNo")
    private String extTxnNo;
    @JsonProperty("externalCode")
    private String externalCode;
    @JsonProperty("firstApprovedBy")
    private String firstApprovedBy;
    @JsonProperty("firstApprovedOn")
    private String firstApprovedOn;
    @JsonProperty("firstApprovedQuantity")
    private Integer firstApprovedQuantity;
    @JsonProperty("firstApproverName")
    private String firstApproverName;
    @JsonProperty("firstApproverRemarks")
    private String firstApproverRemarks;
    @JsonProperty("gradeCode")
    private String gradeCode;
    @JsonProperty("gradeName")
    private String gradeName;
    @JsonProperty("initiatedBy")
    private String initiatedBy;
    @JsonProperty("initiatedOn")
    private String initiatedOn;
    @JsonProperty("initiaterName")
    private String initiaterName;
    @JsonProperty("initiatorRemarks")
    private String initiatorRemarks;
    @JsonProperty("isSlabDefine")
    private Boolean isSlabDefine;
    @JsonProperty("loginID")
    private String loginID;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("modifiedOn")
    private String modifiedOn;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("netPayableAmount")
    private Integer netPayableAmount;
    @JsonProperty("payableAmount")
    private Integer payableAmount;
    @JsonProperty("paymentType")
    private String paymentType;
    @JsonProperty("rcrdStatus")
    private String rcrdStatus;
    @JsonProperty("recordNumber")
    private Integer recordNumber;
    @JsonProperty("referenceNo")
    private String referenceNo;
    @JsonProperty("requestedQuantity")
    private Integer requestedQuantity;
    @JsonProperty("secondApprQty")
    private Integer secondApprQty;
    @JsonProperty("secondApprovedBy")
    private String secondApprovedBy;
    @JsonProperty("secondApprovedOn")
    private String secondApprovedOn;
    @JsonProperty("secondApproverName")
    private String secondApproverName;
    @JsonProperty("secondApproverRemarks")
    private String secondApproverRemarks;
    @JsonProperty("status")
    private String status;
    @JsonProperty("tax1Rate")
    private Integer tax1Rate;
    @JsonProperty("tax1Type")
    private String tax1Type;
    @JsonProperty("tax1Value")
    private Integer tax1Value;
    @JsonProperty("tax2Rate")
    private Integer tax2Rate;
    @JsonProperty("tax2Type")
    private String tax2Type;
    @JsonProperty("tax2Value")
    private Integer tax2Value;
    @JsonProperty("tax3Rate")
    private Integer tax3Rate;
    @JsonProperty("tax3Type")
    private String tax3Type;
    @JsonProperty("tax3Value")
    private Integer tax3Value;
    @JsonProperty("thirdApprovedBy")
    private String thirdApprovedBy;
    @JsonProperty("thirdApprovedOn")
    private String thirdApprovedOn;
    @JsonProperty("thirdApproverRemarks")
    private String thirdApproverRemarks;
    @JsonProperty("transferDate")
    private String transferDate;
    @JsonProperty("transferDateStr")
    private String transferDateStr;
    @JsonProperty("transferMrp")
    private Integer transferMrp;
    @JsonProperty("txnProfile")
    private String txnProfile;
    @JsonProperty("userCategory")
    private String userCategory;
    @JsonProperty("userGradeCode")
    private String userGradeCode;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("userName")
    private String userName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("batchDetailId")
    public String getBatchDetailId() {
        return batchDetailId;
    }

    @JsonProperty("batchDetailId")
    public void setBatchDetailId(String batchDetailId) {
        this.batchDetailId = batchDetailId;
    }

    @JsonProperty("batchId")
    public String getBatchId() {
        return batchId;
    }

    @JsonProperty("batchId")
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @JsonProperty("batchO2CItems")
    public BatchO2CItems getBatchO2CItems() {
        return batchO2CItems;
    }

    @JsonProperty("batchO2CItems")
    public void setBatchO2CItems(BatchO2CItems batchO2CItems) {
        this.batchO2CItems = batchO2CItems;
    }

    @JsonProperty("cancelledBy")
    public String getCancelledBy() {
        return cancelledBy;
    }

    @JsonProperty("cancelledBy")
    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    @JsonProperty("cancelledOn")
    public String getCancelledOn() {
        return cancelledOn;
    }

    @JsonProperty("cancelledOn")
    public void setCancelledOn(String cancelledOn) {
        this.cancelledOn = cancelledOn;
    }

    @JsonProperty("categoryCode")
    public String getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @JsonProperty("categoryName")
    public String getCategoryName() {
        return categoryName;
    }

    @JsonProperty("categoryName")
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("commCalReqd")
    public Boolean getCommCalReqd() {
        return commCalReqd;
    }

    @JsonProperty("commCalReqd")
    public void setCommCalReqd(Boolean commCalReqd) {
        this.commCalReqd = commCalReqd;
    }

    @JsonProperty("commissionProfileDetailId")
    public String getCommissionProfileDetailId() {
        return commissionProfileDetailId;
    }

    @JsonProperty("commissionProfileDetailId")
    public void setCommissionProfileDetailId(String commissionProfileDetailId) {
        this.commissionProfileDetailId = commissionProfileDetailId;
    }

    @JsonProperty("commissionProfileSetId")
    public String getCommissionProfileSetId() {
        return commissionProfileSetId;
    }

    @JsonProperty("commissionProfileSetId")
    public void setCommissionProfileSetId(String commissionProfileSetId) {
        this.commissionProfileSetId = commissionProfileSetId;
    }

    @JsonProperty("commissionProfileVer")
    public String getCommissionProfileVer() {
        return commissionProfileVer;
    }

    @JsonProperty("commissionProfileVer")
    public void setCommissionProfileVer(String commissionProfileVer) {
        this.commissionProfileVer = commissionProfileVer;
    }

    @JsonProperty("commissionRate")
    public Integer getCommissionRate() {
        return commissionRate;
    }

    @JsonProperty("commissionRate")
    public void setCommissionRate(Integer commissionRate) {
        this.commissionRate = commissionRate;
    }

    @JsonProperty("commissionType")
    public String getCommissionType() {
        return commissionType;
    }

    @JsonProperty("commissionType")
    public void setCommissionType(String commissionType) {
        this.commissionType = commissionType;
    }

    @JsonProperty("commissionValue")
    public Integer getCommissionValue() {
        return commissionValue;
    }

    @JsonProperty("commissionValue")
    public void setCommissionValue(Integer commissionValue) {
        this.commissionValue = commissionValue;
    }

    @JsonProperty("dualCommissionType")
    public String getDualCommissionType() {
        return dualCommissionType;
    }

    @JsonProperty("dualCommissionType")
    public void setDualCommissionType(String dualCommissionType) {
        this.dualCommissionType = dualCommissionType;
    }

    @JsonProperty("extTxnDate")
    public String getExtTxnDate() {
        return extTxnDate;
    }

    @JsonProperty("extTxnDate")
    public void setExtTxnDate(String extTxnDate) {
        this.extTxnDate = extTxnDate;
    }

    @JsonProperty("extTxnDateStr")
    public String getExtTxnDateStr() {
        return extTxnDateStr;
    }

    @JsonProperty("extTxnDateStr")
    public void setExtTxnDateStr(String extTxnDateStr) {
        this.extTxnDateStr = extTxnDateStr;
    }

    @JsonProperty("extTxnNo")
    public String getExtTxnNo() {
        return extTxnNo;
    }

    @JsonProperty("extTxnNo")
    public void setExtTxnNo(String extTxnNo) {
        this.extTxnNo = extTxnNo;
    }

    @JsonProperty("externalCode")
    public String getExternalCode() {
        return externalCode;
    }

    @JsonProperty("externalCode")
    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    @JsonProperty("firstApprovedBy")
    public String getFirstApprovedBy() {
        return firstApprovedBy;
    }

    @JsonProperty("firstApprovedBy")
    public void setFirstApprovedBy(String firstApprovedBy) {
        this.firstApprovedBy = firstApprovedBy;
    }

    @JsonProperty("firstApprovedOn")
    public String getFirstApprovedOn() {
        return firstApprovedOn;
    }

    @JsonProperty("firstApprovedOn")
    public void setFirstApprovedOn(String firstApprovedOn) {
        this.firstApprovedOn = firstApprovedOn;
    }

    @JsonProperty("firstApprovedQuantity")
    public Integer getFirstApprovedQuantity() {
        return firstApprovedQuantity;
    }

    @JsonProperty("firstApprovedQuantity")
    public void setFirstApprovedQuantity(Integer firstApprovedQuantity) {
        this.firstApprovedQuantity = firstApprovedQuantity;
    }

    @JsonProperty("firstApproverName")
    public String getFirstApproverName() {
        return firstApproverName;
    }

    @JsonProperty("firstApproverName")
    public void setFirstApproverName(String firstApproverName) {
        this.firstApproverName = firstApproverName;
    }

    @JsonProperty("firstApproverRemarks")
    public String getFirstApproverRemarks() {
        return firstApproverRemarks;
    }

    @JsonProperty("firstApproverRemarks")
    public void setFirstApproverRemarks(String firstApproverRemarks) {
        this.firstApproverRemarks = firstApproverRemarks;
    }

    @JsonProperty("gradeCode")
    public String getGradeCode() {
        return gradeCode;
    }

    @JsonProperty("gradeCode")
    public void setGradeCode(String gradeCode) {
        this.gradeCode = gradeCode;
    }

    @JsonProperty("gradeName")
    public String getGradeName() {
        return gradeName;
    }

    @JsonProperty("gradeName")
    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    @JsonProperty("initiatedBy")
    public String getInitiatedBy() {
        return initiatedBy;
    }

    @JsonProperty("initiatedBy")
    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    @JsonProperty("initiatedOn")
    public String getInitiatedOn() {
        return initiatedOn;
    }

    @JsonProperty("initiatedOn")
    public void setInitiatedOn(String initiatedOn) {
        this.initiatedOn = initiatedOn;
    }

    @JsonProperty("initiaterName")
    public String getInitiaterName() {
        return initiaterName;
    }

    @JsonProperty("initiaterName")
    public void setInitiaterName(String initiaterName) {
        this.initiaterName = initiaterName;
    }

    @JsonProperty("initiatorRemarks")
    public String getInitiatorRemarks() {
        return initiatorRemarks;
    }

    @JsonProperty("initiatorRemarks")
    public void setInitiatorRemarks(String initiatorRemarks) {
        this.initiatorRemarks = initiatorRemarks;
    }

    @JsonProperty("isSlabDefine")
    public Boolean getIsSlabDefine() {
        return isSlabDefine;
    }

    @JsonProperty("isSlabDefine")
    public void setIsSlabDefine(Boolean isSlabDefine) {
        this.isSlabDefine = isSlabDefine;
    }

    @JsonProperty("loginID")
    public String getLoginID() {
        return loginID;
    }

    @JsonProperty("loginID")
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    @JsonProperty("modifiedBy")
    public String getModifiedBy() {
        return modifiedBy;
    }

    @JsonProperty("modifiedBy")
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("modifiedOn")
    public String getModifiedOn() {
        return modifiedOn;
    }

    @JsonProperty("modifiedOn")
    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("netPayableAmount")
    public Integer getNetPayableAmount() {
        return netPayableAmount;
    }

    @JsonProperty("netPayableAmount")
    public void setNetPayableAmount(Integer netPayableAmount) {
        this.netPayableAmount = netPayableAmount;
    }

    @JsonProperty("payableAmount")
    public Integer getPayableAmount() {
        return payableAmount;
    }

    @JsonProperty("payableAmount")
    public void setPayableAmount(Integer payableAmount) {
        this.payableAmount = payableAmount;
    }

    @JsonProperty("paymentType")
    public String getPaymentType() {
        return paymentType;
    }

    @JsonProperty("paymentType")
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @JsonProperty("rcrdStatus")
    public String getRcrdStatus() {
        return rcrdStatus;
    }

    @JsonProperty("rcrdStatus")
    public void setRcrdStatus(String rcrdStatus) {
        this.rcrdStatus = rcrdStatus;
    }

    @JsonProperty("recordNumber")
    public Integer getRecordNumber() {
        return recordNumber;
    }

    @JsonProperty("recordNumber")
    public void setRecordNumber(Integer recordNumber) {
        this.recordNumber = recordNumber;
    }

    @JsonProperty("referenceNo")
    public String getReferenceNo() {
        return referenceNo;
    }

    @JsonProperty("referenceNo")
    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    @JsonProperty("requestedQuantity")
    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    @JsonProperty("requestedQuantity")
    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    @JsonProperty("secondApprQty")
    public Integer getSecondApprQty() {
        return secondApprQty;
    }

    @JsonProperty("secondApprQty")
    public void setSecondApprQty(Integer secondApprQty) {
        this.secondApprQty = secondApprQty;
    }

    @JsonProperty("secondApprovedBy")
    public String getSecondApprovedBy() {
        return secondApprovedBy;
    }

    @JsonProperty("secondApprovedBy")
    public void setSecondApprovedBy(String secondApprovedBy) {
        this.secondApprovedBy = secondApprovedBy;
    }

    @JsonProperty("secondApprovedOn")
    public String getSecondApprovedOn() {
        return secondApprovedOn;
    }

    @JsonProperty("secondApprovedOn")
    public void setSecondApprovedOn(String secondApprovedOn) {
        this.secondApprovedOn = secondApprovedOn;
    }

    @JsonProperty("secondApproverName")
    public String getSecondApproverName() {
        return secondApproverName;
    }

    @JsonProperty("secondApproverName")
    public void setSecondApproverName(String secondApproverName) {
        this.secondApproverName = secondApproverName;
    }

    @JsonProperty("secondApproverRemarks")
    public String getSecondApproverRemarks() {
        return secondApproverRemarks;
    }

    @JsonProperty("secondApproverRemarks")
    public void setSecondApproverRemarks(String secondApproverRemarks) {
        this.secondApproverRemarks = secondApproverRemarks;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("tax1Rate")
    public Integer getTax1Rate() {
        return tax1Rate;
    }

    @JsonProperty("tax1Rate")
    public void setTax1Rate(Integer tax1Rate) {
        this.tax1Rate = tax1Rate;
    }

    @JsonProperty("tax1Type")
    public String getTax1Type() {
        return tax1Type;
    }

    @JsonProperty("tax1Type")
    public void setTax1Type(String tax1Type) {
        this.tax1Type = tax1Type;
    }

    @JsonProperty("tax1Value")
    public Integer getTax1Value() {
        return tax1Value;
    }

    @JsonProperty("tax1Value")
    public void setTax1Value(Integer tax1Value) {
        this.tax1Value = tax1Value;
    }

    @JsonProperty("tax2Rate")
    public Integer getTax2Rate() {
        return tax2Rate;
    }

    @JsonProperty("tax2Rate")
    public void setTax2Rate(Integer tax2Rate) {
        this.tax2Rate = tax2Rate;
    }

    @JsonProperty("tax2Type")
    public String getTax2Type() {
        return tax2Type;
    }

    @JsonProperty("tax2Type")
    public void setTax2Type(String tax2Type) {
        this.tax2Type = tax2Type;
    }

    @JsonProperty("tax2Value")
    public Integer getTax2Value() {
        return tax2Value;
    }

    @JsonProperty("tax2Value")
    public void setTax2Value(Integer tax2Value) {
        this.tax2Value = tax2Value;
    }

    @JsonProperty("tax3Rate")
    public Integer getTax3Rate() {
        return tax3Rate;
    }

    @JsonProperty("tax3Rate")
    public void setTax3Rate(Integer tax3Rate) {
        this.tax3Rate = tax3Rate;
    }

    @JsonProperty("tax3Type")
    public String getTax3Type() {
        return tax3Type;
    }

    @JsonProperty("tax3Type")
    public void setTax3Type(String tax3Type) {
        this.tax3Type = tax3Type;
    }

    @JsonProperty("tax3Value")
    public Integer getTax3Value() {
        return tax3Value;
    }

    @JsonProperty("tax3Value")
    public void setTax3Value(Integer tax3Value) {
        this.tax3Value = tax3Value;
    }

    @JsonProperty("thirdApprovedBy")
    public String getThirdApprovedBy() {
        return thirdApprovedBy;
    }

    @JsonProperty("thirdApprovedBy")
    public void setThirdApprovedBy(String thirdApprovedBy) {
        this.thirdApprovedBy = thirdApprovedBy;
    }

    @JsonProperty("thirdApprovedOn")
    public String getThirdApprovedOn() {
        return thirdApprovedOn;
    }

    @JsonProperty("thirdApprovedOn")
    public void setThirdApprovedOn(String thirdApprovedOn) {
        this.thirdApprovedOn = thirdApprovedOn;
    }

    @JsonProperty("thirdApproverRemarks")
    public String getThirdApproverRemarks() {
        return thirdApproverRemarks;
    }

    @JsonProperty("thirdApproverRemarks")
    public void setThirdApproverRemarks(String thirdApproverRemarks) {
        this.thirdApproverRemarks = thirdApproverRemarks;
    }

    @JsonProperty("transferDate")
    public String getTransferDate() {
        return transferDate;
    }

    @JsonProperty("transferDate")
    public void setTransferDate(String transferDate) {
        this.transferDate = transferDate;
    }

    @JsonProperty("transferDateStr")
    public String getTransferDateStr() {
        return transferDateStr;
    }

    @JsonProperty("transferDateStr")
    public void setTransferDateStr(String transferDateStr) {
        this.transferDateStr = transferDateStr;
    }

    @JsonProperty("transferMrp")
    public Integer getTransferMrp() {
        return transferMrp;
    }

    @JsonProperty("transferMrp")
    public void setTransferMrp(Integer transferMrp) {
        this.transferMrp = transferMrp;
    }

    @JsonProperty("txnProfile")
    public String getTxnProfile() {
        return txnProfile;
    }

    @JsonProperty("txnProfile")
    public void setTxnProfile(String txnProfile) {
        this.txnProfile = txnProfile;
    }

    @JsonProperty("userCategory")
    public String getUserCategory() {
        return userCategory;
    }

    @JsonProperty("userCategory")
    public void setUserCategory(String userCategory) {
        this.userCategory = userCategory;
    }

    @JsonProperty("userGradeCode")
    public String getUserGradeCode() {
        return userGradeCode;
    }

    @JsonProperty("userGradeCode")
    public void setUserGradeCode(String userGradeCode) {
        this.userGradeCode = userGradeCode;
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
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
