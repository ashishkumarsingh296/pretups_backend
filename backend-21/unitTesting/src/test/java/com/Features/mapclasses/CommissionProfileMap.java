package com.Features.mapclasses;

import java.util.HashMap;
import java.util.Map;

import com.Features.Map_CommissionProfile;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class CommissionProfileMap extends BaseTest {

	Map_CommissionProfile Map_CommProfile;
	HashMap<String, String> channelMap=new HashMap<>();
	
     public HashMap<String, String> defaultMap(String commType) {
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		HashMap<String, String> paraMeterMap = new HashMap<>();
		
		if (commType.equalsIgnoreCase(PretupsI.COMM_TYPE_BASECOMM)) {
		
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);			
			paraMeterMap.put("COMMTYPE", PretupsI.COMM_TYPE_BASECOMM);
			paraMeterMap.put("TO_DOMAIN", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1));
			paraMeterMap.put("TO_CATEGORY", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1));
			paraMeterMap.put("GeographicalDomain", ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, 1));
			paraMeterMap.put("TO_GRADE", ExcelUtility.getCellData(0, ExcelI.GRADE, 1));
			paraMeterMap.put("TO_COMMISSION_PROFILE", ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, 1));
			
		} else if (commType.equalsIgnoreCase(PretupsI.COMM_TYPE_ADNLCOMM)) {
			
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			for (int i = 1; i <= rowCount; i++) {
				String adnlCommStatus = ExcelUtility.getCellData(0, ExcelI.ADDITIONAL_COMMISSION, i);
				if (adnlCommStatus.equalsIgnoreCase("Y")) {
					paraMeterMap.put("COMMTYPE", PretupsI.COMM_TYPE_ADNLCOMM);
					paraMeterMap.put("TO_DOMAIN", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i));
					paraMeterMap.put("TO_CATEGORY", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
					paraMeterMap.put("GeographicalDomain", ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, i));
					paraMeterMap.put("TO_GRADE", ExcelUtility.getCellData(0, ExcelI.GRADE, i));
					paraMeterMap.put("TO_COMMISSION_PROFILE", ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i));
					break;
				}
			}
		}
		
		paraMeterMap.put("CBC_VALUE", "10000");
			
		/*paraMeterMap.put("APPLICABLE_FROM_DATE", "CURRENTDATE");
		paraMeterMap.put("APPLICABLE_TO_DATE", "CURRENTDATE");
		paraMeterMap.put("TIME_SLAB", "00:00-23:59");
		paraMeterMap.put("CBC_VALUE", "10000");
		paraMeterMap.put("CBC_TYPE", PretupsI.AMOUNT_TYPE_PERCENTAGE);
		paraMeterMap.put("CBC_RATE", "2");
		*/
		return paraMeterMap;
	   }
     
     public Map<String, String> getCommissionProfileMap(String commType) {	
 		Map<String, String> instanceMap = defaultMap(commType);
 		return instanceMap;
     }
     
}
