package com.btsl.pretups.inter.module;

/**
 * @(#)InterfaceErrorCodesI.java
 *                               Copyright(c) 2005, Bharti Telesoft Int. Public
 *                               Ltd.
 *                               All Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Abhijit Chauhan June 24,2005 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 */
public interface InterfaceErrorCodesI {
    public static String MODULE_C2S = "C2S";
    public static String MODULE_P2P = "P2P";
    public static String SUCCESS = "200";
    public static String AMBIGOUS = "250";
    public static String INVALID_RESPONSE = "17001";
    public static String NULL_INTERFACE_RESPONSE = "17002";
    public static String RETRY_ATTEMPT_FAILED = "17003";
    public static String EXCEPTION_INTERFACE_RESPONSE = "17004";
    public static String INTERFACE_HANDLER_EXCEPTION = "17005";
    public static String VALIDATION_ERROR = "17006";
    public static String ERROR_RESPONSE = "17007";
    public static String INTERFACE_MSISDN_BARRED = "17008";
    public static String INTERFACE_MSISDN_EXPIRED = "17009";
    public static String INTERFACE_CUSTOMER_RECHARGENOTALLOWED = "17010";
    public static String INTERFACE_PARAMETER_MISMATCH = "17011";
    public static String INTERFACE_TRANSIDNOTGENERATED = "17012";
    public static String INTERFACE_TRANSIDGENEXCEPTION = "17013";
    public static String INTERFACE_CONNECTION_EXCEPTION = "17014";
    public static String INTERFACE_CONNECTION_NULL = "17015";
    public static String INTERFACE_PROCESS_TIMEOUT = "17016";
    public static String INTERFACE_MSISDN_NOT_FOUND = "17017";
    public static String INTERFACE_PROCESS_REQUEST_ERROR = "12050";

    public static String ERROR_FERMA_STATUS_TWO = "18800";
    public static String ERROR_FERMA_STATUS_TEN = "18801";
    public static String ERROR_FERMA_STATUS_ELEVEN = "18802";
    public static String ERROR_FERMA_STATUS_TWENTY = "18803";
    public static String ERROR_FERMA_STATUS_TWENTYTWO = "18804";
    public static String ERROR_FERMA_HTTPSTATUS_FOURZEROONE = "18805";

    public static String ERROR_BAD_REQUEST = "13000";
    public static String ERROR_UNKNOWN_INTERFACE_ID = "13001";
    public static String ERROR_INSUFFCIENT_CREDIT = "13002";
    public static String ERROR_SDP_CONNECTION_PROBLEM = "13003";
    public static String ERROR_INVALID_USER_STATUS = "13004";
    // added for post paid bill payment controller date 17/05/06
    public static String INTERFACE_QUEUE_SIZE_FULL = "13005";

    public static String INTERFACE_INVALID_SELECTOR = "13006";
    public static String INTERFACE_REQ_NOT_SEND = "314";

    // Error codes for the CS3 interface added by Ashish-18/10/06
    public static String ERROR_CS3_NO_INTERFACEIDS = "19001";
    public static String ERROR_CS3_WHILE_GETTING_SCHEDULER_OBJECT = "19002";
    public static String ERROR_CS3_NODE_SCHEDULING = "19003";
    public static String ERROR_CS3_NODE_INITIALIZATION = "19004";
    public static String ERROR_CS3_MAX_NODE_CHECK_REACH = "19005";
    public static String ERROR_CS3_NODE_DETAIL_NOT_FOUND = "19006";

    // VOMS integration start
    public static String VOMS_ERROR_INSERTION_AUDIT_TABLE = "14000";
    public static String INVALID_MRP_REQUESTED = "14001";
    public static String INVALID_PRODUCT_REQUESTED = "14002";
    public static String VOMS_ERROR_UPDATION = "14003";
    // VOMS integration end

