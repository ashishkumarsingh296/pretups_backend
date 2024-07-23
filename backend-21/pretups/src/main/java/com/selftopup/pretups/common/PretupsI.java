package com.selftopup.pretups.common;

import com.selftopup.common.TypesI;

public interface PretupsI extends TypesI {
    public String P2P_MODULE = "P2P";
    public String C2C_MODULE = "C2C";
    public String C2S_MODULE = "C2S";
    public String SINGLE = "SINGLE";
    public String MULTIPLE = "MULTIPLE";
    public String PRODUCT_CATEGORY_FIXED = "FIXED";
    public String PRODUCT_CATEGORY_FLEX = "FLEX";
    public String TEMP_TRANS_ID_START_WITH = "TX";
    public String REQUEST_SOURCE_TYPE_STK = "STK";
    public String REQUEST_SOURCE_TYPE_SMS = "SMS";
    public String REQUEST_SOURCE_TYPE_WEB = "WEB";
    public String REQUEST_SOURCE_TYPE_EXTGW = "EXTGW";

    public String REQUEST_TYPE_ACCEPT = "ACCEPT";
    public String INTERFACE_CATEGORY_PRE = "PRE";
    public String INTERFACE_CATEGORY_POST = "POST";
    public String INTERFACE_CATEGORY_BOTH = "BOTH";

    public String MSG_GATEWAY_FLOW_TYPE_THREAD = "T";
    public String MSG_GATEWAY_FLOW_TYPE_COMMON = "C";
    public String MSG_GATEWAY_FLOW_TYPE_REQUEST = "R";// flow type is request

    public String MSG_GATEWAY_RESPONSE_TYPE_PUSH = "PUSH";
    public String MSG_GATEWAY_RESPONSE_TYPE_RESPONSE = "RESPONSE";

    public int TEMP_TRANS_ID_LENGTH = 11;

    // public String SYSTEM_PERCENTAGE ="PER";
    public String SYSTEM_AMOUNT = "AMT";

    public String UDH_HEX = "027000";
    // public String DEFAULT_C2S_PIN="0000";
    // public String DEFAULT_P2P_PIN="0000";

    public String KEYWORD_TYPE_REGISTRATION = "REG";
    public String KEYWORD_TYPE_ADMIN = "ADM";

    public String ENCRYPTION_LEVEL_GLOBAL_CODE = "GLOBAL";
    public String ENCRYPTION_LEVEL_USER_CODE = "USER";
    public String ENCRYPTION_LEVEL_MASTER_KEY = "MASTER_KEY";
    public String LOCALE_LANGAUGE_EN = "en";
    // Make the Doc Series ID Entries here
    public String NETWORK_PREFIX_ID = "NETPREFIX";
    public String SERVICE_KEYWORD_ID = "SVK";// for service keyword id
    public String CHANNEL_TRANSFER_RULE_ID = "TRL"; // for channel transfer rule
                                                    // id
    public String RES_INTERFACE_ID = "RESID";
    public String INTERFACE_TYPE_ID = "INTID"; // For interface type id
    public String INTERFACE_SINGLE_STATE_TRANASACTION = "Y";

    public String ID_GEN_P2P_TRANSFER_NO = "TRANS";
    public String ID_GEN_C2S_TRANSFER_NO = "RECH";

    public String P2P_USER_ID = "P2P";// for service keyword id

    public String STATUS_TYPE = "STAT";// for status
    public String NETWORK_TYPE = "NTTYP";// for Network Type
    public String MODULE_TYPE = "MOTYP"; // for Module Type
    public String SUBSRICBER_TYPE = "SUBTP";// for subscriber Type
    public String GATEWAY_STATUS_TYPE = "GSTAT"; // for message gateway
                                                 // status(active,suspended,deleted)
    public String OPERATOR_TYPE_OPT = "OPT";
    public String OPERATOR_TYPE_CHNL = "CHNL";
    public String OPERATOR_TYPE_OTH = "OTH";
    public String SERIES_TYPE_POSTPAID = "POST";
    public String SERIES_TYPE_PREPAID = "PRE";
    public String SERIES_TYPE_BOTH = "BOTH";
    public String REQ_INTERFACE_TYPE = "RQINT";// for Request Interface Type

    public String INTERFACE_CATEGORY = "INTCT"; // for Interface Category

    public String INTERFACE_CATEGORY_PREPOST = "INCAT"; // interface category
                                                        // for service class

    // public int TRANSFER_ID_PAD_LENGTH=9; //Not being used as this has been
    // shifted to Tax calculator class
    public String SKEY_STATUS_EXPIRED = "E";
    public String SKEY_STATUS_CANCELLED = "C";
    public String MODIFY_ALLOWED_YES = "Y";
    public String MODIFY_ALLOWED_NO = "N";
    // public String BILLING_CYCLE_YES="Y"; //for the validation in the
    // registerActivateSubscriber.jsp
    // public String BILLING_CYCLE_NO="N";//for the validation in the
    // registerActivateSubscriber.jsp

    public String USER_STATUS_NEW = "W";
    public String USER_STATUS_APPROVED = "A";
    public String USER_STATUS_SUSPEND = "S";
    public String USER_STATUS_CANCELED = "C";
    public String USER_STATUS_DELETED = "N";
    public String USER_STATUS_ACTIVE = "Y";
    public String USER_STATUS_DEREGISTERED = "D";
    public String USER_STATUS_BLOCK = "B";
    public String USER_STATUS_SUSPEND_REQUEST = "SR";
    public String USER_STATUS_DELETE_REQUEST = "DR";

    public String USER_APPROVE = "A";
    public String USER_REJECTED = "R";
    public String USER_DISCARD = "D";

    public String USER_STATUS_TYPE = "URTYP";
    public String USR_APPROVAL_LEVEL = "USRLEVELAPPROVAL";

    public String INTERFACE_VALIDATE_ACTION = "V";
    public String INTERFACE_CREDIT_ACTION = "C";
    public String INTERFACE_DEBIT_ACTION = "D";
    public String INTERFACE_UPDATE_VALIDITY_ACTION = "VA";

    public String TRANSACTION_SUCCESS_STATUS = "300";
    public String TRANSACTION_FAIL_STATUS = "350";

    public String P2P_STATUS_KEY_VALUS = "P2P_STATUS";

    public String TRANSFER_TYPE_TXN = "TXN";
    public String TRANSFER_TYPE_RCH_CREDIT = "RCH_CR";
    public String TRANSFER_TYPE_DIFFCR = "DIFFC";

    public String USER_TYPE_SENDER = "SENDER";
    public String USER_TYPE_RECEIVER = "RECEIVER";

    public int MESSAGE_LENGTH_ADD_BUDDY = 5;
    public int MESSAGE_LENGTH_DELETE_BUDDY = 3;

    public int MESSAGE_LENGTH_DELETE_BUDDY_LIST = 3; // added by harsh 09Aug12

    public int MESSAGE_LENGTH_CHANGE_PIN = 4;
    public int MESSAGE_LENGTH_SUSPEND = 2;
    public int MESSAGE_LENGTH_RESUME = 2;
    public int MESSAGE_LENGTH_BUDDYLIST = 2;
    public int MESSAGE_LENGTH_TRANSFERSTATUS = 2;
    public int MESSAGE_LENGTH_ACCOUNTSTATUS = 2;
    public int MESSAGE_LENGTH_DEREGISTER = 2;

    public String BARRED_USER_TYPE_SENDER = "SENDER";
    public String BARRED_USER_TYPE_RECEIVER = "RECEIVER";

    public String SUB_LOOKUP_ID = "SL";
    public String TXN_STATUS_UNDER_PROCESS = "U";
    public String TXN_STATUS_COMPLETED = "C";
    public String TXN_STATUS_AMBIGUOUS = "A";

    public int TRANS_STAGE_BEFORE_INVAL = 0;
    public int TRANS_STAGE_AFTER_INVAL = 1;
    public int TRANS_STAGE_AFTER_FIND_CGROUP = 2;
    public int TRANS_STAGE_AFTER_INTOP = 3;

    public String BONUS_VALIDITY_TYPE_DAYS = "DAYS";
    public String REGISTERATION_REQUEST_PRE = "PRE";
    public String REGISTERATION_REQUEST_POST = "POST";

    public String BARRED_USER_TYPE = "BRTYP";
    public String BARRED_TYPE = "BTYP";
    public String BARRED_TYPE_ALL = "ALL";
    public String BARRED_TYPE_SELF = "SELF";
    public String BARRED_TYPE_CUSTOMERCARE = "CC";
    public String BARRED_TYPE_SYSTEM = "SYSTEM";
    public String BARRED_TYPE_PIN_INVALID = "BRPIN";
    public String BILLIGNG_CYCLE_MONTHLY = "MONTHLY";
    public String BILLIGNG_CYCLE_PERIODLY = "PERIODLY";

    public String VALPERIOD_HIGHEST_TYPE = "VLHI";
    public String VALPERIOD_CUMMULATIVE_TYPE = "VLCUM";
    public String VALPERIOD_LOWEST_TYPE = "VLLO";

    public String AUTH_TYPE_IP = "IP";
    public String AUTH_TYPE_LOGIN = "LOGIN";

    public String VALIDITY_TYPE = "VLTYP";
    public String AMOUNT_TYPE = "AMTYP";
    // for the Gateway Messages module
    public String GATEWAY_TYPE = "GWTYP";
    public String GATEWAY_SUB_TYPE = "GWSTP";
    public String PROTOCOL = "PRCOL";
    public String CONTENT_TYPE = "CNTYP";
    public String AUTH_TYPE = "AUTYP";
    public String ENCRYPTION_LEVEL = "ENCRL";
    public String GATEWAY_STATUS_ACTIVE = "Y";
    public String GATEWAY_STATUS_SUSPEND = "S";
    public String GATEWAY_STATUS_DELETE = "N";
    public String GATEWAY_HANDLER_CLASS = "MESS_GAT_PARSER";

    public String TRANSFER_RULE_STATUS_ACTIVE = "Y";
    public String TRANSFER_RULE_STATUS_SUSPEND = "S";
    public String TRANSFER_RULE_STATUS_DELETE = "N";

    // ends here
    public String SELECT_CHECKBOX = "Y";
    public String RESET_CHECKBOX = "N";

    public String GATEWAY_TYPE_SMSC = "SMSC";
    public String GATEWAY_TYPE_USSD = "USSD";
    public String GATEWAY_TYPE_WEB = "WEB";

    public String SERVICE_TYPE_REGISTERATION = "PREG";
    public String SERVICE_TYPE_DEREGISTERATION = "PDREG";
    public String SERVICE_TYPE_BARRED = "PBAR";
    public String SERVICE_TYPE_RESUMESERVICE = "PRES";
    public String SERVICE_TYPE_P2PRECHARGE = "PRC";
    // added for credit transfer & credit recharge
    public String SERVICE_TYPE_P2PCREDITRECHARGE = "PCR";

    public String SERVICE_TYPE_P2PCHANGEPIN = "CPN";
    public String SERVICE_TYPE_ACCOUNTINFO = "ACCINFO";
    public String SERVICE_TYPE_P2P_HISTORY = "PTR";
    public String SERVICE_TYPE_LANG_NOTIFICATION = "PCHLAN";
    public String SKEY_CANCEL_SUCCESS = "SUCCESS";
    public String NO_SKEY_TO_CANCEL_SUCCESS = "NOSKEYCNCL";

    public String INTERFACE_CATEGORY_PREPAID = "PRE";
    public String INTERFACE_CATEGORY_POSTPAID = "POST";
    public String SERVICE_CLASS_ID = "SERID";
    public String SERVICE_CLASS_STATUS_ACTIVE = "Y";
    public String SERVICE_CLASS_STATUS_DELETE = "N";
    public String SERVICE_CLASS_STATUS_SUSPEND = "S";

    /*
     * public int TRANSFER_RULE_NO=5; // no of transfer rules that can be added
     * at a time.
     * now coming from the constants.props file "NO_ROW_TRANSFER_RULE"
     */

    public String SUBLOOKUP_STATUS_YES = "Y";
    public String SUBLOOKUP_STATUS_NO = "N";

    public String CACHE_ACTION_ADD = "ADD";
    public String CACHE_ACTION_MODIFY = "MODIFY";
    public String CACHE_ACTION_DELETE = "DELETE";
    public String CACHE_ACTION_SAME = "SAME";

    public String PRODUCT_USAGE = "USAGE";
    public String REG_SUBSCRIBER_STATUS = "SSTAT"; // for registered subscriber
                                                   // Reports
    public String SUBSCRIBER_TYPE = "STYPE"; // for p2p subscriber transaction
                                             // summary report(per,post,all)

    public String GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE = "Y";
    public String GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND = "S";
    public String GEOGRAPHICAL_DOMAIN_STATUS_DELETE = "N";

    public String CHNL_TRANSFER_RULE_STATUS_ACTIVE = "Y";
    public String CHNL_TRANSFER_RULE_STATUS_SUSPEND = "S";
    public String CHNL_TRANSFER_RULE_STATUS_DELETE = "N";

    public String DOMAIN_TYPE_DISPLAY_ALLOWED = "Y";
    public String DOMAIN_ROLE_TYPE = "ROTYP";
    public int CATEGORY_SEQUENCE_NUMBER = 1;
    public String DOMAIN_STATUS_ACTIVE = "Y";
    public String DOMAIN_DISPLAY_ALLOWED = "Y";
    public String DOMAIN_STATUS_DELETE = "N";

    public String CATEGORY_STATUS_ACTIVE = "Y";
    public String CATEGORY_DISPLAY_ALLOWED = "Y";
    public String CATEGORY_MODIFY_ALLOWED = "Y";
    public String CATEGORY_STATUS_DELETE = "N";
    public String GROUP_ROLE = "Y";
    public String SYSTEM_ROLE = "N";
    public String GRADE_STATUS_ACTIVE = "Y";
    public String GRADE_STATUS_DELETE = "N";

    public String DIVISION_STATE_ACTIVE = "Y";
    public String DIVISION_ID = "DIVID";
    public String DIVISION_STATUS_DELETE = "N";
    public String DEPARTMENT_STATUS_ACTIVE = "Y";

    public String PHONE_PROFILE_TYPE = "PFTYP";

