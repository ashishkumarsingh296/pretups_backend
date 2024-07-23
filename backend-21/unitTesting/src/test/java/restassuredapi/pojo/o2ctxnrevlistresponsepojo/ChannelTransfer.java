
package restassuredapi.pojo.o2ctxnrevlistresponsepojo;

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
    "afterTransCommisionSenderPreviousStock",
    "afterTransReceiverPreviousStock",
    "afterTransSenderPreviousBonusStock",
    "afterTransSenderPreviousStock",
    "alertingBalance",
    "approvedQuantity",
    "approvedQuantityAsString",
    "balance",
    "balanceAsString",
    "balanceType",
    "bonusBalance",
    "bonusDebtQty",
    "bundleID",
    "cellId",
    "commAsString",
    "commProfDetailId",
    "commProfileDetailID",
    "commProfileProductID",
    "commQuantity",
    "commQuantityAsString",
    "commRate",
    "commRateAsString",
    "commType",
    "commValue",
    "commissionProfileSetId",
    "commissionProfileVer",
    "commissionValuePosi",
    "commissionValuePosiAsString",
    "createdBy",
    "createdOn",
    "dataList",
    "discountRate",
    "discountType",
    "discountValue",
    "endRange",
    "erpProductCode",
    "extTxnDate",
    "extTxnNo",
    "externalCode",
    "firstApprovedQuantity",
    "gradeName",
    "initialRequestedQuantity",
    "initialRequestedQuantityStr",
    "initiatorRemarks",
    "language1Message",
    "language2Message",
    "lastModifiedTime",
    "loginId",
    "mainDebitQty",
    "maxTransferValue",
    "maxTransferValueAsString",
    "minTransferValue",
    "minTransferValueAsString",
    "modifiedBy",
    "modifiedOn",
    "moduleCode",
    "msisdn",
    "netPayableAmount",
    "netPayableAmountApproval",
    "netPayableAmountAsString",
    "netPayableAmountAsStringApproval",
    "networkCode",
    "networkFOCStock",
    "networkFOCStockAsString",
    "networkINCStock",
    "networkStock",
    "networkStockAfterReversal",
    "networkStockAsString",
    "otfAmount",
    "otfApplicableFrom",
    "otfApplicableTo",
    "otfAsString",
    "otfCountsUpdated",
    "otfRate",
    "otfRateAsString",
    "otfTimeSlab",
    "otfTypePctOrAMt",
    "othCommAsString",
    "othCommProfType",
    "othCommProfValue",
    "othCommRate",
    "othCommRateAsString",
    "othCommSetId",
    "othCommType",
    "othCommValue",
    "othSlabDefine",
    "payableAmount",
    "payableAmountApproval",
    "payableAmountAsString",
    "payableAmountAsStringApproval",
    "paymentType",
    "previousBalance",
    "previousBonusBalance",
    "productCategory",
    "productCode",
    "productCost",
    "productFirstApprovedQty",
    "productId",
    "productMrpStr",
    "productName",
    "productSecondApprovedQty",
    "productShortCode",
    "productStatusName",
    "productThirdApprovedQty",
    "productTotalMRP",
    "productType",
    "productUsage",
    "productUsageName",
    "receiverCreditQty",
    "receiverCreditQtyAsString",
    "receiverPostBalAsString",
    "receiverPostStock",
    "receiverPreviousBalAsString",
    "receiverPreviousStock",
    "requestedQuantity",
    "requiredQuantity",
    "reversalQty",
    "reversalRequest",
    "reversalRequestedQuantity",
    "secondApprovedQuantity",
    "senderBalance",
    "senderBalanceAsString",
    "senderDebitQty",
    "senderDebitQtyAsString",
    "senderPostBalAsString",
    "senderPostStock",
    "senderPreviousBalAsString",
    "senderPreviousStock",
    "serialNum",
    "shortName",
    "slabDefine",
    "startRange",
    "status",
    "targetAchieved",
    "tax1Rate",
    "tax1RateAsString",
    "tax1Type",
    "tax1Value",
    "tax1ValueAsString",
    "tax2Rate",
    "tax2RateAsString",
    "tax2Type",
    "tax2Value",
    "tax2ValueAsString",
    "tax3Rate",
    "tax3RateAsString",
    "tax3Type",
    "tax3Value",
    "tax3ValueAsString",
    "taxOnC2CTransfer",
    "taxOnChannelTransfer",
    "taxOnFOCTransfer",
    "thirdApprovedQuantity",
    "totalReceiverBalance",
    "totalSenderBalance",
    "transactionType",
    "transferID",
    "transferMultipleOf",
    "unitValue",
    "unitValueAsString",
    "usage",
    "userCategory",
    "userOTFCountsVO",
    "userWallet",
    "voucherQuantity",
    "walletBalanceStr",
    "walletType",
    "walletbalance"
})
@Generated("jsonschema2pojo")
public class ChannelTransfer {

