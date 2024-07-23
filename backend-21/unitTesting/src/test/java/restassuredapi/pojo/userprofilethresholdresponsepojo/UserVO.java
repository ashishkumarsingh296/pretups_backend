
package restassuredapi.pojo.userprofilethresholdresponsepojo;

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
    "showPassword",
    "confirmPassword",
    "allowedDays",
    "paymentTypes",
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
    "pinRequired",
    "networkCode",
    "serviceTypes",
    "imei",
    "otp",
    "productCode",
    "commissionProfileSuspendMsg",
    "transferRuleID",
    "userChargeGrouptypeCounters",
    "commissionProfileSetID",
    "transferProfileName",
    "userControlGrouptypeCounters",
    "monthlyTransAmt",
    "commissionProfileLang1Msg",
    "invalidPinCount",
    "commissionProfileLang2Msg",
    "geographicalCodeforNewuser",
    "transferProfileID",
    "commissionProfileStatus",
    "commissionProfileSetVersion",
    "commissionProfileSetName",
    "transferCategory",
    "transferProfileStatus",
    "geographicalDesc",
    "securityAnswer",
    "firstExternalUserModify",
    "autoc2cquantity",
    "lowBalAlertAllow",
    "prevBalanceStr",
    "optInOutStatus",
    "trannferRuleTypeId",
    "autoc2callowed",
    "multipleMsisdnlist",
    "catOutletAllowed",
    "parentGeographyCode",
    "asscMsisdnDate",
    "previousBalance",
    "asscMsisdnList",
    "prevParentName",
    "prevCategoryCode",
    "prevUserNameWithCategory",
    "primaryMsisdnPin",
    "trnsfrdUserHierhyList",
    "resetPinOTPMessage",
    "mcommerceServiceAllow",
    "otpInvalidCount",
    "catLowBalanceAlertAllow",
    "gateway",
    "returnFlag",
    "groupRoleFlag",
    "groupRoleCode",
    "languageCode",
    "smsPin",
    "recordNumber",
    "languageName",
    "voucherTypes",
    "userGrade",
    "parentLoginID",
    "outSuspened",
    "userGradeName",
    "inSuspend",
    "userIDPrefix",
    "categoryName",
    "parentStatus",
    "userlevel",
    "applicationID",
    "activatedOn",
    "prevUserName",
    "outletCode",
    "accessType",
    "primaryMsisdn",
    "prevUserId",
    "subOutletCode",
    "userBalance",
    "msisdnPrefix",
    "balanceStr",
    "smsMSisdn",
    "prefixId",
    "mpayProfileID",
    "phoneProfile",
    "balance",
    "userProfileID",
    "lmsProfile",
    "controlGroup",
    "maxTxnAmount",
    "otpModifiedOn",
    "alertEmail",
    "alertType",
    "parentLocale",
    "cellID",
    "decryptionKey",
    "alertMsisdn",
    "switchID",
    "lmsProfileId",
    "othCommSetId",
    "channelUserID",
    "commissionProfileApplicableFrom",
    "prevUserParentNameWithCategory",
    "language",
    "message",
    "state",
    "lastModified",
    "country",
    "userNamePrefix",
    "c2sMisFromDate",
    "p2pMisFromDate",
    "associationModifiedOn",
    "oldLastLoginOn",
    "designation",
    "divisionCode",
    "invalidPasswordCount",
    "reportHeaderName",
    "associationCreatedOn",
    "currentRoleCode",
    "departmentList",
    "associatedGeographicalList",
    "fromTime",
    "createdBy",
    "lastLoginOn",
    "menuItemList",
    "ownerID",
    "msisdnList",
    "parentID",
    "parentName",
    "toTime",
    "networkStatus",
    "validStatus",
    "allowedIps",
    "modifiedBy",
    "modifiedOn",
    "categoryVO",
    "sessionInfoVO",
    "empCode",
    "email",
    "password",
    "status",
    "divisionDesc",
    "passwordCountUpdatedOn",
    "passwordModifyFlag",
    "usingNewSTK",
    "userPhoneVO",
    "domainName",
    "statusDesc",
    "parentMsisdn",
    "serviceList",
    "ownerMsisdn",
    "voucherList",
    "fullAddress",
    "domainList",
    "ownerName",
    "fxedInfoStr",
    "currentModule",
    "passwordReset",
    "pinReset",
    "activeUserPin",
    "remoteAddress",
    "loggerMessage",
    "domainStatus",
    "longitude",
    "latitude",
    "browserType",
    "documentType",
    "company",
    "firstName",
    "documentNo",
    "fax",
    "otpvalidated",
    "rsavalidated",
    "rsaFlag",
    "lastName",
    "rsaAllowed",
    "paymentType",
    "ownerCompany",
    "authType",
    "userName",
    "contactNo",
    "networkID",
    "loginID",
    "userID",
    "categoryCode",
    "domainID",
    "loginTime",
    "networkName",
    "userType",
    "assoMsisdn",
    "assType",
    "webLoginID",
    "requestType",
    "rsaRequired",
    "countryCode",
    "divisionList",
    "address1",
    "staffUser",
    "contactPerson",
    "city",
    "activeUserID",
    "batchName",
    "address2",
    "externalCode",
    "ssn",
    "remarks",
    "batchID",
    "p2pMisToDate",
    "c2sMisToDate",
    "creationType",
    "domainCodes",
    "msisdn",
    "userCode",
    "level1ApprovedOn",
    "geographicalCodeStatus",
    "level2ApprovedOn",
    "passwordModifiedOn",
    "departmentCode",
    "level2ApprovedBy",
    "associatedServiceTypeList",
    "updateSimRequired",
    "ownerCategoryName",
    "createdByUserName",
    "createdOnAsString",
    "parentCategoryName",
    "validRequestURLs",
    "userBalanceList",
    "previousStatus",
    "associatedProductTypeList",
    "geographicalAreaList",
    "userNamewithUserId",
    "userNamewithLoginId",
    "networkNamewithNetworkCode",
    "departmentDesc",
    "level1ApprovedBy",
    "domainTypeCode",
    "moduleCodeString",
    "suspendedByUserName",
    "pageCodeString",
    "staffUserDetails",
    "authTypeAllowed",
    "appointmentDate",
    "activeUserMsisdn",
    "requetedByUserName",
    "activeUserLoginId",
    "requetedOnAsString",
    "userNameWithCategory",
    "suspendedOnAsString",
    "restrictedMsisdnAllow",
    "rolesMapSelectedCount"
})
public class UserVO {

