package com.testscripts.uap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.Enquiries.UserBalanceEnquirySpring;
import com.Features.mapclasses.UserBalanceMap;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class UAP_UserBalanceSpring extends BaseTest {



	static boolean TestCaseCounter = false;

	@Test(dataProvider = "categoryData")
	public void userBalanceTC(int caseNum,String description, String parentCategory, String category, HashMap<String, String> mapParam) throws InterruptedException, IOException	{
		Log.startTestCase("Self Balance");

		if (TestCaseCounter == false) {
			test=extent.createTest("[SIT] User Balance");
			TestCaseCounter = true;
		}


		currentNode = test.createNode("To verify that User Balance Enquiry is available for category "+parentCategory);
		currentNode.assignCategory("SIT");
		UserBalanceEnquirySpring userBalanceEnquirySpring = new UserBalanceEnquirySpring(driver);




		Map<String, String> resultMap = null;
		String actualMsg, expectedMsg;
		switch(caseNum){


		case 0://To verify that user is able to view User Balance of his child through msisdn
			currentNode = test.createNode(description);
			currentNode.assignCategory("SIT");
			resultMap = userBalanceEnquirySpring.validateUserBalancesEnquiry(parentCategory, category, mapParam, "msisdn");
			currentNode.addScreenCaptureFromPath(resultMap.get("screenshot"));
			break;	

		case 1://To verify that user is unable to view User Balance if msisdn is left Blank
			currentNode=test.createNode(description);
			currentNode.assignCategory("SIT");
			resultMap = userBalanceEnquirySpring.validateUserBalancesEnquiry(parentCategory, category, mapParam, "msisdn");
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.msisdn.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;	


		case 2://To verify that user is able to view User Balance of his child through loginId
			currentNode = test.createNode(description);
			currentNode.assignCategory("SIT");
			resultMap = userBalanceEnquirySpring.validateUserBalancesEnquiry(parentCategory, category, mapParam, "loginId");
			currentNode.addScreenCaptureFromPath(resultMap.get("screenshot"));
			break;	


		case 3://To verify that user is able to view User Balance of his child through domain/category

			currentNode=test.createNode(description);
			currentNode.assignCategory("SIT");
			resultMap = userBalanceEnquirySpring.validateUserBalancesEnquiry(parentCategory, category, mapParam, "user");
			currentNode.addScreenCaptureFromPath(resultMap.get("screenshot"));
			break;

		
		}

	}



	/**
	 * DataProvider for Operator to Channel transfer
	 * @return Object
	 */
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		UserBalanceMap userBalanceMap = new UserBalanceMap();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		String parentCategory = null;
		String category = null;
		for(int i = 0; i+2<=rowCount; i++){

			if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i+1) == ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i+2))
			{
				parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i+2);
				category = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i+2);
				break;
			}


		}

		String[] description = new String[4];
		description[0] = "To verify that user is able to view User Balance of his child through msisdn";
		description[1] = "To verify that user is unable to view User Balance if msisdn is left Blank";
		description[2] = "To verify that user is able to view User Balance of his child through loginId";
		description[3] = "To verify that user is able to view User Balance of his child through domain/category";
	

		Object[][] testData = {{0, description[0],parentCategory,category, userBalanceMap.getOperatorUserMap(null, null)},
				{1, description[1],parentCategory,category, userBalanceMap.getOperatorUserMap("MSISDN", "")},
				/*{2, description[2],parentCategory,category, userBalanceMap.getOperatorUserMap(null, null)},*/
				{3, description[3],parentCategory,category, userBalanceMap.getOperatorUserMap(null, null)}
		};

		return testData;
	}


}
