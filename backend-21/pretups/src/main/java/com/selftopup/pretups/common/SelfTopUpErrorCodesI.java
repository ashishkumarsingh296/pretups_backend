package com.selftopup.pretups.common;

public class SelfTopUpErrorCodesI {

    public static final String C2S_ERROR_INVALIDMESSAGEFORMAT = "4323";
    public static final String C2S_PIN_NEWCONFIRMNOTSAME = "4317";
    public static final String PRODUCT_NETWK_SUSPENDED = "6007";
    public static final String PRODUCT_NETWK_CONSUM_NOTALLOWED = "6009";
    public static final String PRODUCT_NETWK_DELETED = "6008";
    public static final String PRODUCT_NOT_ASSOCIATED_WITH_NETWK = "6006";
    public static final String PRODUCT_NOT_FOUND = "6005";
    public static final String PRODUCT_NOT_AVAILABLE = "6004";
    public static final String ERROR_INVALID_SERTYPE_PRODUCT_NOT_FOUND = "6018";
    public static final String ERROR_NOTFOUND_SERIES_TYPE = "4354";
    public static final String WLIST_ERROR_NO_NETWORK_AVALAIBLE = "2265";
    public static final String WLIST_ERROR_NO_POST_PAID_INTERFACE = "2266";
    public static final String WLIST_ERROR_NO_INTERFACE_MAPPED = "2267";
    public static final String WLIST_ERROR_NO_SERVICE_CLASS = "2268";
    public static final String WLIST_ERROR_INVALID_NETWORK_CODE = "2264";
    public static final String WLIST_ERROR_INTERFACEID_NOT_POST_PAID = "2271";
    public static final String WLIST_ERROR_MULTIPLE_NETWK_SUPPORT = "2269";
    public static final String WLIST_ERROR_NETWK_NOT_FOUND = "2270";
    public static final String WLIST_ERROR_INTERFACE_NTWK_MAPPING_NOT_FOUND = "2275";
    public static final String WLIST_ERROR_MOVE_LOCATION_NOT_EXIST = "2232";
    public static final String WLIST_ERROR_FILE_NOT_MOVED_SUCCESSFULLY = "2241";
    public static String XML_ERROR_INVALIDMESSAGEFORMAT = "7532";
    public static final String CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK = "7031";
    public static final String GRACE_DATE_IS_WRONG = "7595";
    public static final String RECHARGE_IS_NOT_ALLOW = "7596";
    public static final String RECHARGE_AMOUNT_IS_NOT_SUFFICIENT = "7597";
    public static final String SOS_MAX_BAL_ALLOWED = "1011010";
    public static final String SOS_LESS_VAL_DAYS = "1011008";
    public static final String SOS_SUBS_NOT_ACTIVE = "1011011";
    public static final String SOS_SUBS_CORE_BAL_NEGATIVE = "1011023";
    public static final String SOS_REQ_AMT_MORE = "1011025";
    public static final String C2S_SQL_ERROR_EXCEPTION = "1033002";
    public static final String EXTSYS_REQ_INVALID_TYPE_VALUE = "1004059";
    public static final String USER_STATUS_RESUME_FAILED = "4034";
    public static final String USER_STATUS_NOTSUSPENDED = "4032";
    public static final String USER_STATUS_RESUME_SUCCESS = "4033";
    public static final String USER_STATUS_SUSPEND_FAILED = "4036";
    public static final String USER_STATUS_SUSPEND_SUCCESS = "4035";

    // -------------------------------------------------------------------

    public static final String TXN_STATUS_SUCCESS = "200";
    public static final String TXN_STATUS_FAIL = "206";
    public static final String TXN_STATUS_AMBIGUOUS = "250";
    public static final String TXN_STATUS_UNDER_PROCESS = "205";
    public static final String P2P_SENDER_SUCCESS = "201";
    public static final String P2P_RECEIVER_SUCCESS = "202";
    public static final String SENDER_UNDERPROCESS_SUCCESS = "203";
    public static final String RECEIVER_UNDERPROCESS_SUCCESS = "204";
    public static final String C2S_RECEIVER_SUCCESS = "207";
    public static final String C2S_RECEIVER_UNDERPROCESS = "208";
    public static final String C2S_SENDER_UNDERPROCESS = "209";
    public static final String C2S_SENDER_SUCCESS = "210";
    public static final String C2S_ADJUSTMENT_SUCCESS = "211";
    public static final String REQUEST_NOT_APPLICABLE = "212";
    public static final String P2P_RECEIVER_UNDERPROCESS = "213";
    public static final String P2P_SENDER_UNDERPROCESS = "214";
    public static final String P2P_SENDER_CREDIT_BACK = "215";
    public static final String C2S_SENDER_UNDERPROCESS_B4VAL = "216";
    public static final String C2S_RECEIVER_FAIL = "217";
    public static final String FAIL_R = "218";
    public static final String C2S_SENDER_FAIL = "219";
    public static final String C2S_SENDER_AMBIGOUS = "220";
    public static final String C2S_SENDER_CREDIT_SUCCESS = "221";
    public static final String P2P_SENDER_UNDERPROCESS_B4VAL = "222";
    public static final String P2P_RECEIVER_FAIL = "223";
    public static final String P2P_FAIL_R = "224";
    public static final String P2P_SENDER_FAIL = "225";
    public static final String REQ_NOT_PROCESS = "616";
    public static final String INTERFACE_ERROR_RESPONSE = "505";

    public static final String C2S_RECEIVER_AMBIGOUS_KEY = "230";
    public static final String C2S_RECEIVER_FAIL_KEY = "231";
    public static final String P2P_RECEIVER_AMBIGOUS_MESSAGE_KEY = "233";
    public static final String P2P_RECEIVER_FAIL_MESSAGE_KEY = "235";

    public static final String P2P_SENDER_SUCCESS_WITHOUT_POSTBAL = "600";
    public static final String P2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "601";
    public static final String P2P_SENDER_CREDIT_BACK_WITHOUT_POSTBAL = "602";
    public static final String C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "603";

    public static final String P2P_ERROR_BLANK_MSISDN = "1000";
    public static final String P2P_ERROR_BLANK_REQUESTMESSAGE = "1001";
    public static final String P2P_ERROR_BLANK_REQUESTINTID = "1002";
    public static final String P2P_ERROR_BLANK_REQUESTINTTYPE = "1003";

    public static final String P2P_ERROR_SENDER_SUSPEND = "1004";
    public static final String P2P_ERROR_SENDER_BLOCKED = "1005";
    public static final String P2P_ERROR_SENDER_STATUS_NEW = "1006";
    public static final String P2P_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND = "1007";
    public static final String P2P_ERROR_AMOUNT_LESSZERO = "1008";
    public static final String P2P_INVALID_MESSAGEFORMAT = "1009";

    public static final String ERROR_EXCEPTION = "1999";
    public static final String P2P_ERROR_NETWORKPREFIX_NOTFOUND = "2000";
    public static final String ERROR_NETWORK_NOTFOUND = "2001";
    public static final String P2P_ERROR_EXCEPTION = "2002";
    public static final String ERROR_USERBARRED = "2003";
    public static final String ERROR_NOTFOUND_SUBSCRIBER = "2090";
    public static final String ERROR_NOTFOUND_SERVICEKEYWORD = "2101";
    public static final String ERROR_INVALID_REQUESTINTTYPE = "2102";
    public static final String ERROR_NOTFOUND_MESSAGEGATEWAY = "2110";
    public static final String ERROR_NOTFOUND_REQMESSAGEGATEWAY = "2111";
    public static final String ERROR_INVALID_IP = "2103";
    public static final String ERROR_INVALID_SERVICEPORT = "2104";
    public static final String ERROR_INVALID_LOGIN = "2105";
    public static final String ERROR_INVALID_PASSWORD = "2106";
    public static final String ERROR_INVALID_KEYWORDMESSAGEFORMAT = "2107";
    public static final String P2P_NOTFOUND_PAYMENTINTERFACEMAPPING = "2108";
    public static final String P2P_NOTFOUND_SERVICEINTERFACEMAPPING = "2109";
    public static final String P2P_ERROR_TRANSFER_RULE_NOTEXIST = "2100";
    public static final String ERROR_RECEIVER_USERBARRED = "2023";
    public static final String ERROR_USERBARRED_R = "2024";

    public static final String P2P_ERROR_SUBS_REQ_UNDERPROCESS = "2004";
    public static final String P2P_ERROR_SUBS_REQUNDERPROCESS_NOTUPDATED = "2005";

    public static final String P2P_ERROR_INVALIDMESSAGEFORMAT = "2006";
    public static final String NOT_GENERATE_TRASNFERID = "2007";

    public static final String ERROR_INVALID_REQUESTFORMAT = "2008";
    public static final String ERROR_INVALID_MSISDN = "2009";
    public static final String ERROR_INVALID_PIN = "2010";
    public static final String ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD = "2011";
    public static final String ERROR_NOTFOUND_SERVICEPAYMENTMETHOD = "2012";
    public static final String ERROR_NOTALLOWED_SELFTOPUPDEFAULTPMT = "2013";
    public static final String ERROR_INVALID_AMOUNT = "2014";
    public static final String ERROR_NOTFOUND_RECEIVERNETWORK = "2015";
    public static final String P2P_ERROR_TRANSFER_RULE_SUSPENDED = "2016";
    public static final String ERROR_INTFCE_SRVCECLSS_NOTFOUND = "2017";
    public static final String ERROR_INTFCE_SRVCECLSS_SUSPEND = "2018";
    public static final String ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_SEN = "2019";
    public static final String RECEIVER_LAST_REQ_UNDERPROCESS_S = "2020";
    public static final String RECEIVER_LAST_REQ_UNDERPROCESS_R = "2021";
    public static final String ERROR_SNDR_PINBLOCK = "2022";

    public static final String P2P_SNDR_DAY_MAX_TRANS_THRESHOLD = "2035";
    public static final String P2P_SNDR_DAY_MAX_AMTTRANS_THRESHOLD = "2036";
    public static final String P2P_SNDR_WEEK_MAX_TRANS_THRESHOLD = "2037";
    public static final String P2P_SNDR_WEEK_MAX_AMTTRANS_THRESHOLD = "2038";
    public static final String P2P_SNDR_MONTH_MAX_TRANS_THRESHOLD = "2039";
    public static final String P2P_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD = "2040";
    public static final String REC_LAST_REQ_UNDERPROCESS = "2043";
    public static final String P2P_MAX_PCT_TRANS_FAILED = "2047";
    public static final String P2P_MIN_VALIDITY_CHECK_FAILED = "2048";
    public static final String P2P_MIN_RESI_BAL_CHECK_FAILED = "2049";
    public static final String P2P_POSTPAID_USER_CTL_PARM_NOTDEFINED = "2044";
    public static final String P2P_SNDR_MIN_TRANS_AMT_LESS = "2045";
    public static final String P2P_SNDR_MAX_TRANS_AMT_MORE = "2046";

    public static final String REC_LAST_SUCCESS_REQ_BLOCK_S = "2050";
    public static final String NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_S = "2051";
    public static final String AMOUNT_TRANSFERS_DAY_EXCEEDED_S = "2052";
    public static final String P2P_CONS_FAILURE_BEFORE_BAR_EXCEEDED = "2053";

    public static final String CARD_GROUP_SETVERNOT_ASSOCIATED = "2070";
    public static final String CARD_GROUP_VALUE_NOT_IN_RANGE = "2071";
    public static final String CARD_GROUP_MSISDN_INGRACE_NOT_ALLOWED = "2072";
    public static final String CARD_GROUP_REQ_VALUE_NOT_IN_MULTIPLE = "2073";

    public static final String SKEY_PREVIOUS_UNUSED = "3010";
    public static final String SKEY_PREVIOUS_NOTDELETE = "3011";
    public static final String SKEY_NOTADDHISTORY = "3012";
    public static final String SKEY_NOTCONVERTSTRTOLONG = "3013";
    public static final String SKEY_NOTADDTRANSFER = "3014";
    public static final String SKEY_INVALID = "3015";
    public static final String SKEY_EXPIRED = "3016";
    public static final String ERROR_INVALID_SKEY = "3017";

    public static final String REQ_RES_MAPPING_NOTFOUND = "3050";
    public static final String NO_RES_MAPPING_FOUND_FORREQ = "3051";
    public static final String REQUEST_REFUSE = "3070";
    public static final String REQUEST_IN_QUEUE = "3071";
    public static final String P2P_REGISTERATION_ERROR = "4010";
    public static final String P2P_REGISTERATION_DUPLICATE = "4015";
    public static final String P2P_REGISTERATION_PREPAID_SUCCESS = "4011";
    public static final String P2P_REGISTERATION_POSTPAID_SUCCESS = "4012";
    public static final String P2P_REGISTERATION_PREPAID_SUCCESS_WITHOUT_PIN = "9979";

