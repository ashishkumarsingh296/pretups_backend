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

import angular.feature.O2CStockReturnRevamp;


@ModuleManager(name=Module.PREREQUISITE_O2C_STOCK_RETURN_REVAMP)
public class PreRequisite_O2CStockReturn_Revamp extends BaseTest{
	
	@Test(dataProvider = "userData")
    public void A_01_Test_O2CStockReturn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin,String chLoginId, String chPassword) {
	  
	  final String methodName = "A_01_Test_O2CStockReturn";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSR1");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_RETURN_REVAMP, chCategoryName, EventsI.O2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);//check
            O2CStockReturnRevamp  o2CStockReturnRevamp = new O2CStockReturnRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	o2CStockReturnRevamp.performO2CStockReturn( opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,productCode, productName, chPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Stock Return is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
	
	//below are all negative test cases
	@Test(dataProvider = "userData")
    public void A_02_Test_O2CStockReturn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin,String chLoginId, String chPassword) {
	  
	  final String methodName = "A_02_Test_O2CStockReturn";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSR2");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_RETURN_REVAMP, chCategoryName, EventsI.O2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CStockReturnRevamp  o2CStockReturnRevamp = new O2CStockReturnRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	o2CStockReturnRevamp.performO2CStockReturnWithoutAmount( opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,productCode, productName);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Stock Return is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
	
	@Test(dataProvider = "userData")
    public void A_03_Test_O2CStockReturn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin,String chLoginId, String chPassword) {
	  
	  final String methodName = "A_03_Test_O2CStockReturn";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSR3");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_RETURN_REVAMP, chCategoryName, EventsI.O2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CStockReturnRevamp  o2CStockReturnRevamp = new O2CStockReturnRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	o2CStockReturnRevamp.performO2CStockReturnWithoutRemark( opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,productCode, productName);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Stock Return is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
	
	@Test(dataProvider = "userData")
    public void A_04_Test_O2CStockReturn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin,String chLoginId, String chPassword) {
	  
	  final String methodName = "A_04_Test_O2CStockReturn";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSR4");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_RETURN_REVAMP, chCategoryName, EventsI.O2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CStockReturnRevamp  o2CStockReturnRevamp = new O2CStockReturnRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	o2CStockReturnRevamp.performO2CStockReturnWithWrongPin( opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,productCode, productName);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Stock Return is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
	
	@Test(dataProvider = "userData")
    public void A_05_Test_O2CStockReturn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin,String chLoginId, String chPassword) {
	  
	  final String methodName = "A_05_Test_O2CStockReturn";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSR5");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_RETURN_REVAMP, chCategoryName, EventsI.O2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CStockReturnRevamp  o2CStockReturnRevamp = new O2CStockReturnRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	o2CStockReturnRevamp.performO2CStockReturnLargeAmount( opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,productCode, productName, chPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Stock Return is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
	
	@Test(dataProvider = "userData")
    public void A_06_Test_O2CStockReturn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin,String chLoginId, String chPassword) {
	  
	  final String methodName = "A_06_Test_O2CStockReturn";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSR6");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_RETURN_REVAMP, chCategoryName, EventsI.O2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CStockReturnRevamp  o2CStockReturnRevamp = new O2CStockReturnRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	o2CStockReturnRevamp.performO2CStockReturnWithAlphaNumericAmount( opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,productCode, productName, chPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Stock Return is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
	
	@Test(dataProvider = "userData")
    public void A_07_Test_O2CStockReturn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin,String chLoginId, String chPassword) {
	  
	  final String methodName = "A_07_Test_O2CStockReturn";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSR7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_RETURN_REVAMP, chCategoryName, EventsI.O2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CStockReturnRevamp  o2CStockReturnRevamp = new O2CStockReturnRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	o2CStockReturnRevamp.performO2CStockReturnWithZeroAmount( opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,productCode, productName, chPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Stock Return is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
	
	@Test(dataProvider = "userData")
    public void A_08_Test_O2CStockReturn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin,String chLoginId, String chPassword) {
	  
	  final String methodName = "A_08_Test_O2CStockReturn";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSR8");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_RETURN_REVAMP, chCategoryName, EventsI.O2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CStockReturnRevamp  o2CStockReturnRevamp = new O2CStockReturnRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	o2CStockReturnRevamp.checkResetButton( opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,productCode, productName, chPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Stock Return is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
	
	@Test(dataProvider = "userData")
    public void A_09_Test_O2CStockReturn(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode, String productName, String chPin,String chLoginId, String chPassword) {
	  
	  final String methodName = "A_09_Test_O2CStockReturn";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CSR9");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_RETURN_REVAMP, chCategoryName, EventsI.O2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CStockReturnRevamp  o2CStockReturnRevamp = new O2CStockReturnRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	o2CStockReturnRevamp.checkPinReset( opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn,productCode, productName, chPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Stock Return is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
	
	
//	...........................................................................................................................
	@DataProvider(name = "userData")
	 public Object[][] TestDataFeed() {
	        String O2CReturnCode = _masterVO.getProperty("O2CReturnCode");
	        String MasterSheetPath = _masterVO.getProperty("DataProvider");
	        
	        
	        ArrayList<String> opUserData =new ArrayList<String>();
	        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_RETURN_REVAMP,EventsI.O2CRETURN_EVENT);
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
	        int chnlCount = ExcelUtility.getRowCount()-4;
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

	        /*
	         * Store products from Product Sheet to Object.
	         */
	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
	        int prodRowCount = ExcelUtility.getRowCount();
	        Object[][] ProductObject = new Object[prodRowCount][2];//changes
//	        Object[] ProductObject = new Object[prodRowCount];
	        for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
	            ProductObject[i][0] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, j);//changes
	            ProductObject[i][1] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, j);//changes
	        }

	        /*
	         * Creating combination of channel users for each product.
	         */
	        int countTotal = ProductObject.length * userCounter;
	        Object[][] o2ctmpData = new Object[countTotal][8];
	        for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
	            o2ctmpData[j][0] = Data[k][0];
	            o2ctmpData[j][1] = Data[k][1];
	            o2ctmpData[j][2] = Data[k][2];
	            o2ctmpData[j][3] = ProductObject[i][0];//changes
	            o2ctmpData[j][4] = ProductObject[i][1];//changes
	            o2ctmpData[j][5] = Data[k][3];
	            o2ctmpData[j][6] = Data[k][4];
	            o2ctmpData[j][7] = Data[k][5];
	          
	            
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
	        
	    
	        Object[][] o2cData =new Object[countTotal][12];
	        
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
