package com.testscripts.sit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.Features.AutoO2CTransfer;
import com.Features.mapclasses.AutoO2CMap;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.sshmanager.SSHService;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_Auto_O2C)
public class SIT_AutoO2C extends BaseTest {

	static boolean testCaseCounter = false;
	static String masterSheetPath;
	static String autoO2CAllowed;
	static String networkCode;
	static Object[][] data;
	String productCode = null;
	static String userId;
	HashMap<String, String> transferMap;
	String type;
	String processID;
	String executedDate = null;
	String currentDate = null;
	HashMap<String, String> tcpDetails;
	String profileID = null;
	Long alertingBalance;
	static boolean isRoleCodeExist;
	String assignCategory="SIT";
	@Test
	public void A_loadPreRequisites() {
		String O2CTransferCode = _masterVO.getProperty("O2CTransferCode");
		AutoO2CMap autoO2CMap = new AutoO2CMap();
		transferMap = autoO2CMap.getAutoOperatorToChannelMap(O2CTransferCode);
		userId = DBHandler.AccessHandler.getUserId(transferMap.get("ChannelUser"));
		autoO2CAllowed = DBHandler.AccessHandler.getChannelUserStatus(userId);
		isRoleCodeExist = ExcelUtility.isRoleExists(RolesI.INITIATE_AUTO_O2C_TRANSFER_ROLECODE);//UserAccess.getRoleStatus(RolesI.INITIATE_AUTO_O2C_TRANSFER_ROLECODE);
		networkCode = _masterVO.getMasterValue("Network Code");
		type = "OPT";

		data = DBHandler.AccessHandler.getProductDetails(networkCode, autoO2CMap.getAutoO2CMap("DOMAIN_CODE", O2CTransferCode), autoO2CMap.getAutoO2CMap("FROM_CATEGORY_CODE", O2CTransferCode), autoO2CMap.getAutoO2CMap("TO_CATEGORY_CODE", O2CTransferCode), type);
		processID = "AUTOO2CPROCESS";
	}

