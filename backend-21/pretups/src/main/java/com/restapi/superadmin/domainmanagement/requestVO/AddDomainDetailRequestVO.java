package com.restapi.superadmin.domainmanagement.requestVO;

import java.util.ArrayList;
import java.util.Date;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddDomainDetailRequestVO {

	private String domainName;
	private String domainTypeCode;
	private String domainCodeforDomain;
	private String numberOfCategories;
	private String categoryCode;
	private String categoryName = null;
	private String grphDomainType;
	private String userIdPrefix;
	private String agentAllowed;
	private String outletsAllowed;
	private String hierarchyAllowed;

	private String[] roleFlag;// store the role codes that are assigned to the
	// use
	private String checkArray[];
	private String multipleLoginAllowed;
	private String scheduledTransferAllowed;
	private String unctrlTransferAllowed;
	private String restrictedMsisdns;
	private String serviceAllowed;
	private String viewOnNetworkBlock;
	private String lowBalanceAlertAllow;
	private String maxTxnMsisdn;
	private String maxLoginCount;
	private String transferToListOnly;
	private String rechargeByParentOnly;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
    private String authType;
    private String fixedRoles;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String multipleGrphDomains;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String domainStatus;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private Date createdOn;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String createdBy;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private Date modifiedOn;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String modifiedBy;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private long lastModifiedTime;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String categoryStatus;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private ArrayList modifiedMessageGatewayList;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String displayAllowed;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String modifyAllowed;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String productTypeAssociationAllowed;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String parentCategoryCode;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String cp2pPayer;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String cp2pPayee;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String cp2pWithinList;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String listLevelCode;
	
}
