package com.selftopup.pretups.util;

/**
 * @(#)PretupsBL.java
 *                    Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                    All Rights Reserved
 *                    This class holds the Business Logic for Pretups system.
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Abhijit Chauhan June 10,2005 Initial Creation
 *                    Abhijit Aug 10,2006 Modify ID=SUBTYPVALRECLMT
 *                    Ankit Zindal Nov 20,2006 ChangeID=LOCALEMASTER
 *                    Sourabh Gupta Dec 14,2006 ChangeID=TATASKYRCHG
 *                    Ankit Zindal Dec 19, 2006 Change ID=ACCOUNTID
 *                    Santanu Mohanty Feb 22,2008 unbarred pin automatically
 *                    ----------------------------------------------------------
 *                    --------------------------------------
 */

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.BTSLMessages;
import com.selftopup.common.ListValueVO;
import com.selftopup.common.TypesI;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.domain.businesslogic.CategoryVO;
import com.selftopup.pretups.gateway.businesslogic.MessageGatewayCache;
import com.selftopup.pretups.gateway.businesslogic.MessageGatewayVO;
import com.selftopup.pretups.gateway.businesslogic.RequestGatewayVO;
import com.selftopup.pretups.gateway.util.GatewayParsersI;
import com.selftopup.pretups.logging.UnauthorizedAccessLog;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.master.businesslogic.NetworkServiceVO;
import com.selftopup.pretups.master.businesslogic.NetworkServicesCache;
import com.selftopup.pretups.master.businesslogic.ResponseInterfaceDetailVO;
import com.selftopup.pretups.master.businesslogic.ServiceClassInfoByCodeCache;
import com.selftopup.pretups.master.businesslogic.ServiceClassVO;
import com.selftopup.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.selftopup.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.selftopup.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceCacheVO;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.product.businesslogic.NetworkProductCache;
import com.selftopup.pretups.product.businesslogic.NetworkProductDAO;
import com.selftopup.pretups.product.businesslogic.NetworkProductServiceTypeCache;
import com.selftopup.pretups.product.businesslogic.NetworkProductVO;
import com.selftopup.pretups.product.businesslogic.ProductVO;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.restrictedsubs.businesslogic.RestrictedSubscriberBL;
import com.selftopup.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.selftopup.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.selftopup.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.selftopup.pretups.routing.subscribermgmt.businesslogic.RoutingDAO;
import com.selftopup.pretups.routing.subscribermgmt.businesslogic.RoutingVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordUtil;
import com.selftopup.pretups.skey.businesslogic.SKeyTransferDAO;
import com.selftopup.pretups.skey.businesslogic.SKeyTransferVO;
import com.selftopup.pretups.subscriber.businesslogic.BarredUserDAO;
import com.selftopup.pretups.subscriber.businesslogic.BarredUserVO;
import com.selftopup.pretups.subscriber.businesslogic.ReceiverVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.subscriber.businesslogic.SubscriberControlDAO;
import com.selftopup.pretups.transfer.businesslogic.TransferDAO;
import com.selftopup.pretups.transfer.businesslogic.TransferItemVO;
import com.selftopup.pretups.transfer.businesslogic.TransferRulesCache;
import com.selftopup.pretups.transfer.businesslogic.TransferRulesVO;
import com.selftopup.pretups.transfer.businesslogic.TransferVO;
import com.selftopup.pretups.user.businesslogic.ChannelUserVO;
import com.selftopup.pretups.whitelist.businesslogic.WhiteListDAO;
import com.selftopup.pretups.whitelist.businesslogic.WhiteListVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;
import com.selftopup.util.CryptoUtil;
import com.selftopup.util.OracleUtil;

public class PretupsBL {
    private static Log _log = LogFactory.getLog(PretupsBL.class.getName());
    public static BarredUserDAO _barredUserDAO = new BarredUserDAO();
    public static TransferDAO _transferDAO = new TransferDAO();
    public static SubscriberControlDAO _subscriberControlDAO = new SubscriberControlDAO();
    public static SubscriberDAO _subscriberDAO = new SubscriberDAO();

