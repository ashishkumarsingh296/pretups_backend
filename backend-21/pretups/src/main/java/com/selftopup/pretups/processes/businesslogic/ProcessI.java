/**
 * @(#)ProcessI.java
 *                   Name Date History
 *                   ----------------------------------------------------------
 *                   --------------
 *                   Ashish Kumar 22/04/2006 Initial Creation
 *                   ----------------------------------------------------------
 *                   --------------
 *                   Copyright (c) 2006 Bharti Telesoft Ltd.
 */

package com.selftopup.pretups.processes.businesslogic;

public interface ProcessI {

    public String STATUS_COMPLETE = "C";
    public String STATUS_UNDERPROCESS = "U";
    public String REST_SCH_TOPUP_PROCESSID = "SCHTOPUP";
    public String DWH_PROCESSID = "DWH";
    public String P2PDWH_PROCESSID = "P2PDWH";
    public String WHITE_LIST_PROCCESSID = "WLIST";
    public String C2SMIS = "C2SMIS";
    public String P2PMIS = "P2PMIS";
    public String NW_STK_MISMATCH = "NWSTKMISMATCH";
    public String CHNL_USR_BAL_MISMATCH = "CHNLUSRBALMISMATCH";
    public String VOUCHER_FILE_UPLOAD_PROCCESSID = "VOULIST";
    public String VOMS_INVENTORY_REPORT_PROCCESSID = "VMINVTRPT";
    public String VOUCHER_ALERT = "VOUCHERALERT";
    public String ROUTING_UPLOAD_PROCESSID = "ROUTUPLOAD";
    public String UBS = "UBS";
    public String DAILY_TRANSFER_PROCESSID = "DLYTRFALT";
    public String ADDITIONAL_COMMISION_DEDUCTION = "ADDCOMMDDT";
    public String HOURLY_COUNT_DETAIL_PROCESSID = "HRCNT";
    public String O2CEXT_TXN_PROCESSID = "O2CEXTTXN";
    public String CGR_PROCESSID = "CGR";
    public String ACTIVATION_BONUS_PROCESSID = "ACTBONUS";
    public String ACT_TXN_BONUS = "ACTTXN";
    public String ACT_VOLUME_BONUS = "ACTVOLUME";
    public String ACTIVATION_MAPPING = "ACTIVATIONMAPPING";
    // Added by Vinay on 20-March-09 for External User Data File
    public String EXTERAL_USRDATA_FILE_PROCESS_ID = "CRMUSRDATARPT";
    public String IAT_AMBIGUOUS_PROCESS = "IATAMG";
    public String IAT_DWH_PROCESSID = "IATDWH";
    // Process ID for Daily subscriber count and User balance movement process
    public String DAILY_SUBS_COUNT = "DLYSUBCNT";
    public String DWH_USER_BAL_MOV_PROCESSID = "USRBALMOVT";
    public String LOW_BAL_ALERT_PARENT = "LOWBALPRNT";
    public String USER_MIG_ID = "USERMIGRATION";// Process Id for UserMigration
                                                // Module : Puneet
    // added by nilesh:for auto c2c
    public String AUTO_C2C_TRANSFER_PROCESS = "AUTOC2C";
    // Added by Babu Kunwar for C2STransaction VFE
    public String TRANSACTION_ID = "RP2PTRANSDATA";

    // LMB
    public String SOS_SETTLEMENT_PROCESSID = "SOSSETTLE";
    public String SOSMIS = "SOSMIS";
    public String SOSDWH_PROCESSID = "SOSDWH";
    public String SOSVALIDITY = "SOSVALIDITY";
    public String VOMS_GEN = "VOMSGEN";

    // Added by Amit Raheja for Voucher MIS Process
    public String VMSMIS = "VMSMIS";

    // Added By Vikas Jauhari for CP User Suspension Process
    public String CPUSER_SUSPENSION_PROCESS = "CPUSRSUSP";

    // Batch Grade Management
    public String BATCH_USER_GRADE_ASSOCIATION_PROCESS_ID = "BTCUSRGDASCPRO";
    // auto O2C process

    public String Auto_O2C_Process = "AUTOO2CPROCESS";

    // for barred for deletion
    public String BAR_FOR_DELETE = "BARFORDEL";
    // LMS Promotion Process
    public String LMS_PROMO_MSG = "LMSPROMO";
    // LMS Target Credit
    public String LMS_TARGET_CREDIT = "LMSCREDIT";
    public String LMSUPLOADLP = "LMSUPLOADLP";
    public String LMSREFTCAL = "LMSREFTCAL";
    public String O2C_SEND_MAIL_PROCESS = "O2CMAILPROCESS";
    // Added By Diwakar for POS process on monthly basis
    public String POS_MIS = "POSMONTHLYREPORT";
    public String SMS_TO_CHANNEL_ADMIN_USERS_HOURLY = "SMS2CHANNELADMINUSER";
    public String SMS_TO_CHANNEL_ADMIN_USERS_TILL_YESTERDAY_FROM_START_DAY_MONTH = "SMS2CHADMUSERM";
    public String NEW_USERS_APPROVED_CSV = "NEWUSERSAPPROVED"; // As per OCI CR
    public String LMS_REF_CAL = "LMSREFTGT";

    public String SELF_TOPUP_SCHEDULED_CREDIT_TRANSFER = "SLFSCCRDTTRF";
    public String SELEF_TOPUP_DWH_PROCESSID = "SLFTPDWH";
}
