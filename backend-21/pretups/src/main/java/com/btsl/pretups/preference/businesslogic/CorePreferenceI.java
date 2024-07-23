package com.btsl.pretups.preference.businesslogic;

public interface CorePreferenceI {

    public String DEFAULT_LANGUAGE = "DEFAULT_LANGUAGE"; 
    public String DEFAULT_COUNTRY = "DEFAULT_COUNTRY"; 
    public String SYSTEM_LEVEL = "SYSTEMPRF"; 
    public String NETWORK_LEVEL = "NETWORKPRF"; 
    public String ZONE_LEVEL = "ZONEPRF"; 
    public String SERVICE_CLASS_LEVEL = "SVCCLSPRF";  
                                                      
    public String SERVICE_TYPE_LEVEL = "SERTYPPREF";
    public String AMOUNT_MULT_FACTOR = "AMOUNT_MULT_FACTOR"; 
                                                             
                                                             
    public String MIN_MSISDN_LENGTH_CODE = "MIN_MSISDN_LENGTH";
    public String MAX_MSISDN_LENGTH_CODE = "MAX_MSISDN_LENGTH";
    public String MSISDN_LENGTH_CODE = "MSISDN_LENGTH";
    public String MSISDN_PREFIX_LENGTH_CODE = "MSISDN_PREFIX_LENGTH";
    public String  MAX_BULK_FILE_SIZE_BYTES = "MAX_BULK_FILE_SIZE_BYTES";
    public String MSISDN_PREFIX_LIST_CODE = "MSISDN_PREFIX_LIST";
    public String SERVICECLASS_CODE = "SVCCLSPRF"; 
                                                   
    public String SKEY_EXPIRY_TIME_CODE = "SKEYEXPIRYSEC"; 
                                                           
