package com.btsl.voms.vomscommon;

import com.btsl.common.TypesI;

public interface VOMSI extends TypesI {

    public String VOMS_STATUS_ACTIVE = "Y";
    public String VOMS_STATUS_DELETED = "N";
    public String VOMS_STATUS_SUSPENDED = "S";
    public String YES = "YES";
    public String NO = "NO";
    public String VOMS_SOLD = "Y";

    // for VOMS category
    public String LOOKUP_EVD_CATEGORY_TYPE = "CTYP";
    public String EVD_CATEGORY_TYPE_FIXED = "CFIX";
    public String EVD_CATEGORY_TYPE_FLEX = "CFLEX";
    public String CATEGORY_ID_TYPE = "EVDCATID";
    public String PRODUCT_ID_TYPE = "VOMSPRID";
    public String VOMS_BATCHES_DOC_TYPE = "VMBTCHUD";
    public String ACTIVE_PRODUCT_ID_TYPE = "VMACTPID";
    public String VOMS_BATCHES_DOC_TYPE_INIT = "VMBTINIT";

    // For voucher status
    public String LOOKUP_VOUCHER_STATUS = "VSTAT";
    public String LOOKUP_BATCH_STATUS = "BSTAT";
    public String ALL = "ALL";
    // For profile status
    public String LOOKUP_PRODUCT_STATUS = "VMPST";
    public String NOTAPPLICABLE = "NA";
    public String PRODUCT_ID = "productId";
    public String PRODUCT_NAME = "productName";
    public String VOUCHER_NEW = "GE";
    public String VOUCHER_STOLEN = "ST";
    public String VOUCHER_SOLD = "SL";
    public String VOUCHER_ON_HOLD = "OH";
    public String VOUCHER_DAMAGED = "DA";
    public String VOUCHER_RECONCILE = "RC";
    public String VOUCHER_ENABLE = "EN";
    public String VOUCHER_UNPROCESS = "UP";
    public String VOUCHER_USED = "CU";

    public String BATCH_GENERATED = "GE";
    public String BATCH_ENABLED = "EN";
    public String BATCH_SOLD = "SL";
    public String BATCH_RECHARGED = "RE";
    public String BATCH_ONHOLD = "OH";
    public String BATCH_STOLEN = "ST";
    public String BATCH_DAMAGED = "DA";
    public String BATCH_INTIATED = "IN";
    public String BATCH_APP1 = "A1";
    public String BATCH_APP2 = "A2";
    public String BATCH_ACCEPTED = "AC";
    public String SCHEDULED = "SC";
    public String EXECUTED = "EX";
    public String UNDERPROCESS = "UP";
    public String PENDING = "PE";
    public String BATCH_CHAR = "B";
    public String BATCHRECONCILESTAT = "RC";
    public String BATCHFAILEDSTATUS = "FA";

    public String BATCHCONSUMESTAT = "CU";

    public String CHANGE_STATUS_FLAG = "CHANGE";

    public String VA_PROCESS_SUCCESS_STAT = "SU"; // Length should be 2
    public String VA_PROCESS_ERROR_STAT = "ER";
    public String RESPONSE_MESSAGE = "MESSAGE";
    public String BATCH_PROCESS_GEN = "GENERATION";
    public String BATCH_PROCESS_ENABLE = "ENABLE";
    public String BATCH_PROCESS_CHANGE = "CHANGESTATUS";
    public String BATCH_PROCESS_RECONCILE = "RECONCILE";
    public String BATCH_PROCESS_INITIATE = "INITIATE";