    // cacheId
    public int CACHE_ALL = 0;
    public int CACHE_NETWORK = 1;
    public int CACHE_LOOKUPS = 2;
    public int CACHE_PreferenceCache = 3;
    public int CACHE_NetworkPrefixCache = 4;
    public int CACHE_ServiceKeywordCache = 5;
    public int CACHE_SystemPreferences = 6;
    public int CACHE_MSISDNPrefixInterfaceMappingCache = 7;
    public int CACHE_NetworkInterfaceModuleCache = 8;
    public int CACHE_ServicePaymentMappingCache = 9;
    public int CACHE_TransferRulesCache = 10;
    public int CACHE_MessageGatewayCache = 11;
    public int CACHE_RequestInterfaceCache = 12;
    public int CACHE_FileCache = 13;
    public int CACHE_LoadControllerCache_INSTANCE = 14;
    public int CACHE_LoadControllerCache_NETWORK = 15;
    public int CACHE_LoadControllerCache_INTERFACE = 16;
    public int CACHE_LoadControllerCache_TRANSACTION = 17;
    public int CACHE_SIM_PROFILE = 18;
    public int CACHE_NETWORK_SERVICE_CACHE = 19;
    public int CACHE_NETWORK_PRODUCT_SERVICE_TYPE = 20;
    public int CACHE_ROUTING_CONTROL = 21;
    public int CACHE_REGISTRATION_CONTROL = 22;
    public int CACHE_CONSTANT_PROPS = 23;
    public int CACHE_LOGGER_CONFIG = 24;
    public int CACHE_MESSAGE = 25;
    public int CACHE_MESSAGE_RESOURCES = 26;
    public int CACHE_SERVICE_ROUTING = 27;
    // sonali garg
    public int CACHE_SERVICE_TYPE_SUBSCRIBER_ENQUIRY = 45;
    public String PRODUCT_TYPE = "PDTYP";

    public String USER_TRANSFER_IN_STATUS_ACTIVE = "N";
    public String USER_TRANSFER_IN_STATUS_SUSPEND = "Y";
    public String USER_TRANSFER_OUT_STATUS_ACTIVE = "N";
    public String USER_TRANSFER_OUT_STATUS_SUSPEND = "Y";
    public String PRODUCT_STATUS = "Y";
    public String PROFILE_ID = "PROFILEID";

    public String TRANSFER_EXTERNAL_TXN_INTIAL_LEVEL = "INTIAL";
    public String TRANSFER_EXTERNAL_TXN_FIRST_LEVEL = "FIRST";
    public String TRANSFER_EXTERNAL_TXN_SECOND_LEVEL = "SECOND";
    // FOR SYSTEM PREFRENCES
    public String TRANSFER_EXTERNAL_TXN_LEVEL = "EXTTXNLEVEL";
    public String TRANSFER_EXTERNAL_TXN_MANDATORY = "EXTTXNMANDT";

    public String PAYMENT_INSTRUMENT_TYPE = "PMTYP";
    public String PAYMENT_INSTRUMENT_TYPE_CASH = "CASH";

    public String OPERATOR_CATEGORY = "BCU";
    public String REQUEST_SOURCE_WEB = "WEB";
    public String REQUEST_SOURCE_STK = "STK";
    public String CHANNEL_TRANSFER_TYPE_ALLOCATION = "TRANSFER";
    public String CHANNEL_TRANSFER_TYPE_RETURN = "RETURN";
    public String CHANNEL_TRANSFER_O2C_ID = "OT";
    public String CHANNEL_RETURN_O2C_ID = "OR";
    public String CHANNEL_WITHDRAW_O2C_ID = "OW";
    public String CHANNEL_TO_CHANNEL_TRANSFER_ID = "CT";
    public String CHANNEL_TO_CHANNEL_RETURN_ID = "CR";
    public String CHANNEL_TO_WITHDRAW_RETURN_ID = "CW";
    public String CHANNEL_TYPE_O2C = "O2C";
    public String CHANNEL_TYPE_C2C = "C2C";
    public String CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW = "W";
    public String CHANNEL_TRANSFER_SUB_TYPE_RETURN = "R";
    public String CHANNEL_TRANSFER_SUB_TYPE_TRANSFER = "T";
    public String CHANNEL_TRANSFER_TYPE = "C2S_TRFTYP";
    public String CHANNEL_TRANSFER_C2C_TYPE = "C2C_TRFTYP";
    public String CHANNEL_TRANSFER_ORDER_STATUS = "CTSTA";
    public String CHANNEL_TRANSFER_ORDER_NEW = "NEW";
    public String CHANNEL_TRANSFER_ORDER_CANCEL = "CNCL";
    public String CHANNEL_TRANSFER_ORDER_CLOSE = "CLOSE";
    public String CHANNEL_TRANSFER_ORDER_APPROVE1 = "APPRV1";
    public String CHANNEL_TRANSFER_ORDER_APPROVE2 = "APPRV2";
    public String CHANNEL_TRANSFER_ORDER_APPROVE3 = "APPRV3";
    public String TRANSFER_STOCK_TYPE_HOME = "HOME";
    public String TRANSFER_STOCK_TYPE_ROAM = "ROAM";
    public String TRANSFER_CATEGORY_SALE = "SALE";
    public String TRANSFER_CATEGORY_FOC = "FOC";
    public String TRANSFER_CATEGORY_TRANSFER = "TRF";
    // public String TRANSFER_CATEGORY_BTM = "BTM";
    public String TRANSFER_CATEGORY = "TRCAT";
    public String TRANSFER_STATUS = "TSTAT";
    public String CHANNEL_TRANSFER_STATUS = "C2SSTAT";
    public String CHANNEL_TRANSFER_C2C_STATUS = "C2CSTAT";
    public final static int NET_PREFIX_DGT = 5;
    public String CREDIT = "CR";
    public String DEBIT = "DR";
    public String CATEGORY_TYPE_OPT = "OPT";
    public String DOMAIN_TYPE_OPT = "OPT";
    public int OPT_SEQUENCE_NUMBER = 0;
    public String CATEGORY_USER_TYPE = "OPERATOR";
    public String USER_TYPE_OPERATOR = "Operator";
    public String TRANSFER_RULE_TYPE_OPT = "OPT";
    public String TRANSFER_RULE_TYPE_CHANNEL = "CHANNEL";
    public String NETWORK_STOCK_TRANSACTION_TRANSFER = "TRANSFER";
    public String NETWORK_STOCK_TRANSACTION_CREATION = "CREATION";
    public String NETWORK_STOCK_TRANSACTION_RETURN = "RETURN";
    public String NETWORK_STOCK_TRANSACTION_ID = "NT";
    public String NETWORK_STOCK_TRANSACTION_WITHDRAW = "WITHDRAW";

    public String STK_PROFILE_ACTIVE = "Y";

    public String ROLE_TYPE = "RLTYP";
    public boolean USE_HOME_STOCK_TRUE = true; // whether only home stock is
                                               // allowed.if false roam stock is
                                               // also applicable
    public String CIRCLE_LOCATION_TYPE = "CIR";
    public String ROAM_LOCATION_TYPE = "RMCIR";
    public String ROOT_PARENT_ID = "ROOT";

    public String GATEWAY_MESSAGE_SUCCESS = "200";
    public String GATEWAY_MESSAGE_FAILED = "500";

    public String UPD_SIM_TXN_ID = "1111111";
    public String NETWORK_STOCK_STATUS = "NSTAT";
    public String NETWORK_STOCK_TYPE = "STTYP";

    public String SMS_INTERFACE_ALLOWED = "Y";

    // added by sandeep goel these entries are used in the AdminController
    public String PREF_TR_ID_REQ = "TR_ID_REQ";
    public String PREF_LG_MN_REQ = "LG_MN_REQ";
    public String PREF_SMS_P_INDX = "SMS_P_INDX";
    public String PREF_PROD_REQ = "PROD_REQ";
    // ends here

    public String PRODUCT_STATUS_ACTIVE = "Y";
    public String PRODUCT_STATUS_SUSPEND = "S";
    public String PRODUCT_STATUS_DELETE = "N";

    public String NETWORK_PRODUCT_STATUS_ACTIVE = "Y";
    public String NETWORK_PRODUCT_STATUS_SUSPEND = "S";
    public String NETWORK_PRODUCT_STATUS_DELETE = "N";

    public String NETWK_PRODUCT_USAGE_DISTRIBUTION = "D";
    public String NETWK_PRODUCT_USAGE_CONSUMPTION = "C";
    public String NETWK_PRODUCT_USAGE_BOTH = "B";

    public String CARD_GROUP_SET_ID = "CARD_SETID";
    public String CARD_GROUP_ID = "CARD_GRPID";
    public String COMMISSION_PROFILE_SET_ID = "COMM_SETID";
    public String COMMISSION_PROFILE_PRODUCT_ID = "COMM_PROID";
    public String COMMISSION_PROFILE_DETAIL_ID = "COMM_DETID";
    public String ADDITIONAL_COMMISSION_SERVICE_ID = "ADD_SERID";
    public String ADDITIONAL_COMMISSION_PROFILE_ID = "ADD_COMMID";

    public String SUBSCRIBER_TRANSFER_OUTCOUNT = "SEP_TRF_CTRL";

    public String TRANSFER_TYPE_O2C = "O2C";
    public String TRANSFER_TYPE_C2C = "C2C";
    public String TRANSFER_TYPE_FOC = "FOC";
    public String TRANSFER_TYPE_C2S = "C2S";
    public String TRANSFER_TYPE_C2S_CREDITBACK = "RCH_CR";
    public String TRANSFER_TYPE_P2P_CREDITBACK = "P2P_CR";

    public String STATUS_ACTIVE = "Y";
    public String STATUS_SUSPEND = "S";
    public String STATUS_DELETE = "N";
    public String STATUS_CANCELED = "C";

    public String CHANNEL_USER_TRANSFER_MODE = "UTRMD";

    public String ADJUSTMENT_TYPE_DIFFERENTIAL = "DIFFC";
    public String ID_GEN_ADJUSTMENT_NO = "ADJUST";

    public String USER_TYPE = "USER TYPE";

    public String NETWORK_STOCK_TXN_STATUS_NEW = "NEW";
    public String NETWORK_STOCK_TXN_STATUS_CANCEL = "CNCL";
    public String NETWORK_STOCK_TXN_STATUS_CLOSE = "CLOSE";
    public String NETWORK_STOCK_TXN_STATUS_APPROVE1 = "APPRV1";

    public String ACCESS_FROM_LOGIN = "LOGIN";
    public String ACCESS_FROM_PHONE = "PHONE";
    public String INTERFACE_STATUS_ALLOWED = "Y";
    public String INTERFACE_STATUS_NOTALLOWED = "N";

    public String USER_CODE_REQUIRED = "USER_CODE_REQUIRED";
    public String USE_HOME_STOCK_YES = "YES";

    public String AMOUNT_TYPE_PERCENTAGE = "PCT";
    public String AMOUNT_TYPE_AMOUNT = "AMT";

    // reports
    public String IN = "IN";
    public String OUT = "OUT";
    public String SORTTYPE_USERNAME = "UN";
    public String SORTTYPE_USERTYPE = "UT";
    public String SORTTYPE_USERCODE = "UC";
    public String SORTTYPE_USER_GRADE = "UG";
    public String SORTTYPE_TRANSFER_ID = "TI";
    public String SORTTYPE_DATE = "D";
    public String SORTTYPE_USER_CATEGORY = "UC";
    public String SORTTYPE_BARRED_DATETIME = "BDT";
    public String SORTTYPE_USER_GEOGRAPHIC = "UG";
    public String SORTTYPE_DEPARTMENT = "D";
    public String SORTTYPE_USERID = "UI";
    public String SORTTYPE_DIVISION = "DIV";
    public String SORTTYPE_USER_STATUS = "US";
    public String SERVICE_TYPE = "SVCTP";
    public String REPORT_TYPE = "RPTTYPE";
    public String FILTER_TYPE_COUNT = "Count";
    public String FILTER_TYPE_AMOUNT = "Amount";
    public String GEO_DOMAIN_STATUS = "Y";
    public String SENDER_NETWORK_CODE = "S";
    public String RECEIVER_NETWORK_CODE = "R";
    public String MONTHLY_FILTER = "MONTHLY";
    public String DAILY_FILTER = "DAILY";
    public String MOBILE_NO = "MOBILE";
    public String DATE_CHECK_PREVIOUS = "PREVIOUS";
    public String DATE_CHECK_CURRENT = "CURRENT";

    public String USERTYPE_CHANNEL_USER = "Channel User";
    public String USERTYPE_CUSTOMER = "Customer";

    public String DOMAIN_TYPE_SALECENTER = "SALE_CENTER";

    public String STATUS_IN = "IN";
    public String STATUS_NOTIN = "NOT IN";
    public String STATUS_EQUAL = "EQUAL";
    public String STATUS_NOTEQUAL = "NOTEQUAL";

    public String ICCID_CHECKSTRING = "9819";
    public String CHANNEL_USER_HIERARCHY_STATUS = "UHSTT";
    public String GATEWAY_DISPLAY_ALLOW_YES = "Y";
    public String GATEWAY_MODIFIED_ALLOW_YES = "Y";

    public String DOMAINS_FIXED = "F";
    public String DOMAINS_ASSIGNED = "A";
    public String LAST_TRANSACTION_C2C_TYPE = "C2C";
    public String LAST_TRANSACTION_C2S_TYPE = "C2S";

    public String REPORTS = "RPT";
    public String CRYSTAL_REPORTS = "CRPT";

    public String C2S_TRANSFER_STATUS = "C2SST";

    public String TXN_LOG_REQTYPE_REQ = "REQ";
    public String TXN_LOG_REQTYPE_RES = "RES";
    public String TXN_LOG_REQTYPE_INT = "INT";

    public String TXN_LOG_TXNSTAGE_RECIVED = "RECEIVE";
    public String TXN_LOG_TXNSTAGE_PROCESS = "PROCESS";
    public String TXN_LOG_TXNSTAGE_INVAL = "VAL";
    public String TXN_LOG_TXNSTAGE_INTOP = "TOP";
    public String TXN_LOG_TXNSTAGE_CREDITBACK = "CRBACK";
    public String TXN_LOG_TXNSTAGE_GETCONN = "CONN";
    public String TXN_LOG_TXNSTAGE_SENDREQ = "SEND";
    public String TXN_LOG_TXNSTAGE_GETRESPONSE = "RESP";

    public String TXN_LOG_STATUS_SUCCESS = "SU";
    public String TXN_LOG_STATUS_FAIL = "FA";
    public String TXN_LOG_STATUS_UNDERPROCESS = "UP";
    // public String TXN_LOG_REQTYPE_RES="RES";
    public String TRANSFER_PROFILE_STATUS_DELETE = "N";
    public String BARRED_SUBSCRIBER_SELF_RSN = "SELF";

