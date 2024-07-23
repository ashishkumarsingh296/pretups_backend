package com.restapi.user.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.web.voms.vomscategory.businesslogic.VomsCategoryWebDAO;

public class VoucherInfo implements ServiceKeywordControllerI, Runnable{
	
	public static final Log log = LogFactory.getLog(VoucherInfo.class.getName());


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
		VoucherTypeRequestVO voucherInfo = new VoucherTypeRequestVO();
		
		TypeData data = new TypeData();
		voucherInfo.setData(data);
		
		ChannelUserVO channelUserVO = new ChannelUserVO();
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		StringBuffer responseStr = new StringBuffer("");
		List<VomsCategoryVO> categoryList= new ArrayList<VomsCategoryVO>();
		VomsCategoryWebDAO vomsCategoryWebDAO= new VomsCategoryWebDAO();
		ArrayList<C2CVoucherInfoResponseVO> denominationList = new ArrayList<C2CVoucherInfoResponseVO>();
		HashMap reqMap = p_requestVO.getRequestMap();
		
		try
		{

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			if (reqMap != null && (reqMap.get("MSISDN") != null || reqMap.get("LOGINID") != null)) {
				voucherInfo.getData().setMsisdn((String) reqMap.get("MSISDN"));
				voucherInfo.getData().setLoginId((String) reqMap.get("LOGINID"));
			}

			log.debug(methodName, "Loading user by msisdn");
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, voucherInfo.getData().getMsisdn());

			
			categoryList = vomsCategoryWebDAO.loadCategoryListForAllVoucherTypes
					(con, VOMSI.VOMS_STATUS_ACTIVE, VOMSI.EVD_CATEGORY_TYPE_FIXED, true, channelUserVO.getNetworkID());

			
			
			String responseString = "";
			
			
			HashMap<String,C2CVoucherInfoResponseVO > responseObj = new HashMap<String,C2CVoucherInfoResponseVO >();

			
			for (int i = 0; i < categoryList.size(); i++) 
			{
				VomsCategoryVO vomsCategoryVO = (VomsCategoryVO) categoryList.get(i);
				
				
				
				if(responseObj.get(vomsCategoryVO.getVoucherType()) == null)
				{
					C2CVoucherInfoResponseVO c2CVoucherInfoResponseVO = new C2CVoucherInfoResponseVO();
					VoucherSegmentResponse voucherSegmentResponse = new VoucherSegmentResponse();
					
					List<VoucherSegmentResponse>voucherSegmentList = new ArrayList<VoucherSegmentResponse>();
					
					//c2CVoucherInfoResponseVO.getSegment().add(voucherSegmentResponse);
					
					voucherSegmentList.add(voucherSegmentResponse);
					
					c2CVoucherInfoResponseVO.setSegment(voucherSegmentList);
					
					c2CVoucherInfoResponseVO.setValue(vomsCategoryVO.getVoucherType());
					
					voucherSegmentResponse.setSegmentType(vomsCategoryVO.getSegment());
					voucherSegmentResponse.setSegmentValue(BTSLUtil.getSegmentDesc(vomsCategoryVO.getSegment()));
					
					List<String> denominations = new ArrayList<String>();
					denominations.add(Double.toString(vomsCategoryVO.getMrp()));
					voucherSegmentResponse.setDenominations(denominations);
					
					
					responseObj.put(vomsCategoryVO.getVoucherType(), c2CVoucherInfoResponseVO);
					
				}
				else
				{
					C2CVoucherInfoResponseVO c2CVoucherInfoResponseVO =responseObj.get(vomsCategoryVO.getVoucherType());
					
					boolean segmentExists = false;
					List<VoucherSegmentResponse> segmentList = c2CVoucherInfoResponseVO.getSegment();
					
					for(VoucherSegmentResponse responseListObj : segmentList)
					{
						if(responseListObj.getSegmentType().equals(vomsCategoryVO.getSegment()))
						{
							segmentExists = true;
							responseListObj.getDenominations().add(Double.toString(vomsCategoryVO.getMrp()));
						}
					}
					if(!segmentExists)
					{
						VoucherSegmentResponse newElement = new VoucherSegmentResponse();
						
						newElement.setSegmentType(vomsCategoryVO.getSegment());
						newElement.setSegmentValue(BTSLUtil.getSegmentDesc(vomsCategoryVO.getSegment()));
						
						List<String> denoms = new ArrayList<String>();
						
						denoms.add(Double.toString(vomsCategoryVO.getMrp()));
						newElement.setDenominations(denoms);
						
						c2CVoucherInfoResponseVO.getSegment().add(newElement);
						
					}
					
				}

			}
			
			 for (Entry<String, C2CVoucherInfoResponseVO> entry : responseObj.entrySet()) 
			 {
				 denominationList.add(entry.getValue());
			 }
			
				new VoucherInfoServices().isVoucherTypeValid(con, denominationList, channelUserVO);
			
			String resType = reqMap.get("TYPE") + "RES";
			responseStr.append("{ \"type\": \"" + resType + "\" ,");
			responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
			responseStr.append(" \"C2CVoucherResponse\": [");
			for(C2CVoucherInfoResponseVO voucherInfoObj : denominationList)
			{

				responseStr.append("{ \"voucherValue\": \"" + voucherInfoObj.getValue() + "\" ,");
				responseStr.append(" \"voucherDisplayValue\": \"" + voucherInfoObj.getDisplayValue() + "\" ,");				
				responseStr.append(" \"voucherSegment\": [");
				
				for(VoucherSegmentResponse element: voucherInfoObj.segment)
				{
					responseStr.append("{ \"segmentType\": \"" + element.getSegmentType() + "\" ,");
					responseStr.append(" \"segmentValue\": \"" + element.getSegmentValue() + "\" ,");
					
					responseStr.append(" \"denominations\":" + element.getDenominations() + "}");
					responseStr.append(",");
					
				}
					responseStr.deleteCharAt( responseStr.length() - 1 );
					responseStr.append("]},");
			}
			responseStr.deleteCharAt( responseStr.length() - 1 );
			responseStr.append("]}");
			
			
			log.debug("response ", "Voucher Info responseStr  " + responseStr);

			responseMap.put("RESPONSE", responseStr.toString());

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
        		mcomCon.close("VoucherInfo#"+methodName);
        		mcomCon=null;
        		}
            if (log.isDebugEnabled()) {
               log.debug(methodName, "Exiting");
            }
		}
		
	}

}