    public static OperatorUtilI _operatorUtil = null;
    public static NetworkProductDAO _networkProductDAO = new NetworkProductDAO();
    public static NumberPortDAO _numberPortDAO = new NumberPortDAO();
    // Loads operator specific class
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * Checks whether User is barred in the System.
     * 
     * @param con
     * @param filteredMSISDN
     * @param module
     * @param type
     * @return
     * @throws BTSLBaseException
     */
    public static void checkMSISDNBarred(Connection con, String filteredMSISDN, String networkCode, String module, String type) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkMSISDNBarred", "Entered filteredMSISDN:" + filteredMSISDN + " networkCode:" + networkCode + " module:" + module + " type:" + type);
        boolean barred = true;
        try {
            barred = _barredUserDAO.isExists(con, module, networkCode, filteredMSISDN, type, null);
            if (barred) {
                if (type.equals(PretupsI.USER_TYPE_RECEIVER)) {
                    throw new BTSLBaseException("PretupsBL", "checkMSISDNBarred", SelfTopUpErrorCodesI.ERROR_RECEIVER_USERBARRED, 0, new String[] { filteredMSISDN }, null);
                } else
                    throw new BTSLBaseException("PretupsBL", "checkMSISDNBarred", SelfTopUpErrorCodesI.ERROR_USERBARRED);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("checkMSISDNBarred", "Exception filteredMSISDN:" + filteredMSISDN + " Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[checkMSISDNBarred]", "", filteredMSISDN, networkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "checkMSISDNBarred", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("checkMSISDNBarred", "Exiting filteredMSISDN:" + filteredMSISDN + " barred:" + barred);
        }
    }

    /**
     * Get Network Details.This method loads network details based on msisdn
     * prefix from memory.
     * 
     * @param p_filteredMSISDN
     * @param p_subscriberType
     * @return NetworkVO
     */
    public static NetworkPrefixVO getNetworkDetails(String p_filteredMSISDN, String p_subscriberType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getNetworkDetails", "Entered filteredMSISDN:" + p_filteredMSISDN + " p_subscriberType=" + p_subscriberType);
        NetworkPrefixVO networkPrefixVO = null;
        try {
            String msisdnPrefix = getMSISDNPrefix(p_filteredMSISDN);
            if (p_subscriberType.equals(PretupsI.USER_TYPE_SENDER))
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            else
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix, false);

            if (networkPrefixVO == null && p_subscriberType.equals(PretupsI.USER_TYPE_SENDER))
                throw new BTSLBaseException("PretupsBL", "getNetworkDetails", SelfTopUpErrorCodesI.ERROR_NETWORK_NOTFOUND);
            else if (networkPrefixVO == null && p_subscriberType.equals(PretupsI.USER_TYPE_RECEIVER))
                throw new BTSLBaseException("PretupsBL", "getNetworkDetails", SelfTopUpErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_filteredMSISDN }, null);
            return networkPrefixVO;
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getNetworkDetails]", "", p_filteredMSISDN, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "getNetworkDetails", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getNetworkDetails", "Exiting networkVO:" + networkPrefixVO);
        }
    }

    /**
     * Get MSISDN Prefix
     * 
     * @param filteredMSISDN
     * @return
     */
    public static String getMSISDNPrefix(String filteredMSISDN) {
        if (_log.isDebugEnabled())
            _log.debug("getMSISDNPrefix", "Entered filteredMSISDN:" + filteredMSISDN + ",SystemPreferences.MSISDN_PREFIX_LENGTH=" + SystemPreferences.MSISDN_PREFIX_LENGTH);
        String msisdnPrefix = filteredMSISDN.substring(0, SystemPreferences.MSISDN_PREFIX_LENGTH);
        if (_log.isDebugEnabled())
            _log.debug("getMSISDNPrefix", "Exit msisdnPrefix:" + msisdnPrefix);
        return msisdnPrefix;
    }

    /**
     * Get Filtered MSISDN
     * 
     * @param p_msisdn
     * @return
     * @throws BTSLBaseException
     */
    public static String getFilteredMSISDN(String p_msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getFilteredMSISDN", "Entered p_msisdn:" + p_msisdn);
        // modified by Amit Raheja for NNP
        return _operatorUtil.getSystemFilteredMSISDN(p_msisdn);
        /*
         * String msisdn=null;
         * boolean prefixFound=false;
         * String prefix=null;
         * try
         * {
         * if(p_msisdn.length()>SystemPreferences.MIN_MSISDN_LENGTH)
         * {
         * if(_log.isDebugEnabled())
         * _log.debug("getFilteredMSISDN","(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST):"
         * +(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST));
         * 
         * StringTokenizer strTok=new
         * StringTokenizer((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST),",");
         * while(strTok.hasMoreTokens())
         * {
         * prefix=strTok.nextToken();
         * if(p_msisdn.startsWith(prefix,0))
         * {
         * prefixFound=true;
         * break;
         * }
         * else
         * continue;
         * }
         * if(prefixFound)
         * msisdn=p_msisdn.substring(prefix.length());
         * else
         * msisdn=p_msisdn;
         * }
         * else
         * msisdn=p_msisdn;
         * }
         * catch(Exception e)
         * {
         * _log.error("getFilteredMSISDN",
         * "Exception while getting the mobile no from passed no="
         * +e.getMessage());
         * e.printStackTrace();
         * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
         * EventStatusI
         * .RAISED,EventLevelI.FATAL,"PretupsBL[getFilteredMSISDN]",""
         * ,p_msisdn,"","Exception:"+e.getMessage());
         * throw new
         * BTSLBaseException("PretupsBL","getFilteredMSISDN",PretupsErrorCodesI
         * .P2P_ERROR_EXCEPTION);
         * }
         * finally
         * {
         * if(_log.isDebugEnabled())_log.debug("getFilteredMSISDN",
         * "Exiting Filtered msisdn="+msisdn);
         * }
         * return msisdn;
         */
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    /**
     * Parse Message
     * 
     * @param p_message
     * @return
     * @throws BTSLBaseException
     */
    public static String[] parsePlainMessage(String p_message) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("parsePlainMessage", "Entered p_message=" + p_message);
        String MESSAGE_SEP = SystemPreferences.P2P_PLAIN_SMS_SEPARATOR;
        if (BTSLUtil.isNullString(MESSAGE_SEP))
            MESSAGE_SEP = " ";

        // String[] messageArray=p_message.split(MESSAGE_SEP);
        String[] messageArray = BTSLUtil.split(p_message, MESSAGE_SEP);
        if (messageArray.length < 1)
            throw new BTSLBaseException("PretupsBL", "parsePlainMessage", SelfTopUpErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        if (_log.isDebugEnabled())
            _log.debug("parsePlainMessage", "Exiting messageArray length=" + messageArray.length);
        return messageArray;
    }

    /**
     * Get Service Keyword Handler Object
     * 
     * @param smsHandler
     * @return
     * @throws BTSLBaseException
     */
    public static java.lang.Object getServiceKeywordHandlerObj(String handlerClassName) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getServiceKeywordHandlerObj", "Entered handlerClassName=" + handlerClassName);
        ServiceKeywordControllerI handlerObj = null;
        try {
            handlerObj = (ServiceKeywordControllerI) Class.forName(handlerClassName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getServiceKeywordHandlerObj", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getServiceKeywordHandlerObj]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "getServiceKeywordHandlerObj", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("getServiceKeywordHandlerObj", "Exiting");
        return handlerObj;
        // return _smsHandlerObj;
    }// end of getSmsHandlerObj

    /**
     * Generates a skey whose length is system specific
     * 
     * @return long
     * @throws BTSLBaseException
     */
    public static long generateFreshSKey() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("generateFreshSKey", "Entered ");
        long currentTimeInMillis = System.currentTimeMillis();
        long sKey = 0;
        String currentTimeInMillisStr = String.valueOf(currentTimeInMillis);
        String sKeyStr = currentTimeInMillisStr.substring(currentTimeInMillisStr.length() - SystemPreferences.SKEY_LENGTH, currentTimeInMillisStr.length());
        try {
            sKey = Long.parseLong(sKeyStr);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("generateSKey", "Not able to convert Skey value to long");
            throw new BTSLBaseException("PretupsBL", "generateFreshSKey", SelfTopUpErrorCodesI.SKEY_NOTCONVERTSTRTOLONG);
        }
        if (_log.isDebugEnabled())
            _log.debug("generateFreshSKey", "Exiting with sKey=" + sKey);
        return sKey;
    }

    /**
     * Get System Amount
     * 
     * @param p_amountStr
     * @return
     * @throws BTSLBaseException
     */
    public static long getSystemAmount(String p_amountStr) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getSystemAmount", "Entered p_amountStr=" + p_amountStr);
        long amount = 0;
        try {
            double p_validAmount = Double.parseDouble(p_amountStr);
            amount = getSystemAmount(p_validAmount);
        } catch (Exception e) {
            throw new BTSLBaseException("PretupsBL", "getSystemAmount", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
        }
        if (_log.isDebugEnabled())
            _log.debug("getSystemAmount", "Exiting amount:" + amount);
        return amount;
    }

    /**
     * Get System Amount
     * 
     * @param p_amountStr
     * @return
     * @throws BTSLBaseException
     */
    public static long getSystemAmount(double p_validAmount) throws BTSLBaseException {
        // if(_log.isDebugEnabled())
        // _log.debug("getSystemAmount","Entered p_validAmount="+p_validAmount);
        int multiplicationFactor = SystemPreferences.AMOUNT_MULT_FACTOR;
        long amount = 0;
        amount = (long) (Round((p_validAmount * multiplicationFactor), 2));
        // if(_log.isDebugEnabled())
        // _log.debug("getSystemAmount","Exiting amount:"+amount);
        return amount;
    }

    /**
     * Method to round values till precision
     * 
     * @param Rval
     * @param Rpl
     * @return
     */
    public static double Round(double Rval, int Rpl) {
        double p = Math.pow(10, Rpl);
        Rval = Rval * p;
        double tmp = Math.round(Rval);
        return tmp / p;
    }

    /**
     * Get Display Amount
     * 
     * @param p_amount
     * @return
     * @throws BTSLBaseException
     */
    public static String getDisplayAmount(long p_amount) {
        // if(_log.isDebugEnabled())_log.debug("getDisplayAmount","Entered p_amount:"+p_amount);
        int multiplicationFactor = SystemPreferences.AMOUNT_MULT_FACTOR;
        double amount = (double) p_amount / (double) multiplicationFactor;
        String amountStr = new DecimalFormat("#############.###").format(amount);
        try {
            long l = Long.parseLong(amountStr);
            amountStr = String.valueOf(l);
        } catch (Exception e) {
            amountStr = new DecimalFormat("############0.00#").format(amount);
        }
        // if(_log.isDebugEnabled())_log.debug("getDisplayAmount","Exiting display amount:"+amountStr);
        return amountStr;
    }

    /**
     * Add Transfer Details
     * 
     * @param p_con
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void addTransferDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        try {
            int updateCount = _transferDAO.addTransferDetails(p_con, p_transferVO);
            if (updateCount <= 0)
                throw new BTSLBaseException("PretupsBL", "addTransferDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[addTransferDetails]", p_transferVO.getTransferID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "addTransferDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Add Validity Extension Transfer Details
     * 
     * @param p_con
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void addValExtTransferDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        try {
            int updateCount = _transferDAO.addValExtTransferDetails(p_con, p_transferVO);
            if (updateCount <= 0)
                throw new BTSLBaseException("PretupsBL", "addValExtTransferDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[addValExtTransferDetails]", p_transferVO.getTransferID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "addValExtTransferDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Update Transfer Details
     * 
     * @param p_con
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void updateTransferDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        try {
            int updateCount = _transferDAO.updateTransferDetails(p_con, p_transferVO);
            if (updateCount <= 0)
                throw new BTSLBaseException("PretupsBL", "updateTransferDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[updateTransferDetails]", p_transferVO.getTransferID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "updateTransferDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Update Validity Extension Transfer Details
     * 
     * @param p_con
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void updateValExtTransferDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        try {
            int updateCount = _transferDAO.updateValExtTransferDetails(p_con, p_transferVO);
            if (updateCount <= 0)
                throw new BTSLBaseException("PretupsBL", "updateValExtTransferDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[updateValExtTransferDetails]", p_transferVO.getTransferID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "updateValExtTransferDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Genrate the pin
     * 
     * @return
     */
    public static String genratePin() {
        StringBuffer sbf = new StringBuffer();
        sbf.append(System.currentTimeMillis());

        return sbf.substring(sbf.length() - (SystemPreferences.PIN_LENGTH), sbf.length());
    }

    /**
     * Get Network Prefix Details.This method loads network prefix details based
     * on msisdn prefix from memory.
     * 
     * @param filteredMSISDN
     * @return NetworkPrefixVO
     */
    public static NetworkPrefixVO getNetworkPrefixDetails(String filteredMSISDN) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getNetworkPrefixDetails", "Entered filteredMSISDN:" + filteredMSISDN);
        NetworkPrefixVO networkPrefixVO = null;
        try {
            String msisdnPrefix = getMSISDNPrefix(filteredMSISDN);
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null)
                throw new BTSLBaseException("PretupsBL", "getNetworkPrefixDetails", SelfTopUpErrorCodesI.P2P_ERROR_NETWORKPREFIX_NOTFOUND);
            return networkPrefixVO;
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getNetworkPrefixDetails", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getNetworkPrefixDetails]", "", filteredMSISDN, "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "getNetworkPrefixDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getNetworkPrefixDetails", "Exiting networkPrefixVO:" + networkPrefixVO);
        }
    }

    /**
     * Load Sender Transfer Status
     * 
     * @param p_con
     * @param p_msisdn
     * @param p_fromDate
     * @param p_toDate
     * @return long[] which have total transfer count and total transfer amount
     * @throws Exception
     * @author avinash.kamthan
     */
    public static long[] loadSenderTransferStatus(Connection p_con, String p_msisdn, Date p_fromDate, Date p_toDate) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadSenderTransferStatus", "Entered p_msisdn:" + p_msisdn + " p_fromDate:" + p_fromDate + " p_toDate:" + p_toDate);
        long data[] = null;
        try {
            data = _transferDAO.loadTransferStatus(p_con, p_msisdn, BTSLUtil.getDateStringFromDate(p_fromDate), BTSLUtil.getDateStringFromDate(p_toDate));
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("loadSenderTransferStatus", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[loadSenderTransferStatus]", "", p_msisdn, "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "loadSenderTransferStatus", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }

        if (_log.isDebugEnabled())
            _log.debug("loadSenderTransferStatus", "Exiting data:" + data);

        return data;
    }

    /**
     * Validate Transfer Rule
     * 
     * @param p_transferVO
     * @throws BTSLBaseException
     *             1. Check Whether Transfer Rule is not existing and we are
     *             using ALL service class code for sender and Receiver
     *             2. If 1 Not exists then show error
     *             3. If ALL is not being used for receiver then load for
     *             reciever ALL and check in transfer rule
     *             4. If not exist then check for sender ALL and check in
     *             transfer rule
     *             5. Last check for ALL sender and ALL receiver rule
     */
    public static void validateTransferRule(Connection p_con, TransferVO p_transferVO, String p_module) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateTransferRule", p_transferVO.getTransferID(), "Entered");
        TransferRulesVO transferRulesVO = null;
        String senderServiceClass = null;
        String recServiceClass = null;
        String cardGroupSetStatus = null;
        boolean isDefaultCardGroupExist = false;
        if (p_module.equals(PretupsI.P2P_MODULE)) {
            SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
            ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
            if (_log.isDebugEnabled())
                _log.debug("validateTransferRule", p_transferVO.getTransferID(), "Validation For Network Code=" + senderVO.getNetworkCode() + " Sender Subscriber Type=" + senderVO.getSubscriberType() + "Receiver Subscriber Type=" + receiverVO.getSubscriberType() + " Sender Service Class ID=" + senderVO.getServiceClassCode() + " Receiver Service Class ID=" + receiverVO.getServiceClassCode() + "" + PretupsI.NOT_APPLICABLE);
            transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), receiverVO.getNetworkCode(), senderVO.getSubscriberType(), receiverVO.getSubscriberType(), senderVO.getServiceClassCode(), receiverVO.getServiceClassCode(), p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE);
            if (transferRulesVO == null && receiverVO.isUsingAllServiceClass() && senderVO.isUsingAllServiceClass()) {
                CardGroupDAO cardGroupDAO = new CardGroupDAO();
                isDefaultCardGroupExist = cardGroupDAO.isDefaultCardGroupExist(p_con, p_transferVO);
                cardGroupSetStatus = p_transferVO.getStatus();
            } else if (transferRulesVO == null) {
                if (!senderVO.isUsingAllServiceClass()) {
                    TransferItemVO senderTransferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(0);
                    try {
                        senderServiceClass = validateServiceClass(p_con, p_transferVO.getSenderMsisdn(), p_transferVO.getTransferID(), senderTransferItemVO.getInterfaceID(), PretupsI.ALL, senderTransferItemVO.getAccountStatus(), senderTransferItemVO.getRequestValue(), senderTransferItemVO.getUserType(), p_module);
                    } catch (Exception e) {
                    }
                } else
                    senderServiceClass = senderVO.getServiceClassCode();
                if (!BTSLUtil.isNullString(senderServiceClass))
                    transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), receiverVO.getNetworkCode(), senderVO.getSubscriberType(), receiverVO.getSubscriberType(), senderServiceClass, receiverVO.getServiceClassCode(), p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE);
                if (transferRulesVO == null) {
                    if (!receiverVO.isUsingAllServiceClass() && !senderVO.getServiceClassCode().equalsIgnoreCase(receiverVO.getServiceClassCode())) {
                        TransferItemVO receiverTransferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);
                        try {
                            recServiceClass = validateServiceClass(p_con, p_transferVO.getReceiverMsisdn(), p_transferVO.getTransferID(), receiverTransferItemVO.getInterfaceID(), PretupsI.ALL, receiverTransferItemVO.getAccountStatus(), receiverTransferItemVO.getRequestValue(), receiverTransferItemVO.getUserType(), p_module);
                        } catch (Exception e) {
                        }
                    } else if (!receiverVO.isUsingAllServiceClass() && !BTSLUtil.isNullString(senderServiceClass) && senderVO.getServiceClassCode().equalsIgnoreCase(receiverVO.getServiceClassCode()))
                        recServiceClass = senderServiceClass;
                    else
                        recServiceClass = receiverVO.getServiceClassCode();
                    if (!BTSLUtil.isNullString(recServiceClass))
                        transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), receiverVO.getNetworkCode(), senderVO.getSubscriberType(), receiverVO.getSubscriberType(), senderVO.getServiceClassCode(), recServiceClass, p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE);
                }
                if (transferRulesVO == null && !BTSLUtil.isNullString(senderServiceClass) && !BTSLUtil.isNullString(recServiceClass))
                    transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), receiverVO.getNetworkCode(), senderVO.getSubscriberType(), receiverVO.getSubscriberType(), senderServiceClass, recServiceClass, p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE);
                if (transferRulesVO == null) {
                    CardGroupDAO cardGroupDAO = new CardGroupDAO();
                    isDefaultCardGroupExist = cardGroupDAO.isDefaultCardGroupExist(p_con, p_transferVO);
                    cardGroupSetStatus = p_transferVO.getStatus();
                }
            }
            if (transferRulesVO == null && !isDefaultCardGroupExist) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Default card group not exist for the service type " + p_transferVO.getServiceType() + " with  Sub Service=" + p_transferVO.getSubService());
                throw new BTSLBaseException("PretupsBL", "validateTransferRule", SelfTopUpErrorCodesI.P2P_ERROR_DEFAULT_CARDGROUP_NOTEXIST);
            }
            if (transferRulesVO != null && transferRulesVO.getStatus().equals(PretupsI.SUSPEND)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Transfer Rule is suspended in P2P between " + senderVO.getServiceClassCode() + " and " + receiverVO.getServiceClassCode() + " sender service class code=" + p_transferVO.getSenderTransferItemVO().getServiceClassCode() + " and receiver service class code=" + p_transferVO.getReceiverTransferItemVO().getServiceClassCode());
                throw new BTSLBaseException("PretupsBL", "validateTransferRule", SelfTopUpErrorCodesI.P2P_ERROR_TRANSFER_RULE_SUSPENDED);
            }
        } else {

        }
        if (transferRulesVO != null) {
            p_transferVO.setCardGroupSetID(transferRulesVO.getCardGroupSetID());
            if (PretupsI.SUSPEND.equals(transferRulesVO.getCardGroupSetIDStatus())) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Card Group Set is suspended " + transferRulesVO.getCardGroupSetID());
                // if default language is english then pick language 1 message
                // else language 2
                String message = null;
                // ChangeID=LOCALEMASTER
                // which language message to be send is determined from the
                // locale master
                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_transferVO.getLocale())).getMessage()))
                    message = transferRulesVO.getCardGroupMessage1();
                else
                    message = transferRulesVO.getCardGroupMessage2();
                p_transferVO.setSenderReturnMessage(message);
                throw new BTSLBaseException("PretupsBL", "validateTransferRule", SelfTopUpErrorCodesI.ERROR_CARD_GROUP_SET_SUSPENDED);
            }
        } else if (PretupsI.SUSPEND.equals(cardGroupSetStatus)) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Card Group Set is suspended   " + p_transferVO.getCardGroupSetID());
            throw new BTSLBaseException("PretupsBL", "validateTransferRule", SelfTopUpErrorCodesI.ERROR_CARD_GROUP_SET_SUSPENDED);
        }
        if (_log.isDebugEnabled())
            _log.debug("validateTransferRule", p_transferVO.getTransferID(), "Exiting");
    }

    /**
     * Validate Request Message Gateway.
     * Method is updated for gateway status check that is now putted on
     * receiver.
     * This is done becuse in case of suspend gateway message has to be send to
     * sender.
     * This is done on 25/07/06 by ankit zindal as discussed with sanjay/gurjeet
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public static void validateRequestMessageGateway(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateRequestMessageGateway", p_requestVO.getRequestIDStr(), "Entered");
        try {
            MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(p_requestVO.getRequestGatewayCode());
            p_requestVO.setMessageGatewayVO(messageGatewayVO);
            if (messageGatewayVO == null)
                throw new BTSLBaseException("PretupsBL", "validateRequestMessageGateway", SelfTopUpErrorCodesI.ERROR_NOTFOUND_MESSAGEGATEWAY);
            RequestGatewayVO requestGatewayVO = messageGatewayVO.getRequestGatewayVO();
            if (requestGatewayVO == null)
                throw new BTSLBaseException("PretupsBL", "validateRequestMessageGateway", SelfTopUpErrorCodesI.ERROR_NOTFOUND_REQMESSAGEGATEWAY);
            if (!p_requestVO.getRequestGatewayType().equalsIgnoreCase(messageGatewayVO.getGatewayType())) {
                if (_log.isDebugEnabled())
                    _log.debug("validateRequestMessageGateway", p_requestVO.getRequestIDStr(), "Request Gateway Type:" + p_requestVO.getRequestGatewayType() + " Gateway Type for code:" + messageGatewayVO.getGatewayType());
                throw new BTSLBaseException("PretupsBL", "validateRequestMessageGateway", SelfTopUpErrorCodesI.ERROR_INVALID_REQUESTINTTYPE);
            }
            if (!requestGatewayVO.getServicePort().equals(p_requestVO.getServicePort())) {
                if (_log.isDebugEnabled())
                    _log.debug("validateRequestMessageGateway", p_requestVO.getRequestIDStr(), "Request Service Port:" + p_requestVO.getServicePort() + " Allowed Port:" + requestGatewayVO.getServicePort());
                throw new BTSLBaseException("PretupsBL", "validateRequestMessageGateway", SelfTopUpErrorCodesI.ERROR_INVALID_SERVICEPORT);
            }

            if (requestGatewayVO.getAuthType().equals(PretupsI.ALL) || requestGatewayVO.getAuthType().equals(PretupsI.AUTH_TYPE_IP)) {
                if (!p_requestVO.getRemoteIP().equals(messageGatewayVO.getHost())) {
                    if (_log.isDebugEnabled())
                        _log.debug("validateRequestMessageGateway", p_requestVO.getRequestIDStr(), "Remote IP:" + p_requestVO.getRemoteIP() + " Allowed Host:" + messageGatewayVO.getHost());
                    throw new BTSLBaseException("PretupsBL", "validateRequestMessageGateway", SelfTopUpErrorCodesI.ERROR_INVALID_IP);
                }
            }
            if (requestGatewayVO.getAuthType().equals(PretupsI.ALL) || requestGatewayVO.getAuthType().equals(PretupsI.AUTH_TYPE_LOGIN)) {
                if (!BTSLUtil.NullToString(requestGatewayVO.getLoginID()).equals(p_requestVO.getLogin())) {
                    throw new BTSLBaseException("PretupsBL", "validateRequestMessageGateway", SelfTopUpErrorCodesI.ERROR_INVALID_LOGIN);
                }

                // if(!BTSLUtil.NullToString(p_requestVO.getPassword()).equals(requestGatewayVO.getDecryptedPassword()))
                /*
                 * change done by ashishT for hashing implementation.
                 * hashed gateway password is sent in reacherge request ,then it
                 * is compared with the hash value stored in db.
                 */
                if ("SHA".equalsIgnoreCase(SystemPreferences.PINPAS_EN_DE_CRYPTION_TYPE)) {
                    // if(p_requestVO.getPassword().length()>SystemPreferences.MAX_LOGIN_PWD_LENGTH)
                    // means hash value is comming in request..
                    if (messageGatewayVO.getReqpasswordtype().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                        if (PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(requestGatewayVO.getDecryptedPassword(), p_requestVO.getPassword())))
                        // if(!BTSLUtil.NullToString(p_requestVO.getPassword()).equals(BTSLUtil.decryptText(requestGatewayVO.getPassword())))
                        {
                            throw new BTSLBaseException("PretupsBL", "validateRequestMessageGateway", SelfTopUpErrorCodesI.ERROR_INVALID_PASSWORD);
                        }
                    }
                    // means plain password is commming in request..
                    else {
                        if (!(requestGatewayVO.getPassword().equalsIgnoreCase(p_requestVO.getPassword()))) {
                            throw new BTSLBaseException("PretupsBL", "validateRequestMessageGateway", SelfTopUpErrorCodesI.ERROR_INVALID_PASSWORD);
                        }
                    }
                } else {
                    // Means Gateway password in request is Plain & Application
                    // is running on DES/AES Algo.
                    if (messageGatewayVO.getReqpasswordtype().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                        if (!BTSLUtil.NullToString(p_requestVO.getPassword()).equals(BTSLUtil.decryptText(requestGatewayVO.getPassword()))) {
                            throw new BTSLBaseException("PretupsBL", "validateRequestMessageGateway", SelfTopUpErrorCodesI.ERROR_INVALID_PASSWORD);
                        }
                    } else // Means Gateway password in request is encrypted &
                           // Application is running on DES/AES Algo.
                    {
                        if (!(requestGatewayVO.getPassword().equalsIgnoreCase(p_requestVO.getPassword()))) {
                            throw new BTSLBaseException("PretupsBL", "validateRequestMessageGateway", SelfTopUpErrorCodesI.ERROR_INVALID_PASSWORD);
                        }
                    }
                }
            }
            if (((String) p_requestVO.getSourceType()).equals("null") || ((String) p_requestVO.getSourceType()).equals(null) || ((String) p_requestVO.getSourceType()).length() == 0) {
                throw new BTSLBaseException("PretupsBL", "validateRequestMessageGateway", SelfTopUpErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            }
            p_requestVO.setServicePort(requestGatewayVO.getServicePort());
        }

        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateRequestMessageGateway]", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", "Request Gateway Validation Failed For Code" + p_requestVO.getRequestGatewayCode() + " ,Error Code=" + be.getMessage());
            // In case of unautherise access no message is send to the user.
            p_requestVO.setSenderMessageRequired(false);
            // Log in Unauthorize log
            UnauthorizedAccessLog.log(p_requestVO, be.getMessageKey(), be.getMessage());
            throw be;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validateRequestMessageGateway", p_requestVO.getRequestIDStr(), "Exiting");
        }
    }

    public static void handlePushInterface(ResponseInterfaceDetailVO responseInterfaceDetailVO, String p_msisdn, String p_message) {
        if (_log.isDebugEnabled())
            _log.debug("handlePushInterface", "Entered p_msisdn:" + p_msisdn + " p_message:" + p_message);
        if (_log.isDebugEnabled())
            _log.debug("handlePushInterface", "Exiting");
    }

    /**
     * Method to check whether the service type is allowed to the user or not
     * 
     * @param p_requestID
     * @param p_msisdn
     * @param p_serviceType
     * @param p_allowedServiceTypes
     * @throws BTSLBaseException
     */
    public static void checkServiceTypeAllowed(String p_requestID, String p_msisdn, String p_serviceType, ArrayList p_allowedServiceTypes) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkServiceTypeAllowed", "Entered for Request ID:" + p_requestID + " MSISDN=" + p_msisdn + " p_serviceType=" + p_serviceType);
        try {
            if (!p_allowedServiceTypes.contains(p_serviceType)) {
                _log.error("checkServiceTypeAllowed", "Request ID:" + p_requestID + " MSISDN=" + p_msisdn + " Service Type not found in allowed List");
                throw new BTSLBaseException("PretupsBL", "checkServiceTypeAllowed", SelfTopUpErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
            }
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("checkServiceTypeAllowed", "Request ID:" + p_requestID + " MSISDN=" + p_msisdn + " Exception :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[checkServiceTypeAllowed]", p_requestID, p_msisdn, "", "Not able to check whether Service Type is in allowed List for Request ID:" + p_requestID + " and MSISDN:" + p_msisdn + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "checkServiceTypeAllowed", SelfTopUpErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("checkServiceTypeAllowed", "Exiting ");
    }

    public static int[] getLocalInfo(String binaryString) {
        byte b[] = binaryString.getBytes();
        int[] cell_id = new int[4];
        StringBuffer sb = new StringBuffer();

        // Determine MCC
        sb.append(b[0] & 0x0f);
        sb.append((b[0] & 0xf0) >>> 4);
        sb.append(b[1] & 0x0f);
        cell_id[0] = Integer.parseInt(sb.toString());

        // Determine MNC
        int i1 = b[2] & 0x0f; // << -- low bit
        int i2 = (b[2] & 0xf0) >>> 4; // << -- high bit
        sb = new StringBuffer();
        if (i2 == 15)
            sb.append(i1);
        else if (i1 > 9 || i2 > 9)
            sb.append(i1 + i2);
        else {
            sb.append(i1);
            sb.append(i2);
        }
        cell_id[1] = Integer.parseInt(sb.toString());

        // Determine LAC
        sb = new StringBuffer();
        addhighbits(sb, b[3]);
        addlowbits(sb, b[3]);
        addhighbits(sb, b[4]);
        addlowbits(sb, b[4]);
        cell_id[2] = Integer.parseInt(sb.toString(), 16);

        // Determine CID
        sb = new StringBuffer();
        addhighbits(sb, b[5]);
        addlowbits(sb, b[5]);
        addhighbits(sb, b[6]);
        addlowbits(sb, b[6]);
        cell_id[3] = Integer.parseInt(sb.toString(), 16);
        return cell_id;
    }

    /**
     * Used by getLocalInfo() method
     * 
     * @author Rahul Kumar
     */
    public static void addhighbits(StringBuffer sb, byte b1) {
        sb.append(Integer.toHexString((b1 >>> 4) & 0x0f));
    }

    /**
     * Used by getLocalInfo() method
     * 
     * @author Rahul Kumar
     */
    public static void addlowbits(StringBuffer sb, byte b1) {
        sb.append(Integer.toHexString(b1 & 0x0f));
    }

    /**
     * Validates the MSISDN
     * 
     * @param p_receiverVO
     * @param p_msisdn
     * @throws BTSLBaseException
     *             21/04/07 : Added Param p_con for MNP
     */
    public static void validateMsisdn(Connection p_con, ReceiverVO p_receiverVO, String p_requestID, String p_msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateMsisdn", p_requestID, "Entered for p_msisdn= " + p_msisdn);
        String[] strArr = null;
        try {
            if (BTSLUtil.isNullString(p_msisdn))
                throw new BTSLBaseException("PretupsBL", "validateMsisdn", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            p_msisdn = getFilteredMSISDN(p_msisdn);
            p_msisdn = _operatorUtil.addRemoveDigitsFromMSISDN(p_msisdn);
            if ((p_msisdn.length() < SystemPreferences.MIN_MSISDN_LENGTH || p_msisdn.length() > SystemPreferences.MAX_MSISDN_LENGTH)) {
                if (SystemPreferences.MIN_MSISDN_LENGTH != SystemPreferences.MAX_MSISDN_LENGTH) {
                    strArr = new String[] { p_msisdn, String.valueOf(SystemPreferences.MIN_MSISDN_LENGTH), String.valueOf(SystemPreferences.MAX_MSISDN_LENGTH) };
                    throw new BTSLBaseException("PretupsBL", "validateMsisdn", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTINRANGE, 0, strArr, null);
                } else {
                    strArr = new String[] { p_msisdn, String.valueOf(SystemPreferences.MIN_MSISDN_LENGTH) };
                    throw new BTSLBaseException("PretupsBL", "validateMsisdn", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_MSISDN_LEN_NOTSAME, 0, strArr, null);
                }
            }
            try {
                long lng = Long.parseLong(p_msisdn);
            } catch (Exception e) {
                strArr = new String[] { p_msisdn };
                throw new BTSLBaseException("PretupsBL", "validateMsisdn", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTNUMERIC, 0, strArr, null);
            }
            p_receiverVO.setMsisdn(p_msisdn);
            if (_log.isDebugEnabled() && p_receiverVO.getMsisdn() != null)
                _log.debug("", "*********************" + p_receiverVO.getMsisdn());

            NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(p_msisdn, PretupsI.USER_TYPE_RECEIVER);
            if (networkPrefixVO == null) {
                strArr = new String[] { p_msisdn };
                throw new BTSLBaseException("PretupsBL", "validateMsisdn", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
            }
            /*
             * 21/04/07 Code Added for MNP
             * Preference to check whether MNP is allowed in system or not.
             * If yes then check whether Number has not been ported out, If yes
             * then throw error, else continue
             */
            if (SystemPreferences.MNP_ALLOWED) {
                boolean numberAllowed = false;
                if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_msisdn, "", PretupsI.PORTED_IN);
                    if (!numberAllowed)
                        throw new BTSLBaseException("PretupsBL", "getNetworkDetails", SelfTopUpErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_msisdn }, null);
                } else {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_msisdn, "", PretupsI.PORTED_OUT);
                    if (numberAllowed)
                        throw new BTSLBaseException("PretupsBL", "getNetworkDetails", SelfTopUpErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_msisdn }, null);
                }
            }
            // 21/04/07: MNP Code End
            p_receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
            p_receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
            p_receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateMsisdn", "  Exception while validating msisdn :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateMsisdn]", "", "", "", "Exception while validating msisdn" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateMsisdn", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("validateMsisdn", p_requestID, "Exiting for p_msisdn= " + p_msisdn);
    }

    public static void getProductFromServiceType(Connection p_con, TransferVO p_transferVO, String p_type, String p_module) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getProductFromServiceType", "Entered p_serviceType:" + p_transferVO.getServiceType() + " p_type=" + p_type + " p_module=" + p_module);
        try {
            String productType = null;
            if (SystemPreferences.SRVC_PROD_MAPPING_ALLOWED.contains(p_type))
                p_transferVO.setSelectorCode(p_transferVO.getSubService());
            else
                p_transferVO.setSelectorCode(PretupsI.DEFAULT_SUBSERVICE);
            ListValueVO listValueVO = NetworkProductServiceTypeCache.getProductServiceValueVO(p_type, p_transferVO.getSelectorCode());
            if (_log.isDebugEnabled())
                _log.debug("getProductFromServiceType", "Got listValueVO=" + listValueVO + " for ServiceType=" + p_type + " p_module=" + p_module);
            if (listValueVO != null) {
                productType = listValueVO.getLabel();
                p_transferVO.setDifferentialAllowedForService(listValueVO.getValue());
                p_transferVO.setGiveOnlineDifferential(listValueVO.getType());
                ProductVO channelProductsVO = NetworkProductCache.getObject(p_module, productType, p_transferVO.getRequestedAmount());
                if (channelProductsVO != null) {
                    p_transferVO.setProductCode(channelProductsVO.getProductCode());
                    p_transferVO.setProductType(productType);
                    p_transferVO.setProductName(channelProductsVO.getShortName());
                    if (channelProductsVO.getProductCategory().equals(PretupsI.PRODUCT_CATEGORY_FLEX)) {
                        p_transferVO.setTransferValue(p_transferVO.getRequestedAmount());
                        p_transferVO.setQuantity(p_transferVO.getRequestedAmount());
                    } else {
                        p_transferVO.setTransferValue(getSystemAmount(1));
                        p_transferVO.setQuantity(getSystemAmount(1));
                    }

                    if (!channelProductsVO.getStatus().equals(PretupsI.PRODUCT_STATUS_ACTIVE))
                        throw new BTSLBaseException("PretupsBL", "getProductFromServiceType", SelfTopUpErrorCodesI.PRODUCT_NOT_AVAILABLE);
                    NetworkProductVO networkProductVO = NetworkProductServiceTypeCache.getNetworkProductDetails(p_transferVO.getNetworkCode(), channelProductsVO.getProductCode());
                    if (networkProductVO != null) {
                        if (networkProductVO.getStatus().equals(PretupsI.NETWORK_PRODUCT_STATUS_SUSPEND)) {
                            // ChangeID=LOCALEMASTER
                            // which language message to be send is determined
                            // from the locale master
                            if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_transferVO.getLocale())).getMessage()))
                                p_transferVO.setSenderReturnMessage(networkProductVO.getLanguage1Message());
                            else
                                p_transferVO.setSenderReturnMessage(networkProductVO.getLanguage2Message());
                            throw new BTSLBaseException("PretupsBL", "getProductFromServiceType", SelfTopUpErrorCodesI.PRODUCT_NETWK_SUSPENDED);
                        } else if (networkProductVO.getStatus().equalsIgnoreCase(PretupsI.NETWORK_PRODUCT_STATUS_DELETE)) {
                            // ChangeID=LOCALEMASTER
                            // which language message to be send is determined
                            // from the locale master
                            if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_transferVO.getLocale())).getMessage()))
                                p_transferVO.setSenderReturnMessage(networkProductVO.getLanguage1Message());
                            else
                                p_transferVO.setSenderReturnMessage(networkProductVO.getLanguage2Message());
                            throw new BTSLBaseException("PretupsBL", "getProductFromServiceType", SelfTopUpErrorCodesI.PRODUCT_NETWK_DELETED);
                        } else if (!((networkProductVO.getUsage().equals(PretupsI.NETWK_PRODUCT_USAGE_BOTH)) || (networkProductVO.getUsage().equals(PretupsI.NETWK_PRODUCT_USAGE_CONSUMPTION))))
                            throw new BTSLBaseException("PretupsBL", "getProductFromServiceType", SelfTopUpErrorCodesI.PRODUCT_NETWK_CONSUM_NOTALLOWED);
                    } else
                        throw new BTSLBaseException("PretupsBL", "getProductFromServiceType", SelfTopUpErrorCodesI.PRODUCT_NOT_ASSOCIATED_WITH_NETWK);
                } else
                    throw new BTSLBaseException("PretupsBL", "getProductFromServiceType", SelfTopUpErrorCodesI.PRODUCT_NOT_FOUND);
            } else {
                _log.error("getProductFromServiceType", "For transfer ID=" + p_transferVO.getTransferID() + " Invalid Service Type request, no product associated with service type=" + p_type);
                throw new BTSLBaseException("PretupsBL", "getProductFromServiceType", SelfTopUpErrorCodesI.ERROR_INVALID_SERTYPE_PRODUCT_NOT_FOUND);
            }
        } catch (BTSLBaseException be) {
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[getProductFromServiceType]",p_transferVO.getTransferID(),p_transferVO.getSenderMsisdn(),p_transferVO.getNetworkCode(),"Network Product Service mapping problem for service type :"+be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getProductFromServiceType]", p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Exception while check product details for service type :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "getProductFromServiceType", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getProductFromServiceType", p_transferVO.getTransferID(), "Exiting for Transfer ID:" + p_transferVO.getTransferID());
        }
    }

    /**
     * Method to get the Service class Object to be used for the service class
     * and Preference
     * 
     * @param p_con
     * @param p_serviceClassCode
     * @param p_preferenceCode
     * @param p_networkCode
     * @param p_module
     * @return Object
     * @throws BTSLBaseException
     */
    public static Object getServiceClassObject(String p_serviceClassCode, String p_preferenceCode, String p_networkCode, String p_module, boolean p_isCheckMoreThanAllReqd, String p_allServiceClassID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getServiceClassObject", "Entered with p_networkCode=" + p_networkCode + " p_module=" + p_module + " p_serviceClassCode=" + p_serviceClassCode + " p_preferenceCode=" + p_preferenceCode + " p_isCheckMoreThanAllReqd=" + p_isCheckMoreThanAllReqd + "p_allServiceClassID=" + p_allServiceClassID);
        Object serviceObjVal = null;
        try {
            // This method is used to get the service class Object to be used
            // for service class preference
            // In this method if p_isCheckMoreThanAllReqd flag is true. Then
            // number of preferences other then all
            // is checked. If this number is greater then 0 i.e. other
            // preferences also defined other then ALL.
            // Then return null. Otherwise that ALL preferences is used.
            // If p_isCheckMoreThanAllReqd flag is false then use the prefeernce
            // code to get the Object.
            // In call to getServicePreference method of PreferenceCache if last
            // parameter is true
            // then find the vlaue at System level other wise just check the
            // value at service level
            if (!BTSLUtil.isNullString(p_serviceClassCode) && p_serviceClassCode.equalsIgnoreCase(p_allServiceClassID)) {
                serviceObjVal = PreferenceCache.getServicePreferenceObject(p_preferenceCode, p_networkCode, p_module, p_serviceClassCode, false);
                if (serviceObjVal != null) {
                    PreferenceCacheVO preferenceCacheVO = (PreferenceCacheVO) serviceObjVal;
                    if (p_isCheckMoreThanAllReqd && preferenceCacheVO.getNoOfOtherPrefOtherThanAll() > 0)
                        return null;
                    else
                        serviceObjVal = PreferenceCache.getServicePreference(p_preferenceCode, p_networkCode, p_module, p_serviceClassCode, true);
                } else if (!p_isCheckMoreThanAllReqd)
                    serviceObjVal = PreferenceCache.getServicePreference(p_preferenceCode, p_networkCode, p_module, p_serviceClassCode, true);
            } else {
                serviceObjVal = PreferenceCache.getServicePreference(p_preferenceCode, p_networkCode, p_module, p_serviceClassCode, false);
                if (serviceObjVal == null) {
                    serviceObjVal = PreferenceCache.getServicePreference(p_preferenceCode, p_networkCode, p_module, p_allServiceClassID, true);
                    return serviceObjVal;
                }
            }
            return serviceObjVal;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getServiceClassObject", "  Exception while getting the Service class object from cache :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getServiceClassObject]", "", "", "", "Exception while getting the Service class object from cache" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "getServiceClassObject", SelfTopUpErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getServiceClassObject", "Exting with serviceObjVal=" + serviceObjVal);
        }
    }

    /**
     * Method to add the crdit bac details in the Items table
     * 
     * @param p_con
     * @param p_transferID
     * @param p_transferItemVO
     * @throws BTSLBaseException
     */
    public static void addTransferCreditBackDetails(Connection p_con, String p_transferID, TransferItemVO p_transferItemVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addTransferCreditBackDetails", p_transferID, "Entered with p_transferItemVO=" + p_transferItemVO);
        try {
            ArrayList itemList = new ArrayList();
            itemList.add(0, p_transferItemVO);
            int updateCount = _transferDAO.addTransferItemDetails(p_con, p_transferID, itemList);
            if (updateCount <= 0)
                throw new BTSLBaseException("PretupsBL", "addTransferCreditBackDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("addTransferCreditBackDetails", "  Exception while making credit back entry :" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[addTransferCreditBackDetails]", p_transferID, "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "addTransferCreditBackDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("addTransferCreditBackDetails", p_transferID, "Exiting ");
    }

    /**
     * Method to validate the service class checks from the code returned
     * 
     * @param p_con
     * @param p_transferItemVO
     * @param p_module
     * @param p_service_Type
     * @throws BTSLBaseException
     */
    public static void validateServiceClassChecks(Connection p_con, TransferItemVO p_transferItemVO, TransferVO _transferVO, String p_module, String p_serviceType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateServiceClassChecks", p_transferItemVO.getTransferID(), "Entered with p_transferItemVO=" + p_transferItemVO + " p_module=" + p_module + " p_serviceType=" + p_serviceType);
        try {
            // ServiceClassDAO serviceClassDAO=new ServiceClassDAO();
            String allowedAccountStatus = null;

            // For get number back service
            String allowedNumberBackServices = null;
            String numberBackStatus = null;
            String[] allowedService;

            OperatorUtilI operatorUtili = null;
            boolean isExist = true;
            String serviceClassString = null;
            String serviceTypeString = null;
            String userTypeString = null;
            ServiceClassVO serviceClassVO = null;

            // HashMap<String, ServiceClassVO> serviceMap=null;
            // serviceMap=serviceClassDAO.loadServiceClassInfoByCodeWithAll(p_con,p_transferItemVO.getServiceClassCode(),p_transferItemVO.getInterfaceID());
            /*
             * if(serviceMap==null)
             * {
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
             * EventStatusI
             * .RAISED,EventLevelI.INFO,"PretupsBL[validateServiceClassChecks]"
             * ,p_transferItemVO
             * .getTransferID(),p_transferItemVO.getMsisdn(),"",
             * "No Service Class defined for "
             * +PretupsI.ALL+" and Interface ID "+
             * p_transferItemVO.getInterfaceID
             * ()+" After searching for Service class Code="
             * +p_transferItemVO.getServiceClassCode());
             * throw new
             * BTSLBaseException("PretupsBL","validateServiceClassChecks"
             * ,PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_NOTFOUND);
             * }
             * else
             * {
             */
            serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(p_transferItemVO.getServiceClassCode(), p_transferItemVO.getInterfaceID());
            if (serviceClassVO == null) {
                serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(PretupsI.ALL, p_transferItemVO.getInterfaceID());
                if (serviceClassVO == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateServiceClassChecks]", p_transferItemVO.getTransferID(), p_transferItemVO.getMsisdn(), "", "No Service Class defined for " + PretupsI.ALL + " and Interface ID " + p_transferItemVO.getInterfaceID() + " After searching for Service class Code=" + p_transferItemVO.getServiceClassCode());
                    throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_INTFCE_SRVCECLSS_NOTFOUND);
                } else
                    p_transferItemVO.setUsingAllServiceClass(true);
            }
            // p_transferItemVO.setUsingAllServiceClass(true);
            // _transferVO.setServiceClassMap(serviceMap);
            // }
            p_transferItemVO.setServiceClassCode(serviceClassVO.getServiceClassCode());
            if (PretupsI.SUSPEND.equals(serviceClassVO.getStatus())) {
                p_transferItemVO.setServiceClass(serviceClassVO.getServiceClassId());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateServiceClassChecks]", p_transferItemVO.getTransferID(), p_transferItemVO.getMsisdn(), "", "Service Class " + serviceClassVO.getServiceClassId() + " is suspended");
                // Chenged by ankit Z on date 2/8/06 to make saperate message
                // for sender and receiver suspend
                if (p_transferItemVO.getUserType().equals(PretupsI.USER_TYPE_SENDER))
                    throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_INTFCE_SRVCECLSS_SUSPEND);
                else
                    throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_INTFCE_SRVCECLSS_SUSPEND_R);
            } else {
                // Change for OCA requirement to disallow some service class but
                // allow some specific numbers of that service class
                try {
                    serviceClassString = new String(Constants.getProperty("CHECK_FOR_SERVICE_CLASSES"));
                    if (_log.isDebugEnabled())
                        _log.debug("validateServiceClassChecks", "serviceClassString=" + serviceClassString);
                } catch (Exception e) {
                    if (_log.isDebugEnabled())
                        _log.debug("validateServiceClassChecks", " No service class has been defined to be checked for accessibility");
                }

                try {
                    serviceTypeString = new String(Constants.getProperty("CHECK_FOR_SERVICE_TYPES"));
                    if (_log.isDebugEnabled())
                        _log.debug("validateServiceClassChecks", "serviceTypeString=" + serviceTypeString);
                } catch (Exception e) {
                    if (_log.isDebugEnabled())
                        _log.debug("validateServiceClassChecks", " No service type has been defined to be checked for accessibility");
                }

                try {
                    userTypeString = new String(Constants.getProperty("CHECK_FOR_USER_TYPES"));
                    if (_log.isDebugEnabled())
                        _log.debug("validateServiceClassChecks", " userTypeString=" + userTypeString);
                } catch (Exception e) {
                    if (_log.isDebugEnabled())
                        _log.debug("validateServiceClassChecks", " No user type has been defined to be checked for accessibility");
                }

                if (!BTSLUtil.isNullString(serviceClassString) && BTSLUtil.isStringContain(serviceClassString, serviceClassVO.getServiceClassId()) && !BTSLUtil.isNullString(serviceTypeString) && BTSLUtil.isStringContain(serviceTypeString, p_serviceType) && !BTSLUtil.isNullString(userTypeString) && BTSLUtil.isStringContain(userTypeString, p_transferItemVO.getUserType())) {
                    try {
                        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
                        operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateServiceClassChecks]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
                    }
                    isExist = operatorUtili.checkMsisdnServiceClassMapping(p_con, p_transferItemVO.getMsisdn(), p_serviceType, serviceClassVO.getServiceClassId(), p_module, p_transferItemVO.getUserType());
                    if (!isExist)
                        throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_SRVCECLSS_SENDER_NOT_ALLOWED);
                }
                // Change block end

                p_transferItemVO.setServiceClass(serviceClassVO.getServiceClassId());
                if (p_transferItemVO.getUserType().equals(PretupsI.USER_TYPE_SENDER)) {
                    if (p_module.equals(PretupsI.P2P_MODULE)) {
                        if (PretupsI.YES.equals(serviceClassVO.getP2pSenderSuspend()))
                            throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_INTFCE_SRVCECLSS_SENDER_SUSPEND);
                        allowedAccountStatus = serviceClassVO.getP2pSenderAllowedStatus();
                    }
                } else {
                    if (p_module.equals(PretupsI.P2P_MODULE)) {
                        if (PretupsI.YES.equals(serviceClassVO.getP2pReceiverSuspend()))
                            throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_INTFCE_SRVCECLSS_P2P_RECEIVER_SUSPEND);
                        allowedAccountStatus = serviceClassVO.getP2pReceiverAllowedStatus();
                    } else {
                        if (PretupsI.YES.equals(serviceClassVO.getC2sReceiverSuspend()))
                            throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_INTFCE_SRVCECLSS_C2S_RECEIVER_SUSPEND);
                        allowedAccountStatus = serviceClassVO.getC2sReceiverAllowedStatus();
                    }
                }
                if (!BTSLUtil.isNullString(allowedAccountStatus) && !PretupsI.ALL.equals(allowedAccountStatus)) {
                    String[] allowedStatus = allowedAccountStatus.split(",");
                    if (_log.isDebugEnabled()) {
                        _log.debug("validateServiceClassChecks", "Received Account Status=" + p_transferItemVO.getAccountStatus());
                        _log.debug("validateServiceClassChecks", "Is null=" + BTSLUtil.isNullString(p_transferItemVO.getAccountStatus()));
                    }
                    if (!BTSLUtil.isNullString(p_transferItemVO.getAccountStatus()) && !Arrays.asList(allowedStatus).contains(p_transferItemVO.getAccountStatus())) {
                        if (_log.isDebugEnabled())
                            _log.debug("validateServiceClassChecks", p_transferItemVO.getTransferID(), "Account Status =" + p_transferItemVO.getAccountStatus() + " is not allowed in the allowed List for interface ID=" + p_transferItemVO.getInterfaceID());

                        // check for allowed services for Number Back
                        allowedNumberBackServices = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.ALWD_NUMBCK_SERVICES, _transferVO.getNetworkCode());
                        allowedService = allowedNumberBackServices.split(",");

                        if (!Arrays.asList(allowedService).contains(_transferVO.getServiceType())) {
                            if (_log.isDebugEnabled())
                                _log.debug("validateServiceClassChecks allowedService:-#####", allowedService + "ServiceType:-#####" + _transferVO.getServiceType() + "Exception");
                            p_transferItemVO.setNumberBackAllowed(false);
                            String[] strArr = new String[] { p_transferItemVO.getMsisdn(), String.valueOf(PretupsBL.getDisplayAmount(p_transferItemVO.getRequestValue())), p_transferItemVO.getAccountStatus() };
                            // throw new
                            // BTSLBaseException("PretupsBL","validateServiceClassChecks",PretupsErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC,0,strArr,null);
                            if (p_transferItemVO.getUserType().equals(PretupsI.USER_TYPE_SENDER))
                                throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_SEN, 0, strArr, null);
                            else
                                throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC, 0, strArr, null);
                        } else {
                            String preferenceKey = _transferVO.getServiceType() + PreferenceI.ALWD_ACC_STATUS_NUMBCK;
                            numberBackStatus = (String) PreferenceCache.getControlPreference(preferenceKey, _transferVO.getNetworkCode(), p_transferItemVO.getInterfaceID());
                            if (BTSLUtil.isNullString(numberBackStatus)) {
                                if (_log.isDebugEnabled())
                                    _log.debug("validateServiceClassChecks numberBackStatus is null:-#####", numberBackStatus);
                                String[] strArr = new String[] { p_transferItemVO.getMsisdn(), String.valueOf(PretupsBL.getDisplayAmount(p_transferItemVO.getRequestValue())), p_transferItemVO.getAccountStatus() };
                                throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC, 0, strArr, null);
                            }
                            if (!numberBackStatus.equals(p_transferItemVO.getAccountStatus())) {
                                if (_log.isDebugEnabled())
                                    _log.debug("validateServiceClassChecks numberBackStatus:-#####", numberBackStatus + "Account Status:-#####" + p_transferItemVO.getAccountStatus() + "Exception");
                                p_transferItemVO.setNumberBackAllowed(false);
                                String[] strArr = new String[] { p_transferItemVO.getMsisdn(), String.valueOf(PretupsBL.getDisplayAmount(p_transferItemVO.getRequestValue())), p_transferItemVO.getAccountStatus() };
                                throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC, 0, strArr, null);
                            }

                            p_transferItemVO.setNumberBackAllowed(true);
                        }

                        if (!p_transferItemVO.isNumberBackAllowed()) {

                            if (p_transferItemVO.getUserType().equals(PretupsI.USER_TYPE_SENDER)) {
                                String[] strArr = new String[] { p_transferItemVO.getMsisdn(), String.valueOf(PretupsBL.getDisplayAmount(p_transferItemVO.getRequestValue())) };
                                throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_SEN, 0, strArr, null);
                            } else {
                                String[] strArr = new String[] { p_transferItemVO.getMsisdn(), String.valueOf(PretupsBL.getDisplayAmount(p_transferItemVO.getRequestValue())), p_transferItemVO.getAccountStatus() };
                                throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC, 0, strArr, null);
                            }
                        }
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _log.error("validateServiceClassChecks", p_transferItemVO.getTransferID(), "  BTSL Exception while getting Service Class ID from Code :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validateServiceClassChecks", "  Exception while getting Service Class ID from Code :" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateServiceClassChecks]", p_transferItemVO.getTransferID(), p_transferItemVO.getMsisdn(), "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateServiceClassChecks", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("validateServiceClassChecks", p_transferItemVO.getTransferID(), "Exiting ");
    }

    /**
     * Load the receiver controls parameters
     * 
     * @param p_con
     * @param p_requestID
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void loadRecieverControlLimits(Connection p_con, String p_requestID, TransferVO p_transferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadRecieverControlLimits", "Eneterd with p_transferVO::=" + p_transferVO);
        ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
        Date currentDate = p_transferVO.getCreatedOn();// for Aktel::AshishS
        if (_log.isDebugEnabled())
            _log.debug("loadRecieverControlLimits", p_requestID, "Entered MSISDN=" + receiverVO.getMsisdn());
        try {
            if (SystemPreferences.MRP_BLOCK_TIME_ALLOWED) {
                boolean isDataFound = _subscriberControlDAO.loadSubscriberControlDetails(p_con, receiverVO);
                if (_log.isDebugEnabled())
                    _log.debug("loadRecieverControlLimits", p_requestID, "MSISDN=" + receiverVO.getMsisdn() + " Data Found=" + isDataFound);

                // added by nilesh: for MRP block time
                receiverVO.setRequestedMRP(p_transferVO.getRequestedAmount());
                receiverVO.setRequestedServiceType(p_transferVO.getServiceType());
                // _subscriberControlDAO.loadSubscriberLastDetails(p_con,
                // receiverVO.getModule(),receiverVO.getMsisdn(),p_transferVO);
                p_transferVO.setLastMRP(receiverVO.getLastMRP());
                p_transferVO.setLastServiceType(receiverVO.getLastServiceType());
                // end
                // Lock the subscriber_controls table if under process check has
                // not be done on channel receiver for sender.
                if (isDataFound) {
                    if (!TypesI.YES.equals(p_transferVO.getUnderProcessCheckReqd())) {
                        _subscriberControlDAO.lockSubscriberControlTable(p_con, (ReceiverVO) p_transferVO.getReceiverVO());
                    }
                }
                if (!isDataFound) {
                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    int addCount = _subscriberControlDAO.addSubscriberControlDetails(p_con, receiverVO);
                    if (addCount <= 0) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[loadRecieverControlLimits]", p_requestID, receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Not able to add in subscriber control table");
                        throw new BTSLBaseException("PretupsBL", "loadRecieverControlLimits", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
                    }
                } else if (receiverVO.getLastTransferStatus().equals(PretupsI.TXN_STATUS_UNDER_PROCESS))// modified
                                                                                                        // for
                                                                                                        // Aktel::AshishS
                {

                    // Changes done by Avinash 20/09/07. This will check that
                    // receiver is underprocess and the time of under process
                    // was more than 5 Min.
                    // if this condition get staisfied make transaction allowed
                    // even if receiver is under process.
                    if (((currentDate.getTime() - receiverVO.getLastTransferOn().getTime()) >= PretupsI.RECEIVER_UNDERPROCESS_UNBLOCK_TIME)) {
                        receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_UNDER_PROCESS);
                        receiverVO.setLastTransferOn(currentDate);
                        int updateCount = _subscriberControlDAO.updateSubscriberControlDetails(p_con, receiverVO);
                        if (updateCount <= 0) {
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[loadRecieverControlLimits]", p_requestID, receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Not able to update subscriber details in transaction control table");
                            throw new BTSLBaseException("PretupsBL", "loadRecieverControlLimits", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
                        }
                    } else {
                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.RECEIVER_LAST_REQ_UNDERPROCESS_R));
                        String[] strArr = new String[] { receiverVO.getMsisdn() };
                        throw new BTSLBaseException("PretupsBL", "loadRecieverControlLimits", SelfTopUpErrorCodesI.RECEIVER_LAST_REQ_UNDERPROCESS_S, 0, strArr, null);
                    }
                } else // Mark under process
                {
                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    receiverVO.setLastTransferOn(currentDate);// for
                                                              // Aktel::AshishS
                    // Here refresh the daily,weekly and monthly thresholds on
                    // change of day, week or month.
                    checkResetCountersAfterPeriodChange(receiverVO, new Date());
                    int updateCount = _subscriberControlDAO.updateSubscriberControlDetails(p_con, receiverVO);
                    if (updateCount <= 0) {
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[loadRecieverControlLimits]", p_requestID, receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Not able to update subscriber details in transaction control table");
                        throw new BTSLBaseException("PretupsBL", "loadRecieverControlLimits", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _log.error("loadRecieverControlLimits", p_requestID, "MSISDN=" + receiverVO.getMsisdn() + " BTSL Exception=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("loadRecieverControlLimits", p_requestID, "MSISDN=" + receiverVO.getMsisdn() + " Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[loadRecieverControlLimits]", p_requestID, receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Not able to get the reciever controls");
            throw new BTSLBaseException("PretupsBL", "loadRecieverControlLimits", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("loadRecieverControlLimits", p_requestID, "Exiting");
    }

    /**
     * Unmarks the last request status for receiver
     * 
     * @param p_con
     * @param p_requestID
     * @param p_receiverVO
     * @throws BTSLBaseException
     */
    public static void unmarkReceiverLastRequest(Connection p_con, String p_requestID, ReceiverVO p_receiverVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("unmarkReceiverLastRequest", p_requestID, "Entered MSISDN=" + p_receiverVO.getMsisdn() + " Restricted Subscriber VO: " + p_receiverVO.getRestrictedSubscriberVO());
        try {
            // code block to update the count and amount of the restricted
            // subscriber
            RestrictedSubscriberVO restrictedSubscriberVO = (RestrictedSubscriberVO) p_receiverVO.getRestrictedSubscriberVO();
            if (restrictedSubscriberVO != null && !BTSLUtil.isNullString(restrictedSubscriberVO.getTempStatus()) && !SelfTopUpErrorCodesI.TXN_STATUS_FAIL.equalsIgnoreCase(restrictedSubscriberVO.getTempStatus())) {
                int updateCount = 0;
                // method call for updating restricted subscriber's details
                updateCount = new RestrictedSubscriberDAO().updateRestrictedSubscriberDetails(p_con, restrictedSubscriberVO);
                /*
                 * //may be used in future, if the excption is thrown,
                 * //rest of code will not execute even if transaction is
                 * successful which should not happen
                 * if(updateCount<=0)
                 * throw new
                 * BTSLBaseException("PretupsBL","unmarkReceiverLastRequest"
                 * ,PretupsErrorCodesI.ERROR_EXCEPTION);
                 */}// end of code block for updating restricted subscriber
                    // details

            p_receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
            if (SystemPreferences.MRP_BLOCK_TIME_ALLOWED) {
                int updateCount = _subscriberControlDAO.updateSubscriberControlDetails(p_con, p_receiverVO);
                if (updateCount <= 0) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[unmarkReceiverLastRequest]", p_requestID, p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), "Not able to update subscriber details in transaction control table");
                    throw new BTSLBaseException("PretupsBL", "unmarkReceiverLastRequest", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
                } else
                    p_receiverVO.setUnmarkRequestStatus(false);
            } else {
                p_receiverVO.setUnmarkRequestStatus(false);
            }
        } catch (BTSLBaseException be) {
            _log.error("unmarkReceiverLastRequest", p_requestID, "MSISDN=" + p_receiverVO.getMsisdn() + " BTSL Exception=" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("unmarkReceiverLastRequest", p_requestID, "MSISDN=" + p_receiverVO.getMsisdn() + " Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[unmarkReceiverLastRequest]", p_requestID, p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "unmarkReceiverLastRequest", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("unmarkReceiverLastRequest", p_requestID, "Exiting");
    }

    /**
     * Check whether the number is present in the Prepaid Number database
     * 
     * @param p_con
     * @param p_msisdn
     * @param p_interfaceType
     * @return ListValueVO: Valid= Interface ID, Label = Handler Class
     * @throws BTSLBaseException
     */
    public static ListValueVO validateNumberInRoutingDatabase(Connection p_con, String p_msisdn, String p_interfaceType) throws BTSLBaseException {
        _log.debug("validateNumberInRoutingDatabase", "Entered with p_msisdn=" + p_msisdn + " p_inrefaceType=" + p_interfaceType);
        ListValueVO listValueVO = null;
        try {
            listValueVO = new RoutingDAO().loadInterfaceID(p_con, p_msisdn, p_interfaceType);
            return listValueVO;

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateNumberInRoutingDatabase]", "", p_msisdn, "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateNumberInRoutingDatabase", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            _log.debug("validateNumberInRoutingDatabase", "Exiting listValueVO=" + listValueVO);
        }

    }

    /**
     * Check whether the number is present in the Postpaid Number database
     * last update for postpaid bill payment date 15/05/06
     * 
     * @param p_con
     * @param p_msisdn
     * @return WhiteListVO
     * @throws BTSLBaseException
     */
    public static WhiteListVO validateNumberInWhiteList(Connection p_con, String p_msisdn) throws BTSLBaseException {
        _log.debug("validateNumberInWhiteList", "Entered with p_msisdn=" + p_msisdn);
        WhiteListVO whiteListVO = null;
        try {
            whiteListVO = new WhiteListDAO().loadInterfaceDetails(p_con, p_msisdn);
            return whiteListVO;
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateNumberInWhiteList]", "", p_msisdn, "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateNumberInWhiteList", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            _log.debug("validateNumberInWhiteList", "Exiting WhiteListVO=" + whiteListVO);
        }
    }

    /**
     * Get Service Keyword Handler Object
     * 
     * @param smsHandler
     * @return
     * @throws BTSLBaseException
     */
    public static java.lang.Object getGatewayHandlerObj(String handlerClassName) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getGatewayHandlerObj", "Entered handlerClassName=" + handlerClassName);
        GatewayParsersI handlerObj = null;
        try {
            handlerObj = (GatewayParsersI) Class.forName(handlerClassName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getGatewayHandlerObj", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getGatewayHandlerObj]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "getGatewayHandlerObj", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("getGatewayHandlerObj", "Exiting");
        return handlerObj;
        // return _smsHandlerObj;
    }// end of getSmsHandlerObj

    /**
     * Method to validate the service class passed in an interface
     * 
     * @param p_con
     * @param p_msisdn
     * @param p_transferID
     * @param p_interfaceID
     * @param p_serviceClassCode
     * @param p_allowedStatus
     * @param p_requestValue
     * @param p_userType
     * @param p_module
     * @return String
     * @throws BTSLBaseException
     */
    public static String validateServiceClass(Connection p_con, String p_msisdn, String p_transferID, String p_interfaceID, String p_serviceClassCode, String p_allowedStatus, long p_requestValue, String p_userType, String p_module) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateServiceClass", p_transferID, "Entered with p_interfaceID=" + p_interfaceID + " p_serviceClassCode=" + p_serviceClassCode + "p_allowedStatus=" + p_allowedStatus + "p_userType=" + p_userType + "p_module=" + p_module);
        String serviceClassID = null;
        String allowedAccountStatus = null;
        try {
            ServiceClassVO serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(p_serviceClassCode, p_interfaceID);
            if (serviceClassVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateServiceClass]", p_transferID, "", "", "No Service Class defined for " + p_serviceClassCode + " and Interface ID " + p_interfaceID);
                throw new BTSLBaseException("PretupsBL", "validateServiceClass", SelfTopUpErrorCodesI.ERROR_INTFCE_SRVCECLSS_NOTFOUND);
            }
            serviceClassID = serviceClassVO.getServiceClassId();

            if (PretupsI.SUSPEND.equals(serviceClassVO.getStatus())) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateServiceClass]", p_transferID, "", "", "Service Class " + serviceClassVO.getServiceClassId() + " is suspended");
                throw new BTSLBaseException("PretupsBL", "validateServiceClass", SelfTopUpErrorCodesI.ERROR_INTFCE_SRVCECLSS_SUSPEND);
            } else {

                if (p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                    if (p_module.equals(PretupsI.P2P_MODULE)) {
                        if (PretupsI.YES.equals(serviceClassVO.getP2pSenderSuspend()))
                            throw new BTSLBaseException("PretupsBL", "validateServiceClass", SelfTopUpErrorCodesI.ERROR_INTFCE_SRVCECLSS_SENDER_SUSPEND);
                        allowedAccountStatus = serviceClassVO.getP2pSenderAllowedStatus();
                    }
                } else {
                    if (p_module.equals(PretupsI.P2P_MODULE)) {
                        if (PretupsI.YES.equals(serviceClassVO.getP2pReceiverSuspend()))
                            throw new BTSLBaseException("PretupsBL", "validateServiceClass", SelfTopUpErrorCodesI.ERROR_INTFCE_SRVCECLSS_P2P_RECEIVER_SUSPEND);
                        allowedAccountStatus = serviceClassVO.getP2pReceiverAllowedStatus();
                    } else {
                        if (PretupsI.YES.equals(serviceClassVO.getC2sReceiverSuspend()))
                            throw new BTSLBaseException("PretupsBL", "validateServiceClass", SelfTopUpErrorCodesI.ERROR_INTFCE_SRVCECLSS_C2S_RECEIVER_SUSPEND);
                        allowedAccountStatus = serviceClassVO.getC2sReceiverAllowedStatus();
                    }
                }
                if (!BTSLUtil.isNullString(allowedAccountStatus) && !PretupsI.ALL.equals(allowedAccountStatus)) {
                    String[] allowedStatus = allowedAccountStatus.split(",");
                    if (!Arrays.asList(allowedStatus).contains(p_allowedStatus)) {
                        if (_log.isDebugEnabled())
                            _log.debug("validateServiceClass", p_transferID, "Account Status =" + p_allowedStatus + " is not allowed in the allowed List for interface ID=" + p_interfaceID);
                        if (p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                            String[] strArr = new String[] { p_msisdn, PretupsBL.getDisplayAmount(p_requestValue) };
                            throw new BTSLBaseException("PretupsBL", "validateServiceClass", SelfTopUpErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_SEN, 0, strArr, null);
                        } else {
                            String[] strArr = new String[] { p_msisdn, PretupsBL.getDisplayAmount(p_requestValue), p_allowedStatus };
                            throw new BTSLBaseException("PretupsBL", "validateServiceClass", SelfTopUpErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC, 0, strArr, null);
                        }
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _log.error("validateServiceClass", p_transferID, "  BTSL Exception while getting Service Class ID from Code :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validateServiceClass", "  Exception while getting Service Class ID from Code :" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateServiceClassChecks]", p_transferID, p_msisdn, "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateServiceClass", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("validateServiceClass", p_transferID, "Exiting with serviceClassID=" + serviceClassID);
        return serviceClassID;
    }

    /**
     * Method to get the Selecor code from value in the request
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public static void getSelectorValueFromCode(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getSelectorValueFromCode", "Entered for Request ID=" + p_requestVO.getRequestIDStr() + ":::p_requestVO.getReqSelector():" + p_requestVO.getReqSelector());
        try {
            // changed for CRE_INT_CR00029 by ankit Zindal
            // This is changed to On-Peak and SMS&Data Credit management
            if (!p_requestVO.isPlainMessage()) {
                if (!BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                    if ("Y".equalsIgnoreCase(Constants.getProperty("STK_CONVERSION_REQD"))) {
                        if (PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
                            p_requestVO.setReqSelector(Constants.getProperty(p_requestVO.getReqSelector()));
                        else {
                            String requestSelectorValue = new CryptoUtil().bytesToBinHex(p_requestVO.getReqSelector().getBytes());
                            if (_log.isDebugEnabled())
                                _log.debug("getSelectorValueFromCode", "Entered for Request ID=" + p_requestVO.getRequestIDStr() + ":::p_requestVO.getReqSelector() Hex Value : " + requestSelectorValue);
                            p_requestVO.setReqSelector(Constants.getProperty(requestSelectorValue));
                        }
                    } else
                        p_requestVO.setReqSelector(Constants.getProperty(p_requestVO.getReqSelector()));
                    if (BTSLUtil.isNullString(p_requestVO.getReqSelector()))
                        throw new BTSLBaseException("PretupsBL", "getSelectorValueFromCode", SelfTopUpErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
                }
            }// else part is added by ankit z on date 2/8/06 to check the
             // validity of selector in case of plain SMS
            else {
                try {
                    if (!BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                        // LookupsCache.getObject(PretupsI.SUB_SERVICES,p_requestVO.getReqSelector());
                        // Changed on 25/05/07 for Service Typoe wise Selector
                        if (!ServiceSelectorMappingCache.getServiceSelectorMap().containsKey(p_requestVO.getServiceType() + "_" + p_requestVO.getReqSelector()))
                            throw new BTSLBaseException("PretupsBL", "getSelectorValueFromCode", SelfTopUpErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
                    }
                } catch (Exception e) {
                    throw new BTSLBaseException("PretupsBL", "getSelectorValueFromCode", SelfTopUpErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
                }
            }
            /*
             * int code=-1;
             * if(p_requestVO.isPlainMessage())
             * {
             * try
             * {
             * code=Integer.parseInt(p_requestVO.getReqSelector());
             * }
             * catch(Exception e)
             * {
             * if(_log.isDebugEnabled())_log.debug("getSelectorValueFromCode",
             * "Invalid Selector Value for Request ID="
             * +p_requestVO.getRequestIDStr());
             * throw new
             * BTSLBaseException("PretupsBL","getSelectorValueFromCode"
             * ,PretupsErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
             * }
             * }
             * else
             * {
             * if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
             * {
             * if(Constants.getProperty("CVG_ENGLISH_CODE").equalsIgnoreCase(
             * p_requestVO.getReqSelector()))
             * code=PretupsI.CHNL_SELECTOR_CVG_VALUE;
             * else
             * if(Constants.getProperty("VG_ENGLISH_CODE").equalsIgnoreCase(
             * p_requestVO.getReqSelector()))
             * code=PretupsI.CHNL_SELECTOR_VG_VALUE;
             * else if(Constants.getProperty("C_ENGLISH_CODE").equalsIgnoreCase(
             * p_requestVO.getReqSelector()))
             * code=PretupsI.CHNL_SELECTOR_C_VALUE;
             * }
             * else
             * {
             * if((Constants.getProperty("CVG_UNICODE_"+p_requestVO.getLocale().
             * getLanguage
             * ().toUpperCase())).equalsIgnoreCase(p_requestVO.getReqSelector
             * ()))
             * code=PretupsI.CHNL_SELECTOR_CVG_VALUE;
             * else
             * if((Constants.getProperty("VG_UNICODE_"+p_requestVO.getLocale
             * ().getLanguage
             * ().toUpperCase())).equalsIgnoreCase(p_requestVO.getReqSelector
             * ()))
             * code=PretupsI.CHNL_SELECTOR_VG_VALUE;
             * else
             * if((Constants.getProperty("V_UNICODE_"+p_requestVO.getLocale(
             * ).getLanguage
             * ().toUpperCase())).equalsIgnoreCase(p_requestVO.getReqSelector
             * ()))
             * code=PretupsI.CHNL_SELECTOR_C_VALUE;
             * 
             * String requestSelectorValue = new
             * CryptoUtil().bytesToBinHex(p_requestVO
             * .getReqSelector().getBytes());
             * if(_log.isDebugEnabled())_log.debug("getSelectorValueFromCode",
             * "Entered for Request ID="+p_requestVO.getRequestIDStr()+
             * ":::p_requestVO.getReqSelector() Hex Value : "
             * +requestSelectorValue
             * +"CVG_UNICODE_"+p_requestVO.getLocale().getLanguage
             * ().toUpperCase());
             * 
             * //if(_log.isDebugEnabled())_log.debug("getSelectorValueFromCode",
             * "CVG_UNICODE_"
             * +p_requestVO.getLocale().getLanguage().toUpperCase());
             * if(_log.isDebugEnabled())_log.debug("getSelectorValueFromCode",
             * Constants
             * .getProperty("CVG_UNICODE_"+p_requestVO.getLocale().getLanguage
             * ().toUpperCase() ));
             * 
             * if( Constants.getProperty("CVG_UNICODE_"+p_requestVO.getLocale().
             * getLanguage().toUpperCase()
             * ).equalsIgnoreCase(requestSelectorValue))
             * code=PretupsI.CHNL_SELECTOR_CVG_VALUE;
             * else
             * if(Constants.getProperty("VG_UNICODE_"+p_requestVO.getLocale(
             * ).getLanguage
             * ().toUpperCase()).equalsIgnoreCase(requestSelectorValue))
             * code=PretupsI.CHNL_SELECTOR_VG_VALUE;
             * else
             * if(Constants.getProperty("C_UNICODE_"+p_requestVO.getLocale()
             * .getLanguage
             * ().toUpperCase()).equalsIgnoreCase(requestSelectorValue))
             * code=PretupsI.CHNL_SELECTOR_C_VALUE;
             * 
             * }
             * }
             * switch(code)
             * {
             * case PretupsI.CHNL_SELECTOR_CVG_VALUE:
             * p_requestVO.setReqSelector(PretupsI.CHNL_SELECTOR_CVG);
             * break;
             * case PretupsI.CHNL_SELECTOR_VG_VALUE:
             * p_requestVO.setReqSelector(PretupsI.CHNL_SELECTOR_VG);
             * break;
             * case PretupsI.CHNL_SELECTOR_C_VALUE:
             * p_requestVO.setReqSelector(PretupsI.CHNL_SELECTOR_C);
             * break;
             * default :
             * {
             * if(_log.isDebugEnabled())_log.debug("getSelectorValueFromCode",
             * "Invalid Selector Value for Request ID="
             * +p_requestVO.getRequestIDStr());
             * throw new
             * BTSLBaseException("PretupsBL","getSelectorValueFromCode"
             * ,PretupsErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
             * }
             * }
             */
        } catch (BTSLBaseException be) {
            _log.error("getSelectorValueFromCode", "BTSL Exception " + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getSelectorValueFromCode", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getSelectorValueFromCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "getSelectorValueFromCode", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("getSelectorValueFromCode", "Exiting with selector=" + p_requestVO.getReqSelector());
    }

    /**
     * Method to get the lang code based on the value passed
     * 
     * @param p_requestVO
     * @param p_langValue
     * @return
     * @throws BTSLBaseException
     */
    public static int getLocaleValueFromCode(RequestVO p_requestVO, String p_langValue) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getLocaleValueFromCode", "Entered for Request ID=" + p_requestVO.getRequestIDStr() + ":::p_langValue:" + p_langValue);
        int code = -1;
        try {
            if (p_requestVO.isPlainMessage()) {
                try {
                    code = Integer.parseInt(p_langValue);
                } catch (Exception e) {
                    if (_log.isDebugEnabled())
                        _log.debug("getLocaleValueFromCode", "Invalid Language Value for Request ID=" + p_requestVO.getRequestIDStr());
                    throw new BTSLBaseException("PretupsBL", "getLocaleValueFromCode", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                }
            } else {
                /*
                 * The following change is done by Ankit Zindal date 11/12/06
                 * Change ID=LOCALEMASTER
                 * This change is done to provide multiple language support for
                 * receiver notification language.
                 * Before this change we are only setting value either 0 or 1
                 * but new we can set any value as defined in constant.props
                 * The value defined in constant.props will be changed for this.
                 * The entry in constant.props will be for example:
                 * English=0
                 * French=1
                 * Malagasy=2
                 * OR
                 * Unicode of english=0
                 * Unicode of french=1
                 * Unicode of malagasy=2
                 * 
                 * Key of entry will be the code that is send by STK and its
                 * value will be the language code that
                 * that is defined in localemaster for that language.
                 */
                /*
                 * if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()
                 * ))
                 * {
                 * if(Constants.getProperty("LANG1_LOCALE_CODE").equalsIgnoreCase
                 * (p_langValue))
                 * code=PretupsI.CHNL_LOCALE_LANG1_VALUE;
                 * else
                 * code=PretupsI.CHNL_LOCALE_LANG2_VALUE;
                 * }
                 * else
                 * {
                 * if((Constants.getProperty("LANG1_UNICODE_"+p_requestVO.getLocale
                 * (
                 * ).getLanguage().toUpperCase())).equalsIgnoreCase(p_langValue)
                 * )
                 * code=PretupsI.CHNL_LOCALE_LANG1_VALUE;
                 * else
                 * code=PretupsI.CHNL_LOCALE_LANG2_VALUE;
                 * }
                 */
                if ("Y".equalsIgnoreCase(Constants.getProperty("STK_CONVERSION_REQD"))) {
                    if (PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
                        code = Integer.parseInt(Constants.getProperty(p_langValue));
                    else {
                        String p_langValue1 = new CryptoUtil().bytesToBinHex(p_langValue.getBytes());
                        if (_log.isDebugEnabled())
                            _log.debug("getLocaleValueFromCode", "Entered for Request ID=" + p_requestVO.getRequestIDStr() + ":::p_langValue. Hex Value : " + p_langValue1);
                        String checkLangvalue = Constants.getProperty(p_langValue1);
                        if (BTSLUtil.isNullString(checkLangvalue))
                            code = 1;
                        else
                            code = Integer.parseInt(checkLangvalue);
                        // code=Integer.parseInt(Constants.getProperty(p_langValue1));
                    }
                } else
                    code = Integer.parseInt(Constants.getProperty(p_langValue));
            }
        } catch (BTSLBaseException be) {
            _log.error("getLocaleValueFromCode", "BTSL Exception " + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getLocaleValueFromCode", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getLocaleValueFromCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "getLocaleValueFromCode", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("getLocaleValueFromCode", "Exiting with code=" + code);
        return code;
    }

    /**
     * Method to update the subscriber interface routing
     * 
     * @param p_interfaceID
     * @param p_externalID
     * @param p_msisdn
     * @param p_subscriberType
     * @param p_modifiedBy
     * @param p_modifiedOn
     * @throws BTSLBaseException
     */
    public static void updateSubscriberInterfaceRouting(String p_interfaceID, String p_externalID, String p_msisdn, String p_subscriberType, String p_modifiedBy, Date p_modifiedOn) throws BTSLBaseException {
        Connection con = null;
        try {
            con = OracleUtil.getConnection();
            updateSubscriberInterfaceRouting(con, p_interfaceID, p_externalID, p_msisdn, p_subscriberType, p_modifiedBy, p_modifiedOn);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
            con = null;
        }
    }

    /**
     * Method to update the subscriber interface routing
     * 
     * @param p_con
     * @param p_interfaceID
     * @param p_externalID
     * @param p_msisdn
     * @param p_subscriberType
     * @param p_modifiedBy
     * @param p_modifiedOn
     * @throws BTSLBaseException
     */
    public static void updateSubscriberInterfaceRouting(Connection p_con, String p_interfaceID, String p_externalID, String p_msisdn, String p_subscriberType, String p_modifiedBy, Date p_modifiedOn) throws BTSLBaseException {
        // Connection con=null;
        try {
            // con=OracleUtil.getConnection();
            RoutingVO routingVO = new RoutingVO();
            routingVO.setInterfaceID(p_interfaceID);
            routingVO.setExternalInterfaceID(p_externalID);
            routingVO.setModifiedBy(p_modifiedBy);
            routingVO.setModifiedOn(p_modifiedOn);
            routingVO.setMsisdn(p_msisdn);
            routingVO.setStatus(PretupsI.YES);
            routingVO.setSubscriberType(p_subscriberType);
            int i = new RoutingDAO().updateSubscriberRoutingInfo(p_con, routingVO);
            if (i < 1)
                throw new BTSLBaseException("PretupsBL", "updateSubscriberInterfaceRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
            p_con.commit();
        } catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
            }
            throw be;
        } catch (Exception e) {
            try {
                p_con.rollback();
            } catch (Exception ex) {
            }
            throw new BTSLBaseException("PretupsBL", "updateSubscriberInterfaceRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }

    }

    /**
     * Method to add insert the subscriber interface routing
     * 
     * @param p_interfaceID
     * @param p_externalID
     * @param p_msisdn
     * @param p_subscriberType
     * @param p_createdBy
     * @param p_createdOn
     * @throws BTSLBaseException
     */
    public static void insertSubscriberInterfaceRouting(String p_interfaceID, String p_externalID, String p_msisdn, String p_subscriberType, String p_createdBy, Date p_createdOn) throws BTSLBaseException {
        Connection con = null;
        try {
            con = OracleUtil.getConnection();
            RoutingVO routingVO = new RoutingVO();
            routingVO.setInterfaceID(p_interfaceID);
            routingVO.setExternalInterfaceID(p_externalID);
            routingVO.setModifiedBy(p_createdBy);
            routingVO.setModifiedOn(p_createdOn);
            routingVO.setMsisdn(p_msisdn);
            routingVO.setSubscriberType(p_subscriberType);
            routingVO.setStatus(PretupsI.YES);
            routingVO.setCreatedBy(p_createdBy);
            routingVO.setCreatedOn(p_createdOn);

            int i = new RoutingDAO().addSubscriberRoutingInfo(con, routingVO);
            if (i < 1)
                throw new BTSLBaseException("PretupsBL", "insertSubscriberInterfaceRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
            con.commit();
        } catch (BTSLBaseException be) {
            try {
                con.rollback();
            } catch (Exception e) {
            }
            throw be;
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception ex) {
            }
            throw new BTSLBaseException("PretupsBL", "insertSubscriberInterfaceRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
            con = null;
        }
    }

    /**
     * Method to delete the subscriber interface routing
     * 
     * @param p_msisdn
     *            String
     * @param p_subscriberType
     *            String
     * @throws BTSLBaseException
     */
    public static void deleteSubscriberInterfaceRouting(String p_msisdn, String p_subscriberType) throws BTSLBaseException {
        Connection con = null;
        try {
            con = OracleUtil.getConnection();
            RoutingVO routingVO = new RoutingVO();
            routingVO.setMsisdn(p_msisdn);
            routingVO.setSubscriberType(p_subscriberType);
            int i = new RoutingDAO().deleteSubscriberRoutingInfo(con, routingVO);
            if (i < 1)
                throw new BTSLBaseException("PretupsBL", "deleteSubscriberInterfaceRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
            con.commit();
        } catch (BTSLBaseException be) {
            try {
                con.rollback();
            } catch (Exception e) {
            }
            throw be;
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception ex) {
            }
            throw new BTSLBaseException("PretupsBL", "deleteSubscriberInterfaceRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
            con = null;
        }
    }

    /**
     * Method getSelectorDescriptionFromCode
     * This method is used to get the description of the passed value of the
     * selector.
     * Here passed value will be the numeric value.
     * 
     * @param p_code
     * @return String
     * @author sandeep.goel
     */
    public static String getSelectorDescriptionFromCode(String p_code) {
        if (_log.isDebugEnabled())
            _log.debug("getSelectorDescriptionFromCode", "Entered p_code=" + p_code);
        String selectorDescription = null;
        try {
            // changed for CRE_INT_CR00029 by ankit Zindal
            // This is changed to On-Peak and SMS&Data Credit management
            /*
             * String selectorValue=null;
             * int code = Integer.parseInt(p_code);
             * switch(code)
             * {
             * case PretupsI.CHNL_SELECTOR_CVG_VALUE:
             * selectorValue=PretupsI.CHNL_SELECTOR_CVG;
             * break;
             * case PretupsI.CHNL_SELECTOR_VG_VALUE:
             * selectorValue=PretupsI.CHNL_SELECTOR_VG;
             * break;
             * case PretupsI.CHNL_SELECTOR_C_VALUE:
             * selectorValue=PretupsI.CHNL_SELECTOR_C;
             * break;
             * 
             * }
             */
            // selectorDescription=((LookupsVO)LookupsCache.getObject(PretupsI.SUB_SERVICES,selectorValue)).getLookupName();
            // selectorDescription=((LookupsVO)LookupsCache.getObject(PretupsI.SUB_SERVICES,p_code)).getLookupName();
            // Changed on 25/05/07 for service Type selector Mapping
            if (!ServiceSelectorMappingCache.getServiceSelectorMap().containsKey(p_code))
                selectorDescription = "N.A.";
            else {
                ServiceSelectorMappingVO serviceSelectorMappingVO = (ServiceSelectorMappingVO) ServiceSelectorMappingCache.getServiceSelectorMap().get(p_code);
                selectorDescription = serviceSelectorMappingVO.getSelectorName();
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getSelectorDescriptionFromCode", "Exception " + e.getMessage());
            selectorDescription = "N.A.";
        }
        if (_log.isDebugEnabled())
            _log.debug("getSelectorDescriptionFromCode", "Exiting with selectorDescription=" + selectorDescription);
        return selectorDescription;
    }

    /**
     * This method is used to get get the message based on service type
     * 
     * @param p_locale
     *            Locale
     * @param p_key
     *            String
     * @param p_args
     *            String[]
     * @param p_serviceType
     *            String
     * 
     * @return String
     */
    public static String getMessage(Locale p_locale, String p_key, String[] p_args, String p_serviceType) {
        String message = null;
        try {
            message = BTSLUtil.getMessage(p_locale, p_key + "_" + p_serviceType, p_args);
            if (BTSLUtil.isNullString(message))
                message = BTSLUtil.getMessage(p_locale, p_key, p_args);
            return message;
        } catch (Exception e) {
            return BTSLUtil.getMessage(p_locale, p_key, p_args);
        }
    }

    /**
     * Method to validate Amount, It checks Amount is not null, It is parsable
     * and Greater than zero
     * It also check the size of the amount field, if p_checkLength is TRUE, it
     * should be less than equal to p_sizeAllowed
     * Length will be checked including decimal
     * 
     * @param p_requestID
     * @param p_requestAmount
     * @param p_checkLength
     * @param p_sizeAllowed
     * @throws BTSLBaseException
     */
    public static void isValidAmount(String p_requestID, String p_requestAmount, boolean p_checkLength, int p_sizeAllowed) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isValidAmount", p_requestID, "Entered p_requestAmount=" + p_requestAmount + " p_checkLength=" + p_checkLength + " p_sizeAllowed=" + p_sizeAllowed);
        String[] strArr = null;
        double requestAmt = 0;
        try {
            if (BTSLUtil.isNullString(p_requestAmount))
                throw new BTSLBaseException("PretupsBL", "isValidAmount", SelfTopUpErrorCodesI.INVALID_AMOUNT_NULL);
            try {
                requestAmt = Double.parseDouble(p_requestAmount.trim());
            } catch (Exception e) {
                strArr = new String[] { p_requestAmount };
                throw new BTSLBaseException("PretupsBL", "isValidAmount", SelfTopUpErrorCodesI.INVALID_AMOUNT_NOTNUMERIC, 0, strArr, null);
            }
            if (requestAmt <= 0) {
                strArr = new String[] { p_requestAmount };
                throw new BTSLBaseException("PretupsBL", "isValidAmount", SelfTopUpErrorCodesI.INVALID_AMOUNT_LESSTHANZERO, 0, strArr, null);
            }
            if (p_checkLength) {
                if (p_requestAmount.trim().length() > p_sizeAllowed) {
                    strArr = new String[] { p_requestAmount, String.valueOf(p_sizeAllowed) };
                    throw new BTSLBaseException("PretupsBL", "isValidAmount", SelfTopUpErrorCodesI.ERROR_INVALID_AMOUNT_PREICISION_NOTALLOWED, 0, strArr, null);
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("isValidAmount", "  Exception while validate amount :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[isValidAmount]", "", "", "", "Exception while validating amount" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "isValidAmount", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("isValidAmount", p_requestID, "Exiting ");
    }

    /**
     * Validate the receiver identification number,
     * New method is added by sourabh for tatasky recharge process where account
     * id has to be validated
     * ChangeID=TATASKYRCHG
     * 
     * @param p_receiverVO
     * @param p_requestID
     * @param p_identificationNumber
     * @throws BTSLBaseException
     *             Added p_con for MNP
     */
    public static void validateReceiverIdentification(Connection p_con, ReceiverVO p_receiverVO, String p_requestID, String p_identificationNumber) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateReceiverIdentification", p_requestID, "Entered for p_identificationNumber= " + p_identificationNumber);
        String[] strArr = null;
        try {
            // Chack if p_identificationNumber is null then throw error
            if (BTSLUtil.isNullString(p_identificationNumber))
                throw new BTSLBaseException("PretupsBL", "validateReceiverIdentification", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_IDENTITY_BLANK);
            // Remove the country code
            p_identificationNumber = getFilteredMSISDN(p_identificationNumber);
            p_identificationNumber = _operatorUtil.addRemoveDigitsFromMSISDN(p_identificationNumber);

            // Check the length of p_identificationNumber with defined range of
            // system preference
            if ((p_identificationNumber.length() < SystemPreferences.MIN_IDENTIFICATION_NUMBER_LENGTH || p_identificationNumber.length() > SystemPreferences.MAX_IDENTIFICATION_NUMBER_LENGTH)) {
                if (SystemPreferences.MIN_IDENTIFICATION_NUMBER_LENGTH != SystemPreferences.MAX_IDENTIFICATION_NUMBER_LENGTH) {
                    strArr = new String[] { p_identificationNumber, String.valueOf(SystemPreferences.MIN_IDENTIFICATION_NUMBER_LENGTH), String.valueOf(SystemPreferences.MAX_IDENTIFICATION_NUMBER_LENGTH) };
                    throw new BTSLBaseException("PretupsBL", "validateReceiverIdentification", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_IDENTITY_NUM_NOTINRANGE, 0, strArr, null);
                } else {
                    strArr = new String[] { p_identificationNumber, String.valueOf(SystemPreferences.MIN_IDENTIFICATION_NUMBER_LENGTH) };
                    throw new BTSLBaseException("PretupsBL", "validateReceiverIdentification", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_NOTIF_NUM_LEN_NOTSAME, 0, strArr, null);
                }
            }
            if (PretupsI.MSISDN_VALIDATION.equals(SystemPreferences.IDENTIFICATION_NUMBER_VAL_TYPE) || !SystemPreferences.ALPHA_ID_NUM_ALLOWED) {
                try {
                    long lng = Long.parseLong(p_identificationNumber);
                } catch (Exception e) {
                    strArr = new String[] { p_identificationNumber };
                    throw new BTSLBaseException("PretupsBL", "validateReceiverIdentification", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_ID_NOTNUMERIC, 0, strArr, null);
                }
            }
            p_receiverVO.setMsisdn(p_identificationNumber);
            // Find the network prefix for the account id
            NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(p_identificationNumber, PretupsI.USER_TYPE_RECEIVER);
            // If network prefix is null then throw error
            if (networkPrefixVO == null) {
                strArr = new String[] { p_identificationNumber };
                throw new BTSLBaseException("PretupsBL", "validateReceiverIdentification", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_NOTIFPREFIX_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
            }

            /*
             * 21/04/07 Code Added for MNP
             * Preference to check whether MNP is allowed in system or not.
             * If yes then check whether Number has not been ported out, If yes
             * then throw error, else continue
             */
            if (SystemPreferences.MNP_ALLOWED) {
                boolean numberAllowed = false;
                if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_identificationNumber, "", PretupsI.PORTED_IN);
                    if (!numberAllowed)
                        throw new BTSLBaseException("PretupsBL", "validateReceiverIdentification", SelfTopUpErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_identificationNumber }, null);
                } else {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_identificationNumber, "", PretupsI.PORTED_OUT);
                    if (numberAllowed)
                        throw new BTSLBaseException("PretupsBL", "validateReceiverIdentification", SelfTopUpErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_identificationNumber }, null);
                }
            }
            // 21/04/07: MNP Code End

            if (_log.isDebugEnabled())
                _log.debug("validateReceiverIdentification", p_requestID, "p_identificationNumber after filtering= " + p_identificationNumber + " length=" + p_identificationNumber.length() + " Min length in preference=" + SystemPreferences.MIN_IDENTIFICATION_NUMBER_LENGTH + " Max length in prefeernce=" + SystemPreferences.MAX_IDENTIFICATION_NUMBER_LENGTH + "network code of receiver= " + networkPrefixVO.getNetworkCode());
            p_receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
            p_receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
            p_receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateReceiverIdentification", "  Exception while validating identificationNumber :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateReceiverIdentification]", p_requestID, "", "", "Exception while validating identificationNumber" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateReceiverIdentification", SelfTopUpErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("validateReceiverIdentification", p_requestID, "Exiting for p_identificationNumber= " + p_identificationNumber);
    }

    /**
     * Validate the receiver identification number
     * This method will remove the prefix from the identification number based
     * on type of validation we will perform
     * If MSISDN type validation is done then MSISDN prefix is removed
     * If OTHER type validation is done then OTHER prefix is removed
     * IF BOTH type validation is done then both prefixes are removed first
     * MSISDN prefix is removed and then OTHER type prefix is removed.
     * 
     * Change ID=ACCOUNTID
     * 
     * @param p_identificationNumber
     * @return
     * @throws BTSLBaseException
     */
    public static String getFilteredIdentificationNumber(String p_identificationNumber) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getFilteredIdentificationNumber", "Entered p_identificationNumber:" + p_identificationNumber);
        String filteredIdentificationNumber = null;
        try {
            // Get which type of validation has to be done i.e. either MSISDN or
            // ACCOUNTID or BOTH type of validation has to be performed
            String validationType = SystemPreferences.IDENTIFICATION_NUMBER_VAL_TYPE;
            if (PretupsI.MSISDN_VALIDATION.equals(validationType))
                filteredIdentificationNumber = removePrefixFromIdentificationNum(SystemPreferences.MIN_MSISDN_LENGTH, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST), p_identificationNumber);
            else if (PretupsI.OTHER_VALIDATION.equals(validationType))
                filteredIdentificationNumber = removePrefixFromIdentificationNum(SystemPreferences.MIN_IDENTIFICATION_NUMBER_LENGTH, SystemPreferences.OTHERID_PREFIX_LIST, p_identificationNumber);
            else if (PretupsI.BOTH_VALIDATION.equals(validationType)) {
                filteredIdentificationNumber = removePrefixFromIdentificationNum(SystemPreferences.MIN_MSISDN_LENGTH, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST), p_identificationNumber);
                filteredIdentificationNumber = removePrefixFromIdentificationNum(SystemPreferences.MIN_IDENTIFICATION_NUMBER_LENGTH, SystemPreferences.OTHERID_PREFIX_LIST, filteredIdentificationNumber);
            }
        } catch (Exception e) {
            _log.error("getFilteredIdentificationNumber", "Exception while getting the identification number from passed no=" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getFilteredIdentificationNumber]", "", p_identificationNumber, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "getFilteredIdentificationNumber", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getFilteredIdentificationNumber", "Exiting Filtered identification number=" + filteredIdentificationNumber);
        }
        return filteredIdentificationNumber;
    }

    /**
     * Remove the given prefix from the given identification number
     * 
     * Change ID=ACCOUNTID
     * 
     * @param p_minLength
     * @param p_prefixList
     * @param p_identificationNumber
     * @return
     */
    private static String removePrefixFromIdentificationNum(int p_minLength, String p_prefixList, String p_identificationNumber) {
        if (_log.isDebugEnabled())
            _log.debug("removePrefixFromIdentificationNum", "Entered p_minLength=" + p_minLength + " p_prefixList=" + p_prefixList + " p_identificationNumber=" + p_identificationNumber);
        StringTokenizer strTok = new StringTokenizer(p_prefixList, ",");
        String prefix = null;
        boolean prefixFound = false;
        if (p_identificationNumber.length() > p_minLength) {
            while (strTok.hasMoreTokens()) {
                prefix = strTok.nextToken();
                if (p_identificationNumber.startsWith(prefix, 0)) {
                    prefixFound = true;
                    break;
                }
            }
            if (prefixFound)
                return p_identificationNumber.substring(prefix.length());
            return p_identificationNumber;
        }
        return p_identificationNumber;
    }

    /**
     * 21/04/07 Code Added for MNP
     * Preference to check whether MNP is allowed in system or not.
     * If yes then check whether Number has not been ported out, If yes then
     * throw error, else continue
     * 
     * @param p_con
     * @param p_requestID
     * @param p_msisdn
     * @param p_networkPrefixVO
     * @throws BTSLBaseException
     */
    public static void checkNumberPortability(Connection p_con, String p_requestID, String p_msisdn, NetworkPrefixVO p_networkPrefixVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkNumberPortability", "Entered with p_requestID=" + p_requestID + "p_msisdn=" + p_msisdn);
        try {
            if (SystemPreferences.MNP_ALLOWED) {
                boolean numberAllowed = false;
                if (p_networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_msisdn, "", PretupsI.PORTED_IN);
                    if (!numberAllowed)
                        throw new BTSLBaseException("PretupsBL", "getNetworkDetails", SelfTopUpErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_msisdn }, null);
                } else {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_msisdn, "", PretupsI.PORTED_OUT);
                    if (numberAllowed)
                        throw new BTSLBaseException("PretupsBL", "getNetworkDetails", SelfTopUpErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_msisdn }, null);
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("checkNumberPortability", "Exception while checking for MNP for MSISDN=" + p_msisdn);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[checkNumberPortability]", "", p_requestID, "", "Exception while checking for MNP for MSISDN=" + p_msisdn + " Getting=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "checkNumberPortability", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("checkNumberPortability", "Exiting ");
    }

    public static boolean checkResetCountersAfterPeriodChange(ReceiverVO p_receiverVO, java.util.Date p_newDate) {
        if (_log.isDebugEnabled())
            _log.debug("checkResetCountersAfterPeriodChange", "Entered p_receiverVO:" + p_receiverVO);
        boolean isCounterChange = false;
        boolean isDayCounterChange = false;
        boolean isWeekCounterChange = false;
        boolean isMonthCounterChange = false;
        Date previousDate = p_receiverVO.getLastSuccessOn();
        if (previousDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(p_newDate);
            int presentDay = cal.get(Calendar.DAY_OF_MONTH);
            int presentWeek = cal.get(Calendar.WEEK_OF_MONTH);
            int presentMonth = cal.get(Calendar.MONTH);
            int presentYear = cal.get(Calendar.YEAR);
            cal.setTime(previousDate);
            int lastWeek = cal.get(Calendar.WEEK_OF_MONTH);
            int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
            int lastTrxMonth = cal.get(Calendar.MONTH);
            int lastTrxYear = cal.get(Calendar.YEAR);
            if (presentDay != lastTrxDay)
                isDayCounterChange = true;
            if (presentWeek != lastWeek)
                isWeekCounterChange = true;
            if (presentMonth != lastTrxMonth) {
                isDayCounterChange = true;
                isWeekCounterChange = true;
                isMonthCounterChange = true;
            }
            if (presentYear != lastTrxYear) {
                isDayCounterChange = true;
                isWeekCounterChange = true;
                isMonthCounterChange = true;
            }
            if (isDayCounterChange) {
                p_receiverVO.setPrevDaySuccCount(p_receiverVO.getTotalSuccessCount());
                p_receiverVO.setTotalSuccessCount(0);
                p_receiverVO.setPrevDayTrasferAmount(p_receiverVO.getTotalTransferAmount());
                p_receiverVO.setTotalTransferAmount(0);
                p_receiverVO.setTotalFailCount(0);// Confirm for this
                isCounterChange = true;
            }
            if (isWeekCounterChange) {
                p_receiverVO.setPrevWeekSuccCount(p_receiverVO.getWeeklySuccCount());
                p_receiverVO.setWeeklySuccCount(0);
                p_receiverVO.setPrevWeekTransferAmount(p_receiverVO.getWeeklyTransferAmount());
                p_receiverVO.setWeeklyTransferAmount(0);
                isCounterChange = true;
            }
            if (isMonthCounterChange) {
                p_receiverVO.setPrevMonthSuccCount(p_receiverVO.getMonthlySuccCount());
                p_receiverVO.setMonthlySuccCount(0);
                p_receiverVO.setPrevMonthTransferAmount(p_receiverVO.getMonthlyTransferAmount());
                p_receiverVO.setMonthlyTransferAmount(0);
                isCounterChange = true;
            }
        } else
            isCounterChange = true;
        if (_log.isDebugEnabled())
            _log.debug("checkResetCountersAfterPeriodChange", "Exiting ");
        return isCounterChange;
    }

    /**
     * isPromotionalRuleExistInRange method is used to check that the rule is
     * exist in defined date or time range or not..
     * 
     * @param con
     * @param transferRulesVO
     * @param p_transferVO
     *            .getTransferDateTime()
     * @return
     * @throws BTSLBaseException
     */

    public static boolean isPromotionalRuleExistInRange(TransferRulesVO p_transferRulesVO, Date transferDate) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isPromotionalRuleExistInRange", "Entered transferRulesVO:" + p_transferRulesVO.toString() + ",transferDate=" + transferDate + ",p_transferRulesVO.getMultipleSlab()=" + p_transferRulesVO.getMultipleSlab() + ",p_transferRulesVO.getSelectRangeType()=" + p_transferRulesVO.getSelectRangeType());
        boolean isExist = false;
        Date fromDate = null;
        Date tillDate = null;

        String currentTime = "";
        int currDay = -1;
        int allwdDay = -1;
        String[] strArr = null;
        boolean isAllowedDay = false;
        long fromTimeHour = 0, tillTimeHour = 0, currentTimeHour = 0;
        String format = Constants.getProperty("PROMOTIONAL_TRANSFER_DATE_FORMAT");
        try {
            currDay = transferDate.getDay();
            if (!(SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW)) {
                if (p_transferRulesVO.getAllowedDays() != null) {
                    strArr = p_transferRulesVO.getAllowedDays().split(",");
                    for (int i = 0; i < strArr.length; i++) {
                        allwdDay = Integer.parseInt(strArr[i]);
                        if (allwdDay == currDay) {
                            isAllowedDay = true;
                            break;
                        }
                    }
                } else {
                    _log.debug("isPromotionalRuleExistInRange", "Allowed days:" + p_transferRulesVO.getAllowedDays() + "isExist:" + isExist);
                    return isExist;
                }
            } else {
                isAllowedDay = true;
            }
            if (isAllowedDay) {
                if (p_transferRulesVO.getMultipleSlab() != null) {
                    String[] timeSlabArr = p_transferRulesVO.getMultipleSlab().split(",");
                    for (int x = 0; x < timeSlabArr.length; x++) {
                        String[] slab = timeSlabArr[x].split("-");
                        String fromTime = slab[0];
                        String tillTime = slab[1];
                        fromDate = BTSLUtil.getSQLDateFromUtilDate(p_transferRulesVO.getStartTime());
                        tillDate = BTSLUtil.getSQLDateFromUtilDate(p_transferRulesVO.getEndTime());
                        fromDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(fromDate) + " " + fromTime, format);
                        tillDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(tillDate) + " " + tillTime, format);
                        if ("Y".equalsIgnoreCase(p_transferRulesVO.getSelectRangeType())) {
                            if (transferDate.after(fromDate) && transferDate.before(tillDate)) {
                                isExist = true;
                                break;
                            } else
                                isExist = false;
                        } else if ("N".equalsIgnoreCase(p_transferRulesVO.getSelectRangeType())) {
                            if (transferDate.after(fromDate) && transferDate.before(tillDate)) {
                                fromTimeHour = fromDate.getTime();
                                tillTimeHour = tillDate.getTime();
                                currentTimeHour = transferDate.getTime();
                                if ((currentTimeHour >= fromTimeHour && currentTimeHour <= tillTimeHour)) {
                                    isExist = true;
                                    break;
                                }

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            _log.error("isPromotionalRuleExistInRange", "Exception:" + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[isPromotionalRuleExistInRange]","",filteredMSISDN,networkCode,"Exception:"+e.getMessage());
            throw new BTSLBaseException("PretupsBL", "isPromotionalRuleExistInRange", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("isPromotionalRuleExistInRange", "Exiting isExist:" + isExist);
        }

        return isExist;

    }

    /**
     * /**
     * Validates the amount
     * 
     * @param p_p2pTransferVO
     * @param p_requestAmount
     * @throws BTSLBaseException
     */
    public static void validateAmountViaSMS(P2PTransferVO p_p2pTransferVO, String p_requestAmount) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateAmountViaSMS", p_p2pTransferVO.getRequestID(), "Entered p_requestAmount=" + p_requestAmount);
        String[] strArr = null;
        double requestAmt = 0;
        String msgRequestAmount = null;
        try {
            if (BTSLUtil.isNullString(p_requestAmount))
                throw new BTSLBaseException("PretupsBL", "validateAmountViaSMS", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_AMT_BLANK);
            msgRequestAmount = p_requestAmount;
            try {
                requestAmt = Double.parseDouble(p_requestAmount);
            } catch (Exception e) {
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", "validateAmountViaSMS", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_AMT_NOTNUMERIC, 0, strArr, null);
            }
            if (requestAmt <= 0) {
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", "validateAmountViaSMS", SelfTopUpErrorCodesI.CHNL_ERROR_RECR_AMT_LESSTHANZERO, 0, strArr, null);
            }

            p_p2pTransferVO.setTransferValue(getSystemAmount(p_requestAmount));
            p_p2pTransferVO.setRequestedAmount(getSystemAmount(p_requestAmount));

            Object objVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.P2P_MINTRNSFR_AMOUNT, p_p2pTransferVO.getNetworkCode());
            if (objVal != null) {
                if (p_p2pTransferVO.getRequestedAmount() < ((Long) objVal).longValue()) {
                    strArr = new String[] { msgRequestAmount, PretupsBL.getDisplayAmount(((Long) objVal).longValue()) };
                    throw new BTSLBaseException("PretupsBL", "validateAmountViaSMS", SelfTopUpErrorCodesI.P2P_SNDR_MIN_TRANS_AMT_LESS, 0, strArr, null);
                }
            }
            objVal = null;
            objVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.P2P_MAXTRNSFR_AMOUNT, p_p2pTransferVO.getNetworkCode());
            if (objVal != null) {
                if (p_p2pTransferVO.getRequestedAmount() > ((Long) objVal).longValue()) {
                    strArr = new String[] { msgRequestAmount, PretupsBL.getDisplayAmount(((Long) objVal).longValue()) };
                    throw new BTSLBaseException("PretupsBL", "validateAmountViaSMS", SelfTopUpErrorCodesI.P2P_SNDR_MAX_TRANS_AMT_MORE, 0, strArr, null);
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateAmountViaSMS", "  Exception while validate amount :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateAmountViaSMS]", "", "", "", "Exception while validating amount" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateAmountViaSMS", SelfTopUpErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("validateAmountViaSMS", p_p2pTransferVO.getRequestID(), "Exiting ");
    }

    public static void updateP2PSubscriberDetail(SenderVO p_SenderVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateP2PSubscriberDetail", "Entered TransferItem VO =" + p_SenderVO.toString());
        Connection con = null;
        try {
            con = OracleUtil.getConnection();
            SubscriberDAO subscriberDAO = new SubscriberDAO();
            int i = subscriberDAO.updateSubscriberDetailsByMSISDN(con, p_SenderVO);
            if (i > 0)
                con.commit();
            else
                con.rollback();
        } catch (BTSLBaseException be) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e) {
            }
            throw be;
        } catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e1) {
            }
            e.printStackTrace();
            _log.error("updateP2PSubscriberDetail", "  Exception while updating p2p Subscriber :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[updateP2PSubscriberDetail]", "", "", "", "Exception while updating p2p Subscriber" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "updateP2PSubscriberDetail", SelfTopUpErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateP2PSubscriberDetail", p_SenderVO.toString(), "Exiting ");
        }

    }

    /**
     * Method to update the subscriber interface routing
     * 
     * @param p_interfaceID
     * @param p_externalID
     * @param p_msisdn
     * @param p_subscriberType
     * @param p_modifiedBy
     * @param p_modifiedOn
     * @throws BTSLBaseException
     */
    public static void updateSubscriberInterfaceAilternateRouting(String p_interfaceID, String p_externalID, String p_msisdn, String p_subscriberType, String p_modifiedBy, Date p_modifiedOn) throws BTSLBaseException {
        Connection con = null;
        try {
            con = OracleUtil.getConnection();
            RoutingVO routingVO = new RoutingVO();
            routingVO.setInterfaceID(p_interfaceID);
            routingVO.setExternalInterfaceID(p_externalID);
            routingVO.setModifiedBy(p_modifiedBy);
            routingVO.setModifiedOn(p_modifiedOn);
            routingVO.setMsisdn(p_msisdn);
            routingVO.setStatus(PretupsI.YES);
            routingVO.setSubscriberType(p_subscriberType);
            int i = new RoutingDAO().updateSubscriberAilternateRoutingInfo(con, routingVO);
            if (i < 1)
                throw new BTSLBaseException("PretupsBL", "updateSubscriberInterfaceAilternateRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
            con.commit();
        } catch (BTSLBaseException be) {
            try {
                con.rollback();
            } catch (Exception e) {
            }
            throw be;
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception ex) {
            }
            throw new BTSLBaseException("PretupsBL", "updateSubscriberInterfaceAilternateRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
            con = null;
        }
    }

    /**
     * Method to add insert the subscriber interface routing
     * 
     * @param p_con
     * @param p_interfaceID
     * @param p_externalID
     * @param p_msisdn
     * @param p_subscriberType
     * @param p_createdBy
     * @param p_createdOn
     * @throws BTSLBaseException
     */
    public static void insertSubscriberInterfaceRouting(Connection p_con, String p_interfaceID, String p_externalID, String p_msisdn, String p_subscriberType, String p_createdBy, Date p_createdOn) throws BTSLBaseException {
        try {
            // p_con=OracleUtil.getConnection();
            RoutingVO routingVO = new RoutingVO();
            routingVO.setInterfaceID(p_interfaceID);
            routingVO.setExternalInterfaceID(p_externalID);
            routingVO.setModifiedBy(p_createdBy);
            routingVO.setModifiedOn(p_createdOn);
            routingVO.setMsisdn(p_msisdn);
            routingVO.setSubscriberType(p_subscriberType);
            routingVO.setStatus(PretupsI.YES);
            routingVO.setCreatedBy(p_createdBy);
            routingVO.setCreatedOn(p_createdOn);

            int i = new RoutingDAO().addSubscriberRoutingInfo(p_con, routingVO);
            if (i < 1)
                throw new BTSLBaseException("PretupsBL", "insertSubscriberInterfaceRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
            // p_con.commit();
        } catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
            }
            throw be;
        } catch (Exception e) {
            try {
                p_con.rollback();
            } catch (Exception ex) {
            }
            throw new BTSLBaseException("PretupsBL", "insertSubscriberInterfaceRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {

        }
    }

    /**
     * Method is called to get object of handler/controller for single db
     * connection changes
     * 
     * @param String
     * @return Object
     * @throws BTSLBaseException
     */
    public static java.lang.Object getServiceKeywordHandlerObjSc(String handlerClassName) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getServiceKeywordHandlerObjSc", "Entered handlerClassName=" + handlerClassName);
        ServiceKeywordUtil handlerObj = null;
        try {
            handlerObj = (ServiceKeywordUtil) Class.forName(handlerClassName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getServiceKeywordHandlerObjSc", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getServiceKeywordHandlerObjSc]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "getServiceKeywordHandlerObjSc", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("getServiceKeywordHandlerObjSc", "Exiting");
        return handlerObj;
        // return _smsHandlerObj;
    }// end of getSmsHandlerObj

    /**
     * Method to delete the subscriber interface routing
     * 
     * @param p_msisdn
     *            String
     * @param p_subscriberType
     *            String
     * @throws BTSLBaseException
     */
    public static void deleteSubscriberInterfaceRouting(Connection p_con, String p_msisdn, String p_subscriberType) throws BTSLBaseException {
        try {
            RoutingVO routingVO = new RoutingVO();
            routingVO.setMsisdn(p_msisdn);
            routingVO.setSubscriberType(p_subscriberType);
            int i = new RoutingDAO().deleteSubscriberRoutingInfo(p_con, routingVO);
            if (i < 1)
                throw new BTSLBaseException("PretupsBL", "deleteSubscriberInterfaceRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
            p_con.commit();
        } catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
            }
            throw be;
        } catch (Exception e) {
            try {
                p_con.rollback();
            } catch (Exception ex) {
            }
            throw new BTSLBaseException("PretupsBL", "deleteSubscriberInterfaceRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }
    }

    /**
     * Method to update the subscriber interface routing
     * 
     * @param p_interfaceID
     * @param p_externalID
     * @param p_msisdn
     * @param p_subscriberType
     * @param p_modifiedBy
     * @param p_modifiedOn
     * @throws BTSLBaseException
     */
    public static void updateSubscriberInterfaceAilternateRouting(Connection p_con, String p_interfaceID, String p_externalID, String p_msisdn, String p_subscriberType, String p_modifiedBy, Date p_modifiedOn) throws BTSLBaseException {
        try {
            RoutingVO routingVO = new RoutingVO();
            routingVO.setInterfaceID(p_interfaceID);
            routingVO.setExternalInterfaceID(p_externalID);
            routingVO.setModifiedBy(p_modifiedBy);
            routingVO.setModifiedOn(p_modifiedOn);
            routingVO.setMsisdn(p_msisdn);
            routingVO.setStatus(PretupsI.YES);
            routingVO.setSubscriberType(p_subscriberType);
            int i = new RoutingDAO().updateSubscriberAilternateRoutingInfo(p_con, routingVO);
            if (i < 1)
                throw new BTSLBaseException("PretupsBL", "updateSubscriberInterfaceAilternateRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
            p_con.commit();
        } catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
            }
            throw be;
        } catch (Exception e) {
            try {
                p_con.rollback();
            } catch (Exception ex) {
            }
            throw new BTSLBaseException("PretupsBL", "updateSubscriberInterfaceAilternateRouting", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }
    }

    public static void updateP2PSubscriberDetail(Connection con, SenderVO p_SenderVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateP2PSubscriberDetail", "Entered TransferItem VO =" + p_SenderVO.toString());
        try {
            SubscriberDAO subscriberDAO = new SubscriberDAO();
            int i = subscriberDAO.updateSubscriberDetailsByMSISDN(con, p_SenderVO);
            if (i > 0)
                con.commit();
            else
                con.rollback();
        } catch (BTSLBaseException be) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e) {
            }
            throw be;
        } catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e1) {
            }
            e.printStackTrace();
            _log.error("updateP2PSubscriberDetail", "  Exception while updating p2p Subscriber :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[updateP2PSubscriberDetail]", "", "", "", "Exception while updating p2p Subscriber" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "updateP2PSubscriberDetail", SelfTopUpErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("updateP2PSubscriberDetail", p_SenderVO.toString(), "Exiting ");
        }

    }

    /**
     * @param allowedDays
     * @param allowedSeries
     * @param deniedSeries
     * @return boolean
     * @author rahul.dutt
     *         this method is used to check whether promotional transfer
     *         rule(prefix based) is applicable for subscriber msisdn
     */
    private static boolean validatePromotionPrefixes(Date p_transfer_date, long p_recPrfxID, String p_allowedDays, String p_allowedSeries, String p_deniedSeries) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validatePromotionPrefixes", "Entered p_transfer_date:" + p_transfer_date + "p_recPrfxID:" + p_recPrfxID + " p_allowedDays:" + p_allowedDays + " p_allowedSeries:" + p_allowedSeries + " p_deniedSeries:" + p_deniedSeries);
        boolean isValidPrefix = false;
        String[] strArr = null;// this array used to store allowed days,allowed
                               // prefixes,denied prefixes
        String prxID = null;
        int currDay = -1;
        // 1. check for whether allowed days is applicable(0,1,2 i.e.
        // 0=sun,1=mon,2=tue)
        // 2. whether subscriber prefix(series) is applicable for this promotion
        try {
            // check for condn 1
            currDay = p_transfer_date.getDay();
            strArr = p_allowedDays.split(",");
            int allwdDay = -1;
            for (int i = 0; i < strArr.length; i++) {
                allwdDay = Integer.parseInt(strArr[i]);
                if (allwdDay == currDay) {
                    isValidPrefix = true;
                    break;
                }
            }
            if (!isValidPrefix)
                return isValidPrefix;// if allowed_days doesnt match with
                                     // current day then return false
                                     // as promotional rule is not applicable
            else
                isValidPrefix = false;// reset the value to check again
            // checking for 2
            // series(prefixID) will come in "," seperated
            if (!BTSLUtil.isNullString(p_allowedSeries)) {
                strArr = p_allowedSeries.split(",");
                for (int i = 0; i < strArr.length; i++) {
                    prxID = strArr[i].trim();
                    if (prxID.equals(Long.toString(p_recPrfxID))) {
                        isValidPrefix = true;
                        break;
                    }
                }
            } else {
                isValidPrefix = true;
                strArr = p_deniedSeries.split(",");
                for (int i = 0; i < strArr.length; i++) {
                    prxID = strArr[i].trim();
                    if (prxID.equals(Long.toString(p_recPrfxID))) {
                        isValidPrefix = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            isValidPrefix = false;
            _log.error("validatePromotionPrefixes", "Exception e:" + e);
            // ------------do we need to throw exception here.
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validatePromotionPrefixes", "Exiting isValidPrefix:" + isValidPrefix);
        }
        return isValidPrefix;
    }

    public static String getDisplayAmount(double p_amount) {
        // if(_log.isDebugEnabled())_log.debug("getDisplayAmount","Entered p_amount:"+p_amount);
        int multiplicationFactor = SystemPreferences.AMOUNT_MULT_FACTOR;
        double amount = p_amount / multiplicationFactor;
        String amountStr = new DecimalFormat("#############.###").format(amount);
        try {
            long l = Long.parseLong(amountStr);
            amountStr = String.valueOf(l);
        } catch (Exception e) {
            amountStr = new DecimalFormat("############0.00#").format(amount);
        }
        // if(_log.isDebugEnabled())_log.debug("getDisplayAmount","Exiting display amount:"+amountStr);
        return amountStr;
    }

    public static long validateMCDListAmount(String p_requestAmount) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("validateMCDListAmount", "Entered p_requestAmount=" + p_requestAmount);
        String[] strArr = null;
        long requestAmt = 0;
        String msgRequestAmount = null;
        try {
            if (BTSLUtil.isNullString(p_requestAmount))
                throw new BTSLBaseException("PretupsBL", "validateMCDListAmount", SelfTopUpErrorCodesI.P2P_MCDL_AMT_BLANK);

            try {
                requestAmt = Long.parseLong(p_requestAmount);
            } catch (Exception e) {
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", "validateMCDListAmount", SelfTopUpErrorCodesI.P2P_MCDL_AMT_NOTNUMERIC, 0, strArr, null);
            }
            if (requestAmt < 0) {
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", "validateMCDListAmount", SelfTopUpErrorCodesI.P2P_MCDL_AMT_LESSTHANZERO, 0, strArr, null);
            }
            // if (requestAmt >SystemPreferences.P2P_MAXTRNSFR_AMOUNT)
            if (requestAmt > SystemPreferences.P2P_MCDL_MAXADD_AMOUNT) {
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", "validateMCDListAmount", SelfTopUpErrorCodesI.P2P_MCDL_AMT_MAX_LIMIT, 0, strArr, null);
            }

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateMCDListAmount", "  Exception while validate amount :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateMCDListAmount]", "", "", "", "Exception while validating amount" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateMCDListAmount", SelfTopUpErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("validateMCDListAmount", "Exiting ");

        return requestAmt;
    }

    /**
     * This method define to get the promotional transfer rule based on the
     * combinations of service class, subscriber status and subscriber service
     * provider group
     * 
     * @author gaurav.pandey
     * @param p_servicetype
     * @param p_module
     * @param p_receivernetworkcode
     * @param p_sendersubscribertype
     * @param p_receiversubscribertype
     * @param p_senderserviceclasscode
     * @param p_receiverserviceclasscode
     * @param p_subservice
     * @param p_promotionallevel
     * @param p_spgroup
     * @param p_accountSsatus
     * @param p_info
     * @return
     */
    private static TransferRulesVO getPromotionalTransferRule(String p_servicetype, String p_module, String p_receivernetworkcode, String p_sendersubscribertype, String p_receiversubscribertype, String p_senderserviceclasscode, String p_receiverserviceclasscode, String p_receiverallserviceclasscode, String p_subservice, String p_promotionallevel, String p_spgroup, String p_accountSsatus, String p_info, HashMap p_transferrulemap, String p_transferid, String p_networkcode) {
        if (_log.isDebugEnabled())
            _log.debug("getPromotionalTransferRule", "p_servicetype=" + p_servicetype + " p_module=" + p_module + " p_receivernetworkcode=" + p_receivernetworkcode + " p_sendersubscribertype=" + p_sendersubscribertype + " p_receiversubscribertype=" + p_receiversubscribertype + " p_senderserviceclasscode=" + p_senderserviceclasscode + " p_receiverserviceclasscode=" + p_receiverserviceclasscode + " p_receiverallserviceclasscode=" + p_receiverallserviceclasscode + " p_subservice=" + p_subservice + " p_promotionallevel=" + p_promotionallevel + " p_spgroup=" + p_spgroup + " p_accountSsatus=" + p_accountSsatus + " p_info" + p_info, "Entered");

        TransferRulesVO transferRulesVO = null;

        // 1. Check the for service Class + ALL SP Name + Subscriber status
        transferRulesVO = (TransferRulesVO) p_transferrulemap.get(p_servicetype + "_" + p_module + "_" + p_receivernetworkcode + "_" + p_sendersubscribertype + "_" + p_receiversubscribertype + "_" + p_senderserviceclasscode + "_" + p_receiverserviceclasscode + "_" + p_subservice + "_" + p_promotionallevel + "_" + PretupsI.ALL + "_" + p_accountSsatus);
        if (_log.isDebugEnabled())
            _log.debug("getPromotionalTransferRule", p_transferid, "Validation For Network Code=" + p_networkcode + " Sender Subscriber Type=" + p_sendersubscribertype + " Sender Service class=" + p_senderserviceclasscode + "Receiver Subscriber Type=" + p_receiverserviceclasscode + " Receiver Service Class ID=" + p_receiverallserviceclasscode + " Sub Service=" + p_subservice + p_info + " Service Class + ALL SP Name + Subscriber Status");

        // 2. Check the for service Class + SP Name + ALL Subscriber status
        if (transferRulesVO == null) {
            transferRulesVO = (TransferRulesVO) p_transferrulemap.get(p_servicetype + "_" + p_module + "_" + p_receivernetworkcode + "_" + p_sendersubscribertype + "_" + p_receiversubscribertype + "_" + p_senderserviceclasscode + "_" + p_receiverserviceclasscode + "_" + p_subservice + "_" + p_promotionallevel + "_" + p_spgroup + "_" + PretupsI.ALL);
            if (_log.isDebugEnabled())
                _log.debug("getPromotionalTransferRule", p_transferid, "Validation For Network Code=" + p_networkcode + " Sender Subscriber Type=" + p_sendersubscribertype + " Sender Service class=" + p_senderserviceclasscode + "Receiver Subscriber Type=" + p_receiverserviceclasscode + " Receiver Service Class ID=" + p_receiverallserviceclasscode + " Sub Service=" + p_subservice + p_info + " service Class +  SP Name + ALL Subscriber status");

        }

        // 3. Check the for service Class + ALL SP Name + ALL Subscriber status
        if (transferRulesVO == null) {
            transferRulesVO = (TransferRulesVO) p_transferrulemap.get(p_servicetype + "_" + p_module + "_" + p_receivernetworkcode + "_" + p_sendersubscribertype + "_" + p_receiversubscribertype + "_" + p_senderserviceclasscode + "_" + p_receiverserviceclasscode + "_" + p_subservice + "_" + p_promotionallevel + "_" + PretupsI.ALL + "_" + PretupsI.ALL);
            if (_log.isDebugEnabled())
                _log.debug("getPromotionalTransferRule", p_transferid, "Validation For Network Code=" + p_networkcode + " Sender Subscriber Type=" + p_sendersubscribertype + " Sender Service class=" + p_senderserviceclasscode + "Receiver Subscriber Type=" + p_receiverserviceclasscode + " Receiver Service Class ID=" + p_receiverallserviceclasscode + " Sub Service=" + p_subservice + p_info + " service Class + ALL SP Name + ALL Subscriber status");

        }

        // 4. Check the for ALL service Class + ALL SP Name + Subscriber status
        if (transferRulesVO == null && !BTSLUtil.isNullString(p_receiverallserviceclasscode)) {
            transferRulesVO = (TransferRulesVO) p_transferrulemap.get(p_servicetype + "_" + p_module + "_" + p_receivernetworkcode + "_" + p_sendersubscribertype + "_" + p_receiversubscribertype + "_" + p_senderserviceclasscode + "_" + p_receiverallserviceclasscode + "_" + p_subservice + "_" + p_promotionallevel + "_" + PretupsI.ALL + "_" + p_accountSsatus);
            if (_log.isDebugEnabled())
                _log.debug("getPromotionalTransferRule", p_transferid, "Validation For Network Code=" + p_networkcode + " Sender Subscriber Type=" + p_sendersubscribertype + " Sender Service class=" + p_senderserviceclasscode + "Receiver Subscriber Type=" + p_receiverserviceclasscode + " Receiver Service Class ID=" + p_receiverallserviceclasscode + " Sub Service=" + p_subservice + p_info + " ALL service Class + ALL SP Name + Subscriber status");

        }

        // 5. Check the for ALL service Class + SP Name + ALL Subscriber status
        if (transferRulesVO == null && !BTSLUtil.isNullString(p_receiverallserviceclasscode)) {
            transferRulesVO = (TransferRulesVO) p_transferrulemap.get(p_servicetype + "_" + p_module + "_" + p_receivernetworkcode + "_" + p_sendersubscribertype + "_" + p_receiversubscribertype + "_" + p_senderserviceclasscode + "_" + p_receiverallserviceclasscode + "_" + p_subservice + "_" + p_promotionallevel + "_" + p_spgroup + "_" + PretupsI.ALL);
            if (_log.isDebugEnabled())
                _log.debug("getPromotionalTransferRule", p_transferid, "Validation For Network Code=" + p_networkcode + " Sender Subscriber Type=" + p_sendersubscribertype + " Sender Service class=" + p_senderserviceclasscode + "Receiver Subscriber Type=" + p_receiverserviceclasscode + " Receiver Service Class ID=" + p_receiverallserviceclasscode + " Sub Service=" + p_subservice + p_info + " ALL service Class + SP Name + ALL Subscriber status ");

        }

        // 6. Check the for ALL service Class + ALL SP Name + ALL Subscriber
        // status
        if (transferRulesVO == null && !BTSLUtil.isNullString(p_receiverallserviceclasscode)) {
            transferRulesVO = (TransferRulesVO) p_transferrulemap.get(p_servicetype + "_" + p_module + "_" + p_receivernetworkcode + "_" + p_sendersubscribertype + "_" + p_receiversubscribertype + "_" + p_senderserviceclasscode + "_" + p_receiverallserviceclasscode + "_" + p_subservice + "_" + p_promotionallevel + "_" + PretupsI.ALL + "_" + PretupsI.ALL);
            if (_log.isDebugEnabled())
                _log.debug("getPromotionalTransferRule", p_transferid, "Validation For Network Code=" + p_networkcode + " Sender Subscriber Type=" + p_sendersubscribertype + " Sender Service class=" + p_senderserviceclasscode + "Receiver Subscriber Type=" + p_receiverserviceclasscode + " Receiver Service Class ID=" + p_receiverallserviceclasscode + " Sub Service=" + p_subservice + p_info + " ALL service Class + ALL SP Name + ALL Subscriber status");

        }

        if (_log.isDebugEnabled())
            _log.debug("getPromotionalTransferRule", transferRulesVO, "Exiting");

        return transferRulesVO;
    }

    private static String spacePad(String p_messageStr, int p_padLength, String p_padStr, char p_direction) throws Exception {
        StringBuffer padStrBuffer = null;
        try {
            if (p_messageStr.length() < p_padLength) {
                int paddingLength = p_padLength - p_messageStr.length();
                padStrBuffer = new StringBuffer(10);
                if (p_direction == 'r')
                    padStrBuffer.append(p_messageStr);
                for (int i = 0; i < paddingLength; i++)
                    padStrBuffer.append(p_padStr);
                if (p_direction == 'l')
                    padStrBuffer.append(p_messageStr);
                p_messageStr = padStrBuffer.toString();
            }
        } catch (Exception e) {
            throw e;
        }
        return p_messageStr;
    }

    /**
     * This method validates the Reciever Limits based on stage passed
     * This method is changes messages prepaid and postpaid wise date 22/05/06
     * 
     * @param p_con
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void validateRecieverLimits(Connection p_con, TransferVO p_transferVO, int p_stage, String p_moduleCode) throws BTSLBaseException {

        String restrictedMSISDN = null;

        // For Number back Service
        String numbckAllowedDays;
        String numbckAmountDeducted;
        String[] numberOfDays;
        String[] amountDeducted;
        Date graceDate = null;

        if (PretupsI.C2S_MODULE.equals(p_moduleCode))
            restrictedMSISDN = (((CategoryVO) ((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryVO()).getRestrictedMsisdns());
        if (_log.isDebugEnabled())
            _log.debug("validateRecieverLimits", p_transferVO.getTransferID(), "Entered p_stage:" + p_stage + " p_moduleCode=" + p_moduleCode + "  restrictedMSISDN: " + restrictedMSISDN + " p_transferVO.getReceiverSubscriberType(): " + p_transferVO.getReceiverSubscriberType());
        ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
        try {

            /*
             * 1) Get the service_class to be used if ALL service class is to be
             * used
             * Load the reciever msisdn date controls records from subscriber
             * control table
             * 2) If record not found then insert the records with the date and
             * transaction ID (irrespective of service class check)
             * 3) If record found then check the last transaction status
             * 4) If last transaction is under process then send response back,
             * update last_transaction_time
             * 5) Is last transaction success then check the time of last
             * success time, if not allowed then update last_transaction_time
             * 6) Check the total success count with the allowed one if not
             * allowed then update last_transaction_time
             * 7) Check for the total transaction Amount in the day with the
             * allowed one if not allowed then update last_transaction_time
             * 8) Send the request to IN
             * 9) After Validation response, get the status
             * a) Is validation success, get the service class
             * b) update the last_transaction_time and stage
             * c) If validation failed, based on the error code determine
             * whether failed count needs to be increased
             * d) If consecutive failures recah the threshold barr the
             * subscribner and initialize the counter to 0
             * e) update the last_transaction_time and stage and status
             * 10) Send topup request to IN
             * 11) After response get the status
             * a) Is success, increase the success count, total transaction
             * amount, last_success_on
             * b) Initialize the consecutive failures to 0
             * c) update the last_transaction_time and stage
             * d) If failed, , based on the error code determine whether failed
             * count needs to be increased, increase the fail count,
             * last_failed_on, consecutive failures
             * e) If consecutive failures recah the threshold barr the
             * subscribner and initialize the counter to 0
             * f) update the last_transaction_time and stage and status
             * 
             * If ALL is not there in service class
             * 1) Send the validation request to IN after 4)
             * 2) After Validation response, get the service class and status
             * a) Is validation success, get the service class
             * b) Is last transaction success then check the time of last
             * success time, if not allowed then update last_transaction_time
             * c) Check the total success count with the allowed one if not
             * allowed then update last_transaction_time
             * d) Check for the total transaction Amount in the day with the
             * allowed one if not allowed then update last_transaction_time
             * e) update the last_transaction_time and stage
             * f) If validation failed, based on the error code determine
             * whether failed count needs to be increased
             * g) If consecutive failures recah the threshold barr the
             * subscribner and initialize the counter to 0
             * h) update the last_transaction_time and stage and status
             * 3) Send topup request to IN
             * 4) After response get the status
             * a) Is success, increase the success count, total transaction
             * amount, last_success_on
             * b) Initialize the consecutive failures to 0
             * c) update the last_transaction_time and stage
             * d) If failed, , based on the error code determine whether failed
             * count needs to be increased , increase the fail count,
             * last_failed_on, consecutive failures
             * e) If consecutive failures recah the threshold barr the
             * subscribner and initialize the counter to 0
             * f) update the last_transaction_time and stage and status
             */
            String service_class = null;
            Object serviceObjVal = null;
            Date currentDate = p_transferVO.getCreatedOn();
            // changes for ID=SUBTYPVALRECLMT
            // The following change is done for following
            // 1.If receiverTransferItem is null then set the subscriber type
            // from the transferVO
            // 2.The receiver subscriber type is set in the transferVO from all
            // controllers
            // 3.If receiverTransferItemVO is not null then set the subscriber
            // type from this VO.
            // 4.If this was not done then when receiver limits are checked
            // before IN validation then receiver transfer item VO was null and
            // it gives null pointer exception
            String receiverSubscriberType = p_transferVO.getReceiverTransferItemVO() == null ? p_transferVO.getReceiverSubscriberType() : p_transferVO.getReceiverTransferItemVO().getSubscriberType();
            if (p_stage == PretupsI.TRANS_STAGE_BEFORE_INVAL) {
                boolean isValidated = false;
                if (PretupsI.STATUS_ACTIVE.equalsIgnoreCase(restrictedMSISDN)) {
                    if (PretupsI.STATUS_ACTIVE.equalsIgnoreCase((((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryVO()).getTransferToListOnly())) {
                        isValidated = RestrictedSubscriberBL.validateRestrictedSubscriberLimits(p_transferVO);
                        if (!isValidated) {
                            throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.C2S_ERROR_EXCEPTION);
                        }
                    }
                }

                service_class = p_transferVO.getReceiverAllServiceClassID();
                if (service_class != null) {
                    if (receiverVO.getLastSuccessOn() != null) {
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                        else
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_SUCCESS_REQUEST_BLOCK_SEC_CODE, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());

                        if (serviceObjVal != null) {
                            if (((currentDate.getTime() - receiverVO.getLastSuccessOn().getTime()) / 1000) <= ((Long) serviceObjVal).longValue()) {
                                if (SystemPreferences.MRP_BLOCK_TIME_ALLOWED) {
                                    Object serviceTypeObjVal = null;

                                    if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                                    // for
                                                                                                    // ID=SUBTYPVALRECLMT
                                                                                                    // receiverSubscriberType
                                                                                                    // that
                                                                                                    // is
                                                                                                    // set
                                                                                                    // above
                                                                                                    // is
                                                                                                    // used
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / (double) 60.0), 2) }));
                                    else
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / (double) 60.0), 2) }));
                                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                    String strArr[] = { receiverVO.getMsisdn(), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / (double) 60.0), 2) };

                                    receiverVO.setLastSuccTransBlockCheckDone(true);
                                    // If this preference is true MRP block time
                                    // check will be done on the basis of
                                    // requested amount is equal to previous
                                    // amount and requested service type is
                                    // equal to previous service type
                                    // If this preference is false MRP block
                                    // time check will be done only on the basis
                                    // of requested amount is equal to previous
                                    // amount.
                                    serviceTypeObjVal = PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_SERVICE_TYPE_CHECK);
                                    boolean flag = (Boolean) serviceTypeObjVal;
                                    // Block need to implement::if
                                    // LAST_SERVICE_TYPE_CHECK is TRUE and
                                    // requested amount is equal to previous
                                    // amount and requested service type is
                                    // equal to previous service type, then
                                    // recharge is not allowed
                                    if (flag && p_transferVO.getRequestedAmount() == receiverVO.getLastMRP() && p_transferVO.getServiceType().equals(receiverVO.getLastServiceType()))
                                        throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.MRP_BLOCK_TIME_WID_SERVICE_TYPE, 0, strArr, null);
                                    // Block need to implement::if requested
                                    // amount is equal to previous amount, here
                                    // No service type check.
                                    else if (p_transferVO.getRequestedAmount() == receiverVO.getLastMRP())
                                        throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.MRP_BLOCK_TIME, 0, strArr, null);

                                } else {

                                    if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                                    // for
                                                                                                    // ID=SUBTYPVALRECLMT
                                                                                                    // receiverSubscriberType
                                                                                                    // that
                                                                                                    // is
                                                                                                    // set
                                                                                                    // above
                                                                                                    // is
                                                                                                    // used
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / (double) 60.0), 2) }));
                                    else
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / (double) 60.0), 2) }));
                                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                    String strArr[] = { receiverVO.getMsisdn(), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / (double) 60.0), 2) };

                                    receiverVO.setLastSuccTransBlockCheckDone(true);
                                    throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_S, 0, strArr, null);
                                }
                            }
                        }

                    }
                    serviceObjVal = null;
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                    else
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_DAILY_TOTAL_TXN_AMT_ALLOWED, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                    if (serviceObjVal != null) {
                        receiverVO.setTotalTransAmtCheckDone(true);
                        // How to differentiate between this preference for
                        // sender or receiver
                        if (receiverVO.getTotalTransferAmount() + p_transferVO.getRequestedAmount() > ((Long) serviceObjVal).longValue()) {
                            // date 22/05/06
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                            // for
                                                                                            // ID=SUBTYPVALRECLMT
                                                                                            // receiverSubscriberType
                                                                                            // that
                                                                                            // is
                                                                                            // set
                                                                                            // above
                                                                                            // is
                                                                                            // used
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R_PRE, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                            // p_transferVO.setReceiverReturnMsg(new
                            // BTSLMessages(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R,new
                            // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),getDisplayAmount(receiverVO.getTotalTransferAmount()),getDisplayAmount(((Long)serviceObjVal).longValue())}));
                            else
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R_POST, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                            String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_S, 0, strArr, null);
                        }
                    }

                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class,false);
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                    else
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_DAILY_SUCCESS_TXN_ALLOWED_COUNT, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                    if (serviceObjVal != null) {
                        receiverVO.setNoOfSuccTransCheckDone(true);
                        // How to differentiate between this preference for
                        // sender or receiver
                        if (receiverVO.getTotalSuccessCount() >= ((Long) serviceObjVal).longValue()) {
                            // date 22/05/06
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                            // for
                                                                                            // ID=SUBTYPVALRECLMT
                                                                                            // receiverSubscriberType
                                                                                            // that
                                                                                            // is
                                                                                            // set
                                                                                            // above
                                                                                            // is
                                                                                            // used
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_PRE, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getTotalSuccessCount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                            // p_transferVO.setReceiverReturnMsg(new
                            // BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R,new
                            // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),String.valueOf(receiverVO.getTotalSuccessCount()),String.valueOf(((Long)serviceObjVal).longValue())}));
                            else
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_POST, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getTotalSuccessCount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                            String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), String.valueOf(receiverVO.getTotalSuccessCount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_S, 0, strArr, null);
                        }
                    }
                    // Check the weekly allowed transfer amount-Ashish K
                    // 07/09/07
                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED,receiverVO.getNetworkCode(),p_moduleCode,service_class,false);
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_TOTXN_AMT_ALLWDCO, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                    else
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_REC_AMT_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                    if (serviceObjVal != null) {
                        receiverVO.setTotalWeeklyTransAmtCheckDone(true);// Confirm
                                                                         // the
                                                                         // reason
                                                                         // for
                                                                         // this
                                                                         // flag
                        // How to differentiate between this preference for
                        // sender or receiver
                        if (receiverVO.getWeeklyTransferAmount() + p_transferVO.getRequestedAmount() > ((Long) serviceObjVal).longValue()) {
                            // date 22/05/06
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                            // for
                                                                                            // ID=SUBTYPVALRECLMT
                                                                                            // receiverSubscriberType
                                                                                            // that
                                                                                            // is
                                                                                            // set
                                                                                            // above
                                                                                            // is
                                                                                            // used
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_R_PRE, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getWeeklyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                            // p_transferVO.setReceiverReturnMsg(new
                            // BTSLMessages(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R,new
                            // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),getDisplayAmount(receiverVO.getTotalTransferAmount()),getDisplayAmount(((Long)serviceObjVal).longValue())}));
                            else
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_R_POST, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getWeeklyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                            String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), getDisplayAmount(receiverVO.getWeeklyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_S, 0, strArr, null);
                        }
                    }
                    // Check weekly transaction number
                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class,false);
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_SUCTRAN_ALLWDCOUN, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                    else
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_SUCTRAN_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                    if (serviceObjVal != null) {
                        receiverVO.setNoOfWeeklySuccTransCheckDone(true);
                        // How to differentiate between this preference for
                        // sender or receiver
                        if (receiverVO.getWeeklySuccCount() >= ((Long) serviceObjVal).longValue()) {
                            // date 22/05/06
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                            // for
                                                                                            // ID=SUBTYPVALRECLMT
                                                                                            // receiverSubscriberType
                                                                                            // that
                                                                                            // is
                                                                                            // set
                                                                                            // above
                                                                                            // is
                                                                                            // used
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_R_PRE, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getWeeklySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                            // p_transferVO.setReceiverReturnMsg(new
                            // BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R,new
                            // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),String.valueOf(receiverVO.getTotalSuccessCount()),String.valueOf(((Long)serviceObjVal).longValue())}));
                            else
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_R_POST, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getWeeklySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                            String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), String.valueOf(receiverVO.getWeeklySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_S, 0, strArr, null);
                        }
                    }
                    // Check the monthly allowed transfer amount-Ashish K
                    // 07/09/07
                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED,receiverVO.getNetworkCode(),p_moduleCode,service_class,false);
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_TOTXN_AMT_ALLWDCO, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                    else
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_REC_AMT_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                    if (serviceObjVal != null) {
                        receiverVO.setTotalMonthlyTransAmtCheckDone(true);// Confirm
                                                                          // the
                                                                          // reason
                                                                          // for
                                                                          // this
                                                                          // flag
                        // How to differentiate between this preference for
                        // sender or receiver
                        if (receiverVO.getMonthlyTransferAmount() + p_transferVO.getRequestedAmount() > ((Long) serviceObjVal).longValue()) {
                            // date 22/05/06
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                            // for
                                                                                            // ID=SUBTYPVALRECLMT
                                                                                            // receiverSubscriberType
                                                                                            // that
                                                                                            // is
                                                                                            // set
                                                                                            // above
                                                                                            // is
                                                                                            // used
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_R_PRE, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                            // p_transferVO.setReceiverReturnMsg(new
                            // BTSLMessages(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R,new
                            // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),getDisplayAmount(receiverVO.getTotalTransferAmount()),getDisplayAmount(((Long)serviceObjVal).longValue())}));
                            else
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_R_POST, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                            String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), getDisplayAmount(receiverVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_S, 0, strArr, null);
                        }
                    }
                    // Check monthly number of successfull transaction number
                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class,false);
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_SUCTRAN_ALLWDCOUN, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                    else
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_SUCTRAN_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO.getReceiverAllServiceClassID());
                    if (serviceObjVal != null) {
                        receiverVO.setNoOfMonthlySuccTransCheckDone(true);
                        // How to differentiate between this preference for
                        // sender or receiver
                        if (receiverVO.getMonthlySuccCount() >= ((Long) serviceObjVal).longValue()) {
                            // date 22/05/06
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                            // for
                                                                                            // ID=SUBTYPVALRECLMT
                                                                                            // receiverSubscriberType
                                                                                            // that
                                                                                            // is
                                                                                            // set
                                                                                            // above
                                                                                            // is
                                                                                            // used
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_R_PRE, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getMonthlySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                            // p_transferVO.setReceiverReturnMsg(new
                            // BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R,new
                            // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),String.valueOf(receiverVO.getTotalSuccessCount()),String.valueOf(((Long)serviceObjVal).longValue())}));
                            else
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_R_POST, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getMonthlySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                            String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), String.valueOf(receiverVO.getMonthlySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_S, 0, strArr, null);
                        }
                    }
                }
            } else if (p_stage == PretupsI.TRANS_STAGE_AFTER_INVAL) {

                // check for the Number back service applicable
                if (p_transferVO.getReceiverTransferItemVO().isNumberBackAllowed()) {
                    String daysDiff = p_transferVO.getServiceType() + PreferenceI.NUMBCK_ALWD_DAYS_DIFF;
                    String deductedAmount = p_transferVO.getServiceType() + PreferenceI.NUMBCK_AMT_DEDCTED;

                    numbckAllowedDays = (String) PreferenceCache.getControlPreference(daysDiff, p_transferVO.getNetworkCode(), p_transferVO.getReceiverTransferItemVO().getInterfaceID());
                    numbckAmountDeducted = (String) PreferenceCache.getControlPreference(deductedAmount, p_transferVO.getNetworkCode(), p_transferVO.getReceiverTransferItemVO().getInterfaceID());

                    // Number back allowed days can be SUSPENDED=20&DEACT=15 OR
                    // SUSPENDED=20 OR ONLY 20
                    if (numbckAllowedDays.contains("=")) {
                        if (numbckAllowedDays.contains("&")) {
                            numberOfDays = numbckAllowedDays.split("&");
                            int size = numberOfDays.length;
                            for (int i = 0; i < size; i++) {
                                if (numberOfDays[i].contains(p_transferVO.getReceiverTransferItemVO().getAccountStatus())) {
                                    String[] newNumberOfDays = numberOfDays[i].split("=");
                                    numbckAllowedDays = newNumberOfDays[1];
                                }
                            }
                        } else if (numbckAllowedDays.contains("=")) {
                            numberOfDays = numbckAllowedDays.split("=");
                            numbckAllowedDays = numberOfDays[1];
                        }
                    }

                    // Number back amount deducted could be
                    // SUSPENDED=2000&DEACT=1500 OR SUSPENDED=2000 OR ONLY 2000
                    if (numbckAmountDeducted.contains("=")) {
                        if (numbckAmountDeducted.contains("&")) {
                            amountDeducted = numbckAmountDeducted.split("&");
                            int size = amountDeducted.length;
                            for (int i = 0; i < size; i++) {
                                if (amountDeducted[i].contains(p_transferVO.getReceiverTransferItemVO().getAccountStatus())) {
                                    String[] newAmountDeducted = amountDeducted[i].split("=");
                                    numbckAmountDeducted = newAmountDeducted[1];
                                }
                            }
                        } else if (numbckAmountDeducted.contains("=")) {
                            amountDeducted = numbckAmountDeducted.split("=");
                            numbckAmountDeducted = amountDeducted[1];
                        }
                    }

                    p_transferVO.getReceiverTransferItemVO().setAmountDeducted(Integer.parseInt(numbckAmountDeducted));

                    String dateStrGrace = null;
                    try {
                        dateStrGrace = (p_transferVO.getReceiverTransferItemVO().getPreviousGraceDate() == null) ? "0" : BTSLUtil.getDateStringFromDate(p_transferVO.getReceiverTransferItemVO().getPreviousGraceDate());
                        graceDate = BTSLUtil.getDateFromDateString(dateStrGrace);
                    } catch (Exception e) {
                        throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.GRACE_DATE_IS_WRONG);
                    }

                    /*
                     * This code was commented on 01/04/08 to eliminate fetch
                     * conversion rate step.
                     * Now Moldova will support single currency. Previously
                     * conversion rate was required to
                     * support multiple currency for moldova.
                     * 
                     * if(p_transferVO.getConvertedRequestedAmount()==0)p_transferVO
                     * .
                     * setConvertedRequestedAmount(p_transferVO.getRequestedAmount
                     * ());
                     * 
                     * if(BTSLUtil.getDifferenceInUtilDates(graceDate,currentDate
                     * )>Integer.parseInt(numbckAllowedDays))
                     * throw new
                     * BTSLBaseException("PretupsBL","validateRecieverLimits"
                     * ,PretupsErrorCodesI.RECHARGE_IS_NOT_ALLOW);
                     * if(!(p_transferVO.getConvertedRequestedAmount()>
                     * Integer.parseInt(numbckAmountDeducted)))
                     * throw new
                     * BTSLBaseException("PretupsBL","validateRecieverLimits"
                     * ,PretupsErrorCodesI.RECHARGE_AMOUNT_IS_NOT_SUFFICIENT);
                     */

                    if (BTSLUtil.getDifferenceInUtilDates(graceDate, currentDate) > Integer.parseInt(numbckAllowedDays))
                        throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.RECHARGE_IS_NOT_ALLOW);
                    if (!(p_transferVO.getRequestedAmount() > Integer.parseInt(numbckAmountDeducted)))
                        throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.RECHARGE_AMOUNT_IS_NOT_SUFFICIENT);
                    // End of single currency request change
                }

                if (receiverVO.getTransactionStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS)) {
                    service_class = receiverVO.getServiceClassCode();

                    if (!receiverVO.isLastSuccTransBlockCheckDone() && receiverVO.getLastSuccessOn() != null) {
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE,receiverVO.getNetworkCode(),p_moduleCode,service_class);
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());
                        else
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_SUCCESS_REQUEST_BLOCK_SEC_CODE, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());

                        if (serviceObjVal != null) {
                            if (((currentDate.getTime() - receiverVO.getLastSuccessOn().getTime()) / 1000) <= ((Long) serviceObjVal).longValue()) {
                                if (SystemPreferences.MRP_BLOCK_TIME_ALLOWED) {
                                    Object serviceTypeObjVal = null;

                                    if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                                    // for
                                                                                                    // ID=SUBTYPVALRECLMT
                                                                                                    // receiverSubscriberType
                                                                                                    // that
                                                                                                    // is
                                                                                                    // set
                                                                                                    // above
                                                                                                    // is
                                                                                                    // used
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / (double) 60.0), 2) }));
                                    else
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / (double) 60.0), 2) }));
                                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                    String strArr[] = { receiverVO.getMsisdn(), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / (double) 60.0), 2) };

                                    receiverVO.setLastSuccTransBlockCheckDone(true);
                                    // If this preference is true MRP block time
                                    // check will be done on the basis of
                                    // requested amount is equal to previous
                                    // amount and requested service type is
                                    // equal to previous service type
                                    // If this preference is false MRP block
                                    // time check will be done only on the basis
                                    // of requested amount is equal to previous
                                    // amount.
                                    serviceTypeObjVal = PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_SERVICE_TYPE_CHECK);
                                    boolean flag = (Boolean) serviceTypeObjVal;
                                    // Block need to implement::if
                                    // LAST_SERVICE_TYPE_CHECK is TRUE and
                                    // requested amount is equal to previous
                                    // amount and requested service type is
                                    // equal to previous service type, then
                                    // recharge is not allowed
                                    if (flag && p_transferVO.getRequestedAmount() == receiverVO.getLastMRP() && p_transferVO.getServiceType().equals(receiverVO.getLastServiceType()))
                                        throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.MRP_BLOCK_TIME_WID_SERVICE_TYPE, 0, strArr, null);
                                    // Block need to implement::if requested
                                    // amount is equal to previous amount, here
                                    // No service type check.
                                    else if (p_transferVO.getRequestedAmount() == receiverVO.getLastMRP())
                                        throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.MRP_BLOCK_TIME, 0, strArr, null);

                                } else {

                                    // date 22/05/06
                                    if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                                    // for
                                                                                                    // ID=SUBTYPVALRECLMT
                                                                                                    // receiverSubscriberType
                                                                                                    // that
                                                                                                    // is
                                                                                                    // set
                                                                                                    // above
                                                                                                    // is
                                                                                                    // used
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / (double) 60.0), 2) }));
                                    // p_transferVO.setReceiverReturnMsg(new
                                    // BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R,new
                                    // String[]{BTSLUtil.roundToStr((((Long)serviceObjVal).longValue()/(double)60.0),2)}));
                                    else
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / (double) 60.0), 2) }));
                                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                    String strArr[] = { receiverVO.getMsisdn(), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / (double) 60.0), 2) };
                                    throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_S, 0, strArr, null);
                                }
                            }
                        }
                    }

                    if (!receiverVO.isTotalTransAmtCheckDone()) {
                        serviceObjVal = null;
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED,receiverVO.getNetworkCode(),p_moduleCode,service_class);
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());
                        else
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_DAILY_TOTAL_TXN_AMT_ALLOWED, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());

                        if (serviceObjVal != null) {
                            // How to differentiate between this preference for
                            // sender or receiver
                            if (receiverVO.getTotalTransferAmount() + p_transferVO.getRequestedAmount() > ((Long) serviceObjVal).longValue()) {
                                // date 22/05/06
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                                // for
                                                                                                // ID=SUBTYPVALRECLMT
                                                                                                // receiverSubscriberType
                                                                                                // that
                                                                                                // is
                                                                                                // set
                                                                                                // above
                                                                                                // is
                                                                                                // used
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R_PRE, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                                // p_transferVO.setReceiverReturnMsg(new
                                // BTSLMessages(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R,new
                                // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),getDisplayAmount(receiverVO.getTotalTransferAmount()),getDisplayAmount(((Long)serviceObjVal).longValue())}));
                                else
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R_POST, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                                String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };

                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }

                    if (!receiverVO.isNoOfSuccTransCheckDone()) {
                        serviceObjVal = null;
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class);
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());
                        else
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_DAILY_SUCCESS_TXN_ALLOWED_COUNT, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());

                        if (serviceObjVal != null) {
                            // How to differentiate between this preference for
                            // sender or receiver
                            if (receiverVO.getTotalSuccessCount() >= ((Long) serviceObjVal).longValue()) {
                                // date 22/05/06
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                                // for
                                                                                                // ID=SUBTYPVALRECLMT
                                                                                                // receiverSubscriberType
                                                                                                // that
                                                                                                // is
                                                                                                // set
                                                                                                // above
                                                                                                // is
                                                                                                // used
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_PRE, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getTotalSuccessCount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                                // p_transferVO.setReceiverReturnMsg(new
                                // BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R,new
                                // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),String.valueOf(receiverVO.getTotalSuccessCount()),String.valueOf(((Long)serviceObjVal).longValue())}));
                                else
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_POST, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getTotalSuccessCount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                                String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), String.valueOf(receiverVO.getTotalSuccessCount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }

                    // Weekly total transaction amount check, if it is not
                    // checked before IN validation.
                    if (!receiverVO.isTotalWeeklyTransAmtCheckDone()) {
                        serviceObjVal = null;
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_TOTXN_AMT_ALLWDCO, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());
                        else
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_REC_AMT_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());
                        if (serviceObjVal != null) {
                            receiverVO.setTotalWeeklyTransAmtCheckDone(true);// Confirm
                                                                             // the
                                                                             // reason
                                                                             // for
                                                                             // this
                                                                             // flag
                            if (receiverVO.getWeeklyTransferAmount() + p_transferVO.getRequestedAmount() > ((Long) serviceObjVal).longValue()) {
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                                // for
                                                                                                // ID=SUBTYPVALRECLMT
                                                                                                // receiverSubscriberType
                                                                                                // that
                                                                                                // is
                                                                                                // set
                                                                                                // above
                                                                                                // is
                                                                                                // used
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_R_PRE, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getWeeklyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                                else
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_R_POST, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getWeeklyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                                String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), getDisplayAmount(receiverVO.getWeeklyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };

                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }
                    // Check the weekly successful transaction number, if it is
                    // not checked before IN validation.
                    if (!receiverVO.isNoOfWeeklySuccTransCheckDone()) {
                        serviceObjVal = null;
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_SUCTRAN_ALLWDCOUN, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());
                        else
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_SUCTRAN_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());
                        if (serviceObjVal != null) {
                            receiverVO.setNoOfWeeklySuccTransCheckDone(true);
                            if (receiverVO.getWeeklySuccCount() >= ((Long) serviceObjVal).longValue()) {
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                                // for
                                                                                                // ID=SUBTYPVALRECLMT
                                                                                                // receiverSubscriberType
                                                                                                // that
                                                                                                // is
                                                                                                // set
                                                                                                // above
                                                                                                // is
                                                                                                // used
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_R_PRE, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getWeeklySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                                else
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_R_POST, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getWeeklySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                                String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), String.valueOf(receiverVO.getWeeklySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }
                    // Check the monthly total successful transfer amount, if it
                    // is not checked before IN validation.
                    if (!receiverVO.isTotalMonthlyTransAmtCheckDone()) {
                        serviceObjVal = null;
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED,receiverVO.getNetworkCode(),p_moduleCode,service_class,false);
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_TOTXN_AMT_ALLWDCO, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());
                        else
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_REC_AMT_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());
                        if (serviceObjVal != null) {
                            receiverVO.setTotalMonthlyTransAmtCheckDone(true);// Confirm
                                                                              // the
                                                                              // reason
                                                                              // for
                                                                              // this
                                                                              // flag
                            // How to differentiate between this preference for
                            // sender or receiver
                            if (receiverVO.getMonthlyTransferAmount() + p_transferVO.getRequestedAmount() > ((Long) serviceObjVal).longValue()) {
                                // date 22/05/06
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                                // for
                                                                                                // ID=SUBTYPVALRECLMT
                                                                                                // receiverSubscriberType
                                                                                                // that
                                                                                                // is
                                                                                                // set
                                                                                                // above
                                                                                                // is
                                                                                                // used
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_R_PRE, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                                // p_transferVO.setReceiverReturnMsg(new
                                // BTSLMessages(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R,new
                                // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),getDisplayAmount(receiverVO.getTotalTransferAmount()),getDisplayAmount(((Long)serviceObjVal).longValue())}));
                                else
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_R_POST, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                                String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), getDisplayAmount(receiverVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };

                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }
                    // Check the monthly successful transaction number, if it is
                    // not checked before IN validation.
                    if (!receiverVO.isNoOfMonthlySuccTransCheckDone()) {
                        serviceObjVal = null;
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class,false);
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_SUCTRAN_ALLWDCOUN, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());
                        else
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_SUCTRAN_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());
                        if (serviceObjVal != null) {
                            receiverVO.setNoOfMonthlySuccTransCheckDone(true);
                            // How to differentiate between this preference for
                            // sender or receiver
                            if (receiverVO.getMonthlySuccCount() >= ((Long) serviceObjVal).longValue()) {
                                // date 22/05/06
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))// changes
                                                                                                // for
                                                                                                // ID=SUBTYPVALRECLMT
                                                                                                // receiverSubscriberType
                                                                                                // that
                                                                                                // is
                                                                                                // set
                                                                                                // above
                                                                                                // is
                                                                                                // used
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_R_PRE, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getMonthlySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                                // p_transferVO.setReceiverReturnMsg(new
                                // BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R,new
                                // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),String.valueOf(receiverVO.getTotalSuccessCount()),String.valueOf(((Long)serviceObjVal).longValue())}));
                                else
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_R_POST, new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getMonthlySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                                String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), String.valueOf(receiverVO.getMonthlySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }
                    // Validate the Max allowed balance of the receiver
                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class,false);
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode))
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.MAX_ALLD_BALANCE_C2S, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());
                    else
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.MAX_ALLD_BALANCE_P2P, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO.getReceiverAllServiceClassID());

                    if (serviceObjVal != null) {

                        TransferItemVO transferItemVO = p_transferVO.getReceiverTransferItemVO();
                        // Confirm for the Postpaid billpayment Credit limit or
                        // Balance.
                        if ((transferItemVO.getPreviousBalance() + p_transferVO.getRequestedAmount()) > ((Long) serviceObjVal).longValue()) {

                            // date 22/05/06
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType))
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.MAX_ALLD_BAL_LESS_REQ_AMOUNT_R_PRE, new String[] { getDisplayAmount((p_transferVO.getReceiverTransferItemVO().getPreviousBalance()) + p_transferVO.getRequestedAmount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                            else
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.MAX_ALLD_BAL_LESS_REQ_AMOUNT_R_POST, new String[] { getDisplayAmount((p_transferVO.getReceiverTransferItemVO().getPreviousBalance()) + p_transferVO.getRequestedAmount()), String.valueOf(((Long) serviceObjVal).longValue()) }));
                            String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), receiverVO.getMsisdn(), String.valueOf((p_transferVO.getReceiverTransferItemVO().getPreviousBalance()) + p_transferVO.getRequestedAmount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.REQ_AMT_EXCEEDS_MAX_ALLD_BAL_S, 0, strArr, null);
                        }
                    }
                } else// If Transaction is failed
                {
                    if (!BTSLUtil.isNullString(receiverVO.getInterfaceResponseCode())) {
                        // TO This needs to be done interface Type wise
                        String errorCodesForFail = BTSLUtil.NullToString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_FAIL_CTINCR_CODES));
                        if (BTSLUtil.isNullString(errorCodesForFail)) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateRecieverLimits]", p_transferVO.getTransferID(), receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Error codes for consideration in Fail cases not defined");
                        } else if (errorCodesForFail.indexOf(receiverVO.getInterfaceResponseCode()) >= 0) // Means
                                                                                                          // need
                                                                                                          // to
                                                                                                          // consider
                                                                                                          // in
                                                                                                          // fail
                                                                                                          // case,
                                                                                                          // then
                                                                                                          // increase
                                                                                                          // the
                                                                                                          // fail
                                                                                                          // count
                        {
                            receiverVO.setTotalFailCount(receiverVO.getTotalFailCount() + 1);
                        }
                    }
                    receiverVO.setLastTransferStage(String.valueOf(PretupsI.TRANS_STAGE_AFTER_INVAL));
                    receiverVO.setLastFailedOn(currentDate);

                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                }

            } else if (p_stage == PretupsI.TRANS_STAGE_AFTER_INTOP) {
                if (receiverVO.getTransactionStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS))// If
                                                                                                      // Transaction
                                                                                                      // is
                                                                                                      // success
                {
                    // Same for Ambiguous Case
                    receiverVO.setTotalConsecutiveFailCount(0);
                    receiverVO.setTotalSuccessCount(receiverVO.getTotalSuccessCount() + 1);
                    receiverVO.setTotalTransferAmount(receiverVO.getTotalTransferAmount() + p_transferVO.getRequestedAmount());
                    receiverVO.setLastTransferStage(PretupsI.TRANSACTION_SUCCESS_STATUS);
                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                    receiverVO.setLastSuccessOn(currentDate);
                    // Increment the weekly and monthly counts
                    receiverVO.setWeeklySuccCount(receiverVO.getWeeklySuccCount() + 1);
                    receiverVO.setWeeklyTransferAmount(receiverVO.getWeeklyTransferAmount() + p_transferVO.getRequestedAmount());
                    receiverVO.setMonthlySuccCount(receiverVO.getMonthlySuccCount() + 1);
                    receiverVO.setMonthlyTransferAmount(receiverVO.getMonthlyTransferAmount() + p_transferVO.getRequestedAmount());

                } else if (receiverVO.getTransactionStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_FAIL))// If
                                                                                                          // Transaction
                                                                                                          // is
                                                                                                          // faile
                {
                    if (!BTSLUtil.isNullString(receiverVO.getInterfaceResponseCode())) {
                        String errorCodesForFail = BTSLUtil.NullToString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_FAIL_CTINCR_CODES));
                        if (errorCodesForFail == null || BTSLUtil.isNullString(errorCodesForFail)) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateSenderLimits]", p_transferVO.getTransferID(), receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Error codes for consideration in Fail cases not defined");
                        } else if (!BTSLUtil.isNullString(errorCodesForFail) && (errorCodesForFail.indexOf(receiverVO.getInterfaceResponseCode()) >= 0)) // Means
                                                                                                                                                         // need
                                                                                                                                                         // to
                                                                                                                                                         // consider
                                                                                                                                                         // in
                                                                                                                                                         // fail
                                                                                                                                                         // case,
                                                                                                                                                         // then
                                                                                                                                                         // increase
                                                                                                                                                         // the
                                                                                                                                                         // fail
                                                                                                                                                         // count
                        {
                            receiverVO.setTotalFailCount(receiverVO.getTotalFailCount() + 1);
                        }
                    }
                    receiverVO.setLastTransferStage(String.valueOf(PretupsI.TRANSACTION_FAIL_STATUS));
                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                    receiverVO.setLastFailedOn(currentDate);
                } else {
                    // What to do in Ambiguous Case
                    receiverVO.setTotalConsecutiveFailCount(0);
                    receiverVO.setTotalSuccessCount(receiverVO.getTotalSuccessCount() + 1);
                    receiverVO.setTotalTransferAmount(receiverVO.getTotalTransferAmount() + p_transferVO.getRequestedAmount());
                    receiverVO.setLastTransferStage(SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS);
                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                    receiverVO.setLastSuccessOn(currentDate);
                    // Increment the weekly, monthly thresholds.
                    receiverVO.setWeeklySuccCount(receiverVO.getWeeklySuccCount() + 1);
                    receiverVO.setWeeklyTransferAmount(receiverVO.getWeeklyTransferAmount() + p_transferVO.getRequestedAmount());
                    receiverVO.setMonthlySuccCount(receiverVO.getMonthlySuccCount() + 1);
                    receiverVO.setMonthlyTransferAmount(receiverVO.getMonthlyTransferAmount() + p_transferVO.getRequestedAmount());

                }

            }
        } catch (BTSLBaseException bex) {
            // Comment this printStackTrace after dicussion with Sanjay Sir
            // bex.printStackTrace();
            _log.error("validateRecieverLimits", "BTSLBaseException :" + bex);
            throw bex;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateRecieverLimits", "Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateRecieverLimits]", p_transferVO.getTransferID(), receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }
    }

    /**
     * Gets the service status for the service type in network
     * 
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void validateNetworkService(TransferVO p_transferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateNetworkService", p_transferVO.getRequestID(), "Enetered Validation For Module Code=" + p_transferVO.getModule() + " Sender Network Code=" + p_transferVO.getNetworkCode() + " Receiver Network Code=" + p_transferVO.getReceiverNetworkCode() + "Service Type=" + p_transferVO.getServiceType());
        NetworkServiceVO networkServiceVO = (NetworkServiceVO) NetworkServicesCache.getObject(p_transferVO.getModule(), p_transferVO.getNetworkCode(), p_transferVO.getReceiverNetworkCode(), p_transferVO.getServiceType());
        if (networkServiceVO == null) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateNetworkService]", p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Service not defined between Sender Network " + p_transferVO.getNetworkCode() + " and Receiver network" + p_transferVO.getReceiverNetworkCode() + " Service Type=" + p_transferVO.getServiceType());
            throw new BTSLBaseException("PretupsBL", "validateNetworkService", SelfTopUpErrorCodesI.ERROR_NETWORK_SERVICE_STATUS_NOTEXIST, 0, new String[] { p_transferVO.getReceiverNetworkCode() }, null);
        } else if (!networkServiceVO.getStatus().equals(PretupsI.YES)) {
            // ChangeID=LOCALEMASTER
            // which language message to be send is determined from the locale
            // master
            if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_transferVO.getLocale())).getMessage()))
                p_transferVO.setSenderReturnMessage(networkServiceVO.getLanguage1Message());
            else
                p_transferVO.setSenderReturnMessage(networkServiceVO.getLanguage2Message());

            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateNetworkService]", p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Service Status is suspended between Sender Network " + p_transferVO.getNetworkCode() + " and Receiver network" + p_transferVO.getReceiverNetworkCode() + " Service Type=" + p_transferVO.getServiceType());
            throw new BTSLBaseException("PretupsBL", "validateNetworkService", SelfTopUpErrorCodesI.ERROR_NETWORK_SERVICE_STATUS_SUSPENDED, 0, new String[] { p_transferVO.getReceiverNetworkCode() }, null);
        }
        if (_log.isDebugEnabled())
            _log.debug("validateNetworkService", p_transferVO.getRequestID(), "Exiting");
    }

    /**
     * Validates the SKey details for the sender MSISDN and generates a new one
     * if required
     * 
     * @param con
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void generateSKey(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        try {
            SKeyTransferDAO sKeyTransferDAO = new SKeyTransferDAO();
            SKeyTransferVO skeyTransferVO = sKeyTransferDAO.loadSKeyTransferDetails(p_con, p_transferVO.getSenderMsisdn());
            int addCount = 0;
            if (skeyTransferVO != null) {
                // Check for expiry for SKey
                boolean isExpiredSKey = isExpiredSKey(skeyTransferVO, p_transferVO.getNetworkCode(), p_transferVO.getTransferDateTime());
                if (!isExpiredSKey) {
                    throw new BTSLBaseException("PretupsBL", "generateSKey", SelfTopUpErrorCodesI.SKEY_PREVIOUS_UNUSED);
                } else {
                    // Delete from SKey_transfer and move in History
                    skeyTransferVO.setPreviousStatus(skeyTransferVO.getStatus());
                    skeyTransferVO.setStatus(PretupsI.SKEY_STATUS_EXPIRED);
                    skeyTransferVO.setTransferStatus(p_transferVO.getTransferStatus());
                    skeyTransferVO.setTransferID(p_transferVO.getTransferID());
                    skeyTransferVO.setCreatedOn(p_transferVO.getCreatedOn());
                    int updateCount = sKeyTransferDAO.deleteSkeyTransferDetails(p_con, skeyTransferVO);
                    if (updateCount <= 0) {
                        throw new BTSLBaseException("PretupsBL", "generateSKey", SelfTopUpErrorCodesI.SKEY_PREVIOUS_NOTDELETE);
                    }

                }
            }
            // Continue processing, Generate SKey as no key found
            long sKey = generateFreshSKey();
            if (_log.isDebugEnabled())
                _log.debug("generateSKey", "SKey generated for msisdn=" + skeyTransferVO.getSenderMsisdn() + " sKey=" + sKey);
            p_transferVO.setSkey(sKey);

            skeyTransferVO = null;
            skeyTransferVO = prepareSKeyTransferVO(p_transferVO);
            if (skeyTransferVO != null) {
                addCount = 0;
                addCount = sKeyTransferDAO.addSKeyTransferDetails(p_con, skeyTransferVO);
                if (_log.isDebugEnabled())
                    _log.debug("generateSKey", "After adding in Transfer Details table=" + addCount);
                if (addCount <= 0)
                    throw new BTSLBaseException("PretupsBL", "generateSKey", SelfTopUpErrorCodesI.SKEY_NOTADDTRANSFER);
            }
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException("PretupsBL", "parseRequest", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    public static boolean isExpiredSKey(SKeyTransferVO p_skeyTransferVO, String p_networkCode, Date p_newTime) throws BTSLBaseException {
        boolean isExpire = false;
        if (_log.isDebugEnabled())
            _log.debug("isExpiredSKey", "Entered p_skeyTransferVO=" + p_skeyTransferVO.toString() + " p_networkCode=" + p_networkCode + " p_newTime:" + p_newTime);
        try {
            long previousTime = p_skeyTransferVO.getRequestOn().getTime();
            long currentRequestTime = p_newTime.getTime();
            long expiredInterval = SystemPreferences.SKEY_EXPIRY_TIME;
            long expiredIntInMilli = expiredInterval * 1000;
            if (_log.isDebugEnabled())
                _log.debug("isExpiredSKey", "previousTime=" + previousTime + " expiredInterval=" + expiredInterval + " expiredIntInMilli=" + expiredIntInMilli + " currentRequestTime=" + currentRequestTime);
            if (previousTime + expiredIntInMilli < currentRequestTime)
                isExpire = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException("PretupsBL", "isExpiredSKey", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("isExpiredSKey", "returning isExpire=" + isExpire);
        return isExpire;
    }

    /**
     * Prepares the SKey Transfer VO from the transfer VO for inserting in the
     * details table
     * 
     * @param p_transferVO
     * @return SKeyTransferVO
     */
    public static SKeyTransferVO prepareSKeyTransferVO(TransferVO p_transferVO) {
        if (_log.isDebugEnabled())
            _log.debug("prepareSKeyTransferVO", "Entered with p_transferVO");
        SKeyTransferVO skeyTransferVO = new SKeyTransferVO();
        skeyTransferVO.setSenderID(p_transferVO.getSenderID());
        skeyTransferVO.setSenderMsisdn(p_transferVO.getSenderMsisdn());
        skeyTransferVO.setModule(p_transferVO.getModule());
        if (p_transferVO.getModule().equals(PretupsI.C2S_MODULE))
            skeyTransferVO.setSenderType(((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryCode());
        else
            skeyTransferVO.setSenderType(((SenderVO) p_transferVO.getSenderVO()).getSubscriberType());
        skeyTransferVO.setSkey(p_transferVO.getSkey());
        skeyTransferVO.setRecieverMsisdn(p_transferVO.getReceiverMsisdn());
        skeyTransferVO.setTransferValue(p_transferVO.getTransferValue());
        skeyTransferVO.setPaymentMethod(p_transferVO.getPaymentMethodType());
        skeyTransferVO.setRequestDate(p_transferVO.getTransferDate());
        skeyTransferVO.setRequestOn(p_transferVO.getTransferDateTime());
        skeyTransferVO.setRequestBy(p_transferVO.getCreatedBy());
        skeyTransferVO.setSkeySentToMsisdn(p_transferVO.getSkeySentToMsisdn());
        skeyTransferVO.setBuddy(p_transferVO.getBuddy());
        skeyTransferVO.setDefaultPaymentMethod(p_transferVO.getDefaultPaymentMethod());
        if (_log.isDebugEnabled())
            _log.debug("prepareSKeyTransferVO", "Exiting with skeyTransferVO=" + skeyTransferVO.toString());
        return skeyTransferVO;
    }

    /**
     * Validate SKey
     * 
     * @param p_con
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void validateSKey(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateSKey", "Entered p_transferVO:" + p_transferVO);
        try {
            SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
            SKeyTransferDAO sKeyTransferDAO = new SKeyTransferDAO();
            SKeyTransferVO skeyTransferVO = sKeyTransferDAO.loadSKeyTransferDetails(p_con, senderVO.getMsisdn());
            if (skeyTransferVO != null) {
                if (skeyTransferVO.getSkey() != p_transferVO.getSkey()) {
                    throw new BTSLBaseException("PretupsBL", "validateSKey", SelfTopUpErrorCodesI.SKEY_INVALID);
                }
                // Check for expiry for SKey
                boolean isExpiredSKey = isExpiredSKey(skeyTransferVO, senderVO.getNetworkCode(), p_transferVO.getTransferDateTime());
                if (isExpiredSKey) {
                    throw new BTSLBaseException("PretupsBL", "validateSKey", SelfTopUpErrorCodesI.SKEY_EXPIRED);
                }
                if (skeyTransferVO.getBuddy().equals("Y")) {
                    BuddyVO buddyVO = new SubscriberDAO().loadBuddyDetails(p_con, ((SenderVO) p_transferVO.getSenderVO()).getUserID(), skeyTransferVO.getRecieverMsisdn());
                    p_transferVO.setReceiverVO(buddyVO);
                } else {
                    ReceiverVO _receiverVO = new ReceiverVO();
                    _receiverVO.setMsisdn(skeyTransferVO.getRecieverMsisdn());
                    p_transferVO.setReceiverVO(_receiverVO);
                }
                p_transferVO.setServiceType(skeyTransferVO.getServiceType());
                p_transferVO.setPaymentMethodType(skeyTransferVO.getPaymentMethod());
                p_transferVO.setTransferValue(skeyTransferVO.getTransferValue());
                p_transferVO.setSkeyGenerationTime(skeyTransferVO.getRequestDate());
                p_transferVO.setSkeySentToMsisdn(skeyTransferVO.getSkeySentToMsisdn());
                p_transferVO.setDefaultPaymentMethod(skeyTransferVO.getDefaultPaymentMethod());
                p_transferVO.setBuddy(skeyTransferVO.getBuddy());
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateSKey", p_transferVO.getTransferID(), "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateSKey]", p_transferVO.getTransferID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateSKey", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validateSKey", "Exiting p_transferVO:" + p_transferVO);
        }
    }

    public static void unBarredUserAutomaic(Connection con, String filteredMSISDN, String networkCode, String module, String type, ChannelUserVO channelUserVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("unBarredUserAutomaic", "Entered filteredMSISDN:" + filteredMSISDN + " networkCode:" + networkCode + " module:" + module + " type:" + type);
        ArrayList barredDetails = null;
        BarredUserVO barredUserVO = null;
        ArrayList otherBarredDetails = null;

        try {
            barredDetails = new ArrayList();
            otherBarredDetails = new ArrayList();
            long expiryTime = 0;
            barredDetails = _barredUserDAO.loadSingleBarredMsisdnDetails(con, module, networkCode, filteredMSISDN, type, null);
            if (null != barredDetails && barredDetails.size() > 0) {
                // find expiryperiod from system
                // for c2s user
                if (null != channelUserVO)
                    expiryTime = ((Long) PreferenceCache.getControlPreference(PreferenceI.C2S_PIN_BLK_EXP_DURATION, channelUserVO.getNetworkID(), channelUserVO.getCategoryCode())).longValue();
                else
                    // p2p user no category
                    expiryTime = SystemPreferences.P2P_PIN_BLK_EXP_DURATION;
                for (int i = 0, j = barredDetails.size(); i < j; i++) {
                    barredUserVO = (BarredUserVO) barredDetails.get(i);
                    if (PretupsI.BARRED_TYPE_PIN_INVALID.equalsIgnoreCase(barredUserVO.getBarredType())) {
                        // check expiry period
                        Date createdOn = barredUserVO.getCreatedOn();
                        if (BTSLUtil.isTimeExpired(createdOn, expiryTime))
                            _barredUserDAO.deleteSingleBarredMsisdn(con, module, networkCode, filteredMSISDN, type, barredUserVO.getBarredType());
                        else
                            otherBarredDetails.add(barredUserVO);
                    } else
                        otherBarredDetails.add(barredUserVO);

                }// for
                if (null != otherBarredDetails && otherBarredDetails.size() > 0) {
                    if (type.equals(PretupsI.USER_TYPE_RECEIVER)) {
                        throw new BTSLBaseException("PretupsBL", "unBarredUserAutomaic", SelfTopUpErrorCodesI.ERROR_RECEIVER_USERBARRED, 0, new String[] { filteredMSISDN }, null);
                    } else
                        throw new BTSLBaseException("PretupsBL", "unBarredUserAutomaic", SelfTopUpErrorCodesI.ERROR_USERBARRED);
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("unBarredUserAutomaic", "Exception filteredMSISDN:" + filteredMSISDN + " Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[unBarredUserAutomaic]", "", filteredMSISDN, networkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "unBarredUserAutomaic", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("unBarredUserAutomaic", "Exiting filteredMSISDN:" + filteredMSISDN + " barred Size:" + barredDetails.size());
        }
    }

}
