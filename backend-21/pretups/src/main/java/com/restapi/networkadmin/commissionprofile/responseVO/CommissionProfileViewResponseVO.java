package com.restapi.networkadmin.commissionprofile.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommissionProfileViewResponseVO extends BaseResponse {

	private String subServiceCode;
	private String defaultProfile;
	private String addtnlComStatusName;
	private String addtnlComStatus;
	private String commissionTypeValue;
	private String commissionTypeValueAsString;
	private String gatewayCode ;
	private String gradeCode;
	private String otherCommissionProfile;	
	private String otherCommissionProfileAsString;
	private String dualCommType;
	private String dualCommTypeDesc;
	private String applicableFromHour ;
	private String oldApplicableFromDate;
	private String oldApplicableFromHour;
	private String version;
	private String applicableFromDate ;
	private String profileName ;
	private String shortCode ;
	private String sequenceNo;
	private String commissionType;
	private String otherCategoryCode;
	private String commissionTypeAsString;
	private String commProfileSetVersionId;
    private boolean deleteAllowed = false;
	private String roamRecharge;

	private ArrayList slabList;
	private ArrayList additionalProfileList;
	private ArrayList commissionProfileList;
	public  ArrayList otfProfileList;

}


