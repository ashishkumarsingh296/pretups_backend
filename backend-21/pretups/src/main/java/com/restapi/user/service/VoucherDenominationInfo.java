package com.restapi.user.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.web.voms.vomscategory.businesslogic.VomsCategoryWebDAO;

public class VoucherDenominationInfo implements ServiceKeywordControllerI, Runnable {

	public static final Log log = LogFactory.getLog(VoucherDenominationInfo.class.getName());

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void process(RequestVO p_requestVO) {

		String methodName = "process";
		log.debug(methodName, "Entered");

		Connection con = null;
		MComConnectionI mcomCon = null;

		HashMap responseMap = new HashMap();
		VomsCategoryWebDAO vomsCategorywebDAO = new VomsCategoryWebDAO();
		
		VoucherDenominationRequestVO voucherDenominationRequestVO = new VoucherDenominationRequestVO();
		
		DenominationData data = new DenominationData();
		voucherDenominationRequestVO.setData(data);
		
		ChannelUserVO channelUserVO = new ChannelUserVO();
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		StringBuffer responseStr = new StringBuffer("");
		String mrp = null;

		HashMap reqMap = p_requestVO.getRequestMap();

		try {
			if (reqMap != null && (reqMap.get("MSISDN") != null || reqMap.get("LOGINID") != null)) {
				voucherDenominationRequestVO.getData().setMsisdn((String) reqMap.get("MSISDN"));
				voucherDenominationRequestVO.getData().setLoginId((String) reqMap.get("LOGINID"));
				voucherDenominationRequestVO.getData().setVoucherType((String) reqMap.get("VTYPE"));
				voucherDenominationRequestVO.getData().setVoucherSegment((String) reqMap.get("VSEG"));
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			log.debug(methodName, "Loading user by msisdn");
			channelUserVO = channelUserDAO.loadChannelUserDetails(con,
					voucherDenominationRequestVO.getData().getMsisdn());

			if (new VoucherInfoServices().isVoucherTypeValid(con,
					voucherDenominationRequestVO.getData().getVoucherType(), channelUserVO) == false) {
				p_requestVO.setSuccessTxn(false);
				p_requestVO.setSenderReturnMessage("Invalid voucher type");
				return;
			}

			String segment = voucherDenominationRequestVO.getData().getVoucherSegment();

			if (segment.equalsIgnoreCase("NL")) {
				segment = "NL";
			} else if (segment.equalsIgnoreCase("LC")) {
				segment = "LC";
			} else {
				p_requestVO.setSuccessTxn(false);
				p_requestVO.setSenderReturnMessage("Invalid voucher segment");
				return;
			}
			@SuppressWarnings("unchecked")
			List<VomsCategoryVO> categoryList = vomsCategorywebDAO.loadCategoryList(con,
					voucherDenominationRequestVO.getData().getVoucherType(), VOMSI.VOMS_STATUS_ACTIVE,
					VOMSI.EVD_CATEGORY_TYPE_FIXED, true, channelUserVO.getNetworkID(), segment);
			String responseString ="";
			responseStr.append("VOUCHERDENOMINATIONS=[");
			for (int i = 0; i < categoryList.size(); i++) {
				VomsCategoryVO vomsCategoryVO = (VomsCategoryVO) categoryList.get(i);

				mrp = Double.toString(vomsCategoryVO.getMrp());
				responseStr.append(mrp);
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

		} catch (Exception e) {
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setSenderReturnMessage("Transaction has been failed!");
		} finally {
			if (mcomCon != null) {
				mcomCon.close("VoucherDenominationInfo#" + methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		}

	}
}