    public static final String P2P_DEREGISTERATION_SUCCESS = "4013";
    public static final String P2P_DEREGISTERATION_FAIL = "4014";

    /*
     * public static final String USER_STATUS_NOTUPDATED = "4030";
     * public static final String USER_STATUS_NOTACTIVE = "4031";
     */

    public static final String PIN_NOTUPDATED = "4050";
    public static final String PIN_OLDNEWSAME = "4060";
    public static final String PIN_NEWCONFIRMNOTSAME = "4065";
    public static final String NEWPIN_NOTNUMERIC = "4070";
    public static final String PIN_LENGTHINVALID = "4075";
    public static final String PIN_CHANGE_SUCCESS = "4076";
    public static final String PIN_CHANGE_FAILED = "4077";
    public static final String PIN_SAME_TO_DEFAULT_PIN = "4078";
    public static final String PIN_CONSECUTIVE = "4079";
    public static final String PIN_SAMEDIGIT = "4080";
    public static final String PIN_INVALID = "4081";
    public static final String PIN_CHANGE_SUCCESS_AND_REG = "4082";
    public static final String PIN_CHANGE_SUCCESS_AND_REGWITHPIN = "4083";

    public static final String ADD_BUDDY_FAILED = "4100";
    public static final String ADD_BUDDY_SUCCESS = "4101";
    public static final String BUDDY_NAME_IMPROPER = "4105";
    public static final String BUDDY_NAME_ALREADYEXIST = "4110";
    public static final String BUDDY_MSISDN_ALREADYEXIST = "4111";
    public static final String BUDDY_LIST_NOTFOUND = "4115";
    public static final String BUDDY_LIST_SUCCESS = "4116";
    public static final String BUDDY_NAME_FROM_DIFF_NETWORK = "4117";
    public static final String BUDDY_LIST_ERROR = "4118";

    public static final String DELETE_BUDDYNAME_INVALID = "4199";
    public static final String DELETE_BUDDY_FAILED = "4200";
    public static final String DELETE_BUDDY_SUCCESS = "4201";
    public static String BUDDY_DOESNOT_EXIST = "4202";

    public static final String NO_TRANSACTION = "4220";
    public static final String ACCOUNT_STATUS_SUCCESS = "4221";
    public static final String ACCOUNT_STATUS_FAILED = "4222";
    public static final String TRANSFER_STATUS_SUCCESS = "4223";
    public static final String TRANSFER_STATUS_FAILED = "4224";
    public static final String TRANSFER_REPORT_SUCCESS = "4225";
    public static final String TRANSFER_REPORT_FAILED = "4226";

    public static final String BARRED_SUBSCRIBER_FAILED = "4227";
    public static final String BARRED_SUBSCRIBER_SUCCESS = "4228";
    public static final String BARRED_SUBSCRIBER_SELF_RSN = "4229";
    public static final String BARRED_SUBSCRIBER_SYS_RSN = "4230";

    public static final String DAILY_TRANSFER_LIST_NOTFOUND = "4400";
    public static final String DAILY_CHANNEL_TRANSFER_IN__PRODUCT_MSG = "4401";
    public static final String DAILY_CHANNEL_TRANSFER_OUT__PRODUCT_MSG = "4402";
    public static final String DAILY_SUBSCRIBER_TRANSFER_OUT_PRODUCT_MSG = "4403";
    public static final String SELF_DAILY_TRANSFER_LIST_SUCCESS = "4404";
    public static final String DAILY_OTHER_TRANSFER_LIST_NOTFOUND = "4405";
    public static final String OTHERUSER_DAILY_TRANSFER_LIST_SUCCESS = "4406";

    public static final String CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED = "7013";
    public static final String CHNL_ERROR_RECR_MSISDN_BLANK = "7016";
    public static final String CHNL_ERROR_RECR_MSISDN_NOTINRANGE = "7017";
    public static final String CHNL_ERROR_RECR_MSISDN_LEN_NOTSAME = "7018";
    public static final String CHNL_ERROR_RECR_MSISDN_NOTNUMERIC = "7019";
    public static final String CHNL_ERROR_RECR_AMT_BLANK = "7020";
    public static final String CHNL_ERROR_RECR_AMT_NOTNUMERIC = "7021";
    public static final String CHNL_ERROR_RECR_AMT_LESSTHANZERO = "7022";
    public static final String CHNL_ERROR_SELF_TOPUP_NTALLOWD = "7023";
    public static String CHNL_ERROR_SNDR_FORCE_CHANGEPIN = "7035";
    public static String CHNL_ERROR_SENDER_STATUS_NEW = "7036";
    public static String CHNL_ERROR_SENDER_STATUS_APPROVED = "7037";
    public static final String CHNL_ERROR_BINARY_SMS_NOT_ALLOWED = "7038";
    public static final String C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND = "7039";
    public static final String C2S_ERROR_TRANSFER_RULE_SUSPENDED = "7040";
    public static final String CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND = "7041";
    public static final String CHNL_ERROR_SNDR_PINBLOCK = "7042";
    public static final String ERROR_CARD_GROUP_SET_SUSPENDED = "7043";
    public static final String CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND = "7044";
    public static final String ERROR_NETWORK_SERVICE_STATUS_NOTEXIST = "7045";
    public static final String ERROR_NETWORK_SERVICE_STATUS_SUSPENDED = "7046";
    public static final String CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED = "7047";
    public static final String ERROR_REC_NETWORK_NOTFOUND = "7048";
    public static final String COMM_PROFILE_SETVERNOT_ASSOCIATED = "7049";
    public static final String CHNL_ERROR_GEODOMAIN_SUSPEND = "7050";

    public static final String ERROR_MAINKEYWORD_NOTADM = "9001";
    public static final String ERROR_OPTKEYWORK_NULL = "9002";
    public static final String ERROR_MESSAGE_NULL = "9003";
    public static final String ERROR_BYTESTRING_NULL = "9004";
    public static final String SIMUPDATE_MESSAGE_SENT = "9005";
    public static final String SIMUPDATE_MESSAGE_SUCCESS = "9006";
    public static final String C2S_ERROR_EXCEPTION = "9007";

    public static String ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED = "5001";
    public static String ERROR_INVALID_USER_CODE_FORMAT = "5002";
    public static String ERROR_INVALID_PRODUCT_QUANTITY = "5003";
    public static String ERROR_INVALID_PRODUCT_CODE_FORMAT = "5004";
    public static String ERROR_USER_NOT_EXIST = "5005";
    public static String ERROR_COMMISSION_PROFILE_SUSPENDED = "5006";
    public static String ERROR_TRANSFER_PROFILE_SUSPENDED = "5007";
    public static String ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED = "5008";
    public static String ERROR_USER_TRANSFER_RULE_NOT_DEFINE = "5009";
    public static String ERROR_USER_TRANSFER_ASSOCIATED_NOT_ALLOWED = "5010";
    public static String ERROR_USER_TRANSFER_CHANNEL_BY_PASS_NOT_ALLOWED = "5011";

    public static final String DIFF_ERROR_AMOUNT_NOTINRANGE = "7059";
    public static final String NOT_GENERATE_ADJUSTMENTID = "7051";
    public static final String ERR_DIFF_FACTOR_CANNOT_BE_ZERO = "7052";
    public static final String C2S_ERROR_INVALID_SENDER_MSISDN = "7053";
    public static final String CHNL_ERROR_NO_SUCH_USER = "7054";

    public static final String REC_LAST_SUCCESS_REQ_BLOCK_R_PRE = "7055";
    public static final String REC_LAST_SUCCESS_REQ_BLOCK_R_POST = "9973";
    public static final String AMOUNT_TRANSFERS_DAY_EXCEEDED_R_PRE = "7056";
    public static final String AMOUNT_TRANSFERS_DAY_EXCEEDED_R_POST = "9974";
    public static final String NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_PRE = "7057";
    public static final String NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_POST = "9975";

    public static final String P2P_ERROR_INVALID_SENDER_MSISDN = "7058";

    public static String P2PSUBSCRIBER_UNBLOCKSENDPIN_MSG = "9900";
    public static String P2PSUBSCRIBER_UNBLOCKPIN_MSG = "9999";
    public static String P2PSUBSCRIBER_RESETPIN_MSG = "9919";
    public static String P2PSUBSCRIBER_SENDPIN_MSG = "9918";
    public static String P2PSUBSCRIBER_ACTIVATEPOSTPAIDSUBSCRIBER_MSG_DELETED = "9901";
    public static String P2PSUBSCRIBER_ACTIVATEPOSTPAIDSUBSCRIBER_MSG_ACTIVATED = "9902";
    public static String P2PSUBSCRIBER_SERVICE_SUSPEND = "9920";
    public static String P2PSUBSCRIBER_SERVICE_RESUME = "9921";

    public static String REG_INVALID_MESG_FORMAT = "9990";
    public static String P2P_SENDER_AUTO_REG_SUCCESS = "9991";
    public static String REG_ERROR_INTFCE_SRVCECLSS_NOTFOUND = "9992";
    public static String REG_ERROR_INTFCE_SRVCECLSS_SUSPEND = "9993";
    public static String REG_ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED = "9994";
    public static String REG_KEY_ERROR_INTFCE_SRVCECLSS_NOTFOUND = "9995";
    public static String REG_KEY_ERROR_INTFCE_SRVCECLSS_SUSPEND = "9996";
    public static String REG_KEY_ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED = "9997";
    public static String P2P_SENDER_AUTO_REG_SUCCESS_WITHPIN = "9998";

    public static String P2P_ERROR_INVALID_LANGUAGECODE = "1900";
    public static String P2P_LANGUAGE_UPDATE_SUCCESS = "1901";
    public static String P2P_LANGUAGE_UPDATE_FAILED = "1902";
    public static String P2P_ERROR_LANGUAGECODE_NOTNUMERIC = "1903";

    public static String P2P_ERROR_CPIN_INVALIDMESSAGEFORMAT = "9989";
    public static String P2P_ERROR_INVALID_TR_REPORTREQUESTFORMAT = "9988";
    public static final String ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC = "9987";
    public static final String INVLID_ACTION = "11111";
    public static String PIN_ALERT_MSG = "3502";
    public static String PASSWORD_ALERT_MSG = "3503";

    public static String USER_WEB_ACTIVATE = "12000";
    public static String USER_SMSPIN_ACTIVATE = "12001";
    public static String USER_WEB_SMSPIN_ACTIVATE = "12002";

    public static final String P2P_ERROR_SENDER_MSISDN_EXPIRED = "2054";

    public static final String ERROR_INVALID_SELECTOR_VALUE = "8500";
    public static final String ERROR_INTFCE_SRVCECLSS_SENDER_SUSPEND = "8501";
    public static final String ERROR_INTFCE_SRVCECLSS_P2P_RECEIVER_SUSPEND = "8502";
    public static final String ERROR_INTFCE_SRVCECLSS_C2S_RECEIVER_SUSPEND = "8503";
    public static final String CHNL_ERROR_RECR_AMT_LESSTHANALLOWED = "8511";
    public static final String CHNL_ERROR_RECR_AMT_MORETHANALLOWED = "8512";

    public static final String REG_KEY_ERROR_INTFCE_SRVCECLSS_SENDER_SUSPEND = "8550";
    public static final String REG_ERROR_INTFCE_SRVCECLSS_SENDER_SUSPEND = "8551";

    public static final String P2P_NETWORK_NOT_ACTIVE = "8513";
    public static final String C2S_NETWORK_NOT_ACTIVE = "8514";
    public static final String INTERFACE_NOT_ACTIVE = "8515";

    public static String LOW_BALANCE_ALERT_MSG_SUBKEY = "3504";
    public static String LOW_BALANCE_ALERT_MSG = "3505";

