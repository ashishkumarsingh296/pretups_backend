package com.selftopup.pretups.preference.businesslogic;

import java.util.Date;

/*
 * PreferenceI.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 20/06/2005 Initial Creation
 * Babu Kunwar 10-Feb-2011 User Event Remarks
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Interface for storing the perferences codes
 */

public interface PreferenceI {

    public String DEFAULT_LANGUAGE = "DEFAULT_LANGUAGE";// for default locale
    public String DEFAULT_COUNTRY = "DEFAULT_COUNTRY";// For Default Country

    public String SYSTEM_LEVEL = "SYSTEMPRF";// for system preference
    public String NETWORK_LEVEL = "NETWORKPRF";// for netword preference
    public String ZONE_LEVEL = "ZONEPRF";// for zone preference
    public String SERVICE_CLASS_LEVEL = "SVCCLSPRF"; // for service class
                                                     // preference

    public String AMOUNT_MULT_FACTOR = "AMOUNT_MULT_FACTOR";// System Amount
                                                            // multiplication
                                                            // factor
    public String MIN_MSISDN_LENGTH_CODE = "MIN_MSISDN_LENGTH";
    public String MAX_MSISDN_LENGTH_CODE = "MAX_MSISDN_LENGTH";
    public String MSISDN_PREFIX_LENGTH_CODE = "MSISDN_PREFIX_LENGTH";
    public String MSISDN_PREFIX_LIST_CODE = "MSISDN_PREFIX_LIST";
    public String SERVICECLASS_CODE = "SVCCLSPRF";// for service class
                                                  // preference
    /* For SKey Related Preferences */
    public String SKEY_EXPIRY_TIME_CODE = "SKEYEXPIRYSEC";// for skey expiry
                                                          // time
    public String SKEY_LENGTH_CODE = "SKEYLENGTH";// for skey length

    /* For subscriber related Preferences */
    public String P2P_MAX_PTAGE_TRANSFER_CODE = "MAX_PER_TRANSFER";// for
                                                                   // maximum
                                                                   // percentage
                                                                   // transfer
    public String DAILY_MAX_TRFR_AMOUNT_CODE = "DAY_SDR_MX_TRANS_AMT";// for
                                                                      // maximum
                                                                      // transfer
                                                                      // amount
                                                                      // allowed
                                                                      // in day
    public String DAILY_MAX_TRFR_NUM_CODE = "DAY_SDR_MX_TRANS_NUM";// for
                                                                   // maximum no
                                                                   // of
                                                                   // transfer
                                                                   // allowed in
                                                                   // day
    public String MONTHLY_MAX_TRFR_AMOUNT_CODE = "MON_SDR_MX_TRANS_AMT";// for
                                                                        // maximum
                                                                        // transfer
                                                                        // amount
                                                                        // allowed
                                                                        // in
                                                                        // month
    public String MONTHLY_MAX_TRFR_NUM_CODE = "MON_SDR_MX_TRANS_NUM";// for
                                                                     // maximum
                                                                     // no of
                                                                     // transfer
                                                                     // allowed
                                                                     // in month
    public String WEEKLY_MAX_TRFR_AMOUNT_CODE = "WK_SDR_MX_TRANS_AMT";// for
                                                                      // maximum
                                                                      // transfer
                                                                      // amount
                                                                      // allowed
                                                                      // in week
    public String WEEKLY_MAX_TRFR_NUM_CODE = "WK_SDR_MX_TRANS_NUM";// for
                                                                   // maximum no
                                                                   // of
                                                                   // transfer
                                                                   // allowed in
                                                                   // week
    public String MIN_RESIDUAL_BAL_TYPE_CODE = "MIN_RES_BALTYPE";// for minimum
                                                                 // residual
                                                                 // balance type
                                                                 // (% or
                                                                 // amount)
    public String MIN_RESIDUAL_BAL_CODE = "MIN_RESIDUAL_BAL";// for minimum
                                                             // residual balance
    public String MIN_VALIDITY_DAYS_CODE = "MIN_VALIDITY_DAYS";// for maximum
                                                               // validity for
                                                               // user allowed
    public String C2S_MAX_PIN_BLOCK_COUNT_CODE = "C2S_MAX_PIN_BLK_CONT";// for
                                                                        // maximum
                                                                        // invalid
                                                                        // pin
                                                                        // count
                                                                        // allowed
                                                                        // before
                                                                        // getting
                                                                        // blocked
    public String P2P_MAX_PIN_BLOCK_COUNT = "P2P_MAX_PIN_BLK_CONT";
    public String SUCCESS_REQUEST_BLOCK_SEC_CODE = "SUCC_BLOCK_TIME"; // for
                                                                      // block
                                                                      // time
                                                                      // between
                                                                      // two
                                                                      // successive
                                                                      // success
                                                                      // request
    public String DAILY_CNSTIVE_FAIL_COUNT_BEFOREBAR = "DA_CONFAIL_COUNT";// For
                                                                          // daily
                                                                          // consecutive
                                                                          // fail
                                                                          // count
                                                                          // allowed
                                                                          // before
                                                                          // getting
                                                                          // barred
    public String DAILY_SUCCESS_TXN_ALLOWED_COUNT = "DA_SUCTRAN_ALLWDCOUN";// Daily
                                                                           // success
                                                                           // transactions
                                                                           // allowed
    public String DAILY_FAIL_TXN_ALLOWED_COUNT = "DA_FAIL_TXN_ALLWDCOU";// Daily
                                                                        // failed
                                                                        // transactions
                                                                        // allowed
    public String DAILY_TOTAL_TXN_AMT_ALLOWED = "DA_TOTXN_AMT_ALLWDCO";// Daily
                                                                       // total
                                                                       // transactions
                                                                       // amount
                                                                       // allowed
    public String TAX2_ON_TAX1_CODE = "TAX2_ON_TAX1";// Whether Tax2 is to be on
                                                     // Tax1 or on requested
                                                     // value
    public String SUBSCRIBER_FAIL_CTINCR_CODES = "ERROR_FOR_FAIL_CT";// For
                                                                     // storing
                                                                     // the
                                                                     // codes
                                                                     // for
                                                                     // which
                                                                     // the fail
                                                                     // count
                                                                     // for
                                                                     // subscriber
                                                                     // needs to
                                                                     // be
                                                                     // increased
    public String C2S_PIN_BLK_RST_DURATION = "C2S_PIN_BLK_RST_DRTN";// Minutes
                                                                    // after
                                                                    // which
                                                                    // invalid
                                                                    // PIN
                                                                    // counter
                                                                    // will be
                                                                    // reset
    public String P2P_PIN_BLK_RST_DURATION = "P2P_PIN_BLK_RST_DRTN";// Minutes
                                                                    // after
                                                                    // which
                                                                    // invalid
                                                                    // PIN
                                                                    // counter
                                                                    // will be
                                                                    // reset
    public String C2S_DAYS_AFTER_CHANGE_PIN = "C2S_DYS_ATR_CNGE_PIN";// Days
                                                                     // after
                                                                     // Pin
                                                                     // CHange
                                                                     // will be
                                                                     // forced
    public String P2P_DAYS_AFTER_CHANGE_PIN = "P2P_DYS_ATR_CNGE_PIN";// Days
                                                                     // after
                                                                     // Pin
                                                                     // CHange
                                                                     // will be
                                                                     // forced

