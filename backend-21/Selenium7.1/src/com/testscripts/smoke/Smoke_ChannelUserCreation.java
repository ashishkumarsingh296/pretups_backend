package com.testscripts.smoke;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

/*
 * @author PVG
 * This class is created to add Channel Users
 */
public class Smoke_ChannelUserCreation extends BaseTest {
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
	
	@Test(dataProvider = "Domain&CategoryProvider")
	public void channelUserCreation(int RowNum, String Domain, String Parent, String Category, String geotype) throws InterruptedException {
		
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[Smoke]Channel User Creation");
			TestCaseCounter = true;
		}
		
		ChannelUser channelUserLogic= new ChannelUser(driver);
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(Category);
		/*
		 * Test Case Number 1: Channel User Initiation
		 */
		currentNode=test.createNode("To verify that Channel Admin is able to initiate " + Category+" category Channel user via parent Category "+Parent+" .");
		currentNode.assignCategory("Smoke");
		channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype);
		
		/*
		 * Test Case Number 2: Message Validation
		 */
		currentNode=test.createNode("To verify that valid message is displayed after initiating "+Category+" category channel user.");
		currentNode.assignCategory("Smoke");
		
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,"USER_APPROVAL_LEVEL");
		
		String intChnlInitiateMsg;
		if(APPLEVEL.equals("0"))
		{	
			intChnlInitiateMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessage", channelresultMap.get("uName"));	
		}else{
			intChnlInitiateMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessageforrequest", channelresultMap.get("uName"));
		}
		
			if (channelresultMap.get("channelInitiateMsg").equals(intChnlInitiateMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
			else {
			currentNode.log(Status.FAIL, "Expected [" + intChnlInitiateMsg + "] but found [" + channelresultMap.get("channelInitiateMsg") + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		
		
		if(APPLEVEL.equals("2"))
		{
		currentNode=test.createNode("To verify that Channel Admin is able to approve level 1 " + Category+" category Channel user.");
		currentNode.assignCategory("Smoke");
		channelresultMap=channelUserLogic.approveLevel1_ChannelUser();
		
		currentNode=test.createNode("To verify that valid message is displayed after approval level 1 of "+Category+" category channel user.");
		currentNode.assignCategory("Smoke");
		String intChnlApprove1Msg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagerequiredleveltwoapproval", channelresultMap.get("uName"));
				if (channelresultMap.get("channelApprovelevel1Msg").equals(intChnlApprove1Msg))
				currentNode.log(Status.PASS, "Message Validation Successful");
				else {
				currentNode.log(Status.FAIL, "Expected [" + intChnlApprove1Msg + "] but found [" + channelresultMap.get("channelApprovelevel1Msg") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
				}
		
		
		currentNode=test.createNode("To verify that Channel Admin is able to approve level 2 " + Category+" category Channel user.");
		currentNode.assignCategory("Smoke");
		channelresultMap=channelUserLogic.approveLevel2_ChannelUser();
		
		currentNode=test.createNode("To verify that valid message is displayed after approval level 2 of "+Category+" category channel user.");
		currentNode.assignCategory("Smoke");
		String intChnlApprove2Msg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", channelresultMap.get("uName"));
				if (channelresultMap.get("channelApprovelevel2Msg").equals(intChnlApprove2Msg))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else {
					currentNode.log(Status.FAIL, "Expected [" + intChnlApprove2Msg + "] but found [" + channelresultMap.get("channelApprovelevel2Msg") + "]");
					currentNode.log(Status.FAIL, "Message Validation Failed");
				}
		}
		else if(APPLEVEL.equals("1")){
		
			currentNode=test.createNode("To verify that Channel Admin is able to approve " + Category+" category Channel user.");
			currentNode.assignCategory("Smoke");
			channelresultMap=channelUserLogic.approveLevel1_ChannelUser();
		
			currentNode=test.createNode("To verify that valid message is displayed after approval of "+Category+" category channel user.");
			currentNode.assignCategory("Smoke");
			String intChnlApproveMsg = MessagesDAO.prepareMessageByKey("user.addchanneluser.level1approvemessagenotrequiredleveltwoapproval", channelresultMap.get("uName"));
			//assertEquals(channelresultMap.get("channelApprovelevel1Msg"), intChnlApproveMsg);
				if (channelresultMap.get("channelApproveMsg").equals(intChnlApproveMsg))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else {
					currentNode.log(Status.FAIL, "Expected [" + intChnlApproveMsg + "] but found [" + channelresultMap.get("channelApproveMsg") + "]");
					currentNode.log(Status.FAIL, "Message Validation Failed");
				}
			
		}else{
			Log.info("Approval not required.");	
		}
		
		if(webAccessAllowed.equalsIgnoreCase("Y")){
		currentNode=test.createNode("To verify that " + Category+" category Channel user is prompted for change password on first time login and successfuly change the password.");
		currentNode.assignCategory("Smoke");
		channelUserLogic.changeUserFirstTimePassword();}
		
		currentNode=test.createNode("To verify that Channel Admin change the PIN of " + Category+" category Channel user for processing further transaction.");
		currentNode.assignCategory("Smoke");
		channelresultMap=channelUserLogic.changeUserFirstTimePIN();
		
		currentNode=test.createNode("To verify that valid message is displayed after PIN is changed.");
		currentNode.assignCategory("Smoke");
		String intChnlChangePINMsg = MessagesDAO.prepareMessageByKey("user.changepin.msg.updatesuccess");

				if (channelresultMap.get("changePINMsg").equals(intChnlChangePINMsg))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else {
					currentNode.log(Status.FAIL, "Expected [" + intChnlChangePINMsg + "] but found [" + channelresultMap.get("changePINMsg") + "]");
					currentNode.log(Status.FAIL, "Message Validation Failed");
				}
	}

	@DataProvider(name = "Domain&CategoryProvider")
	public Object[][] DomainCategoryProvider() {

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

}