    @JsonProperty("showPassword")
    private Object showPassword;
    @JsonProperty("confirmPassword")
    private Object confirmPassword;
    @JsonProperty("allowedDays")
    private String allowedDays;
    @JsonProperty("paymentTypes")
    private Object paymentTypes;
    @JsonProperty("createdOn")
    private Long createdOn;
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
    private String geographicalCode;
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
    private Long otfCount;
    @JsonProperty("allowedUserTypeCreation")
    private Object allowedUserTypeCreation;
    @JsonProperty("agentBalanceList")
    private Object agentBalanceList;
    @JsonProperty("shortName")
    private String shortName;
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
    private Long maxUserLevel;
    @JsonProperty("serviceTypeList")
    private Object serviceTypeList;
    @JsonProperty("tokenLastUsedDate")
    private Object tokenLastUsedDate;
    @JsonProperty("sosAllowed")
    private Object sosAllowed;
    @JsonProperty("sosAllowedAmount")
    private Long sosAllowedAmount;
    @JsonProperty("sosThresholdLimit")
    private Long sosThresholdLimit;
    @JsonProperty("lastSosTransactionId")
    private Object lastSosTransactionId;
    @JsonProperty("lastSosStatus")
    private Object lastSosStatus;
    @JsonProperty("lastSosProductCode")
    private Object lastSosProductCode;
    @JsonProperty("lrAllowed")
    private Object lrAllowed;
    @JsonProperty("lrMaxAmount")
    private Long lrMaxAmount;
    @JsonProperty("lrTransferAmount")
    private Long lrTransferAmount;
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
    @JsonProperty("pinRequired")
    private String pinRequired;
    @JsonProperty("networkCode")
    private Object networkCode;
    @JsonProperty("serviceTypes")
    private Object serviceTypes;
    @JsonProperty("imei")
    private Object imei;
    @JsonProperty("otp")
    private Object otp;
    @JsonProperty("productCode")
    private Object productCode;
    @JsonProperty("commissionProfileSuspendMsg")
    private Object commissionProfileSuspendMsg;
    @JsonProperty("transferRuleID")
    private Object transferRuleID;
    @JsonProperty("userChargeGrouptypeCounters")
    private Object userChargeGrouptypeCounters;
    @JsonProperty("commissionProfileSetID")
    private Object commissionProfileSetID;
    @JsonProperty("transferProfileName")
    private Object transferProfileName;
    @JsonProperty("userControlGrouptypeCounters")
    private Object userControlGrouptypeCounters;
    @JsonProperty("monthlyTransAmt")
    private Long monthlyTransAmt;
    @JsonProperty("commissionProfileLang1Msg")
    private Object commissionProfileLang1Msg;
    @JsonProperty("invalidPinCount")
    private Long invalidPinCount;
    @JsonProperty("commissionProfileLang2Msg")
    private Object commissionProfileLang2Msg;
    @JsonProperty("geographicalCodeforNewuser")
    private Object geographicalCodeforNewuser;
    @JsonProperty("transferProfileID")
    private Object transferProfileID;
    @JsonProperty("commissionProfileStatus")
    private Object commissionProfileStatus;
    @JsonProperty("commissionProfileSetVersion")
    private Object commissionProfileSetVersion;
    @JsonProperty("commissionProfileSetName")
    private Object commissionProfileSetName;
    @JsonProperty("transferCategory")
    private Object transferCategory;
    @JsonProperty("transferProfileStatus")
    private Object transferProfileStatus;
    @JsonProperty("geographicalDesc")
    private String geographicalDesc;
    @JsonProperty("securityAnswer")
    private Object securityAnswer;
    @JsonProperty("firstExternalUserModify")
    private Boolean firstExternalUserModify;
    @JsonProperty("autoc2cquantity")
    private Object autoc2cquantity;
    @JsonProperty("lowBalAlertAllow")
    private Object lowBalAlertAllow;
    @JsonProperty("prevBalanceStr")
    private Object prevBalanceStr;
    @JsonProperty("optInOutStatus")
    private Object optInOutStatus;
    @JsonProperty("trannferRuleTypeId")
    private Object trannferRuleTypeId;
    @JsonProperty("autoc2callowed")
    private Object autoc2callowed;
    @JsonProperty("multipleMsisdnlist")
    private Object multipleMsisdnlist;
    @JsonProperty("catOutletAllowed")
    private Object catOutletAllowed;
    @JsonProperty("parentGeographyCode")
    private Object parentGeographyCode;
    @JsonProperty("asscMsisdnDate")
    private Object asscMsisdnDate;
    @JsonProperty("previousBalance")
    private Long previousBalance;
    @JsonProperty("asscMsisdnList")
    private Object asscMsisdnList;
    @JsonProperty("prevParentName")
    private Object prevParentName;
    @JsonProperty("prevCategoryCode")
    private Object prevCategoryCode;
    @JsonProperty("prevUserNameWithCategory")
    private String prevUserNameWithCategory;
    @JsonProperty("primaryMsisdnPin")
    private Object primaryMsisdnPin;
    @JsonProperty("trnsfrdUserHierhyList")
    private Object trnsfrdUserHierhyList;
    @JsonProperty("resetPinOTPMessage")
    private Object resetPinOTPMessage;
    @JsonProperty("mcommerceServiceAllow")
    private Object mcommerceServiceAllow;
    @JsonProperty("otpInvalidCount")
    private Long otpInvalidCount;
    @JsonProperty("catLowBalanceAlertAllow")
    private Object catLowBalanceAlertAllow;
    @JsonProperty("gateway")
    private Object gateway;
    @JsonProperty("returnFlag")
    private Boolean returnFlag;
    @JsonProperty("groupRoleFlag")
    private Object groupRoleFlag;
    @JsonProperty("groupRoleCode")
    private Object groupRoleCode;
    @JsonProperty("languageCode")
    private Object languageCode;
    @JsonProperty("smsPin")
    private String smsPin;
    @JsonProperty("recordNumber")
    private Object recordNumber;
    @JsonProperty("languageName")
    private Object languageName;
    @JsonProperty("voucherTypes")
    private Object voucherTypes;
    @JsonProperty("userGrade")
    private Object userGrade;
    @JsonProperty("parentLoginID")
    private Object parentLoginID;
    @JsonProperty("outSuspened")
    private Object outSuspened;
    @JsonProperty("userGradeName")
    private Object userGradeName;
    @JsonProperty("inSuspend")
    private Object inSuspend;
    @JsonProperty("userIDPrefix")
    private Object userIDPrefix;
    @JsonProperty("categoryName")
    private String categoryName;
    @JsonProperty("parentStatus")
    private Object parentStatus;
    @JsonProperty("userlevel")
    private Object userlevel;
    @JsonProperty("applicationID")
    private Object applicationID;
    @JsonProperty("activatedOn")
    private Object activatedOn;
    @JsonProperty("prevUserName")
    private Object prevUserName;
    @JsonProperty("outletCode")
    private Object outletCode;
    @JsonProperty("accessType")
    private String accessType;
    @JsonProperty("primaryMsisdn")
    private Object primaryMsisdn;
    @JsonProperty("prevUserId")
    private Object prevUserId;
    @JsonProperty("subOutletCode")
    private Object subOutletCode;
    @JsonProperty("userBalance")
    private Object userBalance;
    @JsonProperty("msisdnPrefix")
    private Object msisdnPrefix;
    @JsonProperty("balanceStr")
    private Object balanceStr;
    @JsonProperty("smsMSisdn")
    private Object smsMSisdn;
    @JsonProperty("prefixId")
    private Long prefixId;
    @JsonProperty("mpayProfileID")
    private Object mpayProfileID;
    @JsonProperty("phoneProfile")
    private Object phoneProfile;
    @JsonProperty("balance")
    private Long balance;
    @JsonProperty("userProfileID")
    private Object userProfileID;
    @JsonProperty("lmsProfile")
    private Object lmsProfile;
    @JsonProperty("controlGroup")
    private Object controlGroup;
    @JsonProperty("maxTxnAmount")
    private Long maxTxnAmount;
    @JsonProperty("otpModifiedOn")
    private Object otpModifiedOn;
    @JsonProperty("alertEmail")
    private Object alertEmail;
    @JsonProperty("alertType")
    private Object alertType;
    @JsonProperty("parentLocale")
    private Object parentLocale;
    @JsonProperty("cellID")
    private Object cellID;
    @JsonProperty("decryptionKey")
    private Object decryptionKey;
    @JsonProperty("alertMsisdn")
    private Object alertMsisdn;
    @JsonProperty("switchID")
    private Object switchID;
    @JsonProperty("lmsProfileId")
    private Object lmsProfileId;
    @JsonProperty("othCommSetId")
    private Object othCommSetId;
    @JsonProperty("channelUserID")
    private Object channelUserID;
    @JsonProperty("commissionProfileApplicableFrom")
    private Object commissionProfileApplicableFrom;
    @JsonProperty("prevUserParentNameWithCategory")
    private String prevUserParentNameWithCategory;
    @JsonProperty("language")
    private String language;
    @JsonProperty("message")
    private Object message;
    @JsonProperty("state")
    private Object state;
    @JsonProperty("lastModified")
    private Long lastModified;
    @JsonProperty("country")
    private Object country;
    @JsonProperty("userNamePrefix")
    private String userNamePrefix;
    @JsonProperty("c2sMisFromDate")
    private Object c2sMisFromDate;
    @JsonProperty("p2pMisFromDate")
    private Object p2pMisFromDate;
    @JsonProperty("associationModifiedOn")
    private Object associationModifiedOn;
    @JsonProperty("oldLastLoginOn")
    private Object oldLastLoginOn;
    @JsonProperty("designation")
    private Object designation;
    @JsonProperty("divisionCode")
    private Object divisionCode;
    @JsonProperty("invalidPasswordCount")
    private Long invalidPasswordCount;
    @JsonProperty("reportHeaderName")
    private Object reportHeaderName;
    @JsonProperty("associationCreatedOn")
    private Object associationCreatedOn;
    @JsonProperty("currentRoleCode")
    private Object currentRoleCode;
    @JsonProperty("departmentList")
    private Object departmentList;
    @JsonProperty("associatedGeographicalList")
    private Object associatedGeographicalList;
    @JsonProperty("fromTime")
    private String fromTime;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("lastLoginOn")
    private Object lastLoginOn;
    @JsonProperty("menuItemList")
    private Object menuItemList;
    @JsonProperty("ownerID")
    private String ownerID;
    @JsonProperty("msisdnList")
    private Object msisdnList;
    @JsonProperty("parentID")
    private String parentID;
    @JsonProperty("parentName")
    private String parentName;
    @JsonProperty("toTime")
    private String toTime;
    @JsonProperty("networkStatus")
    private Object networkStatus;
    @JsonProperty("validStatus")
    private Long validStatus;
    @JsonProperty("allowedIps")
    private Object allowedIps;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("modifiedOn")
    private Long modifiedOn;
    @JsonProperty("categoryVO")
    private CategoryVO categoryVO;
    @JsonProperty("sessionInfoVO")
    private Object sessionInfoVO;
    @JsonProperty("empCode")
    private Object empCode;
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("status")
    private String status;
    @JsonProperty("divisionDesc")
    private Object divisionDesc;
    @JsonProperty("passwordCountUpdatedOn")
    private Object passwordCountUpdatedOn;
    @JsonProperty("passwordModifyFlag")
    private Boolean passwordModifyFlag;
    @JsonProperty("usingNewSTK")
    private Boolean usingNewSTK;
    @JsonProperty("userPhoneVO")
    private Object userPhoneVO;
    @JsonProperty("domainName")
    private String domainName;
    @JsonProperty("statusDesc")
    private String statusDesc;
    @JsonProperty("parentMsisdn")
    private String parentMsisdn;
    @JsonProperty("serviceList")
    private Object serviceList;
    @JsonProperty("ownerMsisdn")
    private String ownerMsisdn;
    @JsonProperty("voucherList")
    private Object voucherList;
    @JsonProperty("fullAddress")
    private String fullAddress;
    @JsonProperty("domainList")
    private Object domainList;
    @JsonProperty("ownerName")
    private String ownerName;
    @JsonProperty("fxedInfoStr")
    private Object fxedInfoStr;
    @JsonProperty("currentModule")
    private Object currentModule;
    @JsonProperty("passwordReset")
    private Object passwordReset;
    @JsonProperty("pinReset")
    private Object pinReset;
    @JsonProperty("activeUserPin")
    private Object activeUserPin;
    @JsonProperty("remoteAddress")
    private Object remoteAddress;
    @JsonProperty("loggerMessage")
    private Object loggerMessage;
    @JsonProperty("domainStatus")
    private Object domainStatus;
    @JsonProperty("longitude")
    private Object longitude;
    @JsonProperty("latitude")
    private Object latitude;
    @JsonProperty("browserType")
    private Object browserType;
    @JsonProperty("documentType")
    private Object documentType;
    @JsonProperty("company")
    private Object company;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("documentNo")
    private Object documentNo;
    @JsonProperty("fax")
    private Object fax;
    @JsonProperty("otpvalidated")
    private Boolean otpvalidated;
    @JsonProperty("rsavalidated")
    private Boolean rsavalidated;
    @JsonProperty("rsaFlag")
    private Object rsaFlag;
    @JsonProperty("lastName")
    private Object lastName;
    @JsonProperty("rsaAllowed")
    private Boolean rsaAllowed;
    @JsonProperty("paymentType")
    private Object paymentType;
    @JsonProperty("ownerCompany")
    private Object ownerCompany;
    @JsonProperty("authType")
    private Object authType;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("contactNo")
    private Object contactNo;
    @JsonProperty("networkID")
    private String networkID;
    @JsonProperty("loginID")
    private String loginID;
    @JsonProperty("userID")
    private String userID;
    @JsonProperty("categoryCode")
    private String categoryCode;
    @JsonProperty("domainID")
    private Object domainID;
    @JsonProperty("loginTime")
    private Object loginTime;
    @JsonProperty("networkName")
    private Object networkName;
    @JsonProperty("userType")
    private String userType;
    @JsonProperty("assoMsisdn")
    private Object assoMsisdn;
    @JsonProperty("assType")
    private Object assType;
    @JsonProperty("webLoginID")
    private Object webLoginID;
    @JsonProperty("requestType")
    private Object requestType;
    @JsonProperty("rsaRequired")
    private Boolean rsaRequired;
    @JsonProperty("countryCode")
    private Object countryCode;
    @JsonProperty("divisionList")
    private Object divisionList;
    @JsonProperty("address1")
    private Object address1;
    @JsonProperty("staffUser")
    private Boolean staffUser;
    @JsonProperty("contactPerson")
    private Object contactPerson;
    @JsonProperty("city")
    private Object city;
    @JsonProperty("activeUserID")
    private Object activeUserID;
    @JsonProperty("batchName")
    private Object batchName;
    @JsonProperty("address2")
    private Object address2;
    @JsonProperty("externalCode")
    private String externalCode;
    @JsonProperty("ssn")
    private Object ssn;
    @JsonProperty("remarks")
    private Object remarks;
    @JsonProperty("batchID")
    private Object batchID;
    @JsonProperty("p2pMisToDate")
    private Object p2pMisToDate;
    @JsonProperty("c2sMisToDate")
    private Object c2sMisToDate;
    @JsonProperty("creationType")
    private String creationType;
    @JsonProperty("domainCodes")
    private Object domainCodes;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("userCode")
    private String userCode;
    @JsonProperty("level1ApprovedOn")
    private Long level1ApprovedOn;
    @JsonProperty("geographicalCodeStatus")
    private Object geographicalCodeStatus;
    @JsonProperty("level2ApprovedOn")
    private Long level2ApprovedOn;
    @JsonProperty("passwordModifiedOn")
    private Long passwordModifiedOn;
    @JsonProperty("departmentCode")
    private Object departmentCode;
    @JsonProperty("level2ApprovedBy")
    private String level2ApprovedBy;
    @JsonProperty("associatedServiceTypeList")
    private Object associatedServiceTypeList;
    @JsonProperty("updateSimRequired")
    private Boolean updateSimRequired;
    @JsonProperty("ownerCategoryName")
    private String ownerCategoryName;
    @JsonProperty("createdByUserName")
    private String createdByUserName;
    @JsonProperty("createdOnAsString")
    private String createdOnAsString;
    @JsonProperty("parentCategoryName")
    private String parentCategoryName;
    @JsonProperty("validRequestURLs")
    private Object validRequestURLs;
    @JsonProperty("userBalanceList")
    private Object userBalanceList;
    @JsonProperty("previousStatus")
    private String previousStatus;
    @JsonProperty("associatedProductTypeList")
    private Object associatedProductTypeList;
    @JsonProperty("geographicalAreaList")
    private Object geographicalAreaList;
    @JsonProperty("userNamewithUserId")
    private String userNamewithUserId;
    @JsonProperty("userNamewithLoginId")
    private String userNamewithLoginId;
    @JsonProperty("networkNamewithNetworkCode")
    private String networkNamewithNetworkCode;
    @JsonProperty("departmentDesc")
    private Object departmentDesc;
    @JsonProperty("level1ApprovedBy")
    private String level1ApprovedBy;
    @JsonProperty("domainTypeCode")
    private Object domainTypeCode;
    @JsonProperty("moduleCodeString")
    private Object moduleCodeString;
    @JsonProperty("suspendedByUserName")
    private String suspendedByUserName;
    @JsonProperty("pageCodeString")
    private Object pageCodeString;
    @JsonProperty("staffUserDetails")
    private Object staffUserDetails;
    @JsonProperty("authTypeAllowed")
    private String authTypeAllowed;
    @JsonProperty("appointmentDate")
    private Object appointmentDate;
    @JsonProperty("activeUserMsisdn")
    private Object activeUserMsisdn;
    @JsonProperty("requetedByUserName")
    private String requetedByUserName;
    @JsonProperty("activeUserLoginId")
    private Object activeUserLoginId;
    @JsonProperty("requetedOnAsString")
    private String requetedOnAsString;
    @JsonProperty("userNameWithCategory")
    private String userNameWithCategory;
    @JsonProperty("suspendedOnAsString")
    private String suspendedOnAsString;
    @JsonProperty("restrictedMsisdnAllow")
    private Object restrictedMsisdnAllow;
    @JsonProperty("rolesMapSelectedCount")
    private Long rolesMapSelectedCount;
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
    public String getAllowedDays() {
        return allowedDays;
    }

