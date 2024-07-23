package com.testscripts.sit;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.Commission_CBC_CAC_Validations;
import com.Features.O2CTransfer;
import com.Features.mapclasses.CommissionProfileMap;
import com.Features.mapclasses.OperatorToChannelMap;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.UserAccess;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;
import com.commons.PretupsI;

public class SIT_Commission_CBC_CAC extends BaseTest{

	static boolean TestCaseCounter = false;
	HashMap<String,String> dataMap;
	String assignCategory="SIT";
	static String moduleCode;
	
	@DataProvider(name = "cbc_cac_validations")
	public HashMap<String, String> CBC_CAC_validations() {
		
		CommissionProfileMap commissionProfileMap = new CommissionProfileMap();
	    
		
		
		return dataMap;
	}
	
/*	//@Test(priority=1)
	public void A_Positive_CBC() {
		final String methodname = "A_Positive_CBC";
		Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) { 
				test = extent.createTest("[SIT] Commission Profile CBC CAC Validations");
				TestCaseCounter = true;
			}
			
			CommissionProfile commissionProfile = new CommissionProfile(driver);
			CommissionProfileMap commissionProfileMap = new CommissionProfileMap();
			HashMap<String, String> mapParam = commissionProfileMap.defaultMap();
			try{
				currentNode = test.createNode("To Validate preference CBC CAC working fine in Commission Profile");
				currentNode.assignCategory("SIT");
				commissionProfile.addCommissionProfile_cbcValidationSIT(mapParam);
			} catch(Exception e){
				Log.writeStackTrace(e);
			}
			
		Log.endTestCase(methodname);
	}*/
	
	@Test(priority=2)
	public void B_Positive_O2C() throws IOException, InterruptedException, ParseException, SQLException{
		final String methodname = "B_Positive_O2C";
		Log.startTestCase(methodname);
		
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITCOMMPROFILE24").getModuleCode();
		if (TestCaseCounter == false) {
			test = extent.createTest(moduleCode);
			TestCaseCounter = true;
		}
		
		Commission_CBC_CAC_Validations commissionProfile = new Commission_CBC_CAC_Validations(driver);
		OperatorToChannelMap OperatorToChannelMap = new OperatorToChannelMap();
		HashMap<String, String> operatorToChannelMap = OperatorToChannelMap.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		operatorToChannelMap.putAll(UserAccess.getUserWithAccess(RolesI.INITIATE_O2C_TRANSFER_ROLECODE));
		businessController businessController = new businessController(_masterVO.getProperty("O2CTransferCode"), null, operatorToChannelMap.get("TO_MSISDN"));
		HashMap<String, String> initiatedQty = new HashMap<String, String>();
		initiatedQty.put(operatorToChannelMap.get("PRODUCT_CODE"), operatorToChannelMap.get("INITIATION_AMOUNT"));
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(operatorToChannelMap.get("TO_CATEGORY"), _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);
		
		O2CTransfer o2cTransfer = new O2CTransfer(driver);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE24").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String versionBeforeUpdation = DBHandler.AccessHandler.getCommProfileVersion(operatorToChannelMap.get("TO_COMMISSION_PROFILE"));
		Log.info("The Commission Profile version is :" + versionBeforeUpdation);
		String profileDetailIDBeforeUpdation = DBHandler.AccessHandler.getCommProfileDetailsID(operatorToChannelMap.get("TO_COMMISSION_PROFILE"), operatorToChannelMap.get("PRODUCT_CODE"), versionBeforeUpdation);
		Log.info("The profile DetailID is : " + profileDetailIDBeforeUpdation);
		String OTFValueBeforeUpdatingCommProfile = DBHandler.AccessHandler.getOTFValue(operatorToChannelMap.get("TO_USER_NAME"), profileDetailIDBeforeUpdation);
		Log.info("The OTF Value before UpdatingCommProfile : " + OTFValueBeforeUpdatingCommProfile);
		
		long time2 = commissionProfile.modifyCBCCACCommissionProfile(operatorToChannelMap);
		Thread.sleep(time2);
		
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_WEB);
		Map<String, String> map = o2cTransfer.initiateO2CTransfer(operatorToChannelMap);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
			map= o2cTransfer.performingLevel1Approval(map.get("TO_MSISDN"), map.get("TRANSACTION_ID"));
			long netPayableAmount = _parser.getSystemAmount(map.get("NetPayableAmount"));
			
			if (netPayableAmount>firstApprov)
				o2cTransfer.performingLevel2Approval(map.get("TO_MSISDN"), map.get("TRANSACTION_ID"), map.get("INITIATION_AMOUNT"));
			
