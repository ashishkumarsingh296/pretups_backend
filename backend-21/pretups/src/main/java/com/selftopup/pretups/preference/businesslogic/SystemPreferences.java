package com.selftopup.pretups.preference.businesslogic;

/**
 * @(#)SystemPreferences.java
 *                            Copyright(c) 2005, Bharti Telesoft Int. Public
 *                            Ltd.
 *                            All Rights Reserved
 *                            This class is used to store System Preferences for
 *                            Pretups System.
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            Abhijit Chauhan June 10,2005 Initial Creation
 *                            Babu Kunwar Feb-10-2011 User Event Remarks
 *                            --------------------------------------------------
 *                            ----------------------------------------------
 */

import java.util.Date;

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;

public class SystemPreferences {

    private static Log _log = LogFactory.getLog(SystemPreferences.class.getName());
    public static int AMOUNT_MULT_FACTOR = 0;
    public static int MIN_MSISDN_LENGTH = 0;
    public static int MAX_MSISDN_LENGTH = 0;
    public static int MSISDN_PREFIX_LENGTH = 0;
    public static String MSISDN_PREFIX_LIST;
    public static String DEFAULT_SUBSCRIBER_TYPE;

    public static long SKEY_EXPIRY_TIME = 0;
    public static int SKEY_LENGTH = 0;

    public static String P2P_PERIOD_TO_BE_USED = null;
    public static String MIN_RESIDUAL_BAL_TYPE = null;
    public static boolean PIN_REQUIRED;
    public static int PIN_LENGTH = 0;
    public static int C2S_MAX_PIN_BLOCK_COUNT = 0;
    public static int P2P_MAX_PIN_BLOCK_COUNT = 0;
    public static boolean IS_TAX2_ON_TAX1;
    public static String SUBSCRIBER_FAIL_CTINCR_CODES = null;
    public static boolean SKEY_REQUIRED = true;
    public static String SKEY_DEFAULT_SENT_TO;
    public static String SYSTEM_DATE_FORMAT;

    public static String SYSTEM_DATETIME_FORMAT;
    public static int DAILY_CNSTIVE_FAIL_COUNT_BEFOREBAR = 0;
    public static String DEFAULT_LANGUAGE = null;// for default language
    public static String DEFAULT_COUNTRY = null;// for default country
    public static boolean USE_PPAID_USER_DEFINED_CONTROLS;
    public static String LANGAUGES_SUPPORTED = null;
    public static String C2S_USER_REGISTRATION_REQUIRED = null;
    public static boolean C2S_ALLOW_SELF_TOPUP;
    public static boolean USE_C2S_SEPARATE_TRNSFR_COUNTS;
    public static long C2S_PIN_BLK_RST_DURATION;
    public static long P2P_PIN_BLK_RST_DURATION;
    public static int C2S_DAYS_AFTER_CHANGE_PIN;
    public static int P2P_DAYS_AFTER_CHANGE_PIN;
    public static int MAX_MSISDN_TEXTBOX_LENGTH;
    public static boolean C2S_SNDR_CREDIT_BK_AMB_STATUS;
    public static boolean P2P_SNDR_CREDIT_BK_AMB_STATUS;
    public static boolean P2P_SNDR_CREDIT_BACK_ALLOWED;
    public static long PASSWORD_BLK_RST_DURATION;
    public static int MAX_PASSWORD_BLOCK_COUNT;

    public static String TRANSFER_DEFAULT_SERVICETYPE;
    public static boolean USE_ALTERNATE_SRVCTYPE_IFFAIL_IN_DEFAULT;
    public static String TRSFR_ALT_SRVCTYPE;
    public static int MAX_USER_HIERARCHY_SIZE;
    public static String DEFAULT_PRODUCT;
    public static int C2S_PIN_MIN_LENGTH;
    public static int C2S_PIN_MAX_LENGTH;
    public static int MAX_LOGIN_PWD_LENGTH;
    public static int MIN_LOGIN_PWD_LENGTH;
    public static int MAX_SMS_PIN_LENGTH;
    public static int MIN_SMS_PIN_LENGTH;
    public static String C2S_DEFAULT_SMSPIN;
    public static String C2S_DEFAULT_PASSWORD;
    public static int CRYSTAL_REPORT_MAX_DATEDIFF;
    public static int REPORT_MAX_DATEDIFF;
    public static int MAX_DATEDIFF;
    public static String DEFAULT_PIN;
    public static String P2P_DEFAULT_SMSPIN;
    public static int PIN_PASSWORD_ALERT_DAYS = 0;
    public static int DAYS_AFTER_CHANGE_PASSWORD = 0;
    public static String CHANGE_PASSWORD_NOT_REQUIRED_CATEGORY;
    public static String CHNL_PLAIN_SMS_SEPARATOR = " ";
    public static String UNCONTROLL_TRANSFER_LEVEL;
    public static String C2S_TRANSFER_DEF_SELECTOR;
    public static int C2S_TRANSFER_DEF_SELECTOR_CODE = 1;
    public static String P2P_TRANSFER_DEF_SELECTOR;
    public static int P2P_TRANSFER_DEF_SELECTOR_CODE = 1;

    // added for credit transfer & credit recharge
    // public static int P2P_CREDIT_RECHARGE_DEF_SELECTOR_CODE=1;

    // end
    public static String P2P_PLAIN_SMS_SEPARATOR = " ";
    public static String GROUP_ROLE_ALLOWED;
    public static String DEF_FRCXML_SEL_P2P;
    public static String DEF_FRCXML_SEL_C2S;
    public static boolean EXTERNAL_TXN_UNIQUE;
    public static int FINANCIAL_YEAR_START_INDEX = 0;
    // Added for the ScheduledTopUp Process APR 24 2006
    public static int C2S_TRANSFER_DEF_SELECTOR_SCH_CODE = 2;
    public static String C2S_TRANSFER_DEF_SELECTOR_SCH;

    public static boolean EXTERNAL_TXN_NUMERIC;

    public static String EXTERNAL_TXN_MANDATORY_DOMAINTYPE = null;
    public static String EXTERNAL_TXN_MANDATORY_FORFOC = null;
    // added for notification message by service class date 12/05/06
    public static boolean NOTIFICATION_SERVICECLASSWISE_SEN;
    public static boolean NOTIFICATION_SERVICECLASSWISE_REC;
    public static boolean NOTIFICATION_SERVICECLASSWISE_REC_C2S;
    public static boolean CHK_BLK_LST_STAT;

    // added for postpaid bill payment
    public static String C2S_BILLPAYMENT_DEF_SELECTOR;// date 15/05/06
    public static boolean NOTIFICATION_SERVICECLASSWISE_REC_BILLPAY; // date
                                                                     // 16/05/06
    public static boolean C2S_ALLOW_SELF_BILLPAY;// date 17/05/06
    public static boolean C2S_SNDR_CREDIT_BK_AMB_STATUS_BILLPAY;// date 17/05/06
    // public static int C2S_DEF_SELECTOR_CODE_BILLPAY=1;//date 26/05/06
    public static String DEF_FRCXML_SEL_BILLPAY;// date 31/05/06
    public static boolean SEP_OUTSIDE_TXN_CTRL;
    public static boolean POSTPAID_REGISTER_AS_ACTIVATED_STATUS; // Whether to
                                                                 // register
                                                                 // Postpaid
                                                                 // User as
                                                                 // activated
                                                                 // status or
                                                                 // not
    public static boolean SPACE_ALLOW_IN_LOGIN;// in login id space allow or not
    public static String DEFAULT_WEB_GATEWAY_CODE = null;
    public static String SECOND_LANGUAGE_ENCODING = null; // Encoding to be used
                                                          // for push message
                                                          // and btslutil
    public static String GRPT_CTRL_ALLOWED;
    public static String GRPT_CHRG_ALLOWED;
    public static String GRPT_CONTROL_LEVEL;
    // Whether to allow Same MSISDN P2P transfer or not (Values =Y/N), for
    // Credit Transfer, post to pre,
    // pre to post - may be allowed in validity extension
    public static boolean P2P_ALLOW_SELF_TOPUP;
    // entery for check the external code mandatory for the batch foc.
    public static boolean EXTERNAL_CODE_MANDATORY_FORFOC;
    public static String SECOND_LANGUAGE_CHARSET = null; // Encoding to be used
                                                         // for message Cache

    // new entry is added for the c2c return as return to the parent user only
    public static boolean C2C_RET_PARENT_ONLY = false;
    // ends here

    public static String DECIMAL_ALLOWED_IN_SERVICES;

    // new entry for the date format of external system
    public static String EXTERNAL_DATE_FORMAT;
    public static int XML_MAX_RCD_SUM_RESP;
    public static int XML_DATE_RANGE;
    public static int XML_DFT_DATE_RANGE;

    // added by sourabh for c2sbillpayment controller
    public static boolean C2S_ALLOW_SELF_UB;
    // public static int C2S_DEF_SELECTOR_CODE_UB ;
    public static boolean C2S_SNDR_CRDT_BK_AMB_UB;
    public static boolean NOTIFICATION_SERVICECLASSWISE_REC_UB;
    public static int MIN_IDENTIFICATION_NUMBER_LENGTH = 0;
    public static int MAX_IDENTIFICATION_NUMBER_LENGTH = 0;
    public static boolean USE_HOME_STOCK = false;

