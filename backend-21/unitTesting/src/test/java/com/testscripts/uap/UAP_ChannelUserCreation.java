package com.testscripts.uap;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.Enquiries.ViewChannelUserEnquiry;
import com.Features.Enquiries.ViewSelfDetailsEnquiry;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

/**
 * @author Lokesh
 * This class is created to add Channel Users and perform all approval levels. Change first time user password and pin. 
 */
@ModuleManager(name =Module.UAP_CHANNEL_USER_CREATION)
public class UAP_ChannelUserCreation extends BaseTest {
	String LoginID;
	String MSISDN;
	String PASSWORD;
	String EXTCODE;
	String CONFIRMPASSWORD;
	String NEWPASSWORD;
	String UserName;
	String UserName1;
	//static WebDriver driver;
	static String homepage1;	
	static HashMap<String, String> map, map1 = null;
	HashMap<String, String> channelresultMap;
	static boolean TestCaseCounter = false;
	HashMap<String, String> userAccessMap;
	
	
	@Test(dataProvider="Domain&CategoryProvider_positive")
	@TestManager(TestKey = "PRETUPS-408") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_channelUseraddition(int RowNum, String Domain, String Parent, String Category, String geotype) throws InterruptedException, IOException{
		final String methodName = "Test_channelUseraddition";
	    Log.startTestCase(methodName);
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PCHNLCREATION1");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SCHNLCREATION1");
		CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PCHNLCREATION2");
		CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("SCHNLCREATION2");
		CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("PCHNLCREATION3");
		CaseMaster CaseMaster6 = _masterVO.getCaseMasterByID("SCHNLCREATION3");
		CaseMaster CaseMaster7 = _masterVO.getCaseMasterByID("PCHNLCREATION4");
		CaseMaster CaseMaster8 = _masterVO.getCaseMasterByID("SCHNLCREATION4");
		CaseMaster CaseMaster9 = _masterVO.getCaseMasterByID("PCHNLCREATION5");
		CaseMaster CaseMaster10 = _masterVO.getCaseMasterByID("PCHNLCREATION6");
		CaseMaster CaseMaster11 = _masterVO.getCaseMasterByID("SCHNLCREATION5");
		CaseMaster CaseMaster12 = _masterVO.getCaseMasterByID("UCHNLCREATION1");
		CaseMaster CaseMaster13 = _masterVO.getCaseMasterByID("UCHNLCREATION2");
		CaseMaster CaseMaster14 = _masterVO.getCaseMasterByID("UCHNLCREATION3");
	