    public static String ERROR_USER_TRANSFER_PRODUCT_ALLOWMAXPCT = "5062";
    public static String ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG = "5022";
    public static String ERROR_INVALID_DEFAULT_PRODUCT_QUANTITY = "5066";
    public static String CHNL_RETURN_SUCCESS_SENDER_AGENT = "8110";
    public static String CHNL_RETURN_SUCCESS_RECEIVER_AGENT = "8111";
    public static String CHNL_WITHDRAW_SUCCESS_SENDER_AGENT = "8112";
    public static String CHNL_WITHDRAW_SUCCESS_RECEIVER_AGENT = "8113";
    public static String CHNL_TRANSFER_SUCCESS_SENDER_AGENT = "8114";
    public static String CHNL_TRANSFER_SUCCESS_RECEIVER_AGENT = "8115";
    public static String P2P_ERROR_INVALID_CHGELANG_REPORTREQUESTFORMAT = "9986";
    public static String P2P_ERROR_INVALID_TR_STATUS_REPORTREQUESTFORMAT = "9985";
    public static String CHNL_ERROR_SENDER_DELETE_REQUEST = "9984";
    public static String CHNL_ERROR_SENDER_SUSPEND_REQUEST = "9983";

    public static final String ERROR_INVALID_LANGUAGE_SEL_VALUE = "9982";

    public static final String REQ_TIMEOUT_FROM_QUEUE_C2S = "9967";
    public static final String REQ_TIMEOUT_FROM_QUEUE_P2P = "9966";

    public static final String CHNL_TRANSFER_ERROR_PRODUCT_SUSPENDED_SUBKEY = "8104";
    public static final String MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND = "9955";
    public static final String P2P_SENDER_SUCCESS_WITHOUT_ACCESSFEE = "9956";
    public static String RM_ERROR_AMOUNT_MONTHLYLIMIT_CROSSED = "3513";
    public static String RM_ERROR_AMOUNT_MORETHANMAXIMUM = "3512";
    public static String RM_ERROR_AMOUNT_LESSTHANMINIMUM = "3511";
    public static String PROCESS_ENTRY_NOT_FOUND = "3521";
    public static String PROCESS_ERROR_UPDATE_STATUS = "3522";
    public static String START_DATE_NOT_FOUND = "3523";
    public static String PROCESS_ALREADY_RUNNING = "3524";

    public static String DWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE = "3520";
    public static String DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "3525";
    public static String DWH_AMB_OR_UP_TXN_FOUND = "3526";
    public static String DWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "3527";
    public static String DWH_COULD_NOT_UPDATE_MAX_DONE_DATE = "3528";
    public static String DWH_ERROR_EXCEPTION = "3530";
    public static String DWH_ALL_FILES_DELETED = "3531";
    public static String RM_ERROR_RESTRICTED_SUBSCRIBER_BLACKLISTED = "3529";

    public static final String REG_USER_NOT_FOUND_ON_INTERFACE = "3532";
    public static final String P2P_POST_SNDR_MIN_TRANS_AMT_LESS = "3533";
    public static final String P2P_POST_SNDR_MAX_TRANS_AMT_MORE = "3534";
    public static final String P2P_POST_SNDR_DAY_MAX_TRANS_THRESHOLD = "3535";
    public static final String P2P_POST_SNDR_WEEK_MAX_TRANS_THRESHOLD = "3536";
    public static final String P2P_POST_SNDR_MONTH_MAX_TRANS_THRESHOLD = "3537";
    public static final String P2P_POST_SNDR_DAY_MAX_AMTTRANS_THRESHOLD = "3538";
    public static final String P2P_POST_SNDR_WEEK_MAX_AMTTRANS_THRESHOLD = "3539";
    public static final String P2P_POST_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD = "3540";
    public static final String P2P_POST_MAX_PCT_TRANS_FAILED = "3541";
    public static final String P2P_POST_MIN_RESI_BAL_CHECK_FAILED = "3542";

    public static final String P2P_SENDER_ALREADY_REG_NOT_FOUND_IN_VAL = "3543";

    public static final String ERROR_P2P_SAME_MSISDN_TRANSFER_NOTALLWD = "4356";
    public static final String P2P_POST_SNDR_MONTH_MAX_AMTTRANS_CCLMT_THRESHOLD = "4357";

    public static String P2PDWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE = "4242";
    public static String P2PDWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "4243";
    public static String P2PDWH_AMB_OR_UP_TXN_FOUND = "4244";
    public static String P2PDWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "4245";
    public static String P2PDWH_COULD_NOT_UPDATE_MAX_DONE_DATE = "4246";
    public static String P2PDWH_ERROR_EXCEPTION = "4247";
    public static String P2PDWH_ALL_FILES_DELETED = "4248";
    public static String COULD_NOT_CREATE_DIR = "3548";
    public static String MESSAGE_GATEWAY_NOT_ACTIVE = "3549";
    public static String REQ_MESSAGE_GATEWAY_NOT_ACTIVE = "3550";
    public static String RES_MESSAGE_GATEWAY_NOT_ACTIVE = "3551";

    public static final String C2S_UPDATE_SIM_PARAMS_REQD = "6666";
    public static String C2S_AMBIGUOUS_CASE_ALERT_MSG_SUBKEY = "3552";
    public static String C2S_AMBIGUOUS_CASE_ALERT_MSG = "3553";
    public static String P2P_AMBIGUOUS_CASE_ALERT_MSG_SUBKEY = "3554";
    public static String P2P_AMBIGUOUS_CASE_ALERT_MSG = "3555";
    public static String C2S_ERROR_GRPT_COUNTERS_REACH_LIMIT_D = "3556";
    public static String P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_D = "3557";
    public static String C2S_ERROR_GRPT_COUNTERS_REACH_LIMIT_M = "3558";
    public static String P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_M = "3559";
    public static String ERROR_LESS_DEFAULT_PRODUCT_QUANTITY = "3566";// is
                                                                      // defined
                                                                      // to send
                                                                      // invalid
                                                                      // requested
                                                                      // quantity
                                                                      // messagew
    public static String ERROR_INVALID_REC_USERCODE = "3567";// is defined to
                                                             // send invalid
                                                             // receiver user
                                                             // code
    public static String ERROR_INTFCE_SRVCECLSS_SUSPEND_R = "3568";// is defined
                                                                   // to send
                                                                   // message
                                                                   // for
                                                                   // receiver
                                                                   // service
                                                                   // class is
                                                                   // suspend
    public static String ERROR_INVALID_LOOKUP_CODE = "3569";// is defined to
                                                            // throw exception
                                                            // of invalid lookup
                                                            // code
    public static String INITIATOR_MSG_SCHEDULE_TOPUP_FINAL = "3570";// is
                                                                     // defined
                                                                     // to send
                                                                     // final
                                                                     // message
                                                                     // to
                                                                     // initiator
                                                                     // os
                                                                     // batch.
                                                                     // This
                                                                     // will
                                                                     // contain
                                                                     // the
                                                                     // successful,
                                                                     // fail and
                                                                     // underprocess
                                                                     // records

    public static String INVALID_AMOUNT_NULL = "6667";
    public static String INVALID_AMOUNT_NOTNUMERIC = "6668";
    public static String INVALID_AMOUNT_LESSTHANZERO = "6669";
    public static String ERROR_INVALID_AMOUNT_PREICISION_NOTALLOWED = "6670";
    public static String ERROR_BUDDY_NAME_MANDATORY = "6671";
    public static String ERROR_BUDDY_NAME_EXCEED_LENGTH = "6672";
    public static String ERROR_MAX_NO_OF_ALLOWED_BUDDY_RCHD = "6674";
    public static String P2P_ERROR_ADDBUDDY_INVALIDMESSAGEFORMAT = "6675";
    public static String ERROR_BUDDY_NAME_SP_CHARACTERS = "6676";
    public static String ERROR_BUDDY_NETWORK_NOTFOUND = "6677";
    public static String P2P_ERROR_DELBUDDY_INVALIDMESSAGEFORMAT = "6678";
    public static String P2P_ERROR_BUDDYLIST_INVALIDMESSAGEFORMAT = "6679";
    public static String P2P_ERROR_DEREGISTER_INVALIDMESSAGEFORMAT = "6680";
    public static String P2P_ERROR_AMBIGOUS_CASE_PENDING = "6681";
    public static String P2P_ERROR_RESUME_SERVCE_INVALIDMESSAGEFORMAT = "6682";
    public static String P2P_ERROR_REGSIETERD_SUBS_BARRING = "6683";
    public static String P2P_ERROR_SUSPEND_SERVCE_INVALIDMESSAGEFORMAT = "6684";
    public static String P2P_USER_STATUS_ALREADY_SUSPENDED = "6685";
    public static String INVALID_MSISDN_NULL = "6686";

    public static final String NETWORK_CODE_MSIDN_NETWORK_MISMATCH = "311";
    public static final String ERROR_EXT_NETWORK_CODE = "312";

    public static String ERROR_USER_TRANSFER_RULE_PRODUCT_NOT_ASSOCIATED = "7109";
    public static String ERROR_NO_COMMISION_PRODUCT_ASSOCIATED = "7110";
    public static String ERROR_NO_LATEST_COMMISSION_PROFILE_ASSOCIATED = "7111";
    public static String ERROR_NO_TRANSFERPROFILE_PRODUCT_ASSOCIATED = "7112";
    public static String ERROR_UPDATING_DATABASE = "7113";
    public static String O2C_WITHDRAW_SUCCESS = "7114";
    public static String O2C_WITHDRAW_SUCCESS_TXNSUBKEY = "7115";
    public static String O2C_WITHDRAW_SUCCESS_BALSUBKEY = "7116";

    public static String ERROR_TRANSFER_CATEGORY_NOT_ALLOWED = "6101";
    public static String ERROR_PAYMENTTYPE_NOTFOUND = "6102";
    public static String ERROR_PAYMENT_INSTRUMENT_NUM_INVALID = "6103";
    public static String ERROR_PAYMENT_INSTRUMENT_DATE_BLANK = "6104";
    public static String ERROR_PAYMENTTYPE_BLANK = "6105";
    public static String ERROR_PAYMENT_INSTRUMENT_DATE_NOT_PROPER = "6106";
    public static String ERROR_USER_TRANSFER_NOT_ALLOWED_NOW = "6107";
    public static String ERROR_NETWORK_PRODUCTS_NOT_MATCHING = "6108";
    public static String ERROR_COMMISSION_PROFILE_PRODUCTS_NOT_MATCHING = "6109";
    public static String ERROR_TRANSFER_PROFILE_PRODUCTS_NOT_MATCHING = "6110";
    public static String ERROR_EXT_TXN_NO_NOT_POSITIVE = "6111";
    public static String ERROR_EXT_TXN_NO_NOT_NUMERIC = "6112";
    public static String ERROR_EXT_TXN_NO_NOT_UNIQUE = "6113";
    public static String ERROR_REFERENCE_NO_BLANK = "6114";
    public static String ERROR_CHANNEL_REAMRK_NOT_PROPER = "6115";
    public static String ERROR_MESSAGE_FORMAT_NOT_PROPER = "6116";
    public static String ERROR_EXT_TXN_NO_BLANK = "6119";
    public static String ERROR_EXT_DATE_BLANK = "6120";
    public static String ERROR_EXT_DATE_NOT_PROPER = "6121";
    public static String ERROR_COMMISSION_PROFILE_QTY_INVALID = "6122";
    public static String ERROR_REFERENCE_NO_LENGTH_NOT_VALID = "6123";
    public static String ERROR_PRODUCT_TYPE_NOT_SAME = "6124";
    public static String ERROR_TRANSFER_RULE_PRODUCTS_NOT_MATCHING = "6125";

    public static String ERROR_USER_TRANSFER_CHANNEL_SENDER_BAR = "13043";
    public static String REQUESTED_QUANTITY_IS_NOT_PROPER = "13044";
    public static String ERROR_EXT_ID_IS_NEGATIVE = "13045";
    public static String ERROR_IN_NETWORK_STOCT_TRANSACTION = "13046";
    public static String ERROR_UPDATION_OPT_CHANNEL_USER_IN_COUNT = "13047";
    public static String ERROR_ADD_CHANNEL_TRANSFER = "13048";
    public static String ERROR_NOT_CREDIT_NETWORK_STOCK = "13049";
    public static String ERROR_UPDATION_USERDAILYBALANCE = "13050";
    public static String ERROR_CHNL_USER_NOT_ACTIVE = "13051";
    public static String CCE_XML_ERROR_MSISDN_DETAILS_NOTFOUND_ROUTING_LIST = "7580";
    public static String CCE_ERROR_INVALID_TRF_CATEGORY = "7581";
    public static String CCE_ERROR_USER_NOTIN_DOMAIN = "7582";
    public static String CCE_ERROR_SENDER_NOT_AUTHORIZE_DOMAIN = "7583";
    public static String CCE_ERROR_USER_NOTIN_GEOGRAPHY = "7584";
    public static String CCE_ERROR_SENDER_NOT_AUTHORIZE_GEOGRAPHY = "7585";
    public static String CCE_ERROR_ACC_CTRL_NOT_UPDATED = "7586";

