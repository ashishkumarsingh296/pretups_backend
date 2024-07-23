package com.btsl.pretups.common;

import java.util.regex.Pattern;

import com.btsl.common.TypesI;


public interface PretupsI extends TypesI {
	public static final String P2P_MODULE = "P2P";
	public static final String C2C_MODULE = "C2C";
	public static final String C2S_MODULE = "C2S";
	public static final String SINGLE = "SINGLE";
	public static final String MULTIPLE = "MULTIPLE";
	public static final String PRODUCT_CATEGORY_FIXED = "FIXED";
	public static final String PRODUCT_CATEGORY_FLEX = "FLEX";
	public static final String TEMP_TRANS_ID_START_WITH = "TX";
	public static final String REQUEST_SOURCE_TYPE_STK = "STK";
	public static final String REQUEST_SOURCE_TYPE_SMS = "SMS";
	public static final String REQUEST_SOURCE_TYPE_WEB = "WEB";
	public static final String REQUEST_SOURCE_TYPE_EXTGW = "EXTGW";
	public static final String VIEWNETWORKDETAIL = "VIEWNETWORKDETAIL";
	public static final String SHOWNETWORKDETAIL = "SHOWNETWORKDETAIL";
	public static final String VIEWSELFDETAIL = "VIEWSELFDETAILS";

	public static final String REQUEST_TYPE_ACCEPT = "ACCEPT";
	public static final String INTERFACE_CATEGORY_PRE = "PRE";
	public static final String INTERFACE_CATEGORY_POST = "POST";
	public static final String INTERFACE_CATEGORY_BOTH = "BOTH";

	public static final String MSG_GATEWAY_FLOW_TYPE_THREAD = "T";
	public static final String MSG_GATEWAY_FLOW_TYPE_COMMON = "C";
	public static final String MSG_GATEWAY_FLOW_TYPE_REQUEST = "R";
	public static final String MSG_GATEWAY_RESPONSE_TYPE_PUSH = "PUSH";
	public static final String MSG_GATEWAY_RESPONSE_TYPE_RESPONSE = "RESPONSE";

	public static final int TEMP_TRANS_ID_LENGTH = 11;
	public static final String SYSTEM_AMOUNT = "AMT";

	public static final String UDH_HEX = "027000";
	public static final String KEYWORD_TYPE_REGISTRATION = "REG";
	public static final String KEYWORD_TYPE_ADMIN = "ADM";

	public static final String ENCRYPTION_LEVEL_GLOBAL_CODE = "GLOBAL";
	public static final String ENCRYPTION_LEVEL_USER_CODE = "USER";
	public static final String ENCRYPTION_LEVEL_MASTER_KEY = "MASTER_KEY";
	public static final String LOCALE_LANGAUGE_EN = "en";
	public static final String NETWORK_PREFIX_ID = "NETPREFIX";
	public static final String SERVICE_KEYWORD_ID = "SVK";
	public static final String CHANNEL_TRANSFER_RULE_ID = "TRL"; 
	public static final String RES_INTERFACE_ID = "RESID";
	public static final String INTERFACE_TYPE_ID = "INTID"; 
	public static final String INTERFACE_SINGLE_STATE_TRANASACTION = "Y";
	public static final String ID_GEN_P2P_TRANSFER_NO = "TRANS";
	public static final String ID_GEN_C2S_TRANSFER_NO = "RECH";
	public static final String P2P_USER_ID = "P2P";
	public static final String STATUS_TYPE = "STAT";
	public static final String NETWORK_TYPE = "NTTYP";
	public static final String MODULE_TYPE = "MOTYP"; 
	public static final String SUBSRICBER_TYPE = "SUBTP";
	public static final String GATEWAY_STATUS_TYPE = "GSTAT"; 
	public static final String OPERATOR_TYPE_OPT = "OPT";
	public static final String OPERATOR_TYPE_CHNL = "CHNL";
	public static final String OPERATOR_TYPE_OTH = "OTH";
	public static final String SERIES_TYPE_POSTPAID = "POST";
	public static final String SERIES_TYPE_PREPAID = "PRE";
	public static final String SERIES_TYPE_BOTH = "BOTH";
	
	public static final String CARDGROUP_SET_ID="cardGroupSetID";
	public static final String VERSION="version";
	public static final String BUNDLE_ID="bundleID";
	public static final String BONUS_VALIDITY="bonusValidity";
	public static final String BONUS_VALUE="bonusValue";
	public static final String MULT_FACTOR="multFactor";
	public static final String BONUS_NAME="bonusName";
	public static final String BUNDLE_TYPE="bundleType";
	public static final String RESTRICTED_ON_IN="restrictedOnIN";
	public static final String BONUS_ACC_DETAIL_LIST="bonusAccDetailList";
	public static final String BOUNUS_CODE="bonusCode";
	public static final String CARDGROUP_ID="cardGroupID";
	public static final String START_RANGE="startrange";
	public static final String END_RANGE="endrange";
	
	
	
	public static final String REQ_INTERFACE_TYPE = "RQINT";
	public static final String INTERFACE_CATEGORY = "INTCT"; 
	public static final String INTERFACE_CATEGORY_PREPOST = "INCAT";
	public static final String SKEY_STATUS_EXPIRED = "E";
	public static final String SKEY_STATUS_CANCELLED = "C";
	public static final String MODIFY_ALLOWED_YES = "Y";
	public static final String MODIFY_ALLOWED_NO = "N";
	public static final String USER_STATUS_NEW = "W";
	public static final String USER_STATUS_APPROVED = "A";
	public static final String USER_STATUS_SUSPEND = "S";
	public static final String USER_STATUS_RESUME = "RE";
	public static final String USER_STATUS_CANCELED = "C";
	public static final String USER_STATUS_DELETED = "N";
	public static final String USER_STATUS_ACTIVE = "Y";
	public static final String USER_STATUS_DEREGISTERED = "D";
	public static final String USER_STATUS_BLOCK = "B";
	public static final String USER_STATUS_SUSPEND_REQUEST = "SR";
	public static final String USER_STATUS_DELETE_REQUEST = "DR";
	public static final String USER_APPROVE = "A";
	public static final String USER_REJECTED = "R";
	public static final String USER_DISCARD = "D";
	public static final String USER_STATUS_TYPE = "URTYP";
	public static final String USR_APPROVAL_LEVEL = "USRLEVELAPPROVAL";
	public static final String INTERFACE_VALIDATE_ACTION = "V";
	public static final String INTERFACE_CREDIT_ACTION = "C";
	public static final String INTERFACE_DEBIT_ACTION = "D";
	public static final String INTERFACE_UPDATE_VALIDITY_ACTION = "VA";
	public static final String TRANSACTION_SUCCESS_STATUS = "300";
	public static final String TRANSACTION_FAIL_STATUS = "350";
	public static final String P2P_STATUS_KEY_VALUS = "P2P_STATUS";
	public static final String TRANSFER_TYPE_TXN = "TXN";
	public static final String TRANSFER_TYPE_RCH_CREDIT = "RCH_CR";
	public static final String TRANSFER_TYPE_DIFFCR = "DIFFC";
	public static final String USER_TYPE_SENDER = "SENDER";
	public static final String USER_TYPE_RECEIVER = "RECEIVER";
	public static final int MESSAGE_LENGTH_ADD_BUDDY = 5;
	public static final int MESSAGE_LENGTH_DELETE_BUDDY = 3;
	public static final int MESSAGE_LENGTH_DELETE_BUDDY_LIST = 3;
	public static final int MESSAGE_LENGTH_CHANGE_PIN = 4;
	public static final int MESSAGE_LENGTH_SUSPEND = 2;
	public static final int MESSAGE_LENGTH_RESUME = 2;
	public static final int MESSAGE_LENGTH_BUDDYLIST = 2;
	public static final int MESSAGE_LENGTH_TRANSFERSTATUS = 2;
	public static final int MESSAGE_LENGTH_ACCOUNTSTATUS = 2;
	public static final int MESSAGE_LENGTH_DEREGISTER = 2;
	public static final String POS_VALUE = "POS";
	public static final String SLAVE_VALUE = "SLAVE";
	public static final int RECHARGE_TYPE_VALUE = 1;
	public static final int SERVICE_TYPE_VALUE = 2;
	public static final String BARRED_USER_TYPE_SENDER = "SENDER";
	public static final String BARRED_USER_TYPE_RECEIVER = "RECEIVER";
	public static final String SUB_LOOKUP_ID = "SL";
	public static final String TXN_STATUS_UNDER_PROCESS = "U";
	public static final String TXN_STATUS_COMPLETED = "C";
	public static final String TXN_STATUS_AMBIGUOUS = "A";
	public static final int TRANS_STAGE_BEFORE_INVAL = 0;
	public static final int TRANS_STAGE_AFTER_INVAL = 1;
	public static final int TRANS_STAGE_AFTER_FIND_CGROUP = 2;
	public static final int TRANS_STAGE_AFTER_INTOP = 3;
	public static final String BONUS_VALIDITY_TYPE_DAYS = "DAYS";
	public static final String REGISTERATION_REQUEST_PRE = "PRE";
	public static final String REGISTERATION_REQUEST_POST = "POST";
	public static final String BARRED_USER_TYPE = "BRTYP";
	public static final String BARRED_TYPE = "BTYP";
	public static final String BARRED_TYPE_ALL = "ALL";
	public static final String BARRED_TYPE_SELF = "SELF";
	public static final String BARRED_TYPE_CUSTOMERCARE = "CC";
	public static final String BARRED_TYPE_SYSTEM = "SYSTEM";
	public static final String BARRED_TYPE_PIN_INVALID = "BRPIN";
	public static final String BILLIGNG_CYCLE_MONTHLY = "MONTHLY";
	public static final String BILLIGNG_CYCLE_PERIODLY = "PERIODLY";
	public static final String VALPERIOD_HIGHEST_TYPE = "VLHI";
	public static final String VALPERIOD_CUMMULATIVE_TYPE = "VLCUM";
	public static final String VALPERIOD_LOWEST_TYPE = "VLLO";
	public static final String AUTH_TYPE_IP = "IP";
	public static final String AUTH_TYPE_LOGIN = "LOGIN";
	public static final String VALIDITY_TYPE = "VLTYP";
	public static final String AMOUNT_TYPE = "AMTYP";
	public static final String AMOUNT_COUNT_TYPE = "ACTYP";
	public static final String GATEWAY_TYPE = "GWTYP";
	public static final String GATEWAY_SUB_TYPE = "GWSTP";
	public static final String PROTOCOL = "PRCOL";
	public static final String CONTENT_TYPE = "CNTYP";
	public static final String AUTH_TYPE = "AUTYP";
	public static final String ENCRYPTION_LEVEL = "ENCRL";
	public static final String GATEWAY_STATUS_ACTIVE = "Y";
	public static final String GATEWAY_STATUS_SUSPEND = "S";
	public static final String GATEWAY_STATUS_DELETE = "N";
	public static final String GATEWAY_HANDLER_CLASS = "MESS_GAT_PARSER";
	public static final String TRANSFER_RULE_STATUS_ACTIVE = "Y";
	public static final String TRANSFER_RULE_STATUS_SUSPEND = "S";
	public static final String TRANSFER_RULE_STATUS_DELETE = "N";
	public static final String SELECT_CHECKBOX = "Y";
	public static final String RESET_CHECKBOX = "N";
	public static final String GATEWAY_TYPE_SMSC = "SMSC";
	public static final String GATEWAY_TYPE_USSD = "USSD";
	public static final String GATEWAY_TYPE_USSDPLAIN = "USSDPLAIN";	public static final String GATEWAY_TYPE_WEB = "WEB";
	public static final String SERVICE_TYPE_REGISTERATION = "PREG";
	public static final String SERVICE_TYPE_DEREGISTERATION = "PDREG";
	public static final String SERVICE_TYPE_BARRED = "PBAR";
	public static final String SERVICE_TYPE_RESUMESERVICE = "PRES";
	public static final String SERVICE_TYPE_P2PRECHARGE = "PRC";
	public static final String SERVICE_TYPE_P2PCREDITRECHARGE = "PCR";
	public static final String SERVICE_TYPE_P2PCHANGEPIN = "CPN";
	public static final String SERVICE_TYPE_ACCOUNTINFO = "ACCINFO";
	public static final String SERVICE_TYPE_P2P_HISTORY = "PTR";
	public static final String SERVICE_TYPE_LANG_NOTIFICATION = "PCHLAN";
	public static final String SKEY_CANCEL_SUCCESS = "SUCCESS";
	public static final String NO_SKEY_TO_CANCEL_SUCCESS = "NOSKEYCNCL";
	public static final String INTERFACE_CATEGORY_PREPAID = "PRE";
	public static final String INTERFACE_CATEGORY_POSTPAID = "POST";
	public static final String SERVICE_CLASS_ID = "SERID";
	public static final String SERVICE_CLASS_STATUS_ACTIVE = "Y";
	public static final String SERVICE_CLASS_STATUS_DELETE = "N";
	public static final String SERVICE_CLASS_STATUS_SUSPEND = "S";
	public static final String SUBLOOKUP_STATUS_YES = "Y";
	public static final String SUBLOOKUP_STATUS_NO = "N";
	public static final String CACHE_ACTION_ADD = "ADD";
	public static final String CACHE_ACTION_MODIFY = "MODIFY";
	public static final String CACHE_ACTION_DELETE = "DELETE";
	public static final String CACHE_ACTION_SAME = "SAME";
	public static final String PRODUCT_USAGE = "USAGE";
	public static final String REG_SUBSCRIBER_STATUS = "SSTAT"; 
	public static final String SUBSCRIBER_TYPE = "STYPE";
	public static final String GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE = "Y";
	public static final String GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND = "S";
	public static final String GEOGRAPHICAL_DOMAIN_STATUS_DELETE = "N";
	public static final String CHNL_TRANSFER_RULE_STATUS_ACTIVE = "Y";
	public static final String CHNL_TRANSFER_RULE_STATUS_SUSPEND = "S";
	public static final String CHNL_TRANSFER_RULE_STATUS_DELETE = "N";
	public static final String DOMAIN_TYPE_DISPLAY_ALLOWED = "Y";
	public static final String DOMAIN_ROLE_TYPE = "ROTYP";
	public static final int CATEGORY_SEQUENCE_NUMBER = 1;
	public static final String DOMAIN_STATUS_ACTIVE = "Y";
	public static final String DOMAIN_DISPLAY_ALLOWED = "Y";
	public static final String DOMAIN_STATUS_DELETE = "N";
	public static final String CATEGORY_STATUS_ACTIVE = "Y";
	public static final String CATEGORY_DISPLAY_ALLOWED = "Y";
	public static final String CATEGORY_MODIFY_ALLOWED = "Y";
	public static final String CATEGORY_STATUS_DELETE = "N";
	public static final String GROUP_ROLE = "Y";
	public static final String SYSTEM_ROLE = "N";
	public static final String GRADE_STATUS_ACTIVE = "Y";
	public static final String GRADE_STATUS_DELETE = "N";
	public static final String DIVISION_STATE_ACTIVE = "Y";
	public static final String DIVISION_ID = "DIVID";
	public static final String DIVISION_STATUS_DELETE = "N";
	public static final String DEPARTMENT_STATUS_ACTIVE = "Y";
	public static final String PHONE_PROFILE_TYPE = "PFTYP";

	public static final String CACHE_ALL = "0";
	public static final String CACHE_NETWORK = "NETWORK";
	public static final String CACHE_LOOKUPS = "LOOKUP";
	public static final String CACHE_PreferenceCache = "PREFERENCE";
	public static final String CACHE_NetworkPrefixCache = "NWPREFIX";
	public static final String CACHE_ServiceKeywordCache = "SRVKEYWORD";
	public static final String CACHE_MSISDNPrefixInterfaceMappingCache = "MOBILENOPRFINTR";
	public static final String CACHE_NetworkInterfaceModuleCache = "NWINTRFCMOD";
	public static final String CACHE_ServicePaymentMappingCache = "SRVPAYMENTMAPP";
	public static final String CACHE_TransferRulesCache = "TRANSFERRULE";
	public static final String CACHE_MessageGatewayCache = "MSGGTW";
	public static final String CACHE_RequestInterfaceCache = "REQINTFC";
	public static final String CACHE_FileCache = "FILE";
	public static final String CACHE_SIM_PROFILE = "SIMPRF";
	public static final String CACHE_NETWORK_SERVICE_CACHE = "NWSERVICE";
	public static final String CACHE_NETWORK_PRODUCT_SERVICE_TYPE = "NWPRDSRVTYPE";
	public static final String CACHE_ROUTING_CONTROL = "ROUTINGCONTL";
	public static final String CACHE_REGISTRATION_CONTROL = "REGCONTRL";
	public static final String CACHE_CONSTANT_PROPS = "CONSTANTS";
	public static final String CACHE_LOGGER_CONFIG = "LOGGER";
	public static final String CACHE_MESSAGE = "MESSAGE";
	public static final String CACHE_MESSAGE_RESOURCES = "MESSAGERESOURCE";
	public static final String CACHE_SERVICE_ROUTING = "SRVINTFCROUTING";
	public static final String CACHE_GROUP_TYPE_PROFILE = "GROUPTYPEPRF";
	public static final String CACHE_INTERFACE_ROUTING_CONTROL = "INTRROUTINGCONTL";
	public static final String CACHE_PAYMENT_METHOD = "PAYMNTMETH";
	public static final String CACHE_SERVICE_SELECTOR_MAPPING = "SRVSLTRMAPP";
	public static final String CACHE_BONUS_BUNDLES = "BONUSBUNDLE";
	public static final String IAT_COUNTRY_MASTER_CACHE = "IATCONTRYMAST";
	public static final String IAT_NETWORK_CACHE = "IATNW";
	public static final String CACHE_USER_SERVICES = "USERSERVICE";
	public static final String CACHE_NETWORK_PRODUCT = "NWPRD";
	public static final String CACHE_CARD_GROUP = "CARDGROUP";
	public static final String CACHE_MESSAGE_GATEWAY_CATEGORY = "MESSAGEGTWCAT";
	public static final String CACHE_SERVICE_CLASS_CODE = "SRVCLASSINFO";
	public static final String TRANSFER_PROFILE = "TRFPRF";
	public static final String TRANSFER_PROFILE_PRODUCT = "TRFPRFPRD";
	public static final String CACHE_COMMISSION_PROFILE = "COMMPRF";
	public static final String CACHE_USER_ALLOWED_STATUS = "USERALLWDSTATUS";
	public static final String CACHE_USER_WALLET_MAPPING = "USERWALLET";
	public static final String CACHE_LMS_PROFILE = "LMSPRF";
	public static final String CACHE_CURRENCY = "CURRENCY";
	public static final String CACHE_CELL_ID = "CELLID";
	public static final String CACHE_USER_DEFAULT = "USERDEFAULTCONFIG";
	public static final String CACHE_SERVICE_INTERFACE_MAPPING = "SRVINTMAPP";
	public static final String CACHE_MESSAGE_MANAGEMENT = "38";
	public static final String CACHE_NETWORK_INTERFACE_MODULE = "29";
/*	public static final String CACHE_SystemPreferences = "6";
	public static final String CACHE_PREFIX_SERVICE_MAPPING = "36";
	public static final String CACHE_NETWORK_VAS_MAPPING = "33";
	public static final String CACHE_SERVICE_TYPE_SUBSCRIBER_ENQUIRY = "45";
	public static final String CACHE_LoadControllerCache_INSTANCE = "14";
	public static final String CACHE_LoadControllerCache_NETWORK = "15";
	public static final String CACHE_LoadControllerCache_INTERFACE = "16";
	public static final String CACHE_LoadControllerCache_TRANSACTION = "17";*/

	public static final String PRODUCT_TYPE = "PDTYP";
	public static final String USER_TRANSFER_IN_STATUS_ACTIVE = "N";
	public static final String USER_TRANSFER_IN_STATUS_SUSPEND = "Y";
	public static final String USER_TRANSFER_OUT_STATUS_ACTIVE = "N";
	public static final String USER_TRANSFER_OUT_STATUS_SUSPEND = "Y";
	public static final String PRODUCT_STATUS = "Y";
	public static final String PROFILE_ID = "PROFILEID";

