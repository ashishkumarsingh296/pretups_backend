
package restassuredapi.pojo.o2Ctransferacknowledgementdetailsresponsepojo;

import java.util.HashMap;
import java.util.List;
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
    "address",
    "approvedQuantity",
    "batchType",
    "category",
    "cbcAmount",
    "cbcRate",
    "cbcType",
    "commisionAmount",
    "commisionRate",
    "commisionType",
    "commissionProfile",
    "dateTime",
    "denomination",
    "denominationAmount",
    "domain",
    "erpCode",
    "firstApprovedRemarks",
    "fromSerialNumber",
    "geography",
    "level1ApprovedQuantity",
    "level2ApprovedQuantity",
    "level3ApprovedQuantity",
    "listVoucherDetails",
    "mobileNumber",
    "netAmount",
    "networkName",
    "payableAmount",
    "paymentInstrumentAmount",
    "paymentInstrumentDate",
    "paymentInstrumentNumber",
    "paymentMode",
    "productName",
    "productShortCode",
    "quantity",
    "receiverCreditQuantity",
    "referenceNumber",
    "secondApprovedRemarks",
    "status",
    "tax1Amount",
    "tax1Rate",
    "tax1Type",
    "tax2Amount",
    "tax2Rate",
    "tax2Type",
    "tax3Rate",
    "tds",
    "thirdApprovedRemarks",
    "toSerialNumber",
    "totalNoofVouchers",
    "transDateExternal",
    "transNumberExternal",
    "transactionID",
    "transferCategory",
    "transferProfile",
    "transferType",
    "userName",
    "vomsProductName",
    "voucherBatchNumber"
})
@Generated("jsonschema2pojo")
public class ListO2CTransferAckDTO {

    @JsonProperty("address")
    private String address;
    @JsonProperty("approvedQuantity")
    private String approvedQuantity;
    @JsonProperty("batchType")
    private String batchType;
    @JsonProperty("category")
    private String category;
    @JsonProperty("cbcAmount")
    private String cbcAmount;
    @JsonProperty("cbcRate")
    private String cbcRate;
    @JsonProperty("cbcType")
    private String cbcType;
    @JsonProperty("commisionAmount")
    private String commisionAmount;
    @JsonProperty("commisionRate")
    private String commisionRate;
    @JsonProperty("commisionType")
    private String commisionType;
    @JsonProperty("commissionProfile")
    private String commissionProfile;
    @JsonProperty("dateTime")
    private String dateTime;
    @JsonProperty("denomination")
    private String denomination;
    @JsonProperty("denominationAmount")
    private String denominationAmount;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("erpCode")
    private String erpCode;
    @JsonProperty("firstApprovedRemarks")
    private String firstApprovedRemarks;
    @JsonProperty("fromSerialNumber")
    private String fromSerialNumber;
    @JsonProperty("geography")
    private String geography;
    @JsonProperty("level1ApprovedQuantity")
    private String level1ApprovedQuantity;
    @JsonProperty("level2ApprovedQuantity")
    private String level2ApprovedQuantity;
    @JsonProperty("level3ApprovedQuantity")
    private String level3ApprovedQuantity;
    @JsonProperty("listVoucherDetails")
    private List<ListVoucherDetail> listVoucherDetails = null;
    @JsonProperty("mobileNumber")
    private String mobileNumber;
    @JsonProperty("netAmount")
    private String netAmount;
    @JsonProperty("networkName")
    private String networkName;
    @JsonProperty("payableAmount")
    private String payableAmount;
    @JsonProperty("paymentInstrumentAmount")
    private String paymentInstrumentAmount;
    @JsonProperty("paymentInstrumentDate")
    private String paymentInstrumentDate;
    @JsonProperty("paymentInstrumentNumber")
    private String paymentInstrumentNumber;
    @JsonProperty("paymentMode")
    private String paymentMode;
    @JsonProperty("productName")
    private String productName;
    @JsonProperty("productShortCode")
    private String productShortCode;
    @JsonProperty("quantity")
    private String quantity;
    @JsonProperty("receiverCreditQuantity")
    private String receiverCreditQuantity;
    @JsonProperty("referenceNumber")
    private String referenceNumber;
    @JsonProperty("secondApprovedRemarks")
    private String secondApprovedRemarks;
    @JsonProperty("status")
    private String status;
    @JsonProperty("tax1Amount")
    private String tax1Amount;
    @JsonProperty("tax1Rate")
    private String tax1Rate;
    @JsonProperty("tax1Type")
    private String tax1Type;
    @JsonProperty("tax2Amount")
    private String tax2Amount;
    @JsonProperty("tax2Rate")
    private String tax2Rate;
    @JsonProperty("tax2Type")
    private String tax2Type;
    @JsonProperty("tax3Rate")
    private String tax3Rate;
    @JsonProperty("tds")
    private String tds;
    @JsonProperty("thirdApprovedRemarks")
    private String thirdApprovedRemarks;
    @JsonProperty("toSerialNumber")
    private String toSerialNumber;
    @JsonProperty("totalNoofVouchers")
    private String totalNoofVouchers;
    @JsonProperty("transDateExternal")
    private String transDateExternal;
    @JsonProperty("transNumberExternal")
    private String transNumberExternal;
    @JsonProperty("transactionID")
    private String transactionID;
    @JsonProperty("transferCategory")
    private String transferCategory;
    @JsonProperty("transferProfile")
    private String transferProfile;
    @JsonProperty("transferType")
    private String transferType;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("vomsProductName")
    private String vomsProductName;
    @JsonProperty("voucherBatchNumber")
    private String voucherBatchNumber;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("approvedQuantity")
    public String getApprovedQuantity() {
        return approvedQuantity;
    }

