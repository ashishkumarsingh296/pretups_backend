package com.btsl.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.util.BTSLUtil;

public class SubscriberVoucherEnquiry implements ServiceKeywordControllerI
{
	 private static Log _log = LogFactory.getLog(TransfersReportController.class.getName());
	 private String _requestID = null;
	@Override
	public void process(RequestVO p_requestVO) {
		
		 _requestID = p_requestVO.getRequestIDStr();
	        if (_log.isDebugEnabled()) {
	            _log.debug("process", _requestID, "Entered p_requestVO: " + p_requestVO);
	        }

	        final String methodName = "process";
			Connection con = null;
			MComConnectionI mcomCon = null;
	        SenderVO senderVO = null;
            senderVO = (SenderVO) p_requestVO.getSenderVO();// changed by Ashish
			
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				SubscriberDAO subscriberDAO = new SubscriberDAO();
				
				ArrayList voucherList = new ArrayList();
				//voucherList=subscriberDAO.loadAvailableDigitalVoucherSubscriberList(con,senderVO.getUserID());
				
				String arr[] =  new String[1];
				arr[0]=BTSLUtil.getMessage(senderVO.getLocale(), voucherList);
				
				 p_requestVO.setMessageArguments(arr);
	             p_requestVO.setMessageCode(PretupsErrorCodesI.TRANSFER_REPORT_SUCCESS);
				 
			} catch (BTSLBaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}

	
	
	
	
	
	
	
}
