
package restassuredapi.pojo.channelUserListResponsepojo;

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
    "_commissionProfileApplicableFromAsString",
    "accessType",
    "activatedOn",
    "activeUserID",
    "activeUserLoginId",
    "activeUserMsisdn",
    "activeUserPin",
    "addCommProfOTFDetailId",
    "address1",
    "address2",
    "agentBalanceList",
    "alertEmail",
    "alertMsisdn",
    "alertType",
    "allowedDays",
    "allowedIps",
    "allowedUserTypeCreation",
    "appintmentDate",
    "applicationID",
    "appointmentDate",
    "assType",
    "asscMsisdnDate",
    "asscMsisdnList",
    "assoMsisdn",
    "associatedGeographicalList",
    "associatedProductTypeList",
    "associatedServiceTypeList",
    "associationCreatedOn",
    "associationModifiedOn",
    "authType",
    "authTypeAllowed",
    "autoc2callowed",
    "autoc2cquantity",
    "balance",
    "balanceStr",
    "batchID",
    "batchName",
    "browserType",
    "c2sMisFromDate",
    "c2sMisToDate",
    "catLowBalanceAlertAllow",
    "catOutletAllowed",
    "categoryCode",
    "categoryCodeDesc",
    "categoryList",
    "categoryName",
    "categoryVO",
    "cellID",
    "channelUserID",
    "city",
    "commissionProfileApplicableFrom",
    "commissionProfileLang1Msg",
    "commissionProfileLang2Msg",
    "commissionProfileSetID",
    "commissionProfileSetName",
    "commissionProfileSetVersion",
    "commissionProfileStatus",
    "commissionProfileSuspendMsg",
    "company",
    "confirmPassword",
    "contactNo",
    "contactPerson",
    "controlGroup",
    "country",
    "countryCode",
    "createdBy",
    "createdByUserName",
    "createdOn",
    "createdOnAsString",
    "creationType",
    "currentModule",
    "currentRoleCode",
    "decryptionKey",
    "departmentCode",
    "departmentDesc",
    "departmentList",
    "designation",
    "divisionCode",
    "divisionDesc",
    "divisionList",
    "documentNo",
    "documentType",
    "domainCodes",
    "domainID",
    "domainList",
    "domainName",
    "domainStatus",
    "domainTypeCode",
    "dualCommissionType",
    "email",
    "empCode",
    "externalCode",
    "fax",
    "firstExternalUserModify",
    "firstName",
    "fromCategoryCode",
    "fromTime",
    "fromUserName",
    "fullAddress",
    "fxedInfoStr",
    "gateway",
    "geographicalAreaList",
    "geographicalCode",
    "geographicalCodeArray",
    "geographicalCodeStatus",
    "geographicalCodeforNewuser",
    "geographicalDesc",
    "geographicalList",
    "groupRoleCode",
    "groupRoleFlag",
    "grphDomainTypeName",
    "imei",
    "inSuspend",
    "info1",
    "info10",
    "info11",
    "info12",
    "info13",
    "info14",
    "info15",
    "info2",
    "info3",
    "info4",
    "info5",
    "info6",
    "info7",
    "info8",
    "info9",
    "invalidPasswordCount",
    "invalidPinCount",
    "isSerAssignChnlAdm",
    "language",
    "languageCode",
    "languageName",
    "lastLoginOn",
    "lastModified",
    "lastName",
    "lastSosProductCode",
    "lastSosStatus",
    "lastSosTransactionId",
    "latitude",
    "level1ApprovedBy",
    "level1ApprovedOn",
    "level2ApprovedBy",
    "level2ApprovedOn",
    "lmsProfile",
    "lmsProfileId",
    "loggerMessage",
    "loginID",
    "loginTime",
    "longitude",
    "lowBalAlertAllow",
    "lrAllowed",
    "lrMaxAmount",
    "lrTransferAmount",
    "maxTxnAmount",
    "maxUserLevel",
    "mcommerceServiceAllow",
    "menuItemList",
    "message",
    "modifiedBy",
    "modifiedOn",
    "moduleCodeString",
    "monthlyTransAmt",
    "mpayProfileID",
    "msisdn",
    "msisdnList",
    "msisdnPrefix",
    "multipleMsisdnlist",
    "nameAndId",
    "networkCode",
    "networkID",
    "networkList",
    "networkName",
    "networkNamewithNetworkCode",
    "networkStatus",
    "oldLastLoginOn",
    "optInOutStatus",
    "otfCount",
    "otfValue",
    "othCommSetId",
    "otp",
    "otpInvalidCount",
    "otpModifiedOn",
    "otpvalidated",
    "outSuspened",
    "outletCode",
    "ownerCategoryName",
    "ownerCompany",
    "ownerID",
    "ownerMsisdn",
    "ownerName",
    "p2pMisFromDate",
    "p2pMisToDate",
    "pageCodeString",
    "parentCategoryName",
    "parentGeographyCode",
    "parentID",
    "parentLocale",
    "parentLoginID",
    "parentMsisdn",
    "parentName",
    "parentStatus",
    "password",
    "passwordCountUpdatedOn",
    "passwordModifiedOn",
    "passwordModifyFlag",
    "passwordReset",
    "paymentType",
    "paymentTypes",
    "paymentTypesList",
    "phoneProfile",
    "pinRequired",
    "pinReset",
    "prefixId",
    "prevBalanceStr",
    "prevCategoryCode",
    "prevParentName",
    "prevUserId",
    "prevUserName",
    "prevUserNameWithCategory",
    "prevUserParentNameWithCategory",
    "previousBalance",
    "previousStatus",
    "primaryMsisdn",
    "primaryMsisdnPin",
    "productCode",
    "productCodes",
    "productsList",
    "recordNumber",
    "referenceID",
    "remarks",
    "remoteAddress",
    "reportHeaderName",
    "requestType",
    "requetedByUserName",
    "requetedOnAsString",
    "resetPinOTPMessage",
    "restrictedMsisdnAllow",
    "returnFlag",
    "roleFlag",
    "roleType",
    "rolesMap",
    "rolesMapSelectedCount",
    "rsaAllowed",
    "rsaFlag",
    "rsaRequired",
    "rsavalidated",
    "securityAnswer",
    "segmentList",
    "segments",
    "serviceList",
    "serviceTypeList",
    "serviceTypes",
    "servicesList",
    "servicesTypes",
    "sessionInfoVO",
    "shortName",
    "showPassword",
    "smsMSisdn",
    "smsPin",
    "sosAllowed",
    "sosAllowedAmount",
    "sosThresholdLimit",
    "ssn",
    "staffUser",
    "state",
    "status",
    "statusDesc",
    "statusList",
    "subOutletCode",
    "suspendedByUserName",
    "suspendedOnAsString",
    "switchID",
    "toCategoryCode",
    "toTime",
    "toUserName",
    "tokenLastUsedDate",
    "trannferRuleTypeId",
    "transferCategory",
    "transferProfileID",
    "transferProfileName",
    "transferProfileStatus",
    "transferRuleID",
    "trnsfrdUserHierhyList",
    "updateSimRequired",
    "userBalance",
    "userBalanceList",
    "userChargeGrouptypeCounters",
    "userCode",
    "userControlGrouptypeCounters",
    "userGrade",
    "userGradeName",
    "userID",
    "userIDPrefix",
    "userLanguage",
    "userLanguageDesc",
    "userLanguageList",
    "userName",
    "userNamePrefix",
    "userNamePrefixCode",
    "userNamePrefixDesc",
    "userNamePrefixList",
    "userNameWithCategory",
    "userNamewithLoginId",
    "userNamewithUserId",
    "userPhoneVO",
    "userPhonesId",
    "userProfileID",
    "userType",
    "userlevel",
    "usingNewSTK",
    "validRequestURLs",
    "validStatus",
    "voucherList",
    "voucherTypes",
    "webLoginID"
})
public class ChannelUserVO {

