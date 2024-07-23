package com.testscripts.sit;

import java.io.IOException;
import java.text.MessageFormat;
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
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.superadminpages.addoperatoruser.AddOperatorUserPage;
import com.pageobjects.superadminpages.addoperatoruser.ApproveOperatorUsersPage;
import com.pageobjects.superadminpages.addoperatoruser.ModifyOperatorUserPage1;
import com.pageobjects.superadminpages.addoperatoruser.ViewOperatorUserPage1;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_OPERATOR_USER_CREATION)
public class SIT_OperatorUserCreation extends BaseTest {
			NetworkAdminHomePage networkAdminHomePage;
			Map<String,String> dataMap;
			static int minPaswdLength;
			static int maxPaswdLength;
			
			
	@DataProvider(name = "Domain&CategoryProvider_validations")
	public Object[][] DomainCategoryProvider_validations() {
			
		OperatorUserCreationMap operatorUserCreationMap = new OperatorUserCreationMap();
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		String parentName = ExcelUtility.getCellData(0,ExcelI.PARENT_NAME, 2);
		String categoryName = ExcelUtility.getCellData(0,ExcelI.CATEGORY_NAME, 2);
		minPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MIN_LOGIN_PWD_LENGTH"));
		 maxPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MAX_LOGIN_PWD_LENGTH"));
		 String password = CommonUtils.generatePassword(minPaswdLength-1)+"@";
		 
		 String[] description=new String[24];
		 description[0]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION1").getExtentCase(),parentName,categoryName);
		 description[1]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION2").getExtentCase(),parentName,categoryName);
		 description[2]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION3").getExtentCase(),parentName,categoryName);
		 description[3]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION4").getExtentCase(),parentName,categoryName);
		 description[4]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION5").getExtentCase(),parentName,categoryName);
		 description[5]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION6").getExtentCase(),parentName,categoryName);
		 description[6]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION7").getExtentCase(),parentName,categoryName);
		 description[7]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION8").getExtentCase(),parentName,categoryName);
		 description[8]=""+MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION9").getExtentCase(), parentName,categoryName,minPaswdLength);
		 description[9]=""+MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION10").getExtentCase(), parentName,categoryName,minPaswdLength);
		 description[10]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION11").getExtentCase(),parentName,categoryName);
		 description[11]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION12").getExtentCase(),parentName,categoryName);
		 description[12]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION13").getExtentCase(),parentName,categoryName);
		 description[13]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION14").getExtentCase(),parentName,categoryName);
		 description[14]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION15").getExtentCase(),parentName,categoryName);
		 description[15]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION16").getExtentCase(),parentName,categoryName);
		 description[16]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION17").getExtentCase(),parentName);
		 description[17]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION18").getExtentCase(),parentName);
		 description[18]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION20").getExtentCase(),categoryName);
		 description[19]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION21").getExtentCase(),categoryName);
		 description[20]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION22").getExtentCase(),parentName);
		 description[21]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION23").getExtentCase(),parentName,categoryName);
		 description[22]=MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION24").getExtentCase(),parentName,categoryName);
		 description[23]=_masterVO.getCaseMasterByID("SITOPTCREATION25").getExtentCase();

		 
		 return new Object[][]{{0,description[0], operatorUserCreationMap.getOperatorUserMap("firstName",""), parentName, categoryName},
				 {1,description[1], operatorUserCreationMap.getOperatorUserMap("subscriberCode", " "), parentName, categoryName},
				 {2,description[2], operatorUserCreationMap.getOperatorUserMap("DIVISION", "N"), parentName, categoryName},
				 {3,description[3], operatorUserCreationMap.getOperatorUserMap("LOGINID", ""), parentName, categoryName},
				 {4,description[4],operatorUserCreationMap.getOperatorUserMap("PASSWORD", ""), parentName, categoryName},
				 {5,description[5], operatorUserCreationMap.getOperatorUserMap("CONFIRMPASSWORD",""), parentName, categoryName},
				 {6,description[6], operatorUserCreationMap.getOperatorUserMap("CONFIRMPASSWORD",password), parentName, categoryName},
				 {7,description[7],operatorUserCreationMap.getOperatorUserMap("PASSWORD", CommonUtils.generateSequential("number", minPaswdLength)), parentName, categoryName},
				 {8,description[8],operatorUserCreationMap.getOperatorUserMap("PASSWORD", "3"), parentName, categoryName},
				 {9,description[9],operatorUserCreationMap.getOperatorUserMap("CONFIRMPASSWORD", "3"), parentName, categoryName},
				 {10,description[10],operatorUserCreationMap.getOperatorUserMap("AssignNetwork", "N"), parentName, categoryName},
				 {11,description[11],operatorUserCreationMap.getOperatorUserMap("AssignGeography", "N"), parentName, categoryName},
				 {12,description[12],operatorUserCreationMap.getOperatorUserMap("AssignPhoneNumber", "N"), parentName, categoryName},
				 {13,description[13],operatorUserCreationMap.getOperatorUserMap("AssignProduct", "N"), parentName, categoryName},
				 {14,description[14],operatorUserCreationMap.getOperatorUserMap("AssignDomain", "N"), parentName, categoryName},
				 {15,description[15],operatorUserCreationMap.defaultMap(), parentName, categoryName},
				 {16,description[16],operatorUserCreationMap.defaultMap(), parentName, categoryName},
				 {17,description[17],operatorUserCreationMap.defaultMap(), parentName, categoryName},
				 {18,description[18],operatorUserCreationMap.defaultMap(), parentName, categoryName},
				 {19,description[19],operatorUserCreationMap.defaultMap(), parentName, categoryName},
				 {20,description[20],operatorUserCreationMap.defaultMap(), parentName, categoryName},
				 {21,description[21],operatorUserCreationMap.getOperatorUserMap("UserName", ""), parentName, categoryName},
				 {22,description[22],operatorUserCreationMap.defaultMap(), parentName, categoryName},
				 {23,description[23],operatorUserCreationMap.defaultMap(), parentName, categoryName},
		 };

	}
	
