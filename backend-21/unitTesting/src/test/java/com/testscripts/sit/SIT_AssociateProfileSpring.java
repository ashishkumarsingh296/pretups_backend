package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.AssociateProfileSpring;
import com.Features.mapclasses.AssociateProfileSpringMap;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.utils.Log;
import com.utils.Validator;

public class SIT_AssociateProfileSpring extends BaseTest {



	
	 static boolean TestCaseCounter = false;
	 Map<String,String> dataMap;
	 
		@DataProvider(name = "testData")
		public Object[][] testData() {
				
			AssociateProfileSpringMap associateProfileMap = new AssociateProfileSpringMap();
			String[] description=new String[16];
			description[0]="To verify that user is unable to Associate Profile if search msisdn is null";
			description[1]="To verify that user is unable to Associate Profile if search msisdn is not numeric";
			description[2]="To verify that user is unable to Associate Profile if search login ID is null";
			description[3]="To verify that user is unable to Associate Profile if search login ID is numeric";
			description[4]="To verify that user is unable to Associate Profile if search user is null";
			description[5]="To verify that user is unable to Associate Profile if category is not selected";
			
			
			Object[][] testData = {{0, description[0], associateProfileMap.getAssociateProfileMap("msisdn", "")},
					{1, description[1], associateProfileMap.getAssociateProfileMap("msisdn", "abcd")},
					{2, description[2], associateProfileMap.getAssociateProfileMap("loginId", "")},
					{3, description[3], associateProfileMap.getAssociateProfileMap("loginId", "1234")},
					{4, description[4], associateProfileMap.getAssociateProfileMap("user", "")},
					{5, description[5], associateProfileMap.getAssociateProfileMap("childCategory", "Select")}
					};
			return testData;
		}
		
		@Test(dataProvider = "testData")
		public void associateProfileSIT(int caseNum,String description, HashMap<String, String> mapParam) throws IOException, InterruptedException{
			 Log.startTestCase(this.getClass().getName());
				
				if (TestCaseCounter == false) { 
					test = extent.createTest("[SIT]Associate Profile");
					TestCaseCounter = true;
				}
				
				AssociateProfileSpring associateProfileSpring = new AssociateProfileSpring(driver);
				Map<String, String> resultMap = null;
				String actualMsg, expectedMsg;
				String searchMsisdn = "msisdn";
				String searchLoginId = "loginId";
				String searchUser = "user";
				currentNode=test.createNode(description);
				currentNode.assignCategory("SIT");
				switch(caseNum){
				
				case 0://To verify that user is unable to Associate Profile if search msisdn is null
					
					resultMap = associateProfileSpring.associateProfile(mapParam, searchMsisdn);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.associateProfile.msisdn.is.required");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;
					
				case 1://To verify that user is unable to Associate Profile if search msisdn is not numeric
					
					resultMap = associateProfileSpring.associateProfile(mapParam, searchMsisdn);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.associateProfile.msisdn.is.not.valid");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;
					
				case 2://To verify that user is unable to Associate Profile if search login ID is null
					
					resultMap = associateProfileSpring.associateProfile(mapParam, searchLoginId);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.loginId.is.required");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;
					
				case 3://To verify that user is unable to Associate Profile if search login ID is numeric

					resultMap = associateProfileSpring.associateProfile(mapParam, searchLoginId);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.alphabetic.and.underscore.allowed");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;
					
				case 4://To verify that user is unable to Associate Profile if search user is null
					
					resultMap = associateProfileSpring.associateProfile(mapParam, searchUser);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.associateProfile.user.name.is.required");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;
					
					
				case 5://To verify that user is unable to Associate Profile if category is not selected
					
					resultMap = associateProfileSpring.associateProfile(mapParam, searchUser);
					actualMsg =  resultMap.get("fieldError");
					expectedMsg = MessagesDAO.prepareMessageByKey("pretups.associateProfile.category.code.is.required");
					Validator.messageCompare(actualMsg, expectedMsg);
					break;
		}
	}


}
