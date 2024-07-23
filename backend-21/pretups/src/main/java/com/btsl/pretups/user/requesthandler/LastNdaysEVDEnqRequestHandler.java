package com.btsl.pretups.user.requesthandler;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.MComReportDBConnection;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.TransferEnquiryDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.transfer.businesslogic.TransferVO;

public class LastNdaysEVDEnqRequestHandler implements ServiceKeywordControllerI{

	private static Log _log = LogFactory.getLog(LastNdaysEVDEnqRequestHandler.class.getName());
	@Override
	public void process(RequestVO p_requestVO) {
		
		final String methodName = "process";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered: ");
		}
		Connection con = null;MComConnectionI mcomCon = null;
        TransferEnquiryDAO transferEnquiryDAO = null;
        List<TransferVO> transfersList = null;
        
        try {
        	mcomCon = new MComReportDBConnection(); con = mcomCon.getConnection();
        	final int noDays = SystemPreferences.LAST_N_DAYS_EVD_TRF;
        	final String serviceType = PretupsI.SERVICE_TYPE_EVD;
        	final String senderMsisdn = p_requestVO.getFilteredMSISDN();
        	final String serialNumber = (String) p_requestVO.getRequestMap().get("SERIALNUMBER");
        	final String denomination = (String) p_requestVO.getRequestMap().get("DENOMINATION");
        	transferEnquiryDAO = new TransferEnquiryDAO();
        	transfersList = transferEnquiryDAO.loadLastNDaysEVDTrfDetails(con, noDays, serviceType, senderMsisdn, serialNumber, denomination);
            
            
        	HashMap map = p_requestVO.getRequestMap();
            if (map == null) {
                map = new HashMap();
            }
            map.put("TRANSFERLIST", transfersList);
            
            if(transfersList != null && transfersList.size() > 0) {
            	p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            	
            }
            if(transfersList != null && transfersList.size() == 0) {
            	if(noDays == 0) {
            		p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_TRANSFER_NO_TRANSACTION_DONE);
            	}
            	
            	else {
                    final Integer temp = noDays;
                    final String[] arg = new String[] { temp.toString() };
                    p_requestVO.setMessageArguments(arg);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.LASTX_TRANSFER_NO_TRANSACTION_DONE);
            	}
            	
            	
            }
            
            p_requestVO.setSuccessTxn(true);
            
            
        }catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastNDaysEVDEnqRequestHandler[process]", "", "", "",
                            "BTSL Exception:" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastNDaysEVDEnqRequestHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("LastNDaysEVDEnqRequestHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
	}


}
