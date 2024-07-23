package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChangePinSpring;
import com.Features.mapclasses.ChangePinMap;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.pageobjects.channeluserspages.changePin.ChangePinPage;
import com.utils.Log;
import com.utils.Validator;

public class SIT_ChangePin extends BaseTest {


	
	 static boolean TestCaseCounter = false;
	 Map<String,String> dataMap;
	 
		@DataProvider(name = "testData")
		public Object[][] testData() {
				
			ChangePinMap changePinMap = new ChangePinMap();
			String[] description=new String[16];
			description[0]="To verify that user is unable to change pin if search msisdn is null";
			description[1]="To verify that user is unable to change pin if search msisdn is not numeric";
			description[2]="To verify that user is unable to change pin if search login ID is null";
			description[3]="To verify that user is unable to change pin if search login ID is numeric";
			description[4]="To verify that user is unable to change pin if search user is null";
			description[5]="To verify that user is unable to change pin if category is not selected";
			description[6]="To verify that user is unable to change  pin if chcekbox is not selected";
			description[7]="To verify that user is unable to change  pin if old pin is left Blank";
			description[8]="To verify that user is unable to change  pin if new pin is left Blank";
			description[9]="To verify that user is unable to change  pin if confirm pin is left Blank";
			description[10]="To verify that user is unable to change  pin if old pin is not numeric";
			description[11]="To verify that user is unable to change  pin if new pin is not numeric";
			description[12]="To verify that user is unable to change  pin if confirm pin is not numeric";
			description[13]="To verify that user is unable to change  pin if new pin and confirm pin are not same";
			description[14]="To verify that user is unable to change  pin if old pin and new pin are same";
			description[15]="To verify that user is able to change pin";
			
			Object[][] testData = {{0, description[0], changePinMap.getChangePinMap("msisdn", "")},
					{1, description[1], changePinMap.getChangePinMap("msisdn", "abcd")},
					{2, description[2], changePinMap.getChangePinMap("loginId", "")},
					/*{3, description[3], changePinMap.getChangePinMap("loginId", "1234")},
					{4, description[4], changePinMap.getChangePinMap("user", "")},*/
					{5, description[5], changePinMap.getChangePinMap("childCategory", "Select")},
					{7, description[7], changePinMap.getChangePinMap("oldPin", "")},
					{8, description[8], changePinMap.getChangePinMap("newPin", "")},
					{9, description[9], changePinMap.getChangePinMap("confirmPin", "")},
					{10, description[10], changePinMap.getChangePinMap("oldPin", "qwerty123")},
					{11, description[11], changePinMap.getChangePinMap("newPin", "asdf123")},
					{12, description[12], changePinMap.getChangePinMap("confirmPin", "john123")},
					{13, description[13], changePinMap.getChangePinMap("newPin", "1")},
					{14, description[14], changePinMap.getChangePinMap("newPin", changePinMap.defaultMap().get("oldPin"))},
					{15, description[15], changePinMap.getChangePinMap(null, null)}
					};
			return testData;
		}
		
		@Test(dataProvider = "testData")
		public void changePinSIT(int caseNum,String description, HashMap<String, String> mapParam) throws IOException, InterruptedException{
			 Log.startTestCase(this.getClass().getName());
				
				if (TestCaseCounter == false) { 
					test = extent.createTest("[SIT]Change  Pin");
					TestCaseCounter = true;
				}
				
				ChangePinPage changePinPage = new ChangePinPage(driver);
				ChangePinSpring changePinSpring = new ChangePinSpring(driver);
				Map<String, String> resultMap = null;
				String actualMsg, expectedMsg;
				String searchMsisdn = "msisdn";
				String searchLoginId = "loginId";
				String searchUser = "user";
				currentNode=test.createNode(description);
				currentNode.assignCategory("SIT");
				switch(caseNum){
				
				case 0://To verify that user is unable to change pin if search msisdn is null
					
					resultMap = changePinSpring.changePIN(mapParam, searchMsisdn);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.changepin.msisdn.is.required");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;
					
				case 1://To verify that user is unable to change pin if search msisdn is not numeric
					
					resultMap = changePinSpring.changePIN(mapParam, searchMsisdn);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.changepin.msisdn.is.not.valid");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;
					
				case 2://To verify that user is unable to change pin if search login ID is null
					
					resultMap = changePinSpring.changePIN(mapParam, searchLoginId);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.loginId.is.required");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;
					
				case 3://To verify that user is unable to change pin if search login ID is numeric

					resultMap = changePinSpring.changePIN(mapParam, searchLoginId);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.alphabetic.and.underscore.allowed");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;
					
				case 4://To verify that user is unable to change pin if search user is null
					
					resultMap = changePinSpring.changePIN(mapParam, searchUser);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.mandatory");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;
					
					
				case 5://To verify that user is unable to change pin if category is not selected
					
					resultMap = changePinSpring.changePIN(mapParam, searchUser);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.category.code.is.required");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;
				
				case 6://To verify that user is unable to change  pin if chcekbox is not selected
					
					resultMap = changePinSpring.changePIN(mapParam, searchMsisdn);
					actualMsg =  resultMap.get("alertifyError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.select.at.least.one.msisdn");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;	
					
				case 7://To verify that user is unable to change  pin if old pin is left Blank
					
					resultMap = changePinSpring.changePIN(mapParam, searchMsisdn);
					actualMsg =  changePinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.mandatory");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;	
					
				case 8://To verify that user is unable to change  pin if new pin is left Blank

					
					resultMap = changePinSpring.changePIN(mapParam, searchMsisdn);
					actualMsg =  changePinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.mandatory");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;	
					
				case 9://To verify that user is unable to change  pin if confirm pin is left Blank
					
					resultMap = changePinSpring.changePIN(mapParam, searchMsisdn);					
					actualMsg =  changePinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.mandatory");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
					
				case 10://To verify that user is unable to change  pin if old pin is not numeric
					
					resultMap = changePinSpring.changePIN(mapParam, searchMsisdn);					
					actualMsg =  changePinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.numeric");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
					
				case 11://To verify that user is unable to change  pin if new pin is not numeric
					
					resultMap = changePinSpring.changePIN(mapParam, searchMsisdn);					
					actualMsg =  changePinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.numeric");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
					
				case 12://To verify that user is unable to change  pin if confirm pin is not numeric
					
					resultMap = changePinSpring.changePIN(mapParam, searchMsisdn);					
					actualMsg =  changePinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.error.numeric");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
					
				case 13://To verify that user is unable to change  pin if new pin and confirm pin are not same
					
					resultMap = changePinSpring.changePIN(mapParam, searchMsisdn);					
					actualMsg =  changePinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.different.new.and.confirm.pin.without.msisdn");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
					
				case 14://To verify that user is unable to change  pin if old pin and new pin are same
					
					resultMap = changePinSpring.changePIN(mapParam, searchMsisdn);					
					actualMsg =  changePinPage.getFieldError();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.same.old.and.new.pin.without.msisdn");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
					
				case 15://To verify that user is able to change pin
					resultMap = changePinSpring.changePIN(mapParam, searchMsisdn);					
					actualMsg =  changePinPage.getFormMessage();
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.changepin.msg.updatesuccess");
					Validator.messageCompare(actualMsg, expectedMsg);					
					break;
		}
	}

}