    @JsonProperty("approvedQuantity")
    public void setApprovedQuantity(String approvedQuantity) {
        this.approvedQuantity = approvedQuantity;
    }

    @JsonProperty("batchType")
    public String getBatchType() {
        return batchType;
    }

    @JsonProperty("batchType")
    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("cbcAmount")
    public String getCbcAmount() {
        return cbcAmount;
    }

    @JsonProperty("cbcAmount")
    public void setCbcAmount(String cbcAmount) {
        this.cbcAmount = cbcAmount;
    }

    @JsonProperty("cbcRate")
    public String getCbcRate() {
        return cbcRate;
    }

    @JsonProperty("cbcRate")
    public void setCbcRate(String cbcRate) {
        this.cbcRate = cbcRate;
    }

    @JsonProperty("cbcType")
    public String getCbcType() {
        return cbcType;
    }

    @JsonProperty("cbcType")
    public void setCbcType(String cbcType) {
        this.cbcType = cbcType;
    }

    @JsonProperty("commisionAmount")
    public String getCommisionAmount() {
        return commisionAmount;
    }

    @JsonProperty("commisionAmount")
    public void setCommisionAmount(String commisionAmount) {
        this.commisionAmount = commisionAmount;
    }

    @JsonProperty("commisionRate")
    public String getCommisionRate() {
        return commisionRate;
    }

    @JsonProperty("commisionRate")
    public void setCommisionRate(String commisionRate) {
        this.commisionRate = commisionRate;
    }

    @JsonProperty("commisionType")
    public String getCommisionType() {
        return commisionType;
    }

    @JsonProperty("commisionType")
    public void setCommisionType(String commisionType) {
        this.commisionType = commisionType;
    }

    @JsonProperty("commissionProfile")
    public String getCommissionProfile() {
        return commissionProfile;
    }

    @JsonProperty("commissionProfile")
    public void setCommissionProfile(String commissionProfile) {
        this.commissionProfile = commissionProfile;
    }

    @JsonProperty("dateTime")
    public String getDateTime() {
        return dateTime;
    }

    @JsonProperty("dateTime")
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @JsonProperty("denomination")
    public String getDenomination() {
        return denomination;
    }

    @JsonProperty("denomination")
    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    @JsonProperty("denominationAmount")
    public String getDenominationAmount() {
        return denominationAmount;
    }

    @JsonProperty("denominationAmount")
    public void setDenominationAmount(String denominationAmount) {
        this.denominationAmount = denominationAmount;
    }

    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    @JsonProperty("domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @JsonProperty("erpCode")
    public String getErpCode() {
        return erpCode;
    }

    @JsonProperty("erpCode")
    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }

    @JsonProperty("firstApprovedRemarks")
    public String getFirstApprovedRemarks() {
        return firstApprovedRemarks;
    }

    @JsonProperty("firstApprovedRemarks")
    public void setFirstApprovedRemarks(String firstApprovedRemarks) {
        this.firstApprovedRemarks = firstApprovedRemarks;
    }

    @JsonProperty("fromSerialNumber")
    public String getFromSerialNumber() {
        return fromSerialNumber;
    }