    public String TYPE_INTEGER = "INT";
    public String TYPE_LONG = "NUMBER";
    public String TYPE_AMOUNT = "AMOUNT";
    public String TYPE_BOOLEAN = "BOOLEAN";
    public String TYPE_STRING = "STRING";
    public String TYPE_DATE = "DATE";

    public String PIN_REQUIRED_CODE = "PIN_REQUIRED";
    public String PIN_LENGTH_CODE = "PIN_LENGTH";
    public String C2S_PIN_LENGTH_CODE = "C2S_PIN_LENGTH";

    public String SKEY_REQUIRED = "SKEY_REQUIRED";
    public String SKEY_DEFAULT_SENT_TO = "SKEY_DEFAULT_SENT_TO";

    public String SYSTEM_DATE_FORMAT = "SYSTEM_DATE_FORMAT";
    public String SYSTEM_DATETIME_FORMAT = "SYSTEM_DTTIME_FORMAT";
    public String PERIOD_DAYS_NUM_CODE = "5";

    public String USE_PPAID_USER_DEFINED_CONTROLS = "USE_PPAID_CONTROLS"; // whether
                                                                          // to
                                                                          // use
                                                                          // the
                                                                          // post
                                                                          // paid
                                                                          // countrols
                                                                          // for
                                                                          // to
                                                                          // go
                                                                          // for
                                                                          // service
                                                                          // class
                                                                          // controls
    public String LANGAUGES_SUPPORTED = "LANGAUGES_SUPPORTED"; // List of
                                                               // languages
                                                               // supported in
                                                               // the system
    public String C2S_USER_REGISTRATION_REQUIRED = "C2S_USER_REGTN_REQ";// Registration
                                                                        // required
                                                                        // in
                                                                        // C2S
    public String C2S_ALLOW_SELF_TOPUP = "ALLOW_SELF_TOPUP";// Allow self topup
                                                            // in C2S
    public String USE_C2S_SEPARATE_TRNSFR_COUNTS = "C2S_SEP_TRFR_COUNT";// whether
                                                                        // to
                                                                        // use
                                                                        // transfer
                                                                        // out
                                                                        // counts
                                                                        // for
                                                                        // C2C
                                                                        // and
                                                                        // C2S
                                                                        // separately
    public String DEFAULT_MESSGATEWAY = "DEFAULT_MESSGATEWAY";
    public String OPERATOR_UTIL_CLASS = "STU_OPTR_UTIL_C";
    public String NETWORK_STOCK_FIRSTAPPLIMIT = "FRSTAPPLM";
    public String NETWORK_STOCK_CIRCLE_MAXLIMIT = "CIRCLEMAXLMT";
    public String MAX_MSISDN_TEXTBOX_LENGTH = "MAX_MSISDN_TEXTBOX";
    public String C2S_SNDR_CREDIT_BK_AMB_STATUS = "C2S_AMB_CR_ALLOWED";
    public String P2P_SNDR_CREDIT_BACK_ALLOWED = "P2P_CR_BACK_ALLOWED";
    public String P2P_SNDR_CREDIT_BK_AMB_STATUS = "P2P_AMB_CR_ALLOWED";

    public String PASSWORD_BLK_RST_DURATION = "PWD_BLK_RST_DURATION";// Minutes
                                                                     // after
                                                                     // which
                                                                     // invalid
                                                                     // Password
                                                                     // counter
                                                                     // will be
                                                                     // reset
    public String MAX_PASSWORD_BLOCK_COUNT = "MAX_PWD_BLOCK_COUNT";// for
                                                                   // maximum
                                                                   // invalid
                                                                   // password
                                                                   // count
                                                                   // allowed
                                                                   // before
                                                                   // getting
                                                                   // blocked

    public String TRSFR_DEF_SRVCTYPE = "TRSFR_DEF_SRVCTYPE";// Default service
                                                            // type to be used
                                                            // if keyword is
                                                            // used as a common

    public String USER_HIERARCHY_SIZE = "USER_HIERARCHY_SIZE"; // Size of the
                                                               // user hierarchy
                                                               // for the
                                                               // channel user
                                                               // operations as
                                                               // transfer,resume,suspend
    public String DEFAULT_PRODUCT = "DEFAULT_PRODUCT"; // Size of the user
                                                       // hierarchy for the
                                                       // channel user
                                                       // operations as
                                                       // transfer,resume,suspend
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
    // public String C2S_TRANSFER_DEF_SELECTOR_CODE="DEF_SEL_CODE";
    // public String P2P_TRANSFER_DEF_SELECTOR_CODE="P2P_DFSL_CODE";
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

