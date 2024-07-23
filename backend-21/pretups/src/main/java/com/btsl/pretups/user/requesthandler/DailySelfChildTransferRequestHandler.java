package com.btsl.pretups.user.requesthandler;

/**
 * @(#)DailySelfChildTransferRequestHandler.java
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferEnquiryDAO;
import com.btsl.pretups.user.businesslogic.UserTransferEnquiryVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class DailySelfChildTransferRequestHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(DailySelfChildTransferRequestHandler.class.getName());
    
    /**
     * Method process.
     * This method processed the request to fetch the list of the UserTransferEnquiryVO object for the C2S Transfers.
     *  
     * @param p_requestVO RequestVO
     * 
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "DailySelfChildTransferRequestHandler[process()]";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered " + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        UserTransferEnquiryDAO userTransferEnquiryDAO = null;
        ChannelUserVO _channelUserVO;
        ArrayList<UserTransferEnquiryVO> userTransferEnquiryVOList = null;
        
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
            String searchTransferForMsisn=null;
            if(p_requestVO.getMsisdn()!=null) {
            	searchTransferForMsisn = p_requestVO.getMsisdn();
            } else {
            	searchTransferForMsisn = _channelUserVO.getMsisdn();
            }
            userTransferEnquiryVOList = userTransferEnquiryDAO.loadServiceWiseTransferCounts(con, searchTransferForMsisn, BTSLUtil.getDateFromDateString(p_requestVO.getReqDate()));
            
            String messageToBeSentToSender = Constants.getProperty("DSR_SENDER_MESSAGE_REQ");
            
            if (BTSLUtil.isNullString(messageToBeSentToSender)) {
            	p_requestVO.setSenderMessageRequired(false);
            } else if (PretupsI.YES.equalsIgnoreCase(messageToBeSentToSender.trim())) {
            	p_requestVO.setSenderMessageRequired(true);
            } else if (PretupsI.NO.equalsIgnoreCase(messageToBeSentToSender.trim())) {
            	p_requestVO.setSenderMessageRequired(false);
            }
            
            
            if(userTransferEnquiryVOList.size()==0) {
            	p_requestVO.setMessageCode(PretupsErrorCodesI.DSR_NO_RECORDS);
                p_requestVO.setMessageArguments(new String[] {searchTransferForMsisn});
            } else {
            	p_requestVO.setMessageCode(PretupsErrorCodesI.DSR_RECORDS_FETCHED);
            	p_requestVO.setMessageArguments(new String[] {searchTransferForMsisn});
            }
            HashMap map = p_requestVO.getRequestMap();
            if (map == null) {
                map = new HashMap();
            }
            map.put("TRANSFER_ENQUIRY_LIST", userTransferEnquiryVOList);
            
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
        		mcomCon.close("DailySelfChildTransferRequestHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exited ");
            }
        }
    }
}
