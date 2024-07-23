package com.btsl.pretups.preference.businesslogic;

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

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class SystemPreferences implements Runnable{

    public void run() {
        try {
            Thread.sleep(50);
            load();
        } catch (Exception e) {
        	 _log.error("SystemPreferences init() Exception ", e);
        }
    }
	private static Log _log = LogFactory.getLog(SystemPreferences.class
			.getName());
	public static int AMOUNT_MULT_FACTOR = 0;
	public static int MIN_MSISDN_LENGTH = 0;
	public static int MAX_MSISDN_LENGTH = 0;
	public static int MSISDN_LENGTH = 10;
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
	public static String DEFAULT_LANGUAGE = null; 
	public static String DEFAULT_COUNTRY = null; 
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
	public static int MAPP_SESSION_EXPIRY_SEC=0;
	 
	 
	public static String P2P_PLAIN_SMS_SEPARATOR = " ";
	public static String GROUP_ROLE_ALLOWED;
	public static String DEF_FRCXML_SEL_P2P;
	public static String DEF_FRCXML_SEL_C2S;
	public static boolean EXTERNAL_TXN_UNIQUE;
	public static int FINANCIAL_YEAR_START_INDEX = 0;
	 
	public static int C2S_TRANSFER_DEF_SELECTOR_SCH_CODE = 2;
	public static String C2S_TRANSFER_DEF_SELECTOR_SCH;
	public static boolean EXTERNAL_TXN_NUMERIC;
	public static String EXTERNAL_TXN_MANDATORY_DOMAINTYPE = null;
	public static String EXTERNAL_TXN_MANDATORY_FORFOC = null;
	 
	public static boolean NOTIFICATION_SERVICECLASSWISE_SEN;
	public static boolean NOTIFICATION_SERVICECLASSWISE_REC;
	public static boolean NOTIFICATION_SERVICECLASSWISE_REC_C2S;
	public static boolean CHK_BLK_LST_STAT;
	public static boolean SAP_ALLOWED;
	 
	public static String C2S_BILLPAYMENT_DEF_SELECTOR; 
	public static boolean NOTIFICATION_SERVICECLASSWISE_REC_BILLPAY;  
																		 
	public static boolean C2S_ALLOW_SELF_BILLPAY; 
	public static boolean C2S_SNDR_CREDIT_BK_AMB_STATUS_BILLPAY; 
	 
	public static String DEF_FRCXML_SEL_BILLPAY; 
	public static boolean SEP_OUTSIDE_TXN_CTRL;
	public static boolean POSTPAID_REGISTER_AS_ACTIVATED_STATUS;  
																	 
																	 
																	 
																	 
																	 
																	 
	public static boolean SPACE_ALLOW_IN_LOGIN; 
	public static String DEFAULT_WEB_GATEWAY_CODE = null;
	public static String SECOND_LANGUAGE_ENCODING = null;  
															 
															 
	public static String GRPT_CTRL_ALLOWED;
	public static String GRPT_CHRG_ALLOWED;
	public static String GRPT_CONTROL_LEVEL;
	 
	public static boolean COMMON_TRANSFER_ID_APPLICABLE = false;
	public static boolean CGTAX34APP = false;
	 
	 
	 
	public static boolean P2P_ALLOW_SELF_TOPUP;
	 
	public static boolean EXTERNAL_CODE_MANDATORY_FORFOC;
	public static String SECOND_LANGUAGE_CHARSET = null;  
															 
	 
	public static boolean C2C_RET_PARENT_ONLY = false;
	public static String DECIMAL_ALLOWED_IN_SERVICES;
	 
	public static String EXTERNAL_DATE_FORMAT;
	public static int XML_MAX_RCD_SUM_RESP;
	public static int XML_DATE_RANGE;
	public static int XML_DFT_DATE_RANGE;
	 
	public static boolean C2S_ALLOW_SELF_UB;
	 
	public static boolean C2S_SNDR_CRDT_BK_AMB_UB;
	public static boolean NOTIFICATION_SERVICECLASSWISE_REC_UB;
	public static int MIN_IDENTIFICATION_NUMBER_LENGTH = 0;
	public static int MAX_IDENTIFICATION_NUMBER_LENGTH = 0;
	public static boolean USE_HOME_STOCK = false;
	public static String OTHERID_PREFIX_LIST;
	public static String IDENTIFICATION_NUMBER_VAL_TYPE;
	public static boolean ALPHA_ID_NUM_ALLOWED = false;
	public static boolean RESPONSE_IN_DISPLAY_AMT = false;
	 
	public static long MAX_DENOMINATION_VAL = 0;
	public static String VOMS_UPEXPHOURS = null;
	public static String VOMS_OFFPEAKHRS = null;
	public static long VOMS_MAX_TOTAL_VOUCHER_OT;
	public static long VOMS_MAX_TOTAL_VOUCHER_EN;
	public static long VOMS_MAX_ERROR_COUNTEN = 0;
	public static long VOMS_MAX_ERROR_COUNTOTH = 0;
	public static int VOMS_MAX_BATCH_DAY;
	public static String VOMS_DATE_FORMAT;
	 
	public static boolean SELF_VOUCHER_DISTRIBUTION_ALLOWED;
	public static String PIN_SEND_TO;
	public static boolean DELIVERY_RECEIPT_TRACKED;
	public static boolean CREDIT_BACK_ALWD_EVD_AMB;
	public static boolean NOTIFICATION_SERVICECLASSWISE_REC_EVD;
	 
	 
	public static boolean FOC_SMS_NOTIFY = false;
	 
	public static boolean PROFILEASSOCIATE_AGENT_PREFERENCES = false;
	public static int VOMS_SERIAL_NO_MIN_LENGTH;
	public static int VOMS_SERIAL_NO_MAX_LENGTH;
	public static int VOMS_PIN_MIN_LENGTH;
	public static int VOMS_PIN_MAX_LENGTH;
	public static int MAX_REQ_VOUCHER_QTY;
	public static int STAFF_USER_COUNT;
	public static boolean PORT_OUT_USER_SUSPEND_REQUIRED = false;
	public static boolean MNP_ALLOWED = false;
	public static boolean DIRECT_VOUCHER_ENABLE = false;
	public static boolean CHANNEL_USER_MNP_ALLOW = false;
	public static boolean AUTO_VOUCHER_CRTN_ALWD = false;
	public static boolean VOMS_NATIONAL_LOCAL_PREFIX_ENABLE = false;
	 
	public static int MVD_MAX_VOUCHER;
	 
	public static boolean PTUPS_MOBQUTY_MERGD = false;
	public static boolean AUTO_PAYMENT_METHOD = true;
	 
	public static String LOW_BAL_MSGGATEWAY;
	 
	public static String ALWD_NUMBCK_SERVICES;
	public static String RC_ALWD_ACC_STATUS_NUMBCK;
	public static boolean RC_NUMBCK_DIFF_REQ_TO_IN = false;
	public static String RC_NUMBCK_ALWD_DAYS_DIFF;
	public static String RC_NUMBCK_AMT_DEDCTED;
	public static String PRC_ALWD_ACC_STATUS_NUMBCK;
	public static boolean PRC_NUMBCK_DIFF_REQ_TO_IN = false;
	public static String PRC_NUMBCK_ALWD_DAYS_DIFF;
	public static String PRC_NUMBCK_AMT_DEDCTED;
	 
	public static long WE_REC_AMT_ALLWD_P2P;
	public static long WE_SUCTRAN_ALLWD_P2P;
	public static long MO_REC_AMT_ALLWD_P2P;
	public static long MO_SUCTRAN_ALLWD_P2P;
	public static long MAX_ALLD_BALANCE_P2P;
	 
	public static long WE_SUCTRAN_ALLWDCOUN;
	public static long WE_TOTXN_AMT_ALLWDCO;
	public static long MO_SUCTRAN_ALLWDCOUN;
	public static long MO_TOTXN_AMT_ALLWDCO;
	public static long MAX_ALLD_BALANCE_C2S;
	public static String VOMS_MIN_ALT_VALUE;
	public static String VOMS_ORDER_SLAB_LENGTH;
	 
	 
	 
	public static boolean PAYAMT_MRP_SAME = false;
	 
	 
	public static boolean C2S_REF_NUMBER_REQUIRED;
	public static boolean C2S_REF_NUMBER_UNIQUE;
	 
	public static int PREV_PASS_NOT_ALLOW;
	public static int PREV_PIN_NOT_ALLOW;
	 
	public static boolean IS_FEE_APPLICABLE_FOR_VALIDATION_EXTENSION = true;
	public static String FEE_AND_VALIDITY_DAYS_TO_EXT = "0,0";
	public static boolean IS_CREDIT_TRFR_WITH_VALIDITY_UPDATION_P2P = true;
	public static int VAL_DAYS_TO_CHK_VALUPD = 30;
	 
	public static long C2S_PWD_BLK_EXP_DURATION;
	public static long C2S_PIN_BLK_EXP_DURATION;
	public static long P2P_PIN_BLK_EXP_DURATION;
	 
	public static boolean IS_SEPARATE_RPT_DB;
	public static boolean ALLOW_SELF_EVR;
	 
	public static boolean NEG_ADD_COMM_APPLY;
	public static boolean IS_SEPARATE_BONUS_REQUIRED;
	 
	public static boolean REC_MSG_SEND_ALLOW = true;
	 
	public static int OPT_USR_APRL_LEVEL = 0;
	 
	public static boolean APPROVER_CAN_EDIT = false;
	 
	public static String DISSABLE_BUTTON_LIST = null;
	public static long P2P_REG_EXPIRY_PERIOD;
	 
	 
	public static boolean MESSAGE_TO_PRIMARY_REQUIRED = false;
	 
	 
	public static boolean SECONDARY_NUMBER_ALLOWED = false;
	 
	public static boolean C2C_SMS_NOTIFY = false;
	 
	public static boolean SMS_MMS_ALLOWED;
	public static long CIRCLEMINLMT = 0;
	 
	public static boolean CP2P_PIN_VALIDATION_REQUIRED = true;
	public static boolean FIXLINE_RC_ALLOW_SELF_TOPUP;
	public static boolean BROADBAND_RC_ALLOW_SELF_TOPUP;
	public static boolean C2S_RANDOM_PIN_GENERATE;
	public static boolean WEB_RANDOM_PWD_GENERATE;
	 
	public static boolean BATCH_USER_PASSWD_MODIFY_ALLOWED;
	public static int RESET_PASSWORD_EXPIRED_TIME_IN_HOURS = 0;
	public static int RESET_PIN_EXPIRED_TIME_IN_HOURS = 0;
	public static String C2S_CARD_GROUP_SLAB_COPY;
	public static String P2P_CARD_GROUP_SLAB_COPY;
	public static boolean STK_REG_ICCID;  
											 
	 
	public static String ACTIVATION_BONUS_MIN_REDEMPTION_AMOUNT;
	public static String ACTIVATION_BONUS_REDEMPTION_DURATION;
	public static String VOLUME_CALC_ALLOWED;
	public static boolean POSITIVE_COMM_APPLY;
	 
	public static boolean AUTO_PWD_GENERATE_ALLOW;
	public static boolean AUTO_PIN_GENERATE_ALLOW;
	public static int PSWD_EXP_TIME_IN_HOUR_AFTER_CREATION = 0;
	 
	public static boolean CHECK_REC_AMBIGUOUS_TXN_AT_IAT = false;
	 
	public static boolean IS_IAT_RUNNING = false;
	public static int POINT_CONVERSION_FACTOR = 0;
	 
	public static String C2S_RECHARGE_MULTIPLE_ENTRY = null;
	 
	public static int LAST_X_TRANSFER_STATUS;
	public static String SERVICE_FOR_LAST_X_TRANSFER;
	 
	public static int LAST_X_CUSTENQ_STATUS;
	 
	public static boolean MULTIPLE_WALLET_APPLY = false;
	 
	public static int RVERSE_TRN_EXPIRY;
	public static int RVE_C2S_TRN_EXPIRY;
	 
	public static String ALWD_REVTXN_SERVICES;
	public static String CHNL_PLAIN_SMS_SEPT_LOGINID = null;
	 
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
	 
	 
	public static boolean NWADM_CROSS_ALLOW = false;
	public static boolean STAFF_AS_USER = false;
	 
	 
	public static boolean PRIVATE_RECHARGE_ALLOWED = false;
	public static String NAMEEMBOSS_SEPT = null;
	public static boolean LOGIN_SPECIAL_CHAR_ALLOWED = false;
	public static int MVD_MIN_VOUCHER;
	public static boolean VOMS_PIN_ENCRIPTION_ALLOWED = false;
	public static int CARD_GROUP_BONUS_RANGE = 0;
	 
	public static boolean IS_DEFAULT_PROFILE = false;
	 
	public static boolean REQ_CUSER_DLT_APP = true;
	 
	public static boolean O2C_EMAIL_NOTIFICATION = false;
	public static int FOC_ODR_APPROVAL_LVL = 0;
	 
	public static int AUTO_C2C_TRANSFER_AMT = 0;
	public static boolean SMS_ALLOWED = false;
	 
	public static int MVD_MAX_VOUCHER_EXTGW;
	 
	public static int CRYSTAL_SUMMARY_REPORT_MAX_DATEDIFF;
	 
	public static boolean SID_ISNUMERIC = true;
	 
	public static String MSISDN_MIGRATION_LIST;
	public static boolean USER_EVENT_REMARKS = false;
	 
	public static int MIN_SID_LENGTH = 0;
	public static int MAX_SID_LENGTH = 15;
	public static boolean PRIVATE_SID_SERVICE_ALLOW = false;
	public static String MULT_CRE_TRA_DED_ACC_SEP = null;
	public static String EXTERNAL_TXN_MANDATORY_FORO2C = null;
	 
	public static boolean EXTERNAL_CODE_MANDATORY_FORO2C;
	 
	public static String RP2PDWH_OPT_SPECIFIC_PROC_NAME = null;
	public static String P2PDWH_OPT_SPECIFIC_PROC_NAME = null;
	public static String IATDWH_OPT_SPECIFIC_PROC_NAME = null;
	 
	public static String PRIVATE_RECH_MESSGATEWAY;
	 
	public static boolean ALLOW_ROAM_ADDCOMM = false;
	 
	 
	 
	public static boolean PLAIN_RES_PARSE_REQUIRED = false;
	public static String COUNTRY_CODE;
	public static boolean USSD_NEW_TAGS_MANDATORY;
	 
	public static boolean O2C_SMS_NOTIFY = false;
	public static String OWNER_CATEGORY_LIST = null;
	public static String PARENT_CATEGORY_LIST = null;
	public static int AUTO_FOC_TRANSFER_AMOUNT = 0;
	public static int MAX_AUTO_FOC_ALLOW_LIMIT = 20;
	 
	public static boolean MRP_BLOCK_TIME_ALLOWED = false;
	 
	public static boolean ACTIVATION_FIRST_REC_APP = false;
	public static int SOS_SETTLE_DAYS;
	 
	public static long SOS_RECHARGE_AMOUNT;
	public static int SOS_MIN_VALIDITY_DAYS;
	public static long SOS_ALLOWED_MAX_BALANCE;
	 
	public static boolean SOS_ST_DEDUCT_UPFRONT = false;
	public static int SOS_DAYS_GAP_BTWN_TWO_TRAN;
	public static String SERV_CLASS_ALLOW_FOR_SOS;
	 
	public static int SOS_MINIMUM_AON = 0;
	public static String SOS_ELIBILITY_ACCOUNT;
	public static boolean SOS_ONLINE_ALLOW = false;
	public static boolean ENQ_POSTBAL_IN = false;
	public static boolean DFLT_PIN_FLAG_FOR_USR_CREATION_RESET;
	public static boolean DFLT_PWD_FLAG_FOR_USR_CREATION_RESET;
	public static boolean ENQ_POSTBAL_ALLOW = false;
	public static int LMB_VALIDITY_DAYS_FORCESETTLE = 120;
	public static boolean LMB_BLK_UPL = false; 
												 
	public static boolean LMB_FORCE_SETL_STAT_ALLOW = false;
	public static int VOMS_MAX_APPROVAL_LEVEL = 2;
	public static boolean VOMS_USER_KEY_REQD = false;
	public static boolean DB_ENTRY_NOT_ALLOWED = false;  
														 
														 
	 
	public static int RVERSE_TXN_APPRV_LVL = 0;
	 
	public static boolean VOUCHER_TRACKING_ALLOWED = false;
	public static boolean VOUCHER_EN_ON_TRACKING = false;
	public static boolean PROCESS_FEE_REV_ALLOWED = false;
	 
	public static int MCDL_MAX_LIST_COUNT;
	public static String MCDL_DIFFERENT_REQUEST_SEPERATOR;
	public static int P2P_MCDL_DEFAULT_AMOUNT = 0;
	public static int P2P_MCDL_MAXADD_AMOUNT = 0;
	public static int P2P_MCDL_AUTO_DELETION_DAYS = 90;
	public static boolean LMB_DEBIT_REQ = true;
	public static String PRVT_RC_MSISDN_PREFIX_LIST = null; 
															 
	public static boolean IS_FNAME_LNAME_ALLOWED = false; 
															 
															 
															 
	 
	public static int LAST_X_RECHARGE_STATUS;
	public static String SERVICE_FOR_LAST_X_RECHARGE = "C2S";
	public static String SMS_PIN_BYPASS_GATEWAY_TYPE;
	 
	public static String CATEGORY_ALLOWED_FOR_CREATION = null;
	public static int CP_SUSPENSION_DAYS_LIMIT = 0;
	 
	public static boolean EXTERNAL_CODE_MANDATORY_FORUSER;
	 
	 
	public static boolean SELECTOR_INTERFACE_MAPPING = false; 
																 
	public static boolean SERVICE_PROVIDER_PROMO_ALLOW = false;
	 
	public static String EXT_VOMS_MSG_DESEDECRYPT_KEY = null;
	public static boolean LOGIN_ID_CHECK_ALLOWED; 
	 
	public static int STAFF_USER_APRL_LEVEL;
	 
	 
	public static boolean LOGIN_PASSWORD_ALLOWED;
	 
	public static boolean RSA_AUTHENTICATION_REQUIRED = false;
	 
	public static boolean BATCH_USER_PROFILE_ASSIGN;
	public static String BATCH_INTIATE_NOTIF_TYPE = "SMS";
	 
	public static boolean IS_TRF_RULE_USER_LEVEL_ALLOW;
	 
	public static boolean IS_EMAIL_SERVICE_ALLOW;
	 
	public static boolean COS_REQUIRED = false;
	 
	public static boolean CELL_GROUP_REQUIRED;
	public static String SRVC_PROD_MAPPING_ALLOWED = null;
	public static String SRVC_PROD_INTFC_MAPPING_ALLOWED = null;
	public static String SRVCS_FOR_PROD_MAPPING = null;
	public static boolean CELL_ID_SWITCH_ID_REQUIRED = false;
	public static boolean MULTI_AMOUNT_ENABLED;
	public static boolean IN_PROMO_REQUIRED = false;
	 
	public static boolean DEBIT_SENDER_SIMACT = false;
	public static String SIMACT_DEFAULT_SELECTOR = null;
	public static int AUTO_O2C_MAX_APPROVAL_LEVEL = 0;
	public static int AUTO_O2C_AMOUNT = 0;
	 
	public static int REQ_CUSER_BAR_APPROVAL;
	 
	public static boolean SHOW_CAPTCHA = false;
	 
	public static boolean EMAIL_AUTH_REQ = false;
	public static int MIN_HRDIF_ST_ED_LMS;
	public static int MIN_HRDIF_CR_ST_LMS;
	public static boolean LMS_APPL = false;
	 
	 
	public static long P2P_SMCDL_DEFAULT_FREQUENCY = 3;
	public static String P2P_SMCDL_ALLOWED_SCHEDULE_TYPE;
	 
	public static boolean IS_SEPARATE_EXT_DB = false;
	 
	public static boolean PROMOTION_HIERARCHY_WISE = false;
	public static boolean LMS_VOL_COUNT_ALLOWED = false;
	public static boolean LMS_PROF_APR_ALLOWED = false;
	public static boolean AUTO_O2C_APPROVAL_ALLOWED = false;
	 
	public static boolean AUTH_TYPE_REQ = false;
	 
	public static boolean TWO_FA_REQ = false;
	 
	public static boolean SAP_INTEGARATION_FOR_USRINFO = false;
	public static int MIN_ACCOUNT_ID_LENGTH = 0;
	public static int MAX_ACCOUNT_ID_LENGTH = 0;
	 
	public static String C2S_TRNSFR_AMTBLCK_SRVCTYP;
	public static int OTP_TIMEOUT_INSEC = 0;
	public static String C2S_TRNSFR_INVNO_SRVCTYP;
	public static int O2C_PNDNG_FOR_APPR_SEND_MAIL_DAYS_CNT = 0;
	public static String C2S_REVERSAL_TXNID_SRVCTYP;
	public static boolean LMS_STOCK_REQUIRED = false;
	public static String LMS_MULT_FACTOR = null;
	public static boolean LMS_VOL_CREDIT_LOYAL_PTS = false;
	 
	 
	public static int EXTSYS_USR_APRL_LEVEL_REQUIRED = 0;
	 
	public static int MAX_LAST_TRANSFERS_DAYS;
	 
	public static int INVALID_PWD_COUNT_FOR_CAPTCHA;
	public static int CAPTCHA_LENGTH;
	 
	 
	public static long MAX_AUTOTOPUP_AMT;
	 
	public static int PAYMENTDETAILSMANDATE_O2C = 0;
	public static int PAYMENTDETAILSMANDATE_C2C = 0;
	public static int USER_APPROVAL_LEVEL = 0;
	public static String USER_CREATION_MANDATORY_FIELDS;
	public static String O2C_APPRV_QTY_LEVEL;
	public static boolean IS_REQ_MSISDN_FOR_STAFF = false;
	public static long THLD_PRTP_PRCSS_TIME;
	public static String STAFF_USER_AUTH_TYPE;
	public static String CHANNEL_USER_ROLE_TYPE_DISPLAY;
	public static int PERCENTAGE_OF_PRE_REVERSAL;
	 
	public static boolean ALLOW_ROAM_RECHARGE;
	public static String ROAM_INTERFACE_ID;
	public static boolean ALLOW_CCARD_ROAM_RECHARGE;
	public static String VOUCHER_SERVICES_LIST;
	public static boolean MULTIPLE_VOUCHER_TABLE;
	 
	public static boolean LMS_PCT_POINTS_CALCULATION;
	public static boolean USER_PRODUCT_MULTIPLE_WALLET = false;
	public static boolean IS_PARTIAL_BATCH_ALLOWED = false;
	public static int MIN_VOUCHER_CODE_LENGTH = 0;
	public static String DEFAULT_WALLET;
	public static String WALLET_FOR_ADNL_CMSN;
	public static boolean IS_MSISDN_ASSOCIATION_REQ = false;
	public static String ADMINISTRBLY_USER_STATUS_CHANG;
	public static boolean REALTIME_AUTO_C2C_ALLOWED = false;
	public static String TXN_SENDER_USER_STATUS_CHANG;
	public static String TXN_RECEIVER_USER_STATUS_CHANG;

	 
	public static String LIFECYCLE_STATUS_DAYS_LIST = null;
	 
	public static boolean OPT_IN_OUT_ALLOW = false;
	public static String EXTERNAL_TXN_MANDATORY_FOR_LPT = null;
	public static boolean EXTERNAL_CODE_MANDATORY_FOR_LPT = false;
	public static int M_PRE_PERCENTAGE = 0;
	public static int M_SLAVE_PERCENTAGE = 0;

	 
	public static String INTRFC_MAX_NODES;
	public static boolean ALERT_ALLOWED = false;
	public static boolean BLOCKING_ALLOWED = false;
	public static boolean RETURN_TO_OPERATOR_STOCK;

	 
	public static long ROAM_RECHARGE_DAILY_THRESHOLD;
	public static int ROAM_RECHARGE_PENALTY_PERCENTAGE;
	public static int ROAM_PENALTY_OWNER_PERCENTAGE;

	public static boolean INTF_NODE_VALIDATION = false;
	 
	public static int DP_NETWORKLEVEL_DAILYLIMIT = 0;
	public static int DP_SYSTEMLEVEL_LIMIT = 0;
	public static int DP_ONLINE_LIMIT = 0;
	public static String DECENTER_ROAM_LOCATION;
	 
	public static int LB_SYSTEMLEVEL_LIMIT = 0;
	 
	public static boolean CHOICE_RECHARGE_APPLICABLE = false;

	 
	public static String TIME_FOR_REVERSAL;
	public static String TIME_FOR_REVERSAL_CCE;
	public static String ALLOWED_DAYS_FOR_REVERSAL;
	public static String ALLOWED_SERVICES_FOR_REVERSAL;
	public static boolean ALLOW_BULK_C2S_REVERSAL_MESSAGE;
	public static String ALLOWED_GATEWAY_FOR_BULK_REVERSAL;

	public static boolean O2C_BATCH_WITHDRAW_MESSAGE_ALLOWED = false;
	public static boolean ADMIN_MESSAGE_REQD;
	 
	public static String IRIS_DATE_FORMAT;
	public static String SUBSRBR_MSG_STOP = null;
	 
	public static boolean OWNER_COMMISION_ALLOWED = false;
	 
	public static boolean ADD_COMM_SEPARATE_MSG;
	public static String C2S_REQUEST_COMMON_MESSAGE = "";
	 
	public static boolean INFO_FIELD_ALLOW;  
	public static boolean DUPLICATE_CARDGROUP_CODE_ALLOW = false;
	public static String DEFAULT_CURRENCY;
	public static int OTP_ALLOWED_LENGTH;
	public static boolean FNF_ZB_ALLOWED = false;
	public static boolean LOW_BASED_ALLOWED = false;

	public static boolean P2P_SERVICES_TYPE_SERVICECLASS = false;

	public static boolean USR_MOV_ACROSS_DOM_ALLOW;  
													 
	public static boolean ORIGIN_ID_ALLOW;  
	 
	public static boolean SID_ENCRYPTION_ALLOWED;
	public static boolean USR_BTCH_SUS_DEL_APRVL=false ;
	public static boolean AUTO_NWSTK_CRTN_ALWD=false;
	public static String AUTO_NWSTK_CRTN_THRESHOLD;
	public static long TOKEN_EXPIRY_IN_MINTS;
	
	
	 
	public static boolean O2C_DIRECT_TRANSFER=false;
	public static int SEQUENCE_ID_RANGE=0;
	public static boolean SEQUENCE_ID_ENABLE=false;
	public static boolean HASHING_ENABLE = false;
	public static int HASHING_ID_RANGE=0;
	public static boolean CHANNEL_SOS_ENABLE =false;
	public static boolean AUTO_C2C_SOS_CAT_ALLOWED =false;
	public static String SOS_SETTLEMENT_TYPE =null;
	public static String CHANNEL_SOS_ALLOWED_WALLET =null;
	public static boolean CHANNEL_AUTOC2C_ENABLE = false;
	public static boolean DECRYPT_KEY_VISIBLE = false;
	public static boolean THIRD_PARTY_VISIBLE = false;
	 
	public static boolean CHANNEL_TRANSFERS_INFO_REQUIRED=false;
	public static boolean DOWNLOAD_CSV_REPORT_REQUIRED=false;
	public static String ALLOW_TRANSACTION_IF_SOS_SETTLEMENT_FAIL;
	public static boolean VOMS_PROFILE_ACTIVATION_REQ = false;
	public static boolean TARGET_BASED_COMMISSION=false;
	public static boolean TARGET_BASED_BASE_COMMISSION=false;
	public static boolean LR_ENABLED = false;
	public static int TARGET_BASED_COMMISSION_SLABS = 5;
	public static int TARGET_BASED_BASE_COMMISSION_SLABS = 5;
	public static boolean OFFLINE_SETTLE_EXTUSR_BAL=true;

	public static int SUBS_BLK_AFT_X_CONS_FAIL;
	public static int SUBS_UNBLK_AFT_X_TIME;

	public static String ALLOWD_USR_TYP_CREATION=null;
	public static String OAUTH_TOKEN_TIME_TO_LIVE;
	public static String SYSTEM_ROLE_ALLOWED;
	
	 
	public static boolean ALIAS_TO_BE_ENCRYPTED;
	
	 
	public static int CHNL_USR_LAST_ACTIVE_TXN;
	
	 
	public static boolean REALTIME_OTF_MSGS=false;
	public static int POSTPAID_SUBS_SERVICECLASS=1000;
	

	public static int LAST_X_TRF_DETAILS_NO=3;
	public static int LAST_X_TRF_DETAILS_DAYS=10;
	
	
	public static String STK_MASTER_KEY;
	public static boolean PIN_VALIDATATION_IN_USSD=true;
	public static String DUALWALLET_C2CAUTO_CAL;
	
	public static String BURN_RATE_THRESHOLD;
	public static boolean VOUCHER_BURN_RATE_SMS_ALERT=false;
	public static boolean VOUCHER_BURN_RATE_EMAIL_ALERT=false;
	
	public static boolean INET_REPORT_ALLOWED=true;
	public static boolean OTH_COM_CHNL = false;
	public static boolean SUBSCRIBER_PREFIX_ROUTING_ALLOWED = false;
	
	public static int VPIN_INVALID_COUNT;
	public static Long VOMS_PIN_BLK_EXP_DRN;
	public static int VOMS_DAMG_PIN_LNTH_ALLOW;
	
	public static String DATE_FORMAT_CAL_JAVA;
	public static String DATE_TIME_FORMAT;
	public static String LOCALE_ENGLISH;
	public static String TIMEZONE_ID;
	public static String CALENDAR_TYPE;
	public static String CALENDER_DATE_FORMAT;
	public static String CALENDAR_SYSTEM;
	public static String FORMAT_MONTH_YEAR;
	public static String EXTERNAL_CALENDAR_TYPE;
	public static String IS_CAL_ICON_VISIBLE;
	public static String IS_MON_DATE_ON_UI;
	
	 
	public static boolean USSD_RC_LANG_PARAM_REQ = false;
	public static boolean COMMA_ALLOW_IN_LOGIN; 
	public static boolean XML_DOC_ENCODING;
	public static String LAST_X_C2S_TXNSTATUS_ALLOWED = null;
	public static String LAST_X_CHNL_TXNSTATUS_ALLOWED = null;
	public static boolean USER_EXTERNAL_CODE_DOMAINWISE= false;
	public static int MAX_HOST_TEXTBOX_LENGTH =50;
	public static boolean LOAD_BALANCER_IP_ALLOWED=false;
	public static boolean CHECK_LAST_TXN_FROM_USER_PHONES=false;
	public static String  ALLOWED_SERVICES_FOR_FAIL_WHEN_AMBIGUOUS= null;
	public static String WIRC_ACCOUNT_MSISDN_OPT = null;
	 
	
	public static String VOUCHER_THIRDPARTY_STATUS ;
	
	public static boolean USER_VOUCHERTYPE_ALLOWED = false;
	public static boolean USER_VOUCHERSEGMENT_ALLOWED = false;
	
	public static boolean P2P_DEBITCREDIT_COMMON = false;

	public static boolean PIN_REQUIRED_P2P = false;
	
	public static boolean P2P_PRE_SERVCLASS_AS_POST = false;
	public static String OTP_ALLOWED_GATEWAY= "MAPPGW";
	public static double MOBILE_APP_VERSION =0.0;
	public static String GAT_CODE_FOR_BARRED_USERS="";
	public static boolean VAS_PRODUCT_GROUPING_REQ_MOBILEAPP =false;
	public static boolean USSD_REC_MSG_SEND_ALLOW=false;
	public static boolean LAST_C2C_ENQ_MSG_REQ=false;
	public static String MAPP_PRODUCT_GROUPING_REQ_SRV=null;
	public static boolean PG_INTEFRATION_ALLOWED =false;
	public static boolean CHNLUSR_VOUCHER_CATGRY_ALLWD = false;
	 
	public static String SERVICES_ALLOWED_SHOW_CARDGROUPLIST;
	public static boolean C2S_SEQID_ALWD=false;
	public static String C2S_SEQID_FOR_GWC="";
	public static String C2S_SEQID_APPL_SER="";
	public static String ADDITIONAL_IN_FIELDS_ALLOWED;
	public static boolean PAYMENT_MODE_ALWD;
	public static boolean TRANSACTION_TYPE_ALWD;
	public static int O2CAMB_MINUTES_DELAY;
	public static int VOMS_MIN_EXPIRY_DAYS=0;
	public static String VMS_ALLOW_CONTENT_TYPE;
	public static String C2C_ALLOW_CONTENT_TYPE;
	public static String EMAIL_DEFAULT_LOCALE;
	
	
	public static boolean PAYMENT_VERIFICATION_ALLOWED;
	public static String DOMAINCODE_FOR_SOS_YABX=null;
	public static String HANDLER_CLASS_FOR_YABX = null;
	public static boolean SOS_ALLOWED_FOR_YABX = false;
	public static boolean IPV6_ENABLED = false;
	public static String VMSPIN_EN_DE_CRYPTION_TYPE;
	public static boolean NATIONAL_VOUCHER_ENABLE = true;
	public static String NATIONAL_VOUCHER_NETWORK_CODE = null;
	public static int ONLINE_BATCH_EXP_DATE_LIMIT;
	public static int MAX_VOUCHER_EXPIRY_EXTN_LIMIT;
	public static String FILE_WRITER_CLASS = null;
	public static boolean IS_PGP_APPL = false;
	
	public static int VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN = 0;
	public static int VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN = 0;
	public static int VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN = 0;
	public static boolean  VOMS_PROF_TALKTIME_MANDATORY=true;
	public static boolean  VOMS_PROF_VALIDITY_MANDATORY=true;
	public static boolean  VOMS_PROFILE_DEF_MINMAXQTY=false;
	public static int VOMS_PROFILE_MIN_REORDERQTY;
	public static int VOMS_PROFILE_MAX_REORDERQTY;
	public static boolean DOWNLD_BATCH_BY_BATCHID = false;
	public static String NW_NATIONAL_PREFIX;
	public static String NW_CODE_NW_PREFIX_MAPPING;
	//added by Ashish for VIL
	public static boolean SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS = false;
	public static boolean ADD_INFO_REQUIRED_FOR_VOUCHER = false;
	public static boolean EMAIL_ALERT_FORVOMS_ORDER_INITIATOR = false;
	public static boolean SMS_ALERT_FORVOMS_ORDER_INITIATOR = false;

	public static boolean NET_PREFIX_TO_VALIDATED_FOR_BAR_UNBAR = true;
	public static boolean VOMS_IS_MRPID_IN_SERIAL = true;
	public static String USSD_RESP_SEPARATOR = "&";
	public static String DW_COMMISSION_CAL = "OTH";
	public static String DW_ALLOWED_GATEWAYS = "EXTGW";
	public static boolean IS_VOU_DEN_PROFILE_ZERO_ALLOW = false;
	public static String VMS_SERVICES;
	public static int ONLINE_VOUCHER_GEN_LIMIT = 0;
	public static boolean VOUCHER_GEN_EMAIL_NOTIFICATION = false;
	public static boolean VOUCHER_GEN_SMS_NOTIFICATION = false;
	public static String SCREEN_WISE_ALLOWED_VOUCHER_TYPE;
	public static String DVD_BATCH_FILEEXT;
	public static int ERROR_FILE_C2C;
	
	
	
	public static boolean SUBSCRIBER_VOUCHER_PIN_REQUIRED=false;
	
	public static int ONLINE_DVD_LIMIT = 0;
	
	public static int ONLINE_VOUCHER_GEN_LIMIT_SYSTEM = 0;

	/*public static String PHYSICAL_VOUCH_STATUS_ONLY = null;*/

	public static int ONLINE_VOUCHER_GEN_LIMIT_NW = 0;

	public static String VMS_D_STATUS_CHANGE;
	public static String VMS_E_STATUS_CHANGE;
	public static String VMS_P_STATUS_CHANGE;
	public static String VMS_D_STATUS_CHANGE_MAP;
	public static String VMS_E_STATUS_CHANGE_MAP;
	public static String VMS_P_STATUS_CHANGE_MAP;
	public static Boolean VOUCHER_PROFLE_IS_OPTIONAL;
	public static String VMS_D_LIFECYCLE;
	public static String VMS_P_LIFECYCLE;
	public static String VMS_E_LIFECYCLE;
	public static int  ONLINE_CHANGE_STATUS_SYSTEM_LMT;
	public static int  ONLINE_CHANGE_STATUS_NETWORK_LMT;
	public static String  DVD_ORDER_BY_PARAMETERS;
	//Added for voms_hcpt by niharika
	public static boolean VOUCHER_PROFILE_OTHER_INFO = false;
	
	public static String MAX_APPROVAL_LEVEL_C2C;
	public static String C2C_ALLOWED_VOUCHER_LIST;
	public static boolean IS_BUN_PRE_ID_NULL_ALLOW = false;
	public static boolean IS_VOU_BUN_NAME_LEN_ZERO_ALLOW = false;
	public static boolean IS_BLANK_VOUCHER_REQ=false;
	public static boolean C2C_EMAIL_NOTIFICATION;
	public static boolean C2C_SMS_NOTIFICATION;
	public static int C2CVCRPT_DATEDIFF;
	public static int O2CVCRPT_DATEDIFF;
	public static String CARD_GROUP_ALLOWED_CATEGORIES;
	public static String TRANSFER_RULE_ALLOWED_CATEGORIES;
	public static String ALPHANUM_SPCL_REGEX = null;
	public static int MIN_LAST_DAYS_CG;
	public static int MAX_LAST_DAYS_CG;
	public static int REPORT_MAX_DATEDIFF_ADMIN_CONS;
	public static int REPORT_MAX_DATEDIFF_USER_CONS;
	public static int REPORT_MAX_DATEDIFF_USER_AVAIL;
	public static int REPORT_MAX_DATEDIFF_ADMIN_AVAIL;
	public static int REPORT_MAX_DATEDIFF_ADMIN_NLEVEL;
	public static String USER_ALLOWED_VINFO;
	public static int RECENT_C2C_TXN;
	//Added for MRP Successive block timeout for channel transaction 
	public static boolean MRP_BLOCK_TIME_ALLOWED_CHNL_TXN=false;
	public static long SUCCESS_REQUEST_BLOCK_SEC_CODE_O2C; 
	public static long SUCCESS_REQUEST_BLOCK_SEC_CODE_C2C; 
	public static String CHANNEL_MRP_BLOCK_TIMEOUT_SERVICES_GATEWAY_CODES=null;
	public static boolean TWO_FA_REQ_FOR_PIN= false; 
	public static int OTP_RESEND_TIMES;
	public static int OTP_RESEND_DURATION;
	public static int OTP_VALIDITY_PERIOD;
	public static int MAX_INVALID_OTP;
	public static int BLOCK_TIME_INVALID_OTP;
	public static String SMS_SENDER_NAME_FOR_SERVICE_TYPE="RC:eTOPUP Customer Recharge";
	public static boolean BYPASS_EVD_KANNEL_MESSAGE_STATUS = false;
	public static String LANGS_SUPT_ENCODING=null;
	public static boolean IS_ONE_TIME_SID=false;
	public static boolean COM_PAY_OUT = false;
	public static boolean ALERT_ALLOWED_USER = false;
	public static boolean IS_EMAIL_ALLOWED_AUTO_NTWKSTK = false;
	public static String C2C_BATCH_FILEEXT;
	public static String UNLOCK_ZERO_MIGRATIONS;
	public static boolean ERP_VOU_WH = false;
	public static long MAX_BULK_FILE_SIZE_BYTES;
	public static String DEFAULT_PRODUCT_CODE = null;
	public static String USER_ALLOW_CONTENT_TYPE = null;
	public static boolean REPORT_OFFLINE=false;
	public static String OFFLINERPT_DOWNLD_PATH=null;
	public static String DEF_CHNL_TRANSFER_ALLOWED = null;
	//added for Retailer loan CR Merge
	public static int LOAN_PROFILE_SLAB_LENGTH;
	public static boolean USERWISE_LOAN_ENABLE = false;
	public static String ALLOW_TRANSACTION_IF_LOAN_SETTLEMENT_FAIL;
	public static String BLOCK_GATEWAYCODE_FOR_LOAN_SETTLEMENT;
	public static String RESTRICT_TRANSACTION_FOR_LOAN_SETTLEMENT;
	public static boolean CAT_USERWISE_LOAN_ENABLE = false;
	public static int TEMP_PIN_EXPIRY_DURATION = 0;
	public static boolean IMEI_OPTIONAL;
	public static String EXTREFNUM_MANDATORY_GATEWAYS;
	public static String CATEGORIES_LIFECYCLECHANGE = "";
	public static boolean IS_RECHARGE_REQUEST=false;
	public static int DAYS_FOR_SENDING_MESSAGE = 3;
	public static int LAST_N_DAYS_EVD_TRF;
	public static String SMS_PIN_BYPASS_GATEWAY_CODE;
	
	
	public static void load() {
		final String methodName = "load";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		try {
			try {
				INET_REPORT_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.INET_REPORT_ALLOWED)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			
			try {
				CHANNEL_TRANSFERS_INFO_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOUCHER_SERVICES_LIST = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOUCHER_SERVICES_LIST)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DEFAULT_PRODUCT_CODE = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT_CODE)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				USER_ALLOW_CONTENT_TYPE = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USER_ALLOW_CONTENT_TYPE)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MSISDN_PREFIX_LIST = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MSISDN_PREFIX_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LENGTH_CODE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				MAX_BULK_FILE_SIZE_BYTES = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LENGTH_CODE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			
			
			try {
				MIN_MSISDN_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH_CODE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_MSISDN_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MSISDN_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MSISDN_LENGTH_CODE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_MAX_PIN_BLOCK_COUNT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_MAX_PIN_BLOCK_COUNT_CODE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_MAX_PIN_BLOCK_COUNT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_MAX_PIN_BLOCK_COUNT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DAILY_CNSTIVE_FAIL_COUNT_BEFOREBAR = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DAILY_CNSTIVE_FAIL_COUNT_BEFOREBAR)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SYSTEM_DATETIME_FORMAT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PIN_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PIN_REQUIRED_CODE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PIN_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PIN_LENGTH_CODE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DEFAULT_LANGUAGE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SKEY_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SKEY_REQUIRED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				AMOUNT_MULT_FACTOR = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SKEY_EXPIRY_TIME = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SKEY_EXPIRY_TIME_CODE)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SKEY_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SKEY_LENGTH_CODE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MIN_RESIDUAL_BAL_TYPE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MIN_RESIDUAL_BAL_TYPE_CODE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				INVALID_PWD_COUNT_FOR_CAPTCHA = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.INVALID_PWD_COUNT_FOR_CAPTCHA));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CAPTCHA_LENGTH = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CAPTCHA_LENGTH));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				IS_TAX2_ON_TAX1 = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TAX2_ON_TAX1_CODE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SUBSCRIBER_FAIL_CTINCR_CODES = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_FAIL_CTINCR_CODES));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SKEY_DEFAULT_SENT_TO = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SKEY_DEFAULT_SENT_TO));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SYSTEM_DATE_FORMAT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				USE_PPAID_USER_DEFINED_CONTROLS = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USE_PPAID_USER_DEFINED_CONTROLS)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LANGAUGES_SUPPORTED = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LANGAUGES_SUPPORTED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DEFAULT_COUNTRY = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_USER_REGISTRATION_REQUIRED = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_USER_REGISTRATION_REQUIRED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_ALLOW_SELF_TOPUP = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_ALLOW_SELF_TOPUP)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				USE_C2S_SEPARATE_TRNSFR_COUNTS = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USE_C2S_SEPARATE_TRNSFR_COUNTS)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_PIN_BLK_RST_DURATION = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_PIN_BLK_RST_DURATION)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_PIN_BLK_RST_DURATION = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_RST_DURATION)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_DAYS_AFTER_CHANGE_PIN = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_DAYS_AFTER_CHANGE_PIN)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_DAYS_AFTER_CHANGE_PIN = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_DAYS_AFTER_CHANGE_PIN)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_MSISDN_TEXTBOX_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_TEXTBOX_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_SNDR_CREDIT_BK_AMB_STATUS = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CREDIT_BK_AMB_STATUS)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_SNDR_CREDIT_BK_AMB_STATUS = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_SNDR_CREDIT_BACK_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BACK_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PASSWORD_BLK_RST_DURATION = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PASSWORD_BLK_RST_DURATION)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_PASSWORD_BLOCK_COUNT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_PASSWORD_BLOCK_COUNT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				TRANSFER_DEFAULT_SERVICETYPE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_USER_HIERARCHY_SIZE = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USER_HIERARCHY_SIZE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DEFAULT_PRODUCT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_PIN_MIN_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_PIN_MIN_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_PIN_MAX_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_PIN_MAX_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_LOGIN_PWD_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MIN_LOGIN_PWD_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_SMS_PIN_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MIN_SMS_PIN_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_DEFAULT_SMSPIN = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_DEFAULT_PASSWORD = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CRYSTAL_REPORT_MAX_DATEDIFF = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				REPORT_MAX_DATEDIFF = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REPORT_MAX_DATEDIFF)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_DATEDIFF = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_DATEDIFF)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_DEFAULT_SMSPIN = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PIN_PASSWORD_ALERT_DAYS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PIN_PASSWORD_ALERT_DAYS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DAYS_AFTER_CHANGE_PASSWORD = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DAYS_AFTER_CHANGE_PASSWORD)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CHANGE_PASSWORD_NOT_REQUIRED_CATEGORY = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHANGE_PASSWORD_NOT_REQUIRED_CATEGORY));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CHNL_PLAIN_SMS_SEPARATOR = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			 
			 
			try {
				P2P_PLAIN_SMS_SEPARATOR = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				GROUP_ROLE_ALLOWED = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.GROUP_ROLE_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DEF_FRCXML_SEL_P2P = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_P2P));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DEF_FRCXML_SEL_C2S = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EXTERNAL_TXN_UNIQUE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				FINANCIAL_YEAR_START_INDEX = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.FINANCIAL_YEAR_START_INDEX)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EXTERNAL_TXN_NUMERIC = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_NUMERIC)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			try {
				EXTERNAL_TXN_MANDATORY_DOMAINTYPE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_DOMAINTYPE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EXTERNAL_TXN_MANDATORY_FORFOC = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_FORFOC));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				NOTIFICATION_SERVICECLASSWISE_SEN = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_SEN)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				NOTIFICATION_SERVICECLASSWISE_REC = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				NOTIFICATION_SERVICECLASSWISE_REC_C2S = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_C2S)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				CHK_BLK_LST_STAT = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHK_BLK_LST_STAT)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				SAP_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SAP_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			
			try {
				VOMS_MIN_ALT_VALUE = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_MIN_ALT_VALUE)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				VOMS_ORDER_SLAB_LENGTH = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_ORDER_SLAB_LENGTH)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			
			
			try {
				NOTIFICATION_SERVICECLASSWISE_REC_BILLPAY = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_BILLPAY)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_ALLOW_SELF_BILLPAY = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_ALLOW_SELF_BILLPAY)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_SNDR_CREDIT_BK_AMB_STATUS_BILLPAY = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CREDIT_BK_AMB_STATUS_BILLPAY)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			try {
				DEF_FRCXML_SEL_BILLPAY = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_BILLPAY));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SEP_OUTSIDE_TXN_CTRL = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				POSTPAID_REGISTER_AS_ACTIVATED_STATUS = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.POSTPAID_REGISTER_AS_ACTIVATED_STATUS)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SPACE_ALLOW_IN_LOGIN = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SPACE_ALLOW_IN_LOGIN)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DEFAULT_WEB_GATEWAY_CODE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SECOND_LANGUAGE_ENCODING = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SECOND_LANGUAGE_ENCODING));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				GRPT_CHRG_ALLOWED = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				GRPT_CTRL_ALLOWED = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				GRPT_CONTROL_LEVEL = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.GRPT_CONTROL_LEVEL));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_ALLOW_SELF_TOPUP = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_ALLOW_SELF_TOPUP)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EXTERNAL_CODE_MANDATORY_FORFOC = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORFOC)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SECOND_LANGUAGE_CHARSET = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SECOND_LANGUAGE_CHARSET));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2C_RET_PARENT_ONLY = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2C_RET_PARENT_ONLY)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DECIMAL_ALLOWED_IN_SERVICES = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DECIMAL_ALLOW_SERVICES));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EXTERNAL_DATE_FORMAT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				XML_MAX_RCD_SUM_RESP = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.XML_MAX_RCD_SUM_RESP)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				XML_DATE_RANGE = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.XML_DATE_RANGE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				XML_DFT_DATE_RANGE = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.XML_DFT_DATE_RANGE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				C2S_ALLOW_SELF_UB = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_ALLOW_SELF_UB)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			 
			try {
				C2S_SNDR_CRDT_BK_AMB_UB = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CRDT_BK_AMB_UB)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				NOTIFICATION_SERVICECLASSWISE_REC_UB = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_UB)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MIN_IDENTIFICATION_NUMBER_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MIN_IDENTIFICATION_NUMBER_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_IDENTIFICATION_NUMBER_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_IDENTIFICATION_NUMBER_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				USE_HOME_STOCK = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				OTHERID_PREFIX_LIST = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OTHERID_PREFIX_LIST));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IDENTIFICATION_NUMBER_VAL_TYPE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IDENTIFICATION_NUMBER_VAL_TYPE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ALPHA_ID_NUM_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALPHA_ID_NUM_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				RESPONSE_IN_DISPLAY_AMT = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RESPONSE_IN_DISPLAY_AMT)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_DENOMINATION_VAL = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_DENOMINATION_VAL)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_UPEXPHOURS = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_UPEXPHOURS));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_OFFPEAKHRS = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_OFFPEAKHRS));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_MAX_TOTAL_VOUCHER_OT = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_MAX_TOTAL_VOUCHER_OT)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_MAX_TOTAL_VOUCHER_EN = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_MAX_TOTAL_VOUCHER_EN)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_MAX_ERROR_COUNTEN = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_MAX_ERROR_COUNTEN)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_MAX_ERROR_COUNTOTH = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_MAX_ERROR_COUNTOTH)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_MAX_BATCH_DAY = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_MAX_BATCH_DAY)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_DATE_FORMAT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_DATE_FORMAT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SELF_VOUCHER_DISTRIBUTION_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SELF_VOUCHER_DISTRIBUTION_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PIN_SEND_TO = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PIN_SEND_TO));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DELIVERY_RECEIPT_TRACKED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DELIVERY_RECEIPT_TRACKED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CREDIT_BACK_ALWD_EVD_AMB = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CREDIT_BACK_ALWD_EVD_AMB)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				NOTIFICATION_SERVICECLASSWISE_REC_EVD = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_EVD)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			 
			 
			try {
				FOC_SMS_NOTIFY = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.FOC_SMS_NOTIFY)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PROFILEASSOCIATE_AGENT_PREFERENCES = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			 
			 
			try {
				VOMS_SERIAL_NO_MIN_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_SERIAL_NO_MAX_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_PIN_MIN_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MIN_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_PIN_MAX_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_REQ_VOUCHER_QTY = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_REQ_VOUCHER_QTY)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				STAFF_USER_COUNT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.STAFF_USER_COUNT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PORT_OUT_USER_SUSPEND_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PORT_USR_SUSPEND_REQ)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MNP_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DIRECT_VOUCHER_ENABLE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DIRECT_VOUCHER_ENABLE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MVD_MAX_VOUCHER = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MVD_MAX_VOUCHER)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				PTUPS_MOBQUTY_MERGD = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				AUTO_PAYMENT_METHOD = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				LOW_BAL_MSGGATEWAY = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LOW_BAL_MSGGATEWAY));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			try {
				ALWD_NUMBCK_SERVICES = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALWD_NUMBCK_SERVICES)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				RC_ALWD_ACC_STATUS_NUMBCK = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RC_ALWD_ACC_STATUS_NUMBCK)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				RC_NUMBCK_DIFF_REQ_TO_IN = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RC_NUMBCK_DIFF_REQ_TO_IN)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				RC_NUMBCK_ALWD_DAYS_DIFF = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RC_NUMBCK_ALWD_DAYS_DIFF)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				RC_NUMBCK_AMT_DEDCTED = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RC_NUMBCK_AMT_DEDCTED)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PRC_ALWD_ACC_STATUS_NUMBCK = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PRC_ALWD_ACC_STATUS_NUMBCK)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PRC_NUMBCK_DIFF_REQ_TO_IN = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PRC_NUMBCK_DIFF_REQ_TO_IN)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PRC_NUMBCK_ALWD_DAYS_DIFF = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PRC_NUMBCK_ALWD_DAYS_DIFF)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PRC_NUMBCK_AMT_DEDCTED = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PRC_NUMBCK_AMT_DEDCTED)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			try {
				WE_REC_AMT_ALLWD_P2P = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.WE_REC_AMT_ALLWD_P2P)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				WE_SUCTRAN_ALLWD_P2P = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.WE_SUCTRAN_ALLWD_P2P)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MO_REC_AMT_ALLWD_P2P = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MO_REC_AMT_ALLWD_P2P)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MO_SUCTRAN_ALLWD_P2P = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MO_SUCTRAN_ALLWD_P2P)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_ALLD_BALANCE_P2P = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_ALLD_BALANCE_P2P)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				WE_SUCTRAN_ALLWDCOUN = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.WE_SUCTRAN_ALLWDCOUN)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				WE_TOTXN_AMT_ALLWDCO = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.WE_TOTXN_AMT_ALLWDCO)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MO_SUCTRAN_ALLWDCOUN = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MO_SUCTRAN_ALLWDCOUN)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MO_TOTXN_AMT_ALLWDCO = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MO_TOTXN_AMT_ALLWDCO)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_ALLD_BALANCE_C2S = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_ALLD_BALANCE_C2S)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			try {
				PAYAMT_MRP_SAME = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			try {
				C2S_REF_NUMBER_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_REF_NUMBER_REQUIRED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_REF_NUMBER_UNIQUE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_REF_NUMBER_UNIQUE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				PREV_PASS_NOT_ALLOW = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PREV_PASS_NOT_ALLOW)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PREV_PIN_NOT_ALLOW = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				C2S_PWD_BLK_EXP_DURATION = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_PWD_BLK_EXP_DURATION)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				C2S_PIN_BLK_EXP_DURATION = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_PIN_BLK_EXP_DURATION)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_PIN_BLK_EXP_DURATION = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_PIN_BLK_EXP_DURATION)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				IS_FEE_APPLICABLE_FOR_VALIDATION_EXTENSION = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_FEE_APPLICABLE_FOR_VALIDATION_EXTENSION)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				FEE_AND_VALIDITY_DAYS_TO_EXT = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.FEE_AND_VALIDITY_DAYS_TO_EXT)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IS_CREDIT_TRFR_WITH_VALIDITY_UPDATION_P2P = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_CREDIT_TRFR_WITH_VALIDITY_UPDATION_P2P)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VAL_DAYS_TO_CHK_VALUPD = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VAL_DAYS_TO_CHK_VALUPD)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				;
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				IS_SEPARATE_RPT_DB = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_RPT_DB)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ALLOW_SELF_EVR = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALLOW_SELF_EVR)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				NEG_ADD_COMM_APPLY = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NEG_ADD_COMM_APPLY)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				IS_SEPARATE_BONUS_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_BONUS_REQUIRED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				REC_MSG_SEND_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REC_MSG_SEND_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				OPT_USR_APRL_LEVEL = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OPT_USR_APRL_LEVEL)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				APPROVER_CAN_EDIT = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DISSABLE_BUTTON_LIST = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DISSABLE_BUTTON_LIST)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_REG_EXPIRY_PERIOD = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_REG_EXPIRY_PERIOD)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MESSAGE_TO_PRIMARY_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SECONDARY_NUMBER_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				C2C_SMS_NOTIFY = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2C_SMS_NOTIFY)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				SMS_MMS_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SMS_MMS_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CIRCLEMINLMT = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CIRCLEMINLMT)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				CP2P_PIN_VALIDATION_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				FIXLINE_RC_ALLOW_SELF_TOPUP = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.FIXLINE_RC_ALLOW_SELF_TOPUP)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				BROADBAND_RC_ALLOW_SELF_TOPUP = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.BROADBAND_RC_ALLOW_SELF_TOPUP)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_RANDOM_PIN_GENERATE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_RANDOM_PIN_GENERATE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				WEB_RANDOM_PWD_GENERATE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.WEB_RANDOM_PWD_GENERATE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				BATCH_USER_PASSWD_MODIFY_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.BATCH_USER_PASSWD_MODIFY_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				RESET_PASSWORD_EXPIRED_TIME_IN_HOURS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RESET_PASSWORD_EXPIRED_TIME_IN_HOURS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				RESET_PIN_EXPIRED_TIME_IN_HOURS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RESET_PIN_EXPIRED_TIME_IN_HOURS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_CARD_GROUP_SLAB_COPY = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_CARD_GROUP_SLAB_COPY));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_CARD_GROUP_SLAB_COPY = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_CARD_GROUP_SLAB_COPY));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				STK_REG_ICCID = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.STK_REG_ICCID)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				ACTIVATION_BONUS_MIN_REDEMPTION_AMOUNT = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ACTIVATION_BONUS_MIN_REDEMPTION_AMOUNT)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ACTIVATION_BONUS_REDEMPTION_DURATION = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ACTIVATION_BONUS_REDEMPTION_DURATION)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOLUME_CALC_ALLOWED = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOLUME_CALC_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				POSITIVE_COMM_APPLY = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.POSITIVE_COMM_APPLY)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				AUTO_PWD_GENERATE_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTO_PWD_GENERATE_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				AUTO_PIN_GENERATE_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PSWD_EXP_TIME_IN_HOUR_AFTER_CREATION = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PSWD_EXP_TIME_IN_HOUR_AFTER_CREATION)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CHECK_REC_AMBIGUOUS_TXN_AT_IAT = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHECK_REC_TXN_AT_IAT)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IS_IAT_RUNNING = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_IAT_RUNNING)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				POINT_CONVERSION_FACTOR = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.POINT_CONVERSION_FACTOR)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				LAST_X_TRANSFER_STATUS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LAST_X_TRANSFER_STATUS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SERVICE_FOR_LAST_X_TRANSFER = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SERVICE_FOR_LAST_X_TRANSFER));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_RECHARGE_MULTIPLE_ENTRY = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_RECHARGE_MULTIPLE_ENTRY));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				LAST_X_CUSTENQ_STATUS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LAST_X_CUSTENQ_STATUS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				MULTIPLE_WALLET_APPLY = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				RVERSE_TRN_EXPIRY = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RVERSE_TRN_EXPIRY)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ALWD_REVTXN_SERVICES = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALWD_REVTXN_SERVICES)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				RVE_C2S_TRN_EXPIRY = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RVE_C2S_TRN_EXPIRY)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CHNL_PLAIN_SMS_SEPT_LOGINID = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPT_LOGINID));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				EXTERNAL_CODE_MANDATORY_FORDP = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORDP)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EXTERNAL_TXN_MANDATORY_FORDP = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_FORDP));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DP_ORDER_APPROVAL_LVL = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DP_ORDER_APPROVAL_LVL)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EXTERNAL_TXN_MANDATORY_DOMAINTYPE_DP = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_DOMAINTYPE_DP));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DP_SMS_NOTIFY = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DP_SMS_NOTIFY)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DP_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DP_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LAST_TRF_MULTIPLE_SMS = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LAST_TRF_MULTIPLE_SMS)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LAST_X_TRF_DAYS_NO = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LAST_X_TRF_DAYS_NO)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SHA2_FAMILY_TYPE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SHA2_FAMILY_TYPE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				HTTPS_ENABLE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.HTTPS_ENABLE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PINPAS_EN_DE_CRYPTION_TYPE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				NWADM_CROSS_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NWADM_CROSS_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				STAFF_AS_USER = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.STAFF_AS_USER)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			 
			try {
				PRIVATE_RECHARGE_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				NAMEEMBOSS_SEPT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NAMEEMBOSS_SEPT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LOGIN_SPECIAL_CHAR_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LOGIN_SPECIAL_CHAR_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MVD_MIN_VOUCHER = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MVD_MIN_VOUCHER)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_PIN_ENCRIPTION_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_PIN_ENCRIPTION_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CARD_GROUP_BONUS_RANGE = ((Integer) ((PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CARD_GROUP_BONUS_RANGE))));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IS_DEFAULT_PROFILE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				REQ_CUSER_DLT_APP = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REQ_CUSER_DLT_APP)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				O2C_EMAIL_NOTIFICATION = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.O2C_EMAIL_NOTIFICATION)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				FOC_ODR_APPROVAL_LVL = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.FOC_ODR_APPROVAL_LVL)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				AUTO_C2C_TRANSFER_AMT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTO_C2C_TRANSFER_AMT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SMS_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SMS_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				MVD_MAX_VOUCHER_EXTGW = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MVD_MAX_VOUCHER_EXTGW)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				CRYSTAL_SUMMARY_REPORT_MAX_DATEDIFF = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CRYSTAL_SUMMARY_REPORT_MAX_DATEDIFF)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				SID_ISNUMERIC = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SID_ISNUMERIC)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MIN_SID_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MIN_SID_LENGTH_CODE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_SID_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_SID_LENGTH_CODE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				MSISDN_MIGRATION_LIST = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MSISDN_MIGRATION_LIST_CODE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				USER_EVENT_REMARKS = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				MIN_SID_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MIN_SID_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_SID_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_SID_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				PRIVATE_SID_SERVICE_ALLOW = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PRIVATE_SID_SERVICE_ALLOW));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				MULT_CRE_TRA_DED_ACC_SEP = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MULT_CRE_TRA_DED_ACC_SEP));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EXTERNAL_TXN_MANDATORY_FORO2C = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_FORO2C));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EXTERNAL_CODE_MANDATORY_FORO2C = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORO2C)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				RP2PDWH_OPT_SPECIFIC_PROC_NAME = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RP2PDWH_OPT_SPECIFIC_PROC_NAME));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2PDWH_OPT_SPECIFIC_PROC_NAME = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2PDWH_OPT_SPECIFIC_PROC_NAME));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IATDWH_OPT_SPECIFIC_PROC_NAME = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IATDWH_OPT_SPECIFIC_PROC_NAME));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				PRIVATE_RECH_MESSGATEWAY = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PRIVATE_RECH_MESSGATEWAY));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				ALLOW_ROAM_ADDCOMM = (Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_RECHARGE);
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			 
			 
			 
			try {
				PLAIN_RES_PARSE_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PLAIN_RES_PARSE_REQUIRED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				COUNTRY_CODE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.COUNTRY_CODE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				USSD_NEW_TAGS_MANDATORY = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USSD_NEW_TAGS_MANDATORY))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				O2C_SMS_NOTIFY = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.O2C_SMS_NOTIFY)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PARENT_CATEGORY_LIST = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PARENT_CATEGORY_LIST));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				OWNER_CATEGORY_LIST = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OWNER_CATEGORY_LIST));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_AUTO_FOC_ALLOW_LIMIT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_AUTO_FOC_ALLOW_LIMIT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				AUTO_FOC_TRANSFER_AMOUNT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTO_FOC_TRANSFER_AMOUNT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ACTIVATION_FIRST_REC_APP = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ACTIVATION_FIRST_REC_APP)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				MRP_BLOCK_TIME_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MRP_BLOCK_TIME_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{
				MAPP_SESSION_EXPIRY_SEC =((Integer)(PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAPP_SESSION_EXPIRY_SEC)))
						.intValue();
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
		    }
			try {
				ACTIVATION_FIRST_REC_APP = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ACTIVATION_FIRST_REC_APP)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				SOS_SETTLE_DAYS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SOS_SETTLE_DAYS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SOS_RECHARGE_AMOUNT = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SOS_RECHARGE_AMOUNT)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SOS_MIN_VALIDITY_DAYS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SOS_MIN_VALIDITY_DAYS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SOS_ALLOWED_MAX_BALANCE = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SOS_ALLOWED_MAX_BALANCE)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			 
			try {
				SOS_ST_DEDUCT_UPFRONT = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SOS_ST_DEDUCT_UPFRONT)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SOS_DAYS_GAP_BTWN_TWO_TRAN = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SOS_DAYS_GAP_BTWN_TWO_TRAN)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SERV_CLASS_ALLOW_FOR_SOS = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SERV_CLASS_ALLOW_FOR_SOS));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			 
			try {
				SOS_MINIMUM_AON = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SOS_MINIMUM_AON)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SOS_ELIBILITY_ACCOUNT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SOS_ELIBILITY_ACCOUNT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SOS_ONLINE_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SOS_ONLINE_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				ENQ_POSTBAL_IN = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_IN)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			 
			 
			try {
				ENQ_POSTBAL_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LMB_VALIDITY_DAYS_FORCESETTLE = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LMB_VALIDITY_DAYS_FORCESETTLE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LMB_BLK_UPL = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LMB_BLK_UPL)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LMB_FORCE_SETL_STAT_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LMB_FORCE_SETL_STAT_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_MAX_APPROVAL_LEVEL = ((Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_MAX_APPROVAL_LEVEL))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_USER_KEY_REQD = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_USER_KEY_REQD)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				DB_ENTRY_NOT_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				RVERSE_TXN_APPRV_LVL = ((Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RVERSE_TXN_APPRV_LVL))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOUCHER_TRACKING_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOUCHER_TRACKING_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOUCHER_EN_ON_TRACKING = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOUCHER_EN_ON_TRACKING)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PROCESS_FEE_REV_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PROCESS_FEE_REV_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MCDL_MAX_LIST_COUNT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MCDL_MAX_LIST_COUNT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MCDL_DIFFERENT_REQUEST_SEPERATOR = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MCDL_DIFFERENT_REQUEST_SEPERATOR));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_MCDL_DEFAULT_AMOUNT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_MCDL_DEFAULT_AMOUNT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_MCDL_MAXADD_AMOUNT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_MCDL_MAXADD_AMOUNT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				P2P_MCDL_AUTO_DELETION_DAYS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_MCDL_AUTO_DELETION_DAYS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LMB_DEBIT_REQ = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LMB_DEBIT_REQ)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PRVT_RC_MSISDN_PREFIX_LIST = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PRVT_RC_MSISDN_PREFIX_LIST));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			} 
			try {
				IS_FNAME_LNAME_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			} 
			 
			try {
				LAST_X_RECHARGE_STATUS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LAST_X_RECHARGE_STATUS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SERVICE_FOR_LAST_X_RECHARGE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SERVICE_FOR_LAST_X_RECHARGE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SMS_PIN_BYPASS_GATEWAY_TYPE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				CATEGORY_ALLOWED_FOR_CREATION = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CATEGORY_ALLOWED_FOR_CREATION));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CP_SUSPENSION_DAYS_LIMIT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CP_SUSPENSION_DAYS_LIMIT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				EXTERNAL_CODE_MANDATORY_FORUSER = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORUSER)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			 
			try {
				SELECTOR_INTERFACE_MAPPING = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PLAIN_RES_PARSE_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PLAIN_RES_PARSE_REQUIRED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			try {
				EXT_VOMS_MSG_DESEDECRYPT_KEY = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXT_VOMS_MSG_DES_ENDE_CRYPT_KEY));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				LOGIN_ID_CHECK_ALLOWED = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LOGIN_ID_CHECK_ALLOWED))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				STAFF_USER_APRL_LEVEL = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.STAFF_USER_APRL_LEVEL)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				RSA_AUTHENTICATION_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				BATCH_USER_PROFILE_ASSIGN = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				BATCH_INTIATE_NOTIF_TYPE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.BATCH_INTIATE_NOTIF_TYPE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IS_TRF_RULE_USER_LEVEL_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				IS_EMAIL_SERVICE_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				COS_REQUIRED = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.COS_REQUIRED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CELL_GROUP_REQUIRED = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SERVICE_PROVIDER_PROMO_ALLOW = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SRVC_PROD_MAPPING_ALLOWED = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SRVC_PROD_INTFC_MAPPING_ALLOWED = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SRVC_PROD_INTFC_MAPPING_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CELL_ID_SWITCH_ID_REQUIRED = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CELL_ID_SWITCH_ID_REQUIRED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MULTI_AMOUNT_ENABLED = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MULTI_AMOUNT_ENABLED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IN_PROMO_REQUIRED = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DEBIT_SENDER_SIMACT = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEBIT_SENDER_SIMACT)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SIMACT_DEFAULT_SELECTOR = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SIMACT_DEFAULT_SELECTOR));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LOGIN_PASSWORD_ALLOWED = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				AUTO_O2C_MAX_APPROVAL_LEVEL = ((Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTO_O2C_MAX_APPROVAL_LEVEL))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				AUTO_O2C_AMOUNT = ((Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTO_O2C_AMOUNT))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				REQ_CUSER_BAR_APPROVAL = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REQ_CUSER_BAR_APPROVAL)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SRVCS_FOR_PROD_MAPPING = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SRVCS_FOR_PROD_MAPPING));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SHOW_CAPTCHA = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SHOW_CAPTCHA))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EMAIL_AUTH_REQ = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EMAIL_AUTH_REQ))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MIN_HRDIF_ST_ED_LMS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MIN_HRDIF_ST_ED_LMS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MIN_HRDIF_CR_ST_LMS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MIN_HRDIF_CR_ST_LMS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LMS_APPL = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LMS_APPL))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			try {
				P2P_SMCDL_ALLOWED_SCHEDULE_TYPE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_MCDL_ALLOWED_SCHEDULE_TYPE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			 
			 
			 
			 
			 
			 
			 
			try {
				LMS_VOL_COUNT_ALLOWED = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LMS_VOL_COUNT_ALLOWED))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LMS_PROF_APR_ALLOWED = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LMS_PROF_APR_ALLOWED))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				AUTO_O2C_APPROVAL_ALLOWED = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTO_O2C_APPROVAL_ALLOWED))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				AUTH_TYPE_REQ = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTH_TYPE_REQ))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				TWO_FA_REQ = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TWO_FA_REQ))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				SAP_INTEGARATION_FOR_USRINFO = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SAP_INTEGARATION_FOR_USRINFO))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MIN_ACCOUNT_ID_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MIN_ACCOUNT_ID_LENGTH_CODE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_ACCOUNT_ID_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_ACCOUNT_ID_LENGTH_CODE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_TRNSFR_AMTBLCK_SRVCTYP = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_TRNSFR_AMTBLCK_SRVCTYP));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				OTP_TIMEOUT_INSEC = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OTP_TIMEOUT_INSEC)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				OTP_ALLOWED_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OTP_ALLOWED_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_TRNSFR_INVNO_SRVCTYP = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_TRNSFR_INVNO_SRVCTYP));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				O2C_PNDNG_FOR_APPR_SEND_MAIL_DAYS_CNT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.O2C_PNDNG_FOR_APPR_SEND_MAIL_DAYS_CNT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2S_REVERSAL_TXNID_SRVCTYP = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2S_REVERSAL_TXNID_SRVCTYP)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LMS_STOCK_REQUIRED = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LMS_STOCK_REQUIRED))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LMS_MULT_FACTOR = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LMS_MULT_FACTOR)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				LMS_VOL_CREDIT_LOYAL_PTS = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LMS_VOL_CREDIT_LOYAL_PTS))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IS_REQ_MSISDN_FOR_STAFF = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_REQ_MSISDN_FOR_STAFF))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EXTSYS_USR_APRL_LEVEL_REQUIRED = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTSYS_USR_APRL_LEVEL_REQUIRED)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_LAST_TRANSFERS_DAYS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_LAST_TRANSFERS_DAYS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_AUTOTOPUP_AMT = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_AUTOTOPUP_AMT)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PAYMENTDETAILSMANDATE_O2C = ((Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PAYMENTDETAILSMANDATE_O2C))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PAYMENTDETAILSMANDATE_C2C = ((Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PAYMENTDETAILSMANDATE_C2C))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				USER_CREATION_MANDATORY_FIELDS = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USER_CREATION_MANDATORY_FIELDS));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				USER_APPROVAL_LEVEL = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USER_APPROVAL_LEVEL)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				STAFF_USER_AUTH_TYPE = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.STAFF_USER_AUTH_TYPE)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CHANNEL_USER_ROLE_TYPE_DISPLAY = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				O2C_APPRV_QTY_LEVEL = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.O2C_APPRV_QTY_LEVEL));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IS_REQ_MSISDN_FOR_STAFF = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_REQ_MSISDN_FOR_STAFF)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				THLD_PRTP_PRCSS_TIME = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.THLD_PRTP_PRCSS_TIME)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				PERCENTAGE_OF_PRE_REVERSAL = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PERCENTAGE_OF_PRE_REVERSAL)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				ALLOW_ROAM_ADDCOMM = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				ALLOW_CCARD_ROAM_RECHARGE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALLOW_CCARD_ROAM_RECHARGE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MULTIPLE_VOUCHER_TABLE = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ALLOW_ROAM_RECHARGE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_RECHARGE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ROAM_INTERFACE_ID = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ROAM_INTERFACE_ID));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LMS_PCT_POINTS_CALCULATION = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LMS_PCT_POINTS_CALCULATION)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IS_PARTIAL_BATCH_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				USER_PRODUCT_MULTIPLE_WALLET = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MIN_VOUCHER_CODE_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MIN_VOUCHER_CODE_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DEFAULT_WALLET = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				WALLET_FOR_ADNL_CMSN = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IS_MSISDN_ASSOCIATION_REQ = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_MSISDN_ASSOCIATION_REQ)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ADMINISTRBLY_USER_STATUS_CHANG = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ADMINISTRBLY_USER_STATUS_CHANG)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				REALTIME_AUTO_C2C_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REALTIME_AUTO_C2C_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				TXN_SENDER_USER_STATUS_CHANG = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				TXN_RECEIVER_USER_STATUS_CHANG = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				LIFECYCLE_STATUS_DAYS_LIST = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LIFECYCLE_STATUS_DAYS_LIST));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				ROAM_RECHARGE_DAILY_THRESHOLD = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ROAM_RECHARGE_DAILY_THRESHOLD)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ROAM_RECHARGE_PENALTY_PERCENTAGE = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ROAM_RECHARGE_PENALTY_PERCENTAGE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ROAM_PENALTY_OWNER_PERCENTAGE = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ROAM_PENALTY_OWNER_PERCENTAGE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			 
			try {
				OPT_IN_OUT_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EXTERNAL_TXN_MANDATORY_FOR_LPT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_FOR_LPT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				EXTERNAL_CODE_MANDATORY_FOR_LPT = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FOR_LPT)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				M_PRE_PERCENTAGE = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.M_PRE_PERCENTAGE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				M_SLAVE_PERCENTAGE = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.M_SLAVE_PERCENTAGE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				ALERT_ALLOWED = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALERT_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		
			
			try {
				BLOCKING_ALLOWED = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.BLOCKING_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				INTRFC_MAX_NODES = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.INTRFC_MAX_NODES)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				RETURN_TO_OPERATOR_STOCK = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RETURN_TO_OPERATOR_STOCK))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DECENTER_ROAM_LOCATION = ((String) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DECENTER_ROAM_LOCATION));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DP_NETWORKLEVEL_DAILYLIMIT = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DP_NETWORKLEVEL_DAILYLIMIT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DP_SYSTEMLEVEL_LIMIT = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DP_SYSTEMLEVEL_LIMIT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DP_ONLINE_LIMIT = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DP_ONLINE_LIMIT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				INTF_NODE_VALIDATION = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.INTF_NODE_VALIDATION)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				LB_SYSTEMLEVEL_LIMIT = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LB_SYSTEMLEVEL_LIMIT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				CHOICE_RECHARGE_APPLICABLE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHOICE_RECHARGE_APPLICABLE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				TIME_FOR_REVERSAL = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TIME_FOR_REVERSAL)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				TIME_FOR_REVERSAL_CCE = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TIME_FOR_REVERSAL_CCE)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ALLOWED_DAYS_FOR_REVERSAL = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALLOWED_DAYS_FOR_REVERSAL)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ALLOWED_SERVICES_FOR_REVERSAL = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALLOWED_SERVICES_FOR_REVERSAL)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ALLOW_BULK_C2S_REVERSAL_MESSAGE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALLOW_BULK_C2S_REVERSAL_MESSAGE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ALLOWED_GATEWAY_FOR_BULK_REVERSAL = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALLOWED_GATEWAY_FOR_BULK_REVERSAL)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				O2C_BATCH_WITHDRAW_MESSAGE_ALLOWED = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.O2C_BATCH_WITHDRAW_MESSAGE_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ADMIN_MESSAGE_REQD = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ADMIN_MESSAGE_REQD)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IRIS_DATE_FORMAT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IRIS_DATE_FORMAT));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			 
			 
			try {
				SUBSRBR_MSG_STOP = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SUBSRBR_MSG_STOP));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			 
			try {
				OWNER_COMMISION_ALLOWED = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				ADD_COMM_SEPARATE_MSG = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ADD_COMM_SEPARATE_MSG)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			C2S_REQUEST_COMMON_MESSAGE = ((String) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.C2S_REQUEST_COMMON_MESSAGE)));
			 
			ALLOW_ROAM_ADDCOMM = (Boolean) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_RECHARGE);
			ROAM_INTERFACE_ID = (String) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.ROAM_INTERFACE_ID));
			USER_PRODUCT_MULTIPLE_WALLET = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)))
					.booleanValue();
			try {
				CGTAX34APP = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CGTAX34APP));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				INFO_FIELD_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.INFO_FIELD_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				COMMON_TRANSFER_ID_APPLICABLE = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.COMMON_TRANSFER_ID_APPLICABLE))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DUPLICATE_CARDGROUP_CODE_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DUPLICATE_CARDGROUP_CODE_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DEFAULT_CURRENCY = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEFAULT_CURRENCY)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				FNF_ZB_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.FNF_ZB_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LOW_BASED_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LOW_BASED_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				P2P_SERVICES_TYPE_SERVICECLASS = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.P2P_SERVICES_TYPE_SERVICECLASS)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				USR_MOV_ACROSS_DOM_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USR_MOV_ACROSS_DOM_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ORIGIN_ID_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ORIGIN_ID_ALLOW)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SID_ENCRYPTION_ALLOWED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				USR_BTCH_SUS_DEL_APRVL = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USR_BTCH_SUS_DEL_APRVL)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				AUTO_NWSTK_CRTN_ALWD = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTO_NWSTK_CRTN_ALWD)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				AUTO_NWSTK_CRTN_THRESHOLD = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTO_NWSTK_CRTN_THRESHOLD));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				TOKEN_EXPIRY_IN_MINTS = ((Long) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TOKEN_EXPIRY_IN_MINTS)))
						.longValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{
				O2C_DIRECT_TRANSFER= ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_DIRECT_TRANSFER)).booleanValue();
			}catch(Exception e){
				_log.errorTrace(methodName, e);
			}

			try{
				SEQUENCE_ID_RANGE= ((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_RANGE));
			}catch(Exception e){
				_log.errorTrace(methodName, e);
			}
			
			try{
				SEQUENCE_ID_ENABLE= ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE));
			}catch(Exception e){
				_log.errorTrace(methodName, e);
			}
			try{
				HASHING_ENABLE= ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE));
			}catch(Exception e){
				_log.errorTrace(methodName, e);
			}
			try{
				CHANNEL_SOS_ENABLE= ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE));
			}catch(Exception e){
				_log.errorTrace(methodName, e);
			}
			try{
				HASHING_ID_RANGE= ((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ID_RANGE));
			}catch(Exception e){
				_log.errorTrace(methodName, e);
			}
			try {
				AUTO_C2C_SOS_CAT_ALLOWED = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTO_C2C_SOS_CAT_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				SOS_SETTLEMENT_TYPE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SOS_SETTLEMENT_TYPE));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				CHANNEL_SOS_ALLOWED_WALLET = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				LR_ENABLED = ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				CHANNEL_AUTOC2C_ENABLE = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHANNEL_AUTOC2C_ENABLE));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			
			try {
				DOWNLOAD_CSV_REPORT_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DOWNLOAD_CSV_REPORT_REQUIRED)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ALLOW_TRANSACTION_IF_SOS_SETTLEMENT_FAIL = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALLOW_TRANSACTION_IF_SOS_SETTLEMENT_FAIL)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
				try{
			DECRYPT_KEY_VISIBLE= ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DECRYPT_KEY_VISIBLE));
		}catch(Exception e){
			_log.errorTrace(methodName, e);
		}
		try{
			THIRD_PARTY_VISIBLE= ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.THIRD_PARTY_VISIBLE));
		}catch(Exception e){
			_log.errorTrace(methodName, e);
		}
		try{
			VOMS_PROFILE_ACTIVATION_REQ= ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PROFILE_ACTIVATION_REQ));
		}catch(Exception e){
			_log.errorTrace(methodName, e);
		}
			try {
				TARGET_BASED_COMMISSION = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TARGET_BASED_COMMISSION)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				TARGET_BASED_COMMISSION_SLABS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TARGET_BASED_COMMISSION_SLABS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				TARGET_BASED_BASE_COMMISSION = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TARGET_BASED_BASE_COMMISSION)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				TARGET_BASED_BASE_COMMISSION_SLABS = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TARGET_BASED_BASE_COMMISSION_SLABS)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{
				OFFLINE_SETTLE_EXTUSR_BAL= ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OFFLINE_SETTLE_EXTUSR)).booleanValue();
			}catch(Exception e){
				_log.errorTrace(methodName, e);
			}
			try {
			SUBS_BLK_AFT_X_CONS_FAIL=((Integer)PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBS_BLK_AFT_X_CONS_FAIL)).intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SUBS_UNBLK_AFT_X_TIME=((Integer)PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBS_UNBLK_AFT_X_TIME)).intValue();
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
				}
			
			try {
			CHNL_USR_LAST_ACTIVE_TXN = ((Integer) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.CHNL_USR_LAST_ACTIVE_TXN))).intValue();
			} catch (Exception e) {
			_log.errorTrace(methodName, e);
			}
			
			try {
				REALTIME_OTF_MSGS = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				AUTO_VOUCHER_CRTN_ALWD = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.AUTO_VOUCHER_CRTN_ALWD)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{
                STK_MASTER_KEY= (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.STK_MASTER_KEY);
			}catch(Exception e){
                _log.errorTrace(methodName, e);
			}
			try {
				PIN_REQUIRED_P2P = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PIN_REQUIRED_P2P));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				VMS_D_STATUS_CHANGE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VMS_D_STATUS_CHANGE));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			
			try {
				VMS_E_STATUS_CHANGE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VMS_E_STATUS_CHANGE));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			
			try {
				VMS_P_STATUS_CHANGE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VMS_P_STATUS_CHANGE));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				VMS_D_STATUS_CHANGE_MAP = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VMS_D_STATUS_CHANGE_MAP));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			
			try {
				VMS_E_STATUS_CHANGE_MAP = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VMS_E_STATUS_CHANGE_MAP));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			
			try {
				VMS_P_STATUS_CHANGE_MAP = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VMS_P_STATUS_CHANGE_MAP));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			
			try {
				VOUCHER_PROFLE_IS_OPTIONAL = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOUCHER_PROFLE_IS_OPTIONAL)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
		} catch (Exception e) {
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.MAJOR,
					"SystemPreferences[" + methodName + "]", "", "", "",
					"Exception:" + e.getMessage());
			_log.errorTrace(methodName, e);
		}
			try{SYSTEM_ROLE_ALLOWED= (String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_ROLE_ALLOWED ));}catch(Exception e){_log.errorTrace(methodName, e);}
			
			try{ALLOWD_USR_TYP_CREATION= (String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION ));}catch(Exception e){_log.errorTrace(methodName, e);}
			try{ALIAS_TO_BE_ENCRYPTED=((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.ALIAS_TO_BE_ENCRYPTED))).booleanValue();}catch(Exception e){_log.errorTrace(methodName, e);}
			try{POSTPAID_SUBS_SERVICECLASS=((Integer)(PreferenceCache.getSystemPreferenceValue(PreferenceI.POST_SERVICE_CLASS))).intValue(); }catch(Exception e){_log.errorTrace(methodName, e);}
			
			try {
				PIN_VALIDATATION_IN_USSD = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PIN_VALIDATATION_IN_USSD));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try{LAST_X_TRF_DETAILS_NO=((Integer)(PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_TRF_DETAILS_NO))).intValue(); }catch(Exception e){_log.errorTrace(methodName, e);}
			try{LAST_X_TRF_DETAILS_DAYS=((Integer)(PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_TRF_DETAILS_DAYS))).intValue(); }catch(Exception e){_log.errorTrace(methodName, e);}
			try{
				DUALWALLET_C2CAUTO_CAL=(String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.DUALWALLET_C2CAUTO_CAL));
			}
			catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{BURN_RATE_THRESHOLD=((String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.BURN_RATE_THRESHOLD))); }catch(Exception e){_log.errorTrace(methodName, e);}
			try{VOUCHER_BURN_RATE_SMS_ALERT=((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_BURN_RATE_SMS_ALERT))).booleanValue(); }catch(Exception e){_log.errorTrace(methodName, e);}

			try{VOUCHER_BURN_RATE_EMAIL_ALERT=((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_BURN_RATE_EMAIL_ALERT))).booleanValue(); }catch(Exception e){_log.errorTrace(methodName, e);}
			try{OTH_COM_CHNL = ((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue();}catch (Exception e) {_log.errorTrace(methodName, e);}
			try{SUBSCRIBER_PREFIX_ROUTING_ALLOWED=(Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_PREFIX_ROUTING_ALLOWED));}catch(Exception e){_log.errorTrace("SystemPreferences[load]: ", e);}
			
		try {
			VPIN_INVALID_COUNT = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VPIN_INVALID_COUNT));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			VOMS_PIN_BLK_EXP_DRN = (Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_BLK_EXP_DRN));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			VOMS_DAMG_PIN_LNTH_ALLOW = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_DAMG_PIN_LNTH_ALLOW));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		
		try {
			DATE_FORMAT_CAL_JAVA = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			DATE_TIME_FORMAT = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_TIME_FORMAT));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			LOCALE_ENGLISH = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOCALE_ENGLISH));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			TIMEZONE_ID = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TIMEZONE_ID));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			CALENDAR_TYPE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			CALENDER_DATE_FORMAT = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDER_DATE_FORMAT));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			CALENDAR_SYSTEM = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_SYSTEM));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			FORMAT_MONTH_YEAR = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.FORMAT_MONTH_YEAR));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			EXTERNAL_CALENDAR_TYPE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CALENDAR_TYPE));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			IS_CAL_ICON_VISIBLE = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_CAL_ICON_VISIBLE));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			IS_MON_DATE_ON_UI = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MON_DATE_ON_UI));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		
		 
			try{USSD_RC_LANG_PARAM_REQ=((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_RC_LANG_PARAM_REQ))).booleanValue(); }catch(Exception e){_log.errorTrace(methodName, e);}
			
			try {
				MAX_HOST_TEXTBOX_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_HOST_TEXTBOX_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				COMMA_ALLOW_IN_LOGIN = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.COMMA_ALLOW_IN_LOGIN)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}			
			try {
				XML_DOC_ENCODING = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.XML_DOC_ENCODING)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				LAST_X_C2S_TXNSTATUS_ALLOWED = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LAST_X_C2S_TXNSTATUS_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				LAST_X_CHNL_TXNSTATUS_ALLOWED = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LAST_X_CHNL_TXNSTATUS_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				System.out.println(PreferenceI.USER_EXTERNAL_CODE_DOMAINWISE);
				USER_EXTERNAL_CODE_DOMAINWISE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USER_EXTERNAL_CODE_DOMAINWISE)))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			
			try {
				LOAD_BALANCER_IP_ALLOWED = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LOAD_BAL_IP_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				CHECK_LAST_TXN_FROM_USER_PHONES = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CHK_LAST_TXN_BY_USER_PHONES));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				WIRC_ACCOUNT_MSISDN_OPT = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.WIRC_ACCOUNT_MSISDN_OPT)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				ALLOWED_SERVICES_FOR_FAIL_WHEN_AMBIGUOUS = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALLOWED_SERVICES_FOR_FAIL_WHEN_AMBIGUOUS)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			try {
				SERVICES_ALLOWED_SHOW_CARDGROUPLIST = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SERVICES_ALLOWED_SHOW_CARDGROUPLIST));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try{
			VOUCHER_THIRDPARTY_STATUS=(String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_THIRDPARTY_STATUS));
		}
		catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		
		try {
				USER_VOUCHERTYPE_ALLOWED = (Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		
		try {
			USER_VOUCHERSEGMENT_ALLOWED = (Boolean) (PreferenceCache
				.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		
		try{
			CHNLUSR_VOUCHER_CATGRY_ALLWD=(Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNLUSR_VOUCHER_CATGRY_ALLWD));
		}
		catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			DOWNLD_BATCH_BY_BATCHID = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.DOWNLD_BATCH_BY_BATCHID)));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		//added by Ashish for VIL
		try {
			SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS)));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		
		
		try {
			ADD_INFO_REQUIRED_FOR_VOUCHER = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.ADD_INFO_REQUIRED_FOR_VOUCHER)));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		
		try {
			SMS_ALERT_FORVOMS_ORDER_INITIATOR = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.SMS_ALERT_FORVOMS_ORDER_INITIATOR)));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			NET_PREFIX_TO_VALIDATED_FOR_BAR_UNBAR = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.NET_PREFIX_TO_VALIDATED_FOR_BAR_UNBAR)));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		try {
			EMAIL_ALERT_FORVOMS_ORDER_INITIATOR = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.EMAIL_ALERT_FORVOMS_ORDER_INITIATOR)));
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		//ended by Ashish for VIL
		try {
			P2P_DEBITCREDIT_COMMON = (Boolean) (PreferenceCache
				.getSystemPreferenceValue(PreferenceI.P2P_DEBITCREDIT_COMMON));
	} catch (Exception e) {
		_log.errorTrace(methodName, e);
	}
	
		try {
			P2P_PRE_SERVCLASS_AS_POST = (Boolean) (PreferenceCache
				.getSystemPreferenceValue(PreferenceI.P2P_PRE_SERVCLASS_AS_POST));
	} catch (Exception e) {
		_log.errorTrace(methodName, e);
	}
		try {
				OTP_ALLOWED_GATEWAY = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OTP_ALLOWED_GATEWAY)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		
		try 
		{
			VOMS_PROF_TALKTIME_MANDATORY = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.VOMS_ADD_PROFILE_TALK_TIME_MANDATORY))); 
		} catch (Exception e) 
		{
			_log.errorTrace(methodName, e);
		}	
		try 
		{
			VOMS_PROF_VALIDITY_MANDATORY = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.VOMS_ADD_PROFILE_VALIDITY_MANDATORY)));
		} catch (Exception e) 
		{
			_log.errorTrace(methodName, e);
		}
		
		try 
		{
			VOMS_PROFILE_DEF_MINMAXQTY = ((Boolean) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.VOMS_PROFILE_DEF_MINMAXQTY)));
		} catch (Exception e) 
		{
			_log.errorTrace(methodName, e);
		}
		try 
		{
			VOMS_PROFILE_MIN_REORDERQTY = ((Integer) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.VOMS_PROFILE_MIN_REORDERQTY)));
		} catch (Exception e) 
		{
			_log.errorTrace(methodName, e);
		}
		try 
		{
			VOMS_PROFILE_MAX_REORDERQTY = ((Integer) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.VOMS_PROFILE_MAX_REORDERQTY)));
		} catch (Exception e) 
		{
			_log.errorTrace(methodName, e);
		}
		
		
		
		
		
		
			try{MOBILE_APP_VERSION=((Double)PreferenceCache.getSystemPreferenceValue(PreferenceI.MOBILE_APP_VERSION)).doubleValue();}catch(Exception e){_log.errorTrace(methodName, e);}
			try{GAT_CODE_FOR_BARRED_USERS=(String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.GAT_CODE_FOR_BARRED_USERS));}catch(Exception e){_log.errorTrace(methodName, e);}
			try{ VAS_PRODUCT_GROUPING_REQ_MOBILEAPP =(Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VAS_PRODUCT_GROUPING_REQ_MOBILEAPP));} catch (Exception e) {_log.errorTrace(methodName, e);}
			try{ USSD_REC_MSG_SEND_ALLOW=((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_REC_MSG_SEND_ALLOW))).booleanValue();} catch (Exception e) {_log.errorTrace(methodName, e);}
			try{ LAST_C2C_ENQ_MSG_REQ=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_C2C_ENQ_MSG_REQ)).booleanValue();} catch (Exception e) {_log.errorTrace(methodName, e);}
			try{MAPP_PRODUCT_GROUPING_REQ_SRV=(String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.MAPP_PRODUCT_GROUPING_REQ_SRV));}catch(Exception e){_log.errorTrace(methodName, e);}
			try{ PG_INTEFRATION_ALLOWED =(Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PG_INTEFRATION_ALLOWED));} catch (Exception e) {_log.errorTrace(methodName, e);}
			
			try{ C2S_SEQID_ALWD=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SEQID_ALWD)).booleanValue();} catch (Exception e) {_log.errorTrace(methodName, e);}
			try{ C2S_SEQID_FOR_GWC= ((String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SEQID_FOR_GWC)));} catch (Exception e) {_log.errorTrace(methodName, e);}
			try{ C2S_SEQID_APPL_SER=((String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SEQID_APPL_SER)));} catch (Exception e) {_log.errorTrace(methodName, e);}
			try{ADDITIONAL_IN_FIELDS_ALLOWED=(String)PreferenceCache.getSystemPreferenceValue(PreferenceI.ADDITIONAL_IN_FIELDS_ALLOWED);}catch(Exception e){_log.trace("load", e);}

			try{VOMS_MIN_EXPIRY_DAYS=((Integer)(PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MIN_EXPIRY_DAYS))).intValue(); }catch(Exception e){_log.errorTrace(methodName, e);}
			
			try{ TRANSACTION_TYPE_ALWD=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD)).booleanValue();} catch (Exception e) {_log.errorTrace(methodName, e);}
			try{ PAYMENT_MODE_ALWD=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD)).booleanValue();} catch (Exception e) {_log.errorTrace(methodName, e);}
			try {
				O2CAMB_MINUTES_DELAY = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.O2CAMB_MINUTES_DELAY)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{VMS_ALLOW_CONTENT_TYPE=(String)PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_ALLOW_CONTENT_TYPE);}catch(Exception e){_log.trace("load", e);}
			try {
				EMAIL_DEFAULT_LOCALE = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.EMAIL_DEFAULT_LOCALE)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2C_ALLOW_CONTENT_TYPE = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2C_ALLOW_CONTENT_TYPE)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{ PAYMENT_VERIFICATION_ALLOWED=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_VERIFICATION_ALLOWED)).booleanValue();} catch (Exception e) {_log.errorTrace(methodName, e);}
			try{DOMAINCODE_FOR_SOS_YABX=(String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.DOMAINCODE_FOR_SOS_YABX));}catch(Exception e){_log.errorTrace(methodName, e);}
			try{HANDLER_CLASS_FOR_YABX=(String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.HANDLER_CLASS_FOR_YABX));}catch(Exception e){_log.errorTrace(methodName, e);}
			try{SOS_ALLOWED_FOR_YABX=(Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_ALLOWED_FOR_YABX));}catch(Exception e){_log.errorTrace(methodName, e);}
			try{IPV6_ENABLED=(Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.IPV6_ENABLED));}catch(Exception e){_log.errorTrace(methodName, e);}
			try{VMSPIN_EN_DE_CRYPTION_TYPE=(String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.VMSPIN_EN_DE_CRYPTION_TYPE));}catch(Exception e){_log.errorTrace(methodName, e);}
			try {
				NATIONAL_VOUCHER_ENABLE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NATIONAL_VOUCHER_ENABLE)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				NATIONAL_VOUCHER_NETWORK_CODE = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NATIONAL_VOUCHER_NETWORK_CODE)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ONLINE_BATCH_EXP_DATE_LIMIT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ONLINE_BATCH_EXP_DATE_LIMIT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_VOUCHER_EXPIRY_EXTN_LIMIT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_VOUCHER_EXPIRY_EXTN_LIMIT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN = ((Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN = ((Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN = ((Integer) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {FILE_WRITER_CLASS = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.FILE_WRITER_CLASS)));} catch (Exception e) {_log.errorTrace(methodName, e);}
			try {IS_PGP_APPL = ((boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PGP_APPL)));} catch (Exception e) {_log.errorTrace(methodName, e);}
			
			try {
				NW_NATIONAL_PREFIX = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NW_NATIONAL_PREFIX));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				NW_CODE_NW_PREFIX_MAPPING = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.NW_CODE_NW_PREFIX_MAPPING));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				VOMS_NATIONAL_LOCAL_PREFIX_ENABLE = (boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOMS_NATIONAL_LOCAL_PREFIX_ENABLE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{
				USSD_RESP_SEPARATOR=(String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.USSD_RESP_SEPARATOR));
			}catch(Exception e){
				_log.errorTrace(methodName, e);
			}
			try {
				DW_COMMISSION_CAL = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DW_COMMISSION_CAL)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DW_ALLOWED_GATEWAYS = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DW_ALLOWED_GATEWAYS)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IS_VOU_DEN_PROFILE_ZERO_ALLOW = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_VOU_DEN_PROFILE_ZERO_ALLOW)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				IS_VOU_DEN_PROFILE_ZERO_ALLOW = false;
			}
			try {
				VMS_SERVICES = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VMS_SERVICES));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				ONLINE_VOUCHER_GEN_LIMIT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ONLINE_VOUCHER_GEN_LIMIT)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				VOUCHER_GEN_EMAIL_NOTIFICATION = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOUCHER_GEN_EMAIL_NOTIFICATION)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				VOUCHER_GEN_SMS_NOTIFICATION = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOUCHER_GEN_SMS_NOTIFICATION)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SUBSCRIBER_VOUCHER_PIN_REQUIRED = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_VOUCHER_PIN_REQUIRED)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try 
			{
				ONLINE_DVD_LIMIT = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ONLINE_DVD_LIMIT)));
			} catch (Exception e) 
			{
				_log.errorTrace(methodName, e);
			}
			
			try 
			{
				ONLINE_VOUCHER_GEN_LIMIT_SYSTEM = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ONLINE_VOUCHER_GEN_LIMIT_SYSTEM)));
			} catch (Exception e) 
			{
				_log.errorTrace(methodName, e);
			}
			
			try 
			{
				ONLINE_VOUCHER_GEN_LIMIT_NW = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ONLINE_VOUCHER_GEN_LIMIT_NW)));
			} catch (Exception e) 
			{
				_log.errorTrace(methodName, e);
			}
			
			/*try 
			{
				PHYSICAL_VOUCH_STATUS_ONLY = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PHYSICAL_VOUCH_STATUS_ONLY)));
			} catch (Exception e) 
			{
				_log.errorTrace(methodName, e);
			}*/
			
			try {
				SCREEN_WISE_ALLOWED_VOUCHER_TYPE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
		}
		
			try {
				VMS_D_LIFECYCLE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VMS_D_LIFECYCLE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VMS_P_LIFECYCLE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VMS_P_LIFECYCLE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				VMS_E_LIFECYCLE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VMS_E_LIFECYCLE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				ONLINE_CHANGE_STATUS_SYSTEM_LMT = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ONLINE_CHANGE_STATUS_SYSTEM_LMT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				
				ONLINE_CHANGE_STATUS_NETWORK_LMT = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ONLINE_CHANGE_STATUS_NETWORK_LMT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			 
			
			try {
				DVD_BATCH_FILEEXT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DVD_BATCH_FILEEXT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			//Added for voms_hcpt by niharika
			try {
				VOUCHER_PROFILE_OTHER_INFO = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.VOUCHER_PROFILE_OTHER_INFO)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				DVD_ORDER_BY_PARAMETERS = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DVD_ORDER_BY_PARAMETERS)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DVD_BATCH_FILEEXT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DVD_BATCH_FILEEXT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				MAX_APPROVAL_LEVEL_C2C = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_APPROVAL_LEVEL_C2C));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2C_ALLOWED_VOUCHER_LIST = (String) (PreferenceCache
						.getSystemPreferenceValue(CorePreferenceI.C2C_ALLOWED_VOUCHER_LIST));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				IS_VOU_BUN_NAME_LEN_ZERO_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_VOU_BUN_NAME_LEN_ZERO_ALLOW)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				IS_VOU_BUN_NAME_LEN_ZERO_ALLOW = false;
			}
			try {
				IS_BUN_PRE_ID_NULL_ALLOW = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_BUN_PRE_ID_NULL_ALLOW)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				IS_BUN_PRE_ID_NULL_ALLOW = false;
			}
			try {
				IS_BLANK_VOUCHER_REQ = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.IS_BLANK_VOUCHER_REQ)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				IS_BLANK_VOUCHER_REQ = false;
			}
			try {
				C2C_EMAIL_NOTIFICATION = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2C_EMAIL_NOTIFICATION)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2C_SMS_NOTIFICATION = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2C_SMS_NOTIFICATION)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				C2CVCRPT_DATEDIFF = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2CVCRPT_DATEDIFF)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				O2CVCRPT_DATEDIFF = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.O2CVCRPT_DATEDIFF)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{
				ALPHANUM_SPCL_REGEX = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ALPHANUM_SPCL_REGEX)));
			} catch(Exception e) {
				_log.errorTrace(methodName,e);
			}
			
			try {
				CARD_GROUP_ALLOWED_CATEGORIES = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CARD_GROUP_ALLOWED_CATEGORIES));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			
			try {
				TRANSFER_RULE_ALLOWED_CATEGORIES = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TRANSFER_RULE_ALLOWED_CATEGORIES));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				MIN_LAST_DAYS_CG = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MIN_LAST_DAYS_CG)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_LAST_DAYS_CG = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_LAST_DAYS_CG)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				REPORT_MAX_DATEDIFF_ADMIN_CONS = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REPORT_MAX_DATEDIFF_ADMIN_CONS));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				REPORT_MAX_DATEDIFF_USER_CONS = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REPORT_MAX_DATEDIFF_USER_CONS));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				REPORT_MAX_DATEDIFF_USER_AVAIL = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REPORT_MAX_DATEDIFF_USER_AVAIL));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				REPORT_MAX_DATEDIFF_ADMIN_AVAIL = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REPORT_MAX_DATEDIFF_ADMIN_AVAIL));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				REPORT_MAX_DATEDIFF_ADMIN_NLEVEL = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REPORT_MAX_DATEDIFF_ADMIN_NLEVEL));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				USER_ALLOWED_VINFO = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.USER_ALLOWED_VINFO));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			try {
				RECENT_C2C_TXN = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RECENT_C2C_TXN));
			} catch (Exception e) {
				_log.errorTrace("load", e);
			}
			//Added for MRP Successive block timeout for channel transaction
			try{
				SUCCESS_REQUEST_BLOCK_SEC_CODE_O2C = ((Long)(PreferenceCache.getSystemPreferenceValue(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE_O2C)));
			}
			catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{
				SUCCESS_REQUEST_BLOCK_SEC_CODE_C2C = ((Long)(PreferenceCache.getSystemPreferenceValue(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE_C2C)));
			}
			catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				TWO_FA_REQ_FOR_PIN = ((Boolean) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.TWO_FA_REQ_FOR_PIN))
						.booleanValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				OTP_RESEND_TIMES = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OTP_RESEND_TIMES));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				OTP_RESEND_DURATION = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OTP_RESEND_DURATION));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				OTP_VALIDITY_PERIOD = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OTP_VALIDITY_PERIOD));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				MAX_INVALID_OTP = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.MAX_INVALID_OTP));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				BLOCK_TIME_INVALID_OTP = (Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.BLOCK_TIME_INVALID_OTP));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				OAUTH_TOKEN_TIME_TO_LIVE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OAUTH_TOKEN_TIME_TO_LIVE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			//Added for SMS Sender Name based on serviceType
			try {
				SMS_SENDER_NAME_FOR_SERVICE_TYPE = ((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_SENDER_NAME_FOR_SERVICE_TYPE)));		
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{ 
		
				BYPASS_EVD_KANNEL_MESSAGE_STATUS = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.BYPASS_EVD_KANNEL_MESSAGE_STATUS))).booleanValue();
			}
			catch(Exception e)
			{
				_log.errorTrace(methodName, e);
			}
			try{ 
		
				LANGS_SUPT_ENCODING = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LANGS_SUPT_ENCODING));
			}
			catch(Exception e)
			{
				_log.errorTrace(methodName, e);
			}
			try{ 
		
				IS_ONE_TIME_SID=((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_ONE_TIME_SID))).booleanValue();
			}
			catch(Exception e)
			{
				_log.errorTrace(methodName, e);
			}
		
			try{ 
				
				IS_EMAIL_ALLOWED_AUTO_NTWKSTK=((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_EMAIL_ALLOWED_AUTO_NTWKSTK))).booleanValue();
			}
			catch(Exception e)
			{
				_log.errorTrace(methodName, e);
			}
			try {
				ALERT_ALLOWED_USER = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALERT_ALLOWED_USER));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				C2C_BATCH_FILEEXT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				ERROR_FILE_C2C = ((int) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ERROR_FILE_C2C)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				UNLOCK_ZERO_MIGRATIONS = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.UNLOCK_ZERO_MIGRATIONS)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				COM_PAY_OUT = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			
			try {
				ERP_VOU_WH = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ERP_VOU_WH)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				SYSTEM_DATETIME_FORMAT = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				REPORT_OFFLINE = (Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.REPORT_OFFLINE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				OFFLINERPT_DOWNLD_PATH = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.OFFLINERPT_DOWNLD_PATH));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				DEF_CHNL_TRANSFER_ALLOWED = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEF_CHNL_TRANSFER_ALLOWED));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			
			try {
				LOAN_PROFILE_SLAB_LENGTH = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LOAN_PROFILE_SLAB_LENGTH)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{
				USERWISE_LOAN_ENABLE=(Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE));
				}
			catch(Exception e){
				_log.errorTrace(methodName, e);
				}

			try {
				ALLOW_TRANSACTION_IF_LOAN_SETTLEMENT_FAIL = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.ALLOW_TRANSACTION_IF_LOAN_SETTLEMENT_FAIL)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			try {
				BLOCK_GATEWAYCODE_FOR_LOAN_SETTLEMENT = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.BLOCK_GATEWAYCODE_FOR_LOAN_SETTLEMENT)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			
			try {
				RESTRICT_TRANSACTION_FOR_LOAN_SETTLEMENT = ((String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.RESTRICT_TRANSACTION_FOR_LOAN_SETTLEMENT)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			
			try {
				CAT_USERWISE_LOAN_ENABLE = ((Boolean) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.CAT_USERWISE_LOAN_ENABLE)));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try{
				TEMP_PIN_EXPIRY_DURATION= ((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.TEMP_PIN_EXPIRY_DURATION));
			}catch(Exception e){
				_log.errorTrace(methodName, e);
			}

			try{
				IMEI_OPTIONAL= ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IMEI_OPTIONAL)).booleanValue();
			}catch(Exception e){
				_log.errorTrace(methodName, e);
			}
			
			
			try{
				EXTREFNUM_MANDATORY_GATEWAYS= ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTREFNUM_MANDATORY_GATEWAYS));
			}catch(Exception e){
				_log.errorTrace(methodName, e);
			}
			
			try{CATEGORIES_LIFECYCLECHANGE=(String)(PreferenceCache.getSystemPreferenceValue(PreferenceI.CATEGORIES_LIFECYCLECHANGE));
			}catch(Exception e){
				_log.errorTrace(methodName, e);
			}
			try {
				LAST_N_DAYS_EVD_TRF = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.LAST_N_DAYS_EVD_TRF)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try{
				IS_RECHARGE_REQUEST=(Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_RECHARGE_REQUEST));
				}
			catch(Exception e){
				_log.errorTrace(methodName, e);
				}
			try {
				DAYS_FOR_SENDING_MESSAGE = ((Integer) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DAYS_FOR_SENDING_MESSAGE)))
						.intValue();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				SMS_PIN_BYPASS_GATEWAY_CODE = (String) (PreferenceCache
						.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_CODE));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
			
	}

	public static void reload() {
		load();
	}

	

	
	public static void main(String[] args) {
		 

	}

}
