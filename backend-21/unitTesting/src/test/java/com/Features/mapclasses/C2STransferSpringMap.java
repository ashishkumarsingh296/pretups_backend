package com.Features.mapclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class C2STransferSpringMap {

	Map<String, String> map = new HashMap<>();

	public C2STransferSpringMap() {
		map = defaultMap();
	}


	public Map<String,String> defaultMap(){
		Map<String, String> c2sparametermap = new HashMap<>();
		String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		ArrayList<String> alist1 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(CustomerRechargeCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();
		int userCounter = 0;
		for (int i = 1; i <= chnlCount; i++) {
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				userCounter++;
			}
		}

		Object[][] Data = new Object[1][6];
		for (int i = 1; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[0][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				Data[0][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[0][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				Data[0][3] = CustomerRechargeCode;
				Data[0][4] = i;
				Data[0][5] = ExcelUtility.getCellData(0, ExcelI.MSISDN,i);
				break;
			}
		}
		String subMSISDN;
		if(CustomerRechargeCode.equals("PPB"))
		{
			subMSISDN=UniqueChecker.generate_subscriber_MSISDN("Postpaid");
		}
		else{
			subMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		}



		c2sparametermap.put("parentCategory", Data[0][0].toString());
		c2sparametermap.put("category", Data[0][1].toString());
		c2sparametermap.put("pin", Data[0][2].toString());
		c2sparametermap.put("service", Data[0][3].toString());
		c2sparametermap.put("msisdn", subMSISDN);
		c2sparametermap.put("subService",_masterVO.getProperty("subService"));
		c2sparametermap.put("amount", "");
		c2sparametermap.put("fromRowNum", Data[0][4].toString());
		c2sparametermap.put("fromMSISDN", Data[0][5].toString());

		int fromrownum = Integer.parseInt(c2sparametermap.get("fromRowNum"));

		c2sparametermap.put("fromTCPName", ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, fromrownum));
		c2sparametermap.put("fromTCPID", ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, fromrownum));
		c2sparametermap.put("fromDomain", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, fromrownum));
		c2sparametermap.put("fromLoginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, fromrownum));
		c2sparametermap.put("fromCategoryCode", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, fromrownum));
		c2sparametermap.put("fromGrade", ExcelUtility.getCellData(0, ExcelI.GRADE,fromrownum));
		c2sparametermap.put("fromCommProfile", ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, fromrownum));
		c2sparametermap.put("domainCode",DBHandler.AccessHandler.getDomainCode(c2sparametermap.get("fromDomain")));
		c2sparametermap.put("cardGroupName", ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME,fromrownum));

		return c2sparametermap;
	}

	public Map<String, String> setC2SMap(String Key, String Value) {	
		Map<String, String> m = new HashMap<>(this.map);
		m.put(Key, Value);
		return m;
	}

	public String getC2SMap(String Key) {	
		Map<String, String> instanceMap= new HashMap<>(this.map);
		return instanceMap.get(Key);
	}
	
	public Map<String, String> getC2SMap(){
		Map<String, String> m = new HashMap<>(this.map);
		return m;
	}

}