    @JsonProperty("_commissionProfileApplicableFromAsString")
    private String commissionProfileApplicableFromAsString;
    @JsonProperty("accessType")
    private String accessType;
    @JsonProperty("activatedOn")
    private String activatedOn;
    @JsonProperty("activeUserID")
    private String activeUserID;
    @JsonProperty("activeUserLoginId")
    private String activeUserLoginId;
    @JsonProperty("activeUserMsisdn")
    private String activeUserMsisdn;
    @JsonProperty("activeUserPin")
    private String activeUserPin;
    @JsonProperty("addCommProfOTFDetailId")
    private String addCommProfOTFDetailId;
    @JsonProperty("address1")
    private String address1;
    @JsonProperty("address2")
    private String address2;
    @JsonProperty("agentBalanceList")
    private List<AgentBalanceList> agentBalanceList = null;
    @JsonProperty("alertEmail")
    private String alertEmail;
    @JsonProperty("alertMsisdn")
    private String alertMsisdn;
    @JsonProperty("alertType")
    private String alertType;
    @JsonProperty("allowedDays")
    private String allowedDays;
    @JsonProperty("allowedIps")
    private String allowedIps;
    @JsonProperty("allowedUserTypeCreation")
    private String allowedUserTypeCreation;
    @JsonProperty("appintmentDate")
    private String appintmentDate;
    @JsonProperty("applicationID")
    private String applicationID;
    @JsonProperty("appointmentDate")
    private String appointmentDate;
    @JsonProperty("assType")
    private String assType;
    @JsonProperty("asscMsisdnDate")
    private String asscMsisdnDate;
    @JsonProperty("asscMsisdnList")
    private List<AsscMsisdnList> asscMsisdnList = null;
    @JsonProperty("assoMsisdn")
    private String assoMsisdn;
    @JsonProperty("associatedGeographicalList")
    private List<AssociatedGeographicalList> associatedGeographicalList = null;
    @JsonProperty("associatedProductTypeList")
    private List<AssociatedProductTypeList> associatedProductTypeList = null;
    @JsonProperty("associatedServiceTypeList")
    private List<AssociatedServiceTypeList> associatedServiceTypeList = null;
    @JsonProperty("associationCreatedOn")
    private String associationCreatedOn;
    @JsonProperty("associationModifiedOn")
    private String associationModifiedOn;
    @JsonProperty("authType")
    private String authType;
    @JsonProperty("authTypeAllowed")
    private String authTypeAllowed;
    @JsonProperty("autoc2callowed")
    private String autoc2callowed;
    @JsonProperty("autoc2cquantity")
    private String autoc2cquantity;
    @JsonProperty("balance")
    private int balance;
    @JsonProperty("balanceStr")
    private String balanceStr;
    @JsonProperty("batchID")
    private String batchID;
    @JsonProperty("batchName")
    private String batchName;
    @JsonProperty("browserType")
    private String browserType;
    @JsonProperty("c2sMisFromDate")
    private String c2sMisFromDate;
    @JsonProperty("c2sMisToDate")
    private String c2sMisToDate;
    @JsonProperty("catLowBalanceAlertAllow")
    private String catLowBalanceAlertAllow;
    @JsonProperty("catOutletAllowed")
    private String catOutletAllowed;
    @JsonProperty("categoryCode")
    private String categoryCode;
    @JsonProperty("categoryCodeDesc")
    private String categoryCodeDesc;
    @JsonProperty("categoryList")
    private CategoryList categoryList;
    @JsonProperty("categoryName")
    private String categoryName;
    @JsonProperty("categoryVO")
    private CategoryVO categoryVO;
    @JsonProperty("cellID")
    private String cellID;
    @JsonProperty("channelUserID")
    private String channelUserID;
    @JsonProperty("city")
    private String city;
    @JsonProperty("commissionProfileApplicableFrom")
    private String commissionProfileApplicableFrom;
    @JsonProperty("commissionProfileLang1Msg")
    private String commissionProfileLang1Msg;
    @JsonProperty("commissionProfileLang2Msg")
    private String commissionProfileLang2Msg;
    @JsonProperty("commissionProfileSetID")
    private String commissionProfileSetID;
    @JsonProperty("commissionProfileSetName")
    private String commissionProfileSetName;
    @JsonProperty("commissionProfileSetVersion")
    private String commissionProfileSetVersion;
    @JsonProperty("commissionProfileStatus")
    private String commissionProfileStatus;
    @JsonProperty("commissionProfileSuspendMsg")
    private String commissionProfileSuspendMsg;
    @JsonProperty("company")
    private String company;
    @JsonProperty("confirmPassword")
    private String confirmPassword;
    @JsonProperty("contactNo")
    private String contactNo;
    @JsonProperty("contactPerson")
    private String contactPerson;
    @JsonProperty("controlGroup")
    private String controlGroup;
    @JsonProperty("country")
    private String country;
    @JsonProperty("countryCode")
    private String countryCode;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdByUserName")
    private String createdByUserName;
    @JsonProperty("createdOn")
    private String createdOn;
    @JsonProperty("createdOnAsString")
    private String createdOnAsString;
    @JsonProperty("creationType")
    private String creationType;
    @JsonProperty("currentModule")
    private String currentModule;
    @JsonProperty("currentRoleCode")
    private String currentRoleCode;
    @JsonProperty("decryptionKey")
    private String decryptionKey;
    @JsonProperty("departmentCode")
    private String departmentCode;
    @JsonProperty("departmentDesc")
    private String departmentDesc;
    @JsonProperty("departmentList")
    private List<DepartmentList> departmentList = null;
    @JsonProperty("designation")
    private String designation;
    @JsonProperty("divisionCode")
    private String divisionCode;
    @JsonProperty("divisionDesc")
    private String divisionDesc;
    @JsonProperty("divisionList")
    private List<DivisionList> divisionList = null;
    @JsonProperty("documentNo")
    private String documentNo;
    @JsonProperty("documentType")
    private String documentType;
    @JsonProperty("domainCodes")
    private List<String> domainCodes = null;
    @JsonProperty("domainID")
    private String domainID;
    @JsonProperty("domainList")
    private List<DomainList> domainList = null;
    @JsonProperty("domainName")
    private String domainName;
    @JsonProperty("domainStatus")
    private String domainStatus;
    @JsonProperty("domainTypeCode")
    private String domainTypeCode;
    @JsonProperty("dualCommissionType")
    private String dualCommissionType;
    @JsonProperty("email")
    private String email;
    @JsonProperty("empCode")
    private String empCode;
    @JsonProperty("externalCode")
    private String externalCode;
    @JsonProperty("fax")
    private String fax;
    @JsonProperty("firstExternalUserModify")
    private boolean firstExternalUserModify;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("fromCategoryCode")
    private String fromCategoryCode;
    @JsonProperty("fromTime")
    private String fromTime;
    @JsonProperty("fromUserName")
    private String fromUserName;
    @JsonProperty("fullAddress")
    private String fullAddress;
    @JsonProperty("fxedInfoStr")
    private String fxedInfoStr;
    @JsonProperty("gateway")
    private String gateway;
    @JsonProperty("geographicalAreaList")
    private List<GeographicalAreaList> geographicalAreaList = null;
    @JsonProperty("geographicalCode")
    private String geographicalCode;
    @JsonProperty("geographicalCodeArray")
    private List<String> geographicalCodeArray = null;
    @JsonProperty("geographicalCodeStatus")
    private String geographicalCodeStatus;
    @JsonProperty("geographicalCodeforNewuser")
    private String geographicalCodeforNewuser;
    @JsonProperty("geographicalDesc")
    private String geographicalDesc;
    @JsonProperty("geographicalList")
    private List<GeographicalList> geographicalList = null;
    @JsonProperty("groupRoleCode")
    private String groupRoleCode;
    @JsonProperty("groupRoleFlag")
    private String groupRoleFlag;
    @JsonProperty("grphDomainTypeName")
    private String grphDomainTypeName;
    @JsonProperty("imei")
    private String imei;
    @JsonProperty("inSuspend")
    private String inSuspend;
    @JsonProperty("info1")
    private String info1;
    @JsonProperty("info10")
    private String info10;
    @JsonProperty("info11")
    private String info11;
    @JsonProperty("info12")
    private String info12;
    @JsonProperty("info13")
    private String info13;
    @JsonProperty("info14")
    private String info14;
    @JsonProperty("info15")
    private String info15;
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
    @JsonProperty("invalidPasswordCount")
    private int invalidPasswordCount;
    @JsonProperty("invalidPinCount")
    private int invalidPinCount;
    @JsonProperty("isSerAssignChnlAdm")
    private boolean isSerAssignChnlAdm;
    @JsonProperty("language")
    private String language;
    @JsonProperty("languageCode")
    private String languageCode;
    @JsonProperty("languageName")
    private String languageName;
    @JsonProperty("lastLoginOn")
    private String lastLoginOn;
    @JsonProperty("lastModified")
    private int lastModified;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("lastSosProductCode")
    private String lastSosProductCode;
    @JsonProperty("lastSosStatus")
    private String lastSosStatus;
    @JsonProperty("lastSosTransactionId")
    private String lastSosTransactionId;
    @JsonProperty("latitude")
    private String latitude;
    @JsonProperty("level1ApprovedBy")
    private String level1ApprovedBy;
    @JsonProperty("level1ApprovedOn")
    private String level1ApprovedOn;
    @JsonProperty("level2ApprovedBy")
    private String level2ApprovedBy;
    @JsonProperty("level2ApprovedOn")
    private String level2ApprovedOn;
    @JsonProperty("lmsProfile")
    private String lmsProfile;
    @JsonProperty("lmsProfileId")
    private String lmsProfileId;
    @JsonProperty("loggerMessage")
    private String loggerMessage;
    @JsonProperty("loginID")
    private String loginID;
    @JsonProperty("loginTime")
    private String loginTime;
    @JsonProperty("longitude")
    private String longitude;
    @JsonProperty("lowBalAlertAllow")
    private String lowBalAlertAllow;
    @JsonProperty("lrAllowed")
    private String lrAllowed;
    @JsonProperty("lrMaxAmount")
    private int lrMaxAmount;
    @JsonProperty("lrTransferAmount")
    private int lrTransferAmount;
    @JsonProperty("maxTxnAmount")
    private int maxTxnAmount;
    @JsonProperty("maxUserLevel")
    private int maxUserLevel;
    @JsonProperty("mcommerceServiceAllow")
    private String mcommerceServiceAllow;
    @JsonProperty("menuItemList")
    private List<MenuItemList> menuItemList = null;
    @JsonProperty("message")
    private String message;
    @JsonProperty("modifiedBy")
    private String modifiedBy;
    @JsonProperty("modifiedOn")
    private String modifiedOn;
    @JsonProperty("moduleCodeString")
    private String moduleCodeString;
    @JsonProperty("monthlyTransAmt")
    private int monthlyTransAmt;
    @JsonProperty("mpayProfileID")
    private String mpayProfileID;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("msisdnList")
    private List<MsisdnList> msisdnList = null;
    @JsonProperty("msisdnPrefix")
    private String msisdnPrefix;
    @JsonProperty("multipleMsisdnlist")
    private String multipleMsisdnlist;
    @JsonProperty("nameAndId")
    private String nameAndId;
    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("networkID")
    private String networkID;
    @JsonProperty("networkList")
    private List<NetworkList> networkList = null;
    @JsonProperty("networkName")
    private String networkName;
    @JsonProperty("networkNamewithNetworkCode")
    private String networkNamewithNetworkCode;
    @JsonProperty("networkStatus")
    private String networkStatus;
    @JsonProperty("oldLastLoginOn")
    private String oldLastLoginOn;
    @JsonProperty("optInOutStatus")
    private String optInOutStatus;
    @JsonProperty("otfCount")
    private int otfCount;
    @JsonProperty("otfValue")
    private int otfValue;
    @JsonProperty("othCommSetId")
    private String othCommSetId;
    @JsonProperty("otp")
    private String otp;
    @JsonProperty("otpInvalidCount")
    private int otpInvalidCount;
    @JsonProperty("otpModifiedOn")
    private String otpModifiedOn;
    @JsonProperty("otpvalidated")
    private boolean otpvalidated;
    @JsonProperty("outSuspened")
    private String outSuspened;
    @JsonProperty("outletCode")
    private String outletCode;
    @JsonProperty("ownerCategoryName")
    private String ownerCategoryName;
    @JsonProperty("ownerCompany")
    private String ownerCompany;
    @JsonProperty("ownerID")
    private String ownerID;
    @JsonProperty("ownerMsisdn")
    private String ownerMsisdn;
    @JsonProperty("ownerName")
    private String ownerName;
    @JsonProperty("p2pMisFromDate")
    private String p2pMisFromDate;
    @JsonProperty("p2pMisToDate")
    private String p2pMisToDate;
    @JsonProperty("pageCodeString")
    private String pageCodeString;
    @JsonProperty("parentCategoryName")
    private String parentCategoryName;
    @JsonProperty("parentGeographyCode")
    private String parentGeographyCode;
    @JsonProperty("parentID")
    private String parentID;
    @JsonProperty("parentLocale")
    private ParentLocale parentLocale;
    @JsonProperty("parentLoginID")
    private String parentLoginID;
    @JsonProperty("parentMsisdn")
    private String parentMsisdn;
    @JsonProperty("parentName")
    private String parentName;
    @JsonProperty("parentStatus")
    private String parentStatus;
    @JsonProperty("password")
    private String password;
    @JsonProperty("passwordCountUpdatedOn")
    private String passwordCountUpdatedOn;
    @JsonProperty("passwordModifiedOn")
    private String passwordModifiedOn;
    @JsonProperty("passwordModifyFlag")
    private boolean passwordModifyFlag;
    @JsonProperty("passwordReset")
    private String passwordReset;
    @JsonProperty("paymentType")
    private String paymentType;
    @JsonProperty("paymentTypes")
    private String paymentTypes;
    @JsonProperty("paymentTypesList")
    private List<PaymentTypesList> paymentTypesList = null;
    @JsonProperty("phoneProfile")
    private String phoneProfile;
    @JsonProperty("pinRequired")
    private String pinRequired;
    @JsonProperty("pinReset")
    private String pinReset;
    @JsonProperty("prefixId")
    private int prefixId;
    @JsonProperty("prevBalanceStr")
    private String prevBalanceStr;
    @JsonProperty("prevCategoryCode")
    private String prevCategoryCode;
    @JsonProperty("prevParentName")
    private String prevParentName;
    @JsonProperty("prevUserId")
    private String prevUserId;
    @JsonProperty("prevUserName")
    private String prevUserName;
    @JsonProperty("prevUserNameWithCategory")
    private String prevUserNameWithCategory;
    @JsonProperty("prevUserParentNameWithCategory")
    private String prevUserParentNameWithCategory;
    @JsonProperty("previousBalance")
    private int previousBalance;
    @JsonProperty("previousStatus")
    private String previousStatus;
    @JsonProperty("primaryMsisdn")
    private String primaryMsisdn;
    @JsonProperty("primaryMsisdnPin")
    private String primaryMsisdnPin;
    @JsonProperty("productCode")
    private String productCode;
    @JsonProperty("productCodes")
    private List<String> productCodes = null;
    @JsonProperty("productsList")
    private List<ProductsList> productsList = null;
    @JsonProperty("recordNumber")
    private String recordNumber;
    @JsonProperty("referenceID")
    private String referenceID;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("remoteAddress")
    private String remoteAddress;
    @JsonProperty("reportHeaderName")
    private String reportHeaderName;
    @JsonProperty("requestType")
    private String requestType;
    @JsonProperty("requetedByUserName")
    private String requetedByUserName;
    @JsonProperty("requetedOnAsString")
    private String requetedOnAsString;
    @JsonProperty("resetPinOTPMessage")
    private ResetPinOTPMessage resetPinOTPMessage;
    @JsonProperty("restrictedMsisdnAllow")
    private String restrictedMsisdnAllow;
    @JsonProperty("returnFlag")
    private boolean returnFlag;
    @JsonProperty("roleFlag")
    private List<String> roleFlag = null;
    @JsonProperty("roleType")
    private String roleType;
    @JsonProperty("rolesMap")
    private RolesMap rolesMap;
    @JsonProperty("rolesMapSelectedCount")
    private int rolesMapSelectedCount;
    @JsonProperty("rsaAllowed")
    private boolean rsaAllowed;
    @JsonProperty("rsaFlag")
    private String rsaFlag;
    @JsonProperty("rsaRequired")
    private boolean rsaRequired;
    @JsonProperty("rsavalidated")
    private boolean rsavalidated;
    @JsonProperty("securityAnswer")
    private String securityAnswer;
    @JsonProperty("segmentList")
    private List<SegmentList> segmentList = null;
    @JsonProperty("segments")
    private String segments;
    @JsonProperty("serviceList")
    private List<ServiceList> serviceList = null;
    @JsonProperty("serviceTypeList")
    private List<String> serviceTypeList = null;
    @JsonProperty("serviceTypes")
    private String serviceTypes;
    @JsonProperty("servicesList")
    private List<ServicesList> servicesList = null;
    @JsonProperty("servicesTypes")
    private List<String> servicesTypes = null;
    @JsonProperty("sessionInfoVO")
    private SessionInfoVO sessionInfoVO;
    @JsonProperty("shortName")
    private String shortName;
    @JsonProperty("showPassword")
    private String showPassword;
    @JsonProperty("smsMSisdn")
    private String smsMSisdn;
    @JsonProperty("smsPin")
    private String smsPin;
    @JsonProperty("sosAllowed")
    private String sosAllowed;
    @JsonProperty("sosAllowedAmount")
    private int sosAllowedAmount;
    @JsonProperty("sosThresholdLimit")
    private int sosThresholdLimit;
    @JsonProperty("ssn")
    private String ssn;
    @JsonProperty("staffUser")
    private boolean staffUser;
    @JsonProperty("state")
    private String state;
    @JsonProperty("status")
    private String status;
    @JsonProperty("statusDesc")
    private String statusDesc;
    @JsonProperty("statusList")
    private List<StatusList> statusList = null;
    @JsonProperty("subOutletCode")
    private String subOutletCode;
    @JsonProperty("suspendedByUserName")
    private String suspendedByUserName;
    @JsonProperty("suspendedOnAsString")
    private String suspendedOnAsString;
    @JsonProperty("switchID")
    private String switchID;
    @JsonProperty("toCategoryCode")
    private String toCategoryCode;
    @JsonProperty("toTime")
    private String toTime;
    @JsonProperty("toUserName")
    private String toUserName;
    @JsonProperty("tokenLastUsedDate")
    private String tokenLastUsedDate;
    @JsonProperty("trannferRuleTypeId")
    private String trannferRuleTypeId;
    @JsonProperty("transferCategory")
    private String transferCategory;
    @JsonProperty("transferProfileID")
    private String transferProfileID;
    @JsonProperty("transferProfileName")
    private String transferProfileName;
    @JsonProperty("transferProfileStatus")
    private String transferProfileStatus;
    @JsonProperty("transferRuleID")
    private String transferRuleID;
    @JsonProperty("trnsfrdUserHierhyList")
    private List<TrnsfrdUserHierhyList> trnsfrdUserHierhyList = null;
    @JsonProperty("updateSimRequired")
    private boolean updateSimRequired;
    @JsonProperty("userBalance")
    private String userBalance;
    @JsonProperty("userBalanceList")
    private List<UserBalanceList> userBalanceList = null;
    @JsonProperty("userChargeGrouptypeCounters")
    private UserChargeGrouptypeCounters userChargeGrouptypeCounters;
    @JsonProperty("userCode")
    private String userCode;
    @JsonProperty("userControlGrouptypeCounters")
    private UserControlGrouptypeCounters userControlGrouptypeCounters;
    @JsonProperty("userGrade")
    private String userGrade;
    @JsonProperty("userGradeName")
    private String userGradeName;
    @JsonProperty("userID")
    private String userID;
    @JsonProperty("userIDPrefix")
    private String userIDPrefix;
    @JsonProperty("userLanguage")
    private String userLanguage;
    @JsonProperty("userLanguageDesc")
    private String userLanguageDesc;
    @JsonProperty("userLanguageList")
    private List<UserLanguageList> userLanguageList = null;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("userNamePrefix")
    private String userNamePrefix;
    @JsonProperty("userNamePrefixCode")
    private String userNamePrefixCode;
    @JsonProperty("userNamePrefixDesc")
    private String userNamePrefixDesc;
    @JsonProperty("userNamePrefixList")
    private List<UserNamePrefixList> userNamePrefixList = null;
    @JsonProperty("userNameWithCategory")
    private String userNameWithCategory;
    @JsonProperty("userNamewithLoginId")
    private String userNamewithLoginId;
    @JsonProperty("userNamewithUserId")
    private String userNamewithUserId;
    @JsonProperty("userPhoneVO")
    private UserPhoneVO userPhoneVO;
    @JsonProperty("userPhonesId")
    private String userPhonesId;
    @JsonProperty("userProfileID")
    private String userProfileID;
    @JsonProperty("userType")
    private String userType;
    @JsonProperty("userlevel")
    private String userlevel;
    @JsonProperty("usingNewSTK")
    private boolean usingNewSTK;
    @JsonProperty("validRequestURLs")
    private String validRequestURLs;
    @JsonProperty("validStatus")
    private int validStatus;
    @JsonProperty("voucherList")
    private List<VoucherList> voucherList = null;
    @JsonProperty("voucherTypes")
    private String voucherTypes;
    @JsonProperty("webLoginID")
    private String webLoginID;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("_commissionProfileApplicableFromAsString")
    public String getCommissionProfileApplicableFromAsString() {
        return commissionProfileApplicableFromAsString;
    }

