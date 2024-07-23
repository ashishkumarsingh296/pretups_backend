package com.testscripts.sit;

import org.testng.annotations.Test;

import com.Features.CacheUpdate;
import com.Features.NetworkPrefix;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.SIT_NETWORK_PREFIX)
public class SIT_NetworkPrefix extends BaseTest {
	CacheUpdate cacheUpdate = new CacheUpdate(driver);
	String assignCategory="SIT";
	
	@Test
	@TestManager(TestKey = "PRETUPS-865") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void a_addPrepaidPrefix() {
		final String methodName = "Test_Network_Prefix";
		Log.startTestCase(methodName);

		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String series = uniqueChecker.UC_PrefixData();
		if(series!=null) {
		String result[] = netwrokPrefix.addSeries(series, "Prepaid");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.successmessage");
		Assertion.assertEquals(expectedMsg, result[0]);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX3").getExtentCase());
		currentNode.assignCategory(assignCategory);
	
		String[] prefixes = result[1].split(",");
		int flag = 0;
		for (int i = 0; i < prefixes.length; i++) {
			if (series.equals(prefixes[i])) {
				flag = 1;
				break;
			} else
				flag = 0;
		}

		if (flag == 1)
			currentNode.log(Status.PASS,"Successfully Added Prefix to Prefix Service Mapping");
		else
			currentNode.log(Status.FAIL,"Couldn't Add Prefix to Prefix Service Mapping");
		
		Assertion.completeAssertions();
		}
		else
			currentNode.log(Status.SKIP,"All Network Prefixes are consumed.");
		
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-866") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void b_addPostpaidPrefix() {
		final String methodName = "Test_Network_Prefix";
		Log.startTestCase(methodName);
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String series = uniqueChecker.UC_PrefixData();
		if(series!=null) {
		String result[] = netwrokPrefix.addSeries(series, "Postpaid");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.successmessage");

		Assertion.assertEquals(expectedMsg, result[0]);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String[] prefixes = result[1].split(",");
		int flag = 0;
		for (int i = 0; i < prefixes.length; i++) {
			if (series.equals(prefixes[i])) {
				flag = 1;
				break;
			} else
				flag = 0;
		}

		if (flag == 1)
			currentNode.log(Status.PASS,"Successfully Added Prefix to Prefix Service Mapping");
		else
			currentNode.log(Status.FAIL,"Couldn't Add Prefix to Prefix Service Mapping");

		Assertion.completeAssertions();
		}
		else
			currentNode.log(Status.SKIP,"All Network Prefixes are consumed.");

		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-867") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void c_addOtherPrefix() {
		final String methodName = "Test_Network_Prefix";
		Log.startTestCase(methodName);
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String series = uniqueChecker.UC_PrefixData();
		if(series!=null) {
		String result[] = netwrokPrefix.addSeries(series, "Other");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String expectedMsg = MessagesDAO
				.prepareMessageByKey("network.networkprefix.successmessage");
		Assertion.assertEquals(expectedMsg, result[0]);
		Assertion.completeAssertions();
		}
		else
			currentNode.log(Status.SKIP,"All Network Prefixes are consumed.");

		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-869") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void d_duplicatePrepaidPrefix() {
		final String methodName = "Test_Network_Prefix";
		Log.startTestCase(methodName);
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String series = DBHandler.AccessHandler.getNetworkPrefix("PRE", "Y");
		if(series!=null) {
		netwrokPrefix.addSeries(series, "Prepaid");
		String[] result = netwrokPrefix.addSeries(series, "Prepaid");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.errors.duplicate", "Prepaid series",series);
		Assertion.assertEquals(expectedMsg, result[0]);
		Assertion.completeAssertions();
		}
		else
			currentNode.log(Status.SKIP,"No Network Prefix Exist");
		
		Log.endTestCase(methodName);
	}

	@Test
	@TestManager(TestKey = "PRETUPS-870") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void e_duplicatePostpaidPrefix() {
		final String methodName = "Test_Network_Prefix";
		Log.startTestCase(methodName);
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX11").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String series = DBHandler.AccessHandler.getNetworkPrefix("POST", "Y");
		if(series!=null) {
		netwrokPrefix.addSeries(series, "Postpaid");
		String[] result = netwrokPrefix.addSeries(series, "Postpaid");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX12").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.errors.duplicate", "Postpaid series",series);
		Assertion.assertEquals(expectedMsg, result[0]);
		Assertion.completeAssertions();
		}
		else
			currentNode.log(Status.SKIP,"No Network Prefix Exist");
		Log.endTestCase(methodName);
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-871") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void f_duplicateOtherPrefix() {
		final String methodName = "Test_Network_Prefix";
		Log.startTestCase(methodName);
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();

