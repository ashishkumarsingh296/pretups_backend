package com.testscripts.smoke;

import org.testng.annotations.Test;

import com.Features.ChangeNetworkStatus;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SMOKE_NETWORK_STATUS)
public class Smoke_NetworkStatus extends BaseTest {

    @Test
     @TestManager(TestKey = "PRETUPS-539") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void a_DeactivateNetwork() {
        final String methodName = "Test_DeactivateNetwork";
        Log.startTestCase(methodName);

        Login login = new Login();

        // Test Case to Verify Operator User is able to deactivate Network
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SNETWORKSTATUS1").getExtentCase()).assignCategory(TestCategory.SMOKE);
        String network = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        ChangeNetworkStatus changeNetworkStatus = new ChangeNetworkStatus(driver);
        String Message = changeNetworkStatus.deactivateNetwork(network);

        // Test Case to Validate Message
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SNETWORKSTATUS2").getExtentCase()).assignCategory(TestCategory.SMOKE);
        String activateNetworkMsg = MessagesDAO.prepareMessageByKey("network.networkstatus.successmessage");
        Assertion.assertEquals(Message, activateNetworkMsg);

        // Test Case to Validate User is not able to login to application after Network Deactivation
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SNETWORKSTATUS3").getExtentCase()).assignCategory(TestCategory.SMOKE);
        String ErrorMessage = login.UserLogin(driver, "Operator", "Network Admin", "Channel Admin");
        if (ErrorMessage.equals(null))
            currentNode.log(Status.FAIL, "User is able to Login");
        else
            currentNode.log(Status.PASS, "User is not able to Login");

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test
    @TestManager(TestKey = "PRETUPS-548") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void b_ActivateNetwork() {
        final String methodName = "Test_ActivateNetwork";
        Log.startTestCase(methodName);

        // Test Case for Validating if Operator user is able to Activate Network
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SNETWORKSTATUS4").getExtentCase()).assignCategory(TestCategory.SMOKE);
        String network = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        ChangeNetworkStatus changeNetworkStatus = new ChangeNetworkStatus(driver);
        String Message = changeNetworkStatus.activateNetwork(network);

        // Test Case for Validating Message
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SNETWORKSTATUS5").getExtentCase()).assignCategory(TestCategory.SMOKE);
        String activateNetworkMsg = MessagesDAO.prepareMessageByKey("network.networkstatus.successmessage");
        Assertion.assertEquals(Message, activateNetworkMsg);

        // Test Case to Verify Operator User is Able to Login to Application After successful Network Activation
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SNETWORKSTATUS6").getExtentCase()).assignCategory(TestCategory.SMOKE);
        try {
            Login login = new Login();
            login.UserLogin(driver, "Operator", "Network Admin", "Channel Admin");
            currentNode.log(Status.PASS, "Operator User Logged in successfully");
        } catch (Exception e) {
            Assertion.assertFail("Operator User failed to login");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
}