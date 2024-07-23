package angular.testscripts.prereuisitesangular;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UserAccessRevamp;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

import angular.feature.C2CVoucherRevamp;
import angular.feature.O2CVoucherTransferRevamp;

@ModuleManager(name=Module.PREREQUISITE_O2C_TRANSFER_REVAMP)
public class PreRequisite_O2CVoucherTransfer_Revamp extends BaseTest {

	  @Test(dataProvider = "userData")
	    public void A_01_Test_O2CVoucherTransferandApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
	  		  ) {
		  
		  final String methodName = "A_01_Test_O2CVoucherTransferandApproval";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT1");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferandApproval( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  	@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_02_Test_O2CVoucherTransferBlankSearchBuyerName(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_02_Test_O2CVoucherTransferBlankSearchBuyerName";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT2");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankSearchBy( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  	@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_03_Test_O2CVoucherTransferBlankMSISDNWithBuyerMobile(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_03_Test_O2CVoucherTransferBlankMSISDNWithBuyerMobile";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT3");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankMSISDNWithBuyerMobile( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_04_Test_O2CVoucherTransferBlankGeoGraphyWithUser(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_04_Test_O2CVoucherTransferBlankGeoGraphyWithUser";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT4");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankGeoGraphyWithUser( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_05_Test_O2CVoucherTransferBlankDomainWithUser(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_05_Test_O2CVoucherTransferBlankDomainWithUser";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT5");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankDomainWithUser( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_06_Test_O2CVoucherTransferBlankOwnerCategoryWithUser(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_06_Test_O2CVoucherTransferBlankOwnerCategoryWithUser";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT6");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankOwnerCategoryWithUser( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_07_Test_O2CVoucherTransferBlankCategoryWithUser(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_07_Test_O2CVoucherTransferBlankCategoryWithUser";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT7");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankCategoryWithUser( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_08_Test_O2CVoucherTransferBlankChannelOwnerNameWithUser(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_08_Test_O2CVoucherTransferBlankChannelOwnerNameWithUser";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT8");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankChannelOwnerNameWithUser( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_09_Test_O2CVoucherTransferBlankUserNameWithUser(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_09_Test_O2CVoucherTransferBlankUserNameWithUser";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT9");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankUserNameWithUser( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_10_Test_O2CVoucherTransferBlankDomainWithLoginId(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_10_Test_O2CVoucherTransferBlankDomainWithLoginId";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT10");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankDomainWithLoginId( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_11_Test_O2CVoucherTransferBlankCategoryWithLoginId(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_11_Test_O2CVoucherTransferBlankCategoryWithLoginId";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT11");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankCategoryWithLoginId( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_12_Test_O2CVoucherTransferResetFields(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_12_Test_O2CVoucherTransferResetFields";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT12");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferResetFields( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_13_Test_O2CVoucherTransferInvalidMSISDNLength(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_13_Test_O2CVoucherTransferInvalidMSISDNLength";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT13");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferInvalidMSISDNLength( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_14_Test_O2CVoucherTransferBlankPaymentDate(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_14_Test_O2CVoucherTransferBlankPaymentDate";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT14");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankPaymentDate( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_15_Test_O2CVoucherTransferBlankPaymentType(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_15_Test_O2CVoucherTransferBlankPaymentType";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT15");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankPaymentType( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_16_Test_O2CVoucherTransferBlankPaymentInstrumentNumber(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_16_Test_O2CVoucherTransferBlankPaymentInstrumentNumber";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT16");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankInstrumentNumber( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_17_Test_O2CVoucherTransferBlankRemarks(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_17_Test_O2CVoucherTransferBlankRemarks";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT17");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankRemarks( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_18_Test_O2CVoucherTransferBlankToSerial(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_18_Test_O2CVoucherTransferBlankToSerial";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT18");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankToSerial( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_19_Test_O2CVoucherTransferInvalidToSerial(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_19_Test_O2CVoucherTransferInvalidToSerial";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT19");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferInvalidToSerial( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_20_Test_O2CVoucherTransferBlankFromSerial(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_20_Test_O2CVoucherTransferBlankFromSerial";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT20");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankFromSerial( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_21_Test_O2CVoucherTransferInvalidFromSerial(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_21_Test_O2CVoucherTransferInvalidFromSerial";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT21");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferInvalidFromSerial( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_22_Test_O2CVoucherTransferBlankDenomination(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_22_Test_O2CVoucherTransferBlankDenomination";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT22");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankDenomination( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_23_Test_O2CVoucherTransferBlankSegment(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_23_Test_O2CVoucherTransferBlankSegment";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT23");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankSegment( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_24_Test_O2CVoucherTransferBlankType(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
	        final String methodName = "A_24_Test_O2CVoucherTransferBlankType";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT24");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherTransferRevamp.performO2CVoucherTransferBlankType( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		
		 @Test(dataProvider = "userData")
		    public void A_25_Test_O2CVoucherTransferandReject(String opCategoryName,String opLoginId,String opPassword,String opPin,String chCategoryName,String chMsisdn, String voucherType,String type,String activeProfile, String mrp
		  		  ) {
			  
			  final String methodName = "A_25_Test_O2CVoucherTransferandReject";
		        Log.startTestCase(methodName);
		        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVT25");
		        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
		        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
		            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
		            O2CVoucherTransferRevamp o2CVoucherTransferRevamp = new O2CVoucherTransferRevamp(driver);

		            if (webAccessAllowed.equals("Y")) {
		            	o2CVoucherTransferRevamp.performO2CVoucherTransferandReject( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  voucherType, type, activeProfile,  mrp);
		            } else {
		                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
		            }
		        } else {
		            Assertion.assertSkip("O2C Voucher Transfer is not allowed to category[" + opCategoryName + "].");
		        }
		        Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
	@DataProvider(name = "userData")
    public Object[][] TestDataFeed() {
        String O2CVoucherTransferCode = _masterVO.getProperty("O2CVoucherTransfer");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        
        
        ArrayList<String> opUserData =new ArrayList<String>();
        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CVOUCHERCTRANSFER_EVENT);
        opUserData.add(userInfo.get("CATEGORY_NAME"));
        opUserData.add(userInfo.get("LOGIN_ID"));
        opUserData.add(userInfo.get("PASSWORD"));
        opUserData.add(userInfo.get("PIN"));
        

     
        
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
            if (aList.contains(O2CVoucherTransferCode)) {
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
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
        Object[][] Data = new Object[userCounter][2];
        for (int i = 1, j = 0; i <= chnlCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                j++;
            }
        }

        /*
         * Store products from Product Sheet to Object.
         */
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		rowCount = ExcelUtility.getRowCount();
		ArrayList<ArrayList<String>> voucherData= new ArrayList<ArrayList<String>>();
		for (int i = 1; i <= rowCount; i++) {
				ArrayList<String> voucherTempData =new ArrayList<>();
				if(ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).equals("D")||ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).equals("DT")) {

				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i));
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
				
				voucherData.add(voucherTempData);
				}		
		}

        /*
         * Creating combination of channel users for each product.
         */
        int countTotal = voucherData.size();
        Object[][] o2cData = new Object[countTotal][10];
        for (int i = 0; i < countTotal; i++) {
        	
        	int counter_j=0;
        	for(int j=0;j<opUserData.size();j++) {
        		o2cData[i][counter_j++]=opUserData.get(j);
        	}
        	
        	for(int j=0;j<Data[0].length;j++) {
        		o2cData[i][counter_j++]=Data[0][j];
        	}
        	
        	for(int j=0;j<voucherData.get(i).size();j++) {
        		o2cData[i][counter_j++]=voucherData.get(i).get(j);
        	}
       
        }
        
        return o2cData;
        
    }
 
}
