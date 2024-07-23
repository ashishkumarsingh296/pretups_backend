
package com.btsl.pretups.common;

public class PretupsErrorCodesI {

	// NOTE: Kindly DO NOT define value 0f 00000 as it stands for Database
	// connection failed defined in oracle util
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
	public static final String ERROR_INVALID_PSWD = "2206";// brajesh
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
	public static final String REMARKS_REQUIRED = "3031";
	public static final String WALLETTYPE_REQUIRED = "241160";
	public static final String WALLETTYPE_INVALID = "241161";

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

	public static final String USER_STATUS_NOTUPDATED = "4030";
	public static final String USER_STATUS_NOTACTIVE = "4031";
	public static final String USER_STATUS_NOTSUSPENDED = "4032";
	public static final String USER_STATUS_RESUME_SUCCESS = "4033";
	public static final String USER_STATUS_RESUME_FAILED = "4034";

	public static final String USER_STATUS_SUSPEND_SUCCESS = "4035";
	public static final String USER_STATUS_SUSPEND_FAILED = "4036";

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
	public static final String BUDDY_DOESNOT_EXIST = "4202";

	public static final String NO_TRANSACTION = "4220";
	public static final String ACCOUNT_STATUS_SUCCESS = "4221";
	public static final String ACCOUNT_STATUS_FAILED = "4222";
	public static final String TRANSFER_STATUS_SUCCESS = "4223";
	public static final String TRANSFER_STATUS_FAILED = "4224";
	public static final String TRANSFER_REPORT_SUCCESS = "4225";
	public static final String TRANSFER_REPORT_FAILED = "4226";

	public static final String BARRED_SUBSCRIBER_FAILED = "4227";
	public static final String BARRED_SUBSCRIBER_SUCCESS = "4228";
	public static final String BARRED_GMB_SUCCESS = "4223031";
	public static final String BARRED_SELF_GMB_SUCCESS = "4223037";
	public static final String BARRED_GMB_ALREADY_BARRED = "4223032";
	public static final String BARRED_SUBSCRIBER_SELF_RSN = "4229";
	public static final String BARRED_SUBSCRIBER_SYS_RSN = "4230";

	public static final String BAL_QUERY_SELF_BALANCE_LIST_SUCCESS = "4300";
	public static final String BAL_QUERY_SELF__USR_PRODUCT_MSG = "4301";
	public static final String BAL_QUERY_SELF_USR_PRODUCT_WITH_NETWORK_MSG = "4302";
	public static final String BAL_QUERY_SELF_BALANCE_LIST_SUCCESS_WITH_USERAGENT = "4303";
	public static final String BAL_QUERY_SELF_USR_PRODUCT_CODE_WITH_NETWORK_MSG = "4304";
	public static final String BAL_QUERY_SELF_BALANCE_LIST_NOTFOUND = "4305";
	public static final String BAL_QUERY_OTHER_BALANCE_CHECK_ALLOWED = "4306";
	public static final String BAL_QUERY_OTHER_BALANCE_CHECK_NOT_ALLOWED = "4307";
	public static final String BAL_QUERY_OTHER_BALANCE_LIST_SUCCESS = "4308";
	public static final String BAL_QUERY_OTHER_BALANCE_LIST_ZERO = "43084";
	public static final String BAL_QUERY_OTHER_BALANCE_LIST_SUCCESS_WITH_USERAGENT = "43081";
	public static final String BAL_QUERY_AGENT_BALANCE_SUCCESSS = "43082";
	public static final String BAL_QUERY_OTHER_AGENT_BALANCE_SUCCESSS = "43083";
	public static final String BAL_QUERY_NO_TRANSFER_HAS_BEEN_DONE = "4309";
	public static final String C2S_USER_LENGTH_NOT_VALID = "4310";
	public static final String C2S_USER_CODE_NOT_NUMERIC = "4311";
	public static final String BAL_QUERY_TRANSACTION_FAILED = "4313";
	public static final String BAL_QUERY_OTHER__USR_PRODUCT_MSG = "4314";
	public static final String BAL_QUERY_OTHER__USR_PRODUCT_WITH_NETWORK_MSG = "43142";
	public static final String BAL_AGENT_NOT_SEEN_SAME_LEVEL = "4328";

	public static final String C2S_PIN_NOTUPDATED = "4315";
	public static final String C2S_PIN_OLDNEWSAME = "4316";
	public static final String C2S_PIN_NEWCONFIRMNOTSAME = "4317";
	public static final String C2S_NEWPIN_NOTNUMERIC = "4318";
	public static final String C2S_PIN_LENGTHINVALID = "4319";
	public static final String C2S_PIN_CHANGE_SUCCESS = "4320";
	public static final String C2S_PIN_CHANGE_FAILED = "4321";
	public static final String C2S_ERROR_INVALID_PIN = "4322";
	public static final String C2S_ERROR_INVALIDMESSAGEFORMAT = "4323";
	public static final String C2S_PIN_NOT_APPLICABLE = "4324";
	public static final String C2S_PIN_BLANK = "4325";
	public static final String C2S_PIN_CONSECUTIVE = "4326";
	public static final String C2S_PIN_SAMEDIGIT = "4327";
	// for MSISDN Change Functionality
	public static final String C2S_MSISDN_CHANGE_SUCCESS = "1034101";
	public static final String C2S_MSISDN_CHANGE_ALREADY_EXIST_FAILURE = "1034002";
	public static final String C2S_MSISDN_CHANGE_UPDATE_FAILURE = "1034003";
	// for ETU Change Recharge Status Functionality Start
	public static final String C2S_MSISDN_RECHARGE_STATUS_SUCCESS = "1035101";
	public static final String C2S_MSISDN_RECHARGE_OTHER_STATUS = "1035002";
	public static final String C2S_MSISDN_RECHARGE_ALREADY = "1035003";
	public static final String C2S_MSISDN_RECHARGE_NOT = "1035004";

	public static final String C2S_LAST_TRANSFER_FAILED = "4330";
	public static final String LAST_TRANSFER_STATUS_NOT_FOUND = "4331";
	public static final String LAST_TRANSFER_STATUS_MSG = "4332";
	public static final String LAST_TRANSFER_STATUS_LIST_SUCCESS = "4333";
	public static final String LAST_C2S_TRANSFER_STATUS_SUCCESS = "4334";
	public static final String LAST_C2S_TRANSFER_STATUS_FAIL = "4335";
	public static final String LAST_C2S_TRANSFER_STATUS_AMBIGUOUS = "4336";
	public static final String LAST_TRANSFER_NO_TRANSACTION_DONE = "4337";

	public static final String DAILY_TRANSFER_LIST_NOTFOUND = "4400";
	public static final String DAILY_CHANNEL_TRANSFER_IN__PRODUCT_MSG = "4401";
	public static final String DAILY_CHANNEL_TRANSFER_OUT__PRODUCT_MSG = "4402";
	public static final String DAILY_SUBSCRIBER_TRANSFER_OUT_PRODUCT_MSG = "4403";
	public static final String SELF_DAILY_TRANSFER_LIST_SUCCESS = "4404";
	public static final String DAILY_OTHER_TRANSFER_LIST_NOTFOUND = "4405";
	public static final String OTHERUSER_DAILY_TRANSFER_LIST_SUCCESS = "4406";

	public static final String CHNL_ERROR_SENDER_REQ_UNDERPROCESS = "7000";
	public static final String CHNL_ERROR_SENDER_REQUNDERPROCESS_NOTUPDATED = "7001";
	public static final String CHNL_ERROR_SENDER_SUSPEND = "7002";
	public static final String CHNL_ERROR_SENDER_BLOCKED = "7003";
	public static final String CHNL_ERROR_CAT_GATETYPENOTALLOWED = "7004";
	public static final String CHNL_ERROR_PLAIN_SMS_NOT_ALLOWED = "7005";
	public static final String CHNL_ERROR_SNDR_ENCR_KEY_NOTFOUND = "7006";
	public static final String CHNL_ERROR_SNDR_WRONG_UDH = "7007";
	public static final String CHNL_ERROR_SNDR_BLANK_MESSAGE = "7008";
	public static final String CHNL_ERROR_SNDR_GEN_SCRTY_EXC = "7009";
	public static final String CHNL_ERROR_SNDR_EXC348_EXC = "7010";
	public static final String CHNL_ERROR_SNDR_MSG_NOT_DECRYPT = "7011";
	public static final String CHNL_ERROR_SNDR_NOTREG_BUTSENDREQ = "7012";
	public static final String CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED = "7013";
	public static final String CHNL_ERROR_SNDR_SVTYPE_VERSION_MISMATCH = "7014";
	public static final String CHNL_ERROR_SNDR_INVALID_PIN = "7015";
	public static final String CHNL_ERROR_SNDR_BLANK_PIN = "7060";
	public static final String CHNL_ERROR_RECR_MSISDN_BLANK = "7016";
	public static final String CHNL_ERROR_RECR_MSISDN_NOTINRANGE = "7017";
	public static final String CHNL_ERROR_RECR_MSISDN_LEN_NOTSAME = "7018";
	public static final String CHNL_ERROR_RECR_MSISDN_NOTNUMERIC = "7019";
	public static final String CHNL_ERROR_RECR_AMT_BLANK = "7020";
	public static final String CHNL_ERROR_RECR_AMT_NOTNUMERIC = "7021";
	public static final String CHNL_ERROR_RECR_AMT_LESSTHANZERO = "7022";
	public static final String CHNL_ERROR_SELF_TOPUP_NTALLOWD = "7023";
	public static final String C2S_ERROR_NOTFOUND_SERVICEINTERFACEMAPPING = "7024";
	public static final String C2S_ERROR_TRANSFER_RULE_NOTEXIST = "7025";
	public static final String C2S_ERROR_NOT_UPDATE_USER_XFER_COUNT = "7026";
	public static final String C2S_ERROR_NOT_DEBIT_BALANCE = "7027";
	public static final String C2S_ERROR_NOT_MAKECREDIT_ENTRY = "7028";
	public static final String CHNL_ERROR_SNDR_TEMPTRANSID_INVALID = "7029";
	public static final String C2S_ERROR_NOTFOUND_SERVICEKEYWORD = "7030";
	public static final String CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK = "7031";
	public static final String C2S_ERROR_NOT_CREDIT_BALANCE = "7032";
	public static final String CHNL_ERROR_SENDER_OUT_SUSPEND = "7033";
	public static final String C2S_INVALID_MESSAGE_FORMAT = "7034";
	public static final String CHNL_ERROR_SNDR_FORCE_CHANGEPIN = "7035";
	public static final String CHNL_ERROR_SENDER_STATUS_NEW = "7036";
	public static final String CHNL_ERROR_SENDER_STATUS_APPROVED = "7037";
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

	public static final String C2S_OPT_CHNL_TRANSFER_APPROVE1 = "8000";
	public static final String C2S_OPT_CHNL_TRANSFER_APPROVE2 = "8002";
	public static final String C2S_OPT_CHNL_TRANSFER_CLOSE = "8003";
	public static final String C2S_OPT_CHNL_TRANSFER_CANCEL = "8004";
	public static final String C2S_OPT_CHNL_TRANSFER_CANCEL_TXNSUBKEY = "8005";
	public static final String C2S_OPT_CHNL_TRANSFER_CANCEL_BALSUBKEY = "8006";
	public static final String C2S_OPT_CHNL_TRANSFER_SMS1 = "8080";
	public static final String C2S_OPT_CHNL_TRANSFER_SMS2 = "8081";
	public static final String C2S_OPT_CHNL_TRANSFER_SMS_BALSUBKEY = "8096";

	public static final String FOC_OPT_CHNL_TRANSFER_SMS1 = "8082";
	public static final String FOC_OPT_CHNL_TRANSFER_SMS2 = "8083";
	public static final String FOC_OPT_CHNL_TRANSFER_SMS3 = "8084";
	public static final String FOC_OPT_CHNL_TRANSFER_SMS_BALSUBKEY = "8097";
	public static final String FOC_OPT_CHNL_TRANSFER_CANCEL_TXNSUBKEY = "8098";
	public static final String FOC_OPT_CHNL_TRANSFER_CANCEL_BALSUBKEY = "8099";

	public static final String C2S_OPT_CHNL_WITHDRAW_SMS1 = "8085";
	public static final String C2S_OPT_CHNL_WITHDRAW_TXNSUBKEY = "8086";
	public static final String C2S_OPT_CHNL_WITHDRAW_BALSUBKEY = "8100";

	public static final String C2S_CHNL_CHNL_TRANSFER_RECEIVER = "8087";
	public static final String C2S_CHNL_CHNL_TRANSFER_RECEIVER_TXNSUBKEY = "8088";
	public static final String C2S_CHNL_CHNL_TRANSFER_RECEIVER_BALSUBKEY = "8089";

	public static final String C2S_CHNL_CHNL_RETURN_RECEIVER = "8090";
	public static final String C2S_CHNL_CHNL_RETURN_RECEIVER_TXNSUBKEY = "8091";
	public static final String C2S_CHNL_CHNL_RETURN_RECEIVER_BALSUBKEY = "8092";

	public static final String C2S_CHNL_CHNL_WITHDRAW_RECEIVER = "8093";
	public static final String C2S_CHNL_CHNL_WITHDRAW_RECEIVER_TXNSUBKEY = "8094";
	public static final String C2S_CHNL_CHNL_WITHDRAW_RECEIVER_BALSUBKEY = "8095";

	// added by sandeep goel used in the AdminController
	public static final String ERROR_MAINKEYWORD_NOTADM = "9001";
	public static final String ERROR_OPTKEYWORK_NULL = "9002";
	public static final String ERROR_MESSAGE_NULL = "9003";
	public static final String ERROR_BYTESTRING_NULL = "9004";
	public static final String SIMUPDATE_MESSAGE_SENT = "9005";
	public static final String SIMUPDATE_MESSAGE_SUCCESS = "9006";
	public static final String C2S_ERROR_EXCEPTION = "9007";

	public static final String C2S_REQUEST_UNDERPROCESS1 = "9010";
	public static final String C2S_REQUEST_UNDERPROCESS2 = "9011";
	public static final String C2S_REGISTRATION_SUCCESS = "9012";

	public static final String C2S_ERROR_BLANK_MSISDN = "6000";
	public static final String C2S_ERROR_BLANK_REQUESTMESSAGE = "6001";
	public static final String C2S_ERROR_BLANK_REQUESTINTID = "6002";
	public static final String C2S_ERROR_BLANK_REQUESTINTTYPE = "6003";
	public static final String PRODUCT_NOT_AVAILABLE = "6004";
	public static final String PRODUCT_NOT_FOUND = "6005";
	public static final String PRODUCT_NOT_ASSOCIATED_WITH_NETWK = "6006";
	public static final String PRODUCT_NETWK_SUSPENDED = "6007";
	public static final String PRODUCT_NETWK_DELETED = "6008";
	public static final String PRODUCT_NETWK_CONSUM_NOTALLOWED = "6009";
	public static final String CHNL_ERROR_SNDR_INSUFF_BALANCE = "6010";
	public static final String CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL = "6011";
	public static final String CHNL_ERROR_SNDR_DAILY_OUT_CTREACHED = "6012";
	public static final String CHNL_ERROR_SNDR_DAILY_OUT_VALREACHED = "6013";
	public static final String CHNL_ERROR_SNDR_WEEKLY_OUT_CTREACHED = "6014";
	public static final String CHNL_ERROR_SNDR_WEEKLY_OUT_VALREACHED = "6015";
	public static final String CHNL_ERROR_SNDR_MONTHLY_OUT_CTREACHED = "6016";
	public static final String CHNL_ERROR_SNDR_MONTHLY_OUT_VALREACHED = "6017";
	public static final String ERROR_INVALID_SERTYPE_PRODUCT_NOT_FOUND = "6018";
	public static final String CHNL_ERROR_SNDR_AMT_NOTBETWEEN_MINMAX = "6019";

	public static final String ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED = "5001";
	public static final String ERROR_INVALID_USER_CODE_FORMAT = "5002";
	public static final String ERROR_INVALID_PRODUCT_QUANTITY = "5003";
	public static final String ERROR_INVALID_PRODUCT_CODE_FORMAT = "5004";
	public static final String ERROR_USER_NOT_EXIST = "5005";
	public static final String ERROR_COMMISSION_PROFILE_SUSPENDED = "5006";
	public static final String ERROR_TRANSFER_PROFILE_SUSPENDED = "5007";
	public static final String ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED = "5008";
	public static final String ERROR_USER_TRANSFER_RULE_NOT_DEFINE = "5009";
	public static final String ERROR_USER_TRANSFER_ASSOCIATED_NOT_ALLOWED = "5010";
	public static final String ERROR_USER_TRANSFER_CHANNEL_BY_PASS_NOT_ALLOWED = "5011";
	public static final String ERROR_USER_TRANSFER_PRODUCT_NOT_DEFINED = "5012";
	public static final String ERROR_USER_TRANSFER_PRODUCT_MIN_TRANSFER = "5013";
	public static final String ERROR_USER_TRANSFER_PRODUCT_MIN_TRANSFER_SUBKEY = "5014";
	public static final String ERROR_USER_TRANSFER_PRODUCT_MAX_TRANSFER = "5015";
	public static final String ERROR_USER_TRANSFER_PRODUCT_MAX_TRANSFER_SUBKEY = "5016";
	public static final String ERROR_USER_TRANSFER_PRODUCT_MULTIPLE_OF = "5017";
	public static final String ERROR_USER_TRANSFER_PRODUCT_MULTIPLE_OF_SUBKEY = "5018";
	public static final String ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE = "5019";
	public static final String ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE_SUBKEY = "5020";
	public static final String ERROR_USER_TRANSFER_PRODUCT_NOT_ALLOWED = "5021";
	public static final String ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS = "5065";
	public static final String ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_SUBKEY = "5023";
	public static final String ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE = "5024";
	public static final String ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE_SUBKEY = "5025";
	public static final String ERROR_USER_TRANSFER = "5026";
	public static final String ERROR_COMMISSION_SLAB_NOT_DEFINE = "5027";
	public static final String ERROR_COMMISSION_SLAB_NOT_DEFINE_SUBKEY = "5028";
	public static final String CHNL_TRANSFER_ERROR_OUTSIDE_OUT_COUNTS_OVER = "5029";
	public static final String CHNL_TRANSFER_ERROR_OUTSIDE_IN_COUNTS_OVER = "5030";
	public static final String CHNL_TRANSFER_ERROR_OUT_COUNTS_OVER = "5031";
	public static final String CHNL_TRANSFER_ERROR_IN_COUNTS_OVER = "5032";
	public static final String CHNL_TRANSFER_ERROR_USER_BALANCE_NOT_EXIST = "5033";
	public static final String CHNL_TRANSFER_ERROR_USER_BALANCE_NOT_EXIST_SUBKEY = "5034";
	public static final String ERROR_PARENT_USER_NOT_EXIST = "5035";
	public static final String CHNL_TRANSFER_SUCCESS_TXNSUBKEY = "5036";
	public static final String CHNL_TRANSFER_SUCCESS_BALSUBKEY = "8101";
	public static final String CHNL_TRANSFER_SUCCESS = "5037";
	public static final String CHNL_WITHDRAW_SUCCESS_TXNSUBKEY = "5038";
	public static final String CHNL_WITHDRAW_SUCCESS_BALSUBKEY = "8103";
	public static final String CHNL_WITHDRAW_SUCCESS = "5039";
	public static final String CHNL_RETURN_SUCCESS_TXNSUBKEY = "5040";
	public static final String CHNL_RETURN_SUCCESS_BALSUBKEY = "8102";
	public static final String CHNL_RETURN_SUCCESS = "5041";
	public static final String ERROR_USER_WITHDRAW_NOT_ALLOWED = "5042";
	public static final String ERROR_USER_RETURN_NOT_ALLOWED = "5043";
	public static final String ERROR_USER_TRANSFER_CHANNEL_DOMAIN_NOTSAME = "5044";
	public static final String ERROR_USER_TRANSFER_CHANNEL_SEQUENCE_NOT_BELOW = "5045";
	public static final String ERROR_USER_TRANSFER_NOT_ALLOWED = "5046";
	public static final String ERROR_USER_TRANSFER_PRODUCT_RULE_NOTDEFINE = "5047";
	public static final String ERROR_USER_TRANSFER_SAME_USER = "5048";
	public static final String ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE1 = "115020";

	public static final String ERROR_USER_TRANSFER_NOPRODUCT_EXIST = "5049";
	public static final String ERROR_USER_TRANSFER_NOTMAPPED_NETWORK = "5050";
	public static final String ERROR_USER_TRANSFER_NO_COMM_PROFILE_ASSOCIATED = "5051";
	public static final String ERROR_USER_TRANSFER_NOPRODUCT_WITH_COMM_PROFILE = "5052";
	public static final String ERROR_USER_TRANSFER_NO_SAME_PRODUCT_IN_COMMPROFILE_NTWMAPPING = "5053";
	public static final String ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE = "5054";
	public static final String ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE_IN_COMMPROFILE_NTWMAPPING = "5055";
	public static final String ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK = "5056";
	public static final String ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR = "5057";
	public static final String ERROR_USER_TRANSFER_PRODUCT_RULE_NOTMATCH = "5058";
	public static final String ERROR_USER_SUSPENDED = "5059";
	public static final String ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE = "5060";

	public static final String CHANNEL_TRANSFER_PROFILE_NOT_EXIST = "5200";// "PROFILE NOT EXIST";
	public static final String CHANNEL_TRANSFER_DAILY_IN_COUNT = "5201";// "DAILY IN COUNT";
	public static final String CHANNEL_TRANSFER_WEEKLY_IN_COUNT = "5202";// "WEEKLY IN COUNT";
	public static final String CHANNEL_TRANSFER_MONTHLY_IN_COUNT = "5203";// "MONTHLY IN COUNT";
	public static final String CHANNEL_TRANSFER_DAILY_IN_VALUE = "5204";// "DAILY IN VALUE";
	public static final String CHANNEL_TRANSFER_WEEKLY_IN_VALUE = "5205";// "WEEKLY IN VALUE";
	public static final String CHANNEL_TRANSFER_MONTHLY_IN_VALUE = "5206";// "MONTHLY IN VALUE";
	public static final String CHANNEL_TRANSFER_DAILY_OUT_COUNT = "5207";// "DAILY OUT COUNT";
	public static final String CHANNEL_TRANSFER_WEEKLY_OUT_COUNT = "5208";// "WEEKLY OUT COUNT";
	public static final String CHANNEL_TRANSFER_MONTHLY_OUT_COUNT = "5209";// "MONTHLY OUT COUNT";
	public static final String CHANNEL_TRANSFER_DAILY_OUT_VALUE = "5210";// "DAILY OUT VALUE";
	public static final String CHANNEL_TRANSFER_WEEKLY_OUT_VALUE = "5211";// "WEEKLY OUT VALUE";
	public static final String CHANNEL_TRANSFER_MONTHLY_OUT_VALUE = "5212";// "MONTHLY OUT VALUE";
	public static final String CHANNEL_TRANSFER_OUTSIDE_DAILY_IN_COUNT = "5213";// "OUTSIDE DAILY IN COUNT ";
	public static final String CHANNEL_TRANSFER_OUTSIDE_WEEKLY_IN_COUNT = "5214";// "OUTSIDE WEEKLY IN COUNT ";
	public static final String CHANNEL_TRANSFER_OUTSIDE_MONTHLY_IN_COUNT = "5215";// "OUTSIDE MONTHLY IN COUNT ";
	public static final String CHANNEL_TRANSFER_OUTSIDE_DAILY_IN_VALUE = "5216";// "OUTSIDE DAILY IN VALUE ";
	public static final String CHANNEL_TRANSFER_OUTSIDE_WEEKLY_IN_VALUE = "5217";// "OUTSIDE WEEKLY IN VALUE ";
	public static final String CHANNEL_TRANSFER_OUTSIDE_MONTHLY_IN_VALUE = "5218";// "OUTSIDE MONTHLY IN VALUE ";
	public static final String CHANNEL_TRANSFER_OUTSIDE_DAILY_OUT_COUNT = "5219";// "OUTSIDE DAILY OUT COUNT ";
	public static final String CHANNEL_TRANSFER_OUTSIDE_WEEKLY_OUT_COUNT = "5220";// "OUTSIDE WEEKLY OUT COUNT ";
	public static final String CHANNEL_TRANSFER_OUTSIDE_MONTHLY_OUT_COUNT = "5221";// "OUTSIDE MONTHLY OUT COUNT ";
	public static final String CHANNEL_TRANSFER_OUTSIDE_DAILY_OUT_VALUE = "5222";// "OUTSIDE DAILY OUT VALUE ";
	public static final String CHANNEL_TRANSFER_OUTSIDE_WEEKLY_OUT_VALUE = "5223";// "OUTSIDE WEEKLY OUT VALUE ";
	public static final String CHANNEL_TRANSFER_OUTSIDE_MONTHLY_OUT_VALUE = "5224";// "OUTSIDE MONTHLY OUT VALUE ";
	public static final String CHANNEL_TRANSFER_COUNT_FOR_ONLINE = "5225";// "DAILY IN COUNT";

	public static final String CATEGORY_TRANSFER_PROFILE_NOT_EXIST = "5300";// "CATEGORY CATEGORY PROFILE NOT EXIST";
	public static final String CATEGORY_TRANSFER_DAILY_IN_COUNT = "5301";// "CATEGORY CATEGORY DAILY IN COUNT";
	public static final String CATEGORY_TRANSFER_WEEKLY_IN_COUNT = "5302";// "CATEGORY WEEKLY IN COUNT";
	public static final String CATEGORY_TRANSFER_MONTHLY_IN_COUNT = "5303";// "CATEGORY MONTHLY IN COUNT";
	public static final String CATEGORY_TRANSFER_DAILY_IN_VALUE = "5304";// "CATEGORY DAILY IN VALUE";
	public static final String CATEGORY_TRANSFER_WEEKLY_IN_VALUE = "5305";// "CATEGORY WEEKLY IN VALUE";
	public static final String CATEGORY_TRANSFER_MONTHLY_IN_VALUE = "5306";// "CATEGORY MONTHLY IN VALUE";
	public static final String CATEGORY_TRANSFER_DAILY_OUT_COUNT = "5307";// "CATEGORY DAILY OUT COUNT";
	public static final String CATEGORY_TRANSFER_WEEKLY_OUT_COUNT = "5308";// "CATEGORY WEEKLY OUT COUNT";
	public static final String CATEGORY_TRANSFER_MONTHLY_OUT_COUNT = "5309";// "CATEGORY MONTHLY OUT COUNT";
	public static final String CATEGORY_TRANSFER_DAILY_OUT_VALUE = "5310";// "CATEGORY DAILY OUT VALUE";
	public static final String CATEGORY_TRANSFER_WEEKLY_OUT_VALUE = "5311";// "CATEGORY WEEKLY OUT VALUE";
	public static final String CATEGORY_TRANSFER_MONTHLY_OUT_VALUE = "5312";// "CATEGORY MONTHLY OUT VALUE";
	public static final String CATEGORY_TRANSFER_OUTSIDE_DAILY_IN_COUNT = "5313";// "CATEGORY OUTSIDE DAILY IN COUNT ";
	public static final String CATEGORY_TRANSFER_OUTSIDE_WEEKLY_IN_COUNT = "5314";// "CATEGORY OUTSIDE WEEKLY IN COUNT
																					// ";
	public static final String CATEGORY_TRANSFER_OUTSIDE_MONTHLY_IN_COUNT = "5315";// "CATEGORY OUTSIDE MONTHLY IN COUNT
																					// ";
	public static final String CATEGORY_TRANSFER_OUTSIDE_DAILY_IN_VALUE = "5316";// "CATEGORY OUTSIDE DAILY IN VALUE ";
	public static final String CATEGORY_TRANSFER_OUTSIDE_WEEKLY_IN_VALUE = "5317";// "CATEGORY OUTSIDE WEEKLY IN VALUE
																					// ";
	public static final String CATEGORY_TRANSFER_OUTSIDE_MONTHLY_IN_VALUE = "5318";// "CATEGORY OUTSIDE MONTHLY IN VALUE
																					// ";
	public static final String CATEGORY_TRANSFER_OUTSIDE_DAILY_OUT_COUNT = "5319";// "CATEGORY OUTSIDE DAILY OUT COUNT
																					// ";
	public static final String CATEGORY_TRANSFER_OUTSIDE_WEEKLY_OUT_COUNT = "5320";// "CATEGORY OUTSIDE WEEKLY OUT COUNT
																					// ";
	public static final String CATEGORY_TRANSFER_OUTSIDE_MONTHLY_OUT_COUNT = "5321";// "CATEGORY OUTSIDE MONTHLY OUT
																					// COUNT ";
	public static final String CATEGORY_TRANSFER_OUTSIDE_DAILY_OUT_VALUE = "5322";// "CATEGORY OUTSIDE DAILY OUT VALUE
																					// ";
	public static final String CATEGORY_TRANSFER_OUTSIDE_WEEKLY_OUT_VALUE = "5323";// "CATEGORY OUTSIDE WEEKLY OUT VALUE
																					// ";
	public static final String CATEGORY_TRANSFER_OUTSIDE_MONTHLY_OUT_VALUE = "5324";// "CATEGORY OUTSIDE MONTHLY OUT
																					// VALUE ";

	public static final String DIFF_ERROR_AMOUNT_NOTINRANGE = "7059";
	public static final String NOT_GENERATE_ADJUSTMENTID = "7051";
	public static final String ERR_DIFF_FACTOR_CANNOT_BE_ZERO = "7052";
	public static final String C2S_ERROR_INVALID_SENDER_MSISDN = "7053";
	public static final String CHNL_ERROR_NO_SUCH_USER = "7054";

	// changes for messages prepaid and postpaid wise date 22/05/06 start
	public static final String REC_LAST_SUCCESS_REQ_BLOCK_R_PRE = "7055";
	public static final String REC_LAST_SUCCESS_REQ_BLOCK_R_POST = "9973";
	public static final String AMOUNT_TRANSFERS_DAY_EXCEEDED_R_PRE = "7056";
	public static final String AMOUNT_TRANSFERS_DAY_EXCEEDED_R_POST = "9974";
	public static final String NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_PRE = "7057";
	public static final String NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_POST = "9975";
	// changes for messages prepaid and postpaid wise date 22/05/06 ends

	public static final String P2P_ERROR_INVALID_SENDER_MSISDN = "7058";

	public static final String CHANNEL_USER_UNBARRED = "9980";
	public static final String CHANNEL_USER_BARRED = "9981";
	public static final String SERVICE_SUCCESSFULLY_DOWNLOADED = "8001";

	// MESSAGES ADDED BY SANDEEP.
	public static final String P2PSUBSCRIBER_UNBLOCKSENDPIN_MSG = "9900";
	public static final String P2PSUBSCRIBER_UNBLOCKPIN_MSG = "9999";
	public static final String P2PSUBSCRIBER_RESETPIN_MSG = "9919";
	public static final String P2PSUBSCRIBER_SENDPIN_MSG = "9918";
	public static final String P2PSUBSCRIBER_ACTIVATEPOSTPAIDSUBSCRIBER_MSG_DELETED = "9901";
	public static final String P2PSUBSCRIBER_ACTIVATEPOSTPAIDSUBSCRIBER_MSG_ACTIVATED = "9902";
	public static final String P2PSUBSCRIBER_SERVICE_SUSPEND = "9920";
	public static final String P2PSUBSCRIBER_SERVICE_RESUME = "9921";
	// public static final String
	// CHANNELUSER_TRANSFERUSERHIERARCHY_MSG_SUCCESS_WITHPRODUCT="9903";
	public static final String CHANNELUSER_TRANSFERUSERHIERARCHY_MSG_SUCCESS_WITHOUTPRODUCT = "9904";
	// public static final String
	// CHANNELUSER_TRANSFERUSERHIERARCHY_PRODUCT_MSG="9905";
	public static final String CHANNELUSER_TRANSFERUSERHIERARCHY_SENDER_PARENT = "9906";
	public static final String CHANNELUSER_TRANSFERUSERHIERARCHY_RECEIVER_PARENT = "9907";

	public static final String C2SSUBSCRIBER_RESETPIN_MSG = "9910";
	public static final String C2SSUBSCRIBER_UNBLOCKSENDPIN_MSG = "9911";
	public static final String C2SSUBSCRIBER_UNBLOCKPIN_MSG = "9912";
	public static final String C2SSUBSCRIBER_SENDPIN_MSG = "9913";

	public static final String C2SSUBSCRIBER_RESETPSWD_MSG = "9914";
	public static final String C2SSUBSCRIBER_UNBLOCKSENDPSWD_MSG = "9915";
	public static final String C2SSUBSCRIBER_UNBLOCKPSWD_MSG = "9916";
	public static final String C2SSUBSCRIBER_SENDPSWD_MSG = "9917";

	public static final String REG_INVALID_MESG_FORMAT = "9990";
	public static final String P2P_SENDER_AUTO_REG_SUCCESS = "9991";
	public static final String REG_ERROR_INTFCE_SRVCECLSS_NOTFOUND = "9992";
	public static final String REG_ERROR_INTFCE_SRVCECLSS_SUSPEND = "9993";
	public static final String REG_ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED = "9994";
	public static final String REG_KEY_ERROR_INTFCE_SRVCECLSS_NOTFOUND = "9995";
	public static final String REG_KEY_ERROR_INTFCE_SRVCECLSS_SUSPEND = "9996";
	public static final String REG_KEY_ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED = "9997";
	public static final String P2P_SENDER_AUTO_REG_SUCCESS_WITHPIN = "9998";

	public static final String P2P_ERROR_INVALID_LANGUAGECODE = "1900";
	public static final String P2P_LANGUAGE_UPDATE_SUCCESS = "1901";
	public static final String P2P_LANGUAGE_UPDATE_FAILED = "1902";
	public static final String P2P_ERROR_LANGUAGECODE_NOTNUMERIC = "1903";

	public static final String C2S_ERROR_INVALID_LANGUAGECODE = "1990";
	public static final String C2S_LANGUAGE_UPDATE_SUCCESS = "1991";
	public static final String C2S_LANGUAGE_UPDATE_FAILED = "1992";
	public static final String C2S_ERROR_LANGUAGECODE_NOTNUMERIC = "1993";

	public static final String P2P_ERROR_CPIN_INVALIDMESSAGEFORMAT = "9989";
	public static final String P2P_ERROR_INVALID_TR_REPORTREQUESTFORMAT = "9988";
	public static final String ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC = "9987";
	public static final String INVLID_ACTION = "11111";
	// alert messages
	public static final String PIN_ALERT_MSG = "3502";
	public static final String PSWD_ALERT_MSG = "3503";

	public static final String USER_WEB_ACTIVATE = "12000";
	public static final String USER_SMSPIN_ACTIVATE = "12001";
	public static final String USER_WEB_SMSPIN_ACTIVATE = "12002";

	public static final String P2P_ERROR_SENDER_MSISDN_EXPIRED = "2054";

	// New Gurjeet : To be added in Propertied
	public static final String ERROR_INVALID_SELECTOR_VALUE = "8500";
	public static final String ERROR_INTFCE_SRVCECLSS_SENDER_SUSPEND = "8501";
	public static final String ERROR_INTFCE_SRVCECLSS_P2P_RECEIVER_SUSPEND = "8502";
	public static final String ERROR_INTFCE_SRVCECLSS_C2S_RECEIVER_SUSPEND = "8503";
	public static final String CHNL_ERROR_CHLAN_INVALIDMESSAGEFORMAT = "8504";
	public static final String CHNL_PIN_SAME_TO_DEFAULT_PIN = "8505";
	public static final String CHNL_ERROR_CPIN_INVALIDMESSAGEFORMAT = "8506";
	public static final String C2S_ERROR_USERBAL_INVALIDMESSAGEFORMAT = "8507";
	public static final String C2S_ERROR_DA_TRANSFER_INVALIDMESSAGEFORMAT = "8508";
	public static final String C2S_ERROR_LAST_TRSFER_INVALIDMESSAGEFORMAT = "8509";
	public static final String C2S_INVALID_PAYEE_NOT_LANG = "8510";
	public static final String CHNL_ERROR_RECR_AMT_LESSTHANALLOWED = "8511";
	public static final String CHNL_ERROR_RECR_AMT_MORETHANALLOWED = "8512";
	public static final String C2S_INVALID_SNDR_NOT_LANG = "8521";
	public static final String REG_KEY_ERROR_INTFCE_SRVCECLSS_SENDER_SUSPEND = "8550";
	public static final String REG_ERROR_INTFCE_SRVCECLSS_SENDER_SUSPEND = "8551";

	public static final String P2P_NETWORK_NOT_ACTIVE = "8513";
	public static final String C2S_NETWORK_NOT_ACTIVE = "8514";
	public static final String INTERFACE_NOT_ACTIVE = "8515";

	// Added by Ankit Singhal
	public static final String LOW_BALANCE_ALERT_MSG_SUBKEY = "3504";
	public static final String LOW_BALANCE_ALERT_MSG = "3505";

	// public static final String ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE_CATLVL
	// = "5060";
	// public static final String
	// ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_CATLVL = "5061";
	public static final String ERROR_USER_TRANSFER_PRODUCT_ALLOWMAXPCT = "5062";
	// public static final String ERROR_USER_TRANSFER_PRODUCT_ALLOWMAXPCT_CATLVL
	// = "5063";
	// public static final String ERROR_USER_TRANSFER_PRODUCT_MAX_BALANCE_MSG =
	// "5064";
	public static final String ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG = "5022";
	public static final String ERROR_INVALID_DEFAULT_PRODUCT_QUANTITY = "5066";
	public static final String CHNL_RETURN_SUCCESS_SENDER_AGENT = "8110";
	public static final String CHNL_RETURN_SUCCESS_RECEIVER_AGENT = "8111";
	public static final String CHNL_WITHDRAW_SUCCESS_SENDER_AGENT = "8112";
	public static final String CHNL_WITHDRAW_SUCCESS_RECEIVER_AGENT = "8113";
	public static final String CHNL_TRANSFER_SUCCESS_SENDER_AGENT = "8114";
	public static final String CHNL_TRANSFER_SUCCESS_RECEIVER_AGENT = "8115";
	public static final String P2P_ERROR_INVALID_CHGELANG_REPORTREQUESTFORMAT = "9986";
	public static final String P2P_ERROR_INVALID_TR_STATUS_REPORTREQUESTFORMAT = "9985";
	public static final String CHNL_ERROR_SENDER_DELETE_REQUEST = "9984";
	public static final String CHNL_ERROR_SENDER_SUSPEND_REQUEST = "9983";

	public static final String ERROR_INVALID_LANGUAGE_SEL_VALUE = "9982";

	public static final String REQ_TIMEOUT_FROM_QUEUE_C2S = "9967";
	public static final String REQ_TIMEOUT_FROM_QUEUE_P2P = "9966";

	public static final String CHNL_TRANSFER_ERROR_PRODUCT_SUSPENDED_SUBKEY = "8104";
	public static final String MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND = "9955";
	public static final String P2P_SENDER_SUCCESS_WITHOUT_ACCESSFEE = "9956";

	// Added by Ankit Singhal for corporate
	public static final String CORP_LOW_BALANCE_ALERT_MSG = "3506";
	public static final String RM_ERROR_RESTRICTED_SUBSCRIBER_DOESNOTEXIST = "3507";
	public static final String RM_ERROR_RESTRICTED_SUBSCRIBER_SUSPEND = "3508";
	public static final String RM_ERROR_AMOUNT_LESSTHANMINIMUM = "3511";
	public static final String RM_ERROR_AMOUNT_MORETHANMAXIMUM = "3512";
	public static final String RM_ERROR_AMOUNT_MONTHLYLIMIT_CROSSED = "3513";
	public static final String CORP_LOW_BALANCE_ALERT_MSG_SUBKEY = "3514";
	public static final String MESSAGE_FOR_PURGING_INTERFACE_TRANSACTIONS = "3515";

	// ADDED BY SUHEL FOR GREETINGMSGPROCESS DATE 02/08/2016
	public static final String GREETMSG_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "4600";
	public static final String GREETMSG_ERROR_EXCEPTION = "4601";
	public static final String GREETMSG_AMB_OR_UP_TXN_FOUND = "4602";

	// ADDED BY ASHISH FOR SCHEDULED TOP UP PROCESS DATE 12/04/06
	public static final String REST_SCH_USER_IS_NOT_ACTIVE = "9000";
	public static final String REST_SCH_ERROR_AMOUNT_LESS_MINTAX_AMT = "9100";
	public static final String REST_SCH_ERROR_AMOUNT_GREATER_MAXTAX_AMT = "9101";
	public static final String SUBSCRIBER_VALIDATION_FAILS_INSIDE_SCHEDULED_PROCESS = "9102";
	public static final String REST_SCH_ERROR_CHANNEL_USER_NOT_EXIST = "9104";
	public static final String SUBSCRIBER_DOESNOT_EXIST_IN_RESTRICTED_LIST = "9105";
	public static final String REST_SCH_NO_BATCH_SCHEDULED = "9106";
	public static final String SCHEDULE_ERR_LOAD_CONS_VALUE = "9108";
	public static final String REST_SCH_NO_MSISDN_SCHEDULED = "9109";
	public static final String REST_SCH_SUBS_VALIDATION_FAIL = "9110";
	public static final String REST_SCH_ERROR_TOTAL_TXN_AMT_GR_MONTHLY_LIMIT = "9111";
	public static final String REST_SCH_ERR_NO_RESPONSE_C2STRANSFER = "9112";
	public static final String REST_SCH_ERROR_CONNECTION = "9113";
	public static final String REST_SCH_ERROR_MAX_LIMIT_CONN_REFUSE_REACH = "9114";
	public static final String REST_SCH_ERR_IN_VALIDATION = "9116";
	public static final String REST_SCH_ERR_BATCH_MASTER_UPDATION = "9117";
	public static final String REST_SCH_ERR_BATCH_DETAIL_UPDATION = "9118";
	public static final String REST_SCH_INSTANCE_NULL = "9119";
	// Added by Ankit Singhal for balance mismatch alert
	public static final String CHNL_BALANCE_MISMATCH_SUCCESS = "3516";
	public static final String CHNL_BALANCE_MISMATCH_FAILURE = "3517";
	public static final String NETWORK_STOCK_MISMATCH_SUCCESS = "3518";
	public static final String NETWORK_STOCK_MISMATCH_FAILURE = "3519";

	// to be used by all the processes
	public static final String PROCESS_ENTRY_NOT_FOUND = "3521";
	public static final String PROCESS_ERROR_UPDATE_STATUS = "3522";
	public static final String START_DATE_NOT_FOUND = "3523";
	public static final String PROCESS_ALREADY_RUNNING = "3524";

	// Added by Ankit Singhal for Data WareHouse
	public static final String DWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE = "3520";
	public static final String DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "3525";
	public static final String DWH_AMB_OR_UP_TXN_FOUND = "3526";
	public static final String DWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "3527";
	public static final String DWH_COULD_NOT_UPDATE_MAX_DONE_DATE = "3528";
	public static final String DWH_ERROR_EXCEPTION = "3530";
	public static final String DWH_ALL_FILES_DELETED = "3531";
	// Added by Ankit Singhal for checking black list status of the sender in
	// P2P transaction
	public static final String RM_ERROR_RESTRICTED_SUBSCRIBER_BLACKLISTED = "3529";

	// add by ved for user modify
	public static final String OPT_USER_LOGIN_AND_PWD_MODIFY = "12003";
	public static final String OPT_USER_LOGIN_MODIFY = "12004";
	public static final String OPT_USER_PWD_MODIFY = "12005";
	public static final String CHNL_USER_DEREGISTER = "12006";
	public static final String CHNL_USER_LOGIN_AND_PWD_MODIFY = "12007";
	public static final String CHNL_USER_LOGIN_MODIFY = "12008";
	public static final String CHNL_USER_PWD_MODIFY = "12009";
	public static final String CHNL_USER_LOGIN_AND_PIN_MODIFY = "12010";
	public static final String CHNL_USER_PWD_AND_PIN_MODIFY = "12011";
	public static final String CHNL_USER_LOGIN_AND_PWD_AND_PIN_MODIFY = "12012";
	public static final String CHNL_USER_PIN_MODIFY = "12013";
	public static final String CHNL_USER_MESSAGE_DEREGISTER = "12014"; // staff
																		// user
																		// msg
																		// sent

	// added by ankit zindal for postpaid bill payment on date 16/05/06
	public static final String C2S_RECEIVER_SUCCESS_BILLPAY = "9958";
	public static final String C2S_SENDER_SUCCESS_BILLPAY = "9959";
	public static final String C2S_RECEIVER_UNDERPROCESS_BILLPAY = "9960";
	public static final String C2S_SENDER_UNDERPROCESS_BILLPAY = "9961";
	public static final String C2S_SENDER_UNDERPROCESS_B4VAL_BILLPAY = "9962";
	public static final String C2S_RECEIVER_AMBIGOUS_KEY_BILLPAY = "9963";
	public static final String C2S_RECEIVER_FAIL_KEY_BILLPAY = "9964";
	public static final String C2S_RECEIVER_FAIL_BILLPAY = "9968";
	public static final String CHNL_ERROR_SELF_BILLPAY_NTALLOWD = "9969";
	public static final String CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND_BILLPAY = "9970";
	public static final String CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND_BILLPAY = "9971";
	public static final String CHNL_ERROR_SENDER_OUT_SUSPEND_BILLPAY = "9972";
	public static final String NUMBER_NOT_EXISTS_IN_WHITELIST = "9976";
	public static final String CHNL_ERROR_SNDR_INVALID_PIN_BILLPAY = "9977";
	public static final String CHNL_ERROR_SNDR_PINBLOCK_BILLPAY = "9978";
	public static final String BILLPAY_INVALID_PAYEE_NOT_LANG = "1111";
	public static final String BILLPAY_INVALID_MESSAGE_FORMAT = "1112";

	// keys added by Ankit Singhal for differentiating PRE and POST
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

	// Keys added by Ashish for WhiteList proccess.
	public static final String WLIST_ERROR_PARSER_CLASS_NOT_INSTANTIATED = "2220";
	public static final String WLIST_ERROR_DIR_CONTAINS_NO_FILES = "2221";
	public static final String WLIST_ERROR_FILE_DOES_NOT_EXIST = "2222";
	public static final String WLIST_ERROR_HEADER_INFO = "2223";
	public static final String WLIST_ERROR_HEADER_START_TAG_NOT_FOUND = "2224";
	public static final String WLIST_ERROR_HEADER_END_TAG_NOT_FOUND = "2225";
	public static final String WLIST_ERROR_MAX_INVALID_REC_REACH = "2226";
	public static final String WLIST_ERROR_INVALID_ACTION = "2227";
	public static final String WLIST_ERROR_NO_ACTION_IS_SELECTED = "2228";
	public static final String WLIST_ERROR_DIR_NOT_EXIST = "2229";
	public static final String WLIST_ERROR_FILE_EXT_NOT_DEFINED = "2230";
	public static final String WLIST_ERROR_FILE_PREFIX_NOT_DEFINED = "2231";
	public static final String WLIST_ERROR_MOVE_LOCATION_NOT_EXIST = "2232";
	public static final String WLIST_ERROR_PARSER_CLASS_PATH = "2233";
	public static final String WLIST_ERROR_ACTION_IS_NOT_FOUND = "2234";
	public static final String WLIST_ERROR_PERC_INVALID_COUNT_NOT_FOUND = "2235";
	public static final String WLIST_ERROR_PERCTG_INVALIDCT_NOT_NUMERIC = "2236";
	public static final String WLIST_ERROR_WHILE_BATCH_INSERTION = "2237";
	public static final String WLIST_ERROR_IN_DELETION_RECORDS = "2238";
	public static final String WLIST_ERROR_IN_INSERT_RECORDS = "2239";
	public static final String WLIST_ERROR_IN_UPDATE_RECORDS = "2240";
	public static final String WLIST_ERROR_FILE_NOT_MOVED_SUCCESSFULLY = "2241";
	public static final String WLIST_ERROR_MCODE_INSERT_NULL = "2242";
	public static final String WLIST_ERROR_MCODE_DELETE_NULL = "2243";
	public static final String WLIST_ERROR_MCODE_UPDATE_NULL = "2248";
	public static final String WLIST_ERROR_DELIMITER_NULL = "2249";
	public static final String WLIST_ERROR_FORMAT_NULL = "2250";
	public static final String WLIST_ERROR_MUL_FACTOR_NOT_NUMERIC = "2251";
	public static final String WLIST_ERROR_COL_NUM_NOT_NUMERIC = "2252";
	public static final String WLIST_ERROR_FORMAT_MSISDN = "2253";
	public static final String WLIST_ERROR_FORMAT_ACCOUNT_STATUS = "2254";
	public static final String WLIST_ERROR_FORMAT_CREDIT_LIMIT = "2255";
	public static final String WLIST_ERROR_FORMAT_IMSI = "2256";
	public static final String WLIST_ERROR_FORMAT_ACCOUNT_ID = "2257";
	public static final String WLIST_ERROR_FORMAT_SERVICE_CLASS = "2258";
	public static final String WLIST_ERROR_FORMAT_MOVEMNET_CODE = "2259";
	public static final String WLIST_ERROR_NO_COLUMN = "2260";
	public static final String WLIST_ERROR_INVALID_COL_NUMBER = "2261";
	public static final String WLIST_ERROR_INVALID_MSISDN = "2262";
	public static final String WLIST_ERROR_INVALID_AMOUNT = "2263";
	public static final String WLIST_ERROR_INVALID_NETWORK_CODE = "2264";
	public static final String WLIST_ERROR_NO_NETWORK_AVALAIBLE = "2265";
	public static final String WLIST_ERROR_NO_POST_PAID_INTERFACE = "2266";
	public static final String WLIST_ERROR_NO_INTERFACE_MAPPED = "2267";
	public static final String WLIST_ERROR_NO_SERVICE_CLASS = "2268";
	public static final String WLIST_ERROR_MULTIPLE_NETWK_SUPPORT = "2269";
	public static final String WLIST_ERROR_NETWK_NOT_FOUND = "2270";
	public static final String WLIST_ERROR_INTERFACEID_NOT_POST_PAID = "2271";
	public static final String WLIST_ERROR_DATA_UPDATE = "2272";
	public static final String WLIST_ERROR_CONN_NULL = "2273";
	public static final String WLIST_ERROR_NETWK_DETAIL_NULL = "2274";
	public static final String WLIST_ERROR_INTERFACE_NTWK_MAPPING_NOT_FOUND = "2275";
	public static final String WLIST_ERROR_MSISDN_PREFIX = "2276";
	public static final String WLIST_ERROR_IMSI_LENGTH_NULL = "2277";
	public static final String WLIST_ERROR_FILE_CONTAINS_NO_RECORDS = "2278";
	public static final String WLIST_ERROR_FILE_NAME_NTWKCODE_NOT_FOUND = "2279";
	public static final String WLIST_ERROR_FILE_NAME_EXTID_NOT_FOUND = "2279";
	public static final String WLIST_ERROR_MULTI_INTERFACE_SUPPORT = "2280";
	public static final String WLIST_ERROR_INVALID_IMSI_LENGTH = "2281";
	public static final String WLIST_ERROR_EXTID_OR_NTWCODE_NOT_IN_INPUTFILE = "2282";
	public static final String WLIST_ERROR_INVALID_FILENAME_FORMAT = "2283";
	public static final String WLIST_ERROR_FILENAME_SEPARATOR = "2284";
	public static final String ERROR_NOTFOUND_SERIES_TYPE = "4354";

	// CDR Files Generation Error Codes AMIT RUWALI
	public static final String CDR_FILE_GENERATION_ERROR = "3544";
	public static final String CDR_RCRD_FORMAT_IMPROPER = "3545";
	public static final String CDR_MIN_INTERVAL_LESS = "3546";
	public static final String CDR_TRAILER_RCRD_FORMAT_IMPROPER = "3547";
	// added for last transfer requesthandler for CRE_INT_CR00030
	public static final String LAST_C2S_TRANSFER_STATUS_UNDER_PROCESS = "4338";
	public static final String LAST_C2S_TRANSFER_STATUS_DEFAULT = "4339";

	public static final String ERROR_P2P_SAME_MSISDN_TRANSFER_NOTALLWD = "4356";
	public static final String P2P_POST_SNDR_MONTH_MAX_AMTTRANS_CCLMT_THRESHOLD = "4357";

	// Added by Amit Singh for P2P Data WareHouse
	public static final String P2PDWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE = "4242";
	public static final String P2PDWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "4243";
	public static final String P2PDWH_AMB_OR_UP_TXN_FOUND = "4244";
	public static final String P2PDWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "4245";
	public static final String P2PDWH_COULD_NOT_UPDATE_MAX_DONE_DATE = "4246";
	public static final String P2PDWH_ERROR_EXCEPTION = "4247";
	public static final String P2PDWH_ALL_FILES_DELETED = "4248";
	public static final String COULD_NOT_CREATE_DIR = "3548";
	public static final String MESSAGE_GATEWAY_NOT_ACTIVE = "3549";
	public static final String REQ_MESSAGE_GATEWAY_NOT_ACTIVE = "3550";
	public static final String RES_MESSAGE_GATEWAY_NOT_ACTIVE = "3551";

	public static final String C2S_UPDATE_SIM_PARAMS_REQD = "6666";
	public static final String C2S_AMBIGUOUS_CASE_ALERT_MSG_SUBKEY = "3552";
	public static final String C2S_AMBIGUOUS_CASE_ALERT_MSG = "3553";
	public static final String P2P_AMBIGUOUS_CASE_ALERT_MSG_SUBKEY = "3554";
	public static final String P2P_AMBIGUOUS_CASE_ALERT_MSG = "3555";
	public static final String C2S_ERROR_GRPT_COUNTERS_REACH_LIMIT_D = "3556";
	public static final String P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_D = "3557";
	public static final String C2S_ERROR_GRPT_COUNTERS_REACH_LIMIT_M = "3558";
	public static final String P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_M = "3559";
	public static final String ERROR_LESS_DEFAULT_PRODUCT_QUANTITY = "3566";// is
																			// defined
																			// to
																			// send
																			// invalid
																			// requested
																			// quantity
																			// messagew
	public static final String ERROR_INVALID_REC_USERCODE = "3567";// is defined
																	// to send
																	// invalid
																	// receiver
																	// user code
	public static final String ERROR_INTFCE_SRVCECLSS_SUSPEND_R = "3568";// is
																			// defined
																			// to
																			// send
																			// message
																			// for
																			// receiver
																			// service
																			// class
																			// is
																			// suspend
	public static final String ERROR_INVALID_LOOKUP_CODE = "3569";// is defined
																	// to throw
																	// exception
																	// of invalid
																	// lookup code
	public static final String INITIATOR_MSG_SCHEDULE_TOPUP_FINAL = "3570";// is
																			// defined
																			// to
																			// send
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
																			// fail
																			// and
																			// underprocess
																			// records
	// do not use keys 3560, 3561, 3562, 3563, 3564, 3565, 3571, 3572, 3573,
	// 3574, 3575, 3576, 3577
	// they are being used in Balance mismatch process, error codes come from
	// oracle procedure
	// so no entry is done here , but corresponding message is defined.

	public static final String INVALID_AMOUNT_NULL = "6667";
	public static final String INVALID_AMOUNT_NOTNUMERIC = "6668";
	public static final String INVALID_AMOUNT_LESSTHANZERO = "6669";
	public static final String ERROR_INVALID_AMOUNT_PREICISION_NOTALLOWED = "6670";
	public static final String ERROR_BUDDY_NAME_MANDATORY = "6671";
	public static final String ERROR_BUDDY_NAME_EXCEED_LENGTH = "6672";
	public static final String ERROR_INVALID_BUDDY_NAME = "6765";
	public static final String ERROR_MAX_NO_OF_ALLOWED_BUDDY_RCHD = "6674";
	public static final String P2P_ERROR_ADDBUDDY_INVALIDMESSAGEFORMAT = "6675";
	public static final String ERROR_BUDDY_NAME_SP_CHARACTERS = "6676";
	public static final String ERROR_BUDDY_NETWORK_NOTFOUND = "6677";
	public static final String P2P_ERROR_DELBUDDY_INVALIDMESSAGEFORMAT = "6678";
	public static final String P2P_ERROR_BUDDYLIST_INVALIDMESSAGEFORMAT = "6679";
	public static final String P2P_ERROR_DEREGISTER_INVALIDMESSAGEFORMAT = "6680";
	public static final String P2P_ERROR_AMBIGOUS_CASE_PENDING = "6681";
	public static final String P2P_ERROR_RESUME_SERVCE_INVALIDMESSAGEFORMAT = "6682";
	public static final String P2P_ERROR_REGSIETERD_SUBS_BARRING = "6683";
	public static final String P2P_ERROR_SUSPEND_SERVCE_INVALIDMESSAGEFORMAT = "6684";
	public static final String P2P_USER_STATUS_ALREADY_SUSPENDED = "6685";
	public static final String INVALID_MSISDN_NULL = "6686";

	public static final String RECON_C2S_ADJUSTMENT_FAIL_MSG1 = "7700";
	public static final String RECON_C2S_ADJUSTMENT_FAIL_MSG2 = "7701";
	public static final String RECON_C2S_ADJUSTMENT_SUCCESS_MSG1 = "7702";
	public static final String RECON_C2S_ADJUSTMENT_SUCCESS_MSG2 = "7703";
	public static final String RECON_C2S_ADJUSTMENT_SUCCESS_MSG3 = "7704";

	// add by nitin
	public static final String SERVICE_TYPE_O2C = "O2C_DAILY_REPORT";
	public static final String SERVICE_TYPE_C2C = "C2C_DAILY_REPORT";

	public static final String O2C_INITIATE_INVALID_MESSAGE_FORMAT = "111";
	public static final String O2C_INITIATE_TR_INVALID_MESSAGE_FORMAT = "112";
	public static final String O2C_RETURN_INVALID_MESSAGE_FORMAT = "113";
	public static final String O2C_WITHDRAW_INVALID_MESSAGE_FORMAT = "114";
	public static final String NETWORK_CODE_MSIDN_NETWORK_MISMATCH = "311";
	public static final String ERROR_EXT_NETWORK_CODE = "312";

	// added by Siddhartha for O2CWithdrawController
	public static final String ERROR_USER_TRANSFER_RULE_PRODUCT_NOT_ASSOCIATED = "7109";
	public static final String ERROR_NO_COMMISION_PRODUCT_ASSOCIATED = "7110";
	public static final String ERROR_NO_LATEST_COMMISSION_PROFILE_ASSOCIATED = "7111";
	public static final String ERROR_NO_TRANSFERPROFILE_PRODUCT_ASSOCIATED = "7112";
	public static final String ERROR_UPDATING_DATABASE = "7113";
	public static final String O2C_WITHDRAW_SUCCESS = "7114";
	public static final String O2C_WITHDRAW_SUCCESS_TXNSUBKEY = "7115";
	public static final String O2C_WITHDRAW_SUCCESS_BALSUBKEY = "7116";

	// added by Amit Singh for O2CInitiateTransferController
	public static final String ERROR_TRANSFER_CATEGORY_NOT_ALLOWED = "6101";
	public static final String ERROR_PAYMENTTYPE_NOTFOUND = "6102";
	public static final String ERROR_PAYMENT_INSTRUMENT_NUM_INVALID = "6103";
	public static final String ERROR_PAYMENT_INSTRUMENT_DATE_BLANK = "6104";
	public static final String ERROR_PAYMENTTYPE_BLANK = "6105";
	public static final String ERROR_PAYMENT_INSTRUMENT_DATE_NOT_PROPER = "6106";
	public static final String ERROR_USER_TRANSFER_NOT_ALLOWED_NOW = "6107";
	public static final String ERROR_NETWORK_PRODUCTS_NOT_MATCHING = "6108";
	public static final String ERROR_COMMISSION_PROFILE_PRODUCTS_NOT_MATCHING = "6109";
	public static final String ERROR_TRANSFER_PROFILE_PRODUCTS_NOT_MATCHING = "6110";
	public static final String ERROR_EXT_TXN_NO_NOT_POSITIVE = "6111";
	public static final String ERROR_EXT_TXN_NO_NOT_NUMERIC = "6112";
	public static final String ERROR_EXT_TXN_NO_NOT_UNIQUE = "6113";
	public static final String ERROR_REFERENCE_NO_BLANK = "6114";
	public static final String ERROR_CHANNEL_REAMRK_NOT_PROPER = "6115";
	public static final String ERROR_MESSAGE_FORMAT_NOT_PROPER = "6116";
	public static final String ERROR_EXT_TXN_NO_BLANK = "6119";
	public static final String ERROR_EXT_DATE_BLANK = "6120";
	public static final String ERROR_EXT_DATE_NOT_PROPER = "6121";
	public static final String ERROR_COMMISSION_PROFILE_QTY_INVALID = "6122";
	public static final String ERROR_REFERENCE_NO_LENGTH_NOT_VALID = "6123";
	public static final String ERROR_PRODUCT_TYPE_NOT_SAME = "6124";
	public static final String ERROR_TRANSFER_RULE_PRODUCTS_NOT_MATCHING = "6125";

	public static final String O2C_DIRECT_TRANSFER_SUCCESS_BALSUBKEY = "6160";
	public static final String O2C_DIRECT_TRANSFER_SUCCESS_TXNSUBKEY = "6161";
	public static final String O2C_DIRECT_TRANSFER_RECEIVER = "6162";
	public static final String O2C_INITIATE_TRANSFER_SUCCESS_BALSUBKEY = "6163";
	public static final String O2C_INITIATE_TRANSFER_SUCCESS_TXNSUBKEY = "6164";
	public static final String O2C_INITIATE_TRANSFER_RECEIVER = "6165";

	// added by Pankaj Namdev for O2CReturn Controller
	public static final String ERROR_USER_TRANSFER_CHANNEL_SENDER_BAR = "13043";
	public static final String REQUESTED_QUANTITY_IS_NOT_PROPER = "13044";
	public static final String ERROR_EXT_ID_IS_NEGATIVE = "13045";
	public static final String ERROR_IN_NETWORK_STOCT_TRANSACTION = "13046";
	public static final String ERROR_UPDATION_OPT_CHANNEL_USER_IN_COUNT = "13047";
	public static final String ERROR_ADD_CHANNEL_TRANSFER = "13048";
	public static final String ERROR_NOT_CREDIT_NETWORK_STOCK = "13049";
	public static final String ERROR_UPDATION_USERDAILYBALANCE = "13050";
	public static final String ERROR_CHNL_USER_NOT_ACTIVE = "13051";
	public static final String O2C_CHNL_RETURN_SUCCESS = "13200";
	public static final String O2C_CHNL_RETURN_SUCCESS_TXNSUBKEY = "13201";
	public static final String O2C_CHNL_RETURN_SUCCESS_BALSUBKEY = "13202";

	// CCE XML
	public static final String CCE_XML_ERROR_MISSING_MANDATORY_VALUE = "7500";
	public static final String CCE_XML_ERROR_INVALID_MODULE_VALUE = "7501";
	public static final String CCE_XML_ERROR_INVALID_ACTION_VALUE = "7502";
	public static final String CCE_XML_ERROR_INVALID_MANDATORY_VALUE = "7503";
	public static final String CCE_XML_ERROR_UNSUPPORTED_NETWORK = "7504";
	public static final String CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST = "7505";
	public static final String CCE_XML_ERROR_INVALID_LANGUAGE_CODE_VALUE = "7506";
	public static final String CCE_XML_ERROR_CHNL_USER_NOTEXIST = "7507";
	public static final String CCE_XML_ERROR_DOMAIN_NOTASSIGNED = "7508";
	public static final String CCE_XML_ERROR_DOMAIN_NOTMATCH = "7509";
	public static final String CCE_XML_ERROR_GEOGRAPHYDOMAIN_NOTIN_HIERARCHY = "7510";
	public static final String CCE_XML_ERROR_INVALID_EXTERNAL_DATE_FORMAT = "7511";
	public static final String CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE = "7512";
	public static final String CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE = "7513";
	public static final String CCE_ERROR_FROMDATE_GREATER_THAN_TODATE = "7514";
	public static final String CCE_ERROR_FROMDATE_INVALID_FORMAT = "7515";
	public static final String CCE_ERROR_TODATE_INVALID_FORMAT = "7516";
	public static final String CCE_ERROR_TRANSFER_DETAILS_NOT_FOUND = "7517";
	public static final String CCE_ERROR_INVALID_SERVICE_KEYWORD = "7518";
	public static final String CCE_ERROR_TRANSFER_SUMMARY_NOT_FOUND = "7519";
	public static final String CCE_ERROR_INVALID_TRANSFER_TYPE = "7587";
	public static final String CCE_ERROR_INVALID_TRANSFER_SUB_TYPE = "75871";

	public static final String CCE_ERROR_USER_DETAIL_NOT_FOUND = "7520";
	public static final String CCE_ERROR_SUBSCRIBER_DETAIL_NOT_FOUND = "7521";
	public static final String CCE_ERROR_PIN_HANDLING_REQUEST_FAILED = "7522";
	public static final String CCE_XML_ERROR_USER_MSISDN_NOT_FOUND = "7523";
	public static final String CCE_ERROR_USER_ALREADY_SUSPENDED = "7524";
	public static final String CCE_ERROR_USER_ALREADY_ACTIVE = "7525";
	public static final String CCE_ERROR_DATE_DIFF_ERROR = "7526";
	public static final String CCE_ERROR_INVALID_MSISDN = "7527";
	public static final String CCE_ERROR_USER_STATUS_NEW = "7528";
	public static final String CCE_ERROR_USER_STATUS_INVALID = "7529";

	public static final String CCE_ACCE_ALREADY_UNBLOCK = "7530";
	public static final String CCE_ACCE_REQUEST_TXN_FAILED = "7531";

	// receiver checks
	public static final String XML_ERROR_INVALIDMESSAGEFORMAT = "7532";
	public static final String XML_ERROR_NO_SUCH_USER = "7533";
	public static final String XML_ERROR_CAT_GATETYPENOTALLOWED = "7534";
	public static final String XML_ERROR_GEODOMAIN_SUSPEND = "7535";
	public static final String XML_ERROR_SENDER_SUSPEND = "7536";
	public static final String XML_ERROR_SENDER_BLOCKED = "7537";
	public static final String XML_ERROR_SENDER_DELETE_REQUEST = "7538";
	public static final String XML_ERROR_SENDER_SUSPEND_REQUEST = "7539";
	public static final String XML_NETWORK_NOT_ACTIVE = "7540";
	public static final String XML_ERROR_INVALID_PSWD = "7541";
	public static final String XML_ERROR_USER_ROLE_UNAVAILABLE = "7542";
	public static final String XML_ERROR_INVALID_EMPCODE = "7543";
	public static final String XML_ERROR_INVALID_CATCODE = "7544";
	public static final String XML_ERROR_INVALID_LOGINID = "7545";
	public static final String XML_ERROR_CHNL_USER_NOTEXIST = "7546";
	public static final String XML_ERROR_EXCEPTION = "7547";
	public static final String XML_ERROR_EXT_NETWORK_CODE = "7548";
	public static final String XML_ERROR_CHANGE_DEFAULT_PASSWD = "7549";

	// whitelist
	public static final String CCE_XML_ERROR_MSISDN_NOT_IN_WHITELIST = "7550";
	// view channel user
	public static final String CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4MSISDN = "7552";
	public static final String CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4LOGINID = "7553";
	public static final String CCE_XML_ERROR_CU_DETAILS_ROLES_NOT_FOUND = "7554";
	public static final String CCE_XML_ERROR_ONE_VALUE_REQUIRED = "7555";

	// Bar User
	public static final String CCE_XML_ERROR_INVALID_BARUSERTYPE_VALUE = "7560";
	public static final String CCE_XML_ERROR_INVALID_BARTYPELIST_EMPTY = "7561";
	public static final String CCE_XML_ERROR_INVALID_BARTYPE_VALUE = "7562";
	public static final String CCE_XML_ERROR_ALREADY_BARRED = "7563";
	public static final String CCE_XML_ERROR_BARRED_USER_NOTUPDATE = "7568";
	public static final String CCE_XML_ERROR_BARRED_USER_NOTEXISTINLIST = "7569";

	// iccid-msisdn
	public static final String CCE_XML_ERROR_ATLEAST_ONE_VALUE_REQUIRED = "7570";
	public static final String CCE_XML_ERROR_ICCID_DETAILS_NOT_FOUND4ICCID = "7571";
	public static final String CCE_XML_ERROR_CU_ICCID_DETAILS_NO_MSISDN = "7572";
	public static final String CCE_XML_ERROR_ICCID_NOT_MATCHING_MSISDN = "7573";
	public static final String CCE_XML_ERROR_ICCID_DETAILS_NOT_FOUND4MSISDN = "7574";
	public static final String CCE_XML_SUCCESS_ICCID_DETAILS_FOUND4MSISDN = "7575";

	// register-deregister subscriber
	public static final String CCE_XML_ERROR_USER_LAST_REQ_UNDERPROCESS = "7576";
	public static final String CCE_XML_ERROR_USER_NOT_REGISTERED = "7577";

	public static final String CCE_XML_ERROR_MSISDN_DETAILS_NOTFOUND_ROUTING_LIST = "7580";
	// O2C Transfer enquiry
	public static final String CCE_ERROR_INVALID_TRF_CATEGORY = "7581";
	public static final String CCE_ERROR_USER_NOTIN_DOMAIN = "7582";
	public static final String CCE_ERROR_SENDER_NOT_AUTHORIZE_DOMAIN = "7583";
	public static final String CCE_ERROR_USER_NOTIN_GEOGRAPHY = "7584";
	public static final String CCE_ERROR_SENDER_NOT_AUTHORIZE_GEOGRAPHY = "7585";
	public static final String CCE_ERROR_ACC_CTRL_NOT_UPDATED = "7586";
	// 7587 being used up there, so dont use Ankit Singhal

	// Suspend resume channel user
	public static final String CCE_XML_ERROR_USER_ALREADY_ACTIVE = "7588";
	public static final String CCE_XML_ERROR_USER_ALREADY_SUSPEND = "7589";
	public static final String CCE_XML_ERROR_CHANNEL_USER_NOTUPDATE = "7590";
	public static final String CCE_XML_ERROR_INVALID_ACTION = "7591";
	public static final String CCE_XML_ERROR_INVALID_LOGIN_ID_FOR_MSISDN = "7592";
	public static final String CCE_XML_ERROR_USER_BALANCE_NOT_FOUND = "7593";

	// added by Sourabh for C2SBillPaymentcontroller
	public static final String ERR_INVALID_AMOUNT_UB = "5555";
	public static final String ERR_NOTFOUND_SERIES_TYPE_UB = "5556";
	public static final String C2S_ERR_SELF_UTILITYBIL_NTALLOWD = "5557";
	public static final String C2S_ERR_SNDR_COMMPROFILE_SUSPEND_UB = "5558";
	public static final String C2S_ERR_SNDR_TRANPROFILE_SUSPEND_UB = "5559";
	public static final String C2S_ERR_SNDR_OUT_SUSPEND_UB = "5560";
	public static final String ERR_RECEIVER_BARRED_UB = "5561";
	public static final String C2S_ERR_EXCEPTION_UB = "5562";
	public static final String C2S_RECEIVER_FAIL_UB = "5563";
	public static final String FAIL_R_UB = "5564";
	public static final String C2S_ERR_NOTFOUND_SRVCINTERFACEMAPPING_UB = "5565";
	public static final String INTERFACE_NOT_ACTIVE_UB = "5566";
	public static final String REQUEST_IN_QUEUE_UB = "5567";
	public static final String REQUEST_REFUSE_UB = "5568";
	public static final String C2S_SENDER_UNDERPROCESS_B4VAL_UB = "5569";
	public static final String C2S_SENDER_UNDERPROCESS_UB = "5570";
	public static final String C2S_RECEIVER_UNDERPROCESS_UB = "5571";
	public static final String C2S_RECEIVER_SUCCESS_UB = "5572";
	public static final String C2S_RECEIVER_AMBIGOUS_KEY_UB = "5573";
	public static final String C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL_UB = "5574";
	public static final String C2S_SENDER_SUCCESS_UB = "5575";
	public static final String C2S_RECEIVER_FAIL_KEY_UB = "5576";
	public static final String CHNL_ERROR_RECR_IDENTITY_BLANK = "5577";
	public static final String CHNL_ERROR_RECR_IDENTITY_NUM_NOTINRANGE = "5578";
	public static final String CHNL_ERROR_RECR_NOTIF_NUM_LEN_NOTSAME = "5579";
	public static final String CHNL_ERROR_RECR_NOTIFPREFIX_NOTFOUND_RECEIVERNETWORK = "5580";
	public static final String INVALID_PAYEE_NOTIF_NUMBER = "5581";
	public static final String INVALID_PAYEE_NOTIF_LANG_UB = "5582";
	public static final String INVALID_MESSAGE_FORMAT_UB = "5583";
	public static final String SENDER_UNDERPROCESS_SUCCESS_UB = "5584";
	public static final String CHNL_ERROR_RECR_ID_NOTNUMERIC = "5585";

	public static final String ERROR_MISSING_SENDER_IDENTIFICATION = "313";
	// Keys are added for DP6 Parser.
	public static final String CHNL_ERROR_SNDR_ICCID_NOTFOUND = "7351";
	public static final String CHNL_ERROR_SNDR_BLANK_MESSAGE_DP6 = "7352";
	public static final String CHNL_ERROR_SNDR_MSG_NOT_DECRYPT_DP6 = "7353";
	public static final String CHNL_ERROR_SNDR_GEN_SCRTY_EXC_DP6 = "7354";
	public static final String CHNL_ERROR_SNDR_EXC348_EXC_DP6 = "7355";
	public static final String CHNL_ERROR_SNDR_ENCR_KEY_NOTFOUND_DP6 = "7356";
	public static final String CHNL_ERROR_SNDR_WRONG_UDH_DP6 = "7357";
	public static final String CHNL_ERROR_PLAIN_SMS_NOT_ALLOWED_DP6 = "7358";
	public static final String CHNL_ERROR_BINARY_SMS_NOT_ALLOWED_DP6 = "7359";
	public static final String WML_SUCCESS_RESPONSE_DP6 = "7360";
	public static final String WML_FAILURE_RESPONSE_DP6 = "7361";

	// For Resume Suspend Process
	public static final String PROCESS_RESUMESUSPEND_INVALID_SERVICE = "7880";
	public static final String PROCESS_RESUMESUSPEND_DB_NOT_UPDATED = "7881";
	public static final String PROCESS_RESUMESUSPEND_CACHE_NOT_UPDATED = "7882";
	public static final String PROCESS_RESUMESUSPEND_SUCCESS = "7883";
	public static final String PROCESS_RESUMESUSPEND_REVERT = "7884";
	public static final String PROCESS_RESUMESUSPEND_SUSPEND_MSG = "7897";

	public static final String CHNL_ERROR_LRCH_INVALIDMESSAGEFORMAT = "7885";
	public static final String LAST_RECHARGE_STATUS_NOT_FOUND = "7886";
	public static final String LAST_C2S_RECHARGE_STATUS_SUCCESS = "7887";
	public static final String LAST_C2S_RECHARGE_STATUS_FAIL = "7888";
	public static final String LAST_C2S_RECHARGE_STATUS_AMBIGUOUS = "7889";
	public static final String LAST_C2S_RECHARGE_STATUS_UNDER_PROCESS = "7890";
	public static final String LAST_C2S_RECHARGE_STATUS_DEFAULT = "7891";

	public static final String EXT_XML_ERROR_NO_SUCH_USER = "7892";
	public static final String EXT_XML_ERROR_INVALID_PSWD = "7893";
	public static final String EXT_XML_ERROR_INVALID_EXTCODE = "7894";
	public static final String EXT_XML_ERROR_INVALID_LOGINID = "7895";
	public static final String LAST_RECHARGE_TXN_NOT_BY_YOU = "7896";

	// VOMS integrations start
	// Voucher File upload errors added by Siddhartha
	public static final String VOUCHER_ERROR_CONN_NULL = "5901";
	public static final String VOUCHER_ERROR_DIR_CONTAINS_NO_FILES = "5902";
	public static final String VOUCHER_ERROR_HEADER_INFO = "5903";
	public static final String VOUCHER_ERROR_FILE_DOES_NOT_EXIST = "5904";
	public static final String VOUCHER_ERROR_INVALID_FILENAME_FORMAT = "5905";
	public static final String VOUCHER_ERROR_FILE_EXT_NOT_DEFINED = "5906";
	public static final String VOUCHER_ERROR_DIR_NOT_EXIST = "5907";
	public static final String VOUCHER_ERROR_FILENAME_SEPARATOR = "5908";
	public static final String VOUCHER_MISSING_CONST_FILE = "5909";
	public static final String VOUCHER_MISSING_LOG_FILE = "5910";
	public static final String VOUCHER_ERROR_PROCESS_ENTRY_NOT_FOUND = "5911";
	public static final String VOUCHER_ERROR_PROCESS_ALREADY_RUNNING = "5912";
	public static final String VOUCHER_ERROR_PROCESS_UPDATE_STATUS = "5913";
	public static final String VOUCHER_ERROR_PARSER_CLASS_NOT_INSTANTIATED = "5914";
	public static final String VOUCHER_MISSING_INITIAL_FILES = "5915";
	public static final String VOUCHER_ERROR_PASSWORD_RETREIVAL = "5916";
	public static final String VOUCHER_ERROR_INVALID_ACCESS = "5917";
	public static final String VOUCHER_ERROR_INVALID_RECORD_COUNT = "5918";
	public static final String VOUCHER_ERROR_TOTAL_ERROR_COUNT = "5919";
	public static final String VOMSBTCID = "5920";
	public static final String VOUCHER_ERROR_MOVE_LOCATION_NOT_EXIST = "5921";
	public static final String VOUCHER_ERROR_FILE_NOT_MOVED_SUCCESSFULLY = "5922";
	public static final String VOUCHER_INVALID_LOGIN = "5923";
	public static final String VOUCHER_ERROR_INSERTION_ERROR = "5924";
	public static final String VOUCHER_ERROR_PRODUCT_NOT_EXISTS = "5925";
	public static final String VOUCHER_UPLOAD_INTERNAL_ERROR_DATA_RETRIEVAL = "5926";
	public static final String VOUCHER_UPLOAD_PROCESS_INVALID_USER = "5927";
	public static final String VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR = "5928";
	public static final String VOUCHER_ERROR_NUMBER_REC_MORE_THAN_ALLWD = "5929";
	public static final String VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR = "5930";
	public static final String VOUCHER_UPLOAD_PROCESS_RECORDS_MISMATCH = "5931";
	public static final String VOUCHER_UPLOAD_PROCESS_NO_RECORDS_ERROR = "5932";
	public static final String VOUCHER_PIN_ALREADY_EXIST_IN_DB = "5933";
	public static final String VOUCHER_SERIAL_NO_ALREADY_EXIST_IN_DB = "5934";

	// Inventory report creation errors added by Amit Singh
	public static final String VOMS_INVRPT_ERROR_EXCEPTION = "88840";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_CON = "88841";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_CONSTANTS = "88842";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_OUT_STREAM = "88843";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_MAIL = "88844";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_VOUCHERINFO = "88845";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_VOARCHINFO = "88846";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_VOARCOUNT = "88847";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_VOSUMMINFO = "88848";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_VOBATCHLIST = "88849";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_FORMATVALUE = "88850";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_MAKEDIR = "88851";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_CONST_FILE_MISSING = "88852";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_LOG_FILE_MISSING = "88853";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_LOCALE = "88854";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_EMAIL_SENDING = "88855";
	public static final String VOMS_INVRPT_ERROR_EXCEPTION_ALREADY_EXECUTED = "88856";
	public static final String CHNL_ERROR_SELF_VOUCHER_DIST_NOTALLOWED = "88857";
	public static final String EVD_RECEIVER_SUCCESS_RET = "88858";
	public static final String EVD_RECEIVER_SUCCESS_CUST = "88859";
	public static final String EVD_RECEIVER_FAIL_RET = "88860";
	public static final String EVD_RECEIVER_FAIL_CUST = "88861";
	public static final String EVD_RECEIVER_SUCCESS_CUST_MSG = "88862";
	public static final String EVD_RECEIVER_SUCCESS_RET_MSG = "88863";

	public static final String CHNL_ERROR_SENDER_OUT_SUSPEND_EVD = "6687";
	public static final String CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND_EVD = "6688";
	public static final String CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND_EVD = "6689";
	public static final String ERROR_INVALID_AMOUNT_EVD = "6690";
	public static final String ERROR_NOTFOUND_SERIES_TYPE_EVD = "6691";
	public static final String C2S_ERROR_EXCEPTION_EVD = "6692";
	public static final String C2S_RECEIVER_FAIL_EVD = "6693";
	public static final String FAIL_R_EVD = "6694";
	public static final String INTERFACE_NOT_ACTIVE_VMS = "6695";
	public static final String EVD_RECEIVER_SUCCESS = "6696";
	public static final String EVD_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "6697";
	public static final String EVD_SENDER_SUCCESS = "6698";
	public static final String C2S_RECEIVER_AMBIGOUS_KEY_EVD = "6699";
	public static final String C2S_RECEIVER_FAIL_KEY_EVD = "6700";
	public static final String C2S_SENDER_UNDERPROCESS_B4VAL_EVD = "6701";
	public static final String C2S_ERROR_NOTFOUND_SERVICEINTERFACEMAPPING_EVD = "6702";
	public static final String C2S_RECEIVER_UNDERPROCESS_VMS = "6703";
	public static final String VOMS_INTERFACE_NOT_FOUND = "6704";
	public static final String PIN_MESSAGE_FOR_R = "6705";
	public static final String BIN_PIN_MESSAGE_FOR_R = "67052";
	public static final String PIN_MESSAGE_FOR_C = "6706";
	public static final String EVD_DELIVERY_STATUS_NOT_FOUND = "6707";
	public static final String EVD_TRANSACTION_DETAILS_NOT_FOUND = "6708";
	public static final String EVD_TRANSACTION_NOT_UNDERPROCESS = "6709";
	public static final String INSTANCE_CODE_NOT_FOUND = "6710";
	public static final String VMS_PIN_SENT_FAIL = "6711";

	// added by siddhartha for pin resend
	public static final String VOUCHER_PIN_RESEND = "6600";
	public static final String VOUCHER_DUPLICATE_SERIAL_NO_ERROR = "6601";
	// VOMS integrations end

	public static final String CHNL_ERROR_SNDR_MAX_PER_TRF_FAIL = "6602";

	// For Voucher Upload Process
	public static final String VOUCHER_FILE_HEADER_ERROR = "6603";
	public static final String VOUCHER_FILE_HEADER_QUANTITY_ERROR = "6604";
	public static final String VOUCHER_FILE_HEADER_PINLENGTH_ERROR = "6605";
	public static final String VOUCHER_FILE_HEADER_CURRENCY_ERROR = "6606";

	// Voucher alert process
	public static final String LOW_VOUCHER_ALERT_MSG = "6607";
	public static final String LOW_VOUCHER_ALERT_MSG_SUBKEY = "6608";
	public static final String VOMS_ALERT_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "6609";
	public static final String VOMS_ALERT_COULD_NOT_UPDATE_MAX_DONE_DATE = "6610";
	public static final String VOMS_ALERT_ERROR_EXCEPTION = "6611";

	// Routing Upload Process
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

	public static final String PROCESS_RESUMESUSPEND_INT_MSG = "7898";
	// added for MNP
	public static final String MNP_MSISDN_PORTPREFIX_NOTDEFINED = "7777";
	public static final String ERROR_CHNL_MSISDN_ALREADY_EXIST = "7778";
	public static final String ERROR_MNP_MSISDN_UPDATE_FAIL = "7789";
	public static final String MNP_MSISDN_UNSUPPORTED_NETWORK = "7790";
	public static final String MNP_USER_SUSPEND_FAIL = "7791";
	public static final String MNP_NOT_VALID_MSISDN = "7792";
	public static final String MNP_MSISDN_INPORTPREFIX_NOTDEFINED = "7793";

	// Auto Resume Suspend Error codes (Related to Interface closer and
	// ResumeSuspend Process)
	public static final String AUTO_RESUMESUSPEND_REVERT = "7899";
	public static final String AUTO_RESUMESUSPEND_SUCCESS = "7900";

	// MNP process
	public static final String MNP_NUMBER_ARG_MISSING = "7804";
	public static final String MNP_ERROR_FILEPATH_NULL = "7794";
	public static final String MNP_ERROR_ZERO_FILESIZE = "7797";
	public static final String MNP_ERROR_NORECORD_IN_FILE = "7798";
	public static final String MNP_ERROR_DIR_NOT_EXIST = "7795";
	public static final String MNP_ERROR_CONN_NULL = "7796";
	public static final String MNP_ERROR_IN_FILE_MOVE = "7799";
	public static final String MNP_MISSING_CONST_FILE = "7800";
	public static final String MNP_MISSING_LOG_FILE = "7801";
	public static final String MNP_UPLOAD_PROCESS_GENERAL_ERROR = "7802";
	public static final String MNP_USER_ALREAY_SUSPEND = "7803";
	public static final String MNP_INVALID_SERIES_TYPE = "7805";
	public static final String MNP_INVALID_PORT_TYPE = "7806";
	// MVD
	public static final String MVD_REQ_MORE_THAN_ALLOWED = "18881";
	public static final String C2S_ERROR_EXCEPTION_MVD = "18883";
	public static final String CHNL_ERROR_SENDER_OUT_SUSPEND_MVD = "18884";
	public static final String CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND_MVD = "18885";
	public static final String CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND_MVD = "18886";
	public static final String ERROR_INVALID_AMOUNT_MVD = "18887";
	// c2s receiver fail message if transfer id is available
	public static final String C2S_RECEIVER_FAIL_MVD = "18888";
	// c2s receiver fail message if transfer id is not available
	public static final String FAIL_R_MVD = "18889";
	public static final String C2S_RECEIVER_AMBIGOUS_KEY_MVD = "18892";
	public static final String C2S_RECEIVER_FAIL_KEY_MVD = "18893";
	public static final String C2S_SENDER_UNDERPROCESS_B4VAL_MVD = "18894";
	// c2s receiver validation fail message
	public static final String MVD_RECEIVER_FAIL = "18895";
	// pin and serial number message to sender
	public static final String MVD_PIN_MESSAGE = "18896";
	// required number of vouchers are not available
	public static final String VOMS_NOT_ENOUGH_VOUCHERS = "18897";

	public static final String MVD_CHNL_ERROR_SNDR_DAILY_OUT_CTREACHED = "18898";
	public static final String MVD_CHNL_ERROR_SNDR_DAILY_OUT_VALREACHED = "18899";
	public static final String MVD_CHNL_ERROR_SNDR_WEEKLY_OUT_CTREACHED = "18900";
	public static final String MVD_CHNL_ERROR_SNDR_WEEKLY_OUT_VALREACHED = "18901";
	public static final String MVD_CHNL_ERROR_SNDR_MONTHLY_OUT_CTREACHED = "18902";
	public static final String MVD_CHNL_ERROR_SNDR_MONTHLY_OUT_VALREACHED = "18903";

	public static final String MVD_REC_LAST_SUCCESS_REQ_BLOCK_R_PRE = "18904";
	public static final String MVD_REC_LAST_SUCCESS_REQ_BLOCK_R_POST = "18905";
	public static final String MVD_AMOUNT_TRANSFERS_DAY_EXCEEDED_R_PRE = "18906";
	public static final String MVD_AMOUNT_TRANSFERS_DAY_EXCEEDED_R_POST = "18907";
	public static final String MVD_NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_PRE = "18908";
	public static final String MVD_NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_POST = "18909";
	public static final String MVD_C2S_SENDER_UNDERPROCESS = "18910";
	public static final String MVD_INVALID_MRP_REQUESTED = "18911";
	public static final String MVD_VOMS_ERROR_UPDATION = "18912";
	public static final String MVD_VOMS_ERROR_INSERTION_AUDIT_TABLE = "18913";
	public static final String MVD_C2S_SENDER_CREDIT_SUCCESS = "18914";

	public static final String MVD_CHNL_ERROR_SNDR_MAX_PER_TRF_FAIL = "18915";
	public static final String MVD_CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL = "18916";
	public static final String MVD_CHNL_ERROR_SNDR_INSUFF_BALANCE = "18917";
	public static final String MVD_CHNL_ERROR_SNDR_AMT_NOTBETWEEN_MINMAX = "18918";

	public static final String REQUEST_REFUSE_FROM_NWLOAD = "13070";
	public static final String REQUEST_REFUSE_FROM_INTLOAD = "23070";
	public static final String REQUEST_REFUSE_FROM_TXNLOAD = "33070";
	public static final String INVALID_QUANTITY = "18920";
	public static final String CCE_ERROR_REMARKS_LEN_MORE_THAN_ALLOWED = "7594";

	// added for OCI changes regarding to change PIN on 1st request
	public static final String CHNL_FIRST_REQUEST_PIN_CHANGE = "7061";

	// added for Moldova Requirement regarding to change PIN on WHEN PIN IS
	// DEFAULT
	public static final String CHNLUSR_CHANGE_DEFAULT_PIN = "7062";

	public static final String RETRY_CONVERSION_RATE = "10000";
	public static final String INTERFACE_LIST_NOTFOUND = "10001";
	public static final String SENDER_CONVERSION_RATE_NOTFOUND = "10002";
	public static final String RECEIVER_CONVERSION_RATE_NOTFOUND = "10003";

	public static final String AUTO_CACHEUPDATE_FAIL = "79001";
	public static final String AUTO_CACHEUPDATE_STATUS = "79002";

	// for Get number back service
	public static final String GRACE_DATE_IS_WRONG = "7595";
	public static final String RECHARGE_IS_NOT_ALLOW = "7596";
	public static final String RECHARGE_AMOUNT_IS_NOT_SUFFICIENT = "7597";
	public static final String TRANSFER_VALUE_IS_NOT_VALID = "7599";
	// send different success messages for C2S and P2P for Get number back
	// service
	public static final String C2S_RECEIVER_GET_NUMBER_BACK_SUCCESS = "226";
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
	public static final String VOUCHER_FILE_MORE_RECORDS = "19031";

	// Error codes for ICCID-MSISDN mapping [Zafar Abbas]
	public static final String XML_ERROR_ICCID_MSISDN_REQUIRED = "19032";
	public static final String ICCID_MSISDN_ALREADY_MAPPED = "19033";
	public static final String ERROR_ICCID_MSISDN_MAPPING_FAIL = "19034";
	public static final String INVALID_ICCID_FOR_MAPPING = "19035";
	public static final String XML_ERROR_ICCID_MSISDN_NULL = "19036";
	public static final String XML_ERROR_ICCID_IS_NULL = "19037";
	public static final String XML_ERROR_MSISDN_IS_NULL = "19038";
	// Error codes 19032 to 19040 are reserved for ICCID-MSISDN mapping

	// added by PN for Cell Plus prepaid controller
	// public static final String P2P_SENDER_NOT_ACTIVE_ON_IN="22001";
	public static final String ERROR_INVALID_AMOUNT_APL_FEE = "22002";

	// for c2s enquiry through external system(increments in error codes of card
	// group)
	public static final String CARD_GROUP_SET_IDNOT_FOUND = "21004";
	public static final String NO_SLAB_FOR_CARD_GROUP_SETID = "21005";
	public static final String CARD_GROUP_SLAB_NOT_FOUND = "21006";
	public static final String SERVICE_NOT_ALLOW_FOR_C2S_ENQUIRY_TO_USER = "21007";
	public static final String SERVICE_NOT_ALLOW_FOR_C2S_ENQUIRY_TO_THIS_NETWORK = "21008";

	// for c2s enquiry status
	public static final String C2S_ENQUIRY_SUCCESS = "21001";
	public static final String C2S_ENQUIRY_FAIL = "21002";

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

	// Error Codes for Gift Recharge
	public static final String CHNL_ERROR_SAME_CHNLUSER_RECEIVER_NTALLOWD_GIFTRECHARGE = "140001";
	public static final String CHNL_ERROR_SAME_GIFTER_RECEIVER_NTALLOWD_GIFTRECHARGE = "140002";
	public static final String CHNL_ERROR_SAME_CHNLUSER_GIFTER_NTALLOWD_GIFTRECHARGE = "140003";
	public static final String CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND_GIFTRECHARGE = "140004";
	public static final String CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND_GIFTRECHARGE = "140005";
	public static final String CHNL_ERROR_SENDER_OUT_SUSPEND_GIFTRECHARGE = "14006";
	public static final String C2S_RECEIVER_FAIL_GIFTRECHARGE = "14007";
	public static final String C2S_SENDER_UNDERPROCESS_B4VAL_GIFTRECHARGE = "14008";
	public static final String C2S_GIFTER_AMBIGOUS_KEY_GIFTRECHARGE = "14009";
	public static final String C2S_SENDER_SUCCESS_GIFTRECHARGE = "14010";
	public static final String C2S_GIFTER_SUCCESS_GIFTRECHARGE = "14011";
	public static final String C2S_GIFTER_UNDERPROCESS_GIFTRECHARGE = "14012";
	public static final String C2S_SENDER_UNDERPROCESS_GIFTRECHARGE = "14013";
	public static final String C2S_GIFTER_FAIL_KEY_GIFTRECHARGE = "14014";
	public static final String C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL_GIFTRECHARGE = "14015";
	public static final String C2S_RECEIVER_SUCCESS_GIFTRECHARGE = "14016";
	public static final String CHNL_ERROR_GIFTER_MSISDN_NOTNUMERIC_GIFTRECHARGE = "14017";
	public static final String CHNL_ERROR_GIFTER_MSISDN_NOTINRANGE_GIFTRECHARGE = "14018";
	public static final String CHNL_ERROR_GIFTER_MSISDN_LEN_NOTSAME_GIFTRECHARGE = "14019";
	public static final String CHNL_ERROR_GIFTER_NAME_NULL_GIFTRECHARGE = "14020";
	public static final String CHNL_ERROR_GIFTER_MSISDN_NULL_GIFTRECHARGE = "14021";
	// public static final String
	// C2S_RECEIVER_FAIL_FORGIFTER_GIFTRECHARGE="14021";
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
	public static final String C2S_ERROR_EXCEPTION_TKING_TIME_TILL_VAL = "251";
	public static final String C2S_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP = "252";
	public static final String P2P_ERROR_EXCEPTION_TKING_TIME_TILL_VAL_R = "253";
	public static final String P2P_ERROR_EXCEPTION_TKING_TIME_TILL_VAL_S = "255";
	public static final String P2P_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP = "254";

	// change for reconciliation
	public static final String VOMS_RECON_INVALID_VOUCHER_STATUS = "5935";
	// Changed to show cancel transactions in DWH process
	public static final String TXN_STATUS_CANCEL = "240";

	// EVD Controller changes by PN date(23/04/08)
	public static final String VOUCHER_TO_BE_SENT_INTERFACE_NOT_DEFINED = "5936";
	public static final String VOUCHER_NOT_FOUND = "5937";

	// Additional commision deduction process
	public static final String ADDCOMMDDT_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "5938";
	public static final String ADDCOMMDDT_COULD_NOT_UPDATE_MAX_DONE_DATE = "5939";
	public static final String ADDCOMMDDT_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "5940";
	public static final String ADDCOMMDDT_ERROR_EXCEPTION = "5941";
	public static final String ADDCOMMDDT_AMB_OR_UP_TXN_FOUND = "5942";
	public static final String ADDCOMMDDT_ERROR_CREDIT_DEBIT_STOCK = "5943";
	public static final String ADDCOMMDDT_ERROR_UPDATING_NW_STOCK_TXN = "5944";
	public static final String ADDCOMMDDT_ERROR_UPDATING_ADJ_DATA = "5945";
	public static final String ERROR_NW_STOCK_NOT_EXIST = "5946";
	public static final String ERROR_NW_STOCK_LESS = "5947";
	// Hourly Transaction Count Process.
	public static final String SERVICE_TRAN_COUNT_MESSAGE = "4445";
	public static final String HOURLY_SERVICE_TRAN_COUNT_MESSAGE = "4446";
	public static final String DAILY_SERVICE_TRAN_COUNT_MESSAGE = "4447";
	public static final String COMBINE_HOURLY_DAILY_TRAN_COUNT_MESSAGE = "4448";
	public static final String MESSAGE_FOR_HOURLY_TRAN_COUNT = "4449";

	// schedule top up management
	public static final String GENERAL_PROCESSING_ERROR = "4455";
	public static final String RM_ERROR_RESTRICTED_SUBSCRIBER_RECHARGE_NOT_ALLOWED = "4456";
	// added for bonus
	public static final String C2S_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS = "229";
	public static final String C2S_RECEIVER_SUCCESS_WITH_BONUS = "236";
	public static final String C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "237";
	public static final String P2P_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS = "238";
	public static final String P2P_RECEIVER_SUCCESS_WITH_BONUS = "239";
	public static final String P2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "241";
	public static final String C2S_RECEIVER_SUCCESS_ALL_BALANCES = "243";

	// added for card group slab suspend/resume
	public static final String CARD_GROUP_SLAB_SUSPENDED = "2075";

	public static final String REC_BAL_LESS_TO_REQ_AMT = "21019";
	public static final String REC_BAL_LESS_TO_REQ_AMT_S = "21020";
	public static final String RM_ERROR_RESTRICTED_SUB_RECHARGE_NOT_ALLOWED_P2P = "4457";
	public static final String EXT_XML_ERROR_INVALID_MSISDN = "7901";

	// added by Gopal for batch C2C transfer
	public static final String C2C_CHNL_CHNL_TRANSFER_SMS1 = "222002";
	public static final String C2C_CHNL_TO_CHNL_TRANSFER_SMS1 = "222005";
	public static final String C2C_CHNL_CHNL_TRANSFER_SMS2 = "222003";
	public static final String C2C_CHNL_CHNL_TRANSFER_SMS_BALSUBKEY = "222004";

	public static final String O2C_EXTGW_DUPLICATE_TRANSCATION = "115";
	public static final String P2P_ERROR_BLANK_AMOUNT = "1010";
	public static final String C2S_ERROR_BLANK_AMOUNT = "6020";

	public static final String O2CEXT_ERROR_EXCEPTION = "5950";
	public static final String O2C_EXT_CONST_PARAM_NOT_FOUND = "5951";
	public static final String O2CEXT_PROCESS_UPTO_DATE_NOT_FOUND = "5952";

	// Added for low network stock alert process
	public static final String LOW_STOCK_ALERT_MSG = "12017";

	// errorcode for fix line customer recharge
	public static final String FIXLINE_NOTIFICATION_MSISDN_BLANK = "5101";
	public static final String FIXLINE_NOTIFICATION_MSISDN_NOTINRANGE_FIXLINE = "5102";
	public static final String FIXLINE_NOTIFICATION_MSISDN_LEN_NOTSAME_FIXLINE = "5103";
	public static final String FIXLINE_RECEIVER_UNDERPROCESS = "5104";
	public static final String FIXLINE_SENDER_SUCCESS = "5105";
	public static final String FIXLINE_RECEIVER_GET_NUMBER_BACK_SUCCESS = "5106";
	public static final String FIXLINE_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS = "5107";
	public static final String FIXLINE_RECEIVER_SUCCESS = "5108";
	public static final String FIXLINE_RECEIVER_SUCCESS_WITH_BONUS = "5109";
	public static final String FIXLINE_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "5110";
	public static final String FIXLINE_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "5111";
	public static final String FIXLINE_SENDER_UNDERPROCESS = "5112";
	public static final String FIXLINE_SENDER_UNDERPROCESS_B4VAL = "5113";
	public static final String FIXLINE_RECEIVER_AMBIGOUS_KEY = "5114";
	public static final String FIXLINE_RECEIVER_FAIL_KEY = "5115";
	public static final String FIXLINE_NOTIFICATION_MSISDN_NOTNUMERIC = "5116";

	// Error code for broadband customer recharge
	public static final String C2S_NOTIFICATION_MSISDN_BLANK = "5601";
	public static final String C2S_NOTIFICATION_MSISDN_NOTINRANGE = "5602";
	public static final String C2S_NOTIFICATION_MSISDN_LEN_NOTSAME = "5603";
	public static final String C2S_NOTIFICATION_MSISDN_NOTNUMERIC = "5604";

	public static final String C2S_BROADBAND_RECEIVER_UNDERPROCESS = "5610";
	public static final String C2S_BROADBAND_SENDER_SUCCESS = "5611";
	public static final String C2S_BROADBAND_RECEIVER_GET_NUMBER_BACK_SUCCESS = "5612";
	public static final String C2S_BROADBAND_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS = "5613";
	public static final String C2S_BROADBAND_RECEIVER_SUCCESS = "5614";
	public static final String C2S_BROADBAND_RECEIVER_SUCCESS_WITH_BONUS = "5615";
	public static final String C2S_BROADBAND_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "5616";
	public static final String C2S_BROADBAND_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "5617";
	public static final String C2S_BROADBAND_SENDER_UNDERPROCESS = "5618";
	public static final String C2S_BROADBAND_SENDER_UNDERPROCESS_B4VAL = "5619";
	public static final String C2S_BROADBAND_RECEIVER_AMBIGOUS_KEY = "5620";
	public static final String C2S_BROADBAND_RECEIVER_FAIL_KEY = "5621";
	public static final String C2S_RESET_PIN_EXPIRED = "4329";
	public static final String CHNL_ERROR_SNDR_FORCE_CHANGE_RESETPIN = "7065";

	public static final String ORDER_MOBILE_RECEIVER_SUCCESS = "5701";
	public static final String ORDER_MOBILE_SENDER_SUCCESS = "5702";
	public static final String PARENT_NOT_FOUND = "5703";
	public static final String ORDER_MOBILE_QUANTITY_NOTNUMERIC = "5704";
	public static final String ORDER_MOBILE_QUANTITY_LENGTHINVALID = "5705";
	public static final String ORDER_MOBILE_QUANTITY_CANT_BE_ZERO = "5706";
	public static final String ORDER_MOBILE_SENDER_CANT_BE_TOP_LEVEL = "5707";
	public static final String ORDER_MOBILE_RECEIVER_NOT_ACTIVE = "5708";
	public static final String ORDER_MOBILE_RECEIVER_SUSPEND = "5709";

	public static final String ORDER_CREDIT_AMOUNT_NOTNUMERIC = "5710";
	public static final String ORDER_CREDIT_AMOUNT_LENGTHINVALID = "5711";
	public static final String ORDER_CREDIT_AMOUNT_CANT_BE_ZERO = "5712";
	public static final String ORDER_CREDIT_SENDER_CANT_BE_TOP_LEVEL = "5713";
	public static final String REQUESTED_USER_PARENT_NOT_FOUND = "5714";
	public static final String ORDER_CREDIT_RECEIVER_SUSPEND = "5715";
	public static final String ORDER_CREDIT_RECEIVER_NOT_ACTIVE = "5716";
	public static final String ORDER_CREDIT_RECEIVER_SUCCESS = "5717";
	public static final String ORDER_CREDIT_SENDER_SUCCESS = "5718";
	public static final String ERROR_INVALID_REQUEST_FORMAT = "5719";
	public static final String ERROR_INVALID_PRODUCTCODE_FORMAT = "5720";

	public static final String BARRED_CHANEL_FAILED = "5721";
	public static final String BARRED_CHANEL_SUCCESS = "5722";
	public static final String CHANEL_USER_ALREADY_BARRED = "5723";
	public static final String ORDER_CREDIT_ERROR_BLANK_AMOUNT = "5724";
	public static final String ORDER_LINE_ERROR_BLANK_AMOUNT = "5725";
	public static final String IMPLICIT_MSG = "242";
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

	// For Activation Mapping
	public static final String MAPPING_PROCESS_EXECUTION_FAILED = "23111";
	public static final String UNABLE_TO_ACCESS_ACTIVATION_MAPPING_RECORDS = "23112";
	public static final String UNABLE_TO_LOAD_SERIES_DETAILS = "23113";
	public static final String UNABLE_TO_LOAD_USERS_DETAILS = "23114";
	public static final String UNABLE_TO_LOAD_PROFILE_DETAILS = "23115";
	public static final String UNABLE_TO_GET_SOURCE_TYPE_FOR_BATCHPROCESS = "23116";
	public static final String UNABLE_TO_GET_FILE_PATH_FOR_BATCHPROCESS = "23117";
	public static final String FAILED_AT_LOADING_PROFILE_SETID_BY_USERID = "23118";
	public static final String FAILED_AT_LOADING_PROFILE_SETID_BY_CATEGORYCODE = "23119";
	public static final String FALED_AT_PROCESS_STATUS_COMPLETE = "23120";
	public static final String ACT_BONUS_CALC_PERIODICITY_NOT_FOUND = "21043";
	// For VAS crbt
	public static final String VAS_RECEIVER_SUCCESS = "7207";
	public static final String VAS_SENDER_SUCCESS = "7210";
	public static final String VAS_RECEIVER_UNDERPROCESS = "7208";
	public static final String VAS_RECEIVER_AMBIGOUS_KEY = "7230";
	public static final String VAS_RECEIVER_FAIL_KEY = "7231";
	public static final String VAS_SENDER_CREDIT_SUCCESS = "7221";
	public static final String VAS_INVALID_SUBSERVICE = "7222";
	// For REQ via SMS
	public static final String REQVIASMS_RECEIVER_SUCCESS = "5731";
	public static final String REQVIASMS_RECEIVER_FAILED = "5732";
	public static final String REQVIASMS_SENDER_SUCCESS = "5733";
	public static final String REQVIASMS_SENDER_FAILED = "5734";
	public static final String REQVIASMS_SENDER_FAILED_RESTRICTED = "5735";
	public static final String REQVIASMS_CHANNEL_USER_NOT_EXIST = "5736";
	public static final String CREDITREQVIASMS_RECEIVER_SUCCESS = "5737";
	public static final String CREDITREQVIASMS_RECEIVER_FAILED = "5738";
	public static final String CREDITREQVIASMS_SENDER_SUCCESS = "5739";
	public static final String CREDITREQVIASMS_SENDER_FAILED = "5740";
	public static final String CREDITREQVIASMS_SENDER_FAILED_RESTRICTED = "5741";
	public static final String CREDITREQVIASMS_RECIEVER_FAILED_RESTRICTED = "5742";
	public static final String CREDITREQVIASMS_BUDDY_AMOUNT_NOT_AVAILABLE = "5743";
	public static final String CREDITREQVIASMS_CHANNELUSER_FAILED = "5744";
	public static final String CREDITREQVIASMS_CHANNELUSER_BARRED = "5745";
	// Message for FOC through external gateway
	public static final String FOC_TRANSFER_EXTGW_RECEIVER = "6166";
	public static final String FOC_INITIATE_TRANSFER_EXTGW_RECEIVER = "6167";
	// Entries for CP2P through web
	public static final String CP2P_WEB_REGISTRATION_SMS = "55555";
	public static final String CP2P_WEB_FORGOTPSWD_SMS = "55551";

	public static final String VOUCHER_FILE_NAME_ERROR = "5995";
	// Error code if default card group not exist in the system wrt service type
	public static final String C2S_ERROR_DEFAULT_CARDGROUP_NOTEXIST = "2076";
	public static final String P2P_ERROR_DEFAULT_CARDGROUP_NOTEXIST = "2077";

	// Error Codes added for IAT.
	public static final String IAT_CNTRY_CODE_NOT_FOUND = "25001";
	public static final String IAT_C2S_EXCEPTION = "25002";
	public static final String IAT_CNTRY_NOT_ACTIVE = "25003";
	public static final String IAT_SEN_REC_SAME_CNTRY = "25004";
	public static final String IAT_ERROR_RECR_MSISDN_NOTINRANGE = "25005";
	public static final String IAT_ERROR_RECR_MSISDN_NOTNUMERIC = "25006";
	public static final String IAT_NW_CNTRY_MAPPING_NOT_FOUND = "25007";
	public static final String IAT_NW_CNTRY_SERVICE_SUSPEND = "25008";
	public static final String IAT_COMMIT_ERR_EXCEPTION = "25009";
	public static final String IAT_NOTIFY_SUCCESS_KEY = "25010";
	public static final String IAT_NOTIFY_FAIL_KEY = "25011";
	public static final String IAT_NOTIFY_AMB_KEY = "25012";
	public static final String IAT_NW_PRFX_NOT_FOUND = "25013";
	public static final String IAT_C2S_SENDER_UNDERPROCESS = "25014";
	// error code for DWH and abg process
	public static final String IAT_DWH_ERROR_EXCEPTION = "25015";
	public static final String IAT_DWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "25016";
	public static final String IAT_DWH_AMB_OR_UP_TXN_FOUND = "25017";
	public static final String IAT_NOT_FOUND = "25018";
	public static final String IAT_AMG_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "25019";

	public static final String IAT_C2S_SENDER_SUCCESS = "25020";
	public static final String IAT_C2S_SENDER_CREDIT_SUCCESS = "25021";
	public static final String IAT_C2S_SENDER_UNDERPROCESS_B4VAL = "25022";
	public static final String IAT_ABG_PROCESS_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE = "25023";
	public static final String IAT_ERROR_NOTIFY_MSISDN_NOTNUMERIC = "25024";
	public static final String IAT_ABG_PRS_ERROR_EXCEPTION = "25025";
	public static final String IAT_NW_SUSPENDED = "25026";

	// vikram for VFE last X transfers
	public static final String LAST_XTRF_SUBKEY = "24120";
	public static final String LAST_XTRF_MAIN_KEY = "24121";

	// vikram for VFE Customer enquiry
	public static final String LAST_XCUST_ENQ_SUBKEY = "24122";
	public static final String LAST_XCUST_ENQ_MAIN_KEY = "24123";

	public static final String CHNL_USER_PIN_MODIFY_STAFF = "25028";
	public static final String CHNL_TRF_SUCCESS_STAFF = "25029";
	public static final String CHNL_WITHDRAW_SUCCESS_STAFF = "25030";
	public static final String CHNL_RETURN_SUCCESS_STAFF = "25031";
	public static final String SMS_LOGINID_NOT_FOUND = "25032";
	public static final String DAILY_TRANSFER_LIST_SUCCESS_STAFF = "25033";
	public static final String NO_USER_EXIST = "25034";
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

	public static final String FILES_NOT_EXIST = "4592";
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
	public static final String STAFF_WEB_SMSPIN_ACTIVATE = "24221"; // only PIN
																	// is active
																	// with no
																	// MSISDN

	public static final String C2SSUBSCRIBER_SENDPSWD_STAFF = "24222";
	public static final String C2SSUBSCRIBER_UNBLOCKSENDPSWD_STAFF = "24223";
	public static final String C2SSUBSCRIBER_UNBLOCKPSWD_STAFF = "24224";
	public static final String C2SSUBSCRIBER_RESETPSWD_STAFF = "24225";

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

	public static final String CHNL_USER_PSWD_MODIFY_STAFF = "14505";
	public static final String CHNL_USER_PSWD_AND_PIN_MODIFY_STAFF = "14506";
	public static final String CHNL_USER_LOGIN_AND_PSWD_AND_PIN_MODIFY_STAFF = "14507";
	public static final String CHNL_USER_LOGIN_MODIFY_STAFF = "14508";
	public static final String CHNL_USER_LOGIN_AND_PSWD_MODIFY_STAFF = "14509";
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
	public static final String CRBT_ERROR_BLANK_SONGCODE = "1001014";

	public static final String EXT_USER_CREATION_USER_MSG = "1001114";
	public static final String EXT_USER_CREATION_PARENT_MSG = "1001115";
	public static final String EXT_USER_CREATION_SENDER_MSG = "1001116";

	public static final String VOUCHER_ERROR_FILE_ALREADY_UPLOADED = "5948";

	// added by ankuj for OMT CR
	public static final String ERROR_NULL_TXNTYPE = "1003001";
	public static final String ERROR_INVALID_DATE_RANGE = "1003002";
	public static final String ERROR_COUNT_EXCEEDS_LIMIT = "1003003";
	public static final String ERROR_INVALID_MESSAGE_FORMAT = "1003004";
	public static final String ERROR_NULL_NWCODE = "1003005";
	public static final String NO_RECORD_AVAILABLE = "1003006";
	public static final String NO_RECORDS_FOUND = "1003007";

	// added by jasmine kaur
	public static final String INVALID_SID_REG_MSG_FORMAT = "1002001";
	public static final String INVALID_SID_LENGTH = "1002002";
	public static final String SID_IS_NOT_NUMERIC = "1002003";
	public static final String SID_IS_NOT_ALPHANUMERIC = "1002004";
	public static final String SID_ALREADY_EXISTING = "1002005";
	public static final String OLD_SID_NOT_MATCHED = "1002006";
	public static final String NOT_VAILD_SID_MODIFICATION = "1002007";
	public static final String OLD_SID_AND_NEW_SID_SAME = "1002008";
	public static final String OLDSID_NEWSID_SAME = "1002009";
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

	// SOS

	public static final String SOS_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "1011001";
	public static final String SOS_COULD_NOT_UPDATE_MAX_DONE_DATE = "1011002";
	public static final String SOS_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "1011003";
	public static final String SOS_ERROR_EXCEPTION = "1011004";
	public static final String SOS_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE = "1011005";
	public static final String SOS_SETTLEMENT_FAIL = "1011006";
	public static final String SOS_SETTLEMENT_SUCCESS_WITHOUT_POSTBAL = "1011007";
	public static final String SOS_LESS_VAL_DAYS = "1011008";
	public static final String SOS_LESS_DAYS_GAP_IN_TWO_TRAN = "1011009";
	public static final String SOS_MAX_BAL_ALLOWED = "1011010";
	public static final String SOS_SUBS_NOT_ACTIVE = "1011011";
	public static final String SOS_SUCCESS = "1011112";
	public static final String SOS_SUCCESS_WITHOUT_POSTBAL = "1011113";
	public static final String SOSDWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE = "1011014";
	public static final String SOSDWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "1011015";
	public static final String SOSDWH_AMB_OR_UP_TXN_FOUND = "1011016";
	public static final String SOSDWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "1011017";
	public static final String SOSDWH_COULD_NOT_UPDATE_MAX_DONE_DATE = "1011018";
	public static final String SOSDWH_ERROR_EXCEPTION = "1011019";
	public static final String P2P_SOS_SERVICE_CLASS_NOT_ALLOWED = "1011020";
	public static final String LMB_LAST_SUCCESS_UNSETTLED = "1011021";
	public static final String P2P_SOS_LESS_AON = "1011022";
	public static final String SOS_SUBS_CORE_BAL_NEGATIVE = "1011023";
	public static final String SOS_LMB_FLAG_ZERO = "1011024";
	public static final String SOS_REQ_AMT_MORE = "1011025";
	public static final String SOS_CORE_BAL_LESS = "1011026";
	public static final String USSD_CELLID_BLANK__ERROR = "1011027";// CHANGE
																	// THE ERROR
																	// CODE
																	// BEFORE
																	// PROCEEDING
	public static final String USSD_SWITCHID_BLANK__ERROR = "1011028";
	public static final String SOS_REQ_ALREADY_PROCESSED = "1011029";
	public static final String FORCEFUL_LMB_SUCCESS_SETTLEMENT_FOR_OPERATOR_CALL = "1011130";
	public static final String FORCEFUL_LMB_FAIL_SETTLEMENT_FOR_OPERATOR_CALL = "1011031";
	public static final String NO_SUCCESS_LMB_FOUND_FOR_FOURCE_SETTLEMENT = "1011032";
	public static final String MULTIPLE_RECORD_EXIST = "1011033";
	public static final String OTHER_UNSETTLED_TRANSACTION = "1011034";
	public static final String INVALID_DATA = "1011035";
	public static final String LMB_SETTLE_STATUS_SAME = "1011036";
	public static final String FORCE_LMB_FAIL_NOT_LATEST = "1011037";
	public static final String FORCEFUL_SUCC_LMB_SETTLEMENT = "1011138";
	public static final String USER_PHONES_NOT_UPDATED = "1011039";
	public static final String LANGUAGE_CODE_GENERAL_EXCEPTION = "1011040";
	public static final String INTERFACE_HANDLER_EXCEPTION = "1011041";
	public static final String INTERFACE_REQ_NOT_SEND = "1011042";
	public static final String ERROR_BAD_REQUEST = "1011043";
	public static final String AMBIGOUS_RESPONSE = "1011044";
	public static final String ERROR_RESPONSE = "1011045";
	public static final String INTERFACE_MSISDN_NOT_FOUND = "1011046";
	public static final String INTERFACE_MSISDN_BARRED = "1011047";
	public static final String INVALID_MSISDN = "1011048";
	public static final String DUPLICATE_MSISDN = "1011049";
	public static final String LMB_BULKUPL_SUCCESS = "1011150";
	public static final String DUPLICATE_MSISDN_IN_DB = "1011051";
	public static final String LMB_EXPIREDVAL_RELOAD = "1011052";
	public static final String SOS_SETTLEMENT_SUCCESS = "1011153";
	public static final String INVALID_DATE = "1011054";
	public static final String LMB_BLANK_MSISDN = "1011055";
	public static final String LMB_AMOUNT_NOT_NUMERIC = "1011056";
	public static final String LMB_TYPE_BALNK = "1011057";
	public static final String INVALID_LMB_FORCE_STATUS = "1011058";
	public static final String LMB_NETWORK_PREFIX_NOT_FOUND = "1011059";
	public static final String LMB_FORCE_MSISDN_NOTIN_NETWORK = "1011060";
	public static final String LMB_BULKUPL_INVALID_RECORD = "1011061";

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
	public static final String P2PGMB_ERROR_USER_BARRED = "2010231";

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
	public static final String EXTSYS_REQ_USER_MSISDN_BLANK = "1004045";
	public static final String EXTSYS_REQ_USER_MSISDN_NOT_FOUND = "1004046";
	public static final String EXTSYS_REQ_NEW_MSISDN_ALREADY_EXIST = "1004047";
	public static final String EXTSYS_REQ_NEW_WEBLOGINID_ALREADY_EXIST = "1004048";
	public static final String EXTSYS_REQ_USR_MODIFICATION_FAILED = "1004049";
	public static final String EXTSYS_REQ_ACTION_BLANK = "1004050";
	public static final String EXTSYS_REQ_ACTION_LENGTH_EXCEEDS = "1004051";
	public static final String EXTSYS_REQ_ACTION_INVALID_VALUE = "1004052";
	public static final String EXTSYS_REQ_USR_ALREADY_SUSPENDED = "1004053";
	public static final String EXTSYS_REQ_USER_NOT_EXIST = "1004054";
	public static final String EXTSYS_REQ_USR_ALREADY_ACTIVE = "1004055";
	public static final String EXTSYS_REQ_USR_SUS_RES_FAILED = "1004056";
	public static final String EXTSYS_REQ_PARENT_CAT_NOT_ALLOWED = "1004057";
	public static final String EXTSYS_REQ_TRF_RULE_NOT_ALLOWED = "1004058";

	// Diwakar
	public static final String EXTSYS_REQ_INVALID_TYPE_VALUE = "1004059";
	public static final String EXTSYS_REQ_EMPCODE_LENGTH_EXCEEDS = "1004060";
	public static final String EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS = "1004061";
	public static final String EXTSYS_REQ_ROLECODE_BLANK_OR_LENGTH_EXCEEDS = "1004062";
	public static final String EXTSYS_REQ_PIN_LENGTH_EXCEEDS = "1004063";
	public static final String EXTSYS_REQ_ROLE_ACTION_INVALID_VALUE = "1004064"; // 11-MAR-20014
	// Ended
	// New Entries for Credit Debit Request through external system
	public static final String EXTSYS_CRDR_REQ_RET_PIN_BLANK = "1009001";
	public static final String EXTSYS_CRDR_REQ_RET_PIN_INVALID = "1009002";

	// Added by Amit Raheja for reverse transactions
	public static final String C2C_REVERSAL_TRX_RECIEVER = "1012102";
	public static final String C2C_REVERSAL_TRX_SENDER = "1012101";
	public static final String O2C_REVERSAL_TRX_CH_USER = "1013101";

	// Added for Voms
	public static final String ERROR_VOMS_GEN = "1016101";
	public static final String ERROR_VOMS_NOTFOUND_COUNTER = "1016102";
	public static final String ERROR_VOMS_INS = "1016103";
	public static final String VOMS_DATA = "1016104";
	public static final String ERROR_VOMS_PINEMPTY = "1016105";
	public static final String ERROR_VOMS_PINNOTFOUNDINDB = "1016106";
	public static final String ERROR_VOMS_VOUCHEREXPIRED = "1016107";
	public static final String ERROR_VOMS_STATUSINVALIDCONSUMP = "1016108";
	public static final String ERROR_VOMS_STATUSINVALIDENQ = "1016109";
	public static final String ERROR_VOMS_STATUSINVALIDFORCONSUMPTION = "1016110";
	public static final String ERROR_VOMS_INVALID_PIN_LENGTH = "1016128";

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

	// CP User Registeration

	public static final String CHN_USR_REG_INVALID_CATEGORY = "1015001";
	public static final String CHN_USR_REG_INVALID_PARENT_MSISDN = "1015002";
	public static final String CHN_USR_REG_PARENT_DETAILS_NOT_FOUND = "1015003";
	public static final String CHN_USR_REG_SENDER_SERVICE_NOT_ALLOWED = "1015004";
	public static final String CHN_USR_REG_NOT_ALLOWED_CATEGORY = "1015005";
	public static final String CHN_USR_REG_DEF_TC_PRF_NOT_FOUND = "1015006";
	public static final String CHN_USR_REG_DEF_COMM_PRF_NOT_FOUND = "1015007";
	public static final String CHN_USR_REG_DEF_GRADE_NOT_FOUND = "1015008";
	public static final String CHN_USR_REG_SENDER_GEOGRAPHY_NOT_FOUND = "1015009";
	public static final String CHN_USR_REG_USER_GEOGRAPHY_NOT_FOUND = "1015010";
	public static final String CHN_USR_REG_DEF_USER_GEOGRAPHY_NOT_FOUND = "1015011";
	public static final String CHN_USR_REG_DEF_GROUP_ROLES_NOT_FOUND = "1015012";
	public static final String CHN_USR_REG_FAILED = "1015013";
	public static final String CHN_USR_REG_SUCESS = "1015114";
	public static final String CHN_USR_REG_SUCESS_P = "1015115";
	public static final String CHN_USR_REG_SUCESS_R = "1015116";

	// Added by Vikas Jauhari for Process CP User Suspension
	public static final String CPUSER_SUSPENSION_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "1015017";
	public static final String CPUSER_SUSPENSION_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "1015018";
	public static final String CPUSER_SUSPENSION_ERROR_EXCEPTION = "1015019";
	public static final String CPUSER_SUSPENSION_SUCCESSFULL_MESSAGE = "1015120";

	// added by harsh on 10Aug12
	public static final String DELETE_BUDDYLIST_SUCCESS = "2018101";
	public static final String BUDDYLIST_NOT_FOUND = "2018002";
	public static final String DELETE_BUDDYLIST_FAILED = "2018003";
	public static final String MULT_DEL_BUDDY_UNDERPROCESS = "2018004";

	// For VAS and PVAS added by Hitesh.

	public static final String VAS_PROMOVAS_REQ_SELECTOR_MISSING = "1024001";
	public static final String VAS_PROMOVAS_MAPPING_NOT_EXIST = "1024002";
	public static final String PVAS_RECEIVER_UNDERPROCESS = "1024003";
	public static final String PVAS_SENDER_UNDERPROCESS = "1024004";
	public static final String PVAS_SENDER_UNDERPROCESS_B4VAL = "1024005";
	public static final String PVAS_RECEIVER_AMBIGOUS_KEY = "1024006";
	public static final String PVAS_RECEIVER_FAIL_KEY = "1024007";
	public static final String VAST_RECEIVER_UNDERPROCESS = "1024008";
	public static final String VAST_SENDER_UNDERPROCESS = "1024009";
	public static final String VAST_SENDER_UNDERPROCESS_B4VAL = "1024010";
	public static final String VAST_RECEIVER_AMBIGOUS_KEY = "1024011";
	public static final String VAST_RECEIVER_FAIL_KEY = "1024012";

	public static final String PVAS_SENDER_SUCCESS = "1024113";
	public static final String VAST_RECEIVER_GET_NUMBER_BACK_SUCCESS = "1024114";
	public static final String VAST_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS = "1024115";
	public static final String PVAS_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "1024116";
	public static final String PVAS_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "1024117";
	public static final String PVAS_RECEIVER_SUCCESS_WITH_BONUS = "1024118";
	public static final String PVAS_RECEIVER_SUCCESS = "1024119";
	public static final String VAST_SENDER_SUCCESS = "1024120";
	public static final String VAST_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "1024121";
	public static final String VAST_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "1024122";
	public static final String VAST_RECEIVER_SUCCESS_WITH_BONUS = "1024123";
	public static final String VAST_RECEIVER_SUCCESS = "1024124";
	public static final String PVAS_RECEIVER_GET_NUMBER_BACK_SUCCESS = "1024125";
	public static final String PVAS_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS = "1024126";
	// for c2s table merging
	public static final String DATE_RANGE_ERROR = "1025001";
	public static final String SID_INVALID_PREFIX = "1002022";

	public static final String EXT_XML_ERROR_CU_DETAILS_NOT_FOUND4LOGINID = "1026001";
	public static final String EXT_TRF_RULE_TYPE_NOT_FOUND4LOGINID = "1026002";
	public static final String EXT_XML_ERROR_MISSING_MANDATORY_VALUE = "1026003";
	public static final String EXT_TRF_RULE_TYPE_NOT_FOUND = "1026004";
	public static final String EXT_XML_ERROR_MSISDN_LOGINID_EXTCODE_ALL_MISSING = "1026005";
	public static final String EXT_GRPH_ERROR_MISSING_MANDATORY_FIELDS = "1020001";
	public static final String EXT_GRPH_ERROR_MISSING_PARENT_DETAILS = "1020002";
	public static final String EXT_GRPH_INVALID_MESSAGE_FORMAT = "1020003";
	public static final String EXT_GRPH_INVALID_CATEGORY = "1020003";
	public static final String EXT_GRPH_INVALID_GEOGRAPHY = "1020005";
	public static final String EXT_GRPH_DETAILS_MISMATCH = "1020006";
	public static final String EXT_GRPH_INVALID_PARENT = "1020007";
	public static final String EXT_GRPH_HIERARCHY_ERROR = "1020008";
	public static final String EXT_GRPH_EXCEPTION = "1020009";
	public static final String EXT_USRADD_ERROR_MISSING_MANDATORY_FIELDS = "1021001";
	public static final String EXT_USRADD_ERROR_MISSING_USER_DETAILS = "1021002";
	public static final String EXT_USRADD_INVALID_MESSAGE_FORMAT = "1021003";
	public static final String EXT_USERADD_OPTLOGIN_NOT_OPERATOR = "1021004";
	public static final String EXT_USRADD_INVALID_CATEGORY = "1021005";
	public static final String EXT_USRADD_LOGIN_EXISTS = "1021006";
	public static final String EXT_USRADD_EXTCODE_EXISTS = "1021007";
	public static final String EXT_USRADD_MSISDN_EXISTS = "1021008";
	public static final String EXT_USRADD_OWNER_MISSING = "1021009";
	public static final String EXT_USRADD_PARENT_MISSING = "1021010";
	public static final String EXT_USRADD_OWNER_NOT_EXIST = "1021011";
	public static final String EXT_USRADD_PARENT_NOT_EXIST = "1021012";
	public static final String EXT_USRADD_TOP_LEVEL_USER = "1021013";
	public static final String EXT_USRADD_INVALID_LOGINID = "1021014";
	public static final String EXT_USRADD_INVALID_MSISDN = "1021015";
	public static final String EXT_USRADD_PROFILE_ENTRIES_MISSING = "1021016";
	public static final String EXT_USRADD_INVALID_RSA = "1021017";
	public static final String EXT_USRADD_SSN_NOT_ALLOWED = "1021018";
	public static final String EXT_USRADD_RSA_NOT_ALLOWED = "1021019";
	public static final String EXT_USRADD_ERROR_WHILE_INSERTION = "1021020";
	public static final String EXT_USRADD_LOGIN_NOT_ALLOWED = "1021021";
	public static final String EXT_USRADD_NOT_IN_SAME_NETWORK = "1021022";
	public static final String EXT_USRADD_USERNAME_INCORRECT = "1021023";
	public static final String EXT_USRADD_INVALID_GEOGRAPHY = "1021024";
	public static final String EXT_USRADD_PARENT_HIERARCY_INVALID = "1021025";
	public static final String EXT_USRADD_OWNER_HIERARCY_INVALID = "1021026";
	public static final String EXT_USRADD_PARENT_GEOGRAPHY_INVALID = "1021027";
	public static final String EXT_USRADD_OWNER_GEOGRAPHY_INVALID = "1021028";
	public static final String EXT_USRADD_SSN_REQUIRD = "1021029";
	public static final String EXT_USRADD_EXCEPTION = "1021030";
	public static final String EXT_USRADD_USER_MSG = "1021131";
	public static final String EXT_USRADD_SENDER_MSG = "1021132";
	public static final String EXT_USRADD_PARENT_MSG = "1021133";
	public static final String EXT_USRADD_USER_TOPLVL_MSG = "1021134";
	public static final String EXT_USRADD_SENDER_TOPLVL_MSG = "1021135";
	public static final String EXT_USRADD_INVALID_RULETYPE = "1021036";

	public static final String PIN_NOT_FOUND = "1027001";
	public static final String XML_PASSWORD_NOT_FOUND = "1027002";
	public static final String XML_ERROR_INVALIDREQUESTFORMAT = "1027003";
	public static final String USER_NOT_EXIST = "1027004";
	public static final String INVALID_PIN = "1027005";

	public static final String SIM_ACTIVATION_FAIL_S = "2028001";
	public static final String C2S_SIMACT_SENDER_SUCCESS = "2028102";
	public static final String C2S_SIMACT_SENDER_UNDERPROCESS = "2028103";
	public static final String C2S_SIMACT_SENDER_UNDERPROCESS_B4VAL = "2028104";
	public static final String USSD_LANGUAGE1_BLANK_ERROR = "2028105";
	public static final String USSD_LANGUAGE2_BLANK_ERROR = "2028106";
	public static final String C2S_SIMACT_RECEIVER_SUCCESS = "2028107";

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
	public static final String USER_BARRED_FOR_DELETION = "1037106";
	public static final String USER_UNBARRED_FOR_DELETION = "1037107";
	public static final String USER_BARRED_FOR_DELETION_REQ = "1037108";

	// Added for Subscriber Threshold Enquiry by VikasJ
	public static final String P2P_SUBSCRIBER_NOT_RGISTERED_FOR_ENQUIRY = "2030001";
	public static final String P2P_SUBSCRIBER_THRESHOLD_ENQUIRY_ERROR = "2030002";
	public static final String P2P_SUBSCRIBER_THRESHOLD_NOT_UPDATED = "2030003";
	public static final String P2P_SUBSCRIBER_PROFILEID_NULL = "2030004";
	public static final String SERVICECLASS_NOT_USED_IN_SYSTEM = "2030005";
	public static final String SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM = "2030006";

	// Added for auto_o2c_process
	public static final String AUTO_O2C_TRASFER_SUCCESS = "2029102";
	public static final String AUTO_O2C_TRASFER_FAIL = "2029103";
	public static final String AUTO_O2C_PROCESS_SUCCESS = "2029104";
	public static final String AUTO_O2C_PROCESS_FAIL = "2029105";

	public static final String C2S_SERVICE_TYPE_VALIDATION_ERROR = "1033001";
	public static final String C2S_SQL_ERROR_EXCEPTION = "1033002";
	public static final String C2S_ERROR_IN_IDS_GENERATE = "1033003";
	public static final String C2S_CONN_ERROR_EXCEPTION = "1033004";
	public static final String ERROR_SKEY_GENERATION = "1033005";

	// Added for Loyalty Managment System
	public static final String LOYALTY_PROCESSING_FAILED = "1021043";
	public static final String LOYALTY_PROMONOT_DEFINED = "1021044";
	public static final String LOYALTY_NETWORK_STOCK_NOT_OK = "1021045";
	public static final String LOYALTY_PROCESSING_EXCEPTION = "1021046";
	public static final String LOYALTY_PROMONOT_DEFINED_EXCEPTION = "1021047";
	public static final String LOYALTY_POINT_CALULATION_EXCEPTION = "1021048";
	public static final String DB_CONNECTION_NULL = "1021049";
	public static final String FAILED = "1021050";
	public static final String LOYALTY_REDEMPTION_CALULATION_EXCEPTION = "1021051";
	public static final String LMS_TXN_GENRATIONERROR = "00099";

	public static final String LOYALTY_POINTS_UPLOAD_PROCESS_ALREADY_EXECUTED = "1021052";
	public static final String LOYALTY_POINTS_UPLOAD_FILE_BLANK = "1021053";
	public static final String LOYALTY_POINTS_UPLOAD_FILE_PROCESS_ERROR = "1021054";
	public static final String C2S_ERROR_NOTADDED_IN_QUEUE = "1030001";
	public static final String TXN_STATUS_IN_QUEUE = "1030002";
	public static final String C2S_ERROR_DUPLICATE_REQUEST = "1030003";
	public static final String C2S_ERROR_QUEUE_TIMEOUT = "1030004";
	public static final String LMS_SETID_NOT_FOUND = "1030005";
	// Added for LMS Promotion Msg
	public static final String LMS_PROMOTION_PROCESS_ALREADY_EXECUTED = "1030010";
	// Added For LMS Target Credit
	public static final String LMS_FOR_TARGET_CREDIT_PROCESS_ALREADY_EXECUTED = "1030011";
	public static final String LMS_NO_TARGET_ACTIVE_PROFILE = "1030012";

	public static final String MVD_CHNL_ERROR_DAILY_SUBSCRIBER_OUT_COUNTREACHED = "18919";
	public static final String MVD_CHNL_ERROR_DAILY_SUBSCRIBER_OUT_VALREACHED = "18920";
	public static final String MVD_CHNL_ERROR_WEEKLY_SUBSCRIBER_OUT_COUNTREACHED = "18921";
	public static final String MVD_CHNL_ERROR_WEEKLY_SUBSCRIBER_OUT_VALREACHED = "18922";
	public static final String MVD_CHNL_ERROR_MONTHLY_SUBSCRIBER_OUT_COUNTREACHED = "18923";
	public static final String MVD_CHNL_ERROR_MONTHLY_SUBSCRIBER_OUT_VALREACHED = "18924";

	public static final String CHNL_ERROR_DAILY_SUBSCRIBER_OUT_COUNTREACHED = "6021";
	public static final String CHNL_ERROR_DAILY_SUBSCRIBER_OUT_VALREACHED = "6022";
	public static final String CHNL_ERROR_WEEKLY_SUBSCRIBER_OUT_COUNTREACHED = "6023";
	public static final String CHNL_ERROR_WEEKLY_SUBSCRIBER_OUT_VALREACHED = "6024";
	public static final String CHNL_ERROR_MONTHLY_SUBSCRIBER_OUT_COUNTREACHED = "6025";
	public static final String CHNL_ERROR_MONTHLY_SUBSCRIBER_OUT_VALREACHED = "6026";

	// added by akanksha for ethiopia telecom
	public static final String C2S_EXTGW_BLANK_SELECTOR = "1234";
	// added by harsh to validate usermsisdn field while channel user creation
	// through USSD
	public static final String INVALID_USERMSISDN = "1025002";
	public static final String GMB_PLAIN_SMS_SUCESS_S = "2222021";
	public static final String GMB_PLAIN_SMS_SUCESS_R = "2222022";
	public static final String O2C_DIRECT_TRANSFER_RP_RECEIVER = "9999022";
	public static final String HLPDESK_SERVICE_SUCCESS = "59090";
	public static final String HLPDESK_SERVICE_NUM_NOT_FOUND = "59091";
	public static final String HLPDESK_SERVICE_INVALIDMESSAGEFORMAT = "59092";
	public static final String LOYALTY_PROGRESSIVE_MESSAGE = "1021059";

	// Added for OTP
	public static final String OTP_MESSAGE = "2262";
	public static final String OTP_MESSAGE_FOR_FORGOT_PIN = "2292";

	public static final String CHNL_ERROR_RECR_ACCOUNT_ID_BLANK = "2029106";
	public static final String CHNL_ERROR_RECR_ACCOUNT_ID_NOTINRANGE = "2029107";
	public static final String CHNL_ERROR_RECR_ACCOUNT_ID_LEN_NOTSAME = "2029108";
	public static final String CHNL_ERROR_RECR_ACCOUNT_ID_NOTNUMERIC = "2029109";

	public static final String DC_RECEIVER_FAIL = "217_DC";// 217;
	public static final String DC_RECEIVER_FAIL_KEY = "231_DC";// 231

	public static final String DC_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "237_DC";// 237
	public static final String DC_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "603_DC";// 603
	public static final String DC_RECEIVER_SUCCESS = "207_DC";// 207
	public static final String DC_RECEIVER_SUCCESS_WITH_BONUS = "236_DC";// 236
	public static final String DC_RECEIVER_SUCCESS_ALL_BALANCES = "243_DC";// 243
	public static final String DC_RECEIVER_UNDERPROCESS = "208_DC";// 208
	public static final String DC_RECEIVER_AMBIGOUS_KEY = "230_DC";// 230
	public static final String DC_RECEIVER_UNDERPROCESS_SUCCESS = "204_DC";// 204
	public static final String DC_IMPLICIT_MSG = "242_DC"; // 242

	public static final String DC_SENDER_SUCCESS = "210_DC";
	public static final String DC_SENDER_UNDERPROCESS = "209_DC";
	public static final String DC_SENDER_UNDERPROCESS_B4VAL = "216_DC";
	public static final String DC_SENDER_CREDIT_SUCCESS = "221_DC";
	public static final String DC_SENDER_UNDERPROCESS_SUCCESS = "203_DC";
	public static final String DC_PARENT_SUCCESS = "24226_DC";

	public static final String DTH_RECEIVER_FAIL = "217_DTH";// 217;
	public static final String DTH_RECEIVER_FAIL_KEY = "231_DTH";// 231

	public static final String DTH_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "237_DTH";// 237
	public static final String DTH_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "603_DTH";// 603
	public static final String DTH_RECEIVER_SUCCESS = "207_DTH";// 207
	public static final String DTH_RECEIVER_SUCCESS_WITH_BONUS = "236_DTH";// 236
	public static final String DTH_RECEIVER_SUCCESS_ALL_BALANCES = "243_DTH";// 243
	public static final String DTH_RECEIVER_UNDERPROCESS = "208_DTH";// 208
	public static final String DTH_RECEIVER_AMBIGOUS_KEY = "230_DTH";// 230
	public static final String DTH_RECEIVER_UNDERPROCESS_SUCCESS = "204_DTH";// 204
	public static final String DTH_IMPLICIT_MSG = "242_DTH"; // 242

	public static final String DTH_SENDER_SUCCESS = "210_DTH";
	public static final String DTH_SENDER_UNDERPROCESS = "209_DTH";
	public static final String DTH_SENDER_UNDERPROCESS_B4VAL = "216_DTH";
	public static final String DTH_SENDER_CREDIT_SUCCESS = "221_DTH";
	public static final String DTH_SENDER_UNDERPROCESS_SUCCESS = "203_DTH";
	public static final String DTH_PARENT_SUCCESS = "24226_DTH";

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
	public static final String PIN_IMPLICIT_MSG = "242_PIN"; // 242

	public static final String PIN_SENDER_SUCCESS = "210_PIN";
	public static final String PIN_SENDER_UNDERPROCESS = "209_PIN";
	public static final String PIN_SENDER_UNDERPROCESS_B4VAL = "216_PIN";
	public static final String PIN_SENDER_CREDIT_SUCCESS = "221_PIN";
	public static final String PIN_SENDER_UNDERPROCESS_SUCCESS = "203_PIN";
	public static final String PIN_PARENT_SUCCESS = "24226_PIN";

	// Added for AUTO O2C APPROVAL
	public static final String AUTO_O2C_EXTCODE_UPDATE_SUCCESS = "1114";
	public static final String AUTO_O2C_EXTCODE_UPDATE_INVALIDDATA = "1115";

	public static final String C2S_RECEIVER_SUCCESS_COLLECTIONENQUIRY = "9958_ENQ";
	public static final String C2S_RECEIVER_SUCCESS_LIST_COLLECTIONENQUIRY = "7842";
	public static final String C2S_SENDER_SUCCESS_COLLECTIONENQUIRY = "9959_ENQ";
	public static final String C2S_RECEIVER_UNDERPROCESS_COLLECTIONENQUIRY = "9960_ENQ";
	public static final String C2S_SENDER_UNDERPROCESS_B4VAL_COLLECTIONENQUIRY = "9962_ENQ";
	public static final String C2S_RECEIVER_FAIL_KEY_COLLECTIONENQUIRY = "9964_ENQ";
	public static final String C2S_RECEIVER_FAIL_COLLECTION = "9968_ENQ";
	public static final String C2S_RECEIVER_AMBIGOUS_KEY_COLLECTIONENQUIRY = "9963_ENQ";

	public static final String C2S_RECEIVER_SUCCESS_COLLECTIONBILLPAYMENT = "9958_COLBP";
	public static final String C2S_SENDER_SUCCESS_COLLECTIONBILLPAYMENT = "9959_COLBP";
	public static final String C2S_RECEIVER_UNDERPROCESS_COLLECTIONBILLPAYMENT = "9960_COLBP";
	public static final String C2S_SENDER_UNDERPROCESS_COLLECTIONBILLPAYMENT = "9961_COLBP";
	public static final String C2S_SENDER_UNDERPROCESS_B4VAL_COLLECTIONBILLPAYMENT = "9962_COLBP";
	public static final String C2S_RECEIVER_FAIL_KEY_COLLECTIONBILLPAYMENT = "9964_COLBP";
	public static final String C2S_RECEIVER_AMBIGOUS_KEY_COLLECTIONBILLPAYMENT = "9963_COLBP";

	public static final String C2S_RECEIVER_SUCCESS_REVERSAL = "9958_REV";
	public static final String C2S_SENDER_SUCCESS_REVERSAL = "9959_REV";
	public static final String C2S_RECEIVER_UNDERPROCESS_REVERSAL = "9960_REV";
	public static final String C2S_SENDER_UNDERPROCESS_REVERSAL = "9961_REV";
	public static final String C2S_SENDER_UNDERPROCESS_B4VAL_REVERSAL = "9962_REV";
	public static final String C2S_RECEIVER_FAIL_KEY_REVERSAL = "9964_REV";
	public static final String C2S_RECEIVER_AMBIGOUS_REVERSAL = "9963_REV";

	public static final String C2S_SENDER_CREDIT_SUCCESS_ENQUIRY = "221_ENQ";

	public static final String DC_ERROR_INVALID_AMOUNT = "2014_DC";
	public static final String PIN_ERROR_INVALID_AMOUNT = "2014_PIN";
	public static final String O2C_SEND_MAIL_PROCESS_ERROR = "2029106";

	public static final String CHNL_ERROR_SENDER_OUT_SUSPEND_COLLECTION = "9972_ENQ";
	public static final String CHNL_ERROR_SENDER_OUT_SUSPEND_REVERSAL = "9972_REV";

	public static final String C2S_INVALID_TXN_ID_REVERSAL = "4567";
	public static final String C2S_WRONG_TRANSACTION_REVERSAL_TIMEOUT = "4568";
	public static final String C2S_BILLPAYMNET_INVOICE_NO_REO = "4569";
	public static final String C2S_BILLPAYMNET_TXN_ID_REO = "4570";

	public static final String C2S_INVALID_SENDERMSISDN_REVERSAL = "4571";
	public static final String C2S_INVALID_RECEIVERMSISDN_REVERSAL = "4572";
	public static final String C2S_PREVIOUS_AMBIGUOUS_REVERSAL = "4573";
	public static final String C2S_PREVIOUS_FAIL_REVERSAL = "4574";
	public static final String C2S_REVERSAL_SUCCESSFULL = "4575";
	public static final String C2S_REVERSAL_ALREADY_DONE = "4576";

	public static final String C2S_PARENT_SUCCESS_REVERSAL = "24227_REV";
	public static final String C2S_RECEIVER_FAIL_REVERSAL = "9968_REV";
	public static final String LOYALTY_REDEM_MESSAGE = "1021064";// brajesh

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

	public static final String LMS_MIS_DEPENDENCY = "1030015";
	public static final String LMS_COULD_NOT_UPDATE_MAX_DONE_DATE = "1030014";
	public static final String C2S_REVERSAL_SUCCESS = "210_REV";
	public static final String C2S_SENDER_ENQ_SUCCESS = "210_ENQ";
	// VFE 6 CR
	public static final String C2S_ERROR_MAX_100_INFO1 = "1011000001";
	public static final String C2S_ERROR_MAX_100_INFO2 = "1011000002";
	public static final String C2S_ERROR_MAX_100_INFO3 = "1011000003";
	public static final String C2S_ERROR_MAX_100_INFO4 = "1011000004";
	public static final String C2S_ERROR_MAX_100_INFO5 = "1011000005";
	public static final String C2S_ERROR_MAX_100_INFO6 = "1011000006";
	public static final String C2S_ERROR_MAX_100_INFO7 = "1011000007";
	public static final String C2S_ERROR_MAX_100_INFO8 = "1011000008";
	public static final String C2S_ERROR_MAX_100_INFO9 = "1011000009";
	public static final String C2S_ERROR_MAX_100_INFO10 = "10110000010";
	// Added for PPB ENQ
	public static final String PPBENQ_INVALIDMESSAGEFORMAT = "111111";
	public static final String PPB_ENQ_SUCCESS_S = "111112";
	public static final String PPB_ENQ_SUCCESS_R = "111113";
	public static final String C2S_SENDER_SUCCESS_EVD = "21000";
	public static final String LOW_BALANCE_ALERT_MSG_OTHER = "3509";
	public static final String LOW_BALANCE_ALERT_MSG_PARENT = "3510";
	public static final String C2S_RECEIVER_BILLPAY_NOTFOUND = "4578";
	// Added by Diwakar for SMS to Channel Admin Users
	public static final String SMS_TO_CHANNEL_ADMIN_USERS_HOURLY = "11000";

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

	// #################################Added by Diwakar for User
	// Add/Modify/Suspend/Resume/Role Add/Modify
	public static final String EXTSYS_REQ_RECEIVER_DETAILS_INVALID = "10081";
	public static final String ERROR_ERP_CHNL_USER_INVALID_MSISDN = "4518";
	public static final String ERROR_ERP_CHNL_USER_PARENT_IS_INVALID = "4514";
	public static final String ERROR_ERP_CHNL_USER_MSISDN_PORTED_IN = "4548";
	public static final String ERROR_ERP_CHNL_USER_MSISDN_PORTED_OUT = "4549";
	public static final String ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST = "4550";
	public static final String ERROR_ERP_CHNL_USER_NEW_EXTERNAL_CODE_INVALID = "4551";
	public static final String ERROR_ERP_USER_NEW_EXTERNAL_CODE__SAME_AS_OLD = "4552";
	public static final String ERP_USER_NONE_PARAMETER = "9000001";
	public static final String ERROR_ERP_CHNL_USER_MSISDN_BLANK = "4547";
	public static final String ERROR_ERP_CHNL_USER_INVALID_PASSWORD = "4511";
	public static final String XML_ERROR_USER_MSISDN_NOT_FOUND = "7523";
	public static final String XML_ERROR_USER_NAME_BLANK = "5000003";
	public static final String XML_ERROR_USER_NOT_AUTHORIZED = "5000004";
	public static final String XML_ERROR_ACTION_ID_MANDATORY = "5000005";
	public static final String XML_ERROR_USER_ALREADY_ACTIVE = "5000006";
	public static final String XML_ERROR_USER_NOT_EXIST = "5000007";
	public static final String XML_ERROR_USER_ALREADY_SUSPEND = "5000008";
	public static final String ERROR_ERP_CHNL_USER_INVALID_USERPRIFIX_CODE = "4505";
	public static final String ERROR_ERP_CHNL_USER_INVALID_CATEGORY_CODE = "4506";
	public static final String ERROR_ERP_CHNL_USER_EXTERNAL_CODE_ALREADY_EXIST = "4507";
	public static final String ERROR_ERP_CHNL_USER_EXTERNAL_CODE_IS_MANDATORY = "4508";
	public static final String ERROR_ERP_CHNL_USER_LOGINID_ALREADY_EXIST = "4509";
	public static final String ERROR_ERP_CHILD_USER_EXISTS = "4553";
	public static final String ERROR_ERP_BALANCE_EXISTS = "4554";
	public static final String ERROR_ERP_O2C_TXN_PENDING = "4555";
	public static final String ERROR_ERP_FOC_TXN_PENDING = "4556";
	public static final String ERROR_ERP_RESTRICTED_LIST_EXISTS = "4557";
	public static final String ERP_CHNL_USER_SUS_SUCESS = "9000002";
	public static final String ERP_CHNL_USER_RES_SUCESS = "9000003";
	public static final String ERP_CHNL_USER_DEL_SUCESS = "9000004";
	public static final String ERP_USER_REGISTRATION_FAILED = "4519";
	public static final String OPT_ERROR_NO_SUCH_USER = "13000";

	// #####################User Add
	public static final String USER_ADD_ERROR_MISSING_MANDATORY_FIELDS_VALUE = "10076";
	public static final String EXTSYS_REQ_SENDER_DETAILS_INVALID = "10077";
	public static final String EXTSYS_REQ_SENDER_EMPLOYEE_DETAILS_INVALID = "10031";
	public static final String USER_WEB_ADDED = "10078";
	public static final String USER_SMSPIN_ADDED = "10079";
	public static final String USER_WEB_SMSPIN_ADDED = "10080";
	public static final String USER_WITH_APPROVAL_REQUIRED_ADDED = "100811";

	// ##################### User Modify
	public static final String USER_MODIFY_SUCCESS = "10101";
	// public static final String
	// USER_MODIFY_ERROR_MISSING_MANDATORY_FIELD="10101";
	// public static final String USER_WEB_MODIFIED="10102";
	// public static final String USER_SMSPIN_MODIFIED="10103";
	// public static final String USER_WEB_SMSPIN_MODIFIED="10104";

	// ##################### User Suspend/Resume
	public static final String USER_SUSPEND_SUCCESS = "10200";
	public static final String USER_RESUMED_SUCCESS = "10201";
	public static final String USER_SUSPEND_RESUME_MSISDN_LOGINID_BLANK = "10202";

	// ##################### User Delete
	public static final String USER_DELETED_SUCCESS = "10300";

	// #################MNP Porting
	public static final String MNP_PORT_SUCCESS = "10400";

	// ##################### User Role Add/Delete
	public static final String XML_ROLE_CODE_INVALID = "10500";
	public static final String XML_ROLE_CODE_ADD_SUCC = "10501";
	public static final String XML_ROLE_CODE_ADD_FAIL = "10502";
	public static final String XML_ROLE_CODE_DELETE_SUCC = "10503";
	public static final String XML_ROLE_CODE_DELETE_FAIL = "10504";

	// ######## ICCID-MSISDN MAP
	public static final String EXTSYS_ICCID_MAP_LENGTH_EXCEEDS = "10600";
	public static final String EXTSYS_CONFIRM_ICCID_MAP_LENGTH_EXCEEDS = "10601";
	public static final String EXTSYS_ICCID_CONFIRM_ICCID_MAP_NOT_SAME = "10602";
	public static final String EXTSYS_MSISDN_MAP_LENGTH_EXCEEDS = "10603";
	public static final String EXTSYS_ICCID_MSISDN_MAP_BLANK = "10604";
	public static final String EXTSYS_ICCID_MSISDN_MAP_SUCCESS = "10605";
	public static final String EXTSYS_ICCID_MSISDN_MAP_FAILURE = "10606";
	public static final String XML_ERROR_CONFIRM_ICCID_IS_NULL = "10607";
	// Ended Here By Diwakar
	// Added by Diwakar on 12-FEB-2014 for sending alert for available vouchers
	public static final String VOUCHER_THRESHOLD_ALERT_MSG = "10700";
	public static final String INVALID_USERMSISDN_AS_PER_NETWORK_CODE = "10701"; // 21-02-2014
	public static final String SQL_ERROR_EXCEPTION = "10702";// 21-02-2014
	public static final String GENERAL_PROCESSING_ERROR_EXCEPTION = "10703";// 21-02-2014
	// Ended Here

	// Added By Diwakar on 28-FEB-2014 for common tag value validation
	public static final String EXTSYS_BLANK = "11100";
	public static final String EXTSYS_LENGTH_RANGE_INVALID = "11101";
	public static final String EXTSYS_NOT_NUMERIC = "11102";
	public static final String EXTSYS_NETWORK_CODE_INVALID = "11103";
	public static final String EXTSYS_LENGTH_INVALID = "11104";
	public static final String EXTSYS_NOT_ALFA_NUMERIC = "11105";
	public static final String EXTSYS_NOT_ALFA_NUMERIC_SPECIAL = "11106";
	public static final String EXTSYS_NOT_ALFABETIC = "11107";
	public static final String EXTSYS_NOT_ALFA_NUMERIC_SPECIAL_ATLEAST = "11108"; // 11-MAR-2014
	public static final String EXTSYS_NOT_NUMERIC_OR_DECIMAL = "11109"; // 31-MAR-2014

	public static final String EXTSYS_EXTCODE_WRONG = "11110";
	// Ended Here
	// 11-MAR-2014
	public static final String SMS_TO_AREA_ADMIN_USERS_HOURLY = "11005";
	public static final String PROMOTION_MESSAGE_NOTIFICATION = "1040111";

	// Added by Vikas Singh on 24-04-2014
	public static final String CARD_MODIFY_FAILED = "2041001";
	public static final String ERROR_NICK_NAME_MANDATORY = "2041002";
	public static final String ERROR_NICK_NAME_EXCEED_LENGTH = "2041003";
	public static final String ERROR_NICK_NAME_SP_CHARACTERS = "2041004";
	public static final String CARD_DELETE_FAILED = "2041005";
	public static final String CARD_MODIFY_SUCCESS = "2041110";
	public static final String ERROR_INVALID_IMEI = "2041006";
	public static final String INVALID_NEW_NICK = "2041007";
	public static final String CARD_DELETE_SUCCESS = "2041111";
	public static final String INVALID_OLD_NICK = "2041008";
	public static final String ERROR_NEW_NICK_SAME_AS_OLD = "2041009";
	public static final String INVALID_CREDITCARD_NUMBER = "2041012";
	public static final String P2P_ERROR_INVALID_RECEIVER_MSISDN = "2041013";
	// Ended here
	public static final String INVALID_EXPIRY_DATE_BEFORE = "1234001";
	public static final String ASSOCIATED_PROFILE_NOT_ACTIVE = "1040006";
	public static final String PARENT_USER_IS_NOT_ACTIVE = "1040007";
	public static final String NOT_ENOUGH_POINTS_TO_REDEMPTION = "1040008";
	public static final String LOYALTY_RECON_MESSAGE = "1021060";
	// added by sonali for self topup
	public static final String INVALID_EMAILID = "2041014";
	// Ended
	// added by sonali for self topup
	public static final String INVALID_IMEI = "2041015";
	public static final String CREDITCARD_LIST_NOTFOUND = "2041016";
	// public static final String CREDITCARD_LIST_SUCCESS = "2041017"; //changed
	// by Vikas singh
	public static final String CREDITCARD_LIST_SUCCESS = "2041117";
	public static final String CREDITCARD_LIST_ERROR = "2041018";
	public static final String P2P_ERROR_CREDITCARDLIST_INVALIDMESSAGEFORMAT = "2041019";
	public static final String P2P_ERROR_CREDITCARDLIST_INVALID_IMEI = "2041020";
	public static final String P2P_SELFTOPUP_REGISTERATION_PREPAID_SUCCESS_WITHOUT_PIN = "2041021";
	// public static final String INVALID_CREDITCARD_NUMBER="2041022";
	// ended
	public static final String NETWORK_PREFIX_SERVICE_MAPPING_NOT_FOUND = "5961";
	// added by Vikas Singh for Auto topup
	public static final String AUTO_TOPUP_REG_FAILED = "2041023";
	public static final String AUTO_TOPUP_DATE_DIFF_FAILED = "2041024";
	public static final String AUTO_TOPUP_WEEK_DAY_ERROR = "2041025";
	public static final String AUTO_TOPUP_MONTH_DAY_ERROR = "2041026";
	public static final String AUTO_TOPUP_DATE_FORMAT_ERROR = "2041027";
	public static final String AUTO_TOPUP_INVALID_AMOUNT = "2041028";
	// end here

	// // ADDED BY GAURAV FOR SCHEDULE SELF TOP UP
	public static final String NO_USER_FOUND_FOR_SCHEDULE_TOPUP = "1091001";
	public static final String SCHEDULE_TOPUP_PROCESS = "1091002";
	public static final String NO_INSTANCE_FOR_REQUESTED_NETWORK = "1091003";
	// public static final String INVALID_EXPIRY_DATE_BEFORE="1091004";
	// /schedule top up process
	public static final String USER_MAX_RETRY_COUNT_REACHED = "1091005";

	// //ADDED BY VIKAS SINGH
	public static final String AUTO_TOPUP_NONNUMERIC_DAY = "2041029";
	public static final String AUTO_TOPUP_REG_SUCCESSFUL = "2041130";
	public static final String MAX_AUTO_TOPUP_AMT_RCHD = "2041031";
	// ENDED HERE
	public static final String EXTSYS_CHANGE_PIN_SUCCESS = "10400090";
	public static final String EXTSYS_CHANGE_MSISDN_SUCCESS = "1040010"; // for
																			// MSISDN
																			// Change
																			// Functionality
	public static final String EXTSYS_MSISDN_RECHARGE_STATUS_SUCCESS = "1040011"; // for
																					// ETU
																					// Change
																					// Recharge
																					// Status
																					// Functionality

	public static final String INVALID_HOLDER_NAME = "2041050";
	public static final String PIN_REPETATION_INVALID = "4084";
	public static final String O2C_OPT_CHNL_TRANSFER_SMS1 = "3702"; // added by
																	// rajeev.kumar2
	public static final String O2C_OPT_CHNL_TRANSFER_SMS2 = "3703";
	public static final String O2C_OPT_CHNL_TRANSFER_SMS3 = "3704";
	public static final String O2C_OPT_CHNL_TRANSFER_SMS_BALSUBKEY = "3705";
	public static final String O2C_OPT_CHNL_TRANSFER_CANCEL_TXNSUBKEY = "3706";
	public static final String O2C_OPT_CHNL_TRANSFER_CANCEL_BALSUBKEY = "3707";

	public static final String THLD_PRTP_PRCSS_TIME_REACH = "1030006";

	// Added by Vikas Singh for the PrePaid TopUP Reversal
	public static final String CHNL_ERROR_SENDER_OUT_SUSPEND_PRE_REVERSAL = "1041040";
	public static final String C2S_RECEIVER_SUCCESS_PRE_REVERSAL = "1041141";
	public static final String C2S_SENDER_SUCCESS_PRE_REVERSAL = "1041142";
	public static final String C2S_RECEIVER_UNDERPROCESS_PRE_REVERSAL = "1041143";
	public static final String C2S_SENDER_UNDERPROCESS_PRE_REVERSAL = "1041144";
	public static final String C2S_SENDER_UNDERPROCESS_B4VAL_PRE_REVERSAL = "1041045";
	public static final String C2S_RECEIVER_FAIL_KEY_PRE_REVERSAL = "1041046";
	public static final String C2S_RECEIVER_AMBIGOUS_PRE_REVERSAL = "1041047";
	public static final String C2S_ERROR_EXCEPTION_INSUF_BALANCE = "1041048";
	public static final String C2S_ERROR_EXCEPTION_BALANCE_PARAM_NOT_PRESENT = "1041049";
	public static final String C2S_ERROR_EXCEPTION_NO_RESP_FROM_IN = "1041050";
	public static final String C2S_PRE_REVERSAL_ALREADY_DONE = "1041051";
	public static final String C2S_INVALID_PRE_TXN_ID_REVERSAL = "1041052";
	public static final String C2S_PRE_REVERSAL_FAILED = "1044574";
	public static final String C2S_INVALID_PRE_SENDERMSISDN_REVERSAL = "1044571";
	public static final String C2S_INVALID_PRE_RECEIVERMSISDN_REVERSAL = "1044572";
	public static final String C2S_REVERSAL_PRE_SUCCESSFULL = "1044575";
	public static final String C2S_WRONG_TRANSACTION_PRE_REVERSAL_TIMEOUT = "1044568";

	public static final String C2S_RECEIVER_AMBIGOUS_PRE_REVERSAL_MESSAGE = "3000438";

	// Added by Akanksha for Reversal reconciliation
	public static final String RECON_C2S_ADJUSTMENT_SUCCESS_REVERSAL_MSG = "7705";
	public static final String RECON_C2S_ADJUSTMENT_FAIL_REVERSAL_MSG = "7706";

	public static final String EXTSYS_REQ_MOBILENUMBER_LENGTH_EXCEEDS = "1004065";
	public static final String EXTSYS_REQ_MOBILENUMBER_NON_NUMERIC = "1004066";
	public static final String EXTSYS_REQ_DIVISION_LENGTH_EXCEEDS = "1004067";
	public static final String EXTSYS_REQ_DEPARTMENT_LENGTH_EXCEEDS = "1004068";
	public static final String EXTSYS_REQ_EMAILID_NOTFOUND = "1004069";
	public static final String EXTSYS_REQ_DIVISION_NOTNULL = "1004070";
	public static final String EXTSYS_REQ_DEPARTMENT_NOTNULL = "1004071";
	public static final String EXTSYS_REQ_DIVISION_INVALID = "1004072";
	public static final String EXTSYS_REQ_DEPARTMENT_INVALID = "1004073";
	public static final String EXTSYS_REQ_CATAGORY_NOT_ALOOWED = "1004074";
	public static final String ERROR_ERP_EXT_OPT_USER_LOGINID_BLANK = "1004075";

	public static final String EXTSYS_REQ_USR_DEFAULT_GEOGRAPY_DOMAIN_NOT_FOUND = "1004076";
	public static final String EXTSYS_REQ_USR_DEFAULT_PRODUCT_NOT_FOUND = "1004077";
	public static final String EXTSYS_REQ_USR_DEFAULT_DOMAIN_NOT_FOUND = "1004078";
	public static final String C2S_USER_REG_MAPPGW_SUCC = "1004079";
	/* Added for Geography */
	public static final String EXTSYS_REQ_USR_GEOGRAPHY_NOT_BELONG_TO_PARENT = "1040080";
	public static final String EXTSYS_REQ_USR_GEOGRAPHY = "1040081";
	// For Roam Recharge
	public static final String C2S_ERROR_NOTDEFINED_ROAM_INRFC = "24001";
	public static final String C2S_ERROR_BLANK_VOUCHERCODE = "24002";

	// For GetMyNumber Request
	public static final String GET_MYNUMBER_SUCCESS = "1042101";
	public static final String GET_MYNUMBER_FAILED = "1042002";

	public static final String CHNL_ERROR_DAILY_SUBSCRIBER_IN_VALREACHED = "1046022";
	public static final String CHNL_ERROR_WEEKLY_SUBSCRIBER_IN_VALREACHED = "1046024";
	public static final String CHNL_ERROR_MONTHLY_SUBSCRIBER_IN_VALREACHED = "1046026";
	// changes for Meditel
	public static final String CHNL_ERROR_SENDER_NOTALLOWED = "705151";
	public static final String CHNL_ERROR_RECEIVER_NOTALLOWED = "705152";

	public static final String ERROR_USERSTATUS_NOTCONFIGURED = "705153";

	// added by Brajesh for Loyalty Points Enquiry and redemption By channel
	// user
	public static final String NO_LOYALTY_POINTS_FOR_USER = "1021055";
	public static final String TOTAL_LOYALTY_POINTS_FOR_USER = "1021056";
	public static final String TOTAL_LOYALTY_REDEMPTION_AND_AMOUNT = "1021057";
	public static final String REDEMP_POINTS_NULL = "1021058";
	public static final String INVALID_REDEMP_LOYALTY_POINTS = "1021079";
	public static final String NOT_ENOUGH_LOYALTY_POINTS = "1021061";
	public static final String INVALID_AMOUNT = "1021062";
	public static final String AMOUNT_ZERO = "1021063";
	public static final String REDEMPTION_STATUS_FAIL = "1021066";
	public static final String LMS_PROFILE_NOT_ACTIVE = "1024444";
	public static final String PARENT_SUSPENDED = "1024445";
	public static final String INSUFFIECIENT_NETWORK_STOCK = "1024446";
	public static final String PARENT_NOT_ENOUGH_BALANCE = "1024447";
	public static final String SERVICE_NOT_ASSOCIATED = "1021067";
	public static final String ZERO_LOYALTY_POINTS_FOR_USER = "1021068";
	public static final String LMS_INVALID_DATE = "1021069";
	public static final String LMS_PROFLIE_NOT_ASSOCIATED = "1021080";

	// public static final String INVALID_DATE1 = "1021080";
	// For Channel User Info Request
	public static final String CHNL_USR_INFO_SUCCESS = "1043101";
	public static final String CHNL_USR_INFO_FAILED = "1043002";
	// By Zeeshan for Voucher Consumption

	public static final String ERROR_INSUFF_LENGTH_VOUCHERCODE = "24003";
	public static final String VOUCHER_CONSUMPTION_SUCCESS = "24004";
	public static final String VOUCHER_CONSUMPTION_FAILED = "24005";
	/** START: Birendra: 31JAN2015 */
	public static final String ERROR_BALANCE_TYPE_BLANK = "1702";
	public static final String ERROR_BALANCE_TYPE_INVALID = "1703";

	public static final String NO_WALLET_EXIST_NETID_PRDTYPE = "1704";
	public static final String NO_PDAWALLET_EXIST = "1705";
	public static final String NO_WALLET_EXIST_NETID_PRDID = "1706";
	/** START: Birendra: 31JAN2015 */
	// added for voms
	public static final String ERROR_VOMS_TPYE_NULL = "1024101";
	public static final String ERROR_VOMS_TPYE_INVALID = "1024102";
	public static final String ERROR_VOMS_INVALID_REQUEST_FORMAT = "1024103";
	public static final String ERROR_VOMS_SUBID_INVALID = "1024104";
	public static final String ERROR_VOMS_NO_ACTIVE_PRODUCT_FOUND = "1024105";

	public static final String ERROR_VOMS_PIN_SERIALNO_EMPTY = "1016110";
	public static final String ERROR_VOMS_PIN_SERIAL_INVALID = "1016111";
	public static final String ERROR_VOMS_STATUSNOTCUORUP = "1016112";
	public static final String ERROR_VOMS_RETRIEVAL_ROLLBACK_ERROR = "1016113";
	public static final String VMS_RECEIVER_SUCCESS = "202_VMS";
	public static final String VMS_RECEIVER_SUCCESS_WITH_BONUS = "239_VMS";
	public static final String VMS_SENDER_SUCCESS = "201_VMS";

	public static final String P2P_PIN_REQUIRED_ERROR = "1016018";

	public static final String USER_WEB_ADD = "12015";
	public static final String USER_SMSPIN_ADD = "12016";
	public static final String USER_WEB_SMSPIN_ADD = "12018";
	public static final String USER_ACTIVATE = "12019";

	public static final String INVALID_EMAIL_MAPP = "10007";
	public static final String MAPP_USER_ALREADY_REGISTERED = "10008";
	public static final String MAPP_USER_LOGIN = "10009";

	public static final String USER_OTP = "120001";
	public static final String USER_OTP_SMS = "2241342";

	// added for Opt-IN/Opt-Out Feature
	public static final String LMS_PROFILE_ALREADY_OPT_IN = "1016114";
	public static final String LMS_PROFILE_OPT_IN_SUCCESS = "1016129";
	public static final String LMS_PROFILE_OPT_OUT_FAILURE = "1016116";
	public static final String LMS_PROFILE_OPT_OUT_SUCCESS = "1016117";
	public static final String INVALID_MESSAGE_LENGTH = "1016118";
	public static final String LMS_PROFILE_OPT_IN_OUT_NA = "1016119";
	public static final String LMS_PROFILE_OPT_IN_OUT_REQTIME_INVALID = "1016120";
	public static final String NO_LMS_PROFILE_ASSOCIATED = "1016121";
	public static final String OPTINOUT_PROMOTION_MESSAGE_NOTIFICATION = "1016122";
	// LMS Point Adjustments
	public static final String LMS_POINT_DEBIT_LESS_ACCUMULATED = "1016123";
	public static final String UPDATED_ERROR_BONUS_TABLE = "1016124";
	public static final String LMS_POINT_DEBIT_NOTOFICATION = "1016125";
	public static final String LMS_POINT_CREDIT_NOTOFICATION = "1016126";

	public static final String WRC_SENDER_SUCCESS = "1028101";
	public static final String ADV_SENDER_SUCCESS = "1028102";
	public static final String CAUT_SENDER_SUCCESS = "1028103";

	// for mobile app
	public static final String MAPP_USER_IS_NOT_VALIDATED_APP = "10006";

	public static final String LOYALTY_REDEM_MESSAGE_ITEMS = "1021065";

	public static final String MAPP_SYSTEM_LANGUAGE = "10009";
	public static final String MAPP_PRODUCT_GATEWAY_SERVICES = "10010";
	public static final String MAPP_PRODUCT_GATEWAY_SERVICES2 = "10012";
	public static final String MAPP_PRODUCT_GATEWAY_SERVICES_FAILED = "10011";
	public static final String MAPP_BASE_COMMISSION = "10013";
	public static final String MAPP_BASE_COMMISSION_FAILED = "10014";
	public static final String MAPP_ADDITIONAL_COMMISSION = "10015";
	public static final String MAPP_ADDITIONAL_COMMISSION_FAILED = "10016";
	public static final String MAPP_CHANNEL_USER_NOT_FOUND = "10017";
	public static final String MAPP_BASE_COMMISSION_OUT_OF_RANGE = "10019";

	// Self TPIN Reset

	public static final String PIN_RESET_INVALIDMESSAGEFORMAT = "1031067";
	public static final String SECURITY_QUESTION = "1031068";
	public static final String PIN_RESET_OTP_SMS = "1031069";
	public static final String PIN_RESET_SUCCESSFUL = "1031070";
	public static final String SECURITY_ANSWER_INCORRECT = "1031071";
	public static final String OTP_INCORRECT = "1031072";
	public static final String OTP_EXPIRED = "1031073";
	public static final String PIN_CONFIRMPIN_DIFFERENT = "1031074";
	public static final String CANNOT_BE_PROCESSED = "1031075";
	public static final String PIN_NOT_VALID = "1031076";
	public static final String SECURITY_QUESTION_NULL = "1031077";
	public static final String DATA_UPDATION_SUCCESSFUL = "1031078";
	public static final String NO_DATA = "1031079";
	public static final String INVALID_DATE_FORMAT = "1031080";
	public static final String SHORT_NAME_LENGTH = "1031081";
	public static final String CONTACT_PERSON_LENGTH = "1031082";
	public static final String SUBSCRIBER_CODE_LENGTH = "1031083";
	public static final String MANDATORY_EMPTY = "1031084";

	// Transaction reversal
	public static final String FOC_REVERSAL_TRX_CH_USER = "1013102";
	public static final String TXN_REVERSAL_SUB_KEY = "1013103";
	public static final String DELETE_SID_SUCCESS = "1002023";

	// geo - fencing entries
	public static final String TRANS_BLOCKED = "232323";
	public static final String SEND_ALERT = "2323232";
	public static final String SEND_ALERT_USER = "2323233";

	// Handling of LMS Detailed report
	public static final String LMS_C2S_SUMMARY_COULD_NOT_UPDATE_MAX_DONE_DATE = "1016222";
	public static final String LMS_C2S_SUMMARY_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "1016223";
	public static final String LMS_C2S_SUMMARY_ERROR_EXCEPTION = "1016224";
	public static final String LMS_C2S_SUMMARY_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "1016225";

	// c2s reversal flag
	public static final String REVERSAL_NOT_ALLOWED_CARDGROUP = "1031090";

	// 6.4
	public static final String CHNL_USER_STATUS_RESUMED = "125265";
	public static final String CHNL_USER_STATUS_SUSPENDED = "125266";

	// VAS services in app
	public static final String MAPP_VAS_SERVICES = "10020";
	public static final String MAPP_VAS_SERVICES_FAILED = "10021";

	public static final String PIN_REQUIRED = "1017200";

	public static final String USER_DELETE_REQUEST_SUCCESS = "202506";
	public static final String CHNL_ERROR_DAILY_SUBSCRIBER_IN_COUNTREACHED = "6027";
	public static final String CHNL_ERROR_WEEKLY_SUBSCRIBER_IN_COUNTREACHED = "6028";
	public static final String CHNL_ERROR_MONTHLY_SUBSCRIBER_IN_COUNTREACHED = "6029";
	public static final String MVD_CHNL_ERROR_MONTHLY_SUBSCRIBER_IN_VALREACHED = "18925";
	public static final String PROMOTION_MESSAGE_NOTIFICATION_TXN = "1040012";
	public static final String OPTINOUT_PROMOTION_MESSAGE_NOTIFICATION_TXN = "1016127";
	public static final String LOYALTY_PROGRESSIVE_MESSAGE_FAIL = "1021070";

	// added for c2s reversal through cce and bcu
	public static final String C2S_SENDER_FAIL_KEY_PRE_REVERSAL = "1044569";

	public static final String SUBSCRIBER_NOT_ACTIVE = "1017117";

	// roam penalty
	public static final String CHNL_ERROR_SNDR_BAL_LESS_ROAM = "105000";
	public static final String CHNL_ROAM_PENALTY_MSG = "105001";
	public static final String CHNL_ERROR_OWNR_BAL_LESS_ROAM = "105002";
	public static final String CHNL_OWNR_ROAM_PENALTY_MSG = "105003";
	public static final String CHNL_ROAM_COMM_SLAB = "105004";
	public static final String USER_ROAM_DEBIT_RECON = "105005";
	public static final String USER_OWNER_ROAM_DEBIT_RECON = "105006";
	public static final String USER_ROAM_CREDIT_RECON = "105007";
	public static final String USER_OWNER_ROAM_CREDIT_RECON = "105008";
	public static final String USER_DELETE_SUCCESS = "12206";

	// added for mobile app
	public static final String USER_OTP_APP = "125268";
	public static final String INVALID_OTP_APP = "125269";
	public static final String MAX_INVALID_ATTEMPTS_REACHED = "125270";
	public static final String OTP_APP_EXPIRED = "125271";

	// for EVD service in app
	public static final String CHANNEL_USER_EVD_REQUEST_SUCCESS = "125267";

	public static final String DEACTIVATE_SUCCESS_MESSAGE = "1002120";

	public static final String USER_CANNOT_SELF_DELETE = "12207";

	public static final String C2S_SENDER_SUCCESS_PRE_REVERSAL_CCE_BCU = "1044570";
	public static final String LASTX_TRANSFER_NOOFTXN_NOTALLOWED = "1016019";
	public static final String LASTX_TRANSFER_SERVICE_TYPE_BLANK = "1016020";
	public static final String LASTX_TRANSFER_C2C_INOUT_BLANK = "1016021";
	public static final String LASTX_TRANSFER_SERVICEWISE_NO_TRANSACTION_DONE = "1016022";
	public static final String LASTX_TRANSFER_MSISDN1_BLANK = "1016023";

	// IAT CP2P
	public static final String ICP2P_SENDER_SUCCESS_WITHOUT_ACCESSFEE = "1040039";
	public static final String ICP2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_D = "1016025";
	public static final String ICP2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_M = "1016026";
	public static final String ERROR_ICP2P_SAME_MSISDN_TRANSFER_NOTALLWD = "1016027";
	public static final String ICP2P_ERROR_EXCEPTION = "1040013";
	public static final String ICP2P_RECEIVER_FAIL = "1040014";
	public static final String ICP2P_FAIL_R = "1040015";
	public static final String ICP2P_SENDER_ALREADY_REG_NOT_FOUND_IN_VAL = "1040016";
	public static final String ICP2P_ERROR_EXCEPTION_TKING_TIME_TILL_VAL_S = "1040017";
	public static final String ICP2P_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP = "1040018";
	public static final String ICP2P_SENDER_AUTO_REG_SUCCESS_WITHPIN = "1040019";
	public static final String ICP2P_SENDER_AUTO_REG_SUCCESS = "1040020";
	public static final String ICP2P_SENDER_SUCCESS = "1040021";
	public static final String ICP2P_SENDER_SUCCESS_WITHOUT_POSTBAL = "1040022";
	public static final String ICP2P_RECEIVER_AMBIGOUS_MESSAGE_KEY = "1040023";
	public static final String ICP2P_RECEIVER_FAIL_MESSAGE_KEY = "1040024";
	public static final String ICP2P_RECEIVER_GET_NUMBER_BACK_SUCCESS = "1040025";
	public static final String ICP2P_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS = "1040026";
	public static final String ICP2P_RECEIVER_SUCCESS = "1040027";
	public static final String ICP2P_RECEIVER_SUCCESS_WITH_BONUS = "1040028";
	public static final String ICP2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "1040029";
	public static final String ICP2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "1040030";
	public static final String ICP2P_NOTFOUND_PAYMENTINTERFACEMAPPING = "1040031";
	public static final String ICP2P_RECEIVER_UNDERPROCESS = "1040032";
	public static final String ICP2P_SENDER_UNDERPROCESS_B4VAL = "1040033";
	public static final String ICP2P_SENDER_UNDERPROCESS = "1040034";
	public static final String ICP2P_SENDER_CREDIT_BACK = "1040035";
	public static final String ICP2P_SENDER_CREDIT_BACK_WITHOUT_POSTBAL = "1040036";
	public static final String ICP2P_SENDER_FAIL = "1040037";
	public static final String ICP2P_NOTFOUND_SERVICEINTERFACEMAPPING = "1040038";
	public static final String ERROR_ERP_C2S_TXN_PENDING = "4517";

	// added for auto c2c and auto o2c fail messages
	public static final String AUTO_O2C_CONFIGURATION_ERROR_SNDR = "1004096";
	public static final String AUTO_CHNL_CONFIGURATION_ERROR_SNDR = "20021";

	public static final String OWNER_USR_BALCREDIT = "1040092";

	public static final String C2S_SENDER_SUCCESS_RET_PRE_REVERSAL = "1061142";
	public static final String C2S_SENDER_SUCCESS_OWNER_PRE_REVERSAL = "1061143";
	public static final String USER_ROAM_RCREV_CREDIT_RECON = "1061144";
	public static final String USER_OWNER__RCREV_ROAM_CREDIT_RECON = "1061145";

	public static final String TXN_STATUS_RC_AMBIGUOUS = "2500001";
	public static final String TXN_STATUS_RC_AMBIGUOUS1 = "2500002";
	public static final String TXN_STATUS_RC_OWNER_AMBIGUOUS = "2500003";
	public static final String REVERSAL_NOT_ALLOWED = "2500004";
	public static final String LASTX_TRANSFER_NOOFTXN_NOTNUMERIC = "1040040";

	// Code Merging from idea to 6.6.0
	public static final String C2S_REVERSAL_INVALID_TXNID = "5999";

	// added for CardGroup Enquiry API
	public static final String CARDGROUP_ENQUIRY_SUCCESS = "1017149";
	public static final String CARDGROUP_ENQUIRY_FAIL = "1017150";
	public static final String CARDGROUP_ENQUIRY_SERVICETYPE_BLANK = "1017151";
	public static final String CARDGROUP_ENQUIRY_AMOUNT_BLANK = "1017152";
	public static final String CARDGROUP_ENQUIRY_SENDR_MSISDN_BLANK = "1017153";
	public static final String CARDGROUP_ENQUIRY_RECR_MSISDN_BLANK = "1017154";

	// Message sent on CCE/SUPER CCE creation
	public static final String USER_CCE_WEB_ACTIVE = "12021";

	/* Rest error codes */
	public static final String PRETUPS_REST_GENERAL_ERROR = "400";

	/* Commission profile status REST */
	public static final String LOAD_DOMAIN_LIST_SUCCESS = "210001";
	public static final String LOAD_COMMISSION_PROFILE_LIST_SUCCESS = "210002";
	public static final String COMMISSION_PROFILE_SAVE_SUSPEND_SUCCESS = "210003";
	public static final String COMMISSION_PROFILE_STATUS_CHANGE_FAILED_WITHOUT_LANGUAGE_MESSAGE = "210004";

	/* C2S Reversal REST */
	public static final String C2S_REVERSAL_REQUEST_INITIATED = "220001";
	public static final String C2S_REVERSAL_NO_SERVICE_ASSIGNED = "220002";
	public static final String C2S_REVERSAL_SERVICE_INVALID = "220003";
	public static final String C2S_REVERSAL_SESSION_DATA_NOT_FOUND = "220004";
	public static final String C2S_REVERSAL_MESSAGE_GATEAWAY_NOT_ACTIVE = "220005";
	public static final String C2S_REVERSAL_REQ_MESSAGE_GATEAWAY_NOT_ACTIVE = "220006";
	public static final String FAILED_TO_LOAD_INSTANCE_ID = "220007";
	public static final String C2S_REVERSAL_CONNECTION_FAILED = "220008";
	public static final String C2S_REVERSAL_REQUEST_TO_GET_TXN_LIST_SUCCESS = "220009";
	public static final String C2S_REVERSAL_TXN_STATUS_CHECK_SUCCESS = "220010";

	/* Barred and Unbarred User REST */
	public static final String SUBSCRIBER_BARRED_USER_UNAUTHORIZED = "230001";
	public static final String SUBSCRIBER_BARRED_USER_UNSUPPORTED_NETWORK = "230002";
	public static final String SUBSCRIBER_BARRED_USER_ALREADY_EXIST = "230003";
	public static final String SUBSCRIBER_UNBARRED_USER_NOT_EXIST = "230004";
	public static final String SUBSCRIBER_BARRED_USER_C2S_NO_ACTIVE_USER = "230005";
	public static final String SUBSCRIBER_BARRED_USER_MOBILE_NOT_AUTHORISE = "230006";
	public static final String MOB_NO_BARRED_SUCCESSFULLY = "230007";
	public static final String SUBSCRIBER_VIEW_BAR_USER_NOT_EXIST = "230008";
	public static final String SUBSCRIBER_BARRED_NO_BAR_TYPE = "230009";
	public static final String SUBSCRIBER_UNBARRED_USER_UNAUTHORIZED = "230010";
	public static final String UNBARRING_FAILED_INCORRECT_AS_USER_INFO = "230011";
	public static final String USER_UNBARRED_SUCCESSFULLY = "230012";

	// Added by Ashutosh for pushing message in case of batch o2c withdraw
	public static final String O2C_WITHDRAW_ADMIN_MESSAGE = "125262";
	public static final String C2C_WITHDRAW_ADMIN_MESSAGE = "125263";
	public static final String O2C_WITHDRAW_USER_MESSAGE = "125264";
	public static final String INSUFFICIENT_BALANCE = "232324";
	public static final String THRESHOLD_BALANCE_REACHED = "232325";

	/* Transaction summary report */

	public static final String MONTHLYUSRTXNSUMRY_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "1016113";

	public static final String MONTHLYUSRTXNSUMRY_COULD_NOT_UPDATE_MAX_DONE_DATE = "1016114";

	public static final String MONTHLYUSRTXNSUMRY_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "1016115";

	public static final String MONTHLYUSRTXNSUMRY_ERROR_EXCEPTION = "1016116";
	// added for Promo VAS
	public static final String C2S_ERROR_MAX_100_EXTERNALDATA1 = "1011000011";
	public static final String C2S_ERROR_MAX_100_EXTERNALDATA2 = "1011000012";
	public static final String C2S_ERROR_MAX_100_EXTERNALDATA3 = "1011000013";
	public static final String C2S_ERROR_MAX_100_EXTERNALDATA4 = "1011000014";
	public static final String C2S_ERROR_MAX_100_EXTERNALDATA5 = "1011000015";
	public static final String C2S_ERROR_MAX_100_EXTERNALDATA6 = "1011000016";
	public static final String C2S_ERROR_MAX_100_EXTERNALDATA7 = "1011000017";
	public static final String C2S_ERROR_MAX_100_EXTERNALDATA8 = "1011000018";
	public static final String C2S_ERROR_MAX_100_EXTERNALDATA9 = "1011000019";
	public static final String C2S_ERROR_MAX_100_EXTERNALDATA10 = "1011000020";
	public static final String ERROR_INVALID_BONUSAMOUNT = "308";
	// added for promo recharge
	public static final String C2S_PROMO_COMMISSION_SUCCESS = "271";
	public static final String C2S_COMMON_BONUS_SUCCESS = "272";
	public static final String C2S_COMMON_SUCCESS = "273";
	public static final String PROMO_VAS_SENDER_SUCCESS = "300";
	public static final String PROMO_VAS_ADJUSTMENT_SUCCESS = "301";
	public static final String VAS_REV_SENDER_SUCCESS = "302";
	public static final String VAS_REV_ADJUSTMENT_SUCCESS = "303";
	// IRIS Changes
	public static final String PVAS_EXTERNALDATA4_INVALID = "1011000021";
	public static final String PROMORC_SENDER_SUCCESS = "304";
	public static final String PROMORC_SENDER_SUCCESS_OFFER = "305";
	public static final String PROMOPPB_SENDER_SUCCESS = "306";
	public static final String PROMOPPB_SENDER_SUCCESS_OFFER = "307";
	public static final String PROMO_VAS_SENDER_SUCCESS_OFFER = "309";
	public static final String VAS_REV_SENDER_SUCCESS_OFFER = "310";
	/* EXTGW Currency Request */
	public static final String CURRENCY_RECORD_NOT_UPDATED = "2400001";
	public static final String CURRENCY_RECORD_EXECUTED_SUCCESFULLY = "2400002";

	public static final String CURRENCY_CONVERSION_LIST_NOT_EXIST = "240001";
	public static final String CURRENCY_CONVERSION_SUCCESS = "240002";

	public static final String PARENT_USER_SUSPENDED = "2500005";
	public static final String ICCID_MAPPED_SUCCESSFUL = "10040051";
	public static final String CHANNEL_USER_TRANSFER_INITIATED = "25000001";
	public static final String CHANNEL_USER_TRANSFER_ALREADY_INITIATED = "25000002";
	public static final String CHANNEL_USER_TRANSFER_CANNOT_INITIATED = "25000003";
	public static final String CHANNEL_USER_TRANSFER_INITIATED_PARENT = "25000004";
	public static final String CHANNEL_USER_TRANSFER_INITIATED_OWNER = "25000005";
	public static final String C2S_INVALID_CURRENCY_CODE = "2400003";
	// added for owner addcom
	public static final String SMS_DIFFCAL_SUCCESS = "24005";

	// low base
	public static final String LB_CONFIGUARATION_ERROR = "5595";
	public static final String LB_SYSTEMLIMIT_ERROR = "5596";
	public static final String LB_FILEREAD_ERROR = "5597";
	public static final String LB_FILE_ERROR = "5598";
	public static final String LOW_BASE_GENERAL_EXCEPTION = "5594";
	public static final String LB_FILEUPLOAD_ERROR = "5592";
	// changes for ZB and FNF
	public static final String MESSAGE_FOR_ZB = "2600001";
	public static final String MSISDN1_MESSAGE_FOR_FNF = "2600002";
	public static final String MSISDN2_MESSAGE_FOR_FNF = "2600003";
	public static final String MSISDN_MESSAGE_FOR_LB = "2600004";

	public static final String LB_REPORT_ALREADY_PROCESSED = "27000000";
	// Added for Low Base Transaction

	public static final String LOW_BASE_RECHARGE_NO_DATA_FOUND = "25000006";

	// added for channel user transfer
	public static final String FROM_PARENT_SAME_TO_PARENT = "80801";
	public static final String INVALID_NETWORK = "80802";
	public static final String INVALID_DOMAIN = "80803";
	public static final String INVALID_GEOGRAPHY = "80804";
	public static final String USER_CANNOT_BE_TRANSFER = "80805";
	public static final String TOPARENT_INVALID = "80806";
	public static final String TO_USER_CATEGORY_INVALID = "80807";
	public static final String TO_USER_GEOGRAPHY_INVALID = "80808";
	public static final String COMPLETE_USER_HIERARCHY_NOT_SUSPENDED = "80809";
	public static final String PENDING_TXN_FOR_USER_HIERARCHY = "80810";
	public static final String USER_ALREADY_MOVED = "80811";
	public static final String USER_TRANSFERE_SUCCESS = "80812";
	public static final String USER_TRANSFERE_FAIL = "80813";
	public static final String PENDING_TXN_FOR_USER = "80814";
	public static final String FROM_USER_VALIDATION_FAIL = "80815";
	public static final String USER_DETAIL_NOT_EXIST = "80816";
	public static final String PARENT_DETAIL_NOT_EXIST = "80817";
	public static final String USER_TRANSFER_ACROSSDOM_FAIL = "80818";
	public static final String INVALID_CATEGORY_NOTLOWEST_IN_HIERARCHY = "80819";
	public static final String EITHER_USERMSISDN_OR_ORIGINID_EXTCODE_REQUIRED = "80820";

	// added for channel user transfer
	public static final String EXTCODE_REQ_WITH_ORIGINID = "88800";
	public static final String INVALID_ORIGINID = "88801";
	public static final String INVALID_EXTCODE = "88802";
	public static final String ORIGINID_TAG_MISSING = "88803";
	public static final String PARENT_ORIGINID_TAG_MISSING = "88804";
	public static final String USER_NETWORK_TAG_MISSING = "88805";
	public static final String USER_GEOGRAPHY_TAG_MISSING = "88806";
	public static final String INVALID_PARENT_ORIGINID = "88807";
	public static final String INVALID_PARENT_EXTCODE = "88808";
	public static final String USER_INVALID_ORIGINID = "10102";
	public static final String USER_INVALID_MSISDN = "10103";
	public static final String EXTSYS_REQ_PRIMARY_ORIGINID_EXTCODE_BLANK = "10082";
	public static final String EXTSYS_REQ_ORIGINID_BLANK_OR_LENGTH_EXCEEDS = "1004080"; // for OriginId Naveen
	public static final String USER_REQ_SENDER_DETAILS_INVALID = "10088";
	public static final String USER_TRANSFER_INITIATED_USER_SEARCH = "25000006";

	public static final String EITHER_FROMUSERMSISDN_OR_FROMLOGINID_REQUIRED = "80821";
	public static final String EITHER_TOUSERMSISDN_OR_TOLOGINID_REQUIRED = "80822";
	public static final String INVALID_LOGIN_ID = "80826";
	public static final String MSISDN_FROM_OTHER_NETWORK = "80827";
	public static final String SUBSCRIBER_SCHEDULE_USER_UNSUPPORTED_NETWORK = "80828";
	// Added for Daily Self/Child Transfer Status Report
	public static final String DSR_NO_RECORDS = "80823";
	public static final String DSR_RECORDS_FETCHED = "80824";
	public static final String DSR_CHILD_MSISDN_INVALID = "80825";
	public static final String SCHEDULED_LIST_NOT_FOUND = "80830";
	public static final String SCHEDULE_RECHARGE_ZERO_RECORD = "80831";
	public static final String SCHEDULE_RECHARGE_INVALID_NO_OF_RECORDS = "80832";
	public static final String SCHEDULE_RECHARGE_SCHEDULE_NOW_NO_OF_RECORDS_EXCEED = "80833";
	public static final String SCHEDULE_RECHARGE_NO_VALID_DATA_FOUND = "80834";
	public static final String SCHEDULE_RECHARGE_SUCCESSFULL = "80835";
	public static final String AUTOSTOCKCREATION_ERROR_EXCEPTION = "80936";
	public static final String MAPP_INVALID_MHASH = "1080001";
	public static final String MAPP_INVALID_TOKEN = "1080002";
	public static final String MAPP_TOKEN_EXPIRED = "1080003";
	public static final String AUTO_STOCK_CREATED = "80937";
	public static final String AUTO_STOCK_CREATED_EMAIL = "80948";

	// O2C Transfer's Network Stock deduction process
	public static final String O2CTRFDDT_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "80939";
	public static final String O2CTRFDDT_COULD_NOT_UPDATE_MAX_DONE_DATE = "80940";
	public static final String O2CTRFDDT_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "80941";
	public static final String O2CTRFDDT_ERROR_EXCEPTION = "80942";
	public static final String O2CTRFDDT_ERROR_EXCEPTION1 = "80943";
	public static final String O2CTRFDDT_ERROR_EXCEPTION2 = "80944";
	public static final String O2CTRFDDT_ERROR_EXCEPTION3 = "80945";
	public static final String NTWRK_STOCK_DEDUCTION_TRF_AMOUNT = "80946";
	public static final String NTWRK_STOCK_DEDUCTION_COMSN_AMOUNT = "80947";

	public static final String SCHEDULE_TOPUP_ADMIN_MESSAGE = "80938";
	public static final String INTERFACE_NOT_ACTIVE_EVD = "6695";

	// owner credit back message for roam recharge penalty
	public static final String TXN_STATUS_RC_OWNER_CREDITBACK_AMBIGUOUS = "80950";
	public static final String MOBILE_NUMBER_MAX_LENGTH = "80951";
	public static final String MSISDN_PREFIX_LENGTH = "80952";
	public static final String NO_NETWORK_FOUND_FOR_MSISDN = "80953";
	public static final String MOBILE_NUMBER_NOT_FROM_SUPPORTED_NETWORK = "80954";
	public static final String MOBILE_NUMBER_NOT_SCHEDULED = "80955";
	public static final String NO_SCHEDULE_FOUND = "80956";
	public static final String SCHEDULE_CAN_NOT_CANCEL = "80957";
	public static final String UNABLE_TO_CANCEL = "80958";
	public static final String INVALID_SCHEDULE_STATUS = "80959";

	// Batch Recharge Reschedule
	public static final String BATCH_ID_NOT_FOUND = "3000100";
	public static final String BATCH_LIST_LOADED = "3000101";
	public static final String BATCH_FILE_CREATED = "3000102";
	public static final String BATCH_FILE_CREATE_FAILED = "3000103";
	public static final String ERROR_WHILE_CANCELLING_OLD_BATCH = "3000104";
	public static final String NO_RECORDS_FOUND_IN_RESCHEDULE = "3000105";
	public static final String INVALID_NOS_OF_RECORDS_IN_RESCHEDULE = "3000106";
	public static final String BATCH_FILE_NOT_AVAILABLE_IN_RESCHEDULE = "3000107";
	public static final String NO_VALID_DATA_FOUND_IN_RESCHEDULE = "3000108";
	public static final String RESCHEDULE_PROCCESSED_WITH_ERRORS = "3000109";
	public static final String RESCHEDULE_PROCCESSED_SUCCESSFULLY = "3000110";

	public static final String VOMS_CHANGE_STATUS_EXECUTED_ALREADY = "89901";
	public static final String VOMS_CHANGE_STATUS_ERROR = "89902";
	public static final String VOMS_CHANGE_STATUS_COMPLETE = "89903";

	public static final String VMS_VAS_RECEIVER_SUCCESS = "202_VMS_VAS";
	public static final String VMS_VAS_RECEIVER_SUCCESS_WITH_BONUS = "239_VMS_VAS";
	public static final String VOUCHER_CONSUMPTION_SUCCESS_VAS = "24004_VAS";
	public static final String VOUCHER_CONSUMPTION_SUCCESS_VAS_NOTBUNDLE = "24005_VAS";

	public static final String ERROR_FROM_TO_SERIALNO_INVALID = "1016114";
	public static final String ERROR_FROM_TO_SERIALNO_RANGE_INVALID = "1016115";
	public static final String ERROR_VOMS_STATUS_INVALID = "1016116";
	public static final String ERROR_FROM_SERIALNO_INVALID = "1016117";
	public static final String ERROR_VOMS_STATUS_FROM_TO_SERIALNO_DIFF = "1016118";
	public static final String ERROR_VOMS_CUR_REQ_STATUS_MAPPING_INVALID = "1016119";
	public static final String ERROR_VOMS_CUR_REQ_STATUS_MAPPING_EXITS = "1016120";
	public static final String ERROR_VOMS_ERROR = "1016121";

	public static final String NO_RECORDS_FOUND_IN_VOUCHER_UPLOAD = "5953";
	public static final String VOUCHER_UPLOAD_PROCESS_NO_SUCH_PROFILE = "5954";
	public static final String VOUCHER_UPLOAD_PROCESS_NETWORK_CODE_EMPTY = "5955";
	public static final String C2S_ERROR_BLANK_PIN = "1016131";
	public static final String C2S_ERROR_BLANK_SUBID = "1016132";
	public static final String C2S_ERROR_BLANK_SNO = "1016133";
	public static final String C2S_ERROR_BLANK_TXNID = "1016134";
	public static final String C2S_ERROR_BLANK_INFOTYPE = "1016135";

	// Added for channel SOS
	public static final String C2S_ERROR_SOS_SETTLE_INVALIDMESSAGEFORMAT = "3000400";
	public static final String SOS_NOT_ENABLE = "3000401";
	public static final String SOS_MANUAL_SETTLEMENT_NOT_ALLOWED = "3000402";
	public static final String SOS_INCORRECT_WALLET = "3000403";
	public static final String SOS_NO_PENDING_TXN_TO_SETTLE = "3000404";
	public static final String SOS_CHANNEL_SETTLEMENT_SUCCESS = "3000405";
	public static final String SOS_CHANNEL_SETTLEMENT_FAILURE = "3000406";
	public static final String SOS_NO_TXN_FOUND_OR_NOT_AUTHORIZED = "3000407";
	public static final String SOS_INVALID_MSISDN = "3000408";
	public static final String SOS_MSISDN_DETAILS_NOT_FOUND = "3000409";
	public static final String SOS_NOT_ENABLED_USER = "3000410";

	public static final String SOS_THRESHOLD_NOT_REACHED = "3000411";
	public static final String SOS_PENDING = "3000412";

	public static final String SOS_PENDING_FOR_SETTLEMENT = "3000417";

	public static final String SOS_NOT_ALLOWED_HIERARCHY = "3000413";
	public static final String SOS_ENABLE_SUCCESS = "303130";
	public static final String SOS_DISABLE_SUCCESS = "303131";

	public static final String ELIGIBLE_FOR_SOS = "303132";

	public static final String SOS_NOT_SETTLED_FOR_DELETION = "3000414";

	public static final String VOUCHER_UPLOAD_PROCESS_EXECUTED = "5949";
	public static final String CHNL_SENDER_NOTALLOWED = "3000415";
	public static final String SOS_INVALID_PRODUCT_CODE = "3000416";
	public static final String SOS_WITHDRAW_SENDER = "300418";
	public static final String SOS_WITHDRAW_RECEIVER = "300419";
	public static final String SOS_SENDER_BALANCE_LESS = "3000420";
	public static final String LAST_RECHARGE_ENABLE_SUCCESS = "303133";
	public static final String LAST_RECHARGE_DISABLE_SUCCESS = "303134";
	public static final String LR_WITHDRAW_MESSAGE_SENDER = "303135";

	public static final String LR_NOT_SETTLED_FOR_DELETION = "303140";
	public static final String SOS_SETTLEMENT_PENDING = "303141";
	public static final String LR_SETTLEMENT_PENDING = "303142";
	public static final String ERROR_USER_TRANSFER_PARENT_OWNER_BAR = "3000421";
	public static final String ERROR_USER_TRANSFER_PARENT_OWNER_OUT_SUSPENDED = "3000422";
	public static final String ERROR_USER_TRANSFER_IN_SUSPENDED = "3000423";
	public static final String FOC_OPT_CHNL_TRANSFER_SMS4 = "8116";

	public static final String REST_RECIEVER_DATA_INVALID = "3031001";
	public static final String STOCK_DEDUCTION_C2S = "3031002";
	public static final String C2STRFDDT_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "3031003";
	public static final String C2STRFDDT_AMB_OR_UP_TXN_FOUND = "3031004";
	public static final String C2STRFDDT_COULD_NOT_UPDATE_MAX_DONE_DATE = "3031005";
	public static final String C2STRFDDT_ERROR_EXCEPTION = "3031006";
	public static final String C2STRFDDT_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "3031007";

	public static final String TARGET_BASED_MESSAGES = "3000424";

	public static final String EXT_XML_ERROR_INVALID_PASSWORD = "7893";

	// Entries for CP2P Data Transfer

	public static final String CP2P_DATA_ERROR_INVALIDMESSAGEFORMAT = "2031201";
	public static final String CP2P_DATA_ERROR_BLANK_AMOUNT = "2031202";
	public static final String CP2P_DATA_ERROR_EXCEPTION = "2031203";
	public static final String CP2P_DATA_ERROR_SENDER_MAIN_BALANCE_LOW = "2031204";

	// Batch O2C Initiate added by Anjali
	public static final String BATCH_O2C_INITITATION_EXECUTED_UPTO_DATE_NOT_FOUND = "1028115";
	// For User Deletion by Anjali
	public static final String USER_ACTIVE = "1028116";

	public static final String TARBASEDCOMM_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "3000425";
	public static final String TARBASEDCOMM_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "3000426";
	public static final String TARBASEDCOMM_PROCESS_NO_OTF_DETAILS_FOUND = "3000427";
	public static final String TARGET_BASED_MESSAGES_TRANSACTION = "3000428";
	public static final String C2S_ERROR_INVALID_ACTION_MSGFORMAT = "43230";
	public static final String ERROR_INVALID_USERNAMEPRFX = "10303131";

	public static final String ERROR_ERP_BATCH_C2C_TXN_PENDING = "4520";
	public static final String ERROR_ERP_BATCH_O2C_TXN_PENDING = "4521";

	public static final String PROCESS_ADMIN_MESSAGE = "3000429";

	public static final String SELF_DAILY_TRANSFER_LIST_SUCCESS_C2S = "2700001";
	public static final String OTHERUSER_DAILY_TRANSFER_LIST_SUCCESS_C2S = "2700002";

	public static final String GEOGRAPHY_NOT_EXISTS = "1004130";
	public static final String DEFAULT_GEO_MODIFIED = "1004131";

	public static final String ERROR_INVALID_GATEWAY_LOGIN = "2124";
	public static final String INVALID_REQ_EITHER_MSISDN_LOGINID_REQ = "2125";
	public static final String EXTSYS_REQ_STATUS_BLANK_OR_LENGTH_EXCEEDS = "1004081";
	public static final String EXTSYS_REQ_APPOINTMENTDATE_LENGTH_EXCEEDS = "1004082";
	public static final String EXTSYS_REQ_ALLOWEDIP_LENGTH_EXCEEDS = "1004083";
	public static final String EXTSYS_REQ_ALLOWEDDAYS_LENGTH_EXCEEDS = "1004084";
	public static final String EXTSYS_REQ_ALLOWEDTIMEFROM_LENGTH_EXCEEDS = "1004085";
	public static final String EXTSYS_REQ_ALLOWEDTIMETO_LENGTH_EXCEEDS = "1004086";
	public static final String EXTSYS_REQ_STATUS_INVALID = "1004087";
	public static final String ERROR_ERP_CHNL_USER_MSISDN_PREFIX_MISMATCH = "1004088";
	public static final String EXTSYS_REQ_USR_IP_INVALID = "1004089";
	public static final String EXTSYS_REQ_USR_ALLOWEDDAYS_INVALID = "1004090";
	public static final String EXTSYS_REQ_USR_FROMTIME_INVALID = "1004091";
	public static final String EXTSYS_REQ_USR_TOTIME_INVALID = "1004092";
	public static final String EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID = "1004093";
	public static final String EXTSYS_REQ_GROUPROLE_LENGTH_EXCEEDS = "1004094";
	public static final String EXTSYS_REQ_SERVICES_LENGTH_EXCEEDS = "1004095";
	public static final String EXTSYS_REQ_USR_GROUPROLE_INVALID = "1004096";
	public static final String EXTSYS_REQ_USR_SERVICES_INVALID = "1004097";
	public static final String EXTSYS_REQ_USR_GRADE_INVALID = "1004098";
	public static final String EXTSYS_REQ_USR_INSUSPEND_INVALID = "1004099";
	public static final String EXTSYS_REQ_USR_OUTSUSPEND_INVALID = "1004100";
	public static final String EXTSYS_REQ_USR_FAX_INVALID = "1004101";

	public static final String GEO_DOMAIN_TYPE_NOT_FOUND = "1004102";
	public static final String GEO_PARENT_CODE_BLANK = "1004103";
	public static final String GEO_PARENT_CODE_NOT_FOUND = "1004104";
	public static final String GEO_PARENT_CODE_INVALID = "1004105";
	public static final String GEO_CODE_LENGTH = "1004106";
	public static final String GEO_CODE_ALREADY_EXISTS = "1004107";
	public static final String GEO_CODE_NOT_FOUND = "1004108";
	public static final String GEO_NAME_ALREADY_EXISTS = "1004109";
	public static final String GEO_NAME_INVALID = "1004110";
	public static final String GEO_NAME_LENGTH_EXCEEDS = "1004111";
	public static final String GEO_SHORTNAME_ALREADY_EXISTS = "1004112";
	public static final String GEO_SHORTNAME_INVALID = "1004113";
	public static final String GEO_SHORTNAME_LENGTH_EXCEEDS = "1004114";
	public static final String GEO_DESCRIPTION_LENGTH_EXCEEDS = "1004115";
	public static final String GEO_ISDEFAULT_INVALID = "1004116";
	public static final String GEO_ACTION_INVALID = "1004117";
	public static final String GEOGRAPHY_ADD_FAILED = "1004118";
	public static final String GEOGRAPHY_UPDATE_FAILED = "1004119";
	public static final String GEO_DOMAIN_TYPE_INVALID = "1004120";
	public static final String GEOGRAPHY_NO_DETAILS_FOUND = "1004121";
	public static final String GEOGRAPHY_CODE_AND_NAME_BLANK = "1004122";
	public static final String GEO_CODE_BLANK = "1004123";
	public static final String GEO_NAME_BLANK = "1004124";
	public static final String GEO_PARENT_NAME_INVALID = "1004125";
	public static final String GEO_SHORTNAME_BLANK = "1004126";
	public static final String GEO_IS_DEFAULT = "1004127";
	public static final String GEO_CHILD_EXISTS = "1004128";
	public static final String GEO_ALREADY_ASSOCIATED = "1004129";
	public static final String CCE_XML_ERROR_FROM_DATE_REQUIRED = "7519";
	public static final String CCE_XML_ERROR_TO_DATE_REQUIRED = "7520";
	public static final String ERROR_ERP_CHNL_USER_INVALID_EXTCODE = "1004140";
	public static final String USER_MODIFY_INVALID_MSISDN = "1004141";
	public static final String USER_TRANSFER_CHANNEL_OUT_SUSPENDED = "1004142";
	public static final String RECHARGE_NOT_FOUND = "1004143";
	public static final String C2S_SUMMARY_ENQUIRY_SUCCESS = "1004144";
	public static final String DATES_MISSING = "1004145";
	public static final String MAX_ALLOWED_INTERVAL_EXCEEDS = "1004146";
	public static final String MAX_PAST_DAYS_LIMIT_EXCEEDS = "1004147";
	public static final String OPERATOR_RECEIVER_LOGIN_PASSWORD_REQUIRED = "1004148";
	// Added for Channel Txn Enquiry API
	public static final String CHNL_TXN_ENQ_ERROR = "2031206";
	// Added for Reverse Commission Hierarchy
	public static final String REVERSE_COMMISION_EXECUTED_UPTO_DATE_NOT_FOUND = "2031207";
	public static final String REVERSE_COMMISION_COULD_NOT_UPDATE_MAX_DONE_DATE = "2031208";
	// Added for Channel User Commission Earned enquiry
	public static final String LAST_COMM_SUCCESS = "2031209";
	public static final String LAST_DAYS_ERROR = "2031210";
	// Added for Channel Txn Enquiry API
	public static final String NOT_IN_GEOGRAPHY = "2031211";
	public static final String BURN_RATE_ALERT_MESSAGE = "2031212";
	public static final String BURN_RATE_ALERT_EMAIL_ADMIN = "2031215";
	public static final String BURN_RATE_ALERT_EMAIL_USER = "2031216";

	/// added for RECHARGERECORD_FMS_Detail
	public static final String RECHARGERECORD_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE = "4000";
	public static final String RECHARGERECORD_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND = "4001";
	public static final String RECHARGERECORD_AMB_OR_UP_TXN_FOUND = "4002";
	public static final String RECHARGERECORD_PROCESS_ALREADY_EXECUTED_TILL_TODAY = "4003";
	public static final String RECHARGERECORD_COULD_NOT_UPDATE_MAX_DONE_DATE = "4004";
	public static final String RECHARGERECORD_ERROR_EXCEPTION = "4005";
	public static final String RECHARGERECORD_ALL_FILES_DELETED = "4006";
	public static final String USER_MIGRATION_IN_PROCESS = "1100116";

	public static final String MAXTPS_ERROR_INVALID_QHOUR = "3031008";
	public static final String MAXTPS_ERROR_INVALID_QDATE = "3031009";
	public static final String MAXTPS_SUCCESS = "3031010";

	public static final String CHNL_ERROR_PROMO_BONUS_NOTNUMERIC = "3031011";
	public static final String CHNL_ERROR_PROMO_BONUS_LESSTHANZERO = "3031012";
	public static final String PROMO_BONUS_ADJUSTMENT_SUCCESS = "3031013";

	// added for subscriber routing for robi
	public static final String SUB_ROUT_REG_ERROR = "1100118";
	public static final String EXTSYS_BLANK_OPERATOR_DETAILS = "1100119";
	public static final String EXTSYS_BLANK_SUBSCRIBER_DETAILS = "1100120";
	public static final String SUB_ROUT_REG_SUCCESS = "1100121";
	public static final String RECHARGE_ERROR_DIFFERENT_NETWORK = "1100122";
	public static final String INVALID_SUBSCRIBER_TYPE = "1100123";
	public static final String SAME_PREFIX_REG_ERROR = "1100124";
	public static final String UNKNOWN_PREFIX_ERROR = "1100125";
	public static final String INTERFACE_INVALID_LENGTH = "1100126";

	// Colombia specific System Preferences - Start
	public static final String INVALID_CONVERSION_RATE = "2400004";
	public static final String SID_ENQUIRY_SUCCESS_MESSAGE = "1002121";
	public static final String MIS_DEPENDENCY = "1002123";
	public static final String NO_TRANSACTION_DONE = "1002124";

	// wireless
	public static final String WIRCT_SENDER_SUCCESS = "300000";
	public static final String WIRC_REQ_SELECTOR_MISSING = "300001";
	public static final String WIRCT_RECEIVER_GET_NUMBER_BACK_SUCCESS = "3000003";
	public static final String WIRCT_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS = "3000004";
	public static final String WIRCT_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "3000005";
	public static final String WIRCT_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "3000006";
	public static final String WIRCT_RECEIVER_SUCCESS = "3000007";
	public static final String WIRCT_RECEIVER_SUCCESS_WITH_BONUS = "3000008";
	public static final String WIRCT_RECEIVER_UNDERPROCESS = "3000009";
	public static final String WIRCT_SENDER_UNDERPROCESS = "3000010";
	public static final String WIRCT_SENDER_UNDERPROCESS_B4VAL = "3000011";
	public static final String WIRCT_RECEIVER_AMBIGOUS_KEY = "3000012";
	public static final String WIRCT_RECEIVER_FAIL_KEY = "3000013";
	public static final String WIRC_PROMO_MAPPING_NOT_EXIST = "3000014";
	public static final String WIRC_ACCOUNT_ID_NOT_CORRECT = "3000015";
	// Colombia specific System Preferences - End
	public static final String BARRED_SUBSCRIBER_FOR_INVALID_PIN_SNDR_MSG = "4231";
	public static final String BARRED_SUBSCRIBER_FOR_INVALID_PIN_RECV_MSG = "4232";
	public static final String TARGET_BASED_CAC_MESSAGES = "3000430";
	public static final String TARGET_BASED_CBC_MESSAGES = "3000431";
	public static final String MESSAGE_FOR_STATUS_CHANGE_IN_LIFECYCLE = "3000432";
	// Message for FOC through external gateway
	public static final String DW_HIERARCHY_COMMISSION_TRANSFER = "2041211";
	// added for OLO Recharge
	public static final String OLO_RECEIVER_FAIL = "217_OLO";// 217;
	public static final String OLO_RECEIVER_FAIL_KEY = "231_OLO";// 231

	public static final String OLO_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "237_OLO";// 237
	public static final String OLO_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "603_OLO";// 603
	public static final String OLO_RECEIVER_SUCCESS = "207_OLO";// 207
	public static final String OLO_RECEIVER_SUCCESS_WITH_BONUS = "236_OLO";// 236
	public static final String OLO_RECEIVER_SUCCESS_ALL_BALANCES = "243_OLO";// 243
	public static final String OLO_RECEIVER_UNDERPROCESS = "208_OLO";// 208
	public static final String OLO_RECEIVER_AMBIGOUS_KEY = "230_OLO";// 230
	public static final String OLO_RECEIVER_UNDERPROCESS_SUCCESS = "204_OLO";// 204
	public static String OLO_IMPLICIT_MSG = "242_OLO"; // 242

	public static final String OLO_SENDER_SUCCESS = "210_OLO";
	public static final String OLO_SENDER_UNDERPROCESS = "209_OLO";
	public static final String OLO_SENDER_UNDERPROCESS_B4VAL = "216_OLO";
	public static final String OLO_SENDER_CREDIT_SUCCESS = "221_OLO";
	public static final String OLO_SENDER_UNDERPROCESS_SUCCESS = "203_OLO";
	public static final String OLO_PARENT_SUCCESS = "24226_OLO";

	public static final String OLO_ERROR_INVALID_AMOUNT = "2014_OLO";
	public static String CHNL_TRANSFER_SUCCESS_LASTTXN = "1005037";
	public static final String ONLINE_O2C_TRANSFER_SUCCESS = "3000432";
	// DB Recharge
	public static String DBRC_IMPLICIT_MSG = "1016201";
	public static final String EXTSYS_VOUCHER_SEGMENT_INVALID = "8051";
	public static final String DBRC_RECEIVER_GET_NUMBER_BACK_SUCCESS = "1016202";
	public static final String DBRC_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS = "1016203";
	public static final String DBRC_RECEIVER_SUCCESS = "1016204";
	public static final String DBRC_RECEIVER_SUCCESS_WITH_BONUS = "1016205";
	public static final String DBRC_RECEIVER_SUCCESS_WITHOUT_POSTBAL = "1016206";
	public static final String DBRC_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS = "1016207";
	public static final String DBRC_RECEIVER_SUCCESS_ALL_BALANCES = "1016208";// 243_DBRC
	public static final String DBRC_SENDER_SUCCESS = "1016209";
	public static final String DBRC_RECEIVER_UNDERPROCESS = "1016210";
	public static final String DBRC_SENDER_UNDERPROCESS = "1016211";
	public static final String DBRC_SENDER_UNDERPROCESS_B4VAL = "1016212";
	public static final String DBRC_RECEIVER_AMBIGOUS_KEY = "1016213";
	public static final String DBRC_RECEIVER_FAIL_KEY = "1016214";
	public static final String DBRC_SENDER_CREDIT_SUCCESS = "1016215";
	public static final String DBRC_RECEIVER_FAIL = "1016216";
	public static final String DBRC_ERROR_EXCEPTION = "1016217";
	// Added by yogesh for ambiguous transaction
	public static final String AMB_SERVER_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE = "998811";
	public static final String AMB_FILE_GENERATION_ERROR = "913544";
	public static final String AMB_RCRD_FORMAT_IMPROPER = "913545";
	public static final String AMB_MIN_INTERVAL_LESS = "913546";
	public static final String AMB_TRAILER_RCRD_FORMAT_IMPROPER = "913547";
	public static final String AMB_CONSTANT_PARAMETER_NOT_FOUND = "913548";
	public static final String ERROR_AMB_FTP_CONNECT_FAIL = "9100001";
	public static final String ERROR_AMB_FTP_LOGIN_FAILED = "9100002";
	public static final String ERROR_AMB_FTP_FILE_UPLOAD = "9100003";
	public static final String AMB_FILE_UPLOAD_HANDLER_EXCEPTION = "9100004";
	public static final String AMB_CONSTANT_ENTRY_MISSING = "9100005";
	public static final String AMB_UTILITY_EXCEPTION = "9100006";
	public static final String AMB_SERVER_PROCESSING_EXCEPTION = "9100007";
	// Added by yogesh for Bulk Bonus
	public static final String BULK_BONUS_BEFORE_C2SMIS = "2110210";
	public static final String PROMO_VAS_SUCCESS = "1046027";
	public static final String O2C_TRANSFER_VOUCHER_INITIATE = "3000433";
	public static final String O2C_TRANSFER_VOUCHER_APPROVED = "3000434";
	public static final String O2C_TRANSFER_VOUCHER_CANCELLED = "3000435";

	public static final String C2C_TRANSFER_VOUCHER_APPROVED_SENDER = "3000441";
	public static final String C2C_TRANSFER_VOUCHER_APPROVED_RECEIVER = "3000440";

	public static final String C2C_TRANSFER_VOUCHER_CANCEL_SENDER = "3000442";
	public static final String C2C_TRANSFER_VOUCHER_CANCEL_RECEIVER = "3000443";

	public static final String LMB_BLANK_AMOUNT = "1011062";
	public static final String O2C_TRANSFER_VOUCHER_OUTSIDE_SETTLEMENT = "3000436";
	public static final String O2C_TRANSFER_VOUCHER_APPROVED_OUTSIDE_SETTLEMENT = "3000437";

	public static final String O2C_TRANSFER_INITIATE = "3000439";
	// Account info USSD
	public static final String USSD_BLANK_SELECTOR = "8010";
	public static final String USSD_BLANK_MSISDN = "8011";
	// LANGUAGE CODE ERROR
	public static final String LANG_CODE_NOT_EXIST = "8015";
	public static final String USSD_ADDBUDDY_MSISDNBLNK = "8017";
	public static final String USSD_ADDBUDDY_PINBLANK = "8018";
	public static final String USSD_ADDBUDDY_BLKBUDDYNAME = "8019";

//Added for VOms Pin Expiry Extension
	public static final String MESSAGE_FOR_VOUCHER_PIN_EXT_SUCCESS = "8020";
	public static final String EXTSYS_REQ_EXPDATE_LESSTHAN_CURRDATE = "8021";
	public static final String NO_OF_VOUCHERS_EXCEEDING_TOTAL_LIMIT = "8022";
	public static final String VOUCHERS_PROCESS_OFFLINE = "8023";
	public static final String FTP_SUCCESS = "19900";
	public static final String FTP_FAIL = "19901";
	public static final String ERROR_VOMS_PIN_EXP_EXT = "8024";

	public static final String VOUCHER_TYPE_BLANK = "8025";
	public static final String FROM_SERIAL_NO_BLANK = "8026";
	public static final String TO_SERIAL_NO_BLANK = "8027";
	public static final String EXPIRY_DATE_BLANK = "8028";
	public static final String USER_NOT_ALLOWED = "8029";
	public static final String VOUCHER_REQUEST_TYPE_BLANK = "8030";
	public static final String VOUCHER_REQUEST_DATE_BLANK = "8031";
	public static final String VOUCHER_EXTNWCODE_BLANK = "8032";
	public static final String VOUCHER_PIN_OR_SERIALNO_BLANK = "8033";
	public static final String VOUCHER_LANGUAGE1_BLANK = "8034";
	public static final String VOUCHER_EXPIRY_CHANGE_REASON_BLANK = "8035";
	public static final String DATE_NOT_CURRENT_DATE = "8036";

	public static final String INAVALID_VOUCHER_SUBSCRIBER_MAPPING = "1016129";
	public static final String VOUCHER_SERIAL_NO_ISREQUIRED = "1016130";
	public static final String VOUCHER_SUBSCRIBER_MSISDN_ISREQUIRED = "1016131";

	public static final String VOUCHER_SUBSCRIBER_MSISDN_MISMATCH_ROLLBK = "1016132";
	public static final String VOUCHER_EXTERNAL_REF_ID_MISMATCH_ROLLBK = "1016133";

	public static final String EXTSYS_REQ_STATE_CHANGE_REASON_INVALID = "1004149";
	public static final String MESSAGE_FOR_VOUCHER_NOT_FOUND = "8037";
	public static final String MESSAGE_FOR_OFFLINE_VOUCHER_STATUS = "8038";
	public static final String EXTSYS_REQ_DATE_BEFORE = "8039";
	public static final String EXTSYS_REQ_SENDER_INVALID = "8040";
	public static final String LOGINID_BLANK = "8041";
	public static final String PASSWORD_BLANK = "8042";
	public static final String SERVICE_TYPE_BLANK = "8043";

	public static final String ERROR_USERBARRED_MAX_INVALID_PIN_ATTEMPTS = "1004150";
	public static final String FROM_SERIAL_NOT_NUMERIC = "8044";
	public static final String TO_SERIAL_NOT_NUMERIC = "8045";
	public static final String SERIAL_NO_INVALID_LENGTH = "8046";
	public static final String DATE_FORMAT_INVALID = "8047";
	public static final String ERROR_VOMS_DIFF_NETWORK = "8048";

	public static final String CARD_GROUP_CHANGE_STATUS_SUCCESS = "241000";
	public static final String VOUCHER_ERROR_TRANSFER_RULE_SUSPENDED = "2080";
	public static final String VOUCHER_CARD_GROUP_SLAB_SUSPENDED = "2081";
	public static final String VOUCHER_CARD_GROUP_VALUE_NOT_IN_RANGE = "2082";
	public static final String VOUCHER_GEN_NOTIFICATION = "8049";
	public static final String VOUCHER_REJECT_NOTIFICATION = "8050";

	public static final String USER_INVALID_LOGINID = "29000";
	public static final String USER_INVALID_PSWD = "29001";
	public static final String USER_UNAUTHORIZED = "29002";
	public static final String REQUEST_LOGGEdIN_LOGINID_NOTPRESENT = "29003";
	public static final String REQUEST_LOGGEdIN_PASSWORD_NOTPRESENT = "29004";
	public static final String VOUCHER_NOT_ASSOCIATED = "8052";
	public static final String SUBSCRIBER_NOT_FOUND = "8053";
	public static final String VOUCHER_NOT_AVAILABLE = "8054";
	public static final String VOUCHER_TYPE_INVALID = "8055";
	public static final String VOUCHER_PRODUCT_INVALID = "8056";
	public static final String ERROR_NO_ACTIVE_PODUCT = "8057";
	public static final String ERROR_ONLINE_DVD_LIMIT = "8058";
	public static final String VOUCHER_NOT_ASSOSIATED = "8059";
	public static final String CHNL_ERROR_SENDER_OUT_SUSPEND_DVD = "8060";
	public static final String CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND_DVD = "8061";
	public static final String CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND_DVD = "8062";
	public static final String DVD_SENDER_SUCCESS = "8063";
	public static final String DVD_RECEIVER_FAIL = "8064";
	public static final String VOUCHER_ASSOSIATED_NOT_EXIST = "8065";
	public static final String ERROR_DVD_FAIL = "8066";
	public static final String VOUCHER_TYPE_DOESNOT_EXIST = "8067";
	public static final String MRP_DOESNOT_EXIST = "8068";
	public static final String INVALID_SEGMENT = "8069";
	public static final String INTERFACE_NOT_ACTIVE_DVD = "8070";
	public static final String C2S_SENDER_UNDERPROCESS_B4VAL_DVD = "8074";
	public static final String SUBSCRIBER_SUCCESS_MESSAGE = "8071";
	public static final String DVD_RECEIVER_SUCCESS = "8075";
	public static final String MSISDN2_BLANK = "8073";

	// added by Ashish for VIL
	public static final String C2S_ERROR_MISSING_IMSI_INFORMATION = "210500";
	public static final String VOUCHER_GENERATION_NOTIFICATION = "210501";

	public static final String VOMS_O2C_VOUCHER_NOT_FOUND = "5265001";
	public static final String VOMS_O2C_VOUCHERS_FROM_DIFFERENT_NETWORK = "5265002";
	public static final String VOMS_O2C_SOME_VOUCHERS_DIFFERENT_STATUS = "5265003";
	public static final String VOMS_O2C_FROM_SERIAL_PENDING_APPROVAL = "5265004";
	public static final String VOMS_O2C_TO_SERIAL_PENDING_APPROVAL = "5265005";
	public static final String VOMS_O2C_FROM_SERIAL_INVALID = "5265006";
	public static final String VOMS_O2C_TO_SERIAL_INVALID = "5265007";
	public static final String VOMS_O2C_INVALID_BATCH = "5265008";
	public static final String VOMS_O2C_SUCCESSFUL = "5265009";
	public static final String VOMS_O2C_ERROR = "5265010";
	public static final String MAX_REQUESTED_DVD_ERROR = "8076";
	public static final String C2C_TRANSFER_INITIATE = "1080004";
	public static final String C2C_TRANSFER_APPROVAL = "1080005";
	public static final String C2C_TRF_APPROVAL = "1080006";
	public static final String C2C_TRF_APPROVAL_API = "1080007";
	public static final String C2C_TRF_APPROVAL_REJECT = "1080008";
	public static final String C2C_TRF_INITIATE_ZERO_LEVEL = "1080011";
	public static final String C2C_TRF_VOMS_LIST = "8105";
	public static final String C2C_TRF_APPROVAL_RECORD_NOT_FOUND = "1080009";
	public static final String C2C_TRF_APPROVAL_RECORD_CLOSE_CNCL = "1080010";
	public static final String VOMS_C2C_SUCCESSFUL = "8106";
	public static final String C2C_VOMS_TRANSFER_APPROVAL = "8107";
	public static final String VOMS_C2C_ERROR = "8108";
	public static final String NOT_BLANK_VOUCHER_BUNDLE = "77772";
	public static final String SERIALNO_OR_MASTERSERIALNO_IS_REQUIRED = "77774";
	public static final String VOU_BUN_PRF_MOD_SUCCESS = "77775";
	public static final String INVALID_MASTER_SERIALNO = "77776";
	public static final String VOU_PRF_MOD_SUCCESS = "77777";
	public static final String INVALID_PRODUCTID = "77778";
	public static final String INVALID_SERIALNO = "77779";
	public static final String RESTRICTED_PRODUCTID = "77789";
	public static final String VOUCHER_DENO_REQUIRED = "77780";
	public static final String VOUCHER_UNASSIGNED_INVALID = "77781";
	public static final String VOUCHER_FROM_SERIALNO_INVALID = "77782";
	public static final String VOUCHER_TO_SERIALNO_INVALID = "77783";
	public static final String VOUCHER_TO_SERIALNO_REQUIRED = "77784";
	public static final String VOUCHER_FROM_SERIALNO_REQUIRED = "77785";
	public static final String VOUCHER_DENO_INVALID = "77786";
	public static final String VOUCHER_FROMTO_SERIALNO_INVALID = "77787";
	public static final String C2C_VOMS_TRF_INITIATE_ZERO_LEVEL = "8109";
	public static final String C2C_APPROVAL_QTY_GREATER_THAN_LAST_APPROVED = "1080011";
	public static final String C2C_VOMS_NOT_ASSOCIATED = "8117";
	public static final String C2C_VOMS_INVALID_SNO = "8118";
	public static final String ERROR_INVALID_REQUESTFORMAT_C2C = "8119";
	public static final String VOMS_C2C_FROM_SERIAL_PENDING_APPROVAL = "8120";
	public static final String VOMS_C2C_INVALID_BATCH = "8121";
	public static final String QUANTITY_NOT_NUMERIC = "8122";
	public static final String C2C_VOMS_MULTIPLE_PROFILES = "8123";

	public static final String C2C_ERROR_INVAILD_APPLEVEL = "8129";
	public static final String C2C_APPLIST_EMPTY = "8130";
	public static final String INVAILD_SEARCH_CRITERIA = "8131";
	public static final String INVAILD_DOMAIN_NAME = "8132";
	public static final String INVAILD_CATEGORY_NAME = "8133";
	public static final String INVAILD_GEOGRAPHY_NAME = "8134";
	public static final String INVAILD_TRANSFERSUB_TYPE = "8135";
	public static final String TRF_ID_EMPTY = "8136";
	public static final String NETWORK_CODE_EMPTY = "8137";
	public static final String NETWORK_CODE_FOR_EMPTY = "8138";
	public static final String TRF_TYPE_EMPTY = "8139";
	public static final String INVALID_TXN = "8140";
	public static final String TXN_SUCCESSFUL = "8141";
	public static final String FAIL_R_DVD = "8142";
	public static final String C2S_RECEIVER_FAIL_DVD = "8143";
	public static final String C2S_RECEIVER_FAIL_KEY_DVD = "8144";
	public static final String INVALID_LANGUAGE_CODE = "11112";
	public static final String C2S_RECEIVER_AMBIGOUS_KEY_DVD = "8145";
	public static final String DVD_SUCCESS = "8146";
	public static final String ERROR_BLANK_SOURCE_TYPE = "11113";
	public static final String PAYMENT_INST_NUM_BLANK = "8147";
	public static final String INVALID_PAYMENT_INST_TYPE = "8148";
	public static final String INVALID_RECIEVER_CREDENTIALS = "8149";
	public static final String FROM_SNO_EMPTY = "8150";
	public static final String TO_SNO_EMPTY = "8151";
	public static final String FROM_SNO_NOT_NUMERIC = "8152";
	public static final String TO_SNO_NOT_NUMERIC = "8153";
	public static final String USER_HIERRACHY_SUCCESS = "20000";
	public static final String USER_HIERRACHY_ERROR = "20011";
	public static final String C2S_ERROR_EXCEPTION_DVD = "20012";
	public static final String VOMS_FROM_SERIAL_C2C_PENDING_APPROVAL = "5265010";
	public static final String VOMS_TO_SERIAL_C2C_PENDING_APPROVAL = "5265011";
	public static final String VOMS_INVALID_BATCH_C2C = "5265012";
	public static final String USER_INVALID_DETAILS_NOT_FOUND = "5265014";
	public static final String MSISDN_INVALID_OR_BLANK = "5265015";
	public static final String C2C_RECENT_TXN_FAILED = "5265016";
	public static final String C2S_NO_TRNX_EXIST = "5265017";
	public static final String COMMISSION_SUCCESS = "5265019";
	public static final String COMMISSION_DATE_ERROR = "5265020";
	public static final String COMMISSION_FAILURE = "5265021";
	public static final String TOTAL_TRANSACTION_DETAILED_VIEW_FAILURE = "5265022";
	public static final String FROM_ROW_GREATER = "5265023";
	public static final String INVALID_ROW_VALUES = "5265024";
	public static final String PROVIDE_ROW_VALUES = "5265025";
	public static final String NO_CATEGORYLIST = "5265026";
	public static final String NO_DOMAIN_FOUND = "5265027";

	// Added for MRP Successive block timeout for channel transaction
	public static final String CHNL_TXN_REC_LAST_SUCCESS_REQ_BLOCK_RECEIVER = "18934";
	public static final String EXTSYS_DATE_INVALID_FORMAT = "8154";
	public static final String C2S_TRANSFER_SUCCESS = "2031219";
	public static final String C2S_TRANSFER_FAIL = "7516";
	public static final String C2S_ERROR_FROMDATE_EQUAL_CURRENTDATE = "7600";
	public static final String C2S_ERROR_TODATE_EQUAL_CURRENTDATE = "7601";
	public static final String C2S_ERROR_TOP_PROD = "7602";
	public static final String RESEND_OTP_REACHED_LIMIT = "7603";
	public static final String OTP_MAX_INVALID = "125272";
	public static final String PIN_CHNG_SUCCESS = "125273";
	public static final String INVALID_OTP_ATTEMPT = "125274";
	public static final String OTP_SENT_MESSAGE = "7604";
	public static final String OTP_SENT_ON_SMS = "7605";
	public static final String OTP_SENT_ON_EMAIL = "7606";
	public static final String INVALID_NETWORK_CODE = "125278";
	public static final String SERVICE_TYPE_INVALID = "7607";
	public static final String SERVICE_TYPE_NOT_ALLOWED = "7608";
	public static final String OTP_USER_TRANSFER_SMS = "2241340";
	public static final String OTP_USER_TRANSFER_EMAIL = "2241341";

	// added for cross network code
	public static final String PRODUCT_ID_NOT_FOUND = "4747";
	public static final String MIN_LENGTH_REQUIRED = "7609";
	public static final String PROVIDE_AT_LEAST_ONE = "7610";
	public static final String TYPE_INVALID = "7611";
	public static final String PMTYPE_SUCCESS = "7612";
	public static final String NO_DETAIL_FOUND = "7613";
	public static final String C2C_INVALID_TRANSFER_MODE = "7614";
	public static final String SUB_MAIL_DELETE_USER = "5265028";
	public static final String DUPLICATE_MSISDN_IN_LIST = "52650306";
	public static final String MAND_FIELD_MISSING = "52650307";
	public static final String INVALID_IDENTIFIER_VALUE = "7615";
	public static final String BLANK_IDENTIFIER_VALUE = "7616";
	public static final String BLANK_IDENTIFIER_TYPE = "7617";
	public static final String INVALID_USER = "7618";
	public static final String SUCCESS = "9020";
	public static final String CHANNEL_USER_UPDATE = "52650308";
	public static final String MAND_PARAMS_MISSING = "7619";
	public static final String PARENT_CATEGORY_INVALID = "7620";
	public static final String USER_CATEGORY_INVALID = "7621";
	public static final String USER_CATEGORY_INVALID_FOR_PARENT_CATEGORY = "7622";
	public static final String PARENT_CATEGORY_INVALID_FOR_LOGGED_USER = "7623";
	public static final String SHORT_NAME_BLANK = "7656";
	public static final String FIRST_NAME_BLANK = "7657";
	public static final String OWNER_USER_REQUIRED = "7658";
	public static final String LOWER_HIERARCHY_TO_HIGHER_HIERARCHY_NOT_POSSIBLE = "7659";
	public static final String USER_NAME_ERROR = "52650281";
	public static final String PAGINATION_VALUES_INVALID = "52650282";
	public static final String PAGINATION_VALUES_REQ = "52650383";
	public static final String NO_CHNL_USER_FOUND = "52650384";
	public static final String NO_RECORD_PAGE = "52650385";
	public static final String NO_GEOGRAPHY = "52650386";
	public static final String DOMAIN_INVALID = "52650387";
	public static final String COMMISSION_SET_ID_INVALID = "7660";
	public static final String TRANSFER_RULE_TYPE_NOT_EXIST = "7661";
	public static final String PROVIDE_LOGINID_OR_MSISDN = "7624";
	public static final String DEL_CHILD_USR_EXIST = "9021";
	public static final String DEL_SOS_PENDING = "9022";
	public static final String DEL_LR_PENDING = "9023";
	public static final String DEL_O2C_PENDING = "9024";
	public static final String DEL_FOC_PENDING = "9025";
	public static final String DEL_RESTRICTED_MSISDN = "9026";
	public static final String DEL_OWNER_SUSPENDED = "9027";
	public static final String DEL_SUCCESS = "9028";
	public static final String DEL_SUCC_APPROVAL_REQD = "9029";
	public static final String DETAILS_BLANK = "100823";
	public static final String DETAIL_NOT_FOUND_WITH_USRNAMEORCATORDOM = "52650388";
	public static final String PARENTUSER_EMPTY = "52650389";
	public static final String PARENT_USER_NOT_IN_GIVEN_CATEGORY = "9030";
	public static final String PARENT_USER_GEOGRAPHY_NOT_FOUND = "9031";
	public static final String PARENT_USER_NOT_GIVEN = "9032";
	public static final String PARENT_CATEGORY_NOT_GIVEN = "9033";
	public static final String PARENT_USER_INVALID = "9034";
	public static final String PARENT_USERS_NOT_EXIST = "9035";
	public static final String USER_DO_NOT_EXIST = "7625";
	public static final String PARENT_USER_OUTSIDE_LOGGED_USER_HIERARCHY = "9036";
	public static final String INVALID_ID_TYPE = "7626";
	public static final String STK_PROFILE_NOT_FOUND = "9038";
	public static final String STK_PROFILE_NOT_VALID = "9037";
	public static final String OWNER_USER_DOES_NOT_EXIST = "9039";
	public static final String USERNAME_TYPE_DOES_NOT_EXIST = "9041";
	public static final String PAYMENT_TYPE_DOES_NOT_EXIST = "9042";
	public static final String DOCUMENT_TYPE_DOES_NOT_EXIST = "9043";
	public static final String LANGUAGE_DOES_NOT_EXIST = "9044";
	public static final String DESIGNATION_NOT_VALID = "9045";
	public static final String SHORT_NAME_INVALID = "9046";
	public static final String OUTLET_CODE_DOES_NOT_EXIST = "9047";
	public static final String SUB_OUTLET_CODE_DOES_NOT_EXIST = "9048";
	public static final String GEOGRAPHY_DOMAIN_CODE_DOES_NOT_EXIST = "9049";
	public static final String EMPCODE_INVALID = "9050";
	public static final String CARD_GROUP_CHNG_STATUS_INVALID = "241001";
	public static final String CARD_GROUP_NAME_EMPTY = "241002";
	public static final String SERVICE_TYPE_DESC_EMPTY = "241003";
	public static final String SUB_SERVICE_TYPE_DESC_EMPTY = "241004";
	public static final String MODIFIED_BY_EMPTY = "241005";
	public static final String STATUS_EMPTY = "241006";
	public static final String LANGUAGE1_MSG_EMPTY = "241007";
	public static final String LANGUAGE2_MSG_EMPTY = "241008";
	public static final String LOGGED_IN_USER_PIN_CHANGE = "241009";
	public static final String RESET_PIN_FAILURE = "241010";
	public static final String STATUS_INVALID = "241011";
	public static final String O2C_TRANSFER_SUCCESS_MAPP = "241014";
	public static final String INVALID_TXN_ID = "241015";
	public static final String INVALID_STATUS = "241016";
	public static final String O2C_TRANSFER_REJECT_MAPP = "241017";
	public static final String MAPPGW_INVALID_PRODUCT_CODE = "241012";
	public static final String MAPPGW_INVALID_PRODUCTS_FORMAT = "241013";
	public static final String INVALID_TOKEN_FORMAT = "241018";
	public static final String NO_EXPIRY_IN_PAYLOAD = "241019";
	public static final String MULTIPLE_PROD = "241020";
	public static final String REQ_QNT = "241021";
	public static final String USER_SUCCESS = "241022";
	public static final String TIME_RANGE = "1003008";
	public static final String UNAUTHORIZED_REQUEST = "241023";
	public static final String FILE_NOT_AVAILABLE = "241024";
	public static final String INVALID_TRF_TYPE = "241025";
	public static final String DNEO_NOT_NUMERIC = "241026";
	public static final String FROM_SR_NOT_NUMERIC = "241027";
	public static final String TO_SR_NOT_NUMERIC = "241028";
	public static final String FROM_SR_REQ = "241029";
	public static final String TO_SR_REQ = "241030";
	public static final String DENO_REQ = "241031";
	public static final String DENO_LESS_THAN_ZERO = "241032";
	public static final String FR_SR_LESS_THAN_TO_SR = "241033";
	public static final String VCR_NOT_SEQ = "241034";
	public static final String INVALID_DENO = "241035";
	public static final String NO_ACTIVE_MRP = "241036";
	public static final String QTY_NOT_NUMERIC = "241037";
	public static final String QTY_REQ = "241038";
	public static final String SEG_REQ = "241039";
	public static final String VTYPE_REQ = "241040";

	public static final String NO_PROD_EXIST = "241041";
	public static final String OUT_SUSPENDED = "241042";
	public static final String FILE_WRITE_ERROR = "241043";
	public static final String FILE_FORMAT_NOT_SUPPORTED = "241044";
	public static final String BLANK_APLHA_CAT = "241045";
	public static final String BLANK_APLHA_OPERATIONTYPE = "241046";
	public static final String BLANK_PRODUCTCODE = "241047";
	public static final String BLANK_DOMAIN = "241214";
	public static final String INVALID_FILE_TYPE = "241213";

	public static final String EMPTY_FILE_SIZE_IN_CONSTANTS = "241048";
	public static final String EMPTY_SEPERATOR_IN_CONSTANTS = "241049";
	public static final String BAD_REQUEST = "241050";
	public static final String BATCH_UPLOAD_FILE_EXISTS = "241051";
	public static final String BATCH_UPLOAD_DIRECTORY_DO_NOT_EXISTS = "241052";
	public static final String INVALID_FILE_INPUT_TYPE = "241053";
	public static final String DUPLICATE_MOBILE_NO = "241054";
	public static final String DUPLICATE_LOGIN_ID = "241055";
	public static final String DUPLICATE_EXT_CODE = "241056";
	public static final String MANDATORY_FIELD = "241057";
	public static final String INVALID_MOBILE_NO = "241058";
	public static final String QUANTITY_NULL = "241059";
	public static final String QUANTITY_NEGATIVE = "241060";
	public static final String FILE_SIZE_LARGE = "241061";
	public static final String INVALID_FILE_FORMAT = "241062";
	public static final String EMPTY_FILE_PATH_IN_CONSTANTS = "241063";
	public static final String INVALID_FILE_INPUT = "241064";
	public static final String INVALID_FILE_NAME1 = "241065";
	public static final String EMPTY_PATTERN_IN_CONSTATNS = "241066";
	public static final String FILE_SIZE_PREFERENCE_EMPTY = "241067";
	public static final String BATCH_EXECUTION_ALREADY_PROCESS = "241068";
	public static final String BLANK_PAYMENT_DETAILS = "241069";
	public static final String BLANK_PRODUCT_DETAILS = "241070";
	public static final String INVALID_PAYMENT_DETAILS = "241071";
	public static final String INVALID_PAYMENT_GATEWAY = "241072";
	public static final String ALL_RECORDS_ERROR = "241073";
	public static final String PARTIAL_PROCESS_NOT_ALLOWED = "241074";
	public static final String ALL_RECORDS_PROCESSED = "241075";
	public static final String BATCH_ID_GENERATED = "241076";
	public static final String BATCH_NAME_EMPTY = "241077";
	public static final String LANGUAGE1_LENGTH = "241078";
	public static final String LANGUAGE2_LENGTH = "241079";
	public static final String MSISDN_REGEX = "241080";
	public static final String AMOUNT_REGEX = "241081";
	public static final String NAME_REGEX = "241082";
	public static final String MIN = "241083";
	public static final String MAX = "241084";
	public static final String INVALID_COLUMN_HEADER = "241085";
	public static final String SEARCH_MSISDN_REGEX = "241086";
	public static final String REFERENCE_NUMBER_REGEX = "241087";
	public static final String PAYMENT_INST_NUM_REGEX = "241088";
	public static final String FROM_SERIAL_NO_REGEX = "241089";
	public static final String TO_SERIAL_NO_REGEX = "241090";
	public static final String QUANTITY_REGEX = "241091";
	public static final String COUNTRY_DOES_NOT_EXIST = "241092";
	public static final String LOCALE_DOES_NOT_EXIST = "241093";
	public static final String O2C_PACKAGE_ASSOCIATION_EXECUTED_UPTO_DATE_NOT_FOUND = "77790";
	public static final String VOUCHER_EXPIRY_CHANGE_DATA_ERROR = "77788";
	public static final String VOUCHER_EXPIRY_CHANGE_PROCESS = "77773";
	public static final String NO_DENOM = "241094";
	public static final String EMPTY_VOUCHER_DETAILS = "241095";
	public static final String DUPLICATE_PROFILE = "241096";
	public static final String INVALID_LIST_SIZE_FOR_DVD = "241097";
	public static final String ARRAY_INDEX_OUT_OF_BOUND_EXCEPTION = "241098";
	public static final String O2C_INITIATE_SUCCESS = "241099";
	public static final String C2C_BULK_SUCCESS = "241100";
	public static final String C2C_BULK_SCH_SUCCESS = "241101";
	public static final String C2C_BULK_RESCH_SUCCESS = "241102";
	public static final String C2C_BULK_No_RECORDS = "241103";
	public static final String C2C_BULK_FILE_UNSUCCESSFUL = "241104";
	public static final String C2C_BULK_NOT_CURRNT_DATE = "241106";
	public static final String C2C_BULK_LESSTHAN_CURRNT_DATE = "241107";
	public static final String C2S_BULK_SCHEDULE_SUCCESS = "241108";
	public static final String MSISDN_TXNID_BLANK = "241109";
	public static final String BLANK_VOUCHER_DETAILS = "241110";
	public static final String BLANK_RECEIVER_MSISDN = "241111";
	public static final String O2_DENO_BLANK = "241112";
	public static final String O2C_DENO_INVALID = "241113";
	public static final String O2C_FROMSNO_REQ = "241114";
	public static final String O2C_TOSNO_REQ = "241115";
	public static final String O2C_FROMSNO_NUMERIC = "241116";
	public static final String O2C_TOSNO_NUMERIC = "241117";
	public static final String O2C_FROM_TO_INVALID = "241118";
	public static final String O2C_SNO_MULTPROF = " 241119";
	public static final String O2C_SNO_NOTASSOCIATED = " 241120";
	public static final String O2C_SNO_SEQUENTIAL = "241121";

	public static final String INVAILD_VOUCHER = "241122";
	public static final String PRODUCT_NOT_ASSOSIATED = "241123";
	public static final String PRODUCT_NOT_MATCH_WITH_TRANSFERRULE = "241124";
	public static final String FROM_SERIAL_PENDING = "241125";
	public static final String TO_SERIAL_PENDING = "241126";
	public static final String FROM_SERIAL_SOLD = "241127";
	public static final String TO_SERIAL_SOLD = "241128";
	public static final String O2C_VOUCHER_TRF_SUCCESS = "241129";
	public static final String VOUCHER_TYPE_REQUIRED = "241130";
	public static final String VOUCHER_SEGMENT_REQUIRED = "241131";
	public static final String O2C_REMARKS_REQUIRED = "241132";
	public static final String O2C_REFNO_NOT_NUMERIC = "241133";
	public static final String O2C_QTY_REQ = "241134";
	public static final String O2C_QTY_NUMERIC = "241135";
	public static final String O2C_VOMS_PROF_NOT_EXIST = "241136";
	public static final String USER_DETAIL_NOT_FOUND = "241137";
	public static final String USER_NOT_IN_GEO_DOMAIN_HIERARCHY = "241138";
	public static final String USER_NOT_FOUND = "241139";
	public static final String MORE_THAN_ONE_USER_EXIST = "241140";
	public static final String CHANNEL_USR_NOT_FOUND = "241141";
	public static final String SERVICE_NOT_FOUND = "241142";
	public static final String SVC_NOT_ASSOCIATED = "241143";
	public static final String CHANNEL_OWNER_CATEGORY_NOT_EXIST = "241144";
	public static final String OWNER_USER_LIST_DOES_NOT_EXIST = "241145";
	public static final String INVALID_FILE_HEADINGS = "241146";
	public static final String INVALID_SUBSCRIBER_MSISDN = "241147";
	public static final String NULL_SUBSCRIBER_MSISDN = "241148";
	public static final String CHANNEL_USER_LIST_DOES_NOT_EXIST = "241149";
	public static final String PRODUCTS_NOT_FOUND = "241150";
	public static final String PRODUCTS_NOT_FOUND_ACCORDING_TO_TRANSFER_RULE = "241151";
	public static final String PRODUCTS_NOT_FOUND_FOR_COMMISSION_PROFILE = "241152";
	public static final String FILE_FORMAT_XLS_INVALID = "241153";
	public static final String FILE_FORMAT_XLSX_INVALID = "241154";
	public static final String PARTIAL_SUCCESS = "241155";
	public static final String ALL_FAIL = "241156";
	public static final String NO_RECORD_FOUND_IN_FILE = "241157";
	public static final String O2C_SNO_DENO_DIFF = "241158";
	public static final String INVALID_O2C_APPROVAL_LEVEL = "241159";
	public static final String INVALID_FILE_DATA = "241160";
	public static final String SERVICE_KEYWORD_REQUIRED = "241161";
	public static final String INVALID_NEGATIVE_AMOUNT = "241162";
	public static final String NO_VOUCHER_PRESENT = "241163";
	public static final String INVALID_SERIAL_NUM = "241164";
	public static final String NO_DETAILS_FOR_TXNID = "241165";
	public static final String NO_DETAILS_FOR_TXNID_STATUS = "241166";
	public static final String NO_PARENT_DENOMINATION_EXIST = "241167";
	public static final String NO_PROD_LIST = "241168";
	public static final String NO_ACTIVE_DENOM_LIST = "241169";
	public static final String QTY_ERROR = "241170";
	public static final String BLANK_REQ_QTY = "241171";
	public static final String SERIAL_QTY_NOT_EQUAL = "241172";
	public static final String VOMS_O2C_FROM_SERIAL_PENDING_APPROVAL1 = "241173";
	public static final String VOMS_O2C_TO_SERIAL_PENDING_APPROVAL1 = "241174";
	public static final String BLANK_TOUSERID = "241175";
	public static final String BLANK_FROMUSERID = "241176";
	public static final String INVALID_TRANSFER_DATE = "241177";
	public static final String NO_USERDET_TOUSERID = "241178";
	public static final String C2C_TRF_APPROVAL_NOT_ALLOWED = "241179";
	public static final String ERROR_DVD_QTY_NOT_AVAILABLE = "241180";
	public static final String EMPTY_FILE = "241181";
	public static final String DUPLICATE_ROW = "241182";
	public static final String C2C_TXN_CLOSE_CNCL = "241183";
	public static final String INVALID_TRANSFER_ID = "241183";
	public static final String O2C_APP_REQUEST_EMPTY = "241184";
	public static final String PARTIAL_PROCESS = "241185";
	public static final String NULL_OR_INVALID_VALUE_IN_REQ = "241186";
	public static final String NULL_VALUE_IN_REQ = "241187";
	public static final String APPR_QUANTITY_MORE = "241188";
	public static final String NULL_TRANSACTION_DETAILS = "241189";
	public static final String TXN_ID_NULL = "241190";
	public static final String O2C_APPROVE_OR_REJECT = "241191";
	public static final String O2C_APPROVAL_WENT_WRONG = "241192";
	public static final String INVALID_TO_MSISDN = "241193";
	public static final String INVALID_PURCH_OR_WITHD_PARAM = "241194";
	public static final String INVALID_LOGGEDIN_USER = "241195";
	public static final String BATCH_O2C_TRF_SUCCESS = "241196";
	public static final String GRPH_INVALID_DOMAIN = "241197";
	public static final String INVALID_WITHDRAW_QUANTITY = "241215";
	public static final String O2C_BATCH_TRF_NOT_SUCCESS = "241198";
	public static final String FOC_USER_NOT_FOUND = "241199";
	public static final String FOC_DEF_LEN_LANG1 = "241200";
	public static final String FOC_DEF_LEN_LANG2 = "241201";
	public static final String FOC_NULL_PRODUCT = "241202";
	public static final String FOC_BLANK_QTY = "241203";
	public static final String FOC_USER_STATUS_NOT_CONGIGURED = "241203";
	public static final String FOC_USER_IN_SUS = "241204";
	public static final String BATCH_O2C_TRF_PARTIAL_SUCCESS = "241206";
	public static final String NO_PENDING_TXN = "241207";
	public static final String NOT_ALLOWED_PEBDING_TXN = "241208";
	public static final String TXN_SUCCES = "241209";
	public static final String NO_WIGET_ALLOTED = "241210";
	public static final String INVALID_VOUCHER_QUANTITY = "241211";
	public static final String EMPTY_ROW_ERRORLIST = "241212";
	public static final String USER_NOT_FOUND_MULTIPLE = "241216";
	public static final String INVALID_APPROVAL_TYPE = "241217";
	public static final String EMPTY_BATCH_ID = "241218";
	public static final String MAX_LVL1 = "241219";
	public static final String MAX_LVL2 = "241220";
	public static final String NO_APPROVAL = "241221";
	public static final String INVALID_LENGTH_NOTLANG = "241222";
	public static final String INVALID_LENGTH_REMARKS = "241223";
	public static final String DET_NOT_FOUND_BULK = "241224";
	public static final String BULK_APPRV_FAILED = "241225";
	public static final String USER_NOT_ALLOWED_TO_APPROVE = "241226";
	public static final String BLANK_BONUS_TYPE = "241227";
	public static final String INCORRECT_BONUS_TYPE = "241228";
	public static final String BATCH_DETAIL_NO_NOT_FOUND = "241229";
	public static final String INVALID_FORMAT_FOR_DATE = "241230";
	public static final String USER_NOT_DELETED = "241231";
	public static final String USER_NOT_FOUND_DELETE = "241232";
	public static final String USER_NOT_FOUND_BAR = "241233";
	public static final String CHANNEL_USER_BAR_SUCC = "241234";
	public static final String CHANNEL_USER_ALREADY_BARRRED = "241235";
	public static final String USER_NOT_AUTH_TO_BAR = "241236";
	public static final String USER_SUCCESS_SUSPEND = "241237";
	public static final String USER_SUCCESS_APP_SUSPEND = "241238";
	public static final String FIELD_IS_MAND = "241239";
	public static final String ERROR_INVALID_MODULE_VALUE = "241240";
	public static final String ERROR_BAR_INVALID_MSISDN = "241241";
	public static final String ERROR_REMARKS_LEN_MORE_THAN_ALLOWED = "241242";
	public static final String ERROR_UNSUPPORTED_NETWORK = "241243";
	public static final String ERROR_NETWORK_NOT_MATCHING_REQUEST = "241244";
	public static final String ERROR_INVALID_BARUSERTYPE_VALUE = "241245";
	public static final String ERROR_INVALID_BARTYPELIST_EMPTY = "241246";
	public static final String ERROR_INVALID_BARTYPE_VALUE = "241247";
	public static final String ERROR_BARRED_USER_NOTEXISTINLIST = "241248";
	public static final String USER_NOT_AUTH_TO_UNBAR = "241249";
	public static final String CHANNEL_USER_UNBARRED_SUCC = "241250";
	public static final String ERROR_BARRED_USER_NOTUPDATE = "241251";
	public static final String ERROR_UNBARRED_USER_NOTUPDATE = "241252";
	public static final String ERROR_NOT_BARRED_USER_NOTEXISTINLIST = "241253";
	public static final String STAFF_USER_SUSSESS = "241254";
	public static final String EMAIL_REGEX = "241255";
	public static final String OLD_PIN_REQD = "241256";
	public static final String NEW_PIN_REQD = "241257";
	public static final String NEW_PIN_ALREADY_USED = "241258";
	public static final String OLD_PIN_INVALID = "241259";
	public static final String EMAIL_SUBJECT_RESET = "241260";
	public static final String SUSPEND_LOGINID_SAME_LEVEL = "241261";
	public static final String SUSPEND_LOGINID_DOMAIN_DIFF = "241262";
	public static final String SUSPEND_LOGINID_GEO_DIFF = "241263";
	public static final String STAFF_USER_ADDED_SUCCESS = "241264";
	public static final String PASSWORD_CANT_BLANK = "241265";
	public static final String USERNAME_CANT_BLANK = "241266";
	public static final String STAFF_USER_LIMIT = "241267";
	public static final String PASSWORD_CONFRIMPASS_NOT_SAME = "241268";
	public static final String STAFF_USER_LOGINID_ALREADY_EXIST = "241269";
	public static final String INVALID_EMAIL = "241270";
	public static final String USERNAME_PREFIX_NULL = "241271";
	public static final String NO_USER_EXIST_BY_NAME = "241272";
	public static final String USER_MORE_THAN_ONE = "241273";
	public static final String STAFF_USER_UPDATED_SUCCESS = "241274";
	public static final String MORE_THAN_ONE_PRIMARY_MSISDN = "241275";
	public static final String MSISDN_INVALID = "241276";
	public static final String MSISDN_EXIST = "241277";
	public static final String LOGINID_EXIST_ALREADY = "241278";
	public static final String PIN_REPEATED_CHAR = "241279";
	public static final String PIN_CONSECUTIVE_CHAR = "241280";
	public static final String PIN_NOT_SAME = "241281";
	public static final String PARENT_GEOGRAPHY_NOT_EXIST = "241282";
	public static final String AREA_SEARCH_SUCCESS = "241283";
	public static final String DOMAIN_NOT_EXIST = "241284";
	public static final String GEOGRAPHY_DOMAIN_NOT_EXIST = "241285";
	public static final String PARENT_USER_NOT_FOUND = "241286";
	public static final String DIRECT_AGENT_O2C = "241287";
	public static final String INVALID_REPORT_DISPLAY_COLS = "241288";
	public static final String LOGIN_ID_IS_NULL = "241289";
	public static final String LOGIN_ID_EXISTS = "241290";
	public static final String LOGIN_ID_DOES_NOT_EXISTS = "241291";

	public static final String LOGINID_SPACE_NOT_ALLOWED = "241292";
	public static final String PROPERTY_MISSING = "241293";
	public static final String MSISDN_COUNT_EXCEED = "241294";
	public static final String NONE_NETWORK_PREFIX_FOUND = "241295";
	public static final String PROPERTY_INVALID = "241296";
	public static final String MSISDN_NOT_SUPPORTING_NETWORK = "241297";
	public static final String MULTIPLE_DUPLICATE_MSISDN_FOUND = "241298";
	public static final String GRP_ROLE_NOT_UNDER_CATEGORY = "241299";
	public static final String COMM_PROFILE_NOT_FOUND_UNDER_CATEGORY = "241300";
	public static final String TRF_PROFILE_NOT_FOUND_UNDER_CATEGORY = "241301";
	public static final String SSN_NULL = "241302";
	public static final String EXTERNALCODE_ALPHANUMERIC = "241303";
	public static final String SSN_NOT_NULL = "241304";
	public static final String LENGTH_EXCEED = "241305";
	public static final String RSA_NOT_ALLOWED = "241306";
	public static final String DOCUMENT_EITHER_BOTH_MANDATORY_OPTIONAL = "241307";
	public static final String VOUCHER_LIST_INVALID = "241308";
	public static final String DEFAULT_GROUP_ROLE_NOT_FOUND = "241309";

	public static final String GEO_NOT_IN_PARENT_HIERARCHY = "241310";
	public static final String HIERARCHY_ERROR = "241311";
	public static final String GEO_NOT_VALID_FOR_CATEOGRY = "241312";
	public static final String EXT_CODE_EXIST = "241313";
	public static final String BATCH_USER_INITIATE_SUCCESS = "241314";
	public static final String UPLOAD_CONTAIN_ERRORS = "241315";
	public static final String TRANSFER_RULE_NOT_DEFINED = "5071";
	public static final String TEXT_ONLY_REGEX = "241316";
	public static final String LONGITUDE_LATITUDE_REGEX = "241317";
	public static final String IP4_REGEX = "241318";
	public static final String IP6_REGEX = "241319";
	public static final String INVALID_BARRED_USER = "241320";
	public static final String FETCH_BAR_USER_LIST_SUCC = "241321";
	public static final String USER_NOT_FOUND_BARREDLIST = "241322";
	public static final String IP4CS_REGEX = "241323";
	public static final String IP6CS_REGEX = "241324";
	public static final String INVALID_THRESHOLD = "241325";
	public static final String USER_RESUMED = "1100175";
	public static final String ALLOWED_REQ_TYPE = "241326";
	public static final String CANNOT_RESUME = "2241328";
	public static final String USER_SUCCESS_RESUMED = "2241229";
	public static final String NO_STAFF_EIXST = "2241230";
	public static final String STAFF_ALREADY_SUS = "2241231";
	public static final String NO_STAFF_MSISDN = "2241232";
	public static final String STAFF_ALREADY_EXIST = "2241233";
	public static final String MSISDN_NOT_SUPPORTED = "2241234";
	public static final String NO_STAFF_LOGINID = "2241235";
	public static final String STAFF_LOGINID_ALREADY_SUSPENDED = "2241236";
	public static final String STAFF_LOGINID_ALREADY_ACTIVE = "2241237";
	public static final String INVALID_MSISDN_BATCH = "2241238";
	public static final String BATCH_CANCEL_FAIL = "2241239";
	public static final String BATCH_CANCEL_SUCCESS = "2241240";
	public static final String INVALID_BATCH_OR_ACCESS = "2241241";
	public static final String CATEGORY_LIST_FOUND = "2241242";
	public static final String USER_NOT_IN_SESSION_HIERARCHY = "2241243";
	public static final String NO_NETWORK_PREFIX = "2241244";
	public static final String NO_NETWORK_SUPPORT_MSISDN = "2241245";
	public static final String INVALID_USER_TYPE = "2241247";
	public static final String SCHEDULED_MSISDN_ALREADY_CANCELLED = "2241246";
	public static final String BATCH_CANCELLED_SUCCESSFULLY = "2241248";
	public static final String INVAID_C2C_TAB_REQ = "2241249";
	public static final String INVALID_TRANSFER_SUBTYPE = "2241250";
	public static final String INVALID_TRANSFER_IN_OUT = "2241251";
	public static final String INVALID_SENDER_USER = "2241252";
	public static final String INVALID_TRANSFER_USER_CATGRY = "2241253";
	public static final String INVALID_TRANSFER_USER = "2241254";
	public static final String INVALID_SENDER_MOBILENUMBER = "2241255";
	public static final String INVALID_RECEIVER_MOBILENUMBER = "2241256";
	public static final String MULTIPLE_USER_EXIST = "2241257";
	public static final String C2C_BATCH_APPR_SUCCESS = "2241258";
	public static final String C2C_BATCH_APPR_FULL_FAIL = "2241259";
	public static final String C2C_BATCH_APPR_PARTIAL_FAIL = "2241260";
	public static final String C2C_BATCH_ALREADY_MODIFIED = "2241261";
	public static final String C2C_BATCH_ALREADY_RUNNING = "2241262";
	public static final String REMARKS_REQD_LIMIT = "2241268";
	public static final String INVALID_BATCH_ID = "2241269";
	public static final String NEW_PASSWORD_MATCHES_OLD_PASSWORD = "22412643";
	public static final String PASSWORD_LENGTH_IS_SHORT = "2241264";
	public static final String NEW_PASSWORD_IS_NULL = "2241265";
	public static final String OLD_PASSWORD_ENTERED_IS_WRONG = "2241266";
	public static final String PASSOWRD_REGEX = "2241267";
	public static final String USER_HIERARCHY_SUCCESS_MESSAGE = "2241270";
	public static final String USER_HIERARCHY_ERROR_MESSAGE = "2241271";
	public static final String INVALID_DISTRIBUTION_TYPE = "2241272";
	public static final String INVALID_RECEIVER_USER = "2241273";
	public static final String INVALID_INCLUDE_STAFF = "2241274";
	public static final String EMPTY_MOBILE_TAB_CATEGORY = "2241276";
	public static final String EMPTY_MOBILE_TAB_GEOGRAPHY = "2241277";
	public static final String EMPTY_MOBILE_TAB_USER = "2241278";
	public static final String EMPTY_MOBILE_TAB_TRANSFERUSER = "2241279";
	public static final String EMPTY_MOBILE_TAB_TRANSFERUSER_CATEGORY = "2241280";

	public static final String C2SENQRY_FILEDS_EMPTY = "2241281";
	public static final String C2SENQRY_INVALID_DATERANGE = "2241282";
	public static final String C2SENQRY_NO_CHILD_USERS = "2241283";
	public static final String C2SENQRY_USER_NOTAUTH = "2241284";
	public static final String INVALID_ENQUIRY_TYPE = "2241285";
	public static final String CAN_NOT_NULL = "2241286";
	public static final String MULTI_VALIDATION_ERROR = "2241287";
	public static final String USERID_NOT_IN_HIERARCHY = "2241288";
	public static final String FROM_AND_TO_MSISDN_CANNOT_BE_SAME = "2241289";
	public static final String INVAID_C2S_TAB_REQ = "2241281";
	public static final String EMPTY_MOBILE_TAB_ReceiverUSERID = "2241282";
	public static final String INVALID_PERIOD = "2241290";
	public static final String INVALID_LOOKUP_TYPE = "2241291";
	public static final String SERVICE_LIST_FOUND = "2241292";
	public static final String SERVICE_LIST_NOT_FOUND = "2241293";

	public static final String INVALID_OPTION_PROVIDED = "2241294";
	public static final String STAFF_LOGIN_ID_MANDATORY = "2241295";
	public static final String STAFF_MOBILE_NUM_MANDATORY = "2241296";
	public static final String INVALID_STAFF_LOGIN_ID = "2241297";
	public static final String INVALID_STAFF_MSISDN = "2241298";
	public static final String INVALID_USER_ID = "2241299";
	public static final String OFFLINERPT_PROCESS_INITIATED = "2241300";
	public static final String INVAID_TAB_REQ = "2241301";
	public static final String EMPTY_CHANNELUSER_MOBILENUM = "2241302";
	public static final String EMPTY_CHANNELUSER_LOGINID = "2241303";
	public static final String DATE_RANGE_ERROR_ARG = "2241304";
	public static final String SAME_REPORT_ALREADY_EXECUTING = "2241305";
	public static final String TOTAL_ALLOWED_RPT_EXEC = "2241306";
	public static final String SEARCH_STAFF_BY = "2241307";

	public static final String INVALID_PAYMENT_DETAILS_GATEWAY = "2241308";
	public static final String INVALID_PAYMENT_GATEWAY_STATUS = "2241309";
	public static final String PAYMENT_FAILURE_GATEWAY = "2241310";
	public static final String TRANSACTION_AUTO_APPROVED_BY_SYSTEM = "2241311";
	public static final String TRANSACTION_SENT_FOR_APPROVAL = "2241312";
	public static final String GATEWAY_STATUS_REQUIRED = "2241313";
	public static final String PAYMENT_ID_REQUIRED = "2241314";
	public static final String ORDER_ID_REQUIRED = "2241315";
	public static final String PAYMENT_SIGNATURE_REQUIRED = "2241316";
	public static final String TRANSFER_ID_REQUIRED = "2241317";
	public static final String INVALID_REPORT_TASKID = "2241318";
	public static final String INVALID_REPORT_ACTION = "2241319";
	public static final String OFFLINE_REPORT_CANCELLED = "2241320";
	public static final String OFFLINE_FILE_DELETE_SUCCESS = "2241321";
	public static final String OFFLINE_FILE_DELETE_FAILED = "2241322";
	public static final String OFFLINE_FILE_DELETE_NOTALLOWED = "2241323";
	public static final String CANNOT_REDIRECT_TO_CHANGE_PASSWORD_SCREEN = "2241324";
	public static final String REDIRECT_TO_CHANGE_PASSWORD_SCREEN = "2241325";
	public static final String FILTER_REQ_RECORD_AVAILABLE = "2241326";
	public static final String NO_DATA_FOUND_TO_LOG = "2241327";
	public static final String ALLOWED_SERACHY_BY = "2241329";
	public static final String AT_LEAST_ONE_REQUIRED = "2241330";
	public static final String MSISDN_OR_USERNAME_WITH_CATEGORY_REQ = "2241331";
	public static final String INVALID_MODE = "2241343";
	public static final String INVALID_USER_LOGINID = "2241332";
	public static final String INVALID_RESEND = "2241344";
	public static final String INVALID_MSISDN_USER_TRANSFER = "2241345";
	public static final String O2CACK_TRANSACTION_NOT_CLOSED_YET = "2241346";
	public static final String PIN_CHANGE_REQUIRED = "2241350";
	public static final String PIN_CHANGE_NOT_REQUIRED = "2241351";
	public static final String NOT_ALLOWED_TO_UPDATE_USER_LANG = "2241352";
	public static final String INVALID_TRF_CATEGORY = "2241353";
	public static final String TRF_O2C_PENDING = "2241356";
	public static final String TRF_FOC_PENDING = "2241357";
	public static final String TRF_RESTRICTED_MSISDN = "2241358";
	public static final String USER_ALREADY_TRANSFERED = "2241360";
	public static final String TRF_CHILD_USR_EXIST = "2241359";
	public static final String TRF_SOS_PENDING = "2241361";
	public static final String TRF_LR_PENDING = "2241362";
	public static final String USER_NOT_UNDER_LOGGEDINCATEGORY = "2241354";
	public static final String USER_NOT_UNDER_SELECT_CATEGORY = "2241355";
	public static final String UPLOADEDFILE_DOESNT_CONTAIN_RECORD = "2241370";
	public static final String USERS_SUSPENDED_SUCCESSFULLY = "2241371";
	public static final String USERS_RESUMED_SUCCESSFULLY = "2241372";
	public static final String INVALID_REQUEST_TYPE = "2241373";
	public static final String NO_USER_HIERARCHY_FOUND_TO_RESUME = "2241374";
	public static final String NO_USER_HIERARCHY_FOUND_TO_SUSPEND = "2241375";
	public static final String USER_NOT_IN_GEO_DOMAIN = "2241376";
	public static final String USER_NOT_IN_DOMAIN = "2241377";
	public static final String SELECT_STATUS_USERS = "2241378";
	public static final String USER_SUSPEND_FAILED = "2241379";
	public static final String USER_RESUME_FAILED = "2241380";
	public static final String IS_REQUIRED = "2241381";
	public static final String NO_C2C_ENQ_ROLES = "2241382";
	public static final String C2C_ENQ_INVALID_USER_TYPE = "2241383";
	public static final String LOGIN_ID_INVALID = "2241384";
	public static final String BULKUSERADD_INVALID_BATCHNO = "2241385";
	public static final String EMPTY_MOBILE_TAB_TRANSFER_CATEGORY = "2241386";
	public static final String NOT_AUTHORIZED_TO_BAR = "2241387";
	public static final String ERROR_USER_TRANSFER_CHNL_SENDER_BAR = "2241388";
	public static final String INVALID_TRANSACTION_ID = "3000500";
	public static final String REVERSED_IN_PAST = "3000501";
	public static final String BARRING_ACTION_NOT_PERFORMED = "2241389";
	public static final String INVALID_FILE_CONTENT = "2241390";
	public static final String FILE_UPLOAD_ERROR = "2241391";
	public static final String NO_DOMAIN_ASSIGNED = "2241392";
	public static final String FAILED_TO_SEND_SMS = "2241393";
	public static final String PARTIAL_BARRED = "2241394";
	public static final String USER_BARRED_SUCCESS = "2241395";
	public static final String USERS_CANNOT_BE_UNBARRED = "2241396";
	public static final String USERS_CANNOT_BE_BARRED = "2241397";
	public static final String PARTIAL_UNBARRED = "2241398";
	public static final String USER_UNBARRED_SUCCESS = "2241399";
	public static final String FILE_EXISTS = "2241400";
	public static final String REPRINT_VOUCHER_LOAD_FAIL = "2241401";
	public static final String MSISDN_NOT_IN_NETWORK = "2241402";
	public static final String MSISDN_NOT_IN_DOMAIN = "2241403";
	public static final String MSISDN_NOT_IN_GEODOMAIN = "2241404";
	public static final String MOBILE_NO_NOT_EXIST = "2241405";
	public static final String SOS_TXN_REVERSAL_NOT_PERMITTED = "2241406";
	public static final String NO_TXNS_FOR_REVERSAL = "2241407";
	public static final String TXN_ALREADY_REVERSED = "2241408";
	public static final String REVERSAL_QUANTITY_NOT_VALID = "2241409";
	public static final String REQ_QUANTITY_MORE_THAN_USER_BALANCE = "2241410";
	public static final String REQ_QUANTITY_MORE_THAN_TXN_VALUE = "2241411";
	public static final String NO_PRODUCT_SELECTED_FOR_REVERSAL = "2241412";
	public static final String REVERSAL_TXN_FAILED = "2241413";
	public static final String TXN_REVERSAL_INIT_SUCCESS = "2241414";
	public static final String TXN_REVERSAL_SUCCESS = "2241415";
	public static final String O2C_REVERAL_BAD_REQ = "3000502";
	public static final String O2C_REVERAL_ALLOWED_DAYS = "3000503";
	public static final String NOT_AUTHORIZED_TO_REVERSE_O2C = "3000504";
	public static final String USER_REJECTED_SUCCESSFULLY = "3000505";
	public static final String USER_2NDAPPROVE_REQUIRED = "3000506";
	public static final String USER_SUCCESSFULLY_ACTIVATED = "3000507";
	public static final String USER_MODIFIED_APPROVAL = "3000508";
	public static final String USER_SUCCESSFULLY_UPDATED = "3000509";

	public static final String INVALID_TAB_REQ = "3000511";
	public static final String INVALID_DOMAIN_OR_EMTPY = "3000512";
	public static final String INVALID_CATEGORY_OR_EMTPY = "3000513";
	public static final String INVALID_GEOGRAPHY_OR_EMTPY = "3000514";
	public static final String NO_APPROVAL_LIST_RECORDS = "3000515";

	public static final String BLANK_CATEGORY = "3000510";
	public static final String MSG_SUCCESSFUL_OF_OPERACTION = "3000516";
	public static final String CANNOT_CHANGE_USER_STATUS = "3000517";
	public static final String CHANGE_USER_SUCCESS = "3000518";
	public static final String SUSPEND_REQUEST = "3000519";

	public static final String TECHNICAL_ERROR = "3000520";
	public static final String EXTERNAL_CODE_SPCL_CHAR_NA = "3000521";

	public static final String CHILD_USER_EXIST = "3001000";
	public static final String SOS_TRANSACTION_PENDING = "3001001";
	public static final String LR_TRANSACTION_PENDING = "3001002";
	public static final String O2C_TRANSACTION_PENDING = "3001003";
	public static final String FOC_TRANSACTION_PENDING = "3001004";
	public static final String RESTRICTED_MSISDN = "3001005";
	public static final String C2C_TRANSACTION_PENDING = "3001006";
	public static final String BATCH_O2C_TRANSACTION_PENDING = "3001007";

	public static final String FILE_NOT_UPLOADED = "3000522";
	public static final String DELIMITER_MISSING = "3000523";
	public static final String FILE_PATH_MISSING = "3000524";
	public static final String CONTENTS_SIZE_MISSING = "3000525";
	public static final String BLANK_LINE_RECORDS = "3000526";
	public static final String FILE_CONTENT_EXCEED = "3000527";
	public static final String INVALID_MSISDN_LIST = "3000528";
	public static final String INVALID_LOGIN_ID_LIST = "3000529";
	public static final String FILE_CANNOT_MOVE = "3000530";
	public static final String DELETE_SUCCESS = "3000531";
	public static final String DELETE_PARTIAL_SUCCESS_MSISDN = "3000532";
	public static final String DELETE_PARTIAL_SUCCESS_LOGIN_ID = "3000533";
	public static final String SUSPEND_SUCCESS = "3000534";
	public static final String SUSPEND_PARTIAL_SUCCESS_MSISDN = "3000535";
	public static final String SUSPEND_PARTIAL_SUCCESS_LOGIN_ID = "3000536";
	public static final String RESUME_SUCCESS = "3000537";
	public static final String RESUME_PARTIAL_SUCCESS_MSISDN = "3000538";
	public static final String RESUME_PARTIAL_SUCCESS_LOGIN_ID = "3000539";
	public static final String CHANGE_STATUS_NOT_PERFORMED = "3000540";
	public static final String UPLOADFILE_INVALIDMSISDNLIST_delete = "3000541";
	public static final String UPLOADFILE_INVALIDLOGINLIST_delete = "3000542";
	public static final String BULK_DELETE_SUCCESSFULL = "3000543";
	public static final String BULK_SUSPEND_SUCCESSFULL = "3000544";
	public static final String UPLOADFILE_INVALIDMSISDNLIST_SUSPEND = "3000546";
	public static final String UPLOADFILE_INVALIDLOGINLIST_SUSPEND = "3000547";
	public static final String BULK_RESUME_SUCCESSFULL = "3000545";
	public static final String CATEGORY_NOT_IN_DOMAIN = "3000548";
	public static final String CATEGORY_DETAILS_NOT_FOUND = "3000549";
	public static final String BARRED_DELETE_REQUEST = "3000550";
	public static final String BARRED_DELETE = "3000551";
	public static final String CHILD_USER_EXIST1 = "3000552";
	public static final String SOS_TRANSACTION_PENDING1 = "3000553";
	public static final String LR_TRANSACTION_PENDING1 = "3000554";
	public static final String O2C_TRANSACTION_PENDING1 = "3000555";
	public static final String FOC_TRANSACTION_PENDING1 = "3000556";
	public static final String USER_CANNOT_BE_BARRED_DELETED = "3000557";
	public static final String USER_IN_CANCELLED_STATUS = "3000559";
	public static final String APPROVAL_NOT_ALLOWED = "3000560";
	public static final String USER_REJECTION_FAILED = "3000561";
	public static final String BULK_CHANGE_STATUS_FAIL = "3000562";
	public static final String BULK_CHANGE_STATUS_PARTIAL_SUCCESS = "3000563";
	public static final String BULK_CHANGE_STATUS_SUCCESS = "3000564";
	public static final String NO_VALID_MSISDN_IN_FILE = "3000565";
	public static final String NO_VALID_LOGIN_IN_FILE = "3000566";
	public static final String FILE_CANNOT_BE_MOVED_BKP = "3000567";
	public static final String USER_APPRV1_ALREADY_COMPLETED = "3000568";
	public static final String USER_APPRV2_ALREADY_COMPLETED = "3000569";
	public static final String INVALID_REQ_QUANTITY = "3000570";

	public static final String BULK_UMOD_DOMAIN = "3000576";
	public static final String BULK_UMOD_DOWNLOADED_BY = "3000577";
	public static final String BULK_UMOD_DOMAIN_NAME = "3000578";
	public static final String BULK_UMOD_CATEGORY_NAME = "3000579";
	public static final String BULK_UMOD_GEOGRAPHY_NAME = "3000580";
	public static final String BULK_UMOD_MANDATORY_MSG = "3000581";
	public static final String BULK_UMOD_USER_ID = "3000582";
	public static final String BULK_UMOD_USER_ID_COMMENT = "3000583";
	public static final String BULK_UMOD_USER_NAME_PREFIX = "3000584";
	public static final String BULK_UMOD_USER_NAME_PREFIX_COMMENT = "3000585";
	public static final String BULK_UMOD_USER_NAME = "3000586";
	public static final String BULK_UMOD_USER_NAME_COMMENT = "3000587";
	public static final String BULK_UMOD_FIRST_NAME = "3000588";
	public static final String BULK_UMOD_FIRST_NAME_COMMENT = "3000589";
	public static final String BULK_UMOD_LAST_NAME = "3000590";
	public static final String BULK_UMOD_LAST_NAME_COMMENT = "3000591";
	public static final String BULK_UMOD_WEB_LOGIN_ID = "3000592";
	public static final String BULK_UMOD_WEB_LOGIN_ID_COMMENT = "3000593";
	public static final String BULK_UMOD_WEB_PASSWORD = "3000594";
	public static final String BULK_UMOD_MOBILE_NO = "3000595";
	public static final String BULK_UMOD_MOBILE_NO_COMMENT = "3000596";
	public static final String BULK_UMOD_PIN = "3000597";
	public static final String BULK_UMOD_GEO_DOMAIN_CODE = "3000598";
	public static final String BULK_UMOD_GEO_DOMAIN_CODE_COMMENT = "3000599";
	public static final String BULK_UMOD_GRP_ROLE_CODE = "3000600";
	public static final String BULK_UMOD_GRP_ROLE_CODE_COMMENT = "3000601";
	public static final String BULK_UMOD_ROLE_CODE = "3000602";
	public static final String BULK_UMOD_ROLE_CODE_COMMENT = "3000603";
	public static final String BULK_UMOD_SERVICES = "3000604";
	public static final String BULK_UMOD_SERVICES_COMMENT = "3000605";
	public static final String BULK_UMOD_SHORT_NAME = "3000606";
	public static final String BULK_UMOD_SUBS_CODE = "3000607";
	public static final String BULK_UMOD_EXTERNAL_CODE = "3000608";
	public static final String BULK_UMOD_EXTERNAL_CODE_COMMENT = "3000609";
	public static final String BULK_UMOD_IN_SUSPEND = "3000610";
	public static final String BULK_UMOD_IN_SUSPEND_COMMENT = "3000611";
	public static final String BULK_UMOD_OUT_SUSPEND = "3000612";
	public static final String BULK_UMOD_OUT_SUSPEND_COMMENT = "3000613";
	public static final String BULK_UMOD_CONTACT_PERSON = "3000614";
	public static final String BULK_UMOD_CONTACT_NO = "3000615";
	public static final String BULK_UMOD_RSA_ID = "3000616";
	public static final String BULK_UMOD_RSA_ID_COMMENT = "3000617";
	public static final String BULK_UMOD_DESIGNATION = "3000618";
	public static final String BULK_UMOD_ADDRESS1 = "3000619";
	public static final String BULK_UMOD_ADDRESS2 = "3000620";
	public static final String BULK_UMOD_CITY = "3000621";
	public static final String BULK_UMOD_STATE = "3000622";
	public static final String BULK_UMOD_COUNTRY = "3000623";
	public static final String BULK_UMOD_COMPANY = "3000624";
	public static final String BULK_UMOD_FAX = "3000625";
	public static final String BULK_UMOD_LANGUAGE = "3000626";
	public static final String BULK_UMOD_EMAIL = "3000627";
	public static final String BULK_UMOD_OUTLET_CODE = "3000628";
	public static final String BULK_UMOD_SUB_OUTLET_CODE = "3000629";
	public static final String BULK_UMOD_ALLOW_LOW_BAL_ALERT = "3000630";
	public static final String BULK_UMOD_TRF_ROLE_CODE = "3000631";
	public static final String BULK_UMOD_TRF_ROLE_CODE_COMMENT = "3000632";
	public static final String BULK_UMOD_RSA_AUTH = "3000633";
	public static final String BULK_UMOD_RSA_AUTH_COMMENT = "3000634";
	public static final String BULK_UMOD_AUTH_TYPE_ALLOWED = "3000635";
	public static final String BULK_UMOD_AUTH_TYPE_COMMENT = "3000636";
	public static final String BULK_UMOD_LONGITUDE = "3000637";
	public static final String BULK_UMOD_LATITUDE = "3000638";
	public static final String BULK_UMOD_DOCUMENT_TYPE = "3000639";
	public static final String BULK_UMOD_DOCUMENT_NO = "3000640";
	public static final String BULK_UMOD_PAYMENT_TYPE = "3000641";
	public static final String BULK_UMOD_LMS_PROFILE = "3000642";
	public static final String BULK_UMOD_COMM_PROFILE = "3000643";
	public static final String BULK_UMOD_COMM_PROFILE_COMMENT = "3000644";
	public static final String BULK_UMOD_TRF_PROFILE = "3000645";
	public static final String BULK_UMOD_TRF_PROFILE_COMMENT = "3000646";
	public static final String BULK_UMOD_GRADE = "3000647";
	public static final String BULK_UMOD_GRADE_COMMENT = "3000648";
	public static final String BULK_UMOD_VOUCHER_TYPE = "3000649";
	public static final String BULK_UMOD_VOUCHER_TYPE_COMMENT = "3000650";
	public static final String BULK_UMOD_MASTER_DATA_DOMAIN = "3000651";
	public static final String BLANK_BATCH_NAME = "3000652";
	public static final String UNDERSCORE_NOT_ALLOWED = "3000653";
	public static final String BLANK_DATA_NOT_ALLOWED = "3000654";
	public static final String NO_OPT_USER_FOUND = "3000655";
	public static final String DENOM_LIST_FOUND = "3000656";
	public static final String BATCH_ID_FOUND = "3000657";
	public static final String BATCH_NOT_FOUND = "3000658";
	public static final String BATCH_CREATION_SUCCESS = "3000659";
	public static final String BATCH_CREATION_FAIL = "3000660";
	public static final String DELETION_FAIL = "3000661";
	public static final String DELETION_SUCCESS = "3000662";

	public static final String SUSPEND_PARTIAL_SUCCESS_MSISDN_MESSAGE = "3000571";
	public static final String SUSPEND_PARTIAL_SUCCESS_LOGIN_ID_MESSAGE = "3000572";
	public static final String RESUME_PARTIAL_SUCCESS_MSISDN_MESSAGE = "3000573";
	public static final String RESUME_PARTIAL_SUCCESS_LOGIN_ID_MESSAGE = "3000574";
	public static final String EVD_SENDER_MESSAGE_FOR_SUCCESS = "3000575";

	public static final String DIV_SUCCESS = "3000663";
	public static final String DIV_FAIL = "3000664";
	public static final String DIV_MODIFY_SUCCESS = "3000665";
	public static final String DIV_MODIFY_FAIL = "3000666";
	public static final String DIV_NAME_EXISTS = "3000667";
	public static final String DIV_CODE_EXISTS = "3000668";
	public static final String DIV_ADD_SUCCESS = "3000669";
	public static final String DIV_ADD_FAIL = "3000670";
	public static final String DIV_DEP_LIST_FOUND = "3000671";
	public static final String DIV_DEP_LIST_NOT_FOUND = "3000672";
	public static final String DIV_DELETE_SUCCESS = "3000673";
	public static final String DIV_DELETE_FAIL = "3000674";
	public static final String DEP_FOR_DEV = "3000675";
	public static final String USER_SUCCESSFULLY_ADDED = "3000676";
	public static final String DEREGISTRATION_SUBSCRIBER_BULK = "3000677";
	public static final String DEREGISTRATION_SUBSCRIBER_BULK_PARTIAL_MESSAGE = "3000678";
	public static final String DELIMETER_MISSING = "3000679";
	public static final String INVALID_FILE = "3000680";
	public static final String FILE_UPLOAD_ERROR_ON_SERVER = "3000681";
	public static final String CONTENT_SIZE_MISSING = "3000682";
	public static final String BLANK_LINE = "3000683";
	public static final String SIZE_LIMIT_EXCEEDED = "3000684";
	public static final String INVALID_MOBILE_NUMBER = "3000685";
	public static final String FILE_CANNOT_BE_MOVED = "3000686";

	public static final String MASTER_SHEET_HEADING = "3000687";
	public static final String OPT_BATCH_ALREADY_RUNNING = "3000688";

	public static final String CHANNEL_USER_BLANK = "3000689";
	public static final String CHANNEL_USER_NOT_FOUND = "3000690";
	public static final String CHANNEL_USER_OWNER_MANDATORY = "3000691";
	public static final String CHANNEL_USER_PARENT_MANDATORY = "3000692";

	public static final String GRADE_MANAGEMENT_DOMAIN_LIST_NOT_FOUND = "3000693";
	public static final String GRADE_MANAGEMENT_CATEGORY_LIST_NOT_FOUND = "3000694";
	public static final String GRADE_MANAGEMENT_DOMAIN_CATEGORY_LIST_FOUND = "3000695";
	public static final String GRADE_LIST_NOT_FOUND = "3000696";
	public static final String GRADE_LIST_FOUND = "3000697";
	public static final String MULTIPLE_PRIMARY_MOBILENUM = "3000698";
	public static final String USER_ADDED_APPR_PENDING = "3000699";
	public static final String ATLEAST_ONE_PRIMARYMSISDN = "3000700";
	public static final String LIST_NOT_FOUND = "3000701";
	public static final String LIST_FOUND = "3000702";
	// added for Robi Retailer Loan CR
	// Added for user Loan
	public static final String USER_LOAN_TRASFER_SUCCESS = "1007097";
	public static final String USER_LOAN_TRASFER_FAIL = "1007098";
	public static final String USER_LOAN_CONFIGURATION_ERROR_SNDR = "1007096";
	public static final String USER_LOAN_PROCESS_SUCCESS = "1007099";
	public static final String USER_LOAN_PROCESS_FAIL = "1007095";
	public static final String LOAN_SETTLEMENT_PENDING = "1007094";
	public static final String ERROR_LOAN_SETTLMENT = "1007093";
	public static final String LOAN_WITHDRAW_SENDER = "1007092";
	public static final String ERROR_ALREADY_OPTIN = "1007099";
	public static final String ERROR_ALREADY_OPTOUT = "1008099";
	public static final String ERROR_OPTOUT_NOT_ALLOWED_DUETO_PENDING_LOAN = "1008098";
	public static final String LOAN_ERROR_EXCEPTION = "1008097";
	public static final String LOAN_OPTIN_SUCCESS = "1008096";
	public static final String LOAN_OPTOUT_SUCCESS = "1008095";

	public static final String NO_LOAN_INFO = "1008001";

	public static final String PARENT_MSISDN_WRONG = "1008002";
	public static final String INCORRECT_USER_BALANCE = "1008003";
	public static final String UNBAR_FAIL = "1008004";
	public static final String UNBAR_SUCCESS = "1008005";
	public static final String INVALID_PARENT_MSISDN = "1008094";
	public static final String INVALID_BALANCE = "1008093";
	public static final String INVALID_CATEGORY_FOR_OPTIN = "1008092";

	public static final String AUTHENDICATION_ERROR = "3000703";
	public static final String AUTHENDICATION_ERROR_ALLOWED_IP = "30007031";
	public static final String PASSWORD_BLOCKED = "3000704";
	public static final String USER_LOGIN_WENT_WRONG = "3000705";
	public static final String TEMPLATE_DOWNLOAD_SUCCESS = "3000706";
	public static final String TEMPLATE_DOWNLOAD_FAIL = "3000707";
	public static final String FILE_PROCESS_SUCCESS = "3000708";
	public static final String FILE_PROCESS_PARTIAL = "3000709";
	public static final String VOMS_SERIAL_NO_ALREADY_SOLD_C2S = "3000710";

	public static final String EXTSYS_REQ_WEBPASSWORD_INVALID_MODIFIED = "3000711";
	public static final String MUTLIPLE_OTP_SEND_ATTEMPTS_BLOCK = "3000712";
	public static final String VOMS_SERIAL_NO_ALREADY_SOLD_C2S_APPRVL = "3000713";
	public static final String OPERATOR_SUCCESSFULY_ADDED = "3000714";
	public static final String USER_SUCCESSFULY_ADDED = "3000715";
	public static final String MSISDN_NOT_FOUND = "3000716";
	public static final String STAFFCAT_GRTRTHAN_PARENTCAT = "3000717";
	public static final String STAFFCAT_PARENTCAT_MANDATORY = "3000718";
	public static final String CHANNEL_USER_SHOULD_BE_CAT = "3000719";
	public static final String PARENT_USER_SHOULD_BE_CAT = "3000720";
	public static final String GENERIC_SERVER_ERROR = "3000721";
	public static final String CHANNEL_USER_NOT_UNDER_PARENT = "3000722";
	public static final String ONLY_NUMBER_IN_MSISDN = "3000723";
	public static final String STAFF_USER_LOGINID_MANDATORY = "3000724";
	public static final String ASSIGN_STAFF_ROLES_MANDATORY = "3000725";
	public static final String SUBSCRIBERCODE_LENGTH_EXCEEDING = "3000726";
	public static final String PROPER_ERROR_NOT_SET_KEYISNULL = "3000727";
	public static final String PRODUCT_INFO_MANDATORY_SUBCU = "3000728";
	public static final String SERVICES_MANDATORY = "3000729";
	public static final String EMPCODE_MANDATORY = "3000730";
	public static final String FROMTIME_GRT_TOTIME = "3000731";
	public static final String CATEGORY_NOT_FOUND = "3000732";
	public static final String FILE_CANNOT_UPLOAD = "3000737";
	public static final String BATCHREV_VAL_UPLOAD_PATH = "3000733";
	public static final String BATCHREV_UPLOAD_FILE_VALID = "3000734";
	public static final String BATCHREV_NO_RECORDS_FILE_PROCESS = "3000735";
	public static final String BATCHREV_MAX_FILE_LIMIT_REACH = "3000736";
	public static final String UPLOAD_FILE_DIR_CREATE = "3000738";
	public static final String NO_FILE_EXIST = "3000739";
	public static final String DIR_NOT_CREATED = "3000740";
	public static final String SUBSCRIBER_CODE_MAX_LEN_EXCEED = "3000741";

	public static final String UPDATE_INSTANCE_LIST_NOT_FOUND = "3000742";
	public static final String UPDATE_CACHE_LIST_NOT_FOUND = "3000743";
	public static final String UPDATE_INSTANCE_CACHE_LIST_FOUND = "3000744";
	public static final String UPDATE_INSTANCE_CACHE_LIST_NOT_FOUND = "3000745";
	public static final String EMPTY_INSTANCE_CACHE_LIST = "3000746";
	public static final String EMPTY_CACHE_LIST = "3000747";
	public static final String SINGLE_CACHE_SUCCESS = "3000748";
	public static final String SINGLE_CACHE_FAIL = "3000749";
	public static final String UPDATE_CACHE_SUCCESS = "3000750";
	public static final String UPDATE_CACHE_FAIL = "3000751";
	public static final String UPDATE_CACHE_PARTIAL = "3000752";
	public static final String MSISDN_NOT_ALLOWED_FOR_CAT = "3000753";
	public static final String DIVISION_CODE_MANDATORY = "3000754";
	public static final String INVALID_DIVISION_CODE = "3000755";
	public static final String DEPARTMENT_CODE_MANDATORY = "3000756";
	public static final String INVALID_DEPARTMENT_CODE = "3000757";
	public static final String FILE_CONTENT_INVALID = "3000758";
	public static final String FILE_SIZE_EXCEEED_LIMIT = "3000759";
	public static final String FILE_ALREADY_EXISTS = "3000760";
	public static final String APPOINTMENTDATE_LESS_CURRDATE = "3000761";
	public static final String INVALID_APPOINTMENT_DATE = "3000762";
	public static final String MOBILE_NUMB_REQUIRED = "3000763";
	public static final String ASSIGN_MSISDN_MANDATAORY = "3000764";
	public static final String BATCHC2S_REVERSAL_PARTIAL_SUCCESS = "3000765";
	public static final String BATCHNAME_EXCEED_LIMIT = "3000766";
	public static final String INVALID_DOMAIN_CODE = "3000767";
	public static final String DOMAIN_LIST_NOT_FOUND = "3000768";
	public static final String ASSIGN_GEOGRAPHY_MANDATORY = "3000769";
	public static final String GATE_SUCCESS = "3000770";
	public static final String GATE_FAIL = "3000771";
	public static final String GATE_UP_SUCCESS = "3000772";
	public static final String GATE_UP_FAIL = "3000773";
	public static final String GATE_DEL_SUCCESS = "3000774";
	public static final String GATE_DEL_FAIL = "3000775";
	public static final String GATE_MSG_SUCCESS = "3000776";
	public static final String GATE_MSG_FAIL = "3000777";
	public static final String GATE_MSG_DEL_SUCCESS = "3000778";
	public static final String GATE__MSG_DEL_FAIL = "3000779";
	public static final String GATE__MSG_DEL_FAIL_MAP = "3000780";
	public static final String GATE__MSG_DEL_FAIL_RMAP = "3000781";
	public static final String GATE__MSG_DEL_FAIL_ROMAP = "3000782";
	public static final String MSG_SUCCESS = "3000783";
	public static final String MSG_FAIL = "3000784";

	// DOMAIN MANAGEMENT
	public static final String DOMAIN_NAME_ALREADY_EXISTS = "3000785";
	public static final String INVALID_NO_OF_CATEGORIES = "3000786";
	public static final String MODIFY_DOMAIN_FAILED = "3000787";
	public static final String CATEGORY_EXIST = "3000788";
	public static final String DOMAIN_DELETE_OPERATION_FAILED = "3000789";
	public static final String DOMAIN_STATUS_UPDATE_OPERATION_FAILED = "3000790";
	public static final String CATEGORY_CODE_EXIST = "3000791";
	public static final String CATEGORY_NAME_EXIST = "3000792";
	public static final String USER_PREFIX_EXISTS = "3000793";
	public static final String ROLES_NOT_ADDED_SUCCESSFULLY = "3000794";
	public static final String DOMAIN_NOT_ADDED_SUCCESSFULLY = "3000795";
	public static final String DOMAIN_ADDED_SUCCESSFULLY = "3000808";
	public static final String DOMAIN_DELETED_SUCCESSFULLY = "3000807";
	public static final String DOMAIN_UPDATED_SUCCESSFULLY = "3000809";

	public static final String USER_NOT_CHILD = "1100118";
	public static final String CHANNEL_USER_ALREADY_UNBARRED = "1100119";
	public static final String USER_BAR_SUCCESS = "1100120";
	public static final String USER_UNBAR_SUCCESS = "1100121";
	public static final String USER_ALREADY_BARRED = "1100122";
	public static final String USER_BAR_FAILED = "1100123";
	public static final String USER_UNBAR_FAILED = "1100124";
	public static final String CHILD_PIN_RESET_SUCCESS = "1100125";
	public static final String TEMP_PIN_EXPIRED = "1100126";

	public static final String MESSAGE_GATEWAY_LIST_NOT_FOUND = "1100127";
	public static final String MESSAGE_GATEWAY_SUB_TYPE_LIST_NOT_FOUND = "1100128";
	public static final String MESSAGE_GATEWAY_LIST_AND_SUB_TYPE_LIST_FOUND = "1100129";
	public static final String MESSAGE_GATEWAY_ALREADY_EXIST_CODE = "1100130";
	public static final String MESSAGE_GATEWAY_ALREADY_EXIST_NAME = "1100131";
	public static final String DISPLAY_MESSAGE_GATEWAY_DETAIL_SUCCESS = "1100132";
	public static final String DISPLAY_MESSAGE_GATEWAY_DETAIL_FAILURE = "1100133";
	public static final String MESSAGE_GATEWAY_ADD_FAILURE = "1100134";
	public static final String MESSAGE_GATEWAY_ADD_SUCCESS = "1100135";
	public static final String MESSAGE_GATEWAY_UPDATE_SUCCESS = "1100136";
	public static final String MESSAGE_GATEWAY_UPDATE_FAILURE = "1100137";
	public static final String MESSAGE_GATEWAY_MAPPING_EXIST = "1100138";
	public static final String BATCHC2S_REVERSAL_FULL_SUCCESS = "1100139";
	public static final String OTP_CANNOT_SENT = "1100140";
	public static final String FILE_CONTAINS_BLANK_LINE = "1100141";
	public static final String BATCHC2S_REVERSAL_ALL_FAILURE = "1100142";
	public static final String BATCH_NAME_SPECIALCHAR_NOT_ALLOWED = "1100143";

	public static final String GROUP_ROLES_LIST_NOT_FOUND = "1100144";
	public static final String GROUP_ROLES_LIST_FOUND = "1100145";
	public static final String ROLES_LIST_BY_GROUP_ROLE_NOT_FOUND = "1100146";
	public static final String ROLES_LIST_BY_GROUP_ROLE_FOUND = "1100147";
	public static final String GROUP_ROLE_ADDED = "1100148";
	public static final String GROUP_ROLE_NOT_ADDED = "1100149";
	public static final String ROLE_CODE_ALREADY_EXISTS = "1100150";
	public static final String ROLES_LIST_BY_GROUP_ROLE_CODE_NOT_FOUND = "1100151";
	public static final String ROLES_LIST_BY_GROUP_ROLE_CODE_FOUND = "1100152";
	public static final String ROLES_LIST_BY_GROUP_ROLE_CODE_EMPTY = "1100153";
	public static final String GROUP_ROLE_UPDATED = "1100154";
	public static final String GROUP_ROLE_NOT_UPDATED = "1100155";
	public static final String GROUP_ROLE_DELETED = "1100156";
	public static final String GROUP_ROLE_NOT_DELETED = "1100157";
	public static final String GROUP_ROLE_NOT_DELETED_USER_ASSOCIATED = "1100158";
	public static final String GROUP_ROLE_NOT_DELETED_DEFAULT = "1100159";
	public static final String DEP_SUCCESS = "1100160";
	public static final String DEP_FAIL = "1100161";
	public static final String DEP_DELETE_SUCCESS = "1100162";
	public static final String DEP_DELETE_FAIL = "1100163";
	public static final String DEP_DELETE_FAIL1 = "1100164";
	public static final String DEP_NAME_EXISTS = "1100165";
	public static final String DEP_CODE_EXISTS = "1100166";
	public static final String DEP_ADD_SUCCESS = "1100167";
	public static final String DEP_ADD_FAIL = "1100168";
	public static final String DEP_MODIFY_SUCCESS = "1100169";
	public static final String DEP_MODIFY_FAIL = "1100170";
	public static final String PREFERENCE_UPDATE_FAIL = "1100171";
	public static final String PREFERENCE_UPDATE_SUCCESS = "1100172";
	public static final String SHORT_NAME_EXCEEED_15CHARS = "1100173";
	public static final String OCCURENCE_EMTPY = "1100174";
	public static final String USER_APPROVAL = "1100176";
	public static final String SERVICEKEYWORD_ALREADY_EXIST = "1100178";
	public static final String NO_USERS_EXIST_LEVEL_APPROVAL = "1100179";
	public static final String LOGINID_SAME_LEVEL = "1100180";
	public static final String LOGINID_SAME_GEO_DOMAIN = "1100181";
	public static final String NO_USER_EXIST_LOGINID = "1100182";
	public static final String NO_USER_EXIST_CATEGORY = "1100183";
	public static final String NO_OPERATOR_USER_FOUND = "1100184";

	public static final String SERVICEKEYWORD_ADD_SUCCESS = "1100185";
	public static final String SERVICEKEYWORD_ADD_UNSUCCESS = "1100186";

	public static final String MSISDN_NETWORK_NOT_SUPPORTED = "1100187";
	public static final String USER_MSISDN_NOT_EXISTS = "1100188";
	public static final String NO_USER_DATA_FOUND = "1100189";
	public static final String NOT_AUTHORIZED = "1100190";
	public static final String SAME_LOGIN_ID = "1100191";
	public static final String MSISDN_DOESNT_EXIST = "1100192";
	public static final String USER_DETAILS_NOT_FOUND_LOGINID = "1100193";
	public static final String USER_DETAILS_NOT_FOUND_MSISDN = "1100194";
	public static final String USER_NOT_YET_APPROVED = "1100195";
	public static final String LOGINID_NETWORK_NOT_SUPPORTED = "1100196";
	public static final String WEB_INTERFACE_NOT_ALLOWED = "1100197";
	public static final String USER_DETAILS_FOUND = "1100198";
	public static final String USER_DETAILS_NOT_FOUND = "1100199";

	public static final String SERVICEKEYWORD_MODIFY_SUCCESS = "1100200";
	public static final String SERVICEKEYWORD_MODIFY_UNSUCCESS = "1100201";
	public static final String SERVICEKEYWORD_DELETE_SUCCESS = "1100202";
	public static final String SERVICEKEYWORD_DELETE_UNSUCCESS = "1100203";
	public static final String OPERATOR_SUCCESSFULY_MODIFIED = "1100204";

	public static final String PREFERENCE_SUCCESS = "1100205";
	public static final String PREFERENCE_FAIL = "1100206";
	public static final String USER_ALREADY_PRESENT = "1100207";

	// CATEGORY MANAGEMENT
	public static final String USER_ALREADY_EXISTS = "3000796";
	public static final String TRANSFER_PROFILE_ALREADY_EXISTS = "3000797";
	public static final String COMMISION_PROFILE_ALREADY_EXISTS = "3000798";
	public static final String TRANSFER_RULE_ALREADY_EXISTS = "3000799";
	public static final String CHILD_CATEGORY_ALREADY_EXISTS = "3000800";
	public static final String CATEGORY_DELETE_OPERATION_FAILED = "3000801";
	public static final String CATEGORY_ADD_OPERATION_FAILED = "3000802";
	public static final String CATEGORY_MODIFY_OPERATION_FAILED = "3000803";
	public static final String CATEGORY_ADDED_SUCCESSFULLY = "3000805";
	public static final String CATEGORY_DELETED_SUCCESSFULLY = "3000804";
	public static final String CATEGORY_UPDATED_SUCCESSFULLY = "3000806";
	public static final String DOMAINS_UPDATECATEGORY_MESSAGE_WARNING = "3000810";

	public static final String DOMAIN_NAME_EMPTY = "1100208";
	public static final String AGENT_ADDED_SUCCESSFULLY = "1100209";
	public static final String AGENT_ADDED_UNSUCCESSFULL = "1100210";
	public static final String FIELD_MANDATORY = "1100211";
	public static final String FIELD_MIN_MAX_LENGTH = "1100212";
	public static final String FIELD_INVALID_DATA = "1100213";
	public static final String ONLY_ALPHABETS_ALLOWED = "1100214";
	public static final String ONLY_ALPHNUMERIC_ALLOWED = "1100215";
	public static final String ONLY_NUMERIC_ALLOWED = "1100216";
	public static final String ONLY_ALLOWED_VALUES = "1100217";
	public static final String C2C_BULK_ONLY_ONE_ALLOWED = "1100218";
	public static final String ADD_TRF_RULE_SUCCESS = "1100219";
	public static final String ADD_TRF_RULE_UNSUCCESS = "1100220";
	public static final String UPDATE_TRF_RULE_SUCCESS = "1100221";
	public static final String UPDATE_TRF_RULE_UNSUCCESS = "1100222";
	public static final String DELETE_TRF_RULE_SUCCESS = "1100223";
	public static final String DELETE_TRF_RULE_UNSUCCESS = "1100224";
	public static final String TO_CATEGORY_SUCCESS = "1100225";
	public static final String TO_CATEGORY_FAIL = "1100226";
	public static final String TRF_RULE_LIST_SUCCESS = "1100227";
	public static final String TRF_RULE_LIST_FAIL = "1100228";
	public static final String CATEGORY_DOMAIN_LIST_SUCCESS = "1100229";
	public static final String CATEGORY_DOMAIN_LIST_FAIL = "1100230";
	public static final String NO_PRODUCT_ASSOCIATED_WITH_TRF_RULE = "1100231";
	public static final String ALL_RULE_ADDED = "1100232";
	public static final String SERVICE_CLASS_LIST_SUCCESS = "1100233";
	public static final String SERVICE_CLASS_LIST_FAIL = "1100234";
	public static final String CTRL_PREFERENCE_UPDATE_FAIL = "1100235";
	public static final String VOUCHER_SUCCESS = "1100236";
	public static final String VOUCHER_FAIL = "1100237";
	public static final String VOUCHER_DATE = "1100238";
	public static final String PIN_SUCCESS = "1100239";
	public static final String PIN_FAIL = "1100240";
	public static final String NO_PRODUCT_MAPPED_TRF_RULES = "1100241";
	public static final String USER_ASSOCIATED = "1100242";
	public static final String MULTI_USER_ASSOCIATED = "1100243";

	public static final String BATCH_NAME_UPLOAD_FILE_EXISTS = "1100244";
	public static final String C2S_SUCCESS = "1100245";
	public static final String C2S_FAIL = "1100246";

	public static final String LOAD_CHANNEL_TRANSFERRULE_LIST_SUCCESS = "1100247";
	public static final String LOAD_CHANNEL_TRANSFERRULE_LIST_FAIL = "1100248";
	public static final String DELETE_CHANNEL_TRANSFERRULE_SUCCESS = "1100249";
	public static final String DELETE_CHANNEL_TRANSFERRULE_FAIL = "1100250";
	public static final String CHANNEL_TRANSFERRULE_DROP_DOWN_FAIL = "1100251";

	public static final String CHANNEL_TRANSFERRULE_DROP_DOWN_SUCCESS = "1100252";
	public static final String UPDATE_CHANNEL_TRANSFERRULE_SUCCESSFULLY = "1100253";
	public static final String UPDATE_CHANNEL_TRANSFERRULE_FAIL = "1100254";
	public static final String ADD_CHANNEL_TRANSFERRULE_SUCCESSFULLY = "1100255";
	public static final String ADD_CHANNEL_TRANSFERRULE_FAIL = "1100256";
	public static final String LOAD_CHANNEL_CATEGORY_LIST_FAIL = "1100257";
	public static final String LOAD_CHANNEL_PRODUCT_LIST_FAIL = "1100258";

	public static final String VIEW_CHANNEL_TRANSFERRULE_SUCCESS = "1100259";
	public static final String VIEW_CHANNEL_TRANSFERRULE_FAIL = "1100260";

	public static final String EXIST_UNDER_TO_CATEGORY_TRANSFER_RULE_CANNOT_BE_DELETE = "1100261";
	public static final String ADD_CHANNEL_TRANSFERRULE_ALREADY_EXIST = "1100262";
	public static final String LOAD_PROMOTIONAL_LEVEL_SUCCESSFULLY = "1100263";
	public static final String LOAD_PROMOTIONAL_LEVEL_FAIL = "1100264";
	public static final String LOAD_PROMOTIONAL_LEVEL_IS_EMPTY = "1100265";
	public static final String BATCH_POMOTIONAL_LELVEL_DROPDOWN_LOADED_SUCCESSFULLY = "1100266";
	public static final String BATCH_POMOTIONAL_LELVEL_DROPDOWN_LOAD_FAIL = "1100267";
	public static final String NO_DOMAIN_LIST = "1100268";
	public static final String NO_CATEGORY_LIST = "1100269";
	public static final String NO_GEOTYPE = "1100270";
	public static final String NO_GEODOMAIN = "1100271";
	public static final String NO_CELLGROUP_LIST = "1100272";
	public static final String NO_SERVICE_GROUP_LIST = "1100273";
	public static final String BULK_PROCESS_FAILED_ALLRECORDS = "1100274";
	public static final String BULK_OPT_USER_INIT_NO_REC = "1100275";
	public static final String BULK_OPT_USER_INIT_MAX_REC_REACHED = "1100276";
	public static final String TRANSFER_ADD_SUCCESS = "1100277";
	public static final String TRANSFER_ADD_FAIL = "1100278";
	public static final String TRANSFER_RULE_EXIXTS = "1100279";
	public static final String TRANSFER_MODIFY_SUCCESS = "1100280";
	public static final String TRANSFER_MODIFY_FAIL = "1100281";
	public static final String TRANSFER_PROFILE_NOT_AVALIABLE = "1100282";
	public static final String TRANSFER_DELETE_FAIL="1100283";
	 public static final String TRANSFER_DELETE_SUCCESS="1100284";
	public static final String C2S_BULK_SCHEDULE_FAILED = "1100285";
	public static final String TRANSFER_SUB_TYPE_REQUIRED="1100286";
	public static final String ALLOWED_SOURCES_MANDATORY="1100287";
	public static final String NO_CATEGORIES_FOUND="1100288";
	public static final String DOMAIN_CODE_MANDATORY ="1100289";
	public static final String CATEGORY_NAME_MANDATORY= "1100290";
	public static final String CATEGORY_CODE_ALREADYEXIST= "1100291";
	public static final String DUPLICATE_PORT= "1100292";
	public static final String GRPHDOMAIN_SELECT_PARENT ="1100293";
	public static final String PARENT_TYPE_LIST_EMPTY="1100294";
	public static final String GEO_DOMAIN_NO_DATA="1100295";
	public static final String GEO_DOMAIN_MULTIPLE_RECORD="1100296";
	public static final String NO_MSISDN_EXISTS="1100297";
	public static final String NO_TRANSFER="1100298";
	public static final String INVALID_PROM_TRANSFER_LEVEL="1100299";
	public static final String NO_DATA_FOUND_CRITERIA="1100300";
	public static final String NO_USER_EXIST_DOMCAT="1100301";
	
	public static final String NO_PROMOTIONALLEVEL ="1100302";
	 public static final String NO_SUBSCRIBER="1100303";
	 public static final String NO_SERVICE_TYPE="1100304";
	 public static final String NO_CARD_GROUP_SET="1100305";
	 public static final String NO_SUBSERVICE="1100306";
	 public static final String LOAD_DOWNLOAD_FILE_SUCCESSFULLY="1100307";
	 public static final String LOAD_DOWNLOAD_FILE_FAIL="1100308";
	 public static final String NO_GRADE="1100309";
	 public static final String NO_XLSFILE="1100310";
	 public static final String MAX_LIMIT_OF_RECS_REACHED="1100311";
	 public static final String UPLOAD_AND_PROCESS_FILE_SUCCESS="1100312";
	 public static final String UPLOAD_AND_PROCESS_FILE_FAIL="1100313";
	 public static final String FILE_SIZE_ZERO="1100314";
	 public static final String NOT_REQUIRED_CONTENT="1100315";
	 public static final String NO_FILE="1100316";
	 public static final String PROMOTIONAL_TRANSFER_RULE_DATA_FILE="1100317";
	 
	 public static final String SUBSCRIBER_STATUS_DESCRIPTION="1100318";
	 public static final String SUBSCRIBER_TYPE_CODE="1100319";
	 public static final String SUBSCRIBER_STATUS="1100320";
	 public static final String CARD_GROUP_DESCRIPTION="1100321";
	 public static final String SUBSCRIBER_TYPE_NAME="1100322";
	 public static final String SERVICE_CLASS_ID="1100323";
	 public static final String SERVICE_CLASS_NAME="1100324";
	 public static final String SERVICE_TYPE_CODE="1100325";
	 
	 
	 public static final String SERVICE_TYPE_NAME="1100326";
	 public static final String SUB_SERVICE_CODE="1100327";
	 public static final String SUB_SERVICE_NAME="1100328";
	 public static final String CARD_GROUP_SET_ID="1100329";
	 public static final String CARD_GROUP_NAME="1100330";
	 public static final String GRADE_DESCRIPTION="1100331";
	 public static final String SENDER_GRADE_CODE="1100332";
	 public static final String SENDER_GRADE_NAME="1100333";
	 
	 public static final String SENDER_CATEGORY_DESCRIPTION="1100334";
	 public static final String SENDER_CATEGORY_CODE="1100335";
	 public static final String SENDER_CATEGORY_NAME="1100336";
	 public static final String SENDER_GEOGRAPHICAL_DOMAIN_DESCRIPTION="1100337";
	 public static final String SENDER_GEOGRAPHICAL_DOMAIN_CODE="1100338";
	 public static final String SENDER_GEOGRAPHICAL_DOMAIN_NAME="1100339";
	 public static final String CELL_GROUP_DESCRIPTION="1100340";
	 public static final String CELL_GROUP_CODE="1100341";
	 public static final String CELL_GROUP_NAME="1100342";
	 public static final String IS_BLANK="1100343";
	 public static final String SHOULD_BE_GREATER_THEN_CURRENT_DATE="1100344";
	 public static final String INVALID_START_DATE_AND_TIME="1100345";
	 public static final String INVALID_END_DATE_AND_TIME="1100346";
	 public static final String APPLICABLE_FROM_DATE_AND_TIME_MUST_BE_LESS_THAN_APPLICABLE_TILL_DATE_AND_TIME="1100347";
	 public static final String INVALID_RECEIVER_SUBSCRIBER_TYPE="1100348";
	 public static final String INVALID_RECEIVER_SERVICE_CLASS_ID="1100349";
	 public static final String INVALID_SERVICE_TYPE="1100350";
	 public static final String INVALID_SUB_SERVICE_CODE="1100351";
	 public static final String SELECTOR_VALUE_IS_NOT_VALID_FOR_SUBSCRIBER_TYPE="1100352";
	 public static final String INVALID_CARD_GROUP_SET_ID="1100353";
	 public static final String INVALID_TRANSFER_RULE="1100354";
	 public static final String INVALID_SUBSCRIBER_STATUS="1100355";
	 public static final String INVALID_SERVICE_PROVIDER_GROUP="1100356";
	 public static final String ALL_FIELDS_MARKED_WITH_ARE_MANDATORY="1100357";
	 public static final String SENDER_MOBILE_NUMBER="1100358";
	 public static final String RECEIVER_SUBSCRIBER_TYPE="1100359";
	 public static final String RECEIVER_SERVICE_CLASS_ID="1100360";
	 public static final String SERVICE_PROVIDER_GROUP_ID="1100361";
	 public static final String APPLICABLE_FROM_DATE="1100362"; 
	 public static final String APPLICABLE_FROM_TIME="1100363";
	 public static final String APPLICABLE_TILL_DATE="1100364"; 
	 public static final String APPLICABLE_TILL_TIME="1100365";
	 public static final String USER_ID_DOES_NOT_EXIST="1100366";
	 public static final String EXCEL_NOT_VALID="1100367";
	 public static final String GRADE_REQ="1100368";
	 public static final String CELL_GROUP_REQ="1100369";

	 public static final String INVALID_START_DATE="1100370";
	 public static final String SERVICE_PROVIDER_GROUP_DESCRIPTION="1100371";
	 public static final String SERVICE_PROVIDER_GROUP_NAME="1100372";
	 public static final String FILE_UPLOAD_AND_PROCCESS_PARTIALLY_SUCCESSFULL="1100373";
	 public static final String SENDER_CATEGORY_CODE_IS_INVALID ="1100374";
	 public static final String SENDER_GRADE_CODE_IS_INVALID ="1100375";
	 public static final String SENDER_GEOGDOMAIN_CODE_IS_INVALID ="1100376";
	 public static final String SENDER_CELL_GROUP_CODE_IS_INVALID ="1100377";

	 public static final String STOCK_PRODUCT_LIST_FAIL = "1100378";
	 public static final String CONFIRM_STOCK_FAIL = "1100379";
	 public static final String STOCK_ADD_SUCCESS="1100380";
	 public static final String STOCK_ADD_FAIL="1100381";

	 public static final String APPROVAL_LIST_FAIL="1100382";
	 public static final String APPROVAL_SUCCESS_MESSAGE="1100383";
	 public static final String APPROVAL_FAIL_MESSAGE="1100384";
	 public static final String DISPLAY_STOCK_FAIL= "1100385";
	 public static final String STOCK_REJECT_FAIL = "1100386";
	 public static final String STOCK_REJECT_SUCCESS="1100387";
	 public static final String STOCK_TXN_LIST_FAIL = "1100388";
	 public static final String TRANSFERRULE_ALLREADY_EXIST = "1100389";
	 public static final String INSERT_FAILD = "1100390";

	 // changes for UserDefaulconfigmangment 
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_INITIATE_HEADING ="1100391";
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_HEADER_DOWNLOADEDBY ="1100392";
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_HEADER_DOMAINNAME ="1100393";
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_DETAILS_CATEGORYCODE ="1100394";
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_DETAILS_TRANSFERPROFILE ="1100395";
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_TRANSFERPROFILE_COMMENT ="1100396";
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_DETAILS_COMMISIONPROFILE ="1100397";
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_COMMISIONPROFILE_COMMENT ="1100398";
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_DETAILS_GRADE ="1100399";	
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_DETAILS_GRADE_COMMENT ="1100400.";
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_DETAILS_GROUPCODE ="1100401";
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_DETAILS_GROUPCODE_COMMENT ="1100402";
	 public static final String USER_DEFAULT_CONFIG_XLXFILE_DETAILS_ACTIONT ="1100403";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_HEADING ="1100404";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYHEADING ="1100405";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYHEADING_NOTE ="1100406";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYCODE ="1100407";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYNAME ="1100408";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYGRADE ="1100409";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYGRADECODE ="1100410";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYGRADENAME ="1100411";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_TRANSFERCONTROLPRF ="1100412";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_TRANSFERCONTROLPRFCODE ="1100413";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_TRANSFERCONTROLPRFNAME ="1100414";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_COMMISIONPROFILE ="1100415";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_COMMISIONPROFILECODE ="1100416";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_COMMISIONPROFILENAME ="1100417";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_GROUPROLE ="1100418";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_GROUPROLECODE ="1100419";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_GROUPROLENAME ="1100420";
	 public static final String USER_DEFAULT_CONFIG_MASTERSHEET_REMARKS ="1100421";
	 public static final String BULKUSER_XLSFILE_DETAILS_ANOYONENOTDEFINED_COMMENT ="1100422";
	 public static final String TEMPLATE_DOWNLOAD_FAIL_DIRNOTCREATED ="1100423";
	 public static final String INVALID_TEMPLATE_FILE_NAME = "1100424";
	 public static final String USER_DEFAULT_CONFIGURATION_FILE_UPLOAD_ERROR_PATHNOTDEFINED = "1100425";


	 public static final String SERVICEGROUP_UPLOAD_VALIDATE_FILE_FILENAMELENGTH="1100426";
	 public static final String USER_DEFAULT_CONFIGURATION_UPLOAD_VALIDATE_FILE_ERROR_DOMAINNAMEERROR="1100427";
	 public static final String USER_DEFAULT_CONFIGURATION_UPLOAD_VALIDATE_FILE_ERROR="1100428";
	 public static final String USER_DEFAULT_CONFIGURATION_UPLOAD_INVALID_FILE="1100429";
	 public static final String USER_DEFAULT_CONFIGURATION_UPLOAD_PROCESS_FAIL="1100430";
	 public static final String NO_RECORDS_FOUND_IN_FILE_MSG_ERROR="1100431";
	 public static final String USERDEFAULCONFIG_MIGRARTION_PROCESSUPLOADEDFILE_CATEGORY_CODE_MISSING_ERROR="1100432";
	 public static final String USERDEFAULCONFIG_PROCESSUPLOADEDFILE_CATEGORY_CODE_INVALID_ERROR="1100433";
	 public static final String USERDEFAULCONFIG_PROCESSUPLOADEDFILE_TRANSFER_PROFILE_ERROR="1100434";
	 public static final String USERDEFAULCONFIG_PROCESSUPLOADEDFILE_TRANSFER_PROFILE_NOTUNDER_CATEGORY_ERROR="1100435";
	 public static final String USERDEFAULCONFIG_PROCESSUPLOADEDFILE_COMMISSION_PROFILE_MISSING_ERRR="1100436";
	 public static final String USERDEFAULCONFIG_PROCESSUPLOADEDFILE_COMMISSION_PROFILE_NOTFOUND_CATEGORY_ERROR="1100437";
	 public static final String USERDEFAULCONFIG_PROCESSUPLOADEDFILE_GRADE_MISSING_ERROR="1100438";
	 public static final String USERDEFAULCONFIG_PROCESSUPLOADEDFILE_GRADE_CODE_MISSMATCH_ERROR="1100439";
	 public static final String USERDEFAULCONFIG_PROCESSUPLOADEDFILE_GRPRFILE_ERROR="1100440";
	 public static final String USERDEFAULCONFIG_PROCESSUPLOADEDFILE_GRPRFILE_UNDERCATEGORY_ERROR="1100441";
	 public static final String USERDEFAULCONFIG_PROCESSUPLOADEDFILE_ERROR_ISDEFAULTALREDDYEXIST="1100442";
	 public static final String USERDEFAULCONFIG_PROCESSUPLOADEDFILE_ERROR_ACTIONCOULMNERROR="1100443";
	 public static final String USERDEFAULCONFIG_MSG_ERROR_UPGRADESCHANNELTABLES="1100444";
	 public static final String USERDEFAULCONFIG_MSG_ERROR_COMMISSIONPROFILEsetid_TABLES="1100445";
	 public static final String USERDEFAULCONFIG_MSG_ERROR_UPADTEROLE_TABLES="1100446";
	 public static final String USERDEFAULCONFIG_MSG_ERROR_UPADTETRANSFERPROFILETABLE_TABLES="1100447";
	 public static final String USERDEFAULCONFIGURATION_UPLOAD_ASSOCIATE_FILE_MSG_SUCCESS="1100448";
	 public static final String USERDEFAULCONFIGURATION_ERROR_MSG_CASHENOTUPDATED="1100449";
	 public static final String USERDEFAULCONFIGURATION_ERROR_MSG_FILENOTUPLOADED="1100450";
	 public static final String USERDEFAULCONFIG_UPLOAD_DATASHEET_ERROR_SHEETNOTMODIFIED="1100451";
	 public static final String USERDEFAULCONFIG_UPLOAD_DATASHEET_ERROR_INVALIDCOLUMNFILE="1100452";
	 public static final String USERDEFAULCONFIGURATION_UPLOAD_ASSOCIATE_FILE_MSG__PARIAL_SUCCESS="1100453";
	 public static final String USERDEFAULCONFIGURATION_UPLOAD_FILE_LENGTH_ERROR="1100454";

	 public static final String PROFILE_ADDITIONAL_SUCCESS_SUSPENORACTIVATE_MESSAGE = "1100455";
	 public static final String GENERAL_ERROR_PROCESSING = "1100456";
	 public static final String CHANGE_STATUS_FAILE_FOR_COMMISSION_PROFILE = "1100457";
	 public static final String  MAKE_DEFAULI_SUCCESS_MESSAGE = "1100458";
	 public static final String MAKE_DEFAULI_FAILE_FOR_COMMISSION_PROFILE = "1100459";

	 public static final String COMMISSION_ASSOCIATED_WITH_USER = "1100460";
	 public static final String ERROR_DELETE_COMMISSION_PROFILE_SET = "1100461";
	 public static final String COMMISSION_DELETE_SUCCESS="1100462";

	 
	 
	 public static final String VERSION_LIST_FAIL="1100466";
	 public static final String VERSION_LIST_SUCCESS="1100467";
	 public static final String ERROR_SUS_RES_COMMISSION_PROFILE_SET="1100468";
	 public static final String COMMISSION_SUS_RES_SUCCESS="1100469";
	 public static final String DEFAULT_PROFILE_SUSPEND="1100470";
	 public static final String COMMISSION_PROFILE_SET_LANGUAGE_ERROR = "1100471";
	 public static final String NO_SERVICE_EXISTS ="1100472";
	 public static final String SUCCESSFULLY_LOAD_C2S_CARD_GROUP_SERVICE="1100473";
	 public static final String FAILED_LOAD_C2S_CARD_GROUP_SERVICE="1100474";
	 public static final String SUCCESSFULLY_LOAD_C2S_CARD_GROUP_LIST="1100475";
	 public static final String FAILED_LOAD_C2S_CARD_GROUP_LIST="1100476";
	 public static final String CARD_GROUP_NAME_ALREADY_EXIST="1100477";
	 public static final String CARD_GROUP_CANNOT_BE_ADDED_FOR_CHOICE_RECHARGE_LOCK_FAILED="1100478";
	 public static final String CARD_GROUP_CANNOT_BE_ADDED_FOR_CHOICE_RECHARGE="1100479";
	 public static final String ERROR_GENERAL_PROCESSING= "1100480";
	 public static final String CARDGROUP_CARDGROUP_C2S_DETAILS_INVALIDSLAB="1100481";
	 public static final String CARDGROUP_CARDGROUP_DETAILS_ERROR_DUPLICATE_CARDGROUPCODE="1100482";
	 public static final String CARDGROUP_DETAILS_ERROR_SLAB_NAME="1100483";
	 public static final String CARDGROUP_C2S_DETATILS_ERROR_INVALID_RECEIVER_TRANSFERRULE="1100484";
	 public static final String LOAD_ADD_TEMP_CARD_GROUP_LIST_SUCCESSFULLY="1100485";
	 public static final String LOAD_ADD_TEMP_CARD_GROUP_LIST_FAILED="1100486";
	 public static final String SAVE_C2S_CARD_GROUP_LIST_SUCCESS="1100487";
	 public static final String SAVE_C2S_CARD_GROUP_LIST_FAILED="1100488";
	 public static final String INVALID_RECEIVER_TRANSFER_VALUE_SLABWISE="1100489";
	 public static final String INVALID_VOUCHER_AMOUNT="1100490";
	 public static final String USER_ALREADY_APPROVED="1100491";
	 public static final String INVALID_TIME_SLAB="1100492";
	 public static final String INVALID_START_DATE_FORMAT="1100493";
	 public static final String INVALID_END_DATE_FORMAT="1100494";

	 public static final String LANGUAGE_ONE_MESS_REQ="1100495";
	 public static final String LANGUAGE_TWO_MESS_REQ="1100496";
	 public static final String MOBILE_NUMBER="1100497";
	 public static final String MESSAGE_GATEWAY_HAS_MODIFIED_SUCCESSFULLY="1100498";
	 public static final String MESSAGE_GATEWAY_HAS_DELETED_SUCCESSFULLY="1100499";
	 
	 
	 public static final String COMMISSION_PROFILE_NOT_FOUND = "1100501";

	 
	 public static final String COMM_PROF_SET_NAME_EXISTS="1100502";
	 public static final String SHORT_CODE_EXISTS="1100503";
	 public static final String COMM_PROF_ALREADY_EXISTS="1100504";
	 public static final String COMM_PRF_UPDATE_SUCCESS="1100505";
	 public static final String COMM_PRF_UPDATE_FAIL="1100506";
	 
	 public static final String COMM_PRF_ADD_SUCCESS="1100507";
	 public static final String COMM_PRF_ADD_FAIL="1100508";
	 
	 public static final String SUCCESSFULLY_LOAD_C2S_CARD_GROUP_SET_NAME_LIST="1100509";
	 public static final String FAILED_LOAD_C2S_CARD_GROUP_SET_NAME_LIST="1100510";
	 public static final String SUCCESSFULLY_LOAD_C2S_CARDGROUP_VERSION_LIST ="1100511";
	 public static final String FAILED_LOAD_C2S_CARDGROUP_VERSION_LIST ="1100512";
	 public static final String SUCCESSFULLY_LOAD_VIEW_C2S_CARDGROUP_DETAILS ="1100513";
	 public static final String FAILED_LOAD_VIEW_C2S_CARDGROUP_DETAILS ="1100514";
	 public static final String INVALID_DATE_TIME_FORMATE="1100515";
	 public static final String INSERT_CARD_GROUP_SET_HAS_ADDED_SUCCESSFULLY ="1100516";
	 public static final String FAIELD_CARD_GROUP_SET_HAS_ADDED_SUCCESSFULLY ="1100517";
	 public static final String INSERT_CARDGROUP_DETAILS_HAS_ADDED_SUCCESSFULLY ="1100518";
	 public static final String INSERT_CARDGROUP_DETAILS_HAS_ADD_FAILED ="1100519";
	 public static final String INSERT_CARDGROUP_SET_VERSION_HAS_ADDED_SUCCESSFULLY="1100520";
	 public static final String INSERT_CARDGROUP_SET_VERSION_HAS_ADDED_FAIELD="1100521";
	 public static final String SUCCESSFULLY_ADD_MODIFY_C2S_CARDGROUP_TEMP_LIST="1100522";
	 public static final String  FAILED_ADD_MODIFY_C2S_CARDGROUP_TEMP_LIST="1100523";
	 public static final String EMPTY_ROW ="1100524";
	 public static final String MODIFY_DELETE_SUCCESSFULLY="1100525";
	 public static final String MODIFY_DELETE_FAILED="1100526";
	 
	// commissionprfilemgmnt
		public static final String BATCH_COMM_PROFILE_ERROR_FILENAMELENGTH = "1100527";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_CATEGORYCODEMISSING = "1100528";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALIDCATEGORYCODE = "1100529";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_GEOCODEMISSING = "1100530";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALIDGEOGRAPHYCODE = "1100531";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_GRADECODEMISSING = "1100532";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALIDGRADECODE = "1100533";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILENAMEMISSING = "1100534";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PROFILENAME = "1100535";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAX_LENGTH_PROFILE_NAME_EXCEED = "1100536";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SHORTCODEMISSING = "1100537";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_SHORTCODE = "1100538";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILE_NAMESHORTCODEMISSATCH = "1100539";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SHORT_NAME_ALREDDY_ASSOCIATED = "1100540";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAX_LENGTH_SHORT_NAME_EXCEED = "1100541";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUALCOMMPROFILE_MISSING = "1100542";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUALCOMMPROFILE_INVALID = "1100543";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUALCOMMPROFILE_WITH_PREVIOUS_RECORD = "1100544";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLE_DATE_MISSING = "1100545";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLEDATENOTMATCHWITH_PREVIOUSRECORD = "1100546";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR__APPLICABLE_TIMEMISSING = "1100547";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_APPLICABLETIMENOTMATCHWITH_PREVIOUSRECORD = "1100548";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_DATE_FORMAT_INVALID = "1100549";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_TIME_FORMAT_INVALID = "1100550";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_APPLICABLETIME = "1100551";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PRODUCTMESSAGE_MISSING = "1100552";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PRODUCTNAME = "1100553";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TRASACTIONTYPE_MISSING = "1100554";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_TRASACTIONTYPE = "1100555";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PAYMENT_MODE_MISSING = "1100556";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PAYMENT_MODE = "1100557";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF = "1100558";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF_NUMERIC = "1100559";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MULTIPLEOFF_DECIMAL = "1100560";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_COMM_MISSING = "1100561";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_NUMERIC = "1100562";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_COMM_MISSING = "1100563";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_NUMERIC = "1100564";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDITIONAL_MAXTRNSF_POSITIVE = "1100565";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_VALUE = "1100566";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINMAXNOTMACHEEDWITH_PREVIOUSRECORS = "1100567";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX_ON_FOC = "1100568";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX_ON_C2C = "1100569";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGE = "1100570";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGE_NUMERIC = "1100571";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE = "1100572";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE_NUMERIC = "1100573";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGEVALUE = "1100574";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_END_RANGE_INVALID = "1100575";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_COMM_TYPE = "1100576";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_COMM_TYPE_NUMERIC = "1100577";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_COMM_RATE = "1100578";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_COMM_AMOUNT = "1100579";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_TYPE = "1100580";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_RATE_NUMERIC = "1100581";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_RATE = "1100582";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX1_AMOUNT = "1100583";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_TYPE = "1100584";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_RATE_NUMERIC = "1100585";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_RATE = "1100586";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX2_AMOUNT = "1100587";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX3_TYPE = "1100588";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX3_RATE_NUMERIC = "1100589";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX3_RATE = "1100590";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TAX3_AMOUNT = "1100591";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGE_INVALID = "1100592";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SLAB_RANGE = "1100593";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_DOMAIN_CODE = "1100594";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DOMAIN_CODE_MISSING = "1100595";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILE_NAME_MISSING = "1100596";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILE_NAME_NOT_MATCHING = "1100597";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PRODUCT_NAME = "1100598";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUPLICATE_RECORD = "1100599";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TODATE_MISSING_INCBC = "1100600";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_FROMDATE_MISSING_ISCBC = "1100601";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PRODUCT_CODE_MISSING = "1100602";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_DATEFORMAT = "1100603";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_DATEFORMAT_AFTER = "1100604";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_OTF_FROMDATE = "1100605";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_VALUE_ESSENTIAL = "1100606";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_ESSENTIAL = "1100607";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_VALUE_INTEGER = "1100608";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TYPE_OTF_NOT_AMT_OR_PCT = "1100609";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_NUMERIC_DECIMAL = "1100610";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_RATE_INVALID = "1100611";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_VALUE_GREATER_FROM_PREVOS = "1100612";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_OTF_INVALID_COMM = "1100613";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILE_NAMEMISSING_INADDITONALSHEET = "1100614";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_PROFILE_NAME_NOT_MATCHIMG = "1100615";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_DATEFORMAT_INADDITIONALCOMPROFILE = "1100616";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SYN_COMMENT_ADD = "1100617";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_CAC_DATEFORMATE_AFTER = "1100618";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADD_GATEWAY_CODE_INVALID = "1100619";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SERVICE_CODE_MISSING = "1100620";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SERVICE_TYPE_INVALID = "1100621";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SELECTORCODE_MISSING = "1100622";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_SUBSERVICE_INVALID = "1100623";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MINTRSF_MISSING_FROMADD = "1100624";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_MAXTRSF_MISSING_FROMADD = "1100625";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDCOMM_RATE_NUMERIC = "1100626";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_ADDCOMM_RATE = "1100627";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDCOMM_AMOUNT = "1100628";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_ROAM_TYPE = "1100629";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ROAM_COMMRATE_NUMERIC = "1100630";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_ROAM_COMMRATE = "1100631";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_ROAM_COMMAMOUNT = "1100632";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DIFFERENTIAL_NUMERIC = "1100633";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADD1TAX_RATE = "1100633";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_ADD1TAX_AMOUNT = "1100635";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_ADDTAX2_RATE = "1100636";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_ADDTAX2_AMOUNT = "1100637";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OWNER_COMM_RATE_NUMERIC = "1100638";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ROAMOWNER_COMM_AMOUNT = "1100639";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_COMMRATE_OWNER = "1100640";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_OWNER_TAX1AMT = "1100641";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_OWNER_TAX1RATE = "1100642";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_OWNER_TAX2AMT = "1100643";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_OWNER_TAX2RATE = "1100644";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_CAC_TO_DATEMISSING = "1100645";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ADD_CAC_FROM_DATEMISSING = "1100646";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_DATEFORMATE = "1100647";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_DATEFORMATE_AFTER = "1100648";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_FROMDATE = "1100649";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_OTF_TODATE = "1100650";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INADD_OTF_TYPE = "1100651";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_RATE_ESSENTIAL = "1100652";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_ESSENTIAL = "1100653";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_INTEGER = "1100654";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_NOTAMT_ORPCT = "1100655";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_DECIMAL_NUMERIC = "1100656";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_OTF_VALUE_GREATER_FROM_PREV = "1100657";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_MISSMATCH_PROFILENAME = "1100658";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFIL_ERROR_TRYLATER = "1100659";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_ADDITIONAL_MINTRNSF_POSITIVE = "1100660";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_UPDATE_COMM_PROFILE_SET_TABLE = "1100661";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INSERT_COMM_PROFILE_SET__VERSION_TABLE = "1100662";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INSERT_COMM_PROFILE_PRODUCT_TABLE = "1100663";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INSERT_COMM_PROFILE_DETAILS_TABLE = "1100664";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_FILE = "1100665";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_RESPONSE_FAILED = "1100666";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_TIME_FORMAT = "1100667";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_HOUR_FORMAT = "1100668";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_MINUTE = "1100669";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_RATELIMITS = "1100670";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_TIME_OVERLAPS = "1100671";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_INCOMPATILBE_TIME = "1100672";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_UPDATE_COMM_PROFILE_OTF_TABLE = "1100673";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_UPDATE_COMM_PROFILE_DETAIL_OTF_TABLE = "1100674";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_UPLOADED_SUCCESSFULLY = "1100675";
		public static final String BATCH_MODIFYCOMMPROFILE_PROCESS_UPLOADEDFILE_BATCHPRILENAME_LENGTH_EXCEED = "1100676";
		public static final String BATCH_MODIFYCOMMPROFILE_PROCESS_UPLOADEDFILE_BATCH_NOT_CREATED = "1100677";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_RANGELIMITS = "1100678";
		public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_INVALID_RANGE_OVERLAPS = "1100679";
		public static final String BATCHADD_INVALID_INCOMPATILBE_TIME = "1100680";
		public static final String BATCHADD_INVALID_TIME_FORMAT = "1100681";
		public static final String BATCHADD_INVALID_HOUR = "1100682";
		public static final String BATCHADD_INVALID_MINUTE = "1100683";
		public static final String OTF_INVALID_TIME_FORMAT = "1100684";
		public static final String OTF_INVALID_HOUR = "1100685";
		public static final String OTF_INVALID_MINUTE = "1100686";
		public static final String OTF_INVALID_RANGE_LIMIT = "1100687";
		public static final String OTF_INVALID_TIME_RANGE_OVERLAP = "1100688";
		public static final String OTF_INVALID_INCOMPATIBLE_TIME = "1100689";
		public static final String OTFTIMESLAB_ACCORDANCE_WITH_ADD_COMM = "1100690";
		public static final String ERROR_INSERT_INTO_COMMPROFILE_SERVICE_TYPE_TABLE = "1100691";
		public static final String ERROR_INSERT_INTO_ADDITIONAL_COMMPROFILE_DETAILS_TABLE = "1100692";
		public static final String INVALID_FILE_BATCH_ADD_COMMPRO= "bulkuser.processuploadedfile.modify.error.notvaliedfile";

	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_HEADING = "1100693";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_COMMISSION_HEADING = "1100694";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_COMMISSION_NOTE = "1100695";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_TAX_TYPE = "1100696";
	public static final String BATCHUSERCREATION_MASTERSHEET_TAX_DESCRIPTION = "1100697";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_AMT = "1100698";
	public static final String BATCHUSERCREATION_MASTERSHEET_AMOUNT = "1100699";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_PCT = "1100700";
	public static final String BATCHUSERCREATION_MASTERSHEET_PERCENTAGE = "1100701";
	public static final String BATCH_ADD_COMM_PROFILE_DUAL_PROFILE_TYPE = "1100702";
	public static final String BATCH_ADD_COMM_PROFILE_DUAL_PROFILE_TYPE_DESCRIPTION = "1100703";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_TAX_ON_FOC = "1100704";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_TAX_VALUE = "1100705";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_TAX_ON_C2C = "1100706";
	public static final String COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_COMMON = "1100707";
	public static final String BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TYPE = "1100708";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_OTF_TYPE_ID_COMMENT = "1100709";
	public static final String BATCH_ADD_MODIFY_MASTER_OTF_COMMENT_DATE_AND_TIME = "1100710";
	public static final String BASE_BATCHADDMODIFYCOMM_PROFILE_OTF_DATE_TIME = "1100711";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_PRODUCT_HEADING = "1100712";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_PRODUCT_NOTE = "1100713";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_PRODUCTCODE = "1100714";
	public static final String BATCHUSERCREATION_MASTERSHEET_PRODUCTNAME = "1100715";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_DOMAIN_NOTE = "1100716";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_DOMAINCODE = "1100717";
	public static final String BATCHUSERCREATION_MASTERSHEET_DOMAINNAME = "1100718";
	public static final String ERROR_WHILE_INSERTING_ADDITIONAL_COMMISSION_PROFILE_DETAILS = "1100719";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_CATEGORY_NOTE = "1100720";
	public static final String BATCHMODIFYCOMMPROFILE_MASTERSHEET_CATEGORYCODE = "1100721";
	public static final String BATCHUSERCREATION_MASTERSHEET_CATEGORYNAME = "1100722";
	public static final String BATCH_ADD_COMM_PROFILE_GATEWAYCODE = "1100723";
	public static final String BATCH_ADD_COMM_PROFILE_GATEWAYNAME = "1100724";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TEMPLATE = "1100725";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_DOWNLOADEDBY = "1100726";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_NETWORK = "1100727";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION_SLAB = "1100728";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION = "1100729";
	public static final String BATCH_ADD_COMM_PROFILE_TAXES = "1100730";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_NAME = "1100731";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_SHORT_CODE = "1100732";
	public static final String BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE = "1100733";
	public static final String BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE_COMMENT = "1100734";
	public static final String BATCH_ADD_COMM_PROFILE_PAYMENTCODE = "1100735";
	public static final String BATCH_ADD_COMM_PROFILE_GRPHDOMAINCODE = "1100736";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_GEOGRAPHYCODE_COMMENT = "1100737";
	public static final String BATCH_ADD_COMM_PROFILE_GRADECODE = "1100738";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_GRADECODE_COMMENT = "1100739";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_SETID = "1100740";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_SETID_COMMENT = "1100741";
	public static final String BATCH_ADD_COMM_PROFILE_DUAL_PROFILE = "1100742";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_VERSION = "1100743";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_VERSION_COMMENT = "1100744";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_PRODUCT_ID = "1100745";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_PRODUCT_ID_COMMENT = "1100746";
	public static final String BASE_BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_DETAILID = "1100747";
	public static final String BASE_BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_PRODUCT_DETAIL_ID_COMMENT = "1100748";
	public static final String BATCHMODIFYCOMMPROFILE_ADDITIONAL_COMMISSION_PROFILE_APPLICABLE_FROM = "1100749";
	public static final String PROFILE_COMMISSIONPROFILEDETAILVIEW_LABEL_APPLICABLEFROMFORMAT = "1100750";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_APPLICABLE_TIME = "1100751";
	public static final String PROFILE_COMMISSIONPROFILEDETAILVIEW_LABEL_APPLICABLEFROMHOURFORMAT = "1100752";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAXON_FOC = "1100753";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAXON_C2C = "1100754";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_MULTIPLE_OF = "1100755";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_MIN_TRANSFER = "1100756";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_MAX_TRANSFER = "1100757";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_FROM_RANGE = "1100758";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TO_RANGE = "1100759";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION_TYPE = "1100760";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISISON_RATE = "1100761";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX1_TYPE = "1100762";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX1_RATE = "1100763";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX2_TYPE = "1100764";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX2_RATE = "1100765";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX3_TYPE = "1100766";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX3_RATE = "1100767";
	public static final String BATCH_ADD_COMM_PROFILE_TRANSACTION_DESCRIPTION = "1100768";
	public static final String BATCH_ADD_COMM_PROFILE_PAYMENT_HEADING = "1100769";
	public static final String BATCH_ADD_COMM_PROFILE_PAYMENTNAME = "1100770";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ADDITIONAL_COMMISSION_SLAB = "1100771";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ROAM_COMMISSION = "1100772";
	public static final String PROFILE_COMMISSIONPROFILEDETAIL_LABEL_HEADING_MARGIN_OWNER = "1100773";
	public static final String PROFILE_COMMISSIONPROFILEDETAIL_LABEL_HEADING_TAXES_OWNER = "1100774";
	public static final String COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL = "1100775";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_NAME_COMMENT = "1100776";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_SERVICEID = "1100777";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_PRODUCT_SERVICE_ID_COMMENT = "1100778";
	public static final String BATCH_ADD_ADDITIONALCOMM_PROFILE_APPLICABLE_TO = "1100779";
	public static final String BATCH_ADD_COMM_PROFILE_TIMESLAB = "1100780";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ADDITIONAL_COMMISSION_SERVICE = "1100781";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ADDITIONAL_COMMISSION_SUBSERVICE = "1100782";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ADDITIONAL_COMMISSION_STATUS = "1100783";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ADDITIONAL_COMMISSION_DIFFERENTIAL_FACTOR = "1100784";
	public static final String COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_APPLICABLE_FROM = "1100785";
	public static final String BATCH_ADD_COMM_PROFILE_OTF_COMMENT_FROM_DATE = "1100786";
	public static final String COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_APPLICABLE_TO = "1100787";
	public static final String BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_DATE = "1100788";
	public static final String COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_TIMESLAB = "1100789";
	public static final String BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TIMESLAB = "1100790";
	public static final String COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_TYPE = "1100791";
	public static final String COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_VALUE = "1100792";
	public static final String COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_TYPE = "1100793";
	public static final String COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_RATE = "1100794";
	public static final String BATCHMODIFYCOMMPROFILE_OTF_PROFILE_TEMPLATE = "1100795";
	public static final String COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_DETAIL = "1100796";
	public static final String COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_APPLICABLE_FROM = "1100797";
	public static final String COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_APPLICABLE_TO = "1100798";
	public static final String COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_TIMESLAB = "1100799";
	public static final String BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TIMESLAB = "1100800";
	public static final String BATCH_ADD_COMM_PROFILE_SERVICE_TYPE_HEADING = "1100801";
	public static final String BATCH_ADD_COMM_PROFILE_MASTERSHEET_SERVICETYPE = "1100802";
	public static final String BATCH_ADD_COMM_PROFILE_MASTERSHEET_SERVICENAME = "1100803";
	public static final String BATCH_ADD_COMM_PROFILE_SUBSERVICE_TYPE_HEADING = "1100804";
	public static final String BATCH_ADD_COMM_PROFILE_SUBSERVICE_TYPE_NOTE = "1100805";
	public static final String BATCH_ADD_COMM_PROFILE_MASTERSHEET_SUBSERVICE_TYPE = "1100806";
	public static final String BATCH_ADD_COMM_PROFILE_MASTERSHEET_SUBSERVICE_NAME = "1100807";

	public static final String INVALID_FILE_BATCH_MODIFY_COMMISSION_PROFILE= "bulkuser.processuploadedfile.modify.error.notvaliedfile";

	public static final String BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_SETNAME = "1100808";
	public static final String BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_SHORTCODE = "1100809";
	public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_DUALCOMMISSIONMISSING = "1100810";
	public static final String BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_MIN_MAX_TRANSFERVALUES = "1100811";
	public static final String BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_STARTRANGE_BLANK = "1100812";
	public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_STARTRANGE_NUMERIC = "1100813";
	public static final String BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_STARTRANGE_POSITIVE = "1100814";
	public static final String BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_ENDRANGE_BLANK = "1100815";
	public static final String ERROR_WHILE_INSERTING_COMMISSION_PROFILE_SERVICE_TYPE = "1100816";
	public static final String BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_ENDRANGE_POSITIVE = "1100817";
	public static final String BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_STARTRANGE_ENDRANGE = "1100818";
	public static final String BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_STARTRANGE = "1100819";
	public static final String BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_ENDRANGE = "1100820";
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_INVALID_TIME = "1100821";
	public static final String BULKOTFPROFILE_PROCESFILE_ERROR_MAX_LENGTH_PROFILE_NAME_EXCEED = "1100822";
	public static final String BATCHMODIFYCOMMPROFILE_TO_DATE_IS_MISSING_ADDITIONALCOMMISSION = "1100823";
	public static final String BATCHMODIFYCOMMPROFILE_FROM_DATE_IS_MISSING_ADDITIONALCOMMISSION = "1100824";
	public static final String BATCHMODIFYCOMMPROFILE_DATE_FORMATE_AFTER = "1100825";
	public static final String BULKADDITIONALPROFILE_PROCESSUPLOADEDFILE_ERROR_MINMAXTRANSFERVALUENOTMATCHEDWITHPREVIOUSRECORD = "1100826";
	public static final String BATCH_MODIFY_PROCESSUPLOADEDFILEFORCOMMPROFILE_ERROR_ADDTAX1RATE = "1100827";
	public static final String BATCHMODIFYCOMMPROFILE_PROCESSUPLOADEDFILE_MSG_SUCCESS = "1100828";

	public static final String DOWNLOAD_BATCH_COMMISSION_PROFILE_TEMPLATE = "Download Batch Commission Profile Template";
	public static final String UPLOAD_FILE_FOR_BATCH_MODIFY_COMMISSION_PROFILE = "Upload File For Batch Commission Profile";
	public static final String BATCHMODIFYCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALIDFILEFORMAT = "1100829";
	public static final String ERROR_WHILE_DELETING_DATA_FROM_ADDNL_COMM_PROFILE_DETAILS = "1100830";
	public static final String ERROR_WHILE_UPDATING_COMMISSIONPROFILE_SET = "1100831";
	public static final String ERROR_WHILE_INSERTING_COMMISSION_PROFILE_SET_VERSION = "1100832";
	public static final String ERROR_WHILE_UPDATING_COMMISSION_PROFILE_SET_VERSIONS = "1100833";
	public static final String ERROR_WHILE_DELETING_COMMISSION_PROFILE_PRODUCT = "1100834";
	public static final String ERROR_WHILE_DELETING_DATA_FROM_COMMISSION_PROFILE_DETAILS = "1100835";
	public static final String ERROR_WHILE_INSERTING_COMMISSION_PROFILE_PRODUCT = "1100836";
	public static final String ERROR_WHILE_INSERTING_COMMISSION_PROFILE_DETAILS = "1100837.";
	public static final String ERROR_WHILE_UPDATING_COMMISSION_PROFILE_OTF = "1100838";
	public static final String ERROR_WHILE_DELETING_DATA_FROM_PROFILE_OTF_DETAILS = "1100839";
	public static final String ERROR_WHILE_UPDATING_PROFILE_OTF_DETAILS = "1100840";
	public static final String ERROR_WHILE_UPDATING_COMMISSION_PROFILE_SERVICE_TYPE = "1100841";
	public static final String BULKUPLOAD_PROCESSUPLOADEDFILE_SHEETNAME_COMMISSIONSHEET = "1100842";
	public static final String BULKUPLOAD_PROCESSUPLOADEDFILE_SHEETNAME_ADDITIONALCOMMSHEET = "1100843";
	public static final String BULKUPLOAD_PROCESSUPLOADEDFILE_SHEETNAME_CBCSHEET = "1100844";
	public static final String MODIFY_C2S_CARDGROUP_SAVE_SUCCUSSFULLY ="1100845";
	public static final String MODIFY_C2S_CARDGROUP_SAVE_FAILED ="1100846";
	public static final String NO_DATA_FOUND_FOR_FILTERS_CRITERIA="1100847";
	public static final String SOS_ALLOWED ="1100848";
	public static final String SOS_THRESHOLD ="1100849";
	public static final String LAST_RECHARGE ="1100850";
	
	public static final String SERVICES_STATUS_UPDATE_SUCCESS="1100851";
	public static final String SERVICES_STATUS_UPDATE_FAIL="1100852";
	
	public static final String BATCH_ADD_COMM_PROFILE_MASTER_HEADING ="1100859";
	public static final String BATCH_ADD_COMM_PROFILE_COMMISSION ="1100860";
	public static final String BATCH_ADD_COMM_PROFILE_COMMISSION_NOTE ="1100861";
	public static final String BATCH_ADD_COMM_PROFILE_TAX_TYPE ="1100862";
	public static final String BATCH_ADD_COMM_PROFILE_TAX_DESCIPTION ="1100863";
	public static final String BATCH_ADD_COMM_PROFILE_AMT ="1100864";
	public static final String BATCH_ADD_COMM_PROFILE_PCT ="1100865";
	public static final String BATCH_ADD_COMM_PROFILE_PERCENTAGE ="1100866";
	public static final String BATCH_ADD_COMM_PROFILE_TAX_CAL_FOC_NOTE ="1100867";
	public static final String BATCH_ADD_COMM_PROFILE_TAX_CAL_FOC_DECRIPTION ="1100868"; 
	public static final String BATCH_ADD_COMM_PROFILE_TAX_CAL_C2C_TRANSFER ="1100869";
	public static final String BATCH_ADD_COMM_PROFILE_TAX_CAL_C2C_TRANSFER_DESCRIPTION ="1100870"; 
	public static final String BATCH_ADD_COMM_PROFILE_TAX_CAL_C2C_TRANSFER_NOTE ="1100871";
	public static final String PROFILE_ADDADDITIONALPROFILE_LABEL_TIMESLAB_EXAMPLE ="1100872";
	public static final String BASE_BATCHADDMODIFYCOMM_PROFILE_OTF_TYPE_ID_COMMENT ="1100873";
	public static final String BATCH_ADD_COMM_PROFILE_PRODUCT_HEADING ="1100874";
	public static final String BATCH_ADD_COMM_PROFILE_PRODUCT_HEADING_NOTE ="1100875";	
	public static final String BATCH_ADD_COMM_PROFILE_PRODUCTCODE ="1100876";
	public static final String BATCH_ADD_COMM_PROFILE_PRODUCTNAME ="1100877";
	public static final String BATCH_ADD_COMM_PROFILE_DOMAIN_CATEGORY_HEADING ="1100878";
	public static final String BATCH_ADD_COMM_PROFILE_DOMAIN_CATEGORY_HEADING_NOTE ="1100879";
	public static final String BATCH_ADD_COMM_PROFILE_DOMAINCODE ="1100880";
	public static final String BATCH_ADD_COMM_PROFILE_DOMAINNAME ="1100881";
	public static final String BATCH_ADD_COMM_PROFILE_CATEGORYCODE ="1100882";
	public static final String BATCH_ADD_COMM_PROFILE_CATEGORYNAME ="1100883";
	public static final String BATCH_ADD_COMM_PROFILE_GRPHDOMAINNAME ="1100884";
	public static final String BATCH_ADD_COMM_PROFILE_GEADECODE ="1100885";
	public static final String BATCH_ADD_COMM_PROFILE_GEADENAME ="1100886"; 
	public static final String BATCH_ADD_COMM_PROFILE_COMM_TEMPLATE_HEADING ="1100887";
	public static final String BATCH_ADD_COMM_PROFILE_DOWNLOADED_BY ="1100888";
	public static final String BATCH_ADD_COMM_PROFILE_CATEGORY ="1100889";
	public static final String BATCH_ADD_COMM_PROFILE_VERSION ="1100890";
	public static final String BATCH_ADD_COMM_PROFILE_FIELDS_MENDATORY_WARNING ="1100891";
	public static final String BATCH_ADD_COMM_PROFILE_PROFILE_NAME ="1100892";
	public static final String BATCH_ADD_COMM_PROFILE_SHORT_CODE ="1100893";
	public static final String BATCH_ADD_COMM_PROFILE_APPLICABLE_FROM ="1100894"; 
	public static final String BATCH_ADD_COMM_PROFILE_APPLICABLE_FROM_COMMENT ="1100895";
	public static final String BATCH_ADD_COMM_PROFILE_APPLICABLE_TIME ="1100896";
	public static final String BATCH_ADD_COMM_PROFILE_APPLICABLE_TIME_COMMENT ="1100897";
	public static final String BATCH_ADD_COMM_PROFILE_MULTIPLE_OF ="1100898";
	public static final String BATCH_ADD_COMM_PROFILE_MIN_TRANSFER_VALUE ="1100899";
	public static final String BATCH_ADD_COMM_PROFILE_MAX_TRANSFER_VALUE ="1100900";
	public static final String BATCH_ADD_COMM_PROFILE_TAX_CAL_FOC ="1100901";
	public static final String BATCH_ADD_COMM_PROFILE_ASSIGN_COMMISSION_SLABS ="1100902";
	public static final String BATCH_ADD_COMM_PROFILE_TO_RANGE ="1100903";
	public static final String BATCH_ADD_COMM_PROFILE_TYPE ="1100904";
	public static final String BATCH_ADD_COMM_PROFILE_RATE ="1100905";
	public static final String BATCH_ADD_COMM_PROFILE_TAX1_TYPE ="1100906";
	public static final String BATCH_ADD_COMM_PROFILE_TAX1_RATE ="1100907";
	public static final String BATCH_ADD_COMM_PROFILE_TAX2_TYPE ="1100908";
	public static final String BATCH_ADD_COMM_PROFILE_TAX2_RATE ="1100909";
	public static final String BATCH_ADD_COMM_PROFILE_TAX3_TYPE ="1100910";
	public static final String BATCH_ADD_COMM_PROFILE_TAX3_RATE ="1100911";
	public static final String BATCH_ADD_COMM_PROFILE_OTF_COMM_TEMPLATE_HEADING ="1100912";
	public static final String BATCH_ADD_COMM_PROFILE_DOMAIN ="1100913";
	public static final String BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_FROM_DATE ="1100914";
	public static final String BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_DATE ="1100915";
	public static final String BATCH_ADD_COMM_PROFILE_ADDITIONAL_COMM_TEMPLATE_HEADING ="1100916";
	public static final String BATCH_ADD_COMM_PROFILE_SYNC_COMMENT ="1100917";
	public static final String BATCH_ADD_COMM_PROFILE_SYNC_COMMENT_ADD ="1100918";
	public static final String BATCH_ADD_ADDITIONALCOMM_PROFILE_APPLICABLE_FROM ="1100919"; 
	public static final String BATCH_ADD_COMM_PROFILE_GATEWAY_CODE ="1100920";
	public static final String BATCH_ADD_COMM_PROFILE_SERVICE_CODE ="1100921";
	public static final String BATCH_ADD_COMM_PROFILE_SUBSERVICE_CODE ="1100922";
	public static final String BATCH_ADD_COMM_PROFILE_ASSIGN_ADDITIONAL_COMMISSION_SLABS ="1100923";
	public static final String BATCH_ADD_COMM_PROFILE_DIFFRENTIAL_FACTOR ="1100924"; 
	public static final String BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION_RATE ="1100925"; 
	public static final String BATCH_ADD_COMM_PROFILE_AMOUNT = "1100926";
	public static final String BATCH_ADD_COMM_PROFILE_FROM_RANGE ="1100927";
	public static final String BASE_BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_OTF_TYPE_ID_COMMENT ="1100928";
	public static final String BARRED ="1100929";
	public static final String LOGIN_TIME_NOT_ALLOWED = "1100930";
	public static final String USER_TRANSFER_PRODUCT_LESS_BALANCE = "1100931";
	public static final String C2C_ALLOWED ="1100932";
	public static final String ERROR_ERP_CHNL_USER_NEW_EXTERNAL_CODE_MISSING = "1100933";
	public static final String COMM_SUSPEND = "1100934";
	public static final String APPLICABLE_FROM_DATE_1="1100935";
	public static final String APPLICABLE_TILL_DATE_1="1100936";

	public static final String LOAD_NETWORK_PRODUCT_MAPPING_FAIL ="1100939";
	public static final String PRODUCT_NETWORKPRODUCTDETAIL_FAILEDMESSAGE ="1100940";
	public static final String PRODUCT_PRODUCTACTION_MSG_NODATAFOUND ="1100941";
	public static final String ADD_NETWORK_PRODUCT_MAPPING_FAIL ="1100942";
	public static final String EXTERNAL_TRANSACTION_DATE_INVALID = "1100943";
	public static final String PRODUCT_NETWORKPRODUCTDETAIL_SUCCESSMESSAGE = "1100944";
	public static final String DIRECTORY_NOT_CREATED = "1100945";
	public static final String SELECT_CARD_GROUP_SET = "1100946";
	public static final String PATH_NOT_DEFINED = "1100947";
	public static final String FILE_NOT_VALID = "1100948";
	public static final String FILE_REJECTED = "1100949";
	public static final String CARD_GROUP_MODIFY_FAIL= "1100950";
	public static final String CARD_GROUP_MODIFY_SUCCESS = "1100951";
	public static final String ERROR_MESSAGE_LABEL = "1100952";
	public static final String GENERATE_FOC_BATCH_MASTER_TRANSFER_ID_FAILED ="1100953";
	public static final String GENERATE_FOC_BATCH_DETAIL_TRANSFER_ID_FAILED ="1100954";
	public static final String GENERATE_DP_BATCH_MASTER_TRANSFER_ID_FAILED ="1100955";
	public static final String GENERATE_DP_BATCH_DETAIL_TRANSFER_ID_FAILED ="1100956";
	public static final String NETWORK_STOCK_MAX_LIMIT = "1100957";
	public static final String NETWORK_PRODUCT_MAP_MODIFIED = "1100958";
	public static final String NETWORK_PRODUCT_MAP_ACTIVATED = "1100959";
	public static final String NETWORK_PRODUCT_MAP_SUSPENDED = "1100960";
	public static final String CHOICE_RECHARGE_ERROR = "1100961";
	public static final String COMM_PRF_UPDATE_SUCCESS_WITH_NEW_VERSION = "1100962";
	public static final String BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_TIMESLAB="1100963";
	public static final String DOMAIN_SUCCESS="1100964";
	public static final String DOMAIN_FAIL="1100965";
	public static final String DOMAIN_FAIL1="1100966";
	public static final String DOMAIN_CODE_EXIST="1100967";
	public static final String DOMAIN_NAME_EXIST="1100968";
	public static final String DOMAIN_LIMIT_CROSS="1100969";
	public static final String BATCH_COMM_ERROR_LOG="1100970";
	public static final String BATCH_COMM_SHEETNAME_LABEL="1100971";
	public static final String BATCH_COMM_LINENO_LABEL="1100972";
	public static final String BATCH_COMM_MESSAGE_LABEL="1100973";
	public static final String ADD_OPERATOR_SUCCSSS_APPRV_REQ = "1100974";
	public static final String ADD_OPERATOR_SUCCESS = "1100975";
	public static final String USER_ADD_FAIL = "1100976";
	public static final String INVALID_NETWORK_PREFIX ="1100977";
	public static final String SQL_ERROR_INSERT_USER_PHONE = "1100978";
	public static final String SQL_ERROR_INSERT_USER_GEOGRAPHY = "1100979";
	public static final String SQL_ERROR_INSERT_USER_ROLES = "1100980";
	public static final String SQL_ERROR_INSERT_USER_DOMAIN = "1100981";
	public static final String SQL_ERROR_INSERT_USER_SERVICE_TYPE = "1100982";
	public static final String SQL_ERROR_INSERT_USER_PRODUCTS = "11009783";
	public static final String SQL_ERROR_INSERT_USER_VOUCHER_TYPES = "1100984";
	public static final String INVALID_FILE_TYPES = "1100985";
	public static final String EMPTY_FILE_NAME = "1100986";
	public static final String INVALID_FILE_ATTACHMENT = "1100987";
	public static final String EMPTY_FILE_TYPE = "1100988";
	public static final String SUCCESSFULLY_LOAD_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS ="1100989";
	public static final String FAILED_LOAD_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS ="1100990";
	public static final String SUCCESSFULLY_SAVE_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS ="1100991";
	public static final String FAILED_SAVE_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS ="1100992";
	public static final String SUSPEND_CARD_GROUP="1100993";
	
	public static final String DOMAIN_MANAGEMENT_NOTSAVED="1100994";
	public static final String DOMAIN_ADDED_SUCCEFULLY="1100995";
	public static final String OWNER_CATEGORY_ROLE_NOTSAVED="1100996";
	public static final String OPERATOR_USER_MSISDN_EXIST = "1100997";
	public static final String LOAD_VIEW_TRANSFER_RULE_CARD_GROUP_SUCCESSFULLY = "1100998";
	public static final String LOAD_VIEW_TRANSFER_RULE_CARD_GROUP_FAILED = "1100999";
	public static final String PROMOTRFRULE_ADDTRFRULE_MSG_NOCATEGORYLIST = "1101000";
	public static final String PROMOTRFRULE_ADDTRFRULE_MSG_NOGRADE="1101001";
	public static final String CARDGROUP_SLAB_MESSAGE_AMOUNTSUSPENDED ="1101002";
	public static final String CARDGROUP_C2SCARDGROUPLIST_MESSAGE_NOTRANAFERRULEASSOCIATED ="1101003";
	public static final String 	SAVE_CARD_GROUP_TRANSFER_RULE_VALUE_SUCCESSFULLY ="1101004";
	public static final String  FAILED_SAVE_CARD_GROUP_TRANSFER_RULE_VALUE ="1101005";
	public static final String NO_TRANSFER_RULE_ASSOCIATED= "1101006";
	public static final String SUCCESSFULLY_CHANGE_DEFAULT_CARD_GROUP="1101007";
	public static final String FAILED_CHANGE_DEFAULT_CARD_GROUP="1101008";
	public static final String SELECTED_CARD_GROUP_SET_IS_DEFAULT_CARD_GROUP="1101009";
	public static final String SELECTED_CARD_GROUP_SET_IS_SUSPENDED="1101010";
	public static final String SELECTED_CARD_GROUP_SET_IS_NO_CURRENT_VERSION="1101011";
	public static final String SELECTED_CARD_GROUP_SET_IS_UNABLE_TO_MAKE_DEFAULT="1101012";
	public static final String DOMAIN_STATUS_UPDATED_SUCCEFULLY= "1101013";
	public static final String DOMAIN_ADDED_SUCCEFULLY_LOG_MSG= "1101014";
	public static final String DOMAIN_STATUS_UPDATED_SUCCEFULLY_lOGMSG= "1101015";
	public static final String FILE_ISEMPTY= "1101016";
	public static final String OPERATOR_USER_MODIFY_FAIL = "1101017";
	public static final String OPERATOR_USER_MODIFY_SUCCESS = "1101018";
	public static final String UNABLE_TO_MAKE_DEFAULT_AS_NO = "1101019";
	public static final String INVALID_RECEIVER_TRANSFER_VALUE_WITH_SINGLE_SLAB ="1101020";
	public static final String MODIFIED_CARD_GROUP_VERSION_NUMBER ="1101021";
	public static final String USERDEFAULT_CATEGORY_NOT_FOUND = "1101022";
	public static final String LENGTH_CATEGROY_CODE_ERROR = "1101023";
	public static final String LENGTH_DOMAIN_CODE_ERROR = "1101024";
	public static final String INVALID_TRANSFER_VALUE ="1101025";
	public static final String CATEGORY_CODE_ALREADYEXISTS= "1101026";
	public static final String CATEGORY_NAME_MANDATORYS= "1101027";
	public static final String CATEGORY_CODE_AND_GATEWAYTYPE_ALREADYEXISTS= "1101028";
	public static final String SUCCESSFULLY_LOAD_SELECTOR_AMOUNT_DETAILS="1101029";
	public static final String FAILED_LOAD_SELECTOR_AMOUNT_DETAILS="1101030";
	public static final String SUCCESSFULLY_ADD_SERVICE_PRODUCT_AMOUNT_MAPPING_DETAILS="1101031";
	public static final String FAILED_ADD_SERVICE_PRODUCT_AMOUNT_MAPPING_DETAILS="1101032";
	public static final String SELECTED_PRODUCT_AMOUNT_MAPPING_ALREADY_EXIST="1101033";
	public static final String SERVICE_PRODUCT_AMOUNT_MAPPING_IS_NOT_ALLOWED_TO_MODIFY="1101034";
	public static final String SERVICE_PRODUCT_AMOUNT_MAPPING_MODIFY_SUCCESSFULLY="1101035";
	public static final String SERVICE_PRODUCT_AMOUNT_MAPPING_MODIFY_FAILED="1101036";
	public static final String SERVICE_PRODUCT_AMOUNT_MAPPING_DELETE_SUCCESSFULLY="1101037";
	public static final String SERVICE_PRODUCT_AMOUNT_MAPPING_DELETE_FAILED="1101038";
	public static final String SERVICE_PRODUCT_AMOUNT_MAPPING_DOMAIN_DELETED_SUCCESSFULLY="1101039";
	public static final String SERVICE_PRODUCT_AMOUNT_MAPPING_DOMAIN_MODIFIED_SUCCESSFULLY="1101040";
	public static final String SERVICE_PRODUCT_AMOUNT_MAPPING_DOMAIN_ADDED_SUCCESSFULLY="1101041";
	public static final String AUTOO2C_ENABLED_SUCCEFULLY= "1101042";
	public static final String AUTOO2C_DISABLED_SUCCEFULLY= "1101043";
	public static final String AUTOO2C_TRANSACTION_AMOUNT= "1101044";
	public static final String AUTOO2C_THRESHOLD_LIMIT= "1101045";
	public static final String AUTOO2C_UPDATE_FAILED= "1101046";
	public static final String SERVICE_PRODUCT_AMOUNT_MAPPING_LOAD_SERVICE_AND_PRODUCT_LIST_SUCCESSFULLY="1101047";
	public static final String SERVICE_PRODUCT_AMOUNT_MAPPING_LOAD_SERVICE_AND_PRODUCT_LIST_FAILED="1101048";
	public static final String XLS_SHEET_HEADING = "1101049";
	public static final String BATCH_ID = "1101050";
	public static final String BATCH_NAME = "1101051";
	public static final String INITIATED_BY = "1101052";
	public static final String INITIATED_ON = "1101053";
	public static final String BATCH_STATUS = "1101054";
	public static final String TOTAL_NUMBER = "1101055";
	public static final String USER_ID = "1101056";
	public static final String PARENT_LOGINID = "1101057";
	public static final String PARENT_MSISDN = "1101058";
	public static final String USER_NAME_PREFIX = "1101059";
	public static final String USER_NAME = "1101060";
	public static final String FIRST_NAME = "1101061";
	public static final String LAST_NAME = "1101062";
	public static final String SHORT_NAME = "1101063";
	public static final String CATEGORY_CODE = "1101064";
	public static final String EXTERNAL_CODE = "1101065";
	public static final String CONTACT_PERSON = "1101066";
	public static final String ADDRESS_1 = "1101067";
	public static final String CITY = "1101068";
	public static final String STATE = "1101069";
	public static final String SSN = "1101070";
	public static final String COUNTRY = "1101071";
	public static final String COMPANY = "1101072";
	public static final String FAX = "1101073";
	public static final String EMAIL = "1101074";
	public static final String LANGUAGE = "1101075";
	public static final String LOGINID = "1101076";
	public static final String PASSWORD = "1101077";
	public static final String MOBILE_NUM = "1101078";
	public static final String PIN = "1101079";
	public static final String GEOGRAPHY_CODE = "1101080";
	public static final String GROUP_ROLE_CODE = "1101081";
	public static final String SERVICES = "1101082";
	public static final String COMMISSION_PROFILE = "1101083";
	public static final String TRANSFER_PROFILE = "1101084";
	public static final String OUTLET = "1101085";
	public static final String SUBOUTLET_CODE = "1101086";
	public static final String STATUS = "1101087";
	public static final String REMARKS = "1101088";
	public static final String GRADE = "1101089";
	public static final String MCOMORCE_FLAG = "1101090";
	public static final String MPAY_PROFILE_ID = "1101091";
	public static final String LOW_BAL_ALERT_ALLOW = "1101092";
	public static final String LONGITUDE = "1101093";
	public static final String LATTITUDE = "1101094";
	public static final String DOCUMENT_TYPE = "1101095";
	public static final String DOCUMENT_NO = "1101096";
	public static final String PAYMENT_TYPE = "1101097";
	public static final String TRF_RULE_TYPE_CODE = "1101098";
	public static final String RSA_AUTHENTICATION = "1101099";
	public static final String VOUCHER_TYPE_WITH_COMMENT = "1101100";
	public static final String USER_LOAN_PROFILE_ID = "1101101";
	public static final String MASTER_SHEET_HEADING_BATCH_APPROVE = "1101102";
	public static final String PREFIX_HEADING = "1101103";
	public static final String PREFIX_HEADING_NOTE = "1101104";
	public static final String PREFIX_CODE = "1101105";
	public static final String PREFIX_NAME = "1101106";
	public static final String OUTLET_SUB_OUTLET = "1101107";
	public static final String OUTLET_SUB_OUTLET_HEADING_NOTE = "1101108";
	public static final String OUTLET_CODE = "1101109";
	public static final String OUTLET_NAME = "1101110";
	public static final String SUB_OUTLET_CODE = "1101111";
	public static final String SUB_OUTLET_NAME = "1101112";
	public static final String MASTERSHEET_SERVICES = "1101113";
	public static final String SERVICE_NOTE = "1101114";
	public static final String SERVICE_TYPE = "1101115";
	public static final String SERVICE_NAME = "1101116";
	public static final String AVAILIABLE_GEO_LIST = "1101117";
	public static final String AVAILIABLE_GEO_LIST_NOTE = "1101118";
	public static final String CATEGORY_HIERARCHY = "1101119";
	public static final String CATEGORY_HIERARCHY_NOTE = "1101120";
	public static final String PARENT_CATEGORY = "1101121";
	public static final String CHILD_CATEGORY = "1101122";
	public static final String CATEGORY_HEADING = "1101123";
	public static final String CATEGORY_HEADING_NOTE = "1101124";
	public static final String MASTERSHEET_CATEGORY_CODE = "1101125";
	public static final String MASTERSHEET_CATEGORY_NAME = "1101126";
	public static final String LOW_BALANCE_ALERT_ALLOW = "1101127";
	public static final String GRAPH_DOMAIN_TYPE = "1101128";
	public static final String GRAPH_DOMAIN_TYPE_CODE = "1101129";
	public static final String GRAPH_DOMAIN_TYPE_NAME = "1101130";
	public static final String TRF_CNTRL_PROFILE = "1101131";
	public static final String TRF_CNTRL_PROFILE_CODE = "1101132";
	public static final String TRF_CNTRL_PROFILE_NAME = "1101133";
	public static final String GROUP_ROLE = "1101134";
	public static final String GROUP_ROLE_NAME = "1101135";
	public static final String RSA_HEADING = "1101136";
	public static final String RSA_HEADING_NOTE = "1101137";
	public static final String RSA_ALLOWED = "1101138";
	public static final String MPAY_PROFILE_DETAILS = "1101139";
	public static final String GRADE_CODE = "1101140";
	public static final String M_PAY_PROFILE_ID = "1101141";
	public static final String M_PAY_PROFILE_ID_DESC = "1101142";
	public static final String TRANSFER_RULE_TYPE_HEADING = "1101143";
	public static final String TRANSFER_RULE_TYPE_HEADING_NOTE = "1101144";
	public static final String TRANSFER_RULE_TYPE_CODE = "1101145";
	public static final String TRANSFER_RULE_TYPE_NAME = "1101146";
	public static final String DOCUMENT_TYPE_HEADING = "1101147";
	public static final String DOCUMENT_TYPE_HEADING_NOTE = "1101148";
	public static final String DOCUMENT_TYPE_CODE = "1101149";
	public static final String DOCUMENT_TYPE_NAME = "1101150";
	public static final String PAYMENT_TYPE_HEADING = "1101151";
	public static final String PAYMENT_TYPE_HEADING_NOTE = "1101152";
	public static final String PAYMENT_TYPE_CODE = "1101153";
	public static final String PAYMENT_TYPE_NAME = "1101154";
	public static final String VOUCHER = "1101155";
	public static final String VOUCHER_NOTE = "1101156";
	public static final String VOUCHER_TYPE = "1101157";
	public static final String VOUCHER_NAME = "1101158";
	public static final String LOAN_PROFILE_HEADING = "1101159";
	public static final String LOAN_PROFILE_HEADING_NOTE = "1101160";
	public static final String LOAN_PROFILE_CODE = "1101161";
	public static final String LOAN_PROFILE_NAME = "1101162";
	public static final String LOAN_PROFILE_CATEGORY_ID = "1101163";
	public static final String LOAN_PROFILE_CATEGORY_NAME = "1101164";
	public static final String BATCH_APPROVE_OPTION = "1101165";
	public static final String PARENT_NOT_ACTIVE_DELETE = "1101166";
	public static final String PARENT_NOT_ACTIVE_DISCARD = "1101167";
	public static final String PARENT_NOT_EXIST = "1101168";
	public static final String UPDATE_USER_DETAIL_ERROR = "1101169";
	public static final String UPDATE_CHANNEL_USER_DETAIL_ERROR = "1101170";
	public static final String LOGIN_DETAILS_INFORMATION = "1101171";
	public static final String NEW_PIN_DETAILS = "1101172";
	public static final String BATCH_PROCESS_ALREADY = "1101173";
	public static final String USERS_APPROVED_SUCCESS = "1101174";
	public static final String USER_APPROVAL_FAIL = "1101175";
	public static final String BATCH_REJECT_ALL = "1101176";
	public static final String UPDATE_USER_ERROR = "1101177";
	public static final String REJECT_SUCCESS = "1101178";
	public static final String REJECT_FAIL = "1101179";
	public static final String USERS_APPROVED_MESSAGE = "1101180";
	public static final String MESSAGE_APPROVAL_FAIL = "1101181";
	public static final String CODE = "1101182";
	public static final String NAME = "1101183";
	public static final String INITAITE_FAIL= "1101184";
	public static final String INITAITE_FAIL1= "1101185";
	public static final String WALLET= "1101186";
	public static final String NTWRK_STOCK_DEDUCTION_ADD_SUCCESS= "1101187";
	public static final String SQL_ERROR_INSERT_USER_VOUCHER_SEGMENTS = "1101188";
	public static final String DOMAIN_SAVECATEGORYDETAILS_ERROR_USERIDPREFIX_ALREADYEXISTS="1101189";
	public static final String C2S_REC_TRF_DETAILS_NOT_FOUND = "1101190";
	public static final String C2S_REC_SUCCESSFULL = "1101191";
	public static final String C2S_REC_UNSUCCESSFULL = "1101192";
	public static final String C2S_REC_NO_LIST_FOUND = "1101193";
	public static final String C2S_REC_WENT_WRONG = "1101194";
	public static final String USER_ID_REQUIRED = "1101195";
	public static final String USER_ID_INVALID = "1101196";
	public static final String MISSING_COMM_PROFILE = "1101197";
	public static final String COMM_PROFILE_INVALID_CATEGORY = "1101198";
	public static final String TRF_PROFILE_MISSING = "1101199";
	public static final String TRF_PROFILE_INVALID_CATEGORY = "1101200";
	public static final String MISSING_GRADE = "1101201";
	public static final String INVALID_GRADE_CODE = "1101202";
	public static final String MPAY_PROFILE_MISSING = "1101203";
	public static final String INVALID_MPAY_PRF_ID = "1101204";
	public static final String TRANSFER_RULE_CODE_MISSING = "1101205";
	public static final String TRANSFER_RULE_CODE_INVALID = "1101206";
	public static final String RSA_ID_NULL = "1101207";
	public static final String RSA_ID_BLANK = "1101208";
	public static final String RSA_AUTH_NOT_ALLOWED = "1101209";
	public static final String STATUS_INVALD = "1101210";
	public static final String REMARKS_MAX_LENGTH = "1101211";
	public static final String INVALID_SHEET="1101212";
	public static final String VOUCHER_GROUP_STATUS_UPDATED_SUCCESSFULLY="1101213";
	public static final String VOUCHER_GROUP_STATUS_UPDATION_FAILED="1101214";
	public static final String VOUCHER_GROUP_DETAILS_ADDED_SUCCESSFULLY="1101215";
	public static final String VOUCHER_GROUP_DETAILS_ADD_FAILED="1101216";
	public static final String VOUCHER_GROUP_MODIFIED_SUCCESSFULLY="1101217";
	public static final String VOUCHER_GROUP_MODIFY_FAILED ="1101218";
	public static final String VOUCHER_GROUP_DETAILS_LOAD_SUCCESSFULLY="1101219";
	public static final String VOUCHER_GROUP_DETAILS_LOAD_FAILED="1101220";
	public static final String SUCCESSFULLY_MODIFIED_DEFAULT_VOUCHER_GROUP_SET="1101221";
	public static final String FAILED_MODIFY_VOUCHER_GROUP_SET="1101222";
	public static final String SUCCESSFULLY_CALCULATE_VOUCHER_GROUP_TRANSFER_VALUE="1101223";
	public static final String FAILED_CALCULATE_VOUCHER_GROUP_TRANSFER_VALUE="1101224";
	public static final String SUCCESSFULLY_LOAD_VOUCHER_SERVICES="1101225";
	public static final String FAILED_LOAD_VOUCHER_GROUP_SERVICES="1101226";
	public static final String SUCCESSFULLY_LOAD_VOUCHER_GROUP_VERSION_DETAILS="1101227";
	public static final String FAILED_LOAD_VOUCHER_GROUP_VERSION_DETAILS="1101228";
	public static final String SUCCESSFULLY_LOAD_VOUCHER_GROUP_DROP_DOWN="1101229";
	public static final String FAILED_LOAD_VOUCHER_GROUP_DROP_DOWN="1101230";
	public static final String SUCCESSFULLY_LOAD_VOUCHER_GROUP_SEGMENT_LIST="1101231";
	public static final String FAILED_LOAD_VOUCHER_GROUP_SEGMENT_LIST="1101232";
	public static final String SUCCESSFULLY_LOAD_VOUCHER_GROUP_DENOMINATION_LIST="1101233";
	public static final String FAILED_LOAD_VOUCHER_GROUP_DENOMINATION_LIST	="1101234";
	public static final String SUCCESSFULLY_LOAD_VOUCHER_GROUP_DENOMINATION_PROFILE_LIST="1101235";
	public static final String FAILED_LOAD_VOUCHER_GROUP_DENOMINATION_PROFILE_LIST="1101236";
	public static final String SUCCESSFULLY_LOAD_VOUCHER_GROUP_VERSION_NUMBERS="1101237";
	public static final String FAILED_LOAD_VOUCHER_GROUP_VERSION_NUMBERS="1101238";
	public static final String NO_SEGMENT_DETAILS_FOUND_WITH_GIVEN_DATA="1101239";
	public static final String NO_DENOMINATION_DETAILS_FOUND_WITH_GIVEN_DATA="1101240";
	public static final String NO_DENOMINATION_PROFILE_DETAILS_FOUND_WITH_GIVEN_DATA="1101241";
	public static final String STOCK_DEDUCTION_APPROVED ="1101242";
	public static final String STOCK_DEDUCTION_REJECTED ="1101243";
	public static final String STOCK_DEDUCTION_REJECTED_FAIL ="1101244";
	public static final String STOCK_DEDUCTION_REJECTED_FAIL1 ="1101245";
	public static final String SUCCESSFULLY_DELETED_VOUCHER_CARD_GROUP_SET="1101246";
	public static final String FAILED_DELETE_VOUCHER_CARD_GROUP_SET="1101247";
	public static final String INTERFACES_ADDINTERFACE_EXTERNALID_ALREADYEXISTS ="1101248";
	public static final String INTERFACES_ADDINTERFACE_ADD_SUCCESS ="1101249";
	public static final String INTERFACES_ADDNODE_FAILED ="1101250";
	public static final String INTERFACES_MODIFY_INTERFACE_SUCCESS ="1101251";
	public static final String INTERFACES_DELETE_INTERFACENWKPREFIXFOUND_ERROR ="1101252";
	public static final String INTERFACES_DELETE_INTERFACENETWORKMAPPINGFOUND_ERROR ="1101253";
	public static final String INTERFACES_DELETE_IATINTERFACECOUNTRYMAPPING_ERROR ="1101254";
	public static final String INTERFACES_DELETE_SUCCESS ="1101255";
	public static final String INTERFACES_DELETE_NOTSUCCESS ="1101256";
	public static final String ADD_INTERFACE_DETAIL_FAIL ="1101257";
	public static final String INTERFACES_ADDINTERFACE_ALREADYEXISTS ="1101258";
	public static final String INTERFACES_DELETE_NODE_FAILED ="1101259";
	public static final String O2C_TRANSFER_REVERSAL_ENQUIRY ="1101260";
	public static final String ASSOCIATE_INTERFACE_PREFIX_SUCCESS ="1101261";
	public static final String ASSOCIATE_INTERFACE_PREFIX_FAIL ="1101262";
	public static final String SUCCESSFULY_ADD_VOUCHER_CARD_GROUP_LOG="1101263";
	public static final String VOUCHER_CARDGROUP_INVALID_SLAB="1101264";
	public static final String COULD_NOT_UPLOAD_THE_FILE="1101265";
	public static final String SERVICE_CODE_ALREADY_EXISTS = "1101266";
	public static final String SERVICE_NAME_ALREADY_EXISTS = "1101267";
	public static final String SERVICE_CLASS_ADDED = "1101268";
	public static final String SERVICE_CLASS_NOT_ADDED = "1101269";
	public static final String INVALID_PIN1 = "1101270";
	public static final String VOUCHER_CARDGROUP_RECIVER_TRANSFER_VALUE_INVALID_SLAB="1101271";
    public static final String SERVICE_CLASS_NOT_MODIFIED = "1101272";
    public static final String SERVICE_CLASS_MODIFIED = "1101273";
    public static final String SERVICE_CLASS_NOT_DELETED_TRF = "1101274";
    public static final String SERVICE_CLASS_NOT_DELETED = "1101275";
    public static final String SERVICE_CLASS_DELETED = "1101276";
	public static final String ASSOCIATE_INTERFACE_PREFIX_VALIDATION_SERIES_PREPAID_DUPLICATED = "1101277";
	public static final String ASSOCIATE_INTERFACE_PREFIX_VALIDATION_SERIES_POSTAID_DUPLICATED = "1101278";
	public static final String ASSOCIATE_INTERFACE_PREFIX_UPDATION_SERIES_PREPAID_DUPLICATED = "1101279";
	public static final String ASSOCIATE_INTERFACE_PREFIX_UPDATION_SERIES_POSTPAID_DUPLICATED = "1101280";
	public static final String VOUCHER_CARD_GROUP_CHANGE_DEFAULT_CARD_GROUP_SET = "1101281";
	public static final String VOUCHER_CARD_GROUP_MODIFY_LOG = "1101282";
	public static final String VOUCHER_CARD_GROUP_SHOULD_BE_CURRENTLY_APPLICABLE = "1101283";
	public static final String VOUCHER_CARD_GROUP_SET_ALREADY_EXISTS_WITH_THE_SAME_APPLICABLE_DATE = "1101284";
	public static final String VOUCHER_CARD_GROUP_SET_APPLICABLE_DATE_NOT_FEATURE_DATE_OR_NOT_LATEST_VERSION = "1101285";
	public static final String NO_CORPORATE_DOMAIN_ASSOCIATED = "1101286";
	public static final String NO_CATEGORY_EXIST="1101287";
	public static final String NO_USER_FOUND="1101288";
	public static final String RESTRICTED_SUBSCRIBER_MSISDN_INVALID="1101289";
	public static final String RESTRICTED_SUBSCRIBER_MSISDN_NOT_FOUND="1101290";
	public static final String INVALID_RESTRICTED_MSISDNS="1101291";
	public static final String RESTRICTED_SUBSCRIBER_DELETION_SUCCESS="1101292";
	public static final String NO_SUBSCRIBER_FOUND = "1101293";
	public static final String SUBSCRIBER_DELETION_FAILED="1101294";
	public static final String INTERFACES_DELETE_DOES_NOT_EXIST = "1101295";
	public static final String SUCCESSFULLY_LOAD_VIEW_RESTRICTED_LIST ="1101296";
	public static final String FAILED_LOAD_VIEW_RESTRICTED_LIST = "1101297"; 
	public static final String FILE_UPLOADED_SUCCESSFULLY = "1101298";
	public static final String FILE_UPLOAD_FAILED ="1101299";
	public static final String FILE_CONTENT_IS_IN_VALID ="1101300";
	public static final String FILE_COULD_NOT_BE_UPLOADED_TRY_A_VALID_FILE = "1101301";
	public static final String THE_FILE_DOES_NOT_EXISTS_OR_THE_FILE_HAS_NO_DATA_PLEASE_USE_PROPER_FILE_WITH_VALID_DATA = "1101302";
	public static final String UNABLE_TO_CREATE_UPLOAD_FILE_DIRECTORY = "1101303";
	public static final String OTHER_FILE_SIZE_IS_MISSING_IN_CONTENT_PROPERTY_FILE = "1101304";
	public static final String START_TAG_NOT_FOUND_IN_THE_FILE = "1101305";
	public static final String END_TAG_NOT_FOUND_IN_THE  = "1101306";
	public static final String A_FILE_WITH_SAME_NAME_ALREADY_EXISTS_ON_THE_SERVER_PLEASE_SELECT_ANOTHER_FILE_NAME= "1101307";
	public static final String FILE_SIZE_SHOULD_NOT_BE_MORE_THAN_BYTE= "1101308"; 
	public static final String UPLOAD_FILE_PATH_IS_MISSING_IN_CONSTANT_PROPERTY_FILE= "1101309";
	public static final String MAXIMUM_FILE_SIZE_LIMIT_IS_MISSING_IN_CONSTANTS_FILE= "1101310";
	public static final String NUMBER_OF_RECORDS_IS_NOT_MATCHING_WITH_THE_ACTUAL_NUMBER_OF_RECORDS_IN_FILE= "1101311";
	public static final String THE_NUMBER_OF_RECORDS_IN_THE_FILE_EXCEEDS_THE_MAXIMUM_LIMIT= "1101312";
	public static final String MOBILE_NUMBER_CANNOT_BE_FILETERED_NOW_PLEASE_CONTACT_SYSTEM_ADMIN= "1101313";
	public static final String SUCCESSFULLY_LOAD_APPROVAL_RESTRICTED_LIST= "1101314";
	public static final String FAILED_LOAD_APPROVAL_RESTRICTED_LIST= "1101315";
	public static final String SUCCESSFULLY_CHANGE_STATUS_OF_APPROVAL_RESTRICTED_LIST= "1101316";
	public static final String FAILED_CHANGE_STATUS_OF_APPROVAL_RESTRICTED_LIST= "1101317";
	public static final String RSC_DUPLICATE_MSISDN = "1101318";
	public static final String RSC_INVALID_MSISDN = "1101319";
	public static final String NETWORK_PREFIX_NOT_FOUND = "1101320";
	public static final String UNSUPPORTED_NETWORK ="1101321";
	public static final String MOBILE_NUMBER_ALREADY_EXISTS_UNDER_THE_OWNER_USER="1101322";
	public static final String CANNOT_REGISTER_THE_SUBSCRIBER = "1101323";
	public static final String MSISDN_SERIES_IS_NOT_FOUND_OF_SELECTED_TYPE = "1101324";
	public static final String COMM1 = "1101325";
	public static final String COMM2 = "1101326";
	public static final String TRANSACTION_FAIL = "1101327";
	public static final String POD_1 = "1101328";
	public static final String POD_2 = "1101329";
	public static final String POD_3 = "1101330";
	public static final String POD_4 = "1101331";
	public static final String UNBLACK_SUCCESS = "1101332";
	public static final String UNBLACK_FAIL = "1101333";
	public static final String NO_SUBS_FOUND = "1101334";
	public static final String UNBLACK_SELECTED_FAILURE = "1101335";
	public static final String MSISDN_REQ = "1101336";
	public static final String OWNER_ID_REQ = "1101337";
	public static final String P2P_PAYER_REQ = "1101338";
	public static final String P2P_PAYEE_REQ = "1101339";
	public static final String C2S_PAYEE_REQ = "1101340";
	public static final String MSISDN_STRING_LENGTH_EXCEEDED ="1101341";
	public static final String SUBSCRIBER_LIST_EMPTY = "1101342";
	public static final String DUPLICATED_MSISDN = "1101343";
	public static final String NO_VALID_MSISDN = "1101344";
	public static final String NO_PREFIX_FOUND="1101345";
	public static final String NO_ETS_SUPPORT="1101346";
	public static final String ALREADY_UNBLACK_CP2P_PAYER="1101347";
	public static final String ALREADY_UNBLACK_CP2P_PAYEE="1101348";
	public static final String ALREADY_UNBLACK_C2S_PAYEE="1101349";
	public static final String RESTRICTED_SUBSCRIBER_LIST_APPROVED_SUCCESSFULLY = "1101350";
	public static final String RESTRICTED_SUBSCRIBER_LIST_APPROVED_FAILED = "1101351";
	public static final String RESTRICTED_SUBSCRIBER_LIST_REJECTED_SUCCESSFULLY = "1101352";
	public static final String RESTRICTED_SUBSCRIBER_LIST_REJECTED_FAILED = "1101353";
	public static final String RESTRICTED_SUBSCRIBER_LIST_APPROVED_PARTIALLY_SUCCESSFULLY = "1101354";
	public static final String RESTRICTED_SUBSCRIBER_LIST_REJECTED_PARTIALLY_SUCCESSFULLY = "1101355";
	public static final String SUBSCRIBER_ALREADY_BLACKLISTED = "1101356";
	public static final String SUBSCRIBER_BLACKLIST_SUCCESS = "1101357";
	public static final String BLACKLIST_SINGLE_SUB_ERROR = "1101358";
	public static final String BLACKLIST_ALL_SUB_ERROR = "1101359";
	public static final String BLACKLIST_ALL_SUBS_NO_SUBS_FOUND = "1101360";
	public static final String BLACKLIST_SUBSCRIBER_FAIL = "1101361";
	public static final String INVALID_PREFIX = "1101362";
	public static final String UNSUPPORTED_NETWORK_ERROR = "1101363";
	public static final String USER_NAME_REQ="1101364";
	public static final String NUMBER_OF_RECORDS_MUST_BE_GREATER_THAN_ZERO = "1101365";
	public static final String UPLOAD_RESTRICTED_LIST_PARTIALLY_SUCCESSFULLY = "1101366";
	public static final String MSISDN_PROCESSED_SUCCESSFULLY = "1101367";
	public static final String MSISDN_PROCESSED_FAILED = "1101368";
	public static final String RESTRICTEDSUBS_BLACKLISTING_ERROR_FILEPATHMISSINGINCONS = "1101369";
	public static final String RESTRICTEDSUBS_BLACKLISTING_ERROR_MAXBLACKLISTMISSINGINCONS = "1101370";
	public static final String RESTRICTEDSUBS_BLACKLISTING_ERROR_BLACKLISTSIZEINVALID = "1101371";
	public static final String RESTRICTEDSUBS_BLACKLISTING_ERROR_ERRORINITIAZING = "1101372";
	public static final String RESTRICTEDSUBS_BLACKLISTING_ERROR_NOSTART = "1101373";
	public static final String RESTRICTEDSUBS_BLACKLISTING_ERROR_NOEND = "1101374";
	public static final String RESTRICTEDSUBS_BLACKLISTING_ERROR_MAXSIZEREACHED = "1101375";
	public static final String RESTRICTEDSUBS_PROCESSUPLOADEDBLACKLISTFILE_ERROR_DULPLICATEMSISDN = "1101376";
	public static final String RESTRICTEDSUBS_BLACKLISTING_ERROR_FILENOMOVE = "1101377";
	public static final String RESTRICTEDSUBS_BLACKLISTING_MESSAGE_SUCCESS = "1101378";
	public static final String RESTRICTEDSUBS_BLACKLISTING_ERROR_FILENOTUPLOADED = "1101379";
	public static final String RESTRICTEDSUBS_PROCESSUPLOADEDBLACKLISTFILE_ERROR_INVALIDMSISDN = "1101380";
	public static final String RESTRICTEDSUBS_PROCESSUPLOADEDBLACKLISTFILE_ERROR_MSISDNNOTEXISTS = "1101381";
	public static final String RESTRICTEDSUBS_PROCESSUPLOADEDBLACKLISTFILE_ERROR_MSISDNALREADYBLACKLIST = "1101382";
	public static final String BLACKLIST_SUBSCRIBER_FAILED = "1101383";
	public static final String GRADE_EXISTS_FOR_CATEGORY = "1101384";
	public static final String RESTRICTED_SUBSCRIBER_APPROVED_SUCCESSFULLY = "1101385";
	public static final String RESTRICTED_SUBSCRIBER_APPROVED_FAILED = "1101386";
	public static final String RESTRICTED_SUBSCRIBER_REJECTED_SUCCESSFULLY = "1101387";
	public static final String RESTRICTED_SUBSCRIBER_REJECTED_FAILED = "1101388";
	public static final String OPERATOR_USER_DELETE_SUCCESSFULLY = "1101389";
	public static final String OPERATOR_USER_DELETE_FAILED = "1101390";
	public static final String OPERATOR_USER_DELETE_PARTIALLY_SUCCESS = "1101391";
	public static final String O2C_RECON_LIST_NOT_FOUND = "1101392";
	public static final String O2C_RECON_WENT_WRONG = "1101393";
	public static final String O2C_RECON_DATE_INVALID = "1101394";
	public static final String FILE_DOES_NOT_CONTAIN_DATA = "1101395";
	public static final String NO_SERVICE_EXIST = "1101396";
	public static final String NO_INTERFACE_EXIST = "1101397";
	public static final String NO_SERVICE_INTERFACE_MAP_LIST_EXIST = "1101398";
	public static final String CONFIGURATION_NOT_AVAILABLE_FOR_SERVICE = "1101399";
	public static final String NO_PRODUCT_EXIST="1101400";
	public static final String VALIDATION_SERIES_PREPAID_NOT_VALID="1101401";
	public static final String VALIDATION_SERIES_PREPAID_DUPLICATED="1101402";
	public static final String VALIDATION_SERIES_POSTPAID_NOT_VALID="1101403";
	public static final String VALIDATION_SERIES_POSTPAID_DUPLICATED="1101404";
	public static final String UPDATION_SERIES_PREPAID_NOT_VALID="1101405";
	public static final String UPDATION_SERIES_PREPAID_DUPLICATED="1101406";
	public static final String UPDATION_SERIES_POSTPAID_NOT_VALID="1101407";
	public static final String UPDATION_SERIES_POSTPAID_DUPLICATED="1101408";
	public static final String NO_INTERFACES_EXIST="1101409";
	public static final String NO_DATA_FOUND="1101410";
	public static final String REMOVE_ERROR = "1101411";
	public static final String VALIDATE_PREPAID_SERIES_ALREADY_EXIST = "1101412";
	public static final String VALIDATE_POSTPAID_SERIES_ALREADY_EXIST="1101413";
	public static final String UPDATE_PREPAID_SERIES_ALREADY_EXIST = "1101414";
	public static final String UPDATE_POSTPAID_SERIES_ALREADY_EXIST = "1101415";
	public static final String PRODUT_SERVICE_INTERFACE_MAPPING_ADDITION_SUCCESS = "1101416";
	public static final String PRODUT_SERVICE_INTERFACE_MAPPING_ADDITION_FAIL = "1101417";

	public static final String LOAD_SERVICE_TYPE_SELECTOR_MAPPING_DROP_DOWN_SUCCESSFULLY="1101418";
	public static final String LOAD_SERVICE_TYPE_SELECTOR_MAPPING_DROP_DOWN_FAILED="1101419";
	public static final String LOAD_SERVICE_TYPE_SELECTOR_MAPPING_LIST_SUCCESSFULLY="1101420";
	public static final String LOAD_SERVICE_TYPE_SELECTOR_MAPPING_LIST_FAILED= "1101421";
	public static final String SERVICE_TYPE_SELECTOR_MAPPING_ADDED_SUCCESSFULLY="1101422";
	public static final String SERVICE_TYPE_SELECTOR_MAPPING_ADD_FAILED= "1101423";
	public static final String SERVICE_TYPE_SELECTOR_MAPPING_MODIFIED_SUCCESSFULLY="1101424";
	public static final String SERVICE_TYPE_SELECTOR_MAPPING_MODIFY_FAILED="1101425";
	public static final String SERVICE_TYPE_SELECTOR_MAPPING_DELETED_SUCCESSFULLY="1101426";
	public static final String SERVICE_TYPE_SELECTOR_MAPPING_DELETE_FAILED="1101427";
	public static final String MAX_NO_OF_PRODUCTS_ALLOWED_PER_SERVICE_LIMIT_REACHED="1101428";
	public static final String PLEASE_ENTER_PRODUCT_CODE_IN_ALPHANUMERIC_FORMAT="1101429";
	public static final String DEFAULT_MAPPING_ALREADY_EXIST_FOR_THIS_SERVICE="1101430";
	public static final String PRODUCT_NAME_OR_PRODUCT_CODE_ALREADY_EXISTS="1101431";
	public static final String SENDER_SUBSCRIBER_TYPE_INVALID="1101432";
	public static final String RECIVER_SUBSCRIBER_TYPE_INVALID="1101433";
	public static final String STATUS_IS_INVALID="1101434";
	public static final String DEFAULT_IS_INVALID="1101435";
	public static final String SENDER_SUBSCRIBER_TYPE_NULL="1101436";
	public static final String RECIVER_SUBSCRIBER_TYPE_NULL="1101437";
	public static final String STATUS_IS_NULL="1101438";
	public static final String DEFAULT_IS_NULL="1101439";
	public static final String SERVICE_TYPE_SELECTOR_MAPPING_SUSPENDED="1101440";
	public static final String SNO_NULL = "1101441";
	public static final String LOAD_SERVICE_TYPE_SELECTOR_MAPPING_SUCCESSFULLY="1101442";
	public static final String LOAD_SERVICE_TYPE_SELECTOR_MAPPING_FAILED= "1101443";
	public static final String SERVICE_TYPE_NAME_INVALID= "1101444";
	public static final String SERVICE_TYPE_NAME_IS_NULL= "1101445";
	public static final String SERVICE_TYPE_IS_NULL= "1101446";
	public static final String PRODUT_SERVICE_INTERFACE_MAPPING_MODIFICATION_SUCCESS = "1101447";
	public static final String PRODUT_SERVICE_INTERFACE_MAPPING_MODIFICATION_FAIL = "1101448";
	public static final String ERROR_SELECTOR_CODE = "1101449";
	public static final String INTERFACE_ID_REQUIRED = "1101450";
	public static final String VALIDATE_OR_UPDATE_SERIES_REQUIRED = "1101451";
	public static final String PRODUT_SERVICE_INTERFACE_MAPPING_DELETION_SUCCESS = "1101452";
	public static final String PRODUT_SERVICE_INTERFACE_MAPPING_DELETION_FAIL = "1101453";
	public static final String TEMP_DOWNLOAD_C2S= "1101454";
	public static final String TEMP_DOWNLOAD_C2S1= "1101455";
	public static final String NO_MAPPING_FOUND= "1101456";
	public static final String SERVICE_TYPE_SELECTOR_MAPPING_INACTIVE= "1101457";
	public static final String SERVICE_TYPE_INACTIVE= "1101458";
	public static final String PATH_NOT_DEFINED1 ="1101459";
	public static final String FILE_NAME_ERROR ="1101460";
	public static final String MAX_ROW ="1101461";
	public static final String DUPLICATE ="1101462";
	public static final String SENDER_SUBSCRIBER_TYPE_IS_EMPTY = "1101463";
	public static final String RECEIVER_SUBSCRIBER_TYPE_IS_EMPTY = "1101464";
	public static final String STATUS_FIELD_IS_EMPTY = "1101465";
	public static final String DEFAULT_FIELD_IS_EMPTY = "1101466";
	public static final String SERVICE_TYPE_IS_EMPTY = "1101467";
	public static final String SERVICE_TYPE_NAME_IS_EMPTY = "1101468";
	public static final String SERVICE_TYPE_IS_INVALID = "1101469";
	public static final String PRODUCT_CODE_LENGTH_SHOULD_BE_BELOW_CHARACTERS = "1101470";
	public static final String PRODUCT_NAME_LENGTH_SHOULD_BE_BELOW_CHARACTERS = "1101471";
	public static final String PRODUCT_CODE_IS_NULL = "1101472";
	public static final String PRODUCT_NAME_IS_NULL = "1101473";
	public static final String PRODUCT_CODE_IS_EMPTY = "1101474";
	public static final String PRODUCT_NAME_IS_EMPTY = "1101475";
	public static final String SNO_IS_EMPTY = "1101476";
	public static final String SNO_IS_NULL = "1101477";
	public static final String MOBILE_NUMBER_IS_NOT_SUPPORT_FROM_YOUR_NETWORK= "1101478";
	public static final String NO_INFORMATION_FOUND_FOR_ICCID_IMSI= "1101479";
	public static final String ICC_ID_ASSOCIATED_WITH_MSISDN_IS_NOT_FROM_YOUR_NETWORK= "1101480";
	public static final String WARNING_ICCID_IMSI_IS_ALREADY_MAPPED_TO_MSISDN_AND_FOLLOWING_ARE_THE_DETAILS= "1101481";
	public static final String NO_SUCH_RECORD_FOR_ICCID_IMSI_FOUND_IN_THE_DATABASE= "1101482";
	public static final String ICC_ID_IS_NOT_FROM_YOUR_NETWORK= "1101483";
	public static final String ICCID_IMSI_IS_ALREADY_MAPPED_WITH_MSISDN= "1101484";
	public static final String ICCID_IS_NULL = "1101485";
	public static final String ICCID_IS_EMPTY = "1101486";
	public static final String MSISDN_IS_NULL = "1101487";
	public static final String MSISDN_IS_EMPTY = "1101488";
	public static final String MAPPING_DETAILS_ADDED_SUCCESSFULLY = "1101489";
	public static final String MAPPING_DETAILS_ADD_FAILED = "1101490";
	public static final String MAPPING_DETAILS_RE_ASSOCIATED_SUCCESSFULLY = "1101491";
	public static final String MAPPING_DETAILS_RE_ASSOCIATED_FAILED = "1101492";
	public static final String PREPAID_SERIES_REQ = "1101493";
	public static final String POSTPAID_SERIES_REQ = "1101494";
	public static final String SERIES_MAP_REQ = "1101495";
	public static final String SELECTOR_CODE_REQ = "1101496";
	public static final String SERVICE_TYPE_REQ = "1101497";
	public static final String INTERFACE_ID_REQ = "1101498";

	public static final String BATCH_SUCCESS = "1101499";
	public static final String DEFAULT_SELECTOR_CAN_NOT_BE_SUSPENDED = "1101500";

	public static final String INVALID_INTERFACE_TYPE="1101501";
	public static final String DUPLICATE_VALUE = "1101502";
	public static final String INVALID_INTERFACE_ID="1101503";
	public static final String ICCID_IMSI_DELETED_SUCCESSFULLY = "1101504";
	public static final String ICCID_IMSI_DELETE_FAILED = "1101505";
	public static final String ICCID_IMSI_IS_ASSOCIATED_WITH_THE_MOBILE_NUMBER_WHICH_IS_ASSOCIATED_WITH_USER= "1101506";
	public static final String ICCID_IMSI_IS_ASSOCIATED_WITH_THE_MOBILE_NUMBER = "1101507";
	public static final String MESSAGE_MANAGEMENT_LABEL_INVALIDMESSAGECODE = "1101508";
	public static final String LOAD_MESSAGE_MANAGEMENT_LABEL_FAILED = "1101509";
	public static final String LOAD_MESSAGE_MANAGEMENT_LABEL_SUCCESS = "1101510";
	public static final String MESSAGES_MESSAGESMANAGEMENT_ERROR_SPECIALCHARS = "1101511";
	public static final String MESSAGE_MANAGEMENT_SUCCESSFULLY_MODIFIED = "1101512";
	public static final String MESSAGE_MANAGEMENT_MODIFY_FAILED = "1101513";
	public static final String MESSAGE_NOT_EDITABLE = "1101514";
	public static final String MESSAGE_DOWNLOADED_SUCCESSFULLY = "1101515";
	public static final String INVALID_UPLOADFILE_MSG_UNSUCCESSUPLOAD = "1101516";
	public static final String MESSAGES_MESSAGESMANAGEMENT_ERROR_PATHNOTDEFINED = "1101517";
	public static final String MESSAGES_MESSAGESMANAGEMENT_ERROR_NORECORDINFILE = "1101518";
	public static final String  MESSAGES_MESSAGESMANAGEMENT_ERROR_NTCODEMISSING = "1101519";
	public static final String  MESSAGES_MESSAGESMANAGEMENT_ERROR_MSGCODEMISSING = "1101520";
	public static final String  MESSAGES_MESSAGESMANAGEMENT_ERROR_DEFAULTMSGMISS = "1101521";
	public static final String  MESSAGES_MESSAGESMANAGEMENT_UPDATE_MSG_FAIL = "1101522";
	public static final String  MESSAGES_MESSAGESMANAGEMENT_ERROR_CONVERT_2D_ARRAY = "1101523";
	public static final String  MESSAGES_MESSAGESMANAGEMENT_MSG_SUCCESS = "1101524";
	public static final String  MESSAGES_MESSAGESMANAGEMENT_ERROR_FILENOTUPLOADED = "1101525";
	public static final String  MESSAGES_XLSHEADING_LABEL_NETWORKCODE = "1101526";
	public static final String  MESSAGES_XLSHEADING_LABEL_KEY = "1101527";
	public static final String  MESSAGE_MANAGEMENT_LABEL_DEFAULTMESSAGE = "1101528";
	public static final String  MESSAGES_XLSHEADING_LABEL_ARGUMENTS = "1101529";
	public static final String  MESSAGES_XLSHEADING_LABEL_MSG = "1101530";
	public static final String  MESSAGES_MESSAGESMANAGEMENT_DOWNLOAD_FAILED = "1101531";
	public static final String  MESSAGES_MESSAGESMANAGEMENT_UPLOAD_FAILED = "1101532";
	public static final String LOGIN_ID_BLANK = "1101533";
	public static final String USER_PROCESSUPLOADEDFILE_MSG_PARTIAL_SUCCESS = "1101534";
	public static final String USER_PROCESSUPLOADEDFILE_MSG_SUCCESS = "1101535";
	public static final String EXTERNAL_CODE_INVALID_CCE = "1101536";
	public static final String EXTERNAL_CODE_USER_SAME_LVL = "1101537";
	public static final String EXTERNAL_CODE_USER_DOMAIN = "1101538";
	public static final String EXTERNAL_CODE_USER_DELETED = "1101539";
	public static final String EXTERNAL_CODE_GEO_ERROR = "1101540";
	public static final String SERVICEGROUP_UPLOAD_VALIDATE_FILE_FILENAME_LENGTH="1101541";
	public static final String ALL_LIST= "1101542";
	public static final String C2S_REPORTS_O2C_AND_C2C_RETURN_WITHDRAW_COMBO_IN= "1101543";
	public static final String C2S_REPORTS_O2C_AND_C2C_RETURN_WITHDRAW_COMBO_OUT= "1101544";
	public static final String MASTER_BULKUPDATESIMTXNID_ERROR_FILENOTUPLOADED= "1101545";
	public static final String MASTER_BULKAMBIGUOUSSETTLEMENT_MESSAGE_FILENAMEINVALID= "1101546";
	public static final String BULKAMBIGUOUSSETTLEMENT_MESSAGE= "1101547";
	public static final String FILE_NAME_INVALID= "1101548";
	public static final String FIlE_SUCCESS= "1101549";
	public static final String INVALID_APP= "1101550";
	public static final String INVALID_APP1= "1101551";
	public static final String INVALID_APP2= "1101552";
	public static final String NOT_GET_RECORD= "1101553";
	public static final String NOT_GET_RECORD1= "1101554";
	public static final String NO_DATE1= "1101555";
	public static final String NO_DATE2= "1101556";
	public static final String NO_DATE3= "1101557";
	public static final String NO_SIM_1= "1101558";
	public static final String NO_SIM_2= "1101559";
	public static final String NO_SIM_3= "1101560";
	public static final String NO_SIM_4= "1101561";
	public static final String NO_DATES1= "1101562";
	public static final String NO_DATES2= "1101563";
	public static final String NO_DATES3= "1101564";
	public static final String ICCID_LENGTH= "1101565";
	public static final String ICCID_LENGTH1= "1101566";
	public static final String ICCID_KEY= "1101567";
	public static final String ICCID_ALREADY= "1101568";
	public static final String ICCID_INFO= "1101569";
	public static final String ICCID_NO_PROCESS= "1101570";
	public static final String N0_RECORD= "1101571";
	public static final String NO_RECORD1= "1101572";
	public static final String IMPROPER_FILE_FORMAT= "1101573";
	public static final String NO_MOVE= "1101574";
	public static final String NOT_ABLE_TO_RETRIEVE_RECORDS ="1101575";
	public static final String IMSI_LENGTH_AT_LINE_NUMBER_CANNOT_BE_GREATER_THAN_CHARACTERS ="1101576";
	public static final String MOBILE_NUMBER_IS_NOT_VALID ="1101577";
	public static final String MSISDN_IS_FROM_UNSUPPORTED_NETWORK ="1101578";
	public static final String MSISDN_IS_NOT_UPDATED_FOR_ICCID_IMSI ="1101579";
	public static final String ICCID_IMSI_ALREADY_HAS_MSISDN_ASSOCIATED_WITH_IT ="1101580";
	public static final String ICCID_IMSI_IS_FROM_UNSUPPORTED_NETWORK_AT_LINE_NUMBER = "1101581";
	public static final String NO_RECORD_FOUND_FOR_ICCID_IMSI_IN_THE_DATABASE= "1101582";
	public static final String RECORD_ALREADY_EXISTS_FOR_MSISDN= "1101583";
	public static final String MOBILE_NUMBER_ALREADY_ASSOCIATED_WITH_A_KEY_FOR_REASSOCIATION_USE_ASSOCIATE_MSISDN_WITH_ICCID_IMSI_OPTION= "1101584";
	public static final String FILE_CANNOT_BE_MOVED_FOR_BACKUP_PURPOSE= "1101585";
	public static final String RECORD_IN_PROPER_SYNTAX= "1101586";
	public static final String MESSAGE_CHANNELTRANSFER_APPROVEDQUANTITY_NOTINSLAB = "1101587";
	public static final String MESSAGE_CHANNELTRANSFER_EXTERNALTXNNUMBERNOTNUMERIC = "1101588";
	public static final String CHANNELTRANSFER_TRANSFERDETAILAPPROVALLEVELONE_LABEL_APPROVERREMARK = "1101589";
	public static final String O2C_EMAIL_NOTIFICATION_FROM = "1101590";
	public static final String O2C_EMAIL_NOTIFICATION_CONTENT = "1101591";
	public static final String O2C_EMAIL_CHANNELUSER_DETAILS = "1101592";
	public static final String O2C_EMAIL_TRANSFERID = "1101593";
	public static final String O2C_EMAIL_CHANNELUSER_NAME = "1101594";
	public static final String O2C_EMAIL_CHANNELUSER_MSISDN = "1101595";
	public static final String O2C_EMAIL_TRANSFER_MRP = "1101596";
	public static final String O2C_EMAIL_NOTIFICATION_CONTENT_REQ_AMOUNT = "1101597";
	public static final String O2C_EMAIL_TRANSFER_TYPE = "1101598";
	public static final String O2C_EMAIL_INITIATOR_NAME = "1101599";
	public static final String O2C_EMAIL_INITIATOR_MSISDN = "1101600";
	public static final String O2C_EMAIL_NOTIFICATION_CONTENT_REJECTED_BY = "1101601";
	public static final String O2C_EMAIL_NOTIFICATION_CONTENT_REJECTION_REMARKS = "1101602";
	public static final String O2C_EMAIL_NOTIFICATION_CONTENT_NET_PAYABLE_AMOUNT = "1101603";
	public static final String O2C_EMAIL_NOTIFICATION_CONTENT_APPR_ONE_QUANTITY = "1101604";
	public static final String O2C_EMAIL_NOTIFICATION_CONTENT_APPR_QUANTITY = "1101605";
	public static final String O2C_EMAIL_TOTAL_COMMISSION = "1101606";
	public static final String O2C_EMAIL_OFFLINE_SETTLEMENT = "1101607";
	public static final String O2C_EMAIL_NOTIFICATION_SERIALNUMBER = "1101608";
	public static final String O2C_EMAIL_NOTIFICATION_DENOMINATION = "1101609";
	public static final String O2C_EMAIL_NOTIFICATION_QUANTITY = "1101610";
	public static final String O2C_EMAIL_NOTIFICATION_FROMSERIALNO = "1101611";
	public static final String O2C_EMAIL_NOTIFICATION_TOSERIALNO = "1101612";
	public static final String O2C_EMAIL_NOTIFICATION_VOUCHERTYPE = "1101613";
	public static final String MESSAGE_CHANNELTRANSFER_EXTERNALTXNNUMBERNOTUNIQUE = "1101614";
	public static final String ERROR_TRANSFER_MAXBALANCE_REACHED = "1101615";
	public static final String EXCEPTION_WHILE_LOADING_THE_CLASS = "1101616";
	public static final String O2C_APPROVAL_ERROR_MRPBLOCKTIMEOUT = "1101617";
	public static final String CHANNELTRANSFER_PHONEINFO_NOTEXIST_MSG = "1101618";
	public static final String CHANNELTRANSFER_APPROVAL_MSG_UNSUCCESS = "1101619";
	public static final String O2C_APPROVAL_ERROR_INVALIDDOMAIN = "1101620";
	public static final String NO_PHONE_INFO_FOUND  = "1101621";
	public static final String GENERIC_ERROR = "1101622";
	public static final String CHANNELTRANSFER_FAIL_RECONCILIATION_MSG_SUCCESS = "1101623";
	public static final String O2C_EMAIL_NOTIFICATION_SUBJECT_INITIATE = "1101624";
	public static final String O2C_EMAIL_NOTIFICATION_SUBJECT_APPROVER = "1101625";
	public static final String O2C_EMAIL_NOTIFICATION_SUBJECT_FAILED = "1101626";
	public static final String O2C_EMAIL_NOTIFICATION_CONTENT_TRANSFER_COMPLETED = "1101627";
	public static final String USER_NOT_ACTIVE = "1101628";
	public static final String NO_USER_COMM_PROFILE = "1101629";
	public static final String COMM_PROFILE_NOT_ACTIVE = "1101630";
	public static final String TRF_PROFILE_NOT_ACTIVE = "1101631";
	public static final String USER_SUSPENDED = "1101632";
	public static final String O2C_RECON_DATE_INVALID_30_DAYS = "1101633";
	public static final String O2C_RECON_DATE_INVALID_FUTURE = "1101634";
	public static final String O2C_RECON_TRANSFER_ID_INVALID = "1101635";
	public static final String NO_ACTIVE_PROFILE_EXISTS = "1101636";
	public static final String NO_PHONE_INFO_EXISTS = "1101637";
	public static final String O2C_RECON_CANCELLATION_FAILED = "1101638";
	public static final String O2C_RECON_CANCELLATION_SUCCESS = "1101639";
	public static final String O2C_RECONCILIATION_MSG_SUCCESS = "1101640";
	public static final String O2C_RECON_ALREADY_PROCESSED  = "1101641";
	public static final String ERROR_MODIFY_TRUE  = "1101642";
	public static final String MOBILE_NUMBER_IS_NOT_FROM_YOUR_NETWORK="1101643";
	public static final String NO_INFORMATION_AVAILABLE="1101644";
	public static final String CORRECT_MAPPING_EXISTS="1101645";
	public static final String AS_BOTH_ENTERED_MSISDN_AND_ICCID_IMSI_ARE_FREE_FOR_USE="1101646";
	public static final String LOAD_CORRECT_MSISDN_DETAILS="1101647";
	public static final String MAPPING_DETAILS_MODIFIED="1101648";
	public static final String MSISDN_WILL_BE_ASSOCIATE="1101649";
	public static final String ICCID_IMSI_WILL_BE_AVAILABLE="1101650";
	public static final String MSISDN_WILL_HAVE_TO_BE_RE_ASSOCIATED="1101651";
	public static final String NO_INFO_FOUND_FOR_ICCID = "1101652";
	public static final String ICCID_NOT_IN_SUPPORTED_NETWORK = "1101653";
	public static final String MOBILE_NOT_IN_SUPPORTED_NETWORK = "1101654";
	public static final String NO_RECORD_FOUND = "1101655";
	public static final String ICCID_ASSOCIATED_WITH_MSISDN_NOT_FROM_NETWORK = "1101656";
	public static final String NO_MAPPING_EXISTS = "1101657";
	public static final String RECORD_FOUND = "1101658";
	public static final String RECORD_NOT_FOUND = "1101659";
	public static final String ICCID_LOAD_HISTORY_SUCCESSFULLY="1101660";
	public static final String O2C_RECON_DATE_INVALID_RANGE="1101661";
	public static final String O2C_RECON_LIST_SUCCESS="1101662";
	public static final String O2C_RECON_TRANSACTION_DETAIL_SUCCESS="1101663";
	public static final String EXTERNAL_TRANSACTION_LENGTH_EXCEED = "1101664";
	public static final String PAYMENT_INSTRUMENT_TYPE_BLANK = "1101665";
	public static final String NETWORK_LIST_WENT_WRONG = "1101666";
	public static final String MODIFY_NETWORK_WENT_WRONG = "1101667";
	public static final String GET_SERVICE_SETID_WENT_WRONG = "1101668";
	public static final String MESSAGE_MANAGEMENT_BLANK_MESSAGECODE = "1101669";
	public static final String MASTER_SUBLOOKUP_ERROR  = "1101670";
	public static final String MASTER_SUBLOOKUP_SUCCESS  = "1101671";
	public static final String INVALID_SUBLOOKUP_CODE  = "1101672";
	public static final String MASTER_SUBLOOKUP_RECORD_ALREADYEXISTS  = "1101673";
	public static final String MASTER_LOOKUPCODE_BLANK  = "1101674";
	public static final String MASTER_SUBLOOKUP_BLANK  = "1101675";
	public static final String FILE_SIZE_EMPTY  = "1101676";
    public static final String CELL_GROUP_CODE_NULL = "1101677";
    public static final String CELL_GROUP_NAME_NULL = "1101678";
    public static final String STATUS_NULL = "1101679";
    public static final String CELL_GROUP_CODE_BLANK = "1101680";
    public static final String CELL_GROUP_NAME_BLANK = "1101681";
    public static final String CELL_GROUP_CODE_EXIST = "1101682";
    public static final String CELL_GROUP_NAME_EXIST = "1101683";
    public static final String CELL_GROUP_ID_ADD_SUCCESS = "1101684";
    public static final String SQL_ERROR = "1101685";
	public static final String BULKAMBIGUOUSSETTLEMENT_MESSAGE_SUCCESS= "1101686";
    public static final String CELL_GROUP_ID_NULL = "1101687";
    public static final String CELL_GROUP_ID_BLANK = "1101688";
    public static final String CELL_GROUP_ID_MODIFY_SUCCESS = "1101689";
    public static final String CELL_GROUP_ID_DELETE_SUCCESS = "1101690";
    public static final String CELL_GROUP_ID_ACTIVE = "1101691";
	public static final String CELL_GROUP_ID = "1101692";
	public static final String CELL_GROUP_NAME_HEADER = "1101693";
	public static final String CELL_GROUP_ID_MAND = "1101694";
	public static final String CELL_GROUP_ID_COMMENT = "1101695";
	public static final String ID_NAME = "1101696";
	public static final String ID_COMMENT = "1101697";
	public static final String CELL_ID = "1101698";
	public static final String CELL_ID_COMMENT = "1101699";
	public static final String SITE_NAME = "1101700";
	public static final String SITE_COMMENT = "1101701";
	public static final String CELL_GROUP_VALID_FILE = "1101702";
	public static final String CELL_GROUP_PATH_ERROR = "1101703";
	public static final String CELL_GROUP_MAX_RECORDS = "1101704";
	public static final String BLANK_ROW = "1101705";
	public static final String CELL_GROUP_ID_MANDATORY = "1101706";
	public static final String CELL_GROUP_ID_INVALID = "1101707";
	public static final String CELL_GROUP_SITE_ID_BLANK = "1101708";
	public static final String CELL_GROUP_SITE_ID_LENGTH_INVALID = "1101709";
	public static final String CELL_GROUP_SITE_ID_INVALID = "1101710";
	public static final String CELL_ID_BLANK = "1101711";
	public static final String CELL_ID_LENGTH_INVALID = "1101712";
	public static final String CELL_ID_INVALID = "1101713";
	public static final String SITE_NAME_BLANK = "1101714";
	public static final String SITE_NAME_LENGTH_INVALID = "1101715";
	public static final String SITE_ID_INVALID = "1101716";
	public static final String CELL_ID_ALREADY_MAPPED = "1101717";
	public static final String CELL_ID_ASSOCIATION_SUCCESS = "1101718";
    public static final String UPLOAD_NOT_TXT_FILE = "1101719";
	public static final String ASSOCIATED_CELL_ID = "1101720";
	public static final String ASSOCIATED_CELL_IN_COMMENT = "1101721";
	public static final String CELL_ID_STATUS = "1101722";
	public static final String CELL_ID_STATUS_COMMENT = "1101723";
	public static final String CELL_ID_TO_BE_ASSOCIATED = "1101724";
	public static final String CELL_ID_TO_BE_ASSOCIATED_COMMENT = "1101725";
	public static final String CELL_ID_ACTION = "1101726";
	public static final String CELL_ID_ACTION_COMMENT = "1101727";
	public static final String CELL_ID_LENGTH = "1101728";
	public static final String ASSOCIATE_CELL_ID_MANDATORY = "1101729";
	public static final String ASSOCIATE_CELL_ID_INVALID = "1101730";
	public static final String CELL_ID_STATUS_MANDATORY = "1101731";
	public static final String CELL_ID_STATUS_INVALID = "1101732";
	public static final String NEW_CELL_GROUP_ID_MANDATORY = "1101733";
	public static final String NEW_CELL_GROUP_ID_INVALID = "1101734";
	public static final String ACTION_INVALID = "1101735";
	public static final String REASSOCIATE_NO_MATCH_FOUND = "1101736";
	public static final String REASSOCIATE_SUCESSFULL = "1101737";
	public static final String MIN_MAX_LENGTH = "1101738";
	public static final String MSISDN_WILL_HAVE_REASSIGNED= "1101739";
	public static final String NETWORK_CODE_INVALID= "1101740";
	public static final String MESSAGE_MANAGEMENT_DATA_SHEET= "1101741";
	public static final String ICCID_NOT_EXIST = "1101742";
	public static final String ICCID_DELETE_SUCCESS = "1101743";
	public static final String ICCID_DELETE_FAIL ="1101744";
	public static final String ICCID_DELETE_ACTION_UNSUCCESS = "1101745";
	public static final String INVALID_FILE_ICCID_ISMIS = "1101746";
	public static final String INVALID_FILE_NAME_UPLOAD_FAIL = "1101747";
	public static final String INVALID_FILE_CONTENT_ICCID = "1101748";
	public static final String NOT_A_TXT_FILE="1101749";
	public static final String MESSAGE_MANAGEMENT_MULTIPLE_DATA_SHEET_NOT_ALLOWED="1101750";
	public static final String INVALID_INTERFACE = "1101751";
	public static final String TEXTAREA_CHARS_ARE_MORETHANMAX = "1101752";
	public static final String ROUTING_UPLOADMSISDN_MSG_NONETWORKFOUND = "1101753";
	public static final String ROUTING_UPLOADMSISDN_MSG_NETWORKNOTSUPPORTED = "1101754";
	public static final String ROUTING_ROUTINGUPLOAD_MSG_SUCCESS = "1101755";
	public static final String ROUTING_ROUTINGUPLOAD_MSG_FAIL = "1101756";
	public static final String ROUTING_UPLOAD_FAILED = "1101757";
	public static final String INTERFACE_CATEGORY_REQUIRED = "1101758";
	public static final String ROUTING_ROUTINGUPLOADFILE_ERROR_INNAMEMISSING = "1101759";
	public static final String ROUTING_ROUTINGUPLOADFILE_ERROR_NOEXTIDFORINTERFACE = "1101760";
	public static final String ROUTING_ROUTINGUPLOADFILE_ERROR_INNAMEFIRSTLINE = "1101761";
	public static final String ROUTING_ROUTINGUPLOADFILE_ERROR_INNAMEVALUEFIRSTLINE = "1101762";
	public static final String ROUTING_ROUTINGUPLOADFILE_ERROR_INNAMEVALUELENGTH = "1101763";
	public static final String ROUTING_ROUTINGUPLOADFILE_ERROR_EXTIDNOTEXIST = "1101764";
	public static final String ROUTING_ROUTINGUPLOADFILE_ERROR_NOTGETRECORD = "1101765";
	public static final String ROUTING_ROUTINGUPLOADFILE_ERROR_IMPROPERSYNTAX = "1101766";
	public static final String ROUTING_ROUTINGUPLOADFILE_ERROR_NORECORDS = "1101767";
	public static final String ROUTING_ROUTINGUPLOADFILE_MSG_SUCCESSFAIL = "1101768";
	public static final String ROUTING_DELETE_MSG_SOMESUCCESS = "1101769";
	public static final String ROUTING_DELETE_MSG_SUCCESS = "1101770";
	public static final String ROUTING_DELETE_MSG_UNSUCCESS = "1101771";
	public static final String ROUTING_DELETE_MSG_BULK_SUCCESS = "1101772";
	public static final String ROUTING_ROUTINGUPLOADFILE_MSG_FAIL = "1101773";
	public static final String ROUTING_DELETE_MSG_BULK_FAIL = "1101774";
	public static final String ERROR_INSERTING_RECORD = "1101775";
	public static final String NO_RECORD_EXIST = "1101776";
	public static final String ERROR_DELETING_RECORD = "1101777";
	public static final String USER_TYPE_SERVICE_LIST_SUCCESS = "1101778";
	public static final String WML_MAX_LENGTH = "1101779";
	public static final String DESCRIPTION_MAX_LENGTH = "1101780";
	public static final String BYTE_CODE_GEN_SUCCESS = "1101781";
	public static final String PUSH_WML_SUCCESS = "1101782";
	public static final String MSISDN_UNSUPPORTED_NETWORK = "1101783";
	public static final String MSISDN_NOT_EXISTS = "1101784";
	public static final String SMS_NOT_SENT = "1101785";
	public static final String ADD_SERVICE_SUCCESS = "1101786";
	public static final String ADD_SERVICE_FAIL = "1101787";
	public static final String WRONG_HEX_STRING = "1101788";
	public static final String BYTE_CONVERSION_FAIL = "1101789";
	public static final String MISSING_80 = "1101790";
	public static final String WML_CODE_NULL = "1101791";
	public static final String SERVICE_SET_ID_NULL = "1101792";
	public static final String LENGTH_BYTECODE_NULL = "1101793";
	public static final String MOBILE_NUMBER_NULL = "1101794";
	public static final String CATEGORIES_LIST_NULL = "1101795";
	public static final String LABEL1_NULL = "1101796";
	public static final String LABEL2_NULL = "1101797";
	public static final String BYTECODE_NULL = "1101798";
	public static final String SERVICE_SET_ID_INVALID = "1101799";
	public static final String OFFSET_NULL = "1101800";
	public static final String POSITION_NULL = "1101801";
	public static final String LABEL2_INVALID = "1101802";
	public static final String BYTECODE_INVALID = "1101803";
	public static final String SIM_PROFILE_CAT_LIST_SUCCESS = "1101804";
	public static final String USER_SIM_SERVICES_LIST_SUCCESS = "1101805";
	public static final String SIM_SERVICES_LIST_SUCCESS = "1101806";
	public static final String CALCULATE_OFFSET_SUCCESS = "1101807";
	public static final String USER_SIM_SERVICE_EXISTS = "1101808";
	public static final String ASSIGN_SERVICE_SUCCESS = "1101809";
	public static final String OFFSET_INVALID = "1101810";
	public static final String USER_SIM_SERVICE_NOT_MODIFIED = "1101811";
	public static final String CATEGORY_CODE_NULL = "1101812";
	public static final String PROFILE_CODE_NULL = "1101813";
	public static final String SIM_PROFILE_CODE_NULL = "1101814";
	public static final String SEARCH_STRING_NULL = "1101815";
	public static final String POSITION_INVALID = "1101816";
	public static final String USER_SERVICE_STATUS_INVALID = "1101817";
	public static final String FREE_OFFSET_LIST_NULL = "1101818";
	public static final String USED_OFFSET_LIST_INVALID = "1101819";
	public static final String WML_CODE_FAIL = "1101820";
	public static final String SERVICE_ID_NULL = "1101821";
	public static final String WML_CODE_INVALID = "1101822";
	public static final String USER_TYPE_LIST_NULL = "1101823";
	public static final String USER_SERVICE_STATUS_NULL = "1101824";
	public static final String INVALID_INTERFACE_CATEGORY = "1101825";
	public static final String ROUTING_ROUTINGUPLOAD_ERROR_ZEROROUTINGNUMBER = "1101826";
	public static final String ROUTING_ROUTINGUPLOAD_ERROR_NORECORDS = "1101827";
	public static final String SERVICE_STATUS = "1101828";
	public static final String USER_TYPE_LIST_INVALID = "1101829";
	public static final String USER_TYPE_INVALID = "1101830";
	public static final String ICCID_AND_MSISDN_REQUIRED ="1101831";
	public static final String PROFILE_CODE_INVALID = "1101832";
	public static final String SIM_PROFILE_CODE_INVALID = "1101833";
	public static final String BYTE_CODE_LENGTH_INVALID = "1101834";
	public static final String MSISDN_LENGTH_INVALID = "1101835";
	public static final String ICCID_LENGTH_INVALID = "1101836";
	public static final String INTERFACE_TYPE_BLANK = "1101837";
	public static final String MOBILE_NUMBER_BLANK = "1101838";
	public static final String MOBILE_NUMBER_INVALID = "1101839";
	public static final String INVALID_SERVICE_ID  = "1101840";
	public static final String MAJOR_VERSION_NULL  = "1101841";
	public static final String MAJOR_VERSION_INVALID  = "1101842";
	public static final String MINOR_VERSION_NULL  = "1101843";
	public static final String MINOR_VERSION_INVALID  = "1101844";
	public static final String MODIFY_SERVICE_FAIL = "1101845";
	public static final String LOAD_SERVICE_DETIALS_SUCCESS = "1101846";
	public static final String MODIFY_SERVICE_SUCCESS = "1101847";
	public static final String MOBILE_NUMBER_INVALID_PARTIAL = "1101848";
	public static final String ROUTING_UPLOADMSISDN_MSG_NONETWORKFOUND_ERROR = "1101849";
	public static final String ROUTING_UPLOADMSISDN_MSG_NETWORKNOTSUPPORTED_ERROR = "1101850";
	public static final String ROUTING_UPLOADMSISDN_MSG_ALREADY_EXIST_ERROR = "1101851";
	public static final String ROUTING_UPLOADMSISDN_MSG_ADD_PARTIAL = "1101852";
	public static final String SERVICE_ID_INVALID = "1101853";
	public static final String SERVICE_ID_INVALID_MAPPED = "1101854";

	public static final String CELL_GEOGRPHY_DOMAIN_CODE = "1101855";
	public static final String CELL_GEOGRPHY_DOMAIN_NAME = "1101856";
	public static final String CELL_GEOGRPHY_PARRENT_DOMAIN_CODE = "1101857";
	public static final String CELL_GEOGRPHY_CELL_ID = "1101858";
	public static final String CELL_GEOGRPHY_CELL_NAME = "1101859";
	public static final String CELL_GEOGRPHY_CELL_COLUMN = "1101860";

	public static final String GEOG_CELL_ID_BLANK_ROW_ERROR="1101861";
	public static final String GEOG_CELL_ID_MANDAT_ERROR="1101862";
	public static final String GEOG_CELL_ID_ALPHABUMERIC_ERROR="1101863";
	public static final String GEOG_CELL_ID_EXISTS="1101864";
	public static final String GEOG_CELL_NAME_EMPTY_ERROR="1101865";
	public static final String GEOG_CELL_NAME_EXISTS="1101866";
	public static final String GEOG_CELL_NAME_LENGTH_ERROR="1101867";
	public static final String GEOG_DOMAIN_CODE_EMPTY_ERROR="1101868";
	public static final String GEOG_DOMAIN_CODE_INVLID="1101869";
	public static final String GEOG_CELL_ID_MPPING_SUCCESS_MSG="1101870";
    public static final String CELL_GROUP_CODE_INVALID="1101871";
	public static final String CELL_GROUP_NAME_INVALID="1101872";
	public static final String WML_LENGTH_INVALID="1101873";
	public static final String OFFSET_INVALID_CHAR="1101874";
	public static final String FREE_OFFSET_LIST_INVALID="1101875";
	public static final String SIM_SERVICE_LENGTH_NULL="1101876";
	public static final String SIM_SERVICE_LENGTH_INVALID="1101877";
	public static final String PROMOTIONAL_TYPE_IS_NULL="1101878";
	public static final String PROMOTIONAL_TYPE_IS_EMPTY="1101879";
	public static final String PROMOTIONAL_TYPE_IS_INVALID="1101880";
	public static final String APPLICABLE_FROM_AND_APPLICABLE_TO_REQUIRED="1101881";
	public static final String APPLICABLE_FROM_INVALID_FORMAT="1101882";
	public static final String APPLICABLE_TO_INVALID_FORMAT="1101883";
	public static final String SET_ID_IS_NULL="1101884";
	public static final String VERSION_IS_NULL="1101885";
	public static final String LOAD_PROFILE_DETAILS_SUCCESSFULLY="1101886";
	public static final String LOAD_LOYALITY_MANAGEMENT_VERSIONS_SUCCESSFULLY="1101887";
	public static final String VERSION_IS_EMPTY= "1101888";
	public static final String SET_ID_IS_EMPTY= "1101889";
	public static final String VERSION_IS_INVALID_FORMAT= "1101890";
	public static final String SET_ID_IS_INVALID_FORMAT= "1101891";
	public static final String LOAD_LOYALITY_MANAGEMENT_PROFILE_DETAILS_SUCCESSFULLY= "1101892";
	public static final String DEFAULT_MESSAGE_IS_NULL= "1101893";
	public static final String LANGUAGE_MESSAGE_IS_NULL="1101894";
	public static final String LOYALTY_PROFILE_INITIATED="1101895";
	public static final String AT_LEAST_ONE_SLAB_IS_REQUIRED="1101896";
	public static final String AT_LEAST_ONE_SLAB_ENTRY_IS_REQUIRED="1101897";
	public static final String COUNT_SLAB_SHOULD_BE_GREATER_THAN_BEFORE="1101898";
	public static final String AMOUNT_SLAB_SHOULD_BE_GREATER_THAN_BEFORE="1101899";
	public static final String PROMOTION_TYPE="1101900";
	public static final String PROFILE_NAME_IS_REQUIRED="1101901";
	public static final String APPLICABLEFROM_DATE="1101902";
	public static final String APPLICABLEFROM_HOUR="1101903";
	public static final String APPLICABLETO_DATE="1101904";
	public static final String APPLICABLETO_HOUR="1101905";
	public static final String OPTINOUTTARGET="1101906";
	public static final String MSGCONFIGENABLED="1101907";
	public static final String OPERATORCONTRIBUTION="1101908";
	public static final String PARENTCONTRIBUTION="1101909";
	public static final String REFERENCEBASED="1101910";
	public static final String REFERENCEFROMDATE="1101911";
	public static final String REFERENCETODATE="1101912";
	public static final String PRODUCT="1101913";
	public static final String MODULE="1101914";
	public static final String SERVICE="1101915";
	public static final String TARGETTYPE="1101916";
	public static final String FREQUENCY="1101917";
	public static final String REWARDSTYPE="1101918";
	public static final String SUBTYPE_IS="1101919";
	public static final String VALID_UP_TO_DATE="1101920";
	public static final String BYTE_CODE_LENGTH_NULL="1101921";

	public static final String PROFILE_ALREADY_ASSOCIATED = "1101922";
	public static final String PROFILE_DELETED_SUCCESSFULLY = "1101923";
	public static final String ROUTING_UPLOAD_FILE_SUCCESS = "1101924";
	public static final String WEAK_PASSWORD = "1101925";
	public static final String GEOGRAPHICAL_DOMAIN_ADDED_SUCCESSFULLY = "1101926";
	public static final String TRANSFER_NOT_ALLOWED = "1101927";
	public static final String LMS_ASSOCIATION_FAIL_MSG = "1101928";
	public static final String LMS_ASSOCIATION_SUCCESS_MSG = "1101929";
	//Loan Profile
	public static final String LOAN_SUCCESS = "1101930";
	public static final String LOAN_FAIL = "1101931";
	public static final String LOAN_MODIFY_SUCCESS = "1101932";
	public static final String LOAN_MODIFY_FAIL = "1101933";
	public static final String LOAN_PROFILE_NAME_EXISTS = "1101934";
	public static final String LOAN_ADD_SUCCESS = "1101935";
	public static final String LOAN_ADD_FAIL = "1101936";
	public static final String LOAN_DELETE_SUCCESS = "1101937";
	public static final String LOAN_DELETE_FAIL = "1101938";
	public static final String LOAN_NOT_ALLOWED = "1101939";
	public static final String O2C_TRANSFER_REVERSAL_ENQUIRY_MSISDN ="1101940";
	public static final String O2C_TRANSFER_REVERSAL_ENQUIRY_ADVANCED ="1101941";

	public static final String VERSION_INVALID="1101942";
	public static final String SET_ID_INVALID="1101943";
	public static final String PROFILE_MODIFIED_SUCCESSFULLY="1101944";
	public static final String PROFILE_CANNT_SUSPENDED_IT_IS_EXPIRED= "1101945";
	public static final String PROFILE_CANNT_SUSPENDED_IT_IS_NOT_ACTIVE= "1101946";
	public static final String PROFILE_SUSPENDED_SUCCESSFULLY= "1101947";
	public static final String LOAD_PROFILE_MODULE_DETAILS_SUCCESSFULLY= "1101948";
	public static final String PROFILE_APPROVE_SUCCESSFULLY= "1101949";
	public static final String PROFILE_REJECTED_SUCCESSFULLY= "1101950";
	public static final String LOAD_PROFILE_MESSAGES_SUCCESSFULLY= "1101951";
	public static final String MODIFY_MESSAGES_SUCCESSFULLY="1101952";
	public static final String ENTER_MESSAGE_BODY="1101953";
	public static final String PROFILE_RESUME_SUCCESSFULLY="1101954";
	public static final String GEOGRAPHICAL_DOMAIN_MODIFIED_SUCCESSFULLY = "1101955";
	public static final String MASTER_SERVICE_MGMT_SELECT_ATLEAST_ONE_MSG = "1101956";
	public static final String GEOGRAPHICAL_DOMAIN_DELETED_SUCCESSFULLY = "1101957";
	public static final String DUPLCATE_CARD_GROUP ="1101958";
	public static final String ERROR_REVERSE ="1101959";
	public static final String VESRION_CARD_GROUP_CANNOT_BE_DELETED = "1101960";
	public static final String FUTURE_CARD_GROUP_CANNOT_BE_DELETED = "1101961";
	public static final String PASSWORD_BLOCKED_NOT ="1101962";
	public static final String OPERATION_ID="1101963";
	public static final String VALID_LOG_MSISDN="1101964";
	public static final String PASSWORD_NOT_BLOCKED="1101965";
	public static final String PASSWORD_RESET_SUCCESS="1101966";
	public static final String PASSWORD_UNBLOCK_SUCCESS="1101967";
	public static final String PASSWORD_SENT_SUCCESS="1101968";
	public static final String OPERATION_FAIL="1101969";
	public static final String OPERATION_PASS="1101970";
	public static final String FETCH_USER_DET_ERROR="1101971";
	public static final String MSG_CONFIG_NOT_ENBLE="1101972";
	public static final String MASTER_UM_HEADING="1101973";
	public static final String AVAILABLE_HEIRARCHY="1101974";
	public static final String USER_MIG_PARTIAL_SUCESS="1101975";
	public static final String ICCID_ERROR_FILE_HEADER = "1101976";
	public static final String ERROR_FILE_HEADER_PAYOUT = "1101977";
	public static final String ERROR_FILE_HEADER_MOVEUSER = "1101978";
	public static final String FROM_DATE_VALIDATION = "1101979";
	public static final String CURRENT_DATE_VALIDATION = "1101980";
	public static final String O2C_BATCH_WITHDRAW_FAIL = "1101981";
	public static final String SIM_TXN_ID_UPDATED_SUCCSSFULLY="1101982";
	public static final String ICCID_ENQUIRY_BASED_ON_DATE_SUCCESSFULLY="1101983";

}
