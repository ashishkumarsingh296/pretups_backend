package com.Features.mapclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class ChnnlChnnlTrfDetailRptMap {

	Map<String, String> map = new HashMap<>();

	public ChnnlChnnlTrfDetailRptMap() {
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

	public Map<String,String> defaultMap(){
		HashMap<String, String> chnnlChnnlTrfDetailRptMap = new HashMap<>();
		Object[][] datac2c = TestDataFeed1();
	
		String masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
		chnnlChnnlTrfDetailRptMap.put("fromCategory", datac2c[0][0].toString());
		chnnlChnnlTrfDetailRptMap.put("toCategory", datac2c[0][1].toString());
		chnnlChnnlTrfDetailRptMap.put("toMSISDN", datac2c[0][2].toString());
		chnnlChnnlTrfDetailRptMap.put("fromPIN", datac2c[0][3].toString());
		chnnlChnnlTrfDetailRptMap.put("fromMSISDN", datac2c[0][4].toString());
		chnnlChnnlTrfDetailRptMap.put("fromRowNum", datac2c[0][5].toString());
		chnnlChnnlTrfDetailRptMap.put("toRowNum", datac2c[0][6].toString());
		
		int fromrownum = Integer.parseInt(chnnlChnnlTrfDetailRptMap.get("fromRowNum"));
		int torownum = Integer.parseInt(chnnlChnnlTrfDetailRptMap.get("toRowNum"));
		chnnlChnnlTrfDetailRptMap.put("fromDomain", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, fromrownum));
		chnnlChnnlTrfDetailRptMap.put("toDomain", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, torownum));
		chnnlChnnlTrfDetailRptMap.put("fromLoginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, fromrownum));
		chnnlChnnlTrfDetailRptMap.put("fromCategoryCode", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, fromrownum));
		chnnlChnnlTrfDetailRptMap.put("toCategoryCode", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE,torownum));
		chnnlChnnlTrfDetailRptMap.put("domainCode",DBHandler.AccessHandler.getDomainCode(chnnlChnnlTrfDetailRptMap.get("fromDomain")));
	    chnnlChnnlTrfDetailRptMap.put("fromUser", ExcelUtility.getCellData(0, ExcelI.USER_NAME, fromrownum));
		
		
		return chnnlChnnlTrfDetailRptMap;
	}

	public Map<String, String> setchnnlChnnlTrfDetailRptMap(String Key, String Value) {	
		Map<String, String> m = new HashMap<>(this.map);
		m.put(Key, Value);
		return m;
	}

	public String getchnnlChnnlTrfDetailRptMap(String Key) {	
		Map<String, String> instanceMap= new HashMap<>(this.map);
		return instanceMap.get(Key);
	}
	
	public Map<String, String> getchnnlChnnlTrfDetailRptMap(){
		Map<String, String> m = new HashMap<>(this.map);
		return m;
	}

}
