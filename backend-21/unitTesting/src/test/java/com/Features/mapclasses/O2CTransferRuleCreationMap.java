package com.Features.mapclasses;

import java.util.HashMap;
import java.util.Map;

import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class O2CTransferRuleCreationMap extends BaseTest {

	String MasterSheetPath;
	String FirstApprovalLimit;
	String SecondApprovalLimit;
	Object[][] TransferRuleCategories;
	
	public Map<String, String> defaultMap() {
		
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		Map<String, String> paraMeterMap = new HashMap<>();
		
		int rowCount = ExcelUtility.getRowCount();

		for (int i = 1; i < rowCount; i++) {
			String FromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i);
			if (FromCategory.equals("Operator")) {
				String toDomain = ExcelUtility.getCellData(0, ExcelI.TO_DOMAIN, i);
				String toCategory = ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i);
				String services= ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				paraMeterMap.put("toDomain",toDomain);
				paraMeterMap.put("toCategory", toCategory);
				paraMeterMap.put("services", services);
				paraMeterMap.put("FirstApprovalLimit", _masterVO.getProperty("O2CFirstApprovalLimit"));
				paraMeterMap.put("SecondApprovalLimit", _masterVO.getProperty("O2CSecondApprovalLimit"));
				paraMeterMap.put("isProductAvailable", "Yes");
				break;
			}
		}
		return paraMeterMap;
	}
	
	public Map<String, String> getO2CTransferMap(String Key, String Value) {	
		Map<String, String> instanceMap = defaultMap();
		instanceMap.put(Key, Value);
		return instanceMap;
}
	}
