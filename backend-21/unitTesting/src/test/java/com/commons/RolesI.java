package com.commons;

public class RolesI {

	//Modules Role Codes
		public static final String GEOGRAPHICAL_DOMAIN_MANAGEMENT_ROLECODE = "VIEWGRPHDOMAIN";
		public static final String DIVISION_DEPARTMENT_ROLECODE = "DIVISIONMGMT";
		public static final String GRADE_MANAGEMENT_ROLECODE = "GRADEMGMT";
		public static final String CATEGORY_TCP_ROLECODE = "TRANSFERPROFCAT";
		public static final String CHANNEL_TCP_ROLECODE = "TRFCNTPROFILE";
		public static final String COMMISSION_PROFILE_ROLECODE = "COMMPROFILEMGMT";
		public static final String ADD_CHANNEL_USER_ROLECODE = "ADDCUSER";
		public static final String LEVEL1_CHANNEL_USER_APPROVAL_ROLECODE = "APPROVALUSER";
		public static final String LEVEL2_CHANNEL_USER_APPROVAL_ROLECODE = "APPROVALUSER2";
		public static final String NETWORK_STOCK_INITIATE_ROLECODE = "ININWSTOCK";
		public static final String NETWORK_STOCK_DEDUCTION_ROLECODE = "INNWSTKREV";
		public static final String NETWORK_STOCK_DEDUCTION_APPROVAL_ROLECODE = "NWSTKDDUCAVL";
		public static final String NETWORK_STOCK_APPROVAL_LEVEL1_ROLECODE = "NWLEVL1APPROVAL";
		public static final String NETWORK_STOCK_APPROVAL_LEVEL2_ROLECODE = "NWLEVL2APPROVAL";
		public static final String C2S_CARD_GROUP_CREATION_ROLECODE = "ADDC2SCARDGRP";
		public static final String C2S_CARD_GROUP_MODIFY_ROLECODE = "EDITC2SCARDGRP";
		public static final String C2S_CARD_GROUP_VIEW_ROLECODE = "VIEWC2SCARDGRP";
		public static final String C2S_CARD_GROUP_STATUS_ROLECODE = "SUSC2SCARDGRP";
		public static final String C2S_CARD_GROUP_DEFAULT_ROLECODE = "DFLTC2SCARDGRP";
		public static final String CHANGEPIN_ROLECODE = "CHANGEPIN";
		public static final String INITIATE_O2C_TRANSFER_ROLECODE = "INITO2CTRF";
		public static final String O2C_APPROVAL_LEVEL1 = "APV1O2CTRF";
		public static final String O2C_APPROVAL_LEVEL2 = "APV2O2CTRF";
		public static final String O2C_APPROVAL_LEVEL3 = "APV3O2CTRF";
		public static final String INITIATE_FOC_TRANSFER_ROLECODE = "FOCINIT";
		public static final String FOC_APPROVAL_LEVEL1 = "FOCAPPROVE1";
		public static final String FOC_APPROVAL_LEVEL2 = "FOCAPPROVE2";
		public static final String FOC_APPROVAL_LEVEL3 = "FOCAPPROVE3";
		public static final String ADD_C2S_TRANSFER_RULE_ROLECODE = "ADDC2STRFRULES";
		public static final String UPDATE_CACHE_ROLECODE = "CACHEUPDATE";
		public static final String CHANNEL_USER_PIN_MGMT_ROLECODE="C2SUNBLOCKPIN";
		public static final String CHANGESELFPIN_ROLECODE = "CHANGESELFPIN";
		public static final String USER_PIN_PASSWORDMGMT_ROLECODE="C2SUNBLOCKPAS";
		public static final String SUSPEND_CHANNEL_USER_ROLECODE = "SUSPENDCUSER";
		public static final String VIEW_STOCK_TRANSACTIONS_ROLECODE = "VIEWSTOCKTXNS";
		public static final String VIEW_CURRENT_STOCK_ROLECODE = "VIEWCURRENTSTOCK";
		public static final String O2C_WITHDRAWAL_ROLECODE = "O2CWITHDRAW";
		public static final String C2C_TRANSFER_RULE_ROLECODE = "INITRFRULE";
		public static final String RESUME_CHANNEL_USER_ROLECODE = "RESUMECUSER";
		public static final String APPROVESUSPEND_CHANNEL_USER_ROLECODE = "APPSUSPENDCUSER";
		public static final String ADD_P2P_TRANSFER_RULE_ROLECODE = "ADDTRFRULES";
		public static final String MODIFY_P2P_TRANSFER_RULE_ROLECODE = "MODTRFRULES";
		public static final String VIEW_P2P_TRANSFER_RULE_ROLECODE = "VIEWTRFRULES";
		public static final String DELETE_CHANNEL_USER = "DELETECUSER";
		public static final String APPROVAL_DELETE_CHANNEL_USER = "APPDELETECUSER";
		public static final String VIEW_SELF_DETAILS_OPERATOR="VIEWUSERSELF";
		public static final String O2C_TRANSFERS_ENQUIRY_ROLECODE = "VIEWTRF";
		public static final String C2C_TRANSFERS_ENQUIRY_ROLECODE = "C2CTRFENQ";
		public static final String C2S_TRANSFERS_ENQUIRY_ROLECODE = "C2STRFANSFERENQ";
		public static final String VIEW_CHANNEL_USERS_ENQUIRY_ROLECODE = "VIEWCUSER";
		public static final String USER_BALANCES_ENQUIRY_ROLECODE = "OTHERBALANCE";
		public static final String MODIFY_CHANNEL_USER_ROLECODE = "EDITCUSER";
		public static final String BAR_USER ="BARUSER";
		public static final String UNBAR_USER ="UNBARUSER";
		public static final String P2P_CARD_GROUP_CREATION_ROLECODE = "ADDCARDGRP";
		public static final String VOUCHER_CARD_GROUP_CREATION_ROLECODE = "VMSADDCARDGRP";
		public static final String P2P_CARD_GROUP_MODIFY_ROLECODE = "EDITCARDGRP";
		public static final String VOUCHER_CARD_GROUP_MODIFY_ROLECODE = "VMSEDITCARDGRP";
		public static final String P2P_CARD_GROUP_VIEW_ROLECODE = "VIEWCARDGRP";
		public static final String VOUCHER_CARD_GROUP_VIEW_ROLECODE = "VMSVIEWCARDGRP";
		public static final String P2P_CARD_GROUP_STATUS_ROLECODE = "SUSCARDGRP";
		public static final String VOUCHER_CARD_GROUP_STATUS_ROLECODE = "VMSSUSCARDGRP";
		public static final String P2P_CARD_GROUP_DEFAULT_ROLECODE = "DFLTP2PCARDGRP";
		public static final String VOUCHER_CARD_GROUP_DEFAULT_ROLECODE = "VMSDFLTP2PCARDGRP";
		public static final String TRANSACTION_REVERSE ="ROLERTRX";
		public static final String ADD_PROMOTIONAL_TRANSFER_RULE = "ADDPROMTRFRULE"; 
		public static final String MODIFY_PROMOTIONAL_TRANSFER_RULE = "MODPROMTRFRULE"; 
		public static final String VIEW_PROMOTIONAL_TRANSFER_RULE = "VIWPROMTRFRULE"; 
		public static final String C2S_RECONCILIATION = "C2SRECONOPT"; 
		public static final String O2C_RECONCILIATION = "RECO2CTRF"; 
		public static final String P2P_RECONCILIATION = "P2PRECONOPT";
		public static final String PRIVATE_RECH_REG = "PRIVATERECHREG";
		public static final String PRIVATE_RECH_MOD = "PRIVATERECHMOD";
		public static final String PRIVATE_RECH_DEACTIVATION = "PRIVATERECHDEL";
		public static final String PRIVATE_RECH_ENQUIRY = "PRIVATERECHENQ";
		public static final String MODIFY_SYSTEM_PRF = "MODSYSTEMPREF";
		public static final String INITIATE_AUTO_O2C_TRANSFER_ROLECODE = "AUTOO2C";
		public static final String AUTO_O2C_APPROVAL1_ROLECODE = "AUTOO2CAP1";
		public static final String AUTO_O2C_APPROVAL2_ROLECODE = "AUTOO2CAP2";
		public static final String AUTO_O2C_APPROVAL3_ROLECODE = "AUTOO2CAP3";
		public static final String MOD_C2S_TRF_RULES = "MODC2STRFRULES";
		public static final String VIEW_C2S_TRF_RULES = "VIEWC2STRFRULES";
		public static final String ADD_STAFF_USER_ROLECODE = "ADDCSTF";
		public static final String STAFF_USER_APPROVAL1_ROLECODE = "APPRSTAFFUSR";
		public static final String STAFF_USER_APPROVAL2_ROLECODE = "APPRSTAFFUSR2";
		public static final String ADDSUBLOOKUP = "ADDSUBLOOKUP";
		public static final String INTERFACE = "INTERFACE" ;
		public static final String GROUPROLE = "GROUPROLE" ;
		public static final String USERSTATUS = "USERSTATUS" ;
		public static final String O2C_TRANSFER_DETAILS = "O2CTRFDETRPT" ;
		public static final String VIEWNETWORK = "VIEWNETWORK";
		public static final String ADDSERVICECLASS = "ADDSERVICECLASS";
		public static final String DOMAINMGMT = "DOMAINMGMT";
		public static final String CATEGORYMGMT = "CATEGORYMGMT";
		public static final String CHANNELDOMAINMGMT = "CHDOMAINMGMT";
		public static final String CHANNELCATGRYMGMT = "CATGRYMGMT";
		public static final String MESSAGEGATEWAY = "MESSAGEGATEWAY";
		public static final String MODMESSAGEGATEWAY = "MODMESSAGEGATEWAY";
		public static final String MESSAGEGATMAPPING = "MESSAGEGATMAPPING";
		public static final String USER_CLOSING_BALANCE = "UBALCLRPT";
		public static final String C2C_TRANSFER_DETAILS = "CHNLSTFRPT";
		public static final String ADDITIONAL_COMMN_DETAIL = "ADDNLCOMPRLRPT";
		public static final String CONTROL_PREFERENCE = "MODCONTROLPREF";
		public static final String NETWORK_PREFERENCE = "MODNETWORKPREF";
		public static final String SYSTEM_PREFERENCE = "MODSYSTEMPREF";
		public static final String AUTO_C2C_CREDIT_LIMIT_ROLECODE = "AUTOC2CCRLMT";
		public static final String AUTO_O2C_CREDIT_LIMIT_ROLECODE="AUTOO2CCRLMT";
		public static final String USER_BALANCE_MOVEMENT_SUMMARY = "USERBALMOV";
		public static final String ADDITIONAL_COMMN_SUMMARY = "ADNLCOMSMRYRPT";
		public static final String ZERO_BAL_SUMMARY = "ZBALCOUNTERSUM" ;
		public static final String ZERO_BALANCE_COUNTER_DETAILS = "ZBALCOUNTERDET" ;
		public static final String C2S_TRANSFER = "C2STRCSRPT" ;
		public static final String O2C_TRANSFER_ACKNOWLEDGEMENT = "O2CTRANSFERACK" ;
		public static final String OPERATION_SUMMARY_REPORT = "OPTSUMMRPT";
        public static final String EXT_USER_REPRT = "ASSNCHUSERROLES" ;
        public static final String STAFF_SELF_REPRT ="SELFSTAFFC2CRPT";
        public static final String CHANGE_NOTIFICATION_LANGUAGE = "CHNOTLAG";
        public static final String C2SRECHARGE = "C2SRECHARGE";
        public static final String C2SRECHARGE_REVAMP = "R_RECHARGE";
        public static final String ASSOCIATE_PROFILE = "ASSCUSR";
        public static final String C2CTRF_ROLECODE ="C2CTRF";
		public static final String C2CTB_ROLECODE ="BC2CAPPROVE";
		public static final String C2CVOUCHERTRF_ROLECODE ="C2CVINI";
        public static final String C2CVOUCHERTRF_APPROVAL1_ROLECODE ="C2CVCTRFAPR1";
        public static final String C2CVOUCHERTRF_APPROVAL2_ROLECODE ="C2CVCTRFAPR2";
        public static final String C2CVOUCHERTRF_APPROVAL3_ROLECODE ="C2CVCTRFAPR3";
        public static final String O2CINIT_ROLECODE = "O2CINIT";
        public static final String C2CWDL_ROLECODE = "C2CWDL";
        public static final String C2CRETURN = "C2CRETURN";
        public static final String CCRPTLMS="CCRPTLMS";
		public static final String LMSREDRPT = "LMSREDRPT";
		public static final String MODNETWORKPREF = "MODNETWORKPREF";
	    public static final String ADD_VOUCHER_DENOMINATION = "VOMSADDCAT";
	    public static final String ADD_VOUCHER_PROFILE = "VOMSADDPROF";
	    public static final String ADD_ACTIVE_VOUCHER_PROFILE = "VOMSADACTPR";
	    public static final String VOMS_ORDER_INITIATION = "INITVOMS";
	    public static final String VOMS_ORDER_APPROVAL1 = "APP1VOMS";
	    public static final String VOMS_ORDER_APPROVAL2 = "APP2VOMS";
	    public static final String VOMS_ORDER_APPROVAL3 = "APP3VOMS";
	    public static final String CREATE_BATCH_FOR_VOUCHER_DOWNLOAD = "ENBLVOUCHER";
	    public static final String VOMS_VOUCHER_DOWNLOAD = "DOWNVOMS";
	    public static final String CHANGE_OTHER_STATUS = "VOMSOTCHGSTATUS";
	    public static final String VOMS_BURN_RATE_INDICATOR = "VOMSBURNRATE";
	    public static final String MODIFY_VOUCHER_PROFILE = "VOMSMODIPROF";
	    public static final String MODSERVICEPREF = "MODSERVICEPREF";
	    public static final String VIEW_SELF_DETAILS = "VIEWCUSERSELF";
	    public static final String VIEW_VOUCHER_DENOMINATION = "VOMSVWDEN"; 
	    public static final String MODIFY_VOUCHER_DENOMINATION = "VOMSMODDENO"; 
	    public static final String VIEW_ACTIVE_VOUCHER_PROFILEE="VOMSVIEWACT";
	    public static final String MODIFY_ACTIVE_VOUCHER_PROFILEE="VOMSMODACTPR";
	    public static final String VIEW_VOUCHER_BATCH_LIST="VIEWBATCHLIST";
	    public static final String RESTRICTED_LIST_UPLOAD="BLKREGSUB";
	    public static final String DEASSOCIATE_SUBSCRIBER="DEASSOCIATERESTMSISD";
	    public static final String DELETE_RESTRICTED_MSISDN="DELRESSUB";
	    public static final String APPROVE_RESTRICTED_MSISDN="RESTAPPROVEMSISDN";
	    public static final String SUSPEND_SUBSCRIBERS="SUSPSUBS";
	    public static final String VIEW_RESTRICTED_MSISDNS="VIEWRESTRICTMSISDN";
	    public static final String UNBLOCKPIN = "UNBLOCKPIN";
	    public static final String DELETEREGSUBSCRIBER = "DELETEREGSUBSCRIBER";
	    public static final String SUSPENDSERVICE ="SUSPENDSERVICE";
	    public static final String RESUMESERVICE = "RESUMESERVICE";
	    public static final String MODSYSTEMPREF = "MODSYSTEMPREF";
	    public static final String VIEWBARREDLIST = "VIEWBARREDLIST";
		public static final String ADD_P2P_PROMOTIONAL_TRANSFER_RULE = "ADDP2PPROMTRFRULE";
		public static final String CHANNEL_USER_TRANSFER = "USERTRF";
		public static final String CHANGE_ROLE_CHANGE_USER= "CHANGEROLE";
		public static final String CHANGE_GENERATED_STATUS = "VOMSCHGSTATUS";
		public static final String CHANGE_VOUCHER_EXPIRY = "VOMSCHGVEXP";
		public static final String C2C_TRANSFER_APPROVAL1= "C2CTRFAPR1"; 
		public static final String C2C_TRANSFER_APPROVAL2= "C2CTRFAPR2"; 
		public static final String C2C_TRANSFER_APPROVAL3= "C2CTRFAPR3"; 
		public static final String C2C_VOUCHERTRACKINGREPORT= "VNDTLTR"; 
		public static final String C2C_VOUCHERAVAILBILITYREPORT= "VOUAVAREPORT"; 
		public static final String C2C_VOUCHERCONSUMREPORT= "VOUCNREPORT"; 
		public static final String VMS_BUNDLE_ADD = "VOUADDBUN";
		public static final String VMS_BUNDLE_MODIFY = "VOUMODBUN";
		public static final String C2C_REVAMP = "R_TRANSACTION";
		public static final String O2C_TRANSFER_REVAMP = "R_O2C";
		public static final String FOC_COMMISSION = "R_O2CCOMMISSION" ;
		public static final String FOC_Approval1 = "R_O2CAPPRV1" ;
		public static final String FOC_Approval2 = "R_O2CAPPRV2" ;
		public static final String FOC_Approval3 = "R_O2CAPPRV3" ;
		public static final String MULTI_CURRENCY = "ADDCURRENCY";
		public static final String O2C_RETURN_REVAMP = "R_O2C";
		public static final String CU_HOME_REVAMP = "R_HOME";
		public static final String PASSBOOK_DETAILS = "R_PASSBOOK";
		public static final String LOWTHRESHOLDTRANSACTIONREPORT_DETAILS = "R_LOWTHRESHOLDTRANSACTIONREPORT";
		public static final String PINPWDHISTORY_DETAILS = "R_PINPWDHISTORY";
		public static final String INITIATE_BATCH_O2CTRANSFER = "INITBO2C";
		public static final String APPROVE1_BATCH_O2CTRANSFER = "BTO2CAPR1";
		public static final String APPROVE2_BATCH_O2CTRANSFER = "BTO2CAPR2";
		public static final String LOAN_PROFILE = "LOANPROFILE";
		public static final String BATCH_GRADE_MANAGEMENT = "BULKGDMGT";
}
