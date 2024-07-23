package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2STransferSpring;
import com.Features.Enquiries.C2STransfersEnquirySpring;
import com.Features.mapclasses.C2STransferSpringMap;
import com.Features.mapclasses.OperatorUserSpringMap;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.Log;
import com.utils.Validator;

public class SIT_OperatorUserC2STransfersEnquirySpring extends BaseTest{
	
	static boolean TestCaseCounter = false;
	NetworkAdminHomePage networkAdminHomePage;
	Map<String,String> dataMap;


	@DataProvider(name = "transferData")
	public Object[][] DomainCategoryProvider_validations() {

		OperatorUserSpringMap c2SEnquirySpringMap = new OperatorUserSpringMap();

		String[] description=new String[15];
		description[0]="To verify that operator user is not able to check C2S Enquiry if Service is not selected for TransferID panel";
		description[1]="To verify that operator user is not able to check C2S Enquiry if From date is null for TransferID panel";
		description[2]="To verify that operator user is not able to check C2S Enquiry if To date is null for TransferID panel";
		description[3]="To verify that operator user is not able to check C2S Enquiry if Transfer ID is null for TransferID panel";
		description[4]="To verify that operator user is not able to check C2S Enquiry if Service is not selected  for Sender Mobile Number panel";
		description[5]="To verify that operator user is not able to check C2S Enquiry if From date is null for Sender Mobile Number panel";
		description[6]="To verify that operator user is not able to check C2S Enquiry if To date is null for Sender Mobile Number panel";
		description[7]="To verify that operator user is not able to check C2S Enquiry if Sender MSISDN is null for Sender Mobile Number panel";
		description[8]="To verify that operator user is not able to check C2S Enquiry if Sender MSISDN is not numeric for Sender Mobile Number panel";
		description[9]="To verify that operator user is not able to check C2S Enquiry if Service is not selected  for Receiver Mobile Number panel";
		description[10]="To verify that operator user is not able to check C2S Enquiry if From date is null for Receiver Mobile Number panel";
		description[11]="To verify that operator user is not able to check C2S Enquiry if To date is null for Receiver Mobile Number panel";
		description[12]="To verify that operator user is not able to check C2S Enquiry if Receiver MSISDN is null for Receiver Mobile Number panel";
		description[13]="To verify that operator user is not able to check C2S Enquiry if Receiver MSISDN is not numeric for Receiver Mobile Number panel";
		description[14]="To verify that operator user is able to check C2S Enquiry for TransferID panel";

		Object[][] testData = {{0,description[0], c2SEnquirySpringMap.setOperatorUserMap("service", "Select")},
				{1,description[1], c2SEnquirySpringMap.setOperatorUserMap("fromDate", "")},
				{2,description[2], c2SEnquirySpringMap.setOperatorUserMap("toDate", "")},
				{3,description[3], c2SEnquirySpringMap.setOperatorUserMap("transferID", "")},
				{4,description[4], c2SEnquirySpringMap.setOperatorUserMap("service", "Select")},
				{5,description[5], c2SEnquirySpringMap.setOperatorUserMap("fromDate", "")},
				{6,description[6], c2SEnquirySpringMap.setOperatorUserMap("toDate", "")},
				{7,description[7], c2SEnquirySpringMap.setOperatorUserMap("senderMSISDN", "")},
				{8,description[8], c2SEnquirySpringMap.setOperatorUserMap("senderMSISDN", "abcd12")},
				{9,description[9], c2SEnquirySpringMap.setOperatorUserMap("service", "Select")},
				{10,description[10], c2SEnquirySpringMap.setOperatorUserMap("fromDate", "")},
				{11,description[11], c2SEnquirySpringMap.setOperatorUserMap("toDate", "")},
				{12,description[12], c2SEnquirySpringMap.setOperatorUserMap("receiverMSISDN", "")},
				{13,description[13], c2SEnquirySpringMap.setOperatorUserMap("receiverMSISDN", "abcd12")},
				/*{14,description[14], c2SEnquirySpringMap.setOperatorUserMap(null, null)}*/
		};

		return testData;
	}