	public static final String TRANSFER_EXTERNAL_TXN_INTIAL_LEVEL = "INTIAL";
	public static final String TRANSFER_EXTERNAL_TXN_FIRST_LEVEL = "FIRST";
	public static final String TRANSFER_EXTERNAL_TXN_SECOND_LEVEL = "SECOND";
	public static final String TRANSFER_EXTERNAL_TXN_LEVEL = "EXTTXNLEVEL";
	public static final String TRANSFER_EXTERNAL_TXN_MANDATORY = "EXTTXNMANDT";
	public static final String PAYMENT_INSTRUMENT_TYPE = "PMTYP";
	public static final String C2C_PAYMENT_INSTRUMENT_TYPE = "C2CPMTYP";
	public static final String VOUCHER_TYPE_LIST = "VOUTYP";
	public static final String PAYMENT_INSTRUMENT_TYPE_CASH = "CASH";
	public static final String PAYMENT_INSTRUMENT_TYPE_ONLINE = "ONLINE";
	public static final String OPERATOR_CATEGORY = "BCU";
	public static final String REQUEST_SOURCE_WEB = "WEB";
	public static final String REQUEST_SOURCE_STK = "STK";
	public static final String CHANNEL_TRANSFER_TYPE_ALLOCATION = "TRANSFER";
	public static final String CHANNEL_TRANSFER_TYPE_RETURN = "RETURN";
	public static final String CHANNEL_TRANSFER_O2C_ID = "OT";
	public static final String CHANNEL_RETURN_O2C_ID = "OR";
	public static final String CHANNEL_WITHDRAW_O2C_ID = "OW";
	public static final String CHANNEL_TO_CHANNEL_TRANSFER_ID = "CT";
	public static final String CHANNEL_TO_CHANNEL_RETURN_ID = "CR";
	public static final String CHANNEL_TO_WITHDRAW_RETURN_ID = "CW";
	public static final String CHANNEL_TYPE_O2C = "O2C";
	public static final String CHANNEL_TYPE_C2C = "C2C";
	public static final String CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW = "W";
	public static final String CHANNEL_TRANSFER_SUB_TYPE_RETURN = "R";
	public static final String CHANNEL_TRANSFER_SUB_TYPE_TRANSFER = "T";
	public static final String CHANNEL_TRANSFER_TYPE = "C2S_TRFTYP";
	public static final String CHANNEL_TRANSFER_C2C_TYPE = "C2C_TRFTYP";
	public static final String CHANNEL_TRANSFER_ORDER_STATUS = "CTSTA";
	public static final String CHANNEL_TRANSFER_ORDER_NEW = "NEW";
	public static final String CHANNEL_TRANSFER_ORDER_CANCEL = "CNCL";
	public static final String CHANNEL_TRANSFER_ORDER_CLOSE = "CLOSE";
	public static final String CHANNEL_TRANSFER_ORDER_APPROVE1 = "APPRV1";
	public static final String CHANNEL_TRANSFER_ORDER_APPROVE2 = "APPRV2";
	public static final String CHANNEL_TRANSFER_ORDER_APPROVE3 = "APPRV3";
	public static final String TRANSFER_STOCK_TYPE_HOME = "HOME";
	public static final String TRANSFER_STOCK_TYPE_ROAM = "ROAM";
	public static final String TRANSFER_CATEGORY_SALE = "SALE";
	public static final String TRANSFER_CATEGORY_FOC = "FOC";
	public static final String TRANSFER_CATEGORY_TRANSFER = "TRF";
	public static final String TRANSFER_CATEGORY = "TRCAT";
	public static final String TRANSFER_STATUS = "TSTAT";
	public static final String CHANNEL_TRANSFER_STATUS = "C2SSTAT";
	public static final String CHANNEL_TRANSFER_C2C_STATUS = "C2CSTAT";
	public static final int NET_PREFIX_DGT = 5;
	public static final String CREDIT = "CR";
	public static final String DEBIT = "DR";
	public static final String CATEGORY_TYPE_OPT = "OPT";
	public static final String DOMAIN_TYPE_OPT = "OPT";
	public static final int OPT_SEQUENCE_NUMBER = 0;
	public static final String CATEGORY_USER_TYPE = "OPERATOR";
	public static final String USER_TYPE_OPERATOR = "Operator";
	public static final String TRANSFER_RULE_TYPE_OPT = "OPT";
	public static final String TRANSFER_RULE_TYPE_CHANNEL = "CHANNEL";
	public static final String NETWORK_STOCK_TRANSACTION_TRANSFER = "TRANSFER";
	public static final String NETWORK_STOCK_TRANSACTION_CREATION = "CREATION";
	public static final String NETWORK_STOCK_TRANSACTION_RETURN = "RETURN";
	public static final String NETWORK_STOCK_TRANSACTION_ID = "NT";
	public static final String NETWORK_STOCK_TRANSACTION_WITHDRAW = "WITHDRAW";
	public static final String STK_PROFILE_ACTIVE = "Y";
	public static final String ROLE_TYPE = "RLTYP";
	public static final boolean USE_HOME_STOCK_TRUE = true;
	public static final String CIRCLE_LOCATION_TYPE = "CIR";
	public static final String ROAM_LOCATION_TYPE = "RMCIR";
	public static final String ROOT_PARENT_ID = "ROOT";
	public static final String GATEWAY_MESSAGE_SUCCESS = "200";
	public static final String GATEWAY_MESSAGE_FAILED = "500";
	public static final String UPD_SIM_TXN_ID = "1111111";
	public static final String NETWORK_STOCK_STATUS = "NSTAT";
	public static final String NETWORK_STOCK_TYPE = "STTYP";
	public static final String SMS_INTERFACE_ALLOWED = "Y";
	public static final String PREF_TR_ID_REQ = "TR_ID_REQ";
	public static final String PREF_LG_MN_REQ = "LG_MN_REQ";
	public static final String PREF_SMS_P_INDX = "SMS_P_INDX";
	public static final String PREF_PROD_REQ = "PROD_REQ";
	public static final String PRODUCT_STATUS_ACTIVE = "Y";
	public static final String PRODUCT_STATUS_SUSPEND = "S";
	public static final String PRODUCT_STATUS_DELETE = "N";
	public static final String NETWORK_PRODUCT_STATUS_ACTIVE = "Y";
	public static final String NETWORK_PRODUCT_STATUS_SUSPEND = "S";
	public static final String NETWORK_PRODUCT_STATUS_DELETE = "N";
	public static final String NETWK_PRODUCT_USAGE_DISTRIBUTION = "D";
	public static final String NETWK_PRODUCT_USAGE_CONSUMPTION = "C";
	public static final String NETWK_PRODUCT_USAGE_BOTH = "B";
	public static final String CARD_GROUP_SET_ID = "CARD_SETID";
	public static final String CARD_GROUP_ID = "CARD_GRPID";
	public static final String COMMISSION_PROFILE_SET_ID = "COMM_SETID";
	public static final String MASTER_SHEET = "MasterSheet_DVD";
	public static final String COMMISSION_PROFILE_PRODUCT_ID = "COMM_PROID";
	public static final String COMMISSION_PROFILE_DETAIL_ID = "COMM_DETID";
	public static final String ADDITIONAL_COMMISSION_SERVICE_ID = "ADD_SERID";
	public static final String ADDITIONAL_COMMISSION_PROFILE_ID = "ADD_COMMID";
	public static final String PROFILE_OTF_ID = "COMMOTF";
	public static final String SUBSCRIBER_TRANSFER_OUTCOUNT = "SEP_TRF_CTRL";
	public static final String TRANSFER_TYPE_O2C = "O2C";
	public static final String TRANSFER_TYPE_C2C = "C2C";
	public static final String TRANSFER_TYPE_FOC = "FOC";
	public static final String TRANSFER_TYPE_C2S = "C2S";
	public static final String TRANSFER_TYPE_C2S_CREDITBACK = "RCH_CR";
	public static final String TRANSFER_TYPE_P2P_CREDITBACK = "P2P_CR";
	public static final String STATUS_ACTIVE = "Y";
	public static final String STATUS_SUSPEND = "S";
	public static final String NO_CURRENT_VERSION="cardgroup.cardgroupp2pdetails.error.nocurrentversion";
	public static final String CARD_GROUP_ALREADY_EXIST="cardgroup.error.cardgroupalreadyexist";
	public static final String INVALID_SLAB="cardgroup.cardgroupdetails.voucher.error.invalidslab";
	public static final String CARD_GROUP_NAME_ALREADY_EXIST ="cardgroup.error.cardgroupnamealreadyexist";
	public static final String VERSION_NOT_EXIST="cardgroup.error.cardgroupversionnotexist";
	public static final String STATUS_DELETE = "N";
	public static final String STATUS_CANCELED = "C";
	public static final String CHANNEL_USER_TRANSFER_MODE = "UTRMD";
	public static final String ADJUSTMENT_TYPE_DIFFERENTIAL = "DIFFC";
	public static final String ID_GEN_ADJUSTMENT_NO = "ADJUST";
	public static final String USER_TYPE = "USER TYPE";
	public static final String NETWORK_STOCK_TXN_STATUS_NEW = "NEW";
	public static final String NETWORK_STOCK_TXN_STATUS_CANCEL = "CNCL";
	public static final String NETWORK_STOCK_TXN_STATUS_CLOSE = "CLOSE";
	public static final String NETWORK_STOCK_TXN_STATUS_APPROVE1 = "APPRV1";
	public static final String ACCESS_FROM_LOGIN = "LOGIN";
	public static final String ACCESS_FROM_PHONE = "PHONE";
	public static final String INTERFACE_STATUS_ALLOWED = "Y";
	public static final String INTERFACE_STATUS_NOTALLOWED = "N";
	public static final String USER_CODE_REQUIRED = "USER_CODE_REQUIRED";
	public static final String USE_HOME_STOCK_YES = "YES";
	public static final String AMOUNT_TYPE_PERCENTAGE = "PCT";
	public static final String AMOUNT_TYPE_AMOUNT = "AMT";
	public static final String IN = "IN";
	public static final String OUT = "OUT";
	public static final String SORTTYPE_USERNAME = "UN";
	public static final String SORTTYPE_USERTYPE = "UT";
	public static final String SORTTYPE_USERCODE = "UC";
	public static final String SORTTYPE_USER_GRADE = "UG";
	public static final String SORTTYPE_TRANSFER_ID = "TI";
	public static final String SORTTYPE_DATE = "D";
	public static final String SORTTYPE_USER_CATEGORY = "UC";
	public static final String SORTTYPE_BARRED_DATETIME = "BDT";
	public static final String SORTTYPE_USER_GEOGRAPHIC = "UG";
	public static final String SORTTYPE_DEPARTMENT = "D";
	public static final String SORTTYPE_USERID = "UI";
	public static final String SORTTYPE_DIVISION = "DIV";
	public static final String SORTTYPE_USER_STATUS = "US";
	public static final String SERVICE_TYPE = "SERVICE TYPE";
	public static final String REPORT_TYPE = "RPTTYPE";
	public static final String FILTER_TYPE_COUNT = "Count";
	public static final String FILTER_TYPE_AMOUNT = "Amount";
	public static final String GEO_DOMAIN_STATUS = "Y";
	public static final String SENDER_NETWORK_CODE = "S";
	public static final String RECEIVER_NETWORK_CODE = "R";
	public static final String MONTHLY_FILTER = "MONTHLY";
	public static final String DAILY_FILTER = "DAILY";
	public static final String MOBILE_NO = "MOBILE";
	public static final String DATE_CHECK_PREVIOUS = "PREVIOUS";
	public static final String DATE_CHECK_CURRENT = "CURRENT";
	public static final String USERTYPE_CHANNEL_USER = "Channel User";
	public static final String USERTYPE_CUSTOMER = "Customer";
	public static final String DOMAIN_TYPE_SALECENTER = "SALE_CENTER";
	public static final String STATUS_IN = "IN";
	public static final String STATUS_NOTIN = "NOT IN";
	public static final String STATUS_EQUAL = "EQUAL";
	public static final String STATUS_NOTEQUAL = "NOTEQUAL";
	public static final String ICCID_CHECKSTRING = "9819";
	public static final String CHANNEL_USER_HIERARCHY_STATUS = "UHSTT";
	public static final String GATEWAY_DISPLAY_ALLOW_YES = "Y";
	public static final String GATEWAY_MODIFIED_ALLOW_YES = "Y";
	public static final String DOMAINS_FIXED = "F";
	public static final String DOMAINS_ASSIGNED = "A";
	public static final String LAST_TRANSACTION_C2C_TYPE = "C2C";
	public static final String LAST_TRANSACTION_C2S_TYPE = "C2S";
	public static final String REPORTS = "RPT";
	public static final String CRYSTAL_REPORTS = "CRPT";
	public static final String C2S_TRANSFER_STATUS = "C2SST";
	public static final String TXN_LOG_REQTYPE_REQ = "REQ";
	public static final String TXN_LOG_REQTYPE_RES = "RES";
	public static final String TXN_LOG_REQTYPE_INT = "INT";
	public static final String TXN_LOG_TXNSTAGE_RECIVED = "RECEIVE";
	public static final String TXN_LOG_TXNSTAGE_PROCESS = "PROCESS";
	public static final String TXN_LOG_TXNSTAGE_INVAL = "VAL";
	public static final String TXN_LOG_TXNSTAGE_INTOP = "TOP";
	public static final String TXN_LOG_TXNSTAGE_DEBIT = "DEBIT";
	public static final String TXN_LOG_TXNSTAGE_CREDITBACK = "CRBACK";
	public static final String TXN_LOG_TXNSTAGE_GETCONN = "CONN";
	public static final String TXN_LOG_TXNSTAGE_SENDREQ = "SEND";
	public static final String TXN_LOG_TXNSTAGE_GETRESPONSE = "RESP";
	public static final String TXN_LOG_STATUS_SUCCESS = "SU";
	public static final String TXN_LOG_STATUS_FAIL = "FA";
	public static final String TXN_LOG_STATUS_EXECUTED = "EX";
	public static final String TXN_LOG_STATUS_SCHEDULE = "SC";
	public static final String TXN_LOG_STATUS_UNDERPROCESS = "UP";
	public static final String TRANSFER_PROFILE_STATUS_DELETE = "N";
	public static final String BARRED_SUBSCRIBER_SELF_RSN = "SELF";
	public static final String INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION = "V";
	public static final String INTERFACE_NETWORK_PREFIX_UPDATE_ACTION = "U";
	public static final String INTERFACE_NETWORK_PREFIX_METHOD_TYPE_PRE = "PRE";
	public static final String INTERFACE_NETWORK_PREFIX_METHOD_TYPE_POST = "POST";
	public static final String C2C_TRANSFER_RULE_FIRST_APPROVAL_LIMIT = "999999998";
	public static final String C2C_TRANSFER_RULE_SECOND_APPROVAL_LIMIT = "999999999";
	public static final String KEY_VALUE_C2C_STATUS = "C2S_STATUS";
	public static final String USAGE_TYPE = "USAGE";
	public static final String KEY_VALUE_IN_RESPONSE_CODE = "IN_RESP_CD";
	public static final String USER_NAME_PREFIX_TYPE = "USRPX";
	public static final String DOMAIN_TYPE_CODE = "OPERATOR";
	public static final String TRANSFER_TYPE = "TRFT";
	public static final String OUTLET_TYPE = "OLTYP";
	public static final String DB_FLAG_UPDATE = "U";
	public static final String DB_FLAG_INSERT = "I";
	public static final String NETWORK_TYPE_DEFAULT = "C";
	public static final String SERVICE_KEYWORD_STATUS_TYPE = "SKSTA";
	public static final String BARRING_TYPE = "BARTP";
	public static final String ROUTING_SUBSCRIBER_TYPE = "RSTP";
	public static final int P2P_MESSAGE_LENGTH_CHANGE_LANGUAGE = 3;
	public static final int C2S_MESSAGE_LENGTH_CHANGE_LANGUAGE = 3;
	public static final String SUB_TRA = "SUBSCRIBER_TRANSFERS";
	public static final String TRA_ITEMS = "TRANSFER_ITEMS";
	public static final String ALL = "ALL";
	public static final String CIRCLE_NETWORK_TYPE = "C";
	public static final String KEY_VALUE_P2P_STATUS = "P2P_STATUS";
	public static final String TXN_STATUS_SUCCESS = "200";
	public static final String PREFERENCE_TYPE = "PRFTP";
	public static final String NODATA_ENTERED = "NODATA";
	public static final String P2P_BARTYPE_LOOKUP_CODE = "P2PBARTYPE";
	public static final String C2S_BARTYPE_LOOKUP_CODE = "C2SBARTYPE";
	public static final String CHANNLE_USER_BARTYPE_LOOKUP_CODE = "CHLBARTYPE";
	public static final String CHNLCATEGORY = "CHUSR";
	public static final String AGENTCATEGORY = "AGENT";
	public static final String AGENT_ALLOWED = "Y";
	public static final String SUB_SERVICES = "SBSER";
	public static final String TRANSFER_TYPE_TRANSFER = "TRF";
	public static final String TRANSFER_TYPE_SALE = "SALE";
	public static final String TRANSFER_TYPE_FOR_TRFRULES = "TRFTY";
	public static final String AGENT_ALLOWED_YES = "Y";
	public static final String AGENT_ALLOWED_NO = "N";
	public static final String CATEGORY_TYPE_CHANNELUSER = "CHUSR";
	public static final String CATEGORY_TYPE_AGENT = "AGENT";
	public static final String CATEGORY_TYPE_CODE = "CATTY";
	public static final String LOOKUP_CHNL_USER_ACCESS_TYPE = "ASTYP";
	public static final String LOOKUP_LOGIN_ID = "LOGIN";
	public static final String LOOKUP_MSISDN = "MSISDN";
	public static final String AGENT_CAT_CODE_APPEND = "A";
	public static final String P2P_ERRCODE_VALUS = "P2P_ERR_CD";
	public static final String C2S_ERRCODE_VALUS = "C2S_ERR_CD";
	public static final String TRANSFER_TYPE_RECON = "RECON";
	public static final String PARENT_PROFILE_ID_USER = "USER";
	public static final String PARENT_PROFILE_ID_CATEGORY = "CAT";
	public static final String SERVICE_TYPE_CHNL_RECHARGE = "RC";
	public static final String SERVICE_TYPE_VOU_PRF_MOD = "VOUPRFMOD";
	public static final String SERVICE_TYPE_CHNL_CHANGEPIN = "C2SCPN";
	public static final String SERVICE_TYPE_CHNL_LANG_NOTIFICATION = "CCHLAN";
	public static final String SERVICE_TYPE_CHNL_TRANSFER = "TRF";
	public static final String SERVICE_TYPE_CHNL_WITHDRAW = "WD";
	public static final String SERVICE_TYPE_CHNL_RETURN = "RET";
	public static final String SERVICE_TYPE_SIDREG = "REGSID";
	public static final String SERVICE_TYPE_CHNL_CHANGEMSISDN = "C2SCMSISDN";
	public static final String SERVICE_TYPE_CHNL_ETURECHARGE = "C2SRCHGSTS";
	public static final int CHNL_SELECTOR_CVG_VALUE = 1;
	public static final int CHNL_SELECTOR_VG_VALUE = 3;
	public static final int CHNL_SELECTOR_C_VALUE = 2;
	public static final int CHNL_LOCALE_LANG1_VALUE = 0;
	public static final int CHNL_LOCALE_LANG2_VALUE = 1;
	public static final String FOC_ORDER_APPROVAL_LVL = "FOC_ODR_APPROVAL_LVL";
	public static final String TRANSFER_TYPE_BA_ADJ_DR = "BA_DR";
	public static final String TRANSFER_TYPE_BA_ADJ_CR = "BA_CR";
	public static final String WEB_LANGUAGE_TYPE = "WLTYP";
	public static final String CHNL_TRA_ITEMS = "CHANNEL_TRANSFERS_ITEMS";
	public static final String CHNL_TRA = "CHANNEL_TRANSFERS";
	public static final String NET_STK_TRA = "NETWORK_STOCK_TRANSACTIONS";
	public static final String NET_STK_TRA_ITEMS = "NETWORK_STOCK_TRANS_ITEMS";
	public static final String C2S_TRA = "C2S_TRANSFERS";
	public static final String C2S_TRA_ITEMS = "C2S_TRANSFER_ITEMS";

	public static final String FREQUENCY_MINUTS = "MINUTES";
	public static final String FREQUENCY_HOUR = "HOUR";
	public static final String FREQUENCY_DAY = "DAY";
	public static final String FREQUENCY_MONTH = "MONTH";
	public static final String FREQUENCY_YEAR = "YEAR";

	public static final String PREFERENCE_VALUE_TYPE = "VALTP";
	public static final String NETWORK_STOCK = "NWSTOCK";

	public static final String SERVICE_TYPE_FOR_SLAB = "SERV";
	public static final String SLAB_ID = "SLABID";

	public static final String MAX_LONG_VALUE = "999999999999";
	public static final String TRANSACTION_SOURCE_TYPE = "SRTYP";

	public static final String TRANSFER_TYPE_RCH_DEBIT = "RCH_DR";

	public static final String C2C_TRANSFER_TYPE = "C2CTR";

	public static final String SUB_SERVICES_FOR_TRANSFERRULE = "SSTTR";
	public static final String SUPER_CHANNEL_ADMIN = "SUBCU";

	// Added by Ankit Singhal for corporate
	public static final String STATUS_DEASSOCIATED = "D";

	// Restricted Msisdns Module(Amit Ruwali)
	public static final String LOOKUP_TYPE_RES_MSISDN_STATUS = "RESST";
	public static final String LOOKUP_TYPE_BLACK_LIST_STATUS = "BLKST";

	
	//MSISDN Pattern
	public static final Pattern MSISDN_PATTERN = Pattern.compile("^[0-9]{6,15}$");
	public static final String RES_MSISDN_STATUS_NEW = "W";
	public static final String RES_MSISDN_STATUS_APPROVED = "A";
	public static final String RES_MSISDN_STATUS_SUSPENDED = "S";
	public static final String RES_MSISDN_STATUS_ASSOCIATED = "Y";
	public static final String RES_MSISDN_BLACKLIST_STATUS = "Y";
	public static final String RES_MSISDN_UNBLACKLIST_STATUS = "N";
	public static final String RES_MSISDN_STATUS_DELETED = "N";
	// Schedule TopUp status
	public static final String SCHEDULE_STATUS_SCHEDULED = "S";
	public static final String SCHEDULE_STATUS_UNDERPROCESSED = "U";
	public static final String SCHEDULE_STATUS_EXECUTED = "E";
	public static final String SCHEDULE_STATUS_CANCELED = "C";

	public static final String SCHEDULE_BATCH_ID = "SB";
	public static final String SCHEDULE_BATCH_STATUS_LOOKUP_TYPE = "SCHE";
	public static final String RES_MSISDN_STATUS_DEASSOCIATE = "A";
	// ADDED BY ASHISH FOR SCHEDULED TOPUP-PROCESS DATE 12/04/06
	public static final String REST_SCH_BATCH_STATUS_UNDER_PROCESS = "U";
	public static final String REST_SCH_BATCH_STATUS_SCHEDULED = "S";
	public static final String REST_SCH_BATCH_STATUS_EXECUTED = "E";
	public static final String SCHEDULED_BATCH_DETAIL_STATUS_EXECUTED = "E";
	public static final String REST_SCH_SUBS_MSISDN_STATUS = "Y";

	// User Phone Primary Number
	public static final String USER_PHONE_PRIM_STATUS = "Y";

	// FOR RESTRICTED APPROVAL Ashish
	public static final String RES_MSISDN_STATUS_REJECT = "R";
	public static final String RES_MSISDN_STATUS_DISCARD = "D";
	public static final String DOMAINS_NOTFIXED_NOTASSIGNED = "N";

	// entries for the transfer rules changes
	public static final String NOT_APPLICABLE = "NA";
	public static final String UNCONTROLL_TXN_LEVEL = "UNCTL";
	public static final String CONTROLL_TXN_LEVEL = "CNTRL";
	public static final String FIXED_LEVEL = "FIXDL";

	// entried for the control or uncontrol transactions
	public static final String CHANNEL_TRANSACTION_CONTROL = "C";
	public static final String CHANNEL_TRANSACTION_UNCONTROL = "U";

	// entries for the channel transfer controlling parametes.
	public static final String CHANNEL_TRANSFER_LEVEL_SELF = "SELF";
	public static final String CHANNEL_TRANSFER_LEVEL_PARENT = "PARENT";
	public static final String CHANNEL_TRANSFER_LEVEL_OWNER = "OWNER";
	public static final String CHANNEL_TRANSFER_LEVEL_DOMAIN = "DOMAIN";
	public static final String CHANNEL_TRANSFER_LEVEL_DOMAINTYPE = "DOMAINTYPE";
	public static final String CHANNEL_TRANSFER_LEVEL_SYSTEM = "SYSTEM";

	public static final String CHANNEL_TRANSFER_FIXED_LEVEL_PARENT = "PARENT";
	public static final String CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY = "HIERARCHY";

	// added for postpaid controller on date=16/05/06
	public static final String STATUS_QUEUE_AVAILABLE = "0";
	public static final String STATUS_QUEUE_FAIL = "5";

	public static final String WHITE_LIST_MOVEMENT_CODE = "MCDE";
	public static final String WHITE_LIST_STATUS = "WLSTA";
	public static final String SERVICE_TYPE_BILLPAYMENT = "BILLPMT";

	// for the admin logger for channel transfer rules
	public static final String LOGGER_TRANSFER_RULE_SOURCE = "TRANSFER_RULE";

	public static final String LOGGER_RESTRICTED_LIST = "RESTRICTED_LIST";
	public static final String LOGGER_CARD_GROUP_SOURCE = "CARD_GROUP";
	// Process ID of CDR generation
	public static final String PROCESS_ID_CDR = "CDR";
	// Interface ID of CDR generation
	public static final String INTERFACE_ID_CDR = "INT003";
	// added for PPB service type
	public static final String SERVICE_TYPE_CHNL_BILLPAY = "PPB";

	// for channel user transfer controlling level
	public static final String CONTROL_LEVEL_ADJ = "A";

	// Added for modify instance load (10-06-06)
	public static final String LOAD_TYPE_TPS_TPS = "Y";
	public static final String LOAD_TYPE_TPS_TRANSACTION = "N";
	public static final String INSTANCE_TYPE_SMS = "SMS";
	public static final String INSTANCE_TYPE_WEB = "WEB";
	// ENTRY FOR ADMIN LOGGER
	public static final String LOGGER_NETWORK_PREFIXES = "NETWORK_PREFIXES";

	public static final String GRPT_CONTROL_LEVEL_USERID = "U";
	public static final String GRPT_CONTROL_LEVEL_MSISDN = "M";
	public static final String GRPT_TYPE_CHARGING = "CHRG";
	public static final String GRPT_TYPE_CONTROLLING = "CTRL";
	public static final String GRPT_TYPE_FREQUENCY_DAILY = "D";
	public static final String GRPT_TYPE_FREQUENCY_MONTHLY = "M";

	public static final String CHANNEL_TRANSFER_BATCH_FOC_STATUS_LOOKUP_TYPE = "BTSTA";
	public static final String CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN = "OPEN";
	public static final String CHANNEL_TRANSFER_BATCH_FOC_STATUS_CLOSE = "CLOSE";
	public static final String CHANNEL_TRANSFER_BATCH_FOC_STATUS_CANCEL = "CANCEL";
	public static final String CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS = "UNDPROCESS";
	public static final String CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED = "P";
	public static final String CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_SCHEDULED = "S";

	public static final String FOC_BATCH_TRANSACTION_ID = "FB";

	// Batch user creation
	public static final String BATCH_USR_SERVICE_LIST = "SERVICE_LIST";
	public static final String BATCH_USR_GEOGRAPHY_LIST = "GEOGRAPHY_LIST";
	public static final String BATCH_USR_GEOGRAPHY_TYPE_LIST = "GEOGRAPHY_TYPE_LIST";
	public static final String BATCH_USR_CATEGORY_HIERARCHY_LIST = "CATEGORY_HIERARCHY_LIST";
	public static final String BATCH_USR_CATEGORY_LIST = "CATEGORY_LIST";
	public static final String BATCH_USR_GRADE_LIST = "GRADE_LIST";
	public static final String BATCH_USR_TRANSFER_CONTROL_PRF_LIST = "TRANSFER_CONTROL_PRF_LIST";
	public static final String BATCH_USR_COMMISION_PRF_LIST = "COMMISION_PRF_LIST";
	public static final String BATCH_USR_GROUP_ROLE_LIST = "GROUP_ROLE_LIST";
	public static final String BATCH_USR_USER_PREFIX_LIST = "USER_PREFIX_LIST";
	public static final String BATCH_USR_OUTLET_LIST = "OUTLET_LIST";
	public static final String BATCH_USR_SUBOUTLET_LIST = "SUBOUTLET_LIST";
	public static final String BATCH_USR_DOMAIN_NAME = "DOMAIN_NAME";
	public static final String BATCH_USR_CREATED_BY = "CREATED_BY";
	public static final String BATCH_USR_GEOGRAPHY_NAME = "GEOGRAPHY_NAME";
	public static final String GRPH_DOMAIN_CODE = "GRAPH DOMAIN CODE";
	// Changes Made by Puneet
	public static final String USER_MIG_HEADING = "USER MIGRATION";
	// ends

	public static final String MANUAL_USR_CREATION_TYPE = "M";
	public static final String BATCH_USR_CREATION_TYPE = "B";
	public static final String USR_BATCH_STATUS_OPEN = "O";
	public static final String USR_BATCH_STATUS_CLOSE = "C";
	public static final String USR_BATCH_STATUS_REJECT = "R";
	public static final String USR_BATCH_STATUS_UNDERPROCESS = "U";

	public static final String USR_PHONE_ID = "PHONE_ID";
	public static final String BULK_USR_BATCH_ID = "BATCH_ID";
	public static final String BULK_USR_BATCH_TYPE = "BULK_USR_CREATION";
	public static final String BULK_USR_STATUS_ACTIVE = "Y";
	public static final String BULK_USR_STATUS_REJECT = "N";
	public static final String BULK_USR_STATUS_DISCARD = "D";
	public static final String BULK_USR_ID_PREFIX = "BU";
	public static final String OUTLET_TYPE_DEFAULT = "TCOM";

	public static final String ROLE_TYPE_FOR_GROUP_ROLE = "A";
	public static final String BATCH_USR_PROCESS_ID = "BULKUSER";
	public static final String BATCH_STATUS_LOOKUP = "BHSTA";
	// for
	// interface
	// routing
	// control
	// cache

	// added by sandeep goel for the commission profile status change logger
	public static final String LOGGER_COMMISSION_PROFILE_SOURCE = "COMMISSION_PROFILE";
	// ends here

	// added by sandeep goel for the foc by batch status in the process status
	// table
	public static final String FOC_BATCH_PROCESS_ID = "FOCBATCH";
	public static final String USR_CREATION_TYPE = "UCRTY";
	// ends here
	public static final String P2P_RECON_INTERFACE_WISE_PROCESS_ID = "P2PRECONINTRPT";
	public static final String P2P_RECON_SERVICE_WISE_PROCESS_ID = "P2PRECONSERRPT";
	public static final String C2S_RECON_INTERFACE_WISE_PROCESS_ID = "C2SRECONINTRPT";
	public static final String KEY_VALUE_TYPE_REOCN = "RECON"; // For the p2p
																// and c2s
																// reconcilation

	public static final String ADJUSTMENT_TRANSACTION_ID = "AD"; // for the
																	// Adjustment
																	// txn ID
	public static final String DAILY_STOCK_CREATION_TYPE_MAN = "M"; // insertion
																	// by the
																	// txn
	public static final String DAILY_BALANCE_CREATION_TYPE_MAN = "M"; // insertion
																		// by
																		// the
																		// txn