    // added for notification message by service class
    public String NOTIFICATION_SERVICECLASSWISE_SEN = "NOTIFI_SRVCCLS_SEN";
    public String NOTIFICATION_SERVICECLASSWISE_REC = "NOTIFI_SRVCCLS_REC";
    public String NOTIFICATION_SERVICECLASSWISE_REC_C2S = "NOTIF_SRVCCLS_RECC2S";
    // added for notification message by service class date 12/05/06
    public String CHK_BLK_LST_STAT = "CHK_BLK_LST_STAT";
    // added for default selecter for postpaid bill payment. date 15/05/06
    public String NOTIFICATION_SERVICECLASSWISE_REC_BILLPAY = "NOTIFSRVCLS_REC_BLPY"; // date
                                                                                      // 16/05/06
    public String C2S_ALLOW_SELF_BILLPAY = "ALLOW_SELF_BILLPAY";// date 17/05/06
    public String C2S_SNDR_CREDIT_BK_AMB_STATUS_BILLPAY = "C2S_AMB_CR_ALOW_PPBP";// date
                                                                                 // 17/05/06

    // preference for the C2C transfer to check the thresholds in the case of
    // uncontrolled transations
    public String SEP_OUTSIDE_TXN_CTRL = "SEP_OUTSIDE_TXN_CTRL";
    // public String C2S_DEF_SELECTOR_CODE_BILLPAY="DEF_SEL_CODE_BILLPAY";
    public String DEF_FRCXML_SEL_BILLPAY = "DEF_FRCXML_SEL_BLPY";

    public String POSTPAID_REGISTER_AS_ACTIVATED_STATUS = "PP_DEF_STATUS_ACT";
    public String SPACE_ALLOW_IN_LOGIN = "SPACE_ALLOW_IN_LOGIN";

    public String DEFAULT_WEB_GATEWAY_CODE = "DEF_WEB_GW_CODE"; // for the
                                                                // default
                                                                // gateway code
                                                                // for web
    public String SECOND_LANGUAGE_ENCODING = "SECOND_LANG_ENCODING"; // Encoding
                                                                     // to be
                                                                     // used for
                                                                     // messages
                                                                     // cache
    public String ICCID_CHECKSTRING = "ICCID_CHECKSTRING"; // FOR ICICID key
                                                           // checking

    public String GRPT_CTRL_ALLOWED = "GRPT_CTRL_ALLOWED";
    public String GRPT_CHRG_ALLOWED = "GRPT_CHRG_ALLOWED";
    public String GRPT_CONTROL_LEVEL = "GRPT_CONTROL_LEVEL";

    // Whether to allow Same MSISDN P2P transfer or not (Values =Y/N), for
    // Credit Transfer, post to pre,
    // pre to post - may be allowed in validity extension
    public String P2P_ALLOW_SELF_TOPUP = "P2P_ALLOW_SELF_TOPUP";// Allow self
                                                                // topup in P2P
    // entery for check the external code mandatory for the batch foc.
    public String EXTERNAL_CODE_MANDATORY_FORFOC = "EXT_CODE_MAND_FOC";

    public String SECOND_LANGUAGE_CHARSET = "SECOND_LANG_CHARSET"; // Encoding
                                                                   // to be used
                                                                   // for
                                                                   // message
                                                                   // Cache

    // new entry is added for the c2c return as return to the parent user only
    public String C2C_RET_PARENT_ONLY = "C2C_RET_PARENT_ONLY";
    // ennds here
    public String MAX_NO_OF_BUDDIES_ALLOWED = "MAX_BUDDIES_ALLOWED";
    public String DECIMAL_ALLOW_SERVICES = "DECIML_ALOW_SERVICES";

    // new entry for the date format of external system
    public String EXTERNAL_DATE_FORMAT = "EXTERNAL_DATE_FORMAT";
    public String XML_MAX_RCD_SUM_RESP = "XML_MAX_RCD_SUM_RESP";
    public String XML_DATE_RANGE = "XML_DATE_RANGE";
    public String XML_DFT_DATE_RANGE = "XML_DFT_DATE_RANGE";

    // added by sourabh
    public String C2S_ALLOW_SELF_UB = "C2S_ALOW_SLF_UTLTBIL";
    // public String C2S_DEF_SELECTOR_CODE_UB = "C2S_DEF_SEL_UTLTBIL";
    public String C2S_SNDR_CRDT_BK_AMB_UB = "C2S_S_CRBKAM_UTLTBIL";
    public String NOTIFICATION_SERVICECLASSWISE_REC_UB = "NOTIFY_SRVCCLS_REC";
    public String MIN_IDENTIFICATION_NUMBER_LENGTH = "MIN_ID_NUM_LNTH";
    public String MAX_IDENTIFICATION_NUMBER_LENGTH = "MAX_ID_NUM_LNTH";

    public String USE_HOME_STOCK = "USE_HOME_STOCK";

    public String OTHERID_PREFIX_LIST = "OTH_ID_PREFIX_LIST";
    public String IDENTIFICATION_NUMBER_VAL_TYPE = "ID_NUM_VAL_TYPE";
    public String ALPHA_ID_NUM_ALLOWED = "ALPHANUM_ID_NUM_ALWD";

    public String RESPONSE_IN_DISPLAY_AMT = "USE_DISPLAY_AMT";