    // Added by Ashish for POOLLoader 16-02-07
    public static String ERROR_OBJECT_POOL_INITIALIZATION = "15000";// Error
                                                                    // while
                                                                    // initializing
                                                                    // the Pool
                                                                    // of
                                                                    // Connection
                                                                    // client
                                                                    // objects.
    public static String ERROR_FETCH_CLIENT_OBJECT = "15001";// Error while
                                                             // getting the
                                                             // Connection
                                                             // client object
                                                             // from the pool.
    public static String ERROR_NO_FREE_OBJ_IN_POOL = "15002";// Free Connection
                                                             // clients are not
                                                             // available in
                                                             // pool.
    public static String ERROR_GETTIN_NEW_CLIENT_OBJECT = "15003";// //Error
                                                                  // occured
                                                                  // while
                                                                  // creating
                                                                  // new
                                                                  // Connection
                                                                  // client.
    public static String ERROR_CLIENT_OBJECT_INITIALIZATION = "15004";// Error
                                                                      // occured
                                                                      // while
                                                                      // initialing
                                                                      // Connection
                                                                      // client
    public static String ERROR_CLIENT_OBJECT_LOGIN_REQ_FAIL = "15005"; // Error
                                                                       // occured
                                                                       // while
                                                                       // trying
                                                                       // to
                                                                       // login
                                                                       // in IN
                                                                       // System.

    // Added by Ashish for POOLLoader 19-02-07
    public static String ERROR_INIT_SOCKET_CONNECTION = "15006";// Error while
                                                                // initialing
                                                                // Connection
                                                                // Clients

    // Added for Huawei
    public static String HEARTBEAT_ERROR_OBJECT_POOL_INIT = "15007";// Error
                                                                    // occured
                                                                    // while
                                                                    // trying to
                                                                    // start
                                                                    // Heart
                                                                    // beat
                                                                    // thread.
    public static String HEARTBEAT_ERROR_OBJECT_POOL_DESTROY = "15008";// //Error
                                                                       // occured
                                                                       // while
                                                                       // trying
                                                                       // to
                                                                       // destroy
                                                                       // Heart
                                                                       // beat
                                                                       // thread.

    // Added for Cancel failed request on 160307
    public static String FAIL = "206";
    // public static String FAIL="350";
    // Added Interface Closer on 030407
    public static String INTERFACE_SUSPENDED = "500";

    // Error Codes definded for InterfaceCloserServlet
    public static String ERROR_INIT_INTERFACE_CLOSER = "15009";// Error occured
                                                               // while
                                                               // initialing
                                                               // Interface
                                                               // Closer
                                                               // properties
    public static String ERROR_UPDATE_INTERFACE_CLOSER = "15010";// Error
                                                                 // occured
                                                                 // while
                                                                 // updating
                                                                 // Interface
                                                                 // Closer
                                                                 // properties

    // Error codes defined for the Billing system 24042007
    public static String ERROR_INVALID_REQ_TYPE = "16000";
    public static String ERROR_FTP_CONNECT_FAIL = "16001";
    public static String ERROR_FTP_LOGIN_FAILED = "16002";
    public static String ERROR_DB_CON_INITILIZATION = "16003";
    public static String ERROR_INITILIZATION_INNERCLASS = "16004";
    public static String ERROR_FTP_FILE_UPLOAD = "16005";

    public static String ERROR_NOT_RESOLVE_NAMESERVICE = "1000";
    public static String ERROR_NOT_NARROW_INITREF = "1001";
    public static String ERROR_NOT_RESOLVE_NAMECOMPONENT = "1002";
    public static String ERROR_NOT_NARROW_AUTHSERVICE = "1003";
    public static String ERROR_FACTORY_OBJ_NULL = "1004";
    public static String ERROR_NOT_GET_ACCMANAGERREF = "1005";
    public static String ERROR_NOT_PROCESS_REQ = "1006";
    public static String ERROR_CONFIG_PROBLEM = "1007";
    public static String ERROR_NOT_GET_PPACCOUNTFACTORYREF = "1008";