	public static final String SMS_LOCALE = "SMS";// Check the locale is
													// applicable for SMS
	public static final String BOTH_LOCALE = "BOTH";// Check the locale is
													// applicable to WEB
	public static final String WEB_LOCALE = "WEB";// Check the locale is
													// applicable to BOTH(WEB
													// and
													// SMS)
	public static final String LANG1_MESSAGE = "LANG1";// Send language 1
														// message for the
														// locale
	public static final String LANG2_MESSAGE = "LANG2";// Send language 2
														// message for the
														// locale
	public static final short FORM_NCHAR = 2;// Set the char set value to 2 for
												// prepared statement
	public static final short FORM_CHAR = 1;// Set the char set value to 1 for
											// prepared statement

	public static final String SERVICE_TYPE_CHNL_O2C_IN = "O2CIN";
	public static final String SERVICE_TYPE_CHNL_O2C_INTR = "O2CINTR";
	
	public static final String SERVICE_TYPE_CHNL_C2C_INTR = "C2CINTR";
	
	public static final String SERVICE_TYPE_CHNL_O2C_RET = "O2CRET";
	public static final String SERVICE_TYPE_CHNL_O2C_WTDW = "O2CWD";

	// added for O2C transferm, return & withdrawal
	public static final String CATEGORY_CODE_NETWORK_ADMIN = "NWADM";

	public static final String MSISDN_VALIDATION = "M";
	public static final String OTHER_VALIDATION = "O";
	public static final String BOTH_VALIDATION = "B";

	public static final int C2S_MESSAGE_LENGTH_LAST_RECHARGE = 3;
	public static final String SERVICE_TYPE_RECHARGE_STATUS = "RS";

	// VOMS integration start for EVD/EVR controller
	public static final String INTERFACE_CATEGORY_VOMS = "VOMS";
	public static final String SERVICE_TYPE_EVR = "EVR";
	public static final String SERVICE_TYPE_EVD = "EVD";
	public static final String SERVICE_TYPE_EVD101 = "101EVD";
	public static final String SERVICE_TYPE_EVD102 = "102EVD";
	public static final String SERVICE_TYPE_EVD104 = "104EVD";
	public static final String SERVICE_TYPE_EVD105 = "105EVD";
	public static final String SERVICE_TYPE_EVD106 = "106EVD";
	public static final String ID_GEN_EVD_TRANSFER_NO = "EVD";
	public static final String PIN_SENT_RET = "R";

	public static final String SMS_STATUS_NOTFOUND = "NOTFOUND";
	public static final String SMS_STATUS_FOUND = "FOUND";
	public static final String SMS_STATUS_EXCEPTION = "EXCEPTION";
	// VOMS integration end for EVD/EVR controller

	// added by Siddhartha for Voucher Pin Resend
	public static final String MSISDN_CHECK_RETA = "RETAILER";
	public static final String MSISDN_CHECK_DIST = "DIST";

	// control preferences
	public static final String INTERFACE_STATUS_TYPE_MANUAL = "M";
	public static final String INTERFACE_STATUS_TYPE_AUTO = "A";

	public static final String LOOKUP_TYPE_CONTROL = "CTRTY";

	// Batch operator user creation
	public static final String BATCH_OPT_USR_CATEGORY_LIST = "CATEGORY_LIST";
	public static final String BATCH_OPT_USR_CATEGORY_CODE = "CATEGORY_CODE";
	public static final String BATCH_OPT_USR_CATEGORY_NAME = "CATEGORY_NAME";
	public static final String BATCH_OPT_USR_CREATED_BY = "CREATED_BY";
	public static final String BATCH_OPT_USER_PREFIX_LIST = "USER_PREFIX_LIST";
	public static final String BATCH_OPT_USR_SERVICE_LIST = "SERVICE_LIST";
	public static final String BATCH_OPT_USR_STATUS_LIST = "STATUS_LIST";
	public static final String BATCH_OPT_USR_DIVDEPT_LIST = "DIVDEPT_LIST";
	public static final String BATCH_OPT_USR_ASSIGN_ROLES = "ASSIGN_ROLES";
	public static final String BATCH_OPT_USR_GEOGRAPHY_LIST = "GEOGRAPHY_LIST";
	public static final String BATCH_OPT_USR_DOMAIN_LIST = "DOMAIN_LIST";
	public static final String BATCH_OPT_USR_PRODUCT_LIST = "PRODUCT_LIST";
	public static final String BATCH_OPT_USR_PROCESS_ID = "BATCHOPTUSER";
	public static final String BATCH_OPT_USR_ID_PREFIX = "BOU";
	public static final String BATCH_OPT_USR_ID = "OPTUSERID";
	public static final String BATCH_OPT_USR_BATCH_ID = "BATCH_ID";
	public static final String BATCH_OPT_USR_BATCH_TYPE = "BATCH_OPT_USR_CREATE";
	public static final String BATCH_OPT_USR_VOUCHERTYPE_LIST= "VOUCHERTYPE_LIST";
	public static final String BATCH_OPT_USR_VOUCHERSEGMENT_LIST= "VOUCHERSEGMENT_LIST";
	public static final String BATCH_OPT_USR_INITIATION_SERVICE= "BATCH_USER_INITIATION";
	public static final String BATCH_OPT_USR_MODIFICATION_SERVICE= "BATCH_USER_MODIFICATION";

	public static final String BATCH_USR_CATEGORY_NAME = "CATEGORY_NAME"; // Added
																			// by
																			// Sanjeew
																			// 30/03/07
	public static final String BATCH_USR_CATEGORY_VO = "CATEGORY_VO"; // Added
																		// by
																		// Sanjeew
																		// 30/03/07
	public static final String BATCH_USR_EXCEL_DATA = "BATCH_USR_EXCEL_DATA";// Added
																				// by
																				// Sanjeew
																				// 02/04/07
	public static final String BATCH_USR_ROLE_CODE_LIST = "BATCH_USR_GROUP_ROLE_CODE_LIST";// Added
																							// by
																							// Sanjeew
																							// 02/04/07
	public static final String PORTED_TYPE = "PORTP";// Mobile number
														// portability
	public static final String PORTED_IN = "IN";
	public static final String PORTED_OUT = "OUT";
	public static final String OPERATOR_TYPE_PORT = "PORT";

	// Batch transfer rules creation
	public static final String BATCH_TRF_RULES_PROCESS_ID = "BATCHTRFRULES";
	public static final String TRF_RULES_BATCH_PREFIX = "TR";
	public static final String TRF_RULES_BATCH_TYPE = "TRF_RULES_CREATION";
	public static final String TRF_RULES_ID = "TRFRULEID";
	public static final String TRF_RULES_BATCH_ID = "BATCH_ID";
	public static final String TRF_RULE_BATCH_STATUS_OPEN = "O";
	public static final String TRF_RULE_BATCH_STATUS_CLOSE = "C";
	public static final String TRF_RULE_BATCH_STATUS_REJECT = "R";
	public static final String TRF_RULE_BATCH_STATUS_UNDERPROCESS = "U";
	public static final String SERVICE_TYPE_FOR_EVD = "EVD";
	public static final String ACCOUNT_TYPE_MAIN = "MAIN";
	public static final String ACCOUNT_TYPE_TOTAL = "TOTAL";
	// Added for suspend resume service.
	public static final String USER_STATUS_ACTIVE_R = "R";

	public static final String SERVICE_TYPE_MVD = "MVD";
	// Added for Rusume/Suspend of all P2P Services
	public static final String SERVICE_TYPE_P2PSUSPEND = "PSUS";
	public static final String SERVICE_TYPE_P2PRESUME = "PRES";

	// Added for Add/Delete/List Buddy
	public static final String SERVICE_TYPE_ADD_BUDDY = "PADD";
	public static final String SERVICE_TYPE_DELETE_BUDDY = "PDEL";
	public static final String SERVICE_TYPE_LIST_BUDDY = "PLIST";

	// Added for Delete Buddy List by harsh on 09Aug12
	public static final String SERVICE_TYPE_DELETE_BUDDY_LIST = "MULTDEL";

	public static final String SERVICE_TYPE_CHNL_BALANCE_ENQUIRY = "C2SBAL"; // added
																				// for
																				// Balance
																				// Enquiry
																				// 03/05/07
	public static final String SERVICE_TYPE_CHNL_DAILY_STATUS_REPORT = "C2SDAILYTR"; // added
																						// for
																						// Daily
																						// Status
																						// Report
																						// 03/05/07
	public static final String SERVICE_TYPE_LAST_TRANSFER_STATUS = "C2SLASTTRF"; // added
																					// for
																					// Last
																					// Transfer
																					// Status(RP2P)
																					// 03/05/07
	public static final String SERVICE_TYPE_P2P_LAST_TRANSFER_STATUS = "PLT"; // added
																				// for
																				// Last
																				// Transfer
																				// Status(P2P)
																				// 03/05/07
	public static final String SERVICE_TYPE_SELF_BAR = "BARUSER";// self Bar
	public static final String SERVICE_TYPE_CHNL_EVD = "EVD";
	public static final String SERVICE_TYPE_CHNL_BALANCE_XML = "BAL";

	// Added for Multple Electronic Voucher Distribution of all C2S Services
	public static final String SERVICE_TYPE_MEVD_REQUEST = "MEVDREQ";
	// Added for Utility Bill Payment of all C2S Services
	public static final String SERVICE_TYPE_UBILLPAYMENT_REQUEST = "UBPREQ";
	public static final String SERVICE_TYPE_UTILITY_BILLPAY = "UBP";

	public static final String CARD_GROUP_SET_TYPE = "SETTY";
	public static final String TRANSFER_RULE_PROMOTIONAL = "P";
	public static final String TRANSFER_RULE_NORMAL = "N";
	public static final int PROMO_TRF_RULE_LVL_USR_CODE = 1;
	public static final int PROMO_TRF_RULE_LVL_GRADE_CODE = 2;
	public static final int PROMO_TRF_RULE_LVL_CATEGORY_CODE = 3;
	public static final int PROMO_TRF_RULE_LVL_GEOGRAPHY_CODE = 4;
	public static final int PROMO_TRF_RULE_LVL_PREFIX_ID = 5;// added by rahul
																// for prefix id
																// based checks

	// added by sanjeew for Batch Promotional Transfer rule
	public static final String CARDGROUP_SET_TYPE_PROMOTIONAL = "P";
	public static final String CARDGROUP_SET_TYPE_NORMAL = "N";
	public static final String PROMOTIONAL_LEVEL = "PROMO";
	public static final String PROMOTIONAL_LEVEL_USER = "USR";
	public static final String PROMOTIONAL_LEVEL_GRADE = "GRD";
	// VFE public String PROMOTIONAL_LEVEL_GRADE = "GDR";
	public static final String PROMOTIONAL_LEVEL_GEOGRAPHY = "GRP";
	public static final String PROMOTIONAL_LEVEL_CATEGORY = "CAT";
	public static final String DEFAULT_SELECT_TYPE = "Y";
	// Prefix ID based promotional rule
	public static final String PROMOTIONAL_LEVEL_PREFIXID = "PRX";

	public static final String PROMOTIONAL_INTERFACE_CATEGORY_CLASS = "SERVINTCLASS";
	public static final String PROMOTIONAL_BATCH_TRF_RULE = "Promotional transfer rule";
	public static final String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_USR = "USR";
	public static final String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CAT = "CAT";
	public static final String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRP = "GRP";
	// public static final String
	// PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD="GDR";
	public static final String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD = "GRD";
	public static final String PROMOTIONAL_BATCH_TRF_MODULE = "C2S";
	public static final String PROMOTIONAL_BATCH_TRF_RULE_TYP = "P";
	public static final String PROMOTIONAL_BATCH_TRF_RULE_STATUS = "Y";
	public static final String PROMOTIONAL_BATCH_TRF_RULE_SENDER_SERVICE_CLASS_ID = "ALL";
	public static final String PROMOTIONAL_BATCH_TRF_RULE_MSISDN_NOTVALIED = "Invalid Mobile Number";

	// added by siddhartha for Account Master Details.
	public static final String ACCOUNT_MASTER_INTERFACE_ACCOUNT_TYPE = "ACCTY";
	public static final String ACCOUNT_MASTER_STATUS_LIST = "ACCST";
	public static final String ACCOUNT_TYPE_BONUS = "B";
	public static final String ACCOUNT_TYPE_NORMAL = "N";
	public static final String ACCOUNT_MODE_SINGLE_REQ = "SR";
	public static final String ACCOUNT_MODE_MULTIPLE_REQ = "MR";

	// added by ashish srivastav for Iccid Batch Delete
	public static final String ICCID_DELETEABLE = "DELETEABLE";
	public static final String ICCID_USER_ASSOCIATED = "ASSOCIATEDCHNLUSER";
	public static final String ICCID_MSISDN_ASSOCIATED = "ASSOCIATEDTOMSISDN";
	public static final String ICCID_NOT_EXISTING = "NOTEXIST";
	public static final String ICCID_IMSI = "ICCID/IMSI";

	// for Zebra and Tango added by sanjeew date 06/07/07
	public static final String M_PAY_PROFILE_LIST = "MPAYPROFILELIST";
	// end Zebra and Tango

	public static final String LOOKUP_TYPE_CONTROL_CAT = "CATPRF";
	public static final String LOOKUP_TYPE_CONTROL_ZONE = "ZONEPRF";
	public static final String LOOKUP_TYPE_CONTROL_INTERFACE = "INTPREF";
	public static final String LOOKUP_TYPE_CONTROL_SERVICE_TYPE = "SERTYPPREF";

	// Added by Vinay for ExternalFile(IMA File) on 24/07/07
	public static final String EXTERAL_FILE_PROCESS_ID = "EXTERNLFILERPT";

	public static final String SERVICE_TYPE_P2PRECHARGE_SINGLEREQUEST = "SPRC";

	public static final String SRV_CLASS_USER_TYPE = "SCUTY";
	public static final String SRV_CLASS_STATUS = "LKTST";
	public static final String SRV_CLASS_MODULE = "MODTY";// used for service
															// eligibility
															// module.

	// TIME IN MILISECONDS i.e 5 Seconds for Aktel::AshishS
	public static final long RECEIVER_UNDERPROCESS_UNBLOCK_TIME = 300000;

	// added by ranjana for C2S transfer bill payment
	public static final String SERVICE_TYPE_POSTPAID_BILL_PAYMENT = "PPB";

	// add for password management by santanu
	public static final String USER_PASSWORD_MANAGEMENT = "PWD";
	public static final String USER_PIN_MANAGEMENT = "PIN";
	public static final String USER_FIXED_DOMAIN = "F";
	public static final String PWD_DOMAIN_CODE = "OPT";
	public static final String PIN_USER_CP2P = "CP2P";
	public static final String PIN_USER_RP2P = "RP2P";
	public static final String PIN_USER_CHU = "CHU";
	public static final String PIN_USER_STAFF = "STAFF";
	public static final String PWD_USER_NAME = "ALL";
	public static final String PWD_USER_OPTU = "OPTU";
	public static final String PWD_USER_SUADM = "SUADM";
	public static final String PWD_CAT_CODE_SUADM = "SUADM";
	public static final String PWD_CAT_CODE_NWADM = "NWADM";
	public static final String PWD_CAT_CODE_CCE = "CCE";
	public static final String LOOKUP_USER_TYPE = "USRTP";
	public static final String USER_TYPE_OPT = "OPERATOR";
	public static final String USER_TYPE_CHANNEL = "CHANNEL";
	public static final String USER_TYPE_STAFF = "STAFF";
	public static final String USER_TYPE_P2P = "P2P";
	

	// added by PN for cellplus p2p controller
	public static final String P2P_SENDER_ACCOUNT_STATUS_ACTIVE_ONIN = "TRUE";
	public static final String P2P_RECEIVER_ACCOUNT_STATUS_ACTIVE_ONIN = "TRUE";

	public static final String P2P_SENDER_ACCOUNT_STATUS_INACTIVE_ONIN = "INACTIVE";
	public static final String P2P_RECEIVER_ACCOUNT_STATUS_INACTIVE_ONIN = "INACTIVE";
	public static final long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;

	// for C2S Enquiry through external system
	public static final String SERVICE_TYPE_C2S_ENQUIRY = "C2SENQ";

	public static final String SUFIX_ADJUST_TXN_ID_NW = "N";
	public static final String SUFIX_ADJUST_TXN_ID_USER = "U";

	// For Black list restricted subscribers not allowed for recharge or for
	// CP2P services.
	public static final String CP2P_PAYER = "CP2PAYER";
	public static final String CP2P_PAYEE = "CP2PPAYEE";
	public static final String C2S_PAYEE = "C2SPAYEE";

	// added by Vipul for Modify status C2C transfer rule
	public static final String TRANSFER_RULE_TYPE = "TRTYP";// shows controlled
															// or uncontrolled
															// transfer
	// added by Vipul for Modify status O2C & C2C transfer rule
	public static final String TRANSFER_RULE_STATUS = "TRST";// Status suspended
																// or active
	public static final String REQUEST_SOURCE_TYPE_DUMMY = "DUMMY";// Dummy
																	// entry for
																	// Apache if
																	// used for
																	// load
																	// balancing.

	// Gift Recharge
	public static final String C2S_REC_GEN_FAIL_MSG_REQD_V_FOR_GIFTER = "C2S_REC_GEN_FAIL_MSG_REQD_V_FOR_GIFTER";
	public static final String C2S_REC_GEN_FAIL_MSG_REQD_T_FOR_GIFTER = "C2S_REC_GEN_FAIL_MSG_REQD_T_FOR_GIFTER";
	public static final String SERVICE_TYPE_CHNL_COMMON_RECHARGE = "CRC";
	public static final String SERVICE_TYPE_CHANNEL_GIFT_RECHARGE = "GRC";

	// schedule batch recharge (Manisha 29/04/08)
	public static final String BATCH_TYPE_CORPORATE = "CORPORATE";
	public static final String BATCH_TYPE_NORMAL = "NORMAL";
	public static final String BATCH_TYPE_BOTH = "BOTH";
	public static final String BATCH_TYPE_MASTER = "MASTER";

	// gift recharge For web
	public static final String GIFT_RECHARGE_CODE = "GRC";
	public static final String SERVICE_TYPE_P2PRECHARGEWITHVALEXT = "VU";

	// gift recharge
	public static final String SERVICE_TYPE_GIFT_RECHARGE = "GRC";
	// for category List Management
	public static final String SELECT_PARENT = "P";
	public static final String SELECT_OWNER = "O";
	public static final String LOOKUP_CP2P_LIST_LEVEL = "P2PWL";
	public static final String CP2P_WITHIN_LIST_LEVEL_OWNER = "O";
	public static final String CP2P_WITHIN_LIST_LEVEL_PARENT = "P";
	public static final String CP2P_WITHIN_LIST_LEVEL_DOMAIN = "D";
	public static final String MODULE_TYPE_BOTH = "BOTH";
	public static final String FILE_CONTENT_TYPE_CSV = "CSV";
	public static final String FILE_CONTENT_TYPE_XLS = "XLS";
	public static final String FILE_CONTENT_TYPE_XLSX = "XLSX";
	public static final String FILE_CONTENT_TYPE_PLAIN_TEXT = "PLAIN_TEXT";
	public static final String FILE_CONTENT_TYPE_PDF = "PDF";

	// Batch C2C Transfer
	public static final String CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN = "OPEN";
	public static final String CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED = "P";
	public static final String CHANNEL_TRANSFER_BATCH_C2C_STATUS_CANCEL = "CANCEL";
	public static final String CHANNEL_TRANSFER_BATCH_C2C_STATUS_CLOSE = "CLOSE";
	public static final String CHANNEL_TRANSFER_BATCH_C2C_STATUS_UNDERPROCESS = "UNDPROCESS";
	public static final String C2C_BATCH_TRANSACTION_ID = "CB";
	public static final String C2C_BATCH_PROCESS_ID = "C2CBATCH";

	public static final String CHANNEL_TRANSFER_TYPE_TRANSFER = "TRANSFER";
	public static final String CHANNEL_CATEGORY_TYPE_SALE = "SALE";
	public static final String CHANNEL_TRANSFER_TYPE_WITHDRAW = "WITHDRAW";
	public static final String CHANNEL__CATEGORY_TYPE_TRANSFER = "TRF";
	public static final String CHANNEL_TRANSFER_ORDER_APPROVE = "APPRV";
	// user's assigned roles
	public static final String USERTYPE = "OPT";

	// added for USSD Recharge(CDMA,PSTN,Broadband)
	public static final String SERVICE_TYPE_CHNL_RECHARGE_CDMA = "CDMARC";
	public static final String SERVICE_TYPE_CHNL_RECHARGE_PSTN = "PSTNRC";
	public static final String SERVICE_TYPE_CHNL_RECHARGE_INTR = "INTRRC";

	// added for Bank Recharge(CDMA,PSTN,Broadband)
	public static final String SERVICE_TYPE_EXT_CHNL_RECHARGE_CDMA = "CDMARC";
	public static final String SERVICE_TYPE_EXT_CHNL_RECHARGE_PSTN = "PSTNRC";
	public static final String SERVICE_TYPE_EXT_CHNL_RECHARGE_INTR = "INTRRC";
	// added by Gopal for Batch C2C Transfer approval,
	public static final String C2C_BATCH_APPROVAL_LEVEL = "C2C_BATCH_APPROVAL_LVL";
	public static final String CHANGE_PIN_SERVICE_TYPE = "C2SCPN,CPN,USERREG";
	// Added for Controller(OrderCredit, OrderLine, BarredMsisdn)

	public static final int ORDER_CREDIT_MESSAGE_LENGTH = 3;
	public static final int ORDER_CREDIT_LENGTH_MAX_LENGTH = 9;
	public static final int ORDER_MOBILE_MESSAGE_LENGTH = 4;
	public static final int ORDER_MOBILE_QUANTITY_MAX_LENGTH = 4;
	public static final String CHANEL_BARRED_TYPE_SELF = "SELF";
	public static final String CHANEL_BARRED_USER_TYPE_SENDER = "SENDER";
	public static final String SERVICE_TYPE_EXT_CHNL_ORDER_LINE = "ORDL";
	public static final String SERVICE_TYPE_EXT_CHNL_ORDER_CREDIT = "ORDC";
	public static final String SERVICE_TYPE_EXT_CHNL_BARRED = "BAR";
	// batch C2S cardgroup modify
	public static final String BAT_MOD_C2S_CARDGROUP = "BATCH_MODIFY_CARDGROUP";
	public static final String BATCH_CARD_GROUP_EXCEL_DATA = "BATCH_CARD_GROUP_EXCEL_DATA";
	public static final String BAT_MOD_C2S_CG_PROCESS_ID = "BATCARDGROUP";
	// batch P2P cardgroup modify
	public String BAT_MOD_P2P_CG_PROCESS_ID = "BATCARDGROUPP2P";
	// added by rahul for add activation bonus
	public static final String PERIOD_TYPE = "PETYP";
	public static final String PROFIL_DETAIL_TYPE = "PRDTY";
	public static final String PROFILE_TRANS = "TRANS";
	public static final String PROFILE_VOL = "VOLUME";
	public static final String PROFILE_STATUS_ACTIVE = "Y";
	public static final String PROFILE_TYPE_ACTIVATION = "ACT";
	public static final String ACTIVATION_PROFILE_SETID = "PR_SETID";
	public static final String ACTIVATION_PROFILE_USER_TYPE_SENDER = "S";
	public static final String USER_SUB_TYPE_COUNT = "COUNT";
	public static final String USER_SUB_TYPE_AMOUNT = "AMOUNT";
	public static final String ACTIVATION_TYPE_C2S = "C2S";
	public static final String ACTIVATION_SUBSCRIBER_TYPE = "SUBAP";
	public static final String PROFILE_TYPE_ACTIVATION_BONUS = "ACT";
	public static final String PROFILE_TYPE_NONE = "NONE";
	public static final String PERIOD_TYPE_PCT = "PCT";

	public static final String ACT_PROF_TYPE = "ACT";

	public static final String SYSTEM = "SYSTEM";
	public static final String BUCKET_ONE = "1";
	public static final String NO_MAPPING_FOUND = "S";
	public static final String SUB_TYPE_AMOUNT = "AMOUNT";
	public static final String SUB_TYPE_COUNT = "COUNT";
	public static final String SERVICE_TYPE_PRE = "PRE";
	public static final String SERVICE_TYPE_POST = "POST";
	public static final String SERVICE_TYPE_ALL = "ALL";
	public static final String SERVICE_TYPE_BOTH = "BOTH";
	public static final String VOLUME_CALC_DONE = "V";

	// batch associate activation profile
	public static final String BATCH_ASSOCIATE_PROFILE_USERS = "ACTIVE_USERS";
	public static final String BATCH_ASSOCIATE_PROFILE_DOMAIN = "DOMAIN_NAME";
	public static final String BATCH_ASSOCIATE_PROFILE_CATEGORY = "CATEGORY_NAME";
	public static final String BATCH_ASSOCIATE_PROFILE_LIST = "ACTIVE_PROFILE_LIST";
	public static final String BATCH_ASSOCIATE_PROFILE_PROCESS_ID = "BATCHASSOCIATE";
	public static final String PERIOD_UNLIMITED = "UNLIMITED";

	public static final String SERVICE_TYPE_CHNL_VAS_CRBT = "CRBT";

	public static final String NETWORK_STOCK_TRANSACTION_COMMISSION = "COMMISSION";
	public static final String NETWORK_STOCK_TRANSACTION_COMMISSION_SUB_TYPE = "C";
	public static final int SMS_RECHARGE_MESSAGE_LENGTH = 3;

	// FOR IAT
	public static final String CONTROLLER = "CONTROLLER";
	public static final String IAT_TRANSACTION_TYPE = "IAT";
	public static final String IAT_SERVICE_TYPE_ROAM_RECHARGE = "RR";
	public static final String IAT_SERVICE_TYPE_INTERNATIONAL_RECHARGE = "IR";
	public static final String IAT_SERVICE_STATUS_ACTIVE = "Y";
	public static final String IAT_SERVICE_STATUS_INACTIVE = "S";
	public static final String IAT_NW_SERVICE_MAPP_ID_TYPE = "IATMP";
	public static final String IAT_COUNTRY_STATUS_ACTIVE = "Y";
	public static final String IAT_COUNTRY_STATUS_SUSPEND = "S";
	public static final String IAT_COUNTRY_STATUS_DELETED = "N";
	public static final String IAT_NETWORK_STATUS_ACTIVE = "Y";
	public static final String IAT_NETWORK_STATUS_SUSPEND = "S";
	public static final String IAT_NETWORK_STATUS_DELETED = "N";
	public static final String IAT_NW_COUNTRY_MAPP_ID_TYPE = "IATNWCNT";
	public static final String IAT_COUNTRY_ID_TYPE = "IATCNT";
	public static final String TXN_STATUS_FAIL = "206";

