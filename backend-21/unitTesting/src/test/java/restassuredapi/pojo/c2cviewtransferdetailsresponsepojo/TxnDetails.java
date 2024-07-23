
package restassuredapi.pojo.c2cviewtransferdetailsresponsepojo;

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
    "_transferMRPReplica",
    "activeUserId",
    "activeUserName",
    "address1",
    "address2",
    "adjustmentID",
    "apprRejStatus",
    "approvalFile",
    "approvedAmount",
    "approvedQuantity",
    "batchDate",
    "batchNum",
    "batch_no",
    "batch_type",
    "bundleType",
    "c2cReturnPlusWithCount",
    "c2cReturnPlusWithINAmount",
    "c2cReturnWithOutAmount",
    "c2cReturnWithOutCount",
    "c2cTransferInAmount",
    "c2cTransferInCount",
    "c2cTransferOutAmount",
    "c2cTransferOutCount",
    "c2sTransferAmount",
    "c2sTransferOutCount",
    "c2sTransfers",
    "canceledBy",
    "canceledByApprovedName",
    "canceledOn",
    "categoryCode",
    "categoryName",
    "cellId",
    "channelRemarks",
    "channelReturn",
    "channelSoSVOList",
    "channelTransferList",
    "channelTransferitemsVOList",
    "channelTransferitemsVOListforOTF",
    "channelTransfers",
    "channelUserStatus",
    "channelVoucherItemsVoList",
    "city",
    "closeDate",
    "closingBalance",
    "commProfileDetailID",
    "commProfileName",
    "commProfileSetId",
    "commProfileVersion",
    "commQty",
    "commQtyAsString",
    "commisionTxnId",
    "commissionRate",
    "commissionType",
    "commissionValue",
    "controlTransfer",
    "country",
    "createdBy",
    "createdOn",
    "curStatus",
    "dbdateTime",
    "defaultLang",
    "differentialAmount",
    "displayTransferMRP",
    "domainCode",
    "domainCodeDesc",
    "domainName",
    "dualCommissionType",
    "email",
    "entryType",
    "erpNum",
    "externalCode",
    "externalTranDate",
    "externalTxnDate",
    "externalTxnDateAsString",
    "externalTxnNum",
    "fileUploaded",
    "finalApprovedBy",
    "finalApprovedDateAsString",
    "firstApprovalRemark",
    "firstApprovedBy",
    "firstApprovedByName",
    "firstApprovedOn",
    "firstApprovedOnAsString",
    "firstApproverLimit",
    "fromCategoryDesc",
    "fromChannelUserStatus",
    "fromCommissionProfileIDDesc",
    "fromEXTCODE",
    "fromGradeCodeDesc",
    "fromMsisdn",
    "fromOwnerGeo",
    "fromTxnProfileDesc",
    "fromUserCode",
    "fromUserGeo",
    "fromUserID",
    "fromUserName",
    "from_serial_no",
    "fullAddress",
    "gegoraphyDomainName",
    "grandGeo",
    "grandMsisdn",
    "grandName",
    "grandcategory",
    "graphicalDomainCode",
    "grphDomainCodeDesc",
    "index",
    "info1",
    "info10",
    "info2",
    "info3",
    "info4",
    "info5",
    "info6",
    "info7",
    "info8",
    "info9",
    "isFileC2C",
    "lastModifiedTime",
    "levelOneApprovedQuantity",
    "levelThreeApprovedQuantity",
    "levelTwoApprovedQuantity",
    "loginID",
    "lrWithdrawAmt",
    "lrflag",
    "lrstatus",
    "marginAmount",
    "marginRate",
    "messageArgumentList",
    "modifiedBy",
    "modifiedOn",
    "modifiedOnAsString",
    "mrp",
    "msisdn",
    "name",
    "netBalance",
    "netLifting",
    "netPayableAmount",
    "netPayableAmountAsStr",
    "netPayableAmountAsString",
    "netPayableAmounts",
    "networkCode",
    "networkCodeFor",
    "ntpybleAmt",
    "o2cReturnPlusWithoutAmount",
    "o2cReturnPlusWithoutCount",
    "o2cTransferInAmount",
    "o2cTransferInCount",
    "openingBalance",
    "otfAmount",
    "otfCountsUpdated",
    "otfFlag",
    "otfRate",
    "otfType",
    "otfTypePctOrAMt",
    "ownerCat",
    "ownerGeo",
    "ownerMsisdn",
    "ownerName",
    "ownerProfile",
    "ownerUser",
    "parentCategory",
    "parentGeoName",
    "parentMsisdn",
    "parentName",
    "parentProfile",
    "payInstrumentAmt",
    "payInstrumentDate",
    "payInstrumentDateAsString",
    "payInstrumentName",
    "payInstrumentNum",
    "payInstrumentStatus",
    "payInstrumentType",
    "payableAmount",
    "payableAmountAsStr",
    "payableAmountAsString",
    "payableAmounts",
    "paymentInstSource",
    "paymentInstType",
    "previousStatus",
    "productCode",
    "productType",
    "product_name",
    "profileNames",
    "pybleAmt",
    "pyinsAmt",
    "receiverCategoryCode",
    "receiverCategoryDesc",
    "receiverCrQty",
    "receiverCrQtyAsString",
    "receiverDomainCode",
    "receiverGgraphicalDomainCode",
    "receiverGradeCode",
    "receiverGradeCodeDesc",
    "receiverLoginID",
    "receiverMsisdn",
    "receiverPostStock",
    "receiverPreviousStock",
    "receiverTxnProfile",
    "receiverTxnProfileName",
    "recieverPostBalance",
    "reconStatus",
    "reconciliationFlag",
    "refTransferID",
    "referenceID",
    "referenceNum",
    "reqQuantity",
    "requestGatewayCode",
    "requestGatewayType",
    "requestedQuantity",
    "requestedQuantityAsString",
    "requiredQuantity",
    "reversalFlag",
    "secondApprovalLimit",
    "secondApprovalRemark",
    "secondApprovedBy",
    "secondApprovedByName",
    "secondApprovedOn",
    "secondApprovedOnAsString",
    "secondLang",
    "segment",
    "selectorName",
    "senderCatName",
    "senderCategory",
    "senderDrQty",
    "senderDrQtyAsString",
    "senderGradeCode",
    "senderLoginID",
    "senderPostStock",
    "senderPostbalance",
    "senderPreviousStock",
    "senderTxnProfile",
    "senderTxnProfileName",
    "serviceClass",
    "serviceTypeName",
    "slabslist",
    "sosFlag",
    "sosProductCode",
    "sosRequestAmount",
    "sosSettlementDate",
    "sosStatus",
    "sosTxnId",
    "source",
    "state",
    "status",
    "statusDesc",
    "stockBought",
    "stockReturn",
    "stockUpdated",
    "subSid",
    "switchId",
    "targetAchieved",
    "tax1Rate",
    "tax1Type",
    "tax1Value",
    "tax2Rate",
    "tax2Type",
    "tax2Value",
    "tax3ValueAsString",
    "thirdApprovalRemark",
    "thirdApprovedBy",
    "thirdApprovedByName",
    "thirdApprovedOn",
    "time",
    "toCategoryDesc",
    "toChannelUserStatus",
    "toCommissionProfileIDDesc",
    "toEXTCODE",
    "toGradeCodeDesc",
    "toMSISDN",
    "toMsisdn",
    "toOwnerGeo",
    "toPrimaryMSISDN",
    "toTxnProfileDesc",
    "toUserCode",
    "toUserGeo",
    "toUserID",
    "toUserMsisdn",
    "toUserName",
    "to_serial_no",
    "totalTax1",
    "totalTax2",
    "totalTax3",
    "total_no_of_vouchers",
    "transInAmount",
    "transInCount",
    "transOutAmount",
    "transOutCount",
    "transactionCode",
    "transactionCount",
    "transactionMode",
    "transferAmt",
    "transferCategory",
    "transferCategoryCode",
    "transferCategoryCodeDesc",
    "transferDate",
    "transferDateAsString",
    "transferID",
    "transferInitatedBy",
    "transferInitatedByName",
    "transferMRP",
    "transferMRPAsString",
    "transferProfileID",
    "transferSubType",
    "transferSubTypeAsString",
    "transferSubTypeValue",
    "transferType",
    "type",
    "unitValue",
    "uploadedFile",
    "uploadedFileName",
    "uploadedFilePath",
    "userID",
    "userMsisdn",
    "userName",
    "userOTFCountsVO",
    "userWalletCode",
    "userrevlist",
    "voucherTypeList",
    "voucher_type",
    "walletType",
    "web"
})
public class TxnDetails {

