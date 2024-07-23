package com.Features.mapclasses;

import java.util.HashMap;
import java.util.Map;

import com.classes.UserAccess;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class UserBalanceMap {


	Map<String, String> map = new HashMap<>();

	public UserBalanceMap() {
		map = defaultMap();
	}

	public Map<String,String> defaultMap(){



		String MasterSheetPath = _masterVO.getProperty("DataProvider");


		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		String parentCategory = null;
		String category = null;
		for(int i = 0; i+2<=rowCount; i++){

			if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i+1) == ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i+2))
			{
				parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i+2);
				category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i+2);
				break;
			}


		}

		Map<String, String> channelUserMap = UserAccess.getChannelUserDetails(parentCategory, category);


		Map<String, String> paraMeterMap = new HashMap<>();


		paraMeterMap.put("MSISDN",channelUserMap.get("MSISDN"));

		paraMeterMap.put("LOGINID",channelUserMap.get("LOGIN_ID"));

		paraMeterMap.put("CATEGORY",channelUserMap.get("CATEGORY"));

		paraMeterMap.put("USER_NAME",channelUserMap.get("USER_NAME"));

		return paraMeterMap;

	}

	public Map<String, String> getOperatorUserMap(String Key, String Value) {
		Map<String, String> m= new HashMap<>(this.map);
		m.put(Key, Value);
		return m;
	}
}
