/**
 * 
 */
package com.testscripts.uap;

import java.io.IOException;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChangeChannelUserPIN;
import com.Features.ChannelUserPinManagement;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */


public class UAP_ChannelUserPinMgmt extends BaseTest {

	HashMap<String, String> pinresultMap,channelPINMap;
	static boolean TestCaseCounter = false;
	HashMap<String, String> userAccessMap;
	
	@Test(dataProvider="pinMgmtData")
	public void chnlPINMgmt(String mobileNo, String LoginID,String Remarks) throws InterruptedException, IOException{
		
		Log.startTestCase(this.getClass().getName());
		
		ChannelUserPinManagement chnlPinMgmt = new ChannelUserPinManagement(driver);
		ChangeChannelUserPIN changePIN = new ChangeChannelUserPIN(driver);
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[UAP]Channel User PIN Management");
			TestCaseCounter = true;
		}
		HashMap<String, String> pinMgmtMap = new HashMap<String, String>();
		pinMgmtMap.put("mobileNumber", mobileNo);
		pinMgmtMap.put("Remarks", Remarks);
		pinMgmtMap.put("LoginID",LoginID);
	
		/*
		 * Test case1
		 */
		currentNode=test.createNode("To verify that operator user is able to perform Send PIN.");
		currentNode.assignCategory("UAP");
		pinresultMap=chnlPinMgmt.channelUserPinMgmt_sendPIN(pinMgmtMap);
		
		/*
		 * Test case2
		 */
		currentNode=test.createNode("To verify that valid message appears once send PIN is performed.");
		currentNode.assignCategory("UAP");
		String sendPINMsg=MessagesDAO.prepareMessageByKey("channeluser.unblockpin.msg.sendmsg.success", "");
		if(sendPINMsg.equals(pinresultMap.get("sendPinMsg")))
			currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + sendPINMsg + "] but found [" + pinresultMap.get("sendPinMsg") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		
		
		/*
		 * Test case3
		 */
		currentNode=test.createNode("To verify that operator user is able to perform Reset PIN.");
		currentNode.assignCategory("UAP");
		pinresultMap=chnlPinMgmt.channelUserPinMgmt_ResetPIN(pinMgmtMap);
		
		/*
		 * Test case4
		 */
		currentNode=test.createNode("To verify that valid message appears once reset PIN is performed.");
		currentNode.assignCategory("UAP");
		String resetPINMsg=MessagesDAO.prepareMessageByKey("channeluser.unblockpin.msg.resetsuccess", "");
		if(resetPINMsg.equals(pinresultMap.get("resetPinMsg")))
			currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + resetPINMsg + "] but found [" + pinresultMap.get("resetPinMsg") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		
		/*
		 * Test case5
		 */
		currentNode=test.createNode("To verify that after Reset pin, operator is able to change PIN for the channel user whose PIN is reset.");
		currentNode.assignCategory("UAP");
		channelPINMap=changePIN.changePINafterReset(pinMgmtMap.get("LoginID"));
		
		/*
		 * Test case6
		 */
		currentNode=test.createNode("To verify that valid message appears once Change PIN is performed.");
		currentNode.assignCategory("UAP");
		String changePINMsg=MessagesDAO.prepareMessageByKey("user.changepin.msg.updatesuccess", "");
		if(changePINMsg.equals(channelPINMap.get("changePINMsg")))
			currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + changePINMsg + "] but found [" + channelPINMap.get("changePINMsg") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		
	}
	
	@DataProvider(name="pinMgmtData")
	public Object[][] pinManagementData(){
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowNum=1;
		RandomGeneration randStr = new RandomGeneration();
		
		String userDetailsHL[] = {ExcelUtility.getCellData(0, ExcelI.MSISDN, rowNum),ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, rowNum)};	
		
		//sequence:: MSISDN, LoginID, Remarks
		Object[][] categoryData = new Object[][]{
				{userDetailsHL[0],userDetailsHL[1],"AUT"+randStr.randomAlphabets(8)+":: Automated remarks for channelPin management"}
			};
		
		return categoryData;
		
	}
}