    public String SKEY_LENGTH_CODE = "SKEYLENGTH"; 
    public String P2P_MAX_PTAGE_TRANSFER_CODE = "MAX_PER_TRANSFER"; 
    public String DAILY_MAX_TRFR_AMOUNT_CODE = "DAY_SDR_MX_TRANS_AMT"; 
    public String DAILY_MAX_TRFR_NUM_CODE = "DAY_SDR_MX_TRANS_NUM"; 
    public String MONTHLY_MAX_TRFR_AMOUNT_CODE = "MON_SDR_MX_TRANS_AMT"; 
    public String MONTHLY_MAX_TRFR_NUM_CODE = "MON_SDR_MX_TRANS_NUM"; 
    public String WEEKLY_MAX_TRFR_AMOUNT_CODE = "WK_SDR_MX_TRANS_AMT"; 
    public String WEEKLY_MAX_TRFR_NUM_CODE = "WK_SDR_MX_TRANS_NUM"; 
    public String MIN_RESIDUAL_BAL_TYPE_CODE = "MIN_RES_BALTYPE"; 
    public String MIN_RESIDUAL_BAL_CODE = "MIN_RESIDUAL_BAL"; 
    public String MIN_VALIDITY_DAYS_CODE = "MIN_VALIDITY_DAYS"; 
    public String C2S_MAX_PIN_BLOCK_COUNT_CODE = "C2S_MAX_PIN_BLK_CONT"; 
    public String P2P_MAX_PIN_BLOCK_COUNT = "P2P_MAX_PIN_BLK_CONT";
    public String SUCCESS_REQUEST_BLOCK_SEC_CODE = "SUCC_BLOCK_TIME";  
    public String DAILY_CNSTIVE_FAIL_COUNT_BEFOREBAR = "DA_CONFAIL_COUNT"; 
    public String DAILY_SUCCESS_TXN_ALLOWED_COUNT = "DA_SUCTRAN_ALLWDCOUN"; 
    public String DAILY_FAIL_TXN_ALLOWED_COUNT = "DA_FAIL_TXN_ALLWDCOU"; 
    public String DAILY_TOTAL_TXN_AMT_ALLOWED = "DA_TOTXN_AMT_ALLWDCO"; 
    public String TAX2_ON_TAX1_CODE = "TAX2_ON_TAX1"; 
    public String SUBSCRIBER_FAIL_CTINCR_CODES = "ERROR_FOR_FAIL_CT"; 
    public String C2S_PIN_BLK_RST_DURATION = "C2S_PIN_BLK_RST_DRTN"; 
    public String P2P_PIN_BLK_RST_DURATION = "P2P_PIN_BLK_RST_DRTN"; 
    public String C2S_DAYS_AFTER_CHANGE_PIN = "C2S_DYS_ATR_CNGE_PIN"; 
    public String P2P_DAYS_AFTER_CHANGE_PIN = "P2P_DYS_ATR_CNGE_PIN"; 
    public String TYPE_INTEGER = "INT";
    public String TYPE_LONG = "NUMBER";
    public String TYPE_AMOUNT = "AMOUNT";
    public String TYPE_BOOLEAN = "BOOLEAN";
    public String TYPE_STRING = "STRING";
    public String TYPE_DOUBLE = "DOUBLE";
    public String TYPE_DATE = "DATE";
	public String TYPE_DECIMAL="DECIMAL";
    public String PIN_REQUIRED_CODE = "PIN_REQUIRED";
    public String PIN_LENGTH_CODE = "PIN_LENGTH";
    public String C2S_PIN_LENGTH_CODE = "C2S_PIN_LENGTH";
    public String SKEY_REQUIRED = "SKEY_REQUIRED";
    public String SKEY_DEFAULT_SENT_TO = "SKEY_DEFAULT_SENT_TO";
    public String SYSTEM_DATE_FORMAT = "SYSTEM_DATE_FORMAT";
    public String SYSTEM_DATETIME_FORMAT = "SYSTEM_DTTIME_FORMAT";
    public String PERIOD_DAYS_NUM_CODE = "5";
    public String USE_PPAID_USER_DEFINED_CONTROLS = "USE_PPAID_CONTROLS";  
    public String LANGAUGES_SUPPORTED = "LANGAUGES_SUPPORTED";  
    public String C2S_USER_REGISTRATION_REQUIRED = "C2S_USER_REGTN_REQ"; 
    public String C2S_ALLOW_SELF_TOPUP = "ALLOW_SELF_TOPUP"; 
    public String USE_C2S_SEPARATE_TRNSFR_COUNTS = "C2S_SEP_TRFR_COUNT"; 
    public String DEFAULT_MESSGATEWAY = "DEFAULT_MESSGATEWAY";
    public String OPERATOR_UTIL_CLASS = "OPERATOR_UTIL_C";
    public String NETWORK_STOCK_FIRSTAPPLIMIT = "FRSTAPPLM";
    public String NETWORK_STOCK_CIRCLE_MAXLIMIT = "CIRCLEMAXLMT";
    public String MAX_MSISDN_TEXTBOX_LENGTH = "MAX_MSISDN_TEXTBOX";
    public String C2S_SNDR_CREDIT_BK_AMB_STATUS = "C2S_AMB_CR_ALLOWED";
    public String P2P_SNDR_CREDIT_BACK_ALLOWED = "P2P_CR_BACK_ALLOWED";
    public String P2P_SNDR_CREDIT_BK_AMB_STATUS = "P2P_AMB_CR_ALLOWED";
    public String PASSWORD_BLK_RST_DURATION = "PWD_BLK_RST_DURATION"; 
    public String MAX_PASSWORD_BLOCK_COUNT = "MAX_PWD_BLOCK_COUNT"; 
    public String TRSFR_DEF_SRVCTYPE = "TRSFR_DEF_SRVCTYPE"; 
    public String USER_HIERARCHY_SIZE = "USER_HIERARCHY_SIZE";  
    public String DEFAULT_PRODUCT = "DEFAULT_PRODUCT";  
    public String C2S_PIN_MIN_LENGTH = "C2S_MIN_PIN_LENGTH";
    public String C2S_PIN_MAX_LENGTH = "C2S_MAX_PIN_LENGTH";
    public String MAX_LOGIN_PWD_LENGTH = "MAX_LOGIN_PWD_LENGTH";
    public String MIN_LOGIN_PWD_LENGTH = "MIN_LOGIN_PWD_LENGTH";
    public String MAX_SMS_PIN_LENGTH = "MAX_SMS_PIN_LENGTH";
    public String MIN_SMS_PIN_LENGTH = "MIN_SMS_PIN_LENGTH";
    public String C2S_DEFAULT_SMSPIN = "C2S_DEFAULT_SMSPIN";
    public String C2S_DEFAULT_PASSWORD = "C2S_DEFAULT_PASSWORD";
    public String CRYSTAL_REPORT_MAX_DATEDIFF = "CREPT_MAX_DATEDIFF";
    public String REPORT_MAX_DATEDIFF = "REPORT_MAX_DATEDIFF";
    public String MAX_DATEDIFF = "MAX_DATEDIFF";
    public String P2P_DEFAULT_SMSPIN = "P2P_DEFAULT_SMSPIN";
    public String P2P_MINTRNSFR_AMOUNT = "PEERTRFMINLMT";
    public String P2P_MAXTRNSFR_AMOUNT = "PEERTRFMAXLMT";
    public String PIN_PASSWORD_ALERT_DAYS = "PIN_PWD_ALERT_DYS";
    public String DAYS_AFTER_CHANGE_PASSWORD = "DYS_AFTER_CHANGE_PWD";
    public String CHANGE_PASSWORD_NOT_REQUIRED_CATEGORY = "PWD_CHANGE_NOT_REQ";
    public String REQ_CUSER_DELETION_APPROVAL = "REQ_CUSER_DLT_APP";
    public String REQ_CUSER_SUSPENSION_APPROVAL = "REQ_CUSER_SUS_APP";
    public String CHNL_PLAIN_SMS_SEPARATOR = "CHNL_PLAIN_SMS_SEPT";
    public String C2S_MINTRNSFR_AMOUNT = "MINTRNSFR";
    public String C2S_MAXTRNSFR_AMOUNT = "MAXTRNSFR";
    public String P2P_PLAIN_SMS_SEPARATOR = "P2P_PLAIN_SMS_SEPT";
    public String SMS_TO_LOGIN_USER = "SMS_TO_LOGIN_USER";
    public String P2P_SUCCESS_REQUEST_BLOCK_SEC_CODE = "SCC_BLCK_TIME_P2P";
    public String P2P_DAILY_TOTAL_TXN_AMT_ALLOWED = "DA_REC_AMT_ALLWD_P2P";
    public String P2P_DAILY_SUCCESS_TXN_ALLOWED_COUNT = "DA_SUCTRAN_ALLWD_P2P";
    public String GROUP_ROLE_ALLOWED = "GROUP_ROLE_ALLOWED";
    public String DEF_FRCXML_SEL_P2P = "DEF_FRCXML_SEL_P2P";
    public String DEF_FRCXML_SEL_C2S = "DEF_FRCXML_SEL_C2S";
    public String EXTERNAL_TXN_UNIQUE = "EXTERNAL_TXN_UNIQUE";
    public String FINANCIAL_YEAR_START_INDEX = "FINANCIAL_YEAR_START";
    public String EXTERNAL_TXN_NUMERIC = "EXTERNAL_TXN_NUMERIC";
    public String EXTERNAL_TXN_MANDATORY_DOMAINTYPE = "EXTTXNMANDT_DOMAINTP";
    public String EXTERNAL_TXN_MANDATORY_FORFOC = "EXTTXNMANDT_FOC";
    public String NOTIFICATION_SERVICECLASSWISE_SEN = "NOTIFI_SRVCCLS_SEN";
    public String NOTIFICATION_SERVICECLASSWISE_REC = "NOTIFI_SRVCCLS_REC";
    public String NOTIFICATION_SERVICECLASSWISE_REC_C2S = "NOTIF_SRVCCLS_RECC2S";
    public String CHK_BLK_LST_STAT = "CHK_BLK_LST_STAT";
    public String SAP_ALLOWED = "SAP_ALLOWED";
    public String VOMS_MIN_ALT_VALUE = "VOMS_MIN_ALT_VALUE"; 
    public String VOMS_ORDER_SLAB_LENGTH = "VOMS_ORDER_SLAB_LENGTH"; 
    public String NOTIFICATION_SERVICECLASSWISE_REC_BILLPAY = "NOTIFSRVCLS_REC_BLPY";  
    public String C2S_ALLOW_SELF_BILLPAY = "ALLOW_SELF_BILLPAY"; 
    public String C2S_SNDR_CREDIT_BK_AMB_STATUS_BILLPAY = "C2S_AMB_CR_ALOW_PPBP"; 
    public String SEP_OUTSIDE_TXN_CTRL = "SEP_OUTSIDE_TXN_CTRL";
    public String DEF_FRCXML_SEL_BILLPAY = "DEF_FRCXML_SEL_BLPY";
    public String POSTPAID_REGISTER_AS_ACTIVATED_STATUS = "PP_DEF_STATUS_ACT";
    public String SPACE_ALLOW_IN_LOGIN = "SPACE_ALLOW_IN_LOGIN";
    public String DEFAULT_WEB_GATEWAY_CODE = "DEF_WEB_GW_CODE";  
    public String SECOND_LANGUAGE_ENCODING = "SECOND_LANG_ENCODING";  
    public String ICCID_CHECKSTRING = "ICCID_CHECKSTRING";  
    public String GRPT_CTRL_ALLOWED = "GRPT_CTRL_ALLOWED";
    public String GRPT_CHRG_ALLOWED = "GRPT_CHRG_ALLOWED";
    public String GRPT_CONTROL_LEVEL = "GRPT_CONTROL_LEVEL";
    public String P2P_ALLOW_SELF_TOPUP = "P2P_ALLOW_SELF_TOPUP"; 
    public String EXTERNAL_CODE_MANDATORY_FORFOC = "EXT_CODE_MAND_FOC";
    public String SECOND_LANGUAGE_CHARSET = "SECOND_LANG_CHARSET";  
    public String C2C_RET_PARENT_ONLY = "C2C_RET_PARENT_ONLY";
    public String MAX_NO_OF_BUDDIES_ALLOWED = "MAX_BUDDIES_ALLOWED";
    public String DECIMAL_ALLOW_SERVICES = "DECIML_ALOW_SERVICES";
    public String EXTERNAL_DATE_FORMAT = "EXTERNAL_DATE_FORMAT";
    public String XML_MAX_RCD_SUM_RESP = "XML_MAX_RCD_SUM_RESP";
    public String XML_DATE_RANGE = "XML_DATE_RANGE";
    public String XML_DFT_DATE_RANGE = "XML_DFT_DATE_RANGE";
    public String C2S_ALLOW_SELF_UB = "C2S_ALOW_SLF_UTLTBIL";
    public String C2S_SNDR_CRDT_BK_AMB_UB = "C2S_S_CRBKAM_UTLTBIL";
    public String NOTIFICATION_SERVICECLASSWISE_REC_UB = "NOTIFY_SRVCCLS_REC";
    public String MIN_IDENTIFICATION_NUMBER_LENGTH = "MIN_ID_NUM_LNTH";
    public String MAX_IDENTIFICATION_NUMBER_LENGTH = "MAX_ID_NUM_LNTH";
    public String USE_HOME_STOCK = "USE_HOME_STOCK";
    public String OTHERID_PREFIX_LIST = "OTH_ID_PREFIX_LIST";
    public String IDENTIFICATION_NUMBER_VAL_TYPE = "ID_NUM_VAL_TYPE";
    public String ALPHA_ID_NUM_ALLOWED = "ALPHANUM_ID_NUM_ALWD";
    public String RESPONSE_IN_DISPLAY_AMT = "USE_DISPLAY_AMT";
    public String MAX_DENOMINATION_VAL = "MAX_DENOMINATION_VAL";  
    public String VOMS_UPEXPHOURS = "VOMS_UPEXPHOURS";  
    public String VOMS_OFFPEAKHRS = "VOMS_OFFPEAKHRS";  
    public String VOMS_MAX_ERROR_COUNTEN = "VOMS_MAXERRORCOUNTEN"; 
    public String VOMS_MAX_ERROR_COUNTOTH = "VOMS_MAXERRORCOUNTOT";  
    public String VOMS_MAX_TOTAL_VOUCHER_EN = "VOMS_MAX_VOUCHER_EN";  
    public String VOMS_MAX_TOTAL_VOUCHER_OT = "VOMS_MAX_VOUCHER_OT";  
    public String VOMS_MAX_BATCH_DAY = "VOMS_MAXBATCHDY";  
    public String VOMS_DATE_FORMAT = "VOMS_DATE_FORMAT";
    public String SELF_VOUCHER_DISTRIBUTION_ALLOWED = "SLF_EVD_ALWD";
    public String PIN_SEND_TO = "PIN_SEND_TO";
    public String DELIVERY_RECEIPT_TRACKED = "DLRY_RCPT_TRK";
    public String CREDIT_BACK_ALWD_EVD_AMB = "CR_BK_ALW_EVD_AMB";
    public String NOTIFICATION_SERVICECLASSWISE_REC_EVD = "SR_WISE_MSG_EVD";
     
