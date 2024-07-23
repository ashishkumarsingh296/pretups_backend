package com.commons;

public class PretupsI {
	
	public static final String SUPERADMIN_CATCODE = "SUADM";
	public static final String NETWORKADMIN_CATCODE = "NWADM";
	public static final String MONITORSERVER_CATCODE = "MONTR";
	public static final String CHANNELADMIN_CATCODE = "BCU";
	public static final String COLON = ":";
	public static final String COMMA=",";
	public static final String C2S_MODULE = "C2S";
	public static final String NETWK_PRODUCT_USAGE_BOTH = "B";
	public static final String NETWK_PRODUCT_USAGE_DISTRIBUTION = "D";
	public static final String STATUS_ACTIVE = "Y";
	public static final String BARRING_SENDER_TYPE = "SENDER";
	public static final String BARRING_RECEIVER_TYPE = "RECEIVER";
	
	public static final String SYSTEM_AMOUNT = "AMT";
	public static final String COMM_TYPE_BASECOMM="COMM";
	public static final String COMM_TYPE_ADNLCOMM = "ADNL";
	
	public static final String TRANSFER_EXTERNAL_TXN_INITIAL_LEVEL = "INITIAL";
	public static final String TRANSFER_EXTERNAL_TXN_FIRST_LEVEL = "FIRST";
	public static final String TRANSFER_EXTERNAL_TXN_SECOND_LEVEL = "SECOND";
	
	public static final String AMOUNT_TYPE_AMOUNT = "AMT";
    public static final String AMOUNT_TYPE_PERCENTAGE = "PCT";
    public static final String YES = "Y";
    public static final String TARGET_BASED_BASE_COMMISSION = "TARGET_BASED_BASE_COMMISSION";
	public static final String AMOUNT_MULT_FACTOR = "AMOUNT_MULT_FACTOR";
	
	// Lookups for Wallet Types Dropdown
	public static final String WLTYP_LOOKUP = "WLTYP"; // Lookup Type for wallet Type dropdown
	public static final String SALE_WALLET_LOOKUP = "SAL";
    public static final String FOC_WALLET_LOOKUP = "FOC";
    public static final String INCENTIVE_WALLET_LOOKUP = "INC";
    
    public static final String O2C_STOCK_TYPE_LOOKUP = "STOCK";
    public static final String O2C_VOUCHER_TYPE_LOOKUP = "VOUCHTRACK";
    public static final String O2C_DIST_MODE_LOOKUP = "PACKAGEMODE";
    public static final String O2C_DIST_MODE_LOOKUP_NORMAL = "NORMALMODE";
    
	//Lookup Codes
    public static final String STAT_LOOKUP = "STAT";
    public static final String DEF_LOOKUP = "DEF";//For Geo Domain used in Argentina
	public static final String STATUS_ACTIVE_LOOKUPS = "Y";
	public static final String STATUS_SUSPENDED_LOOKUPS = "S";
	public static final String VOMS_STATUS_LOOKUPS = "VSTAT";
	
	public static final String CARDGRP_NORMAL_LOOKUPS = "N";
	public static final String CARDGRP_PROMO_LOOKUPS = "P";
	public static final String RECHARGE_INTERFACE =  "RC";
	
	public static final String WAREHOUSE =  "WH";
	public static final String STOLEN =  "ST";
	public static final String DISABLED =  "DA";
	public static final String ONHOLD =  "OH";
	public static final String ENABLE =  "EN";
	public static final String SUSPENDED =  "S";
	public static final String GENERATED =  "GE";
	public static final String PRINT_ENABLE =  "PE";
	public static final String UNDER_PROCESS = "UP";
	public static final String EXPIRED = "EX";
	public static final String CONSUMED = "CU";
	public static final String C2C = "C2C";
	
	//C2S Services Lookups
	public static final String SCTYP_LOOKUP = "SCTYP";
	public static final String SUBTP_LOOKUP = "SUBTP";
	public static final String POSTPAID_SUB_LOOKUPS = "POST";
	public static final String PREPAID_SUB_LOOKUPS = "PRE";
	
	public static final String TRANSFER_CATEGORY_SALE = "SALE";
	
