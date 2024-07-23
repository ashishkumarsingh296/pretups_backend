package com.testscripts.uap;

import org.testng.annotations.Test;

import com.Features.NetworkPrefix;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
@ModuleManager(name =Module.UAP_NETWORK_PREFIX)
public class UAP_NetworkPrefix extends BaseTest {

	@Test
	@TestManager(TestKey = "PRETUPS-383") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_addPrepaidPrefix() {

		final String methodName = "Test_addPrepaidPrefix";
        Log.startTestCase(methodName);

		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker  ();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-383").getExtentCase());
		currentNode.assignCategory("UAP");
		String series = uniqueChecker.UC_PrefixData();
		if(series!=null) {
		String result[] = netwrokPrefix.addSeries(series, "Prepaid");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-383").getExtentCase());
		currentNode.assignCategory("UAP");
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.successmessage");
		Assertion.assertEquals(result[0], expectedMsg);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-383").getExtentCase());
		currentNode.assignCategory("UAP");
		String[] prefixes = result[1].split(",");
		int flag = 0;
		for (int i = 0; i < prefixes.length; i++) {
			if (series.equals(prefixes[i])){
				flag = 1;
				break;
			}
			else
				flag = 0;
		}

		if (flag == 1)
			currentNode.log(Status.PASS, "Successfully Added Prefix to Prefix Service Mapping");
		else {
			Assertion.assertFail("Couldn't Add Prefix to Prefix Service Mapping");
			currentNode.log(Status.FAIL, "Couldn't Add Prefix to Prefix Service Mapping");
		}
			
		}
		else {
			Assertion.assertSkip("All the network prefixes are already consumed.");
			currentNode.log(Status.SKIP, "All the network prefixes are already consumed.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-384") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_addPostpaidPrefix() {
		final String methodName = "Test_addPostpaidPrefix";
        Log.startTestCase(methodName);	
		Log.startTestCase(this.getClass().getName());
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-384").getExtentCase());
		currentNode.assignCategory("UAP");
		String series = uniqueChecker.UC_PrefixData();
		if(series!=null) {
		String result[] = netwrokPrefix.addSeries(series, "Postpaid");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-384").getExtentCase());
		currentNode.assignCategory("UAP");
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.successmessage");
		Assertion.assertEquals(result[0], expectedMsg);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-384").getExtentCase());
		currentNode.assignCategory("UAP");
		String[] prefixes = result[1].split(",");
		int flag = 0;
		for (int i = 0; i < prefixes.length; i++) {
			if (series.equals(prefixes[i])){
				flag = 1;
				break;
			}
			else
				flag = 0;
		}

		if (flag == 1)
			currentNode.log(Status.PASS, "Successfully Added Prefix to Prefix Service Mapping");
		else {
			Assertion.assertFail("Couldn't Add Prefix to Prefix Service Mapping");
			currentNode.log(Status.FAIL, "Couldn't Add Prefix to Prefix Service Mapping");
		}
		}
		else {
			Assertion.assertSkip("All the network prefixes are already consumed.");
			currentNode.log(Status.SKIP, "All the network prefixes are already consumed.");
		}
		Assertion.completeAssertions();
			Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-385") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void c_addOtherPrefix() {
		final String methodName = "Test_addOtherPrefix";
        Log.startTestCase(methodName);		
	
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-385").getExtentCase());
		currentNode.assignCategory("UAP");
		String series = uniqueChecker.UC_PrefixData();
		if(series!=null) {
		String result[] = netwrokPrefix.addSeries(series, "Other");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-385").getExtentCase());
		currentNode.assignCategory("UAP");
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.successmessage");
		Assertion.assertEquals(result[0], expectedMsg);
		}
		else {
			Assertion.assertSkip("All the network prefixes are already consumed.");
			currentNode.log(Status.SKIP, "All the network prefixes are already consumed.");
		}
		Assertion.completeAssertions();
			Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-386") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void d_duplicatePrefix() {
		final String methodName = "Test_duplicatePrefix";
        Log.startTestCase(methodName);	
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-386").getExtentCase());
		currentNode.assignCategory("UAP");
		String series = DBHandler.AccessHandler.getNetworkPrefix("PRE", "Y");
		if(series!=null)
		{
		netwrokPrefix.addSeries(series, "Prepaid");
		String[] result = netwrokPrefix.addSeries(series, "Prepaid");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PRETUPS-386").getExtentCase());
		currentNode.assignCategory("UAP");
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.errors.duplicate","Prepaid series",series);
		Assertion.assertEquals(result[0], expectedMsg);
		}
		else
		{
			Assertion.assertSkip("No network prefix exist");
			currentNode.log(Status.SKIP, "No network prefix exist");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
