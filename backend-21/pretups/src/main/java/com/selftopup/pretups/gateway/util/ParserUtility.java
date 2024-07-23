package com.selftopup.pretups.gateway.util;

import java.util.Locale;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.master.businesslogic.LocaleMasterVO;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.util.BTSLUtil;

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

    public static Log _log = LogFactory.getLog(ParserUtility.class.getName());
    public final static int ACTION_ACCOUNT_INFO = 0;
    public final static int CREDIT_TRANSFER = 1;
    public final static int CHANGE_PIN = 2;
    public final static int NOTIFICATION_LANGUAGE = 3;
    public final static int HISTORY_MESSAGE = 4;
    public final static int CREDIT_RECHARGE = 5;
    public final static int SUBSCRIBER_REGISTRATION = 6;
    public final static int SUBSCRIBER_DEREGISTRATION = 7;
    public final static int P2P_SERVICE_SUSPEND = 8;// P2P service Suspend
    public final static int P2P_SERVICE_RESUME = 9;// P2P service Resume
    public final static int ADD_BUDDY = 10;
    public final static int DELETE_BUDDY = 11;
    public final static int LIST_BUDDY = 12;
    public final static int LAST_TRANSFER_STATUS = 13;
    // added for Last Transfer Status(CP2P) 03/05/07
    public final static int SELF_BAR = 14;

    // added for Delete Subscriber List by harsh on 09Aug12
    public final static int DELETE_MULTLIST = 15;

    public final static int ACTION_CHNL_ACCOUNT_INFO = 0;
    public final static int ACTION_CHNL_CREDIT_TRANSFER = 1;
    public final static int ACTION_CHNL_CHANGE_PIN = 2;
    public final static int ACTION_CHNL_NOTIFICATION_LANGUAGE = 3;
    public final static int ACTION_CHNL_HISTORY_MESSAGE = 4;
    public final static int ACTION_CHNL_TRANSFER_MESSAGE = 5;
    public final static int ACTION_CHNL_RETURN_MESSAGE = 6;
    public final static int ACTION_CHNL_WITHDRAW_MESSAGE = 7;
    public final static int ACTION_CHNL_POSTPAID_BILLPAYMENT = 8;
    public final static int ACTION_CHNL_O2C_INITIATE = 9;
    public final static int ACTION_CHNL_O2C_INITIATE_TRFR = 10;
    public final static int ACTION_CHNL_O2C_RETURN = 11;
    public final static int ACTION_CHNL_O2C_WITHDRAW = 12;
    public final static int ACTION_CHNL_EXT_RECH_STATUS = 13;
    public final static int ACTION_CHNL_EXT_CREDIT_TRANSFER = 14;
    public final static int ACTION_CHNL_BALANCE_ENQUIRY = 16; // added for
                                                              // Balance Enquiry
                                                              // 03/05/07
    public final static int ACTION_CHNL_DAILY_STATUS_REPORT = 17; // added for
                                                                  // Daily
                                                                  // Status
                                                                  // Report
                                                                  // 03/05/07
    public final static int ACTION_CHNL_LAST_TRANSFER_STATUS = 18; // added for
                                                                   // Last
                                                                   // Transfer
                                                                   // Status(RP2P)
                                                                   // 03/05/07
    public final static int ACTION_CHNL_EVD_REQUEST = 19;
    public final static int ACTION_MULTIPLE_VOUCHER_DISTRIBUTION = 20;// Multiple
                                                                      // Voucher
                                                                      // Distribution
    public final static int ACTION_UTILITY_BILL_PAYMENT = 21;// Utility Bill
                                                             // Payment
    public final static int ACTION_CHNL_EXT_TRANSFER_BILL_PAYMENT = 22;// for
                                                                       // C2S
                                                                       // Bill
                                                                       // payment
    public final static int ACTION_CHNL_EXT_ENQUIRY_REQUEST = 23; // for c2s
                                                                  // enquiry
    public final static int ACTION_CHNL_EXT_POST_RECHARGE_STATUS = 24; // for
                                                                       // post
                                                                       // recharge
                                                                       // status
    public final static int ACTION_CHNL_EXT_COMMON_RECHARGE = 25; // for common
                                                                  // recharge
                                                                  // request
    public final static int ACTION_CHNL_GIFT_RECHARGE_XML = 26; // for Gift
                                                                // Recharge
                                                                // through XML
                                                                // API 23/04/08
    public final static int ACTION_CHNL_GIFT_RECHARGE_USSD = 27; // for Gift
                                                                 // Recharge
                                                                 // through USSD
                                                                 // 23/04/08
    public final static int ACTION_CHNL_BAL_ENQ_XML = 28;
    public final static int ACTION_CHNL_EVD_XML = 29;
    public final static int ACTION_C2C_TRANSFER_EXT_XML = 30;
    public final static int ACTION_C2C_RETURN_EXT_XML = 31;
    public final static int ACTION_C2C_WITHDRAW_EXT_XML = 32;
    public final static int ACTION_EXT_C2SCHANGEPIN_XML = 33;// for C2S Change
                                                             // Pin through XML
                                                             // API
    public final static int ACTION_CHNL_CREDIT_TRANSFER_CDMA = 34;// Added for
                                                                  // CDMA
                                                                  // Recharge
    public final static int ACTION_CHNL_CREDIT_TRANSFER_PSTN = 35;// Added for
                                                                  // PSTN
                                                                  // Recharge
    public final static int ACTION_CHNL_CREDIT_TRANSFER_INTR = 36;// Added for
                                                                  // INTERNET
                                                                  // Recharge
    public final static int ACTION_CHNL_EXT_CREDIT_TRANSFER_CDMA = 37;// Added
                                                                      // for
                                                                      // CDMA
                                                                      // Bank
                                                                      // Recharge
    public final static int ACTION_CHNL_EXT_CREDIT_TRANSFER_PSTN = 38;// Added
                                                                      // for
                                                                      // PSTN
                                                                      // Bank
                                                                      // Recharge
    public final static int ACTION_CHNL_EXT_CREDIT_TRANSFER_INTR = 39;// Added
                                                                      // for
                                                                      // INTR
                                                                      // Bank
                                                                      // Recharge
    public final static int ACTION_CHNL_ORDER_CREDIT = 40;// Added for
                                                          // ORDER_CREDIT
    public final static int ACTION_CHNL_ORDER_LINE = 41;// Added for ORDER_LINE
    public final static int ACTION_CHNL_BARRING = 42;// Added for BARRING

    public final static int ACTION_CHNL_EXT_VAS_SELLING = 43; // for vas selling
                                                              // CRBT
    public final static int ACTION_CHNL_IAT_ROAM_RECHARGE = 44; // for IAT roam
                                                                // recharge
    public final static int ACTION_CHNL_IAT_INTERNATIONAL_RECHARGE = 45; // for
                                                                         // IAT
                                                                         // international
                                                                         // recharge

    public final static int ACTION_CHNL_C2S_LAST_XTRANSFER = 46; // For last 3
                                                                 // c2s transfer
                                                                 // report
    public final static int ACTION_CHNL_CUST_LAST_XTRANSFER = 47; // For last 3
                                                                  // C2S/C2C/BOTH
                                                                  // transfer
                                                                  // status
    public final static int ACTION_EXT_LAST_XTRF_ENQ = 48;
    public final static int ACTION_EXT_CUSTOMER_ENQ_REQ = 49;
    public final static int ACTION_EXT_OTHER_BAL_ENQ = 50;
    public final static int ACTION_EXT_LAST_TRF = 51;
    public final static int ACTION_EXT_DAILY_STATUS_REPORT = 52;
    public final static int ACTION_EXT_MVD_DWNLD_RQST = 53; // Changes done by
                                                            // ashishT for MVD
                                                            // voucher download.
    public final static int ACTION_C2S_TRANS_ENQ = 54;
    public final static int ACTION_REGISTER_SID = 55;
    public final static int SUSPEND_RESUME_CUSR = 56; // Suspend or resume
                                                      // channel user.
    public final static int ACTION_EXT_USER_CREATION = 57;
    public final static int ACTION_ENQUIRY_TXNIDEXTCODEDATE = 58;
    public final static int ACTION_DELETE_SID_REQ = 59;// added by ankuj for SID
                                                       // deletion
    public final static int ACTION_ENQUIRY_SID_REQ = 60;// added by ankuj for
                                                        // SID enquiry
    public final static int ACTION_CRBT_REGISTRATION = 61;// added for CRBT
                                                          // Registration
    public final static int ACTION_CRBT_SONG_SELECTION = 62;// added for CRBT
                                                            // Song Selection
    public final static int ACTION_P2P_CRIT_TRANS = 63; // added for Multiple
                                                        // credit recharge
    public final static int ACTION_ELECTRONIC_VOUCHER_RECHARGE = 64; // Changes
                                                                     // done by
                                                                     // Harpreet
                                                                     // for EVR
    public final static int ACTION_CHNL_EVR_XML = 65;// added for EVR through
                                                     // XML API
    public final static int ACTION_EXT_PRIVATERC_XML = 66;// added for Private
                                                          // Recharge through
                                                          // External gateway
    public final static int ACTION_EXT_DRCR_C2C_CUSER = 67;// added for DrCr
                                                           // Transfer through
                                                           // External Gateway
    public final static int P2P_GIVE_ME_BALANCE = 68;// Added for GiveMeBalance
    public final static int ACTION_SUSPEND_RESUME_CUSR_EXTGW = 69;// ADDED FOR
                                                                  // SUSPEND
                                                                  // RESUME
                                                                  // CHANNEL
                                                                  // USER
                                                                  // THROUGH
                                                                  // EXTERNAL
                                                                  // GATEWAY
    public final static int P2P_LEND_ME_BALANCE = 70;// Added for LendMeBalance
    public final static int ACTION_MULT_CDT_TXR_LIST_AMD = 71; // Added for
                                                               // Adding,Modifying,deleting
                                                               // Multiple
                                                               // Credit
                                                               // Transfer List
    public final static int ACTION_MULT_CDT_TXR_LIST_VIEW = 72; // Added for
                                                                // view list for
                                                                // Multiple
                                                                // Credit
    public final static int ACTION_MULT_CDT_TXR_LIST_REQUEST = 73; // Added for
                                                                   // credit to
                                                                   // MCDL
    public final static int LMB_ONLINE_DEBIT = 74; // Added for LMB debit online
                                                   // API
    public final static int ACTION_C2S_RPT_LAST_XTRANSFER = 75;// rahul for
                                                               // korek
    // added for VAS and promoVAS
    public final static int ACTION_VAS_RC_REQUEST = 76; // VASTRIX Added by
                                                        // hitesh
    public final static int ACTION_EXTVAS_RC_REQUEST = 77; // VASTRIX Added by
                                                           // hitesh
    public final static int ACTION_PVAS_RC_REQUEST = 78; // VASTRIX Added by
                                                         // hitesh
    public final static int ACTION_EXTPVAS_RC_REQUEST = 79; // VASTRIX Added by
                                                            // hitesh
    // added for DMS
    public final static int ACTION_EXT_USERADD_REQUEST = 80;// for External User
                                                            // Addition
    public final static int ACTION_EXT_GEOGRAPHY_REQUEST = 81;// For DMS
                                                              // Configuration.7/Nov/2012
    public final static int ACTION_EXT_TRF_RULE_TYPE_REQ = 82;// for DMS
                                                              // Configuration.

    public final static int ACTION_CRM_USER_AUTH_XML = 83; // added by shashank
                                                           // for channel user
                                                           // authentication.17/Jan/2013
    // sim activate
    public final static int ACTION_USSD_SIM_ACT_REQ = 84;
    // added by Sonali Garg to enquire for a subscriber at IN
    public final static int ACTION_EXT_SUBENQ = 85;

    // added by harsh for Scheduled Credit Transfer (Add/Modify/Delete API)
    public final static int ACTION_MULT_CDT_TXR_SCDLIST_AMD = 86;
    // added by Vikas Kumar for Scheduled Credit Transfer Service
    public final static int SCHEDULE_CREDIT_TRANSFER = 87;
    // added by Pradyumn Mishra for Scheduled Credit Transfer (View/Delete
    // Complete Subscriber List)
    public final static int ACTION_MULT_CDT_TXR_SCDLIST_DLT = 88;
    public final static int ACTION_MULT_CDT_TXR_SCDLIST_VEW = 89;
    public final static int ACTION_CHNL_HLPDESK_REQUEST = 90; // added by
                                                              // arvinder for
                                                              // HelpDesk
                                                              // Request//
    public final static int ACTION_EXT_HLPDESK_REQUEST = 91;// added by arvinder
                                                            // for HelpDesk
                                                            // Request External
                                                            // Gateway//
    // added by akanksha for peru claro update
    public final static int ACTION_O2C_SAP_ENQUIRY = 92;
    public final static int ACTION_O2C_SAP_EXTCODE_UPDATE = 93;
    public final static int ACTION_COL_ENQ = 94;
    public final static int ACTION_COL_BILLPAYMENT = 95;

    public final static int ACTION_DTH = 96;
    public final static int ACTION_DC = 97;
    public final static int ACTION_PMD = 98;
    public final static int ACTION_PIN = 99;
    public final static int ACTION_BPB = 100;
    public final static int ACTION_FLRC = 101;
    public final static int ACTION_C2S_POSTPAID_REVERSAL = 102;
    // PPBENQ :rahul.d
    public final static int ACTION_EXT_PPBENQ = 103;
    public final static int ACTION_EXT_EVD_RC_POS = 104;

    // Added By Diwakar for ROBI
    // Request Type
    public final static String ADD_USER_REQ = "USERADDREQ";
    public final static String MODIFY_USER_REQ = "USERMODREQ";
    public final static String DELETE_USER_REQ = "USERDELREQ";
    public final static String SUSPEND_RESUME_USER_REQ = "USERSRREQ";
    public final static String CHANE_PASSWORD_REQ = "EXTCNGPWDREQ";
    public final static String ADD_DELETE_USER_ROLE_REQ = "EXTCNGROLEREQ";
    public final static String MNP_REQ = "UPLOADMNPFILEREQ";
    public final static String ICCID_MSISDN_MAP_REQ = "ICCIDMSISDNMAPREQ";

    // Request Action for request Type
    public final static int ADD_USER_ACTION = 105;
    public final static int MODIFY_USER_ACTION = 106;
    public final static int DELETE_USER_ACTION = 107;
    public final static int SUSPEND_RESUME_USER_ACTION = 108;
    public final static int ADD_DELETE_USER_ROLE_ACTION = 109;
    public final static int CHANGE_PASSWORD_ACTION = 110;
    public final static int MNP_ACTION = 111;
    public final static int ICCID_MSISDN_MAP_ACTION = 112;
    // Ended By Diwakar for ROBI

    // /Added by sonali for self topup user registration
    public final static int ACTION_SELF_TOPUP_USER_REGISTRATION = 113;
    public final static int ACTION_SELF_TOPUP_CHANGE_PIN = 114;
    public final static int ACTION_SELF_TOPUP_CARD_REGISTRATION = 117;

    // Added by Vikas Singh for CARD modify, delete & view

    // Request Keywords
    public final static String CARD_MODIFY_REQ = "STPMCREQ";
    public final static String CARD_DELETE_REQ = "STPDCREQ";
    public final static String CARD_VIEW_REQ = "STPVCREQ";
    // Request Action for request type for CARD: modify, delete & view
    public final static int ACTION_CARD_MODIFY = 115;
    public final static int ACTION_CARD_DELETE = 116;
    public final static int ACTION_SELF_TOPUP_RECHARGE_USING_REG_CARD = 117;
    public static OperatorUtilI _operatorUtil = null;
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ParserUtility[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * Performs Validation of MSISDN
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void validateMSISDN(RequestVO p_requestVO) throws BTSLBaseException {
        String filteredMSISDN = _operatorUtil.getSystemFilteredMSISDN(p_requestVO.getRequestMSISDN());
        p_requestVO.setFilteredMSISDN(filteredMSISDN);
        p_requestVO.setMessageSentMsisdn(filteredMSISDN);
        if (!BTSLUtil.isValidMSISDN(filteredMSISDN)) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ParserUtility[validateMSISDN]", p_requestVO.getRequestIDStr(), filteredMSISDN, "", "Sender MSISDN Not valid");
            p_requestVO.setSenderMessageRequired(false);
            throw new BTSLBaseException(this, "validateMSISDN", SelfTopUpErrorCodesI.C2S_ERROR_INVALID_SENDER_MSISDN);
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
        String requestStr = p_requestVO.getRequestMessage();
        if (_log.isDebugEnabled())
            _log.debug("requestParser", "Entered p_requestVO " + p_requestVO.toString() + " requestStr: " + requestStr);
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

            String contentType = p_requestVO.getReqContentType();
            if (contentType != null && (contentType.indexOf("plain") != -1 || contentType.indexOf("PLAIN") != -1)) {
                type = p_requestVO.getServiceKeyword();
            } else if (contentType != null && (p_requestVO.getReqContentType().indexOf("xml") != -1 || p_requestVO.getReqContentType().indexOf("XML") != -1)) {
                if (!(requestStr.indexOf("<?xml version=\"1.0\"?>") != -1)) {
                    throw new BTSLBaseException(SelfTopUpErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
                }
                /*
                 * As it is not used by O2C XML API. and PreTUPS do not validate
                 * DTD . so it is not required
                 * if(!(requestStr.indexOf("xml/command.dtd")!=-1) )
                 * {
                 * throw new BTSLBaseException(PretupsErrorCodesI.
                 * P2P_ERROR_INVALIDMESSAGEFORMAT);
                 * }
                 */if (!(requestStr.indexOf("<COMMAND>") != -1)) {
                    throw new BTSLBaseException(SelfTopUpErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
                }
                if (!(requestStr.indexOf("</COMMAND>") != -1)) {
                    throw new BTSLBaseException(SelfTopUpErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
                }
                int index = requestStr.indexOf("<TYPE>");
                type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
            }

            if (type.equals("CACINFREQ")) {
                action = 0;
            } else if (type.equals("CCTRFREQ")) {
                action = 1;
            }
            if (type.equals("CCPNREQ")) {
                action = 2;
            } else if (type.equals("CCLANGREQ")) {
                action = 3;
            } else if (type.equals("CCHISREQ")) {
                action = 4;
            } else if (type.equals("CCRCREQ")) {
                action = CREDIT_RECHARGE;
            } else if (type.equals("REGREQ")) {
                action = SUBSCRIBER_REGISTRATION;
            } else if (type.equals("DREGREQ")) {
                action = SUBSCRIBER_DEREGISTRATION;
            } else if (type.equals("SUSREQ")) {
                action = P2P_SERVICE_SUSPEND;
            } else if (type.equals("RESREQ")) {
                action = P2P_SERVICE_RESUME;
            } else if (type.equals("PLTREQ"))// added for Last Transfer
                                             // Status(P2P) 03/05/07
            {
                action = LAST_TRANSFER_STATUS;
            } else if (type.equals("ADDBUDDYREQ")) {
                action = ADD_BUDDY;
            } else if (type.equals("DELBUDDYREQ")) {
                action = DELETE_BUDDY;
            }
            // added by harsh 09Aug12
            else if (type.equals("SCLDREQ")) {
                action = DELETE_MULTLIST;
            } else if (type.equals("LSTBUDDYREQ")) {
                action = LIST_BUDDY;
            } else if (type.equals("BARREQ")) {
                action = SELF_BAR;
            } else if (type.equals("RCETRANREQ")) {
                action = ACTION_C2S_TRANS_ENQ;
            } else if (type.equals("SIDREQ")) {
                action = ACTION_REGISTER_SID;
            } else if (type.equals("DELSID")) {
                action = ACTION_DELETE_SID_REQ;
            } else if (type.equals("ENQSID")) {
                action = ACTION_ENQUIRY_SID_REQ;
            } else if (type.equals("CCMULTRFREQ")) {
                action = ACTION_P2P_CRIT_TRANS;
            } else if (type.equals("CGMBALREQ")) {
                action = P2P_GIVE_ME_BALANCE;
            } else if (type.equals("LMBREQ")) {
                action = P2P_LEND_ME_BALANCE;
            } else if (type.equals("SCLAMREQ")) {
                action = ACTION_MULT_CDT_TXR_LIST_AMD;
            } else if (type.equals("SCLVREQ")) {
                action = ACTION_MULT_CDT_TXR_LIST_VIEW;
            } else if (type.equals("SCLTRFREQ")) {
                action = ACTION_MULT_CDT_TXR_LIST_REQUEST;
            } else if (type.equals("LMBDBTREQ")) {
                action = LMB_ONLINE_DEBIT;
            }
            // added by harsh for Scheduled Credit List (Add/Modify/Delete) API
            // on 22 Apr 13
            else if (type.equals("PSCTAMREQ")) {
                action = ACTION_MULT_CDT_TXR_SCDLIST_AMD;
            } else if (type.equals("SHCCTRFREQ")) {
                action = SCHEDULE_CREDIT_TRANSFER;
            }
            // added by pradyumn for scheduled list view
            else if (type.equals("PSCTDREQ")) {
                action = ACTION_MULT_CDT_TXR_SCDLIST_DLT;
            }
            // added by pradyumn for scheduled credit list view
            else if (type.equals("PSCTVREQ")) {
                action = ACTION_MULT_CDT_TXR_SCDLIST_VEW;
            }
            // Added by sonali for self topup user registration
            else if (type.equals("STPREGREQ")) {
                action = ACTION_SELF_TOPUP_USER_REGISTRATION;
            }
            // Added by sonali for self topup change pin request
            else if (type.equals("STPCPNREQ")) {
                action = ACTION_SELF_TOPUP_CHANGE_PIN;
            }

            // added by gaurav for card registration
            else if (type.equals("STPACREQ")) {
                action = ACTION_SELF_TOPUP_CARD_REGISTRATION;
            }

            // changes ends here
            if (action == -1)
                throw new BTSLBaseException(SelfTopUpErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            p_requestVO.setActionValue(action);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("actionParser", "Exception e: " + e);
            e.printStackTrace();
            throw new BTSLBaseException(SelfTopUpErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("actionParser", "exit action:" + action);
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
        String requestStr = p_requestVO.getRequestMessage();
        if (_log.isDebugEnabled())
            _log.debug("actionChannelParser", "Entered p_requestVO=" + p_requestVO.toString() + " requestStr: " + requestStr);
        int action = -1;
        String type = null;
        try {
            String contentType = p_requestVO.getReqContentType();
            if (contentType != null && (contentType.indexOf("plain") != -1 || contentType.indexOf("PLAIN") != -1)) {
                type = p_requestVO.getServiceKeyword();
                // int index=requestStr.indexOf("TYPE=");
                // type=requestStr.substring(index+"TYPE=".length(),requestStr.indexOf("&",index));
            } else if (contentType != null && (p_requestVO.getReqContentType().indexOf("xml") != -1 || p_requestVO.getReqContentType().indexOf("XML") != -1)) {
                if (!(requestStr.indexOf("<?xml version=\"1.0\"?>") != -1)) {
                    throw new BTSLBaseException(SelfTopUpErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
                /*
                 * if(!(requestStr.indexOf("xml/command.dtd")!=-1) )
                 * {
                 * throw new BTSLBaseException(PretupsErrorCodesI.
                 * C2S_ERROR_INVALIDMESSAGEFORMAT);
                 * }
                 */
                if (!(requestStr.indexOf("<COMMAND>") != -1)) {
                    throw new BTSLBaseException(SelfTopUpErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
                if (!(requestStr.indexOf("</COMMAND>") != -1)) {
                    throw new BTSLBaseException(SelfTopUpErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                }
                int index = requestStr.indexOf("<TYPE>");
                type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
                _log.debug("actionChannelParser", "TYPE Coming" + " " + type);

            }

            if (type.equals("CACINFREQ")) {
                action = 0;
            } else if (type.equals("RCTRFREQ")) {
                action = 1;
            } else if (type.equals("RCPNREQ")) {
                action = 2;
            } else if (type.equals("RCNLANGREQ")) {
                action = 3;
            } else if (type.equals("CCHISREQ")) {
                action = 4;
            } else if (type.equals("TRFREQ")) {
                action = 5;
            } else if (type.equals("RETREQ")) {
                action = 6;
            } else if (type.equals("WDTHREQ")) {
                action = 7;
            } else if (type.equals("PPBTRFREQ")) {
                action = 8;
            } else if (type.equals("O2CINREQ")) {
                action = ACTION_CHNL_O2C_INITIATE;
            } else if (type.equals("O2CINTREQ")) {
                action = ACTION_CHNL_O2C_INITIATE_TRFR;
            } else if (type.equals("O2CRETREQ")) {
                action = ACTION_CHNL_O2C_RETURN;
            } else if (type.equals("O2CWDREQ")) {
                action = ACTION_CHNL_O2C_WITHDRAW;
            } else if (type.equals("EXRCSTATREQ")) {
                action = ACTION_CHNL_EXT_RECH_STATUS;
            } else if (type.equals("EXRCTRFREQ")) {
                action = ACTION_CHNL_EXT_CREDIT_TRANSFER;
            } else if (type.equals("BALREQ")) // added for Balance Enquiry
                                              // 03/05/07
            {
                action = ACTION_CHNL_BALANCE_ENQUIRY;
            } else if (type.equals("DSRREQ")) // added for Daily Status Report
                                              // 03/05/07
            {
                action = ACTION_CHNL_DAILY_STATUS_REPORT;
            } else if (type.equals("LTSREQ")) // added for Last Transfer
                                              // Status(RP2P) 03/05/07
            {
                action = ACTION_CHNL_LAST_TRANSFER_STATUS;
            } else if (type.equals("EVDREQ")) {
                action = ACTION_CHNL_EVD_REQUEST;
            } else if (type.equals("MVDREQ")) {
                action = ACTION_MULTIPLE_VOUCHER_DISTRIBUTION;
            } else if (type.equals("UBPREQ")) {
                action = ACTION_UTILITY_BILL_PAYMENT;
            } else if (type.equals("EXPPBREQ"))// added for C2S Bill payment
            {
                action = ACTION_CHNL_EXT_TRANSFER_BILL_PAYMENT;
            } else if (type.equals("EXTSYSENQREQ"))// added for c2s Enquiry
            {
                action = ACTION_CHNL_EXT_ENQUIRY_REQUEST;
            } else if (type.equals("EXPPBSTATREQ"))// added for c2s Enquiry
            {
                action = ACTION_CHNL_EXT_POST_RECHARGE_STATUS;
            } else if (type.equals("CEXRCTRFREQ")) // added for common recharge
            {
                action = ACTION_CHNL_EXT_COMMON_RECHARGE;
            } else if (type.equals("GFTRCREQ"))// added for Gift Recharge
                                               // through USSD
            {
                action = ACTION_CHNL_GIFT_RECHARGE_USSD;
            } else if (type.equals("EXGFTRCREQ"))// added for Gift Recharge
                                                 // through XML API
            {
                action = ACTION_CHNL_GIFT_RECHARGE_XML;
            } else if (type.equals("EXUSRBALREQ"))// added for Gift Recharge
                                                  // through XML API
            {
                action = ACTION_CHNL_BAL_ENQ_XML;
            } else if (type.equals("EXEVDREQ"))// added for Gift Recharge
                                               // through XML API
            {
                action = ACTION_CHNL_EVD_XML;
            } else if (type.equals("EXC2CTRFREQ"))// C2C transfer through
                                                  // external getway (XML API)
            {
                action = ACTION_C2C_TRANSFER_EXT_XML;
            } else if (type.equals("EXC2CWDREQ"))// C2C withdraw through
                                                 // external getway (XML API)
            {
                action = ACTION_C2C_WITHDRAW_EXT_XML;
            } else if (type.equals("EXC2CRETREQ"))// C2C Return through external
                                                  // getway (XML API)
            {
                action = ACTION_C2C_RETURN_EXT_XML;
            } else if (type.equals("EXC2SCPNREQ"))// Change Pin through external
                                                  // getway (XML API)
            {
                action = ACTION_EXT_C2SCHANGEPIN_XML;
            } else if (type.equals("CDMARCTRFREQ"))// Added for CDMA recharge
            {
                action = ACTION_CHNL_CREDIT_TRANSFER_CDMA;
            } else if (type.equals("PSTNRCTRFREQ"))// Added for PSTN recharge
            {
                action = ACTION_CHNL_CREDIT_TRANSFER_PSTN;
            } else if (type.equals("INTRRCTRFREQ"))// Added for INTERNET
                                                   // recharge
            {
                action = ACTION_CHNL_CREDIT_TRANSFER_INTR;
            } else if (type.equals("EXCDMARCREQ"))// Added for CDMA Bank
                                                  // Recharge
            {
                action = ACTION_CHNL_EXT_CREDIT_TRANSFER_CDMA;
            } else if (type.equals("EXPSTNRCREQ"))// Added for PSTN Bank
                                                  // Recharge
            {
                action = ACTION_CHNL_EXT_CREDIT_TRANSFER_PSTN;
            } else if (type.equals("EXINTRRCREQ"))// Added for INTR Bank
                                                  // Recharge
            {
                action = ACTION_CHNL_EXT_CREDIT_TRANSFER_INTR;
            } else if (type.equals("ORDLREQ"))// Added for Order Line
            {
                action = ACTION_CHNL_ORDER_LINE;
            } else if (type.equals("ORDCREQ"))// Added for Order Credit
            {
                action = ACTION_CHNL_ORDER_CREDIT;
            } else if (type.equals("BARREQ"))// Added for Barring
            {
                action = ACTION_CHNL_BARRING;
            } else if (type.equals("VASSELLREQ"))// Roam Recharge through
                                                 // external getway (XML API)
            {
                action = ACTION_CHNL_EXT_VAS_SELLING;
            } else if (type.equals("ROAMRCREQ"))// Roam Recharge through
                                                // external getway (XML API)
            {
                action = ACTION_CHNL_IAT_ROAM_RECHARGE;
            } else if (type.equals("INTLRCREQ"))// Roam Recharge through
                                                // external getway (XML API)
            {
                action = ACTION_CHNL_IAT_INTERNATIONAL_RECHARGE;
            } else if (type.equals("LXTSREQ") || (type.equals("L3TSREQ")))// By
                                                                          // vikram
                                                                          // for
                                                                          // last
                                                                          // x
                                                                          // transfers
                                                                          // c2c/c2s/both//korek
            {
                action = ACTION_CHNL_C2S_LAST_XTRANSFER;
            } else if (type.equals("CUSTREQ"))// By vikram for Customer enquiry
            {
                action = ACTION_CHNL_CUST_LAST_XTRANSFER;
            } else if (type.equals("EXLST3TRFREQ")) {
                action = ACTION_EXT_LAST_XTRF_ENQ;
            } else if (type.equals("EXCUSTREQ")) {
                action = ACTION_EXT_CUSTOMER_ENQ_REQ;
            }

            else if (type.equals("EXOTHUSRBALREQ")) {
                action = ACTION_EXT_OTHER_BAL_ENQ;
            } else if (type.equals("EXLSTTRFREQ")) {
                action = ACTION_EXT_LAST_TRF;
            } else if (type.equals("EXDLYREQ")) {
                action = ACTION_EXT_DAILY_STATUS_REPORT;
            } else if (type.equals("EXTMVDREQ")) // changes done by ashishT for
                                                 // MVD USSD voucher download.
            {
                action = ACTION_EXT_MVD_DWNLD_RQST;
            } else if (type.equals("RCETRANREQ")) // changes done by ashishT for
                                                  // MVD USSD voucher download.
            {
                action = ACTION_C2S_TRANS_ENQ;
            }

            else if (type.equals("SIDREQ")) {
                action = ACTION_REGISTER_SID;
            }

            else if (type.equals("ETXNENQREQ")) {
                action = ACTION_ENQUIRY_TXNIDEXTCODEDATE;
            } else if (type.equals("DELSID")) {
                action = ACTION_DELETE_SID_REQ;
            } else if (type.equals("ENQSID")) {
                action = ACTION_ENQUIRY_SID_REQ;
            } else if (type.equals("CRBTACREQ")) // changes done by ShashankS
                                                 // for CRBT Registration.
            {
                action = ACTION_CRBT_REGISTRATION;
            } else if (type.equals("CRBTSGREQ")) // changes done by ShashankS
                                                 // for CRBT Song Selection
            {
                action = ACTION_CRBT_SONG_SELECTION;
            } else if (type.equals("EVRTRFREQ")) // changes done by Harpreet for
                                                 // EVR
            {
                action = ACTION_ELECTRONIC_VOUCHER_RECHARGE;
            } else if (type.equals("EXEVRTRFREQ"))// added by Harpreet for EVR
                                                  // External Gateway API
            {
                action = ACTION_CHNL_EVR_XML;
            } else if (type.equals("SRCUSRREQ")) // Suspend Resume channel user
                                                 // through USSD
            {
                action = SUSPEND_RESUME_CUSR;
            } else if (type.equals("EXPVEVDREQ"))// added for Private Recharge
                                                 // through External G/W
            {
                action = ACTION_EXT_PRIVATERC_XML;
            } else if (type.equals("EXTC2CDRCRREQ")) {
                action = ACTION_EXT_DRCR_C2C_CUSER;// added for DrCr Transfer
                                                   // through External Gateway
            } else if (type.equals("ADDCHUSR"))// added for user creation thru
                                               // ussd
            {
                action = ACTION_EXT_USER_CREATION;
            } else if (type.equals("SRCUSRREQEX")) // Suspend Resume channel
                                                   // user through USSD
            {
                action = ACTION_SUSPEND_RESUME_CUSR_EXTGW;
            } else if (type.equals("LMBREQ")) {
                action = P2P_LEND_ME_BALANCE;
            } else if (type.equals("LXC2STSREQ"))// By rahuld for last X c2s
                                                 // trans
            {
                action = ACTION_C2S_RPT_LAST_XTRANSFER;
            } else if (type.equals("VASTRFREQ")) // VASTRIX CHANGES
            {
                action = ACTION_VAS_RC_REQUEST;
            } else if (type.equals("PVASTRFREQ")) {
                action = ACTION_PVAS_RC_REQUEST;
            } else if (type.equals("VASEXTRFREQ"))// r.dutt
            {
                action = ACTION_EXTVAS_RC_REQUEST;
            } else if (type.equals("PVASEXTRFREQ")) {
                action = ACTION_EXTPVAS_RC_REQUEST;
            }// VASTRIX ENDS
            else if (type.equals("EXTGRPH"))// ADDED BY ANUPAM MALVIYA FOR DMS
            {
                action = ACTION_EXT_GEOGRAPHY_REQUEST;
            } else if (type.equals("TRFRULETYP"))// ADDED BY ANUPAM MALVIYA FOR
                                                 // DMS
            {
                action = ACTION_EXT_TRF_RULE_TYPE_REQ;
            } else if (type.equals("EXTUSRADD"))// ADDED BY ANUPAM MALVIYA FOR
                                                // DMS
            {
                action = ACTION_EXT_USERADD_REQUEST;
            } else if (type.equals("AUTHCUSER"))// //added by shashank for
                                                // channel user authentication
            {
                action = ACTION_CRM_USER_AUTH_XML;
            } else if (type.equals("SIMACTREQ")) // change for SIM activation by
                                                 // sachin date 01/06/2011
            {
                action = ACTION_USSD_SIM_ACT_REQ;
            } else if (type.equals("EXTSYSSUBENQREQ"))// added by Sonali Garg to
                                                      // enquire subscriber at
                                                      // IN
            {
                action = ACTION_EXT_SUBENQ;
            } else if (type.equals("O2CEXTENQREQ")) {
                action = ACTION_O2C_SAP_ENQUIRY;
            } else if (type.equals("O2CEXTCODEUPDREQ")) {
                action = ACTION_O2C_SAP_EXTCODE_UPDATE;
            } else if (type.equals("COLENQREQ")) {
                action = ACTION_COL_ENQ;
            } else if (type.equals("COLBPREQ")) {
                action = ACTION_COL_BILLPAYMENT;
            } else if (type.equals("EXDTHTRFREQ")) {
                action = ACTION_DTH;
            } else if (type.equals("DTHTRFREQ")) {
                action = ACTION_DTH;
            } else if (type.equals("EXDCTRFREQ")) {
                action = ACTION_DC;
            } else if (type.equals("DCTRFREQ")) {
                action = ACTION_DC;
            } else if (type.equals("EXBPBTRFREQ")) {
                action = ACTION_BPB;
            } else if (type.equals("BPBTRFREQ")) {
                action = ACTION_BPB;
            } else if (type.equals("EXPINTRFREQ")) {
                action = ACTION_PIN;
            } else if (type.equals("PINTRFREQ")) {
                action = ACTION_PIN;
            } else if (type.equals("EXPMDTRFREQ")) {
                action = ACTION_PMD;
            } else if (type.equals("PMDTRFREQ")) {
                action = ACTION_PMD;
            } else if (type.equals("EXFLRCTRFREQ")) {
                action = ACTION_FLRC;
            } else if (type.equals("FLRCTRFREQ")) {
                action = ACTION_FLRC;
            } else if (type.equals("COLCCNREQ")) {
                action = ACTION_C2S_POSTPAID_REVERSAL;
            }
            /*
             * VFE else if(type.equals("EXPVEVDREQ")) //By Nilesh : private
             * Recharge through POS
             * {
             * action=ACTION_EXT_EVD_RC_POS;
             * }
             */
            else if (type.equals("EXPBENQREQ") || "PBENQREQ".equals(type)) // PPBENQ
                                                                           // rahul.d
                                                                           // POS
            {
                action = ACTION_EXT_PPBENQ;
            } else if (type.equals("EXTSYSSUBENQREQ"))// added by Sonali Garg to
                                                      // enquire subscriber at
                                                      // IN
            {
                action = ACTION_EXT_SUBENQ;
            }
            // Added by Diwakar on 20-JAN-2014 for ROBI
            else if (type.equals(ADD_USER_REQ)) {
                action = ADD_USER_ACTION;
            } else if (type.equals(MODIFY_USER_REQ)) {
                action = MODIFY_USER_ACTION;
            } else if (type.equals(DELETE_USER_REQ)) {
                action = DELETE_USER_ACTION;
            } else if (type.equals(SUSPEND_RESUME_USER_REQ)) {
                action = SUSPEND_RESUME_USER_ACTION;
            } else if (type.equals(ADD_DELETE_USER_ROLE_REQ)) {
                action = ADD_DELETE_USER_ROLE_ACTION;
            } else if (type.equals(CHANE_PASSWORD_REQ)) {
                action = CHANGE_PASSWORD_ACTION;
            } else if (type.equals(MNP_REQ)) {
                action = MNP_ACTION;
            } else if (type.equals(ICCID_MSISDN_MAP_REQ)) {
                action = ICCID_MSISDN_MAP_ACTION;
            }
            if (action == -1 && type.contains("USER")) {
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.EXTSYS_REQ_INVALID_TYPE_VALUE);
                throw new BTSLBaseException("ParserUtility", "actionChannelParser", SelfTopUpErrorCodesI.EXTSYS_REQ_INVALID_TYPE_VALUE);
            }
            // Ended here by Diwakar

            if (action == -1)
                throw new BTSLBaseException(SelfTopUpErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            p_requestVO.setActionValue(action);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("actionChannelParser", "Exception e: " + e);
            _log.debug("actionChannelParser", "exit TYUPE:" + type);
            e.printStackTrace();
            throw new BTSLBaseException(SelfTopUpErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("actionChannelParser", "exit action:" + action);
        }
        return action;
    }

    /**
     * Method to validate the Network status
     * 
     * @param p_requestVO
     * @param p_networkPrefixVO
     * @throws BTSLBaseException
     */
    public void validateNetwork(RequestVO p_requestVO, NetworkPrefixVO p_networkPrefixVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateNetwork", "Entered Request ID=" + p_requestVO.getRequestID() + " Network Code=" + p_networkPrefixVO.getNetworkCode());
        try {
            String networkID = p_networkPrefixVO.getNetworkCode();
            String message = null;
            p_requestVO.setRequestNetworkCode(networkID);

            // Check for location status (Active or suspend)
            if (!PretupsI.YES.equals(p_networkPrefixVO.getStatus())) {
                // if default language is english then pick language 1 message
                // else language 2
                LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage()))
                    message = p_networkPrefixVO.getLanguage1Message();
                else
                    message = p_networkPrefixVO.getLanguage2Message();
                p_requestVO.setSenderReturnMessage(message);
                throw new BTSLBaseException(this, "validateNetwork", SelfTopUpErrorCodesI.C2S_NETWORK_NOT_ACTIVE);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException(this, "validateNetwork", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validateNetwork", "Exiting Request ID=" + p_requestVO.getRequestID() + " Network Code=" + p_networkPrefixVO.getNetworkCode());
        }
    }
}