    public static String ERR_INVALID_AMOUNT_UB = "5555";
    public static String ERR_NOTFOUND_SERIES_TYPE_UB = "5556";
    public static String C2S_ERR_SELF_UTILITYBIL_NTALLOWD = "5557";
    public static String ERR_RECEIVER_BARRED_UB = "5561";
    public static String C2S_ERR_EXCEPTION_UB = "5562";
    public static String C2S_RECEIVER_FAIL_UB = "5563";
    public static String FAIL_R_UB = "5564";
    public static String C2S_ERR_NOTFOUND_SRVCINTERFACEMAPPING_UB = "5565";
    public static String INTERFACE_NOT_ACTIVE_UB = "5566";
    public static String REQUEST_IN_QUEUE_UB = "5567";
    public static String REQUEST_REFUSE_UB = "5568";
    public static String CHNL_ERROR_RECR_IDENTITY_BLANK = "5577";
    public static String CHNL_ERROR_RECR_IDENTITY_NUM_NOTINRANGE = "5578";
    public static String CHNL_ERROR_RECR_NOTIF_NUM_LEN_NOTSAME = "5579";
    public static String CHNL_ERROR_RECR_NOTIFPREFIX_NOTFOUND_RECEIVERNETWORK = "5580";
    public static String INVALID_PAYEE_NOTIF_NUMBER = "5581";
    public static String INVALID_PAYEE_NOTIF_LANG_UB = "5582";
    public static String INVALID_MESSAGE_FORMAT_UB = "5583";
    public static String SENDER_UNDERPROCESS_SUCCESS_UB = "5584";
    public static String CHNL_ERROR_RECR_ID_NOTNUMERIC = "5585";

    public static final String ERROR_MISSING_SENDER_IDENTIFICATION = "313";
    public static String PROCESS_RESUMESUSPEND_INVALID_SERVICE = "7880";
    public static String PROCESS_RESUMESUSPEND_DB_NOT_UPDATED = "7881";
    public static String PROCESS_RESUMESUSPEND_CACHE_NOT_UPDATED = "7882";
    public static String PROCESS_RESUMESUSPEND_SUCCESS = "7883";
    public static String PROCESS_RESUMESUSPEND_REVERT = "7884";
    public static String PROCESS_RESUMESUSPEND_SUSPEND_MSG = "7897";

    public static final String CHNL_ERROR_LRCH_INVALIDMESSAGEFORMAT = "7885";
    public static final String LAST_RECHARGE_STATUS_NOT_FOUND = "7886";
    public static final String LAST_C2S_RECHARGE_STATUS_SUCCESS = "7887";
    public static final String LAST_C2S_RECHARGE_STATUS_FAIL = "7888";
    public static final String LAST_C2S_RECHARGE_STATUS_AMBIGUOUS = "7889";
    public static final String LAST_C2S_RECHARGE_STATUS_UNDER_PROCESS = "7890";
    public static final String LAST_C2S_RECHARGE_STATUS_DEFAULT = "7891";

    public static final String CHNL_ERROR_SNDR_MAX_PER_TRF_FAIL = "6602";

    public static final String ROUTING_ERROR_DIR_NOT_EXIST = "8888";
    public static final String ROUTING_ERROR_FILE_EXT_NOT_DEFINED = "8889";
    public static final String ROUTING_ERROR_COUNT_NOT_FOUND = "8890";
    public static final String ROUTING_ERROR_COUNT_NOT_NUMERIC = "8891";
    public static final String ROUTING_ERROR_CONN_NULL = "8892";
    public static final String ROUTING_ERROR_NO_PRE_PAID_INTERFACE = "8893";
    public static final String ROUTING_ERROR_DIR_CONTAINS_NO_FILES = "8894";
    public static final String ROUTING_ERROR_FILE_DOES_NOT_EXIST = "8895";
    public static final String ROUTING_ERROR_INTFACE_CAT_NOT_FOUND = "8896";
    public static final String ROUTING_ERROR_INTFACE_ID_NOT_FOUND = "8897";
    public static final String ROUTING_ERROR_EXT_ID_NOT_FOUND = "8898";
    public static final String ROUTING_ERROR_NO_OF_REC_NOT_FOUND = "8899";
    public static final String ROUTING_ERROR_NTW_CODE_NOT_FOUND = "8900";
    public static final String ROUTING_ERROR_HEADER_INFO = "8901";
    public static final String ROUTING_ERROR_NO_NETWORK_AVALAIBLE = "8902";
    public static final String ROUTING_ERROR_INVALID_INTFACE_CAT = "8903";
    public static final String ROUTING_ERROR_INVALID_NTW_CODE = "8904";
    public static final String ROUTING_ERROR_INVALID_EXT_ID = "8905";
    public static final String ROUTING_ERROR_NO_INTERFACE_MAPPED = "8906";
    public static final String ROUTING_ERROR_INTERFACE_NTWK_MAPPING_NOT_FOUND = "8907";
    public static final String ROUTING_ERROR_EXT_ID_INVALID = "8908";
    public static final String ROUTING_MISSING_INITIAL_FILES = "8909";
    public static final String ROUTING_MISSING_CONST_FILE = "8910";
    public static final String ROUTING_MISSING_LOG_FILE = "8911";
    public static final String ROUTING_UPLOAD_PROCESS_GENERAL_ERROR = "8912";
    public static final String ROUTING_UPLOAD_PROCESS_CONFIG_ERROR = "8914";
    public static final String ROUTING_ERROR_FILE_NO_RECORDS = "8915";
    public static final String ROUTING_ERROR_DATA_UPDATE = "8916";
    public static final String ROUTING_ERROR_HEADER_START_TAG_NOT_FOUND = "8917";
    public static final String ROUTING_FILE_MOVE_ERROR = "8918";
    public static final String ROUTING_NO_OF_REC_NOT_FOUND = "8919";
    public static final String ROUTING_NO_OF_REC_INVALID = "8920";
    public static final String ROUTING_FILE_AREADY_EXISTS_DESTINATION = "8921";
    public static final String ROUTING_NO_OF_REC_NOT_MATCHING = "8922";
    public static final String ROUTING_ERROR_NO_END_TAG = "8923";
    public static final String ROUTING_NO_OF_ERROR_MORE = "8924";
    public static final String EVR_SENDER_SUCCESS = "6612";

    public static String PROCESS_RESUMESUSPEND_INT_MSG = "7898";

    public static final String AUTO_RESUMESUSPEND_REVERT = "7899";
    public static final String AUTO_RESUMESUSPEND_SUCCESS = "7900";

    public static final String REQUEST_REFUSE_FROM_NWLOAD = "13070";
    public static final String REQUEST_REFUSE_FROM_INTLOAD = "23070";
    public static final String REQUEST_REFUSE_FROM_TXNLOAD = "33070";
    public static final String INVALID_QUANTITY = "18920";
    public static final String CCE_ERROR_REMARKS_LEN_MORE_THAN_ALLOWED = "7594";

    public static final String CHNL_FIRST_REQUEST_PIN_CHANGE = "7061";

    public static final String CHNLUSR_CHANGE_DEFAULT_PIN = "7062";

    public static final String RETRY_CONVERSION_RATE = "10000";
    public static final String INTERFACE_LIST_NOTFOUND = "10001";
    public static final String SENDER_CONVERSION_RATE_NOTFOUND = "10002";
    public static final String RECEIVER_CONVERSION_RATE_NOTFOUND = "10003";

    public static final String AUTO_CACHEUPDATE_FAIL = "79001";
    public static final String AUTO_CACHEUPDATE_STATUS = "79002";

    public static final String P2P_RECEIVER_GET_NUMBER_BACK_SUCCESS = "227";
    public static final String ERROR_SRVCECLSS_SENDER_NOT_ALLOWED = "228";

    // Error Codes for the BTRC imlplementation, used for the receiver side.
    // -Ashish K [03-10-07]
    public static final String AMOUNT_TRANSFERS_WEEK_EXCEEDED_R_PRE = "19000";
    public static final String AMOUNT_TRANSFERS_WEEK_EXCEEDED_R_POST = "19001";
    public static final String AMOUNT_TRANSFERS_MONTH_EXCEEDED_R_PRE = "19002";
    public static final String AMOUNT_TRANSFERS_MONTH_EXCEEDED_R_POST = "19003";
    public static final String AMOUNT_TRANSFERS_WEEK_EXCEEDED_S = "19004";
    public static final String AMOUNT_TRANSFERS_MONTH_EXCEEDED_S = "19005";
    public static final String NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_R_PRE = "19006";
    public static final String NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_R_POST = "19007";
    public static final String NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_R_PRE = "19008";
    public static final String NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_R_POST = "19009";
    public static final String NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_S = "19010";
    public static final String NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_S = "19011";
    public static final String MAX_ALLD_BAL_LESS_REQ_AMOUNT_R_PRE = "19012";
    public static final String MAX_ALLD_BAL_LESS_REQ_AMOUNT_R_POST = "19013";
    public static final String REQ_AMT_EXCEEDS_MAX_ALLD_BAL_S = "19014";

    // Error codes for the Network Alert Balance Process [Ashish K, 03-10-07]
    public static final String ERROR_LOW_BAL_NET_PROCESS = "19015";
    public static final String ERROR_LOW_BAL_NET_CONN_NULL = "19016";
    public static final String ERROR_LOW_BAL_NET_MSISDN_NOT_DEFINED = "19017";
    public static final String ERROR_LOW_BAL_NET_NETSTK_DETAIL_NOT_FOUND = "19018";
    public static final String ERROR_LOW_BAL_NET_NO_ACTIVE_NETWORK = "19019";
    public static final String P2P_POST_SNDR_NEGATIVE_RESIDUAL_BAL_THRESHOLD = "3578";

    // To enquire Transaction Status with External Refrence Number -
    // Vipul[23/10/2007]
    public static final String ERROR_RC_EXT_TXN_NO_NOT_UNIQUE = "7820";

    // pin password management by santanu
    public static final String C2S_PIN_CHECK_HISTORY_EXIST = "7063";
    public static final String P2P_PIN_CHECK_HISTORY_EXIST = "7064";

    // voucher file upload
    public static final String EXPIRY_DATE_BEFORE_CURRENT_DATE = "19020";
    public static final String START_SERIALNO_NOT_EQUAL_TO_FIRST_SERIALNO = "19021";
    public static final String NO_OF_RECORDS_NOT_EQUALTO_QUANTITY = "19022";
    public static final String INVALID_CREATE_DATE_FORMAT = "19023";
    public static final String INVALID_EXPIRY_DATE_FORMAT = "19024";
    public static final String PRODUCT_NOT_EXIST = "19025";
    public static final String INCORRECT_VALIDITY = "19026";
    public static final String HEADER_FIELDS_NOT_EQUALTO_DEFINED_VALUE = "19029";
    public static final String FILE_SCHEDULE_LATER = "19030";
    // public static final String VOUCHER_FILE_MORE_RECORDS="19031";

    // public static final String ERROR_INVALID_AMOUNT_APL_FEE="22002";

    // for c2s enquiry through external system(increments in error codes of card
    // group)
    public static final String CARD_GROUP_SET_IDNOT_FOUND = "21004";
    public static final String NO_SLAB_FOR_CARD_GROUP_SETID = "21005";
    public static final String CARD_GROUP_SLAB_NOT_FOUND = "21006";
    public static final String SERVICE_NOT_ALLOW_FOR_C2S_ENQUIRY_TO_USER = "21007";
    public static final String SERVICE_NOT_ALLOW_FOR_C2S_ENQUIRY_TO_THIS_NETWORK = "21008";

    // for PrepaidControllerWithValExt .
    public static final String P2P_RECEIVER_SUCCESS_OF_VAL_EXT = "22003";
    public static final String P2P_SENDER_SUCCESS_OF_VAL_EXT = "22004";
    public static final String P2P_ERROR_WHILE_GETTING_VALDAYS_AND_APPL_FEE = "22005";

    // For Black list restricted subscribers not allowed for recharge or for
    // CP2P services.
    public static final String RM_ERROR_RESTRICTED_SUB_RECHARGE_NOT_ALLOWED = "21009";
    public static final String RM_ERROR_RESTRICTED_SUB_NOT_ALLOWED_CP2P_PAYER = "21010";
    public static final String RM_ERROR_RESTRICTED_SUB_NOT_ALLOWED_CP2P_PAYEE = "21011";
    public static final String RM_ERROR_RESTRICTED_SUB_EXCEPTION_UB = "21012";
    // For category wise listing of daily balance listing of users
    public static final String UBS_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "22006";
    public static final String UBS_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "22007";