    @JsonProperty("_commissionProfileApplicableFromAsString")
    public void setCommissionProfileApplicableFromAsString(String commissionProfileApplicableFromAsString) {
        this.commissionProfileApplicableFromAsString = commissionProfileApplicableFromAsString;
    }

    @JsonProperty("accessType")
    public String getAccessType() {
        return accessType;
    }

    @JsonProperty("accessType")
    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    @JsonProperty("activatedOn")
    public String getActivatedOn() {
        return activatedOn;
    }

    @JsonProperty("activatedOn")
    public void setActivatedOn(String activatedOn) {
        this.activatedOn = activatedOn;
    }

    @JsonProperty("activeUserID")
    public String getActiveUserID() {
        return activeUserID;
    }

    @JsonProperty("activeUserID")
    public void setActiveUserID(String activeUserID) {
        this.activeUserID = activeUserID;
    }

    @JsonProperty("activeUserLoginId")
    public String getActiveUserLoginId() {
        return activeUserLoginId;
    }

    @JsonProperty("activeUserLoginId")
    public void setActiveUserLoginId(String activeUserLoginId) {
        this.activeUserLoginId = activeUserLoginId;
    }

    @JsonProperty("activeUserMsisdn")
    public String getActiveUserMsisdn() {
        return activeUserMsisdn;
    }

    @JsonProperty("activeUserMsisdn")
    public void setActiveUserMsisdn(String activeUserMsisdn) {
        this.activeUserMsisdn = activeUserMsisdn;
    }

    @JsonProperty("activeUserPin")
    public String getActiveUserPin() {
        return activeUserPin;
    }

    @JsonProperty("activeUserPin")
    public void setActiveUserPin(String activeUserPin) {
        this.activeUserPin = activeUserPin;
    }

    @JsonProperty("addCommProfOTFDetailId")
    public String getAddCommProfOTFDetailId() {
        return addCommProfOTFDetailId;
    }