    public String INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION = "V";
    public String INTERFACE_NETWORK_PREFIX_UPDATE_ACTION = "U";
    public String INTERFACE_NETWORK_PREFIX_METHOD_TYPE_PRE = "PRE";
    public String INTERFACE_NETWORK_PREFIX_METHOD_TYPE_POST = "POST";
    public String C2C_TRANSFER_RULE_FIRST_APPROVAL_LIMIT = "999999998";
    public String C2C_TRANSFER_RULE_SECOND_APPROVAL_LIMIT = "999999999";
    public String KEY_VALUE_C2C_STATUS = "C2S_STATUS"; // disscussed with manoj
    public String USAGE_TYPE = "USAGE";// for status
    public String KEY_VALUE_IN_RESPONSE_CODE = "IN_RESP_CD";

    public String USER_NAME_PREFIX_TYPE = "USRPX";
    public String DOMAIN_TYPE_CODE = "OPERATOR";
    public String TRANSFER_TYPE = "TRFT";

    public String OUTLET_TYPE = "OLTYP";
    public String DB_FLAG_UPDATE = "U";
    public String DB_FLAG_INSERT = "I";

    public String NETWORK_TYPE_DEFAULT = "C";
    public String SERVICE_KEYWORD_STATUS_TYPE = "SKSTA"; // STATUS TYPE FOR
                                                         // SERVICE KEYWORD
                                                         // MODULE
    public String BARRING_TYPE = "BARTP";
    public String ROUTING_SUBSCRIBER_TYPE = "RSTP";

    public int P2P_MESSAGE_LENGTH_CHANGE_LANGUAGE = 3;
    public int C2S_MESSAGE_LENGTH_CHANGE_LANGUAGE = 3;

    // added by ankit singhal for purging
    public String SUB_TRA = "SUBSCRIBER_TRANSFERS";
    public String TRA_ITEMS = "TRANSFER_ITEMS";
    public String ALL = "ALL";
    public String CIRCLE_NETWORK_TYPE = "C";
    public String KEY_VALUE_P2P_STATUS = "P2P_STATUS";
    public String TXN_STATUS_SUCCESS = "200";

    public String PREFERENCE_TYPE = "PRFTP";

    public String NODATA_ENTERED = "NODATA";
    public String P2P_BARTYPE_LOOKUP_CODE = "P2PBARTYPE";
    public String C2S_BARTYPE_LOOKUP_CODE = "C2SBARTYPE";
    public String CHANNLE_USER_BARTYPE_LOOKUP_CODE = "CHLBARTYPE";
    public String CHNLCATEGORY = "CHUSR";
    public String AGENTCATEGORY = "AGENT";
    public String AGENT_ALLOWED = "Y";
    public String SUB_SERVICES = "SBSER";
    public String TRANSFER_TYPE_TRANSFER = "TRF";
    public String TRANSFER_TYPE_SALE = "SALE";
    public String TRANSFER_TYPE_FOR_TRFRULES = "TRFTY";
    public String AGENT_ALLOWED_YES = "Y";
    public String AGENT_ALLOWED_NO = "N";
    public String CATEGORY_TYPE_CHANNELUSER = "CHUSR";
    public String CATEGORY_TYPE_AGENT = "AGENT";
    public String CATEGORY_TYPE_CODE = "CATTY";

    public String LOOKUP_CHNL_USER_ACCESS_TYPE = "ASTYP";
    public String LOOKUP_LOGIN_ID = "LOGIN";
    public String LOOKUP_MSISDN = "MSISDN";
    public String AGENT_CAT_CODE_APPEND = "A";

    public String P2P_ERRCODE_VALUS = "P2P_ERR_CD";
    public String C2S_ERRCODE_VALUS = "C2S_ERR_CD";
    public String TRANSFER_TYPE_RECON = "RECON";
    public String PARENT_PROFILE_ID_USER = "USER";
    public String PARENT_PROFILE_ID_CATEGORY = "CAT";

    public String SERVICE_TYPE_CHNL_RECHARGE = "RC";
    public String SERVICE_TYPE_CHNL_CHANGEPIN = "C2SCPN";
    public String SERVICE_TYPE_CHNL_LANG_NOTIFICATION = "CCHLAN";
    public String SERVICE_TYPE_CHNL_TRANSFER = "TRF";
    public String SERVICE_TYPE_CHNL_WITHDRAW = "WD";
    public String SERVICE_TYPE_CHNL_RETURN = "RET";
    public String SERVICE_TYPE_SIDREG = "REGSID";
    // if value of any of these changes then change it in the messageResources
    // value of key
    // restrictedsubs.scheduletopupdetails.file.label.subservice AND
    // restrictedsubs.rescheduletopupdetails.file.label.subservice
    // this key is used in the RESTRICTED MODULE for scheduleing/Rescheduleing

    public int CHNL_SELECTOR_CVG_VALUE = 1;
    public int CHNL_SELECTOR_VG_VALUE = 3;
    public int CHNL_SELECTOR_C_VALUE = 2;

    public int CHNL_LOCALE_LANG1_VALUE = 0;
    public int CHNL_LOCALE_LANG2_VALUE = 1;

    // public String CHNL_SELECTOR_CVG="CVG";
    // public String CHNL_SELECTOR_VG="VG";
    // public String CHNL_SELECTOR_C="C";

    public String FOC_ORDER_APPROVAL_LVL = "FOC_ODR_APPROVAL_LVL";
    // public String SUB_SERVICE_TYPE_VG="VG";

    public String TRANSFER_TYPE_BA_ADJ_DR = "BA_DR";
    public String TRANSFER_TYPE_BA_ADJ_CR = "BA_CR";
    public String WEB_LANGUAGE_TYPE = "WLTYP";

    // Added by Ankit Singhal for purging
    public String CHNL_TRA_ITEMS = "CHANNEL_TRANSFERS_ITEMS";
    public String CHNL_TRA = "CHANNEL_TRANSFERS";
    public String NET_STK_TRA = "NETWORK_STOCK_TRANSACTIONS";
    public String NET_STK_TRA_ITEMS = "NETWORK_STOCK_TRANS_ITEMS";
    public String C2S_TRA = "C2S_TRANSFERS";
    public String C2S_TRA_ITEMS = "C2S_TRANSFER_ITEMS";

    public String FREQUENCY_MINUTS = "MINUTES";
    public String FREQUENCY_HOUR = "HOUR";
    public String FREQUENCY_DAY = "DAY";
    public String FREQUENCY_MONTH = "MONTH";
    public String FREQUENCY_YEAR = "YEAR";

    public String PREFERENCE_VALUE_TYPE = "VALTP";
    public String NETWORK_STOCK = "NWSTOCK";

    public String SERVICE_TYPE_FOR_SLAB = "SERV";
    public String SLAB_ID = "SLABID";

    public String MAX_LONG_VALUE = "999999999999";
    public String TRANSACTION_SOURCE_TYPE = "SRTYP";

    public String TRANSFER_TYPE_RCH_DEBIT = "RCH_DR";

    public String C2C_TRANSFER_TYPE = "C2CTR";

    public String SUB_SERVICES_FOR_TRANSFERRULE = "SSTTR";

    // Added by Ankit Singhal for corporate
    public String STATUS_DEASSOCIATED = "D";

    // Restricted Msisdns Module(Amit Ruwali)
    public String LOOKUP_TYPE_RES_MSISDN_STATUS = "RESST";
    public String LOOKUP_TYPE_BLACK_LIST_STATUS = "BLKST";

    public String RES_MSISDN_STATUS_NEW = "W";
    public String RES_MSISDN_STATUS_APPROVED = "A";
    public String RES_MSISDN_STATUS_SUSPENDED = "S";
    public String RES_MSISDN_STATUS_ASSOCIATED = "Y";
    public String RES_MSISDN_BLACKLIST_STATUS = "Y";
    public String RES_MSISDN_UNBLACKLIST_STATUS = "N";
    public String RES_MSISDN_STATUS_DELETED = "N";
    // Schedule TopUp status
    public String SCHEDULE_STATUS_SCHEDULED = "S";
    public String SCHEDULE_STATUS_UNDERPROCESSED = "U";
    public String SCHEDULE_STATUS_EXECUTED = "E";
    public String SCHEDULE_STATUS_CANCELED = "C";

    public String SCHEDULE_BATCH_ID = "SB";
    public String SCHEDULE_BATCH_STATUS_LOOKUP_TYPE = "SCHE";
    public String RES_MSISDN_STATUS_DEASSOCIATE = "A";
    // ADDED BY ASHISH FOR SCHEDULED TOPUP-PROCESS DATE 12/04/06
    public String REST_SCH_BATCH_STATUS_UNDER_PROCESS = "U";
    public String REST_SCH_BATCH_STATUS_SCHEDULED = "S";
    public String REST_SCH_BATCH_STATUS_EXECUTED = "E";
    public String SCHEDULED_BATCH_DETAIL_STATUS_EXECUTED = "E";
    public String REST_SCH_SUBS_MSISDN_STATUS = "Y";

    // User Phone Primary Number
    public String USER_PHONE_PRIM_STATUS = "Y";

    // FOR RESTRICTED APPROVAL Ashish
    public String RES_MSISDN_STATUS_REJECT = "R";
    public String RES_MSISDN_STATUS_DISCARD = "D";
    public String DOMAINS_NOTFIXED_NOTASSIGNED = "N";

    // entries for the transfer rules changes
    public String NOT_APPLICABLE = "NA";
    public String UNCONTROLL_TXN_LEVEL = "UNCTL";
    public String CONTROLL_TXN_LEVEL = "CNTRL";
    public String FIXED_LEVEL = "FIXDL";

    // entried for the control or uncontrol transactions
    public String CHANNEL_TRANSACTION_CONTROL = "C";
    public String CHANNEL_TRANSACTION_UNCONTROL = "U";

    // entries for the channel transfer controlling parametes.
    public String CHANNEL_TRANSFER_LEVEL_SELF = "SELF";
    public String CHANNEL_TRANSFER_LEVEL_PARENT = "PARENT";
    public String CHANNEL_TRANSFER_LEVEL_OWNER = "OWNER";
    public String CHANNEL_TRANSFER_LEVEL_DOMAIN = "DOMAIN";
    public String CHANNEL_TRANSFER_LEVEL_DOMAINTYPE = "DOMAINTYPE";
    public String CHANNEL_TRANSFER_LEVEL_SYSTEM = "SYSTEM";

    public String CHANNEL_TRANSFER_FIXED_LEVEL_PARENT = "PARENT";
    public String CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY = "HIERARCHY";

    // added for postpaid controller on date=16/05/06
    public String STATUS_QUEUE_AVAILABLE = "0";
    public String STATUS_QUEUE_FAIL = "5";

    public String WHITE_LIST_MOVEMENT_CODE = "MCDE";
    public String WHITE_LIST_STATUS = "WLSTA";
    public String SERVICE_TYPE_BILLPAYMENT = "BILLPMT";

    // for the admin logger for channel transfer rules
    public String LOGGER_TRANSFER_RULE_SOURCE = "TRANSFER_RULE";

    // Process ID of CDR generation
    public String PROCESS_ID_CDR = "CDR";
    // Interface ID of CDR generation
    public String INTERFACE_ID_CDR = "INT003";
    // added for PPB service type
    public String SERVICE_TYPE_CHNL_BILLPAY = "PPB";

    // for channel user transfer controlling level
    public String CONTROL_LEVEL_ADJ = "A";

    // Added for modify instance load (10-06-06)
    public String LOAD_TYPE_TPS_TPS = "Y";
    public String LOAD_TYPE_TPS_TRANSACTION = "N";
    public String INSTANCE_TYPE_SMS = "SMS";
    public String INSTANCE_TYPE_WEB = "WEB";
    // ENTRY FOR ADMIN LOGGER
    public String LOGGER_NETWORK_PREFIXES = "NETWORK_PREFIXES";

    public String GRPT_CONTROL_LEVEL_USERID = "U";
    public String GRPT_CONTROL_LEVEL_MSISDN = "M";
    public String GRPT_TYPE_CHARGING = "CHRG";
    public String GRPT_TYPE_CONTROLLING = "CTRL";
    public String GRPT_TYPE_FREQUENCY_DAILY = "D";
    public String GRPT_TYPE_FREQUENCY_MONTHLY = "M";

    public String CHANNEL_TRANSFER_BATCH_FOC_STATUS_LOOKUP_TYPE = "BTSTA";
    public String CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN = "OPEN";
    public String CHANNEL_TRANSFER_BATCH_FOC_STATUS_CLOSE = "CLOSE";
    public String CHANNEL_TRANSFER_BATCH_FOC_STATUS_CANCEL = "CANCEL";
    public String CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS = "UNDPROCESS";
    public String CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED = "P";
    public String CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_SCHEDULED = "S";

    public String FOC_BATCH_TRANSACTION_ID = "FB";

    // Batch user creation
    public String BATCH_USR_SERVICE_LIST = "SERVICE_LIST";
    public String BATCH_USR_GEOGRAPHY_LIST = "GEOGRAPHY_LIST";
    public String BATCH_USR_GEOGRAPHY_TYPE_LIST = "GEOGRAPHY_TYPE_LIST";
    public String BATCH_USR_CATEGORY_HIERARCHY_LIST = "CATEGORY_HIERARCHY_LIST";
    public String BATCH_USR_CATEGORY_LIST = "CATEGORY_LIST";
    public String BATCH_USR_GRADE_LIST = "GRADE_LIST";
    public String BATCH_USR_TRANSFER_CONTROL_PRF_LIST = "TRANSFER_CONTROL_PRF_LIST";
    public String BATCH_USR_COMMISION_PRF_LIST = "COMMISION_PRF_LIST";
    public String BATCH_USR_GROUP_ROLE_LIST = "GROUP_ROLE_LIST";
    public String BATCH_USR_USER_PREFIX_LIST = "USER_PREFIX_LIST";
    public String BATCH_USR_OUTLET_LIST = "OUTLET_LIST";
    public String BATCH_USR_SUBOUTLET_LIST = "SUBOUTLET_LIST";
    public String BATCH_USR_DOMAIN_NAME = "DOMAIN_NAME";
    public String BATCH_USR_CREATED_BY = "CREATED_BY";
    public String BATCH_USR_GEOGRAPHY_NAME = "GEOGRAPHY_NAME";
    public String GRPH_DOMAIN_CODE = "GRAPH DOMAIN CODE";
    // Changes Made by Puneet
    public String USER_MIG_HEADING = "USER MIGRATION";
    // ends
    public int CACHE_GROUP_TYPE_PROFILE = 28;

