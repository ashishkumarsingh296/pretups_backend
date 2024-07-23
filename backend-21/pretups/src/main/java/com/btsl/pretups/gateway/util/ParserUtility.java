package com.btsl.pretups.gateway.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.BeanUtils;

import com.btsl.blutil.BLConstants;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ExtUserDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.txn.pretups.channel.transfer.businesslogic.ChannelTransferTxnDAO;

/**
 * @(#)ParserUtility.java
 *                        Copyright(c) 2006, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 * 
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Gurjeet Bedi Nov 16, 2006 Initial Creation
 *                        Kapil Mehta Feb 03, 2009 Modification
 *                        Harpreet Kaur OCT 18, 2011 Modification
 *                        Utility Class for all parser class
 * 
 */

public abstract class ParserUtility implements GatewayParsersI {

    private static final Log LOG = LogFactory.getLog(ParserUtility.class.getName());
    private static final String CLASSNAME = "ParserUtility";
    public static final int ACTION_ACCOUNT_INFO = 0;
    public static final int CREDIT_TRANSFER = 1;
    public static final int CHANGE_PIN = 2;
    public static final int NOTIFICATION_LANGUAGE = 3;
    public static final int HISTORY_MESSAGE = 4;
    public static final int CREDIT_RECHARGE = 5;
    public static final int SUBSCRIBER_REGISTRATION = 6;
    public static final int SUBSCRIBER_DEREGISTRATION = 7;
    public static final int P2P_SERVICE_SUSPEND = 8;// P2P service Suspend
    public static final int P2P_SERVICE_RESUME = 9;// P2P service Resume
    public static final int ADD_BUDDY = 10;
    public static final int DELETE_BUDDY = 11;
    public static final int LIST_BUDDY = 12;
    public static final int LAST_TRANSFER_STATUS = 13;
    // added for Last Transfer Status(CP2P) 03/05/07
    public static final int SELF_BAR = 14;

    // added for Delete Subscriber List by harsh on 09Aug12
    public static final int DELETE_MULTLIST = 15;

    public static final int ACTION_CHNL_ACCOUNT_INFO = 0;
    public static final int ACTION_CHNL_CREDIT_TRANSFER = 1;
    public static final int ACTION_CHNL_CHANGE_PIN = 2;
    public static final int ACTION_CHNL_NOTIFICATION_LANGUAGE = 3;
    public static final int ACTION_CHNL_HISTORY_MESSAGE = 4;
    public static final int ACTION_CHNL_TRANSFER_MESSAGE = 5;
    public static final int ACTION_CHNL_RETURN_MESSAGE = 6;
    public static final int ACTION_CHNL_WITHDRAW_MESSAGE = 7;
    public static final int ACTION_CHNL_POSTPAID_BILLPAYMENT = 8;
    public static final int ACTION_CHNL_O2C_INITIATE = 9;
    public static final int ACTION_CHNL_O2C_INITIATE_TRFR = 10;
    public static final int ACTION_CHNL_O2C_RETURN = 11;
    public static final int ACTION_CHNL_O2C_WITHDRAW = 12;
    public static final int ACTION_CHNL_EXT_RECH_STATUS = 13;
    public static final int ACTION_CHNL_EXT_CREDIT_TRANSFER = 14;
    public static final int ACTION_CHNL_BALANCE_ENQUIRY = 16; // added for
    // Balance Enquiry
    // 03/05/07
    public static final int ACTION_CHNL_DAILY_STATUS_REPORT = 17; // added for
    // Daily
    // Status
    // Report
    // 03/05/07
    public static final int ACTION_CHNL_LAST_TRANSFER_STATUS = 18; // added for
    // Last
    // Transfer
    // Status(RP2P)
    // 03/05/07
    public static final int ACTION_CHNL_EVD_REQUEST = 19;
    public static final int ACTION_MULTIPLE_VOUCHER_DISTRIBUTION = 20;// Multiple
    // Voucher
    // Distribution
    public static final int ACTION_UTILITY_BILL_PAYMENT = 21;// Utility Bill
    // Payment
    public static final int ACTION_CHNL_EXT_TRANSFER_BILL_PAYMENT = 22;// for
    // C2S
    // Bill
    // payment
    public static final int ACTION_CHNL_EXT_ENQUIRY_REQUEST = 23; // for c2s
    // enquiry
    public static final int ACTION_CHNL_EXT_POST_RECHARGE_STATUS = 24; // for
    // post
    // recharge
    // status
    public static final int ACTION_CHNL_EXT_COMMON_RECHARGE = 25; // for common
    // recharge
    // request
    public static final int ACTION_CHNL_GIFT_RECHARGE_XML = 26; // for Gift
    // Recharge
    // through XML
    // API 23/04/08
    public static final int ACTION_CHNL_GIFT_RECHARGE_USSD = 27; // for Gift
    // Recharge
    // through USSD
    // 23/04/08
    public static final int ACTION_CHNL_BAL_ENQ_XML = 28;
    public static final int ACTION_CHNL_EVD_XML = 29;
    public static final int ACTION_C2C_TRANSFER_EXT_XML = 30;
    public static final int ACTION_C2C_RETURN_EXT_XML = 31;
    public static final int ACTION_C2C_WITHDRAW_EXT_XML = 32;
    public static final int ACTION_EXT_C2SCHANGEPIN_XML = 33;// for C2S Change
    // Pin through XML
    // API
    public static final int ACTION_CHNL_CREDIT_TRANSFER_CDMA = 34;// Added for
    // CDMA
    // Recharge
    public static final int ACTION_CHNL_CREDIT_TRANSFER_PSTN = 35;// Added for
    // PSTN
    // Recharge
    public static final int ACTION_CHNL_CREDIT_TRANSFER_INTR = 36;// Added for
    // INTERNET
    // Recharge
    public static final int ACTION_CHNL_EXT_CREDIT_TRANSFER_CDMA = 37;// Added
    // for
    // CDMA
    // Bank
    // Recharge
    public static final int ACTION_CHNL_EXT_CREDIT_TRANSFER_PSTN = 38;// Added
    // for
    // PSTN
    // Bank
    // Recharge
    public static final int ACTION_CHNL_EXT_CREDIT_TRANSFER_INTR = 39;// Added
    // for
    // INTR
    // Bank
    // Recharge
    public static final int ACTION_CHNL_ORDER_CREDIT = 40;// Added for
    // ORDER_CREDIT
    public static final int ACTION_CHNL_ORDER_LINE = 41;// Added for ORDER_LINE
    public static final int ACTION_CHNL_BARRING = 42;// Added for BARRING

    public static final int ACTION_CHNL_EXT_VAS_SELLING = 43; // for vas selling
    // CRBT
    public static final int ACTION_CHNL_IAT_ROAM_RECHARGE = 44; // for IAT roam
    // recharge
    public static final int ACTION_CHNL_IAT_INTERNATIONAL_RECHARGE = 45; // for
    // IAT
    // international
    // recharge

    public static final int ACTION_CHNL_C2S_LAST_XTRANSFER = 46; // For last 3
    // c2s transfer
    // report
    public static final int ACTION_CHNL_CUST_LAST_XTRANSFER = 47; // For last 3
    // C2S/C2C/BOTH
    // transfer
    // status
    public static final int ACTION_EXT_LAST_XTRF_ENQ = 48;
    public static final int ACTION_EXT_CUSTOMER_ENQ_REQ = 49;
    public static final int ACTION_EXT_OTHER_BAL_ENQ = 50;
    public static final int ACTION_EXT_LAST_TRF = 51;
    public static final int ACTION_EXT_DAILY_STATUS_REPORT = 52;
    public static final int ACTION_EXT_MVD_DWNLD_RQST = 53; // Changes done by
    // ashishT for MVD
    // voucher download.
    public static final int ACTION_C2S_TRANS_ENQ = 54;
    public static final int ACTION_REGISTER_SID = 55;
    public static final int SUSPEND_RESUME_CUSR = 56; // Suspend or resume
    // channel user.
    public static final int ACTION_EXT_USER_CREATION = 57;
    public static final int ACTION_ENQUIRY_TXNIDEXTCODEDATE = 58;
    public static final int ACTION_DELETE_SID_REQ = 59;// added by ankuj for SID
    // deletion
    public static final int ACTION_ENQUIRY_SID_REQ = 60;// added by ankuj for
    // SID enquiry
    public static final int ACTION_CRBT_REGISTRATION = 61;// added for CRBT
    // Registration
    public static final int ACTION_CRBT_SONG_SELECTION = 62;// added for CRBT
    // Song Selection
    public static final int ACTION_P2P_CRIT_TRANS = 63; // added for Multiple
    // credit recharge
    public static final int ACTION_ELECTRONIC_VOUCHER_RECHARGE = 64; // Changes
    // done by
    // Harpreet
    // for EVR
    public static final int ACTION_CHNL_EVR_XML = 65;// added for EVR through
    // XML API
    public static final int ACTION_EXT_PRIVATERC_XML = 66;// added for Private
    // Recharge through
    // External gateway
    public static final int ACTION_EXT_DRCR_C2C_CUSER = 67;// added for DrCr
    // Transfer through
    // External Gateway
    public static final int P2P_GIVE_ME_BALANCE = 68;// Added for GiveMeBalance
    public static final int ACTION_SUSPEND_RESUME_CUSR_EXTGW = 69;// ADDED FOR
    // SUSPEND
    // RESUME
    // CHANNEL
    // USER
    // THROUGH
    // EXTERNAL
    // GATEWAY
    public static final int P2P_LEND_ME_BALANCE = 70;// Added for LendMeBalance
    public static final int ACTION_MULT_CDT_TXR_LIST_AMD = 71; // Added for
    // Adding,Modifying,deleting
    // Multiple
    // Credit
    // Transfer List
    public static final int ACTION_MULT_CDT_TXR_LIST_VIEW = 72; // Added for
    // view list for
    // Multiple
    // Credit
    public static final int ACTION_MULT_CDT_TXR_LIST_REQUEST = 73; // Added for
    // credit to
    // MCDL
    public static final int LMB_ONLINE_DEBIT = 74; // Added for LMB debit online
    // API
    public static final int ACTION_C2S_RPT_LAST_XTRANSFER = 75;// rahul for
    // korek
    // added for VAS and promoVAS
    public static final int ACTION_VAS_RC_REQUEST = 76; // VASTRIX Added by
    // hitesh
    public static final int ACTION_EXTVAS_RC_REQUEST = 77; // VASTRIX Added by
    // hitesh
    public static final int ACTION_PVAS_RC_REQUEST = 78; // VASTRIX Added by
    // hitesh
    public static final int ACTION_EXTPVAS_RC_REQUEST = 79; // VASTRIX Added by
    // hitesh
    // added for DMS
    public static final int ACTION_EXT_USERADD_REQUEST = 80;// for External User
    // Addition
    public static final int ACTION_EXT_GEOGRAPHY_REQUEST = 81;// For DMS
    // Configuration.7/Nov/2012
    public static final int ACTION_EXT_TRF_RULE_TYPE_REQ = 82;// for DMS
    // Configuration.

    public static final int ACTION_CRM_USER_AUTH_XML = 83; // added by shashank
    // for channel user
    // authentication.17/Jan/2013
    // sim activate
    public static final int ACTION_USSD_SIM_ACT_REQ = 84;
    // added by Sonali Garg to enquire for a subscriber at IN
    public static final int ACTION_EXT_SUBENQ = 85;

    // added by harsh for Scheduled Credit Transfer (Add/Modify/Delete API)
    public static final int ACTION_MULT_CDT_TXR_SCDLIST_AMD = 86;
    // added by Vikas Kumar for Scheduled Credit Transfer Service
    public static final int SCHEDULE_CREDIT_TRANSFER = 87;
    // added by Pradyumn Mishra for Scheduled Credit Transfer (View/Delete
    // Complete Subscriber List)
    public static final int ACTION_MULT_CDT_TXR_SCDLIST_DLT = 88;
    public static final int ACTION_MULT_CDT_TXR_SCDLIST_VEW = 89;
    public static final int ACTION_CHNL_HLPDESK_REQUEST = 90; // added by
    // arvinder for
    // HelpDesk
    // Request//
    public static final int ACTION_EXT_HLPDESK_REQUEST = 91;// added by arvinder
    // for HelpDesk
    // Request External
    // Gateway//
    // added by akanksha for peru claro update
    public static final int ACTION_O2C_SAP_ENQUIRY = 92;
    public static final int ACTION_O2C_SAP_EXTCODE_UPDATE = 93;
    public static final int ACTION_COL_ENQ = 94;
    public static final int ACTION_COL_BILLPAYMENT = 95;

    public static final int ACTION_DTH = 96;
    public static final int ACTION_DC = 97;
    public static final int ACTION_PMD = 98;
    public static final int ACTION_PIN = 99;
    public static final int ACTION_BPB = 100;
    public static final int ACTION_FLRC = 101;
    public static final int ACTION_C2S_POSTPAID_REVERSAL = 102;
    // PPBENQ :rahul.d
    public static final int ACTION_EXT_PPBENQ = 103;
    public static final int ACTION_EXT_EVD_RC_POS = 104;

    // Added By Diwakar for ROBI
    // Request Type
    public static final String ADD_USER_REQ = "USERADDREQ";
    public static final String MODIFY_USER_REQ = "USERMODREQ";
    public static final String DELETE_USER_REQ = "USERDELREQ";
    public static final String SUSPEND_RESUME_USER_REQ = "USERSRREQ";
    public static final String CHANE_PASSWORD_REQ = "EXTCNGPWDREQ";
    public static final String ADD_DELETE_USER_ROLE_REQ = "EXTCNGROLEREQ";
    public static final String MNP_REQ = "UPLOADMNPFILEREQ";
    public static final String ICCID_MSISDN_MAP_REQ = "ICCIDMSISDNMAPREQ";
    public static final String C2C_REV_REQ ="C2CREVREQ";
    public static final String O2C_REV_REQ ="O2CREVREQ";
    public static final String VOMS_STCH_REQ ="VOMSSTCHGREQ";
    

    // Request Action for request Type
    public static final int ADD_USER_ACTION = 105;
    public static final int MODIFY_USER_ACTION = 106;
    public static final int DELETE_USER_ACTION = 107;
    public static final int SUSPEND_RESUME_USER_ACTION = 108;
    public static final int ADD_DELETE_USER_ROLE_ACTION = 109;
    public static final int CHANGE_PASSWORD_ACTION = 110;
    public static final int MNP_ACTION = 111;
    public static final int ICCID_MSISDN_MAP_ACTION = 112;
    // Ended By Diwakar for ROBI

    // /Added by sonali for self topup user registration
    public static final int ACTION_SELF_TOPUP_USER_REGISTRATION = 113;
    public static final int ACTION_SELF_TOPUP_CHANGE_PIN = 114;
    public static final int ACTION_SELF_TOPUP_CARD_REGISTRATION = 117;

    // Added by Vikas Singh for CARD modify, delete & view

    // Request Keywords
    public static final String CARD_MODIFY_REQ = "STPMCREQ";
    public static final String CARD_DELETE_REQ = "STPDCREQ";
    public static final String CARD_VIEW_REQ = "STPVCREQ";
    // Request Action for request type for CARD: modify, delete & view
    public static final int ACTION_CARD_MODIFY = 115;
    public static final int ACTION_CARD_DELETE = 116;
    public static final int ACTION_SELF_TOPUP_RECHARGE_USING_REG_CARD = 117;
    // added by Vikas Singh for prepaid reversal
    public static final int ACTION_C2S_PRE_PAID_REVERSAL = 118;
    public static final int ACTION_CHNL_EXT_ROAM_RECHARGE = 119;
    public static final int ACTION_CHNL_EXT_WARRANTY_TRANSFER = 121;
    public static final int ACTION_CHNL_EXT_ADVANCE_TRANSFER = 122;
    public static final int ACTION_CHNL_EXT_CAUTION_TRANSFER = 123;
    // added by Brajesh Prasad for LMS Points Enquiry Through External
    // Gateway(USSD and EXTGW)
    public static final int ACTION_CHNL_LMS_POINTS_ENQUIRY = 124;
    // added by Brajesh Prasad for LMS Points Enquiry Through External
    // Gateway(USSD and EXTGW)
    public static final int ACTION_CHNL_LMS_POINTS_REDEMPTION = 125;
    public static final int GET_MY_MSISDN = 126;
    public static final int ACTION_EXT_C2SCHANGEMSISDN = 127; // for MSISDN
    // Change
    // Functionality
    public static final int ACTION_EXT_C2SRECHARGESTATUS = 128; // for ETU
    // Change
    // Recharge
    // Status
    // Functionality
    // Added by Zeeshan Aleem
    public static final String VOUCHER_CONSUMPTION = "VOMSCONSREQ";
    public static final int ACTION_VOUCHER_CONSUMPTION = 129;
    public static final int GET_CHNL_USR_INFO = 130;
    public static final String VOUCHER_CONSUMPTION_O2C = "VCO2CREQ";
    public static final int ACTION_VOUCHER_CONSUMPTION_O2C = 131;
    // ADDED FOR VOMS
    public static final int ACTION_VOMS_ENQ = 132;
    public static final int ACTION_VOMS_CON = 133;
    public static final int ACTION_VOMS_RET = 134;

    // added for voucher query and rollback request
    public static final int ACTION_VOMS_QRY = 135;
    public static final int ACTION_VOMS_ROLLBACK = 136;
    public static final int ACTION_VOMS_RETAGAIN = 137;

    // added for Voucher Retrieval RollBack Request
    public static final int ACTION_VOMS_RETRIEVAL_ROLLBACK = 138;
    // Added for Self TPIN Reset
    public static final int ACTION_INITIATE_PIN_RESET = 139;
    public static final int ACTION_PIN_RESET = 140;
    public static final int ACTION_DATA_UPDATE = 141;
    public static final int ACTION_CUST_C2S_ENQ_REQ = 142;

