package com.selftopup.pretups.inter.module;

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

}
