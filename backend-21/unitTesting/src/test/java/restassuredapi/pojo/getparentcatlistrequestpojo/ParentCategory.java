package restassuredapi.pojo.getparentcatlistrequestpojo;

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
"lastModifiedTime",
"sequenceNumber",
"parentCategoryCode",
"hierarchyAllowed",
"categoryStatus",
"radioIndex",
"recordCount",
"fixedRoles",
"modifyAllowed",
"agentAllowed",
"categoryType",
"displayAllowed",
"grphDomainType",
"cp2pPayer",
"agentCp2pPayee",
"fixedDomains",
"agentFixedRoles",
"combinedKey",
"trasnferKey",
"agentRoleName",
"maxLoginCount",
"cp2pPayee",
"cp2pWithinList",
"agentCheckArray",
"domainAllowed",
"agentDomainName",
"agentCp2pPayer",
"maxTxnMsisdnInt",
"categoryCode",
"domainName",
"categoryName",
"modifiedOn",
"createdBy",
"createdOn",
"modifiedBy",
"domainTypeCode",
"domainCodeforCategory",
"restrictedMsisdns",
"transferToListOnly",
"txnOutsideHierchy",
"productTypeAllowed",
"agentWebInterfaceAllowed",
"productTypeAssociationAllowed",
"allowedGatewayTypes",
"agentSmsInterfaceAllowed",
"geographicalDomainSeqNo",
"viewOnNetworkBlock",
"grphDomainSequenceNo",
"multipleLoginAllowed",
"agentMaxLoginCount",
"multipleGrphDomains",
"categorySequenceNumber",
"scheduledTransferAllowed",
"unctrlTransferAllowed",
"webInterfaceAllowed",
"agentCategoryType",
"agentGeographicalDomainList",
"agentMaxTxnMsisdn",
"agentRestrictedMsisdns",
"agentRoleTypeList",
"agentScheduledTransferAllowed",
"agentDomainCodeforCategory",
"agentGrphDomainType",
"agentServiceAllowed",
"agentDisplayAllowed",
"agentModifyAllowed",
"agentMultipleGrphDomains",
"agentAllowedFlag",
"agentCategoryCode",
"agentMultipleLoginAllowed",
"agentRolesMapSelected",
"agentAgentAllowed",
"agentGatewayType",
"agentMessageGatewayTypeList",
"agentCategoryStatusList",
"agentCategoryName",
"agentHierarchyAllowed",
"agentOutletsAllowed",
"agentCategoryStatus",
"agentGatewayName",
"agentUserIdPrefix",
"parentOrOwnerRadioValue",
"agentLowBalAlertAllow",
"categoryTypeCode",
"agentUnctrlTransferAllowed",
"agentRechargeByParentOnly",
"agentCp2pWithinList",
"rechargeByParentOnly",
"authenticationType",
"agentParentOrOwnerRadioValue",
"agentViewOnNetworkBlock",
"numberOfCategoryForDomain",
"grphDomainTypeName",
"agentModifiedMessageGatewayTypeList",
"agentProductTypeAssociationAllowed",
"smsInterfaceAllowed",
"lowBalAlertAllow",
"serviceAllowed",
"outletsAllowed",
"userIdPrefix",
"maxTxnMsisdn"
})
@Generated("jsonschema2pojo")
public class ParentCategory {

@JsonProperty("lastModifiedTime")
private String lastModifiedTime;
@JsonProperty("sequenceNumber")
private String sequenceNumber;
@JsonProperty("parentCategoryCode")
private String parentCategoryCode;
@JsonProperty("hierarchyAllowed")
private String hierarchyAllowed;
@JsonProperty("categoryStatus")
private String categoryStatus;
@JsonProperty("radioIndex")
private String radioIndex;
@JsonProperty("recordCount")
private String recordCount;
@JsonProperty("fixedRoles")
private String fixedRoles;
@JsonProperty("modifyAllowed")
private String modifyAllowed;
@JsonProperty("agentAllowed")
private String agentAllowed;
@JsonProperty("categoryType")
private String categoryType;
@JsonProperty("displayAllowed")
private String displayAllowed;
@JsonProperty("grphDomainType")
private String grphDomainType;
@JsonProperty("cp2pPayer")
private String cp2pPayer;
@JsonProperty("agentCp2pPayee")
private String agentCp2pPayee;
@JsonProperty("fixedDomains")
private String fixedDomains;
@JsonProperty("agentFixedRoles")
private String agentFixedRoles;
@JsonProperty("combinedKey")
private String combinedKey;
@JsonProperty("trasnferKey")
private String trasnferKey;
@JsonProperty("agentRoleName")
private String agentRoleName;
@JsonProperty("maxLoginCount")
private String maxLoginCount;
@JsonProperty("cp2pPayee")
private String cp2pPayee;
@JsonProperty("cp2pWithinList")
private String cp2pWithinList;
@JsonProperty("agentCheckArray")
private String agentCheckArray;
@JsonProperty("domainAllowed")
private String domainAllowed;
@JsonProperty("agentDomainName")
private String agentDomainName;
@JsonProperty("agentCp2pPayer")
private String agentCp2pPayer;
@JsonProperty("maxTxnMsisdnInt")
private Integer maxTxnMsisdnInt;
@JsonProperty("categoryCode")
private String categoryCode;
@JsonProperty("domainName")
private String domainName;
@JsonProperty("categoryName")
private String categoryName;
@JsonProperty("modifiedOn")
private String modifiedOn;
@JsonProperty("createdBy")
private String createdBy;
@JsonProperty("createdOn")
private String createdOn;
@JsonProperty("modifiedBy")
private String modifiedBy;
@JsonProperty("domainTypeCode")
private String domainTypeCode;
@JsonProperty("domainCodeforCategory")
private String domainCodeforCategory;
@JsonProperty("restrictedMsisdns")
private String restrictedMsisdns;
@JsonProperty("transferToListOnly")
private String transferToListOnly;
@JsonProperty("txnOutsideHierchy")
private String txnOutsideHierchy;
@JsonProperty("productTypeAllowed")
private String productTypeAllowed;
@JsonProperty("agentWebInterfaceAllowed")
private String agentWebInterfaceAllowed;
@JsonProperty("productTypeAssociationAllowed")
private String productTypeAssociationAllowed;
@JsonProperty("allowedGatewayTypes")
private String allowedGatewayTypes;
@JsonProperty("agentSmsInterfaceAllowed")
private String agentSmsInterfaceAllowed;
@JsonProperty("geographicalDomainSeqNo")
private String geographicalDomainSeqNo;
@JsonProperty("viewOnNetworkBlock")
private String viewOnNetworkBlock;
@JsonProperty("grphDomainSequenceNo")
private String grphDomainSequenceNo;
@JsonProperty("multipleLoginAllowed")
private String multipleLoginAllowed;
@JsonProperty("agentMaxLoginCount")
private String agentMaxLoginCount;
@JsonProperty("multipleGrphDomains")
private String multipleGrphDomains;
@JsonProperty("categorySequenceNumber")
private String categorySequenceNumber;
@JsonProperty("scheduledTransferAllowed")
private String scheduledTransferAllowed;
@JsonProperty("unctrlTransferAllowed")
private String unctrlTransferAllowed;
@JsonProperty("webInterfaceAllowed")
private String webInterfaceAllowed;
@JsonProperty("agentCategoryType")
private String agentCategoryType;
@JsonProperty("agentGeographicalDomainList")
private String agentGeographicalDomainList;
@JsonProperty("agentMaxTxnMsisdn")
private String agentMaxTxnMsisdn;
@JsonProperty("agentRestrictedMsisdns")
private String agentRestrictedMsisdns;
@JsonProperty("agentRoleTypeList")
private String agentRoleTypeList;
@JsonProperty("agentScheduledTransferAllowed")
private String agentScheduledTransferAllowed;
@JsonProperty("agentDomainCodeforCategory")
private String agentDomainCodeforCategory;
@JsonProperty("agentGrphDomainType")
private String agentGrphDomainType;
@JsonProperty("agentServiceAllowed")
private String agentServiceAllowed;
@JsonProperty("agentDisplayAllowed")
private String agentDisplayAllowed;
@JsonProperty("agentModifyAllowed")
private String agentModifyAllowed;
@JsonProperty("agentMultipleGrphDomains")
private String agentMultipleGrphDomains;
@JsonProperty("agentAllowedFlag")
private String agentAllowedFlag;
@JsonProperty("agentCategoryCode")
private String agentCategoryCode;
@JsonProperty("agentMultipleLoginAllowed")
private String agentMultipleLoginAllowed;
@JsonProperty("agentRolesMapSelected")
private String agentRolesMapSelected;
@JsonProperty("agentAgentAllowed")
private String agentAgentAllowed;
@JsonProperty("agentGatewayType")
private String agentGatewayType;
@JsonProperty("agentMessageGatewayTypeList")
private String agentMessageGatewayTypeList;
@JsonProperty("agentCategoryStatusList")
private String agentCategoryStatusList;
@JsonProperty("agentCategoryName")
private String agentCategoryName;
@JsonProperty("agentHierarchyAllowed")
private String agentHierarchyAllowed;
@JsonProperty("agentOutletsAllowed")
private String agentOutletsAllowed;
@JsonProperty("agentCategoryStatus")
private String agentCategoryStatus;
@JsonProperty("agentGatewayName")
private String agentGatewayName;
@JsonProperty("agentUserIdPrefix")
private String agentUserIdPrefix;
@JsonProperty("parentOrOwnerRadioValue")
private String parentOrOwnerRadioValue;
@JsonProperty("agentLowBalAlertAllow")
private String agentLowBalAlertAllow;
@JsonProperty("categoryTypeCode")
private String categoryTypeCode;
@JsonProperty("agentUnctrlTransferAllowed")
private String agentUnctrlTransferAllowed;
@JsonProperty("agentRechargeByParentOnly")
private String agentRechargeByParentOnly;
@JsonProperty("agentCp2pWithinList")
private String agentCp2pWithinList;
@JsonProperty("rechargeByParentOnly")
private String rechargeByParentOnly;
@JsonProperty("authenticationType")
private String authenticationType;
@JsonProperty("agentParentOrOwnerRadioValue")
private String agentParentOrOwnerRadioValue;
@JsonProperty("agentViewOnNetworkBlock")
private String agentViewOnNetworkBlock;
@JsonProperty("numberOfCategoryForDomain")
private String numberOfCategoryForDomain;
@JsonProperty("grphDomainTypeName")
private String grphDomainTypeName;
@JsonProperty("agentModifiedMessageGatewayTypeList")
private String agentModifiedMessageGatewayTypeList;
@JsonProperty("agentProductTypeAssociationAllowed")
private String agentProductTypeAssociationAllowed;
@JsonProperty("smsInterfaceAllowed")
private String smsInterfaceAllowed;
@JsonProperty("lowBalAlertAllow")
private String lowBalAlertAllow;
@JsonProperty("serviceAllowed")
private String serviceAllowed;
@JsonProperty("outletsAllowed")
private String outletsAllowed;
@JsonProperty("userIdPrefix")
private String userIdPrefix;
@JsonProperty("maxTxnMsisdn")
private String maxTxnMsisdn;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("lastModifiedTime")
public String getLastModifiedTime() {
return lastModifiedTime;
}

@JsonProperty("lastModifiedTime")
public void setLastModifiedTime(String lastModifiedTime) {
this.lastModifiedTime = lastModifiedTime;
}

@JsonProperty("sequenceNumber")
public String getSequenceNumber() {
return sequenceNumber;
}

@JsonProperty("sequenceNumber")
public void setSequenceNumber(String sequenceNumber) {
this.sequenceNumber = sequenceNumber;
}

@JsonProperty("parentCategoryCode")
public String getParentCategoryCode() {
return parentCategoryCode;
}

@JsonProperty("parentCategoryCode")
public void setParentCategoryCode(String parentCategoryCode) {
this.parentCategoryCode = parentCategoryCode;
}

@JsonProperty("hierarchyAllowed")
public String getHierarchyAllowed() {
return hierarchyAllowed;
}

@JsonProperty("hierarchyAllowed")
public void setHierarchyAllowed(String hierarchyAllowed) {
this.hierarchyAllowed = hierarchyAllowed;
}

@JsonProperty("categoryStatus")
public String getCategoryStatus() {
return categoryStatus;
}

@JsonProperty("categoryStatus")
public void setCategoryStatus(String categoryStatus) {
this.categoryStatus = categoryStatus;
}

@JsonProperty("radioIndex")
public String getRadioIndex() {
return radioIndex;
}

@JsonProperty("radioIndex")
public void setRadioIndex(String radioIndex) {
this.radioIndex = radioIndex;
}

@JsonProperty("recordCount")
public String getRecordCount() {
return recordCount;
}

@JsonProperty("recordCount")
public void setRecordCount(String recordCount) {
this.recordCount = recordCount;
}

@JsonProperty("fixedRoles")
public String getFixedRoles() {
return fixedRoles;
}

@JsonProperty("fixedRoles")
public void setFixedRoles(String fixedRoles) {
this.fixedRoles = fixedRoles;
}

@JsonProperty("modifyAllowed")
public String getModifyAllowed() {
return modifyAllowed;
}

@JsonProperty("modifyAllowed")
public void setModifyAllowed(String modifyAllowed) {
this.modifyAllowed = modifyAllowed;
}

@JsonProperty("agentAllowed")
public String getAgentAllowed() {
return agentAllowed;
}

@JsonProperty("agentAllowed")
public void setAgentAllowed(String agentAllowed) {
this.agentAllowed = agentAllowed;
}

@JsonProperty("categoryType")
public String getCategoryType() {
return categoryType;
}

@JsonProperty("categoryType")
public void setCategoryType(String categoryType) {
this.categoryType = categoryType;
}

@JsonProperty("displayAllowed")
public String getDisplayAllowed() {
return displayAllowed;
}

@JsonProperty("displayAllowed")
public void setDisplayAllowed(String displayAllowed) {
this.displayAllowed = displayAllowed;
}

@JsonProperty("grphDomainType")
public String getGrphDomainType() {
return grphDomainType;
}

@JsonProperty("grphDomainType")
public void setGrphDomainType(String grphDomainType) {
this.grphDomainType = grphDomainType;
}

@JsonProperty("cp2pPayer")
public String getCp2pPayer() {
return cp2pPayer;
}

@JsonProperty("cp2pPayer")
public void setCp2pPayer(String cp2pPayer) {
this.cp2pPayer = cp2pPayer;
}

@JsonProperty("agentCp2pPayee")
public String getAgentCp2pPayee() {
return agentCp2pPayee;
}

@JsonProperty("agentCp2pPayee")
public void setAgentCp2pPayee(String agentCp2pPayee) {
this.agentCp2pPayee = agentCp2pPayee;
}

@JsonProperty("fixedDomains")
public String getFixedDomains() {
return fixedDomains;
}

@JsonProperty("fixedDomains")
public void setFixedDomains(String fixedDomains) {
this.fixedDomains = fixedDomains;
}

@JsonProperty("agentFixedRoles")
public String getAgentFixedRoles() {
return agentFixedRoles;
}

@JsonProperty("agentFixedRoles")
public void setAgentFixedRoles(String agentFixedRoles) {
this.agentFixedRoles = agentFixedRoles;
}

@JsonProperty("combinedKey")
public String getCombinedKey() {
return combinedKey;
}

@JsonProperty("combinedKey")
public void setCombinedKey(String combinedKey) {
this.combinedKey = combinedKey;
}

@JsonProperty("trasnferKey")
public String getTrasnferKey() {
return trasnferKey;
}

@JsonProperty("trasnferKey")
public void setTrasnferKey(String trasnferKey) {
this.trasnferKey = trasnferKey;
}

@JsonProperty("agentRoleName")
public String getAgentRoleName() {
return agentRoleName;
}

@JsonProperty("agentRoleName")
public void setAgentRoleName(String agentRoleName) {
this.agentRoleName = agentRoleName;
}

@JsonProperty("maxLoginCount")
public String getMaxLoginCount() {
return maxLoginCount;
}

@JsonProperty("maxLoginCount")
public void setMaxLoginCount(String maxLoginCount) {
this.maxLoginCount = maxLoginCount;
}

@JsonProperty("cp2pPayee")
public String getCp2pPayee() {
return cp2pPayee;
}

@JsonProperty("cp2pPayee")
public void setCp2pPayee(String cp2pPayee) {
this.cp2pPayee = cp2pPayee;
}

@JsonProperty("cp2pWithinList")
public String getCp2pWithinList() {
return cp2pWithinList;
}

@JsonProperty("cp2pWithinList")
public void setCp2pWithinList(String cp2pWithinList) {
this.cp2pWithinList = cp2pWithinList;
}

@JsonProperty("agentCheckArray")
public String getAgentCheckArray() {
return agentCheckArray;
}

@JsonProperty("agentCheckArray")
public void setAgentCheckArray(String agentCheckArray) {
this.agentCheckArray = agentCheckArray;
}

@JsonProperty("domainAllowed")
public String getDomainAllowed() {
return domainAllowed;
}

@JsonProperty("domainAllowed")
public void setDomainAllowed(String domainAllowed) {
this.domainAllowed = domainAllowed;
}

@JsonProperty("agentDomainName")
public String getAgentDomainName() {
return agentDomainName;
}

@JsonProperty("agentDomainName")
public void setAgentDomainName(String agentDomainName) {
this.agentDomainName = agentDomainName;
}

@JsonProperty("agentCp2pPayer")
public String getAgentCp2pPayer() {
return agentCp2pPayer;
}

@JsonProperty("agentCp2pPayer")
public void setAgentCp2pPayer(String agentCp2pPayer) {
this.agentCp2pPayer = agentCp2pPayer;
}

@JsonProperty("maxTxnMsisdnInt")
public Integer getMaxTxnMsisdnInt() {
return maxTxnMsisdnInt;
}

@JsonProperty("maxTxnMsisdnInt")
public void setMaxTxnMsisdnInt(Integer maxTxnMsisdnInt) {
this.maxTxnMsisdnInt = maxTxnMsisdnInt;
}

@JsonProperty("categoryCode")
public String getCategoryCode() {
return categoryCode;
}

@JsonProperty("categoryCode")
public void setCategoryCode(String categoryCode) {
this.categoryCode = categoryCode;
}

@JsonProperty("domainName")
public String getDomainName() {
return domainName;
}

@JsonProperty("domainName")
public void setDomainName(String domainName) {
this.domainName = domainName;
}

@JsonProperty("categoryName")
public String getCategoryName() {
return categoryName;
}

@JsonProperty("categoryName")
public void setCategoryName(String categoryName) {
this.categoryName = categoryName;
}

@JsonProperty("modifiedOn")
public String getModifiedOn() {
return modifiedOn;
}

@JsonProperty("modifiedOn")
public void setModifiedOn(String modifiedOn) {
this.modifiedOn = modifiedOn;
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

@JsonProperty("modifiedBy")
public String getModifiedBy() {
return modifiedBy;
}

@JsonProperty("modifiedBy")
public void setModifiedBy(String modifiedBy) {
this.modifiedBy = modifiedBy;
}

@JsonProperty("domainTypeCode")
public String getDomainTypeCode() {
return domainTypeCode;
}

@JsonProperty("domainTypeCode")
public void setDomainTypeCode(String domainTypeCode) {
this.domainTypeCode = domainTypeCode;
}

@JsonProperty("domainCodeforCategory")
public String getDomainCodeforCategory() {
return domainCodeforCategory;
}

@JsonProperty("domainCodeforCategory")
public void setDomainCodeforCategory(String domainCodeforCategory) {
this.domainCodeforCategory = domainCodeforCategory;
}

@JsonProperty("restrictedMsisdns")
public String getRestrictedMsisdns() {
return restrictedMsisdns;
}

@JsonProperty("restrictedMsisdns")
public void setRestrictedMsisdns(String restrictedMsisdns) {
this.restrictedMsisdns = restrictedMsisdns;
}

@JsonProperty("transferToListOnly")
public String getTransferToListOnly() {
return transferToListOnly;
}

@JsonProperty("transferToListOnly")
public void setTransferToListOnly(String transferToListOnly) {
this.transferToListOnly = transferToListOnly;
}

@JsonProperty("txnOutsideHierchy")
public String getTxnOutsideHierchy() {
return txnOutsideHierchy;
}

@JsonProperty("txnOutsideHierchy")
public void setTxnOutsideHierchy(String txnOutsideHierchy) {
this.txnOutsideHierchy = txnOutsideHierchy;
}

@JsonProperty("productTypeAllowed")
public String getProductTypeAllowed() {
return productTypeAllowed;
}

@JsonProperty("productTypeAllowed")
public void setProductTypeAllowed(String productTypeAllowed) {
this.productTypeAllowed = productTypeAllowed;
}

@JsonProperty("agentWebInterfaceAllowed")
public String getAgentWebInterfaceAllowed() {
return agentWebInterfaceAllowed;
}

@JsonProperty("agentWebInterfaceAllowed")
public void setAgentWebInterfaceAllowed(String agentWebInterfaceAllowed) {
this.agentWebInterfaceAllowed = agentWebInterfaceAllowed;
}

@JsonProperty("productTypeAssociationAllowed")
public String getProductTypeAssociationAllowed() {
return productTypeAssociationAllowed;
}

@JsonProperty("productTypeAssociationAllowed")
public void setProductTypeAssociationAllowed(String productTypeAssociationAllowed) {
this.productTypeAssociationAllowed = productTypeAssociationAllowed;
}

@JsonProperty("allowedGatewayTypes")
public String getAllowedGatewayTypes() {
return allowedGatewayTypes;
}

@JsonProperty("allowedGatewayTypes")
public void setAllowedGatewayTypes(String allowedGatewayTypes) {
this.allowedGatewayTypes = allowedGatewayTypes;
}

@JsonProperty("agentSmsInterfaceAllowed")
public String getAgentSmsInterfaceAllowed() {
return agentSmsInterfaceAllowed;
}

@JsonProperty("agentSmsInterfaceAllowed")
public void setAgentSmsInterfaceAllowed(String agentSmsInterfaceAllowed) {
this.agentSmsInterfaceAllowed = agentSmsInterfaceAllowed;
}

@JsonProperty("geographicalDomainSeqNo")
public String getGeographicalDomainSeqNo() {
return geographicalDomainSeqNo;
}

@JsonProperty("geographicalDomainSeqNo")
public void setGeographicalDomainSeqNo(String geographicalDomainSeqNo) {
this.geographicalDomainSeqNo = geographicalDomainSeqNo;
}

@JsonProperty("viewOnNetworkBlock")
public String getViewOnNetworkBlock() {
return viewOnNetworkBlock;
}

@JsonProperty("viewOnNetworkBlock")
public void setViewOnNetworkBlock(String viewOnNetworkBlock) {
this.viewOnNetworkBlock = viewOnNetworkBlock;
}

@JsonProperty("grphDomainSequenceNo")
public String getGrphDomainSequenceNo() {
return grphDomainSequenceNo;
}

@JsonProperty("grphDomainSequenceNo")
public void setGrphDomainSequenceNo(String grphDomainSequenceNo) {
this.grphDomainSequenceNo = grphDomainSequenceNo;
}

@JsonProperty("multipleLoginAllowed")
public String getMultipleLoginAllowed() {
return multipleLoginAllowed;
}

@JsonProperty("multipleLoginAllowed")
public void setMultipleLoginAllowed(String multipleLoginAllowed) {
this.multipleLoginAllowed = multipleLoginAllowed;
}

@JsonProperty("agentMaxLoginCount")
public String getAgentMaxLoginCount() {
return agentMaxLoginCount;
}

@JsonProperty("agentMaxLoginCount")
public void setAgentMaxLoginCount(String agentMaxLoginCount) {
this.agentMaxLoginCount = agentMaxLoginCount;
}

@JsonProperty("multipleGrphDomains")
public String getMultipleGrphDomains() {
return multipleGrphDomains;
}

@JsonProperty("multipleGrphDomains")
public void setMultipleGrphDomains(String multipleGrphDomains) {
this.multipleGrphDomains = multipleGrphDomains;
}

@JsonProperty("categorySequenceNumber")
public String getCategorySequenceNumber() {
return categorySequenceNumber;
}

@JsonProperty("categorySequenceNumber")
public void setCategorySequenceNumber(String categorySequenceNumber) {
this.categorySequenceNumber = categorySequenceNumber;
}

@JsonProperty("scheduledTransferAllowed")
public String getScheduledTransferAllowed() {
return scheduledTransferAllowed;
}

@JsonProperty("scheduledTransferAllowed")
public void setScheduledTransferAllowed(String scheduledTransferAllowed) {
this.scheduledTransferAllowed = scheduledTransferAllowed;
}

@JsonProperty("unctrlTransferAllowed")
public String getUnctrlTransferAllowed() {
return unctrlTransferAllowed;
}

@JsonProperty("unctrlTransferAllowed")
public void setUnctrlTransferAllowed(String unctrlTransferAllowed) {
this.unctrlTransferAllowed = unctrlTransferAllowed;
}

@JsonProperty("webInterfaceAllowed")
public String getWebInterfaceAllowed() {
return webInterfaceAllowed;
}

@JsonProperty("webInterfaceAllowed")
public void setWebInterfaceAllowed(String webInterfaceAllowed) {
this.webInterfaceAllowed = webInterfaceAllowed;
}

@JsonProperty("agentCategoryType")
public String getAgentCategoryType() {
return agentCategoryType;
}

@JsonProperty("agentCategoryType")
public void setAgentCategoryType(String agentCategoryType) {
this.agentCategoryType = agentCategoryType;
}

@JsonProperty("agentGeographicalDomainList")
public String getAgentGeographicalDomainList() {
return agentGeographicalDomainList;
}

@JsonProperty("agentGeographicalDomainList")
public void setAgentGeographicalDomainList(String agentGeographicalDomainList) {
this.agentGeographicalDomainList = agentGeographicalDomainList;
}

@JsonProperty("agentMaxTxnMsisdn")
public String getAgentMaxTxnMsisdn() {
return agentMaxTxnMsisdn;
}

@JsonProperty("agentMaxTxnMsisdn")
public void setAgentMaxTxnMsisdn(String agentMaxTxnMsisdn) {
this.agentMaxTxnMsisdn = agentMaxTxnMsisdn;
}

@JsonProperty("agentRestrictedMsisdns")
public String getAgentRestrictedMsisdns() {
return agentRestrictedMsisdns;
}

@JsonProperty("agentRestrictedMsisdns")
public void setAgentRestrictedMsisdns(String agentRestrictedMsisdns) {
this.agentRestrictedMsisdns = agentRestrictedMsisdns;
}

@JsonProperty("agentRoleTypeList")
public String getAgentRoleTypeList() {
return agentRoleTypeList;
}

@JsonProperty("agentRoleTypeList")
public void setAgentRoleTypeList(String agentRoleTypeList) {
this.agentRoleTypeList = agentRoleTypeList;
}

@JsonProperty("agentScheduledTransferAllowed")
public String getAgentScheduledTransferAllowed() {
return agentScheduledTransferAllowed;
}

@JsonProperty("agentScheduledTransferAllowed")
public void setAgentScheduledTransferAllowed(String agentScheduledTransferAllowed) {
this.agentScheduledTransferAllowed = agentScheduledTransferAllowed;
}

@JsonProperty("agentDomainCodeforCategory")
public String getAgentDomainCodeforCategory() {
return agentDomainCodeforCategory;
}

@JsonProperty("agentDomainCodeforCategory")
public void setAgentDomainCodeforCategory(String agentDomainCodeforCategory) {
this.agentDomainCodeforCategory = agentDomainCodeforCategory;
}

@JsonProperty("agentGrphDomainType")
public String getAgentGrphDomainType() {
return agentGrphDomainType;
}

@JsonProperty("agentGrphDomainType")
public void setAgentGrphDomainType(String agentGrphDomainType) {
this.agentGrphDomainType = agentGrphDomainType;
}

@JsonProperty("agentServiceAllowed")
public String getAgentServiceAllowed() {
return agentServiceAllowed;
}

@JsonProperty("agentServiceAllowed")
public void setAgentServiceAllowed(String agentServiceAllowed) {
this.agentServiceAllowed = agentServiceAllowed;
}

@JsonProperty("agentDisplayAllowed")
public String getAgentDisplayAllowed() {
return agentDisplayAllowed;
}

@JsonProperty("agentDisplayAllowed")
public void setAgentDisplayAllowed(String agentDisplayAllowed) {
this.agentDisplayAllowed = agentDisplayAllowed;
}

@JsonProperty("agentModifyAllowed")
public String getAgentModifyAllowed() {
return agentModifyAllowed;
}

@JsonProperty("agentModifyAllowed")
public void setAgentModifyAllowed(String agentModifyAllowed) {
this.agentModifyAllowed = agentModifyAllowed;
}

@JsonProperty("agentMultipleGrphDomains")
public String getAgentMultipleGrphDomains() {
return agentMultipleGrphDomains;
}

@JsonProperty("agentMultipleGrphDomains")
public void setAgentMultipleGrphDomains(String agentMultipleGrphDomains) {
this.agentMultipleGrphDomains = agentMultipleGrphDomains;
}

@JsonProperty("agentAllowedFlag")
public String getAgentAllowedFlag() {
return agentAllowedFlag;
}

@JsonProperty("agentAllowedFlag")
public void setAgentAllowedFlag(String agentAllowedFlag) {
this.agentAllowedFlag = agentAllowedFlag;
}

@JsonProperty("agentCategoryCode")
public String getAgentCategoryCode() {
return agentCategoryCode;
}

@JsonProperty("agentCategoryCode")
public void setAgentCategoryCode(String agentCategoryCode) {
this.agentCategoryCode = agentCategoryCode;
}

@JsonProperty("agentMultipleLoginAllowed")
public String getAgentMultipleLoginAllowed() {
return agentMultipleLoginAllowed;
}

@JsonProperty("agentMultipleLoginAllowed")
public void setAgentMultipleLoginAllowed(String agentMultipleLoginAllowed) {
this.agentMultipleLoginAllowed = agentMultipleLoginAllowed;
}

@JsonProperty("agentRolesMapSelected")
public String getAgentRolesMapSelected() {
return agentRolesMapSelected;
}

@JsonProperty("agentRolesMapSelected")
public void setAgentRolesMapSelected(String agentRolesMapSelected) {
this.agentRolesMapSelected = agentRolesMapSelected;
}

@JsonProperty("agentAgentAllowed")
public String getAgentAgentAllowed() {
return agentAgentAllowed;
}

@JsonProperty("agentAgentAllowed")
public void setAgentAgentAllowed(String agentAgentAllowed) {
this.agentAgentAllowed = agentAgentAllowed;
}

@JsonProperty("agentGatewayType")
public String getAgentGatewayType() {
return agentGatewayType;
}

@JsonProperty("agentGatewayType")
public void setAgentGatewayType(String agentGatewayType) {
this.agentGatewayType = agentGatewayType;
}

@JsonProperty("agentMessageGatewayTypeList")
public String getAgentMessageGatewayTypeList() {
return agentMessageGatewayTypeList;
}

@JsonProperty("agentMessageGatewayTypeList")
public void setAgentMessageGatewayTypeList(String agentMessageGatewayTypeList) {
this.agentMessageGatewayTypeList = agentMessageGatewayTypeList;
}

@JsonProperty("agentCategoryStatusList")
public String getAgentCategoryStatusList() {
return agentCategoryStatusList;
}

@JsonProperty("agentCategoryStatusList")
public void setAgentCategoryStatusList(String agentCategoryStatusList) {
this.agentCategoryStatusList = agentCategoryStatusList;
}

@JsonProperty("agentCategoryName")
public String getAgentCategoryName() {
return agentCategoryName;
}

@JsonProperty("agentCategoryName")
public void setAgentCategoryName(String agentCategoryName) {
this.agentCategoryName = agentCategoryName;
}

@JsonProperty("agentHierarchyAllowed")
public String getAgentHierarchyAllowed() {
return agentHierarchyAllowed;
}

@JsonProperty("agentHierarchyAllowed")
public void setAgentHierarchyAllowed(String agentHierarchyAllowed) {
this.agentHierarchyAllowed = agentHierarchyAllowed;
}

@JsonProperty("agentOutletsAllowed")
public String getAgentOutletsAllowed() {
return agentOutletsAllowed;
}

@JsonProperty("agentOutletsAllowed")
public void setAgentOutletsAllowed(String agentOutletsAllowed) {
this.agentOutletsAllowed = agentOutletsAllowed;
}

@JsonProperty("agentCategoryStatus")
public String getAgentCategoryStatus() {
return agentCategoryStatus;
}

@JsonProperty("agentCategoryStatus")
public void setAgentCategoryStatus(String agentCategoryStatus) {
this.agentCategoryStatus = agentCategoryStatus;
}

@JsonProperty("agentGatewayName")
public String getAgentGatewayName() {
return agentGatewayName;
}

@JsonProperty("agentGatewayName")
public void setAgentGatewayName(String agentGatewayName) {
this.agentGatewayName = agentGatewayName;
}

@JsonProperty("agentUserIdPrefix")
public String getAgentUserIdPrefix() {
return agentUserIdPrefix;
}

@JsonProperty("agentUserIdPrefix")
public void setAgentUserIdPrefix(String agentUserIdPrefix) {
this.agentUserIdPrefix = agentUserIdPrefix;
}

@JsonProperty("parentOrOwnerRadioValue")
public String getParentOrOwnerRadioValue() {
return parentOrOwnerRadioValue;
}

@JsonProperty("parentOrOwnerRadioValue")
public void setParentOrOwnerRadioValue(String parentOrOwnerRadioValue) {
this.parentOrOwnerRadioValue = parentOrOwnerRadioValue;
}

@JsonProperty("agentLowBalAlertAllow")
public String getAgentLowBalAlertAllow() {
return agentLowBalAlertAllow;
}

@JsonProperty("agentLowBalAlertAllow")
public void setAgentLowBalAlertAllow(String agentLowBalAlertAllow) {
this.agentLowBalAlertAllow = agentLowBalAlertAllow;
}

@JsonProperty("categoryTypeCode")
public String getCategoryTypeCode() {
return categoryTypeCode;
}

@JsonProperty("categoryTypeCode")
public void setCategoryTypeCode(String categoryTypeCode) {
this.categoryTypeCode = categoryTypeCode;
}

@JsonProperty("agentUnctrlTransferAllowed")
public String getAgentUnctrlTransferAllowed() {
return agentUnctrlTransferAllowed;
}

@JsonProperty("agentUnctrlTransferAllowed")
public void setAgentUnctrlTransferAllowed(String agentUnctrlTransferAllowed) {
this.agentUnctrlTransferAllowed = agentUnctrlTransferAllowed;
}

@JsonProperty("agentRechargeByParentOnly")
public String getAgentRechargeByParentOnly() {
return agentRechargeByParentOnly;
}

@JsonProperty("agentRechargeByParentOnly")
public void setAgentRechargeByParentOnly(String agentRechargeByParentOnly) {
this.agentRechargeByParentOnly = agentRechargeByParentOnly;
}

@JsonProperty("agentCp2pWithinList")
public String getAgentCp2pWithinList() {
return agentCp2pWithinList;
}

@JsonProperty("agentCp2pWithinList")
public void setAgentCp2pWithinList(String agentCp2pWithinList) {
this.agentCp2pWithinList = agentCp2pWithinList;
}

@JsonProperty("rechargeByParentOnly")
public String getRechargeByParentOnly() {
return rechargeByParentOnly;
}

@JsonProperty("rechargeByParentOnly")
public void setRechargeByParentOnly(String rechargeByParentOnly) {
this.rechargeByParentOnly = rechargeByParentOnly;
}

@JsonProperty("authenticationType")
public String getAuthenticationType() {
return authenticationType;
}

@JsonProperty("authenticationType")
public void setAuthenticationType(String authenticationType) {
this.authenticationType = authenticationType;
}

@JsonProperty("agentParentOrOwnerRadioValue")
public String getAgentParentOrOwnerRadioValue() {
return agentParentOrOwnerRadioValue;
}

@JsonProperty("agentParentOrOwnerRadioValue")
public void setAgentParentOrOwnerRadioValue(String agentParentOrOwnerRadioValue) {
this.agentParentOrOwnerRadioValue = agentParentOrOwnerRadioValue;
}

@JsonProperty("agentViewOnNetworkBlock")
public String getAgentViewOnNetworkBlock() {
return agentViewOnNetworkBlock;
}

@JsonProperty("agentViewOnNetworkBlock")
public void setAgentViewOnNetworkBlock(String agentViewOnNetworkBlock) {
this.agentViewOnNetworkBlock = agentViewOnNetworkBlock;
}

@JsonProperty("numberOfCategoryForDomain")
public String getNumberOfCategoryForDomain() {
return numberOfCategoryForDomain;
}

@JsonProperty("numberOfCategoryForDomain")
public void setNumberOfCategoryForDomain(String numberOfCategoryForDomain) {
this.numberOfCategoryForDomain = numberOfCategoryForDomain;
}

@JsonProperty("grphDomainTypeName")
public String getGrphDomainTypeName() {
return grphDomainTypeName;
}

@JsonProperty("grphDomainTypeName")
public void setGrphDomainTypeName(String grphDomainTypeName) {
this.grphDomainTypeName = grphDomainTypeName;
}

@JsonProperty("agentModifiedMessageGatewayTypeList")
public String getAgentModifiedMessageGatewayTypeList() {
return agentModifiedMessageGatewayTypeList;
}

@JsonProperty("agentModifiedMessageGatewayTypeList")
public void setAgentModifiedMessageGatewayTypeList(String agentModifiedMessageGatewayTypeList) {
this.agentModifiedMessageGatewayTypeList = agentModifiedMessageGatewayTypeList;
}

@JsonProperty("agentProductTypeAssociationAllowed")
public String getAgentProductTypeAssociationAllowed() {
return agentProductTypeAssociationAllowed;
}

@JsonProperty("agentProductTypeAssociationAllowed")
public void setAgentProductTypeAssociationAllowed(String agentProductTypeAssociationAllowed) {
this.agentProductTypeAssociationAllowed = agentProductTypeAssociationAllowed;
}

@JsonProperty("smsInterfaceAllowed")
public String getSmsInterfaceAllowed() {
return smsInterfaceAllowed;
}

@JsonProperty("smsInterfaceAllowed")
public void setSmsInterfaceAllowed(String smsInterfaceAllowed) {
this.smsInterfaceAllowed = smsInterfaceAllowed;
}

@JsonProperty("lowBalAlertAllow")
public String getLowBalAlertAllow() {
return lowBalAlertAllow;
}

@JsonProperty("lowBalAlertAllow")
public void setLowBalAlertAllow(String lowBalAlertAllow) {
this.lowBalAlertAllow = lowBalAlertAllow;
}

@JsonProperty("serviceAllowed")
public String getServiceAllowed() {
return serviceAllowed;
}

@JsonProperty("serviceAllowed")
public void setServiceAllowed(String serviceAllowed) {
this.serviceAllowed = serviceAllowed;
}

@JsonProperty("outletsAllowed")
public String getOutletsAllowed() {
return outletsAllowed;
}

@JsonProperty("outletsAllowed")
public void setOutletsAllowed(String outletsAllowed) {
this.outletsAllowed = outletsAllowed;
}

@JsonProperty("userIdPrefix")
public String getUserIdPrefix() {
return userIdPrefix;
}

@JsonProperty("userIdPrefix")
public void setUserIdPrefix(String userIdPrefix) {
this.userIdPrefix = userIdPrefix;
}

@JsonProperty("maxTxnMsisdn")
public String getMaxTxnMsisdn() {
return maxTxnMsisdn;
}

@JsonProperty("maxTxnMsisdn")
public void setMaxTxnMsisdn(String maxTxnMsisdn) {
this.maxTxnMsisdn = maxTxnMsisdn;
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