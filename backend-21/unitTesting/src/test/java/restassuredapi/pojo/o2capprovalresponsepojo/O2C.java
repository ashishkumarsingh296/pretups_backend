
package restassuredapi.pojo.o2capprovalresponsepojo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "reqQuantity",
    "status",
    "_transferMRPReplica",
    "userWalletCode",
    "tax1Rate",
    "tax1Type",
    "tax2Rate",
    "tax2Type",
    "senderPostbalance",
    "recieverPostBalance",
    "sosRequestAmount",
    "sosFlag",
    "fileUploaded",
    "lrWithdrawAmt",
    "otfCountsUpdated",
    "targetAchieved",
    "channelTransferitemsVOListforOTF",
    "fromUserGeo",
    "fromOwnerGeo",
    "toUserGeo",
    "toOwnerGeo",
    "toMSISDN",
    "mrp",
    "modifiedOnAsString",
    "approvedAmount",
    "tax1Value",
    "tax2Value",
    "receiverCrQtyAsString",
    "payInstrumentDateAsString",
    "payableAmountAsString",
    "netPayableAmountAsString",
    "adjustmentID",
    "time",
    "userName",
    "msisdn",
    "categoryName",
    "gegoraphyDomainName",
    "parentName",
    "parentMsisdn",
    "parentGeoName",
    "ownerUser",
    "ownerMsisdn",
    "ownerGeo",
    "ownerCat",
    "name",
    "receiverMsisdn",
    "commissionType",
    "commissionRate",
    "commissionValue",
    "transferAmt",
    "marginAmount",
    "marginRate",
    "otfType",
    "parentCategory",
    "paymentInstType",
    "domainName",
    "ownerName",
    "transInCount",
    "transOutCount",
    "transInAmount",
    "fromEXTCODE",
    "toEXTCODE",
    "ownerProfile",
    "parentProfile",
    "externalTranDate",
    "commQtyAsString",
    "senderDrQtyAsString",
    "payableAmounts",
    "netPayableAmounts",
    "approvedQuantity",
    "userID",
    "loginID",
    "serviceTypeName",
    "selectorName",
    "differentialAmount",
    "transactionCount",
    "o2cTransferInCount",
    "o2cTransferInAmount",
    "c2cTransferInCount",
    "c2cTransferInAmount",
    "c2cReturnPlusWithCount",
    "c2cReturnPlusWithINAmount",
    "o2cReturnPlusWithoutCount",
    "o2cReturnPlusWithoutAmount",
    "c2cTransferOutCount",
    "c2cTransferOutAmount",
    "c2cReturnWithOutCount",
    "c2cReturnWithOutAmount",
    "c2sTransferOutCount",
    "c2sTransferAmount",
    "unitValue",
    "requiredQuantity",
    "dualCommissionType",
    "profileNames",
    "curStatus",
    "apprRejStatus",
    "batch_no",
    "product_name",
    "batch_type",
    "from_serial_no",
    "to_serial_no",
    "total_no_of_vouchers",
    "userOTFCountsVO",
    "transferSubTypeAsString",
    "fromMsisdn",
    "toMsisdn",
    "toPrimaryMSISDN",
    "fromCategoryDesc",
    "toCategoryDesc",
    "fromGradeCodeDesc",
    "toGradeCodeDesc",
    "fromCommissionProfileIDDesc",
    "toCommissionProfileIDDesc",
    "toTxnProfileDesc",
    "fromTxnProfileDesc",
    "segment",
    "firstApprovedOnAsString",
    "secondApprovedOnAsString",
    "isFileC2C",
    "uploadedFile",
    "uploadedFilePath",
    "uploadedFileName",
    "channelTransferList",
    "voucherTypeList",
    "slabslist",
    "approvalFile",
    "bundleType",
    "reconciliationFlag",
    "previousStatus",
    "tax3ValueAsString",
    "transOutAmount",
    "commProfileDetailID",
    "sosStatus",
    "sosTxnId",
    "sosProductCode",
    "transactionCode",
    "info1",
    "info2",
    "info3",
    "info4",
    "info5",
    "info6",
    "info7",
    "info8",
    "info9",
    "info10",
    "otfFlag",
    "otfTypePctOrAMt",
    "otfRate",
    "otfAmount",
    "channelSoSVOList",
    "sosSettlementDate",
    "grandName",
    "grandGeo",
    "openingBalance",
    "stockBought",
    "stockReturn",
    "channelReturn",
    "c2sTransfers",
    "closingBalance",
    "reconStatus",
    "netBalance",
    "netLifting",
    "messageArgumentList",
    "toUserMsisdn",
    "channelRemarks",
    "transferInitatedBy",
    "controlTransfer",
    "graphicalDomainCode",
    "receiverCategoryCode",
    "senderTxnProfile",
    "receiverTxnProfile",
    "payInstrumentAmt",
    "commQty",
    "firstApproverLimit",
    "payInstrumentDate",
    "payInstrumentType",
    "payInstrumentNum",
    "commProfileVersion",
    "referenceNum",
    "netPayableAmountAsStr",
    "payableAmountAsStr",
    "web",
    "reversalFlag",
    "erpNum",
    "canceledBy",
    "canceledOn",
    "externalTxnDateAsString",
    "firstApprovalRemark",
    "batchNum",
    "requestedQuantityAsString",
    "secondApprovalRemark",
    "thirdApprovalRemark",
    "paymentInstSource",
    "canceledByApprovedName",
    "commProfileName",
    "firstApprovedByName",
    "receiverTxnProfileName",
    "secondApprovedByName",
    "receiverGradeCodeDesc",
    "thirdApprovedByName",
    "transferInitatedByName",
    "transferMRPAsString",
    "finalApprovedBy",
    "finalApprovedDateAsString",
    "senderTxnProfileName",
    "transferSubTypeValue",
    "dbdateTime",
    "commisionTxnId",
    "levelOneApprovedQuantity",
    "levelThreeApprovedQuantity",
    "levelTwoApprovedQuantity",
    "ntpybleAmt",
    "pybleAmt",
    "pyinsAmt",
    "refTransferID",
    "closeDate",
    "userrevlist",
    "stockUpdated",
    "lrstatus",
    "lrflag",
    "grandMsisdn",
    "channelTransfers",
    "channelVoucherItemsVoList",
    "payInstrumentStatus",
    "payInstrumentName",
    "voucher_type",
    "grandcategory",
    "totalTax1",
    "totalTax2",
    "totalTax3",
    "secondApprovalLimit",
    "channelUserStatus",
    "transferSubType",
    "transactionMode",
    "transferCategoryCode",
    "defaultLang",
    "secondLang",
    "userMsisdn",
    "transferDateAsString",
    "receiverGradeCode",
    "externalTxnDate",
    "externalTxnNum",
    "toUserID",
    "transferMRP",
    "fromUserID",
    "senderDrQty",
    "receiverCrQty",
    "fromChannelUserStatus",
    "toChannelUserStatus",
    "channelTransferitemsVOList",
    "senderGradeCode",
    "fromUserCode",
    "toUserCode",
    "transferCategoryCodeDesc",
    "receiverDomainCode",
    "receiverGgraphicalDomainCode",
    "senderCatName",
    "receiverCategoryDesc",
    "activeUserName",
    "activeUserId",
    "subSid",
    "senderCategory",
    "displayTransferMRP",
    "grphDomainCodeDesc",
    "batchDate",
    "firstApprovedBy",
    "firstApprovedOn",
    "secondApprovedBy",
    "secondApprovedOn",
    "thirdApprovedBy",
    "thirdApprovedOn",
    "domainCode",
    "domainCodeDesc",
    "productType",
    "commProfileSetId",
    "requestedQuantity",
    "walletType",
    "createdBy",
    "fromUserName",
    "toUserName",
    "transferProfileID",
    "transferCategory",
    "categoryCode",
    "requestGatewayCode",
    "requestGatewayType",
    "networkCode",
    "senderLoginID",
    "networkCodeFor",
    "receiverLoginID",
    "cellId",
    "switchId",
    "productCode",
    "address1",
    "address2",
    "city",
    "country",
    "externalCode",
    "referenceID",
    "modifiedBy",
    "createdOn",
    "modifiedOn",
    "email",
    "fullAddress",
    "source",
    "type",
    "state",
    "statusDesc",
    "index",
    "transferID",
    "transferDate",
    "serviceClass",
    "transferType",
    "entryType",
    "netPayableAmount",
    "payableAmount",
    "receiverPreviousStock",
    "senderPreviousStock",
    "senderPostStock",
    "receiverPostStock",
    "lastModifiedTime"
})
public class O2C {