    public static final int ACTION_CHNL_EXT_ROAM_RECHARGE_REVERSAL = 162;
	public static final int ACTION_EXT_LAST_XTRF_SRVCWISE_ENQ=143;//Added for Last X Transfer Service Wise
	 public static final int IAT_P2P_RECHRG=144; //Added by Zeeshan for IAT P2P Recharge
		//added for CP2PDATA
		public static final int ACTION_DATA_CP2P_RECHARGE = 145;
		//Lite Recharge
		public static final int ACTION_CHNL_LITE_RECHARGE=153;
		public static final int ACTION_CHNL_CARDGROUP_ENQUIRY_REQUEST=154;
		public static final int ACTION_CHNL_CURRENCY_CONVERSION_REQUEST=155;
		//Promo VAS
		public static final int ACTION_EXTPROMOPVAS_REQUEST=104;
		public static final int ACTION_EXTPROMOINTLTRFREQ_REQUEST = 163; //Added for INTERNATIONAL Recharge API
	
		public static final int ACTION_MULTICURRENCY_RECHARGE =164;
				
//added for channel user movement
		public static final int ACTION_EXT_USER_TRANSFER=165;
			//Added for Data Recharge
		public static final String DATA_RECHARGE = "EXDATATRFREQ";
		public static final int ACTION_EXDATATRFREQ_REQUEST = 166;
		public static final int ACTION_EXT_DSR_REQUEST = 167;
		public static final int ACTION_EXT_STOCK_BALANCE_REQUEST = 168;
		public static final int ACTION_CHNL_EXT_BULK_RCH_REVERSAL=170;
		public static final int ACTION_CHNL_EVR_UMNIAH_XML = 171;// added for EVR through
		
		public static final int ACTION_VOUCHER_STATUS_CHANGES=172;//added for status Change API
		public static final int ACTION_VOUCHER_EXPIRY_CHANGES=215;//added for expiry Change API
		//added for SOS Settlement request
		public static final int ACTION_CHNL_SOS_SETTLEMENT_REQUEST = 173;
		//added for SOS request
		public static final int ACTION_CHNL_SOS_REQUEST = 174;
		
		public static final int ACTION_VAS_VOUCHER_CONSUMPTION = 175;

		
		//sayyed yasin
		public static final int ACTION_LAST_X_TRF_OPT_CHANL= 176;
		
	    public static final String CP2P_DATA_TRANSFER = "CCDATATRFREQ";
	    public static final int ACTION_DTH_ENQUIRY_REQUEST = 181;
		public static final int ACTION_DTH_PAYMENT_REQUEST = 182;
        public static final int ACTION_C2S_TXNID_STAT = 183;
        public static final int ACTION_LIST_RET = 184;

		  
    public static final String ADD_CHANNEL_USER_REQ = "ADDUSERREQ";
    public static final String MODIFY_CHANNEL_USER_REQ = "MODIFYUSERREQ";
	
    public static final int ADD_CHANNEL_USER_ACTION = 175;
    public static final int MODIFY_CHANNEL_USER_ACTION = 176;
	
	//added for Vietnam STK.
    public static final int ACTION_BALANCE=177;
    public static final int ACTION_CHGMPIN=178;
    public static final int ACTION_CASHTRANSFER=179;
    public static final int ACTION_RELOAD=180;
	
	public static final int ACTION_TPS_MAX_CALCULATION=181;	  
    //end fro Vietnam STK.
		
    public static final int TXN_ENQUIRY=185;
    
    public static final int LAST_TXN_STATUS_SUBSCRIBER=186;
    
    public static final int C2S_SUMMARY_ENQUIRY=187;
    //Added for O2C/C2C Txn Enq
  	public static final int ACTION_C2C_O2C_TXN_STATUS = 188;
  	//Added for Channel User Commission Earner Enq
  	public static final int ACTION_USER_COMM_EARNED = 189;
  	
  	public static final int ACTION_WIRELESS_RC_REQUEST = 214;
  	public static final int ACTION_OLORECHARGE_REQUEST=194;
	public static final int ACTION_CHNL_O2CAPRL_REQUEST = 190;
	public static final int ACTION_USER_CARDGROUP_ENQUIRY_REQUEST = 193;
	//Added for BFaso
	public static final int ACTION_CHNL_ADD_CHNL_USER=146;
	public static final int ACTION_PRODUCT_RECHARGE_REQ=147;
	public static final int ACTION_GMB = 148;
	public static final String SOS_FLAG_UPDATE_REQ="SOSFLAGUPDATEREQ";
    public static final int ACTION_SOS_FLAG_UPDATE_REQ=194;
 ///////////VHA START////////////////
    public static final String VOMS_VALIDATE_REQ = "VOMSVALREQ";
    public static final String VOMS_RESERVE_REQ = "VOMSRSVREQ";
    public static final String VOMS_DIRECT_CONSUMPTION_REQ = "VOMSDCONSREQ";
    public static final String VOMS_EXPIRY_EXTENTION_REQ = "VOMSEXPEXTREQ";
    public static final String VOMS_ROLLBACK_REQ = "VOMSROLLBACKREQ";
    
    public static final int ACTION_VOMS_VALIDATE_REQ = 200;
    public static final int ACTION_VOMS_RESERVE_REQ = 196;
    public static final int ACTION_VOMS_DIRECT_CONSUMPTION_REQ = 197;
    public static final int ACTION_VOMS_EXPIRY_EXTENTION_REQ = 198;
    public static final int ACTION_VOMS_DIRECT_ROLLBACK_REQ = 199;
    /////////////VHA END//////////////////////////////////////////
    public static final int ACTION_VMS_PIN_EXP_EXT_REQ=195;
    public static final String VMS_PIN_EXP_EXT_REQ="VMSPINEXT";
    public static final String EXT_DVD_REQ="EXDVDREQ";

    /////////////SUBSCRIBER VOUCHER ENQUIARY //////////
    public static final String MY_VOUCHR_ENQUIRY_SUBSCRIBER_REQ="SELFVCRENQREQ";
    public static final int ACTION_VOMS_MY_VOUCHR_ENQUIRY_SUBSCRIBER_REQ=203;


    public static final int ACTION_CHNL_VOUCHER_AVAILABILITY_XML = 201;
    
    public static final int ACTION_CHNL_DVD_XML = 204;
    
  //added by Ashish for VIL 
    public static final int ACTION_VOMS_O2C=205;
    public static final int ACTION_VMS_PIN_CONSUME_REQ= 207;
    public static final String VMS_PIN_CONSUME_REQ="VOMSRCREQ";
    public static final String ACTION_C2C_APPR_REQ = "C2CAPPR";
    public static final int ACTION_C2C_REQ_REC=208;
    public static final int ACTION_C2C_APPR=209;
    public static final int ACTION_C2C_VOMS_TRF=210;
    public static final int ACTION_C2C_VOUCHER_APPR=211;
    public static final int ACTION_C2C_VOMS_INI=213;
    
    public static final int ACTION_UPUSRHRCHY=216;
    public static final int CHANNEL_USER_DETAILS=217;
    public static final int ACTION_PSBKRPT= 220;
    public static final int C2C_BUY_ENQ= 218;
    public static final int C2S_SV_DETAILS= 219;
    public static final int C2S_TOTAL_TXN= 221;
    public static final int C2S_TOTAL_TRANSACTION_COUNT=222;
    public static final int TOTAL_USER_INCOME_DETAILS_VIEW = 230;
    public static final int C2S_PROD_TXN_DETAILS = 223;
    public static final int PASSBOOK_VIEW_DETAILS=224;
    public static final int C2S_N_PROD_TXN_DETAILS = 225;

    public static final int TOTAL_TRANSACTION_DETAILED_VIEW=226;
    public static final int COMMISSION_CALCULATOR=227;
    /*public static final String ACTION_C2C_TRANSFER_EXT_XML="VOMS";*/
    //ended by Ashish for VIL
    public static final int ACTION_VOU_PRF_MOD_REQ= 212;
    public final static int ACTION_SUBS_THR_ENQ_REQUEST=231; //Added for Subscriber threshold enquiry
    
    
    //added for Robi Retailor Loan CR
    public final static String LOAN_OPTIN_REQ="LOANOPTINREQ";
    public final static String SERVICE_LOAN_OPTIN_REQ="LOANOPTIN";
    public final static int ACTION_LOAN_OPTIN_REQ=251;
    public final static String LOAN_OPTOUT_REQ="LOANOPTOUTREQ";
    public final static String SERVICE_LOAN_OPTOUT_REQ="LOANOPTOUT";
    public final static int ACTION_LOAN_OPTOUT_REQ=254;
    
    public final static String LST_LOAN_ENQ = "LSTLOANENQREQ";
    public final static int ACTION_LST_LOAN_ENQ = 250;
    
    public final static String SELF_CUBAR = "SELFCUBARREQ";
    public final static int ACTION_SELF_CUBAR = 252;
    public final static String SERVICE_SELF_CUBAR = "SELFCUBAR";
    
    public final static String SELF_CU_UNBAR = "SELFCUUNBARREQ";
    public final static int ACTION_SELF_CU_UNBAR = 202;
    public final static String SERVICE_SELF_CU_UNBAR = "SLFCUUNBAR";
    public final static String SELF_PIN_RESET_REQ = "SPINRESET";
 public static final int ACTION_SELF_PIN_RESET = 253;
    public final static String LST_N_EVD_TRF = "EXTLSTNEVDTRF";
    public final static int ACTION_LST_N_EVD_TRF = 255;
    
    abstract public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException;

    abstract public void generateResponseMessage(RequestVO p_requestVO);

    abstract public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException;

    abstract public void generateChannelResponseMessage(RequestVO p_requestVO);

