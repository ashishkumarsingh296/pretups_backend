/**
 * 
 */
package com.testscripts.uap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.Features.ChangeChannelUserPIN;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */


public class UAP_ChangeSelfPin extends BaseTest {

	HashMap<String, String> selfpinresultMap,channelPINMap;
	static boolean TestCaseCounter = false;
	HashMap<String, String> userAccessMap;
	
	@Test
	public void changeSelfPIN() throws InterruptedException, IOException{
		
		Log.startTestCase(this.getClass().getName());
		
		ChangeChannelUserPIN changePIN = new ChangeChannelUserPIN(driver);
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[UAP]Change Self PIN");
			TestCaseCounter = true;
		}
		
		Map<String, String> chngSelfPin = UserAccess.getUserWithAccess(RolesI.CHANGESELFPIN_ROLECODE);
		if(chngSelfPin.get("LOGIN_ID") != null && !chngSelfPin.get("LOGIN_ID").equals("")){
		currentNode=test.createNode("To verify that operator user is able to perform 'Change self PIN'.");
		currentNode.assignCategory("UAP");
		selfpinresultMap=changePIN.changeSelfPIN();
		
		currentNode=test.createNode("To verify that valid message appears once change self PIN is performed.");
		currentNode.assignCategory("UAP");
		String sendPINMsg=MessagesDAO.prepareMessageByKey("user.changepin.msg.updatesuccess", "");
		if(sendPINMsg.equals(selfpinresultMap.get("changeSelfPINMsg")))
			currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + sendPINMsg + "] but found [" + selfpinresultMap.get("changeSelfPINMsg") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}
		else{
			currentNode=test.createNode("To verify that operator user is able to perform 'Change self PIN'.");
			currentNode.assignCategory("UAP");
			currentNode.log(Status.PASS, "Role for ChangeSelfPIN does not exist with any operator user.");
		}
		
	}

	
}