    // Error codes defined for Mobi IN.
    public static String ERROR_CONV_RATE = "20001";
    // Error code defined in Bank IN
    public static String ERROR_PARAMETER_MISSING = "20002";

    public static String INTERFACE_VOUCHER_ALREADY_USED = "17018";
    public static String INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P = "17020";
    public static String ERROR_RECHARGE_AMOUNT_LESS = "17021";
    // Error code difined for ZTE IN on the basis of received StateSet from IN
    // by Vinay on 06-Aug-09.
    public static String INTERFACE_SIM_LOSS_BLOCk = "17022";
    public static String INTERFACE_USER_APPLIED_BLOCK = "17023";
    public static String INTERFACE_OPERATOR_FORCED_BLOCK = "17024";
    // If bundle is not defined for the suscriber at IN.
    public static String INTERFACE_RECHARGE_BUNDLE_NOT_DEFINED = "17025";

    public static String CHECK_AMB_STATUS_FAIL = "17026";
    public static String INVALID_POSTPAID_NUMBER = "20021";// Number is not a
                                                           // valid Postpaid
                                                           // number

    // For Mauritius

    public static String ACCOUNT_TRANSFER_STATUS_NOT_ALLOWED = "17031";

    // Error Code for UGANDA Web-service IN
    public static String ERROR_LOGIN_FAILED = "17032"; // Login fail before
                                                       // validate request.
    public static String VALIDATE_LOGIN_FAILED = "17033"; // Login fail before
                                                          // validate request.
    public static String CREDIT_LOGIN_FAILED = "17034"; // Login fail before
                                                        // credit request.
    public static String INTERFACE_PACKAGE_NOT_FOUND = "17035"; // Package not
                                                                // defined for
                                                                // the requested
                                                                // amount.
    // Dedicated account re-charge
    public static String DEDICATED_ACC_NOT_DEFINED_S = "17027";
    // Private Recharge
    public static String VOMS_STATE_NOT_ALLOWED_AT_IN = "14005";
    public static String VOMS_PIN_NOT_RECEIVED_FROM_IN = "14006";

    // Added By Vipan for Citycell
    public static String PBP_INVALID_RESPONSE = "30001";
    public static String PBP_INVALID_SERVICE_NO = "30002";
    public static String PBP_DUPLICATE_TXN_ID = "30003";
    public static String PBP_INVALID_VALUE_AMOUNT_FIELD = "30004";
    public static String PBP_UNKNOWN_USER = "30005";
    public static String PBP_DATABASE_CONN_ERROR = "30006";
    public static String PBP_UNKNOWN_EXCEPTION = "30007";
    public static String PBP_INVALID_REQUEST = "30008";
    public static String PBP_INSUFFICIENT_BALANCE = "30009";
    public static String PBP_DESTINATION_MOBILENO = "30010";
    public static String PBP_ERROR_CLIENT_OBJECT_INITIALIZATION = "30000";// Error
                                                                          // occured
                                                                          // while
                                                                          // initialing
                                                                          // Connection
                                                                          // client
    public static String PBP_ERROR_INVALID_SERVICE_USER = "30021";
    public static String PBP_ERROR_INVALID_SERVICE_START_DATE = "30021";
    public static String RC_HEARTBEAT_RESPONSE = "31001";
    public static String RC_ACCOUNT_NUMBER_NOT_FOUND = "31002";
    public static String RC_ACCOUNT_EXPIRED = "31003";
    public static String RC_USED_FIRST_TIME_INVALID = "31004";
    public static String RC_SUBSCRIBER_NOT_ACTIVATED = "31005";
    public static String RC_INCORRECT_PIN = "31006";
    public static String RC_EXCEED_MAX_RECH_AMOUNT = "31007";
    public static String RC_NO_PPS_INFO = "31008";
    public static String RC_INSIFFICENT_ACC_BALANCE = "31009";
    public static String RC_RECH_FAILED = "31010";
    public static String RC_SYSTEM_EXCEPTION = "31011";
    public static String RC_TXN_SN_REPEATED = "31012";
    public static String RC_RECH_SUCCESS_LOGGING_FAILED = "31013";
    public static String RC_QUERY_AREA_CODE_FAILED = "31014";
    public static String RC_QUERY_VALI_PERD_RECH_AMT_FAILED = "31015";
    public static String RC_RECHING_MAX_NO_REG_CUST = "31016";
    public static String RC_SERVICE_DATA_NOT_CONFIGURE = "31017";
    public static String INTERFACE_INVALID_AMOUNT = "31018";