    @JsonProperty("afterTransCommisionSenderPreviousStock")
    private Integer afterTransCommisionSenderPreviousStock;
    @JsonProperty("afterTransReceiverPreviousStock")
    private Integer afterTransReceiverPreviousStock;
    @JsonProperty("afterTransSenderPreviousBonusStock")
    private Integer afterTransSenderPreviousBonusStock;
    @JsonProperty("afterTransSenderPreviousStock")
    private Integer afterTransSenderPreviousStock;
    @JsonProperty("alertingBalance")
    private String alertingBalance;
    @JsonProperty("approvedQuantity")
    private Integer approvedQuantity;
    @JsonProperty("approvedQuantityAsString")
    private String approvedQuantityAsString;
    @JsonProperty("balance")
    private Integer balance;
    @JsonProperty("balanceAsString")
    private String balanceAsString;
    @JsonProperty("balanceType")
    private String balanceType;
    @JsonProperty("bonusBalance")
    private Integer bonusBalance;
    @JsonProperty("bonusDebtQty")
    private Integer bonusDebtQty;
    @JsonProperty("bundleID")
    private String bundleID;
    @JsonProperty("cellId")
    private String cellId;
    @JsonProperty("commAsString")
    private String commAsString;
    @JsonProperty("commProfDetailId")
    private String commProfDetailId;
    @JsonProperty("commProfileDetailID")
    private String commProfileDetailID;
    @JsonProperty("commProfileProductID")
    private String commProfileProductID;
    @JsonProperty("commQuantity")
    private Integer commQuantity;
    @JsonProperty("commQuantityAsString")
    private String commQuantityAsString;
    @JsonProperty("commRate")
    private Integer commRate;
    @JsonProperty("commRateAsString")
    private String commRateAsString;
    @JsonProperty("commType")
    private String commType;
    @JsonProperty("commValue")
    private Integer commValue;
    @JsonProperty("commissionProfileSetId")
    private String commissionProfileSetId;
    @JsonProperty("commissionProfileVer")
    private String commissionProfileVer;
    @JsonProperty("commissionValuePosi")
    private Integer commissionValuePosi;
    @JsonProperty("commissionValuePosiAsString")
    private String commissionValuePosiAsString;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdOn")
    private String createdOn;
    @JsonProperty("dataList")
    private List<Data> dataList = null;
    @JsonProperty("discountRate")
    private Integer discountRate;
    @JsonProperty("discountType")
    private String discountType;
    @JsonProperty("discountValue")
    private Integer discountValue;
    @JsonProperty("endRange")
    private Integer endRange;
    @JsonProperty("erpProductCode")
    private String erpProductCode;
    @JsonProperty("extTxnDate")
    private String extTxnDate;
    @JsonProperty("extTxnNo")
    private String extTxnNo;
    @JsonProperty("externalCode")
    private String externalCode;
    @JsonProperty("firstApprovedQuantity")
    private String firstApprovedQuantity;
    @JsonProperty("gradeName")
    private String gradeName;
    @JsonProperty("initialRequestedQuantity")
    private Integer initialRequestedQuantity;
    @JsonProperty("initialRequestedQuantityStr")
    private String initialRequestedQuantityStr;
    @JsonProperty("initiatorRemarks")
    private String initiatorRemarks;
    @JsonProperty("language1Message")
    private String language1Message;
    @JsonProperty("language2Message")
    private String language2Message;
    @JsonProperty("lastModifiedTime")
    private Integer lastModifiedTime;
    @JsonProperty("loginId")
    private String loginId;
    @JsonProperty("mainDebitQty")
    private Integer mainDebitQty;
    @JsonProperty("maxTransferValue")
    private Integer maxTransferValue;
    @JsonProperty("maxTransferValueAsString")
    private String maxTransferValueAsString;
    @JsonProperty("minTransferValue")
    private Integer minTransferValue;
    @JsonProperty("minTransferValueAsString")
    private String minTransferValueAsString;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("modifiedOn")
    private String modifiedOn;
    @JsonProperty("moduleCode")
    private String moduleCode;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("netPayableAmount")
    private Integer netPayableAmount;
    @JsonProperty("netPayableAmountApproval")
    private Integer netPayableAmountApproval;
    @JsonProperty("netPayableAmountAsString")
    private String netPayableAmountAsString;
    @JsonProperty("netPayableAmountAsStringApproval")
    private String netPayableAmountAsStringApproval;
    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("networkFOCStock")
    private Integer networkFOCStock;
    @JsonProperty("networkFOCStockAsString")
    private String networkFOCStockAsString;
    @JsonProperty("networkINCStock")
    private Integer networkINCStock;
    @JsonProperty("networkStock")
    private Integer networkStock;
    @JsonProperty("networkStockAfterReversal")
    private Integer networkStockAfterReversal;
    @JsonProperty("networkStockAsString")
    private String networkStockAsString;
    @JsonProperty("otfAmount")
    private Integer otfAmount;
    @JsonProperty("otfApplicableFrom")
    private String otfApplicableFrom;
    @JsonProperty("otfApplicableTo")
    private String otfApplicableTo;
    @JsonProperty("otfAsString")
    private String otfAsString;
    @JsonProperty("otfCountsUpdated")
    private Boolean otfCountsUpdated;
    @JsonProperty("otfRate")
    private Integer otfRate;
    @JsonProperty("otfRateAsString")
    private String otfRateAsString;
    @JsonProperty("otfTimeSlab")
    private String otfTimeSlab;
    @JsonProperty("otfTypePctOrAMt")
    private String otfTypePctOrAMt;
    @JsonProperty("othCommAsString")
    private String othCommAsString;
    @JsonProperty("othCommProfType")
    private String othCommProfType;
    @JsonProperty("othCommProfValue")
    private String othCommProfValue;
    @JsonProperty("othCommRate")
    private Integer othCommRate;
    @JsonProperty("othCommRateAsString")
    private String othCommRateAsString;
    @JsonProperty("othCommSetId")
    private String othCommSetId;
    @JsonProperty("othCommType")
    private String othCommType;
    @JsonProperty("othCommValue")
    private Integer othCommValue;
    @JsonProperty("othSlabDefine")
    private Boolean othSlabDefine;
    @JsonProperty("payableAmount")
    private Integer payableAmount;
    @JsonProperty("payableAmountApproval")
    private Integer payableAmountApproval;
    @JsonProperty("payableAmountAsString")
    private String payableAmountAsString;
    @JsonProperty("payableAmountAsStringApproval")
    private String payableAmountAsStringApproval;
    @JsonProperty("paymentType")
    private String paymentType;
    @JsonProperty("previousBalance")
    private Integer previousBalance;
    @JsonProperty("previousBonusBalance")
    private Integer previousBonusBalance;
    @JsonProperty("productCategory")
    private String productCategory;
    @JsonProperty("productCode")
    private String productCode;
    @JsonProperty("productCost")
    private String productCost;
    @JsonProperty("productFirstApprovedQty")
    private String productFirstApprovedQty;
    @JsonProperty("productId")
    private String productId;
    @JsonProperty("productMrpStr")
    private String productMrpStr;
    @JsonProperty("productName")
    private String productName;
    @JsonProperty("productSecondApprovedQty")
    private String productSecondApprovedQty;
    @JsonProperty("productShortCode")
    private Integer productShortCode;
    @JsonProperty("productStatusName")
    private String productStatusName;
    @JsonProperty("productThirdApprovedQty")
    private String productThirdApprovedQty;
    @JsonProperty("productTotalMRP")
    private Integer productTotalMRP;
    @JsonProperty("productType")
    private String productType;
    @JsonProperty("productUsage")
    private String productUsage;
    @JsonProperty("productUsageName")
    private String productUsageName;
    @JsonProperty("receiverCreditQty")
    private Integer receiverCreditQty;
    @JsonProperty("receiverCreditQtyAsString")
    private String receiverCreditQtyAsString;
    @JsonProperty("receiverPostBalAsString")
    private String receiverPostBalAsString;
    @JsonProperty("receiverPostStock")
    private Integer receiverPostStock;
    @JsonProperty("receiverPreviousBalAsString")
    private String receiverPreviousBalAsString;
    @JsonProperty("receiverPreviousStock")
    private Integer receiverPreviousStock;
    @JsonProperty("requestedQuantity")
    private String requestedQuantity;
    @JsonProperty("requiredQuantity")
    private Integer requiredQuantity;
    @JsonProperty("reversalQty")
    private Integer reversalQty;
    @JsonProperty("reversalRequest")
    private Boolean reversalRequest;
    @JsonProperty("reversalRequestedQuantity")
    private String reversalRequestedQuantity;
    @JsonProperty("secondApprovedQuantity")
    private String secondApprovedQuantity;
    @JsonProperty("senderBalance")
    private Integer senderBalance;
    @JsonProperty("senderBalanceAsString")
    private String senderBalanceAsString;
    @JsonProperty("senderDebitQty")
    private Integer senderDebitQty;
    @JsonProperty("senderDebitQtyAsString")
    private String senderDebitQtyAsString;
    @JsonProperty("senderPostBalAsString")
    private String senderPostBalAsString;
    @JsonProperty("senderPostStock")
    private Integer senderPostStock;
    @JsonProperty("senderPreviousBalAsString")
    private String senderPreviousBalAsString;
    @JsonProperty("senderPreviousStock")
    private Integer senderPreviousStock;
    @JsonProperty("serialNum")
    private Integer serialNum;
    @JsonProperty("shortName")
    private String shortName;
    @JsonProperty("slabDefine")
    private Boolean slabDefine;
    @JsonProperty("startRange")
    private Integer startRange;
    @JsonProperty("status")
    private String status;
    @JsonProperty("targetAchieved")
    private Boolean targetAchieved;
    @JsonProperty("tax1Rate")
    private Integer tax1Rate;
    @JsonProperty("tax1RateAsString")
    private String tax1RateAsString;
    @JsonProperty("tax1Type")
    private String tax1Type;
    @JsonProperty("tax1Value")
    private Integer tax1Value;
    @JsonProperty("tax1ValueAsString")
    private String tax1ValueAsString;
    @JsonProperty("tax2Rate")
    private Integer tax2Rate;
    @JsonProperty("tax2RateAsString")
    private String tax2RateAsString;
    @JsonProperty("tax2Type")
    private String tax2Type;
    @JsonProperty("tax2Value")
    private Integer tax2Value;
    @JsonProperty("tax2ValueAsString")
    private String tax2ValueAsString;
    @JsonProperty("tax3Rate")
    private Integer tax3Rate;
    @JsonProperty("tax3RateAsString")
    private String tax3RateAsString;
    @JsonProperty("tax3Type")
    private String tax3Type;
    @JsonProperty("tax3Value")
    private Integer tax3Value;
    @JsonProperty("tax3ValueAsString")
    private String tax3ValueAsString;
    @JsonProperty("taxOnC2CTransfer")
    private String taxOnC2CTransfer;
    @JsonProperty("taxOnChannelTransfer")
    private String taxOnChannelTransfer;
    @JsonProperty("taxOnFOCTransfer")
    private String taxOnFOCTransfer;
    @JsonProperty("thirdApprovedQuantity")
    private String thirdApprovedQuantity;
    @JsonProperty("totalReceiverBalance")
    private Integer totalReceiverBalance;
    @JsonProperty("totalSenderBalance")
    private Integer totalSenderBalance;
    @JsonProperty("transactionType")
    private String transactionType;
    @JsonProperty("transferID")
    private String transferID;
    @JsonProperty("transferMultipleOf")
    private Integer transferMultipleOf;
    @JsonProperty("unitValue")
    private Integer unitValue;
    @JsonProperty("unitValueAsString")
    private String unitValueAsString;
    @JsonProperty("usage")
    private String usage;
    @JsonProperty("userCategory")
    private String userCategory;
    @JsonProperty("userOTFCountsVO")
    private UserOTFCountsVO userOTFCountsVO;
    @JsonProperty("userWallet")
    private String userWallet;
    @JsonProperty("voucherQuantity")
    private Integer voucherQuantity;
    @JsonProperty("walletBalanceStr")
    private String walletBalanceStr;
    @JsonProperty("walletType")
    private String walletType;
    @JsonProperty("walletbalance")
    private Integer walletbalance;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("afterTransCommisionSenderPreviousStock")
    public Integer getAfterTransCommisionSenderPreviousStock() {
        return afterTransCommisionSenderPreviousStock;
    }