    @JsonProperty("_transferMRPReplica")
    private String transferMRPReplica;
    @JsonProperty("activeUserId")
    private String activeUserId;
    @JsonProperty("activeUserName")
    private String activeUserName;
    @JsonProperty("address1")
    private String address1;
    @JsonProperty("address2")
    private String address2;
    @JsonProperty("adjustmentID")
    private String adjustmentID;
    @JsonProperty("apprRejStatus")
    private String apprRejStatus;
    @JsonProperty("approvalFile")
    private ApprovalFile approvalFile;
    @JsonProperty("approvedAmount")
    private String approvedAmount;
    @JsonProperty("approvedQuantity")
    private String approvedQuantity;
    @JsonProperty("batchDate")
    private String batchDate;
    @JsonProperty("batchNum")
    private String batchNum;
    @JsonProperty("batch_no")
    private String batchNo;
    @JsonProperty("batch_type")
    private String batchType;
    @JsonProperty("bundleType")
    private boolean bundleType;
    @JsonProperty("c2cReturnPlusWithCount")
    private String c2cReturnPlusWithCount;
    @JsonProperty("c2cReturnPlusWithINAmount")
    private String c2cReturnPlusWithINAmount;
    @JsonProperty("c2cReturnWithOutAmount")
    private String c2cReturnWithOutAmount;
    @JsonProperty("c2cReturnWithOutCount")
    private String c2cReturnWithOutCount;
    @JsonProperty("c2cTransferInAmount")
    private String c2cTransferInAmount;
    @JsonProperty("c2cTransferInCount")
    private String c2cTransferInCount;
    @JsonProperty("c2cTransferOutAmount")
    private String c2cTransferOutAmount;
    @JsonProperty("c2cTransferOutCount")
    private String c2cTransferOutCount;
    @JsonProperty("c2sTransferAmount")
    private String c2sTransferAmount;
    @JsonProperty("c2sTransferOutCount")
    private String c2sTransferOutCount;
    @JsonProperty("c2sTransfers")
    private String c2sTransfers;
    @JsonProperty("canceledBy")
    private String canceledBy;
    @JsonProperty("canceledByApprovedName")
    private String canceledByApprovedName;
    @JsonProperty("canceledOn")
    private String canceledOn;
    @JsonProperty("categoryCode")
    private String categoryCode;
    @JsonProperty("categoryName")
    private String categoryName;
    @JsonProperty("cellId")
    private String cellId;
    @JsonProperty("channelRemarks")
    private String channelRemarks;
    @JsonProperty("channelReturn")
    private String channelReturn;
    @JsonProperty("channelSoSVOList")
    private List<ChannelSoSVOList> channelSoSVOList = null;
    @JsonProperty("channelTransferList")
    private List<ChannelTransferList> channelTransferList = null;
    @JsonProperty("channelTransferitemsVOList")
    private List<ChannelTransferitemsVOList> channelTransferitemsVOList = null;
    @JsonProperty("channelTransferitemsVOListforOTF")
    private List<ChannelTransferitemsVOListforOTF> channelTransferitemsVOListforOTF = null;
    @JsonProperty("channelTransfers")
    private String channelTransfers;
    @JsonProperty("channelUserStatus")
    private String channelUserStatus;
    @JsonProperty("channelVoucherItemsVoList")
    private List<ChannelVoucherItemsVoList> channelVoucherItemsVoList = null;
    @JsonProperty("city")
    private String city;
    @JsonProperty("closeDate")
    private String closeDate;
    @JsonProperty("closingBalance")
    private String closingBalance;
    @JsonProperty("commProfileDetailID")
    private String commProfileDetailID;
    @JsonProperty("commProfileName")
    private String commProfileName;
    @JsonProperty("commProfileSetId")
    private String commProfileSetId;
    @JsonProperty("commProfileVersion")
    private String commProfileVersion;
    @JsonProperty("commQty")
    private String commQty;
    @JsonProperty("commQtyAsString")
    private String commQtyAsString;
    @JsonProperty("commisionTxnId")
    private String commisionTxnId;
    @JsonProperty("commissionRate")
    private String commissionRate;
    @JsonProperty("commissionType")
    private String commissionType;
    @JsonProperty("commissionValue")
    private String commissionValue;
    @JsonProperty("controlTransfer")
    private String controlTransfer;
    @JsonProperty("country")
    private String country;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdOn")
    private String createdOn;
    @JsonProperty("curStatus")
    private String curStatus;
    @JsonProperty("dbdateTime")
    private DbdateTime dbdateTime;
    @JsonProperty("defaultLang")
    private String defaultLang;
    @JsonProperty("differentialAmount")
    private String differentialAmount;
    @JsonProperty("displayTransferMRP")
    private String displayTransferMRP;
    @JsonProperty("domainCode")
    private String domainCode;
    @JsonProperty("domainCodeDesc")
    private String domainCodeDesc;
    @JsonProperty("domainName")
    private String domainName;
    @JsonProperty("dualCommissionType")
    private String dualCommissionType;
    @JsonProperty("email")
    private String email;
    @JsonProperty("entryType")
    private String entryType;
    @JsonProperty("erpNum")
    private String erpNum;
    @JsonProperty("externalCode")
    private String externalCode;
    @JsonProperty("externalTranDate")
    private String externalTranDate;
    @JsonProperty("externalTxnDate")
    private String externalTxnDate;
    @JsonProperty("externalTxnDateAsString")
    private String externalTxnDateAsString;
    @JsonProperty("externalTxnNum")
    private String externalTxnNum;
    @JsonProperty("fileUploaded")
    private boolean fileUploaded;
    @JsonProperty("finalApprovedBy")
    private String finalApprovedBy;
    @JsonProperty("finalApprovedDateAsString")
    private String finalApprovedDateAsString;
    @JsonProperty("firstApprovalRemark")
    private String firstApprovalRemark;
    @JsonProperty("firstApprovedBy")
    private String firstApprovedBy;
    @JsonProperty("firstApprovedByName")
    private String firstApprovedByName;
    @JsonProperty("firstApprovedOn")
    private String firstApprovedOn;
    @JsonProperty("firstApprovedOnAsString")
    private String firstApprovedOnAsString;
    @JsonProperty("firstApproverLimit")
    private String firstApproverLimit;
    @JsonProperty("fromCategoryDesc")
    private String fromCategoryDesc;
    @JsonProperty("fromChannelUserStatus")
    private String fromChannelUserStatus;
    @JsonProperty("fromCommissionProfileIDDesc")
    private String fromCommissionProfileIDDesc;
    @JsonProperty("fromEXTCODE")
    private String fromEXTCODE;
    @JsonProperty("fromGradeCodeDesc")
    private String fromGradeCodeDesc;
    @JsonProperty("fromMsisdn")
    private String fromMsisdn;
    @JsonProperty("fromOwnerGeo")
    private String fromOwnerGeo;
    @JsonProperty("fromTxnProfileDesc")
    private String fromTxnProfileDesc;
    @JsonProperty("fromUserCode")
    private String fromUserCode;
    @JsonProperty("fromUserGeo")
    private String fromUserGeo;
    @JsonProperty("fromUserID")
    private String fromUserID;
    @JsonProperty("fromUserName")
    private String fromUserName;
    @JsonProperty("from_serial_no")
    private String fromSerialNo;
    @JsonProperty("fullAddress")
    private String fullAddress;
    @JsonProperty("gegoraphyDomainName")
    private String gegoraphyDomainName;
    @JsonProperty("grandGeo")
    private String grandGeo;
    @JsonProperty("grandMsisdn")
    private String grandMsisdn;
    @JsonProperty("grandName")
    private String grandName;
    @JsonProperty("grandcategory")
    private String grandcategory;
    @JsonProperty("graphicalDomainCode")
    private String graphicalDomainCode;
    @JsonProperty("grphDomainCodeDesc")
    private String grphDomainCodeDesc;
    @JsonProperty("index")
    private String index;
    @JsonProperty("info1")
    private String info1;
    @JsonProperty("info10")
    private String info10;
    @JsonProperty("info2")
    private String info2;
    @JsonProperty("info3")
    private String info3;
    @JsonProperty("info4")
    private String info4;
    @JsonProperty("info5")
    private String info5;
    @JsonProperty("info6")
    private String info6;
    @JsonProperty("info7")
    private String info7;
    @JsonProperty("info8")
    private String info8;
    @JsonProperty("info9")
    private String info9;
    @JsonProperty("isFileC2C")
    private String isFileC2C;
    @JsonProperty("lastModifiedTime")
    private String lastModifiedTime;
    @JsonProperty("levelOneApprovedQuantity")
    private String levelOneApprovedQuantity;
    @JsonProperty("levelThreeApprovedQuantity")
    private String levelThreeApprovedQuantity;
    @JsonProperty("levelTwoApprovedQuantity")
    private String levelTwoApprovedQuantity;
    @JsonProperty("loginID")
    private String loginID;
    @JsonProperty("lrWithdrawAmt")
    private String lrWithdrawAmt;
    @JsonProperty("lrflag")
    private boolean lrflag;
    @JsonProperty("lrstatus")
    private String lrstatus;
    @JsonProperty("marginAmount")
    private String marginAmount;
    @JsonProperty("marginRate")
    private String marginRate;
    @JsonProperty("messageArgumentList")
    private List<String> messageArgumentList = null;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("modifiedOn")
    private String modifiedOn;
    @JsonProperty("modifiedOnAsString")
    private String modifiedOnAsString;
    @JsonProperty("mrp")
    private String mrp;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("name")
    private String name;
    @JsonProperty("netBalance")
    private String netBalance;
    @JsonProperty("netLifting")
    private String netLifting;
    @JsonProperty("netPayableAmount")
    private String netPayableAmount;
    @JsonProperty("netPayableAmountAsStr")
    private String netPayableAmountAsStr;
    @JsonProperty("netPayableAmountAsString")
    private String netPayableAmountAsString;
    @JsonProperty("netPayableAmounts")
    private String netPayableAmounts;
    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("networkCodeFor")
    private String networkCodeFor;
    @JsonProperty("ntpybleAmt")
    private String ntpybleAmt;
    @JsonProperty("o2cReturnPlusWithoutAmount")
    private String o2cReturnPlusWithoutAmount;
    @JsonProperty("o2cReturnPlusWithoutCount")
    private String o2cReturnPlusWithoutCount;
    @JsonProperty("o2cTransferInAmount")
    private String o2cTransferInAmount;
    @JsonProperty("o2cTransferInCount")
    private String o2cTransferInCount;
    @JsonProperty("openingBalance")
    private String openingBalance;
    @JsonProperty("otfAmount")
    private String otfAmount;
    @JsonProperty("otfCountsUpdated")
    private boolean otfCountsUpdated;
    @JsonProperty("otfFlag")
    private boolean otfFlag;
    @JsonProperty("otfRate")
    private String otfRate;
    @JsonProperty("otfType")
    private String otfType;
    @JsonProperty("otfTypePctOrAMt")
    private String otfTypePctOrAMt;
    @JsonProperty("ownerCat")
    private String ownerCat;
    @JsonProperty("ownerGeo")
    private String ownerGeo;
    @JsonProperty("ownerMsisdn")
    private String ownerMsisdn;
    @JsonProperty("ownerName")
    private String ownerName;
    @JsonProperty("ownerProfile")
    private String ownerProfile;
    @JsonProperty("ownerUser")
    private String ownerUser;
    @JsonProperty("parentCategory")
    private String parentCategory;
    @JsonProperty("parentGeoName")
    private String parentGeoName;
    @JsonProperty("parentMsisdn")
    private String parentMsisdn;
    @JsonProperty("parentName")
    private String parentName;
    @JsonProperty("parentProfile")
    private String parentProfile;
    @JsonProperty("payInstrumentAmt")
    private String payInstrumentAmt;
    @JsonProperty("payInstrumentDate")
    private String payInstrumentDate;
    @JsonProperty("payInstrumentDateAsString")
    private String payInstrumentDateAsString;
    @JsonProperty("payInstrumentName")
    private String payInstrumentName;
    @JsonProperty("payInstrumentNum")
    private String payInstrumentNum;
    @JsonProperty("payInstrumentStatus")
    private String payInstrumentStatus;
    @JsonProperty("payInstrumentType")
    private String payInstrumentType;
    @JsonProperty("payableAmount")
    private String payableAmount;
    @JsonProperty("payableAmountAsStr")
    private String payableAmountAsStr;
    @JsonProperty("payableAmountAsString")
    private String payableAmountAsString;
    @JsonProperty("payableAmounts")
    private String payableAmounts;
    @JsonProperty("paymentInstSource")
    private String paymentInstSource;
    @JsonProperty("paymentInstType")
    private String paymentInstType;
    @JsonProperty("previousStatus")
    private String previousStatus;
    @JsonProperty("productCode")
    private String productCode;
    @JsonProperty("productType")
    private String productType;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("profileNames")
    private String profileNames;
    @JsonProperty("pybleAmt")
    private String pybleAmt;
    @JsonProperty("pyinsAmt")
    private String pyinsAmt;
    @JsonProperty("receiverCategoryCode")
    private String receiverCategoryCode;
    @JsonProperty("receiverCategoryDesc")
    private String receiverCategoryDesc;
    @JsonProperty("receiverCrQty")
    private String receiverCrQty;
    @JsonProperty("receiverCrQtyAsString")
    private String receiverCrQtyAsString;
    @JsonProperty("receiverDomainCode")
    private String receiverDomainCode;
    @JsonProperty("receiverGgraphicalDomainCode")
    private String receiverGgraphicalDomainCode;
    @JsonProperty("receiverGradeCode")
    private String receiverGradeCode;
    @JsonProperty("receiverGradeCodeDesc")
    private String receiverGradeCodeDesc;
    @JsonProperty("receiverLoginID")
    private String receiverLoginID;
    @JsonProperty("receiverMsisdn")
    private String receiverMsisdn;
    @JsonProperty("receiverPostStock")
    private String receiverPostStock;
    @JsonProperty("receiverPreviousStock")
    private String receiverPreviousStock;
    @JsonProperty("receiverTxnProfile")
    private String receiverTxnProfile;
    @JsonProperty("receiverTxnProfileName")
    private String receiverTxnProfileName;
    @JsonProperty("recieverPostBalance")
    private String recieverPostBalance;
    @JsonProperty("reconStatus")
    private String reconStatus;
    @JsonProperty("reconciliationFlag")
    private boolean reconciliationFlag;
    @JsonProperty("refTransferID")
    private String refTransferID;
    @JsonProperty("referenceID")
    private String referenceID;
    @JsonProperty("referenceNum")
    private String referenceNum;
    @JsonProperty("reqQuantity")
    private String reqQuantity;
    @JsonProperty("requestGatewayCode")
    private String requestGatewayCode;
    @JsonProperty("requestGatewayType")
    private String requestGatewayType;
    @JsonProperty("requestedQuantity")
    private String requestedQuantity;
    @JsonProperty("requestedQuantityAsString")
    private String requestedQuantityAsString;
    @JsonProperty("requiredQuantity")
    private String requiredQuantity;
    @JsonProperty("reversalFlag")
    private boolean reversalFlag;
    @JsonProperty("secondApprovalLimit")
    private String secondApprovalLimit;
    @JsonProperty("secondApprovalRemark")
    private String secondApprovalRemark;
    @JsonProperty("secondApprovedBy")
    private String secondApprovedBy;
    @JsonProperty("secondApprovedByName")
    private String secondApprovedByName;
    @JsonProperty("secondApprovedOn")
    private String secondApprovedOn;
    @JsonProperty("secondApprovedOnAsString")
    private String secondApprovedOnAsString;
    @JsonProperty("secondLang")
    private String secondLang;
    @JsonProperty("segment")
    private String segment;
    @JsonProperty("selectorName")
    private String selectorName;
    @JsonProperty("senderCatName")
    private String senderCatName;
    @JsonProperty("senderCategory")
    private String senderCategory;
    @JsonProperty("senderDrQty")
    private String senderDrQty;
    @JsonProperty("senderDrQtyAsString")
    private String senderDrQtyAsString;
    @JsonProperty("senderGradeCode")
    private String senderGradeCode;
    @JsonProperty("senderLoginID")
    private String senderLoginID;
    @JsonProperty("senderPostStock")
    private String senderPostStock;
    @JsonProperty("senderPostbalance")
    private String senderPostbalance;
    @JsonProperty("senderPreviousStock")
    private String senderPreviousStock;
    @JsonProperty("senderTxnProfile")
    private String senderTxnProfile;
    @JsonProperty("senderTxnProfileName")
    private String senderTxnProfileName;
    @JsonProperty("serviceClass")
    private String serviceClass;
    @JsonProperty("serviceTypeName")
    private String serviceTypeName;
    @JsonProperty("slabslist")
    private List<Slabslist> slabslist = null;
    @JsonProperty("sosFlag")
    private boolean sosFlag;
    @JsonProperty("sosProductCode")
    private String sosProductCode;
    @JsonProperty("sosRequestAmount")
    private String sosRequestAmount;
    @JsonProperty("sosSettlementDate")
    private String sosSettlementDate;
    @JsonProperty("sosStatus")
    private String sosStatus;
    @JsonProperty("sosTxnId")
    private String sosTxnId;
    @JsonProperty("source")
    private String source;
    @JsonProperty("state")
    private String state;
    @JsonProperty("status")
    private String status;
    @JsonProperty("statusDesc")
    private String statusDesc;
    @JsonProperty("stockBought")
    private String stockBought;
    @JsonProperty("stockReturn")
    private String stockReturn;
    @JsonProperty("stockUpdated")
    private String stockUpdated;
    @JsonProperty("subSid")
    private String subSid;
    @JsonProperty("switchId")
    private String switchId;
    @JsonProperty("targetAchieved")
    private boolean targetAchieved;
    @JsonProperty("tax1Rate")
    private String tax1Rate;
    @JsonProperty("tax1Type")
    private String tax1Type;
    @JsonProperty("tax1Value")
    private String tax1Value;
    @JsonProperty("tax2Rate")
    private String tax2Rate;
    @JsonProperty("tax2Type")
    private String tax2Type;
    @JsonProperty("tax2Value")
    private String tax2Value;
    @JsonProperty("tax3ValueAsString")
    private String tax3ValueAsString;
    @JsonProperty("thirdApprovalRemark")
    private String thirdApprovalRemark;
    @JsonProperty("thirdApprovedBy")
    private String thirdApprovedBy;
    @JsonProperty("thirdApprovedByName")
    private String thirdApprovedByName;
    @JsonProperty("thirdApprovedOn")
    private String thirdApprovedOn;
    @JsonProperty("time")
    private String time;
    @JsonProperty("toCategoryDesc")
    private String toCategoryDesc;
    @JsonProperty("toChannelUserStatus")
    private String toChannelUserStatus;
    @JsonProperty("toCommissionProfileIDDesc")
    private String toCommissionProfileIDDesc;
    @JsonProperty("toEXTCODE")
    private String toEXTCODE;
    @JsonProperty("toGradeCodeDesc")
    private String toGradeCodeDesc;
    @JsonProperty("toMSISDN")
    private String toMSISDN;
    @JsonProperty("toMsisdn")
    private String toMsisdn;
    @JsonProperty("toOwnerGeo")
    private String toOwnerGeo;
    @JsonProperty("toPrimaryMSISDN")
    private String toPrimaryMSISDN;
    @JsonProperty("toTxnProfileDesc")
    private String toTxnProfileDesc;
    @JsonProperty("toUserCode")
    private String toUserCode;
    @JsonProperty("toUserGeo")
    private String toUserGeo;
    @JsonProperty("toUserID")
    private String toUserID;
    @JsonProperty("toUserMsisdn")
    private String toUserMsisdn;
    @JsonProperty("toUserName")
    private String toUserName;
    @JsonProperty("to_serial_no")
    private String toSerialNo;
    @JsonProperty("totalTax1")
    private String totalTax1;
    @JsonProperty("totalTax2")
    private String totalTax2;
    @JsonProperty("totalTax3")
    private String totalTax3;
    @JsonProperty("total_no_of_vouchers")
    private String totalNoOfVouchers;
    @JsonProperty("transInAmount")
    private String transInAmount;
    @JsonProperty("transInCount")
    private String transInCount;
    @JsonProperty("transOutAmount")
    private String transOutAmount;
    @JsonProperty("transOutCount")
    private String transOutCount;
    @JsonProperty("transactionCode")
    private String transactionCode;
    @JsonProperty("transactionCount")
    private String transactionCount;
    @JsonProperty("transactionMode")
    private String transactionMode;
    @JsonProperty("transferAmt")
    private String transferAmt;
    @JsonProperty("transferCategory")
    private String transferCategory;
    @JsonProperty("transferCategoryCode")
    private String transferCategoryCode;
    @JsonProperty("transferCategoryCodeDesc")
    private String transferCategoryCodeDesc;
    @JsonProperty("transferDate")
    private String transferDate;
    @JsonProperty("transferDateAsString")
    private String transferDateAsString;
    @JsonProperty("transferID")
    private String transferID;
    @JsonProperty("transferInitatedBy")
    private String transferInitatedBy;
    @JsonProperty("transferInitatedByName")
    private String transferInitatedByName;
    @JsonProperty("transferMRP")
    private String transferMRP;
    @JsonProperty("transferMRPAsString")
    private String transferMRPAsString;
    @JsonProperty("transferProfileID")
    private String transferProfileID;
    @JsonProperty("transferSubType")
    private String transferSubType;
    @JsonProperty("transferSubTypeAsString")
    private String transferSubTypeAsString;
    @JsonProperty("transferSubTypeValue")
    private String transferSubTypeValue;
    @JsonProperty("transferType")
    private String transferType;
    @JsonProperty("type")
    private String type;
    @JsonProperty("unitValue")
    private String unitValue;
    @JsonProperty("uploadedFile")
    private UploadedFile uploadedFile;
    @JsonProperty("uploadedFileName")
    private String uploadedFileName;
    @JsonProperty("uploadedFilePath")
    private String uploadedFilePath;
    @JsonProperty("userID")
    private String userID;
    @JsonProperty("userMsisdn")
    private String userMsisdn;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("userOTFCountsVO")
    private UserOTFCountsVO_ userOTFCountsVO;
    @JsonProperty("userWalletCode")
    private String userWalletCode;
    @JsonProperty("userrevlist")
    private List<Userrevlist> userrevlist = null;
    @JsonProperty("voucherTypeList")
    private List<VoucherTypeList> voucherTypeList = null;
    @JsonProperty("voucher_type")
    private String voucherType;
    @JsonProperty("walletType")
    private String walletType;
    @JsonProperty("web")
    private boolean web;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("_transferMRPReplica")
    public String getTransferMRPReplica() {
        return transferMRPReplica;
    }