    public String BUDDY_PIN_REQUIRED = "BUDDY_PIN_REQ";
     
    public String FOC_SMS_NOTIFY = "FOC_SMS_NOTIFY";
     
    public String PROFILEASSOCIATE_AGENT_PREFERENCES = "PRF_ASSOCIATE_AGENT";
     
     
     
     
    public String VOMS_SERIAL_NO_MIN_LENGTH = "VOMS_SNO_MIN_LENGTH";  
                                                                      
                                                                      
                                                                      
    public String VOMS_SERIAL_NO_MAX_LENGTH = "VOMS_SNO_MAX_LENGTH";  
                                                                      
                                                                      
                                                                      
    public String VOMS_PIN_MIN_LENGTH = "VOMS_PIN_MIN_LENGTH";  
    public String VOMS_PIN_MAX_LENGTH = "VOMS_PIN_MAX_LENGTH";  
    public String STAFF_USER_COUNT = "STAFF_USER_COUNT";  
    public String C2S_ENQ_BAL_FLAG = "C2S_ENQ_BAL_HIDE";  
                                                          
                                                          
                                                          
                                                          
    public String P2P_ENQ_BAL_FLAG = "P2P_ENQ_BAL_HIDE";
    public String PORT_USR_SUSPEND_REQ = "PORT_USR_SUSPEND_REQ"; 
                                                                 
    public String MNP_ALLOWED = "MNP_ALLOWED";
    public String DIRECT_VOUCHER_ENABLE = "DCT_VOUCHER_EN";

    public String C2S_PROMOTIONAL_TRFRULE_CHECK = "C2S_PROMO_TRF_APP";
    public String PROMO_TRF_START_LVL_CODE = "PRO_TRF_ST_LVL_CODE";
     
    public String MVD_MAX_VOUCHER = "MVD_MAX_VOUCHER";

     
    public String PTUPS_MOBQUTY_MERGD = "PTUPS_MOBQUTY_MERGD";
    public String AUTO_PAYMENT_METHOD = "AUTO_PAYMENT_METHOD";

     
    public String LOW_BAL_MSGGATEWAY = "LOW_BAL_MSGGATEWAY";

     

    public String ALWD_ACC_STATUS_NUMBCK = "_AL_AC_STATUS_NBK";
    public String NUMBCK_DIFF_REQ_TO_IN = "_NBK_DIF_RQ_TO_IN";
    public String NUMBCK_ALWD_DAYS_DIFF = "_NBK_AL_DAYS_DIF";
    public String NUMBCK_AMT_DEDCTED = "_NBK_AMT_DEDCTED";
    public String ALWD_NUMBCK_SERVICES = "ALWD_SERVICES_NUMBCK";
    public String RC_ALWD_ACC_STATUS_NUMBCK = "RC_AL_AC_STATUS_NBK";
    public String RC_NUMBCK_DIFF_REQ_TO_IN = "RC_NBK_DIF_RQ_TO_IN";
    public String RC_NUMBCK_ALWD_DAYS_DIFF = "RC_NBK_AL_DAYS_DIF";
    public String RC_NUMBCK_AMT_DEDCTED = "RC_NBK_AMT_DEDCTED";
    public String PRC_ALWD_ACC_STATUS_NUMBCK = "PRC_AL_AC_STATUS_NBK";
    public String PRC_NUMBCK_DIFF_REQ_TO_IN = "PRC_NBK_DIF_RQ_TO_IN";
    public String PRC_NUMBCK_ALWD_DAYS_DIFF = "PRC_NBK_AL_DAYS_DIF";
    public String PRC_NUMBCK_AMT_DEDCTED = "PRC_NBK_AMT_DEDCTED";

     
    public String WE_REC_AMT_ALLWD_P2P = "WE_REC_AMT_ALLWD_P2P";
    public String WE_SUCTRAN_ALLWD_P2P = "WE_SUCTRAN_ALLWD_P2P";
    public String MO_REC_AMT_ALLWD_P2P = "MO_REC_AMT_ALLWD_P2P";
    public String MO_SUCTRAN_ALLWD_P2P = "MO_SUCTRAN_ALLWD_P2P";
    public String MAX_ALLD_BALANCE_P2P = "MAX_ALLD_BALANCE_P2P";

     

    public String WE_SUCTRAN_ALLWDCOUN = "WE_SUCTRAN_ALLWDCOUN";
    public String WE_TOTXN_AMT_ALLWDCO = "WE_TOTXN_AMT_ALLWDCO";
    public String MO_SUCTRAN_ALLWDCOUN = "MO_SUCTRAN_ALLWDCOUN";
    public String MO_TOTXN_AMT_ALLWDCO = "MO_TOTXN_AMT_ALLWDCO";
    public String MAX_ALLD_BALANCE_C2S = "MAX_ALLD_BALANCE_C2S";

     
     
    public String C2S_REF_NUMBER_REQUIRED = "C2S_REF_NUM_REQ";
    public String C2S_REF_NUMBER_UNIQUE = "C2S_REF_NUM_UNIQUE";
     
     
    public String PREV_PASS_NOT_ALLOW = "PRV_PASS_NOT_ALLOW";
    public String PREV_PIN_NOT_ALLOW = "PRV_PIN_NOT_ALLOW";

     
     
     
     
    public String PAYAMT_MRP_SAME = "PAYAMT_MRP_SAME";

     
    public String IS_FEE_APPLICABLE_FOR_VALIDATION_EXTENSION = "IS_FEE_APPL_VAL_EXT";
    public String IS_CREDIT_TRFR_WITH_VALIDITY_UPDATION_P2P = "IS_CT_WITH_VAL_UPDN";
    public String VAL_DAYS_TO_CHK_VALUPD = "VAL_DAYS_TO_CHK_VLUP";
    public String FEE_AND_VALIDITY_DAYS_TO_EXT = "FEE_VALDAYS_TO_EXT";

     
    public String C2S_PWD_BLK_EXP_DURATION = "C2S_PWD_BLK_EXP_DRN";  
                                                                     
                                                                     
    public String C2S_PIN_BLK_EXP_DURATION = "C2S_PIN_BLK_EXP_DRN"; 
                                                                    
                                                                    
                                                                    
                                                                    
                                                                    
                                                                    
                                                                    
    public String P2P_PIN_BLK_EXP_DURATION = "CP2P_PIN_BLK_EXP_DRN"; 
                                                                     
                                                                     
                                                                     
                                                                     
                                                                     
                                                                     
                                                                     

     
    public String IS_SEPARATE_RPT_DB = "IS_SEPARATE_RPT_DB";
    public String ALLOW_SELF_EVR = "ALLOW_SELF_EVR";

     
    public String NEG_ADD_COMM_APPLY = "NEG_ADD_COMM_APPLY";

    public String IS_SEPARATE_BONUS_REQUIRED = "IS_SEP_BONUS_REQUIRED";

     
    public String REC_MSG_SEND_ALLOW = "REC_MSG_SEND_ALLOW";
   
    public String REC_MSG_SEND_ALLOW_C2C = "REC_MSG_SEND_ALLOW_C2C";
    
     
    public String OPT_USR_APRL_LEVEL = "OPT_USR_APRL_LEVEL";
     