    // VOMS integration start
    // entries for voucher change status
    public String MAX_DENOMINATION_VAL = "MAX_DENOMINATION_VAL"; // for EVD
                                                                 // system
    public String VOMS_UPEXPHOURS = "VOMS_UPEXPHOURS"; // Under process expiry
                                                       // hours
    public String VOMS_OFFPEAKHRS = "VOMS_OFFPEAKHRS"; // Off peak hours range
    public String VOMS_MAX_ERROR_COUNTEN = "VOMS_MAXERRORCOUNTEN";// Max errors
                                                                  // entries
                                                                  // allowed in
                                                                  // change
                                                                  // voucher
                                                                  // status with
                                                                  // enable
                                                                  // option
    public String VOMS_MAX_ERROR_COUNTOTH = "VOMS_MAXERRORCOUNTOT"; // Max
                                                                    // errors
                                                                    // entries
                                                                    // allowed
                                                                    // in change
                                                                    // voucher
                                                                    // status
                                                                    // without
                                                                    // enable
                                                                    // option
    public String VOMS_MAX_TOTAL_VOUCHER_EN = "VOMS_MAX_VOUCHER_EN"; // Maximum
                                                                     // no.of
                                                                     // total
                                                                     // vouchers
                                                                     // whose
                                                                     // status
                                                                     // can
                                                                     // changed
                                                                     // from
                                                                     // Enable
                                                                     // Screen
    public String VOMS_MAX_TOTAL_VOUCHER_OT = "VOMS_MAX_VOUCHER_OT"; // Maximum
                                                                     // no.of
                                                                     // vouchers
                                                                     // whose
                                                                     // status
                                                                     // can
                                                                     // changed
                                                                     // from
                                                                     // change
                                                                     // other
                                                                     // status
                                                                     // Screen
    public String VOMS_MAX_BATCH_DAY = "VOMS_MAXBATCHDY"; // maximum number of
                                                          // days of which batch
                                                          // is to be generated
    public String VOMS_DATE_FORMAT = "VOMS_DATE_FORMAT";
    public String SELF_VOUCHER_DISTRIBUTION_ALLOWED = "SLF_EVD_ALWD";
    public String PIN_SEND_TO = "PIN_SEND_TO";
    public String DELIVERY_RECEIPT_TRACKED = "DLRY_RCPT_TRK";
    public String CREDIT_BACK_ALWD_EVD_AMB = "CR_BK_ALW_EVD_AMB";
    public String NOTIFICATION_SERVICECLASSWISE_REC_EVD = "SR_WISE_MSG_EVD";
    // VOMS integration end
    public String BUDDY_PIN_REQUIRED = "BUDDY_PIN_REQ";
    // Added by Dhiraj on 07/03/2007
    public String FOC_SMS_NOTIFY = "FOC_SMS_NOTIFY";
    // added by vikas for associate profile module (mobinil ph2)
    public String PROFILEASSOCIATE_AGENT_PREFERENCES = "PRF_ASSOCIATE_AGENT";
    // For Credit Recharge Selector Code
    // public String P2P_CRRECH_DEF_SELECTOR_CODE="CR_RC_DEF_SEL";
    // Added for validating serial no and PIN with min and max values instead of
    // one fix value
    public String VOMS_SERIAL_NO_MIN_LENGTH = "VOMS_SNO_MIN_LENGTH"; // Min
                                                                     // serial
                                                                     // number
                                                                     // length
    public String VOMS_SERIAL_NO_MAX_LENGTH = "VOMS_SNO_MAX_LENGTH"; // Max
                                                                     // serial
                                                                     // number
                                                                     // length
    public String VOMS_PIN_MIN_LENGTH = "VOMS_PIN_MIN_LENGTH"; // Min PIN length
    public String VOMS_PIN_MAX_LENGTH = "VOMS_PIN_MAX_LENGTH"; // Max PIN length
    public String STAFF_USER_COUNT = "STAFF_USER_COUNT"; // Staff user count
    public String C2S_ENQ_BAL_FLAG = "C2S_ENQ_BAL_HIDE"; // if value is false in
                                                         // database then pre
                                                         // and post balance
                                                         // will be displayed on
                                                         // jsp.
    public String P2P_ENQ_BAL_FLAG = "P2P_ENQ_BAL_HIDE";
    public String PORT_USR_SUSPEND_REQ = "PORT_USR_SUSPEND_REQ";// mobile number
                                                                // portability
    public String MNP_ALLOWED = "MNP_ALLOWED";
    public String DIRECT_VOUCHER_ENABLE = "DCT_VOUCHER_EN";

    public String C2S_PROMOTIONAL_TRFRULE_CHECK = "C2S_PROMO_TRF_APP";
    public String PROMO_TRF_START_LVL_CODE = "PRO_TRF_ST_LVL_CODE";
    // MVD
    public String MVD_MAX_VOUCHER = "MVD_MAX_VOUCHER";

    // for Zebra and Tango added by sanjeew date 06/07/07
    public String PTUPS_MOBQUTY_MERGD = "PTUPS_MOBQUTY_MERGD";
    public String AUTO_PAYMENT_METHOD = "AUTO_PAYMENT_METHOD";

    // for OCI Changes added by Ashish S date 16/07/07
    public String LOW_BAL_MSGGATEWAY = "LOW_BAL_MSGGATEWAY";

    // For get number back service

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

    // BTRC implementation requires these thresholds for P2P-AshishK [03/10/07].
    public String WE_REC_AMT_ALLWD_P2P = "WE_REC_AMT_ALLWD_P2P";
    public String WE_SUCTRAN_ALLWD_P2P = "WE_SUCTRAN_ALLWD_P2P";
    public String MO_REC_AMT_ALLWD_P2P = "MO_REC_AMT_ALLWD_P2P";
    public String MO_SUCTRAN_ALLWD_P2P = "MO_SUCTRAN_ALLWD_P2P";
    public String MAX_ALLD_BALANCE_P2P = "MAX_ALLD_BALANCE_P2P";

    // for C2S-AshishK [03/10/07].

    public String WE_SUCTRAN_ALLWDCOUN = "WE_SUCTRAN_ALLWDCOUN";
    public String WE_TOTXN_AMT_ALLWDCO = "WE_TOTXN_AMT_ALLWDCO";
    public String MO_SUCTRAN_ALLWDCOUN = "MO_SUCTRAN_ALLWDCOUN";
    public String MO_TOTXN_AMT_ALLWDCO = "MO_TOTXN_AMT_ALLWDCO";
    public String MAX_ALLD_BALANCE_C2S = "MAX_ALLD_BALANCE_C2S";

    // To enquire Transaction Status with External Refrence Number -
    // Vipul[23/10/2007]
    public String C2S_REF_NUMBER_REQUIRED = "C2S_REF_NUM_REQ";
    public String C2S_REF_NUMBER_UNIQUE = "C2S_REF_NUM_UNIQUE";
    // pasword/pin History Management(used for checking last 'X' numbers of
    // password/pin from password History table)
    public String PREV_PASS_NOT_ALLOW = "PRV_PASS_NOT_ALLOW";
    public String PREV_PIN_NOT_ALLOW = "PRV_PIN_NOT_ALLOW";

    // When Payable amount is different from the Denomination Amount then
    // preference will be used to show the field on the denomination screen or
    // not
    // preference will be used at the time of message push
    public String PAYAMT_MRP_SAME = "PAYAMT_MRP_SAME";

