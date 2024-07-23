package com.testscripts.uap;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.FOCTransfer;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.commons.PretupsI;
@ModuleManager(name = Module.UAP_FOC_TRANSFER)
public class UAP_FOCTransfer extends BaseTest{
	
	static boolean TestCaseCounter = false;
	static String ActualFOCSuccessMessage = null;
	String assignCategory="UAP";
	/*
	 * Test Case Number 2: To verify that Channel Admin is able to perform FOC Transfer successfully
	 * 					   The test case covers Approval Levels & Message validation
	 */
	@Test(dataProvider="categoryData")
	 @TestManager(TestKey = "PRETUPS-305") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void TC1_FOCTransfer(String parentCategory, String category, String userMSISDN, String productType, String productCode, String shortName) throws InterruptedException, ParseException, SQLException	{
		
		final String methodName = "Test_FOCTransfer";
		Log.startTestCase(methodName);
		
		String quantity = _masterVO.getProperty("InitiateFOCAmount");
		String remarks = "Automated FOC Initiation";
		HashMap<String, String> initiatedQty = new HashMap<String, String>();
		initiatedQty.put(productCode, quantity);

		businessController businessController = new businessController(_masterVO.getProperty("FOCCode"), null, userMSISDN);
		FOCTransfer FOCTransfer = new FOCTransfer(driver);
		
		/*
		 * Test Case Number 1.1: FOC Transfer Initiation
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UFOCTRANSFER1").getExtentCase(), category,parentCategory,productType));
		currentNode.assignCategory(assignCategory);
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_WEB);
		Map<String, String> map= FOCTransfer.initiateFOCTransfer(userMSISDN, productType, quantity,shortName, remarks);
		String TransactionID= map.get("TRANSACTION_ID");
		//String MSISDN= map.get("CHANNELUSER_MSISDN");
		String ActualInitiateMessage= map.get("INITIATE_MESSAGE");
		int PREF_FOCApprovalLevels = Integer.parseInt(map.get("PREF_FOC_APPROVAL_LEVEL"));
		
		/*
		 * Test Case Number 1.2: FOC Transfer Initiation Message Validation
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UFOCTRANSFER2").getExtentCase(), category,parentCategory,productType));		
		currentNode.assignCategory(assignCategory);
		String expected= MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailsfocview.message.focsuccess", TransactionID);
		Assertion.assertEquals(ActualInitiateMessage, expected);
		
		
		/*
		 * Test Case Number 1.3: FOC Transfer Approval Level 1
		 */
		if (PREF_FOCApprovalLevels > 0) {
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UFOCTRANSFER3").getExtentCase(), category,parentCategory,productType));
		currentNode.assignCategory(assignCategory);
		ActualFOCSuccessMessage = FOCTransfer.performFOCApprovalLevel1(userMSISDN, TransactionID);
		}
		
		/*
		 * Test Case Number 1.4: FOC Transfer Approval Level 2
		 */
		if (PREF_FOCApprovalLevels > 1) {
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UFOCTRANSFER4").getExtentCase(), category,parentCategory,productType));
		currentNode.assignCategory(assignCategory);
		ActualFOCSuccessMessage = FOCTransfer.performFOCApprovalLevel2(userMSISDN, TransactionID, quantity);
		}
		
		/*
		 * Test Case Number 1.5: FOC Transfer Approval Level 3
		 */
		if (PREF_FOCApprovalLevels > 2) {
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UFOCTRANSFER5").getExtentCase(), category,parentCategory,productType));
		currentNode.assignCategory(assignCategory);
		ActualFOCSuccessMessage = FOCTransfer.performFOCApprovalLevel3(userMSISDN, TransactionID, quantity);
		}
		
		/*
		 * Test Case Number 1.6: FOC Final Message Validation
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UFOCTRANSFER6").getExtentCase(), category,parentCategory,productType));		
		currentNode.assignCategory(assignCategory);
		String FinalMessage = MessagesDAO.prepareMessageByKey("channeltransfer.foctransferapprovaldetailview.approval.success", TransactionID);
		Assertion.assertEquals(ActualFOCSuccessMessage, FinalMessage);
		
		/*
		 * Test Case to validate Network Stocks after successful O2C Transfer
		 */
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UFOCTRANSFER7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
		BusinessValidator.validateStocks(TransactionVO);
		
		/*
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 */
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UFOCTRANSFER8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		BusinessValidator.validateUserBalances(TransactionVO);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	/*
	 * Test Case Number 2: To verify that Channel Admin is able to Reject FOC Transfer at Level 1, Level 2 & Level 3 Approval.
	 * 					   Test Case Covers Message validation as well.
	 */
	@Test(dataProvider="categoryData")
	 @TestManager(TestKey = "PRETUPS-309") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void TC2_FOCRejection(String parentCategory, String category, String userMSISDN, String productType, String productCode,String shortName) throws NumberFormatException, SQLException, InterruptedException{
		
		final String methodName = "Test_FOCRejection";
		Log.startTestCase(methodName);
		
		// Objects Initialization
		FOCTransfer FOCTransfer = new FOCTransfer(driver);
		
		/*
		 * Test Case Number 2.1: FOC Rejection At Approval Level 1
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UFOCTRANSFER9").getExtentCase(), category,parentCategory,productType));
		currentNode.assignCategory(assignCategory);
		currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Initiation for Rejection Scenerio validation", ExtentColor.BLUE));
		// Initiating FOC Transfer as a Pre-Requisite for FOC Transfer Rejection at Approval Level 1
		String quantity = _masterVO.getProperty("InitiateFOCAmount");
		String remarks = "Automated FOC Initiation";
		Map<String, String> map= FOCTransfer.initiateFOCTransfer(userMSISDN, productType, quantity,shortName, remarks);
		String TransactionID= map.get("TRANSACTION_ID");
		int PREF_FOCApprovalLevels = Integer.parseInt(map.get("PREF_FOC_APPROVAL_LEVEL"));
		currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Rejection at Level 1 Approval Begins", ExtentColor.BLUE));
		String RejectionMessage = FOCTransfer.rejectFOCApprovalLevel1(userMSISDN, TransactionID);
		
		/*
		 * Test Case Number 2.2: FOC Message Validation on successful FOC Approval 1 Rejection
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UFOCTRANSFER10").getExtentCase(), category,parentCategory,productType));
		currentNode.assignCategory(assignCategory);
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.foctransferapprovaldetailview.msg.level1cancel", TransactionID);
		Assertion.assertEquals(RejectionMessage, ExpectedMessage);
		
		
		if (PREF_FOCApprovalLevels > 1) {
			
			/*
			 * Test Case Number 2.3: FOC Rejection at Approval Level 2
			 */
			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UFOCTRANSFER11").getExtentCase(), category,parentCategory,productType));
			currentNode.assignCategory(assignCategory);
			currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Initiation for Rejection Scenerio validation", ExtentColor.BLUE));
			// Initiating FOC Transfer as a Pre-Requisite for FOC Transfer Rejection at Approval Level 2
			quantity = _masterVO.getProperty("InitiateFOCAmount");
			remarks = "Automated FOC Initiation";
			map= FOCTransfer.initiateFOCTransfer(userMSISDN, productType, quantity,shortName, remarks);
			TransactionID= map.get("TRANSACTION_ID");
			PREF_FOCApprovalLevels = Integer.parseInt(map.get("PREF_FOC_APPROVAL_LEVEL"));
			currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Level 1 Approval for Rejection Scenerio validation", ExtentColor.BLUE));
			FOCTransfer.performFOCApprovalLevel1(userMSISDN, TransactionID);
			currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Rejection at Level 2 Approval Begins", ExtentColor.BLUE));
			RejectionMessage = FOCTransfer.rejectFOCApprovalLevel2(userMSISDN, TransactionID);
			
			/*
			 * Test Case Number 2.4: FOC Message Validation on successful FOC Approval 2 Rejection
			 */
			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UFOCTRANSFER12").getExtentCase(), category,parentCategory,productType));
			currentNode.assignCategory(assignCategory);
			ExpectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.foctransferapprovaldetailview.msg.level2cancel", TransactionID);
			Assertion.assertEquals(RejectionMessage, ExpectedMessage);
		}
		
		if (PREF_FOCApprovalLevels > 2) {			
			/*
			 * Test Case Number 2.5: FOC Rejection at Approval Level 3
			 */
			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UFOCTRANSFER13").getExtentCase(), category,parentCategory,productType));
			currentNode.assignCategory(assignCategory);
			currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Initiation for Rejection Scenerio validation", ExtentColor.BLUE));
			// Initiating FOC Transfer as a Pre-Requisite for FOC Transfer Rejection at Approval Level 3
			quantity = _masterVO.getProperty("InitiateFOCAmount");
			remarks = "Automated FOC Initiation";
			map= FOCTransfer.initiateFOCTransfer(userMSISDN, productType, quantity,shortName, remarks);
			TransactionID= map.get("TRANSACTION_ID");
			PREF_FOCApprovalLevels = Integer.parseInt(map.get("PREF_FOC_APPROVAL_LEVEL"));
			currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Level 1 Approval for Rejection Scenerio validation", ExtentColor.BLUE));
			FOCTransfer.performFOCApprovalLevel1(userMSISDN, TransactionID);
			currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Level 2 Approval for Rejection Scenerio validation", ExtentColor.BLUE));
			FOCTransfer.performFOCApprovalLevel2(userMSISDN, TransactionID, quantity);
			currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Rejection at Level 3 Approval Begins", ExtentColor.BLUE));
			RejectionMessage = FOCTransfer.rejectFOCApprovalLevel3(userMSISDN, TransactionID);
			
			/*
			 * Test Case Number 2.6: FOC Message Validation on successful FOC Approval 3 Rejection
			 */
			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UFOCTRANSFER14").getExtentCase(), category,parentCategory,productType));
			currentNode.assignCategory(assignCategory);
			ExpectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.foctransferapprovaldetailview.msg.level3cancel", TransactionID);
			Assertion.assertEquals(RejectionMessage, ExpectedMessage);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	/**
	 * DataProvider for Operator to Channel transfer
	 * @return Object
	 */
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed() {
		String FOCTransferCode = _masterVO.getProperty("FOCCode");
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
			if (aList.contains(FOCTransferCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
			}
		}

/*
 * Counter to count number of users exists in channel users hierarchy sheet 
 * of Categories for which O2C transfer is allowed
 */
		ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
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
			ExcelUtility.setExcelFile(MasterSheetPath,"Channel Users Hierarchy");
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
		Object[][] FOCData = new Object[countTotal][6];      
		for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
			FOCData[j][0] = Data[k][0];
			FOCData[j][1] = Data[k][1];
			FOCData[j][2] = Data[k][2];
			FOCData[j][3] = ProductObject[i][0];
			FOCData[j][4] = ProductObject[i][1];
			FOCData[j][5] = ProductObject[i][2];
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
			return FOCData;
	}
}
