package com.testscripts.sit;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.OperatorUser;
import com.Features.mapclasses.OperatorUserCreationMap;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.superadminpages.addoperatoruser.AddOperatorUserPage;
import com.pageobjects.superadminpages.addoperatoruser.ApproveOperatorUsersPage;
import com.pageobjects.superadminpages.addoperatoruser.ModifyOperatorUserPage1;
import com.pageobjects.superadminpages.addoperatoruser.ViewOperatorUserPage1;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;


public class SIT_OperatorUserCreation extends BaseTest {
	        static boolean TestCaseCounter = false;
			NetworkAdminHomePage networkAdminHomePage;
			Map<String,String> dataMap;
			static int minPaswdLength;
			static int maxPaswdLength;
			
			
	@DataProvider(name = "Domain&CategoryProvider_validations")
	public Object[][] DomainCategoryProvider_validations() {
			
		OperatorUserCreationMap operatorUserCreationMap = new OperatorUserCreationMap();
		
		String[] description=new String[27];
		description[0]="To verify that operator is unable to add Operator User if First Name is not selected";
		description[1]="To verify that operator is unable to add Operator User if Subscriber Code is left Blank";
		description[2]="To verify that operator is unable to add  Operator User if Division is not selected";
		description[3]="To verify that operator is unable to add Operator User if LoginID is left Blank";
		description[4]="To verify that operator is unable to add Operator User if Password is left Blank";
		description[5]="To verify that operator is unable to add Operator User if Confirm Password is left Blank";
		description[6]="To verify that operator is unable to add Operator User if Confirm Password is different from Password entered.";
		description[7]="To verify that operator is unable to add Operator User if Password contains sequential characters";
		description[8]="To verify that operator is unable to add Operator User if Password's length is less than min characters";
		description[9]="To verify that operator is unable to add Operator User if Confirm Password's length is less than min characters";
		description[10]="To verify that operator is unable to add Operator User if Network is not assigned";
		description[11]="To verify that operator is unable to add Operator User if Geography is not assigned";
		description[12]="To verify that operator is unable to add Operator User if Phone Number is not assigned";
		description[13]="To verify that operator is unable to add Operator User if Product is not assigned";
		description[14]="To verify that operator is unable to add Operator User if Domain is not assigned";
		description[15]="To verify that operator is unable to modify Operator User if UserName is blank.";
		description[16]="To verify that operator is unable to modify Operator User if category is not selected.";
		description[17]="To verify that operator is unable to approve Operator User if loginID or category is not selected.";
		description[18]="To verify that operator is able to reject Operator User.";
		description[19]="To verify that operator is able to delete Operator User.";
		description[20]="To verify that operator is unable to view Operator User if category is not selected.";
		description[21]="To verify that operator is unable to view Operator User if UserName is blank.";
		description[22]="To verify that operator is unable to add Operator User if loginID is duplicate.";
		description[23]="To verify that Deleted Operator login id can be re-assigned to any other user.";
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		String parentName = ExcelUtility.getCellData(0,ExcelI.PARENT_NAME, 2);
		String categoryName = ExcelUtility.getCellData(0,ExcelI.CATEGORY_NAME, 2);
		minPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MIN_LOGIN_PWD_LENGTH"));
		 maxPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MAX_LOGIN_PWD_LENGTH"));

			
			Object[][] geoDomainData = {{0,description[0], operatorUserCreationMap.getOperatorUserMap("firstName",""), parentName, categoryName},
				                                {1,description[1], operatorUserCreationMap.getOperatorUserMap("subscriberCode", " "), parentName, categoryName},
												 {2,description[2], operatorUserCreationMap.getOperatorUserMap("DIVISION", "N"), parentName, categoryName},
										        {3,description[3], operatorUserCreationMap.getOperatorUserMap("LOGINID", ""), parentName, categoryName},
											   {4,description[4],operatorUserCreationMap.getOperatorUserMap("PASSWORD", ""), parentName, categoryName},
												 {5,description[5], operatorUserCreationMap.getOperatorUserMap("CONFIRMPASSWORD",""), parentName, categoryName},
												  {6,description[6], operatorUserCreationMap.getOperatorUserMap("CONFIRMPASSWORD","1364"), parentName, categoryName},
												 {7,description[7],operatorUserCreationMap.getOperatorUserMap("PASSWORD", "1234"), parentName, categoryName},
				                                {8,description[8],operatorUserCreationMap.getOperatorUserMap("PASSWORD", "3"), parentName, categoryName},
												{9,description[9],operatorUserCreationMap.getOperatorUserMap("CONFIRMPASSWORD", "3"), parentName, categoryName},
												{10,description[10],operatorUserCreationMap.getOperatorUserMap("AssignNetwork", "N"), parentName, categoryName},
												{11,description[11],operatorUserCreationMap.getOperatorUserMap("AssignGeography", "N"), parentName, categoryName},
												{12,description[12],operatorUserCreationMap.getOperatorUserMap("AssignPhoneNumber", "N"), parentName, categoryName},
												{13,description[13],operatorUserCreationMap.getOperatorUserMap("AssignProduct", "N"), parentName, categoryName},
												{14,description[14],operatorUserCreationMap.getOperatorUserMap("AssignDomain", "N"), parentName, categoryName},
												{15,description[15],operatorUserCreationMap.getOperatorUserMap("UserName", ""), parentName, categoryName},
				                                {16,description[16],operatorUserCreationMap.defaultMap(), parentName, categoryName},
				                                {17,description[17],operatorUserCreationMap.defaultMap(), parentName, categoryName},
				                                {18,description[18],operatorUserCreationMap.defaultMap(), parentName, categoryName},
				                                {19,description[19],operatorUserCreationMap.defaultMap(), parentName, categoryName},
				                                {20,description[20],operatorUserCreationMap.defaultMap(), parentName, categoryName},
				                                {21,description[21],operatorUserCreationMap.getOperatorUserMap("UserName", ""), parentName, categoryName},
				                                {22,description[22],operatorUserCreationMap.defaultMap(), parentName, categoryName},
					                            {23,description[23],operatorUserCreationMap.defaultMap(), parentName, categoryName},
												};
		
		return geoDomainData;
	}
	