    public String APPROVER_CAN_EDIT = "APPROVER_CAN_EDIT";

     
    public String DISSABLE_BUTTON_LIST = "DISSABLE_BUTTON_LIST";

    public String P2P_REG_EXPIRY_PERIOD = "P2P_REG_EXPIRY_PERIOD";

     
     
    public String MESSAGE_TO_PRIMARY_REQUIRED = "MESSAGE_TO_PRIMARY_REQUIRED";
     
     
    public String SECONDARY_NUMBER_ALLOWED = "SECONDARY_NUMBER_ALLOWED";

     
    public String C2C_SMS_NOTIFY = "C2C_SMS_NOTIFY";
     
    public String SMS_MMS_ALLOWED = "SMS_MMS_ALLOWED";
    public String CIRCLEMINLMT = "CIRCLEMINLMT";
     
    public String CP2P_PIN_VALIDATION_REQUIRED = "CP2P_PIN_VALIDAT_REQ";
     
    public String FIXLINE_RC_ALLOW_SELF_TOPUP = "ALLOW_SELF_TOPUP_FRC";
    public String USER_VOUCHERSEGMENT_ALLOWED = "USER_VOUCHERSEGMENT_ALLOWED";
                                                                        
                                                                        
                                                                        
                                                                        
     
    public String BROADBAND_RC_ALLOW_SELF_TOPUP = "ALLOW_SELF_TOPUP_BRC";
    public String C2S_RANDOM_PIN_GENERATE = "C2S_RANDOM_PIN_GENERATE";  
                                                                        
                                                                        
    public String WEB_RANDOM_PWD_GENERATE = "WEB_RANDOM_PWD_GENERATE";  
                                                                        
                                                                        
    public String BATCH_USER_PASSWD_MODIFY_ALLOWED = "BATCH_USER_PASSWD_MODIFY_ALLOWED"; 
                                                                                         
                                                                                         
                                                                                         
                                                                                         
                                                                                         
                                                                                         
                                                                                         
                                                                                         
                                                                                         
                                                                                         
    public String RESET_PASSWORD_EXPIRED_TIME_IN_HOURS = "RESET_PWD_EXP_TIME_IN_HOURS";
    public String RESET_PIN_EXPIRED_TIME_IN_HOURS = "RESET_PIN_EXP_TIME_IN_HOURS";
    public String P2P_CARD_GROUP_SLAB_COPY = "P2P_CARD_GROUP_SLAB_COPY";
    public String C2S_CARD_GROUP_SLAB_COPY = "C2S_CARD_GROUP_SLAB_COPY";
    public String STK_REG_ICCID = "STK_REG_ICCID";  
                                                    
                                                    

     
    public static String ACTIVATION_BONUS_MIN_REDEMPTION_AMOUNT = "ACT_BONUS_MIN_AMOUNT";
    public static String ACTIVATION_BONUS_REDEMPTION_DURATION = "ACT_BONUS_REDEM_DUR";

    public String VOLUME_CALC_ALLOWED = "VOLUME_CALC_ALLOWED"; 
                                                               
                                                               
                                                               
                                                               
    public String POSITIVE_COMM_APPLY = "POSITIVE_COMM_APPLY";  
                                                                
                                                                
                                                                
                                                                
                                                                

     
    public String AUTO_PWD_GENERATE_ALLOW = "AUTO_PWD_GENERATE_ALLOW";
    public String AUTO_PIN_GENERATE_ALLOW = "AUTO_PIN_GENERATE_ALLOW";
     
    public String PSWD_EXP_TIME_IN_HOUR_AFTER_CREATION = "PSWD_EXP_TIME_IN_HOUR_ON_CREATION";
     
     
    public String CHECK_REC_TXN_AT_IAT = "CHECK_REC_TXN_AT_IAT";
     
    public String IS_IAT_RUNNING = "IS_IAT_RUNNING";
    public String POINT_CONVERSION_FACTOR = "POINT_CONVERSION_FACTOR";

     
    public String LAST_X_TRANSFER_STATUS = "LAST_X_TRANSFER_STATUS";
    public String SERVICE_FOR_LAST_X_TRANSFER = "SERVICE_FOR_LAST_X_TRANSFER";
     
    public String C2S_RECHARGE_MULTIPLE_ENTRY = "C2S_RECHARGE_MULTIPLE_ENTRY";
     
    public String LAST_X_CUSTENQ_STATUS = "LAST_X_CUSTENQ_STATUS";

     
    public String MULTIPLE_WALLET_APPLY = "MULTIPLE_WALLET_APPLY";
    public String NETWORK_STOCK_FOC_FIRSTAPPLIMIT = "FRSTAPPFOCLM";
    public String NETWORK_STOCK_INC_FIRSTAPPLIMIT = "FRSTAPPINCLM";

     
    public String RVERSE_TRN_EXPIRY = "RVERSE_TRN_EXPIRY";
    public String ALWD_REVTXN_SERVICES = "ALWD_REVTXN_SERVICES";
    public String RVE_C2S_TRN_EXPIRY = "RVE_C2S_TRN_EXPIRY";
    public String CHNL_PLAIN_SMS_SEPT_LOGINID = "CHNL_PLAIN_SMS_SEPT_LOGINID";

     
    public String EXTERNAL_CODE_MANDATORY_FORDP = "EXT_CODE_MAND_DP";
    public String EXTERNAL_TXN_MANDATORY_FORDP = "EXTTXNMANDT_DP";
    public String DP_ORDER_APPROVAL_LVL = "DP_ODR_APPROVAL_LVL";
    public String EXTERNAL_TXN_MANDATORY_DOMAINTYPE_DP = "EXTTXNMANDT_DOMAINTP_DP";
    public String DP_SMS_NOTIFY = "DP_SMS_NOTIFY";
    public String DP_ALLOWED = "DP_ALLOWED";
    public String LAST_TRF_MULTIPLE_SMS = "LAST_TRF_MULTIPLE_SMS";
    public String LAST_X_TRF_DAYS_NO = "LAST_X_TRF_DAYS_NO";

    public String SHA2_FAMILY_TYPE = "SHA2_FAMILY_TYPE";
    public String HTTPS_ENABLE = "HTTPS_ENABLE";

     
     
    public String PINPAS_EN_DE_CRYPTION_TYPE = "PINPAS_EN_DE_CRYPTION_TYPE";

    public String NWADM_CROSS_ALLOW = "NWADM_CROSS_ALLOW";
    public String STAFF_AS_USER = "STAFF_AS_USER";
     
     
    public String PRIVATE_RECHARGE_ALLOWED = "PRVT_RCHRG_ALLOW";
    public String NAMEEMBOSS_SEPT = "NAMEEMBOSS_SEPT";
    public String LOGIN_SPECIAL_CHAR_ALLOWED = "LOGIN_SPCL_CHAR_ALLOW";
    public String MVD_MIN_VOUCHER = "MVD_MIN_VOUCHER";
    public String VOMS_PIN_ENCRIPTION_ALLOWED = "VOMS_PIN_ENCRIPT_ALLOWED";
    public String CARD_GROUP_BONUS_RANGE = "CARD_GROUP_BONUS_RANGE";
     
    public String IS_DEFAULT_PROFILE = "IS_DEFAULT_PROFILE";
     
    public String REQ_CUSER_DLT_APP = "REQ_CUSER_DLT_APP";
     
    public String O2C_EMAIL_NOTIFICATION = "O2C_EMAIL_NOTIFICATION";
    public String FOC_ODR_APPROVAL_LVL = "FOC_ODR_APPROVAL_LVL";
     
    public String AUTO_C2C_TRANSFER_AMT = "AUTO_C2C_TRANSFER_AMT";
    public String SMS_ALLOWED = "SMS_ALLOWED";

     
    public String MVD_MAX_VOUCHER_EXTGW = "MVD_MAX_VOUCHER_EXTGW";
     
    public String CRYSTAL_SUMMARY_REPORT_MAX_DATEDIFF = "RPTSUMM_MAX_DATEDIFF";
     
    public String SID_ISNUMERIC = "SID_ISNUMERIC";

