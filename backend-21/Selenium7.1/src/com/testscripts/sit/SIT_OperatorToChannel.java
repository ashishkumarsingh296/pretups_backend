package com.testscripts.sit;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.O2CTransfer;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.OperatorToChannelMap;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_OperatorToChannel extends BaseTest {
	
	static boolean testCaseCounter = false;
	static String masterSheetPath;
	
	@Test(dataProvider="usersProvider")
	public void CASEA_CUSuspendedO2C(HashMap<String, String> userDetails) {
		SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
				
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap.putAll(userDetails);
				
		if (testCaseCounter == false) {
			test=extent.createTest("[SIT]O2C Transfer");
			testCaseCounter = true;
		}
		
		currentNode=test.createNode("To verify that "+ transferMap.get("FROM_CATEGORY_NAME") +" is not able to perform O2C Transfer for " + transferMap.get("PRODUCT_NAME") + " if Channel User is suspended.");
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Channel User");
        suspendCHNLUser.suspendChannelUser_MSISDN(transferMap.get("MSISDN"), "CASEA_CuspendedO2C");
        ExtentI.Markup(ExtentColor.TEAL, "Approving Channel User Suspend Request");
		suspendCHNLUser.approveCSuspendRequest_MSISDN(transferMap.get("MSISDN"), "CASEA_CuspendedO2C");
	
		try{
			ExtentI.Markup(ExtentColor.TEAL, "Initiating O2C Transfer");
			O2CTransfer.initiateO2CTransfer(transferMap);
			currentNode.log(Status.FAIL, "O2C Transfer initiation is successful, hence Test Case failed");
		} catch(Exception e){
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.usersuspended", transferMap.get("MSISDN"));
			Log.info("Message fetched from WEB as : "+actualMessage);
			Validator.messageCompare(actualMessage, expectedMessage);
		}
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Channel User");
		resumeCHNLUser.resumeChannelUser_MSISDN(transferMap.get("MSISDN"), "Auto Resume Remarks");
	}
	
	@Test(dataProvider="usersProvider")
	public void CASEB_CUInSuspendedO2C(HashMap<String, String> userDetails) throws InterruptedException {
		ChannelUser chnlUsr = new ChannelUser(driver);
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		HashMap<String, String> modificationMap = new HashMap<String, String>();
		
		if (testCaseCounter == false) {
			test=extent.createTest("[SIT]O2C Transfer");
			testCaseCounter = true;
		}
				
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap.putAll(userDetails);
		
		currentNode=test.createNode("To verify that "+ transferMap.get("FROM_CATEGORY_NAME") +" is not able to perform O2C Transfer for " + transferMap.get("PRODUCT_NAME") + " product if Channel User is IN Suspended.");
		currentNode.assignCategory("SIT");
		modificationMap.put("inSuspend_chk", "true");
		modificationMap.put("searchMSISDN", transferMap.get("MSISDN"));
		ExtentI.Markup(ExtentColor.TEAL, "In Suspending Channel User");
		chnlUsr.modifyChannelUserDetails(transferMap.get("TO_CATEGORY"), modificationMap);
		try{
			ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
			O2CTransfer.initiateO2CTransfer(transferMap);
			currentNode.log(Status.FAIL, "O2C Transfer initiation is successful, hence Test Case failed");
		} catch(Exception e){
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.transfernotallowed");
			Log.info("Message fetched from WEB as : "+actualMessage);
			Validator.messageCompare(actualMessage, expectedMessage);
		}
		modificationMap.put("inSuspend_chk", "false");
		ExtentI.Markup(ExtentColor.TEAL, "Removing In Suspended Status from Channel User");
		chnlUsr.modifyChannelUserDetails(transferMap.get("TO_CATEGORY"), modificationMap);
	}
	
	
	@Test(dataProvider="usersProvider")
	public void CASEC_CUTCPSuspendedO2C(HashMap<String, String> userDetails) throws InterruptedException {
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		
		if (testCaseCounter == false) {
			test=extent.createTest("[SIT]O2C Transfer");
			testCaseCounter = true;
		}
				
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap.putAll(userDetails);
		
		currentNode=test.createNode("To verify that "+ transferMap.get("FROM_CATEGORY_NAME") +" is not able to perform O2C Transfer for " + transferMap.get("PRODUCT_NAME") + " product if Channel User's TCP is suspended.");
		currentNode.assignCategory("SIT");
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Transfer Control Profile");
		trfCntrlProf.channelLevelTransferControlProfileSuspend(0, transferMap.get("TO_DOMAIN"), transferMap.get("TO_CATEGORY"), transferMap.get("NA_TCP_NAME"), "NULL");
		
		try {
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		O2CTransfer.initiateO2CTransfer(transferMap);
		currentNode.log(Status.FAIL, "O2C Transfer initiation is successful, hence Test Case failed");
		} catch (Exception e) {
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.transferprofilenotactive", transferMap.get("MSISDN"));
			Log.info("Message fetched from WEB as : "+actualMessage);
			Validator.messageCompare(actualMessage, expectedMessage);
		}
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Transfer Control Profile");
		trfCntrlProf.channelLevelTransferControlProfileActive(0, transferMap.get("TO_DOMAIN"), transferMap.get("TO_CATEGORY"), transferMap.get("NA_TCP_NAME"), "NULL");
	}

	
	@Test(dataProvider="usersProvider")
	public void CASED_CUMaxResidualBalanceO2C(HashMap<String, String> userDetails) throws InterruptedException {
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		
		if (testCaseCounter == false) {
			test=extent.createTest("[SIT]O2C Transfer");
			testCaseCounter = true;
		}
				
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap.putAll(userDetails);
		
		currentNode=test.createNode("To verify that "+ transferMap.get("FROM_CATEGORY_NAME") +" is not able to perform O2C Transfer for " + transferMap.get("PRODUCT_NAME") + " product if Channel User's Maximum Residual Balance is reached.");
		currentNode.assignCategory("SIT");
		
		ExtentI.Markup(ExtentColor.TEAL, "Modifying Maximum Residual Balance in Transfer Control Profile");
		trfCntrlProf.modifyTCPmaximumBalance(transferMap.get("TO_DOMAIN"), transferMap.get("TO_CATEGORY"), transferMap.get("NA_TCP_ID"), "50","49", transferMap.get("PRODUCT_NAME"));
		
		try {
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Level 1 Approval");
		O2CTransfer.performingLevel1Approval(transferMap);
		currentNode.log(Status.FAIL, "O2C Transfer initiation is successful, hence Test Case failed");
		} catch (Exception e) {
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("error.transfer.maxbalance.reached", transferMap.get("PRODUCT_SHORT_NAME"));
			Log.info("Message fetched from WEB as : "+actualMessage);
			Validator.messageCompare(actualMessage, expectedMessage);
		}
		ExtentI.Markup(ExtentColor.TEAL, "Updating Maximum Residual Balance in Transfer Control Profile");
		trfCntrlProf.modifyTCPmaximumBalance(transferMap.get("TO_DOMAIN"), transferMap.get("TO_CATEGORY"), transferMap.get("NA_TCP_ID"), _masterVO.getProperty("MaximumBalance"), _masterVO.getProperty("AlertingCount"), transferMap.get("PRODUCT_NAME"));
	}
	
	@Test
	public void CASEE_NullDistributionType() {
		Log.startTestCase(this.getClass().getName());
		
		if (testCaseCounter == false) { 
			test = extent.createTest("[SIT]O2C Transfer");
			testCaseCounter = true;
		}
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		
		currentNode=test.createNode("To verify that proper error message is displayed if user does not select stock type while performing Operator to Channel Transfer");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap.put("STOCK_TYPE", null);
		transferMap = getProductDetails(transferMap);
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		try {
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
		if (transferMap.get("STOCK_TYPE_DROPDOWN_STATUS").equals("false"))
			currentNode.log(Status.SKIP, "Stock Type Dropdown is not available in system, hence test case skipped.");
		else
			Log.failNode("Operator to Channel Transfer Initiated successfully, hence Test Case Failed");
		} catch(Exception e) {
		String actualMessage = O2CTransfer.getErrorMessage();
		String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.searchchanneluser.distributortype.required");
		Validator.messageCompare(actualMessage, expectedMessage);
		}
	} 
	
	@Test
	public void CASEF_NullMSISDN() {
		Log.startTestCase(this.getClass().getName());
		
		if (testCaseCounter == false) { 
			test = extent.createTest("[SIT]O2C Transfer");
			testCaseCounter = true;
		}
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		
		currentNode=test.createNode("To verify that proper error message is displayed if user does not select stock type while performing Operator to Channel Transfer");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap.put("MSISDN", "");
		transferMap = getProductDetails(transferMap);
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		try {
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
		Log.failNode("Operator to Channel Transfer Initiated successfully, hence Test Case Failed");
		} catch(Exception e) {
		String actualMessage = O2CTransfer.getErrorMessage();
		String expectedMessage = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("channeltransfer.searchchanneluser.label.userCode"));
		Validator.messageCompare(actualMessage, expectedMessage);
		}
	}
	
	@Test
	public void CASEG_NullProductType() {
		Log.startTestCase(this.getClass().getName());
		
		if (testCaseCounter == false) { 
			test = extent.createTest("[SIT]O2C Transfer");
			testCaseCounter = true;
		}
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		
		currentNode=test.createNode("To verify that proper error message is displayed if user does not select stock type while performing Operator to Channel Transfer");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		try {
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
		Log.failNode("Operator to Channel Transfer Initiated successfully, hence Test Case Failed");
		} catch(Exception e) {
		String actualMessage = O2CTransfer.getErrorMessage();
		String expectedMessage = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("channeltransfer.searchchanneluser.label.producttype"));
		Validator.messageCompare(actualMessage, expectedMessage);
		}
	}
	
	@Test
	public void CASEH_NullInitiationAmount() {
		Log.startTestCase(this.getClass().getName());
		
		if (testCaseCounter == false) { 
			test = extent.createTest("[SIT]O2C Transfer");
			testCaseCounter = true;
		}
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		
		currentNode=test.createNode("To verify that proper error message is displayed if user does not enter Initiation Amount while performing Operator to Channel Transfer");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap = getProductDetails(transferMap);
		transferMap.put("INITIATION_AMOUNT", "");
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		try {
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
		Log.failNode("Operator to Channel Transfer Initiated successfully, hence Test Case Failed");
		} catch(Exception e) {
		String actualMessage = O2CTransfer.getErrorMessage();
		String expectedMessage = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("channeltransfer.searchchanneluser.label.producttype"));
		Validator.messageCompare(actualMessage, expectedMessage);
		}
	}
	
	@DataProvider(name="usersProvider")
	public Object[] loadProducts() {
		
		Object userDetails[][] = UserAccess.getUsersWithAccess(RolesI.INITIATE_O2C_TRANSFER_ROLECODE);
		int usersRowCounter = userDetails.length;
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int prodRowCount = ExcelUtility.getRowCount();
		Object[][] ProductObject = new Object[prodRowCount][3];
		for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
			ProductObject[i][0] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, j);
			ProductObject[i][1] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, j);
			ProductObject[i][2] = ExcelUtility.getCellData(0, ExcelI.SHORT_NAME, j);
		}
		
		Object iteratorObject[] = new Object[usersRowCounter * prodRowCount];
		int objIterator = 0;
		
		for (int i=0; i<usersRowCounter; i++) {
			for (int j=0; j<prodRowCount; j++) {
				HashMap<String, String> userDetailMap = new HashMap<String, String>();
				userDetailMap.put("FROM_CATEGORY_CODE", userDetails[i][0].toString());
				userDetailMap.put("FROM_CATEGORY_PARENT_NAME", userDetails[i][1].toString());
				userDetailMap.put("FROM_CATEGORY_NAME", userDetails[i][2].toString());
				userDetailMap.put("FROM_USER_NAME", userDetails[i][3].toString());
				userDetailMap.put("LOGIN_ID", userDetails[i][4].toString());
				userDetailMap.put("PASSWORD", userDetails[i][5].toString());
				userDetailMap.put("PIN", userDetails[i][6].toString());
				userDetailMap.put("PRODUCT_TYPE", ProductObject[j][0].toString());
				userDetailMap.put("PRODUCT_NAME", ProductObject[j][1].toString());
				userDetailMap.put("PRODUCT_SHORT_NAME", ProductObject[j][2].toString());
				iteratorObject[objIterator] = userDetailMap;
				objIterator++;
			}
		}
		
		return iteratorObject;
	}
	
	public HashMap<String, String> getProductDetails(HashMap<String, String> transferMap) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		transferMap.put("PRODUCT_TYPE", ExcelUtility.getCellData(0, "PRODUCT_TYPE", 1));
		transferMap.put("PRODUCT_NAME", ExcelUtility.getCellData(0, "PRODUCT_NAME", 1));
		transferMap.put("PRODUCT_SHORT_NAME", ExcelUtility.getCellData(0, "PRODUCT_SHORT_NAME", 1));
		return transferMap;
	}

}