    @JsonProperty("afterTransCommisionSenderPreviousStock")
    public void setAfterTransCommisionSenderPreviousStock(Integer afterTransCommisionSenderPreviousStock) {
        this.afterTransCommisionSenderPreviousStock = afterTransCommisionSenderPreviousStock;
    }

    @JsonProperty("afterTransReceiverPreviousStock")
    public Integer getAfterTransReceiverPreviousStock() {
        return afterTransReceiverPreviousStock;
    }

    @JsonProperty("afterTransReceiverPreviousStock")
    public void setAfterTransReceiverPreviousStock(Integer afterTransReceiverPreviousStock) {
        this.afterTransReceiverPreviousStock = afterTransReceiverPreviousStock;
    }

    @JsonProperty("afterTransSenderPreviousBonusStock")
    public Integer getAfterTransSenderPreviousBonusStock() {
        return afterTransSenderPreviousBonusStock;
    }

    @JsonProperty("afterTransSenderPreviousBonusStock")
    public void setAfterTransSenderPreviousBonusStock(Integer afterTransSenderPreviousBonusStock) {
        this.afterTransSenderPreviousBonusStock = afterTransSenderPreviousBonusStock;
    }

    @JsonProperty("afterTransSenderPreviousStock")
    public Integer getAfterTransSenderPreviousStock() {
        return afterTransSenderPreviousStock;
    }

    @JsonProperty("afterTransSenderPreviousStock")
    public void setAfterTransSenderPreviousStock(Integer afterTransSenderPreviousStock) {
        this.afterTransSenderPreviousStock = afterTransSenderPreviousStock;
    }

    @JsonProperty("alertingBalance")
    public String getAlertingBalance() {
        return alertingBalance;
    }

    @JsonProperty("alertingBalance")
    public void setAlertingBalance(String alertingBalance) {
        this.alertingBalance = alertingBalance;
    }

    @JsonProperty("approvedQuantity")
    public Integer getApprovedQuantity() {
        return approvedQuantity;
    }

    @JsonProperty("approvedQuantity")
    public void setApprovedQuantity(Integer approvedQuantity) {
        this.approvedQuantity = approvedQuantity;
    }

    @JsonProperty("approvedQuantityAsString")
    public String getApprovedQuantityAsString() {
        return approvedQuantityAsString;
    }

    @JsonProperty("approvedQuantityAsString")
    public void setApprovedQuantityAsString(String approvedQuantityAsString) {
        this.approvedQuantityAsString = approvedQuantityAsString;
    }

    @JsonProperty("balance")
    public Integer getBalance() {
        return balance;
    }

    @JsonProperty("balance")
    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    @JsonProperty("balanceAsString")
    public String getBalanceAsString() {
        return balanceAsString;
    }

    @JsonProperty("balanceAsString")
    public void setBalanceAsString(String balanceAsString) {
        this.balanceAsString = balanceAsString;
    }

    @JsonProperty("balanceType")
    public String getBalanceType() {
        return balanceType;
    }

    @JsonProperty("balanceType")
    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    @JsonProperty("bonusBalance")
    public Integer getBonusBalance() {
        return bonusBalance;
    }

    @JsonProperty("bonusBalance")
    public void setBonusBalance(Integer bonusBalance) {
        this.bonusBalance = bonusBalance;
    }

    @JsonProperty("bonusDebtQty")
    public Integer getBonusDebtQty() {
        return bonusDebtQty;
    }

    @JsonProperty("bonusDebtQty")
    public void setBonusDebtQty(Integer bonusDebtQty) {
        this.bonusDebtQty = bonusDebtQty;
    }

    @JsonProperty("bundleID")
    public String getBundleID() {
        return bundleID;
    }

    @JsonProperty("bundleID")
    public void setBundleID(String bundleID) {
        this.bundleID = bundleID;
    }

    @JsonProperty("cellId")
    public String getCellId() {
        return cellId;
    }

    @JsonProperty("cellId")
    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    @JsonProperty("commAsString")
    public String getCommAsString() {
        return commAsString;
    }

    @JsonProperty("commAsString")
    public void setCommAsString(String commAsString) {
        this.commAsString = commAsString;
    }

    @JsonProperty("commProfDetailId")
    public String getCommProfDetailId() {
        return commProfDetailId;
    }