    public String MANUAL_USR_CREATION_TYPE = "M";
    public String BATCH_USR_CREATION_TYPE = "B";
    public String USR_BATCH_STATUS_OPEN = "O";
    public String USR_BATCH_STATUS_CLOSE = "C";
    public String USR_BATCH_STATUS_REJECT = "R";
    public String USR_BATCH_STATUS_UNDERPROCESS = "U";

    public String USR_PHONE_ID = "PHONE_ID";
    public String BULK_USR_BATCH_ID = "BATCH_ID";
    public String BULK_USR_BATCH_TYPE = "BULK_USR_CREATION";
    public String BULK_USR_STATUS_ACTIVE = "Y";
    public String BULK_USR_STATUS_REJECT = "N";
    public String BULK_USR_STATUS_DISCARD = "D";
    public String BULK_USR_ID_PREFIX = "BU";
    public String OUTLET_TYPE_DEFAULT = "TCOM";

    public String ROLE_TYPE_FOR_GROUP_ROLE = "A";
    public String BATCH_USR_PROCESS_ID = "BULKUSER";
    public String BATCH_STATUS_LOOKUP = "BHSTA";
    public int CACHE_NETWORK_INTERFACE_MODULE = 29; // is define for network
                                                    // interface module cache
    public int CACHE_INTERFACE_ROUTING_CONTROL = 30;// is defined for interface
                                                    // routing control cache

    // added by sandeep goel for the commission profile status change logger
    public String LOGGER_COMMISSION_PROFILE_SOURCE = "COMMISSION_PROFILE";
    // ends here

    // added by sandeep goel for the foc by batch status in the process status
    // table
    public String FOC_BATCH_PROCESS_ID = "FOCBATCH";
    public String USR_CREATION_TYPE = "UCRTY";
    // ends here
    public String P2P_RECON_INTERFACE_WISE_PROCESS_ID = "P2PRECONINTRPT";
    public String P2P_RECON_SERVICE_WISE_PROCESS_ID = "P2PRECONSERRPT";
    public String C2S_RECON_INTERFACE_WISE_PROCESS_ID = "C2SRECONINTRPT";
    public String KEY_VALUE_TYPE_REOCN = "RECON"; // For the p2p and c2s
                                                  // reconcilation

    public String ADJUSTMENT_TRANSACTION_ID = "AD"; // for the Adjustment txn ID
    public String DAILY_STOCK_CREATION_TYPE_MAN = "M"; // insertion by the txn
    public String DAILY_BALANCE_CREATION_TYPE_MAN = "M"; // insertion by the txn

    public String SMS_LOCALE = "SMS";// Check the locale is applicable for SMS
    public String BOTH_LOCALE = "BOTH";// Check the locale is applicable to WEB
    public String WEB_LOCALE = "WEB";// Check the locale is applicable to
                                     // BOTH(WEB and SMS)
    public String LANG1_MESSAGE = "LANG1";// Send language 1 message for the
                                          // locale
    public String LANG2_MESSAGE = "LANG2";// Send language 2 message for the
                                          // locale
    public short FORM_NCHAR = 2;// Set the char set value to 2 for prepared
                                // statement
    public short FORM_CHAR = 1;// Set the char set value to 1 for prepared
                               // statement

    public String SERVICE_TYPE_CHNL_O2C_IN = "O2CIN";
    public String SERVICE_TYPE_CHNL_O2C_INTR = "O2CINTR";
    public String SERVICE_TYPE_CHNL_O2C_RET = "O2CRET";
    public String SERVICE_TYPE_CHNL_O2C_WTDW = "O2CWD";

    // added for O2C transferm, return & withdrawal
    public String CATEGORY_CODE_NETWORK_ADMIN = "NWADM";

    public String MSISDN_VALIDATION = "M";
    public String OTHER_VALIDATION = "O";
    public String BOTH_VALIDATION = "B";

    public int C2S_MESSAGE_LENGTH_LAST_RECHARGE = 3;
    public String SERVICE_TYPE_RECHARGE_STATUS = "RS";

    // VOMS integration start for EVD/EVR controller
    public String INTERFACE_CATEGORY_VOMS = "VOMS";
    public String SERVICE_TYPE_EVR = "EVR";
    public String SERVICE_TYPE_EVD = "EVD";
    public String ID_GEN_EVD_TRANSFER_NO = "EVD";
    public String PIN_SENT_RET = "R";

    public String SMS_STATUS_NOTFOUND = "NOTFOUND";
    public String SMS_STATUS_FOUND = "FOUND";
    public String SMS_STATUS_EXCEPTION = "EXCEPTION";
    // VOMS integration end for EVD/EVR controller

    // added by Siddhartha for Voucher Pin Resend
    public String MSISDN_CHECK_RETA = "RETAILER";
    public String MSISDN_CHECK_DIST = "DIST";

    // control preferences
    public String INTERFACE_STATUS_TYPE_MANUAL = "M";
    public String INTERFACE_STATUS_TYPE_AUTO = "A";

    public String LOOKUP_TYPE_CONTROL = "CTRTY";

    // Batch operator user creation
    public String BATCH_OPT_USR_CATEGORY_LIST = "CATEGORY_LIST";
    public String BATCH_OPT_USR_CATEGORY_CODE = "CATEGORY_CODE";
    public String BATCH_OPT_USR_CATEGORY_NAME = "CATEGORY_NAME";
    public String BATCH_OPT_USR_CREATED_BY = "CREATED_BY";
    public String BATCH_OPT_USER_PREFIX_LIST = "USER_PREFIX_LIST";
    public String BATCH_OPT_USR_SERVICE_LIST = "SERVICE_LIST";
    public String BATCH_OPT_USR_STATUS_LIST = "STATUS_LIST";
    public String BATCH_OPT_USR_DIVDEPT_LIST = "DIVDEPT_LIST";
    public String BATCH_OPT_USR_ASSIGN_ROLES = "ASSIGN_ROLES";
    public String BATCH_OPT_USR_GEOGRAPHY_LIST = "GEOGRAPHY_LIST";
    public String BATCH_OPT_USR_DOMAIN_LIST = "DOMAIN_LIST";
    public String BATCH_OPT_USR_PRODUCT_LIST = "PRODUCT_LIST";
    public String BATCH_OPT_USR_PROCESS_ID = "BATCHOPTUSER";
    public String BATCH_OPT_USR_ID_PREFIX = "BOU";
    public String BATCH_OPT_USR_ID = "OPTUSERID";
    public String BATCH_OPT_USR_BATCH_TYPE = "BATCH_OPT_USR_CREATE";

    public String BATCH_USR_CATEGORY_NAME = "CATEGORY_NAME"; // Added by Sanjeew
                                                             // 30/03/07
    public String BATCH_USR_CATEGORY_VO = "CATEGORY_VO"; // Added by Sanjeew
                                                         // 30/03/07
    public String BATCH_USR_EXCEL_DATA = "BATCH_USR_EXCEL_DATA";// Added by
                                                                // Sanjeew
                                                                // 02/04/07
    public String BATCH_USR_ROLE_CODE_LIST = "BATCH_USR_GROUP_ROLE_CODE_LIST";// Added
                                                                              // by
                                                                              // Sanjeew
                                                                              // 02/04/07
    public String PORTED_TYPE = "PORTP";// Mobile number portability
    public String PORTED_IN = "IN";
    public String PORTED_OUT = "OUT";
    public String OPERATOR_TYPE_PORT = "PORT";
    public int CACHE_PAYMENT_METHOD = 31;
    public int CACHE_SERVICE_SELECTOR_MAPPING = 32;

    // Batch transfer rules creation
    public String BATCH_TRF_RULES_PROCESS_ID = "BATCHTRFRULES";
    public String TRF_RULES_BATCH_PREFIX = "TR";
    public String TRF_RULES_BATCH_TYPE = "TRF_RULES_CREATION";
    public String TRF_RULES_ID = "TRFRULEID";
    public String TRF_RULES_BATCH_ID = "BATCH_ID";
    public String TRF_RULE_BATCH_STATUS_OPEN = "O";
    public String TRF_RULE_BATCH_STATUS_CLOSE = "C";
    public String TRF_RULE_BATCH_STATUS_REJECT = "R";
    public String TRF_RULE_BATCH_STATUS_UNDERPROCESS = "U";
    public String SERVICE_TYPE_FOR_EVD = "EVD";
    public String ACCOUNT_TYPE_MAIN = "MAIN";
    public String ACCOUNT_TYPE_TOTAL = "TOTAL";
    // Added for suspend resume service.
    public String USER_STATUS_ACTIVE_R = "R";

    public String SERVICE_TYPE_MVD = "MVD";
    // Added for Rusume/Suspend of all P2P Services
    public String SERVICE_TYPE_P2PSUSPEND = "PSUS";
    public String SERVICE_TYPE_P2PRESUME = "PRES";

    // Added for Add/Delete/List Buddy
    public String SERVICE_TYPE_ADD_BUDDY = "PADD";
    public String SERVICE_TYPE_DELETE_BUDDY = "PDEL";
    public String SERVICE_TYPE_LIST_BUDDY = "PLIST";

    // Added for Delete Buddy List by harsh on 09Aug12
    public String SERVICE_TYPE_DELETE_BUDDY_LIST = "MULTDEL";

    public String SERVICE_TYPE_CHNL_BALANCE_ENQUIRY = "C2SBAL"; // added for
                                                                // Balance
                                                                // Enquiry
                                                                // 03/05/07
    public String SERVICE_TYPE_CHNL_DAILY_STATUS_REPORT = "C2SDAILYTR"; // added
                                                                        // for
                                                                        // Daily
                                                                        // Status
                                                                        // Report
                                                                        // 03/05/07
    public String SERVICE_TYPE_LAST_TRANSFER_STATUS = "C2SLASTTRF"; // added for
                                                                    // Last
                                                                    // Transfer
                                                                    // Status(RP2P)
                                                                    // 03/05/07
    public String SERVICE_TYPE_P2P_LAST_TRANSFER_STATUS = "PLT"; // added for
                                                                 // Last
                                                                 // Transfer
                                                                 // Status(P2P)
                                                                 // 03/05/07
    public String SERVICE_TYPE_SELF_BAR = "BARUSER";// self Bar
    public String SERVICE_TYPE_CHNL_EVD = "EVD";
    public String SERVICE_TYPE_CHNL_BALANCE_XML = "BAL";

    // Added for Multple Electronic Voucher Distribution of all C2S Services
    public String SERVICE_TYPE_MEVD_REQUEST = "MEVDREQ";
    // Added for Utility Bill Payment of all C2S Services
    public String SERVICE_TYPE_UBILLPAYMENT_REQUEST = "UBPREQ";
    public String SERVICE_TYPE_UTILITY_BILLPAY = "UBP";

    public String CARD_GROUP_SET_TYPE = "SETTY";
    public String TRANSFER_RULE_PROMOTIONAL = "P";
    public String TRANSFER_RULE_NORMAL = "N";
    public int PROMO_TRF_RULE_LVL_USR_CODE = 1;
    public int PROMO_TRF_RULE_LVL_GRADE_CODE = 2;
    public int PROMO_TRF_RULE_LVL_CATEGORY_CODE = 3;
    public int PROMO_TRF_RULE_LVL_GEOGRAPHY_CODE = 4;
    public int PROMO_TRF_RULE_LVL_PREFIX_ID = 5;// added by rahul for prefix id
                                                // based checks

    // added by sanjeew for Batch Promotional Transfer rule
    public String CARDGROUP_SET_TYPE_PROMOTIONAL = "P";
    public String CARDGROUP_SET_TYPE_NORMAL = "N";
    public String PROMOTIONAL_LEVEL = "PROMO";
    public String PROMOTIONAL_LEVEL_USER = "USR";
    public String PROMOTIONAL_LEVEL_GRADE = "GRD";
    // VFE public String PROMOTIONAL_LEVEL_GRADE = "GDR";
    public String PROMOTIONAL_LEVEL_GEOGRAPHY = "GRP";
    public String PROMOTIONAL_LEVEL_CATEGORY = "CAT";
    // Prefix ID based promotional rule
    public String PROMOTIONAL_LEVEL_PREFIXID = "PRX";

    public String PROMOTIONAL_INTERFACE_CATEGORY_CLASS = "SERVINTCLASS";
    public String PROMOTIONAL_BATCH_TRF_RULE = "Promotional transfer rule";
    public String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_USR = "USR";
    public String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CAT = "CAT";
    public String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRP = "GRP";
    // public String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD="GDR";
    public String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD = "GRD";
    public String PROMOTIONAL_BATCH_TRF_MODULE = "C2S";
    public String PROMOTIONAL_BATCH_TRF_RULE_TYP = "P";
    public String PROMOTIONAL_BATCH_TRF_RULE_STATUS = "Y";
    public String PROMOTIONAL_BATCH_TRF_RULE_SENDER_SERVICE_CLASS_ID = "ALL";
    public String PROMOTIONAL_BATCH_TRF_RULE_MSISDN_NOTVALIED = "Invalied Mobile Number";

    // added by siddhartha for Account Master Details.
    public String ACCOUNT_MASTER_INTERFACE_ACCOUNT_TYPE = "ACCTY";
    public String ACCOUNT_MASTER_STATUS_LIST = "ACCST";
    public String ACCOUNT_TYPE_BONUS = "B";
    public String ACCOUNT_TYPE_NORMAL = "N";
    public String ACCOUNT_MODE_SINGLE_REQ = "SR";
    public String ACCOUNT_MODE_MULTIPLE_REQ = "MR";

    // added by ashish srivastav for Iccid Batch Delete
    public String ICCID_DELETEABLE = "DELETEABLE";
    public String ICCID_USER_ASSOCIATED = "ASSOCIATEDCHNLUSER";
    public String ICCID_MSISDN_ASSOCIATED = "ASSOCIATEDTOMSISDN";
    public String ICCID_NOT_EXISTING = "NOTEXIST";

    // for Zebra and Tango added by sanjeew date 06/07/07
    public String M_PAY_PROFILE_LIST = "MPAYPROFILELIST";
    // end Zebra and Tango

    public String LOOKUP_TYPE_CONTROL_CAT = "CATPRF";
    public String LOOKUP_TYPE_CONTROL_ZONE = "ZONEPRF";
    public String LOOKUP_TYPE_CONTROL_INTERFACE = "INTPREF";
    public String LOOKUP_TYPE_CONTROL_SERVICE_TYPE = "SERTYPPREF";

    // Added by Vinay for ExternalFile(IMA File) on 24/07/07
    public String EXTERAL_FILE_PROCESS_ID = "EXTERNLFILERPT";

    public String SERVICE_TYPE_P2PRECHARGE_SINGLEREQUEST = "SPRC";

