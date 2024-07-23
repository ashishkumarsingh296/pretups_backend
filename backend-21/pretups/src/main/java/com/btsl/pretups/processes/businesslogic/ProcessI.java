/**
 * @(#)ProcessI.java
 *                   Name Date History
 *                   ----------------------------------------------------------
 *                   --------------
 *                   Ashish Kumar 22/04/2006 Initial Creation
 *                   ----------------------------------------------------------
 *                   --------------
 *                   Copyright (c) 2006 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.processes.businesslogic;

public interface ProcessI {

    public String STATUS_COMPLETE = "C";
    public String STATUS_UNDERPROCESS = "U";
    public String REST_SCH_TOPUP_PROCESSID = "SCHTOPUP";
    public String DWH_PROCESSID = "DWH";
    public String P2PDWH_PROCESSID = "P2PDWH";
    public String WHITE_LIST_PROCCESSID = "WLIST";
    public String C2SMIS = "C2SMIS";
    public String P2PMIS = "P2PMIS";
    public String NW_STK_MISMATCH = "NWSTKMISMATCH";
    public String CHNL_USR_BAL_MISMATCH = "CHNLUSRBALMISMATCH";
    public String VOUCHER_FILE_UPLOAD_PROCCESSID = "VOULIST";
    public String VOMS_INVENTORY_REPORT_PROCCESSID = "VMINVTRPT";
    public String VOUCHER_ALERT = "VOUCHERALERT";
    public String ROUTING_UPLOAD_PROCESSID = "ROUTUPLOAD";
    public String UBS = "UBS";
    public String DAILY_TRANSFER_PROCESSID = "DLYTRFALT";
    public String ADDITIONAL_COMMISION_DEDUCTION = "ADDCOMMDDT";
    public String HOURLY_COUNT_DETAIL_PROCESSID = "HRCNT";
    public String O2CEXT_TXN_PROCESSID = "O2CEXTTXN";
    public String CGR_PROCESSID = "CGR";
    public String ACTIVATION_BONUS_PROCESSID = "ACTBONUS";
    public String ACT_TXN_BONUS = "ACTTXN";
    public String ACT_VOLUME_BONUS = "ACTVOLUME";
    public String ACTIVATION_MAPPING = "ACTIVATIONMAPPING";
    // Added by Vinay on 20-March-09 for External User Data File
    public String EXTERAL_USRDATA_FILE_PROCESS_ID = "CRMUSRDATARPT";
    public String IAT_AMBIGUOUS_PROCESS = "IATAMG";
    public String IAT_DWH_PROCESSID = "IATDWH";
    // Process ID for Daily subscriber count and User balance movement process
    public String DAILY_SUBS_COUNT = "DLYSUBCNT";
    public String DWH_USER_BAL_MOV_PROCESSID = "USRBALMOVT";
    public String LOW_BAL_ALERT_PARENT = "LOWBALPRNT";
    public String USER_MIG_ID = "USERMIGRATION";// Process Id for UserMigration
                                                // Module : Puneet
    // added by nilesh:for auto c2c
    public String AUTO_C2C_TRANSFER_PROCESS = "AUTOC2C";
    // Added by Babu Kunwar for C2STransaction VFE
    public String TRANSACTION_ID = "RP2PTRANSDATA";

    // LMB
    public String SOS_SETTLEMENT_PROCESSID = "SOSSETTLE";
    public String SOSMIS = "SOSMIS";
    public String SOSDWH_PROCESSID = "SOSDWH";
    public String SOSVALIDITY = "SOSVALIDITY";
    public String VOMS_GEN = "VOMSGEN";

    // Added by Amit Raheja for Voucher MIS Process
    public String VMSMIS = "VMSMIS";

    // Added By Vikas Jauhari for CP User Suspension Process
    public String CPUSER_SUSPENSION_PROCESS = "CPUSRSUSP";

    // Batch Grade Management
    public String BATCH_USER_GRADE_ASSOCIATION_PROCESS_ID = "BTCUSRGDASCPRO";
    // auto O2C process

    public String Auto_O2C_Process = "AUTOO2CPROCESS";

    // for barred for deletion
    public String BAR_FOR_DELETE = "BARFORDEL";
    // LMS Promotion Process
    public String LMS_PROMO_MSG = "LMSPROMO";
    // LMS Target Credit
    public String LMS_TARGET_CREDIT = "LMSCREDIT";
    public String LMSUPLOADLP = "LMSUPLOADLP";
    public String LMSREFTCAL = "LMSREFTCAL";
    public String O2C_SEND_MAIL_PROCESS = "O2CMAILPROCESS";
    // Added By Diwakar for POS process on monthly basis
    public String POS_MIS = "POSMONTHLYREPORT";
    public String SMS_TO_CHANNEL_ADMIN_USERS_HOURLY = "SMS2CHANNELADMINUSER";
    public String SMS_TO_CHANNEL_ADMIN_USERS_TILL_YESTERDAY_FROM_START_DAY_MONTH = "SMS2CHADMUSERM";
    public String NEW_USERS_APPROVED_CSV = "NEWUSERSAPPROVED"; // As per OCI CR
    public String LMS_REF_CAL = "LMSREFTGT";
    public String SELF_TOPUP_SCHEDULED_CREDIT_TRANSFER = "SLFSCCRDTTRF";
    // added by Harsh for creation separate process id for LMS Redemption
    // Process
    public String LMS_BONUS_PROCESSID = "LMSBOUNS";

   
    
    public static final String C2S_ROAM_RECHARGE_DAILY_PROCESS_ID = "C2SROAMRCGDRPT";
    public static final String C2S_ROAM_RECHARGE_MONTH_PROCESS_ID = "C2SROAMRCGMRPT";
    
    public String LMS_OPTINOUT_PROMO_MSG="LMSOPTINOUTPROMO"; //LMS OPT IN/OUT Promotion Process Id
	public String LMS_OPTINOUT_REF_CAL="LMSOPTINOUTREFTGT"; //LMS OPT IN/OUT Reference Target calculation Process
	public String LMS_C2S_SUMMARY="LMSC2SSUMMARY";
	//addded by Ashutosh for creating separate process id for Direct Payout file upload
	public String DP_FILEUPLOADID="DPFILEUPLOAD";
	
	//Shashi:Lowbase
	public String LB_FILEUPLOADID="LBFILEUPLOAD";
	
	//added by trasha
	public String BALANCE_ALERT="BALANCE-ALERT";
	public String  BULK_USER_SUSPENSION_PROCESSID="BULKUSRSUSPN"; // BULK User Suspension Process_Status
	
	
	//added by trasha
	public String SUMMARY_ALERT="SUMMARY-ALERT";
	public String FOC_FILEUPLOADID="FOCFILEUPLOAD";
	
	//added by Vikas Chaudhary for Bulk User Deletion process
	public String  BULK_USER_DELETION_PROCESSID="BULKUSRDELTN";
	
	//added by trasha
	public String User_Movement="USER-MOVEMENT";
	
	public String MONTHLY_RED_PROCESSID = "MONREDPROC";
	//added by suhel ** Greeting Msg PRocess **
	public String GREETING_MSG = "GREETMSG";

 /* Transaction summary report*/
	public String RUN_PROCEDURE_TRNSUM = "RUNTRNSUM";
	public String LMS_TARGET_VS_ACHIEVEMENT = "LMSTARGETVSACHIEVE";
    
	public String ZB_FNF_UPLOAD_PROCESSID = "ZBFNFUPLOAD";
	//network stock deduction process - added by satakshi
	public String O2CTRFDDT_PROCESS = "O2CTRFDDT";
	
	public String VOMS_CHANGE_STATUS_PROCESS="CHANGEVOMSTAT";
	
	//messages in otf
	public String TARGET_BASED_MESSAGES_PROCESS="TARGETBASECOMMISSION";
	

	//Batch O2C Initiation added by Anjali
	public String BATCH_O2C_INITIATION="BATO2CINITN";
	//for vioucher auto generation
	public String VOMS_GEN_AUTO ="VOMSGENAUTO";
	//add by vishal to add reverse Hirerachy Commision
	public String RHCOM="RHCOM";
	public String BATCH_COMM_TRF_PROCESS="BATCOMMTRF";
	//for Voucher burn rate indicator
	public String VOMS_BURN_RATE ="VOMSBURNED";
	//added for ERP reports
     public String C2S_ERP_DETAIL_PROCESS="C2SERPDET";
	 
	//added for RIGHTEL reports
    public String VOUCHER_SALES_DAILY_REPORT_PROCCESSID = "SALESRPT";
    //added by yogesh for ambiguous transaction
 	public String AMB_SERVER_UPDATE="AMBSERVERUPDATE";
 	public String AMB_P2P_SERVER_UPDATE="AMBP2PSERVERUPDATE";
    public String VOUCHER_DAILY_SUMMARY = "VOUCHERDASUM";
    
    public String VOMSPINEXP = "VOMSPINEXP";
	public String VCHR_STATUS_OFFLN_RPT="VCHRRPT";
	public String VOMS_CPOS_NOTIFY = "VOMCPOSNOTIFY";
	public String O2C_PACKAGE_ASSOCIATION = "O2CPKGVOUASC";
	public String MO_UPLOAD_PROCESSID = "MOUPLOAD";
	public String VOUCHER_EXPIRY_CHANGE_PROCESS = "VCHREXPCHG";
	//added for Robi Retailer Loan CR
    public String LOAN_FILE_UPLOAD_PROCCESSID = "USRLOANLIST";
    public String DWHLOAN_PROCESSID = "DWHLOAN";

}
