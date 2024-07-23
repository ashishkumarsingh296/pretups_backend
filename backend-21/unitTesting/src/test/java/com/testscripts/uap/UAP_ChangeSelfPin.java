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
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

/**
 * @author lokesh.kontey
 *
 */

@ModuleManager(name = Module.UAP_CHANGE_SELF_PIN)
public class UAP_ChangeSelfPin extends BaseTest {

	HashMap<String, String> selfpinresultMap,channelPINMap;
	static boolean TestCaseCounter = false;
	HashMap<String, String> userAccessMap;
	
	@Test
	@TestManager(TestKey = "PRETUPS-406") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void changeSelfPIN() throws InterruptedException, IOException{
		
		final String methodName = "Test_changeSelfPIN";
        Log.startTestCase(methodName);
			
		ChangeChannelUserPIN changePIN = new ChangeChannelUserPIN(driver);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("UCHNGSELFPIN1");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("UCHNGSELFPIN2");
		
		Map<String, String> chngSelfPin = UserAccess.getUserWithAccess(RolesI.CHANGESELFPIN_ROLECODE);
		if(chngSelfPin.get("LOGIN_ID") != null && !chngSelfPin.get("LOGIN_ID").equals("")){
		currentNode=test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("UAP");
		selfpinresultMap=changePIN.changeSelfPIN();
		
		currentNode=test.createNode(CaseMaster2.getExtentCase());
		currentNode.assignCategory("UAP");
		String sendPINMsg=MessagesDAO.prepareMessageByKey("user.changepin.msg.updatesuccess", "");
		Assertion.assertEquals(selfpinresultMap.get("changeSelfPINMsg"), sendPINMsg);
		
		}
		else{
			currentNode=test.createNode(CaseMaster1.getExtentCase());
			currentNode.assignCategory("UAP");
			currentNode.log(Status.PASS, "Role for ChangeSelfPIN does not exist with any operator user.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
}
