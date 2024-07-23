 package com.testscripts.sit;

import org.testng.annotations.Test;

import com.Features.P2PTransferRulesNegative;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_ADD_P2P_Transfer_Rule)
public class SIT_ADDP2PTransferRule extends BaseTest {
	static boolean TestCaseCounter = false;
	NetworkAdminHomePage networkAdminHomePage;
	public static boolean testCaseCounter = false;
	String assignCategory="SIT";
	String MasterSheetPath;
	
	//all null
	@Test
	@TestManager(TestKey = "PRETUPS-165") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_addP2PTransferRules() {	
		
		final String methodName = "Test_ADDP2PTransferRule";
        Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-165").getExtentCase());
		currentNode.assignCategory(assignCategory);
		P2PTransferRulesNegative p2ptransrulenegative =new P2PTransferRulesNegative(driver);
		String msg=p2ptransrulenegative.allNull();
		String addP2PTransferRuleFailureMsg = MessagesDAO.prepareMessageByKey("trfrule.addtrfrule.error.selectdata");
		Assertion.assertEquals(msg, addP2PTransferRuleFailureMsg);
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
	}
	// Extgw and sender Type selected 
	@Test
	@TestManager(TestKey = "PRETUPS-166") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void b_addP2PTransferRules()
	{

		final String methodName = "Test_ADDP2PTransferRule";
        Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-166").getExtentCase());
		currentNode.assignCategory(assignCategory);
		P2PTransferRulesNegative p2ptransrulenegative =new P2PTransferRulesNegative(driver);
	    String atcualmsg=	p2ptransrulenegative.addP2PReciverTypeNotSelected();
	    String addP2PTransferRuleFailureMsg = MessagesDAO.prepareMessageByKey("trfrule.addtrfrule.error.receiverrequired","1");
	    Assertion.assertEquals(atcualmsg, addP2PTransferRuleFailureMsg);
	    Assertion.completeAssertions();
	    Log.endTestCase(this.getClass().getName());
	}
	//Extgw , sender and reciver types selected 
	@Test
	@TestManager(TestKey = "PRETUPS-167") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void c_addP2PTransferRules()
	{

		final String methodName = "Test_ADDP2PTransferRule";
        Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-167").getExtentCase());
		currentNode.assignCategory(assignCategory);
		P2PTransferRulesNegative p2ptransrulenegative =new P2PTransferRulesNegative(driver);
		String atcualmsg =p2ptransrulenegative.addP2PServiceTypeNotSelected();
		String addP2PTransferRuleFailureMsg = MessagesDAO.prepareMessageByKey("trfrule.addtrfrule.error.subservicetyperequired","1");
		 Assertion.assertEquals(atcualmsg, addP2PTransferRuleFailureMsg);
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
	}
	// negative test case when sender ,receiver ,card group set , sub service is missing 
	@Test
	@TestManager(TestKey = "PRETUPS-168") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void d_addP2PTransferRules()
	{

		final String methodName = "Test_ADDP2PTransferRule";
        Log.startTestCase(methodName);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-168").getExtentCase());
		currentNode.assignCategory(assignCategory);
		P2PTransferRulesNegative p2ptransrulenegative =new P2PTransferRulesNegative(driver);
		String atcualmsg =p2ptransrulenegative.addP2PEXTGWAndServiceTypeSelected();
		String addP2PTransferRuleFailureMsg = MessagesDAO.prepareMessageByKey("trfrule.addtrfrule.error.senderrequired","1");
		 Assertion.assertEquals(atcualmsg, addP2PTransferRuleFailureMsg);
		Assertion.completeAssertions();
		Log.endTestCase(this.getClass().getName());
	}
	

}
