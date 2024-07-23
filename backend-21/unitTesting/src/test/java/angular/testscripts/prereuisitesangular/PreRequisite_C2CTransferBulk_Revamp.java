package angular.testscripts.prereuisitesangular;

import angular.feature.C2CTransferBulkRevamp;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.*;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

@ModuleManager(name = Module.PREREQUISITE_C2C_BULK_TRANSFER_REVAMP)
public class PreRequisite_C2CTransferBulk_Revamp extends BaseTest {

	public PreRequisite_C2CTransferBulk_Revamp() {
		CHROME_OPTIONS = CONSTANT.CHROME_OPTION_C2CBULKTRANSFER ;
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_01_Test_C2CBulkTransfer(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_01_Test_C2CBulkTransfer";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF1");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransfer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_02_Test_C2CBulkTransferBlankCategory(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_02_Test_C2CBulkTransferBlankCategory";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF2");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferBlankCategory(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_03_Test_C2CBulkTransferBlankBatchName(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_03_Test_C2CBulkTransferBlankBatchName";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF3");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferBlankBatchName(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_04_Test_C2CBulkTransferWithoutUpload(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_04_Test_C2CBulkTransferWithoutUpload";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF4");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferWithoutUpload(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_05_Test_C2CBulkTransferResetButton(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_05_Test_C2CBulkTransferResetButton";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF5");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferResetButton(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_06_Test_C2CBulkTransferDownloadUserListWithoutCategory(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_06_Test_C2CBulkTransferDownloadUserListWithoutCategory";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF6");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferDownloadUserListWithoutCategory(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_07_Test_C2CBulkTransferDownloadTemplateWithoutCategory(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_07_Test_C2CBulkTransferDownloadTemplateWithoutCategory";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF7");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferDownloadTemplateWithoutCategory(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_08_Test_C2CBulkTransferFileUploadType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) throws InterruptedException {
		final String methodName = "A_08_Test_C2CBulkTransferFileUploadType";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF8");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferFileUploadType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_09_Test_C2CBulkTransferBlankPIN(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_09_Test_C2CBulkTransferBlankPIN";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF9");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferBlankPIN(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_10_Test_C2CBulkTransferBlankFileUpload(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_10_Test_C2CBulkTransferBlankFileUpload";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF10");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferBlankFileUpload(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_11_Test_C2CBulkTransferInvalidMSISDN(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_11_Test_C2CBulkTransferInvalidMSISDN";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF11");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferInvalidMSISDN(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_12_Test_C2CBulkTransferInvaliHeaderFileUpload(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_12_Test_C2CBulkTransferInvaliHeaderFileUpload";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF12");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferInvalidHeaderFileUpload(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_13_Test_C2CBulkTransferInvaliDataFileUpload(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_13_Test_C2CBulkTransferInvaliDataFileUpload";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF13");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferInvalidDataFileUpload(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}



	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_14_Test_C2CBulkTransferBlankDataFileWithHeaderUpload(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_14_Test_C2CBulkTransferBlankDataFileWithHeaderUpload(";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF14");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferBlankDataFileWithHeaderUpload(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}








/*

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_03_Test_C2CBulkTransferBlankProduct(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_01_Test_C2CBulkTransfer";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF3");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferBlankProduct(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
*/


/*@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	public void A_04_Test_C2CBulkTransferSymbolBatchName(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) {
		final String methodName = "A_04_Test_C2CBulkTransfer";
		Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CTRF4");
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

		if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBULKTRANSFER_EVENT)) {
			String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
			C2CTransferBulkRevamp C2CTransfer = new C2CTransferBulkRevamp(driver);

			if (webAccessAllowed.equals("Y")) {
				C2CTransfer.performC2CBulkTransferSymbolBatchName(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
			} else {
				Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
			}
		} else {
			Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}*/





	/* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
	    /* ------------------------------------------------------------------------------------------------- */

	    @DataProvider(name = "categoryData")
		public Object[][] TestDataFeed1() {
			String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");
			String MasterSheetPath = _masterVO.getProperty("DataProvider");

			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			/*
			 * Array list to store Categories for which C2C withdraw is allowed
			 */
			ArrayList<String> alist1 = new ArrayList<String>();
			ArrayList<String> alist2 = new ArrayList<String>();
			ArrayList<String> categorySize = new ArrayList<String>();
			ArrayList<String> transfer_rule_type = new ArrayList<String>();
			for (int i = 1; i <= rowCount; i++) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
				if (aList.contains(C2CTransferCode) || aList.contains(C2CTransferCode + "[P]") ||
						aList.contains(C2CTransferCode + "[S]") || aList.contains(C2CTransferCode + "[O]") ||
						aList.contains(C2CTransferCode + "[D]")) {
					ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
					alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
					alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
					if (ExcelUtility.getCellData(0, ExcelI.TRF_RULE_TYPE, i).equals(""))
						transfer_rule_type.add("D");
					else
						transfer_rule_type.add(ExcelUtility.getCellData(0, ExcelI.TRF_RULE_TYPE, i));
				}
			}

			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();

			/*
			 * Calculate the Count of Users for each category
			 */
			int totalObjectCounter = 0;
			for (int i = 0; i < alist1.size(); i++) {
				int categorySizeCounter = 0;
				for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
					if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(i))) {
						categorySizeCounter++;
					}
				}
				categorySize.add("" + categorySizeCounter);
				totalObjectCounter = totalObjectCounter + categorySizeCounter;
			}

			/*
			 * Counter to count number of users exists in channel users hierarchy sheet
			 * of Categories for which C2C Withdraw is allowed
			 */

			Object[][] Data = new Object[totalObjectCounter][9];

			for (int j = 0, k = 0; j < alist1.size(); j++) {

				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
				int excelRowSize = ExcelUtility.getRowCount();
				String ChannelUserPIN = null;
				for (int i = 1; i <= excelRowSize; i++) {
					if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist2.get(j))) {
						ChannelUserPIN = ExcelUtility.getCellData(0, ExcelI.PIN, i);
						break;
					}
				}

				for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
					if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(j))) {
						Data[k][0] = alist2.get(j);
						Data[k][1] = alist1.get(j);
						Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
						Data[k][3] = ChannelUserPIN;
						Data[k][4] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, excelCounter);
						Data[k][5] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
						Data[k][6] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, excelCounter);
						Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
						Data[k][8] = excelCounter;
						k++;
					}
				}

			}

			/*
			 * Clean data on the basis of transfer rules
			 */
			String trfUserLevelAlllow = DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRF_RULE_USER_LEVEL_ALLOW);
			if (trfUserLevelAlllow.equalsIgnoreCase("FALSE")) {
				int q = 0;
				ArrayList<Integer> removeData = new ArrayList<Integer>();
				for (int i = 0; i < alist1.size(); i++) {
					if (transfer_rule_type.get(i).equals("P")) {
						Log.info("From: " + alist2.get(i) + "| To: " + alist1.get(i) + "| TYPE: " + transfer_rule_type.get(i));

						for (int p = 0; p < Data.length; p++) {
							if (Data[p][0].equals(alist2.get(i)) && Data[p][1].equals(alist1.get(i)) && !Data[p][5].equals(alist2.get(i))) {
								Log.info("Data to be removed:[" + p + "]");
								q++;
								removeData.add(p);
							}
						}
					}

					ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
					int excelRowSize = ExcelUtility.getRowCount();

					if (transfer_rule_type.get(i).equals("O")) {
						Log.info("From: " + alist2.get(i) + "| To: " + alist1.get(i) + "| TYPE: " + transfer_rule_type.get(i));
						for (int p = 0; p < Data.length; p++) {
							int k;
							for (k = 1; k <= excelRowSize; k++) {
								if (ExcelUtility.getCellData(0, ExcelI.SEQUENCE_NO, k).equals("1") && ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, k).equals(Data[p][4])) {
									break;
								}
							}

							if ((Data[p][0].equals(alist2.get(i)) || Data[p][0].equals(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, k)))
									&& Data[p][1].equals(alist1.get(i)) && !Data[p][5].equals(alist2.get(i))) {
								Log.info("Data to be removed:[" + p + "]");
								q++;
								removeData.add(p);
							}
						}
					}
				}

				int newObj = Data.length - q;
				Object[][] Data1 = new Object[newObj][8];
				for (int l = 0, m = 0; l < Data.length; l++) {
					if (!removeData.contains(l)) {
						for (int x = 0; x < 8; x++) {
							Data1[m][x] = Data[l][x];
						}
						Log.info(Data1);
						m++;
					}
				}


				return Data1;
			} else {
				return Data;
			}
		}
	    /* ------------------------------------------------------------------------------------------------ */

	
}