    public String MIN_SID_LENGTH_CODE = "MIN_SID_LENGTH";
    public String MAX_SID_LENGTH_CODE = "MAX_SID_LENGTH";

     
    public String MSISDN_MIGRATION_LIST_CODE = "MSISDN_MIGRATION_LIST";
     
    public String USER_EVENT_REMARKS = "USER_EVENT_REMARKS";
     
    public String MIN_SID_LENGTH = "MIN_SID_LENGTH";
    public String MAX_SID_LENGTH = "MAX_SID_LENGTH";

     
    public String PRIVATE_SID_SERVICE_ALLOW = "PVT_SID_SERVICE_ALLOW";
     
    public String MULT_CRE_TRA_DED_ACC_SEP = "MULT_CRE_TRA_DED_ACC_SEP";

    public String EXTERNAL_TXN_MANDATORY_FORO2C = "EXTTXNMANDT_O2C";

     
    public String EXTERNAL_CODE_MANDATORY_FORO2C = "EXT_CODE_MAND_O2C";

    public String O2C_SMS_NOTIFY = "O2C_SMS_NOTIFY";

     
    public String RP2PDWH_OPT_SPECIFIC_PROC_NAME = "RP2PDWH_OPT_SPECIFIC_PROC_NAME";
    public String P2PDWH_OPT_SPECIFIC_PROC_NAME = "P2PDWH_OPT_SPECIFIC_PROC_NAME";
    public String IATDWH_OPT_SPECIFIC_PROC_NAME = "IATDWH_OPT_SPECIFIC_PROC_NAME";

     
    public String PRIVATE_RECH_MESSGATEWAY = "PVT_RECH_MESSGATEWAY";

    public String PARENT_CATEGORY_LIST = "PARENT_CATEGORY_LIST";
    public String OWNER_CATEGORY_LIST = "OWNER_CATEGORY_LIST";

     
    public String MAX_AUTO_FOC_ALLOW_LIMIT = "MAX_AUTO_FOC_ALLOW_LIMIT";

     
    public String AUTO_FOC_TRANSFER_AMOUNT = "AUTO_FOC_TRANSFER_AMOUNT";
     

    public String ALLOW_ROAM_RECHARGE = "ALLOW_ROAM_RECHARGE";

     
     
     
     
    public String USSD_NEW_TAGS_MANDATORY = "USSD_TAGS_CELLID_SWITCHID_MANDATORY";
    public String PLAIN_RES_PARSE_REQUIRED = "PLAIN_RES_PARSE_REQUIRED";
    public String COUNTRY_CODE = "COUNTRY_CODE";

     
    public String MRP_BLOCK_TIME_ALLOWED = "MRP_BLOCK_TIME_ALLOWED";
    public String LAST_SERVICE_TYPE_CHECK = "LAST_SERVICE_TYPE_CHECK";
     
    public String ACTIVATION_FIRST_REC_APP = "ACT_FRST_RCH_APP";

     
    public String SOS_SETTLE_DAYS = "SOS_SETTLE_DAYS";
    public String SOS_RECHARGE_AMOUNT = "SOS_RECHARGE_AMOUNT";
    public String SOS_MIN_VALIDITY_DAYS = "SOS_MIN_VALIDITY_DAYS";
    public String SOS_ALLOWED_MAX_BALANCE = "SOS_ALLOWED_MAX_BALANCE";
    public String SOS_ST_DEDUCT_UPFRONT = "SOS_ST_DEDUCT_UPFRONT";
    public String SOS_DAYS_GAP_BTWN_TWO_TRAN = "SOS_DAYS_GAP_BTWN_TWO_TRAN";
    public String SERV_CLASS_ALLOW_FOR_SOS = "SERV_CLASS_ALLOW_FOR_SOS";
    public String SOS_MINIMUM_AON = "SOS_MINIMUM_AON";
    public String SOS_ELIBILITY_ACCOUNT = "SOS_ELIBILITY_ACC";
    public String SOS_ONLINE_ALLOW = "SOS_ONLINE_ALLOW";
    public String ENQ_POSTBAL_IN = "ENQ_POSTBAL_IN";
    public String LMB_FORCE_SETL_STAT_ALLOW = "LMB_FORCE_SETL_STAT_ALLOW";
    public String LMB_VALIDITY_DAYS_FORCESETTLE = "LMB_VALIDITY_DAYS_FORCESETTLE";
    public String ENQ_POSTBAL_ALLOW = "ENQ_POSTBAL_ALLOW";
    public String LMB_BLK_UPL = "LMB_BLK_UPL";
     
    public String VOMS_MAX_APPROVAL_LEVEL = "VOMS_MAX_APPROVAL_LEVEL"; 
                                                                       
                                                                       
    public String VOMS_USER_KEY_REQD = "VOMS_USER_KEY_REQD";
    public String DB_ENTRY_NOT_ALLOWED = "DB_ENTRY_NOT_ALLOWED";
    public String RVERSE_TXN_APPRV_LVL = "RVERSE_TXN_APPRV_LVL";
    public String VOUCHER_TRACKING_ALLOWED = "VOUCHER_TRACKING_ALLOWED";
    public String PROCESS_FEE_REV_ALLOWED = "PROCESS_FEE_REV_ALLOWED";
    public String VOUCHER_EN_ON_TRACKING = "VOUCHER_EN_ON_TRACKING";
    public String MCDL_MAX_LIST_COUNT = "MCDL_MAX_LIST_COUNT";
    public String MCDL_DIFFERENT_REQUEST_SEPERATOR = "MCDL_DIFF_REQST_SEP";
    public String P2P_MCDL_DEFAULT_AMOUNT = "P2P_MCDL_DEFAULT_AMOUNT";
    public String P2P_MCDL_MAXADD_AMOUNT = "P2P_MCDL_MAXADD_AMOUNT";
    public String P2P_MCDL_AUTO_DELETION_DAYS = "P2P_MCDL_AUTO_DELETION_DAYS";

    public String LMB_DEBIT_REQ = "LMB_DEBIT_REQ";
    public String PRVT_RC_MSISDN_PREFIX_LIST = "PRVT_RC_MSISDN_PREFIX_LIST";
    public String IS_FNAME_LNAME_ALLOWED = "IS_FNAME_LNAME_ALLOWED";
    public String LAST_X_RECHARGE_STATUS = "LAST_X_RECHARGE_STATUS";
    public String SERVICE_FOR_LAST_X_RECHARGE = "SERVICE_FOR_LAST_X_RECHARGE";
    public String SMS_PIN_BYPASS_GATEWAY_TYPE = "SMS_PIN_BYPASS_GATEWAY";

    public String CATEGORY_ALLOWED_FOR_CREATION = "CAT_ALLOW_CREATION";
    public String CP_SUSPENSION_DAYS_LIMIT = "CP_SUSPENSION_DAYS_LIMIT";
    public String EXTERNAL_CODE_MANDATORY_FORUSER = "EXT_CODE_MAND_USER";

    public String SIMACT_DEFAULT_SELECTOR = "SIMACT_DEFAULT_SELECTOR";
    public String SELECTOR_INTERFACE_MAPPING = "SELECTOR_INTERFACE_MAPPING";
     
     
    public String EXT_VOMS_MSG_DES_ENDE_CRYPT_KEY = "EXT_VOMS_MSG_ENDEC_KEY";

     
    public String USR_DEF_CONFIG_UPDATE_REQ = "USR_DEF_CONFIG_UPDATE_REQ";
     
    public String LOGIN_ID_CHECK_ALLOWED = "LOGIN_ID_CHECK_ALLOWED";
     
    public String RSA_AUTHENTICATION_REQUIRED = "RSA_AUTHENTICATION_REQUIRED";
    public String RSA_AUTHENTICATION_COUNT = "COUNT_TO_ASK_RSA_CODE";
    public String STAFF_USER_APRL_LEVEL = "STAFF_USER_APRL_LEVEL"; 
                                                                   
                                                                   

     
    public String TRF_RULE_USER_LEVEL_ALLOW = "TRF_RULE_USER_LEVEL_ALLOW";

     
    public String BATCH_USER_PROFILE_ASSIGN = "BATCH_USER_PROFILE_ASSIGN";
    public String BATCH_INTIATE_NOTIF_TYPE = "BATCH_INTIATE_NOTIF_TYPE";
     
