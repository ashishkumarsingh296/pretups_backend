package com.testscripts.uap;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CTransfer;
import com.Features.Enquiries.O2CTransfersEnquiry;
import com.Features.Enquiries.UserBalanceEnquiry;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.GetScreenshot;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;

public class UAP_OperatorToChannel extends BaseTest{
	static boolean TestCaseCounter = false;

	@Test(dataProvider = "categoryData")
	public void a_o2cTransferTC(String parentCategory, String category, String userMSISDN, String productType) throws InterruptedException	{
		Log.startTestCase("O2C Transfer");
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Operator to Channel");
			TestCaseCounter = true;
		}	
		O2CTransfer o2cTrans = new O2CTransfer(driver);
		
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
		
		/*
		 * Test case to initiate O2C Transfer
		 */
		currentNode = test.createNode("To verify that Operator user is able to perform Operator to channel Transfer initiation for category "+category+" with parent category  "+parentCategory+", product type"+productType);
		currentNode.assignCategory("UAP");
		
		Map<String, String> map= o2cTrans.initiateTransfer(userMSISDN, productType, quantity,remarks);
		String txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		String expected= MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		/*
		 * Test case to perform O2C approval level 1
		 */
		currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 1 for category "+category+" with parent category "+parentCategory+", product type"+productType);
		currentNode.assignCategory("UAP");

		map= o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		
		amountsParser.convertStringToLong(map.get("NetPayableAmount"));
		Long netPayableAmount= amountsParser.getValue();
		 
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
		 * Test Case to perform approval level 2
		 */
		if(netPayableAmount>firstApprov)
		{
			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 2 for category "+category+" with parent category "+parentCategory+", product type"+productType);
			currentNode.assignCategory("UAP");

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
		 * Test case to perform approval level 3
		 */
		
		if((netPayableAmount>secondApprov))
		{
			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 3 for category "+category+" with parent category "+parentCategory+", product type"+productType);
			currentNode.assignCategory("UAP");

			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

			if (actual3.equals(expected3))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + expected3 + "] but found [" + actual3 + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}
		
		Log.endTestCase("O2C Transfer");
	}
	
	
	//@Test(dataProvider = "categoryData")
	public void UAP_O2CTransferPositive_TC1(String parentCategory, String category, String userMSISDN, String productType) throws InterruptedException, IOException	{

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Operator to Channel");
			TestCaseCounter = true;
		}

		String expected1;
		String expected2 = null;
		
		//Calculate quantity to be entered.
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		Object[][] resultObj;
		resultObj=DBHandler.AccessHandler.getProductsDetails(networkCode, "SAL");
		
		DecimalFormat df = new DecimalFormat("#.00");
		df.setMaximumFractionDigits(0);
		
		BigDecimal wallet_BD = (BigDecimal) resultObj[0][2];
		int walletBalance = wallet_BD.intValue();
		int qty = (int) ((walletBalance / 100) *0.15);
		String quantity = ""+qty;
		String remarks= _masterVO.getProperty("Remarks");
		double quant = qty;
		Integer firstApprov = Integer.parseInt(_masterVO.getProperty("O2CFirstApprovalLimit"));
		Integer secondApprov = Integer.parseInt(_masterVO.getProperty("O2CSecondApprovalLimit"));

		O2CTransfer o2cTrans = new O2CTransfer(driver);

		currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer initiation for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("UAP");
		
		Map<String, String> map= o2cTrans.initiateTransfer(userMSISDN, productType, quantity, remarks);
		String txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		String expected= MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 1 for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("UAP");