    public String SRV_CLASS_USER_TYPE = "SCUTY";
    public String SRV_CLASS_STATUS = "LKTST";
    public String SRV_CLASS_MODULE = "MODTY";// used for service eligibility
                                             // module.

    // TIME IN MILISECONDS i.e 5 Seconds for Aktel::AshishS
    public long RECEIVER_UNDERPROCESS_UNBLOCK_TIME = 300000;

    // added by ranjana for C2S transfer bill payment
    public String SERVICE_TYPE_POSTPAID_BILL_PAYMENT = "PPB";

    // add for password management by santanu
    public String USER_PASSWORD_MANAGEMENT = "PWD";
    public String USER_PIN_MANAGEMENT = "PIN";
    public String USER_FIXED_DOMAIN = "F";
    public String PWD_DOMAIN_CODE = "OPT";
    public String PIN_USER_CP2P = "CP2P";
    public String PIN_USER_CHU = "CHU";
    public String PIN_USER_STAFF = "STAFF";
    public String PWD_USER_NAME = "ALL";
    public String PWD_USER_OPTU = "OPTU";
    public String PWD_USER_SUADM = "SUADM";
    public String PWD_CAT_CODE_SUADM = "SUADM";
    public String PWD_CAT_CODE_NWADM = "NWADM";
    public String PWD_CAT_CODE_CCE = "CCE";
    public String LOOKUP_USER_TYPE = "USRTP";
    public String USER_TYPE_OPT = "OPERATOR";
    public String USER_TYPE_CHANNEL = "CHANNEL";
    public String USER_TYPE_STAFF = "STAFF";
    public String USER_TYPE_P2P = "P2P";

    // added by PN for cellplus p2p controller
    public String P2P_SENDER_ACCOUNT_STATUS_ACTIVE_ONIN = "TRUE";
    public String P2P_RECEIVER_ACCOUNT_STATUS_ACTIVE_ONIN = "TRUE";

    public String P2P_SENDER_ACCOUNT_STATUS_INACTIVE_ONIN = "INACTIVE";
    public String P2P_RECEIVER_ACCOUNT_STATUS_INACTIVE_ONIN = "INACTIVE";
    public static long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;

    // for C2S Enquiry through external system
    public String SERVICE_TYPE_C2S_ENQUIRY = "C2SENQ";

    public String SUFIX_ADJUST_TXN_ID_NW = "N";
    public String SUFIX_ADJUST_TXN_ID_USER = "U";

    // For Black list restricted subscribers not allowed for recharge or for
    // CP2P services.
    public String CP2P_PAYER = "CP2PAYER";
    public String CP2P_PAYEE = "CP2PPAYEE";
    public String C2S_PAYEE = "C2SPAYEE";

    // added by Vipul for Modify status C2C transfer rule
    public String TRANSFER_RULE_TYPE = "TRTYP";// shows controlled or
                                               // uncontrolled transfer
    // added by Vipul for Modify status O2C & C2C transfer rule
    public String TRANSFER_RULE_STATUS = "TRST";// Status suspended or active
    public String REQUEST_SOURCE_TYPE_DUMMY = "DUMMY";// Dummy entry for Apache
                                                      // if used for load
                                                      // balancing.

    // Gift Recharge
    public String C2S_REC_GEN_FAIL_MSG_REQD_V_FOR_GIFTER = "C2S_REC_GEN_FAIL_MSG_REQD_V_FOR_GIFTER";
    public String C2S_REC_GEN_FAIL_MSG_REQD_T_FOR_GIFTER = "C2S_REC_GEN_FAIL_MSG_REQD_T_FOR_GIFTER";
    public String SERVICE_TYPE_CHNL_COMMON_RECHARGE = "CRC";
    public String SERVICE_TYPE_CHANNEL_GIFT_RECHARGE = "GRC";

    // schedule batch recharge (Manisha 29/04/08)
    public String BATCH_TYPE_CORPORATE = "CORPORATE";
    public String BATCH_TYPE_NORMAL = "NORMAL";
    public String BATCH_TYPE_BOTH = "BOTH";
    // gift recharge For web
    public String GIFT_RECHARGE_CODE = "GRC";
    public String SERVICE_TYPE_P2PRECHARGEWITHVALEXT = "VU";

    // gift recharge
    public String SERVICE_TYPE_GIFT_RECHARGE = "GRC";
    // for category List Management
    public String SELECT_PARENT = "P";
    public String SELECT_OWNER = "O";
    public String LOOKUP_CP2P_LIST_LEVEL = "P2PWL";
    public String CP2P_WITHIN_LIST_LEVEL_OWNER = "O";
    public String CP2P_WITHIN_LIST_LEVEL_PARENT = "P";
    public String CP2P_WITHIN_LIST_LEVEL_DOMAIN = "D";
    public String MODULE_TYPE_BOTH = "BOTH";
    public String FILE_CONTENT_TYPE_CSV = "CSV";
    public String FILE_CONTENT_TYPE_XLS = "XLS";
    public String FILE_CONTENT_TYPE_PLAIN_TEXT = "PLAIN_TEXT";

    // Batch C2C Transfer
    public String CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN = "OPEN";
    public String CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED = "P";
    public String CHANNEL_TRANSFER_BATCH_C2C_STATUS_CANCEL = "CANCEL";
    public String CHANNEL_TRANSFER_BATCH_C2C_STATUS_CLOSE = "CLOSE";
    public String CHANNEL_TRANSFER_BATCH_C2C_STATUS_UNDERPROCESS = "UNDPROCESS";
    public String C2C_BATCH_TRANSACTION_ID = "CB";
    public String C2C_BATCH_PROCESS_ID = "C2CBATCH";

    public String CHANNEL_TRANSFER_TYPE_TRANSFER = "TRANSFER";
    public String CHANNEL_CATEGORY_TYPE_SALE = "SALE";
    public String CHANNEL_TRANSFER_TYPE_WITHDRAW = "WITHDRAW";
    public String CHANNEL__CATEGORY_TYPE_TRANSFER = "TRF";
    public String CHANNEL_TRANSFER_ORDER_APPROVE = "APPRV";
    // user's assigned roles
    public String USERTYPE = "OPT";

    // added for USSD Recharge(CDMA,PSTN,Broadband)
    public String SERVICE_TYPE_CHNL_RECHARGE_CDMA = "CDMARC";
    public String SERVICE_TYPE_CHNL_RECHARGE_PSTN = "PSTNRC";
    public String SERVICE_TYPE_CHNL_RECHARGE_INTR = "INTRRC";

    // added for Bank Recharge(CDMA,PSTN,Broadband)
    public String SERVICE_TYPE_EXT_CHNL_RECHARGE_CDMA = "CDMARC";
    public String SERVICE_TYPE_EXT_CHNL_RECHARGE_PSTN = "PSTNRC";
    public String SERVICE_TYPE_EXT_CHNL_RECHARGE_INTR = "INTRRC";
    // added by Gopal for Batch C2C Transfer approval,
    public String C2C_BATCH_APPROVAL_LEVEL = "C2C_BATCH_APPROVAL_LVL";
    public String CHANGE_PIN_SERVICE_TYPE = "C2SCPN,CPN";
    // Added for Controller(OrderCredit, OrderLine, BarredMsisdn)

    public int ORDER_CREDIT_MESSAGE_LENGTH = 3;
    public int ORDER_CREDIT_LENGTH_MAX_LENGTH = 9;
    public int ORDER_MOBILE_MESSAGE_LENGTH = 4;
    public int ORDER_MOBILE_QUANTITY_MAX_LENGTH = 4;
    public String CHANEL_BARRED_TYPE_SELF = "SELF";
    public String CHANEL_BARRED_USER_TYPE_SENDER = "SENDER";
    public String SERVICE_TYPE_EXT_CHNL_ORDER_LINE = "ORDL";
    public String SERVICE_TYPE_EXT_CHNL_ORDER_CREDIT = "ORDC";
    public String SERVICE_TYPE_EXT_CHNL_BARRED = "BAR";
    // batch C2S cardgroup modify
    public String BAT_MOD_C2S_CARDGROUP = "BATCH_MODIFY_CARDGROUP";
    public String BATCH_CARD_GROUP_EXCEL_DATA = "BATCH_CARD_GROUP_EXCEL_DATA";
    public String BAT_MOD_C2S_CG_PROCESS_ID = "BATCARDGROUP";
    // batch P2P cardgroup modify
    public String BAT_MOD_P2P_CG_PROCESS_ID = "BATCARDGROUPP2P";
    // added by rahul for add activation bonus
    public String PERIOD_TYPE = "PETYP";
    public String PROFIL_DETAIL_TYPE = "PRDTY";
    public String PROFILE_TRANS = "TRANS";
    public String PROFILE_VOL = "VOLUME";
    public String PROFILE_STATUS_ACTIVE = "Y";
    public String PROFILE_TYPE_ACTIVATION = "ACT";
    public String ACTIVATION_PROFILE_SETID = "PR_SETID";
    public String ACTIVATION_PROFILE_USER_TYPE_SENDER = "S";
    public String USER_SUB_TYPE_COUNT = "COUNT";
    public String USER_SUB_TYPE_AMOUNT = "AMOUNT";
    public String ACTIVATION_TYPE_C2S = "C2S";
    public String ACTIVATION_SUBSCRIBER_TYPE = "SUBAP";
    public String PROFILE_TYPE_ACTIVATION_BONUS = "ACT";
    public String PROFILE_TYPE_NONE = "NONE";
    public String PERIOD_TYPE_PCT = "PCT";

    public String ACT_PROF_TYPE = "ACT";

    public String SYSTEM = "SYSTEM";
    public String BUCKET_ONE = "1";
    public String NO_MAPPING_FOUND = "S";
    public String SUB_TYPE_AMOUNT = "AMOUNT";
    public String SUB_TYPE_COUNT = "COUNT";
    public String SERVICE_TYPE_PRE = "PRE";
    public String SERVICE_TYPE_POST = "POST";
    public String SERVICE_TYPE_ALL = "ALL";
    public String SERVICE_TYPE_BOTH = "BOTH";
    public String VOLUME_CALC_DONE = "V";

    // batch associate activation profile
    public String BATCH_ASSOCIATE_PROFILE_USERS = "ACTIVE_USERS";
    public String BATCH_ASSOCIATE_PROFILE_DOMAIN = "DOMAIN_NAME";
    public String BATCH_ASSOCIATE_PROFILE_CATEGORY = "CATEGORY_NAME";
    public String BATCH_ASSOCIATE_PROFILE_LIST = "ACTIVE_PROFILE_LIST";
    public String BATCH_ASSOCIATE_PROFILE_PROCESS_ID = "BATCHASSOCIATE";
    public String PERIOD_UNLIMITED = "UNLIMITED";

    public String SERVICE_TYPE_CHNL_VAS_CRBT = "CRBT";

    public String NETWORK_STOCK_TRANSACTION_COMMISSION = "COMMISSION";
    public String NETWORK_STOCK_TRANSACTION_COMMISSION_SUB_TYPE = "C";
    public int SMS_RECHARGE_MESSAGE_LENGTH = 3;

    // FOR IAT
    public String CONTROLLER = "CONTROLLER";
    public String IAT_TRANSACTION_TYPE = "IAT";
    public String IAT_SERVICE_TYPE_ROAM_RECHARGE = "RR";
    public String IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE = "IR";
    public String IAT_SERVICE_STATUS_ACTIVE = "Y";
    public String IAT_SERVICE_STATUS_INACTIVE = "S";
    public String IAT_NW_SERVICE_MAPP_ID_TYPE = "IATMP";
    public String IAT_COUNTRY_STATUS_ACTIVE = "Y";
    public String IAT_COUNTRY_STATUS_SUSPEND = "S";
    public String IAT_COUNTRY_STATUS_DELETED = "N";
    public String IAT_NETWORK_STATUS_ACTIVE = "Y";
    public String IAT_NETWORK_STATUS_SUSPEND = "S";
    public String IAT_NETWORK_STATUS_DELETED = "N";
    public String IAT_NW_COUNTRY_MAPP_ID_TYPE = "IATNWCNT";
    public String IAT_COUNTRY_ID_TYPE = "IATCNT";
    public int IAT_COUNTRY_MASTER_CACHE = 34;
    public int IAT_NETWORK_CACHE = 35;
    public String TXN_STATUS_FAIL = "206";

    public String SERVICE_TYPE_IAT = "IAT";
    public String IAT_REQUEST_GATEWAY_TYPE = "EXTGW";
    public String IAT_SOURCE_TYPE = "WEB";
    public String IAT_CHECK_STATUS = "Y";

    public String INTERFACE_CATEGORY_IAT = "IAT";

    // Added for bonus bundle mapping
    public int CACHE_BONUS_BUNDLES = 33;
    public String BONUS_BUNDLE_LIST = "BONUS_BUNDLE_LIST";

    // VIKRAM last 3 transfer service added for Last Transfer Report(c2s)
    public String SERVICE_TYPE_LASTX_TRANSFER_REPORT = "LASTXTRF";
    public String SERVICE_TYPE_C2S_LAST_X_TRANSFER = "C2S";
    public String SERVICE_TYPE_C2C_LAST_X_TRANSFER = "C2C";
    public String SERVICE_TYPE_O2C_LAST_X_TRANSFER = "O2C";

    // added for Customer enquiry(c2s)
    public String SERVICE_TYPE_CUSTX_ENQUIRY = "CUSTXTRF";
    // vikram for c2s recharge
    public String MULTIPLE_ENTRY_ALLOWED = "M";
    public int CACHE_PREFIX_SERVICE_MAPPING = 36;
    public String NETWORK_STOCK_TRANSACTION_DEDUCTION = "DEDUCT";
    public String STOCK_TXN_TYPE = "DEDUCT";
    // rev
    public String TRANSFER_TYPE_O2C_LOOKUP_TYPE = "OTRFT";
    public String TRANSFER_TYPE_REVERSE_LOOKUP_TYPE = "TRXT";
    public String TRANSFER_TYPE_REVERSE_ID_TYPE = "CX";
    public String TRANSFER_TYPE_REVERSE_EVENT_TYPE = "C2C_REVERSE_TRN";
    public String TRANSFER_TYPE_REVERSE_SUB_TYPE = "X";
    // reverse c2s txn
    public String TRANSFER_TYPE_C2S_REVERSE_EVENT_TYPE = "C2S_REVERSE_TRN";
    public String C2S_REVERSE_BALANCE_LOGER_TYPE = "REV_CR";
    public String C2S_TYPE_REVERSE_TRANSFER_LOOKUP_TYPE = "RC2S";

    // added by Amit Raheja for reverse txn
    public String TRANSFER_TYPE_O2C_REVERSE_ID_TYPE = "OX";

