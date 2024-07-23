package com.btsl.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VoucherTypeVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class SubscriberVoucherInquiryController implements ServiceKeywordControllerI
{
	 private static Log _log = LogFactory.getLog(TransfersReportController.class.getName());
	 private String _requestID = null;
	@SuppressWarnings("unchecked")
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
            VomsProductDAO vomsProductDAO = new VomsProductDAO();
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				VomsVoucherDAO vomsVoucherDao=new VomsVoucherDAO();
				ChannelUserDAO channelUserDAO= new ChannelUserDAO();
				ArrayList<VomsVoucherVO> voucherList = new ArrayList<VomsVoucherVO>();
				 senderVO = (SenderVO) p_requestVO.getSenderVO();
		            // If user is not already registered then register the user with
		            // status as NEW and Default PIN
		            if (senderVO == null) {
		                new RegisterationController().regsiterNewUser(p_requestVO);
		                senderVO = (SenderVO) p_requestVO.getSenderVO();
		                senderVO.setDefUserRegistration(true);
		                senderVO.setActivateStatusReqd(true);
						p_requestVO.setSenderLocale(new Locale(senderVO.getLanguage(), senderVO.getCountry()));
		            }
				voucherList=vomsVoucherDao.loadAvailableDigitalVoucherSubscriberList(con,senderVO.getMsisdn());
				Iterator<VomsVoucherVO> it =voucherList.iterator();
			    StringBuffer stb = new StringBuffer();
			    ArrayList<VoucherTypeVO> voucherType=vomsProductDAO.loadVoucherDetails(con);
				while(it.hasNext())
				{
					VomsVoucherVO vo=(VomsVoucherVO) it.next();
					vo.setVoucherSegment(BTSLUtil.getSegmentDesc(vo.getVoucherSegment()));
					vo.setVoucherType(BTSLUtil.getVoucherTypeDesc(voucherType,vo.getVoucherType()));
					vo.setPinNo(BTSLUtil.decryptText(vo.getPinNo())); 
					if(vo.getUserID()!=null)
					{
						vo.setUserID(channelUserDAO.loadChannelUserByUserID(con, vo.getUserID()).getLoginID());
					}
					stb.append(vo.getSerialNo()+",");
					stb.append(vo.getProductName()+",");
					stb.append(vo.getVoucherType()+",");
					stb.append(vo.getVoucherSegment()+",");
					stb.append(BTSLDateUtil.getSystemLocaleDate(vo.getExpiryDate().toString())+",");
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_VOUCHER_PIN_REQUIRED))).booleanValue())
					stb.append(vo.getPinNo()+",");
					if(it.hasNext())
					{
					stb.append(vo.getUserID()+":");
					}
					else
					{
					stb.append(vo.getUserID());				
					
					}
				}
				if(voucherList!=null && voucherList.size()>0)
				{
				senderVO.setVoList(voucherList);
				p_requestVO.setValueObject(voucherList);// for REST
	            p_requestVO.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_SUCCESS_MESSAGE);
	            
	            p_requestVO.setMessageArguments(new String[]{stb.toString(),p_requestVO.getRequestMSISDN()});
	            p_requestVO.setSuccessTxn(true);
				}
				else
				{
					  throw new BTSLBaseException(this, "process", PretupsErrorCodesI.VOUCHER_NOT_ASSOCIATED);
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
			} catch (Exception e) {
			    p_requestVO.setSuccessTxn(false);
	            _log.error("process", "BTSLBaseException " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberVoucherInquiryController[process]", "", "", "",
	                            "Exception:" + e.getMessage());
	            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			}
			finally 
			{
				if (mcomCon != null) {
					mcomCon.close("TransfersReportController#process");
					mcomCon = null;
				}
	            if (_log.isDebugEnabled()) {
	                _log.debug("process", _requestID, " Exited ");
	            }
			}
			
}

	
	
	
	
	
	
	
}