    public String EMAIL_SERVICE_ALLOW = "EMAIL_SERVICE_ALLOW";

     
    public String COS_REQUIRED = "COS_REQUIRED";
    public String SERVICE_PROVIDER_PROMO_ALLOW = "SERVICE_PROVIDER_PROMO_ALLOW";

     

    public String CELL_GROUP_REQUIRED = "CELL_GROUP_REQUIRED";

     
    public String SRVC_PROD_MAPPING_ALLOWED = "SRVC_PROD_MAPPING_ALLOWED";
    public String SRVC_PROD_INTFC_MAPPING_ALLOWED = "SRVC_PROD_INTFC_MAPPING_ALLOWED";

     
    public String CELL_ID_SWITCH_ID_REQUIRED = "CELL_ID_SWITCH_ID_REQUIRED";
     
    public String MULTI_AMOUNT_ENABLED = "MULTI_AMOUNT_ENABLED";
     
    public String IN_PROMO_REQUIRED = "IN_PROMO_REQUIRED";
    public String DEBIT_SENDER_SIMACT = "DEBIT_SENDER_SIMACT";
     
    public String LOGIN_PASSWORD_ALLOWED = "LOGIN_PASSWORD_ALLOWED";
     
    public String AUTO_O2C_AMOUNT = "AUTO_O2C_AMOUNT";
    public String AUTO_O2C_MAX_APPROVAL_LEVEL = "AUTO_O2C_MAX_APPROVAL_LEVEL";
    public String AUTO_O2C_USER_SPECIFIC_AMOUNT = "AUTO_O2C_USER_SPECIFIC_AMOUNT";

     
    public String DAYS_TILL_USER_IS_BARRED = "BAR_FOR_DEL_DAYS";
    public String PER_DAY_LIMIT_BAR_FOR_DEL = "PER_DAY_BAR_FOR_DEL_LIMIT";
    public String REQ_CUSER_BAR_APPROVAL = "REQ_CUSER_BAR_APP";
    public String SRVCS_FOR_PROD_MAPPING = "SRVCS_FOR_PROD_MAPPING";

     
    public String SHOW_CAPTCHA = "SHOW_CAPTCHA";

     
    public String EMAIL_AUTH_REQ = "EMAIL_AUTH_REQ";
    public String MIN_HRDIF_ST_ED_LMS = "MIN_HRDIF_ST_ED_LMS";
    public String MIN_HRDIF_CR_ST_LMS = "MIN_HRDIF_CR_ST_LMS";
    public String LMS_APPL = "LMS_APPL";
     
    public String P2P_MCDL_ALLOWED_SCHEDULE_TYPE = "P2P_ALLOWED_SCHTYPE";
    public String P2P_MCDL_DEFAULT_FREQUENCY = "P2P_DEFAULT_NO_OF_SCHEDULES";
     
    public String IS_SEPARATE_EXT_DB = "IS_SEPARATE_EXT_DB";
     
    public String PROMOTION_HIERARCHY_WISE = "PROMOTION_HIERARCHY_WISE";
    public String LMS_VOL_COUNT_ALLOWED = "LMS_VOL_COUNT_ALLOWED";
    public String LMS_PROF_APR_ALLOWED = "LMS_PROF_APR_ALLOWED";

    public String AUTO_O2C_APPROVAL_ALLOWED = "AUTO_O2C_APPROVAL_ALLOWED";
    public String AUTH_TYPE_REQ = "AUTH_TYPE_REQ";
    public String TWO_FA_REQ= "TWO_FA_REQ";
    public String SAP_INTEGARATION_FOR_USRINFO = "SAP_INTEGARATION_FOR_USRINFO";
    public String OTP_AUTHENTICATION_COUNT = "COUNT_TO_ASK_OTP_CODE";
     
    public String MIN_ACCOUNT_ID_LENGTH_CODE = "MIN_ACCOUNT_ID_LENGTH";
    public String MAX_ACCOUNT_ID_LENGTH_CODE = "MAX_ACCOUNT_ID_LENGTH";

    public String C2S_TRNSFR_AMTBLCK_SRVCTYP = "C2S_TRNSFR_AMTBLCK_SRVCTYP";
    public String OTP_TIMEOUT_INSEC = "OTP_TIMEOUT_INSEC";
    public String C2S_TRNSFR_INVNO_SRVCTYP = "C2S_TRNSFR_INVNO_SRVCTYP";
    public String O2C_PNDNG_FOR_APPR_SEND_MAIL_DAYS_CNT = "O2C_PNDNG_FOR_APPR_SEND_MAIL_DAYS_CNT";
    public String C2S_REVERSAL_TXNID_SRVCTYP = "C2S_REVERSAL_TXNID_SRVCTYP";
    public String LMS_STOCK_REQUIRED = "LMS_STOCK_REQUIRED";
    public String LMS_MULT_FACTOR = "LMS_MULT_FACTOR";

    public String LMS_VOL_CREDIT_LOYAL_PTS = "LMS_VOL_CREDIT_LOYAL_PTS";
     
    public String EXTSYS_USR_APRL_LEVEL_REQUIRED = "EXTSYS_USR_APRL_LEVEL_REQUIRED";
     
    public String MAX_LAST_TRANSFERS_DAYS = "MAX_LAST_TRANSFERS_DAYS";
     
    public String MAX_AUTOTOPUP_AMT = "MAX_AUTOTOPUP_AMT";
     
     
    public String INVALID_PWD_COUNT_FOR_CAPTCHA = "INVALID_PWD_COUNT_FOR_CAPTCHA";
    public String CAPTCHA_LENGTH = "CAPTCHA_LENGTH";
     
    public String PAYMENTDETAILSMANDATE_O2C = "PAYMENTDETAILSMANDATE_O2C";
    public String PAYMENTDETAILSMANDATE_C2C = "PAYMENTDETAILSMANDATE_C2C";

    public String USER_CREATION_MANDATORY_FIELDS = "USER_CREATION_MANDATORY_FIELDS";

    public String USER_APPROVAL_LEVEL = "USER_APPROVAL_LEVEL";
    public String O2C_APPRV_QTY_LEVEL = "O2C_APPRV_QTY_LEVEL";

    public String STAFF_USER_AUTH_TYPE = "STAFF_USER_AUTH_TYPE";

    public String IS_REQ_MSISDN_FOR_STAFF = "IS_REQ_MSISDN_FOR_STAFF";
    public String THLD_PRTP_PRCSS_TIME = "THLD_PRTP_PRCSS_TIME";
    public String CHANNEL_USER_ROLE_TYPE_DISPLAY = "CHANNEL_USER_ROLE_TYPE_DISPLAY";
    public String PERCENTAGE_OF_PRE_REVERSAL = "PERCENTAGE_OF_PRE_REVERSAL";
    public String ALLOW_ROAM_ADDCOMM = "ALLOW_ROAM_ADDCOMM";
     
    public String ALLOW_CCARD_ROAM_RECHARGE = "ALLOW_CCARD_ROAM_RECHARGE"; 
                                                                           
    public String ROAM_INTERFACE_ID = "ROAM_INTERFACE_ID"; 
    public String VOUCHER_SERVICES_LIST = "VOUCHER_SERVICES_LIST";
    public String MULTIPLE_VOUCHER_TABLE = "MULTIPLE_VOUCHER_TABLE";
     
    public String LMS_PCT_POINTS_CALCULATION = "LMS_PCT_POINTS_CALCULATION";

    public String IS_PARTIAL_BATCH_ALLOWED = "IS_PARTIAL_BATCH_ALLOWED";
    public String USER_PRODUCT_MULTIPLE_WALLET = "USER_PRODUCT_MULTIPLE_WALLET";
    public String MIN_VOUCHER_CODE_LENGTH = "MIN_VOUCHER_CODE_LENGTH";
    public String WALLET_FOR_ADNL_CMSN = "WALLET_FOR_ADNL_CMSN";
    public String IS_MSISDN_ASSOCIATION_REQ = "IS_MSISDN_ASSO_REQ";
    public String ADMINISTRBLY_USER_STATUS_CHANG = "ADMINISTRBLY_USER_STATUS_CHANG";
    public String REALTIME_AUTO_C2C_ALLOWED = "REALTIME_AUTO_C2C_ALLOWED";
    public String TXN_SENDER_USER_STATUS_CHANG = "TXN_SENDER_USER_STATUS_CHANG";
    public String TXN_RECEIVER_USER_STATUS_CHANG = "TXN_RECEIVER_USER_STATUS_CHANG";

    
    
    
    