	public static final String SERVICE_TYPE_IAT = "IAT";
	public static final String IAT_REQUEST_GATEWAY_TYPE = "EXTGW";
	public static final String IAT_SOURCE_TYPE = "WEB";
	public static final String IAT_CHECK_STATUS = "Y";

	public static final String INTERFACE_CATEGORY_IAT = "IAT";

	// Added for bonus bundle mapping
	public static final String BONUS_BUNDLE_LIST = "BONUS_BUNDLE_LIST";

	// VIKRAM last 3 transfer service added for Last Transfer Report(c2s)
	public static final String SERVICE_TYPE_LASTX_TRANSFER_REPORT = "LASTXTRF";
	public static final String SERVICE_TYPE_C2S_LAST_X_TRANSFER = "C2S";
	public static final String SERVICE_TYPE_C2C_LAST_X_TRANSFER = "C2C";
	public static final String SERVICE_TYPE_O2C_LAST_X_TRANSFER = "O2C";

	// added for Customer enquiry(c2s)
	public static final String SERVICE_TYPE_CUSTX_ENQUIRY = "CUSTXTRF";
	// vikram for c2s recharge
	public static final String MULTIPLE_ENTRY_ALLOWED = "M";
	public static final String NETWORK_STOCK_TRANSACTION_DEDUCTION = "DEDUCT";
	public static final String STOCK_TXN_TYPE = "DEDUCT";
	// rev
	public static final String TRANSFER_TYPE_O2C_LOOKUP_TYPE = "OTRFT";
	public static final String TRANSFER_TYPE_REVERSE_LOOKUP_TYPE = "TRXT";
	public static final String TRANSFER_TYPE_REVERSE_ID_TYPE = "CX";
	public static final String TRANSFER_TYPE_REVERSE_EVENT_TYPE = "C2C_REVERSE_TRN";
	public static final String TRANSFER_TYPE_REVERSE_SUB_TYPE = "X";
	// reverse c2s txn
	public static final String TRANSFER_TYPE_C2S_REVERSE_EVENT_TYPE = "C2S_REVERSE_TRN";
	public static final String C2S_REVERSE_BALANCE_LOGER_TYPE = "REV_CR";
	public static final String C2S_TYPE_REVERSE_TRANSFER_LOOKUP_TYPE = "RC2S";

	// added by Amit Raheja for reverse txn
	public static final String TRANSFER_TYPE_O2C_REVERSE_ID_TYPE = "OX";

	public static final String LOOKUP_CSADMIN_USER_TYPE = "ADMUT";
	public static final String LOOKUP_OPT_USR_TYPE = "CSADM";
	public static final String CSADMIN_OPERATOR_USER = "OPTU";

	// vikram in bar unbar module
	public static final String BAR_TYPE_PARENT_BARRED = "BRPRT";
	public static final String BAR_REASON_PARENT_BARRED = "Parent is barred";

	public static final String NOT_AVAILABLE = "NA";
	public static final String NOT_AVAILABLE_DESC = "NOT AVAILABLE";
	public static final String ROLE_FOR_BOTH = "B";
	public static final String ROLE_FOR_STAFF = "S";
	public static final String LAST_TRANSACTION_O2C_TYPE = "O2C";
	public static final String CHANNEL_ENQUIRY = "ENQ";

	// added for multiple wallet feature
	public static final String MULTIPLE_WALLET_TYPE = "WLTYP";
	public static final String SALE_WALLET_TYPE = "SAL";
	public static final String FOC_WALLET_TYPE = "FOC";
	public static final String INCENTIVE_WALLET_TYPE = "INC";
	// Bonus bundle management
	public static final String BONUS_BUNDLE_TYPE = "BUNTP";

	public static final String GATEWAY_TYPE_PHYSICAL_POS = "EXPHPOS";
	public static final String GATEWAY_TYPE_SMS_POS = "SMSPOS";

	// lohit
	public static final String DP_BATCH_PROCESS_ID = "DPBATCH";
	public static final String DP_BATCH_TRANSACTION_ID = "DP";
	public static final String CHANNEL_TRANSFER_BATCH_DP_STATUS_LOOKUP_TYPE = "BTSTA";
	public static final String CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN = "OPEN";
	public static final String CHANNEL_TRANSFER_BATCH_DP_STATUS_CLOSE = "CLOSE";
	public static final String CHANNEL_TRANSFER_BATCH_DP_STATUS_CANCEL = "CANCEL";
	public static final String CHANNEL_TRANSFER_BATCH_DP_STATUS_UNDERPROCESS = "UNDPROCESS";
	public static final String CHANNEL_TRANSFER_BATCH_DP_ITEM_RCRDSTATUS_PROCESSED = "P";
	public static final String CHANNEL_TRANSFER_BATCH_DP_ITEM_RCRDSTATUS_SCHEDULED = "S";
	public static final String TRANSFER_TYPE_DP = "FOC";
	public static final String DP_ORDER_APPROVAL_LVL = "DP_ODR_APPROVAL_LVL";
	// for lookups entry of bulk commission payout
	public static final String BULK_COMM_STATUS = "DPSTA";
	public static final String FOC_TYPE = "DP";
	public static final String BONUS_TYPE = "BCTP";
	// added by vikram
	public static final String LOOKUP_CHANNEL_TRANSFER_TYPE_WITHDRAW = "Withdraw";

	// added by chetan for LDCC handling
	public static final String SUBSCRIBER_TYPE_POST = "POST";
	public static final String SERVICE_CLASS_LDCC = "LDCC";
	public static final String MONITOR_SERVER_USER_VIEW = "MONITOR_VW";
	public static final String MONITOR_SERVER_USER_ADMIN = "MONITOR_AD";
	public static final String USSD_UNDERPROCESS = "UNDERPROCESS";
	public static final String FALSE = "FALSE";
	public static final String TRUE = "TRUE";

	// AFTER 5.5 BY VIKRAM
	public static final String THRESHOLD_COUNTER_TYPE = "THRTP";
	public static final String ABOVE_THRESHOLD_TYPE = "AT";
	public static final String BELOW_THRESHOLD_TYPE = "BT";

	// For Bulk voucher download
	public static final String SALE_BATCH_NUMBER = "SBM";

	public static final String P2P_CREDIT_RECHARGE = "CDPCR";

	public static final String IS_DEFAULT = "DEF";
	public static final String DEFAULT_YES = "YES";
	public static final String STK_SYSTEM_USR_CREATION_TYPE = "S";

	// added by nilesh : for user deletion
	public static final String SERVICE_TYPE_USER_DELETION = "USRDEL";
	// added by nilesh : for Auto c2c
	public static final String THRESHOLD_TYPE_MIN = "MIN";
	public static final String THRESHOLD_TYPE_ALERT = "ALERT";
	public static final String REQUEST_SOURCE_SYSTEM = "SYSTEM";

	// Chnages done by ashishT for MVD voucher download.
	public static final String MVD_VOUCHER_DOWNLOAD = "MVD";
	// added for Post Payment EL service type
	public static final String SERVICE_TYPE_CHNL_PAYMENTEL = "PPEL";
	// added by nilesh : for Auto c2c
	public static final String AUTO_C2C_TXN_MODE = "A";
	public static final String SERVICE_TYPE_CHNL_TRANS_ENQ = "RCE";// added by
																	// rahul for
																	// c2s
																	// transaction
																	// enquiry
	// added by priyanka 21/01/11 for mobilecom
	public static final String CRYSTAL_SUMMARY_REPORTS = "CRPTSUMM";
	// addded by jasmine
	public static final String SERVICESELECTOR_TYPE = "OPSRV";

	// ADDED FOR USER CREATION FROM USSD
	public static final String ADD_CHNL_USER = "ADDCHUSR";
	public static final String REQUEST_SOURCE_TYPE_USSD = "USSD";
	public static final String STK_USER_CREATION_TYPE = "S";
	public static final String USSD_USER_CREATION_TYPE = "U";
	public static final String EXT_USER_CREATION_TYPE = "E";

	// added by anu for new o2c history report
	public static final String O2C_TRANSFER = "O2CTR";
	public static final String O2C_TRANSFER_CATEGORY = "OCHIS";

	public static final String ENQ_TXNID_DATE_EXTNO = "ETXNENQREQ";
	public static final int TXNID_COUNT = 100;
	public static final String ENQ_TXNID_NVL = "NULL";

	public static final String ENQ_TXN_ID = "TXENQREQ";
	// New keyword for credit transfer for multiple user's dedicated account
	// public String MULT_CRE_TRA_DED_ACC="MCTDA";
	// private Recharge
	public static final String TXN_STATUS_SUCCESS_MESSAGE = "Success Transaction";
	/*
	 * added By Babu Kunwar for providing Remarks for
	 * Deleting/Suspending/Resuming/Barring/UnBarring Channel User Added on 16th
	 * Feb 2011 fro Pretups 5.5.3
	 */
	public static final String USER_EVENT_REPORTS = "ERPT";
	public static final String DELETE_REQUEST_EVENT = "DELETE_REQ";
	public static final String SUSPEND_REQUEST_EVENT = "SUSPND_REQ";
	public static final String DELETE_EVENT_APPROVAL = "DEL_APRV";
	public static final String SUSPEND_EVENT_APPROVAL = "SUSPND";
	public static final String RESUME_EVENT_REMARKS = "RESUME";
	public static final String BARRING_USER_REMARKS = "BAR";
	public static final String UNBARRING_USER_REMARKS = "UNBAR";
	public static final String CHANGE_PIN = "CHNG_PIN";
	public static final String CHUSER_WITHDRAW = "WITHDRAWAL";
	public static final String CHUSER_RETURN = "RETURN";
	public static final String PASSWD_RESEND = "SEND_PSWD";
	public static final String PASSWD_RESET = "RESET_PSWD";
	public static final String PIN_RESET = "RESET_PIN";
	public static final String PIN_RESEND = "PIN_RESEND";
	public static final String VOUCHER_PIN_RESEND = "VOUCHER_PIN_RESEND";

	// Added for Private recharge.
	public static final String SID_AUTO_FILTER = "AUTO";
	public static final String SID_MANUAL_FILTER = "MANUAL";

	public static final String RECEIVER_MOBILE_NO = "MSISDN";
	public static final String RECEIVER_SID = "SID";
	public static final String SERVICE_TYPE_SID_ENQUIRY = "ENQSID";
	public static final int MESSAGE_LENGTH_SID_ENQUIRY = 2;
	public static final int MESSAGE_LENGTH_SID_ENQUIRYSMS = 1;
	public static final String SERVICE_TYPE_SID_DELETE = "DELSID";
	public static final int MESSAGE_LENGTH_SID_DELETE = 3;
	public static final int MESSAGE_LENGTH_SID_DELETESMS = 2;
	public static final boolean IS_SID_NUMERIC = true;
	public static final int SID_DELETION_MSG_LENGTH = 4;
	// public String TXN_STATUS_SUCCESS_MESSAGE="Success Transaction";
	public static final String TXN_STATUS_FAILURE_MESSAGE = "Failed Transaction";
	// For CRBT Registration and CRBT Song Selection
	public static final String SERVICE_TYPE_CRBTREGISTRATION = "CRBTRG";
	public static final String SERVICE_TYPE_CRBT_SONGSEL = "CRBTSGSEL";
	// Added for Corporate IAT Recharge
	public static final String RESTRICTED_TYPE = "IAT";
	public static final String DEFAULT_RESTRICTED_TYPE = "NOTIAT";
	public static final String APPROVE_IAT_LIST = "APPROVE";
	public static final String REJECT_IAT_LIST = "REJECT";
	public static final String DISCARD_IAT_LIST = "DELETE";
	// New keyword for credit transfer for multiple user's dedicated account
	public static final String MULT_CRE_TRA_DED_ACC = "PRCMDA";
	public static final String SUBSCRIBER_TYPE_LIST = "SCTYP"; // LOOKUPS
	public static final String SERVICE_SELECTOR_ID = "SER_SELID"; // IDS
	public static final String SERVICE_TYPE_SELECTOR_MAPPING_TYPE = "SSER";

	// Added for Alert_Type
	public static final String ALERT_TYPE_SELF = "S";
	public static final String ALERT_TYPE_PARENT = "P";
	public static final String ALERT_TYPE_OTHER = "O";

	// Added for EVR
	public static final String SERVICE_TYPE_CHNL_EVR = "EVR";

	// added by nilesh:for O2C and C2C transfer rule
	public static final String CHNL_TRANSFER_RULE_STATUS_NEW = "W";
	public static final String CHNL_TRANSFER_RULE_STATUS_SUSPEND_REQUEST = "P";
	public static final String CHNL_TRANSFER_RULE_STATUS_RESUME_REQUEST = "Q";
	public static final String TRANS_TYPE = "TFTYP";
	public static final String CHNL_TRANSFER_RULE_STATUS_MODIFY_REQUEST = "R";
	// public static final String CHNL_TRANSFER_TYPE_TRF_WITD =
	// "Transfer/Withdraw";
	// public static final String CHNL_TRANSFER_TYPE_RET = "Return";
	public static final String CHNL_TRANSFER_TYPE_TRF_WITD = "TRWD";
	public static final String CHNL_TRANSFER_TYPE_RET = "RET";

	public static final String C2C_RULE_TYPE = "RUTYP";
	public static final String ACTIVE = "ACTIVE";
	public static final String NEW = "NEW";
	public static final String SUSPENDED = "SUSPENDED";
	public static final String SUSPEND_REQUEST = "SUSPENDREQUEST";
	public static final String RESUME_REQUEST = "RESUMEREQUEST";
	public static final String MODIFY_REQUEST = "MODIFYREQUEST";

	public static final String O2C_BATCH_TRANSACTION_ID = "OB";
	public static final String O2C_BATCH_PROCESS_ID = "O2CBATCH";
	public static final String CT_BATCH_O2C_STATUS_UNDERPROCESS = "UNDPROCESS";
	public static final String SERVICE_TYPE_USR_SUSPEND_RESUME = "SUSRESUSR";

	// Added for Bulk Resume feature in Mobinil
	public static final String USER_STATUS_RESUMED = "Y";
	public static final String USER_STATUS_RESUME_REQUEST = "RR";

	// Added for C2C/C2S transaction specific dial
	public static final String TRANSACTION_TYPE_C2C = "C2C";
	public static final String TRANSACTION_TYPE_C2S = "C2S";

	public static final String CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED = "P";
	public static final String CT_BATCH_O2C_STATUS_OPEN = "OPEN";
	public static final String O2C_ORDER_APPROVAL_LVL = "O2C_ODR_APPROVAL_LVL";

	public static final String CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN = "OPEN";
	public static final String CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE = "CLOSE";
	public static final String CHANNEL_TRANSFER_BATCH_O2C_STATUS_CANCEL = "CANCEL";

	public static final String O2C_WALLET_TYPE = "O2C";
	// added by nilesh
	public static final String DELETE_TRANSFER_RULE_REQ_STATUS = "D";
	public static final String DELETE_TRANSFER_RULE_APPRV = "Delete Request";

	public static final String SERVICE_TYPE_POSTPAID_BILL_DEPOSIT = "PPD";
	public static final String TRANSFER_CATEGORY_WITHDRAW = "TRWD";

	// added for Channel User Dr/Cr Service API
	public static final String TRANSACTION_MODE_DRCR_TRANSFER = "C";
	public static final String SERVICE_TYPE_CHNL_DRCR_TRANSFER = "DRCRTRF";
	public static final String DRCR_CHANNEL_USER_ID = "CM";
	public static final String AUTO_FOC_ALLOW = "Y";
	public static final String AUTO_FOC_TXN_MODE = "A";
	public static final String AUTO_FOC_WALLET = "FOC";
	// for DB2
	public static final String DATABASE_TYPE_DB2 = "DB2";
	public static final String P2P_MCD_LIST_SERVICE_TYPE = "MCDL";
	public static final String P2P_MCD_LIST_DELETE = "MCDLD";
	public static final String P2P_MCD_LIST_ACTION_ADD = "A";
	public static final String P2P_MCD_LIST_ACTION_MODIFY = "M";
	public static final String P2P_MCD_LIST_ACTION_DELETE = "D";
	public static final String P2P_MCD_LIST_VIEW = "MCDLV";
	public static final String P2P_MCD_LIST_REQUEST = "MCDLR";

	public static final String XLS_PINPASSWARD = "****";

	// Give Me Balance
	public static final String SERVICE_TYPE_GIVE_ME_BALANCE = "CGMBALREQ";
	public static final String SERVICE_TYPE_BAR_GIVE_ME_BALANCE = "GMBBAR";
	public static final String FLARES_CONTENT_TYPE = "application/x-www-form-urlencoded";

	// LendMeBalance
	public static final String SERVICE_TYPE_LEND_ME_BALANCE = "LMB";

	// added by Puneet for user creation through EXT system
	public static final String SERVICE_TYPE_CHNN_USER_REGISTRATION = "USRREGREQ";
	public static final String SERVICE_TYPE_CHNN_USER_MODIFICATION = "USRMODREQ";
	public static final String SERVICE_TYPE_CHNL_USER_DELETE_REQ = "USRDELREQ";
	public static final String SERVICE_TYPE_CHNL_USER_DELETE_RES_SUS = "USRACTREQ";
	public static final String EXTERNAL_SYSTEM_USR_CREATION_TYPE = "E";
	public static final String PARENT_USER_ID = "ROOT";
	public static final String SUCCESSIVE_TXN_INTERVAL = "S_TXN_INTRVAL";
	public static final String LOW_BALANCE_ALERTING = "LOW_BAL_ALERT";
	public static final String COUNT_OR_VALUE_ALERT = "MAXCOUNT";
	// Addition Ends

	public static final String SERVICE_TYPE_VOUCHER_TYPE= "C2CVTYPE";
	public static final String SERVICE_TYPE_VOUCHER_SEGMENT= "C2CVSEG";
	public static final String SERVICE_TYPE_VOUCHER_DENOMINATION= "C2CVDEN";
	public static final String SERVICE_TYPE_VOUCHER_INFO= "C2CVINFO";

	
	// added for roam commission
	public static final String ROAM_COMM_VALUE = "100";

	public static final String AON_TAG = "AON";
	// for LMB reports
	public static final String TRNX_TYPE = "TXTYP";
	public static final String LANGUAGE_CODE = "LANGC";
	public static final String LMB_VAL_NT_EXPIRED = "N";
	public static final String LMB_VAL_EXPIRED = "Y";

	// ADDED BY HITESH
	// for user profile threshold enquiry
	public static final String USER_PRODUCT_LIST = "USER_PRODUCT_LIST";
	public static final String TRANSFER_PROFILE_VO = "TRANSFER_PROFILE_VO";
	public static final String USER_TRF_COUNT_VO = "USER_TRF_COUNT_VO";
	public static final String SUB_OUT_COUNT_FLAG = "SUB_OUT_COUNT_FLAG";
	// for bulk voucher
	// CHANGE FINISHED
	// batch add/modify commission profile by gaurav pandey
	public static final String COMM_PROFILE_STATUS = "N";
	public static final String BATCH_MODIFY_COMM_PROFILE_DOMAIN = "DOMAIN_NAME";
	public static final String BATCH_MODIFY_COMM_PROFILE_CATEGORY = "CATEGORY_NAME";
	public static final String BATCH_MODIFY_COMM_PROFILE_PRODUCT_LIST = "PRODUCT_LIST";
	public static final String BATCH_MODIFY_COMM_PROFILE_PAYMENT_MODE = "PAYMENT_MODE";
	public static final String BATCH_MODIFY_COMM_PROFILE_TRANSACTION_TYPE = "TRANSACTION_TYPE";
	public static final String BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE = "DOMAIN_CODE";
	public static final String BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE = "CATEGORY_CODE";
	public static final String BATCH_MODIFY_COMM_PROFILE_SET_NAME = "SET_NAME";
	public static final String BATCH_MODIFY_COMM_PROFILE_SET_VERSION = "SET_VERSION";
	public static final String BATCH_MODIFY_COMM_PROFILE_NETWORK_NAME = "NETWORK_NAME";
	public static final String BATCH_MODIFY_COMM_PROFILE_ADDITIONAL_COMMISSION = "ADDITIONAL_COMMISSION";
	public static final String BATCH_MODIFY_COMM_PROFILE_ADDL_COMM_OTF = "OTF_DETAILS";
	public static final String BATCH_MODIFY_COMM_PROFILE_PROCESS_ID = "BATCHMODCOMMPRF";
	public static final String BATCH_COMM_CREATED_BY = "DOWNLOADED_BY";
	public static final String BATCH_COMM_CATEGORY_LIST = "CATEGORY_LIST";
	public static final String BATCH_COMM_DOMAIN_LIST = "DOMAIN_LIST";
	public static final String BATCH_COMM_SERVICE_LIST = "SERVICE_LIST";
	public static final String BATCH_COMM_VERSION = "1";
	public static final String DOWNLOADED_BY = "DOWNLOADED";
	public static final String BATCH_COMM_PROFILE_PREFIX = "BCOM";
	public static final String COMM_PROFILE_BATCH_ID = "BATCH_ID";
	public static final String BATCH_COMM_PROFILE_TYPE = "BATCH_COMM_PROF_ADD";
	public static final String BATCH_COMM_PROFILE_STATUS = "C";
	public static final String BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE = "NETWORK_CODE";
	public static final String CHANNEL_TRANSFER_O2C_VOMS_ID = "OV";
	public static final String VOUCHER_PRODUCT_O2C = "VOUCHTRACK";

	// added for Multiple Credit List CR
	/*
	 * public static final String P2P_MCD_LIST_SERVICE_TYPE="MCDL"; public
	 * static final String P2P_MCD_LIST_DELETE="MCDLD"; public static final
	 * String P2P_MCD_LIST_ACTION_ADD="A"; public static final String
	 * P2P_MCD_LIST_ACTION_MODIFY="M"; public static final String
	 * P2P_MCD_LIST_ACTION_DELETE="D"; public static static final final String
	 * P2P_MCD_LIST_VIEW="MCDLV"; public static final String
	 * P2P_MCD_LIST_REQUEST="MCDLR";
	 */

	// added by gaurav for cos management and cellid management

	public static final String CELL_GROUP_LIST = "CELLGRPLST";
	public static final String CELL_GROUP_ASSOCIATION_PROCESS_ID = "CELLGRPPRO";
	public static final String CELL_ID_MAPPING_ADD = "ADD";
	public static final String CELL_ID_MAPPING_MODIFY = "MODIFY";
	public static final String CELL_ID_MAPPING_DELETE = "DELETE";
	public static final String CELL_ID_MAPPING_MODSUSPEND = "MODSTATUS";
	public static final String CELL_ID_MODIFY_STATUS = "STATUSMODIFY";
	public static final String CELL_ID_REASSOCIATE_CELLGRPID = "REASSOGRPID";
	public static final String CELL_ID_STATUS_ACTIVE = "ACTIVE";
	public static final String CELL_ID_STATUS_SUSPEND = "SUSPEND";
	public static final String CELL_ID_STATUS_A = "A";
	public static final String CELL_ID_STATUS_S = "S";
	public static final String CELL_ID_STATUS_D = "D";
	public static final String CELL_ID_STATUS_R = "R";

	public static final String CELL_GROUP_TYPE_ID = "CLGRP";
	public static final String CELL_ACTIVE_STATUS = "ACTIVE";
	public static final String CELL_SUSPEND_STATUS = "SUSPEND";

	// COS Management
	public static final String COS_STATUS_ACTIVE = "A";
	public static final String COS_STATUS_SUSPEND = "S";
	public static final String COS_STATUS_DELETE = "D";
	public static final String CELL_ID_VO_LIST = "CELLVODETAILSLST";
	public static final String CELL_ID_REASSOCIATION_PROCESS_ID = "CIDREASSON";
	public static final String COS_DEFINE_FILE_PROCESS = "COSDEFINE";
	public static final String COS_MANAGE_FILE_PROCESS = "COSMANAGE";

	// added for lmb debit api
	public static final String SERVICE_TYPE_LMBDEBIT = "LMBDBT";
	public static final int LMB_DBT_MESSAGE_LENGTH = 4;

	// added by deepika aggarwal
	public static final String BATCH_USR_LANGUAGE_LIST = "USER_LANG_LIST";
	public static final String C2S_LASTX_TRANSFER_REPORT = "C2SLASXTRF";
	// Added for CP Registration
	public static final String OPT_MODULE = "OPT";
	public static final String OPERATOR_SUBCHNL_ADMIN = "TSM";

	// VASTRIX CHANGES start...
	public static final String C2S_REC_GEN_FAIL_MSG_REQD_V_VAS = "C2S_REC_GEN_FAIL_MSG_REQD_V_VAS";
	public static final String C2S_REC_GEN_FAIL_MSG_REQD_T_VAS = "C2S_REC_GEN_FAIL_MSG_REQD_T_VAS";
	// Gift VAS
	public static final String C2S_REC_GEN_FAIL_MSG_REQD_V_GIFTVAS = "C2S_REC_GEN_FAIL_MSG_REQD_V_GIFTVAS";
	public static final String C2S_REC_GEN_FAIL_MSG_REQD_T_GIFTVAS = "C2S_REC_GEN_FAIL_MSG_REQD_T_GIFTVAS";
	public static final String VAS_REQUEST_TYPE_ADD = "add";
	public static final String VAS_REQUEST_TYPE_MODIFY = "modify";
	public static final String VAS_REQUEST_TYPE_DELETE = "delete";
	public static final String VAS_REQUEST_TYPE_APPROVE = "approve";
	public static final String VAS_REQUEST_TYPE_VIEW = "view";
	public static final String VAS_CATEGORY_STATUS_ACTIVE = "Y";
	public static final String VAS_ITEM_STATUS_ACTIVE = "Y";
	public static final String VAS_ITEM_STATUS_NEW = "W";
	public static final String VAS_ITEM_STATUS_DELETE = "N";
	public static final String VAS_STATUS_ACTIVE = "Y";
	public static final String VAS_TYPE_CATEGORY = "CAT";
	public static final String VAS_TYPE_ITEM = "ITEM";
	public static final String VAS_SINGLE_ITEM_INDEX = "S";
	public static final String VAS_BATCH_ITEMS_INDEX = "B";
	public static final String VAS_ALL_ITEMS_INDEX = "ALL";
	public static final String VAS_NETWORK_ALL = "ALL";
	public static final String BATCH_VAS_MODIFY_PROCESS_ID = "VAS_MODIFY";
	public static final String BATCH_VAS_DELETE_PROCESS_ID = "VAS_DELETE";
	public static final String BATCH_VAS_ADD_PROCESS_ID = "VAS_ADD";

