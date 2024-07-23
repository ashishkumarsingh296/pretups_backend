/**
 * @(#)BarredSubscriberController.java
 *                                     Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                     All Rights Reserved
 *                                     Self barring of subscribers from PreTUPS
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     Author Date History
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     avinash.kamthan Mar 29, 2005 Initital
 *                                     Creation
 *                                     Gurjeet Singh Bedi 26/06/06 Modified
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 * 
 */

package com.btsl.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.Date;
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
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class BarredSubscriberController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(BarredSubscriberController.class.getName());

    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());
        }
        final String methodName = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;
        try {

            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            BarredUserVO barredUserVO = null;
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            // Only to non registered subscriber service is applicable
            if (senderVO == null ) {
            	senderVO = new SenderVO();
            	senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
            }
            	
                if(PretupsI.SERVICE_TYPE_BAR_GIVE_ME_BALANCE.equals(p_requestVO.getDecryptedMessage().substring(0,p_requestVO.getDecryptedMessage().indexOf(' ')))){
                	//msisdn2 check
                	if(p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.GATEWAY_TYPE_SMSC)){
                	String msisdn1 = p_requestVO.getRequestMSISDN();
                	String msisdn2 = p_requestVO.getDecryptedMessage().substring(p_requestVO.getDecryptedMessage().indexOf(' ')+1);
                	
                	if (BTSLUtil.isNullString(msisdn2)) {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.P2PGMB_MSISDN2_BLANK);
                        throw new BTSLBaseException("process", "GMBBAR", PretupsErrorCodesI.P2PGMB_MSISDN2_BLANK);
                    }
                	
                     if(!(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()<msisdn2.length()&&((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()>msisdn2.length())){
                    	 p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALID_RECEIVER_MSISDN);
                         throw new BTSLBaseException("process", "GMBBAR", PretupsErrorCodesI.P2P_ERROR_INVALID_RECEIVER_MSISDN); 
                     }
                     Pattern p = Pattern.compile("[^0-9 ]", Pattern.CASE_INSENSITIVE);
                     Matcher m = p.matcher(msisdn2);
                    if( m.find()){
                    	p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALID_RECEIVER_MSISDN);
                        throw new BTSLBaseException("process", "GMBBAR", PretupsErrorCodesI.P2P_ERROR_INVALID_RECEIVER_MSISDN); 
                    }
                    m = p.matcher(msisdn1);
                   if( m.find()){
                    p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALID_SENDER_MSISDN);
                    throw new BTSLBaseException("process", "GMBBAR", PretupsErrorCodesI.P2P_ERROR_INVALID_SENDER_MSISDN); 
                    }
                	}
                senderVO.setMsisdn(p_requestVO.getDecryptedMessage().substring(p_requestVO.getDecryptedMessage().indexOf(' ')+1));
                senderVO.setServiceType(p_requestVO.getDecryptedMessage().substring(0,p_requestVO.getDecryptedMessage().indexOf(' ')));
                senderVO.setForMsisdn(p_requestVO.getFilteredMSISDN());
                }
                senderVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(senderVO.getMsisdn()));
                senderVO.setNetworkCode(((NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix())).getNetworkCode());
                senderVO.setModule(p_requestVO.getModule());
                senderVO.setCreatedBy(PretupsI.SYSTEM_USER);
                senderVO.setCreatedOn(new Date());
                senderVO.setModifiedBy(PretupsI.SYSTEM_USER);
                senderVO.setModifiedOn(new Date());
               
                // to prepare the barredUserVO from senderVO
                barredUserVO = SubscriberBL.prepareBarredUserVO(senderVO, PretupsI.BARRED_TYPE_SELF, PretupsI.BARRED_USER_TYPE_SENDER, PretupsI.BARRED_SUBSCRIBER_SELF_RSN,
                    senderVO.getCreatedBy());

				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
                final BarredUserDAO barredUserDAO = new BarredUserDAO();
                if(PretupsI.SERVICE_TYPE_BAR_GIVE_ME_BALANCE.equals(senderVO.getServiceType())){
                	
                 int isExist = barredUserDAO.barredDataExistsForGMB(con,  p_requestVO.getDecryptedMessage().substring(p_requestVO.getDecryptedMessage().indexOf(' ')+1) ,p_requestVO.getFilteredMSISDN(), senderVO.getServiceType());
                if(isExist==1||isExist==2)
                {
                	throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BARRED_GMB_ALREADY_BARRED);
                }
          }
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
                		 final int status = barredUserDAO.addBarredUser(con, barredUserVO);

                if (status > 0) {
                	mcomCon.finalCommit();
                    if(PretupsI.SERVICE_TYPE_BAR_GIVE_ME_BALANCE.equals(senderVO.getServiceType())){
                    	if(senderVO.getMsisdn().equals(senderVO.getForMsisdn())){
                    		p_requestVO.setMessageCode(PretupsErrorCodesI.BARRED_SELF_GMB_SUCCESS);
                    	}else{
                    	p_requestVO.setMessageCode(PretupsErrorCodesI.BARRED_GMB_SUCCESS);
                    	}
                    }else{
                    p_requestVO.setMessageCode(PretupsErrorCodesI.BARRED_SUBSCRIBER_SUCCESS);
                    }
                    } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BARRED_SUBSCRIBER_FAILED);
                }
        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException while self barring of=" + p_requestVO.getFilteredMSISDN() + " getting Exception =" + be.getMessage());
            _log.errorTrace(methodName, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.BARRED_SUBSCRIBER_FAILED);
            }

        } catch (Exception e) {
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(methodName, ee);
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception while self barring of=" + p_requestVO.getFilteredMSISDN() + " getting Exception =" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredSubscriberController[process]", p_requestVO
                .getFilteredMSISDN(), "", "", "Exception while self barring:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("BarredSubscriberController#process");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }
}