	public static final String NETWORK_STOCK_TRANSACTION_ID = "NT";	
	public static final String CHANNEL_TRANSFER_O2C_ID = "OT";
    public static final String CHANNEL_RETURN_O2C_ID = "OR";
    public static final String CHANNEL_WITHDRAW_O2C_ID = "OW";
    public static final String CHANNEL_TO_CHANNEL_TRANSFER_ID = "CT";
    public static final String CHANNEL_TO_CHANNEL_RETURN_ID = "CR";
    public static final String CHANNEL_TO_WITHDRAW_RETURN_ID = "CW";
    public static final String CHANNEL_TO_SUBSCRIBER_TRANSACTION_ID = "R";
    public static final String INTERNET_TRANSACTION_ID = "B";
    public static final String FIXLINE_TRANSACTION_ID = "F";
    
    //Promotional Transfer Rule used in Argentina
    public static final String PROMO_LOOKUP = "PROMO";
    public static final String USER_LOOKUP = "USR";
    public static final String GRADE_LOOKUP = "GRD";
    public static final String GEOGRAPHY_LOOKUP = "GRP";
    public static final String CATEGORY_LOOKUP = "CAT";
    public static final String SERVICE_LOOUP = "SUBTP";
    public static final String PREPAID_LOOKUP = "PRE";
    public static final String POSTPAID_LOOKUP = "POST";
    
    public static final String PMTYP_CASH_LOOKUP = "CASH";
    
  //C2S CardGroup Validity Lookups
  	public static final String VLTYP_LOOKUP = "VLHI";
	
	//For TCP
    public static final String PARENT_PROFILE_ID_CATEGORY = "CAT";
    
    //For SubLookUps
    
    public static final String Bonus_Comm_Type = "CBTYPE";
    
    //For Group Role 
    
    public static final String Approve_Batch_C2C = "BC2CAPPROVE";
    public static final String Approve_Batch_C2CW = "BC2CWDRAPP";
    public static final String ACCESS_CONTROL_MGMT = "C2SUNBLOCKPAS";
    public static final String C2CTRANSFER = "C2CTRF";
    
    //For Service Class Management
    
    public static final String RECHARGE_INTERFACE_TYPE = "RC:RC";
    
    //For User Status Configuration
    
    public static final String GATEWAY_TYPE_WEB = "WEB";
    public static final String GATEWAY_SUB_TYPE_WEB = "WEB:WEB";
    public static final String CHANNEL_USER_TYPE = "CHANNEL";
    public static final String HTTP_PROTOCOL = "HTTP";
    public static final String ENCRYPTION_LEVEL_USER = "USER";
    public static final String CONTENT_TYPE_PLAIN = "PLAIN";
    
    public static final String GATEWAY_TYPE_ALL = "ALL";
    public static final String GATEWAY_TYPE_EXTGW = "EXTGW";
    public static final String SERVICE_CODE_CUSTOMER_RECHARGE = "RC";
    
    //Dual Commission Type
    
    public static final String Normal_Commission = "NC";
    public static final String Postive_Commission = "PC";
    
    // For Preference Type
    
    public static final String SERVICE_CLASS_PREFERENCE_TYPE = "SVCCLSPRF";
    public static final String CONTROL_PREFERENCE_TYPE = "CATPRF";
    public static final String NETWORK_PREFERENCE_TYPE = "NETWORKPRF";
    public static final String SYSTEM_PREFERENCE_TYPE = "SYSTEMPRF";
    public static final String VOMS_MAX_APPROVAL_LEVEL = "VOMS_MAX_APPROVAL_LEVEL";
    public static final String VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN = "VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN";
    public static final String VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN = "VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN";
    public static final String VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN = "VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN";
    public static final String VOMS_SNO_MIN_LENGTH = "VOMS_SNO_MIN_LENGTH";
    public static final String VOMS_SNO_MAX_LENGTH = "VOMS_SNO_MAX_LENGTH";
    public static final String MIN_MSISDN_LENGTH = "MIN_MSISDN_LENGTH";
    public static final String MAX_MSISDN_LENGTH = "MAX_MSISDN_LENGTH";
    public static final String VOUCHER_THIRDPARTY_STATUS = "VOUCHER_THIRDPARTY_STATUS";
    public static final String PIN_REQUIRED = "PIN_REQUIRED";
    public static final String SCREEN_WISE_ALLOWED_VOUCHER_TYPE = "SCREEN_WISE_ALLOWED_VOUCHER_TYPE";
    public static final String VOUCHER_PROFILE_OTHER_INFO = "VOUCHER_PROFILE_OTHER_INFO";
    public static final String MAX_APPROVAL_LEVEL_C2C_INITIATE = "MAX_APPROVAL_LEVEL_C2C_INITIATE";
    public static final String MAX_APPROVAL_LEVEL_C2C_TRANSFER="MAX_APPROVAL_LEVEL_C2C_TRANSFER";
    public static final String MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE="MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE";
    public static final String MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER="MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER";
    public static final String FORWARD_SLASH = "/";
    public static final String HYPHEN = "-";
    public static final String EMPTY = "";
    public static final String DATE = "date";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    
    public static final String DATE_FORMAT_DDMMYYYY = "dd/MM/yyyy";
    public static final String DATE_FORMAT_YYMMDD = "yy/MM/dd";
    public static final String DATE_FORMAT_DDMMYYYY_HYPHEN = "dd-MM-yyyy";
    public static final String DATE_FORMAT_DDMMYY_HYPHEN = "dd-MM-yy";
    public static final String DATE_FORMAT_YYYYMMDD = "yyyy/MM/dd";
    public static final String DATE_FORMAT_YYYYMMDD_HYPHEN = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YYMMDD_HYPHEN = "yy-MM-dd";
    public static final String DATE_FORMAT_DDMMMYYYY = "dd/MMM/yyyy";
    public static final String DATE_FORMAT_YYYYMMMDD = "yyyy/MMM/dd";
    public static final String DATE_FORMAT_DDMMMYY = "dd/MMM/yy";
    public static final String TIMESTAMP_DDMMMYYYYHHMMSS = "dd/MMM/yyyy HH:mm:ss";
    public static final String TIMESTAMP_YYYYMMMDDHHMMSS = "yyyy/MMM/dd HH:mm:ss";
    public static final String TIMESTAMP_DDMMMYYHHMMSS = "dd/MMM/yy HH:mm:ss";
    