	@Test(dataProvider = "Domain&CategoryProvider_validations")
	public void testCycleSIT(int CaseNum,String Description, HashMap<String, String> mapParam, String ParentUser, String LoginUser) throws IOException{
		 Log.startTestCase(this.getClass().getName());
			
			if (TestCaseCounter == false) { 
				test = extent.createTest("[SIT]Operator User Creation");
				TestCaseCounter = true;
			}
			
			AddOperatorUserPage addOptrUserPage = new AddOperatorUserPage(driver);
			ModifyOperatorUserPage1 modifyOptPage1  = new ModifyOperatorUserPage1(driver);;
			ApproveOperatorUsersPage approveOperatorUser  = new ApproveOperatorUsersPage(driver);
			ViewOperatorUserPage1 viewOptUserPage1 = new ViewOperatorUserPage1(driver);
			
			OperatorUser OperatorUserLogic = new OperatorUser(driver);
			String APPLEVEL = DBHandler.AccessHandler.getSystemPreference("OPT_USR_APRL_LEVEL");
			HashMap<String, String> optresultMap = null;
			switch(CaseNum){
			//Add Operator User
			
			case 0://To verify that operator is unable to add Operator User if First Name is not selected
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " when First Name is blank");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("user.addchanneluser.label.firstName"));
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 1://To verify that operator is unable to add Operator User if Subscriber Code is left Blank
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Subscriber Code is left Blank");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("user.addchanneluser.label.empcode"));
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 2://To verify that operator is unable to add Operator User if Division is not selected
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Subscriber Code is left Blank");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("user.addoperatoruser.label.division"));
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 3://To verify that operator is unable to add Operator User if LoginID is left Blank
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if LoginID is left Blank");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.adduser.error.loginidrequired");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 4://To verify that operator is unable to add Operator User if Password is left Blank
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Password is left Blank");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.adduser.error.webpasswordrequired");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 5://To verify that operator is unable to add Operator User if Confirm Password is left Blank
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Confirm Password is left Blank");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.adduser.error.confirmpasswordrequired");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 6://To verify that operator is unable to add Operator User if Confirm Password is different from Password entered.
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Confirm Password is different from Password entered.");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.adduser.error.confirmpasswordnotvalid");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;

			case 7://To verify that operator is unable to add Operator User if Password contains sequential characters
				try{ 
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Password contains sequential characters.");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("operatorutil.validatepassword.error.passwordconsecutive");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 8://To verify that operator is unable to add Operator User if Password's length is less than minimum characters
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Password's length is less than "+ String.valueOf(minPaswdLength)+ ".");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("operatorutil.validatepassword.error.passwordlenerr",String.valueOf(minPaswdLength),String.valueOf(maxPaswdLength));
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 9://To verify that operator is unable to add Operator User if Confirm Password's length is less than minimum characters
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Confirm Password's length is less than "+ String.valueOf(minPaswdLength)+ ".");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("viewedituser.editoperatoruser.error.invalidconfirmpasswordlength",String.valueOf(minPaswdLength));
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 10://To verify that operator is unable to add Operator User if Network is not assigned
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Network is not assigned.");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.adduser.error.networknotassigned");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 11://To verify that operator is unable to add Operator User if Geography is not assigned
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Geography is not assigned.");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.adduser.error.geographiesnotassigned");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 12://To verify that operator is unable to add Operator User if Phone Number is not assigned
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Phone Number is not assigned.");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("viewedituser.editoperatoruser.error.msisdnnotassigned");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 13://To verify that operator is unable to add Operator User if Product is not assigned
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Product is not assigned.");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.error.productrequired");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 14://To verify that operator is unable to add Operator User if Domain is not assigned
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to initiate " + LoginUser+ " if Domain is not assigned.");
					currentNode.assignCategory("SIT");
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.error.domainrequired");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
			
			//Modify Operator User
				
			case 15://To verify that operator is unable to modify Operator User if UserName is blank.
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to modify " + LoginUser+ " if UserName is blank.");
					currentNode.assignCategory("SIT");
					String type = "Modify";
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					String actualMessage = OperatorUserLogic.modifyOperatorDetails_SIT(ParentUser, LoginUser, mapParam, type);
					String expectedMessage = MessagesDAO.prepareMessageByKey("user.addoperatoruser.updatesuccessmessage", mapParam.get("UserName"));
					Validator.messageCompare(actualMessage, expectedMessage);
					
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  modifyOptPage1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.selectcategoryforedit.error.usermorethanone");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 16://To verify that operator is unable to modify Operator User if category is not selected.
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to modify if category is not selected.");
					currentNode.assignCategory("SIT");
					String type = "Modify";
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					String actualMessage = OperatorUserLogic.modifyOperatorDetails_SIT(ParentUser, "", mapParam, type);
					String expectedMessage = MessagesDAO.prepareMessageByKey("user.addoperatoruser.updatesuccessmessage", mapParam.get("UserName"));
					Validator.messageCompare(actualMessage, expectedMessage);
					
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  modifyOptPage1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("user.addoperatoruserview.label.category"));
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
			
				//Operator User Approval
				
			case 17://To verify that operator is unable to approve Operator User if loginId or category is not selected.
				try{
					if(APPLEVEL.equals("1")){
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to modify if category is not selected.");
					currentNode.assignCategory("SIT");
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					mapParam.put("LOGINID", "");
					String type = "approve";
					optresultMap=OperatorUserLogic.approveUser_SIT(ParentUser, mapParam, type);
					currentNode=test.createNode("To verify that valid message is displayed after approval of "+LoginUser+" .");
					currentNode.assignCategory("SIT");
					String intOptApproveMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.approveuccessmessage", optresultMap.get("UserName"));
					Validator.messageCompare(optresultMap.get("approveMsg"),intOptApproveMsg);
					}else{
						Log.info("Approval is not required.");
					}
					
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  approveOperatorUser.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.approvaloperatoruser.loginidorcategoryrequired");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 18://To verify that operator is unable to reject Operator User.
				try{
					if(APPLEVEL.equals("1")){
					currentNode=test.createNode("To verify that valid message is displayed after rejection of "+LoginUser+" .");
					currentNode.assignCategory("SIT");
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					String type = "Reject";
					optresultMap=OperatorUserLogic.approveUser_SIT(ParentUser, mapParam, type);
					String intOptApproveMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.rejectsuccessmessage", mapParam.get("UserName"));
					Validator.messageCompare(optresultMap.get("approveMsg"),intOptApproveMsg);
					}else{
						Log.info("Approval is not required.");
					}
					
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				}
				break;
				
				// Delete Operator User
				
			case 19://To verify that operator is able to Delete Operator User.
				try{
					currentNode=test.createNode("To verify that valid message is displayed after Deletion of "+LoginUser+" .");
					currentNode.assignCategory("SIT");
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					String type = "Delete";
					String actualMessage = OperatorUserLogic.modifyOperatorDetails_SIT(ParentUser, LoginUser, mapParam, type);
					String expectedMessage = MessagesDAO.prepareMessageByKey("user.addoperatoruser.deletesuccessmessage", mapParam.get("UserName"));
					Validator.messageCompare(actualMessage, expectedMessage);
					}
				catch(Exception e){
					Log.writeStackTrace(e);
				}
				break;
				
			//View Operator User
				
			case 20://To verify that operator is unable to view Operator User if category is not selected.
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to view if category is not selected.");
					currentNode.assignCategory("SIT");
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					OperatorUserLogic.viewOperatorUser_SIT(ParentUser, "", mapParam);
					
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  viewOptUserPage1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.selectoperatoruserforview.error.categorycode.required");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
			
			case 21://To verify that operator is unable to view Operator User if UserName is blank.
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to view " + LoginUser+ " if UserName is blank.");
					currentNode.assignCategory("SIT");
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					OperatorUserLogic.viewOperatorUser_SIT(ParentUser, LoginUser, mapParam);
					
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  viewOptUserPage1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.selectoperatoruserforview.error.username.required");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 22://To verify that operator is unable to add Operator User if loginID is duplicate.
				try{
					currentNode=test.createNode("To verify that " + ParentUser + " is unable to add " + LoginUser+ " if loginID is duplicate.");
					currentNode.assignCategory("SIT");
					optresultMap=OperatorUserLogic.operatorUserInitiate(ParentUser, LoginUser);
					String loginID = optresultMap.get("LOGINID");
					mapParam.put("LOGINID", loginID);
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					
					
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.error.loginallreadyexist");
				Validator.messageCompare(actualMsg, expectedMsg);
				}
				break;
				
			case 23://To verify that Deleted Operator login id can be re-assigned to any other user.
				try{
					currentNode=test.createNode("To verify that Deleted Operator login id can be re-assigned to any other user.");
					currentNode.assignCategory("SIT");
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					String type = "Delete";
					OperatorUserLogic.modifyOperatorDetails_SIT(ParentUser, LoginUser, mapParam, type);
					mapParam.put("firstName", "NewOperator");
					mapParam.put("lastName", "User");
					mapParam.put("UserName", mapParam.get("firstName") + " " + mapParam.get("lastName"));
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					String actualMsg = optresultMap.get("initiateMsg");
					String expectedMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.addsuccessmessageforrequest", optresultMap.get("UserName"));
					Validator.messageCompare(actualMsg, expectedMsg);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				}
				break;
				
			default: Log.info("No valid data found."); 
			}
			
			
			
	}
}
