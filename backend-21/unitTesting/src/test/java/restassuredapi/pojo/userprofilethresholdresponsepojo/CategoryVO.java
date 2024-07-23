
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
    "lastModifiedTime",
    "sequenceNumber",
    "parentCategoryCode",
    "smsInterfaceAllowed",
    "multipleGrphDomains",
    "domainCodeforCategory",
    "productTypeAllowed",
    "serviceAllowed",
    "trasnferKey",
    "combinedKey",
    "radioIndex",
    "webInterfaceAllowed",
    "createdBy",
    "modifiedBy",
    "createdOn",
    "modifiedOn",
    "numberOfCategoryForDomain",
    "geographicalDomainSeqNo",
    "allowedGatewayTypes",
    "categoryTypeCode",
    "agentLowBalAlertAllow",
    "agentParentOrOwnerRadioValue",
    "authenticationType",
    "parentOrOwnerRadioValue",
    "domainName",
    "maxLoginCount",
    "lowBalAlertAllow",
    "outletsAllowed",
    "grphDomainType",
    "agentRestrictedMsisdns",
    "agentServiceAllowed",
    "agentRolesMapSelected",
    "agentOutletsAllowed",
    "agentUserIdPrefix",
    "agentScheduledTransferAllowed",
    "agentUnctrlTransferAllowed",
    "agentMultipleLoginAllowed",
    "agentViewOnNetworkBlock",
    "agentRoleTypeList",
    "agentRechargeByParentOnly",
    "cp2pWithinList",
    "rechargeByParentOnly",
    "agentCp2pPayer",
    "agentCp2pWithinList",
    "agentCp2pPayee",
    "agentAllowedFlag",
    "agentWebInterfaceAllowed",
    "categorySequenceNumber",
    "agentSmsInterfaceAllowed",
    "agentAgentAllowed",
    "agentDisplayAllowed",
    "agentCheckArray",
    "displayAllowed",
    "categoryStatus",
    "agentCategoryName",
    "productTypeAssociationAllowed",
    "scheduledTransferAllowed",
    "txnOutsideHierchy",
    "unctrlTransferAllowed",
    "agentCategoryStatus",
    "agentCategoryStatusList",
    "viewOnNetworkBlock",
    "agentCategoryType",
    "agentCategoryCode",
    "agentMaxTxnMsisdn",
    "agentGatewayType",
    "agentDomainCodeforCategory",
    "agentMultipleGrphDomains",
    "agentModifyAllowed",
    "agentFixedRoles",
    "agentGatewayName",
    "agentGrphDomainType",
    "agentMaxLoginCount",
    "agentHierarchyAllowed",
    "agentGeographicalDomainList",
    "agentMessageGatewayTypeList",
    "agentDomainName",
    "hierarchyAllowed",
    "transferToListOnly",
    "restrictedMsisdns",
    "maxTxnMsisdnInt",
    "grphDomainSequenceNo",
    "categoryName",
    "domainAllowed",
    "categoryCode",
    "fixedDomains",
    "modifyAllowed",
    "domainTypeCode",
    "grphDomainTypeName",
    "multipleLoginAllowed",
    "recordCount",
    "categoryType",
    "agentModifiedMessageGatewayTypeList",
    "agentProductTypeAssociationAllowed",
    "fixedRoles",
    "userIdPrefix",
    "agentAllowed",
    "maxTxnMsisdn",
    "agentRoleName",
    "cp2pPayer",
    "cp2pPayee"
})
public class CategoryVO {

    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;
    @JsonProperty("sequenceNumber")
    private Long sequenceNumber;
    @JsonProperty("parentCategoryCode")
    private Object parentCategoryCode;
    @JsonProperty("smsInterfaceAllowed")
    private String smsInterfaceAllowed;
    @JsonProperty("multipleGrphDomains")
    private String multipleGrphDomains;
    @JsonProperty("domainCodeforCategory")
    private String domainCodeforCategory;
    @JsonProperty("productTypeAllowed")
    private Object productTypeAllowed;
    @JsonProperty("serviceAllowed")
    private String serviceAllowed;
    @JsonProperty("trasnferKey")
    private String trasnferKey;
    @JsonProperty("combinedKey")
    private String combinedKey;
    @JsonProperty("radioIndex")
    private Long radioIndex;
    @JsonProperty("webInterfaceAllowed")
    private String webInterfaceAllowed;
    @JsonProperty("createdBy")
    private Object createdBy;
    @JsonProperty("modifiedBy")
    private Object modifiedBy;
    @JsonProperty("createdOn")
    private Object createdOn;
    @JsonProperty("modifiedOn")
    private Object modifiedOn;
    @JsonProperty("numberOfCategoryForDomain")
    private Long numberOfCategoryForDomain;
    @JsonProperty("geographicalDomainSeqNo")
    private Long geographicalDomainSeqNo;
    @JsonProperty("allowedGatewayTypes")
    private Object allowedGatewayTypes;
    @JsonProperty("categoryTypeCode")
    private Object categoryTypeCode;
    @JsonProperty("agentLowBalAlertAllow")
    private Object agentLowBalAlertAllow;
    @JsonProperty("agentParentOrOwnerRadioValue")
    private Object agentParentOrOwnerRadioValue;
    @JsonProperty("authenticationType")
    private String authenticationType;
    @JsonProperty("parentOrOwnerRadioValue")
    private Object parentOrOwnerRadioValue;
    @JsonProperty("domainName")
    private Object domainName;
    @JsonProperty("maxLoginCount")
    private Long maxLoginCount;
    @JsonProperty("lowBalAlertAllow")
    private String lowBalAlertAllow;
    @JsonProperty("outletsAllowed")
    private Object outletsAllowed;
    @JsonProperty("grphDomainType")
    private String grphDomainType;
    @JsonProperty("agentRestrictedMsisdns")
    private Object agentRestrictedMsisdns;
    @JsonProperty("agentServiceAllowed")
    private Object agentServiceAllowed;
    @JsonProperty("agentRolesMapSelected")
    private Object agentRolesMapSelected;
    @JsonProperty("agentOutletsAllowed")
    private Object agentOutletsAllowed;
    @JsonProperty("agentUserIdPrefix")
    private Object agentUserIdPrefix;
    @JsonProperty("agentScheduledTransferAllowed")
    private Object agentScheduledTransferAllowed;
    @JsonProperty("agentUnctrlTransferAllowed")
    private Object agentUnctrlTransferAllowed;
    @JsonProperty("agentMultipleLoginAllowed")
    private Object agentMultipleLoginAllowed;
    @JsonProperty("agentViewOnNetworkBlock")
    private Object agentViewOnNetworkBlock;
    @JsonProperty("agentRoleTypeList")
    private Object agentRoleTypeList;
    @JsonProperty("agentRechargeByParentOnly")
    private Object agentRechargeByParentOnly;
    @JsonProperty("cp2pWithinList")
    private Object cp2pWithinList;
    @JsonProperty("rechargeByParentOnly")
    private Object rechargeByParentOnly;
    @JsonProperty("agentCp2pPayer")
    private Object agentCp2pPayer;
    @JsonProperty("agentCp2pWithinList")
    private Object agentCp2pWithinList;
    @JsonProperty("agentCp2pPayee")
    private Object agentCp2pPayee;
    @JsonProperty("agentAllowedFlag")
    private Object agentAllowedFlag;
    @JsonProperty("agentWebInterfaceAllowed")
    private Object agentWebInterfaceAllowed;
    @JsonProperty("categorySequenceNumber")
    private Long categorySequenceNumber;
    @JsonProperty("agentSmsInterfaceAllowed")
    private Object agentSmsInterfaceAllowed;
    @JsonProperty("agentAgentAllowed")
    private Object agentAgentAllowed;
    @JsonProperty("agentDisplayAllowed")
    private Object agentDisplayAllowed;
    @JsonProperty("agentCheckArray")
    private Object agentCheckArray;
    @JsonProperty("displayAllowed")
    private Object displayAllowed;
    @JsonProperty("categoryStatus")
    private String categoryStatus;
    @JsonProperty("agentCategoryName")
    private Object agentCategoryName;
    @JsonProperty("productTypeAssociationAllowed")
    private Object productTypeAssociationAllowed;
    @JsonProperty("scheduledTransferAllowed")
    private Object scheduledTransferAllowed;
    @JsonProperty("txnOutsideHierchy")
    private Object txnOutsideHierchy;
    @JsonProperty("unctrlTransferAllowed")
    private String unctrlTransferAllowed;
    @JsonProperty("agentCategoryStatus")
    private Object agentCategoryStatus;
    @JsonProperty("agentCategoryStatusList")
    private Object agentCategoryStatusList;
    @JsonProperty("viewOnNetworkBlock")
    private Object viewOnNetworkBlock;
    @JsonProperty("agentCategoryType")
    private Object agentCategoryType;
    @JsonProperty("agentCategoryCode")
    private Object agentCategoryCode;
    @JsonProperty("agentMaxTxnMsisdn")
    private Object agentMaxTxnMsisdn;
    @JsonProperty("agentGatewayType")
    private Object agentGatewayType;
    @JsonProperty("agentDomainCodeforCategory")
    private Object agentDomainCodeforCategory;
    @JsonProperty("agentMultipleGrphDomains")
    private Object agentMultipleGrphDomains;
    @JsonProperty("agentModifyAllowed")
    private Object agentModifyAllowed;
    @JsonProperty("agentFixedRoles")
    private Object agentFixedRoles;
    @JsonProperty("agentGatewayName")
    private Object agentGatewayName;
    @JsonProperty("agentGrphDomainType")
    private Object agentGrphDomainType;
    @JsonProperty("agentMaxLoginCount")
    private Long agentMaxLoginCount;
    @JsonProperty("agentHierarchyAllowed")
    private Object agentHierarchyAllowed;
    @JsonProperty("agentGeographicalDomainList")
    private Object agentGeographicalDomainList;
    @JsonProperty("agentMessageGatewayTypeList")
    private Object agentMessageGatewayTypeList;
    @JsonProperty("agentDomainName")
    private Object agentDomainName;
    @JsonProperty("hierarchyAllowed")
    private Object hierarchyAllowed;
    @JsonProperty("transferToListOnly")
    private String transferToListOnly;
    @JsonProperty("restrictedMsisdns")
    private String restrictedMsisdns;
    @JsonProperty("maxTxnMsisdnInt")
    private Long maxTxnMsisdnInt;
    @JsonProperty("grphDomainSequenceNo")
    private Long grphDomainSequenceNo;
    @JsonProperty("categoryName")
    private String categoryName;
    @JsonProperty("domainAllowed")
    private Object domainAllowed;
    @JsonProperty("categoryCode")
    private String categoryCode;
    @JsonProperty("fixedDomains")
    private Object fixedDomains;
    @JsonProperty("modifyAllowed")
    private Object modifyAllowed;
    @JsonProperty("domainTypeCode")
    private String domainTypeCode;
    @JsonProperty("grphDomainTypeName")
    private Object grphDomainTypeName;
    @JsonProperty("multipleLoginAllowed")
    private String multipleLoginAllowed;
    @JsonProperty("recordCount")
    private Long recordCount;
    @JsonProperty("categoryType")
    private String categoryType;
    @JsonProperty("agentModifiedMessageGatewayTypeList")
    private Object agentModifiedMessageGatewayTypeList;
    @JsonProperty("agentProductTypeAssociationAllowed")
    private Object agentProductTypeAssociationAllowed;
    @JsonProperty("fixedRoles")
    private String fixedRoles;
    @JsonProperty("userIdPrefix")
    private Object userIdPrefix;
    @JsonProperty("agentAllowed")
    private String agentAllowed;
    @JsonProperty("maxTxnMsisdn")
    private String maxTxnMsisdn;
    @JsonProperty("agentRoleName")
    private Object agentRoleName;
    @JsonProperty("cp2pPayer")
    private Object cp2pPayer;
    @JsonProperty("cp2pPayee")
    private Object cp2pPayee;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("lastModifiedTime")
    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }

    @JsonProperty("lastModifiedTime")
    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @JsonProperty("sequenceNumber")
    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    @JsonProperty("sequenceNumber")
    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @JsonProperty("parentCategoryCode")
    public Object getParentCategoryCode() {
        return parentCategoryCode;
    }

    @JsonProperty("parentCategoryCode")
    public void setParentCategoryCode(Object parentCategoryCode) {
        this.parentCategoryCode = parentCategoryCode;
    }

    @JsonProperty("smsInterfaceAllowed")
    public String getSmsInterfaceAllowed() {
        return smsInterfaceAllowed;
    }

    @JsonProperty("smsInterfaceAllowed")
    public void setSmsInterfaceAllowed(String smsInterfaceAllowed) {
        this.smsInterfaceAllowed = smsInterfaceAllowed;
    }

    @JsonProperty("multipleGrphDomains")
    public String getMultipleGrphDomains() {
        return multipleGrphDomains;
    }

    @JsonProperty("multipleGrphDomains")
    public void setMultipleGrphDomains(String multipleGrphDomains) {
        this.multipleGrphDomains = multipleGrphDomains;
    }

    @JsonProperty("domainCodeforCategory")
    public String getDomainCodeforCategory() {
        return domainCodeforCategory;
    }

    @JsonProperty("domainCodeforCategory")
    public void setDomainCodeforCategory(String domainCodeforCategory) {
        this.domainCodeforCategory = domainCodeforCategory;
    }

    @JsonProperty("productTypeAllowed")
    public Object getProductTypeAllowed() {
        return productTypeAllowed;
    }

    @JsonProperty("productTypeAllowed")
    public void setProductTypeAllowed(Object productTypeAllowed) {
        this.productTypeAllowed = productTypeAllowed;
    }

    @JsonProperty("serviceAllowed")
    public String getServiceAllowed() {
        return serviceAllowed;
    }

    @JsonProperty("serviceAllowed")
    public void setServiceAllowed(String serviceAllowed) {
        this.serviceAllowed = serviceAllowed;
    }

    @JsonProperty("trasnferKey")
    public String getTrasnferKey() {
        return trasnferKey;
    }

    @JsonProperty("trasnferKey")
    public void setTrasnferKey(String trasnferKey) {
        this.trasnferKey = trasnferKey;
    }

    @JsonProperty("combinedKey")
    public String getCombinedKey() {
        return combinedKey;
    }

    @JsonProperty("combinedKey")
    public void setCombinedKey(String combinedKey) {
        this.combinedKey = combinedKey;
    }

    @JsonProperty("radioIndex")
    public Long getRadioIndex() {
        return radioIndex;
    }

    @JsonProperty("radioIndex")
    public void setRadioIndex(Long radioIndex) {
        this.radioIndex = radioIndex;
    }

    @JsonProperty("webInterfaceAllowed")
    public String getWebInterfaceAllowed() {
        return webInterfaceAllowed;
    }

    @JsonProperty("webInterfaceAllowed")
    public void setWebInterfaceAllowed(String webInterfaceAllowed) {
        this.webInterfaceAllowed = webInterfaceAllowed;
    }

    @JsonProperty("createdBy")
    public Object getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
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

    @JsonProperty("numberOfCategoryForDomain")
    public Long getNumberOfCategoryForDomain() {
        return numberOfCategoryForDomain;
    }

    @JsonProperty("numberOfCategoryForDomain")
    public void setNumberOfCategoryForDomain(Long numberOfCategoryForDomain) {
        this.numberOfCategoryForDomain = numberOfCategoryForDomain;
    }

    @JsonProperty("geographicalDomainSeqNo")
    public Long getGeographicalDomainSeqNo() {
        return geographicalDomainSeqNo;
    }

    @JsonProperty("geographicalDomainSeqNo")
    public void setGeographicalDomainSeqNo(Long geographicalDomainSeqNo) {
        this.geographicalDomainSeqNo = geographicalDomainSeqNo;
    }

    @JsonProperty("allowedGatewayTypes")
    public Object getAllowedGatewayTypes() {
        return allowedGatewayTypes;
    }

    @JsonProperty("allowedGatewayTypes")
    public void setAllowedGatewayTypes(Object allowedGatewayTypes) {
        this.allowedGatewayTypes = allowedGatewayTypes;
    }

    @JsonProperty("categoryTypeCode")
    public Object getCategoryTypeCode() {
        return categoryTypeCode;
    }

    @JsonProperty("categoryTypeCode")
    public void setCategoryTypeCode(Object categoryTypeCode) {
        this.categoryTypeCode = categoryTypeCode;
    }

    @JsonProperty("agentLowBalAlertAllow")
    public Object getAgentLowBalAlertAllow() {
        return agentLowBalAlertAllow;
    }

    @JsonProperty("agentLowBalAlertAllow")
    public void setAgentLowBalAlertAllow(Object agentLowBalAlertAllow) {
        this.agentLowBalAlertAllow = agentLowBalAlertAllow;
    }

    @JsonProperty("agentParentOrOwnerRadioValue")
    public Object getAgentParentOrOwnerRadioValue() {
        return agentParentOrOwnerRadioValue;
    }

    @JsonProperty("agentParentOrOwnerRadioValue")
    public void setAgentParentOrOwnerRadioValue(Object agentParentOrOwnerRadioValue) {
        this.agentParentOrOwnerRadioValue = agentParentOrOwnerRadioValue;
    }

    @JsonProperty("authenticationType")
    public String getAuthenticationType() {
        return authenticationType;
    }

    @JsonProperty("authenticationType")
    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    @JsonProperty("parentOrOwnerRadioValue")
    public Object getParentOrOwnerRadioValue() {
        return parentOrOwnerRadioValue;
    }

    @JsonProperty("parentOrOwnerRadioValue")
    public void setParentOrOwnerRadioValue(Object parentOrOwnerRadioValue) {
        this.parentOrOwnerRadioValue = parentOrOwnerRadioValue;
    }

    @JsonProperty("domainName")
    public Object getDomainName() {
        return domainName;
    }

    @JsonProperty("domainName")
    public void setDomainName(Object domainName) {
        this.domainName = domainName;
    }

    @JsonProperty("maxLoginCount")
    public Long getMaxLoginCount() {
        return maxLoginCount;
    }

    @JsonProperty("maxLoginCount")
    public void setMaxLoginCount(Long maxLoginCount) {
        this.maxLoginCount = maxLoginCount;
    }

    @JsonProperty("lowBalAlertAllow")
    public String getLowBalAlertAllow() {
        return lowBalAlertAllow;
    }

    @JsonProperty("lowBalAlertAllow")
    public void setLowBalAlertAllow(String lowBalAlertAllow) {
        this.lowBalAlertAllow = lowBalAlertAllow;
    }

    @JsonProperty("outletsAllowed")
    public Object getOutletsAllowed() {
        return outletsAllowed;
    }

    @JsonProperty("outletsAllowed")
    public void setOutletsAllowed(Object outletsAllowed) {
        this.outletsAllowed = outletsAllowed;
    }

    @JsonProperty("grphDomainType")
    public String getGrphDomainType() {
        return grphDomainType;
    }

    @JsonProperty("grphDomainType")
    public void setGrphDomainType(String grphDomainType) {
        this.grphDomainType = grphDomainType;
    }

    @JsonProperty("agentRestrictedMsisdns")
    public Object getAgentRestrictedMsisdns() {
        return agentRestrictedMsisdns;
    }

    @JsonProperty("agentRestrictedMsisdns")
    public void setAgentRestrictedMsisdns(Object agentRestrictedMsisdns) {
        this.agentRestrictedMsisdns = agentRestrictedMsisdns;
    }

    @JsonProperty("agentServiceAllowed")
    public Object getAgentServiceAllowed() {
        return agentServiceAllowed;
    }

    @JsonProperty("agentServiceAllowed")
    public void setAgentServiceAllowed(Object agentServiceAllowed) {
        this.agentServiceAllowed = agentServiceAllowed;
    }

    @JsonProperty("agentRolesMapSelected")
    public Object getAgentRolesMapSelected() {
        return agentRolesMapSelected;
    }

    @JsonProperty("agentRolesMapSelected")
    public void setAgentRolesMapSelected(Object agentRolesMapSelected) {
        this.agentRolesMapSelected = agentRolesMapSelected;
    }

    @JsonProperty("agentOutletsAllowed")
    public Object getAgentOutletsAllowed() {
        return agentOutletsAllowed;
    }

    @JsonProperty("agentOutletsAllowed")
    public void setAgentOutletsAllowed(Object agentOutletsAllowed) {
        this.agentOutletsAllowed = agentOutletsAllowed;
    }

    @JsonProperty("agentUserIdPrefix")
    public Object getAgentUserIdPrefix() {
        return agentUserIdPrefix;
    }

    @JsonProperty("agentUserIdPrefix")
    public void setAgentUserIdPrefix(Object agentUserIdPrefix) {
        this.agentUserIdPrefix = agentUserIdPrefix;
    }

    @JsonProperty("agentScheduledTransferAllowed")
    public Object getAgentScheduledTransferAllowed() {
        return agentScheduledTransferAllowed;
    }

    @JsonProperty("agentScheduledTransferAllowed")
    public void setAgentScheduledTransferAllowed(Object agentScheduledTransferAllowed) {
        this.agentScheduledTransferAllowed = agentScheduledTransferAllowed;
    }

    @JsonProperty("agentUnctrlTransferAllowed")
    public Object getAgentUnctrlTransferAllowed() {
        return agentUnctrlTransferAllowed;
    }

    @JsonProperty("agentUnctrlTransferAllowed")
    public void setAgentUnctrlTransferAllowed(Object agentUnctrlTransferAllowed) {
        this.agentUnctrlTransferAllowed = agentUnctrlTransferAllowed;
    }

    @JsonProperty("agentMultipleLoginAllowed")
    public Object getAgentMultipleLoginAllowed() {
        return agentMultipleLoginAllowed;
    }

    @JsonProperty("agentMultipleLoginAllowed")
    public void setAgentMultipleLoginAllowed(Object agentMultipleLoginAllowed) {
        this.agentMultipleLoginAllowed = agentMultipleLoginAllowed;
    }

    @JsonProperty("agentViewOnNetworkBlock")
    public Object getAgentViewOnNetworkBlock() {
        return agentViewOnNetworkBlock;
    }

    @JsonProperty("agentViewOnNetworkBlock")
    public void setAgentViewOnNetworkBlock(Object agentViewOnNetworkBlock) {
        this.agentViewOnNetworkBlock = agentViewOnNetworkBlock;
    }

    @JsonProperty("agentRoleTypeList")
    public Object getAgentRoleTypeList() {
        return agentRoleTypeList;
    }

    @JsonProperty("agentRoleTypeList")
    public void setAgentRoleTypeList(Object agentRoleTypeList) {
        this.agentRoleTypeList = agentRoleTypeList;
    }

    @JsonProperty("agentRechargeByParentOnly")
    public Object getAgentRechargeByParentOnly() {
        return agentRechargeByParentOnly;
    }

    @JsonProperty("agentRechargeByParentOnly")
    public void setAgentRechargeByParentOnly(Object agentRechargeByParentOnly) {
        this.agentRechargeByParentOnly = agentRechargeByParentOnly;
    }

    @JsonProperty("cp2pWithinList")
    public Object getCp2pWithinList() {
        return cp2pWithinList;
    }

    @JsonProperty("cp2pWithinList")
    public void setCp2pWithinList(Object cp2pWithinList) {
        this.cp2pWithinList = cp2pWithinList;
    }

    @JsonProperty("rechargeByParentOnly")
    public Object getRechargeByParentOnly() {
        return rechargeByParentOnly;
    }

    @JsonProperty("rechargeByParentOnly")
    public void setRechargeByParentOnly(Object rechargeByParentOnly) {
        this.rechargeByParentOnly = rechargeByParentOnly;
    }

    @JsonProperty("agentCp2pPayer")
    public Object getAgentCp2pPayer() {
        return agentCp2pPayer;
    }

    @JsonProperty("agentCp2pPayer")
    public void setAgentCp2pPayer(Object agentCp2pPayer) {
        this.agentCp2pPayer = agentCp2pPayer;
    }

    @JsonProperty("agentCp2pWithinList")
    public Object getAgentCp2pWithinList() {
        return agentCp2pWithinList;
    }

    @JsonProperty("agentCp2pWithinList")
    public void setAgentCp2pWithinList(Object agentCp2pWithinList) {
        this.agentCp2pWithinList = agentCp2pWithinList;
    }

    @JsonProperty("agentCp2pPayee")
    public Object getAgentCp2pPayee() {
        return agentCp2pPayee;
    }

    @JsonProperty("agentCp2pPayee")
    public void setAgentCp2pPayee(Object agentCp2pPayee) {
        this.agentCp2pPayee = agentCp2pPayee;
    }

    @JsonProperty("agentAllowedFlag")
    public Object getAgentAllowedFlag() {
        return agentAllowedFlag;
    }

    @JsonProperty("agentAllowedFlag")
    public void setAgentAllowedFlag(Object agentAllowedFlag) {
        this.agentAllowedFlag = agentAllowedFlag;
    }

    @JsonProperty("agentWebInterfaceAllowed")
    public Object getAgentWebInterfaceAllowed() {
        return agentWebInterfaceAllowed;
    }

    @JsonProperty("agentWebInterfaceAllowed")
    public void setAgentWebInterfaceAllowed(Object agentWebInterfaceAllowed) {
        this.agentWebInterfaceAllowed = agentWebInterfaceAllowed;
    }

    @JsonProperty("categorySequenceNumber")
    public Long getCategorySequenceNumber() {
        return categorySequenceNumber;
    }

    @JsonProperty("categorySequenceNumber")
    public void setCategorySequenceNumber(Long categorySequenceNumber) {
        this.categorySequenceNumber = categorySequenceNumber;
    }

    @JsonProperty("agentSmsInterfaceAllowed")
    public Object getAgentSmsInterfaceAllowed() {
        return agentSmsInterfaceAllowed;
    }

    @JsonProperty("agentSmsInterfaceAllowed")
    public void setAgentSmsInterfaceAllowed(Object agentSmsInterfaceAllowed) {
        this.agentSmsInterfaceAllowed = agentSmsInterfaceAllowed;
    }

    @JsonProperty("agentAgentAllowed")
    public Object getAgentAgentAllowed() {
        return agentAgentAllowed;
    }

    @JsonProperty("agentAgentAllowed")
    public void setAgentAgentAllowed(Object agentAgentAllowed) {
        this.agentAgentAllowed = agentAgentAllowed;
    }

    @JsonProperty("agentDisplayAllowed")
    public Object getAgentDisplayAllowed() {
        return agentDisplayAllowed;
    }

    @JsonProperty("agentDisplayAllowed")
    public void setAgentDisplayAllowed(Object agentDisplayAllowed) {
        this.agentDisplayAllowed = agentDisplayAllowed;
    }

    @JsonProperty("agentCheckArray")
    public Object getAgentCheckArray() {
        return agentCheckArray;
    }

    @JsonProperty("agentCheckArray")
    public void setAgentCheckArray(Object agentCheckArray) {
        this.agentCheckArray = agentCheckArray;
    }

    @JsonProperty("displayAllowed")
    public Object getDisplayAllowed() {
        return displayAllowed;
    }

    @JsonProperty("displayAllowed")
    public void setDisplayAllowed(Object displayAllowed) {
        this.displayAllowed = displayAllowed;
    }

    @JsonProperty("categoryStatus")
    public String getCategoryStatus() {
        return categoryStatus;
    }

    @JsonProperty("categoryStatus")
    public void setCategoryStatus(String categoryStatus) {
        this.categoryStatus = categoryStatus;
    }

    @JsonProperty("agentCategoryName")
    public Object getAgentCategoryName() {
        return agentCategoryName;
    }

    @JsonProperty("agentCategoryName")
    public void setAgentCategoryName(Object agentCategoryName) {
        this.agentCategoryName = agentCategoryName;
    }

    @JsonProperty("productTypeAssociationAllowed")
    public Object getProductTypeAssociationAllowed() {
        return productTypeAssociationAllowed;
    }

    @JsonProperty("productTypeAssociationAllowed")
    public void setProductTypeAssociationAllowed(Object productTypeAssociationAllowed) {
        this.productTypeAssociationAllowed = productTypeAssociationAllowed;
    }

    @JsonProperty("scheduledTransferAllowed")
    public Object getScheduledTransferAllowed() {
        return scheduledTransferAllowed;
    }

    @JsonProperty("scheduledTransferAllowed")
    public void setScheduledTransferAllowed(Object scheduledTransferAllowed) {
        this.scheduledTransferAllowed = scheduledTransferAllowed;
    }

    @JsonProperty("txnOutsideHierchy")
    public Object getTxnOutsideHierchy() {
        return txnOutsideHierchy;
    }

    @JsonProperty("txnOutsideHierchy")
    public void setTxnOutsideHierchy(Object txnOutsideHierchy) {
        this.txnOutsideHierchy = txnOutsideHierchy;
    }

    @JsonProperty("unctrlTransferAllowed")
    public String getUnctrlTransferAllowed() {
        return unctrlTransferAllowed;
    }

    @JsonProperty("unctrlTransferAllowed")
    public void setUnctrlTransferAllowed(String unctrlTransferAllowed) {
        this.unctrlTransferAllowed = unctrlTransferAllowed;
    }

    @JsonProperty("agentCategoryStatus")
    public Object getAgentCategoryStatus() {
        return agentCategoryStatus;
    }

    @JsonProperty("agentCategoryStatus")
    public void setAgentCategoryStatus(Object agentCategoryStatus) {
        this.agentCategoryStatus = agentCategoryStatus;
    }

    @JsonProperty("agentCategoryStatusList")
    public Object getAgentCategoryStatusList() {
        return agentCategoryStatusList;
    }

    @JsonProperty("agentCategoryStatusList")
    public void setAgentCategoryStatusList(Object agentCategoryStatusList) {
        this.agentCategoryStatusList = agentCategoryStatusList;
    }

    @JsonProperty("viewOnNetworkBlock")
    public Object getViewOnNetworkBlock() {
        return viewOnNetworkBlock;
    }

    @JsonProperty("viewOnNetworkBlock")
    public void setViewOnNetworkBlock(Object viewOnNetworkBlock) {
        this.viewOnNetworkBlock = viewOnNetworkBlock;
    }

    @JsonProperty("agentCategoryType")
    public Object getAgentCategoryType() {
        return agentCategoryType;
    }

    @JsonProperty("agentCategoryType")
    public void setAgentCategoryType(Object agentCategoryType) {
        this.agentCategoryType = agentCategoryType;
    }

    @JsonProperty("agentCategoryCode")
    public Object getAgentCategoryCode() {
        return agentCategoryCode;
    }

    @JsonProperty("agentCategoryCode")
    public void setAgentCategoryCode(Object agentCategoryCode) {
        this.agentCategoryCode = agentCategoryCode;
    }

    @JsonProperty("agentMaxTxnMsisdn")
    public Object getAgentMaxTxnMsisdn() {
        return agentMaxTxnMsisdn;
    }

    @JsonProperty("agentMaxTxnMsisdn")
    public void setAgentMaxTxnMsisdn(Object agentMaxTxnMsisdn) {
        this.agentMaxTxnMsisdn = agentMaxTxnMsisdn;
    }

    @JsonProperty("agentGatewayType")
    public Object getAgentGatewayType() {
        return agentGatewayType;
    }

    @JsonProperty("agentGatewayType")
    public void setAgentGatewayType(Object agentGatewayType) {
        this.agentGatewayType = agentGatewayType;
    }

    @JsonProperty("agentDomainCodeforCategory")
    public Object getAgentDomainCodeforCategory() {
        return agentDomainCodeforCategory;
    }

    @JsonProperty("agentDomainCodeforCategory")
    public void setAgentDomainCodeforCategory(Object agentDomainCodeforCategory) {
        this.agentDomainCodeforCategory = agentDomainCodeforCategory;
    }

    @JsonProperty("agentMultipleGrphDomains")
    public Object getAgentMultipleGrphDomains() {
        return agentMultipleGrphDomains;
    }

    @JsonProperty("agentMultipleGrphDomains")
    public void setAgentMultipleGrphDomains(Object agentMultipleGrphDomains) {
        this.agentMultipleGrphDomains = agentMultipleGrphDomains;
    }

    @JsonProperty("agentModifyAllowed")
    public Object getAgentModifyAllowed() {
        return agentModifyAllowed;
    }

    @JsonProperty("agentModifyAllowed")
    public void setAgentModifyAllowed(Object agentModifyAllowed) {
        this.agentModifyAllowed = agentModifyAllowed;
    }

    @JsonProperty("agentFixedRoles")
    public Object getAgentFixedRoles() {
        return agentFixedRoles;
    }

    @JsonProperty("agentFixedRoles")
    public void setAgentFixedRoles(Object agentFixedRoles) {
        this.agentFixedRoles = agentFixedRoles;
    }

    @JsonProperty("agentGatewayName")
    public Object getAgentGatewayName() {
        return agentGatewayName;
    }

    @JsonProperty("agentGatewayName")
    public void setAgentGatewayName(Object agentGatewayName) {
        this.agentGatewayName = agentGatewayName;
    }

    @JsonProperty("agentGrphDomainType")
    public Object getAgentGrphDomainType() {
        return agentGrphDomainType;
    }

    @JsonProperty("agentGrphDomainType")
    public void setAgentGrphDomainType(Object agentGrphDomainType) {
        this.agentGrphDomainType = agentGrphDomainType;
    }

    @JsonProperty("agentMaxLoginCount")
    public Long getAgentMaxLoginCount() {
        return agentMaxLoginCount;
    }

    @JsonProperty("agentMaxLoginCount")
    public void setAgentMaxLoginCount(Long agentMaxLoginCount) {
        this.agentMaxLoginCount = agentMaxLoginCount;
    }

    @JsonProperty("agentHierarchyAllowed")
    public Object getAgentHierarchyAllowed() {
        return agentHierarchyAllowed;
    }

    @JsonProperty("agentHierarchyAllowed")
    public void setAgentHierarchyAllowed(Object agentHierarchyAllowed) {
        this.agentHierarchyAllowed = agentHierarchyAllowed;
    }

    @JsonProperty("agentGeographicalDomainList")
    public Object getAgentGeographicalDomainList() {
        return agentGeographicalDomainList;
    }

    @JsonProperty("agentGeographicalDomainList")
    public void setAgentGeographicalDomainList(Object agentGeographicalDomainList) {
        this.agentGeographicalDomainList = agentGeographicalDomainList;
    }

    @JsonProperty("agentMessageGatewayTypeList")
    public Object getAgentMessageGatewayTypeList() {
        return agentMessageGatewayTypeList;
    }

    @JsonProperty("agentMessageGatewayTypeList")
    public void setAgentMessageGatewayTypeList(Object agentMessageGatewayTypeList) {
        this.agentMessageGatewayTypeList = agentMessageGatewayTypeList;
    }

    @JsonProperty("agentDomainName")
    public Object getAgentDomainName() {
        return agentDomainName;
    }

    @JsonProperty("agentDomainName")
    public void setAgentDomainName(Object agentDomainName) {
        this.agentDomainName = agentDomainName;
    }

    @JsonProperty("hierarchyAllowed")
    public Object getHierarchyAllowed() {
        return hierarchyAllowed;
    }

    @JsonProperty("hierarchyAllowed")
    public void setHierarchyAllowed(Object hierarchyAllowed) {
        this.hierarchyAllowed = hierarchyAllowed;
    }

    @JsonProperty("transferToListOnly")
    public String getTransferToListOnly() {
        return transferToListOnly;
    }

    @JsonProperty("transferToListOnly")
    public void setTransferToListOnly(String transferToListOnly) {
        this.transferToListOnly = transferToListOnly;
    }

    @JsonProperty("restrictedMsisdns")
    public String getRestrictedMsisdns() {
        return restrictedMsisdns;
    }

    @JsonProperty("restrictedMsisdns")
    public void setRestrictedMsisdns(String restrictedMsisdns) {
        this.restrictedMsisdns = restrictedMsisdns;
    }

    @JsonProperty("maxTxnMsisdnInt")
    public Long getMaxTxnMsisdnInt() {
        return maxTxnMsisdnInt;
    }

    @JsonProperty("maxTxnMsisdnInt")
    public void setMaxTxnMsisdnInt(Long maxTxnMsisdnInt) {
        this.maxTxnMsisdnInt = maxTxnMsisdnInt;
    }

    @JsonProperty("grphDomainSequenceNo")
    public Long getGrphDomainSequenceNo() {
        return grphDomainSequenceNo;
    }

    @JsonProperty("grphDomainSequenceNo")
    public void setGrphDomainSequenceNo(Long grphDomainSequenceNo) {
        this.grphDomainSequenceNo = grphDomainSequenceNo;
    }

    @JsonProperty("categoryName")
    public String getCategoryName() {
        return categoryName;
    }

    @JsonProperty("categoryName")
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("domainAllowed")
    public Object getDomainAllowed() {
        return domainAllowed;
    }

    @JsonProperty("domainAllowed")
    public void setDomainAllowed(Object domainAllowed) {
        this.domainAllowed = domainAllowed;
    }

    @JsonProperty("categoryCode")
    public String getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @JsonProperty("fixedDomains")
    public Object getFixedDomains() {
        return fixedDomains;
    }

    @JsonProperty("fixedDomains")
    public void setFixedDomains(Object fixedDomains) {
        this.fixedDomains = fixedDomains;
    }

    @JsonProperty("modifyAllowed")
    public Object getModifyAllowed() {
        return modifyAllowed;
    }

    @JsonProperty("modifyAllowed")
    public void setModifyAllowed(Object modifyAllowed) {
        this.modifyAllowed = modifyAllowed;
    }

    @JsonProperty("domainTypeCode")
    public String getDomainTypeCode() {
        return domainTypeCode;
    }

    @JsonProperty("domainTypeCode")
    public void setDomainTypeCode(String domainTypeCode) {
        this.domainTypeCode = domainTypeCode;
    }

    @JsonProperty("grphDomainTypeName")
    public Object getGrphDomainTypeName() {
        return grphDomainTypeName;
    }

    @JsonProperty("grphDomainTypeName")
    public void setGrphDomainTypeName(Object grphDomainTypeName) {
        this.grphDomainTypeName = grphDomainTypeName;
    }

    @JsonProperty("multipleLoginAllowed")
    public String getMultipleLoginAllowed() {
        return multipleLoginAllowed;
    }

    @JsonProperty("multipleLoginAllowed")
    public void setMultipleLoginAllowed(String multipleLoginAllowed) {
        this.multipleLoginAllowed = multipleLoginAllowed;
    }

    @JsonProperty("recordCount")
    public Long getRecordCount() {
        return recordCount;
    }

    @JsonProperty("recordCount")
    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    @JsonProperty("categoryType")
    public String getCategoryType() {
        return categoryType;
    }

    @JsonProperty("categoryType")
    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    @JsonProperty("agentModifiedMessageGatewayTypeList")
    public Object getAgentModifiedMessageGatewayTypeList() {
        return agentModifiedMessageGatewayTypeList;
    }

    @JsonProperty("agentModifiedMessageGatewayTypeList")
    public void setAgentModifiedMessageGatewayTypeList(Object agentModifiedMessageGatewayTypeList) {
        this.agentModifiedMessageGatewayTypeList = agentModifiedMessageGatewayTypeList;
    }

    @JsonProperty("agentProductTypeAssociationAllowed")
    public Object getAgentProductTypeAssociationAllowed() {
        return agentProductTypeAssociationAllowed;
    }

    @JsonProperty("agentProductTypeAssociationAllowed")
    public void setAgentProductTypeAssociationAllowed(Object agentProductTypeAssociationAllowed) {
        this.agentProductTypeAssociationAllowed = agentProductTypeAssociationAllowed;
    }

    @JsonProperty("fixedRoles")
    public String getFixedRoles() {
        return fixedRoles;
    }

    @JsonProperty("fixedRoles")
    public void setFixedRoles(String fixedRoles) {
        this.fixedRoles = fixedRoles;
    }

    @JsonProperty("userIdPrefix")
    public Object getUserIdPrefix() {
        return userIdPrefix;
    }

    @JsonProperty("userIdPrefix")
    public void setUserIdPrefix(Object userIdPrefix) {
        this.userIdPrefix = userIdPrefix;
    }

    @JsonProperty("agentAllowed")
    public String getAgentAllowed() {
        return agentAllowed;
    }

    @JsonProperty("agentAllowed")
    public void setAgentAllowed(String agentAllowed) {
        this.agentAllowed = agentAllowed;
    }

    @JsonProperty("maxTxnMsisdn")
    public String getMaxTxnMsisdn() {
        return maxTxnMsisdn;
    }

    @JsonProperty("maxTxnMsisdn")
    public void setMaxTxnMsisdn(String maxTxnMsisdn) {
        this.maxTxnMsisdn = maxTxnMsisdn;
    }

    @JsonProperty("agentRoleName")
    public Object getAgentRoleName() {
        return agentRoleName;
    }

    @JsonProperty("agentRoleName")
    public void setAgentRoleName(Object agentRoleName) {
        this.agentRoleName = agentRoleName;
    }

    @JsonProperty("cp2pPayer")
    public Object getCp2pPayer() {
        return cp2pPayer;
    }

    @JsonProperty("cp2pPayer")
    public void setCp2pPayer(Object cp2pPayer) {
        this.cp2pPayer = cp2pPayer;
    }

    @JsonProperty("cp2pPayee")
    public Object getCp2pPayee() {
        return cp2pPayee;
    }

    @JsonProperty("cp2pPayee")
    public void setCp2pPayee(Object cp2pPayee) {
        this.cp2pPayee = cp2pPayee;
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
