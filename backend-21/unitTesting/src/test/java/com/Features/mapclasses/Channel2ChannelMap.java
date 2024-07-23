/**
 * 
 */
package com.Features.mapclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class Channel2ChannelMap extends BaseTest{

	HashMap<String, String> c2cparametermap = new HashMap<>();
	
	public Object[][] TestDataFeed1() {
          String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");
          String masterSheetPath = _masterVO.getProperty("DataProvider");
          ChannelUserMap chnlUsrMap = new ChannelUserMap();
          ExcelUtility.setExcelFile(masterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
          int rowCount = ExcelUtility.getRowCount();
/*
* Array list to store Categories for which C2C withdraw is allowed
*/
          ArrayList<String> alist1 = new ArrayList<String>();
          ArrayList<String> alist2 = new ArrayList<String>();
          for (int i = 1; i <= rowCount; i++) {
                ExcelUtility.setExcelFile(masterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
                String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
                ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
                if (aList.contains(C2CTransferCode)) {
                      ExcelUtility.setExcelFile(masterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
                      alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
                      alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
                }
          }
                            
/*
* Counter to count number of users exists in channel users hierarchy sheet 
* of Categories for which C2C Withdraw is allowed
*/
          ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
          int chnlCount = ExcelUtility.getRowCount();
          
          Object[][] data = new Object[alist1.size()][8];

          for(int j=0,p =0;p<alist1.size();p++){
        	  if(!alist2.get(p).equals(alist1.get(p))){
                data[j][0] = alist2.get(p);
                data[j][1] = alist1.get(p);
          
                for(int i=1;i<=chnlCount;i++){
                      if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(data[j][1])){
                            data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                            data[j][7] = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
                            data[j][6] =i;
                            break;}
                      }
                
                for(int i=1;i<=chnlCount;i++){
                      if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(data[j][0])){
                            data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                            data[j][4] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                            data[j][5] = i;
                            break;}
                }
          j++;}}   
        		  Object[][] data1 = new Object[1][9];
			         data1[0][0] = data[0][0];
			         data1[0][1] = data[0][1];
			         data1[0][2] = data[0][2];
			         data1[0][3] = data[0][3];
			         data1[0][4] = data[0][4];
			         data1[0][5] = data[0][5];
			         data1[0][6] = data[0][6];
			         data1[0][7] = chnlUsrMap.getChannelUserMap(null, null);
			         data1[0][8] = data[0][7];
                return data1;
    }
	
	public HashMap<String,String> defaultMap(){
		Object[][] datac2c = TestDataFeed1();
		
		String masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
		c2cparametermap.put("fromCategory", datac2c[0][0].toString());
		c2cparametermap.put("toCategory", datac2c[0][1].toString());
		c2cparametermap.put("toMSISDN", datac2c[0][2].toString());
		c2cparametermap.put("fromPIN", datac2c[0][3].toString());
		c2cparametermap.put("fromMSISDN", datac2c[0][4].toString());
		c2cparametermap.put("fromRowNum", datac2c[0][5].toString());
		c2cparametermap.put("toRowNum", datac2c[0][6].toString());
		c2cparametermap.put("toUserName", datac2c[0][8].toString());
		
		int fromrownum = Integer.parseInt(c2cparametermap.get("fromRowNum"));
		int torownum = Integer.parseInt(c2cparametermap.get("toRowNum"));
		c2cparametermap.put("fromTCPName", ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, fromrownum));
		c2cparametermap.put("fromTCPID", ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, fromrownum));
		c2cparametermap.put("fromDomain", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, fromrownum));
		c2cparametermap.put("toTCPName", ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, torownum));
		c2cparametermap.put("toTCPID", ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, torownum));
		c2cparametermap.put("toDomain", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, torownum));
		c2cparametermap.put("fromLoginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, fromrownum));
		c2cparametermap.put("fromUserName", ExcelUtility.getCellData(0, ExcelI.USER_NAME, fromrownum));
		c2cparametermap.put("fromCategoryCode", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, fromrownum));
		c2cparametermap.put("toCategoryCode", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE,torownum));
		c2cparametermap.put("channeltcpID", ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID,torownum));
		c2cparametermap.put("fromGrade", ExcelUtility.getCellData(0, ExcelI.GRADE,fromrownum));
		c2cparametermap.put("toGrade", ExcelUtility.getCellData(0, ExcelI.GRADE,torownum));
		c2cparametermap.put("toCommProfile", ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, torownum));
		c2cparametermap.put("fromCommProfile", ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, fromrownum));
		c2cparametermap.put("domainCode",ExtentI.getValueofCorrespondingColumns(ExcelI.CHANNEL_USER_CATEGORY_SHEET, ExcelI.DOMAIN_CODE, new String[]{ExcelI.DOMAIN_NAME}, new String[]{c2cparametermap.get("fromDomain")}));
		
		
		
		return c2cparametermap;
		
	}
	
	public String getC2CMap(String Key) {	
		Map<String, String> instanceMap = defaultMap();
		return instanceMap.get(Key);
}
	
	public Map<String, String> setC2CMap(String Key, String Value) {	
		Map<String, String> instanceMap = defaultMap();
		instanceMap.put(Key, Value);
		return instanceMap;
}
	
	@Test
	public void test(){
		Log.info(""+defaultMap());
	}
	
}