    @JsonProperty("_transferMRPReplica")
    public void setTransferMRPReplica(String transferMRPReplica) {
        this.transferMRPReplica = transferMRPReplica;
    }

    @JsonProperty("activeUserId")
    public String getActiveUserId() {
        return activeUserId;
    }

    @JsonProperty("activeUserId")
    public void setActiveUserId(String activeUserId) {
        this.activeUserId = activeUserId;
    }

    @JsonProperty("activeUserName")
    public String getActiveUserName() {
        return activeUserName;
    }

    @JsonProperty("activeUserName")
    public void setActiveUserName(String activeUserName) {
        this.activeUserName = activeUserName;
    }

    @JsonProperty("address1")
    public String getAddress1() {
        return address1;
    }

    @JsonProperty("address1")
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    @JsonProperty("address2")
    public String getAddress2() {
        return address2;
    }

    @JsonProperty("address2")
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    @JsonProperty("adjustmentID")
    public String getAdjustmentID() {
        return adjustmentID;
    }

    @JsonProperty("adjustmentID")
    public void setAdjustmentID(String adjustmentID) {
        this.adjustmentID = adjustmentID;
    }

    @JsonProperty("apprRejStatus")
    public String getApprRejStatus() {
        return apprRejStatus;
    }

    @JsonProperty("apprRejStatus")
    public void setApprRejStatus(String apprRejStatus) {
        this.apprRejStatus = apprRejStatus;
    }

    @JsonProperty("approvalFile")
    public ApprovalFile getApprovalFile() {
        return approvalFile;
    }

    @JsonProperty("approvalFile")
    public void setApprovalFile(ApprovalFile approvalFile) {
        this.approvalFile = approvalFile;
    }

    @JsonProperty("approvedAmount")
    public String getApprovedAmount() {
        return approvedAmount;
    }

    @JsonProperty("approvedAmount")
    public void setApprovedAmount(String approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    @JsonProperty("approvedQuantity")
    public String getApprovedQuantity() {
        return approvedQuantity;
    }

    @JsonProperty("approvedQuantity")
    public void setApprovedQuantity(String approvedQuantity) {
        this.approvedQuantity = approvedQuantity;
    }

    @JsonProperty("batchDate")
    public String getBatchDate() {
        return batchDate;
    }

    @JsonProperty("batchDate")
    public void setBatchDate(String batchDate) {
        this.batchDate = batchDate;
    }

    @JsonProperty("batchNum")
    public String getBatchNum() {
        return batchNum;
    }

    @JsonProperty("batchNum")
    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    @JsonProperty("batch_no")
    public String getBatchNo() {
        return batchNo;
    }

    @JsonProperty("batch_no")
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    @JsonProperty("batch_type")
    public String getBatchType() {
        return batchType;
    }

    @JsonProperty("batch_type")
    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }

    @JsonProperty("bundleType")
    public boolean isBundleType() {
        return bundleType;
    }

    @JsonProperty("bundleType")
    public void setBundleType(boolean bundleType) {
        this.bundleType = bundleType;
    }

    @JsonProperty("c2cReturnPlusWithCount")
    public String getC2cReturnPlusWithCount() {
        return c2cReturnPlusWithCount;
    }

    @JsonProperty("c2cReturnPlusWithCount")
    public void setC2cReturnPlusWithCount(String c2cReturnPlusWithCount) {
        this.c2cReturnPlusWithCount = c2cReturnPlusWithCount;
    }

    @JsonProperty("c2cReturnPlusWithINAmount")
    public String getC2cReturnPlusWithINAmount() {
        return c2cReturnPlusWithINAmount;
    }

    @JsonProperty("c2cReturnPlusWithINAmount")
    public void setC2cReturnPlusWithINAmount(String c2cReturnPlusWithINAmount) {
        this.c2cReturnPlusWithINAmount = c2cReturnPlusWithINAmount;
    }

    @JsonProperty("c2cReturnWithOutAmount")
    public String getC2cReturnWithOutAmount() {
        return c2cReturnWithOutAmount;
    }

    @JsonProperty("c2cReturnWithOutAmount")
    public void setC2cReturnWithOutAmount(String c2cReturnWithOutAmount) {
        this.c2cReturnWithOutAmount = c2cReturnWithOutAmount;
    }

    @JsonProperty("c2cReturnWithOutCount")
    public String getC2cReturnWithOutCount() {
        return c2cReturnWithOutCount;
    }

    @JsonProperty("c2cReturnWithOutCount")
    public void setC2cReturnWithOutCount(String c2cReturnWithOutCount) {
        this.c2cReturnWithOutCount = c2cReturnWithOutCount;
    }

    @JsonProperty("c2cTransferInAmount")
    public String getC2cTransferInAmount() {
        return c2cTransferInAmount;
    }

    @JsonProperty("c2cTransferInAmount")
    public void setC2cTransferInAmount(String c2cTransferInAmount) {
        this.c2cTransferInAmount = c2cTransferInAmount;
    }

    @JsonProperty("c2cTransferInCount")
    public String getC2cTransferInCount() {
        return c2cTransferInCount;
    }

    @JsonProperty("c2cTransferInCount")
    public void setC2cTransferInCount(String c2cTransferInCount) {
        this.c2cTransferInCount = c2cTransferInCount;
    }

    @JsonProperty("c2cTransferOutAmount")
    public String getC2cTransferOutAmount() {
        return c2cTransferOutAmount;
    }

    @JsonProperty("c2cTransferOutAmount")
    public void setC2cTransferOutAmount(String c2cTransferOutAmount) {
        this.c2cTransferOutAmount = c2cTransferOutAmount;
    }

    @JsonProperty("c2cTransferOutCount")
    public String getC2cTransferOutCount() {
        return c2cTransferOutCount;
    }

    @JsonProperty("c2cTransferOutCount")
    public void setC2cTransferOutCount(String c2cTransferOutCount) {
        this.c2cTransferOutCount = c2cTransferOutCount;
    }

    @JsonProperty("c2sTransferAmount")
    public String getC2sTransferAmount() {
        return c2sTransferAmount;
    }

    @JsonProperty("c2sTransferAmount")
    public void setC2sTransferAmount(String c2sTransferAmount) {
        this.c2sTransferAmount = c2sTransferAmount;
    }

    @JsonProperty("c2sTransferOutCount")
    public String getC2sTransferOutCount() {
        return c2sTransferOutCount;
    }

    @JsonProperty("c2sTransferOutCount")
    public void setC2sTransferOutCount(String c2sTransferOutCount) {
        this.c2sTransferOutCount = c2sTransferOutCount;
    }

    @JsonProperty("c2sTransfers")
    public String getC2sTransfers() {
        return c2sTransfers;
    }

    @JsonProperty("c2sTransfers")
    public void setC2sTransfers(String c2sTransfers) {
        this.c2sTransfers = c2sTransfers;
    }

    @JsonProperty("canceledBy")
    public String getCanceledBy() {
        return canceledBy;
    }

    @JsonProperty("canceledBy")
    public void setCanceledBy(String canceledBy) {
        this.canceledBy = canceledBy;
    }

    @JsonProperty("canceledByApprovedName")
    public String getCanceledByApprovedName() {
        return canceledByApprovedName;
    }

    @JsonProperty("canceledByApprovedName")
    public void setCanceledByApprovedName(String canceledByApprovedName) {
        this.canceledByApprovedName = canceledByApprovedName;
    }

    @JsonProperty("canceledOn")
    public String getCanceledOn() {
        return canceledOn;
    }

