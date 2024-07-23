package com.testscripts.uap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.FOCTransfer;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

public class UAP_FOCTransfer extends BaseTest{
	
	static boolean TestCaseCounter = false;
	static String ActualFOCSuccessMessage = null;
	
	/*
	 * Test Case Number 2: To verify that Channel Admin is able to perform FOC Transfer successfully
	 * 					   The test case covers Approval Levels & Message validation
	 */
	@Test(dataProvider="categoryData")
	public void TC1_FOCTransfer(String parentCategory, String category, String userMSISDN, String productType) throws InterruptedException	{

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]FOC Transfer");
			TestCaseCounter = true;
		}

		String quantity = _masterVO.getProperty("InitiateFOCAmount");
		String remarks = "Automated FOC Initiation";

		FOCTransfer FOCTransfer = new FOCTransfer(driver);
		
		/*
		 * Test Case Number 1.1: FOC Transfer Initiation
		 */
		currentNode = test.createNode("To verify that Channel Admin is able to initiate FOC Transfer for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("UAP");
		Map<String, String> map= FOCTransfer.initiateFOCTransfer(userMSISDN, productType, quantity, remarks);
		String TransactionID= map.get("TRANSACTION_ID");
		//String MSISDN= map.get("CHANNELUSER_MSISDN");
		String ActualInitiateMessage= map.get("INITIATE_MESSAGE");
		int PREF_FOCApprovalLevels = Integer.parseInt(map.get("PREF_FOC_APPROVAL_LEVEL"));
		
		/*
		 * Test Case Number 1.2: FOC Transfer Initiation Message Validation
		 */
		currentNode = test.createNode("To verify that Proper Message is displayed on successful FOC Transfer Initiate for category "+category+" with parent category "+parentCategory+", product type "+productType);		
		currentNode.assignCategory("UAP");
		String expected= MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailsfocview.message.focsuccess", TransactionID);
		if (ActualInitiateMessage.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + ActualInitiateMessage + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
		/*
		 * Test Case Number 1.3: FOC Transfer Approval Level 1
		 */
		if (PREF_FOCApprovalLevels > 0) {
		currentNode = test.createNode("To verify that Channel Admin is able to perform FOC Transfer Approval 1 for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("UAP");
		ActualFOCSuccessMessage = FOCTransfer.performFOCApprovalLevel1(userMSISDN, TransactionID);
		}
		
		/*
		 * Test Case Number 1.4: FOC Transfer Approval Level 2
		 */
		if (PREF_FOCApprovalLevels > 1) {
		currentNode = test.createNode("To verify that Channel Admin is able to perform FOC Transfer Approval 2 for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("UAP");
		ActualFOCSuccessMessage = FOCTransfer.performFOCApprovalLevel2(userMSISDN, TransactionID, quantity);
		}
		
		/*
		 * Test Case Number 1.5: FOC Transfer Approval Level 3
		 */
		if (PREF_FOCApprovalLevels > 2) {
		currentNode = test.createNode("To verify that Channel Admin is able to perform FOC Transfer Approval 3 for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("UAP");
		ActualFOCSuccessMessage = FOCTransfer.performFOCApprovalLevel3(userMSISDN, TransactionID, quantity);
		}
		
		/*
		 * Test Case Number 1.6: FOC Final Message Validation
		 */
		currentNode = test.createNode("To verify that Proper Message is displayed on successful FOC Transfer for category "+category+" with parent category "+parentCategory+", product type "+productType);		
		currentNode.assignCategory("UAP");
		String FinalMessage = MessagesDAO.prepareMessageByKey("channeltransfer.foctransferapprovaldetailview.approval.success", TransactionID);
		if (ActualFOCSuccessMessage.equals(FinalMessage))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + FinalMessage + "] but found [" + ActualFOCSuccessMessage + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
	}
	
	/*
	 * Test Case Number 2: To verify that Channel Admin is able to Reject FOC Transfer at Level 1, Level 2 & Level 3 Approval.
	 * 					   Test Case Covers Message validation as well.
	 */
	@Test(dataProvider="categoryData")
	public void TC2_FOCRejection(String parentCategory, String category, String userMSISDN, String productType) throws NumberFormatException, SQLException, InterruptedException{
		
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]FOC Transfer");
			TestCaseCounter = true;
		}
		
		// Objects Initialization
		FOCTransfer FOCTransfer = new FOCTransfer(driver);
		
		/*
		 * Test Case Number 2.1: FOC Rejection At Approval Level 1
		 */
		currentNode = test.createNode("To verify that Channel Admin is able to Reject FOC Transfer Request at Level 1 Approval for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("UAP");
		currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Initiation for Rejection Scenerio validation", ExtentColor.BLUE));
		// Initiating FOC Transfer as a Pre-Requisite for FOC Transfer Rejection at Approval Level 1
		String quantity = _masterVO.getProperty("InitiateFOCAmount");
		String remarks = "Automated FOC Initiation";
		Map<String, String> map= FOCTransfer.initiateFOCTransfer(userMSISDN, productType, quantity, remarks);
		String TransactionID= map.get("TRANSACTION_ID");
		int PREF_FOCApprovalLevels = Integer.parseInt(map.get("PREF_FOC_APPROVAL_LEVEL"));
		currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Rejection at Level 1 Approval Begins", ExtentColor.BLUE));
		String RejectionMessage = FOCTransfer.rejectFOCApprovalLevel1(userMSISDN, TransactionID);
		
		/*
		 * Test Case Number 2.2: FOC Message Validation on successful FOC Approval 1 Rejection
		 */
		currentNode = test.createNode("To verify that Proper Message is displayed on Successful FOC Transfer Rejection at Approval Level 1 for category "+category+" with parent category "+parentCategory+", product type "+productType);
		currentNode.assignCategory("UAP");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.foctransferapprovaldetailview.msg.level1cancel", TransactionID);
		if (RejectionMessage.equals(ExpectedMessage))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + ExpectedMessage + "] but found [" + RejectionMessage + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
		
		if (PREF_FOCApprovalLevels > 1) {
			
			/*
			 * Test Case Number 2.3: FOC Rejection at Approval Level 2
			 */
			currentNode = test.createNode("To verify that Channel Admin is able to Reject FOC Transfer Request at Level 2 Approval for category "+category+" with parent category "+parentCategory+", product type "+productType);
			currentNode.assignCategory("UAP");
			currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Initiation for Rejection Scenerio validation", ExtentColor.BLUE));
			// Initiating FOC Transfer as a Pre-Requisite for FOC Transfer Rejection at Approval Level 2
			quantity = _masterVO.getProperty("InitiateFOCAmount");
			remarks = "Automated FOC Initiation";
			map= FOCTransfer.initiateFOCTransfer(userMSISDN, productType, quantity, remarks);
			TransactionID= map.get("TRANSACTION_ID");
			PREF_FOCApprovalLevels = Integer.parseInt(map.get("PREF_FOC_APPROVAL_LEVEL"));
			currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Level 1 Approval for Rejection Scenerio validation", ExtentColor.BLUE));
			FOCTransfer.performFOCApprovalLevel1(userMSISDN, TransactionID);
			currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Rejection at Level 2 Approval Begins", ExtentColor.BLUE));
			RejectionMessage = FOCTransfer.rejectFOCApprovalLevel2(userMSISDN, TransactionID);
			
			/*
			 * Test Case Number 2.4: FOC Message Validation on successful FOC Approval 2 Rejection
			 */
			currentNode = test.createNode("To verify that Proper Message is displayed on Successful FOC Transfer Rejection at Approval Level 2 for category "+category+" with parent category "+parentCategory+", product type "+productType);
			currentNode.assignCategory("UAP");
			ExpectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.foctransferapprovaldetailview.msg.level2cancel", TransactionID);
			if (RejectionMessage.equals(ExpectedMessage))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + ExpectedMessage + "] but found [" + RejectionMessage + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}
		
		if (PREF_FOCApprovalLevels > 2) {			
			/*
			 * Test Case Number 2.5: FOC Rejection at Approval Level 3
			 */
			currentNode = test.createNode("To verify that Channel Admin is able to Reject FOC Transfer Request at Level 3 Approval for category "+category+" with parent category "+parentCategory+", product type "+productType);
			currentNode.assignCategory("UAP");
			currentNode.log(Status.INFO, MarkupHelper.createLabel("FOC Initiation for Rejection Scenerio validation", ExtentColor.BLUE));
			// Initiating FOC Transfer as a Pre-Requisite for FOC Transfer Rejection at Approval Level 3
			quantity = _masterVO.getProperty("InitiateFOCAmount");
			remarks = "Automated FOC Initiation";
			map= FOCTransfer.initiateFOCTransfer(userMSISDN, productType, quantity, remarks);
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
			currentNode = test.createNode("To verify that Proper Message is displayed on Successful FOC Transfer Rejection at Approval Level 3 for category "+category+" with parent category "+parentCategory+", product type "+productType);
			currentNode.assignCategory("UAP");
			ExpectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.foctransferapprovaldetailview.msg.level3cancel", TransactionID);
			if (RejectionMessage.equals(ExpectedMessage))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + ExpectedMessage + "] but found [" + RejectionMessage + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}
		
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
		Object[] ProductObject = new Object[prodRowCount];
		for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
			ProductObject[i] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, j);
		}

/*
 * Creating combination of channel users for each product.
 */
		int countTotal = ProductObject.length * userCounter;
		Object[][] FOCData = new Object[countTotal][4];      
		for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
			FOCData[j][0] = Data[k][0];
			FOCData[j][1] = Data[k][1];
			FOCData[j][2] = Data[k][2];
			FOCData[j][3] = ProductObject[i];
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
