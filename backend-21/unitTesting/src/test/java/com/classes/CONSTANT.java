package com.classes;

public class CONSTANT {

	private CONSTANT() {
		
	}
	
	public static String ONELINEC2STRANSACTIONLOGS_PATH = null;
	public static String CHANNELREQUESTDAILYLOG_PATH = null;
	
	public static String NetworkName; // The Variable is updated through Base Test Class.
	public static boolean EXCELLOGGER_STATUS; // The Variable is updated through Base Test Class.
	
	//Network Stock Constants
	public static double NETWORKSTOCKPREBALANCES[] = null; // Array to fetch Pre Balance of Products during Network Stock Initiation
	public static double NETWORKSTOCKPOSTBALANCES[] = null; // Array to fetch Post Balance of Products during Network Stock Initiation
	
	//Channel User Variable for Error Message validation during Assign Phone Number
	public static String CU_ASSIGNPHONENO_ERR = null;
	public static String CARDGROUP_SLAB_ERR = null;
	public static String COMM_SLAB_ERR = null;
	public static String ADDCOMM_SLAB_ERR = null;
	
	
	//Preference Code Constants
	public static final String MULTIWALLET_SYSTEM_STATUS = "MULTIPLE_WALLET_APPLY"; // System Preference Code for Multi Wallet Status
	public static final String NETWORK_STOCK_REQUEST_LIMIT = "CIRCLEMAXLMT"; // Preference to fetch Network Stock Maximum Transfer Limit
	public static final String NETWORK_STOCK_FIRSTAPPROVAL_LIMIT = "FRSTAPPLM"; // Prefrence to fetch Network Stock First Approval Limit
	public static final String CHOICE_RECHARGE_STATUS = "CHOICE_RECHARGE_APPLICABLE"; // System Preference to check default status for Choice Recharge
	public static final String REQ_CUSER_SUS_APP = "REQ_CUSER_SUS_APP"; // System Preference to check if Approval is required to Suspend a Channel User
	public static final String PRVT_RC_MSISDN_PREFIX_LIST = "PRVT_RC_MSISDN_PREFIX_LIST"; // System Preference for List of SID prefixes for private recharge, comma separated
	public static final String MAX_SID_LENGTH = "MAX_SID_LENGTH"; //maximum SID length
	public static final String MRP_BLOCK_TIME_ALLOWED = "MRP_BLOCK_TIME_ALLOWED";
	public static final String AUTO_NWSTK_CRTN_THRESHOLD = "AUTO_NWSTK_CRTN_THRESHOLD";
	public static final String SUCC_BLOCK_TIME = "SUCC_BLOCK_TIME";
	public static final String SID_ENCRYPTION_ALLOWED ="SID_ENCRYPTION_ALLOWED";
	public static final String C2S_MAX_PIN_BLK_CONT = "C2S_MAX_PIN_BLK_CONT";
	public static final String PIN_LENGTH = "PIN_LENGTH";
	public static final String USER_EVENT_REMARKS="USER_EVENT_REMARKS";
	public static final String MAX_SMS_PIN_LENGTH="MAX_SMS_PIN_LENGTH";
	public static final String TRF_RULE_USER_LEVEL_ALLOW="TRF_RULE_USER_LEVEL_ALLOW";
	public static final String GROUP_ROLE_ALLOWED="GROUP_ROLE_ALLOWED";
	public static final String CHANNEL_USER_ROLE_TYPE_DISPLAY="CHANNEL_USER_ROLE_TYPE_DISPLAY";
	public static final String USERWISE_LOAN_ENABLE="USERWISE_LOAN_ENABLE";
	public static final String SYSTEM_ROLE_ALLOWED="SYSTEM_ROLE_ALLOWED";
	public static final String MIN_SMS_PIN_LENGTH = "MIN_SMS_PIN_LENGTH";
	public static final String DISSABLE_BUTTON_LIST = "DISSABLE_BUTTON_LIST";
	public static final String MAXTRNSFR  = "MAXTRNSFR";
	public static final String AUTO_NWSTK_CRTN_ALWD = "AUTO_NWSTK_CRTN_ALWD";
	public static final String VMS_AUTO_VOUCHER_CRTN_ALWD  = "VMS_AUTO_VOUCHER_CRTN_ALWD";
	public static final String DOWNLD_BATCH_BY_BATCHID  = "DOWNLD_BATCH_BY_BATCHID";
	public static final String VOMS_PROFILE_DEF_MINMAXQTY  = "VOMS_PROFILE_DEF_MINMAXQTY";
	public static final String VOMS_PROFILE_MIN_REORDERQTY="VOMS_PROFILE_MIN_REORDERQTY";
	public static final String VOMS_PROF_TALKTIME_MANDATORY = "VOMS_PROF_TALKTIME_MANDATORY";
	public static final String VOMS_PROF_VALIDITY_MANDATORY = "VOMS_PROF_VALIDITY_MANDATORY";
	public static final String CALENDAR_TYPE = "CALENDAR_TYPE";
	public static final String DATE_FORMAT_CAL_JAVA = "DATE_FORMAT_CAL_JAVA";
	public static final String SYSTEM_DATE_FORMAT = "SYSTEM_DATE_FORMAT";
	public static final String USER_VOUCHERTYPE_ALLOWED  = "USER_VOUCHERTYPE_ALLOWED";
	public static final String DYS_AFTER_CHANGE_PWD = "DYS_AFTER_CHANGE_PWD";
	public static final String C2S_DEFAULT_SMSPIN="C2S_DEFAULT_SMSPIN";
	public static final String C2S_DEFAULT_PASSWORD="C2S_DEFAULT_PASSWORD";
	public static final String P2P_PROMO_TRF_APP = "P2P_PROMO_TRF_APP";
	public static final String P2P_PRO_TRF_ST_LVL_CODE = "P2P_PRO_TRF_ST_LVL_CODE";
	public static final String TARGET_BASED_BASE_COMMISSION = "TARGET_BASED_BASE_COMMISSION"; 
    public static final String TARGET_BASED_BASE_COMMISSION_SLABS = "TARGET_BASED_BASE_COMMISSION_SLABS";
	public static final String TRANSACTION_TYPE = "TRANSACTION_TYPE";
	public static final String PAYMENT_MODE_ALWD = "PAYMENT_MODE_ALWD";
	public static final String PINPAS_EN_DE_CRYPTION_TYPE = "PINPAS_EN_DE_CRYPTION_TYPE";
	public static final String AUTO_PWD_GENERATE_ALLOW="AUTO_PWD_GENERATE_ALLOW";
	public static final String AUTO_PIN_GENERATE_ALLOW = "AUTO_PIN_GENERATE_ALLOW";
	public static final String VMSPIN_EN_DE_CRYPTION_TYPE = "VMSPIN_EN_DE_CRYPTION_TYPE";
    public static final String VOMS_PIN_MAX_LENGTH = "VOMS_PIN_MAX_LENGTH";
    public static final String VOMS_SNO_MAX_LENGTH = "VOMS_SNO_MAX_LENGTH";
	public static final String ONLINE_DVD_LIMIT = "ONLINE_DVD_LIMIT";
    