    @JsonProperty("commProfDetailId")
    public void setCommProfDetailId(String commProfDetailId) {
        this.commProfDetailId = commProfDetailId;
    }

    @JsonProperty("commProfileDetailID")
    public String getCommProfileDetailID() {
        return commProfileDetailID;
    }

    @JsonProperty("commProfileDetailID")
    public void setCommProfileDetailID(String commProfileDetailID) {
        this.commProfileDetailID = commProfileDetailID;
    }

    @JsonProperty("commProfileProductID")
    public String getCommProfileProductID() {
        return commProfileProductID;
    }

    @JsonProperty("commProfileProductID")
    public void setCommProfileProductID(String commProfileProductID) {
        this.commProfileProductID = commProfileProductID;
    }

    @JsonProperty("commQuantity")
    public Integer getCommQuantity() {
        return commQuantity;
    }

    @JsonProperty("commQuantity")
    public void setCommQuantity(Integer commQuantity) {
        this.commQuantity = commQuantity;
    }

    @JsonProperty("commQuantityAsString")
    public String getCommQuantityAsString() {
        return commQuantityAsString;
    }

    @JsonProperty("commQuantityAsString")
    public void setCommQuantityAsString(String commQuantityAsString) {
        this.commQuantityAsString = commQuantityAsString;
    }

    @JsonProperty("commRate")
    public Integer getCommRate() {
        return commRate;
    }

    @JsonProperty("commRate")
    public void setCommRate(Integer commRate) {
        this.commRate = commRate;
    }

    @JsonProperty("commRateAsString")
    public String getCommRateAsString() {
        return commRateAsString;
    }

    @JsonProperty("commRateAsString")
    public void setCommRateAsString(String commRateAsString) {
        this.commRateAsString = commRateAsString;
    }

    @JsonProperty("commType")
    public String getCommType() {
        return commType;
    }

    @JsonProperty("commType")
    public void setCommType(String commType) {
        this.commType = commType;
    }

    @JsonProperty("commValue")
    public Integer getCommValue() {
        return commValue;
    }

    @JsonProperty("commValue")
    public void setCommValue(Integer commValue) {
        this.commValue = commValue;
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

    @JsonProperty("commissionValuePosi")
    public Integer getCommissionValuePosi() {
        return commissionValuePosi;
    }

    @JsonProperty("commissionValuePosi")
    public void setCommissionValuePosi(Integer commissionValuePosi) {
        this.commissionValuePosi = commissionValuePosi;
    }

    @JsonProperty("commissionValuePosiAsString")
    public String getCommissionValuePosiAsString() {
        return commissionValuePosiAsString;
    }

    @JsonProperty("commissionValuePosiAsString")
    public void setCommissionValuePosiAsString(String commissionValuePosiAsString) {
        this.commissionValuePosiAsString = commissionValuePosiAsString;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("createdOn")
    public String getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    @JsonProperty("dataList")
    public List<Data> getDataList() {
        return dataList;
    }

    @JsonProperty("dataList")
    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }

    @JsonProperty("discountRate")
    public Integer getDiscountRate() {
        return discountRate;
    }

    @JsonProperty("discountRate")
    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
    }

    @JsonProperty("discountType")
    public String getDiscountType() {
        return discountType;
    }

    @JsonProperty("discountType")
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    @JsonProperty("discountValue")
    public Integer getDiscountValue() {
        return discountValue;
    }

    @JsonProperty("discountValue")
    public void setDiscountValue(Integer discountValue) {
        this.discountValue = discountValue;
    }

    @JsonProperty("endRange")
    public Integer getEndRange() {
        return endRange;
    }

    @JsonProperty("endRange")
    public void setEndRange(Integer endRange) {
        this.endRange = endRange;
    }

    @JsonProperty("erpProductCode")
    public String getErpProductCode() {
        return erpProductCode;
    }

    @JsonProperty("erpProductCode")
    public void setErpProductCode(String erpProductCode) {
        this.erpProductCode = erpProductCode;
    }

    @JsonProperty("extTxnDate")
    public String getExtTxnDate() {
        return extTxnDate;
    }

