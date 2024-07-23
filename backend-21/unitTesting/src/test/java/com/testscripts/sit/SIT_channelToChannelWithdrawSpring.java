package com.testscripts.sit;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CWithdrawSpring;
import com.Features.mapclasses.C2CWithdrawSpringMap;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.pageobjects.channeluserspages.c2cwithdraw.C2CWithdrawConfirmPageSpring;
import com.pageobjects.channeluserspages.c2cwithdraw.C2CWithdrawProductPageSpring;
import com.pageobjects.channeluserspages.c2cwithdraw.C2CWithdrawUserSearchPageSpring;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.utils.Log;
import com.utils.Validator;

public class SIT_channelToChannelWithdrawSpring extends BaseTest {

	C2CWithdrawConfirmPageSpring c2CWithdrawConfirmPageSpring;
	C2CWithdrawProductPageSpring c2CWithdrawProductPageSpring;
	C2CWithdrawUserSearchPageSpring c2CWithdrawUserSearchPageSpring;


		        static boolean TestCaseCounter = false;
				NetworkAdminHomePage networkAdminHomePage;
				Map<String,String> dataMap;
				
				
		@DataProvider(name = "withdraw")
		public Object[][] DomainCategoryProvider_validations() {
			C2CWithdrawSpringMap  c2CWithdrawSpringMap=new C2CWithdrawSpringMap();
			
			String[] description=new String[15];
			description[0]="To verify that channel user is not able to perform C2C withdraw if it msisdn is null";
			description[1]="To verify that channel User is not able to go tRo the product page if category name is blank";
			description[2]="To verify that the channel user is not able to go to product page if username is blank";
			description[3]="To verify that the user is not able to go to confirm page if pin is blank";
			description[4]="To verify that the user is not able to go to confirm page if quantity is blank";
			description[5]="To verify that user is not able to perform withdraw when the user is suspended";
			/*description[6]="To verify that user is not able to perform withdraw when the user is out suspended";*/
			description[7]="To verify that user is  able to perform withdraw successfully";
			
			Object[][] geoDomainData = {{0,description[0], c2CWithdrawSpringMap.setC2CMap("toMSISDN", "")},
					{1,description[1], c2CWithdrawSpringMap.setC2CMap("toCategory", "")},
					{2,description[2], c2CWithdrawSpringMap.setC2CMap("userName", "")},
					{3,description[3], c2CWithdrawSpringMap.setC2CMap("PIN", "")},
					{4,description[4], c2CWithdrawSpringMap.setC2CMap("quantity", "")},
					{5,description[5], c2CWithdrawSpringMap.setC2CMap("senderSuspended", "Y")},
					/*{6,description[6], c2CWithdrawSpringMap.setC2CMap("outSuspended", "Y")},//Removed it
*/					{7,description[7], c2CWithdrawSpringMap.setC2CMap("", "")}
					
					                    };
													
			return geoDomainData;
		}
		
		@Test(dataProvider = "withdraw")
		public void testCycleSIT(int caseNum,String description, Map<String, String> mapParam) throws IOException, InterruptedException{
			 Log.startTestCase(this.getClass().getName());
				
				if (TestCaseCounter == false) { 
					test = extent.createTest("[SIT]C2C Withdraw");
					TestCaseCounter = true;
				}
				
				Map<String, String> resultMap = new HashMap<String, String>();
				
				C2CWithdrawSpring c2CWithdrawSpring = new C2CWithdrawSpring(driver);
				String actualMsg;
				String expectedMsg;
				
				switch(caseNum){
				//Add Operator User
				
				case 0://To verify that channel user is not able to perform C2C withdraw if it msisdn is null
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = c2CWithdrawSpring.SIT_channel2channelWithdrawSpring(mapParam);
					 actualMsg =  resultMap.get("fieldError");
					 expectedMsg = MessagesDAO.prepareMessageByKey("pretups.userreturn.withdraw.msisdn.is.required");
					Validator.messageCompare(actualMsg, expectedMsg.trim());
					break;
					
				case 1://To verify that channel User is not able to go to the product page if category name is blank
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = c2CWithdrawSpring.SIT_channel2channelWithdrawSpringForUserSearch(mapParam);
					 actualMsg =  resultMap.get("fieldError");
					 expectedMsg = MessagesDAO.prepareMessageByKey("pretups.withdraw.category.code.is.required");
					Validator.messageCompare(actualMsg, expectedMsg.trim());
					break;
					
				case 2://To verify that the channel user is not able to go to product page if username is blank
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = c2CWithdrawSpring.SIT_channel2channelWithdrawSpringForUserSearch(mapParam);
					 actualMsg =  resultMap.get("fieldError");
					 expectedMsg = MessagesDAO.prepareMessageByKey("pretups.user.name.is.required");
					Validator.messageCompare(actualMsg, expectedMsg.trim());
					break;
					
				
				case 3://To verify that the user is not able to go to confirm page if pin is blank
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = c2CWithdrawSpring.SIT_channel2channelWithdrawSpringForUserSearch(mapParam);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.pin.is.required");
					Validator.messageCompare(actualMsg, expectedMsg.trim());
					break;
					
				case 4://To verify that the user is not able to go to confirm page if quantity is blank
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = c2CWithdrawSpring.SIT_channel2channelWithdrawSpringForUserSearch(mapParam);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = "SUCCESS";
					Validator.messageCompare(actualMsg, expectedMsg.trim());
					break;
					
				case 5://To verify that user is not able to perform withdraw when the user is suspended
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = c2CWithdrawSpring.SIT_channel2channelWithdrawSpring(mapParam);
					actualMsg=resultMap.get("actualMessage");
					expectedMsg=resultMap.get("expectedMessage");
					Validator.messageCompare(actualMsg, expectedMsg.trim());
					break;
			
				/*case 6://sender is out suspended
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = c2CWithdrawSpring.SIT_channel2channelWithdrawSpring(mapParam);
					actualMsg=resultMap.get("actualMessage");
					expectedMsg=resultMap.get("expectedMessage");
					Validator.messageCompare(actualMsg, expectedMsg.trim());
					
					break;*/
					
				case 7://To verify that user is  able to perform withdraw successfully
					c2CWithdrawProductPageSpring=new C2CWithdrawProductPageSpring(driver);
					currentNode=test.createNode(description);
					currentNode.assignCategory("SIT");
					resultMap = c2CWithdrawSpring.SIT_channel2channelWithdrawSpring(mapParam);
					actualMsg=resultMap.get("actualMessage");
					expectedMsg=MessagesDAO.prepareMessageByKey("pretups.userreturn.withdraw.msg.success",c2CWithdrawProductPageSpring.getTransactionID());
					Validator.messageCompare(actualMsg, expectedMsg.trim());
					break;
		
		}
				
				
				
		}



}