    @JsonProperty("allowedDays")
    public void setAllowedDays(String allowedDays) {
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

    @JsonProperty("createdOn")
    public Long getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(Long createdOn) {
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
    public String getGeographicalCode() {
        return geographicalCode;
    }

    @JsonProperty("geographicalCode")
    public void setGeographicalCode(String geographicalCode) {
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
    public Long getOtfCount() {
        return otfCount;
    }

    @JsonProperty("otfCount")
    public void setOtfCount(Long otfCount) {
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
    public String getShortName() {
        return shortName;
    }

    @JsonProperty("shortName")
    public void setShortName(String shortName) {
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
    public Long getMaxUserLevel() {
        return maxUserLevel;
    }

    @JsonProperty("maxUserLevel")
    public void setMaxUserLevel(Long maxUserLevel) {
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
    public Long getSosAllowedAmount() {
        return sosAllowedAmount;
    }

    @JsonProperty("sosAllowedAmount")
    public void setSosAllowedAmount(Long sosAllowedAmount) {
        this.sosAllowedAmount = sosAllowedAmount;
    }

    @JsonProperty("sosThresholdLimit")
    public Long getSosThresholdLimit() {
        return sosThresholdLimit;
    }

    @JsonProperty("sosThresholdLimit")
    public void setSosThresholdLimit(Long sosThresholdLimit) {
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
    public Long getLrMaxAmount() {
        return lrMaxAmount;
    }

    @JsonProperty("lrMaxAmount")
    public void setLrMaxAmount(Long lrMaxAmount) {
        this.lrMaxAmount = lrMaxAmount;
    }

    @JsonProperty("lrTransferAmount")
    public Long getLrTransferAmount() {
        return lrTransferAmount;
    }

    @JsonProperty("lrTransferAmount")
    public void setLrTransferAmount(Long lrTransferAmount) {
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

    @JsonProperty("pinRequired")
    public String getPinRequired() {
        return pinRequired;
    }

    @JsonProperty("pinRequired")
    public void setPinRequired(String pinRequired) {
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

    @JsonProperty("commissionProfileSuspendMsg")
    public Object getCommissionProfileSuspendMsg() {
        return commissionProfileSuspendMsg;
    }

    @JsonProperty("commissionProfileSuspendMsg")
    public void setCommissionProfileSuspendMsg(Object commissionProfileSuspendMsg) {
        this.commissionProfileSuspendMsg = commissionProfileSuspendMsg;
    }

    @JsonProperty("transferRuleID")
    public Object getTransferRuleID() {
        return transferRuleID;
    }

    @JsonProperty("transferRuleID")
    public void setTransferRuleID(Object transferRuleID) {
        this.transferRuleID = transferRuleID;
    }

    @JsonProperty("userChargeGrouptypeCounters")
    public Object getUserChargeGrouptypeCounters() {
        return userChargeGrouptypeCounters;
    }

    @JsonProperty("userChargeGrouptypeCounters")
    public void setUserChargeGrouptypeCounters(Object userChargeGrouptypeCounters) {
        this.userChargeGrouptypeCounters = userChargeGrouptypeCounters;
    }

    @JsonProperty("commissionProfileSetID")
    public Object getCommissionProfileSetID() {
        return commissionProfileSetID;
    }

    @JsonProperty("commissionProfileSetID")
    public void setCommissionProfileSetID(Object commissionProfileSetID) {
        this.commissionProfileSetID = commissionProfileSetID;
    }

    @JsonProperty("transferProfileName")
    public Object getTransferProfileName() {
        return transferProfileName;
    }

    @JsonProperty("transferProfileName")
    public void setTransferProfileName(Object transferProfileName) {
        this.transferProfileName = transferProfileName;
    }

    @JsonProperty("userControlGrouptypeCounters")
    public Object getUserControlGrouptypeCounters() {
        return userControlGrouptypeCounters;
    }

    @JsonProperty("userControlGrouptypeCounters")
    public void setUserControlGrouptypeCounters(Object userControlGrouptypeCounters) {
        this.userControlGrouptypeCounters = userControlGrouptypeCounters;
    }

    @JsonProperty("monthlyTransAmt")
    public Long getMonthlyTransAmt() {
        return monthlyTransAmt;
    }

    @JsonProperty("monthlyTransAmt")
    public void setMonthlyTransAmt(Long monthlyTransAmt) {
        this.monthlyTransAmt = monthlyTransAmt;
    }

    @JsonProperty("commissionProfileLang1Msg")
    public Object getCommissionProfileLang1Msg() {
        return commissionProfileLang1Msg;
    }

    @JsonProperty("commissionProfileLang1Msg")
    public void setCommissionProfileLang1Msg(Object commissionProfileLang1Msg) {
        this.commissionProfileLang1Msg = commissionProfileLang1Msg;
    }

    @JsonProperty("invalidPinCount")
    public Long getInvalidPinCount() {
        return invalidPinCount;
    }

    @JsonProperty("invalidPinCount")
    public void setInvalidPinCount(Long invalidPinCount) {
        this.invalidPinCount = invalidPinCount;
    }

    @JsonProperty("commissionProfileLang2Msg")
    public Object getCommissionProfileLang2Msg() {
        return commissionProfileLang2Msg;
    }

    @JsonProperty("commissionProfileLang2Msg")
    public void setCommissionProfileLang2Msg(Object commissionProfileLang2Msg) {
        this.commissionProfileLang2Msg = commissionProfileLang2Msg;
    }

    @JsonProperty("geographicalCodeforNewuser")
    public Object getGeographicalCodeforNewuser() {
        return geographicalCodeforNewuser;
    }

    @JsonProperty("geographicalCodeforNewuser")
    public void setGeographicalCodeforNewuser(Object geographicalCodeforNewuser) {
        this.geographicalCodeforNewuser = geographicalCodeforNewuser;
    }

    @JsonProperty("transferProfileID")
    public Object getTransferProfileID() {
        return transferProfileID;
    }

    @JsonProperty("transferProfileID")
    public void setTransferProfileID(Object transferProfileID) {
        this.transferProfileID = transferProfileID;
    }

    @JsonProperty("commissionProfileStatus")
    public Object getCommissionProfileStatus() {
        return commissionProfileStatus;
    }

    @JsonProperty("commissionProfileStatus")
    public void setCommissionProfileStatus(Object commissionProfileStatus) {
        this.commissionProfileStatus = commissionProfileStatus;
    }

    @JsonProperty("commissionProfileSetVersion")
    public Object getCommissionProfileSetVersion() {
        return commissionProfileSetVersion;
    }

    @JsonProperty("commissionProfileSetVersion")
    public void setCommissionProfileSetVersion(Object commissionProfileSetVersion) {
        this.commissionProfileSetVersion = commissionProfileSetVersion;
    }

    @JsonProperty("commissionProfileSetName")
    public Object getCommissionProfileSetName() {
        return commissionProfileSetName;
    }

    @JsonProperty("commissionProfileSetName")
    public void setCommissionProfileSetName(Object commissionProfileSetName) {
        this.commissionProfileSetName = commissionProfileSetName;
    }

    @JsonProperty("transferCategory")
    public Object getTransferCategory() {
        return transferCategory;
    }

    @JsonProperty("transferCategory")
    public void setTransferCategory(Object transferCategory) {
        this.transferCategory = transferCategory;
    }

    @JsonProperty("transferProfileStatus")
    public Object getTransferProfileStatus() {
        return transferProfileStatus;
    }

    @JsonProperty("transferProfileStatus")
    public void setTransferProfileStatus(Object transferProfileStatus) {
        this.transferProfileStatus = transferProfileStatus;
    }

    @JsonProperty("geographicalDesc")
    public String getGeographicalDesc() {
        return geographicalDesc;
    }

    @JsonProperty("geographicalDesc")
    public void setGeographicalDesc(String geographicalDesc) {
        this.geographicalDesc = geographicalDesc;
    }

    @JsonProperty("securityAnswer")
    public Object getSecurityAnswer() {
        return securityAnswer;
    }

    @JsonProperty("securityAnswer")
    public void setSecurityAnswer(Object securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    @JsonProperty("firstExternalUserModify")
    public Boolean getFirstExternalUserModify() {
        return firstExternalUserModify;
    }

    @JsonProperty("firstExternalUserModify")
    public void setFirstExternalUserModify(Boolean firstExternalUserModify) {
        this.firstExternalUserModify = firstExternalUserModify;
    }

    @JsonProperty("autoc2cquantity")
    public Object getAutoc2cquantity() {
        return autoc2cquantity;
    }

    @JsonProperty("autoc2cquantity")
    public void setAutoc2cquantity(Object autoc2cquantity) {
        this.autoc2cquantity = autoc2cquantity;
    }

    @JsonProperty("lowBalAlertAllow")
    public Object getLowBalAlertAllow() {
        return lowBalAlertAllow;
    }

    @JsonProperty("lowBalAlertAllow")
    public void setLowBalAlertAllow(Object lowBalAlertAllow) {
        this.lowBalAlertAllow = lowBalAlertAllow;
    }

    @JsonProperty("prevBalanceStr")
    public Object getPrevBalanceStr() {
        return prevBalanceStr;
    }

    @JsonProperty("prevBalanceStr")
    public void setPrevBalanceStr(Object prevBalanceStr) {
        this.prevBalanceStr = prevBalanceStr;
    }

    @JsonProperty("optInOutStatus")
    public Object getOptInOutStatus() {
        return optInOutStatus;
    }

    @JsonProperty("optInOutStatus")
    public void setOptInOutStatus(Object optInOutStatus) {
        this.optInOutStatus = optInOutStatus;
    }

    @JsonProperty("trannferRuleTypeId")
    public Object getTrannferRuleTypeId() {
        return trannferRuleTypeId;
    }

    @JsonProperty("trannferRuleTypeId")
    public void setTrannferRuleTypeId(Object trannferRuleTypeId) {
        this.trannferRuleTypeId = trannferRuleTypeId;
    }

    @JsonProperty("autoc2callowed")
    public Object getAutoc2callowed() {
        return autoc2callowed;
    }

    @JsonProperty("autoc2callowed")
    public void setAutoc2callowed(Object autoc2callowed) {
        this.autoc2callowed = autoc2callowed;
    }

    @JsonProperty("multipleMsisdnlist")
    public Object getMultipleMsisdnlist() {
        return multipleMsisdnlist;
    }

    @JsonProperty("multipleMsisdnlist")
    public void setMultipleMsisdnlist(Object multipleMsisdnlist) {
        this.multipleMsisdnlist = multipleMsisdnlist;
    }

    @JsonProperty("catOutletAllowed")
    public Object getCatOutletAllowed() {
        return catOutletAllowed;
    }

    @JsonProperty("catOutletAllowed")
    public void setCatOutletAllowed(Object catOutletAllowed) {
        this.catOutletAllowed = catOutletAllowed;
    }

    @JsonProperty("parentGeographyCode")
    public Object getParentGeographyCode() {
        return parentGeographyCode;
    }

    @JsonProperty("parentGeographyCode")
    public void setParentGeographyCode(Object parentGeographyCode) {
        this.parentGeographyCode = parentGeographyCode;
    }

    @JsonProperty("asscMsisdnDate")
    public Object getAsscMsisdnDate() {
        return asscMsisdnDate;
    }

    @JsonProperty("asscMsisdnDate")
    public void setAsscMsisdnDate(Object asscMsisdnDate) {
        this.asscMsisdnDate = asscMsisdnDate;
    }

    @JsonProperty("previousBalance")
    public Long getPreviousBalance() {
        return previousBalance;
    }

    @JsonProperty("previousBalance")
    public void setPreviousBalance(Long previousBalance) {
        this.previousBalance = previousBalance;
    }

    @JsonProperty("asscMsisdnList")
    public Object getAsscMsisdnList() {
        return asscMsisdnList;
    }

    @JsonProperty("asscMsisdnList")
    public void setAsscMsisdnList(Object asscMsisdnList) {
        this.asscMsisdnList = asscMsisdnList;
    }

    @JsonProperty("prevParentName")
    public Object getPrevParentName() {
        return prevParentName;
    }

    @JsonProperty("prevParentName")
    public void setPrevParentName(Object prevParentName) {
        this.prevParentName = prevParentName;
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

    @JsonProperty("primaryMsisdnPin")
    public Object getPrimaryMsisdnPin() {
        return primaryMsisdnPin;
    }

    @JsonProperty("primaryMsisdnPin")
    public void setPrimaryMsisdnPin(Object primaryMsisdnPin) {
        this.primaryMsisdnPin = primaryMsisdnPin;
    }

    @JsonProperty("trnsfrdUserHierhyList")
    public Object getTrnsfrdUserHierhyList() {
        return trnsfrdUserHierhyList;
    }

    @JsonProperty("trnsfrdUserHierhyList")
    public void setTrnsfrdUserHierhyList(Object trnsfrdUserHierhyList) {
        this.trnsfrdUserHierhyList = trnsfrdUserHierhyList;
    }

    @JsonProperty("resetPinOTPMessage")
    public Object getResetPinOTPMessage() {
        return resetPinOTPMessage;
    }

    @JsonProperty("resetPinOTPMessage")
    public void setResetPinOTPMessage(Object resetPinOTPMessage) {
        this.resetPinOTPMessage = resetPinOTPMessage;
    }

    @JsonProperty("mcommerceServiceAllow")
    public Object getMcommerceServiceAllow() {
        return mcommerceServiceAllow;
    }

    @JsonProperty("mcommerceServiceAllow")
    public void setMcommerceServiceAllow(Object mcommerceServiceAllow) {
        this.mcommerceServiceAllow = mcommerceServiceAllow;
    }

    @JsonProperty("otpInvalidCount")
    public Long getOtpInvalidCount() {
        return otpInvalidCount;
    }

    @JsonProperty("otpInvalidCount")
    public void setOtpInvalidCount(Long otpInvalidCount) {
        this.otpInvalidCount = otpInvalidCount;
    }

    @JsonProperty("catLowBalanceAlertAllow")
    public Object getCatLowBalanceAlertAllow() {
        return catLowBalanceAlertAllow;
    }

    @JsonProperty("catLowBalanceAlertAllow")
    public void setCatLowBalanceAlertAllow(Object catLowBalanceAlertAllow) {
        this.catLowBalanceAlertAllow = catLowBalanceAlertAllow;
    }

    @JsonProperty("gateway")
    public Object getGateway() {
        return gateway;
    }

    @JsonProperty("gateway")
    public void setGateway(Object gateway) {
        this.gateway = gateway;
    }

    @JsonProperty("returnFlag")
    public Boolean getReturnFlag() {
        return returnFlag;
    }

    @JsonProperty("returnFlag")
    public void setReturnFlag(Boolean returnFlag) {
        this.returnFlag = returnFlag;
    }

    @JsonProperty("groupRoleFlag")
    public Object getGroupRoleFlag() {
        return groupRoleFlag;
    }

    @JsonProperty("groupRoleFlag")
    public void setGroupRoleFlag(Object groupRoleFlag) {
        this.groupRoleFlag = groupRoleFlag;
    }

    @JsonProperty("groupRoleCode")
    public Object getGroupRoleCode() {
        return groupRoleCode;
    }

    @JsonProperty("groupRoleCode")
    public void setGroupRoleCode(Object groupRoleCode) {
        this.groupRoleCode = groupRoleCode;
    }

    @JsonProperty("languageCode")
    public Object getLanguageCode() {
        return languageCode;
    }

    @JsonProperty("languageCode")
    public void setLanguageCode(Object languageCode) {
        this.languageCode = languageCode;
    }

    @JsonProperty("smsPin")
    public String getSmsPin() {
        return smsPin;
    }

    @JsonProperty("smsPin")
    public void setSmsPin(String smsPin) {
        this.smsPin = smsPin;
    }

    @JsonProperty("recordNumber")
    public Object getRecordNumber() {
        return recordNumber;
    }

    @JsonProperty("recordNumber")
    public void setRecordNumber(Object recordNumber) {
        this.recordNumber = recordNumber;
    }

    @JsonProperty("languageName")
    public Object getLanguageName() {
        return languageName;
    }

    @JsonProperty("languageName")
    public void setLanguageName(Object languageName) {
        this.languageName = languageName;
    }

    @JsonProperty("voucherTypes")
    public Object getVoucherTypes() {
        return voucherTypes;
    }

    @JsonProperty("voucherTypes")
    public void setVoucherTypes(Object voucherTypes) {
        this.voucherTypes = voucherTypes;
    }

    @JsonProperty("userGrade")
    public Object getUserGrade() {
        return userGrade;
    }

    @JsonProperty("userGrade")
    public void setUserGrade(Object userGrade) {
        this.userGrade = userGrade;
    }

    @JsonProperty("parentLoginID")
    public Object getParentLoginID() {
        return parentLoginID;
    }

    @JsonProperty("parentLoginID")
    public void setParentLoginID(Object parentLoginID) {
        this.parentLoginID = parentLoginID;
    }

    @JsonProperty("outSuspened")
    public Object getOutSuspened() {
        return outSuspened;
    }

    @JsonProperty("outSuspened")
    public void setOutSuspened(Object outSuspened) {
        this.outSuspened = outSuspened;
    }

    @JsonProperty("userGradeName")
    public Object getUserGradeName() {
        return userGradeName;
    }

    @JsonProperty("userGradeName")
    public void setUserGradeName(Object userGradeName) {
        this.userGradeName = userGradeName;
    }

    @JsonProperty("inSuspend")
    public Object getInSuspend() {
        return inSuspend;
    }

    @JsonProperty("inSuspend")
    public void setInSuspend(Object inSuspend) {
        this.inSuspend = inSuspend;
    }

    @JsonProperty("userIDPrefix")
    public Object getUserIDPrefix() {
        return userIDPrefix;
    }

    @JsonProperty("userIDPrefix")
    public void setUserIDPrefix(Object userIDPrefix) {
        this.userIDPrefix = userIDPrefix;
    }

    @JsonProperty("categoryName")
    public String getCategoryName() {
        return categoryName;
    }

    @JsonProperty("categoryName")
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("parentStatus")
    public Object getParentStatus() {
        return parentStatus;
    }

    @JsonProperty("parentStatus")
    public void setParentStatus(Object parentStatus) {
        this.parentStatus = parentStatus;
    }

    @JsonProperty("userlevel")
    public Object getUserlevel() {
        return userlevel;
    }

    @JsonProperty("userlevel")
    public void setUserlevel(Object userlevel) {
        this.userlevel = userlevel;
    }

    @JsonProperty("applicationID")
    public Object getApplicationID() {
        return applicationID;
    }

    @JsonProperty("applicationID")
    public void setApplicationID(Object applicationID) {
        this.applicationID = applicationID;
    }

    @JsonProperty("activatedOn")
    public Object getActivatedOn() {
        return activatedOn;
    }

    @JsonProperty("activatedOn")
    public void setActivatedOn(Object activatedOn) {
        this.activatedOn = activatedOn;
    }

    @JsonProperty("prevUserName")
    public Object getPrevUserName() {
        return prevUserName;
    }

    @JsonProperty("prevUserName")
    public void setPrevUserName(Object prevUserName) {
        this.prevUserName = prevUserName;
    }

    @JsonProperty("outletCode")
    public Object getOutletCode() {
        return outletCode;
    }

    @JsonProperty("outletCode")
    public void setOutletCode(Object outletCode) {
        this.outletCode = outletCode;
    }

    @JsonProperty("accessType")
    public String getAccessType() {
        return accessType;
    }

    @JsonProperty("accessType")
    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    @JsonProperty("primaryMsisdn")
    public Object getPrimaryMsisdn() {
        return primaryMsisdn;
    }

    @JsonProperty("primaryMsisdn")
    public void setPrimaryMsisdn(Object primaryMsisdn) {
        this.primaryMsisdn = primaryMsisdn;
    }

    @JsonProperty("prevUserId")
    public Object getPrevUserId() {
        return prevUserId;
    }

    @JsonProperty("prevUserId")
    public void setPrevUserId(Object prevUserId) {
        this.prevUserId = prevUserId;
    }

    @JsonProperty("subOutletCode")
    public Object getSubOutletCode() {
        return subOutletCode;
    }

    @JsonProperty("subOutletCode")
    public void setSubOutletCode(Object subOutletCode) {
        this.subOutletCode = subOutletCode;
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

    @JsonProperty("balanceStr")
    public Object getBalanceStr() {
        return balanceStr;
    }

    @JsonProperty("balanceStr")
    public void setBalanceStr(Object balanceStr) {
        this.balanceStr = balanceStr;
    }

    @JsonProperty("smsMSisdn")
    public Object getSmsMSisdn() {
        return smsMSisdn;
    }

    @JsonProperty("smsMSisdn")
    public void setSmsMSisdn(Object smsMSisdn) {
        this.smsMSisdn = smsMSisdn;
    }

    @JsonProperty("prefixId")
    public Long getPrefixId() {
        return prefixId;
    }

    @JsonProperty("prefixId")
    public void setPrefixId(Long prefixId) {
        this.prefixId = prefixId;
    }

    @JsonProperty("mpayProfileID")
    public Object getMpayProfileID() {
        return mpayProfileID;
    }

    @JsonProperty("mpayProfileID")
    public void setMpayProfileID(Object mpayProfileID) {
        this.mpayProfileID = mpayProfileID;
    }

    @JsonProperty("phoneProfile")
    public Object getPhoneProfile() {
        return phoneProfile;
    }

    @JsonProperty("phoneProfile")
    public void setPhoneProfile(Object phoneProfile) {
        this.phoneProfile = phoneProfile;
    }

    @JsonProperty("balance")
    public Long getBalance() {
        return balance;
    }

    @JsonProperty("balance")
    public void setBalance(Long balance) {
        this.balance = balance;
    }

    @JsonProperty("userProfileID")
    public Object getUserProfileID() {
        return userProfileID;
    }

    @JsonProperty("userProfileID")
    public void setUserProfileID(Object userProfileID) {
        this.userProfileID = userProfileID;
    }

    @JsonProperty("lmsProfile")
    public Object getLmsProfile() {
        return lmsProfile;
    }

    @JsonProperty("lmsProfile")
    public void setLmsProfile(Object lmsProfile) {
        this.lmsProfile = lmsProfile;
    }

    @JsonProperty("controlGroup")
    public Object getControlGroup() {
        return controlGroup;
    }

    @JsonProperty("controlGroup")
    public void setControlGroup(Object controlGroup) {
        this.controlGroup = controlGroup;
    }

    @JsonProperty("maxTxnAmount")
    public Long getMaxTxnAmount() {
        return maxTxnAmount;
    }

    @JsonProperty("maxTxnAmount")
    public void setMaxTxnAmount(Long maxTxnAmount) {
        this.maxTxnAmount = maxTxnAmount;
    }

    @JsonProperty("otpModifiedOn")
    public Object getOtpModifiedOn() {
        return otpModifiedOn;
    }

    @JsonProperty("otpModifiedOn")
    public void setOtpModifiedOn(Object otpModifiedOn) {
        this.otpModifiedOn = otpModifiedOn;
    }

    @JsonProperty("alertEmail")
    public Object getAlertEmail() {
        return alertEmail;
    }

    @JsonProperty("alertEmail")
    public void setAlertEmail(Object alertEmail) {
        this.alertEmail = alertEmail;
    }

    @JsonProperty("alertType")
    public Object getAlertType() {
        return alertType;
    }

    @JsonProperty("alertType")
    public void setAlertType(Object alertType) {
        this.alertType = alertType;
    }

    @JsonProperty("parentLocale")
    public Object getParentLocale() {
        return parentLocale;
    }

    @JsonProperty("parentLocale")
    public void setParentLocale(Object parentLocale) {
        this.parentLocale = parentLocale;
    }

    @JsonProperty("cellID")
    public Object getCellID() {
        return cellID;
    }

    @JsonProperty("cellID")
    public void setCellID(Object cellID) {
        this.cellID = cellID;
    }

    @JsonProperty("decryptionKey")
    public Object getDecryptionKey() {
        return decryptionKey;
    }

    @JsonProperty("decryptionKey")
    public void setDecryptionKey(Object decryptionKey) {
        this.decryptionKey = decryptionKey;
    }

    @JsonProperty("alertMsisdn")
    public Object getAlertMsisdn() {
        return alertMsisdn;
    }

    @JsonProperty("alertMsisdn")
    public void setAlertMsisdn(Object alertMsisdn) {
        this.alertMsisdn = alertMsisdn;
    }

    @JsonProperty("switchID")
    public Object getSwitchID() {
        return switchID;
    }

    @JsonProperty("switchID")
    public void setSwitchID(Object switchID) {
        this.switchID = switchID;
    }

    @JsonProperty("lmsProfileId")
    public Object getLmsProfileId() {
        return lmsProfileId;
    }

    @JsonProperty("lmsProfileId")
    public void setLmsProfileId(Object lmsProfileId) {
        this.lmsProfileId = lmsProfileId;
    }

    @JsonProperty("othCommSetId")
    public Object getOthCommSetId() {
        return othCommSetId;
    }

    @JsonProperty("othCommSetId")
    public void setOthCommSetId(Object othCommSetId) {
        this.othCommSetId = othCommSetId;
    }

    @JsonProperty("channelUserID")
    public Object getChannelUserID() {
        return channelUserID;
    }

    @JsonProperty("channelUserID")
    public void setChannelUserID(Object channelUserID) {
        this.channelUserID = channelUserID;
    }

    @JsonProperty("commissionProfileApplicableFrom")
    public Object getCommissionProfileApplicableFrom() {
        return commissionProfileApplicableFrom;
    }

    @JsonProperty("commissionProfileApplicableFrom")
    public void setCommissionProfileApplicableFrom(Object commissionProfileApplicableFrom) {
        this.commissionProfileApplicableFrom = commissionProfileApplicableFrom;
    }

    @JsonProperty("prevUserParentNameWithCategory")
    public String getPrevUserParentNameWithCategory() {
        return prevUserParentNameWithCategory;
    }

    @JsonProperty("prevUserParentNameWithCategory")
    public void setPrevUserParentNameWithCategory(String prevUserParentNameWithCategory) {
        this.prevUserParentNameWithCategory = prevUserParentNameWithCategory;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("message")
    public Object getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(Object message) {
        this.message = message;
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
    public Long getLastModified() {
        return lastModified;
    }

    @JsonProperty("lastModified")
    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @JsonProperty("country")
    public Object getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(Object country) {
        this.country = country;
    }

    @JsonProperty("userNamePrefix")
    public String getUserNamePrefix() {
        return userNamePrefix;
    }

    @JsonProperty("userNamePrefix")
    public void setUserNamePrefix(String userNamePrefix) {
        this.userNamePrefix = userNamePrefix;
    }

    @JsonProperty("c2sMisFromDate")
    public Object getC2sMisFromDate() {
        return c2sMisFromDate;
    }

    @JsonProperty("c2sMisFromDate")
    public void setC2sMisFromDate(Object c2sMisFromDate) {
        this.c2sMisFromDate = c2sMisFromDate;
    }

    @JsonProperty("p2pMisFromDate")
    public Object getP2pMisFromDate() {
        return p2pMisFromDate;
    }

    @JsonProperty("p2pMisFromDate")
    public void setP2pMisFromDate(Object p2pMisFromDate) {
        this.p2pMisFromDate = p2pMisFromDate;
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

    @JsonProperty("invalidPasswordCount")
    public Long getInvalidPasswordCount() {
        return invalidPasswordCount;
    }

    @JsonProperty("invalidPasswordCount")
    public void setInvalidPasswordCount(Long invalidPasswordCount) {
        this.invalidPasswordCount = invalidPasswordCount;
    }

    @JsonProperty("reportHeaderName")
    public Object getReportHeaderName() {
        return reportHeaderName;
    }

    @JsonProperty("reportHeaderName")
    public void setReportHeaderName(Object reportHeaderName) {
        this.reportHeaderName = reportHeaderName;
    }

    @JsonProperty("associationCreatedOn")
    public Object getAssociationCreatedOn() {
        return associationCreatedOn;
    }

    @JsonProperty("associationCreatedOn")
    public void setAssociationCreatedOn(Object associationCreatedOn) {
        this.associationCreatedOn = associationCreatedOn;
    }

    @JsonProperty("currentRoleCode")
    public Object getCurrentRoleCode() {
        return currentRoleCode;
    }

    @JsonProperty("currentRoleCode")
    public void setCurrentRoleCode(Object currentRoleCode) {
        this.currentRoleCode = currentRoleCode;
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

    @JsonProperty("fromTime")
    public String getFromTime() {
        return fromTime;
    }

    @JsonProperty("fromTime")
    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    @JsonProperty("ownerID")
    public String getOwnerID() {
        return ownerID;
    }

    @JsonProperty("ownerID")
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    @JsonProperty("msisdnList")
    public Object getMsisdnList() {
        return msisdnList;
    }

    @JsonProperty("msisdnList")
    public void setMsisdnList(Object msisdnList) {
        this.msisdnList = msisdnList;
    }

    @JsonProperty("parentID")
    public String getParentID() {
        return parentID;
    }

    @JsonProperty("parentID")
    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    @JsonProperty("parentName")
    public String getParentName() {
        return parentName;
    }

    @JsonProperty("parentName")
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @JsonProperty("toTime")
    public String getToTime() {
        return toTime;
    }

    @JsonProperty("toTime")
    public void setToTime(String toTime) {
        this.toTime = toTime;
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
    public Long getValidStatus() {
        return validStatus;
    }

    @JsonProperty("validStatus")
    public void setValidStatus(Long validStatus) {
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
    public String getModifiedBy() {
        return modifiedBy;
    }

    @JsonProperty("modifiedBy")
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("modifiedOn")
    public Long getModifiedOn() {
        return modifiedOn;
    }

    @JsonProperty("modifiedOn")
    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @JsonProperty("categoryVO")
    public CategoryVO getCategoryVO() {
        return categoryVO;
    }

    @JsonProperty("categoryVO")
    public void setCategoryVO(CategoryVO categoryVO) {
        this.categoryVO = categoryVO;
    }

    @JsonProperty("sessionInfoVO")
    public Object getSessionInfoVO() {
        return sessionInfoVO;
    }

    @JsonProperty("sessionInfoVO")
    public void setSessionInfoVO(Object sessionInfoVO) {
        this.sessionInfoVO = sessionInfoVO;
    }

    @JsonProperty("empCode")
    public Object getEmpCode() {
        return empCode;
    }

    @JsonProperty("empCode")
    public void setEmpCode(Object empCode) {
        this.empCode = empCode;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("divisionDesc")
    public Object getDivisionDesc() {
        return divisionDesc;
    }

    @JsonProperty("divisionDesc")
    public void setDivisionDesc(Object divisionDesc) {
        this.divisionDesc = divisionDesc;
    }

    @JsonProperty("passwordCountUpdatedOn")
    public Object getPasswordCountUpdatedOn() {
        return passwordCountUpdatedOn;
    }

    @JsonProperty("passwordCountUpdatedOn")
    public void setPasswordCountUpdatedOn(Object passwordCountUpdatedOn) {
        this.passwordCountUpdatedOn = passwordCountUpdatedOn;
    }

    @JsonProperty("passwordModifyFlag")
    public Boolean getPasswordModifyFlag() {
        return passwordModifyFlag;
    }

    @JsonProperty("passwordModifyFlag")
    public void setPasswordModifyFlag(Boolean passwordModifyFlag) {
        this.passwordModifyFlag = passwordModifyFlag;
    }

    @JsonProperty("usingNewSTK")
    public Boolean getUsingNewSTK() {
        return usingNewSTK;
    }

    @JsonProperty("usingNewSTK")
    public void setUsingNewSTK(Boolean usingNewSTK) {
        this.usingNewSTK = usingNewSTK;
    }

    @JsonProperty("userPhoneVO")
    public Object getUserPhoneVO() {
        return userPhoneVO;
    }

    @JsonProperty("userPhoneVO")
    public void setUserPhoneVO(Object userPhoneVO) {
        this.userPhoneVO = userPhoneVO;
    }

    @JsonProperty("domainName")
    public String getDomainName() {
        return domainName;
    }

    @JsonProperty("domainName")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @JsonProperty("statusDesc")
    public String getStatusDesc() {
        return statusDesc;
    }

    @JsonProperty("statusDesc")
    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    @JsonProperty("parentMsisdn")
    public String getParentMsisdn() {
        return parentMsisdn;
    }

    @JsonProperty("parentMsisdn")
    public void setParentMsisdn(String parentMsisdn) {
        this.parentMsisdn = parentMsisdn;
    }

    @JsonProperty("serviceList")
    public Object getServiceList() {
        return serviceList;
    }

    @JsonProperty("serviceList")
    public void setServiceList(Object serviceList) {
        this.serviceList = serviceList;
    }

    @JsonProperty("ownerMsisdn")
    public String getOwnerMsisdn() {
        return ownerMsisdn;
    }

    @JsonProperty("ownerMsisdn")
    public void setOwnerMsisdn(String ownerMsisdn) {
        this.ownerMsisdn = ownerMsisdn;
    }

    @JsonProperty("voucherList")
    public Object getVoucherList() {
        return voucherList;
    }

    @JsonProperty("voucherList")
    public void setVoucherList(Object voucherList) {
        this.voucherList = voucherList;
    }

    @JsonProperty("fullAddress")
    public String getFullAddress() {
        return fullAddress;
    }

    @JsonProperty("fullAddress")
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @JsonProperty("domainList")
    public Object getDomainList() {
        return domainList;
    }

    @JsonProperty("domainList")
    public void setDomainList(Object domainList) {
        this.domainList = domainList;
    }

    @JsonProperty("ownerName")
    public String getOwnerName() {
        return ownerName;
    }

    @JsonProperty("ownerName")
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @JsonProperty("fxedInfoStr")
    public Object getFxedInfoStr() {
        return fxedInfoStr;
    }

    @JsonProperty("fxedInfoStr")
    public void setFxedInfoStr(Object fxedInfoStr) {
        this.fxedInfoStr = fxedInfoStr;
    }

    @JsonProperty("currentModule")
    public Object getCurrentModule() {
        return currentModule;
    }

    @JsonProperty("currentModule")
    public void setCurrentModule(Object currentModule) {
        this.currentModule = currentModule;
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

    @JsonProperty("activeUserPin")
    public Object getActiveUserPin() {
        return activeUserPin;
    }

    @JsonProperty("activeUserPin")
    public void setActiveUserPin(Object activeUserPin) {
        this.activeUserPin = activeUserPin;
    }

    @JsonProperty("remoteAddress")
    public Object getRemoteAddress() {
        return remoteAddress;
    }

    @JsonProperty("remoteAddress")
    public void setRemoteAddress(Object remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @JsonProperty("loggerMessage")
    public Object getLoggerMessage() {
        return loggerMessage;
    }

    @JsonProperty("loggerMessage")
    public void setLoggerMessage(Object loggerMessage) {
        this.loggerMessage = loggerMessage;
    }

    @JsonProperty("domainStatus")
    public Object getDomainStatus() {
        return domainStatus;
    }

    @JsonProperty("domainStatus")
    public void setDomainStatus(Object domainStatus) {
        this.domainStatus = domainStatus;
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

    @JsonProperty("browserType")
    public Object getBrowserType() {
        return browserType;
    }

    @JsonProperty("browserType")
    public void setBrowserType(Object browserType) {
        this.browserType = browserType;
    }

    @JsonProperty("documentType")
    public Object getDocumentType() {
        return documentType;
    }

    @JsonProperty("documentType")
    public void setDocumentType(Object documentType) {
        this.documentType = documentType;
    }

    @JsonProperty("company")
    public Object getCompany() {
        return company;
    }

    @JsonProperty("company")
    public void setCompany(Object company) {
        this.company = company;
    }

    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("documentNo")
    public Object getDocumentNo() {
        return documentNo;
    }

    @JsonProperty("documentNo")
    public void setDocumentNo(Object documentNo) {
        this.documentNo = documentNo;
    }

    @JsonProperty("fax")
    public Object getFax() {
        return fax;
    }

    @JsonProperty("fax")
    public void setFax(Object fax) {
        this.fax = fax;
    }

    @JsonProperty("otpvalidated")
    public Boolean getOtpvalidated() {
        return otpvalidated;
    }

    @JsonProperty("otpvalidated")
    public void setOtpvalidated(Boolean otpvalidated) {
        this.otpvalidated = otpvalidated;
    }

    @JsonProperty("rsavalidated")
    public Boolean getRsavalidated() {
        return rsavalidated;
    }

    @JsonProperty("rsavalidated")
    public void setRsavalidated(Boolean rsavalidated) {
        this.rsavalidated = rsavalidated;
    }

    @JsonProperty("rsaFlag")
    public Object getRsaFlag() {
        return rsaFlag;
    }

    @JsonProperty("rsaFlag")
    public void setRsaFlag(Object rsaFlag) {
        this.rsaFlag = rsaFlag;
    }

    @JsonProperty("lastName")
    public Object getLastName() {
        return lastName;
    }

    @JsonProperty("lastName")
    public void setLastName(Object lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("rsaAllowed")
    public Boolean getRsaAllowed() {
        return rsaAllowed;
    }

    @JsonProperty("rsaAllowed")
    public void setRsaAllowed(Boolean rsaAllowed) {
        this.rsaAllowed = rsaAllowed;
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

    @JsonProperty("authType")
    public Object getAuthType() {
        return authType;
    }

    @JsonProperty("authType")
    public void setAuthType(Object authType) {
        this.authType = authType;
    }

    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("contactNo")
    public Object getContactNo() {
        return contactNo;
    }

    @JsonProperty("contactNo")
    public void setContactNo(Object contactNo) {
        this.contactNo = contactNo;
    }

    @JsonProperty("networkID")
    public String getNetworkID() {
        return networkID;
    }

    @JsonProperty("networkID")
    public void setNetworkID(String networkID) {
        this.networkID = networkID;
    }

    @JsonProperty("loginID")
    public String getLoginID() {
        return loginID;
    }

    @JsonProperty("loginID")
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    @JsonProperty("userID")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @JsonProperty("categoryCode")
    public String getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
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

    @JsonProperty("networkName")
    public Object getNetworkName() {
        return networkName;
    }

    @JsonProperty("networkName")
    public void setNetworkName(Object networkName) {
        this.networkName = networkName;
    }

    @JsonProperty("userType")
    public String getUserType() {
        return userType;
    }

    @JsonProperty("userType")
    public void setUserType(String userType) {
        this.userType = userType;
    }

    @JsonProperty("assoMsisdn")
    public Object getAssoMsisdn() {
        return assoMsisdn;
    }

    @JsonProperty("assoMsisdn")
    public void setAssoMsisdn(Object assoMsisdn) {
        this.assoMsisdn = assoMsisdn;
    }

    @JsonProperty("assType")
    public Object getAssType() {
        return assType;
    }

    @JsonProperty("assType")
    public void setAssType(Object assType) {
        this.assType = assType;
    }

    @JsonProperty("webLoginID")
    public Object getWebLoginID() {
        return webLoginID;
    }

    @JsonProperty("webLoginID")
    public void setWebLoginID(Object webLoginID) {
        this.webLoginID = webLoginID;
    }

    @JsonProperty("requestType")
    public Object getRequestType() {
        return requestType;
    }

    @JsonProperty("requestType")
    public void setRequestType(Object requestType) {
        this.requestType = requestType;
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

    @JsonProperty("divisionList")
    public Object getDivisionList() {
        return divisionList;
    }

    @JsonProperty("divisionList")
    public void setDivisionList(Object divisionList) {
        this.divisionList = divisionList;
    }

    @JsonProperty("address1")
    public Object getAddress1() {
        return address1;
    }

    @JsonProperty("address1")
    public void setAddress1(Object address1) {
        this.address1 = address1;
    }

    @JsonProperty("staffUser")
    public Boolean getStaffUser() {
        return staffUser;
    }

    @JsonProperty("staffUser")
    public void setStaffUser(Boolean staffUser) {
        this.staffUser = staffUser;
    }

    @JsonProperty("contactPerson")
    public Object getContactPerson() {
        return contactPerson;
    }

    @JsonProperty("contactPerson")
    public void setContactPerson(Object contactPerson) {
        this.contactPerson = contactPerson;
    }

    @JsonProperty("city")
    public Object getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(Object city) {
        this.city = city;
    }

    @JsonProperty("activeUserID")
    public Object getActiveUserID() {
        return activeUserID;
    }

    @JsonProperty("activeUserID")
    public void setActiveUserID(Object activeUserID) {
        this.activeUserID = activeUserID;
    }

    @JsonProperty("batchName")
    public Object getBatchName() {
        return batchName;
    }

    @JsonProperty("batchName")
    public void setBatchName(Object batchName) {
        this.batchName = batchName;
    }

    @JsonProperty("address2")
    public Object getAddress2() {
        return address2;
    }

    @JsonProperty("address2")
    public void setAddress2(Object address2) {
        this.address2 = address2;
    }

    @JsonProperty("externalCode")
    public String getExternalCode() {
        return externalCode;
    }

    @JsonProperty("externalCode")
    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    @JsonProperty("ssn")
    public Object getSsn() {
        return ssn;
    }

    @JsonProperty("ssn")
    public void setSsn(Object ssn) {
        this.ssn = ssn;
    }

    @JsonProperty("remarks")
    public Object getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(Object remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("batchID")
    public Object getBatchID() {
        return batchID;
    }

    @JsonProperty("batchID")
    public void setBatchID(Object batchID) {
        this.batchID = batchID;
    }

    @JsonProperty("p2pMisToDate")
    public Object getP2pMisToDate() {
        return p2pMisToDate;
    }

    @JsonProperty("p2pMisToDate")
    public void setP2pMisToDate(Object p2pMisToDate) {
        this.p2pMisToDate = p2pMisToDate;
    }

    @JsonProperty("c2sMisToDate")
    public Object getC2sMisToDate() {
        return c2sMisToDate;
    }

    @JsonProperty("c2sMisToDate")
    public void setC2sMisToDate(Object c2sMisToDate) {
        this.c2sMisToDate = c2sMisToDate;
    }

    @JsonProperty("creationType")
    public String getCreationType() {
        return creationType;
    }

    @JsonProperty("creationType")
    public void setCreationType(String creationType) {
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

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("userCode")
    public String getUserCode() {
        return userCode;
    }

    @JsonProperty("userCode")
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @JsonProperty("level1ApprovedOn")
    public Long getLevel1ApprovedOn() {
        return level1ApprovedOn;
    }

    @JsonProperty("level1ApprovedOn")
    public void setLevel1ApprovedOn(Long level1ApprovedOn) {
        this.level1ApprovedOn = level1ApprovedOn;
    }

    @JsonProperty("geographicalCodeStatus")
    public Object getGeographicalCodeStatus() {
        return geographicalCodeStatus;
    }

    @JsonProperty("geographicalCodeStatus")
    public void setGeographicalCodeStatus(Object geographicalCodeStatus) {
        this.geographicalCodeStatus = geographicalCodeStatus;
    }

    @JsonProperty("level2ApprovedOn")
    public Long getLevel2ApprovedOn() {
        return level2ApprovedOn;
    }

    @JsonProperty("level2ApprovedOn")
    public void setLevel2ApprovedOn(Long level2ApprovedOn) {
        this.level2ApprovedOn = level2ApprovedOn;
    }

    @JsonProperty("passwordModifiedOn")
    public Long getPasswordModifiedOn() {
        return passwordModifiedOn;
    }

    @JsonProperty("passwordModifiedOn")
    public void setPasswordModifiedOn(Long passwordModifiedOn) {
        this.passwordModifiedOn = passwordModifiedOn;
    }

    @JsonProperty("departmentCode")
    public Object getDepartmentCode() {
        return departmentCode;
    }

    @JsonProperty("departmentCode")
    public void setDepartmentCode(Object departmentCode) {
        this.departmentCode = departmentCode;
    }

    @JsonProperty("level2ApprovedBy")
    public String getLevel2ApprovedBy() {
        return level2ApprovedBy;
    }

    @JsonProperty("level2ApprovedBy")
    public void setLevel2ApprovedBy(String level2ApprovedBy) {
        this.level2ApprovedBy = level2ApprovedBy;
    }

    @JsonProperty("associatedServiceTypeList")
    public Object getAssociatedServiceTypeList() {
        return associatedServiceTypeList;
    }

    @JsonProperty("associatedServiceTypeList")
    public void setAssociatedServiceTypeList(Object associatedServiceTypeList) {
        this.associatedServiceTypeList = associatedServiceTypeList;
    }

    @JsonProperty("updateSimRequired")
    public Boolean getUpdateSimRequired() {
        return updateSimRequired;
    }

    @JsonProperty("updateSimRequired")
    public void setUpdateSimRequired(Boolean updateSimRequired) {
        this.updateSimRequired = updateSimRequired;
    }

    @JsonProperty("ownerCategoryName")
    public String getOwnerCategoryName() {
        return ownerCategoryName;
    }

    @JsonProperty("ownerCategoryName")
    public void setOwnerCategoryName(String ownerCategoryName) {
        this.ownerCategoryName = ownerCategoryName;
    }

    @JsonProperty("createdByUserName")
    public String getCreatedByUserName() {
        return createdByUserName;
    }

    @JsonProperty("createdByUserName")
    public void setCreatedByUserName(String createdByUserName) {
        this.createdByUserName = createdByUserName;
    }

    @JsonProperty("createdOnAsString")
    public String getCreatedOnAsString() {
        return createdOnAsString;
    }

    @JsonProperty("createdOnAsString")
    public void setCreatedOnAsString(String createdOnAsString) {
        this.createdOnAsString = createdOnAsString;
    }

    @JsonProperty("parentCategoryName")
    public String getParentCategoryName() {
        return parentCategoryName;
    }

    @JsonProperty("parentCategoryName")
    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    @JsonProperty("validRequestURLs")
    public Object getValidRequestURLs() {
        return validRequestURLs;
    }

    @JsonProperty("validRequestURLs")
    public void setValidRequestURLs(Object validRequestURLs) {
        this.validRequestURLs = validRequestURLs;
    }

    @JsonProperty("userBalanceList")
    public Object getUserBalanceList() {
        return userBalanceList;
    }

    @JsonProperty("userBalanceList")
    public void setUserBalanceList(Object userBalanceList) {
        this.userBalanceList = userBalanceList;
    }

    @JsonProperty("previousStatus")
    public String getPreviousStatus() {
        return previousStatus;
    }

    @JsonProperty("previousStatus")
    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    @JsonProperty("associatedProductTypeList")
    public Object getAssociatedProductTypeList() {
        return associatedProductTypeList;
    }

    @JsonProperty("associatedProductTypeList")
    public void setAssociatedProductTypeList(Object associatedProductTypeList) {
        this.associatedProductTypeList = associatedProductTypeList;
    }

    @JsonProperty("geographicalAreaList")
    public Object getGeographicalAreaList() {
        return geographicalAreaList;
    }

    @JsonProperty("geographicalAreaList")
    public void setGeographicalAreaList(Object geographicalAreaList) {
        this.geographicalAreaList = geographicalAreaList;
    }

    @JsonProperty("userNamewithUserId")
    public String getUserNamewithUserId() {
        return userNamewithUserId;
    }

    @JsonProperty("userNamewithUserId")
    public void setUserNamewithUserId(String userNamewithUserId) {
        this.userNamewithUserId = userNamewithUserId;
    }

    @JsonProperty("userNamewithLoginId")
    public String getUserNamewithLoginId() {
        return userNamewithLoginId;
    }

    @JsonProperty("userNamewithLoginId")
    public void setUserNamewithLoginId(String userNamewithLoginId) {
        this.userNamewithLoginId = userNamewithLoginId;
    }

    @JsonProperty("networkNamewithNetworkCode")
    public String getNetworkNamewithNetworkCode() {
        return networkNamewithNetworkCode;
    }

    @JsonProperty("networkNamewithNetworkCode")
    public void setNetworkNamewithNetworkCode(String networkNamewithNetworkCode) {
        this.networkNamewithNetworkCode = networkNamewithNetworkCode;
    }

    @JsonProperty("departmentDesc")
    public Object getDepartmentDesc() {
        return departmentDesc;
    }

    @JsonProperty("departmentDesc")
    public void setDepartmentDesc(Object departmentDesc) {
        this.departmentDesc = departmentDesc;
    }

    @JsonProperty("level1ApprovedBy")
    public String getLevel1ApprovedBy() {
        return level1ApprovedBy;
    }

    @JsonProperty("level1ApprovedBy")
    public void setLevel1ApprovedBy(String level1ApprovedBy) {
        this.level1ApprovedBy = level1ApprovedBy;
    }

    @JsonProperty("domainTypeCode")
    public Object getDomainTypeCode() {
        return domainTypeCode;
    }

    @JsonProperty("domainTypeCode")
    public void setDomainTypeCode(Object domainTypeCode) {
        this.domainTypeCode = domainTypeCode;
    }

    @JsonProperty("moduleCodeString")
    public Object getModuleCodeString() {
        return moduleCodeString;
    }

    @JsonProperty("moduleCodeString")
    public void setModuleCodeString(Object moduleCodeString) {
        this.moduleCodeString = moduleCodeString;
    }

    @JsonProperty("suspendedByUserName")
    public String getSuspendedByUserName() {
        return suspendedByUserName;
    }

    @JsonProperty("suspendedByUserName")
    public void setSuspendedByUserName(String suspendedByUserName) {
        this.suspendedByUserName = suspendedByUserName;
    }

    @JsonProperty("pageCodeString")
    public Object getPageCodeString() {
        return pageCodeString;
    }

    @JsonProperty("pageCodeString")
    public void setPageCodeString(Object pageCodeString) {
        this.pageCodeString = pageCodeString;
    }

    @JsonProperty("staffUserDetails")
    public Object getStaffUserDetails() {
        return staffUserDetails;
    }

    @JsonProperty("staffUserDetails")
    public void setStaffUserDetails(Object staffUserDetails) {
        this.staffUserDetails = staffUserDetails;
    }

    @JsonProperty("authTypeAllowed")
    public String getAuthTypeAllowed() {
        return authTypeAllowed;
    }

    @JsonProperty("authTypeAllowed")
    public void setAuthTypeAllowed(String authTypeAllowed) {
        this.authTypeAllowed = authTypeAllowed;
    }

    @JsonProperty("appointmentDate")
    public Object getAppointmentDate() {
        return appointmentDate;
    }

    @JsonProperty("appointmentDate")
    public void setAppointmentDate(Object appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    @JsonProperty("activeUserMsisdn")
    public Object getActiveUserMsisdn() {
        return activeUserMsisdn;
    }

    @JsonProperty("activeUserMsisdn")
    public void setActiveUserMsisdn(Object activeUserMsisdn) {
        this.activeUserMsisdn = activeUserMsisdn;
    }

    @JsonProperty("requetedByUserName")
    public String getRequetedByUserName() {
        return requetedByUserName;
    }

    @JsonProperty("requetedByUserName")
    public void setRequetedByUserName(String requetedByUserName) {
        this.requetedByUserName = requetedByUserName;
    }

    @JsonProperty("activeUserLoginId")
    public Object getActiveUserLoginId() {
        return activeUserLoginId;
    }

    @JsonProperty("activeUserLoginId")
    public void setActiveUserLoginId(Object activeUserLoginId) {
        this.activeUserLoginId = activeUserLoginId;
    }

    @JsonProperty("requetedOnAsString")
    public String getRequetedOnAsString() {
        return requetedOnAsString;
    }

    @JsonProperty("requetedOnAsString")
    public void setRequetedOnAsString(String requetedOnAsString) {
        this.requetedOnAsString = requetedOnAsString;
    }

    @JsonProperty("userNameWithCategory")
    public String getUserNameWithCategory() {
        return userNameWithCategory;
    }

    @JsonProperty("userNameWithCategory")
    public void setUserNameWithCategory(String userNameWithCategory) {
        this.userNameWithCategory = userNameWithCategory;
    }

    @JsonProperty("suspendedOnAsString")
    public String getSuspendedOnAsString() {
        return suspendedOnAsString;
    }

    @JsonProperty("suspendedOnAsString")
    public void setSuspendedOnAsString(String suspendedOnAsString) {
        this.suspendedOnAsString = suspendedOnAsString;
    }

    @JsonProperty("restrictedMsisdnAllow")
    public Object getRestrictedMsisdnAllow() {
        return restrictedMsisdnAllow;
    }

    @JsonProperty("restrictedMsisdnAllow")
    public void setRestrictedMsisdnAllow(Object restrictedMsisdnAllow) {
        this.restrictedMsisdnAllow = restrictedMsisdnAllow;
    }

    @JsonProperty("rolesMapSelectedCount")
    public Long getRolesMapSelectedCount() {
        return rolesMapSelectedCount;
    }

    @JsonProperty("rolesMapSelectedCount")
    public void setRolesMapSelectedCount(Long rolesMapSelectedCount) {
        this.rolesMapSelectedCount = rolesMapSelectedCount;
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