    public static String VAS_PROMO_NOT_AVL_SUBSTYPE = "17036";
    public static String VAS_PROMO_INCOMPATIBLE = "17037";
    public static String VAS_INCORRECT_CODE = "17038";
    public static String VAS_INCORRECT_AMOUNT = "17039";
    public static String VAS_REQUEST_FAIL = "3035";
    public static String SIM_ACTIVATION_FAIL = "3032";

    public static String ERROR_COMVERSE_NO_INTERFACEIDS = "21001";
    public static String ERROR_COMVERSE_NODE_INITIALIZATION = "21004";
    public static String ERROR_COMVERSE_WHILE_GETTING_SCHEDULER_OBJECT = "21002";
    public static String ERROR_COMVERSE_NODE_SCHEDULING = "21003";
    public static String ERROR_COMVERSE_MAX_NODE_CHECK_REACH = "21005";
    public static String ERROR_COMVERSE_NODE_DETAIL_NOT_FOUND = "21006";

    public static String ERROR_INVALID_CLIENT = "16001";
    public static String ERROR_INVALID_NUMBER = "16002";

    public static String ERROR_NO_INTERFACEIDS = "21001";
    public static String ERROR_NODE_INITIALIZATION = "21004";
    public static String ERROR_WHILE_GETTING_SCHEDULER_OBJECT = "21002";
    public static String ERROR_NODE_SCHEDULING = "21003";
    public static String ERROR_MAX_NODE_CHECK_REACH = "21005";
    public static String ERROR_NODE_DETAIL_NOT_FOUND = "21006";

    public static String ERROR_INVALID_RESPONSE_OBJECT = "21007";
    public static String ERROR_INVALID_INVOICE_NO = "17041";
    public static String ERROR_REVERSAL_AMBIGOUS = "17042";
    public static String CMS_NO_INVOICE_FOUND = "17027";
    public static String INTERFACE_WEBSERVICE_EXCEPTION = "170040";
    // Channel ID Related Exception
    public static String ERROR_CHANNEL_ID_CONFIG_PROBLEM = "170041";

    public static String P2P_MAIN_BALANCE_LESS = "170042";
    public static String P2P_WADATA_BALANCE_LESS = "170043";
    public static String ZTE_INCORRECT_PASSWORD = "170044";
    public static String ZTE_USER_UNUSED = "170045";
    public static String ZTE_USER_ON_RETENTION = "170046";
    public static String ZTE_USER_TERMINATED = "170047";
    public static String ZTE_USER_LOST = "170048";
    public static String ZTE_USER_ONE_WAY_BLOCKED = "170049";
    public static String ZTE_USER_TWO_WAY_BLOCKED = "170050";
    public static String ZTE_USER_SUSPENDED = "170051";
    public static String ZTE_USER_FORCE_TO_SUSPEND = "170052";
    public static String ZTE_PREPAID_TRANSFER_TO_POSTPAID = "170053";
    public static String ZTE_USER_BLACKLISTED = "170054";
    public static String ERROR_HB_CONNECTION_CREATION = "170055";

    public static String ERROR_VAS_NODE_SCHEDULING = "17043";
    public static String ERROR_VAS_NODE_INITIALIZATION = "17044";
    public static String ERROR_VAS_NODE_DETAIL_NOT_FOUND = "17045";

