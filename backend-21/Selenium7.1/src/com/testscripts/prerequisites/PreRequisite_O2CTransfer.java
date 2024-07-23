package com.testscripts.prerequisites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CTransfer;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;

public class PreRequisite_O2CTransfer extends BaseTest{
	static boolean TestCaseCounter = false;
	
	

	@Test(dataProvider = "categoryData")
	public void o2cTransferTC(String parentCategory, String category, String userMSISDN, String productType) throws InterruptedException	{
		Log.startTestCase("O2C Transfer");
		
		if (TestCaseCounter == false) {
			test=extent.createTest("[Pre-Requisite]O2C Transfer");
			TestCaseCounter = true;
		}	
		O2CTransfer o2cTrans = new O2CTransfer(driver);
		
		String expected1;
		String expected2 = null;
		String quantity= _masterVO.getProperty("Quantity");
		String remarks= _masterVO.getProperty("Remarks");
		String netCode=_masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, netCode);
		_parser amountsParser = new _parser();
		amountsParser.convertStringToLong(approvalLevel[0]).changeDenomation();
		Long firstApprov = amountsParser.getValue();
		amountsParser.convertStringToLong(approvalLevel[1]).changeDenomation();
		Long secondApprov = amountsParser.getValue();
		
		/*
		 * Test case to initiate O2C Transfer
		 */
		currentNode = test.createNode("To verify that Operator user is able to perform Operator to channel Transfer initiation for category "+category+" with parent category  "+parentCategory+", product type"+productType);
		currentNode.assignCategory("Pre-Requisite");
		
		Map<String, String> map= o2cTrans.initiateTransfer(userMSISDN, productType, quantity,remarks);
		String txnId= map.get("TRANSACTION_ID");
		String actual= map.get("INITIATE_MESSAGE");
		String expected= MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);

		if (actual.equals(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		/*
		 * Test case to perform O2C approval level 1
		 */
		currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 1 for category "+category+" with parent category "+parentCategory+", product type"+productType);
		currentNode.assignCategory("Pre-Requisite");

		map= o2cTrans.performingLevel1Approval(userMSISDN, txnId);
		
		amountsParser.convertStringToLong(map.get("NetPayableAmount"));
		Long netPayableAmount= amountsParser.getValue();
		 
		if(netPayableAmount<=firstApprov){
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
		}
		else{
			expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);
		}

		if (map.get("actualMessage").equals(expected1))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected1 + "] but found [" + map.get("actualMessage") + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}

		/*
		 * Test Case to perform approval level 2
		 */
		if(netPayableAmount>firstApprov)
		{
			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 2 for category "+category+" with parent category "+parentCategory+", product type"+productType);
			currentNode.assignCategory("Pre-Requisite");

			String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);
			if(netPayableAmount<=secondApprov){
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			}
			else{
				expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);
			}
			
			if (actual2.equals(expected2))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + expected2 + "] but found [" + actual2 + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}

		/*
		 * Test case to perform approval level 3
		 */
		
		if((netPayableAmount>secondApprov))
		{
			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 3 for category "+category+" with parent category "+parentCategory+", product type"+productType);
			currentNode.assignCategory("Pre-Requisite");

			String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
			String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

			if (actual3.equals(expected3))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + expected3 + "] but found [" + actual3 + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}
		
		Log.endTestCase("O2C Transfer");
	}

	/**
	 * DataProvider for Operator to Channel transfer
	 * @return Object
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
				ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
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
		Object[][] Data = new Object[userCounter][3];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
				Data[j][0] = ExcelUtility.getCellData(0,ExcelI.PARENT_CATEGORY_NAME, i);
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
		Object[][] o2cData = new Object[countTotal][4];      
		for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
			o2cData[j][0] = Data[k][0];
			o2cData[j][1] = Data[k][1];
			o2cData[j][2] = Data[k][2];
			o2cData[j][3] = ProductObject[i];
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
}
