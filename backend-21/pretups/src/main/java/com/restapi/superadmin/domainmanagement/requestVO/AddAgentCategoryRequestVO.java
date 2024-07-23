package com.restapi.superadmin.domainmanagement.requestVO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAgentCategoryRequestVO {
	private String domainTypeCode;
	private String domainCodeforDomain ;
	private String categoryCode;
    private String agentDomainName;
	private String agentCategoryName;
	private String agentFixedRoles;
	private String agentGrphDomainType;
	private String agentUserIdPrefix;
	private String agentOutletsAllowed;
	private String agentHierarchyAllowed;
	private String[] agentRoleFlag;// store the role codes that are assigned to
	// the use
	private String agentCheckArray[];
	private String agentMultipleLoginAllowed;
	private String agentScheduledTransferAllowed;
	private String agentUnctrlTransferAllowed;
	private String agentRestrictedMsisdns;
	private String agentServiceAllowed;
	private String agentViewOnNetworkBlock;
	private String lowBalanceAlertAllow;
	private String agentMaxTxnMsisdn;
	private String agentMaxLoginCount;
	private String agentTransferToListOnly;
	private String agentRechargeByParentOnly;

	


	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String agentAllowedFlag;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String agentMultipleGrphDomains;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String agentCategoryStatus;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String agentDisplayAllowed;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String agentModifyAllowed;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String agentProductTypeAssociationAllowed;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private long lastModifiedTime;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String agentCp2pPayer;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String agentCp2pPayee;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String agentCp2pWithinList;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String agentListLevelCode;

}