    public static Object[][] USERACCESSDAO;
    public static Object[][] USERACCESSDAOREVAMP;	
	
	public static int COMM_SLAB_COUNT = 0;
	public static int ADDCOMM_SLAB_COUNT = 0;
	
	//DB Table and Column Names
	public static final String TRANSFER_PROFILE="transfer_profile";
	public static final String IS_DEFAULT="is_default";
	public static final String PROFILE_ID="profile_id";
	public static final String COMM_PROFILE_SET_ID="comm_profile_set_id";
	public static final String COMMISSION_PROFILE_SET="commission_profile_set";
	public static final String CHANNEL_GRADES="channel_grades";
	public static final String GRADE_CODE="grade_code";
	public static final String IS_DEFAULT_GRADE="is_default_grade";
	public static final String LOGIN_ID="login_id";
	public static final String PASSWORD = "password";
	public static final String USERS="users";
	public static final String USER_PHONES = "user_phones";
	public static final String PIN = "sms_pin";
	public static final String USER_ID = "user_id";
	
	//Initialize Driver options
	public static final String CHROME_OPTION_BATCH = "Batch";
	public static final String CHROME_OPTION_C2CBULKTRANSFER = "C2CBULKTRANSFER";
	public static final String CHROME_OPTION_C2CBULKWITHDRAW = "C2CBULKWITHDRAW";
	public static final String CHROME_OPTION_C2SBULKTRANSFER = "C2SBULKTRANSFER";
	public static final String CHROME_OPTION_MVD = "CHROME_OPTION_MVD";
	public static final String CHROME_OPTION_DVDBULKRECHARGE = "DVDBULKRECHARGE";
	public static final String CHROME_OPTION_BATCHO2CTRANSFER = "BATCHO2CTRANSFER";
	public static final String CHROME_OPTION_BATCHFOCTRANSFER = "BATCHFOCTRANSFER";
	public static final String CHROME_OPTION_BATCHGRADEMANAGEMENT = "BATCHGRADEMANAGEMENT";
	public static final String CHROME_OPTION_BATCHO2CWITHDRAW = "BATCHO2CWITHDRAW";


	//For Password change
	public static String CHANGING_PASSWORD = "";
}