			if (netPayableAmount>secondApprov)
				o2cTransfer.performingLevel3Approval(map.get("TO_MSISDN"), map.get("TRANSACTION_ID"), map.get("INITIATION_AMOUNT"));
			
		}
		
		String version = DBHandler.AccessHandler.getCommProfileVersion(operatorToChannelMap.get("TO_COMMISSION_PROFILE"));
		Log.info("The Commission Profile updated version is :" + version);
		String profileDetailID = DBHandler.AccessHandler.getCommProfileDetailsID(operatorToChannelMap.get("TO_COMMISSION_PROFILE"), operatorToChannelMap.get("PRODUCT_CODE"), version);
		Log.info("The profile DetailID is : " + profileDetailID);
		String OTFValue = DBHandler.AccessHandler.getOTFValue(operatorToChannelMap.get("TO_USER_NAME"), profileDetailID);
		Log.info("The OTF Count after O2C transaction is : " + OTFValue);
		
		currentNode = test.createNode("To validate the OTF Count after 2nd O2C transaction ");
		currentNode.assignCategory("SIT");
		
		Map<String, String> map1 = o2cTransfer.initiateO2CTransfer(operatorToChannelMap);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
			map1= o2cTransfer.performingLevel1Approval(map1.get("TO_MSISDN"), map1.get("TRANSACTION_ID"));
			long netPayableAmount = _parser.getSystemAmount(map1.get("NetPayableAmount"));
			
			if (netPayableAmount>firstApprov)
				o2cTransfer.performingLevel2Approval(map1.get("TO_MSISDN"), map1.get("TRANSACTION_ID"), map1.get("INITIATION_AMOUNT"));
			
			if (netPayableAmount>secondApprov)
				o2cTransfer.performingLevel3Approval(map1.get("TO_MSISDN"), map1.get("TRANSACTION_ID"), map1.get("INITIATION_AMOUNT"));
			
		}
		
		
		
		String OTFCount2 = DBHandler.AccessHandler.getOTFValue(operatorToChannelMap.get("TO_USER_NAME"), profileDetailID);
		Log.info("The OTF Value after 2nd O2C transaction is : " + OTFCount2);
		
		/*
		 * Test Case to validate Network Stocks after successful O2C Transfer
		 */
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
		currentNode.assignCategory("SIT");
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
		BusinessValidator.validateStocks(TransactionVO);
		
		/*
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 */
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
		currentNode.assignCategory("SIT");
		BusinessValidator.validateUserBalances(TransactionVO);
		
		Log.endTestCase(methodname);
		
		/*
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 */
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
		currentNode.assignCategory("SIT");
		
		
		
		
	}
	