		map = o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		if(quant<firstApprov){
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

		if(quant>=firstApprov){

			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 2 for category "+category+" with parent category "+parentCategory+", product type "+productType);
			currentNode.assignCategory("UAP");

			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);
			if(quant<secondApprov){
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

		if((quant>=secondApprov)){
			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 3 for category "+category+" with parent category "+parentCategory+", product type "+productType);
			currentNode.assignCategory("UAP");

			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

			if (actual3.equals(expected3))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + expected3 + "] but found [" + actual3 + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}
		
		currentNode = test.createNode("To verify that an O2C Transfers Enquiry is available on successful O2C Transfer for category "+category+" & product type "+productType + " using Transaction Number");
		currentNode.assignCategory("UAP");
		O2CTransfersEnquiry O2CEnquiry = new O2CTransfersEnquiry(driver);
		String ScreenShotPath = O2CEnquiry.validateO2CTransfersEnquiry(txnId, productType);
		currentNode.addScreenCaptureFromPath(ScreenShotPath);
		
		currentNode = test.createNode("To verify that User Balance Enquiry is available for category "+category);
		currentNode.assignCategory("UAP");
		UserBalanceEnquiry UserBalanceEnquiry = new UserBalanceEnquiry(driver);
		String ScreenshotPath = UserBalanceEnquiry.validateUserBalancesEnquiry(parentCategory, category);
		currentNode.addScreenCaptureFromPath(ScreenshotPath);
	}

	
/*	@Test(dataProvider = "categoryData")
	public void UAP_InitiateTransferByChannelUser_TC2(String parentCategory, String category, String userMSISDN, String productType) throws InterruptedException, IOException {

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Operator to Channel");
			TestCaseCounter = true;
		}

		String expected1;
		String expected2 = null;
		
		//Calculate quantity to be entered.
		String networkCode = _masterVO.getMasterValue("Network Code");
		Object[][] resultObj;
		resultObj=DBHandler.AccessHandler.getProductsDetails(networkCode, "SAL");
		
		DecimalFormat df = new DecimalFormat("#.00");
		df.setMaximumFractionDigits(0);
		
		BigDecimal wallet_BD = (BigDecimal) resultObj[0][2];
		int walletBalance = wallet_BD.intValue();
		int qty = (int) ((walletBalance / 100) *0.15);
		String quantity = ""+qty;
		double quant = qty;
		Integer firstApprov = Integer.parseInt(_masterVO.getProperty("O2CFirstApprovalLimit"));
		Integer secondApprov = Integer.parseInt(_masterVO.getProperty("O2CSecondApprovalLimit"));

		O2CTransfer o2cTrans = new O2CTransfer(driver);

		currentNode = test.createNode("To verify that "+ category +" category Channel User is able to perform Initiate Network Stock for Product Type: "+productType);
		currentNode.assignCategory("UAP");
		
		Map<String, String> map= o2cTrans.initiateTransferByChannelUser(parentCategory, category, productType, quantity, "PreTUPS Automation Script");
		String txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		String expected= MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		currentNode = test.createNode("To verify that Channel Admin is able to perform Transfer Approval 1 for category "+category+" against Initiate Transfer Request");
		currentNode.assignCategory("UAP");

		map = o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		if(quant<firstApprov){
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

		if(quant>=firstApprov){

			currentNode = test.createNode("To verify that Channel Admin is able to perform Transfer Approval 2 for category "+category+" against Initiate Transfer Request");
			currentNode.assignCategory("UAP");

			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);
			if(quant<secondApprov){
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

		if((quant>=secondApprov)){
			currentNode = test.createNode("To verify that Channel Admin is able to perform Transfer Approval 3 for category "+category+" against Initiate Transfer Request");
			currentNode.assignCategory("UAP");

			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

			if (actual3.equals(expected3))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + expected3 + "] but found [" + actual3 + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}
		
		currentNode = test.createNode("To verify that an O2C Transfers Enquiry is available on successful O2C Transfer Approval against Initiate Transfer Request for category "+category);
		currentNode.assignCategory("UAP");
		O2CTransfersEnquiry O2CEnquiry = new O2CTransfersEnquiry(driver);
		String ScreenShotPath = O2CEnquiry.validateO2CTransfersEnquiry(txnId, productType);
		currentNode.log(Status.INFO, "Enquiry Snapshot: " + currentNode.addScreenCaptureFromPath(ScreenShotPath));
	} */
	
	
	@Test(dataProvider = "categoryData")
	public void b_UAP_O2CTransfer_Approval1_Negative(String parentCategory, String category, String userMSISDN, String productType) throws InterruptedException	{

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test=extent.createTest("[UAP]Operator to Channel");
			TestCaseCounter = true;
		}
		
		//Calculate quantity to be entered.
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		DBHandler.AccessHandler.getProductsDetails(networkCode, "SAL");
		int FirstApprovalLimit = Integer.parseInt(_masterVO.getProperty("O2CFirstApprovalLimit"));
		Integer SecondApprovalLimit = Integer.parseInt(_masterVO.getProperty("O2CSecondApprovalLimit"));
		int approvalLength = DBHandler.AccessHandler.getSystemPreference("O2C_APPRV_QTY_LEVEL").split(",").length;
		int approvalCount=Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("O2C_APPRV_QTY_LEVEL").split(",")[approvalLength-1]);
	
		String remarks= _masterVO.getProperty("Remarks");
		