    public static final String DATE_FORMAT_DDMMMYYYY_HYPHEN = "dd-MMM-yyyy";
    public static final String DATE_FORMAT_YYYYMMMDD_HYPHEN = "yyyy-MM-Mdd";
    public static final String DATE_FORMAT_DDMMMYY_HYPHEN = "dd-MMM-yy";
    public static final String TIMESTAMP_DDMMMYYYYHHMMSS_HYPHEN = "dd-MMM-yyyy HH:mm:ss";
    public static final String TIMESTAMP_YYYYMMMDDHHMMSS_HYPHEN = "yyyy-MMM-dd HH:mm:ss";
    public static final String TIMESTAMP_DDMMMYYHHMMSS_HYPHEN = "dd-MMM-yy HH:mm:ss";
    
    public static final String TIMESTAMP_DATESPACEHHMMSS = "dd/MM/yy HH:mm:ss";
    public static final String TIMESTAMP_DATESPACEHHMMSS_HYPHEN = "dd-MM-yy HH:mm:ss";
    public static final String TIMESTAMP_DATESPACEHHMM = "dd/MM/yy HH:mm";
    public static final String TIMESTAMP_DDMMYYYYHHMMSS = "dd/MM/yyyy HH:mm:ss";
    public static final String TIMESTAMP_DDMMYYYYHHMMSS_HYPHEN = "dd-MM-yyyy HH:mm:ss";
    public static final String TIMESTAMP_DDMMYYYYHHMM = "dd/MM/yyyy HH:mm";
    public static final String TIMESTAMP_DATE_HHMM = "dd/MM/yy-HH:mm";
    public static final String TIMESTAMP_YYYYMMDDHHMMSS = "yyyy/MM/dd HH:mm:ss";
    public static final String TIMESTAMP_YYYYMMDDHHMMSS_HYPHEN = "yyyy-MM-dd HH:mm:ss";
    public static final String GREGORIAN = "gregorian";
    public static final String PERSIAN = "persian";
    public static final String DATE_FORMAT_DDMMYY = "dd/MM/yy";//For validation
    public static final String DATE_FORMAT = "dd/MM/yy";//For system level
    public static final String DATE_FORMAT_HYPHEN = "dd-MM-yy";//For system level
    public static final String DATE_FORMAT_LITERAL = "dateFormat";
    public static final String IS_MON_FORMAT_LITERAL = "isMonFormat";
    public static final String FALSE = "FALSE";
    public static final String TRUE = "TRUE";
    public static final String SPACE = " ";
    public static final String VOMS_DAMG_PIN_LNTH_ALLOW = "VOMS_DAMG_PIN_LNTH_ALLOW";
  	public static final String MAX_SMS_PIN_LENGTH="MAX_SMS_PIN_LENGTH";
  	
  	
  	// Voucher Types
  	
