package com.testscripts.sit;


	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.HashMap;

	import org.openqa.selenium.By;
	import org.testng.annotations.DataProvider;
	import org.testng.annotations.Test;

	import com.Features.C2CReturn;
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

	public class SIT_C2C_Return  extends BaseTest {

		HashMap<String, String> c2cMap=new HashMap<String, String>();
		static boolean TestCaseCounter = false;
		
		@Test(dataProvider = "categoryData")
		public void C2CReturn(String FromCategory, String ToCategory, String toMSISDN, String FromPIN) throws InterruptedException, IOException {
			C2CReturn c2cReturn= new C2CReturn(driver);
			SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
			ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
			
			Log.startTestCase(this.getClass().getName());
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]C2C Return");
				TestCaseCounter = true;
			}
			
			/*
			 * Test Case Number 1: To initiate C2C Transfer
			 */
			currentNode=test.createNode("To verify C2C Return from "+FromCategory+" to "+ToCategory+" .");
			currentNode.assignCategory("SIT");
			c2cMap=c2cReturn.channel2channelReturn(FromCategory, ToCategory, toMSISDN, FromPIN);

			/*
			 * Test Case Number 2: Message Validation
			 */
			currentNode=test.createNode("To verify that valid message appears on C2C Return from "+FromCategory+" to "+ToCategory+" .");
			currentNode.assignCategory("SIT");
			Validator.messageCompare(c2cMap.get("actualMessage"), c2cMap.get("expectedMessage"));
			
			/*
			 * Test Case: If receiver user is suspended
			 */
			currentNode=test.createNode("To verify that C2C Return is not successful if Receiver channel user is suspended.");
			currentNode.assignCategory("SIT");
			suspendCHNLUser.suspendChannelUser_MSISDN(toMSISDN, "Automation Remarks");
			suspendCHNLUser.approveCSuspendRequest_MSISDN(toMSISDN, "Automation remarks");
			try{
				c2cReturn.channel2channelReturn(FromCategory, ToCategory, toMSISDN, FromPIN);
				currentNode.log(Status.FAIL, "C2C Transfer is successful.");}
			catch(Exception e){
				String actualMessage = driver.findElement(By.xpath("//ul/li")).getText();
				String expectedMessage = MessagesDAO.prepareMessageByKey("message.channeltransfer.usersuspended.msg", toMSISDN);
				Log.info(" Message fetched from WEB as : "+actualMessage);
				Validator.messageCompare(actualMessage, expectedMessage);
				}
			resumeCHNLUser.resumeChannelUser_MSISDN(toMSISDN, "Auto Resume Remarks");
			
			
			Log.endTestCase(this.getClass().getName());
		}
		
		@DataProvider(name = "categoryData")
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
	          ArrayList<String> categorySize = new ArrayList<String>();
	          for (int i = 1; i <= rowCount; i++) {
	                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
	                String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
	                ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
	                if (aList.contains(C2CReturnCode)) {
	                      ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
	                      alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
	                      alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
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
	          
	          Object[][] Data = new Object[totalObjectCounter][4];
	          
	          for(int j=0, k=0;j<alist1.size();j++){
	        	  
	        	  ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	  	  		  int excelRowSize = ExcelUtility.getRowCount();
	  	  		  String ChannelUserPIN = null;
	              for(int i=1;i<=excelRowSize;i++){
	                  if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(alist2.get(j))){
	                	  	ChannelUserPIN = ExcelUtility.getCellData(0, ExcelI.PIN, i);
	                        break;
	                        }
	              }
	        	  
	        	  		for(int excelCounter=1; excelCounter <=excelRowSize; excelCounter++){
	                        if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,excelCounter).equals(alist1.get(j))){
	                              Data[k][0] = alist2.get(j);
	                              Data[k][1] = alist1.get(j);
	                              Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
	                              Data[k][3] = ChannelUserPIN;
	                              k++;
	                              }
	                        }

	          }                       
	            
	          return Data;
	    }
		
	}