    // added by PN for Cell Plus prepaid controller
    public String IS_FEE_APPLICABLE_FOR_VALIDATION_EXTENSION = "IS_FEE_APPL_VAL_EXT";
    public String IS_CREDIT_TRFR_WITH_VALIDITY_UPDATION_P2P = "IS_CT_WITH_VAL_UPDN";
    public String VAL_DAYS_TO_CHK_VALUPD = "VAL_DAYS_TO_CHK_VLUP";
    public String FEE_AND_VALIDITY_DAYS_TO_EXT = "FEE_VALDAYS_TO_EXT";

    // Used for automatically unblock pin or password
    public String C2S_PWD_BLK_EXP_DURATION = "C2S_PWD_BLK_EXP_DRN"; // password
                                                                    // expiry
                                                                    // period
    public String C2S_PIN_BLK_EXP_DURATION = "C2S_PIN_BLK_EXP_DRN";// Minutes
                                                                   // after
                                                                   // which
                                                                   // invalid
                                                                   // PIN
                                                                   // counter
                                                                   // will be
                                                                   // Expired
    public String P2P_PIN_BLK_EXP_DURATION = "CP2P_PIN_BLK_EXP_DRN";// Minutes
                                                                    // after
                                                                    // which
                                                                    // invalid
                                                                    // PIN
                                                                    // counter
                                                                    // will be
                                                                    // Expired

    // Added for saparate database for report on 26/02/08
    public String IS_SEPARATE_RPT_DB = "IS_SEPARATE_RPT_DB";
    public String ALLOW_SELF_EVR = "ALLOW_SELF_EVR";

    // Added for Negative Additional commission on 21/04/08
    public String NEG_ADD_COMM_APPLY = "NEG_ADD_COMM_APPLY";

    public String IS_SEPARATE_BONUS_REQUIRED = "IS_SEP_BONUS_REQUIRED";

    // Receiver message suppresion
    public String REC_MSG_SEND_ALLOW = "REC_MSG_SEND_ALLOW";

    // Operator user approval required or not.
    public String OPT_USR_APRL_LEVEL = "OPT_USR_APRL_LEVEL";
    // User Approval page mode flag(it can be editable or view).
    public String APPROVER_CAN_EDIT = "APPROVER_CAN_EDIT";

    // for enableing and dissabling the required button
    public String DISSABLE_BUTTON_LIST = "DISSABLE_BUTTON_LIST";

    public String P2P_REG_EXPIRY_PERIOD = "P2P_REG_EXPIRY_PERIOD";

    // If this flag is true then system will send SMS to primary number also if
    // transaction is done through secondary number
    public String MESSAGE_TO_PRIMARY_REQUIRED = "MESSAGE_TO_PRIMARY_REQUIRED";
    // If this flag is true then system will allow transaction through secondary
    // mobile number like C2C, O2C, view user details etc
    public String SECONDARY_NUMBER_ALLOWED = "SECONDARY_NUMBER_ALLOWED";

    // Added by Gopal on 10/11/2008
    public String C2C_SMS_NOTIFY = "C2C_SMS_NOTIFY";
    // added by vikas kumar for card group updation on 17/12/2008 sms/mms
    public String SMS_MMS_ALLOWED = "SMS_MMS_ALLOWED";
    public String CIRCLEMINLMT = "CIRCLEMINLMT";
    // Add for PIN validation is requird in CP2P services 07/01/2009
    public String CP2P_PIN_VALIDATION_REQUIRED = "CP2P_PIN_VALIDAT_REQ";
    // Added for self top up in fix recharge
    public String FIXLINE_RC_ALLOW_SELF_TOPUP = "ALLOW_SELF_TOPUP_FRC";// Allow
                                                                       // self
                                                                       // topup
                                                                       // in Fix
                                                                       // line
    // Added for self topup in boardband recharge
    public String BROADBAND_RC_ALLOW_SELF_TOPUP = "ALLOW_SELF_TOPUP_BRC";
    public String C2S_RANDOM_PIN_GENERATE = "C2S_RANDOM_PIN_GENERATE"; // generate
                                                                       // random
                                                                       // Pin
    public String WEB_RANDOM_PWD_GENERATE = "WEB_RANDOM_PWD_GENERATE"; // generate
                                                                       // random
                                                                       // Pin
    public String BATCH_USER_PASSWD_MODIFY_ALLOWED = "BATCH_USER_PASSWD_MODIFY_ALLOWED";// modify
                                                                                        // the
                                                                                        // password
                                                                                        // during
                                                                                        // batch
                                                                                        // user
                                                                                        // creation
                                                                                        // is
                                                                                        // allowed
                                                                                        // or
                                                                                        // not.
    public String RESET_PASSWORD_EXPIRED_TIME_IN_HOURS = "RESET_PWD_EXP_TIME_IN_HOURS";
    public String RESET_PIN_EXPIRED_TIME_IN_HOURS = "RESET_PIN_EXP_TIME_IN_HOURS";
    public String P2P_CARD_GROUP_SLAB_COPY = "P2P_CARD_GROUP_SLAB_COPY";
    public String C2S_CARD_GROUP_SLAB_COPY = "C2S_CARD_GROUP_SLAB_COPY";
    public String STK_REG_ICCID = "STK_REG_ICCID"; // TRUE if registration using
                                                   // ICCID otherwise FALSE for
                                                   // IMSI

    // Added by chetan for activation bonus
    public static String ACTIVATION_BONUS_MIN_REDEMPTION_AMOUNT = "ACT_BONUS_MIN_AMOUNT";
    public static String ACTIVATION_BONUS_REDEMPTION_DURATION = "ACT_BONUS_REDEM_DUR";

    public String VOLUME_CALC_ALLOWED = "VOLUME_CALC_ALLOWED";// Volume
                                                              // calculation
                                                              // allowed in
                                                              // activation
                                                              // bonus
    public String POSITIVE_COMM_APPLY = "POSITIVE_COMM_APPLY"; // To allow
                                                               // positive
                                                               // commission in
                                                               // the system
                                                               // during O2C and
                                                               // C2C

