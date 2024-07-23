package com.btsl.pretups.common;

import com.btsl.common.TypesI;

public interface PretupsRestI extends TypesI {
	
	public static final String LOAD_CARDGROUP_SET = "LOADCARDGROUPSET";
	public static final String DEFAULT_CARDGROUP = "DFLTCARDGRP";
	public static final String SUSPEND_CARDGROUP = "SUSPENDCARDGROUP";
	public static final String VIEW_CARDGROUP_VERSIONLIST = "VIEWCARDGRPVERSION";
	public static final String VIEW_CARDGROUP = "VIEWCARDGROUP";
	public static final String ADD_CARDGROUP= "ADDCARDGROUP";
	public static final String ADD_VOUCHER_BUNDLE="ADDVOUBUN";
	public static final String MOD_VOUCHER_BUNDLE="MODVOUBUN";
	
	public static final String DELETE_CARDGROUP= "DELETECARDGROUP";
	public static final String MODIFY_CARDGROUP= "MODIFYCARDGROUP";
	public static final String CALCULATE_VOUCHER_CARDGROUP= "CALCULATEVOUCHERCARDGROUP";
	public static final String DVD = "DVD"; 
	public static final String SELF_VOUCHER_ENQUIRY = "SELFVCRENQ";
	public static final String SELF_AVLBL_VOUCHERS = "VCAVLBLREQ";
	public static final String DVD_RECEIVER = "DVDRECEIVER";
	public static final String C2C_TRF_APPROVAL_RECEIVER = "C2CAPPRRECEIVER";
	public static final String C2C_TRF_APPR="C2CTRFAPPR";
	public static final String C2C_TRF_VCR_INITIATE = "C2CTRFVCRINI";
	public static final String C2C_VOUCHER_APPROVAL = "C2CVOUCHERAPP";
	public static final String C2C_VOUCHER_APPROVAL_I = "C2CVOUCHERAPPROVAL";
	public static final String C2C_TRF_VOMS_INITIATE = "C2CVOMSINI";
	public static final String C2C_TRF_VOMS_VIEW = "C2CVIEWVC";
	public static final String C2C_TRF_APPR_ST = "C2CTRFAPPR";
	public static final String C2C_TRF_APPR_LIST_VC_ST = "C2CVCRAPPLIST";
	public static final String C2C_DOWNLOAD_FILE = "DOWNLOADFILE";
	public static final String C2C_VOMSTRF_INI = "C2CVOMSTRFINI";
	public static final String C2C_VOMSTRF = "C2CVOMSTRF";
	public static final String C2C_STOCK_TRF = "C2CTRF";
	public static final String CHANNEL_USER_DETAILS = "USRDETAILS";
	public static final String C2C_BUY_ENQUIRY = "C2CBUYUSENQ";
	public static final String COMMISSION_INCOME = "COMINCOME";
	public static final String PASSBOOKVIEW="PASBDET";

	public static final String TRANSACTIONDETAIL="TOTTRANSDETAIL";
	

	public static final String TOATLINCOMEDETAILSVIEW="USRINCVIEW";
	public static final String TRANSFERDETAILVIEW="TXNCALVIEW";
	public static final String USERPMTYPE="USRPMTYPE";
	public static final String GETDOMAINCATEGORY="GETDOMAINCATEGORY";
	public static final String USERINFO="USERINFO";
	public static final String C2CRETURN="C2CRET";
	public static final String C2CWITHDRAW="C2CWITHDRAW";
	public static final String C2CINITIATE="C2CTRFINI";
	public static final String VOUCHER_EXPIRY_CHANGE = "VEXPCH";
	public static final String VOUCHER_EXPIRY_CHANGE_BULK = "VEXCHB";
	public static final String VOUCHER_EXPIRY_CHANGE_BULK_APPROVAL = "VEXCHBA";
	
}