    public static String OTHERID_PREFIX_LIST;
    public static String IDENTIFICATION_NUMBER_VAL_TYPE;
    public static boolean ALPHA_ID_NUM_ALLOWED = false;
    public static boolean RESPONSE_IN_DISPLAY_AMT = false;

    // added for change status
    public static long MAX_DENOMINATION_VAL = 0;
    public static String VOMS_UPEXPHOURS = null;
    public static String VOMS_OFFPEAKHRS = null;
    public static long VOMS_MAX_TOTAL_VOUCHER_OT;
    public static long VOMS_MAX_TOTAL_VOUCHER_EN;
    public static long VOMS_MAX_ERROR_COUNTEN = 0;
    public static long VOMS_MAX_ERROR_COUNTOTH = 0;
    public static int VOMS_MAX_BATCH_DAY;
    public static String VOMS_DATE_FORMAT;

    // Added by Amit Ruwali
    public static boolean SELF_VOUCHER_DISTRIBUTION_ALLOWED;
    public static String PIN_SEND_TO;
    public static boolean DELIVERY_RECEIPT_TRACKED;
    public static boolean CREDIT_BACK_ALWD_EVD_AMB;
    public static boolean NOTIFICATION_SERVICECLASSWISE_REC_EVD;
    // public static boolean BUDDY_PIN_REQUIRED;
    // Added by Dhiraj on 130307
    public static boolean FOC_SMS_NOTIFY = false;
    // Added by vikas for Associate profile
    public static boolean PROFILEASSOCIATE_AGENT_PREFERENCES = false;
    public static int VOMS_SERIAL_NO_MIN_LENGTH;
    public static int VOMS_SERIAL_NO_MAX_LENGTH;
    public static int VOMS_PIN_MIN_LENGTH;
    public static int VOMS_PIN_MAX_LENGTH;
    public static int STAFF_USER_COUNT;
    public static boolean PORT_OUT_USER_SUSPEND_REQUIRED = false;
    public static boolean MNP_ALLOWED = false;
    public static boolean DIRECT_VOUCHER_ENABLE = false;

    // Entries for MVD
    public static int MVD_MAX_VOUCHER;

    // for Zebra and Tango added by sanjeew date 06/07/07
    public static boolean PTUPS_MOBQUTY_MERGD = false;
    public static boolean AUTO_PAYMENT_METHOD = true;

    // for OCI Changes added by Ashish S date 16/07/07
    public static String LOW_BAL_MSGGATEWAY;

    // Get number back service
    public static String ALWD_NUMBCK_SERVICES;
    public static String RC_ALWD_ACC_STATUS_NUMBCK;
    public static boolean RC_NUMBCK_DIFF_REQ_TO_IN = false;
    public static String RC_NUMBCK_ALWD_DAYS_DIFF;
    public static String RC_NUMBCK_AMT_DEDCTED;
    public static String PRC_ALWD_ACC_STATUS_NUMBCK;
    public static boolean PRC_NUMBCK_DIFF_REQ_TO_IN = false;
    public static String PRC_NUMBCK_ALWD_DAYS_DIFF;
    public static String PRC_NUMBCK_AMT_DEDCTED;

    // BTRC implementation requires these thresholds for P2P-AshishK [07/09/07].
    public static long WE_REC_AMT_ALLWD_P2P;
    public static long WE_SUCTRAN_ALLWD_P2P;
    public static long MO_REC_AMT_ALLWD_P2P;
    public static long MO_SUCTRAN_ALLWD_P2P;
    public static long MAX_ALLD_BALANCE_P2P;
    // for C2S-AshishK [07/09/07].
    public static long WE_SUCTRAN_ALLWDCOUN;
    public static long WE_TOTXN_AMT_ALLWDCO;
    public static long MO_SUCTRAN_ALLWDCOUN;
    public static long MO_TOTXN_AMT_ALLWDCO;
    public static long MAX_ALLD_BALANCE_C2S;

    // When Payable amount is different from the Denomination Amount then
    // preference will be used to show the field on the denomination screen or
    // not
    // preference will be used at the time of message push
    public static boolean PAYAMT_MRP_SAME = false;

    // To enquire Transaction Status with External Refrence Number -
    // Vipul[23/10/2007]
    public static boolean C2S_REF_NUMBER_REQUIRED;
    public static boolean C2S_REF_NUMBER_UNIQUE;
    // added by santanu for password/pin management
    public static int PREV_PASS_NOT_ALLOW;
    public static int PREV_PIN_NOT_ALLOW;
    // added by PN for cellplus controller
    public static boolean IS_FEE_APPLICABLE_FOR_VALIDATION_EXTENSION = true;
    public static String FEE_AND_VALIDITY_DAYS_TO_EXT = "0,0";
    public static boolean IS_CREDIT_TRFR_WITH_VALIDITY_UPDATION_P2P = true;
    public static int VAL_DAYS_TO_CHK_VALUPD = 30;

    // added for pin and password block duration
    public static long C2S_PWD_BLK_EXP_DURATION;
    public static long C2S_PIN_BLK_EXP_DURATION;
    public static long P2P_PIN_BLK_EXP_DURATION;

    // Added for separate database reports on 26/02/2008
    public static boolean IS_SEPARATE_RPT_DB;

    public static boolean ALLOW_SELF_EVR;

    // Added for Negative Additional commission on 21/04/08
    public static boolean NEG_ADD_COMM_APPLY;

    public static boolean IS_SEPARATE_BONUS_REQUIRED;
    // Receiver message suppresion
    public static boolean REC_MSG_SEND_ALLOW = true;

    // Operator user approval level.
    public static int OPT_USR_APRL_LEVEL = 0;
    // User Approval page mode flag(it can be editable or view).
    public static boolean APPROVER_CAN_EDIT = false;

    // for enableing and dissabling the send pin button
    public static String DISSABLE_BUTTON_LIST = null;

    public static long P2P_REG_EXPIRY_PERIOD;
    // If this flag is true then system will send SMS to primary number also if
    // transaction is done through secondary number
    public static boolean MESSAGE_TO_PRIMARY_REQUIRED = false;
    // If this flag is true then system will allow transaction through secondary
    // mobile number like C2C, O2C, view user details etc
    public static boolean SECONDARY_NUMBER_ALLOWED = false;

    // Added by Gopal on 10/11/08 for batch c2c transfer SMS Notification
    public static boolean C2C_SMS_NOTIFY = false;
    // added by vikas kumar for card group updation in sms/mms
    public static boolean SMS_MMS_ALLOWED;
    public static long CIRCLEMINLMT = 0;
    // Add for PIN validation is requird in CP2P services 07/01/2009
    public static boolean CP2P_PIN_VALIDATION_REQUIRED = true;

    public static boolean FIXLINE_RC_ALLOW_SELF_TOPUP;
    public static boolean BROADBAND_RC_ALLOW_SELF_TOPUP;
    public static boolean C2S_RANDOM_PIN_GENERATE;
    public static boolean WEB_RANDOM_PWD_GENERATE;
    // to allow to modify the password during batch user modify.
    public static boolean BATCH_USER_PASSWD_MODIFY_ALLOWED;
    public static int RESET_PASSWORD_EXPIRED_TIME_IN_HOURS = 0;
    public static int RESET_PIN_EXPIRED_TIME_IN_HOURS = 0;

    public static String C2S_CARD_GROUP_SLAB_COPY;
    public static String P2P_CARD_GROUP_SLAB_COPY;
    public static boolean STK_REG_ICCID; // TRUE if registration using ICCID
                                         // otherwise FALSE for IMSI
    // Added for activation bonus
    public static String ACTIVATION_BONUS_MIN_REDEMPTION_AMOUNT;
    public static String ACTIVATION_BONUS_REDEMPTION_DURATION;
    public static String VOLUME_CALC_ALLOWED;
    public static boolean POSITIVE_COMM_APPLY;

    // added for auto generating password/pin
    public static boolean AUTO_PWD_GENERATE_ALLOW;
    public static boolean AUTO_PIN_GENERATE_ALLOW;
    public static int PSWD_EXP_TIME_IN_HOUR_AFTER_CREATION = 0;
    // For IAt ambiguous transactions for receiver zeba
    public static boolean CHECK_REC_AMBIGUOUS_TXN_AT_IAT = false;
    // IAT is enabled in zeba or not
    public static boolean IS_IAT_RUNNING = false;
    public static int POINT_CONVERSION_FACTOR = 0;

    // added by Vikram for C2S recharge screen change.
    public static String C2S_RECHARGE_MULTIPLE_ENTRY = null;

    // VIKRAM FOR VFE Last 3 transfer
    public static int LAST_X_TRANSFER_STATUS;
    public static String SERVICE_FOR_LAST_X_TRANSFER;

    // customer enquiry
    public static int LAST_X_CUSTENQ_STATUS;

    // added for multiple wallet
    public static boolean MULTIPLE_WALLET_APPLY = false;

    // reverse trx
    public static int RVERSE_TRN_EXPIRY;
    public static int RVE_C2S_TRN_EXPIRY;
    // allowed reverse txn services
    public static String ALWD_REVTXN_SERVICES;
    public static String CHNL_PLAIN_SMS_SEPT_LOGINID = null;