		currentNode = test
				.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX13").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String series = DBHandler.AccessHandler.getOtherNetworkPrefix("OTH", "Y");
		if(series!=null) {
		netwrokPrefix.addSeries(series, "Other");
		String[] result = netwrokPrefix.addSeries(series, "Other");
		currentNode = test
				.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX14").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.errors.duplicate", "Other series",series);
		Assertion.assertEquals(expectedMsg, result[0]);
		Assertion.completeAssertions();
		}
		else
			currentNode.log(Status.SKIP,"No Network Prefix Exist");
		
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-873") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void g_invalidLengthPostpaidPrefix() {
		final String methodName = "Test_Network_Prefix";
		Log.startTestCase(methodName);
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		RandomGeneration randomGeneration = new RandomGeneration();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX15").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String series = uniqueChecker.UC_PrefixData();
		if(series!=null) {
		String additionalData = randomGeneration.randomNumberWithoutZero(1);
		String seriesData = series.concat(additionalData);
		String[] result = netwrokPrefix.addSeries(seriesData, "Postpaid");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX16").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String expectedMsg = MessagesDAO.prepareMessageByKey(
				"network.networkprefix.errors.invalid", "Postpaid series",
				seriesData, String.valueOf(SystemPreferences.MSISDN_PREFIX_LENGTH));
		Assertion.assertEquals(expectedMsg, result[0]);
		Assertion.completeAssertions();
		}
		else
			currentNode.log(Status.SKIP,"All Network Prefixes are consumed.");
		
		Log.endTestCase(methodName);
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-874") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void h_invalidLengthPrepaidPrefix() {
		final String methodName = "Test_Network_Prefix";
		Log.startTestCase(methodName);
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		RandomGeneration randomGeneration = new RandomGeneration();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX17").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String series = uniqueChecker.UC_PrefixData();
		if(series!=null) {
		String additionalData = randomGeneration.randomNumberWithoutZero(1);
		String seriesData = series.concat(additionalData);
		String[] result = netwrokPrefix.addSeries(seriesData, "Prepaid");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX18").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.errors.invalid", "Prepaid series",
				seriesData, String.valueOf(SystemPreferences.MSISDN_PREFIX_LENGTH));
		Assertion.assertEquals(expectedMsg, result[0]);
		Assertion.completeAssertions();
	}
	else
		currentNode.log(Status.SKIP,"All Network Prefixes are consumed.");
		Log.endTestCase(methodName);
	}

	
	@Test
	@TestManager(TestKey = "PRETUPS-875") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void i_invalidLengthOtherPrefix() {
		final String methodName = "Test_Network_Prefix";
		Log.startTestCase(methodName);
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);
		UniqueChecker uniqueChecker = new UniqueChecker();
		RandomGeneration randomGeneration = new RandomGeneration();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX19").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String series = uniqueChecker.UC_PrefixData();
		if(series!=null) {
		String additionalData = randomGeneration.randomNumberWithoutZero(1);
		String seriesData = series.concat(additionalData);
		String[] result = netwrokPrefix.addSeries(seriesData, "Other");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX20").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String expectedMsg = MessagesDAO.prepareMessageByKey(
				"network.networkprefix.errors.invalid", "Other series",
				seriesData, String.valueOf(SystemPreferences.MSISDN_PREFIX_LENGTH));
		Assertion.assertEquals(expectedMsg, result[0]);
		Assertion.completeAssertions();
		}
		else
			currentNode.log(Status.SKIP,"All Network Prefixes are consumed.");
		
		Log.endTestCase(methodName);
	}
	
	
	@Test
	@TestManager(TestKey = "PRETUPS-876") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void j_invalidPrepaidPrefix() {
		final String methodName = "Test_Network_Prefix";
		Log.startTestCase(methodName);
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX21").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String series = " ,";
		String[] result = netwrokPrefix.addSeries(series, "Prepaid");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX22").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.errors.numeric", "Prepaid","series");
		Assertion.assertEquals(expectedMsg, result[0]);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-877") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void k_invalidPostpaidPrefix() {
		final String methodName = "Test_Network_Prefix";
		Log.startTestCase(methodName);
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX23").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String series = " ,";
		String[] result = netwrokPrefix.addSeries(series, "Postpaid");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX24").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.errors.numeric", "Postpaid","series");
		Assertion.assertEquals(expectedMsg, result[0]);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey = "PRETUPS-878") //TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void l_invalidOtherPrefix() {
		final String methodName = "Test_Network_Prefix";
		Log.startTestCase(methodName);
		NetworkPrefix netwrokPrefix = new NetworkPrefix(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX25").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String series = " ,";
		String[] result = netwrokPrefix.addSeries(series, "Other");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITNETWORKPREFIX26").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String expectedMsg = MessagesDAO.prepareMessageByKey("network.networkprefix.errors.numeric", "Other","series");

		Assertion.assertEquals(expectedMsg, result[0]);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