    @JsonProperty("addCommProfOTFDetailId")
    public void setAddCommProfOTFDetailId(String addCommProfOTFDetailId) {
        this.addCommProfOTFDetailId = addCommProfOTFDetailId;
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

    @JsonProperty("agentBalanceList")
    public List<AgentBalanceList> getAgentBalanceList() {
        return agentBalanceList;
    }

    @JsonProperty("agentBalanceList")
    public void setAgentBalanceList(List<AgentBalanceList> agentBalanceList) {
        this.agentBalanceList = agentBalanceList;
    }

    @JsonProperty("alertEmail")
    public String getAlertEmail() {
        return alertEmail;
    }

    @JsonProperty("alertEmail")
    public void setAlertEmail(String alertEmail) {
        this.alertEmail = alertEmail;
    }

    @JsonProperty("alertMsisdn")
    public String getAlertMsisdn() {
        return alertMsisdn;
    }

    @JsonProperty("alertMsisdn")
    public void setAlertMsisdn(String alertMsisdn) {
        this.alertMsisdn = alertMsisdn;
    }

    @JsonProperty("alertType")
    public String getAlertType() {
        return alertType;
    }

    @JsonProperty("alertType")
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    @JsonProperty("allowedDays")
    public String getAllowedDays() {
        return allowedDays;
    }

    @JsonProperty("allowedDays")
    public void setAllowedDays(String allowedDays) {
        this.allowedDays = allowedDays;
    }

    @JsonProperty("allowedIps")
    public String getAllowedIps() {
        return allowedIps;
    }

    @JsonProperty("allowedIps")
    public void setAllowedIps(String allowedIps) {
        this.allowedIps = allowedIps;
    }

    @JsonProperty("allowedUserTypeCreation")
    public String getAllowedUserTypeCreation() {
        return allowedUserTypeCreation;
    }

    @JsonProperty("allowedUserTypeCreation")
    public void setAllowedUserTypeCreation(String allowedUserTypeCreation) {
        this.allowedUserTypeCreation = allowedUserTypeCreation;
    }

    @JsonProperty("appintmentDate")
    public String getAppintmentDate() {
        return appintmentDate;
    }

    @JsonProperty("appintmentDate")
    public void setAppintmentDate(String appintmentDate) {
        this.appintmentDate = appintmentDate;
    }

    @JsonProperty("applicationID")
    public String getApplicationID() {
        return applicationID;
    }

    @JsonProperty("applicationID")
    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    @JsonProperty("appointmentDate")
    public String getAppointmentDate() {
        return appointmentDate;
    }

    @JsonProperty("appointmentDate")
    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    @JsonProperty("assType")
    public String getAssType() {
        return assType;
    }

    @JsonProperty("assType")
    public void setAssType(String assType) {
        this.assType = assType;
    }

    @JsonProperty("asscMsisdnDate")
    public String getAsscMsisdnDate() {
        return asscMsisdnDate;
    }

    @JsonProperty("asscMsisdnDate")
    public void setAsscMsisdnDate(String asscMsisdnDate) {
        this.asscMsisdnDate = asscMsisdnDate;
    }

    @JsonProperty("asscMsisdnList")
    public List<AsscMsisdnList> getAsscMsisdnList() {
        return asscMsisdnList;
    }

    @JsonProperty("asscMsisdnList")
    public void setAsscMsisdnList(List<AsscMsisdnList> asscMsisdnList) {
        this.asscMsisdnList = asscMsisdnList;
    }

    @JsonProperty("assoMsisdn")
    public String getAssoMsisdn() {
        return assoMsisdn;
    }

    @JsonProperty("assoMsisdn")
    public void setAssoMsisdn(String assoMsisdn) {
        this.assoMsisdn = assoMsisdn;
    }

    @JsonProperty("associatedGeographicalList")
    public List<AssociatedGeographicalList> getAssociatedGeographicalList() {
        return associatedGeographicalList;
    }

    @JsonProperty("associatedGeographicalList")
    public void setAssociatedGeographicalList(List<AssociatedGeographicalList> associatedGeographicalList) {
        this.associatedGeographicalList = associatedGeographicalList;
    }

    @JsonProperty("associatedProductTypeList")
    public List<AssociatedProductTypeList> getAssociatedProductTypeList() {
        return associatedProductTypeList;
    }

    @JsonProperty("associatedProductTypeList")
    public void setAssociatedProductTypeList(List<AssociatedProductTypeList> associatedProductTypeList) {
        this.associatedProductTypeList = associatedProductTypeList;
    }

    @JsonProperty("associatedServiceTypeList")
    public List<AssociatedServiceTypeList> getAssociatedServiceTypeList() {
        return associatedServiceTypeList;
    }

    @JsonProperty("associatedServiceTypeList")
    public void setAssociatedServiceTypeList(List<AssociatedServiceTypeList> associatedServiceTypeList) {
        this.associatedServiceTypeList = associatedServiceTypeList;
    }

    @JsonProperty("associationCreatedOn")
    public String getAssociationCreatedOn() {
        return associationCreatedOn;
    }

    @JsonProperty("associationCreatedOn")
    public void setAssociationCreatedOn(String associationCreatedOn) {
        this.associationCreatedOn = associationCreatedOn;
    }

    @JsonProperty("associationModifiedOn")
    public String getAssociationModifiedOn() {
        return associationModifiedOn;
    }

    @JsonProperty("associationModifiedOn")
    public void setAssociationModifiedOn(String associationModifiedOn) {
        this.associationModifiedOn = associationModifiedOn;
    }

    @JsonProperty("authType")
    public String getAuthType() {
        return authType;
    }

    @JsonProperty("authType")
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    @JsonProperty("authTypeAllowed")
    public String getAuthTypeAllowed() {
        return authTypeAllowed;
    }

    @JsonProperty("authTypeAllowed")
    public void setAuthTypeAllowed(String authTypeAllowed) {
        this.authTypeAllowed = authTypeAllowed;
    }

    @JsonProperty("autoc2callowed")
    public String getAutoc2callowed() {
        return autoc2callowed;
    }

    @JsonProperty("autoc2callowed")
    public void setAutoc2callowed(String autoc2callowed) {
        this.autoc2callowed = autoc2callowed;
    }

    @JsonProperty("autoc2cquantity")
    public String getAutoc2cquantity() {
        return autoc2cquantity;
    }

    @JsonProperty("autoc2cquantity")
    public void setAutoc2cquantity(String autoc2cquantity) {
        this.autoc2cquantity = autoc2cquantity;
    }

    @JsonProperty("balance")
    public int getBalance() {
        return balance;
    }

    @JsonProperty("balance")
    public void setBalance(int balance) {
        this.balance = balance;
    }

    @JsonProperty("balanceStr")
    public String getBalanceStr() {
        return balanceStr;
    }

    @JsonProperty("balanceStr")
    public void setBalanceStr(String balanceStr) {
        this.balanceStr = balanceStr;
    }

    @JsonProperty("batchID")
    public String getBatchID() {
        return batchID;
    }

    @JsonProperty("batchID")
    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    @JsonProperty("batchName")
    public String getBatchName() {
        return batchName;
    }

    @JsonProperty("batchName")
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    @JsonProperty("browserType")
    public String getBrowserType() {
        return browserType;
    }

    @JsonProperty("browserType")
    public void setBrowserType(String browserType) {
        this.browserType = browserType;
    }

    @JsonProperty("c2sMisFromDate")
    public String getC2sMisFromDate() {
        return c2sMisFromDate;
    }

    @JsonProperty("c2sMisFromDate")
    public void setC2sMisFromDate(String c2sMisFromDate) {
        this.c2sMisFromDate = c2sMisFromDate;
    }

    @JsonProperty("c2sMisToDate")
    public String getC2sMisToDate() {
        return c2sMisToDate;
    }

    @JsonProperty("c2sMisToDate")
    public void setC2sMisToDate(String c2sMisToDate) {
        this.c2sMisToDate = c2sMisToDate;
    }

    @JsonProperty("catLowBalanceAlertAllow")
    public String getCatLowBalanceAlertAllow() {
        return catLowBalanceAlertAllow;
    }

    @JsonProperty("catLowBalanceAlertAllow")
    public void setCatLowBalanceAlertAllow(String catLowBalanceAlertAllow) {
        this.catLowBalanceAlertAllow = catLowBalanceAlertAllow;
    }

    @JsonProperty("catOutletAllowed")
    public String getCatOutletAllowed() {
        return catOutletAllowed;
    }

    @JsonProperty("catOutletAllowed")
    public void setCatOutletAllowed(String catOutletAllowed) {
        this.catOutletAllowed = catOutletAllowed;
    }

    @JsonProperty("categoryCode")
    public String getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @JsonProperty("categoryCodeDesc")
    public String getCategoryCodeDesc() {
        return categoryCodeDesc;
    }

    @JsonProperty("categoryCodeDesc")
    public void setCategoryCodeDesc(String categoryCodeDesc) {
        this.categoryCodeDesc = categoryCodeDesc;
    }

    @JsonProperty("categoryList")
    public CategoryList getCategoryList() {
        return categoryList;
    }

    @JsonProperty("categoryList")
    public void setCategoryList(CategoryList categoryList) {
        this.categoryList = categoryList;
    }

    @JsonProperty("categoryName")
    public String getCategoryName() {
        return categoryName;
    }

    @JsonProperty("categoryName")
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("categoryVO")
    public CategoryVO getCategoryVO() {
        return categoryVO;
    }

    @JsonProperty("categoryVO")
    public void setCategoryVO(CategoryVO categoryVO) {
        this.categoryVO = categoryVO;
    }

    @JsonProperty("cellID")
    public String getCellID() {
        return cellID;
    }

    @JsonProperty("cellID")
    public void setCellID(String cellID) {
        this.cellID = cellID;
    }

    @JsonProperty("channelUserID")
    public String getChannelUserID() {
        return channelUserID;
    }

    @JsonProperty("channelUserID")
    public void setChannelUserID(String channelUserID) {
        this.channelUserID = channelUserID;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("commissionProfileApplicableFrom")
    public String getCommissionProfileApplicableFrom() {
        return commissionProfileApplicableFrom;
    }

    @JsonProperty("commissionProfileApplicableFrom")
    public void setCommissionProfileApplicableFrom(String commissionProfileApplicableFrom) {
        this.commissionProfileApplicableFrom = commissionProfileApplicableFrom;
    }

    @JsonProperty("commissionProfileLang1Msg")
    public String getCommissionProfileLang1Msg() {
        return commissionProfileLang1Msg;
    }

    @JsonProperty("commissionProfileLang1Msg")
    public void setCommissionProfileLang1Msg(String commissionProfileLang1Msg) {
        this.commissionProfileLang1Msg = commissionProfileLang1Msg;
    }

    @JsonProperty("commissionProfileLang2Msg")
    public String getCommissionProfileLang2Msg() {
        return commissionProfileLang2Msg;
    }

    @JsonProperty("commissionProfileLang2Msg")
    public void setCommissionProfileLang2Msg(String commissionProfileLang2Msg) {
        this.commissionProfileLang2Msg = commissionProfileLang2Msg;
    }

    @JsonProperty("commissionProfileSetID")
    public String getCommissionProfileSetID() {
        return commissionProfileSetID;
    }

    @JsonProperty("commissionProfileSetID")
    public void setCommissionProfileSetID(String commissionProfileSetID) {
        this.commissionProfileSetID = commissionProfileSetID;
    }

    @JsonProperty("commissionProfileSetName")
    public String getCommissionProfileSetName() {
        return commissionProfileSetName;
    }

    @JsonProperty("commissionProfileSetName")
    public void setCommissionProfileSetName(String commissionProfileSetName) {
        this.commissionProfileSetName = commissionProfileSetName;
    }

    @JsonProperty("commissionProfileSetVersion")
    public String getCommissionProfileSetVersion() {
        return commissionProfileSetVersion;
    }

    @JsonProperty("commissionProfileSetVersion")
    public void setCommissionProfileSetVersion(String commissionProfileSetVersion) {
        this.commissionProfileSetVersion = commissionProfileSetVersion;
    }

    @JsonProperty("commissionProfileStatus")
    public String getCommissionProfileStatus() {
        return commissionProfileStatus;
    }

    @JsonProperty("commissionProfileStatus")
    public void setCommissionProfileStatus(String commissionProfileStatus) {
        this.commissionProfileStatus = commissionProfileStatus;
    }

    @JsonProperty("commissionProfileSuspendMsg")
    public String getCommissionProfileSuspendMsg() {
        return commissionProfileSuspendMsg;
    }

    @JsonProperty("commissionProfileSuspendMsg")
    public void setCommissionProfileSuspendMsg(String commissionProfileSuspendMsg) {
        this.commissionProfileSuspendMsg = commissionProfileSuspendMsg;
    }

    @JsonProperty("company")
    public String getCompany() {
        return company;
    }

    @JsonProperty("company")
    public void setCompany(String company) {
        this.company = company;
    }

    @JsonProperty("confirmPassword")
    public String getConfirmPassword() {
        return confirmPassword;
    }

    @JsonProperty("confirmPassword")
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @JsonProperty("contactNo")
    public String getContactNo() {
        return contactNo;
    }

    @JsonProperty("contactNo")
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    @JsonProperty("contactPerson")
    public String getContactPerson() {
        return contactPerson;
    }

    @JsonProperty("contactPerson")
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @JsonProperty("controlGroup")
    public String getControlGroup() {
        return controlGroup;
    }

    @JsonProperty("controlGroup")
    public void setControlGroup(String controlGroup) {
        this.controlGroup = controlGroup;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("countryCode")
    public String getCountryCode() {
        return countryCode;
    }

    @JsonProperty("countryCode")
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("createdByUserName")
    public String getCreatedByUserName() {
        return createdByUserName;
    }

    @JsonProperty("createdByUserName")
    public void setCreatedByUserName(String createdByUserName) {
        this.createdByUserName = createdByUserName;
    }

    @JsonProperty("createdOn")
    public String getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    @JsonProperty("createdOnAsString")
    public String getCreatedOnAsString() {
        return createdOnAsString;
    }

    @JsonProperty("createdOnAsString")
    public void setCreatedOnAsString(String createdOnAsString) {
        this.createdOnAsString = createdOnAsString;
    }

    @JsonProperty("creationType")
    public String getCreationType() {
        return creationType;
    }

    @JsonProperty("creationType")
    public void setCreationType(String creationType) {
        this.creationType = creationType;
    }

    @JsonProperty("currentModule")
    public String getCurrentModule() {
        return currentModule;
    }

    @JsonProperty("currentModule")
    public void setCurrentModule(String currentModule) {
        this.currentModule = currentModule;
    }

    @JsonProperty("currentRoleCode")
    public String getCurrentRoleCode() {
        return currentRoleCode;
    }

    @JsonProperty("currentRoleCode")
    public void setCurrentRoleCode(String currentRoleCode) {
        this.currentRoleCode = currentRoleCode;
    }

    @JsonProperty("decryptionKey")
    public String getDecryptionKey() {
        return decryptionKey;
    }

    @JsonProperty("decryptionKey")
    public void setDecryptionKey(String decryptionKey) {
        this.decryptionKey = decryptionKey;
    }

    @JsonProperty("departmentCode")
    public String getDepartmentCode() {
        return departmentCode;
    }

    @JsonProperty("departmentCode")
    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    @JsonProperty("departmentDesc")
    public String getDepartmentDesc() {
        return departmentDesc;
    }

    @JsonProperty("departmentDesc")
    public void setDepartmentDesc(String departmentDesc) {
        this.departmentDesc = departmentDesc;
    }

    @JsonProperty("departmentList")
    public List<DepartmentList> getDepartmentList() {
        return departmentList;
    }

    @JsonProperty("departmentList")
    public void setDepartmentList(List<DepartmentList> departmentList) {
        this.departmentList = departmentList;
    }

    @JsonProperty("designation")
    public String getDesignation() {
        return designation;
    }

    @JsonProperty("designation")
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @JsonProperty("divisionCode")
    public String getDivisionCode() {
        return divisionCode;
    }

    @JsonProperty("divisionCode")
    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }

    @JsonProperty("divisionDesc")
    public String getDivisionDesc() {
        return divisionDesc;
    }

    @JsonProperty("divisionDesc")
    public void setDivisionDesc(String divisionDesc) {
        this.divisionDesc = divisionDesc;
    }

    @JsonProperty("divisionList")
    public List<DivisionList> getDivisionList() {
        return divisionList;
    }

    @JsonProperty("divisionList")
    public void setDivisionList(List<DivisionList> divisionList) {
        this.divisionList = divisionList;
    }

    @JsonProperty("documentNo")
    public String getDocumentNo() {
        return documentNo;
    }

    @JsonProperty("documentNo")
    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    @JsonProperty("documentType")
    public String getDocumentType() {
        return documentType;
    }

    @JsonProperty("documentType")
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    @JsonProperty("domainCodes")
    public List<String> getDomainCodes() {
        return domainCodes;
    }

    @JsonProperty("domainCodes")
    public void setDomainCodes(List<String> domainCodes) {
        this.domainCodes = domainCodes;
    }

    @JsonProperty("domainID")
    public String getDomainID() {
        return domainID;
    }

    @JsonProperty("domainID")
    public void setDomainID(String domainID) {
        this.domainID = domainID;
    }

    @JsonProperty("domainList")
    public List<DomainList> getDomainList() {
        return domainList;
    }

    @JsonProperty("domainList")
    public void setDomainList(List<DomainList> domainList) {
        this.domainList = domainList;
    }

    @JsonProperty("domainName")
    public String getDomainName() {
        return domainName;
    }

    @JsonProperty("domainName")
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @JsonProperty("domainStatus")
    public String getDomainStatus() {
        return domainStatus;
    }

    @JsonProperty("domainStatus")
    public void setDomainStatus(String domainStatus) {
        this.domainStatus = domainStatus;
    }

    @JsonProperty("domainTypeCode")
    public String getDomainTypeCode() {
        return domainTypeCode;
    }

    @JsonProperty("domainTypeCode")
    public void setDomainTypeCode(String domainTypeCode) {
        this.domainTypeCode = domainTypeCode;
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

    @JsonProperty("empCode")
    public String getEmpCode() {
        return empCode;
    }

    @JsonProperty("empCode")
    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    @JsonProperty("externalCode")
    public String getExternalCode() {
        return externalCode;
    }

    @JsonProperty("externalCode")
    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    @JsonProperty("fax")
    public String getFax() {
        return fax;
    }

    @JsonProperty("fax")
    public void setFax(String fax) {
        this.fax = fax;
    }

    @JsonProperty("firstExternalUserModify")
    public boolean isFirstExternalUserModify() {
        return firstExternalUserModify;
    }

    @JsonProperty("firstExternalUserModify")
    public void setFirstExternalUserModify(boolean firstExternalUserModify) {
        this.firstExternalUserModify = firstExternalUserModify;
    }

    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("fromCategoryCode")
    public String getFromCategoryCode() {
        return fromCategoryCode;
    }

    @JsonProperty("fromCategoryCode")
    public void setFromCategoryCode(String fromCategoryCode) {
        this.fromCategoryCode = fromCategoryCode;
    }

    @JsonProperty("fromTime")
    public String getFromTime() {
        return fromTime;
    }

    @JsonProperty("fromTime")
    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    @JsonProperty("fromUserName")
    public String getFromUserName() {
        return fromUserName;
    }

    @JsonProperty("fromUserName")
    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
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
    public String getFxedInfoStr() {
        return fxedInfoStr;
    }

    @JsonProperty("fxedInfoStr")
    public void setFxedInfoStr(String fxedInfoStr) {
        this.fxedInfoStr = fxedInfoStr;
    }

    @JsonProperty("gateway")
    public String getGateway() {
        return gateway;
    }

    @JsonProperty("gateway")
    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    @JsonProperty("geographicalAreaList")
    public List<GeographicalAreaList> getGeographicalAreaList() {
        return geographicalAreaList;
    }

    @JsonProperty("geographicalAreaList")
    public void setGeographicalAreaList(List<GeographicalAreaList> geographicalAreaList) {
        this.geographicalAreaList = geographicalAreaList;
    }

    @JsonProperty("geographicalCode")
    public String getGeographicalCode() {
        return geographicalCode;
    }

    @JsonProperty("geographicalCode")
    public void setGeographicalCode(String geographicalCode) {
        this.geographicalCode = geographicalCode;
    }

    @JsonProperty("geographicalCodeArray")
    public List<String> getGeographicalCodeArray() {
        return geographicalCodeArray;
    }

    @JsonProperty("geographicalCodeArray")
    public void setGeographicalCodeArray(List<String> geographicalCodeArray) {
        this.geographicalCodeArray = geographicalCodeArray;
    }

    @JsonProperty("geographicalCodeStatus")
    public String getGeographicalCodeStatus() {
        return geographicalCodeStatus;
    }

    @JsonProperty("geographicalCodeStatus")
    public void setGeographicalCodeStatus(String geographicalCodeStatus) {
        this.geographicalCodeStatus = geographicalCodeStatus;
    }

    @JsonProperty("geographicalCodeforNewuser")
    public String getGeographicalCodeforNewuser() {
        return geographicalCodeforNewuser;
    }

    @JsonProperty("geographicalCodeforNewuser")
    public void setGeographicalCodeforNewuser(String geographicalCodeforNewuser) {
        this.geographicalCodeforNewuser = geographicalCodeforNewuser;
    }

    @JsonProperty("geographicalDesc")
    public String getGeographicalDesc() {
        return geographicalDesc;
    }

    @JsonProperty("geographicalDesc")
    public void setGeographicalDesc(String geographicalDesc) {
        this.geographicalDesc = geographicalDesc;
    }

    @JsonProperty("geographicalList")
    public List<GeographicalList> getGeographicalList() {
        return geographicalList;
    }

    @JsonProperty("geographicalList")
    public void setGeographicalList(List<GeographicalList> geographicalList) {
        this.geographicalList = geographicalList;
    }

    @JsonProperty("groupRoleCode")
    public String getGroupRoleCode() {
        return groupRoleCode;
    }

    @JsonProperty("groupRoleCode")
    public void setGroupRoleCode(String groupRoleCode) {
        this.groupRoleCode = groupRoleCode;
    }

    @JsonProperty("groupRoleFlag")
    public String getGroupRoleFlag() {
        return groupRoleFlag;
    }

    @JsonProperty("groupRoleFlag")
    public void setGroupRoleFlag(String groupRoleFlag) {
        this.groupRoleFlag = groupRoleFlag;
    }

    @JsonProperty("grphDomainTypeName")
    public String getGrphDomainTypeName() {
        return grphDomainTypeName;
    }

    @JsonProperty("grphDomainTypeName")
    public void setGrphDomainTypeName(String grphDomainTypeName) {
        this.grphDomainTypeName = grphDomainTypeName;
    }

    @JsonProperty("imei")
    public String getImei() {
        return imei;
    }

    @JsonProperty("imei")
    public void setImei(String imei) {
        this.imei = imei;
    }

    @JsonProperty("inSuspend")
    public String getInSuspend() {
        return inSuspend;
    }

    @JsonProperty("inSuspend")
    public void setInSuspend(String inSuspend) {
        this.inSuspend = inSuspend;
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

    @JsonProperty("info11")
    public String getInfo11() {
        return info11;
    }

    @JsonProperty("info11")
    public void setInfo11(String info11) {
        this.info11 = info11;
    }

    @JsonProperty("info12")
    public String getInfo12() {
        return info12;
    }

    @JsonProperty("info12")
    public void setInfo12(String info12) {
        this.info12 = info12;
    }

    @JsonProperty("info13")
    public String getInfo13() {
        return info13;
    }

    @JsonProperty("info13")
    public void setInfo13(String info13) {
        this.info13 = info13;
    }

    @JsonProperty("info14")
    public String getInfo14() {
        return info14;
    }

    @JsonProperty("info14")
    public void setInfo14(String info14) {
        this.info14 = info14;
    }

    @JsonProperty("info15")
    public String getInfo15() {
        return info15;
    }

    @JsonProperty("info15")
    public void setInfo15(String info15) {
        this.info15 = info15;
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

    @JsonProperty("invalidPasswordCount")
    public int getInvalidPasswordCount() {
        return invalidPasswordCount;
    }

    @JsonProperty("invalidPasswordCount")
    public void setInvalidPasswordCount(int invalidPasswordCount) {
        this.invalidPasswordCount = invalidPasswordCount;
    }

    @JsonProperty("invalidPinCount")
    public int getInvalidPinCount() {
        return invalidPinCount;
    }

    @JsonProperty("invalidPinCount")
    public void setInvalidPinCount(int invalidPinCount) {
        this.invalidPinCount = invalidPinCount;
    }

    @JsonProperty("isSerAssignChnlAdm")
    public boolean isIsSerAssignChnlAdm() {
        return isSerAssignChnlAdm;
    }

    @JsonProperty("isSerAssignChnlAdm")
    public void setIsSerAssignChnlAdm(boolean isSerAssignChnlAdm) {
        this.isSerAssignChnlAdm = isSerAssignChnlAdm;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
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

    @JsonProperty("lastLoginOn")
    public String getLastLoginOn() {
        return lastLoginOn;
    }

    @JsonProperty("lastLoginOn")
    public void setLastLoginOn(String lastLoginOn) {
        this.lastLoginOn = lastLoginOn;
    }

    @JsonProperty("lastModified")
    public int getLastModified() {
        return lastModified;
    }

    @JsonProperty("lastModified")
    public void setLastModified(int lastModified) {
        this.lastModified = lastModified;
    }

    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("lastSosProductCode")
    public String getLastSosProductCode() {
        return lastSosProductCode;
    }

    @JsonProperty("lastSosProductCode")
    public void setLastSosProductCode(String lastSosProductCode) {
        this.lastSosProductCode = lastSosProductCode;
    }

    @JsonProperty("lastSosStatus")
    public String getLastSosStatus() {
        return lastSosStatus;
    }

    @JsonProperty("lastSosStatus")
    public void setLastSosStatus(String lastSosStatus) {
        this.lastSosStatus = lastSosStatus;
    }

    @JsonProperty("lastSosTransactionId")
    public String getLastSosTransactionId() {
        return lastSosTransactionId;
    }

    @JsonProperty("lastSosTransactionId")
    public void setLastSosTransactionId(String lastSosTransactionId) {
        this.lastSosTransactionId = lastSosTransactionId;
    }

    @JsonProperty("latitude")
    public String getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("level1ApprovedBy")
    public String getLevel1ApprovedBy() {
        return level1ApprovedBy;
    }

    @JsonProperty("level1ApprovedBy")
    public void setLevel1ApprovedBy(String level1ApprovedBy) {
        this.level1ApprovedBy = level1ApprovedBy;
    }

    @JsonProperty("level1ApprovedOn")
    public String getLevel1ApprovedOn() {
        return level1ApprovedOn;
    }

    @JsonProperty("level1ApprovedOn")
    public void setLevel1ApprovedOn(String level1ApprovedOn) {
        this.level1ApprovedOn = level1ApprovedOn;
    }

    @JsonProperty("level2ApprovedBy")
    public String getLevel2ApprovedBy() {
        return level2ApprovedBy;
    }

    @JsonProperty("level2ApprovedBy")
    public void setLevel2ApprovedBy(String level2ApprovedBy) {
        this.level2ApprovedBy = level2ApprovedBy;
    }

    @JsonProperty("level2ApprovedOn")
    public String getLevel2ApprovedOn() {
        return level2ApprovedOn;
    }

    @JsonProperty("level2ApprovedOn")
    public void setLevel2ApprovedOn(String level2ApprovedOn) {
        this.level2ApprovedOn = level2ApprovedOn;
    }

    @JsonProperty("lmsProfile")
    public String getLmsProfile() {
        return lmsProfile;
    }

    @JsonProperty("lmsProfile")
    public void setLmsProfile(String lmsProfile) {
        this.lmsProfile = lmsProfile;
    }

    @JsonProperty("lmsProfileId")
    public String getLmsProfileId() {
        return lmsProfileId;
    }

    @JsonProperty("lmsProfileId")
    public void setLmsProfileId(String lmsProfileId) {
        this.lmsProfileId = lmsProfileId;
    }

    @JsonProperty("loggerMessage")
    public String getLoggerMessage() {
        return loggerMessage;
    }

    @JsonProperty("loggerMessage")
    public void setLoggerMessage(String loggerMessage) {
        this.loggerMessage = loggerMessage;
    }

    @JsonProperty("loginID")
    public String getLoginID() {
        return loginID;
    }

    @JsonProperty("loginID")
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    @JsonProperty("loginTime")
    public String getLoginTime() {
        return loginTime;
    }

    @JsonProperty("loginTime")
    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    @JsonProperty("longitude")
    public String getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("lowBalAlertAllow")
    public String getLowBalAlertAllow() {
        return lowBalAlertAllow;
    }

    @JsonProperty("lowBalAlertAllow")
    public void setLowBalAlertAllow(String lowBalAlertAllow) {
        this.lowBalAlertAllow = lowBalAlertAllow;
    }

    @JsonProperty("lrAllowed")
    public String getLrAllowed() {
        return lrAllowed;
    }

    @JsonProperty("lrAllowed")
    public void setLrAllowed(String lrAllowed) {
        this.lrAllowed = lrAllowed;
    }

    @JsonProperty("lrMaxAmount")
    public int getLrMaxAmount() {
        return lrMaxAmount;
    }

    @JsonProperty("lrMaxAmount")
    public void setLrMaxAmount(int lrMaxAmount) {
        this.lrMaxAmount = lrMaxAmount;
    }

    @JsonProperty("lrTransferAmount")
    public int getLrTransferAmount() {
        return lrTransferAmount;
    }

    @JsonProperty("lrTransferAmount")
    public void setLrTransferAmount(int lrTransferAmount) {
        this.lrTransferAmount = lrTransferAmount;
    }

    @JsonProperty("maxTxnAmount")
    public int getMaxTxnAmount() {
        return maxTxnAmount;
    }

    @JsonProperty("maxTxnAmount")
    public void setMaxTxnAmount(int maxTxnAmount) {
        this.maxTxnAmount = maxTxnAmount;
    }

    @JsonProperty("maxUserLevel")
    public int getMaxUserLevel() {
        return maxUserLevel;
    }

    @JsonProperty("maxUserLevel")
    public void setMaxUserLevel(int maxUserLevel) {
        this.maxUserLevel = maxUserLevel;
    }

    @JsonProperty("mcommerceServiceAllow")
    public String getMcommerceServiceAllow() {
        return mcommerceServiceAllow;
    }

    @JsonProperty("mcommerceServiceAllow")
    public void setMcommerceServiceAllow(String mcommerceServiceAllow) {
        this.mcommerceServiceAllow = mcommerceServiceAllow;
    }

    @JsonProperty("menuItemList")
    public List<MenuItemList> getMenuItemList() {
        return menuItemList;
    }

    @JsonProperty("menuItemList")
    public void setMenuItemList(List<MenuItemList> menuItemList) {
        this.menuItemList = menuItemList;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
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

    @JsonProperty("moduleCodeString")
    public String getModuleCodeString() {
        return moduleCodeString;
    }

    @JsonProperty("moduleCodeString")
    public void setModuleCodeString(String moduleCodeString) {
        this.moduleCodeString = moduleCodeString;
    }

    @JsonProperty("monthlyTransAmt")
    public int getMonthlyTransAmt() {
        return monthlyTransAmt;
    }

    @JsonProperty("monthlyTransAmt")
    public void setMonthlyTransAmt(int monthlyTransAmt) {
        this.monthlyTransAmt = monthlyTransAmt;
    }

    @JsonProperty("mpayProfileID")
    public String getMpayProfileID() {
        return mpayProfileID;
    }

    @JsonProperty("mpayProfileID")
    public void setMpayProfileID(String mpayProfileID) {
        this.mpayProfileID = mpayProfileID;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("msisdnList")
    public List<MsisdnList> getMsisdnList() {
        return msisdnList;
    }

    @JsonProperty("msisdnList")
    public void setMsisdnList(List<MsisdnList> msisdnList) {
        this.msisdnList = msisdnList;
    }

    @JsonProperty("msisdnPrefix")
    public String getMsisdnPrefix() {
        return msisdnPrefix;
    }

    @JsonProperty("msisdnPrefix")
    public void setMsisdnPrefix(String msisdnPrefix) {
        this.msisdnPrefix = msisdnPrefix;
    }

    @JsonProperty("multipleMsisdnlist")
    public String getMultipleMsisdnlist() {
        return multipleMsisdnlist;
    }

    @JsonProperty("multipleMsisdnlist")
    public void setMultipleMsisdnlist(String multipleMsisdnlist) {
        this.multipleMsisdnlist = multipleMsisdnlist;
    }

    @JsonProperty("nameAndId")
    public String getNameAndId() {
        return nameAndId;
    }

    @JsonProperty("nameAndId")
    public void setNameAndId(String nameAndId) {
        this.nameAndId = nameAndId;
    }

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("networkID")
    public String getNetworkID() {
        return networkID;
    }

    @JsonProperty("networkID")
    public void setNetworkID(String networkID) {
        this.networkID = networkID;
    }

    @JsonProperty("networkList")
    public List<NetworkList> getNetworkList() {
        return networkList;
    }

    @JsonProperty("networkList")
    public void setNetworkList(List<NetworkList> networkList) {
        this.networkList = networkList;
    }

    @JsonProperty("networkName")
    public String getNetworkName() {
        return networkName;
    }

    @JsonProperty("networkName")
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    @JsonProperty("networkNamewithNetworkCode")
    public String getNetworkNamewithNetworkCode() {
        return networkNamewithNetworkCode;
    }

    @JsonProperty("networkNamewithNetworkCode")
    public void setNetworkNamewithNetworkCode(String networkNamewithNetworkCode) {
        this.networkNamewithNetworkCode = networkNamewithNetworkCode;
    }

    @JsonProperty("networkStatus")
    public String getNetworkStatus() {
        return networkStatus;
    }

    @JsonProperty("networkStatus")
    public void setNetworkStatus(String networkStatus) {
        this.networkStatus = networkStatus;
    }

    @JsonProperty("oldLastLoginOn")
    public String getOldLastLoginOn() {
        return oldLastLoginOn;
    }

    @JsonProperty("oldLastLoginOn")
    public void setOldLastLoginOn(String oldLastLoginOn) {
        this.oldLastLoginOn = oldLastLoginOn;
    }

    @JsonProperty("optInOutStatus")
    public String getOptInOutStatus() {
        return optInOutStatus;
    }

    @JsonProperty("optInOutStatus")
    public void setOptInOutStatus(String optInOutStatus) {
        this.optInOutStatus = optInOutStatus;
    }

    @JsonProperty("otfCount")
    public int getOtfCount() {
        return otfCount;
    }

    @JsonProperty("otfCount")
    public void setOtfCount(int otfCount) {
        this.otfCount = otfCount;
    }

    @JsonProperty("otfValue")
    public int getOtfValue() {
        return otfValue;
    }

    @JsonProperty("otfValue")
    public void setOtfValue(int otfValue) {
        this.otfValue = otfValue;
    }

    @JsonProperty("othCommSetId")
    public String getOthCommSetId() {
        return othCommSetId;
    }

    @JsonProperty("othCommSetId")
    public void setOthCommSetId(String othCommSetId) {
        this.othCommSetId = othCommSetId;
    }

    @JsonProperty("otp")
    public String getOtp() {
        return otp;
    }

    @JsonProperty("otp")
    public void setOtp(String otp) {
        this.otp = otp;
    }

    @JsonProperty("otpInvalidCount")
    public int getOtpInvalidCount() {
        return otpInvalidCount;
    }

    @JsonProperty("otpInvalidCount")
    public void setOtpInvalidCount(int otpInvalidCount) {
        this.otpInvalidCount = otpInvalidCount;
    }

    @JsonProperty("otpModifiedOn")
    public String getOtpModifiedOn() {
        return otpModifiedOn;
    }

    @JsonProperty("otpModifiedOn")
    public void setOtpModifiedOn(String otpModifiedOn) {
        this.otpModifiedOn = otpModifiedOn;
    }

    @JsonProperty("otpvalidated")
    public boolean isOtpvalidated() {
        return otpvalidated;
    }

    @JsonProperty("otpvalidated")
    public void setOtpvalidated(boolean otpvalidated) {
        this.otpvalidated = otpvalidated;
    }

    @JsonProperty("outSuspened")
    public String getOutSuspened() {
        return outSuspened;
    }

    @JsonProperty("outSuspened")
    public void setOutSuspened(String outSuspened) {
        this.outSuspened = outSuspened;
    }

    @JsonProperty("outletCode")
    public String getOutletCode() {
        return outletCode;
    }

    @JsonProperty("outletCode")
    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }

    @JsonProperty("ownerCategoryName")
    public String getOwnerCategoryName() {
        return ownerCategoryName;
    }

    @JsonProperty("ownerCategoryName")
    public void setOwnerCategoryName(String ownerCategoryName) {
        this.ownerCategoryName = ownerCategoryName;
    }

    @JsonProperty("ownerCompany")
    public String getOwnerCompany() {
        return ownerCompany;
    }

    @JsonProperty("ownerCompany")
    public void setOwnerCompany(String ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    @JsonProperty("ownerID")
    public String getOwnerID() {
        return ownerID;
    }

    @JsonProperty("ownerID")
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
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

    @JsonProperty("p2pMisFromDate")
    public String getP2pMisFromDate() {
        return p2pMisFromDate;
    }

    @JsonProperty("p2pMisFromDate")
    public void setP2pMisFromDate(String p2pMisFromDate) {
        this.p2pMisFromDate = p2pMisFromDate;
    }

    @JsonProperty("p2pMisToDate")
    public String getP2pMisToDate() {
        return p2pMisToDate;
    }

    @JsonProperty("p2pMisToDate")
    public void setP2pMisToDate(String p2pMisToDate) {
        this.p2pMisToDate = p2pMisToDate;
    }

    @JsonProperty("pageCodeString")
    public String getPageCodeString() {
        return pageCodeString;
    }

    @JsonProperty("pageCodeString")
    public void setPageCodeString(String pageCodeString) {
        this.pageCodeString = pageCodeString;
    }

    @JsonProperty("parentCategoryName")
    public String getParentCategoryName() {
        return parentCategoryName;
    }

    @JsonProperty("parentCategoryName")
    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    @JsonProperty("parentGeographyCode")
    public String getParentGeographyCode() {
        return parentGeographyCode;
    }

    @JsonProperty("parentGeographyCode")
    public void setParentGeographyCode(String parentGeographyCode) {
        this.parentGeographyCode = parentGeographyCode;
    }

    @JsonProperty("parentID")
    public String getParentID() {
        return parentID;
    }

    @JsonProperty("parentID")
    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    @JsonProperty("parentLocale")
    public ParentLocale getParentLocale() {
        return parentLocale;
    }

    @JsonProperty("parentLocale")
    public void setParentLocale(ParentLocale parentLocale) {
        this.parentLocale = parentLocale;
    }

    @JsonProperty("parentLoginID")
    public String getParentLoginID() {
        return parentLoginID;
    }

    @JsonProperty("parentLoginID")
    public void setParentLoginID(String parentLoginID) {
        this.parentLoginID = parentLoginID;
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

    @JsonProperty("parentStatus")
    public String getParentStatus() {
        return parentStatus;
    }

    @JsonProperty("parentStatus")
    public void setParentStatus(String parentStatus) {
        this.parentStatus = parentStatus;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("passwordCountUpdatedOn")
    public String getPasswordCountUpdatedOn() {
        return passwordCountUpdatedOn;
    }

    @JsonProperty("passwordCountUpdatedOn")
    public void setPasswordCountUpdatedOn(String passwordCountUpdatedOn) {
        this.passwordCountUpdatedOn = passwordCountUpdatedOn;
    }

    @JsonProperty("passwordModifiedOn")
    public String getPasswordModifiedOn() {
        return passwordModifiedOn;
    }

    @JsonProperty("passwordModifiedOn")
    public void setPasswordModifiedOn(String passwordModifiedOn) {
        this.passwordModifiedOn = passwordModifiedOn;
    }

    @JsonProperty("passwordModifyFlag")
    public boolean isPasswordModifyFlag() {
        return passwordModifyFlag;
    }

    @JsonProperty("passwordModifyFlag")
    public void setPasswordModifyFlag(boolean passwordModifyFlag) {
        this.passwordModifyFlag = passwordModifyFlag;
    }

    @JsonProperty("passwordReset")
    public String getPasswordReset() {
        return passwordReset;
    }

    @JsonProperty("passwordReset")
    public void setPasswordReset(String passwordReset) {
        this.passwordReset = passwordReset;
    }

    @JsonProperty("paymentType")
    public String getPaymentType() {
        return paymentType;
    }

    @JsonProperty("paymentType")
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @JsonProperty("paymentTypes")
    public String getPaymentTypes() {
        return paymentTypes;
    }

    @JsonProperty("paymentTypes")
    public void setPaymentTypes(String paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    @JsonProperty("paymentTypesList")
    public List<PaymentTypesList> getPaymentTypesList() {
        return paymentTypesList;
    }

    @JsonProperty("paymentTypesList")
    public void setPaymentTypesList(List<PaymentTypesList> paymentTypesList) {
        this.paymentTypesList = paymentTypesList;
    }

    @JsonProperty("phoneProfile")
    public String getPhoneProfile() {
        return phoneProfile;
    }

    @JsonProperty("phoneProfile")
    public void setPhoneProfile(String phoneProfile) {
        this.phoneProfile = phoneProfile;
    }

    @JsonProperty("pinRequired")
    public String getPinRequired() {
        return pinRequired;
    }

    @JsonProperty("pinRequired")
    public void setPinRequired(String pinRequired) {
        this.pinRequired = pinRequired;
    }

    @JsonProperty("pinReset")
    public String getPinReset() {
        return pinReset;
    }

    @JsonProperty("pinReset")
    public void setPinReset(String pinReset) {
        this.pinReset = pinReset;
    }

    @JsonProperty("prefixId")
    public int getPrefixId() {
        return prefixId;
    }

    @JsonProperty("prefixId")
    public void setPrefixId(int prefixId) {
        this.prefixId = prefixId;
    }

    @JsonProperty("prevBalanceStr")
    public String getPrevBalanceStr() {
        return prevBalanceStr;
    }

    @JsonProperty("prevBalanceStr")
    public void setPrevBalanceStr(String prevBalanceStr) {
        this.prevBalanceStr = prevBalanceStr;
    }

    @JsonProperty("prevCategoryCode")
    public String getPrevCategoryCode() {
        return prevCategoryCode;
    }

    @JsonProperty("prevCategoryCode")
    public void setPrevCategoryCode(String prevCategoryCode) {
        this.prevCategoryCode = prevCategoryCode;
    }

    @JsonProperty("prevParentName")
    public String getPrevParentName() {
        return prevParentName;
    }

    @JsonProperty("prevParentName")
    public void setPrevParentName(String prevParentName) {
        this.prevParentName = prevParentName;
    }

    @JsonProperty("prevUserId")
    public String getPrevUserId() {
        return prevUserId;
    }

    @JsonProperty("prevUserId")
    public void setPrevUserId(String prevUserId) {
        this.prevUserId = prevUserId;
    }

    @JsonProperty("prevUserName")
    public String getPrevUserName() {
        return prevUserName;
    }

    @JsonProperty("prevUserName")
    public void setPrevUserName(String prevUserName) {
        this.prevUserName = prevUserName;
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

    @JsonProperty("previousBalance")
    public int getPreviousBalance() {
        return previousBalance;
    }

    @JsonProperty("previousBalance")
    public void setPreviousBalance(int previousBalance) {
        this.previousBalance = previousBalance;
    }

    @JsonProperty("previousStatus")
    public String getPreviousStatus() {
        return previousStatus;
    }

    @JsonProperty("previousStatus")
    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    @JsonProperty("primaryMsisdn")
    public String getPrimaryMsisdn() {
        return primaryMsisdn;
    }

    @JsonProperty("primaryMsisdn")
    public void setPrimaryMsisdn(String primaryMsisdn) {
        this.primaryMsisdn = primaryMsisdn;
    }

    @JsonProperty("primaryMsisdnPin")
    public String getPrimaryMsisdnPin() {
        return primaryMsisdnPin;
    }

    @JsonProperty("primaryMsisdnPin")
    public void setPrimaryMsisdnPin(String primaryMsisdnPin) {
        this.primaryMsisdnPin = primaryMsisdnPin;
    }

    @JsonProperty("productCode")
    public String getProductCode() {
        return productCode;
    }

    @JsonProperty("productCode")
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @JsonProperty("productCodes")
    public List<String> getProductCodes() {
        return productCodes;
    }

    @JsonProperty("productCodes")
    public void setProductCodes(List<String> productCodes) {
        this.productCodes = productCodes;
    }

    @JsonProperty("productsList")
    public List<ProductsList> getProductsList() {
        return productsList;
    }

    @JsonProperty("productsList")
    public void setProductsList(List<ProductsList> productsList) {
        this.productsList = productsList;
    }

    @JsonProperty("recordNumber")
    public String getRecordNumber() {
        return recordNumber;
    }

    @JsonProperty("recordNumber")
    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    @JsonProperty("referenceID")
    public String getReferenceID() {
        return referenceID;
    }

    @JsonProperty("referenceID")
    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("remoteAddress")
    public String getRemoteAddress() {
        return remoteAddress;
    }

    @JsonProperty("remoteAddress")
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @JsonProperty("reportHeaderName")
    public String getReportHeaderName() {
        return reportHeaderName;
    }

    @JsonProperty("reportHeaderName")
    public void setReportHeaderName(String reportHeaderName) {
        this.reportHeaderName = reportHeaderName;
    }

    @JsonProperty("requestType")
    public String getRequestType() {
        return requestType;
    }

    @JsonProperty("requestType")
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    @JsonProperty("requetedByUserName")
    public String getRequetedByUserName() {
        return requetedByUserName;
    }

    @JsonProperty("requetedByUserName")
    public void setRequetedByUserName(String requetedByUserName) {
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

    @JsonProperty("resetPinOTPMessage")
    public ResetPinOTPMessage getResetPinOTPMessage() {
        return resetPinOTPMessage;
    }

    @JsonProperty("resetPinOTPMessage")
    public void setResetPinOTPMessage(ResetPinOTPMessage resetPinOTPMessage) {
        this.resetPinOTPMessage = resetPinOTPMessage;
    }

    @JsonProperty("restrictedMsisdnAllow")
    public String getRestrictedMsisdnAllow() {
        return restrictedMsisdnAllow;
    }

    @JsonProperty("restrictedMsisdnAllow")
    public void setRestrictedMsisdnAllow(String restrictedMsisdnAllow) {
        this.restrictedMsisdnAllow = restrictedMsisdnAllow;
    }

    @JsonProperty("returnFlag")
    public boolean isReturnFlag() {
        return returnFlag;
    }

    @JsonProperty("returnFlag")
    public void setReturnFlag(boolean returnFlag) {
        this.returnFlag = returnFlag;
    }

    @JsonProperty("roleFlag")
    public List<String> getRoleFlag() {
        return roleFlag;
    }

    @JsonProperty("roleFlag")
    public void setRoleFlag(List<String> roleFlag) {
        this.roleFlag = roleFlag;
    }

    @JsonProperty("roleType")
    public String getRoleType() {
        return roleType;
    }

    @JsonProperty("roleType")
    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    @JsonProperty("rolesMap")
    public RolesMap getRolesMap() {
        return rolesMap;
    }

    @JsonProperty("rolesMap")
    public void setRolesMap(RolesMap rolesMap) {
        this.rolesMap = rolesMap;
    }

    @JsonProperty("rolesMapSelectedCount")
    public int getRolesMapSelectedCount() {
        return rolesMapSelectedCount;
    }

    @JsonProperty("rolesMapSelectedCount")
    public void setRolesMapSelectedCount(int rolesMapSelectedCount) {
        this.rolesMapSelectedCount = rolesMapSelectedCount;
    }

    @JsonProperty("rsaAllowed")
    public boolean isRsaAllowed() {
        return rsaAllowed;
    }

    @JsonProperty("rsaAllowed")
    public void setRsaAllowed(boolean rsaAllowed) {
        this.rsaAllowed = rsaAllowed;
    }

    @JsonProperty("rsaFlag")
    public String getRsaFlag() {
        return rsaFlag;
    }

    @JsonProperty("rsaFlag")
    public void setRsaFlag(String rsaFlag) {
        this.rsaFlag = rsaFlag;
    }

    @JsonProperty("rsaRequired")
    public boolean isRsaRequired() {
        return rsaRequired;
    }

    @JsonProperty("rsaRequired")
    public void setRsaRequired(boolean rsaRequired) {
        this.rsaRequired = rsaRequired;
    }

    @JsonProperty("rsavalidated")
    public boolean isRsavalidated() {
        return rsavalidated;
    }

    @JsonProperty("rsavalidated")
    public void setRsavalidated(boolean rsavalidated) {
        this.rsavalidated = rsavalidated;
    }

    @JsonProperty("securityAnswer")
    public String getSecurityAnswer() {
        return securityAnswer;
    }

    @JsonProperty("securityAnswer")
    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    @JsonProperty("segmentList")
    public List<SegmentList> getSegmentList() {
        return segmentList;
    }

    @JsonProperty("segmentList")
    public void setSegmentList(List<SegmentList> segmentList) {
        this.segmentList = segmentList;
    }

    @JsonProperty("segments")
    public String getSegments() {
        return segments;
    }

    @JsonProperty("segments")
    public void setSegments(String segments) {
        this.segments = segments;
    }

    @JsonProperty("serviceList")
    public List<ServiceList> getServiceList() {
        return serviceList;
    }

    @JsonProperty("serviceList")
    public void setServiceList(List<ServiceList> serviceList) {
        this.serviceList = serviceList;
    }

    @JsonProperty("serviceTypeList")
    public List<String> getServiceTypeList() {
        return serviceTypeList;
    }

    @JsonProperty("serviceTypeList")
    public void setServiceTypeList(List<String> serviceTypeList) {
        this.serviceTypeList = serviceTypeList;
    }

    @JsonProperty("serviceTypes")
    public String getServiceTypes() {
        return serviceTypes;
    }

    @JsonProperty("serviceTypes")
    public void setServiceTypes(String serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    @JsonProperty("servicesList")
    public List<ServicesList> getServicesList() {
        return servicesList;
    }

    @JsonProperty("servicesList")
    public void setServicesList(List<ServicesList> servicesList) {
        this.servicesList = servicesList;
    }

    @JsonProperty("servicesTypes")
    public List<String> getServicesTypes() {
        return servicesTypes;
    }

    @JsonProperty("servicesTypes")
    public void setServicesTypes(List<String> servicesTypes) {
        this.servicesTypes = servicesTypes;
    }

    @JsonProperty("sessionInfoVO")
    public SessionInfoVO getSessionInfoVO() {
        return sessionInfoVO;
    }

    @JsonProperty("sessionInfoVO")
    public void setSessionInfoVO(SessionInfoVO sessionInfoVO) {
        this.sessionInfoVO = sessionInfoVO;
    }

    @JsonProperty("shortName")
    public String getShortName() {
        return shortName;
    }

    @JsonProperty("shortName")
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @JsonProperty("showPassword")
    public String getShowPassword() {
        return showPassword;
    }

    @JsonProperty("showPassword")
    public void setShowPassword(String showPassword) {
        this.showPassword = showPassword;
    }

    @JsonProperty("smsMSisdn")
    public String getSmsMSisdn() {
        return smsMSisdn;
    }

    @JsonProperty("smsMSisdn")
    public void setSmsMSisdn(String smsMSisdn) {
        this.smsMSisdn = smsMSisdn;
    }

    @JsonProperty("smsPin")
    public String getSmsPin() {
        return smsPin;
    }

    @JsonProperty("smsPin")
    public void setSmsPin(String smsPin) {
        this.smsPin = smsPin;
    }

    @JsonProperty("sosAllowed")
    public String getSosAllowed() {
        return sosAllowed;
    }

    @JsonProperty("sosAllowed")
    public void setSosAllowed(String sosAllowed) {
        this.sosAllowed = sosAllowed;
    }

    @JsonProperty("sosAllowedAmount")
    public int getSosAllowedAmount() {
        return sosAllowedAmount;
    }

    @JsonProperty("sosAllowedAmount")
    public void setSosAllowedAmount(int sosAllowedAmount) {
        this.sosAllowedAmount = sosAllowedAmount;
    }

    @JsonProperty("sosThresholdLimit")
    public int getSosThresholdLimit() {
        return sosThresholdLimit;
    }

    @JsonProperty("sosThresholdLimit")
    public void setSosThresholdLimit(int sosThresholdLimit) {
        this.sosThresholdLimit = sosThresholdLimit;
    }

    @JsonProperty("ssn")
    public String getSsn() {
        return ssn;
    }

    @JsonProperty("ssn")
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    @JsonProperty("staffUser")
    public boolean isStaffUser() {
        return staffUser;
    }

    @JsonProperty("staffUser")
    public void setStaffUser(boolean staffUser) {
        this.staffUser = staffUser;
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

    @JsonProperty("statusList")
    public List<StatusList> getStatusList() {
        return statusList;
    }

    @JsonProperty("statusList")
    public void setStatusList(List<StatusList> statusList) {
        this.statusList = statusList;
    }

    @JsonProperty("subOutletCode")
    public String getSubOutletCode() {
        return subOutletCode;
    }

    @JsonProperty("subOutletCode")
    public void setSubOutletCode(String subOutletCode) {
        this.subOutletCode = subOutletCode;
    }

    @JsonProperty("suspendedByUserName")
    public String getSuspendedByUserName() {
        return suspendedByUserName;
    }

    @JsonProperty("suspendedByUserName")
    public void setSuspendedByUserName(String suspendedByUserName) {
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

    @JsonProperty("switchID")
    public String getSwitchID() {
        return switchID;
    }

    @JsonProperty("switchID")
    public void setSwitchID(String switchID) {
        this.switchID = switchID;
    }

    @JsonProperty("toCategoryCode")
    public String getToCategoryCode() {
        return toCategoryCode;
    }

    @JsonProperty("toCategoryCode")
    public void setToCategoryCode(String toCategoryCode) {
        this.toCategoryCode = toCategoryCode;
    }

    @JsonProperty("toTime")
    public String getToTime() {
        return toTime;
    }

    @JsonProperty("toTime")
    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    @JsonProperty("toUserName")
    public String getToUserName() {
        return toUserName;
    }

    @JsonProperty("toUserName")
    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    @JsonProperty("tokenLastUsedDate")
    public String getTokenLastUsedDate() {
        return tokenLastUsedDate;
    }

    @JsonProperty("tokenLastUsedDate")
    public void setTokenLastUsedDate(String tokenLastUsedDate) {
        this.tokenLastUsedDate = tokenLastUsedDate;
    }

    @JsonProperty("trannferRuleTypeId")
    public String getTrannferRuleTypeId() {
        return trannferRuleTypeId;
    }

    @JsonProperty("trannferRuleTypeId")
    public void setTrannferRuleTypeId(String trannferRuleTypeId) {
        this.trannferRuleTypeId = trannferRuleTypeId;
    }

    @JsonProperty("transferCategory")
    public String getTransferCategory() {
        return transferCategory;
    }

    @JsonProperty("transferCategory")
    public void setTransferCategory(String transferCategory) {
        this.transferCategory = transferCategory;
    }

    @JsonProperty("transferProfileID")
    public String getTransferProfileID() {
        return transferProfileID;
    }

    @JsonProperty("transferProfileID")
    public void setTransferProfileID(String transferProfileID) {
        this.transferProfileID = transferProfileID;
    }

    @JsonProperty("transferProfileName")
    public String getTransferProfileName() {
        return transferProfileName;
    }

    @JsonProperty("transferProfileName")
    public void setTransferProfileName(String transferProfileName) {
        this.transferProfileName = transferProfileName;
    }

    @JsonProperty("transferProfileStatus")
    public String getTransferProfileStatus() {
        return transferProfileStatus;
    }

    @JsonProperty("transferProfileStatus")
    public void setTransferProfileStatus(String transferProfileStatus) {
        this.transferProfileStatus = transferProfileStatus;
    }

    @JsonProperty("transferRuleID")
    public String getTransferRuleID() {
        return transferRuleID;
    }

    @JsonProperty("transferRuleID")
    public void setTransferRuleID(String transferRuleID) {
        this.transferRuleID = transferRuleID;
    }

    @JsonProperty("trnsfrdUserHierhyList")
    public List<TrnsfrdUserHierhyList> getTrnsfrdUserHierhyList() {
        return trnsfrdUserHierhyList;
    }

    @JsonProperty("trnsfrdUserHierhyList")
    public void setTrnsfrdUserHierhyList(List<TrnsfrdUserHierhyList> trnsfrdUserHierhyList) {
        this.trnsfrdUserHierhyList = trnsfrdUserHierhyList;
    }

    @JsonProperty("updateSimRequired")
    public boolean isUpdateSimRequired() {
        return updateSimRequired;
    }

    @JsonProperty("updateSimRequired")
    public void setUpdateSimRequired(boolean updateSimRequired) {
        this.updateSimRequired = updateSimRequired;
    }

    @JsonProperty("userBalance")
    public String getUserBalance() {
        return userBalance;
    }

    @JsonProperty("userBalance")
    public void setUserBalance(String userBalance) {
        this.userBalance = userBalance;
    }

    @JsonProperty("userBalanceList")
    public List<UserBalanceList> getUserBalanceList() {
        return userBalanceList;
    }

    @JsonProperty("userBalanceList")
    public void setUserBalanceList(List<UserBalanceList> userBalanceList) {
        this.userBalanceList = userBalanceList;
    }

    @JsonProperty("userChargeGrouptypeCounters")
    public UserChargeGrouptypeCounters getUserChargeGrouptypeCounters() {
        return userChargeGrouptypeCounters;
    }

    @JsonProperty("userChargeGrouptypeCounters")
    public void setUserChargeGrouptypeCounters(UserChargeGrouptypeCounters userChargeGrouptypeCounters) {
        this.userChargeGrouptypeCounters = userChargeGrouptypeCounters;
    }

    @JsonProperty("userCode")
    public String getUserCode() {
        return userCode;
    }

    @JsonProperty("userCode")
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @JsonProperty("userControlGrouptypeCounters")
    public UserControlGrouptypeCounters getUserControlGrouptypeCounters() {
        return userControlGrouptypeCounters;
    }

    @JsonProperty("userControlGrouptypeCounters")
    public void setUserControlGrouptypeCounters(UserControlGrouptypeCounters userControlGrouptypeCounters) {
        this.userControlGrouptypeCounters = userControlGrouptypeCounters;
    }

    @JsonProperty("userGrade")
    public String getUserGrade() {
        return userGrade;
    }

    @JsonProperty("userGrade")
    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }

    @JsonProperty("userGradeName")
    public String getUserGradeName() {
        return userGradeName;
    }

    @JsonProperty("userGradeName")
    public void setUserGradeName(String userGradeName) {
        this.userGradeName = userGradeName;
    }

    @JsonProperty("userID")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @JsonProperty("userIDPrefix")
    public String getUserIDPrefix() {
        return userIDPrefix;
    }

    @JsonProperty("userIDPrefix")
    public void setUserIDPrefix(String userIDPrefix) {
        this.userIDPrefix = userIDPrefix;
    }

    @JsonProperty("userLanguage")
    public String getUserLanguage() {
        return userLanguage;
    }

    @JsonProperty("userLanguage")
    public void setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
    }

    @JsonProperty("userLanguageDesc")
    public String getUserLanguageDesc() {
        return userLanguageDesc;
    }

    @JsonProperty("userLanguageDesc")
    public void setUserLanguageDesc(String userLanguageDesc) {
        this.userLanguageDesc = userLanguageDesc;
    }

    @JsonProperty("userLanguageList")
    public List<UserLanguageList> getUserLanguageList() {
        return userLanguageList;
    }

    @JsonProperty("userLanguageList")
    public void setUserLanguageList(List<UserLanguageList> userLanguageList) {
        this.userLanguageList = userLanguageList;
    }

    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("userNamePrefix")
    public String getUserNamePrefix() {
        return userNamePrefix;
    }

    @JsonProperty("userNamePrefix")
    public void setUserNamePrefix(String userNamePrefix) {
        this.userNamePrefix = userNamePrefix;
    }

    @JsonProperty("userNamePrefixCode")
    public String getUserNamePrefixCode() {
        return userNamePrefixCode;
    }

    @JsonProperty("userNamePrefixCode")
    public void setUserNamePrefixCode(String userNamePrefixCode) {
        this.userNamePrefixCode = userNamePrefixCode;
    }

    @JsonProperty("userNamePrefixDesc")
    public String getUserNamePrefixDesc() {
        return userNamePrefixDesc;
    }

    @JsonProperty("userNamePrefixDesc")
    public void setUserNamePrefixDesc(String userNamePrefixDesc) {
        this.userNamePrefixDesc = userNamePrefixDesc;
    }

    @JsonProperty("userNamePrefixList")
    public List<UserNamePrefixList> getUserNamePrefixList() {
        return userNamePrefixList;
    }

    @JsonProperty("userNamePrefixList")
    public void setUserNamePrefixList(List<UserNamePrefixList> userNamePrefixList) {
        this.userNamePrefixList = userNamePrefixList;
    }

    @JsonProperty("userNameWithCategory")
    public String getUserNameWithCategory() {
        return userNameWithCategory;
    }

    @JsonProperty("userNameWithCategory")
    public void setUserNameWithCategory(String userNameWithCategory) {
        this.userNameWithCategory = userNameWithCategory;
    }

    @JsonProperty("userNamewithLoginId")
    public String getUserNamewithLoginId() {
        return userNamewithLoginId;
    }

    @JsonProperty("userNamewithLoginId")
    public void setUserNamewithLoginId(String userNamewithLoginId) {
        this.userNamewithLoginId = userNamewithLoginId;
    }

    @JsonProperty("userNamewithUserId")
    public String getUserNamewithUserId() {
        return userNamewithUserId;
    }

    @JsonProperty("userNamewithUserId")
    public void setUserNamewithUserId(String userNamewithUserId) {
        this.userNamewithUserId = userNamewithUserId;
    }

    @JsonProperty("userPhoneVO")
    public UserPhoneVO getUserPhoneVO() {
        return userPhoneVO;
    }

    @JsonProperty("userPhoneVO")
    public void setUserPhoneVO(UserPhoneVO userPhoneVO) {
        this.userPhoneVO = userPhoneVO;
    }

    @JsonProperty("userPhonesId")
    public String getUserPhonesId() {
        return userPhonesId;
    }

    @JsonProperty("userPhonesId")
    public void setUserPhonesId(String userPhonesId) {
        this.userPhonesId = userPhonesId;
    }

    @JsonProperty("userProfileID")
    public String getUserProfileID() {
        return userProfileID;
    }

    @JsonProperty("userProfileID")
    public void setUserProfileID(String userProfileID) {
        this.userProfileID = userProfileID;
    }

    @JsonProperty("userType")
    public String getUserType() {
        return userType;
    }

    @JsonProperty("userType")
    public void setUserType(String userType) {
        this.userType = userType;
    }

    @JsonProperty("userlevel")
    public String getUserlevel() {
        return userlevel;
    }

    @JsonProperty("userlevel")
    public void setUserlevel(String userlevel) {
        this.userlevel = userlevel;
    }

    @JsonProperty("usingNewSTK")
    public boolean isUsingNewSTK() {
        return usingNewSTK;
    }

    @JsonProperty("usingNewSTK")
    public void setUsingNewSTK(boolean usingNewSTK) {
        this.usingNewSTK = usingNewSTK;
    }

    @JsonProperty("validRequestURLs")
    public String getValidRequestURLs() {
        return validRequestURLs;
    }

    @JsonProperty("validRequestURLs")
    public void setValidRequestURLs(String validRequestURLs) {
        this.validRequestURLs = validRequestURLs;
    }

    @JsonProperty("validStatus")
    public int getValidStatus() {
        return validStatus;
    }

    @JsonProperty("validStatus")
    public void setValidStatus(int validStatus) {
        this.validStatus = validStatus;
    }

    @JsonProperty("voucherList")
    public List<VoucherList> getVoucherList() {
        return voucherList;
    }

    @JsonProperty("voucherList")
    public void setVoucherList(List<VoucherList> voucherList) {
        this.voucherList = voucherList;
    }

    @JsonProperty("voucherTypes")
    public String getVoucherTypes() {
        return voucherTypes;
    }

    @JsonProperty("voucherTypes")
    public void setVoucherTypes(String voucherTypes) {
        this.voucherTypes = voucherTypes;
    }

    @JsonProperty("webLoginID")
    public String getWebLoginID() {
        return webLoginID;
    }

    @JsonProperty("webLoginID")
    public void setWebLoginID(String webLoginID) {
        this.webLoginID = webLoginID;
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
