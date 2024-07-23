package com.testscripts.uap;

import org.testng.annotations.Test;

import com.Features.CacheUpdate;
import com.Features.ChangeNetworkStatus;
import com.classes.BaseTest;
import com.classes.Login;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.UAP_UPDATE_CACHE)
public class UAP_UpdateCache extends BaseTest{
	
	public static boolean TestCaseCounter = false;
	String assignCategory="UAP";
	@Test
	@TestManager(TestKey = "PRETUPS-321") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_updateCache() {
		
		final String methodName = "Test_UpdateCache";
        Log.startTestCase(methodName);
		
		ChangeNetworkStatus NetworkStatus = new ChangeNetworkStatus(driver);
		CacheUpdate CacheUpdate = new CacheUpdate(driver);
		Login login = new Login();
		String NetworkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String NetworkDeactivationMessage = "Network disabled by PreTUPS Automation Suite";
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UUPDATECACHE1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		CacheUpdate.updateCache();
		
		Log.endTestCase(methodName);
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
