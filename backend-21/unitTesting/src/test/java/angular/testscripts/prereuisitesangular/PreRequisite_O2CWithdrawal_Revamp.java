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
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

import angular.feature.O2CWithdrawalRevamp;

@ModuleManager(name = Module.PREREQUISITE_O2CWITHDRAWAL_REVAMP)
public class PreRequisite_O2CWithdrawal_Revamp extends BaseTest{

	
	  @Test(dataProvider = "userData")
	    public void A_01_Test_O2CWithdrawalByMsisdn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_01_Test_O2CWithdrawalByMsisdn";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW1");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalByMsisdn(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_02_Test_O2CWithdrawalByUserName(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName, String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_02_Test_O2CWithdrawalByUserName";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW1");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "User Name")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalByUserName(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName, chUserName, chDomainName, chGeography);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_03_Test_O2CWithdrawalByLoginId(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName, String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_03_Test_O2CWithdrawalByLoginId";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW1");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName , productName , "Login ID")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalByLoginId(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, productCode, productName, chLoginId, chDomainName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_04_Test_O2CWithdrawalWithWrongPin(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_04_Test_O2CWithdrawalWithWrongPin";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW2");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalWithWrongPin(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_05_Test_O2CWithdrawalCheckPinReset(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_05_Test_O2CWithdrawalCheckPinReset";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW3");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.checkPinReset(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_06_Test_O2CWithdrawalWithBlankRemarks(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_06_Test_O2CWithdrawalWithBlankRemarks";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW4");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalWithBlankRemarks(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_07_Test_O2CWithdrawalWithBlankAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_07_Test_O2CWithdrawalWithBlankAmount";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW5");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalWithBlankAmount(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_08_Test_O2CWithdrawalWithZeroAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_08_Test_O2CWithdrawalWithZeroAmount";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW6");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalWithZeroAmount(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_09_Test_O2CWithdrawalWithAlphanumericAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_09_Test_O2CWithdrawalWithAlphanumericAmount";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW7");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalWithAlphanumericAmount(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_10_Test_O2CWithdrawalWithLargeAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_10_Test_O2CWithdrawalWithLargeAmount";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW8");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalWithLargeAmount(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_11_Test_O2CWithdrawalCheckWithdrawalDetailsReset(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_11_Test_O2CWithdrawalCheckWithdrawalDetailsReset";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW9");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.checkWithdrawalDetailsReset(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_12_Test_O2CWithdrawalWithBlankOperatorWallet(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_12_Test_O2CWithdrawalWithBlankOperatorWallet";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW10");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalWithBlankOperatorWallet(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_13_Test_O2CWithdrawalWithBlankSearchByCriteria(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_13_Test_O2CWithdrawalWithBlankSearchByCriteria";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW11");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalWithBlankSearchByCriteria(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_14_Test_O2CWithdrawalByLoginIdBlankDetails(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName, String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_14_Test_O2CWithdrawalByLoginIdBlankDetails";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW12");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName , productName , "Login ID")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalByLoginIdBlankDetails(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, productCode, productName, chLoginId, chDomainName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_15_Test_O2CWithdrawalByInvalidLoginId(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName, String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_15_Test_O2CWithdrawalByInvalidLoginId";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW13");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName , productName , "Login ID")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalByInvalidLoginId(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, productCode, productName, chLoginId, chDomainName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_16_Test_O2CWithdrawalByUserNameBlankDetails(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName, String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_16_Test_O2CWithdrawalByUserNameBlankDetails";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW14");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "User Name")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalByUserNameBlankDetails(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName, chUserName, chDomainName, chGeography);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_17_Test_O2CWithdrawalCheckUserDetailsReset(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName, String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_17_Test_O2CWithdrawalCheckUserDetailsReset";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW15");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName , productName , "Login ID")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.checkUserDetailsReset(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, productCode, productName, chLoginId, chDomainName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_18_Test_O2CWithdrawalByBlankMsisdn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_18_Test_O2CWithdrawalByBlankMsisdn";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW16");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalByBlankMsisdn(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_19_Test_O2CWithdrawalByInvalidMsisdn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_19_Test_O2CWithdrawalByInvalidMsisdn";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW17");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalByInvalidMsisdn(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }

	  @Test(dataProvider = "userData")
	    public void A_20_Test_O2CWithdrawalByAlphanumericMsisdn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName,String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_20_Test_O2CWithdrawalByAlphanumericMsisdn";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW18");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "Mobile Number")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalByAlphanumericMsisdn(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
	  
	  @Test(dataProvider = "userData")
	    public void A_21_Test_O2CWithdrawalByInvalidUserName(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chUserName, String chLoginId, String chDomainName, String chGeography) {
		  final String methodName = "A_21_Test_O2CWithdrawalByInvalidUserName";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSW19");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName , "User Name")).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CWITHDRAW_EVENT)) {	
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CWithdrawalRevamp o2CWithdrawalRevamp = new O2CWithdrawalRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CWithdrawalRevamp.performO2CWithdrawalByInvalidUserName(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,  productCode, productName, chUserName, chDomainName, chGeography);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Withdrawal is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }

	
	
	
	
	
	
//	  ----------------------------------------------DATA PROVIDER------------------------------------------------------------------------
	  @DataProvider(name = "userData")
		 public Object[][] TestDataFeed() {
		        String O2CReturnCode = _masterVO.getProperty("O2CReturnCode");
		        String MasterSheetPath = _masterVO.getProperty("DataProvider");
		       
		       
		        ArrayList<String> opUserData =new ArrayList<String>();
		        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CWITHDRAW_EVENT);//changed
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
		            if (aList.contains(O2CReturnCode)) {
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
		        Object[][] Data = new Object[userCounter][7];
		        for (int i = 1, j = 0; i <= chnlCount; i++) {
		            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
		                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
		                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
		                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
		                Data[j][3] = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);//CHANGED
		                Data[j][4] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
		                Data[j][5] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
		                Data[j][6] = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, i);//changed
		                j++;
		            }
		        }
		        /*
		         * Store products from Product Sheet to Object.
		         */
		        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
		        int prodRowCount = ExcelUtility.getRowCount();
		        Object[][] ProductObject = new Object[prodRowCount][2];//changes
//		        Object[] ProductObject = new Object[prodRowCount];
		        for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
		            ProductObject[i][0] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, j);//changes
		            ProductObject[i][1] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, j);//changes
		        }
		        /*
		         * Creating combination of channel users for each product.
		         */
		        int countTotal = ProductObject.length * userCounter;
		        Object[][] o2ctmpData = new Object[countTotal][9];
		        for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
		            o2ctmpData[j][0] = Data[k][0];
		            o2ctmpData[j][1] = Data[k][1];
		            o2ctmpData[j][2] = Data[k][2];
		            o2ctmpData[j][3] = ProductObject[i][0];//changes
		            o2ctmpData[j][4] = ProductObject[i][1];//changes
		            o2ctmpData[j][5] = Data[k][3];
		            o2ctmpData[j][6] = Data[k][4];
		            o2ctmpData[j][7] = Data[k][5];
		            o2ctmpData[j][8] = Data[k][6];//changed
		         
		           
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
		       
		   
		        Object[][] o2cData =new Object[countTotal][13];
		       
		        int counter_1=0;
		        	
		        for(int k=0;k<o2ctmpData.length;k++) {
		        	int counter_2=0;
		        		
		        	for(int j=0;j<opUserData.size();j++)
		        	o2cData[counter_1][counter_2++]=opUserData.get(j);
		        		
		        	for(int l=0;l<o2ctmpData[0].length;l++)
		        	o2cData[counter_1][counter_2++]=o2ctmpData[k][l];
		        			
		        	counter_1++;
		        	}
		     
		        return o2cData;
		       
		    }
	
}
