package com.Features.mapclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils._masterVO;

/**
 * 
 * @author yogesh.keshari
 *
 */
public class Channel2ChannelMapSpring {

	HashMap<String, String> c2cparametermap = new HashMap<>();
	Map<String, String> map = new HashMap<>();
	
	public Channel2ChannelMapSpring() {
		map = defaultMap();
	}
	
	
	public Object[][] TestDataFeed1() {
          String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");//C2CT
          String masterSheetPath = _masterVO.getProperty("DataProvider");//DataProvider.xlsx
          ChannelUserMap chnlUsrMap = new ChannelUserMap();
          ExcelUtility.setExcelFile(masterSheetPath, ExcelI.TRANSFER_RULE_SHEET);//Transfer Rule Sheet
          int rowCount = ExcelUtility.getRowCount();

 /* Array list to store FromCategories and ToCategory for which C2C transfer is allowed*/

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
          
          Object[][] data = new Object[alist1.size()][7];
          
          for(int j=0;j<alist1.size();j++){
                data[j][0] = alist2.get(j);
                data[j][1] = alist1.get(j);
                
                for(int i=1;i<=chnlCount;i++){
                      if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(data[j][1])){
                            data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i); 
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
          }   
        		  Object[][] data1 = new Object[1][8];
			         data1[0][0] = data[0][0];
			         data1[0][1] = data[0][1];
			         data1[0][2] = data[0][2];
			         data1[0][3] = data[0][3];
			         data1[0][4] = data[0][4];
			         data1[0][5] = data[0][5];
			         data1[0][6] = data[0][6];
			         data1[0][7] = chnlUsrMap.getChannelUserMap(null, null);
                return data1;
    }
	
	public HashMap<String,String> defaultMap(){
		
		Object[][] datac2c = TestDataFeed1();
		HashMap<String, String> c2cmap = new HashMap<>();
		String masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
		c2cmap.put("fromCategory", datac2c[0][0].toString());
		c2cmap.put("toCategory", datac2c[0][1].toString());
		c2cmap.put("toMSISDN", datac2c[0][2].toString());
		c2cmap.put("fromPIN", datac2c[0][3].toString());
		c2cmap.put("fromMSISDN", datac2c[0][4].toString());
		c2cmap.put("fromRowNum", datac2c[0][5].toString());
		c2cmap.put("toRowNum", datac2c[0][6].toString());
		
		int fromrownum = Integer.parseInt(c2cmap.get("fromRowNum"));
		int torownum = Integer.parseInt(c2cmap.get("toRowNum"));
		c2cmap.put("fromTCPName", ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, fromrownum));
		c2cmap.put("fromTCPID", ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, fromrownum));
		c2cmap.put("fromDomain", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, fromrownum));
		c2cmap.put("toTCPName", ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, torownum));
		c2cmap.put("toTCPID", ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, torownum));
		c2cmap.put("toDomain", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, torownum));
		c2cmap.put("fromLoginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, fromrownum));
		c2cmap.put("fromCategoryCode", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, fromrownum));
		c2cmap.put("toCategoryCode", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE,torownum));
		c2cmap.put("channeltcpID", ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID,torownum));
		c2cmap.put("fromGrade", ExcelUtility.getCellData(0, ExcelI.GRADE,fromrownum));
		c2cmap.put("toGrade", ExcelUtility.getCellData(0, ExcelI.GRADE,torownum));
		c2cmap.put("toCommProfile", ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, torownum));
		c2cmap.put("fromCommProfile", ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, fromrownum));
		c2cmap.put("domainCode",DBHandler.AccessHandler.getDomainCode(c2cmap.get("fromDomain")));
		c2cmap.put("quantity","2");
		c2cmap.put("toUser", ExcelUtility.getCellData(0, ExcelI.USER_NAME, fromrownum));
		c2cmap.put("remarks","Remarks for C2C");
		
		
		return c2cmap;
		
	}
	
	public Map<String, String> getC2CMap() {	
		/*Map<String, String> instanceMap = defaultMap();
		return instanceMap;*/
		Map<String, String> m= new HashMap<>(this.map);
		return m;
}
	
	public String getC2CMap(String Key) {	
		/*Map<String, String> instanceMap = defaultMap();
		return instanceMap.get(Key);*/
		return this.map.get(Key);
}
	
	public Map<String, String> getC2CMap(String Key, String Value) {
		Map<String, String> m= new HashMap<>(this.map);
		//Map<String, String> instanceMap = this.map;
		m.put(Key, Value);
		return m;
}
	
	
	
}