  	public static String VOUCHER_TYPE_PHYSICAL = "P";
  	public static String VOUCHER_TYPE_ELECTRONIC = "E";
  	public static String VOUCHER_TYPE_TEST_PHYSICAL = "PT";
  	public static String VOUCHER_TYPE_TEST_ELECTRONIC = "ET";
  	public static String VOUCHER_TYPE_DIGITAL = "D";
  	public static String VOUCHER_TYPE_TEST_DIGITAL = "DT";
  	public static String ACTIVE_PROF = "ACTIVE_PROF";
  	public static String VOUC_DOWN = "VOUC_DOWN";
  	public static String O2C = "O2C";
  	public static String VMS_P_STATUS_CHANGE_MAP = "VMS_P_STATUS_CHANGE_MAP";
  	public static String VMS_E_STATUS_CHANGE_MAP = "VMS_E_STATUS_CHANGE_MAP";
  	public static String VMS_D_STATUS_CHANGE_MAP = "VMS_D_STATUS_CHANGE_MAP";
  	public static String VOMS_PROFILE_MIN_REORDERQTY = "VOMS_PROFILE_MIN_REORDERQTY";
  	public static String IS_BLANK_VOUCHER_REQUIRED = "IS_BLANK_VOUCHER_REQ";
  	
  	public static final String TABLE_OTHER_COMM_PRF_SET = "other_comm_prf_set";
  	public static final String COLUMN_OTHER_COMM_PRF_SET_NAME = "oth_comm_prf_set_name";
    public static final String OTH_COMM_TYPE_LOOKUP = "OTCTP";
    public static final String GATEWAY_CODE = "Gateway Code";
  	public static final String CATEGORY = "Category Code"; 
  	public static final String GRADE = "Grade";

    /* ---------------  SYSTEM PREFERENCE  ----------------- */
    /*C2C TRANSFER REVAMP */
    public static final String DATE_FORMAT_CAL_JAVA = "DATE_FORMAT_CAL_JAVA" ;
    public static final String CALENDER_DATE_FORMAT = "CALENDER_DATE_FORMAT" ;

    /* ---------------  MVD  ----------------- */
    public static final String MVD_MAX_VOUCHER = "MVD_MAX_VOUCHER" ;
    public static final String MVD_MIN_VOUCHER = "MVD_MIN_VOUCHER" ;
  	public static final String OTHER_COMM_PROFILE_GATEWAY = "GAT";
  	public static final String DW_COMMISSION_CAL = "DW_COMMISSION_CAL";
    public static final String DW_COMMISSION_CAL_OTH_COMMISSION ="OTH";
    public static final String DW_COMMISSION_CAL_BASE_OTH_COMMISSION ="BASE_OTH"; 
    public static final String REQ_CUSER_SUS_APP ="REQ_CUSER_SUS_APP";

    /* C2C BATCH */
    public static final String C2C_BATCH_APPROVAL_LVL="C2C_BATCH_APPROVAL_LVL";
    
    /* BULK EVD RECHARGE*/
    
    public static final String SCHEDULE_ON= "on";
    public static final String SCHEDULE_OFF= "off";
    public static final String OCCURENCE_DAILY= "Daily";
    public static final String OCCURENCE_WEEKLY= "Weekly";
    public static final String OCCURENCE_MONTHLY= "Monthly";
    public static final String COLUMN_STATUS= "STATUS";
    public static final String COLUMN_BATCH_ID= "BATCH_ID";
    public static final String TABLE_SCHEDULED_BATCH_MASTER= "SCHEDULED_BATCH_MASTER";
    public static final String PREPAID_MOB_NUM = "Prepaid";
    public static final String ALPHANUMERIC_MOB_NUM = "AlphaNumeric";
    public static final String INVALID_PIN = "9999";
    public static final String NO_OCCURENCE= " ";
    public static final String NORMAL_TEMPLATE= "Normal Template";
    public static final String RESTRICTED_TEMPLATE= "Restricted Template";
    
    public static final String SYSTEM_DATE_FORMAT = "SYSTEM_DATE_FORMAT";
    public static final String SYSTEM_DATETIME_FORMAT = "SYSTEM_DTTIME_FORMAT";
    public static final String INTERFACE_CATEGORY_PREPAID = "PRE";
	public static final String INTERFACE_CATEGORY_POSTPAID = "POST";
	public static final String INTERFACE_CATEGORY_VOMS = "VOMS";
	
}



	
	