package com.btsl.pretups.user.requesthandler;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
/**
 * @(#)ChannelSOSSettlementHandler.java
 * @author satakshi.gaur
 *
 * 
 */
public class ChannelSOSSettlementHandler implements ServiceKeywordControllerI {
    private final Log log = LogFactory.getLog(ChannelSOSSettlementHandler.class.getName());
    private UserTransferCountsVO userTransferCountsVO = null;
    private UserDAO userDAO = new UserDAO();
    private UserTransferCountsDAO userTrfCountsDAO = new UserTransferCountsDAO();
    private ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();
    private String receiverMsisdn = null;
    private ChannelUserVO senderChannelUserVO;
    private String senderUserID;
    private String receiverUserID;
    private UserVO receiverUserVO = null;
    @Override
	public void process(RequestVO requestVO) {
        final String methodName = "process";
        LogFactory.printLog(methodName, " Entered p_requestVO=" + requestVO, log);
        Connection con = null;
        MComConnectionI mcomCon = null;
        
        try {
            senderChannelUserVO = (ChannelUserVO) requestVO.getSenderVO();
            senderUserID = senderChannelUserVO.getUserID();
            final String messageArr[] = requestVO.getRequestMessageArray();
            LogFactory.printLog(methodName, " Message Array " + messageArr, log);
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            validateRequestMessage(requestVO, messageArr, con);
            validateSOSFeature(senderChannelUserVO.getNetworkID());
            if(!BTSLUtil.isValidMSISDN(receiverMsisdn))
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SOS_INVALID_MSISDN);
            receiverUserVO = userDAO.loadUsersDetails(con,receiverMsisdn);
          	if(receiverUserVO==null){
          		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SOS_MSISDN_DETAILS_NOT_FOUND);
          	}
            receiverUserID = receiverUserVO.getUserID();
          	int updateCount = 0;
          	updateCount = processRequest(receiverUserID, senderUserID, con, senderChannelUserVO.getNetworkID() );
            