		O2CTransfer o2cTrans = new O2CTransfer(driver);
		
		/*
		 * Test Case Number 1: To validate if O2C Transfer Closes at Level 1
		 */
		currentNode = test.createNode("To verify that O2C Transfer is closed on Approval Level 1 if the Initiated Quantity is less than the First Approval Limit for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("UAP");
		double InitiationAmount_TC1 = FirstApprovalLimit - (FirstApprovalLimit * 0.5);
		String InitiateAmount_TC1 = "" + InitiationAmount_TC1;
		Map<String, String> InitiateMap_TC1= o2cTrans.initiateTransfer(userMSISDN, productType, InitiateAmount_TC1, remarks);
		String TransactionID_TC1= InitiateMap_TC1.get("TRANSACTION_ID");
		o2cTrans.performingLevel1Approval(userMSISDN, TransactionID_TC1);
				try {
					o2cTrans.performingLevel2Approval(userMSISDN, TransactionID_TC1, InitiateAmount_TC1);
					currentNode.log(Status.FAIL, "O2C Transfer Request Reached Level 2 Approval.");
					String screenshotPath = GetScreenshot.capture(driver);
					currentNode.addScreenCaptureFromPath(screenshotPath);
				}
				catch (Exception e) {
					currentNode.log(Status.PASS, "O2C Transfer Request Closed on Level 1 Successfully");
				}
		
		/*
		 * Test Case Number 2: To validate if O2C Transfer Closes at Level 2
		 */
		if(approvalCount==2){
		double InitiationAmount_TC2 = SecondApprovalLimit - ((SecondApprovalLimit - FirstApprovalLimit) * 0.5);
		String InitiateAmount_TC2 = "" + InitiationAmount_TC2;
		currentNode = test.createNode("To verify that O2C Transfer is closed on Approval Level 2 if the Initiated Quantity is greater than First Approval Limit for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("UAP");
		Map<String, String> InitiateMap_TC2= o2cTrans.initiateTransfer(userMSISDN, productType, InitiateAmount_TC2, remarks);
		String TransactionID_TC2= InitiateMap_TC2.get("TRANSACTION_ID");
		o2cTrans.performingLevel1Approval(userMSISDN, TransactionID_TC2);
		o2cTrans.performingLevel2Approval(userMSISDN, TransactionID_TC2, InitiateAmount_TC2);
				try {
					o2cTrans.performingLevel3Approval(userMSISDN, TransactionID_TC2, InitiateAmount_TC2);
					currentNode.log(Status.FAIL, "O2C Transfer Request Reached Level 3 Approval.");
				}
				catch (NoSuchElementException e) {
					currentNode.log(Status.PASS, "O2C Transfer Request Closed on Level 2 Successfully");
				}	
		
		/*
		 * Test Case Number 3: To validate if O2C Transfer Reaches at Level 3
		 */
		double InitiationAmount_TC3 = SecondApprovalLimit + (SecondApprovalLimit * 0.5);
		String InitiateAmount_TC3 = "" + InitiationAmount_TC3;
		currentNode = test.createNode("To verify that O2C Transfer reaches Level 3 if the Initiated Quantity is greater than Second Approval Limit for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("UAP");
		Map<String, String> InitiateMap_TC3= o2cTrans.initiateTransfer(userMSISDN, productType, InitiateAmount_TC3, remarks);
		String TransactionID_TC3= InitiateMap_TC3.get("TRANSACTION_ID");
		o2cTrans.performingLevel1Approval(userMSISDN, TransactionID_TC3);
		o2cTrans.performingLevel2Approval(userMSISDN, TransactionID_TC3, InitiateAmount_TC3);
			try {
				o2cTrans.performingLevel3Approval(userMSISDN, TransactionID_TC3, InitiateAmount_TC3);
				currentNode.log(Status.PASS, "O2C Transfer Request Reached Level 3 Successfully.");
			}
			catch (NoSuchElementException e) {
				currentNode.log(Status.FAIL, "O2C Transfer Closed at Level 2.");
			}	
		
		}
	}
	
	
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
		Object[][] Data = new Object[userCounter][3];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
				Data[j][0] = ExcelUtility.getCellData(0,ExcelI.PARENT_CATEGORY_NAME, i);
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
		Object[][] o2cData = new Object[countTotal][4];      
		for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
			o2cData[j][0] = Data[k][0];
			o2cData[j][1] = Data[k][1];
			o2cData[j][2] = Data[k][2];
			o2cData[j][3] = ProductObject[i];
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
}