    @JsonProperty("reqQuantity")
    private Object reqQuantity;
    @JsonProperty("status")
    private String status;
    @JsonProperty("_transferMRPReplica")
    private Long transferMRPReplica;
    @JsonProperty("userWalletCode")
    private String userWalletCode;
    @JsonProperty("tax1Rate")
    private Object tax1Rate;
    @JsonProperty("tax1Type")
    private Object tax1Type;
    @JsonProperty("tax2Rate")
    private Object tax2Rate;
    @JsonProperty("tax2Type")
    private Object tax2Type;
    @JsonProperty("senderPostbalance")
    private Long senderPostbalance;
    @JsonProperty("recieverPostBalance")
    private Long recieverPostBalance;
    @JsonProperty("sosRequestAmount")
    private Long sosRequestAmount;
    @JsonProperty("sosFlag")
    private Boolean sosFlag;
    @JsonProperty("fileUploaded")
    private Boolean fileUploaded;
    @JsonProperty("lrWithdrawAmt")
    private Long lrWithdrawAmt;
    @JsonProperty("otfCountsUpdated")
    private Boolean otfCountsUpdated;
    @JsonProperty("targetAchieved")
    private Boolean targetAchieved;
    @JsonProperty("channelTransferitemsVOListforOTF")
    private Object channelTransferitemsVOListforOTF;
    @JsonProperty("fromUserGeo")
    private Object fromUserGeo;
    @JsonProperty("fromOwnerGeo")
    private Object fromOwnerGeo;
    @JsonProperty("toUserGeo")
    private Object toUserGeo;
    @JsonProperty("toOwnerGeo")
    private Object toOwnerGeo;
    @JsonProperty("toMSISDN")
    private String toMSISDN;
    @JsonProperty("mrp")
    private Object mrp;
    @JsonProperty("modifiedOnAsString")
    private Object modifiedOnAsString;
    @JsonProperty("approvedAmount")
    private Object approvedAmount;
    @JsonProperty("tax1Value")
    private Object tax1Value;
    @JsonProperty("tax2Value")
    private Object tax2Value;
    @JsonProperty("receiverCrQtyAsString")
    private Object receiverCrQtyAsString;
    @JsonProperty("payInstrumentDateAsString")
    private Object payInstrumentDateAsString;
    @JsonProperty("payableAmountAsString")
    private String payableAmountAsString;
    @JsonProperty("netPayableAmountAsString")
    private String netPayableAmountAsString;
    @JsonProperty("adjustmentID")
    private Object adjustmentID;
    @JsonProperty("time")
    private Object time;
    @JsonProperty("userName")
    private Object userName;
    @JsonProperty("msisdn")
    private Object msisdn;
    @JsonProperty("categoryName")
    private Object categoryName;
    @JsonProperty("gegoraphyDomainName")
    private Object gegoraphyDomainName;
    @JsonProperty("parentName")
    private Object parentName;
    @JsonProperty("parentMsisdn")
    private Object parentMsisdn;
    @JsonProperty("parentGeoName")
    private Object parentGeoName;
    @JsonProperty("ownerUser")
    private Object ownerUser;
    @JsonProperty("ownerMsisdn")
    private Object ownerMsisdn;
    @JsonProperty("ownerGeo")
    private Object ownerGeo;
    @JsonProperty("ownerCat")
    private Object ownerCat;
    @JsonProperty("name")
    private Object name;
    @JsonProperty("receiverMsisdn")
    private Object receiverMsisdn;
    @JsonProperty("commissionType")
    private Object commissionType;
    @JsonProperty("commissionRate")
    private Object commissionRate;
    @JsonProperty("commissionValue")
    private Object commissionValue;
    @JsonProperty("transferAmt")
    private Object transferAmt;
    @JsonProperty("marginAmount")
    private Object marginAmount;
    @JsonProperty("marginRate")
    private Object marginRate;
    @JsonProperty("otfType")
    private Object otfType;
    @JsonProperty("parentCategory")
    private Object parentCategory;
    @JsonProperty("paymentInstType")
    private Object paymentInstType;
    @JsonProperty("domainName")
    private Object domainName;
    @JsonProperty("ownerName")
    private Object ownerName;
    @JsonProperty("transInCount")
    private Object transInCount;
    @JsonProperty("transOutCount")
    private Object transOutCount;
    @JsonProperty("transInAmount")
    private Object transInAmount;
    @JsonProperty("fromEXTCODE")
    private Object fromEXTCODE;
    @JsonProperty("toEXTCODE")
    private Object toEXTCODE;
    @JsonProperty("ownerProfile")
    private Object ownerProfile;
    @JsonProperty("parentProfile")
    private Object parentProfile;
    @JsonProperty("externalTranDate")
    private Object externalTranDate;
    @JsonProperty("commQtyAsString")
    private Object commQtyAsString;
    @JsonProperty("senderDrQtyAsString")
    private Object senderDrQtyAsString;
    @JsonProperty("payableAmounts")
    private Object payableAmounts;
    @JsonProperty("netPayableAmounts")
    private Object netPayableAmounts;
    @JsonProperty("approvedQuantity")
    private Object approvedQuantity;
    @JsonProperty("userID")
    private Object userID;
    @JsonProperty("loginID")
    private Object loginID;
    @JsonProperty("serviceTypeName")
    private Object serviceTypeName;
    @JsonProperty("selectorName")
    private Object selectorName;
    @JsonProperty("differentialAmount")
    private Object differentialAmount;
    @JsonProperty("transactionCount")
    private Object transactionCount;
    @JsonProperty("o2cTransferInCount")
    private Object o2cTransferInCount;
    @JsonProperty("o2cTransferInAmount")
    private Object o2cTransferInAmount;
    @JsonProperty("c2cTransferInCount")
    private Object c2cTransferInCount;
    @JsonProperty("c2cTransferInAmount")
    private Object c2cTransferInAmount;
    @JsonProperty("c2cReturnPlusWithCount")
    private Object c2cReturnPlusWithCount;
    @JsonProperty("c2cReturnPlusWithINAmount")
    private Object c2cReturnPlusWithINAmount;
    @JsonProperty("o2cReturnPlusWithoutCount")
    private Object o2cReturnPlusWithoutCount;
    @JsonProperty("o2cReturnPlusWithoutAmount")
    private Object o2cReturnPlusWithoutAmount;
    @JsonProperty("c2cTransferOutCount")
    private Object c2cTransferOutCount;
    @JsonProperty("c2cTransferOutAmount")
    private Object c2cTransferOutAmount;
    @JsonProperty("c2cReturnWithOutCount")
    private Object c2cReturnWithOutCount;
    @JsonProperty("c2cReturnWithOutAmount")
    private Object c2cReturnWithOutAmount;
    @JsonProperty("c2sTransferOutCount")
    private Object c2sTransferOutCount;
    @JsonProperty("c2sTransferAmount")
    private Object c2sTransferAmount;
    @JsonProperty("unitValue")
    private Long unitValue;
    @JsonProperty("requiredQuantity")
    private Long requiredQuantity;
    @JsonProperty("dualCommissionType")
    private String dualCommissionType;
    @JsonProperty("profileNames")
    private Object profileNames;
    @JsonProperty("curStatus")
    private Object curStatus;
    @JsonProperty("apprRejStatus")
    private Object apprRejStatus;
    @JsonProperty("batch_no")
    private Object batchNo;
    @JsonProperty("product_name")
    private Object productName;
    @JsonProperty("batch_type")
    private Object batchType;
    @JsonProperty("from_serial_no")
    private Object fromSerialNo;
    @JsonProperty("to_serial_no")
    private Object toSerialNo;
    @JsonProperty("total_no_of_vouchers")
    private Long totalNoOfVouchers;
    @JsonProperty("userOTFCountsVO")
    private Object userOTFCountsVO;
    @JsonProperty("transferSubTypeAsString")
    private String transferSubTypeAsString;
    @JsonProperty("fromMsisdn")
    private Object fromMsisdn;
    @JsonProperty("toMsisdn")
    private Object toMsisdn;
    @JsonProperty("toPrimaryMSISDN")
    private Object toPrimaryMSISDN;
    @JsonProperty("fromCategoryDesc")
    private Object fromCategoryDesc;
    @JsonProperty("toCategoryDesc")
    private Object toCategoryDesc;
    @JsonProperty("fromGradeCodeDesc")
    private Object fromGradeCodeDesc;
    @JsonProperty("toGradeCodeDesc")
    private Object toGradeCodeDesc;
    @JsonProperty("fromCommissionProfileIDDesc")
    private Object fromCommissionProfileIDDesc;
    @JsonProperty("toCommissionProfileIDDesc")
    private Object toCommissionProfileIDDesc;
    @JsonProperty("toTxnProfileDesc")
    private Object toTxnProfileDesc;
    @JsonProperty("fromTxnProfileDesc")
    private Object fromTxnProfileDesc;
    @JsonProperty("segment")
    private Object segment;
    @JsonProperty("firstApprovedOnAsString")
    private Object firstApprovedOnAsString;
    @JsonProperty("secondApprovedOnAsString")
    private Object secondApprovedOnAsString;
    @JsonProperty("isFileC2C")
    private String isFileC2C;
    @JsonProperty("uploadedFile")
    private Object uploadedFile;
    @JsonProperty("uploadedFilePath")
    private Object uploadedFilePath;
    @JsonProperty("uploadedFileName")
    private Object uploadedFileName;
    @JsonProperty("channelTransferList")
    private Object channelTransferList;
    @JsonProperty("voucherTypeList")
    private Object voucherTypeList;
    @JsonProperty("slabslist")
    private Object slabslist;
    @JsonProperty("approvalFile")
    private Object approvalFile;
    @JsonProperty("bundleType")
    private Boolean bundleType;
    @JsonProperty("reconciliationFlag")
    private Boolean reconciliationFlag;
    @JsonProperty("previousStatus")
    private Object previousStatus;
    @JsonProperty("tax3ValueAsString")
    private Object tax3ValueAsString;
    @JsonProperty("transOutAmount")
    private Object transOutAmount;
    @JsonProperty("commProfileDetailID")
    private Object commProfileDetailID;
    @JsonProperty("sosStatus")
    private Object sosStatus;
    @JsonProperty("sosTxnId")
    private Object sosTxnId;
    @JsonProperty("sosProductCode")
    private Object sosProductCode;
    @JsonProperty("transactionCode")
    private Object transactionCode;
    @JsonProperty("info1")
    private Object info1;
    @JsonProperty("info2")
    private Object info2;
    @JsonProperty("info3")
    private Object info3;
    @JsonProperty("info4")
    private Object info4;
    @JsonProperty("info5")
    private Object info5;
    @JsonProperty("info6")
    private Object info6;
    @JsonProperty("info7")
    private Object info7;
    @JsonProperty("info8")
    private Object info8;
    @JsonProperty("info9")
    private Object info9;
    @JsonProperty("info10")
    private Object info10;
    @JsonProperty("otfFlag")
    private Boolean otfFlag;
    @JsonProperty("otfTypePctOrAMt")
    private Object otfTypePctOrAMt;
    @JsonProperty("otfRate")
    private Long otfRate;
    @JsonProperty("otfAmount")
    private Long otfAmount;
    @JsonProperty("channelSoSVOList")
    private Object channelSoSVOList;
    @JsonProperty("sosSettlementDate")
    private Object sosSettlementDate;
    @JsonProperty("grandName")
    private Object grandName;
    @JsonProperty("grandGeo")
    private Object grandGeo;
    @JsonProperty("openingBalance")
    private Object openingBalance;
    @JsonProperty("stockBought")
    private Object stockBought;
    @JsonProperty("stockReturn")
    private Object stockReturn;
    @JsonProperty("channelReturn")
    private Object channelReturn;
    @JsonProperty("c2sTransfers")
    private Object c2sTransfers;
    @JsonProperty("closingBalance")
    private Object closingBalance;
    @JsonProperty("reconStatus")
    private Object reconStatus;
    @JsonProperty("netBalance")
    private Object netBalance;
    @JsonProperty("netLifting")
    private Object netLifting;
    @JsonProperty("messageArgumentList")
    private Object messageArgumentList;
    @JsonProperty("toUserMsisdn")
    private Object toUserMsisdn;
    @JsonProperty("channelRemarks")
    private Object channelRemarks;
    @JsonProperty("transferInitatedBy")
    private Object transferInitatedBy;
    @JsonProperty("controlTransfer")
    private Object controlTransfer;
    @JsonProperty("graphicalDomainCode")
    private Object graphicalDomainCode;
    @JsonProperty("receiverCategoryCode")
    private Object receiverCategoryCode;
    @JsonProperty("senderTxnProfile")
    private Object senderTxnProfile;
    @JsonProperty("receiverTxnProfile")
    private String receiverTxnProfile;
    @JsonProperty("payInstrumentAmt")
    private Long payInstrumentAmt;
    @JsonProperty("commQty")
    private Long commQty;
    @JsonProperty("firstApproverLimit")
    private Long firstApproverLimit;
    @JsonProperty("payInstrumentDate")
    private Object payInstrumentDate;
    @JsonProperty("payInstrumentType")
    private String payInstrumentType;
    @JsonProperty("payInstrumentNum")
    private Object payInstrumentNum;
    @JsonProperty("commProfileVersion")
    private String commProfileVersion;
    @JsonProperty("referenceNum")
    private Object referenceNum;
    @JsonProperty("netPayableAmountAsStr")
    private Object netPayableAmountAsStr;
    @JsonProperty("payableAmountAsStr")
    private Object payableAmountAsStr;
    @JsonProperty("web")
    private Boolean web;
    @JsonProperty("reversalFlag")
    private Boolean reversalFlag;
    @JsonProperty("erpNum")
    private Object erpNum;
    @JsonProperty("canceledBy")
    private Object canceledBy;
    @JsonProperty("canceledOn")
    private Object canceledOn;
    @JsonProperty("externalTxnDateAsString")
    private Object externalTxnDateAsString;
    @JsonProperty("firstApprovalRemark")
    private Object firstApprovalRemark;
    @JsonProperty("batchNum")
    private Object batchNum;
    @JsonProperty("requestedQuantityAsString")
    private String requestedQuantityAsString;
    @JsonProperty("secondApprovalRemark")
    private Object secondApprovalRemark;
    @JsonProperty("thirdApprovalRemark")
    private Object thirdApprovalRemark;
    @JsonProperty("paymentInstSource")
    private Object paymentInstSource;
    @JsonProperty("canceledByApprovedName")
    private Object canceledByApprovedName;
    @JsonProperty("commProfileName")
    private Object commProfileName;
    @JsonProperty("firstApprovedByName")
    private Object firstApprovedByName;
    @JsonProperty("receiverTxnProfileName")
    private Object receiverTxnProfileName;
    @JsonProperty("secondApprovedByName")
    private Object secondApprovedByName;
    @JsonProperty("receiverGradeCodeDesc")
    private Object receiverGradeCodeDesc;
    @JsonProperty("thirdApprovedByName")
    private Object thirdApprovedByName;
    @JsonProperty("transferInitatedByName")
    private String transferInitatedByName;
    @JsonProperty("transferMRPAsString")
    private String transferMRPAsString;
    @JsonProperty("finalApprovedBy")
    private Object finalApprovedBy;
    @JsonProperty("finalApprovedDateAsString")
    private Object finalApprovedDateAsString;
    @JsonProperty("senderTxnProfileName")
    private Object senderTxnProfileName;
    @JsonProperty("transferSubTypeValue")
    private Object transferSubTypeValue;
    @JsonProperty("dbdateTime")
    private Object dbdateTime;
    @JsonProperty("commisionTxnId")
    private Object commisionTxnId;
    @JsonProperty("levelOneApprovedQuantity")
    private Object levelOneApprovedQuantity;
    @JsonProperty("levelThreeApprovedQuantity")
    private Object levelThreeApprovedQuantity;
    @JsonProperty("levelTwoApprovedQuantity")
    private Object levelTwoApprovedQuantity;
    @JsonProperty("ntpybleAmt")
    private Long ntpybleAmt;
    @JsonProperty("pybleAmt")
    private Long pybleAmt;
    @JsonProperty("pyinsAmt")
    private Long pyinsAmt;
    @JsonProperty("refTransferID")
    private Object refTransferID;
    @JsonProperty("closeDate")
    private Object closeDate;
    @JsonProperty("userrevlist")
    private Object userrevlist;
    @JsonProperty("stockUpdated")
    private String stockUpdated;
    @JsonProperty("lrstatus")
    private Object lrstatus;
    @JsonProperty("lrflag")
    private Boolean lrflag;
    @JsonProperty("grandMsisdn")
    private Object grandMsisdn;
    @JsonProperty("channelTransfers")
    private Object channelTransfers;
    @JsonProperty("channelVoucherItemsVoList")
    private Object channelVoucherItemsVoList;
    @JsonProperty("payInstrumentStatus")
    private Object payInstrumentStatus;
    @JsonProperty("payInstrumentName")
    private Object payInstrumentName;
    @JsonProperty("voucher_type")
    private Object voucherType;
    @JsonProperty("grandcategory")
    private Object grandcategory;
    @JsonProperty("totalTax1")
    private Long totalTax1;
    @JsonProperty("totalTax2")
    private Long totalTax2;
    @JsonProperty("totalTax3")
    private Long totalTax3;
    @JsonProperty("secondApprovalLimit")
    private Long secondApprovalLimit;
    @JsonProperty("channelUserStatus")
    private Object channelUserStatus;
    @JsonProperty("transferSubType")
    private String transferSubType;
    @JsonProperty("transactionMode")
    private String transactionMode;
    @JsonProperty("transferCategoryCode")
    private Object transferCategoryCode;
    @JsonProperty("defaultLang")
    private Object defaultLang;
    @JsonProperty("secondLang")
    private Object secondLang;
    @JsonProperty("userMsisdn")
    private Object userMsisdn;
    @JsonProperty("transferDateAsString")
    private String transferDateAsString;
    @JsonProperty("receiverGradeCode")
    private Object receiverGradeCode;
    @JsonProperty("externalTxnDate")
    private Object externalTxnDate;
    @JsonProperty("externalTxnNum")
    private Object externalTxnNum;
    @JsonProperty("toUserID")
    private String toUserID;
    @JsonProperty("transferMRP")
    private Long transferMRP;
    @JsonProperty("fromUserID")
    private Object fromUserID;
    @JsonProperty("senderDrQty")
    private Long senderDrQty;
    @JsonProperty("receiverCrQty")
    private Long receiverCrQty;
    @JsonProperty("fromChannelUserStatus")
    private Object fromChannelUserStatus;
    @JsonProperty("toChannelUserStatus")
    private Object toChannelUserStatus;
    @JsonProperty("channelTransferitemsVOList")
    private Object channelTransferitemsVOList;
    @JsonProperty("senderGradeCode")
    private Object senderGradeCode;
    @JsonProperty("fromUserCode")
    private Object fromUserCode;
    @JsonProperty("toUserCode")
    private Object toUserCode;
    @JsonProperty("transferCategoryCodeDesc")
    private Object transferCategoryCodeDesc;
    @JsonProperty("receiverDomainCode")
    private Object receiverDomainCode;
    @JsonProperty("receiverGgraphicalDomainCode")
    private Object receiverGgraphicalDomainCode;
    @JsonProperty("senderCatName")
    private Object senderCatName;
    @JsonProperty("receiverCategoryDesc")
    private String receiverCategoryDesc;
    @JsonProperty("activeUserName")
    private Object activeUserName;
    @JsonProperty("activeUserId")
    private Object activeUserId;
    @JsonProperty("subSid")
    private Object subSid;
    @JsonProperty("senderCategory")
    private Object senderCategory;
    @JsonProperty("displayTransferMRP")
    private Object displayTransferMRP;
    @JsonProperty("grphDomainCodeDesc")
    private String grphDomainCodeDesc;
    @JsonProperty("batchDate")
    private Object batchDate;
    @JsonProperty("firstApprovedBy")
    private Object firstApprovedBy;
    @JsonProperty("firstApprovedOn")
    private Object firstApprovedOn;
    @JsonProperty("secondApprovedBy")
    private Object secondApprovedBy;
    @JsonProperty("secondApprovedOn")
    private Object secondApprovedOn;
    @JsonProperty("thirdApprovedBy")
    private Object thirdApprovedBy;
    @JsonProperty("thirdApprovedOn")
    private Object thirdApprovedOn;
    @JsonProperty("domainCode")
    private Object domainCode;
    @JsonProperty("domainCodeDesc")
    private String domainCodeDesc;
    @JsonProperty("productType")
    private String productType;
    @JsonProperty("commProfileSetId")
    private String commProfileSetId;
    @JsonProperty("requestedQuantity")
    private Long requestedQuantity;
    @JsonProperty("walletType")
    private String walletType;
    @JsonProperty("createdBy")
    private Object createdBy;
    @JsonProperty("fromUserName")
    private Object fromUserName;
    @JsonProperty("toUserName")
    private String toUserName;
    @JsonProperty("transferProfileID")
    private Object transferProfileID;
    @JsonProperty("transferCategory")
    private Object transferCategory;
    @JsonProperty("categoryCode")
    private Object categoryCode;
    @JsonProperty("requestGatewayCode")
    private Object requestGatewayCode;
    @JsonProperty("requestGatewayType")
    private Object requestGatewayType;
    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("senderLoginID")
    private Object senderLoginID;
    @JsonProperty("networkCodeFor")
    private String networkCodeFor;
    @JsonProperty("receiverLoginID")
    private Object receiverLoginID;
    @JsonProperty("cellId")
    private Object cellId;
    @JsonProperty("switchId")
    private Object switchId;
    @JsonProperty("productCode")
    private Object productCode;
    @JsonProperty("address1")
    private Object address1;
    @JsonProperty("address2")
    private Object address2;
    @JsonProperty("city")
    private Object city;
    @JsonProperty("country")
    private Object country;
    @JsonProperty("externalCode")
    private Object externalCode;
    @JsonProperty("referenceID")
    private Object referenceID;
    @JsonProperty("modifiedBy")
    private Object modifiedBy;
    @JsonProperty("createdOn")
    private Object createdOn;
    @JsonProperty("modifiedOn")
    private Object modifiedOn;
    @JsonProperty("email")
    private Object email;
    @JsonProperty("fullAddress")
    private String fullAddress;
    @JsonProperty("source")
    private Object source;
    @JsonProperty("type")
    private String type;
    @JsonProperty("state")
    private Object state;
    @JsonProperty("statusDesc")
    private String statusDesc;
    @JsonProperty("index")
    private Long index;
    @JsonProperty("transferID")
    private String transferID;
    @JsonProperty("transferDate")
    private Long transferDate;
    @JsonProperty("serviceClass")
    private Object serviceClass;
    @JsonProperty("transferType")
    private String transferType;
    @JsonProperty("entryType")
    private Object entryType;
    @JsonProperty("netPayableAmount")
    private Long netPayableAmount;
    @JsonProperty("payableAmount")
    private Long payableAmount;
    @JsonProperty("receiverPreviousStock")
    private Long receiverPreviousStock;
    @JsonProperty("senderPreviousStock")
    private Long senderPreviousStock;
    @JsonProperty("senderPostStock")
    private Object senderPostStock;
    @JsonProperty("receiverPostStock")
    private Object receiverPostStock;
    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("reqQuantity")
    public Object getReqQuantity() {
        return reqQuantity;
    }

