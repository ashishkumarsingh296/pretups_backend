package com.btsl.pretups.user.requesthandler;

/**
 * @(#)DailySelfChildBalanceRequestHandler.java
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

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
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferEnquiryDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class DailySelfChildBalanceRequestHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(DailySelfChildBalanceRequestHandler.class.getName());
    
    /**
     * Method process.
     * This method processed the request to fetch the list of the stock wise user balances.
     *  
     * @param p_requestVO RequestVO
     * 
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "DailySelfChildBalanceRequestHandler[process()]";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered " + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        UserTransferEnquiryDAO userTransferEnquiryDAO = null;
        ChannelUserVO _channelUserVO;
        UserVO cUserVO;
        
        UserDAO _userDAO = new UserDAO();
        
        try {
        	userTransferEnquiryDAO = new UserTransferEnquiryDAO();
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();

            final String messageArr[] = p_requestVO.getRequestMessageArray();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Message Array " + messageArr.length);
            }
            if (messageArr.length < 1 || messageArr.length > 3) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_ERROR_DA_TRANSFER_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
            }
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            String searchBalanceForUserID=null;
            String searchBalanceForMsisn=null;
            if(p_requestVO.getMsisdn()!=null) {
            	searchBalanceForMsisn=p_requestVO.getMsisdn();
            	cUserVO= _userDAO.loadUsersDetails(con, searchBalanceForMsisn);
            	if(cUserVO!=null) {
            		searchBalanceForUserID = cUserVO.getUserID();
            	}
            } else {
            	searchBalanceForMsisn=_channelUserVO.getMsisdn();
            	searchBalanceForUserID = _channelUserVO.getUserID();
            }
            HashMap map = p_requestVO.getRequestMap();
            if (map == null) {
                map = new HashMap();
            }
            
            
            ArrayList<UserBalancesVO> userBalanceList = userTransferEnquiryDAO.loadUserTransferBalancesCount(con, searchBalanceForUserID);
            
            String messageToBeSentToSender = Constants.getProperty("USR_BAL_SENDER_MESSAGE_REQ");
            
            if (BTSLUtil.isNullString(messageToBeSentToSender)) {
            	p_requestVO.setSenderMessageRequired(false);
            } else if (PretupsI.YES.equalsIgnoreCase(messageToBeSentToSender.trim())) {
            	p_requestVO.setSenderMessageRequired(true);
            } else if (PretupsI.NO.equalsIgnoreCase(messageToBeSentToSender.trim())) {
            	p_requestVO.setSenderMessageRequired(false);
            }
            
            if(userBalanceList.size()==0) {
            	p_requestVO.setMessageCode(PretupsErrorCodesI.DSR_NO_RECORDS);
                p_requestVO.setMessageArguments(new String[] {searchBalanceForMsisn});
            } else {
            	p_requestVO.setMessageCode(PretupsErrorCodesI.DSR_RECORDS_FETCHED);
            	p_requestVO.setMessageArguments(new String[] {searchBalanceForMsisn});
            }
            
            map.put("USER_BALANCE_LIST", userBalanceList);
            
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setSuccessTxn(false);
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,METHOD_NAME,"","","","BTSL Exception:"+be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setSuccessTxn(false);
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("DailySelfChildBalanceRequestHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exited ");
            }
        }
    }
}
