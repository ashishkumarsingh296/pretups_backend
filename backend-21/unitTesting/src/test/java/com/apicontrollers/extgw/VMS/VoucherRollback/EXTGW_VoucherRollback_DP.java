package com.apicontrollers.extgw.VMS.VoucherRollback;

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

public class EXTGW_VoucherRollback_DP {
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() throws ParseException {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGW_VoucherRollback_API voucherRollbackAPI = new EXTGW_VoucherRollback_API();
		
		/*
		 * Object Declaration
		 */
		RandomGeneration randomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */

		apiData.put(voucherRollbackAPI.EXTREFNUM, randomGeneration.randomNumeric(5));
		apiData.put(voucherRollbackAPI.EXTNWCODE,  _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		apiData.put(voucherRollbackAPI.LANGUAGE1, DBHandler.AccessHandler.checkForLangCode(LangCode));
		String sno = DBHandler.AccessHandler.getSerialNumberFromStatus(PretupsI.UNDER_PROCESS);
		String pin = Decrypt.decryptionVMS(DBHandler.AccessHandler.getPinFromSerialNumber(sno));
		
		apiData.put(voucherRollbackAPI.SNO, sno);
		apiData.put(voucherRollbackAPI.PIN, pin);
		Date pdate = new SimpleDateFormat("dd/MM/YY").parse(_APIUtil.getCurrentTimeStamp());
		String date = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss").format(pdate);
		apiData.put(voucherRollbackAPI.DATE,date);
		String[] subID = DBHandler.AccessHandler.getVomsVoucherDetailsFromSerialNumber(sno, "subscriber_id");
		apiData.put(voucherRollbackAPI.SUBID,subID[0]);
		apiData.put(voucherRollbackAPI.STATE_CHANGE_REASON,"There is Reason for status change");
		return apiData;
	}
}
