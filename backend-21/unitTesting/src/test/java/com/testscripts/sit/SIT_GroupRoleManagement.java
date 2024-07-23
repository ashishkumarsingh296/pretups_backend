package com.testscripts.sit;

import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.GroupRoleManagement;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name=Module.SIT_GROUP_ROLE_MANAGEMENT)
public class SIT_GroupRoleManagement extends BaseTest{
	
	static boolean TestCaseCounter = false;
	HashMap<String, String> channelMap=new HashMap<>();
	String groupRoleName;
	String newName;
	


	//Data Provider
	@DataProvider(name = "categoryData")
	public Object[][] TestDataFeed() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
		Object[][] categoryData = new Object[1][5];
		for (int i = 1, j = 0; i <= 1; i++) {
			if(DBHandler.AccessHandler.webInterface(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i)).equals("Y"))
			   {categoryData[j][0] = i;
				categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
				categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				categoryData[j][4] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i);
				}
		}

		return categoryData;
	}
	

		@Test(dataProvider = "categoryData")
		@TestManager(TestKey = "PRETUPS-1000")
		public void a_GroupRoleCreation(int rowNum, String domainName, String categoryName,String ParentCategory, String geoType) throws InterruptedException {

			final String methodName="";Log.startTestCase(methodName);

			CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITGRPROLE1");
			
			

			GroupRoleManagement GroupRoleManagement = new GroupRoleManagement(driver);

			currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), categoryName, domainName));
			//currentNode=test.createNode("To verify that Super Admin is able to perform Group Role Creation for " + categoryName + " category");
			currentNode.assignCategory("SIT");
		    String result[] = GroupRoleManagement.addGroupRole(domainName, categoryName);
			
			groupRoleName= result[0];

			System.out.println("The Created Group Role  is:" +groupRoleName);

			String ExpectedMessage = MessagesDAO.prepareMessageByKey("roles.addgrouprole.message.successaddmessage");
			
            Assertion.assertEquals(result[1], ExpectedMessage);
			Assertion.completeAssertions();Log.endTestCase(methodName);
		}
		
		
		@Test(dataProvider = "categoryData")
		@TestManager(TestKey = "PRETUPS-1001")
		public void b_GroupRolemodify(int rowNum, String domainName, String categoryName, String ParentCategory, String geoType) throws InterruptedException {

			final String methodName="b_GroupRolemodify";Log.startTestCase(methodName);

            CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITGRPROLE2");
		
			GroupRoleManagement GroupRoleManagement = new GroupRoleManagement(driver);

			currentNode=test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), categoryName, domainName));
			//currentNode=test.createNode("To verify that Super Admin is able to perform Group Role modification for " + categoryName + " category");
			currentNode.assignCategory("SIT");
		    String result[] = GroupRoleManagement.modifyGroupRole(domainName, categoryName,groupRoleName);
		    //groupRoleName= result[0];

			System.out.println("The modified Group Role Name is:" +groupRoleName);

			String ExpectedMessage = MessagesDAO.prepareMessageByKey("roles.addgrouprole.message.successeditmessage");
			 Assertion.assertEquals(result[1], ExpectedMessage);

			Assertion.completeAssertions();Log.endTestCase(methodName);
		}
		
		
		@Test(dataProvider = "categoryData")
		@TestManager(TestKey = "PRETUPS-1002")
		public void c_GroupRoledeletion(int rowNum, String domainName, String categoryName,String ParentCategory, String geoType) throws InterruptedException {

			final String methodName="c_GroupRoledeletion";Log.startTestCase(methodName);

            CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITGRPROLE3");
			
			
			GroupRoleManagement GroupRoleManagement = new GroupRoleManagement(driver);

			currentNode=test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), categoryName, domainName));
			//currentNode=test.createNode("To verify that Super Admin is able to perform Group Role deletion for " + categoryName + " category and Role Name :" +groupRoleName);
			currentNode.assignCategory("SIT");
		    String result[] = GroupRoleManagement.deleteGroupRole(domainName, categoryName,groupRoleName);
		    //groupRoleName= result[0];

			System.out.println("The deleted Group Role Name is:" +groupRoleName);

			String ExpectedMessage = MessagesDAO.prepareMessageByKey("roles.addgrouprole.message.successdeletemessage");
			 Assertion.assertEquals(result[1], ExpectedMessage);

			Assertion.completeAssertions();Log.endTestCase(methodName);
		}
		
		
		
		@Test(dataProvider = "categoryData")
		@TestManager(TestKey = "PRETUPS-1003")
		public void d_GroupRoleCreationwithSpecificRoles (int rowNum, String domainName, String categoryName,String ParentCategory, String geoType) throws InterruptedException {

			final String methodName="d_GroupRoleCreationwithSpecificRoles";Log.startTestCase(methodName);

            CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITGRPROLE4");
            CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SITGRPROLE5");
            CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SITGRPROLE6");
            CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("SITGRPROLE7");
            
			

			GroupRoleManagement GroupRoleManagement = new GroupRoleManagement(driver);

			ChannelUser channelUser= new ChannelUser(driver);
			
			currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), categoryName, domainName));
			//currentNode=test.createNode("To verify that Super Admin is able to perform Group Role Creation with specific Roles for " + categoryName + " category");
			currentNode.assignCategory("SIT");
			
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			String categoryCode=ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, rowNum);
			boolean RoleExist = ExcelUtility.isRoleExists(RolesI.C2SRECHARGE);
			
			int dataLength = CONSTANT.USERACCESSDAO.length;
			boolean counter = false;
			String grpRole=DBHandler.AccessHandler.getSystemPreference(CONSTANT.GROUP_ROLE_ALLOWED);
			String sysRole=DBHandler.AccessHandler.getSystemPreference(CONSTANT.SYSTEM_ROLE_ALLOWED);
			String roleTypeDisp=DBHandler.AccessHandler.getSystemPreference(CONSTANT.CHANNEL_USER_ROLE_TYPE_DISPLAY);
			
			if (!RoleExist){
				Assertion.assertSkip("Customer Recharge through Web is not available");
				
			}
			
			else{
				for (int i = 0; i < dataLength; i++) {
					if (CONSTANT.USERACCESSDAO[i][3].equals(RolesI.C2SRECHARGE) && CONSTANT.USERACCESSDAO[i][4].equals(categoryCode))
					{
						if(grpRole.equalsIgnoreCase("Y")&&sysRole.equalsIgnoreCase("N")&&roleTypeDisp.equalsIgnoreCase("GROUP"))
						{
							if(!CONSTANT.USERACCESSDAO[i][5].equals(PretupsI.YES)){
								Assertion.assertSkip("C2SRECHARGE Role code is not allowed.");return;
							}
						}
						counter=true;
						break;
					}
				}
		    			
				if(counter){		
					String result[] = GroupRoleManagement.addGroupRolewithC2S(domainName, categoryName);
					groupRoleName= result[0];

					System.out.println("The Created Group Role  is:" +groupRoleName);
					String ExpectedMessage = MessagesDAO.prepareMessageByKey("roles.addgrouprole.message.successaddmessage");
					Assertion.assertEquals(result[1], ExpectedMessage);

					ExtentI.Markup(ExtentColor.TEAL, "Creating Channel User with the above role "+groupRoleName);
					channelMap=channelUser.channelUserInitiateWithGroupRole(rowNum, domainName, ParentCategory, categoryName, geoType,groupRoleName);
					String actual1 = channelMap.get("channelInitiateMsg");

					String APPLEVEL = DBHandler.AccessHandler.getSystemPreference(UserAccess.userapplevelpreference());
					String expected1 = null;
					if(APPLEVEL.equals("0"))
					{	
						expected1 = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessage", channelMap.get("uName"));	
					}else{
						expected1 = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessageforrequest", channelMap.get("uName"));
					}

					Assertion.assertEquals(actual1, expected1);
					


					if(APPLEVEL.equals("2"))
					{channelUser.approveLevel1_ChannelUser();
					channelUser.approveLevel2_ChannelUser();
					String actual2 = channelMap.get("channelApprovelevel2Msg");
					String expected2 = MessagesDAO.prepareMessageByKey("user.addchanneluser.level2approvemessage",channelMap.get("uName"));
				
					Assertion.assertEquals(actual2, expected2);
					}
					else if(APPLEVEL.equals("1")){
						channelUser.approveLevel1_ChannelUser();
						String actual2 = channelMap.get("channelApproveMsg");
						String expected2 = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval",channelMap.get("uName"));
						Assertion.assertEquals(actual2, expected2);
					}else{
						Log.info("Approval not required.");	
					}
					HashMap<String, String> dataMap =  channelUser.changeUserFirstTimePassword();


					String MSISDN =channelMap.get("MSISDN");

					Log.info("Newly created User Name:" +channelMap.get("UserName") );

					String LoginId = channelMap.get("LOGIN_ID");
					Log.info("Login Id is: " + LoginId);

					String Pwd = dataMap.get("PASSWORD");

					currentNode=test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(),groupRoleName)) ;
					//currentNode=test.createNode("To verify that Channel User homepage will display the roles included in the associated Group Role "+groupRoleName+ "only");
					currentNode.assignCategory("SIT");

					String actual = GroupRoleManagement.loginWithUserHavingSpecificGroupRole(LoginId, Pwd);
					String expected = "Only the Links included in the associated GroupRole are displayed on Channel user homepage";
					
					Assertion.assertEquals(actual, expected);
					currentNode=test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(),groupRoleName)) ;
					//currentNode = test.createNode("To verify that Channel Admin is able to dis-associate the Group role " +groupRoleName+" with User");
					currentNode.assignCategory("SIT");


					String actualMessage = channelUser.modifyChannelUserAssignedRoleDetails(MSISDN);
					String expectedMessage = MessagesDAO.prepareMessageByKey("user.addchanneluser.updatesuccessmessage", channelMap.get("uName"));
					 Assertion.assertEquals(actualMessage, expectedMessage);

					

					currentNode=test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(),groupRoleName)) ;
					//currentNode = test.createNode("Verify that Superadmin can delete the Group role " +groupRoleName+" created with few selected roles");
					currentNode.assignCategory("SIT");
					String data[] = GroupRoleManagement.deleteGroupRole(domainName, categoryName,groupRoleName);

					String DeletionMessage = MessagesDAO.prepareMessageByKey("roles.addgrouprole.message.successdeletemessage");
					
                    Assertion.assertEquals(data[1], DeletionMessage);
					
				}else{
					Assertion.assertSkip("C2S Recharge role is not allowed.");
				}
				Assertion.completeAssertions();
				Log.endTestCase(methodName);

			}
		}
		

		
		
		
		
		@Test(dataProvider = "categoryData")
		@TestManager(TestKey = "PRETUPS-1004")
		public void e_SuspendedGroupRoleValidation(int rowNum, String domainName, String categoryName,String ParentCategory, String geoType) throws InterruptedException {

			final String methodName="e_SuspendedGroupRoleValidation";Log.startTestCase(methodName);

            CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITGRPROLE8");
			

			GroupRoleManagement GroupRoleManagement = new GroupRoleManagement(driver);
			ChannelUser channelUser= new ChannelUser(driver);
			
			currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),categoryName,domainName )) ;
			//currentNode=test.createNode("To verify that a suspended Group Role is not available for association while creating channel User  for " + categoryName + " category");
			currentNode.assignCategory("SIT");
			
			boolean RoleExist = ExcelUtility.isRoleExists(RolesI.C2SRECHARGE);

			if (!RoleExist){
				Assertion.assertSkip("Customer Recharge through Web is not available");
				
			}
			else{
		    
			String result[] = GroupRoleManagement.addGroupRole(domainName, categoryName);
			groupRoleName= result[0];

			System.out.println("The Created Group Role  is:" +groupRoleName);
			String ExpectedMessage = MessagesDAO.prepareMessageByKey("roles.addgrouprole.message.successaddmessage");
		
			 Assertion.assertEquals(result[1], ExpectedMessage);
			ExtentI.Markup(ExtentColor.TEAL, "Suspending the above role "+groupRoleName);
			
			String result1[] = GroupRoleManagement.suspendGroupRole(domainName, categoryName ,groupRoleName);
			
			String ExpectedMessage0 = MessagesDAO.prepareMessageByKey("roles.addgrouprole.message.successeditmessage");
			Validator.messageCompare(result1[1], ExpectedMessage0);
			
			ExtentI.Markup(ExtentColor.TEAL, "Creating Channel User with the above role "+groupRoleName);
			channelMap=channelUser.channelUserInitiateWithGroupRole(rowNum, domainName, ParentCategory, categoryName, geoType,groupRoleName);
			
			String actual1 = channelMap.get("channelInitiateMsg");
			
			String expected1 = "As Group Role is suspended, It is not available to associate with Channel User";
			
			
						Assertion.assertEquals(actual1, expected1)	;
						Assertion.completeAssertions();Log.endTestCase(methodName);
			
			}
		}
		

}
