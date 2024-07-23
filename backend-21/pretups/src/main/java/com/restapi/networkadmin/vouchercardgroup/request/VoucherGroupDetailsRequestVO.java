package com.restapi.networkadmin.vouchercardgroup.request;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VoucherGroupDetailsRequestVO {
	private String serviceType;
	private String serviceTypeDesc;
	private String subService;
	private String subServiceDesc;
	private String cardGroupSetName;
	private String cardGroupSetType;
	private String applicableFromDate;
	private String applicableFromtime;
	private String oldApplicableFromDate;
	private String oldApplicableFromHour;
	private String defaultCardGroup;
	private String status;
	private ArrayList<VoucherGroupDetails> voucherGroupDetailsList;
}