    // autogenerate password and pin on 08/06/2009
    public String AUTO_PWD_GENERATE_ALLOW = "AUTO_PWD_GENERATE_ALLOW";
    public String AUTO_PIN_GENERATE_ALLOW = "AUTO_PIN_GENERATE_ALLOW";
    // used to check the reset time after user creation only.
    public String PSWD_EXP_TIME_IN_HOUR_AFTER_CREATION = "PSWD_EXP_TIME_IN_HOUR_ON_CREATION";
    // this preference will be used to settle IAT ambiguous transaction in
    // receiver zebra.
    public String CHECK_REC_TXN_AT_IAT = "CHECK_REC_TXN_AT_IAT";
    // this preference will be used to check IAT is enabled in zebra or not.
    public String IS_IAT_RUNNING = "IS_IAT_RUNNING";
    public String POINT_CONVERSION_FACTOR = "POINT_CONVERSION_FACTOR";

    // VIKRAM VFE
    public String LAST_X_TRANSFER_STATUS = "LAST_X_TRANSFER_STATUS";
    public String SERVICE_FOR_LAST_X_TRANSFER = "SERVICE_FOR_LAST_X_TRANSFER";
    // Vikram
    public String C2S_RECHARGE_MULTIPLE_ENTRY = "C2S_RECHARGE_MULTIPLE_ENTRY";
    // customer enquiry
    public String LAST_X_CUSTENQ_STATUS = "LAST_X_CUSTENQ_STATUS";

    // added for multiple wallet
    public String MULTIPLE_WALLET_APPLY = "MULTIPLE_WALLET_APPLY";
    public String NETWORK_STOCK_FOC_FIRSTAPPLIMIT = "FRSTAPPFOCLM";
    public String NETWORK_STOCK_INC_FIRSTAPPLIMIT = "FRSTAPPINCLM";

    // added by santanu
    public String RVERSE_TRN_EXPIRY = "RVERSE_TRN_EXPIRY";
    public String ALWD_REVTXN_SERVICES = "ALWD_REVTXN_SERVICES";
    public String RVE_C2S_TRN_EXPIRY = "RVE_C2S_TRN_EXPIRY";
    public String CHNL_PLAIN_SMS_SEPT_LOGINID = "CHNL_PLAIN_SMS_SEPT_LOGINID";

    // Added for Direct Payout by Lohit
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

    // use this preference only for EN & DE CRYPTION TYPE for PIN PASS, not for
    // DB.
    public String PINPAS_EN_DE_CRYPTION_TYPE = "PINPAS_EN_DE_CRYPTION_TYPE";

    public String NWADM_CROSS_ALLOW = "NWADM_CROSS_ALLOW";
    public String STAFF_AS_USER = "STAFF_AS_USER";
    // Added by vikram
    // public String ZERO_BAL_THRESHOLD_VALUE="ZERO_BAL_THRESHOLD_VALUE";
    public String PRIVATE_RECHARGE_ALLOWED = "PRVT_RCHRG_ALLOW";
    public String NAMEEMBOSS_SEPT = "NAMEEMBOSS_SEPT";
    public String LOGIN_SPECIAL_CHAR_ALLOWED = "LOGIN_SPCL_CHAR_ALLOW";
    public String MVD_MIN_VOUCHER = "MVD_MIN_VOUCHER";
    public String VOMS_PIN_ENCRIPTION_ALLOWED = "VOMS_PIN_ENCRIPT_ALLOWED";
    public String CARD_GROUP_BONUS_RANGE = "CARD_GROUP_BONUS_RANGE";
    // added by nilesh : for default grade
    public String IS_DEFAULT_PROFILE = "IS_DEFAULT_PROFILE";
    // added by nilesh: for user delete approval
    public String REQ_CUSER_DLT_APP = "REQ_CUSER_DLT_APP";
    // O2C email notification
    public String O2C_EMAIL_NOTIFICATION = "O2C_EMAIL_NOTIFICATION";
    public String FOC_ODR_APPROVAL_LVL = "FOC_ODR_APPROVAL_LVL";
    // added by nilesh: for auto c2c transfer
    public String AUTO_C2C_TRANSFER_AMT = "AUTO_C2C_TRANSFER_AMT";
    public String SMS_ALLOWED = "SMS_ALLOWED";

    // MVD voucher upload limit for extgw
    public String MVD_MAX_VOUCHER_EXTGW = "MVD_MAX_VOUCHER_EXTGW";
    // added by priyanka 21/01/11 for mobilecom
    public String CRYSTAL_SUMMARY_REPORT_MAX_DATEDIFF = "RPTSUMM_MAX_DATEDIFF";
    // addded by jasmine kaur
    public String SID_ISNUMERIC = "SID_ISNUMERIC";

    public String MIN_SID_LENGTH_CODE = "MIN_SID_LENGTH";
    public String MAX_SID_LENGTH_CODE = "MAX_SID_LENGTH";

    // Added by Amit Raheja for NNP
    public String MSISDN_MIGRATION_LIST_CODE = "MSISDN_MIGRATION_LIST";
    // added by Babu Kunwar 15/02/2011 for C2S Remarks
    public String USER_EVENT_REMARKS = "USER_EVENT_REMARKS";
    // added by ankuj
    public String MIN_SID_LENGTH = "MIN_SID_LENGTH";
    public String MAX_SID_LENGTH = "MAX_SID_LENGTH";

    // Added for the SID services
    public String PRIVATE_SID_SERVICE_ALLOW = "PVT_SID_SERVICE_ALLOW";
    // Multiple credit transfer for dedicated account
    public String MULT_CRE_TRA_DED_ACC_SEP = "MULT_CRE_TRA_DED_ACC_SEP";

    public String EXTERNAL_TXN_MANDATORY_FORO2C = "EXTTXNMANDT_O2C";

    // entery for check the external code mandatory for the batch O2C.
    public String EXTERNAL_CODE_MANDATORY_FORO2C = "EXT_CODE_MAND_O2C";

    public String O2C_SMS_NOTIFY = "O2C_SMS_NOTIFY";

    // Added for DWH Changes by Anu garg
    public String RP2PDWH_OPT_SPECIFIC_PROC_NAME = "RP2PDWH_OPT_SPECIFIC_PROC_NAME";
    public String P2PDWH_OPT_SPECIFIC_PROC_NAME = "P2PDWH_OPT_SPECIFIC_PROC_NAME";
    public String IATDWH_OPT_SPECIFIC_PROC_NAME = "IATDWH_OPT_SPECIFIC_PROC_NAME";