	public static final String VAS_ALL_ITEM_INDEX = "I";
	public static final String VAS_VIEW_ACTION_SINGLE = "SINGLE";
	public static final String VAS_VIEW_ACTION_ALL = "ALL";
	public static final String VAS_VIEW_ACTION_BULK = "BULK";
	public static final String VAS_BOTH_ITEM_STATUS_INDEX = "A";
	public static final String VAS_ACTIVE_ITEM_STATUS_INDEX = "Y";
	public static final String VAS_NEW_ITEM_STATUS_INDEX = "W";
	public static final String VAS_ROOT_PARENT_ID = "ROOT";
	public static final String VAS_C2S_MODULE = "C2S";
	public static final String INTERFACE_CATEGORY_VAS_PT = "VAS-PT";
	public static final String INTERFACE_CATEGORY_VAS_HT = "VAS-HT";
	public static final String SERVICE_ACTIVE_STATUS = "ACTIVE";
	public static final String SERVICE_SUSPEND_STATUS = "SUSPEND";
	public static final String SERVICE_ID_MAPPING_ADD = "ADD";
	public static final String SERVICE_ID_MAPPING_MODIFY = "MODIFY";
	public static final String SERVICE_ID_MAPPING_DELETE = "DELETE";
	public static final String SERVICE_GROUP_TYPE_ID = "SVGRP";
	public static final String SERVICE_GROUP_ASSOCIATION_PROCESS_ID = "SERVGRPPRO";
	public static final String SERVICE_GROUP_LIST = "SERVGRPLST";
	public static final String SERVICE_ID_VO_LIST = "SERVODETAILSLST";
	public static final String SERVICE_ID_REASSOCIATION_PROCESS_ID = "SIDREASSON";
	public static final String SERVICE_ID_STATUS_SUSPEND = "SUSPEND";
	public static final String SERVICE_ID_STATUS_A = "A";
	public static final String SERVICE_ID_STATUS_S = "S";
	public static final String SERVICE_ID_STATUS_D = "D";
	public static final String SERVICE_ID_STATUS_R = "R";
	public static final String SERVICE_ID_STATUS_ACTIVE = "ACTIVE";
	public static final String SERVICE_ID_MODIFY_STATUS = "STATUSMODIFY";
	public static final String SERVICE_ID_REASSOCIATE_SERVICEGRPID = "REASSOGRPID";
	public static final String SERVICE_INTERFACE_MAPPING_ID_TYPE = "SIM";// arvinder
	public static final String VAS_CATEGORY_TYPE = "CAT";
	public static final String VAS_ITEM_TYPE = "ITEM";
	public static final String VAS_TYPE_VCC = "VCC";
	public static final String VAS_TYPE_HT = "HT";
	public static final String VAS_TYPE_PT = "PT";
	public static final String ID_GEN_VAS_TRANSFER_NO = "VAS";
	public static final String INTERFACE_CATEGORY_VAS = "VAS";
	public static final String VAS_VSTP = "VSTP";
	public static final String VAS_VSSTP = "VSSTP";
	public static final String VAS_VSSSTP = "VSSSTP";
	public static final String VAS_CATEGORY_PREFIX_ID = "VC";
	public static final String VAS_ITEM_PREFIX_ID = "ITM";
	public static final String SERVICE_TYPE_VAS_RECHARGE = "VAS";
	public static final String SERVICE_TYPE_PVAS_RECHARGE = "PVAS";
	public static final String SERVICE_TYPE_INTL_RECHARGE = "INTL";
	// /// VASTRIX CHANGES end...

	// Entries for DMS
	// Added by Ankur for update cache for DMS user API
	public static final String EXTERNAL_USR_CREATION_TYPE = "E";
	public static final String SERVICE_TYPE_EXT_GEOGRAPHY = "EXTGRPH";
	public static final String GEOGRAPHY_LIST = "GEOLIST";
	public static final String SUB_AREA_TYPE = "SA";
	public static final String PARENT_GEOGRAPHY_ROOT = "ROOT";
	public static final String SERVICE_TYPE_EXT_USR_ADD = "EXTUSRADD";
	public static final String USER_MAP = "USERMAP";
	public static final String SUB_OUTLET_DEFAULT = "SL019";
	public static final String UNAME_PREFIX_DEFAULT = "CMPY";
	public static final String SERVICE_TYPE_TRF_RULE_TYPE = "TRFRULETYP";
	public static final String RULETYPEDETAILS_STR = "RULETYPEDETAILS";
	// Added for User default Config management
	public static final String USER_DEFAULT_CONFIG_PROCESS_ID = "USRDEFCONFPROID";
	public static final String USR_DEF_CONFIG_CREATED_BY = "CREATED_BY";
	public static final String USR_DEF_CONFIG_DOMAIN_NAME = "DOMAIN_NAME";
	public static final String USR_DEF_CONFIG_DOMAIN_CODE = "DOMAIN_CODE";
	public static final String USR_DEF_CONFIG_CATEGORY_LIST = "CATEGORY_LIST";
	public static final String USR_DEF_CONFIG_GROUP_ROLE_LIST = "GROUP_ROLE_LIST";
	public static final String USR_DEF_CONFIG_GRADE_LIST = "GRADE_LIST";
	public static final String USR_DEF_CONFIG_TRANSFER_CONTROL_PRF_LIST = "TRANSFER_CONTROL_PRF_LIST";
	public static final String USR_DEF_CONFIG_COMMISION_PRF_LIST = "COMMISION_PRF_LIST";
	public static final String USR_DEF_CONFIG_EXCEL_DATA = "USR_DEF_CONFIG_EXCEL_DATA";
	public static final String USR_DEF_CONFIG_ADD = "A";
	public static final String USR_DEF_CONFIG_MODIFY = "M";
	public static final String USR_DEF_CONFIG_DELETE = "D";
	public static final String USR_DEF_CONFIG_NOCHANGE = "N";
	public static final String USR_CACHE_GEOCODE_SUFFIX = "_GRDCODE";
	public static final String USR_CACHE_TRFPRF_SUFFIX = "_TRFPRF";
	public static final String USR_CACHE_COMPRF_SUFFIX = "_COMPRF";
	public static final String USR_CACHE_ROLECODE_SUFFIX = "_ROLECODE";
	public static final String USR_CACHE_GRDCODE_SUFFIX = "_GRDCODE";
	public static final String TRANSFER_RULE_AT_USER_LEVEL = "TRFRU";// added on
																		// 23/oct/2012
	public static final String SERVICE_TYPE_USER_AUTH = "USRAUTH";// on
																	// 29/nov/2012
	// / end of DMS Entries

	// Added for trf by shashank
	public static final String BATCH_USR_TRF_RULE_LIST = "TRANSFER_RULE_LIST";

	// / added for promotional transfer rule
	public static final String PROMOTIONAL_LEVEL_CELLGROUP = "CEL";
	// For Service Group
	public static final String PROMOTIONAL_LEVEL_SERVICEGROUP = "SRV";
	public static final String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CEL = "CEL";
	public static final String TRANSFER_RULE_SUBSCRIBER_STATUS = "SUBSTATUS";
	public static final String PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE = "SRV";

	public static final String BATCH_COMM_SUBSERVICE_LIST = "BATCH_COMM_SUBSERVICE_LIST";

	// added by shashank for channel user authentication 17/JAN/2013
	public static final String USER_AUTH_XML_REQ = "AUTHCUSER";
	public static final String USER_AUTH_XML_RESP = "AUTHCUSERRESP";

	public static final String DEFAULT_SUBSERVICE = "DEF";
	public static final String LOOKUP_TYPE_SUBSCRIBER_STATUS = "SUBST";

	public static final int PROMO_TRF_RULE_LVL_CELLGRP_CODE = 6;
	public static final int PROMO_TRF_RULE_LVL_SPNAME_CODE = 7;
	public static final String SERVICE_TYPE_SIM_ACT_REQ = "SIMACTREQ";
	public static final String SIM_ACTIVATE_RESP = "SIMACTRES";
	public static final String PROMO_BALANCE_PREFIX = "PROMO";
	// added by Sonali Garg
	// to validate subscriber at IN
	// public String EXTERNAL_SYSTEM_SUBSCRIBER_ENQUIRY="EXTSYSENQ";
	public static final String SERVICE_TYPE_SUBSCRIBER_ENQUIRY = "SUBENQ";

	// Batcg Grade association
	public static final String BATCH_USER_GRADE_LIST = "SERVGRPLST";
	public static final String BATCH_USER_GRADE_VO_LIST = "SERVODETAILSLST";
	public static final String BATCH_USER_GRADE_ASSOCIATION_USERGRDASSCID = "USRGDASCID";
	// public String TRANSFER_RULE_SUBSCRIBER_STATUS = "SUBSTATUS";
	public static final String ERROR_LINE = "-line";

	// for auto o2c
	public static final String AUTO_O2C_ALLOW = "Y";
	public static final String PRODUCT_TYPE_AUTO_O2C = "PREPROD";
	public static final String AUTO_O2C_PROCESS = "AUTOO2CPROCESS";

	public static final String AUTO_O2C_ORDER_NEW = "NEW";

	public static final String AUTO_O2C_ORDER_APPROVE1 = "AP1";
	public static final String AUTO_O2C_ORDER_APPROVE2 = "AP2";
	public static final String AUTO_O2C_ORDER_APPROVE3 = "AP3";
	public static final String AUTO_O2C_ORDER_REJECTED = "CNCL";

	// added by shashank for barred for deletion 12/MAR/2013
	public static final String USER_STATUS_BAR_FOR_DEL_REQUEST = "BR";
	public static final String USER_STATUS_BARRED = "BD";
	public static final String USER_STATUS_BAR_FOR_DEL_APPROVE = "BA";
	public static final String BARRED_REQUEST_EVENT = "BAR_REQ";

	// added by shashank for batch bar for deletion 17/APRIL/2013
	public static final String USER_STATUS_BCH_BAR_FOR_DEL_REQUEST = "OPEN";
	public static final String USER_STATUS_BCH_BAR_FOR_DEL_APPROVE1 = "APPR1";
	public static final String USER_STATUS_BCH_BAR_FOR_DEL_REJECT = "REJCT";
	public static final String USER_STATUS_BCH_BAR_FOR_DEL_BARRED = "CLOSE";
	// public String BATCH_BARRED_REQUEST_EVENT="BCH_BAR_REQ";

	public static final String USR_BATCH_BAR_STATUS_OPEN = "O";
	public static final String USR_BATCH_BAR_STATUS_CLOSE = "C";
	public static final String USR_BATCH_BAR_STATUS_REJECT = "R";
	public static final String USR_BATCH_BAR_STATUS_APPROVE1 = "A";
	public static final String USR_BATCH_BAR_STATUS_UNDERPROCESS = "U";

	public static final String BULK_USR_BAR_ID_PREFIX = "BD";
	public static final String BULK_USR_BAR_DETAIL_ID_PREFIX = "BBD";
	public static final String BATCH_BAR_FOR_DEL_TYPE = "BATCH_BAR_FOR_DEL";
	public static final String BATCH_USR_BAR_BATCH_ID = "BATCH_BAR_ID";

	public static final String DEFAULT_GATEWAY_FOR_BAR = "WEB";
	public static final String LMS_SERVICE = "LMSER";
	public static final String LMS_PROMOTION_TYPE = "LMPTY";
	public static final String LMS_PROMO_ID = "LMSPR";
	public static final String LMRWD = "LMRWD";

	public static final String LMS_REW_ID = "LMS_REW_ID";
	public static final String PAYER = "PAYER";
	public static final String PAYER_HIERARCHY = "PAYER_HIERARCHY";
	public static final String PAYER_REGISTERER = "PAYER_REGISTERER";
	public static final String PAYEE = "PAYEE";
	public static final String PAYEE_HIERARCHY = "PAYEE_HIERARCHY";
	public static final String PAYEE_REGISTERER = "PAYEE_REGISTERER";
	public static final String LMAPP = "LMAPP";
	public static final String RWD_RANGE = "RWD_RANGE";
	public static final String PR_ASSC = "PR_ASSC";
	public static final String VIEW_STATUS_TYP = "PRVST";
	// For Redemption
	public static final String LMS_REDEMP_TYPE = "LPRED";
	public static final String REDEMP_TYPE_OTHER = "OTHER";
	public static final String REDEMP_TYPE_STOCK = "STOCK";
	public static final String ID_GEN_LMS_TRANSFER_NO = "LMS";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAIL = "FAIL";
	public static final String LMSFOCO2C = "LMSFOCO2C";
	public static final String O2C_MODULE = "O2C";

	// added by harsh for Scheduled Credit List (Add/Modify/Delete) API
	public static final String P2P_SMCD_LIST_SERVICE_TYPE = "SMCDL";
	// added by harsh for P2P Batches
	public static final String P2P_BUDDYLIST_BATCH_ID = "P2PB";
	// added by Vikas Kumar for scheduled credit transfer service
	public static final String SERVICE_TYPE_SCH_CREDIT_TRANSFER = "SCPRC";

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
	public String SERVICE_TYPE_OPT_USER_ADD = "OPTUSERADD";
	public String SERVICE_TYPE_OPT_USER_MOD = "OPTUSERMOD";
	public String SERVICE_TYPE_OPT_USER_SRD = "OPTUSERSRD";

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
	public String DEFAULT_P2P_WEB_IMEI = "0000";
	// changes end
	// / added by gaurav

	public String SELF_TOPUP_SCHEDULED_CREDIT_TRANSFER = "SLFSCCRDTTRF";
	public String REQUEST_SOURCE_TYPE_STUGW = "PLAIN";

	// added by Vikas Singh for Auto topup
	public int MESSAGE_LENGTH_AUTO_TOPUP = 7;

	public String DEFAULT_PAYMENT_GATEWAY = "CITI";
	public String INTERFACE_CATEGORY_PG = "PG";
	public String GROUP = "GROUP";
	// added by Vikas Singh for prepaid reversal
	public String SERVICE_TYPE_C2S_PREPAID_REVERSAL = "RCREV";
	public String C2S_PREPAID_REVERSAL_SUB_SERV_TYPE = "2";

	// ///added by Ashutosh for batch user creation
	public String BATCH_USR_GEOG_LIST = "GEOGRAPHY CODE AND NAME LIST";
	public String BATCH_USR_COMM_LIST = "GEOGRAPHY CODE, GRADE AND COMMISSION PROFILE LIST";
	// added by shashank for batch commission profile
	public static final String BATCH_MODIFY_COMM_PROFILE_GEOGRAPHY = "GEOGRAPHY_NAME";
	public static final String BATCH_MODIFY_COMM_PROFILE_GRADE = "GRADE_NAME";
	public static final String BATCH_MODIFY_COMM_PROFILE_GEOGRAPHY_CODE = "GEOGRAPHY_CODE";
	public static final String BATCH_MODIFY_COMM_PROFILE_GRADE_CODE = "GRADE_CODE";
	public static final String BATCH_COMM_GEOGRAPHY_LIST = "GEOGRAPY_LIST";
	public static final String BATCH_COMM_GRADE_LIST = "GRADE_LIST";
	public static final String BATCH_COMM_GATEWAY_LIST = "GATEWAY_LIST";
	public static final String MOBILE_APP_GATEWAY = "MAPPGW";
	public String SERVICE_TYPE_CHNL_ROAM_RECHARGE = "RRC";// for roaming
															// recharge
	// used in roaming In handler
	public String FERRARI_SERVICE_TYPE = "FRR";
	public String FERRARI_SERVICE_NAME = "Farrari Recharge";
	// Added by Brajesh
	// Done to set different Bucket Code For different Types of Allocation Types
	// in LMS
	public static final String BUCKET_CODE = "BTYP";
	public static final String BUCKET_CODE_TRANS = "LMSTXN";// set for
															// transaction Type,
															// Bucket_Code
															// Corresponding to
															// Transaction Type
															// in Lookups Table
															// is Picked up
	public static final String BUCKET_CODE_VOL = "LMSVOL";// set for volume
															// Type, Bucket_Code
															// Corresponding to
															// Volume Type in
															// Lookups Table is
															// Picked up
	public static final String BUCKET_CODE_ACTB = "LMACT";// set for Activation
															// Bonus,
															// Bucket_Code
															// Corresponding to
															// Activation Bonus
															// in
															// Lookups Table is
															// Picked up
	// /added by Ashutosh for voucher type management
	public String VOUCHER_STATE_ACTIVE = "Y";
	public String VOUCHER_STATUS_DELETE = "N";
	public static final String SERVICE_TYPE_CHNL_WARRANTY_RECHARGE = "WRC";
	public static final String SERVICE_TYPE_CHNL_ADVANCE_RECHARGE = "ADV";
	public static final String SERVICE_TYPE_CHNL_CAUTION_RECHARGE = "CAUT";
	public String ORANGE_ID_TYPE = "ORNGINID";

	// added by Akanksha for user life cycle
	public static final String ALLOWED_USER_STATUS = "USTAT";
	public static final String USER_STATUS_PREACTIVE = "PA";
	public static final String USER_STATUS_EXPIRED = "EX";
	public static final String USER_STATUS_DEACTIVATED = "DE";
	public static final String USER_STATUS_CHURN = "CH";
	// added for meditel UI
	public static final String LOOKUP_CHANNEL_USER_TYPE = "USRTY";
	public static final String EXTGW_GET_MY_NUMBER = "EXMSISDNREQ";

	public static final String BONUS_ACC = "BONUS";
	// associateDeassociatefiletemplateupdating mithlesh
	public static final String ASSODEASSO_BATCH_PROCESS_ID = "ASSDEASSBATCH";
	// Added by Brajesh
	public static final String SERVICE_TYPE_LMS_POINTS_ENQUIRY = "LMSPTENQ"; // added
																				// for
																				// Balance
																				// Enquiry
																				// 03/05/07
	public static final String GATEWAY_TYPE_EXTGW = "EXTGW";
	public static final String SERVICE_TYPE_LMS_POINTS_REDEMPTION = "LMSPTRED";
	// Added by Zeeshan Aleem
	public String SERVICE_TYPE_VOUCHER_CONSUMPTION = "VOMSCONSREQ";
	public static final String EXTGW_CHANNEL_USER_DETAILS = "EXUSERINFOREQ";
	public String SERVICE_TYPE_VCNO2C = "VCNO2C";
	public String SERVICE_TYPE_VOUCHER_CONSUMPTION_O2C = "VCO2CREQ";
	public static final String BONUS_BUNDLETYPE_STRING = "STR";
	public static final int MESSAGE_LENGTH_GET_MSISDN = 2;
	public static final int MESSAGE_LENGTH_USER_INFO = 2;

	// added by brajesh for LMS Enhancment
	public String LMS_MSG_SUBSTRING4 = "&Target&";
	public String LMS_MSG_SUBSTRING5 = "&period&";
	public String LMS_MSG_SUBSTRING6 = "&Current_Transaction&";
	public String LMS_MSG_SUBSTRING1 = "&Promotion_Name&";
	public String LMS_MSG_SUBSTRING2 = "&Start_Date&";
	public String LMS_MSG_SUBSTRING3 = "&End_Date&";
	public String LMS_MSG_SUBSTRING7 = "&Product_Code&";
	public String LMS_MSG_SUBSTRING8 = "&Service_Name&";
	public String WEL_MESSAGE = "2601";
	public String SUCCESS_MESSAGE = "2602";
	public String FAILURE_MESSAGE = "2603";
	public String WEL_MESSAGE_LANG1 = "2604";
	public String WEL_MESSAGE_LANG2 = "2607";
	public String SUCCESS_MESSAGE_LANG1 = "2605";
	public String SUCCESS_MESSAGE_LANG2 = "2608";
	public String FAILURE_MESSAGE_LANG1 = "2606";
	public String FAILURE_MESSAGE_LANG2 = "2609";
	public String TRA_WEL_MESSAGE = "2618";
	public String TRA_WEL_MSG_LANG1 = "2610";
	public String TRA_WEL_MSG_LANG2 = "2611";

	// for C2C transfer on the basis of Geography
	public String CHANNEL_TRANSFER_LEVEL_GEOGRAPHY = "GRP";

	// added by Fiza for Mobile App User Registration
	public String MAPP_REG_REQ = "USERREG";
	public String MAPP_LOGIN_REQ = "USRAUTH";// Login
	// LMS Related
	public String LMS_PROFILE_TYPE_CONTROLLED = "CNTRLP";
	// LMS Opt-In/Opt-Out Feature related services
	public String OPT_OUT_STATUS = "O";
	public String LMS_PROFILE_OPT_IN = "OPTIN";
	public String LMS_PROFILE_OPT_OUT = "OPTOUT";
	public String OPTINOUT_WEL_MESSAGE = "2612";
	public String OPTINOUT_WEL_MESSAGE_LANG1 = "2613";
	public String OPTINOUT_WEL_MESSAGE_LANG2 = "2614";
	public String OPTINOUT_TRA_WEL_MSG = "2615";
	public String OPTINOUT_TRA_WEL_MSG_LANG1 = "2616";
	public String OPTINOUT_TRA_WEL_MSG_LANG2 = "2617";
	public String OPT_STATUS_TYPE = "IOTYP";
	public String LMS_OPT_IN = "I";
	public String LMS_OPT_OUT = "O";
	public String LMS_OPT_ALL = "ALL";

	// LMS Point Adjustment
	public static final String LPT_ORDER_APPROVAL_LVL = "LPT_ODR_APPROVAL_LVL";
	public static final String LPT_BATCH_PROCESS_ID = "LPTBATCH";
	public static final String CHANNEL_TRANSFER_BATCH_LPT_STATUS_LOOKUP_TYPE = "BTSTA";
	public static final String CHANNEL_TRANSFER_BATCH_LPT_STATUS_OPEN = "OPEN";
	public static final String CHANNEL_TRANSFER_BATCH_LPT_STATUS_CLOSE = "CLOSE";
	public static final String CHANNEL_TRANSFER_BATCH_LPT_STATUS_CANCEL = "CANCEL";
	public static final String CHANNEL_TRANSFER_BATCH_LPT_STATUS_UNDERPROCESS = "UNDPROCESS";
	public static final String CHANNEL_TRANSFER_BATCH_LPT_ITEM_RCRDSTATUS_PROCESSED = "P";
	public static final String CHANNEL_TRANSFER_BATCH_LPT_ITEM_RCRDSTATUS_SCHEDULED = "S";
	public static final String LPT_BATCH_TRANSACTION_ID = "LPT";
	public static final String LPT_BATCH_ACTION_CREDIT = "LCR";
	public static final String LPT_BATCH_ACTION_DEBIT = "LDR";
	public static final String TRANSFER_TYPE_LPT = "LPT";
	public static final String BUCKET_CODE_LPT = "3";
	public static final String CHANNEL_TRANSFER_SUB_TYPE_POINT_CREDIT = "PCR";
	public static final String CHANNEL_TRANSFER_SUB_TYPE_POINT_DEBIT = "PDR";
	public static final String EXPIRED = "EXPIRED";
	// forgot password
	public static final String FORGOT_PASSWORD_MODE_EMAIL = "EMAIL";
	public static final String FORGOT_PASSWORD_MODE_SMS = "SMS";

	public static final String GEOG_CELLID_STATUS_ACTIVE = "Y";
	public static final String PPS = "PPS";

	// Service Management
	public static final String SERV_MGMT_TYPE = "STYP";

	// CATEGORY MANAGEMENT
	public static final String PROG_MGMT_TYPE = "PGMGT";
	public static final String REWARD_TYPE = "RWDTY";
	public static final String REDEMP_FREQ_TYPE = "RFTYP";

	// LMS Profile Cache
	public String PRODUCT_POSTETOPUP = "POSTETOPUP";
	public static final String PRODUCT_TYPE_AUTO_O2C_POST = "POSTPROD";

