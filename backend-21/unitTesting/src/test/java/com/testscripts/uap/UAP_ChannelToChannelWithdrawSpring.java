package com.testscripts.uap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CWithdrawSpring;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class UAP_ChannelToChannelWithdrawSpring extends BaseTest{
	
	HashMap<String, String> c2cWithdrawMap=new HashMap<String, String>();
	static boolean TestCaseCounter = false;	
		
		@Test(dataProvider = "categoryData")
		public void C2CWithdraw1Spring(String ToCategory, String FromCategory, String fromMSISDN, String toPIN) throws InterruptedException {
			C2CWithdrawSpring c2cWithdrawSpring= new C2CWithdrawSpring(driver);
			SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
			ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
			
			if(TestCaseCounter == false){
			test=extent.createTest("[UAP]C2C Withdraw");
			TestCaseCounter=true;}
			
			//Test Case : C2C Withdraw
			currentNode=test.createNode("To verify C2C Withdraw from "+FromCategory+" to "+ToCategory+" .");
			currentNode.assignCategory("UAP");
			c2cWithdrawMap=c2cWithdrawSpring.channel2channelWithdrawSpring(ToCategory, FromCategory, fromMSISDN, toPIN);

			//Test Case : Message validation
			currentNode=test.createNode("To verify that valid message appears on C2C Withdraw from "+FromCategory+" to "+ToCategory+" .");
			currentNode.assignCategory("UAP");
			Validator.messageCompare(c2cWithdrawMap.get("actualMessage"), c2cWithdrawMap.get("expectedMessage"));

			//Test Case: If Sender is suspended
			currentNode=test.createNode("To verify that C2C Withdraw is not successful if Sender channel user is suspended.");
			currentNode.assignCategory("UAP");
			suspendCHNLUser.suspendChannelUser_MSISDN(fromMSISDN, "Automation Remarks");
			suspendCHNLUser.approveCSuspendRequest_MSISDN(fromMSISDN, "Automation remarks");
			try{
				c2cWithdrawSpring.channel2channelWithdrawSpring(ToCategory, FromCategory, fromMSISDN, toPIN);
				currentNode.log(Status.FAIL, "C2C Withdraw is successful.");}
			catch(Exception e){
				String actualMessage = driver.findElement(By.xpath("//*[@class='errorClass']")).getText();
				String expectedMessage = MessagesDAO.prepareMessageByKey("pretups.message.channeltransfer.usersuspended.msg", fromMSISDN);
				Log.info(" Message fetched from WEB as : "+actualMessage);
				Validator.messageCompare(actualMessage, expectedMessage);
				}
			resumeCHNLUser.resumeChannelUser_MSISDN(fromMSISDN, "Auto Resume Remarks");
			
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
