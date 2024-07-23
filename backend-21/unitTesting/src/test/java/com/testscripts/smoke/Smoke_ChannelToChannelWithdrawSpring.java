package com.testscripts.smoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CWithdrawSpring;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class Smoke_ChannelToChannelWithdrawSpring extends BaseTest{
	
	
	Map<String, String> c2cWithdrawMap=new HashMap<String, String>();
	static boolean TestCaseCounter = false;	
		
		@Test(dataProvider = "categoryData")
		public void C2CWithdraw2(String ToCategory, String FromCategory, String fromMSISDN, String toPIN) throws InterruptedException {
			/*C2CWithdraw c2cWithdraw= new C2CWithdraw(driver);*/
			C2CWithdrawSpring c2CWithdrawSpring=new C2CWithdrawSpring(driver);
			if(TestCaseCounter == false){
			test=extent.createTest("[Smoke]C2C Withdraw Spring");
			TestCaseCounter=true;}
			
			currentNode=test.createNode("To verify C2C Withdraw from Category(MSISDN): "+FromCategory+"("+fromMSISDN+") to Category: "+ToCategory+" .");
			currentNode.assignCategory("Smoke");
			c2cWithdrawMap=c2CWithdrawSpring.channel2channelWithdrawSpring(ToCategory, FromCategory, fromMSISDN, toPIN);

			currentNode=test.createNode("To verify that valid message appears on C2C Withdraw from "+FromCategory+" to "+ToCategory+" .");
			currentNode.assignCategory("Smoke");
			if(c2cWithdrawMap.get("actualMessage").equals(c2cWithdrawMap.get("expectedMessage")))
			{
			currentNode.log(Status.PASS,"Message validation successful");	
			}
			else {
				currentNode.log(Status.FAIL, "Expected [" + c2cWithdrawMap.get("expectedMessage") + "] but found [" + c2cWithdrawMap.get("actualMessage") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
			
			Log.endTestCase(this.getClass().getName());
		}
		
		@DataProvider(name = "categoryData")
	    public Object[][] TestDataFeed1() {
	          String C2CWithdrawCode = _masterVO.getProperty("C2CWithdrawCode");
	          String MasterSheetPath = _masterVO.getProperty("DataProvider");

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
	                            
	/*
	* Counter to count number of users exists in channel users hierarchy sheet 
	* of Categories for which C2C Withdraw is allowed
	*/
	          ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	          int chnlCount = ExcelUtility.getRowCount();
	          
	          Object[][] Data = new Object[alist1.size()][4];
	          
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
	          }                       
	                return Data;
	    }


}
