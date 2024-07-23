/**
 * 
 */
package com.Features.mapclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils._masterVO;

public class Channel2ChannelReturnMap extends BaseTest {

	HashMap<String, String> c2creturnparametermap = new HashMap<>();
	
	 public String toMSISDN = "toMSISDN";
	 public String toTCPName = "toTCPName";
	 public String toCommProfile = "toCommProfile";
	 public String domainCode = "domainCode";
	 public String fromCategoryCode = "fromCategoryCode";
	 public String toGrade = "toGrade";
	 public String toCategory = "toCategory";
	 public String toDomain = "toDomain";
	 public String fromTCPName = "fromTCPName";
	 public String fromTCPID = "fromTCPID";
	 public String fromDomain = "fromDomain";
	 public String fromUserName = "fromUserName";
	 public String fromRowNum = "fromRowNum";
	 public String fromCommProfile = "fromCommProfile";
	 public String fromGrade = "fromGrade";
	 public String fromPIN = "fromPIN";
	 public String fromLoginID = "fromLoginID";
	 public String fromMSISDN = "fromMSISDN";
	 public String toRowNum = "toRowNum";
	 public String toCategoryCode = "toCategoryCode";
	 public String toTCPID = "toTCPID";
	 public String fromCategory = "fromCategory";
	
	public Object[][] TestDataFeed1() {
		String C2CReturnCode = _masterVO.getProperty("C2CReturnCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();


		/*
		 * Array list to store Categories for which C2C withdraw is allowed
		 */
		ArrayList<String> alist1 = new ArrayList<String>();
		ArrayList<String> alist2 = new ArrayList<String>();
		ArrayList<String> alist3 = new ArrayList<String>();
		ArrayList<String> categorySize = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(C2CReturnCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
				alist3.add(ExcelUtility.getCellData(0, ExcelI.FROM_DOMAIN, i));
			}
		}

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();

		/*
		 * Calculate the Count of Users for each category
		 */
		int totalObjectCounter = 0;
		for (int i=0; i<alist1.size(); i++) {
			int categorySizeCounter = 0;
			for (int excelCounter=0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,excelCounter).equals(alist1.get(i))){
					categorySizeCounter++;
				}
			}
			categorySize.add(""+categorySizeCounter);
			totalObjectCounter = totalObjectCounter + categorySizeCounter;
		}

		/*
		 * Counter to count number of users exists in channel users hierarchy sheet 
		 * of Categories for which C2C Withdraw is allowed
		 */

		Object[][] Data = new Object[totalObjectCounter][8];
		Object[][] Data1 = new Object[1][8];

		for(int j=0, k=0;j<alist1.size();j++){

			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int excelRowSize = ExcelUtility.getRowCount();
			String ChannelUserPIN = null; String fromMSISDN = null; int fromRowNum=0;
			for(int i=1;i<=excelRowSize;i++){
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(alist2.get(j))){
					ChannelUserPIN = ExcelUtility.getCellData(0, ExcelI.PIN, i);
					fromMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
					fromRowNum = i;
					break;
				}
			}

			for(int excelCounter=1; excelCounter <=excelRowSize; excelCounter++){
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,excelCounter).equals(alist1.get(j))){
					Data[k][0] = alist2.get(j);
					Data[k][1] = alist1.get(j);
					Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
					Data[k][3] = ChannelUserPIN;
					Data[k][4] = alist3.get(j);
					Data[k][5] = fromMSISDN;
					Data[k][6] = fromRowNum;
					Data[k][7] = excelCounter;
					k++;
				}
			}



			Data1[0][0] = Data[0][0];
			Data1[0][1] = Data[0][1];
			Data1[0][2] = Data[0][2];
			Data1[0][3] = Data[0][3];
			Data1[0][4] = Data[0][4];
			Data1[0][5] = Data[0][5];
			Data1[0][6] = Data[0][6];
			Data1[0][7] = Data[0][7];
		}
		return Data1;
	}

	
	public HashMap<String,String> defaultMap(){
		Object[][] datac2c = TestDataFeed1();
		
		String masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
		c2creturnparametermap.put(fromCategory, datac2c[0][0].toString());
		c2creturnparametermap.put(toCategory, datac2c[0][1].toString());
		c2creturnparametermap.put(toMSISDN, datac2c[0][2].toString());
		c2creturnparametermap.put(fromPIN, datac2c[0][3].toString());
		c2creturnparametermap.put(fromDomain, datac2c[0][4].toString());
		c2creturnparametermap.put(fromMSISDN, datac2c[0][5].toString());
		c2creturnparametermap.put(fromRowNum, datac2c[0][6].toString());
		c2creturnparametermap.put(toRowNum, datac2c[0][7].toString());
		
		int fromrownum = Integer.parseInt(c2creturnparametermap.get(fromRowNum));
		int torownum = Integer.parseInt(c2creturnparametermap.get(toRowNum));
		c2creturnparametermap.put(fromTCPName, ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, fromrownum));
		c2creturnparametermap.put(fromTCPID, ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, fromrownum));
		c2creturnparametermap.put(toTCPName, ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, torownum));
		c2creturnparametermap.put(toTCPID, ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, torownum));
		c2creturnparametermap.put(toDomain, ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, torownum));
		c2creturnparametermap.put(fromLoginID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, fromrownum));
		c2creturnparametermap.put(fromUserName, ExcelUtility.getCellData(0, ExcelI.USER_NAME, fromrownum));
		c2creturnparametermap.put(fromCategoryCode, ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, fromrownum));
		c2creturnparametermap.put(toCategoryCode, ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE,torownum));
		c2creturnparametermap.put(fromGrade, ExcelUtility.getCellData(0, ExcelI.GRADE,fromrownum));
		c2creturnparametermap.put(toGrade, ExcelUtility.getCellData(0, ExcelI.GRADE,torownum));
		c2creturnparametermap.put(toCommProfile, ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, torownum));
		c2creturnparametermap.put(fromCommProfile, ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, fromrownum));
		c2creturnparametermap.put(domainCode,ExtentI.getValueofCorrespondingColumns(ExcelI.CHANNEL_USER_CATEGORY_SHEET, ExcelI.DOMAIN_CODE, new String[]{ExcelI.DOMAIN_NAME}, new String[]{c2creturnparametermap.get("fromDomain")}));
		
		
		
		return c2creturnparametermap;
		
	}
	
	public String getC2CReturnMap(String Key) {	
		Map<String, String> instanceMap = defaultMap();
		return instanceMap.get(Key);
}
	
	public Map<String, String> setC2CReturnMap(String Key, String Value) {	
		Map<String, String> instanceMap = defaultMap();
		instanceMap.put(Key, Value);
		return instanceMap;
}

}