    public static final String C2S_GIFTER_UNDERPROCESS_B4VAL_GIFTRECHARGE = "14022";
    public static final String ERROR_USERBARRED_R_GIFTRECHARGE = "14023";
    public static final String ERROR_INVALID_AMOUNT_GIFTRECHARGE = "14024";
    public static final String ERROR_NOTFOUND_SERIES_TYPE_GIFTRECHARGE = "14025";
    public static final String C2S_ERROR_EXCEPTION_GIFTRECHARGE = "14026";
    public static final String C2S_ERROR_NOTFOUND_SERVICEINTERFACEMAPPING_GIFTRECHARGE = "14027";
    public static final String INTERFACE_NOT_ACTIVE_GIFTRECHARGE = "14028";
    public static final String REQUEST_IN_QUEUE_GIFTRECHARGE = "14029";
    public static final String REQUEST_REFUSE_FROM_INTLOAD_GIFTRECHARGE = "14030";
    public static final String TXN_STATUS_AMBIGUOUS_GIFTRECHARGE = "14031";
    public static final String FAIL_R_GIFTRECHARGE = "14032";

    // error codes for daily ransaction summary message
    public static final String ERROR_IN_DAILY_ALERT = "21013";
    public static final String MESSAGE_MULTIPLE_SEVICES_SUBKEY = "21014";
    public static final String MESSAGE_FOR_MULTIPLE_SERVICE_MAIN = "21015";
    public static final String DAILY_ALERT_EXECUTED_UPTO_DATE_NOT_FOUND = "21016";
    public static final String DAILY_MESSAGE_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "21017";

    // added for Mali (P2P SMS Confirmation Step)
    public static final String P2P_SENDER_CONFIRM = "21018";

    // Added to Remove issue of slow processing of request
    public static final String P2P_ERROR_EXCEPTION_TKING_TIME_TILL_VAL_R = "253";
    public static final String P2P_ERROR_EXCEPTION_TKING_TIME_TILL_VAL_S = "255";
    public static final String P2P_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP = "254";

    // change for reconciliation
    public static final String TXN_STATUS_CANCEL = "240";

    // schedule top up management
    public static final String GENERAL_PROCESSING_ERROR = "4455";
    public static final String RM_ERROR_RESTRICTED_SUBSCRIBER_RECHARGE_NOT_ALLOWED = "4456";
    // added for bonus
    public static final String P2P_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS = "238";
    public static final String P2P_RECEIVER_SUCCESS_WITH_BONUS = "239";
    public static final String P2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "241";

    // added for card group slab suspend/resume
    public static final String CARD_GROUP_SLAB_SUSPENDED = "2075";

    public static final String REC_BAL_LESS_TO_REQ_AMT = "21019";
    public static final String REC_BAL_LESS_TO_REQ_AMT_S = "21020";
    public static String RM_ERROR_RESTRICTED_SUB_RECHARGE_NOT_ALLOWED_P2P = "4457";
    public static String EXT_XML_ERROR_INVALID_MSISDN = "7901";

    public static final String O2C_EXTGW_DUPLICATE_TRANSCATION = "115";
    public static final String P2P_ERROR_BLANK_AMOUNT = "1010";
    public static final String C2S_ERROR_BLANK_AMOUNT = "6020";

    // Added for low network stock alert process
    public static final String LOW_STOCK_ALERT_MSG = "12017";

    public static final String BARRED_CHANEL_FAILED = "5721";
    public static final String BARRED_CHANEL_SUCCESS = "5722";
    public static String CHANEL_USER_ALREADY_BARRED = "5723";
    public static final String ORDER_CREDIT_ERROR_BLANK_AMOUNT = "5724";
    public static final String ORDER_LINE_ERROR_BLANK_AMOUNT = "5725";
    public static String IMPLICIT_MSG = "242";
    public static final String CHNL_ERROR_SNDR_REGISTERED_PIN_EXPIRED = "7066";

    public static final String ACTBONUS_SUCCESSFUL_MESSAGE = "6903";
    // Activation bonus calculation process
    public static final String ACT_TXN_PROCESS_ALREADY_EXECUTED = "21030";
    public static final String ACT_VOLUME_PROCESS_NOT_EXECUTED = "21031";
    public static final String TEMP_TABLE_NOT_CREATED = "21032";
    public static final String TEMP_TABLE_NOT_UPDATED = "21033";
    public static final String INSERTION_ERROR_BONUS_TABLE = "21034";
    public static final String VOLUME_PROCESS_ALREADY_EXECUTED = "21035";
    public static final String INSERTION_ERROR_USER_TXN = "21036";
    public static final String VOLUME_PROCESS_NOT_EXECUTED_SUCCESSFULLY = "21037";
    public static final String ERROR_DROPING_TEMP_TABLE = "21038";
    public static final String ACT_BONUS_EXCEPTION = "21039";
    public static final String BONUS_TABLE_NOT_UPDATED = "21040";
    public static final String USER_TXN_TABLE_NOT_UPDATED = "21041";
    public static final String UNABLE_TO_LOAD_UTIL_CLASS = "21042";

    // Entries for CP2P through web
    public static final String CP2P_WEB_REGISTRATION_SMS = "55555";
    public static final String CP2P_WEB_FORGOTPASSWORD_SMS = "55551";

    // Error code if default card group not exist in the system wrt service type
    public static final String P2P_ERROR_DEFAULT_CARDGROUP_NOTEXIST = "2077";

    // vikram for VFE last X transfers
    public static final String LAST_XTRF_SUBKEY = "24120";
    public static final String LAST_XTRF_MAIN_KEY = "24121";

    // vikram for VFE Customer enquiry
    public static final String LAST_XCUST_ENQ_SUBKEY = "24122";
    public static final String LAST_XCUST_ENQ_MAIN_KEY = "24123";

    public static final String CHNL_USER_PIN_MODIFY_STAFF = "25028";
    public static final String CHNL_TRF_SUCCESS_STAFF = "25029";
    public static String CHNL_WITHDRAW_SUCCESS_STAFF = "25030";
    public static String CHNL_RETURN_SUCCESS_STAFF = "25031";
    public static String SMS_LOGINID_NOT_FOUND = "25032";
    public static String DAILY_TRANSFER_LIST_SUCCESS_STAFF = "25033";
    public static String NO_USER_EXIST = "25034";
    // Error codes for Daily Subscriber Count process
    public static final String DLYSUBCNT_ERROR_EXCEPTION = "4450";
    public static final String DLYSUBCNT_EXECUTED_UPTO_DATE_NOT_FOUND = "4451";
    public static final String DLYSUBCNT_ALREADY_EXECUTED_TILL_TODAY = "4452";
    // Error codes for User Balance Movement process
    public static final String USRBALMOVT_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "4453";
    public static final String USRBALMOVT_COULD_NOT_UPDATE_MAX_DONE_DATE = "4454";
    public static final String USRBALMOVT_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "4455";
    public static final String USRBALMOVT_ERROR_EXCEPTION = "4456";
    public static final String USRBALMOVT_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE = "4457";
    public static final String C2S_ERROR_MISSING_MANDATORY_FIELD = "25027";

    // error code for direct payout
    public static final String DP_OPT_CHNL_TRANSFER_SMS1 = "8882";
    public static final String DP_OPT_CHNL_TRANSFER_SMS2 = "8883";
    public static final String DP_OPT_CHNL_TRANSFER_SMS3 = "8884";
    public static final String DP_OPT_CHNL_TRANSFER_SMS_BALSUBKEY = "9897";
    public static final String DP_OPT_CHNL_TRANSFER_CANCEL_TXNSUBKEY = "9898";
    public static final String DP_OPT_CHNL_TRANSFER_CANCEL_BALSUBKEY = "9899";

    public static final String DIR_NOT_EXIST = "4593";
    public static final String DIRECT_PAY_OUT_GENERAL_EXCEPTION = "4594";
    public static final String DPO_CONFIGUARATION_ERROR = "4595";
    public static final String NETWORK_STOCK_NOT_EXIST = "4596";
    public static final String ERROR_MOVING_FILE_TO_FINAL_DIR = "4597";
    public static final String INVALID_FILE_NAME = "4598";
    public static final String INVALID_PRODUCT_CODE = "4599";
    public static final String DP_PROCESS_IS_ALREADY_EXECUTING = "9051";

    public static final String LAST_TRF_DETAILS = "24124";
    // added by vikram for last 3 transfer details
    public static final String LASTX_TRANSFER_NO_TRANSACTION_DONE = "24220";
    public static String STAFF_WEB_SMSPIN_ACTIVATE = "24221"; // only PIN is
                                                              // active with no
                                                              // MSISDN

    public static final String C2SSUBSCRIBER_SENDPASSWORD_STAFF = "24222";
    public static final String C2SSUBSCRIBER_UNBLOCKSENDPASSWORD_STAFF = "24223";
    public static final String C2SSUBSCRIBER_UNBLOCKPASSWORD_STAFF = "24224";
    public static final String C2SSUBSCRIBER_RESETPASSWORD_STAFF = "24225";

    public static final String C2S_PARENT_SUCCESS = "24226";
    public static final String C2S_PARENT_SUCCESS_BILLPAY = "24227";

    public static final String INVALID_ACCESS_TIME = "24228";
    public static final String INVALID_POS_MSISDN = "24229";

    public static final String HTTPS_PARAMETER_ERROR = "24230";

    // low balance alert for parent
    public static final String LOW_BAL_ALERT_PARENT_ALREADY_EXECUTED = "14500";
    public static final String LOW_BAL_ALERT_PARENT_EXECUTED_UPTO_DATE_NOT_FOUND = "14501";
    public static final String LOW_BALANCE_ALERT_PARENT_MSG = "14502";
    public static final String LOW_BALANCE_ALERT_PARENT_MSG_SUBKEY = "14503";

    public static final String INVALID_RECEIVER_MSISDN = "14504";

    public static final String CHNL_USER_PWD_MODIFY_STAFF = "14505";
    public static final String CHNL_USER_PWD_AND_PIN_MODIFY_STAFF = "14506";
    public static final String CHNL_USER_LOGIN_AND_PWD_AND_PIN_MODIFY_STAFF = "14507";
    public static final String CHNL_USER_LOGIN_MODIFY_STAFF = "14508";
    public static final String CHNL_USER_LOGIN_AND_PWD_MODIFY_STAFF = "14509";
    public static final String CHNL_USER_LOGIN_AND_PIN_MODIFY_STAFF = "14510";
    public static final String PIN_MODIFY_STAFF = "14511";
    public static final String CHNL_USER_DEREGISTER_STAFF = "14513";
    public static final String USER_WEB_ACTIVATE_STAFF = "14512";

    public static final String P2P_ERROR_CARD_DETAILS_INVALIDMESSAGEFORMAT = "14514";
    public static final String INVALID_CARD_TYPE = "14515";
    public static final String INVALID_EXPIRY_DATE = "14516";
    public static final String INVALID_NICK_NAME = "14517";
    public static final String INVALID_BANK_NAME = "14518";
    public static final String SUBSCRIBER_NOT_REGISTERED = "14519";
    public static final String CARD_DETAILS_INSERTION_ERROR = "14520";
    public static final String CARD_DETAILS_INSERTION_SUCCESSFUL = "14521";
    public static final String CARD_DETAILS_ENCRYPTION_ERROR = "14522";
    public static final String INVALID_CARD_NUMBER = "14523";
    public static final String CARD_DETAILS_INSERTION_FAILED = "14524";
    public static final String CARD_DETAILS_ALREADY_EXIST = "14525";
    public static final String NICK_NAME_ALREADY_EXIST = "14526";

