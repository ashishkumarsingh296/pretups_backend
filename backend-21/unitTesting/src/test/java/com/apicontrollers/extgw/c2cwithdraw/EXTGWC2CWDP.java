package com.apicontrollers.extgw.c2cwithdraw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.classes.BaseTest;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;

public class EXTGWC2CWDP extends BaseTest {

	public static String CUCategory = null;
	public static String TCPName = null;
	public static String Domain = null;
	public static String ProductCode = null;
	public static String LoginID = null;
	public static String ProductName = null;
	
	public static Object[] getAPIdataWithAllUsers() {
		String C2CWithdrawCode = _masterVO.getProperty("C2CWithdrawCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ArrayList<String> extgwfromcategoryList  = new ArrayList<String>();
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();

		//Checking for categories having EXTGW access
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		int abmsrowCount = ExcelUtility.getRowCount();
		int counter=0;
		for(counter=1;counter<=abmsrowCount;counter++){
			if(ExcelUtility.getCellData(0, ExcelI.EXTGW, counter).equals("Y")&& 
					(ExcelUtility.getCellData(0, ExcelI.EXTGW, counter)!=null ||
					!ExcelUtility.getCellData(0, ExcelI.EXTGW, counter).equals("")))
			{extgwfromcategoryList.add(ExcelUtility.getCellData(0, ExcelI.CATEGORY_USERS, counter));
			}
		}

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		/*
		 * Array list to store Categories for which C2C withdraw is allowed
		 */
		ArrayList<String> alist1 = new ArrayList<String>();
		ArrayList<String> alist2 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(C2CWithdrawCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}

		//Lists combined
		String[][] combination = new String[alist1.size()][2];
		for(int i=0;i<alist1.size();i++){
			combination[i][0]=alist2.get(i);
			combination[i][1]=alist1.get(i);
		}

		//Identify categories having C2CW allowed but no EXTGW access
		ArrayList<String> listtoremove = new ArrayList<String>(); 
		for(int list=0;list<extgwfromcategoryList.size();list++){
			if(!alist2.contains(extgwfromcategoryList.get(list))){
				Log.info("To be Removed: "+extgwfromcategoryList.get(list)+" | "+list);
				listtoremove.add(extgwfromcategoryList.get(list));
			}
			/*			if(listtoremove.size()==extgwfromcategoryList.size()){
				Log.info("No user exist having EXTGW access and C2C Withdraw");
				return apiData;
			}*/
		} 

		extgwfromcategoryList.removeAll(listtoremove);

		int count=0;
		for(int i=0;i<combination.length;i++){
			if(extgwfromcategoryList.contains(combination[i][0])){
				count++;
			}
		}

		//Categories to which C2C withdraw is allowed and having EXTGW access
		String[][] ncombination = new String[count][2];

		for(int i=0,p=0;i<combination.length;i++){
			if(extgwfromcategoryList.contains(combination[i][0])){
				ncombination[p][0]=combination[i][0];
				ncombination[p][1]=combination[i][1];
				p++;
			}
		}
		Log.info("NCombination: "+ncombination);

		int productCount = ExcelUtility.getRowCount(MasterSheetPath, ExcelI.PRODUCT_SHEET);

		for(String x:extgwfromcategoryList){
			Log.info("Required 'From Category' for C2CW : "+x);}

		//Object[] Data = new Object[ncombination.length * productCount]; //to be used for multiuser execution
		Object[] Data = new Object[1*productCount];
		int objCounter = 0;
		/*
		 * Counter to count number of users exists in channel users hierarchy sheet 
		 * of Categories for which C2C Withdraw and EXTGW access is allowed
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();

		//for(int j=0;j<ncombination.length;j++){//to be used for multiuser execution
		for(int j=0;j<1;j++){
			for (int k=0; k<productCount; k++) {
				EXTGW_C2CWDAO C2CDAO = new EXTGW_C2CWDAO();
				HashMap<String, String> dataMap = new HashMap<String, String>();
				//From Category data
				dataMap.put(C2CWithdrawAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
				for(int i=1;i<=chnlCount;i++){
					ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
					if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(ncombination[j][0])){
						dataMap.put(C2CWithdrawAPI.LOGINID, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
						dataMap.put(C2CWithdrawAPI.PASSWORD, ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
						dataMap.put(C2CWithdrawAPI.MSISDN1, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
						dataMap.put(C2CWithdrawAPI.PIN, ExcelUtility.getCellData(0, ExcelI.PIN, i));
						dataMap.put(C2CWithdrawAPI.EXTCODE, ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i));
						break;}

				}
				//To Category data
				for(int i=1;i<=chnlCount;i++){
					if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(ncombination[j][1])){
						dataMap.put(C2CWithdrawAPI.MSISDN2, ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
						dataMap.put(C2CWithdrawAPI.LOGINID2, ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
						dataMap.put(C2CWithdrawAPI.EXTCODE2, ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i));
						break;}
				}

				RandomGeneration RandomGeneration = new RandomGeneration();
				dataMap.put(C2CWithdrawAPI.EXTREFNUM, RandomGeneration.randomNumeric(5));

				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
				dataMap.put(C2CWithdrawAPI.PRODUCTCODE, ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, (k+1)));
				dataMap.put("productCode",ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE,(k+1)));
				dataMap.put(C2CWithdrawAPI.QTY, "60");
				C2CDAO.setApiData(dataMap);
				Data[objCounter] = C2CDAO;
				objCounter++;
			}
		}
		return Data;
	}
	
//######################################################################//
	public static HashMap<String, String> getAPIdata() {
		String C2CWithdrawCode = _masterVO.getProperty("C2CWithdrawCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ArrayList<String> extgwfromcategoryList  = new ArrayList<String>();
		HashMap<String, String> apiData = new HashMap<String, String>();
		EXTGWC2CWAPI C2CWithdrawAPI = new EXTGWC2CWAPI();

		//Checking for categories having EXTGW access
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.ACCESS_BEARER_MATRIX_SHEET);
		int abmsrowCount = ExcelUtility.getRowCount();
		int counter=0;
		for(counter=1;counter<=abmsrowCount;counter++){
			if(ExcelUtility.getCellData(0, ExcelI.EXTGW, counter).equals("Y")&& 
					(ExcelUtility.getCellData(0, ExcelI.EXTGW, counter)!=null ||
					!ExcelUtility.getCellData(0, ExcelI.EXTGW, counter).equals("")))
			{extgwfromcategoryList.add(ExcelUtility.getCellData(0, ExcelI.CATEGORY_USERS, counter));
			}
		}
		if(counter==0){Log.skip("No EXTGW access to any category.");
		return apiData;
		}

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		/*
		 * Array list to store Categories for which C2C withdraw is allowed
		 */
		ArrayList<String> alist1 = new ArrayList<String>();
		ArrayList<String> alist2 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(C2CWithdrawCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}

		//Lists combined
		String[][] combination = new String[alist1.size()][2];
		for(int i=0;i<alist1.size();i++){
			combination[i][0]=alist2.get(i);
			combination[i][1]=alist1.get(i);
		}

		//Identify categories having C2CW allowed but no EXTGW access
		ArrayList<String> listtoremove = new ArrayList<String>(); 
		for(int list=0;list<extgwfromcategoryList.size();list++){
			if(!alist2.contains(extgwfromcategoryList.get(list))){
				Log.info("To be Removed: "+extgwfromcategoryList.get(list)+" | "+list);
				listtoremove.add(extgwfromcategoryList.get(list));
			}
			if(listtoremove.size()==extgwfromcategoryList.size()){
				Log.info("No user exist having EXTGW access and C2C Withdraw");
				return apiData;
			}
		} 

		extgwfromcategoryList.removeAll(listtoremove);

		int count=0;
		for(int i=0;i<combination.length;i++){
			if(extgwfromcategoryList.contains(combination[i][0])){
				count++;
			}
		}

		//Categories to which C2C withdraw is allowed and having EXTGW access
		String[][] ncombination = new String[count][2];

		for(int i=0,p=0;i<combination.length;i++){
			if(extgwfromcategoryList.contains(combination[i][0])){
				ncombination[p][0]=combination[i][0];
				ncombination[p][1]=combination[i][1];
				p++;
			}
		}
		Log.info(ncombination);

		for(String x:extgwfromcategoryList){
			Log.info("Required 'From Category' for C2CW : "+x);}
		/*
		 * Counter to count number of users exists in channel users hierarchy sheet 
		 * of Categories for which C2C Withdraw and EXTGW access is allowed
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[ncombination.length][10];

		for(int j=0;j<ncombination.length;j++){
			//From Category data  
			Data[j][0] = ncombination[j][0];
			for(int i=1;i<=chnlCount;i++){
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(Data[j][0])){
					Data[j][1] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
					Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
					Data[j][3] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
					Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
					Data[j][5] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i);
					break;}
			}

			//To Category data
			Data[j][6] = ncombination[j][1];
			for(int i=1;i<=chnlCount;i++){
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(Data[j][6])){
					Data[j][7] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
					Data[j][8] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
					Data[j][9] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i);
					break;}
			}
		}

		Log.info("Data fetched for C2C Withdraw: "+Data);

		apiData.put(C2CWithdrawAPI.EXTNWCODE, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		apiData.put(C2CWithdrawAPI.LOGINID,String.valueOf(Data[0][1]));
		apiData.put(C2CWithdrawAPI.PASSWORD, _APIUtil.implementEncryption(String.valueOf(Data[0][2])));
		apiData.put(C2CWithdrawAPI.MSISDN1, String.valueOf(Data[0][3]));
		apiData.put(C2CWithdrawAPI.PIN, _APIUtil.implementEncryption(String.valueOf(Data[0][4])));
		apiData.put(C2CWithdrawAPI.EXTCODE, String.valueOf(Data[0][5]));
		apiData.put(C2CWithdrawAPI.MSISDN2, String.valueOf(Data[0][7]));
		apiData.put(C2CWithdrawAPI.LOGINID2, String.valueOf(Data[0][8]));
		apiData.put(C2CWithdrawAPI.EXTCODE2, String.valueOf(Data[0][9]));

		RandomGeneration RandomGeneration = new RandomGeneration();
		apiData.put(C2CWithdrawAPI.EXTREFNUM, RandomGeneration.randomNumeric(5));
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
		
		int rows = ExcelUtility.getRowCount();
		for(int i=1;i<=rows;i++){
			apiData.put(C2CWithdrawAPI.PRODUCTCODE+"["+i+"]", ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i));
			apiData.put("productCode["+i+"]", ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i));
		}
		
		ProductName = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, 1);
		apiData.put(C2CWithdrawAPI.QTY, "60");
		apiData.put("productCount", String.valueOf(rows));
		return apiData;
	}

}