    public static String PBP_ERROR_USER_ID = "30022";
    public static String PBP_USER_ERROR_EXPIRY = "30023";
    public static String PBP_ERROR_INVALID_PASSWORD = "30024";
    public static String PBP_ERROR_USER_PASSWORD = "30025";
    public static String PBP_ERROR_DUPLICATE_TXN = "30026";
    public static String PBP_ERROR_PAYMENT_FAILED = "30027";
    public static String PBP_ERROR_SERVICE_CLASS = "30028";
    public static String PBP_ERROR_USER_ACCOUNT_INFO = "30029";
    public static String PBP_ERROR_ACCOUNT_STATUS = "30030";
	
	// Added by Sanjay for Claro CS5 IN
  	public static String ERROR_CS5_NO_INTERFACEIDS="44001";		
  	public static String ERROR_CS5_WHILE_GETTING_SCHEDULER_OBJECT="44002";	
  	public static String ERROR_CS5_NODE_SCHEDULING="44003";
  	public static String ERROR_CS5_NODE_INITIALIZATION="44004";
  	public static String ERROR_CS5_MAX_NODE_CHECK_REACH="44005";
  	public static String ERROR_CS5_NODE_DETAIL_NOT_FOUND="44006";
  	
  	public static String ERROR_ACCOUNT_BARRED_FROM_REFILL="44008";
  	public static String ERROR_INVALID_PAYMENT_PROFILE="44009";
  	public static String ERROR_SYSTEM_UNAVAILABLE="44010";
  	public static String ERROR_ACCOUNT_NOT_ACTIVE="44011";
  	public static String ERROR_DATE_ADJUSTMENT_ISSUE="44012";
  	public static String ERROR_IN_CONN_FAIL="44013";
  	public static String ERROR_IN_RESPONSE_FAIL="44014";
  	public static String ERROR_ACC_MAX_CREDIT_LIMIT="44015";
  	
	public static String ERROR_VOUCHER_STATUS_PENDING="44016";
	public static String ERROR_VOUCHER_GROUP_SERVICE_CLASS="44017";
	public static String ERROR_BELOW_MIN_BAL="44018";
	public static String ERROR_INTERFACE_NETW_CODE_NOT_DEF="44019";
	public static String ERROR_INTERFACE_MULTFACTOR="44020";
	public static String ERROR_ACCOUNT_TEMPORARY_BLOCKED="44021";//for GP
	public static String ERROR_MAX_CREDIT_LIMIT="44022";
	
	public static String ERROR_TRANSACTIONID_DIFFERENCE="15012";
	
	public static String ERROR_COMMON_NO_INTERFACEIDS="45001";		
	public static String ERROR_COMMON_WHILE_GETTING_SCHEDULER_OBJECT="45002";
	
	public static String ERROR_COMMON_NODE_SCHEDULING="45003";
	public static String ERROR_COMMON_NODE_INITIALIZATION="45004";
	public static String ERROR_COMMON_MAX_NODE_CHECK_REACH="45005";
	public static String ERROR_COMMON_NODE_DETAIL_NOT_FOUND="45006";
	
	
	// Added by Sanjay for Claro CS5	
	public static String ERROR_100_VAL_STAGE="44024";
	public static String ERROR_100_TOP_STAGE="44026";
	public static String OTHER_IN_EXCEPTION="44025";
	public static String ERROR_NOT_PREPAID_SERVICE_CLASS="44023";
	
	public static String VOMS_VAS_SERVICE_BUNDLE_NOT_UPDATE="17044";
	//Entries for Data bundle recharge
      public static String DRC_ERROR_RESPONSE="17030";