    @JsonProperty("reqQuantity")
    public void setReqQuantity(Object reqQuantity) {
        this.reqQuantity = reqQuantity;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("_transferMRPReplica")
    public Long getTransferMRPReplica() {
        return transferMRPReplica;
    }

    @JsonProperty("_transferMRPReplica")
    public void setTransferMRPReplica(Long transferMRPReplica) {
        this.transferMRPReplica = transferMRPReplica;
    }

    @JsonProperty("userWalletCode")
    public String getUserWalletCode() {
        return userWalletCode;
    }

    @JsonProperty("userWalletCode")
    public void setUserWalletCode(String userWalletCode) {
        this.userWalletCode = userWalletCode;
    }

    @JsonProperty("tax1Rate")
    public Object getTax1Rate() {
        return tax1Rate;
    }

    @JsonProperty("tax1Rate")
    public void setTax1Rate(Object tax1Rate) {
        this.tax1Rate = tax1Rate;
    }

    @JsonProperty("tax1Type")
    public Object getTax1Type() {
        return tax1Type;
    }

    @JsonProperty("tax1Type")
    public void setTax1Type(Object tax1Type) {
        this.tax1Type = tax1Type;
    }

    @JsonProperty("tax2Rate")
    public Object getTax2Rate() {
        return tax2Rate;
    }

    @JsonProperty("tax2Rate")
    public void setTax2Rate(Object tax2Rate) {
        this.tax2Rate = tax2Rate;
    }

    @JsonProperty("tax2Type")
    public Object getTax2Type() {
        return tax2Type;
    }

    @JsonProperty("tax2Type")
    public void setTax2Type(Object tax2Type) {
        this.tax2Type = tax2Type;
    }

    @JsonProperty("senderPostbalance")
    public Long getSenderPostbalance() {
        return senderPostbalance;
    }

    @JsonProperty("senderPostbalance")
    public void setSenderPostbalance(Long senderPostbalance) {
        this.senderPostbalance = senderPostbalance;
    }

    @JsonProperty("recieverPostBalance")
    public Long getRecieverPostBalance() {
        return recieverPostBalance;
    }

    @JsonProperty("recieverPostBalance")
    public void setRecieverPostBalance(Long recieverPostBalance) {
        this.recieverPostBalance = recieverPostBalance;
    }

    @JsonProperty("sosRequestAmount")
    public Long getSosRequestAmount() {
        return sosRequestAmount;
    }

    @JsonProperty("sosRequestAmount")
    public void setSosRequestAmount(Long sosRequestAmount) {
        this.sosRequestAmount = sosRequestAmount;
    }

    @JsonProperty("sosFlag")
    public Boolean getSosFlag() {
        return sosFlag;
    }

    @JsonProperty("sosFlag")
    public void setSosFlag(Boolean sosFlag) {
        this.sosFlag = sosFlag;
    }

    @JsonProperty("fileUploaded")
    public Boolean getFileUploaded() {
        return fileUploaded;
    }

    @JsonProperty("fileUploaded")
    public void setFileUploaded(Boolean fileUploaded) {
        this.fileUploaded = fileUploaded;
    }

    @JsonProperty("lrWithdrawAmt")
    public Long getLrWithdrawAmt() {
        return lrWithdrawAmt;
    }

    @JsonProperty("lrWithdrawAmt")
    public void setLrWithdrawAmt(Long lrWithdrawAmt) {
        this.lrWithdrawAmt = lrWithdrawAmt;
    }

    @JsonProperty("otfCountsUpdated")
    public Boolean getOtfCountsUpdated() {
        return otfCountsUpdated;
    }

    @JsonProperty("otfCountsUpdated")
    public void setOtfCountsUpdated(Boolean otfCountsUpdated) {
        this.otfCountsUpdated = otfCountsUpdated;
    }

    @JsonProperty("targetAchieved")
    public Boolean getTargetAchieved() {
        return targetAchieved;
    }

    @JsonProperty("targetAchieved")
    public void setTargetAchieved(Boolean targetAchieved) {
        this.targetAchieved = targetAchieved;
    }

    @JsonProperty("channelTransferitemsVOListforOTF")
    public Object getChannelTransferitemsVOListforOTF() {
        return channelTransferitemsVOListforOTF;
    }

    @JsonProperty("channelTransferitemsVOListforOTF")
    public void setChannelTransferitemsVOListforOTF(Object channelTransferitemsVOListforOTF) {
        this.channelTransferitemsVOListforOTF = channelTransferitemsVOListforOTF;
    }

    @JsonProperty("fromUserGeo")
    public Object getFromUserGeo() {
        return fromUserGeo;
    }

    @JsonProperty("fromUserGeo")
    public void setFromUserGeo(Object fromUserGeo) {
        this.fromUserGeo = fromUserGeo;
    }

    @JsonProperty("fromOwnerGeo")
    public Object getFromOwnerGeo() {
        return fromOwnerGeo;
    }

    @JsonProperty("fromOwnerGeo")
    public void setFromOwnerGeo(Object fromOwnerGeo) {
        this.fromOwnerGeo = fromOwnerGeo;
    }

    @JsonProperty("toUserGeo")
    public Object getToUserGeo() {
        return toUserGeo;
    }

    @JsonProperty("toUserGeo")
    public void setToUserGeo(Object toUserGeo) {
        this.toUserGeo = toUserGeo;
    }

    @JsonProperty("toOwnerGeo")
    public Object getToOwnerGeo() {
        return toOwnerGeo;
    }

    @JsonProperty("toOwnerGeo")
    public void setToOwnerGeo(Object toOwnerGeo) {
        this.toOwnerGeo = toOwnerGeo;
    }

    @JsonProperty("toMSISDN")
    public String getToMSISDN() {
        return toMSISDN;
    }

    @JsonProperty("toMSISDN")
    public void setToMSISDN(String toMSISDN) {
        this.toMSISDN = toMSISDN;
    }

    @JsonProperty("mrp")
    public Object getMrp() {
        return mrp;
    }

    @JsonProperty("mrp")
    public void setMrp(Object mrp) {
        this.mrp = mrp;
    }

    @JsonProperty("modifiedOnAsString")
    public Object getModifiedOnAsString() {
        return modifiedOnAsString;
    }

    @JsonProperty("modifiedOnAsString")
    public void setModifiedOnAsString(Object modifiedOnAsString) {
        this.modifiedOnAsString = modifiedOnAsString;
    }

    @JsonProperty("approvedAmount")
    public Object getApprovedAmount() {
        return approvedAmount;
    }

    @JsonProperty("approvedAmount")
    public void setApprovedAmount(Object approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    @JsonProperty("tax1Value")
    public Object getTax1Value() {
        return tax1Value;
    }

    @JsonProperty("tax1Value")
    public void setTax1Value(Object tax1Value) {
        this.tax1Value = tax1Value;
    }

    @JsonProperty("tax2Value")
    public Object getTax2Value() {
        return tax2Value;
    }

    @JsonProperty("tax2Value")
    public void setTax2Value(Object tax2Value) {
        this.tax2Value = tax2Value;
    }

    @JsonProperty("receiverCrQtyAsString")
    public Object getReceiverCrQtyAsString() {
        return receiverCrQtyAsString;
    }

    @JsonProperty("receiverCrQtyAsString")
    public void setReceiverCrQtyAsString(Object receiverCrQtyAsString) {
        this.receiverCrQtyAsString = receiverCrQtyAsString;
    }

    @JsonProperty("payInstrumentDateAsString")
    public Object getPayInstrumentDateAsString() {
        return payInstrumentDateAsString;
    }

    @JsonProperty("payInstrumentDateAsString")
    public void setPayInstrumentDateAsString(Object payInstrumentDateAsString) {
        this.payInstrumentDateAsString = payInstrumentDateAsString;
    }

    @JsonProperty("payableAmountAsString")
    public String getPayableAmountAsString() {
        return payableAmountAsString;
    }

    @JsonProperty("payableAmountAsString")
    public void setPayableAmountAsString(String payableAmountAsString) {
        this.payableAmountAsString = payableAmountAsString;
    }

    @JsonProperty("netPayableAmountAsString")
    public String getNetPayableAmountAsString() {
        return netPayableAmountAsString;
    }

    @JsonProperty("netPayableAmountAsString")
    public void setNetPayableAmountAsString(String netPayableAmountAsString) {
        this.netPayableAmountAsString = netPayableAmountAsString;
    }

    @JsonProperty("adjustmentID")
    public Object getAdjustmentID() {
        return adjustmentID;
    }

    @JsonProperty("adjustmentID")
    public void setAdjustmentID(Object adjustmentID) {
        this.adjustmentID = adjustmentID;
    }

    @JsonProperty("time")
    public Object getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(Object time) {
        this.time = time;
    }

    @JsonProperty("userName")
    public Object getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(Object userName) {
        this.userName = userName;
    }

    @JsonProperty("msisdn")
    public Object getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(Object msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("categoryName")
    public Object getCategoryName() {
        return categoryName;
    }

    @JsonProperty("categoryName")
    public void setCategoryName(Object categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("gegoraphyDomainName")
    public Object getGegoraphyDomainName() {
        return gegoraphyDomainName;
    }

    @JsonProperty("gegoraphyDomainName")
    public void setGegoraphyDomainName(Object gegoraphyDomainName) {
        this.gegoraphyDomainName = gegoraphyDomainName;
    }

    @JsonProperty("parentName")
    public Object getParentName() {
        return parentName;
    }

    @JsonProperty("parentName")
    public void setParentName(Object parentName) {
        this.parentName = parentName;
    }

    @JsonProperty("parentMsisdn")
    public Object getParentMsisdn() {
        return parentMsisdn;
    }

    @JsonProperty("parentMsisdn")
    public void setParentMsisdn(Object parentMsisdn) {
        this.parentMsisdn = parentMsisdn;
    }

    @JsonProperty("parentGeoName")
    public Object getParentGeoName() {
        return parentGeoName;
    }

    @JsonProperty("parentGeoName")
    public void setParentGeoName(Object parentGeoName) {
        this.parentGeoName = parentGeoName;
    }

    @JsonProperty("ownerUser")
    public Object getOwnerUser() {
        return ownerUser;
    }

    @JsonProperty("ownerUser")
    public void setOwnerUser(Object ownerUser) {
        this.ownerUser = ownerUser;
    }

    @JsonProperty("ownerMsisdn")
    public Object getOwnerMsisdn() {
        return ownerMsisdn;
    }

    @JsonProperty("ownerMsisdn")
    public void setOwnerMsisdn(Object ownerMsisdn) {
        this.ownerMsisdn = ownerMsisdn;
    }

    @JsonProperty("ownerGeo")
    public Object getOwnerGeo() {
        return ownerGeo;
    }

    @JsonProperty("ownerGeo")
    public void setOwnerGeo(Object ownerGeo) {
        this.ownerGeo = ownerGeo;
    }

    @JsonProperty("ownerCat")
    public Object getOwnerCat() {
        return ownerCat;
    }

    @JsonProperty("ownerCat")
    public void setOwnerCat(Object ownerCat) {
        this.ownerCat = ownerCat;
    }

    @JsonProperty("name")
    public Object getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(Object name) {
        this.name = name;
    }

    @JsonProperty("receiverMsisdn")
    public Object getReceiverMsisdn() {
        return receiverMsisdn;
    }

    @JsonProperty("receiverMsisdn")
    public void setReceiverMsisdn(Object receiverMsisdn) {
        this.receiverMsisdn = receiverMsisdn;
    }

    @JsonProperty("commissionType")
    public Object getCommissionType() {
        return commissionType;
    }

    @JsonProperty("commissionType")
    public void setCommissionType(Object commissionType) {
        this.commissionType = commissionType;
    }

    @JsonProperty("commissionRate")
    public Object getCommissionRate() {
        return commissionRate;
    }

    @JsonProperty("commissionRate")
    public void setCommissionRate(Object commissionRate) {
        this.commissionRate = commissionRate;
    }

    @JsonProperty("commissionValue")
    public Object getCommissionValue() {
        return commissionValue;
    }

    @JsonProperty("commissionValue")
    public void setCommissionValue(Object commissionValue) {
        this.commissionValue = commissionValue;
    }

    @JsonProperty("transferAmt")
    public Object getTransferAmt() {
        return transferAmt;
    }

    @JsonProperty("transferAmt")
    public void setTransferAmt(Object transferAmt) {
        this.transferAmt = transferAmt;
    }

    @JsonProperty("marginAmount")
    public Object getMarginAmount() {
        return marginAmount;
    }

    @JsonProperty("marginAmount")
    public void setMarginAmount(Object marginAmount) {
        this.marginAmount = marginAmount;
    }

    @JsonProperty("marginRate")
    public Object getMarginRate() {
        return marginRate;
    }

    @JsonProperty("marginRate")
    public void setMarginRate(Object marginRate) {
        this.marginRate = marginRate;
    }

    @JsonProperty("otfType")
    public Object getOtfType() {
        return otfType;
    }

    @JsonProperty("otfType")
    public void setOtfType(Object otfType) {
        this.otfType = otfType;
    }

    @JsonProperty("parentCategory")
    public Object getParentCategory() {
        return parentCategory;
    }

    @JsonProperty("parentCategory")
    public void setParentCategory(Object parentCategory) {
        this.parentCategory = parentCategory;
    }

    @JsonProperty("paymentInstType")
    public Object getPaymentInstType() {
        return paymentInstType;
    }

    @JsonProperty("paymentInstType")
    public void setPaymentInstType(Object paymentInstType) {
        this.paymentInstType = paymentInstType;
    }

    @JsonProperty("domainName")
    public Object getDomainName() {
        return domainName;
    }

    @JsonProperty("domainName")
    public void setDomainName(Object domainName) {
        this.domainName = domainName;
    }

    @JsonProperty("ownerName")
    public Object getOwnerName() {
        return ownerName;
    }

    @JsonProperty("ownerName")
    public void setOwnerName(Object ownerName) {
        this.ownerName = ownerName;
    }

    @JsonProperty("transInCount")
    public Object getTransInCount() {
        return transInCount;
    }

    @JsonProperty("transInCount")
    public void setTransInCount(Object transInCount) {
        this.transInCount = transInCount;
    }

    @JsonProperty("transOutCount")
    public Object getTransOutCount() {
        return transOutCount;
    }

    @JsonProperty("transOutCount")
    public void setTransOutCount(Object transOutCount) {
        this.transOutCount = transOutCount;
    }

    @JsonProperty("transInAmount")
    public Object getTransInAmount() {
        return transInAmount;
    }

    @JsonProperty("transInAmount")
    public void setTransInAmount(Object transInAmount) {
        this.transInAmount = transInAmount;
    }

    @JsonProperty("fromEXTCODE")
    public Object getFromEXTCODE() {
        return fromEXTCODE;
    }

    @JsonProperty("fromEXTCODE")
    public void setFromEXTCODE(Object fromEXTCODE) {
        this.fromEXTCODE = fromEXTCODE;
    }

    @JsonProperty("toEXTCODE")
    public Object getToEXTCODE() {
        return toEXTCODE;
    }

    @JsonProperty("toEXTCODE")
    public void setToEXTCODE(Object toEXTCODE) {
        this.toEXTCODE = toEXTCODE;
    }

    @JsonProperty("ownerProfile")
    public Object getOwnerProfile() {
        return ownerProfile;
    }

    @JsonProperty("ownerProfile")
    public void setOwnerProfile(Object ownerProfile) {
        this.ownerProfile = ownerProfile;
    }

    @JsonProperty("parentProfile")
    public Object getParentProfile() {
        return parentProfile;
    }

    @JsonProperty("parentProfile")
    public void setParentProfile(Object parentProfile) {
        this.parentProfile = parentProfile;
    }

    @JsonProperty("externalTranDate")
    public Object getExternalTranDate() {
        return externalTranDate;
    }

    @JsonProperty("externalTranDate")
    public void setExternalTranDate(Object externalTranDate) {
        this.externalTranDate = externalTranDate;
    }

    @JsonProperty("commQtyAsString")
    public Object getCommQtyAsString() {
        return commQtyAsString;
    }

    @JsonProperty("commQtyAsString")
    public void setCommQtyAsString(Object commQtyAsString) {
        this.commQtyAsString = commQtyAsString;
    }

    @JsonProperty("senderDrQtyAsString")
    public Object getSenderDrQtyAsString() {
        return senderDrQtyAsString;
    }

    @JsonProperty("senderDrQtyAsString")
    public void setSenderDrQtyAsString(Object senderDrQtyAsString) {
        this.senderDrQtyAsString = senderDrQtyAsString;
    }

    @JsonProperty("payableAmounts")
    public Object getPayableAmounts() {
        return payableAmounts;
    }

    @JsonProperty("payableAmounts")
    public void setPayableAmounts(Object payableAmounts) {
        this.payableAmounts = payableAmounts;
    }

    @JsonProperty("netPayableAmounts")
    public Object getNetPayableAmounts() {
        return netPayableAmounts;
    }

    @JsonProperty("netPayableAmounts")
    public void setNetPayableAmounts(Object netPayableAmounts) {
        this.netPayableAmounts = netPayableAmounts;
    }

    @JsonProperty("approvedQuantity")
    public Object getApprovedQuantity() {
        return approvedQuantity;
    }

    @JsonProperty("approvedQuantity")
    public void setApprovedQuantity(Object approvedQuantity) {
        this.approvedQuantity = approvedQuantity;
    }

    @JsonProperty("userID")
    public Object getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(Object userID) {
        this.userID = userID;
    }

    @JsonProperty("loginID")
    public Object getLoginID() {
        return loginID;
    }

    @JsonProperty("loginID")
    public void setLoginID(Object loginID) {
        this.loginID = loginID;
    }

    @JsonProperty("serviceTypeName")
    public Object getServiceTypeName() {
        return serviceTypeName;
    }

    @JsonProperty("serviceTypeName")
    public void setServiceTypeName(Object serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    @JsonProperty("selectorName")
    public Object getSelectorName() {
        return selectorName;
    }

    @JsonProperty("selectorName")
    public void setSelectorName(Object selectorName) {
        this.selectorName = selectorName;
    }

    @JsonProperty("differentialAmount")
    public Object getDifferentialAmount() {
        return differentialAmount;
    }

    @JsonProperty("differentialAmount")
    public void setDifferentialAmount(Object differentialAmount) {
        this.differentialAmount = differentialAmount;
    }

    @JsonProperty("transactionCount")
    public Object getTransactionCount() {
        return transactionCount;
    }

    @JsonProperty("transactionCount")
    public void setTransactionCount(Object transactionCount) {
        this.transactionCount = transactionCount;
    }

    @JsonProperty("o2cTransferInCount")
    public Object getO2cTransferInCount() {
        return o2cTransferInCount;
    }

    @JsonProperty("o2cTransferInCount")
    public void setO2cTransferInCount(Object o2cTransferInCount) {
        this.o2cTransferInCount = o2cTransferInCount;
    }

    @JsonProperty("o2cTransferInAmount")
    public Object getO2cTransferInAmount() {
        return o2cTransferInAmount;
    }

    @JsonProperty("o2cTransferInAmount")
    public void setO2cTransferInAmount(Object o2cTransferInAmount) {
        this.o2cTransferInAmount = o2cTransferInAmount;
    }

    @JsonProperty("c2cTransferInCount")
    public Object getC2cTransferInCount() {
        return c2cTransferInCount;
    }

    @JsonProperty("c2cTransferInCount")
    public void setC2cTransferInCount(Object c2cTransferInCount) {
        this.c2cTransferInCount = c2cTransferInCount;
    }

    @JsonProperty("c2cTransferInAmount")
    public Object getC2cTransferInAmount() {
        return c2cTransferInAmount;
    }

    @JsonProperty("c2cTransferInAmount")
    public void setC2cTransferInAmount(Object c2cTransferInAmount) {
        this.c2cTransferInAmount = c2cTransferInAmount;
    }

    @JsonProperty("c2cReturnPlusWithCount")
    public Object getC2cReturnPlusWithCount() {
        return c2cReturnPlusWithCount;
    }

    @JsonProperty("c2cReturnPlusWithCount")
    public void setC2cReturnPlusWithCount(Object c2cReturnPlusWithCount) {
        this.c2cReturnPlusWithCount = c2cReturnPlusWithCount;
    }

    @JsonProperty("c2cReturnPlusWithINAmount")
    public Object getC2cReturnPlusWithINAmount() {
        return c2cReturnPlusWithINAmount;
    }

    @JsonProperty("c2cReturnPlusWithINAmount")
    public void setC2cReturnPlusWithINAmount(Object c2cReturnPlusWithINAmount) {
        this.c2cReturnPlusWithINAmount = c2cReturnPlusWithINAmount;
    }

    @JsonProperty("o2cReturnPlusWithoutCount")
    public Object getO2cReturnPlusWithoutCount() {
        return o2cReturnPlusWithoutCount;
    }

    @JsonProperty("o2cReturnPlusWithoutCount")
    public void setO2cReturnPlusWithoutCount(Object o2cReturnPlusWithoutCount) {
        this.o2cReturnPlusWithoutCount = o2cReturnPlusWithoutCount;
    }

    @JsonProperty("o2cReturnPlusWithoutAmount")
    public Object getO2cReturnPlusWithoutAmount() {
        return o2cReturnPlusWithoutAmount;
    }

    @JsonProperty("o2cReturnPlusWithoutAmount")
    public void setO2cReturnPlusWithoutAmount(Object o2cReturnPlusWithoutAmount) {
        this.o2cReturnPlusWithoutAmount = o2cReturnPlusWithoutAmount;
    }

    @JsonProperty("c2cTransferOutCount")
    public Object getC2cTransferOutCount() {
        return c2cTransferOutCount;
    }

    @JsonProperty("c2cTransferOutCount")
    public void setC2cTransferOutCount(Object c2cTransferOutCount) {
        this.c2cTransferOutCount = c2cTransferOutCount;
    }

    @JsonProperty("c2cTransferOutAmount")
    public Object getC2cTransferOutAmount() {
        return c2cTransferOutAmount;
    }

    @JsonProperty("c2cTransferOutAmount")
    public void setC2cTransferOutAmount(Object c2cTransferOutAmount) {
        this.c2cTransferOutAmount = c2cTransferOutAmount;
    }

    @JsonProperty("c2cReturnWithOutCount")
    public Object getC2cReturnWithOutCount() {
        return c2cReturnWithOutCount;
    }

    @JsonProperty("c2cReturnWithOutCount")
    public void setC2cReturnWithOutCount(Object c2cReturnWithOutCount) {
        this.c2cReturnWithOutCount = c2cReturnWithOutCount;
    }

    @JsonProperty("c2cReturnWithOutAmount")
    public Object getC2cReturnWithOutAmount() {
        return c2cReturnWithOutAmount;
    }

    @JsonProperty("c2cReturnWithOutAmount")
    public void setC2cReturnWithOutAmount(Object c2cReturnWithOutAmount) {
        this.c2cReturnWithOutAmount = c2cReturnWithOutAmount;
    }

    @JsonProperty("c2sTransferOutCount")
    public Object getC2sTransferOutCount() {
        return c2sTransferOutCount;
    }

    @JsonProperty("c2sTransferOutCount")
    public void setC2sTransferOutCount(Object c2sTransferOutCount) {
        this.c2sTransferOutCount = c2sTransferOutCount;
    }

    @JsonProperty("c2sTransferAmount")
    public Object getC2sTransferAmount() {
        return c2sTransferAmount;
    }

    @JsonProperty("c2sTransferAmount")
    public void setC2sTransferAmount(Object c2sTransferAmount) {
        this.c2sTransferAmount = c2sTransferAmount;
    }

    @JsonProperty("unitValue")
    public Long getUnitValue() {
        return unitValue;
    }

    @JsonProperty("unitValue")
    public void setUnitValue(Long unitValue) {
        this.unitValue = unitValue;
    }

    @JsonProperty("requiredQuantity")
    public Long getRequiredQuantity() {
        return requiredQuantity;
    }

    @JsonProperty("requiredQuantity")
    public void setRequiredQuantity(Long requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    @JsonProperty("dualCommissionType")
    public String getDualCommissionType() {
        return dualCommissionType;
    }

    @JsonProperty("dualCommissionType")
    public void setDualCommissionType(String dualCommissionType) {
        this.dualCommissionType = dualCommissionType;
    }

    @JsonProperty("profileNames")
    public Object getProfileNames() {
        return profileNames;
    }

    @JsonProperty("profileNames")
    public void setProfileNames(Object profileNames) {
        this.profileNames = profileNames;
    }

    @JsonProperty("curStatus")
    public Object getCurStatus() {
        return curStatus;
    }

    @JsonProperty("curStatus")
    public void setCurStatus(Object curStatus) {
        this.curStatus = curStatus;
    }

    @JsonProperty("apprRejStatus")
    public Object getApprRejStatus() {
        return apprRejStatus;
    }

    @JsonProperty("apprRejStatus")
    public void setApprRejStatus(Object apprRejStatus) {
        this.apprRejStatus = apprRejStatus;
    }

    @JsonProperty("batch_no")
    public Object getBatchNo() {
        return batchNo;
    }

    @JsonProperty("batch_no")
    public void setBatchNo(Object batchNo) {
        this.batchNo = batchNo;
    }

    @JsonProperty("product_name")
    public Object getProductName() {
        return productName;
    }

    @JsonProperty("product_name")
    public void setProductName(Object productName) {
        this.productName = productName;
    }

    @JsonProperty("batch_type")
    public Object getBatchType() {
        return batchType;
    }

    @JsonProperty("batch_type")
    public void setBatchType(Object batchType) {
        this.batchType = batchType;
    }

    @JsonProperty("from_serial_no")
    public Object getFromSerialNo() {
        return fromSerialNo;
    }

    @JsonProperty("from_serial_no")
    public void setFromSerialNo(Object fromSerialNo) {
        this.fromSerialNo = fromSerialNo;
    }

    @JsonProperty("to_serial_no")
    public Object getToSerialNo() {
        return toSerialNo;
    }

    @JsonProperty("to_serial_no")
    public void setToSerialNo(Object toSerialNo) {
        this.toSerialNo = toSerialNo;
    }

    @JsonProperty("total_no_of_vouchers")
    public Long getTotalNoOfVouchers() {
        return totalNoOfVouchers;
    }

    @JsonProperty("total_no_of_vouchers")
    public void setTotalNoOfVouchers(Long totalNoOfVouchers) {
        this.totalNoOfVouchers = totalNoOfVouchers;
    }

    @JsonProperty("userOTFCountsVO")
    public Object getUserOTFCountsVO() {
        return userOTFCountsVO;
    }

    @JsonProperty("userOTFCountsVO")
    public void setUserOTFCountsVO(Object userOTFCountsVO) {
        this.userOTFCountsVO = userOTFCountsVO;
    }

    @JsonProperty("transferSubTypeAsString")
    public String getTransferSubTypeAsString() {
        return transferSubTypeAsString;
    }

    @JsonProperty("transferSubTypeAsString")
    public void setTransferSubTypeAsString(String transferSubTypeAsString) {
        this.transferSubTypeAsString = transferSubTypeAsString;
    }

    @JsonProperty("fromMsisdn")
    public Object getFromMsisdn() {
        return fromMsisdn;
    }

    @JsonProperty("fromMsisdn")
    public void setFromMsisdn(Object fromMsisdn) {
        this.fromMsisdn = fromMsisdn;
    }

    @JsonProperty("toMsisdn")
    public Object getToMsisdn() {
        return toMsisdn;
    }

    @JsonProperty("toMsisdn")
    public void setToMsisdn(Object toMsisdn) {
        this.toMsisdn = toMsisdn;
    }

    @JsonProperty("toPrimaryMSISDN")
    public Object getToPrimaryMSISDN() {
        return toPrimaryMSISDN;
    }

    @JsonProperty("toPrimaryMSISDN")
    public void setToPrimaryMSISDN(Object toPrimaryMSISDN) {
        this.toPrimaryMSISDN = toPrimaryMSISDN;
    }

    @JsonProperty("fromCategoryDesc")
    public Object getFromCategoryDesc() {
        return fromCategoryDesc;
    }

    @JsonProperty("fromCategoryDesc")
    public void setFromCategoryDesc(Object fromCategoryDesc) {
        this.fromCategoryDesc = fromCategoryDesc;
    }

    @JsonProperty("toCategoryDesc")
    public Object getToCategoryDesc() {
        return toCategoryDesc;
    }

    @JsonProperty("toCategoryDesc")
    public void setToCategoryDesc(Object toCategoryDesc) {
        this.toCategoryDesc = toCategoryDesc;
    }

    @JsonProperty("fromGradeCodeDesc")
    public Object getFromGradeCodeDesc() {
        return fromGradeCodeDesc;
    }

    @JsonProperty("fromGradeCodeDesc")
    public void setFromGradeCodeDesc(Object fromGradeCodeDesc) {
        this.fromGradeCodeDesc = fromGradeCodeDesc;
    }

    @JsonProperty("toGradeCodeDesc")
    public Object getToGradeCodeDesc() {
        return toGradeCodeDesc;
    }

    @JsonProperty("toGradeCodeDesc")
    public void setToGradeCodeDesc(Object toGradeCodeDesc) {
        this.toGradeCodeDesc = toGradeCodeDesc;
    }

    @JsonProperty("fromCommissionProfileIDDesc")
    public Object getFromCommissionProfileIDDesc() {
        return fromCommissionProfileIDDesc;
    }

    @JsonProperty("fromCommissionProfileIDDesc")
    public void setFromCommissionProfileIDDesc(Object fromCommissionProfileIDDesc) {
        this.fromCommissionProfileIDDesc = fromCommissionProfileIDDesc;
    }

    @JsonProperty("toCommissionProfileIDDesc")
    public Object getToCommissionProfileIDDesc() {
        return toCommissionProfileIDDesc;
    }

    @JsonProperty("toCommissionProfileIDDesc")
    public void setToCommissionProfileIDDesc(Object toCommissionProfileIDDesc) {
        this.toCommissionProfileIDDesc = toCommissionProfileIDDesc;
    }

    @JsonProperty("toTxnProfileDesc")
    public Object getToTxnProfileDesc() {
        return toTxnProfileDesc;
    }

    @JsonProperty("toTxnProfileDesc")
    public void setToTxnProfileDesc(Object toTxnProfileDesc) {
        this.toTxnProfileDesc = toTxnProfileDesc;
    }

    @JsonProperty("fromTxnProfileDesc")
    public Object getFromTxnProfileDesc() {
        return fromTxnProfileDesc;
    }

    @JsonProperty("fromTxnProfileDesc")
    public void setFromTxnProfileDesc(Object fromTxnProfileDesc) {
        this.fromTxnProfileDesc = fromTxnProfileDesc;
    }

    @JsonProperty("segment")
    public Object getSegment() {
        return segment;
    }

    @JsonProperty("segment")
    public void setSegment(Object segment) {
        this.segment = segment;
    }

    @JsonProperty("firstApprovedOnAsString")
    public Object getFirstApprovedOnAsString() {
        return firstApprovedOnAsString;
    }

    @JsonProperty("firstApprovedOnAsString")
    public void setFirstApprovedOnAsString(Object firstApprovedOnAsString) {
        this.firstApprovedOnAsString = firstApprovedOnAsString;
    }

    @JsonProperty("secondApprovedOnAsString")
    public Object getSecondApprovedOnAsString() {
        return secondApprovedOnAsString;
    }

    @JsonProperty("secondApprovedOnAsString")
    public void setSecondApprovedOnAsString(Object secondApprovedOnAsString) {
        this.secondApprovedOnAsString = secondApprovedOnAsString;
    }

    @JsonProperty("isFileC2C")
    public String getIsFileC2C() {
        return isFileC2C;
    }

    @JsonProperty("isFileC2C")
    public void setIsFileC2C(String isFileC2C) {
        this.isFileC2C = isFileC2C;
    }

    @JsonProperty("uploadedFile")
    public Object getUploadedFile() {
        return uploadedFile;
    }

    @JsonProperty("uploadedFile")
    public void setUploadedFile(Object uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    @JsonProperty("uploadedFilePath")
    public Object getUploadedFilePath() {
        return uploadedFilePath;
    }

    @JsonProperty("uploadedFilePath")
    public void setUploadedFilePath(Object uploadedFilePath) {
        this.uploadedFilePath = uploadedFilePath;
    }

    @JsonProperty("uploadedFileName")
    public Object getUploadedFileName() {
        return uploadedFileName;
    }

    @JsonProperty("uploadedFileName")
    public void setUploadedFileName(Object uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }

    @JsonProperty("channelTransferList")
    public Object getChannelTransferList() {
        return channelTransferList;
    }

    @JsonProperty("channelTransferList")
    public void setChannelTransferList(Object channelTransferList) {
        this.channelTransferList = channelTransferList;
    }

    @JsonProperty("voucherTypeList")
    public Object getVoucherTypeList() {
        return voucherTypeList;
    }

    @JsonProperty("voucherTypeList")
    public void setVoucherTypeList(Object voucherTypeList) {
        this.voucherTypeList = voucherTypeList;
    }

    @JsonProperty("slabslist")
    public Object getSlabslist() {
        return slabslist;
    }

    @JsonProperty("slabslist")
    public void setSlabslist(Object slabslist) {
        this.slabslist = slabslist;
    }

    @JsonProperty("approvalFile")
    public Object getApprovalFile() {
        return approvalFile;
    }

    @JsonProperty("approvalFile")
    public void setApprovalFile(Object approvalFile) {
        this.approvalFile = approvalFile;
    }

    @JsonProperty("bundleType")
    public Boolean getBundleType() {
        return bundleType;
    }

    @JsonProperty("bundleType")
    public void setBundleType(Boolean bundleType) {
        this.bundleType = bundleType;
    }

    @JsonProperty("reconciliationFlag")
    public Boolean getReconciliationFlag() {
        return reconciliationFlag;
    }

    @JsonProperty("reconciliationFlag")
    public void setReconciliationFlag(Boolean reconciliationFlag) {
        this.reconciliationFlag = reconciliationFlag;
    }

    @JsonProperty("previousStatus")
    public Object getPreviousStatus() {
        return previousStatus;
    }

    @JsonProperty("previousStatus")
    public void setPreviousStatus(Object previousStatus) {
        this.previousStatus = previousStatus;
    }

    @JsonProperty("tax3ValueAsString")
    public Object getTax3ValueAsString() {
        return tax3ValueAsString;
    }

    @JsonProperty("tax3ValueAsString")
    public void setTax3ValueAsString(Object tax3ValueAsString) {
        this.tax3ValueAsString = tax3ValueAsString;
    }

    @JsonProperty("transOutAmount")
    public Object getTransOutAmount() {
        return transOutAmount;
    }

    @JsonProperty("transOutAmount")
    public void setTransOutAmount(Object transOutAmount) {
        this.transOutAmount = transOutAmount;
    }

    @JsonProperty("commProfileDetailID")
    public Object getCommProfileDetailID() {
        return commProfileDetailID;
    }

    @JsonProperty("commProfileDetailID")
    public void setCommProfileDetailID(Object commProfileDetailID) {
        this.commProfileDetailID = commProfileDetailID;
    }

    @JsonProperty("sosStatus")
    public Object getSosStatus() {
        return sosStatus;
    }

    @JsonProperty("sosStatus")
    public void setSosStatus(Object sosStatus) {
        this.sosStatus = sosStatus;
    }

    @JsonProperty("sosTxnId")
    public Object getSosTxnId() {
        return sosTxnId;
    }

    @JsonProperty("sosTxnId")
    public void setSosTxnId(Object sosTxnId) {
        this.sosTxnId = sosTxnId;
    }

    @JsonProperty("sosProductCode")
    public Object getSosProductCode() {
        return sosProductCode;
    }

    @JsonProperty("sosProductCode")
    public void setSosProductCode(Object sosProductCode) {
        this.sosProductCode = sosProductCode;
    }

    @JsonProperty("transactionCode")
    public Object getTransactionCode() {
        return transactionCode;
    }

    @JsonProperty("transactionCode")
    public void setTransactionCode(Object transactionCode) {
        this.transactionCode = transactionCode;
    }

    @JsonProperty("info1")
    public Object getInfo1() {
        return info1;
    }

    @JsonProperty("info1")
    public void setInfo1(Object info1) {
        this.info1 = info1;
    }

    @JsonProperty("info2")
    public Object getInfo2() {
        return info2;
    }

    @JsonProperty("info2")
    public void setInfo2(Object info2) {
        this.info2 = info2;
    }

    @JsonProperty("info3")
    public Object getInfo3() {
        return info3;
    }

    @JsonProperty("info3")
    public void setInfo3(Object info3) {
        this.info3 = info3;
    }

    @JsonProperty("info4")
    public Object getInfo4() {
        return info4;
    }

    @JsonProperty("info4")
    public void setInfo4(Object info4) {
        this.info4 = info4;
    }

    @JsonProperty("info5")
    public Object getInfo5() {
        return info5;
    }

    @JsonProperty("info5")
    public void setInfo5(Object info5) {
        this.info5 = info5;
    }

    @JsonProperty("info6")
    public Object getInfo6() {
        return info6;
    }

    @JsonProperty("info6")
    public void setInfo6(Object info6) {
        this.info6 = info6;
    }

    @JsonProperty("info7")
    public Object getInfo7() {
        return info7;
    }

    @JsonProperty("info7")
    public void setInfo7(Object info7) {
        this.info7 = info7;
    }

    @JsonProperty("info8")
    public Object getInfo8() {
        return info8;
    }

    @JsonProperty("info8")
    public void setInfo8(Object info8) {
        this.info8 = info8;
    }

    @JsonProperty("info9")
    public Object getInfo9() {
        return info9;
    }

    @JsonProperty("info9")
    public void setInfo9(Object info9) {
        this.info9 = info9;
    }

    @JsonProperty("info10")
    public Object getInfo10() {
        return info10;
    }

    @JsonProperty("info10")
    public void setInfo10(Object info10) {
        this.info10 = info10;
    }

    @JsonProperty("otfFlag")
    public Boolean getOtfFlag() {
        return otfFlag;
    }

    @JsonProperty("otfFlag")
    public void setOtfFlag(Boolean otfFlag) {
        this.otfFlag = otfFlag;
    }

    @JsonProperty("otfTypePctOrAMt")
    public Object getOtfTypePctOrAMt() {
        return otfTypePctOrAMt;
    }

    @JsonProperty("otfTypePctOrAMt")
    public void setOtfTypePctOrAMt(Object otfTypePctOrAMt) {
        this.otfTypePctOrAMt = otfTypePctOrAMt;
    }

    @JsonProperty("otfRate")
    public Long getOtfRate() {
        return otfRate;
    }

    @JsonProperty("otfRate")
    public void setOtfRate(Long otfRate) {
        this.otfRate = otfRate;
    }

    @JsonProperty("otfAmount")
    public Long getOtfAmount() {
        return otfAmount;
    }

    @JsonProperty("otfAmount")
    public void setOtfAmount(Long otfAmount) {
        this.otfAmount = otfAmount;
    }

    @JsonProperty("channelSoSVOList")
    public Object getChannelSoSVOList() {
        return channelSoSVOList;
    }

    @JsonProperty("channelSoSVOList")
    public void setChannelSoSVOList(Object channelSoSVOList) {
        this.channelSoSVOList = channelSoSVOList;
    }

    @JsonProperty("sosSettlementDate")
    public Object getSosSettlementDate() {
        return sosSettlementDate;
    }

    @JsonProperty("sosSettlementDate")
    public void setSosSettlementDate(Object sosSettlementDate) {
        this.sosSettlementDate = sosSettlementDate;
    }

    @JsonProperty("grandName")
    public Object getGrandName() {
        return grandName;
    }

    @JsonProperty("grandName")
    public void setGrandName(Object grandName) {
        this.grandName = grandName;
    }

    @JsonProperty("grandGeo")
    public Object getGrandGeo() {
        return grandGeo;
    }

    @JsonProperty("grandGeo")
    public void setGrandGeo(Object grandGeo) {
        this.grandGeo = grandGeo;
    }

    @JsonProperty("openingBalance")
    public Object getOpeningBalance() {
        return openingBalance;
    }

    @JsonProperty("openingBalance")
    public void setOpeningBalance(Object openingBalance) {
        this.openingBalance = openingBalance;
    }

    @JsonProperty("stockBought")
    public Object getStockBought() {
        return stockBought;
    }

    @JsonProperty("stockBought")
    public void setStockBought(Object stockBought) {
        this.stockBought = stockBought;
    }

    @JsonProperty("stockReturn")
    public Object getStockReturn() {
        return stockReturn;
    }

    @JsonProperty("stockReturn")
    public void setStockReturn(Object stockReturn) {
        this.stockReturn = stockReturn;
    }

    @JsonProperty("channelReturn")
    public Object getChannelReturn() {
        return channelReturn;
    }

    @JsonProperty("channelReturn")
    public void setChannelReturn(Object channelReturn) {
        this.channelReturn = channelReturn;
    }

    @JsonProperty("c2sTransfers")
    public Object getC2sTransfers() {
        return c2sTransfers;
    }

    @JsonProperty("c2sTransfers")
    public void setC2sTransfers(Object c2sTransfers) {
        this.c2sTransfers = c2sTransfers;
    }

    @JsonProperty("closingBalance")
    public Object getClosingBalance() {
        return closingBalance;
    }

    @JsonProperty("closingBalance")
    public void setClosingBalance(Object closingBalance) {
        this.closingBalance = closingBalance;
    }

    @JsonProperty("reconStatus")
    public Object getReconStatus() {
        return reconStatus;
    }

    @JsonProperty("reconStatus")
    public void setReconStatus(Object reconStatus) {
        this.reconStatus = reconStatus;
    }

    @JsonProperty("netBalance")
    public Object getNetBalance() {
        return netBalance;
    }

    @JsonProperty("netBalance")
    public void setNetBalance(Object netBalance) {
        this.netBalance = netBalance;
    }

    @JsonProperty("netLifting")
    public Object getNetLifting() {
        return netLifting;
    }

    @JsonProperty("netLifting")
    public void setNetLifting(Object netLifting) {
        this.netLifting = netLifting;
    }

    @JsonProperty("messageArgumentList")
    public Object getMessageArgumentList() {
        return messageArgumentList;
    }

    @JsonProperty("messageArgumentList")
    public void setMessageArgumentList(Object messageArgumentList) {
        this.messageArgumentList = messageArgumentList;
    }

    @JsonProperty("toUserMsisdn")
    public Object getToUserMsisdn() {
        return toUserMsisdn;
    }

    @JsonProperty("toUserMsisdn")
    public void setToUserMsisdn(Object toUserMsisdn) {
        this.toUserMsisdn = toUserMsisdn;
    }

    @JsonProperty("channelRemarks")
    public Object getChannelRemarks() {
        return channelRemarks;
    }

    @JsonProperty("channelRemarks")
    public void setChannelRemarks(Object channelRemarks) {
        this.channelRemarks = channelRemarks;
    }

    @JsonProperty("transferInitatedBy")
    public Object getTransferInitatedBy() {
        return transferInitatedBy;
    }

    @JsonProperty("transferInitatedBy")
    public void setTransferInitatedBy(Object transferInitatedBy) {
        this.transferInitatedBy = transferInitatedBy;
    }

    @JsonProperty("controlTransfer")
    public Object getControlTransfer() {
        return controlTransfer;
    }

    @JsonProperty("controlTransfer")
    public void setControlTransfer(Object controlTransfer) {
        this.controlTransfer = controlTransfer;
    }

    @JsonProperty("graphicalDomainCode")
    public Object getGraphicalDomainCode() {
        return graphicalDomainCode;
    }

    @JsonProperty("graphicalDomainCode")
    public void setGraphicalDomainCode(Object graphicalDomainCode) {
        this.graphicalDomainCode = graphicalDomainCode;
    }

    @JsonProperty("receiverCategoryCode")
    public Object getReceiverCategoryCode() {
        return receiverCategoryCode;
    }

    @JsonProperty("receiverCategoryCode")
    public void setReceiverCategoryCode(Object receiverCategoryCode) {
        this.receiverCategoryCode = receiverCategoryCode;
    }

    @JsonProperty("senderTxnProfile")
    public Object getSenderTxnProfile() {
        return senderTxnProfile;
    }

    @JsonProperty("senderTxnProfile")
    public void setSenderTxnProfile(Object senderTxnProfile) {
        this.senderTxnProfile = senderTxnProfile;
    }

    @JsonProperty("receiverTxnProfile")
    public String getReceiverTxnProfile() {
        return receiverTxnProfile;
    }

    @JsonProperty("receiverTxnProfile")
    public void setReceiverTxnProfile(String receiverTxnProfile) {
        this.receiverTxnProfile = receiverTxnProfile;
    }

    @JsonProperty("payInstrumentAmt")
    public Long getPayInstrumentAmt() {
        return payInstrumentAmt;
    }

    @JsonProperty("payInstrumentAmt")
    public void setPayInstrumentAmt(Long payInstrumentAmt) {
        this.payInstrumentAmt = payInstrumentAmt;
    }

    @JsonProperty("commQty")
    public Long getCommQty() {
        return commQty;
    }

    @JsonProperty("commQty")
    public void setCommQty(Long commQty) {
        this.commQty = commQty;
    }

    @JsonProperty("firstApproverLimit")
    public Long getFirstApproverLimit() {
        return firstApproverLimit;
    }

    @JsonProperty("firstApproverLimit")
    public void setFirstApproverLimit(Long firstApproverLimit) {
        this.firstApproverLimit = firstApproverLimit;
    }

    @JsonProperty("payInstrumentDate")
    public Object getPayInstrumentDate() {
        return payInstrumentDate;
    }

    @JsonProperty("payInstrumentDate")
    public void setPayInstrumentDate(Object payInstrumentDate) {
        this.payInstrumentDate = payInstrumentDate;
    }

    @JsonProperty("payInstrumentType")
    public String getPayInstrumentType() {
        return payInstrumentType;
    }

    @JsonProperty("payInstrumentType")
    public void setPayInstrumentType(String payInstrumentType) {
        this.payInstrumentType = payInstrumentType;
    }

    @JsonProperty("payInstrumentNum")
    public Object getPayInstrumentNum() {
        return payInstrumentNum;
    }

    @JsonProperty("payInstrumentNum")
    public void setPayInstrumentNum(Object payInstrumentNum) {
        this.payInstrumentNum = payInstrumentNum;
    }

    @JsonProperty("commProfileVersion")
    public String getCommProfileVersion() {
        return commProfileVersion;
    }

    @JsonProperty("commProfileVersion")
    public void setCommProfileVersion(String commProfileVersion) {
        this.commProfileVersion = commProfileVersion;
    }

    @JsonProperty("referenceNum")
    public Object getReferenceNum() {
        return referenceNum;
    }

    @JsonProperty("referenceNum")
    public void setReferenceNum(Object referenceNum) {
        this.referenceNum = referenceNum;
    }

    @JsonProperty("netPayableAmountAsStr")
    public Object getNetPayableAmountAsStr() {
        return netPayableAmountAsStr;
    }

    @JsonProperty("netPayableAmountAsStr")
    public void setNetPayableAmountAsStr(Object netPayableAmountAsStr) {
        this.netPayableAmountAsStr = netPayableAmountAsStr;
    }

    @JsonProperty("payableAmountAsStr")
    public Object getPayableAmountAsStr() {
        return payableAmountAsStr;
    }

    @JsonProperty("payableAmountAsStr")
    public void setPayableAmountAsStr(Object payableAmountAsStr) {
        this.payableAmountAsStr = payableAmountAsStr;
    }

    @JsonProperty("web")
    public Boolean getWeb() {
        return web;
    }

    @JsonProperty("web")
    public void setWeb(Boolean web) {
        this.web = web;
    }

    @JsonProperty("reversalFlag")
    public Boolean getReversalFlag() {
        return reversalFlag;
    }

    @JsonProperty("reversalFlag")
    public void setReversalFlag(Boolean reversalFlag) {
        this.reversalFlag = reversalFlag;
    }

    @JsonProperty("erpNum")
    public Object getErpNum() {
        return erpNum;
    }

    @JsonProperty("erpNum")
    public void setErpNum(Object erpNum) {
        this.erpNum = erpNum;
    }

    @JsonProperty("canceledBy")
    public Object getCanceledBy() {
        return canceledBy;
    }

    @JsonProperty("canceledBy")
    public void setCanceledBy(Object canceledBy) {
        this.canceledBy = canceledBy;
    }

    @JsonProperty("canceledOn")
    public Object getCanceledOn() {
        return canceledOn;
    }

    @JsonProperty("canceledOn")
    public void setCanceledOn(Object canceledOn) {
        this.canceledOn = canceledOn;
    }

    @JsonProperty("externalTxnDateAsString")
    public Object getExternalTxnDateAsString() {
        return externalTxnDateAsString;
    }

    @JsonProperty("externalTxnDateAsString")
    public void setExternalTxnDateAsString(Object externalTxnDateAsString) {
        this.externalTxnDateAsString = externalTxnDateAsString;
    }

    @JsonProperty("firstApprovalRemark")
    public Object getFirstApprovalRemark() {
        return firstApprovalRemark;
    }

    @JsonProperty("firstApprovalRemark")
    public void setFirstApprovalRemark(Object firstApprovalRemark) {
        this.firstApprovalRemark = firstApprovalRemark;
    }

    @JsonProperty("batchNum")
    public Object getBatchNum() {
        return batchNum;
    }

    @JsonProperty("batchNum")
    public void setBatchNum(Object batchNum) {
        this.batchNum = batchNum;
    }

    @JsonProperty("requestedQuantityAsString")
    public String getRequestedQuantityAsString() {
        return requestedQuantityAsString;
    }

    @JsonProperty("requestedQuantityAsString")
    public void setRequestedQuantityAsString(String requestedQuantityAsString) {
        this.requestedQuantityAsString = requestedQuantityAsString;
    }

    @JsonProperty("secondApprovalRemark")
    public Object getSecondApprovalRemark() {
        return secondApprovalRemark;
    }

    @JsonProperty("secondApprovalRemark")
    public void setSecondApprovalRemark(Object secondApprovalRemark) {
        this.secondApprovalRemark = secondApprovalRemark;
    }

    @JsonProperty("thirdApprovalRemark")
    public Object getThirdApprovalRemark() {
        return thirdApprovalRemark;
    }

    @JsonProperty("thirdApprovalRemark")
    public void setThirdApprovalRemark(Object thirdApprovalRemark) {
        this.thirdApprovalRemark = thirdApprovalRemark;
    }

    @JsonProperty("paymentInstSource")
    public Object getPaymentInstSource() {
        return paymentInstSource;
    }

    @JsonProperty("paymentInstSource")
    public void setPaymentInstSource(Object paymentInstSource) {
        this.paymentInstSource = paymentInstSource;
    }

    @JsonProperty("canceledByApprovedName")
    public Object getCanceledByApprovedName() {
        return canceledByApprovedName;
    }

    @JsonProperty("canceledByApprovedName")
    public void setCanceledByApprovedName(Object canceledByApprovedName) {
        this.canceledByApprovedName = canceledByApprovedName;
    }

    @JsonProperty("commProfileName")
    public Object getCommProfileName() {
        return commProfileName;
    }

    @JsonProperty("commProfileName")
    public void setCommProfileName(Object commProfileName) {
        this.commProfileName = commProfileName;
    }

    @JsonProperty("firstApprovedByName")
    public Object getFirstApprovedByName() {
        return firstApprovedByName;
    }

    @JsonProperty("firstApprovedByName")
    public void setFirstApprovedByName(Object firstApprovedByName) {
        this.firstApprovedByName = firstApprovedByName;
    }

    @JsonProperty("receiverTxnProfileName")
    public Object getReceiverTxnProfileName() {
        return receiverTxnProfileName;
    }

    @JsonProperty("receiverTxnProfileName")
    public void setReceiverTxnProfileName(Object receiverTxnProfileName) {
        this.receiverTxnProfileName = receiverTxnProfileName;
    }

    @JsonProperty("secondApprovedByName")
    public Object getSecondApprovedByName() {
        return secondApprovedByName;
    }

    @JsonProperty("secondApprovedByName")
    public void setSecondApprovedByName(Object secondApprovedByName) {
        this.secondApprovedByName = secondApprovedByName;
    }

    @JsonProperty("receiverGradeCodeDesc")
    public Object getReceiverGradeCodeDesc() {
        return receiverGradeCodeDesc;
    }

    @JsonProperty("receiverGradeCodeDesc")
    public void setReceiverGradeCodeDesc(Object receiverGradeCodeDesc) {
        this.receiverGradeCodeDesc = receiverGradeCodeDesc;
    }

    @JsonProperty("thirdApprovedByName")
    public Object getThirdApprovedByName() {
        return thirdApprovedByName;
    }

    @JsonProperty("thirdApprovedByName")
    public void setThirdApprovedByName(Object thirdApprovedByName) {
        this.thirdApprovedByName = thirdApprovedByName;
    }

    @JsonProperty("transferInitatedByName")
    public String getTransferInitatedByName() {
        return transferInitatedByName;
    }

    @JsonProperty("transferInitatedByName")
    public void setTransferInitatedByName(String transferInitatedByName) {
        this.transferInitatedByName = transferInitatedByName;
    }

    @JsonProperty("transferMRPAsString")
    public String getTransferMRPAsString() {
        return transferMRPAsString;
    }

    @JsonProperty("transferMRPAsString")
    public void setTransferMRPAsString(String transferMRPAsString) {
        this.transferMRPAsString = transferMRPAsString;
    }

    @JsonProperty("finalApprovedBy")
    public Object getFinalApprovedBy() {
        return finalApprovedBy;
    }

    @JsonProperty("finalApprovedBy")
    public void setFinalApprovedBy(Object finalApprovedBy) {
        this.finalApprovedBy = finalApprovedBy;
    }

    @JsonProperty("finalApprovedDateAsString")
    public Object getFinalApprovedDateAsString() {
        return finalApprovedDateAsString;
    }

    @JsonProperty("finalApprovedDateAsString")
    public void setFinalApprovedDateAsString(Object finalApprovedDateAsString) {
        this.finalApprovedDateAsString = finalApprovedDateAsString;
    }

    @JsonProperty("senderTxnProfileName")
    public Object getSenderTxnProfileName() {
        return senderTxnProfileName;
    }

    @JsonProperty("senderTxnProfileName")
    public void setSenderTxnProfileName(Object senderTxnProfileName) {
        this.senderTxnProfileName = senderTxnProfileName;
    }

    @JsonProperty("transferSubTypeValue")
    public Object getTransferSubTypeValue() {
        return transferSubTypeValue;
    }

    @JsonProperty("transferSubTypeValue")
    public void setTransferSubTypeValue(Object transferSubTypeValue) {
        this.transferSubTypeValue = transferSubTypeValue;
    }

    @JsonProperty("dbdateTime")
    public Object getDbdateTime() {
        return dbdateTime;
    }

    @JsonProperty("dbdateTime")
    public void setDbdateTime(Object dbdateTime) {
        this.dbdateTime = dbdateTime;
    }

    @JsonProperty("commisionTxnId")
    public Object getCommisionTxnId() {
        return commisionTxnId;
    }

    @JsonProperty("commisionTxnId")
    public void setCommisionTxnId(Object commisionTxnId) {
        this.commisionTxnId = commisionTxnId;
    }

    @JsonProperty("levelOneApprovedQuantity")
    public Object getLevelOneApprovedQuantity() {
        return levelOneApprovedQuantity;
    }

    @JsonProperty("levelOneApprovedQuantity")
    public void setLevelOneApprovedQuantity(Object levelOneApprovedQuantity) {
        this.levelOneApprovedQuantity = levelOneApprovedQuantity;
    }

    @JsonProperty("levelThreeApprovedQuantity")
    public Object getLevelThreeApprovedQuantity() {
        return levelThreeApprovedQuantity;
    }

    @JsonProperty("levelThreeApprovedQuantity")
    public void setLevelThreeApprovedQuantity(Object levelThreeApprovedQuantity) {
        this.levelThreeApprovedQuantity = levelThreeApprovedQuantity;
    }

    @JsonProperty("levelTwoApprovedQuantity")
    public Object getLevelTwoApprovedQuantity() {
        return levelTwoApprovedQuantity;
    }

    @JsonProperty("levelTwoApprovedQuantity")
    public void setLevelTwoApprovedQuantity(Object levelTwoApprovedQuantity) {
        this.levelTwoApprovedQuantity = levelTwoApprovedQuantity;
    }

    @JsonProperty("ntpybleAmt")
    public Long getNtpybleAmt() {
        return ntpybleAmt;
    }

    @JsonProperty("ntpybleAmt")
    public void setNtpybleAmt(Long ntpybleAmt) {
        this.ntpybleAmt = ntpybleAmt;
    }

    @JsonProperty("pybleAmt")
    public Long getPybleAmt() {
        return pybleAmt;
    }

    @JsonProperty("pybleAmt")
    public void setPybleAmt(Long pybleAmt) {
        this.pybleAmt = pybleAmt;
    }

    @JsonProperty("pyinsAmt")
    public Long getPyinsAmt() {
        return pyinsAmt;
    }

    @JsonProperty("pyinsAmt")
    public void setPyinsAmt(Long pyinsAmt) {
        this.pyinsAmt = pyinsAmt;
    }

    @JsonProperty("refTransferID")
    public Object getRefTransferID() {
        return refTransferID;
    }

    @JsonProperty("refTransferID")
    public void setRefTransferID(Object refTransferID) {
        this.refTransferID = refTransferID;
    }

    @JsonProperty("closeDate")
    public Object getCloseDate() {
        return closeDate;
    }

    @JsonProperty("closeDate")
    public void setCloseDate(Object closeDate) {
        this.closeDate = closeDate;
    }

    @JsonProperty("userrevlist")
    public Object getUserrevlist() {
        return userrevlist;
    }

    @JsonProperty("userrevlist")
    public void setUserrevlist(Object userrevlist) {
        this.userrevlist = userrevlist;
    }

    @JsonProperty("stockUpdated")
    public String getStockUpdated() {
        return stockUpdated;
    }

    @JsonProperty("stockUpdated")
    public void setStockUpdated(String stockUpdated) {
        this.stockUpdated = stockUpdated;
    }

    @JsonProperty("lrstatus")
    public Object getLrstatus() {
        return lrstatus;
    }

    @JsonProperty("lrstatus")
    public void setLrstatus(Object lrstatus) {
        this.lrstatus = lrstatus;
    }

    @JsonProperty("lrflag")
    public Boolean getLrflag() {
        return lrflag;
    }

    @JsonProperty("lrflag")
    public void setLrflag(Boolean lrflag) {
        this.lrflag = lrflag;
    }

    @JsonProperty("grandMsisdn")
    public Object getGrandMsisdn() {
        return grandMsisdn;
    }

    @JsonProperty("grandMsisdn")
    public void setGrandMsisdn(Object grandMsisdn) {
        this.grandMsisdn = grandMsisdn;
    }

    @JsonProperty("channelTransfers")
    public Object getChannelTransfers() {
        return channelTransfers;
    }

    @JsonProperty("channelTransfers")
    public void setChannelTransfers(Object channelTransfers) {
        this.channelTransfers = channelTransfers;
    }

    @JsonProperty("channelVoucherItemsVoList")
    public Object getChannelVoucherItemsVoList() {
        return channelVoucherItemsVoList;
    }

    @JsonProperty("channelVoucherItemsVoList")
    public void setChannelVoucherItemsVoList(Object channelVoucherItemsVoList) {
        this.channelVoucherItemsVoList = channelVoucherItemsVoList;
    }

    @JsonProperty("payInstrumentStatus")
    public Object getPayInstrumentStatus() {
        return payInstrumentStatus;
    }

    @JsonProperty("payInstrumentStatus")
    public void setPayInstrumentStatus(Object payInstrumentStatus) {
        this.payInstrumentStatus = payInstrumentStatus;
    }

    @JsonProperty("payInstrumentName")
    public Object getPayInstrumentName() {
        return payInstrumentName;
    }

    @JsonProperty("payInstrumentName")
    public void setPayInstrumentName(Object payInstrumentName) {
        this.payInstrumentName = payInstrumentName;
    }

    @JsonProperty("voucher_type")
    public Object getVoucherType() {
        return voucherType;
    }

    @JsonProperty("voucher_type")
    public void setVoucherType(Object voucherType) {
        this.voucherType = voucherType;
    }

    @JsonProperty("grandcategory")
    public Object getGrandcategory() {
        return grandcategory;
    }

    @JsonProperty("grandcategory")
    public void setGrandcategory(Object grandcategory) {
        this.grandcategory = grandcategory;
    }

    @JsonProperty("totalTax1")
    public Long getTotalTax1() {
        return totalTax1;
    }

    @JsonProperty("totalTax1")
    public void setTotalTax1(Long totalTax1) {
        this.totalTax1 = totalTax1;
    }

    @JsonProperty("totalTax2")
    public Long getTotalTax2() {
        return totalTax2;
    }

    @JsonProperty("totalTax2")
    public void setTotalTax2(Long totalTax2) {
        this.totalTax2 = totalTax2;
    }

    @JsonProperty("totalTax3")
    public Long getTotalTax3() {
        return totalTax3;
    }

    @JsonProperty("totalTax3")
    public void setTotalTax3(Long totalTax3) {
        this.totalTax3 = totalTax3;
    }

    @JsonProperty("secondApprovalLimit")
    public Long getSecondApprovalLimit() {
        return secondApprovalLimit;
    }

    @JsonProperty("secondApprovalLimit")
    public void setSecondApprovalLimit(Long secondApprovalLimit) {
        this.secondApprovalLimit = secondApprovalLimit;
    }

    @JsonProperty("channelUserStatus")
    public Object getChannelUserStatus() {
        return channelUserStatus;
    }

    @JsonProperty("channelUserStatus")
    public void setChannelUserStatus(Object channelUserStatus) {
        this.channelUserStatus = channelUserStatus;
    }

    @JsonProperty("transferSubType")
    public String getTransferSubType() {
        return transferSubType;
    }

    @JsonProperty("transferSubType")
    public void setTransferSubType(String transferSubType) {
        this.transferSubType = transferSubType;
    }

    @JsonProperty("transactionMode")
    public String getTransactionMode() {
        return transactionMode;
    }

    @JsonProperty("transactionMode")
    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    @JsonProperty("transferCategoryCode")
    public Object getTransferCategoryCode() {
        return transferCategoryCode;
    }

    @JsonProperty("transferCategoryCode")
    public void setTransferCategoryCode(Object transferCategoryCode) {
        this.transferCategoryCode = transferCategoryCode;
    }

    @JsonProperty("defaultLang")
    public Object getDefaultLang() {
        return defaultLang;
    }

    @JsonProperty("defaultLang")
    public void setDefaultLang(Object defaultLang) {
        this.defaultLang = defaultLang;
    }

    @JsonProperty("secondLang")
    public Object getSecondLang() {
        return secondLang;
    }

    @JsonProperty("secondLang")
    public void setSecondLang(Object secondLang) {
        this.secondLang = secondLang;
    }

    @JsonProperty("userMsisdn")
    public Object getUserMsisdn() {
        return userMsisdn;
    }

    @JsonProperty("userMsisdn")
    public void setUserMsisdn(Object userMsisdn) {
        this.userMsisdn = userMsisdn;
    }

    @JsonProperty("transferDateAsString")
    public String getTransferDateAsString() {
        return transferDateAsString;
    }

    @JsonProperty("transferDateAsString")
    public void setTransferDateAsString(String transferDateAsString) {
        this.transferDateAsString = transferDateAsString;
    }

    @JsonProperty("receiverGradeCode")
    public Object getReceiverGradeCode() {
        return receiverGradeCode;
    }

    @JsonProperty("receiverGradeCode")
    public void setReceiverGradeCode(Object receiverGradeCode) {
        this.receiverGradeCode = receiverGradeCode;
    }

    @JsonProperty("externalTxnDate")
    public Object getExternalTxnDate() {
        return externalTxnDate;
    }

    @JsonProperty("externalTxnDate")
    public void setExternalTxnDate(Object externalTxnDate) {
        this.externalTxnDate = externalTxnDate;
    }

    @JsonProperty("externalTxnNum")
    public Object getExternalTxnNum() {
        return externalTxnNum;
    }

    @JsonProperty("externalTxnNum")
    public void setExternalTxnNum(Object externalTxnNum) {
        this.externalTxnNum = externalTxnNum;
    }

    @JsonProperty("toUserID")
    public String getToUserID() {
        return toUserID;
    }

    @JsonProperty("toUserID")
    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
    }

    @JsonProperty("transferMRP")
    public Long getTransferMRP() {
        return transferMRP;
    }

    @JsonProperty("transferMRP")
    public void setTransferMRP(Long transferMRP) {
        this.transferMRP = transferMRP;
    }

    @JsonProperty("fromUserID")
    public Object getFromUserID() {
        return fromUserID;
    }

    @JsonProperty("fromUserID")
    public void setFromUserID(Object fromUserID) {
        this.fromUserID = fromUserID;
    }

    @JsonProperty("senderDrQty")
    public Long getSenderDrQty() {
        return senderDrQty;
    }

    @JsonProperty("senderDrQty")
    public void setSenderDrQty(Long senderDrQty) {
        this.senderDrQty = senderDrQty;
    }

    @JsonProperty("receiverCrQty")
    public Long getReceiverCrQty() {
        return receiverCrQty;
    }

    @JsonProperty("receiverCrQty")
    public void setReceiverCrQty(Long receiverCrQty) {
        this.receiverCrQty = receiverCrQty;
    }

    @JsonProperty("fromChannelUserStatus")
    public Object getFromChannelUserStatus() {
        return fromChannelUserStatus;
    }

    @JsonProperty("fromChannelUserStatus")
    public void setFromChannelUserStatus(Object fromChannelUserStatus) {
        this.fromChannelUserStatus = fromChannelUserStatus;
    }

    @JsonProperty("toChannelUserStatus")
    public Object getToChannelUserStatus() {
        return toChannelUserStatus;
    }

    @JsonProperty("toChannelUserStatus")
    public void setToChannelUserStatus(Object toChannelUserStatus) {
        this.toChannelUserStatus = toChannelUserStatus;
    }

    @JsonProperty("channelTransferitemsVOList")
    public Object getChannelTransferitemsVOList() {
        return channelTransferitemsVOList;
    }

    @JsonProperty("channelTransferitemsVOList")
    public void setChannelTransferitemsVOList(Object channelTransferitemsVOList) {
        this.channelTransferitemsVOList = channelTransferitemsVOList;
    }

    @JsonProperty("senderGradeCode")
    public Object getSenderGradeCode() {
        return senderGradeCode;
    }

    @JsonProperty("senderGradeCode")
    public void setSenderGradeCode(Object senderGradeCode) {
        this.senderGradeCode = senderGradeCode;
    }

    @JsonProperty("fromUserCode")
    public Object getFromUserCode() {
        return fromUserCode;
    }

    @JsonProperty("fromUserCode")
    public void setFromUserCode(Object fromUserCode) {
        this.fromUserCode = fromUserCode;
    }

    @JsonProperty("toUserCode")
    public Object getToUserCode() {
        return toUserCode;
    }

    @JsonProperty("toUserCode")
    public void setToUserCode(Object toUserCode) {
        this.toUserCode = toUserCode;
    }

    @JsonProperty("transferCategoryCodeDesc")
    public Object getTransferCategoryCodeDesc() {
        return transferCategoryCodeDesc;
    }

    @JsonProperty("transferCategoryCodeDesc")
    public void setTransferCategoryCodeDesc(Object transferCategoryCodeDesc) {
        this.transferCategoryCodeDesc = transferCategoryCodeDesc;
    }

    @JsonProperty("receiverDomainCode")
    public Object getReceiverDomainCode() {
        return receiverDomainCode;
    }

    @JsonProperty("receiverDomainCode")
    public void setReceiverDomainCode(Object receiverDomainCode) {
        this.receiverDomainCode = receiverDomainCode;
    }

    @JsonProperty("receiverGgraphicalDomainCode")
    public Object getReceiverGgraphicalDomainCode() {
        return receiverGgraphicalDomainCode;
    }

    @JsonProperty("receiverGgraphicalDomainCode")
    public void setReceiverGgraphicalDomainCode(Object receiverGgraphicalDomainCode) {
        this.receiverGgraphicalDomainCode = receiverGgraphicalDomainCode;
    }

    @JsonProperty("senderCatName")
    public Object getSenderCatName() {
        return senderCatName;
    }

    @JsonProperty("senderCatName")
    public void setSenderCatName(Object senderCatName) {
        this.senderCatName = senderCatName;
    }

    @JsonProperty("receiverCategoryDesc")
    public String getReceiverCategoryDesc() {
        return receiverCategoryDesc;
    }

    @JsonProperty("receiverCategoryDesc")
    public void setReceiverCategoryDesc(String receiverCategoryDesc) {
        this.receiverCategoryDesc = receiverCategoryDesc;
    }

    @JsonProperty("activeUserName")
    public Object getActiveUserName() {
        return activeUserName;
    }

    @JsonProperty("activeUserName")
    public void setActiveUserName(Object activeUserName) {
        this.activeUserName = activeUserName;
    }

    @JsonProperty("activeUserId")
    public Object getActiveUserId() {
        return activeUserId;
    }

    @JsonProperty("activeUserId")
    public void setActiveUserId(Object activeUserId) {
        this.activeUserId = activeUserId;
    }

    @JsonProperty("subSid")
    public Object getSubSid() {
        return subSid;
    }

    @JsonProperty("subSid")
    public void setSubSid(Object subSid) {
        this.subSid = subSid;
    }

    @JsonProperty("senderCategory")
    public Object getSenderCategory() {
        return senderCategory;
    }

    @JsonProperty("senderCategory")
    public void setSenderCategory(Object senderCategory) {
        this.senderCategory = senderCategory;
    }

    @JsonProperty("displayTransferMRP")
    public Object getDisplayTransferMRP() {
        return displayTransferMRP;
    }

    @JsonProperty("displayTransferMRP")
    public void setDisplayTransferMRP(Object displayTransferMRP) {
        this.displayTransferMRP = displayTransferMRP;
    }

    @JsonProperty("grphDomainCodeDesc")
    public String getGrphDomainCodeDesc() {
        return grphDomainCodeDesc;
    }

    @JsonProperty("grphDomainCodeDesc")
    public void setGrphDomainCodeDesc(String grphDomainCodeDesc) {
        this.grphDomainCodeDesc = grphDomainCodeDesc;
    }

    @JsonProperty("batchDate")
    public Object getBatchDate() {
        return batchDate;
    }

    @JsonProperty("batchDate")
    public void setBatchDate(Object batchDate) {
        this.batchDate = batchDate;
    }

    @JsonProperty("firstApprovedBy")
    public Object getFirstApprovedBy() {
        return firstApprovedBy;
    }

    @JsonProperty("firstApprovedBy")
    public void setFirstApprovedBy(Object firstApprovedBy) {
        this.firstApprovedBy = firstApprovedBy;
    }

    @JsonProperty("firstApprovedOn")
    public Object getFirstApprovedOn() {
        return firstApprovedOn;
    }

    @JsonProperty("firstApprovedOn")
    public void setFirstApprovedOn(Object firstApprovedOn) {
        this.firstApprovedOn = firstApprovedOn;
    }

    @JsonProperty("secondApprovedBy")
    public Object getSecondApprovedBy() {
        return secondApprovedBy;
    }

    @JsonProperty("secondApprovedBy")
    public void setSecondApprovedBy(Object secondApprovedBy) {
        this.secondApprovedBy = secondApprovedBy;
    }

    @JsonProperty("secondApprovedOn")
    public Object getSecondApprovedOn() {
        return secondApprovedOn;
    }

    @JsonProperty("secondApprovedOn")
    public void setSecondApprovedOn(Object secondApprovedOn) {
        this.secondApprovedOn = secondApprovedOn;
    }

    @JsonProperty("thirdApprovedBy")
    public Object getThirdApprovedBy() {
        return thirdApprovedBy;
    }

    @JsonProperty("thirdApprovedBy")
    public void setThirdApprovedBy(Object thirdApprovedBy) {
        this.thirdApprovedBy = thirdApprovedBy;
    }

    @JsonProperty("thirdApprovedOn")
    public Object getThirdApprovedOn() {
        return thirdApprovedOn;
    }

    @JsonProperty("thirdApprovedOn")
    public void setThirdApprovedOn(Object thirdApprovedOn) {
        this.thirdApprovedOn = thirdApprovedOn;
    }

    @JsonProperty("domainCode")
    public Object getDomainCode() {
        return domainCode;
    }

    @JsonProperty("domainCode")
    public void setDomainCode(Object domainCode) {
        this.domainCode = domainCode;
    }

    @JsonProperty("domainCodeDesc")
    public String getDomainCodeDesc() {
        return domainCodeDesc;
    }

    @JsonProperty("domainCodeDesc")
    public void setDomainCodeDesc(String domainCodeDesc) {
        this.domainCodeDesc = domainCodeDesc;
    }

    @JsonProperty("productType")
    public String getProductType() {
        return productType;
    }

    @JsonProperty("productType")
    public void setProductType(String productType) {
        this.productType = productType;
    }

    @JsonProperty("commProfileSetId")
    public String getCommProfileSetId() {
        return commProfileSetId;
    }

    @JsonProperty("commProfileSetId")
    public void setCommProfileSetId(String commProfileSetId) {
        this.commProfileSetId = commProfileSetId;
    }

    @JsonProperty("requestedQuantity")
    public Long getRequestedQuantity() {
        return requestedQuantity;
    }

    @JsonProperty("requestedQuantity")
    public void setRequestedQuantity(Long requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    @JsonProperty("walletType")
    public String getWalletType() {
        return walletType;
    }

    @JsonProperty("walletType")
    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    @JsonProperty("createdBy")
    public Object getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("fromUserName")
    public Object getFromUserName() {
        return fromUserName;
    }

    @JsonProperty("fromUserName")
    public void setFromUserName(Object fromUserName) {
        this.fromUserName = fromUserName;
    }

    @JsonProperty("toUserName")
    public String getToUserName() {
        return toUserName;
    }

    @JsonProperty("toUserName")
    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    @JsonProperty("transferProfileID")
    public Object getTransferProfileID() {
        return transferProfileID;
    }

    @JsonProperty("transferProfileID")
    public void setTransferProfileID(Object transferProfileID) {
        this.transferProfileID = transferProfileID;
    }

    @JsonProperty("transferCategory")
    public Object getTransferCategory() {
        return transferCategory;
    }

    @JsonProperty("transferCategory")
    public void setTransferCategory(Object transferCategory) {
        this.transferCategory = transferCategory;
    }

    @JsonProperty("categoryCode")
    public Object getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(Object categoryCode) {
        this.categoryCode = categoryCode;
    }

    @JsonProperty("requestGatewayCode")
    public Object getRequestGatewayCode() {
        return requestGatewayCode;
    }

    @JsonProperty("requestGatewayCode")
    public void setRequestGatewayCode(Object requestGatewayCode) {
        this.requestGatewayCode = requestGatewayCode;
    }

    @JsonProperty("requestGatewayType")
    public Object getRequestGatewayType() {
        return requestGatewayType;
    }

    @JsonProperty("requestGatewayType")
    public void setRequestGatewayType(Object requestGatewayType) {
        this.requestGatewayType = requestGatewayType;
    }

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("senderLoginID")
    public Object getSenderLoginID() {
        return senderLoginID;
    }

    @JsonProperty("senderLoginID")
    public void setSenderLoginID(Object senderLoginID) {
        this.senderLoginID = senderLoginID;
    }

    @JsonProperty("networkCodeFor")
    public String getNetworkCodeFor() {
        return networkCodeFor;
    }

    @JsonProperty("networkCodeFor")
    public void setNetworkCodeFor(String networkCodeFor) {
        this.networkCodeFor = networkCodeFor;
    }

    @JsonProperty("receiverLoginID")
    public Object getReceiverLoginID() {
        return receiverLoginID;
    }

    @JsonProperty("receiverLoginID")
    public void setReceiverLoginID(Object receiverLoginID) {
        this.receiverLoginID = receiverLoginID;
    }

    @JsonProperty("cellId")
    public Object getCellId() {
        return cellId;
    }

    @JsonProperty("cellId")
    public void setCellId(Object cellId) {
        this.cellId = cellId;
    }

    @JsonProperty("switchId")
    public Object getSwitchId() {
        return switchId;
    }

    @JsonProperty("switchId")
    public void setSwitchId(Object switchId) {
        this.switchId = switchId;
    }

    @JsonProperty("productCode")
    public Object getProductCode() {
        return productCode;
    }

    @JsonProperty("productCode")
    public void setProductCode(Object productCode) {
        this.productCode = productCode;
    }

    @JsonProperty("address1")
    public Object getAddress1() {
        return address1;
    }

    @JsonProperty("address1")
    public void setAddress1(Object address1) {
        this.address1 = address1;
    }

    @JsonProperty("address2")
    public Object getAddress2() {
        return address2;
    }

    @JsonProperty("address2")
    public void setAddress2(Object address2) {
        this.address2 = address2;
    }

    @JsonProperty("city")
    public Object getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(Object city) {
        this.city = city;
    }

    @JsonProperty("country")
    public Object getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(Object country) {
        this.country = country;
    }

    @JsonProperty("externalCode")
    public Object getExternalCode() {
        return externalCode;
    }

    @JsonProperty("externalCode")
    public void setExternalCode(Object externalCode) {
        this.externalCode = externalCode;
    }

    @JsonProperty("referenceID")
    public Object getReferenceID() {
        return referenceID;
    }

    @JsonProperty("referenceID")
    public void setReferenceID(Object referenceID) {
        this.referenceID = referenceID;
    }

    @JsonProperty("modifiedBy")
    public Object getModifiedBy() {
        return modifiedBy;
    }

    @JsonProperty("modifiedBy")
    public void setModifiedBy(Object modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("createdOn")
    public Object getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    @JsonProperty("modifiedOn")
    public Object getModifiedOn() {
        return modifiedOn;
    }

    @JsonProperty("modifiedOn")
    public void setModifiedOn(Object modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @JsonProperty("email")
    public Object getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(Object email) {
        this.email = email;
    }

    @JsonProperty("fullAddress")
    public String getFullAddress() {
        return fullAddress;
    }

    @JsonProperty("fullAddress")
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @JsonProperty("source")
    public Object getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(Object source) {
        this.source = source;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("state")
    public Object getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(Object state) {
        this.state = state;
    }

    @JsonProperty("statusDesc")
    public String getStatusDesc() {
        return statusDesc;
    }

    @JsonProperty("statusDesc")
    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    @JsonProperty("index")
    public Long getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(Long index) {
        this.index = index;
    }

    @JsonProperty("transferID")
    public String getTransferID() {
        return transferID;
    }

    @JsonProperty("transferID")
    public void setTransferID(String transferID) {
        this.transferID = transferID;
    }

    @JsonProperty("transferDate")
    public Long getTransferDate() {
        return transferDate;
    }

    @JsonProperty("transferDate")
    public void setTransferDate(Long transferDate) {
        this.transferDate = transferDate;
    }

    @JsonProperty("serviceClass")
    public Object getServiceClass() {
        return serviceClass;
    }

    @JsonProperty("serviceClass")
    public void setServiceClass(Object serviceClass) {
        this.serviceClass = serviceClass;
    }

    @JsonProperty("transferType")
    public String getTransferType() {
        return transferType;
    }

    @JsonProperty("transferType")
    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    @JsonProperty("entryType")
    public Object getEntryType() {
        return entryType;
    }

    @JsonProperty("entryType")
    public void setEntryType(Object entryType) {
        this.entryType = entryType;
    }

    @JsonProperty("netPayableAmount")
    public Long getNetPayableAmount() {
        return netPayableAmount;
    }

    @JsonProperty("netPayableAmount")
    public void setNetPayableAmount(Long netPayableAmount) {
        this.netPayableAmount = netPayableAmount;
    }

    @JsonProperty("payableAmount")
    public Long getPayableAmount() {
        return payableAmount;
    }

    @JsonProperty("payableAmount")
    public void setPayableAmount(Long payableAmount) {
        this.payableAmount = payableAmount;
    }

    @JsonProperty("receiverPreviousStock")
    public Long getReceiverPreviousStock() {
        return receiverPreviousStock;
    }

    @JsonProperty("receiverPreviousStock")
    public void setReceiverPreviousStock(Long receiverPreviousStock) {
        this.receiverPreviousStock = receiverPreviousStock;
    }

    @JsonProperty("senderPreviousStock")
    public Long getSenderPreviousStock() {
        return senderPreviousStock;
    }

    @JsonProperty("senderPreviousStock")
    public void setSenderPreviousStock(Long senderPreviousStock) {
        this.senderPreviousStock = senderPreviousStock;
    }

    @JsonProperty("senderPostStock")
    public Object getSenderPostStock() {
        return senderPostStock;
    }

    @JsonProperty("senderPostStock")
    public void setSenderPostStock(Object senderPostStock) {
        this.senderPostStock = senderPostStock;
    }

    @JsonProperty("receiverPostStock")
    public Object getReceiverPostStock() {
        return receiverPostStock;
    }

    @JsonProperty("receiverPostStock")
    public void setReceiverPostStock(Object receiverPostStock) {
        this.receiverPostStock = receiverPostStock;
    }

    @JsonProperty("lastModifiedTime")
    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }

    @JsonProperty("lastModifiedTime")
    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("reqQuantity", reqQuantity).append("status", status).append("transferMRPReplica", transferMRPReplica).append("userWalletCode", userWalletCode).append("tax1Rate", tax1Rate).append("tax1Type", tax1Type).append("tax2Rate", tax2Rate).append("tax2Type", tax2Type).append("senderPostbalance", senderPostbalance).append("recieverPostBalance", recieverPostBalance).append("sosRequestAmount", sosRequestAmount).append("sosFlag", sosFlag).append("fileUploaded", fileUploaded).append("lrWithdrawAmt", lrWithdrawAmt).append("otfCountsUpdated", otfCountsUpdated).append("targetAchieved", targetAchieved).append("channelTransferitemsVOListforOTF", channelTransferitemsVOListforOTF).append("fromUserGeo", fromUserGeo).append("fromOwnerGeo", fromOwnerGeo).append("toUserGeo", toUserGeo).append("toOwnerGeo", toOwnerGeo).append("toMSISDN", toMSISDN).append("mrp", mrp).append("modifiedOnAsString", modifiedOnAsString).append("approvedAmount", approvedAmount).append("tax1Value", tax1Value).append("tax2Value", tax2Value).append("receiverCrQtyAsString", receiverCrQtyAsString).append("payInstrumentDateAsString", payInstrumentDateAsString).append("payableAmountAsString", payableAmountAsString).append("netPayableAmountAsString", netPayableAmountAsString).append("adjustmentID", adjustmentID).append("time", time).append("userName", userName).append("msisdn", msisdn).append("categoryName", categoryName).append("gegoraphyDomainName", gegoraphyDomainName).append("parentName", parentName).append("parentMsisdn", parentMsisdn).append("parentGeoName", parentGeoName).append("ownerUser", ownerUser).append("ownerMsisdn", ownerMsisdn).append("ownerGeo", ownerGeo).append("ownerCat", ownerCat).append("name", name).append("receiverMsisdn", receiverMsisdn).append("commissionType", commissionType).append("commissionRate", commissionRate).append("commissionValue", commissionValue).append("transferAmt", transferAmt).append("marginAmount", marginAmount).append("marginRate", marginRate).append("otfType", otfType).append("parentCategory", parentCategory).append("paymentInstType", paymentInstType).append("domainName", domainName).append("ownerName", ownerName).append("transInCount", transInCount).append("transOutCount", transOutCount).append("transInAmount", transInAmount).append("fromEXTCODE", fromEXTCODE).append("toEXTCODE", toEXTCODE).append("ownerProfile", ownerProfile).append("parentProfile", parentProfile).append("externalTranDate", externalTranDate).append("commQtyAsString", commQtyAsString).append("senderDrQtyAsString", senderDrQtyAsString).append("payableAmounts", payableAmounts).append("netPayableAmounts", netPayableAmounts).append("approvedQuantity", approvedQuantity).append("userID", userID).append("loginID", loginID).append("serviceTypeName", serviceTypeName).append("selectorName", selectorName).append("differentialAmount", differentialAmount).append("transactionCount", transactionCount).append("o2cTransferInCount", o2cTransferInCount).append("o2cTransferInAmount", o2cTransferInAmount).append("c2cTransferInCount", c2cTransferInCount).append("c2cTransferInAmount", c2cTransferInAmount).append("c2cReturnPlusWithCount", c2cReturnPlusWithCount).append("c2cReturnPlusWithINAmount", c2cReturnPlusWithINAmount).append("o2cReturnPlusWithoutCount", o2cReturnPlusWithoutCount).append("o2cReturnPlusWithoutAmount", o2cReturnPlusWithoutAmount).append("c2cTransferOutCount", c2cTransferOutCount).append("c2cTransferOutAmount", c2cTransferOutAmount).append("c2cReturnWithOutCount", c2cReturnWithOutCount).append("c2cReturnWithOutAmount", c2cReturnWithOutAmount).append("c2sTransferOutCount", c2sTransferOutCount).append("c2sTransferAmount", c2sTransferAmount).append("unitValue", unitValue).append("requiredQuantity", requiredQuantity).append("dualCommissionType", dualCommissionType).append("profileNames", profileNames).append("curStatus", curStatus).append("apprRejStatus", apprRejStatus).append("batchNo", batchNo).append("productName", productName).append("batchType", batchType).append("fromSerialNo", fromSerialNo).append("toSerialNo", toSerialNo).append("totalNoOfVouchers", totalNoOfVouchers).append("userOTFCountsVO", userOTFCountsVO).append("transferSubTypeAsString", transferSubTypeAsString).append("fromMsisdn", fromMsisdn).append("toMsisdn", toMsisdn).append("toPrimaryMSISDN", toPrimaryMSISDN).append("fromCategoryDesc", fromCategoryDesc).append("toCategoryDesc", toCategoryDesc).append("fromGradeCodeDesc", fromGradeCodeDesc).append("toGradeCodeDesc", toGradeCodeDesc).append("fromCommissionProfileIDDesc", fromCommissionProfileIDDesc).append("toCommissionProfileIDDesc", toCommissionProfileIDDesc).append("toTxnProfileDesc", toTxnProfileDesc).append("fromTxnProfileDesc", fromTxnProfileDesc).append("segment", segment).append("firstApprovedOnAsString", firstApprovedOnAsString).append("secondApprovedOnAsString", secondApprovedOnAsString).append("isFileC2C", isFileC2C).append("uploadedFile", uploadedFile).append("uploadedFilePath", uploadedFilePath).append("uploadedFileName", uploadedFileName).append("channelTransferList", channelTransferList).append("voucherTypeList", voucherTypeList).append("slabslist", slabslist).append("approvalFile", approvalFile).append("bundleType", bundleType).append("reconciliationFlag", reconciliationFlag).append("previousStatus", previousStatus).append("tax3ValueAsString", tax3ValueAsString).append("transOutAmount", transOutAmount).append("commProfileDetailID", commProfileDetailID).append("sosStatus", sosStatus).append("sosTxnId", sosTxnId).append("sosProductCode", sosProductCode).append("transactionCode", transactionCode).append("info1", info1).append("info2", info2).append("info3", info3).append("info4", info4).append("info5", info5).append("info6", info6).append("info7", info7).append("info8", info8).append("info9", info9).append("info10", info10).append("otfFlag", otfFlag).append("otfTypePctOrAMt", otfTypePctOrAMt).append("otfRate", otfRate).append("otfAmount", otfAmount).append("channelSoSVOList", channelSoSVOList).append("sosSettlementDate", sosSettlementDate).append("grandName", grandName).append("grandGeo", grandGeo).append("openingBalance", openingBalance).append("stockBought", stockBought).append("stockReturn", stockReturn).append("channelReturn", channelReturn).append("c2sTransfers", c2sTransfers).append("closingBalance", closingBalance).append("reconStatus", reconStatus).append("netBalance", netBalance).append("netLifting", netLifting).append("messageArgumentList", messageArgumentList).append("toUserMsisdn", toUserMsisdn).append("channelRemarks", channelRemarks).append("transferInitatedBy", transferInitatedBy).append("controlTransfer", controlTransfer).append("graphicalDomainCode", graphicalDomainCode).append("receiverCategoryCode", receiverCategoryCode).append("senderTxnProfile", senderTxnProfile).append("receiverTxnProfile", receiverTxnProfile).append("payInstrumentAmt", payInstrumentAmt).append("commQty", commQty).append("firstApproverLimit", firstApproverLimit).append("payInstrumentDate", payInstrumentDate).append("payInstrumentType", payInstrumentType).append("payInstrumentNum", payInstrumentNum).append("commProfileVersion", commProfileVersion).append("referenceNum", referenceNum).append("netPayableAmountAsStr", netPayableAmountAsStr).append("payableAmountAsStr", payableAmountAsStr).append("web", web).append("reversalFlag", reversalFlag).append("erpNum", erpNum).append("canceledBy", canceledBy).append("canceledOn", canceledOn).append("externalTxnDateAsString", externalTxnDateAsString).append("firstApprovalRemark", firstApprovalRemark).append("batchNum", batchNum).append("requestedQuantityAsString", requestedQuantityAsString).append("secondApprovalRemark", secondApprovalRemark).append("thirdApprovalRemark", thirdApprovalRemark).append("paymentInstSource", paymentInstSource).append("canceledByApprovedName", canceledByApprovedName).append("commProfileName", commProfileName).append("firstApprovedByName", firstApprovedByName).append("receiverTxnProfileName", receiverTxnProfileName).append("secondApprovedByName", secondApprovedByName).append("receiverGradeCodeDesc", receiverGradeCodeDesc).append("thirdApprovedByName", thirdApprovedByName).append("transferInitatedByName", transferInitatedByName).append("transferMRPAsString", transferMRPAsString).append("finalApprovedBy", finalApprovedBy).append("finalApprovedDateAsString", finalApprovedDateAsString).append("senderTxnProfileName", senderTxnProfileName).append("transferSubTypeValue", transferSubTypeValue).append("dbdateTime", dbdateTime).append("commisionTxnId", commisionTxnId).append("levelOneApprovedQuantity", levelOneApprovedQuantity).append("levelThreeApprovedQuantity", levelThreeApprovedQuantity).append("levelTwoApprovedQuantity", levelTwoApprovedQuantity).append("ntpybleAmt", ntpybleAmt).append("pybleAmt", pybleAmt).append("pyinsAmt", pyinsAmt).append("refTransferID", refTransferID).append("closeDate", closeDate).append("userrevlist", userrevlist).append("stockUpdated", stockUpdated).append("lrstatus", lrstatus).append("lrflag", lrflag).append("grandMsisdn", grandMsisdn).append("channelTransfers", channelTransfers).append("channelVoucherItemsVoList", channelVoucherItemsVoList).append("payInstrumentStatus", payInstrumentStatus).append("payInstrumentName", payInstrumentName).append("voucherType", voucherType).append("grandcategory", grandcategory).append("totalTax1", totalTax1).append("totalTax2", totalTax2).append("totalTax3", totalTax3).append("secondApprovalLimit", secondApprovalLimit).append("channelUserStatus", channelUserStatus).append("transferSubType", transferSubType).append("transactionMode", transactionMode).append("transferCategoryCode", transferCategoryCode).append("defaultLang", defaultLang).append("secondLang", secondLang).append("userMsisdn", userMsisdn).append("transferDateAsString", transferDateAsString).append("receiverGradeCode", receiverGradeCode).append("externalTxnDate", externalTxnDate).append("externalTxnNum", externalTxnNum).append("toUserID", toUserID).append("transferMRP", transferMRP).append("fromUserID", fromUserID).append("senderDrQty", senderDrQty).append("receiverCrQty", receiverCrQty).append("fromChannelUserStatus", fromChannelUserStatus).append("toChannelUserStatus", toChannelUserStatus).append("channelTransferitemsVOList", channelTransferitemsVOList).append("senderGradeCode", senderGradeCode).append("fromUserCode", fromUserCode).append("toUserCode", toUserCode).append("transferCategoryCodeDesc", transferCategoryCodeDesc).append("receiverDomainCode", receiverDomainCode).append("receiverGgraphicalDomainCode", receiverGgraphicalDomainCode).append("senderCatName", senderCatName).append("receiverCategoryDesc", receiverCategoryDesc).append("activeUserName", activeUserName).append("activeUserId", activeUserId).append("subSid", subSid).append("senderCategory", senderCategory).append("displayTransferMRP", displayTransferMRP).append("grphDomainCodeDesc", grphDomainCodeDesc).append("batchDate", batchDate).append("firstApprovedBy", firstApprovedBy).append("firstApprovedOn", firstApprovedOn).append("secondApprovedBy", secondApprovedBy).append("secondApprovedOn", secondApprovedOn).append("thirdApprovedBy", thirdApprovedBy).append("thirdApprovedOn", thirdApprovedOn).append("domainCode", domainCode).append("domainCodeDesc", domainCodeDesc).append("productType", productType).append("commProfileSetId", commProfileSetId).append("requestedQuantity", requestedQuantity).append("walletType", walletType).append("createdBy", createdBy).append("fromUserName", fromUserName).append("toUserName", toUserName).append("transferProfileID", transferProfileID).append("transferCategory", transferCategory).append("categoryCode", categoryCode).append("requestGatewayCode", requestGatewayCode).append("requestGatewayType", requestGatewayType).append("networkCode", networkCode).append("senderLoginID", senderLoginID).append("networkCodeFor", networkCodeFor).append("receiverLoginID", receiverLoginID).append("cellId", cellId).append("switchId", switchId).append("productCode", productCode).append("address1", address1).append("address2", address2).append("city", city).append("country", country).append("externalCode", externalCode).append("referenceID", referenceID).append("modifiedBy", modifiedBy).append("createdOn", createdOn).append("modifiedOn", modifiedOn).append("email", email).append("fullAddress", fullAddress).append("source", source).append("type", type).append("state", state).append("statusDesc", statusDesc).append("index", index).append("transferID", transferID).append("transferDate", transferDate).append("serviceClass", serviceClass).append("transferType", transferType).append("entryType", entryType).append("netPayableAmount", netPayableAmount).append("payableAmount", payableAmount).append("receiverPreviousStock", receiverPreviousStock).append("senderPreviousStock", senderPreviousStock).append("senderPostStock", senderPostStock).append("receiverPostStock", receiverPostStock).append("lastModifiedTime", lastModifiedTime).append("additionalProperties", additionalProperties).toString();
    }

}
