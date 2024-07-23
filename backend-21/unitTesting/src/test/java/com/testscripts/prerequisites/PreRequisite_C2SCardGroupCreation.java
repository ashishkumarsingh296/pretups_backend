package com.testscripts.prerequisites;

import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2SCardGroup;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.PREREQUISITE_C2S_CARDGROUP)
public class PreRequisite_C2SCardGroupCreation extends BaseTest {

	@Test(dataProvider="serviceData")
	@TestManager(TestKey = "PRETUPS-418") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void Test_C2SCardGroupGroupCreation(int rowNum, String serviceName, String subService) throws InterruptedException{
		final String methodName = "Test_C2SCardGroupGroupCreation";
		Log.startTestCase(methodName, rowNum, serviceName, subService);

		CaseMaster CaseMaster1=_masterVO.getCaseMasterByID("PC2SCARDGROUP1");

		// Test Case - To create C2S Card Group through Network Admin as per the DataProvider
		currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), serviceName,subService)).assignCategory(TestCategory.PREREQUISITE);
		C2SCardGroup c2sCardGroup = new C2SCardGroup(driver);
		HashMap<String, String> mapInfo = (HashMap<String, String>) c2sCardGroup.c2SCardGroupCreation(serviceName, subService);
		c2sCardGroup.writeCardGroupToExcel(mapInfo.get("CARDGROUPNAME"),mapInfo.get("CARDGROUP_SETID"), rowNum);

		Log.endTestCase(methodName);
	}

	/* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
	/* ------------------------------------------------------------------------------------------------- */

	@DataProvider(name = "serviceData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[rowCount][3];
		for (int i = 1, j = 0; i <= rowCount; i++, j++) {
			categoryData[j][0] = i;
			categoryData[j][1] = ExcelUtility.getCellData(i, 1);
			categoryData[j][2] = ExcelUtility.getCellData(i, 2);
		}
		return categoryData;
	}

	/* ------------------------------------------------------------------------------------------------- */
}
