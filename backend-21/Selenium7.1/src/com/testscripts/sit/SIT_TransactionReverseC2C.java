package com.testscripts.sit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CTransfer;
import com.Features.ChannelUser;
import com.Features.TransactionReverseC2C;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_TransactionReverseC2C extends BaseTest { 

	HashMap<String, String> c2cMap=new HashMap<String, String>();
	HashMap<String, String> channelMap=new HashMap<>();
	static boolean TestCaseCounter = false;
	String txnID;
	
	@Test(dataProvider = "categoryData")
	public void a_C2CTransactionReverseWithRecieverMSISDN(String FromCategory, String ToCategory, String toMSISDN, String FromPIN,String Domain, String ParentCategory, String geoType, int RowNum) throws InterruptedException, IOException {
		C2CTransfer c2cTransfer= new C2CTransfer(driver);
		TransactionReverseC2C TransactionReverseC2C = new TransactionReverseC2C(driver);
		ChannelUser channelUser= new ChannelUser(driver);
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2C Transfer");
			TestCaseCounter = true;
		}
		
		/*
		 * Test Case Number 1: To initiate C2C Transfer
		 */
		currentNode=test.createNode("To verify C2C Transfer from "+FromCategory+" to "+ToCategory+" .");
		currentNode.assignCategory("SIT");
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
		c2cMap=c2cTransfer.channel2channelTransfer(FromCategory, ToCategory, toMSISDN, FromPIN);

    	currentNode=test.createNode("To verify that valid message appears on C2C Transfer from "+FromCategory+" to "+ToCategory+" .");
		currentNode.assignCategory("SIT");
		Validator.messageCompare(c2cMap.get("actualMessage"), c2cMap.get("expectedMessage"));
		
		txnID = c2cMap.get("TransactionID");
		
		currentNode=test.createNode("To verify that C2C Transaction Reverse is successful from "+FromCategory+" to "+ToCategory+" .");
		currentNode.assignCategory("SIT");
		String actual = TransactionReverseC2C.initiateC2CTxnReverse(txnID,toMSISDN);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		int totalRow1 = ExcelUtility.getRowCount();
		int i=1;
		for( i=1; i<=totalRow1;i++)
		{
			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))
			break;
		}
		System.out.println(i);
		String SenderUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
		System.out.println(SenderUserName);
		
		int k=1;
		for( k=1; k<=totalRow1;k++)
		{			if((ExcelUtility.getCellData(0, ExcelI.MSISDN, k).matches(toMSISDN)))

			break;
		}
		System.out.println(k);
		String RecieverUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, k);
	
		String expected = MessagesDAO.prepareMessageByKey("channelreversetrx.reverse.msg.success",null, txnID,SenderUserName,RecieverUserName);
		Validator.messageCompare(actual, expected);
			}
	
	
	

	
	@Test(dataProvider = "categoryData")
	public void b_C2CTransactionReverseWithSenderMSISDN(String FromCategory, String ToCategory, String toMSISDN, String FromPIN,String Domain, String ParentCategory, String geoType, int RowNum) throws InterruptedException, IOException {
		C2CTransfer c2cTransfer= new C2CTransfer(driver);
		TransactionReverseC2C TransactionReverseC2C = new TransactionReverseC2C(driver);
		ChannelUser channelUser= new ChannelUser(driver);
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2C Transfer");
			TestCaseCounter = true;
		}
		
		/*
		 * Test Case Number 1: To initiate C2C Transfer
		 */
		currentNode=test.createNode("To verify C2C Transfer from "+FromCategory+" to "+ToCategory+" .");
		currentNode.assignCategory("SIT");
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
		c2cMap=c2cTransfer.channel2channelTransfer(FromCategory, ToCategory, toMSISDN, FromPIN);

		currentNode=test.createNode("To verify that valid message appears on C2C Transfer from "+FromCategory+" to "+ToCategory+" .");
		currentNode.assignCategory("SIT");
		Validator.messageCompare(c2cMap.get("actualMessage"), c2cMap.get("expectedMessage"));
		
		txnID = c2cMap.get("TransactionID");
		
		currentNode=test.createNode("To verify that C2C Transaction Reverse is successful from "+FromCategory+" to "+ToCategory+" .");
		currentNode.assignCategory("SIT");
		
		String actual = TransactionReverseC2C.initiateC2CTxnReverseWithSenderMSISDN(FromCategory, txnID);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
		int totalRow1 = ExcelUtility.getRowCount();
		int i=1;
		for( i=1; i<=totalRow1;i++)
		{
			System.out.println(FromCategory);
			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))

			break;
		}
		System.out.println(i);
		String SenderUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
		System.out.println(SenderUserName);
		
		int k=1;
		for( k=1; k<=totalRow1;k++)
		{			if((ExcelUtility.getCellData(0, ExcelI.MSISDN, k).matches(toMSISDN)))

			break;
		}
		System.out.println(k);
		String RecieverUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, k);
		System.out.println(RecieverUserName);
		String expected = MessagesDAO.prepareMessageByKey("channelreversetrx.reverse.msg.success",null, txnID,SenderUserName,RecieverUserName);
		Validator.messageCompare(actual, expected);
			}
	

	

	@Test(dataProvider = "categoryData")
	public void c_C2CTransactionReverseWithSenderLoginID(String FromCategory, String ToCategory, String toMSISDN, String FromPIN,String Domain, String ParentCategory, String geoType, int RowNum) throws InterruptedException, IOException {
		C2CTransfer c2cTransfer= new C2CTransfer(driver);
		TransactionReverseC2C TransactionReverseC2C = new TransactionReverseC2C(driver);
		ChannelUser channelUser= new ChannelUser(driver);
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2C Transfer");
			TestCaseCounter = true;
		}
		
		/*
		 * Test Case Number 1: To initiate C2C Transfer
		 */
		currentNode=test.createNode("To verify C2C Transfer from "+FromCategory+" to "+ToCategory+" .");
		currentNode.assignCategory("SIT");
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
		c2cMap=c2cTransfer.channel2channelTransfer(FromCategory, ToCategory, toMSISDN, FromPIN);

		currentNode=test.createNode("To verify that valid message appears on C2C Transfer from "+FromCategory+" to "+ToCategory+" .");
		currentNode.assignCategory("SIT");
		Validator.messageCompare(c2cMap.get("actualMessage"), c2cMap.get("expectedMessage"));
		
		txnID = c2cMap.get("TransactionID");
		
		currentNode=test.createNode("To verify that C2C Transaction Reverse is successful from "+FromCategory+" to "+ToCategory+" .");
		currentNode.assignCategory("SIT");
		
		String actual = TransactionReverseC2C.initiateC2CTxnReverseWithSenderLoginID(FromCategory, txnID);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
		int totalRow1 = ExcelUtility.getRowCount();
		int i=1;
		for( i=1; i<=totalRow1;i++)
		{
			System.out.println(FromCategory);
			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))

			break;
		}
		System.out.println(i);
		String SenderUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
		System.out.println(SenderUserName);
		
		int k=1;
		for( k=1; k<=totalRow1;k++)
		{			if((ExcelUtility.getCellData(0, ExcelI.MSISDN, k).matches(toMSISDN)))

			break;
		}
		System.out.println(k);
		String RecieverUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, k);
		System.out.println(RecieverUserName);
		String expected = MessagesDAO.prepareMessageByKey("channelreversetrx.reverse.msg.success",null, txnID,SenderUserName,RecieverUserName);
		Validator.messageCompare(actual, expected);
			}
	


	
	
	@Test(dataProvider = "categoryData")
	public void d_C2CTransactionReverseWithDomainCode(String FromCategory, String ToCategory, String toMSISDN, String FromPIN,String Domain, String ParentCategory, String geoType, int RowNum) throws InterruptedException, IOException {
		C2CTransfer c2cTransfer= new C2CTransfer(driver);
		TransactionReverseC2C TransactionReverseC2C = new TransactionReverseC2C(driver);
		ChannelUser channelUser= new ChannelUser(driver);
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]C2C Transfer");
			TestCaseCounter = true;
		}
		
		/*
		 * Test Case Number 1: To initiate C2C Transfer
		 */
		currentNode=test.createNode("To verify C2C Transfer from "+FromCategory+" to "+ToCategory+" .");
		currentNode.assignCategory("SIT");
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
		c2cMap=c2cTransfer.channel2channelTransfer(FromCategory, ToCategory, toMSISDN, FromPIN);

    	currentNode=test.createNode("To verify that valid message appears on C2C Transfer from "+FromCategory+" to "+ToCategory+" .");
		currentNode.assignCategory("SIT");
		Validator.messageCompare(c2cMap.get("actualMessage"), c2cMap.get("expectedMessage"));
		
		txnID = c2cMap.get("TransactionID");
		
		currentNode=test.createNode("To verify that C2C Transaction Reverse is successful from "+FromCategory+" to "+ToCategory+" .");
		currentNode.assignCategory("SIT");
		String actual = TransactionReverseC2C.initiateC2CTxnReverseWithDomainCode(FromCategory,Domain,txnID);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		int totalRow1 = ExcelUtility.getRowCount();
		int i=1;
		for( i=1; i<=totalRow1;i++)
		{
			if((ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).matches(FromCategory)))
			break;
		}
		System.out.println(i);
		String SenderUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
		System.out.println(SenderUserName);
		
		int k=1;
		for( k=1; k<=totalRow1;k++)
		{			if((ExcelUtility.getCellData(0, ExcelI.MSISDN, k).matches(toMSISDN)))

			break;
		}
		System.out.println(k);
		String RecieverUserName = ExcelUtility.getCellData(0, ExcelI.USER_NAME, k);
	
		String expected = MessagesDAO.prepareMessageByKey("channelreversetrx.reverse.msg.success",null, txnID,SenderUserName,RecieverUserName);
		Validator.messageCompare(actual, expected);
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
          
          Object[][] Data1 = new Object[1][8];
          Data1[0][0]=Data[0][0];
          Data1[0][1]=Data[0][1];
          Data1[0][2]=Data[0][2];
          Data1[0][3]=Data[0][3];
          Data1[0][4]=Data[0][4];
          Data1[0][5]=Data[0][5];
          Data1[0][6]=Data[0][6];
          Data1[0][7]=Data[0][7];
          
          return Data1;
    }
	


}
