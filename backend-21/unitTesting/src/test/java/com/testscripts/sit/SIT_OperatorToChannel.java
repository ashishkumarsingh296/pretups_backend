package com.testscripts.sit;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;

import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.CommissionProfile;
import com.Features.O2CTransfer;
import com.Features.O2CTransferRule;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.OperatorToChannelMap;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CaseMaster;
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
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_O2C_TRANSFER)
public class SIT_OperatorToChannel extends BaseTest {
	static String masterSheetPath;
	String assignCategory="SIT";
	
	@Test(dataProvider="usersProvider")
	@TestManager(TestKey = "PRETUPS-1144") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASEA_CUSuspendedO2C(HashMap<String, String> userDetails) {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
				
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap.putAll(userDetails);
		
		String moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CTRF1").getModuleCode();
		
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITO2CTRF1").getExtentCase(), transferMap.get("FROM_CATEGORY_NAME"),transferMap.get("PRODUCT_NAME")));
		currentNode.assignCategory(assignCategory);
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Channel User");
        suspendCHNLUser.suspendChannelUser_MSISDN(transferMap.get("TO_MSISDN"), "CASEA_CuspendedO2C");
        ExtentI.Markup(ExtentColor.TEAL, "Approving Channel User Suspend Request");
		suspendCHNLUser.approveCSuspendRequest_MSISDN(transferMap.get("TO_MSISDN"), "CASEA_CuspendedO2C");
	
		try{
			ExtentI.Markup(ExtentColor.TEAL, "Initiating O2C Transfer");
			O2CTransfer.initiateO2CTransfer(transferMap);
			Assertion.assertFail("O2C Transfer initiation is successful, hence Test Case failed");
		} catch(Exception e){
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.usersuspended", transferMap.get("TO_MSISDN"));
			Log.info("Message fetched from WEB as : "+actualMessage);
			Assertion.assertEquals(actualMessage, expectedMessage);
		}
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Channel User");
		resumeCHNLUser.resumeChannelUser_MSISDN(transferMap.get("TO_MSISDN"), "Auto Resume Remarks");
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="usersProvider")
	@TestManager(TestKey = "PRETUPS-1145") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASEB_CUInSuspendedO2C(HashMap<String, String> userDetails) throws InterruptedException {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		ChannelUser chnlUsr = new ChannelUser(driver);
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		HashMap<String, String> modificationMap = new HashMap<String, String>();
		
		String moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CTRF2").getModuleCode();
				
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap.putAll(userDetails);
		
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITO2CTRF2").getExtentCase(), transferMap.get("FROM_CATEGORY_NAME"),transferMap.get("PRODUCT_NAME")));
		currentNode.assignCategory(assignCategory);
		modificationMap.put("inSuspend_chk", "true");
		modificationMap.put("searchMSISDN", transferMap.get("TO_MSISDN"));
		ExtentI.Markup(ExtentColor.TEAL, "In Suspending Channel User");
		chnlUsr.modifyChannelUserDetails(transferMap.get("TO_CATEGORY"), modificationMap);
		try{
			ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
			O2CTransfer.initiateO2CTransfer(transferMap);
			Assertion.assertFail("O2C Transfer initiation is successful, hence Test Case failed");
		} catch(Exception e){
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.transfernotallowed");
			Log.info("Message fetched from WEB as : "+actualMessage);
			Assertion.assertEquals(actualMessage, expectedMessage);
		}
		modificationMap.put("inSuspend_chk", "false");
		ExtentI.Markup(ExtentColor.TEAL, "Removing In Suspended Status from Channel User");
		chnlUsr.modifyChannelUserDetails(transferMap.get("TO_CATEGORY"), modificationMap);
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider="usersProvider")
	@TestManager(TestKey = "PRETUPS-1146") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASEC_CUTCPSuspendedO2C(HashMap<String, String> userDetails) throws InterruptedException {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		
		String moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CTRF3").getModuleCode();
				
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap.putAll(userDetails);
		
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITO2CTRF3").getExtentCase(), transferMap.get("FROM_CATEGORY_NAME"),transferMap.get("PRODUCT_NAME")));
		currentNode.assignCategory(assignCategory);
		ExtentI.Markup(ExtentColor.TEAL, "Suspending Transfer Control Profile");
		trfCntrlProf.channelLevelTransferControlProfileSuspend(0, transferMap.get("TO_DOMAIN"), transferMap.get("TO_CATEGORY"), transferMap.get("TO_NA_TCP_NAME"), "NULL");
		
		try {
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		O2CTransfer.initiateO2CTransfer(transferMap);
		Assertion.assertFail("O2C Transfer initiation is successful, hence Test Case failed");
		} catch (Exception e) {
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.selectcategoryforfoctransfer.errormsg.transferprofilenotactive", transferMap.get("TO_MSISDN"));
			Log.info("Message fetched from WEB as : "+actualMessage);
			Assertion.assertEquals(actualMessage, expectedMessage);
		}
		ExtentI.Markup(ExtentColor.TEAL, "Resuming Transfer Control Profile");
		trfCntrlProf.channelLevelTransferControlProfileActive(0, transferMap.get("TO_DOMAIN"), transferMap.get("TO_CATEGORY"), transferMap.get("TO_NA_TCP_NAME"), "NULL");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
		
	@Test(dataProvider="usersProvider")
	@TestManager(TestKey = "PRETUPS-1147") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASED_CUMaxResidualBalanceO2C(HashMap<String, String> userDetails) throws InterruptedException {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		TransferControlProfile trfCntrlProf = new TransferControlProfile(driver);
		
		String moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CTRF4").getModuleCode();
				
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap.putAll(userDetails);
		
		currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITO2CTRF4").getExtentCase(), transferMap.get("FROM_CATEGORY_NAME"),transferMap.get("PRODUCT_NAME")));
		currentNode.assignCategory(assignCategory);
		
		ExtentI.Markup(ExtentColor.TEAL, "Modifying Maximum Residual Balance in Transfer Control Profile");
		trfCntrlProf.modifyTCPmaximumBalance(transferMap.get("TO_DOMAIN"), transferMap.get("TO_CATEGORY"), transferMap.get("TO_NA_TCP_ID"), "50","49", transferMap.get("PRODUCT_NAME"));
		
		try {
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Level 1 Approval");
		O2CTransfer.performingLevel1Approval(transferMap);
		Assertion.assertFail("O2C Transfer initiation is successful, hence Test Case failed");
		} catch (Exception e) {
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("error.transfer.maxbalance.reached", transferMap.get("PRODUCT_SHORT_NAME"));
			Log.info("Message fetched from WEB as : "+actualMessage);
			Assertion.assertEquals(actualMessage, expectedMessage);
		}
		ExtentI.Markup(ExtentColor.TEAL, "Updating Maximum Residual Balance in Transfer Control Profile");
		trfCntrlProf.modifyTCPmaximumBalance(transferMap.get("TO_DOMAIN"), transferMap.get("TO_CATEGORY"), transferMap.get("TO_NA_TCP_ID"), _masterVO.getProperty("MaximumBalance"), _masterVO.getProperty("AlertingCount"), transferMap.get("PRODUCT_NAME"));
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1148") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASEE_NullDistributionType() {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		
		String moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CTRF5").getModuleCode();
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITO2CTRF5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap.put("TO_STOCK_TYPE", null);
		transferMap = getProductDetails(transferMap);
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		try {
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
		if (transferMap.get("STOCK_TYPE_DROPDOWN_STATUS").equals("false"))
			Assertion.assertSkip("Stock Type Dropdown is not available in system, hence test case skipped.");
		else
			Assertion.assertFail("Operator to Channel Transfer Initiated successfully, hence Test Case Failed");
		} catch(SkipException e) {
			Log.info("Test case skipped");
		} catch(Exception e) {
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.searchchanneluser.distributortype.required");
			Assertion.assertEquals(actualMessage, expectedMessage);
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	} 
	
	@Test
	@TestManager(TestKey = "PRETUPS-1149") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASEF_NullMSISDN() {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		
		String moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CTRF6").getModuleCode();
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITO2CTRF6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap.put("TO_MSISDN", "");
		transferMap = getProductDetails(transferMap);
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		try {
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
		Assertion.assertFail("Operator to Channel Transfer Initiated successfully, hence Test Case Failed");
		} catch(Exception e) {
		String actualMessage = O2CTransfer.getErrorMessage();
		String expectedMessage = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("channeltransfer.searchchanneluser.label.userCode"));
		Assertion.assertEquals(actualMessage, expectedMessage);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1150") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASEG_NullProductType() {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		
		String moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CTRF7").getModuleCode();
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		int productCount = ExcelUtility.getRowCount(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITO2CTRF7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (productCount > 1) {
			HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("PRODUCT_TYPE", null);
			ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
			try {
			transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
			Assertion.assertFail("Operator to Channel Transfer Initiated successfully, hence Test Case Failed");
			} catch(Exception e) {
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("channeltransfer.searchchanneluser.label.producttype"));
			Assertion.assertEquals(actualMessage, expectedMessage);
			
			}
		} else {
			Assertion.assertSkip("Only Single Product is available in system, hence Test Case Skipped");
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1151") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASEH_NullInitiationAmount() {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		
		String moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CTRF8").getModuleCode();
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITO2CTRF8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap = getProductDetails(transferMap);
		transferMap.put("INITIATION_AMOUNT", "");
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		try {
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
		Assertion.assertFail("Operator to Channel Transfer Initiated successfully, hence Test Case Failed");
		} catch(Exception e) {
		String actualMessage = O2CTransfer.getErrorMessage();
		String expectedMessage = MessagesDAO.getLabelByKey("channeltransfer.transferdetails.error.noproductselect");
		Assertion.assertEquals(actualMessage, expectedMessage);
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1152") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASEI_AlphaNumericAmount() {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		
		String moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CTRF9").getModuleCode();
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITO2CTRF9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap = getProductDetails(transferMap);
		RandomGeneration RandomGenerator = new RandomGeneration();
		transferMap.put("INITIATION_AMOUNT", RandomGenerator.randomAlphaNumeric(10));
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		try {
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
		Assertion.assertFail("Operator to Channel Transfer Initiated successfully, hence Test Case Failed");
		} catch(Exception e) {
		String actualMessage = O2CTransfer.getErrorMessage();
		String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetails.error.qtynumeric", transferMap.get("PRODUCT_NAME"));
		Assertion.assertEquals(actualMessage, expectedMessage);
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1153") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASEJ_SpecialCharacterInAmount() {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		
		String moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CTRF10").getModuleCode();
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITO2CTRF10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap = getProductDetails(transferMap);
		transferMap.put("INITIATION_AMOUNT", "or '1' = '1'");
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		try {
			transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
			Assertion.assertFail("Operator to Channel Transfer Initiated successfully, hence Test Case Failed");
		} catch(Exception e) {
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetails.error.qtynumeric", transferMap.get("PRODUCT_NAME"));
			Assertion.assertEquals(actualMessage, expectedMessage);
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1154") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASEK_MultipleOfCommissionAmount() throws InterruptedException, ParseException {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		
		String moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CTRF11").getModuleCode();
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITO2CTRF11").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap = getProductDetails(transferMap);
		
		ExtentI.Markup(ExtentColor.TEAL, "Modify multiple of value in Commission profile");
		String multiple = "1";
		long commissionApplicableTime = commissionProfile.modifyCommissionProfileMultipleOf(transferMap.get("TO_DOMAIN"),
				transferMap.get("TO_CATEGORY"), transferMap.get("TO_GRADE"), transferMap.get("TO_COMMISSION_PROFILE"),
				transferMap.get("PRODUCT_NAME"), multiple);
		
		transferMap.put("INITIATION_AMOUNT", "100.5");
		
		Thread.sleep(commissionApplicableTime);
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		try {
			transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
			Assertion.assertFail("Operator to Channel Transfer Initiated successfully, hence Test Case Failed");
		} catch(Exception e) {
			String actualMessage = O2CTransfer.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetails.error.multipleof", transferMap.get("PRODUCT_NAME"), multiple);
			Assertion.assertEquals(actualMessage, expectedMessage);
		}
		
		ExtentI.Markup(ExtentColor.TEAL, "Reverting the multiple of value of commission profile");
		commissionProfile.modifyCommissionProfileMultipleOf(transferMap.get("TO_DOMAIN"),
				transferMap.get("TO_CATEGORY"), transferMap.get("TO_GRADE"), transferMap.get("TO_COMMISSION_PROFILE"),
				transferMap.get("PRODUCT_NAME"), _masterVO.getProperty("MultipleOf"));
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1155") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASEL_AlphaNumericExternalTxnNo() throws InterruptedException {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		
		String EXTERNAL_TXN_NUMERIC = DBHandler.AccessHandler.getSystemPreference("EXTERNAL_TXN_NUMERIC");
		CaseMaster CaseMaster = null;
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		O2CTransferRule O2CTransferRule = new O2CTransferRule(driver);
		RandomGeneration RandomGeneration = new RandomGeneration();
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		
		if (EXTERNAL_TXN_NUMERIC.equalsIgnoreCase("true"))
			CaseMaster = _masterVO.getCaseMasterByID("SITO2CTRF13");
		else
			CaseMaster = _masterVO.getCaseMasterByID("SITO2CTRF12");
		
		long FirstApprovalLimit = 100;
		long SecondApprovalLimit = 200;
		
		currentNode=test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory(assignCategory);		
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap = getProductDetails(transferMap);
		
		String[] originalApprovalLimits = DBHandler.AccessHandler.o2cApprovalLimits(transferMap.get("TO_CATEGORY"), _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		
		String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		if (directO2CPreference == null ||!directO2CPreference.equalsIgnoreCase("true")) {
			O2CTransferRule.modifyTransferRule(transferMap.get("TO_DOMAIN"), transferMap.get("TO_CATEGORY"), Long.toString(FirstApprovalLimit), Long.toString(SecondApprovalLimit));
			new UpdateCache().updateCache();
			transferMap.put("INITIATION_AMOUNT", "" + (FirstApprovalLimit - 1));
			transferMap.put("EXTERNAL_TXN_NUM", RandomGeneration.randomAlphaNumeric(10));
			
			transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
			
			if (EXTERNAL_TXN_NUMERIC.equalsIgnoreCase("true")) {
				try {
					transferMap = (HashMap<String, String>) O2CTransfer.performingLevel1Approval(transferMap);
					Assertion.assertFail("O2C Transfer Request Approval at Level 1 Successfully");
				} catch (Exception ex) {
					String actualMessage = O2CTransfer.getErrorMessage();
					String expectedMessage = MessagesDAO.prepareMessageByKey("message.channeltransfer.externaltxnnumbernotnumeric");
					Assertion.assertEquals(actualMessage, expectedMessage);
				}
			} else {
				transferMap = (HashMap<String, String>) O2CTransfer.performingLevel1Approval(transferMap);
				String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", transferMap.get("TRANSACTION_ID"));
				Assertion.assertEquals(transferMap.get("actualMessage"), expectedMessage);
			}
			
			O2CTransferRule.modifyTransferRule(transferMap.get("TO_DOMAIN"), transferMap.get("TO_CATEGORY"), _parser.getDisplayAmount(Long.parseLong(originalApprovalLimits[0])), _parser.getDisplayAmount(Long.parseLong(originalApprovalLimits[1])));
			new UpdateCache().updateCache();		
			} else {
				Assertion.assertSkip("Direct Operator to Channel is applicable in system");
			}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-1855") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void CASEM_NegativeInitiationAmount() {
		final String methodName = "Test_O2C_Transfer";
		Log.startTestCase(methodName);
		
		String moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITO2CTRF14").getModuleCode();
		
		O2CTransfer O2CTransfer = new O2CTransfer(driver);
		OperatorToChannelMap _mapgenerator = new OperatorToChannelMap();
		
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITO2CTRF14").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		HashMap<String, String> transferMap = _mapgenerator.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
		transferMap = getProductDetails(transferMap);
		transferMap.put("INITIATION_AMOUNT", "-1");
		ExtentI.Markup(ExtentColor.TEAL, "Performing O2C Transfer");
		try {
		transferMap = O2CTransfer.initiateO2CTransfer(transferMap);
		} catch(Exception e) {
		String actualMessage = O2CTransfer.getErrorMessage();
		String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.chnltochnlviewproduct.error.qtygtzero", transferMap.get("PRODUCT_NAME"));
		Assertion.assertEquals(actualMessage, expectedMessage);
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
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