    public String LOOKUP_CSADMIN_USER_TYPE = "ADMUT";
    public String LOOKUP_OPT_USR_TYPE = "CSADM";
    public String CSADMIN_OPERATOR_USER = "OPTU";

    // vikram in bar unbar module
    public String BAR_TYPE_PARENT_BARRED = "BRPRT";
    public String BAR_REASON_PARENT_BARRED = "Parent is barred";

    public String NOT_AVAILABLE = "NA";
    public String NOT_AVAILABLE_DESC = "NOT AVAILABLE";
    public String ROLE_FOR_BOTH = "B";
    public String ROLE_FOR_STAFF = "S";
    public String LAST_TRANSACTION_O2C_TYPE = "O2C";
    public String CHANNEL_ENQUIRY = "ENQ";

    // added for multiple wallet feature
    public String MULTIPLE_WALLET_TYPE = "WLTYP";
    public String SALE_WALLET_TYPE = "SAL";
    public String FOC_WALLET_TYPE = "FOC";
    public String INCENTIVE_WALLET_TYPE = "INC";
    // Bonus bundle management
    public String BONUS_BUNDLE_TYPE = "BUNTP";

    public String GATEWAY_TYPE_PHYSICAL_POS = "EXPHPOS";
    public String GATEWAY_TYPE_SMS_POS = "SMSPOS";

    // lohit
    public String DP_BATCH_PROCESS_ID = "DPBATCH";
    public String DP_BATCH_TRANSACTION_ID = "DP";
    public String CHANNEL_TRANSFER_BATCH_DP_STATUS_LOOKUP_TYPE = "BTSTA";
    public String CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN = "OPEN";
    public String CHANNEL_TRANSFER_BATCH_DP_STATUS_CLOSE = "CLOSE";
    public String CHANNEL_TRANSFER_BATCH_DP_STATUS_CANCEL = "CANCEL";
    public String CHANNEL_TRANSFER_BATCH_DP_STATUS_UNDERPROCESS = "UNDPROCESS";
    public String CHANNEL_TRANSFER_BATCH_DP_ITEM_RCRDSTATUS_PROCESSED = "P";
    public String CHANNEL_TRANSFER_BATCH_DP_ITEM_RCRDSTATUS_SCHEDULED = "S";
    public String TRANSFER_TYPE_DP = "FOC";
    public String DP_ORDER_APPROVAL_LVL = "DP_ODR_APPROVAL_LVL";
    // for lookups entry of bulk commission payout
    public String BULK_COMM_STATUS = "DPSTA";
    public String FOC_TYPE = "DP";
    public String BONUS_TYPE = "BCTP";
    // added by vikram
    public String LOOKUP_CHANNEL_TRANSFER_TYPE_WITHDRAW = "Withdraw";

    // added by chetan for LDCC handling
    public String SUBSCRIBER_TYPE_POST = "POST";
    public String SERVICE_CLASS_LDCC = "LDCC";
    public String MONITOR_SERVER_USER_VIEW = "MONITOR_VW";
    public String MONITOR_SERVER_USER_ADMIN = "MONITOR_AD";
    public String USSD_UNDERPROCESS = "UNDERPROCESS";
    public String FALSE = "FALSE";

    // AFTER 5.5 BY VIKRAM
    public String THRESHOLD_COUNTER_TYPE = "THRTP";
    public String ABOVE_THRESHOLD_TYPE = "AT";
    public String BELOW_THRESHOLD_TYPE = "BT";

    // For Bulk voucher download
    public String SALE_BATCH_NUMBER = "SBM";

    public String P2P_CREDIT_RECHARGE = "CDPCR";

    public String IS_DEFAULT = "DEF";
    public String DEFAULT_YES = "YES";
    public String STK_SYSTEM_USR_CREATION_TYPE = "S";

    // added by nilesh : for user deletion
    public String SERVICE_TYPE_USER_DELETION = "USRDEL";
    // added by nilesh : for Auto c2c
    public String THRESHOLD_TYPE_MIN = "MIN";
    public String THRESHOLD_TYPE_ALERT = "ALERT";
    public String REQUEST_SOURCE_SYSTEM = "SYSTEM";

    // Chnages done by ashishT for MVD voucher download.
    public String MVD_VOUCHER_DOWNLOAD = "MVD";
    // added for Post Payment EL service type
    public String SERVICE_TYPE_CHNL_PAYMENTEL = "PPEL";
    // added by nilesh : for Auto c2c
    public String AUTO_C2C_TXN_MODE = "A";
    public String SERVICE_TYPE_CHNL_TRANS_ENQ = "RCE";// added by rahul for c2s
                                                      // transaction enquiry
    // added by priyanka 21/01/11 for mobilecom
    public String CRYSTAL_SUMMARY_REPORTS = "CRPTSUMM";
    // addded by jasmine
    public String SERVICESELECTOR_TYPE = "OPSRV";

    // ADDED FOR USER CREATION FROM USSD
    public String ADD_CHNL_USER = "ADDCHUSR";
    public String REQUEST_SOURCE_TYPE_USSD = "USSD";
    public String STK_USER_CREATION_TYPE = "S";
    public String USSD_USER_CREATION_TYPE = "U";
    public String EXT_USER_CREATION_TYPE = "E";

    // added by anu for new o2c history report
    public String O2C_TRANSFER = "O2CTR";
    public String O2C_TRANSFER_CATEGORY = "OCHIS";

    public String ENQ_TXNID_DATE_EXTNO = "ETXNENQREQ";
    public int TXNID_COUNT = 100;
    public String ENQ_TXNID_NVL = "NULL";

    public String ENQ_TXN_ID = "TXENQREQ";
    // New keyword for credit transfer for multiple user's dedicated account
    // public String MULT_CRE_TRA_DED_ACC="MCTDA";
    // private Recharge
    public String TXN_STATUS_SUCCESS_MESSAGE = "Success Transaction";
    /*
     * added By Babu Kunwar for providing Remarks for
     * Deleting/Suspending/Resuming/Barring/UnBarring Channel User
     * Added on 16th Feb 2011 fro Pretups 5.5.3
     */
    public String USER_EVENT_REPORTS = "ERPT";
    public String DELETE_REQUEST_EVENT = "DELETE_REQ";
    public String SUSPEND_REQUEST_EVENT = "SUSPND_REQ";
    public String DELETE_EVENT_APPROVAL = "DEL_APRV";
    public String SUSPEND_EVENT_APPROVAL = "SUSPND";
    public String RESUME_EVENT_REMARKS = "RESUME";
    public String BARRING_USER_REMARKS = "BAR";
    public String UNBARRING_USER_REMARKS = "UNBAR";
    public String CHANGE_PIN = "CHNG_PIN";
    public String CHUSER_WITHDRAW = "WITHDRAWAL";
    public String CHUSER_RETURN = "RETURN";
    public String PASSWD_RESEND = "SEND_PSWD";
    public String PASSWD_RESET = "RESET_PSWD";
    public String PIN_RESET = "RESET_PIN";
    public String PIN_RESEND = "PIN_RESEND";
    public String VOUCHER_PIN_RESEND = "VOUCHER_PIN_RESEND";

    // Added for Private recharge.
    public String SID_AUTO_FILTER = "AUTO";
    public String SID_MANUAL_FILTER = "MANUAL";

    public String RECEIVER_MOBILE_NO = "MSISDN";
    public String RECEIVER_SID = "SID";
    public String SERVICE_TYPE_SID_ENQUIRY = "ENQSID";
    public int MESSAGE_LENGTH_SID_ENQUIRY = 2;
    public int MESSAGE_LENGTH_SID_ENQUIRYSMS = 1;
    public String SERVICE_TYPE_SID_DELETE = "DELSID";
    public int MESSAGE_LENGTH_SID_DELETE = 3;
    public int MESSAGE_LENGTH_SID_DELETESMS = 2;
    public boolean IS_SID_NUMERIC = true;
    public int SID_DELETION_MSG_LENGTH = 4;
    // public String TXN_STATUS_SUCCESS_MESSAGE="Success Transaction";
    public String TXN_STATUS_FAILURE_MESSAGE = "Failed Transaction";
    // For CRBT Registration and CRBT Song Selection
    public String SERVICE_TYPE_CRBTREGISTRATION = "CRBTRG";
    public String SERVICE_TYPE_CRBT_SONGSEL = "CRBTSGSEL";
    // Added for Corporate IAT Recharge
    public String RESTRICTED_TYPE = "IAT";
    public String DEFAULT_RESTRICTED_TYPE = "NOTIAT";
    public String APPROVE_IAT_LIST = "APPROVE";
    public String REJECT_IAT_LIST = "REJECT";
    public String DISCARD_IAT_LIST = "DELETE";
    // New keyword for credit transfer for multiple user's dedicated account
    public String MULT_CRE_TRA_DED_ACC = "PRCMDA";
    public String SUBSCRIBER_TYPE_LIST = "SCTYP"; // LOOKUPS
    public String SERVICE_SELECTOR_ID = "SER_SELID"; // IDS
    public String SERVICE_TYPE_SELECTOR_MAPPING_TYPE = "SSER";

    // Added for Alert_Type
    public String ALERT_TYPE_SELF = "S";
    public String ALERT_TYPE_PARENT = "P";
    public String ALERT_TYPE_OTHER = "O";

    // Added for EVR
    public String SERVICE_TYPE_CHNL_EVR = "EVR";

    // added by nilesh:for O2C and C2C transfer rule
    public String CHNL_TRANSFER_RULE_STATUS_NEW = "W";
    public String CHNL_TRANSFER_RULE_STATUS_SUSPEND_REQUEST = "P";
    public String CHNL_TRANSFER_RULE_STATUS_RESUME_REQUEST = "Q";
    public String TRANS_TYPE = "TFTYP";
    public String CHNL_TRANSFER_RULE_STATUS_MODIFY_REQUEST = "R";
    public String CHNL_TRANSFER_TYPE_TRF_WITD = "Transfer/Withdraw";
    public String CHNL_TRANSFER_TYPE_RET = "Return";
    public String C2C_RULE_TYPE = "RUTYP";
    public String ACTIVE = "ACTIVE";
    public String NEW = "NEW";
    public String SUSPENDED = "SUSPENDED";
    public String SUSPEND_REQUEST = "SUSPENDREQUEST";
    public String RESUME_REQUEST = "RESUMEREQUEST";
    public String MODIFY_REQUEST = "MODIFYREQUEST";

    public String O2C_BATCH_TRANSACTION_ID = "OB";
    public String O2C_BATCH_PROCESS_ID = "O2CBATCH";
    public String CT_BATCH_O2C_STATUS_UNDERPROCESS = "UNDPROCESS";
    public String SERVICE_TYPE_USR_SUSPEND_RESUME = "SUSRESUSR";

    // Added for Bulk Resume feature in Mobinil
    public String USER_STATUS_RESUMED = "Y";
    public String USER_STATUS_RESUME_REQUEST = "RR";

    // Added for C2C/C2S transaction specific dial
    public String TRANSACTION_TYPE_C2C = "C2C";
    public String TRANSACTION_TYPE_C2S = "C2S";

    public String CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED = "P";
    public String CT_BATCH_O2C_STATUS_OPEN = "OPEN";
    public String O2C_ORDER_APPROVAL_LVL = "O2C_ODR_APPROVAL_LVL";

    public String CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN = "OPEN";
    public String CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE = "CLOSE";
    public String CHANNEL_TRANSFER_BATCH_O2C_STATUS_CANCEL = "CANCEL";
    public int CACHE_MESSAGE_MANAGEMENT = 38;
    public int CACHE_NETWORK_PRODUCT = 39;
    public int CACHE_CARD_GROUP = 40;

    public String O2C_WALLET_TYPE = "O2C";
    // added by nilesh
    public String DELETE_TRANSFER_RULE_REQ_STATUS = "D";
    public String DELETE_TRANSFER_RULE_APPRV = "Delete Request";

    public String SERVICE_TYPE_POSTPAID_BILL_DEPOSIT = "PPD";
    public String TRANSFER_CATEGORY_WITHDRAW = "TRWD";

    // added for Channel User Dr/Cr Service API
    public String TRANSACTION_MODE_DRCR_TRANSFER = "C";
    public String SERVICE_TYPE_CHNL_DRCR_TRANSFER = "DRCRTRF";
    public String DRCR_CHANNEL_USER_ID = "CM";
    public String AUTO_FOC_ALLOW = "Y";
    public String AUTO_FOC_TXN_MODE = "A";
    public String AUTO_FOC_WALLET = "FOC";
    // for DB2
    public String DATABASE_TYPE_DB2 = "DB2";
    public String P2P_MCD_LIST_SERVICE_TYPE = "MCDL";
    public String P2P_MCD_LIST_DELETE = "MCDLD";
    public String P2P_MCD_LIST_ACTION_ADD = "A";
    public String P2P_MCD_LIST_ACTION_MODIFY = "M";
    public String P2P_MCD_LIST_ACTION_DELETE = "D";
    public String P2P_MCD_LIST_VIEW = "MCDLV";
    public String P2P_MCD_LIST_REQUEST = "MCDLR";

    public String XLS_PINPASSWARD = "****";

    // Give Me Balance
    public String SERVICE_TYPE_GIVE_ME_BALANCE = "CGMBALREQ";
    public String FLARES_CONTENT_TYPE = "application/x-www-form-urlencoded";

    // LendMeBalance
    public String SERVICE_TYPE_LEND_ME_BALANCE = "LMB";

    // added by Puneet for user creation through EXT system
    public String SERVICE_TYPE_CHNN_USER_REGISTRATION = "USRREGREQ";
    public String SERVICE_TYPE_CHNN_USER_MODIFICATION = "USRMODREQ";
    public String SERVICE_TYPE_CHNL_USER_DELETE_REQ = "USRDELREQ";
    public String SERVICE_TYPE_CHNL_USER_DELETE_RES_SUS = "USRACTREQ";
    public String EXTERNAL_SYSTEM_USR_CREATION_TYPE = "E";
    public String PARENT_USER_ID = "ROOT";
    public String SUCCESSIVE_TXN_INTERVAL = "S_TXN_INTRVAL";
    public String LOW_BALANCE_ALERTING = "LOW_BAL_ALERT";
    public String COUNT_OR_VALUE_ALERT = "MAXCOUNT";
    // Addition Ends

    // added for roam commission
    public String ROAM_COMM_VALUE = "100";

    public String AON_TAG = "AON";
    // for LMB reports
    public String TRNX_TYPE = "TXTYP";
    public String LANGUAGE_CODE = "LANGC";
    public String LMB_VAL_NT_EXPIRED = "N";
    public String LMB_VAL_EXPIRED = "Y";

