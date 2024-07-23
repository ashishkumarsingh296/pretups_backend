package com.btsl.pretups.util;

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
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.NotificationToOtherSystem;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.ota.util.SIMFileReader;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetDAO;
import com.btsl.pretups.cellidmgt.businesslogic.CellIdMgmtDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingCache;
import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayMappingCacheVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.businesslogic.RequestGatewayVO;
import com.btsl.pretups.gateway.util.GatewayParsersI;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeCountersVO;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeDAO;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileCache;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileVO;
import com.btsl.pretups.iat.businesslogic.IATCountryMasterCache;
import com.btsl.pretups.iat.businesslogic.IATCountryMasterVO;
import com.btsl.pretups.iat.businesslogic.IATNWServiceCache;
import com.btsl.pretups.iat.businesslogic.IATNetworkCountryMappingVO;
import com.btsl.pretups.iat.transfer.businesslogic.IATTransferItemVO;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyDAO;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyVO;
import com.btsl.pretups.inter.module.IATInterfaceHandlerI;
import com.btsl.pretups.logging.UnauthorizedAccessLog;
import com.btsl.pretups.loyalitystock.businesslogic.LoyalityStockTxnVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.NetworkServiceVO;
import com.btsl.pretups.master.businesslogic.NetworkServicesCache;
import com.btsl.pretups.master.businesslogic.ResponseInterfaceDetailVO;
import com.btsl.pretups.master.businesslogic.ServiceClassInfoByCodeCache;
import com.btsl.pretups.master.businesslogic.ServiceClassVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBuddiesDAO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.p2p.transfer.businesslogic.MCDListVO;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductCache;
import com.btsl.pretups.product.businesslogic.NetworkProductServiceTypeCache;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.receiver.FixedInformationVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberBL;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.RoutingVO;
import com.btsl.pretups.servicegpmgt.businesslogic.ServiceGpMgmtDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordUtil;
import com.btsl.pretups.skey.businesslogic.SKeyTransferDAO;
import com.btsl.pretups.skey.businesslogic.SKeyTransferVO;
import com.btsl.pretups.sos.businesslogic.SOSDAO;
import com.btsl.pretups.stk.Exception348;
import com.btsl.pretups.stk.Message348;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.subscriber.businesslogic.SubscriberControlDAO;
import com.btsl.pretups.transfer.businesslogic.TransferDAO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferRulesCache;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.whitelist.businesslogic.WhiteListDAO;
import com.btsl.pretups.whitelist.businesslogic.WhiteListVO;
import com.btsl.user.businesslogic.CellIdCache;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VoucherTypeVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.ibm.icu.util.Calendar;
import com.txn.pretups.routing.subscribermgmt.businesslogic.RoutingTxnDAO;

public class PretupsBL {
    private static Log LOG = LogFactory.getLog(PretupsBL.class.getName());
    private static BarredUserDAO _barredUserDAO = new BarredUserDAO();
    private static TransferDAO _transferDAO = new TransferDAO();
    private static SubscriberControlDAO _subscriberControlDAO = new SubscriberControlDAO();
    private static PosKeyDAO _posKeyDAO = new PosKeyDAO();
    public static OperatorUtilI _operatorUtil = null;
    private static NumberPortDAO _numberPortDAO = new NumberPortDAO();
    private static final String SQL_EXCEPTION = "SQL_EXCEPTION: ";
    private static final String EXCEPTION = "EXCEPTION: ";
    private static final String QUERY_KEY = "QUERY: ";
    // Loads operator specific class
   
    
    /**
    *  ensures no instantiation
    */
   private PretupsBL(){
    	
    }
    
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
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
        final String methodName = "checkMSISDNBarred";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: filteredMSISDN=");
        	loggerValue.append(filteredMSISDN);
        	loggerValue.append("networkCode=");
        	loggerValue.append(networkCode);
        	loggerValue.append("module=");
        	loggerValue.append(module);
        	loggerValue.append("type=");
        	loggerValue.append(type);
        	LOG.debug(methodName, loggerValue);
        }
        boolean barred = true;
        try {
            barred = _barredUserDAO.isExists(con, module, networkCode, filteredMSISDN, type, null);
            if (barred) {
                if (type.equals(PretupsI.USER_TYPE_RECEIVER)) {
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED, 0, new String[] { filteredMSISDN }, null);
                } else {
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_USERBARRED);
                }
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[checkMSISDNBarred]", "", filteredMSISDN,
                networkCode, loggerValue.toString());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: filteredMSISDN:");
             	loggerValue.append(filteredMSISDN);
             	loggerValue.append(" barred:");
             	loggerValue.append(barred);
             	LOG.debug(methodName, loggerValue);
             }
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
        final String methodName = "getNetworkDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: filteredMSISDN=");
        	loggerValue.append(p_filteredMSISDN);
        	loggerValue.append("p_subscriberType=");
        	loggerValue.append(p_subscriberType);
        	LOG.debug(methodName, loggerValue);
        }
        NetworkPrefixVO networkPrefixVO = null;
        if(BTSLUtil.isNullString(p_filteredMSISDN))
    	{
    		 throw new BTSLBaseException("PretupsBL",methodName, PretupsErrorCodesI.C2S_ERROR_INVALID_SENDER_MSISDN);
    	}
        try {
            final String msisdnPrefix = getMSISDNPrefix(p_filteredMSISDN);
            if (p_subscriberType.equals(PretupsI.USER_TYPE_SENDER)) {
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            } else {
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix, false);
            }

            if (networkPrefixVO == null && p_subscriberType.equals(PretupsI.USER_TYPE_SENDER)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND);
            } else if (networkPrefixVO == null && p_subscriberType.equals(PretupsI.USER_TYPE_RECEIVER)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_filteredMSISDN }, null);
            }
            return networkPrefixVO;
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getNetworkDetails]", "", p_filteredMSISDN,
                "", loggerValue.toString());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: networkPrefixVO:");
             	loggerValue.append(networkPrefixVO);
             	LOG.debug(methodName, loggerValue);
             }
        }
    }

    /**
     * Get MSISDN Prefix
     * 
     * @param filteredMSISDN
     * @return
     */
    public static String getMSISDNPrefix(String filteredMSISDN) {
    		
    	String methodName = "getMSISDNPrefix";
    	StringBuilder loggerValue= new StringBuilder();
         if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Entered: filteredMSISDN=");
         	loggerValue.append(filteredMSISDN);
         	LOG.debug(methodName, loggerValue);
         }
         Integer msisdnPrefixLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LENGTH_CODE);
        final String msisdnPrefix = filteredMSISDN.substring(0, (int)msisdnPrefixLength);
        if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Exiting: msisdnPrefix:");
         	loggerValue.append(msisdnPrefix);
         	LOG.debug(methodName, loggerValue);
         }
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
    	String methodName = "getFilteredMSISDN";
    	StringBuilder loggerValue= new StringBuilder();
         if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Entered: p_msisdn=");
         	loggerValue.append(p_msisdn);
         	LOG.debug(methodName, loggerValue);
         }
        // modified by Amit Raheja for NNP
        return _operatorUtil.getSystemFilteredMSISDN(p_msisdn);
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
    	String methodName = "parsePlainMessage";
    	StringBuilder loggerValue= new StringBuilder();
         if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Entered: p_message=");
         	loggerValue.append(p_message);
         	LOG.debug(methodName, loggerValue);
         }
        String p2pPlainSmsSeparator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR);
        String MESSAGE_SEP = p2pPlainSmsSeparator;
        if (BTSLUtil.isNullString(MESSAGE_SEP)) {
            MESSAGE_SEP = " ";
        }

        // String[] messageArray=p_message.split(MESSAGE_SEP)
        final String[] messageArray = BTSLUtil.split(p_message, MESSAGE_SEP);
        if (messageArray.length < 1) {
            throw new BTSLBaseException("PretupsBL", "parsePlainMessage", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("parsePlainMessage", "Exiting messageArray length=" + messageArray.length);
        }
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
        final String methodName = "getServiceKeywordHandlerObj";
    	StringBuilder loggerValue= new StringBuilder();
         if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Entered: handlerClassName=");
         	loggerValue.append(handlerClassName);
         	LOG.debug(methodName, loggerValue);
         }
        ServiceKeywordControllerI handlerObj = null;
        try {
            handlerObj = (ServiceKeywordControllerI) Class.forName(handlerClassName).newInstance();
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getServiceKeywordHandlerObj]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting");
        }
        return handlerObj;
        // return _smsHandlerObj
    }// end of getSmsHandlerObj

    /**
     * Validates the SKey details for the sender MSISDN and generates a new one
     * if required
     * 
     * @param con
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void generateSKey(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "generateSKey";
        StringBuilder loggerValue= new StringBuilder();
        
        try {
            final SKeyTransferDAO sKeyTransferDAO = new SKeyTransferDAO();
            SKeyTransferVO skeyTransferVO = sKeyTransferDAO.loadSKeyTransferDetails(p_con, p_transferVO.getSenderMsisdn());
            int addCount = 0;
            if (skeyTransferVO != null) {
                // Check for expiry for SKey
                final boolean isExpiredSKey = isExpiredSKey(skeyTransferVO, p_transferVO.getNetworkCode(), p_transferVO.getTransferDateTime());
                if (!isExpiredSKey) {
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.SKEY_PREVIOUS_UNUSED);
                } else {
                    // Delete from SKey_transfer and move in History
                    skeyTransferVO.setPreviousStatus(skeyTransferVO.getStatus());
                    skeyTransferVO.setStatus(PretupsI.SKEY_STATUS_EXPIRED);
                    skeyTransferVO.setTransferStatus(p_transferVO.getTransferStatus());
                    skeyTransferVO.setTransferID(p_transferVO.getTransferID());
                    skeyTransferVO.setCreatedOn(p_transferVO.getCreatedOn());
                    final int updateCount = sKeyTransferDAO.deleteSkeyTransferDetails(p_con, skeyTransferVO);
                    if (updateCount <= 0) {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.SKEY_PREVIOUS_NOTDELETE);
                    }

                }
            }
            // Continue processing, Generate SKey as no key found
            final long sKey = generateFreshSKey();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Entered: msisdn=");
            	loggerValue.append(skeyTransferVO.getSenderMsisdn());
            	loggerValue.append("sKey=");
            	loggerValue.append(sKey);
            	LOG.debug(methodName, loggerValue);
            }
            p_transferVO.setSkey(sKey);

            skeyTransferVO = null;
            skeyTransferVO = prepareSKeyTransferVO(p_transferVO);
            if (skeyTransferVO != null) {
                addCount = 0;
                addCount = sKeyTransferDAO.addSKeyTransferDetails(p_con, skeyTransferVO);
                if (LOG.isDebugEnabled()) {
                 	loggerValue.setLength(0);
                 	loggerValue.append("After adding in Transfer Details table:");
                 	loggerValue.append(addCount);
                 	LOG.debug(methodName, loggerValue);
                 }
                if (addCount <= 0) {
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.SKEY_NOTADDTRANSFER);
                }
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
    }

    /**
     * Checks whether the SKey previously stored for the sender MSISDN is
     * expired or not
     * 
     * @param p_skeyTransferVO
     * @param p_networkCode
     * @param p_newTime
     * @return boolean
     * @throws BTSLBaseException
     */
    public static boolean isExpiredSKey(SKeyTransferVO p_skeyTransferVO, String p_networkCode, Date p_newTime) throws BTSLBaseException {
        boolean isExpire = false;
        final String methodName = "isExpiredSKey";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_skeyTransferVO=");
        	loggerValue.append(p_skeyTransferVO);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_newTime=");
        	loggerValue.append(p_newTime);
        	LOG.debug(methodName, loggerValue);
        }
        try {
            final long previousTime = p_skeyTransferVO.getRequestOn().getTime();
            final long currentRequestTime = p_newTime.getTime();
            Long skeyExpiryTime = (Long) PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_EXPIRY_TIME_CODE);
            final long expiredInterval = (long)skeyExpiryTime;
            final long expiredIntInMilli = expiredInterval * 1000;
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("previousTime=");
            	loggerValue.append(previousTime);
            	loggerValue.append(",expiredInterval=");
            	loggerValue.append(expiredInterval);
            	loggerValue.append(",expiredIntInMilli=");
            	loggerValue.append(expiredIntInMilli);
            	loggerValue.append(",currentRequestTime=");
            	loggerValue.append(currentRequestTime);
            	LOG.debug(methodName, loggerValue);
            }
            if (previousTime + expiredIntInMilli < currentRequestTime) {
                isExpire = true;
            }
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Exiting: isExpire:");
         	loggerValue.append(isExpire);
         	LOG.debug(methodName, loggerValue);
         }
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
    	String methodName = "SKeyTransferVO";
    	StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_transferVO=");
        	loggerValue.append(p_transferVO);
        	LOG.debug(methodName, loggerValue);
        }
        final SKeyTransferVO skeyTransferVO = new SKeyTransferVO();
        skeyTransferVO.setSenderID(p_transferVO.getSenderID());
        skeyTransferVO.setSenderMsisdn(p_transferVO.getSenderMsisdn());
        skeyTransferVO.setModule(p_transferVO.getModule());
        if (p_transferVO.getModule().equals(PretupsI.C2S_MODULE)) {
            skeyTransferVO.setSenderType(((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryCode());
        } else {
            skeyTransferVO.setSenderType(((SenderVO) p_transferVO.getSenderVO()).getSubscriberType());
        }
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
        if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Exiting: skeyTransferVO:");
         	loggerValue.append(skeyTransferVO.toString());
         	LOG.debug(methodName, loggerValue);
         }
        return skeyTransferVO;
    }

    /**
     * Generates a skey whose length is system specific
     * 
     * @return long
     * @throws BTSLBaseException
     */
    public static long generateFreshSKey() throws BTSLBaseException {
        final String methodName = "generateFreshSKey";
    	StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered ");
        }
        final long currentTimeInMillis = System.currentTimeMillis();
        long sKey = 0;
        final String currentTimeInMillisStr = String.valueOf(currentTimeInMillis);
        Integer skeyLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_LENGTH_CODE);
        final String sKeyStr = currentTimeInMillisStr.substring(currentTimeInMillisStr.length() - (int)skeyLength, currentTimeInMillisStr.length());
        try {
            sKey = Long.parseLong(sKeyStr);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.SKEY_NOTCONVERTSTRTOLONG);
        }
        if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Exiting: sKey:");
         	loggerValue.append(sKey);
         	LOG.debug(methodName, loggerValue);
         }
        return sKey;
    }

    /**
     * Method to generate the Transfer ID
     * 
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void generateTransferID(TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "generateTransferID";
    	StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered ");
        }
        long newTransferID = 0;
        String transferID = null;
        try {
            final ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
            newTransferID = IDGenerator.getNextID(PretupsI.ID_GEN_P2P_TRANSFER_NO, BTSLUtil.getFinancialYearLastDigits(4), receiverVO.getNetworkCode(), p_transferVO
                .getCreatedOn());
            if (newTransferID == 0) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = _operatorUtil.formatP2PTransferID(p_transferVO, newTransferID);
            if (transferID == null) {
                throw new BTSLBaseException("PretupsBL", "generateC2STransferID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID,e);
        }
        if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Exiting: TransferID:");
         	loggerValue.append(transferID);
         	LOG.debug(methodName, loggerValue);
         }
    }

    /**
     * Get System Amount
     * 
     * @param p_amountStr
     * @return
     * @throws BTSLBaseException
     */
    public static long getSystemAmount(String p_amountStr) throws BTSLBaseException {
    	 final String methodName = "getSystemAmount";
    	 StringBuilder loggerValue= new StringBuilder();
         if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Entered: p_amountStr=");
         	loggerValue.append(p_amountStr);
         	LOG.debug(methodName, loggerValue);
         }
        final String METHOD_NAME = "getSystemAmount";
        long amount = 0;
        try {
            final double p_validAmount = Double.parseDouble(p_amountStr);
            amount = getSystemAmount(p_validAmount);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO,e);
        }
        if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Exiting: amount:");
         	loggerValue.append(amount);
         	LOG.debug(methodName, loggerValue);
         }
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
    	Integer amountMultFactor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
        final int multiplicationFactor = (int)amountMultFactor;
        long amount = 0;
        amount = Double.valueOf((Round((p_validAmount * multiplicationFactor), 2))).longValue();
        return amount;
    }


    
    /*public static double Round(double Rval, int Rpl) {
        final double p = Math.pow(10, Rpl);
        Rval = Rval * p;
        final double tmp = Math.round(Rval);
        return tmp / p;
    }*/
    
    /**
     * Method to round values till precision
     * 
     * @param Rval
     * @param Rpl
     * @return
     */
    public static double Round(double Rval, int Rpl) {
        final double p = Math.pow(10, Rpl);
        Rval = Rval * p;
        final double tmp = Math.round(Rval);
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
        // if(LOG.isDebugEnabled())LOG.debug("getDisplayAmount","Entered p_amount:"+p_amount)
        final String METHOD_NAME = "getDisplayAmount";
   	 	StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_amountStr=");
        	loggerValue.append(p_amount);
        	LOG.debug(METHOD_NAME, loggerValue);
        }
        Integer amountMultFactor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
        final int multiplicationFactor = amountMultFactor.intValue();
        final double amount = (double) p_amount / (double) multiplicationFactor;
        String amountStr = new DecimalFormat("#############.#####").format(amount);
        try {
            final long l = Long.parseLong(amountStr);
            amountStr = String.valueOf(l);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			/*LOG.error(METHOD_NAME, loggerValue);
    		  LOG.errorTrace(METHOD_NAME, e);*/
            amountStr = new DecimalFormat("############0.00#").format(amount);
        }
        // if(LOG.isDebugEnabled())LOG.debug("getDisplayAmount","Exiting display amount:"+amountStr)
        return amountStr;
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
        SenderVO senderVO = null;

        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
            restrictedMSISDN = (((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryVO().getRestrictedMsisdns());
        }
        final String methodName = "validateRecieverLimits";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_stage=");
        	loggerValue.append(p_stage);
        	loggerValue.append("p_moduleCode=");
        	loggerValue.append(p_moduleCode);
        	loggerValue.append("restrictedMSISDN=");
        	loggerValue.append(restrictedMSISDN);
        	loggerValue.append("p_transferVO.getReceiverSubscriberType=");
        	loggerValue.append(p_transferVO.getReceiverSubscriberType());
        	LOG.debug(methodName, loggerValue);
        }
        final ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
        Boolean mrpBlockTimeAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MRP_BLOCK_TIME_ALLOWED);
        String subscriberFailCtincrCodes = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_FAIL_CTINCR_CODES);
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
            final Date currentDate = p_transferVO.getCreatedOn();
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
            final String receiverSubscriberType = p_transferVO.getReceiverTransferItemVO() == null ? p_transferVO.getReceiverSubscriberType() : p_transferVO
                .getReceiverTransferItemVO().getSubscriberType();
            if (p_stage == PretupsI.TRANS_STAGE_BEFORE_INVAL) {
                boolean isValidated = false;
                if (PretupsI.STATUS_ACTIVE.equalsIgnoreCase(restrictedMSISDN)) {
                    if (PretupsI.STATUS_ACTIVE.equalsIgnoreCase((((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryVO()).getTransferToListOnly())) {
                        isValidated = RestrictedSubscriberBL.validateRestrictedSubscriberLimits(p_transferVO);
                        if (!isValidated) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                        }
                    }
                }

                service_class = p_transferVO.getReceiverAllServiceClassID();
				if (LOG.isDebugEnabled()){
					LOG.debug(methodName,"p_transferVO.getReceiverAllServiceClassID() = "+service_class);
				}
                if (service_class != null) {
                    if (receiverVO.getLastSuccessOn() != null) {
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE, receiverVO.getNetworkCode(), p_moduleCode, true,
                                p_transferVO.getReceiverAllServiceClassID());
                        } else {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_SUCCESS_REQUEST_BLOCK_SEC_CODE, receiverVO.getNetworkCode(), p_moduleCode,
                                true, p_transferVO.getReceiverAllServiceClassID());
                            if (LOG.isDebugEnabled()) {
                             	loggerValue.setLength(0);
                             	loggerValue.append("Exiting: receiverVO.getLastSuccessOn()!=null :");
                             	loggerValue.append(serviceObjVal);
                             	LOG.debug(methodName, loggerValue);
                             }
                        }
                        if (serviceObjVal != null) {
                        	if (LOG.isDebugEnabled()){
								LOG.debug(methodName,"If receiverVO.getLastSuccessOn()!=null "+((Long)serviceObjVal).longValue());
							}
                            if (((currentDate.getTime() - receiverVO.getLastSuccessOn().getTime()) / 1000) <= ((Long) serviceObjVal).longValue()) {
                                if (mrpBlockTimeAllowed) {
                                    Object serviceTypeObjVal = null;

                                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                    final String strArr[] = { String.valueOf(receiverVO.getSid()), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) };

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
                                    final boolean flag = (Boolean) serviceTypeObjVal;
                                    // Block need to implement::if
                                    // LAST_SERVICE_TYPE_CHECK is TRUE and
                                    // requested amount is equal to previous
                                    // amount and requested service type is
                                    // equal to previous service type, then
                                    // recharge is not allowed
                                    if (flag && p_transferVO.getRequestedAmount() == receiverVO.getLastMRP() && p_transferVO.getServiceType().equals(
                                        receiverVO.getLastServiceType())) {
                                        if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil
                                                .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        } else {
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil
                                                .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        }
                                        EventHandler.handle(EventIDI.REQ_BLOCKTIME,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateRecieverLimits]","","","","Reques MRP with service type Block Time.");
                                        
                                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MRP_BLOCK_TIME_WID_SERVICE_TYPE, 0, strArr, null);
                                    }
                                    // Block need to implement::if requested
                                    // amount is equal to previous amount, here
                                    // No service type check.
                                    else if (p_transferVO.getRequestedAmount() == receiverVO.getLastMRP()) {
                                        if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil
                                                .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        } else {
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil
                                                .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        }
                                        EventHandler.handle(EventIDI.REQ_BLOCKTIME,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateRecieverLimits]","","","","Reques MRP Block Time.");
                                        
                                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MRP_BLOCK_TIME, 0, strArr, null);
                                    }

                                } else {
                                	if (LOG.isDebugEnabled()){
										LOG.debug(methodName,"MRP_BLOCK_TIME_ALLOWED "+ mrpBlockTimeAllowed);
									}
                                    if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil
                                            .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                    } else {
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil
                                            .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                    }
                                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                    String strArr[]=null;
                                    if(!BTSLUtil.isNullString(receiverVO.getSid())){
                                    	strArr = new String []{ String.valueOf(receiverVO.getSid()), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) };
                                    } else {
                                    	strArr = new String []{ String.valueOf(receiverVO.getMsisdn()), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) };
                                    }
                                    receiverVO.setLastSuccTransBlockCheckDone(true);
                                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_S, 0, strArr, null);
                                }
                            }
                        }

                    }
                    serviceObjVal = null;
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED, receiverVO.getNetworkCode(), p_moduleCode, true,
                            p_transferVO.getReceiverAllServiceClassID());
                    } else {
                    	
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_DAILY_TOTAL_TXN_AMT_ALLOWED, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                .getReceiverAllServiceClassID());
                        	
                    }
                    if (serviceObjVal != null) {
                        receiverVO.setTotalTransAmtCheckDone(true);
                        // How to differentiate between this preference for
                        // sender or receiver
                        if (receiverVO.getTotalTransferAmount() + p_transferVO.getRequestedAmount() > ((Long) serviceObjVal).longValue()) {
                            // date 22/05/06
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                // for
                                // ID=SUBTYPVALRECLMT
                                // receiverSubscriberType
                                // that
                                // is
                                // set
                                // above
                                // is
                                // used
                                p_transferVO
                                    .setReceiverReturnMsg(new BTSLMessages(
                                        PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R_PRE,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                            .longValue()) }));
                                // p_transferVO.setReceiverReturnMsg(new
                                // BTSLMessages(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R,new
                                // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),getDisplayAmount(receiverVO.getTotalTransferAmount()),getDisplayAmount(((Long)serviceObjVal).longValue())}))
                            } else {
                                p_transferVO
                                    .setReceiverReturnMsg(new BTSLMessages(
                                        PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R_POST,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                            .longValue()) }));
                            }
                            String strArr[] = null;
                           
                       
                                strArr = new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getMsisdn()), getDisplayAmount(receiverVO
                                    .getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_S, 0, strArr, null);
                        }
                    }

                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class,false)
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT, receiverVO.getNetworkCode(), p_moduleCode, true,
                            p_transferVO.getReceiverAllServiceClassID());
                    } else {
                    	
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_DAILY_SUCCESS_TXN_ALLOWED_COUNT, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                .getReceiverAllServiceClassID());
                        	
                    	}
                    if (serviceObjVal != null) {
                        receiverVO.setNoOfSuccTransCheckDone(true);
                        // How to differentiate between this preference for
                        // sender or receiver
                        if (receiverVO.getTotalSuccessCount() >= ((Long) serviceObjVal).longValue()) {
                            // date 22/05/06
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                // for
                                // ID=SUBTYPVALRECLMT
                                // receiverSubscriberType
                                // that
                                // is
                                // set
                                // above
                                // is
                                // used
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_PRE,
                                    new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getTotalSuccessCount()), String
                                        .valueOf(((Long) serviceObjVal).longValue()) }));
                                // p_transferVO.setReceiverReturnMsg(new
                                // BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R,new
                                // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),String.valueOf(receiverVO.getTotalSuccessCount()),String.valueOf(((Long)serviceObjVal).longValue())}))
                            } else {
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_POST,
                                    new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getTotalSuccessCount()), String
                                        .valueOf(((Long) serviceObjVal).longValue()) }));
                            }
                            final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), String.valueOf(receiverVO
                                .getTotalSuccessCount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_S, 0, strArr, null);
                        }
                    }
                    // Check the weekly allowed transfer amount-Ashish K
                    // 07/09/07
                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED,receiverVO.getNetworkCode(),p_moduleCode,service_class,false)
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_TOTXN_AMT_ALLWDCO, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                            .getReceiverAllServiceClassID());
                    } else {
                    	
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_REC_AMT_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                .getReceiverAllServiceClassID());
                        	
                    }
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
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                // for
                                // ID=SUBTYPVALRECLMT
                                // receiverSubscriberType
                                // that
                                // is
                                // set
                                // above
                                // is
                                // used
                                p_transferVO
                                    .setReceiverReturnMsg(new BTSLMessages(
                                        PretupsErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_R_PRE,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getWeeklyTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                            .longValue()) }));
                                // p_transferVO.setReceiverReturnMsg(new
                                // BTSLMessages(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R,new
                                // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),getDisplayAmount(receiverVO.getTotalTransferAmount()),getDisplayAmount(((Long)serviceObjVal).longValue())}))
                            } else {
                                p_transferVO
                                    .setReceiverReturnMsg(new BTSLMessages(
                                        PretupsErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_R_POST,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getWeeklyTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                            .longValue()) }));
                            }
                            final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), getDisplayAmount(receiverVO
                                .getWeeklyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_S, 0, strArr, null);
                        }
                    }
                    // Check weekly transaction number
                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class,false)
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_SUCTRAN_ALLWDCOUN, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                            .getReceiverAllServiceClassID());
                    } else {
                    	
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_SUCTRAN_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                .getReceiverAllServiceClassID());
                        
                    }
                    if (serviceObjVal != null) {
                        receiverVO.setNoOfWeeklySuccTransCheckDone(true);
                        // How to differentiate between this preference for
                        // sender or receiver
                        if (receiverVO.getWeeklySuccCount() >= ((Long) serviceObjVal).longValue()) {
                            // date 22/05/06
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                // for
                                // ID=SUBTYPVALRECLMT
                                // receiverSubscriberType
                                // that
                                // is
                                // set
                                // above
                                // is
                                // used
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_R_PRE,
                                    new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getWeeklySuccCount()), String
                                        .valueOf(((Long) serviceObjVal).longValue()) }));
                                // p_transferVO.setReceiverReturnMsg(new
                                // BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R,new
                                // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),String.valueOf(receiverVO.getTotalSuccessCount()),String.valueOf(((Long)serviceObjVal).longValue())}))
                            } else {
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_R_POST,
                                    new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getWeeklySuccCount()), String
                                        .valueOf(((Long) serviceObjVal).longValue()) }));
                            }
                            final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), String.valueOf(receiverVO
                                .getWeeklySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_S, 0, strArr, null);
                        }
                    }
                    // Check the monthly allowed transfer amount-Ashish K
                    // 07/09/07
                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED,receiverVO.getNetworkCode(),p_moduleCode,service_class,false)
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_TOTXN_AMT_ALLWDCO, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                            .getReceiverAllServiceClassID());
                    } else {
                    	
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_REC_AMT_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                .getReceiverAllServiceClassID());
                        	}
                        	
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
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                // for
                                // ID=SUBTYPVALRECLMT
                                // receiverSubscriberType
                                // that
                                // is
                                // set
                                // above
                                // is
                                // used
                                p_transferVO
                                    .setReceiverReturnMsg(new BTSLMessages(
                                        PretupsErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_R_PRE,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                            .longValue()) }));
                                // p_transferVO.setReceiverReturnMsg(new
                                // BTSLMessages(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R,new
                                // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),getDisplayAmount(receiverVO.getTotalTransferAmount()),getDisplayAmount(((Long)serviceObjVal).longValue())}))
                            } else {
                                p_transferVO
                                    .setReceiverReturnMsg(new BTSLMessages(
                                        PretupsErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_R_POST,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                            .longValue()) }));
                            }
                            final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), getDisplayAmount(receiverVO
                                .getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_S, 0, strArr, null);
                        }
                    }
                    // Check monthly number of successfull transaction number
                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class,false)
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_SUCTRAN_ALLWDCOUN, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                            .getReceiverAllServiceClassID());
                    } else {
                    	
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_SUCTRAN_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                            .getReceiverAllServiceClassID());
                    	
                    }
                    if (serviceObjVal != null) {
                        receiverVO.setNoOfMonthlySuccTransCheckDone(true);
                        // How to differentiate between this preference for
                        // sender or receiver
                        if (receiverVO.getMonthlySuccCount() >= ((Long) serviceObjVal).longValue()) {
                            // date 22/05/06
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                // for
                                // ID=SUBTYPVALRECLMT
                                // receiverSubscriberType
                                // that
                                // is
                                // set
                                // above
                                // is
                                // used
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_R_PRE,
                                    new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getMonthlySuccCount()), String
                                        .valueOf(((Long) serviceObjVal).longValue()) }));
                                // p_transferVO.setReceiverReturnMsg(new
                                // BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R,new
                                // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),String.valueOf(receiverVO.getTotalSuccessCount()),String.valueOf(((Long)serviceObjVal).longValue())}))
                            } else {
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_R_POST,
                                    new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getMonthlySuccCount()), String
                                        .valueOf(((Long) serviceObjVal).longValue()) }));
                            }
                            final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), String.valueOf(receiverVO
                                .getMonthlySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_S, 0, strArr, null);
                        }
                    }
                }
            } else if (p_stage == PretupsI.TRANS_STAGE_AFTER_INVAL) {

                // check for the Number back service applicable
                if (p_transferVO.getReceiverTransferItemVO().isNumberBackAllowed()) {
                    final String daysDiff = p_transferVO.getServiceType() + PreferenceI.NUMBCK_ALWD_DAYS_DIFF;
                    final String deductedAmount = p_transferVO.getServiceType() + PreferenceI.NUMBCK_AMT_DEDCTED;

                    numbckAllowedDays = (String) PreferenceCache.getControlPreference(daysDiff, p_transferVO.getNetworkCode(), p_transferVO.getReceiverTransferItemVO()
                        .getInterfaceID());
                    numbckAmountDeducted = (String) PreferenceCache.getControlPreference(deductedAmount, p_transferVO.getNetworkCode(), p_transferVO
                        .getReceiverTransferItemVO().getInterfaceID());

                    // Number back allowed days can be SUSPENDED=20&DEACT=15 OR
                    // SUSPENDED=20 OR ONLY 20
                    if (numbckAllowedDays.contains("=")) {
                        if (numbckAllowedDays.contains("&")) {
                            numberOfDays = numbckAllowedDays.split("&");
                            final int size = numberOfDays.length;
                            for (int i = 0; i < size; i++) {
                                if (numberOfDays[i].contains(p_transferVO.getReceiverTransferItemVO().getAccountStatus())) {
                                    final String[] newNumberOfDays = numberOfDays[i].split("=");
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
                            final int size = amountDeducted.length;
                            for (int i = 0; i < size; i++) {
                                if (amountDeducted[i].contains(p_transferVO.getReceiverTransferItemVO().getAccountStatus())) {
                                    final String[] newAmountDeducted = amountDeducted[i].split("=");
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
                        dateStrGrace = (p_transferVO.getReceiverTransferItemVO().getPreviousGraceDate() == null) ? "0" : BTSLUtil.getDateStringFromDate(p_transferVO
                            .getReceiverTransferItemVO().getPreviousGraceDate());
                        graceDate = BTSLUtil.getDateFromDateString(dateStrGrace);
                    } catch (Exception e) {
                        LOG.errorTrace(methodName, e);
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.GRACE_DATE_IS_WRONG,e);
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
                     * ())
                     * 
                     * if(BTSLUtil.getDifferenceInUtilDates(graceDate,currentDate
                     * )>Integer.parseInt(numbckAllowedDays))
                     * throw new
                     * BTSLBaseException("PretupsBL","validateRecieverLimits"
                     * ,PretupsErrorCodesI.RECHARGE_IS_NOT_ALLOW)
                     * if(!(p_transferVO.getConvertedRequestedAmount()>
                     * Integer.parseInt(numbckAmountDeducted)))
                     * throw new
                     * BTSLBaseException("PretupsBL","validateRecieverLimits"
                     * ,PretupsErrorCodesI.RECHARGE_AMOUNT_IS_NOT_SUFFICIENT)
                     */

                    if (BTSLUtil.getDifferenceInUtilDates(graceDate, currentDate) > Integer.parseInt(numbckAllowedDays)) {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.RECHARGE_IS_NOT_ALLOW);
                    }
                    if (!(p_transferVO.getRequestedAmount() > Integer.parseInt(numbckAmountDeducted))) {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.RECHARGE_AMOUNT_IS_NOT_SUFFICIENT);
                        // End of single currency request change
                    }
                }

                if (receiverVO.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    boolean processPostBillPay = false;
                    if (PretupsI.SERIES_TYPE_POSTPAID.equalsIgnoreCase(receiverSubscriberType)) {
                        processPostBillPay = _operatorUtil.processPostBillPayment(p_transferVO.getRequestedAmount(), p_transferVO.getReceiverTransferItemVO()
                            .getPreviousBalance());

                        if (!processPostBillPay) {
                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_BAL_LESS_TO_REQ_AMT, new String[] { getDisplayAmount(p_transferVO
                                .getRequestedAmount()), getDisplayAmount(p_transferVO.getReceiverTransferItemVO().getPreviousBalance()) }));
                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            final String strArr[] = { String.valueOf(receiverVO.getSid()), getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(p_transferVO
                                .getReceiverTransferItemVO().getPreviousBalance()) };
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.REC_BAL_LESS_TO_REQ_AMT_S, 0, strArr, null);
                        }
                    }

                    service_class = receiverVO.getServiceClassCode();

                    if (!receiverVO.isLastSuccTransBlockCheckDone() && receiverVO.getLastSuccessOn() != null) {
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE,receiverVO.getNetworkCode(),p_moduleCode,service_class)
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE, receiverVO.getNetworkCode(), p_moduleCode, false,
                                p_transferVO.getReceiverAllServiceClassID());
                        } else {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_SUCCESS_REQUEST_BLOCK_SEC_CODE, receiverVO.getNetworkCode(), p_moduleCode,
                                false, p_transferVO.getReceiverAllServiceClassID());
                        }

                        if (serviceObjVal != null) {
                            if (((currentDate.getTime() - receiverVO.getLastSuccessOn().getTime()) / 1000) <= ((Long) serviceObjVal).longValue()) {
                                if (mrpBlockTimeAllowed) {
                                    Object serviceTypeObjVal = null;

                                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                    // String
                                    // strArr[]={String.valueOf(receiverVO.getSid()),BTSLUtil.roundToStr((((Long)serviceObjVal).longValue()/(double)60.0),2)}
                                    // added by Diwakar for fixing null value
                                    // for receiver MSISDN for OMG
                                    String strArr[] = null;
                                    if (!BTSLUtil.isNullString(receiverVO.getSid())) {
                                        strArr = new String[] { String.valueOf(receiverVO.getSid()), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) };
                                    } else {
                                        strArr = new String[] { receiverVO.getMsisdn(), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) };
                                    }

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
                                    final boolean flag = (Boolean) serviceTypeObjVal;
                                    // Block need to implement::if
                                    // LAST_SERVICE_TYPE_CHECK is TRUE and
                                    // requested amount is equal to previous
                                    // amount and requested service type is
                                    // equal to previous service type, then
                                    // recharge is not allowed
                                    if (flag && p_transferVO.getRequestedAmount() == receiverVO.getLastMRP() && p_transferVO.getServiceType().equals(
                                        receiverVO.getLastServiceType())) {
                                        if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil
                                                .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        } else {
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil
                                                .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        }
                                        EventHandler.handle(EventIDI.REQ_BLOCKTIME,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateRecieverLimits]","","","","Reques MRP with service type Block Time.");
                                        
                                        throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", PretupsErrorCodesI.MRP_BLOCK_TIME_WID_SERVICE_TYPE, 0, strArr, null);
                                    }
                                    // Block need to implement::if requested
                                    // amount is equal to previous amount, here
                                    // No service type check.
                                    else if (p_transferVO.getRequestedAmount() == receiverVO.getLastMRP()) {
                                        if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil
                                                .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        } else {
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil
                                                .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        }
                                        EventHandler.handle(EventIDI.REQ_BLOCKTIME,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateRecieverLimits]","","","","Reques MRP Block Time.");
                                        
                                        throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", PretupsErrorCodesI.MRP_BLOCK_TIME, 0, strArr, null);
                                    }

                                } else {

                                    // date 22/05/06
                                    if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil
                                            .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        // p_transferVO.setReceiverReturnMsg(new
                                        // BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R,new
                                        // String[]{BTSLUtil.roundToStr((((Long)serviceObjVal).longValue()/(double)60.0),2)}))
                                    } else {
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil
                                            .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                    }
                                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                    String strArr[] = null;
                                    if(!BTSLUtil.isNullString(receiverVO.getSid())){
                                    	strArr = new String[]{ String.valueOf(receiverVO.getSid()), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) };
                                    } else {
                                    	strArr = new String[]{ String.valueOf(receiverVO.getMsisdn()), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) };
                                    }
                                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_S, 0, strArr, null);
                                }
                            }
                        }
                    }

                    if (!receiverVO.isTotalTransAmtCheckDone()) {
                        serviceObjVal = null;
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED,receiverVO.getNetworkCode(),p_moduleCode,service_class)
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED, receiverVO.getNetworkCode(), p_moduleCode, false,
                                p_transferVO.getReceiverAllServiceClassID());
                        } else {
                        	
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_DAILY_TOTAL_TXN_AMT_ALLOWED, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                    .getReceiverAllServiceClassID());
                            	
                            	
                        	}

                        if (serviceObjVal != null) {
                            // How to differentiate between this preference for
                            // sender or receiver
                            if (receiverVO.getTotalTransferAmount() + p_transferVO.getRequestedAmount() > ((Long) serviceObjVal).longValue()) {
                                // date 22/05/06
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                    // for
                                    // ID=SUBTYPVALRECLMT
                                    // receiverSubscriberType
                                    // that
                                    // is
                                    // set
                                    // above
                                    // is
                                    // used
                                    p_transferVO
                                        .setReceiverReturnMsg(new BTSLMessages(
                                            PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R_PRE,
                                            new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                                .longValue()) }));
                                    // p_transferVO.setReceiverReturnMsg(new
                                    // BTSLMessages(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R,new
                                    // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),getDisplayAmount(receiverVO.getTotalTransferAmount()),getDisplayAmount(((Long)serviceObjVal).longValue())}))
                                } else {
                                    p_transferVO
                                        .setReceiverReturnMsg(new BTSLMessages(
                                            PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R_POST,
                                            new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                                .longValue()) }));
                                }
                                String strArr[] = null;
                                try {
                                    senderVO = (SenderVO) p_transferVO.getSenderVO();
                                } catch (RuntimeException e1) {
                                    LOG.errorTrace(methodName, e1);
                                }
                                if (senderVO != null) {
                                    strArr = new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(senderVO.getMsisdn()), getDisplayAmount(receiverVO
                                        .getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };
                                } else {
                                    strArr = new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getMsisdn()), getDisplayAmount(receiverVO
                                        .getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };
                                }
                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }

                    if (!receiverVO.isNoOfSuccTransCheckDone()) {
                        serviceObjVal = null;
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class)
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT, receiverVO.getNetworkCode(), p_moduleCode,
                                false, p_transferVO.getReceiverAllServiceClassID());
                        } else {
                        	
                                serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_DAILY_SUCCESS_TXN_ALLOWED_COUNT, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                    .getReceiverAllServiceClassID());
                            	
                            	
                        }

                        if (serviceObjVal != null) {
                            // How to differentiate between this preference for
                            // sender or receiver
                            if (receiverVO.getTotalSuccessCount() >= ((Long) serviceObjVal).longValue()) {
                                // date 22/05/06
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                    // for
                                    // ID=SUBTYPVALRECLMT
                                    // receiverSubscriberType
                                    // that
                                    // is
                                    // set
                                    // above
                                    // is
                                    // used
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_PRE,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getTotalSuccessCount()), String
                                            .valueOf(((Long) serviceObjVal).longValue()) }));
                                    // p_transferVO.setReceiverReturnMsg(new
                                    // BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R,new
                                    // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),String.valueOf(receiverVO.getTotalSuccessCount()),String.valueOf(((Long)serviceObjVal).longValue())}))
                                } else {
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_POST,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getTotalSuccessCount()), String
                                            .valueOf(((Long) serviceObjVal).longValue()) }));
                                }
                                final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), String.valueOf(receiverVO
                                    .getTotalSuccessCount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }

                    // Weekly total transaction amount check, if it is not
                    // checked before IN validation.
                    if (!receiverVO.isTotalWeeklyTransAmtCheckDone()) {
                        serviceObjVal = null;
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_TOTXN_AMT_ALLWDCO, receiverVO.getNetworkCode(), p_moduleCode, false,
                                p_transferVO.getReceiverAllServiceClassID());
                        } else {
                        	
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_REC_AMT_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                    .getReceiverAllServiceClassID());
                            	
                            	
                        	}
                        if (serviceObjVal != null) {
                            receiverVO.setTotalWeeklyTransAmtCheckDone(true);// Confirm
                            // the
                            // reason
                            // for
                            // this
                            // flag
                            if (receiverVO.getWeeklyTransferAmount() + p_transferVO.getRequestedAmount() > ((Long) serviceObjVal).longValue()) {
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                    // for
                                    // ID=SUBTYPVALRECLMT
                                    // receiverSubscriberType
                                    // that
                                    // is
                                    // set
                                    // above
                                    // is
                                    // used
                                    p_transferVO
                                        .setReceiverReturnMsg(new BTSLMessages(
                                            PretupsErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_R_PRE,
                                            new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getWeeklyTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                                .longValue()) }));
                                } else {
                                    p_transferVO
                                        .setReceiverReturnMsg(new BTSLMessages(
                                            PretupsErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_R_POST,
                                            new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getWeeklyTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                                .longValue()) }));
                                }
                                final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), getDisplayAmount(receiverVO
                                    .getWeeklyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };

                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }
                    // Check the weekly successful transaction number, if it is
                    // not checked before IN validation.
                    if (!receiverVO.isNoOfWeeklySuccTransCheckDone()) {
                        serviceObjVal = null;
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_SUCTRAN_ALLWDCOUN, receiverVO.getNetworkCode(), p_moduleCode, false,
                                p_transferVO.getReceiverAllServiceClassID());
                        } else {
                        	
                                serviceObjVal = getServiceClassObject(service_class, PreferenceI.WE_SUCTRAN_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                    .getReceiverAllServiceClassID());
                            	
                        }
                        if (serviceObjVal != null) {
                            receiverVO.setNoOfWeeklySuccTransCheckDone(true);
                            if (receiverVO.getWeeklySuccCount() >= ((Long) serviceObjVal).longValue()) {
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                    // for
                                    // ID=SUBTYPVALRECLMT
                                    // receiverSubscriberType
                                    // that
                                    // is
                                    // set
                                    // above
                                    // is
                                    // used
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_R_PRE,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getWeeklySuccCount()), String
                                            .valueOf(((Long) serviceObjVal).longValue()) }));
                                } else {
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_R_POST,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getWeeklySuccCount()), String
                                            .valueOf(((Long) serviceObjVal).longValue()) }));
                                }
                                final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), String.valueOf(receiverVO
                                    .getWeeklySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }
                    // Check the monthly total successful transfer amount, if it
                    // is not checked before IN validation.
                    if (!receiverVO.isTotalMonthlyTransAmtCheckDone()) {
                        serviceObjVal = null;
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED,receiverVO.getNetworkCode(),p_moduleCode,service_class,false)
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_TOTXN_AMT_ALLWDCO, receiverVO.getNetworkCode(), p_moduleCode, false,
                                p_transferVO.getReceiverAllServiceClassID());
                        } 
                        else{
                        		
                                serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_REC_AMT_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                        .getReceiverAllServiceClassID());
                                	
                        		}
                        
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
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                    // for
                                    // ID=SUBTYPVALRECLMT
                                    // receiverSubscriberType
                                    // that
                                    // is
                                    // set
                                    // above
                                    // is
                                    // used
                                    p_transferVO
                                        .setReceiverReturnMsg(new BTSLMessages(
                                            PretupsErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_R_PRE,
                                            new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                                .longValue()) }));
                                    // p_transferVO.setReceiverReturnMsg(new
                                    // BTSLMessages(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R,new
                                    // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),getDisplayAmount(receiverVO.getTotalTransferAmount()),getDisplayAmount(((Long)serviceObjVal).longValue())}))
                                } else {
                                    p_transferVO
                                        .setReceiverReturnMsg(new BTSLMessages(
                                            PretupsErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_R_POST,
                                            new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                                .longValue()) }));
                                }
                                final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), getDisplayAmount(receiverVO
                                    .getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };

                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }
                    // Check the monthly successful transaction number, if it is
                    // not checked before IN validation.
                    if (!receiverVO.isNoOfMonthlySuccTransCheckDone()) {
                        serviceObjVal = null;
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class,false)
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_SUCTRAN_ALLWDCOUN, receiverVO.getNetworkCode(), p_moduleCode, false,
                                p_transferVO.getReceiverAllServiceClassID());
                        } else {
                        	
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.MO_SUCTRAN_ALLWD_P2P, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                    .getReceiverAllServiceClassID());
                            	
                            	
                        	}
                        if (serviceObjVal != null) {
                            receiverVO.setNoOfMonthlySuccTransCheckDone(true);
                            // How to differentiate between this preference for
                            // sender or receiver
                            if (receiverVO.getMonthlySuccCount() >= ((Long) serviceObjVal).longValue()) {
                                // date 22/05/06
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                    // for
                                    // ID=SUBTYPVALRECLMT
                                    // receiverSubscriberType
                                    // that
                                    // is
                                    // set
                                    // above
                                    // is
                                    // used
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_R_PRE,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getMonthlySuccCount()), String
                                            .valueOf(((Long) serviceObjVal).longValue()) }));
                                    // p_transferVO.setReceiverReturnMsg(new
                                    // BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R,new
                                    // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),String.valueOf(receiverVO.getTotalSuccessCount()),String.valueOf(((Long)serviceObjVal).longValue())}))
                                } else {
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_R_POST,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getMonthlySuccCount()), String
                                            .valueOf(((Long) serviceObjVal).longValue()) }));
                                }
                                final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), String.valueOf(receiverVO
                                    .getMonthlySuccCount()), String.valueOf(((Long) serviceObjVal).longValue()) };

                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }
                    // Validate the Max allowed balance of the receiver
                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class,false)
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.MAX_ALLD_BALANCE_C2S, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO
                            .getReceiverAllServiceClassID());
                    } else {
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.MAX_ALLD_BALANCE_P2P, receiverVO.getNetworkCode(), p_moduleCode, false, p_transferVO
                            .getReceiverAllServiceClassID());
                    }

                    if (serviceObjVal != null) {

                        final TransferItemVO transferItemVO = p_transferVO.getReceiverTransferItemVO();
                        // Confirm for the Postpaid billpayment Credit limit or
                        // Balance.
                        if ((transferItemVO.getPreviousBalance() + p_transferVO.getRequestedAmount()) > ((Long) serviceObjVal).longValue()) {

                            // date 22/05/06
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                p_transferVO
                                    .setReceiverReturnMsg(new BTSLMessages(
                                        PretupsErrorCodesI.MAX_ALLD_BAL_LESS_REQ_AMOUNT_R_PRE,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()),p_transferVO.getReceiverMsisdn() ,getDisplayAmount((p_transferVO.getReceiverTransferItemVO().getPreviousBalance()) + p_transferVO.getRequestedAmount()),getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                            } else {
                                p_transferVO
                                    .setReceiverReturnMsg(new BTSLMessages(
                                        PretupsErrorCodesI.MAX_ALLD_BAL_LESS_REQ_AMOUNT_R_POST,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()),p_transferVO.getReceiverMsisdn() ,getDisplayAmount((p_transferVO.getReceiverTransferItemVO().getPreviousBalance()) + p_transferVO.getRequestedAmount()),getDisplayAmount(((Long) serviceObjVal).longValue()) }));
                            }
                            final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), getDisplayAmount((p_transferVO
                                .getReceiverTransferItemVO().getPreviousBalance()) + p_transferVO.getRequestedAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };

                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.REQ_AMT_EXCEEDS_MAX_ALLD_BAL_S, 0, strArr, null);
                        }
                    }
                } else// If Transaction is failed
                {
                    if (!BTSLUtil.isNullString(receiverVO.getInterfaceResponseCode())) {
                        // TO This needs to be done interface Type wise
                        final String errorCodesForFail = BTSLUtil.NullToString(subscriberFailCtincrCodes);
                        if (BTSLUtil.isNullString(errorCodesForFail)) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateRecieverLimits]",
                                p_transferVO.getTransferID(), receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Error codes for consideration in Fail cases not defined");
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
                if (receiverVO.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS))// If
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

                } else if (receiverVO.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL))// If
                // Transaction
                // is
                // faile
                {
                    if (!BTSLUtil.isNullString(receiverVO.getInterfaceResponseCode())) {
                        final String errorCodesForFail = BTSLUtil.NullToString(subscriberFailCtincrCodes);
                        if (errorCodesForFail == null || BTSLUtil.isNullString(errorCodesForFail)) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateSenderLimits]",
                                p_transferVO.getTransferID(), receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Error codes for consideration in Fail cases not defined");
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
                    receiverVO.setLastTransferStage(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
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
            LOG.errorTrace(methodName, bex);
            throw bex;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateRecieverLimits]", p_transferVO
                .getTransferID(), receiverVO.getMsisdn(), receiverVO.getNetworkCode(), loggerValue.toString());
            throw e;
        }
    }
    
    
    public static void validateSenderLimitsForGMB(Connection p_con, RequestVO p_requestVO,  SenderVO p_senderVO, String p_moduleCode) throws BTSLBaseException {
    	Object serviceObjVal = null;
    	final String methodName = "validateRecieverLimitsForGMB";
    	try{
    		serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE, p_senderVO.getNetworkCode(),PretupsI.SERVICE_TYPE_GIVE_ME_BALANCE );
            if (serviceObjVal != null) {
                if (p_senderVO.getDailyTransferAmount() + p_requestVO.getAmount1() > ((Long) serviceObjVal).longValue()) {
                	 final String strArr[] = { getDisplayAmount(p_requestVO.getAmount1()), null, getDisplayAmount(p_senderVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };
                    	throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R_PRE, 0, strArr, null); 
                }
            }
            serviceObjVal =null;
          serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.DAILY_MAX_TRFR_NUM_CODE, p_senderVO.getNetworkCode(),PretupsI.SERVICE_TYPE_GIVE_ME_BALANCE );
            if (serviceObjVal != null) {
                if (p_senderVO.getDailyTransferCount() + 1 > ((Integer) serviceObjVal).intValue()) {
                	 final String strArr[] = { getDisplayAmount(p_requestVO.getAmount1()), null, getDisplayAmount(p_senderVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };
                    	throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_PRE, 0, strArr, null); 
                }
            }
            serviceObjVal =null;
            serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.WEEKLY_MAX_TRFR_AMOUNT_CODE, p_senderVO.getNetworkCode(),PretupsI.SERVICE_TYPE_GIVE_ME_BALANCE );
            if (serviceObjVal != null) {
                if (p_senderVO.getWeeklyTransferAmount() + p_requestVO.getAmount1() > ((Long)serviceObjVal).longValue()) {
                	 final String strArr[] = { getDisplayAmount(p_requestVO.getAmount1()), null, getDisplayAmount(p_senderVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };
                    	throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_R_PRE, 0, strArr, null); 
                }
            }
            serviceObjVal =null;
            serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.WEEKLY_MAX_TRFR_NUM_CODE, p_senderVO.getNetworkCode(),PretupsI.SERVICE_TYPE_GIVE_ME_BALANCE);
            if (serviceObjVal != null) {
                if (p_senderVO.getWeeklyTransferCount() + 1 > ((Integer) serviceObjVal).intValue()) {
                	 final String strArr[] = { getDisplayAmount(p_requestVO.getAmount1()), null, getDisplayAmount(p_senderVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };
                    	throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_R_PRE, 0, strArr, null); 
                }
            }
            serviceObjVal =null;
            serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE, p_senderVO.getNetworkCode(),PretupsI.SERVICE_TYPE_GIVE_ME_BALANCE );
              if (serviceObjVal != null) {
                  if (p_senderVO.getMonthlyTransferAmount() + p_requestVO.getAmount1() > ((Long) serviceObjVal).longValue()) {
                  	 final String strArr[] = { getDisplayAmount(p_requestVO.getAmount1()), null, getDisplayAmount(p_senderVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };
                      	throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_S, 0, strArr, null); 
                  }
              }
              serviceObjVal = null;
              serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.MONTHLY_MAX_TRFR_NUM_CODE, p_senderVO.getNetworkCode(),PretupsI.SERVICE_TYPE_GIVE_ME_BALANCE );
              if (serviceObjVal != null) {
                  if (p_senderVO.getMonthlyTransferCount() + 1 > ((Integer)serviceObjVal).intValue()) {
                  	 final String strArr[] = { getDisplayAmount(p_requestVO.getAmount1()), null, getDisplayAmount(p_senderVO.getMonthlyTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()) };
                      	throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_R_PRE, 0, strArr, null); 
                  }
              }
    	}
    	catch (BTSLBaseException bex) {
            LOG.errorTrace(methodName, bex);
            throw bex;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            
        }
    	
    }

    /**
     * Add Transfer Details
     * 
     * @param p_con
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void addTransferDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String METHOD_NAME = "addTransferDetails";
        StringBuilder loggerValue= new StringBuilder();
        try {
            final int updateCount = _transferDAO.addTransferDetails(p_con, p_transferVO);
            if (updateCount <= 0) {
                throw new BTSLBaseException("PretupsBL", "addTransferDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[addTransferDetails]", p_transferVO
                .getTransferID(), "", "", loggerValue.toString());
            throw new BTSLBaseException("PretupsBL", "addTransferDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
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
        final String METHOD_NAME = "addValExtTransferDetails";
        StringBuilder loggerValue= new StringBuilder();
        try {
            final int updateCount = _transferDAO.addValExtTransferDetails(p_con, p_transferVO);
            if (updateCount <= 0) {
                throw new BTSLBaseException("PretupsBL", "addValExtTransferDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[addValExtTransferDetails]", p_transferVO
                .getTransferID(), "", "", loggerValue.toString());
            throw new BTSLBaseException("PretupsBL", "addValExtTransferDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
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
        final String METHOD_NAME = "updateTransferDetails";
        StringBuilder loggerValue= new StringBuilder();
        try {
            final int updateCount = _transferDAO.updateTransferDetails(p_con, p_transferVO);
            if (updateCount <= 0) {
                throw new BTSLBaseException("PretupsBL", "updateTransferDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[updateTransferDetails]", p_transferVO
                .getTransferID(), "", "", loggerValue.toString());
            throw new BTSLBaseException("PretupsBL", "updateTransferDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
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
        final String METHOD_NAME = "updateValExtTransferDetails";
        StringBuilder loggerValue= new StringBuilder();
        try {
            final int updateCount = _transferDAO.updateValExtTransferDetails(p_con, p_transferVO);
            if (updateCount <= 0) {
                throw new BTSLBaseException("PretupsBL", "updateValExtTransferDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[updateValExtTransferDetails]", p_transferVO
                .getTransferID(), "", "", loggerValue.toString());
            throw new BTSLBaseException("PretupsBL", "updateValExtTransferDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
    }

    /**
     * Genrate the pin
     * 
     * @return
     */
    public static String genratePin() {
        final StringBuffer sbf = new StringBuffer();
        sbf.append(System.currentTimeMillis());
        Integer pinLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_LENGTH_CODE);
        return sbf.substring(sbf.length() - ((int)pinLength), sbf.length());
    }

    /**
     * Get Network Prefix Details.This method loads network prefix details based
     * on msisdn prefix from memory.
     * 
     * @param filteredMSISDN
     * @return NetworkPrefixVO
     */
    public static NetworkPrefixVO getNetworkPrefixDetails(String filteredMSISDN) throws BTSLBaseException {
        final String methodName = "getNetworkPrefixDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: filteredMSISDN=");
        	loggerValue.append(filteredMSISDN);
        	LOG.debug(methodName, loggerValue);
        }
        NetworkPrefixVO networkPrefixVO = null;
        try {
            final String msisdnPrefix = getMSISDNPrefix(filteredMSISDN);
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_NETWORKPREFIX_NOTFOUND);
            }
            return networkPrefixVO;
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getNetworkPrefixDetails]", "",
                filteredMSISDN, "", loggerValue.toString());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: networkPrefixVO:");
             	loggerValue.append(networkPrefixVO);
             	LOG.debug(methodName, loggerValue);
             }
        }
    }

    /**
     * Validate SKey
     * 
     * @param p_con
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void validateSKey(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "validateSKey";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_transferVO=");
        	loggerValue.append(p_transferVO);
        	LOG.debug(methodName, loggerValue);
        }
        try {
            final SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
            final SKeyTransferDAO sKeyTransferDAO = new SKeyTransferDAO();
            final SKeyTransferVO skeyTransferVO = sKeyTransferDAO.loadSKeyTransferDetails(p_con, senderVO.getMsisdn());
            if (skeyTransferVO != null) {
                if (skeyTransferVO.getSkey() != p_transferVO.getSkey()) {
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.SKEY_INVALID);
                }
                // Check for expiry for SKey
                final boolean isExpiredSKey = isExpiredSKey(skeyTransferVO, senderVO.getNetworkCode(), p_transferVO.getTransferDateTime());
                if (isExpiredSKey) {
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.SKEY_EXPIRED);
                }
                if ("Y".equals(skeyTransferVO.getBuddy())) {
                    final BuddyVO buddyVO = new P2PBuddiesDAO().loadBuddyDetails(p_con, ((SenderVO) p_transferVO.getSenderVO()).getUserID(), skeyTransferVO
                        .getRecieverMsisdn());
                    p_transferVO.setReceiverVO(buddyVO);
                } else {
                    final ReceiverVO _receiverVO = new ReceiverVO();
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
           throw be ;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateSKey]",
                p_transferVO.getTransferID(), "", "", loggerValue.toString());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: p_transferVO:");
             	loggerValue.append(p_transferVO);
             	LOG.debug(methodName, loggerValue);
             }
        }
    }

    /**
     * Cancel Skey
     * 
     * @param p_con
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void cancelSKey(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "cancelSKey";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_transferVO=");
        	loggerValue.append(p_transferVO);
        	LOG.debug(methodName, loggerValue);
        }
        try {
            final SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
            final SKeyTransferDAO sKeyTransferDAO = new SKeyTransferDAO();
            final SKeyTransferVO skeyTransferVO = sKeyTransferDAO.loadSKeyTransferDetails(p_con, senderVO.getMsisdn());
            if (skeyTransferVO != null) {
                // Delete from SKey_transfer and move in History
                skeyTransferVO.setPreviousStatus(skeyTransferVO.getStatus());
                skeyTransferVO.setStatus(PretupsI.SKEY_STATUS_CANCELLED);
                skeyTransferVO.setTransferStatus(PretupsI.TRANSACTION_SUCCESS_STATUS);
                skeyTransferVO.setCreatedOn(p_transferVO.getCreatedOn());
                final int updateCount = sKeyTransferDAO.deleteSkeyTransferDetails(p_con, skeyTransferVO);
                if (updateCount <= 0) {
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.SKEY_PREVIOUS_NOTDELETE);
                }
                p_transferVO.setTransferStatus(PretupsI.SKEY_CANCEL_SUCCESS);
            } else {
                // Generate message that no key to cancel
                p_transferVO.setTransferStatus(PretupsI.NO_SKEY_TO_CANCEL_SUCCESS);
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[cancelSKey]", p_transferVO.getTransferID(),
                "", "", loggerValue.toString());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting");
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
        final String methodName = "loadSenderTransferStatus";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append("p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	LOG.debug(methodName, loggerValue);
        }
        long data[] = null;
        try {
            data = _transferDAO.loadTransferStatus(p_con, p_msisdn, BTSLUtil.getDateStringFromDate(p_fromDate), BTSLUtil.getDateStringFromDate(p_toDate));
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[loadSenderTransferStatus]", "", p_msisdn,
                "", loggerValue.toString());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("Exiting: data:");
         	loggerValue.append(data);
         	LOG.debug(methodName, loggerValue);
         }
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
        final String methodName = "validateTransferRule";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_transferVO.getTransferID(), "Entered");
        }
        TransferRulesVO transferRulesVO = null;
        String senderServiceClass = null;
        String recServiceClass = null;
        String cardGroupSetStatus = null;
        boolean isP2PPromotionalTrfRuleFound = false;
        Boolean isServiceProviderPromoAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);
        boolean isDefaultCardGroupExist = false;
        if (p_module.equals(PretupsI.P2P_MODULE)) {
            final SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
            final ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Validation For Network Code=");
            	loggerValue.append(senderVO.getNetworkCode());
            	loggerValue.append(" Sender Subscriber Type=");
            	loggerValue.append(senderVO.getSubscriberType());
            	loggerValue.append(" Receiver Subscriber Type=");
            	loggerValue.append(receiverVO.getSubscriberType());
            	loggerValue.append(" Sender Service Class ID=");
            	loggerValue.append(senderVO.getServiceClassCode());
            	loggerValue.append(" Receiver Service Class ID=");
            	loggerValue.append(receiverVO.getServiceClassCode());
            	loggerValue.append(" ");
            	loggerValue.append(PretupsI.NOT_APPLICABLE);
            	LOG.debug(methodName, loggerValue);
            }
            // check for the p2p promotional transfer rule applicable or not as network preference
           if (((Boolean) (PreferenceCache.getNetworkPrefrencesValue(PreferenceI.P2P_PROMOTIONAL_TRFRULE_CHECK, p_transferVO.getNetworkCode()))).booleanValue()) {

				if (LOG.isDebugEnabled()) {
		            LOG.debug(methodName, "P2P promotional transfer is applicable");
		        }
                int promotionalTransferRuleStartLevelCode = 1;
                // Find the p2p promotional transfer rule start level. IF not found
                // the consider it as 1.
                // 1 means user subscriber
                // 2 means grade cell id
               try {
                    promotionalTransferRuleStartLevelCode = ((Integer) (PreferenceCache.getNetworkPrefrencesValue(PreferenceI.P2P_PROMO_TRF_START_LVL_CODE, p_transferVO
                        .getNetworkCode()))).intValue();
                    
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "promotionalTransferRuleStartLevelCode", +promotionalTransferRuleStartLevelCode);
                    }
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    promotionalTransferRuleStartLevelCode = 1;
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", p_transferVO
                        .getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(),
                        "Promotional transfer rule starting level is either not defined or is not numeric in the preference so taking user as default");
                }
                // Load promotional transfer rules for the transfer date.
               	
               	final HashMap transferRuleMap = TransferRulesCache.loadPromotionalTransferRuleCache(p_transferVO.getTransferDateTime());
                final TransferItemVO receiverTransferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);
                
                String recAllServiceClassForPromotional = null;
               
                switch (promotionalTransferRuleStartLevelCode) {
				
					case PretupsI.P2P_PROMO_TRF_RULE_LVL_SUBSRIBER:
						 
						String subscriberMsisdn = ((SenderVO) p_transferVO.getSenderVO()).getMsisdn();
						if (LOG.isDebugEnabled()) {
				            LOG.debug(methodName, "p2p promotional transfer rule level for subscriber with msisdn" + subscriberMsisdn);
				        }
						
						// getting transferrulevo for request gateway code
						if (LOG.isDebugEnabled()) {
	                        LOG.debug(methodName, "key for particular gateway code is " , p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + subscriberMsisdn + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO
                                    .getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_SUBSCRIBER +  "_" + p_transferVO.getRequestGatewayCode());
	                    }
                            transferRulesVO = (TransferRulesVO) transferRuleMap
                                .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + subscriberMsisdn + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO
                                    .getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_SUBSCRIBER +  "_" + p_transferVO.getRequestGatewayCode());
                            
                        // getting transferrulevo for ALL gateway code
                        if(transferRulesVO == null)
							{		
							if (LOG.isDebugEnabled()) {
    	                        LOG.debug(methodName, "Key for ALL gateway code is " , p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + subscriberMsisdn + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO
                                        .getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_SUBSCRIBER +  "_" + PretupsI.ALL);
    	                    }
                                transferRulesVO = (TransferRulesVO) transferRuleMap
                                    .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + subscriberMsisdn + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO
                                            .getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_SUBSCRIBER +  "_" + PretupsI.ALL);    
							}
                            
                      if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                methodName,
                                p_transferVO.getTransferID(),
                                "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + subscriberMsisdn + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO.getSubscriberType() + " Receiver Service Class ID=" + receiverVO
                                    .getServiceClassCode() + " Sub Service=" + p_transferVO.getSubService() + " subscriber level promotional transfer rule");
                        }
                        if (transferRulesVO == null && !receiverVO.isUsingAllServiceClass()) {
                        	if (LOG.isDebugEnabled()) {
    				            LOG.debug(methodName, "receiverVO is UsingAllServiceClass() " + receiverVO.isUsingAllServiceClass());
    				        }
    						
                            try {
                            	
                            	recAllServiceClassForPromotional = validateServiceClass(p_con, p_transferVO.getReceiverMsisdn(), p_transferVO.getTransferID(),
                                    receiverTransferItemVO.getInterfaceID(), PretupsI.ALL, receiverTransferItemVO.getAccountStatus(),
                                    receiverTransferItemVO.getRequestValue(), receiverTransferItemVO.getUserType(), p_module);
                            	
                            } catch (Exception e) {
                                LOG.errorTrace(methodName, e);
                            }
                            if (!BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                            	
                            	if (LOG.isDebugEnabled()) {
        				            LOG.debug(methodName, "recAllServiceClassForPromotional value " + recAllServiceClassForPromotional);
        				            LOG.debug(methodName, "map key for particular gateway code " + p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + subscriberMsisdn + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO
                                            .getSubService() + "_" + PretupsI.P2P_PROMO_TRF_RULE_LVL_SUBSRIBER + "_"+ p_transferVO.getRequestGatewayCode());
                            	
                            	}
                            	
                            	transferRulesVO = (TransferRulesVO) transferRuleMap
                                    .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + subscriberMsisdn + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO
                                        .getSubService() + "_" + PretupsI.P2P_PROMO_TRF_RULE_LVL_SUBSRIBER + "_"+ p_transferVO.getRequestGatewayCode());
                                if(transferRulesVO == null)
                                {	
                                	if (LOG.isDebugEnabled()) {
            				            LOG.debug(methodName, "map key for particular gateway code " + p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + subscriberMsisdn + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO
                                                .getSubService() + "_" + PretupsI.P2P_PROMO_TRF_RULE_LVL_SUBSRIBER + "_"+ PretupsI.ALL );
                                	}
                                	
                                	transferRulesVO = (TransferRulesVO) transferRuleMap
                                            .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + subscriberMsisdn + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO
                                                .getSubService() + "_" + PretupsI.P2P_PROMO_TRF_RULE_LVL_SUBSRIBER + "_"+ PretupsI.ALL);
                                }
                            }
                        }
                       

                        if (transferRulesVO != null) {
	                        	if (LOG.isDebugEnabled()) {
	            		            LOG.debug(methodName, "P2P promotional transfer rule found for subscriber level with transferRulesVO "+transferRulesVO);
	            		        }
	                        	isP2PPromotionalTrfRuleFound = true;
	                            break;
                        }
				case PretupsI.P2P_PROMO_TRF_RULE_LVL_CELLGRP_CODE:
					
					String cellId = p_transferVO.getCellId();
					if (LOG.isDebugEnabled()) {
			            LOG.debug(methodName, "p2p promotional transfer rule level for CELL GROUP with cell id " + cellId);
			        }
					//get the cell grp from cellId
                    String cellGroup = new CellIdMgmtDAO().getCellGroupFromCellId(p_con, cellId);
                    
                    if (LOG.isDebugEnabled()) {
				            LOG.debug(methodName," p2p promotional transfer rule level for CELL GROUP with cellGroup "+ cellGroup);
				        }
                   
                    if (LOG.isDebugEnabled()) {
	                        LOG.debug(methodName, "cell group key value for request gateway " , p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO
	                                .getReceiverNetworkCode() + "_" + cellGroup + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO
	                                .getServiceClassCode() + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CELLGROUP + "_" + p_transferVO.getRequestGatewayCode());
	                    } 
                       transferRulesVO = (TransferRulesVO) transferRuleMap.get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO
                                .getReceiverNetworkCode() + "_" + cellGroup + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO
                                .getServiceClassCode() + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CELLGROUP + "_" + p_transferVO.getRequestGatewayCode());
                      
                       
                       if(transferRulesVO ==null)
                        {
                    	   if (LOG.isDebugEnabled()) {
   	                        LOG.debug(methodName, "cell group key value for ALL gateway " , p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO
                                    .getReceiverNetworkCode() + "_" + cellGroup + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO
                                    .getServiceClassCode() + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CELLGROUP + "_" + PretupsI.ALL);
   	                    	} 
                    	   
                    	   	transferRulesVO = (TransferRulesVO) transferRuleMap.get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO
                                    .getReceiverNetworkCode() + "_" + cellGroup + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO
                                    .getServiceClassCode() + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CELLGROUP + "_" + PretupsI.ALL);
                        
                        }
                        
                       if (transferRulesVO == null && !receiverVO.isUsingAllServiceClass()) {
                            if (BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                try {
                                    recAllServiceClassForPromotional = validateServiceClass(p_con, p_transferVO.getReceiverMsisdn(), p_transferVO.getTransferID(),
                                        receiverTransferItemVO.getInterfaceID(), PretupsI.ALL, receiverTransferItemVO.getAccountStatus(), receiverTransferItemVO
                                            .getRequestValue(), receiverTransferItemVO.getUserType(), p_module);
                                } catch (Exception e) {
                                    LOG.errorTrace(methodName, e);
                                }
                            }
                            if (!BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                            	
                            	if (LOG.isDebugEnabled()) {
           	                        LOG.debug(methodName, "recAllServiceClassForPromotional cell group key value for request gateway " , p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + cellGroup + "_" + receiverVO
                                            .getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CELLGROUP + "_"+p_transferVO.getRequestGatewayCode());
           	                    	} 
                            	
                            	
                                transferRulesVO = (TransferRulesVO) transferRuleMap
                                    .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + cellGroup + "_" + receiverVO
                                        .getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CELLGROUP + "_"+p_transferVO.getRequestGatewayCode());
                                if(transferRulesVO == null)
                                {
                                	if (LOG.isDebugEnabled()) {
               	                        LOG.debug(methodName, "recAllServiceClassForPromotional cell group key value for request gateway " , p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + cellGroup + "_" + receiverVO
                                                .getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CELLGROUP + "_"+PretupsI.ALL);
               	                    	}
                                		transferRulesVO = (TransferRulesVO) transferRuleMap
                                            .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + cellGroup + "_" + receiverVO
                                                .getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CELLGROUP + "_"+PretupsI.ALL );
                                }
                            }
                        }

                       	if (transferRulesVO != null) {
                            
                        	if (LOG.isDebugEnabled()) {
            		            LOG.debug(methodName, "promotional rule found for P2P cell id with transferRulesVO " +transferRulesVO);
            		        }
                            isP2PPromotionalTrfRuleFound = true;
                            break;
                        }

                    
                } // switch end
			}
			
			if (!isP2PPromotionalTrfRuleFound) {
			
				if (LOG.isDebugEnabled()) {
		            LOG.debug(methodName, "No promotional rule forund for P2P going for normal transfer rule");
		        }
				
            transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), receiverVO.getNetworkCode(), senderVO
                .getSubscriberType(), receiverVO.getSubscriberType(), senderVO.getServiceClassCode(), receiverVO.getServiceClassCode(), p_transferVO.getSubService(),
                PretupsI.NOT_APPLICABLE, p_transferVO.getRequestGatewayCode());
            if (transferRulesVO == null) {
                transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), receiverVO.getNetworkCode(),
                    senderVO.getSubscriberType(), receiverVO.getSubscriberType(), senderVO.getServiceClassCode(), receiverVO.getServiceClassCode(), p_transferVO
                        .getSubService(), PretupsI.NOT_APPLICABLE, PretupsI.ALL);
                // transferRulesVO=(TransferRulesVO)TransferRulesCache.getObject(p_transferVO.getServiceType(),p_transferVO.getModule(),receiverVO.getNetworkCode(),senderVO.getSubscriberType(),receiverVO.getSubscriberType(),senderVO.getServiceClassCode(),receiverVO.getServiceClassCode(),p_transferVO.getSubService(),PretupsI.NOT_APPLICABLE)
            }

            if (transferRulesVO == null && receiverVO.isUsingAllServiceClass() && senderVO.isUsingAllServiceClass()) {
                final CardGroupDAO cardGroupDAO = new CardGroupDAO();
                final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
                isDefaultCardGroupExist = cardGroupSetDAO.isDefaultCardGroupExist(p_con, p_transferVO);
                cardGroupSetStatus = p_transferVO.getStatus();
            } else if (transferRulesVO == null) {
                if (!senderVO.isUsingAllServiceClass()) {
                    final TransferItemVO senderTransferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(0);
                    try {
                        senderServiceClass = validateServiceClass(p_con, p_transferVO.getSenderMsisdn(), p_transferVO.getTransferID(), senderTransferItemVO.getInterfaceID(),
                            PretupsI.ALL, senderTransferItemVO.getAccountStatus(), senderTransferItemVO.getRequestValue(), senderTransferItemVO.getUserType(), p_module);
                    } catch (Exception e) {
                        LOG.errorTrace(methodName, e);
                    }
                } else {
                    senderServiceClass = senderVO.getServiceClassCode();
                }
                if (!BTSLUtil.isNullString(senderServiceClass)) {
                    transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), receiverVO.getNetworkCode(),
                        senderVO.getSubscriberType(), receiverVO.getSubscriberType(), senderServiceClass, receiverVO.getServiceClassCode(), p_transferVO.getSubService(),
                        PretupsI.NOT_APPLICABLE, p_transferVO.getRequestGatewayCode());
                }
                if (transferRulesVO == null) {
                    transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), receiverVO.getNetworkCode(),
                        senderVO.getSubscriberType(), receiverVO.getSubscriberType(), senderServiceClass, receiverVO.getServiceClassCode(), p_transferVO.getSubService(),
                        PretupsI.NOT_APPLICABLE, PretupsI.ALL);
                }
                // transferRulesVO=(TransferRulesVO)TransferRulesCache.getObject(p_transferVO.getServiceType(),p_transferVO.getModule(),receiverVO.getNetworkCode(),senderVO.getSubscriberType(),receiverVO.getSubscriberType(),senderServiceClass,receiverVO.getServiceClassCode(),p_transferVO.getSubService(),PretupsI.NOT_APPLICABLE)
                if (transferRulesVO == null) {
                    if (!receiverVO.isUsingAllServiceClass() && !senderVO.getServiceClassCode().equalsIgnoreCase(receiverVO.getServiceClassCode())) {
                        final TransferItemVO receiverTransferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);
                        try {
                            recServiceClass = validateServiceClass(p_con, p_transferVO.getReceiverMsisdn(), p_transferVO.getTransferID(), receiverTransferItemVO
                                .getInterfaceID(), PretupsI.ALL, receiverTransferItemVO.getAccountStatus(), receiverTransferItemVO.getRequestValue(), receiverTransferItemVO
                                .getUserType(), p_module);
                        } catch (Exception e) {
                            LOG.errorTrace(methodName, e);
                        }
                    } else if (!receiverVO.isUsingAllServiceClass() && !BTSLUtil.isNullString(senderServiceClass) && senderVO.getServiceClassCode().equalsIgnoreCase(
                        receiverVO.getServiceClassCode())) {
                        recServiceClass = senderServiceClass;
                    } else {
                        recServiceClass = receiverVO.getServiceClassCode();
                    }
                    if (!BTSLUtil.isNullString(recServiceClass)) {
                        transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), receiverVO.getNetworkCode(),
                            senderVO.getSubscriberType(), receiverVO.getSubscriberType(), senderVO.getServiceClassCode(), recServiceClass, p_transferVO.getSubService(),
                            PretupsI.NOT_APPLICABLE, p_transferVO.getRequestGatewayCode());
                    }
                    if (transferRulesVO == null) {
                        transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), receiverVO.getNetworkCode(),
                            senderVO.getSubscriberType(), receiverVO.getSubscriberType(), senderVO.getServiceClassCode(), recServiceClass, p_transferVO.getSubService(),
                            PretupsI.NOT_APPLICABLE, PretupsI.ALL);
                        // transferRulesVO=(TransferRulesVO)TransferRulesCache.getObject(p_transferVO.getServiceType(),p_transferVO.getModule(),receiverVO.getNetworkCode(),senderVO.getSubscriberType(),receiverVO.getSubscriberType(),senderVO.getServiceClassCode(),recServiceClass,p_transferVO.getSubService(),PretupsI.NOT_APPLICABLE)
                    }
                }
                if (transferRulesVO == null && !BTSLUtil.isNullString(senderServiceClass) && !BTSLUtil.isNullString(recServiceClass)) {
                    transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), receiverVO.getNetworkCode(),
                        senderVO.getSubscriberType(), receiverVO.getSubscriberType(), senderServiceClass, recServiceClass, p_transferVO.getSubService(),
                        PretupsI.NOT_APPLICABLE, p_transferVO.getRequestGatewayCode());
                }
                if (transferRulesVO == null) {
                    transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), receiverVO.getNetworkCode(),
                        senderVO.getSubscriberType(), receiverVO.getSubscriberType(), senderServiceClass, recServiceClass, p_transferVO.getSubService(),
                        PretupsI.NOT_APPLICABLE, PretupsI.ALL);
                }
                // transferRulesVO=(TransferRulesVO)TransferRulesCache.getObject(p_transferVO.getServiceType(),p_transferVO.getModule(),receiverVO.getNetworkCode(),senderVO.getSubscriberType(),receiverVO.getSubscriberType(),senderServiceClass,recServiceClass,p_transferVO.getSubService(),PretupsI.NOT_APPLICABLE)
                if (transferRulesVO == null) {
                    final CardGroupDAO cardGroupDAO = new CardGroupDAO();
                    final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
                    isDefaultCardGroupExist = cardGroupSetDAO.isDefaultCardGroupExist(p_con, p_transferVO);
                    cardGroupSetStatus = p_transferVO.getStatus();
                }
            }
            if (transferRulesVO == null && !isDefaultCardGroupExist) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", p_transferVO
                    .getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Default card group not exist for the service type " + p_transferVO
                    .getServiceType() + " with  Sub Service=" + p_transferVO.getSubService());
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_DEFAULT_CARDGROUP_NOTEXIST);
            }
			}
            if (transferRulesVO != null && transferRulesVO.getStatus().equals(PretupsI.SUSPEND)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", p_transferVO
                    .getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Transfer Rule is suspended in P2P between " + senderVO
                    .getServiceClassCode() + " and " + receiverVO.getServiceClassCode() + " sender service class code=" + p_transferVO.getSenderTransferItemVO()
                    .getServiceClassCode() + " and receiver service class code=" + p_transferVO.getReceiverTransferItemVO().getServiceClassCode());
                String messageKey = PretupsI.VOUCHER_CONS_SERVICE.equals(p_transferVO.getServiceType()) ? PretupsErrorCodesI.VOUCHER_ERROR_TRANSFER_RULE_SUSPENDED : 
                	PretupsErrorCodesI.P2P_ERROR_TRANSFER_RULE_SUSPENDED;
                throw new BTSLBaseException("PretupsBL", methodName, messageKey);
            }
        } else {
            // For C2S: Sender service class will be ALL and subscriber type
            // will be Domain ID
            final ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
            boolean isPromotionalTrfRuleFound = false;
            String spGroup = null;
            String spName = null;
            // If network preference enables the promotional transfer rules then
            // check that
            if (((Boolean) (PreferenceCache.getNetworkPrefrencesValue(PreferenceI.C2S_PROMOTIONAL_TRFRULE_CHECK, p_transferVO.getNetworkCode()))).booleanValue()) {

                int promotionalTransferRuleStartLevelCode = 1;
                // Find the promotional transfer rule start level. IF not found
                // the consider it as 1.
                // 1 means user level
                // 2 means grade level
                // 3 means category level
                // 4 means geography level
                // 5 prefix level
                // 6 cell group level
                // 7 service provider group level
                try {
                    promotionalTransferRuleStartLevelCode = ((Integer) (PreferenceCache.getNetworkPrefrencesValue(PreferenceI.PROMO_TRF_START_LVL_CODE, p_transferVO
                        .getNetworkCode()))).intValue();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    promotionalTransferRuleStartLevelCode = 1;
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", p_transferVO
                        .getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(),
                        "Promotional transfer rule starting level is either not defined or is not numeric in the preference so taking user as default");
                }
                // Load promotional transfer rules for the transfer date.
                // HashMap
                // transferRuleMap=transferDAO.loadPromotionalTransferRuleMap(p_con,p_transferVO.getTransferDateTime())
                final HashMap transferRuleMap = TransferRulesCache.loadPromotionalTransferRuleCache(p_transferVO.getTransferDateTime());
                final TransferItemVO receiverTransferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);
                String recAllServiceClassForPromotional = null;
                if (isServiceProviderPromoAllow) {
                    spName = p_transferVO.getServiceProviderName();
                    spGroup = new ServiceGpMgmtDAO().getServiceGroupFromServiceId(p_con, spName);
                }
                switch (promotionalTransferRuleStartLevelCode) {
                    case PretupsI.PROMO_TRF_RULE_LVL_USR_CODE:
                        // transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getUserID()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMO_TRF_RULE_LVL_USR_CODE)
                        if (!isServiceProviderPromoAllow) {
                            transferRulesVO = (TransferRulesVO) transferRuleMap
                                .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                    .getSenderVO()).getUserID() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO
                                    .getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_USER);
                        } else {
                            transferRulesVO = (TransferRulesVO) transferRuleMap
                                .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                    .getSenderVO()).getUserID() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO
                                    .getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_USER + "_" + spGroup + "_" + receiverTransferItemVO.getAccountStatus());
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                methodName,
                                p_transferVO.getTransferID(),
                                "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO.getSenderVO())
                                    .getUserID() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO.getSubscriberType() + " Receiver Service Class ID=" + receiverVO
                                    .getServiceClassCode() + " Sub Service=" + p_transferVO.getSubService() + " user level promotional transfer rule");
                        }
                        if (transferRulesVO == null && !receiverVO.isUsingAllServiceClass()) {
                            try {
                                recAllServiceClassForPromotional = validateServiceClass(p_con, p_transferVO.getReceiverMsisdn(), p_transferVO.getTransferID(),
                                    receiverTransferItemVO.getInterfaceID(), PretupsI.ALL, receiverTransferItemVO.getAccountStatus(),
                                    receiverTransferItemVO.getRequestValue(), receiverTransferItemVO.getUserType(), p_module);
                            } catch (Exception e) {
                                LOG.errorTrace(methodName, e);
                            }
                            if (!BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                transferRulesVO = (TransferRulesVO) transferRuleMap
                                    .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                        .getSenderVO()).getUserID() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO
                                        .getSubService() + "_" + PretupsI.PROMO_TRF_RULE_LVL_USR_CODE);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(methodName, p_transferVO.getTransferID(),
                                        "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO
                                            .getSenderVO()).getUserID() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO
                                            .getSubscriberType() + " Receiver Service Class ID=" + recAllServiceClassForPromotional + " Sub Service=" + p_transferVO
                                            .getSubService() + " user level promotional transfer rule");
                                }
                            }
                        }
                        if (transferRulesVO == null && isServiceProviderPromoAllow)// condition
                        // mod
                        // by
                        // rahul
                        // for
                        // serv
                        // provdr
                        // grp
                        // preference
                        {
                            transferRulesVO = getPromotionalTransferRule(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO.getReceiverNetworkCode(),
                                ((ChannelUserVO) p_transferVO.getSenderVO()).getUserID(), receiverVO.getSubscriberType(), PretupsI.ALL, receiverVO.getServiceClassCode(),
                                recAllServiceClassForPromotional, p_transferVO.getSubService(), PretupsI.PROMOTIONAL_LEVEL_USER, spGroup, receiverTransferItemVO
                                    .getAccountStatus(), "user level promotional transfer rule", transferRuleMap, p_transferVO.getTransferID(), p_transferVO.getNetworkCode());
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    methodName,
                                    p_transferVO.getTransferID(),
                                    "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO.getSenderVO())
                                        .getUserID() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO.getSubscriberType() + " Receiver Service Class ID=" + recAllServiceClassForPromotional + " Sub Service=" + p_transferVO
                                        .getSubService() + " user level promotional transfer rule");
                            }
                        }

                        if (transferRulesVO != null) {
                            /*
                             * isExist=isPromotionalRuleExistInRange(transferRulesVO
                             * ,
                             * p_transferVO.getTransferDateTime());
                             * if(isExist)
                             * isPromotionalTrfRuleFound=true;
                             * else
                             */
                            isPromotionalTrfRuleFound = true;
                            break;
                        }
                    case PretupsI.PROMO_TRF_RULE_LVL_GRADE_CODE:
                        // transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getUserGrade()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMO_TRF_RULE_LVL_GRADE_CODE)
                        if (!isServiceProviderPromoAllow)

                        {
                            transferRulesVO = (TransferRulesVO) transferRuleMap
                                .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                    .getSenderVO()).getUserGrade() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO
                                    .getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_GRADE);
                        } else {
                            transferRulesVO = (TransferRulesVO) transferRuleMap
                                .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                    .getSenderVO()).getUserGrade() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO
                                    .getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_GRADE + "_" + spGroup + "_" + receiverTransferItemVO.getAccountStatus());
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                methodName,
                                p_transferVO.getTransferID(),
                                "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO.getSenderVO())
                                    .getUserGrade() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO.getSubscriberType() + " Receiver Service Class ID=" + receiverVO
                                    .getServiceClassCode() + " Sub Service=" + p_transferVO.getSubService() + " grade level promotional transfer rule");
                        }
                        if (transferRulesVO == null && !receiverVO.isUsingAllServiceClass()) {
                            if (BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                try {
                                    recAllServiceClassForPromotional = validateServiceClass(p_con, p_transferVO.getReceiverMsisdn(), p_transferVO.getTransferID(),
                                        receiverTransferItemVO.getInterfaceID(), PretupsI.ALL, receiverTransferItemVO.getAccountStatus(), receiverTransferItemVO
                                            .getRequestValue(), receiverTransferItemVO.getUserType(), p_module);
                                } catch (Exception e) {
                                    LOG.errorTrace(methodName, e);
                                }
                            }
                            if (!BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                transferRulesVO = (TransferRulesVO) transferRuleMap
                                    .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                        .getSenderVO()).getUserGrade() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO
                                        .getSubService() + "_" + PretupsI.PROMO_TRF_RULE_LVL_GRADE_CODE);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(methodName, p_transferVO.getTransferID(),
                                        "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO
                                            .getSenderVO()).getUserGrade() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO
                                            .getSubscriberType() + " Receiver Service Class ID=" + recAllServiceClassForPromotional + " Sub Service=" + p_transferVO
                                            .getSubService() + " grade level promotional transfer rule");
                                }
                            }
                        }
                        if (transferRulesVO == null && isServiceProviderPromoAllow)// condition
                        // mod
                        // by
                        // rahul
                        // for
                        // serv
                        // provdr
                        // grp
                        // preference
                        {
                            transferRulesVO = getPromotionalTransferRule(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO.getReceiverNetworkCode(),
                                ((ChannelUserVO) p_transferVO.getSenderVO()).getUserGrade(), receiverVO.getSubscriberType(), PretupsI.ALL, receiverVO.getServiceClassCode(),
                                recAllServiceClassForPromotional, p_transferVO.getSubService(), PretupsI.PROMOTIONAL_LEVEL_GRADE, spGroup, receiverTransferItemVO
                                    .getAccountStatus(), "grade level promotional transfer rule", transferRuleMap, p_transferVO.getTransferID(), p_transferVO.getNetworkCode());
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    methodName,
                                    p_transferVO.getTransferID(),
                                    "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO.getSenderVO())
                                        .getUserGrade() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO.getSubscriberType() + " Receiver Service Class ID=" + recAllServiceClassForPromotional + " Sub Service=" + p_transferVO
                                        .getSubService() + " grade level promotional transfer rule");
                            }
                        }
                        if (transferRulesVO != null) {
                            /*
                             * isExist=isPromotionalRuleExistInRange(transferRulesVO
                             * ,
                             * p_transferVO.getTransferDateTime());if(isExist)
                             * if(isExist)
                             * isPromotionalTrfRuleFound=true
                             * else
                             */
                            isPromotionalTrfRuleFound = true;
                            break;
                        }
                    case PretupsI.PROMO_TRF_RULE_LVL_CATEGORY_CODE:
                        // transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getCategoryCode()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMO_TRF_RULE_LVL_CATEGORY_CODE)
                        if (!isServiceProviderPromoAllow) {
                            transferRulesVO = (TransferRulesVO) transferRuleMap
                                .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                    .getSenderVO()).getCategoryCode() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO
                                    .getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CATEGORY);

                        } else {
                            transferRulesVO = (TransferRulesVO) transferRuleMap
                                .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                    .getSenderVO()).getCategoryCode() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO
                                    .getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CATEGORY + "_" + spGroup + "_" + receiverTransferItemVO.getAccountStatus());
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                methodName,
                                p_transferVO.getTransferID(),
                                "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO.getSenderVO())
                                    .getCategoryCode() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO.getSubscriberType() + " Receiver Service Class ID=" + receiverVO
                                    .getServiceClassCode() + " Sub Service=" + p_transferVO.getSubService() + " Category level promotional transfer rule");
                        }
                        if (transferRulesVO == null && !receiverVO.isUsingAllServiceClass()) {
                            if (BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                try {
                                    recAllServiceClassForPromotional = validateServiceClass(p_con, p_transferVO.getReceiverMsisdn(), p_transferVO.getTransferID(),
                                        receiverTransferItemVO.getInterfaceID(), PretupsI.ALL, receiverTransferItemVO.getAccountStatus(), receiverTransferItemVO
                                            .getRequestValue(), receiverTransferItemVO.getUserType(), p_module);
                                } catch (Exception e) {
                                    LOG.errorTrace(methodName, e);
                                }
                            }
                            if (!BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                transferRulesVO = (TransferRulesVO) transferRuleMap
                                    .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                        .getSenderVO()).getCategoryCode() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO
                                        .getSubService() + "_" + PretupsI.PROMO_TRF_RULE_LVL_CATEGORY_CODE);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(methodName, p_transferVO.getTransferID(),
                                        "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO
                                            .getSenderVO()).getCategoryCode() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO
                                            .getSubscriberType() + " Receiver Service Class ID=" + recAllServiceClassForPromotional + " Sub Service=" + p_transferVO
                                            .getSubService() + " Category level promotional transfer rule");
                                }
                            }
                        }
                        if (transferRulesVO == null && isServiceProviderPromoAllow)// condition
                        // mod
                        // by
                        // rahul
                        // for
                        // serv
                        // provdr
                        // grp
                        // preference
                        {
                            transferRulesVO = getPromotionalTransferRule(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO.getReceiverNetworkCode(),
                                ((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryCode(), receiverVO.getSubscriberType(), PretupsI.ALL,
                                receiverVO.getServiceClassCode(), recAllServiceClassForPromotional, p_transferVO.getSubService(), PretupsI.PROMOTIONAL_LEVEL_CATEGORY,
                                spGroup, receiverTransferItemVO.getAccountStatus(), " Category level promotional transfer rule", transferRuleMap,
                                p_transferVO.getTransferID(), p_transferVO.getNetworkCode());
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    methodName,
                                    p_transferVO.getTransferID(),
                                    "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO.getSenderVO())
                                        .getCategoryCode() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO.getSubscriberType() + " Receiver Service Class ID=" + recAllServiceClassForPromotional + " Sub Service=" + p_transferVO
                                        .getSubService() + " Category level promotional transfer rule");
                            }
                        }

                        if (transferRulesVO != null) {
                            /*
                             * isExist=isPromotionalRuleExistInRange(transferRulesVO
                             * ,
                             * p_transferVO.getTransferDateTime());if(isExist)
                             * if(isExist)
                             * isPromotionalTrfRuleFound=true;
                             * else
                             */
                            isPromotionalTrfRuleFound = true;
                            break;
                        }

                    case PretupsI.PROMO_TRF_RULE_LVL_GEOGRAPHY_CODE:
                        if (!isServiceProviderPromoAllow) {
                            transferRulesVO = (TransferRulesVO) transferRuleMap
                                .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                    .getSenderVO()).getGeographicalCode() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO
                                    .getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_GEOGRAPHY);

                        } else {
                            transferRulesVO = (TransferRulesVO) transferRuleMap
                                .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                    .getSenderVO()).getGeographicalCode() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO
                                    .getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_GEOGRAPHY + "_" + spGroup + "_" + receiverTransferItemVO.getAccountStatus());
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                methodName,
                                p_transferVO.getTransferID(),
                                "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO.getSenderVO())
                                    .getGeographicalCode() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO.getSubscriberType() + " Receiver Service Class ID=" + receiverVO
                                    .getServiceClassCode() + " Sub Service=" + p_transferVO.getSubService() + " Geography level promotional transfer rule");
                        }
                        if (transferRulesVO == null && !receiverVO.isUsingAllServiceClass()) {
                            if (BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                try {
                                    recAllServiceClassForPromotional = validateServiceClass(p_con, p_transferVO.getReceiverMsisdn(), p_transferVO.getTransferID(),
                                        receiverTransferItemVO.getInterfaceID(), PretupsI.ALL, receiverTransferItemVO.getAccountStatus(), receiverTransferItemVO
                                            .getRequestValue(), receiverTransferItemVO.getUserType(), p_module);
                                } catch (Exception e) {
                                    LOG.errorTrace(methodName, e);
                                }
                            }
                            if (!BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                transferRulesVO = (TransferRulesVO) transferRuleMap
                                    .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                        .getSenderVO()).getGeographicalCode() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO
                                        .getSubService() + "_" + PretupsI.PROMO_TRF_RULE_LVL_GEOGRAPHY_CODE);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(methodName, p_transferVO.getTransferID(),
                                        "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO
                                            .getSenderVO()).getGeographicalCode() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO
                                            .getSubscriberType() + " Receiver Service Class ID=" + recAllServiceClassForPromotional + " Sub Service=" + p_transferVO
                                            .getSubService() + " Geography level promotional transfer rule");
                                }
                            }
                        }
                        if (transferRulesVO != null) {
                            /*
                             * isExist=isPromotionalRuleExistInRange(transferRulesVO
                             * ,
                             * p_transferVO.getTransferDateTime());if(isExist)
                             * if(isExist)
                             * isPromotionalTrfRuleFound=true
                             * else
                             */
                            isPromotionalTrfRuleFound = true;
                            break;
                        }

                    case PretupsI.PROMO_TRF_RULE_LVL_PREFIX_ID:
                        // transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getGeographicalCode()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_PREFIXID)
                        transferRulesVO = (TransferRulesVO) transferRuleMap.get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO
                            .getReceiverNetworkCode() + "_" + PretupsI.ALL + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO
                            .getServiceClassCode() + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_PREFIXID);
                        boolean prefixapplicable = false;
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                methodName,
                                p_transferVO.getTransferID(),
                                "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO.getSenderVO())
                                    .getGeographicalCode() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO.getSubscriberType() + " Receiver Service Class ID=" + receiverVO
                                    .getServiceClassCode() + " Sub Service=" + p_transferVO.getSubService() + " Prefix_id level promotional transfer rule");
                        }
                        if (transferRulesVO == null && !receiverVO.isUsingAllServiceClass()) {
                            if (BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                try {
                                    recAllServiceClassForPromotional = validateServiceClass(p_con, p_transferVO.getReceiverMsisdn(), p_transferVO.getTransferID(),
                                        receiverTransferItemVO.getInterfaceID(), PretupsI.ALL, receiverTransferItemVO.getAccountStatus(), receiverTransferItemVO
                                            .getRequestValue(), receiverTransferItemVO.getUserType(), p_module);
                                } catch (Exception e) {
                                    LOG.errorTrace(methodName, e);
                                }
                            }
                            if (!BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                transferRulesVO = (TransferRulesVO) transferRuleMap
                                    .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + ((ChannelUserVO) p_transferVO
                                        .getSenderVO()).getGeographicalCode() + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO
                                        .getSubService() + "_" + PretupsI.PROMO_TRF_RULE_LVL_PREFIX_ID);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(methodName, p_transferVO.getTransferID(),
                                        "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO
                                            .getSenderVO()).getGeographicalCode() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO
                                            .getSubscriberType() + " Receiver Service Class ID=" + recAllServiceClassForPromotional + " Sub Service=" + p_transferVO
                                            .getSubService() + " Prefix_id level promotional transfer rule");
                                }
                            }
                        }
                        if (transferRulesVO != null) {
                            // if promotional transfer rule is applicable now
                            // check
                            // for valid prefixes here
                            prefixapplicable = validatePromotionPrefixes(p_transferVO.getTransferDateTime(), receiverVO.getPrefixID(), transferRulesVO.getAllowedDays(),
                                transferRulesVO.getAllowedSeries(), transferRulesVO.getDeniedSeries());
                            if (!prefixapplicable) {
                                break;
                            }
                            isPromotionalTrfRuleFound = true;
                            break;

                        }

                    case PretupsI.PROMO_TRF_RULE_LVL_CELLGRP_CODE:
                        String cellId = ((ChannelUserVO) p_transferVO.getSenderVO()).getCellID();
                        String cellGroup = new CellIdMgmtDAO().getCellGroupFromCellId(p_con, cellId);
                        if (!isServiceProviderPromoAllow) {
                            transferRulesVO = (TransferRulesVO) transferRuleMap.get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO
                                .getReceiverNetworkCode() + "_" + cellGroup + "_" + receiverVO.getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO
                                .getServiceClassCode() + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CELLGROUP);
                        } else {
                            transferRulesVO = (TransferRulesVO) transferRuleMap
                                .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + cellGroup + "_" + receiverVO
                                    .getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CELLGROUP + "_" + spGroup + "_" + receiverTransferItemVO
                                    .getAccountStatus());
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, p_transferVO.getTransferID(), " transferRulesVO 1 :" + transferRulesVO);
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                methodName,
                                p_transferVO.getTransferID(),
                                "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + cellGroup + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO
                                    .getSubscriberType() + " Receiver Service Class ID=" + receiverVO.getServiceClassCode() + " Sub Service=" + p_transferVO.getSubService() + " cell group level promotional transfer rule");
                        }
                        if (transferRulesVO == null && !receiverVO.isUsingAllServiceClass()) {
                            if (BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                try {
                                    recAllServiceClassForPromotional = validateServiceClass(p_con, p_transferVO.getReceiverMsisdn(), p_transferVO.getTransferID(),
                                        receiverTransferItemVO.getInterfaceID(), PretupsI.ALL, receiverTransferItemVO.getAccountStatus(), receiverTransferItemVO
                                            .getRequestValue(), receiverTransferItemVO.getUserType(), p_module);
                                } catch (Exception e) {
                                    LOG.errorTrace(methodName, e);
                                }
                            }
                            if (!BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                transferRulesVO = (TransferRulesVO) transferRuleMap
                                    .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + cellGroup + "_" + receiverVO
                                        .getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_CELLGROUP + "_" + spGroup + "_" + receiverTransferItemVO
                                        .getAccountStatus());
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(methodName, p_transferVO.getTransferID(), " transferRulesVO 2 :" + transferRulesVO);
                                }
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(
                                        methodName,
                                        p_transferVO.getTransferID(),
                                        "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + cellGroup + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO
                                            .getSubscriberType() + " Receiver Service Class ID=" + recAllServiceClassForPromotional + " Sub Service=" + p_transferVO
                                            .getSubService() + " cell group level promotional transfer rule");
                                }
                            }
                        }

                        if (transferRulesVO == null && isServiceProviderPromoAllow)// condition
                        // mod
                        // by
                        // rahul
                        // for
                        // serv
                        // provdr
                        // grp
                        // preference
                        {

                            // spGroup = PretupsI.ALL

                            cellId = ((ChannelUserVO) p_transferVO.getSenderVO()).getCellID();
                            cellGroup = new CellIdMgmtDAO().getCellGroupFromCellId(p_con, cellId);

                            transferRulesVO = getPromotionalTransferRule(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO.getReceiverNetworkCode(),
                                cellGroup, receiverVO.getSubscriberType(), PretupsI.ALL, receiverVO.getServiceClassCode(), recAllServiceClassForPromotional, p_transferVO
                                    .getSubService(), PretupsI.PROMOTIONAL_LEVEL_CELLGROUP, spGroup, receiverTransferItemVO.getAccountStatus(),
                                " cell group level promotional transfer rule", transferRuleMap, p_transferVO.getTransferID(), p_transferVO.getNetworkCode());
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(methodName, p_transferVO.getTransferID(), " transferRulesVO 3 :" + transferRulesVO);
                            }
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    methodName,
                                    p_transferVO.getTransferID(),
                                    "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + cellGroup + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO
                                        .getSubscriberType() + " Receiver Service Class ID=" + recAllServiceClassForPromotional + " Sub Service=" + p_transferVO
                                        .getSubService() + " cell group level promotional transfer rule");
                            }
                        }

                        if (transferRulesVO != null) {
                            /*
                             * isExist=isPromotionalRuleExistInRange(transferRulesVO
                             * ,
                             * p_transferVO.getTransferDateTime());if(isExist)
                             * if(isExist)
                             * isPromotionalTrfRuleFound=true
                             * else
                             */
                            isPromotionalTrfRuleFound = true;
                            break;
                        }

                    case PretupsI.PROMO_TRF_RULE_LVL_SPNAME_CODE:
                        transferRulesVO = (TransferRulesVO) transferRuleMap
                            .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + spGroup + "_" + receiverVO
                                .getSubscriberType() + "_" + PretupsI.ALL + "_" + receiverVO.getServiceClassCode() + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_SERVICEGROUP + "_" + spGroup + "_" + receiverTransferItemVO
                                .getAccountStatus());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                methodName,
                                p_transferVO.getTransferID(),
                                "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + spGroup + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO
                                    .getSubscriberType() + " Receiver Service Class ID=" + receiverVO.getServiceClassCode() + " Sub Service=" + p_transferVO.getSubService() + " service group level promotional transfer rule");
                        }
                        if (transferRulesVO == null && !receiverVO.isUsingAllServiceClass()) {
                            if (BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                try {
                                    recAllServiceClassForPromotional = validateServiceClass(p_con, p_transferVO.getReceiverMsisdn(), p_transferVO.getTransferID(),
                                        receiverTransferItemVO.getInterfaceID(), PretupsI.ALL, receiverTransferItemVO.getAccountStatus(), receiverTransferItemVO
                                            .getRequestValue(), receiverTransferItemVO.getUserType(), p_module);
                                } catch (Exception e) {
                                    LOG.errorTrace(methodName, e);
                                }
                            }
                            if (!BTSLUtil.isNullString(recAllServiceClassForPromotional)) {
                                transferRulesVO = (TransferRulesVO) transferRuleMap
                                    .get(p_transferVO.getServiceType() + "_" + p_transferVO.getModule() + "_" + p_transferVO.getReceiverNetworkCode() + "_" + spGroup + "_" + receiverVO
                                        .getSubscriberType() + "_" + PretupsI.ALL + "_" + recAllServiceClassForPromotional + "_" + p_transferVO.getSubService() + "_" + PretupsI.PROMOTIONAL_LEVEL_SERVICEGROUP + "_" + spGroup + "_" + receiverTransferItemVO
                                        .getAccountStatus());
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(
                                        methodName,
                                        p_transferVO.getTransferID(),
                                        "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + spGroup + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO
                                            .getSubscriberType() + " Receiver Service Class ID=" + recAllServiceClassForPromotional + " Sub Service=" + p_transferVO
                                            .getSubService() + " service group level promotional transfer rule");
                                }
                            }
                        }

                        if (transferRulesVO == null && isServiceProviderPromoAllow)// condition
                        // mod
                        // by
                        // rahul
                        // for
                        // serv
                        // provdr
                        // grp
                        // preference
                        {
                            transferRulesVO = getPromotionalTransferRule(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO.getReceiverNetworkCode(),
                                spGroup, receiverVO.getSubscriberType(), PretupsI.ALL, receiverVO.getServiceClassCode(), recAllServiceClassForPromotional, p_transferVO
                                    .getSubService(), PretupsI.PROMOTIONAL_LEVEL_SERVICEGROUP, spGroup, receiverTransferItemVO.getAccountStatus(),
                                "service group level promotional transfer rule", transferRuleMap, p_transferVO.getTransferID(), p_transferVO.getNetworkCode());
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    methodName,
                                    p_transferVO.getTransferID(),
                                    "Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + spGroup + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO
                                        .getSubscriberType() + " Receiver Service Class ID=" + recAllServiceClassForPromotional + " Sub Service=" + p_transferVO
                                        .getSubService() + " service group level promotional transfer rule");
                            }
                        }

                        if (transferRulesVO != null) {
                            /*
                             * isExist=isPromotionalRuleExistInRange(transferRulesVO
                             * ,
                             * p_transferVO.getTransferDateTime());if(isExist)
                             * if(isExist)
                             * isPromotionalTrfRuleFound=true
                             * else
                             */
                            isPromotionalTrfRuleFound = true;
                            break;
                        }
                }

            }
            if (!isPromotionalTrfRuleFound) {

            	transferRulesVO = selectTransferRuleFromCache(p_con,p_transferVO,receiverVO);
            	
            	/*if (transferRulesVO == null) {
            		transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO
            				.getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getDomainID(), receiverVO.getSubscriberType(), PretupsI.ALL, receiverVO
            				.getServiceClassCode(), p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE, PretupsI.ALL);
            	}*/

            	if (LOG.isDebugEnabled()) {
            		LOG.debug(
            				methodName,
            				p_transferVO.getTransferID(),
            				"Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO.getSenderVO())
            				.getDomainID() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO.getSubscriberType() + " Receiver Service Class ID=" + receiverVO
            				.getServiceClassCode() + " Sub Service=" + p_transferVO.getSubService());
            	}
            	if (transferRulesVO == null && receiverVO.isUsingAllServiceClass()) {

            		final CardGroupDAO cardGroupDAO = new CardGroupDAO();
            		final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
            		isDefaultCardGroupExist = cardGroupSetDAO.isDefaultCardGroupExist(p_con, p_transferVO);
            		cardGroupSetStatus = p_transferVO.getStatus();
            	} else if (transferRulesVO == null && !receiverVO.isUsingAllServiceClass()) {
            		final TransferItemVO receiverTransferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);
            		try {
            			recServiceClass = validateServiceClass(p_con, p_transferVO.getReceiverMsisdn(), p_transferVO.getTransferID(), receiverTransferItemVO.getInterfaceID(),
            					PretupsI.ALL, receiverTransferItemVO.getAccountStatus(), receiverTransferItemVO.getRequestValue(), receiverTransferItemVO.getUserType(), p_module);
            		} catch (Exception e) {
            			LOG.errorTrace(methodName, e);
            		}
            		if (!BTSLUtil.isNullString(recServiceClass)) {
            			transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO
            					.getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getDomainID(), receiverVO.getSubscriberType(), PretupsI.ALL,
            					recServiceClass, p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE, p_transferVO.getRequestGatewayCode());
            		}
            		if (transferRulesVO == null) {
            			transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO
            					.getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getDomainID(), receiverVO.getSubscriberType(), PretupsI.ALL,
            					recServiceClass, p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE, PretupsI.ALL);
            			// transferRulesVO=(TransferRulesVO)TransferRulesCache.getObject(p_transferVO.getServiceType(),p_transferVO.getModule(),p_transferVO.getReceiverNetworkCode(),((ChannelUserVO)p_transferVO.getSenderVO()).getDomainID(),receiverVO.getSubscriberType(),PretupsI.ALL,recServiceClass,p_transferVO.getSubService(),PretupsI.NOT_APPLICABLE)
            		}

            		if (transferRulesVO == null) {
            			final CardGroupDAO cardGroupDAO = new CardGroupDAO();
            			final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
            			isDefaultCardGroupExist = cardGroupSetDAO.isDefaultCardGroupExist(p_con, p_transferVO);
            			cardGroupSetStatus = p_transferVO.getStatus();
            		}
            	}
            	if (transferRulesVO == null && !isDefaultCardGroupExist) {
            		EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", p_transferVO
            				.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Default card group not exist for the service type " + p_transferVO
            				.getServiceType() + " with  Sub Service=" + p_transferVO.getSubService());
            		throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_DEFAULT_CARDGROUP_NOTEXIST);
            	}
            }
            if (transferRulesVO != null && transferRulesVO.getStatus().equals(PretupsI.SUSPEND)) {
                EventHandler
                    .handle(
                        EventIDI.SYSTEM_INFO,
                        EventComponentI.SYSTEM,
                        EventStatusI.RAISED,
                        EventLevelI.INFO,
                        "PretupsBL[validateTransferRule]",
                        p_transferVO.getTransferID(),
                        p_transferVO.getSenderMsisdn(),
                        p_transferVO.getNetworkCode(),
                        "Transfer Rule is suspended in C2S between " + ((ChannelUserVO) p_transferVO.getSenderVO()).getDomainID() + " and " + receiverVO.getServiceClassCode() + " and receiver service class code=" + p_transferVO
                            .getReceiverTransferItemVO().getServiceClassCode());
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_TRANSFER_RULE_NOTEXIST);
            }

        }
        if (transferRulesVO != null) {
            p_transferVO.setCardGroupSetID(transferRulesVO.getCardGroupSetID());
            if (PretupsI.SUSPEND.equals(transferRulesVO.getCardGroupSetIDStatus())) {
                EventHandler.handle(EventIDI.CARD_GROUP_SUSPENDED, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", p_transferVO
                    .getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Card Group Set is suspended " + transferRulesVO.getCardGroupSetID());
                // if default language is english then pick language 1 message
                // else language 2
                String message = null;
                // ChangeID=LOCALEMASTER
                // which language message to be send is determined from the
                // locale master
                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_transferVO.getLocale())).getMessage())) {
                    message = transferRulesVO.getCardGroupMessage1();
                } else {
                    message = transferRulesVO.getCardGroupMessage2();
                }
                p_transferVO.setSenderReturnMessage(message);
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_CARD_GROUP_SET_SUSPENDED);
            }
        } else if (PretupsI.SUSPEND.equals(cardGroupSetStatus)) {
            EventHandler.handle(EventIDI.CARD_GROUP_SUSPENDED, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateTransferRule]", p_transferVO
                .getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Card Group Set is suspended   " + p_transferVO.getCardGroupSetID());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_CARD_GROUP_SET_SUSPENDED);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_transferVO.getTransferID(), "Exiting");
        }
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
        /** Birendra:START:18FEB **/
        final String methodName = "validateRequestMessageGateway";
        StringBuilder loggerValue = new StringBuilder();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), "Entered");
        }
        String pinpasEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
        try {
            final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(p_requestVO.getRequestGatewayCode());
            p_requestVO.setMessageGatewayVO(messageGatewayVO);
            if (messageGatewayVO == null) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_NOTFOUND_MESSAGEGATEWAY);
            }
            final RequestGatewayVO requestGatewayVO = messageGatewayVO.getRequestGatewayVO();
            if (requestGatewayVO == null) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_NOTFOUND_REQMESSAGEGATEWAY);
            }
            if (!p_requestVO.getRequestGatewayType().equalsIgnoreCase(messageGatewayVO.getGatewayType())) {
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Request Gateway Type :=");
                	loggerValue.append(p_requestVO.getRequestGatewayType());
                	loggerValue.append(" Gateway Type for code:=");
                	loggerValue.append(messageGatewayVO.getGatewayType());
                	LOG.debug(methodName, loggerValue);
                }
                throw new BTSLBaseException("PretupsBL", "validateRequestMessageGateway", PretupsErrorCodesI.ERROR_INVALID_REQUESTINTTYPE);
            }
            if (!requestGatewayVO.getServicePort().equals(p_requestVO.getServicePort())) {
            	 if (LOG.isDebugEnabled()) {
                 	loggerValue.setLength(0);
                 	loggerValue.append("Request Service Port :=");
                 	loggerValue.append(p_requestVO.getServicePort());
                 	loggerValue.append(" Allowed Port : ");
                 	loggerValue.append(requestGatewayVO.getServicePort());
                 	LOG.debug(methodName, loggerValue);
                 }
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, p_requestVO.getRequestIDStr(), "Request Service Port : " + p_requestVO.getServicePort() + ", Allowed Port : " + requestGatewayVO
                        .getServicePort());
                }
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_SERVICEPORT);
            }

            if (PretupsI.ALL.equals(requestGatewayVO.getAuthType()) || PretupsI.AUTH_TYPE_IP.equals(requestGatewayVO.getAuthType())) {
            	if(!BTSLUtil.isStringContain(messageGatewayVO.getHost(), p_requestVO.getRemoteIP())){
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, p_requestVO.getRequestIDStr(), "Remote IP : " + p_requestVO.getRemoteIP() + ", Allowed Host : " + messageGatewayVO.getHost());
                    }
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_IP);
                }
            }
            if (PretupsI.ALL.equals(requestGatewayVO.getAuthType()) || PretupsI.AUTH_TYPE_LOGIN.equals(requestGatewayVO.getAuthType())) {
               /* if (!BTSLUtil.NullToString(requestGatewayVO.getLoginID()).equals(p_requestVO.getLogin())) {
                	  if (LOG.isDebugEnabled()) {
                          LOG.debug(methodName,"", "requestGatewayVO: " + requestGatewayVO + ", p_requestVO : " + p_requestVO);
                      }
                	throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_GATEWAY_LOGIN);
                }*/

                // if(!BTSLUtil.NullToString(p_requestVO.getPassword()).equals(requestGatewayVO.getDecryptedPassword()))
                /*
                 * change done by ashishT for hashing implementation.
                 * hashed gateway password is sent in reacherge request ,then it
                 * is compared with the hash value stored in db.
                 */
                LOG.debug(methodName,"1: " + requestGatewayVO.getDecryptedPassword() + " 2: " + p_requestVO.getPassword() + " 3: " + requestGatewayVO.getPassword());
                if ("SHA".equalsIgnoreCase(pinpasEnDeCryptionType)) {
                    // if(p_requestVO.getPassword().length()>((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue())
                    // means hash value is comming in request..

                    if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageGatewayVO.getReqpasswordtype())) {
                        if (!(requestGatewayVO.getPassword().equalsIgnoreCase( BTSLUtil.encryptText(p_requestVO.getPassword())))) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_PSWD);
                        }
                    }
                    // means plain password is commming in request..
                    else {
                        if (!(requestGatewayVO.getPassword().equalsIgnoreCase(p_requestVO.getPassword()))) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_PSWD);
                        }
                    }
                } else {
                    // Means Gateway password in request is Plain & Application
                    // is running on DES/AES Algo.
                	
                	
                 /*   if (PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageGatewayVO.getReqpasswordtype())) {
                        if (!BTSLUtil.NullToString(BTSLUtil.encryptText(p_requestVO.getPassword())).equals(requestGatewayVO.getPassword())) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_PSWD);
                        }
                    } else // Means Gateway password in request is encrypted &
                           // Application is running on DES/AES Algo.
                    {
                        if (!(requestGatewayVO.getPassword().equalsIgnoreCase(p_requestVO.getPassword()))) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_PSWD);
                        }
                    }*/
                }
            }
            if ("null".equals((p_requestVO.getSourceType())) || (p_requestVO.getSourceType()) == (null) || p_requestVO.getSourceType().length() == 0) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_BLANK_SOURCE_TYPE);
            }
            p_requestVO.setServicePort(requestGatewayVO.getServicePort());
        }

        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateRequestMessageGateway]", p_requestVO
                .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",
                "Request Gateway Validation Failed For Code : " + p_requestVO.getRequestGatewayCode() + ", Error Code = " + be.getMessage());
            // In case of unautherise access no message is send to the user.
            p_requestVO.setSenderMessageRequired(false);
            // Log in Unauthorize log
            UnauthorizedAccessLog.log(p_requestVO, be.getMessageKey(), be.getMessage());
            throw be;
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, p_requestVO.getRequestIDStr(), "Exiting...");
            }
        }
    }

    public static void handlePushInterface(ResponseInterfaceDetailVO responseInterfaceDetailVO, String p_msisdn, String p_message) {
        final String methodName = "handlePushInterface";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append("p_message=");
        	loggerValue.append(p_message);
        	LOG.debug(methodName, loggerValue);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting...");
        }
    }

    /**
     * Method to check whether message is Plain Message and whether it is
     * allowed or not
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public static void isPlainMessageAndAllowed(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "isPlainMessageAndAllowed";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), "Entered for Request ID:" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN());
        }
        try {
            if (BTSLUtil.isNullString(p_requestVO.getUDH())) {
                final String userMessage = p_requestVO.getDecryptedMessage();
                if (userMessage != null && userMessage.length() > 2) {
                    final String msgStartBinary = userMessage.substring(0, 3);
                    final String udhHex = Message348.bytesToBinHex(msgStartBinary.getBytes());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, p_requestVO.getRequestIDStr(), "Binary Message" + msgStartBinary + " udhHex=" + udhHex);
                    }
                    p_requestVO.setUDHHex(udhHex);
                    if (PretupsI.UDH_HEX.equals(udhHex)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                methodName,
                                p_requestVO.getRequestIDStr(),
                                "UDH_HEX is received as part of userMessage: Doing required conversion for Request ID:" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO
                                    .getFilteredMSISDN());
                        }
                        p_requestVO.setUDH(msgStartBinary);
                        p_requestVO.setDecryptedMessage(p_requestVO.getDecryptedMessage().substring(3));
                    }
                }
            }

            if (BTSLUtil.isNullString(p_requestVO.getUDH())) {
                p_requestVO.setPlainMessage(true);
                // p_requestVO.setDecryptedMessage(p_requestVO.getDecryptedMessage())
                // If plain SMS is not allowed in channel
                if (PretupsI.NO.equals(p_requestVO.getMessageGatewayVO().getPlainMsgAllowed())) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.REQUEST_RESPONSE_INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,
                        "PretupsBL[isPlainMessageAndAllowed]", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",
                        "Plain SMS not allowed Gateway =" + p_requestVO.getMessageGatewayVO().getGatewayCode());
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_PLAIN_SMS_NOT_ALLOWED);
                }
            } else {
                p_requestVO.setPlainMessage(false);
                // p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage())
                if (PretupsI.NO.equals(p_requestVO.getMessageGatewayVO().getBinaryMsgAllowed())) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.REQUEST_RESPONSE_INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,
                        "PretupsBL[isPlainMessageAndAllowed]", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",
                        "Binary SMS not allowed Gateway =" + p_requestVO.getMessageGatewayVO().getGatewayCode());
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_BINARY_SMS_NOT_ALLOWED);
                }
            }
            if (BTSLUtil.isNullString(p_requestVO.getSourceType()) && p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.GATEWAY_TYPE_SMSC)) {
                if (!p_requestVO.isPlainMessage()) {
                    p_requestVO.setSourceType(PretupsI.REQUEST_SOURCE_TYPE_STK);
                } else {
                    p_requestVO.setSourceType(PretupsI.REQUEST_SOURCE_TYPE_SMS);
                }
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[isPlainMessageAndAllowed]", "", p_requestVO
                .getFilteredMSISDN(), "", "Not able to check the message is plain and allowed for number:" + p_requestVO.getFilteredMSISDN() + " ,getting Exception=" + e
                .getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), "Exiting ");
        }
    }

    /**
     * Method to get the encryption key for the user
     * 
     * @param p_con
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public static void getEncryptionKeyForUser(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "getEncryptionKeyForUser";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(),
                "Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Encryption Level=" + p_requestVO.getMessageGatewayVO()
                    .getRequestGatewayVO().getEncryptionLevel());
        }
        String c2sUserRegistrationRequired = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_USER_REGISTRATION_REQUIRED);
        try {
            if (PretupsI.YES.equalsIgnoreCase(c2sUserRegistrationRequired)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, p_requestVO.getRequestIDStr(),
                        "p_requestVO.getUDH().getBytes()=" + p_requestVO.getUDH().getBytes() + " Message348.bytesToBinHex(p_requestVO.getUDH().getBytes())=" + Message348
                            .bytesToBinHex(p_requestVO.getUDH().getBytes()));
                }
                if (PretupsI.UDH_HEX.equals(Message348.bytesToBinHex(p_requestVO.getUDH().getBytes()))) {
                    UserPhoneVO userPhoneVO = null;
                    final ChannelUserVO channelUserVO = ((ChannelUserVO) (p_requestVO.getSenderVO()));
                    if (!channelUserVO.isStaffUser()) {
                        userPhoneVO = channelUserVO.getUserPhoneVO();
                    } else {
                        userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
                    }
                    if (p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getEncryptionLevel().equals(PretupsI.ENCRYPTION_LEVEL_USER_CODE)) {
                        // Load the pos key of the msisdn
                        final PosKeyVO posKeyVO = _posKeyDAO.loadPosKeyByMsisdn(p_con, p_requestVO.getFilteredMSISDN());
                        if (posKeyVO == null) {
                            LOG.error(methodName, p_requestVO.getRequestIDStr(), " MSISDN=" + p_requestVO.getFilteredMSISDN() + " User Encryption Not found in Database");
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[isPlainMessageAndAllowed]",
                                p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", "User Encryption Not found in Database for MSISDN=" + p_requestVO
                                    .getFilteredMSISDN());
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_ENCR_KEY_NOTFOUND);
                        } else {
                            userPhoneVO.setEncryptDecryptKey(posKeyVO.getKey());
                            userPhoneVO.setSimProfileID(posKeyVO.getSimProfile());
                            userPhoneVO.setRegistered(posKeyVO.isRegistered());
                        }
                    } else {
                        userPhoneVO.setEncryptDecryptKey(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getEncryptionKey());
                    }
                } else // If UDH is not 27000 then it is the response of the ICC
                       // ID Key getting
                {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, p_requestVO.getRequestIDStr(),
                            "Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Got the ICC ID Key response for the request");
                    }
                    // Log the response of the ICC ID KEY
                    final SIMFileReader simFileReader = new SIMFileReader();
                    simFileReader.logAPDUMessage(p_requestVO.getUDH(), p_requestVO.getFilteredMSISDN(), p_requestVO.getRequestMessage());

                    // message should not be sent
                    p_requestVO.setSenderMessageRequired(false);
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_WRONG_UDH);
                }
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[checkPostPaidTransactionControls]", "",
                p_requestVO.getFilteredMSISDN(), "", "Not able to get the encryption for the number: " + p_requestVO.getFilteredMSISDN() + " getting Exception=" + e
                    .getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), "Exiting");
        }
    }

    /**
     * Method to parse the Binary Message by decrypting the same by the
     * encryption key
     * 
     * @param p_requestVO
     * @param p_simProfileVO
     * @throws BTSLBaseException
     */
    public static void parseBinaryMessage(RequestVO p_requestVO, SimProfileVO p_simProfileVO) throws BTSLBaseException {
        final String methodName = "parseBinaryMessage";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), " MSISDN=" + p_requestVO.getFilteredMSISDN());
        }
        String userMessage = p_requestVO.getRequestMessage();
        try {
            userMessage = p_requestVO.getDecryptedMessage();
            final String UDH = p_requestVO.getUDH();
            final String udhStr = Message348.bytesToBinHex(UDH.getBytes());
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, p_requestVO.getRequestIDStr(), " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Message=" + userMessage + " udhStr= " + udhStr);
            }

            if (userMessage == null || userMessage.length() < 1) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[parseBinaryMessage]", p_requestVO
                    .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", " Blank binary message from number :" + p_requestVO.getFilteredMSISDN());
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_MESSAGE);
            }// end if

            String dataString = "";
            if (p_requestVO.getHexUrlEncodedRequired()) {
                // Decrypt key of the PosKeyVO will be used to decypt the binary
                // message
                dataString = Message348.bytesToBinHex(userMessage.getBytes());
            } else {
                dataString = userMessage;
            }
            // added by amit
            // dataString="00"+dataString+"04"
            // dataString="00"+dataString

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, p_requestVO.getRequestIDStr(), " dataString (WITHOUT DECODED)for Request ID:" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO
                    .getFilteredMSISDN() + " userMessage =" + userMessage.getBytes() + " Decoded Message:" + dataString);
            }
            // final Text value
            try {
                final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
                final Message348 message348 = new Message348();
                // userMessage =
                // message348.parse348Message(dataString,p_requestVO.
                // getMessageGatewayVO().getRequestGatewayVO().getEncryptionKey(),p_simProfileVO,(channelUserVO.getUserPhoneVO()).getPhoneProfile())
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, p_requestVO.getRequestIDStr(), " channelUserVO: " + channelUserVO);
                    LOG.debug(methodName, p_requestVO.getRequestIDStr(), " channelUserVO.getUserPhoneVO(): " + channelUserVO.getUserPhoneVO());
                }
                userMessage = message348.parse348Message(dataString, channelUserVO.getUserPhoneVO().getEncryptDecryptKey(), p_simProfileVO, (channelUserVO.getUserPhoneVO())
                    .getPhoneProfile());
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, p_requestVO.getRequestIDStr(), " DECODED DATA for Request ID:" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO
                        .getFilteredMSISDN() + " MESSAGE:" + userMessage);
                }

                /*
                 * commented as discussed with sanjay, may not be required , TBC
                 * int indx=userMessage.indexOf("CSMS")
                 * if(indx != -1)
                 * {
                 * int ind = userMessage.indexOf(0)
                 * if (ind != -1)
                 * userMessage=userMessage.substring(0,ind)
                 * }
                 */
            } catch (java.security.GeneralSecurityException ge) {
                LOG.errorTrace(methodName, ge);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[parseBinaryMessage]", p_requestVO
                    .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",
                    " General security Exception for number:" + p_requestVO.getFilteredMSISDN() + " Exception348=" + ge.getMessage());
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_GEN_SCRTY_EXC,ge);
            } catch (Exception348 e348) {
                LOG.errorTrace(methodName, e348);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[parseBinaryMessage]", p_requestVO
                    .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", "Exception348 for number:" + p_requestVO.getFilteredMSISDN() + " Exception348=" + e348
                    .getMessage());
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_EXC348_EXC,e348);
            } catch (Exception exstk) {
                LOG.errorTrace(methodName, exstk);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[parseBinaryMessage]", p_requestVO
                    .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", "Exception for number:" + p_requestVO.getFilteredMSISDN() + " Exception=" + exstk.getMessage());
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,exstk);
            }

            char ch = ' ';

            // This has been done to check whether the Message has be decrypted
            // properly or not
            // only first 3 chars are checked in this case
            for (int i = 0; i < 3; i++) {
                ch = userMessage.charAt(i);
                if (!Character.isLetter(ch)) {
                    if (!Character.isDigit(ch)) {
                        if (!Character.isWhitespace(ch)) {
                            LOG.error(methodName, p_requestVO.getRequestIDStr(),
                                " SMS can not be properly decrypted for Request ID:" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN());
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "PretupsBL[parseBinaryMessage]",
                                p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", "SMS can not be properly decrypted:" + p_requestVO.getFilteredMSISDN());
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_MSG_NOT_DECRYPT);
                        }
                    }
                }
            }
            p_requestVO.setDecryptedMessage(userMessage);
            p_requestVO.setDecryptedMessage(BTSLUtil.NullToString(p_requestVO.getDecryptedMessage().trim()));
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            // EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"PretupsBL[parseBinaryMessage]","",p_requestVO.getFilteredMSISDN(),"","Not able to decrypt message for the number:"+p_requestVO.getFilteredMSISDN()+" ,getting Base Exception="+be.getMessage())
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[parseBinaryMessage]", "", p_requestVO
                .getFilteredMSISDN(), "", "Not able to decrypt message for the number: " + p_requestVO.getFilteredMSISDN() + " getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), " Incoming User Data " + userMessage);
        }
    }

    /**
     * This mesthod will validate the Transaction ID maintained at the server
     * and that given by SIM
     * Helps in fraud check
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public static void validateTempTransactionID(RequestVO p_requestVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
        final String methodName = "validateTempTransactionID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), "Entered MSISDN=" + p_requestVO.getFilteredMSISDN());
        }
        try {
            String tempTransID = null;
            final String[] messageArray = p_requestVO.getRequestMessageArray();
            final int msgArrLength = messageArray.length;
            final String lastword = messageArray[msgArrLength - 1];
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, p_requestVO.getRequestIDStr(), " MSISDN=" + p_requestVO.getFilteredMSISDN() + " msgArrLength:" + msgArrLength + " lastword:" + lastword);
            }
            final int indx = BTSLUtil.NullToString(lastword).indexOf(PretupsI.TEMP_TRANS_ID_START_WITH);
            if (indx > -1 && lastword.length() == PretupsI.TEMP_TRANS_ID_LENGTH) {
                tempTransID = lastword.substring((indx + (PretupsI.TEMP_TRANS_ID_START_WITH).length()), lastword.length());
            } else {
                return;
            }
            p_requestVO.setTempTransID(tempTransID);

            if (!BTSLUtil.isNullString(tempTransID)) {
                // ChannelUserVO
                // channelUserVO=(ChannelUserVO)p_requestVO.getSenderVO()
                final String lastTransferID = (channelUserVO.getUserPhoneVO()).getTempTransferID();
                if (!BTSLUtil.isNullString(lastTransferID)) {
                    final long dbTxnId = Long.parseLong(lastTransferID);
                    long inTxnId = 0L;
                    try {
                        inTxnId = Long.parseLong(tempTransID);
                    } catch (Exception e) {
                        LOG.error(methodName, p_requestVO.getRequestIDStr(),
                            " MSISDN=" + p_requestVO.getFilteredMSISDN() + " NumberFormatException invalid Temp Transaction ID= " + tempTransID + " remove ':' and validate");
                        LOG.errorTrace(methodName, e);
                        // Remove the special characters from temp transaction
                        // id
                        String newtempTransID = null;
                        if (tempTransID.contains(":")) {
                            newtempTransID = tempTransID.replaceAll(":", "");
                        } else if (tempTransID.contains(";")) {
                            newtempTransID = tempTransID.replaceAll(";", "");
                        } else if (tempTransID.contains("?")) {
                            newtempTransID = tempTransID.replaceAll("?", "");
                        } else if (tempTransID.contains("*")) {
                            newtempTransID = tempTransID.replaceAll("*", "");
                        }

                        LOG.error(methodName, p_requestVO.getRequestIDStr(),
                            " MSISDN=" + p_requestVO.getFilteredMSISDN() + " NumberFormatException remove ':' from temptxn id new id is= " + newtempTransID);
                        inTxnId = Long.parseLong(newtempTransID);
                    }

                    if (inTxnId <= dbTxnId) {
                        LOG.error(methodName, p_requestVO.getRequestIDStr(),
                            " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Temp Trans ID in DB=" + dbTxnId + " from STK:" + inTxnId);
                        EventHandler
                            .handle(
                                EventIDI.SYSTEM_INFO,
                                EventComponentI.SYSTEM,
                                EventStatusI.RAISED,
                                EventLevelI.INFO,
                                "PretupsBL[validateTempTransactionID]",
                                p_requestVO.getRequestIDStr(),
                                p_requestVO.getFilteredMSISDN(),
                                "",
                                "Incoming transaction is less than or equal to that stored in DB for the number: " + p_requestVO.getFilteredMSISDN() + " in DB=" + dbTxnId + " from STK:" + inTxnId);
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_TEMPTRANSID_INVALID);
                    }
                    channelUserVO.getUserPhoneVO().setTempTransferID(tempTransID);
                }
                // Remove the Transac ID from the message Array
                final String[] newarr = new String[msgArrLength - 1];
                System.arraycopy(messageArray, 0, newarr, 0, msgArrLength - 1);
                p_requestVO.setRequestMessageArray(newarr);
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateTempTransactionID]", "", p_requestVO
                .getFilteredMSISDN(), "", "Not able to check for temp transaction ID for Request ID:" + p_requestVO.getRequestID() + " and MSISDN:" + p_requestVO
                .getFilteredMSISDN() + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), "Exiting ");
        }
    }

    /**
     * Method that will validate the Service Type and Version that the sender
     * MSISDN is using
     * 
     * @param p_requestVO
     * @param p_serviceKeywordCacheVO
     * @throws BTSLBaseException
     */
    public static void validateServiceTypeIdandVersion(RequestVO p_requestVO, ServiceKeywordCacheVO p_serviceKeywordCacheVO) throws BTSLBaseException {
        final String methodName = "validateServiceTypeIdandVersion";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), " p_serviceType=" + p_serviceKeywordCacheVO.getServiceType());
        }
        boolean isAllowedFlag = false;
        try {
            final FixedInformationVO fixedInformationVO = (FixedInformationVO) p_requestVO.getFixedInformationVO();
            final String allowedVersion = p_serviceKeywordCacheVO.getAllowedVersion();
            final String inVersion = fixedInformationVO.getServiceVersion();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, p_requestVO.getRequestIDStr(), " allowedVersion :: " + allowedVersion + " inVersion::" + inVersion);
            }
            final StringTokenizer stk = new StringTokenizer(BTSLUtil.NullToString(allowedVersion), ",");
            String tskStr = null;
            while (stk.hasMoreTokens()) {
                tskStr = stk.nextToken();
                if (inVersion.equalsIgnoreCase(tskStr)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, p_requestVO.getRequestIDStr(), " matched:: tskStr:" + tskStr + " inVersion:" + inVersion);
                    }
                    isAllowedFlag = true;
                    break;
                }
            }// end of while
            if (!isAllowedFlag) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, p_requestVO.getRequestIDStr(),
                        " For MSISDN:" + p_requestVO.getFilteredMSISDN() + " Incoming version does not matched with the allowed version");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateServiceTypeIdandVersion]",
                    p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "",
                    "Incoming version does not matched with the allowed version for Request ID:" + p_requestVO.getRequestID() + " and MSISDN:" + p_requestVO
                        .getFilteredMSISDN());
                throw new BTSLBaseException("PretupsBL", "validateTempTransactionID", PretupsErrorCodesI.CHNL_ERROR_SNDR_SVTYPE_VERSION_MISMATCH);
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler
                .handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateServiceTypeIdandVersion]", p_requestVO
                    .getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", "Not able to check Incoming version with the allowed version for Request ID:" + p_requestVO
                    .getRequestID() + " and MSISDN:" + p_requestVO.getFilteredMSISDN() + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }

    }// end of validateIdandVersion

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
        final String methodName = "checkServiceTypeAllowed";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered for Request ID:" + p_requestID + " MSISDN=" + p_msisdn + " p_serviceType=" + p_serviceType);
        }
        try {
            if (!p_allowedServiceTypes.contains(p_serviceType)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[checkServiceTypeAllowed]", p_requestID,
                p_msisdn, "",
                "Not able to check whether Service Type is in allowed List for Request ID:" + p_requestID + " and MSISDN:" + p_msisdn + " ,getting Exception=" + e
                    .getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting ");
        }
    }

    /**
     * Method to Process the Fixed Information coming with the mnessage
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public static void processFixedInfo(RequestVO p_requestVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
        final String methodName = "processFixedInfo";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), " MSISDN=" + p_requestVO.getFilteredMSISDN());
        }
        final String requestID = p_requestVO.getRequestIDStr();
        final FixedInformationVO fiVO = new FixedInformationVO();
        // ChannelUserVO channelUserVO=(ChannelUserVO)p_requestVO.getSenderVO()
        String fixedInfo = channelUserVO.getFxedInfoStr();
        try {
            if (fixedInfo != null) {
                int totalLength = fixedInfo.length();
                byte[] bytearr_org = new byte[14];
                final byte[] bytearr = new byte[14];
                bytearr_org = fixedInfo.getBytes();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, requestID, " original totalLength bytearr:" + Message348.bytesToBinHex(bytearr_org));
                }
                // added by sanjay to add the removed 0 in case of fixed length
                // at the end.
                System.arraycopy(bytearr_org, 0, bytearr, 0, totalLength);
                for (int i = 0; i < (13 - totalLength); i++) {
                    bytearr[totalLength + i] = 0x00;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, requestID, " Iteration=" + i + " original totalLength 2");
                    }
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, requestID, " original totalLength bytearr:" + Message348.bytesToBinHex(bytearr));
                }
                int ffind = -1;
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, requestID, " before removing ff");
                }
                fixedInfo = new String(bytearr);
                char c = 0xFF;
                do {
                    ffind = fixedInfo.indexOf(c); // ' ');
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, requestID, " inside while ffind:" + ffind + " If not -1 then replace them");
                    }
                    if (ffind != -1) {
                        fixedInfo = fixedInfo.replace(c, ' ');
                    }
                } while (ffind != -1);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, requestID, " original totalLength after replace of ff p_fixedInfo:" + fixedInfo + ": totalLength:" + fixedInfo.length());
                }
                totalLength = fixedInfo.length();
                if (totalLength >= 13) {
                    fixedInfo = fixedInfo.substring(0, 13);
                } else {
                    fixedInfo = fixedInfo.substring(0, 2);
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, requestID, "  original totalLength p_fixedInfo.length::" + fixedInfo.length());
                }
                totalLength = fixedInfo.length();
                String mcc = ""; //
                String mnc = ""; //
                String lac = ""; //
                String cid = ""; //

                // 1 Applet Version
                final int baver = fixedInfo.charAt(0);//
                fiVO.setApplicationVersion("" + baver);

                // 1Language
                final int blang = fixedInfo.charAt(1);
                fiVO.setLanguage("" + blang);

                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, requestID, " aver:" + baver + " lang:" + blang);
                }
                if (totalLength < 3) {
                    p_requestVO.setFixedInformationVO(fiVO);
                    return;
                }
                // 7 Siminfo
                final String simInfo = fixedInfo.substring(2, 9);
                int[] cellid = new int[4];
                cellid = getLocalInfo(simInfo);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, requestID, " simInfo:" + simInfo + " cellid:" + cellid);
                }

                mcc = "" + cellid[0];
                mnc = "" + cellid[1];
                lac = "" + cellid[2];
                cid = "" + cellid[3];
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, requestID, " mcc:" + mcc + " mnc:" + mnc + " lac:" + lac + " cid:" + cid);
                }

                fiVO.setMcc(mcc);
                fiVO.setMnc(mnc);
                fiVO.setLac(lac);
                fiVO.setCid(cid);

                // Position
                final int bpos = fixedInfo.charAt(9);
                fiVO.setPosition("" + bpos);

                // service id
                final int bsid = fixedInfo.charAt(10);
                fiVO.setServiceId("" + bsid);

                // service Version Major
                final int bsverm = fixedInfo.charAt(11);

                // service Version Minor
                final int bsvermi = fixedInfo.charAt(12);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, requestID, " bpos:" + bpos + " bsid:" + bsid + " bsverm=" + bsverm + " bsvermi=" + bsvermi);
                }

                // service Version Final
                fiVO.setServiceVersion("" + bsverm + "." + "" + bsvermi);
                p_requestVO.setFixedInformationVO(fiVO);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, requestID, " Exiting Fixed Info:: " + fiVO.toString());
                }
            } else {
                p_requestVO.setFixedInformationVO(null);
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler
                .handle(
                    EventIDI.SYSTEM_INFO,
                    EventComponentI.SYSTEM,
                    EventStatusI.RAISED,
                    EventLevelI.INFO,
                    "PretupsBL[processFixedInfo]",
                    p_requestVO.getRequestIDStr(),
                    p_requestVO.getFilteredMSISDN(),
                    "",
                    "Not able to traverse the Fixed Information for Request ID:" + p_requestVO.getRequestID() + " and MSISDN:" + p_requestVO.getFilteredMSISDN() + " ,getting Exception=" + e
                        .getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), "Exiting");
        }
    }

    /**
     * This method return array of int containing MCC, MNC, LAC & CID
     * Binary local info is passed as method param. This method uses the
     * following method addhighbits() & addlowbits
     * Creation date: (05/08/05 )
     * 
     * @return int[]
     * @param binaryString
     *            java.lang.String
     */
    public static int[] getLocalInfo(String binaryString) {
        final byte b[] = binaryString.getBytes();
        final int[] cell_id = new int[4];
        StringBuffer sb = new StringBuffer();

        // Determine MCC
        sb.append(b[0] & 0x0f);
        sb.append((b[0] & 0xf0) >>> 4);
        sb.append(b[1] & 0x0f);
        cell_id[0] = Integer.parseInt(sb.toString());

        // Determine MNC
        final int i1 = b[2] & 0x0f; // << -- low bit
        final int i2 = (b[2] & 0xf0) >>> 4; // << -- high bit
        sb = new StringBuffer();
        if (i2 == 15) {
            sb.append(i1);
        } else if (i1 > 9 || i2 > 9) {
            sb.append(i1 + i2);
        } else {
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
     * This method will return the Locale depending on the passed LangCode
     * 
     * @return locale object of Locale class
     * @param String
     *            langCode, language code
     */
    public static void getCurrentLocale(RequestVO p_requestVO, UserPhoneVO p_userPhoneVO) {
        final String methodName = "getCurrentLocale";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), " MSISDN=" + p_requestVO.getFilteredMSISDN());
        }
        final FixedInformationVO fixedInformationVO = (FixedInformationVO) p_requestVO.getFixedInformationVO();
        final String languageCode = fixedInformationVO.getLanguage();
        String languagesSupported = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.LANGAUGES_SUPPORTED);
        final String supLan = languagesSupported;// "1:en#US,2:hi#US";
        LOG.debug(methodName, p_requestVO.getRequestIDStr(), " Supported Language :" + supLan + " languageCode:" + languageCode);
        String lng = null;
        String country = null;

        try {
            final StringTokenizer stk = new StringTokenizer(supLan, ",");
            int ilIndx = -1;
            while (stk.hasMoreTokens()) {
                final String tmpStr = stk.nextToken();
                ilIndx = tmpStr.indexOf(languageCode);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, p_requestVO.getRequestIDStr(), " Language Code Index Found in tmpStr:" + tmpStr + " at posistion ilIndx:" + ilIndx);
                }
                if (ilIndx != -1) {
                    final int pindx = tmpStr.indexOf(":", ilIndx);
                    final int hindx = tmpStr.indexOf("#", ilIndx);
                    lng = tmpStr.substring(pindx + 1, hindx);
                    country = tmpStr.substring(hindx + 1);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, p_requestVO.getRequestIDStr(), " pindx:" + pindx + " hindx:" + hindx + " lng:" + lng + " country:" + country);
                    }
                    break;
                } else {
                    lng = p_userPhoneVO.getPhoneLanguage();
                    country = p_userPhoneVO.getCountry();
                }
            }// end while
            p_requestVO.setLocale(new Locale(lng, country));
            p_requestVO.setSenderLocale(p_requestVO.getLocale());

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestVO.getRequestIDStr(), " Exiting");
        }
    }// end of getCurrentLocale

    /**
     * Validates the MSISDN
     * 
     * @param p_receiverVO
     * @param p_msisdn
     * @throws BTSLBaseException
     *             21/04/07 : Added Param p_con for MNP
     */
    public static void validateMsisdn(Connection p_con, ReceiverVO p_receiverVO, String p_requestID, String p_msisdn) throws BTSLBaseException {
        final String methodName = "validateMsisdn";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Entered for p_msisdn= " + p_msisdn);
        }
        String[] strArr = null;
        try {
            if (BTSLUtil.isNullString(p_msisdn)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }
            p_msisdn = getFilteredMSISDN(p_msisdn);
            Integer minMsisdnLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH_CODE);
            Integer maxMsisdnLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE);
            p_msisdn = _operatorUtil.addRemoveDigitsFromMSISDN(p_msisdn);
            if ((p_msisdn.length() < (int)minMsisdnLength || p_msisdn.length() > (int)maxMsisdnLength)) {
                if ((int)minMsisdnLength != (int)maxMsisdnLength) {
                    strArr = new String[] { p_msisdn, String.valueOf((int)minMsisdnLength), String.valueOf((int)maxMsisdnLength) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTINRANGE, 0, strArr, null);
                } else {
                    strArr = new String[] { p_msisdn, String.valueOf((int)minMsisdnLength) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_LEN_NOTSAME, 0, strArr, null);
                }
            }
            NetworkPrefixVO  networkPrefixVO1 = null;
            networkPrefixVO1 = (NetworkPrefixVO) NetworkPrefixCache.getObject(getMSISDNPrefix(p_msisdn));
            if (networkPrefixVO1 == null) {
            
            	EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "RegistrationController[isUserRegistrationApplicable]", "", "", "", "Series =" + getMSISDNPrefix(p_msisdn));
                throw new BTSLBaseException("", "process", PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { p_msisdn }, null);
                }
            try {
                final long lng = Long.parseLong(p_msisdn);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                strArr = new String[] { p_msisdn };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTNUMERIC, 0, strArr, null);
            }
            p_receiverVO.setMsisdn(p_msisdn);
            if (LOG.isDebugEnabled() && p_receiverVO.getMsisdn() != null) {
                LOG.debug("", "*********************" + p_receiverVO.getMsisdn());
            }

            final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(p_msisdn, PretupsI.USER_TYPE_RECEIVER);
            if (networkPrefixVO == null) {
                strArr = new String[] { p_msisdn };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
            }
            /*
             * 21/04/07 Code Added for MNP
             * Preference to check whether MNP is allowed in system or not.
             * If yes then check whether Number has not been ported out, If yes
             * then throw error, else continue
             */
            Boolean isMNPAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED);
            if (isMNPAllowed) {
                boolean numberAllowed = false;
                if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_msisdn, "", PretupsI.PORTED_IN);
                    if (!numberAllowed) {
                        throw new BTSLBaseException("PretupsBL", "getNetworkDetails", PretupsErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_msisdn }, null);
                    }
                } else {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_msisdn, "", PretupsI.PORTED_OUT);
                    if (numberAllowed) {
                        throw new BTSLBaseException("PretupsBL", "getNetworkDetails", PretupsErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_msisdn }, null);
                    }
                }
            }
            // 21/04/07: MNP Code End
            p_receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
            p_receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
            p_receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateMsisdn]", "", "", "",
                "Exception while validating msisdn" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Exiting for p_msisdn= " + p_msisdn);
        }
    }

    /**
     * Validates the amount
     * 
     * @param p_c2sTransferVO
     * @param p_requestAmount
     * @throws BTSLBaseException
     */
    public static void validateAmount(C2STransferVO p_c2sTransferVO, String p_requestAmount) throws BTSLBaseException {
        final String methodName = "validateAmount";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_c2sTransferVO.getRequestID(), "Entered p_requestAmount=" + p_requestAmount);
        }
        String[] strArr = null;
        double requestAmt = 0;
        String msgRequestAmount = null;
        try {
            if (BTSLUtil.isNullString(p_requestAmount)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_BLANK);
            }
            if (p_c2sTransferVO.getRequestGatewayType().equals(Constants.getProperty("GATEWAYTYPES_MIN_DENOMINATION_ALLOWED"))) {
                msgRequestAmount = getDisplayAmount(Long.parseLong(p_requestAmount));
            } else {
                msgRequestAmount = p_requestAmount;
            }
            try {
                requestAmt = Double.parseDouble(p_requestAmount);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_NOTNUMERIC, 0, strArr, null);
            }
            if (requestAmt <= 0) {
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_LESSTHANZERO, 0, strArr, null);
            }
            if (p_c2sTransferVO.getRequestGatewayType().equals(Constants.getProperty("GATEWAYTYPES_MIN_DENOMINATION_ALLOWED"))) {
                p_c2sTransferVO.setTransferValue(Long.parseLong(p_requestAmount));
                p_c2sTransferVO.setRequestedAmount(Long.parseLong(p_requestAmount));
            } else {
                p_c2sTransferVO.setTransferValue(getSystemAmount(p_requestAmount));
                p_c2sTransferVO.setRequestedAmount(getSystemAmount(p_requestAmount));
            }
            if (p_c2sTransferVO.getServiceType().equals("RR") || p_c2sTransferVO.getServiceType().equals("IR")) {
                p_c2sTransferVO.getIatTransferItemVO().setQuantity(p_c2sTransferVO.getRequestedAmount());
            }

            Object objVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.C2S_MINTRNSFR_AMOUNT, p_c2sTransferVO.getNetworkCode());
            if (objVal != null) {
                if (p_c2sTransferVO.getRequestedAmount() < ((Long) objVal).longValue()) {
                    strArr = new String[] { msgRequestAmount, PretupsBL.getDisplayAmount(((Long) objVal).longValue()) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_LESSTHANALLOWED, 0, strArr, null);
                }
            }
            objVal = null;
            objVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.C2S_MAXTRNSFR_AMOUNT, p_c2sTransferVO.getNetworkCode());
            if (objVal != null) {
                if (p_c2sTransferVO.getRequestedAmount() > ((Long) objVal).longValue()) {
                    strArr = new String[] { msgRequestAmount, PretupsBL.getDisplayAmount(((Long) objVal).longValue()) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_MORETHANALLOWED, 0, strArr, null);
                }
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateAmount]", "", "", "",
                "Exception while validating amount" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_c2sTransferVO.getRequestID(), "Exiting ");
        }
    }

    /**
     * Method to get the Product from the service type list
     * 
     * @param p_con
     * @param p_c2stransferVO
     * @param p_type
     * @param p_module
     * @throws BTSLBaseException
     */
    public static void getProductFromServiceType(Connection p_con, TransferVO p_transferVO, String p_type, String p_module) throws BTSLBaseException {
        final String methodName = "getProductFromServiceType";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_serviceType:" + p_transferVO.getServiceType() + " p_type=" + p_type + " p_module=" + p_module);
        }
        String srvcProdMappingAllowed = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED);
        try {
            String productType = null;
            if (srvcProdMappingAllowed.contains(p_type)) {
                p_transferVO.setSelectorCode(p_transferVO.getSubService());
            } else {
                p_transferVO.setSelectorCode(PretupsI.DEFAULT_SUBSERVICE);
            }
            final ListValueVO listValueVO = NetworkProductServiceTypeCache.getProductServiceValueVO(p_type, p_transferVO.getSelectorCode());
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Got listValueVO=" + listValueVO + " for ServiceType=" + p_type + " p_module=" + p_module+" PRODUCTCODE"+listValueVO.getCodeName());
            }
            String prodCode = null;
            if (listValueVO != null) {
                productType = listValueVO.getLabel();
                prodCode = listValueVO.getCodeName();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Got prodCode=" + prodCode+listValueVO.getValue()+listValueVO.getType());
                }
                p_transferVO.setDifferentialAllowedForService(listValueVO.getValue());
                p_transferVO.setGiveOnlineDifferential(listValueVO.getType());
                final ProductVO channelProductsVO = NetworkProductCache.getObject(p_module, productType, p_transferVO.getRequestedAmount(), p_type, prodCode);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Got channelProductsVO=" + channelProductsVO);
                }
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

                    if (!channelProductsVO.getStatus().equals(PretupsI.PRODUCT_STATUS_ACTIVE)) {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.PRODUCT_NOT_AVAILABLE);
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "before networkProductVO=" + channelProductsVO);
                    }
                    final NetworkProductVO networkProductVO = NetworkProductServiceTypeCache.getNetworkProductDetails(p_transferVO.getNetworkCode(), channelProductsVO
                        .getProductCode());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "after networkProductVO=" + networkProductVO);
                    }
                    if (networkProductVO != null) {
                        if (networkProductVO.getStatus().equals(PretupsI.NETWORK_PRODUCT_STATUS_SUSPEND)) {
                            // ChangeID=LOCALEMASTER
                            // which language message to be send is determined
                            // from the locale master
                            if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_transferVO.getLocale())).getMessage())) {
                                p_transferVO.setSenderReturnMessage(networkProductVO.getLanguage1Message());
                            } else {
                                p_transferVO.setSenderReturnMessage(networkProductVO.getLanguage2Message());
                            }
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.PRODUCT_NETWK_SUSPENDED);
                        } else if (networkProductVO.getStatus().equalsIgnoreCase(PretupsI.NETWORK_PRODUCT_STATUS_DELETE)) {
                            // ChangeID=LOCALEMASTER
                            // which language message to be send is determined
                            // from the locale master
                            if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_transferVO.getLocale())).getMessage())) {
                                p_transferVO.setSenderReturnMessage(networkProductVO.getLanguage1Message());
                            } else {
                                p_transferVO.setSenderReturnMessage(networkProductVO.getLanguage2Message());
                            }
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.PRODUCT_NETWK_DELETED);
                        } else if (!((networkProductVO.getUsage().equals(PretupsI.NETWK_PRODUCT_USAGE_BOTH)) || (networkProductVO.getUsage()
                            .equals(PretupsI.NETWK_PRODUCT_USAGE_CONSUMPTION)))) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.PRODUCT_NETWK_CONSUM_NOTALLOWED);
                        }
                    } else {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.PRODUCT_NOT_ASSOCIATED_WITH_NETWK);
                    }
                } else {
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.PRODUCT_NOT_FOUND);
                }
            } else {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_SERTYPE_PRODUCT_NOT_FOUND);
            }
        } catch (BTSLBaseException be) {
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[getProductFromServiceType]",p_transferVO.getTransferID(),p_transferVO.getSenderMsisdn(),p_transferVO.getNetworkCode(),"Network Product Service mapping problem for service type :"+be.getMessage())
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getProductFromServiceType]", p_transferVO
                .getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Exception while check product details for service type :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, p_transferVO.getTransferID(), "Exiting for Transfer ID:" + p_transferVO.getTransferID());
            }
        }
    }

    /**
     * Generates the Transfer ID For C2S
     * 
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void generateC2STransferID(TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "generateC2STransferID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered ");
        }
        long newTransferID = 0;
        String transferID = null;
        try {
            final ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
            newTransferID = IDGenerator.getNextID(PretupsI.ID_GEN_C2S_TRANSFER_NO, BTSLUtil.getFinancialYearLastDigits(4), receiverVO.getNetworkCode(), p_transferVO
                .getCreatedOn());
            if (newTransferID == 0) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = _operatorUtil.formatC2STransferID(p_transferVO, newTransferID);
            if (transferID == null) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting with TransferID=" + transferID);
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
        final String methodName = "getServiceClassObject";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered with p_networkCode=" + p_networkCode + " p_module=" + p_module + " p_serviceClassCode=" + p_serviceClassCode + " p_preferenceCode=" + p_preferenceCode + " p_isCheckMoreThanAllReqd=" + p_isCheckMoreThanAllReqd + "p_allServiceClassID=" + p_allServiceClassID);
        }
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
                    final PreferenceCacheVO preferenceCacheVO = (PreferenceCacheVO) serviceObjVal;
                   if (p_isCheckMoreThanAllReqd && preferenceCacheVO.getNoOfOtherPrefOtherThanAll() > 0 && !p_module.equals(PretupsI.P2P_MODULE)) {
                        return null;
                    } else {
                        serviceObjVal = PreferenceCache.getServicePreference(p_preferenceCode, p_networkCode, p_module, p_serviceClassCode, true);
                    }
                } else if (!p_isCheckMoreThanAllReqd) {
                    serviceObjVal = PreferenceCache.getServicePreference(p_preferenceCode, p_networkCode, p_module, p_serviceClassCode, true);
                }
            } else {
                serviceObjVal = PreferenceCache.getServicePreference(p_preferenceCode, p_networkCode, p_module, p_serviceClassCode, false);
                if (serviceObjVal == null) {
                    serviceObjVal = PreferenceCache.getServicePreference(p_preferenceCode, p_networkCode, p_module, p_allServiceClassID, true);
                    return serviceObjVal;
                }
            }
            return serviceObjVal;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getServiceClassObject]", "", "", "",
                "Exception while getting the Service class object from cache" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exting with serviceObjVal=" + serviceObjVal);
            }
        }
    }

    /**
     * Generates the Adjustment ID
     * 
     * @param p_locationCode
     * @param p_currentDate
     * @return
     * @throws BTSLBaseException
     */
    public static String generateAdjustmentID(String p_locationCode, Date p_currentDate) throws BTSLBaseException {
        final String methodName = "generateAdjustmentID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered with p_locationCode=" + p_locationCode);
        }
        long newTransferID = 0;
        String transferID = null;
        try {
            newTransferID = IDGenerator.getNextID(PretupsI.ID_GEN_ADJUSTMENT_NO, BTSLUtil.getFinancialYearLastDigits(4), p_locationCode);
            if (newTransferID == 0) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_ADJUSTMENTID);
            }
            transferID = _operatorUtil.formatAdjustmentTxnID(p_locationCode, p_currentDate, newTransferID);
            if (transferID == null) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_ADJUSTMENTID);
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_ADJUSTMENTID,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting with TransferID=" + transferID);
        }
        return transferID;
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
        final String methodName = "addTransferCreditBackDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_transferID, "Entered with p_transferItemVO=" + p_transferItemVO);
        }
        try {
            final ArrayList itemList = new ArrayList();
            itemList.add(0, p_transferItemVO);
            final int updateCount = _transferDAO.addTransferItemDetails(p_con, p_transferID, itemList);
            if (updateCount <= 0) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.error(methodName, "  Exception while making credit back entry :" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[addTransferCreditBackDetails]",
                p_transferID, "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_transferID, "Exiting ");
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
    public static void addValExtTransferCreditBackDetails(Connection p_con, TransferVO p_transferVO, TransferItemVO p_transferItemVO) throws BTSLBaseException {
        final String methodName = "addTransferCreditBackDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_transferVO.getValExtTransferID(), "Entered with p_transferItemVO=" + p_transferItemVO);
        }
        try {
            final ArrayList itemList = new ArrayList();
            itemList.add(0, p_transferItemVO);
            final int updateCount = _transferDAO.addValExtTransferItemDetails(p_con, p_transferVO, itemList);
            if (updateCount <= 0) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[addTransferCreditBackDetails]", p_transferVO
                .getValExtTransferID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_transferVO.getValExtTransferID(), "Exiting ");
        }
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
        final String methodName = "validateServiceClassChecks";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_transferItemVO.getTransferID(),
                "Entered with p_transferItemVO=" + p_transferItemVO + " p_module=" + p_module + " p_serviceType=" + p_serviceType);
        }
        try {
            // ServiceClassDAO serviceClassDAO=new ServiceClassDAO()
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

            // HashMap<String, ServiceClassVO> serviceMap=null
            // serviceMap=serviceClassDAO.loadServiceClassInfoByCodeWithAll(p_con,p_transferItemVO.getServiceClassCode(),p_transferItemVO.getInterfaceID())
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
             * +p_transferItemVO.getServiceClassCode())
             * throw new
             * BTSLBaseException("PretupsBL","validateServiceClassChecks"
             * ,PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_NOTFOUND)
             * }
             * else
             * {
             */
            serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(p_transferItemVO.getServiceClassCode(), p_transferItemVO.getInterfaceID());
            if (serviceClassVO == null) {
                serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(PretupsI.ALL, p_transferItemVO.getInterfaceID());
                if (serviceClassVO == null) {
                    EventHandler
                        .handle(
                            EventIDI.SYSTEM_ERROR,
                            EventComponentI.SYSTEM,
                            EventStatusI.RAISED,
                            EventLevelI.INFO,
                            "PretupsBL[validateServiceClassChecks]",
                            p_transferItemVO.getTransferID(),
                            p_transferItemVO.getMsisdn(),
                            "",
                            "No Service Class defined for " + PretupsI.ALL + " and Interface ID " + p_transferItemVO.getInterfaceID() + " After searching for Service class Code=" + p_transferItemVO
                                .getServiceClassCode());
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_NOTFOUND);
                } else {
                    p_transferItemVO.setUsingAllServiceClass(true);
                }
            }
            // p_transferItemVO.setUsingAllServiceClass(true)
            // _transferVO.setServiceClassMap(serviceMap)
            // }
            p_transferItemVO.setServiceClassCode(serviceClassVO.getServiceClassCode());
            if (PretupsI.SUSPEND.equals(serviceClassVO.getStatus())) {
                p_transferItemVO.setServiceClass(serviceClassVO.getServiceClassId());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateServiceClassChecks]",
                    p_transferItemVO.getTransferID(), p_transferItemVO.getMsisdn(), "", "Service Class " + serviceClassVO.getServiceClassId() + " is suspended");
                // Chenged by ankit Z on date 2/8/06 to make saperate message
                // for sender and receiver suspend
                if (p_transferItemVO.getUserType().equals(PretupsI.USER_TYPE_SENDER)) {
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_SUSPEND);
                } else {
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_SUSPEND_R);
                }
            } else {
                // Change for OCA requirement to disallow some service class but
                // allow some specific numbers of that service class
                try {
                    serviceClassString = new String(Constants.getProperty("CHECK_FOR_SERVICE_CLASSES"));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "serviceClassString=" + serviceClassString);
                    }
                } catch (Exception e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, " No service class has been defined to be checked for accessibility");
                    }
                    LOG.errorTrace(methodName, e);
                }

                try {
                    serviceTypeString = new String(Constants.getProperty("CHECK_FOR_SERVICE_TYPES"));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "serviceTypeString=" + serviceTypeString);
                    }
                } catch (Exception e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, " No service type has been defined to be checked for accessibility");
                    }
                    LOG.errorTrace(methodName, e);
                }

                try {
                    userTypeString = new String(Constants.getProperty("CHECK_FOR_USER_TYPES"));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, " userTypeString=" + userTypeString);
                    }
                } catch (Exception e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, " No user type has been defined to be checked for accessibility");
                    }
                    LOG.errorTrace(methodName, e);
                }

                if (!BTSLUtil.isNullString(serviceClassString) && BTSLUtil.isStringContain(serviceClassString, serviceClassVO.getServiceClassId()) && !BTSLUtil
                    .isNullString(serviceTypeString) && BTSLUtil.isStringContain(serviceTypeString, p_serviceType) && !BTSLUtil.isNullString(userTypeString) && BTSLUtil
                    .isStringContain(userTypeString, p_transferItemVO.getUserType())) {
                    try {
                        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
                        operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
                    } catch (Exception e) {
                        LOG.errorTrace(methodName, e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateServiceClassChecks]",
                            "", "", "", "Exception while loading the class at the call:" + e.getMessage());
                    }
                    isExist = operatorUtili.checkMsisdnServiceClassMapping(p_con, p_transferItemVO.getMsisdn(), p_serviceType, serviceClassVO.getServiceClassId(), p_module,
                        p_transferItemVO.getUserType());
                    if (!isExist) {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_SRVCECLSS_SENDER_NOT_ALLOWED);
                    }
                }
                // Change block end

                p_transferItemVO.setServiceClass(serviceClassVO.getServiceClassId());
                if (p_transferItemVO.getUserType().equals(PretupsI.USER_TYPE_SENDER)) {
                    if (p_module.equals(PretupsI.P2P_MODULE)) {
                        if (PretupsI.YES.equals(serviceClassVO.getP2pSenderSuspend())) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_SENDER_SUSPEND);
                        }
                        allowedAccountStatus = serviceClassVO.getP2pSenderAllowedStatus();
                    }
                } else {
                    if (p_module.equals(PretupsI.P2P_MODULE)) {
                        if (PretupsI.YES.equals(serviceClassVO.getP2pReceiverSuspend())) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_P2P_RECEIVER_SUSPEND);
                        }
                        allowedAccountStatus = serviceClassVO.getP2pReceiverAllowedStatus();
                    } else {
                        if (PretupsI.YES.equals(serviceClassVO.getC2sReceiverSuspend())) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_C2S_RECEIVER_SUSPEND);
                        }
                        allowedAccountStatus = serviceClassVO.getC2sReceiverAllowedStatus();
                    }
                }
                if (!BTSLUtil.isNullString(allowedAccountStatus) && !PretupsI.ALL.equals(allowedAccountStatus)) {
                    final String[] allowedStatus = allowedAccountStatus.split(",");
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "Received Account Status=" + p_transferItemVO.getAccountStatus());
                        LOG.debug(methodName, "Is null=" + BTSLUtil.isNullString(p_transferItemVO.getAccountStatus()));
                    }
                    if (!BTSLUtil.isNullString(p_transferItemVO.getAccountStatus()) && !Arrays.asList(allowedStatus).contains(p_transferItemVO.getAccountStatus())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, p_transferItemVO.getTransferID(),
                                "Account Status =" + p_transferItemVO.getAccountStatus() + " is not allowed in the allowed List for interface ID=" + p_transferItemVO
                                    .getInterfaceID());
                        }

                        // check for allowed services for Number Back
                        allowedNumberBackServices = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.ALWD_NUMBCK_SERVICES, _transferVO.getNetworkCode());
                        allowedService = allowedNumberBackServices.split(",");

                        if (!Arrays.asList(allowedService).contains(_transferVO.getServiceType())) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("validateServiceClassChecks allowedService:-#####",
                                    allowedService + "ServiceType:-#####" + _transferVO.getServiceType() + "Exception");
                            }
                            p_transferItemVO.setNumberBackAllowed(false);
                            final String[] strArr = new String[] { p_transferItemVO.getMsisdn(), String
                                .valueOf(PretupsBL.getDisplayAmount(p_transferItemVO.getRequestValue())), p_transferItemVO.getAccountStatus() };
                            // throw new
                            // BTSLBaseException("PretupsBL","validateServiceClassChecks",PretupsErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC,0,strArr,null)
                            if (p_transferItemVO.getUserType().equals(PretupsI.USER_TYPE_SENDER)) {
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_SEN, 0, strArr, null);
                            } else {
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC, 0, strArr, null);
                            }
                        } else {
                            final String preferenceKey = _transferVO.getServiceType() + PreferenceI.ALWD_ACC_STATUS_NUMBCK;
                            numberBackStatus = (String) PreferenceCache.getControlPreference(preferenceKey, _transferVO.getNetworkCode(), p_transferItemVO.getInterfaceID());
                            if (BTSLUtil.isNullString(numberBackStatus)) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("validateServiceClassChecks numberBackStatus is null:-#####", numberBackStatus);
                                }
                                final String[] strArr = new String[] { p_transferItemVO.getMsisdn(), String.valueOf(PretupsBL.getDisplayAmount(p_transferItemVO
                                    .getRequestValue())), p_transferItemVO.getAccountStatus() };
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC, 0, strArr, null);
                            }
                            if (!numberBackStatus.equals(p_transferItemVO.getAccountStatus())) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("validateServiceClassChecks numberBackStatus:-#####", numberBackStatus + "Account Status:-#####" + p_transferItemVO
                                        .getAccountStatus() + "Exception");
                                }
                                p_transferItemVO.setNumberBackAllowed(false);
                                final String[] strArr = new String[] { p_transferItemVO.getMsisdn(), String.valueOf(PretupsBL.getDisplayAmount(p_transferItemVO
                                    .getRequestValue())), p_transferItemVO.getAccountStatus() };
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC, 0, strArr, null);
                            }

                            p_transferItemVO.setNumberBackAllowed(true);
                        }

                        if (!p_transferItemVO.isNumberBackAllowed()) {

                            if (p_transferItemVO.getUserType().equals(PretupsI.USER_TYPE_SENDER)) {
                                final String[] strArr = new String[] { p_transferItemVO.getMsisdn(), String.valueOf(PretupsBL.getDisplayAmount(p_transferItemVO
                                    .getRequestValue())) };
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_SEN, 0, strArr, null);
                            } else {
                                final String[] strArr = new String[] { p_transferItemVO.getMsisdn(), String.valueOf(PretupsBL.getDisplayAmount(p_transferItemVO
                                    .getRequestValue())), p_transferItemVO.getAccountStatus() };
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC, 0, strArr, null);
                            }
                        }
                    }
                }
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateServiceClassChecks]",
                p_transferItemVO.getTransferID(), p_transferItemVO.getMsisdn(), "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_transferItemVO.getTransferID(), "Exiting ");
        }
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
        final String methodName = "loadRecieverControlLimits";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Eneterd with p_transferVO::=" + p_transferVO);
        }
        final ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
        final Date currentDate = p_transferVO.getCreatedOn();// for
                                                             // Aktel::AshishS
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Entered MSISDN=" + receiverVO.getMsisdn());
        }
        try {
            //if (mrpBlockTimeAllowed) 
        	{
                final boolean isDataFound = _subscriberControlDAO.loadSubscriberControlDetails(p_con, receiverVO);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, p_requestID, "MSISDN=" + receiverVO.getMsisdn() + " Data Found=" + isDataFound);
                }

                // added by nilesh: for MRP block time
                receiverVO.setRequestedMRP(p_transferVO.getRequestedAmount());
                receiverVO.setRequestedServiceType(p_transferVO.getServiceType());
                receiverVO.setModule(p_transferVO.getModule());
   
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
                    final int addCount = _subscriberControlDAO.addSubscriberControlDetails(p_con, receiverVO);
                    if (addCount <= 0) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[loadRecieverControlLimits]",
                            p_requestID, receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Not able to add in subscriber control table");
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
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
                        final int updateCount = _subscriberControlDAO.updateSubscriberControlDetails(p_con, receiverVO);
                        if (updateCount <= 0) {
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[loadRecieverControlLimits]",
                                p_requestID, receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Not able to update subscriber details in transaction control table");
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
                        }
                    }
                    else if(!(p_transferVO.getSenderMsisdn().equals(p_transferVO.getReceiverMsisdn())))
    				{
                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.RECEIVER_LAST_REQ_UNDERPROCESS_R));
                        final String[] strArr = new String[] { receiverVO.getMsisdn() };
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.RECEIVER_LAST_REQ_UNDERPROCESS_S, 0, strArr, null);
                    }
                    
                } else // Mark under process
                {
                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    receiverVO.setLastTransferOn(currentDate);// for
                    // Aktel::AshishS
                    // Here refresh the daily,weekly and monthly thresholds on
                    // change of day, week or month.
                    checkResetCountersAfterPeriodChange(receiverVO, new Date());
                    final int updateCount = _subscriberControlDAO.updateSubscriberControlDetails(p_con, receiverVO);
                    if (updateCount <= 0) {
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[loadRecieverControlLimits]",
                            p_requestID, receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Not able to update subscriber details in transaction control table");
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
                    }
                }
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[loadRecieverControlLimits]", p_requestID,
                receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Not able to get the reciever controls");
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Exiting");
        }
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
        final String methodName = "unmarkReceiverLastRequest";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Entered MSISDN=" + p_receiverVO.getMsisdn() + " Restricted Subscriber VO: " + p_receiverVO.getRestrictedSubscriberVO());
        }
        try {
            // code block to update the count and amount of the restricted
            // subscriber
            final RestrictedSubscriberVO restrictedSubscriberVO = (RestrictedSubscriberVO) p_receiverVO.getRestrictedSubscriberVO();
            if (restrictedSubscriberVO != null && !BTSLUtil.isNullString(restrictedSubscriberVO.getTempStatus()) && !PretupsErrorCodesI.TXN_STATUS_FAIL
                .equalsIgnoreCase(restrictedSubscriberVO.getTempStatus())) {
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
                 * ,PretupsErrorCodesI.ERROR_EXCEPTION)
                 */}// end of code block for updating restricted subscriber
            // details

            p_receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
            //if (mrpBlockTimeAllowed) {
                final int updateCount = _subscriberControlDAO.updateSubscriberControlDetails(p_con, p_receiverVO);
                if (updateCount <= 0) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[unmarkReceiverLastRequest]",
                        p_requestID, p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), "Not able to update subscriber details in transaction control table");
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
                } else {
                    p_receiverVO.setUnmarkRequestStatus(false);
                }
           /* } else {
                p_receiverVO.setUnmarkRequestStatus(false);
            }*/
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[unmarkReceiverLastRequest]", p_requestID,
                p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Exiting");
        }
    }

    /**
     * Gets the service status for the service type in network
     * 
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void validateNetworkService(TransferVO p_transferVO) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "validateNetworkService",
                p_transferVO.getRequestID(),
                "Enetered Validation For Module Code=" + p_transferVO.getModule() + " Sender Network Code=" + p_transferVO.getNetworkCode() + " Receiver Network Code=" + p_transferVO
                    .getReceiverNetworkCode() + "Service Type=" + p_transferVO.getServiceType());
        }
        final NetworkServiceVO networkServiceVO = NetworkServicesCache.getObject(p_transferVO.getModule(), p_transferVO.getNetworkCode(), p_transferVO
            .getReceiverNetworkCode(), p_transferVO.getServiceType());
        if (networkServiceVO == null) {
            EventHandler
                .handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateNetworkService]",
                    p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Service not defined between Sender Network " + p_transferVO
                        .getNetworkCode() + " and Receiver network" + p_transferVO.getReceiverNetworkCode() + " Service Type=" + p_transferVO.getServiceType());
            throw new BTSLBaseException("PretupsBL", "validateNetworkService", PretupsErrorCodesI.ERROR_NETWORK_SERVICE_STATUS_NOTEXIST, 0, new String[] { p_transferVO
                .getReceiverNetworkCode() }, null);
        } else if (!networkServiceVO.getStatus().equals(PretupsI.YES)) {
            // ChangeID=LOCALEMASTER
            // which language message to be send is determined from the locale
            // master
            if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_transferVO.getLocale())).getMessage())) {
                p_transferVO.setSenderReturnMessage(networkServiceVO.getLanguage1Message());
            } else {
                p_transferVO.setSenderReturnMessage(networkServiceVO.getLanguage2Message());
            }

            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateNetworkService]", p_transferVO
                .getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "Service Status is suspended between Sender Network " + p_transferVO
                .getNetworkCode() + " and Receiver network" + p_transferVO.getReceiverNetworkCode() + " Service Type=" + p_transferVO.getServiceType());
            throw new BTSLBaseException("PretupsBL", "validateNetworkService", PretupsErrorCodesI.ERROR_NETWORK_SERVICE_STATUS_SUSPENDED, 0, new String[] { p_transferVO
                .getReceiverNetworkCode() }, null);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("validateNetworkService", p_transferVO.getRequestID(), "Exiting");
        }
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
        LOG.debug("validateNumberInRoutingDatabase", "Entered with p_msisdn=" + p_msisdn + " p_inrefaceType=" + p_interfaceType);
        final String METHOD_NAME = "validateNumberInRoutingDatabase";
        ListValueVO listValueVO = null;
        try {
            listValueVO = new RoutingTxnDAO().loadInterfaceID(p_con, p_msisdn, p_interfaceType);
            return listValueVO;

        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateNumberInRoutingDatabase]", "",
                p_msisdn, "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateNumberInRoutingDatabase", PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        } finally {
            LOG.debug("validateNumberInRoutingDatabase", "Exiting listValueVO=" + listValueVO);
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
        final String methodName = "validateNumberInWhiteList";
        LOG.debug(methodName, "Entered with p_msisdn=" + p_msisdn);
        WhiteListVO whiteListVO = null;
        try {
            whiteListVO = new WhiteListDAO().loadInterfaceDetails(p_con, p_msisdn);
            return whiteListVO;
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateNumberInWhiteList]", "", p_msisdn,
                "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
            LOG.debug(methodName, "Exiting WhiteListVO=" + whiteListVO);
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
        final String methodName = "getGatewayHandlerObj";

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered handlerClassName = " + handlerClassName);
        }
        GatewayParsersI handlerObj = null;
        try {
            handlerObj = (GatewayParsersI) Class.forName(handlerClassName).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getGatewayHandlerObj]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting...");
        }
        return handlerObj;
        // return _smsHandlerObj
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
        final String methodName = "validateServiceClass";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                p_transferID,
                "Entered with p_interfaceID=" + p_interfaceID + " p_serviceClassCode=" + p_serviceClassCode + "p_allowedStatus=" + p_allowedStatus + "p_userType=" + p_userType + "p_module=" + p_module);
        }
        String serviceClassID = null;
        String allowedAccountStatus = null;
        try {
            final ServiceClassVO serviceClassVO = ServiceClassInfoByCodeCache.getServiceClassByCode(p_serviceClassCode, p_interfaceID);
            if (serviceClassVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateServiceClass]", p_transferID, "",
                    "", "No Service Class defined for " + p_serviceClassCode + " and Interface ID " + p_interfaceID);
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_NOTFOUND);
            }
            serviceClassID = serviceClassVO.getServiceClassId();

            if (PretupsI.SUSPEND.equals(serviceClassVO.getStatus())) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateServiceClass]", p_transferID, "",
                    "", "Service Class " + serviceClassVO.getServiceClassId() + " is suspended");
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_SUSPEND);
            } else {

                if (p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                    if (p_module.equals(PretupsI.P2P_MODULE)) {
                        if (PretupsI.YES.equals(serviceClassVO.getP2pSenderSuspend())) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_SENDER_SUSPEND);
                        }
                        allowedAccountStatus = serviceClassVO.getP2pSenderAllowedStatus();
                    }
                } else {
                    if (p_module.equals(PretupsI.P2P_MODULE)) {
                        if (PretupsI.YES.equals(serviceClassVO.getP2pReceiverSuspend())) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_P2P_RECEIVER_SUSPEND);
                        }
                        allowedAccountStatus = serviceClassVO.getP2pReceiverAllowedStatus();
                    } else {
                        if (PretupsI.YES.equals(serviceClassVO.getC2sReceiverSuspend())) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_SRVCECLSS_C2S_RECEIVER_SUSPEND);
                        }
                        allowedAccountStatus = serviceClassVO.getC2sReceiverAllowedStatus();
                    }
                }
                if (!BTSLUtil.isNullString(allowedAccountStatus) && !PretupsI.ALL.equals(allowedAccountStatus)) {
                    final String[] allowedStatus = allowedAccountStatus.split(",");
                    if (!Arrays.asList(allowedStatus).contains(p_allowedStatus)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, p_transferID, "Account Status =" + p_allowedStatus + " is not allowed in the allowed List for interface ID=" + p_interfaceID);
                        }
                        if (p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                            final String[] strArr = new String[] { p_msisdn, PretupsBL.getDisplayAmount(p_requestValue) };
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_SEN, 0, strArr, null);
                        } else {
                            final String[] strArr = new String[] { p_msisdn, PretupsBL.getDisplayAmount(p_requestValue), p_allowedStatus };
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INTFCE_ACCOUNTSTATUS_NOTALLOWED_REC, 0, strArr, null);
                        }
                    }
                }
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateServiceClassChecks]", p_transferID,
                p_msisdn, "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_transferID, "Exiting with serviceClassID=" + serviceClassID);
        }
        return serviceClassID;
    }

    /**
     * Method to get the Selecor code from value in the request
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public static void getSelectorValueFromCode(RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "getSelectorValueFromCode";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered for Request ID=" + p_requestVO.getRequestIDStr() + ":::p_requestVO.getReqSelector():" + p_requestVO.getReqSelector());
        }
        try {
            // changed for CRE_INT_CR00029 by ankit Zindal
            // This is changed to On-Peak and SMS&Data Credit management
            if (!p_requestVO.isPlainMessage()) {
                if (!BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                    if ("Y".equalsIgnoreCase(Constants.getProperty("STK_CONVERSION_REQD"))) {
                        if (PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                            p_requestVO.setReqSelector(Constants.getProperty(p_requestVO.getReqSelector()));
                        } else {
                            final String requestSelectorValue = new CryptoUtil().bytesToBinHex(p_requestVO.getReqSelector().getBytes());
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(methodName,
                                    "Entered for Request ID=" + p_requestVO.getRequestIDStr() + ":::p_requestVO.getReqSelector() Hex Value : " + requestSelectorValue);
                            }
                            p_requestVO.setReqSelector(Constants.getProperty(requestSelectorValue));
                        }
                    } else {
                        p_requestVO.setReqSelector(Constants.getProperty(p_requestVO.getReqSelector()));
                    }
                    if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
                    }
                }
            }// else part is added by ankit z on date 2/8/06 to check the
             // validity of selector in case of plain SMS
            else {
                try {
                    if (!BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                        // LookupsCache.getObject(PretupsI.SUB_SERVICES,p_requestVO.getReqSelector())
                        // Changed on 25/05/07 for Service Typoe wise Selector
                        if (!ServiceSelectorMappingCache.getServiceSelectorMap().containsKey(p_requestVO.getServiceType() + "_" + p_requestVO.getReqSelector())) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
                        }
                    }
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_SELECTOR_VALUE,e);
                }
            }
            /*
             * int code=-1;
             * if(p_requestVO.isPlainMessage())
             * {
             * try
             * {
             * code=Integer.parseInt(p_requestVO.getReqSelector())
             * }
             * catch(Exception e)
             * {
             * if(LOG.isDebugEnabled())LOG.debug("getSelectorValueFromCode",
             * "Invalid Selector Value for Request ID="
             * +p_requestVO.getRequestIDStr())
             * throw new
             * BTSLBaseException("PretupsBL","getSelectorValueFromCode"
             * ,PretupsErrorCodesI.ERROR_INVALID_SELECTOR_VALUE)
             * }
             * }
             * else
             * {
             * if("en".equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
             * {
             * if(Constants.getProperty("CVG_ENGLISH_CODE").equalsIgnoreCase(
             * p_requestVO.getReqSelector()))
             * code=PretupsI.CHNL_SELECTOR_CVG_VALUE
             * else
             * if(Constants.getProperty("VG_ENGLISH_CODE").equalsIgnoreCase(
             * p_requestVO.getReqSelector()))
             * code=PretupsI.CHNL_SELECTOR_VG_VALUE
             * else if(Constants.getProperty("C_ENGLISH_CODE").equalsIgnoreCase(
             * p_requestVO.getReqSelector()))
             * code=PretupsI.CHNL_SELECTOR_C_VALUE
             * }
             * else
             * {
             * if((Constants.getProperty("CVG_UNICODE_"+p_requestVO.getLocale().
             * getLanguage
             * ().toUpperCase())).equalsIgnoreCase(p_requestVO.getReqSelector
             * ()))
             * code=PretupsI.CHNL_SELECTOR_CVG_VALUE
             * else
             * if((Constants.getProperty("VG_UNICODE_"+p_requestVO.getLocale
             * ().getLanguage
             * ().toUpperCase())).equalsIgnoreCase(p_requestVO.getReqSelector
             * ()))
             * code=PretupsI.CHNL_SELECTOR_VG_VALUE
             * else
             * if((Constants.getProperty("V_UNICODE_"+p_requestVO.getLocale(
             * ).getLanguage
             * ().toUpperCase())).equalsIgnoreCase(p_requestVO.getReqSelector
             * ()))
             * code=PretupsI.CHNL_SELECTOR_C_VALUE
             * 
             * String requestSelectorValue = new
             * CryptoUtil().bytesToBinHex(p_requestVO
             * .getReqSelector().getBytes())
             * if(LOG.isDebugEnabled())LOG.debug("getSelectorValueFromCode",
             * "Entered for Request ID="+p_requestVO.getRequestIDStr()+
             * ":::p_requestVO.getReqSelector() Hex Value : "
             * +requestSelectorValue
             * +"CVG_UNICODE_"+p_requestVO.getLocale().getLanguage
             * ().toUpperCase())
             * 
             * //if(LOG.isDebugEnabled())LOG.debug("getSelectorValueFromCode",
             * "CVG_UNICODE_"
             * +p_requestVO.getLocale().getLanguage().toUpperCase());
             * if(LOG.isDebugEnabled())LOG.debug("getSelectorValueFromCode",
             * Constants
             * .getProperty("CVG_UNICODE_"+p_requestVO.getLocale().getLanguage
             * ().toUpperCase() ))
             * 
             * if( Constants.getProperty("CVG_UNICODE_"+p_requestVO.getLocale().
             * getLanguage().toUpperCase()
             * ).equalsIgnoreCase(requestSelectorValue))
             * code=PretupsI.CHNL_SELECTOR_CVG_VALUE
             * else
             * if(Constants.getProperty("VG_UNICODE_"+p_requestVO.getLocale(
             * ).getLanguage
             * ().toUpperCase()).equalsIgnoreCase(requestSelectorValue))
             * code=PretupsI.CHNL_SELECTOR_VG_VALUE
             * else
             * if(Constants.getProperty("C_UNICODE_"+p_requestVO.getLocale()
             * .getLanguage
             * ().toUpperCase()).equalsIgnoreCase(requestSelectorValue))
             * code=PretupsI.CHNL_SELECTOR_C_VALUE
             * 
             * }
             * }
             * switch(code)
             * {
             * case PretupsI.CHNL_SELECTOR_CVG_VALUE:
             * p_requestVO.setReqSelector(PretupsI.CHNL_SELECTOR_CVG)
             * break;
             * case PretupsI.CHNL_SELECTOR_VG_VALUE:
             * p_requestVO.setReqSelector(PretupsI.CHNL_SELECTOR_VG)
             * break;
             * case PretupsI.CHNL_SELECTOR_C_VALUE:
             * p_requestVO.setReqSelector(PretupsI.CHNL_SELECTOR_C)
             * break;
             * default :
             * {
             * if(LOG.isDebugEnabled())LOG.debug("getSelectorValueFromCode",
             * "Invalid Selector Value for Request ID="
             * +p_requestVO.getRequestIDStr())
             * throw new
             * BTSLBaseException("PretupsBL","getSelectorValueFromCode"
             * ,PretupsErrorCodesI.ERROR_INVALID_SELECTOR_VALUE)
             * }
             * }
             */
        } catch (BTSLBaseException be) {
            LOG.error(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getSelectorValueFromCode]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting with selector=" + p_requestVO.getReqSelector());
        }
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
        final String methodName = "getLocaleValueFromCode";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered for Request ID=" + p_requestVO.getRequestIDStr() + ":::p_langValue:" + p_langValue);
        }
        int code = -1;
        try {
            if (p_requestVO.isPlainMessage()) {
                try {
                    code = Integer.parseInt(p_langValue);
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "Invalid Language Value for Request ID=" + p_requestVO.getRequestIDStr());
                    }
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
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
                 * code=PretupsI.CHNL_LOCALE_LANG1_VALUE
                 * else
                 * code=PretupsI.CHNL_LOCALE_LANG2_VALUE
                 * }
                 * else
                 * {
                 * if((Constants.getProperty("LANG1_UNICODE_"+p_requestVO.getLocale
                 * (
                 * ).getLanguage().toUpperCase())).equalsIgnoreCase(p_langValue)
                 * )
                 * code=PretupsI.CHNL_LOCALE_LANG1_VALUE
                 * else
                 * code=PretupsI.CHNL_LOCALE_LANG2_VALUE
                 * }
                 */
                if ("Y".equalsIgnoreCase(Constants.getProperty("STK_CONVERSION_REQD"))) {
                    if (PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                        code = Integer.parseInt(Constants.getProperty(p_langValue));
                    } else {
                        final String p_langValue1 = new CryptoUtil().bytesToBinHex(p_langValue.getBytes());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, "Entered for Request ID=" + p_requestVO.getRequestIDStr() + ":::p_langValue. Hex Value : " + p_langValue1);
                        }
                        final String checkLangvalue = Constants.getProperty(p_langValue1);
                        if (BTSLUtil.isNullString(checkLangvalue)) {
                            code = 1;
                        } else {
                            code = Integer.parseInt(checkLangvalue);
                            // code=Integer.parseInt(Constants.getProperty(p_langValue1))
                        }
                    }
                } else {
                    code = Integer.parseInt(Constants.getProperty(p_langValue));
                }
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getLocaleValueFromCode]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting with code=" + code);
        }
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
       MComConnectionI mcomCon = null;
        final String methodName = "updateSubscriberInterfaceRouting";
        try {
            mcomCon = new MComConnection();
            try{con=mcomCon.getConnection();}catch(SQLException e){
            	LOG.error(methodName,  "Exception"+ e.getMessage());
        		LOG.errorTrace(methodName, e);
            }
            updateSubscriberInterfaceRouting(con, p_interfaceID, p_externalID, p_msisdn, p_subscriberType, p_modifiedBy, p_modifiedOn);
        } finally {
        	if(mcomCon != null){mcomCon.close("PretupsBL#updateSubscriberInterfaceRouting");mcomCon=null;}
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
        // Connection con=null
        final String methodName = "updateSubscriberInterfaceRouting";
        try {
            // con=OracleUtil.getConnection()
            final RoutingVO routingVO = new RoutingVO();
            routingVO.setInterfaceID(p_interfaceID);
            routingVO.setExternalInterfaceID(p_externalID);
            routingVO.setModifiedBy(p_modifiedBy);
            routingVO.setModifiedOn(p_modifiedOn);
            routingVO.setMsisdn(p_msisdn);
            routingVO.setStatus(PretupsI.YES);
            routingVO.setSubscriberType(p_subscriberType);
            final int i = new RoutingTxnDAO().updateSubscriberRoutingInfo(p_con, routingVO);
            if (i < 1) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
            }
            p_con.commit();
        } catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            try {
                p_con.rollback();
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
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
       MComConnectionI mcomCon = null;
        final String methodName = "insertSubscriberInterfaceRouting";
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final RoutingVO routingVO = new RoutingVO();
            routingVO.setInterfaceID(p_interfaceID);
            routingVO.setExternalInterfaceID(p_externalID);
            routingVO.setModifiedBy(p_createdBy);
            routingVO.setModifiedOn(p_createdOn);
            routingVO.setMsisdn(p_msisdn);
            routingVO.setSubscriberType(p_subscriberType);
            routingVO.setStatus(PretupsI.YES);
            routingVO.setCreatedBy(p_createdBy);
            routingVO.setCreatedOn(p_createdOn);

            final int i = new RoutingTxnDAO().addSubscriberRoutingInfo(con, routingVO);
            if (i < 1) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
            }
            mcomCon.finalCommit();
        } catch (BTSLBaseException be) {
            try {
                mcomCon.finalRollback();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            try {
                mcomCon.finalRollback();
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
        	if(mcomCon != null){mcomCon.close("PretupsBL#insertSubscriberInterfaceRouting");mcomCon=null;}
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
       MComConnectionI mcomCon = null;
        final String methodName = "deleteSubscriberInterfaceRouting";
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final RoutingVO routingVO = new RoutingVO();
            routingVO.setMsisdn(p_msisdn);
            routingVO.setSubscriberType(p_subscriberType);
            final int i = new RoutingTxnDAO().deleteSubscriberRoutingInfo(con, routingVO);
            if (i < 1) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
            }
            mcomCon.finalCommit();
        } catch (BTSLBaseException be) {
            try {
                mcomCon.finalRollback();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            try {
                mcomCon.finalRollback();
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
        	if(mcomCon != null){mcomCon.close("PretupsBL#deleteSubscriberInterfaceRouting");mcomCon=null;}
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
        final String methodName = "getSelectorDescriptionFromCode";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_code=" + p_code);
        }
        String selectorDescription = null;
        try {
            // changed for CRE_INT_CR00029 by ankit Zindal
            // This is changed to On-Peak and SMS&Data Credit management
            // Changed on 25/05/07 for service Type selector Mapping
            if (!ServiceSelectorMappingCache.getServiceSelectorMap().containsKey(p_code)) {
                selectorDescription = "N.A.";
            } else {
                final ServiceSelectorMappingVO serviceSelectorMappingVO = (ServiceSelectorMappingVO) ServiceSelectorMappingCache.getServiceSelectorMap().get(p_code);
                selectorDescription = serviceSelectorMappingVO.getSelectorName();
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            selectorDescription = "N.A.";
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting with selectorDescription=" + selectorDescription);
        }
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
        final String METHOD_NAME = "getMessage";
        String message = null;
        try {
            message = BTSLUtil.getMessage(p_locale, p_key + "_" + p_serviceType, p_args);
            if (BTSLUtil.isNullString(message)) {
                message = BTSLUtil.getMessage(p_locale, p_key, p_args);
            }
            return message;
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            return BTSLUtil.getMessage(p_locale, p_key, p_args);
        }
    }

    /**
     * Method to check whether Counters needs to be reinitialized or not
     * 
     * @param p_userGroupTypeCounters
     * @param p_frequency
     * 
     * @return boolean
     */
    public static boolean checkResetGroupTypeCounters(GroupTypeCountersVO p_userGroupTypeCounters, String p_frequency, Date p_newDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkResetGroupTypeCounters", "Entered with p_userGroupTypeCounters=" + p_userGroupTypeCounters.toString() + " p_frequency=" + p_frequency);
        }
        boolean isCounterChange = false;
        final Calendar cal = BTSLDateUtil.getInstance();
        cal.setTime(p_newDate);
        final int currentYear = cal.get(Calendar.YEAR);
        final int currentMonth = cal.get(Calendar.MONTH) + 1;
        final int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        // If frequency is daily then if year,month and day are same, no counter
        // is reinitialised.
        if (PretupsI.GRPT_TYPE_FREQUENCY_DAILY.equals(p_frequency)) {
            if (currentYear == p_userGroupTypeCounters.getYear()) {
                if (currentMonth == p_userGroupTypeCounters.getMonth()) {
                    if (currentDay == p_userGroupTypeCounters.getDay()) {
                        isCounterChange = false;
                    } else {
                        isCounterChange = true;
                    }
                } else {
                    isCounterChange = true;
                }
            } else {
                isCounterChange = true;
            }
        }
        // If frequency is monthly then if year and month are same, no counter
        // is reinitialised.
        else if (PretupsI.GRPT_TYPE_FREQUENCY_MONTHLY.equals(p_frequency)) {
            if (currentYear == p_userGroupTypeCounters.getYear()) {
                if (currentMonth == p_userGroupTypeCounters.getMonth()) {
                    isCounterChange = false;
                } else {
                    isCounterChange = true;
                }
            } else {
                isCounterChange = true;
            }
        }
        if (isCounterChange) {
            p_userGroupTypeCounters.setCounters(0);
        }
        p_userGroupTypeCounters.setYear(currentYear);
        p_userGroupTypeCounters.setMonth(currentMonth);
        p_userGroupTypeCounters.setDay(currentDay);
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkResetGroupTypeCounters", "Exiting with isCounterChange=" + isCounterChange);
        }
        return isCounterChange;
    }

    /**
     * METHOD:loadGroupTypeProfileCounters
     * This method is used to load the group type profileVO from cache
     * 
     * @param p_networkID
     * @param p_grpType
     * @param p_reqGatewayType
     * @param p_resGatewayType
     * @param p_type
     * @return
     */
    public static GroupTypeProfileVO loadGroupTypeProfileCounters(String p_networkID, String p_grpType, String p_reqGatewayType, String p_resGatewayType, String p_type) {
        GroupTypeProfileVO groupTypeProfileVO = null;
        groupTypeProfileVO = (GroupTypeProfileVO) GroupTypeProfileCache.getObject(p_networkID, p_grpType, p_reqGatewayType, p_resGatewayType, p_type);
        if (groupTypeProfileVO == null) {
            groupTypeProfileVO = (GroupTypeProfileVO) GroupTypeProfileCache.getObject(p_networkID, p_grpType, PretupsI.ALL, p_resGatewayType, p_type);
        } else {
            return groupTypeProfileVO;
        }
        if (groupTypeProfileVO == null) {
            groupTypeProfileVO = (GroupTypeProfileVO) GroupTypeProfileCache.getObject(p_networkID, p_grpType, p_reqGatewayType, PretupsI.ALL, p_type);
        } else {
            return groupTypeProfileVO;
        }
        if (groupTypeProfileVO == null) {
            groupTypeProfileVO = (GroupTypeProfileVO) GroupTypeProfileCache.getObject(p_networkID, p_grpType, PretupsI.ALL, PretupsI.ALL, p_type);
        }
        return groupTypeProfileVO;
    }

    /**
     * METHOD:loadAndChackC2SGroupTypeCounters
     * This method will load the user counters.
     * If found then check them for reset otherwise create new counters
     * check them agains the profile counters
     * update the running counters
     * 
     * @param p_requestVO
     * @param p_level
     * @return
     */
    public static GroupTypeProfileVO loadAndCheckC2SGroupTypeCounters(RequestVO p_requestVO, String p_level) {
        final String methodName = "loadAndCheckC2SGroupTypeCounters";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered p_requestVO:" + p_requestVO + " p_level:" + p_level);
        }
        GroupTypeProfileVO groupTypeProfileVO = null;
       Connection con = null;
       MComConnectionI mcomCon = null;
       String grptControlLevel = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CONTROL_LEVEL);
        try {
            GroupTypeCountersVO userGroupTypeCounterVO = null;
            final GroupTypeDAO groupTypeDAO = new GroupTypeDAO();
            boolean found = true;
            final ChannelUserVO senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            // If group type level is MSISDN then pass the msisdn to load the
            // counters otherwise ALL will be passed
            if (PretupsI.GRPT_CONTROL_LEVEL_MSISDN.equals(grptControlLevel)) {
                userGroupTypeCounterVO = groupTypeDAO.loadUserGroupTypeCounters(con, senderVO.getUserID(), senderVO.getMsisdn(), p_requestVO.getGroupType(), p_level);
            } else if (PretupsI.GRPT_CONTROL_LEVEL_USERID.equals(grptControlLevel)) {
                userGroupTypeCounterVO = groupTypeDAO.loadUserGroupTypeCounters(con, senderVO.getUserID(), PretupsI.ALL, p_requestVO.getGroupType(), p_level);
            }
            // If user running counters are not found then create the new
            // counters
            if (userGroupTypeCounterVO == null) {
                final Calendar calender = BTSLDateUtil.getInstance();
                calender.setTime(p_requestVO.getCreatedOn());
                userGroupTypeCounterVO = new GroupTypeCountersVO();
                userGroupTypeCounterVO.setUserID(senderVO.getUserID());
                userGroupTypeCounterVO.setMsisdn(senderVO.getMsisdn());
                if (PretupsI.GRPT_CONTROL_LEVEL_USERID.equals(grptControlLevel)) {
                    userGroupTypeCounterVO.setMsisdn(PretupsI.ALL);
                }
                userGroupTypeCounterVO.setGroupType(p_requestVO.getGroupType());
                userGroupTypeCounterVO.setType(p_level);
                userGroupTypeCounterVO.setCounters(0);
                userGroupTypeCounterVO.setYear(calender.get(Calendar.YEAR));
                userGroupTypeCounterVO.setMonth(calender.get(Calendar.MONTH) + 1);
                userGroupTypeCounterVO.setDay(calender.get(Calendar.DAY_OF_MONTH));
                userGroupTypeCounterVO.setModule(p_requestVO.getModule());
                found = false;
            }
            MessageGatewayVO messageGatewayVO = null;
            final MessageGatewayMappingCacheVO messageGatewayMappingCacheVO = MessageGatewayCache.getMappingObject(p_requestVO.getRequestGatewayCode());
            messageGatewayVO = MessageGatewayCache.getObject(messageGatewayMappingCacheVO.getResponseCode());
            if (messageGatewayVO != null) {
                // Load the profile countres from the cache.
                groupTypeProfileVO = PretupsBL.loadGroupTypeProfileCounters(senderVO.getNetworkID(), p_requestVO.getGroupType(), p_requestVO.getRequestGatewayType(),
                    messageGatewayVO.getGatewayType(), p_level);
            }
            // If profile counters are null then raise alarm
            if (groupTypeProfileVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[loadAndCheckC2SGroupTypeCounters]", "",
                    "", "", "Group type profile counts are not defined for group type :" + p_requestVO.getGroupType() + " type:" + p_level);
            } else {
                // If user running counters are found then reset the counters if
                // required
                if (found) {
                    checkResetGroupTypeCounters(userGroupTypeCounterVO, groupTypeProfileVO.getFrequency(), p_requestVO.getCreatedOn());
                }
                // check the current counters with profile counters
                if (userGroupTypeCounterVO.getCounters() >= groupTypeProfileVO.getThresholdValue()) {
                    groupTypeProfileVO.setGroupTypeCounterReach(true);
                } else {
                    groupTypeProfileVO.setGroupTypeCounterReach(false);
                }
                // Set the current counters in senderVO
                if (PretupsI.GRPT_TYPE_CHARGING.equals(p_level)) {
                    senderVO.setUserChargeGrouptypeCounters(userGroupTypeCounterVO);
                } else {
                    senderVO.setUserControlGrouptypeCounters(userGroupTypeCounterVO);
                }
                // Update the user running counters
                // In case of control, when thresold reach limit then no
                // updation will be done(in this case decrease counter is not
                // called)
                if (!(PretupsI.GRPT_TYPE_CONTROLLING.equals(p_level) && groupTypeProfileVO.isGroupTypeCounterReach())) {
                    if (found) {
                        groupTypeDAO.updateUserGroupTypeCounters(con, userGroupTypeCounterVO);
                    } else {
                        groupTypeDAO.insertUserGroupTypeCounters(con, userGroupTypeCounterVO);
                    }
                    // commit the connetion
                    mcomCon.finalCommit();
                } else {
                    mcomCon.finalRollback();
                }
            }
        } catch (Exception e) {
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            LOG.errorTrace(methodName, e);
        } finally {
            // close the connection
        	if(mcomCon != null){mcomCon.close("PretupsBL#loadAndCheckC2SGroupTypeCounters");mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, " Exited groupTypeProfileVO:" + groupTypeProfileVO);
            }
        }
        return groupTypeProfileVO;
    }

    /**
     * METHOD:loadAndCheckP2PGroupTypeCounters
     * This method will load the user counters.
     * If found then check them for reset otherwise create new counters
     * check them agains the profile counters
     * update the running counters
     * 
     * @param p_requestVO
     * @param p_level
     * @return
     */
    public static GroupTypeProfileVO loadAndCheckP2PGroupTypeCounters(RequestVO p_requestVO, String p_level) {
        final String methodName = "loadAndCheckP2PGroupTypeCounters";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered p_requestVO:" + p_requestVO + " p_level:" + p_level);
        }
        GroupTypeProfileVO groupTypeProfileVO = null;
       Connection con = null;
       MComConnectionI mcomCon = null;
       String grptControlLevel = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CONTROL_LEVEL);
        try {
            GroupTypeCountersVO userGroupTypeCounterVO = null;
            final GroupTypeDAO groupTypeDAO = new GroupTypeDAO();
            boolean found = true;
            final SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            // If group type level is MSISDN then pass the msisdn to load the
            // counters otherwise ALL will be passed
            if (PretupsI.GRPT_CONTROL_LEVEL_MSISDN.equals(grptControlLevel)) {
                userGroupTypeCounterVO = groupTypeDAO.loadUserGroupTypeCounters(con, senderVO.getUserID(), senderVO.getMsisdn(), p_requestVO.getGroupType(), p_level);
            } else if (PretupsI.GRPT_CONTROL_LEVEL_USERID.equals(grptControlLevel)) {
                userGroupTypeCounterVO = groupTypeDAO.loadUserGroupTypeCounters(con, senderVO.getUserID(), PretupsI.ALL, p_requestVO.getGroupType(), p_level);
            }
            // If user running counters are not found then create the new
            // counters
            if (userGroupTypeCounterVO == null) {
                final Calendar calender = BTSLDateUtil.getInstance();
                calender.setTime(p_requestVO.getCreatedOn());
                userGroupTypeCounterVO = new GroupTypeCountersVO();
                userGroupTypeCounterVO.setUserID(senderVO.getUserID());
                userGroupTypeCounterVO.setMsisdn(senderVO.getMsisdn());
                if (PretupsI.GRPT_CONTROL_LEVEL_USERID.equals(grptControlLevel)) {
                    userGroupTypeCounterVO.setMsisdn(PretupsI.ALL);
                }
                userGroupTypeCounterVO.setGroupType(p_requestVO.getGroupType());
                userGroupTypeCounterVO.setType(p_level);
                userGroupTypeCounterVO.setCounters(0);
                userGroupTypeCounterVO.setYear(calender.get(Calendar.YEAR));
                userGroupTypeCounterVO.setMonth(calender.get(Calendar.MONTH) + 1);
                userGroupTypeCounterVO.setDay(calender.get(Calendar.DAY_OF_MONTH));
                userGroupTypeCounterVO.setModule(p_requestVO.getModule());
                found = false;
            }
            MessageGatewayVO messageGatewayVO = null;
            final MessageGatewayMappingCacheVO messageGatewayMappingCacheVO = MessageGatewayCache.getMappingObject(p_requestVO.getRequestGatewayCode());
            messageGatewayVO = MessageGatewayCache.getObject(messageGatewayMappingCacheVO.getResponseCode());
            if (messageGatewayVO != null) {
                // Load the profile countres from the cache.
                groupTypeProfileVO = PretupsBL.loadGroupTypeProfileCounters(senderVO.getNetworkCode(), p_requestVO.getGroupType(), p_requestVO.getRequestGatewayType(),
                    messageGatewayVO.getGatewayType(), p_level);
            }
            // If profile counters are null then raise alarm
            if (groupTypeProfileVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[loadAndCheckP2PGroupTypeCounters]", "",
                    "", "", "Group type profile counts are not defined for group type :" + p_requestVO.getGroupType() + " type:" + p_level);
            } else {
                // If user running counters are found then reset the counters if
                // required
                if (found) {
                    checkResetGroupTypeCounters(userGroupTypeCounterVO, groupTypeProfileVO.getFrequency(), p_requestVO.getCreatedOn());
                }
                // check the current counters with profile counters
                if (userGroupTypeCounterVO.getCounters() >= groupTypeProfileVO.getThresholdValue()) {
                    groupTypeProfileVO.setGroupTypeCounterReach(true);
                } else {
                    groupTypeProfileVO.setGroupTypeCounterReach(false);
                }
                // set the current counters in senderVO
                if (PretupsI.GRPT_TYPE_CHARGING.equals(p_level)) {
                    senderVO.setUserChargeGrouptypeCounters(userGroupTypeCounterVO);
                } else {
                    senderVO.setUserControlGrouptypeCounters(userGroupTypeCounterVO);
                }
                // Update the user running counters
                // In case of control, when thresold reach limit then no
                // updation will be done(in this case decrease counter is not
                // called)
                if (!(PretupsI.GRPT_TYPE_CONTROLLING.equals(p_level) && groupTypeProfileVO.isGroupTypeCounterReach())) {
                    if (found) {
                        groupTypeDAO.updateUserGroupTypeCounters(con, userGroupTypeCounterVO);
                    } else {
                        groupTypeDAO.insertUserGroupTypeCounters(con, userGroupTypeCounterVO);
                    }
                    // commit the connection
                    mcomCon.finalCommit();
                } else {
                    mcomCon.finalRollback();
                }

            }
        } catch (Exception e) {
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            LOG.errorTrace(methodName, e);
        } finally {
            // close the connection
        	if(mcomCon != null){mcomCon.close("PretupsBL#loadAndCheckP2PGroupTypeCounters");mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, " Exited groupTypeProfileVO:" + groupTypeProfileVO);
            }
        }
        return groupTypeProfileVO;
    }

    /**
     * METHOD:decreaseGroupTypeCounters
     * This method will decrese the userGroup type counters
     * 
     * @param
     * @param p_groupTypeCountersVO
     * @return
     */
    public static int decreaseGroupTypeCounters(GroupTypeCountersVO p_groupTypeCountersVO) {
        final String methodName = "decreaseGroupTypeCounters";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered p_groupTypeCountersVO:" + p_groupTypeCountersVO);
        }
       Connection con = null;
       MComConnectionI mcomCon = null;
        int updateCount = -1;
        try {
            final GroupTypeDAO groupTypeDAO = new GroupTypeDAO();
            // take the connection
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            // Update the counters
            updateCount = groupTypeDAO.decreaseUserGroupTypeCounters(con, p_groupTypeCountersVO);
            // if update counts are greater then 0 then commit the connection
            // Else rollback the connection
            if (updateCount > 0) {
               mcomCon.finalCommit();
            } else {
                mcomCon.finalRollback();
            }
        } catch (Exception e) {
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            LOG.errorTrace(methodName, e);
        } finally {
            // close the connection
        	if(mcomCon != null){mcomCon.close("PretupsBL#decreaseGroupTypeCounters");mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, " Exited updateCount:" + updateCount);
            }
        }
        return updateCount;
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
        final String methodName = "isValidAmount";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Entered p_requestAmount=" + p_requestAmount + " p_checkLength=" + p_checkLength + " p_sizeAllowed=" + p_sizeAllowed);
        }
        String[] strArr = null;
        double requestAmt = 0;
        try {
            if (BTSLUtil.isNullString(p_requestAmount)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.INVALID_AMOUNT_NULL);
            }
            try {
                requestAmt = Double.parseDouble(p_requestAmount.trim());
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                strArr = new String[] { p_requestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.INVALID_AMOUNT_NOTNUMERIC, 0, strArr, null);
            }
            if (requestAmt <= 0) {
                strArr = new String[] { p_requestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.INVALID_AMOUNT_LESSTHANZERO, 0, strArr, null);
            }
            if (p_checkLength) {
                if (p_requestAmount.trim().length() > p_sizeAllowed) {
                    strArr = new String[] { p_requestAmount, String.valueOf(p_sizeAllowed) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_INVALID_AMOUNT_PREICISION_NOTALLOWED, 0, strArr, null);
                }
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[isValidAmount]", "", "", "",
                "Exception while validating amount" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Exiting ");
        }
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
        final String methodName = "validateReceiverIdentification";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Entered for p_identificationNumber= " + p_identificationNumber);
        }
        String[] strArr = null;
        String identificationNumberValType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.IDENTIFICATION_NUMBER_VAL_TYPE);
        Integer minIdentificationNumberLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_IDENTIFICATION_NUMBER_LENGTH);
        Integer maxIdentificationNumberLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_IDENTIFICATION_NUMBER_LENGTH);
        try {
            // Chack if p_identificationNumber is null then throw error
            if (BTSLUtil.isNullString(p_identificationNumber)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_IDENTITY_BLANK);
            }
            // Remove the country code
            p_identificationNumber = getFilteredMSISDN(p_identificationNumber);
            p_identificationNumber = _operatorUtil.addRemoveDigitsFromMSISDN(p_identificationNumber);

            // Check the length of p_identificationNumber with defined range of
            // system preference
            if ((p_identificationNumber.length() < (int)minIdentificationNumberLength || p_identificationNumber.length() > (int)maxIdentificationNumberLength)) {
                if ((int)minIdentificationNumberLength != (int)maxIdentificationNumberLength) {
                    strArr = new String[] { p_identificationNumber, String.valueOf((int)minIdentificationNumberLength), String
                        .valueOf((int)maxIdentificationNumberLength) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_IDENTITY_NUM_NOTINRANGE, 0, strArr, null);
                } else {
                    strArr = new String[] { p_identificationNumber, String.valueOf((int)minIdentificationNumberLength) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_NOTIF_NUM_LEN_NOTSAME, 0, strArr, null);
                }
            }
            Boolean alphaIdNumAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALPHA_ID_NUM_ALLOWED);
            if (PretupsI.MSISDN_VALIDATION.equals(identificationNumberValType) || !alphaIdNumAllowed) {
                try {
                    final long lng = Long.parseLong(p_identificationNumber);
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    strArr = new String[] { p_identificationNumber };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_ID_NOTNUMERIC, 0, strArr, null);
                }
            }
            p_receiverVO.setMsisdn(p_identificationNumber);
            // Find the network prefix for the account id
            final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(p_identificationNumber, PretupsI.USER_TYPE_RECEIVER);
            // If network prefix is null then throw error
            if (networkPrefixVO == null) {
                strArr = new String[] { p_identificationNumber };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_NOTIFPREFIX_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
            }

            /*
             * 21/04/07 Code Added for MNP
             * Preference to check whether MNP is allowed in system or not.
             * If yes then check whether Number has not been ported out, If yes
             * then throw error, else continue
             */
            Boolean isMNPAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED);
            if (isMNPAllowed) {
                boolean numberAllowed = false;
                if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_identificationNumber, "", PretupsI.PORTED_IN);
                    if (!numberAllowed) {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_identificationNumber }, null);
                    }
                } else {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_identificationNumber, "", PretupsI.PORTED_OUT);
                    if (numberAllowed) {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_identificationNumber }, null);
                    }
                }
            }
            // 21/04/07: MNP Code End

            if (LOG.isDebugEnabled()) {
                LOG.debug(
                    methodName,
                    p_requestID,
                    "p_identificationNumber after filtering= " + p_identificationNumber + " length=" + p_identificationNumber.length() + " Min length in preference=" + (int)minIdentificationNumberLength + " Max length in prefeernce=" + (int)maxIdentificationNumberLength + "network code of receiver= " + networkPrefixVO
                        .getNetworkCode());
            }
            p_receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
            p_receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
            p_receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateReceiverIdentification]",
                p_requestID, "", "", "Exception while validating identificationNumber" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Exiting for p_identificationNumber= " + p_identificationNumber);
        }
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
        final String methodName = "getFilteredIdentificationNumber";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_identificationNumber:" + p_identificationNumber);
        }
        String filteredIdentificationNumber = null;
        Integer minMsisdnLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH_CODE);
        String msisdnPrefixList = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE);
        String identificationNumberValType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.IDENTIFICATION_NUMBER_VAL_TYPE);
        Integer minIdentificationNumberLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_IDENTIFICATION_NUMBER_LENGTH);
        String otherIdPrefixList = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTHERID_PREFIX_LIST);
        
        try {
            // Get which type of validation has to be done i.e. either MSISDN or
            // ACCOUNTID or BOTH type of validation has to be performed
            final String validationType = identificationNumberValType;
            if (PretupsI.MSISDN_VALIDATION.equals(validationType)) {
                filteredIdentificationNumber = removePrefixFromIdentificationNum((int)minMsisdnLength, msisdnPrefixList, p_identificationNumber);
            } else if (PretupsI.OTHER_VALIDATION.equals(validationType)) {
                filteredIdentificationNumber = removePrefixFromIdentificationNum((int)minIdentificationNumberLength, otherIdPrefixList, p_identificationNumber);
            } else if (PretupsI.BOTH_VALIDATION.equals(validationType)) {
                filteredIdentificationNumber = removePrefixFromIdentificationNum((int)minMsisdnLength, msisdnPrefixList, p_identificationNumber);
                filteredIdentificationNumber = removePrefixFromIdentificationNum((int)minIdentificationNumberLength, otherIdPrefixList, filteredIdentificationNumber);
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getFilteredIdentificationNumber]", "",
                p_identificationNumber, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting Filtered identification number=" + filteredIdentificationNumber);
            }
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("removePrefixFromIdentificationNum",
                "Entered p_minLength=" + p_minLength + " p_prefixList=" + p_prefixList + " p_identificationNumber=" + p_identificationNumber);
        }
        final StringTokenizer strTok = new StringTokenizer(p_prefixList, ",");
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
            if (prefixFound) {
                return p_identificationNumber.substring(prefix.length());
            }
            return p_identificationNumber;
        }
        return p_identificationNumber;
    }

    /**
     * Generates the Transfer ID For EVD
     * 
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    public static void generateEVDTransferID(TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "generateEVDTransferID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered ");
        }
        long newTransferID = 0;
        String transferID = null;
        try {
            final ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
            newTransferID = IDGenerator.getNextID(PretupsI.ID_GEN_EVD_TRANSFER_NO, BTSLUtil.getFinancialYearLastDigits(4), receiverVO.getNetworkCode(), p_transferVO
                .getCreatedOn());
            if (newTransferID == 0) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = _operatorUtil.formatEVDTransferID(p_transferVO, newTransferID);
            if (transferID == null) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting with TransferID=" + transferID);
        }
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
        final String methodName = "checkNumberPortability";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered with p_requestID=" + p_requestID + "p_msisdn=" + p_msisdn);
        }
        Boolean isMNPAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED);
        try {
            if (isMNPAllowed) {
                boolean numberAllowed = false;
                if (p_networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_msisdn, "", PretupsI.PORTED_IN);
                    if (!numberAllowed) {
                        throw new BTSLBaseException("PretupsBL", "getNetworkDetails", PretupsErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_msisdn }, null);
                    }
                } else {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_msisdn, "", PretupsI.PORTED_OUT);
                    if (numberAllowed) {
                        throw new BTSLBaseException("PretupsBL", "getNetworkDetails", PretupsErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_msisdn }, null);
                    }
                }
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[checkNumberPortability]", "", p_requestID,
                "", "Exception while checking for MNP for MSISDN=" + p_msisdn + " Getting=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting ");
        }
    }

    /**
     * Generates the Transfer ID For MVD
     * 
     * @param p_transferVO
     * @param p_quantityRequested
     * @throws BTSLBaseException
     * @returns transferIDList
     */
    public static ArrayList generateMVDTransferID(TransferVO p_transferVO, int p_quantityRequested) throws BTSLBaseException {
        final String methodName = "generateMVDTransferID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_quantityRequested:" + p_quantityRequested);
        }
        long newTransferID = 0;
        ArrayList transferIDList = null;
        String tmpId = null;
        try {
            transferIDList = new ArrayList();
            final ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
            newTransferID = IDGenerator.getNextID(PretupsI.ID_GEN_EVD_TRANSFER_NO, BTSLUtil.getFinancialYearLastDigits(4), receiverVO.getNetworkCode(), p_transferVO
                .getCreatedOn(), p_quantityRequested);
            if (newTransferID == 0) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            for (int i = 0; i < p_quantityRequested; i++) {
                tmpId = _operatorUtil.formatEVDTransferID(p_transferVO, newTransferID + i);
                transferIDList.add(tmpId);
            }
            // setting the last transfer id
            p_transferVO.setTransferID((String) transferIDList.get(0));
            return transferIDList;
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID,e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting with TransferIDListSize=" + transferIDList.size());
            }
        }
    }

    /**
     * This method validates the Reciever Limits based on stage passed
     * This method is changes messages prepaid and postpaid wise
     * 
     * @param p_transferVO
     * @param p_stage
     * @param p_moduleCode
     * @param p_quantityRequired
     * @throws BTSLBaseException
     */
    public static void validateRecieverLimits(TransferVO p_transferVO, int p_stage, String p_moduleCode, int p_quantityRequired) throws BTSLBaseException {
        final String methodName = "validateRecieverLimits";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                p_transferVO.getTransferID(),
                "Entered p_stage:" + p_stage + " p_moduleCode=" + p_moduleCode + " p_transferVO.getReceiverSubscriberType(): " + p_transferVO.getReceiverSubscriberType() + " p_quantityRequired:" + p_quantityRequired);
        }
        // boolean isUpdateRequired=false
        String restrictedMSISDN = null;
        String service_class = null;
        Object serviceObjVal = null;
        ReceiverVO receiverVO = null;
        SenderVO senderVO = null;
        // For Number back Service
        String numbckAllowedDays;
        String numbckAmountDeducted;
        String[] numberOfDays;
        String[] amountDeducted;
        Date graceDate = null;
        Date currentDate = null;
        Boolean mrpBlockTimeAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MRP_BLOCK_TIME_ALLOWED);
        String subscriberFailCtincrCodes = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_FAIL_CTINCR_CODES);
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
            if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                restrictedMSISDN = (((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryVO().getRestrictedMsisdns());
            }
            receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
//            senderVO = (SenderVO) p_transferVO.getSenderVO(); // this is the issue here : ClassCaseException. It shall return the ChannelUserVo as it's set in the MVD controller's populateVOFromRequest()
            currentDate = p_transferVO.getCreatedOn();
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
            final String receiverSubscriberType = p_transferVO.getReceiverTransferItemVO() == null ? p_transferVO.getReceiverSubscriberType() : p_transferVO
                .getReceiverTransferItemVO().getSubscriberType();
            if (p_stage == PretupsI.TRANS_STAGE_BEFORE_INVAL) {
                boolean isValidated = false;
                if (PretupsI.STATUS_ACTIVE.equalsIgnoreCase(restrictedMSISDN)) {
                    if (PretupsI.STATUS_ACTIVE.equalsIgnoreCase((((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryVO()).getTransferToListOnly())) {
                        isValidated = RestrictedSubscriberBL.validateRestrictedSubscriberLimits(p_transferVO, p_quantityRequired);
                        if (!isValidated) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                        }
                    }
                }

                service_class = p_transferVO.getReceiverAllServiceClassID();
                if (service_class != null) {
                    if (receiverVO.getLastSuccessOn() != null) {
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE,receiverVO.getNetworkCode(),p_moduleCode,service_class,false)
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE, receiverVO.getNetworkCode(), p_moduleCode, true,
                                p_transferVO.getReceiverAllServiceClassID());
                        } else {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_SUCCESS_REQUEST_BLOCK_SEC_CODE, receiverVO.getNetworkCode(), p_moduleCode,
                                true, p_transferVO.getReceiverAllServiceClassID());
                        }

                        if (serviceObjVal != null) {
                            receiverVO.setLastSuccTransBlockCheckDone(true);
                            if (((currentDate.getTime() - receiverVO.getLastSuccessOn().getTime()) / 1000) <= ((Long) serviceObjVal).longValue()) {
                                if (mrpBlockTimeAllowed) {
                                    Object serviceTypeObjVal = null;

                                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                    final String strArr[] = { String.valueOf(receiverVO.getSid()), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) };

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
                                    final boolean flag = (Boolean) serviceTypeObjVal;
                                    // Block need to implement::if
                                    // LAST_SERVICE_TYPE_CHECK is TRUE and
                                    // requested amount is equal to previous
                                    // amount and requested service type is
                                    // equal to previous service type, then
                                    // recharge is not allowed
                                    if (flag && p_transferVO.getRequestedAmount() == receiverVO.getLastMRP() && p_transferVO.getServiceType().equals(
                                        receiverVO.getLastServiceType())) {
                                        if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil
                                                .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        } else {
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil
                                                .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        }
                                        EventHandler.handle(EventIDI.REQ_BLOCKTIME,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateRecieverLimits]","","","","Reques MRP Block time with service type");
                                        
                                        throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", PretupsErrorCodesI.MRP_BLOCK_TIME_WID_SERVICE_TYPE, 0, strArr, null);
                                    }
                                    // Block need to implement::if requested
                                    // amount is equal to previous amount, here
                                    // No service type check.
                                    else if (p_transferVO.getRequestedAmount() == receiverVO.getLastMRP()) {
                                        if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil
                                                .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        } else {
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil
                                                .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) }));
                                        }

                                        EventHandler.handle(EventIDI.REQ_BLOCKTIME,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateRecieverLimits]","","","","Reques MRP Block time");
                                        
                                        throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", PretupsErrorCodesI.MRP_BLOCK_TIME, 0, strArr, null);
                                    }
                                } else {
                                    // isUpdateRequired=true
                                    // date 27/04/07
                                    if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.MVD_REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil
                                            .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2), String.valueOf(p_quantityRequired) }));
                                        // p_transferVO.setReceiverReturnMsg(new
                                        // BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R,new
                                        // String[]{BTSLUtil.roundToStr((((Long)serviceObjVal).longValue()/(double)60.0),2)}))
                                    } else {
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.MVD_REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil
                                            .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2), String.valueOf(p_quantityRequired) }));
                                    }
                                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                    final String strArr[] = { String.valueOf(receiverVO.getMsisdn()), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2), String
                                        .valueOf(p_quantityRequired) };
                                    EventHandler.handle(EventIDI.REQ_BLOCKTIME,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateRecieverLimits]","","","","Reques  Block time");
                                    
                                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_S, 0, strArr, null);
                                }
                            }
                        }
                    }
                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED,receiverVO.getNetworkCode(),p_moduleCode,service_class,false)
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED, receiverVO.getNetworkCode(), p_moduleCode, true,
                            p_transferVO.getReceiverAllServiceClassID());
                    } else {
                    	
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_DAILY_TOTAL_TXN_AMT_ALLOWED, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                .getReceiverAllServiceClassID());
                        	
                        	
                    }
                    if (serviceObjVal != null) {
                        receiverVO.setTotalTransAmtCheckDone(true);
                        // How to differentiate between this preference for
                        // sender or receiver
                        if (receiverVO.getTotalTransferAmount() + (p_transferVO.getRequestedAmount() * p_quantityRequired) > ((Long) serviceObjVal).longValue()) {
                            // date 27/04/07
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                p_transferVO
                                    .setReceiverReturnMsg(new BTSLMessages(
                                        PretupsErrorCodesI.MVD_AMOUNT_TRANSFERS_DAY_EXCEEDED_R_PRE,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                            .longValue()), String.valueOf(p_quantityRequired) }));
                                // p_transferVO.setReceiverReturnMsg(new
                                // BTSLMessages(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R,new
                                // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),getDisplayAmount(receiverVO.getTotalTransferAmount()),getDisplayAmount(((Long)serviceObjVal).longValue())}))
                            } else {
                                p_transferVO
                                    .setReceiverReturnMsg(new BTSLMessages(
                                        PretupsErrorCodesI.MVD_AMOUNT_TRANSFERS_DAY_EXCEEDED_R_POST,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                            .longValue()), String.valueOf(p_quantityRequired) }));
                            }
                            String strArr[] = null;
                            if (senderVO != null) {
                                strArr = new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(senderVO.getMsisdn()), getDisplayAmount(receiverVO
                                    .getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()), String.valueOf(p_quantityRequired) };
                            } else {
                                strArr = new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getMsisdn()), getDisplayAmount(receiverVO
                                    .getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()), String.valueOf(p_quantityRequired) };
                            }
                            // isUpdateRequired=true
                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_S, 0, strArr, null);
                        }
                    }

                    serviceObjVal = null;
                    // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class,false)
                    if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                        serviceObjVal = getServiceClassObject(service_class, PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT, receiverVO.getNetworkCode(), p_moduleCode, true,
                            p_transferVO.getReceiverAllServiceClassID());
                    } else {
                    	
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_DAILY_SUCCESS_TXN_ALLOWED_COUNT, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                .getReceiverAllServiceClassID());
                        	
                        
                    }
                    if (serviceObjVal != null) {
                        receiverVO.setNoOfSuccTransCheckDone(true);
                        // How to differentiate between this preference for
                        // sender or receiver
                        if (receiverVO.getTotalSuccessCount() + p_quantityRequired > ((Long) serviceObjVal).longValue()) {
                            // date 27/04/07
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.MVD_NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_PRE,
                                    new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getTotalSuccessCount()), String
                                        .valueOf(((Long) serviceObjVal).longValue()), String.valueOf(p_quantityRequired) }));
                                // p_transferVO.setReceiverReturnMsg(new
                                // BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R,new
                                // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),String.valueOf(receiverVO.getTotalSuccessCount()),String.valueOf(((Long)serviceObjVal).longValue())}))
                            } else {
                                p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.MVD_NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_POST,
                                    new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getTotalSuccessCount()), String
                                        .valueOf(((Long) serviceObjVal).longValue()), String.valueOf(p_quantityRequired) }));
                            }
                            final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getMsisdn()), String.valueOf(receiverVO
                                .getTotalSuccessCount()), String.valueOf(((Long) serviceObjVal).longValue()), String.valueOf(p_quantityRequired) };
                            // isUpdateRequired=true
                            receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_S, 0, strArr, null);
                        }
                    }
                }
            } else if (p_stage == PretupsI.TRANS_STAGE_AFTER_INVAL) {

                // check for the Number back service applicable
                if (p_transferVO.getReceiverTransferItemVO().isNumberBackAllowed()) {
                    final String daysDiff = p_transferVO.getServiceType() + PreferenceI.NUMBCK_ALWD_DAYS_DIFF;
                    final String deductedAmount = p_transferVO.getServiceType() + PreferenceI.NUMBCK_AMT_DEDCTED;

                    numbckAllowedDays = (String) PreferenceCache.getControlPreference(daysDiff, p_transferVO.getNetworkCode(), p_transferVO.getReceiverTransferItemVO()
                        .getInterfaceID());
                    numbckAmountDeducted = (String) PreferenceCache.getControlPreference(deductedAmount, p_transferVO.getNetworkCode(), p_transferVO
                        .getReceiverTransferItemVO().getInterfaceID());

                    // Number back allowed days can be SUSPENDED=20&DEACT=15 OR
                    // SUSPENDED=20 OR ONLY 20
                    if (numbckAllowedDays.contains("=")) {
                        if (numbckAllowedDays.contains("&")) {
                            numberOfDays = numbckAllowedDays.split("&");
                            final int size = numberOfDays.length;
                            for (int i = 0; i < size; i++) {
                                if (numberOfDays[i].contains(p_transferVO.getReceiverTransferItemVO().getAccountStatus())) {
                                    final String[] newNumberOfDays = numberOfDays[i].split("=");
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
                            final int size = amountDeducted.length;
                            for (int i = 0; i < size; i++) {
                                if (amountDeducted[i].contains(p_transferVO.getReceiverTransferItemVO().getAccountStatus())) {
                                    final String[] newAmountDeducted = amountDeducted[i].split("=");
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
                        dateStrGrace = (p_transferVO.getReceiverTransferItemVO().getPreviousGraceDate() == null) ? "0" : BTSLUtil.getDateStringFromDate(p_transferVO
                            .getReceiverTransferItemVO().getPreviousGraceDate());
                        graceDate = BTSLUtil.getDateFromDateString(dateStrGrace);
                    } catch (Exception e) {
                        LOG.errorTrace(methodName, e);
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.GRACE_DATE_IS_WRONG);
                    }
                    /*
                     * This code was commented on 01/04/08 to eliminate fetch
                     * conversion rate step.
                     * Now Moldova will support single currency. Previously
                     * conversion rate was required to
                     * support multiple currency for moldova.
                     */

                    if (BTSLUtil.getDifferenceInUtilDates(graceDate, currentDate) > Integer.parseInt(numbckAllowedDays)) {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.RECHARGE_IS_NOT_ALLOW);
                    }
                    if (!(p_transferVO.getRequestedAmount() > Integer.parseInt(numbckAmountDeducted))) {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.RECHARGE_AMOUNT_IS_NOT_SUFFICIENT);
                        // End of single currency request change
                    }
                }
                if (receiverVO.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    service_class = receiverVO.getServiceClassCode();

                    if (!receiverVO.isLastSuccTransBlockCheckDone() && receiverVO.getLastSuccessOn() != null) {
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE,receiverVO.getNetworkCode(),p_moduleCode,service_class)
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE, receiverVO.getNetworkCode(), p_moduleCode, false,
                                p_transferVO.getReceiverAllServiceClassID());
                        } else {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_SUCCESS_REQUEST_BLOCK_SEC_CODE, receiverVO.getNetworkCode(), p_moduleCode,
                                false, p_transferVO.getReceiverAllServiceClassID());
                        }

                        if (serviceObjVal != null) {
                            if (((currentDate.getTime() - receiverVO.getLastSuccessOn().getTime()) / 1000) <= ((Long) serviceObjVal).longValue()) {
                                if (mrpBlockTimeAllowed) {
                                    Object serviceTypeObjVal = null;
                                    if (p_transferVO.getRequestedAmount() == receiverVO.getLastMRP()) {
                                        if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                            // p_transferVO.setReceiverReturnMsg(new
                                            // BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_PRE,new
                                            // String[]{BTSLUtil.roundToStr((((Long)serviceObjVal).longValue()/(double)60.0),2)}))
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.MVD_REC_LAST_SUCCESS_REQ_BLOCK_R_PRE,
                                                new String[] { BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2), String.valueOf(p_quantityRequired) }));
                                        } else {
                                            // p_transferVO.setReceiverReturnMsg(new
                                            // BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R_POST,new
                                            // String[]{BTSLUtil.roundToStr((((Long)serviceObjVal).longValue()/(double)60.0),2)}))
                                            p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.MVD_REC_LAST_SUCCESS_REQ_BLOCK_R_POST,
                                                new String[] { BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2), String.valueOf(p_quantityRequired) }));
                                        }
                                    }
                                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                    final String strArr[] = { String.valueOf(receiverVO.getSid()), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2) };

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
                                    final boolean flag = (Boolean) serviceTypeObjVal;
                                    // Block need to implement::if
                                    // LAST_SERVICE_TYPE_CHECK is TRUE and
                                    // requested amount is equal to previous
                                    // amount and requested service type is
                                    // equal to previous service type, then
                                    // recharge is not allowed
                                    if (flag && p_transferVO.getRequestedAmount() == receiverVO.getLastMRP() && p_transferVO.getServiceType().equals(
                                        receiverVO.getLastServiceType())) {
                                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MRP_BLOCK_TIME_WID_SERVICE_TYPE, 0, strArr, null);
                                    } else if (p_transferVO.getRequestedAmount() == receiverVO.getLastMRP()) {
                                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MRP_BLOCK_TIME, 0, strArr, null);
                                    }
                                }

                                // isUpdateRequired=true
                                // date 27/04/07
                                else {
                                    if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                        // for
                                        // ID=SUBTYPVALRECLMT
                                        // receiverSubscriberType
                                        // that
                                        // is
                                        // set
                                        // above
                                        // is
                                        // used
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.MVD_REC_LAST_SUCCESS_REQ_BLOCK_R_PRE, new String[] { BTSLUtil
                                            .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2), String.valueOf(p_quantityRequired) }));
                                        // p_transferVO.setReceiverReturnMsg(new
                                        // BTSLMessages(PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_R,new
                                        // String[]{BTSLUtil.roundToStr((((Long)serviceObjVal).longValue()/(double)60.0),2)}))
                                    } else {
                                        p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.MVD_REC_LAST_SUCCESS_REQ_BLOCK_R_POST, new String[] { BTSLUtil
                                            .roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2), String.valueOf(p_quantityRequired) }));
                                    }
                                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                    final String strArr[] = { String.valueOf(receiverVO.getSid()), BTSLUtil.roundToStr((((Long) serviceObjVal).longValue() / 60.0), 2), String
                                        .valueOf(p_quantityRequired) };
                                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.REC_LAST_SUCCESS_REQ_BLOCK_S, 0, strArr, null);
                                }
                            }
                        }
                    }

                    if (!receiverVO.isTotalTransAmtCheckDone()) {
                        serviceObjVal = null;
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED,receiverVO.getNetworkCode(),p_moduleCode,service_class)
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.DAILY_TOTAL_TXN_AMT_ALLOWED, receiverVO.getNetworkCode(), p_moduleCode, false,
                                p_transferVO.getReceiverAllServiceClassID());
                        } else {
                        	
                                serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_DAILY_TOTAL_TXN_AMT_ALLOWED, receiverVO.getNetworkCode(), p_moduleCode, true, p_transferVO
                                    .getReceiverAllServiceClassID());
                            	
                        }

                        if (serviceObjVal != null) {
                            // How to differentiate between this preference for
                            // sender or receiver
                            if (receiverVO.getTotalTransferAmount() + (p_transferVO.getRequestedAmount() * p_quantityRequired) > ((Long) serviceObjVal).longValue()) {
                                // date 27/04/07
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                    // for
                                    // ID=SUBTYPVALRECLMT
                                    // receiverSubscriberType
                                    // that
                                    // is
                                    // set
                                    // above
                                    // is
                                    // used
                                    p_transferVO
                                        .setReceiverReturnMsg(new BTSLMessages(
                                            PretupsErrorCodesI.MVD_AMOUNT_TRANSFERS_DAY_EXCEEDED_R_PRE,
                                            new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                                .longValue()), String.valueOf(p_quantityRequired) }));
                                    // p_transferVO.setReceiverReturnMsg(new
                                    // BTSLMessages(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R,new
                                    // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),getDisplayAmount(receiverVO.getTotalTransferAmount()),getDisplayAmount(((Long)serviceObjVal).longValue())}))
                                } else {
                                    p_transferVO
                                        .setReceiverReturnMsg(new BTSLMessages(
                                            PretupsErrorCodesI.MVD_AMOUNT_TRANSFERS_DAY_EXCEEDED_R_POST,
                                            new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), getDisplayAmount(receiverVO.getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal)
                                                .longValue()), String.valueOf(p_quantityRequired) }));
                                }
                                String strArr[] = null;
                                if (senderVO != null) {
                                    strArr = new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(senderVO.getSid()), getDisplayAmount(receiverVO
                                        .getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()), String.valueOf(p_quantityRequired) };
                                } else {
                                    strArr = new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), getDisplayAmount(receiverVO
                                        .getTotalTransferAmount()), getDisplayAmount(((Long) serviceObjVal).longValue()), String.valueOf(p_quantityRequired) };
                                }
                                // isUpdateRequired=true
                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }

                    if (!receiverVO.isNoOfSuccTransCheckDone()) {
                        serviceObjVal = null;
                        // serviceObjVal=PreferenceCache.getServicePreference(PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT,receiverVO.getNetworkCode(),p_moduleCode,service_class)
                        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.DAILY_SUCCESS_TXN_ALLOWED_COUNT, receiverVO.getNetworkCode(), p_moduleCode,
                                false, p_transferVO.getReceiverAllServiceClassID());
                        } else {
                            serviceObjVal = getServiceClassObject(service_class, PreferenceI.P2P_DAILY_SUCCESS_TXN_ALLOWED_COUNT, receiverVO.getNetworkCode(), p_moduleCode,
                                false, p_transferVO.getReceiverAllServiceClassID());
                        }

                        if (serviceObjVal != null) {
                            // How to differentiate between this preference for
                            // sender or receiver
                            if (receiverVO.getTotalSuccessCount() + p_quantityRequired > ((Long) serviceObjVal).longValue()) {
                                // date 27/04/07
                                if (PretupsI.SERIES_TYPE_PREPAID.equals(receiverSubscriberType)) {
                                    // for
                                    // ID=SUBTYPVALRECLMT
                                    // receiverSubscriberType
                                    // that
                                    // is
                                    // set
                                    // above
                                    // is
                                    // used
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.MVD_NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_PRE,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getTotalSuccessCount()), String
                                            .valueOf(((Long) serviceObjVal).longValue()), String.valueOf(p_quantityRequired) }));
                                    // p_transferVO.setReceiverReturnMsg(new
                                    // BTSLMessages(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R,new
                                    // String[]{getDisplayAmount(p_transferVO.getRequestedAmount()),String.valueOf(receiverVO.getTotalSuccessCount()),String.valueOf(((Long)serviceObjVal).longValue())}))
                                } else {
                                    p_transferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.MVD_NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_POST,
                                        new String[] { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getTotalSuccessCount()), String
                                            .valueOf(((Long) serviceObjVal).longValue()), String.valueOf(p_quantityRequired) }));
                                }
                                final String strArr[] = { getDisplayAmount(p_transferVO.getRequestedAmount()), String.valueOf(receiverVO.getSid()), String.valueOf(receiverVO
                                    .getTotalSuccessCount()), String.valueOf(((Long) serviceObjVal).longValue()), String.valueOf(p_quantityRequired) };
                                // isUpdateRequired=true
                                receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_S, 0, strArr, null);
                            }
                        }
                    }
                } else// If Transaction is failed
                {
                    if (!BTSLUtil.isNullString(receiverVO.getInterfaceResponseCode())) {
                        // TO This needs to be done interface Type wise
                        final String errorCodesForFail = BTSLUtil.NullToString(subscriberFailCtincrCodes);
                        if (BTSLUtil.isNullString(errorCodesForFail)) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateRecieverLimits]",
                                p_transferVO.getTransferID(), receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Error codes for consideration in Fail cases not defined");
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
                            receiverVO.setTotalFailCount(receiverVO.getTotalFailCount() + p_quantityRequired);
                        }
                    }
                    receiverVO.setLastTransferStage(String.valueOf(PretupsI.TRANS_STAGE_AFTER_INVAL));
                    receiverVO.setLastFailedOn(currentDate);
                    // isUpdateRequired=true
                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                }

            } else if (p_stage == PretupsI.TRANS_STAGE_AFTER_INTOP) {
                if (receiverVO.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS))// If
                // Transaction
                // is
                // success
                {
                    // Same for Ambiguous Case
                    receiverVO.setTotalConsecutiveFailCount(0);
                    receiverVO.setTotalSuccessCount(receiverVO.getTotalSuccessCount() + p_quantityRequired);
                    receiverVO.setTotalTransferAmount(receiverVO.getTotalTransferAmount() + (p_transferVO.getRequestedAmount() * p_quantityRequired));
                    receiverVO.setLastTransferStage(PretupsI.TRANSACTION_SUCCESS_STATUS);
                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                    receiverVO.setLastSuccessOn(currentDate);
                } else if (receiverVO.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL))// If
                // Transaction
                // is
                // faile
                {
                    if (!BTSLUtil.isNullString(receiverVO.getInterfaceResponseCode())) {
                        final String errorCodesForFail = BTSLUtil.NullToString(subscriberFailCtincrCodes);
                        if (errorCodesForFail == null || BTSLUtil.isNullString(errorCodesForFail)) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateSenderLimits]",
                                p_transferVO.getTransferID(), receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Error codes for consideration in Fail cases not defined");
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
                            receiverVO.setTotalFailCount(receiverVO.getTotalFailCount() + p_quantityRequired);
                        }
                    }
                    receiverVO.setLastTransferStage(String.valueOf(PretupsI.TRANSACTION_FAIL_STATUS));
                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                    receiverVO.setLastFailedOn(currentDate);
                } else {
                    // What to do in Ambiguous Case
                    receiverVO.setTotalConsecutiveFailCount(0);
                    receiverVO.setTotalSuccessCount(receiverVO.getTotalSuccessCount() + p_quantityRequired);
                    receiverVO.setTotalTransferAmount(receiverVO.getTotalTransferAmount() + (p_transferVO.getRequestedAmount() * p_quantityRequired));
                    receiverVO.setLastTransferStage(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                    receiverVO.setLastSuccessOn(currentDate);
                }

            }
        } catch (BTSLBaseException bex) {
            LOG.errorTrace(methodName, bex);
            throw bex;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateRecieverLimits]", p_transferVO
                .getTransferID(), receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_transferVO.getTransferID(), "Exiting ");
        }
    }

    /**
     * Generates the Adjustment ID
     * 
     * @param p_locationCode
     * @param p_currentDate
     * @param p_quantityRequired
     * @return adjustmentIDList
     * @throws BTSLBaseException
     */
    public static ArrayList generateAdjustmentID(String p_locationCode, Date p_currentDate, int p_quantityRequired) throws BTSLBaseException {
        final String methodName = "generateAdjustmentID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered with p_locationCode=" + p_locationCode + " p_currentDate=" + p_currentDate + " p_quantityRequired=" + p_quantityRequired);
        }
        long newTransferID = 0;
        String tmpId = null;
        ArrayList adjustmentIDList = null;
        try {
            adjustmentIDList = new ArrayList();
            newTransferID = IDGenerator.getNextID(PretupsI.ID_GEN_ADJUSTMENT_NO, BTSLUtil.getFinancialYearLastDigits(4), p_locationCode, null, p_quantityRequired);
            if (newTransferID == 0) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_ADJUSTMENTID);
            }
            for (int i = 0; i < p_quantityRequired; i++) {
                tmpId = _operatorUtil.formatAdjustmentTxnID(p_locationCode, p_currentDate, newTransferID + i);
                adjustmentIDList.add(tmpId);
            }
            if (adjustmentIDList.size() < 0) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_ADJUSTMENTID);
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_ADJUSTMENTID,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting with adjustmentIDList=" + adjustmentIDList.size());
        }
        return adjustmentIDList;
    }

    /**
     * Method to check whether Counters needs to be reinitialized or not
     * 
     * @param p_receiverVO
     * @param p_newDate
     * @return boolean
     */
    public static boolean checkResetCountersAfterPeriodChange(ReceiverVO p_receiverVO, java.util.Date p_newDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkResetCountersAfterPeriodChange", "Entered p_receiverVO:" + p_receiverVO);
        }
        boolean isCounterChange = false;
        boolean isDayCounterChange = false;
        boolean isWeekCounterChange = false;
        boolean isMonthCounterChange = false;
        final Date previousDate = p_receiverVO.getLastSuccessOn();
        if (previousDate != null) {
            final Calendar cal = BTSLDateUtil.getInstance();
            cal.setTime(p_newDate);
            final int presentDay = cal.get(Calendar.DAY_OF_MONTH);
            final int presentWeek = cal.get(Calendar.WEEK_OF_MONTH);
            final int presentMonth = cal.get(Calendar.MONTH);
            final int presentYear = cal.get(Calendar.YEAR);
            cal.setTime(previousDate);
            final int lastWeek = cal.get(Calendar.WEEK_OF_MONTH);
            final int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
            final int lastTrxMonth = cal.get(Calendar.MONTH);
            final int lastTrxYear = cal.get(Calendar.YEAR);
            if (presentDay != lastTrxDay) {
                isDayCounterChange = true;
            }
            if (presentWeek != lastWeek) {
                isWeekCounterChange = true;
            }
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
        } else {
            isCounterChange = true;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkResetCountersAfterPeriodChange", "Exiting ");
        }
        return isCounterChange;
    }

    /**
     * unBarredUserAutomaic method used to un barred in the System depend on
     * expiry period.
     * 
     * @param con
     * @param filteredMSISDN
     * @param module
     * @param type
     * @param channelUserVO
     * @return
     * @throws BTSLBaseException
     */
    public static void unBarredUserAutomaic(Connection con, String filteredMSISDN, String networkCode, String module, String type, ChannelUserVO channelUserVO) throws BTSLBaseException {
        final String methodName = "unBarredUserAutomaic";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered filteredMSISDN:" + filteredMSISDN + " networkCode:" + networkCode + " module:" + module + " type:" + type);
        }
        ArrayList barredDetails = null;
        BarredUserVO barredUserVO = null;
        ArrayList otherBarredDetails = null;
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Long vomsPinBlkExpDrn = (Long) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_BLK_EXP_DRN);
        Integer vPinInvalidCount = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.VPIN_INVALID_COUNT);
        
        try {
            barredDetails = new ArrayList();
            otherBarredDetails = new ArrayList();
            long expiryTime = 0;
            barredDetails = _barredUserDAO.loadSingleBarredMsisdnDetails(con, module, networkCode, filteredMSISDN, type, null);
            if (null != barredDetails && barredDetails.size() > 0) {
                if (null != channelUserVO) {
                    expiryTime = ((Long) PreferenceCache.getControlPreference(PreferenceI.C2S_PIN_BLK_EXP_DURATION, channelUserVO.getNetworkID(), channelUserVO
                        .getCategoryCode())).longValue();
                } else {
                    expiryTime = (long)vomsPinBlkExpDrn;
                }
                for (int i = 0, j = barredDetails.size(); i < j; i++) {
                    barredUserVO = (BarredUserVO) barredDetails.get(i);
                    if (PretupsI.BARRED_TYPE_PIN_INVALID.equalsIgnoreCase(barredUserVO.getBarredType())) {
                        final Date createdOn = barredUserVO.getCreatedOn();
                        if (BTSLUtil.isTimeExpired(createdOn, expiryTime)) {
                            _barredUserDAO.deleteSingleBarredMsisdn(con, module, networkCode, filteredMSISDN, type, barredUserVO.getBarredType());
                            Locale locale = new Locale(defaultLanguage, defaultCountry);
                            PushMessage pushMessage = new PushMessage(filteredMSISDN,new BTSLMessages(PretupsErrorCodesI.CHANNEL_USER_UNBARRED), null, null, locale,networkCode);
                            pushMessage.push();
                        } else if (PretupsI.BARRED_TYPE_SYSTEM.equalsIgnoreCase(barredUserVO.getBarredType()) == false) {
                            otherBarredDetails.add(barredUserVO);
                        }
                    } 
                     else if(PretupsI.BARRED_TYPE_VOUCHER_PIN_INVALID_ATTEMPT_EXCEED.equalsIgnoreCase(barredUserVO.getBarredType())){
                    	final Date createdOn = barredUserVO.getCreatedOn();
                        if (BTSLUtil.isTimeExpired(createdOn, (long)vomsPinBlkExpDrn))
                            _barredUserDAO.deleteSingleBarredMsisdn(con, module, networkCode, filteredMSISDN, type, barredUserVO.getBarredType());
                        else
                        	otherBarredDetails.add(barredUserVO);
                        NotificationToOtherSystem nt = new NotificationToOtherSystem("UNBAR",barredUserVO);
                        new Thread(nt).start();
                        }else if (!PretupsI.SERVICE_TYPE_BAR_GIVE_ME_BALANCE.equals(barredUserVO.getBarredType())) {//if (PretupsI.BARRED_TYPE_SYSTEM.equalsIgnoreCase(barredUserVO.getBarredType()) == false &&!PretupsI.SERVICE_TYPE_BAR_GIVE_ME_BALANCE.equals(barredUserVO.getBarredType())) {
                            otherBarredDetails.add(barredUserVO);
                        }
                }
                if (null != otherBarredDetails && otherBarredDetails.size() > 0) {
                    if (type.equals(PretupsI.USER_TYPE_RECEIVER)) {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED, 0, new String[] { filteredMSISDN }, null);
                    } else {
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_USERBARRED_MAX_INVALID_PIN_ATTEMPTS, 0, new String[] { String.valueOf((int)vPinInvalidCount), String.valueOf((long)vomsPinBlkExpDrn) }, null);
                    }
                }
            }
        } catch (BTSLBaseException be) {
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "BTSLBaseException:" + be.getMessage());
            }
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[unBarredUserAutomaic]", "", filteredMSISDN,
                networkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting filteredMSISDN:" + filteredMSISDN + " barred Size:" + barredDetails.size());
            }
        }
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
        final String methodName = "isPromotionalRuleExistInRange";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName,
                "Entered transferRulesVO:" + p_transferRulesVO.toString() + ",transferDate=" + transferDate + ",p_transferRulesVO.getMultipleSlab()=" + p_transferRulesVO
                    .getMultipleSlab() + ",p_transferRulesVO.getSelectRangeType()=" + p_transferRulesVO.getSelectRangeType());
        }
        boolean isExist = false;
        Date fromDate = null;
        Date tillDate = null;

        final String currentTime = "";
        int currDay = -1;
        int allwdDay = -1;
        String[] strArr = null;
        boolean isAllowedDay = false;
        long fromTimeHour = 0, tillTimeHour = 0, currentTimeHour = 0;
        final String format = Constants.getProperty("PROMOTIONAL_TRANSFER_DATE_FORMAT");
        Boolean isServiceProviderPromoAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);
        Boolean isCellGroupRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED);
        
        try {
            currDay = transferDate.getDay();
            if (!(isCellGroupRequired || isServiceProviderPromoAllow)) {
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
                    LOG.debug(methodName, "Allowed days:" + p_transferRulesVO.getAllowedDays() + "isExist:" + isExist);
                    return isExist;
                }
            } else {
                isAllowedDay = true;
            }
            if (isAllowedDay) {
                if (p_transferRulesVO.getMultipleSlab() != null) {
                    final String[] timeSlabArr = p_transferRulesVO.getMultipleSlab().split(",");
                    for (int x = 0; x < timeSlabArr.length; x++) {
                        final String[] slab = timeSlabArr[x].split("-");
                        final String fromTime = slab[0];
                        final String tillTime = slab[1];
                        fromDate = BTSLUtil.getSQLDateFromUtilDate(p_transferRulesVO.getStartTime());
                        tillDate = BTSLUtil.getSQLDateFromUtilDate(p_transferRulesVO.getEndTime());
                        fromDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(fromDate) + " " + fromTime, format);
                        tillDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(tillDate) + " " + tillTime, format);
                        if ("Y".equalsIgnoreCase(p_transferRulesVO.getSelectRangeType())) {
                            if (transferDate.after(fromDate) && transferDate.before(tillDate)) {
                                isExist = true;
                                break;
                            } else {
                                isExist = false;
                            }
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
            LOG.errorTrace(methodName, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[isPromotionalRuleExistInRange]","",filteredMSISDN,networkCode,"Exception:"+e.getMessage())
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting isExist:" + isExist);
            }
        }

        return isExist;

    }

    /**
     * Validates the amount
     * 
     * @param p_c2sTransferVO
     * @param p_requestAmount
     * @throws BTSLBaseException
     */
    public static void validateAmountViaSMS1(C2STransferVO p_c2sTransferVO, String p_requestAmount) throws BTSLBaseException {
        final String methodName = "validateAmountViaSMS1";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_c2sTransferVO.getRequestID(), "Entered p_requestAmount=" + p_requestAmount);
        }
        String[] strArr = null;
        double requestAmt = 0;
        String msgRequestAmount = null;
        try {
            if (BTSLUtil.isNullString(p_requestAmount)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_BLANK);
            }
            msgRequestAmount = p_requestAmount;
            try {
                requestAmt = Double.parseDouble(p_requestAmount);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_NOTNUMERIC, 0, strArr, null);
            }
            if (requestAmt <= 0) {
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_LESSTHANZERO, 0, strArr, null);
            }

            p_c2sTransferVO.setTransferValue(getSystemAmount(p_requestAmount));
            p_c2sTransferVO.setRequestedAmount(getSystemAmount(p_requestAmount));

            Object objVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.P2P_MINTRNSFR_AMOUNT, p_c2sTransferVO.getNetworkCode());
            if (objVal != null) {
                if (p_c2sTransferVO.getRequestedAmount() < ((Long) objVal).longValue()) {
                    strArr = new String[] { msgRequestAmount, PretupsBL.getDisplayAmount(((Long) objVal).longValue()) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_SNDR_MIN_TRANS_AMT_LESS, 0, strArr, null);
                }
            }
            objVal = null;
            objVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.P2P_MAXTRNSFR_AMOUNT, p_c2sTransferVO.getNetworkCode());
            if (objVal != null) {
                if (p_c2sTransferVO.getRequestedAmount() > ((Long) objVal).longValue()) {
                    strArr = new String[] { msgRequestAmount, PretupsBL.getDisplayAmount(((Long) objVal).longValue()) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_SNDR_MAX_TRANS_AMT_MORE, 0, strArr, null);
                }
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateAmountViaSMS1]", "", "", "",
                "Exception while validating amount" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_c2sTransferVO.getRequestID(), "Exiting ");
        }
    }

    /**
     * Validates the amount
     * 
     * @param p_p2pTransferVO
     * @param p_requestAmount
     * @throws BTSLBaseException
     */
    public static void validateAmountViaSMS(P2PTransferVO p_p2pTransferVO, String p_requestAmount) throws BTSLBaseException {
        final String methodName = "validateAmountViaSMS";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_p2pTransferVO.getRequestID(), "Entered p_requestAmount=" + p_requestAmount);
        }
        String[] strArr = null;
        double requestAmt = 0;
        String msgRequestAmount = null;
        try {
            if (BTSLUtil.isNullString(p_requestAmount)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_BLANK);
            }
            msgRequestAmount = p_requestAmount;
            try {
                requestAmt = Double.parseDouble(p_requestAmount);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_NOTNUMERIC, 0, strArr, null);
            }
            if (requestAmt <= 0) {
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_LESSTHANZERO, 0, strArr, null);
            }

            p_p2pTransferVO.setTransferValue(getSystemAmount(p_requestAmount));
            p_p2pTransferVO.setRequestedAmount(getSystemAmount(p_requestAmount));

            Object objVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.P2P_MINTRNSFR_AMOUNT, p_p2pTransferVO.getNetworkCode());
            if (objVal != null) {
                if (p_p2pTransferVO.getRequestedAmount() < ((Long) objVal).longValue()) {
                    strArr = new String[] { msgRequestAmount, PretupsBL.getDisplayAmount(((Long) objVal).longValue()) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_SNDR_MIN_TRANS_AMT_LESS, 0, strArr, null);
                }
            }
            objVal = null;
            objVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.P2P_MAXTRNSFR_AMOUNT, p_p2pTransferVO.getNetworkCode());
            if (objVal != null) {
                if (p_p2pTransferVO.getRequestedAmount() > ((Long) objVal).longValue()) {
                    strArr = new String[] { msgRequestAmount, PretupsBL.getDisplayAmount(((Long) objVal).longValue()) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_SNDR_MAX_TRANS_AMT_MORE, 0, strArr, null);
                }
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateAmountViaSMS]", "", "", "",
                "Exception while validating amount" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_p2pTransferVO.getRequestID(), "Exiting ");
        }
    }

    /**
     * Validates the MSISDN
     * 
     * @param p_receiverVO
     * @param p_msisdn
     * @throws BTSLBaseException
     *             p_requestID
     */
    public static void validateIATMsisdn(ReceiverVO p_receiverVO, C2STransferVO p_c2sTransferVO, String p_msisdn, IATTransferItemVO p_iatTrfItemVO) throws BTSLBaseException {
        final String methodName = "validateIATMsisdn";
        final String p_requestID = p_c2sTransferVO.getRequestID();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Entered for p_msisdn= " + p_msisdn);
        }
        String[] strArr = null;

        try {
            if (BTSLUtil.isNullString(p_msisdn)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }
            // If Receiver msisdn contains '00' or '+', remove these.
            if (p_msisdn.startsWith("00")) {
                p_msisdn = p_msisdn.substring(2);
            } else if (p_msisdn.startsWith("+")) {
                p_msisdn = p_msisdn.substring(1);
            }
            try {
                final long lng = Long.parseLong(p_msisdn);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                strArr = new String[] { p_msisdn };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.IAT_ERROR_RECR_MSISDN_NOTNUMERIC, 0, strArr, null);
            }
            // Get receiver country VO.
            final IATCountryMasterVO masCountryVO = getIATCountryVO(p_msisdn);
            // From country masters VO set country short name, country code
            // country currency in the iat item vo.
            p_iatTrfItemVO.setIatRecCountryShortName(masCountryVO.getRecCountryShortName());
            p_iatTrfItemVO.setIatRcvrCountryName(masCountryVO.getRecCountryName());
            p_iatTrfItemVO.setIatRcvrCurrency(masCountryVO.getCurrency());

            // check if country is enabled ofr IAT transactions or not.If not
            // then throw exception
            if (!PretupsI.YES.equals(masCountryVO.getCountryStatus())) {
                // put params in msg
                // LOG.error("getIATFilteredMSISDN","MSISDN provided belongs to the country where IAT services are not active. Country Name: "+masCountryVO.getRecCountryName())
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[getIATFilteredMSISDN]","",p_msisdn,"","MSISDN provided belongs to the country where IAT services are not active. Country Name: "+masCountryVO.getRecCountryName())
                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p_c2sTransferVO.getLocale())).getMessage())) {
                    p_c2sTransferVO.setSenderReturnMessage(masCountryVO.getLanguage1Message());
                } else {
                    p_c2sTransferVO.setSenderReturnMessage(masCountryVO.getLanguage2Message());
                }
                throw new BTSLBaseException(PretupsErrorCodesI.IAT_CNTRY_NOT_ACTIVE, new String[] { String.valueOf(masCountryVO.getRecCountryCode()), masCountryVO
                    .getRecCountryShortName(), masCountryVO.getRecCountryName(), p_msisdn });
            }
            String msisdnPrefixList = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE);
            // Check if country code received from country vo matches with
            // Prefix/country code (for country) defined in the system
            // preference, then throw exception.
            // sender and receiver country can not be same for IAT transactions.
            final Object[] countryCodeList = msisdnPrefixList.split(",");

            String senderCountryCode = (String) countryCodeList[0];
            if ("0".equals(senderCountryCode)) {
                senderCountryCode = (String) countryCodeList[1];
            }
            if (senderCountryCode.startsWith("00")) {
                senderCountryCode = senderCountryCode.substring(2);
            } else if (senderCountryCode.startsWith("+")) {
                senderCountryCode = senderCountryCode.substring(1);
            }

            p_iatTrfItemVO.setSenderCountryCode(senderCountryCode);
            if (Arrays.asList(countryCodeList).contains(String.valueOf(masCountryVO.getRecCountryCode()))) {
                // LOG.error("getIATFilteredMSISDN","Countries of sender and receiver can not be same for IAT services")
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[getIATFilteredMSISDN]","",p_msisdn,"","Countries of sender and receiver can not be same for IAT services")
                throw new BTSLBaseException(PretupsErrorCodesI.IAT_SEN_REC_SAME_CNTRY);
            }

            // get the msisdn without country code
            // receiver MSISDN will be inserted in iat item table without
            // country code.
            p_msisdn = p_msisdn.substring(String.valueOf(masCountryVO.getRecCountryCode()).length());
            p_iatTrfItemVO.setIatRcvrPrfxLength(masCountryVO.getPrefixLength());
            p_iatTrfItemVO.setIatRcvrCountryCode(masCountryVO.getRecCountryCode());
            if (p_msisdn.length() < masCountryVO.getPrefixLength()) {
                strArr = new String[] { p_msisdn, String.valueOf(masCountryVO.getMinMsisdnLength()), String.valueOf(masCountryVO.getMaxMsisdnLength()) };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.IAT_ERROR_RECR_MSISDN_NOTINRANGE, 0, strArr, null);
            }

            p_iatTrfItemVO.setIatRecMsisdn(p_msisdn);
            // ---p_iatTrfItemVO.setIatRcvrPrfx(p_msisdn.substring(0,masCountryVO.getPrefixLength()))
            // validate length of MSISDN is valid or not. (with out country
            // code)
            if ((p_msisdn.length() < masCountryVO.getMinMsisdnLength() || p_msisdn.length() > masCountryVO.getMaxMsisdnLength())) {
                strArr = new String[] { p_msisdn, String.valueOf(masCountryVO.getMinMsisdnLength()), String.valueOf(masCountryVO.getMaxMsisdnLength()) };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.IAT_ERROR_RECR_MSISDN_NOTINRANGE, 0, strArr, null);
            }

            // Get network_country Cache object. Iterate through the cache,
            // check whether any vo contains the prefix in it's prefix list.
            // If contains then get network code from the VO else throw error.
            final ArrayList networkCntryCache = IATNWServiceCache.getNetworkCountryArrObject();
            if (networkCntryCache != null) {
                final int cacheLen = networkCntryCache.size();
                if (cacheLen > 0) {
                    for (int i = 0; i < cacheLen; i++) {
                        final IATNetworkCountryMappingVO networkCntryVO = (IATNetworkCountryMappingVO) networkCntryCache.get(i);
                        if (networkCntryVO != null) {
                            final String[] prefixList = networkCntryVO.getRecNetworkPrefix().split(",");
                            for (int m = 0, n = prefixList.length; m < n; m++) {
                                final String nwPrfx = prefixList[m];
                                if (p_msisdn.startsWith(nwPrfx) && networkCntryVO.getRecCountryShortName().equals(masCountryVO.getRecCountryShortName())) {
                                    p_iatTrfItemVO.setIatRcvrPrfx(p_msisdn.substring(0, nwPrfx.length()));
                                    p_iatTrfItemVO.setIatRecNWCode(networkCntryVO.getRecNetworkCode());
                                    if (!PretupsI.YES.equals(networkCntryVO.getStatus())) {
                                        final String[] strMsg = new String[] { p_iatTrfItemVO.getIatRcvrCountryName(), networkCntryVO.getRecNetworkCode(), networkCntryVO
                                            .getRecNetworkName(), p_iatTrfItemVO.getIatRcvrPrfx(), p_iatTrfItemVO.getIatRecMsisdn() };
                                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.IAT_NW_SUSPENDED, 0, strMsg, null);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    if (p_iatTrfItemVO.getIatRecNWCode() == null) {
                        final String[] strMsg = new String[] { "+" + p_iatTrfItemVO.getIatRcvrCountryCode() + p_iatTrfItemVO.getIatRecMsisdn(), p_iatTrfItemVO
                            .getIatRcvrPrfx(), p_iatTrfItemVO.getIatRecCountryShortName(), p_iatTrfItemVO.getIatRcvrCountryName() };
                        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.IAT_NW_PRFX_NOT_FOUND, 0, strMsg, null);
                    }
                } else {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateIATMsisdn]", "", p_msisdn,
                        "", "Network Country mapping for IAT are not defined in system");
                    throw new BTSLBaseException(PretupsErrorCodesI.IAT_NW_CNTRY_MAPPING_NOT_FOUND, new String[] { p_iatTrfItemVO.getIatRcvrCountryCode() + p_iatTrfItemVO
                        .getIatRecMsisdn(), p_iatTrfItemVO.getIatRcvrPrfx(), p_iatTrfItemVO.getIatRecCountryShortName(), p_iatTrfItemVO.getIatRcvrCountryName() });
                }
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateIATMsisdn]", "", p_msisdn, "",
                    "Network Country mapping for IAT are not defined in system");
                throw new BTSLBaseException(PretupsErrorCodesI.IAT_NW_CNTRY_MAPPING_NOT_FOUND, new String[] { p_iatTrfItemVO.getIatRcvrCountryCode() + p_iatTrfItemVO
                    .getIatRecMsisdn(), p_iatTrfItemVO.getIatRcvrPrfx(), p_iatTrfItemVO.getIatRecCountryShortName(), p_iatTrfItemVO.getIatRcvrCountryName() });
            }
            // receiver MSISDN will be inserted in main transfer table without
            // country code.
            p_receiverVO.setMsisdn(p_msisdn);
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateIATMsisdn]", "", "", "",
                "Exception while validating msisdn" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.IAT_C2S_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Exiting for p_msisdn= " + p_msisdn);
        }
    }

    private static IATCountryMasterVO getIATCountryVO(String p_msisdn) throws BTSLBaseException {
        final String methodName = "getIATCountryVO";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_msisdn:" + p_msisdn);
        }
        IATCountryMasterVO masCountryVO = null;
        // Get Country master cache object. Iterate through all VOs in the cache
        // object.
        // If country code of any vo matches with the starting digits of the
        // MSISDN. Pick the VO and return. If not found such VO, THROW
        // EXCEPTION.
        try {
            final ArrayList iatCountryCache = IATCountryMasterCache.getIATCountryMasterObject();
            if (iatCountryCache != null) {
                final int cacheLen = iatCountryCache.size();
                if (cacheLen > 0) {
                    for (int i = 0; i < cacheLen; i++) {
                        final IATCountryMasterVO countryVO = (IATCountryMasterVO) iatCountryCache.get(i);
                        if (countryVO != null) {
                            if (p_msisdn.startsWith(String.valueOf(countryVO.getRecCountryCode()))) {
                                masCountryVO = countryVO;
                                break;
                            }
                        }
                    }
                    if (masCountryVO == null) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getCountryCode]", "", p_msisdn,
                            "", "MSISDN country code does not match with the list of country code maintained in system");
                        throw new BTSLBaseException(PretupsErrorCodesI.IAT_CNTRY_CODE_NOT_FOUND, new String[] { p_msisdn });
                    }
                } else {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getCountryCode]", "", p_msisdn, "",
                        "IAT country codes are not defined in system");
                    throw new BTSLBaseException(PretupsErrorCodesI.IAT_CNTRY_CODE_NOT_FOUND, new String[] { p_msisdn });
                }
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getCountryCode]", "", p_msisdn, "",
                    "IAT country codes are not defined in system");
                throw new BTSLBaseException(PretupsErrorCodesI.IAT_CNTRY_CODE_NOT_FOUND, new String[] { p_msisdn });
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "masCountryVO=" + masCountryVO.toString());
            }
            return masCountryVO;

        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getCountryCode]", "", p_msisdn, "",
                "Exception while getting IAT COUNTRY CODE. Exception: " + e.getMessage());
            throw new BTSLBaseException(PretupsErrorCodesI.IAT_C2S_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
        }
    }

    /**
     * Get Handler Object
     * 
     * @param handlerClassName
     * @return
     */
    public static java.lang.Object getIATHandlerObj(String handlerClassName) {
        final String methodName = "getHandlerObj";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered handlerClassName:" + handlerClassName);
        }
        IATInterfaceHandlerI handlerObj = null;
        try {
            handlerObj = (IATInterfaceHandlerI) Class.forName(handlerClassName).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }
        return handlerObj;
    }// end of getHandlerObj

    /**
     * Validates the IAT amount
     * 
     * @param p_c2sTransferVO
     * @param p_requestAmount
     * @throws BTSLBaseException
     */
    public static void validateIATAmount(C2STransferVO p_c2sTransferVO, String p_requestAmount) throws BTSLBaseException {
        final String methodName = "validateIATAmount";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_c2sTransferVO.getRequestID(), "Entered p_requestAmount=" + p_requestAmount);
        }
        String[] strArr = null;
        double requestAmt = 0;
        String msgRequestAmount = null;
        try {
            if (BTSLUtil.isNullString(p_requestAmount)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_BLANK);
            }
            if (p_c2sTransferVO.getRequestGatewayType().equals(Constants.getProperty("GATEWAYTYPES_MIN_DENOMINATION_ALLOWED"))) {
                msgRequestAmount = getDisplayAmount(Long.parseLong(p_requestAmount));
            } else {
                msgRequestAmount = p_requestAmount;
            }
            try {
                requestAmt = Double.parseDouble(p_requestAmount);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_NOTNUMERIC, 0, strArr, null);
            }
            if (requestAmt <= 0) {
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_LESSTHANZERO, 0, strArr, null);
            }
            if (p_c2sTransferVO.getRequestGatewayType().equals(Constants.getProperty("GATEWAYTYPES_MIN_DENOMINATION_ALLOWED"))) {
                p_c2sTransferVO.setTransferValue(Long.parseLong(p_requestAmount));
                p_c2sTransferVO.setRequestedAmount(Long.parseLong(p_requestAmount));
            } else {
                p_c2sTransferVO.setTransferValue(getSystemAmount(p_requestAmount));
                p_c2sTransferVO.setRequestedAmount(getSystemAmount(p_requestAmount));
            }
            p_c2sTransferVO.getIatTransferItemVO().setQuantity(p_c2sTransferVO.getRequestedAmount());

            Object objVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.C2S_MINTRNSFR_AMOUNT, p_c2sTransferVO.getNetworkCode());
            if (objVal != null) {
                if (p_c2sTransferVO.getRequestedAmount() < ((Long) objVal).longValue()) {
                    strArr = new String[] { msgRequestAmount, PretupsBL.getDisplayAmount(((Long) objVal).longValue()) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_LESSTHANALLOWED, 0, strArr, null);
                }
            }
            objVal = null;
            objVal = PreferenceCache.getNetworkPrefrencesValue(PreferenceI.C2S_MAXTRNSFR_AMOUNT, p_c2sTransferVO.getNetworkCode());
            if (objVal != null) {
                if (p_c2sTransferVO.getRequestedAmount() > ((Long) objVal).longValue()) {
                    strArr = new String[] { msgRequestAmount, PretupsBL.getDisplayAmount(((Long) objVal).longValue()) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_MORETHANALLOWED, 0, strArr, null);
                }
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateAmount]", "", "", "",
                "Exception while validating amount" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateAmount", PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("validateAmount", p_c2sTransferVO.getRequestID(), "Exiting ");
        }
    }

    public static void updateP2PSubscriberDetail(SenderVO p_SenderVO) throws BTSLBaseException {
        final String methodName = "updateP2PSubscriberDetail";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered TransferItem VO =" + p_SenderVO.toString());
        }
       Connection con = null;
       MComConnectionI mcomCon = null;
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final SubscriberDAO subscriberDAO = new SubscriberDAO();
            final int i = subscriberDAO.updateSubscriberDetailsByMSISDN(con, p_SenderVO);
            if (i > 0) {
               mcomCon.finalCommit();
            } else {
                mcomCon.finalRollback();
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            throw be;
        } catch (Exception e) {
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception e1) {
                LOG.errorTrace(methodName, e1);
            }
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[updateP2PSubscriberDetail]", "", "", "",
                "Exception while updating p2p Subscriber" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        } finally {
        	if(mcomCon != null){mcomCon.close("PretupsBL#updateP2PSubscriberDetail");mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, p_SenderVO.toString(), "Exiting ");
            }
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
       MComConnectionI mcomCon = null;
        final String methodName = "updateSubscriberInterfaceAilternateRouting";
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final RoutingVO routingVO = new RoutingVO();
            routingVO.setInterfaceID(p_interfaceID);
            routingVO.setExternalInterfaceID(p_externalID);
            routingVO.setModifiedBy(p_modifiedBy);
            routingVO.setModifiedOn(p_modifiedOn);
            routingVO.setMsisdn(p_msisdn);
            routingVO.setStatus(PretupsI.YES);
            routingVO.setSubscriberType(p_subscriberType);
            final int i = new RoutingTxnDAO().updateSubscriberAilternateRoutingInfo(con, routingVO);
            if (i < 1) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
            }
            mcomCon.finalCommit();
        } catch (BTSLBaseException be) {
            try {
                mcomCon.finalRollback();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            try {
                mcomCon.finalRollback();
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
        	if(mcomCon != null){mcomCon.close("PretupsBL#updateSubscriberInterfaceAilternateRouting");mcomCon=null;}
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
        final String methodName = "insertSubscriberInterfaceRouting";
        try {
            // p_con=OracleUtil.getConnection();
            final RoutingVO routingVO = new RoutingVO();
            routingVO.setInterfaceID(p_interfaceID);
            routingVO.setExternalInterfaceID(p_externalID);
            routingVO.setModifiedBy(p_createdBy);
            routingVO.setModifiedOn(p_createdOn);
            routingVO.setMsisdn(p_msisdn);
            routingVO.setSubscriberType(p_subscriberType);
            routingVO.setStatus(PretupsI.YES);
            routingVO.setCreatedBy(p_createdBy);
            routingVO.setCreatedOn(p_createdOn);

            final int i = new RoutingTxnDAO().addSubscriberRoutingInfo(p_con, routingVO);
            if (i < 1) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
                // p_con.commit()
            }
        } catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            try {
                p_con.rollback();
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
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
        final String methodName = "getServiceKeywordHandlerObjSc";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered handlerClassName=" + handlerClassName);
        }
        ServiceKeywordUtil handlerObj = null;
        try {
            handlerObj = (ServiceKeywordUtil) Class.forName(handlerClassName).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[getServiceKeywordHandlerObjSc]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting");
        }
        return handlerObj;
        // return _smsHandlerObj
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
        final String methodName = "deleteSubscriberInterfaceRouting";
        try {
            final RoutingVO routingVO = new RoutingVO();
            routingVO.setMsisdn(p_msisdn);
            routingVO.setSubscriberType(p_subscriberType);
            final int i = new RoutingTxnDAO().deleteSubscriberRoutingInfo(p_con, routingVO);
            if (i < 1) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
            }
            p_con.commit();
        } catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            try {
                p_con.rollback();
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }

    /**
     * METHOD:decreaseGroupTypeCounters
     * This method will decrese the userGroup type counters
     * 
     * @param
     * @param p_groupTypeCountersVO
     * @return
     */
    public static int decreaseGroupTypeCounters(Connection p_con, GroupTypeCountersVO p_groupTypeCountersVO) {
        final String methodName = "decreaseGroupTypeCounters";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered p_groupTypeCountersVO:" + p_groupTypeCountersVO);
        }
        int updateCount = -1;
        try {
            final GroupTypeDAO groupTypeDAO = new GroupTypeDAO();
            // Update the counters
            updateCount = groupTypeDAO.decreaseUserGroupTypeCounters(p_con, p_groupTypeCountersVO);
            // if update counts are greater then 0 then commit the connection
            // Else rollback the connection
            if (updateCount > 0) {
                p_con.commit();
            } else {
                p_con.rollback();
            }
        } catch (Exception e) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            LOG.errorTrace(methodName, e);
        } finally {
            // close the connection
            try {
                if (p_con != null) {
                    p_con.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, " Exited updateCount:" + updateCount);
            }
        }
        return updateCount;
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
        final String methodName = "updateSubscriberInterfaceAilternateRouting";
        try {
            final RoutingVO routingVO = new RoutingVO();
            routingVO.setInterfaceID(p_interfaceID);
            routingVO.setExternalInterfaceID(p_externalID);
            routingVO.setModifiedBy(p_modifiedBy);
            routingVO.setModifiedOn(p_modifiedOn);
            routingVO.setMsisdn(p_msisdn);
            routingVO.setStatus(PretupsI.YES);
            routingVO.setSubscriberType(p_subscriberType);
            final int i = new RoutingTxnDAO().updateSubscriberAilternateRoutingInfo(p_con, routingVO);
            if (i < 1) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
            }
            p_con.commit();
        } catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            try {
                p_con.rollback();
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }

    public static void updateP2PSubscriberDetail(Connection con, SenderVO p_SenderVO) throws BTSLBaseException {
        final String methodName = "updateP2PSubscriberDetail";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered TransferItem VO =" + p_SenderVO.toString());
        }
        try {
            final SubscriberDAO subscriberDAO = new SubscriberDAO();
            final int i = subscriberDAO.updateSubscriberDetailsByMSISDN(con, p_SenderVO);
            if (i > 0) {
                con.commit();
            } else {
                con.rollback();
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            throw be;
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e1) {
                LOG.errorTrace(methodName, e1);
            }
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[updateP2PSubscriberDetail]", "", "", "",
                "Exception while updating p2p Subscriber" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, p_SenderVO.toString(), "Exiting ");
            }
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
        final String methodName = "validatePromotionPrefixes";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered p_transfer_date:" + p_transfer_date + "p_recPrfxID:" + p_recPrfxID + " p_allowedDays:" + p_allowedDays + " p_allowedSeries:" + p_allowedSeries + " p_deniedSeries:" + p_deniedSeries);
        }
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
            if (!isValidPrefix) {
                return isValidPrefix;// if allowed_days doesnt match with
                // current day then return false
                // as promotional rule is not applicable
            } else {
                isValidPrefix = false;// reset the value to check again
            }
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
            LOG.errorTrace(methodName, e);
            // ------------do we need to throw exception here.
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting isValidPrefix:" + isValidPrefix);
            }
        }
        return isValidPrefix;
    }

    public static String getDisplayAmount(double p_amount) {
        final String methodName = "getDisplayAmount";
        // if(LOG.isDebugEnabled())LOG.debug("getDisplayAmount","Entered p_amount:"+p_amount);
        Integer amountMultFactor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
        final int multiplicationFactor = (int)amountMultFactor;
        final double amount = p_amount / multiplicationFactor;
        String amountStr = new DecimalFormat("#############.#####").format(amount);
        try {
            final double d = Double.parseDouble(amountStr);
            amountStr = String.valueOf(d);
        } catch (Exception e) {
            amountStr = new DecimalFormat("############0.00#").format(amount);
          /*  LOG.errorTrace(methodName, e);*/
        }
        // if(LOG.isDebugEnabled())LOG.debug("getDisplayAmount","Exiting display amount:"+amountStr);
        return amountStr;
    }

    /**
     * addSOSRechargeDetails method used to add SOS transaction details in
     * database.
     * 
     * @param con
     *            Connection
     * @param p_transferVO
     *            TransferVO
     * @return
     * @throws BTSLBaseException
     */
    public static void addSOSRechargeDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "addSOSRechargeDetails";
        try {

            final SOSDAO sosDAO = new SOSDAO();
            final int updateCount = sosDAO.addSOSRechargeDetails(p_con, p_transferVO);
            if (updateCount <= 0) {
                throw new BTSLBaseException("PretupsBL", "addSOSRechargeDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[addSOSRechargeDetails]", p_transferVO
                .getTransferID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "addSOSRechargeDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
    }

    /**
     * updateSOSRechargeDetails method used to update the final SOS transaction
     * details in database.
     * 
     * @param con
     *            Connection
     * @param p_transferVO
     *            TransferVO
     * @return
     * @throws BTSLBaseException
     */
    public static void updateSOSRechargeDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "updateSOSRechargeDetails";
        try {
            final SOSDAO sosDAO = new SOSDAO();
            final int updateCount = sosDAO.updateSOSRechargeDetails(p_con, p_transferVO);
            if (updateCount <= 0) {
                throw new BTSLBaseException("PretupsBL", "updateSOSRechargeDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[updateSOSRechargeDetails]", p_transferVO
                .getTransferID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "updateSOSRechargeDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
    }

    public static void validateMCDListMsisdn(Connection p_con, MCDListVO p_mcdListVO, String p_msisdn) throws BTSLBaseException {

        final String methodName = "validateMCDListMsisdn";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered for p_msisdn= " + p_msisdn);
        }
        String[] strArr = null;
        try {
            if (BTSLUtil.isNullString(p_msisdn)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_MCD_LIST_MSISDN_BLANK);
            }
            Integer minMsisdnLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH_CODE);
            Integer maxMsisdnLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE);
            Boolean isMNPAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED);
            
            p_msisdn = getFilteredMSISDN(p_msisdn);
            p_msisdn = _operatorUtil.addRemoveDigitsFromMSISDN(p_msisdn);
            if ((p_msisdn.length() < (int)minMsisdnLength || p_msisdn.length() > (int)maxMsisdnLength)) {
                if ((int)minMsisdnLength != (int)maxMsisdnLength) {
                    strArr = new String[] { p_msisdn, String.valueOf((int)minMsisdnLength), String.valueOf((int)maxMsisdnLength) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_MCD_LIST_MSISDN_NOTINRANGE, 0, strArr, null);
                } else {
                    strArr = new String[] { p_msisdn, String.valueOf((int)minMsisdnLength) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_MCD_LIST_MSISDN_LEN_NOTSAME, 0, strArr, null);
                }
            }
            try {
                final long lng = Long.parseLong(p_msisdn);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                strArr = new String[] { p_msisdn };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_MCD_LIST_MSISDN_NOTNUMERIC, 0, strArr, null);
            }

            if (LOG.isDebugEnabled() && p_mcdListVO.getMsisdn() != null) {
                LOG.debug("", "*********************" + p_mcdListVO.getMsisdn());
            }

            try {

                final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(p_msisdn, PretupsI.USER_TYPE_RECEIVER);
                if (networkPrefixVO == null) {
                    strArr = new String[] { p_msisdn };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_MCD_LIST_RECR_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
                }

                if (isMNPAllowed) {
                    boolean numberAllowed = false;
                    if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                        numberAllowed = _numberPortDAO.isExists(p_con, p_msisdn, "", PretupsI.PORTED_IN);
                        if (!numberAllowed) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_MCD_LIST_NETWORK_NOTFOUND, 0, new String[] { p_msisdn }, null);
                        }
                    } else {
                        numberAllowed = _numberPortDAO.isExists(p_con, p_msisdn, "", PretupsI.PORTED_OUT);
                        if (numberAllowed) {
                            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_MCD_LIST_NETWORK_NOTFOUND, 0, new String[] { p_msisdn }, null);
                        }
                    }
                }
                // 21/04/07: MNP Code End
                p_mcdListVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                p_mcdListVO.setPrefixID(networkPrefixVO.getPrefixID());
                p_mcdListVO.setSubscriberType(networkPrefixVO.getSeriesType());
            }

            catch (BTSLBaseException be) {
                LOG.errorTrace(methodName, be);
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_MCD_LIST_RECR_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_MCD_LIST_RECR_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
            }

        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateMCDListMsisdn]", "", "", "",
                "Exception while validating msisdn" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting for p_msisdn= " + p_msisdn);
        }

    }

    public static long validateMCDListAmount(String p_requestAmount) throws BTSLBaseException {

        final String methodName = "validateMCDListAmount";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_requestAmount=" + p_requestAmount);
        }
        String[] strArr = null;
        long requestAmt = 0;
        final String msgRequestAmount = null;
        Integer p2pMcdlMaxAddAmount = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_MAXADD_AMOUNT);
        
        try {
            if (BTSLUtil.isNullString(p_requestAmount)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_MCDL_AMT_BLANK);
            }

            try {
                requestAmt = Long.parseLong(p_requestAmount);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_MCDL_AMT_NOTNUMERIC, 0, strArr, null);
            }
            if (requestAmt < 0) {
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_MCDL_AMT_LESSTHANZERO, 0, strArr, null);
            }
            // if (requestAmt >SystemPreferences.P2P_MAXTRNSFR_AMOUNT)
            if (requestAmt > (int)p2pMcdlMaxAddAmount) {
                strArr = new String[] { msgRequestAmount };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_MCDL_AMT_MAX_LIMIT, 0, strArr, null);
            }

        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateMCDListAmount]", "", "", "",
                "Exception while validating amount" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting ");
        }

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
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "getPromotionalTransferRule",
                "p_servicetype=" + p_servicetype + " p_module=" + p_module + " p_receivernetworkcode=" + p_receivernetworkcode + " p_sendersubscribertype=" + p_sendersubscribertype + " p_receiversubscribertype=" + p_receiversubscribertype + " p_senderserviceclasscode=" + p_senderserviceclasscode + " p_receiverserviceclasscode=" + p_receiverserviceclasscode + " p_receiverallserviceclasscode=" + p_receiverallserviceclasscode + " p_subservice=" + p_subservice + " p_promotionallevel=" + p_promotionallevel + " p_spgroup=" + p_spgroup + " p_accountSsatus=" + p_accountSsatus + " p_info" + p_info,
                "Entered");
        }

        TransferRulesVO transferRulesVO = null;

        // 1. Check the for service Class + ALL SP Name + Subscriber status
        transferRulesVO = (TransferRulesVO) p_transferrulemap
            .get(p_servicetype + "_" + p_module + "_" + p_receivernetworkcode + "_" + p_sendersubscribertype + "_" + p_receiversubscribertype + "_" + p_senderserviceclasscode + "_" + p_receiverserviceclasscode + "_" + p_subservice + "_" + p_promotionallevel + "_" + PretupsI.ALL + "_" + p_accountSsatus);
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "getPromotionalTransferRule",
                p_transferid,
                "Validation For Network Code=" + p_networkcode + " Sender Subscriber Type=" + p_sendersubscribertype + " Sender Service class=" + p_senderserviceclasscode + "Receiver Subscriber Type=" + p_receiverserviceclasscode + " Receiver Service Class ID=" + p_receiverallserviceclasscode + " Sub Service=" + p_subservice + p_info + " Service Class + ALL SP Name + Subscriber Status");
        }

        // 2. Check the for service Class + SP Name + ALL Subscriber status
        if (transferRulesVO == null) {
            transferRulesVO = (TransferRulesVO) p_transferrulemap
                .get(p_servicetype + "_" + p_module + "_" + p_receivernetworkcode + "_" + p_sendersubscribertype + "_" + p_receiversubscribertype + "_" + p_senderserviceclasscode + "_" + p_receiverserviceclasscode + "_" + p_subservice + "_" + p_promotionallevel + "_" + p_spgroup + "_" + PretupsI.ALL);
            if (LOG.isDebugEnabled()) {
                LOG.debug(
                    "getPromotionalTransferRule",
                    p_transferid,
                    "Validation For Network Code=" + p_networkcode + " Sender Subscriber Type=" + p_sendersubscribertype + " Sender Service class=" + p_senderserviceclasscode + "Receiver Subscriber Type=" + p_receiverserviceclasscode + " Receiver Service Class ID=" + p_receiverallserviceclasscode + " Sub Service=" + p_subservice + p_info + " service Class +  SP Name + ALL Subscriber status");
            }

        }

        // 3. Check the for service Class + ALL SP Name + ALL Subscriber status
        if (transferRulesVO == null) {
            transferRulesVO = (TransferRulesVO) p_transferrulemap
                .get(p_servicetype + "_" + p_module + "_" + p_receivernetworkcode + "_" + p_sendersubscribertype + "_" + p_receiversubscribertype + "_" + p_senderserviceclasscode + "_" + p_receiverserviceclasscode + "_" + p_subservice + "_" + p_promotionallevel + "_" + PretupsI.ALL + "_" + PretupsI.ALL);
            if (LOG.isDebugEnabled()) {
                LOG.debug(
                    "getPromotionalTransferRule",
                    p_transferid,
                    "Validation For Network Code=" + p_networkcode + " Sender Subscriber Type=" + p_sendersubscribertype + " Sender Service class=" + p_senderserviceclasscode + "Receiver Subscriber Type=" + p_receiverserviceclasscode + " Receiver Service Class ID=" + p_receiverallserviceclasscode + " Sub Service=" + p_subservice + p_info + " service Class + ALL SP Name + ALL Subscriber status");
            }

        }

        // 4. Check the for ALL service Class + ALL SP Name + Subscriber status
        if (transferRulesVO == null && !BTSLUtil.isNullString(p_receiverallserviceclasscode)) {
            transferRulesVO = (TransferRulesVO) p_transferrulemap
                .get(p_servicetype + "_" + p_module + "_" + p_receivernetworkcode + "_" + p_sendersubscribertype + "_" + p_receiversubscribertype + "_" + p_senderserviceclasscode + "_" + p_receiverallserviceclasscode + "_" + p_subservice + "_" + p_promotionallevel + "_" + PretupsI.ALL + "_" + p_accountSsatus);
            if (LOG.isDebugEnabled()) {
                LOG.debug(
                    "getPromotionalTransferRule",
                    p_transferid,
                    "Validation For Network Code=" + p_networkcode + " Sender Subscriber Type=" + p_sendersubscribertype + " Sender Service class=" + p_senderserviceclasscode + "Receiver Subscriber Type=" + p_receiverserviceclasscode + " Receiver Service Class ID=" + p_receiverallserviceclasscode + " Sub Service=" + p_subservice + p_info + " ALL service Class + ALL SP Name + Subscriber status");
            }

        }

        // 5. Check the for ALL service Class + SP Name + ALL Subscriber status
        if (transferRulesVO == null && !BTSLUtil.isNullString(p_receiverallserviceclasscode)) {
            transferRulesVO = (TransferRulesVO) p_transferrulemap
                .get(p_servicetype + "_" + p_module + "_" + p_receivernetworkcode + "_" + p_sendersubscribertype + "_" + p_receiversubscribertype + "_" + p_senderserviceclasscode + "_" + p_receiverallserviceclasscode + "_" + p_subservice + "_" + p_promotionallevel + "_" + p_spgroup + "_" + PretupsI.ALL);
            if (LOG.isDebugEnabled()) {
                LOG.debug(
                    "getPromotionalTransferRule",
                    p_transferid,
                    "Validation For Network Code=" + p_networkcode + " Sender Subscriber Type=" + p_sendersubscribertype + " Sender Service class=" + p_senderserviceclasscode + "Receiver Subscriber Type=" + p_receiverserviceclasscode + " Receiver Service Class ID=" + p_receiverallserviceclasscode + " Sub Service=" + p_subservice + p_info + " ALL service Class + SP Name + ALL Subscriber status ");
            }

        }

        // 6. Check the for ALL service Class + ALL SP Name + ALL Subscriber
        // status
        if (transferRulesVO == null && !BTSLUtil.isNullString(p_receiverallserviceclasscode)) {
            transferRulesVO = (TransferRulesVO) p_transferrulemap
                .get(p_servicetype + "_" + p_module + "_" + p_receivernetworkcode + "_" + p_sendersubscribertype + "_" + p_receiversubscribertype + "_" + p_senderserviceclasscode + "_" + p_receiverallserviceclasscode + "_" + p_subservice + "_" + p_promotionallevel + "_" + PretupsI.ALL + "_" + PretupsI.ALL);
            if (LOG.isDebugEnabled()) {
                LOG.debug(
                    "getPromotionalTransferRule",
                    p_transferid,
                    "Validation For Network Code=" + p_networkcode + " Sender Subscriber Type=" + p_sendersubscribertype + " Sender Service class=" + p_senderserviceclasscode + "Receiver Subscriber Type=" + p_receiverserviceclasscode + " Receiver Service Class ID=" + p_receiverallserviceclasscode + " Sub Service=" + p_subservice + p_info + " ALL service Class + ALL SP Name + ALL Subscriber status");
            }

        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("getPromotionalTransferRule", transferRulesVO, "Exiting");
        }

        return transferRulesVO;
    }

    public static void generateLMSTransferID(LoyaltyVO p_transferVO) throws BTSLBaseException {
        final String methodName = "generateLMSTransferID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered ");
        }
        long newTransferID = 0;
        String transferID = null;
        try {
            newTransferID = IDGenerator.getNextID(PretupsI.ID_GEN_LMS_TRANSFER_NO, BTSLUtil.getFinancialYearLastDigits(4), p_transferVO.getNetworkCode(), p_transferVO
                .getCreatedOn());
            if (newTransferID == 0) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = formatLMSTransferID(p_transferVO, newTransferID);
            if (transferID == null) {
                throw new BTSLBaseException("PretupsBL", "generateC2STransferID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setLmstxnid(transferID);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID,e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting with TransferID=" + transferID);
            }
        }
    }

    public static String formatLMSTransferID(LoyaltyVO p_transferVO, long p_tempTransferID) {
        String returnStr = null;
        final String methodName = "formatLMSTransferID";
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toHexString(p_tempTransferID), Integer.parseInt(Constants.getProperty("LMS_PADDING_LENGTH")));
            returnStr = "LMS" + _operatorUtil.currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + _operatorUtil.currentTimeFormatString(p_transferVO
                .getCreatedOn()) + "." + Constants.getProperty("INSTANCE_ID") + paddedTransferIDStr;
            p_transferVO.setLmstxnid(returnStr);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    public static void generateLMSTransferID(LoyalityStockTxnVO loyalityStockTxnVO) throws BTSLBaseException {
        final String methodName = "generateLMSTransferID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered ");
        }
        long newTransferID = 0;
        String transferID = null;
        try {
            newTransferID = IDGenerator.getNextID(PretupsI.ID_GEN_LMS_TRANSFER_NO, BTSLUtil.getFinancialYearLastDigits(4), loyalityStockTxnVO.getNetworkCode(),
                loyalityStockTxnVO.getCreatedOn());
            if (newTransferID == 0) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = formatLMSTransferID(loyalityStockTxnVO, newTransferID);
            if (transferID == null) {
                throw new BTSLBaseException("PretupsBL", "generateC2STransferID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            loyalityStockTxnVO.setTxnNo(transferID);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID,e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting with TransferID=" + transferID);
            }
        }
    }

    public static String formatLMSTransferID(LoyalityStockTxnVO loyalityStockTxnVO, long p_tempTransferID) {
        String returnStr = null;
        final String methodName = "formatLMSTransferID";
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toHexString(p_tempTransferID), Integer.parseInt(Constants.getProperty("LMS_PADDING_LENGTH")));
            returnStr = "LMS" + _operatorUtil.currentDateTimeFormatString(loyalityStockTxnVO.getCreatedOn()) + "." + _operatorUtil.currentTimeFormatString(loyalityStockTxnVO
                .getCreatedOn()) + "." + Constants.getProperty("INSTANCE_ID") + paddedTransferIDStr;
            loyalityStockTxnVO.setTxnNo(returnStr);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Validates the MSISDN
     * 
     * @param p_receiverVO
     * @param p_msisdn
     * @throws BTSLBaseException
     *             21/04/07 : Added Param p_con for MNP
     */
    public static void validateDTHAccount(Connection p_con, ReceiverVO p_receiverVO, String p_requestID, String p_msisdn) throws BTSLBaseException {
        final String methodName = "validateDTHAccountId";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Entered for p_msisdn= " + p_msisdn);
        }
        String[] strArr = null;
        Integer minAccountIdLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_ACCOUNT_ID_LENGTH_CODE);
        Integer maxAccountIdLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_ACCOUNT_ID_LENGTH_CODE);
        
        try {
            if (BTSLUtil.isNullString(p_msisdn)) {
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_ACCOUNT_ID_BLANK);
            }
            p_msisdn = getFilteredMSISDN(p_msisdn);
            p_msisdn = _operatorUtil.addRemoveDigitsFromMSISDN(p_msisdn);
            p_msisdn = spacePad(p_msisdn, (int)maxAccountIdLength, "0", 'l');

            if ((p_msisdn.length() < (int)minAccountIdLength || p_msisdn.length() > (int)maxAccountIdLength)) {
                if ((int)minAccountIdLength != (int)maxAccountIdLength) {
                    strArr = new String[] { p_msisdn, String.valueOf((int)minAccountIdLength), String.valueOf((int)maxAccountIdLength) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_ACCOUNT_ID_NOTINRANGE, 0, strArr, null);
                } else {
                    strArr = new String[] { p_msisdn, String.valueOf((int)minAccountIdLength) };
                    throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_ACCOUNT_ID_LEN_NOTSAME, 0, strArr, null);
                }
            }
            try {
                final long lng = Long.parseLong(p_msisdn);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                strArr = new String[] { p_msisdn };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_ACCOUNT_ID_NOTNUMERIC, 0, strArr, null);
            }
            p_receiverVO.setMsisdn(p_msisdn);
            if (LOG.isDebugEnabled() && p_receiverVO.getMsisdn() != null) {
                LOG.debug("", "*********************" + p_receiverVO.getMsisdn());
            }

            final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(p_msisdn, PretupsI.USER_TYPE_RECEIVER);
            if (networkPrefixVO == null) {
                strArr = new String[] { p_msisdn };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
            }
            /*
             * 21/04/07 Code Added for MNP
             * Preference to check whether MNP is allowed in system or not.
             * If yes then check whether Number has not been ported out, If yes
             * then throw error, else continue
             */
            Boolean isMNPAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED);
            if (isMNPAllowed) {
                boolean numberAllowed = false;
                if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_msisdn, "", PretupsI.PORTED_IN);
                    if (!numberAllowed) {
                        throw new BTSLBaseException("PretupsBL", "getNetworkDetails", PretupsErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_msisdn }, null);
                    }
                } else {
                    numberAllowed = _numberPortDAO.isExists(p_con, p_msisdn, "", PretupsI.PORTED_OUT);
                    if (numberAllowed) {
                        throw new BTSLBaseException("PretupsBL", "getNetworkDetails", PretupsErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_msisdn }, null);
                    }
                }
            }
            // 21/04/07: MNP Code End
            p_receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
            p_receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
            p_receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());

        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateDTHAccountId]", "", "", "",
                "Exception while validating msisdn" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_requestID, "Exiting for p_msisdn= " + p_msisdn);
        }
    }

    private static String spacePad(String p_messageStr, int p_padLength, String p_padStr, char p_direction) throws BTSLBaseException {
        StringBuffer padStrBuffer = null;
        try {
            if (p_messageStr.length() < p_padLength) {
                final int paddingLength = p_padLength - p_messageStr.length();
                padStrBuffer = new StringBuffer(10);
                if (p_direction == 'r') {
                    padStrBuffer.append(p_messageStr);
                }
                for (int i = 0; i < paddingLength; i++) {
                    padStrBuffer.append(p_padStr);
                }
                if (p_direction == 'l') {
                    padStrBuffer.append(p_messageStr);
                }
                p_messageStr = padStrBuffer.toString();
            }
        } catch (Exception e) {
        	throw new BTSLBaseException("PretupsBL", "spacePad", "Exception in space pad",e);
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
    public static void validateRecieverLimitsReversal(TransferVO p_transferVO, int p_stage, String p_moduleCode) throws BTSLBaseException {

        String restrictedMSISDN = null;
        final String methodName = "validateRecieverLimitsReversal";

        // For Number back Service
        if (PretupsI.C2S_MODULE.equals(p_moduleCode)) {
            restrictedMSISDN = (((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryVO().getRestrictedMsisdns());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                p_transferVO.getTransferID(),
                "Entered p_stage:" + p_stage + " p_moduleCode=" + p_moduleCode + "  restrictedMSISDN: " + restrictedMSISDN + " p_transferVO.getReceiverSubscriberType(): " + p_transferVO
                    .getReceiverSubscriberType());
        }
        final ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
        String subscriberFailCtincrCodes = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_FAIL_CTINCR_CODES);
        try {
            final Date currentDate = p_transferVO.getCreatedOn();
            final String receiverSubscriberType = p_transferVO.getReceiverTransferItemVO() == null ? p_transferVO.getReceiverSubscriberType() : p_transferVO
                .getReceiverTransferItemVO().getSubscriberType();

            if (p_stage == PretupsI.TRANS_STAGE_AFTER_INVAL) {

                if (!BTSLUtil.isNullString(receiverVO.getInterfaceResponseCode())) {
                    // TO This needs to be done interface Type wise
                    final String errorCodesForFail = BTSLUtil.NullToString(subscriberFailCtincrCodes);
                    if (BTSLUtil.isNullString(errorCodesForFail)) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateRecieverLimits]",
                            p_transferVO.getTransferID(), receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Error codes for consideration in Fail cases not defined");
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

            } else if (p_stage == PretupsI.TRANS_STAGE_AFTER_INTOP) {
                if (receiverVO.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS))// If
                // Transaction
                // is
                // success
                {
                    // Same for Ambiguous Case
                    receiverVO.setTotalConsecutiveFailCount(0);
                    if (receiverVO.getTotalSuccessCount() > 0) {
                        receiverVO.setTotalSuccessCount(receiverVO.getTotalSuccessCount() - 1);
                    } else {
                        receiverVO.setTotalSuccessCount(0);
                    }
                    if (receiverVO.getTotalTransferAmount() > 0) {
                        receiverVO.setTotalTransferAmount(receiverVO.getTotalTransferAmount() - p_transferVO.getRequestedAmount());
                    } else {
                        receiverVO.setTotalTransferAmount(0);
                    }
                    receiverVO.setLastTransferStage(PretupsI.TRANSACTION_SUCCESS_STATUS);
                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                    receiverVO.setLastSuccessOn(currentDate);
                    // Increment the weekly and monthly counts
                    if (receiverVO.getWeeklySuccCount() > 0) {
                        receiverVO.setWeeklySuccCount(receiverVO.getWeeklySuccCount() - 1);
                    } else {
                        receiverVO.setWeeklySuccCount(0);
                    }
                    if (receiverVO.getWeeklyTransferAmount() > 0) {
                        receiverVO.setWeeklyTransferAmount(receiverVO.getWeeklyTransferAmount() - p_transferVO.getRequestedAmount());
                    } else {
                        receiverVO.setWeeklyTransferAmount(0);
                    }

                    if (receiverVO.getMonthlySuccCount() > 0) {
                        receiverVO.setMonthlySuccCount(receiverVO.getMonthlySuccCount() - 1);
                    } else {
                        receiverVO.setMonthlySuccCount(0);
                    }
                    if (receiverVO.getMonthlyTransferAmount() > 0) {
                        receiverVO.setMonthlyTransferAmount(receiverVO.getMonthlyTransferAmount() - p_transferVO.getRequestedAmount());
                    } else {
                        receiverVO.setMonthlyTransferAmount(0);
                    }

                } else if (receiverVO.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL))// If
                // Transaction
                // is
                // faile
                {
                    if (!BTSLUtil.isNullString(receiverVO.getInterfaceResponseCode())) {
                        final String errorCodesForFail = BTSLUtil.NullToString(subscriberFailCtincrCodes);
                        if (errorCodesForFail == null || BTSLUtil.isNullString(errorCodesForFail)) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[validateSenderLimits]",
                                p_transferVO.getTransferID(), receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Error codes for consideration in Fail cases not defined");
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
                    // Same for Ambiguous Case
                    receiverVO.setTotalConsecutiveFailCount(0);
                    if (receiverVO.getTotalSuccessCount() > 0) {
                        receiverVO.setTotalSuccessCount(receiverVO.getTotalSuccessCount() - 1);
                    } else {
                        receiverVO.setTotalSuccessCount(0);
                    }
                    if (receiverVO.getTotalTransferAmount() > 0) {
                        receiverVO.setTotalTransferAmount(receiverVO.getTotalTransferAmount() - p_transferVO.getRequestedAmount());
                    } else {
                        receiverVO.setTotalTransferAmount(0);
                    }
                    receiverVO.setLastTransferStage(PretupsI.TRANSACTION_SUCCESS_STATUS);
                    receiverVO.setLastTransferStatus(PretupsI.TXN_STATUS_COMPLETED);
                    receiverVO.setLastSuccessOn(currentDate);
                    // Increment the weekly and monthly counts
                    if (receiverVO.getWeeklySuccCount() > 0) {
                        receiverVO.setWeeklySuccCount(receiverVO.getWeeklySuccCount() - 1);
                    } else {
                        receiverVO.setWeeklySuccCount(0);
                    }
                    if (receiverVO.getWeeklyTransferAmount() > 0) {
                        receiverVO.setWeeklyTransferAmount(receiverVO.getWeeklyTransferAmount() - p_transferVO.getRequestedAmount());
                    } else {
                        receiverVO.setWeeklyTransferAmount(0);
                    }

                    if (receiverVO.getMonthlySuccCount() > 0) {
                        receiverVO.setMonthlySuccCount(receiverVO.getMonthlySuccCount() - 1);
                    } else {
                        receiverVO.setMonthlySuccCount(0);
                    }
                    if (receiverVO.getMonthlyTransferAmount() > 0) {
                        receiverVO.setMonthlyTransferAmount(receiverVO.getMonthlyTransferAmount() - p_transferVO.getRequestedAmount());
                    } else {
                        receiverVO.setMonthlyTransferAmount(0);
                    }

                }

            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateRecieverLimits]", p_transferVO
                .getTransferID(), receiverVO.getMsisdn(), receiverVO.getNetworkCode(), "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "validateRecieverLimits", PretupsErrorCodesI.ERROR_EXCEPTION,e);
        }
    }

    /**
     * @author birendra.mishra
     *         This Method Pulls the complete list of
     *         User_Product_Network_Wallet_Mapping and furhter filters out those
     *         records which matces the given
     *         networkId & productId. Then it sorts the list in ascending order
     *         based upon the wallet priority.
     * @param p_networkId
     * @param p_productId
     * @return
     */
    public static List<UserProductWalletMappingVO> getPrtSortWalletsForNetIdAndPrdId(String p_networkId, String p_productId) throws BTSLBaseException {

        final String methodName = "getPrtSortWalletsForNetAndPrdct";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered...");
        }

        final List<UserProductWalletMappingVO> userProductWalletMappingList = UserProductWalletMappingCache.getUserProductWalletMappingList();
        final List<UserProductWalletMappingVO> networkPrdMatchingList = new ArrayList<UserProductWalletMappingVO>();

        if (userProductWalletMappingList == null || userProductWalletMappingList.isEmpty()) {
            final String strArr[] = { p_networkId, p_productId };
            throw new BTSLBaseException(PretupsBL.class, methodName, PretupsErrorCodesI.NO_WALLET_EXIST_NETID_PRDID, 0, strArr, null);
        }

        for (final Iterator<UserProductWalletMappingVO> iterator = userProductWalletMappingList.iterator(); iterator.hasNext();) {
            final UserProductWalletMappingVO userProductWalletMappingVO = iterator.next();

            final String networkCode = userProductWalletMappingVO.getNetworkCode();
            final String productCode = userProductWalletMappingVO.getProductCode();

            if (!BTSLUtil.isNullString(networkCode) && !BTSLUtil.isNullString(productCode)) {
                if (networkCode.equals(p_networkId) && productCode.equals(p_productId)) {
                    networkPrdMatchingList.add(userProductWalletMappingVO);
                }
            }
        }
        Collections.sort(networkPrdMatchingList);

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting...");
        }
        return networkPrdMatchingList;
    }

    /**
     * @author birendra.mishra
     *         This method shall return the list of wallets configured for the
     *         incoming netoworkId and ProductType values.
     * @param p_networkId
     * @param p_productType
     * @return
     * @throws BTSLBaseException
     */
    public static List<UserProductWalletMappingVO> getPrtSortWalletsForNetIdAndPrdType(String p_networkId, String p_productType) throws BTSLBaseException {

        final String methodName = "getPrtSortWalletsForNetIdAndPrdType";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered...");
        }

        final List<UserProductWalletMappingVO> userProductWalletMappingList = UserProductWalletMappingCache.getUserProductWalletMappingList();
        final List<UserProductWalletMappingVO> networkPrdMatchingList = new ArrayList<UserProductWalletMappingVO>();

        if (userProductWalletMappingList == null || userProductWalletMappingList.isEmpty()) {
            final String strArr[] = { p_networkId, p_productType };
            throw new BTSLBaseException(PretupsBL.class, methodName, PretupsErrorCodesI.NO_WALLET_EXIST_NETID_PRDTYPE, 0, strArr, null);
        }

        for (final Iterator<UserProductWalletMappingVO> iterator = userProductWalletMappingList.iterator(); iterator.hasNext();) {
            final UserProductWalletMappingVO userProductWalletMappingVO = iterator.next();

            final String networkCode = userProductWalletMappingVO.getNetworkCode();
            final String productType = userProductWalletMappingVO.getProductType();

            if (!BTSLUtil.isNullString(networkCode) && !BTSLUtil.isNullString(productType)) {
                if (networkCode.equals(p_networkId) && productType.equals(p_productType)) {
                    networkPrdMatchingList.add(userProductWalletMappingVO);
                }
            }
        }
        Collections.sort(networkPrdMatchingList);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting...");
        }
        return networkPrdMatchingList;
    }

    /**
     * This method iterates over the received wallet list and creates a new of
     * wallets whose partialDeductionAllowed flag is active.
     * 
     * @param walletsForNetAndPrdct
     * @return
     */
    public static List<String> getPDAWallets(List<UserProductWalletMappingVO> walletsForNetAndPrdct) {
        final String methodName = "getPDAWallets";

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered...");
        }

        final List<String> pdaWallets = new ArrayList<String>();
        for (final Iterator<UserProductWalletMappingVO> iterator = walletsForNetAndPrdct.iterator(); iterator.hasNext();) {
            final UserProductWalletMappingVO userProductWalletMappingVO = iterator.next();
            if (PretupsI.YES.equals(userProductWalletMappingVO.getPartialDedAlwd())) {
                pdaWallets.add(userProductWalletMappingVO.getAccountCode());
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting...");
        }
        return pdaWallets;
    }

    /**
     * @author birendra.mishra
     *         This method shall iterator over the list of wallets and creates a
     *         new list containing the wallets whose PDA flag is active.
     * @param walletsForNetAndPrdct
     * @return
     */
    public static List<UserProductWalletMappingVO> getPDAWalletsVO(List<UserProductWalletMappingVO> walletsForNetAndPrdct) throws BTSLBaseException {
        final String methodName = "getPDAWalletsVO";

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered...");
        }

        final List<UserProductWalletMappingVO> pdaWallets = new ArrayList<UserProductWalletMappingVO>();
        for (final Iterator<UserProductWalletMappingVO> iterator = walletsForNetAndPrdct.iterator(); iterator.hasNext();) {
            final UserProductWalletMappingVO userProductWalletMappingVO = iterator.next();

            pdaWallets.add(userProductWalletMappingVO);

        }

        if (pdaWallets.isEmpty()) {
            throw new BTSLBaseException(PretupsBL.class, methodName, PretupsErrorCodesI.NO_PDAWALLET_EXIST);
        }

        Collections.sort(pdaWallets);

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting...");
        }
        return pdaWallets;
    }

    /**
     * @author birendra.mishra
     *         This method shall iterate over the incoming wallet list and shall
     *         create a new list containing wallets whose Addition Commision
     *         Flag
     *         is active.
     * @param pdaWalletsListWithBalanceDebited
     * @return
     */
    public static List<UserProductWalletMappingVO> getACAWallets(List<UserProductWalletMappingVO> pdaWalletsListWithBalanceDebited) {
        final String methodName = "getACAWallets";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered...");
        }

        UserProductWalletMappingVO userProductWalletMappingVO = null;
        final List<UserProductWalletMappingVO> acaWalletsList = new ArrayList<UserProductWalletMappingVO>();
        for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletsListWithBalanceDebited.iterator(); iterator.hasNext();) {
            userProductWalletMappingVO = iterator.next();
            if (PretupsI.YES.equals(userProductWalletMappingVO.getAddnlComAlwd())) {
                acaWalletsList.add(userProductWalletMappingVO);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting...");
        }
        return acaWalletsList;
    }

    // Meditel changes
    public static String userStatusNotIn() {
        String status = null;
        status = "'"+PretupsI.USER_STATUS_DELETED+"','"+PretupsI.USER_STATUS_CANCELED+"'";
        return status;
    }

    public static String userStatusIn() {
        String status = null;
        status = "'" + PretupsI.USER_STATUS_ACTIVE + "','" + PretupsI.USER_STATUS_SUSPEND + "','" + PretupsI.USER_STATUS_CHURN + "','" + PretupsI.USER_STATUS_PREACTIVE + "','" + PretupsI.USER_STATUS_BARRED + "'";
        return status;
    }

    public static String userStatusActive() {
        String status = null;
        status = "'" + PretupsI.USER_STATUS_ACTIVE + "','" + PretupsI.USER_STATUS_CHURN + "','" + PretupsI.USER_STATUS_PREACTIVE + "'";
        return status;
    }

    public static void updateVoucherTransferDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "updateVoucherTransferDetails";
        try {
            final int updateCount = _transferDAO.updateVoucherTransferDetails(p_con, p_transferVO);
            if (updateCount <= 0) {
                throw new BTSLBaseException("PretupsBL", "updateVoucherTransferDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[updateVoucherTransferDetails]", p_transferVO
                .getTransferID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", "updateVoucherTransferDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
    }
    public static void updateVoucherTransferDetails(Connection p_con,P2PTransferVO p_transferVO,VomsVoucherVO _vomsVO) throws BTSLBaseException
	{
    	final String methodName = "updateVoucherTransferDetails";
		try
		{
			int updateCount=_transferDAO.updateVoucherTransferDetails(p_con,p_transferVO,_vomsVO);
			if(updateCount<=0)
				throw new BTSLBaseException("PretupsBL","updateVoucherTransferDetails",PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			LOG.error(methodName,  "Exception"+ e.getMessage());
    		LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[updateVoucherTransferDetails]",p_transferVO.getTransferID(),"","","Exception :"+e.getMessage());
			throw new BTSLBaseException("PretupsBL","updateVoucherTransferDetails",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
		}
	}

    public static int chkAllwdStatusToBecomeActive(Connection p_con, String p_allowedStatus, String p_userId, String p_userStatus) {
        final String methodName = "chkAllwdStatusToBecomeActive";
        int cnt = 0;
        try {
            if (!PretupsI.USER_STATUS_ACTIVE.equals(p_userStatus) && !BTSLUtil.isNullString(p_allowedStatus)) {
                final String str[] = p_allowedStatus.split(","); // "CH:Y,EX:Y,PA:Y".split(",")
                String newStatus[] = null;
                for (int i = 0; i < str.length; i++) {
                    newStatus = str[i].split(":");
                    if (newStatus[0].equals(p_userStatus)) {
                        cnt = _operatorUtil.changeUserStatusToActive(p_con, p_userId, p_userStatus, newStatus[1]);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[chkAllwdStatusToBecomeActive]",
                p_allowedStatus, "", "", "Exception :" + e.getMessage());
        }
        return cnt;
    }

    public static boolean checkGeoFencing(RequestVO requestVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
        // geo-fencing validation
    	Boolean isAlertAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALERT_ALLOWED);
    	Boolean isBlockingAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BLOCKING_ALLOWED);
    	Boolean isEmailServiceAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
    	
        final boolean alertAllowed = isAlertAllowed;// SystemPreference
        // flag
        final boolean blockingAllowed = isBlockingAllowed;// SystemPreference
        // flag
        final boolean emailAllowed = isEmailServiceAllow;// SystemPreference
        boolean mappingExists =false;
        // flag
        String cellId = null;
        if (("USSD".equals(requestVO.getMessageGatewayVO().getGatewayType()) || "EXTGW".equals(requestVO.getMessageGatewayVO().getGatewayType())) && (alertAllowed || blockingAllowed)) {
            try {
                // String
                // cellId=(String)requestVO.getRequestMap().get("CELLID")
                // //transaction cell id
                final String requestStr = requestVO.getRequestMessage();
                final int index = requestStr.indexOf("<CELLID>");
                if (index >= 0) {
                    cellId = requestStr.substring(index + "<CELLID>".length(), requestStr.indexOf("</CELLID>", index));
                }

                //USSD Plain Request
                if(cellId==null){
                	cellId=requestVO.getCellId();
                }
                mappingExists = !BTSLUtil.isNullString((String) CellIdCache.getObject(cellId, channelUserVO.getGeographicalCode()));
                if (!BTSLUtil.isNullString(cellId)) {
                    if (!mappingExists && blockingAllowed) {

                        throw new BTSLBaseException("PretupsBL", "checkGeoFencing", PretupsErrorCodesI.TRANS_BLOCKED);// "232323"
                        // messageCode
                    }
                    if (!mappingExists && alertAllowed) {
                        final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), PretupsErrorCodesI.SEND_ALERT,
                            new String[] { channelUserVO.getMsisdn(), cellId });// requestVO.getMessageCode(),requestVO.getMessageArguments()
                        final PushMessage pushMessage = new PushMessage(Constants.getProperty("adminmobile"), senderMessage, requestVO.getRequestIDStr(), requestVO
                            .getRequestGatewayCode(), requestVO.getLocale());
                        pushMessage.push();
                    }
                    
                    Boolean isAlertAllowedUser = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALERT_ALLOWED_USER);
                    if (isAlertAllowedUser )
                    {
                    	  final String senderUserMessage = BTSLUtil.getMessage(requestVO.getLocale(), PretupsErrorCodesI.SEND_ALERT_USER,
                                  new String[] { channelUserVO.getMsisdn(), cellId });// requestVO.getMessageCode(),requestVO.getMessageArguments()
                              final PushMessage pushMessage1 = new PushMessage(channelUserVO.getMsisdn(), senderUserMessage, requestVO.getRequestIDStr(), requestVO
                                  .getRequestGatewayCode(), requestVO.getLocale());
                              pushMessage1.push();	
                    }
                   
                }
            } catch (BTSLBaseException e) {
                throw e;
            }
        }
        // end of geo-fencing validation
        return mappingExists;
    }
    
    public  static TransferRulesVO selectTransferRuleFromCache(Connection p_con,TransferVO p_transferVO, ReceiverVO p_receiverVO) {
    	final String methodName = "selectTransferRuleFromCache";
    	TransferRulesVO transferRulesVO = null;
    	UserDAO userDAO = null;
    	try {
    		//ASHU
    		if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Entered :");
            }
    		userDAO = new UserDAO();
    		//String userGrade = ((ChannelUserVO)(userDAO.loadUserDetailsFormUserID(p_con,p_transferVO.getSenderID()))).getUserGrade();
    		String userGrade = ((ChannelUserVO)(p_transferVO.getSenderVO())).getUserGrade();

    		//pick for gateway = G category=X and grade=Y
    		transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO
    				.getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getDomainID(), p_receiverVO.getSubscriberType(), PretupsI.ALL, p_receiverVO
    				.getServiceClassCode(), p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE, p_transferVO.getRequestGatewayCode(),((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryCode(),userGrade);

    		//pick for gateway = ALL category=X and grade=Y
    		if (transferRulesVO == null) {
    			transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO
    					.getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getDomainID(), p_receiverVO.getSubscriberType(), PretupsI.ALL, p_receiverVO
    					.getServiceClassCode(), p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE, PretupsI.ALL,((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryCode(),userGrade);
    		}

    		//pick for gateway = G category=X and grade=ALL
    		if (transferRulesVO == null) {
    		transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO
    				.getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getDomainID(), p_receiverVO.getSubscriberType(), PretupsI.ALL, p_receiverVO
    				.getServiceClassCode(), p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE, p_transferVO.getRequestGatewayCode(),((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryCode(),PretupsI.ALL);
    		}
    		//pick for gateway = ALL category=X and grade=ALL
    		if (transferRulesVO == null) {
    			transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO
    					.getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getDomainID(), p_receiverVO.getSubscriberType(), PretupsI.ALL, p_receiverVO
    					.getServiceClassCode(), p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE, PretupsI.ALL,((ChannelUserVO) p_transferVO.getSenderVO()).getCategoryCode(),PretupsI.ALL);
    		}

    		//pick for gateway = G category=ALL and grade=ALL
    		if (transferRulesVO == null) {
    			transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO
    					.getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getDomainID(), p_receiverVO.getSubscriberType(), PretupsI.ALL, p_receiverVO
    					.getServiceClassCode(), p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE, p_transferVO.getRequestGatewayCode(),PretupsI.ALL,PretupsI.ALL);
    		}

    		//pick for gateway = ALL category=ALL and grade=ALL
    		if (transferRulesVO == null) {
    			transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(p_transferVO.getServiceType(), p_transferVO.getModule(), p_transferVO
    					.getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getDomainID(), p_receiverVO.getSubscriberType(), PretupsI.ALL, p_receiverVO
    					.getServiceClassCode(), p_transferVO.getSubService(), PretupsI.NOT_APPLICABLE, PretupsI.ALL,PretupsI.ALL,PretupsI.ALL);
    		}
    		//changes end here
    	} catch (Exception e) {
    		
    		LOG.error(methodName,  "Exception"+ e.getMessage());
    		LOG.errorTrace(methodName, e);
    	}finally {
    		if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting transferRulesVO : "+transferRulesVO);
            }
    	}
    	return transferRulesVO;
    }

	/**
	 * Validates the IAT amount
	 * @param p2pTransferVO
	 * @param p_requestAmount
	 * @throws BTSLBaseException
	 */
	public static void validateIATAmount(P2PTransferVO p2pTransferVO,String p_requestAmount) throws BTSLBaseException
	{
		final String method_name="validateIATAmount";
		if(LOG.isDebugEnabled()) LOG.debug("validateIATAmount",p2pTransferVO.getRequestID(),"Entered p_requestAmount="+p_requestAmount);
		String[] strArr=null;
		double requestAmt=0;
		String msgRequestAmount=null;
		try
		{
			if (BTSLUtil.isNullString(p_requestAmount))
				throw new BTSLBaseException("PretupsBL","validateIATAmount",PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_BLANK);
			if(p2pTransferVO.getRequestGatewayType().equals(Constants.getProperty("GATEWAYTYPES_MIN_DENOMINATION_ALLOWED")))
				msgRequestAmount=getDisplayAmount(Long.parseLong(p_requestAmount));
			else
				msgRequestAmount=p_requestAmount;
			try
			{
				requestAmt=Double.parseDouble(p_requestAmount);
			}
			catch(Exception e)
			{
				strArr=new String[]{msgRequestAmount};
				throw new BTSLBaseException("PretupsBL","validateIATAmount",PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_NOTNUMERIC,0,strArr,null);
			}
			if (requestAmt <=0)
			{
				strArr=new String[]{msgRequestAmount};
				throw new BTSLBaseException("PretupsBL","validateIATAmount",PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_LESSTHANZERO,0,strArr,null);
			}
			 if(p2pTransferVO.getRequestGatewayType().equals(Constants.getProperty("GATEWAYTYPES_MIN_DENOMINATION_ALLOWED")))
            {
                 p2pTransferVO.setTransferValue(Long.parseLong(p_requestAmount));
                 p2pTransferVO.setRequestedAmount(Long.parseLong(p_requestAmount));
            }
             else
             {
                 p2pTransferVO.setTransferValue(getSystemAmount(p_requestAmount));
                 p2pTransferVO.setRequestedAmount(getSystemAmount(p_requestAmount));
             }
			 p2pTransferVO.getIatTransferItemVO().setQuantity(p2pTransferVO.getRequestedAmount());
			
			Object objVal=PreferenceCache.getNetworkPrefrencesValue(PreferenceI.C2S_MINTRNSFR_AMOUNT,p2pTransferVO.getNetworkCode());
			if(objVal!=null)
			{
				if(p2pTransferVO.getRequestedAmount()<((Long)objVal).longValue())
				{
					strArr=new String[]{msgRequestAmount,PretupsBL.getDisplayAmount(((Long)objVal).longValue())};
					throw new BTSLBaseException("PretupsBL","validateIATAmount",PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_LESSTHANALLOWED,0,strArr,null);
				}
			}
			objVal=null;
			objVal=PreferenceCache.getNetworkPrefrencesValue(PreferenceI.C2S_MAXTRNSFR_AMOUNT,p2pTransferVO.getNetworkCode());
			if(objVal!=null)
			{
				if(p2pTransferVO.getRequestedAmount()>((Long)objVal).longValue())
				{
					strArr=new String[]{msgRequestAmount,PretupsBL.getDisplayAmount(((Long)objVal).longValue())};
					throw new BTSLBaseException("PretupsBL","validateIATAmount",PretupsErrorCodesI.CHNL_ERROR_RECR_AMT_MORETHANALLOWED,0,strArr,null);
				}
			}
		}
		catch(BTSLBaseException be)
		{
			
			throw be;
		}
		catch(Exception e)
		{
		
    		
			LOG.error("validateAmount","  Exception while validate amount :"+e.getMessage());
			LOG.errorTrace(method_name, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateAmount]","","","","Exception while validating amount" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException("PretupsBL","validateAmount",PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
		}		
		if(LOG.isDebugEnabled()) LOG.debug("validateAmount",p2pTransferVO.getRequestID(),"Exiting ");
	}

		/**
	 * Validates the MSISDN
	 * @param p_receiverVO
	 * @param p_msisdn
	 * @throws BTSLBaseException
	 * p_requestID
	 */
	public static void validateIATMsisdn(ReceiverVO p_receiverVO,P2PTransferVO p2pTransferVO,String p_msisdn,IATTransferItemVO p_iatTrfItemVO) throws BTSLBaseException
	{
	    String p_requestID = p2pTransferVO.getRequestID();
		if(LOG.isDebugEnabled()) LOG.debug("validateMsisdn",p_requestID,"Entered for p_msisdn= "+p_msisdn);
		String[] strArr=null;
		
		try
		{
		    if (BTSLUtil.isNullString(p_msisdn))
				throw new BTSLBaseException("PretupsBL","validateMsisdn",PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
			//If Receiver msisdn contains '00' or '+', remove these.
		    if(p_msisdn.startsWith("00"))
	            p_msisdn=p_msisdn.substring(2);	        
	        else if(p_msisdn.startsWith("+"))
	            p_msisdn=p_msisdn.substring(1);
	        try
			{
				long lng=Long.parseLong(p_msisdn);
			}
			catch(Exception e)
			{
				strArr=new String[]{p_msisdn};
				throw new BTSLBaseException("PretupsBL","validateIATMsisdn",PretupsErrorCodesI.IAT_ERROR_RECR_MSISDN_NOTNUMERIC,0,strArr,null);
			}
	        //Get receiver country VO. 
		    IATCountryMasterVO masCountryVO= getIATCountryVO(p_msisdn);
		    //From country masters VO set country short name, country code country currency in the iat item vo.
		    p_iatTrfItemVO.setIatRecCountryShortName(masCountryVO.getRecCountryShortName());
		    p_iatTrfItemVO.setIatRcvrCountryName(masCountryVO.getRecCountryName());
		    p_iatTrfItemVO.setIatRcvrCurrency(masCountryVO.getCurrency());
		    String msisdnPrefixList = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE);
		    //check if country is enabled ofr IAT transactions or not.If not then throw exception
		    if(!PretupsI.YES.equals(masCountryVO.getCountryStatus()))
		    {
		        //put params in msg
		        //LOG.error("getIATFilteredMSISDN","MSISDN provided belongs to the country where IAT services are not active. Country Name: "+masCountryVO.getRecCountryName());
		        //EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[getIATFilteredMSISDN]","",p_msisdn,"","MSISDN provided belongs to the country where IAT services are not active. Country Name: "+masCountryVO.getRecCountryName());
				if(PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(p2pTransferVO.getLocale())).getMessage()))								
				    p2pTransferVO.setSenderReturnMessage(masCountryVO.getLanguage1Message());
				else 
				    p2pTransferVO.setSenderReturnMessage(masCountryVO.getLanguage2Message());
		        throw new BTSLBaseException(PretupsErrorCodesI.IAT_CNTRY_NOT_ACTIVE,new String[]{String.valueOf(masCountryVO.getRecCountryCode()),masCountryVO.getRecCountryShortName(),masCountryVO.getRecCountryName(),p_msisdn});
		    }
		    
		    //Check if country code received from country vo matches with Prefix/country code (for country) defined in the system preference, then throw exception.
		    //sender and receiver country can not be same for IAT transactions.
		    Object[] countryCodeList= msisdnPrefixList.split(",");
		    
		    String senderCountryCode=(String)countryCodeList[0];
		    if("0".equals(senderCountryCode))
		        senderCountryCode=(String)countryCodeList[1];
		    if(senderCountryCode.startsWith("00"))
		        senderCountryCode=senderCountryCode.substring(2);	        
	        else if(senderCountryCode.startsWith("+"))
	            senderCountryCode=senderCountryCode.substring(1);
	        
		    p_iatTrfItemVO.setSenderCountryCode(senderCountryCode);
		    if(Arrays.asList(countryCodeList).contains(String.valueOf(masCountryVO.getRecCountryCode())))
		    {
		        //LOG.error("getIATFilteredMSISDN","Countries of sender and receiver can not be same for IAT services");
		        //EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[getIATFilteredMSISDN]","",p_msisdn,"","Countries of sender and receiver can not be same for IAT services");
		        throw new BTSLBaseException(PretupsErrorCodesI.IAT_SEN_REC_SAME_CNTRY);
		    }
		    
		    //get the msisdn without country code
		    //receiver MSISDN will be inserted in iat item table without country code.
		    p_msisdn=p_msisdn.substring(String.valueOf(masCountryVO.getRecCountryCode()).length());
		    p_iatTrfItemVO.setIatRcvrPrfxLength(masCountryVO.getPrefixLength());
		    p_iatTrfItemVO.setIatRcvrCountryCode(masCountryVO.getRecCountryCode());
		    if(p_msisdn.length() < masCountryVO.getPrefixLength())
		    {
		    	strArr=new String[]{p_msisdn,String.valueOf(masCountryVO.getMinMsisdnLength()),String.valueOf(masCountryVO.getMaxMsisdnLength())};
				throw new BTSLBaseException("PretupsBL","validateIATMsisdn",PretupsErrorCodesI.IAT_ERROR_RECR_MSISDN_NOTINRANGE,0,strArr,null);
		    }
	    	
	    	p_iatTrfItemVO.setIatRecMsisdn(p_msisdn);
		    //---p_iatTrfItemVO.setIatRcvrPrfx(p_msisdn.substring(0,masCountryVO.getPrefixLength()));
		    //validate length of MSISDN is valid or not. (with out country code)
		    if((p_msisdn.length() < masCountryVO.getMinMsisdnLength() || p_msisdn.length() > masCountryVO.getMaxMsisdnLength()))
			{
		        strArr=new String[]{p_msisdn,String.valueOf(masCountryVO.getMinMsisdnLength()),String.valueOf(masCountryVO.getMaxMsisdnLength())};
				throw new BTSLBaseException("PretupsBL","validateIATMsisdn",PretupsErrorCodesI.IAT_ERROR_RECR_MSISDN_NOTINRANGE,0,strArr,null);				
			}
			
		    //Get network_country Cache object. Iterate through the cache, check whether any vo contains the prefix in it's prefix list.
		    //If contains then get network code from the VO else throw error.
		    ArrayList networkCntryCache = IATNWServiceCache.getNetworkCountryArrObject();
		    if(networkCntryCache!=null)
		    {
		    	int cacheLen = networkCntryCache.size();
		    	if(cacheLen > 0)
		    	{
			  	    for (int i=0;i<cacheLen;i++)
			  	    {
				  		IATNetworkCountryMappingVO networkCntryVO =(IATNetworkCountryMappingVO)networkCntryCache.get(i);
				  		if(networkCntryVO!=null)
				  		{
				  		    String[] prefixList= networkCntryVO.getRecNetworkPrefix().split(",");
				  		    for(int m=0,n=prefixList.length;m<n;m++)
				  		    {
				  		        String nwPrfx=prefixList[m];
				  		        if(p_msisdn.startsWith(nwPrfx) && networkCntryVO.getRecCountryShortName().equals(masCountryVO.getRecCountryShortName()))
				  			    {
				  		            p_iatTrfItemVO.setIatRcvrPrfx(p_msisdn.substring(0,nwPrfx.length()));
				  			    	p_iatTrfItemVO.setIatRecNWCode(networkCntryVO.getRecNetworkCode());
				  			    	if(!PretupsI.YES.equals(networkCntryVO.getStatus()))
				  			    	{
				  			    		String[] strMsg=new String[]{p_iatTrfItemVO.getIatRcvrCountryName(),networkCntryVO.getRecNetworkCode(),networkCntryVO.getRecNetworkName(),p_iatTrfItemVO.getIatRcvrPrfx(),p_iatTrfItemVO.getIatRecMsisdn()};
							  			throw new BTSLBaseException("PretupsBL","validateIATMsisdn",PretupsErrorCodesI.IAT_NW_SUSPENDED,0,strMsg,null);
				  			    	}
				  			    	break;
				  			    }
				  		    }
				  		}
			  	    }
			  	    if(p_iatTrfItemVO.getIatRecNWCode()==null)
			  	    {
			  	    	String[] strMsg=new String[]{"+"+p_iatTrfItemVO.getIatRcvrCountryCode()+p_iatTrfItemVO.getIatRecMsisdn(),p_iatTrfItemVO.getIatRcvrPrfx(),p_iatTrfItemVO.getIatRecCountryShortName(),p_iatTrfItemVO.getIatRcvrCountryName()};
			  			throw new BTSLBaseException("PretupsBL","validateIATMsisdn",PretupsErrorCodesI.IAT_NW_PRFX_NOT_FOUND,0,strMsg,null);
			  	    }	    	            
			  	}
			  	else
			  	{
				  	LOG.error("validateIATMsisdn","Network Country mapping for IAT are not defined in system");
				  	EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateIATMsisdn]","",p_msisdn,"","Network Country mapping for IAT are not defined in system");
				  	throw new BTSLBaseException(PretupsErrorCodesI.IAT_NW_CNTRY_MAPPING_NOT_FOUND,new String[]{p_iatTrfItemVO.getIatRcvrCountryCode()+p_iatTrfItemVO.getIatRecMsisdn(),p_iatTrfItemVO.getIatRcvrPrfx(),p_iatTrfItemVO.getIatRecCountryShortName(),p_iatTrfItemVO.getIatRcvrCountryName()});
			  	}
		      }
		      else
		      {
		    	  
			  	LOG.error("validateIATMsisdn","Network Country mapping for IAT are not defined in system");
			  	EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateIATMsisdn]","",p_msisdn,"","Network Country mapping for IAT are not defined in system");
			  	throw new BTSLBaseException(PretupsErrorCodesI.IAT_NW_CNTRY_MAPPING_NOT_FOUND,new String[]{p_iatTrfItemVO.getIatRcvrCountryCode()+p_iatTrfItemVO.getIatRecMsisdn(),p_iatTrfItemVO.getIatRcvrPrfx(),p_iatTrfItemVO.getIatRecCountryShortName(),p_iatTrfItemVO.getIatRcvrCountryName()});
		      }
			//receiver MSISDN will be inserted in main transfer table without country code.
			p_receiverVO.setMsisdn(p_msisdn);
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
            LOG.errorTrace("validateIATMsisdn", e);
			LOG.error("validateIATMsisdn","  Exception while validating msisdn :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateIATMsisdn]","","","","Exception while validating msisdn" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException("PretupsBL","validateIATMsisdn",PretupsErrorCodesI.IAT_C2S_EXCEPTION,e);
		}	
		if(LOG.isDebugEnabled()) LOG.debug("validateIATMsisdn",p_requestID,"Exiting for p_msisdn= "+p_msisdn);
	}
	

	
	
	public static ListValueVO validateNumberInRoutingDatabaseForMNP(Connection p_con,String p_msisdn,String p_interfaceType) throws BTSLBaseException
	{
		 LOG.debug("validateNumberInRoutingDatabaseForMNP", "Entered with p_msisdn="+p_msisdn+" p_inrefaceType="+p_interfaceType );
		    final String METHOD_NAME = "validateNumberInRoutingDatabaseForMNP";
		    ListValueVO listValueVO=null;
			try
			{
				
				listValueVO=_operatorUtil.getClientMNPInfo(p_con,p_msisdn,p_interfaceType);
				if(listValueVO==null)
				{
				listValueVO=new RoutingTxnDAO().loadInterfaceIDForMNP(p_con,p_msisdn,p_interfaceType);
				}
				return listValueVO;
				
			}
			catch(BTSLBaseException be)
			{
				throw be;
			}
			catch(Exception e)
			{
				LOG.errorTrace(METHOD_NAME,e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateNumberInRoutingDatabase]","",p_msisdn,"","Exception :"+e.getMessage());
				throw new BTSLBaseException("PretupsBL","validateNumberInRoutingDatabase",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
			}
			finally
			{
				LOG.debug("validateNumberInRoutingDatabaseFORMNP","Exiting listValueVO="+listValueVO);
			}
		
	}
	
	


	
    /**
     * Validate Transfer Rule
     * @param p_transferVO
     * @throws BTSLBaseException
     * 1. Check Whether Transfer Rule is not existing and we are using ALL service class code for sender and Receiver
     * 2. If 1 Not exists then show error
     * 3. If ALL is not being used for receiver then load for reciever ALL and check in transfer rule
     * 4. If not exist then check for sender ALL and check in transfer rule
     * 5. Last check for ALL sender and ALL receiver rule
     */
    public static TransferRulesVO getServiceTransferRule(Connection p_con,TransferVO p_transferVO,String p_module) throws BTSLBaseException
    {
    	final String methodName = "validateTransferRule";
    	if (LOG.isDebugEnabled()) 	LOG.debug(methodName,p_transferVO.getTransferID(),"Entered" );
    	TransferRulesVO transferRulesVO=null;
    	TransferItemVO receiverTransferItemVO =new  TransferItemVO();
		receiverTransferItemVO.setAccountStatus("Active");
		//For C2S: Sender service class will be ALL and subscriber type will be Domain ID
    	ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO(); 
    	boolean isPromotionalTrfRuleFound=false;
    	String spGroup=null;String spName=null;
    	//If network preference enables the promotional transfer rules then check that
    	Boolean isServiceProviderPromoAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);
    	if(((Boolean)(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.C2S_PROMOTIONAL_TRFRULE_CHECK,p_transferVO.getNetworkCode()))).booleanValue())
    	{

    		int promotionalTransferRuleStartLevelCode=1;
    		//Find the promotional transfer rule start level. IF not found the  consider it as 1.
    		//1 means user level
    		//2 means grade level
    		//3 means category level
    		//4 means geography level
    		// 5 prefix level
    		// 6 cell group level
    		// 7 service provider group level
    		try 
    		{
    			promotionalTransferRuleStartLevelCode=((Integer)(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.PROMO_TRF_START_LVL_CODE,p_transferVO.getNetworkCode()))).intValue();
    		} 
    		catch (Exception e) 
    		{
    			LOG.errorTrace(methodName,e);
    			promotionalTransferRuleStartLevelCode=1;
    			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"PretupsBL[validateTransferRule]",p_transferVO.getTransferID(),p_transferVO.getSenderMsisdn(),p_transferVO.getNetworkCode(),"Promotional transfer rule starting level is either not defined or is not numeric in the preference so taking user as default");
    		}
    		//Load promotional transfer rules for the transfer date.
    		//HashMap transferRuleMap=transferDAO.loadPromotionalTransferRuleMap(p_con,p_transferVO.getTransferDateTime());
    		HashMap transferRuleMap=TransferRulesCache.loadPromotionalTransferRuleCache(p_transferVO.getTransferDateTime());
    		String recAllServiceClassForPromotional=null;
    	
    		switch(promotionalTransferRuleStartLevelCode)
    		{
    		case PretupsI.PROMO_TRF_RULE_LVL_USR_CODE:
    			//	transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getUserID()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMO_TRF_RULE_LVL_USR_CODE);
    			if(!isServiceProviderPromoAllow)
    			{
    				transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getUserID()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_USER);
    			}
    			else
    			{
    				transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getUserID()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_USER+"_"+spGroup+"_"+receiverTransferItemVO.getAccountStatus());
    			}
    			if (LOG.isDebugEnabled()) 	LOG.debug(methodName,p_transferVO.getTransferID(),"Validation For Network Code="+p_transferVO.getNetworkCode()+" Sender Subscriber Type="+((ChannelUserVO)p_transferVO.getSenderVO()).getUserID()+" Sender Service class="+PretupsI.ALL+"Receiver Subscriber Type="+receiverVO.getSubscriberType()+" Receiver Service Class ID="+receiverVO.getServiceClassCode()+" Sub Service="+p_transferVO.getSubService()+" user level promotional transfer rule");
    			
    			
    			if(transferRulesVO!=null)
    			{
    				/*isExist=isPromotionalRuleExistInRange(transferRulesVO,p_transferVO.getTransferDateTime());
							if(isExist)		
								isPromotionalTrfRuleFound=true;
							else*/
    				isPromotionalTrfRuleFound=true;
    				break;
    			}
    		case PretupsI.PROMO_TRF_RULE_LVL_GRADE_CODE:
    			//transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getUserGrade()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMO_TRF_RULE_LVL_GRADE_CODE);
    			if(!isServiceProviderPromoAllow )

    			{
    				transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getUserGrade()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_GRADE);
    			}
    			else
    			{
    				transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getUserGrade()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_GRADE+"_"+spGroup+"_"+receiverTransferItemVO.getAccountStatus());
    			}
    			if(transferRulesVO!=null)
    			{
    				/*isExist=isPromotionalRuleExistInRange(transferRulesVO,p_transferVO.getTransferDateTime());if(isExist)
							if(isExist)		
								isPromotionalTrfRuleFound=true;
							else*/
    				isPromotionalTrfRuleFound=true;
    				break;
    			}
    		case PretupsI.PROMO_TRF_RULE_LVL_CATEGORY_CODE:
    			//transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getCategoryCode()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMO_TRF_RULE_LVL_CATEGORY_CODE);
    			if(!isServiceProviderPromoAllow )
    			{
    				transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getCategoryCode()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_CATEGORY);

    			}
    			else
    			{
    				transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getCategoryCode()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_CATEGORY+"_"+spGroup+"_"+receiverTransferItemVO.getAccountStatus());
    			}
    			if(transferRulesVO!=null)
    			{
    				/*isExist=isPromotionalRuleExistInRange(transferRulesVO,p_transferVO.getTransferDateTime());if(isExist)
							if(isExist)		
								isPromotionalTrfRuleFound=true;
							else*/
    				isPromotionalTrfRuleFound=true;
    				break;
    			}

    		case PretupsI.PROMO_TRF_RULE_LVL_GEOGRAPHY_CODE:
    			if(!isServiceProviderPromoAllow )
    			{
    				transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getGeographicalCode()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_GEOGRAPHY);

    			}
    			else
    			{
    				transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getGeographicalCode()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_GEOGRAPHY+"_"+spGroup+"_"+receiverTransferItemVO.getAccountStatus());	
    			}
    			if(transferRulesVO!=null)
    			{
    				/*isExist=isPromotionalRuleExistInRange(transferRulesVO,p_transferVO.getTransferDateTime());if(isExist)
							if(isExist)		
								isPromotionalTrfRuleFound=true;
							else*/
    				isPromotionalTrfRuleFound=true;
    				break;
    			}

    		case PretupsI.PROMO_TRF_RULE_LVL_PREFIX_ID:
    			//transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+((ChannelUserVO)p_transferVO.getSenderVO()).getGeographicalCode()+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_PREFIXID);
    			transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+PretupsI.ALL+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_PREFIXID);
    			boolean prefixapplicable=false;
    			if(transferRulesVO!=null)
    			{
    				// if promotional transfer rule is applicable now check for valid prefixes here
    				prefixapplicable=validatePromotionPrefixes(p_transferVO.getTransferDateTime(),receiverVO.getPrefixID(),transferRulesVO.getAllowedDays(),transferRulesVO.getAllowedSeries(),transferRulesVO.getDeniedSeries());
    				if(!prefixapplicable)
    					break;
    				isPromotionalTrfRuleFound=true;
    				break;

    			}

    		case PretupsI.PROMO_TRF_RULE_LVL_CELLGRP_CODE:
    			String cellId = ((ChannelUserVO)p_transferVO.getSenderVO()).getCellID();
    			String cellGroup = new CellIdMgmtDAO().getCellGroupFromCellId(p_con, cellId);
    			if(!isServiceProviderPromoAllow)
    			{
    				transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+cellGroup+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_CELLGROUP);
    			}
    			else
    			{
    				transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+cellGroup+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_CELLGROUP+"_"+spGroup+"_"+receiverTransferItemVO.getAccountStatus());
    			}
    			if(transferRulesVO==null&&isServiceProviderPromoAllow)//condition mod by rahul for serv provdr grp preference
    			{

    				//spGroup = PretupsI.ALL;

    				cellId = ((ChannelUserVO)p_transferVO.getSenderVO()).getCellID();
    				cellGroup = new CellIdMgmtDAO().getCellGroupFromCellId(p_con, cellId);

    				transferRulesVO=getPromotionalTransferRule(p_transferVO.getServiceType(),p_transferVO.getModule(),p_transferVO.getReceiverNetworkCode(),cellGroup,receiverVO.getSubscriberType(),PretupsI.ALL,receiverVO.getServiceClassCode(),recAllServiceClassForPromotional,p_transferVO.getSubService(),PretupsI.PROMOTIONAL_LEVEL_CELLGROUP,spGroup,receiverTransferItemVO.getAccountStatus()," cell group level promotional transfer rule",transferRuleMap,p_transferVO.getTransferID(),p_transferVO.getNetworkCode());
    				if (LOG.isDebugEnabled())
    					LOG.debug(methodName,p_transferVO.getTransferID()," transferRulesVO 3 :"+transferRulesVO);
    				if (LOG.isDebugEnabled()) 	
    					LOG.debug(methodName,p_transferVO.getTransferID(),"Validation For Network Code="+p_transferVO.getNetworkCode()+" Sender Subscriber Type="+cellGroup+" Sender Service class="+PretupsI.ALL+"Receiver Subscriber Type="+receiverVO.getSubscriberType()+" Receiver Service Class ID="+recAllServiceClassForPromotional+" Sub Service="+p_transferVO.getSubService()+" cell group level promotional transfer rule");
    			}

    			if(transferRulesVO!=null)
    			{
    				/*isExist=isPromotionalRuleExistInRange(transferRulesVO,p_transferVO.getTransferDateTime());if(isExist)
							if(isExist)		
								isPromotionalTrfRuleFound=true;
							else*/
    				isPromotionalTrfRuleFound=true;
    				break;
    			}

    		case PretupsI.PROMO_TRF_RULE_LVL_SPNAME_CODE:
    			transferRulesVO=(TransferRulesVO)transferRuleMap.get(p_transferVO.getServiceType()+"_"+p_transferVO.getModule()+"_"+p_transferVO.getReceiverNetworkCode()+"_"+spGroup+"_"+receiverVO.getSubscriberType()+"_"+PretupsI.ALL+"_"+receiverVO.getServiceClassCode()+"_"+p_transferVO.getSubService()+"_"+PretupsI.PROMOTIONAL_LEVEL_SERVICEGROUP+"_"+spGroup+"_"+receiverTransferItemVO.getAccountStatus());

    			if(transferRulesVO!=null)
    			{
    				/*isExist=isPromotionalRuleExistInRange(transferRulesVO,p_transferVO.getTransferDateTime());if(isExist)
						if(isExist)		
							isPromotionalTrfRuleFound=true;
						else*/
    				isPromotionalTrfRuleFound=true;
    				break;
    			}
    		}
    	}
    	
    	if(!isPromotionalTrfRuleFound)
    	{
    		transferRulesVO = selectTransferRuleFromCache(p_con,p_transferVO,receiverVO);
    		if (LOG.isDebugEnabled()) {
    			LOG.debug(
    					methodName,
    					p_transferVO.getTransferID(),
    					"Validation For Network Code=" + p_transferVO.getNetworkCode() + " Sender Subscriber Type=" + ((ChannelUserVO) p_transferVO.getSenderVO())
    					.getDomainID() + " Sender Service class=" + PretupsI.ALL + "Receiver Subscriber Type=" + receiverVO.getSubscriberType() + " Receiver Service Class ID=" + receiverVO
    					.getServiceClassCode() + " Sub Service=" + p_transferVO.getSubService());
    		}
    		
    		if (transferRulesVO == null) {
    			CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
    			transferRulesVO = cardGroupSetDAO.loadDefaultCardGroup(p_con,p_transferVO);
    		}
    	}
    	if (LOG.isDebugEnabled()) 	LOG.debug(methodName,"Exiting" );
    	return transferRulesVO;
    }
    
    
    public static ListValueVO validateChannelUserForMNP(String p_msisdn) throws BTSLBaseException
   	{
   		 LOG.debug("validateChannelUserForMNP", "Entered with p_msisdn="+p_msisdn );
   		    final String METHOD_NAME = "validateChannelUserForMNP";
   		    ListValueVO listValueVO=null;
   			try
   			{
   				
   				listValueVO= _operatorUtil.validateMSISDNForMNP(p_msisdn);
   				return listValueVO;
   				
   			}
   			catch(BTSLBaseException be)
   			{
   				throw be;
   			}
   			catch(Exception e)
   			{
   				LOG.errorTrace(METHOD_NAME,e);
   				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateChannelUserForMNP]","",p_msisdn,"","Exception :"+e.getMessage());
   				throw new BTSLBaseException("PretupsBL","validateChannelUserForMNP",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
   			}
   			finally
   			{
   				LOG.debug("validateChannelUserForMNP","Exiting listValueVO="+listValueVO);
   			}
   		
   	}
	public static void addVoucherTransferDetails(Connection p_con,P2PTransferVO p_transferVO) throws BTSLBaseException
	{
		try
		{
			int updateCount=_transferDAO.addVoucherTransferDetails(p_con,p_transferVO);
			if(updateCount<=0)
				throw new BTSLBaseException("PretupsBL","addTransferDetails",PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[addTransferDetails]",p_transferVO.getTransferID(),"","","Exception :"+e.getMessage());
			throw new BTSLBaseException("PretupsBL","addTransferDetails",PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
		}
	}

    
    public static void loyaltyPointsDistribution(ChannelTransferVO channelTransferVo,Connection con){
    	 final String methodName="loyaltyPointsDistribution";
             final LoyaltyBL _loyaltyBL = new LoyaltyBL();
             final LoyaltyVO loyaltyVO = new LoyaltyVO();
             PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
             final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
             final ArrayList arr2 = new ArrayList();
             try {
             loyaltyVO.setModuleType(PretupsI.C2C_MODULE);
             loyaltyVO.setServiceType(PretupsI.C2C_MODULE);
             loyaltyVO.setTransferamt(channelTransferVo.getRequestedQuantity());
             loyaltyVO.setCategory(channelTransferVo.getCategoryCode());
             loyaltyVO.setFromuserId(channelTransferVo.getFromUserID());
             loyaltyVO.setTouserId(channelTransferVo.getToUserID());
             loyaltyVO.setNetworkCode(channelTransferVo.getNetworkCode());
             loyaltyVO.setTxnId(channelTransferVo.getTransferID());
             loyaltyVO.setCreatedOn(channelTransferVo.getCreatedOn());
             loyaltyVO.setSenderMsisdn(channelTransferVo.getFromUserCode());
             loyaltyVO.setReciverMsisdn(channelTransferVo.getToUserCode());
             loyaltyVO.setProductCode(channelTransferVo.getProductCode());
             arr2.add(loyaltyVO.getFromuserId());
             arr2.add(loyaltyVO.getTouserId());
             promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(con, arr2);
             loyaltyVO.setSetId(promotionDetailsVO.get_setId());
             loyaltyVO.setToSetId(promotionDetailsVO.get_toSetId());

             if (loyaltyVO.getSetId() == null && loyaltyVO.getToSetId() == null) {
            	 LOG.error("process", "Exception during LMS Module.SetId not found");
             } else {
                 _loyaltyBL.distributeLoyaltyPoints(PretupsI.C2C_MODULE, channelTransferVo.getTransferID(), loyaltyVO);
             }

         } catch (BTSLBaseException ex) {
        	 LOG.errorTrace(methodName, ex);
        	 LOG.error(methodName, "Exception durign LMS Module " + ex.getMessage());

         }
    }
    
	
	 /**
     * Validates the promo Bonus Amount
     * 
     * @param p_c2sTransferVO
     * @param p_bonusAmount
     * @throws BTSLBaseException
     */
    public static void validatePromoBonus(C2STransferVO p_c2sTransferVO, String p_bonusAmount) throws BTSLBaseException {
        final String methodName = "validatePromoBonus";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_c2sTransferVO.getRequestID(), "Entered p_bonusAmount=" + p_bonusAmount);
        }
		
        String[] strArr = null;
        double requestPromoBonus= 0;
        String msgRequestPromoBonus = null;
        if(BTSLUtil.isNullString(p_bonusAmount))
			p_bonusAmount="0";
			
		try {
           
            if (p_c2sTransferVO.getRequestGatewayType().equals(Constants.getProperty("GATEWAYTYPES_MIN_DENOMINATION_ALLOWED"))) {
                msgRequestPromoBonus = getDisplayAmount(Long.parseLong(p_bonusAmount));
            } else {
                msgRequestPromoBonus = p_bonusAmount;
            }
            try {
                requestPromoBonus = Double.parseDouble(p_bonusAmount);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                strArr = new String[] { msgRequestPromoBonus };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_PROMO_BONUS_NOTNUMERIC, 0, strArr, null);
            }
            if (requestPromoBonus < 0) {
                strArr = new String[] { msgRequestPromoBonus };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.CHNL_ERROR_PROMO_BONUS_LESSTHANZERO, 0, strArr, null);
            }
            if (p_c2sTransferVO.getRequestGatewayType().equals(Constants.getProperty("GATEWAYTYPES_MIN_DENOMINATION_ALLOWED"))) {
              
				p_c2sTransferVO.setPromoBonus(Long.parseLong(p_bonusAmount));
                
            } else {
                
                p_c2sTransferVO.setPromoBonus(getSystemAmount(p_bonusAmount));
            }
         

        } catch (BTSLBaseException be) {
           throw be ;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validatePromoBonus]", "", "", "",
                "Exception while validating amount" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_c2sTransferVO.getRequestID(), "Exiting ");
        }
    }

	/**
	 * @param amountAllowed
	 * @return string value
	 */
	public static String convertExponential(double amountAllowed) {
		
		return String.format("%.2f",amountAllowed);
	}
	
	
	/**
	 * @param p_c2sTransferVO
	 * @param quantity
	 * @throws BTSLBaseException
	 */
	public static void validateQuantity(C2STransferVO p_c2sTransferVO,String quantity) throws BTSLBaseException {
	    final String methodName = "validateQuantity";
	    StringBuilder loggerValue= new StringBuilder();
	    if (LOG.isDebugEnabled()) {
	    	loggerValue.setLength(0);
	    	loggerValue.append("Entered: quantity=");
	    	loggerValue.append(quantity);
	    	LOG.debug(methodName, loggerValue);
	    }
	    int onlineDvdLimit = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.ONLINE_DVD_LIMIT);
	    try {
	    	if(BTSLUtil.isNullString(quantity) || !BTSLUtil.isNumeric(quantity) || Long.parseLong(quantity) <= 0)
	    	{   
	    		String[] messageArgs = {quantity}; 
	    		throw new BTSLBaseException("PretupsBL","validateQuantity",PretupsErrorCodesI.ERROR_INVALID_DEFAULT_PRODUCT_QUANTITY,messageArgs);
	    	}
	    	else if(Long.parseLong(quantity) > onlineDvdLimit )
    		{
	    		String[] messageArgs = {String.valueOf(onlineDvdLimit)}; 
	    		throw new BTSLBaseException("PretupsBL","validateQuantity",PretupsErrorCodesI.MAX_REQUESTED_DVD_ERROR,messageArgs);
    		}

	    		p_c2sTransferVO.setVoucherQuantity(quantity);
	    } catch (BTSLBaseException be) {
	        throw be;
	    } catch (Exception e) {
	    	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
	        LOG.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateQuantity]",
	        		quantity, "", "", loggerValue.toString());
	        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
	    } finally {
	    	if (LOG.isDebugEnabled()) {
	         	loggerValue.setLength(0);
	         	loggerValue.append("Exiting: quantity:");
	         	loggerValue.append(quantity);
	         	LOG.debug(methodName, loggerValue);
	         }
	    }
	}



	/**
	 * 
	 * @param con
	 * @param voucherType
	 * @throws BTSLBaseException
	 */
	public static void validateVoucherType(Connection con, String voucherType) throws BTSLBaseException {
	    final String methodName = "validateVoucherType";
	    StringBuilder loggerValue= new StringBuilder();
	    String voucherDesc;
	    VomsProductDAO vomsProductDAO;
	    ArrayList<VoucherTypeVO> voucherTypeList;
	    if (LOG.isDebugEnabled()) {
	    	loggerValue.setLength(0);
	    	loggerValue.append("Entered: voucherType=");
	    	loggerValue.append(voucherType);
	    	LOG.debug(methodName, loggerValue);
	    }
	    try {
	    	vomsProductDAO = new VomsProductDAO();
	    	voucherTypeList = vomsProductDAO.loadDigitalVoucherDetails(con);
	    	if(BTSLUtil.isNullString(voucherType) || !BTSLUtil.isAlphaNumericWithUnderscore(voucherType) ){
    			throw new BTSLBaseException("PretupsBL","validateVoucher",PretupsErrorCodesI.VOUCHER_TYPE_INVALID);
    		}
	    	if(BTSLUtil.isNullOrEmptyList(voucherTypeList)){
	            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.VOUCHER_TYPE_DOESNOT_EXIST);
	    	}
			voucherDesc = BTSLUtil.getVoucherTypeDesc(voucherTypeList, voucherType);
			if (BTSLUtil.isNullString(voucherDesc)){
		            throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.VOUCHER_TYPE_DOESNOT_EXIST);
		    }
	    } catch (BTSLBaseException be) {
	        throw be;
	    } catch (Exception e) {
	    	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
	        LOG.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateVoucher]",
	        		"", "", "", loggerValue.toString());
	        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
	    } finally {
	    	if (LOG.isDebugEnabled()) {
	         	loggerValue.setLength(0);
	         	loggerValue.append("Entered: voucherType=");
		    	loggerValue.append(voucherType);
		    	LOG.debug(methodName, loggerValue);
	         }
	    }
	}
	
	
	/**
	 * @param con
	 * @param p_c2sTransferVO
	 * @param voucherProfile
	 * @throws BTSLBaseException
	 */
	public static void validateVoucherProfile(Connection con,C2STransferVO p_c2sTransferVO,String voucherProfile) throws BTSLBaseException {
	    final String methodName = "validateVoucher";
	    StringBuilder loggerValue= new StringBuilder();
	    if (LOG.isDebugEnabled()) {
	    	loggerValue.setLength(0);
	    	loggerValue.append("Entered: voucherProfile=");
	    	loggerValue.append(voucherProfile);
	    	LOG.debug(methodName, loggerValue);
	    }
	    try {
	    	String productName = null;
	    	if("0".equals(voucherProfile)){
	    		voucherProfile = "";
			}
	    	 if (!BTSLUtil.isNullString(voucherProfile)) {
		    		VomsProductDAO vomsProductDAO = new VomsProductDAO();
		    		productName = vomsProductDAO.getProductName(con, voucherProfile);
		    		if(BTSLUtil.isNullString(productName))
						throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.VOUCHER_PRODUCT_INVALID);
	            }
	    	p_c2sTransferVO.setProductId(voucherProfile);
	    } catch (BTSLBaseException be) {
	        throw be;
	    } catch (Exception e) {
	    	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
	        LOG.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateVoucherProfile]",
	        		"", "", "", loggerValue.toString());
	        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
	    } finally {
	    	if (LOG.isDebugEnabled()) {
	         	loggerValue.setLength(0);
		    	loggerValue.append("Entered: voucherProfile=");
		    	loggerValue.append(voucherProfile);
		    	LOG.debug(methodName, loggerValue);
	         }
	    }
	}
	
	/**
	 * @param con
	 * @param p_c2sTransferVO
	 * @throws BTSLBaseException
	 */
	public static void validateVoucher(Connection con,C2STransferVO p_c2sTransferVO) throws BTSLBaseException {
	    final String methodName = "validateVoucher";
	    StringBuilder loggerValue= new StringBuilder();
	    if (LOG.isDebugEnabled()) {
	    	loggerValue.setLength(0);
	    	loggerValue.append("Entered: C2STransferVO=");
	    	loggerValue.append(p_c2sTransferVO);
	    	LOG.debug(methodName, loggerValue);
	    }
	    try {
	    	VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
	    	long vomsVoucherCount = vomsVoucherDAO.loadVomsVoucherDetails(con, p_c2sTransferVO);
	    	if(vomsVoucherCount <= 0 ){
    			throw new BTSLBaseException("PretupsBL",methodName,"cardgroup.cardgroupc2sdetails.modify.error.voucher.invalidcombination");
    		}
	    } catch (BTSLBaseException be) {
	        throw be;
	    } catch (Exception e) {
	    	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
	        LOG.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateVoucher]",
	        		"", "", "", loggerValue.toString());
	        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
	    } finally {
	    	if (LOG.isDebugEnabled()) {
	    		loggerValue.setLength(0);
		    	loggerValue.append("Exited: C2STransferVO=");
		    	loggerValue.append(p_c2sTransferVO);
		    	LOG.debug(methodName, loggerValue);
	         }
	    }
	}
	
	/**
	 * @param con
	 * @param p_c2sTransferVO
	 * @throws BTSLBaseException
	 */
	public static void validateVoucherDenomination(Connection con,C2STransferVO p_c2sTransferVO,String p_requestAmount) throws BTSLBaseException {
	    final String methodName = "validateVoucherDenomination";
	    StringBuilder loggerValue= new StringBuilder();
	    if (LOG.isDebugEnabled()) {
	    	loggerValue.setLength(0);
	    	loggerValue.append("Entered: C2STransferVO=");
	    	loggerValue.append(p_c2sTransferVO);
	    	LOG.debug(methodName, loggerValue);
	    }
	    try {
	    	VomsProductDAO vomsProductDAO = new VomsProductDAO();
	        ArrayList<VomsCategoryVO> denominationList = vomsProductDAO.getMrpList(con, p_c2sTransferVO.getVoucherType(),  p_c2sTransferVO.getNetworkCode(),p_c2sTransferVO.getVoucherSegment()); 
	        if (BTSLUtil.isNullOrEmptyList(denominationList)) {
                if (LOG.isDebugEnabled()) {
                	LOG.debug(methodName, "No MRP found");
                }
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MRP_DOESNOT_EXIST);
            }
	        for(VomsCategoryVO vomsCategoryVO : denominationList){
	        	if(BTSLUtil.floatEqualityCheck(Double.parseDouble(p_requestAmount), vomsCategoryVO.getMrp(), "==")){
	        		  p_c2sTransferVO.setTransferValue(getSystemAmount(p_requestAmount));
	                  p_c2sTransferVO.setRequestedAmount(getSystemAmount(p_requestAmount));
	        	}
	        	
	        }
	    } catch (BTSLBaseException be) {
	        throw be;
	    } catch (Exception e) {
	    	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
	        LOG.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateVoucher]",
	        		"", "", "", loggerValue.toString());
	        throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION,e);
	    } finally {
	    	if (LOG.isDebugEnabled()) {
	    		loggerValue.setLength(0);
		    	loggerValue.append("Entered: C2STransferVO=");
		    	loggerValue.append(p_c2sTransferVO);
		    	LOG.debug(methodName, loggerValue);
	         }
	    }
	}

	public static boolean validateVoucherSegment(String voucherSegment) throws BTSLBaseException {
		final String methodName = "validateVoucherSegment";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: voucherSegment = ");
        	loggerValue.append(voucherSegment);
        	LOG.debug(methodName, loggerValue);
        }
        
        boolean isVoucherSegmentValid = false;
        try {
        	if(BTSLUtil.isNullString(voucherSegment) || !BTSLUtil.isAlphaNumeric(voucherSegment) ){
    			throw new BTSLBaseException("PretupsBL","validateVoucher",PretupsErrorCodesI.EXTSYS_VOUCHER_SEGMENT_INVALID,voucherSegment);
    		}
	    	
	    	ArrayList segmentList = LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true);
	    	for(int i=0;i<segmentList.size();i++)
	    	{
	    		ListValueVO listValueVO = (ListValueVO) segmentList.get(i);
	    		if(voucherSegment.equals(listValueVO.getValue()))
	    			isVoucherSegmentValid = true;
	    	}
	    	
	    	if(!isVoucherSegmentValid)
	    		  throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.INVALID_SEGMENT);
        }
        catch (BTSLBaseException be) {
        	throw be;
        }
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append(EXCEPTION);
        	loggerValue.append(e.getMessage());
        	LOG.error(methodName, loggerValue);
        	LOG.errorTrace(methodName, e);
        	throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION,e);
        }
        
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting:");
        	loggerValue.append(isVoucherSegmentValid);
        	LOG.debug(methodName, loggerValue);
        }

        return isVoucherSegmentValid;
	}
	
	/**
	 * This method will check weather the passed voucher type is of electronics/digital type or not by validating TYPE in VOMS_TYPE 
	 * @param con
	 * @param voucherType
	 * @return
	 * @throws BTSLBaseException
	 *//*
	public static boolean isElectronicsOrDigitalType(Connection con, String voucherType) throws BTSLBaseException {
		final String methodName = "isElectronicsOrDigitalType";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: voucherType = ");
        	loggerValue.append(voucherType);
        	LOG.debug(methodName, loggerValue);
        }
		VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
		ArrayList<VoucherTypeVO> voucherTypeList;
		boolean isElectronicsOrDigitalType = false;
		voucherTypeList = vomsVoucherDAO.loadVomsTypeDetails(con);
		if(voucherTypeList !=null) {
			for(int i=0;i<voucherTypeList.size();i++) {
				VoucherTypeVO voucherTypeVO = (VoucherTypeVO) voucherTypeList.get(i);
	    		if(!BTSLUtil.isNullString(voucherType) && voucherTypeVO != null && 
	    				VOMSI.VOUCHER_TYPE_ELECTRONIC.equals(voucherTypeVO.getType()) &&
	    				voucherType.equals(voucherTypeVO.getVoucherType())) {
	    					isElectronicsOrDigitalType = true;
	    		}
	    	}
		}
		if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exited: isElectronicsOrDigitalType = ");
        	loggerValue.append(isElectronicsOrDigitalType);
        	LOG.debug(methodName, loggerValue);
        }
		return isElectronicsOrDigitalType;
	}*/
	
}