    public String LIFECYCLE_STATUS_DAYS_LIST = "LIFECYCLE_STATUS_DAYS_LIST";
    public String ALERT_ALLOWED = "ALERT_ALLOWED";
    public String BLOCKING_ALLOWED = "BLOCKING_ALLOWED";
    public String INTRFC_MAX_NODES = "INTRFC_MAX_NODES";
    public String ROAM_RECHARGE_DAILY_THRESHOLD = "ROAM_RECHARGE_DAILY_THRESHOLD";
    public String ROAM_RECHARGE_PENALTY_PERCENTAGE = "ROAM_RECHARGE_PENALTY_PERCENTAGE";
    public String ROAM_PENALTY_OWNER_PERCENTAGE = "ROAM_PENALTY_OWNER_PERCENTAGE";
    public String RETURN_TO_OPERATOR_STOCK = "RET_OPRTR_STOCK";
	public String INTF_NODE_VALIDATION="INTF_NODE_VALIDATION";
	public static String DP_NETWORKLEVEL_DAILYLIMIT="DP_NETWORKLEVEL_DAILYLIMIT";
	public static String DP_SYSTEMLEVEL_LIMIT="DP_SYSTEMLEVEL_LIMIT";
	public static String DP_ONLINE_LIMIT="DP_ONLINE_LIMIT";
	public String DECENTER_ROAM_LOCATION ="DECENTER_ROAM_LOCATION";
	public static String LB_SYSTEMLEVEL_LIMIT="LB_SYSTEMLEVEL_LIMIT";
	public String CHOICE_RECHARGE_APPLICABLE="CHOICE_RECHARGE_APPLICABLE";
	public String TIME_FOR_REVERSAL="TIME_FOR_REVERSAL";
	public String TIME_FOR_REVERSAL_CCE="TIME_FOR_REVERSAL_CCE";
	public String ALLOWED_DAYS_FOR_REVERSAL="ALLOWED_DAYS_FOR_REVERSAL";
	public String ALLOWED_SERVICES_FOR_REVERSAL="ALLOWED_SERVICES_FOR_REVERSAL";
	public String ALLOW_BULK_C2S_REVERSAL_MESSAGE="ALLOW_BULK_C2S_REVERSAL_MESSAGE";
	public String ALLOWED_GATEWAY_FOR_BULK_REVERSAL="ALLOWED_GATEWAY_FOR_BULK_REVERSAL";
	public String O2C_BATCH_WITHDRAW_MESSAGE_ALLOWED="O2C_BATCH_WITHDRAW_MESSAGE_ALLOWED";
	public String ADMIN_MESSAGE_REQD="ADMIN_MESSAGE_REQD";  
	public String FNF_ZB_ALLOWED="FNF_ZB_ALLOWED";
	public String LOW_BASED_ALLOWED="LOW_BASED_ALLOWED";
	public String P2P_SERVICES_TYPE_SERVICECLASS = "P2P_SERVICES_TYPE_SERVICECLASS";
	public String AUTO_NWSTK_CRTN_ALWD="AUTO_NWSTK_CRTN_ALWD";
	public String AUTO_NWSTK_CRTN_THRESHOLD="AUTO_NWSTK_CRTN_THRESHOLD";
	public String TOKEN_EXPIRY_IN_MINTS = "TOKEN_EXPIRY_IN_MINTS";
	public String O2C_DIRECT_TRANSFER="O2C_DIRECT_TRANSFER";
	public String SEQUENCE_ID_RANGE="SEQUENCE_ID_RANGE";
	public String SEQUENCE_ID_ENABLE="SEQUENCE_ID_ENABLE";
	public String HASHING_ENABLE="HASHING_ENABLE";
	public String HASHING_ID_RANGE="HASHING_ID_RANGE";
	public String CHANNEL_SOS_ENABLE ="CHANNEL_SOS_ENABLE";
	public String CHANNEL_AUTOC2C_ENABLE ="CHANNEL_AUTOC2C_ENABLE";
	public String AUTO_C2C_SOS_CAT_ALLOWED ="AUTO_C2C_SOS_CAT_ALLOWED";
	public String SOS_SETTLEMENT_TYPE ="SOS_SETTLEMENT_TYPE";
	public String CHANNEL_SOS_ALLOWED_WALLET ="CHANNEL_SOS_ALLOWED_WALLET";
	public String DOWNLOAD_CSV_REPORT_REQUIRED="DOWNLOAD_CSV_REPORT_REQUIRED"; 
	public String ALLOW_TRANSACTION_IF_SOS_SETTLEMENT_FAIL = "ALLOW_TRANSACTION_IF_SOS_SETTLEMENT_FAIL";
	public String TARGET_BASED_COMMISSION = "TARGET_BASED_ADDNL_COMMISSION";
	public String TARGET_BASED_BASE_COMMISSION = "TARGET_BASED_BASE_COMMISSION";
	public String LR_ENABLED ="LR_ENABLED";
	public String TARGET_BASED_COMMISSION_SLABS = "TARGET_BASED_ADDNL_COMMISSION_SLABS";
	public String TARGET_BASED_BASE_COMMISSION_SLABS = "TARGET_BASED_BASE_COMMISSION_SLABS";
	public String DECRYPT_KEY_VISIBLE="DECRYPT_KEY_VISIBLE";
	public String THIRD_PARTY_VISIBLE="THIRD_PARTY_VISIBLE";
	public String VOMS_PROFILE_ACTIVATION_REQ="VOMS_PROFILE_ACTIVATION_REQ";
	public String OFFLINE_SETTLE_EXTUSR="OFFLINE_SETTLE_EXTUSR";
	public String REALTIME_OTF_MSGS="REALTIME_OTF_MESSAGES";
	public String AUTO_VOUCHER_CRTN_ALWD="VMS_AUTO_VOUCHER_CRTN_ALWD";
	public String BURN_RATE_THRESHOLD="BURN_RATE_THRESHOLD_PCT";
	public String VOUCHER_BURN_RATE_SMS_ALERT="VOUCHER_BURN_RATE_SMS_ALERT";
	public String VOUCHER_BURN_RATE_EMAIL_ALERT="VOUCHER_BURN_RATE_EMAIL_ALERT";
	public String INET_REPORT_ALLOWED="INET_REPORT_ALLOWED";
	public String VOMS_DAMG_PIN_LNTH_ALLOW = "VOMS_DAMG_PIN_LNTH_ALLOW";
	public String DATE_FORMAT_CAL_JAVA = "DATE_FORMAT_CAL_JAVA";
	public String DATE_TIME_FORMAT = "DATE_TIME_FORMAT";
	public String LOCALE_ENGLISH = "LOCALE_ENGLISH";
	public String TIMEZONE_ID = "TIMEZONE_ID";
	public String CALENDAR_TYPE = "CALENDAR_TYPE";
	public String CALENDER_DATE_FORMAT = "CALENDER_DATE_FORMAT";
	public String CALENDAR_SYSTEM = "CALENDAR_SYSTEM";
	public String FORMAT_MONTH_YEAR = "FORMAT_MONTH_YEAR";
	public String EXTERNAL_CALENDAR_TYPE = "EXTERNAL_CALENDAR_TYPE";
	public String IS_CAL_ICON_VISIBLE = "IS_CAL_ICON_VISIBLE";
	public String IS_MON_DATE_ON_UI = "IS_MON_DATE_ON_UI";
	public String PIN_REQUIRED_P2P = "PIN_REQUIRED_P2P";
	public String MAX_REQ_VOUCHER_QTY="MAX_REQ_VOUCHER_QTY";
	public String PG_INTEFRATION_ALLOWED="PG_INTEFRATION_ALLOWED";
	public String CHNLUSR_VOUCHER_CATGRY_ALLWD = "CHNLUSR_VOUCHER_CATGRY_ALLWD";
	public String PAYMENT_MODE_ALWD="PAYMENT_MODE_ALWD";
	public String TRANSACTION_TYPE_ALWD="TRANSACTION_TYPE";
	public String O2CAMB_MINUTES_DELAY="O2CAMB_MINUTES_DELAY";
	public String VMS_ALLOW_CONTENT_TYPE="VMS_ALLOW_CONTENT_TYPE";
	public String C2C_ALLOW_CONTENT_TYPE="C2C_ALLOW_CONTENT_TYPE";
	public String EMAIL_DEFAULT_LOCALE="EMAIL_DEFAULT_LOCALE";
	public String IPV6_ENABLED="IPV6_ENABLED";
	public String VMSPIN_EN_DE_CRYPTION_TYPE="VMSPIN_EN_DE_CRYPTION_TYPE";
	public String LDAP_UTIL_CLASS = "LDAP_UTIL_C";
	
