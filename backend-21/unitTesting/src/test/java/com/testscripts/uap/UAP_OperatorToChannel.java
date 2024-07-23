package com.testscripts.uap;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CTransfer;
import com.Features.O2CTransferRule;
import com.Features.Enquiries.O2CTransfersEnquiry;
import com.Features.Enquiries.UserBalanceEnquiry;
import com.aventstack.extentreports.Status;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.GetScreenshot;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.testscripts.prerequisites.UpdateCache;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;
import com.commons.PretupsI;

@ModuleManager(name = Module.UAP_OPERATOR_TO_CHANNEL)
public class UAP_OperatorToChannel extends BaseTest{
	static boolean TestCaseCounter = false;
	static String directO2CPreference;
	String assignCategory="UAP";
	static String moduleCode;
	
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-311") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_o2cTransferTC(String parentCategory, String domain, String category, String userMSISDN, String productType, String productCode, String productName) throws InterruptedException, SQLException, ParseException {
		final String methodName = "Test_o2cTransferTC";
		Log.startTestCase(methodName);
		
		
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("UO2CTRF1").getModuleCode();
		
		
		String expected1, expected;
		String expected2 = null;
		Long netPayableAmount = null;
		String quantity= _masterVO.getProperty("Quantity");
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);
		HashMap<String, String> initiatedQty = new HashMap<String, String>();
		initiatedQty.put(productCode, quantity);
		
		businessController businessController = new businessController(_masterVO.getProperty("O2CTransferCode"), null, userMSISDN);
		O2CTransfer o2cTrans = new O2CTransfer(driver);
		
		/*
		 * Test case to initiate O2C Transfer
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRF1").getExtentCase(), category,parentCategory,productType));//"To verify that Operator user is able to perform Operator to channel Transfer initiation for category "+category+" with parent category  "+parentCategory+", product type"+productType);
		currentNode.assignCategory(assignCategory);
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_WEB);
		Map<String, String> map= o2cTrans.initiateTransfer(userMSISDN, productType, quantity,productName, "Automated UAP O2C Transfer Testing");
		String txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		//Added a DirectO2C Transfer Handling where O2C Approvals are bypassed through AUTO_O2C_Preference - Krishan
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
			expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);
		else
			expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.successwithautoapproval", txnId);

		Assertion.assertEquals(actual, expected);

		/*
		 * Test case to perform O2C approval level 1
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRF2").getExtentCase(), category,parentCategory,productType));
		currentNode.assignCategory(assignCategory);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
		map= o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		netPayableAmount= _parser.getSystemAmount(map.get("NetPayableAmount"));
		 
		if(netPayableAmount<=firstApprov)
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
		else
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);

		Assertion.assertEquals(map.get("actualMessage"), expected1);
		} else {
			Assertion.assertSkip("Direct Operator to Channel is applicable in system");
		}

		/*
		 * Test Case to perform approval level 2
		 */
		if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount>firstApprov) {
			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRF3").getExtentCase(), category,parentCategory,productType));
			currentNode.assignCategory(assignCategory);

			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);
			if(netPayableAmount<=secondApprov)
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			else
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);
			
			Assertion.assertEquals(actual2, expected2);
		}

		/*
		 * Test case to perform approval level 3
		 */
		if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && netPayableAmount>secondApprov) {
			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRF4").getExtentCase(), category,parentCategory,productType));
			currentNode.assignCategory(assignCategory);

			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			
			Assertion.assertEquals(actual3, expected3);
		}
		
		/*
		 * Test Case to validate Network Stocks after successful O2C Transfer
		 */
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SO2CTRF5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
		BusinessValidator.validateStocks(TransactionVO);
		
		/*
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 */
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SO2CTRF6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		BusinessValidator.validateUserBalances(TransactionVO);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	//@Test(dataProvider = "categoryData")
	public void UAP_O2CTransferPositive_TC1(String parentCategory, String category, String userMSISDN, String productType, String productName) throws InterruptedException, IOException	{
		final String methodName = "Test_O2C Transfer_Positive";
		
		Log.startTestCase(methodName);

		String expected1, expected;
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

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRF1").getExtentCase(), category,parentCategory,productType));
		currentNode.assignCategory(assignCategory);
		
		Map<String, String> map= o2cTrans.initiateTransfer(userMSISDN, productType, quantity,productName, remarks);
		String txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		if (directO2CPreference == null ||!directO2CPreference.equalsIgnoreCase("true"))
			expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);
		else
			expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.successwithautoapproval", txnId);

		Validator.messageCompare(actual, expected);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRF2").getExtentCase(), category,parentCategory,productType));
		currentNode.assignCategory(assignCategory);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
			map = o2cTrans.performingLevel1Approval(userMSISDN, txnId);
			if(quant<firstApprov)
				expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			else
				expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);
		
			Validator.messageCompare(map.get("actualMessage"), expected1);
		} else {
			Log.skip("Direct Operator to Channel is applicable in system");
		}

		if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && quant>=firstApprov){
			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRF3").getExtentCase(), category,parentCategory,productType));
			currentNode.assignCategory(assignCategory);

			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);
			if(quant<secondApprov)
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			else
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);

			Validator.messageCompare(actual2, expected2);
		}

		if((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) && quant>=secondApprov){
			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PO2CTRF4").getExtentCase(), category,parentCategory,productType));
			currentNode.assignCategory(assignCategory);
			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			
			Validator.messageCompare(actual3, expected3);
		}
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UO2CTRF1").getExtentCase(), category,productType));
		currentNode.assignCategory(assignCategory);
		O2CTransfersEnquiry O2CEnquiry = new O2CTransfersEnquiry(driver);
		String ScreenShotPath = O2CEnquiry.validateO2CTransfersEnquiry(txnId, productType);
		currentNode.addScreenCaptureFromPath(ScreenShotPath);
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UO2CTRF2").getExtentCase(), category));
		currentNode.assignCategory(assignCategory);
		UserBalanceEnquiry UserBalanceEnquiry = new UserBalanceEnquiry(driver);
		String ScreenshotPath = UserBalanceEnquiry.validateUserBalancesEnquiry(parentCategory, category);
		currentNode.addScreenCaptureFromPath(ScreenshotPath);
		
		Log.endTestCase(methodName);
	}

	
	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-314") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void b_UAP_InitiateTransferByChannelUser_TC2(String parentCategory, String domain, String category, String userMSISDN, String productType, String productCode, String productName) throws InterruptedException, IOException {

		final String methodName = "Test_InitiateTransferByChannelUser";
        Log.startTestCase(methodName);

		String expected1;
		String expected2 = null;

		Long netPayableAmount = null;
		String quantity= _masterVO.getProperty("Quantity");
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);
		HashMap<String, String> initiatedQty = new HashMap<String, String>();
		initiatedQty.put(productCode, quantity);
			
		
		O2CTransfer o2cTrans = new O2CTransfer(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UO2CTRF3").getExtentCase(), category,productType));//"To verify that "+ category +" category Channel User is able to perform Operator to Channel Initate for Product Type: "+productType);
		currentNode.assignCategory(assignCategory);
		
		if(CommonUtils.roleCodeExistInLinkSheet(RolesI.O2CINIT_ROLECODE, category)){
		Map<String, String> map= o2cTrans.initiateTransferByChannelUser(parentCategory, category, productType, quantity, "PreTUPS Automation Script");
		String txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		String expected= MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);

		Assertion.assertEquals(actual, expected);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UO2CTRF4").getExtentCase(), category));//"To verify that Channel Admin is able to perform Transfer Approval 1 for category "+category+" against Initiate Transfer Request");
		currentNode.assignCategory(assignCategory);

		map = o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		netPayableAmount= _parser.getSystemAmount(map.get("NetPayableAmount"));	
		if(netPayableAmount<=firstApprov){
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
		}
		else{
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);
		}
		Assertion.assertEquals(map.get("actualMessage"), expected1);

		if(netPayableAmount>firstApprov){

			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UO2CTRF5").getExtentCase(), category));//"To verify that Channel Admin is able to perform Transfer Approval 2 for category "+category+" against Initiate Transfer Request");
			currentNode.assignCategory(assignCategory);

			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);
			if(netPayableAmount<=secondApprov){
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			}
			else{
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);
			}
			Assertion.assertEquals(actual2, expected2);
		}

		if((netPayableAmount>secondApprov)){
			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UO2CTRF6").getExtentCase(), category));//"To verify that Channel Admin is able to perform Transfer Approval 3 for category "+category+" against Initiate Transfer Request");
			currentNode.assignCategory(assignCategory);

			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

			Assertion.assertEquals(actual3, expected3);
		}
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UO2CTRF7").getExtentCase(), category));//"To verify that an O2C Transfers Enquiry is available on successful O2C Transfer Approval against Initiate Transfer Request for category "+category);
		currentNode.assignCategory(assignCategory);
		O2CTransfersEnquiry O2CEnquiry = new O2CTransfersEnquiry(driver);
		String ScreenShotPath = O2CEnquiry.validateO2CTransfersEnquiry(txnId, productType);
		currentNode.log(Status.INFO, "Enquiry Snapshot: " + currentNode.addScreenCaptureFromPath(ScreenShotPath));
		}else{
			currentNode.log(Status.SKIP, "O2CInitiation is not allowed to category["+category+"].");
		}
		
		Log.endTestCase(methodName);
	} 
	
	
	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-318") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void c_UAP_O2CTransfer_Approval1_Negative(String parentCategory, String domain, String category, String userMSISDN, String productType, String ProductCode, String productName) throws InterruptedException	{
		 
		final String methodName = "Test_O2CTransfer_Approval1_Negative";
	        Log.startTestCase(methodName);
		
		//Calculate quantity to be entered.
		String[] originalApprovalLimits = DBHandler.AccessHandler.o2cApprovalLimits(category, _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		
		long FirstApprovalLimit = 100;
		long SecondApprovalLimit = 200;
		
		String remarks= "Automated UAP O2C Transfer Testing";
		O2CTransfer o2cTrans = new O2CTransfer(driver);
		O2CTransferRule O2CTransferRule = new O2CTransferRule(driver);
		
		/*
		 * Test Case Number 1: To validate if O2C Transfer Closes at Level 1
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UO2CTRF8").getExtentCase(), category,parentCategory,productType));
		currentNode.assignCategory(assignCategory);
		if (directO2CPreference == null ||!directO2CPreference.equalsIgnoreCase("true")) {
			O2CTransferRule.modifyTransferRule(domain, category, Long.toString(FirstApprovalLimit), Long.toString(SecondApprovalLimit));
			new UpdateCache().updateCache();
			
			String InitiateAmount_TC1 = "" + (FirstApprovalLimit - 1);
			Map<String, String> InitiateMap_TC1= o2cTrans.initiateTransfer(userMSISDN, productType, InitiateAmount_TC1,productName, remarks);
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
			} else {
				Assertion.assertSkip("Direct Operator to Channel is applicable in system");
		}
		
		/*
		 * Test Case Number 2: To validate if O2C Transfer Closes at Level 2
		 */
		if(directO2CPreference == null ||!directO2CPreference.equalsIgnoreCase("true")){
		String InitiateAmount_TC2 = "" + (SecondApprovalLimit - 1);
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UO2CTRF9").getExtentCase(), category,parentCategory,productType));
		currentNode.assignCategory(assignCategory);
		Map<String, String> InitiateMap_TC2= o2cTrans.initiateTransfer(userMSISDN, productType, InitiateAmount_TC2,productName, remarks);
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
		if (UserAccess.getRoleStatus(RolesI.O2C_APPROVAL_LEVEL3)) {
			String InitiateAmount_TC3 = "" + (SecondApprovalLimit + 1);
			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UO2CTRF10").getExtentCase(), category,parentCategory,productType));
			currentNode.assignCategory(assignCategory);
			Map<String, String> InitiateMap_TC3= o2cTrans.initiateTransfer(userMSISDN, productType, InitiateAmount_TC3,productName, remarks);
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
		
		O2CTransferRule.modifyTransferRule(domain, category, _parser.getDisplayAmount(Long.parseLong(originalApprovalLimits[0])), _parser.getDisplayAmount(Long.parseLong(originalApprovalLimits[1])));
		new UpdateCache().updateCache();
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	/**
	 * DataProvider for Operator to Channel transfer
	 * @return Object
	 **/
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
		Object[][] Data = new Object[userCounter][4];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
				Data[j][0] = ExcelUtility.getCellData(0,ExcelI.PARENT_CATEGORY_NAME, i);
				Data[j][3] = ExcelUtility.getCellData(0,ExcelI.DOMAIN_NAME, i);
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
		Object[][] o2cData = new Object[countTotal][7];      
		for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
			o2cData[j][0] = Data[k][0];
			o2cData[j][1] = Data[k][3];
			o2cData[j][2] = Data[k][1];
			o2cData[j][3] = Data[k][2];
			o2cData[j][4] = ProductObject[i][0];
			o2cData[j][5] = ProductObject[i][1];
			o2cData[j][6] = ProductObject[i][2];
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
