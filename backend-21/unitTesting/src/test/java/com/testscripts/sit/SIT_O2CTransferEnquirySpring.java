package com.testscripts.sit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.Enquiries.O2CTransferEnquirySpring;
import com.Features.mapclasses.O2CEnquiryTransferMap;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.Log;
import com.utils.Validator;

public class SIT_O2CTransferEnquirySpring extends BaseTest{


	static boolean TestCaseCounter = false;
	NetworkAdminHomePage networkAdminHomePage;
	Map<String,String> dataMap;


	@DataProvider(name = "transferData")
	public Object[][] DomainCategoryProvider_validations() throws SQLException {

		O2CEnquiryTransferMap o2cEnquiryTransfermap = new O2CEnquiryTransferMap();

		String[] description=new String[10];
		description[0]="To verify that channel user is not able to check O2C Enquiry if Mobile Number is null for Mobile Number panel";
		description[1]="To verify that channel user is not able to check O2C Enquiry if Mobile Number is not numeric for Mobile Number panel";
		description[2]="To verify that channel user is not able to check O2C Enquiry if Transfer category is not selected for Mobile Number panel";
		description[3]="To verify that channel user is not able to check O2C Enquiry if To date is null for Mobile Number panel";
		description[4]="To verify that channel user is not able to check O2C Enquiry if Product type is not selected for Geographical Domain panel";
		description[5]="To verify that channel user is not able to check O2C Enquiry if Category is not selected for Geographical Domain panel";
		description[6]="To verify that channel user is not able to check O2C Enquiry if Transfer Category is not selected for Geographical Domain panel";
		description[7]="To verify that channel user is not able to check O2C Enquiry if From date is null for Geographical Domain panel";
		description[8]="To verify that channel user is not able to check O2C Enquiry if To date is null for Geographical Domain panel";
		description[9]="To verify that channel user is not able to check O2C Enquiry if User Name is null for Geographical Domain panel";


		Object[][] testData={
				{0,description[0], o2cEnquiryTransfermap.setO2CEnquiryMap("msisdn","")},
				{1,description[1], o2cEnquiryTransfermap.setO2CEnquiryMap("msisdn", "wsfw")},
				{2,description[2], o2cEnquiryTransfermap.setO2CEnquiryMap("transferCategory", "Select")},
				{3,description[3], o2cEnquiryTransfermap.setO2CEnquiryMap("toDate","")},
				{4,description[4], o2cEnquiryTransfermap.setO2CEnquiryMap("productType", "Select")},
				{5,description[5], o2cEnquiryTransfermap.setO2CEnquiryMap("domainCategory", "Select")},
				{6,description[6], o2cEnquiryTransfermap.setO2CEnquiryMap("transferCategory", "Select")},
				{7,description[7], o2cEnquiryTransfermap.setO2CEnquiryMap("fromDate","")},
				{8,description[8], o2cEnquiryTransfermap.setO2CEnquiryMap("toDate","")},
				{9,description[9], o2cEnquiryTransfermap.setO2CEnquiryMap("userName","")}
		};

		return testData;
	}

	@Test(dataProvider = "transferData")
	public void SIT_O2CTransferEnquiry(int caseNum, String description, HashMap<String, String> mapParam) throws InterruptedException, IOException{
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]O2C Transfer Enquiry");
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
		currentNode.assignCategory("SIT");
		switch(caseNum){

		case 0://To verify that channel user is not able to check O2C Enquiry if Mobile Number is not numeric for Mobile Number panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchMobileNumber);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.userCode.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;

		case 1://To verify that channel user is not able to check O2C Enquiry if Mobile Number is not numeric for Mobile Number panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchMobileNumber);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.userCode.is.not.valid");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;

		case 2://To verify that channel user is not able to check O2C Enquiry if Transfer category is null for Mobile Number panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchMobileNumber);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.trfCatForUserCode.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;


		case 3://To verify that channel user is not able to check O2C Enquiry if to date is null for Mobile Number panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchMobileNumber);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.to.date.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;

		case 4://To verify that channel user is not able to check O2C Enquiry if Product type is null for Geographical Domain panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchGeography);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.productType.code.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;

		case 5://To verify that channel user is not able to check O2C Enquiry if category is null for Geographical Domain panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchGeography);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.category.code.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;

		case 6://To verify that channel user is not able to check O2C Enquiry if transfer category is null for Geographical Domain panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchGeography);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.transferCategoryCode.code.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;

		case 7://To verify that channel user is not able to check O2C Enquiry if from date is null for Geographical Domain panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchGeography);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.from.date.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;

		case 8://To verify that channel user is not able to check O2C Enquiry if to date is null for Geographical Domain panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchGeography);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.to.date.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;

		case 9://To verify that channel user is not able to check O2C Enquiry if user name is null for Geographical Domain panel
			resultMap = o2CTransfersEnquirySpring.o2CTransfer(mapParam, searchGeography);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.channelCategoryUserName.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;


		default: Log.info("No valid data found."); 
		}

	}
}