    public static final String INVALID_CVV_NUMBER = "14527";
    public static final String WRONG_NICK_NAME = "14528";
    public static final String P2P_CARD_DETAILS_RECEIVER_AMBIGOUS_MESSAGE = "14529";
    public static final String P2P_CARD_DETAILS_RECEIVER_FAIL_MESSAGE = "14530";
    public static final String P2P_CARD_DETAILS_RECEIVER_GET_NUMBER_BACK_SUCCESS = "14531";
    public static final String P2P_CARD_DETAILS_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS = "14532";
    public static final String P2P_CARD_DETAILS_RECEIVER_SUCCESS = "14533";
    public static final String P2P_CARD_DETAILS_RECEIVER_SUCCESS_WITH_BONUS = "14534";
    public static final String P2P_CARD_DETAILS_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "14535";
    public static final String P2P_CARD_DETAILS_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "14536";
    public static final String P2P_CARD_DETAILS_SENDER_SUCCESS_WITHOUT_ACCESSFEE = "14537";
    public static final String P2P_CARD_DETAILS_SENDER_SUCCESS = "14538";
    public static final String P2P_CARD_DETAILS_SENDER_SUCCESS_WITHOUT_POSTBAL = "14539";
    public static final String P2P_CARD_DETAILS_SENDER_UNDERPROCESS = "14540";
    public static final String P2P_CARD_DETAILS_SENDER_CREDIT_BACK = "14541";
    public static final String P2P_CARD_DETAILS_SENDER_CREDIT_BACK_WITHOUT_POSTBAL = "14542";
    public static final String P2P_CARD_DETAILS_SENDER_UNDERPROCESS_B4VAL = "14543";
    public static final String P2P_CARD_DETAILS_RECEIVER_UNDERPROCESS = "14544";
    public static final String DEFAULT_CARD_NOT_FOUND = "14545";
    public static final String DEFAULT_CARD_DETAILS_UPDATION_ERROR = "14546";
    // scheduled user balance report
    public static final String USER_BALANCE_SCHEDULE_PARAMETER_LOADED = "14504";
    public static final String USER_BALANCE_SCHEDULE_EXCEPTION = "14505";
    public static final String USER_BALANCE_SCHEDULE_EXECUTED_SUCCESS = "14506";

    public static final String USER_REGISTRATION_FAILED = "15507";
    public static final String USER_REGISTRATION_SUCCESS = "15500";
    public static final String USER_TRANSFER_RULE_NOT_EXIST_BTWEEN_CATEGORIES = "15501";
    public static final String USER_TRANSFER_RULE_NOT_EXIST = "15502";
    public static final String CHN_USR_REG_LEAF_CATEGORY = "15503";
    public static final String CHN_USR_REG_INVALID_REQUEST_FORMAT = "15504";
    public static final String ERROR_CHNL_USER_LOGINID_ALREADY_EXIST = "15505";
    public static final String ERROR_CHNL_USER_MSISDN_ALREADY_EXIST = "15506";
    // added by nilesh:user deletion through USSD
    public static final String USSD_USRDEL_CHILD_EXIST_ERROR = "20000";
    public static final String USSD_USRDEL_USRMSISDN_INVALID = "20001";
    public static final String USSD_USRDEL_BALANCE_EXIST_ERROR = "20002";
    public static final String USSD_USRDEL_O2CTXN_EXIST_ERROR = "20003";
    public static final String USSD_USRDEL_FOCTXN_EXIST_ERROR = "20004";
    public static final String USSD_USRDEL_EXTCODE_USRMSISDN_INVALID = "20005";
    public static final String USSD_USRDEL_REQ_FAILED = "20006";
    public static final String USSD_USRDEL_RESTRICTED_LIST_EXIST_ERROR = "20007";
    public static final String USSD_USRDEL_MSG_PARENT = "20008";
    public static final String USSD_USRDEL_MSG_OWNER = "20009";
    public static final String USSD_USRDEL_APP_FAILED = "20010";
    public static final String USSD_USRDEL_MESSAGE_INVALID = "20011";
    public static final String USSD_USRDEL_STATUS_AS_DR = "20012";
    public static final String USSD_USRDEL_MSG_CHILD = "20013";
    // added by nilesh : Auto c2c
    public static final String C2C_NOT_ALLOWED_TO_ROOT_USR = "20017";
    public static final String AUTO_CHNL_ERROR_SNDR_INSUFF_BALANCE = "20018";
    public static final String SNDR_INSUFF_BALANCE = "20019";
    public static final String AUTO_C2C_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "20020";

    // For MVD DOWNLOAD..
    public static final String MVD_DOWNLOAD_MSG = "99999";
    // added by rahul for c2s transfer enquiry service amount datewise
    public static final String C2S_ERROR_TRSFER_ENQ_INVALIDMESSAGEFORMAT = "8516";
    public static final String C2S_ERROR_NO_TXN_FOUND_IN_DATE_AMT_RANGE = "8517";
    public static final String LAST_TRANSFER_ENQ_MSG = "8518";
    public static final String LAST_TRANSFER_ENQ_LIST_SUCCESS = "8519";

    // user creation from ussd
    public static final String CATEGORY_NOT_EXIST = "1001001";
    public static final String USSD_USER_CREATION_ERROR = "1001002";
    public static final String USSD_USER_CREATION_NOT_ALLOWED = "1001003";
    public static final String DEFAULT_GRADE_NOT_FOUND = "1001004";
    public static final String DEFAULT_COMM_PRF_NOT_FOUND = "1001005";
    public static final String DEFAULT_TRF_PRF_NOT_FOUND = "1001006";
    public static final String DEFAULT_GEO_NOT_FOUND = "1001007";
    public static final String DEFAULT_ROLES_NOT_FOUND = "1001008";
    public static final String USER_NOT_IN_HIERARCHY = "1001009";
    public static final String LOGINID_ALREADY_EXIST = "1001010";
    public static final String EXTERNAL_CODE_ALREARY_EXIST = "1001011";
    public static final String PORTED_MSISDN = "1001012";
    public static final String USER_ALREADY_EXIST = "1001013";
    // added by shashank for CRBT Services
    // public static final String CRBT_ERROR_BLANK_SONGCODE="1001014";

    public static final String EXT_USER_CREATION_USER_MSG = "1001114";
    public static final String EXT_USER_CREATION_PARENT_MSG = "1001115";
    public static final String EXT_USER_CREATION_SENDER_MSG = "1001116";

    // public static final String VOUCHER_ERROR_FILE_ALREADY_UPLOADED="5948";

    // added by ankuj for OMT CR
    public static final String ERROR_NULL_TXNTYPE = "1003001";
    public static final String ERROR_INVALID_DATE_RANGE = "1003002";
    public static final String ERROR_COUNT_EXCEEDS_LIMIT = "1003003";
    public static final String ERROR_INVALID_MESSAGE_FORMAT = "1003004";
    public static final String ERROR_NULL_NWCODE = "1003005";
    public static final String NO_RECORD_AVAILABLE = "1003006";
    public static final String NO_RECORDS_FOUND = "1003007";

    // added by jasmine kaur
    public static String INVALID_SID_LENGTH = "1002001";
    public static String INVALID_SID_REG_MSG_FORMAT = "1002002";
    public static String SID_IS_NOT_NUMERIC = "1002003";
    public static String SID_IS_NOT_ALPHANUMERIC = "1002004";
    public static String SID_ALREADY_EXISTING = "1002005";
    public static String OLD_SID_NOT_MATCHED = "1002006";
    public static String NOT_VAILD_SID_MODIFICATION = "1002007";
    public static String OLD_SID_AND_NEW_SID_SAME = "1002008";
    public static String OLDSID_NEWSID_SAME = "1002009";
    public static final String SUCCESS_SID = "1002110";
    public static final String ALREADY_REGISTERED = "1002020";
    public static final String MSISDN_NOT_EXIST = "1002021";
    // added by Ankuj
    public static final String ERROR_DELETE_SID_INVALID_MESSAGE_FORMAT = "1002011";
    public static final String ERROR_DELETE_SID_NULL = "1002012";
    public static final String SID_LENGTHINVALID = "1002014";
    public static final String MESSAGE_LENGTH_SID = "15511";
    public static final String SID_INVALID = "1002013";
    public static final String MSISDN_NULL = "1002015";
    public static final String ERROR_SNDR_INVALID_MSISDN = "1002016";
    public static final String ERROR_DELETE_SID_NORECORD = "1002017";
    public static final String TXN_SUCCESS_RESPONSE_MESSAGE = "1002118";
    public static final String ERROR_DELETE_MSISDN_NORECORD = "1002019";
    public static final String C2S_ERROR_INVALID_AUTH_PARAMETER = "4340";
    // Added by Babu Kunwar For VFE
    public static final String RP2P_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "1001201";
    public static final String RP2P_COULD_NOT_UPDATE_MAX_DONE_DATE = "1001202";
    public static final String RP2P_ERROR_EXCEPTION = "1001203";
    public static final String RP2P_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE = "1001204";
    public static final String COULD_NOT_CREATE_DIRECTORY = "1001205";
    public static final String UNABLE_TO_MOVE_FILES = "1001206";

    // PostPaid Bill Deposit on 28/12/10 for Citycell
    public static final String C2S_RECEIVER_SUCCESS_BILLDEPOSIT = "9938";
    public static final String C2S_SENDER_SUCCESS_BILLDEPOSIT = "9939";
    public static final String C2S_RECEIVER_UNDERPROCESS_BILLDEPOSIT = "9940";
    public static final String C2S_SENDER_UNDERPROCESS_BILLDEPOSIT = "9941";
    public static final String C2S_RECEIVER_AMBIGOUS_KEY_BILLDEPOSIT = "9942";
    public static final String C2S_RECEIVER_FAIL_KEY_BILLDEPOSIT = "9943";
    public static final String P2P_RECEIVER_SUCCESS_WITHOUT_VALIDITY = "260";

    // Multiple dedicated account transfer
    public static final String SUCCESS_DEDICATED_ACC = "11001";
    public static final String FAIL_DEDICATED_ACC = "11002";
    public static final String P2P_RECEIVER_SUCCESS_DEDICATED_ACC_MINUTES = "11003";
    public static final String P2P_RECEIVER_SUCCESS_DEDICATED_ACC_EGP = "11004";
    // Channel User Suspend Resume service through USSD
    public static final String CCE_USER_STATUS_RESUME_SUCCESS_S = "1001101";
    public static final String CCE_USER_STATUS_RESUME_SUCCESS_R = "1001102";
    public static final String CCE_USER_STATUS_SUSPEND_SUCCESS_S = "1001103";
    public static final String CCE_USER_STATUS_SUSPEND_SUCCESS_R = "1001104";

    // added for uploading of messages in DB through script
    public static final String MESSAGE_SCRIPT_NUMBER_ARG_MISSING = "3005001";
    public static final String MESSAGE_SCRIPT_ERROR_FILEPATH_NULL = "3005002";
    public static final String MESSAGE_SCRIPT_ERROR_CONN_NULL = "3005203";
    public static final String MESSAGE_SCRIPT_MISSING_CONST_FILE = "3005004";
    public static final String MESSAGE_SCRIPT_MISSING_LOG_FILE = "3005005";
    public static final String MESSAGE_SCRIPT_UPLOAD_PROCESS_GENERAL_ERROR = "3005206";

    // for DrCr Transfer Through External Gateway
    public static final String CHNL_CREDIT_TRANSFER_SUCCESS_S = "1007101";
    public static final String CHNL_DEBIT_TRANSFER_SUCCESS_R = "1007102";
    public static final String CHNL_CREDIT_TRANSFER_SUCCESS_R = "1007103";
    public static final String CHNL_DEBIT_TRANSFER_SUCCESS_S = "1007104";
    public static final String INVALID_TXN_TYPE = "1007005";

    // For MRP Block Time
    public static final String MRP_BLOCK_TIME = "3006201";
    public static final String MRP_BLOCK_TIME_WID_SERVICE_TYPE = "3006202";

    // Channel User Suspend Resume service through SMS/ussd/ExtGateway

    public static final String CCE_USER_STATUS_SUSPEND_REQUEST_S = "1008101";
    public static final String CCE_USER_STATUS_SUSPEND_REQUEST_R = "1008102";
    public static final String CCE_USER_SUSPENDED_APPROVAL_PENDING = "1008103";

    // Entries for GiveMeBalance through USSD
    public static final String P2PGMB_INVALID_MESSAGE = "2010201";
    public static final String P2PGMB_ERROR_AMOUNT_NOT_NUMERIC = "2010202";
    public static final String P2PGMB_ERROR_INVALID_MSISDN1 = "2010203";
    public static final String P2PGMB_ERROR_INVALID_MSISDN2 = "2010204";
    public static final String P2PGMB_ERROR_UNSUPPORTED_NETWORK_MSISDN1 = "2010205";
    public static final String P2PGMB_ERROR_UNSUPPORTED_NETWORK_MSISDN2 = "2010206";
    public static final String P2PGMB_ERROR_TIME_OUT = "2010207";
    public static final String USSD_CELLID_BLANK_ERROR = "2010208";
    public static final String USSD_SWITCHID_BLANK_ERROR = "2010209";
    public static final String P2PGMB_TYPE_BLANK = "2010210";
    public static final String P2PGMB_MSISDN1_BLANK = "2010211";
    public static final String P2PGMB_MSISDN2_BLANK = "2010212";
    public static final String P2PGMB_MSISDN1_MSISDN2_EQUAL = "2010213";