    // added by Lohit for Direct Payout
    public static boolean EXTERNAL_CODE_MANDATORY_FORDP;
    public static String EXTERNAL_TXN_MANDATORY_FORDP = null;
    public static int DP_ORDER_APPROVAL_LVL = 0;
    public static String EXTERNAL_TXN_MANDATORY_DOMAINTYPE_DP = null;
    public static boolean DP_SMS_NOTIFY = false;
    public static boolean DP_ALLOWED = false;
    public static boolean LAST_TRF_MULTIPLE_SMS = false;
    public static int LAST_X_TRF_DAYS_NO;
    public static boolean HTTPS_ENABLE = false;
    public static String SHA2_FAMILY_TYPE;
    public static String PINPAS_EN_DE_CRYPTION_TYPE;

    // variable true if network admin can see all chadm,if false than only under
    // him
    public static boolean NWADM_CROSS_ALLOW = false;
    public static boolean STAFF_AS_USER = false;
    // Added by vikram
    // public static long ZERO_BAL_THRESHOLD_VALUE=0;
    public static boolean PRIVATE_RECHARGE_ALLOWED = false;

    public static String NAMEEMBOSS_SEPT = null;
    public static boolean LOGIN_SPECIAL_CHAR_ALLOWED = false;
    public static int MVD_MIN_VOUCHER;
    public static boolean VOMS_PIN_ENCRIPTION_ALLOWED = false;
    public static int CARD_GROUP_BONUS_RANGE = 0;
    // added by nilesh:for default grade
    public static boolean IS_DEFAULT_PROFILE = false;
    // added by nilesh:for user delete approval
    public static boolean REQ_CUSER_DLT_APP = true;
    // added by nilesh:for O2C_EMAIL_NOTIFICATION
    public static boolean O2C_EMAIL_NOTIFICATION = false;
    public static int FOC_ODR_APPROVAL_LVL = 0;
    // added by nilesh: for auto c2c transfer
    public static int AUTO_C2C_TRANSFER_AMT = 0;
    public static boolean SMS_ALLOWED = false;

    // MVD_SMSC_VOUCHER_LIMIT ashishT
    public static int MVD_MAX_VOUCHER_EXTGW;
    // added by priyanka 21/01/11 for mobilecom
    public static int CRYSTAL_SUMMARY_REPORT_MAX_DATEDIFF;
    // added by jasmine kaur for PRIVATE RECHARGE
    public static boolean SID_ISNUMERIC = true;

    // Added by Amit Raheja for NNP
    public static String MSISDN_MIGRATION_LIST;
    /*
     * Added By Babu Kunwar
     */
    public static boolean USER_EVENT_REMARKS = false;
    // added by Ankuj for SID Deletion
    public static int MIN_SID_LENGTH = 0;
    public static int MAX_SID_LENGTH = 15;

    public static boolean PRIVATE_SID_SERVICE_ALLOW = false;
    public static String MULT_CRE_TRA_DED_ACC_SEP = null;
    public static String EXTERNAL_TXN_MANDATORY_FORO2C = null;
    // entery for check the external code mandatory for the batch foc.
    public static boolean EXTERNAL_CODE_MANDATORY_FORO2C;

    // Added for DWH Changes by Anu garg
    public static String RP2PDWH_OPT_SPECIFIC_PROC_NAME = null;
    public static String P2PDWH_OPT_SPECIFIC_PROC_NAME = null;
    public static String IATDWH_OPT_SPECIFIC_PROC_NAME = null;

    // Added for the private recharge differnent Message Gateway Short Code
    public static String PRIVATE_RECH_MESSGATEWAY;

    // added for roam recharge
    public static boolean ALLOW_ROAM_ADDCOMM = false;

    // added for GiveMeBalance(GMB) response
    // public static String INTERACTIVE_OPTION_ALLOWED;
    // public static long MENU_CODE;
    public static boolean PLAIN_RES_PARSE_REQUIRED = false;
    public static String COUNTRY_CODE;
    public static boolean USSD_NEW_TAGS_MANDATORY;

    // Added by Chhaya on 11/11/11 for batch o2c withdraw SMS Notification
    public static boolean O2C_SMS_NOTIFY = false;
    public static String OWNER_CATEGORY_LIST = null;
    public static String PARENT_CATEGORY_LIST = null;

    public static int AUTO_FOC_TRANSFER_AMOUNT = 0;
    public static int MAX_AUTO_FOC_ALLOW_LIMIT = 20;
    // added by nilesh : for MRP block time
    public static boolean MRP_BLOCK_TIME_ALLOWED = false;
    // Tunisia specific
    public static boolean ACTIVATION_FIRST_REC_APP = false;
    public static int SOS_SETTLE_DAYS;
    // added for SOS recharge service
    public static long SOS_RECHARGE_AMOUNT;
    public static int SOS_MIN_VALIDITY_DAYS;
    public static long SOS_ALLOWED_MAX_BALANCE;
    // public static String SOS_ALLOWED_MAX_BALANCE;
    public static boolean SOS_ST_DEDUCT_UPFRONT = false;
    public static int SOS_DAYS_GAP_BTWN_TWO_TRAN;
    public static String SERV_CLASS_ALLOW_FOR_SOS;
    // public static boolean SOS_SERV_CLASS_BASED_OR_AON=true;
    public static int SOS_MINIMUM_AON = 0;
    public static String SOS_ELIBILITY_ACCOUNT;
    public static boolean SOS_ONLINE_ALLOW = false;
    public static boolean ENQ_POSTBAL_IN = false;
    public static boolean DFLT_PIN_FLAG_FOR_USR_CREATION_RESET;
    public static boolean DFLT_PWD_FLAG_FOR_USR_CREATION_RESET;
    public static boolean ENQ_POSTBAL_ALLOW = false;
    public static int LMB_VALIDITY_DAYS_FORCESETTLE = 120;
    public static boolean LMB_BLK_UPL = false;// added by ankuj for LMB bulk
                                              // upload

    public static boolean LMB_FORCE_SETL_STAT_ALLOW = false;
    public static int VOMS_MAX_APPROVAL_LEVEL = 2;
    public static boolean VOMS_USER_KEY_REQD = false;
    public static boolean DB_ENTRY_NOT_ALLOWED = false; // consolidated for
                                                        // logger : added by
                                                        // nilesh

    // Added by Amit Raheja for reverse txn
    public static int RVERSE_TXN_APPRV_LVL = 0;
    // added for VMS module part O2C/C2C
    public static boolean VOUCHER_TRACKING_ALLOWED = false;
    public static boolean VOUCHER_EN_ON_TRACKING = false;
    public static boolean PROCESS_FEE_REV_ALLOWED = false;
    // Added for Multiple Credit List CR
    public static int MCDL_MAX_LIST_COUNT;
    public static String MCDL_DIFFERENT_REQUEST_SEPERATOR;
    public static int P2P_MCDL_DEFAULT_AMOUNT = 0;
    public static int P2P_MCDL_MAXADD_AMOUNT = 0;
    public static int P2P_MCDL_AUTO_DELETION_DAYS = 90;
    public static boolean LMB_DEBIT_REQ = true;
    public static String PRVT_RC_MSISDN_PREFIX_LIST = null;// added by rahul for
                                                           // private recharge
    public static boolean IS_FNAME_LNAME_ALLOWED = false;// added by deepika
                                                         // aggarwal korek tel
                                                         // modifications
    // RAVI FOR AB Last N Recharge
    public static int LAST_X_RECHARGE_STATUS;
    public static String SERVICE_FOR_LAST_X_RECHARGE = "C2S";
    public static String SMS_PIN_BYPASS_GATEWAY_TYPE;
    // Added for CP User Registration Feature
    public static String CATEGORY_ALLOWED_FOR_CREATION = null;
    public static int CP_SUSPENSION_DAYS_LIMIT = 0;
    // For External Code Mandatory Changes : added by harsh
    public static boolean EXTERNAL_CODE_MANDATORY_FORUSER;
    // public static Date MIGRATION_DATE=null;
    // VASTRIX added by hitesh
    public static boolean SELECTOR_INTERFACE_MAPPING = false;// Vas service
                                                             // based
    public static boolean SERVICE_PROVIDER_PROMO_ALLOW = false;
    // To Encrypt the response message through External System : added by harsh
    public static String EXT_VOMS_MSG_DESEDECRYPT_KEY = null;

    public static boolean LOGIN_ID_CHECK_ALLOWED;// Anupam Malviya
    // added for staff user approval
    public static int STAFF_USER_APRL_LEVEL;
    // added for tigo hounduras moved LOGIN_PASSWORD_ALLOWED from TypesI to
    // system preferences
    public static boolean LOGIN_PASSWORD_ALLOWED;

    // For RSA authentication at User Category level 21/10/2009
    public static boolean RSA_AUTHENTICATION_REQUIRED = false;
    // Added by Ankur for batch user creation by channel users
    public static boolean BATCH_USER_PROFILE_ASSIGN;
    public static String BATCH_INTIATE_NOTIF_TYPE = "SMS";
    // For Transfer Rule Type at User level 24/09/2009
    public static boolean IS_TRF_RULE_USER_LEVEL_ALLOW;
    // Email for pin & password
    public static boolean IS_EMAIL_SERVICE_ALLOW;
    // / added for cos required
    public static boolean COS_REQUIRED = false;
    // /added for promotional transfer rule
    public static boolean CELL_GROUP_REQUIRED;

