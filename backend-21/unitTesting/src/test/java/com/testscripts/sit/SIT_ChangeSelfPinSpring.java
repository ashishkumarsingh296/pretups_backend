package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChangeSelfPinSpring;
import com.Features.mapclasses.ChangeSelfPinMap;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.pageobjects.channeluserspages.changeSelfPin.ChangeSelfPinPage;
import com.utils.Log;
import com.utils.Validator;

public class SIT_ChangeSelfPinSpring extends BaseTest {

	
	 static boolean TestCaseCounter = false;
	 Map<String,String> dataMap;
	 
		@DataProvider(name = "testData")
		public Object[][] testData() {
				
			ChangeSelfPinMap changeSelfPinMap = new ChangeSelfPinMap();
			String[] description=new String[10];
			description[0]="To verify that user is unable to change self pin if chcekbox is not selected";
			description[1]="To verify that user is unable to change self pin if old pin is left Blank";
			description[2]="To verify that user is unable to change self pin if new pin is left Blank";
			description[3]="To verify that user is unable to change self pin if confirm pin is left Blank";
			description[4]="To verify that user is unable to change self pin if old pin is not numeric";
			description[5]="To verify that user is unable to change self pin if new pin is not numeric";
			description[6]="To verify that user is unable to change self pin if confirm pin is not numeric";
			description[7]="To verify that user is unable to change self pin if new pin and confirm pin are not same";
			description[8]="To verify that user is unable to change self pin if old pin and new pin are same";
			description[9]="To verify that user is able to change self pin";
			
			Object[][] testData = {{0, description[0], changeSelfPinMap.getChangeSelfPinMap("msisdn", "")},
					{1, description[1], changeSelfPinMap.getChangeSelfPinMap("oldPin", "")},
					{2, description[2], changeSelfPinMap.getChangeSelfPinMap("newPin", "")},
					{3, description[3], changeSelfPinMap.getChangeSelfPinMap("confirmPin", "")},
					{4, description[4], changeSelfPinMap.getChangeSelfPinMap("oldPin", "qwerty123")},
					{5, description[5], changeSelfPinMap.getChangeSelfPinMap("newPin", "asdf123")},
					{6, description[6], changeSelfPinMap.getChangeSelfPinMap("confirmPin", "john123")},
					{7, description[7], changeSelfPinMap.getChangeSelfPinMap("newPin", "1")},
					{8, description[8], changeSelfPinMap.getChangeSelfPinMap("newPin", changeSelfPinMap.defaultMap().get("oldPin"))},
					{9, description[9], changeSelfPinMap.getChangeSelfPinMap(null, null)}
					
			};
			return testData;
		}
		
		@Test(dataProvider = "testData")
		public void changeSelfPinSIT(int caseNum,String description, HashMap<String, String> mapParam) throws IOException, InterruptedException{
			 Log.startTestCase(this.getClass().getName());
				
				if (TestCaseCounter == false) { 
					test = extent.createTest("[SIT]Change Self Pin");
					TestCaseCounter = true;
				}
				
				ChangeSelfPinPage changeSelfPinPage = new ChangeSelfPinPage(driver);
				ChangeSelfPinSpring changeSelfPinSpring = new ChangeSelfPinSpring(driver);
				Map<String, String> resultMap = null;
				String actualMsg, expectedMsg;
				switch(caseNum){
				
				
				case 0://To verify that user is unable to change self pin if chcekbox is not selected
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = changeSelfPinSpring.changeSelfPIN(mapParam);
					actualMsg =  resultMap.get("alertifyError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.select.at.least.one.msisdn");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;	
					
				case 1://To verify that user is unable to change self pin if old pin is left Blank
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = changeSelfPinSpring.changeSelfPIN(mapParam);
					actualMsg =  changeSelfPinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.mandatory");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;	
					
				case 2://To verify that user is unable to change self pin if new pin is left Blank

					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = changeSelfPinSpring.changeSelfPIN(mapParam);
					actualMsg =  changeSelfPinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.mandatory");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;	
					
				case 3://To verify that user is unable to change self pin if confirm pin is left Blank
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = changeSelfPinSpring.changeSelfPIN(mapParam);					
					actualMsg =  changeSelfPinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.mandatory");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
					
				case 4://To verify that user is unable to change self pin if old pin is not numeric
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = changeSelfPinSpring.changeSelfPIN(mapParam);					
					actualMsg =  changeSelfPinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.numeric");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
					
				case 5://To verify that user is unable to change self pin if new pin is not numeric
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = changeSelfPinSpring.changeSelfPIN(mapParam);					
					actualMsg =  changeSelfPinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.numeric");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
					
				case 6://To verify that user is unable to change self pin if confirm pin is not numeric
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = changeSelfPinSpring.changeSelfPIN(mapParam);					
					actualMsg =  changeSelfPinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.numeric");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
					
				case 7://To verify that user is unable to change self pin if new pin and confirm pin are not same
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = changeSelfPinSpring.changeSelfPIN(mapParam);					
					actualMsg =  changeSelfPinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.different.new.and.confirm.pin.without.msisdn");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
					
				case 8://To verify that user is unable to change self pin if old pin and new pin are same
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = changeSelfPinSpring.changeSelfPIN(mapParam);					
					actualMsg =  changeSelfPinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.same.old.and.new.pin.without.msisdn");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
					
				case 9://To verify that user is able to change self pin
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = changeSelfPinSpring.changeSelfPIN(mapParam);					
					actualMsg =  changeSelfPinPage.getFormMessage();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.msg.updatesuccess");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
		}
	}
}