    @JsonProperty("fromSerialNumber")
    public void setFromSerialNumber(String fromSerialNumber) {
        this.fromSerialNumber = fromSerialNumber;
    }

    @JsonProperty("geography")
    public String getGeography() {
        return geography;
    }

    @JsonProperty("geography")
    public void setGeography(String geography) {
        this.geography = geography;
    }

    @JsonProperty("level1ApprovedQuantity")
    public String getLevel1ApprovedQuantity() {
        return level1ApprovedQuantity;
    }

    @JsonProperty("level1ApprovedQuantity")
    public void setLevel1ApprovedQuantity(String level1ApprovedQuantity) {
        this.level1ApprovedQuantity = level1ApprovedQuantity;
    }

    @JsonProperty("level2ApprovedQuantity")
    public String getLevel2ApprovedQuantity() {
        return level2ApprovedQuantity;
    }

    @JsonProperty("level2ApprovedQuantity")
    public void setLevel2ApprovedQuantity(String level2ApprovedQuantity) {
        this.level2ApprovedQuantity = level2ApprovedQuantity;
    }

    @JsonProperty("level3ApprovedQuantity")
    public String getLevel3ApprovedQuantity() {
        return level3ApprovedQuantity;
    }

    @JsonProperty("level3ApprovedQuantity")
    public void setLevel3ApprovedQuantity(String level3ApprovedQuantity) {
        this.level3ApprovedQuantity = level3ApprovedQuantity;
    }

    @JsonProperty("listVoucherDetails")
    public List<ListVoucherDetail> getListVoucherDetails() {
        return listVoucherDetails;
    }

    @JsonProperty("listVoucherDetails")
    public void setListVoucherDetails(List<ListVoucherDetail> listVoucherDetails) {
        this.listVoucherDetails = listVoucherDetails;
    }

    @JsonProperty("mobileNumber")
    public String getMobileNumber() {
        return mobileNumber;
    }

    @JsonProperty("mobileNumber")
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @JsonProperty("netAmount")
    public String getNetAmount() {
        return netAmount;
    }

    @JsonProperty("netAmount")
    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    @JsonProperty("networkName")
    public String getNetworkName() {
        return networkName;
    }

    @JsonProperty("networkName")
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    @JsonProperty("payableAmount")
    public String getPayableAmount() {
        return payableAmount;
    }

    @JsonProperty("payableAmount")
    public void setPayableAmount(String payableAmount) {
        this.payableAmount = payableAmount;
    }

    @JsonProperty("paymentInstrumentAmount")
    public String getPaymentInstrumentAmount() {
        return paymentInstrumentAmount;
    }

    @JsonProperty("paymentInstrumentAmount")
    public void setPaymentInstrumentAmount(String paymentInstrumentAmount) {
        this.paymentInstrumentAmount = paymentInstrumentAmount;
    }

    @JsonProperty("paymentInstrumentDate")
    public String getPaymentInstrumentDate() {
        return paymentInstrumentDate;
    }

    @JsonProperty("paymentInstrumentDate")
    public void setPaymentInstrumentDate(String paymentInstrumentDate) {
        this.paymentInstrumentDate = paymentInstrumentDate;
    }

    @JsonProperty("paymentInstrumentNumber")
    public String getPaymentInstrumentNumber() {
        return paymentInstrumentNumber;
    }

    @JsonProperty("paymentInstrumentNumber")
    public void setPaymentInstrumentNumber(String paymentInstrumentNumber) {
        this.paymentInstrumentNumber = paymentInstrumentNumber;
    }

    @JsonProperty("paymentMode")
    public String getPaymentMode() {
        return paymentMode;
    }

    @JsonProperty("paymentMode")
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    @JsonProperty("productName")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("productName")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @JsonProperty("productShortCode")
    public String getProductShortCode() {
        return productShortCode;
    }

    @JsonProperty("productShortCode")
    public void setProductShortCode(String productShortCode) {
        this.productShortCode = productShortCode;
    }

    @JsonProperty("quantity")
    public String getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @JsonProperty("receiverCreditQuantity")
    public String getReceiverCreditQuantity() {
        return receiverCreditQuantity;
    }

    @JsonProperty("receiverCreditQuantity")
    public void setReceiverCreditQuantity(String receiverCreditQuantity) {
        this.receiverCreditQuantity = receiverCreditQuantity;
    }