	@Test(dataProvider = "Domain&CategoryProvider_validations")
	@TestManager(TestKey = "PRETUPS-922") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void testCycleSIT(int CaseNum,String Description, HashMap<String, String> mapParam, String ParentUser, String LoginUser) throws IOException{
		final String methodName = "Test_Operator_User_Creation";
		Log.startTestCase(methodName);
			
			AddOperatorUserPage addOptrUserPage = new AddOperatorUserPage(driver);
			ModifyOperatorUserPage1 modifyOptPage1  = new ModifyOperatorUserPage1(driver);;
			ApproveOperatorUsersPage approveOperatorUser  = new ApproveOperatorUsersPage(driver);
			ViewOperatorUserPage1 viewOptUserPage1 = new ViewOperatorUserPage1(driver);
			
			OperatorUser OperatorUserLogic = new OperatorUser(driver);
			String APPLEVEL = DBHandler.AccessHandler.getSystemPreference("OPT_USR_APRL_LEVEL");
			HashMap<String, String> optresultMap = null;
			currentNode=test.createNode(Description);
			currentNode.assignCategory("SIT");
			
			switch(CaseNum){
			//Add Operator User
			
			case 0://To verify that operator is unable to add Operator User if First Name is not selected
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("user.addchanneluser.label.firstName"));
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 1://To verify that operator is unable to add Operator User if Subscriber Code is left Blank
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("user.addchanneluser.label.empcode"));
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 2://To verify that operator is unable to add Operator User if Division is not selected
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("user.addoperatoruser.label.division"));
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 3://To verify that operator is unable to add Operator User if LoginID is left Blank
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.adduser.error.loginidrequired");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 4://To verify that operator is unable to add Operator User if Password is left Blank
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.adduser.error.webpasswordrequired");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 5://To verify that operator is unable to add Operator User if Confirm Password is left Blank
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.adduser.error.confirmpasswordrequired");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 6://To verify that operator is unable to add Operator User if Confirm Password is different from Password entered.
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.adduser.error.confirmpasswordnotvalid");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;

			case 7://To verify that operator is unable to add Operator User if Password contains sequential characters
				try{
					mapParam.put("CONFIRMPASSWORD", mapParam.get("PASSWORD"));// CONFIRMPASSWORD need to be same as PASSWORD for this case
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  new AddChannelUserDetailsPage(driver).getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("operatorutil.validatepassword.error.passwordconsecutive");
				Assertion.assertContainsEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 8://To verify that operator is unable to add Operator User if Password's length is less than minimum characters
				try{
					mapParam.put("CONFIRMPASSWORD",mapParam.get("PASSWORD"));
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("operatorutil.validatepassword.error.passwordlenerr",String.valueOf(minPaswdLength),String.valueOf(maxPaswdLength));
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 9://To verify that operator is unable to add Operator User if Confirm Password's length is less than minimum characters
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("viewedituser.editoperatoruser.error.invalidconfirmpasswordlength",String.valueOf(minPaswdLength));
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 10://To verify that operator is unable to add Operator User if Network is not assigned
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.adduser.error.networknotassigned");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 11://To verify that operator is unable to add Operator User if Geography is not assigned
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.adduser.error.geographiesnotassigned");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 12://To verify that operator is unable to add Operator User if Phone Number is not assigned
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("routing.mnp.upload.msisdnrequired");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 13://To verify that operator is unable to add Operator User if Product is not assigned
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.error.productrequired");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 14://To verify that operator is unable to add Operator User if Domain is not assigned
				try{
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.error.domainrequired");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
			
			//Modify Operator User
				
			case 15://To verify that operator is unable to modify Operator User if UserName is blank.
				try{
					String type = "Modify";
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					mapParam.put("UserName", "");
					String actualMessage = OperatorUserLogic.modifyOperatorDetails_SIT(ParentUser, LoginUser, mapParam, type);
					String expectedMessage = MessagesDAO.prepareMessageByKey("user.addoperatoruser.updatesuccessmessage", mapParam.get("UserName"));
					Assertion.assertEquals(actualMessage, expectedMessage);	
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  modifyOptPage1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.selectcategoryforedit.error.usermorethanone");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 16://To verify that operator is unable to modify Operator User if category is not selected.
				try{
					String type = "Modify";
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					String actualMessage = OperatorUserLogic.modifyOperatorDetails_SIT(ParentUser, "", mapParam, type);
					String expectedMessage = MessagesDAO.prepareMessageByKey("user.addoperatoruser.updatesuccessmessage", mapParam.get("UserName"));
					Assertion.assertEquals(actualMessage, expectedMessage);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  modifyOptPage1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("user.addoperatoruserview.label.category"));
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
			
				//Operator User Approval
				
			case 17://To verify that operator is unable to approve Operator User if loginId is not selected.
				try{
					if(APPLEVEL.equals("1")){
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					mapParam.put("LOGINID", "");
					String type = "approve";
					optresultMap=OperatorUserLogic.approveUser_SIT(ParentUser, mapParam, type);
					currentNode=test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITOPTCREATION19").getExtentCase(), LoginUser));
					currentNode.assignCategory("SIT");
					String intOptApproveMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.approveuccessmessage", optresultMap.get("UserName"));
					Assertion.assertEquals(optresultMap.get("approveMsg"), intOptApproveMsg);
					}else{
						Log.info("Approval is not required.");
					}
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  approveOperatorUser.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.approvaloperatoruser.loginidorcategoryrequired");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 18://To verify that operator is unable to reject Operator User.
				try{
					if(APPLEVEL.equals("1")){
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					String type = "Reject";
					optresultMap=OperatorUserLogic.approveUser_SIT(ParentUser, mapParam, type);
					String intOptApproveMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.rejectsuccessmessage", mapParam.get("UserName"));
					Assertion.assertEquals(optresultMap.get("approveMsg"), intOptApproveMsg);
					}else{
						Log.info("Approval is not required.");
					}
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				}
				Assertion.completeAssertions();
				break;
				
				// Delete Operator User
				
			case 19://To verify that operator is able to Delete Operator User.
				try{
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					String type = "Delete";
					String actualMessage = OperatorUserLogic.modifyOperatorDetails_SIT(ParentUser, LoginUser, mapParam, type);
					String expectedMessage = MessagesDAO.prepareMessageByKey("user.addoperatoruser.deletesuccessmessage", mapParam.get("UserName"));
					Assertion.assertEquals(actualMessage, expectedMessage);
					}
				catch(Exception e){
					Log.writeStackTrace(e);
				}
				Assertion.completeAssertions();
				break;
				
			//View Operator User
				
			case 20://To verify that operator is unable to view Operator User if category is not selected.
				try{
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					OperatorUserLogic.viewOperatorUser_SIT(ParentUser, "", mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  viewOptUserPage1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.selectoperatoruserforview.error.categorycode.required");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
			
			case 21://To verify that operator is unable to view Operator User if UserName is blank.
				try{
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					OperatorUserLogic.viewOperatorUser_SIT(ParentUser, LoginUser, mapParam);
					
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  viewOptUserPage1.getActualMsg();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.selectoperatoruserforview.error.username.required");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 22://To verify that operator is unable to add Operator User if loginID is duplicate.
				try{
					optresultMap=OperatorUserLogic.operatorUserInitiate(ParentUser, LoginUser);
					String loginID = optresultMap.get("LOGINID");
					mapParam.put("LOGINID", loginID);
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				String actualMsg =  addOptrUserPage.getActualMessage();
				String expectedMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.error.loginallreadyexist");
				Assertion.assertEquals(actualMsg, expectedMsg);
				}
				Assertion.completeAssertions();
				break;
				
			case 23://To verify that Deleted Operator login id can be re-assigned to any other user.
				try{
					OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					String type = "Delete";
					OperatorUserLogic.modifyOperatorDetails_SIT(ParentUser, LoginUser, mapParam, type);
					mapParam.put("firstName", "AUTFN"+new RandomGeneration().randomNumeric(4));
					mapParam.put("lastName", "AUTLN"+new RandomGeneration().randomNumeric(4));
					mapParam.put("UserName", mapParam.get("firstName") + " " + mapParam.get("lastName"));
					optresultMap = OperatorUserLogic.operatorUserInitiate_SIT(ParentUser, LoginUser, mapParam);
					String actualMsg = optresultMap.get("initiateMsg");
					String expectedMsg = null;
					if(APPLEVEL.equals("1")){
					expectedMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.addsuccessmessageforrequest", optresultMap.get("UserName"));}
					else{expectedMsg = MessagesDAO.prepareMessageByKey("user.addoperatoruser.addsuccessmessage", optresultMap.get("UserName"));}
					Assertion.assertEquals(actualMsg, expectedMsg);
				}
				catch(Exception e){
					Log.writeStackTrace(e);
				}
				Assertion.completeAssertions();
				break;
				
			default: Log.info("No valid data found."); 
			}
			
			
			Log.endTestCase(methodName);
	}
}