    // New error codes for user creation, modification, suspend, and resume
    // request from external system.
    public static final String EXTSYS_REQ_INVALID_FORMAT = "1004001";
    public static final String EXTSYS_REQ_DATE_BLANK = "1004002";
    public static final String EXTSYS_REQ_DATE_INVALID_FORMAT = "1004003";
    public static final String EXTSYS_REQ_EXTNWCODE_BLANK = "1004004";
    public static final String EXTSYS_REQ_PARENTMSISDN_BLANK = "1004005";
    public static final String EXTSYS_REQ_PRIMARY_MSISDN_BLANK = "1004006";// MSISDNS
                                                                           // tag
                                                                           // is
                                                                           // not
                                                                           // coming
                                                                           // in
                                                                           // the
                                                                           // request.
    public static final String EXTSYS_REQ_USERCATCODE_BLANK_OR_LENGTH_EXCEEDS = "1004007";
    public static final String EXTSYS_REQ_USERCATCODE_INVALID = "1004008";
    public static final String EXTSYS_REQ_USERNAME_BLANK_OR_LENGTH_INVALID = "1004009";
    public static final String EXTSYS_REQ_MSISDN_INVALID_FORMAT = "1004010";
    public static final String EXTSYS_REQ_EXTNWCODE_INVALID = "1004011";
    public static final String EXTSYS_REQ_USR_MSISDNS_LIST_INVALID = "1004012";
    public static final String EXTSYS_REQ_EXTREFNUM_LENGTH_EXCEEDS = "1004013";
    public static final String EXTSYS_REQ_EXTREFNUM_NON_NUMERIC = "1004014";
    public static final String EXTSYS_REQ_SHORTNAME_LENGTH_EXCEEDS = "1004015";
    public static final String EXTSYS_REQ_USERNAMEPREFIX_BLANK_OR_LENGTH_EXCEEDS = "1004016";
    public static final String EXTSYS_REQ_SUBSCRIBERCODE_BLANK_OR_LENGTH_EXCEEDS = "1004017";
    public static final String EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS = "1004018";
    public static final String EXTSYS_REQ_CONTACTPERSON_LENGTH_EXCEEDS = "1004019";
    public static final String EXTSYS_REQ_CONTACTNUMBER_LENGTH_EXCEEDS = "1004020";
    public static final String EXTSYS_REQ_CONTACTNUMBER_NON_NUMERIC = "1004021";
    public static final String EXTSYS_REQ_SSN_LENGTH_EXCEEDS = "1004022";
    public static final String EXTSYS_REQ_ADDRESS1_LENGTH_EXCEEDS = "1004023";
    public static final String EXTSYS_REQ_ADDRESS2_LENGTH_EXCEEDS = "1004024";
    public static final String EXTSYS_REQ_CITY_LENGTH_EXCEEDS = "1004025";
    public static final String EXTSYS_REQ_STATE_LENGTH_EXCEEDS = "1004026";
    public static final String EXTSYS_REQ_COUNTRY_LENGTH_EXCEEDS = "1004027";
    public static final String EXTSYS_REQ_EMAILID_LENGTH_EXCEEDS = "1004028";
    public static final String EXTSYS_REQ_EMAILID_INVALID_FORMAT = "1004029";
    public static final String EXTSYS_REQ_WEBLOGINID_BLANK = "1004030";
    public static final String EXTSYS_REQ_WEBLOGINID_ALREADY_EXIST = "1004031";
    public static final String EXTSYS_REQ_WEBPASSWORD_INVALID = "1004032";
    public static final String EXTSYS_REQ_ACTUAL_NW_CODE_INVALID = "1004033";
    public static final String EXTSYS_REQ_USR_PARENT_NOT_EXIST = "1004034";
    public static final String EXTSYS_REQ_USR_PARENT_NOT_ACTIVE = "1004035";
    public static final String EXTSYS_REQ_USR_REGISTRATION_FAILED = "1004036";
    public static final String EXTSYS_REQ_USR_DEFAULT_GRADE_NOT_FOUND = "1004037";
    public static final String EXTSYS_REQ_USR_DEFAULT_COMMISSION_PROFILE_NOT_FOUND = "1004038";
    public static final String EXTSYS_REQ_USR_DEFAULT_TRANSFER_PROFILE_NOT_FOUND = "1004039";
    public static final String EXTSYS_REQ_USR_SERVICES_NOT_FOUND = "1004040";
    public static final String EXTSYS_REQ_USR_ROLES_NOT_FOUND = "1004041";
    public static final String EXTSYS_REQ_PARENTMSISDN_INVALID_FORMAT = "1004042";
    public static final String EXTSYS_REQ_NEW_PRIMARY_MSISDN_BLANK = "1004043";
    public static final String EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS = "1004044";
    public static String EXTSYS_REQ_USER_MSISDN_BLANK = "1004045";
    public static String EXTSYS_REQ_USER_MSISDN_NOT_FOUND = "1004046";
    public static String EXTSYS_REQ_NEW_MSISDN_ALREADY_EXIST = "1004047";
    public static String EXTSYS_REQ_NEW_WEBLOGINID_ALREADY_EXIST = "1004048";
    public static String EXTSYS_REQ_USR_MODIFICATION_FAILED = "1004049";
    public static String EXTSYS_REQ_ACTION_BLANK = "1004050";
    public static String EXTSYS_REQ_ACTION_LENGTH_EXCEEDS = "1004051";
    public static String EXTSYS_REQ_ACTION_INVALID_VALUE = "1004052";
    public static String EXTSYS_REQ_USR_ALREADY_SUSPENDED = "1004053";
    public static String EXTSYS_REQ_USER_NOT_EXIST = "1004054";
    public static String EXTSYS_REQ_USR_ALREADY_ACTIVE = "1004055";
    public static String EXTSYS_REQ_USR_SUS_RES_FAILED = "1004056";
    public static final String EXTSYS_REQ_PARENT_CAT_NOT_ALLOWED = "1004057";
    public static final String EXTSYS_REQ_TRF_RULE_NOT_ALLOWED = "1004058";

    // Added for MOLDOVA: Reference ID should be unique and non-numeric in each
    // external gateway request for Recharge request.
    // If received external reference number is not numeric.
    public static final String EXTREFNUM_BLANK = "1017001";
    public static final String EXTREFNUM_LENGTH_EXCEEDS = "1017002";
    public static final String EXTREFNUM_NOT_NUMERIC = "1017003";
    // If received external reference number already exist in the system.
    public static final String EXTREFNUM_ALREADY_EXIST = "1017004";

    // added for messageGatewayForCategoryCache, transferprofileCahce,
    // transferProfileProductCache
    public static final String ERROR_NOTFOUND_MESSAGE_GATEWAY_FOR_CATEGORY = "3019001";
    public static final String ERROR_TRANSFER_PROFILE_FOR_ID_NOTFOUND = "3019002";
    public static final String ERROR_TRANSFER_PROFILE_PRODUCT_FOR_ID_NOTFOUND = "3019003";

    // Multiple Credit CR

    public static final String P2P_MULT_CDT_LIST_AMD_ALREADYREGD = "1002201";
    public static final String P2P_MULT_CDT_LIST_AMD_INVALID_MSGFT = "1002202";
    public static final String P2P_ERROR_MCD_LIST_MSISDN_BLANK = "1002203";
    public static final String P2P_ERROR_MCD_LIST_MSISDN_NOTINRANGE = "1002204";
    public static final String P2P_ERROR_MCD_LIST_MSISDN_LEN_NOTSAME = "1002205";
    public static final String P2P_ERROR_MCD_LIST_ACTION_BLANK = "1002206";
    public static final String P2P_ERROR_MCD_LIST_ACTION_INVALID = "1002207";
    public static final String P2P_ERROR_MCD_LIST_ADD_SUCCESS = "1002208";
    public static final String P2P_ERROR_MCD_LIST_ADD_FAIL_MSISDN = "1002209";
    public static final String P2P_ERROR_MCD_LIST_ADD_FAIL = "1002210";
    public static final String P2P_ERROR_MCD_LIST_DELETE_NOTPRESENT = "1002211";
    public static final String P2P_ERROR_MCD_LIST_DELETE_SUCCESS = "1002212";
    public static final String P2P_ERROR_MCD_LIST_DELETE_FAIL = "1002213";
    public static final String P2P_ERROR_MCD_LIST_NO_SUBSCRIBER = "1002214";
    public static final String P2P_ERROR_MCD_LIST_VIEW_NO_RECORD = "1002215";
    public static final String P2P_ERROR_MCD_LIST_VIEW_SUCCES = "1002216";
    public static final String P2P_ERROR_MCD_LIST_VIEW_FAIL = "1002217";
    public static final String P2P_ERROR_MCD_LIST_SELECTOR_REQUIRED = "1002218";
    public static final String P2P_ERROR_MCD_LIST_SELECTOR_INVALID = "1002219";
    public static final String P2P_MCDL_AMT_BLANK = "1002220";
    public static final String P2P_MCDL_AMT_NOTNUMERIC = "1002221";
    public static final String P2P_MCDL_AMT_LESSTHANZERO = "1002222";
    public static final String P2P_MCDL_INVALID_OPERATION = "1002223";
    public static final String P2P_ERROR_MCD_LIST_MSISDN_NOTNUMERIC = "1002224";
    public static final String P2P_ERROR_MCD_LIST_RECR_NOTFOUND_RECEIVERNETWORK = "1002225";
    public static final String P2P_ERROR_MCD_LIST_COUNT_EXCEED = "1002226";
    public static final String P2P_ERROR_MCD_LIST_NAME_BLANK = "1002227";
    public static final String P2P_ERROR_MCD_LIST_LENGTH_EXCEED = "1002228";
    public static final String P2P_ERROR_MCD_LIST_ALLOWED_ALPHANUMERIC = "1002229";
    public static final String P2P_MCDL_PIN_REQUIRED = "1002230";
    public static final String MCDL_SUCCESS_DEDICATED_ACC = "1002231";
    public static final String MCDL_FAIL_DEDICATED_ACC = "1002232";
    public static final String P2P_MCDL_RECEIVER_SUCCESS_DEDICATED_ACC_MINUTES = "1002233";
    public static final String P2P_MCDL_RECEIVER_SUCCESS_DEDICATED_ACC_EGP = "1002234";
    public static final String P2P_MCDL_AMT_MAX_LIMIT = "1002235";
    public static final String P2P_MCDL_NEW_USER_REG = "1002236";
    public static final String P2P_ERROR_MCD_LIST_NETWORK_NOTFOUND = "1002237";
    public static final String P2P_MULT_CDT_LIST_AMD_SENDER_MSISDN_NOTALLOWD = "1002238";

    // for LMB debit API
    public static final String LMB_AMT_DIFF_AT_IN = "2023001";
    public static final String LMB_NOT_SETTLED = "2023002";
    public static final String LMB_INSUFF_BALANCE = "2023003";

    // added by harsh on 10Aug12
    public static final String DELETE_BUDDYLIST_SUCCESS = "2018101";
    public static String BUDDYLIST_NOT_FOUND = "2018002";
    public static final String DELETE_BUDDYLIST_FAILED = "2018003";
    public static final String MULT_DEL_BUDDY_UNDERPROCESS = "2018004";

    public static final String PIN_NOT_FOUND = "1027001";
    public static final String XML_PASSWORD_NOT_FOUND = "1027002";
    public static final String XML_ERROR_INVALIDREQUESTFORMAT = "1027003";
    public static final String USER_NOT_EXIST = "1027004";
    public static final String INVALID_PIN = "1027005";

    public static final String CARD_GROUP_START_AND_END_RANGE_DIFFERENT = "1029001";
    public static final String AMOUNT_REQUIRED = "1029002";

    public static final String EXT_GRPH_INVALID_DATE = "1020010";
    public static final String EXT_USRADD_INVALID_DATE = "1021037";
    public static final String EXT_USRADD_RULETYPE_MANADATORY = "1021038";
    public static final String EXT_TRF_RULE_TYPE_INVALID_DATE = "1026006";
    public static final String AUTO_O2C_PROCESS = "2029101";

    // for barred for delete
    public static final String BAR_FOR_DEL_PROCESS_ALREADY_EXECUTED = "1031001";
    public static final String DATA_NOT_UPDATED = "1031002";
    public static final String BAR_FOR_DEL_EXCEPTION = "1031003";
    public static final String ASS_PROD_NOT_FOUND = "1031004";
    public static final String BARRED_USER_DELETED = "1037105";

