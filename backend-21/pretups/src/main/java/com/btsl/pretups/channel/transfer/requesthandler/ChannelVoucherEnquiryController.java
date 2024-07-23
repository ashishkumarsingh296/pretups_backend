package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
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
import com.btsl.util.BTSLUtil;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;

public class ChannelVoucherEnquiryController implements ServiceKeywordControllerI{
	private final Log _log = LogFactory.getLog(VoucherExpiryDateExtensionController.class.getName());
	@Override
	public void process(RequestVO p_requestVO) {
		String methodName = "process";
		if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered p_requestVO=" + p_requestVO);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
			final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
			ArrayList<ArrayList<String> > availableVoucherList = new ArrayList<> ();
			if(PretupsI.GATEWAY_TYPE_SMSC.equals(p_requestVO.getRequestGatewayCode())) {
				setRequestMap(p_requestVO);
			}
			
			String voucherType = (p_requestVO.getRequestMap().get("VOUCHERTYPE")).toString();
			String voucherSegment = (p_requestVO.getRequestMap().get("VOUCHERSEGMENT")).toString();
			String denomination = (p_requestVO.getRequestMap().get("DENOMINATION")).toString();
			if(BTSLUtil.isNullString(voucherType) || BTSLUtil.isNullString(voucherSegment) || BTSLUtil.isNullString(denomination) || BTSLUtil.isNullString(channelUserVO.getUserID()) || BTSLUtil.isNullString(channelUserVO.getNetworkID()))
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_MANDATORY_FIELD);
			
			availableVoucherList = vomsVoucherDAO.loadAvailableVouchers(con, voucherType, voucherSegment, denomination, p_requestVO.getRequestMap().get("VOUCHERPROFILE").toString(), channelUserVO.getUserID(), channelUserVO.getNetworkID());
			if(availableVoucherList!=null && availableVoucherList.size()>0)
			{
				channelUserVO.setVoucherList(availableVoucherList);
				if(PretupsI.GATEWAY_TYPE_SMSC.equals(p_requestVO.getRequestGatewayCode()))
				{
					p_requestVO.setMessageCode(PretupsI.USER_AVAILABLE_VOUCHER_SMSC);
					p_requestVO.setMessageArguments(new String[] {availableVoucherList.get(0).get(availableVoucherList.get(0).size()-1)});
				}
				else
					p_requestVO.setMessageCode(PretupsI.TXN_STATUS_SUCCESS);
				
				p_requestVO.setSuccessTxn(true);
			}
			else
			{
				 channelUserVO.setVoucherList(availableVoucherList);
				 if(PretupsI.GATEWAY_TYPE_SMSC.equals(p_requestVO.getRequestGatewayCode()))
				 {
					 p_requestVO.setMessageCode(PretupsI.USER_AVAILABLE_VOUCHER_SMSC);
					 p_requestVO.setMessageArguments(new String[] {"0"});
				 }
				 
				 else
					 p_requestVO.setMessageCode(PretupsErrorCodesI.VOUCHER_NOT_AVAILABLE);
				 
		         p_requestVO.setSuccessTxn(true);
			}
			
		} catch (BTSLBaseException e) {
			p_requestVO.setSuccessTxn(false);
            p_requestVO.setMessageCode(e.getMessageKey());
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(methodName, e);
            if (e.isKey()) {
                p_requestVO.setMessageCode(e.getMessageKey());
                p_requestVO.setMessageArguments(e.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
		} catch (SQLException e) {
			p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelVoucherEnquiryController[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
		}
        
        finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("ChannelVoucherEnquiryController#process");
        		mcomCon=null;
        	}
        	
            if (_log.isDebugEnabled()) {
            	_log.debug("process", " Exited ");
            }
        }
        
		}
	
	/**
	 * This method will set requestMap for SMSC gateway
	 * @param p_requestVO
	 */
	public void setRequestMap(RequestVO p_requestVO) {
		String methodName = "setRequestMap";
		if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered p_requestVO=" + p_requestVO);
        }
		HashMap<String, String> requestMap = null;
		if(!BTSLUtil.isNullString(p_requestVO.getRequestMessage())) {
			String[] requestArr = p_requestVO.getRequestMessage().split(" ");
			requestMap = new HashMap<String, String>();
			requestMap.put("VOUCHERTYPE", requestArr[1]);
			requestMap.put("VOUCHERSEGMENT", requestArr[2]);
			requestMap.put("DENOMINATION", requestArr[3]);
			requestMap.put("VOUCHERPROFILE", requestArr[4]);
		}
		p_requestVO.setRequestMap(requestMap);
	}
}