    @JsonProperty("canceledOn")
    public void setCanceledOn(String canceledOn) {
        this.canceledOn = canceledOn;
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

    @JsonProperty("cellId")
    public String getCellId() {
        return cellId;
    }

    @JsonProperty("cellId")
    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    @JsonProperty("channelRemarks")
    public String getChannelRemarks() {
        return channelRemarks;
    }

    @JsonProperty("channelRemarks")
    public void setChannelRemarks(String channelRemarks) {
        this.channelRemarks = channelRemarks;
    }

    @JsonProperty("channelReturn")
    public String getChannelReturn() {
        return channelReturn;
    }

    @JsonProperty("channelReturn")
    public void setChannelReturn(String channelReturn) {
        this.channelReturn = channelReturn;
    }

    @JsonProperty("channelSoSVOList")
    public List<ChannelSoSVOList> getChannelSoSVOList() {
        return channelSoSVOList;
    }

    @JsonProperty("channelSoSVOList")
    public void setChannelSoSVOList(List<ChannelSoSVOList> channelSoSVOList) {
        this.channelSoSVOList = channelSoSVOList;
    }

    @JsonProperty("channelTransferList")
    public List<ChannelTransferList> getChannelTransferList() {
        return channelTransferList;
    }

    @JsonProperty("channelTransferList")
    public void setChannelTransferList(List<ChannelTransferList> channelTransferList) {
        this.channelTransferList = channelTransferList;
    }

    @JsonProperty("channelTransferitemsVOList")
    public List<ChannelTransferitemsVOList> getChannelTransferitemsVOList() {
        return channelTransferitemsVOList;
    }

    @JsonProperty("channelTransferitemsVOList")
    public void setChannelTransferitemsVOList(List<ChannelTransferitemsVOList> channelTransferitemsVOList) {
        this.channelTransferitemsVOList = channelTransferitemsVOList;
    }

    @JsonProperty("channelTransferitemsVOListforOTF")
    public List<ChannelTransferitemsVOListforOTF> getChannelTransferitemsVOListforOTF() {
        return channelTransferitemsVOListforOTF;
    }

    @JsonProperty("channelTransferitemsVOListforOTF")
    public void setChannelTransferitemsVOListforOTF(List<ChannelTransferitemsVOListforOTF> channelTransferitemsVOListforOTF) {
        this.channelTransferitemsVOListforOTF = channelTransferitemsVOListforOTF;
    }

    @JsonProperty("channelTransfers")
    public String getChannelTransfers() {
        return channelTransfers;
    }

    @JsonProperty("channelTransfers")
    public void setChannelTransfers(String channelTransfers) {
        this.channelTransfers = channelTransfers;
    }

    @JsonProperty("channelUserStatus")
    public String getChannelUserStatus() {
        return channelUserStatus;
    }

    @JsonProperty("channelUserStatus")
    public void setChannelUserStatus(String channelUserStatus) {
        this.channelUserStatus = channelUserStatus;
    }

    @JsonProperty("channelVoucherItemsVoList")
    public List<ChannelVoucherItemsVoList> getChannelVoucherItemsVoList() {
        return channelVoucherItemsVoList;
    }

    @JsonProperty("channelVoucherItemsVoList")
    public void setChannelVoucherItemsVoList(List<ChannelVoucherItemsVoList> channelVoucherItemsVoList) {
        this.channelVoucherItemsVoList = channelVoucherItemsVoList;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("closeDate")
    public String getCloseDate() {
        return closeDate;
    }

    @JsonProperty("closeDate")
    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    @JsonProperty("closingBalance")
    public String getClosingBalance() {
        return closingBalance;
    }

    @JsonProperty("closingBalance")
    public void setClosingBalance(String closingBalance) {
        this.closingBalance = closingBalance;
    }

    @JsonProperty("commProfileDetailID")
    public String getCommProfileDetailID() {
        return commProfileDetailID;
    }

    @JsonProperty("commProfileDetailID")
    public void setCommProfileDetailID(String commProfileDetailID) {
        this.commProfileDetailID = commProfileDetailID;
    }

    @JsonProperty("commProfileName")
    public String getCommProfileName() {
        return commProfileName;
    }

    @JsonProperty("commProfileName")
    public void setCommProfileName(String commProfileName) {
        this.commProfileName = commProfileName;
    }

    @JsonProperty("commProfileSetId")
    public String getCommProfileSetId() {
        return commProfileSetId;
    }

    @JsonProperty("commProfileSetId")
    public void setCommProfileSetId(String commProfileSetId) {
        this.commProfileSetId = commProfileSetId;
    }

    @JsonProperty("commProfileVersion")
    public String getCommProfileVersion() {
        return commProfileVersion;
    }

    @JsonProperty("commProfileVersion")
    public void setCommProfileVersion(String commProfileVersion) {
        this.commProfileVersion = commProfileVersion;
    }

    @JsonProperty("commQty")
    public String getCommQty() {
        return commQty;
    }

    @JsonProperty("commQty")
    public void setCommQty(String commQty) {
        this.commQty = commQty;
    }

    @JsonProperty("commQtyAsString")
    public String getCommQtyAsString() {
        return commQtyAsString;
    }

    @JsonProperty("commQtyAsString")
    public void setCommQtyAsString(String commQtyAsString) {
        this.commQtyAsString = commQtyAsString;
    }

    @JsonProperty("commisionTxnId")
    public String getCommisionTxnId() {
        return commisionTxnId;
    }

    @JsonProperty("commisionTxnId")
    public void setCommisionTxnId(String commisionTxnId) {
        this.commisionTxnId = commisionTxnId;
    }

    @JsonProperty("commissionRate")
    public String getCommissionRate() {
        return commissionRate;
    }

    @JsonProperty("commissionRate")
    public void setCommissionRate(String commissionRate) {
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
    public String getCommissionValue() {
        return commissionValue;
    }

    @JsonProperty("commissionValue")
    public void setCommissionValue(String commissionValue) {
        this.commissionValue = commissionValue;
    }

    @JsonProperty("controlTransfer")
    public String getControlTransfer() {
        return controlTransfer;
    }

    @JsonProperty("controlTransfer")
    public void setControlTransfer(String controlTransfer) {
        this.controlTransfer = controlTransfer;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
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

    @JsonProperty("curStatus")
    public String getCurStatus() {
        return curStatus;
    }

    @JsonProperty("curStatus")
    public void setCurStatus(String curStatus) {
        this.curStatus = curStatus;
    }

    @JsonProperty("dbdateTime")
    public DbdateTime getDbdateTime() {
        return dbdateTime;
    }

    @JsonProperty("dbdateTime")
    public void setDbdateTime(DbdateTime dbdateTime) {
        this.dbdateTime = dbdateTime;
    }

    @JsonProperty("defaultLang")
    public String getDefaultLang() {
        return defaultLang;
    }

    @JsonProperty("defaultLang")
    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    @JsonProperty("differentialAmount")
    public String getDifferentialAmount() {
        return differentialAmount;
    }

    @JsonProperty("differentialAmount")
    public void setDifferentialAmount(String differentialAmount) {
        this.differentialAmount = differentialAmount;
    }

    @JsonProperty("displayTransferMRP")
    public String getDisplayTransferMRP() {
        return displayTransferMRP;
    }

    @JsonProperty("displayTransferMRP")
    public void setDisplayTransferMRP(String displayTransferMRP) {
        this.displayTransferMRP = displayTransferMRP;
    }

    @JsonProperty("domainCode")
    public String getDomainCode() {
        return domainCode;
    }

    @JsonProperty("domainCode")
    public void setDomainCode(String domainCode) {
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

    @JsonProperty("domainName")
    public String getDomainName() {
        return domainName;
    }

    @JsonProperty("domainName")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @JsonProperty("dualCommissionType")
    public String getDualCommissionType() {
        return dualCommissionType;
    }

    @JsonProperty("dualCommissionType")
    public void setDualCommissionType(String dualCommissionType) {
        this.dualCommissionType = dualCommissionType;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("entryType")
    public String getEntryType() {
        return entryType;
    }

    @JsonProperty("entryType")
    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    @JsonProperty("erpNum")
    public String getErpNum() {
        return erpNum;
    }

    @JsonProperty("erpNum")
    public void setErpNum(String erpNum) {
        this.erpNum = erpNum;
    }

    @JsonProperty("externalCode")
    public String getExternalCode() {
        return externalCode;
    }

    @JsonProperty("externalCode")
    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    @JsonProperty("externalTranDate")
    public String getExternalTranDate() {
        return externalTranDate;
    }

    @JsonProperty("externalTranDate")
    public void setExternalTranDate(String externalTranDate) {
        this.externalTranDate = externalTranDate;
    }

    @JsonProperty("externalTxnDate")
    public String getExternalTxnDate() {
        return externalTxnDate;
    }

    @JsonProperty("externalTxnDate")
    public void setExternalTxnDate(String externalTxnDate) {
        this.externalTxnDate = externalTxnDate;
    }

    @JsonProperty("externalTxnDateAsString")
    public String getExternalTxnDateAsString() {
        return externalTxnDateAsString;
    }

    @JsonProperty("externalTxnDateAsString")
    public void setExternalTxnDateAsString(String externalTxnDateAsString) {
        this.externalTxnDateAsString = externalTxnDateAsString;
    }

    @JsonProperty("externalTxnNum")
    public String getExternalTxnNum() {
        return externalTxnNum;
    }

    @JsonProperty("externalTxnNum")
    public void setExternalTxnNum(String externalTxnNum) {
        this.externalTxnNum = externalTxnNum;
    }

    @JsonProperty("fileUploaded")
    public boolean isFileUploaded() {
        return fileUploaded;
    }

    @JsonProperty("fileUploaded")
    public void setFileUploaded(boolean fileUploaded) {
        this.fileUploaded = fileUploaded;
    }

    @JsonProperty("finalApprovedBy")
    public String getFinalApprovedBy() {
        return finalApprovedBy;
    }

    @JsonProperty("finalApprovedBy")
    public void setFinalApprovedBy(String finalApprovedBy) {
        this.finalApprovedBy = finalApprovedBy;
    }

    @JsonProperty("finalApprovedDateAsString")
    public String getFinalApprovedDateAsString() {
        return finalApprovedDateAsString;
    }

    @JsonProperty("finalApprovedDateAsString")
    public void setFinalApprovedDateAsString(String finalApprovedDateAsString) {
        this.finalApprovedDateAsString = finalApprovedDateAsString;
    }

    @JsonProperty("firstApprovalRemark")
    public String getFirstApprovalRemark() {
        return firstApprovalRemark;
    }

    @JsonProperty("firstApprovalRemark")
    public void setFirstApprovalRemark(String firstApprovalRemark) {
        this.firstApprovalRemark = firstApprovalRemark;
    }

    @JsonProperty("firstApprovedBy")
    public String getFirstApprovedBy() {
        return firstApprovedBy;
    }

    @JsonProperty("firstApprovedBy")
    public void setFirstApprovedBy(String firstApprovedBy) {
        this.firstApprovedBy = firstApprovedBy;
    }

    @JsonProperty("firstApprovedByName")
    public String getFirstApprovedByName() {
        return firstApprovedByName;
    }

    @JsonProperty("firstApprovedByName")
    public void setFirstApprovedByName(String firstApprovedByName) {
        this.firstApprovedByName = firstApprovedByName;
    }

    @JsonProperty("firstApprovedOn")
    public String getFirstApprovedOn() {
        return firstApprovedOn;
    }

    @JsonProperty("firstApprovedOn")
    public void setFirstApprovedOn(String firstApprovedOn) {
        this.firstApprovedOn = firstApprovedOn;
    }

    @JsonProperty("firstApprovedOnAsString")
    public String getFirstApprovedOnAsString() {
        return firstApprovedOnAsString;
    }

    @JsonProperty("firstApprovedOnAsString")
    public void setFirstApprovedOnAsString(String firstApprovedOnAsString) {
        this.firstApprovedOnAsString = firstApprovedOnAsString;
    }

    @JsonProperty("firstApproverLimit")
    public String getFirstApproverLimit() {
        return firstApproverLimit;
    }

    @JsonProperty("firstApproverLimit")
    public void setFirstApproverLimit(String firstApproverLimit) {
        this.firstApproverLimit = firstApproverLimit;
    }

    @JsonProperty("fromCategoryDesc")
    public String getFromCategoryDesc() {
        return fromCategoryDesc;
    }

    @JsonProperty("fromCategoryDesc")
    public void setFromCategoryDesc(String fromCategoryDesc) {
        this.fromCategoryDesc = fromCategoryDesc;
    }

    @JsonProperty("fromChannelUserStatus")
    public String getFromChannelUserStatus() {
        return fromChannelUserStatus;
    }

    @JsonProperty("fromChannelUserStatus")
    public void setFromChannelUserStatus(String fromChannelUserStatus) {
        this.fromChannelUserStatus = fromChannelUserStatus;
    }

    @JsonProperty("fromCommissionProfileIDDesc")
    public String getFromCommissionProfileIDDesc() {
        return fromCommissionProfileIDDesc;
    }

    @JsonProperty("fromCommissionProfileIDDesc")
    public void setFromCommissionProfileIDDesc(String fromCommissionProfileIDDesc) {
        this.fromCommissionProfileIDDesc = fromCommissionProfileIDDesc;
    }

    @JsonProperty("fromEXTCODE")
    public String getFromEXTCODE() {
        return fromEXTCODE;
    }

    @JsonProperty("fromEXTCODE")
    public void setFromEXTCODE(String fromEXTCODE) {
        this.fromEXTCODE = fromEXTCODE;
    }

    @JsonProperty("fromGradeCodeDesc")
    public String getFromGradeCodeDesc() {
        return fromGradeCodeDesc;
    }

    @JsonProperty("fromGradeCodeDesc")
    public void setFromGradeCodeDesc(String fromGradeCodeDesc) {
        this.fromGradeCodeDesc = fromGradeCodeDesc;
    }

    @JsonProperty("fromMsisdn")
    public String getFromMsisdn() {
        return fromMsisdn;
    }

    @JsonProperty("fromMsisdn")
    public void setFromMsisdn(String fromMsisdn) {
        this.fromMsisdn = fromMsisdn;
    }

    @JsonProperty("fromOwnerGeo")
    public String getFromOwnerGeo() {
        return fromOwnerGeo;
    }

    @JsonProperty("fromOwnerGeo")
    public void setFromOwnerGeo(String fromOwnerGeo) {
        this.fromOwnerGeo = fromOwnerGeo;
    }

    @JsonProperty("fromTxnProfileDesc")
    public String getFromTxnProfileDesc() {
        return fromTxnProfileDesc;
    }

    @JsonProperty("fromTxnProfileDesc")
    public void setFromTxnProfileDesc(String fromTxnProfileDesc) {
        this.fromTxnProfileDesc = fromTxnProfileDesc;
    }

    @JsonProperty("fromUserCode")
    public String getFromUserCode() {
        return fromUserCode;
    }

    @JsonProperty("fromUserCode")
    public void setFromUserCode(String fromUserCode) {
        this.fromUserCode = fromUserCode;
    }

    @JsonProperty("fromUserGeo")
    public String getFromUserGeo() {
        return fromUserGeo;
    }

    @JsonProperty("fromUserGeo")
    public void setFromUserGeo(String fromUserGeo) {
        this.fromUserGeo = fromUserGeo;
    }

    @JsonProperty("fromUserID")
    public String getFromUserID() {
        return fromUserID;
    }

    @JsonProperty("fromUserID")
    public void setFromUserID(String fromUserID) {
        this.fromUserID = fromUserID;
    }

    @JsonProperty("fromUserName")
    public String getFromUserName() {
        return fromUserName;
    }

    @JsonProperty("fromUserName")
    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    @JsonProperty("from_serial_no")
    public String getFromSerialNo() {
        return fromSerialNo;
    }

    @JsonProperty("from_serial_no")
    public void setFromSerialNo(String fromSerialNo) {
        this.fromSerialNo = fromSerialNo;
    }

    @JsonProperty("fullAddress")
    public String getFullAddress() {
        return fullAddress;
    }

    @JsonProperty("fullAddress")
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @JsonProperty("gegoraphyDomainName")
    public String getGegoraphyDomainName() {
        return gegoraphyDomainName;
    }

    @JsonProperty("gegoraphyDomainName")
    public void setGegoraphyDomainName(String gegoraphyDomainName) {
        this.gegoraphyDomainName = gegoraphyDomainName;
    }

    @JsonProperty("grandGeo")
    public String getGrandGeo() {
        return grandGeo;
    }

    @JsonProperty("grandGeo")
    public void setGrandGeo(String grandGeo) {
        this.grandGeo = grandGeo;
    }

    @JsonProperty("grandMsisdn")
    public String getGrandMsisdn() {
        return grandMsisdn;
    }

    @JsonProperty("grandMsisdn")
    public void setGrandMsisdn(String grandMsisdn) {
        this.grandMsisdn = grandMsisdn;
    }

    @JsonProperty("grandName")
    public String getGrandName() {
        return grandName;
    }

    @JsonProperty("grandName")
    public void setGrandName(String grandName) {
        this.grandName = grandName;
    }

    @JsonProperty("grandcategory")
    public String getGrandcategory() {
        return grandcategory;
    }

    @JsonProperty("grandcategory")
    public void setGrandcategory(String grandcategory) {
        this.grandcategory = grandcategory;
    }

    @JsonProperty("graphicalDomainCode")
    public String getGraphicalDomainCode() {
        return graphicalDomainCode;
    }

    @JsonProperty("graphicalDomainCode")
    public void setGraphicalDomainCode(String graphicalDomainCode) {
        this.graphicalDomainCode = graphicalDomainCode;
    }

    @JsonProperty("grphDomainCodeDesc")
    public String getGrphDomainCodeDesc() {
        return grphDomainCodeDesc;
    }

    @JsonProperty("grphDomainCodeDesc")
    public void setGrphDomainCodeDesc(String grphDomainCodeDesc) {
        this.grphDomainCodeDesc = grphDomainCodeDesc;
    }

    @JsonProperty("index")
    public String getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(String index) {
        this.index = index;
    }

    @JsonProperty("info1")
    public String getInfo1() {
        return info1;
    }

    @JsonProperty("info1")
    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    @JsonProperty("info10")
    public String getInfo10() {
        return info10;
    }

    @JsonProperty("info10")
    public void setInfo10(String info10) {
        this.info10 = info10;
    }

    @JsonProperty("info2")
    public String getInfo2() {
        return info2;
    }

    @JsonProperty("info2")
    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    @JsonProperty("info3")
    public String getInfo3() {
        return info3;
    }

    @JsonProperty("info3")
    public void setInfo3(String info3) {
        this.info3 = info3;
    }

    @JsonProperty("info4")
    public String getInfo4() {
        return info4;
    }

    @JsonProperty("info4")
    public void setInfo4(String info4) {
        this.info4 = info4;
    }

    @JsonProperty("info5")
    public String getInfo5() {
        return info5;
    }

    @JsonProperty("info5")
    public void setInfo5(String info5) {
        this.info5 = info5;
    }

    @JsonProperty("info6")
    public String getInfo6() {
        return info6;
    }

    @JsonProperty("info6")
    public void setInfo6(String info6) {
        this.info6 = info6;
    }

    @JsonProperty("info7")
    public String getInfo7() {
        return info7;
    }

    @JsonProperty("info7")
    public void setInfo7(String info7) {
        this.info7 = info7;
    }

    @JsonProperty("info8")
    public String getInfo8() {
        return info8;
    }

    @JsonProperty("info8")
    public void setInfo8(String info8) {
        this.info8 = info8;
    }

    @JsonProperty("info9")
    public String getInfo9() {
        return info9;
    }

    @JsonProperty("info9")
    public void setInfo9(String info9) {
        this.info9 = info9;
    }

    @JsonProperty("isFileC2C")
    public String getIsFileC2C() {
        return isFileC2C;
    }

    @JsonProperty("isFileC2C")
    public void setIsFileC2C(String isFileC2C) {
        this.isFileC2C = isFileC2C;
    }

    @JsonProperty("lastModifiedTime")
    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    @JsonProperty("lastModifiedTime")
    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @JsonProperty("levelOneApprovedQuantity")
    public String getLevelOneApprovedQuantity() {
        return levelOneApprovedQuantity;
    }

    @JsonProperty("levelOneApprovedQuantity")
    public void setLevelOneApprovedQuantity(String levelOneApprovedQuantity) {
        this.levelOneApprovedQuantity = levelOneApprovedQuantity;
    }

    @JsonProperty("levelThreeApprovedQuantity")
    public String getLevelThreeApprovedQuantity() {
        return levelThreeApprovedQuantity;
    }

    @JsonProperty("levelThreeApprovedQuantity")
    public void setLevelThreeApprovedQuantity(String levelThreeApprovedQuantity) {
        this.levelThreeApprovedQuantity = levelThreeApprovedQuantity;
    }

    @JsonProperty("levelTwoApprovedQuantity")
    public String getLevelTwoApprovedQuantity() {
        return levelTwoApprovedQuantity;
    }

    @JsonProperty("levelTwoApprovedQuantity")
    public void setLevelTwoApprovedQuantity(String levelTwoApprovedQuantity) {
        this.levelTwoApprovedQuantity = levelTwoApprovedQuantity;
    }

    @JsonProperty("loginID")
    public String getLoginID() {
        return loginID;
    }

    @JsonProperty("loginID")
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    @JsonProperty("lrWithdrawAmt")
    public String getLrWithdrawAmt() {
        return lrWithdrawAmt;
    }

    @JsonProperty("lrWithdrawAmt")
    public void setLrWithdrawAmt(String lrWithdrawAmt) {
        this.lrWithdrawAmt = lrWithdrawAmt;
    }

    @JsonProperty("lrflag")
    public boolean isLrflag() {
        return lrflag;
    }

    @JsonProperty("lrflag")
    public void setLrflag(boolean lrflag) {
        this.lrflag = lrflag;
    }

    @JsonProperty("lrstatus")
    public String getLrstatus() {
        return lrstatus;
    }

    @JsonProperty("lrstatus")
    public void setLrstatus(String lrstatus) {
        this.lrstatus = lrstatus;
    }

    @JsonProperty("marginAmount")
    public String getMarginAmount() {
        return marginAmount;
    }

    @JsonProperty("marginAmount")
    public void setMarginAmount(String marginAmount) {
        this.marginAmount = marginAmount;
    }

    @JsonProperty("marginRate")
    public String getMarginRate() {
        return marginRate;
    }

    @JsonProperty("marginRate")
    public void setMarginRate(String marginRate) {
        this.marginRate = marginRate;
    }

    @JsonProperty("messageArgumentList")
    public List<String> getMessageArgumentList() {
        return messageArgumentList;
    }

    @JsonProperty("messageArgumentList")
    public void setMessageArgumentList(List<String> messageArgumentList) {
        this.messageArgumentList = messageArgumentList;
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

    @JsonProperty("modifiedOnAsString")
    public String getModifiedOnAsString() {
        return modifiedOnAsString;
    }

    @JsonProperty("modifiedOnAsString")
    public void setModifiedOnAsString(String modifiedOnAsString) {
        this.modifiedOnAsString = modifiedOnAsString;
    }

    @JsonProperty("mrp")
    public String getMrp() {
        return mrp;
    }

    @JsonProperty("mrp")
    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("netBalance")
    public String getNetBalance() {
        return netBalance;
    }

    @JsonProperty("netBalance")
    public void setNetBalance(String netBalance) {
        this.netBalance = netBalance;
    }

    @JsonProperty("netLifting")
    public String getNetLifting() {
        return netLifting;
    }

    @JsonProperty("netLifting")
    public void setNetLifting(String netLifting) {
        this.netLifting = netLifting;
    }

    @JsonProperty("netPayableAmount")
    public String getNetPayableAmount() {
        return netPayableAmount;
    }

    @JsonProperty("netPayableAmount")
    public void setNetPayableAmount(String netPayableAmount) {
        this.netPayableAmount = netPayableAmount;
    }

    @JsonProperty("netPayableAmountAsStr")
    public String getNetPayableAmountAsStr() {
        return netPayableAmountAsStr;
    }

    @JsonProperty("netPayableAmountAsStr")
    public void setNetPayableAmountAsStr(String netPayableAmountAsStr) {
        this.netPayableAmountAsStr = netPayableAmountAsStr;
    }

    @JsonProperty("netPayableAmountAsString")
    public String getNetPayableAmountAsString() {
        return netPayableAmountAsString;
    }

    @JsonProperty("netPayableAmountAsString")
    public void setNetPayableAmountAsString(String netPayableAmountAsString) {
        this.netPayableAmountAsString = netPayableAmountAsString;
    }

    @JsonProperty("netPayableAmounts")
    public String getNetPayableAmounts() {
        return netPayableAmounts;
    }

    @JsonProperty("netPayableAmounts")
    public void setNetPayableAmounts(String netPayableAmounts) {
        this.netPayableAmounts = netPayableAmounts;
    }

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("networkCodeFor")
    public String getNetworkCodeFor() {
        return networkCodeFor;
    }

    @JsonProperty("networkCodeFor")
    public void setNetworkCodeFor(String networkCodeFor) {
        this.networkCodeFor = networkCodeFor;
    }

    @JsonProperty("ntpybleAmt")
    public String getNtpybleAmt() {
        return ntpybleAmt;
    }

    @JsonProperty("ntpybleAmt")
    public void setNtpybleAmt(String ntpybleAmt) {
        this.ntpybleAmt = ntpybleAmt;
    }

    @JsonProperty("o2cReturnPlusWithoutAmount")
    public String getO2cReturnPlusWithoutAmount() {
        return o2cReturnPlusWithoutAmount;
    }

    @JsonProperty("o2cReturnPlusWithoutAmount")
    public void setO2cReturnPlusWithoutAmount(String o2cReturnPlusWithoutAmount) {
        this.o2cReturnPlusWithoutAmount = o2cReturnPlusWithoutAmount;
    }

    @JsonProperty("o2cReturnPlusWithoutCount")
    public String getO2cReturnPlusWithoutCount() {
        return o2cReturnPlusWithoutCount;
    }

    @JsonProperty("o2cReturnPlusWithoutCount")
    public void setO2cReturnPlusWithoutCount(String o2cReturnPlusWithoutCount) {
        this.o2cReturnPlusWithoutCount = o2cReturnPlusWithoutCount;
    }

    @JsonProperty("o2cTransferInAmount")
    public String getO2cTransferInAmount() {
        return o2cTransferInAmount;
    }

    @JsonProperty("o2cTransferInAmount")
    public void setO2cTransferInAmount(String o2cTransferInAmount) {
        this.o2cTransferInAmount = o2cTransferInAmount;
    }

    @JsonProperty("o2cTransferInCount")
    public String getO2cTransferInCount() {
        return o2cTransferInCount;
    }

    @JsonProperty("o2cTransferInCount")
    public void setO2cTransferInCount(String o2cTransferInCount) {
        this.o2cTransferInCount = o2cTransferInCount;
    }

    @JsonProperty("openingBalance")
    public String getOpeningBalance() {
        return openingBalance;
    }

    @JsonProperty("openingBalance")
    public void setOpeningBalance(String openingBalance) {
        this.openingBalance = openingBalance;
    }

    @JsonProperty("otfAmount")
    public String getOtfAmount() {
        return otfAmount;
    }

    @JsonProperty("otfAmount")
    public void setOtfAmount(String otfAmount) {
        this.otfAmount = otfAmount;
    }

    @JsonProperty("otfCountsUpdated")
    public boolean isOtfCountsUpdated() {
        return otfCountsUpdated;
    }

    @JsonProperty("otfCountsUpdated")
    public void setOtfCountsUpdated(boolean otfCountsUpdated) {
        this.otfCountsUpdated = otfCountsUpdated;
    }

    @JsonProperty("otfFlag")
    public boolean isOtfFlag() {
        return otfFlag;
    }

    @JsonProperty("otfFlag")
    public void setOtfFlag(boolean otfFlag) {
        this.otfFlag = otfFlag;
    }

    @JsonProperty("otfRate")
    public String getOtfRate() {
        return otfRate;
    }

    @JsonProperty("otfRate")
    public void setOtfRate(String otfRate) {
        this.otfRate = otfRate;
    }

    @JsonProperty("otfType")
    public String getOtfType() {
        return otfType;
    }

    @JsonProperty("otfType")
    public void setOtfType(String otfType) {
        this.otfType = otfType;
    }

    @JsonProperty("otfTypePctOrAMt")
    public String getOtfTypePctOrAMt() {
        return otfTypePctOrAMt;
    }

    @JsonProperty("otfTypePctOrAMt")
    public void setOtfTypePctOrAMt(String otfTypePctOrAMt) {
        this.otfTypePctOrAMt = otfTypePctOrAMt;
    }

    @JsonProperty("ownerCat")
    public String getOwnerCat() {
        return ownerCat;
    }

    @JsonProperty("ownerCat")
    public void setOwnerCat(String ownerCat) {
        this.ownerCat = ownerCat;
    }

    @JsonProperty("ownerGeo")
    public String getOwnerGeo() {
        return ownerGeo;
    }

    @JsonProperty("ownerGeo")
    public void setOwnerGeo(String ownerGeo) {
        this.ownerGeo = ownerGeo;
    }

    @JsonProperty("ownerMsisdn")
    public String getOwnerMsisdn() {
        return ownerMsisdn;
    }

    @JsonProperty("ownerMsisdn")
    public void setOwnerMsisdn(String ownerMsisdn) {
        this.ownerMsisdn = ownerMsisdn;
    }

    @JsonProperty("ownerName")
    public String getOwnerName() {
        return ownerName;
    }

    @JsonProperty("ownerName")
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @JsonProperty("ownerProfile")
    public String getOwnerProfile() {
        return ownerProfile;
    }

    @JsonProperty("ownerProfile")
    public void setOwnerProfile(String ownerProfile) {
        this.ownerProfile = ownerProfile;
    }

    @JsonProperty("ownerUser")
    public String getOwnerUser() {
        return ownerUser;
    }

    @JsonProperty("ownerUser")
    public void setOwnerUser(String ownerUser) {
        this.ownerUser = ownerUser;
    }

    @JsonProperty("parentCategory")
    public String getParentCategory() {
        return parentCategory;
    }

    @JsonProperty("parentCategory")
    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    @JsonProperty("parentGeoName")
    public String getParentGeoName() {
        return parentGeoName;
    }

    @JsonProperty("parentGeoName")
    public void setParentGeoName(String parentGeoName) {
        this.parentGeoName = parentGeoName;
    }

    @JsonProperty("parentMsisdn")
    public String getParentMsisdn() {
        return parentMsisdn;
    }

    @JsonProperty("parentMsisdn")
    public void setParentMsisdn(String parentMsisdn) {
        this.parentMsisdn = parentMsisdn;
    }

    @JsonProperty("parentName")
    public String getParentName() {
        return parentName;
    }

    @JsonProperty("parentName")
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @JsonProperty("parentProfile")
    public String getParentProfile() {
        return parentProfile;
    }

    @JsonProperty("parentProfile")
    public void setParentProfile(String parentProfile) {
        this.parentProfile = parentProfile;
    }

    @JsonProperty("payInstrumentAmt")
    public String getPayInstrumentAmt() {
        return payInstrumentAmt;
    }

    @JsonProperty("payInstrumentAmt")
    public void setPayInstrumentAmt(String payInstrumentAmt) {
        this.payInstrumentAmt = payInstrumentAmt;
    }

    @JsonProperty("payInstrumentDate")
    public String getPayInstrumentDate() {
        return payInstrumentDate;
    }

    @JsonProperty("payInstrumentDate")
    public void setPayInstrumentDate(String payInstrumentDate) {
        this.payInstrumentDate = payInstrumentDate;
    }

    @JsonProperty("payInstrumentDateAsString")
    public String getPayInstrumentDateAsString() {
        return payInstrumentDateAsString;
    }

    @JsonProperty("payInstrumentDateAsString")
    public void setPayInstrumentDateAsString(String payInstrumentDateAsString) {
        this.payInstrumentDateAsString = payInstrumentDateAsString;
    }

    @JsonProperty("payInstrumentName")
    public String getPayInstrumentName() {
        return payInstrumentName;
    }

    @JsonProperty("payInstrumentName")
    public void setPayInstrumentName(String payInstrumentName) {
        this.payInstrumentName = payInstrumentName;
    }

    @JsonProperty("payInstrumentNum")
    public String getPayInstrumentNum() {
        return payInstrumentNum;
    }

    @JsonProperty("payInstrumentNum")
    public void setPayInstrumentNum(String payInstrumentNum) {
        this.payInstrumentNum = payInstrumentNum;
    }

    @JsonProperty("payInstrumentStatus")
    public String getPayInstrumentStatus() {
        return payInstrumentStatus;
    }

    @JsonProperty("payInstrumentStatus")
    public void setPayInstrumentStatus(String payInstrumentStatus) {
        this.payInstrumentStatus = payInstrumentStatus;
    }

    @JsonProperty("payInstrumentType")
    public String getPayInstrumentType() {
        return payInstrumentType;
    }

    @JsonProperty("payInstrumentType")
    public void setPayInstrumentType(String payInstrumentType) {
        this.payInstrumentType = payInstrumentType;
    }

    @JsonProperty("payableAmount")
    public String getPayableAmount() {
        return payableAmount;
    }

    @JsonProperty("payableAmount")
    public void setPayableAmount(String payableAmount) {
        this.payableAmount = payableAmount;
    }

    @JsonProperty("payableAmountAsStr")
    public String getPayableAmountAsStr() {
        return payableAmountAsStr;
    }

    @JsonProperty("payableAmountAsStr")
    public void setPayableAmountAsStr(String payableAmountAsStr) {
        this.payableAmountAsStr = payableAmountAsStr;
    }

    @JsonProperty("payableAmountAsString")
    public String getPayableAmountAsString() {
        return payableAmountAsString;
    }

    @JsonProperty("payableAmountAsString")
    public void setPayableAmountAsString(String payableAmountAsString) {
        this.payableAmountAsString = payableAmountAsString;
    }

    @JsonProperty("payableAmounts")
    public String getPayableAmounts() {
        return payableAmounts;
    }

    @JsonProperty("payableAmounts")
    public void setPayableAmounts(String payableAmounts) {
        this.payableAmounts = payableAmounts;
    }

    @JsonProperty("paymentInstSource")
    public String getPaymentInstSource() {
        return paymentInstSource;
    }

    @JsonProperty("paymentInstSource")
    public void setPaymentInstSource(String paymentInstSource) {
        this.paymentInstSource = paymentInstSource;
    }

    @JsonProperty("paymentInstType")
    public String getPaymentInstType() {
        return paymentInstType;
    }

    @JsonProperty("paymentInstType")
    public void setPaymentInstType(String paymentInstType) {
        this.paymentInstType = paymentInstType;
    }

    @JsonProperty("previousStatus")
    public String getPreviousStatus() {
        return previousStatus;
    }

    @JsonProperty("previousStatus")
    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    @JsonProperty("productCode")
    public String getProductCode() {
        return productCode;
    }

    @JsonProperty("productCode")
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @JsonProperty("productType")
    public String getProductType() {
        return productType;
    }

    @JsonProperty("productType")
    public void setProductType(String productType) {
        this.productType = productType;
    }

    @JsonProperty("product_name")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("product_name")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @JsonProperty("profileNames")
    public String getProfileNames() {
        return profileNames;
    }

    @JsonProperty("profileNames")
    public void setProfileNames(String profileNames) {
        this.profileNames = profileNames;
    }

    @JsonProperty("pybleAmt")
    public String getPybleAmt() {
        return pybleAmt;
    }

    @JsonProperty("pybleAmt")
    public void setPybleAmt(String pybleAmt) {
        this.pybleAmt = pybleAmt;
    }

    @JsonProperty("pyinsAmt")
    public String getPyinsAmt() {
        return pyinsAmt;
    }

    @JsonProperty("pyinsAmt")
    public void setPyinsAmt(String pyinsAmt) {
        this.pyinsAmt = pyinsAmt;
    }

    @JsonProperty("receiverCategoryCode")
    public String getReceiverCategoryCode() {
        return receiverCategoryCode;
    }

    @JsonProperty("receiverCategoryCode")
    public void setReceiverCategoryCode(String receiverCategoryCode) {
        this.receiverCategoryCode = receiverCategoryCode;
    }

    @JsonProperty("receiverCategoryDesc")
    public String getReceiverCategoryDesc() {
        return receiverCategoryDesc;
    }

    @JsonProperty("receiverCategoryDesc")
    public void setReceiverCategoryDesc(String receiverCategoryDesc) {
        this.receiverCategoryDesc = receiverCategoryDesc;
    }

    @JsonProperty("receiverCrQty")
    public String getReceiverCrQty() {
        return receiverCrQty;
    }

    @JsonProperty("receiverCrQty")
    public void setReceiverCrQty(String receiverCrQty) {
        this.receiverCrQty = receiverCrQty;
    }

    @JsonProperty("receiverCrQtyAsString")
    public String getReceiverCrQtyAsString() {
        return receiverCrQtyAsString;
    }

    @JsonProperty("receiverCrQtyAsString")
    public void setReceiverCrQtyAsString(String receiverCrQtyAsString) {
        this.receiverCrQtyAsString = receiverCrQtyAsString;
    }

    @JsonProperty("receiverDomainCode")
    public String getReceiverDomainCode() {
        return receiverDomainCode;
    }

    @JsonProperty("receiverDomainCode")
    public void setReceiverDomainCode(String receiverDomainCode) {
        this.receiverDomainCode = receiverDomainCode;
    }

    @JsonProperty("receiverGgraphicalDomainCode")
    public String getReceiverGgraphicalDomainCode() {
        return receiverGgraphicalDomainCode;
    }

    @JsonProperty("receiverGgraphicalDomainCode")
    public void setReceiverGgraphicalDomainCode(String receiverGgraphicalDomainCode) {
        this.receiverGgraphicalDomainCode = receiverGgraphicalDomainCode;
    }

    @JsonProperty("receiverGradeCode")
    public String getReceiverGradeCode() {
        return receiverGradeCode;
    }

    @JsonProperty("receiverGradeCode")
    public void setReceiverGradeCode(String receiverGradeCode) {
        this.receiverGradeCode = receiverGradeCode;
    }

    @JsonProperty("receiverGradeCodeDesc")
    public String getReceiverGradeCodeDesc() {
        return receiverGradeCodeDesc;
    }

    @JsonProperty("receiverGradeCodeDesc")
    public void setReceiverGradeCodeDesc(String receiverGradeCodeDesc) {
        this.receiverGradeCodeDesc = receiverGradeCodeDesc;
    }

    @JsonProperty("receiverLoginID")
    public String getReceiverLoginID() {
        return receiverLoginID;
    }

    @JsonProperty("receiverLoginID")
    public void setReceiverLoginID(String receiverLoginID) {
        this.receiverLoginID = receiverLoginID;
    }

    @JsonProperty("receiverMsisdn")
    public String getReceiverMsisdn() {
        return receiverMsisdn;
    }

    @JsonProperty("receiverMsisdn")
    public void setReceiverMsisdn(String receiverMsisdn) {
        this.receiverMsisdn = receiverMsisdn;
    }

    @JsonProperty("receiverPostStock")
    public String getReceiverPostStock() {
        return receiverPostStock;
    }

    @JsonProperty("receiverPostStock")
    public void setReceiverPostStock(String receiverPostStock) {
        this.receiverPostStock = receiverPostStock;
    }

    @JsonProperty("receiverPreviousStock")
    public String getReceiverPreviousStock() {
        return receiverPreviousStock;
    }

    @JsonProperty("receiverPreviousStock")
    public void setReceiverPreviousStock(String receiverPreviousStock) {
        this.receiverPreviousStock = receiverPreviousStock;
    }

    @JsonProperty("receiverTxnProfile")
    public String getReceiverTxnProfile() {
        return receiverTxnProfile;
    }

    @JsonProperty("receiverTxnProfile")
    public void setReceiverTxnProfile(String receiverTxnProfile) {
        this.receiverTxnProfile = receiverTxnProfile;
    }

    @JsonProperty("receiverTxnProfileName")
    public String getReceiverTxnProfileName() {
        return receiverTxnProfileName;
    }

    @JsonProperty("receiverTxnProfileName")
    public void setReceiverTxnProfileName(String receiverTxnProfileName) {
        this.receiverTxnProfileName = receiverTxnProfileName;
    }

    @JsonProperty("recieverPostBalance")
    public String getRecieverPostBalance() {
        return recieverPostBalance;
    }

    @JsonProperty("recieverPostBalance")
    public void setRecieverPostBalance(String recieverPostBalance) {
        this.recieverPostBalance = recieverPostBalance;
    }

    @JsonProperty("reconStatus")
    public String getReconStatus() {
        return reconStatus;
    }

    @JsonProperty("reconStatus")
    public void setReconStatus(String reconStatus) {
        this.reconStatus = reconStatus;
    }

    @JsonProperty("reconciliationFlag")
    public boolean isReconciliationFlag() {
        return reconciliationFlag;
    }

    @JsonProperty("reconciliationFlag")
    public void setReconciliationFlag(boolean reconciliationFlag) {
        this.reconciliationFlag = reconciliationFlag;
    }

    @JsonProperty("refTransferID")
    public String getRefTransferID() {
        return refTransferID;
    }

    @JsonProperty("refTransferID")
    public void setRefTransferID(String refTransferID) {
        this.refTransferID = refTransferID;
    }

    @JsonProperty("referenceID")
    public String getReferenceID() {
        return referenceID;
    }

    @JsonProperty("referenceID")
    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    @JsonProperty("referenceNum")
    public String getReferenceNum() {
        return referenceNum;
    }

    @JsonProperty("referenceNum")
    public void setReferenceNum(String referenceNum) {
        this.referenceNum = referenceNum;
    }

    @JsonProperty("reqQuantity")
    public String getReqQuantity() {
        return reqQuantity;
    }

    @JsonProperty("reqQuantity")
    public void setReqQuantity(String reqQuantity) {
        this.reqQuantity = reqQuantity;
    }

    @JsonProperty("requestGatewayCode")
    public String getRequestGatewayCode() {
        return requestGatewayCode;
    }

    @JsonProperty("requestGatewayCode")
    public void setRequestGatewayCode(String requestGatewayCode) {
        this.requestGatewayCode = requestGatewayCode;
    }

    @JsonProperty("requestGatewayType")
    public String getRequestGatewayType() {
        return requestGatewayType;
    }

    @JsonProperty("requestGatewayType")
    public void setRequestGatewayType(String requestGatewayType) {
        this.requestGatewayType = requestGatewayType;
    }

    @JsonProperty("requestedQuantity")
    public String getRequestedQuantity() {
        return requestedQuantity;
    }

    @JsonProperty("requestedQuantity")
    public void setRequestedQuantity(String requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    @JsonProperty("requestedQuantityAsString")
    public String getRequestedQuantityAsString() {
        return requestedQuantityAsString;
    }

    @JsonProperty("requestedQuantityAsString")
    public void setRequestedQuantityAsString(String requestedQuantityAsString) {
        this.requestedQuantityAsString = requestedQuantityAsString;
    }

    @JsonProperty("requiredQuantity")
    public String getRequiredQuantity() {
        return requiredQuantity;
    }

    @JsonProperty("requiredQuantity")
    public void setRequiredQuantity(String requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    @JsonProperty("reversalFlag")
    public boolean isReversalFlag() {
        return reversalFlag;
    }

    @JsonProperty("reversalFlag")
    public void setReversalFlag(boolean reversalFlag) {
        this.reversalFlag = reversalFlag;
    }

    @JsonProperty("secondApprovalLimit")
    public String getSecondApprovalLimit() {
        return secondApprovalLimit;
    }

    @JsonProperty("secondApprovalLimit")
    public void setSecondApprovalLimit(String secondApprovalLimit) {
        this.secondApprovalLimit = secondApprovalLimit;
    }

    @JsonProperty("secondApprovalRemark")
    public String getSecondApprovalRemark() {
        return secondApprovalRemark;
    }

    @JsonProperty("secondApprovalRemark")
    public void setSecondApprovalRemark(String secondApprovalRemark) {
        this.secondApprovalRemark = secondApprovalRemark;
    }

    @JsonProperty("secondApprovedBy")
    public String getSecondApprovedBy() {
        return secondApprovedBy;
    }

    @JsonProperty("secondApprovedBy")
    public void setSecondApprovedBy(String secondApprovedBy) {
        this.secondApprovedBy = secondApprovedBy;
    }

    @JsonProperty("secondApprovedByName")
    public String getSecondApprovedByName() {
        return secondApprovedByName;
    }

    @JsonProperty("secondApprovedByName")
    public void setSecondApprovedByName(String secondApprovedByName) {
        this.secondApprovedByName = secondApprovedByName;
    }

    @JsonProperty("secondApprovedOn")
    public String getSecondApprovedOn() {
        return secondApprovedOn;
    }

    @JsonProperty("secondApprovedOn")
    public void setSecondApprovedOn(String secondApprovedOn) {
        this.secondApprovedOn = secondApprovedOn;
    }

    @JsonProperty("secondApprovedOnAsString")
    public String getSecondApprovedOnAsString() {
        return secondApprovedOnAsString;
    }

    @JsonProperty("secondApprovedOnAsString")
    public void setSecondApprovedOnAsString(String secondApprovedOnAsString) {
        this.secondApprovedOnAsString = secondApprovedOnAsString;
    }

    @JsonProperty("secondLang")
    public String getSecondLang() {
        return secondLang;
    }

    @JsonProperty("secondLang")
    public void setSecondLang(String secondLang) {
        this.secondLang = secondLang;
    }

    @JsonProperty("segment")
    public String getSegment() {
        return segment;
    }

    @JsonProperty("segment")
    public void setSegment(String segment) {
        this.segment = segment;
    }

    @JsonProperty("selectorName")
    public String getSelectorName() {
        return selectorName;
    }

    @JsonProperty("selectorName")
    public void setSelectorName(String selectorName) {
        this.selectorName = selectorName;
    }

    @JsonProperty("senderCatName")
    public String getSenderCatName() {
        return senderCatName;
    }

    @JsonProperty("senderCatName")
    public void setSenderCatName(String senderCatName) {
        this.senderCatName = senderCatName;
    }

    @JsonProperty("senderCategory")
    public String getSenderCategory() {
        return senderCategory;
    }

    @JsonProperty("senderCategory")
    public void setSenderCategory(String senderCategory) {
        this.senderCategory = senderCategory;
    }

    @JsonProperty("senderDrQty")
    public String getSenderDrQty() {
        return senderDrQty;
    }

    @JsonProperty("senderDrQty")
    public void setSenderDrQty(String senderDrQty) {
        this.senderDrQty = senderDrQty;
    }

    @JsonProperty("senderDrQtyAsString")
    public String getSenderDrQtyAsString() {
        return senderDrQtyAsString;
    }

    @JsonProperty("senderDrQtyAsString")
    public void setSenderDrQtyAsString(String senderDrQtyAsString) {
        this.senderDrQtyAsString = senderDrQtyAsString;
    }

    @JsonProperty("senderGradeCode")
    public String getSenderGradeCode() {
        return senderGradeCode;
    }

    @JsonProperty("senderGradeCode")
    public void setSenderGradeCode(String senderGradeCode) {
        this.senderGradeCode = senderGradeCode;
    }

    @JsonProperty("senderLoginID")
    public String getSenderLoginID() {
        return senderLoginID;
    }

    @JsonProperty("senderLoginID")
    public void setSenderLoginID(String senderLoginID) {
        this.senderLoginID = senderLoginID;
    }

    @JsonProperty("senderPostStock")
    public String getSenderPostStock() {
        return senderPostStock;
    }

    @JsonProperty("senderPostStock")
    public void setSenderPostStock(String senderPostStock) {
        this.senderPostStock = senderPostStock;
    }

    @JsonProperty("senderPostbalance")
    public String getSenderPostbalance() {
        return senderPostbalance;
    }

    @JsonProperty("senderPostbalance")
    public void setSenderPostbalance(String senderPostbalance) {
        this.senderPostbalance = senderPostbalance;
    }

    @JsonProperty("senderPreviousStock")
    public String getSenderPreviousStock() {
        return senderPreviousStock;
    }

    @JsonProperty("senderPreviousStock")
    public void setSenderPreviousStock(String senderPreviousStock) {
        this.senderPreviousStock = senderPreviousStock;
    }

    @JsonProperty("senderTxnProfile")
    public String getSenderTxnProfile() {
        return senderTxnProfile;
    }

    @JsonProperty("senderTxnProfile")
    public void setSenderTxnProfile(String senderTxnProfile) {
        this.senderTxnProfile = senderTxnProfile;
    }

    @JsonProperty("senderTxnProfileName")
    public String getSenderTxnProfileName() {
        return senderTxnProfileName;
    }

    @JsonProperty("senderTxnProfileName")
    public void setSenderTxnProfileName(String senderTxnProfileName) {
        this.senderTxnProfileName = senderTxnProfileName;
    }

    @JsonProperty("serviceClass")
    public String getServiceClass() {
        return serviceClass;
    }

    @JsonProperty("serviceClass")
    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    @JsonProperty("serviceTypeName")
    public String getServiceTypeName() {
        return serviceTypeName;
    }

    @JsonProperty("serviceTypeName")
    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    @JsonProperty("slabslist")
    public List<Slabslist> getSlabslist() {
        return slabslist;
    }

    @JsonProperty("slabslist")
    public void setSlabslist(List<Slabslist> slabslist) {
        this.slabslist = slabslist;
    }

    @JsonProperty("sosFlag")
    public boolean isSosFlag() {
        return sosFlag;
    }

    @JsonProperty("sosFlag")
    public void setSosFlag(boolean sosFlag) {
        this.sosFlag = sosFlag;
    }

    @JsonProperty("sosProductCode")
    public String getSosProductCode() {
        return sosProductCode;
    }

    @JsonProperty("sosProductCode")
    public void setSosProductCode(String sosProductCode) {
        this.sosProductCode = sosProductCode;
    }

    @JsonProperty("sosRequestAmount")
    public String getSosRequestAmount() {
        return sosRequestAmount;
    }

    @JsonProperty("sosRequestAmount")
    public void setSosRequestAmount(String sosRequestAmount) {
        this.sosRequestAmount = sosRequestAmount;
    }

    @JsonProperty("sosSettlementDate")
    public String getSosSettlementDate() {
        return sosSettlementDate;
    }

    @JsonProperty("sosSettlementDate")
    public void setSosSettlementDate(String sosSettlementDate) {
        this.sosSettlementDate = sosSettlementDate;
    }

    @JsonProperty("sosStatus")
    public String getSosStatus() {
        return sosStatus;
    }

    @JsonProperty("sosStatus")
    public void setSosStatus(String sosStatus) {
        this.sosStatus = sosStatus;
    }

    @JsonProperty("sosTxnId")
    public String getSosTxnId() {
        return sosTxnId;
    }

    @JsonProperty("sosTxnId")
    public void setSosTxnId(String sosTxnId) {
        this.sosTxnId = sosTxnId;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("statusDesc")
    public String getStatusDesc() {
        return statusDesc;
    }

    @JsonProperty("statusDesc")
    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    @JsonProperty("stockBought")
    public String getStockBought() {
        return stockBought;
    }

    @JsonProperty("stockBought")
    public void setStockBought(String stockBought) {
        this.stockBought = stockBought;
    }

    @JsonProperty("stockReturn")
    public String getStockReturn() {
        return stockReturn;
    }

    @JsonProperty("stockReturn")
    public void setStockReturn(String stockReturn) {
        this.stockReturn = stockReturn;
    }

    @JsonProperty("stockUpdated")
    public String getStockUpdated() {
        return stockUpdated;
    }

    @JsonProperty("stockUpdated")
    public void setStockUpdated(String stockUpdated) {
        this.stockUpdated = stockUpdated;
    }

    @JsonProperty("subSid")
    public String getSubSid() {
        return subSid;
    }

    @JsonProperty("subSid")
    public void setSubSid(String subSid) {
        this.subSid = subSid;
    }

    @JsonProperty("switchId")
    public String getSwitchId() {
        return switchId;
    }

    @JsonProperty("switchId")
    public void setSwitchId(String switchId) {
        this.switchId = switchId;
    }

    @JsonProperty("targetAchieved")
    public boolean isTargetAchieved() {
        return targetAchieved;
    }

    @JsonProperty("targetAchieved")
    public void setTargetAchieved(boolean targetAchieved) {
        this.targetAchieved = targetAchieved;
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

    @JsonProperty("tax1Value")
    public String getTax1Value() {
        return tax1Value;
    }

    @JsonProperty("tax1Value")
    public void setTax1Value(String tax1Value) {
        this.tax1Value = tax1Value;
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

    @JsonProperty("tax2Value")
    public String getTax2Value() {
        return tax2Value;
    }

    @JsonProperty("tax2Value")
    public void setTax2Value(String tax2Value) {
        this.tax2Value = tax2Value;
    }

    @JsonProperty("tax3ValueAsString")
    public String getTax3ValueAsString() {
        return tax3ValueAsString;
    }

    @JsonProperty("tax3ValueAsString")
    public void setTax3ValueAsString(String tax3ValueAsString) {
        this.tax3ValueAsString = tax3ValueAsString;
    }

    @JsonProperty("thirdApprovalRemark")
    public String getThirdApprovalRemark() {
        return thirdApprovalRemark;
    }

    @JsonProperty("thirdApprovalRemark")
    public void setThirdApprovalRemark(String thirdApprovalRemark) {
        this.thirdApprovalRemark = thirdApprovalRemark;
    }

    @JsonProperty("thirdApprovedBy")
    public String getThirdApprovedBy() {
        return thirdApprovedBy;
    }

    @JsonProperty("thirdApprovedBy")
    public void setThirdApprovedBy(String thirdApprovedBy) {
        this.thirdApprovedBy = thirdApprovedBy;
    }

    @JsonProperty("thirdApprovedByName")
    public String getThirdApprovedByName() {
        return thirdApprovedByName;
    }

    @JsonProperty("thirdApprovedByName")
    public void setThirdApprovedByName(String thirdApprovedByName) {
        this.thirdApprovedByName = thirdApprovedByName;
    }

    @JsonProperty("thirdApprovedOn")
    public String getThirdApprovedOn() {
        return thirdApprovedOn;
    }

    @JsonProperty("thirdApprovedOn")
    public void setThirdApprovedOn(String thirdApprovedOn) {
        this.thirdApprovedOn = thirdApprovedOn;
    }

    @JsonProperty("time")
    public String getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(String time) {
        this.time = time;
    }

    @JsonProperty("toCategoryDesc")
    public String getToCategoryDesc() {
        return toCategoryDesc;
    }

    @JsonProperty("toCategoryDesc")
    public void setToCategoryDesc(String toCategoryDesc) {
        this.toCategoryDesc = toCategoryDesc;
    }

    @JsonProperty("toChannelUserStatus")
    public String getToChannelUserStatus() {
        return toChannelUserStatus;
    }

    @JsonProperty("toChannelUserStatus")
    public void setToChannelUserStatus(String toChannelUserStatus) {
        this.toChannelUserStatus = toChannelUserStatus;
    }

    @JsonProperty("toCommissionProfileIDDesc")
    public String getToCommissionProfileIDDesc() {
        return toCommissionProfileIDDesc;
    }

    @JsonProperty("toCommissionProfileIDDesc")
    public void setToCommissionProfileIDDesc(String toCommissionProfileIDDesc) {
        this.toCommissionProfileIDDesc = toCommissionProfileIDDesc;
    }

    @JsonProperty("toEXTCODE")
    public String getToEXTCODE() {
        return toEXTCODE;
    }

    @JsonProperty("toEXTCODE")
    public void setToEXTCODE(String toEXTCODE) {
        this.toEXTCODE = toEXTCODE;
    }

    @JsonProperty("toGradeCodeDesc")
    public String getToGradeCodeDesc() {
        return toGradeCodeDesc;
    }

    @JsonProperty("toGradeCodeDesc")
    public void setToGradeCodeDesc(String toGradeCodeDesc) {
        this.toGradeCodeDesc = toGradeCodeDesc;
    }

    @JsonProperty("toMSISDN")
    public String getToMSISDN() {
        return toMSISDN;
    }

    @JsonProperty("toMSISDN")
    public void setToMSISDN(String toMSISDN) {
        this.toMSISDN = toMSISDN;
    }

    @JsonProperty("toMsisdn")
    public String getToMsisdn() {
        return toMsisdn;
    }

    @JsonProperty("toMsisdn")
    public void setToMsisdn(String toMsisdn) {
        this.toMsisdn = toMsisdn;
    }

    @JsonProperty("toOwnerGeo")
    public String getToOwnerGeo() {
        return toOwnerGeo;
    }

    @JsonProperty("toOwnerGeo")
    public void setToOwnerGeo(String toOwnerGeo) {
        this.toOwnerGeo = toOwnerGeo;
    }

    @JsonProperty("toPrimaryMSISDN")
    public String getToPrimaryMSISDN() {
        return toPrimaryMSISDN;
    }

    @JsonProperty("toPrimaryMSISDN")
    public void setToPrimaryMSISDN(String toPrimaryMSISDN) {
        this.toPrimaryMSISDN = toPrimaryMSISDN;
    }

    @JsonProperty("toTxnProfileDesc")
    public String getToTxnProfileDesc() {
        return toTxnProfileDesc;
    }

    @JsonProperty("toTxnProfileDesc")
    public void setToTxnProfileDesc(String toTxnProfileDesc) {
        this.toTxnProfileDesc = toTxnProfileDesc;
    }

    @JsonProperty("toUserCode")
    public String getToUserCode() {
        return toUserCode;
    }

    @JsonProperty("toUserCode")
    public void setToUserCode(String toUserCode) {
        this.toUserCode = toUserCode;
    }

    @JsonProperty("toUserGeo")
    public String getToUserGeo() {
        return toUserGeo;
    }

    @JsonProperty("toUserGeo")
    public void setToUserGeo(String toUserGeo) {
        this.toUserGeo = toUserGeo;
    }

    @JsonProperty("toUserID")
    public String getToUserID() {
        return toUserID;
    }

    @JsonProperty("toUserID")
    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
    }

    @JsonProperty("toUserMsisdn")
    public String getToUserMsisdn() {
        return toUserMsisdn;
    }

    @JsonProperty("toUserMsisdn")
    public void setToUserMsisdn(String toUserMsisdn) {
        this.toUserMsisdn = toUserMsisdn;
    }

    @JsonProperty("toUserName")
    public String getToUserName() {
        return toUserName;
    }

    @JsonProperty("toUserName")
    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    @JsonProperty("to_serial_no")
    public String getToSerialNo() {
        return toSerialNo;
    }

    @JsonProperty("to_serial_no")
    public void setToSerialNo(String toSerialNo) {
        this.toSerialNo = toSerialNo;
    }

    @JsonProperty("totalTax1")
    public String getTotalTax1() {
        return totalTax1;
    }

    @JsonProperty("totalTax1")
    public void setTotalTax1(String totalTax1) {
        this.totalTax1 = totalTax1;
    }

    @JsonProperty("totalTax2")
    public String getTotalTax2() {
        return totalTax2;
    }

    @JsonProperty("totalTax2")
    public void setTotalTax2(String totalTax2) {
        this.totalTax2 = totalTax2;
    }

    @JsonProperty("totalTax3")
    public String getTotalTax3() {
        return totalTax3;
    }

    @JsonProperty("totalTax3")
    public void setTotalTax3(String totalTax3) {
        this.totalTax3 = totalTax3;
    }

    @JsonProperty("total_no_of_vouchers")
    public String getTotalNoOfVouchers() {
        return totalNoOfVouchers;
    }

    @JsonProperty("total_no_of_vouchers")
    public void setTotalNoOfVouchers(String totalNoOfVouchers) {
        this.totalNoOfVouchers = totalNoOfVouchers;
    }

    @JsonProperty("transInAmount")
    public String getTransInAmount() {
        return transInAmount;
    }

    @JsonProperty("transInAmount")
    public void setTransInAmount(String transInAmount) {
        this.transInAmount = transInAmount;
    }

    @JsonProperty("transInCount")
    public String getTransInCount() {
        return transInCount;
    }

    @JsonProperty("transInCount")
    public void setTransInCount(String transInCount) {
        this.transInCount = transInCount;
    }

    @JsonProperty("transOutAmount")
    public String getTransOutAmount() {
        return transOutAmount;
    }

    @JsonProperty("transOutAmount")
    public void setTransOutAmount(String transOutAmount) {
        this.transOutAmount = transOutAmount;
    }

    @JsonProperty("transOutCount")
    public String getTransOutCount() {
        return transOutCount;
    }

    @JsonProperty("transOutCount")
    public void setTransOutCount(String transOutCount) {
        this.transOutCount = transOutCount;
    }

    @JsonProperty("transactionCode")
    public String getTransactionCode() {
        return transactionCode;
    }

    @JsonProperty("transactionCode")
    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    @JsonProperty("transactionCount")
    public String getTransactionCount() {
        return transactionCount;
    }

    @JsonProperty("transactionCount")
    public void setTransactionCount(String transactionCount) {
        this.transactionCount = transactionCount;
    }

    @JsonProperty("transactionMode")
    public String getTransactionMode() {
        return transactionMode;
    }

    @JsonProperty("transactionMode")
    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    @JsonProperty("transferAmt")
    public String getTransferAmt() {
        return transferAmt;
    }

    @JsonProperty("transferAmt")
    public void setTransferAmt(String transferAmt) {
        this.transferAmt = transferAmt;
    }

    @JsonProperty("transferCategory")
    public String getTransferCategory() {
        return transferCategory;
    }

    @JsonProperty("transferCategory")
    public void setTransferCategory(String transferCategory) {
        this.transferCategory = transferCategory;
    }

    @JsonProperty("transferCategoryCode")
    public String getTransferCategoryCode() {
        return transferCategoryCode;
    }

    @JsonProperty("transferCategoryCode")
    public void setTransferCategoryCode(String transferCategoryCode) {
        this.transferCategoryCode = transferCategoryCode;
    }

    @JsonProperty("transferCategoryCodeDesc")
    public String getTransferCategoryCodeDesc() {
        return transferCategoryCodeDesc;
    }

    @JsonProperty("transferCategoryCodeDesc")
    public void setTransferCategoryCodeDesc(String transferCategoryCodeDesc) {
        this.transferCategoryCodeDesc = transferCategoryCodeDesc;
    }

    @JsonProperty("transferDate")
    public String getTransferDate() {
        return transferDate;
    }

    @JsonProperty("transferDate")
    public void setTransferDate(String transferDate) {
        this.transferDate = transferDate;
    }

    @JsonProperty("transferDateAsString")
    public String getTransferDateAsString() {
        return transferDateAsString;
    }

    @JsonProperty("transferDateAsString")
    public void setTransferDateAsString(String transferDateAsString) {
        this.transferDateAsString = transferDateAsString;
    }

    @JsonProperty("transferID")
    public String getTransferID() {
        return transferID;
    }

    @JsonProperty("transferID")
    public void setTransferID(String transferID) {
        this.transferID = transferID;
    }

    @JsonProperty("transferInitatedBy")
    public String getTransferInitatedBy() {
        return transferInitatedBy;
    }

    @JsonProperty("transferInitatedBy")
    public void setTransferInitatedBy(String transferInitatedBy) {
        this.transferInitatedBy = transferInitatedBy;
    }

    @JsonProperty("transferInitatedByName")
    public String getTransferInitatedByName() {
        return transferInitatedByName;
    }

    @JsonProperty("transferInitatedByName")
    public void setTransferInitatedByName(String transferInitatedByName) {
        this.transferInitatedByName = transferInitatedByName;
    }

    @JsonProperty("transferMRP")
    public String getTransferMRP() {
        return transferMRP;
    }

    @JsonProperty("transferMRP")
    public void setTransferMRP(String transferMRP) {
        this.transferMRP = transferMRP;
    }

    @JsonProperty("transferMRPAsString")
    public String getTransferMRPAsString() {
        return transferMRPAsString;
    }

    @JsonProperty("transferMRPAsString")
    public void setTransferMRPAsString(String transferMRPAsString) {
        this.transferMRPAsString = transferMRPAsString;
    }

    @JsonProperty("transferProfileID")
    public String getTransferProfileID() {
        return transferProfileID;
    }

    @JsonProperty("transferProfileID")
    public void setTransferProfileID(String transferProfileID) {
        this.transferProfileID = transferProfileID;
    }

    @JsonProperty("transferSubType")
    public String getTransferSubType() {
        return transferSubType;
    }

    @JsonProperty("transferSubType")
    public void setTransferSubType(String transferSubType) {
        this.transferSubType = transferSubType;
    }

    @JsonProperty("transferSubTypeAsString")
    public String getTransferSubTypeAsString() {
        return transferSubTypeAsString;
    }

    @JsonProperty("transferSubTypeAsString")
    public void setTransferSubTypeAsString(String transferSubTypeAsString) {
        this.transferSubTypeAsString = transferSubTypeAsString;
    }

    @JsonProperty("transferSubTypeValue")
    public String getTransferSubTypeValue() {
        return transferSubTypeValue;
    }

    @JsonProperty("transferSubTypeValue")
    public void setTransferSubTypeValue(String transferSubTypeValue) {
        this.transferSubTypeValue = transferSubTypeValue;
    }

    @JsonProperty("transferType")
    public String getTransferType() {
        return transferType;
    }

    @JsonProperty("transferType")
    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("unitValue")
    public String getUnitValue() {
        return unitValue;
    }

    @JsonProperty("unitValue")
    public void setUnitValue(String unitValue) {
        this.unitValue = unitValue;
    }

    @JsonProperty("uploadedFile")
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    @JsonProperty("uploadedFile")
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    @JsonProperty("uploadedFileName")
    public String getUploadedFileName() {
        return uploadedFileName;
    }

    @JsonProperty("uploadedFileName")
    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }

    @JsonProperty("uploadedFilePath")
    public String getUploadedFilePath() {
        return uploadedFilePath;
    }

    @JsonProperty("uploadedFilePath")
    public void setUploadedFilePath(String uploadedFilePath) {
        this.uploadedFilePath = uploadedFilePath;
    }

    @JsonProperty("userID")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @JsonProperty("userMsisdn")
    public String getUserMsisdn() {
        return userMsisdn;
    }

    @JsonProperty("userMsisdn")
    public void setUserMsisdn(String userMsisdn) {
        this.userMsisdn = userMsisdn;
    }

    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("userOTFCountsVO")
    public UserOTFCountsVO_ getUserOTFCountsVO() {
        return userOTFCountsVO;
    }

    @JsonProperty("userOTFCountsVO")
    public void setUserOTFCountsVO(UserOTFCountsVO_ userOTFCountsVO) {
        this.userOTFCountsVO = userOTFCountsVO;
    }

    @JsonProperty("userWalletCode")
    public String getUserWalletCode() {
        return userWalletCode;
    }

    @JsonProperty("userWalletCode")
    public void setUserWalletCode(String userWalletCode) {
        this.userWalletCode = userWalletCode;
    }

    @JsonProperty("userrevlist")
    public List<Userrevlist> getUserrevlist() {
        return userrevlist;
    }

    @JsonProperty("userrevlist")
    public void setUserrevlist(List<Userrevlist> userrevlist) {
        this.userrevlist = userrevlist;
    }

    @JsonProperty("voucherTypeList")
    public List<VoucherTypeList> getVoucherTypeList() {
        return voucherTypeList;
    }

    @JsonProperty("voucherTypeList")
    public void setVoucherTypeList(List<VoucherTypeList> voucherTypeList) {
        this.voucherTypeList = voucherTypeList;
    }

    @JsonProperty("voucher_type")
    public String getVoucherType() {
        return voucherType;
    }

    @JsonProperty("voucher_type")
    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    @JsonProperty("walletType")
    public String getWalletType() {
        return walletType;
    }

    @JsonProperty("walletType")
    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    @JsonProperty("web")
    public boolean isWeb() {
        return web;
    }

    @JsonProperty("web")
    public void setWeb(boolean web) {
        this.web = web;
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