    public static String SRVC_PROD_MAPPING_ALLOWED = null;
    public static String SRVC_PROD_INTFC_MAPPING_ALLOWED = null;
    public static String SRVCS_FOR_PROD_MAPPING = null;

    public static boolean CELL_ID_SWITCH_ID_REQUIRED = false;
    public static boolean MULTI_AMOUNT_ENABLED;
    public static boolean IN_PROMO_REQUIRED = false;
    // sim activation
    public static boolean DEBIT_SENDER_SIMACT = false;
    public static String SIMACT_DEFAULT_SELECTOR = null;
    public static int AUTO_O2C_MAX_APPROVAL_LEVEL = 0;
    public static int AUTO_O2C_AMOUNT = 0;

    // barred for deletion added by shashank
    public static int REQ_CUSER_BAR_APPROVAL;
    // For Captcha- Vibhu
    public static boolean SHOW_CAPTCHA = false;
    // Email for pin & password- authorization
    public static boolean EMAIL_AUTH_REQ = false;
    public static int MIN_HRDIF_ST_ED_LMS;
    public static int MIN_HRDIF_CR_ST_LMS;
    public static boolean LMS_APPL = false;
    // added by harsh for setting default scheduling frequency in Scheduled
    // Credit List
    public static long P2P_SMCDL_DEFAULT_FREQUENCY = 3;
    public static String P2P_SMCDL_ALLOWED_SCHEDULE_TYPE;
    // For Separate External Database- 18/12/13
    public static boolean IS_SEPARATE_EXT_DB = false;
    // LMS Hierarchy-Wise Promotion- 27/12/13
    public static boolean PROMOTION_HIERARCHY_WISE = false;
    public static boolean LMS_VOL_COUNT_ALLOWED = false;
    public static boolean LMS_PROF_APR_ALLOWED = false;

    public static boolean AUTO_O2C_APPROVAL_ALLOWED = false;
    // Added by abhilasha for Authentication Type System Preference
    public static boolean AUTH_TYPE_REQ = false;
    // Added For Fetiching user information
    public static boolean SAP_INTEGARATION_FOR_USRINFO = false;

    public static int MIN_ACCOUNT_ID_LENGTH = 0;
    public static int MAX_ACCOUNT_ID_LENGTH = 0;
    // Added for Blocking Amount For some service type
    public static String C2S_TRNSFR_AMTBLCK_SRVCTYP;
    public static int OTP_TIMEOUT_INSEC = 0;
    public static String C2S_TRNSFR_INVNO_SRVCTYP;
    public static int O2C_PNDNG_FOR_APPR_SEND_MAIL_DAYS_CNT = 0;

    public static String C2S_REVERSAL_TXNID_SRVCTYP;
    public static boolean LMS_STOCK_REQUIRED = false;
    public static String LMS_MULT_FACTOR = null;
    public static boolean LMS_VOL_CREDIT_LOYAL_PTS = false;

    // Added by Diwakar on 05-FEB-2014
    // user approval level through external system.
    public static int EXTSYS_USR_APRL_LEVEL_REQUIRED = 0;
    // Ended Her
    public static int MAX_LAST_TRANSFERS_DAYS;

    // Captcha authentication in login page by akanksha
    public static int INVALID_PWD_COUNT_FOR_CAPTCHA;
    public static int CAPTCHA_LENGTH;
    // Added by Vikas Singh for Auto Top Up Maximum amount @ the time of user
    // Registration for AUTO TOP UP
    public static long MAX_AUTOTOPUP_AMT;
    // Ended Here
    public static int PAYMENTDETAILSMANDATE_O2C = 0;

    public static int USER_APPROVAL_LEVEL = 0;

    public static String USER_CREATION_MANDATORY_FIELDS;
    public static String SELFTOPUPDWH_OPT_SPECIFIC_PROC_NAME = null;
    public static int AUTOSTU_NO_DAYS_ALERT = 1;
    public static boolean SELFTOPUP_TOKENIZATION = false;