    // ADDED BY HITESH
    // for user profile threshold enquiry
    public String USER_PRODUCT_LIST = "USER_PRODUCT_LIST";
    public String TRANSFER_PROFILE_VO = "TRANSFER_PROFILE_VO";
    public String USER_TRF_COUNT_VO = "USER_TRF_COUNT_VO";
    public String SUB_OUT_COUNT_FLAG = "SUB_OUT_COUNT_FLAG";
    // for bulk voucher
    // CHANGE FINISHED
    // batch add/modify commission profile by gaurav pandey
    public String COMM_PROFILE_STATUS = "N";
    public String BATCH_MODIFY_COMM_PROFILE_DOMAIN = "DOMAIN_NAME";
    public String BATCH_MODIFY_COMM_PROFILE_CATEGORY = "CATEGORY_NAME";
    public String BATCH_MODIFY_COMM_PROFILE_PRODUCT_LIST = "PRODUCT_LIST";
    public String BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE = "DOMAIN_CODE";
    public String BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE = "CATEGORY_CODE";
    public String BATCH_MODIFY_COMM_PROFILE_SET_NAME = "SET_NAME";
    public String BATCH_MODIFY_COMM_PROFILE_SET_VERSION = "SET_VERSION";
    public String BATCH_MODIFY_COMM_PROFILE_NETWORK_NAME = "NETWORK_NAME";
    public String BATCH_MODIFY_COMM_PROFILE_ADDITIONAL_COMMISSION = "ADDITIONAL_COMMISSION";
    public String BATCH_MODIFY_COMM_PROFILE_PROCESS_ID = "BATCHMODCOMMPRF";
    public String BATCH_COMM_CREATED_BY = "DOWNLOADED_BY";
    public String BATCH_COMM_CATEGORY_LIST = "CATEGORY_LIST";
    public String BATCH_COMM_DOMAIN_LIST = "DOMAIN_LIST";
    public String BATCH_COMM_SERVICE_LIST = "SERVICE_LIST";
    public String BATCH_COMM_VERSION = "1";
    public String DOWNLOADED_BY = "DOWNLOADED";
    public String BATCH_COMM_PROFILE_PREFIX = "BCOM";
    public String COMM_PROFILE_BATCH_ID = "BATCH_ID";
    public String BATCH_COMM_PROFILE_TYPE = "BATCH_COMM_PROF_ADD";
    public String BATCH_COMM_PROFILE_STATUS = "C";

    public String CHANNEL_TRANSFER_O2C_VOMS_ID = "OV";
    public String VOUCHER_PRODUCT_O2C = "VOUCHTRACK";

    public int CACHE_MESSAGE_GATEWAY_CATEGORY = 41;
    public int CACHE_SERVICE_CLASS_CODE = 42;

    public int TRANSFER_PROFILE = 43;
    public int TRANSFER_PROFILE_PRODUCT = 44;

    // added for Multiple Credit List CR
    /*
     * public String P2P_MCD_LIST_SERVICE_TYPE="MCDL";
     * public String P2P_MCD_LIST_DELETE="MCDLD";
     * public String P2P_MCD_LIST_ACTION_ADD="A";
     * public String P2P_MCD_LIST_ACTION_MODIFY="M";
     * public String P2P_MCD_LIST_ACTION_DELETE="D";
     * public String P2P_MCD_LIST_VIEW="MCDLV";
     * public String P2P_MCD_LIST_REQUEST="MCDLR";
     */

    // added by gaurav for cos management and cellid management

    public String CELL_GROUP_LIST = "CELLGRPLST";
    public String CELL_GROUP_ASSOCIATION_PROCESS_ID = "CELLGRPPRO";
    public String CELL_ID_MAPPING_ADD = "ADD";
    public String CELL_ID_MAPPING_MODIFY = "MODIFY";
    public String CELL_ID_MAPPING_DELETE = "DELETE";
    public String CELL_ID_MAPPING_MODSUSPEND = "MODSTATUS";
    public String CELL_ID_MODIFY_STATUS = "STATUSMODIFY";
    public String CELL_ID_REASSOCIATE_CELLGRPID = "REASSOGRPID";
    public String CELL_ID_STATUS_ACTIVE = "ACTIVE";
    public String CELL_ID_STATUS_SUSPEND = "SUSPEND";
    public String CELL_ID_STATUS_A = "A";
    public String CELL_ID_STATUS_S = "S";
    public String CELL_ID_STATUS_D = "D";
    public String CELL_ID_STATUS_R = "R";

    public String CELL_GROUP_TYPE_ID = "CLGRP";
    public String CELL_ACTIVE_STATUS = "ACTIVE";
    public String CELL_SUSPEND_STATUS = "SUSPEND";

    // COS Management
    public String COS_STATUS_ACTIVE = "A";
    public String COS_STATUS_SUSPEND = "S";
    public String COS_STATUS_DELETE = "D";
    public String CELL_ID_VO_LIST = "CELLVODETAILSLST";
    public String CELL_ID_REASSOCIATION_PROCESS_ID = "CIDREASSON";
    public String COS_DEFINE_FILE_PROCESS = "COSDEFINE";
    public String COS_MANAGE_FILE_PROCESS = "COSMANAGE";

    // added for lmb debit api
    public String SERVICE_TYPE_LMBDEBIT = "LMBDBT";
    public int LMB_DBT_MESSAGE_LENGTH = 4;

    // added by deepika aggarwal
    public String BATCH_USR_LANGUAGE_LIST = "USER_LANG_LIST";
    public String C2S_LASTX_TRANSFER_REPORT = "C2SLASXTRF";
    // Added for CP Registration
    public String OPT_MODULE = "OPT";
    public String OPERATOR_SUBCHNL_ADMIN = "TSM";

    // VASTRIX CHANGES start...
    public String C2S_REC_GEN_FAIL_MSG_REQD_V_VAS = "C2S_REC_GEN_FAIL_MSG_REQD_V_VAS";
    public String C2S_REC_GEN_FAIL_MSG_REQD_T_VAS = "C2S_REC_GEN_FAIL_MSG_REQD_T_VAS";
    // Gift VAS
    public String C2S_REC_GEN_FAIL_MSG_REQD_V_GIFTVAS = "C2S_REC_GEN_FAIL_MSG_REQD_V_GIFTVAS";
    public String C2S_REC_GEN_FAIL_MSG_REQD_T_GIFTVAS = "C2S_REC_GEN_FAIL_MSG_REQD_T_GIFTVAS";
    public String VAS_REQUEST_TYPE_ADD = "add";
    public String VAS_REQUEST_TYPE_MODIFY = "modify";
    public String VAS_REQUEST_TYPE_DELETE = "delete";
    public String VAS_REQUEST_TYPE_APPROVE = "approve";
    public String VAS_REQUEST_TYPE_VIEW = "view";
    public String VAS_CATEGORY_STATUS_ACTIVE = "Y";
    public String VAS_ITEM_STATUS_ACTIVE = "Y";
    public String VAS_ITEM_STATUS_NEW = "W";
    public String VAS_ITEM_STATUS_DELETE = "N";
    public String VAS_STATUS_ACTIVE = "Y";
    public String VAS_TYPE_CATEGORY = "CAT";
    public String VAS_TYPE_ITEM = "ITEM";
    public String VAS_SINGLE_ITEM_INDEX = "S";
    public String VAS_BATCH_ITEMS_INDEX = "B";
    public String VAS_ALL_ITEMS_INDEX = "ALL";
    public String VAS_NETWORK_ALL = "ALL";
    public String BATCH_VAS_MODIFY_PROCESS_ID = "VAS_MODIFY";
    public String BATCH_VAS_DELETE_PROCESS_ID = "VAS_DELETE";
    public String BATCH_VAS_ADD_PROCESS_ID = "VAS_ADD";

    public String VAS_ALL_ITEM_INDEX = "I";
    public String VAS_VIEW_ACTION_SINGLE = "SINGLE";
    public String VAS_VIEW_ACTION_ALL = "ALL";
    public String VAS_VIEW_ACTION_BULK = "BULK";
    public String VAS_BOTH_ITEM_STATUS_INDEX = "A";
    public String VAS_ACTIVE_ITEM_STATUS_INDEX = "Y";
    public String VAS_NEW_ITEM_STATUS_INDEX = "W";
    public String VAS_ROOT_PARENT_ID = "ROOT";
    public String VAS_C2S_MODULE = "C2S";
    public int CACHE_NETWORK_VAS_MAPPING = 33;
    public String INTERFACE_CATEGORY_VAS_PT = "VAS-PT";
    public String INTERFACE_CATEGORY_VAS_HT = "VAS-HT";
    public String SERVICE_ACTIVE_STATUS = "ACTIVE";
    public String SERVICE_SUSPEND_STATUS = "SUSPEND";
    public String SERVICE_ID_MAPPING_ADD = "ADD";
    public String SERVICE_ID_MAPPING_MODIFY = "MODIFY";
    public String SERVICE_ID_MAPPING_DELETE = "DELETE";
    public String SERVICE_GROUP_TYPE_ID = "SVGRP";
    public String SERVICE_GROUP_ASSOCIATION_PROCESS_ID = "SERVGRPPRO";
    public String SERVICE_GROUP_LIST = "SERVGRPLST";
    public String SERVICE_ID_VO_LIST = "SERVODETAILSLST";
    public String SERVICE_ID_REASSOCIATION_PROCESS_ID = "SIDREASSON";
    public String SERVICE_ID_STATUS_SUSPEND = "SUSPEND";
    public String SERVICE_ID_STATUS_A = "A";
    public String SERVICE_ID_STATUS_S = "S";
    public String SERVICE_ID_STATUS_D = "D";
    public String SERVICE_ID_STATUS_R = "R";
    public String SERVICE_ID_STATUS_ACTIVE = "ACTIVE";
    public String SERVICE_ID_MODIFY_STATUS = "STATUSMODIFY";
    public String SERVICE_ID_REASSOCIATE_SERVICEGRPID = "REASSOGRPID";
    public String SERVICE_INTERFACE_MAPPING_ID_TYPE = "SIM";// arvinder
    public String VAS_CATEGORY_TYPE = "CAT";
    public String VAS_ITEM_TYPE = "ITEM";
    public String VAS_TYPE_VCC = "VCC";
    public String VAS_TYPE_HT = "HT";
    public String VAS_TYPE_PT = "PT";
    public String ID_GEN_VAS_TRANSFER_NO = "VAS";
    public String INTERFACE_CATEGORY_VAS = "VAS";
    public String VAS_VSTP = "VSTP";
    public String VAS_VSSTP = "VSSTP";
    public String VAS_VSSSTP = "VSSSTP";
    public String VAS_CATEGORY_PREFIX_ID = "VC";
    public String VAS_ITEM_PREFIX_ID = "ITM";
    public String SERVICE_TYPE_VAS_RECHARGE = "VAS";
    public String SERVICE_TYPE_PVAS_RECHARGE = "PVAS";
    public int CACHE_USER_DEFAULT = 36;
    public int CACHE_USER_SERVICES = 37;
    public int CACHE_SERVICE_INTERFACE_MAPPING = 38;

    // /// VASTRIX CHANGES end...

    // Entries for DMS
    // Added by Ankur for update cache for DMS user API
    public String EXTERNAL_USR_CREATION_TYPE = "E";
    public String SERVICE_TYPE_EXT_GEOGRAPHY = "EXTGRPH";
    public String GEOGRAPHY_LIST = "GEOLIST";
    public String SUB_AREA_TYPE = "SA";
    public String PARENT_GEOGRAPHY_ROOT = "ROOT";
    public String SERVICE_TYPE_EXT_USR_ADD = "EXTUSRADD";
    public String USER_MAP = "USERMAP";
    public String SUB_OUTLET_DEFAULT = "SL019";
    public String UNAME_PREFIX_DEFAULT = "CMPY";
    public String SERVICE_TYPE_TRF_RULE_TYPE = "TRFRULETYP";
    public String RULETYPEDETAILS_STR = "RULETYPEDETAILS";
    // Added for User default Config management
    public String USER_DEFAULT_CONFIG_PROCESS_ID = "USRDEFCONFPROID";
    public String USR_DEF_CONFIG_CREATED_BY = "CREATED_BY";
    public String USR_DEF_CONFIG_DOMAIN_NAME = "DOMAIN_NAME";
    public String USR_DEF_CONFIG_DOMAIN_CODE = "DOMAIN_CODE";
    public String USR_DEF_CONFIG_CATEGORY_LIST = "CATEGORY_LIST";
    public String USR_DEF_CONFIG_GROUP_ROLE_LIST = "GROUP_ROLE_LIST";
    public String USR_DEF_CONFIG_GRADE_LIST = "GRADE_LIST";
    public String USR_DEF_CONFIG_TRANSFER_CONTROL_PRF_LIST = "TRANSFER_CONTROL_PRF_LIST";
    public String USR_DEF_CONFIG_COMMISION_PRF_LIST = "COMMISION_PRF_LIST";
    public String USR_DEF_CONFIG_EXCEL_DATA = "USR_DEF_CONFIG_EXCEL_DATA";
    public String USR_DEF_CONFIG_ADD = "A";
    public String USR_DEF_CONFIG_MODIFY = "M";
    public String USR_DEF_CONFIG_DELETE = "D";
    public String USR_DEF_CONFIG_NOCHANGE = "N";
    public String USR_CACHE_GEOCODE_SUFFIX = "_GRDCODE";
    public String USR_CACHE_TRFPRF_SUFFIX = "_TRFPRF";
    public String USR_CACHE_COMPRF_SUFFIX = "_COMPRF";
    public String USR_CACHE_ROLECODE_SUFFIX = "_ROLECODE";
    public String USR_CACHE_GRDCODE_SUFFIX = "_GRDCODE";
    public String TRANSFER_RULE_AT_USER_LEVEL = "TRFRU";// added on 23/oct/2012
    public String SERVICE_TYPE_USER_AUTH = "USRAUTH";// on 29/nov/2012
    // / end of DMS Entries

    // Added for trf by shashank
    public String BATCH_USR_TRF_RULE_LIST = "TRANSFER_RULE_LIST";

    // / added for promotional transfer rule
    public String PROMOTIONAL_LEVEL_CELLGROUP = "CEL";
    // For Service Group
    public String PROMOTIONAL_LEVEL_SERVICEGROUP = "SRV";
    public String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CEL = "CEL";
    public String TRANSFER_RULE_SUBSCRIBER_STATUS = "SUBSTATUS";
    public String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE = "SRV";