    @JsonProperty("referenceNumber")
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @JsonProperty("referenceNumber")
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @JsonProperty("secondApprovedRemarks")
    public String getSecondApprovedRemarks() {
        return secondApprovedRemarks;
    }

    @JsonProperty("secondApprovedRemarks")
    public void setSecondApprovedRemarks(String secondApprovedRemarks) {
        this.secondApprovedRemarks = secondApprovedRemarks;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("tax1Amount")
    public String getTax1Amount() {
        return tax1Amount;
    }

    @JsonProperty("tax1Amount")
    public void setTax1Amount(String tax1Amount) {
        this.tax1Amount = tax1Amount;
    }

    @JsonProperty("tax1Rate")
    public String getTax1Rate() {
        return tax1Rate;
    }

    @JsonProperty("tax1Rate")
    public void setTax1Rate(String tax1Rate) {
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

    @JsonProperty("tax2Amount")
    public String getTax2Amount() {
        return tax2Amount;
    }

    @JsonProperty("tax2Amount")
    public void setTax2Amount(String tax2Amount) {
        this.tax2Amount = tax2Amount;
    }

    @JsonProperty("tax2Rate")
    public String getTax2Rate() {
        return tax2Rate;
    }

    @JsonProperty("tax2Rate")
    public void setTax2Rate(String tax2Rate) {
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

    @JsonProperty("tax3Rate")
    public String getTax3Rate() {
        return tax3Rate;
    }

    @JsonProperty("tax3Rate")
    public void setTax3Rate(String tax3Rate) {
        this.tax3Rate = tax3Rate;
    }

    @JsonProperty("tds")
    public String getTds() {
        return tds;
    }

    @JsonProperty("tds")
    public void setTds(String tds) {
        this.tds = tds;
    }

    @JsonProperty("thirdApprovedRemarks")
    public String getThirdApprovedRemarks() {
        return thirdApprovedRemarks;
    }

    @JsonProperty("thirdApprovedRemarks")
    public void setThirdApprovedRemarks(String thirdApprovedRemarks) {
        this.thirdApprovedRemarks = thirdApprovedRemarks;
    }

    @JsonProperty("toSerialNumber")
    public String getToSerialNumber() {
        return toSerialNumber;
    }

    @JsonProperty("toSerialNumber")
    public void setToSerialNumber(String toSerialNumber) {
        this.toSerialNumber = toSerialNumber;
    }

    @JsonProperty("totalNoofVouchers")
    public String getTotalNoofVouchers() {
        return totalNoofVouchers;
    }

    @JsonProperty("totalNoofVouchers")
    public void setTotalNoofVouchers(String totalNoofVouchers) {
        this.totalNoofVouchers = totalNoofVouchers;
    }

    @JsonProperty("transDateExternal")
    public String getTransDateExternal() {
        return transDateExternal;
    }

    @JsonProperty("transDateExternal")
    public void setTransDateExternal(String transDateExternal) {
        this.transDateExternal = transDateExternal;
    }

    @JsonProperty("transNumberExternal")
    public String getTransNumberExternal() {
        return transNumberExternal;
    }

    @JsonProperty("transNumberExternal")
    public void setTransNumberExternal(String transNumberExternal) {
        this.transNumberExternal = transNumberExternal;
    }

    @JsonProperty("transactionID")
    public String getTransactionID() {
        return transactionID;
    }

    @JsonProperty("transactionID")
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    @JsonProperty("transferCategory")
    public String getTransferCategory() {
        return transferCategory;
    }

    @JsonProperty("transferCategory")
    public void setTransferCategory(String transferCategory) {
        this.transferCategory = transferCategory;
    }

    @JsonProperty("transferProfile")
    public String getTransferProfile() {
        return transferProfile;
    }

    @JsonProperty("transferProfile")
    public void setTransferProfile(String transferProfile) {
        this.transferProfile = transferProfile;
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

    @JsonProperty("vomsProductName")
    public String getVomsProductName() {
        return vomsProductName;
    }

    @JsonProperty("vomsProductName")
    public void setVomsProductName(String vomsProductName) {
        this.vomsProductName = vomsProductName;
    }

    @JsonProperty("voucherBatchNumber")
    public String getVoucherBatchNumber() {
        return voucherBatchNumber;
    }

    @JsonProperty("voucherBatchNumber")
    public void setVoucherBatchNumber(String voucherBatchNumber) {
        this.voucherBatchNumber = voucherBatchNumber;
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
