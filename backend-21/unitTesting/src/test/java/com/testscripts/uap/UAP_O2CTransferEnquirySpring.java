package com.testscripts.uap;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.O2CTransfer;
import com.Features.Enquiries.O2CTransferEnquirySpring;
import com.Features.mapclasses.O2CEnquiryTransferMap;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils._parser;

public class UAP_O2CTransferEnquirySpring extends BaseTest {



	static boolean TestCaseCounter = false;
	NetworkAdminHomePage networkAdminHomePage;
	Map<String,String> dataMap;
	static boolean directO2CPreference;
	


	@DataProvider(name = "transferData")
	public Object[][] DomainCategoryProvider_validations() throws SQLException {

		O2CEnquiryTransferMap o2cEnquiryTransfermap = new O2CEnquiryTransferMap();

		String[] description=new String[4];
		description[0]="To verify that channel user is not able to check O2C Enquiry if Transaction Number is null for Transaction Number panel ";
		description[1]="To verify that channel user is not able to check O2C Enquiry if From date is null for Mobile Number panel";
		description[2]="To verify that channel user is not able to check O2C Enquiry if Transfer Type is not selected for Geographical Domain panel";
		description[3]="To verify that channel user is able to check O2C Enquiry";


		Object[][] testData={{0,description[0], o2cEnquiryTransfermap.setO2CEnquiryMap("transferNum","")},
				{1,description[1], o2cEnquiryTransfermap.setO2CEnquiryMap("fromDate","")},
				{2,description[2], o2cEnquiryTransfermap.setO2CEnquiryMap("transferType", "Select")}
				,{3,description[3], o2cEnquiryTransfermap.getO2CEnquiryMap()}
		};

		return testData;
	}

	@Test(dataProvider = "transferData")
	public void UAP_O2CTransferEnquiry(int caseNum, String description, HashMap<String, String> mapParam) throws InterruptedException, IOException{
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) { 
			test = extent.createTest("[UAP] O2C Transfer Enquiry");
			TestCaseCounter = true;
		}

		//Make object of feature class
		O2CTransferEnquirySpring o2CTransfersEnquirySpring = new O2CTransferEnquirySpring(driver);
		Map<String, String> resultMap = null;
		String actualMsg, expectedMsg;
		String searchTransferID = "transferNum";
		String searchMobileNumber = "mobileNumber";
		String searchGeography = "geographicalDomain";
		currentNode=test.createNode(description);
		currentNode.assignCategory("UAP");
		switch(caseNum){

		case 0:
			//To verify that channel user is not able to check O2C Enquiry if Transaction Number is null for Transaction Number panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchTransferID);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.transferNum.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;

		case 1://To verify that channel user is not able to check O2C Enquiry if From date is null for Mobile Number panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchMobileNumber);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.from.date.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;


		case 2://To verify that channel user is not able to check O2C Enquiry if transfer type is null for Geographical Domain panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchGeography);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.transferTypeCode.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;

		case 3:
			O2CTransfer o2cTrans = new O2CTransfer(driver);
			
			String expected1, expected;
			String expected2 = null;
			Long netPayableAmount = null;
			String quantity= _masterVO.getProperty("Quantity");
			String remarks= _masterVO.getProperty("Remarks");
			String netCode=_masterVO.getMasterValue(MasterI.NETWORK_CODE);
			String category = mapParam.get("category");
			String productType = mapParam.get("productTypeCode");
			String userMSISDN  = mapParam.get("userMSISDN");
			String parentCategory = mapParam.get("parentCategory");
			String masterSheetPath =_masterVO.getProperty("DataProvider");
			String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(category, netCode);
			directO2CPreference = Boolean.parseBoolean(DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED"));
			_parser amountsParser = new _parser();
			amountsParser.convertStringToLong(approvalLevel[0]).changeDenomation();
			Long firstApprov = amountsParser.getValue();
			amountsParser.convertStringToLong(approvalLevel[1]).changeDenomation();
			Long secondApprov = amountsParser.getValue();
			
			/*
			 * Test case to initiate O2C Transfer
			 */
			
			Map<String, String> map= o2cTrans.initiateTransfer1(userMSISDN, productType, quantity,remarks);
			String txnId= map.get("TRANSACTION_ID");
			String actual= map.get("INITIATE_MESSAGE");
			if (!directO2CPreference)
				expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.success", txnId);
			else
				expected = MessagesDAO.prepareMessageByKey("channeltransfer.transferdetailssuccess.msg.successwithautoapproval", txnId);

			Validator.messageCompare(actual, expected);

			/*
			 * Test case to perform O2C approval level 1
			 */
			currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 1 for category "+category+" with parent category "+parentCategory+", product type"+productType);
			currentNode.assignCategory("UAP");
			if (!directO2CPreference) {
			map= o2cTrans.performingLevel1Approval(userMSISDN, txnId);
			amountsParser.convertStringToLong(map.get("NetPayableAmount"));
			netPayableAmount= amountsParser.getValue();
			 
			if(netPayableAmount<=firstApprov)
				expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
			else
				expected1= MessagesDAO.prepareMessageByKey("channeltransfer.approval.levelone.msg.success", txnId);

			Validator.messageCompare(map.get("actualMessage"), expected1);
			} else {
				Log.skip("Direct Operator to Channel is applicable in system");
			}

			/*
			 * Test Case to perform approval level 2
			 */
			if(!directO2CPreference && netPayableAmount>firstApprov) {
				currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 2 for category "+category+" with parent category "+parentCategory+", product type"+productType);
				currentNode.assignCategory("UAP");

				String actual2= o2cTrans.performingLevel2Approval(userMSISDN, txnId, quantity);
				if(netPayableAmount<=secondApprov)
					expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);
				else
					expected2= MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", txnId);
				
				Validator.messageCompare(actual2, expected2);
			}

			/*
			 * Test case to perform approval level 3
			 */
			if(!directO2CPreference && netPayableAmount>secondApprov) {
				currentNode = test.createNode("To verify that Channel Admin is able to perform Operator to channel Transfer Approval 3 for category "+category+" with parent category "+parentCategory+", product type"+productType);
				currentNode.assignCategory("UAP");

				String actual3= o2cTrans.performingLevel3Approval(userMSISDN, txnId, quantity);
				String expected3= MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", txnId);

				Validator.messageCompare(actual3, expected3);
			}

			
			mapParam.put("transferNum", map.get("TRANSACTION_ID").trim());
			mapParam.put("productType", productType);
			ExcelUtility.setExcelFile(masterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			mapParam.put("category", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1));

			currentNode=test.createNode("To verify that channel user is able to check O2C Enquiry by Transaction Number Pannel");
			currentNode.assignCategory("UAP");
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchTransferID);
			currentNode.addScreenCaptureFromPath(resultMap.get("screenshot"));

			break;



		default: Log.info("No valid data found."); 
		}

	}


}
