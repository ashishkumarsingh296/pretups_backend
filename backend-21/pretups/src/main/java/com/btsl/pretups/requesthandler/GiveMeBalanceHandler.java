package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.GiveMeBalanceRequestResponseLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.p2p.subscriber.requesthandler.RegisterationController;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;


/**
 * * @(#)GiveMeBalanceHandler.java
 * Copyright(c) 1999-2009, Comviva Technologies Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Amit Singh July,31 2009 Initial Creation
 * 
 * This class handles the request for balance initiated by subscriber to the
 * other subscriber
 * registered as a P2P Subscriber in pretups system.
 */
public class GiveMeBalanceHandler implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(GiveMeBalanceHandler.class.getName());
    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private SenderVO _senderVO = null;
    private String _senderMsisdn = null;
    private String _receiverMsisdn = null;
    private String _amount = null;
    private SubscriberDAO subscriberDao = null;
    private P2PTransferVO p2pTransferVO = new P2PTransferVO();
    private Long amt ;
    private static int _prevMinut = 0;
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
    private static int _transactionIDCounter = 0;
  
    

    /**
     * This is the entry point and only public method of the class.The process
     * involved in the Give me balance request
     * for a subscriber are called from this method.
     * 
     * @param p_requestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered....: p_requestVO= " + p_requestVO);
        }

        _requestVO = p_requestVO;
        Connection con = null;
        MComConnectionI mcomCon = null;
        boolean messageRequired = false;
        try {
            // retreiving the Map which has all the values in the request.These
            // values can be retreived by
            // using the tag name
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            _requestMap = _requestVO.getRequestMap();
			if(PretupsI.GATEWAY_TYPE_SMSC.equalsIgnoreCase(_requestVO.getRequestGatewayType()))
			{
				if(_requestMap ==null)
					_requestMap = new HashMap();
				if(_log.isDebugEnabled()){
					_log.debug(METHOD_NAME,"_requestVO.getRequestMessageArray()= "+_requestVO.getRequestMessageArray()+", _requestVO.getFilteredMSISDN()="+_requestVO.getFilteredMSISDN());
				}
				_requestMap.put("MSISDN1", _requestVO.getFilteredMSISDN());
				_requestMap.put("MSISDN2", _requestVO.getRequestMessageArray()[1]);
				_requestMap.put("AMOUNT", _requestVO.getRequestMessageArray()[2]);
				_requestVO.setRequestMap(_requestMap);
			}
            _senderMsisdn = (String) _requestMap.get("MSISDN1");
            _receiverMsisdn = (String) _requestMap.get("MSISDN2");
            _amount = (String) _requestMap.get("AMOUNT");
            
            
            Pattern p = Pattern.compile("[^0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(_amount);
           if( m.find()) {
           	p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_AMOUNT);
           	throw new BTSLBaseException("GiveMeBalanceHandler", METHOD_NAME, PretupsErrorCodesI.INVALID_AMOUNT);
           }
            
            
            // GiveMeBalanceRequestResponseLog to log request
            GiveMeBalanceRequestResponseLog.log(_requestVO);
            // retreiving senderVO from requestMap for the validation
            _senderVO = (SenderVO) _requestVO.getSenderVO();
            if (_senderVO != null) {
                messageRequired = true;
            }
            amt = Long.parseLong(_amount);
            
            p_requestVO.setAmount1(amt*((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
            p2pTransferVO.setRequestedAmount(amt*((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
            p2pTransferVO.setTransferID("GMB");
            p2pTransferVO.setTransferValue(amt*((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
            p2pTransferVO.setServiceType(p_requestVO.getServiceType());
   
            // validates the parameter passed in the request eg. type
            subscriberDao = new SubscriberDAO();
            _senderVO = subscriberDao.loadSubscriberDetailsByMsisdn(con, _senderMsisdn, PretupsI.SERVICE_TYPE_GIVE_ME_BALANCE);
            validate();
            
            if (_senderVO == null) {
            	
            	p_requestVO.setFilteredMSISDN(_senderMsisdn);
                new RegisterationController().regsiterNewUser(p_requestVO);
                _senderVO = (SenderVO) p_requestVO.getSenderVO();
                _senderVO.setDefUserRegistration(true);
                p_requestVO.setSenderLocale(new Locale(_senderVO.getLanguage(), _senderVO.getCountry()));
                messageRequired = true;
            }
            if(_senderVO.getLastTransferOn()!=null)
              p2pTransferVO.setTransferDate(_senderVO.getLastTransferOn());
            else{
            	p2pTransferVO.setTransferDate(new java.util.Date());
            }
            
            
            // setting the transaction status to true
          p2pTransferVO.setSenderID(_senderVO.getUserID());
            SubscriberBL.increaseTransferOutCountsForGMB(con,  p2pTransferVO, PretupsI.SERVICE_TYPE_GIVE_ME_BALANCE);
            
            _requestVO.setSuccessTxn(true);
			if(_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME,"messageRequired = "+messageRequired);
			}
            if (messageRequired) {
                String keySender = PretupsErrorCodesI.GMB_PLAIN_SMS_SUCESS_S;
                String keyReceiver = PretupsErrorCodesI.GMB_PLAIN_SMS_SUCESS_R;
                String[] arrMsg = {  _receiverMsisdn,_senderMsisdn, _amount };
                Locale locale =null;
                if(_requestVO.getReceiverLocale()==null){
                	 locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));	
                }else{
                	locale= _requestVO.getReceiverLocale();
                }
                
               
                PushMessage pushMessage = null;
                String receiverMessage = BTSLUtil.getMessage(locale, keyReceiver, arrMsg);
                pushMessage = new PushMessage(_receiverMsisdn, receiverMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), locale);
                pushMessage.push();
                if(_requestVO.getSenderLocale()==null){
               	 locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));	
               }else{
               	locale= _requestVO.getSenderLocale();
               }
                if (!BTSLUtil.isNullString(p_requestVO.getRequestGatewayType()) && "USSD".equals(p_requestVO.getRequestGatewayType())) {
                	 PushMessage pushMessage1 = null;
                     String senderMessage = BTSLUtil.getMessage(locale, keySender, arrMsg);
                     pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), locale);
                     pushMessage1.push();
                }
                _requestVO.setMessageCode(PretupsErrorCodesI.GMB_PLAIN_SMS_SUCESS_S);
                _requestVO.setMessageArguments(arrMsg);

            }
            GiveMeBalanceRequestResponseLog.log(_requestVO, "validation successful", (String) _requestMap.get("MSISDN1"), (String) _requestMap.get("MSISDN2"), (String) _requestMap.get("AMOUNT"));
           mcomCon.finalCommit();
        } catch (BTSLBaseException be) {
            _requestVO.setSuccessTxn(false);
            // GiveMeBalanceRequestResponseLog logger to check response after
            // validation
            GiveMeBalanceRequestResponseLog.log(_requestVO, "validation failed", (String) _requestMap.get("MSISDN1"), (String) _requestMap.get("MSISDN2"), (String) _requestMap.get("AMOUNT"));
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            PushMessage pushMessage1 = null;
            Object serviceObjVal = null;
             if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_DAY_EXCEEDED_R_PRE)){
            	serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.DAILY_MAX_TRFR_NUM_CODE, _senderVO.getNetworkCode(), _senderVO.getServiceType());
            	String[] arrMsg={PretupsBL.getDisplayAmount(_senderVO.getDailyTransferAmount()),PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()),amt.toString()};
            	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.P2P_SNDR_DAY_MAX_AMTTRANS_THRESHOLD ,arrMsg);
                pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, null,null, locale);
                pushMessage1.push();
            } else if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R_PRE)){
            	serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE, _senderVO.getNetworkCode(), _senderVO.getServiceType());
            	String[] arrMsg={PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()),amt.toString()};
            	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.AMOUNT_TRANSFERS_DAY_EXCEEDED_R_PRE ,arrMsg);
                pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, null,null, locale);
                pushMessage1.push();
            }
            else if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.AMOUNT_TRANSFERS_WEEK_EXCEEDED_R_PRE)){
            	serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.WEEKLY_MAX_TRFR_AMOUNT_CODE, _senderVO.getNetworkCode(), _senderVO.getServiceType());
            	String[] arrMsg={PretupsBL.getDisplayAmount(_senderVO.getWeeklyTransferAmount()),PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()),amt.toString()};
            	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.P2P_SNDR_WEEK_MAX_AMTTRANS_THRESHOLD ,arrMsg);
                pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, null,null, locale);
                pushMessage1.push();
            } else if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_WEEK_EXCEEDED_R_PRE)){
            	serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.WEEKLY_MAX_TRFR_NUM_CODE, _senderVO.getNetworkCode(), _senderVO.getServiceType());
            	String[] arrMsg={null,serviceObjVal.toString()};
            	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.P2P_SNDR_WEEK_MAX_TRANS_THRESHOLD ,arrMsg);
                pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, null,null, locale);
                pushMessage1.push();
            } else if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.AMOUNT_TRANSFERS_MONTH_EXCEEDED_S)){
            	serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE, _senderVO.getNetworkCode(), _senderVO.getServiceType());
            	String[] arrMsg={PretupsBL.getDisplayAmount(_senderVO.getMonthlyTransferAmount()),PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()),amt.toString()};
            	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.P2P_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD ,arrMsg);
                pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, null,null, locale);
                pushMessage1.push();
            } else if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.NO_SUCCESS_TRANSFERS_MONTH_EXCEEDED_R_PRE)){
            	serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.MONTHLY_MAX_TRFR_NUM_CODE, _senderVO.getNetworkCode(), _senderVO.getServiceType());
            	String[] arrMsg={null,serviceObjVal.toString()};
            	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.P2P_SNDR_MONTH_MAX_TRANS_THRESHOLD ,arrMsg);
                pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, null,null, locale);
                pushMessage1.push();
            }
            
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            if (!BTSLUtil.isNullString(p_requestVO.getRequestGatewayType()) && "USSD".equals(p_requestVO.getRequestGatewayType())) {
            final String senderMessage = BTSLUtil.getMessage(_requestVO.getLocale(), _requestVO.getMessageCode(), _requestVO.getMessageArguments());
            PushMessage pushMessage = null;
            pushMessage = new PushMessage(_requestVO.getFilteredMSISDN(), senderMessage, _requestVO.getRequestIDStr(), _requestVO.getRequestGatewayCode(), _requestVO
                .getLocale());
            pushMessage.push();
            }
            // Rollbacking the transaction
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        }// end of BTSLBaseException
        catch (Exception ex) {
            _requestVO.setSuccessTxn(false);
            // Rollbacking the transaction
            try {
                if (con != null) {
                   mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GiveMeBalanceHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            p_requestVO = _requestVO;
            p_requestVO.setRequestMap(_requestMap);
            _requestMap = null;
            _requestVO = null;
            _senderVO = null;
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("GiveMeBalanceHandler#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }// end of finally
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Exiting ....: ");
        }
    }

    /**
     * This methods gets the msisdn from the requestMap and validates it. It
     * checks whether the msisdn is not null.
     * Also it checks whether the filteredMsisdn is from the network passed in
     * the request and the amount is validated for
     * numeric
     * 
     * @throws BTSLBaseException
     */
    private void validate() throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered  ");
        }
        Object serviceObjVal = null;
        String msisdnPrefix = null;
        String filteredMsisdn = null;
        Connection p_con = null;MComConnectionI p_mcomCon = null;
        BarredUserDAO barredUserDao = null;
        int isUserBarredExists =0;
        try {
            // validating the initiator msisdn
            String msisdn = (String) _requestMap.get("MSISDN1");

            // filtering the msisdn for country independent dial format
            filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                _requestMap.put("RES_ERR_KEY", "MSISDN1");
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.P2PGMB_ERROR_INVALID_MSISDN1);
            }
            msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
            _requestVO.setFilteredMSISDN(filteredMsisdn);

            // checking whether the msisdn prefix is valid in the network
            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                String[] arr = new String[] { filteredMsisdn };
                throw new BTSLBaseException("GiveMeBalanceHandler", "validate", PretupsErrorCodesI.P2PGMB_ERROR_UNSUPPORTED_NETWORK_MSISDN1, arr);
            }

            // validating the donor msisdn
            msisdn = (String) _requestMap.get("MSISDN2");
            filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                _requestMap.put("RES_ERR_KEY", "MSISDN2");
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.P2PGMB_ERROR_INVALID_MSISDN2);
            }
            msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
            // checking whether the msisdn prefix is valid in the network
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                String[] arr = new String[] { filteredMsisdn };
                throw new BTSLBaseException("GiveMeBalanceHandler", "validate", PretupsErrorCodesI.P2PGMB_ERROR_UNSUPPORTED_NETWORK_MSISDN2, arr);
            }
            // amount validation
            String amount = (String) _requestMap.get("AMOUNT");
            if (!BTSLUtil.isNumeric(amount)) {
                _requestMap.put("RES_ERR_KEY", "AMOUNT");
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.P2PGMB_ERROR_AMOUNT_NOT_NUMERIC);
            }
            // check whether amount requested is greater than PEERTRFMINLMT(p2p
            // min limit) and lesser than PEERTRFMAXLMT(p2p max limit)
           
            // Check if user has been barred 
            barredUserDao = new BarredUserDAO();
            String  donorMsisdn = (String) _requestMap.get("MSISDN2");
            String  intiaterMsisdn = (String) _requestMap.get("MSISDN1");
            p_mcomCon = new MComConnection();p_con=p_mcomCon.getConnection();
             isUserBarredExists = barredUserDao.barredDataExistsForGMB(p_con, intiaterMsisdn, donorMsisdn, PretupsI.SERVICE_TYPE_BAR_GIVE_ME_BALANCE);
            if(isUserBarredExists==1){
            	throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.P2PGMB_ERROR_USER_BARRED);
            }
            else if(isUserBarredExists==2){
            	throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.P2PGMB_MSISDN1_MSISDN2_EQUAL);
            }
        } catch (BTSLBaseException be) {
            if (_senderVO != null) {
            	Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                PushMessage pushMessage1 = null;
                if (be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_SNDR_MAX_TRANS_AMT_MORE)) {
                    serviceObjVal = PretupsBL.getServiceClassObject(_senderVO.getServiceClassCode(), PreferenceI.P2P_MAXTRNSFR_AMOUNT, _senderVO.getNetworkCode(), PretupsI.P2P_MODULE, true, PretupsI.ALL);
                    _requestMap.put("RES_ERR_KEY", PretupsErrorCodesI.P2P_SNDR_MAX_TRANS_AMT_MORE + ":" + (String) (_requestMap.get("AMOUNT")) + ":" + PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()));
                } else if (be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_SNDR_MIN_TRANS_AMT_LESS)) {
                    serviceObjVal = PretupsBL.getServiceClassObject(_senderVO.getServiceClassCode(), PreferenceI.P2P_MINTRNSFR_AMOUNT, _senderVO.getNetworkCode(), PretupsI.P2P_MODULE, true, PretupsI.ALL);
                    _requestMap.put("RES_ERR_KEY", PretupsErrorCodesI.P2P_SNDR_MIN_TRANS_AMT_LESS + ":" + (String) (_requestMap.get("AMOUNT")) + ":" + PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()));
                } else if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_SNDR_DAY_MAX_AMTTRANS_THRESHOLD)||(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_POST_SNDR_DAY_MAX_AMTTRANS_THRESHOLD))){
                	serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE, _senderVO.getNetworkCode(), _senderVO.getServiceType());
                	String[] arrMsg={PretupsBL.getDisplayAmount(_senderVO.getDailyTransferAmount()),PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()),amt.toString()};
                	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.P2P_SNDR_DAY_MAX_AMTTRANS_THRESHOLD ,arrMsg);
                    pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, null,null, locale);
                    pushMessage1.push();
                } else if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_SNDR_DAY_MAX_TRANS_THRESHOLD)||(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_POST_SNDR_DAY_MAX_TRANS_THRESHOLD))){
                	serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.DAILY_MAX_TRFR_NUM_CODE, _senderVO.getNetworkCode(), _senderVO.getServiceType());
                	String[] arrMsg={null,serviceObjVal.toString()};
                	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.P2P_SNDR_DAY_MAX_TRANS_THRESHOLD ,arrMsg);
                    pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, null,null, locale);
                    pushMessage1.push();
                } else if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_SNDR_WEEK_MAX_AMTTRANS_THRESHOLD)||(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_POST_SNDR_WEEK_MAX_AMTTRANS_THRESHOLD))){	
                	serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.WEEKLY_MAX_TRFR_AMOUNT_CODE, _senderVO.getNetworkCode(), _senderVO.getServiceType());
                	String[] arrMsg={PretupsBL.getDisplayAmount(_senderVO.getWeeklyTransferAmount()),PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()),amt.toString()};
                	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.P2P_SNDR_WEEK_MAX_AMTTRANS_THRESHOLD ,arrMsg);
                    pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, null,null, locale);
                    pushMessage1.push();
                } else if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD)||(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_POST_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD))){	
                  	serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE, _senderVO.getNetworkCode(), _senderVO.getServiceType());
                  	String[] arrMsg={PretupsBL.getDisplayAmount(_senderVO.getMonthlyTransferAmount()),PretupsBL.getDisplayAmount(((Long) serviceObjVal).longValue()),amt.toString()};
                	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.P2P_SNDR_MONTH_MAX_AMTTRANS_THRESHOLD ,arrMsg);
                    pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, null,null, locale);
                    pushMessage1.push();
                } else if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_SNDR_WEEK_MAX_TRANS_THRESHOLD)||(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_POST_SNDR_WEEK_MAX_TRANS_THRESHOLD))){	
                	serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.WEEKLY_MAX_TRFR_NUM_CODE, _senderVO.getNetworkCode(), _senderVO.getServiceType());
                	String[] arrMsg={null,serviceObjVal.toString()};
                	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.P2P_SNDR_WEEK_MAX_TRANS_THRESHOLD ,arrMsg);
                    pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, null,null, locale);
                    pushMessage1.push();
                } else if(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_SNDR_MONTH_MAX_TRANS_THRESHOLD)||(be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.P2P_POST_SNDR_MONTH_MAX_TRANS_THRESHOLD))){	                	
                	serviceObjVal = PreferenceCache.getControlPreference(PreferenceI.MONTHLY_MAX_TRFR_NUM_CODE, _senderVO.getNetworkCode(), _senderVO.getServiceType());
                	String[] arrMsg={null,serviceObjVal.toString()};
                	String senderMessage = BTSLUtil.getMessage(locale,PretupsErrorCodesI.P2P_SNDR_MONTH_MAX_TRANS_THRESHOLD ,arrMsg);
                    pushMessage1 = new PushMessage(_senderMsisdn, senderMessage, null,null, locale);
                    pushMessage1.push();
                }
                else{
                    _requestMap.put("RES_ERR_KEY_ARG", be.getArgs());
                    _requestMap.put("RES_ERR_KEY", be.getMessage() + ":" + (String) (_requestMap.get("AMOUNT")) + ":");
                }
            }
            _log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GiveMeBalanceHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("GiveMeBalanceHandler", "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
			if (p_mcomCon != null) {
				p_mcomCon.close("GiveMeBalanceHandler#validate");
				p_mcomCon = null;
			}
            p_con = null;
            if (_log.isDebugEnabled()) {
                _log.debug("validate", "Exiting ");
            }
        }
    }
   
}