    public static ChannelUserDAO _channelUserDAO = new ChannelUserDAO();  
    public static OperatorUtilI _operatorUtil = null;
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ParserUtility[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * Method to validate the basic User Identification Code (MSISDN) in this
     * case
     */
    public void validateUserIdentification(RequestVO p_requestVO) throws BTSLBaseException {
        validateMSISDN(p_requestVO);
    }

    /**
     * Performs Validation of MSISDN
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateMSISDN(RequestVO p_requestVO) throws BTSLBaseException {
        final String filteredMSISDN = _operatorUtil.getSystemFilteredMSISDN(p_requestVO.getRequestMSISDN());
        p_requestVO.setFilteredMSISDN(filteredMSISDN);
        p_requestVO.setMessageSentMsisdn(filteredMSISDN);
        if (!BTSLUtil.isValidMSISDN(filteredMSISDN)) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ParserUtility[validateMSISDN]", p_requestVO
                .getRequestIDStr(), filteredMSISDN, "", "Sender MSISDN Not valid");
            p_requestVO.setSenderMessageRequired(false);
            throw new BTSLBaseException(this, "validateMSISDN", PretupsErrorCodesI.C2S_ERROR_INVALID_SENDER_MSISDN);
        }
    }

    /**
     * Method to find the action (Keyword) in the request
     * 
     * @param p_requestVO
     * @return
     * @throws BTSLBaseException
     */
    public static int actionParser(RequestVO p_requestVO) throws BTSLBaseException {
        final String requestStr = p_requestVO.getRequestMessage();
        if (LOG.isDebugEnabled()) {
            LOG.debug("requestParser", "Entered p_requestVO " + p_requestVO.toString() + " requestStr: " + requestStr);
        }
        final String METHOD_NAME = "actionParser";
        int action = -1;
        String type = null;
        try {
            // if(!(requestStr.indexOf("<?xml version=\"1.0\"?>")!=-1) )
            // {
            // throw new
            // BTSLBaseException(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            // }
            // /*
            // * As it is not used by O2C XML API. and PreTUPS do not validate
            // DTD . so it is not required
            // * if(!(requestStr.indexOf("xml/command.dtd")!=-1) )
            // {
            // throw new
            // BTSLBaseException(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            // }
            // */ if(!(requestStr.indexOf("<COMMAND>")!=-1) )
            // {
            // throw new
            // BTSLBaseException(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            // }
            // if(!(requestStr.indexOf("</COMMAND>")!=-1) )
            // {
            // throw new
            // BTSLBaseException(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            // }
            // int index=requestStr.indexOf("<TYPE>");
            // String
            // type=requestStr.substring(index+"<TYPE>".length(),requestStr.indexOf("</TYPE>",index));

            final String contentType = p_requestVO.getReqContentType();
            if (contentType != null && (contentType.indexOf("plain") != -1 || contentType.indexOf("PLAIN") != -1)) {
                type = p_requestVO.getServiceKeyword();
                if (BTSLUtil.isNullString(type)) {
                    final int index = requestStr.indexOf("TYPE=");
                    type = requestStr.substring(index + "TYPE=".length(), requestStr.indexOf("&", index));
                }
            } else if (contentType != null && (p_requestVO.getReqContentType().indexOf("xml") != -1 || p_requestVO.getReqContentType().indexOf("XML") != -1)) {
//                if (!(requestStr.indexOf("<?xml version=\"1.0\"?>") != -1 || requestStr.indexOf("<?xml version=\"1.0\" ?>") != -1)) {
//                    throw new BTSLBaseException(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
//                }
                /*
                 * As it is not used by O2C XML API. and PreTUPS do not validate
                 * DTD . so it is not required
                 * if(!(requestStr.indexOf("xml/command.dtd")!=-1) )
                 * {
                 * throw new BTSLBaseException(PretupsErrorCodesI.
                 * P2P_ERROR_INVALIDMESSAGEFORMAT);
                 * }
                 */if (!(requestStr.indexOf("<COMMAND>") != -1)) {
                    throw new BTSLBaseException(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
                }
                if (!(requestStr.indexOf("</COMMAND>") != -1)) {
                    throw new BTSLBaseException(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
                }
                final int index = requestStr.indexOf("<TYPE>");
                type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            }
            if(LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME,"type = "+type);
			}
            
            if ("CACINFREQ".equals(type)) {
                action = 0;
            } else if ("CCTRFREQ".equals(type)) {
                action = 1;
            }
            if ("CCPNREQ".equals(type)) {
                action = 2;
            } else if ("CCLANGREQ".equals(type)) {
                action = 3;
            } else if ("CCHISREQ".equals(type)) {
                action = 4;
            } else if ("CCRCREQ".equals(type)) {
                action = CREDIT_RECHARGE;
            } else if ("REGREQ".equals(type)) {
                action = SUBSCRIBER_REGISTRATION;
            } else if ("DREGREQ".equals(type)) {
                action = SUBSCRIBER_DEREGISTRATION;
            } else if ("SUSREQ".equals(type)) {
                action = P2P_SERVICE_SUSPEND;
            } else if ("RESREQ".equals(type)) {
                action = P2P_SERVICE_RESUME;
            } else if ("PLTREQ".equals(type))// added for Last Transfer
            // Status(P2P) 03/05/07
            {
                action = LAST_TRANSFER_STATUS;
            } else if ("ADDBUDDYREQ".equals(type)) {
                action = ADD_BUDDY;
            } else if ("DELBUDDYREQ".equals(type)) {
                action = DELETE_BUDDY;
            }
            // added by harsh 09Aug12
            else if ("SCLDREQ".equals(type)) {
                action = DELETE_MULTLIST;
            } else if ("LSTBUDDYREQ".equals(type)) {
                action = LIST_BUDDY;
            } else if ("BARREQ".equals(type)) {
                action = SELF_BAR;
            } else if ("GMBBAR".equals(type)) {
                action = SELF_BAR;
            }else if ("RCETRANREQ".equals(type)) {
                action = ACTION_C2S_TRANS_ENQ;
            } else if ( "SIDREQ".equals(type) || "REGSID".equals(type) || "SIDREG".equals(type)) {
		    	action = ACTION_REGISTER_SID;
            } else if ("DELSID".equals(type)) {
                action = ACTION_DELETE_SID_REQ;
            } else if ("ENQSID".equals(type)) {
                action = ACTION_ENQUIRY_SID_REQ;
            } else if ("CCMULTRFREQ".equals(type)) {
                action = ACTION_P2P_CRIT_TRANS;
            } else if ("CGMBALREQ".equals(type)) {
                action = P2P_GIVE_ME_BALANCE;
            } else if ("LMBREQ".equals(type)) {
                action = P2P_LEND_ME_BALANCE;
            } else if ("SCLAMREQ".equals(type)) {
                action = ACTION_MULT_CDT_TXR_LIST_AMD;
            } else if ("SCLVREQ".equals(type)) {
                action = ACTION_MULT_CDT_TXR_LIST_VIEW;
            } else if ("SCLTRFREQ".equals(type)) {
                action = ACTION_MULT_CDT_TXR_LIST_REQUEST;
            } else if ("LMBDBTREQ".equals(type)) {
                action = LMB_ONLINE_DEBIT;
            }
            // added by harsh for Scheduled Credit List (Add/Modify/Delete) API
            // on 22 Apr 13
            else if ("PSCTAMREQ".equals(type)) {
                action = ACTION_MULT_CDT_TXR_SCDLIST_AMD;
            } else if ("SHCCTRFREQ".equals(type)) {
                action = SCHEDULE_CREDIT_TRANSFER;
            }
            // added by pradyumn for scheduled list view
            else if ("PSCTDREQ".equals(type)) {
                action = ACTION_MULT_CDT_TXR_SCDLIST_DLT;
            }
            // added by pradyumn for scheduled credit list view
            else if ("PSCTVREQ".equals(type)) {
                action = ACTION_MULT_CDT_TXR_SCDLIST_VEW;
            }
            // Added by sonali for self topup user registration
            else if ("STPREGREQ".equals(type)) {
                action = ACTION_SELF_TOPUP_USER_REGISTRATION;
            }
            // Added by sonali for self topup change pin request
            else if ("STPCPNREQ".equals(type)) {
                action = ACTION_SELF_TOPUP_CHANGE_PIN;
            }

            // added by gaurav for card registration
            else if ("STPACREQ".equals(type)) {
                action = ACTION_SELF_TOPUP_CARD_REGISTRATION;
            } else if (type.equals(VOUCHER_CONSUMPTION)) {
                action = ACTION_VOUCHER_CONSUMPTION;
            } else if (type.equals(VOUCHER_CONSUMPTION_O2C)) {
                action = ACTION_VOUCHER_CONSUMPTION_O2C;
            } else if ("VOUENQREQ".equals(type))// Added for voms
            {
                action = ACTION_VOMS_ENQ;
            } else if ("VOUCONSREQ".equals(type))// Added for voms
            {
                action = ACTION_VOMS_CON;
            } else if ("VOURETREQ".equals(type))// Added for voms
            {
                action = ACTION_VOMS_RET;
            }

            // added for voucher query and rollback request
            else if ("VOUQRYREQ".equals(type))// Added for voms
            {
                action = ACTION_VOMS_QRY;
            } else if ("VOURBKREQ".equals(type))// Added for voms
            {
                action = ACTION_VOMS_ROLLBACK;
            } else if ("VOURETAGREQ".equals(type))// Added for voms
            {
                action = ACTION_VOMS_RETAGAIN;
            }
            // added for voucher retrieval request
            else if ("VOURETRBKREQ".equals(type))// Added for voms
            {
                action = ACTION_VOMS_RETRIEVAL_ROLLBACK;
            }
			else if(type.equals("IATPRCREQ"))
			{
				action = IAT_P2P_RECHRG;
			}
          //added for user movement
			else if("USERMOVEMENTREQ".equals(type))
			{
		    action=ACTION_EXT_USER_TRANSFER;
			}

		else if("VOMSSTCHGREQ".equals(type))
			{
				action = ACTION_VOUCHER_STATUS_CHANGES;
			}
		else if("VOMSEXPCHGREQ".equals(type))
		{
			action = ACTION_VOUCHER_EXPIRY_CHANGES;
		}
	   else if(type.equals("VOMSVASCONSREQ"))
			{
				action = ACTION_VAS_VOUCHER_CONSUMPTION;
			} 		
		 else if (type.equals(CP2P_DATA_TRANSFER)) {
				 action = ACTION_DATA_CP2P_RECHARGE;
			 }
			
		 else if(VMS_PIN_EXP_EXT_REQ.equals(type))
			{
				action = ACTION_VMS_PIN_EXP_EXT_REQ;
		    }
			  ///////////VHA START////////////////
		 else if (type.equals(VOMS_VALIDATE_REQ)) {
			 action = ACTION_VOMS_VALIDATE_REQ;
		 }
		 else if (type.equals(VOMS_RESERVE_REQ)) {
			 action = ACTION_VOMS_RESERVE_REQ;
		 }
		 else if (type.equals(VOMS_DIRECT_CONSUMPTION_REQ)) {
			 action = ACTION_VOMS_DIRECT_CONSUMPTION_REQ;
		 }
		 else if (type.equals(VOMS_EXPIRY_EXTENTION_REQ)) {
			 action = ACTION_VOMS_EXPIRY_EXTENTION_REQ;
		 }
		 else if (type.equals(VOMS_ROLLBACK_REQ)) {
			 action = ACTION_VOMS_DIRECT_ROLLBACK_REQ;
		 }
		 else if(MY_VOUCHR_ENQUIRY_SUBSCRIBER_REQ.equals(type))
         {
			 action=ACTION_VOMS_MY_VOUCHR_ENQUIRY_SUBSCRIBER_REQ;
         }
  ///////////VHA END////////////////
          //added by Ashish for VIL
         else if(VMS_PIN_CONSUME_REQ.equals(type))
		 {
			action = ACTION_VMS_PIN_CONSUME_REQ;
	     }
         else if("C2CTRFREC".equals(type))
         {
        	 action = ACTION_C2C_REQ_REC;
         }
         else if(ACTION_C2C_APPR_REQ.equals(type))
         {
        	 action = ACTION_C2C_APPR;
         }else if("C2CVOUCHERAPPROVAL".equals(type))
         {
        	 action = ACTION_C2C_VOUCHER_APPR;
         }
         else if(type.equals("SUBTEQREQ"))
         {
             action=ACTION_SUBS_THR_ENQ_REQUEST;
         }   
         //ended by Ashish for VIL
            // changes ends here
            if (action == -1) {
                throw new BTSLBaseException(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            }
            p_requestVO.setActionValue(action);
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            LOG.error("actionParser", "Exception e: " + e);
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("actionParser", "exit action:" + action);
            }
        }
        return action;
    }

    /**
     * Method to find the action (Keyword) in Channel requests
     * 
     * @param p_requestVO
     * @return
     * @throws BTSLBaseException
     */
    public static int actionChannelParser(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "actionChannelParser";
        final String requestStr = p_requestVO.getRequestMessage();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_requestVO = " + p_requestVO.toString() + ", requestStr : " + requestStr);
        }
        int action = -1;
        String type = null;
        try {
            final String contentType = p_requestVO.getReqContentType();
            if (contentType != null && (contentType.indexOf("plain") != -1 || contentType.indexOf("PLAIN") != -1)) {
                type = p_requestVO.getServiceKeyword();
                if (BTSLUtil.isNullString(type)) {
                    final int index = requestStr.indexOf("TYPE=");
                    type = requestStr.substring(index + "TYPE=".length(), requestStr.indexOf("&", index));
                }
            } else if (contentType != null && (p_requestVO.getReqContentType().indexOf("xml") != -1 || p_requestVO.getReqContentType().indexOf("XML") != -1)) {
//                if (!(requestStr.indexOf("<?xml version=\"1.0\"?>") != -1 || requestStr.indexOf("<?xml version=\"1.0\" ?>") != -1)) {
//                    throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
//                }
                /*
                 * if(!(requestStr.indexOf("xml/command.dtd")!=-1) )
                 * {
                 * throw new BTSLBaseException(PretupsErrorCodesI.
                 * C2S_ERROR_INVALIDMESSAGEFORMAT);
                 * }
                 */
                if (!(requestStr.indexOf("<COMMAND>") != -1)) {
                    throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
                if (!(requestStr.indexOf("</COMMAND>") != -1)) {
                    throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
                final int index = requestStr.indexOf("<TYPE>");
                type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
                LOG.debug("actionChannelParser", "TYPE Coming" + " " + type);

            }else if(contentType != null && PretupsI.JSON_CONTENT_TYPE.equals(contentType)){
            	type=p_requestVO.getServiceKeyword();
            }

            if ("CACINFREQ".equals(type)) {
                action = 0;
            } else if ("RCTRFREQ".equals(type)) {
                action = 1;
            } else if ("RCPNREQ".equals(type)) {
                action = 2;
            } else if ("RCNLANGREQ".equals(type)) {
                action = 3;
            } else if ("CCHISREQ".equals(type)) {
                action = 4;
            } else if ("TRFREQ".equals(type)) {
                action = 5;
            } else if ("RETREQ".equals(type)) {
                action = 6;
            } else if ("WDTHREQ".equals(type)) {
                action = 7;
            } else if ("PPBTRFREQ".equals(type)) {
                action = 8;
            } else if ("O2CINREQ".equals(type)) {
                action = ACTION_CHNL_O2C_INITIATE;
            } else if ("O2CINTREQ".equals(type)) {
                action = ACTION_CHNL_O2C_INITIATE_TRFR;
            } else if ("O2CRETREQ".equals(type)) {
                action = ACTION_CHNL_O2C_RETURN;
            } else if ("O2CWDREQ".equals(type)) {
                action = ACTION_CHNL_O2C_WITHDRAW;
            } else if ("EXRCSTATREQ".equals(type)) {
                action = ACTION_CHNL_EXT_RECH_STATUS;
            } else if ("EXRCTRFREQ".equals(type)) {
                action = ACTION_CHNL_EXT_CREDIT_TRANSFER;
            } else if ("EXVOUPRFMOD".equals(type)) {
                action = ACTION_VOU_PRF_MOD_REQ;
            } else if ("BALREQ".equals(type)) // added for Balance Enquiry
            // 03/05/07
            {
                action = ACTION_CHNL_BALANCE_ENQUIRY;
            } else if ("DSRREQ".equals(type)) // added for Daily Status Report
            // 03/05/07
            {
                action = ACTION_CHNL_DAILY_STATUS_REPORT;
            } else if ("LTSREQ".equals(type)) // added for Last Transfer
            // Status(RP2P) 03/05/07
            {
                action = ACTION_CHNL_LAST_TRANSFER_STATUS;
            } else if ("EVDREQ".equals(type)) {
                action = ACTION_CHNL_EVD_REQUEST;
            } else if ("MVDREQ".equals(type)) {
                action = ACTION_MULTIPLE_VOUCHER_DISTRIBUTION;
            } else if ("UBPREQ".equals(type)) {
                action = ACTION_UTILITY_BILL_PAYMENT;
            } else if ("EXPPBREQ".equals(type))// added for C2S Bill payment
            {
                action = ACTION_CHNL_EXT_TRANSFER_BILL_PAYMENT;
            } else if ("EXTSYSENQREQ".equals(type))// added for c2s Enquiry
            {
                action = ACTION_CHNL_EXT_ENQUIRY_REQUEST;
            } else if ("EXPPBSTATREQ".equals(type))// added for c2s Enquiry
            {
                action = ACTION_CHNL_EXT_POST_RECHARGE_STATUS;
            } else if ("CEXRCTRFREQ".equals(type)) // added for common recharge
            {
                action = ACTION_CHNL_EXT_COMMON_RECHARGE;
            } else if ("GFTRCREQ".equals(type))// added for Gift Recharge
            // through USSD
            {
                action = ACTION_CHNL_GIFT_RECHARGE_USSD;
            } else if ("EXGFTRCREQ".equals(type))// added for Gift Recharge
            // through XML API
            {
                action = ACTION_CHNL_GIFT_RECHARGE_XML;
            } else if ("EXUSRBALREQ".equals(type))// added for Gift Recharge
            // through XML API
            {
                action = ACTION_CHNL_BAL_ENQ_XML;
            } else if ("EXEVDREQ".equals(type))// added for Gift Recharge
            // through XML API
            {
                action = ACTION_CHNL_EVD_XML;
            } else if ("EXC2CTRFREQ".equals(type))// C2C transfer through
            // external getway (XML API)
            {
                action = ACTION_C2C_TRANSFER_EXT_XML;
            } else if ("EXC2CWDREQ".equals(type))// C2C withdraw through
            // external getway (XML API)
            {
                action = ACTION_C2C_WITHDRAW_EXT_XML;
            } else if ("EXC2CRETREQ".equals(type))// C2C Return through external
            // getway (XML API)
            {
                action = ACTION_C2C_RETURN_EXT_XML;
            } else if ("EXC2SCPNREQ".equals(type))// Change Pin through external
            // getway (XML API)
            {
                action = ACTION_EXT_C2SCHANGEPIN_XML;
            } else if ("CDMARCTRFREQ".equals(type))// Added for CDMA recharge
            {
                action = ACTION_CHNL_CREDIT_TRANSFER_CDMA;
            } else if ("PSTNRCTRFREQ".equals(type))// Added for PSTN recharge
            {
                action = ACTION_CHNL_CREDIT_TRANSFER_PSTN;
            } else if ("INTRRCTRFREQ".equals(type))// Added for INTERNET
            // recharge
            {
                action = ACTION_CHNL_CREDIT_TRANSFER_INTR;
            } else if ("EXCDMARCREQ".equals(type))// Added for CDMA Bank
            // Recharge
            {
                action = ACTION_CHNL_EXT_CREDIT_TRANSFER_CDMA;
            } else if ("EXPSTNRCREQ".equals(type))// Added for PSTN Bank
            // Recharge
            {
                action = ACTION_CHNL_EXT_CREDIT_TRANSFER_PSTN;
            } else if ("EXINTRRCREQ".equals(type))// Added for INTR Bank
            // Recharge
            {
                action = ACTION_CHNL_EXT_CREDIT_TRANSFER_INTR;
            } else if ("ORDLREQ".equals(type))// Added for Order Line
            {
                action = ACTION_CHNL_ORDER_LINE;
            } else if ("ORDCREQ".equals(type))// Added for Order Credit
            {
                action = ACTION_CHNL_ORDER_CREDIT;
            } else if ("BARREQ".equals(type))// Added for Barring
            {
                action = ACTION_CHNL_BARRING;
            } else if ("VASSELLREQ".equals(type))// Roam Recharge through
            // external getway (XML API)
            {
                action = ACTION_CHNL_EXT_VAS_SELLING;
            } else if ("ROAMRCREQ".equals(type))// Roam Recharge through
            // external getway (XML API)
            {
                action = ACTION_CHNL_IAT_ROAM_RECHARGE;
            } else if ("USRINCVIEWREQ".equals(type))// Roam Recharge through
                // external getway (XML API)
                {
                    action = TOTAL_USER_INCOME_DETAILS_VIEW;
                }
            else if ("INTLRCREQ".equals(type))// Roam Recharge through
            // external getway (XML API)
            {
                action = ACTION_CHNL_IAT_INTERNATIONAL_RECHARGE;
            } else if ("LXTSREQ".equals(type) || "L3TSREQ".equals(type))// By
            // vikram
            // for
            // last
            // x
            // transfers
            // c2c/c2s/both//korek
            {
                action = ACTION_CHNL_C2S_LAST_XTRANSFER;
            } else if ("CUSTREQ".equals(type))// By vikram for Customer enquiry
            {
                action = ACTION_CHNL_CUST_LAST_XTRANSFER;
            } else if ("EXLST3TRFREQ".equals(type)) {
                action = ACTION_EXT_LAST_XTRF_ENQ;
            } else if ("EXCUSTREQ".equals(type)) {
                action = ACTION_EXT_CUSTOMER_ENQ_REQ;
            } else if ("EXOTHUSRBALREQ".equals(type)) {
                action = ACTION_EXT_OTHER_BAL_ENQ;
            } else if ("EXLSTTRFREQ".equals(type)) {
                action = ACTION_EXT_LAST_TRF;
            } else if ("EXDLYREQ".equals(type)) {
                action = ACTION_EXT_DAILY_STATUS_REPORT;
            } else if ("EXTMVDREQ".equals(type)) // changes done by ashishT for
            // MVD USSD voucher download.
            {
                action = ACTION_EXT_MVD_DWNLD_RQST;
            } else if ("RCETRANREQ".equals(type)) // changes done by ashishT for
            // MVD USSD voucher download.
            {
                action = ACTION_C2S_TRANS_ENQ;
            } else if ("SIDREQ".equals(type)) {
                action = ACTION_REGISTER_SID;
            } else if ("ETXNENQREQ".equals(type)) {
                action = ACTION_ENQUIRY_TXNIDEXTCODEDATE;
            } else if ("DELSID".equals(type)) {
                action = ACTION_DELETE_SID_REQ;
            } else if ("ENQSID".equals(type)) {
                action = ACTION_ENQUIRY_SID_REQ;
            } else if ("CRBTACREQ".equals(type)) // changes done by ShashankS
            // for CRBT Registration.
            {
                action = ACTION_CRBT_REGISTRATION;
            } else if ("CRBTSGREQ".equals(type)) // changes done by ShashankS
            // for CRBT Song Selection
            {
                action = ACTION_CRBT_SONG_SELECTION;
            } else if ("EVRTRFREQ".equals(type)) // changes done by Harpreet for
            // EVR
            {
                action = ACTION_ELECTRONIC_VOUCHER_RECHARGE;
            } else if ("EXEVRTRFREQ".equals(type))// added by Harpreet for EVR
            // External Gateway API
            {
                action = ACTION_CHNL_EVR_XML;
            }else if ("EXEVRREQ".equals(type))// added by Abhilasha for EVR Umniah
             // External Gateway API
            {
                    action = ACTION_CHNL_EVR_UMNIAH_XML;
            }else if ("SRCUSRREQ".equals(type)) // Suspend Resume channel user
            // through USSD
            {
                action = SUSPEND_RESUME_CUSR;
            } else if ("EXPVEVDREQ".equals(type))// added for Private Recharge
            // through External G/W
            {
                action = ACTION_EXT_PRIVATERC_XML;
            } else if ("EXTC2CDRCRREQ".equals(type)) {
                action = ACTION_EXT_DRCR_C2C_CUSER;// added for DrCr Transfer
                // through External Gateway
            } else if ("ADDCHUSR".equals(type))// added for user creation thru
            // ussd
            {
                action = ACTION_EXT_USER_CREATION;
            } else if ("SRCUSRREQEX".equals(type)) // Suspend Resume channel
            // user through USSD
            {
                action = ACTION_SUSPEND_RESUME_CUSR_EXTGW;
            } else if ("LMBREQ".equals(type)) {
                action = P2P_LEND_ME_BALANCE;
            } else if ("LXC2STSREQ".equals(type))// By rahuld for last X c2s
            // trans
            {
                action = ACTION_C2S_RPT_LAST_XTRANSFER;
            } else if ("VASTRFREQ".equals(type)) // VASTRIX CHANGES
            {
                action = ACTION_VAS_RC_REQUEST;
            } else if ("PVASTRFREQ".equals(type)) {
                action = ACTION_PVAS_RC_REQUEST;
            } else if ("VASEXTRFREQ".equals(type))// r.dutt
            {
                action = ACTION_EXTVAS_RC_REQUEST;
            } else if ("PVASEXTRFREQ".equals(type)) {
                action = ACTION_EXTPVAS_RC_REQUEST;
            }// VASTRIX ENDS
            else if ("EXTGRPH".equals(type))// ADDED BY ANUPAM MALVIYA FOR DMS
            {
                action = ACTION_EXT_GEOGRAPHY_REQUEST;
            } else if ("TRFRULETYP".equals(type))// ADDED BY ANUPAM MALVIYA FOR
            // DMS
            {
                action = ACTION_EXT_TRF_RULE_TYPE_REQ;
            } else if ("EXTUSRADD".equals(type))// ADDED BY ANUPAM MALVIYA FOR
            // DMS
            {
                action = ACTION_EXT_USERADD_REQUEST;
            } else if ("AUTHCUSER".equals(type))// //added by shashank for
            // channel user authentication
            {
                action = ACTION_CRM_USER_AUTH_XML;
            } else if ("SIMACTREQ".equals(type)) // change for SIM activation by
            // sachin date 01/06/2011
            {
                action = ACTION_USSD_SIM_ACT_REQ;
            } else if ("EXTSYSSUBENQREQ".equals(type))// added by Sonali Garg to
            // enquire subscriber at
            // IN
            {
                action = ACTION_EXT_SUBENQ;
            } else if ("O2CEXTENQREQ".equals(type)) {
                action = ACTION_O2C_SAP_ENQUIRY;
            } else if ("O2CEXTCODEUPDREQ".equals(type)) {
                action = ACTION_O2C_SAP_EXTCODE_UPDATE;
            } else if ("COLENQREQ".equals(type)) {
                action = ACTION_COL_ENQ;
            } else if ("COLBPREQ".equals(type)) {
                action = ACTION_COL_BILLPAYMENT;
            } else if ("EXDTHTRFREQ".equals(type)) {
                action = ACTION_DTH;
            } else if ("DTHTRFREQ".equals(type)) {
                action = ACTION_DTH;
            } else if ("EXDCTRFREQ".equals(type)) {
                action = ACTION_DC;
            } else if ("DCTRFREQ".equals(type)) {
                action = ACTION_DC;
            } else if ("EXBPBTRFREQ".equals(type)) {
                action = ACTION_BPB;
            } else if ("BPBTRFREQ".equals(type)) {
                action = ACTION_BPB;
            } else if ("EXPINTRFREQ".equals(type)) {
                action = ACTION_PIN;
            } else if ("PINTRFREQ".equals(type)) {
                action = ACTION_PIN;
            } else if ("EXPMDTRFREQ".equals(type)) {
                action = ACTION_PMD;
            } else if ("PMDTRFREQ".equals(type)) {
                action = ACTION_PMD;
            } else if ("EXFLRCTRFREQ".equals(type)) {
                action = ACTION_FLRC;
            } else if ("FLRCTRFREQ".equals(type)) {
                action = ACTION_FLRC;
            } else if ("COLCCNREQ".equals(type)) {
                action = ACTION_C2S_POSTPAID_REVERSAL;
            } else if ("EXPBENQREQ".equals(type) || "PBENQREQ".equals(type)) // PPBENQ
            // rahul.d
            // POS
            {
                action = ACTION_EXT_PPBENQ;
            } else if ("EXTSYSSUBENQREQ".equals(type))// added by Sonali Garg to
            // enquire subscriber at
            // IN
            {
                action = ACTION_EXT_SUBENQ;
            }
            // Added by Diwakar on 20-JAN-2014 for ROBI
            else if (ADD_USER_REQ.equals(type)) {
                action = ADD_USER_ACTION;
            } else if (MODIFY_USER_REQ.equals(type)) {
                action = MODIFY_USER_ACTION;
            } else if (DELETE_USER_REQ.equals(type)) {
                action = DELETE_USER_ACTION;
            } else if (SUSPEND_RESUME_USER_REQ.equals(type)) {
                action = SUSPEND_RESUME_USER_ACTION;
            } else if (ADD_DELETE_USER_ROLE_REQ.equals(type)) {
                action = ADD_DELETE_USER_ROLE_ACTION;
            } else if (CHANE_PASSWORD_REQ.equals(type)) {
                action = CHANGE_PASSWORD_ACTION;
            } else if (MNP_REQ.equals(type)) {
                action = MNP_ACTION;
            } else if (ICCID_MSISDN_MAP_REQ.equals(type)) {
                action = ICCID_MSISDN_MAP_ACTION;
            } else if ("RCREVREQ".equals(type)) {
                action = ACTION_C2S_PRE_PAID_REVERSAL;
            } else if ("EXTROAMRCREQ".equals(type))// Roam Recharge through
            // external getway (XML API)
            {
                action = ACTION_CHNL_EXT_ROAM_RECHARGE;
            }
            // Added by Surabhi for GetMyNumber
            else if ("EXMSISDNREQ".equals(type)) {
                action = GET_MY_MSISDN;
            } else if ("EXCHNGMSISDNREQ".equals(type)) { // for MSISDN Change
                // Functionality
                action = ACTION_EXT_C2SCHANGEMSISDN;
            } else if ("EXCHNGSTATUSREQ".equals(type)) { // for ETU Change
                // Recharge Status
                // Functionality
                action = ACTION_EXT_C2SRECHARGESTATUS;
            } else if ("EXWRTRFREQ".equals(type)) {
                action = ACTION_CHNL_EXT_WARRANTY_TRANSFER;
            } else if ("EXADTRFREQ".equals(type)) {
                action = ACTION_CHNL_EXT_ADVANCE_TRANSFER;
            } else if ("EXCAUTTRFREQ".equals(type)) {
                action = ACTION_CHNL_EXT_CAUTION_TRANSFER;
            } else if ("LMSPTENQ".equals(type))// Added By Brajesh for LMS
            // Points Enquiry Through
            // external getway (XML API)
            {
                action = ACTION_CHNL_LMS_POINTS_ENQUIRY;
            } else if ("LMSPTRED".equals(type))// Added By Brajesh for LMS
            // Points Enquiry Through
            // external getway (XML API)
            {
                action = ACTION_CHNL_LMS_POINTS_REDEMPTION;
            } else if (VOUCHER_CONSUMPTION.equals(type)) {
                action = ACTION_VOUCHER_CONSUMPTION;
            } else if ("EXUSERINFOREQ".equals(type)) {
                action = GET_CHNL_USR_INFO;
            } else if (VOUCHER_CONSUMPTION_O2C.equals(type)) {
                action = ACTION_VOUCHER_CONSUMPTION_O2C;
            } else if ("INPRESET".equals(type)) {
                action = ACTION_INITIATE_PIN_RESET;
            } else if ("PRESET".equals(type)) {
                action = ACTION_PIN_RESET;
            } else if ("DUPDATE".equals(type)) {
                action = ACTION_DATA_UPDATE;
            } else if ("C2STXNENQREQ".equals(type)) {
                action = ACTION_CUST_C2S_ENQ_REQ;
            } else if ("EXTROAMRCREVREQ".equals(type))// Roam Recharge through
            // external getway (XML
            // API)
            {
                action = ACTION_CHNL_EXT_ROAM_RECHARGE_REVERSAL;
            }
            else if ("MCRTRFREQ".equals(type)) {
                action = ACTION_MULTICURRENCY_RECHARGE;
                
            }else if (ADD_CHANNEL_USER_REQ.equals(type)) {
                action = ADD_CHANNEL_USER_ACTION;
            }else if (MODIFY_CHANNEL_USER_REQ.equals(type)) {
                action = MODIFY_CHANNEL_USER_ACTION;
            }
            else if("C2CAPPR".equals(type))
            {
           	 action = ACTION_C2C_APPR;
            }
            else if("C2CVOUCHERAPPROVAL".equals(type))
            {
           	 action = ACTION_C2C_VOUCHER_APPR;
            }else if("USERDETAILSREQ".equals(type))
            {
              	 action = CHANNEL_USER_DETAILS;
            }             
            if (action == -1 && type.contains("USER")) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_INVALID_TYPE_VALUE);
                throw new BTSLBaseException("ParserUtility", "actionChannelParser", PretupsErrorCodesI.EXTSYS_REQ_INVALID_TYPE_VALUE);
            }
            // Ended here by Diwakar
			//Added new request type specific for Last X Transfer Service Wise request
		    else if("EXLASTXSERENQREQ".equals(type))
            {
                action=ACTION_EXT_LAST_XTRF_SRVCWISE_ENQ;
            }
		    //End here
			else if("RCTRFSERREQ".equals(type)){
				action=ACTION_CHNL_LITE_RECHARGE;
			}
			else if("CGENQREQ".equalsIgnoreCase(type))
			{
				action=ACTION_CHNL_CARDGROUP_ENQUIRY_REQUEST;
			}
	 		else if("EXTPROMOVASTRFREQ".equals(type))
			{
				action=ACTION_EXTPROMOPVAS_REQUEST;
			}
            else if("EXTPROMOINTLTRFREQ".equals(type))
            {
                action=ACTION_EXTPROMOINTLTRFREQ_REQUEST;
            }

			else if("CURRENCYCONVERSIONREQ".equalsIgnoreCase(type))
			{
				action=ACTION_CHNL_CURRENCY_CONVERSION_REQUEST;
			}

			else if("MCRTRFREQ".equalsIgnoreCase(type))
			{
				action=ACTION_MULTICURRENCY_RECHARGE;
			}
            //added for user movement
			else if("USERMOVEMENTREQ".equals(type))
			{
		    action=ACTION_EXT_USER_TRANSFER;
			}
			else if("EXDATATRFREQ".equalsIgnoreCase(type))
			{
				action=ACTION_EXDATATRFREQ_REQUEST;
			}
			else if("EXTDCSRREQ".equalsIgnoreCase(type))
			{
				action=ACTION_EXT_DSR_REQUEST;
			}
			else if("EXTSTKBALREQ".equalsIgnoreCase(type))
			{
				action=ACTION_EXT_STOCK_BALANCE_REQUEST;
			}
			else if("BRCREVREQ".equals(type))
			{
			   action=ACTION_CHNL_EXT_BULK_RCH_REVERSAL;
			}
			else if("VOMSSTCHGREQ".equals(type))
			{
				action = ACTION_VOUCHER_STATUS_CHANGES;
			}
			else if("VOMSEXPCHGREQ".equals(type))
			{
				action = ACTION_VOUCHER_EXPIRY_CHANGES;
			}
			else if("SOSSTL".equals(type))
			{
				action = ACTION_CHNL_SOS_SETTLEMENT_REQUEST;
			}
			else if("SOSTRF".equals(type))
			{
				action = ACTION_CHNL_SOS_REQUEST;
			}
           else if("VOMSVASCONSREQ".equals(type))
			{
				action = ACTION_VAS_VOUCHER_CONSUMPTION;

			} 
            //sayyed yasin
           else if("LASTXTRFREQ".equals(type))
			{
				action = ACTION_LAST_X_TRF_OPT_CHANL;
			} 
            

			else if("EXTDTHENQ".equalsIgnoreCase(type))
			{
				action=ACTION_DTH_ENQUIRY_REQUEST;
			}
			else if("EXTDTHPAY".equalsIgnoreCase(type))
			{
				action=ACTION_DTH_PAYMENT_REQUEST;
			}
			else if("TXENQREQ".equals(type))
			{
				action = ACTION_C2S_TXNID_STAT;
			}
			else if("DSRREQC2S".equals(type))
			{
				action = ACTION_CHNL_DAILY_STATUS_REPORT;
			}
			else if("EXUSRLISTREQ".equals(type))
			{
				action = ACTION_LIST_RET;
			}    
			else if("TXNENQREQ".equals(type))
			{
				action = TXN_ENQUIRY;
			} 
			else if("LTSRVRREQ".equals(type))
			{
				action = LAST_TXN_STATUS_SUBSCRIBER;
			} 
			else if("C2SSUMREQ".equals(type))
			{
				action = C2S_SUMMARY_ENQUIRY;
			} 
            //Added for C2C/O2C Txn Enquiry
		    else if("EXCHNLSTATREQ".equals(type))
		    {
		    	action = ACTION_C2C_O2C_TXN_STATUS;
		    }
		    //Added for Channel User Commission Earned enquiry
		    else if("LTCOMREQ".equals(type))
		    {
		    	action = ACTION_USER_COMM_EARNED;
		    }
			// //Added for Max TPS fetch
			else if ("MAXTPSHOURLYREQ".equals(type)){
				
				action = ACTION_TPS_MAX_CALCULATION;
			
			}
			else if("OLORCTRFREQ".equals(type))
			{
			   action=ACTION_OLORECHARGE_REQUEST;
			}
			else if("O2CAPRL".equalsIgnoreCase(type))
			{
				action=ACTION_CHNL_O2CAPRL_REQUEST;
			}
			else if (type.equals("CHCGENREQ")) {
				 action = ACTION_USER_CARDGROUP_ENQUIRY_REQUEST;
		    }
            //Added for BFaso
		    else if(type.equals("ADUSRREQ"))
            {
                action=ACTION_CHNL_ADD_CHNL_USER;
            }
            else if(type.equals("TBRCTRFREQ")) //Added for Product Recharge through plain ussd req
            {
             action=ACTION_PRODUCT_RECHARGE_REQ;
            }				
            else if(SOS_FLAG_UPDATE_REQ.equals(type))
			{
				action = ACTION_SOS_FLAG_UPDATE_REQ;
		    }
            else if(VMS_PIN_EXP_EXT_REQ.equals(type))
			{
				action = ACTION_VMS_PIN_EXP_EXT_REQ;
		    }
            else if (EXT_DVD_REQ.equals(type)) {//DVD
                action = ACTION_CHNL_DVD_XML;
            }
            else if("VOMSENREQ".equals(type))
            {
            	action =ACTION_VOMS_O2C;
            }
            else if("VCAVLBLREQ".equals(type))
            	action = 201;
            else if("C2CTRFREC".equals(type))
            {
            	action = ACTION_C2C_REQ_REC;
            }
            else if("C2CAPPR".equals(type))
            {
            	action = ACTION_C2C_APPR;
            }else if("C2CVOUCHERAPPROVAL".equals(type))
            {
            	action = ACTION_C2C_VOUCHER_APPR;
            }
            else if("C2CVOMSTRF".equals(type))
            {
            	action = ACTION_C2C_VOMS_TRF;
            }

            else if("C2CVOMSTRFINI".equals(type))
            {
            	action = ACTION_C2C_VOMS_INI;
            }
            else if("UPUSRHRCHY".equals(type))
            {
            	action = ACTION_UPUSRHRCHY;
            } else if ("USERDETAILSREQ".equals(type)){
            	action = CHANNEL_USER_DETAILS;
            }
            else if ("C2CBUYENQREQ".equals(type)){
            	action = C2C_BUY_ENQ;
            }
            else if ("C2SSRVTRFCNT".equals(type)){
            	action = C2S_SV_DETAILS;
            }else if ("PSBKRPT".equals(type)){
            	action = ACTION_PSBKRPT;
            }
            else if ("C2STOTALTRANS".equals(type)) {
            	action = C2S_TOTAL_TRANSACTION_COUNT;
            }
            else if ("C2SPRODTXNDETAILS".equals(type)){
            	action = C2S_PROD_TXN_DETAILS;
            }
            else if("PASBDET".equals(type))
            {
            	action = PASSBOOK_VIEW_DETAILS;
            } 
            else if ("C2SNPRODTXNDETAILS".equals(type)){
            	action = C2S_N_PROD_TXN_DETAILS;
            }
            else if("TOTTRANSDETAIL".equals(type))
            {
            	action = TOTAL_TRANSACTION_DETAILED_VIEW;

            } 
            else if("COMINCOME".equals(type))
            {
            	action = COMMISSION_CALCULATOR;

            }
            else if("VCNCONSREQ".equals(type))
            {
            	action = ACTION_VOUCHER_CONSUMPTION;

            }
            
            else if(LOAN_OPTOUT_REQ.equals(type)) {
				action = ACTION_LOAN_OPTOUT_REQ;
			}
			else if(LOAN_OPTIN_REQ.equals(type)) {
				action = ACTION_LOAN_OPTIN_REQ;
			}
			else if(LST_LOAN_ENQ.equals(type)) {
				action = ACTION_LST_LOAN_ENQ;
			}
			else if(SELF_CUBAR.equals(type)) {
				action = ACTION_SELF_CUBAR;
			}
			else if(SELF_CU_UNBAR.equals(type)) {
				action = ACTION_SELF_CU_UNBAR;
			}
			else if (SELF_PIN_RESET_REQ.equals(type)) {
                action = ACTION_SELF_PIN_RESET;
    		}
    		else if(LST_N_EVD_TRF.equals(type)) {
				action = ACTION_LST_N_EVD_TRF;
			}
            
             if (action == -1) {
                throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }
           
            p_requestVO.setActionValue(action);
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            LOG.error(methodName, "Exception e : " + e);
            LOG.debug(methodName, "exit TYPE : " + type);
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "exit action : " + action);
            }
        }
        return action;
    }

    /**
     * Method to load the network details and validate the same
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void loadValidateNetworkDetails(RequestVO p_requestVO) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadValidateNetworkDetails", "Entered Request ID=" + p_requestVO.getRequestID());
        }
        try {
            final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(p_requestVO.getFilteredMSISDN(), PretupsI.USER_TYPE_SENDER);
            p_requestVO.setValueObject(networkPrefixVO);
            validateNetwork(p_requestVO, networkPrefixVO);
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("loadValidateNetworkDetails", "Exiting Request ID=" + p_requestVO.getRequestID());
            }
        }
    }

    /**
     * Method to validate the Network status
     * 
     * @param p_requestVO
     * @param p_networkPrefixVO
     * @throws BTSLBaseException
     */
    public void validateNetwork(RequestVO p_requestVO, NetworkPrefixVO p_networkPrefixVO) throws BTSLBaseException {
        final String methodName = "validateNetwork";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered Request ID = " + p_requestVO.getRequestID() + ", Network Code = " + p_networkPrefixVO.getNetworkCode());
        }
        try {
            final String networkID = p_networkPrefixVO.getNetworkCode();
            String message = null;
            p_requestVO.setRequestNetworkCode(networkID);

            // Check for location status (Active or suspend)
            if (!PretupsI.YES.equals(p_networkPrefixVO.getStatus())) {
                // if default language is english then pick language 1 message
                // else language 2
                final LocaleMasterVO localeVO = LocaleMasterCache
                    .getLocaleDetailsFromlocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                    message = p_networkPrefixVO.getLanguage1Message();
                } else {
                    message = p_networkPrefixVO.getLanguage2Message();
                }
                p_requestVO.setSenderReturnMessage(message);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_NETWORK_NOT_ACTIVE);
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting Request ID = " + p_requestVO.getRequestID() + ", Network Code = " + p_networkPrefixVO.getNetworkCode());
            }
        }
    }

    /**
     * Method to load and Validate the channel User Details (On MSISDN by
     * Default)
     * 
     * @param p_con
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public ChannelUserVO loadValidateUserDetails(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "loadValidateUserDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered Request ID = " + p_requestVO.getRequestID() + "Action= " + p_requestVO.getActionValue());
        }
        ChannelUserVO channelUserVO = null;
        ChannelUserVO staffUserVO = null;
        boolean byPassCheck = false;
        if (p_requestVO.getActionValue() == ACTION_VOMS_QRY) {
        	byPassCheck = true;
        }  
        try {
        	String DEFAULT_LANGUAGE = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        	String DEFAULT_COUNTRY = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        	String CHNL_PLAIN_SMS_SEPARATOR = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
        	if (byPassCheck == false && BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())
					&& BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())
					&& BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_MISSING_SENDER_IDENTIFICATION);
			}
        	String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			OperatorUtilI operatorUtili = null;
			try {
				operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "ExtAPIParsers[loadValidateUserDetails]", "", "", "",
						"Exception while loading the class at the call:" + e.getMessage());
			}

			int st = p_requestVO.getActionValue();

			// Changing for passowrd security
			if (p_requestVO.getRequestMap() != null) {
				String passwordtemp = (String) p_requestVO.getRequestMap().get("PASSWORD");
				try {
					passwordtemp = operatorUtili.decryptPINPassword(passwordtemp);
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
				p_requestVO.getRequestMap().put("PASSWORD", passwordtemp);
			}
        	switch (p_requestVO.getActionValue()) {

        	
        	//case ACTION_USRH:
        		
			case ACTION_VOMS_QRY: 
			////////////VHA START/////////////////////////
			case ACTION_VOMS_VALIDATE_REQ:
			case ACTION_VOMS_RESERVE_REQ:
			case ACTION_VOMS_DIRECT_CONSUMPTION_REQ:
			case ACTION_VMS_PIN_EXP_EXT_REQ:
			case ACTION_VOMS_DIRECT_ROLLBACK_REQ:
			////////////VHA END/////////////////////////	
			{
				String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
				String extnetworkID = p_requestVO.getExternalNetworkCode();
				String loginID = p_requestVO.getSenderLoginID();
				String password = p_requestVO.getPassword();
				LoginDAO _loginDAO = new LoginDAO();
				byPassCheck = true;
				Locale locale = new Locale(DEFAULT_LANGUAGE, DEFAULT_COUNTRY);

				if (!BTSLUtil.isNullString(loginID)) {
					channelUserVO = _loginDAO.loadUserDetails(p_con, loginID, password, locale);
					if (channelUserVO == null) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
					}
				} else if (!BTSLUtil.isNullString(extCode)) {
					channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con,
							BTSLUtil.NullToString(extCode).trim());
				}
				if (!BTSLUtil.isNullString(extnetworkID)) {
					NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(extnetworkID);
					if (networkVO == null) {
						;
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
					}
				}

				if (channelUserVO != null) {
					if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
					}
					if (!BTSLUtil.isNullString(extnetworkID)
							&& !extnetworkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
					}
					if (!BTSLUtil.isNullString(loginID)) {
						if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
							throw new BTSLBaseException(this, methodName,
									PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
						}
					}
					if (!BTSLUtil.isNullString(extCode)) {
						if (!extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
							throw new BTSLBaseException(this, methodName,
									PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
						}
					}
					
				}

			}
			
			//added by Pankaj Rawat
			case ACTION_CHNL_VOUCHER_AVAILABILITY_XML:
			{
				String extCode = p_requestVO.getRequestMap().get("EXTCODE").toString();
				String msisdn = p_requestVO.getRequestMap().get("MSISDN").toString();
				String pin = p_requestVO.getRequestMap().get("PIN").toString();
				  
                String extnetworkID = p_requestVO.getRequestMap().get("EXTNWCODE").toString();
                String loginID = p_requestVO.getRequestMap().get("LOGINID").toString();
                String password = p_requestVO.getRequestMap().get("PASSWORD").toString();
                
                byPassCheck=true;
                Locale locale = new Locale(DEFAULT_LANGUAGE, DEFAULT_COUNTRY);
                
                 if (!BTSLUtil.isNullString(loginID)) {
                	 channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(p_con, loginID);
                	 if (channelUserVO== null)
                	 {
                		 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER); 
                	 }
                	 
                	 if (!operatorUtili.validateTransactionPassword(channelUserVO, password) || BTSLUtil.isNullString(password)) {
                         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                     }
                     if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                     }
                     
                } else if (!BTSLUtil.isNullString(extCode)) {
                    channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(p_con, BTSLUtil.NullToString(extCode).trim());
                }
                 
                else if(!BTSLUtil.isNullString(msisdn))
                {
                	channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con,msisdn);
                	if(channelUserVO == null)
                	{
                		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                	}
                	
                	else
                	{
                		operatorUtili.validatePIN(p_con, channelUserVO, pin);
                	}
                }
                 if (!BTSLUtil.isNullString(extnetworkID)) {
                     NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(extnetworkID);
                     if(networkVO==null){
                     String messageArray[]= {extnetworkID};
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID, messageArray);
                     }
                 }
                 
                 if (channelUserVO != null) {
                 	if (!extnetworkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                     }
                 }
                 if (!BTSLUtil.isNullString(extnetworkID) && !extnetworkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                     throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                 }
                 if (!BTSLUtil.isNullString(loginID)) {
                     if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                     }
                 }
                 if (!BTSLUtil.isNullString(extCode)) {
                     if (!extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                     }
                 }
                 
                 break;
			}
			case ACTION_C2C_VOMS_TRF:
            case ACTION_C2C_VOMS_INI:
			case ACTION_EXTVAS_RC_REQUEST:
                {
                	String msisdn = p_requestVO.getRequestMap().get("MSISDN").toString();
    				String pin = p_requestVO.getRequestMap().get("PIN").toString();
                    String networkId = p_requestVO.getRequestNetworkCode();
                    String errorDetails = null;

                    if(!BTSLUtil.isNullString(p_requestVO.getActiverUserId()) && p_requestVO.getIsStaffUser()!=null && p_requestVO.getIsStaffUser()) {//means staff
                    	UserDAO _userDAO = new UserDAO();
                    	UserVO userVO = _userDAO.loadUserDetailsFormUserID(p_con, p_requestVO.getActiverUserId());
                    	channelUserVO = _channelUserDAO.loadStaffUserDetailsByLoginId(p_con, userVO.getLoginID());
                    	settingStaffDetails(channelUserVO);
                    	if(channelUserVO.getUserPhoneVO().getMsisdn()==null) {//means staff has no msisdn
                    		UserPhoneVO parentPhoneVO = _userDAO.loadUserPhoneVO(p_con, channelUserVO.getUserID());//getting parent User phoneVO
                    		channelUserVO.setUserPhoneVO(parentPhoneVO);
                    	}
                    }else	if (!BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                    	errorDetails = p_requestVO.getFilteredMSISDN();
                        channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
                    }
                    if (channelUserVO != null ) {
                        if (!BTSLUtil.isNullString(networkId)) {
                            NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(networkId);
                            if(networkVO==null){
                            String messageArray[]= {networkId};
                           throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID, messageArray);
                            }
                            if (!networkId.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                            }
                            operatorUtili.validatePIN(p_con, channelUserVO, pin);
                        }
                    } else {
                    	String errArgs[] = { errorDetails };
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_NOT_EXIST, 0, errArgs, null);
                    }
                    if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                        p_requestVO.setPinValidationRequired(false);
                    } else if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                        p_requestVO.setPinValidationRequired(false);
                    }
                    if (BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("MSISDN")) || !BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                        p_requestVO.setPinValidationRequired(false);
                    }
                    String chnlMessageSep = CHNL_PLAIN_SMS_SEPARATOR;
                    if (BTSLUtil.isNullString(chnlMessageSep)) {
                        chnlMessageSep = " ";
                    }
                    String message = p_requestVO.getDecryptedMessage();
                    if (!p_requestVO.isPinValidationRequired()) {
                        if (!BTSLUtil.isNullString((String) p_requestVO.getRequestMap().get("PIN"))) {
                            message = message + chnlMessageSep + (String) p_requestVO.getRequestMap().get("PIN");
                        } else {
                            message = message + chnlMessageSep + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                        }
                    } else {
                        if (BTSLUtil.NullToString(p_requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_PIN_BLANK);
                        }
                        message = message + chnlMessageSep + (String) p_requestVO.getRequestMap().get("PIN");
                    }
                    p_requestVO.setDecryptedMessage(message);
                    break;
                }
                
			default: {
				// Load ChannelUser on basis of MSISDN

	            if(!BTSLUtil.isNullString(p_requestVO.getActiverUserId()) && p_requestVO.getIsStaffUser()!=null && p_requestVO.getIsStaffUser()) {//means staff
                	UserDAO _userDAO = new UserDAO();
                	UserVO userVO = _userDAO.loadUserDetailsFormUserID(p_con, p_requestVO.getActiverUserId());
                	channelUserVO = _channelUserDAO.loadStaffUserDetailsByLoginId(p_con, userVO.getLoginID());
                	settingStaffDetails(channelUserVO);
                	if(channelUserVO.getUserPhoneVO().getMsisdn()==null) {//means staff has no msisdn
                		UserPhoneVO parentPhoneVO = _userDAO.loadUserPhoneVO(p_con, channelUserVO.getUserID());//getting parent User phoneVO
                		channelUserVO.setUserPhoneVO(parentPhoneVO);
                	}
                }else {
					channelUserVO = _channelUserDAO.loadChannelUserDetails(p_con, p_requestVO.getFilteredMSISDN());
				}
			}
			}
        	

            // To handle request comming from OperatorReceiver for OPT
            // operations.
            if (channelUserVO != null &&PretupsI.CATEGORY_USER_TYPE.equalsIgnoreCase(channelUserVO.getUserType())) {
                channelUserVO.setGeographicalAreaList(new GeographicalDomainDAO().loadUserGeographyList(p_con, channelUserVO.getUserID(), channelUserVO.getNetworkID()));
                p_requestVO.setActiverUserId(channelUserVO.getUserID());
            }
            
            if (channelUserVO != null &&PretupsI.USER_TYPE_CHANNEL.equalsIgnoreCase(channelUserVO.getUserType())) {
                  p_requestVO.setActiverUserId(channelUserVO.getUserID());
            }
            if (BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()) && channelUserVO != null) {
                p_requestVO.setFilteredMSISDN(PretupsBL.getFilteredMSISDN(channelUserVO.getUserPhoneVO().getMsisdn()));
            }
            validateUserDetails(p_requestVO, channelUserVO);
            if (channelUserVO != null) {
                p_requestVO.setMessageSentMsisdn(channelUserVO.getUserPhoneVO().getMsisdn());

                if (!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId())) {
                    channelUserVO.setStaffUser(true);
                    staffUserVO = _channelUserDAO.loadChannelUserDetailsByUserId(p_con, p_requestVO.getActiverUserId(), channelUserVO.getUserID());
                    validateUserDetails(p_requestVO, staffUserVO);
                    channelUserVO.setActiveUserID(staffUserVO.getUserID());
                    staffUserVO.setActiveUserID(staffUserVO.getUserID());
                    staffUserVO.setStaffUser(true);
                    if (staffUserVO != null && !PretupsI.NOT_AVAILABLE.equals(staffUserVO.getUserPhoneVO().getMsisdn())) {
                        p_requestVO.setMessageSentMsisdn(staffUserVO.getUserPhoneVO().getMsisdn());
                    } else {
                        final UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
                        p_requestVO.setSenderLocale(new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry()));
                    }
                    channelUserVO.setStaffUserDetails(staffUserVO);
                } else {
                    channelUserVO.setActiveUserID(channelUserVO.getUserID());
                }
            }

            p_requestVO.setSenderVO(channelUserVO);

            if (!p_requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                p_requestVO.setPinValidationRequired(false);
            }
        } catch (BTSLBaseException be) {
        	LOG.errorTrace(methodName,be);	
           throw be ;
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting Request ID = " + p_requestVO.getRequestID());
            }
        }
        if (channelUserVO != null && !channelUserVO.isStaffUser()) {
            return channelUserVO;
        } else {
            return staffUserVO;
        }
    }

    /**
     * Method to validate User Details, check various status
     * 
     * @param p_requestVO
     * @param p_channelUserVO
     * @throws BTSLBaseException
     */
    public void validateUserDetails(RequestVO p_requestVO, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String methodName = "validateUserDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered Request ID = " + p_requestVO.getRequestID());
        }
        try {
            if (p_channelUserVO == null) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_NO_SUCH_USER);
            }
            p_requestVO.setLocale(new Locale((p_channelUserVO.getUserPhoneVO()).getPhoneLanguage(), (p_channelUserVO.getUserPhoneVO()).getCountry()));
            if (p_channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_SUSPEND);
            }
            if(p_requestVO.getRequestMessage().contains("SOSTRF")) 
            {
            	return;
            }

            // meditel changes
            
  
            boolean statusAllowed = false;
            final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_channelUserVO.getNetworkID(), p_channelUserVO.getCategoryCode(), p_channelUserVO
                .getUserType(), p_requestVO.getRequestGatewayType());
            if (userStatusVO == null) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
            } else {
                final String userStatusAllowed = userStatusVO.getUserSenderAllowed();
                final String status[] = userStatusAllowed.split(",");
                for (int i = 0; i < status.length; i++) {
                    if (status[i].equals(p_channelUserVO.getStatus())) {
                        statusAllowed = true;
                    }
                }
                if (statusAllowed) {
                    if (!p_channelUserVO.getCategoryVO().getAllowedGatewayTypes().contains(p_requestVO.getMessageGatewayVO().getGatewayType())) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_CAT_GATETYPENOTALLOWED);
                    } else if (p_channelUserVO.getGeographicalCodeStatus().equals(PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND)) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_GEODOMAIN_SUSPEND);
                    }
                } else {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_NOTALLOWED);
                }
            } 
            
        } catch (BTSLBaseException be) {
           LOG.errorTrace(methodName, be);
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting Request ID = " + p_requestVO.getRequestID());
            }
        }

    }

    // Same till when responses are same in USSD and ExtAPI , when separated
    // this method will be overridden in respective parsers
    /**
     * Method to generate Response of P2P requests
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateResponse(int action, RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateResponse", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }

        String messageCode = p_requestVO.getMessageCode();
        if ((!BTSLUtil.isNullString(messageCode)) && (!p_requestVO.isSuccessTxn()) && messageCode.indexOf("_") != -1) {
            messageCode = messageCode.substring(0, messageCode.indexOf('_'));
            p_requestVO.setMessageCode(messageCode);
        }
        switch (action) {
            case ACTION_ACCOUNT_INFO:
                {
                    XMLStringParser.generateGetAccountInfoResponse(p_requestVO);
                    break;
                }
            case CREDIT_TRANSFER:
                {
                    XMLStringParser.generateCreditTransferResponse(p_requestVO);
                    break;
                }
            case CHANGE_PIN:
                {
                    XMLStringParser.generateChangePinResponse(p_requestVO);
                    break;
                }
            case NOTIFICATION_LANGUAGE:
                {
                    XMLStringParser.generateNotificationLanguageResponse(p_requestVO);
                    break;
                }
            case HISTORY_MESSAGE:
                {
                    XMLStringParser.generateHistoryMessageResponse(p_requestVO);
                    break;
                }
            case CREDIT_RECHARGE:
                {
                    XMLStringParser.generateCreditRechargeResponse(p_requestVO);
                    break;
                }
            case SUBSCRIBER_REGISTRATION:
                {
                    XMLStringParser.generateSubscriberRegistrationResponse(p_requestVO);
                    break;
                }
            case SUBSCRIBER_DEREGISTRATION:
                {
                    XMLStringParser.generateSubscriberDeRegistrationResponse(p_requestVO);
                    break;
                }
            case P2P_SERVICE_SUSPEND:
                {
                    XMLStringParser.generateP2PServiceSuspendResponse(p_requestVO);
                    break;
                }
            case P2P_SERVICE_RESUME:
                {
                    XMLStringParser.generateP2PServiceResumeResponse(p_requestVO);
                    break;
                }
            // added for Last Transfer Status(P2P) 03/05/07
            case LAST_TRANSFER_STATUS:
                {
                    XMLStringParser.generateLastTransferStatus(p_requestVO);
                    break;
                }
            case ADD_BUDDY:
                {
                    XMLStringParser.generateAddBuddyResponse(p_requestVO);
                    break;
                }
            case DELETE_BUDDY:
                {
                    XMLStringParser.generateDeleteBuddyResponse(p_requestVO);
                    break;
                }
            // added by harsh on 09Aug12
            case DELETE_MULTLIST:
                {
                    XMLStringParser.generateDelMultCreditListResponse(p_requestVO);
                    break;
                }
            case LIST_BUDDY:
                {
                    XMLStringParser.generateListBuddyResponse(p_requestVO);
                    break;
                }
            case SELF_BAR:
                {
                    XMLStringParser.generateSelfBarResponse(p_requestVO);
                    break;
                }
            // added by jasmine kaur
            case ACTION_REGISTER_SID:
                {
                    XMLStringParser.generatePvtRechargeRegistrationResponse(p_requestVO);
                    break;
                }

            case ACTION_DELETE_SID_REQ: // added by ankuj for generating a
                // response
                // for SID Deletion
                {
                    XMLStringParser.generateDeleteSIDResponse(p_requestVO);
                    break;
                }
            case ACTION_ENQUIRY_SID_REQ: // added by ankuj for generating a
                // response
                // for SID Deletion
                {
                    XMLStringParser.generateEnquirySIDResponse(p_requestVO);
                    break;
                }
            /*
             * case ACTION_EXT_USER_CREATION:
             * {
             * XMLStringParser.generateChannelUserCreationResponse(p_requestVO);
             * break;
             * }
             */
            case ACTION_P2P_CRIT_TRANS: // Added for multiple credit recharge
                {
                    XMLStringParser.generateP2PCRITResponse(p_requestVO);
                    break;
                }
            case P2P_GIVE_ME_BALANCE:
                {
                    XMLStringParser.generateGiveMeBalanceResponse(p_requestVO);
                    break;
                }
            case P2P_LEND_ME_BALANCE:
                {
                    XMLStringParser.generateLendMeBalanceResponse(p_requestVO);
                    break;
                }
            case ACTION_MULT_CDT_TXR_LIST_AMD: // Added for multiple credit
                // recharge
                {
                    XMLStringParser.generateResponseP2PMCDAddModifyDeleteRequest(p_requestVO);
                    break;
                }
            case ACTION_MULT_CDT_TXR_LIST_VIEW: // Added for deleting list for
                // multiple credit
                {
                    XMLStringParser.generateResponseP2PMCDListViewRequest(p_requestVO);
                    break;
                }
            case ACTION_MULT_CDT_TXR_LIST_REQUEST: // Added for deleting list
                // for
                // multiple credit
                {
                    XMLStringParser.generateResponseP2PMCDListCreditRequest(p_requestVO);
                    break;
                }
            case ACTION_C2S_RPT_LAST_XTRANSFER: // rahuld
                {
                    XMLStringParser.generateC2SLastXTransferStatusResponse(p_requestVO);
                    break;
                }
            // added by harsh for Scheduled Credit List (Add/Modify/Delete) API
            case ACTION_MULT_CDT_TXR_SCDLIST_AMD:
                {
                    XMLStringParser.generateResponseP2PSMCDAddModifyDeleteRequest(p_requestVO);
                    break;
                }
            case SCHEDULE_CREDIT_TRANSFER:
                {
                    XMLStringParser.generateScheduleCreditTransferResponse(p_requestVO);
                    break;
                }
            // added by pradyumn for Scheduled Credit List Delete list API
            case ACTION_MULT_CDT_TXR_SCDLIST_DLT:
                {
                    XMLStringParser.generateResponseP2PSMCDDeleteListRequest(p_requestVO);
                    break;
                }

            // added by pradyumn for Scheduled Credit List view API
            case ACTION_MULT_CDT_TXR_SCDLIST_VEW:
                {
                    XMLStringParser.generateResponseP2PSMCDViewRequest(p_requestVO);
                    break;
                }
            case ACTION_VOUCHER_CONSUMPTION:
                {
                    ExtAPIXMLStringParser.generateVoucherConsumptionResponse(p_requestVO, "VOMSCONSRES");
                    break;
                }

            // added for voms
            case ACTION_VOMS_ENQ:
                {
                    ExtAPIXMLStringParser.generateVoucherEnqResponse(p_requestVO);
                    break;
                }
            case ACTION_VOMS_CON:
                {
                    ExtAPIXMLStringParser.generateVoucherConsResponse(p_requestVO);
                    break;
                }
            case ACTION_VOMS_RET:
                {
                    ExtAPIXMLStringParser.generateVoucherRetResponse(p_requestVO);
                    break;
                }

            // added for voucher query and rollback request
            case ACTION_VOMS_QRY:
                {
                	if(p_requestVO.getRequestMap().get(VOMSI.MASTER_SERIAL_NO) != null && BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get(VOMSI.MASTER_SERIAL_NO))){
                		ExtAPIXMLStringParser.generatePackageVoucherEnquiryResponse(p_requestVO);
                	}
                	else {              		
                		ExtAPIXMLStringParser.generateVoucherQueryResponse(p_requestVO);
                	}
                    break;
                }
            case ACTION_VOMS_ROLLBACK:
                {
                    ExtAPIXMLStringParser.generateVoucherRollBackResponse(p_requestVO);
                    break;
                }
            case ACTION_VOMS_RETAGAIN: // added to generate responce for get
                // voucher
                // again after request time out from VOMS
                {
                    ExtAPIXMLStringParser.generateGetVoucherRetResponse(p_requestVO);
                    break;
                }
            // added for Voucher TXN ID and SUB ID RollBack Request
            case ACTION_VOMS_RETRIEVAL_ROLLBACK:
                {
                    ExtAPIXMLStringParser.generateVoucherRetrievalRollBackResponse(p_requestVO);
                    break;
                }
					case IAT_P2P_RECHRG:
			{
			XMLStringParser.generateP2PIATCreditResponse(p_requestVO);
				break;
			}
        case ACTION_VOUCHER_STATUS_CHANGES: 
            {
            	p_requestVO.getResponseMap().put("TYPE","VOMSSTCHGRES");
            	ExtAPIStringParser.generateExtStringResponse(p_requestVO);            
                break; 
            }
        case ACTION_VOUCHER_EXPIRY_CHANGES: 
        {
        	p_requestVO.getResponseMap().put("TYPE","VOMSSTCHGRES");
        	ExtAPIStringParser.generateExtStringResponse(p_requestVO);            
            break; 
        }
		  case ACTION_VAS_VOUCHER_CONSUMPTION:
			{
				ExtAPIXMLStringParser.generateVASVoucherConsumptionResponse(p_requestVO, "VOMSVASCONSRES");
				break;
			}	
			case ACTION_DATA_CP2P_RECHARGE:
			{
				XMLStringParser.generateChannelCP2PDataTransferResponse(p_requestVO);
				break;
			}
			case ACTION_VMS_PIN_EXP_EXT_REQ: 
			{
				ExtAPIXMLStringParser.gernerateVmsPinExpiryExtResponse(p_requestVO);
                break;
			}
					///////////////VHA START////////////////////////////////////////
			case ACTION_VOMS_VALIDATE_REQ:
            {
            	ClientExtAPIXMLStringParserI clientExtAPIXMLStringParserI = (ClientExtAPIXMLStringParserI) ObjectProducer.getObject(BLConstants.Client_ExtAPIXMLS_tringParser_Obj, BLConstants.BL_PRODUCER);
            	clientExtAPIXMLStringParserI.generateVoucherValidateResponse(p_requestVO);
                break;
            }
			case ACTION_VOMS_RESERVE_REQ:
            {
            	ClientExtAPIXMLStringParserI clientExtAPIXMLStringParserI = (ClientExtAPIXMLStringParserI) ObjectProducer.getObject(BLConstants.Client_ExtAPIXMLS_tringParser_Obj, BLConstants.BL_PRODUCER);
            	clientExtAPIXMLStringParserI.generateVoucherReserveResponse(p_requestVO);
                break;
            }
			case ACTION_VOMS_DIRECT_CONSUMPTION_REQ:
            {
            	ClientExtAPIXMLStringParserI clientExtAPIXMLStringParserI = (ClientExtAPIXMLStringParserI) ObjectProducer.getObject(BLConstants.Client_ExtAPIXMLS_tringParser_Obj, BLConstants.BL_PRODUCER);
            	clientExtAPIXMLStringParserI.generateVoucherDirectConsumptionResponse(p_requestVO);
                break;
            }
            case ACTION_VOMS_DIRECT_ROLLBACK_REQ:
            {
            	ClientExtAPIXMLStringParserI clientExtAPIXMLStringParserI = (ClientExtAPIXMLStringParserI) ObjectProducer.getObject(BLConstants.Client_ExtAPIXMLS_tringParser_Obj, BLConstants.BL_PRODUCER);
            	clientExtAPIXMLStringParserI.generateVoucherDirectRollbackResponse(p_requestVO);
                break;
            }
            case ACTION_VOMS_MY_VOUCHR_ENQUIRY_SUBSCRIBER_REQ:
            {
            	ExtAPIXMLStringParser.generateSelfVoucherSubscriberEnqResponse(p_requestVO);
                break;
            }
            case  ACTION_SUBS_THR_ENQ_REQUEST: //Added for Subscriber Threshold Enquiry 
            {
            	ExtAPIXMLStringParser.generateResponseSubscriberThresholdEnquiry(p_requestVO);
                break;  
            }
            default:
     	     	 if(LOG.isDebugEnabled()){
     	     		LOG.debug("Default Value " ,action);
     	     	 }
        }
    }

    // Same till when responses are same in USSD and ExtAPI , when separated
    // this method will be overridden in respective parsers
    /**
     * Method to generate Response of Channel requests
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateChannelResponse(int action, RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateChannelResponse", "Entered Request ID=" + p_requestVO.getRequestID() + " action=" + action);
        }

        String messageCode = p_requestVO.getMessageCode();
        if ((!BTSLUtil.isNullString(messageCode)) && (!p_requestVO.isSuccessTxn()) && messageCode.indexOf("_") != -1) {
            messageCode = messageCode.substring(0, messageCode.indexOf("_"));
            p_requestVO.setMessageCode(messageCode);
        }
        switch (action) {
        	
        case TOTAL_USER_INCOME_DETAILS_VIEW:
    	{
    		ExtAPIXMLStringParser.generateTotalIcomeDetailsViewResponse(p_requestVO);
    		break;
    	}
        	case ACTION_UPUSRHRCHY: {
        		
        		
        		ExtAPIXMLStringParser.generateUserHierarchyResponse(p_requestVO);
        		break;
        	}
        	case C2C_BUY_ENQ: {

        		ExtAPIXMLStringParser.generateC2cBuyUserResponse(p_requestVO);
        		break;
        	}
        	case C2S_SV_DETAILS: {

        		ExtAPIXMLStringParser.generateC2sServiceDetailsResponse(p_requestVO);
        		break;
        	}
            case ACTION_CHNL_CREDIT_TRANSFER:
                {
                    ExtAPIXMLStringParser.generateChannelCreditTransferResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CHANGE_PIN:
                {
                    ExtAPIXMLStringParser.generateChannelChangeResponse(p_requestVO, "RCPNRESP");
                    break;
                }
            case ACTION_CHNL_NOTIFICATION_LANGUAGE:
                {
                    // ExtAPIXMLStringParser.generateChannelNotificationLanguageResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelChangeResponse(p_requestVO, "RCLANGRESP");
                    break;
                }
            case ACTION_CHNL_TRANSFER_MESSAGE:
                {
                    // ExtAPIXMLStringParser.generateChannelTransferResponse(p_requestVO,ACTION_CHNL_TRANSFER_MESSAGE);
                    ExtAPIXMLStringParser.generateChannelChangeResponse(p_requestVO, "TRFRESP");
                    break;
                }
            case ACTION_CHNL_WITHDRAW_MESSAGE:
                {
                    // ExtAPIXMLStringParser.generateChannelTransferResponse(p_requestVO,ACTION_CHNL_WITHDRAW_MESSAGE);
                    ExtAPIXMLStringParser.generateChannelChangeResponse(p_requestVO, "WDTHRESP");
                    break;
                }
            case ACTION_CHNL_RETURN_MESSAGE:
                {
                    // ExtAPIXMLStringParser.generateChannelTransferResponse(p_requestVO,ACTION_CHNL_RETURN_MESSAGE);
                    ExtAPIXMLStringParser.generateChannelChangeResponse(p_requestVO, "RETRESP");
                    break;
                }

            case ACTION_CHNL_POSTPAID_BILLPAYMENT:
                {
                    ExtAPIXMLStringParser.generateChannelPostPaidBillPaymentResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_O2C_INITIATE:
                {
                    ExtAPIXMLStringParser.generateChannelO2CAPIResponse(p_requestVO, "O2CINRESP");
                    break;
                }
            case ACTION_CHNL_O2C_INITIATE_TRFR:
                {
                    // ExtAPIXMLStringParser.generateChannelO2CInitiateTrfrAPIResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelO2CAPIResponse(p_requestVO, "O2CINTRESP");
                    break;
                }
            case ACTION_CHNL_O2C_RETURN:
                {
                    // ExtAPIXMLStringParser.generateChannelO2CReturnAPIResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelO2CAPIResponse(p_requestVO, "OCRETRESP");
                    break;
                }
            case ACTION_CHNL_O2C_WITHDRAW:
                {
                    // ExtAPIXMLStringParser.generateChannelO2CWithdrawAPIResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelO2CAPIResponse(p_requestVO, "O2CWDRESP");
                    break;
                }
            case ACTION_CHNL_EXT_RECH_STATUS:
                {
                    ExtAPIXMLStringParser.generateChannelExtRechargeStatusResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER:
                {
                    ExtAPIXMLStringParser.generateChannelExtRechargeStatusResponse(p_requestVO);
                    break;
                }
            // added for Balance Enquiry 03/05/07
            case ACTION_CHNL_BALANCE_ENQUIRY:
                {
                    XMLStringParser.generateChannelBalanceEnquiryResponse(p_requestVO);
                    break;
                }
            // added for Daily Status Report 03/05/07
            case ACTION_CHNL_DAILY_STATUS_REPORT:
                {
                    XMLStringParser.generateChannelDailyStatusReportResponse(p_requestVO);
                    break;
                }
            // added for Last Transfer Status(RP2P) 03/05/07
            case ACTION_CHNL_LAST_TRANSFER_STATUS:
                {
                    XMLStringParser.generateChannelLastTransferStatusResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EVD_REQUEST:
                {
                    XMLStringParser.generateEVDResponse(p_requestVO);
                    break;
                }
            case ACTION_MULTIPLE_VOUCHER_DISTRIBUTION:
                {
                    XMLStringParser.generateMultipleVoucherDistributionResponse(p_requestVO);
                    break;
                }
            case ACTION_UTILITY_BILL_PAYMENT:
                {
                    XMLStringParser.generateUtilityBillPaymentResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_ENQUIRY_REQUEST:
                {
                    ExtAPIXMLStringParser.generateC2SEnquiryResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_TRANSFER_BILL_PAYMENT:
                {
                    // ExtAPIXMLStringParser.generateExtPostpaidBillPaymentResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXTPPBRESP");
                    break;
                }
            case ACTION_CHNL_EXT_POST_RECHARGE_STATUS:
                {
                    ExtAPIXMLStringParser.generateChannelExtPostRechargeStatusResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_COMMON_RECHARGE:
                {
                    // ExtAPIXMLStringParser.generateChannelExtCommonRechargeResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "CEXRCTRFRESP");
                    break;
                }
            case ACTION_CHNL_GIFT_RECHARGE_USSD:
                {
                    XMLStringParser.generateGiftRechargeResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_GIFT_RECHARGE_XML:
                {
                    ExtAPIXMLStringParser.generateExtGiftRechargeResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EVD_XML:
                {
                    ExtAPIXMLStringParser.generateExtEVDResponse(p_requestVO);
                    break;
                }
            case ACTION_C2C_TRANSFER_EXT_XML:
                {
                    ExtAPIXMLStringParser.generateExtC2CTransferResponse(p_requestVO, ACTION_C2C_TRANSFER_EXT_XML);
                    break;
                }
            case ACTION_C2C_WITHDRAW_EXT_XML:
                {
                    ExtAPIXMLStringParser.generateExtC2CTransferResponse(p_requestVO, ACTION_C2C_TRANSFER_EXT_XML);
                    break;
                }
            case ACTION_C2C_RETURN_EXT_XML:
                {
                    ExtAPIXMLStringParser.generateExtC2CTransferResponse(p_requestVO, ACTION_C2C_TRANSFER_EXT_XML);
                    break;
                }
            case ACTION_CHNL_CREDIT_TRANSFER_CDMA: // added to CDMA Recharge
                {
                    XMLStringParser.generateChannelCreditTransferCDMAResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CREDIT_TRANSFER_PSTN: // added to PSTN Recharge
                {
                    XMLStringParser.generateChannelCreditTransferPSTNResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CREDIT_TRANSFER_INTR: // added to INTERNET Recharge
                {
                    XMLStringParser.generateChannelCreditTransferINTRResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER_CDMA:// Added for CDMA Bank
                // Recharge
                {
                    // ExtAPIXMLStringParser.generateChannelExtCreditTransferResponseCDMA(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelExtTransferResponse(p_requestVO, "EXCDMARCRESP");
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER_PSTN:// Added for PSTN Bank
                // Recharge
                {
                    // ExtAPIXMLStringParser.generateChannelExtCreditTransferResponsePSTN(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelExtTransferResponse(p_requestVO, "EXPSTNRCRESP");
                    break;
                }
            case ACTION_CHNL_EXT_CREDIT_TRANSFER_INTR:// Added for INTR Bank
                // Recharge
                {
                    // ExtAPIXMLStringParser.generateChannelExtCreditTransferResponseINTR(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelExtTransferResponse(p_requestVO, "EXINTRRCRESP");
                    break;
                }
            case ACTION_CHNL_ORDER_LINE: // added to Order Line
                {
                    XMLStringParser.generateChannelOrderLineResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_ORDER_CREDIT: // added to Order Credit
                {
                    XMLStringParser.generateChannelOrderCreditResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_BARRING: // added to Barring
                {
                    XMLStringParser.generateChannelBarringResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_IAT_ROAM_RECHARGE: // added to Iat roam recharge
                {
                    XMLStringParser.generateIATRoamRechargeResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_IAT_INTERNATIONAL_RECHARGE: // added to Iat
                // international recharge
                {
                    XMLStringParser.generateIATInternationalRechargeResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_C2S_LAST_XTRANSFER: // last X C2S transfer
                {
                    XMLStringParser.generateChannelLastXTransferStatusResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CUST_LAST_XTRANSFER: // last X C2S transfer
                {
                    XMLStringParser.generateChannelXEnquiryStatusResponse(p_requestVO);
                    break;
                }
            case ACTION_EXT_MVD_DWNLD_RQST: // response mvd voucher download
                // done by
                // ashishT
                {
                    XMLStringParser.generateMVDDownloadResponse(p_requestVO);
                    break;
                }
            case ACTION_C2S_TRANS_ENQ:
                {
                    XMLStringParser.generate2STransferEnqResp(p_requestVO);// for
                    // channel
                    // transaction
                    // enquiry
                    // request
                    // date/msisdn
                    // by RahulD
                    break;
                }
            // added by jasmine kaur
            case ACTION_REGISTER_SID:
                {
                    XMLStringParser.generatePvtRechargeRegistrationResponse(p_requestVO);
                    break;
                }

            case ACTION_ENQUIRY_TXNIDEXTCODEDATE:
                {
                    ExtAPIXMLStringParser.parseEnquiryTxnIDExtCodeDateResponse(p_requestVO);
                    break;
                }
            case ACTION_DELETE_SID_REQ: // added by ankuj for generating a
                // response
                // for SID Deletion
                {
                    XMLStringParser.generateDeleteSIDResponse(p_requestVO);
                    break;
                }
            case ACTION_ENQUIRY_SID_REQ: // added by ankuj for generating a
                // response
                // for SID Deletion
                {
                    XMLStringParser.generateEnquirySIDResponse(p_requestVO);
                    break;
                }
            /*
             * Added By Babu Kunwar For Last Transaction Details
             */
            case ACTION_EXT_LAST_TRF: // last X C2S transfer
                {
                    ExtAPIXMLStringParser.generateExtChannelLastTransferStatusResponse(p_requestVO);
                    break;
                }
            /*
             * Added By Babu Kunwar For Channel User Balance Enquiry
             */
            case ACTION_CHNL_BAL_ENQ_XML:
                {
                    ExtAPIXMLStringParser.generateExtChannelUserBalanceResponse(p_requestVO);
                    break;
                }
            /*
             * Added By Babu Kunwar for Other User Balance
             */
            case ACTION_EXT_OTHER_BAL_ENQ:
                {
                    ExtAPIXMLStringParser.generateExtChannelUserOtherBalRes(p_requestVO);
                    break;
                }
            /*
             * Added By Babu Kunwar For Change Pin
             */
            case ACTION_EXT_C2SCHANGEPIN_XML:
                {
                    ExtAPIXMLStringParser.generateExtChangepinResponse(p_requestVO);
                    break;
                }
            /*
             * Added By Babu Kunwar for Last 3 Transfer
             */
            case ACTION_EXT_LAST_XTRF_ENQ: // last X C2S transfer
                {
                    XMLStringParser.generateExtLastXTrfResponse(p_requestVO);
                    break;
                }
            /*
             * Added By Babu Kunwar For Customer Enquiry
             */
            case ACTION_EXT_CUSTOMER_ENQ_REQ: // last X C2S transfer
                {
                    XMLStringParser.generateExtCustomerEnqResponse(p_requestVO);
                    break;
                }
            /*
             * Added By Babu Kunwar For Daily Transfer Reports
             */
            case ACTION_EXT_DAILY_STATUS_REPORT: // last X C2S transfer
                {
                    ExtAPIXMLStringParser.generateExtDailyTransactionResponse(p_requestVO);
                    break;
                }

            case ACTION_CRBT_REGISTRATION:// For CRBT Registration by Shashank
                {
                    XMLStringParser.generateCRBTRegistrationResponse(p_requestVO);
                    break;
                }
            case ACTION_CRBT_SONG_SELECTION:// For CRBT Song Selection by
                // Shashank
                {
                    XMLStringParser.generateCRBTSongSelectionResponse(p_requestVO);
                    break;
                }
            case ACTION_ELECTRONIC_VOUCHER_RECHARGE:// For EVR by Harpreet
                {
                    XMLStringParser.generateElectronicVoucherRechargeResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_EVR_XML:// For EVR through XML API by Harpreet
                {
                    // ExtAPIXMLStringParser.generateExtEVRResponse(p_requestVO);
                    ExtAPIXMLStringParser.generateChannelExtTransferResponse(p_requestVO, "EXEVRTRFRESP");
                    break;
                }
            case SUSPEND_RESUME_CUSR:
                {
                    ExtAPIXMLStringParser.generateChannelUserSuspendResumeResponse(p_requestVO);
                    break;
                }
            case ACTION_EXT_DRCR_C2C_CUSER:
                {
                    ExtAPIXMLStringParser.generateExtC2CTrfDrCrResponse(p_requestVO, ACTION_EXT_DRCR_C2C_CUSER);// added
                    // for
                    // DrCr
                    // Transfer
                    // through
                    // External
                    // Gateway
                    break;
                }
            case ACTION_EXT_USER_CREATION:
                {
                    XMLStringParser.generateChannelUserCreationResponse(p_requestVO);
                    break;
                }
            case ACTION_C2S_RPT_LAST_XTRANSFER:
                {
                    XMLStringParser.generateC2SLastXTransferStatusResponse(p_requestVO);
                    break;
                }
            // added by shashank for channel user authentication
            case ACTION_CRM_USER_AUTH_XML:
                {
                    XMLStringParser.generateChannelUserAuthResponse(p_requestVO);
                    break;
                }
            case ACTION_USSD_SIM_ACT_REQ: // changed for sim activation
                {
                    XMLStringParser.generateSIMACTResponse(p_requestVO);
                    break;
                }
            case ACTION_VAS_RC_REQUEST:
                {
                    XMLStringParser.generateChannelVasTransferResponse(p_requestVO);
                    break;
                }
            case ACTION_PVAS_RC_REQUEST:
                {
                    XMLStringParser.generateChannelPrVasTransferResponse(p_requestVO);
                    break;
                }
            case ACTION_EXT_SUBENQ: // added by sonali garg to enquire
                // subscriber at
                // IN
                {
                    ExtAPIXMLStringParser.generateChannelExtSubscriberEnqResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_HLPDESK_REQUEST:
                {
                    XMLStringParser.generateChannelHelpDeskResponse(p_requestVO);
                    break;
                }
            case ACTION_COL_ENQ:
                {
                    ExtAPIXMLStringParser.generateChannelExtColEnqResponse(p_requestVO);
                    break;
                }
            case ACTION_COL_BILLPAYMENT:
                {
                    ExtAPIXMLStringParser.generateChannelCollectionBillPaymentResponse(p_requestVO);
                    break;
                }
            case ACTION_DTH:
                {
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_DTH);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXDTHTRFRESP");
                    break;
                }
            case ACTION_DC:
                {
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXDCTRFRESP");
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_DC);
                    break;
                }
            case ACTION_BPB:
                {
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_BPB);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXBPBTRFRESP");
                    break;
                }
            case ACTION_PIN:
                {
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_PIN);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXPINTRFRESP");
                    break;
                }
            case ACTION_PMD:
                {
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_PMD);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXPMDTRFRESP");
                    break;
                }
            case ACTION_FLRC:
                {
                    // ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,ACTION_FLRC);
                    ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXFLRCTRFRESP");
                    break;
                }
            case ACTION_C2S_POSTPAID_REVERSAL:
                {
                    ExtAPIXMLStringParser.generateC2SPostPaidReversalResponse(p_requestVO);
                    break;
                }
            // added by brajesh prasad for LMS Points Enquiry
            case ACTION_CHNL_LMS_POINTS_ENQUIRY:
                {
                    p_requestVO.getResponseMap().put("TYPE", "LMSPTENQRES");
                    p_requestVO.getResponseMap().put("POINTS", p_requestVO.getCurrentLoyaltyPoints());
					p_requestVO.getResponseMap().put("PRODUCTCODE",p_requestVO.getProductCode());
                    ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.TOTAL_LOYALTY_POINTS_FOR_USER);
                    ExtAPIStringParser.generateExtStringResponse(p_requestVO);
                    p_requestVO.setSenderMessageRequired(false);
                    // ExtAPIXMLStringParser.generateLMSPointsEnquiryResponse(p_requestVO);
                    break;
                }
            // added by brajesh prasad for LMS Points Redemption
            case ACTION_CHNL_LMS_POINTS_REDEMPTION:
                {
                    p_requestVO.getResponseMap().put("TYPE", "LMSPTREDRES");
                    p_requestVO.getResponseMap().put("REDTXNID", p_requestVO.getRedemptionId());
                    p_requestVO.getResponseMap().put("REMPOINTS", p_requestVO.getCurrentLoyaltyPoints());
                    p_requestVO.getResponseMap().put("CREDITEDAMOUNT", p_requestVO.getCreditedAmount());
					p_requestVO.getResponseMap().put("PRODUCTCODE",p_requestVO.getProductCode());
                    ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.TOTAL_LOYALTY_REDEMPTION_AND_AMOUNT);
                    ExtAPIStringParser.generateExtStringResponse(p_requestVO);
                    p_requestVO.setSenderMessageRequired(false);
                    // ExtAPIXMLStringParser.generateLMSPointsRedemptionResponse(p_requestVO);
                    break;
                }
            // Added by Surabhi for GetMyMSISDN
            case GET_MY_MSISDN:
                {
                    ExtAPIXMLStringParser.generateGetMyNumberResponse(p_requestVO, "EXMSISDNRSP");
                    break;
                }
            case ACTION_INITIATE_PIN_RESET:
                {
                    p_requestVO.getResponseMap().put("TYPE", "INPRESET");
                    ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.SECURITY_QUESTION);
                    ExtAPIStringParser.generateExtStringResponse(p_requestVO);
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_PIN_RESET:
                {
                    p_requestVO.getResponseMap().put("TYPE", "PINRESET");
                    ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.PIN_RESET_SUCCESSFUL);
                    ExtAPIStringParser.generateExtStringResponse(p_requestVO);
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_DATA_UPDATE:
                {
                    p_requestVO.getResponseMap().put("TYPE", "DUPDATE");
                    ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.DATA_UPDATION_SUCCESSFUL);
                    ExtAPIStringParser.generateExtStringResponse(p_requestVO);
                    p_requestVO.setSenderMessageRequired(false);
                    break;
                }
            case ACTION_CUST_C2S_ENQ_REQ:
                {
                    XMLStringParser.generateC2STrfEnquiryUSSDResponse(p_requestVO);
                }
                
            case ACTION_C2S_PRE_PAID_REVERSAL:
			{
				ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO,"RCREVRESP");
				break;
	        }    
            case ACTION_CHNL_LITE_RECHARGE:
			{
				p_requestVO.getResponseMap().put("TYPE","RCTRFSERRESP");
				p_requestVO.getResponseMap().put("TXNID",p_requestVO.getTransactionID());
				if(p_requestVO.getRequestGatewayCode().equals(PretupsI.GATEWAY_TYPE_USSD)){
					ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				}
				else{
				ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.C2S_SENDER_SUCCESS);
				}
				ExtAPIStringParser.generateExtStringResponse(p_requestVO);
				break;
			}
            case ACTION_CHNL_CARDGROUP_ENQUIRY_REQUEST:
			{
				ExtAPIStringParser.generateResponseCardGroupEnquiryRequest(p_requestVO);
				break;
			}
            case ACTION_EXDATATRFREQ_REQUEST:
            {
                ExtAPIXMLStringParser.generateC2STransferResponse(p_requestVO, "EXDATATRFRESP");
                break;
            }
            case ACTION_EXT_DSR_REQUEST:
            {
                ExtAPIXMLStringParser.generateDSRResponse(p_requestVO, "EXTDCSRRESP");
                break;
            }
            case ACTION_EXT_STOCK_BALANCE_REQUEST:
            {
                ExtAPIXMLStringParser.generateStockBalanceResponse(p_requestVO, "EXTSTKBALRESP");
                break;
            }
            case ACTION_CHNL_SOS_SETTLEMENT_REQUEST:
			{
				ExtAPIXMLStringParser.generateSOSSettlementResponse(p_requestVO);
				break;
			}
            case ACTION_CHNL_SOS_REQUEST:
			{
				ExtAPIXMLStringParser.generateSOSResponse(p_requestVO);
				break;
			}
			//Added for Channel User Commission Earned Enq Response
			case ACTION_USER_COMM_EARNED:
			{
				XMLStringParser.generateCUCommEarnedEnqResponse(p_requestVO);
				break;
			}
			case ACTION_USER_CARDGROUP_ENQUIRY_REQUEST:
			{
				ExtAPIStringParser.generateChannelCardGroupEnquiryResponse(p_requestVO);
				break;
			}
			case ACTION_CHNL_DVD_XML:
            {
                XMLStringParser.generateDVDResponse(p_requestVO);
                break;
            }
            
			case ACTION_CHNL_VOUCHER_AVAILABILITY_XML:
			{
				ExtAPIXMLStringParser.userAvailableVoucherEnquiryResponse(p_requestVO);
	            break;
			}
			case ACTION_C2C_REQ_REC:
            {
                // ExtAPIXMLStringParser.generateChannelTransferResponse(p_requestVO,ACTION_CHNL_WITHDRAW_MESSAGE);
                ExtAPIXMLStringParser.generateExtC2CTransferRequestResponse(p_requestVO, ACTION_C2C_REQ_REC);
                break;
            }
			case ACTION_C2C_APPR:
			{
				ExtAPIXMLStringParser.generateExtC2CTransferApprovalResponse(p_requestVO, ACTION_C2C_APPR);
                break;
			}
			case ACTION_C2C_VOUCHER_APPR:
			{
				ExtAPIXMLStringParser.generateExtC2CTransferApprovalResponse(p_requestVO, ACTION_C2C_VOUCHER_APPR);
                break;
			}
		
			case ACTION_C2C_VOMS_TRF:
			{
				ExtAPIXMLStringParser.generateExtC2CVomsTransferRequestResponse(p_requestVO, ACTION_C2C_VOMS_TRF);
                break;
			}
			 case ACTION_C2C_VOMS_INI:
				{
	                ExtAPIXMLStringParser.generateExtC2CVomsInitiateRequestResponse(p_requestVO, ACTION_C2C_VOMS_INI);
	                break;
				}
			 case CHANNEL_USER_DETAILS:
				{
	                ExtAPIXMLStringParser.generateChannelUserDetailsResponse(p_requestVO, CHANNEL_USER_DETAILS);
	                break;
				}
			 case C2S_PROD_TXN_DETAILS: {

	        		ExtAPIXMLStringParser.generateTxnCountDetailsResponse(p_requestVO,C2S_PROD_TXN_DETAILS);
	        		break;
	        	}
			 case PASSBOOK_VIEW_DETAILS:
			 {
				 ExtAPIXMLStringParser.generatePassbookDetailsViewResponse(p_requestVO);
	        		break;
			 }
			 case C2S_TOTAL_TRANSACTION_COUNT:
				{
	                ExtAPIXMLStringParser.generateTotalTnxCountResponse(p_requestVO, C2S_TOTAL_TRANSACTION_COUNT);
	                break;
				}
			 case C2S_N_PROD_TXN_DETAILS: {

	        		ExtAPIXMLStringParser.generateTxnCountDetailsResponse(p_requestVO,C2S_N_PROD_TXN_DETAILS);
	        		break;
	        	}
			 case TOTAL_TRANSACTION_DETAILED_VIEW:
				{
	                ExtAPIXMLStringParser.generateTotalTnxDetailedResponse(p_requestVO, TOTAL_TRANSACTION_DETAILED_VIEW);
	                break;
				}
			 case COMMISSION_CALCULATOR:
				{
	                ExtAPIXMLStringParser.generateCommissionCalculatorResponse(p_requestVO, COMMISSION_CALCULATOR);
	                break;
				}
				case ACTION_EXTVAS_RC_REQUEST:
             {
            	 USSDC2SXMLStringParser.generateVasExtCreditTransferResponse(p_requestVO);
                 break;
             }
				
 			case ACTION_LST_LOAN_ENQ:
 			{
 				
 				ExtAPIXMLStringParser.generateLastLoanEnqResponse(p_requestVO);
 				break;
 			}
 			
 			case ACTION_LOAN_OPTIN_REQ:
            {
                ExtAPIXMLStringParser.generateLoanOptInOptOutResponse(p_requestVO);
                break;
            }
			case ACTION_LOAN_OPTOUT_REQ:
            {
                ExtAPIXMLStringParser.generateLoanOptInOptOutResponse(p_requestVO);
                break;
            }

            case ACTION_SELF_CUBAR: 
            {
                
                XMLStringParser.generateSelfChannelUserBarResponse(p_requestVO);
            	break;
            }
            
            case ACTION_SELF_CU_UNBAR: 
            {
            	XMLStringParser.generateSelfChannelUserBarResponse(p_requestVO);
                break;
            }
            
    	    case ACTION_SELF_PIN_RESET:
            {
                p_requestVO.getResponseMap().put("TYPE", "SPINRESET");
                ExtAPIStringParser.populateResponseMap(p_requestVO, PretupsErrorCodesI.PIN_RESET_SUCCESSFUL);
                ExtAPIStringParser.generateExtStringResponse(p_requestVO);
                p_requestVO.setSenderMessageRequired(false);
                break;
            }
            
			default:
     	     	 if(LOG.isDebugEnabled()){
     	     		LOG.debug("Default Value " ,action);
     	     	 }
        }
    }
 
    /**
     * Method to prepare the Failure Response Message
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateFailureResponse(RequestVO p_requestVO) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateFailureResponse", "Entered Request ID=" + p_requestVO.getRequestID());
        }
        try {
            ExtAPIXMLStringParser.generateFailureResponse(p_requestVO);
        } catch (Exception e) {
        	throw new BTSLBaseException("ParserUtility", "generateFailureResponse", "Exception in generating Failure response");
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateFailureResponse", "Exiting Request ID=" + p_requestVO.getRequestID());
            }
        }
    }

    /**
     * Method to mark and unmark the request for subscriber
     * 
     * @param p_con
     * @param p_requestVO
     * @param p_module
     * @param p_mark
     * @throws BTSLBaseException
     */
    public void checkRequestUnderProcess(Connection p_con, RequestVO p_requestVO, String p_module, boolean p_mark, ChannelUserVO channeluserVO) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkRequestUnderProcess",
                "Entered Request ID=" + p_requestVO.getRequestID() + " p_module=" + p_module + " p_mark=" + p_mark + " Check Required=" + p_requestVO.getMessageGatewayVO()
                    .getRequestGatewayVO().getUnderProcessCheckReqd());
        }
        final String METHOD_NAME = "checkRequestUnderProcess";
        try {
            if (TypesI.YES.equals(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd())) {
                if (PretupsI.C2S_MODULE.equals(p_module)) {
                    ChannelUserBL.checkRequestUnderProcess(p_con, p_requestVO.getRequestIDStr(), channeluserVO.getUserPhoneVO(), p_mark);
                }

            }
		else {
    			if(PretupsI.C2S_MODULE.equals(p_module)  && !PretupsI.REQUEST_SOURCE_TYPE_XMLGW.equalsIgnoreCase(p_requestVO.getRequestGatewayType()) && !PretupsI.USER_TYPE_OPT.equalsIgnoreCase(channeluserVO.getUserType()) && channeluserVO.getUserPhoneVO()!=null && channeluserVO.getUserPhoneVO().getLastTransferID()!=null)
    			{
    				ChannelUserBL.checkRequestUnderProcess(p_con,p_requestVO.getRequestIDStr(),channeluserVO.getUserPhoneVO(),false);
    			}
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "validateUserDetails", PretupsErrorCodesI.ERROR_EXCEPTION);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkRequestUnderProcess", "Exiting For Request ID=" + p_requestVO.getRequestID());
        }

    }

    /**
     * Update parent user information
     * 
     * @param p_con
     *            Connection
     * @param p_requestVO
     *            RequestVO
     * @throws BTSLBaseException
     */
    public void updateUserInfo(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug(this, "updateUserInfo Entered p_requestVO: " + p_requestVO);
        }
        final String METHOD_NAME = "updateUserInfo";
        try {
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

            if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                final ChannelUserVO userVO = channelUserDAO.loadActiveUserId(p_con, p_requestVO.getSenderLoginID(), "LOGINID");
                if (userVO != null) {
                    p_requestVO.setActiverUserId(userVO.getUserID());
                } else {
                    throw new BTSLBaseException(this, "updateUserInfo", PretupsErrorCodesI.NO_USER_EXIST);
                }
                // if sender is staff user then load msisdn of parent channel
                // user and set it in requestMsisdn field,
                // otherwise set msisdn of channel user in the field
                // Changed according to new requirement now staff user may have
                // his own MSISDN.
                String parentMsisdn = null;
                if ((!BTSLUtil.isNullString(userVO.getMsisdn())) && (!PretupsI.STAFF_USER_TYPE.equals(userVO.getUserType()))) {
                    p_requestVO.setRequestMSISDN(userVO.getMsisdn());
                } else {
                    // parentMsisdn=channelUserDAO.loadParentUserMsisdn(p_con,p_requestVO.getSenderLoginID(),false);
                    parentMsisdn = channelUserDAO.loadParentUserMsisdn(p_con, p_requestVO.getSenderLoginID(), "LOGINID");
                    p_requestVO.setRequestMSISDN(parentMsisdn);
                }
            } else if (!BTSLUtil.isNullString(p_requestVO.getRequestMSISDN())) {
                final String filteredMsisdn = PretupsBL.getFilteredMSISDN(p_requestVO.getRequestMSISDN());
                ChannelUserVO userVO = null;
                if (BTSLUtil.isNullString(p_requestVO.getActiverUserId())) {
                    userVO = channelUserDAO.loadActiveUserId(p_con, filteredMsisdn, "MSISDN");
                    if (userVO != null) {
                        p_requestVO.setActiverUserId(userVO.getUserID());
                    } else {
                        throw new BTSLBaseException(this, "updateUserInfo", PretupsErrorCodesI.NO_USER_EXIST);
                    }
                }
                if ((!BTSLUtil.isNullString(filteredMsisdn)) && userVO == null) {
                    p_requestVO.setRequestMSISDN(filteredMsisdn);
                } else { // String
                    // parentMsisdn=channelUserDAO.loadParentUserMsisdn(p_con,filteredMsisdn,true);
                    final String parentMsisdn = channelUserDAO.loadParentUserMsisdn(p_con, filteredMsisdn, "MSISDN");
                    if (!BTSLUtil.isNullString(parentMsisdn)) {
                        p_requestVO.setRequestMSISDN(parentMsisdn);
                    }
                }
            } else if (!BTSLUtil.isNullString(p_requestVO.getSenderLoginID())) {
                final ChannelUserVO userVO = channelUserDAO.loadActiveUserId(p_con, p_requestVO.getSenderLoginID(), "LOGINID");
                if (userVO != null) {
                    p_requestVO.setActiverUserId(userVO.getUserID());
                } else {
                    throw new BTSLBaseException(this, "updateUserInfo", PretupsErrorCodesI.NO_USER_EXIST);
                }
                // if sender is staff user then load msisdn of parent channel
                // user and set it in requestMsisdn field,
                // otherwise set msisdn of channel user in the field
                // Changed according to new requirement now staff user may have
                // his own MSISDN.
                String parentMsisdn = null;
                if ((!BTSLUtil.isNullString(userVO.getMsisdn())) && (!PretupsI.STAFF_USER_TYPE.equals(userVO.getUserType()))) {
                    p_requestVO.setRequestMSISDN(userVO.getMsisdn());
                } else {
                    // parentMsisdn=channelUserDAO.loadParentUserMsisdn(p_con,p_requestVO.getSenderLoginID(),false);
                    parentMsisdn = channelUserDAO.loadParentUserMsisdn(p_con, p_requestVO.getSenderLoginID(), "LOGINID");
                    p_requestVO.setRequestMSISDN(parentMsisdn);
                }
            } else if (!BTSLUtil.isNullString(p_requestVO.getSenderExternalCode())) {
                final ChannelUserVO userVO = channelUserDAO.loadActiveUserId(p_con, p_requestVO.getSenderExternalCode(), "EXTGWCODE");
                if (userVO != null) {
                    p_requestVO.setActiverUserId(userVO.getUserID());
                } else {
                    throw new BTSLBaseException(this, "updateUserInfo", PretupsErrorCodesI.NO_USER_EXIST);
                }

                // channged according to new requirement as Staff user will have
                // MSISDN
                String parentMsisdn = null;
                if ((!BTSLUtil.isNullString(userVO.getMsisdn())) && (!PretupsI.STAFF_USER_TYPE.equals(userVO.getUserType()))) {
                    p_requestVO.setRequestMSISDN(userVO.getMsisdn());
                } else {
                    parentMsisdn = channelUserDAO.loadParentUserMsisdn(p_con, p_requestVO.getSenderExternalCode(), "EXTGWCODE");
                    p_requestVO.setRequestMSISDN(parentMsisdn);
                }
            }
        } catch (BTSLBaseException be) {
            LOG.error("updateUserInfo", " BTSL Exception while updating parent user info :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.error("updateUserInfo", " Exception while updating parent user info :" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[updateUserInfo]", "", "", "",
                "Exception :" + e.getMessage());
            throw new BTSLBaseException(this, "updateUserInfo", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("updateUserInfo", "Exit");
            }
        }
    }

    public void parseChannelRequestMessage(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
    }

    // For OperatorReceiver to handle operator user as sender.
    abstract public void parseOperatorRequestMessage(RequestVO p_requestVO) throws BTSLBaseException;
    
    
    public static  ChannelUserVO  validateAddUser(Connection con,RequestVO requestVO,ChannelUserVO channelUserVO,ChannelUserVO staffUserVO )throws BTSLBaseException{
        final String methodName = "validateAddUser";
        String DEFAULT_LANGUAGE = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String DEFAULT_COUNTRY = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Boolean IS_DEFAULT_PROFILE = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE);
        String parentMSISDN = (String) requestVO.getRequestMap().get("PARENTMSISDN");
        String childCatCode = (String) requestVO.getRequestMap().get("USERCATCODE");
        // Changed 21-02-2014
        String parentExtCode = (String) requestVO.getRequestMap().get("PARENTEXTERNALCODE");
        // Ended Here
        UserDAO userDao = new UserDAO();
        String parentCatCode = "";
        String pin = (String) requestVO.getRequestMap().get("PIN"); // 03-MAR-2014
        if (!BTSLUtil.isNullString(parentMSISDN) || !BTSLUtil.isNullString(parentExtCode)) {
            parentCatCode = userDao.channelAdminUserCategoryFromMSISDN(con, parentMSISDN, parentExtCode);
            if (BTSLUtil.isNullString(parentCatCode)) {
                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_PARENT_CAT_NOT_ALLOWED);
            }
        }

        requestVO.getMessageGatewayVO().getRequestGatewayVO().setUnderProcessCheckReqd("N");
        String extNwCode = (String) requestVO.getRequestMap().get("EXTNWCODE");
        String senderMSISDN = (String) requestVO.getRequestMap().get("MSISDN");
        String networkID = requestVO.getRequestNetworkCode();
        String password = (String) requestVO.getRequestMap().get("PASSWORD");
        String loginID = (String) requestVO.getRequestMap().get("LOGINID");
        String empCode = (String) requestVO.getRequestMap().get("EMPCODE");
        Locale locale = new Locale("en", "US");
        try {
            locale = new Locale(DEFAULT_LANGUAGE, DEFAULT_COUNTRY);
        } catch (RuntimeException e) {
            LOG.errorTrace(methodName, e);
            locale = new Locale("en", "US");
        }
        if (extNwCode != null || loginID != null || senderMSISDN != null) {
            boolean isTrfRuleExist = false;
            ChannelTransferTxnDAO chnlTrfTxnDAO = new ChannelTransferTxnDAO();
            ChannelTransferRuleVO chnlTrfVO = null;
            ArrayList<ChannelTransferRuleVO> allowedTrfRuleList = chnlTrfTxnDAO
                    .loadC2SRulesListForChannelOperatorUserAssociation(con, extNwCode, parentCatCode);
            int length = allowedTrfRuleList.size();
            if (BTSLUtil.isNullString(parentMSISDN) && BTSLUtil.isNullString(parentExtCode)) {
                parentCatCode = PretupsI.OPERATOR_TYPE_OPT;
            }
            LogFactory.printLog(methodName, "parentCatCode=" + parentCatCode + ", childCatCode=" + childCatCode, LOG);
            for (int i = 0; i < length; i++) {
                chnlTrfVO = allowedTrfRuleList.get(i);
                LogFactory.printLog(CLASSNAME, "FromCategory()=" + chnlTrfVO.getFromCategory() + ", ToCategory="
                            + chnlTrfVO.getToCategory(), LOG);
                String tempParentCatCode;
                if (chnlTrfVO.getFromCategory().trim().equalsIgnoreCase(PretupsI.OPERATOR_TYPE_OPT)) {
                    tempParentCatCode = PretupsI.OPERATOR_TYPE_OPT;
                } else {
                    tempParentCatCode = chnlTrfVO.getFromCategory();
                }
                if (parentCatCode.equals(tempParentCatCode)) {
                    if (childCatCode.trim().equals(chnlTrfVO.getToCategory().trim())) {
                        LogFactory.printLog(methodName, "parentCatCode=" + tempParentCatCode + ", childCatCode="
                                + chnlTrfVO.getToCategory(), LOG);
                        isTrfRuleExist = true;
                        break;
                    }
                }
            }
            if (!isTrfRuleExist) {
                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_REQ_TRF_RULE_NOT_ALLOWED);
            }

            // checked name
            ExtUserDAO extUserDao = new ExtUserDAO();
            if (BTSLUtil.isNullString(parentMSISDN) && BTSLUtil.isNullString(parentExtCode)) {
                channelUserVO = extUserDao.loadChannelUserDetailsByMsisdnOrLoginIdOrEmpCodeWithExtNwCode(con,
                        senderMSISDN, pin, loginID, password, empCode, extNwCode, "", "", locale);
            } else {
                if (!BTSLUtil.isNullString(parentMSISDN)) {
                    channelUserVO = userDao.loadUserDetailsByMsisdn(con, parentMSISDN);
                } else {
                    try{
                    channelUserVO = userDao.loadAllUserDetailsByExternalCode(con, parentExtCode);
                    }catch(SQLException sql){
                        throw new BTSLBaseException(CLASSNAME, methodName,
                                PretupsErrorCodesI.SQL_ERROR_EXCEPTION);
                    }
                }
            }
            if (channelUserVO == null) {
                throw new BTSLBaseException(CLASSNAME, methodName,
                        PretupsErrorCodesI.OPT_ERROR_NO_SUCH_USER);
            }
            /*
             * If IS_DEFAULT_PROFILE is FALSE Then validate input geography to
             * belong under Operator User
             */
            if (!(boolean) IS_DEFAULT_PROFILE) {
                GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
                channelUserVO.setGeographicalAreaList(geographyDAO.loadUserGeographyList(con,
                        channelUserVO.getUserID(), channelUserVO.getNetworkID()));

                String newUserGeoCode = (String) requestVO.getRequestMap().get("GEOGRAPHYCODE");
                if (BTSLUtil.isNullString(newUserGeoCode)) {
                    throw new BTSLBaseException(CLASSNAME, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_GEOGRAPHY);
                }
                requestVO.setNetworkCode(channelUserVO.getNetworkID());
                List<UserGeographiesVO> childGeographyVOlist = geographyDAO.loadParentGeographyInfo(con,
                        newUserGeoCode, requestVO);
                if (childGeographyVOlist.isEmpty()) {
                    throw new BTSLBaseException(CLASSNAME, "process",
                            PretupsErrorCodesI.EXTSYS_REQ_USR_GEOGRAPHY_NOT_BELONG_TO_PARENT);
                }
                int count = channelUserVO.getGeographicalAreaList().size();
                boolean graphbelongstoparent = false;

                for (int i = 0; i < count; i++) {
                    UserGeographiesVO userGeogVO =  channelUserVO.getGeographicalAreaList().get(i);

                    if (userGeogVO.getGraphDomainCode().equals(childGeographyVOlist.get(0).getParentGraphDomainCode())
                            || userGeogVO.getGraphDomainCode().equals(childGeographyVOlist.get(0).getGraphDomainCode())) {
                        channelUserVO.setgeographicalCodeforNewuser(childGeographyVOlist.get(0).getGraphDomainCode());
                        graphbelongstoparent = true;
                    }

                }
                if (!graphbelongstoparent) {
                    throw new BTSLBaseException(CLASSNAME, methodName,
                            PretupsErrorCodesI.EXTSYS_REQ_USR_GEOGRAPHY_NOT_BELONG_TO_PARENT);
                }
            }
            // load domains

            DomainDAO domainDAO = new DomainDAO();
            channelUserVO.setDomainList(domainDAO.loadDomainListByUserId(con, channelUserVO.getUserID()));
            UserPhoneVO phoneVO = new UserPhoneVO();
            phoneVO.setPhoneLanguage(DEFAULT_LANGUAGE);
            phoneVO.setCountry(DEFAULT_COUNTRY);
            phoneVO.setMsisdn(channelUserVO.getMsisdn());
            phoneVO.setUserPhonesId(channelUserVO.getUserPhonesId());
            channelUserVO.setUserPhoneVO(phoneVO);

            // end
        } else {
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERP_USER_NONE_PARAMETER);
        }

        validateChannelUserVO(channelUserVO, senderMSISDN, staffUserVO, networkID, requestVO, loginID);
        return channelUserVO;
    }
    
    public static void validateChannelUserVO(ChannelUserVO channelUserVO, String senderMSISDN, ChannelUserVO staffUserVO, String networkID, RequestVO p_requestVO, String loginID) throws BTSLBaseException {
        final String METHOD_NAME="validateChannelUserVO";
        if (channelUserVO != null) {
            if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                throw new BTSLBaseException(CLASSNAME, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
            }
            if (!("OPERATOR".equals(channelUserVO.getUserType()))) {

                if (!BTSLUtil.isNullString(senderMSISDN) && !(staffUserVO == null)) {
                    if (!channelUserVO.getUserID().equals(p_requestVO.getActiverUserId())) {
                        if (!PretupsBL.getFilteredMSISDN(staffUserVO.getMsisdn()).equalsIgnoreCase(senderMSISDN)) {
                            throw new BTSLBaseException(CLASSNAME, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_INVALID_SENDER_MSISDN);
                        }
                    } else {
                        if (!p_requestVO.getFilteredMSISDN().equalsIgnoreCase(senderMSISDN)) {
                            throw new BTSLBaseException(CLASSNAME, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_INVALID_SENDER_MSISDN);
                        }
                    }
                }
                // Changes END By Babu Kunwar
                    if (!BTSLUtil.isNullString(loginID) && !loginID.equalsIgnoreCase(p_requestVO.getSenderLoginID())) {
                        throw new BTSLBaseException(CLASSNAME, METHOD_NAME, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                    }
                if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
                    throw new BTSLBaseException(CLASSNAME, METHOD_NAME, PretupsErrorCodesI.XML_ERROR_SENDER_SUSPEND);
                } else if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_DELETE_REQUEST)) {
                    throw new BTSLBaseException(CLASSNAME, METHOD_NAME, PretupsErrorCodesI.XML_ERROR_SENDER_DELETE_REQUEST);
                } else if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
                    throw new BTSLBaseException(CLASSNAME, METHOD_NAME, PretupsErrorCodesI.XML_ERROR_SENDER_SUSPEND_REQUEST);
                }
            }
        } else {
            throw new BTSLBaseException(CLASSNAME, METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
        }

    }

    private void settingStaffDetails(ChannelUserVO channelUserVO) {

		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			channelUserVO.setActiveUserID(channelUserVO.getUserID());
			UserDAO userDao = new UserDAO();
            UserPhoneVO phoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
            if (phoneVO != null) {
                channelUserVO.setActiveUserMsisdn(phoneVO.getMsisdn());
                channelUserVO.setActiveUserPin(phoneVO.getSmsPin());
               }
            ChannelUserVO staffUserVO = new ChannelUserVO();
            UserPhoneVO staffphoneVO = new UserPhoneVO();
            BeanUtils.copyProperties(staffUserVO, channelUserVO);
            if (phoneVO != null) {
                BeanUtils.copyProperties(staffphoneVO, phoneVO);
                staffUserVO.setUserPhoneVO(staffphoneVO);
            }
            staffUserVO.setPinReset(channelUserVO.getPinReset());
            channelUserVO.setStaffUserDetails(staffUserVO);
            ChannelUserVO parentChannelUserVO = new UserDAO().loadUserDetailsFormUserID(con, channelUserVO.getParentID());
            staffUserDetails(channelUserVO, parentChannelUserVO);
            channelUserVO.setPrefixId(parentChannelUserVO.getPrefixId());
				
		}catch(Exception e) {
			
		}finally {
			if(mcomCon != null)
			{
				mcomCon.close("C2CTransferController#checkAndSetStaffVO");
				mcomCon=null;
			}
		}
		
	}
	
	protected void staffUserDetails(ChannelUserVO channelUserVO, ChannelUserVO parentChannelUserVO) {
        channelUserVO.setUserID(channelUserVO.getParentID());
        channelUserVO.setParentID(parentChannelUserVO.getParentID());
        channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
        channelUserVO.setStatus(parentChannelUserVO.getStatus());
        channelUserVO.setUserType(parentChannelUserVO.getUserType());
        channelUserVO.setStaffUser(true);
        channelUserVO.setMsisdn(parentChannelUserVO.getMsisdn());
        channelUserVO.setPinRequired(parentChannelUserVO.getPinRequired());
        channelUserVO.setSmsPin(parentChannelUserVO.getSmsPin());
        channelUserVO.setParentLoginID(parentChannelUserVO.getLoginID());
    }
    
}
