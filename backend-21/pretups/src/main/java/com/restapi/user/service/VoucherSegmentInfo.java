package com.restapi.user.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.voms.vomscommon.VOMSI;

public class VoucherSegmentInfo implements ServiceKeywordControllerI, Runnable{
	
	public static final Log log = LogFactory.getLog(VoucherSegmentInfo.class.getName());


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process(RequestVO p_requestVO) {
		
		String methodName = "process";
		log.debug(methodName, "Entring");
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		HashMap responseMap = new HashMap();
		VoucherSegmentRequestVO voucherSegmentRequestVO = new VoucherSegmentRequestVO();
		
		SegmentData data = new SegmentData();
		voucherSegmentRequestVO.setData(data);
		
		ChannelUserVO channelUserVO = new ChannelUserVO();
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		StringBuffer responseStr = new StringBuffer("");

		HashMap reqMap = p_requestVO.getRequestMap();
		
		try
		{
			
	
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();

		if (reqMap != null && (reqMap.get("MSISDN") != null || reqMap.get("LOGINID") != null)) {
			voucherSegmentRequestVO.getData().setMsisdn((String) reqMap.get("MSISDN"));
			voucherSegmentRequestVO.getData().setLoginId((String) reqMap.get("LOGINID"));
			voucherSegmentRequestVO.getData().setVoucherType((String) reqMap.get("VTYPE"));
		}
		
		log.debug(methodName, "Loading user by msisdn");
		channelUserVO = channelUserDAO.loadChannelUserDetails(con, voucherSegmentRequestVO.getData().getMsisdn());
		
		if(new VoucherInfoServices().isVoucherTypeValid(con, voucherSegmentRequestVO.getData().getVoucherType(), channelUserVO) == false)
        {
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setSenderReturnMessage("Invalid voucher type");
			return ;
        }
        
        
    	@SuppressWarnings("unchecked")
		ArrayList<ListValueVO> segmentList = com.btsl.util.BTSLUtil.getSegmentList(voucherSegmentRequestVO.getData().getVoucherType(), LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true));
    	String responseString="";
    	responseStr.append("VOUCHERSEGMENTS=[");
    	for(ListValueVO value: segmentList)
    	{
    		C2CVoucherSegmentResponse voucherObj = new C2CVoucherSegmentResponse();
    		
    		
    		voucherObj.setCode(value.getValue());
    		voucherObj.setValue(value.getLabel());
    		
    		responseStr.append(voucherObj.toString());
    		responseStr.append(",");    		
    	}
    	responseString = responseStr.substring(0,responseStr.length()-1);
    	responseString= responseString+"]";

    	
    	log.debug("response ", "Voucher Segment responseStr  " + responseStr);

		responseMap.put("RESPONSE", responseString);

		p_requestVO.setResponseMap(responseMap);
		p_requestVO.setSuccessTxn(true);
		p_requestVO.setMessageCode("20000");
		p_requestVO.setSenderReturnMessage("Transaction has been completed!");
		
		}
		catch(Exception e)
		{
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setSenderReturnMessage("Transaction has been failed!");
		}
		finally {
			if(mcomCon != null)
        	{
        		mcomCon.close("VoucherSegmentInfo#"+methodName);
        		mcomCon=null;
        		}
            if (log.isDebugEnabled()) {
               log.debug(methodName, "Exiting");
            }
		}
		
	}

}
