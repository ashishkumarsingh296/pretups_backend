package com.testscripts.sit;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.FOCTransfer;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.TransferControlProfile;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_FOCTransfer extends BaseTest {
	
	boolean TestCaseCounter = false;
	final static String assignCategory = "SIT";

	/*
	 * Test Case Number 1: To verify that Channel Admin is able to perform FOC Transfer successfully
	 * 					   The test case covers Approval Levels & Message validation
	 */
	@Test(dataProvider="categoryData", priority=1)
	public void TC1_MultipleOfFOCTransfer(String domainname, String parentCategory, String category, String userMSISDN, String tcpname, String tcpid, String productType, String productCode, String productName) throws InterruptedException, ParseException, SQLException {
		final String methodname = "TC1_MultipleOfFOCTransfer";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITFOCTRANSFER01").getModuleCode());
			TestCaseCounter = true;
		}

		String quantity = "100";
		String remarks = "Automated FOC Initiation";
		FOCTransfer FOCTransfer = new FOCTransfer(driver);
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		
		/*
		 * Test Case Number 1.1: FOC Transfer Initiation
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITFOCTRANSFER01").getExtentCase(), category));
		currentNode.assignCategory(assignCategory);
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Transfer Control Profile");
		trfCntrlProf.channelLevelTransferControlProfileSuspend(0, domainname, category, tcpname, tcpid);
		try {
			FOCTransfer.initiateFOCTransfer(userMSISDN, productType, quantity, productName, remarks);
			currentNode.fail("FOC Transfer Initiated Successfully!");
		} catch (Exception e) {
			String actualMessage = FOCTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("transferprofile.notactive.msg", userMSISDN);
			Validator.messageCompare(actualMessage, expectedMessage);
		}
		
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Transfer Control Profile");
		trfCntrlProf.channelLevelTransferControlProfileActive(0, domainname, category, tcpname, tcpid);
	}
	
	/*
	 * Test Case Number 2: To verify that Channel Admin cannot initiate FOC Stock if the amount entered is Alphanumeric.
	 */
	@Test(dataProvider="categoryData", priority=2)
	public void TC2_AlphaNumericFOCQuantity(String domainname, String parentCategory, String category, String userMSISDN, String tcpname, String tcpid, String productType, String productCode, String productName) throws InterruptedException, ParseException, SQLException {
		final String methodname = "TC2_AlphaNumericFOCQuantity";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITFOCTRANSFER02").getModuleCode());
			TestCaseCounter = true;
		}

		RandomGeneration RandomGeneration = new RandomGeneration();
		String quantity = RandomGeneration.randomAlphaNumeric(5);
		String remarks = "Automated FOC Initiation";
		FOCTransfer FOCTransfer = new FOCTransfer(driver);
		
		/*
		 * Test Case Number 1.1: FOC Transfer Initiation
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITFOCTRANSFER02").getExtentCase(), category));
		currentNode.assignCategory(assignCategory);
		try {
			FOCTransfer.initiateFOCTransfer(userMSISDN, productType, quantity,productName, remarks);
			currentNode.fail("FOC Transfer Initiated Successfully!");
		} catch (Exception e) {
			String actualMessage = FOCTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailsfoc.error.qtynumeric", productName);
			Validator.messageCompare(actualMessage, expectedMessage);
		}
	}
	
	/*
	 * Test Case Number 3: To verify that Channel Admin cannot initiate FOC Stock if the amount entered is Special Characters.
	 */
	@Test(dataProvider="categoryData", priority=3)
	public void TC3_SpecialCharacterFOCQuantity(String domainname, String parentCategory, String category, String userMSISDN, String tcpname, String tcpid, String productType, String productCode, String productName) throws InterruptedException, ParseException, SQLException {
		final String methodname = "TC3_SpecialCharacterFOCQuantity";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITFOCTRANSFER03").getModuleCode());
			TestCaseCounter = true;
		}

		String quantity = "!@#$%^&*";
		String remarks = "Automated FOC Initiation";
		FOCTransfer FOCTransfer = new FOCTransfer(driver);
		
		/*
		 * Test Case Number 1.1: FOC Transfer Initiation
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITFOCTRANSFER03").getExtentCase(), category));
		currentNode.assignCategory(assignCategory);
		try {
			FOCTransfer.initiateFOCTransfer(userMSISDN, productType, quantity, productName,remarks);
			currentNode.fail("FOC Transfer Initiated Successfully!");
		} catch (Exception e) {
			String actualMessage = FOCTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailsfoc.error.qtynumeric", productName);
			Validator.messageCompare(actualMessage, expectedMessage);
		}
	}
	
	@Test(dataProvider="categoryData", priority=4)
	public void TC4_SuspendedCUTransfer(String domainname, String parentCategory, String category, String userMSISDN, String tcpname, String tcpid, String productType, String productCode, String productName) throws InterruptedException, ParseException, SQLException {
		final String methodname = "TC4_SuspendedCUTransfer";
		Log.startTestCase(methodname);

		if (TestCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITFOCTRANSFER04").getModuleCode());
			TestCaseCounter = true;
		}

		String quantity = "100";
		String remarks = "Automated FOC Initiation";
		FOCTransfer FOCTransfer = new FOCTransfer(driver);
		SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
		
		/*
		 * Test Case Number 1.1: FOC Transfer Initiation
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITFOCTRANSFER04").getExtentCase(), category));
		currentNode.assignCategory(assignCategory);
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Channel User");
		suspendCHNLUser.suspendChannelUser_MSISDN(userMSISDN, methodname);
		ExtentI.Markup(ExtentColor.TEAL, "Approving Channel User Suspend Request");
		suspendCHNLUser.approveCSuspendRequest_MSISDN(userMSISDN, methodname
				);
		try {
			FOCTransfer.initiateFOCTransfer(userMSISDN, productType, quantity,productName, remarks);
			currentNode.fail("FOC Transfer Initiated Successfully!");
		} catch (Exception e) {
			String actualMessage = FOCTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.usersuspended", userMSISDN);
			Validator.messageCompare(actualMessage, expectedMessage);
		}
		
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Channel User");
		resumeCHNLUser.resumeChannelUser_MSISDN(userMSISDN, methodname);
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
		Object[][] Data = new Object[userCounter][6];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,"Channel Users Hierarchy");
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[j][0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
				Data[j][1] = ExcelUtility.getCellData(0,ExcelI.PARENT_CATEGORY_NAME, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][3] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
				Data[j][4] = ExcelUtility.getCellData(0, ExcelI.NA_TCP_NAME, i);
				Data[j][5] = ExcelUtility.getCellData(0, ExcelI.NA_TCP_PROFILE_ID, i);
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
		Object[][] FOCData = new Object[countTotal][9];      
		for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
			FOCData[j][0] = Data[k][0];
			FOCData[j][1] = Data[k][1];
			FOCData[j][2] = Data[k][2];
			FOCData[j][3] = Data[k][3];
			FOCData[j][4] = Data[k][4];
			FOCData[j][5] = Data[k][5];
			FOCData[j][6] = ProductObject[i][0];
			FOCData[j][7] = ProductObject[i][1];
			FOCData[j][8] = ProductObject[i][2];
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
