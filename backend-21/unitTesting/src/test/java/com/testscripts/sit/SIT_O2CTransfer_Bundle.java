package com.testscripts.sit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CTransfer;
import com.Features.VMS;
import com.apicontrollers.extgw.VMS.EXTGW_VoucherChangeStatus_API;
import com.apicontrollers.extgw.VMS.EXTGW_VoucherChangeStatus_DP;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.commons.ServicesControllerI;
import com.databuilder.BuilderLogic;
import com.dbrepository.DBHandler;
import com.dbrepository.OracleRepository;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SIT_O2C_TRANSFER_BUNDLE)
public class SIT_O2CTransfer_Bundle extends BaseTest {
	static String moduleCode;
	private String skipDetail = "Test cases not applicable for this client";

	@TestManager(TestKey = "PRETUPS-421")
	@Test(dataProvider = "VOMSDENOMINATIONS") // TO BE UNCOMMENTED WITH JIRA
												// TEST ID
	public void A0001_VoucherDenomination(HashMap<String, String> initiateMap, int dataCounter) {

		final String methodName = "Test_VoucherDenomination";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERDENOM");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"), initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		if (_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
			initiateMap = vms.voucherDenomination(initiateMap, "");
			if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
				vms.writeDenomination(initiateMap, dataCounter);

				// Message Validation Here
			} else
				Assertion.assertFail(
						"Add Voucher Denomination Failure with Following Message: " + initiateMap.get("Message"));
		} else {
			Assertion.assertSkip(skipDetail);
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-423")
	@Test(dataProvider = "VOMSDENOMPROFILES") // TO BE UNCOMMENTED WITH JIRA
												// TEST ID
	public void A0002_VoucherProfile(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {

		final String methodName = "Test_VoucherProfile";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERPROF");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"), initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		if (_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
			initiateMap = vms.addVoucherProfile(initiateMap, "");

			if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
				vms.writeProfile(initiateMap, dataCounter);

				// Message Validation Here
			} else
				Assertion.assertFail(
						"Add Voucher Profile Failure with Following Message: " + initiateMap.get("Message"));
		} else {
			Assertion.assertSkip(skipDetail);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@TestManager(TestKey = "PRETUPS-3001")
	@Test
	public void A0003_AddVB() {
		final String methodName = "Bundle_Add";
		Log.startTestCase(methodName);

		HashMap<String, String> initiateMap = new HashMap<String, String>();

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVMSBUNDLEADD");
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), "login"));
		currentNode.assignCategory("VMS");
		if (_masterVO.getClientDetail("VOMS_BUNDLE_MANAGEMENT").equalsIgnoreCase("1")) {
			
			initiateMap.put("rowCountBundle", _masterVO.getClientDetail("BUNDLE_CREATION_ROWS"));
			initiateMap = vms.addVoucherBundleForO2CTransfer();

			if (initiateMap.get("MessageStatus").equalsIgnoreCase("Y")) {
				Assertion.assertPass(initiateMap.get("Message"));
				// Message Validation Here
			} else
				Assertion.assertFail("Add Voucher Bundle Failure with Following Message: " + initiateMap.get("Message"));
		} else {
			Assertion.assertSkip(skipDetail);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "VOMSDENOMPROFILES")
	@TestManager(TestKey = "PRETUPS-432") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A0004_VoucherOrderInitiate(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {

		final String methodName = "Test_VoucherOrderInitiate";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERORDERINIT");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"), initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		initiateMap = vms.voucherGenerationInitiate(initiateMap, "");

		Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "VOMSDENOMPROFILES")
	@TestManager(TestKey = "PRETUPS-434") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A0005_VoucherOrderApproval1(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {
		int maxApprovalLevel = 0;
		if (initiateMap.get("categoryName").equals("SUADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		} else if (initiateMap.get("categoryName").equals("NWADM")) {
			String approvalLevel = DBHandler.AccessHandler
					.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		} else if (initiateMap.get("categoryName").equals("SSADM")) {
			String approvalLevel = DBHandler.AccessHandler
					.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		} else if (initiateMap.get("categoryName").equals("SUNWADM")) {
			String approvalLevel = DBHandler.AccessHandler
					.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}

		final String methodName = "Test_VoucherOrderApproval1";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERAPPROV1");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"), initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		if (maxApprovalLevel > 0) {
			initiateMap = vms.voucherGenerationApproval1(initiateMap, "");

			Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}

		else
			Assertion.assertSkip("Max Approval Level is: " + maxApprovalLevel);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "VOMSDENOMPROFILES")
	@TestManager(TestKey = "PRETUPS-435") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A0006_VoucherOrderApproval2(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {
		int maxApprovalLevel = 0;
		if (initiateMap.get("categoryName").equals("SUADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		} else if (initiateMap.get("categoryName").equals("NWADM")) {
			String approvalLevel = DBHandler.AccessHandler
					.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		} else if (initiateMap.get("categoryName").equals("SSADM")) {
			String approvalLevel = DBHandler.AccessHandler
					.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		} else if (initiateMap.get("categoryName").equals("SUNWADM")) {
			String approvalLevel = DBHandler.AccessHandler
					.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}
		final String methodName = "Test_VoucherOrderApproval2";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERAPPROV2");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"), initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");

		if (maxApprovalLevel > 1) {
			initiateMap = vms.voucherGenerationApproval2(initiateMap, "");

			Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		} else
			Assertion.assertSkip("Max Approval Level is: " + maxApprovalLevel);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "VOMSDENOMPROFILES")
	@TestManager(TestKey = "PRETUPS-436") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A0007_VoucherOrderApproval3(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {
		int maxApprovalLevel = 0;
		if (initiateMap.get("categoryName").equals("SUADM")) {
			String approvalLevel = DBHandler.AccessHandler.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		} else if (initiateMap.get("categoryName").equals("NWADM")) {
			String approvalLevel = DBHandler.AccessHandler
					.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		} else if (initiateMap.get("categoryName").equals("SSADM")) {
			String approvalLevel = DBHandler.AccessHandler
					.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		} else if (initiateMap.get("categoryName").equals("SUNWADM")) {
			String approvalLevel = DBHandler.AccessHandler
					.getSystemPreference(PretupsI.VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN);
			maxApprovalLevel = Integer.parseInt(approvalLevel);
		}

		final String methodName = "Test_VoucherOrderApproval3";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERAPPROV3");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"), initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		if (maxApprovalLevel > 2) {
			initiateMap = vms.voucherGenerationApproval3(initiateMap, "");

			Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		}

		else
			Assertion.assertSkip("Max Approval Level is: " + maxApprovalLevel);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "VOMSDENOMPROFILES")
	@TestManager(TestKey = "PRETUPS-440") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A0008_ChangeStatus(HashMap<String, String> initiateMap, int dataCounter) throws InterruptedException {

		final String methodName = "Test_ChangeStatus";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITCHANGESTATUS");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"), initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if (BTSLUtil.isNullString(value)) {
			initiateMap = vms.changeOtherStatus(initiateMap, "");
			Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		} else {
			String type[] = getAllowedVoucherTypesForScreen(PretupsI.VOUC_DOWN);
			List al = Arrays.asList(type);
			if (al.contains(initiateMap.get("type"))) {
				initiateMap = vms.changeOtherStatus(initiateMap, "");
				Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
			} else if (initiateMap.get("type").equals("E") || initiateMap.get("type").equals("ET")) {
				initiateMap = vms.changeGeneratedStatusElectronic(initiateMap, "");
			} else {
				Assertion.assertSkip("Not a valid case for this scenario");
			}
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "VOMSDENOMPROFILES")
	@TestManager(TestKey = "PRETUPS-437") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A0009_VoucherChangeStatusScript(HashMap<String, String> initiateMap, int dataCounter)
			throws InterruptedException {

		final String methodName = "Test_VoucherChangeStatusScript";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITCHANGESTATUSSCRIPT");
		moduleCode = CaseMaster1.getModuleCode();
		VMS vms = new VMS(driver);

		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), initiateMap.get("voucherType"),
				initiateMap.get("service"), initiateMap.get("subService"), initiateMap.get("categoryName")));
		currentNode.assignCategory("SIT");
		int HCPT_VMS = Integer.parseInt(_masterVO.getClientDetail("HCPT_VMS"));
		if (HCPT_VMS == 1) {
			String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
			if(DBHandler.AccessHandler.isVomsBatchWithStatusPresent(productID, PretupsI.WAREHOUSE, "SC")) {
				vms.voucherChangeStatusScriptExecution();
				
				
			//	String batchType = DBHandler.AccessHandler.fetchBatchType(productID);
			//	Assertion.assertEquals(batchType, PretupsI.WAREHOUSE);
				if(DBHandler.AccessHandler.isVomsBatchWithStatusPresent(productID, PretupsI.WAREHOUSE, "EX"))
				Assertion.assertPass("Batch with WH type of product "+ productID +" is in EX status");
							else
								Assertion.assertFail("Batch with WH type of product "+ productID +" is not in EX status");
			}else
				Assertion.assertSkip("No scheduled batches found");
		} else {

			Assertion.assertSkip("Skipped for network IN");
		}
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST
	public void I0001_InitiateAndApproveTransferForBundlePOS(String parentCategory, String category, String userMSISDN,
			String productType, String loginId, String password) throws InterruptedException, SQLException {
		final String methodName = "InitiateAndApproveTransferForBundlePOS - Positive test case";
		Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);

		if (_masterVO.getClientDetail("O2C_TRANSFER_FOR_BUNDLE").equalsIgnoreCase("1")) {
			CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PO2CTRF5");
			CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PO2CTRF2");
			CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PO2CTRF3");
			CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PO2CTRF4");
			O2CTransfer o2cTrans = new O2CTransfer(driver);
			String expected1 = null;
			String expected2 = null;
			Random rand = new Random();
			int quantity = rand.nextInt(1) + 1;
			String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, netCode);
			String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
			Long netPayableAmount = 0l;
			Long firstApprov = Long.parseLong(approvalLevel[0]);
			Long secondApprov = Long.parseLong(approvalLevel[1]);
			// Test case to initiate O2C Transfer for Voucher Bundles
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);
			Map<String, String> map = o2cTrans.initiateTransferForBundle(userMSISDN, productType, quantity);
			String txnId = map.get("TRANSACTION_ID");
			String actual = map.get("INITIATE_MESSAGE");
			String expected = null;

			// Added a DirectO2C Transfer Handling where O2C Approvals are
			// bypassed
			// through AUTO_O2C_Preference
			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);
			else
				expected = MessagesDAO.prepareMessageByKey(
						"channeltransfer.transferdetailssuccess.msg.successwithautoapproval", txnId);

			Assertion.assertEquals(actual, expected);

			// Test Case to perform approval level 1
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);

			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
				map = o2cTrans.performingLevel1ApprovalPackage(userMSISDN, txnId);
				netPayableAmount = (long) BTSLUtil
						.getDisplayAmount((double) _parser.getSystemAmount(map.get("NetPayableAmount")));
				if (netPayableAmount <= firstApprov)
					expected1 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
				else
					expected1 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);
				String actual1 = map.get("actualMessage");
				Assertion.assertEquals(actual1, expected1);
			} else {
				Log.skip("Direct Operator to Channel is applicable in system");
			}

			// Test Case to perform approval level 2
			if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
					&& netPayableAmount > firstApprov) {
				currentNode = test.createNode(
						MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType))
						.assignCategory(TestCategory.PREREQUISITE);

				// Added a DirectO2C Transfer Handling where O2C Approvals are
				// bypassed through AUTO_O2C_Preference
				String actual2 = o2cTrans.performingLevel2ApprovalPackage(userMSISDN, txnId, quantity);
				if (netPayableAmount <= secondApprov)
					expected2 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

				else
					expected2 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);

				Assertion.assertEquals(actual2, expected2);
			}

			// Test case to perform approval level 3
			if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
					&& netPayableAmount > secondApprov) {
				currentNode = test.createNode(
						MessageFormat.format(CaseMaster4.getExtentCase(), category, parentCategory, productType))
						.assignCategory(TestCategory.PREREQUISITE);

				// Added a DirectO2C Transfer Handling where O2C Approvals are
				// bypassed through AUTO_O2C_Preference
				String actual3 = o2cTrans.performingLevel3ApprovalPackage(userMSISDN, txnId, quantity);
				String expected3 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

				Assertion.assertEquals(actual3, expected3);
			}

			// Changing status from PA to EN
			ResultSet rs = DBHandler.AccessHandler.fetchVouchersFromTxnId(txnId);
			CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERCHANGESTATUS");
			moduleCode = CaseMaster1.getModuleCode();
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);
			while (rs.next()) {
				String serialNo = rs.getString("serial_no");
				String masterSerialNo = rs.getString("master_serial_no");
				HashMap<String, String> apiData = EXTGW_VoucherChangeStatus_DP.getAPIdata();
				EXTGW_VoucherChangeStatus_API voucherChangeStatusAPI = new EXTGW_VoucherChangeStatus_API();
				apiData.put(voucherChangeStatusAPI.FROM_SERIALNO, serialNo);
				apiData.put(voucherChangeStatusAPI.TO_SERIALNO, serialNo);
				apiData.put(voucherChangeStatusAPI.MASTER_SERIALNO, masterSerialNo);
				apiData.put(voucherChangeStatusAPI.STATUS, "EN");
				apiData.put(voucherChangeStatusAPI.LOGINID, loginId);
				apiData.put(voucherChangeStatusAPI.PASSWORD, password);
				String API = voucherChangeStatusAPI.prepareAPI(apiData);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				Log.debug(APIResponse.toString());
			}

		} else {
			Assertion.assertSkip(skipDetail);
		}

		Assertion.completeAssertions();
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST
	public void I0002_InitiateTransferForBundleNEG(String parentCategory, String category, String userMSISDN,
			String productType, String loginId, String password) throws InterruptedException {
		final String methodName = "InitiateTransferForBundleNEG - Clicking submit on blank page";
		Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);
		if (_masterVO.getClientDetail("O2C_TRANSFER_FOR_BUNDLE").equalsIgnoreCase("1")) {
			CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PO2CTRF5");

			O2CTransfer o2cTrans = new O2CTransfer(driver);

			String remarks = _masterVO.getProperty("Remarks");

			// Test case to initiate O2C Transfer for Voucher Bundles
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);
			Map<String, String> map = o2cTrans.initiateTransferForBundleNegative001(userMSISDN, productType, remarks);

			String actual = map.get("ERROR_MESSAGE");
			String expected = MessagesDAO.getLabelByKey("channeltransfer.packagetransferdetails.error.enterdata");
			Assertion.assertEquals(actual, expected);
		} else {
			Assertion.assertSkip(skipDetail);
		}
		Assertion.completeAssertions();
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST
	// CASE ID
	public void I0003_InitiateTransferForBundleNEG(String parentCategory, String category, String userMSISDN,
			String productType, String loginId, String password) throws InterruptedException {
		final String methodName = "InitiateTransferForBundleNEG - Clicking submit without giving any remarks";
		Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);
		if (_masterVO.getClientDetail("O2C_TRANSFER_FOR_BUNDLE").equalsIgnoreCase("1")) {
			CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PO2CTRF5");

			O2CTransfer o2cTrans = new O2CTransfer(driver);

			// Test case to initiate O2C Transfer for Voucher Bundles
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);
			Map<String, String> map = o2cTrans.initiateTransferForBundleNegative002(userMSISDN, productType);

			String actual = map.get("ERROR_MESSAGE");
			String expected = MessagesDAO.getLabelByKey("user.addchanneluser.error.remarkrequired");
			Assertion.assertEquals(actual, expected);
		} else {
			Assertion.assertSkip(skipDetail);
		}
		Assertion.completeAssertions();
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST
	// CASE ID
	public void I0004_InitiateTransferForBundleNEG(String parentCategory, String category, String userMSISDN,
			String productType, String loginId, String password) throws InterruptedException {
		final String methodName = "InitiateTransferForBundleNEG - Clicking submit without selecting payment instrument type";
		Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);
		if (_masterVO.getClientDetail("O2C_TRANSFER_FOR_BUNDLE").equalsIgnoreCase("1")) {
			CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PO2CTRF5");

			O2CTransfer o2cTrans = new O2CTransfer(driver);

			// Test case to initiate O2C Transfer for Voucher Bundles
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);
			Map<String, String> map = o2cTrans.initiateTransferForBundleNegative003(userMSISDN, productType);

			String actual = map.get("ERROR_MESSAGE");
			String expected = MessagesDAO.getLabelByKey("channeltransfer.transferdetails.error.required.paymentinstrumenttype");
			Assertion.assertEquals(actual, expected);
		} else {
			Assertion.assertSkip(skipDetail);
		}
		Assertion.completeAssertions();
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST
	// CASE ID
	public void I0005_InitiateTransferForBundleNEG(String parentCategory, String category, String userMSISDN,
			String productType, String loginId, String password) throws InterruptedException {
		final String methodName = "InitiateTransferForBundleNEG - Clicking submit without selecting payment instrument date";
		Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);
		if (_masterVO.getClientDetail("O2C_TRANSFER_FOR_BUNDLE").equalsIgnoreCase("1")) {
			CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PO2CTRF5");

			O2CTransfer o2cTrans = new O2CTransfer(driver);

			// Test case to initiate O2C Transfer for Voucher Bundles
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);
			Map<String, String> map = o2cTrans.initiateTransferForBundleNegative004(userMSISDN, productType);

			String actual = map.get("ERROR_MESSAGE");
			String expected = MessagesDAO.getLabelByKey("channeltransfer.transferdetails.error.required.paymentinstrumentdate");
			Assertion.assertEquals(actual, expected);
		} else {
			Assertion.assertSkip(skipDetail);
		}
		Assertion.completeAssertions();
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST
	public void I0006_InitiateAndApproveTransferForBundleNEG(String parentCategory, String category, String userMSISDN,
			String productType, String loginId, String password) throws InterruptedException {
		final String methodName = "InitiateAndApproveTransferForBundleNEG - Failure at approval level 1";
		Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);
		if (_masterVO.getClientDetail("O2C_TRANSFER_FOR_BUNDLE").equalsIgnoreCase("1")) {
			CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PO2CTRF5");
			CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PO2CTRF2");

			O2CTransfer o2cTrans = new O2CTransfer(driver);
			Random rand = new Random();
			int quantity = rand.nextInt(4) + 1;
			String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");

			// Test case to initiate O2C Transfer for Voucher Bundles
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);
			Map<String, String> map = o2cTrans.initiateTransferForBundle(userMSISDN, productType, quantity);
			String txnId = map.get("TRANSACTION_ID");
			String actual = map.get("INITIATE_MESSAGE");
			String expected = null;

			// Added a DirectO2C Transfer Handling where O2C Approvals are
			// bypassed
			// through AUTO_O2C_Preference
			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);
			else
				expected = MessagesDAO.prepareMessageByKey(
						"channeltransfer.transferdetailssuccess.msg.successwithautoapproval", txnId);

			Assertion.assertEquals(actual, expected);

			// Test Case to perform approval level 1

			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);

			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
				String actual1 = o2cTrans.performingLevel1ApprovalPackageNEG(userMSISDN, txnId);
				Assertion.assertEquals(actual1,
						MessagesDAO.getLabelByKey("channeltransfer.packagetransferdetails.error.exttxnnum"));
			} else {
				Log.skip("Direct Operator to Channel is applicable in system");
			}
		} else {
			Assertion.assertSkip(skipDetail);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	/*@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST
	public void I0007_InitiateAndApproveTransferForBundlePOS(String parentCategory, String category, String userMSISDN,
			String productType, String loginId, String password) throws InterruptedException {
		final String methodName = "Positive test cases for voucher download link";
		Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);
		if (_masterVO.getClientDetail("O2C_TRANSFER_FOR_BUNDLE").equalsIgnoreCase("1")) {
			CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PO2CTRF5");
			CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PO2CTRF2");
			CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PO2CTRF3");
			CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PO2CTRF4");
			CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("SITVOUCHERDOWNLOAD");

			O2CTransfer o2cTrans = new O2CTransfer(driver);
			String expected1 = null;
			String expected2 = null;
			Random rand = new Random();
			int quantity = rand.nextInt(4) + 1;
			String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, netCode);
			String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
			Long netPayableAmount = null;
			Long firstApprov = Long.parseLong(approvalLevel[0]);
			Long secondApprov = Long.parseLong(approvalLevel[1]);

			// Test case to initiate O2C Transfer for Voucher Bundles
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);
			Map<String, String> map = o2cTrans.initiateTransferForBundle(userMSISDN, productType, quantity);
			String txnId = map.get("TRANSACTION_ID");
			String actual = map.get("INITIATE_MESSAGE");
			String expected = null;

			// Added a DirectO2C Transfer Handling where O2C Approvals are
			// bypassed
			// through AUTO_O2C_Preference
			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);
			else
				expected = MessagesDAO.prepareMessageByKey(
						"channeltransfer.transferdetailssuccess.msg.successwithautoapproval", txnId);

			Assertion.assertEquals(actual, expected);

			// Test Case to perform approval level 1
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster2.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);

			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true")) {
				map = o2cTrans.performingLevel1ApprovalPackage(userMSISDN, txnId);
				netPayableAmount = _parser.getSystemAmount(map.get("NetPayableAmount"));
				if (netPayableAmount <= firstApprov)
					expected1 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
				else
					expected1 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);
				String actual1 = map.get("actualMessage");
				Assertion.assertEquals(actual1, expected1);
			} else {
				Log.skip("Direct Operator to Channel is applicable in system");
			}

			// Test Case to perform approval level 2
			if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
					&& netPayableAmount > firstApprov) {
				currentNode = test.createNode(
						MessageFormat.format(CaseMaster3.getExtentCase(), category, parentCategory, productType))
						.assignCategory(TestCategory.PREREQUISITE);

				// Added a DirectO2C Transfer Handling where O2C Approvals are
				// bypassed through AUTO_O2C_Preference
				String actual2 = o2cTrans.performingLevel2ApprovalPackage(userMSISDN, txnId, quantity);
				if (netPayableAmount <= secondApprov)
					expected2 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

				else
					expected2 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);

				Assertion.assertEquals(actual2, expected2);
			}

			// Test case to perform approval level 3
			if ((directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
					&& netPayableAmount > secondApprov) {
				currentNode = test.createNode(
						MessageFormat.format(CaseMaster4.getExtentCase(), category, parentCategory, productType))
						.assignCategory(TestCategory.PREREQUISITE);

				// Added a DirectO2C Transfer Handling where O2C Approvals are
				// bypassed through AUTO_O2C_Preference
				String actual3 = o2cTrans.performingLevel3ApprovalPackage(userMSISDN, txnId, quantity);
				String expected3 = MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

				Assertion.assertEquals(actual3, expected3);
			}

			// Test case voucher download link
			HashMap<String, String> initiateMap = new HashMap<>();
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster5.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);
			initiateMap.put("transactionID", txnId);
			initiateMap.put("msisdn", userMSISDN);
			VMS vms = new VMS(driver);
			initiateMap = vms.voucherDownloadView(initiateMap, "");
			Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
			initiateMap = vms.voucherDownloadFile(initiateMap, "");
			Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
			initiateMap = vms.voucherDownloadFTPFile(initiateMap, "");
			Assertion.assertEquals(initiateMap.get("MessageStatus"), "Y");
		} else {
			Assertion.assertSkip(skipDetail);
		}
		Assertion.completeAssertions();
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST
	public void I0008_InitiateTransferForBundleNEG(String parentCategory, String category, String userMSISDN,
			String productType, String loginId, String password) throws InterruptedException {
		final String methodName = "Negative test cases for voucher download link";
		Log.startTestCase(methodName, parentCategory, category, userMSISDN, productType);
		if (_masterVO.getClientDetail("O2C_TRANSFER_FOR_BUNDLE").equalsIgnoreCase("1")) {
			CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PO2CTRF5");
			CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("SITVOUCHERDOWNLOAD");

			O2CTransfer o2cTrans = new O2CTransfer(driver);
			Random rand = new Random();
			int quantity = rand.nextInt(4) + 1;
			String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");

			// Test case to initiate O2C Transfer for Voucher Bundles
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);
			Map<String, String> map = o2cTrans.initiateTransferForBundle(userMSISDN, productType, quantity);
			String txnId = map.get("TRANSACTION_ID");
			String actual = map.get("INITIATE_MESSAGE");
			String expected = null;

			// Added a DirectO2C Transfer Handling where O2C Approvals are
			// bypassed
			// through AUTO_O2C_Preference
			if (directO2CPreference == null || !directO2CPreference.equalsIgnoreCase("true"))
				expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);
			else
				expected = MessagesDAO.prepareMessageByKey(
						"channeltransfer.transferdetailssuccess.msg.successwithautoapproval", txnId);

			Assertion.assertEquals(actual, expected);

			// Test case voucher download link - NEG
			HashMap<String, String> initiateMap = new HashMap<>();
			currentNode = test
					.createNode(
							MessageFormat.format(CaseMaster5.getExtentCase(), category, parentCategory, productType))
					.assignCategory(TestCategory.PREREQUISITE);
			initiateMap.put("transactionID", txnId);
			initiateMap.put("msisdn", userMSISDN);
			VMS vms = new VMS(driver);
			initiateMap = vms.voucherDownloadFileNeg(initiateMap, "");
			Assertion.assertEquals(initiateMap.get("MessageStatus"), "N");
			initiateMap = vms.voucherDownloadFTPFile(initiateMap, "");
			Assertion.assertEquals(initiateMap.get("MessageStatus"), "N");
		} else {
			Assertion.assertSkip(skipDetail);
		}
		Assertion.completeAssertions();
	}*/
	
	/*@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-306") // TO BE UNCOMMENTED WITH JIRA TEST
	public void I011_InitiateTransferForBundleNEG(String parentCategory, String category, String userMSISDN,
			String productType, String loginId , String password) throws InterruptedException, SQLException {
		// Changing status from PA to EN
		OracleRepository oraRepo = new OracleRepository();
		ResultSet rs = oraRepo.fetchVouchersFromTxnId("OT200226.0524.100002");
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITVOUCHERCHANGESTATUS");
		moduleCode = CaseMaster1.getModuleCode();
		currentNode = test
				.createNode(
						MessageFormat.format(CaseMaster1.getExtentCase(), category, parentCategory, productType))
				.assignCategory(TestCategory.PREREQUISITE);
		while(rs.next()) {
			String serialNo = rs.getString("serial_no");
			String masterSerialNo = rs.getString("master_serial_no");
			HashMap<String, String> apiData = EXTGW_VoucherChangeStatus_DP.getAPIdata();
			EXTGW_VoucherChangeStatus_API voucherChangeStatusAPI = new EXTGW_VoucherChangeStatus_API();
			apiData.put(voucherChangeStatusAPI.FROM_SERIALNO, serialNo);
			apiData.put(voucherChangeStatusAPI.TO_SERIALNO, serialNo);
			apiData.put(voucherChangeStatusAPI.MASTER_SERIALNO, masterSerialNo);
			apiData.put(voucherChangeStatusAPI.MSISDN, userMSISDN);
			apiData.put(voucherChangeStatusAPI.STATUS, "EN");
	        apiData.put(voucherChangeStatusAPI.LOGINID,loginId);
	        apiData.put(voucherChangeStatusAPI.PASSWORD,password);
	        String API = voucherChangeStatusAPI.prepareAPI(apiData);
	        String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
			Log.debug(APIResponse.toString());
		}
	}*/

	public static String[] getAllowedVoucherTypesForScreen(String screen) {

		HashMap<String, String[]> screenWiseAllowedVoucherTypeMap = new HashMap<String, String[]>();
		String[] allowedVoucherTypes = { PretupsI.VOUCHER_TYPE_DIGITAL, PretupsI.VOUCHER_TYPE_TEST_DIGITAL,
				PretupsI.VOUCHER_TYPE_ELECTRONIC, PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC, PretupsI.VOUCHER_TYPE_PHYSICAL,
				PretupsI.VOUCHER_TYPE_TEST_PHYSICAL };

		populateScreenWiseAllowedVoucherTypesMap(screen, screenWiseAllowedVoucherTypeMap);

		String[] tempAllowedVoucherTypes = screenWiseAllowedVoucherTypeMap.get(screen);
		if (tempAllowedVoucherTypes != null) {
			allowedVoucherTypes = tempAllowedVoucherTypes;
		}

		return allowedVoucherTypes;
	}

	/*
	 * DENO:D,DT,E,ET,P,PT;PROF:D,DT,E,ET,P,PT;ACTIVE_PROF:E,ET;VOUC_GEN:D,DT,E,
	 * ET,P,PT;VOUC_APP:D,DT,E,ET,P,PT;VOUC_DOWN:P,PT;CHAN_STATUS:D,DT,E,ET,P,PT
	 * ;O2C:D,DT,P,PT
	 * 
	 * @param screen
	 * 
	 * @param screenWiseAllowedVoucherTypeMap
	 */
	public static void populateScreenWiseAllowedVoucherTypesMap(String screen,
			HashMap<String, String[]> screenWiseAllowedVoucherTypeMap) {

		String screenWiseAllowedVoucherTypePref = DBHandler.AccessHandler
				.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		if (BTSLUtil.isNullString(screenWiseAllowedVoucherTypePref)) {
			return;
		}

		String[] screens = screenWiseAllowedVoucherTypePref.split(";");
		for (int i = 0; i < screens.length; i++) {
			if (BTSLUtil.isNullString(screens[i])) {
				return;
			}
			String[] screenWiseAllowedVoucherType = screens[i].split(PretupsI.COLON);
			screenWiseAllowedVoucherTypeMap.put(screenWiseAllowedVoucherType[0],
					screenWiseAllowedVoucherType[1].split(PretupsI.COMMA));
		}

	}

	/* ----------------------- D A T A P R O V I D E R ---------------------- */
	/*
	 * -------------------------------------------------------------------------
	 * ------------------------
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
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
			}
		}

		/*
		 * Counter to count number of users exists in channel users hierarchy
		 * sheet of Categories for which O2C transfer is allowed
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
		 * Store required data of 'O2C transfer allowed category' users in
		 * Object
		 */
		Object[][] Data = new Object[userCounter][5];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
				Data[j][3] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
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
			o2cData[j][3] = ProductObject[i];
			o2cData[j][4] = Data[k][3];
			o2cData[j][5] = Data[k][4];
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

	@DataProvider(name = "VOMSDENOMINATIONS")
	public Object[][] VOMSDenominationDP() {

		int VOMS_DATA_COUNT = Integer.parseInt(_masterVO.getProperty("vms.voms.profiles.count"));
		Object[][] VOMSData = DBHandler.AccessHandler.getVOMSDetails();

		int objCounter = 0;
		ArrayList<String> categoryList = UserAccess.getCategoryWithAccess(RolesI.ADD_VOUCHER_DENOMINATION);

		if (categoryList.contains("SSADM")) {
			categoryList.remove(categoryList.indexOf("SSADM"));
		}

		if (categoryList.contains("SUNADM")) {
			categoryList.remove(categoryList.indexOf("SUNADM"));
		}

		int categorySize = categoryList.size();
		Object[][] dataObj = new Object[VOMS_DATA_COUNT * VOMSData.length * categorySize][2];

		for (int i = 0; i < VOMS_DATA_COUNT; i++) {
			for (int j = 0; j < VOMSData.length; j++) {
				for (int k = 0; k < categorySize; k++) {

					HashMap<String, String> VomsData = new HashMap<String, String>();
					VomsData.put("voucherType", String.valueOf(VOMSData[j][0]));
					VomsData.put("type", String.valueOf(VOMSData[j][1]));
					VomsData.put("service", String.valueOf(VOMSData[j][2]));
					// VomsData.put("subService",
					// String.valueOf(VOMSData[j][3]));
					VomsData.put("categoryName", categoryList.get(k));
					VomsData.put("payableAmount", String.valueOf(10));
					VomsData.put("description", "Automation Testing");
					VomsData.put("minQuantity", "1");
					VomsData.put("maxQuantity", "60");
					VomsData.put("talkTime", "5");
					VomsData.put("validity", "80");
					VomsData.put("threshold", "10");
					VomsData.put("quantity", "10");
					VomsData.put("expiryPeriod", "90");
					dataObj[objCounter][0] = VomsData.clone();
					dataObj[objCounter][1] = ++objCounter;
				}
			}
		}

		BuilderLogic VOMSDenomSheetBuilder = new BuilderLogic();
		VOMSDenomSheetBuilder.prepareVOMSProfileSheet(dataObj);

		return dataObj;
	}

	@DataProvider(name = "VOMSBUNDLES")
	public Object[][] VOMSBUNDLES() {

		BuilderLogic VBSheet = new BuilderLogic();
		VBSheet.prepareVOMSBundleSheet();

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] dataObj = new Object[rowCount][2];
		int objCounter = 0;
		for (int i = 1; i <= rowCount; i++) {
			HashMap<String, String> VomsData = new HashMap<String, String>();
			VomsData.put("voucherBundleName", ExcelUtility.getCellData(0, ExcelI.VOMS_BUNDLE_NAME, i));
			VomsData.put("voucherBundlePrefix", ExcelUtility.getCellData(0, ExcelI.VOMS_BUNDLE_PREFIX, i));

			dataObj[objCounter][0] = VomsData.clone();
			dataObj[objCounter][1] = ++objCounter;
		}

		return dataObj;
	}

	@DataProvider(name = "VOMSDENOMPROFILES")
	public Object[][] VOMSDenominationProfilesDP() {

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		int rowCount = ExcelUtility.getRowCount();
		rowCount = 1;
		Object[][] dataObj = new Object[rowCount][2];
		int objCounter = 0;
		for (int i = 1; i <= rowCount; i++) {
			HashMap<String, String> VomsData = new HashMap<String, String>();
			VomsData.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
			VomsData.put("type", ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i));
			VomsData.put("service", ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE, i));
			VomsData.put("subService", ExcelUtility.getCellData(0, ExcelI.VOMS_SUB_SERVICE, i));
			VomsData.put("categoryName", ExcelUtility.getCellData(0, ExcelI.VOMS_USER_CATEGORY_NAME, i));
			VomsData.put("denominationName", ExcelUtility.getCellData(0, ExcelI.VOMS_DENOMINATION_NAME, i));
			VomsData.put("shortName", ExcelUtility.getCellData(0, ExcelI.VOMS_SHORT_NAME, i));
			VomsData.put("mrp", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
			String mrp = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
			VomsData.put("activeProfile", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
			String activeProfile = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i);
			String denomination = mrp + ".0";
			VomsData.put("denomination", denomination);
			String productID = activeProfile + "(" + denomination + ")";
			VomsData.put("productID", productID);
			VomsData.put("payableAmount", String.valueOf(10));
			VomsData.put("description", "Automation Testing");
			VomsData.put("remarks", "Automation Testing");
			VomsData.put("minQuantity", "1");
			VomsData.put("maxQuantity", "60");
			VomsData.put("talkTime", "5");
			VomsData.put("validity", "80");
			VomsData.put("threshold", "10");
			VomsData.put("quantity", "60");
			VomsData.put("expiryPeriod", "90");
			VomsData.put("batchType", "printing");
			VomsData.put("voucherStatus", PretupsI.WAREHOUSE);
			VomsData.put("viewBatchFor", "N");
			dataObj[objCounter][0] = VomsData.clone();
			dataObj[objCounter][1] = ++objCounter;
		}

		return dataObj;
	}
}