/*	
	@Test(priority=3)
	public void C_Positive_O2C() throws IOException, InterruptedException, ParseException, SQLException{
		final String methodname = "C_Positive_O2C";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT] Commission Profile CBC CAC Validations");
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode("To verify that user's CBC counts are updated and CBC is credited when Auto O2C is performed through web.");
		currentNode.assignCategory("SIT");
		Commission_CBC_CAC_Validations commissionProfile = new Commission_CBC_CAC_Validations(driver);
		AutoO2CMap autoO2CMap = new AutoO2CMap();
		HashMap<String, String> operatorToChannelMap = autoO2CMap.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		operatorToChannelMap.putAll(UserAccess.getUserWithAccess(RolesI.INITIATE_AUTO_O2C_TRANSFER_ROLECODE));
		businessController businessController = new businessController(_masterVO.getProperty("O2CTransferCode"), null, operatorToChannelMap.get("TO_MSISDN"));
		HashMap<String, String> initiatedQty = new HashMap<String, String>();
		initiatedQty.put(operatorToChannelMap.get("PRODUCT_CODE"), operatorToChannelMap.get("INITIATION_AMOUNT"));
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(operatorToChannelMap.get("TO_CATEGORY"), _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);
		
		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		commissionProfile.modifyCBCCACCommissionProfile(operatorToChannelMap);
		CacheUpdate cacheupdate = new CacheUpdate(driver);
		cacheupdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE()); 
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		Map<String, String> map = autoO2CTransfer.initiateAutoO2CTransfer(operatorToChannelMap);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
			map= autoO2CTransfer.performingLevel1Approval(operatorToChannelMap, _masterVO.getProperty("O2CTransferCode"));
			long netPayableAmount = _parser.getSystemAmount(map.get("NetPayableAmount"));
			
			if (netPayableAmount>firstApprov)
				autoO2CTransfer.performingLevel2Approval(operatorToChannelMap);
			
			if (netPayableAmount>secondApprov)
				autoO2CTransfer.performingLevel3Approval(operatorToChannelMap);
			
		}
		
		
		
		 * Test Case to validate Network Stocks after successful O2C Transfer
		 
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
		BusinessValidator.validateStocks(TransactionVO);
		
		
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		BusinessValidator.validateUserBalances(TransactionVO);
		
		Log.endTestCase(methodname);
	}
	
	@Test(priority=4)
	public void D_Positive_O2C() throws IOException, InterruptedException, ParseException, SQLException{
		final String methodname = "D_Positive_O2C";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT] Commission Profile CBC CAC Validations");
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode("To verify that user's CBC counts are updated and CBC is credited when O2C Reversal is performed through web.");
		currentNode.assignCategory("SIT");
		Commission_CBC_CAC_Validations commissionProfile = new Commission_CBC_CAC_Validations(driver);
		O2CReturn O2CReturnFeature = new O2CReturn(driver);
		OperatorToChannelMap OperatorToChannelMap = new OperatorToChannelMap();
		HashMap<String, String> operatorToChannelMap = OperatorToChannelMap.getOperatorToChannelMap(_masterVO.getProperty("O2CReturnCode"));
		operatorToChannelMap.putAll(UserAccess.getUserWithAccess(RolesI.INITIATE_O2C_TRANSFER_ROLECODE));
		businessController businessController = new businessController(_masterVO.getProperty("O2CReturnCode"), operatorToChannelMap.get("TO_MSISDN"), null);
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		O2CReturnFeature.performO2CReturn(operatorToChannelMap.get("PARENT_CATEGORY"), operatorToChannelMap.get("TO_CATEGORY"), operatorToChannelMap.get("TO_MSISDN"), operatorToChannelMap.get("PRODUCT_TYPE"), "100", "PVG Automated Testing");
		HashMap<String, String> initiatedQty = new HashMap<String, String>();
		initiatedQty.put(operatorToChannelMap.get("PRODUCT_CODE"), operatorToChannelMap.get("INITIATION_AMOUNT"));
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(operatorToChannelMap.get("TO_CATEGORY"), _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
		Long firstApprov = Long.parseLong(approvalLevel[0]);
		Long secondApprov = Long.parseLong(approvalLevel[1]);
		
		O2CTransfer o2cTransfer = new O2CTransfer(driver);
		
		currentNode = test.createNode("To verify that user's CBC counts are updated and CBC is credited when O2C is performed through web.");
		currentNode.assignCategory("SIT");
		commissionProfile.modifyCBCCACCommissionProfile(operatorToChannelMap);
		CacheUpdate cacheupdate = new CacheUpdate(driver);
		cacheupdate.updateCache(CacheController.CacheI.COMMISSION_PROFILE()); 
		Map<String, String> map = o2cTransfer.initiateO2CTransfer(operatorToChannelMap);
		if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
			map= o2cTransfer.performingLevel1Approval(map.get("TO_MSISDN"), map.get("TRANSACTION_ID"));
			long netPayableAmount = _parser.getSystemAmount(map.get("NetPayableAmount"));
			
			if (netPayableAmount>firstApprov)
				o2cTransfer.performingLevel2Approval(map.get("TO_MSISDN"), map.get("TRANSACTION_ID"), map.get("INITIATION_AMOUNT"));
			
			if (netPayableAmount>secondApprov)
				o2cTransfer.performingLevel3Approval(map.get("TO_MSISDN"), map.get("TRANSACTION_ID"), map.get("INITIATION_AMOUNT"));
			
		}
		
		
		 * Test Case to validate Network Stocks after successful O2C Transfer
		 
		currentNode = test.createNode("To validate Network Stocks on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQty);
		BusinessValidator.validateStocks(TransactionVO);
		
		
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 
		currentNode = test.createNode("To validate Receiver User Balance on successful Operator to Channel Transfer");
		currentNode.assignCategory("Smoke");
		BusinessValidator.validateUserBalances(TransactionVO);
		
		Log.endTestCase(methodname);
	}
	
	@Test(priority=5)
	public void E_Negative() throws IOException, InterruptedException, ParseException, SQLException{
		final String methodname = "E_Negative";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT] Commission Profile CBC CAC Validations");
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode("To validate that proper valid message should display on add commission Profile Details for CBC SLABS when Applicable From Date is null");
		currentNode.assignCategory("SIT");
		Commission_CBC_CAC_Validations commissionProfile = new Commission_CBC_CAC_Validations(driver);
		CommissionProfileMap commissionProfileMap = new CommissionProfileMap();
		HashMap<String, String> dataMap = commissionProfileMap.defaultMap(PretupsI.COMM_TYPE_BASECOMM);
		dataMap.put("APPLICABLE_FROM_DATE", "");
		String _error = commissionProfile.modifyCBCCACCommissionProfile(dataMap);
		String expectedMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.frommissing.otf", "1", MessagesDAO.getLabelByKey("channeltransfer.transferdetailapprovallevelone.label.otf"));
		Validator.messageCompare(_error, expectedMsg);
		
		Log.endTestCase(methodname);
	}
	
	@Test(priority=6)
	public void F_Negative() throws IOException, InterruptedException, ParseException, SQLException{
		final String methodname = "F_Negative";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT] Commission Profile CBC CAC Validations");
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode("To validate that proper valid message should display on add commission Profile Details for CBC SLABS when Applicable To Date is null");
		currentNode.assignCategory("SIT");
		Commission_CBC_CAC_Validations commissionProfile = new Commission_CBC_CAC_Validations(driver);
		CommissionProfileMap commissionProfileMap = new CommissionProfileMap();
		HashMap<String, String> dataMap = commissionProfileMap.defaultMap(PretupsI.COMM_TYPE_BASECOMM);
		dataMap.put("APPLICABLE_TO_DATE", "");
		String _error = commissionProfile.modifyCBCCACCommissionProfile(dataMap);
		String expectedMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.tomissing.otf", "1", MessagesDAO.getLabelByKey("channeltransfer.transferdetailapprovallevelone.label.otf"));
		Validator.messageCompare(_error, expectedMsg);
		
		Log.endTestCase(methodname);
	}
	
	@Test(priority=7)
	public void G_Negative() throws IOException, InterruptedException, ParseException, SQLException{
		final String methodname = "G_Negative";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT] Commission Profile CBC CAC Validations");
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode("To validate that proper valid message should display on add commission Profile Details for CBC SLABS when Time Slab is null");
		currentNode.assignCategory("SIT");
		Commission_CBC_CAC_Validations commissionProfile = new Commission_CBC_CAC_Validations(driver);
		CommissionProfileMap commissionProfileMap = new CommissionProfileMap();
		HashMap<String, String> dataMap = commissionProfileMap.defaultMap(PretupsI.COMM_TYPE_BASECOMM);
		dataMap.put("TIME_SLAB", "");
		String _error = commissionProfile.modifyCBCCACCommissionProfile(dataMap);
		String expectedMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.tomissing.otf", "1", MessagesDAO.getLabelByKey("channeltransfer.transferdetailapprovallevelone.label.otf"));
		Validator.messageCompare(_error, expectedMsg);
		
		Log.endTestCase(methodname);
	}
	
	@Test(priority=8)
	public void H_Negative() throws IOException, InterruptedException, ParseException, SQLException{
		final String methodname = "H_Negative";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT] Commission Profile CBC CAC Validations");
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode("To validate that proper valid message should display on add commission Profile Details for CBC SLABS when Time Slab is null");
		currentNode.assignCategory("SIT");
		Commission_CBC_CAC_Validations commissionProfile = new Commission_CBC_CAC_Validations(driver);
		CommissionProfileMap commissionProfileMap = new CommissionProfileMap();
		HashMap<String, String> dataMap = commissionProfileMap.defaultMap(PretupsI.COMM_TYPE_BASECOMM);
		dataMap.put("CBC_VALUE", "");
		String _error = commissionProfile.modifyCBCCACCommissionProfile(dataMap);
		String expectedMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.tomissing.otf", "1", MessagesDAO.getLabelByKey("channeltransfer.transferdetailapprovallevelone.label.otf"));
		Validator.messageCompare(_error, expectedMsg);
		
		Log.endTestCase(methodname);
	}
	
	@Test(priority=9)
	public void I_Negative() throws IOException, InterruptedException, ParseException, SQLException{
		final String methodname = "H_Negative";
		Log.startTestCase(methodname);
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT] Commission Profile CBC CAC Validations");
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode("To validate that proper valid message should display on add commission Profile Details for CBC SLABS when Time Slab is null");
		currentNode.assignCategory("SIT");
		Commission_CBC_CAC_Validations commissionProfile = new Commission_CBC_CAC_Validations(driver);
		CommissionProfileMap commissionProfileMap = new CommissionProfileMap();
		HashMap<String, String> dataMap = commissionProfileMap.defaultMap(PretupsI.COMM_TYPE_BASECOMM);
		dataMap.put("CBC_RATE", "");
		String _error = commissionProfile.modifyCBCCACCommissionProfile(dataMap);
		String expectedMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.tomissing.otf", "1", MessagesDAO.getLabelByKey("channeltransfer.transferdetailapprovallevelone.label.otf"));
		Validator.messageCompare(_error, expectedMsg);
		
		Log.endTestCase(methodname);
	}
	
	
	public void testCycle1(int CaseNum, String Description, HashMap<String, String> mapParam){
		
		Log.startTestCase(this.getClass().getName());
	
		Log.info("" + CaseNum+" ,"+Description+" ,"+mapParam);
		
	}
*/
	}