    // Voucher File upload errors added by Siddhartha
    public static String VOUCHER_ERROR_CONN_NULL = "CONNULL";
    public static String VOUCHER_ERROR_DIR_CONTAINS_NO_FILES = "NOFILE";
    public static String VOUCHER_ERROR_HEADER_INFO = "HEADERERR";
    public static String VOUCHER_ERROR_FILE_DOES_NOT_EXIST = "NOFILE";
    public static String VOUCHER_ERROR_INVALID_FILENAME_FORMAT = "FILEFORMATINVALID";
    public static String VOUCHER_ERROR_FILE_EXT_NOT_DEFINED = "NOEXT";
    public static String VOUCHER_ERROR_DIR_NOT_EXIST = "NODIR";
    public static String VOUCHER_ERROR_FILENAME_SEPARATOR = "SEPAERROR";
    public String VOMSBTCID = "VOMSBTCID";
    public static String PROCESS_ALREADY_RUNNING = "PROCESSRUNNING";
    public static String VOUCHER_ERROR_MOVE_LOCATION_NOT_EXIST = "ERRLOC";
    public static String VOUCHER_ERROR_FILE_NOT_MOVED_SUCCESSFULLY = "FILENOTMOVSUCC";
    public static String SEQNUM = "SEQNUM";

    // For report

    public static String OH_AND_EN = "OH_AND_EN";
    public static String LOOKUP_REPORT_VOUCHER_STATUS = "RSTAT";
    public static String SHORT_SERIAL_NUMBER = "SN";
    public static String SHORT_VOUCHER_STATUS = "VS";
    public static String VOUCHER_REP_ENABLE = "EN";
    public static String VOUCHER_REP_ON_HOLD = "OH";
    public static String VOMS_DEF_SEP = "##";
    public static String DATE_FORMAT = "yyyyMMDD";
    public static String SERVICE_TYPE_VOUCHER_ENQ = "VE";
    public static String SERVICE_TYPE_VOUCHER_REC = "VR";
    public static String SERVICE_TYPE_VOUCHER_CON = "VC";
    public static String SERIAL_NO = "SNO";
    public static String TOPUP = "TUP";
    public static String SUBSCRIBER_ID = "SUBSID";
    public static String REGION = "REGION";
    public static String FIRST_CONSUMED_ON = "first_consumed_on";
    public static String EXPIRY_DATE = "expiry_date";
    
    public static String VALID = "VAL";
    public static String MESSAGE = "MSG";
    public static String ERROR = "ERR";
    public static String CONSUMED = "CNS";
    public static String XML_REQUEST_SOURCE = "API";
    // added by gaurav
    public static String VOMS_PRINT_ENABLE_STATUS = "PE";

    public static String VOMS_PRINT_BATCH = "PB";

    public static String BATCH_O2C_TRANSFER = "OC";

    public static int ACTION_SERIALNO_PIN_DETAILS = 1;
    public static int ACTION_VOUCHER_INFO = 2;
    public static int ACTION_VOUCHER_CONSUMPTION = 3;
    public static int ACTION_VOUCHER_DETAILS_AGAIN = 4;
    public static int ACTION_VOUCHER_ROLLBACK = 5;
    public static int ACTION_VOUCHER_RET_ROLLBACK = 6;

    public static String HTTP_STATUS_200 = "200";
    public static String SUBSCRIBER_NOT_FOUND = "4056";

    // voms new status changes
    public static String VOMS_WARE_HOUSE_STATUS = "WH";
    public static String VOMS_PRE_ACTIVE_STATUS = "PA";
    public static String VOMS_SUSPEND_STATUS = "S";
    public String VOMS_INITIATED_STATUS = "IN";
    public static String WARE_HOUSE = "WH";
    public static String VOMS_MRP = "MRP";
    public static String VOMS_PIN = "PIN";

