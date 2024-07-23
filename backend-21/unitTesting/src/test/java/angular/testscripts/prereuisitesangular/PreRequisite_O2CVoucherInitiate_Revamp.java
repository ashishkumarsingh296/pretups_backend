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

import angular.feature.O2CVoucherInitiateRevamp;

@ModuleManager(name=Module.PREREQUISITE_O2C_VOUCHER_INITIATE_REVAMP)
public class PreRequisite_O2CVoucherInitiate_Revamp extends BaseTest {
	 
		@Test(dataProvider = "userData")
	    public void A_01_Test_O2CVoucherInitiateandApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) {
		  
		  final String methodName = "A_01_Test_O2CVoucherInitiateandApproval";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVI1");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherInitiateRevamp o2CVoucherInitiateRevamp = new O2CVoucherInitiateRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherInitiateRevamp.performO2CVoucherInitiateandApproval( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  vouchertype, type, activeProfile,  mrp);
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
	    public void A_02_Test_O2CVoucherInitiateBlankPaymentDate(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) {
	        final String methodName = "A_02_Test_O2CVoucherInitiateBlankPaymentDate";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVI2");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherInitiateRevamp o2CVoucherInitiateRevamp = new O2CVoucherInitiateRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherInitiateRevamp.performO2CVoucherInitiateBlankPaymentDate( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  vouchertype, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Initiate is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_03_Test_O2CVoucherInitiateBlankPaymentType(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) {
	        final String methodName = "A_03_Test_O2CVoucherInitiateBlankPaymentType";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVI3");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherInitiateRevamp o2CVoucherInitiateRevamp = new O2CVoucherInitiateRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherInitiateRevamp.performO2CVoucherInitiateBlankPaymentType( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  vouchertype, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Initiate is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_04_Test_O2CVoucherInitiateBlankInstrumentNumber(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) {
	        final String methodName = "A_04_Test_O2CVoucherInitiateBlankPaymentType";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVI4");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherInitiateRevamp o2CVoucherInitiateRevamp = new O2CVoucherInitiateRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherInitiateRevamp.performO2CVoucherInitiateBlankInstrumentNumber( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  vouchertype, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Initiate is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_05_Test_O2CVoucherInitiateBlankRemarks(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) {
	        final String methodName = "A_05_Test_O2CVoucherInitiateBlankRemarks";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVI5");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherInitiateRevamp o2CVoucherInitiateRevamp = new O2CVoucherInitiateRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherInitiateRevamp.performO2CVoucherInitiateBlankRemarks( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  vouchertype, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Initiate is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_06_Test_O2CVoucherInitiateBlankQuantity(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) {
	        final String methodName = "A_06_Test_O2CVoucherInitiateBlankRemarks";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVI6");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherInitiateRevamp o2CVoucherInitiateRevamp = new O2CVoucherInitiateRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherInitiateRevamp.performO2CVoucherInitiateBlankQuantity( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  vouchertype, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Initiate is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_07_Test_O2CVoucherInitiateBlankDenomination(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) {
	        final String methodName = "A_07_Test_O2CVoucherInitiateBlankDenomination";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVI7");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherInitiateRevamp o2CVoucherInitiateRevamp = new O2CVoucherInitiateRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherInitiateRevamp.performO2CVoucherInitiateBlankDenomination( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  vouchertype, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Initiate is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
	    public void A_08_Test_O2CVoucherInitiateBlankSegment(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) {
	        final String methodName = "A_08_Test_O2CVoucherInitiateBlankSegment";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVI8");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherInitiateRevamp o2CVoucherInitiateRevamp = new O2CVoucherInitiateRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherInitiateRevamp.performO2CVoucherInitiateBlankSegment( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  vouchertype, type, activeProfile,  mrp);
	            } else {
	                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
	            }
	        } else {
	            Assertion.assertSkip("O2C Voucher Initiate is not allowed to category[" + opCategoryName + "].");
	        }
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		@Test(dataProvider = "userData")
	    public void A_09_Test_O2CVoucherInitiateReset(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) {
		  
		  final String methodName = "A_09_Test_O2CVoucherInitiateReset";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVI9");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherInitiateRevamp o2CVoucherInitiateRevamp = new O2CVoucherInitiateRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherInitiateRevamp.performO2CVoucherInitiateReset( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  vouchertype, type, activeProfile,  mrp);
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
	    public void A_10_Test_O2CVoucherInitiateandReject(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn,String chPin,String chLoginId, String chPassword,String vouchertype,String activeProfile,String mrp,String type) {
		  
		  final String methodName = "A_10_Test_O2CVoucherInitiateandReject";
	        Log.startTestCase(methodName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CVI10");
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName)).assignCategory(TestCategory.PREREQUISITE);
	        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CVOUCHERCTRANSFER_EVENT)) {
	            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
	            O2CVoucherInitiateRevamp o2CVoucherInitiateRevamp = new O2CVoucherInitiateRevamp(driver);

	            if (webAccessAllowed.equals("Y")) {
	            	o2CVoucherInitiateRevamp.performO2CVoucherInitiateandReject( opCategoryName, opLoginId, opPassword, opPin, chCategoryName, chMsisdn,  vouchertype, type, activeProfile,  mrp);
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
        String O2CVoucherInititateCode = _masterVO.getProperty("O2CVoucherInitiate");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        
        
        ArrayList<String> opUserData =new ArrayList<String>();
        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
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
            if (aList.contains(O2CVoucherInititateCode)) {
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
        Object[][] Data = new Object[userCounter][6];
        for (int i = 1, j = 0; i <= chnlCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                Data[j][4] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                Data[j][5] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                j++;
            }
        }

        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		rowCount = ExcelUtility.getRowCount();
		ArrayList<ArrayList<String>> voucherData= new ArrayList<ArrayList<String>>();
		for (int i = 1; i <= rowCount; i++) {
				ArrayList<String> voucherTempData =new ArrayList<>();
				if(ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).equals("D")||ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i).equals("DT")) {
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i));
				voucherData.add(voucherTempData);		
				}
				}
				

        /*
         * Creating combination of channel users for each product.
         */
        int countTotal = voucherData.size();
        Object[][] o2cData = new Object[countTotal][14];
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