	public String VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN = "VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN";
	public String VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN = "VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN";
	public String VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN = "VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN";
	
	public String	VOMS_ADD_PROFILE_TALK_TIME_MANDATORY="VOMS_PROF_TALKTIME_MANDATORY";
	public String   VOMS_ADD_PROFILE_VALIDITY_MANDATORY="VOMS_PROF_VALIDITY_MANDATORY";
	public String	VOMS_PROFILE_DEF_MINMAXQTY="VOMS_PROFILE_DEF_MINMAXQTY";
	public String	VOMS_PROFILE_MIN_REORDERQTY="VOMS_PROFILE_MIN_REORDERQTY";
	public String	VOMS_PROFILE_MAX_REORDERQTY="VOMS_PROFILE_MAX_REORDERQTY";
	
	public String NW_NATIONAL_PREFIX = "NW_NATIONAL_PREFIX";
	public String NW_CODE_NW_PREFIX_MAPPING = "NW_CODE_NW_PREFIX_MAPPING";
	public String VOMS_NATIONAL_LOCAL_PREFIX_ENABLE = "VOMS_NATIONAL_LOCAL_PREFIX_ENABLE";
	
	public String VMS_SERVICES = "VMS_SERVICES";
	
	public String ONLINE_VOUCHER_GEN_LIMIT = "ONLINE_VOUCHER_GEN_LIMIT";
	
	public String VOUCHER_GEN_EMAIL_NOTIFICATION = "VOUCHER_GEN_EMAIL_NOTIFICATION";
	
	public String VOUCHER_GEN_SMS_NOTIFICATION = "VOUCHER_GEN_SMS_NOTIFICATION";

	public String VMS_D_STATUS_CHANGE = "VMS_D_STATUS_CHANGE";
	public String VMS_E_STATUS_CHANGE = "VMS_E_STATUS_CHANGE";
	public String VMS_P_STATUS_CHANGE = "VMS_P_STATUS_CHANGE";
	public String VMS_D_STATUS_CHANGE_MAP = "VMS_D_STATUS_CHANGE_MAP";
	public String VMS_E_STATUS_CHANGE_MAP = "VMS_E_STATUS_CHANGE_MAP";
	public String VMS_P_STATUS_CHANGE_MAP = "VMS_P_STATUS_CHANGE_MAP";

	public String SUBSCRIBER_VOUCHER_PIN_REQUIRED = "SUBSCRIBER_VOUCHER_PIN_REQUIRED";
	public String ONLINE_DVD_LIMIT = "ONLINE_DVD_LIMIT";
	public String ONLINE_VOUCHER_GEN_LIMIT_NW = "ONLINE_VOUCHER_GEN_LIMIT_NW";
	public String ONLINE_VOUCHER_GEN_LIMIT_SYSTEM = "ONLINE_VOUCHER_GEN_LIMIT_SYSTEM";
	public String DOWNLD_BATCH_BY_BATCHID = "DOWNLD_BATCH_BY_BATCHID";
	public String ONLINE_BATCH_EXP_DATE_LIMIT="ONLINE_BATCH_EXP_DATE_LIMIT";
	public String MAX_VOUCHER_EXPIRY_EXTN_LIMIT = "MAX_VOUCHER_EXPIRY_EXTN_LIMIT";
	public String NATIONAL_VOUCHER_ENABLE="NATIONAL_VOUCHER_ENABLE";
	public String NATIONAL_VOUCHER_NETWORK_CODE="NATIONAL_VOUCHER_NETWORK_CODE";
	
	public String SCREEN_WISE_ALLOWED_VOUCHER_TYPE="SCREEN_WISE_ALLOWED_VOUCHER_TYPE";
	public String ONLINE_CHANGE_STATUS_NETWORK_LMT="ONLINE_CHANGE_STATUS_NETWORK_LMT";
	public String ONLINE_CHANGE_STATUS_SYSTEM_LMT = "ONLINE_CHANGE_STATUS_SYSTEM_LMT";
	public String C2C_ALLOWED_VOUCHER_LIST = "C2C_ALLOWED_VOUCHER_LIST";
	public String MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER="MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER";
	public String MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE = "MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE";
	public String CARD_GROUP_ALLOWED_CATEGORIES = "CARD_GROUP_ALLOWED_CATEGORIES";
	public String TRANSFER_RULE_ALLOWED_CATEGORIES = "TRANSFER_RULE_ALLOWED_CATEGORIES";
	public String MIN_LAST_DAYS_CG = "MIN_LAST_DAYS_CG";
	public String MAX_LAST_DAYS_CG = "MAX_LAST_DAYS_CG";
	public String REPORT_MAX_DATEDIFF_ADMIN_CONS = "REPORT_MAX_DATEDIFF_ADMIN_CONS";
	public String REPORT_MAX_DATEDIFF_USER_CONS = "REPORT_MAX_DATEDIFF_USER_CONS";
	public String REPORT_MAX_DATEDIFF_USER_AVAIL = "REPORT_MAX_DATEDIFF_USER_AVAIL";
	public String REPORT_MAX_DATEDIFF_ADMIN_AVAIL = "REPORT_MAX_DATEDIFF_USER_AVAIL";
	public String REPORT_MAX_DATEDIFF_ADMIN_NLEVEL = "REPORT_MAX_DATEDIFF_ADMIN_NLEVEL";
	public String USER_ALLOWED_VINFO = "USER_ALLOWED_VINFO";
	public String TWO_FA_REQ_FOR_PIN = "TWO_FA_REQ_FOR_PIN";
	public String OTP_RESEND_TIMES = "OTP_RESEND_TIMES";
	public String OTP_RESEND_DURATION ="OTP_RESEND_DURATION";
	public String OTP_VALIDITY_PERIOD = "OTP_VALIDITY_PERIOD";
	public String MAX_INVALID_OTP = "MAX_INVALID_OTP";
	public String BLOCK_TIME_INVALID_OTP = "BLOCK_TIME_INVALID_OTP";
	public String OTP_ON_SMS = "OTP_ON_SMS";
	public String AUTOCOMPLETE_USER_DETAILS_COUNT ="AUTOCOMPLETE_USER_DETAILS_COUNT";
	public String MIN_LENGTH_TO_AUTOCOMPLETE= "MIN_LENGTH_TO_AUTOCOMPLETE";
	public String TOKEN_EXPIRE_TIME = "TOKEN_EXPIRE_TIME";
	public String REFRESH_TOKEN_EXPIRE_TIME = "REFRESH_TOKEN_EXPIRE_TIME";
	public String ALERT_ALLOWED_USER = "ALERT_ALLOWED_USER";
	public String CHANNEL_USER_MNP_ALLOW = "CHANNEL_USER_MNP_ALLOW";
	public String FILE_UPLOAD_MAX_SIZE = "FILE_UPLOAD_MAX_SIZE";
	public String DEFAULT_PRODUCT_CODE = "DEFAULT_PRODUCT_CODE";
	public String REPORT_ONLINE_OR_OFFLINE = "REPORT_ONLINE_OR_OFFLINE";
	public String  REPORT_OFFLINE="REPORT_OFFLINE";
	public String OTP_VALIDITY_TIME="OTP_VALIDITY_TIME";
 

	
}