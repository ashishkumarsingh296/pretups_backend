
package restassuredapi.pojo.phonedetailsresponsepojo;

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
    "showPassword",
    "confirmPassword",
    "allowedDays",
    "paymentTypes",
    "paymentTypesList",
    "createdOn",
    "categoryCodeDesc",
    "appintmentDate",
    "statusList",
    "userNamePrefixCode",
    "userNamePrefixList",
    "userNamePrefixDesc",
    "userLanguage",
    "userLanguageList",
    "userLanguageDesc",
    "isSerAssignChnlAdm",
    "geographicalList",
    "grphDomainTypeName",
    "geographicalCodeArray",
    "geographicalCode",
    "networkList",
    "roleFlag",
    "rolesMap",
    "roleType",
    "servicesList",
    "servicesTypes",
    "productsList",
    "productCodes",
    "addCommProfOTFDetailId",
    "otfValue",
    "otfCount",
    "allowedUserTypeCreation",
    "agentBalanceList",
    "shortName",
    "referenceID",
    "segmentList",
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
    "info11",
    "info12",
    "info13",
    "info14",
    "info15",
    "_commissionProfileApplicableFromAsString",
    "segments",
    "maxUserLevel",
    "serviceTypeList",
    "tokenLastUsedDate",
    "sosAllowed",
    "sosAllowedAmount",
    "sosThresholdLimit",
    "lastSosTransactionId",
    "lastSosStatus",
    "lastSosProductCode",
    "lrAllowed",
    "lrMaxAmount",
    "lrTransferAmount",
    "toCategoryCode",
    "toUserName",
    "fromUserName",
    "fromCategoryCode",
    "nameAndId",
    "userPhonesId",
    "dualCommissionType",
    "imei",
    "otp",
    "productCode",
    "pinRequired",
    "networkCode",
    "serviceTypes",
    "commissionProfileLang1Msg",
    "commissionProfileLang2Msg",
    "commissionProfileSuspendMsg",
    "transferCategory",
    "commissionProfileApplicableFrom",
    "userChargeGrouptypeCounters",
    "userControlGrouptypeCounters",
    "accessType",
    "applicationID",
    "mpayProfileID",
    "userProfileID",
    "mcommerceServiceAllow",
    "lowBalAlertAllow",
    "primaryMsisdn",
    "balance",
    "balanceStr",
    "previousBalance",
    "trnsfrdUserHierhyList",
    "prevBalanceStr",
    "prevParentName",
    "prevUserId",
    "prevUserName",
    "prevCategoryCode",
    "prevUserNameWithCategory",
    "prevUserParentNameWithCategory",
    "userBalance",
    "msisdnPrefix",
    "primaryMsisdnPin",
    "multipleMsisdnlist",
    "firstExternalUserModify",
    "smsMSisdn",
    "phoneProfile",
    "prefixId",
    "catLowBalanceAlertAllow",
    "catOutletAllowed",
    "parentLocale",
    "alertMsisdn",
    "alertType",
    "alertEmail",
    "trannferRuleTypeId",
    "cellID",
    "switchID",
    "lmsProfile",
    "otpModifiedOn",
    "autoc2callowed",
    "autoc2cquantity",
    "channelUserID",
    "maxTxnAmount",
    "lmsProfileId",
    "parentGeographyCode",
    "decryptionKey",
    "asscMsisdnList",
    "asscMsisdnDate",
    "optInOutStatus",
    "controlGroup",
    "resetPinOTPMessage",
    "securityAnswer",
    "otpInvalidCount",
    "othCommSetId",
    "categoryList",
    "returnFlag",
    "geographicalCodeforNewuser",
    "gateway",
    "monthlyTransAmt",
    "groupRoleFlag",
    "parentStatus",
    "voucherTypes",
    "parentLoginID",
    "groupRoleCode",
    "recordNumber",
    "languageCode",
    "languageName",
    "invalidPinCount",
    "userGrade",
    "commissionProfileSetID",
    "transferProfileID",
    "inSuspend",
    "outSuspened",
    "commissionProfileSetName",
    "userGradeName",
    "commissionProfileSetVersion",
    "transferProfileName",
    "categoryName",
    "smsPin",
    "userlevel",
    "userIDPrefix",
    "geographicalDesc",
    "commissionProfileStatus",
    "transferProfileStatus",
    "transferRuleID",
    "outletCode",
    "subOutletCode",
    "activatedOn",
    "categoryCode",
    "networkID",
    "sessionInfoVO",
    "userID",
    "loggerMessage",
    "loginID",
    "networkName",
    "userName",
    "userType",
    "domainID",
    "loginTime",
    "browserType",
    "currentRoleCode",
    "firstName",
    "lastName",
    "language",
    "rsaFlag",
    "rsaAllowed",
    "rsavalidated",
    "otpvalidated",
    "authTypeAllowed",
    "rolesMapSelectedCount",
    "documentType",
    "documentNo",
    "paymentType",
    "ownerCompany",
    "password",
    "createdBy",
    "status",
    "message",
    "authType",
    "divisionList",
    "departmentList",
    "associatedGeographicalList",
    "rsaRequired",
    "countryCode",
    "assType",
    "assoMsisdn",
    "associationCreatedOn",
    "associationModifiedOn",
    "oldLastLoginOn",
    "c2sMisFromDate",
    "c2sMisToDate",
    "p2pMisFromDate",
    "p2pMisToDate",
    "contactPerson",
    "staffUser",
    "activeUserID",
    "remarks",
    "batchName",
    "batchID",
    "creationType",
    "domainCodes",
    "passwordModifyFlag",
    "reportHeaderName",
    "invalidPasswordCount",
    "passwordCountUpdatedOn",
    "address1",
    "address2",
    "city",
    "country",
    "externalCode",
    "userCode",
    "ssn",
    "userNamePrefix",
    "msisdn",
    "company",
    "fax",
    "categoryVO",
    "remoteAddress",
    "statusDesc",
    "level1ApprovedBy",
    "level1ApprovedOn",
    "level2ApprovedBy",
    "level2ApprovedOn",
    "createdByUserName",
    "ownerCategoryName",
    "ownerMsisdn",
    "ownerName",
    "parentCategoryName",
    "parentMsisdn",
    "domainName",
    "userBalanceList",
    "domainList",
    "serviceList",
    "voucherList",
    "geographicalCodeStatus",
    "associatedProductTypeList",
    "appointmentDate",
    "userNameWithCategory",
    "requetedByUserName",
    "requetedOnAsString",
    "suspendedByUserName",
    "suspendedOnAsString",
    "domainStatus",
    "currentModule",
    "domainTypeCode",
    "restrictedMsisdnAllow",
    "moduleCodeString",
    "pageCodeString",
    "passwordReset",
    "pinReset",
    "activeUserMsisdn",
    "activeUserPin",
    "activeUserLoginId",
    "staffUserDetails",
    "longitude",
    "latitude",
    "requestType",
    "webLoginID",
    "empCode",
    "fromTime",
    "geographicalAreaList",
    "lastLoginOn",
    "menuItemList",
    "msisdnList",
    "networkNamewithNetworkCode",
    "ownerID",
    "parentID",
    "parentName",
    "toTime",
    "validRequestURLs",
    "passwordModifiedOn",
    "networkStatus",
    "validStatus",
    "allowedIps",
    "modifiedBy",
    "modifiedOn",
    "email",
    "previousStatus",
    "userNamewithUserId",
    "userNamewithLoginId",
    "contactNo",
    "departmentCode",
    "departmentDesc",
    "designation",
    "divisionCode",
    "divisionDesc",
    "userPhoneVO",
    "associatedServiceTypeList",
    "fullAddress",
    "fxedInfoStr",
    "usingNewSTK",
    "updateSimRequired",
    "createdOnAsString",
    "state",
    "lastModified"
})
@Generated("jsonschema2pojo")
public class User {