	 public static String ERROR_AMBIGUOUS_RETRY_BALANCE_SAME="44027";
    //Changes GP Phase-II
    	 public String	DEDICATED_ACCOUNT_NOT_ALLOWED="44028";
    	 public String	DEDICATED_ACCOUNT_NEGATIVE="44029";
  	public String	VOUCHER_STATUS_USED_BY_SAME="44030";
  	public String	VOUCHER_STATUS_USED_BY_DIFFERENT="44031";
  	public String	VOUCHER_STATUS_UNAVAILABLE="44032";
  	public String	VOUCHER_STATUS_EXPIRED="44033";
  	public String	VOUCHER_STATUS_STOLEN_OR_MISSING="44034";
  	public String	VOUCHER_STATUS_DAMAGED="44035";
  	public String	VOUCHER_TYPE_NOT_ACCEPTED="44036";
  	public String	SERVICE_CLASS_CHANGE_NOT_ALLOWED="44037";
  	public String	INVALID_VOUCHER_ACTIVATION_CODE="44038";
  	public String	SUPERVISION_PERIOD_TOO_LONG="44039";
  	public String	SERVICE_FEE_PERIOD_TOO_LONG="44040";
  	public String	ACCUMULATOR_NOT_AVAILABLE="44041";
  	public String	GET_BALANCE_AND_DATE_NOT_ALLOWED="44042";
  	public String	OPERATION_NOT_ALLOWED_FROM_CURRENT_LOCATION="44043";
  	public String	FAILED_TO_GET_LOCATION_INFORMATION="44044";
  	public String	INVALID_DEDICATED_ACCOUNT_PERIOD="44045";
  	public String	INVALID_DEDICATED_ACCOUNT_START_DATE="44046";
  	public String	OFFER_NOT_FOUND="44047";
  	public String	INVALID_UNIT_TYPE="44048";
  	public String	REFILL_DENIED_FIRST_IVR_CALL_NOT_MADE="44049";
  	public String	REFILL_DENIED_ACCOUNT_NOT_ACTIVE="44050";
  	public String	REFILL_DENIED_SERVICE_FEE_PERIOD_EXPIRED="44051";
  	public String	REFILL_DENIED_SUPERVISION_PERIOD_EXPIRED="44052";
  	public String	PERIODIC_ACCOUNT_MANAGEMENT_EVALUATION_FAILED="44053";
  	public String	OFFER_START_DATE_NOT_CHANGED_AS_OFFER_ALREADY_ACTIVE="44054";
  	public String	SHARED_ACCOUNT_OFFER_NOT_ALLOWED_SUBORDINATE_SUBSCRIBER="44055";
  	public String	ATTRIBUTE_NAME_NOT_EXIST="44056";
  	public String	CAPABILITY_NOT_AVAILABLE="44057";
  	public String	ATTRIBUTE_UPDATE_NOT_ALLOWED_FOR_ATTRIBUTE="44058";
     //Changes end
    public static String ERROR_DATAPACK_NO_INTERFACEIDS="30033";
    public static String ERROR_DATAPACK_WHILE_GETTING_SCHEDULER_OBJECT="30034";
    public static String ERROR_DATAPACK_NODE_SCHEDULING="30035";
    public static String ERROR_DATAPACK_NODE_INITIALIZATION="30011";
    public static String ERROR_DATAPACK_MAX_NODE_CHECK_REACH="30012";
    public static String ERROR_DATAPACK_NODE_DETAIL_NOT_FOUND="30013";
    public static String ERROR_DATAPACK_RESPONSE_STATUS_INVALID="30014";
    public static String ERROR_DATAPACK_RESPONSE_BAD_REQUEST="400";
    public static String ERROR_COMBOPACK_NO_INTERFACEIDS="30015";
    public static String ERROR_COMBOPACK_WHILE_GETTING_SCHEDULER_OBJECT="30016";
    public static String ERROR_COMBOPACK_NODE_SCHEDULING="30017";
    public static String ERROR_COMBOPACK_NODE_INITIALIZATION="30018";
    public static String ERROR_COMBOPACK_MAX_NODE_CHECK_REACH="30019";
    public static String ERROR_COMBOPACK_NODE_DETAIL_NOT_FOUND="30020";
    public static String ERROR_COMBOPACK_RESPONSE_STATUS_INVALID="30036";
    public static String ERROR_COMBOPACK_RESPONSE_SUBSCRIBER_INVALID_STATE1="30037";
    public static String ERROR_COMBOPACK_RESPONSE_SUBSCRIBER_INVALID_STATE2="30038";
    public static String ERROR_COMBOPACK_RESPONSE_SUBSCRIBER_INVALID_STATE3="30039";
    public static String ERROR_COMBOPACK_RESPONSE_SUBSCRIBER_INVALID_PROD_REQ="30040";
    public static String ERROR_COMBOPACK_RESPONSE_SUBSCRIBER_INVALID_PROD_MAPPING_MAIN="30041";
    public static String ERROR_COMBOPACK_RESPONSE_SUBSCRIBER_INVALID_PARENT_CARD_STATE="30042";
    public static String ERROR_COMBOPACK_RESPONSE_TXNNOTALLOWED_NORMAL_CHILDUSR="30043";
    public static String ERROR_COMBOPACK_REQUEST_ALREADY_PROCESSED="30044";
    public static String ERROR_COMBOPACK_RESPONSE_APPLYDATETIME_NOTBEFORE_EXPIRED="30045";
    public static String ERROR_COMBOPACK_RESPONSE_MUTUALLY_EXCLUSIVE_PRODUCTS="30046";
    public static String ERROR_COMBOPACK_RESPONSE_UNDEFINED_ERROR_STATE="30047";
	  public static String OLO_ERROR_RESPONSE_INVALID_KEY="18001";
	  public static String OLO_ERROR_RESPONSE_APPLICATION_FAIL="18002";
	  public static String OLO_ERROR_RESPONSE_CUSTOMER_NOT_EXIST="18003";
	  public static String OLO_ERROR_RESPONSE_DUPLICATE_ENTRY="18004";
	  public static String EXT_REF_CODE_LENGTH_EXCEEDS="18005";
	  public static String OLO_BANK_CODE_NOT_EXIST="18006";
	  public static String OLO_BANK_CODE_LENGTH_EXCEEDS="18007";
	  public static String OLO_DATE_LENGTH_EXCEEDS="18008";
	  
