package com.apicontrollers.extgw.VMS.VoucherStatusChange;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.classes.CONSTANT;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGW_VoucherStatusChange_DP {
	public static String LangCode = _masterVO.getMasterValue(MasterI.LANGUAGE);
	
	public static HashMap<String, String> getAPIdata() throws ParseException {
		
		/*
		 * Variable Declaration
		 */
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGW_VoucherStatusChange_API voucherStatusChangeAPI = new EXTGW_VoucherStatusChange_API();
		
		/*
		 * Object Declaration
		 */
		RandomGeneration randomGeneration = new RandomGeneration();

		/*
		 * Variable initializations
		 */
		String masterSheetpath= _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetpath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		apiData.put(voucherStatusChangeAPI.LOGINID, ExcelUtility.getCellData(0,ExcelI.LOGIN_ID,1));
		apiData.put(voucherStatusChangeAPI.PASSWORD, ExcelUtility.getCellData(0,ExcelI.PASSWORD,1));
		apiData.put(voucherStatusChangeAPI.EXTCODE, ExcelUtility.getCellData(0,ExcelI.EXTERNAL_CODE,1));
		apiData.put(voucherStatusChangeAPI.EXTREFNUM, randomGeneration.randomNumeric(5));
		apiData.put(voucherStatusChangeAPI.EXTNWCODE,  _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		Date pdate = new SimpleDateFormat("dd/MM/YY").parse(_APIUtil.getCurrentTimeStamp());
		String dateFormat = DBHandler.AccessHandler.getSystemPreference(CONSTANT.SYSTEM_DATE_FORMAT);
		String date = new SimpleDateFormat(dateFormat).format(pdate);
		apiData.put(voucherStatusChangeAPI.DATE,date);
		apiData.put(voucherStatusChangeAPI.STATE_CHANGE_REASON,"There is Reason for status change");
		return apiData;
	}
}