    public String BATCH_COMM_SUBSERVICE_LIST = "BATCH_COMM_SUBSERVICE_LIST";

    // added by shashank for channel user authentication 17/JAN/2013
    public String USER_AUTH_XML_REQ = "AUTHCUSER";
    public String USER_AUTH_XML_RESP = "AUTHCUSERRESP";

    public String DEFAULT_SUBSERVICE = "DEF";
    public String LOOKUP_TYPE_SUBSCRIBER_STATUS = "SUBST";

    public int PROMO_TRF_RULE_LVL_CELLGRP_CODE = 6;
    public int PROMO_TRF_RULE_LVL_SPNAME_CODE = 7;
    public String SERVICE_TYPE_SIM_ACT_REQ = "SIMACTREQ";
    public String SIM_ACTIVATE_RESP = "SIMACTRES";
    public String PROMO_BALANCE_PREFIX = "PROMO";
    // added by Sonali Garg
    // to validate subscriber at IN
    // public String EXTERNAL_SYSTEM_SUBSCRIBER_ENQUIRY="EXTSYSENQ";
    public String SERVICE_TYPE_SUBSCRIBER_ENQUIRY = "SUBENQ";

    // Batcg Grade association
    public String BATCH_USER_GRADE_LIST = "SERVGRPLST";
    public String BATCH_USER_GRADE_VO_LIST = "SERVODETAILSLST";
    public String BATCH_USER_GRADE_ASSOCIATION_USERGRDASSCID = "USRGDASCID";
    // public String TRANSFER_RULE_SUBSCRIBER_STATUS = "SUBSTATUS";
    public String ERROR_LINE = "-line";

    // for auto o2c
    public String AUTO_O2C_ALLOW = "Y";
    public String PRODUCT_TYPE_AUTO_O2C = "PREPROD";
    public String AUTO_O2C_PROCESS = "AUTOO2CPROCESS";

    public String AUTO_O2C_ORDER_NEW = "NEW";

    public String AUTO_O2C_ORDER_APPROVE1 = "AP1";
    public String AUTO_O2C_ORDER_APPROVE2 = "AP2";
    public String AUTO_O2C_ORDER_APPROVE3 = "AP3";
    public String AUTO_O2C_ORDER_REJECTED = "CNCL";

    // added by shashank for barred for deletion 12/MAR/2013
    public String USER_STATUS_BAR_FOR_DEL_REQUEST = "BR";
    public String USER_STATUS_BARRED = "BD";
    public String USER_STATUS_BAR_FOR_DEL_APPROVE = "BA";
    public String BARRED_REQUEST_EVENT = "BAR_REQ";

    // added by shashank for batch bar for deletion 17/APRIL/2013
    public String USER_STATUS_BCH_BAR_FOR_DEL_REQUEST = "OPEN";
    public String USER_STATUS_BCH_BAR_FOR_DEL_APPROVE1 = "APPR1";
    public String USER_STATUS_BCH_BAR_FOR_DEL_REJECT = "REJCT";
    public String USER_STATUS_BCH_BAR_FOR_DEL_BARRED = "CLOSE";
    // public String BATCH_BARRED_REQUEST_EVENT="BCH_BAR_REQ";

    public String USR_BATCH_BAR_STATUS_OPEN = "O";
    public String USR_BATCH_BAR_STATUS_CLOSE = "C";
    public String USR_BATCH_BAR_STATUS_REJECT = "R";
    public String USR_BATCH_BAR_STATUS_APPROVE1 = "A";
    public String USR_BATCH_BAR_STATUS_UNDERPROCESS = "U";

    public String BULK_USR_BAR_ID_PREFIX = "BD";
    public String BULK_USR_BAR_DETAIL_ID_PREFIX = "BBD";
    public String BATCH_BAR_FOR_DEL_TYPE = "BATCH_BAR_FOR_DEL";
    public String BATCH_USR_BAR_BATCH_ID = "BATCH_BAR_ID";

    public String DEFAULT_GATEWAY_FOR_BAR = "WEB";
    public String LMS_SERVICE = "LMSER";
    public String LMS_PROMOTION_TYPE = "LMPTY";
    public String LMS_PROMO_ID = "LMSPR";
    public String LMRWD = "LMRWD";

    public String LMS_REW_ID = "LMS_REW_ID";
    public String PAYER = "PAYER";
    public String PAYER_HIERARCHY = "PAYER_HIERARCHY";
    public String PAYER_REGISTERER = "PAYER_REGISTERER";
    public String PAYEE = "PAYEE";
    public String PAYEE_HIERARCHY = "PAYEE_HIERARCHY";
    public String PAYEE_REGISTERER = "PAYEE_REGISTERER";
    public String LMAPP = "LMAPP";
    public String RWD_RANGE = "RWD_RANGE";
    public String PR_ASSC = "PR_ASSC";
    public String VIEW_STATUS_TYP = "PRVST";
    // For Redemption
    public String LMS_REDEMP_TYPE = "LPRED";
    public String REDEMP_TYPE_OTHER = "OTHER";
    public String REDEMP_TYPE_STOCK = "STOCK";
    public String ID_GEN_LMS_TRANSFER_NO = "LMS";
    public String SUCCESS = "SUCCESS";
    public String FAIL = "FAIL";
    public String LMSFOCO2C = "LMSFOCO2C";
    public String O2C_MODULE = "O2C";

    // added by harsh for Scheduled Credit List (Add/Modify/Delete) API
    public String P2P_SMCD_LIST_SERVICE_TYPE = "SMCDL";
    // added by harsh for P2P Batches
    public String P2P_BUDDYLIST_BATCH_ID = "P2PB";
    // added by Vikas Kumar for scheduled credit transfer service
    public String SERVICE_TYPE_SCH_CREDIT_TRANSFER = "SCPRC";

    // added by Sonali Garg for scheduled multiple credit CR(for the process)
    public String PROCESS_NAME_SCHEDULED_MULTIPLE_CREDIT_TRANSFER = "P2PSCMULCRDTTRF";
    public String SCHEDULE_TYPE_WEEKLY_FILTER = "WK";
    public String SCHEDULE_TYPE_MONTHLY_FILTER = "MO";
    public String SCHEDULE_TYPE_DAILY_FILTER = "DL";

    // added by pradyumn for Scheduled Credit List (View/Delete whole List) API
    public String P2P_SMCD_LIST_VIEW = "PSCTVREQ";
    public String P2P_SMCD_LIST_DLT = "PSCTDREQ";

    public String OAM_SERVICE_TYPE = "ST";
    public String OAM_COUNTER_TYPE = "CT";
    public String OAM_NETWORK_TYPE = "NT";
    // For LMS- 1/3/2014
    public String LMS_PROFILE_TYPE = "LMS";
    public String LMS_PROMOTION_TYPE_LOYALTYPOINT = "LOYALTYPT";
    public String LMS_PROMOTION_TYPE_STOCK = "STOCK";
    public String LMS_ASS_TYP = "LASTY";
    public String LMS = "LMS";
    public String SINGLE_USER = "SGL";
    public String BULK_USER = "BLK";
    public String LMS_O2C_SERVICE_LIST = "O2CSL";
    public String LMS_C2C_SERVICE_LIST = "C2CSL";
    public String LMS_C2S_SERVICE_LIST = "C2SSL";
    public String SERVICE_TYPE_HLPDSK_REQUEST = "HLPDSK";
    public String LMS_MSG_SUBSTRING1 = "<Promotion_Name>";
    public String LMS_MSG_SUBSTRING2 = "<Start_Date>";
    public String LMS_MSG_SUBSTRING3 = "<End_Date>";

    // For Batch User Addtional Fields in batch module
    public String BATCH_USRADDET_PROCESS_ID = "BULKUSERADDET";
    public String BATCH_USER_ADET_PROCESS_STATUS = "UNDERPROCESS";
    public String BATCH_USRADDET_PREFIX_ID = "BUADET";
    public String BATCH_USRADDET_ID = "BTUSR_ADET";
    public String BULK_USRADDET_BATCH_TYPE = "BULK_USR_ADDNLDETAIL";
    public String O2C_APPROVED = "Auto O2C";
    public String O2C_APPROVED_BY = "Approved By Auto O2C";
    public String O2C_SAP_ENQUIRY = "O2CEXTENQ";
    public String SAP_MODULE = "SAP";
    // Added for authentication type
    public String AUTHENTICATION_TYPE = "AUTHT";
    public String AUTH_TYPE_OTP = "OTP";
    public String AUTH_TYPE_LDAP = "LDAP";
    public String CUINFO = "CUINFO";
    public String PRODUCT_ETOPUP = "ETOPUP";
    public String INTERFACE_CATEGORY_DTH = "DTH";
    public String INTERFACE_CATEGORY_PIN = "PIN";
    public String INTERFACE_CATEGORY_DATACARD = "DC";
    public String INTERFACE_CATEGORY_PMD = "PMD";
    public String INTERFACE_CATEGORY_FLRC = "FLRC";
    public String INTERFACE_CATEGORY_BPB = "RPB";

    public String AUTO_C2C_SOS_CAT_ALLOWED = "AUTO_C2C_SOS_CAT_ALLOWED";
    public String AUTO_C2C_TRUE = "true";
    public String AUTO_C2C_PROCESS_ID = "AUTOC2C";
    public String COLLECTION_ENQUIRY = "CE";
    public String COLLECTION_BILLPAYMENT = "CBP";
    public String COLLECTION_CANCELATION = "CCN";
    public String TRANSFER_TYPE_DIFFDR = "DIFFD";
    // Added by Aatif
    public String BATCH_USR_LMS_PROFILE = "BATCH_USR_LMS_PROFILE";
    public String TXNTYPE_T = "T";
    public String TXNTYPE_X = "X";

    public String LMS_REFERENCE_TYPE_ACTIVE = "Y";
    public String LMS_REFERENCE_TYPE_INACTIVE = "N";
    public String CHANNEL_TRANSFER_SUB_TYPE_LMS = "L";
    public String O2C_SAP_UPDATE = "O2CEXTCODEUPDREQ";
    // PPB ENQ
    public String SERVICE_TYPE_PPB_ENQUIRY = "PPBENQ";
    public String CATEGORISATION_TYPE = "CATMN";
    public String CATEGORISATION_CLASS_TYPE = "CACMN";
    public String CATEGORISATION_ID = "PR";

    // Added By Diwakar on 20-JAN-2014
    public String USER_ROLE_ADD = "A";
    public String USER_ROLE_DELETE = "D";

    public String SERVICE_TYPE_EXT_CHANGE_PASSWORD = "CPWD";
    public String SERVICE_TYPE_EXT_USER_ADD = "USERADD";
    public String SERVICE_TYPE_EXT_USER_MODIFY = "USERMOD";
    public String SERVICE_TYPE_EXT_USER_DELETE = "USERDEL";
    public String SERVICE_TYPE_EXT_USER_SUSPEND_RESUME = "USERSR";
    public String SERVICE_TYPE_EXT_USER_ROLE_ADD_MODIFY = "USEROLEAM";// 21-02-2014
    public String SERVICE_TYPE_EXT_ICCID_MSISDN_MAP = "ICCMSISDNM";
    public String SERVICE_TYPE_EXT_MNP_UPLOAD = "MNPUPLOAD";// 21-02-2014
    // ENded Here by Diwakar

    public String CHANNEL_TRANSFER_BATCH_O2C_STATUS_UNDERPROCESS = "UNDPROCESS";
    public String CUSTOMER_TXN_C2S_ENQ_USSD = "EN";

    public String REQUEST_SOURCE_TYPE_XMLGW = "XMLGW"; // 01-APR-2014

    public String TARGET_TYPE = "TRGYP";

    // added by VIkas Singh for CARD Modify,delete & view
    public String SERVICE_TYPE_SELFTOPUP_CARD_MODIFY = "CARDMOD";
    public String SERVICE_TYPE_SELFTOPUP_CARD_DELETE = "CARDDEL";
    public String SERVICE_TYPE_SELFTOPUP_RECHARGE_USING_REGISTERED_CARD = "ADHOCRCREG";
    public int MESSAGE_LENGTH_MODIFY_CARD = 5;
    public int MESSAGE_LENGTH_DELETE_CARD = 4;

    // sonali changes start SELF TOPUP
    public String SERVICE_TYPE_SELFTOPUP = "STU";
    public String CP2P_MODULE = "CP2P";
    public String SERVICE_TYPE_SELFTOPUP_ADHOCRECHARGE = "ADHOCRC";
    // sonali changes end

    // added by sonali for Self Top up
    public String SERVICE_TYPE_SELFTOPUP_USER_REGISTRATION = "STUREG";
    public String SERVICE_TYPE_SELFTOPUP_VIEW_CREDITCARDLIST = "VWCARD";
    public int MESSAGE_LENGTH_CREDIT_CARD_LIST = 4;
    public String GATEWAY_TYPE_SELFTOPUP = "STUGW";
    // default imei while registering from WEB. of length 15
    public String DEFAULT_P2P_WEB_IMEI = "000000000000000";
    // changes end
    public Integer MESSAGE_LENGTH_AUTO_TOPUP = 8;
    public String SELF_TOPUP_SCHEDULED_CREDIT_TRANSFER = "SLFSCCRDTTRF";
    public String REQUEST_SOURCE_TYPE_STUGW = "PLAIN";
    // added by Vikas Singh for Auto topup

    public String DEFAULT_PAYMENT_GATEWAY = "CITI";
    public Integer MESSAGE_LENGTH_DISABLE_AUTO_TOPUP = 3;

    final int NO_OF_DAYS_IN_WEEK = 7;
    final int NAME_ALLOWED_LENGTH = 10;
    final int IMEI_LENGTH = 15;
    final int CVV_LENGTH = 3;
    public String SELFTOPUP_SCHEDULE_TYPE_WEEKLY = "W";
    public String SELFTOPUP_SCHEDULE_TYPE_MONTHLY = "M";
    public String SELFTOPUP_SCHEDULE_TYPE_ONCE = "O";
    final int VALID_LENGTH_CARD_HOLDER_NAME = 20;
    public String STU_SER_TYPE_SUBS_REG_USING_MOB_API = "STUREG2";
    
    public static final String DATE_FORMAT = "dd/MM/yy";
    public static final String DATE_FORMAT_DDMMYYYY = "dd/MM/yyyy";
    public static final String TIMESTAMP_DATESPACEHHMMSS = "dd/MM/yy HH:mm:ss";
    public static final String TIMESTAMP_DATESPACEHHMM = "dd/MM/yy HH:mm";
    public static final String TIMESTAMP_DDMMYYYYHHMMSS = "dd/MM/yyyy HH:mm:ss";
}