	public static final String WALLET_TYPE_BONUS = "BONUS";
	public static final String INTERFACE_NODE_TYPE_ID = "NODID";
	public static final String CHANNEL_TRANSFER_SUB_TYPE_AUTO_RETURN = "A";
	public static final String SERVICE_TYPE_CHNL_ROAM_RECHARGE_REVERSAL = "RRCREV";
	public static final String SUBSCRIBER_STATUS = "Active";
	public static final String COMMISSION_TYPE_PENALTY = "PENALTY";
	public static final String SYSTEM_LANGUAGE = "LANG";
	public static final String PRODUCT_GATEWAY_SERVICES = "PRGWC2S2";
	public static final String VAS_SERVICES = "VASSERVICE";
	public static final String VAS_ENQUIRY = "VASENQUIRY";
	public static final String SERVICE_TYPE_LASTX_TRANSFER_SERVICEWISE_REPORT = "LSTXTRFSRV";
	public static final String PASSWD_UNBLOCK_RESEND = "SEND_UNBLOCK_PSWD";
	public static final String PASSWD_UNBLOCK = "UNBOCK_PSWD";
	public static final String PIN_UNBLOCK = "PIN_UNBLOCK";
	public static final String PIN_UNBLOCK_SEND = "PIN_UNBLOCK_SEND";
	public static final String SERVICE_TYPE_P2PRECHARGE_IAT = "PRCIAT";
	public static final String TXN_STATUS_AMBIGIOUS = "250";
	public static final String TXN_STATUS_AMBIGIOUS1 = "205";
	public static final String NULL_VAR = null;
	public static final String BATCH_C2S_REV_PROCESS_ID = "BC2STREVERSAL";
	public static final String SERVICE_TYPE_C2S_PREPAID_REVERSAL_BULK = "BRCREV";
	public static final String C2S_REV_BATCH = "BATCH";
	public static final String OTF_COMMISSION = "PROMO";
	public static final String LOOKUP = "LOOKUP";
	public static final String SUBLOOKUP = "SUBLOOKUP";
	public static final String BARUSER = "BARUSER";
	public static final String VIEWBARUSER = "VIEWBARREDLIST";
	public static final String UNBARUSER = "UNBARUSER";
	public static final String CONUNBARUSER = "CONUNBARUSER";
	public static final Integer RESPONSE_SUCCESS = 200;
	public static final Integer RESPONSE_FAIL = 400;
	public static final String SERVICE_TYPE_LITE_RECHARGE = "LRC";
	public static final String SERVICE_TYPE_CARDGROUP_ENQUIRY = "CGENQREQ";
	public static final String COMMPS = "COMMPS";
	public static final String COMMPSL = "COMMPSL";
	public static final String COMMPLSS = "COMMPLSS";
	public static final String SERVER_DETAIL = "SERVER_DETAIL";
	public static final String REQUEST_SOURCE_TYPE_REST = "REST";
	public static final String HTTPS_URL = "https://";
	public static final String HTTP_URL = "http://";
	public static final String COLON = ":";
	public static final String FORWARD_SLASH = "/";
	public static final String HYPHEN = "-";
	public static final String UNDERSCORE = "_";
	public static final String COMMA = ",";
	public static final String ROUND_PARAN_START = "(";
	public static final String ROUND_PARAN_END = ")";
	public static final String C2SREV = "C2SREV";
	public static final String C2SLOADTXN = "C2SLOADTXN";
	public static final String C2SDOREV = "C2SDOREV";
	public static final String C2SREVSTAT = "C2SREVSTAT";
	public static final String INTERFACE_DETAIL = "INTFCDETAIL";
	public static final String DELETE_INTERFACE = "INTFCDELETE";
	public static final String HOURLY_FILTER = "HOURLY";
	public static final String NET_SUMM_DOWNLOAD = "NETSUMMDNLD";
	public static final String GREETMSGDOMAIN = "GREETMSGDOMAIN";
	public static final String GREETMSGCAT = "GREETMSGCAT";
	public static final String GREET_USER_DOWNLOAD = "GREETUSRDNLD";
	public static final String PROMOVAS_EXTGW_TYPE = "EXTPROMOVASTRFREQ";
	public static final String NORMAL_COMMISSION = "DIFF";
	public static final String REVERSAL_EXTGW_TYPE = "RCREVREQ";
	public static final String KEY_VALUE_BSCS_STATUS = "BSCS_STATE";
	public static final String CURRCONV = "CURRCONV";
	public static final String CURRCONVUPD = "CURRCONVUPD";
	public static final String CURRENCY_CONVERSION = "CURENCYMOD";
	public static final String AUTO_ASSIGN_SERVICES = "A";
	public static final String TRANSACTION_MODE_DELETE = "D";
	public static final String MULTI_CURRENCY_SERVICE_TYPE = "MRC";
	public static final String CHANNEL_USER_TRANSFER = "USRTRF";
	public static final String USERTRFCAT = "USERTRFCAT";
	public static final String FNF_TYPE = "FNF";
	public static final String ZB_TYPE = "ZB";
	public static final String IS_LOW_BASE_RECHARGE = "Y";
	public static final String DEFAULT_NO = "NO";
	public static final String DEFAULT_SUCCESS_MESSAGE = "Recharge Successful";
	public static final String ENTERED = "ENTERED";
	public static final String EXITED = "EXITED";
	public static final String ENTERED_VALUE = "ENTERED value is : ";
	public static final String EXITED_VALUE = "EXITED value is : ";
	public static final String LOW_BASE_ELIGIBILITY_ENQUIRY = "LBSEENQ";
	public static final String BACK_BUTTON_CLICKED = "backButtonClicked";
	public static final String LOW_BASE_TRANSACTION_ENQUIRY = "LOWBALRCH";
	public static final String SCHEDULE_NOW_TYPE = "BOTH";
	public static final String SERVICE_TYPE_USERMOVEMENT = "USRMOVEMNT";
	public static final String REQUEST_TYPE = "CHNLUSERTRF";
	public static final String LOWBASE_REPORT_PROCESS_ID = "LBREPORT";
	public static final String DATA_OBJECT = "Data Object is";
	public static final String SERVICE_TYPE_CHNL_DATA_RECHARGE = "DRC";
	public static final String INTERFACE_CATEGORY_DATARECHARGE = "DRC";
	public static final String VAS_BLANK_SLCTR_AMNT = "VAS_BLNK_AMT";
	public static final String SQLEXCEPTION = "SQLException::";
	public static final String EXCEPTION = "Exception::";
	public static final String BTSLEXCEPTION = "BTSLBaseException::";
	public static final String ERROR = "Error in :: ";
	public static final String USER_STATUS_SUSPEND_BATCH = "SRB";
	public static final String USER_STATUS_DELETE_BATCH = "DRB";
	public static final String SUSPEND_DELETE_USER_DOWNLOAD = "DNLDUSRDLTSPNDLIST";
	public static final String APPROVE_DELETE_SUSPEND_BATCH = "APRVLBATCHDLTSPNDUSR";
	public static final String NETWORKSTATUS = "NETWORKSTATUS";
	public static final String SAVENETWORKSTATUS = "SAVENETWORKSTATUS";
	public static final String CHANGENETWORK = "CHANGENETWORK";
	public static final String SUBMITCHANGENETWORK = "SUBMITCHANGENETWORK";
	public static final String BATCH_GRPH_DOMAIN_PPROCESS_ID = "BATCHGRPHDMN";
	public static final String BATCH_GRPH_DOMAIN_PREFIX = "BG";
	public static final String BATCH_GRPH_DOMAIN_ID = "BGRPHDMN";
	public static final String BATCH_GRPH_CREATION = "BATCH_GRPH_CREATION";
	public static final String BATCH_GRPH_DOMAIN_STATUS_UNDERPROCESS = "U";
	public static final String BATCH_GRPH_DOMAIN_STATUS_CLOSE = "C";
	public static final String ADD_ACTION = "A";
	public static final String MODIFY_ACTION = "M";
	public static final String DELETE_ACTION = "D";
	public static final String BATCH_GRPH_DOMAIN_DOWNLOAD = "DNLDGRPHDMNLIST";
	public static final String BATCH_GRPH_DOMAIN_INITIATE = "INITIATEGRPHDMNBATCH";
	public static final String LOGIN_ID = "loginId";
	public static final String NETWORK_CODE = "networkCode";
	public static final String DATA = "data";
	public static final String LOADGEODOMAIN = "LOADGEODOMAIN";
	public static final String LOADDOMAIN = "LOADDOMAIN";
	public static final String BY_USER_TYPE = "userType";
	public static final String BY_OPT_USER_TYPE = "OPERATOR";
	public static final String IS_RESTRICTED = "isRestricted";
	public static final String OWNER_ONLY = "ownerOnly";
	public static final String LOADCATEGORY = "LOADCATEGORY";
	public static final String IS_SCHEDULED = "isScheduled";
	public static final String USER_ID = "userID";
	public static final String LOADSERVICE = "LOADSERVICE";
	public static final String CSV_EXT = ".csv";
	public static final String SERVICE_CODE = "serviceCode";
	public static final String FILE_TYPE = "fileType";
	public static final String SCHEDULE = "schedule";
	public static final String RESCHEDULE = "reschedule";
	public static final String COLUMN_HEADER_KEY = "column_header_key";
	public static final String OWNER_ID = "ownerID";
	public static final String HEADER_KEY = "header_key";
	public static final String SCTPTEMPL = "SCTPTEMPL";
	public static final String CATEGORY_CODE = "categoryCode";
	public static final String DOMAIN_CODE = "domainCode";
	public static final String NO_OF_RECORDS = "noOfRecords";
	public static final String SCHEDULE_NOE = "scheduleNow";
	public static final String REQUEST_FOR = "requestFor";
	public static final String SCHTOUPFLUP = "SCHTOUPFLUP";
	public static final String LOOK_UP_TYPE = "lookupType";
	public static final String FREQUENCY = "FREQ";
	public static final String VIEWSUBSSCHE = "VIEWSUBSSCHEDULE";
	public static final String LOADBATCHLIST = "LOADBATCHLIST";
	public static final String DWNLDBATCHFILE = "DWNLDBATCHFILE";
	public static final String PROCESSRESCHDL = "PROCESSRESCHDL";
	public static final String VIEWCANCEL = "VIEWCANCEL";
	public static final String VIEWCANCELMSISDN = "VIEWCANCELMSISDN";
	public static final String CANCELMSISDN = "CANCELMSISDN";
	public static final String CANCELBATCH = "CANCELBATCH";
	public static final String TYPE = "type";
	public static final String AUTO_NETWORKSTOCK_CREATE = "AUTOCREATE";
	public static final String BULK_VOUCHER_RESEND_PIN = "BULKVOUCHERRESENDPIN";
	public static final String CHANNEL_TRANSFER_APPROVAL_1 = "Approved Level 1";
	public static final String CHANNEL_TRANSFER_APPROVAL_2 = "Approved Level 2";
	public static final String STOCK_PRODUCT_O2C = "STOCK";
	public static final String CHANNEL_TRANSFER_SUB_TYPE_VOUCHER = "V";
	public static final String HEADER_MESSAGE = "header_message";
	public static final String SOS_PENDING_STATUS = "Pending";
	public static final String SOS_MANUAL_SETTLED_STATUS = "MSettled";
	public static final String SOS_AUTO_SETTLED_STATUS = "ASettled";
	public static final String SOS_ALLOWED_FLAG_YES = "Y";
	public static final String SOS_PARENT = "PARENT";
	public static final String SOS_OWNER = "OWNER";
	public static final String SOS_NETWORK = "NETWORK";
	public static final String SOS_TRANSACTION_MODE = "S";
	public static final String SOS_TRANSFER = "SOS Transfer";
	public static final String SOS_SETTLEMENT_TYPE_AUTO = "AUTO";
	public static final String SOS_SETTLEMENT_TYPE_MANUAL = "MANUAL";
	public static final String DO_WITHDRAW = "DO_WITHDRAW";
	public static final String BLOCK_TRANSACTION = "BLOCK_TRANSACTION";
	public static final String WITHDRAW_AMOUNT = "WITHDRAW_AMOUNT";
	public static final String TRANSFER_TYPE_DP_CODE = "DP";
	public static final String LAST_LR_PENDING_STATUS = "LRPending";
	public static final String LAST_LR_SETTLED_STATUS = "LRSettled";

	public static final String LR_REQUEST_TYPE = "LR";
	public static final String SOS_REQUEST_TYPE = "SOS";

	public String SERVICE_TYPE_CHNL_PROMO_RECHARGE = "PRORC";
	public static final String LR_TRANSACTION_MODE = "LR";
	public static final String LR_TRANSFER = "Last Recharge credit";

	public static final String OTF_TYPE_AMOUNT = "AMT";
	public static final String OTF_TYPE_COUNT = "CNT";

	public static final String PROMOTIONAL_TRNFR_TYPE_BOTH = "BOTH";
	public static final String DISTRIBUTION_TYPE = "O2CDT";
	public static final String DISTRIBUTION_MODE = "O2CDM";
	public static final String PACKAGE_MODE = "PACKAGEMODE";
	public static final String NORMAL_MODE = "NORMALMODE";



	public static final String COMM_TYPE_BASECOMM = "COMM";
	public static final String COMM_TYPE_ADNLCOMM = "ADNL";
	public static final String CONFIGURATION_TYPE = "COTYP";
	public static final String C2C_REVERSAL = "C2CREV";
	public static final String RECHARGE_REVERSAL = "RCREV";
	public static final String YES = "Y";
	public static final String C2S_TRANSFER_ORDERBY = "ORDBY";

	public static final String C2S_TRANSFER_REQUESTGATEWAY = "RG";
	public static final String C2S_TRANSFER_CHANEELDOMAIN = "CD";
	public static final String C2S_TRANSFER_TYPE = "TYP";
	public static final String C2S_TRANSFER_SERVICECLASS = "SC";
	public static final String C2S_TRANSFER_SERVICETYPE = "ST";
	public static final String C2S_TRANSFER_SUBSERVICE = "SS";
	public static final String C2S_TRANSFER_CARDGROUPSET = "CGS";
	public static final String C2S_TRANSFER_STS = "STATUS";
	public static final String JSON_CONTENT_TYPE = "application/json";
	public static final String OFFLINEC2S = "C2SBALSETLMNT";
	public static final String CHANNEL_USER_BALANCE = "EXUSRBALREQ";
	public static final String CHANNEL_TO_CHANNEL_TRF = "C2CTRF";
	public static final int MIN_LENGTH_DAMAGED_PIN_VOMS = 4;
	public String SERVICE_TYPE_VAS_VOUCHER_CONSUMPTION = "VCNVAS";
	public static final String VOUCHER_ENQ_ACTION_PIN = "P";
	public static final String VOUCHER_ENQ_ACTION_SNO = "S";
	public static final String VOUCHER_ENQ_ACTION_BOTH = "B";

	public static final String NO = "N";

	public static final String SERVICE_TYPE_DATA_CP2P = "CDATA";
	public static final String COMMISSION_PROFILE_STATUS_SUSPEND = "S";
	public static final String SERVICE_TYPE_TRRC = "TRRC";
	public static final String SERVICE_TYPE_CHNL_RECHARGE_PROMO = "PROMORC";

	public String SERVICE_TYPE_ADD_GEOGRAPHY_API = "GEOREQ";
	public String SERVICE_TYPE_VIEW_GEOGRAPHY_API = "VIEWGEOREQ";
	public String SERVICE_TYPE_CHANNEL_USER_ADD = "CHNLUSRADD";
	public String SERVICE_TYPE_CHANNEL_USER_MODIFY = "CHNLUSRMOD";

	public static final int SYSTEM_RECEIVER_ACTION = 0;
	public static final int OPERATOR_RECEIVER_ACTION = 1;
	public static final int CHANNEL_RECEIVER_ACTION = 2;

	public static final String ROLE_ACCESS_TYPE_BOTH = "B";
	public static final String C2C_REVERSE_SUBTYPE = "Reverse";
	public static final String SERVICE_TYPE_LAST_X_TRF_REQ = "LSTXTXNSW";
	public static final String TXN_ENQUIRY_SERVICE_TYPE = "TXNENQ";
	public static final String LAST_TXN_STATUS_SUBSCRIBER_SERVICE_TYPE = "LTSRVR";
	public static final String C2S_SUMMARY_ENQUIRY_SERVICE_TYPE = "C2SSUMENQ";
	public static final String SERVICE_TYPE_CHNLTXNSTATUS = "EXTXNSTAT";
	public static final String CU_COMM_EARNED = "LTCOMM";
	public static final String BURN_RATE_PROCESS = "BURN_RATE_INDICATOR";
	
	public static final String THRESHOLD_TYPE_ALL = "ALL";
	public static final String SERVICE_TYPE_VOMS = "VOMS";
	
	public String USER_MIGRATION_UNDER_PROCESS_STATUS="UP";
	public String USER_MIGRATION_MOVED_STATUS="M";
	public String USER_MIGRATION_COMPLETE_STATUS="C";
	
	public static final String COMM_TYPE_NORMAL = "NC";	
	public static final String COMM_TYPE_POSITIVE = "PC";
	public static final String DUAL_COMM_TYPE = "COMMT";
	
	//Colombia specific System Preferences - Start
	public static final String SERVICE_TYPE_WIRELESS_INTERNET_RECHARGE="WIRC";
	//Colombia specific System Preferences - End

	public static final String EMPTY = "";
	public static final String SPACE = " ";
	public static final String DATE_FORMAT_DDMMYY = "dd/MM/yy";//For validation
	public static final String DATE_FORMAT = "dd/MM/yy";//For system level
	public static final String DATE_FORMAT_HYPHEN = "dd-MM-yy";//For system level
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
	public static final String DATE_FORMAT_DDMMYY_WOSEPARATOR = "ddMMyy";
	public static final String DATE_FORMAT_YYMMDD_WOSEPARATOR = "yyMMdd";
	public static final String TIME_FORMAT_HHMMSS_WOSEPARATOR = "hhmmss";
	public static final String TIME_FORMAT_HHMM_WOSEPARATOR = "hhmm";
	
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
	public static final String TIME_HHMMSS_VALUE = "00:00:00";
	public static final String LOGGER_DATE_PASSED = " date passed is : ";
	public static final String LOGGER_DATE_RETURNED = " date returned is : ";
	public static final String LOGGER_DATE_FORMAT_PASSED = " and passed dateFormat is : ";
	public static final String LOGGER_DATE_FORMAT = " and dateFormat is : ";
	
	public static final String OTHER_COMMISSION_TYPE="OTCTP"; //for Other Commission Type
    public static final String OTHER_COMMISSION_PROFILE_SET_ID="OT_COM_SID";
    public static final String OTHER_COMMISSION_PROFILE_DETAIL_ID="OT_COM_DID";

	public static final String LOCALE_PERSIAN = "fa_IR@calendar=persian";
	public static final String GREGORIAN = "gregorian";
	public static final String PERSIAN = "persian";
	public static final String NEPALI = "nepali";
	public static final String NLS_CALENDAR = "nls_calendar";
	public static final String FORMAT_MONTH_YEAR = "FORMAT_MONTH_YEAR";
	public static final String CALENDER_DATE_FORMAT = "CALENDER_DATE_FORMAT";
	
	public static final String METHOD_GET_LOCALE_DATE = "getLocaleDate";
	public static final String DATE_FORMAT_LITERAL = "dateFormat";
	public static final String IS_MON_FORMAT_LITERAL = "isMonFormat";
	
	public String SUBSCRIBER_ROUTING_REGISTRATION = "SUBROUTREG";
	public String SUBSCRIBER_ROUTING_DELETION = "SUBROUTDEL";
	public static final String BARRED_TYPE_VOUCHER_PIN_INVALID_ATTEMPT_EXCEED = "BRVPINEX";
	public static final String USER_DOCUMENT_TYPE = "DOCTP";
	
	public static final String DISTRIBUTION_TYPE_STOCK = "ST";
	public static final String DISTRIBUTION_TYPE_VOUCHER = "VT";
	public static final String VC_SOLD_CONDITION = "VSLTYPE";
	public static final String VC_SOLD_CITY = "CITY";
	public static final String VC_SOLD_BANK = "BANK";
	public static final String VC_SOLD_CHANNEL = "CHANNEL";
	public static final String VC_SOLD_SOLDDATE = "SLDATE";
	public static final String VOUCHER_TYPE_PHYSICAL = "physical";
	public static final String VOUCHER_TYPE_ELECTRONICS = "eletronic";
	public static final String VOUCHER_SOLD_STATUS = "SL";
	
	public static final String TERMINAL_TYPE_VOUCHER = "TTYPE";

	public static final String SERVICE_DEFAULT_SELECTOR = "1";
	//Added for Airtel BFaso
    public static final String SERVICE_TYPE_USER_CREATION = "USRRG";
    public static final String SERVICE_TYPE_PRODUCT_RECHARGE = "PDRC";
    public static final String SERVICE_TYPE_GMB="CGMBALREQ";
    //Added for OSL
    public static final String SERVICE_TYPE_INIT_SELF_TPIN_RESET = "INPRESET";
    public static final String SERVICE_TYPE_SELF_TPIN_RESET = "PRESET";
    public static final String SERVICE_TYPE_SELF_TPIN_DATA_UPDATE = "DUPDATE";
    public static final String DW_COMMISSION_CAL_OTH_COMMISSION ="OTH";
    public static final String DW_COMMISSION_CAL_BASE_OTH_COMMISSION ="BASE_OTH";
    public static final String OTHER_COMMISSION_TYPE_CATEGORY = "CAT";
    public static final String OTHER_COMMISSION_TYPE_GATEWAY= "GAT";
    public static final String OTHER_COMMISSION_TYPE_GRADE= "GRAD";
    public static final String SERVICE_TYPE_SOS_TRANSFER = "SOSTRF";
    public static final String SERVICE_TYPE_SOS_MANUAL_SETELMENT = "SOSSTL";
	public static final String SALT_VALUE = "a47sAtZYP4FUsPDa42FY";
	public static final String MAPP_VERSION_REQ = "VERINFO";
	 public String SERVICE_TYPE_OLO_RECHARGE="OLO";
	
	public static final String PAYMENT_GATEWAY_MODULE = "PG";
	public static final String CHANNEL_TRANSFER_ORDER_PENDING = "PENDING";
    public static final String VMS_PRODUCT_TYPE = "VMSPT";
	
	public static final String P2P_PROMOTIONAL_LEVEL = "P2PPROMO";
	public static final String PROMOTIONAL_LEVEL_SUBSCRIBER = "SUB";

	public static final String PAYMENT_GATEWAY_TYPE = "PGTYP";
	public static final int P2P_PROMO_TRF_RULE_LVL_SUBSRIBER = 1;
	public static final int P2P_PROMO_TRF_RULE_LVL_CELLGRP_CODE = 2;
	/** START: Birendra: CLARO : 19-MARCH-2015 */
	public static final int C2S_SEQ_ID_MAX_VALUE=9999999;
	public static final int C2S_SEQ_ID_SUFFIX_LEN=7;
	public static final int C2S_SEQ_ID_PREFIX_LEN=4;
	/** STOP: Birendra: CLARO : 19-MARCH-2015 */
	public static final int VOUCHER_SENDER_RECIEVER_CREDIT = 0;
	public static final String PAYMENT_INSTRUMENT_MODE = "PMTMD";	
	
	
	
		public static final String INCR_QUANTITY_TYPE  = "INCQTTYP";
	public static final String DECR_QUANTITY_TYPE  = "DECQTTYP";
	
	
	
	public static final String KIND_OF_TRANSACTION_O2CASH = "O2CCASH";
	public static final String KIND_OF_TRANSACTION_O2CCONGMNT = "O2CCONGMNT";
	public static final String KIND_OF_TRANSACTION_O2CONLINE = "O2CONLINE";
	public static final String KIND_OF_TRANSACTION_O2CFOC = "O2CFOC";
	public static final String KIND_OF_TRANSACTION_C2C = "C2C";
	public static final String KIND_OF_TRANSACTION_O2CWITHDRW = "O2CWITHDRW";
	public static final String KIND_OF_TRANSACTION_O2CREVSAL = "O2CREVSAL";
	public static final String KIND_OF_TRANSACTION_C2CWITHDRW = "C2CWITHDRW";
	public static final String KIND_OF_TRANSACTION_C2CREVSAL = "C2CREVSAL";
	public static final String KIND_OF_TRANSACTION_C2SRC = "C2SRC";
	public static final String KIND_OF_TRANSACTION_C2SBRC = "C2SBRC";
	
	public static final String PAYMENT_INSTRUMENT_TYPE_CONS = "CONS";
	public static final String TRANSACTION_TYPE = "TRXTP";
		
	public static final String COMMISSION_PROFILE_OTF_ID = "COMM_OTFID";
	
	public static final int P2P_RECEIVER_ACTION = 4;		
	public static final String SERVICE_TYPE_NMVD = "NMVD";
	public static final String VOUCHER_CONSUMED_STATUS="CU";
	public static final String VOUCHER_STOLEN_STATUS="ST";
	public static final String VOUCHER_DAMAGED_STATUS = "DA";
	public static final String VOUCHER_HOLD_STATUS ="OH";
	public static final String VOUCHER_ENABLE_STATUS ="EN";
	public static final String VOUCHER_PREACTIVE_STATUS ="PA";
	public static final String SERVICE_TYPE_VMSPINEXT="VMSPINEXT";
	public static final String SUBSCRIBER_VOUCHER_ENQ = "SELFVCRENQ";
	public static final String CARD_GROUP_P2P="P2P";
	public static final String CARD_GROUP_VMS="VMS";
	public static final String VOUCHER_CONS_SERVICE="VCN";
	public static final String VOUCHER_LIST = "VOUCHER_LIST";
	public static final String VOUCHER_SENDER_TYPE_ID = "PRE";
	public static final String CATEGORY_CODE_DIST = "DIST";
	public static final String REST_LOGGEDIN_IDENTIFIER_TYPE = "identifierType";
	public static final String REST_LOGGEDIN_IDENTIFIER_VALUE = "identifierValue";
	public static final String SERVICE_TYPE_DVD = "DVD";    
	public static final String VOUCHER_SENDER_CLASS_ID = "SERID00020"; 
	public static final String CATEGORY_CODE_AGENT = "AG";
    public static final String CATEGORY_CODE_RETAILER = "RET";
    public static final String CATEGORY_CODE_DEALER = "SE";
    public static final String USER_AVAILABLE_VOUCHER_ENQ = "VCAVLBLREQ";
    public static final String USER_AVAILABLE_VOUCHER_SMSC = "8072";
	public static final String VOMS_BATCH_INITIATE = "VOMSBATCHINITCOUNT";
	public static final String VOMS_EXPIRY_PROCESS_ID = "VOMSEXPIRY";
	public static final String SERVICE_TYPE_VOUCHER_O2C = "VO2C";
	public static final String ONLINE_VOMS_GEN = "ONLINEVOMSGEN";
	
	public static final String SCREEN_O2C = "O2C";
	public static final String SCREEN_VOUCHER_DOWNLOAD = "VOUC_DOWN";
	public static final String SCREEN_VOUCHER_ACTIVE_PROFILE = "ACTIVE_PROF";
	
	public static final String BUNDLE_STATUS_ACTIVE = "Active";
	public static final String VOMS_BUNDLE_ID = "VOMS_BUNID";
	public static final String VOMS_BUNDLE_DETAIL_ID="VOMS_DETID";
	public static final String CHANGE_STATUS_ONLINE="CHANGESTATUSONLINE";
	public static final String CHANGE_STATUS_ONLINE_NETWORK_COUNTER="CHNGESTATOLNWCOUNT";
	public static final String SERVICE_TYPE_C2C_INITIATE="TRFINI";
	public static final String SERVICE_TYPE_C2C_APPROVAL="TRFAPPR";
	public static final String SERVICE_TYPE_C2C_VOUCHER_APPROVAL="C2CVOUCHERAPPROVAL";
	public static final String SERVICE_TYPE_UPUSRHRCHY="UPUSRHRCHY";
	
