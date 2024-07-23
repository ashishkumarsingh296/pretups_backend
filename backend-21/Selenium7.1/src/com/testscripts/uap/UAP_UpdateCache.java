package com.testscripts.uap;

import org.testng.annotations.Test;

import com.Features.CacheUpdate;
import com.Features.ChangeNetworkStatus;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.commons.MasterI;
import com.utils._masterVO;
import com.utils.Log;

public class UAP_UpdateCache extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	
	@Test
	public void A_updateCache() {
		
		Log.startTestCase(this.getClass().getName());
		
		ChangeNetworkStatus NetworkStatus = new ChangeNetworkStatus(driver);
		CacheUpdate CacheUpdate = new CacheUpdate(driver);
		Login login = new Login();
		String NetworkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String NetworkDeactivationMessage = "Network disabled by PreTUPS Automation Suite";
		
		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Update Cache");
			TestCaseCounter = true;
		}
		
		currentNode = test.createNode("To verify that Operator is able to perform Update Cache successfully");
		currentNode.assignCategory("UAP");
		CacheUpdate.updateCache();
		/*NetworkStatus.deactivateNetwork(NetworkCode, NetworkDeactivationMessage, NetworkDeactivationMessage);
		String SuccessMessage = CacheUpdate.updateCache();
		
		currentNode = test.createNode("To verify that proper Message is displayed on Successful Update Cache");
		currentNode.assignCategory("UAP");
		boolean MessageStatus = CacheUpdate.validateMessage(SuccessMessage);
		if (MessageStatus == true)
			currentNode.log(Status.PASS, "Message Validated Successful");
		else
			currentNode.log(Status.FAIL, "Message Validation Failure");
		
		currentNode = test.createNode("To verify that Cache is updated properly & changes are reflected to Application");
		currentNode.assignCategory("UAP");
		String ErrorMessage = login.UserLogin(driver, "Operator", "Channel Admin");
		Log.info("Validating Network Deactivation Message with the Actual Web Message");
		if (ErrorMessage.equals(NetworkDeactivationMessage))
			currentNode.log(Status.PASS, "Cache Refleted successfully");
		else
			currentNode.log(Status.FAIL, "Cache didn't reflect");
		
		currentNode = null;
		NetworkStatus.activateNetwork(NetworkCode);*/
	}
	
}