    @JsonProperty("showPassword")
    private Object showPassword;
    @JsonProperty("confirmPassword")
    private Object confirmPassword;
    @JsonProperty("allowedDays")
    private Object allowedDays;
    @JsonProperty("paymentTypes")
    private Object paymentTypes;
    @JsonProperty("paymentTypesList")
    private Object paymentTypesList;
    @JsonProperty("createdOn")
    private Object createdOn;
    @JsonProperty("categoryCodeDesc")
    private Object categoryCodeDesc;
    @JsonProperty("appintmentDate")
    private Object appintmentDate;
    @JsonProperty("statusList")
    private Object statusList;
    @JsonProperty("userNamePrefixCode")
    private Object userNamePrefixCode;
    @JsonProperty("userNamePrefixList")
    private Object userNamePrefixList;
    @JsonProperty("userNamePrefixDesc")
    private Object userNamePrefixDesc;
    @JsonProperty("userLanguage")
    private Object userLanguage;
    @JsonProperty("userLanguageList")
    private Object userLanguageList;
    @JsonProperty("userLanguageDesc")
    private Object userLanguageDesc;
    @JsonProperty("isSerAssignChnlAdm")
    private Boolean isSerAssignChnlAdm;
    @JsonProperty("geographicalList")
    private Object geographicalList;
    @JsonProperty("grphDomainTypeName")
    private Object grphDomainTypeName;
    @JsonProperty("geographicalCodeArray")
    private Object geographicalCodeArray;
    @JsonProperty("geographicalCode")
    private Object geographicalCode;
    @JsonProperty("networkList")
    private Object networkList;
    @JsonProperty("roleFlag")
    private Object roleFlag;
    @JsonProperty("rolesMap")
    private Object rolesMap;
    @JsonProperty("roleType")
    private Object roleType;
    @JsonProperty("servicesList")
    private Object servicesList;
    @JsonProperty("servicesTypes")
    private Object servicesTypes;
    @JsonProperty("productsList")
    private Object productsList;
    @JsonProperty("productCodes")
    private Object productCodes;
    @JsonProperty("addCommProfOTFDetailId")
    private Object addCommProfOTFDetailId;
    @JsonProperty("otfValue")
    private Object otfValue;
    @JsonProperty("otfCount")
    private Integer otfCount;
    @JsonProperty("allowedUserTypeCreation")
    private Object allowedUserTypeCreation;
    @JsonProperty("agentBalanceList")
    private Object agentBalanceList;
    @JsonProperty("shortName")
    private Object shortName;
    @JsonProperty("referenceID")
    private Object referenceID;
    @JsonProperty("segmentList")
    private Object segmentList;
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
    @JsonProperty("info11")
    private Object info11;
    @JsonProperty("info12")
    private Object info12;
    @JsonProperty("info13")
    private Object info13;
    @JsonProperty("info14")
    private Object info14;
    @JsonProperty("info15")
    private Object info15;
    @JsonProperty("_commissionProfileApplicableFromAsString")
    private Object commissionProfileApplicableFromAsString;
    @JsonProperty("segments")
    private Object segments;
    @JsonProperty("maxUserLevel")
    private Integer maxUserLevel;
    @JsonProperty("serviceTypeList")
    private Object serviceTypeList;
    @JsonProperty("tokenLastUsedDate")
    private Object tokenLastUsedDate;
    @JsonProperty("sosAllowed")
    private Object sosAllowed;
    @JsonProperty("sosAllowedAmount")
    private Integer sosAllowedAmount;
    @JsonProperty("sosThresholdLimit")
    private Integer sosThresholdLimit;
    @JsonProperty("lastSosTransactionId")
    private Object lastSosTransactionId;
    @JsonProperty("lastSosStatus")
    private Object lastSosStatus;
    @JsonProperty("lastSosProductCode")
    private Object lastSosProductCode;
    @JsonProperty("lrAllowed")
    private Object lrAllowed;
    @JsonProperty("lrMaxAmount")
    private Integer lrMaxAmount;
    @JsonProperty("lrTransferAmount")
    private Integer lrTransferAmount;
    @JsonProperty("toCategoryCode")
    private Object toCategoryCode;
    @JsonProperty("toUserName")
    private Object toUserName;
    @JsonProperty("fromUserName")
    private Object fromUserName;
    @JsonProperty("fromCategoryCode")
    private Object fromCategoryCode;
    @JsonProperty("nameAndId")
    private Object nameAndId;
    @JsonProperty("userPhonesId")
    private Object userPhonesId;
    @JsonProperty("dualCommissionType")
    private Object dualCommissionType;
    @JsonProperty("imei")
    private Object imei;
    @JsonProperty("otp")
    private Object otp;
    @JsonProperty("productCode")
    private Object productCode;
    @JsonProperty("pinRequired")
    private Object pinRequired;
    @JsonProperty("networkCode")
    private Object networkCode;
    @JsonProperty("serviceTypes")
    private Object serviceTypes;
    @JsonProperty("commissionProfileLang1Msg")
    private Object commissionProfileLang1Msg;
    @JsonProperty("commissionProfileLang2Msg")
    private Object commissionProfileLang2Msg;
    @JsonProperty("commissionProfileSuspendMsg")
    private Object commissionProfileSuspendMsg;
    @JsonProperty("transferCategory")
    private Object transferCategory;
    @JsonProperty("commissionProfileApplicableFrom")
    private Object commissionProfileApplicableFrom;
    @JsonProperty("userChargeGrouptypeCounters")
    private Object userChargeGrouptypeCounters;
    @JsonProperty("userControlGrouptypeCounters")
    private Object userControlGrouptypeCounters;
    @JsonProperty("accessType")
    private Object accessType;
    @JsonProperty("applicationID")
    private Object applicationID;
    @JsonProperty("mpayProfileID")
    private Object mpayProfileID;
    @JsonProperty("userProfileID")
    private Object userProfileID;
    @JsonProperty("mcommerceServiceAllow")
    private Object mcommerceServiceAllow;
    @JsonProperty("lowBalAlertAllow")
    private Object lowBalAlertAllow;
    @JsonProperty("primaryMsisdn")
    private Object primaryMsisdn;
    @JsonProperty("balance")
    private Integer balance;
    @JsonProperty("balanceStr")
    private Object balanceStr;
    @JsonProperty("previousBalance")
    private Integer previousBalance;
    @JsonProperty("trnsfrdUserHierhyList")
    private Object trnsfrdUserHierhyList;
    @JsonProperty("prevBalanceStr")
    private Object prevBalanceStr;
    @JsonProperty("prevParentName")
    private Object prevParentName;
    @JsonProperty("prevUserId")
    private Object prevUserId;
    @JsonProperty("prevUserName")
    private Object prevUserName;
    @JsonProperty("prevCategoryCode")
    private Object prevCategoryCode;
    @JsonProperty("prevUserNameWithCategory")
    private String prevUserNameWithCategory;
    @JsonProperty("prevUserParentNameWithCategory")
    private String prevUserParentNameWithCategory;
    @JsonProperty("userBalance")
    private Object userBalance;
    @JsonProperty("msisdnPrefix")
    private Object msisdnPrefix;
    @JsonProperty("primaryMsisdnPin")
    private Object primaryMsisdnPin;
    @JsonProperty("multipleMsisdnlist")
    private Object multipleMsisdnlist;
    @JsonProperty("firstExternalUserModify")
    private Boolean firstExternalUserModify;
    @JsonProperty("smsMSisdn")
    private Object smsMSisdn;
    @JsonProperty("phoneProfile")
    private Object phoneProfile;
    @JsonProperty("prefixId")
    private Integer prefixId;
    @JsonProperty("catLowBalanceAlertAllow")
    private Object catLowBalanceAlertAllow;
    @JsonProperty("catOutletAllowed")
    private Object catOutletAllowed;
    @JsonProperty("parentLocale")
    private Object parentLocale;
    @JsonProperty("alertMsisdn")
    private Object alertMsisdn;
    @JsonProperty("alertType")
    private Object alertType;
    @JsonProperty("alertEmail")
    private Object alertEmail;
    @JsonProperty("trannferRuleTypeId")
    private Object trannferRuleTypeId;
    @JsonProperty("cellID")
    private Object cellID;
    @JsonProperty("switchID")
    private Object switchID;
    @JsonProperty("lmsProfile")
    private Object lmsProfile;
    @JsonProperty("otpModifiedOn")
    private Object otpModifiedOn;
    @JsonProperty("autoc2callowed")
    private Object autoc2callowed;
    @JsonProperty("autoc2cquantity")
    private Object autoc2cquantity;
    @JsonProperty("channelUserID")
    private Object channelUserID;
    @JsonProperty("maxTxnAmount")
    private Integer maxTxnAmount;
    @JsonProperty("lmsProfileId")
    private Object lmsProfileId;
    @JsonProperty("parentGeographyCode")
    private Object parentGeographyCode;
    @JsonProperty("decryptionKey")
    private Object decryptionKey;
    @JsonProperty("asscMsisdnList")
    private Object asscMsisdnList;
    @JsonProperty("asscMsisdnDate")
    private Object asscMsisdnDate;
    @JsonProperty("optInOutStatus")
    private Object optInOutStatus;
    @JsonProperty("controlGroup")
    private Object controlGroup;
    @JsonProperty("resetPinOTPMessage")
    private Object resetPinOTPMessage;
    @JsonProperty("securityAnswer")
    private Object securityAnswer;
    @JsonProperty("otpInvalidCount")
    private Integer otpInvalidCount;
    @JsonProperty("othCommSetId")
    private Object othCommSetId;
    @JsonProperty("categoryList")
    private Object categoryList;
    @JsonProperty("returnFlag")
    private Boolean returnFlag;
    @JsonProperty("geographicalCodeforNewuser")
    private Object geographicalCodeforNewuser;
    @JsonProperty("gateway")
    private Object gateway;
    @JsonProperty("monthlyTransAmt")
    private Integer monthlyTransAmt;
    @JsonProperty("groupRoleFlag")
    private Object groupRoleFlag;
    @JsonProperty("parentStatus")
    private Object parentStatus;
    @JsonProperty("voucherTypes")
    private Object voucherTypes;
    @JsonProperty("parentLoginID")
    private Object parentLoginID;
    @JsonProperty("groupRoleCode")
    private Object groupRoleCode;
    @JsonProperty("recordNumber")
    private Object recordNumber;
    @JsonProperty("languageCode")
    private String languageCode;
    @JsonProperty("languageName")
    private String languageName;
    @JsonProperty("invalidPinCount")
    private Integer invalidPinCount;
    @JsonProperty("userGrade")
    private Object userGrade;
    @JsonProperty("commissionProfileSetID")
    private Object commissionProfileSetID;
    @JsonProperty("transferProfileID")
    private Object transferProfileID;
    @JsonProperty("inSuspend")
    private Object inSuspend;
    @JsonProperty("outSuspened")
    private Object outSuspened;
    @JsonProperty("commissionProfileSetName")
    private Object commissionProfileSetName;
    @JsonProperty("userGradeName")
    private Object userGradeName;
    @JsonProperty("commissionProfileSetVersion")
    private Object commissionProfileSetVersion;
    @JsonProperty("transferProfileName")
    private Object transferProfileName;
    @JsonProperty("categoryName")
    private Object categoryName;
    @JsonProperty("smsPin")
    private Object smsPin;
    @JsonProperty("userlevel")
    private Object userlevel;
    @JsonProperty("userIDPrefix")
    private Object userIDPrefix;
    @JsonProperty("geographicalDesc")
    private Object geographicalDesc;
    @JsonProperty("commissionProfileStatus")
    private Object commissionProfileStatus;
    @JsonProperty("transferProfileStatus")
    private Object transferProfileStatus;
    @JsonProperty("transferRuleID")
    private Object transferRuleID;
    @JsonProperty("outletCode")
    private Object outletCode;
    @JsonProperty("subOutletCode")
    private Object subOutletCode;
    @JsonProperty("activatedOn")
    private Object activatedOn;
    @JsonProperty("categoryCode")
    private Object categoryCode;
    @JsonProperty("networkID")
    private Object networkID;
    @JsonProperty("sessionInfoVO")
    private Object sessionInfoVO;
    @JsonProperty("userID")
    private Object userID;
    @JsonProperty("loggerMessage")
    private Object loggerMessage;
    @JsonProperty("loginID")
    private Object loginID;
    @JsonProperty("networkName")
    private Object networkName;
    @JsonProperty("userName")
    private Object userName;
    @JsonProperty("userType")
    private Object userType;
    @JsonProperty("domainID")
    private Object domainID;
    @JsonProperty("loginTime")
    private Object loginTime;
    @JsonProperty("browserType")
    private Object browserType;
    @JsonProperty("currentRoleCode")
    private Object currentRoleCode;
    @JsonProperty("firstName")
    private Object firstName;
    @JsonProperty("lastName")
    private Object lastName;
    @JsonProperty("language")
    private Object language;
    @JsonProperty("rsaFlag")
    private Object rsaFlag;
    @JsonProperty("rsaAllowed")
    private Boolean rsaAllowed;
    @JsonProperty("rsavalidated")
    private Boolean rsavalidated;
    @JsonProperty("otpvalidated")
    private Boolean otpvalidated;
    @JsonProperty("authTypeAllowed")
    private Object authTypeAllowed;
    @JsonProperty("rolesMapSelectedCount")
    private Integer rolesMapSelectedCount;
    @JsonProperty("documentType")
    private Object documentType;
    @JsonProperty("documentNo")
    private Object documentNo;
    @JsonProperty("paymentType")
    private Object paymentType;
    @JsonProperty("ownerCompany")
    private Object ownerCompany;
    @JsonProperty("password")
    private Object password;
    @JsonProperty("createdBy")
    private Object createdBy;
    @JsonProperty("status")
    private Object status;
    @JsonProperty("message")
    private Object message;
    @JsonProperty("authType")
    private Object authType;
    @JsonProperty("divisionList")
    private Object divisionList;
    @JsonProperty("departmentList")
    private Object departmentList;
    @JsonProperty("associatedGeographicalList")
    private Object associatedGeographicalList;
    @JsonProperty("rsaRequired")
    private Boolean rsaRequired;
    @JsonProperty("countryCode")
    private Object countryCode;
    @JsonProperty("assType")
    private Object assType;
    @JsonProperty("assoMsisdn")
    private Object assoMsisdn;
    @JsonProperty("associationCreatedOn")
    private Object associationCreatedOn;
    @JsonProperty("associationModifiedOn")
    private Object associationModifiedOn;
    @JsonProperty("oldLastLoginOn")
    private Object oldLastLoginOn;
    @JsonProperty("c2sMisFromDate")
    private Object c2sMisFromDate;
    @JsonProperty("c2sMisToDate")
    private Object c2sMisToDate;
    @JsonProperty("p2pMisFromDate")
    private Object p2pMisFromDate;
    @JsonProperty("p2pMisToDate")
    private Object p2pMisToDate;
    @JsonProperty("contactPerson")
    private Object contactPerson;
    @JsonProperty("staffUser")
    private Boolean staffUser;
    @JsonProperty("activeUserID")
    private Object activeUserID;
    @JsonProperty("remarks")
    private Object remarks;
    @JsonProperty("batchName")
    private Object batchName;
    @JsonProperty("batchID")
    private Object batchID;
    @JsonProperty("creationType")
    private Object creationType;
    @JsonProperty("domainCodes")
    private Object domainCodes;
    @JsonProperty("passwordModifyFlag")
    private Boolean passwordModifyFlag;
    @JsonProperty("reportHeaderName")
    private Object reportHeaderName;
    @JsonProperty("invalidPasswordCount")
    private Integer invalidPasswordCount;
    @JsonProperty("passwordCountUpdatedOn")
    private Object passwordCountUpdatedOn;
    @JsonProperty("address1")
    private Object address1;
    @JsonProperty("address2")
    private Object address2;
    @JsonProperty("city")
    private Object city;
    @JsonProperty("country")
    private String country;
    @JsonProperty("externalCode")
    private Object externalCode;
    @JsonProperty("userCode")
    private Object userCode;
    @JsonProperty("ssn")
    private Object ssn;
    @JsonProperty("userNamePrefix")
    private Object userNamePrefix;
    @JsonProperty("msisdn")
    private Object msisdn;
    @JsonProperty("company")
    private Object company;
    @JsonProperty("fax")
    private Object fax;
    @JsonProperty("categoryVO")
    private Object categoryVO;
    @JsonProperty("remoteAddress")
    private Object remoteAddress;
    @JsonProperty("statusDesc")
    private Object statusDesc;
    @JsonProperty("level1ApprovedBy")
    private Object level1ApprovedBy;
    @JsonProperty("level1ApprovedOn")
    private Object level1ApprovedOn;
    @JsonProperty("level2ApprovedBy")
    private Object level2ApprovedBy;
    @JsonProperty("level2ApprovedOn")
    private Object level2ApprovedOn;
    @JsonProperty("createdByUserName")
    private Object createdByUserName;
    @JsonProperty("ownerCategoryName")
    private Object ownerCategoryName;
    @JsonProperty("ownerMsisdn")
    private Object ownerMsisdn;
    @JsonProperty("ownerName")
    private Object ownerName;
    @JsonProperty("parentCategoryName")
    private Object parentCategoryName;
    @JsonProperty("parentMsisdn")
    private Object parentMsisdn;
    @JsonProperty("domainName")
    private Object domainName;
    @JsonProperty("userBalanceList")
    private Object userBalanceList;
    @JsonProperty("domainList")
    private Object domainList;
    @JsonProperty("serviceList")
    private Object serviceList;
    @JsonProperty("voucherList")
    private Object voucherList;
    @JsonProperty("geographicalCodeStatus")
    private Object geographicalCodeStatus;
    @JsonProperty("associatedProductTypeList")
    private Object associatedProductTypeList;
    @JsonProperty("appointmentDate")
    private Object appointmentDate;
    @JsonProperty("userNameWithCategory")
    private String userNameWithCategory;
    @JsonProperty("requetedByUserName")
    private Object requetedByUserName;
    @JsonProperty("requetedOnAsString")
    private String requetedOnAsString;
    @JsonProperty("suspendedByUserName")
    private Object suspendedByUserName;
    @JsonProperty("suspendedOnAsString")
    private String suspendedOnAsString;
    @JsonProperty("domainStatus")
    private Object domainStatus;
    @JsonProperty("currentModule")
    private Object currentModule;
    @JsonProperty("domainTypeCode")
    private Object domainTypeCode;
    @JsonProperty("restrictedMsisdnAllow")
    private Object restrictedMsisdnAllow;
    @JsonProperty("moduleCodeString")
    private Object moduleCodeString;
    @JsonProperty("pageCodeString")
    private Object pageCodeString;
    @JsonProperty("passwordReset")
    private Object passwordReset;
    @JsonProperty("pinReset")
    private Object pinReset;
    @JsonProperty("activeUserMsisdn")
    private Object activeUserMsisdn;
    @JsonProperty("activeUserPin")
    private Object activeUserPin;
    @JsonProperty("activeUserLoginId")
    private Object activeUserLoginId;
    @JsonProperty("staffUserDetails")
    private Object staffUserDetails;
    @JsonProperty("longitude")
    private Object longitude;
    @JsonProperty("latitude")
    private Object latitude;
    @JsonProperty("requestType")
    private Object requestType;
    @JsonProperty("webLoginID")
    private Object webLoginID;
    @JsonProperty("empCode")
    private Object empCode;
    @JsonProperty("fromTime")
    private Object fromTime;
    @JsonProperty("geographicalAreaList")
    private Object geographicalAreaList;
    @JsonProperty("lastLoginOn")
    private Object lastLoginOn;
    @JsonProperty("menuItemList")
    private Object menuItemList;
    @JsonProperty("msisdnList")
    private Object msisdnList;
    @JsonProperty("networkNamewithNetworkCode")
    private Object networkNamewithNetworkCode;
    @JsonProperty("ownerID")
    private Object ownerID;
    @JsonProperty("parentID")
    private Object parentID;
    @JsonProperty("parentName")
    private Object parentName;
    @JsonProperty("toTime")
    private Object toTime;
    @JsonProperty("validRequestURLs")
    private Object validRequestURLs;
    @JsonProperty("passwordModifiedOn")
    private Object passwordModifiedOn;
    @JsonProperty("networkStatus")
    private Object networkStatus;
    @JsonProperty("validStatus")
    private Integer validStatus;
    @JsonProperty("allowedIps")
    private Object allowedIps;
    @JsonProperty("modifiedBy")
    private Object modifiedBy;
    @JsonProperty("modifiedOn")
    private Object modifiedOn;
    @JsonProperty("email")
    private Object email;
    @JsonProperty("previousStatus")
    private Object previousStatus;
    @JsonProperty("userNamewithUserId")
    private Object userNamewithUserId;
    @JsonProperty("userNamewithLoginId")
    private Object userNamewithLoginId;
    @JsonProperty("contactNo")
    private Object contactNo;
    @JsonProperty("departmentCode")
    private Object departmentCode;
    @JsonProperty("departmentDesc")
    private Object departmentDesc;
    @JsonProperty("designation")
    private Object designation;
    @JsonProperty("divisionCode")
    private Object divisionCode;
    @JsonProperty("divisionDesc")
    private Object divisionDesc;
    @JsonProperty("userPhoneVO")
    private UserPhoneVO userPhoneVO;
    @JsonProperty("associatedServiceTypeList")
    private Object associatedServiceTypeList;
    @JsonProperty("fullAddress")
    private String fullAddress;
    @JsonProperty("fxedInfoStr")
    private Object fxedInfoStr;
    @JsonProperty("usingNewSTK")
    private Boolean usingNewSTK;
    @JsonProperty("updateSimRequired")
    private Boolean updateSimRequired;
    @JsonProperty("createdOnAsString")
    private Object createdOnAsString;
    @JsonProperty("state")
    private Object state;
    @JsonProperty("lastModified")
    private Integer lastModified;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("showPassword")
    public Object getShowPassword() {
        return showPassword;
    }