    // Added for the prvate recharge messsafge short code
    public String PRIVATE_RECH_MESSGATEWAY = "PVT_RECH_MESSGATEWAY";

    public String PARENT_CATEGORY_LIST = "PARENT_CATEGORY_LIST";
    public String OWNER_CATEGORY_LIST = "OWNER_CATEGORY_LIST";

    // added for Channel User Debit/Credit Service API
    public String MAX_AUTO_FOC_ALLOW_LIMIT = "MAX_AUTO_FOC_ALLOW_LIMIT";

    // Added by Gaurav pandey: for autoFOCtranfer process
    public String AUTO_FOC_TRANSFER_AMOUNT = "AUTO_FOC_TRANSFER_AMOUNT";
    // added by gaurav pandey: for roam recharge

    public String ALLOW_ROAM_RECHARGE = "ALLOW_ROAM_RECHARGE";

    // Added for GiveMeBalance(GMB)
    // public String
    // INTERACTIVE_OPTION_ALLOWED="GMB_INTERACTIVE_OPTION_ALLOWED";
    // public String MENU_CODE="USSD_MENU_CODE";
    public String USSD_NEW_TAGS_MANDATORY = "USSD_TAGS_CELLID_SWITCHID_MANDATORY";
    public String PLAIN_RES_PARSE_REQUIRED = "PLAIN_RES_PARSE_REQUIRED";
    public String COUNTRY_CODE = "COUNTRY_CODE";

    // added by nilesh: MRP block time
    public String MRP_BLOCK_TIME_ALLOWED = "MRP_BLOCK_TIME_ALLOWED";
    public String LAST_SERVICE_TYPE_CHECK = "LAST_SERVICE_TYPE_CHECK";
    // Tunisia specific
    public String ACTIVATION_FIRST_REC_APP = "ACT_FRST_RCH_APP";

    // SOS Settlement
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
    // voms
    public String VOMS_MAX_APPROVAL_LEVEL = "VOMS_MAX_APPROVAL_LEVEL";// added
                                                                      // by
                                                                      // rahul
    public String VOMS_USER_KEY_REQD = "VOMS_USER_KEY_REQD";
    // added by nilesh : consolidated for logger
    public String DB_ENTRY_NOT_ALLOWED = "DB_ENTRY_NOT_ALLOWED";
    // Added by Amit Raheja for reverse txn
    public String RVERSE_TXN_APPRV_LVL = "RVERSE_TXN_APPRV_LVL";
    // added for vms in o2c
    public String VOUCHER_TRACKING_ALLOWED = "VOUCHER_TRACKING_ALLOWED";
    public String PROCESS_FEE_REV_ALLOWED = "PROCESS_FEE_REV_ALLOWED";
    public String VOUCHER_EN_ON_TRACKING = "VOUCHER_EN_ON_TRACKING";
    // Added for Multiple Credit List CR
    public String MCDL_MAX_LIST_COUNT = "MCDL_MAX_LIST_COUNT";
    public String MCDL_DIFFERENT_REQUEST_SEPERATOR = "MCDL_DIFF_REQST_SEP";
    public String P2P_MCDL_DEFAULT_AMOUNT = "P2P_MCDL_DEFAULT_AMOUNT";
    public String P2P_MCDL_MAXADD_AMOUNT = "P2P_MCDL_MAXADD_AMOUNT";
    public String P2P_MCDL_AUTO_DELETION_DAYS = "P2P_MCDL_AUTO_DELETION_DAYS";

    // for LMB debit API
    public String LMB_DEBIT_REQ = "LMB_DEBIT_REQ";
    // korek changes rahul.d
    public String PRVT_RC_MSISDN_PREFIX_LIST = "PRVT_RC_MSISDN_PREFIX_LIST";
    public String IS_FNAME_LNAME_ALLOWED = "IS_FNAME_LNAME_ALLOWED";// added by
                                                                    // deepika
                                                                    // aggarwal
                                                                    // for korek
    public String LAST_X_RECHARGE_STATUS = "LAST_X_RECHARGE_STATUS";
    public String SERVICE_FOR_LAST_X_RECHARGE = "SERVICE_FOR_LAST_X_RECHARGE";
    public String SMS_PIN_BYPASS_GATEWAY_TYPE = "SMS_PIN_BYPASS_GATEWAY";

    // CP User Registration
    public String CATEGORY_ALLOWED_FOR_CREATION = "CAT_ALLOW_CREATION";
    public String CP_SUSPENSION_DAYS_LIMIT = "CP_SUSPENSION_DAYS_LIMIT";
    // Added by harsh 24Jul12 :Entry for check the external code mandatory for
    // add/modify user (channel/operator)
    public String EXTERNAL_CODE_MANDATORY_FORUSER = "EXT_CODE_MAND_USER";

    // for reports after c2s_transfers table merging
    // public String MIGRATION_DATE="MIGRATION_DATE";
    // VASTRIX ADDED BY HITESH
    public String SIMACT_DEFAULT_SELECTOR = "SIMACT_DEFAULT_SELECTOR";
    public String SELECTOR_INTERFACE_MAPPING = "SELECTOR_INTERFACE_MAPPING";
    // To Encrypt/Decrypt the resonse message through External System: Added by
    // harsh
    public String EXT_VOMS_MSG_DES_ENDE_CRYPT_KEY = "EXT_VOMS_MSG_ENDEC_KEY";

    // BY ANUPAM MALVIYA FOR USER DEFAULT CONFIGURATION MANAGEMENT
    public String USR_DEF_CONFIG_UPDATE_REQ = "USR_DEF_CONFIG_UPDATE_REQ";
    // for loingId check like case senitive & special char
    public String LOGIN_ID_CHECK_ALLOWED = "LOGIN_ID_CHECK_ALLOWED";
    // For RSA authentication at User Category level
    public String RSA_AUTHENTICATION_REQUIRED = "RSA_AUTHENTICATION_REQUIRED";
    public String RSA_AUTHENTICATION_COUNT = "COUNT_TO_ASK_RSA_CODE";
    public String STAFF_USER_APRL_LEVEL = "STAFF_USER_APRL_LEVEL";// added by
                                                                  // Praveen
                                                                  // Kumar