    public static void load() {
        if (_log.isDebugEnabled())
            _log.debug("load", "Entered");
        try {
            MSISDN_PREFIX_LIST = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE));
            MSISDN_PREFIX_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LENGTH_CODE))).intValue();
            MIN_MSISDN_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH_CODE))).intValue();
            MAX_MSISDN_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue();
            C2S_MAX_PIN_BLOCK_COUNT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_MAX_PIN_BLOCK_COUNT_CODE))).intValue();
            P2P_MAX_PIN_BLOCK_COUNT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MAX_PIN_BLOCK_COUNT))).intValue();
            DAILY_CNSTIVE_FAIL_COUNT_BEFOREBAR = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DAILY_CNSTIVE_FAIL_COUNT_BEFOREBAR))).intValue();
            SYSTEM_DATETIME_FORMAT = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT));
            PIN_REQUIRED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_REQUIRED_CODE))).booleanValue();
            PIN_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_LENGTH_CODE))).intValue();
            DEFAULT_LANGUAGE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
            SKEY_REQUIRED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_REQUIRED))).booleanValue();

            AMOUNT_MULT_FACTOR = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
            SKEY_EXPIRY_TIME = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_EXPIRY_TIME_CODE))).longValue();
            SKEY_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_LENGTH_CODE))).intValue();
            MIN_RESIDUAL_BAL_TYPE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_RESIDUAL_BAL_TYPE_CODE));

            IS_TAX2_ON_TAX1 = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TAX2_ON_TAX1_CODE))).booleanValue();
            SUBSCRIBER_FAIL_CTINCR_CODES = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_FAIL_CTINCR_CODES));
            SKEY_DEFAULT_SENT_TO = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_DEFAULT_SENT_TO));
            SYSTEM_DATE_FORMAT = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));

            USE_PPAID_USER_DEFINED_CONTROLS = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_PPAID_USER_DEFINED_CONTROLS))).booleanValue();
            LANGAUGES_SUPPORTED = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LANGAUGES_SUPPORTED));
            DEFAULT_COUNTRY = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
            C2S_USER_REGISTRATION_REQUIRED = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_USER_REGISTRATION_REQUIRED));
            C2S_ALLOW_SELF_TOPUP = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_ALLOW_SELF_TOPUP))).booleanValue();
            USE_C2S_SEPARATE_TRNSFR_COUNTS = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS))).booleanValue();
            C2S_PIN_BLK_RST_DURATION = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_PIN_BLK_RST_DURATION))).longValue();
            P2P_PIN_BLK_RST_DURATION = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION))).longValue();
            C2S_DAYS_AFTER_CHANGE_PIN = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DAYS_AFTER_CHANGE_PIN))).intValue();
            P2P_DAYS_AFTER_CHANGE_PIN = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DAYS_AFTER_CHANGE_PIN))).intValue();
            MAX_MSISDN_TEXTBOX_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_TEXTBOX_LENGTH))).intValue();
            C2S_SNDR_CREDIT_BK_AMB_STATUS = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue();
            P2P_SNDR_CREDIT_BK_AMB_STATUS = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue();
            P2P_SNDR_CREDIT_BACK_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BACK_ALLOWED))).booleanValue();
            PASSWORD_BLK_RST_DURATION = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PASSWORD_BLK_RST_DURATION))).longValue();
            MAX_PASSWORD_BLOCK_COUNT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_PASSWORD_BLOCK_COUNT))).intValue();
            TRANSFER_DEFAULT_SERVICETYPE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
            MAX_USER_HIERARCHY_SIZE = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_HIERARCHY_SIZE))).intValue();
            DEFAULT_PRODUCT = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT));
            C2S_PIN_MIN_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_PIN_MIN_LENGTH))).intValue();
            C2S_PIN_MAX_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_PIN_MAX_LENGTH))).intValue();
            MAX_LOGIN_PWD_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue();
            MIN_LOGIN_PWD_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue();
            MAX_SMS_PIN_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue();
            MIN_SMS_PIN_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue();
            C2S_DEFAULT_SMSPIN = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN));
            C2S_DEFAULT_PASSWORD = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD));

            CRYSTAL_REPORT_MAX_DATEDIFF = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue();
            REPORT_MAX_DATEDIFF = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_MAX_DATEDIFF))).intValue();
            MAX_DATEDIFF = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_DATEDIFF))).intValue();
            P2P_DEFAULT_SMSPIN = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
            PIN_PASSWORD_ALERT_DAYS = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_PASSWORD_ALERT_DAYS))).intValue();
            DAYS_AFTER_CHANGE_PASSWORD = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DAYS_AFTER_CHANGE_PASSWORD))).intValue();
            CHANGE_PASSWORD_NOT_REQUIRED_CATEGORY = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANGE_PASSWORD_NOT_REQUIRED_CATEGORY));
            CHNL_PLAIN_SMS_SEPARATOR = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR));
            // C2S_TRANSFER_DEF_SELECTOR_CODE=((Integer)(PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE))).intValue();
            // P2P_TRANSFER_DEF_SELECTOR_CODE=((Integer)(PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_TRANSFER_DEF_SELECTOR_CODE))).intValue();
            P2P_PLAIN_SMS_SEPARATOR = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR));
            GROUP_ROLE_ALLOWED = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.GROUP_ROLE_ALLOWED));
            DEF_FRCXML_SEL_P2P = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_P2P));
            DEF_FRCXML_SEL_C2S = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S));

            EXTERNAL_TXN_UNIQUE = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE))).booleanValue();
            FINANCIAL_YEAR_START_INDEX = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.FINANCIAL_YEAR_START_INDEX))).intValue();
            EXTERNAL_TXN_NUMERIC = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_NUMERIC))).booleanValue();

            // added by sandeep for the O2C/FOC transfer for external txn
            // mandatory
            EXTERNAL_TXN_MANDATORY_DOMAINTYPE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_DOMAINTYPE));
            EXTERNAL_TXN_MANDATORY_FORFOC = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_FORFOC));
            // added for notification message by service class date 12/05/06
            NOTIFICATION_SERVICECLASSWISE_SEN = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_SEN))).booleanValue();
            NOTIFICATION_SERVICECLASSWISE_REC = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC))).booleanValue();
            NOTIFICATION_SERVICECLASSWISE_REC_C2S = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_C2S))).booleanValue();
            // added for check of black list
            CHK_BLK_LST_STAT = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHK_BLK_LST_STAT))).booleanValue();
            NOTIFICATION_SERVICECLASSWISE_REC_BILLPAY = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_BILLPAY))).booleanValue();
            C2S_ALLOW_SELF_BILLPAY = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_ALLOW_SELF_BILLPAY))).booleanValue();
            C2S_SNDR_CREDIT_BK_AMB_STATUS_BILLPAY = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CREDIT_BK_AMB_STATUS_BILLPAY))).booleanValue();
            // C2S_DEF_SELECTOR_CODE_BILLPAY=((Integer)(PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEF_SELECTOR_CODE_BILLPAY))).intValue();
            DEF_FRCXML_SEL_BILLPAY = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_BILLPAY));
            SEP_OUTSIDE_TXN_CTRL = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL))).booleanValue();

            POSTPAID_REGISTER_AS_ACTIVATED_STATUS = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.POSTPAID_REGISTER_AS_ACTIVATED_STATUS))).booleanValue();
            SPACE_ALLOW_IN_LOGIN = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SPACE_ALLOW_IN_LOGIN))).booleanValue();
            DEFAULT_WEB_GATEWAY_CODE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE));
            SECOND_LANGUAGE_ENCODING = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SECOND_LANGUAGE_ENCODING));

            GRPT_CHRG_ALLOWED = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED));
            GRPT_CTRL_ALLOWED = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED));
            GRPT_CONTROL_LEVEL = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CONTROL_LEVEL));

            P2P_ALLOW_SELF_TOPUP = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_ALLOW_SELF_TOPUP))).booleanValue();
            EXTERNAL_CODE_MANDATORY_FORFOC = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORFOC))).booleanValue();
            SECOND_LANGUAGE_CHARSET = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SECOND_LANGUAGE_CHARSET));

            C2C_RET_PARENT_ONLY = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_RET_PARENT_ONLY))).booleanValue();
            DECIMAL_ALLOWED_IN_SERVICES = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DECIMAL_ALLOW_SERVICES));
            EXTERNAL_DATE_FORMAT = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT));
            XML_MAX_RCD_SUM_RESP = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.XML_MAX_RCD_SUM_RESP))).intValue();
            XML_DATE_RANGE = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.XML_DATE_RANGE))).intValue();
            XML_DFT_DATE_RANGE = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.XML_DFT_DATE_RANGE))).intValue();

            // added by sourabh for c2sbillpaymentcontroller
            C2S_ALLOW_SELF_UB = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_ALLOW_SELF_UB))).booleanValue();
            // C2S_DEF_SELECTOR_CODE_UB =
            // ((Integer)(PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEF_SELECTOR_CODE_UB))).intValue();
            C2S_SNDR_CRDT_BK_AMB_UB = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CRDT_BK_AMB_UB))).booleanValue();
            NOTIFICATION_SERVICECLASSWISE_REC_UB = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_UB))).booleanValue();
            MIN_IDENTIFICATION_NUMBER_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_IDENTIFICATION_NUMBER_LENGTH))).intValue();
            MAX_IDENTIFICATION_NUMBER_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_IDENTIFICATION_NUMBER_LENGTH))).intValue();
            USE_HOME_STOCK = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue();
            OTHERID_PREFIX_LIST = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTHERID_PREFIX_LIST));
            IDENTIFICATION_NUMBER_VAL_TYPE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IDENTIFICATION_NUMBER_VAL_TYPE));
            ALPHA_ID_NUM_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ALPHA_ID_NUM_ALLOWED))).booleanValue();
            RESPONSE_IN_DISPLAY_AMT = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RESPONSE_IN_DISPLAY_AMT))).booleanValue();

            MAX_DENOMINATION_VAL = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_DENOMINATION_VAL))).longValue();
            VOMS_UPEXPHOURS = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_UPEXPHOURS));
            VOMS_OFFPEAKHRS = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_OFFPEAKHRS));
            VOMS_MAX_TOTAL_VOUCHER_OT = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_TOTAL_VOUCHER_OT))).longValue();
            VOMS_MAX_TOTAL_VOUCHER_EN = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_TOTAL_VOUCHER_EN))).longValue();
            VOMS_MAX_ERROR_COUNTEN = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_ERROR_COUNTEN))).longValue();
            VOMS_MAX_ERROR_COUNTOTH = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_ERROR_COUNTOTH))).longValue();
            VOMS_MAX_BATCH_DAY = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_BATCH_DAY))).intValue();
            VOMS_DATE_FORMAT = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_DATE_FORMAT));
            SELF_VOUCHER_DISTRIBUTION_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SELF_VOUCHER_DISTRIBUTION_ALLOWED))).booleanValue();
            PIN_SEND_TO = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_SEND_TO));
            DELIVERY_RECEIPT_TRACKED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DELIVERY_RECEIPT_TRACKED))).booleanValue();
            CREDIT_BACK_ALWD_EVD_AMB = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CREDIT_BACK_ALWD_EVD_AMB))).booleanValue();
            NOTIFICATION_SERVICECLASSWISE_REC_EVD = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_EVD))).booleanValue();
            // BUDDY_PIN_REQUIRED=
            // ((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.BUDDY_PIN_REQUIRED))).booleanValue();
            // Added by Dhiraj on 07/03/2007 for FOC SMS Notification
            FOC_SMS_NOTIFY = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.FOC_SMS_NOTIFY))).booleanValue();
            PROFILEASSOCIATE_AGENT_PREFERENCES = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES))).booleanValue();
            // P2P_CREDIT_RECHARGE_DEF_SELECTOR_CODE=((Integer)(PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_CRRECH_DEF_SELECTOR_CODE))).intValue();
            // Added for validating serial no and PIN with min and max values
            // instead of one fix value
            VOMS_SERIAL_NO_MIN_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue();
            VOMS_SERIAL_NO_MAX_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            VOMS_PIN_MIN_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MIN_LENGTH))).intValue();
            VOMS_PIN_MAX_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue();
            STAFF_USER_COUNT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.STAFF_USER_COUNT))).intValue();
            PORT_OUT_USER_SUSPEND_REQUIRED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PORT_USR_SUSPEND_REQ))).booleanValue();
            MNP_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue();
            DIRECT_VOUCHER_ENABLE = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DIRECT_VOUCHER_ENABLE))).booleanValue();
            MVD_MAX_VOUCHER = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MVD_MAX_VOUCHER))).intValue();

            // for Zebra and Tango added by sanjeew date 06/07/07
            PTUPS_MOBQUTY_MERGD = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue();
            // End of Zebra and Tango

            AUTO_PAYMENT_METHOD = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue();

            // for OCI Changes added by Ashish S date 16/07/07
            LOW_BAL_MSGGATEWAY = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOW_BAL_MSGGATEWAY));
            // end

            // for get number back service
            ALWD_NUMBCK_SERVICES = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ALWD_NUMBCK_SERVICES)));
            RC_ALWD_ACC_STATUS_NUMBCK = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RC_ALWD_ACC_STATUS_NUMBCK)));
            RC_NUMBCK_DIFF_REQ_TO_IN = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RC_NUMBCK_DIFF_REQ_TO_IN))).booleanValue();
            RC_NUMBCK_ALWD_DAYS_DIFF = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RC_NUMBCK_ALWD_DAYS_DIFF)));
            RC_NUMBCK_AMT_DEDCTED = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RC_NUMBCK_AMT_DEDCTED)));
            PRC_ALWD_ACC_STATUS_NUMBCK = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRC_ALWD_ACC_STATUS_NUMBCK)));
            PRC_NUMBCK_DIFF_REQ_TO_IN = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRC_NUMBCK_DIFF_REQ_TO_IN))).booleanValue();
            PRC_NUMBCK_ALWD_DAYS_DIFF = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRC_NUMBCK_ALWD_DAYS_DIFF)));
            PRC_NUMBCK_AMT_DEDCTED = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRC_NUMBCK_AMT_DEDCTED)));

            // Added for the weekly and monthly receivers thresholds for
            // P2P-AshishK [03/10/07].
            WE_REC_AMT_ALLWD_P2P = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.WE_REC_AMT_ALLWD_P2P))).longValue();
            WE_SUCTRAN_ALLWD_P2P = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.WE_SUCTRAN_ALLWD_P2P))).longValue();
            MO_REC_AMT_ALLWD_P2P = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MO_REC_AMT_ALLWD_P2P))).longValue();
            MO_SUCTRAN_ALLWD_P2P = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MO_SUCTRAN_ALLWD_P2P))).longValue();
            MAX_ALLD_BALANCE_P2P = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_ALLD_BALANCE_P2P))).longValue();
            // for C2S-AshishK [03/10/07].
            WE_SUCTRAN_ALLWDCOUN = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.WE_SUCTRAN_ALLWDCOUN))).longValue();
            WE_TOTXN_AMT_ALLWDCO = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.WE_TOTXN_AMT_ALLWDCO))).longValue();
            MO_SUCTRAN_ALLWDCOUN = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MO_SUCTRAN_ALLWDCOUN))).longValue();
            MO_TOTXN_AMT_ALLWDCO = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MO_TOTXN_AMT_ALLWDCO))).longValue();
            MAX_ALLD_BALANCE_C2S = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_ALLD_BALANCE_C2S))).longValue();
            // Bobba[16-11-07]-For Umniah to cater the condition if denomination
            // and payable amount is different.
            PAYAMT_MRP_SAME = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue();

            // To enquire Transaction Status with External Refrence Number -
            // Vipul[23/10/2007]
            C2S_REF_NUMBER_REQUIRED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_REF_NUMBER_REQUIRED))).booleanValue();
            C2S_REF_NUMBER_UNIQUE = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_REF_NUMBER_UNIQUE))).booleanValue();

            // added by santanu for pin and password management
            PREV_PASS_NOT_ALLOW = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PASS_NOT_ALLOW))).intValue();
            PREV_PIN_NOT_ALLOW = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue();

            // added for pin and password block duration
            C2S_PWD_BLK_EXP_DURATION = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_PWD_BLK_EXP_DURATION))).longValue();
            // Pin
            C2S_PIN_BLK_EXP_DURATION = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_PIN_BLK_EXP_DURATION))).longValue();
            P2P_PIN_BLK_EXP_DURATION = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_EXP_DURATION))).longValue();

            // added by PN for cell plus controller
            IS_FEE_APPLICABLE_FOR_VALIDATION_EXTENSION = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FEE_APPLICABLE_FOR_VALIDATION_EXTENSION))).booleanValue();
            FEE_AND_VALIDITY_DAYS_TO_EXT = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.FEE_AND_VALIDITY_DAYS_TO_EXT)));
            IS_CREDIT_TRFR_WITH_VALIDITY_UPDATION_P2P = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_CREDIT_TRFR_WITH_VALIDITY_UPDATION_P2P))).booleanValue();
            VAL_DAYS_TO_CHK_VALUPD = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VAL_DAYS_TO_CHK_VALUPD))).intValue();
            ;
            // Added for separate databse for report on 26/02/08.
            IS_SEPARATE_RPT_DB = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_RPT_DB))).booleanValue();

            ALLOW_SELF_EVR = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_SELF_EVR))).booleanValue();

            NEG_ADD_COMM_APPLY = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NEG_ADD_COMM_APPLY))).booleanValue();
            // added for displaying separate bonus in receiver message
            IS_SEPARATE_BONUS_REQUIRED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_BONUS_REQUIRED))).booleanValue();

            // Receiver Message suppresion
            REC_MSG_SEND_ALLOW = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REC_MSG_SEND_ALLOW))).booleanValue();
            OPT_USR_APRL_LEVEL = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_USR_APRL_LEVEL))).intValue();
            APPROVER_CAN_EDIT = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue();

            DISSABLE_BUTTON_LIST = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DISSABLE_BUTTON_LIST)));

            P2P_REG_EXPIRY_PERIOD = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_REG_EXPIRY_PERIOD))).longValue();
            MESSAGE_TO_PRIMARY_REQUIRED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED))).booleanValue();
            SECONDARY_NUMBER_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED))).booleanValue();

            // Added by Gopal on 10/11/2008 for batch C2C SMS Notification
            C2C_SMS_NOTIFY = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_SMS_NOTIFY))).booleanValue();
            // added by vikas kumar on 18/12/2008 as card group updation field
            SMS_MMS_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_MMS_ALLOWED))).booleanValue();
            CIRCLEMINLMT = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CIRCLEMINLMT))).longValue();
            // Add for PIN validation is requird in CP2P services 07/01/2009
            CP2P_PIN_VALIDATION_REQUIRED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED))).booleanValue();
            FIXLINE_RC_ALLOW_SELF_TOPUP = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.FIXLINE_RC_ALLOW_SELF_TOPUP))).booleanValue();
            BROADBAND_RC_ALLOW_SELF_TOPUP = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.BROADBAND_RC_ALLOW_SELF_TOPUP))).booleanValue();
            C2S_RANDOM_PIN_GENERATE = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_RANDOM_PIN_GENERATE))).booleanValue();
            WEB_RANDOM_PWD_GENERATE = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.WEB_RANDOM_PWD_GENERATE))).booleanValue();
            BATCH_USER_PASSWD_MODIFY_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PASSWD_MODIFY_ALLOWED))).booleanValue();
            RESET_PASSWORD_EXPIRED_TIME_IN_HOURS = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RESET_PASSWORD_EXPIRED_TIME_IN_HOURS))).intValue();
            RESET_PIN_EXPIRED_TIME_IN_HOURS = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RESET_PIN_EXPIRED_TIME_IN_HOURS))).intValue();
            P2P_CARD_GROUP_SLAB_COPY = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_CARD_GROUP_SLAB_COPY));
            C2S_CARD_GROUP_SLAB_COPY = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_CARD_GROUP_SLAB_COPY));
            STK_REG_ICCID = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.STK_REG_ICCID))).booleanValue();
            // Added for activation bonus.
            ACTIVATION_BONUS_MIN_REDEMPTION_AMOUNT = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ACTIVATION_BONUS_MIN_REDEMPTION_AMOUNT)));
            ACTIVATION_BONUS_REDEMPTION_DURATION = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ACTIVATION_BONUS_REDEMPTION_DURATION)));

            VOLUME_CALC_ALLOWED = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOLUME_CALC_ALLOWED));
            POSITIVE_COMM_APPLY = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.POSITIVE_COMM_APPLY))).booleanValue();
            AUTO_PWD_GENERATE_ALLOW = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PWD_GENERATE_ALLOW))).booleanValue();
            AUTO_PIN_GENERATE_ALLOW = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW))).booleanValue();
            PSWD_EXP_TIME_IN_HOUR_AFTER_CREATION = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PSWD_EXP_TIME_IN_HOUR_AFTER_CREATION))).intValue();
            CHECK_REC_AMBIGUOUS_TXN_AT_IAT = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHECK_REC_TXN_AT_IAT))).booleanValue();
            IS_IAT_RUNNING = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_IAT_RUNNING))).booleanValue();
            POINT_CONVERSION_FACTOR = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.POINT_CONVERSION_FACTOR))).intValue();

            // VIKRAM FOR VFE Last 3 transfer
            LAST_X_TRANSFER_STATUS = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_TRANSFER_STATUS))).intValue();
            SERVICE_FOR_LAST_X_TRANSFER = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_FOR_LAST_X_TRANSFER));
            C2S_RECHARGE_MULTIPLE_ENTRY = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_RECHARGE_MULTIPLE_ENTRY));

            // customer enquiry
            LAST_X_CUSTENQ_STATUS = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_CUSTENQ_STATUS))).intValue();
            // added for multiple wallet
            MULTIPLE_WALLET_APPLY = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY))).booleanValue();

            RVERSE_TRN_EXPIRY = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RVERSE_TRN_EXPIRY))).intValue();
            ALWD_REVTXN_SERVICES = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ALWD_REVTXN_SERVICES)));
            RVE_C2S_TRN_EXPIRY = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RVE_C2S_TRN_EXPIRY))).intValue();
            CHNL_PLAIN_SMS_SEPT_LOGINID = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPT_LOGINID));
            // For Direct Payout added by Lohit
            EXTERNAL_CODE_MANDATORY_FORDP = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORDP))).booleanValue();
            EXTERNAL_TXN_MANDATORY_FORDP = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_FORDP));
            DP_ORDER_APPROVAL_LVL = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DP_ORDER_APPROVAL_LVL))).intValue();
            EXTERNAL_TXN_MANDATORY_DOMAINTYPE_DP = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_DOMAINTYPE_DP));
            DP_SMS_NOTIFY = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DP_SMS_NOTIFY))).booleanValue();
            DP_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DP_ALLOWED))).booleanValue();
            LAST_TRF_MULTIPLE_SMS = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_TRF_MULTIPLE_SMS))).booleanValue();
            LAST_X_TRF_DAYS_NO = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_TRF_DAYS_NO))).intValue();
            // *************/

            SHA2_FAMILY_TYPE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SHA2_FAMILY_TYPE));
            HTTPS_ENABLE = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HTTPS_ENABLE))).booleanValue();
            PINPAS_EN_DE_CRYPTION_TYPE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE));
            NWADM_CROSS_ALLOW = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NWADM_CROSS_ALLOW))).booleanValue();
            STAFF_AS_USER = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.STAFF_AS_USER))).booleanValue();
            // Added by vikram
            // ZERO_BAL_THRESHOLD_VALUE=((Long)(PreferenceCache.getSystemPreferenceValue(PreferenceI.
            // ZERO_BAL_THRESHOLD_VALUE))).longValue();
            PRIVATE_RECHARGE_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue();

            NAMEEMBOSS_SEPT = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NAMEEMBOSS_SEPT));
            LOGIN_SPECIAL_CHAR_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_SPECIAL_CHAR_ALLOWED))).booleanValue();
            MVD_MIN_VOUCHER = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MVD_MIN_VOUCHER))).intValue();
            VOMS_PIN_ENCRIPTION_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_ENCRIPTION_ALLOWED))).booleanValue();
            CARD_GROUP_BONUS_RANGE = ((Integer) ((PreferenceCache.getSystemPreferenceValue(PreferenceI.CARD_GROUP_BONUS_RANGE))));
            // added by nilesh:for default grade
            IS_DEFAULT_PROFILE = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue();
            // added by nilesh: for user delete approval
            REQ_CUSER_DLT_APP = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REQ_CUSER_DLT_APP))).booleanValue();
            // added by nilesh: for O2C email notification
            O2C_EMAIL_NOTIFICATION = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_EMAIL_NOTIFICATION))).booleanValue();
            FOC_ODR_APPROVAL_LVL = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.FOC_ODR_APPROVAL_LVL))).intValue();
            // added by nilesh: for auto c2c transfer
            AUTO_C2C_TRANSFER_AMT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_C2C_TRANSFER_AMT))).intValue();
            SMS_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_ALLOWED))).booleanValue();

            // MVD voucher upload limit for extgw ashishT
            MVD_MAX_VOUCHER_EXTGW = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MVD_MAX_VOUCHER_EXTGW))).intValue();
            // added by priyanka 21/01/11 for mobilecom
            CRYSTAL_SUMMARY_REPORT_MAX_DATEDIFF = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_SUMMARY_REPORT_MAX_DATEDIFF))).intValue();
            // added by jasmine kaur for private recharge
            SID_ISNUMERIC = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ISNUMERIC))).booleanValue();

            MIN_SID_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SID_LENGTH_CODE))).intValue();
            MAX_SID_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SID_LENGTH_CODE))).intValue();
            // Added by Amit Raheja for NNP
            MSISDN_MIGRATION_LIST = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_MIGRATION_LIST_CODE));
            // added by Babu Kunwar 15/02/2011 for User_Event_Remarks
            USER_EVENT_REMARKS = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS))).booleanValue();
            // Added by Ankuj
            MIN_SID_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SID_LENGTH))).intValue();
            MAX_SID_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SID_LENGTH))).intValue();
            ;

            // Added for the SID
            PRIVATE_SID_SERVICE_ALLOW = (Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_SID_SERVICE_ALLOW));
            // Added for multiple credit recharge
            MULT_CRE_TRA_DED_ACC_SEP = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULT_CRE_TRA_DED_ACC_SEP));

            EXTERNAL_TXN_MANDATORY_FORO2C = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_FORO2C));
            EXTERNAL_CODE_MANDATORY_FORO2C = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORO2C))).booleanValue();

            // Added for DWH Changes by Anu garg
            RP2PDWH_OPT_SPECIFIC_PROC_NAME = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RP2PDWH_OPT_SPECIFIC_PROC_NAME));
            P2PDWH_OPT_SPECIFIC_PROC_NAME = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2PDWH_OPT_SPECIFIC_PROC_NAME));
            IATDWH_OPT_SPECIFIC_PROC_NAME = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IATDWH_OPT_SPECIFIC_PROC_NAME));
            // Added for the private recharge message short code
            PRIVATE_RECH_MESSGATEWAY = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECH_MESSGATEWAY));

            // added for roam recharge
            ALLOW_ROAM_ADDCOMM = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_RECHARGE);

            // Added by Give Me Balance response
            // INTERACTIVE_OPTION_ALLOWED=(String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.INTERACTIVE_OPTION_ALLOWED));
            // MENU_CODE=((Long)(PreferenceCache.getSystemPreferenceValue(PreferenceI.MENU_CODE))).longValue();
            PLAIN_RES_PARSE_REQUIRED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PLAIN_RES_PARSE_REQUIRED))).booleanValue();
            COUNTRY_CODE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.COUNTRY_CODE));
            USSD_NEW_TAGS_MANDATORY = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY)).booleanValue();

            O2C_SMS_NOTIFY = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_SMS_NOTIFY))).booleanValue();
            PARENT_CATEGORY_LIST = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PARENT_CATEGORY_LIST));
            OWNER_CATEGORY_LIST = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_CATEGORY_LIST));
            MAX_AUTO_FOC_ALLOW_LIMIT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_AUTO_FOC_ALLOW_LIMIT))).intValue();
            AUTO_FOC_TRANSFER_AMOUNT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_FOC_TRANSFER_AMOUNT))).intValue();
            ACTIVATION_FIRST_REC_APP = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ACTIVATION_FIRST_REC_APP))).booleanValue();

            // added by nilesh: MRP block time
            MRP_BLOCK_TIME_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MRP_BLOCK_TIME_ALLOWED))).booleanValue();
            ACTIVATION_FIRST_REC_APP = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ACTIVATION_FIRST_REC_APP))).booleanValue();
            // Added for SOS/LMB service

            SOS_SETTLE_DAYS = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_SETTLE_DAYS))).intValue();
            SOS_RECHARGE_AMOUNT = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_RECHARGE_AMOUNT))).longValue();
            SOS_MIN_VALIDITY_DAYS = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_MIN_VALIDITY_DAYS))).intValue();
            SOS_ALLOWED_MAX_BALANCE = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_ALLOWED_MAX_BALANCE))).longValue();
            // SOS_ALLOWED_MAX_BALANCE =
            // (String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_ALLOWED_MAX_BALANCE));
            SOS_ST_DEDUCT_UPFRONT = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_ST_DEDUCT_UPFRONT))).booleanValue();
            SOS_DAYS_GAP_BTWN_TWO_TRAN = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_DAYS_GAP_BTWN_TWO_TRAN))).intValue();
            SERV_CLASS_ALLOW_FOR_SOS = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERV_CLASS_ALLOW_FOR_SOS));
            // SOS_SERV_CLASS_BASED_OR_AON=((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_SERV_CLASS_BASED_OR_AON
            // ))).booleanValue();
            SOS_MINIMUM_AON = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_MINIMUM_AON))).intValue();
            SOS_ELIBILITY_ACCOUNT = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_ELIBILITY_ACCOUNT));
            SOS_ONLINE_ALLOW = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_ONLINE_ALLOW))).booleanValue();
            // post balance enquiry at IN or local calculation
            ENQ_POSTBAL_IN = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_IN))).booleanValue();
            // DFLT_PIN_FLAG_FOR_USR_CREATION_RESET=((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.DFLT_PIN_FLAG_FOR_USR_CREATION_RESET))).booleanValue();
            // DFLT_PWD_FLAG_FOR_USR_CREATION_RESET=((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.DFLT_PWD_FLAG_FOR_USR_CREATION_RESET))).booleanValue();
            ENQ_POSTBAL_ALLOW = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue();
            LMB_VALIDITY_DAYS_FORCESETTLE = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMB_VALIDITY_DAYS_FORCESETTLE))).intValue();
            LMB_BLK_UPL = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMB_BLK_UPL))).booleanValue();

            LMB_FORCE_SETL_STAT_ALLOW = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMB_FORCE_SETL_STAT_ALLOW))).booleanValue();
            VOMS_MAX_APPROVAL_LEVEL = ((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_APPROVAL_LEVEL)).intValue();
            VOMS_USER_KEY_REQD = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_USER_KEY_REQD))).booleanValue();
            // added by nilesh : consolidated for logger
            DB_ENTRY_NOT_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED))).booleanValue();
            RVERSE_TXN_APPRV_LVL = ((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.RVERSE_TXN_APPRV_LVL)).intValue();
            VOUCHER_TRACKING_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_TRACKING_ALLOWED))).booleanValue();
            VOUCHER_EN_ON_TRACKING = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_EN_ON_TRACKING))).booleanValue();
            PROCESS_FEE_REV_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PROCESS_FEE_REV_ALLOWED))).booleanValue();
            MCDL_MAX_LIST_COUNT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MCDL_MAX_LIST_COUNT))).intValue();
            MCDL_DIFFERENT_REQUEST_SEPERATOR = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MCDL_DIFFERENT_REQUEST_SEPERATOR));
            P2P_MCDL_DEFAULT_AMOUNT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_DEFAULT_AMOUNT))).intValue();
            P2P_MCDL_MAXADD_AMOUNT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_MAXADD_AMOUNT))).intValue();
            P2P_MCDL_AUTO_DELETION_DAYS = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_AUTO_DELETION_DAYS))).intValue();
            LMB_DEBIT_REQ = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMB_DEBIT_REQ))).booleanValue();
            PRVT_RC_MSISDN_PREFIX_LIST = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRVT_RC_MSISDN_PREFIX_LIST));// rahul
            IS_FNAME_LNAME_ALLOWED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED))).booleanValue();// deepika
            // RAVI FOR Last n recharge
            LAST_X_RECHARGE_STATUS = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_RECHARGE_STATUS))).intValue();
            SERVICE_FOR_LAST_X_RECHARGE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_FOR_LAST_X_RECHARGE));
            SMS_PIN_BYPASS_GATEWAY_TYPE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE));
            // CP User Registration Process
            CATEGORY_ALLOWED_FOR_CREATION = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CATEGORY_ALLOWED_FOR_CREATION));
            CP_SUSPENSION_DAYS_LIMIT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CP_SUSPENSION_DAYS_LIMIT))).intValue();
            // To make External Code Field Mandatory changes : added by harsh
            EXTERNAL_CODE_MANDATORY_FORUSER = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORUSER))).booleanValue();
            // MIGRATION_DATE=((Date)(PreferenceCache.getSystemPreferenceValue(PreferenceI.MIGRATION_DATE)));
            // VASTRIX ADDED BY HITESH
            SELECTOR_INTERFACE_MAPPING = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING))).booleanValue();
            PLAIN_RES_PARSE_REQUIRED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PLAIN_RES_PARSE_REQUIRED))).booleanValue();
            // To Encrypt the response message through External System :added by
            // harsh
            EXT_VOMS_MSG_DESEDECRYPT_KEY = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXT_VOMS_MSG_DES_ENDE_CRYPT_KEY));
            // for LOGIN_ID CHECKS
            LOGIN_ID_CHECK_ALLOWED = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_ID_CHECK_ALLOWED)).booleanValue();
            // Added for staff user approval
            STAFF_USER_APRL_LEVEL = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.STAFF_USER_APRL_LEVEL))).intValue();
            RSA_AUTHENTICATION_REQUIRED = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED))).booleanValue();
            // Added by Ankur for batch user creation by channel users
            BATCH_USER_PROFILE_ASSIGN = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN)).booleanValue();
            BATCH_INTIATE_NOTIF_TYPE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_INTIATE_NOTIF_TYPE));
            IS_TRF_RULE_USER_LEVEL_ALLOW = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue();
            // Email for pin & password
            IS_EMAIL_SERVICE_ALLOW = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue();
            COS_REQUIRED = (Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED));
            CELL_GROUP_REQUIRED = (Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED));
            SERVICE_PROVIDER_PROMO_ALLOW = (Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW));
            SRVC_PROD_MAPPING_ALLOWED = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED));
            SRVC_PROD_INTFC_MAPPING_ALLOWED = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_INTFC_MAPPING_ALLOWED));
            CELL_ID_SWITCH_ID_REQUIRED = (Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_ID_SWITCH_ID_REQUIRED));
            MULTI_AMOUNT_ENABLED = (Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTI_AMOUNT_ENABLED));
            IN_PROMO_REQUIRED = (Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED));
            DEBIT_SENDER_SIMACT = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEBIT_SENDER_SIMACT))).booleanValue();
            SIMACT_DEFAULT_SELECTOR = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SIMACT_DEFAULT_SELECTOR));
            LOGIN_PASSWORD_ALLOWED = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED)).booleanValue();
            AUTO_O2C_MAX_APPROVAL_LEVEL = ((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_O2C_MAX_APPROVAL_LEVEL)).intValue();
            AUTO_O2C_AMOUNT = ((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_O2C_AMOUNT)).intValue();
            // barred for deletion added by shashank
            REQ_CUSER_BAR_APPROVAL = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REQ_CUSER_BAR_APPROVAL))).intValue();
            SRVCS_FOR_PROD_MAPPING = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVCS_FOR_PROD_MAPPING));
            SHOW_CAPTCHA = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SHOW_CAPTCHA)).booleanValue();

            EMAIL_AUTH_REQ = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_AUTH_REQ)).booleanValue();
            MIN_HRDIF_ST_ED_LMS = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_HRDIF_ST_ED_LMS))).intValue();
            MIN_HRDIF_CR_ST_LMS = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_HRDIF_CR_ST_LMS))).intValue();
            LMS_APPL = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue();
            // added by harsh for validating Maximum Added Amount in Schedulder
            // Credit Transfer List
            P2P_SMCDL_ALLOWED_SCHEDULE_TYPE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_ALLOWED_SCHEDULE_TYPE));
            // P2P_SMCDL_DEFAULT_FREQUENCY=((Long)(PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_DEFAULT_FREQUENCY))).longValue();
            // For Separate External Database- 18/12/13
            // IS_SEPARATE_EXT_DB=((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_EXT_DB))).booleanValue();
            // LMS Hierarchy-Wise Promotion- 27/12/13
            // PROMOTION_HIERARCHY_WISE=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.PROMOTION_HIERARCHY_WISE)).booleanValue();
            LMS_VOL_COUNT_ALLOWED = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_VOL_COUNT_ALLOWED)).booleanValue();
            LMS_PROF_APR_ALLOWED = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_PROF_APR_ALLOWED)).booleanValue();

            AUTO_O2C_APPROVAL_ALLOWED = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_O2C_APPROVAL_ALLOWED)).booleanValue();
            AUTH_TYPE_REQ = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTH_TYPE_REQ)).booleanValue();
            SAP_INTEGARATION_FOR_USRINFO = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SAP_INTEGARATION_FOR_USRINFO)).booleanValue();

            MIN_ACCOUNT_ID_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_ACCOUNT_ID_LENGTH_CODE))).intValue();
            MAX_ACCOUNT_ID_LENGTH = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_ACCOUNT_ID_LENGTH_CODE))).intValue();
            C2S_TRNSFR_AMTBLCK_SRVCTYP = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRNSFR_AMTBLCK_SRVCTYP));
            OTP_TIMEOUT_INSEC = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_TIMEOUT_INSEC))).intValue();
            C2S_TRNSFR_INVNO_SRVCTYP = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRNSFR_INVNO_SRVCTYP));
            O2C_PNDNG_FOR_APPR_SEND_MAIL_DAYS_CNT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_PNDNG_FOR_APPR_SEND_MAIL_DAYS_CNT))).intValue();
            // /Added by Vikas
            MAX_AUTOTOPUP_AMT = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_AUTOTOPUP_AMT))).longValue();
            // end here
            C2S_REVERSAL_TXNID_SRVCTYP = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_REVERSAL_TXNID_SRVCTYP)));
            LMS_STOCK_REQUIRED = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_STOCK_REQUIRED)).booleanValue();
            LMS_MULT_FACTOR = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_MULT_FACTOR)));
            // For Volume Process- To credit loyalty points instead of Air-time
            LMS_VOL_CREDIT_LOYAL_PTS = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_VOL_CREDIT_LOYAL_PTS)).booleanValue();
            /*
             * //Added By Diwakar
             * MAX_LAST_TRANSFERS_DAYS =
             * ((Integer)(PreferenceCache.getSystemPreferenceValue
             * (PreferenceI.MAX_LAST_TRANSFERS_DAYS))).intValue();
             * EXTSYS_USR_APRL_LEVEL_REQUIRED
             * =((Integer)(PreferenceCache.getSystemPreferenceValue
             * (PreferenceI.EXTSYS_USR_APRL_LEVEL_REQUIRED))).intValue();
             * //Ended Here
             */
            // Captcha Authentication at Login Page Start- by akanksha.
            INVALID_PWD_COUNT_FOR_CAPTCHA = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.INVALID_PWD_COUNT_FOR_CAPTCHA));
            CAPTCHA_LENGTH = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CAPTCHA_LENGTH));
            // Captcha Authentication at Login Page End- by akanksha.

            /*
             * PAYMENTDETAILSMANDATE_O2C=((Integer)PreferenceCache.
             * getSystemPreferenceValue
             * (PreferenceI.PAYMENTDETAILSMANDATE_O2C)).intValue();
             * 
             * USER_CREATION_MANDATORY_FIELDS=(String)(PreferenceCache.
             * getSystemPreferenceValue
             * (PreferenceI.USER_CREATION_MANDATORY_FIELDS ));
             * USER_APPROVAL_LEVEL=((Integer)(PreferenceCache.
             * getSystemPreferenceValue
             * (PreferenceI.USER_APPROVAL_LEVEL))).intValue();
             */
            SELFTOPUPDWH_OPT_SPECIFIC_PROC_NAME = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SELFTOPUPDWH_OPT_SPECIFIC_PROC_NAME));
            AUTOSTU_NO_DAYS_ALERT = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTOSTU_NO_DAYS_ALERT))).intValue();
            SELFTOPUP_TOKENIZATION = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELFTOPUP_TOKENIZATION)).booleanValue();

        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SystemPreferences[load]", "", "", "", "Exception:" + e.getMessage());
            e.printStackTrace();
        }

    }

    public static void reload() {
        load();
    }

    public SystemPreferences() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