	  //added for airtel huawaie IN
		public static String MAIN_ACCOUNT_TYPE_NOT_FOUND="30501";
		public static String DBRC_ERROR_RESPONSE = "17007";
		
	    public static String ERROR_RADIX_NODE_INITIALIZATION = "19004";
	    public static String ERROR_RADIX_NODE_SCHEDULING = "19003";
	    public static String ERROR_RADIX_NODE_DETAIL_NOT_FOUND = "19006";
	    public static String ERROR_RADIX_NO_INTERFACEIDS = "19001";
	    public static String ERROR_RADIX_WHILE_GETTING_SCHEDULER_OBJECT = "19002";
	    
		//Saficom postpaid in response
		public String INTERFACE_MSISDN_NOT_VALID_POSTPAID_NO="25010";
		public String INTERFACE_UNKONOW_ERROR_ON_POSTPAID="25011";
		public String INTERFACE_AMT_PAID_NEGATIVE_OR_ZERO="25012";
		public String INTERFACE_MSISDN_NOT_VALID_POSTPAID_NO_AND_NEGATIVE_OR_ZERO="25013";
		public static String ERROR_GENERATE_INTXNID="20003";
		//Error codes defined for Huawae IN.
	    public static String ERROR_IDLE_SUBS_CASH_RCH="7100";
	    public static String ERROR_IDLE_SUBS_VOUCHER_RCH="7101";
	    public static String ERROR_SUSPEND_SUBS_VOUCHER_RCH="7102";
	    public static String ERROR_DISABLED_SUBS_VOUCHER_RCH="7103";
	    public static String ERROR_BAL_EXCEED_MAXBAL="7104";
	    public static String ERROR_RC_FAIL_EXCEED_MAXBAL="7105";
	    public static String ERROR_SUBSCRIBER_DEREGISTRATION="7106";
	    public static String ERROR_RC_FAIL_IDLE_SUBS="7107";
		public static String OFFER_ID_INVALID="17092";
	  
	  
}