	public static final String C2C_TRF_APPRV_REJ_STATUS = "N";
	public static final String C2C_VOUCHER_STATUS_CLOSE = "CLOSE";
	public static final String C2C_VOUCHER_STATUS_NEW = "NEW";
	public static final String C2C_VOUCHER_STATUS_CANCEL = "CANCEL";
	public static final String SERVICE_TYPE_C2C_VOMS_TRANSFERS = "C2CVOMSTRF";
	public static final String C2C_VOUCHER_COMPLETION_SUBJECT_SENDER = "Voucher Transfer Request";
	public static final String C2C_VOUCHER_COMPLETION_SUBJECT_RECEIVER = "Voucher Transfer Request";
	public static final String SERVICE_TYPE_C2C_VOMS_INITIIATE = "C2CVOMSTRFINI";
	public static final String SERVICE_TYPE_C2C_VOMS = "TRFVOMS";
	public static final String SERVICE_TYPE_C2C_VOMS_INI = "INIVOMS";
	public static final String SERVICE_TYPE_C2C_VOMS_APPR ="C2CVOUCHER";    
	public static final String TRANSFER_SUB_TYPE_VOUCHER = "V";
	public static final String CHANNEL_USER_DETAILS = "USRDETAILS";
	public static final String SERVICE_TYPE_C2CBUYENQ="C2CBUYUSENQ";
	public static final String SERVICE_TYPE_C2STRFSVCNT="C2SSRVTRFCNT";
	public static final String SERVICE_TYPE_C2S_PROD_TXN="C2SPRODTXNDETAILS";
	public static final String C2S_TOTAL_NO_OF_TRANSACTION="C2STOTALTRANS";
	public static final String SERVICE_TYPE_PSBKRPT="PSBKRPT";
	public static final String SERVICE_TYPE_PASSBOOKVIEW="PASBDET";
	public static final String SEND_OTP_FOR_FORGOT_PIN = "OTPFORFORGOTPIN";
	public static final String SERVICE_TYPE_C2S_N_PROD_TXN="C2SNPRODTXNDETAILS";
	public static final String TOP_PRODUCTS = "Y";
	public static final String OTP_VALID_PIN_RST = "OTPVDPINRST";
	public static final String USER_INCOME_DETAILS_VIEW ="USRINCVIEW";
	public static final String TRANSACTION_DETAILED_VIEW="TOTTRANSDETAIL";
	public static final String COMMISSION_CALCULATOR="COMINCOME";
	public static final String SERVICE_TYPE_TAX_COMMISSION_CALCULATION="TXNCALVIEW";
	public static final String AUTO_COMPLETE_USERS_DETAILS = "AUTOCOMPLETE";
	public static final String GET_DOMAIN_CATEGORY_CONTROLLER="GETDOMAINCATEGORY";
	public static final String REDIS_ENABLE ="Y";
	public static final String MSISDN = "MSISDN";
	public static final String LOGINID = "LOGINID";
	public static final String STATUS_USED = "NOT IN";
	public static final String STATUS_N_AND_C ="'N', 'C'";
	public static final Integer NO_DATA_FOUND_STATUS = 204;
	public static final String O2C_DIRECT_APPRVL = "O2CAGAPRL";
	public static final String PAYABLE_AMOUNT_CALCULATION="PAYAMTCALC";
	public static final String O2C_TRANSFER_INITIATE="O2CINICU";
	public static final Integer UNAUTHORIZED_ACCESS =401;
	public String SERVICE_TYPE_O2C_TXN_REV = "O2CREV";
	public String SERVICE_TYPE_C2C_TXN_REV = "C2CREV";
    public static final Integer UNABLE_TO_PROCESS_REQUEST = 500;
    public static final String  EXTCODE = "EXTCODE";
    public static final String  MOBILE_NUMBER = "Mobile number**";
    public static final String  USER_MOBILE_NUMBER = "Mobile number";
    public static final String  LOGIN_ID1 = "Login ID**";
    public static final String  EXTCODE_1 = "External code**";
    public static final String  QUANTITY = "Quantity*";
    public static final String  REMARKS = "Remarks";
    public static final Integer CREATED =201;
    public static final String[] OAUTHCODES = {"1080001","1080002","1080003","241023","241018"};
    public static final String VOMS_EXPIRY_BATCH_ID = "VECH_BID";
    public static final String PRODUCT="ProductCode*";
    public static final String FILE_TYPE1 = "FILETYPE";
    public static final String FILE_NAME = "FILENAME";
    public static final String FILE_ATTACHMENT = "FILEATTACHMENT";
    public static final String SERVICE_KEYWORD = "SERVICEKEYWORD";
	public static final String SERVICE_TYPE_O2C_VOUCHER_TRF = "O2CVOUCHERTRF";
	public static final String SERVICE_TYPE_O2C_VOUCHER_INI = "O2CVOUCHERINI";
	public static final Integer DVD_BULK_FILE_HEADER_SIZE =6;
	public static final String [] DVD_FILE_HEADER = {"Susbcriber's MSISDN*","Voucher Type Code*","Voucher Segment Code*","Voucher Denomination*","Voucher Profile ID*","Number of Vouchers*"};
	public static final String[] DELTE_FILE_CODES = {"241146"};
	public static final String[] VALIDATION_FOR_MSISDN = {"Susbcriber's MSISDN", "Mobile number*", "Gifter mobile number*", "Notification MSISDN *"};
	public static final String[] VALIDATION_FOR_AMOUNT = {"Requested amount*", "Voucher Denomination"};
	public static final String[] VALID_NUMBER = {"Number of Vouchers"};
	public static final String SERVICE_TYPE_DVDBULK = "DVDBULK"; 
	public static final String APPRVL_LEVEL_1 = "ONE"; 
	public static final String  APPRVL_LEVEL_2 = "TWO"; 
	public static final String  APPRVL_LEVEL_3 = "THREE"; 
	public static final String C2C_VOUCHER_TRF_APR1 = "C2CVCTRFAPR1";
	public static final String C2C_VOUCHER_TRF_APR2 = "C2CVCTRFAPR2";
	public static final String C2C_VOUCHER_TRF_APR3 = "C2CVCTRFAPR3";
	public static final String SERVICE_TYPE_C2CAPPR_LIST = "C2CVTAPLST";
	public static final String O2C_APPROVE = "APPROVE";
	public static final String O2C_REJECT = "REJECT";
	public static final String USER_NAME = "USER NAME";
	 public static final String MOBILE_NUMBERR = "MOBILE NUMBER";
	 public static final String ETOPUP_BALANCE = "BALANCE";
	 public static final String STATUS = "STATUS";
	 public static final String DOMAIN = "DOMAIN";
	 public static final String CATEGORY = "CATEGORY";
	 public static final String PARENT_NAME = "PARENT_NAME";
	 public static final String USER_PARENTS_NAME = "PARENT NAME";
	 public static final String PARENT_MOBILE_NUMBER = "PARENT MOBILE NUMBER";
	 public static final String OWNER_MOBILE_NUMBER = "OWNER MOBILE NUMBER";
	 public static final String DATE_RANGE = "DATE RANGE";
	 public static final String FROM_AMOUNT = "FROM AMOUNT";
	 public static final String TO_AMOUNT = "TO_AMOUNT";
	 public static final String OWNER_NAME = "OWNER NAME";
	 public static final String GEOGRAPHY = "GEOGRAPHY";
	 public static final String LOGINIDD = "LOGINID";
	 public static final String CONTACT_PERSON_NAME = "CONTACT PERSON_NAME";
	 public static final String GRADE = "GRADE";
	 public static final String REGISTERED_DATE_TIME = "REGISTERED DATE TIME";
	 public static final String LAST_MODIFIED_ON = "LAST MODIFIED ON";
	 public static final String LAST_MODIFIED_BY = "LAST MODIFIED BY";
	 public static final String TRANSACTION_PROFILE = "TRANSACTION PROFILE";
	 public static final String COMMISSION_PROFILE = "COMMISSION PROFILE";
	 public static final String LAST_TXN_DATE_TIME = "LAST TXN DATE";
	 public static final String CHANNLE_USER_BAR = "BARUNBARUSER";
	 public static final String USERNAME = "userName";
	 public static final String CACHE_SUB_LOOKUPS = "SUBLOOKUP";
	 public static final String BATCH_ID = "BATCH ID";
	 public static final String BATCH_TYPE = "BATCH TYPE";
	 public static final String NUMBER_OF_RECORDS = "NO OF RECORDS";
	 public static final String INITIATOR_USER = "INITIATOR USER";
	 public static final String SCHEDULED_BY = "SCHEDULED BY";
	 public static final String BATCH_CREATION_DATE = "BATCH CREATION DATE";
	 public static final String SCHEDULED_STATUS = "SCHEDULED STATUS";
	 public static final String SERVICE_TYPEE = "SERVICE TYPE";
	 public static final String NEXTSCHEDULEON = "SCHEDULED ON";
	 public static final String LASTPROCESSEDON = "LAST PROCESSED ON";
	 public static final String MOBILENOSTATUS = "MOBILE NUMBER STATUS";
	 public static final String LASTTRANSACTIONID = "LAST TRANSACTION ID";
	 public static final String LASTTRANSACTIONSTATUS = "LAST TRANSACTION STATUS";
	 public static final String SUBSERVICE = "SUB-SERVICE";
	 public static final String SCHEDULEDAMOUNT = "SCHEDULED AMOUNT";
	 public static final String C2C_MOBILENUMBER_TAB_REQ = "C2C_MOBILENUMB_TAB_REQ";
	 public static final String C2C_ADVANCED_TAB_REQ = "C2C_ADVANCED_TAB_REQ";
	 public static final String TOTAL_RECORDS_UPLOADED ="TOTAL RECORDS UPLOADED";
	 public static final String UPLOAD_FAILED_COUNT = "UPLOAD FAILED COUNT";
	 public static final String BATCH_SIZE = "BATCH SIZE";
	 public static final String SCHEDULED_SIZE = "SCHEDULED SIZE";
	 public static final String CANCELLED_SIZE = "CANCELLED_SIZE";
	 public static final String STOCK="STOCK";
	 public static final String VOUCHER="VOUCHER";
	 public static final String NEXTSCHEDULEDATE="NEXT SCHEDULE DATE";
	 public static final String FREQ="FREQUENCY";
	 public static final String SCHEDULED_ITERATIONS="SCHEDULED ITERATIONS";
	 public static final String EXECUTED_ITERATIONS="EXECUTED ITERATIONS";
	 public static final String SEARCH_BY_TRANSACTIONID = "TRANSACTIONID";
	 public static final String SEARCH_BY_MSISDN = "MSISDN";
	 public static final String SEARCH_BY_ADVANCE = "ADVANCE";
	 public static final String SEARCH_BY_EXTCODE = "EXTERNAL CODE";
	 public static final String CHNL_CONTROL_TRANSFER_CONTROLLED = "Controlled";
	 public static final String CHNL_CONTROL_TRANSFER_UNCONTROLLED = "Uncontrolled";
	 public static final String CHNL_CONTROL_TRANSFER_ADJUSTMENT = "Adjustment";
	 public static final String DATE_TIME="DATE & TIME";
	 public static final String TRANSACTION_ID="TRANSACTION ID";
	 public static final String SUB_SERVICE="SUB SERVICE";
	 public static final String PRODUCT_NAME="PRODUCT NAME";
	 public static final String SENDER_NAME="SENDER NAME";
	 public static final String SENDER_MOBILE="SENDER MOBILE NUMBER";
	 public static final String SENDER_NETWORK="SENDER NETWORK CODE";
	 public static final String RECIEVER_MOBILE="RECEIVER MOBILE NUMBER";
     public static final String ERROR_MESSAGE="ERROR MESSAGE";
	 public static final String DIFFERENTIAL_APPLICABLE="DIFFERENTIAL APPLICABLE";
	 public static final String DIFFERNTIAL_GIVEN="DIFFERNTIAL GIVEN";
	 public static final String TRANSFER_VALUE="TRANSFER VALUE";
	 public static final String REQUEST_SOURCE="REQUEST SOURCE";
	 public static final String REVERSAL_ID="REVERSAL ID";
	 public static final String BONUS="BONUS";
	 public static final String ADD_CELL_ID="ADD CELL ID";
	 public static final String SWITCH_ID="SWITCH ID";
	 public static final String APPROVED_BY="APPROVED BY";
	 public static final String APPROVED_ON="APPROVED ON";
	 public static final String TRANSACTION_MODE="TRANSACTION MODE";
	 public static final String PAYMENT_MODE="PAYMENT MODE";
	 public static final String TRANSACTION_CONTROLLING_TYPE="TRANSACTION CONTROLLING TYPE";
	 public static final String REFERENCE_NUMBER="REFERENCE NUMBER";
	 public static final String DISTRIBUTION_T="DISTRIBUTION TYPE";
	 public static final String TRANSFER_CAT="TRANSFER CATEGORY";
	 public static final String TRANSFER_SUB_TYPE="TRANSFER SUB TYPE";
	 public static final String REQUESTED_QUANTITY="REQUESTED QUANTITY";
	 public static final String PAYABLE_AMOUNT="PAYABLE AMOUNT";
	 public static final String MOBILE_NUM="MOBILE NUMBER";
	 public static final String PRODUCT_NAME1="PRODUCT NAME";
	 public static final String CHNL_TRANSACTION_MODE_NORMAL= "Normal";
	 public static final String CHNL_TRANSACTION_MODE_AUTO= "Auto";
	 public static final String FROM_DATE= "FROM DATE";
	 public static final String TO_DATE= "TO DATE";
	 public static final String OPTION_LOGIN_ID= "OPTION_LOGIN_ID";
	 public static final String OPTION_MSISDN= "OPTION_MSISDN";
	 public static final String SENDER_NETWORK1="SENDER MOBILE NUMBER";
	 public static final String OFFLINE_STATUS_INITIATED="INITIATED";
	 public static final String OFFLINE_STATUS_FAILED="FAILED";
	 public static final String OFFLINE_STATUS_INPROGRESS="INPROGRESS";
	 public static final String OFFLINE_STATUS_COMPLETED="COMPLETED";
	 public static final String OFFLINE_STATUS_DOWNLOADED="DOWNLOADED";
	 public static final String OFFLINE_STATUS_CANCELLED="CANCELLED";
	 public static final String OFFLINE_STATUS_DELETED="DELETED";
	 public static final String OFFLINEREPORTSERVICE_BEAN_NAME ="OfflineReportService";
	 public static final String OFFLINE_TASK_ID_PREFIX="RTASK"; 
	 public static final String MOBILE_OPERATOR="Operator";
	 public static final String REPORT_EXE_ONLINE="ONLINE";
	 public static final String REPORT_EXE_OFFLINE="OFFLINE";
	 public static final String XLSX_LAST_ROW="XLSX_LAST_ROW";
	 public static final String USER_STATUS = "USER STATUS";
	 public static final String CATEGORY_NAME="CATEGORY NAME";
	 public static final String RECORD_TYPE="RECORD TYPE";
	 public static final String THRESHOLD="THRESHOLD";
	 public static final String DATE="DATE";
	 public static final String MONTH="MONTH";
	 //Constants to be sent to INHandler and to be received from INhandler
		public static String  TAG_PARAMATER_SEPARATOR="&";
		public static String  TAG_VALUE_SEPARATOR="=";
		public static String  TAG_GEO_FENCING_ALERT_REQ="GEO_FENCING_ALERT_REQ";
	    public static String  TAG_GEO_FENCING_ALERT_REQ_USER="GEO_FENCING_ALERT_REQ_USER";
	    public static String  TAG_GATEWAY_TYPE="GATEWAY_TYPE";
	    public static String  TAG_GEOGRAPHY_CODE="GEOGRAPHY_CODE";
	    public static String  TAG_SELECTOR_BUNDLE_ID="SELECTOR_BUNDLE_ID";
	 	public static String  TAG_SELECTOR_BUNDLE_TYPE="SELECTOR_BUNDLE_TYPE";
		public static String  TAG_BONUS_BUNDLE_IDS="BONUS_BUNDLE_IDS";
		public static String  TAG_BONUS_BUNDLE_TYPES="BONUS_BUNDLE_TYPES";
		public static String  TAG_BONUS_BUNDLE_VALUES="BONUS_BUNDLE_VALUES";
		public static String  TAG_BONUS_BUNDLE_VALIDITIES="BONUS_BUNDLE_VALIDITIES";
		public static String  TAG_IN_RESP_BUNDLE_CODES="IN_RESP_BUNDLE_CODES";
		public static String  TAG_BONUS_BUNDLE_NAMES="_BONUS_BUNDLE_NAMES";
		public static String  TAG_BONUS_BUNDLE_RATES="BONUS_BUNDLE_RATES";
		public static String  TAG_BONUS_BUNDLE_CODES="BONUS_BUNDLE_CODES";
		public static String  TAG_IN_RESP_BUNDLE_PREV_BALS="IN_RESP_BUNDLE_PREV_BALS";
		public static String  TAG_IN_RESP_BUNDLE_PREV_VALIDITY="IN_RESP_BUNDLE_PREV_VALIDITY";
		public static final String GROUP_ROLE_LIST="GRPROLELST";
		public static final String FOC_LIMIT_VO_LIST="FOCLMTLST";
		public static final String FOC_LIMIT_ASSOCIATION_PROCESS_ID = "FOCLIMIT";
		public static final String MULTI_CURRENCY_INITIATE = "IN";
		public static final String MULTI_CURRENCY_APPROVE1 = "A1";
		public static final String MULTI_CURRENCY_APPROVE2 = "A2";
		public static final String MULTI_CURRENCY_REJECT = "RE";
		public static final String DB_ORACLE="ORACLE";
		public static final String DB_POSTGRES="POSTGRES";
		public static final String OFFLINE_STATUS_NODATA="NODATAFOUND";
		public static final String OFFLINE_REPORTACTION_DELETE="DELETE";
		public static final String OFFLINE_REPORTACTION_CANCEL="CANCEL";
		public static final String USER_ALLOW_CONTENT_TYPE = "XLS";
		public static final String TOTALTDS ="TOTALTDS";
		public static final String TOTALREQUESTEDCREDITQUANTITY ="TOTALREQUESTEDCREDITQUANTITY";
		public static final String TOTAPPRV_QNNTY_LEVEL1 ="TOTAPPRVQNNTYLEVEL1";
		public static final String TOTAPPRV_QNNTY_LEVEL2 ="TOTAPPRVQNNTYLEVEL2";
		public static final String TOTAPPRV_QNNTY_LEVEL3 ="TOTAPPRVQNNTYLEVEL3";
		public static final String TOT_TAX1_AMOUNT ="TOT_TAX1_AMOUNT";
		public static final String TOT_TAX2_AMOUNT ="TOT_TAX2_AMOUNT";
		public static final String TOT_CBC_AMOUNT ="TOT_CBC_AMOUNT";
		public static final String TOT_RECV_CREDIT_QNTY ="TOT_RECV_CREDIT_QNTY";
		public static final String TOT_DENOM_AMNT ="TOT_DENOM_AMNT";
		public static final String TOT_PAYABLE_AMNT ="TOT_PAYABLE_AMNT";
		public static final String TOT_NET_PAYABLE_AMNT ="TOT_NET_PAYABLE_AMNT";
		public static final String TRFT_TRANSFER ="T";
		public static final String SEARCH_BY_ADVANCED = "ADVANCED";
		public static final String O2CACKNOWLEDGE_LABEL_PREFIX = "o2cAcknowledge_label";
		public static final String TOT_COMM_AMOUNT ="TOT_COMM_AMOUNT";
		public static final String TRANSFER_CATEGORY_LOOKUPTYPE ="TRFTY";
		public static final String GROUP_ROLE_CHNL_ENQ = "Channel Enquiry";
		public static final String SUBGROUP_ROLE_CHNL_ENQ = "Reports & Enquiries";
		public static final String BULKUSER_ADVANCEDTAB_REQ = "ADVANCEDTAB";
		public static final String BULKUSER_BATCHNO_REQ = "BATCHNO";
		
		public static final  String  BATCHID_L="BATCH ID";
		public static final  String  BATCH_NAME_L="BATCH NAME";
	    public static final  String  DOMAIN_NAME_L="DOMAIN_NAME";
		public static final  String  PRODUCT_NAME_L="PRODUCT NAME";
	    public static final  String  BATCH_DATE_L="BATCH DATE";
		public static final  String  INITIATED_ON_L="INITIATED ON";
		public static final  String  INITIATED_BY_L="INITIATED BY";
		public static final  String  STATUS_L="STATUS";
		public static final  String  DETAILS_BATCHID_L="DETAILS BATCH ID";
		public static final  String  USERNAME_L="USER NAME";
		public static final  String  MSISDN_L="MSISDN";
		public static final  String  CATEGORY_L="CATEGORY";
		public static final  String  USERGRADE_L="USER GRADE";
		public static final  String  EXTUSRCODE_L="EXTUSRCODE";
		public static final String SERVICE_TYPE_SELECTOR_MAPPING = "SERVICE TYPE SELECTOR MAPPING";
		
		public static final  String  REQUESTEDQNTY_L="REQUESTED QNTY";
		public static final  String  TRFMRP_L="TRF MRP";
	    public static final  String  INITIATORREMARK_L="INITIATOR REMARK";
		public static final  String  APPROVED_ON_L="APPROVED ON";
		public static final  String  APPROVED_BY_L="APPROVED BY";
		public static final  String  APPROVED_REMARK_L="APPROVED REMARK";
		public static final  String PROMO_BONUS_CODE ="PROMO";
		public static final  String NAME ="NAME";
		public static final  String MODULE ="MODULE";
		public static final  String BARRED_TIME_AND_DATE ="BARRED TIME AND DATE";
		public static final  String BARRING_TYPE1 ="BARRING TYPE";
		public static final  String REASON_FOR_BARRING ="REASON FOR BARRING";
		public static final  String BARRED_AS ="BARRED AS";
		public static final  String BARRED_BY ="BARRED BY";
		public static final String CHANNEL_USER_APPROVE1 = "APPRV1";
		public static final String CHANNEL_USER_APPROVE2 = "APPRV2";
		public static final String CHANNEL_USER_APPROVE3 = "APPRV3";
		public static final String REPORT_FILENAME = "REPORT_FILENAME";
		public static final String OPERATION_ADD = "ADD";
		public static final String OPERATION_EDIT = "EDIT";
		public static final String USER_MODIFIED = "M";
		public static final String LOGIN_ID_TAB = "LOGIN_ID";
		public static final String MSISDN_TAB = "MSISDN";
		public static final String ADVANCED_TAB = "ADVANCED";
		public static final String BULK_PROC_ID = "BULKPROCID";
		public static final String USER_ACTION_DELETE = "DELETE";
		public static final String USER_ACTION_SUSPEND = "SUSPEND";
		public static final String USER_ACTION_RESUME = "RESUME";
		public static final String USER_ACTION_REJECT = "REJECT";
		public static final String ALWAYS_OTP_REQUIRED = "YES";
		public static final String ONE_TIME_OTP_REQUIRED = "ONETIME";
		public static final String NO_OTP_REQUIRED = "NO";
		public static final String CHANNEL_TRANSFER_SUB_TYPE_REVERSAL="X";
		public static final String AMOUNT="AMOUNT";
		public static final String SENDER_NETWORK_NAME="SENDER NETWORK NAME";
		public static final String EXTERNAL_REFERENCE_NUMBER="EXTERNAL REFERENCE NUMBER";
		public static final String PROMO_BONUS="PROMO BONUS";
		public static final String VOUCHER_SERIAL_NUMBER="VOUCHER SERIAL NUMBER";
		public static final String USER_STATUS_BARRED_REQUEST = "BR";
		
		//Operator Batch C2C
		public static final String OPT_C2C_BATCH_TRANSACTION_ID="OCB";
		public static final String OPT_C2C_BATCH_PROCESS_ID="OPTC2CBATCH";
		//Operator Batch C2C Transfer approval
		public static final String OPT_C2C_BATCH_APPROVAL_LEVEL = "OPT_C2C_BATCH_APPROVAL_LVL";

		public static final String P2P_SUBS_THRSHOLD_ENQ="SUBTHRHENQ";
	public static final String SERVICE_TYPE_LST_N_EVD_TRF = "LSTNEVDTRF";
		public static final String USER_LOAN_TXN_MODE = "L";
		public static final String USER_LOAN_REQUEST_TYPE = "LOAN";
		public static final String LOAN_SETTLEMENT_TYPE_AUTO = "AUTO";
		public static final String LOAN_SETTLEMENT_TYPE_MANUAL = "MANUAL";

		// lookup type for loan profile type
		public static final String PROFILE_TYPE="LPTYP";
		public static final String LOAN_PROFILE_IN_DAYS="D";
		public static final String LOAN_PROFILE_IN_HOURS="H";
		
		public static final String BATCH_LOAN_PROFILE_LIST = "LOAN_PROFILE";


	public static final String SERVICE_TYPE_102VAS_RECHARGE = "102VAS";
		public static final String SERVICE_TYPE_103VAS_RECHARGE = "103VAS";
		public static final String SERVICE_TYPE_104VAS_RECHARGE = "104VAS";
		public static final String SERVICE_TYPE_105VAS_RECHARGE = "105VAS";
		public static final String SERVICE_TYPE_106VAS_RECHARGE = "106VAS";
		public static final int SUBSCRIBER_MAX_LENGTH=12;
		public static final String BATCH_FOR_C2S_REV = "BATCH_FOR_C2S_REV";
		public static final String TIME_FORMAT = "HH:mm:ss";
		public static final String TIME_SECONDS_SUFFIX = ":00";
		
