package com.testscripts.sit;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransfer;
import com.Features.ChannelUser;
import com.Features.O2CTransfer;
import com.Features.TransactionReverseO2C;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_Transaction_Reverse_O2C)
public class SIT_TransactionReverse_O2C extends BaseTest{
	static boolean TestCaseCounter = false;
	static String directO2CPreference;

	/**
	 * DataProvider for Operator to Channel transfer
	 * @return Object
	 */
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed() {
		String O2CTransferCode = _masterVO.getProperty("O2CTransferCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		/*
		 * Array list to store Categories for which O2C transfer is allowed
		 */
		ArrayList<String> alist1 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(O2CTransferCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
			}
		}

		/*
		 * Counter to count number of users exists in channel users hierarchy sheet 
		 * of Categories for which O2C transfer is allowed
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();
		int userCounter = 0;
		for (int i = 1; i <= chnlCount; i++) {
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				userCounter++;
			}
		}

		/*
		 * Store required data of 'O2C transfer allowed category' users in Object
		 */
		Object[][] Data = new Object[userCounter][6];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[j][0] = ExcelUtility.getCellData(0,ExcelI.DOMAIN_NAME, i);
				System.out.println(Data[j][0]);
				Data[j][1] = ExcelUtility.getCellData(0,ExcelI.PARENT_CATEGORY_NAME, i);
				System.out.println(Data[j][1]);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				System.out.println(Data[j][2]);
				Data[j][3] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
				System.out.println(Data[j][3]);
				Data[j][4] = ExcelUtility.getCellData(0,ExcelI.GEOGRAPHY, i);
				System.out.println(Data[j][4]);
				Data[j][5] = i;
				
				j++;
			}
		}

		/*
		 * Store products from Product Sheet to Object.
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
		int prodRowCount = ExcelUtility.getRowCount();
		Object[][] ProductObject = new Object[prodRowCount][3];
		for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
			ProductObject[i][0] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, j);
			ProductObject[i][1] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, j);
            ProductObject[i][2] = ExcelUtility.getCellData(0, ExcelI.SHORT_NAME, j);
		}

		/*
		 * Creating combination of channel users for each product.
		 */
		int countTotal = ProductObject.length * userCounter;
		Object[][] o2cData = new Object[countTotal][9];      
		for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
			o2cData[j][0] = Data[k][0];
			o2cData[j][1] = Data[k][1];
			o2cData[j][2] = Data[k][2];
			o2cData[j][3] = Data[k][3];
			o2cData[j][4] = Data[k][4];
			o2cData[j][5] = Data[k][5];
			o2cData[j][6] = ProductObject[i][0];
			o2cData[j][7] = ProductObject[i][1];
			o2cData[j][8] = ProductObject[i][2];
			if (k < userCounter) {
				k++;
				if (k >= userCounter) {
					k = 0;
					i++;
					if (i >= ProductObject.length)
						i = 0;
				}
			} else {
				k = 0;
			}
		}


		return o2cData;
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-893") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void o2cTransferTC(String domainName,String parentCategory, String category, String userMSISDN, String geography,int rownum, String productType,String productCode, String productName) throws InterruptedException	{
		String txnId;
		final String methodName = "Test_TransactionReverseO2C";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITO2CTXNREV1");

		System.out.println("domain Name:" +domainName);
		System.out.println("parentCategory:" +parentCategory);
		System.out.println("category:" +category);
		System.out.println("userMSISDN:" +userMSISDN);
		System.out.println("geography is" +geography);
		System.out.println("product Type is" +productType);

		String expected1;
		String expected2 = null;

		String quantity= _masterVO.getProperty("Quantity");
		String remarks= _masterVO.getProperty("Remarks");
		String netCode=_masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, netCode);
		_parser amountsParser = new _parser();
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);

		O2CTransfer o2cTrans = new O2CTransfer(driver);
		TransactionReverseO2C TransactionReverseO2C = new TransactionReverseO2C(driver);
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");

		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),category, parentCategory, productType)) ;
		//currentNode = test.createNode("To verify that Channel Admin is able to perform Transaction Reverse O2C from category "+category+" with parent category "+parentCategory+" using TXN ID for " +productType);
		currentNode.assignCategory("SIT");

		// Test case to perform O2C Transfer and message validation


		Log.info("Verifying that Channel Admin is able to perform Operator to channel Transfer initiation for category "+category+" with parent category "+parentCategory+", product type "+productType);
		Map<String, String> map= o2cTrans.initiateTransfer(userMSISDN, productType, quantity,productName, remarks);
		txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		String expected = null;
		Long netPayableAmount = null;
		
		//Added a DirectO2C Transfer Handling where O2C Approvals are bypassed through AUTO_O2C_Preference - Krishan
				if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")){
					 expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);
				}
				else
				{
					expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.successwithautoapproval", txnId);
				}
		Assertion.assertEquals(actual, expected);

		//Test Case to perform approval level 1 and message validation


		 Log.info(" verifying that Channel Admin is able to perform Operator to channel Transfer Approval 1 for category "+category+" with parent category "+parentCategory+", product type "+productType);

		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
		map= o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		netPayableAmount= _parser.getSystemAmount(map.get("NetPayableAmount"));
		if(netPayableAmount<=firstApprov){
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
		}
		else{
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);
		}
		Assertion.assertEquals(map.get("actualMessage"), expected1);
		} else {
			Assertion.assertSkip("Direct Operator to Channel is applicable in system");
		}


		//Test Case to perform approval level 2 and message validation


		if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount>firstApprov) {

			Log.info("verifying that Channel Admin is able to perform Operator to channel Transfer Approval 2 for category "+category+" with parent category "+parentCategory+", product type "+productType);
			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);
			if(netPayableAmount<=secondApprov){
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			}
			else{
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);
			}
			Assertion.assertEquals(actual2, expected2);
		}


		//Test case to perform approval level 3 and message validation

		if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount>secondApprov) {
			Log.info(" verifying that Channel Admin is able to perform Operator to channel Transfer Approval 3 for category "+category+" with parent category "+parentCategory+", product type "+productType);
			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

			Assertion.assertEquals(actual3, expected3);
		}

		txnId= map.get("TRANSACTION_ID");
		System.out.println(txnId);



		String actual4 = TransactionReverseO2C.initiateO2CTxnReverse(txnId);

		String expected4= MessagesDAO.prepareMessageByKey("o2cchannelreversetrx.reverse.msg.success", null, txnId);
		Assertion.assertEquals(actual4, expected4);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}






	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-894") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void o2cTransferTxnReverseUsingMSISDN(String domainName,String parentCategory, String category, String userMSISDN, String geography,int rownum, String productType,String productCode, String productName) throws InterruptedException	{
		String txnId;
		final String methodName = "Test_TransactionReverseO2C";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITO2CTXNREV2");

		String expected1;
		String expected2 = null;

		String quantity= _masterVO.getProperty("Quantity");
		String remarks= _masterVO.getProperty("Remarks");
		String netCode=_masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, netCode);
		_parser amountsParser = new _parser();
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);

		O2CTransfer o2cTrans = new O2CTransfer(driver);
		TransactionReverseO2C TransactionReverseO2C = new TransactionReverseO2C(driver);
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");

		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),category, parentCategory, productType)) ;
		//currentNode = test.createNode("To verify that Channel Admin is able to perform Transaction Reverse O2C from category "+category+" with parent category "+parentCategory+" using MSISDN for " +productType);
		currentNode.assignCategory("SIT");
		//Test case to perform O2C Transfer and message validation


		Log.info( "verifying that Channel Admin is able to perform Operator to channel Transfer initiation for category "+category+" with parent category "+parentCategory+", product type "+productType);
		Map<String, String> map= o2cTrans.initiateTransfer(userMSISDN, productType, quantity,productName, remarks);

		txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		
		String expected = null;
		Long netPayableAmount = null;
		
		//Added a DirectO2C Transfer Handling where O2C Approvals are bypassed through AUTO_O2C_Preference - Krishan
				if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")){
					 expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);
				}
				else
				{
					expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.successwithautoapproval", txnId);
				}
				Assertion.assertEquals(actual, expected);
		
		
		//Test Case to perform approval level 1 and message validation
		Log.info( "verifying that Channel Admin is able to perform Operator to channel Transfer Approval 1 for category "+category+" with parent category "+parentCategory+", product type "+productType);
		
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
		map= o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		 netPayableAmount= _parser.getSystemAmount(map.get("NetPayableAmount"));

		if(netPayableAmount<=firstApprov){
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
		}
		else{
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);
		}
		Assertion.assertEquals(map.get("actualMessage"), expected1);
		} else {
			Assertion.assertSkip("Direct Operator to Channel is applicable in system");
		}


		//Test Case to perform approval level 2 and message validation
		if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount>firstApprov) {

			Log.info("verifying that Channel Admin is able to perform Operator to channel Transfer Approval 2 for category "+category+" with parent category "+parentCategory+", product type "+productType);

			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);



			if(netPayableAmount<=secondApprov){
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			}
			else{
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);
			}

			Assertion.assertEquals(actual2, expected2);			
		}


		//Test case to perform approval level 3 and message validation

		if((netPayableAmount>secondApprov)){
			Log.info("verifying that Channel Admin is able to perform Operator to channel Transfer Approval 3 for category "+category+" with parent category "+parentCategory+", product type "+productType);


			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);


			Assertion.assertEquals(actual3, expected3);
		}



		String actual4 = TransactionReverseO2C.initiateO2CTxnReverseWithMSISDN(userMSISDN, txnId);

		String expected4= MessagesDAO.prepareMessageByKey("o2cchannelreversetrx.reverse.msg.success",null, txnId);


		Assertion.assertEquals(actual4, expected4);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}





	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-895") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void o2cTransferTxnReverseWithGeography(String domainName,String parentCategory, String category, String userMSISDN, String geography, int rownum, String productType,String productCode, String productName) throws InterruptedException	{
		String txnId;
		final String methodName = "Test_TransactionReverseO2C";
        Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITO2CTXNREV3");
		String expected1;
		String expected2 = null;

		String quantity= _masterVO.getProperty("Quantity");
		String remarks= _masterVO.getProperty("Remarks");
		String netCode=_masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, netCode);
		_parser amountsParser = new _parser();
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);

		O2CTransfer o2cTrans = new O2CTransfer(driver);
		TransactionReverseO2C TransactionReverseO2C = new TransactionReverseO2C(driver);
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");

		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),category, parentCategory, productType)) ;
		//currentNode = test.createNode("To verify that Channel Admin is able to perform Transaction Reverse O2C from category "+category+" with parent category "+parentCategory+" using Geographical Domain for " +productType);
		currentNode.assignCategory("SIT");

		/*
		 * Test case to perform O2C Transfer and message validation
		 */

		Log.info("verifying that Channel Admin is able to perform Operator to channel Transfer initiation for category "+category+" with parent category "+parentCategory+", product type "+productType);


		Map<String, String> map= o2cTrans.initiateTransfer(userMSISDN, productType, quantity,productName, remarks);


		txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		String expected = null;
		Long netPayableAmount = null;
		
		//Added a DirectO2C Transfer Handling where O2C Approvals are bypassed through AUTO_O2C_Preference - Krishan
				if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")){
					 expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);
				}
				else
				{
					expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.successwithautoapproval", txnId);
				}
				Assertion.assertEquals(actual, expected);

		/*
		 * Test Case to perform approval level 1 and message validation
		 */

		Log.info("verifying that Channel Admin is able to perform Operator to channel Transfer Approval 1 for category "+category+" with parent category "+parentCategory+", product type "+productType);

		map= o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
		netPayableAmount= _parser.getSystemAmount(map.get("NetPayableAmount"));



		if(netPayableAmount<=firstApprov){
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
		}
		else{
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);
		}
		Assertion.assertEquals(map.get("actualMessage"), expected1);
		} else {
			Assertion.assertSkip("Direct Operator to Channel is applicable in system");
		}

		/*
		 * Test Case to perform approval level 2 and message validation
		 */

		if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount>firstApprov) {

			Log.info("verify that Channel Admin is able to perform Operator to channel Transfer Approval 2 for category "+category+" with parent category "+parentCategory+", product type "+productType);

			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);

			if(netPayableAmount<=secondApprov){
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			}
			else{
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);
			}
			Assertion.assertEquals(actual2, expected2);
		}

		/*
		 * Test case to perform approval level 3 and message validation
		 */
		if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount>secondApprov) {
			Log.info("verifying that Channel Admin is able to perform Operator to channel Transfer Approval 3 for category "+category+" with parent category "+parentCategory+", product type "+productType);

			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);


			Assertion.assertEquals(actual3, expected3);
		}

		txnId= map.get("TRANSACTION_ID");
		System.out.println(txnId);




		String actual4 = TransactionReverseO2C.initiateO2CTxnReverseWithDomain(geography,domainName,productType,category,userMSISDN,txnId);

		String expected4= MessagesDAO.prepareMessageByKey("o2cchannelreversetrx.reverse.msg.success",null,txnId);

		Assertion.assertEquals(actual4, expected4);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}




	
	
	
	
	
	
	//@Test(dataProvider = "categoryData")
	//@TestManager(TestKey = "PRETUPS-896") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void o2cTransferTxnReverseUsingMSISDN_Neg(String domainName,String parentCategory, String category, String userMSISDN, String geography,int rownum, String productType,String productCode, String productName) throws InterruptedException, IOException	{
		String txnId;
		Log.startTestCase(this.getClass().getName());

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITO2CTXNREV4");

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]O2C Transaction Reverse"+ CaseMaster1.getModuleCode());
			TestCaseCounter = true;
		}

		String expected1;
		String expected2 = null;

		String quantity= _masterVO.getProperty("Quantity");
		String remarks= _masterVO.getProperty("Remarks");
		String netCode=_masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, netCode);
		_parser amountsParser = new _parser();
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);

		O2CTransfer o2cTrans = new O2CTransfer(driver);
		TransactionReverseO2C TransactionReverseO2C = new TransactionReverseO2C(driver);
		HashMap<String, String> channelMap=new HashMap<>();
		ChannelUser channelUser= new ChannelUser(driver);
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");

		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),category, parentCategory, productType)) ;
		//currentNode = test.createNode("To verify that Channel Admin is able to perform Transaction Reverse O2C from category "+category+" with parent category "+parentCategory+" using MSISDN for " +productType);
		currentNode.assignCategory("SIT");
		//Test case to perform O2C Transfer and message validation

		 channelMap=channelUser.channelUserInitiate(rownum, domainName, parentCategory, category, geography);
			String APPLEVEL = DBHandler.AccessHandler.getSystemPreference(UserAccess.userapplevelpreference());
			if(APPLEVEL.equals("2"))
			{channelUser.approveLevel1_ChannelUser();
			channelUser.approveLevel2_ChannelUser();
			}
			else if(APPLEVEL.equals("1")){
				channelUser.approveLevel1_ChannelUser();	
			}else{
				Log.info("Approval not required.");	
			}

			String newToMSISDN =channelMap.get("MSISDN");
		
		

		Log.info( "verifying that Channel Admin is able to perform Operator to channel Transfer initiation for category "+category+" with parent category "+parentCategory+", product type "+productType);
		Map<String, String> map= o2cTrans.initiateTransfer(newToMSISDN, productType, quantity,productName, remarks);

		txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		
		String expected = null;
		Long netPayableAmount = null;
		
		//Added a DirectO2C Transfer Handling where O2C Approvals are bypassed through AUTO_O2C_Preference - Krishan
				if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")){
					 expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);
				}
				else
				{
					expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.successwithautoapproval", txnId);
				}
		Validator.messageCompare(actual, expected);
		
		
		//Test Case to perform approval level 1 and message validation
		Log.info( "verifying that Channel Admin is able to perform Operator to channel Transfer Approval 1 for category "+category+" with parent category "+parentCategory+", product type "+productType);
		
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
		map= o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		 netPayableAmount= _parser.getSystemAmount(map.get("NetPayableAmount"));

		if(netPayableAmount<=firstApprov){
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
		}
		else{
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);
		}
		Validator.messageCompare(map.get("actualMessage"), expected1);
		} else {
			Log.skip("Direct Operator to Channel is applicable in system");
		}


		//Test Case to perform approval level 2 and message validation
		if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount>firstApprov) {

			Log.info("verifying that Channel Admin is able to perform Operator to channel Transfer Approval 2 for category "+category+" with parent category "+parentCategory+", product type "+productType);

			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);



			if(netPayableAmount<=secondApprov){
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			}
			else{
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);
			}

			Validator.messageCompare(actual2, expected2);			
		}


		//Test case to perform approval level 3 and message validation

		if((netPayableAmount>secondApprov)){
			Log.info("verifying that Channel Admin is able to perform Operator to channel Transfer Approval 3 for category "+category+" with parent category "+parentCategory+", product type "+productType);


			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);


			Validator.messageCompare(actual3, expected3);
		}
		
		//Test Case to perform C2S Transaction to exhaust user balance
		
		C2STransfer c2STransfer = new C2STransfer(driver);
		c2STransfer.performC2STransfer(parentCategory,category ,channelMap.get("PIN"),_masterVO.getProperty("CustomerRechargeCode"),"100",UniqueChecker.generate_subscriber_MSISDN("Prepaid"));



		String actual4 = TransactionReverseO2C.initiateO2CTxnReverseWithMSISDN_neg(userMSISDN, txnId);

		String expected4= MessagesDAO.prepareMessageByKey("o2cchannelreversetrx.reverse.msg.success",null, txnId);


		Validator.messageCompare(actual4, expected4);
	}

	
	





}