	@Test(dataProvider = "transferData")
	public void SIT_C2STransferEnquiry(int caseNum, String description, HashMap<String, String> mapParam) throws InterruptedException, IOException{

		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]C2S Transfer Enquiry");
			TestCaseCounter = true;
		}

		C2STransfersEnquirySpring c2STransfersEnquirySpring = new C2STransfersEnquirySpring(driver);
		Map<String, String> resultMap = null;
		String actualMsg, expectedMsg;
		String searchTransferID = "transferID";
		String searchSenderMSISDN = "senderMsisdn";
		String searchReceiverMSISDN = "receiverMsisdn";
		currentNode=test.createNode(description);
		currentNode.assignCategory("SIT");
		switch(caseNum){

     	case 0://To verify that operator user is not able to check C2S Enquiry if Service is null for TransferID panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchTransferID);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.c2s.query.c2stransferenquiry.servicetype.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
			
		case 1://To verify that operator user is not able to check C2S Enquiry if From date is null for TransferID panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchTransferID);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.from.date.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
			
		case 2://To verify that operator user is not able to check C2S Enquiry if To date is null for TransferID panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchTransferID);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.to.date.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
		
		case 3://To verify that operator user is not able to check C2S Enquiry if Transfer ID is null for TransferID panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchTransferID);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.transferID.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
		
		case 4://To verify that operator user is not able to check C2S Enquiry if Service is null for Sender Mobile Number panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchSenderMSISDN);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.c2s.query.c2stransferenquiry.servicetype.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
			
		case 5://To verify that operator user is not able to check C2S Enquiry if From date is null for Sender Mobile Number panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchSenderMSISDN);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.from.date.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
			
		case 6://To verify that operator user is not able to check C2S Enquiry if To date is null for Sender Mobile Number panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchSenderMSISDN);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.to.date.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
			
		case 7://To verify that operator user is not able to check C2S Enquiry if Sender MSISDN is null for Sender Mobile Number panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchSenderMSISDN);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.msisdn.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
		
		case 8://To verify that operator user is not able to check C2S Enquiry if Sender MSISDN is not numeric for Sender Mobile Number panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchSenderMSISDN);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.msisdn.is.not.valid");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
			
		case 9://To verify that operator user is not able to check C2S Enquiry if Service is null for Receiver Mobile Number panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchReceiverMSISDN);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.c2s.query.c2stransferenquiry.servicetype.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
			
		case 10://To verify that operator user is not able to check C2S Enquiry if From date is null for Receiver Mobile Number panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchReceiverMSISDN);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.from.date.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
			
		case 11://To verify that operator user is not able to check C2S Enquiry if To date is null for Receiver Mobile Number panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchReceiverMSISDN);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.to.date.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
			
		case 12://To verify that operator user is not able to check C2S Enquiry if Receiver MSISDN is null for Receiver Mobile Number panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchReceiverMSISDN);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.msisdn.is.required");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
		
		case 13://To verify that operator user is not able to check C2S Enquiry if Receiver MSISDN is not numeric for Receiver Mobile Number panel
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchReceiverMSISDN);
			actualMsg =  resultMap.get("fieldError");
			expectedMsg = MessagesDAO.prepareMessageByKey("pretups.msisdn.is.not.valid");
			Validator.messageCompare(actualMsg, expectedMsg);
			break;
		
		case 14://To verify that channel user is able to check C2S Enquiry by TransferID/Sender MSISDN/Receiver MSISDN
			Map<String, String> c2SResultMap = new HashMap<String,String>();
			C2STransferSpringMap c2STransferSpringMap = new C2STransferSpringMap();
			C2STransferSpring c2sTransferSpring = new C2STransferSpring(driver);
			c2SResultMap = c2sTransferSpring.performC2STransfer(c2STransferSpringMap.defaultMap(),false,false);
			mapParam.put("transferID", c2SResultMap.get("transferID").trim());
			mapParam.put("senderMSISDN", c2SResultMap.get("senderMSISDN"));
			mapParam.put("receiverMSISDN", c2SResultMap.get("receiverMSISDN"));
			mapParam.put("category", c2STransferSpringMap.getC2SMap("category"));
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchTransferID);
			currentNode.addScreenCaptureFromPath(resultMap.get("screenshot"));
			
			currentNode=test.createNode("To verify that channel user is able to check C2S Enquiry by Sender Mobile Number panel");
			currentNode.assignCategory("SIT");
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchSenderMSISDN);
			currentNode.addScreenCaptureFromPath(resultMap.get("screenshot"));
			
			currentNode=test.createNode("To verify that channel user is able to check C2S Enquiry by Receiver Mobile Number panel");
			currentNode.assignCategory("SIT");
			resultMap = c2STransfersEnquirySpring.validateC2STransfersEnquiryForOperatorUser(mapParam, searchReceiverMSISDN);
			currentNode.addScreenCaptureFromPath(resultMap.get("screenshot"));
			break;
			
		default: Log.info("No valid data found."); 
		}
	}

}