    @JsonProperty("extTxnDate")
    public void setExtTxnDate(String extTxnDate) {
        this.extTxnDate = extTxnDate;
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

    @JsonProperty("firstApprovedQuantity")
    public String getFirstApprovedQuantity() {
        return firstApprovedQuantity;
    }

    @JsonProperty("firstApprovedQuantity")
    public void setFirstApprovedQuantity(String firstApprovedQuantity) {
        this.firstApprovedQuantity = firstApprovedQuantity;
    }

    @JsonProperty("gradeName")
    public String getGradeName() {
        return gradeName;
    }

    @JsonProperty("gradeName")
    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    @JsonProperty("initialRequestedQuantity")
    public Integer getInitialRequestedQuantity() {
        return initialRequestedQuantity;
    }

    @JsonProperty("initialRequestedQuantity")
    public void setInitialRequestedQuantity(Integer initialRequestedQuantity) {
        this.initialRequestedQuantity = initialRequestedQuantity;
    }

    @JsonProperty("initialRequestedQuantityStr")
    public String getInitialRequestedQuantityStr() {
        return initialRequestedQuantityStr;
    }

    @JsonProperty("initialRequestedQuantityStr")
    public void setInitialRequestedQuantityStr(String initialRequestedQuantityStr) {
        this.initialRequestedQuantityStr = initialRequestedQuantityStr;
    }

    @JsonProperty("initiatorRemarks")
    public String getInitiatorRemarks() {
        return initiatorRemarks;
    }

    @JsonProperty("initiatorRemarks")
    public void setInitiatorRemarks(String initiatorRemarks) {
        this.initiatorRemarks = initiatorRemarks;
    }

    @JsonProperty("language1Message")
    public String getLanguage1Message() {
        return language1Message;
    }

    @JsonProperty("language1Message")
    public void setLanguage1Message(String language1Message) {
        this.language1Message = language1Message;
    }

    @JsonProperty("language2Message")
    public String getLanguage2Message() {
        return language2Message;
    }

    @JsonProperty("language2Message")
    public void setLanguage2Message(String language2Message) {
        this.language2Message = language2Message;
    }

    @JsonProperty("lastModifiedTime")
    public Integer getLastModifiedTime() {
        return lastModifiedTime;
    }

    @JsonProperty("lastModifiedTime")
    public void setLastModifiedTime(Integer lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @JsonProperty("loginId")
    public String getLoginId() {
        return loginId;
    }

    @JsonProperty("loginId")
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    @JsonProperty("mainDebitQty")
    public Integer getMainDebitQty() {
        return mainDebitQty;
    }

    @JsonProperty("mainDebitQty")
    public void setMainDebitQty(Integer mainDebitQty) {
        this.mainDebitQty = mainDebitQty;
    }

    @JsonProperty("maxTransferValue")
    public Integer getMaxTransferValue() {
        return maxTransferValue;
    }

    @JsonProperty("maxTransferValue")
    public void setMaxTransferValue(Integer maxTransferValue) {
        this.maxTransferValue = maxTransferValue;
    }

    @JsonProperty("maxTransferValueAsString")
    public String getMaxTransferValueAsString() {
        return maxTransferValueAsString;
    }

    @JsonProperty("maxTransferValueAsString")
    public void setMaxTransferValueAsString(String maxTransferValueAsString) {
        this.maxTransferValueAsString = maxTransferValueAsString;
    }

    @JsonProperty("minTransferValue")
    public Integer getMinTransferValue() {
        return minTransferValue;
    }

    @JsonProperty("minTransferValue")
    public void setMinTransferValue(Integer minTransferValue) {
        this.minTransferValue = minTransferValue;
    }

    @JsonProperty("minTransferValueAsString")
    public String getMinTransferValueAsString() {
        return minTransferValueAsString;
    }

    @JsonProperty("minTransferValueAsString")
    public void setMinTransferValueAsString(String minTransferValueAsString) {
        this.minTransferValueAsString = minTransferValueAsString;
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

    @JsonProperty("moduleCode")
    public String getModuleCode() {
        return moduleCode;
    }

    @JsonProperty("moduleCode")
    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
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

    @JsonProperty("netPayableAmountApproval")
    public Integer getNetPayableAmountApproval() {
        return netPayableAmountApproval;
    }

    @JsonProperty("netPayableAmountApproval")
    public void setNetPayableAmountApproval(Integer netPayableAmountApproval) {
        this.netPayableAmountApproval = netPayableAmountApproval;
    }

    @JsonProperty("netPayableAmountAsString")
    public String getNetPayableAmountAsString() {
        return netPayableAmountAsString;
    }

    @JsonProperty("netPayableAmountAsString")
    public void setNetPayableAmountAsString(String netPayableAmountAsString) {
        this.netPayableAmountAsString = netPayableAmountAsString;
    }

    @JsonProperty("netPayableAmountAsStringApproval")
    public String getNetPayableAmountAsStringApproval() {
        return netPayableAmountAsStringApproval;
    }

    @JsonProperty("netPayableAmountAsStringApproval")
    public void setNetPayableAmountAsStringApproval(String netPayableAmountAsStringApproval) {
        this.netPayableAmountAsStringApproval = netPayableAmountAsStringApproval;
    }

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("networkFOCStock")
    public Integer getNetworkFOCStock() {
        return networkFOCStock;
    }

    @JsonProperty("networkFOCStock")
    public void setNetworkFOCStock(Integer networkFOCStock) {
        this.networkFOCStock = networkFOCStock;
    }

    @JsonProperty("networkFOCStockAsString")
    public String getNetworkFOCStockAsString() {
        return networkFOCStockAsString;
    }

    @JsonProperty("networkFOCStockAsString")
    public void setNetworkFOCStockAsString(String networkFOCStockAsString) {
        this.networkFOCStockAsString = networkFOCStockAsString;
    }

    @JsonProperty("networkINCStock")
    public Integer getNetworkINCStock() {
        return networkINCStock;
    }

    @JsonProperty("networkINCStock")
    public void setNetworkINCStock(Integer networkINCStock) {
        this.networkINCStock = networkINCStock;
    }

    @JsonProperty("networkStock")
    public Integer getNetworkStock() {
        return networkStock;
    }

    @JsonProperty("networkStock")
    public void setNetworkStock(Integer networkStock) {
        this.networkStock = networkStock;
    }

    @JsonProperty("networkStockAfterReversal")
    public Integer getNetworkStockAfterReversal() {
        return networkStockAfterReversal;
    }

    @JsonProperty("networkStockAfterReversal")
    public void setNetworkStockAfterReversal(Integer networkStockAfterReversal) {
        this.networkStockAfterReversal = networkStockAfterReversal;
    }

    @JsonProperty("networkStockAsString")
    public String getNetworkStockAsString() {
        return networkStockAsString;
    }

    @JsonProperty("networkStockAsString")
    public void setNetworkStockAsString(String networkStockAsString) {
        this.networkStockAsString = networkStockAsString;
    }

    @JsonProperty("otfAmount")
    public Integer getOtfAmount() {
        return otfAmount;
    }

    @JsonProperty("otfAmount")
    public void setOtfAmount(Integer otfAmount) {
        this.otfAmount = otfAmount;
    }

    @JsonProperty("otfApplicableFrom")
    public String getOtfApplicableFrom() {
        return otfApplicableFrom;
    }

    @JsonProperty("otfApplicableFrom")
    public void setOtfApplicableFrom(String otfApplicableFrom) {
        this.otfApplicableFrom = otfApplicableFrom;
    }

    @JsonProperty("otfApplicableTo")
    public String getOtfApplicableTo() {
        return otfApplicableTo;
    }

    @JsonProperty("otfApplicableTo")
    public void setOtfApplicableTo(String otfApplicableTo) {
        this.otfApplicableTo = otfApplicableTo;
    }

    @JsonProperty("otfAsString")
    public String getOtfAsString() {
        return otfAsString;
    }

    @JsonProperty("otfAsString")
    public void setOtfAsString(String otfAsString) {
        this.otfAsString = otfAsString;
    }

    @JsonProperty("otfCountsUpdated")
    public Boolean getOtfCountsUpdated() {
        return otfCountsUpdated;
    }

    @JsonProperty("otfCountsUpdated")
    public void setOtfCountsUpdated(Boolean otfCountsUpdated) {
        this.otfCountsUpdated = otfCountsUpdated;
    }

    @JsonProperty("otfRate")
    public Integer getOtfRate() {
        return otfRate;
    }

    @JsonProperty("otfRate")
    public void setOtfRate(Integer otfRate) {
        this.otfRate = otfRate;
    }

    @JsonProperty("otfRateAsString")
    public String getOtfRateAsString() {
        return otfRateAsString;
    }

    @JsonProperty("otfRateAsString")
    public void setOtfRateAsString(String otfRateAsString) {
        this.otfRateAsString = otfRateAsString;
    }

    @JsonProperty("otfTimeSlab")
    public String getOtfTimeSlab() {
        return otfTimeSlab;
    }

    @JsonProperty("otfTimeSlab")
    public void setOtfTimeSlab(String otfTimeSlab) {
        this.otfTimeSlab = otfTimeSlab;
    }

    @JsonProperty("otfTypePctOrAMt")
    public String getOtfTypePctOrAMt() {
        return otfTypePctOrAMt;
    }

    @JsonProperty("otfTypePctOrAMt")
    public void setOtfTypePctOrAMt(String otfTypePctOrAMt) {
        this.otfTypePctOrAMt = otfTypePctOrAMt;
    }

    @JsonProperty("othCommAsString")
    public String getOthCommAsString() {
        return othCommAsString;
    }

    @JsonProperty("othCommAsString")
    public void setOthCommAsString(String othCommAsString) {
        this.othCommAsString = othCommAsString;
    }

    @JsonProperty("othCommProfType")
    public String getOthCommProfType() {
        return othCommProfType;
    }

    @JsonProperty("othCommProfType")
    public void setOthCommProfType(String othCommProfType) {
        this.othCommProfType = othCommProfType;
    }

    @JsonProperty("othCommProfValue")
    public String getOthCommProfValue() {
        return othCommProfValue;
    }

    @JsonProperty("othCommProfValue")
    public void setOthCommProfValue(String othCommProfValue) {
        this.othCommProfValue = othCommProfValue;
    }

    @JsonProperty("othCommRate")
    public Integer getOthCommRate() {
        return othCommRate;
    }

    @JsonProperty("othCommRate")
    public void setOthCommRate(Integer othCommRate) {
        this.othCommRate = othCommRate;
    }

    @JsonProperty("othCommRateAsString")
    public String getOthCommRateAsString() {
        return othCommRateAsString;
    }

    @JsonProperty("othCommRateAsString")
    public void setOthCommRateAsString(String othCommRateAsString) {
        this.othCommRateAsString = othCommRateAsString;
    }

    @JsonProperty("othCommSetId")
    public String getOthCommSetId() {
        return othCommSetId;
    }

    @JsonProperty("othCommSetId")
    public void setOthCommSetId(String othCommSetId) {
        this.othCommSetId = othCommSetId;
    }

    @JsonProperty("othCommType")
    public String getOthCommType() {
        return othCommType;
    }

    @JsonProperty("othCommType")
    public void setOthCommType(String othCommType) {
        this.othCommType = othCommType;
    }

    @JsonProperty("othCommValue")
    public Integer getOthCommValue() {
        return othCommValue;
    }

    @JsonProperty("othCommValue")
    public void setOthCommValue(Integer othCommValue) {
        this.othCommValue = othCommValue;
    }

    @JsonProperty("othSlabDefine")
    public Boolean getOthSlabDefine() {
        return othSlabDefine;
    }

    @JsonProperty("othSlabDefine")
    public void setOthSlabDefine(Boolean othSlabDefine) {
        this.othSlabDefine = othSlabDefine;
    }

    @JsonProperty("payableAmount")
    public Integer getPayableAmount() {
        return payableAmount;
    }

    @JsonProperty("payableAmount")
    public void setPayableAmount(Integer payableAmount) {
        this.payableAmount = payableAmount;
    }

    @JsonProperty("payableAmountApproval")
    public Integer getPayableAmountApproval() {
        return payableAmountApproval;
    }

    @JsonProperty("payableAmountApproval")
    public void setPayableAmountApproval(Integer payableAmountApproval) {
        this.payableAmountApproval = payableAmountApproval;
    }

    @JsonProperty("payableAmountAsString")
    public String getPayableAmountAsString() {
        return payableAmountAsString;
    }

    @JsonProperty("payableAmountAsString")
    public void setPayableAmountAsString(String payableAmountAsString) {
        this.payableAmountAsString = payableAmountAsString;
    }

    @JsonProperty("payableAmountAsStringApproval")
    public String getPayableAmountAsStringApproval() {
        return payableAmountAsStringApproval;
    }

    @JsonProperty("payableAmountAsStringApproval")
    public void setPayableAmountAsStringApproval(String payableAmountAsStringApproval) {
        this.payableAmountAsStringApproval = payableAmountAsStringApproval;
    }

    @JsonProperty("paymentType")
    public String getPaymentType() {
        return paymentType;
    }

    @JsonProperty("paymentType")
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @JsonProperty("previousBalance")
    public Integer getPreviousBalance() {
        return previousBalance;
    }

    @JsonProperty("previousBalance")
    public void setPreviousBalance(Integer previousBalance) {
        this.previousBalance = previousBalance;
    }

    @JsonProperty("previousBonusBalance")
    public Integer getPreviousBonusBalance() {
        return previousBonusBalance;
    }

    @JsonProperty("previousBonusBalance")
    public void setPreviousBonusBalance(Integer previousBonusBalance) {
        this.previousBonusBalance = previousBonusBalance;
    }

    @JsonProperty("productCategory")
    public String getProductCategory() {
        return productCategory;
    }

    @JsonProperty("productCategory")
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    @JsonProperty("productCode")
    public String getProductCode() {
        return productCode;
    }

    @JsonProperty("productCode")
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @JsonProperty("productCost")
    public String getProductCost() {
        return productCost;
    }

    @JsonProperty("productCost")
    public void setProductCost(String productCost) {
        this.productCost = productCost;
    }

    @JsonProperty("productFirstApprovedQty")
    public String getProductFirstApprovedQty() {
        return productFirstApprovedQty;
    }

    @JsonProperty("productFirstApprovedQty")
    public void setProductFirstApprovedQty(String productFirstApprovedQty) {
        this.productFirstApprovedQty = productFirstApprovedQty;
    }

    @JsonProperty("productId")
    public String getProductId() {
        return productId;
    }

    @JsonProperty("productId")
    public void setProductId(String productId) {
        this.productId = productId;
    }

    @JsonProperty("productMrpStr")
    public String getProductMrpStr() {
        return productMrpStr;
    }

    @JsonProperty("productMrpStr")
    public void setProductMrpStr(String productMrpStr) {
        this.productMrpStr = productMrpStr;
    }

    @JsonProperty("productName")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("productName")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @JsonProperty("productSecondApprovedQty")
    public String getProductSecondApprovedQty() {
        return productSecondApprovedQty;
    }

    @JsonProperty("productSecondApprovedQty")
    public void setProductSecondApprovedQty(String productSecondApprovedQty) {
        this.productSecondApprovedQty = productSecondApprovedQty;
    }

    @JsonProperty("productShortCode")
    public Integer getProductShortCode() {
        return productShortCode;
    }

    @JsonProperty("productShortCode")
    public void setProductShortCode(Integer productShortCode) {
        this.productShortCode = productShortCode;
    }

    @JsonProperty("productStatusName")
    public String getProductStatusName() {
        return productStatusName;
    }

    @JsonProperty("productStatusName")
    public void setProductStatusName(String productStatusName) {
        this.productStatusName = productStatusName;
    }

    @JsonProperty("productThirdApprovedQty")
    public String getProductThirdApprovedQty() {
        return productThirdApprovedQty;
    }

    @JsonProperty("productThirdApprovedQty")
    public void setProductThirdApprovedQty(String productThirdApprovedQty) {
        this.productThirdApprovedQty = productThirdApprovedQty;
    }

    @JsonProperty("productTotalMRP")
    public Integer getProductTotalMRP() {
        return productTotalMRP;
    }

    @JsonProperty("productTotalMRP")
    public void setProductTotalMRP(Integer productTotalMRP) {
        this.productTotalMRP = productTotalMRP;
    }

    @JsonProperty("productType")
    public String getProductType() {
        return productType;
    }

    @JsonProperty("productType")
    public void setProductType(String productType) {
        this.productType = productType;
    }

    @JsonProperty("productUsage")
    public String getProductUsage() {
        return productUsage;
    }

    @JsonProperty("productUsage")
    public void setProductUsage(String productUsage) {
        this.productUsage = productUsage;
    }

    @JsonProperty("productUsageName")
    public String getProductUsageName() {
        return productUsageName;
    }

    @JsonProperty("productUsageName")
    public void setProductUsageName(String productUsageName) {
        this.productUsageName = productUsageName;
    }

    @JsonProperty("receiverCreditQty")
    public Integer getReceiverCreditQty() {
        return receiverCreditQty;
    }

    @JsonProperty("receiverCreditQty")
    public void setReceiverCreditQty(Integer receiverCreditQty) {
        this.receiverCreditQty = receiverCreditQty;
    }

    @JsonProperty("receiverCreditQtyAsString")
    public String getReceiverCreditQtyAsString() {
        return receiverCreditQtyAsString;
    }

    @JsonProperty("receiverCreditQtyAsString")
    public void setReceiverCreditQtyAsString(String receiverCreditQtyAsString) {
        this.receiverCreditQtyAsString = receiverCreditQtyAsString;
    }

    @JsonProperty("receiverPostBalAsString")
    public String getReceiverPostBalAsString() {
        return receiverPostBalAsString;
    }

    @JsonProperty("receiverPostBalAsString")
    public void setReceiverPostBalAsString(String receiverPostBalAsString) {
        this.receiverPostBalAsString = receiverPostBalAsString;
    }

    @JsonProperty("receiverPostStock")
    public Integer getReceiverPostStock() {
        return receiverPostStock;
    }

    @JsonProperty("receiverPostStock")
    public void setReceiverPostStock(Integer receiverPostStock) {
        this.receiverPostStock = receiverPostStock;
    }

    @JsonProperty("receiverPreviousBalAsString")
    public String getReceiverPreviousBalAsString() {
        return receiverPreviousBalAsString;
    }

    @JsonProperty("receiverPreviousBalAsString")
    public void setReceiverPreviousBalAsString(String receiverPreviousBalAsString) {
        this.receiverPreviousBalAsString = receiverPreviousBalAsString;
    }

    @JsonProperty("receiverPreviousStock")
    public Integer getReceiverPreviousStock() {
        return receiverPreviousStock;
    }

    @JsonProperty("receiverPreviousStock")
    public void setReceiverPreviousStock(Integer receiverPreviousStock) {
        this.receiverPreviousStock = receiverPreviousStock;
    }

    @JsonProperty("requestedQuantity")
    public String getRequestedQuantity() {
        return requestedQuantity;
    }

    @JsonProperty("requestedQuantity")
    public void setRequestedQuantity(String requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    @JsonProperty("requiredQuantity")
    public Integer getRequiredQuantity() {
        return requiredQuantity;
    }

    @JsonProperty("requiredQuantity")
    public void setRequiredQuantity(Integer requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    @JsonProperty("reversalQty")
    public Integer getReversalQty() {
        return reversalQty;
    }

    @JsonProperty("reversalQty")
    public void setReversalQty(Integer reversalQty) {
        this.reversalQty = reversalQty;
    }

    @JsonProperty("reversalRequest")
    public Boolean getReversalRequest() {
        return reversalRequest;
    }

    @JsonProperty("reversalRequest")
    public void setReversalRequest(Boolean reversalRequest) {
        this.reversalRequest = reversalRequest;
    }

    @JsonProperty("reversalRequestedQuantity")
    public String getReversalRequestedQuantity() {
        return reversalRequestedQuantity;
    }

    @JsonProperty("reversalRequestedQuantity")
    public void setReversalRequestedQuantity(String reversalRequestedQuantity) {
        this.reversalRequestedQuantity = reversalRequestedQuantity;
    }

    @JsonProperty("secondApprovedQuantity")
    public String getSecondApprovedQuantity() {
        return secondApprovedQuantity;
    }

    @JsonProperty("secondApprovedQuantity")
    public void setSecondApprovedQuantity(String secondApprovedQuantity) {
        this.secondApprovedQuantity = secondApprovedQuantity;
    }

    @JsonProperty("senderBalance")
    public Integer getSenderBalance() {
        return senderBalance;
    }

    @JsonProperty("senderBalance")
    public void setSenderBalance(Integer senderBalance) {
        this.senderBalance = senderBalance;
    }

    @JsonProperty("senderBalanceAsString")
    public String getSenderBalanceAsString() {
        return senderBalanceAsString;
    }

    @JsonProperty("senderBalanceAsString")
    public void setSenderBalanceAsString(String senderBalanceAsString) {
        this.senderBalanceAsString = senderBalanceAsString;
    }

    @JsonProperty("senderDebitQty")
    public Integer getSenderDebitQty() {
        return senderDebitQty;
    }

    @JsonProperty("senderDebitQty")
    public void setSenderDebitQty(Integer senderDebitQty) {
        this.senderDebitQty = senderDebitQty;
    }

    @JsonProperty("senderDebitQtyAsString")
    public String getSenderDebitQtyAsString() {
        return senderDebitQtyAsString;
    }

    @JsonProperty("senderDebitQtyAsString")
    public void setSenderDebitQtyAsString(String senderDebitQtyAsString) {
        this.senderDebitQtyAsString = senderDebitQtyAsString;
    }

    @JsonProperty("senderPostBalAsString")
    public String getSenderPostBalAsString() {
        return senderPostBalAsString;
    }

    @JsonProperty("senderPostBalAsString")
    public void setSenderPostBalAsString(String senderPostBalAsString) {
        this.senderPostBalAsString = senderPostBalAsString;
    }

    @JsonProperty("senderPostStock")
    public Integer getSenderPostStock() {
        return senderPostStock;
    }

    @JsonProperty("senderPostStock")
    public void setSenderPostStock(Integer senderPostStock) {
        this.senderPostStock = senderPostStock;
    }

    @JsonProperty("senderPreviousBalAsString")
    public String getSenderPreviousBalAsString() {
        return senderPreviousBalAsString;
    }

    @JsonProperty("senderPreviousBalAsString")
    public void setSenderPreviousBalAsString(String senderPreviousBalAsString) {
        this.senderPreviousBalAsString = senderPreviousBalAsString;
    }

    @JsonProperty("senderPreviousStock")
    public Integer getSenderPreviousStock() {
        return senderPreviousStock;
    }

    @JsonProperty("senderPreviousStock")
    public void setSenderPreviousStock(Integer senderPreviousStock) {
        this.senderPreviousStock = senderPreviousStock;
    }

    @JsonProperty("serialNum")
    public Integer getSerialNum() {
        return serialNum;
    }

    @JsonProperty("serialNum")
    public void setSerialNum(Integer serialNum) {
        this.serialNum = serialNum;
    }

    @JsonProperty("shortName")
    public String getShortName() {
        return shortName;
    }

    @JsonProperty("shortName")
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @JsonProperty("slabDefine")
    public Boolean getSlabDefine() {
        return slabDefine;
    }

    @JsonProperty("slabDefine")
    public void setSlabDefine(Boolean slabDefine) {
        this.slabDefine = slabDefine;
    }

    @JsonProperty("startRange")
    public Integer getStartRange() {
        return startRange;
    }

    @JsonProperty("startRange")
    public void setStartRange(Integer startRange) {
        this.startRange = startRange;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("targetAchieved")
    public Boolean getTargetAchieved() {
        return targetAchieved;
    }

    @JsonProperty("targetAchieved")
    public void setTargetAchieved(Boolean targetAchieved) {
        this.targetAchieved = targetAchieved;
    }

    @JsonProperty("tax1Rate")
    public Integer getTax1Rate() {
        return tax1Rate;
    }

    @JsonProperty("tax1Rate")
    public void setTax1Rate(Integer tax1Rate) {
        this.tax1Rate = tax1Rate;
    }

    @JsonProperty("tax1RateAsString")
    public String getTax1RateAsString() {
        return tax1RateAsString;
    }

    @JsonProperty("tax1RateAsString")
    public void setTax1RateAsString(String tax1RateAsString) {
        this.tax1RateAsString = tax1RateAsString;
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

    @JsonProperty("tax1ValueAsString")
    public String getTax1ValueAsString() {
        return tax1ValueAsString;
    }

    @JsonProperty("tax1ValueAsString")
    public void setTax1ValueAsString(String tax1ValueAsString) {
        this.tax1ValueAsString = tax1ValueAsString;
    }

    @JsonProperty("tax2Rate")
    public Integer getTax2Rate() {
        return tax2Rate;
    }

    @JsonProperty("tax2Rate")
    public void setTax2Rate(Integer tax2Rate) {
        this.tax2Rate = tax2Rate;
    }

    @JsonProperty("tax2RateAsString")
    public String getTax2RateAsString() {
        return tax2RateAsString;
    }

    @JsonProperty("tax2RateAsString")
    public void setTax2RateAsString(String tax2RateAsString) {
        this.tax2RateAsString = tax2RateAsString;
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

    @JsonProperty("tax2ValueAsString")
    public String getTax2ValueAsString() {
        return tax2ValueAsString;
    }

    @JsonProperty("tax2ValueAsString")
    public void setTax2ValueAsString(String tax2ValueAsString) {
        this.tax2ValueAsString = tax2ValueAsString;
    }

    @JsonProperty("tax3Rate")
    public Integer getTax3Rate() {
        return tax3Rate;
    }

    @JsonProperty("tax3Rate")
    public void setTax3Rate(Integer tax3Rate) {
        this.tax3Rate = tax3Rate;
    }

    @JsonProperty("tax3RateAsString")
    public String getTax3RateAsString() {
        return tax3RateAsString;
    }

    @JsonProperty("tax3RateAsString")
    public void setTax3RateAsString(String tax3RateAsString) {
        this.tax3RateAsString = tax3RateAsString;
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

    @JsonProperty("tax3ValueAsString")
    public String getTax3ValueAsString() {
        return tax3ValueAsString;
    }

    @JsonProperty("tax3ValueAsString")
    public void setTax3ValueAsString(String tax3ValueAsString) {
        this.tax3ValueAsString = tax3ValueAsString;
    }

    @JsonProperty("taxOnC2CTransfer")
    public String getTaxOnC2CTransfer() {
        return taxOnC2CTransfer;
    }

    @JsonProperty("taxOnC2CTransfer")
    public void setTaxOnC2CTransfer(String taxOnC2CTransfer) {
        this.taxOnC2CTransfer = taxOnC2CTransfer;
    }

    @JsonProperty("taxOnChannelTransfer")
    public String getTaxOnChannelTransfer() {
        return taxOnChannelTransfer;
    }

    @JsonProperty("taxOnChannelTransfer")
    public void setTaxOnChannelTransfer(String taxOnChannelTransfer) {
        this.taxOnChannelTransfer = taxOnChannelTransfer;
    }

    @JsonProperty("taxOnFOCTransfer")
    public String getTaxOnFOCTransfer() {
        return taxOnFOCTransfer;
    }

    @JsonProperty("taxOnFOCTransfer")
    public void setTaxOnFOCTransfer(String taxOnFOCTransfer) {
        this.taxOnFOCTransfer = taxOnFOCTransfer;
    }

    @JsonProperty("thirdApprovedQuantity")
    public String getThirdApprovedQuantity() {
        return thirdApprovedQuantity;
    }

    @JsonProperty("thirdApprovedQuantity")
    public void setThirdApprovedQuantity(String thirdApprovedQuantity) {
        this.thirdApprovedQuantity = thirdApprovedQuantity;
    }

    @JsonProperty("totalReceiverBalance")
    public Integer getTotalReceiverBalance() {
        return totalReceiverBalance;
    }

    @JsonProperty("totalReceiverBalance")
    public void setTotalReceiverBalance(Integer totalReceiverBalance) {
        this.totalReceiverBalance = totalReceiverBalance;
    }

    @JsonProperty("totalSenderBalance")
    public Integer getTotalSenderBalance() {
        return totalSenderBalance;
    }

    @JsonProperty("totalSenderBalance")
    public void setTotalSenderBalance(Integer totalSenderBalance) {
        this.totalSenderBalance = totalSenderBalance;
    }

    @JsonProperty("transactionType")
    public String getTransactionType() {
        return transactionType;
    }

    @JsonProperty("transactionType")
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @JsonProperty("transferID")
    public String getTransferID() {
        return transferID;
    }

    @JsonProperty("transferID")
    public void setTransferID(String transferID) {
        this.transferID = transferID;
    }

    @JsonProperty("transferMultipleOf")
    public Integer getTransferMultipleOf() {
        return transferMultipleOf;
    }

    @JsonProperty("transferMultipleOf")
    public void setTransferMultipleOf(Integer transferMultipleOf) {
        this.transferMultipleOf = transferMultipleOf;
    }

    @JsonProperty("unitValue")
    public Integer getUnitValue() {
        return unitValue;
    }

    @JsonProperty("unitValue")
    public void setUnitValue(Integer unitValue) {
        this.unitValue = unitValue;
    }

    @JsonProperty("unitValueAsString")
    public String getUnitValueAsString() {
        return unitValueAsString;
    }

    @JsonProperty("unitValueAsString")
    public void setUnitValueAsString(String unitValueAsString) {
        this.unitValueAsString = unitValueAsString;
    }

    @JsonProperty("usage")
    public String getUsage() {
        return usage;
    }

    @JsonProperty("usage")
    public void setUsage(String usage) {
        this.usage = usage;
    }

    @JsonProperty("userCategory")
    public String getUserCategory() {
        return userCategory;
    }

    @JsonProperty("userCategory")
    public void setUserCategory(String userCategory) {
        this.userCategory = userCategory;
    }

    @JsonProperty("userOTFCountsVO")
    public UserOTFCountsVO getUserOTFCountsVO() {
        return userOTFCountsVO;
    }

    @JsonProperty("userOTFCountsVO")
    public void setUserOTFCountsVO(UserOTFCountsVO userOTFCountsVO) {
        this.userOTFCountsVO = userOTFCountsVO;
    }

    @JsonProperty("userWallet")
    public String getUserWallet() {
        return userWallet;
    }

    @JsonProperty("userWallet")
    public void setUserWallet(String userWallet) {
        this.userWallet = userWallet;
    }

    @JsonProperty("voucherQuantity")
    public Integer getVoucherQuantity() {
        return voucherQuantity;
    }

    @JsonProperty("voucherQuantity")
    public void setVoucherQuantity(Integer voucherQuantity) {
        this.voucherQuantity = voucherQuantity;
    }

    @JsonProperty("walletBalanceStr")
    public String getWalletBalanceStr() {
        return walletBalanceStr;
    }

    @JsonProperty("walletBalanceStr")
    public void setWalletBalanceStr(String walletBalanceStr) {
        this.walletBalanceStr = walletBalanceStr;
    }

    @JsonProperty("walletType")
    public String getWalletType() {
        return walletType;
    }

    @JsonProperty("walletType")
    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    @JsonProperty("walletbalance")
    public Integer getWalletbalance() {
        return walletbalance;
    }

    @JsonProperty("walletbalance")
    public void setWalletbalance(Integer walletbalance) {
        this.walletbalance = walletbalance;
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