    @JsonProperty("showPassword")
    public void setShowPassword(Object showPassword) {
        this.showPassword = showPassword;
    }

    @JsonProperty("confirmPassword")
    public Object getConfirmPassword() {
        return confirmPassword;
    }

    @JsonProperty("confirmPassword")
    public void setConfirmPassword(Object confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @JsonProperty("allowedDays")
    public Object getAllowedDays() {
        return allowedDays;
    }

    @JsonProperty("allowedDays")
    public void setAllowedDays(Object allowedDays) {
        this.allowedDays = allowedDays;
    }

    @JsonProperty("paymentTypes")
    public Object getPaymentTypes() {
        return paymentTypes;
    }

    @JsonProperty("paymentTypes")
    public void setPaymentTypes(Object paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    @JsonProperty("paymentTypesList")
    public Object getPaymentTypesList() {
        return paymentTypesList;
    }

    @JsonProperty("paymentTypesList")
    public void setPaymentTypesList(Object paymentTypesList) {
        this.paymentTypesList = paymentTypesList;
    }

    @JsonProperty("createdOn")
    public Object getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    @JsonProperty("categoryCodeDesc")
    public Object getCategoryCodeDesc() {
        return categoryCodeDesc;
    }

    @JsonProperty("categoryCodeDesc")
    public void setCategoryCodeDesc(Object categoryCodeDesc) {
        this.categoryCodeDesc = categoryCodeDesc;
    }

    @JsonProperty("appintmentDate")
    public Object getAppintmentDate() {
        return appintmentDate;
    }

    @JsonProperty("appintmentDate")
    public void setAppintmentDate(Object appintmentDate) {
        this.appintmentDate = appintmentDate;
    }

    @JsonProperty("statusList")
    public Object getStatusList() {
        return statusList;
    }

    @JsonProperty("statusList")
    public void setStatusList(Object statusList) {
        this.statusList = statusList;
    }

    @JsonProperty("userNamePrefixCode")
    public Object getUserNamePrefixCode() {
        return userNamePrefixCode;
    }

    @JsonProperty("userNamePrefixCode")
    public void setUserNamePrefixCode(Object userNamePrefixCode) {
        this.userNamePrefixCode = userNamePrefixCode;
    }

    @JsonProperty("userNamePrefixList")
    public Object getUserNamePrefixList() {
        return userNamePrefixList;
    }

    @JsonProperty("userNamePrefixList")
    public void setUserNamePrefixList(Object userNamePrefixList) {
        this.userNamePrefixList = userNamePrefixList;
    }

    @JsonProperty("userNamePrefixDesc")
    public Object getUserNamePrefixDesc() {
        return userNamePrefixDesc;
    }

    @JsonProperty("userNamePrefixDesc")
    public void setUserNamePrefixDesc(Object userNamePrefixDesc) {
        this.userNamePrefixDesc = userNamePrefixDesc;
    }

    @JsonProperty("userLanguage")
    public Object getUserLanguage() {
        return userLanguage;
    }

    @JsonProperty("userLanguage")
    public void setUserLanguage(Object userLanguage) {
        this.userLanguage = userLanguage;
    }

    @JsonProperty("userLanguageList")
    public Object getUserLanguageList() {
        return userLanguageList;
    }

    @JsonProperty("userLanguageList")
    public void setUserLanguageList(Object userLanguageList) {
        this.userLanguageList = userLanguageList;
    }

    @JsonProperty("userLanguageDesc")
    public Object getUserLanguageDesc() {
        return userLanguageDesc;
    }

    @JsonProperty("userLanguageDesc")
    public void setUserLanguageDesc(Object userLanguageDesc) {
        this.userLanguageDesc = userLanguageDesc;
    }

    @JsonProperty("isSerAssignChnlAdm")
    public Boolean getIsSerAssignChnlAdm() {
        return isSerAssignChnlAdm;
    }

    @JsonProperty("isSerAssignChnlAdm")
    public void setIsSerAssignChnlAdm(Boolean isSerAssignChnlAdm) {
        this.isSerAssignChnlAdm = isSerAssignChnlAdm;
    }

    @JsonProperty("geographicalList")
    public Object getGeographicalList() {
        return geographicalList;
    }

    @JsonProperty("geographicalList")
    public void setGeographicalList(Object geographicalList) {
        this.geographicalList = geographicalList;
    }

    @JsonProperty("grphDomainTypeName")
    public Object getGrphDomainTypeName() {
        return grphDomainTypeName;
    }

    @JsonProperty("grphDomainTypeName")
    public void setGrphDomainTypeName(Object grphDomainTypeName) {
        this.grphDomainTypeName = grphDomainTypeName;
    }

    @JsonProperty("geographicalCodeArray")
    public Object getGeographicalCodeArray() {
        return geographicalCodeArray;
    }

    @JsonProperty("geographicalCodeArray")
    public void setGeographicalCodeArray(Object geographicalCodeArray) {
        this.geographicalCodeArray = geographicalCodeArray;
    }

    @JsonProperty("geographicalCode")
    public Object getGeographicalCode() {
        return geographicalCode;
    }

    @JsonProperty("geographicalCode")
    public void setGeographicalCode(Object geographicalCode) {
        this.geographicalCode = geographicalCode;
    }

    @JsonProperty("networkList")
    public Object getNetworkList() {
        return networkList;
    }

    @JsonProperty("networkList")
    public void setNetworkList(Object networkList) {
        this.networkList = networkList;
    }

    @JsonProperty("roleFlag")
    public Object getRoleFlag() {
        return roleFlag;
    }

    @JsonProperty("roleFlag")
    public void setRoleFlag(Object roleFlag) {
        this.roleFlag = roleFlag;
    }

    @JsonProperty("rolesMap")
    public Object getRolesMap() {
        return rolesMap;
    }

    @JsonProperty("rolesMap")
    public void setRolesMap(Object rolesMap) {
        this.rolesMap = rolesMap;
    }

    @JsonProperty("roleType")
    public Object getRoleType() {
        return roleType;
    }

    @JsonProperty("roleType")
    public void setRoleType(Object roleType) {
        this.roleType = roleType;
    }

    @JsonProperty("servicesList")
    public Object getServicesList() {
        return servicesList;
    }

    @JsonProperty("servicesList")
    public void setServicesList(Object servicesList) {
        this.servicesList = servicesList;
    }

    @JsonProperty("servicesTypes")
    public Object getServicesTypes() {
        return servicesTypes;
    }

    @JsonProperty("servicesTypes")
    public void setServicesTypes(Object servicesTypes) {
        this.servicesTypes = servicesTypes;
    }

    @JsonProperty("productsList")
    public Object getProductsList() {
        return productsList;
    }

    @JsonProperty("productsList")
    public void setProductsList(Object productsList) {
        this.productsList = productsList;
    }

    @JsonProperty("productCodes")
    public Object getProductCodes() {
        return productCodes;
    }

    @JsonProperty("productCodes")
    public void setProductCodes(Object productCodes) {
        this.productCodes = productCodes;
    }

    @JsonProperty("addCommProfOTFDetailId")
    public Object getAddCommProfOTFDetailId() {
        return addCommProfOTFDetailId;
    }

    @JsonProperty("addCommProfOTFDetailId")
    public void setAddCommProfOTFDetailId(Object addCommProfOTFDetailId) {
        this.addCommProfOTFDetailId = addCommProfOTFDetailId;
    }

    @JsonProperty("otfValue")
    public Object getOtfValue() {
        return otfValue;
    }

    @JsonProperty("otfValue")
    public void setOtfValue(Object otfValue) {
        this.otfValue = otfValue;
    }

    @JsonProperty("otfCount")
    public Integer getOtfCount() {
        return otfCount;
    }

    @JsonProperty("otfCount")
    public void setOtfCount(Integer otfCount) {
        this.otfCount = otfCount;
    }

    @JsonProperty("allowedUserTypeCreation")
    public Object getAllowedUserTypeCreation() {
        return allowedUserTypeCreation;
    }

    @JsonProperty("allowedUserTypeCreation")
    public void setAllowedUserTypeCreation(Object allowedUserTypeCreation) {
        this.allowedUserTypeCreation = allowedUserTypeCreation;
    }

    @JsonProperty("agentBalanceList")
    public Object getAgentBalanceList() {
        return agentBalanceList;
    }

    @JsonProperty("agentBalanceList")
    public void setAgentBalanceList(Object agentBalanceList) {
        this.agentBalanceList = agentBalanceList;
    }

    @JsonProperty("shortName")
    public Object getShortName() {
        return shortName;
    }

    @JsonProperty("shortName")
    public void setShortName(Object shortName) {
        this.shortName = shortName;
    }

    @JsonProperty("referenceID")
    public Object getReferenceID() {
        return referenceID;
    }

    @JsonProperty("referenceID")
    public void setReferenceID(Object referenceID) {
        this.referenceID = referenceID;
    }

    @JsonProperty("segmentList")
    public Object getSegmentList() {
        return segmentList;
    }

    @JsonProperty("segmentList")
    public void setSegmentList(Object segmentList) {
        this.segmentList = segmentList;
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

    @JsonProperty("info11")
    public Object getInfo11() {
        return info11;
    }

    @JsonProperty("info11")
    public void setInfo11(Object info11) {
        this.info11 = info11;
    }

    @JsonProperty("info12")
    public Object getInfo12() {
        return info12;
    }

    @JsonProperty("info12")
    public void setInfo12(Object info12) {
        this.info12 = info12;
    }

    @JsonProperty("info13")
    public Object getInfo13() {
        return info13;
    }

    @JsonProperty("info13")
    public void setInfo13(Object info13) {
        this.info13 = info13;
    }

    @JsonProperty("info14")
    public Object getInfo14() {
        return info14;
    }

    @JsonProperty("info14")
    public void setInfo14(Object info14) {
        this.info14 = info14;
    }

    @JsonProperty("info15")
    public Object getInfo15() {
        return info15;
    }

    @JsonProperty("info15")
    public void setInfo15(Object info15) {
        this.info15 = info15;
    }

    @JsonProperty("_commissionProfileApplicableFromAsString")
    public Object getCommissionProfileApplicableFromAsString() {
        return commissionProfileApplicableFromAsString;
    }

    @JsonProperty("_commissionProfileApplicableFromAsString")
    public void setCommissionProfileApplicableFromAsString(Object commissionProfileApplicableFromAsString) {
        this.commissionProfileApplicableFromAsString = commissionProfileApplicableFromAsString;
    }

    @JsonProperty("segments")
    public Object getSegments() {
        return segments;
    }

    @JsonProperty("segments")
    public void setSegments(Object segments) {
        this.segments = segments;
    }

    @JsonProperty("maxUserLevel")
    public Integer getMaxUserLevel() {
        return maxUserLevel;
    }

    @JsonProperty("maxUserLevel")
    public void setMaxUserLevel(Integer maxUserLevel) {
        this.maxUserLevel = maxUserLevel;
    }

    @JsonProperty("serviceTypeList")
    public Object getServiceTypeList() {
        return serviceTypeList;
    }

    @JsonProperty("serviceTypeList")
    public void setServiceTypeList(Object serviceTypeList) {
        this.serviceTypeList = serviceTypeList;
    }

    @JsonProperty("tokenLastUsedDate")
    public Object getTokenLastUsedDate() {
        return tokenLastUsedDate;
    }

    @JsonProperty("tokenLastUsedDate")
    public void setTokenLastUsedDate(Object tokenLastUsedDate) {
        this.tokenLastUsedDate = tokenLastUsedDate;
    }

    @JsonProperty("sosAllowed")
    public Object getSosAllowed() {
        return sosAllowed;
    }

    @JsonProperty("sosAllowed")
    public void setSosAllowed(Object sosAllowed) {
        this.sosAllowed = sosAllowed;
    }

    @JsonProperty("sosAllowedAmount")
    public Integer getSosAllowedAmount() {
        return sosAllowedAmount;
    }

    @JsonProperty("sosAllowedAmount")
    public void setSosAllowedAmount(Integer sosAllowedAmount) {
        this.sosAllowedAmount = sosAllowedAmount;
    }

    @JsonProperty("sosThresholdLimit")
    public Integer getSosThresholdLimit() {
        return sosThresholdLimit;
    }

    @JsonProperty("sosThresholdLimit")
    public void setSosThresholdLimit(Integer sosThresholdLimit) {
        this.sosThresholdLimit = sosThresholdLimit;
    }

    @JsonProperty("lastSosTransactionId")
    public Object getLastSosTransactionId() {
        return lastSosTransactionId;
    }

    @JsonProperty("lastSosTransactionId")
    public void setLastSosTransactionId(Object lastSosTransactionId) {
        this.lastSosTransactionId = lastSosTransactionId;
    }

    @JsonProperty("lastSosStatus")
    public Object getLastSosStatus() {
        return lastSosStatus;
    }

    @JsonProperty("lastSosStatus")
    public void setLastSosStatus(Object lastSosStatus) {
        this.lastSosStatus = lastSosStatus;
    }

    @JsonProperty("lastSosProductCode")
    public Object getLastSosProductCode() {
        return lastSosProductCode;
    }

    @JsonProperty("lastSosProductCode")
    public void setLastSosProductCode(Object lastSosProductCode) {
        this.lastSosProductCode = lastSosProductCode;
    }

    @JsonProperty("lrAllowed")
    public Object getLrAllowed() {
        return lrAllowed;
    }

    @JsonProperty("lrAllowed")
    public void setLrAllowed(Object lrAllowed) {
        this.lrAllowed = lrAllowed;
    }

    @JsonProperty("lrMaxAmount")
    public Integer getLrMaxAmount() {
        return lrMaxAmount;
    }

    @JsonProperty("lrMaxAmount")
    public void setLrMaxAmount(Integer lrMaxAmount) {
        this.lrMaxAmount = lrMaxAmount;
    }

    @JsonProperty("lrTransferAmount")
    public Integer getLrTransferAmount() {
        return lrTransferAmount;
    }

    @JsonProperty("lrTransferAmount")
    public void setLrTransferAmount(Integer lrTransferAmount) {
        this.lrTransferAmount = lrTransferAmount;
    }

    @JsonProperty("toCategoryCode")
    public Object getToCategoryCode() {
        return toCategoryCode;
    }

    @JsonProperty("toCategoryCode")
    public void setToCategoryCode(Object toCategoryCode) {
        this.toCategoryCode = toCategoryCode;
    }

    @JsonProperty("toUserName")
    public Object getToUserName() {
        return toUserName;
    }

    @JsonProperty("toUserName")
    public void setToUserName(Object toUserName) {
        this.toUserName = toUserName;
    }

    @JsonProperty("fromUserName")
    public Object getFromUserName() {
        return fromUserName;
    }

    @JsonProperty("fromUserName")
    public void setFromUserName(Object fromUserName) {
        this.fromUserName = fromUserName;
    }

    @JsonProperty("fromCategoryCode")
    public Object getFromCategoryCode() {
        return fromCategoryCode;
    }

    @JsonProperty("fromCategoryCode")
    public void setFromCategoryCode(Object fromCategoryCode) {
        this.fromCategoryCode = fromCategoryCode;
    }

    @JsonProperty("nameAndId")
    public Object getNameAndId() {
        return nameAndId;
    }

    @JsonProperty("nameAndId")
    public void setNameAndId(Object nameAndId) {
        this.nameAndId = nameAndId;
    }

    @JsonProperty("userPhonesId")
    public Object getUserPhonesId() {
        return userPhonesId;
    }

    @JsonProperty("userPhonesId")
    public void setUserPhonesId(Object userPhonesId) {
        this.userPhonesId = userPhonesId;
    }

    @JsonProperty("dualCommissionType")
    public Object getDualCommissionType() {
        return dualCommissionType;
    }

    @JsonProperty("dualCommissionType")
    public void setDualCommissionType(Object dualCommissionType) {
        this.dualCommissionType = dualCommissionType;
    }

    @JsonProperty("imei")
    public Object getImei() {
        return imei;
    }

    @JsonProperty("imei")
    public void setImei(Object imei) {
        this.imei = imei;
    }

    @JsonProperty("otp")
    public Object getOtp() {
        return otp;
    }

    @JsonProperty("otp")
    public void setOtp(Object otp) {
        this.otp = otp;
    }

    @JsonProperty("productCode")
    public Object getProductCode() {
        return productCode;
    }

    @JsonProperty("productCode")
    public void setProductCode(Object productCode) {
        this.productCode = productCode;
    }

    @JsonProperty("pinRequired")
    public Object getPinRequired() {
        return pinRequired;
    }

    @JsonProperty("pinRequired")
    public void setPinRequired(Object pinRequired) {
        this.pinRequired = pinRequired;
    }

    @JsonProperty("networkCode")
    public Object getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(Object networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("serviceTypes")
    public Object getServiceTypes() {
        return serviceTypes;
    }

    @JsonProperty("serviceTypes")
    public void setServiceTypes(Object serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    @JsonProperty("commissionProfileLang1Msg")
    public Object getCommissionProfileLang1Msg() {
        return commissionProfileLang1Msg;
    }

    @JsonProperty("commissionProfileLang1Msg")
    public void setCommissionProfileLang1Msg(Object commissionProfileLang1Msg) {
        this.commissionProfileLang1Msg = commissionProfileLang1Msg;
    }

    @JsonProperty("commissionProfileLang2Msg")
    public Object getCommissionProfileLang2Msg() {
        return commissionProfileLang2Msg;
    }

    @JsonProperty("commissionProfileLang2Msg")
    public void setCommissionProfileLang2Msg(Object commissionProfileLang2Msg) {
        this.commissionProfileLang2Msg = commissionProfileLang2Msg;
    }

    @JsonProperty("commissionProfileSuspendMsg")
    public Object getCommissionProfileSuspendMsg() {
        return commissionProfileSuspendMsg;
    }

    @JsonProperty("commissionProfileSuspendMsg")
    public void setCommissionProfileSuspendMsg(Object commissionProfileSuspendMsg) {
        this.commissionProfileSuspendMsg = commissionProfileSuspendMsg;
    }

    @JsonProperty("transferCategory")
    public Object getTransferCategory() {
        return transferCategory;
    }

    @JsonProperty("transferCategory")
    public void setTransferCategory(Object transferCategory) {
        this.transferCategory = transferCategory;
    }

    @JsonProperty("commissionProfileApplicableFrom")
    public Object getCommissionProfileApplicableFrom() {
        return commissionProfileApplicableFrom;
    }

    @JsonProperty("commissionProfileApplicableFrom")
    public void setCommissionProfileApplicableFrom(Object commissionProfileApplicableFrom) {
        this.commissionProfileApplicableFrom = commissionProfileApplicableFrom;
    }

    @JsonProperty("userChargeGrouptypeCounters")
    public Object getUserChargeGrouptypeCounters() {
        return userChargeGrouptypeCounters;
    }

    @JsonProperty("userChargeGrouptypeCounters")
    public void setUserChargeGrouptypeCounters(Object userChargeGrouptypeCounters) {
        this.userChargeGrouptypeCounters = userChargeGrouptypeCounters;
    }

    @JsonProperty("userControlGrouptypeCounters")
    public Object getUserControlGrouptypeCounters() {
        return userControlGrouptypeCounters;
    }

    @JsonProperty("userControlGrouptypeCounters")
    public void setUserControlGrouptypeCounters(Object userControlGrouptypeCounters) {
        this.userControlGrouptypeCounters = userControlGrouptypeCounters;
    }

    @JsonProperty("accessType")
    public Object getAccessType() {
        return accessType;
    }

    @JsonProperty("accessType")
    public void setAccessType(Object accessType) {
        this.accessType = accessType;
    }

    @JsonProperty("applicationID")
    public Object getApplicationID() {
        return applicationID;
    }

    @JsonProperty("applicationID")
    public void setApplicationID(Object applicationID) {
        this.applicationID = applicationID;
    }

    @JsonProperty("mpayProfileID")
    public Object getMpayProfileID() {
        return mpayProfileID;
    }

    @JsonProperty("mpayProfileID")
    public void setMpayProfileID(Object mpayProfileID) {
        this.mpayProfileID = mpayProfileID;
    }

    @JsonProperty("userProfileID")
    public Object getUserProfileID() {
        return userProfileID;
    }

    @JsonProperty("userProfileID")
    public void setUserProfileID(Object userProfileID) {
        this.userProfileID = userProfileID;
    }

    @JsonProperty("mcommerceServiceAllow")
    public Object getMcommerceServiceAllow() {
        return mcommerceServiceAllow;
    }

    @JsonProperty("mcommerceServiceAllow")
    public void setMcommerceServiceAllow(Object mcommerceServiceAllow) {
        this.mcommerceServiceAllow = mcommerceServiceAllow;
    }

    @JsonProperty("lowBalAlertAllow")
    public Object getLowBalAlertAllow() {
        return lowBalAlertAllow;
    }

    @JsonProperty("lowBalAlertAllow")
    public void setLowBalAlertAllow(Object lowBalAlertAllow) {
        this.lowBalAlertAllow = lowBalAlertAllow;
    }

    @JsonProperty("primaryMsisdn")
    public Object getPrimaryMsisdn() {
        return primaryMsisdn;
    }

    @JsonProperty("primaryMsisdn")
    public void setPrimaryMsisdn(Object primaryMsisdn) {
        this.primaryMsisdn = primaryMsisdn;
    }

    @JsonProperty("balance")
    public Integer getBalance() {
        return balance;
    }

    @JsonProperty("balance")
    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    @JsonProperty("balanceStr")
    public Object getBalanceStr() {
        return balanceStr;
    }

    @JsonProperty("balanceStr")
    public void setBalanceStr(Object balanceStr) {
        this.balanceStr = balanceStr;
    }

    @JsonProperty("previousBalance")
    public Integer getPreviousBalance() {
        return previousBalance;
    }

    @JsonProperty("previousBalance")
    public void setPreviousBalance(Integer previousBalance) {
        this.previousBalance = previousBalance;
    }

    @JsonProperty("trnsfrdUserHierhyList")
    public Object getTrnsfrdUserHierhyList() {
        return trnsfrdUserHierhyList;
    }

    @JsonProperty("trnsfrdUserHierhyList")
    public void setTrnsfrdUserHierhyList(Object trnsfrdUserHierhyList) {
        this.trnsfrdUserHierhyList = trnsfrdUserHierhyList;
    }

    @JsonProperty("prevBalanceStr")
    public Object getPrevBalanceStr() {
        return prevBalanceStr;
    }

    @JsonProperty("prevBalanceStr")
    public void setPrevBalanceStr(Object prevBalanceStr) {
        this.prevBalanceStr = prevBalanceStr;
    }

    @JsonProperty("prevParentName")
    public Object getPrevParentName() {
        return prevParentName;
    }

    @JsonProperty("prevParentName")
    public void setPrevParentName(Object prevParentName) {
        this.prevParentName = prevParentName;
    }

    @JsonProperty("prevUserId")
    public Object getPrevUserId() {
        return prevUserId;
    }

    @JsonProperty("prevUserId")
    public void setPrevUserId(Object prevUserId) {
        this.prevUserId = prevUserId;
    }

    @JsonProperty("prevUserName")
    public Object getPrevUserName() {
        return prevUserName;
    }

    @JsonProperty("prevUserName")
    public void setPrevUserName(Object prevUserName) {
        this.prevUserName = prevUserName;
    }

    @JsonProperty("prevCategoryCode")
    public Object getPrevCategoryCode() {
        return prevCategoryCode;
    }

    @JsonProperty("prevCategoryCode")
    public void setPrevCategoryCode(Object prevCategoryCode) {
        this.prevCategoryCode = prevCategoryCode;
    }

    @JsonProperty("prevUserNameWithCategory")
    public String getPrevUserNameWithCategory() {
        return prevUserNameWithCategory;
    }

    @JsonProperty("prevUserNameWithCategory")
    public void setPrevUserNameWithCategory(String prevUserNameWithCategory) {
        this.prevUserNameWithCategory = prevUserNameWithCategory;
    }

    @JsonProperty("prevUserParentNameWithCategory")
    public String getPrevUserParentNameWithCategory() {
        return prevUserParentNameWithCategory;
    }

    @JsonProperty("prevUserParentNameWithCategory")
    public void setPrevUserParentNameWithCategory(String prevUserParentNameWithCategory) {
        this.prevUserParentNameWithCategory = prevUserParentNameWithCategory;
    }

    @JsonProperty("userBalance")
    public Object getUserBalance() {
        return userBalance;
    }

    @JsonProperty("userBalance")
    public void setUserBalance(Object userBalance) {
        this.userBalance = userBalance;
    }

    @JsonProperty("msisdnPrefix")
    public Object getMsisdnPrefix() {
        return msisdnPrefix;
    }

    @JsonProperty("msisdnPrefix")
    public void setMsisdnPrefix(Object msisdnPrefix) {
        this.msisdnPrefix = msisdnPrefix;
    }

    @JsonProperty("primaryMsisdnPin")
    public Object getPrimaryMsisdnPin() {
        return primaryMsisdnPin;
    }

    @JsonProperty("primaryMsisdnPin")
    public void setPrimaryMsisdnPin(Object primaryMsisdnPin) {
        this.primaryMsisdnPin = primaryMsisdnPin;
    }

    @JsonProperty("multipleMsisdnlist")
    public Object getMultipleMsisdnlist() {
        return multipleMsisdnlist;
    }

    @JsonProperty("multipleMsisdnlist")
    public void setMultipleMsisdnlist(Object multipleMsisdnlist) {
        this.multipleMsisdnlist = multipleMsisdnlist;
    }

    @JsonProperty("firstExternalUserModify")
    public Boolean getFirstExternalUserModify() {
        return firstExternalUserModify;
    }

    @JsonProperty("firstExternalUserModify")
    public void setFirstExternalUserModify(Boolean firstExternalUserModify) {
        this.firstExternalUserModify = firstExternalUserModify;
    }

    @JsonProperty("smsMSisdn")
    public Object getSmsMSisdn() {
        return smsMSisdn;
    }

    @JsonProperty("smsMSisdn")
    public void setSmsMSisdn(Object smsMSisdn) {
        this.smsMSisdn = smsMSisdn;
    }

    @JsonProperty("phoneProfile")
    public Object getPhoneProfile() {
        return phoneProfile;
    }

    @JsonProperty("phoneProfile")
    public void setPhoneProfile(Object phoneProfile) {
        this.phoneProfile = phoneProfile;
    }

    @JsonProperty("prefixId")
    public Integer getPrefixId() {
        return prefixId;
    }

    @JsonProperty("prefixId")
    public void setPrefixId(Integer prefixId) {
        this.prefixId = prefixId;
    }

    @JsonProperty("catLowBalanceAlertAllow")
    public Object getCatLowBalanceAlertAllow() {
        return catLowBalanceAlertAllow;
    }

    @JsonProperty("catLowBalanceAlertAllow")
    public void setCatLowBalanceAlertAllow(Object catLowBalanceAlertAllow) {
        this.catLowBalanceAlertAllow = catLowBalanceAlertAllow;
    }

    @JsonProperty("catOutletAllowed")
    public Object getCatOutletAllowed() {
        return catOutletAllowed;
    }

    @JsonProperty("catOutletAllowed")
    public void setCatOutletAllowed(Object catOutletAllowed) {
        this.catOutletAllowed = catOutletAllowed;
    }

    @JsonProperty("parentLocale")
    public Object getParentLocale() {
        return parentLocale;
    }

    @JsonProperty("parentLocale")
    public void setParentLocale(Object parentLocale) {
        this.parentLocale = parentLocale;
    }

    @JsonProperty("alertMsisdn")
    public Object getAlertMsisdn() {
        return alertMsisdn;
    }

    @JsonProperty("alertMsisdn")
    public void setAlertMsisdn(Object alertMsisdn) {
        this.alertMsisdn = alertMsisdn;
    }

    @JsonProperty("alertType")
    public Object getAlertType() {
        return alertType;
    }

    @JsonProperty("alertType")
    public void setAlertType(Object alertType) {
        this.alertType = alertType;
    }

    @JsonProperty("alertEmail")
    public Object getAlertEmail() {
        return alertEmail;
    }

    @JsonProperty("alertEmail")
    public void setAlertEmail(Object alertEmail) {
        this.alertEmail = alertEmail;
    }

    @JsonProperty("trannferRuleTypeId")
    public Object getTrannferRuleTypeId() {
        return trannferRuleTypeId;
    }

    @JsonProperty("trannferRuleTypeId")
    public void setTrannferRuleTypeId(Object trannferRuleTypeId) {
        this.trannferRuleTypeId = trannferRuleTypeId;
    }

    @JsonProperty("cellID")
    public Object getCellID() {
        return cellID;
    }

    @JsonProperty("cellID")
    public void setCellID(Object cellID) {
        this.cellID = cellID;
    }

    @JsonProperty("switchID")
    public Object getSwitchID() {
        return switchID;
    }

    @JsonProperty("switchID")
    public void setSwitchID(Object switchID) {
        this.switchID = switchID;
    }

    @JsonProperty("lmsProfile")
    public Object getLmsProfile() {
        return lmsProfile;
    }

    @JsonProperty("lmsProfile")
    public void setLmsProfile(Object lmsProfile) {
        this.lmsProfile = lmsProfile;
    }

    @JsonProperty("otpModifiedOn")
    public Object getOtpModifiedOn() {
        return otpModifiedOn;
    }

    @JsonProperty("otpModifiedOn")
    public void setOtpModifiedOn(Object otpModifiedOn) {
        this.otpModifiedOn = otpModifiedOn;
    }

    @JsonProperty("autoc2callowed")
    public Object getAutoc2callowed() {
        return autoc2callowed;
    }

    @JsonProperty("autoc2callowed")
    public void setAutoc2callowed(Object autoc2callowed) {
        this.autoc2callowed = autoc2callowed;
    }

    @JsonProperty("autoc2cquantity")
    public Object getAutoc2cquantity() {
        return autoc2cquantity;
    }

    @JsonProperty("autoc2cquantity")
    public void setAutoc2cquantity(Object autoc2cquantity) {
        this.autoc2cquantity = autoc2cquantity;
    }

    @JsonProperty("channelUserID")
    public Object getChannelUserID() {
        return channelUserID;
    }

    @JsonProperty("channelUserID")
    public void setChannelUserID(Object channelUserID) {
        this.channelUserID = channelUserID;
    }

    @JsonProperty("maxTxnAmount")
    public Integer getMaxTxnAmount() {
        return maxTxnAmount;
    }

    @JsonProperty("maxTxnAmount")
    public void setMaxTxnAmount(Integer maxTxnAmount) {
        this.maxTxnAmount = maxTxnAmount;
    }

    @JsonProperty("lmsProfileId")
    public Object getLmsProfileId() {
        return lmsProfileId;
    }

    @JsonProperty("lmsProfileId")
    public void setLmsProfileId(Object lmsProfileId) {
        this.lmsProfileId = lmsProfileId;
    }

    @JsonProperty("parentGeographyCode")
    public Object getParentGeographyCode() {
        return parentGeographyCode;
    }

    @JsonProperty("parentGeographyCode")
    public void setParentGeographyCode(Object parentGeographyCode) {
        this.parentGeographyCode = parentGeographyCode;
    }

    @JsonProperty("decryptionKey")
    public Object getDecryptionKey() {
        return decryptionKey;
    }

    @JsonProperty("decryptionKey")
    public void setDecryptionKey(Object decryptionKey) {
        this.decryptionKey = decryptionKey;
    }

    @JsonProperty("asscMsisdnList")
    public Object getAsscMsisdnList() {
        return asscMsisdnList;
    }

    @JsonProperty("asscMsisdnList")
    public void setAsscMsisdnList(Object asscMsisdnList) {
        this.asscMsisdnList = asscMsisdnList;
    }

    @JsonProperty("asscMsisdnDate")
    public Object getAsscMsisdnDate() {
        return asscMsisdnDate;
    }

    @JsonProperty("asscMsisdnDate")
    public void setAsscMsisdnDate(Object asscMsisdnDate) {
        this.asscMsisdnDate = asscMsisdnDate;
    }

    @JsonProperty("optInOutStatus")
    public Object getOptInOutStatus() {
        return optInOutStatus;
    }

    @JsonProperty("optInOutStatus")
    public void setOptInOutStatus(Object optInOutStatus) {
        this.optInOutStatus = optInOutStatus;
    }

    @JsonProperty("controlGroup")
    public Object getControlGroup() {
        return controlGroup;
    }

    @JsonProperty("controlGroup")
    public void setControlGroup(Object controlGroup) {
        this.controlGroup = controlGroup;
    }

    @JsonProperty("resetPinOTPMessage")
    public Object getResetPinOTPMessage() {
        return resetPinOTPMessage;
    }

    @JsonProperty("resetPinOTPMessage")
    public void setResetPinOTPMessage(Object resetPinOTPMessage) {
        this.resetPinOTPMessage = resetPinOTPMessage;
    }

    @JsonProperty("securityAnswer")
    public Object getSecurityAnswer() {
        return securityAnswer;
    }

    @JsonProperty("securityAnswer")
    public void setSecurityAnswer(Object securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    @JsonProperty("otpInvalidCount")
    public Integer getOtpInvalidCount() {
        return otpInvalidCount;
    }

    @JsonProperty("otpInvalidCount")
    public void setOtpInvalidCount(Integer otpInvalidCount) {
        this.otpInvalidCount = otpInvalidCount;
    }

    @JsonProperty("othCommSetId")
    public Object getOthCommSetId() {
        return othCommSetId;
    }

    @JsonProperty("othCommSetId")
    public void setOthCommSetId(Object othCommSetId) {
        this.othCommSetId = othCommSetId;
    }

    @JsonProperty("categoryList")
    public Object getCategoryList() {
        return categoryList;
    }

    @JsonProperty("categoryList")
    public void setCategoryList(Object categoryList) {
        this.categoryList = categoryList;
    }

    @JsonProperty("returnFlag")
    public Boolean getReturnFlag() {
        return returnFlag;
    }

    @JsonProperty("returnFlag")
    public void setReturnFlag(Boolean returnFlag) {
        this.returnFlag = returnFlag;
    }

    @JsonProperty("geographicalCodeforNewuser")
    public Object getGeographicalCodeforNewuser() {
        return geographicalCodeforNewuser;
    }

    @JsonProperty("geographicalCodeforNewuser")
    public void setGeographicalCodeforNewuser(Object geographicalCodeforNewuser) {
        this.geographicalCodeforNewuser = geographicalCodeforNewuser;
    }

    @JsonProperty("gateway")
    public Object getGateway() {
        return gateway;
    }

    @JsonProperty("gateway")
    public void setGateway(Object gateway) {
        this.gateway = gateway;
    }

    @JsonProperty("monthlyTransAmt")
    public Integer getMonthlyTransAmt() {
        return monthlyTransAmt;
    }

    @JsonProperty("monthlyTransAmt")
    public void setMonthlyTransAmt(Integer monthlyTransAmt) {
        this.monthlyTransAmt = monthlyTransAmt;
    }

    @JsonProperty("groupRoleFlag")
    public Object getGroupRoleFlag() {
        return groupRoleFlag;
    }

    @JsonProperty("groupRoleFlag")
    public void setGroupRoleFlag(Object groupRoleFlag) {
        this.groupRoleFlag = groupRoleFlag;
    }

    @JsonProperty("parentStatus")
    public Object getParentStatus() {
        return parentStatus;
    }

    @JsonProperty("parentStatus")
    public void setParentStatus(Object parentStatus) {
        this.parentStatus = parentStatus;
    }

    @JsonProperty("voucherTypes")
    public Object getVoucherTypes() {
        return voucherTypes;
    }

    @JsonProperty("voucherTypes")
    public void setVoucherTypes(Object voucherTypes) {
        this.voucherTypes = voucherTypes;
    }

    @JsonProperty("parentLoginID")
    public Object getParentLoginID() {
        return parentLoginID;
    }

    @JsonProperty("parentLoginID")
    public void setParentLoginID(Object parentLoginID) {
        this.parentLoginID = parentLoginID;
    }

    @JsonProperty("groupRoleCode")
    public Object getGroupRoleCode() {
        return groupRoleCode;
    }

    @JsonProperty("groupRoleCode")
    public void setGroupRoleCode(Object groupRoleCode) {
        this.groupRoleCode = groupRoleCode;
    }

    @JsonProperty("recordNumber")
    public Object getRecordNumber() {
        return recordNumber;
    }

    @JsonProperty("recordNumber")
    public void setRecordNumber(Object recordNumber) {
        this.recordNumber = recordNumber;
    }

    @JsonProperty("languageCode")
    public String getLanguageCode() {
        return languageCode;
    }

    @JsonProperty("languageCode")
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @JsonProperty("languageName")
    public String getLanguageName() {
        return languageName;
    }

    @JsonProperty("languageName")
    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    @JsonProperty("invalidPinCount")
    public Integer getInvalidPinCount() {
        return invalidPinCount;
    }

    @JsonProperty("invalidPinCount")
    public void setInvalidPinCount(Integer invalidPinCount) {
        this.invalidPinCount = invalidPinCount;
    }

    @JsonProperty("userGrade")
    public Object getUserGrade() {
        return userGrade;
    }

    @JsonProperty("userGrade")
    public void setUserGrade(Object userGrade) {
        this.userGrade = userGrade;
    }

    @JsonProperty("commissionProfileSetID")
    public Object getCommissionProfileSetID() {
        return commissionProfileSetID;
    }

    @JsonProperty("commissionProfileSetID")
    public void setCommissionProfileSetID(Object commissionProfileSetID) {
        this.commissionProfileSetID = commissionProfileSetID;
    }

    @JsonProperty("transferProfileID")
    public Object getTransferProfileID() {
        return transferProfileID;
    }

    @JsonProperty("transferProfileID")
    public void setTransferProfileID(Object transferProfileID) {
        this.transferProfileID = transferProfileID;
    }

    @JsonProperty("inSuspend")
    public Object getInSuspend() {
        return inSuspend;
    }

    @JsonProperty("inSuspend")
    public void setInSuspend(Object inSuspend) {
        this.inSuspend = inSuspend;
    }

    @JsonProperty("outSuspened")
    public Object getOutSuspened() {
        return outSuspened;
    }

    @JsonProperty("outSuspened")
    public void setOutSuspened(Object outSuspened) {
        this.outSuspened = outSuspened;
    }

    @JsonProperty("commissionProfileSetName")
    public Object getCommissionProfileSetName() {
        return commissionProfileSetName;
    }

    @JsonProperty("commissionProfileSetName")
    public void setCommissionProfileSetName(Object commissionProfileSetName) {
        this.commissionProfileSetName = commissionProfileSetName;
    }

    @JsonProperty("userGradeName")
    public Object getUserGradeName() {
        return userGradeName;
    }

    @JsonProperty("userGradeName")
    public void setUserGradeName(Object userGradeName) {
        this.userGradeName = userGradeName;
    }

    @JsonProperty("commissionProfileSetVersion")
    public Object getCommissionProfileSetVersion() {
        return commissionProfileSetVersion;
    }

    @JsonProperty("commissionProfileSetVersion")
    public void setCommissionProfileSetVersion(Object commissionProfileSetVersion) {
        this.commissionProfileSetVersion = commissionProfileSetVersion;
    }

    @JsonProperty("transferProfileName")
    public Object getTransferProfileName() {
        return transferProfileName;
    }

    @JsonProperty("transferProfileName")
    public void setTransferProfileName(Object transferProfileName) {
        this.transferProfileName = transferProfileName;
    }

    @JsonProperty("categoryName")
    public Object getCategoryName() {
        return categoryName;
    }

    @JsonProperty("categoryName")
    public void setCategoryName(Object categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("smsPin")
    public Object getSmsPin() {
        return smsPin;
    }

    @JsonProperty("smsPin")
    public void setSmsPin(Object smsPin) {
        this.smsPin = smsPin;
    }

    @JsonProperty("userlevel")
    public Object getUserlevel() {
        return userlevel;
    }

    @JsonProperty("userlevel")
    public void setUserlevel(Object userlevel) {
        this.userlevel = userlevel;
    }

    @JsonProperty("userIDPrefix")
    public Object getUserIDPrefix() {
        return userIDPrefix;
    }

    @JsonProperty("userIDPrefix")
    public void setUserIDPrefix(Object userIDPrefix) {
        this.userIDPrefix = userIDPrefix;
    }

    @JsonProperty("geographicalDesc")
    public Object getGeographicalDesc() {
        return geographicalDesc;
    }

    @JsonProperty("geographicalDesc")
    public void setGeographicalDesc(Object geographicalDesc) {
        this.geographicalDesc = geographicalDesc;
    }

    @JsonProperty("commissionProfileStatus")
    public Object getCommissionProfileStatus() {
        return commissionProfileStatus;
    }

    @JsonProperty("commissionProfileStatus")
    public void setCommissionProfileStatus(Object commissionProfileStatus) {
        this.commissionProfileStatus = commissionProfileStatus;
    }

    @JsonProperty("transferProfileStatus")
    public Object getTransferProfileStatus() {
        return transferProfileStatus;
    }

    @JsonProperty("transferProfileStatus")
    public void setTransferProfileStatus(Object transferProfileStatus) {
        this.transferProfileStatus = transferProfileStatus;
    }

    @JsonProperty("transferRuleID")
    public Object getTransferRuleID() {
        return transferRuleID;
    }

    @JsonProperty("transferRuleID")
    public void setTransferRuleID(Object transferRuleID) {
        this.transferRuleID = transferRuleID;
    }

    @JsonProperty("outletCode")
    public Object getOutletCode() {
        return outletCode;
    }

    @JsonProperty("outletCode")
    public void setOutletCode(Object outletCode) {
        this.outletCode = outletCode;
    }

    @JsonProperty("subOutletCode")
    public Object getSubOutletCode() {
        return subOutletCode;
    }

    @JsonProperty("subOutletCode")
    public void setSubOutletCode(Object subOutletCode) {
        this.subOutletCode = subOutletCode;
    }

    @JsonProperty("activatedOn")
    public Object getActivatedOn() {
        return activatedOn;
    }

    @JsonProperty("activatedOn")
    public void setActivatedOn(Object activatedOn) {
        this.activatedOn = activatedOn;
    }

    @JsonProperty("categoryCode")
    public Object getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(Object categoryCode) {
        this.categoryCode = categoryCode;
    }

    @JsonProperty("networkID")
    public Object getNetworkID() {
        return networkID;
    }

    @JsonProperty("networkID")
    public void setNetworkID(Object networkID) {
        this.networkID = networkID;
    }

    @JsonProperty("sessionInfoVO")
    public Object getSessionInfoVO() {
        return sessionInfoVO;
    }

    @JsonProperty("sessionInfoVO")
    public void setSessionInfoVO(Object sessionInfoVO) {
        this.sessionInfoVO = sessionInfoVO;
    }

    @JsonProperty("userID")
    public Object getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(Object userID) {
        this.userID = userID;
    }

    @JsonProperty("loggerMessage")
    public Object getLoggerMessage() {
        return loggerMessage;
    }

    @JsonProperty("loggerMessage")
    public void setLoggerMessage(Object loggerMessage) {
        this.loggerMessage = loggerMessage;
    }

    @JsonProperty("loginID")
    public Object getLoginID() {
        return loginID;
    }

    @JsonProperty("loginID")
    public void setLoginID(Object loginID) {
        this.loginID = loginID;
    }

    @JsonProperty("networkName")
    public Object getNetworkName() {
        return networkName;
    }

    @JsonProperty("networkName")
    public void setNetworkName(Object networkName) {
        this.networkName = networkName;
    }

    @JsonProperty("userName")
    public Object getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(Object userName) {
        this.userName = userName;
    }

    @JsonProperty("userType")
    public Object getUserType() {
        return userType;
    }

    @JsonProperty("userType")
    public void setUserType(Object userType) {
        this.userType = userType;
    }

    @JsonProperty("domainID")
    public Object getDomainID() {
        return domainID;
    }

    @JsonProperty("domainID")
    public void setDomainID(Object domainID) {
        this.domainID = domainID;
    }

    @JsonProperty("loginTime")
    public Object getLoginTime() {
        return loginTime;
    }

    @JsonProperty("loginTime")
    public void setLoginTime(Object loginTime) {
        this.loginTime = loginTime;
    }

    @JsonProperty("browserType")
    public Object getBrowserType() {
        return browserType;
    }

    @JsonProperty("browserType")
    public void setBrowserType(Object browserType) {
        this.browserType = browserType;
    }

    @JsonProperty("currentRoleCode")
    public Object getCurrentRoleCode() {
        return currentRoleCode;
    }

    @JsonProperty("currentRoleCode")
    public void setCurrentRoleCode(Object currentRoleCode) {
        this.currentRoleCode = currentRoleCode;
    }

    @JsonProperty("firstName")
    public Object getFirstName() {
        return firstName;
    }

    @JsonProperty("firstName")
    public void setFirstName(Object firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("lastName")
    public Object getLastName() {
        return lastName;
    }

    @JsonProperty("lastName")
    public void setLastName(Object lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("language")
    public Object getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(Object language) {
        this.language = language;
    }

    @JsonProperty("rsaFlag")
    public Object getRsaFlag() {
        return rsaFlag;
    }

    @JsonProperty("rsaFlag")
    public void setRsaFlag(Object rsaFlag) {
        this.rsaFlag = rsaFlag;
    }

    @JsonProperty("rsaAllowed")
    public Boolean getRsaAllowed() {
        return rsaAllowed;
    }

    @JsonProperty("rsaAllowed")
    public void setRsaAllowed(Boolean rsaAllowed) {
        this.rsaAllowed = rsaAllowed;
    }

    @JsonProperty("rsavalidated")
    public Boolean getRsavalidated() {
        return rsavalidated;
    }

    @JsonProperty("rsavalidated")
    public void setRsavalidated(Boolean rsavalidated) {
        this.rsavalidated = rsavalidated;
    }

    @JsonProperty("otpvalidated")
    public Boolean getOtpvalidated() {
        return otpvalidated;
    }

    @JsonProperty("otpvalidated")
    public void setOtpvalidated(Boolean otpvalidated) {
        this.otpvalidated = otpvalidated;
    }

    @JsonProperty("authTypeAllowed")
    public Object getAuthTypeAllowed() {
        return authTypeAllowed;
    }

    @JsonProperty("authTypeAllowed")
    public void setAuthTypeAllowed(Object authTypeAllowed) {
        this.authTypeAllowed = authTypeAllowed;
    }

    @JsonProperty("rolesMapSelectedCount")
    public Integer getRolesMapSelectedCount() {
        return rolesMapSelectedCount;
    }

    @JsonProperty("rolesMapSelectedCount")
    public void setRolesMapSelectedCount(Integer rolesMapSelectedCount) {
        this.rolesMapSelectedCount = rolesMapSelectedCount;
    }

    @JsonProperty("documentType")
    public Object getDocumentType() {
        return documentType;
    }

    @JsonProperty("documentType")
    public void setDocumentType(Object documentType) {
        this.documentType = documentType;
    }

    @JsonProperty("documentNo")
    public Object getDocumentNo() {
        return documentNo;
    }

    @JsonProperty("documentNo")
    public void setDocumentNo(Object documentNo) {
        this.documentNo = documentNo;
    }

    @JsonProperty("paymentType")
    public Object getPaymentType() {
        return paymentType;
    }

    @JsonProperty("paymentType")
    public void setPaymentType(Object paymentType) {
        this.paymentType = paymentType;
    }

    @JsonProperty("ownerCompany")
    public Object getOwnerCompany() {
        return ownerCompany;
    }

    @JsonProperty("ownerCompany")
    public void setOwnerCompany(Object ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    @JsonProperty("password")
    public Object getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(Object password) {
        this.password = password;
    }

    @JsonProperty("createdBy")
    public Object getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("status")
    public Object getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Object status) {
        this.status = status;
    }

    @JsonProperty("message")
    public Object getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(Object message) {
        this.message = message;
    }

    @JsonProperty("authType")
    public Object getAuthType() {
        return authType;
    }

    @JsonProperty("authType")
    public void setAuthType(Object authType) {
        this.authType = authType;
    }

    @JsonProperty("divisionList")
    public Object getDivisionList() {
        return divisionList;
    }

    @JsonProperty("divisionList")
    public void setDivisionList(Object divisionList) {
        this.divisionList = divisionList;
    }

    @JsonProperty("departmentList")
    public Object getDepartmentList() {
        return departmentList;
    }

    @JsonProperty("departmentList")
    public void setDepartmentList(Object departmentList) {
        this.departmentList = departmentList;
    }

    @JsonProperty("associatedGeographicalList")
    public Object getAssociatedGeographicalList() {
        return associatedGeographicalList;
    }

    @JsonProperty("associatedGeographicalList")
    public void setAssociatedGeographicalList(Object associatedGeographicalList) {
        this.associatedGeographicalList = associatedGeographicalList;
    }

    @JsonProperty("rsaRequired")
    public Boolean getRsaRequired() {
        return rsaRequired;
    }

    @JsonProperty("rsaRequired")
    public void setRsaRequired(Boolean rsaRequired) {
        this.rsaRequired = rsaRequired;
    }

    @JsonProperty("countryCode")
    public Object getCountryCode() {
        return countryCode;
    }

    @JsonProperty("countryCode")
    public void setCountryCode(Object countryCode) {
        this.countryCode = countryCode;
    }

    @JsonProperty("assType")
    public Object getAssType() {
        return assType;
    }

    @JsonProperty("assType")
    public void setAssType(Object assType) {
        this.assType = assType;
    }

    @JsonProperty("assoMsisdn")
    public Object getAssoMsisdn() {
        return assoMsisdn;
    }

    @JsonProperty("assoMsisdn")
    public void setAssoMsisdn(Object assoMsisdn) {
        this.assoMsisdn = assoMsisdn;
    }

    @JsonProperty("associationCreatedOn")
    public Object getAssociationCreatedOn() {
        return associationCreatedOn;
    }

    @JsonProperty("associationCreatedOn")
    public void setAssociationCreatedOn(Object associationCreatedOn) {
        this.associationCreatedOn = associationCreatedOn;
    }

    @JsonProperty("associationModifiedOn")
    public Object getAssociationModifiedOn() {
        return associationModifiedOn;
    }

    @JsonProperty("associationModifiedOn")
    public void setAssociationModifiedOn(Object associationModifiedOn) {
        this.associationModifiedOn = associationModifiedOn;
    }

    @JsonProperty("oldLastLoginOn")
    public Object getOldLastLoginOn() {
        return oldLastLoginOn;
    }

    @JsonProperty("oldLastLoginOn")
    public void setOldLastLoginOn(Object oldLastLoginOn) {
        this.oldLastLoginOn = oldLastLoginOn;
    }

    @JsonProperty("c2sMisFromDate")
    public Object getC2sMisFromDate() {
        return c2sMisFromDate;
    }

    @JsonProperty("c2sMisFromDate")
    public void setC2sMisFromDate(Object c2sMisFromDate) {
        this.c2sMisFromDate = c2sMisFromDate;
    }

    @JsonProperty("c2sMisToDate")
    public Object getC2sMisToDate() {
        return c2sMisToDate;
    }

    @JsonProperty("c2sMisToDate")
    public void setC2sMisToDate(Object c2sMisToDate) {
        this.c2sMisToDate = c2sMisToDate;
    }

    @JsonProperty("p2pMisFromDate")
    public Object getP2pMisFromDate() {
        return p2pMisFromDate;
    }

    @JsonProperty("p2pMisFromDate")
    public void setP2pMisFromDate(Object p2pMisFromDate) {
        this.p2pMisFromDate = p2pMisFromDate;
    }

    @JsonProperty("p2pMisToDate")
    public Object getP2pMisToDate() {
        return p2pMisToDate;
    }

    @JsonProperty("p2pMisToDate")
    public void setP2pMisToDate(Object p2pMisToDate) {
        this.p2pMisToDate = p2pMisToDate;
    }

    @JsonProperty("contactPerson")
    public Object getContactPerson() {
        return contactPerson;
    }

    @JsonProperty("contactPerson")
    public void setContactPerson(Object contactPerson) {
        this.contactPerson = contactPerson;
    }

    @JsonProperty("staffUser")
    public Boolean getStaffUser() {
        return staffUser;
    }

    @JsonProperty("staffUser")
    public void setStaffUser(Boolean staffUser) {
        this.staffUser = staffUser;
    }

    @JsonProperty("activeUserID")
    public Object getActiveUserID() {
        return activeUserID;
    }

    @JsonProperty("activeUserID")
    public void setActiveUserID(Object activeUserID) {
        this.activeUserID = activeUserID;
    }

    @JsonProperty("remarks")
    public Object getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(Object remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("batchName")
    public Object getBatchName() {
        return batchName;
    }

    @JsonProperty("batchName")
    public void setBatchName(Object batchName) {
        this.batchName = batchName;
    }

    @JsonProperty("batchID")
    public Object getBatchID() {
        return batchID;
    }

    @JsonProperty("batchID")
    public void setBatchID(Object batchID) {
        this.batchID = batchID;
    }

    @JsonProperty("creationType")
    public Object getCreationType() {
        return creationType;
    }

    @JsonProperty("creationType")
    public void setCreationType(Object creationType) {
        this.creationType = creationType;
    }

    @JsonProperty("domainCodes")
    public Object getDomainCodes() {
        return domainCodes;
    }

    @JsonProperty("domainCodes")
    public void setDomainCodes(Object domainCodes) {
        this.domainCodes = domainCodes;
    }

    @JsonProperty("passwordModifyFlag")
    public Boolean getPasswordModifyFlag() {
        return passwordModifyFlag;
    }

    @JsonProperty("passwordModifyFlag")
    public void setPasswordModifyFlag(Boolean passwordModifyFlag) {
        this.passwordModifyFlag = passwordModifyFlag;
    }

    @JsonProperty("reportHeaderName")
    public Object getReportHeaderName() {
        return reportHeaderName;
    }

    @JsonProperty("reportHeaderName")
    public void setReportHeaderName(Object reportHeaderName) {
        this.reportHeaderName = reportHeaderName;
    }

    @JsonProperty("invalidPasswordCount")
    public Integer getInvalidPasswordCount() {
        return invalidPasswordCount;
    }

    @JsonProperty("invalidPasswordCount")
    public void setInvalidPasswordCount(Integer invalidPasswordCount) {
        this.invalidPasswordCount = invalidPasswordCount;
    }

    @JsonProperty("passwordCountUpdatedOn")
    public Object getPasswordCountUpdatedOn() {
        return passwordCountUpdatedOn;
    }

    @JsonProperty("passwordCountUpdatedOn")
    public void setPasswordCountUpdatedOn(Object passwordCountUpdatedOn) {
        this.passwordCountUpdatedOn = passwordCountUpdatedOn;
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
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
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

    @JsonProperty("userCode")
    public Object getUserCode() {
        return userCode;
    }

    @JsonProperty("userCode")
    public void setUserCode(Object userCode) {
        this.userCode = userCode;
    }

    @JsonProperty("ssn")
    public Object getSsn() {
        return ssn;
    }

    @JsonProperty("ssn")
    public void setSsn(Object ssn) {
        this.ssn = ssn;
    }

    @JsonProperty("userNamePrefix")
    public Object getUserNamePrefix() {
        return userNamePrefix;
    }

    @JsonProperty("userNamePrefix")
    public void setUserNamePrefix(Object userNamePrefix) {
        this.userNamePrefix = userNamePrefix;
    }

    @JsonProperty("msisdn")
    public Object getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(Object msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("company")
    public Object getCompany() {
        return company;
    }

    @JsonProperty("company")
    public void setCompany(Object company) {
        this.company = company;
    }

    @JsonProperty("fax")
    public Object getFax() {
        return fax;
    }

    @JsonProperty("fax")
    public void setFax(Object fax) {
        this.fax = fax;
    }

    @JsonProperty("categoryVO")
    public Object getCategoryVO() {
        return categoryVO;
    }

    @JsonProperty("categoryVO")
    public void setCategoryVO(Object categoryVO) {
        this.categoryVO = categoryVO;
    }

    @JsonProperty("remoteAddress")
    public Object getRemoteAddress() {
        return remoteAddress;
    }

    @JsonProperty("remoteAddress")
    public void setRemoteAddress(Object remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @JsonProperty("statusDesc")
    public Object getStatusDesc() {
        return statusDesc;
    }

    @JsonProperty("statusDesc")
    public void setStatusDesc(Object statusDesc) {
        this.statusDesc = statusDesc;
    }

    @JsonProperty("level1ApprovedBy")
    public Object getLevel1ApprovedBy() {
        return level1ApprovedBy;
    }

    @JsonProperty("level1ApprovedBy")
    public void setLevel1ApprovedBy(Object level1ApprovedBy) {
        this.level1ApprovedBy = level1ApprovedBy;
    }

    @JsonProperty("level1ApprovedOn")
    public Object getLevel1ApprovedOn() {
        return level1ApprovedOn;
    }

    @JsonProperty("level1ApprovedOn")
    public void setLevel1ApprovedOn(Object level1ApprovedOn) {
        this.level1ApprovedOn = level1ApprovedOn;
    }

    @JsonProperty("level2ApprovedBy")
    public Object getLevel2ApprovedBy() {
        return level2ApprovedBy;
    }

    @JsonProperty("level2ApprovedBy")
    public void setLevel2ApprovedBy(Object level2ApprovedBy) {
        this.level2ApprovedBy = level2ApprovedBy;
    }

    @JsonProperty("level2ApprovedOn")
    public Object getLevel2ApprovedOn() {
        return level2ApprovedOn;
    }

    @JsonProperty("level2ApprovedOn")
    public void setLevel2ApprovedOn(Object level2ApprovedOn) {
        this.level2ApprovedOn = level2ApprovedOn;
    }

    @JsonProperty("createdByUserName")
    public Object getCreatedByUserName() {
        return createdByUserName;
    }

    @JsonProperty("createdByUserName")
    public void setCreatedByUserName(Object createdByUserName) {
        this.createdByUserName = createdByUserName;
    }

    @JsonProperty("ownerCategoryName")
    public Object getOwnerCategoryName() {
        return ownerCategoryName;
    }

    @JsonProperty("ownerCategoryName")
    public void setOwnerCategoryName(Object ownerCategoryName) {
        this.ownerCategoryName = ownerCategoryName;
    }

    @JsonProperty("ownerMsisdn")
    public Object getOwnerMsisdn() {
        return ownerMsisdn;
    }

    @JsonProperty("ownerMsisdn")
    public void setOwnerMsisdn(Object ownerMsisdn) {
        this.ownerMsisdn = ownerMsisdn;
    }

    @JsonProperty("ownerName")
    public Object getOwnerName() {
        return ownerName;
    }

    @JsonProperty("ownerName")
    public void setOwnerName(Object ownerName) {
        this.ownerName = ownerName;
    }

    @JsonProperty("parentCategoryName")
    public Object getParentCategoryName() {
        return parentCategoryName;
    }

    @JsonProperty("parentCategoryName")
    public void setParentCategoryName(Object parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    @JsonProperty("parentMsisdn")
    public Object getParentMsisdn() {
        return parentMsisdn;
    }

    @JsonProperty("parentMsisdn")
    public void setParentMsisdn(Object parentMsisdn) {
        this.parentMsisdn = parentMsisdn;
    }

    @JsonProperty("domainName")
    public Object getDomainName() {
        return domainName;
    }

    @JsonProperty("domainName")
    public void setDomainName(Object domainName) {
        this.domainName = domainName;
    }

    @JsonProperty("userBalanceList")
    public Object getUserBalanceList() {
        return userBalanceList;
    }

    @JsonProperty("userBalanceList")
    public void setUserBalanceList(Object userBalanceList) {
        this.userBalanceList = userBalanceList;
    }

    @JsonProperty("domainList")
    public Object getDomainList() {
        return domainList;
    }

    @JsonProperty("domainList")
    public void setDomainList(Object domainList) {
        this.domainList = domainList;
    }

    @JsonProperty("serviceList")
    public Object getServiceList() {
        return serviceList;
    }

    @JsonProperty("serviceList")
    public void setServiceList(Object serviceList) {
        this.serviceList = serviceList;
    }

    @JsonProperty("voucherList")
    public Object getVoucherList() {
        return voucherList;
    }

    @JsonProperty("voucherList")
    public void setVoucherList(Object voucherList) {
        this.voucherList = voucherList;
    }

    @JsonProperty("geographicalCodeStatus")
    public Object getGeographicalCodeStatus() {
        return geographicalCodeStatus;
    }

    @JsonProperty("geographicalCodeStatus")
    public void setGeographicalCodeStatus(Object geographicalCodeStatus) {
        this.geographicalCodeStatus = geographicalCodeStatus;
    }

    @JsonProperty("associatedProductTypeList")
    public Object getAssociatedProductTypeList() {
        return associatedProductTypeList;
    }

    @JsonProperty("associatedProductTypeList")
    public void setAssociatedProductTypeList(Object associatedProductTypeList) {
        this.associatedProductTypeList = associatedProductTypeList;
    }

    @JsonProperty("appointmentDate")
    public Object getAppointmentDate() {
        return appointmentDate;
    }

    @JsonProperty("appointmentDate")
    public void setAppointmentDate(Object appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    @JsonProperty("userNameWithCategory")
    public String getUserNameWithCategory() {
        return userNameWithCategory;
    }

    @JsonProperty("userNameWithCategory")
    public void setUserNameWithCategory(String userNameWithCategory) {
        this.userNameWithCategory = userNameWithCategory;
    }

    @JsonProperty("requetedByUserName")
    public Object getRequetedByUserName() {
        return requetedByUserName;
    }

    @JsonProperty("requetedByUserName")
    public void setRequetedByUserName(Object requetedByUserName) {
        this.requetedByUserName = requetedByUserName;
    }

    @JsonProperty("requetedOnAsString")
    public String getRequetedOnAsString() {
        return requetedOnAsString;
    }

    @JsonProperty("requetedOnAsString")
    public void setRequetedOnAsString(String requetedOnAsString) {
        this.requetedOnAsString = requetedOnAsString;
    }

    @JsonProperty("suspendedByUserName")
    public Object getSuspendedByUserName() {
        return suspendedByUserName;
    }

    @JsonProperty("suspendedByUserName")
    public void setSuspendedByUserName(Object suspendedByUserName) {
        this.suspendedByUserName = suspendedByUserName;
    }

    @JsonProperty("suspendedOnAsString")
    public String getSuspendedOnAsString() {
        return suspendedOnAsString;
    }

    @JsonProperty("suspendedOnAsString")
    public void setSuspendedOnAsString(String suspendedOnAsString) {
        this.suspendedOnAsString = suspendedOnAsString;
    }

    @JsonProperty("domainStatus")
    public Object getDomainStatus() {
        return domainStatus;
    }

    @JsonProperty("domainStatus")
    public void setDomainStatus(Object domainStatus) {
        this.domainStatus = domainStatus;
    }

    @JsonProperty("currentModule")
    public Object getCurrentModule() {
        return currentModule;
    }

    @JsonProperty("currentModule")
    public void setCurrentModule(Object currentModule) {
        this.currentModule = currentModule;
    }

    @JsonProperty("domainTypeCode")
    public Object getDomainTypeCode() {
        return domainTypeCode;
    }

    @JsonProperty("domainTypeCode")
    public void setDomainTypeCode(Object domainTypeCode) {
        this.domainTypeCode = domainTypeCode;
    }

    @JsonProperty("restrictedMsisdnAllow")
    public Object getRestrictedMsisdnAllow() {
        return restrictedMsisdnAllow;
    }

    @JsonProperty("restrictedMsisdnAllow")
    public void setRestrictedMsisdnAllow(Object restrictedMsisdnAllow) {
        this.restrictedMsisdnAllow = restrictedMsisdnAllow;
    }

    @JsonProperty("moduleCodeString")
    public Object getModuleCodeString() {
        return moduleCodeString;
    }

    @JsonProperty("moduleCodeString")
    public void setModuleCodeString(Object moduleCodeString) {
        this.moduleCodeString = moduleCodeString;
    }

    @JsonProperty("pageCodeString")
    public Object getPageCodeString() {
        return pageCodeString;
    }

    @JsonProperty("pageCodeString")
    public void setPageCodeString(Object pageCodeString) {
        this.pageCodeString = pageCodeString;
    }

    @JsonProperty("passwordReset")
    public Object getPasswordReset() {
        return passwordReset;
    }

    @JsonProperty("passwordReset")
    public void setPasswordReset(Object passwordReset) {
        this.passwordReset = passwordReset;
    }

    @JsonProperty("pinReset")
    public Object getPinReset() {
        return pinReset;
    }

    @JsonProperty("pinReset")
    public void setPinReset(Object pinReset) {
        this.pinReset = pinReset;
    }

    @JsonProperty("activeUserMsisdn")
    public Object getActiveUserMsisdn() {
        return activeUserMsisdn;
    }

    @JsonProperty("activeUserMsisdn")
    public void setActiveUserMsisdn(Object activeUserMsisdn) {
        this.activeUserMsisdn = activeUserMsisdn;
    }

    @JsonProperty("activeUserPin")
    public Object getActiveUserPin() {
        return activeUserPin;
    }

    @JsonProperty("activeUserPin")
    public void setActiveUserPin(Object activeUserPin) {
        this.activeUserPin = activeUserPin;
    }

    @JsonProperty("activeUserLoginId")
    public Object getActiveUserLoginId() {
        return activeUserLoginId;
    }

    @JsonProperty("activeUserLoginId")
    public void setActiveUserLoginId(Object activeUserLoginId) {
        this.activeUserLoginId = activeUserLoginId;
    }

    @JsonProperty("staffUserDetails")
    public Object getStaffUserDetails() {
        return staffUserDetails;
    }

    @JsonProperty("staffUserDetails")
    public void setStaffUserDetails(Object staffUserDetails) {
        this.staffUserDetails = staffUserDetails;
    }

    @JsonProperty("longitude")
    public Object getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(Object longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("latitude")
    public Object getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("requestType")
    public Object getRequestType() {
        return requestType;
    }

    @JsonProperty("requestType")
    public void setRequestType(Object requestType) {
        this.requestType = requestType;
    }

    @JsonProperty("webLoginID")
    public Object getWebLoginID() {
        return webLoginID;
    }

    @JsonProperty("webLoginID")
    public void setWebLoginID(Object webLoginID) {
        this.webLoginID = webLoginID;
    }

    @JsonProperty("empCode")
    public Object getEmpCode() {
        return empCode;
    }

    @JsonProperty("empCode")
    public void setEmpCode(Object empCode) {
        this.empCode = empCode;
    }

    @JsonProperty("fromTime")
    public Object getFromTime() {
        return fromTime;
    }

    @JsonProperty("fromTime")
    public void setFromTime(Object fromTime) {
        this.fromTime = fromTime;
    }

    @JsonProperty("geographicalAreaList")
    public Object getGeographicalAreaList() {
        return geographicalAreaList;
    }

    @JsonProperty("geographicalAreaList")
    public void setGeographicalAreaList(Object geographicalAreaList) {
        this.geographicalAreaList = geographicalAreaList;
    }

    @JsonProperty("lastLoginOn")
    public Object getLastLoginOn() {
        return lastLoginOn;
    }

    @JsonProperty("lastLoginOn")
    public void setLastLoginOn(Object lastLoginOn) {
        this.lastLoginOn = lastLoginOn;
    }

    @JsonProperty("menuItemList")
    public Object getMenuItemList() {
        return menuItemList;
    }

    @JsonProperty("menuItemList")
    public void setMenuItemList(Object menuItemList) {
        this.menuItemList = menuItemList;
    }

    @JsonProperty("msisdnList")
    public Object getMsisdnList() {
        return msisdnList;
    }

    @JsonProperty("msisdnList")
    public void setMsisdnList(Object msisdnList) {
        this.msisdnList = msisdnList;
    }

    @JsonProperty("networkNamewithNetworkCode")
    public Object getNetworkNamewithNetworkCode() {
        return networkNamewithNetworkCode;
    }

    @JsonProperty("networkNamewithNetworkCode")
    public void setNetworkNamewithNetworkCode(Object networkNamewithNetworkCode) {
        this.networkNamewithNetworkCode = networkNamewithNetworkCode;
    }

    @JsonProperty("ownerID")
    public Object getOwnerID() {
        return ownerID;
    }

    @JsonProperty("ownerID")
    public void setOwnerID(Object ownerID) {
        this.ownerID = ownerID;
    }

    @JsonProperty("parentID")
    public Object getParentID() {
        return parentID;
    }

    @JsonProperty("parentID")
    public void setParentID(Object parentID) {
        this.parentID = parentID;
    }

    @JsonProperty("parentName")
    public Object getParentName() {
        return parentName;
    }

    @JsonProperty("parentName")
    public void setParentName(Object parentName) {
        this.parentName = parentName;
    }

    @JsonProperty("toTime")
    public Object getToTime() {
        return toTime;
    }

    @JsonProperty("toTime")
    public void setToTime(Object toTime) {
        this.toTime = toTime;
    }

    @JsonProperty("validRequestURLs")
    public Object getValidRequestURLs() {
        return validRequestURLs;
    }

    @JsonProperty("validRequestURLs")
    public void setValidRequestURLs(Object validRequestURLs) {
        this.validRequestURLs = validRequestURLs;
    }

    @JsonProperty("passwordModifiedOn")
    public Object getPasswordModifiedOn() {
        return passwordModifiedOn;
    }

    @JsonProperty("passwordModifiedOn")
    public void setPasswordModifiedOn(Object passwordModifiedOn) {
        this.passwordModifiedOn = passwordModifiedOn;
    }

    @JsonProperty("networkStatus")
    public Object getNetworkStatus() {
        return networkStatus;
    }

    @JsonProperty("networkStatus")
    public void setNetworkStatus(Object networkStatus) {
        this.networkStatus = networkStatus;
    }

    @JsonProperty("validStatus")
    public Integer getValidStatus() {
        return validStatus;
    }

    @JsonProperty("validStatus")
    public void setValidStatus(Integer validStatus) {
        this.validStatus = validStatus;
    }

    @JsonProperty("allowedIps")
    public Object getAllowedIps() {
        return allowedIps;
    }

    @JsonProperty("allowedIps")
    public void setAllowedIps(Object allowedIps) {
        this.allowedIps = allowedIps;
    }

    @JsonProperty("modifiedBy")
    public Object getModifiedBy() {
        return modifiedBy;
    }

    @JsonProperty("modifiedBy")
    public void setModifiedBy(Object modifiedBy) {
        this.modifiedBy = modifiedBy;
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

    @JsonProperty("previousStatus")
    public Object getPreviousStatus() {
        return previousStatus;
    }

    @JsonProperty("previousStatus")
    public void setPreviousStatus(Object previousStatus) {
        this.previousStatus = previousStatus;
    }

    @JsonProperty("userNamewithUserId")
    public Object getUserNamewithUserId() {
        return userNamewithUserId;
    }

    @JsonProperty("userNamewithUserId")
    public void setUserNamewithUserId(Object userNamewithUserId) {
        this.userNamewithUserId = userNamewithUserId;
    }

    @JsonProperty("userNamewithLoginId")
    public Object getUserNamewithLoginId() {
        return userNamewithLoginId;
    }

    @JsonProperty("userNamewithLoginId")
    public void setUserNamewithLoginId(Object userNamewithLoginId) {
        this.userNamewithLoginId = userNamewithLoginId;
    }

    @JsonProperty("contactNo")
    public Object getContactNo() {
        return contactNo;
    }

    @JsonProperty("contactNo")
    public void setContactNo(Object contactNo) {
        this.contactNo = contactNo;
    }

    @JsonProperty("departmentCode")
    public Object getDepartmentCode() {
        return departmentCode;
    }

    @JsonProperty("departmentCode")
    public void setDepartmentCode(Object departmentCode) {
        this.departmentCode = departmentCode;
    }

    @JsonProperty("departmentDesc")
    public Object getDepartmentDesc() {
        return departmentDesc;
    }

    @JsonProperty("departmentDesc")
    public void setDepartmentDesc(Object departmentDesc) {
        this.departmentDesc = departmentDesc;
    }

    @JsonProperty("designation")
    public Object getDesignation() {
        return designation;
    }

    @JsonProperty("designation")
    public void setDesignation(Object designation) {
        this.designation = designation;
    }

    @JsonProperty("divisionCode")
    public Object getDivisionCode() {
        return divisionCode;
    }

    @JsonProperty("divisionCode")
    public void setDivisionCode(Object divisionCode) {
        this.divisionCode = divisionCode;
    }

    @JsonProperty("divisionDesc")
    public Object getDivisionDesc() {
        return divisionDesc;
    }

    @JsonProperty("divisionDesc")
    public void setDivisionDesc(Object divisionDesc) {
        this.divisionDesc = divisionDesc;
    }

    @JsonProperty("userPhoneVO")
    public UserPhoneVO getUserPhoneVO() {
        return userPhoneVO;
    }

    @JsonProperty("userPhoneVO")
    public void setUserPhoneVO(UserPhoneVO userPhoneVO) {
        this.userPhoneVO = userPhoneVO;
    }

    @JsonProperty("associatedServiceTypeList")
    public Object getAssociatedServiceTypeList() {
        return associatedServiceTypeList;
    }

    @JsonProperty("associatedServiceTypeList")
    public void setAssociatedServiceTypeList(Object associatedServiceTypeList) {
        this.associatedServiceTypeList = associatedServiceTypeList;
    }

    @JsonProperty("fullAddress")
    public String getFullAddress() {
        return fullAddress;
    }

    @JsonProperty("fullAddress")
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @JsonProperty("fxedInfoStr")
    public Object getFxedInfoStr() {
        return fxedInfoStr;
    }

    @JsonProperty("fxedInfoStr")
    public void setFxedInfoStr(Object fxedInfoStr) {
        this.fxedInfoStr = fxedInfoStr;
    }

    @JsonProperty("usingNewSTK")
    public Boolean getUsingNewSTK() {
        return usingNewSTK;
    }

    @JsonProperty("usingNewSTK")
    public void setUsingNewSTK(Boolean usingNewSTK) {
        this.usingNewSTK = usingNewSTK;
    }

    @JsonProperty("updateSimRequired")
    public Boolean getUpdateSimRequired() {
        return updateSimRequired;
    }

    @JsonProperty("updateSimRequired")
    public void setUpdateSimRequired(Boolean updateSimRequired) {
        this.updateSimRequired = updateSimRequired;
    }

    @JsonProperty("createdOnAsString")
    public Object getCreatedOnAsString() {
        return createdOnAsString;
    }

    @JsonProperty("createdOnAsString")
    public void setCreatedOnAsString(Object createdOnAsString) {
        this.createdOnAsString = createdOnAsString;
    }

    @JsonProperty("state")
    public Object getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(Object state) {
        this.state = state;
    }

    @JsonProperty("lastModified")
    public Integer getLastModified() {
        return lastModified;
    }

    @JsonProperty("lastModified")
    public void setLastModified(Integer lastModified) {
        this.lastModified = lastModified;
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