            if(updateCount > 0){
               	mcomCon.finalCommit();
                 // call the local method for the message formating
                 this.formatForSMSForSelf(receiverMsisdn, requestVO, userTransferCountsVO.getLastSOSTxnID());
             }
             else{
               	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SOS_CHANNEL_SETTLEMENT_FAILURE);
             }
            
        }catch (BTSLBaseException be) {
            requestVO.setSuccessTxn(false);
            if (con != null)
				try {
					mcomCon.finalRollback();
				} catch (SQLException e) {
					log.errorTrace(methodName, e);
				}
            log.error(methodName, " BTSLBaseException " + be.getMessage());
            log.errorTrace(methodName, be);
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } 
        catch (Exception e) {
            requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                log.errorTrace(methodName, ee);
            }
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
            		ChannelSOSSettlementHandler.class.getName(),"", "", "","Exception:" + e.getMessage());
            requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("ChannelSOSSettlementHandler#process");
        		mcomCon=null;
        		}
            LogFactory.printLog(methodName, "Exited ", log);
        }
    }

	private int processRequest(String receiverUserID, String senderUserID, Connection con, String networkCode) throws BTSLBaseException {
		int updateCount;
		userTransferCountsVO = userTrfCountsDAO.selectLastSOSTxnID(receiverUserID, con, true,null);
        if(userTransferCountsVO==null)
        	throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.SOS_NO_PENDING_TXN_TO_SETTLE);
       
        updateCount = channelTrfDAO.sosUpdateChannelTransfer(con, userTransferCountsVO.getLastSOSTxnID(), senderUserID, receiverUserID, PretupsI.SOS_MANUAL_SETTLED_STATUS, networkCode );
        if(updateCount>0)
        	 updateCount = userTrfCountsDAO.updateLastSOSTxnStatus(receiverUserID, con, PretupsI.SOS_MANUAL_SETTLED_STATUS );
        else
        	throw new BTSLBaseException(this, "processRequest", PretupsErrorCodesI.SOS_NO_TXN_FOUND_OR_NOT_AUTHORIZED);
        return updateCount;
	}

	private void validateRequestMessage(RequestVO requestVO, String[] messageArr, Connection con) throws BTSLBaseException {
		if (!BTSLUtil.isNullString(messageArr[0])) {
            if (messageArr.length < 3 || messageArr.length > 5) {
                throw new BTSLBaseException(this, "validateRequestMessage", PretupsErrorCodesI.C2S_ERROR_SOS_SETTLE_INVALIDMESSAGEFORMAT, 0, 
                		new String[]{ requestVO.getActualMessageFormat() }, null);
            }
            else if(messageArr.length == 3){
            	receiverMsisdn = messageArr[1];
           	 	requestVO.setReceiverMsisdn(receiverMsisdn);
           	 	requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
           	 	requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
           	 	validatePIN(con, senderChannelUserVO, messageArr[2]);
            }
            else if(messageArr.length == 4){
            	 receiverMsisdn = messageArr[1];
            	 requestVO.setReceiverMsisdn(receiverMsisdn);
            	 if (BTSLUtil.isNullString(messageArr[2])) {
                     requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                 } else {
                     final int langCode = PretupsBL.getLocaleValueFromCode(requestVO, messageArr[2]);
                     if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                         throw new BTSLBaseException(this, "validateRequestMessage", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                     }
                     requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                 }
            	 requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
            	 validatePIN(con, senderChannelUserVO, messageArr[3]);
            }
            else {
           	 receiverMsisdn = messageArr[1];
           	 requestVO.setReceiverMsisdn(receiverMsisdn);
           	validatePIN(con, senderChannelUserVO, messageArr[4]);
           }
        }
	}

	private void validatePIN(Connection con, ChannelUserVO channelUserVO, String pin) throws BTSLBaseException {
		try {
			BTSLUtil.validatePIN(pin);
            ChannelUserBL.validatePIN(con, channelUserVO, pin);
        } 
		catch (BTSLBaseException be) {
            log.errorTrace("validatePIN", be);
            log.error(" validatePIN", "BTSLBaseException " + be);
            throw new BTSLBaseException(this, " validatePIN", PretupsErrorCodesI.ERROR_INVALID_PIN);
        }		
	}

	private void validateSOSFeature(String networkCode) throws BTSLBaseException {
		if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue())  {
        	throw new BTSLBaseException(this, "validateSOSFeature ", PretupsErrorCodesI.SOS_NOT_ENABLE);
        	
        } 
        if (PretupsI.SOS_SETTLEMENT_TYPE_AUTO.equalsIgnoreCase(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_SETTLEMENT_TYPE)))) {
        	throw new BTSLBaseException(this, " validateSOSFeature", PretupsErrorCodesI.SOS_MANUAL_SETTLEMENT_NOT_ALLOWED);
        }
    	if(PretupsI.SOS_NETWORK.equalsIgnoreCase((String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, networkCode))){
    		throw new BTSLBaseException(this, "validateSOSFeature", PretupsErrorCodesI.SOS_INCORRECT_WALLET);
    	} 		
	}

	/**
	 * @param receiverMsisdn
	 * @param requestVO
	 * @param lastSOSTxnID
	 * @param senderChannelUserVO
	 * @throws BTSLBaseException
	 */
	private void formatForSMSForSelf(String receiverMsisdn, RequestVO requestVO, String lastSOSTxnID)
			throws BTSLBaseException {
        final String methodName = "formatForSMSForSelf";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: settlement for msisdn :" + receiverMsisdn + " , p_requestVO = " + requestVO.toString());
        }
        try {
            final String[] productArr = new String[3];
            productArr[0] = lastSOSTxnID;
            productArr[1] = receiverMsisdn;
            requestVO.setMessageArguments(productArr);
            requestVO.setMessageCode(PretupsErrorCodesI.SOS_CHANNEL_SETTLEMENT_SUCCESS);
            requestVO.setSenderReturnMessage(BTSLUtil.getMessage(requestVO.getSenderLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments()));

            sendMessageToSender(requestVO);
            sendMessageToReceiver(requestVO,lastSOSTxnID);
        } catch (Exception e) {
            log.error(" formatForSMSForSelf", "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "ChannelSOSSettlementHandler[formatForSMSForSelf]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ChannelSOSSettlementHandler", "formatForSMSForSelf", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("formatForSMSForSelf ", "Exited: ");
            }
        }
    }

	private void sendMessageToSender(RequestVO requestVO){
        final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), requestVO.getSenderReturnMessage(), 
        		requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(), requestVO.getSenderLocale());
        if(requestVO.getRequestGatewayCode().equalsIgnoreCase(PretupsI.GATEWAY_TYPE_SMSC))
            pushMessage.push();
            
	}
    private void sendMessageToReceiver(RequestVO requestVO, String lastSOSTxnID){
    	final String[] productArr = new String[3];
        productArr[0] = lastSOSTxnID;
        productArr[1] = requestVO.getRequestMSISDN();
        requestVO.setMessageArguments(productArr);
        requestVO.setMessageCode(PretupsErrorCodesI.SOS_CHANNEL_SETTLEMENT_SUCCESS);
        requestVO.setRecmsg(BTSLUtil.getMessage(requestVO.getReceiverLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments()));
        final PushMessage pushMessage = new PushMessage(requestVO.getReceiverMsisdn(), requestVO.getRecmsg(), requestVO.getRequestIDStr(), requestVO
            .getRequestGatewayCode(), requestVO.getReceiverLocale());
        pushMessage.push();
    }

    /**
     * @param con
     * @param userID
     * @return
     * @throws BTSLBaseException
     */
    public boolean validateSOSPending(Connection con, String userID) throws BTSLBaseException{
        	UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
        	UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
        	userTrfCntVO = userTrfCntDAO.selectLastSOSTxnID(userID, con, false, null);
        	if (userTrfCntVO!=null){
        		return true;
        	}
        	else 
        		return false;
        }
    	
}
