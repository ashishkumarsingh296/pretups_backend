package com.testscripts.prerequisites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CTransferSpring;
import com.Features.ChannelUser;
import com.classes.BaseTest;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class PreRequisite_Channel2ChannelTransferSpring extends BaseTest {

	HashMap<String, String> c2cMap=new HashMap<String, String>();
	HashMap<String, String> channelMap=new HashMap<>();
	static boolean TestCaseCounter = false;
	
	@Test(dataProvider = "categoryData")
	public void C2CTransfer1(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, int RowNum) throws InterruptedException {
		
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[Pre-Requisite]C2C Transfer");
			TestCaseCounter = true;
		}
		
		C2CTransferSpring c2cTransferSpring= new C2CTransferSpring(driver);
		ChannelUser channelUser= new ChannelUser(driver);
		currentNode=test.createNode("To verify C2C Transfer from Category: "+FromCategory+" to Category(Parent Category): "+ToCategory+"("+ParentCategory+")"+" is successful.");
		currentNode.assignCategory("Pre-Requisite");
		
	if(FromCategory.equals(ToCategory))
	{   channelMap=channelUser.channelUserInitiate(RowNum, Domain, ParentCategory, ToCategory, geoType);
		String APPLEVEL = DBHandler.AccessHandler.getSystemPreference("USER_APPROVAL_LEVEL");
		if(APPLEVEL.equals("2"))
		{channelUser.approveLevel1_ChannelUser();
		channelUser.approveLevel2_ChannelUser();
		}
		else if(APPLEVEL.equals("1")){
			channelUser.approveLevel1_ChannelUser();	
		}else{
			Log.info("Approval not required.");	
		}
		toMSISDN =channelMap.get("MSISDN");
	}
    c2cMap=c2cTransferSpring.channel2channelTransfer(FromCategory, ToCategory, toMSISDN, FromPIN);
	}
	
	@DataProvider(name = "categoryData")
    public Object[][] TestDataFeed1() {
          String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");
          String MasterSheetPath = _masterVO.getProperty("DataProvider");

          ExcelUtility.setExcelFile(MasterSheetPath, "Transfer Rule Sheet");
          int rowCount = ExcelUtility.getRowCount();
/*
* Array list to store Categories for which C2C withdraw is allowed
*/
          ArrayList<String> alist1 = new ArrayList<String>();
          ArrayList<String> alist2 = new ArrayList<String>();
          ArrayList<String> categorySize = new ArrayList<String>();
          for (int i = 1; i <= rowCount; i++) {
                ExcelUtility.setExcelFile(MasterSheetPath, "Transfer Rule Sheet");
                String services = ExcelUtility.getCellData(0, "SERVICES", i);
                ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
                if (aList.contains(C2CTransferCode)) {
                      ExcelUtility.setExcelFile(MasterSheetPath,"Transfer Rule Sheet");
                      alist1.add(ExcelUtility.getCellData(0, "TO_CATEGORY", i));
                      alist2.add(ExcelUtility.getCellData(0, "FROM_CATEGORY", i));
                }
          }

        ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
        int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();

        /*
		 * Calculate the Count of Users for each category
		 */
          int totalObjectCounter = 0;
          for (int i=0; i<alist1.size(); i++) {
        	  int categorySizeCounter = 0;
        	  for (int excelCounter=0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
        		  if(ExcelUtility.getCellData(0, "CATEGORY_NAME",excelCounter).equals(alist1.get(i))){
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
          
          for(int j=0, k=0;j<alist1.size();j++){
        	  
        	  ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
  	  		  int excelRowSize = ExcelUtility.getRowCount();
  	  		  String ChannelUserPIN = null;
              for(int i=1;i<=excelRowSize;i++){
                  if(ExcelUtility.getCellData(0, "CATEGORY_NAME",i).equals(alist2.get(j))){
                	  	ChannelUserPIN = ExcelUtility.getCellData(0, "PIN", i);
                        break;
                        }
              }
        	  
        	  		for(int excelCounter=1; excelCounter <=excelRowSize; excelCounter++){
                        if(ExcelUtility.getCellData(0, "CATEGORY_NAME",excelCounter).equals(alist1.get(j))){
                              Data[k][0] = alist2.get(j);
                              Data[k][1] = alist1.get(j);
                              Data[k][2] = ExcelUtility.getCellData(0, "MSISDN", excelCounter);
                              Data[k][3] = ChannelUserPIN;
                              Data[k][4]= ExcelUtility.getCellData(0, "DOMAIN_NAME", excelCounter);
                              Data[k][5]= ExcelUtility.getCellData(0,"PARENT_CATEGORY_NAME",excelCounter);
                              Data[k][6]= ExcelUtility.getCellData(0,"GRPH_DOMAIN_TYPE",excelCounter);
                              Data[k][7]= excelCounter;
                              k++;
                              }
                        }

          }                       
            
          return Data;
    }
	
}
