package com.testscripts.sit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CTransfer;
import com.Features.TransactionReverseO2C;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;

public class SIT_TransactionReverse_O2C extends BaseTest{
	static boolean TestCaseCounter = false;
	
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
		Object[][] Data = new Object[userCounter][5];
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
				j++;
			}
		}
			
/*
 * Store products from Product Sheet to Object.
 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
		int prodRowCount = ExcelUtility.getRowCount();
		Object[] ProductObject = new Object[prodRowCount];
		for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
			ProductObject[i] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, j);
		}

/*
 * Creating combination of channel users for each product.
 */
		int countTotal = ProductObject.length * userCounter;
		Object[][] o2cData = new Object[countTotal][6];      
		for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
			o2cData[j][0] = Data[k][0];
			o2cData[j][1] = Data[k][1];
			o2cData[j][2] = Data[k][2];
			o2cData[j][3] = Data[k][3];
			o2cData[j][4] = Data[k][4];
			o2cData[j][5] = ProductObject[i];
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
	public void o2cTransferTC(String domainName,String parentCategory, String category, String userMSISDN, String geography, String productType) throws InterruptedException	{
		String txnId;
		Log.startTestCase(this.getClass().getName());

		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]O2C Transaction Reverse");
			TestCaseCounter = true;
		}
		
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
		amountsParser.convertStringToLong(approvalLevel[0]).changeDenomation();
		Long firstApprov = amountsParser.getValue();
		amountsParser.convertStringToLong(approvalLevel[1]).changeDenomation();
		Long secondApprov = amountsParser.getValue();

		O2CTransfer o2cTrans = new O2CTransfer(driver);
		TransactionReverseO2C TransactionReverseO2C = new TransactionReverseO2C(driver);
		
		
		 // Test case to perform O2C Transfer and message validation
		 
		
		currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer initiation for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("SIT");

		Map<String, String> map= o2cTrans.initiateTransfer(userMSISDN, productType, quantity, remarks);
		
		
		txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		String expected= MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		
		 //Test Case to perform approval level 1 and message validation
		 
		
		currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 1 for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("SIT");

		map= o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		Long netPayableAmount=Long.parseLong(map.get("NetPayableAmount"));
		
		
		
		if(netPayableAmount<=firstApprov){
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
		}
		else{
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);
		}

		if (map.get("actualMessage").equals(expected1))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected1 + "] but found [" + map.get("actualMessage") + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		
		 //Test Case to perform approval level 2 and message validation
		 
		
		if(netPayableAmount>firstApprov){

			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 2 for category "+category+" with parent category "+parentCategory+", product type "+productType);
			currentNode.assignCategory("SIT");

			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);
			
			
			
			if(netPayableAmount<=secondApprov){
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			}
			else{
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);
			}

			if (actual2.equals(expected2))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + expected2 + "] but found [" + actual2 + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}

		
		 //Test case to perform approval level 3 and message validation
		 
		if((netPayableAmount>secondApprov)){
			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 3 for category "+category+" with parent category "+parentCategory+", product type "+productType);
			currentNode.assignCategory("SIT");

			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

			
			
			if (actual3.equals(expected3))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + expected3 + "] but found [" + actual3 + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}
		
		txnId= map.get("TRANSACTION_ID");
		System.out.println(txnId);
		
		currentNode = test.createNode("To verify that Channel Admin is able to perform Transaction Reverse O2C ");
		currentNode.assignCategory("SIT");
		
				
		String actual4 = TransactionReverseO2C.initiateO2CTxnReverse(txnId);
		
		String expected4= MessagesDAO.prepareMessageByKey("o2cchannelreversetrx.reverse.msg.success", null, txnId);
		
		
		if (actual4.equals(expected4))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected4 + "] but found [" + actual4 + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
		}
		
		 
		

	
	
	@Test(dataProvider = "categoryData")
	public void o2cTransferTxnReverseUsingMSISDN(String domainName,String parentCategory, String category, String userMSISDN, String geography, String productType) throws InterruptedException	{
		String txnId;
		Log.startTestCase(this.getClass().getName());

		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]O2C Transaction Reverse");
			TestCaseCounter = true;
		}

		String expected1;
		String expected2 = null;
		
		String quantity= _masterVO.getProperty("Quantity");
		String remarks= _masterVO.getProperty("Remarks");
		String netCode=_masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, netCode);
		_parser amountsParser = new _parser();
		amountsParser.convertStringToLong(approvalLevel[0]).changeDenomation();
		Long firstApprov = amountsParser.getValue();
		amountsParser.convertStringToLong(approvalLevel[1]).changeDenomation();
		Long secondApprov = amountsParser.getValue();

		O2CTransfer o2cTrans = new O2CTransfer(driver);
		TransactionReverseO2C TransactionReverseO2C = new TransactionReverseO2C(driver);
		
		
		 //Test case to perform O2C Transfer and message validation
		 
		
		currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer initiation for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("SIT");

		Map<String, String> map= o2cTrans.initiateTransfer(userMSISDN, productType, quantity, remarks);
		
		
		txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		String expected= MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		
		 //Test Case to perform approval level 1 and message validation
		 
		
		currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 1 for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("SIT");

		map= o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		Long netPayableAmount=Long.parseLong(map.get("NetPayableAmount"));
		
		
		
		if(netPayableAmount<=firstApprov){
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
		}
		else{
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);
		}

		if (map.get("actualMessage").equals(expected1))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected1 + "] but found [" + map.get("actualMessage") + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		
		 //Test Case to perform approval level 2 and message validation
		 
		
		if(netPayableAmount>firstApprov){

			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 2 for category "+category+" with parent category "+parentCategory+", product type "+productType);
			currentNode.assignCategory("SIT");

			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);
			
			
			
			if(netPayableAmount<=secondApprov){
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			}
			else{
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);
			}

			if (actual2.equals(expected2))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + expected2 + "] but found [" + actual2 + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}

		
		 //Test case to perform approval level 3 and message validation
		 
		if((netPayableAmount>secondApprov)){
			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 3 for category "+category+" with parent category "+parentCategory+", product type "+productType);
			currentNode.assignCategory("SIT");

			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

			
			
			if (actual3.equals(expected3))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + expected3 + "] but found [" + actual3 + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}
		

		
		currentNode = test.createNode("To verify that Channel Admin is able to perform Transaction Reverse O2C ");
		currentNode.assignCategory("SIT");
		
				
		String actual4 = TransactionReverseO2C.initiateO2CTxnReverseWithMSISDN(userMSISDN, txnId);
		
		String expected4= MessagesDAO.prepareMessageByKey("o2cchannelreversetrx.reverse.msg.success",null, txnId);
		
		
		if (actual4.equals(expected4))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected4 + "] but found [" + actual4 + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
		}

	

	
	
	@Test(dataProvider = "categoryData")
	public void o2cTransferTxnReverseWithGeography(String domainName,String parentCategory, String category, String userMSISDN, String geography, String productType) throws InterruptedException	{
		String txnId;
		
		Log.startTestCase(this.getClass().getName());

		
		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT]O2C Transaction Reverse");
			TestCaseCounter = true;
		}

		String expected1;
		String expected2 = null;
		
		String quantity= _masterVO.getProperty("Quantity");
		String remarks= _masterVO.getProperty("Remarks");
		String netCode=_masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, netCode);
		_parser amountsParser = new _parser();
		amountsParser.convertStringToLong(approvalLevel[0]).changeDenomation();
		Long firstApprov = amountsParser.getValue();
		amountsParser.convertStringToLong(approvalLevel[1]).changeDenomation();
		Long secondApprov = amountsParser.getValue();

		O2CTransfer o2cTrans = new O2CTransfer(driver);
		TransactionReverseO2C TransactionReverseO2C = new TransactionReverseO2C(driver);
		
		/*
		 * Test case to perform O2C Transfer and message validation
		 */
		
		currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer initiation for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("SIT");

		Map<String, String> map= o2cTrans.initiateTransfer(userMSISDN, productType, quantity, remarks);
		
		
		txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		String expected= MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		/*
		 * Test Case to perform approval level 1 and message validation
		 */
		
		currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 1 for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("SIT");

		map= o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		Long netPayableAmount=Long.parseLong(map.get("NetPayableAmount"));
		
		
		
		if(netPayableAmount<=firstApprov){
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
		}
		else{
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);
		}

		if (map.get("actualMessage").equals(expected1))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected1 + "] but found [" + map.get("actualMessage") + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		/*
		 * Test Case to perform approval level 2 and message validation
		 */
		
		if(netPayableAmount>firstApprov){

			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 2 for category "+category+" with parent category "+parentCategory+", product type "+productType);
			currentNode.assignCategory("SIT");

			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);
			
			
			
			if(netPayableAmount<=secondApprov){
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			}
			else{
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);
			}

			if (actual2.equals(expected2))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + expected2 + "] but found [" + actual2 + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}

		/*
		 * Test case to perform approval level 3 and message validation
		 */
		if((netPayableAmount>secondApprov)){
			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 3 for category "+category+" with parent category "+parentCategory+", product type "+productType);
			currentNode.assignCategory("SIT");

			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

			
			
			if (actual3.equals(expected3))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + expected3 + "] but found [" + actual3 + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}
		
		txnId= map.get("TRANSACTION_ID");
		System.out.println(txnId);
		
		currentNode = test.createNode("To verify that Channel Admin is able to perform Transaction Reverse O2C ");
		currentNode.assignCategory("SIT");
		
				
		String actual4 = TransactionReverseO2C.initiateO2CTxnReverseWithDomain(geography,domainName,productType,category,userMSISDN,txnId);
		System.out.println("===========Test========="+actual4);
		String expected4= MessagesDAO.prepareMessageByKey("o2cchannelreversetrx.reverse.msg.success",null,txnId);
		System.out.println("===========Test========="+expected4);
		
		if (actual4.equals(expected4))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected4 + "] but found [" + actual4 + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
		}
	
	
	
	
	
	
	

	
}