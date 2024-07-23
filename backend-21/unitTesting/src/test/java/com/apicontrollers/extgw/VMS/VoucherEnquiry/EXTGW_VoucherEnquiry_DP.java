package com.apicontrollers.extgw.VMS.VoucherEnquiry;

import java.util.HashMap;

import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGW_VoucherEnquiry_DP {
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGW_VoucherEnquiry_API voucherEnquiryAPI = new EXTGW_VoucherEnquiry_API();
		
		/*
		 * Object Declaration
		 */
		RandomGeneration randomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */

		apiData.put(voucherEnquiryAPI.EXTREFNUM, randomGeneration.randomNumeric(5));
		apiData.put(voucherEnquiryAPI.EXTNWCODE,  _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		String sno = DBHandler.AccessHandler.getSerialNumberAssignedToUser(PretupsI.ENABLE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		String pin = Decrypt.decryptionVMS(DBHandler.AccessHandler.getPinFromSerialNumber(sno));
		
		apiData.put(voucherEnquiryAPI.SNO, sno);
		apiData.put(voucherEnquiryAPI.PIN, pin);
		apiData.put(voucherEnquiryAPI.DATE,_APIUtil.getCurrentTimeStamp());
		return apiData;
	}
}