		public static final String SERVICE_TYPE_OTHERBALAN = "OTHERBALAN";
		public static final String SERVICE_TYPE_CHILDBAR = "CHILDBAR";
		public static final String SERVICE_TYPE_CHILDUNBAR = "CHILDUNBAR";
		public static final String SERVICE_TYPE_CHILDPINRESET = "CHLDPINRST";
		public static final String ON = "on";
		public static final String OFF = "off";
		public static final String COLUMN_LENGTH = "COLUMN_LENGTH";
		public static final String OPERATOR_COMM_WALLET_OPTION = "OPTCOMMWALLETOPTION";
		public static final String DIRECT_COMM_PAYOUT = "DPBATCHTRF";
		public static final String COMM_PAYOUT="COMM_PAYOUT";
		public static final String FOC_TRANSFER="FB";
		public static final String DP_TRANSFER="DP";
		public static final String MASK_TYPE_MASK="MASK";
		public static final String MASK_TYPE_HMAC="MAC";
		public static final String MASK_TYPE_ENC="ENC";
		public static final String STATUS_NEW="W";
		public static final String SERVICE = "SERVICE";
		public static final String PWD_UNBLOCKED = "Password is un-blocked.";
		public static final String PWD_BLOCKED = "Password is blocked.";
		public static final String VOUCHER_RESEND = "VOUCHER PIN RESEND";
		public static final String CHANGE_STATUS="CHANGESTATUS";
		public static final String TRANSFER ="Transfer rule already exists";
		public static final String DOMAIN_UNASIGNED_CATEGORY ="#UNASGND#";
		
		public static final String C2C_TRF_APR1 = "C2CTRFAPR1";
		public static final String C2C_TRF_APR2 = "C2CTRFAPR2";
		public static final String C2C_TRF_APR3 = "C2CTRFAPR3";
		public static final String PROMO_LEVEL_USER ="USR";
		public static final String PROMO_LEVEL_GRADE ="GRD";
		public static final String PROMO_LEVEL_GEOGRAPHY ="GRP";
		public static final String PROMO_LEVEL_CATEGORY ="CAT";
		public static final String PROMO_LEVEL_CELLGROUP ="CEL";
		public static final String PROMO_LEVEL_SERVICE ="SRV";
		public static final String PARTIAL_SUCCESS ="PARTIAL_SUCCESS";
		public static final String RECORD_PROCESS_STATUS ="RECORD_PROCESS_STATUS";
		public static final String USER_NAME_REQ = "user.selectparentuser.error.UserNamereq";
		public static final String GEOGRAPHY_REQ ="user.selectparentuser.error.goegraphyreq";
		public static final String CATEGORY_REQ ="user.selectparentuser.error.categoryreq";
		public static final String USER_NOT_EXIST ="user.selectparentuser.error.usernotexist";
		public static final String NO_GEOGRAPHY_EXIST= "promotrfrule.addtrfrule.msg.nogeotype";
		public static final String NO_GEO_DOMAIN_EXIST= "promotrfrule.addtrfrule.msg.nogeodomain";
		public static final String NO_GRADE_EXIST= "promotrfrule.addtrfrule.msg.nograde";
		public static final String NO_CELL_GROUP_EXIST= "promotrfrule.addtrfrule.msg.nocellgrouplist";
		public static final String NO_RULE_DATA="user.prmotrnsferRule.error.noRuleData";
		public static final String MODIFY_PROMO_TR_SUCCESS = "promotrfrule.modtrfrule.msg.success";
		public static final String MODIFY_PROMO_TR_UNSUCCESS = "promotrfrule.modtrfrule.msg.unsuccess";
		public static final String PROMO_RECORD_EXISTS="promotrfrule.operation.msg.alreadyexist";
		public static final String PROMO_RECORD_NOT_EXISTS="promotrfrule.operation.msg.recordNotexist";
		public static final String PROMO_MODULE="MODULE";
		public static final String PROMO_SEPERATOR=":";
		public static final String PROMO_COMMA=",";
		public static final String PROMO_NETWORK_CODE="NETWORK CODE";
		public static final String PROMO_SENDER_SUBSCRIBER_TYPE="Sender subscriber type";
		public static final String PROMO_SENDER_SERVICE_CLASSID="Sender service class id";
		public static final String  PROMO_RECEIVER_SERVICE_CLASSID="Receiver service class id";
		public static final String PROMO_SUB_SERVICE_TYPE_ID="Sub service type ID";
		public static final String PROMO_SERVICE_TYPE="Service type";
		public static final String PROMO_RULE_LEVEL="Rule level";
		public static final String PROMO_RULE_ADD_SCCUESS ="promotrfrule.addtrfrule.msg.success";
		public static final String PROMO_RULE_ADD_UNSCCUESS = "promotrfrule.addtrfrule.msg.unsuccess";
		public static final String PROMO_RULE_DUPLICATE = "promotrfrule.operation.msg.alreadyassign";
		public static final String PROMO_RULE_DELETE_SUCCESS = "promotrfrule.deltrfrule.msg.success";
		public static final String PROMO_RULE_DELETE_UNSUCCESS = "promotrfrule.deltrfrule.msg.unsuccess";
		public static final String PROMO_RULE_MAX_SLABS_EXCEED = "promotrfrule.addpromoc2stransferrules.error.slabMorethanDefined";
		public static final String PROMO_RULE_FROM_DATE_ERROR ="promotrfrule.addpromoc2stransferrules.error.fromdatetimeerror";
		public static final String PROMO_RULE_TILL_DATE_ERROR = "promotrfrule.addpromoc2stransferrules.error.tilldatetimeerror";
		public static final String PROMO_RULE_FROM_GRT_TILL_DATE_ERROR ="promotrfrule.addpromoc2stransferrules.error.daterange";
		public static final String PROMO_RULE_INVALID_CARD_GROUP ="promotrfrule.addpromoc2stransferrules.error.invalidCardGroup";
		public static final String VALIDATION_STAGE="VALIDATE";
		public static final String CONFIRM_STAGE="CONFIRM";
		public static final String NO_VALIDATION_ERRORS="NO_VALIDATION_ERRORS";
		public static final String NO_CATEGORY_LEVEL= "profile.transferprofileaction.msg.nocatlvlprofile";
		public static final String PROMO_RULE_INVALID_SERVICE_TYPE ="promotrfrule.addpromoc2stransferrules.error.invalidServiceType";
		public static final String PROMO_RULE_INVALID_SERVICE_GROUP ="promotrfrule.addpromoc2stransferrules.error.invalidServiceGroup";
		public static final String PROMO_RULE_TIME_RANGE_DUPLICATED ="promotrfrule.addpromoc2stransferrules.error.timeRangeDuplicated";
		public static final String PROMO_RULE_FROMTIME_grt_TILLTIME = "promotrfrule.addpromoc2stransferrules.error.fromTimgrtTillTime";
		public static final String PROMO_RULE_FROMTIME_TILLTIME_ALREADYDEFRANGE = "promotrfrule.addpromoc2stransferrules.error.fromtimeTilltimealreadyinRang";
		public static final String PROMO_RULE_INVALID_TIME_SLAB_FORMAT = "promotrfrule.addpromoc2stransferrules.error.invalidTimeSlab";
		public static final String SERVICE_KEYWORD_MESSAGEGATEWAY_LENGTH_EXCEEDED = "servicekeyword.messageGateway.length.exceeded";
		public static final String PROMO_RULE_INVALID_APPLICATION_FROM_DATE="promotrfrule.addpromoc2stransferrules.error.invalidApplicableFromDate";
		public static final String PROMO_RULE_INVALID_APPLICATION_TO_DATE= "promotrfrule.addpromoc2stransferrules.error.invalidApplicableToDate";
		public static final String ALPHABET_WITH_SPACE= "ALPHABET_WITH_SPACE";
		public static final String REGEX_ERROR_CODE= "REG738483";
		public static final String REGEX_NUMERIC_ONLY= "REGEX_NUMERIC_ONLY";
		public static final String CONTACT_NUMBER_LABEL="Contact number"; 
		public static final String NUMERIC_LABEL ="Numeric";
		public static final String DEREGISTER_SUCCESS = "p2p.subscriber.uploadsubscriberfileforunreg.message.success";
		public static final String ZERO = "0";
		public static final String ADDITIONAL_SUMMARY_NOSERVICEFOUND =  "additionalcommsummary.noServicelist.found";
		
		public static final String RESUMED = "RESUMED";
		public static final String INPUT_MSISDN="Msisdn";
		public static final String INPUT_LOGIN_ID="LoginId";
		public static final String INPUT_ADVANCE="Advance";
		public static final String SUBKEYWORD_ALREADY_EXIST = "servicekeyword.operation.msg.subkeywordalreadyexist";
		public static final String  SERVICEKEYWORD_ALREADY_EXIST  =  "servicekeyword.operation.msg.alreadyexist";
		public static final String  SUBKEYWORD_INPUT_MANDATORY  =  "subkeyword.input.mandatory";
		public static final String  SUBKEYWORD_BLANK_VALUE_PORT  = "error.blankvalueinPort";
		public static final String NOT_APPL = "Not applicable";
		public static final String  SUBKEYWORD_PORT_ALREADY_USED  =  "error.portalalreadyUsed";
		public static final String NAME_REGEX="NAME_REGEX";
		public static final String LABEL_MENU="Menu";
		public static final String LABEL_NAME="Name";
		public static final String DESC_ALPHA_NUMERIC ="Alpha numeric";
		public static final String DESC_ALPHA_NUMERIC_SPACE ="Alphabets and space ";
		public static final String BOTH_GRDCODE_GRDNAME_ALREADYEXISTS= "domain.addgrade.error.gradecode.gradeName.bothalreadyexists";
		public static final String GRADE_CODE_ALREADY_EXISTS= "domain.addgrade.error.gradecode.alreadyexists";
		public static final String GRADE_NAME_ALREADY_EXISTS="domain.addgrade.error.gradename.alreadyexists";
		public static final String PREFIX_UNDERSCORE="_";
		public static final String BULK_USER_FILE_NOT_UPLOADED= "bulkuser.uploadandvalidatebulkuserfile.error.filenotuploaded";
		public static final String LIST_OF_FILE_VALIDATION_ERRORS=  "bulkUser.listofValidationErrors.message";
		public static final String GENERAL_ERROR_CODE = "error.general.processing";
		public static final String LINE=  "Line ";
		public static final String BATCH_ADD_COMMPROFILE_INITIATE="Batch Add Commission Profile Initiate";
		public static final String BASE64_ENCODED_DATA="Base64 Encoded data";
		public static final String FILE_NAME_BATCH_ADD="File Name";
		public static final String BASE64_ENCODED_DATA_STRING="Base64 Encoded File as String";
		public static final String FILE_TYPE_XLS="xls";
		public static final String FILE_TYPE_XLSTYPE="File Type(xls)";
		public static final String SELECT_DOMAIN_FORBATCH_ADD_COMMPROFILE="selectDomainForBatchAddCommProfile";
		public static final String FILE_UPLOADED_SUCCESS="file uploaded successfully";
		public static final String PROMO_RULE_FROM_DATE_MANDATORY ="promotrfrule.addpromotransferrules.error.fromdatemandatory";
		public static final String PROMO_RULE_TILL_DATE_MANDATORY = "promotrfrule.addpromoc2stransferrules.error.tilldatemandatory";
		public static final String PROMO_RULES_ADDED_SUCCESS  ="promotrfrule.addtrfrulemultiple.msg.success";
		public static final String PROMO_RULES_MODIFIED_SUCCESS="promotrfrule.modtrfrulemultiple.msg.success";
		public static final String PROMO_RULE_TIME_SLAB_MANDATORY ="promotrfrule.timeslab.error.mandatory";
		public static final String PROMO_RULE_FROM_DATE_CURRNTDATE_ERROR= "promotrfrule.addpromorules.error.fromdateerror";
		public static final String PROMO_RULE_TILL_DATE_CURRNTDATE_ERROR= "promotrfrule.addpromorules.error.tilldateerror";
		public static final String PROMO_RULE_TILL_DATE_RANGE_ERROR = "promotrfrule.addpromorules.error.daterange";
		public static final String NTWRK_PREFIX_PREAPIDSERIES_ALREADYEXIST =  "network.networkprefix.prepaidseriesalreadyexist";
		public static final String NTWRK_PREFIX_PREAPIDSERIES_CANNOTDELETE = "network.networkprefix.prepaidseriesnotdeleted";
		public static final String NTWRK_PREFIX_POSTAPIDSERIES_ALREADYEXIST="network.networkprefix.postpaidseriesalreadyexist";
		public static final String NTWRK_PREFIX_POSTAPIDSERIES_CANNOTDELETE="network.networkprefix.postpaidseriesnotdeleted";
		public static final String NTWRK_PREFIX_OTHERDSERIES_ALREADY ="network.networkprefix.otherseriesalreadyexist";
		public static final String NTWRK_PREFIX_OTHERDSERIES_CANNOTDELETE ="network.networkprefix.otherseriesnotdeleted";
		public static final String NTWRK_PREFIX_PORTPREPAID_ALREADYEXIST ="network.networkprefix.portprepaidseriesalreadyexist";
		public static final String NTWRK_PREFIX_SAVE_SUCCESS ="network.networkprefix.successmessage";
		public static final String NTWRK_PREFIX_NOT_FOUND ="network.networkprefix.failedmessage";
		public static final String NTWRK_PREFIX_SERIES_CANNOTBEBALNK = "network.networkprefix.errors.series.required";
		public static final String NTWRK_PREFIX_PREPAID_SERIES_INVALID= "network.networkprefix.errors.invalidprepaidseries";
		public static final String NTWRK_PREFIX_OTHER_PREPAID_DUPLICATE= "network.networkprefix.errors.otherseriesprepaidduplication";
		public static final String NTWRK_PREFIX_PORT_PREPAID_INVALID= "network.networkprefix.errors.invalidportprepaidseries";
		public static final String NTWRK_PREFIX_PORT_PREPAID_DUPLICATION = "network.networkprefix.errors.portprepaidseriesprepaidduplication";
		public static final String NTWRK_PREFIX_PORT_OTHER_DUPLICATION ="network.networkprefix.errors.portprepaidseriesohterduplication";
		public static final String NTWRK_PREFIX_PORT_LOG_INFO ="network.networkprefix.log.info";
		
		public static final String DOWNLOAD_ADD_COMMISSION_PROFILE ="DOWNLOADADDCOMMISSIONPROFILE";
		public static final String DOWNLOAD_FILE_FOR_COMMISSION_PROFILE = "DOWNLOADFILEFORCOMMISSIONPROFILE";
		public static final String FILE_TYPE_XLS_ = ".xls";
		
		public static final String NTWRK_PREFIX_PRE_PAID_SERIES ="Pre paid series";
		public static final String NTWRK_PREFIX_POST_PAID_SERIES ="Post paid series";
		public static final String NTWRK_PREFIX_OTHER_SERIES ="Other series";
		public static final String NTWRK_PREFIX_PORT_SERIES ="Port series";
		public static final String NTWRK_PREFIX_NON_NUMERIC_VALUE ="network.networkPrefix.nonnumeric.port.value";
		public static final String SERVICE_MGMT_DOMAIN_CODE_NOTFOUND= "servicemgmt.searchservice.domainCode.notfound"; 
		public static final String SERVICE_MGMT_ADD_SUCCESS= "master.servicemgmt.msg.success";
		public static final String SERVICE_MGMT_ADD_FAILED ="master.servicemgmt.msg.fail";
		public static final String SERVICEPRDMAPPING_SAVE_SUCCESS = "master.selectoramountmapping.add.details.success.msg";
		public static final String SERVICEPRDMAPPING_SAVE_FAILED = "master.selectoramountmapping.add.details.nosuccess.msg";
		public static final String SERVICEPRDMAPPING_MODIFY_DETNOTEXIST = "master.selectoramountmapping.modify.details.notexist.msg";
		public static final String SERVICEPRDMAPPING_MODIFY_SUCCESS="master.selectoramountmapping.modify.details.success.msg";
		public static final String SERVICEPRDMAPPING_MODIFY_FAILED="master.selectoramountmapping.modify.details.nosuccess.msg";
		public static final String SERVICEPRDMAPPING_SAVE_ALREADYEXIST = "master.selectoramountmapping.add.details.alreadyexist.msg";
		public static final String SERVICEPRDMAPPING_DELETE_NOTEXIST =  "master.selectoramountmapping.delete.details.notexist.msg";
		public static final String SERVICEPRDMAPPING_DELETE_SUCCESS ="master.selectoramountmapping.delete.details.success.msg";
		public static final String SERVICEPRDMAPPING_DELETE_NOTSUCCESS ="master.selectoramountmapping.delete.not.success.msg";
		public static final String SERVICEPRDMAPPING_INVALID_AMOUNT ="master.selectoramountmapping.invalidAmount";
		public static final String SERVICEPRDMAPPING_SEARCH_FAILED = "master.selectoramountmapping.search.failed";
		public static final String SERVICEPRDMAPPING_ERROR_CLOSE_CONNECTION= "Error while close connection";
		public static final String SERVICEPRDMAPPING_INVALID_SERVICETYPE= "master.selectoramountmapping.invalid.servieType";
		public static final String NO_SERVICE_PRD_MAPPING_FOUND="1100937";
		public static final String SEARCH_SUCCESS="1100938";
		public static final String LOGGER_BATCHCOMMISSION_PROFILE_SUCCESS="BATCHADD_COMMISSION_PROFILE_SUCCESS";
		public static final String LOGGER_BATCH_MODIFY_COMMISSION_PROFILE_SUCCESS="BATCH_MODIFY_COMMISSION_PROFILE_SUCCESS";
		public static final String LOGGER_COMMISSION_PROFILE_USERDEFAULT_SUCCESS="COMMISSION_PROFILE_USER_DEFAULT_MODIFIED_SUCCESS";
		public static final String ERRORLOG_FILENAME_USERDEFAULT_CONFIG="USERDEFAULTERRORLOG_";
		public static final String LOGGER_O2C_CREDITLIMIT_SUCCESS = "O2C_CREDIT_LIMIT";
		public static final String DOMAIN_SUSPEND_MESSAGE = "suspended";
		public static final String DOMAIN_RESUME_MESSAGE = "resumed";
		public static final String NETWORK_STOCK_ALREADY_EXISTS="Previous stock deduction request is pending for approval.";
		public static final String NETWORK_STOCK_SUCCESS="Stock deduction has been successfully initiated";
		public static final String REMARKS_LENGTH = "100";
	    public static final String NETWORK_STOCK_APPROVAL ="NETWORK STOCK APPROVAL";

		public static final String MOBILE_CATEGORY = "MFOC";
		public static final String MULTIPLE_SUBSCRIBER_SELECTED = "multipleSubsSel";
		public static final String LOGGER_BLACKLIST_SUBSCRIBER = "BLACKLIST_SUBSCRIBER";
		public static final String UPLOAD_ICCID ="uploadiccidkeyfile";
	    public static final String DELIMITER_ICCID ="Delimiterforuploadiccid";
	    public static final String UPLOAD_POSH ="UploadPOSKeysFilePath";
	    public static final String BATCH_CORRECT_ICCID ="BATCH_CORRECT_ICCID_MSISDN_MAPPING_FILE_ROWS";
	    public static final String ICCID_KEY_FILE_SIZE="ICCID_KEY_FILE_SIZE";
	    public static final String OTHER_FILE_SIZE ="OTHER_FILE_SIZE";
	    public static final String STK_REGISTRATION_REQUIRED="STK_REGISTRATION_REQUIRED";
	    public static final String APP_VERSION="master.poskeyupload.label.applversion";
	    public static final String DATE_ICCID="master.poskeyupload.label.date";
		public static final String SIM_ICCID="master.poskeyupload.label.simprofile";
	    public static final String START_DATA="master.poskeyupload.label.startdata";
	    public static final String END_DATA="master.poskeyupload.label.enddata";
	    public static final String TEXT_OR_PLAIN = "text/plain";
		public static final String FILE_SIZE_ZERO= "Zero file size";
		public static final String NEW_LINE_CHARACTER= "\n";
		public static final String END= "end";
		public static final String UPLOAD_MSISDN_ROWS_LIMIT= "500";
		public static final String DELIMITER_FOR_UPLOAD_ICCID="Delimiterforuploadiccid";
		public static final String UPLOADPOSKEYSFILEPATH="UploadPOSKeysFilePath";
		public static final String BATCH_CORRECT_ICCID_MSISDN_MAPPING_FILE_ROWS="BATCH_CORRECT_ICCID_MSISDN_MAPPING_FILE_ROWS";
		public static final String FILE_DELETED ="File deleted successfully";
		public static final String ERROR_LOG_FILE_NAME ="downloadErrorLogFile";
		public static final String ERROR_FILE_HEADER_MOVEUSER ="ERROR_FILE_HEADER_MOVEUSER";
		public static final String ERROR_FILE_HEADER_PAYOUT ="ERROR_FILE_HEADER_PAYOUT";
		public static final String ICCID_MAX_LIGHTH="20";
		public static final String IMSI_MAX_LIGHTH="15";
		public static final String TRANSFER_INITIATE="Transfer Initiated";
		public static final String TRANSFER_FAILED="Transfer Failed";
		public static final String PAID="PAID";
		public static final String SINGLE_QUOTES="'";
		public static final String SUCCESS_MSG = "successfully";
		public static final String FAILED_MSG = "failed";
		public static final String ICCID_NOT_ASSOCIATED = "This ICCID/IMSI is not associated with any MSISDN";
		public static final String MSISDN_NOT_ASSIGNED ="This MSISDN is not assigned to any user";
		public static final String MSISDN_NOT_ASSOCIATED = "This MSISDN is not associated with any ICCID/IMSI";
		public static final String ICCID_UNSUPPORTED_NETWORK = "ICCID is from unsupported network";
		public static final String VOUCHER_EXPIRY_DATE_FORMAT = "VOUCHER_EXPIRY_DATE_FORMAT";
        public static final Pattern STATUS_PATTERN = Pattern.compile("^[YSN]$");
		public static final Integer PARTIAL_SUCESS_STATUS = 207;
		public static final String CELL_ID_FILE_PREFIX = "CELLGRP";
		public static final String CELL_ID_REASSOCIATE_FILE_PREFIX = "CRASSFILE_";
		public static final Pattern FILE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
		public static final Pattern FILE_TYPE_PATTERN = Pattern.compile("(xlsx?|csv)$",Pattern.CASE_INSENSITIVE);
		public static final String UPLOAD_DELETE_ICCID_FILE_PATH = "UploadDeleteICCIDFilePath";
		public static final String ICCID_ERROR_FILE_HEADER ="ICCID_ERROR_FILE_HEADER";
		public static final String MOBILE_NUMBER_LENGTH ="500";
		public static final String DELIMETER_FOR_UPLOAD_ROUTING ="DelimiterforuploadRouting";
		public static final String UPLOAD_ROUTING_FILE_PATH ="UploadRoutingFilePath";
	//stk
	public static final String WML_CODE = "WML code";
	public static final String DESCRIPTION = "description";
	public static final String SUPPORTED_LANGUAGE = "supportedLanguage";
	public static final String HI = "hi";
	public static final String SERVICE_SUCC_ADDED = "Service Successfully Added";
	public static final String STK_DRAFT_VERSION = "DD";
	public static final String STK_LABEL2_PATTERN = "stkLabel2Pattern";
	public static final boolean BOOLEAN_TRUE = true;
	public static final String PERCENTAGE = "%";
	public static final String BYTE_FILE_LENGTH = "byteFileLength";
    public static final String STK_SAVE_DRAFT = "D";
    public static final String CHECK_ZEROES = "^0+|0+$";
	public static final String SERVICE_SUCC_MODIFIED = "STK Service modified Successfully";
	public static final String STK_VERSION_PATTERN = "STK_VERSION_PATTERN";
	public static final String STK_DETLETE_LOG_MESSAGE = "saving as draft";
	public static final String DOWNLOAD_CELL_ID_PATH ="DownloadCellGroupPath";
	public static final String DOWNLOAD_CELL_FILE_NAME ="GEOGCELL";
	public static final String DOWNLOAD_SHEET_NAME ="Template Sheet";
	public static final String DOWNLOAD_MASTER_SHEET_NAME ="Master Sheet";
	public static final String UPLOAD_CELL_ID_PATH = "UploadCellIdFilePath";
	public static final String MAX_FILE_SIZE_CELL_ID = "MaxFileSizeInByteForCellIdMgmt";

	public static final String MAXRECORD_IN_CELL_ID_ASSOCIATION="MaxRecordsInCellIdAssociation";
	public static final String NAME_REGEX_ALPHANUMERIC ="NAME_REGEX_ALPHANUMERIC";

	public static final String LOGGER_CELL_ID_MAPPING_SUCCESS_OPT ="Cell Ids batch association completed.";

	public static final String DOWNLOAD_ERROR_FILEPATH ="DownloadErLogFilePath";
	public static final String CELLID_MAX_LENGTH = "CELLID_MAX_LENGTH";

	public static final String DELETE_ERROR_MESSAGE = "Error in deleting the uploaded file";
	public static final String DELETE_VALIDATION_FAILED = "as file validations are failed Exception::";
	public static final String MESSAGE_LABEL="Message";
	public static final String LINENO_LABEL ="LineNo";
	public static final String CELL_ID	="cellId";
	public static final String ERROR_FILE_PREFIX_LABEL="GeogCell";
	public static final String FILE_ATTACMENT_ERROR_LABEL="FILENAME/FILEATTACHMENT IS NULL";
	public static final String STK_SERVICE_ADDED="added";
	public static final String STK_SERVICE_DRAFT="saved";
	public static final String TIMESTAMP_FORMAT="yyyy-MM-dd HH:mm:ss.S";
	public static final String NUMERIC_TYPE = "^[0-9]*$";
	public static final String YES_DES = "Yes";
	public static final String NO_DES = "No";
	public static final String LOGGER_LMS_SOURCE = "LOYALITY_MANAGEMENT";
	public static final String ADD_LMS="addactprofile";
	public static final String REQIRED= "required";
	public static final String INVALID= "invalid";
	public static final String DATE_FORMATTER="\\d{2}/\\d{2}/\\d{4}";
	public static final String TIME_FORMATTER="\\d{2}:\\d{2}";
	public static final String MOD_LMS="modifyactprofile";
	public static final String GROUP_ROLE_MGMT_REQUEST = "grpmgmt";
	public static final String LOGOUT_REQUEST = "logout";
	public static final String M_CLASS_AND_P_ID="mclass^2&pid^61:";
	public static final String RECORD_SUCESS="All records processed successfully";
	public static final String MAX="max";
	public static final String MIN="min";
	public static final String SYSTEM_DATE_FORMAT_VALIDATE="SYSTEM_DATE_FORMAT_VALIDATE";


}