package com.Features.mapclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class C2CWithdrawSpringMap {
	
	
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String,String> defaultMap(){
		HashMap<String, String> c2cparametermap = new HashMap<>();
        String C2CWithdrawCode = _masterVO.getProperty("C2CWithdrawCode");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        int rowCount = ExcelUtility.getRowCount();

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
                          
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int chnlCount = ExcelUtility.getRowCount();
        
        Object[][] Data = new Object[alist1.size()][6];
        
        for(int j=0;j<alist1.size();j++){
              Data[j][0] = alist2.get(j);
              Data[j][1] = alist1.get(j);
              
              for(int i=1;i<=chnlCount;i++){
                    if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(Data[j][1])){
                          Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i); 
                          break;}
                    }
              
              for(int i=1;i<=chnlCount;i++){
                    if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(Data[j][0])){
                          Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                          break;}
              }
              for(int i=1;i<=chnlCount;i++){
                  if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(Data[j][1])){
                        Data[j][4] = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
                        break;}
            }
             for(int i=1;i<=chnlCount;i++){
                 
                        Data[j][5] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
                        break;
            }
              break;
        }            
		
		c2cparametermap.put("fromCategory", Data[0][0].toString());
		c2cparametermap.put("toCategory", Data[0][1].toString());
		c2cparametermap.put("toMSISDN", Data[0][2].toString());
		c2cparametermap.put("PIN", Data[0][3].toString());
		c2cparametermap.put("userName", Data[0][4].toString());
		c2cparametermap.put("quantity","2");
		c2cparametermap.put("senderSuspended", "N");
		c2cparametermap.put("outSuspended", "N");
		c2cparametermap.put("domainName", Data[0][5].toString());
		
		return c2cparametermap;
		
	}
	
	
	public Map<String, String> setC2CMap(String Key, String Value) {	
		Map<String, String> instanceMap = defaultMap();
		instanceMap.put(Key, Value);
		return instanceMap;
}
	
}