    // Added for Subscriber Threshold Enquiry by VikasJ
    public static final String P2P_SUBSCRIBER_NOT_RGISTERED_FOR_ENQUIRY = "2030001";
    public static final String P2P_SUBSCRIBER_THRESHOLD_ENQUIRY_ERROR = "2030002";
    public static final String P2P_SUBSCRIBER_THRESHOLD_NOT_UPDATED = "2030003";
    public static final String P2P_SUBSCRIBER_PROFILEID_NULL = "2030004";
    public static final String SERVICECLASS_NOT_USED_IN_SYSTEM = "2030005";
    public static final String SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM = "2030006";

    // added by akanksha for ethiopia telecom
    public static final String C2S_EXTGW_BLANK_SELECTOR = "1234";
    // added by harsh to validate usermsisdn field while channel user creation
    // through USSD
    public static final String INVALID_USERMSISDN = "1025002";
    public static final String GMB_PLAIN_SMS_SUCESS_S = "2222021";
    public static final String GMB_PLAIN_SMS_SUCESS_R = "2222022";

    public static final String PIN_RECEIVER_FAIL = "217_PIN";// 217;
    public static final String PIN_RECEIVER_FAIL_KEY = "231_PIN";// 231

    public static final String PIN_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "237_PIN";// 237
    public static final String PIN_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "603_PIN";// 603
    public static final String PIN_RECEIVER_SUCCESS = "207_PIN";// 207
    public static final String PIN_RECEIVER_SUCCESS_WITH_BONUS = "236_PIN";// 236
    public static final String PIN_RECEIVER_SUCCESS_ALL_BALANCES = "243_PIN";// 243
    public static final String PIN_RECEIVER_UNDERPROCESS = "208_PIN";// 208
    public static final String PIN_RECEIVER_AMBIGOUS_KEY = "230_PIN";// 230
    public static final String PIN_RECEIVER_UNDERPROCESS_SUCCESS = "204_PIN";// 204
    public static String PIN_IMPLICIT_MSG = "242_PIN"; // 242

    public static final String PIN_SENDER_SUCCESS = "210_PIN";
    public static final String PIN_SENDER_UNDERPROCESS = "209_PIN";
    public static final String PIN_SENDER_UNDERPROCESS_B4VAL = "216_PIN";
    public static final String PIN_SENDER_CREDIT_SUCCESS = "221_PIN";
    public static final String PIN_SENDER_UNDERPROCESS_SUCCESS = "203_PIN";
    public static final String PIN_PARENT_SUCCESS = "24226_PIN";

    public static final String P2P_MCDL_ALLOWED_FREQUENCY = "2031101";
    public static final String P2P_MCDL_INVALID_SCHEDULETYPE = "2031102";
    public static final String P2P_MULT_CDT_BATCH_AMD_ALREADYREGD = "2031103";
    public static final String P2P_ERROR_MCD_SCTYPE_BLANK = "2031104";
    public static final String P2P_ERROR_MCD_SCTYPE_LENGTH_EXCEED = "2031105";
    public static final String P2P_ERROR_MCD_NOSC_LENGTH_EXCEED = "2031106";
    public static final String P2P_MCDL_INVALID_FREQUENCY = "2031107";
    public static final String P2P_ERROR_SMCD_LIST_ADD_SUCCESS_W = "2031108";
    public static final String P2P_ERROR_SMCD_LIST_ADD_FAIL_MSISDN = "2031109";
    public static final String P2P_ERROR_SMCD_LIST_VIEW_SCTYPE_LISTNAME_REQUIRED = "2031110";
    public static final String P2P_SENDER_SCT_SUCCESS_W = "2031111";
    public static final String P2P_SENDER_SCT_FAIL_W = "2031112";
    public static final String P2P_RECEIVER_SCT_SUCCESS_W = "2031113";
    public static final String P2P_SENDER_SCT_RECORDS_W = "2031114";
    public static final String P2P_SENDER_MIN_RESI_BAL_CHECK_FAILED_W = "2031115";
    public static final String P2P_SCT_SUCC_FAIL_DEL_BUDDY = "2031116";
    public static final String P2P_SENDER_MAX_PCT_TRANS_FAILED_W = "2031117";
    public static final String P2P_ERROR_SMCD_LIST_ADD_SUCCESS_R_W = "2031118";
    public static final String P2P_ERROR_SMCD_LIST_DEL_SUCCESS_R_W = "2031119";
    public static final String P2P_RECEIVER_SMCD_FAIL_MESSAGE_KEY_W = "2031120";
    public static final String P2P_SENDER_SCT_SUCCESS_SUBKEY = "2031121";
    public static final String P2P_ERROR_SMCD_LIST_ADD_SUCCESS_SUBKEY = "2031122";
    public static final String P2P_SENDER_SCT_RECORDS_SUBKEY = "2031123";
    public static final String P2P_SENDER_SCT_FAIL_SUBKEY = "2031124";
    public static final String P2P_SENDER_SCT_INSUFF_SUBKEY_W = "2031125";
    public static final String P2P_SENDER_SCT_ALLFAIL_SUBKEY_W = "2031126";
    public static final String P2P_SENDER_SCT_ALLINSUFF_SUBKEY_W = "2031127";
    public static final String P2P_SENDER_SCT_MIXINSUFF_SUBKEY_W = "2031128";
    public static final String P2P_ERROR_SMCD_LIST_ADD_SUCCESS_R_M = "2031129";
    public static final String P2P_ERROR_SMCD_LIST_DEL_SUCCESS_R_M = "2031130";
    public static final String P2P_ERROR_SMCD_LIST_ADD_SUCCESS_M = "2031131";
    public static final String P2P_SENDER_SCT_FAIL_M = "2031132";
    public static final String P2P_SENDER_SCT_RECORDS_M = "2031133";
    public static final String P2P_RECEIVER_SMCD_FAIL_MESSAGE_KEY_M = "2031134";
    public static final String P2P_SENDER_SCT_ALLFAIL_SUBKEY_M = "2031135";
    public static final String P2P_SENDER_SCT_MIXINSUFF_SUBKEY_M = "2031136";
    public static final String P2P_SENDER_SCT_SUCCESS_M = "2031137";
    public static final String P2P_SENDER_MIN_RESI_BAL_CHECK_FAILED_M = "2031138";
    public static final String P2P_SENDER_MAX_PCT_TRANS_FAILED_M = "2031139";
    public static final String P2P_SENDER_SCT_ALLINSUFF_SUBKEY_M = "2031140";
    public static final String P2P_SENDER_SCT_INSUFF_SUBKEY_M = "2031141";
    public static final String P2P_RECEIVER_SCT_SUCCESS_M = "2031142";
    // Change Password
    public static final String CHANGE_PASSWORD_OLD_NEW_SAME = "10060";
    public static final String CHANGE_PASSWORD_NEW_CONFIRM_NOTSAME = "10061";
    public static final String CHANGE_PASSWORD_NEWPASSWORD_EXIST_HIST = "10062";
    public static final String CHANGE_PASSWORD_NEWPASSWORD_LENGTH = "10063";
    public static final String CHANGE_PASSWORD_NEWPASSWORD_SAME_DIGIT = "10064";
    public static final String CHANGE_PASSWORD_NEWPASSWORD_CONSSECUTIVE = "10065";
    public static final String CHANGE_PASSWORD_SUCCESS = "10066";
    public static final String CHANGE_PASSWORD_FAILED = "10067";
    public static final String CHANGE_PASSWORD_LOGIID_MSISDN_BLANK = "10068";
    public static final String CHANGE_PASSWORD_SAME_LOGIID_PASSWORD = "10069"; // 01-APR-2014

    // public static final String USER_MODIFY_SUCCESS="10101";

    public static final String SMS_TO_AREA_ADMIN_USERS_HOURLY = "11005";
    public static final String PROMOTION_MESSAGE_NOTIFICATION = "1040009";

    // Added by Vikas Singh on 24-04-2014
    public static String CARD_MODIFY_FAILED = "2041001";
    public static String ERROR_NICK_NAME_MANDATORY = "2041002";
    public static String ERROR_NICK_NAME_EXCEED_LENGTH = "2041003";
    public static String ERROR_NICK_NAME_SP_CHARACTERS = "2041004";
    public static String CARD_DELETE_FAILED = "2041005";
    public static String CARD_MODIFY_SUCCESS = "2041110";
    public static String ERROR_INVALID_IMEI = "2041006";
    public static String INVALID_NEW_NICK = "2041007";
    public static String CARD_DELETE_SUCCESS = "2041111";
    public static String INVALID_OLD_NICK = "2041008";
    public static String ERROR_NEW_NICK_SAME_AS_OLD = "2041009";
    public static String INVALID_CREDITCARD_NUMBER = "2041012";
    public static String P2P_ERROR_INVALID_RECEIVER_MSISDN = "2041013";
    public static final String INVALID_EXPIRY_DATE_BEFORE = "1040005";
    public static final String ASSOCIATED_PROFILE_NOT_ACTIVE = "1040006";
    public static final String PARENT_USER_IS_NOT_ACTIVE = "1040007";
    public static final String NOT_ENOUGH_POINTS_TO_REDEMPTION = "1040008";
    public static final String LOYALTY_RECON_MESSAGE = "1021060";
    public static String INVALID_EMAILID = "2041014";
    public static String INVALID_IMEI = "2041015";
    public static final String CREDITCARD_LIST_NOTFOUND = "2041016";
    public static final String CREDITCARD_LIST_SUCCESS = "2041017";
    public static final String CREDITCARD_LIST_ERROR = "2041018";
    public static final String P2P_ERROR_CREDITCARDLIST_INVALIDMESSAGEFORMAT = "2041019";
    public static final String P2P_ERROR_CREDITCARDLIST_INVALID_IMEI = "2041020";
    public static final String P2P_SELFTOPUP_REGISTERATION_PREPAID_SUCCESS_WITHOUT_PIN = "2041021";
    // public static final String
    // NETWORK_PREFIX_SERVICE_MAPPING_NOT_FOUND="5961";
    public static String AUTO_TOPUP_REG_FAILED = "2041023";
    public static String AUTO_TOPUP_DATE_DIFF_FAILED = "2041024";
    public static String AUTO_TOPUP_WEEK_DAY_ERROR = "2041025";
    public static String AUTO_TOPUP_MONTH_DAY_ERROR = "2041026";
    public static String AUTO_TOPUP_DATE_FORMAT_ERROR = "2041027";
    public static String AUTO_TOPUP_INVALID_AMOUNT = "2041028";
    public static final String SUBSCRIBER_ACTIVE_SYSTEM = "1091006";
    public static final String P2P_REGISTERATION_SUCCESS = "2041140";
    public static final String NO_USER_FOUND_FOR_SCHEDULE_TOPUP = "1091001";
    public static final String SCHEDULE_TOPUP_PROCESS = "1091002";
    public static final String NO_INSTANCE_FOR_REQUESTED_NETWORK = "1091003";
    public static final String USER_MAX_RETRY_COUNT_REACHED = "1091005";
    public static String AUTO_TOPUP_NONNUMERIC_DAY = "2041029";
    public static String AUTO_TOPUP_REG_SUCCESSFUL = "2041130";
    public static String MAX_AUTO_TOPUP_AMT_RCHD = "2041031";
    public static final String EXTSYS_CHANGE_PIN_SUCCESS = "1040009";
    public static String INVALID_HOLDER_NAME = "2041050";
    public static final String PIN_REPETATION_INVALID = "4084";
    public static final String AUTO_TOPUP_SUB_ALREADY_REG = "2041051";
    public static final String AUTO_TOPUP_ALERT = "2041052";
    public static final String AUTO_TOPUP_DISABLE_SUCCESS = "2041153";
    public static final String AUTO_TOPUP_DISABLE_NO_USERID = "2041154";
    public static final String AUTO_TOPUP_END_DATE_DIFF_FAILED = "2041055";
    public static final String CARD_EXPIRY_DATE_CROSSED = "2041056";
    public static final String P2P_REGISTERATION_APP_PREPAID_SUCCESS = "2041157";
    public static final String INVALID_LENGTH_HOLDER_NAME = "2041058";
    public static String INVALID_NEW_NICK_ALREADY_SCHEDULED = "2041059";
    public static String SCHEDULE_TOPUP_USER_PRESENT_IN_DB_WITH_N_STATUS = "2041060";
    public static final String STU_REGISTERATION_PREPAID_SUCCESS_WITHOUT_PIN = "2041061";
    public static final String STU_REG_SUCCESS_WITH_PIN_WEB = "2041062";

}