    // For Transfer Rule Type at User level 24/09/2009
    public String TRF_RULE_USER_LEVEL_ALLOW = "TRF_RULE_USER_LEVEL_ALLOW";

    // For batch user initiate by channel users
    public String BATCH_USER_PROFILE_ASSIGN = "BATCH_USER_PROFILE_ASSIGN";
    public String BATCH_INTIATE_NOTIF_TYPE = "BATCH_INTIATE_NOTIF_TYPE";
    // Email for pin & password
    public String EMAIL_SERVICE_ALLOW = "EMAIL_SERVICE_ALLOW";

    // added for cos required
    public String COS_REQUIRED = "COS_REQUIRED";
    public String SERVICE_PROVIDER_PROMO_ALLOW = "SERVICE_PROVIDER_PROMO_ALLOW";

    // added for cell_group

    public String CELL_GROUP_REQUIRED = "CELL_GROUP_REQUIRED";

    // vastrix
    public String SRVC_PROD_MAPPING_ALLOWED = "SRVC_PROD_MAPPING_ALLOWED";
    public String SRVC_PROD_INTFC_MAPPING_ALLOWED = "SRVC_PROD_INTFC_MAPPING_ALLOWED";

    // cell id switch id
    public String CELL_ID_SWITCH_ID_REQUIRED = "CELL_ID_SWITCH_ID_REQUIRED";
    // multiple amount for vastrix
    public String MULTI_AMOUNT_ENABLED = "MULTI_AMOUNT_ENABLED";
    // for in promo
    public String IN_PROMO_REQUIRED = "IN_PROMO_REQUIRED";
    public String DEBIT_SENDER_SIMACT = "DEBIT_SENDER_SIMACT";
    // added by hitesh- LOGIN Password not allowed
    public String LOGIN_PASSWORD_ALLOWED = "LOGIN_PASSWORD_ALLOWED";
    // ///////////////auto o2c
    public String AUTO_O2C_AMOUNT = "AUTO_O2C_AMOUNT";
    public String AUTO_O2C_MAX_APPROVAL_LEVEL = "AUTO_O2C_MAX_APPROVAL_LEVEL";

    // Barred for Deletion added by shashank
    public String DAYS_TILL_USER_IS_BARRED = "BAR_FOR_DEL_DAYS";
    public String PER_DAY_LIMIT_BAR_FOR_DEL = "PER_DAY_BAR_FOR_DEL_LIMIT";
    public String REQ_CUSER_BAR_APPROVAL = "REQ_CUSER_BAR_APP";
    public String SRVCS_FOR_PROD_MAPPING = "SRVCS_FOR_PROD_MAPPING";

    // CAPTCHA Authentication
    public String SHOW_CAPTCHA = "SHOW_CAPTCHA";

    // Email for pin & password
    public String EMAIL_AUTH_REQ = "EMAIL_AUTH_REQ";
    public String MIN_HRDIF_ST_ED_LMS = "MIN_HRDIF_ST_ED_LMS";
    public String MIN_HRDIF_CR_ST_LMS = "MIN_HRDIF_CR_ST_LMS";
    public String LMS_APPL = "LMS_APPL";
    // added by harsh for setting default frequency in Schedule Credit List
    public String P2P_MCDL_ALLOWED_SCHEDULE_TYPE = "P2P_ALLOWED_SCHTYPE";
    public String P2P_MCDL_DEFAULT_FREQUENCY = "P2P_DEFAULT_NO_OF_SCHEDULES";
    // For Separate External Database- 18/12/13
    public String IS_SEPARATE_EXT_DB = "IS_SEPARATE_EXT_DB";
    // LMS Hierarchy-Wise Promotion- 27/12/13
    public String PROMOTION_HIERARCHY_WISE = "PROMOTION_HIERARCHY_WISE";
    public String LMS_VOL_COUNT_ALLOWED = "LMS_VOL_COUNT_ALLOWED";
    public String LMS_PROF_APR_ALLOWED = "LMS_PROF_APR_ALLOWED";

    public String AUTO_O2C_APPROVAL_ALLOWED = "AUTO_O2C_APPROVAL_ALLOWED";
    public String AUTH_TYPE_REQ = "AUTH_TYPE_REQ";
    public String SAP_INTEGARATION_FOR_USRINFO = "SAP_INTEGARATION_FOR_USRINFO";
    public String OTP_AUTHENTICATION_COUNT = "COUNT_TO_ASK_OTP_CODE";
    // Added By Vipan
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
    /*
     * //Diwakar
     * public String EXTSYS_USR_APRL_LEVEL_REQUIRED =
     * "EXTSYS_USR_APRL_LEVEL_REQUIRED";
     * //Ended
     * public String MAX_LAST_TRANSFERS_DAYS="MAX_LAST_TRANSFERS_DAYS";
     */

    // Added by Vikas Singh
    public String MAX_AUTOTOPUP_AMT = "MAX_AUTOTOPUP_AMT";
    // Ended here

    // Captcha Authentication at Login Page Start- by akanksha.
    public String INVALID_PWD_COUNT_FOR_CAPTCHA = "INVALID_PWD_COUNT_FOR_CAPTCHA";
    public String CAPTCHA_LENGTH = "CAPTCHA_LENGTH";
    // Captcha Authentication at Login Page E- by akanksha.
    /*
     * public String PAYMENTDETAILSMANDATE_O2C="PAYMENTDETAILSMANDATE_O2C";
     * 
     * public String
     * USER_CREATION_MANDATORY_FIELDS="USER_CREATION_MANDATORY_FIELDS";
     * 
     * public String USER_APPROVAL_LEVEL="USER_APPROVAL_LEVEL";
     */

    public String SELFTOPUPDWH_OPT_SPECIFIC_PROC_NAME = "SELFTOPUPDWH_OPT_SPECIFIC_PROC_NAME";
    public String AUTOSTU_NO_DAYS_ALERT = "AUTOSTU_NO_DAYS_ALERT";
    public String SELFTOPUP_TOKENIZATION = "SELFTOPUP_TOKENIZATION";

}