	@Test
	@TestManager(TestKey = "PRETUPS-938") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEA_GeoDomainNull() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);

		AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (isRoleCodeExist) {
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("GEO_DOMAIN","");
			
				Map<String, String> userAccessMap = UserAccess.getUserWithAccess(RolesI.INITIATE_AUTO_O2C_TRANSFER_ROLECODE);
				int countGeo=Integer.parseInt(DBHandler.AccessHandler.fetchUserGeographyCount(userAccessMap.get("LOGIN_ID")));
				if(countGeo>1) {
					try {
					transferMap=autoO2CTransfer.initiateAutoO2CTransfer(transferMap); 
					
					}catch(Exception e) {
						String actualMessage =  autoO2CTransfer.getErrorMessage();
						String expectedMessage = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("channeltransfer.searchchanneluser.label.geographicaldomain"));
						Assertion.assertEquals(actualMessage, expectedMessage);
					}
					
				}
				else
					Assertion.assertSkip("Only single geographical domain exist for operator user, hence no drop down to select.");
			
		} else { Assertion.assertSkip("Auto O2C Module not available in system.") ; }
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-941") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEB_DomainNull() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);
		AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (isRoleCodeExist) {
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("TO_DOMAIN","");
			try{
				transferMap=autoO2CTransfer.initiateAutoO2CTransfer(transferMap);
			}catch(Exception e) {
				String actualMessage =  autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("operatortrfrule.viewtrfrule.label.domain"));
				Assertion.assertEquals(actualMessage, expectedMessage);
			}
		} else {Assertion.assertSkip("Auto O2C Module not available in system.") ; }
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}
	/*
			@Test()
			public void CASEC_CategoryNull() {

		          Log.startTestCase(this.getClass().getName());

				if (testCaseCounter == false) { 
					test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2C1").getModuleCode());
					testCaseCounter = true;
				}
				AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
				AutoO2CMap _mapgenerator = new AutoO2CMap();
				currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C3").getExtentCase());
				currentNode.assignCategory(assignCategory);
				HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
				transferMap.put("TO_CATEGORY","");
				try{
					transferMap=autoO2CTransfer.initiateAutoO2CTransfer(transferMap);
				}catch(Exception e) {
					String actualMessage =  autoO2CTransfer.getErrorMessage();
					String expectedMessage = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("channeltransfer.searchchanneluser.label.category"));
					Validator.messageCompare(actualMessage, expectedMessage);
					}
		}*/

	@Test
	@TestManager(TestKey = "PRETUPS-942") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASED_ChannelUserNull() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);
		AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (isRoleCodeExist) {
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("ChannelUser","");
			try{
				transferMap=autoO2CTransfer.initiateAutoO2CTransfer(transferMap);
				String actualMessage = transferMap.get("Message");
				if(transferMap.get("Message").equals("")||transferMap.get("Message")==null)
				{actualMessage =  autoO2CTransfer.getErrorMessage();}

				String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.searchchanneluser2.error.channelcategory");
				Assertion.assertEquals(actualMessage, expectedMessage);
			}catch(Exception e) {
				String actualMessage =  autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.searchchanneluser2.error.channelcategory");
				Assertion.assertEquals(actualMessage, expectedMessage);
			}
		} else { Assertion.assertSkip("Auto O2C Module not available in system.") ; }
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-943") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEE_PositiveFlow() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);
		AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (isRoleCodeExist && autoO2CAllowed.equalsIgnoreCase("N")) {
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			try{
				transferMap=autoO2CTransfer.initiateAutoO2CTransfer(transferMap);
				userId = DBHandler.AccessHandler.getUserId(transferMap.get("ChannelUser"));
				autoO2CAllowed = DBHandler.AccessHandler.getChannelUserStatus(userId);
				if(autoO2CAllowed.equalsIgnoreCase("NEW"))
					Assertion.assertPass("Message Validation Successful");
			}catch(Exception e) {
				String actualMessage =  autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO.prepareMessageByKey("autoO2c.transferdetailssuccess.msg.success");
				Assertion.assertEquals(actualMessage, expectedMessage);
			}
		} else { Assertion.assertSkip("Auto O2C Module not available in system.") ; }
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-945") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEF_AlreadyInitiated() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);
		AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (isRoleCodeExist && !autoO2CAllowed.equalsIgnoreCase("N")) {
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			try{
				transferMap=autoO2CTransfer.initiateAutoO2CTransfer(transferMap);
				String actualMessage =  autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.searchchanneluser2.error.channelcategory");
				Assertion.assertEquals(actualMessage, expectedMessage);
			}catch(Exception e) {
				String actualMessage =  autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO.prepareMessageByKey("channeltransfer.searchchanneluser2.error.channelcategory");
				Assertion.assertEquals(actualMessage, expectedMessage);
			}
		} else {Assertion.assertSkip("Auto O2C Module not available in system.") ; }
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	/*@Test()
			public void CASEG_RejectApprovalLevel1() {


		          Log.startTestCase(this.getClass().getName());

				if (testCaseCounter == false) { 
					test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2C1").getModuleCode());
					testCaseCounter = true;
				}
				AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
				AutoO2CMap _mapgenerator = new AutoO2CMap();
				currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C7").getExtentCase());
				currentNode.assignCategory(assignCategory);
				HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
				Map<String,String> resultMap = null;
				try{
					ExtentI.Markup(ExtentColor.TEAL, "Performing Auto O2C Transfer");
					String type= "Rejecting";
					ExtentI.Markup(ExtentColor.TEAL, "Rejecting Auto O2C Level1");
					resultMap=autoO2CTransfer.performingLevel1Approval(transferMap,type);
				}catch(Exception e) {
					String actualMessage =  resultMap.get("actualMessage");
					String expectedMessage = MessagesDAO.prepareMessageByKey("autoO2c.ApprovalReject.msg.success",transferMap.get("ChannelUser"));
					Validator.messageCompare(actualMessage, expectedMessage);
					}


		}*/


	@Test
	@TestManager(TestKey = "PRETUPS-946") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEH_AcceptApprovalLevel1() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);
		AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		autoO2CAllowed = DBHandler.AccessHandler.getChannelUserStatus(userId);
		if (isRoleCodeExist && autoO2CAllowed.equalsIgnoreCase("NEW")) {
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			Map<String,String> resultMap = null;
			try{
				ExtentI.Markup(ExtentColor.TEAL, "Performing Auto O2C Transfer");
				String type= "Approving";
				ExtentI.Markup(ExtentColor.TEAL, "Approving Auto O2C Level1");
				resultMap=autoO2CTransfer.performingLevel1Approval(transferMap,type);
			}catch(Exception e) {
				String actualMessage =  resultMap.get("actualMessage");
				String expectedMessage = MessagesDAO.prepareMessageByKey("autoO2c.Approvalssuccess.msg.success",transferMap.get("ChannelUser"));
				Assertion.assertEquals(actualMessage, expectedMessage);
			}
		} else { Assertion.assertSkip("Auto O2C Module not available in system.") ; }
		Assertion.completeAssertions();
		Log.endTestCase(methodname);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-948") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEI_AcceptApprovalLevel2() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);
		AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		autoO2CAllowed = DBHandler.AccessHandler.getChannelUserStatus(userId);
		if (isRoleCodeExist && autoO2CAllowed.equalsIgnoreCase("AP1")) {
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			Map<String,String> resultMap = null;
			try{
				ExtentI.Markup(ExtentColor.TEAL, "Approving Auto O2C Level2");
				resultMap=autoO2CTransfer.performingLevel2Approval(transferMap);
			}catch(Exception e) {
				String actualMessage =  resultMap.get("actualMessage");
				String expectedMessage = MessagesDAO.prepareMessageByKey("autoO2c.Approvalssuccess.msg.success",transferMap.get("ChannelUser"));
				Assertion.assertEquals(actualMessage, expectedMessage);
			}
		} else {Assertion.assertSkip("Auto O2C Module not available in system.") ; }
		Assertion.completeAssertions();
		Log.endTestCase(methodname);

	}

	@Test
	@TestManager(TestKey = "PRETUPS-950") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEJ_AcceptApprovalLevel3() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);
		AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		autoO2CAllowed = DBHandler.AccessHandler.getChannelUserStatus(userId);
		if (isRoleCodeExist && autoO2CAllowed.equalsIgnoreCase("AP2")) {
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			Map<String,String> resultMap = null;
			try{
				ExtentI.Markup(ExtentColor.TEAL, "Performing Auto O2C Transfer");
				ExtentI.Markup(ExtentColor.TEAL, "Approving Auto O2C Level3");
				resultMap=autoO2CTransfer.performingLevel3Approval(transferMap);
			}catch(Exception e) {
				String actualMessage =  resultMap.get("actualMessage");
				String expectedMessage = MessagesDAO.prepareMessageByKey("autoO2c.Approvalssuccess.msg.success",transferMap.get("ChannelUser"));
				Assertion.assertEquals(actualMessage, expectedMessage);
			}
		} else { Assertion.assertSkip("Auto O2C Module not available in system.") ;
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	//Validating Balance
	@Test
	@TestManager(TestKey = "PRETUPS-952") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEK_ValidateTransfer() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);
		for(int productCount=0;productCount<data.length;productCount++){
			productCode = data[productCount][0].toString();

			AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
			AutoO2CMap _mapgenerator = new AutoO2CMap();
			currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C11").getExtentCase());
			currentNode.assignCategory(assignCategory);
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			String loginID = transferMap.get("Login_ID");
			String channelUser = transferMap.get("ChannelUser");
			String b = DBHandler.AccessHandler.getUserBalance(productCode, loginID);
			long balance= Long.parseLong(b);
			profileID = transferMap.get("PROFILE_ID");
			tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, productCode, "ALERTING_BALANCE");
			alertingBalance = Long.parseLong(tcpDetails.get("ALERTING_BALANCE"));
			long diff = balance - alertingBalance;
			String userID = DBHandler.AccessHandler.getUserId(channelUser);
			String type1 = "O2C";
			String recordType = DBHandler.AccessHandler.getUserThresholdStatus(userID, type1, productCode);
			if(diff < 0)
			{
				SSHService.executeScript("Auto_O2C_Process.sh");
				executedDate = DBHandler.AccessHandler.getExecutedDate(processID);

				DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
				Date date = new Date();
				currentDate = dateFormat.format(date);
				System.out.println(currentDate);

				if(!executedDate.equalsIgnoreCase(currentDate))
				{
					Assertion.assertFail("Script not executed Properly");
				}
				long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(productCode, loginID));
				long expectedBalance = balance + alertingBalance;
				if(newBalance == expectedBalance)
				{
					Assertion.assertPass("Balance Validation Successful");
				}
				else
				{
					Assertion.assertFail("Balance Validation Failed");
				}
			}
			else{
				Assertion.assertSkip("Balance Already Above Threshold");
			}
			//String recordType1 = DBHandler.AccessHandler.getUserThresholdStatus(userID, type);
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	//Balance greater than Threshold
	@Test
	@TestManager(TestKey = "PRETUPS-954") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEL_ValidateTransfer() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);
		for(int productCount=0;productCount<data.length;productCount++){
			productCode = data[productCount][0].toString();

			AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
			AutoO2CMap _mapgenerator = new AutoO2CMap();
			currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C12").getExtentCase());
			currentNode.assignCategory(assignCategory);
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			String loginID = transferMap.get("Login_ID");
			String channelUser = transferMap.get("ChannelUser");
			String b = DBHandler.AccessHandler.getUserBalance(productCode, loginID);
			long balance= Long.parseLong(b);
			profileID = transferMap.get("PROFILE_ID");
			tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, productCode, "ALERTING_BALANCE");
			alertingBalance = Long.parseLong(tcpDetails.get("ALERTING_BALANCE"));
			Long diff = balance - alertingBalance;
			String userID = DBHandler.AccessHandler.getUserId(channelUser);
			String type1 = "O2C";
			String recordType = DBHandler.AccessHandler.getUserThresholdStatus(userID, type1, productCode);
			if(diff > 0)
			{
				SSHService.executeScript("Auto_O2C_Process.sh");
				executedDate = DBHandler.AccessHandler.getExecutedDate(processID);

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				currentDate = dateFormat.format(date);
				System.out.println(currentDate); 

				if(!executedDate.contains(currentDate))
				{
					Assertion.assertFail("Script not executed Properly");
				}
				Long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(productCode, loginID));
				if(newBalance == balance)
				{
					Assertion.assertPass("Balance Validation Successful");
				}
				else
				{
					Assertion.assertFail("Balance Validation Failed");
				}
			}

			else{
				Assertion.assertSkip("Balance Below Threshold");
			}
			//String recordType1 = DBHandler.AccessHandler.getUserThresholdStatus(userID, type);
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	//Validating Threshold Status for Balance below Threshold
	@Test
	@TestManager(TestKey = "PRETUPS-957") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEM_ValidateTransfer() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);
		for(int productCount=0;productCount<data.length;productCount++){
			productCode = data[productCount][0].toString();

			AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
			AutoO2CMap _mapgenerator = new AutoO2CMap();
			currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C13").getExtentCase());
			currentNode.assignCategory(assignCategory);
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			String loginID = transferMap.get("Login_ID");
			String channelUser = transferMap.get("ChannelUser");
			//String profileID = transferMap.get("PROFILE_ID");
			String b = DBHandler.AccessHandler.getUserBalance(productCode, loginID);
			Long balance= Long.parseLong(b);
			profileID = transferMap.get("PROFILE_ID");
			tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, productCode, "ALERTING_BALANCE");
			alertingBalance = Long.parseLong(tcpDetails.get("ALERTING_BALANCE"));
			Long diff = balance - alertingBalance;
			String userID = DBHandler.AccessHandler.getUserId(channelUser);
			String type1 = "O2C";
			if(diff < 0)
			{
				String recordType = DBHandler.AccessHandler.getUserThresholdStatus(userID, type1, productCode);
				if(recordType.equalsIgnoreCase("BT"))
					Assertion.assertPass("Balance Validation Successful");
				else
				{
					Assertion.assertFail("Balance Validation Failed");
				}
			}
			else{
				Assertion.assertSkip("Balance Below Threshold");
			}
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	//Validating Threshold Status for Balance Above Threshold
	@Test
	@TestManager(TestKey = "PRETUPS-958") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEN_ValidateTransfer() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);
		for(int productCount=0;productCount<data.length;productCount++){
			productCode = data[productCount][0].toString();

			AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
			AutoO2CMap _mapgenerator = new AutoO2CMap();
			currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C14").getExtentCase());
			currentNode.assignCategory(assignCategory);
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			String loginID = transferMap.get("Login_ID");
			String channelUser = transferMap.get("ChannelUser");
			String b = DBHandler.AccessHandler.getUserBalance(productCode, loginID);
			Long balance= Long.parseLong(b);
			profileID = transferMap.get("PROFILE_ID");
			tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, productCode, "ALERTING_BALANCE");
			alertingBalance = Long.parseLong(tcpDetails.get("ALERTING_BALANCE"));
			Long diff = balance - alertingBalance;
			String userID = DBHandler.AccessHandler.getUserId(channelUser);
			String type1 = "O2C";
			if(diff > 0)
			{
				String recordType = DBHandler.AccessHandler.getUserThresholdStatus(userID, type1, productCode);
				if(recordType.equalsIgnoreCase("AT"))
					Assertion.assertPass("Balance Validation Successful");
				else
				{
					Assertion.assertFail("Balance Validation Failed");
				}
			}
			else{
				Assertion.assertSkip("Balance Below Threshold");
			}


		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	//Validating Threshold Status for Balance Below Threshold after executing script
	@Test
	@TestManager(TestKey = "PRETUPS-959") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void CASEO_ValidateTransfer() {
		final String methodname = "Test_AutoO2C";
		Log.startTestCase(methodname);
		for(int productCount=0;productCount<data.length;productCount++){
			productCode = data[productCount][0].toString();

			AutoO2CTransfer autoO2CTransfer=new AutoO2CTransfer(driver);
			AutoO2CMap _mapgenerator = new AutoO2CMap();
			currentNode=test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2C15").getExtentCase());
			currentNode.assignCategory(assignCategory);
			HashMap<String, String> transferMap = _mapgenerator.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			String loginID = transferMap.get("Login_ID");
			String channelUser = transferMap.get("ChannelUser");
			String b = DBHandler.AccessHandler.getUserBalance(productCode, loginID);
			Long balance= Long.parseLong(b);
			profileID = transferMap.get("PROFILE_ID");
			tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, productCode, "ALERTING_BALANCE");
			alertingBalance = Long.parseLong(tcpDetails.get("ALERTING_BALANCE"));
			Long diff = balance - alertingBalance;
			String userID = DBHandler.AccessHandler.getUserId(channelUser);
			String type1 = "O2C";
			String recordType = DBHandler.AccessHandler.getUserThresholdStatus(userID, type1, productCode);
			if(diff < 0 && recordType.equalsIgnoreCase("BT"))
			{
				SSHService.executeScript("Auto_O2C_Process.sh");
				executedDate = DBHandler.AccessHandler.getExecutedDate(processID);

				DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
				Date date = new Date();
				currentDate = dateFormat.format(date);
				System.out.println(currentDate); 

				if(!executedDate.equalsIgnoreCase(currentDate))
				{
					Assertion.assertFail("Script not executed Properly");
				}
				Long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(productCode, loginID));
				Long newDiff = newBalance - alertingBalance;
				String newRecordType = DBHandler.AccessHandler.getUserThresholdStatus(userID, type1, productCode);
				if(newDiff < 0 && newRecordType.equalsIgnoreCase("BT"))
					Assertion.assertPass("Balance Validation Successful");
				else if(newDiff >= 0 && newRecordType.equalsIgnoreCase("AT"))
					Assertion.assertPass("Balance Validation Successful");
				else
				{
					Assertion.assertFail("Balance Validation Failed");
				}
			}

		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}





}



