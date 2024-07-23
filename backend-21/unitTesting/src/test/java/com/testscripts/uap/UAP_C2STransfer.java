package com.testscripts.uap;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransfer;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CaseMaster;
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
@ModuleManager(name=Module.UAP_C2S_RECHARGE)
public class UAP_C2STransfer extends BaseTest {

	static boolean TestCaseCounter = false;

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-402") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void C2Srecharge(String ParentCategory, String FromCategory, String PIN,String service)
			throws IOException, InterruptedException {
		
		final String methodName = "Test_C2Srecharge";
        Log.startTestCase(methodName);
        
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PC2STRF1");
		
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),FromCategory));
		currentNode.assignCategory("UAP");
		
		if(CommonUtils.roleCodeExistInLinkSheet(RolesI.C2SRECHARGE, FromCategory)){
		C2STransfer C2STransfer = new C2STransfer(driver);
		if(webAccessAllowed.equals("Y")){
		C2STransfer.performC2STransfer(ParentCategory, FromCategory, PIN,service);}
		else{
			Assertion.assertSkip("As webaccess is not allowed for "+FromCategory+", case is skipped.");
			currentNode.log(Status.SKIP,"As webaccess is not allowed for "+FromCategory+", case is skipped.");
			}
		}
		else{
			Assertion.assertSkip("C2S Recharge is not allowed to category["+FromCategory+"]");
			currentNode.log(Status.SKIP,"C2S Recharge is not allowed to category["+FromCategory+"]");
			}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	//############################################################################################3
	
		/**
		 * DataProvider for Operator to Channel transfer
		 * @return Object
		 */
		@DataProvider(name = "categoryData")
		public Object[][] TestDataFeed1() {
			String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
			String MasterSheetPath = _masterVO.getProperty("DataProvider");

			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			int rowCount = ExcelUtility.getRowCount();
	/*
	 * Array list to store Categories for which Customer Recharge is allowed
	 */
			ArrayList<String> alist1 = new ArrayList<String>();
			for (int i = 1; i <= rowCount; i++) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
				ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
				if (aList.contains(CustomerRechargeCode)) {
					ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
					alist1.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
				}
			}

	/*
	 * Counter to count number of users exists in channel users hierarchy sheet 
	 * of Categories for which C2S transfer is allowed
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
	 * Store required data of 'C2S transfer allowed category' users in Object
	 */
			Object[][] Data = new Object[userCounter][4];
			for (int i = 1, j = 0; i <= chnlCount; i++) {
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
				if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
					Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
					Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
					Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
					Data[j][3] = CustomerRechargeCode;
					j++;
				}
			}
						
		return Data;
		}

	}