    // added for voucher query and rollback request
    public static String SERVICE_TYPE_VOUCHER_QRY = "VQ";
    public static String SERVICE_TYPE_VOUCHER_ROLLBACK = "VB";
    public static String VOMS_STATUS = "STATUS";
    public static String VOMS_GENERATED_STATUS = "GENERATED";
    public static String VOMS_PRINTENABLE_STATUS = "PRINTENABLE";
    public static String VOMS_ENABLE_STATUS = "ENABLE";
    public static String VOMS_PREACTIVE_STATUS = "PREACTIVE";
    public static String VOMS_WAREHOUSE_STATUS = "WAREHOUSE";
    public static String VOMS_CLOSED_STATUS = "CLOSED";
    public static String VOMS_SUSPENDED_STATUS = "SUSPEND";
    public static String PIN = "PIN";
    public static String SERVICE_TYPE_VOUCHER_REC_AGAIN = "VRAG";
    public static String VOMS_TALKTIME = "TALKTIME";
    public static String VOMS_VALIDITY = "VALIDITY";

    // added for Voucher Retrieval RollBack Request
    public static String SERVICE_TYPE_VOUCHER_RETRIEVAL_ROLLBACK = "VL";
    public static String VOMS_TXNID = "TXNID";
    public static String VOMS_EXPIRY_DATE="EXPIRYDATE";
    public static String VOMS_CONSUMED_DATE="CONSUMEDDATE";
    public static String TALKTIME="TKTIME";
	 public static String SERVICE_TYPE_VOUCHER_STATUS_CHANGE="VSCH";
	 public static String FROM_SERIAL_NO="FROM_SERIALNO";
	 public static String TO_SERIAL_NO="TO_SERIALNO";
	 public static String MSISDN="MSISDN";
	 public static String PRE_STATUS="PRE_STATUS";
	 public static String REQ_STATUS="REQ_STATUS";
	public static String ERROR_TAG = "ERROR";
	 public static String MESSAGE_TAG = "MESSAGE";
	 public static String TXNSTATUS_TAG = "TXNSTATUS";
	 /* Added for voucher Auto Generation*/
	 public static String AUTO = "AUTO";
	 public static String MANUAL ="MANUAL";
	 public static String VOMS_AUTO_GEN_ALLOW = "Y";
	 public static String VOUCHER_TYPE_PHYSICAL = "P";
	 public static String VOUCHER_TYPE_ELECTRONIC = "E";
	 public static String VOUCHER_STATUS_DB = "VOMS_DB_STATUS";
	 public static String VOUCHER_TYPE_TEST_PHYSICAL = "PT";
	 public static String VOUCHER_TYPE_TEST_ELECTRONIC = "ET";
	    public static String VOMS_TYPE = "VOMS_TYPE";
	 public static String VOMS_PIN_EXP_EXT = "VMPNEXPEXT";
	 public static String VOUCHER_VALIDATION = "VV";
	 public static String VOUCHER_RESERVATION = "VRR";
	 public static String VOUCHER_ENABLED_DATE = "ENABLEDDATE";
	 public static String VOUCHER_GENERATED_DATE = "GENERATEDDATE";
	 public static String VOUCHER_DIRECT_CONSUMPTION = "VDC";
	 public static String VOUCHER_DIRECT_ROLLBACK = "VDR";
	 public static String VOUCHER_SEGMENT = "VMSSEG";
	 public static String VOUCHER_SEGMENT_LOCAL = "LC";
	 public static String VOUCHER_SEGMENT_NATIONAL = "NL";
	 public static String VOUCHER_TYPE_DIGITAL = "D";
	 public static String VOUCHER_TYPE_TEST_DIGITAL = "DT";
	 public static String EXTERNAL_NETWORKCODE = "EXTNWCODE";
	 public static String BUNDLE_NAME = "BUNDLE_NAME";
	 public static String MASTER_SERIAL_NO = "MASTER_SERIAL_NO";
	 public static String USER_NAME = "USER_NAME";
	 public static String DEFAULT_PRODUCT_CODE = "PREPROD";
	 public static String DEFAULT_PRODUCT_CODE_VCR = "ETOPUP";
	 public static String TXNBATCHID="TXNBATCHID";
	public static String ERP_SYSTEM = "ERP_SYSTEM_PROCESS";
	public static String REQ_EXPIRY_DATE="REQ_EXPIRY_DATE";
}