		ChannelUser channelUserLogic= new ChannelUser(driver);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(Category);
		/*
		 * Test Case: Channel User Initiation
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), Category,Parent));//"To verify that operator user is able to initiate " + Category+" category Channel user via parent Category "+Parent+" .");
		currentNode.assignCategory("UAP");
		channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype);
		
		/*
		 * Test Case: Message Validation after add channel user.
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), Category));//"To verify that valid message is displayed after initiating "+Category+" category channel user.");
		currentNode.assignCategory("UAP");
		
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		
		String intChnlInitiateMsg;
		if(APPLEVEL.equals("0"))
		{	
			intChnlInitiateMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessage", channelresultMap.get("uName"));	
		}else{
			intChnlInitiateMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessageforrequest", channelresultMap.get("uName"));
		}
		Assertion.assertEquals(channelresultMap.get("channelInitiateMsg"), intChnlInitiateMsg);
		
		/*
		 * Approval levels
		 */
		if(APPLEVEL.equals("2"))
		{
		//Approval level 1
		currentNode=test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), Category));//"To verify that operator user is able to approve level 1 " + Category+" category Channel user.");
		currentNode.assignCategory("UAP");
		channelresultMap=channelUserLogic.approveLevel1_ChannelUser();
		
		//Approval level 1 message validation
		currentNode=test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), Category));//"To verify that valid message is displayed after approval level 1 of "+Category+" category channel user.");
		currentNode.assignCategory("UAP");
		String intChnlApprove1Msg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagerequiredleveltwoapproval", channelresultMap.get("uName"));
		Assertion.assertEquals(channelresultMap.get("channelApprovelevel1Msg"), intChnlApprove1Msg);
		
		//Approval level 2
		currentNode=test.createNode(MessageFormat.format(CaseMaster5.getExtentCase(), Category));//"To verify that Operator user is able to approve level 2 " + Category+" category Channel user.");
		currentNode.assignCategory("UAP");
		channelresultMap=channelUserLogic.approveLevel2_ChannelUser();
		
		//Approval level 2 message validation
		currentNode=test.createNode(MessageFormat.format(CaseMaster6.getExtentCase(), Category));//"To verify that valid message is displayed after approval level 2 of "+Category+" category channel user.");
		currentNode.assignCategory("UAP");
		String intChnlApprove2Msg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", channelresultMap.get("uName"));
		Assertion.assertEquals(channelresultMap.get("channelApprovelevel2Msg"), intChnlApprove2Msg);
		}
		else if(APPLEVEL.equals("1")){
		//Approval level 1
			currentNode=test.createNode(MessageFormat.format(CaseMaster7.getExtentCase(), Category));//"To verify that Operator user is able to approve " + Category+" category Channel user.");
			currentNode.assignCategory("UAP");
			channelresultMap=channelUserLogic.approveLevel1_ChannelUser();
		
		//Approval level 1 message validation
			currentNode=test.createNode(MessageFormat.format(CaseMaster8.getExtentCase(), Category));//"To verify that valid message is displayed after approval of "+Category+" category channel user.");
			currentNode.assignCategory("UAP");
			String intChnlApproveMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", channelresultMap.get("uName"));
			//assertEquals(channelresultMap.get("channelApprovelevel1Msg"), intChnlApproveMsg);
			Assertion.assertEquals(channelresultMap.get("channelApproveMsg"), intChnlApproveMsg);
		}else{
			Log.info("Approval not required.");	
		}
		
		//Change user password for first time login.
		if(webAccessAllowed.equalsIgnoreCase("Y")){
		currentNode=test.createNode(MessageFormat.format(CaseMaster9.getExtentCase(), Category));//"To verify that " + Category+" category Channel user is prompted for change password on first time login and successfuly change the password.");
		currentNode.assignCategory("UAP");
		channelUserLogic.changeUserFirstTimePassword();}
		
		//Change user PIN for first time.
		currentNode=test.createNode(MessageFormat.format(CaseMaster10.getExtentCase(), Category));//"To verify that Operator user change the PIN of " + Category+" category Channel user for processing further transaction.");
		currentNode.assignCategory("UAP");
		channelresultMap=channelUserLogic.changeUserFirstTimePIN();
		
		//Message validation for user PIN change.
		currentNode=test.createNode(CaseMaster11.getExtentCase());//"To verify that valid message is displayed after PIN is changed.");
		currentNode.assignCategory("UAP");
		String intChnlChangePINMsg = MessagesDAO.prepareMessageByKey("user.changepin.msg.updatesuccess");
		Assertion.assertEquals(channelresultMap.get("changePINMsg"), intChnlChangePINMsg);
		currentNode=test.createNode(CaseMaster12.getExtentCase());//"To validate View Channel User Enquiry");
		currentNode.assignCategory("UAP");
		channelresultMap.put("Category", Category);
		ViewChannelUserEnquiry ChannelUserEnquiry = new ViewChannelUserEnquiry(driver);
		String EnquiryScreenShot = ChannelUserEnquiry.validateViewChannelUserEnquiry(channelresultMap);
		currentNode.addScreenCaptureFromPath(EnquiryScreenShot);
		
		currentNode=test.createNode(MessageFormat.format(CaseMaster13.getExtentCase(), Category));//"To validate Self Details Enquiry through "+ Category+" category user");
		currentNode.assignCategory("UAP");
		if(webAccessAllowed.equalsIgnoreCase("Y")){
			if(CommonUtils.roleCodeExistInLinkSheet(RolesI.VIEW_SELF_DETAILS, Category)){
				ViewSelfDetailsEnquiry ViewSelfDetailsEnquiry = new ViewSelfDetailsEnquiry(driver);
				String EnquiryScreenShot_ViewSelfDetails = ViewSelfDetailsEnquiry.validateSelfDetailsEnquiry(channelresultMap);
				currentNode.addScreenCaptureFromPath(EnquiryScreenShot_ViewSelfDetails);}
			else{
				Assertion.assertSkip("View Self Details link is not allowed to this user");
				currentNode.log(Status.SKIP, "View Self Details link is not allowed to this user");
				}
			}
		else{
			Assertion.assertSkip("Web access is not allowed to this category");
			currentNode.log(Status.SKIP, "Web access is not allowed to this category");
		}
				
		/*
		 * Test Case: Modify Operator user details
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster14.getExtentCase(), Category));//"To verify that operator user is able to modify details of " + Category+" channnel user.");
		currentNode.assignCategory("UAP");
		String actualMessage = channelUserLogic.modifyChannelUserDetails();
		String expectedMessage = MessagesDAO.prepareMessageByKey("user.addchanneluser.updatesuccessmessage", channelresultMap.get("uName"));
		Assertion.assertEquals(actualMessage, expectedMessage);
		Assertion.completeAssertions();	
		Log.endTestCase(methodName);
	}
	
	
	// Negative testing
	@Test(dataProvider = "Domain&CategoryProvider_validations")
	@TestManager(TestKey = "PRETUPS-409") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_channelUserCreation(int RowNum, String Domain, String Parent,
			String Category, String geotype, String description, String fName, String lName,
			String uName, String sName, String subscriberCode,
			String externalCode, String MSISDN, String selectOutletSubOutlet,
			String contactNo, String address1, String address2, String city,
			String state, String country, String emailID, String loginID,
			String assignGeography, String assignRoles, String assignServices,
			String assignProducts, String assgnPhoneNumber, String PASSWORD,
			String CONFIRMPASSWORD, String PIN) throws InterruptedException {
		
		HashMap<String, String> paraMeterMap = new HashMap<String, String>();
		paraMeterMap.put("description", description);
		paraMeterMap.put("fName", fName);
		paraMeterMap.put("lName",lName);
		paraMeterMap.put("uName",uName);
		paraMeterMap.put("sName",sName);
		paraMeterMap.put("subscriberCode", subscriberCode);
		paraMeterMap.put("EXTCODE", externalCode);
		paraMeterMap.put("MSISDN",MSISDN);
		paraMeterMap.put("selectOutletSubOutlet", selectOutletSubOutlet);
		paraMeterMap.put("contactNo",contactNo);
		paraMeterMap.put("address1",address1);
		paraMeterMap.put("address2",address2);
		paraMeterMap.put("city",city);
		paraMeterMap.put("state",state);
		paraMeterMap.put("country",country);
		paraMeterMap.put("emailID",emailID);
		paraMeterMap.put("LoginID", loginID);
		paraMeterMap.put("assignGeography",assignGeography);
		paraMeterMap.put("assignRoles",assignRoles);
		paraMeterMap.put("assignServices",assignServices);
		paraMeterMap.put("assignProducts", assignProducts);
		paraMeterMap.put("assgnPhoneNumber", assgnPhoneNumber);
		paraMeterMap.put("PASSWORD", PASSWORD);
		paraMeterMap.put("CONFIRMPASSWORD", CONFIRMPASSWORD);
		paraMeterMap.put("PIN", PIN);
		

		final String methodName = "Test_channelUserCreation";
        Log.startTestCase(methodName);
		Log.startTestCase(this.getClass().getName());
		
		ChannelUser channelUserLogic= new ChannelUser(driver);
		userAccessMap = (HashMap<String, String>) UserAccess.getUserWithAccess(RolesI.ADD_CHANNEL_USER_ROLECODE);
		
		/*
		 * Test Case Number 1: Channel User Initiation
		 */
		currentNode=test.createNode(paraMeterMap.get("description"));
		currentNode.assignCategory("UAP");
		try{
		channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,paraMeterMap);
		String actual = channelresultMap.get("channelInitiateMsg");
		String expected = MessagesDAO.prepareMessageByKey("user.addchanneluser.error.loginallreadyexist","");
		Assertion.assertEquals(actual, expected);
		}
		catch(Exception e){
			String actual = CONSTANT.CU_ASSIGNPHONENO_ERR;
			String expected = MessagesDAO.prepareMessageByKey("user.assignphone.error.msisdnallreadyexist", "");
			Log.info("Actual Message: "+actual);
			Assertion.assertContainsEquals(actual, expected);
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	//Rejection at approval level1 and approval level 2
	@Test(dataProvider="Domain&CategoryProvider_positive")
	@TestManager(TestKey = "PRETUPS-1159") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void rejectChannelUserApproval(int RowNum, String Domain, String Parent, String Category, String geotype) throws InterruptedException{
		final String methodName = "Test_rejectChannelUserApproval";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster15 = _masterVO.getCaseMasterByID("UCHNLCREATION4");
		CaseMaster CaseMaster16 = _masterVO.getCaseMasterByID("UCHNLCREATION5");
		
		ChannelUser channelUserLogic= new ChannelUser(driver);
		
		/*
		 * Test Case: Channel User Initiation and rejection at approval level 1
		 */
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		
		if(APPLEVEL.equals("2")||APPLEVEL.equals("1"))
		{
			currentNode=test.createNode(MessageFormat.format(CaseMaster15.getExtentCase(), Category,Parent));//"To verify that Channel Admin is able to initiate " + Category+" category Channel user via parent Category "+Parent+"  and reject at approval level 1.");
			currentNode.assignCategory("UAP");
			channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype);
			channelresultMap=channelUserLogic.rejection_approveLevel1ChannelUser();
			
			String intChnlApprove1Msg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1rejectmessage", channelresultMap.get("uName"));
				Assertion.assertEquals(channelresultMap.get("channelReject1Msg"), intChnlApprove1Msg);
				
		}
		
		/*
		 * Test Case: Channel User Initiation and rejection at approval level 2
		 */
		
		if(APPLEVEL.equals("2")){
			currentNode=test.createNode(MessageFormat.format(CaseMaster16.getExtentCase(), Category,Parent));//"To verify that Channel Admin is able to initiate " + Category+" category Channel user via parent Category "+Parent+"  and reject at approval level 2.");
			currentNode.assignCategory("UAP");
			channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype);
			channelresultMap=channelUserLogic.approveLevel1_ChannelUser();
			channelresultMap=channelUserLogic.rejection_approveLevel2ChannelUser();
			
			String intChnlApprove1Msg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level2rejectmessage", channelresultMap.get("uName"));
			Assertion.assertEquals(channelresultMap.get("channelReject2Msg"), intChnlApprove1Msg);
			
			
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@DataProvider(name = "Domain&CategoryProvider_positive")
	public Object[][] DomainCategoryProvider_positive() {
		
		_masterVO.loadGeoDomains();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[rowCount][5];
		for (int i = 1, j = 0; i <= rowCount; i++, j++) {
			categoryData[j][0] = i;
			categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			categoryData[j][4] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i);
		}
		return categoryData;
	}
	
	@DataProvider(name = "Domain&CategoryProvider_validations")
	public Object[][] DomainCategoryProvider_validations() {

		CaseMaster CaseMaster17 = _masterVO.getCaseMasterByID("UCHNLCREATION6");
		CaseMaster CaseMaster18 = _masterVO.getCaseMasterByID("UCHNLCREATION7");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowNum=1;
		RandomGeneration randStr = new RandomGeneration();
		
		String[] userDetailsHL = new String[5];
		
		userDetailsHL[0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		userDetailsHL[1] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, 1);
		userDetailsHL[2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		userDetailsHL[3] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, 1);	
		
		String description[]=new String[2];
		description[0]=CaseMaster17.getExtentCase();//"To verify that channel user addition is not successful if entered LoginID already exists.";
		description[1]=CaseMaster18.getExtentCase();//"To verify that channel user addition is not successful if entered MSISDN already exists.";
		
		
		Object[][] categoryData = new Object[][] {
				// sequence:: rowNum,domain,parent category,
				// category,geography,fName,lName,uName,sName,subscriberCode,externalCode,
				// MSISDN,selectOutletSubOutlet,contactNo,address1,address2,city,state,country,emailID,
				// loginID,assignGeography,assignRoles,assignServices,assignProducts,assgnPhoneNumber,PASSWORD,CONFIRMPASSWORD,PIN
				{
						rowNum,
						userDetailsHL[0],
						userDetailsHL[1],
						userDetailsHL[2],
						userDetailsHL[3],
						description[0],
						"AUTFN" + randStr.randomNumeric(4),
						"AUTLN" + randStr.randomNumeric(4),
						"AUTFN" + randStr.randomNumeric(4) + " AUTLN"
								+ randStr.randomNumeric(4),
						"AUTSN" + randStr.randomNumeric(4),
						"" + randStr.randomNumeric(6),
						UniqueChecker.UC_EXTCODE(),
						UniqueChecker.UC_MSISDN(),
						"Y",
						"" + randStr.randomNumeric(6),
						"Add1" + randStr.randomNumeric(4),
						"Add2" + randStr.randomNumeric(4),
						"City" + randStr.randomNumeric(4),
						"State" + randStr.randomNumeric(4),
						"country" + randStr.randomNumeric(2),
						randStr.randomAlphaNumeric(5).toLowerCase()
								+ "@mail.com",
						DBHandler.AccessHandler.existingLoginID(),
						"Y",
						"Y",
						"Y",
						"Y",
						"Y",
						_masterVO.getProperty("Password"),
						_masterVO.getProperty("ConfirmPassword"),
						_masterVO.getProperty("PIN") },
				{
						rowNum,
						userDetailsHL[0],
						userDetailsHL[1],
						userDetailsHL[2],
						userDetailsHL[3],
						description[1],
						"AUTFN" + randStr.randomNumeric(4),
						"AUTLN" + randStr.randomNumeric(4),
						"AUTFN" + randStr.randomNumeric(4) + " AUTLN"
								+ randStr.randomNumeric(4),
						"AUTSN" + randStr.randomNumeric(4),
						"" + randStr.randomNumeric(6),
						UniqueChecker.UC_EXTCODE(),
						DBHandler.AccessHandler.existingMSISDN(),
						"Y",
						"" + randStr.randomNumeric(6),
						"Add1" + randStr.randomNumeric(4),
						"Add2" + randStr.randomNumeric(4),
						"City" + randStr.randomNumeric(4),
						"State" + randStr.randomNumeric(4),
						"country" + randStr.randomNumeric(2),
						randStr.randomAlphaNumeric(5).toLowerCase()
								+ "@mail.com",
						UniqueChecker.UC_LOGINID(),
						"Y",
						"Y",
						"Y",
						"Y",
						"Y",
						_masterVO.getProperty("Password"),
						_masterVO.getProperty("ConfirmPassword"),
						_masterVO.getProperty("PIN") }, };
		
		
		
		return categoryData;
	}
}