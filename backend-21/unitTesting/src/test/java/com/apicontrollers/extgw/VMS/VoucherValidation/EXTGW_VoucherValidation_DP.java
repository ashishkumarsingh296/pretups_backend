package com.apicontrollers.extgw.VMS.VoucherValidation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGW_VoucherValidation_DP {
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() throws ParseException {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGW_VoucherValidation_API voucherValidationAPI = new EXTGW_VoucherValidation_API();
		
		/*
		 * Object Declaration
		 */
		RandomGeneration randomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */

		apiData.put(voucherValidationAPI.EXTREFNUM, randomGeneration.randomNumeric(5));
		apiData.put(voucherValidationAPI.EXTNWCODE,  _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		apiData.put(voucherValidationAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(LangCode));
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.ENABLE);
		String pin = Decrypt.decryptionVMS(DBHandler.AccessHandler.getPinFromSerialNumber(sno));
		
		apiData.put(voucherValidationAPI.SNO, sno);
		apiData.put(voucherValidationAPI.PIN, pin);
		Date pdate = new SimpleDateFormat("dd/MM/YY").parse(_APIUtil.getCurrentTimeStamp());
		String date = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss").format(pdate);
		apiData.put(voucherValidationAPI.DATE,date);
		return apiData;
	}
}
