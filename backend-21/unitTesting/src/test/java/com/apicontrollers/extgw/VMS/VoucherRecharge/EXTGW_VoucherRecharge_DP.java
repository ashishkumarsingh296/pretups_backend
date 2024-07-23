package com.apicontrollers.extgw.VMS.VoucherRecharge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.classes.UniqueChecker;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.utils.Decrypt;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGW_VoucherRecharge_DP {
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() throws ParseException {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGW_VoucherRecharge_API voucherRechargeAPI = new EXTGW_VoucherRecharge_API();
		
		/*
		 * Object Declaration
		 */
		RandomGeneration randomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */

		apiData.put(voucherRechargeAPI.EXTREFNUM, randomGeneration.randomNumeric(5));
		apiData.put(voucherRechargeAPI.EXTNWCODE,  _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		apiData.put(voucherRechargeAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(LangCode));
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.UNDER_PROCESS);
		String pin = Decrypt.decryptionVMS(DBHandler.AccessHandler.getPinFromSerialNumber(sno));
		
		apiData.put(voucherRechargeAPI.SNO, sno);
		apiData.put(voucherRechargeAPI.PIN, pin);
		Date pdate = new SimpleDateFormat("dd/MM/YY").parse(_APIUtil.getCurrentTimeStamp());
		String date = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss").format(pdate);
		apiData.put(voucherRechargeAPI.DATE,date);
		apiData.put(voucherRechargeAPI.SUBID, UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
		return apiData;
	}
}
