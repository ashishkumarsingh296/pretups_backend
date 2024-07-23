package com.testscripts.smoke;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.MasterI;
import com.Features.ChangeNetworkStatus;
import com.utils._masterVO;
import com.utils.Log;

public class Smoke_NetworkStatus extends BaseTest {
	
	static boolean TestCaseCounter = false;
	
	/**
	 * <h1>Deactivate Network Test Cases</h1>
	 */
	@Test
	public void a_deactivateNetwork() {
		
		Log.startTestCase(this.getClass().getName());
		Login login = new Login();
		
		if (TestCaseCounter == false) {
			test = extent.createTest("[Smoke] Network Activate / Deactivate");
			TestCaseCounter = true;
		}
		
		/*
		 * Test Case to Verify Operator User is able to deactivate Network
		 */
		currentNode = test.createNode("To verify that Operator user is able to Deactivate Network");
		currentNode.assignCategory("Smoke");
		String network = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		ChangeNetworkStatus changeNetworkStatus = new ChangeNetworkStatus(driver);
		String Message = changeNetworkStatus.deactivateNetwork(network);
		
		/*
		 * Test Case to Validate Message
		 */
		currentNode = test.createNode("To verify that proper Message is displayed on successful Network Deactivation");
		currentNode.assignCategory("Smoke");
		String activateNetworkMsg = MessagesDAO.prepareMessageByKey("network.networkstatus.successmessage");
		if (Message.equals(activateNetworkMsg))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + activateNetworkMsg + "] but found [" + Message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		
		/*
		 * Test Case to Validate User is not able to login to application after Network Deactivation
		 */
		currentNode = test.createNode("To verify that Operator user is not able to login to application on Successful Network Deactivation");
		currentNode.assignCategory("Smoke");
		String ErrorMessage = login.UserLogin(driver, "Operator", "Network Admin", "Channel Admin");
		if (ErrorMessage.equals(null))		
			currentNode.log(Status.FAIL, "User is able to Login");
		else
			currentNode.log(Status.PASS, "User is not able to Login");
		
		Log.endTestCase(this.getClass().getName());
	}
	

	/**
	 * <h1>Activate Network Test Case</h1>
	 */
	@Test
	public void b_activateNetwork() {

		Log.startTestCase(this.getClass().getName());
		
		/*
		 * Test Case for Validating if Operator user is able to Activate Network
		 */
		currentNode = test.createNode("To verify that Operator user is able to Activate Network");
		currentNode.assignCategory("Smoke");
		String network = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		ChangeNetworkStatus changeNetworkStatus = new ChangeNetworkStatus(driver);
		String Message = changeNetworkStatus.activateNetwork(network);
		
		/*
		 * Test Case for Validating Message
		 */
		currentNode =  test.createNode("To verify that Proper Message is displayed on Successful Network Activation");
		currentNode.assignCategory("Smoke");
		String activateNetworkMsg = MessagesDAO.prepareMessageByKey("network.networkstatus.successmessage");
		if (activateNetworkMsg.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + activateNetworkMsg + "] but found [" + Message + "]");
			currentNode.log(Status.FAIL, "Message Validation Unsuccessful");
		}
		
		/*
		 * Test Case to Verify Operator User is Able to Login to Application After successful Network Activation
		 */
		currentNode = test.createNode("To verify that Operator User is able to login to application successfully after Successful Netrwork Activation");
		currentNode.assignCategory("Smoke");
		try {
			Login login = new Login();
			login.UserLogin(driver, "Operator", "Network Admin", "Channel Admin");
			currentNode.log(Status.PASS, "Operator User Logged in successfully");
		}
		catch (Exception e) {
			currentNode.log(Status.FAIL, "Operator User failed to login");
		}
		
		Log.endTestCase(this.getClass().getName());
	}
}
